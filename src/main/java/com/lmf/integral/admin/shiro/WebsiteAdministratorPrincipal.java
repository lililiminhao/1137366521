/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.shiro;

import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteAdministrator;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Mine
 */
public class WebsiteAdministratorPrincipal  implements Serializable{
    
    private Integer id;
    
    private String  loginName;
    
    private String  workerName;
    
    private String  mobile;
    
    private String  email;
    
    private String  address;
    
    private boolean enabled;
    
    private boolean provider;
    
    private Website website;
    
    public WebsiteAdministratorPrincipal() {}
    
    public WebsiteAdministratorPrincipal(WebsiteAdministrator work) {
        this.id             = work.getId();
        this.loginName      = work.getLoginName();
        this.workerName     = work.getWorkerName();
        this.mobile         = work.getMobile();
        this.email          = work.getEmail();
        this.address        = work.getAddress();
        this.enabled        = work.isEnabled();
        this.provider       = work.isProvider();
        this.website        = work.getWebsite();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isProvider() {
        return provider;
    }

    public void setProvider(boolean provider) {
        this.provider = provider;
    }

    public Website getWebsite() {
        return website;
    }

    public void setWebsite(Website website) {
        this.website = website;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
        hash = 89 * hash + Objects.hashCode(this.loginName);
        hash = 89 * hash + Objects.hashCode(this.workerName);
        hash = 89 * hash + Objects.hashCode(this.mobile);
        hash = 89 * hash + Objects.hashCode(this.email);
        hash = 89 * hash + Objects.hashCode(this.address);
        hash = 89 * hash + (this.enabled ? 1 : 0);
        hash = 89 * hash + (this.provider ? 1 : 0);
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
        final WebsiteAdministratorPrincipal other = (WebsiteAdministratorPrincipal) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.loginName, other.loginName)) {
            return false;
        }
        if (!Objects.equals(this.workerName, other.workerName)) {
            return false;
        }
        if (!Objects.equals(this.mobile, other.mobile)) {
            return false;
        }
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        if (!Objects.equals(this.address, other.address)) {
            return false;
        }
        if (this.enabled != other.enabled) {
            return false;
        }
        if (this.provider != other.provider) {
            return false;
        }
        return true;
    }

    
    
}
