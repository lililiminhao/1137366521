/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.argresolver;

import com.lmf.common.visitlog.VisitLog;
import com.lmf.common.visitlog.service.VisitLogService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

/**
 *
 * @author zhangxinling
 */
public class VisitLogArgumentResolver implements WebArgumentResolver {
    
    @Autowired
    private VisitLogService visitLogService;

    @Override
    public Object resolveArgument(MethodParameter mp, NativeWebRequest nwr) throws Exception {
        if (mp.getParameterType().equals(VisitLog.class) && !mp.hasParameterAnnotation(Valid.class))
        {
            HttpServletRequest  request = nwr.getNativeRequest(HttpServletRequest.class);
            VisitLog    visitLog    = (VisitLog) request.getAttribute("VISIT_LOG");
            if (visitLog == null)
            {
                HttpSession session = request.getSession(true);
                visitLog    = visitLogService.getVisitLog(session, true);
                request.setAttribute("VISIT_LOG", visitLog);
            }
            return visitLog;
        }
        return UNRESOLVED;
    }
}
