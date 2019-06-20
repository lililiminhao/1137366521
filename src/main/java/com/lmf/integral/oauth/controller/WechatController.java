/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.oauth.controller;

import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.enums.Gender;
import com.lmf.common.util.MobileSupportUtil;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.openapi.wechat.entity.WechatUser;
import com.lmf.openapi.wechat.service.WechatUserService;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.service.WebsiteUserService;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author shenzhixiong
 */
@Controller
public class WechatController {
    
    @Autowired
    private WechatUserService wechatUserService;
    
    @Autowired
    private WebsiteUserService websiteUserService;
    
    @RequestMapping(value = "/wechat/callback.php", method = RequestMethod.GET)
    public  @ResponseBody void callback(@RequestParam("token") String token,
                                        @RequestParam(value = "openid", required = false) String openId,
                                        @RequestParam(value = "nickname", required = false) String nickName,
                                        @RequestParam(value = "sex", required = false) Gender sex,
                                        @RequestParam(value = "language", required = false) String language,
                                        @RequestParam(value = "province", required = false) String province,
                                        @RequestParam(value = "city", required = false) String city,
                                        @RequestParam(value = "country", required = false) String country,
                                        @RequestParam(value = "headimgurl", required = false) String headimgurl,
                                        HttpSession session,
                                        HttpServletResponse response) throws IOException {
        Object  tmp = session.getAttribute("RETURN_URI");
        String  url = "/mobile/index.php";
        if (tmp != null) {
            url = tmp.toString();
        }
        WechatUser wechatUser = wechatUserService.findOne(openId);
        if(wechatUser == null) {
            wechatUser = new WechatUser();
            wechatUser.setOpenid(openId);
            wechatUser.setIntegral(0);
        }
        wechatUser.setNickname(nickName);
        wechatUser.setSex(sex);
        wechatUser.setLanguage(language);
        wechatUser.setProvince(province);
        wechatUser.setCity(city);
        wechatUser.setCountry(country);
        wechatUser.setHeadimgurl(headimgurl);
        
        wechatUser = wechatUserService.save(wechatUser);
        session.setAttribute("wechat_user", wechatUser);
        //微信授权自动登录
        WebsiteUser user = websiteUserService.findOne(wechatUser);
        UserDetail  currentUser = null;
        if(user != null) {
            currentUser = new UserDetail(user.getId().intValue(), user.getLoginName(), user.getNickName(), user.getIntegral());
        }
        session.setAttribute("currentUser", currentUser);
        
        response.sendRedirect(url);
    }
    
    @RequestMapping(value = "/my/wechat/relieveBound.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse relieveBound(WebsiteUser websiteUser, HttpServletRequest request, HttpSession session) {
        
        if (MobileSupportUtil.isWechat(request)) {
            WechatUser  wechatUser  = (WechatUser) session.getAttribute("wechat_user");
            if(wechatUser == null) {
                return new SimpleJsonResponse(false, "当前登录信息已失效，请重新登录！");
            }
            if(!wechatUserService.isMapped(wechatUser, websiteUser)) {
                return new SimpleJsonResponse(false, "该用户暂未绑定当前微信，无需解绑操作！");
            }
            wechatUserService.unMap(wechatUser, websiteUser);
            return new SimpleJsonResponse(true, null);
        }
        return new SimpleJsonResponse(false, "请使用微信使用该解绑功能！");
    }
    
}
