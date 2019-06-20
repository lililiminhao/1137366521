/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.mobile.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.cart.AbstractShoppingCart;
import com.lmf.common.cart.ShoppingCartEntry;
import com.lmf.common.cart.service.AnonymousShoppingCartService;
import com.lmf.common.enums.Gender;
import com.lmf.common.util.Exceptions;
import com.lmf.enterprise.entity.EnterpriseEmployee;
import com.lmf.enterprise.entity.EnterpriseUserMap;
import com.lmf.enterprise.service.EnterpriseEmployeeService;
import com.lmf.enterprise.service.EnterpriseUserMapService;
import com.lmf.integral.SystemConfig;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.market.entity.FenxiaoUser;
import com.lmf.market.service.FenXiaoUserService;
import com.lmf.sys.service.PersistentShoppingCartService;
import com.lmf.system.sdk.HttpClientUtils;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.enums.WebsiteLoginMethod;
import com.lmf.website.enums.WebsiteLoginType;
import com.lmf.website.service.WebsiteUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author shenzhixiong
 */
@Controller("mobileLoginController")
public class LoginController {

    @Autowired
    private WebsiteUserService websiteUserService;

    @Autowired
    private PersistentShoppingCartService shoppingCartService;

    @Autowired
    private AnonymousShoppingCartService anonymousShoppingCartService;

    @Autowired
    private EnterpriseEmployeeService enterpriseEmployeeService;

    @Autowired
    private EnterpriseUserMapService enterpriseUserMapService;

    @Autowired
    private SystemConfig systemConfig;

    @Autowired
    private HttpClient httpClient;
    
    @Autowired
    private FenXiaoUserService fenXiaoUserService;

    private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    private static final JavaType mapType;

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
    }

    @RequestMapping(value = "/login.php", method = RequestMethod.GET)
    public String  login(@RequestParam(value = "login", defaultValue = "false") Boolean isLogin,
                         @RequestParam(value = "retUrl", required = false) String retUrl,
                         HttpServletRequest request, Model model)
    {
        String loginUrl;
        if(retUrl != null && !"".equals(retUrl)) {
    		loginUrl = systemConfig.getLoginUrl() + systemConfig.getHostName() + "/jdvop"+retUrl;
        } else {
            loginUrl = systemConfig.getLoginUrl() + systemConfig.getHostName() + "/jdvop/mobile/login.php";
        }
        
        return "redirect:" + loginUrl;
    }
    
    /**
     * 用户登录请求
     *
     * @param token 从cookie中获取的wap_token
     * @param retUrl    前往的访问链接
     * @param shoppingCart
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/mobile/login.php", produces = "text/html;charset=utf-8")
    public String login(@CookieValue(name = "wap_token", required = false) String token,
                        @RequestParam(value = "retUrl", required = false) String retUrl,
                        AbstractShoppingCart shoppingCart, HttpServletRequest request,
                        HttpServletResponse response, Model model) {
        boolean needLogin = false;
        WebsiteUser websiteUser = null;
        //开发环境
//        if(systemConfig.isDevelopEnvironment()){
//            //token = "46df47f7bd8c4c47a5e3a5c554147878"; 
//        	//13569420290
//        	//token = "72905723f7144c16a0a70849afd0c26e"; 
//        	//13569420520
//        	//token = "e41ea39c9e3b4b689500af1cec06dd30";
//        	//13569420522
//        	//token = "72905723f7144c16a0a70849afd0c26e";
//            //18700001102
//            token = "7d60fcc5e0f34df29965516c9ebe47b8";
//            
//        }
        if (StringUtils.isBlank(token)) {//未获取到cookie中的wap_token，需要跳转到登录页
            needLogin = true;
        } else {
            Map<String, Object> userInfoMap = doRequest(systemConfig.getApiHost() + "/users/token/" + token + "?system_code=smkjdmall");
            if (Objects.equals(userInfoMap.get("code"), "0000")) { 
                Map<String, Object> data = (Map) userInfoMap.get("data");
                String userId = data.get("user_id").toString();
                websiteUser = websiteUserService.findOne(WebsiteLoginType.loginName, userId);
                if (websiteUser == null) {//不存在，进行新增用户操作
                    Map<String, Object> baseInfoMap = doRequest(systemConfig.getApiHost() + "/users/" + token + "/userinfo?system_code=smkjdmall");
                    WebsiteUser user = new WebsiteUser();
                    user.setLoginName(userId);
                    user.setLoginPassword("0");
                    if (Objects.equals(baseInfoMap.get("code"), "0000")) {
                        Map<String, Object> baseInfo = (Map) baseInfoMap.get("data");
                        user.setMobile(baseInfo.get("mobile") != null ? baseInfo.get("mobile").toString() : null);
                        user.setNickName(baseInfo.get("nickname") != null ? baseInfo.get("nickname").toString() : "");
                        user.setHeadPortrait(baseInfo.get("user_photo") != null ? baseInfo.get("user_photo").toString() : "");
                    }
                    user.setMethod(WebsiteLoginMethod.password);
                    user.setType(WebsiteLoginType.loginName);
                    user.setEnabled(true);
                    user.setGender(Gender.unkown);
                    websiteUser = websiteUserService.save(user, false);
                }
                EnterpriseEmployee enterpriseEmployee = enterpriseEmployeeService.selectOneByMobile(websiteUser.getMobile());
                if(enterpriseEmployee != null){
                    EnterpriseUserMap enterpriseUserMap = new EnterpriseUserMap();
                    enterpriseUserMap.setUserId(websiteUser.getId());
                    enterpriseUserMap.setEnterpriseEmployeeId(enterpriseEmployee.getId());
                    enterpriseUserMapService.save(enterpriseUserMap);
                }
            } else {
                needLogin = true;
            }
        }
        
        if (needLogin) {
            StringBuilder loginUrl = new StringBuilder(systemConfig.getHostName()).append("/index.php");
            if(retUrl != null) {
                loginUrl = new StringBuilder().append(systemConfig.getLoginUrl()).append(systemConfig.getHostName()).append((!retUrl.contains("/jdvop") ?  "/jdvop" + retUrl : retUrl));
            }
            HttpSession session = request.getSession(true);

            UserDetail currentUser = new UserDetail(235502, "唐唐", "测试", 0);
            session.setAttribute("currentUser", currentUser);
            return "/mobile/user_center/record";
//            return "redirect:" + loginUrl;
        }
        HttpSession session = request.getSession(true);

        UserDetail currentUser = new UserDetail(websiteUser.getId().intValue(), websiteUser.getLoginName(), websiteUser.getNickName(), websiteUser.getIntegral());
        session.setAttribute("currentUser", currentUser);

        //将AnonymousShoppingCart存储为ShoppingCart
        if (shoppingCart != null) {
            List<ShoppingCartEntry> cartEntryList = shoppingCart.entries();
            List<ShoppingCartEntry> oldCartEntryList = shoppingCartService.findEntries(currentUser.getUserId());
            if (cartEntryList != null && !cartEntryList.isEmpty()) {
                for (ShoppingCartEntry newEntry : cartEntryList) {
                    oldCartEntryList.add(newEntry);
                }
                shoppingCartService.save(currentUser.getUserId(), oldCartEntryList);
                anonymousShoppingCartService.destroyShoppingCart(session);
            }
        }

        WebsiteUser user = websiteUserService.findOne(currentUser.getUserId());
        FenxiaoUser fenxiao = fenXiaoUserService.findByMobile(user.getMobile());
        if(fenxiao != null && fenxiao.getStatus()!=null && fenxiao.getStatus().intValue() != -1){
        	if(fenxiao.getStatus().intValue() == 0){
        		fenxiao.setStatus(1);
        		fenxiao.setUserId(user.getId().intValue());
        		fenXiaoUserService.editFenxiaoUser(fenxiao);
        	}
        }
        
        if(retUrl != null) {
            return "redirect:" + systemConfig.getHostName() + (!retUrl.contains("/jdvop") ?  "/jdvop" + retUrl : retUrl);
        } else {
            return "redirect:"+ systemConfig.getHostName() +"/jdvop/index.php";
        }
    }

    /**
     * ajax请求判断用户登陆状态
     *
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "/mobile/checkLoginStatus.php", method = RequestMethod.GET)
    public @ResponseBody SimpleJsonResponse checkLoginStatus(HttpSession httpSession) {
        if (httpSession.getAttribute("currentUser") != null) {
            return new SimpleJsonResponse(true, "用户已登陆！");
        } else {
            return new SimpleJsonResponse(false, "用户未登陆，请登陆！");
        }
    }

    private Map<String, Object> doRequest(String url) {
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse response = HttpClientUtils.notPorxyExecute(httpClient, get);
            if (response != null) {
                String content = EntityUtils.toString(response.getEntity());
                return mapper.readValue(content, mapType);
            }
        } catch (IOException exp) {
            throw Exceptions.unchecked(exp);
        } finally {
            get.abort();
        }
        return null;
    }
}
