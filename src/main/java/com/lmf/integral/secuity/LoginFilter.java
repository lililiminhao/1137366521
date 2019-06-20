/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.secuity;

import com.lmf.common.cart.service.AnonymousShoppingCartService;
import com.lmf.common.util.MobileSupportUtil;
import com.lmf.enterprise.entity.EnterpriseEmployee;
import com.lmf.enterprise.entity.EnterpriseUserMap;
import com.lmf.enterprise.service.EnterpriseEmployeeService;
import com.lmf.enterprise.service.EnterpriseUserMapService;
import com.lmf.integral.SystemConfig;
import com.lmf.integral.UserUtil;
import com.lmf.integral.service.WebsiteProxyService;
import com.lmf.market.entity.FenxiaoUser;
import com.lmf.market.service.FenXiaoUserService;
import com.lmf.sys.service.PersistentShoppingCartService;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.service.WebsiteUserService;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * 移动端部分登陆拦截器
 *
 * @author wuwenchao
 */
public class LoginFilter implements Filter {

    private final static Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    private FireWall    fireWall;
    
    private WebsiteProxyService websiteProxyService;
    
    @Autowired
    private WebsiteUserService websiteUserService;

    @Autowired
    private SystemConfig systemConfig;
    
    @Autowired
    private PersistentShoppingCartService shoppingCartService;

    @Autowired
    private AnonymousShoppingCartService anonymousShoppingCartService;
    
    @Autowired
    private EnterpriseEmployeeService enterpriseEmployeeService;
    
    @Autowired
    private EnterpriseUserMapService enterpriseUserMapService;
    
    
    @Autowired
    private FenXiaoUserService fenXiaoUserService;
    
    @Autowired
    private HttpClient httpClient;
            
    public void setFireWall(FireWall fireWall) {
        this.fireWall = fireWall;
    }
    
    public void setWebsiteProxyService(WebsiteProxyService proxy) {
        websiteProxyService    = proxy;
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        Website website = getWebsite();
        if (website == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "指定的网站不存在");
            if (logger.isWarnEnabled()) {
                logger.warn("visit website http://{}/{}?{} but website is not found", req.getServerName(), req.getServletPath(), req.getQueryString());
            }
            return;
        }
        req.setAttribute("website", website);

        String url = req.getServletPath();
        if (url.contains("/admin") || url.contains("/captcha.php") || url.contains("/smkpay/orderNotify.php") || url.contains("/smkpay/aycOrderNotify.php")
                || url.contains("/image/readImage.php") || url.contains("/shoppingCart/add.php")){
            chain.doFilter(request, response);
            return;
        }
        
        //tsmPay支付、退款通知
        if(url.contains("/tsmpay/refund/notify.php") || url.contains("/tsmPay/payNotify.php")) {
            chain.doFilter(request, response);
            return;
        }

        if (!MobileSupportUtil.isMobile(req)) {
        	if("/openid/redirectUrl.php".equals(req.getServletPath())){
        		chain.doFilter(request, response);
        	}else{
        		  resp.sendError(HttpServletResponse.SC_NOT_FOUND, "本站不提供电脑端页面，请使用移动端访问！");
                  if (logger.isWarnEnabled()) {
                      logger.warn("visit website http://{}/{}?{} but website site does not provide PC side", req.getServerName(), req.getServletPath(), req.getQueryString());
                  }
                  return;
        	}
          
        }
        
        UserDetail    user  = UserUtil.getCurrentUser(req,resp);
        if(user!=null) {
        	 WebsiteUser websiteUser = websiteUserService.findOne(user.getUserId());
        	 FenxiaoUser fenxiao = fenXiaoUserService.findByMobile(websiteUser.getMobile());
             if(fenxiao != null && fenxiao.getStatus()!=null && fenxiao.getStatus().intValue() != -1){
             	if(fenxiao.getStatus().intValue() == 0){
             		fenxiao.setStatus(1);
             		fenxiao.setUserId(websiteUser.getId().intValue());
             		fenXiaoUserService.editFenxiaoUser(fenxiao);
             	}
             }
             
             EnterpriseEmployee enterpriseEmployee = enterpriseEmployeeService.selectOneByMobile(websiteUser.getMobile());
             if(enterpriseEmployee != null){
            	 EnterpriseUserMap map= enterpriseUserMapService.getOneByUserId(websiteUser.getId());
            	 if(map==null) {
            		 EnterpriseUserMap enterpriseUserMap = new EnterpriseUserMap();
                     enterpriseUserMap.setUserId(websiteUser.getId());
                     enterpriseUserMap.setEnterpriseEmployeeId(enterpriseEmployee.getId());
                     enterpriseUserMapService.save(enterpriseUserMap); 
            	 }
             }
        }
        
        if (!fireWall.isAuthorized(url, user) && !url.contains("/mobile/login.php"))
        {
            String qs = req.getQueryString();
            if (qs != null && !qs.isEmpty()) {
                url = url + "?" + qs;
            }
            resp.sendRedirect("/jdvop/mobile/login.php?retUrl=" + URLEncoder.encode(url, "UTF-8"));
            return;
        }
        chain.doFilter(request, response);
        
    }

    @Override
    public void destroy() {

    }



    private Website getWebsite() {
        return websiteProxyService.getWebsite();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

//    public UserDetail getCurrentUser(HttpServletRequest req, HttpServletResponse resp, String url) throws UnsupportedEncodingException, IOException {
//        HttpSession session = req.getSession(true);
//        //wap_token(cookie)由市民卡主站控制，如果cookie能找到wap_token且currentUser(session)为null,则重新初始化session
//        if(!systemConfig.isDevelopEnvironment()) {
//            if(isExistsCookie("wap_token",req)){
//                Object o = session.getAttribute("currentUser");
//                if (o == null || !(o instanceof UserDetail)) {
//                    initUserSession(req);
//                }
//            }else{//wap_token(cookie)找不到则清除session
//                if(session != null){
//                    session.removeAttribute("currentUser");
//                    return null;
//                }
//            }
//        }
//        //初始化用户session有失败可能，防止空指针判断
//        if(session == null){
//            return null;
//        }
//        //获取用户
//        Object o = session.getAttribute("currentUser");
//        if(o == null) {
//            return null;
//        }
//        UserDetail user = (UserDetail) o;
//        WebsiteUser websiteUser = websiteUserService.findOne(user.getUserId());
//        if (websiteUser == null) {
//            return null;
//        }
//        return user;
//
//    }
//
//
//    /**
//     * 执行市民卡初始化用户session
//     * @param req
//     */
//    private WebsiteUser initUserSession(HttpServletRequest req) {
//        HttpSession session = req.getSession();
//        String token = getCookieValue("wap_token",req);
//        WebsiteUser websiteUser = null;
//        if(token != null) {
//            Map<String, Object> userInfoMap = doRequest(systemConfig.getApiHost() + "/users/token/" + token + "?system_code=smkjdmall");
//            if (Objects.equals(userInfoMap.get("code"), "0000")) {
//                Map<String, Object> data = (Map) userInfoMap.get("data");
//                String userId = data.get("user_id").toString();
//                websiteUser = websiteUserService.findOne(WebsiteLoginType.loginName, userId);
//                if (websiteUser == null) {//不存在，进行新增用户操作
//                    Map<String, Object> baseInfoMap = doRequest(systemConfig.getApiHost() + "/users/" + token + "/userinfo?system_code=smkjdmall");
//                    WebsiteUser user = new WebsiteUser();
//                    user.setLoginName(userId);
//                    user.setLoginPassword("0");
//                    if (Objects.equals(baseInfoMap.get("code"), "0000")) {
//                        Map<String, Object> baseInfo = (Map) baseInfoMap.get("data");
//                        user.setMobile(baseInfo.get("mobile") != null ? baseInfo.get("mobile").toString() : null);
//                        user.setNickName(baseInfo.get("nickname") != null ? baseInfo.get("nickname").toString() : "");
//                        user.setHeadPortrait(baseInfo.get("user_photo") != null ? baseInfo.get("user_photo").toString() : "");
//                    }
//                    user.setMethod(WebsiteLoginMethod.password);
//                    user.setType(WebsiteLoginType.loginName);
//                    user.setEnabled(true);
//                    user.setGender(Gender.unkown);
//                    websiteUser = websiteUserService.save(user, false);
//                }
//                session.setAttribute("currentUser", new UserDetail(websiteUser.getId().intValue(), websiteUser.getLoginName(), websiteUser.getNickName(), websiteUser.getIntegral()));
//
//                //将AnonymousShoppingCart存储为ShoppingCart
//                AnonymousShoppingCart spcart  = anonymousShoppingCartService.getShoppingCart(session, true);
//                try {
//                    List<ShoppingCartEntry> cartEntryList = spcart.entries();
//                    List<ShoppingCartEntry> oldCartEntryList = shoppingCartService.findEntries(websiteUser.getId().intValue());
//                    if (cartEntryList != null && !cartEntryList.isEmpty()) {
//                        for (ShoppingCartEntry newEntry : cartEntryList) {
//                            oldCartEntryList.add(newEntry);
//                        }
//                        shoppingCartService.save(websiteUser.getId().intValue(), oldCartEntryList);
//                        anonymousShoppingCartService.destroyShoppingCart(session);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return websiteUser;
//    }
//
//    /**
//     * 根据cookie名判断对应的cookie是否存在
//     * @param cookieName
//     * @param req
//     * @return
//     */
//    private boolean isExistsCookie(String cookieName,HttpServletRequest req) {
//        boolean result=false;
//        if(req != null) {
//            Cookie[] cookies = req.getCookies();
//            if(cookies != null && cookies.length > 0) {
//                for(Cookie c:cookies){
//                    if(cookieName.equals(c.getName())){
//                        result=true;
//                        break;
//                    }
//                }
//            }
//        }
//        return result;
//    }
//
//    /**
//     * 获取cookie值
//     * @param cookieName
//     * @param req
//     * @return
//     */
//    private String getCookieValue(String cookieName,HttpServletRequest req) {
//        String result = null;
//        if(req != null) {
//            Cookie[] cookies=req.getCookies();
//            if(cookies != null && cookies.length > 0) {
//                for(Cookie c:cookies){
//                    if(cookieName.equals(c.getName())){
//                        result = c.getValue();
//                        break;
//                    }
//                }
//            }
//        }
//        return result;
//    }
//
//    /**
//     * 发起请求
//     * @param url
//     * @return
//     */
//    private Map<String, Object> doRequest(String url) {
//        ObjectMapper mapper = new ObjectMapper();
//        JavaType mapType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
//        HttpGet get = new HttpGet(url);
//        try {
//            HttpResponse response = HttpClientUtils.notPorxyExecute(httpClient, get);
//            if (response != null) {
//                String content = EntityUtils.toString(response.getEntity());
//                return mapper.readValue(content, mapType);
//            }
//        } catch (IOException exp) {
//            throw Exceptions.unchecked(exp);
//        } finally {
//            get.abort();
//        }
//        return null;
//    }
    
}
