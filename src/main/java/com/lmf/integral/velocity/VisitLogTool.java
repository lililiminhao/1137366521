/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.velocity;

import com.lmf.common.visitlog.VisitLog;
import com.lmf.common.visitlog.VisitLogEntry;
import com.lmf.common.visitlog.service.VisitLogService;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.InvalidScope;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 访问历史
 * @author zhouwei
 */
@DefaultKey("logTool")
@InvalidScope({Scope.APPLICATION})
public class VisitLogTool{
    
    private ChainedContext  cctx;
    
    private VisitLog        visitLog;
    
    public  void init(Object ctx)
    {
        cctx    = (ChainedContext) ctx;
    }
    
    public  final VisitLog getVisitLog()
    {
        if (visitLog == null)
        {
            HttpServletRequest  request     = cctx.getRequest();
            visitLog    = (VisitLog) request.getAttribute("VISIT_LOG");
            if (visitLog == null)
            {
                ApplicationContext  appCtx = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
                VisitLogService visitLogService = appCtx.getBean(VisitLogService.class);
                HttpSession session = request.getSession(false);
                if (session != null)
                {
                    visitLog    = visitLogService.getVisitLog(session, false);
                    if (visitLog != null)
                    {
                        request.setAttribute("VISIT_LOG", visitLog);
                    }
                }
            }
        }
        return visitLog;
    }
    
    public List<VisitLogEntry> getVisotLogEntry()
    {
        VisitLog    vl  = getVisitLog();
        if (vl != null)
        {
            return vl.entries();
        }
        return Collections.emptyList();
    }
}
