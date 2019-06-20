/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.argresolver;

import com.lmf.integral.UserUtil;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.service.WebsiteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 *
 * @author zhangxinling
 */
public class WebsiteUserArgumentResolver implements WebArgumentResolver {
    
    @Autowired
    private WebsiteUserService websiteUserService;

    @Override
    public Object resolveArgument(MethodParameter mp, NativeWebRequest nwr) throws Exception {
        if (mp.getParameterType().equals(WebsiteUser.class) && !mp.hasParameterAnnotation(Valid.class))
        {
            HttpServletRequest request  = (HttpServletRequest) nwr.getNativeRequest();
            HttpSession session = nwr.getNativeRequest(HttpServletRequest.class).getSession(false);
            if (session != null)
            {
//                UserDetail  ud  = (UserDetail) session.getAttribute("currentUser");
                UserDetail ud = UserUtil.getCurrentUser(request,null);
                if (ud != null && ud.isAuthorized())
                {
                    return websiteUserService.findOne(ud.getUserId());
                }
            }
            return null;
        }
        return UNRESOLVED;
    }
}
