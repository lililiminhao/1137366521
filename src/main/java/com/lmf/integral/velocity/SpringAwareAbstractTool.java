/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.velocity;

import javax.servlet.ServletContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * see http://velocity.apache.org/tools/releases/2.0/creatingtools-printer.html#Standard_Properties_in_a_Web_Environment
 * 
 * @author zhangxinling
 */
public abstract class SpringAwareAbstractTool {
    
    private  ServletContext servletContext;
    
    private  WebApplicationContext  springCtx;
    
    public  void   init(Object ctx)
    {
        servletContext  = (ServletContext) ctx;
        springCtx       = WebApplicationContextUtils.getWebApplicationContext(servletContext);
    }
    
    public  ApplicationContext  getApplicationContext()
    {
        return springCtx;
    }
}
