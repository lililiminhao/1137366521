/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.shiro.security;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 *
 * @author Administrator
 */
public class CaptchaUsernamePasswordToken extends UsernamePasswordToken {
    
    private String  captcha;

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
    
    public CaptchaUsernamePasswordToken()
    {
        super();
    }
    
    public CaptchaUsernamePasswordToken(String username, 
                                        char[] password, 
                                        boolean rememberMe, 
                                        String  host,
                                        String captcha)
    {
        super(username, password, rememberMe, host);
        this.captcha    = captcha;
    }
}
