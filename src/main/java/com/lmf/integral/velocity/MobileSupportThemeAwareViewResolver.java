/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.velocity;

import com.lmf.common.util.MobileSupportUtil;
import java.util.Locale;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

/**
 *
 * @author zhangxinling
 */
public class MobileSupportThemeAwareViewResolver extends VelocityViewResolver {
    
    @Override
    public  View resolveViewName(String viewName, Locale locale) throws Exception
    {
        if (viewName.startsWith("redirect")) {
            return super.resolveViewName(viewName, locale);
        }
        if (viewName.startsWith("admin/") || viewName.startsWith("/admin/"))
        {
            return super.resolveViewName(viewName, locale);
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        boolean isMobile = MobileSupportUtil.isMobile(attributes.getRequest());
        if(isMobile){
            String  prefixViewName  = getPrefixedViewName(viewName, "default", true);
            View view = super.resolveViewName(prefixViewName, locale);
            if (view != null) {
                return view;
            }
        }
        View view   = super.resolveViewName(getPrefixedViewName(viewName, "default", false), locale);
        if (view != null)
        {
            return view;
        }
        return super.resolveViewName(viewName, locale);
    }
    
    private String  getPrefixedViewName(String viewName, String prefix, boolean isMobile)
    {
        StringBuilder sbd = new StringBuilder();
        if(isMobile){
            if (viewName.startsWith("/")) {
                sbd.append("mobile").append(viewName);
            } else {
                sbd.append("mobile/").append(viewName);
            } 
        }else{
            if (viewName.startsWith("/")) {
                sbd.append(prefix).append(viewName);
            } else {
                sbd.append(prefix).append('/').append(viewName);
            }
        }
        return sbd.toString();
    }
}
