/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.shiro.security;

import org.apache.shiro.authc.AuthenticationException;

/**
 *
 * @author Administrator
 */
public class IncorrectCaptchaException extends AuthenticationException {
    
    public IncorrectCaptchaException()
    {
        super();
    }
    
    public IncorrectCaptchaException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public IncorrectCaptchaException(String message) 
    {
        super(message);
    }
    
    public IncorrectCaptchaException(Throwable cause)
    {
        super(cause);
    }
}
