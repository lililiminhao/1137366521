/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.controller;

import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author shenzhixiong
 */
@Controller("adminLoginController")
public class LoginController {
    
    private final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    @RequestMapping(value = "/admin/login.php", method = RequestMethod.GET)
    public String login() {
        return "admin/login";
    }
    
    @RequestMapping(value = "/admin/login.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String fail(HttpServletRequest request, Model model)
    {
        String  message = null;
        Object  err     = request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
        if (err != null)
        {
            logger.warn(null, err);
            if ("org.apache.shiro.authc.UnknownAccountException".equals(err))
            {
                message = "未知的帐号名";
            } else if ("org.apache.shiro.authc.IncorrectCredentialsException".equals(err)) {
                message = "帐号名、密码错误";
            } else if ("org.apache.shiro.authc.AuthenticationException".equals(err)) {
                message = "认证失败";
            } else if ("com.lmf.integral.admin.shiro.security.IncorrectCaptchaException".equals(err)) {
                message = "验证码不正确";
            }
            model.addAttribute("errMsg", message);
        }
        model.addAttribute("userName", request.getAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM));
        return "admin/login";
    }
}
