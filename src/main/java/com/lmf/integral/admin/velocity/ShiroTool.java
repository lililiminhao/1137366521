/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.velocity;

import com.lmf.integral.admin.shiro.WebsiteAdministratorPrincipal;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.velocity.tools.config.DefaultKey;

/**
 *
 * @author Mine
 */
@DefaultKey("shiro")
public class ShiroTool {
    
    public  boolean isPermitted(String permission)
    {
        Subject subject = SecurityUtils.getSubject();
        return subject.isPermitted(permission);
    }
    
    public  boolean isPermitted(String ... permissions)
    {
        Subject subject = SecurityUtils.getSubject();
        return subject.isPermittedAll(permissions);
    }
    
    public  boolean isIncludePermitted(String ... permissions)
    {
        Subject subject = SecurityUtils.getSubject();
        for(String item : permissions){
            if(subject.isPermitted(item)){
                return true;
            }
        }
        return false;
    }
    
    public  WebsiteAdministratorPrincipal getPrincipal()
    {
        return (WebsiteAdministratorPrincipal) SecurityUtils.getSubject().getPrincipal();
    }
}
