/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin;

import com.lmf.integral.admin.shiro.WebsiteAdministratorPrincipal;
import com.lmf.website.entity.WebsiteAdministrator;
import com.lmf.website.service.WebsiteAdministratorService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;

/**
 *
 * @author shenzhixiong
 */
public class WebsiteAdministratorArgumentResolver implements WebArgumentResolver {
    
    @Autowired
    private WebsiteAdministratorService websiteAdministratorService;

    @Override
    public Object resolveArgument(MethodParameter mp, NativeWebRequest nwr) throws Exception {
        if(mp.getParameterType().equals(WebsiteAdministrator.class) && !mp.hasParameterAnnotation(Valid.class)){
            Subject cu  = SecurityUtils.getSubject();
            Object  obj = cu.getPrincipal();
            if (obj != null && obj instanceof WebsiteAdministratorPrincipal)
            {
                return websiteAdministratorService.findOne(((WebsiteAdministratorPrincipal) obj).getId());
            }
            return null;
        }
        return UNRESOLVED;
    }
    
}
