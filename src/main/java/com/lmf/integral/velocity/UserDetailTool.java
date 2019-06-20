/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.velocity;

import com.lmf.integral.secuity.UserDetail;
import javax.servlet.http.HttpSession;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.InvalidScope;
import org.apache.velocity.tools.view.context.ChainedContext;

/**
 *
 * @author zhangxinling
 */
@DefaultKey("userTool")
@InvalidScope({Scope.APPLICATION})
public class UserDetailTool {
    
    private UserDetail  ud;
    
    public  void  init(Object ob)
    {
        ChainedContext  ctx    = (ChainedContext) ob;
        HttpSession session = ctx.getRequest().getSession(false);
        if (session != null)
        {
            ud  = (UserDetail) session.getAttribute("currentUser");
        }
    }
    
    public boolean  isAuthorized()
    {
        return ud != null && ud.isAuthorized();
    }
    
    public String   getLoginName()
    {
        if (ud != null)
        {
            return ud.getLoginName();
        }
        return null;
    }
    
    public  String  getNickName()
    {
        if (ud != null)
        {
            return ud.getNickName();
        }
        return null;
    }
    
    public  int     getUserId()
    {
        if (ud != null)
        {
            return ud.getUserId();
        }
        return 0;
    }
}
