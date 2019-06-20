/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.argresolver;

import com.lmf.common.cart.AbstractShoppingCart;
import com.lmf.common.cart.PersistentShoppingCart;
import com.lmf.common.cart.ShoppingCartEntry;
import com.lmf.common.cart.service.AnonymousShoppingCartService;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.sys.service.PersistentShoppingCartService;
import com.lmf.website.entity.Website;
import java.util.List;
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
public class ShoppingCartArgumentResolver implements WebArgumentResolver {
    
    @Autowired
    private AnonymousShoppingCartService    anonymousShoppingCartService;
    
    @Autowired
    private PersistentShoppingCartService   persistentShoppingCartService;
    
    @Override
    public Object resolveArgument(MethodParameter mp, NativeWebRequest nwr) throws Exception {
        if (mp.getParameterType().equals(AbstractShoppingCart.class) && !mp.hasParameterAnnotation(Valid.class))
        {
            HttpServletRequest  request = nwr.getNativeRequest(HttpServletRequest.class);
            AbstractShoppingCart spcart = (AbstractShoppingCart) request.getAttribute("SP_CART");
            if (spcart == null)
            {
                HttpSession session = request.getSession(true);
                UserDetail  ud  = (UserDetail) session.getAttribute("currentUser");
                if (ud == null || !ud.isAuthorized())
                {
                    spcart  = anonymousShoppingCartService.getShoppingCart(session, true);
                } else {
                    PersistentShoppingCart  psc = new PersistentShoppingCart();
                    psc.setUserId(ud.getUserId());
                    List<ShoppingCartEntry> cartEntryList = persistentShoppingCartService.findEntries(ud.getUserId());
                    psc.setEntries(cartEntryList);
                    psc.setShoppingCartService(persistentShoppingCartService);
                    spcart  = psc;
                }
                request.setAttribute("SP_CART", spcart);
            }
            return spcart;
        }
        return UNRESOLVED;
    }
}
