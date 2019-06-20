/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.secuity;

import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author shenzhixiong
 */
public class UserDetail implements Serializable {
    
    public static final UserDetail UNAUTHORIZED_USER    = new UserDetail(0, null, null,0);
    
    private int userId;
    
    private String  loginName;
    
    private String  nickName;
    
    private Set<String> roleSpecs;
    
    private int integral;
    
    public UserDetail() {
    }

    public UserDetail(int userId, String loginName, String nickName,int integral) {
        this.userId = userId;
        this.loginName = loginName;
        this.nickName = nickName;
        this.integral = integral;
    }
    
    public boolean isAuthorized()
    {
        if (this == UNAUTHORIZED_USER)  return false;
        if (userId == 0)    return false;
        return true;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Set<String> getRoleSpecs() {
        return roleSpecs;
    }

    public void setRoleSpecs(Set<String> roleSpecs) {
        this.roleSpecs = roleSpecs;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + this.userId;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserDetail other = (UserDetail) obj;
        if (this.userId != other.userId) {
            return false;
        }
        return true;
    }
}
