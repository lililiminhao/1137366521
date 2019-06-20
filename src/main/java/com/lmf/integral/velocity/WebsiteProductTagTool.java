/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.velocity;

import com.lmf.website.entity.UserDefinedCate;
import com.lmf.website.entity.Website;
import com.lmf.website.service.UserDefinedCateService;
import java.util.List;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.InvalidScope;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author Mine
 */
@DefaultKey("websiteProductTagTool")
@InvalidScope({Scope.REQUEST, Scope.SESSION})
public class WebsiteProductTagTool{
    
    private UserDefinedCateService  userDefinedCateService;
    
    private Website website;
    
    private ChainedContext  cctx;
    
    public  void init(Object ctx)
    {
        cctx    = (ChainedContext) ctx;
        WebApplicationContext  wctx = WebApplicationContextUtils.getWebApplicationContext(cctx.getServletContext());
        userDefinedCateService    = wctx.getBean(UserDefinedCateService.class);
    }
    
    public final  Website getWebsite()
    {
        if (website == null)
        {
            website = (Website) cctx.getRequest().getAttribute("website");
        }
        return  website;
    }
    
    public List<UserDefinedCate> getRootTag()
    {
        return userDefinedCateService.rootCates(true);
    }
    
    public List<UserDefinedCate> getChilds(int parentId){
        return userDefinedCateService.findChilds(parentId, true);
    }
    
}
