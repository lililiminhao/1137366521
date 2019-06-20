/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.velocity;

import com.lmf.integral.SystemConfig;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.InvalidScope;

/**
 *
 * @author zhouwei
 */
@DefaultKey("configTool")
@InvalidScope({Scope.REQUEST, Scope.SESSION})
public class ConfigTool extends SpringAwareAbstractTool{
    
    private SystemConfig systemConfig;
    
    @Override
    public void init(Object ctx)
    {
        super.init(ctx);
        
        systemConfig    = getApplicationContext().getBean(SystemConfig.class);
    }
    
    public String getImageHost()
    {
        return systemConfig.getImageHost();
    }
}
