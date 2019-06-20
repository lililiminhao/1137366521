/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.argresolver;

import com.lmf.integral.UserUtil;
import com.lmf.integral.secuity.UserDetail;
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
public class UserDetailArgumentResolver implements WebArgumentResolver {

    @Override
    public Object resolveArgument(MethodParameter methodParamter, NativeWebRequest webRequest) throws Exception {
        
        if (methodParamter.getParameterType().equals(UserDetail.class) && !methodParamter.hasParameterAnnotation(Valid.class))
        {
            HttpServletRequest request  = (HttpServletRequest) webRequest.getNativeRequest();
            HttpSession session = request.getSession(false);
            if (session != null)
            {
//                UserDetail  ud  = (UserDetail) session.getAttribute("currentUser");
                UserDetail ud = UserUtil.getCurrentUser(request,null);
                if (ud != UserDetail.UNAUTHORIZED_USER)
                {
                    return ud;
                }
            }
            return null;
        }
        return UNRESOLVED;
    }
    
}
