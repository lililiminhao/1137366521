/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author shenzhixiong
 */
public class CookieUtil {
    
    public  static void addCookie(HttpServletResponse response, String name, String value, int secondsToLive)
    {
        Cookie  cookie   = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(secondsToLive);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
    
    public  static void removeCookie(HttpServletResponse response, String name)
    {
        addCookie(response, name, "", 0);
    }
    
}
