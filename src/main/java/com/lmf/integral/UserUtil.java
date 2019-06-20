package com.lmf.integral;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmf.common.cart.AnonymousShoppingCart;
import com.lmf.common.cart.ShoppingCartEntry;
import com.lmf.common.cart.service.AnonymousShoppingCartService;
import com.lmf.common.enums.Gender;
import com.lmf.common.util.Exceptions;
import com.lmf.integral.secuity.UserDetail;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @version: v1.0
 * project:
 * copyright: TECHNOLOGY CO., LTD. (c) 2015-2017
 * createTime: 2017/9/18 14:43
 * modifyTime:
 * modifyBy:
 */
@Component
public class UserUtil {

    @Autowired
    private WebsiteUserService websiteUserService;

    @Autowired
    private SystemConfig systemConfig;

    @Autowired
    private PersistentShoppingCartService shoppingCartService;

    @Autowired
    private AnonymousShoppingCartService anonymousShoppingCartService;

    @Autowired
    private HttpClient httpClient;

    public static UserUtil userUtil;

    @PostConstruct
    public void init() {
        userUtil = this;
    }

    public static UserDetail getCurrentUser(HttpServletRequest req,HttpServletResponse response) throws  IOException {
        HttpSession session = req.getSession(true);
        //wap_token(cookie)由市民卡主站控制，如果cookie能找到wap_token且currentUser(session)为null,则重新初始化session
        if(!userUtil.systemConfig.isDevelopEnvironment()) {
            if(isExistsCookie("wap_token",req)){
                Object o = session.getAttribute("currentUser");
                if (o == null || !(o instanceof UserDetail)) {
                    initUserSession(req);
                }
            }else{//wap_token(cookie)找不到则清除session
                if(session != null){
                    session.removeAttribute("currentUser");
                    return null;
                }
            }
        }
        if(userUtil.systemConfig.isDevelopEnvironment()) {
        	String token = req.getParameter("token");
        	if(StringUtils.isNotBlank(token)) {
        		Object o = session.getAttribute("currentUser");
                if(o == null) {
                	//写入cookie
                	Cookie cookie = new Cookie("wap_token", token);
                	cookie.setPath("/");
                	cookie.setMaxAge(24*7*3600);
                	if(response!=null) {
                		response.addCookie(cookie);
                	}
                	initUserSession(req,token);
                }
        	}
            if(isExistsCookie("wap_token",req)){
                Object o = session.getAttribute("currentUser");
                if (o == null || !(o instanceof UserDetail)) {
                    initUserSession(req);
                }
            }
        }
        //初始化用户session有失败可能，防止空指针判断
        if(session == null){
            return null;
        }
        //获取用户
        Object o = session.getAttribute("currentUser");
        if(o == null) {
            return null;
        }
        UserDetail user = (UserDetail) o;
        WebsiteUser websiteUser = userUtil.websiteUserService.findOne(user.getUserId());
        if (websiteUser == null) {
            return null;
        }
        return user;

    }


    /**
     * 执行市民卡初始化用户session
     * @param req
     */
    private static WebsiteUser initUserSession(HttpServletRequest req) {
        HttpSession session = req.getSession();
        String token = getCookieValue("wap_token",req);
        WebsiteUser websiteUser = null;
        if(token != null) {
            Map<String, Object> userInfoMap = doRequest(userUtil.systemConfig.getApiHost() + "/users/token/" + token + "?system_code=smkjdmall");
            if (Objects.equals(userInfoMap.get("code"), "0000")) {
                Map<String, Object> data = (Map) userInfoMap.get("data");
                String userId = data.get("user_id").toString();
                websiteUser = userUtil.websiteUserService.findOne(WebsiteLoginType.loginName, userId);
                if (websiteUser == null) {//不存在，进行新增用户操作
                    Map<String, Object> baseInfoMap = doRequest(userUtil.systemConfig.getApiHost() + "/users/" + token + "/userinfo?system_code=smkjdmall");
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
                    websiteUser = userUtil.websiteUserService.save(user, false);
                }
                session.setAttribute("currentUser", new UserDetail(websiteUser.getId().intValue(), websiteUser.getLoginName(), websiteUser.getNickName(), websiteUser.getIntegral()));

                //将AnonymousShoppingCart存储为ShoppingCart
                AnonymousShoppingCart spcart  = userUtil.anonymousShoppingCartService.getShoppingCart(session, true);
                try {
                    List<ShoppingCartEntry> cartEntryList = spcart.entries();
                    List<ShoppingCartEntry> oldCartEntryList = userUtil.shoppingCartService.findEntries(websiteUser.getId().intValue());
                    if (cartEntryList != null && !cartEntryList.isEmpty()) {
                        for (ShoppingCartEntry newEntry : cartEntryList) {
                            oldCartEntryList.add(newEntry);
                        }
                        userUtil.shoppingCartService.save(websiteUser.getId().intValue(), oldCartEntryList);
                        userUtil.anonymousShoppingCartService.destroyShoppingCart(session);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return websiteUser;
    }
    
    /**
     * 执行市民卡初始化用户session
     * @param req
     */
    private static WebsiteUser initUserSession(HttpServletRequest req,String token) {
        HttpSession session = req.getSession();
        WebsiteUser websiteUser = null;
        if(token != null) {
            Map<String, Object> userInfoMap = doRequest(userUtil.systemConfig.getApiHost() + "/users/token/" + token + "?system_code=smkjdmall");
            if (Objects.equals(userInfoMap.get("code"), "0000")) {
                Map<String, Object> data = (Map) userInfoMap.get("data");
                String userId = data.get("user_id").toString();
                websiteUser = userUtil.websiteUserService.findOne(WebsiteLoginType.loginName, userId);
                if (websiteUser == null) {//不存在，进行新增用户操作
                    Map<String, Object> baseInfoMap = doRequest(userUtil.systemConfig.getApiHost() + "/users/" + token + "/userinfo?system_code=smkjdmall");
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
                    websiteUser = userUtil.websiteUserService.save(user, false);
                }
                session.setAttribute("currentUser", new UserDetail(websiteUser.getId().intValue(), websiteUser.getLoginName(), websiteUser.getNickName(), websiteUser.getIntegral()));

                //将AnonymousShoppingCart存储为ShoppingCart
                AnonymousShoppingCart spcart  = userUtil.anonymousShoppingCartService.getShoppingCart(session, true);
                try {
                    List<ShoppingCartEntry> cartEntryList = spcart.entries();
                    List<ShoppingCartEntry> oldCartEntryList = userUtil.shoppingCartService.findEntries(websiteUser.getId().intValue());
                    if (cartEntryList != null && !cartEntryList.isEmpty()) {
                        for (ShoppingCartEntry newEntry : cartEntryList) {
                            oldCartEntryList.add(newEntry);
                        }
                        userUtil.shoppingCartService.save(websiteUser.getId().intValue(), oldCartEntryList);
                        userUtil.anonymousShoppingCartService.destroyShoppingCart(session);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return websiteUser;
    }

    /**
     * 根据cookie名判断对应的cookie是否存在
     * @param cookieName
     * @param req
     * @return
     */
    private static boolean  isExistsCookie(String cookieName,HttpServletRequest req) {
        boolean result=false;
        if(req != null) {
            Cookie[] cookies = req.getCookies();
            if(cookies != null && cookies.length > 0) {
                for(Cookie c:cookies){
                    if(cookieName.equals(c.getName())){
                        result=true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取cookie值
     * @param cookieName
     * @param req
     * @return
     */
    private static String getCookieValue(String cookieName, HttpServletRequest req) {
        String result = null;
        if(req != null) {
            Cookie[] cookies=req.getCookies();
            if(cookies != null && cookies.length > 0) {
                for(Cookie c:cookies){
                    if(cookieName.equals(c.getName())){
                        result = c.getValue();
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 发起请求
     * @param url
     * @return
     */
    private static Map<String, Object> doRequest(String url) {
        ObjectMapper mapper = new ObjectMapper();
        JavaType mapType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse response = HttpClientUtils.notPorxyExecute(userUtil.httpClient, get);
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
