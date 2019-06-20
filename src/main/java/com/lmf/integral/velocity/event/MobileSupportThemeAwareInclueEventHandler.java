/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.velocity.event;

import com.lmf.common.util.MobileSupportUtil;
import com.lmf.integral.service.WebsiteProxyService;
import org.apache.velocity.app.event.IncludeEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.util.ContextAware;
import org.apache.velocity.util.RuntimeServicesAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContext;

/**
 *
 * @author shenzhixiong
 */
public class MobileSupportThemeAwareInclueEventHandler implements IncludeEventHandler, RuntimeServicesAware, ContextAware {
    
    private RuntimeServices runtimeServices;
    
    private Context         context;
    
    @Override
    public String includeEvent(String includeResourcePath, String currentResourcePath, String directiveName) {
        String  includePath;
        RequestContext  ctx = (RequestContext) context.get("springMacroRequestContext");
        WebApplicationContext  wctx    = ctx.getWebApplicationContext();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        boolean isMobile = MobileSupportUtil.isMobile(attributes.getRequest());
        if(isMobile)
        {
            includePath = getPrefixedIncludeResourcePath(includeResourcePath, "default", true);
            if (includePath != null) {
                return includePath;
            }
        }
        includePath = getPrefixedIncludeResourcePath(includeResourcePath, "default", false);
        return includePath;
    }

    @Override
    public void setRuntimeServices(RuntimeServices rs) {
        this.runtimeServices = rs;
    }

    @Override
    public void setContext(Context cntxt) {
        this.context = cntxt;
    }

    private String getPrefixedIncludeResourcePath(String includeResourcePath, String prefix, boolean isMobile) {
        StringBuilder sb = new StringBuilder("/");
        if(isMobile){
            if (includeResourcePath.startsWith("/")) {
                sb.append("mobile").append(includeResourcePath);
            } else {
                sb.append("mobile/").append(includeResourcePath);
            }
        }else{
            if (includeResourcePath.startsWith("/")) {
                sb.append(prefix).append(includeResourcePath);
            } else {
                sb.append(prefix).append('/').append(includeResourcePath);
            }
        }
        
        String  path  = sb.toString();
        if (runtimeServices.getLoaderNameForResource(path) != null)
        {
            return path;
        }else{
            return null;
        }
    }
    
}
