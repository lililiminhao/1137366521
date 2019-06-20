/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.argresolver;

import com.lmf.website.entity.Website;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import static org.springframework.web.bind.support.WebArgumentResolver.UNRESOLVED;
import org.springframework.web.context.request.NativeWebRequest;

/**
 *
 * @author zhangxinling
 */
public class WebsiteArgumentResolver implements WebArgumentResolver {
    
    @Override
    public Object resolveArgument(MethodParameter mp, NativeWebRequest nwr) throws Exception {
        if (mp.getParameterType().equals(Website.class) && !mp.hasParameterAnnotation(Valid.class))
        {
            return nwr.getNativeRequest(HttpServletRequest.class).getAttribute("website");
        }
        return UNRESOLVED;
    }
}
