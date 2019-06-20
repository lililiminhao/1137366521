/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.secuity;

import java.util.Set;

/**
 *
 * @author Administrator
 */
public class FireWall {
    
    private Set<Rule>   fireWallRules;

    public void setFireWallRules(Set<Rule> fireWallRules) {
        this.fireWallRules = fireWallRules;
    }

    public boolean isAuthorized(String url, UserDetail user) {
        for (Rule rule : fireWallRules)
        {
            if (rule.getType().equals(RuleType.START_WITH))
            {
                if (!url.startsWith(rule.getValue()))
                {
                    continue;
                }
            } else if (rule.getType().equals(RuleType.END_WITH)) {
                if (!url.endsWith(rule.getValue()))
                {
                    continue;
                }
            } else if (rule.getType().equals(RuleType.CONTAINS)) {
                if (url.indexOf(rule.getValue()) == -1)
                {
                    continue;
                }
            }
            
            //这条规则适用此项目
            FilterType  filterType  = rule.getFilterType();
            if (filterType.equals(FilterType.NONE))
            {
                //不检查任何权限
                return true;
            }
            if (user == null)
            {
                return false;
            }
            if (!user.isAuthorized())
            {
                return false;
            }
            if (filterType.equals(FilterType.VALID_USER)) {
                
                return true;
                
            } else if (filterType.equals(FilterType.REQUIRE_ANY)) {
                //检查任意权限
                return containsAny(rule.getFilterSpecs(), user.getRoleSpecs());
            } else if (filterType.equals(FilterType.REQUIRE_ALL)) {
                Set<String> userRoles   = user.getRoleSpecs();
                if (userRoles == null || userRoles.isEmpty())
                {
                    return false;
                }
                return userRoles.containsAll(rule.getFilterSpecs());
            }
        }
        return true;//默认开放
    }
    
    static boolean  containsAny(Set<String> needle, Set<String> heystack)
    {
        for (String str : needle)
        {
            if (heystack.contains(str))
            {
                return true;
            }
        }
        return false;
    }
}
