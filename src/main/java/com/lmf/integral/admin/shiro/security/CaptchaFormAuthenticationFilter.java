/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.shiro.security;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

/**
 *
 * @author 张心灵
 */
public class CaptchaFormAuthenticationFilter extends FormAuthenticationFilter {
    
    public static final String  DEFAULT_CAPTCHA_PARAM   = "captcha";
    
    protected String  getCaptcha(ServletRequest request)
    {
        return WebUtils.getCleanParam(request, DEFAULT_CAPTCHA_PARAM);
    }
    
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response)
    {
        String  username    = getUsername(request);
        String  password    = getPassword(request);
        String  captcha     = getCaptcha(request);
        boolean rememberMe  = isRememberMe(request);
        String  host        = getHost(request);
        
        return new CaptchaUsernamePasswordToken(username, password == null? null : password.toCharArray(), rememberMe, host, captcha);
    }
    
}
