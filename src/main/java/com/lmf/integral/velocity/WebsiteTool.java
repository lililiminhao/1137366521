/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.velocity;

import com.lmf.common.Page;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.service.OrderService;
import com.lmf.website.entity.NavigationItem;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteCustomBlock;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.service.WebsiteCustomBlockService;
import com.lmf.website.service.WebsiteCustomEnterpriseBlockService;
import com.lmf.website.service.WebsiteService;
import java.util.List;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.InvalidScope;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author zhangxinling
 */
@DefaultKey("websiteTool")
@InvalidScope({Scope.APPLICATION})
public class WebsiteTool {
    
    private ChainedContext  cctx;
    
    private Website website;
    
    private OrderService orderService;
    
    private WebsiteCustomBlockService websiteCustomBlockService;
    
    private WebsiteCustomEnterpriseBlockService websiteCustomEnterpriseBlockService;
    
    private WebsiteService websiteService;
    
    public  void init(Object ctx)
    {
        cctx    = (ChainedContext) ctx;
        WebApplicationContext   wctx = WebApplicationContextUtils.getWebApplicationContext(cctx.getServletContext());
        orderService                 = wctx.getBean(OrderService.class);
        websiteCustomBlockService    = wctx.getBean(WebsiteCustomBlockService.class);
        websiteCustomEnterpriseBlockService    = wctx.getBean(WebsiteCustomEnterpriseBlockService.class);
        websiteService = wctx.getBean(WebsiteService.class);
    }
    
    public final  Website getWebsite()
    {
        if (website == null)
        {
            website = (Website) cctx.getRequest().getAttribute("website");
        }
        return  website;
    }
    
    public final NavigationItem[] getNavigationItems()
    {
        NavigationItem[]   result  = websiteService.getWebsiteNavigations();
        if (result == null) {
            result  = new NavigationItem[0];
        }
        return result;
    }
    
    public final List<WebsiteCustomBlock> getEnterpriseBlocks() {
    	List<WebsiteCustomBlock> blocks = websiteCustomEnterpriseBlockService.find();
        return blocks;
    }
    
    public  final List<WebsiteCustomBlock> getBlocks()
    {
        List<WebsiteCustomBlock> blocks = websiteCustomBlockService.find();
        return blocks;
    }
    
    
    public  final boolean hasBriefDescription()
    {
        if (website.getBriefDescription() == null || website.getBriefDescription().isEmpty())
        {
            return false;
        }
        return true;
    }
    
    public final String getBriefDescription()
    {
        return website.getBriefDescription();
    }
    
    
    public final Page<ShoppingOrder> findOrders(){
        return orderService.find((WebsiteUser)null, null, null);
    }
}
