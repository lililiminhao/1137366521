/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.velocity;

import com.lmf.common.cart.AbstractShoppingCart;
import com.lmf.common.cart.PersistentShoppingCart;
import com.lmf.common.cart.ShoppingCartEntry;
import com.lmf.common.cart.service.AnonymousShoppingCartService;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.sys.service.PersistentShoppingCartService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.InvalidScope;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author zhangxinling
 */
@DefaultKey("spcart")
@InvalidScope({Scope.APPLICATION})
public class ShoppingCartTool {
    
    private AbstractShoppingCart shoppingCart;
    
    private HttpServletRequest  request;
    
    public  void   init(Object ctx)
    {
        ChainedContext  cctx    = (ChainedContext) ctx;
        WebApplicationContext  wctx = WebApplicationContextUtils.getWebApplicationContext(cctx.getServletContext());
        request = cctx.getRequest();
    }
    
    public  AbstractShoppingCart  getShoppingCart()
    {
        if (shoppingCart == null)
        {
            shoppingCart    = (AbstractShoppingCart) request.getAttribute("SP_CART");
            if (shoppingCart == null)
            {
                ApplicationContext  appCtx = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
                HttpSession session = request.getSession(true);
                UserDetail  ud      = (UserDetail) session.getAttribute("currentUser");
                if (ud == null || !ud.isAuthorized())
                {
                    shoppingCart    = appCtx.getBean(AnonymousShoppingCartService.class).getShoppingCart(session, true);
                } else {
                    PersistentShoppingCart  psc = new PersistentShoppingCart();
                    psc.setUserId(ud.getUserId());
                    PersistentShoppingCartService   pscs    = appCtx.getBean(PersistentShoppingCartService.class);
                    List<ShoppingCartEntry> cartEntryList = pscs.findEntries(ud.getUserId());
                    psc.setEntries(cartEntryList);
                    psc.setShoppingCartService(pscs);
                    shoppingCart    = psc;
                }
                request.setAttribute("SP_CART", shoppingCart);
            }
        }
        return shoppingCart;
    }
    
    public  boolean isPersistent()
    {
        return getShoppingCart() instanceof PersistentShoppingCart;
    }
    
    public  int totalAmount()
    {
        return getShoppingCart().totalAmount();
    }
    
    public  boolean isEmpty()
    {
        return getShoppingCart().isEmpty();
    }
}
