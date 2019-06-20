/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.misc.controller;

import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.ValidUtil;
import com.lmf.sys.entity.ShortMessageSendToken;
import com.lmf.sys.enums.ShortMessageSendType;
import com.lmf.sys.service.ShortMessageSendTokenService;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.service.WebsiteUserService;
import javax.servlet.http.HttpServletRequest;
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
public class MessageCaptchaController {
    
    @Autowired
    private WebsiteUserService websiteUserService;
    
    @Autowired
    private ShortMessageSendTokenService shortMessageSendTokenService;
    
    @RequestMapping(value = "/ajaxGetCaptcha.php", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public @ResponseBody SimpleJsonResponse getCode(@RequestParam("mobile") String mobile, HttpServletRequest request)
    {
        if (mobile == null) {
            return new SimpleJsonResponse(false, "请输入手机号码");
        }
        if(!ValidUtil.isMobile(mobile)){
            return new SimpleJsonResponse(false, "请输入正确的手机号码");
        }
        if(websiteUserService.existsLoginName(mobile)) {
            return new SimpleJsonResponse(false, "手机号已被注册，请重新输入！");
        } 
        
        WebsiteUser websiteUser = new WebsiteUser();
        websiteUser.setId(-1L); //注册设置为-1
        
        ShortMessageSendToken token = shortMessageSendTokenService.send(websiteUser, mobile, request.getRemoteAddr(), ShortMessageSendType.register);
        if (token == null) {
            return new SimpleJsonResponse(false, "未获取到短信验证数据，请重试！");
        }
        if (token.isDisabled()) {
            return new SimpleJsonResponse(false, "您获取验证码过于频繁，如有需要请明天再尝试！");
        }
        return new SimpleJsonResponse(true, "短信验证码已发送到您的手机，请注意查收！");
    }
    
}
