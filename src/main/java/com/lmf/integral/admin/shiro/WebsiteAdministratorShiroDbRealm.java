/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.shiro;

import com.lmf.common.util.Encodes;
import com.lmf.common.util.StringsUtil;
import com.lmf.integral.admin.shiro.security.CaptchaUsernamePasswordToken;
import com.lmf.integral.admin.shiro.security.IncorrectCaptchaException;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteAdministrator;
import com.lmf.website.service.WebsiteAdministratorService;
import com.lmf.website.service.WebsiteService;
import com.lmf.website.service.WorkerPrivilegeService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 *
 * @author zhouw
 */
public class WebsiteAdministratorShiroDbRealm  extends AuthorizingRealm {
    
    @Autowired
    private WebsiteService websiteService;
    
    @Autowired
    private WorkerPrivilegeService workerPrivilegeService;
    
    @Autowired
    private WebsiteAdministratorService websiteAdministratorService;
            
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection pc) {
        WebsiteAdministratorPrincipal wp  = (WebsiteAdministratorPrincipal) pc.getPrimaryPrincipal();
        WebsiteAdministrator worker = websiteAdministratorService.findOne(wp.getId());
        SimpleAuthorizationInfo ai  = new SimpleAuthorizationInfo();
        Map<String, List<String>> actionMap = workerPrivilegeService.findWorkerPrivilegesAsMap(worker);
        if(actionMap != null && !actionMap.isEmpty()){
            for(Map.Entry<String, List<String>> item : actionMap.entrySet()){
                ai.addStringPermission(
                                new StringBuilder(item.getKey())
                                .append(":")
                                .append(StringsUtil.implode(",", item.getValue()))
                                .toString());
            }
        }
        
        return ai;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        CaptchaUsernamePasswordToken   loginToken  = (CaptchaUsernamePasswordToken) authcToken;
        //验证验证码
        String  captcha = null;
        Session  session = SecurityUtils.getSubject().getSession();
        if (session != null)
        {
            Object  sessionCaptcha  = session.getAttribute("CAPTCHA");
            if (sessionCaptcha != null && sessionCaptcha instanceof String)
            {
                captcha = (String) sessionCaptcha;
            }
        }
        if (captcha == null)
        {
            throw new IncorrectCaptchaException("验证码不存在");
        } else if (!captcha.equalsIgnoreCase(loginToken.getCaptcha())) {
            throw new IncorrectCaptchaException("验证码错误");
        }
        
        //验证用户名密码
        String loginName = loginToken.getUsername();
        if(loginName == null || loginName.trim().isEmpty()){
            throw new UnknownAccountException();
        }
        WebsiteAdministrator adminUser = websiteAdministratorService.findOne(loginName);
        if(adminUser == null){
            if("admins".equals(loginName)) {
                //admin超级管理员首次登陆时创建
                adminUser = new WebsiteAdministrator();
                adminUser.setLoginName(loginName);
                adminUser.setLoginPassword("888888");
                adminUser.setWorkerName("超级管理员");
                adminUser.setEnabled(true);
                Website website = websiteService.findOne(1087);
                adminUser.setWebsite(website);
                adminUser = websiteAdministratorService.save(adminUser, false);
                //admin超级管理员设置所有权限
                String[] actions = {"system:edit", "account:view", "account:create", "account:edit", "account:delete", "pc_template:edit", 
                "advertisement:edit", "mobile_template:edit", "custom_navigation:edit", "custom_cate:edit", "website_user:view", "website_user:create", 
                "website_user:edit", "website_user:delete", "integral_card:view", "integral_card:create", "integral_card:edit", 
                "integral_template_card:view", "integral_template_card:create", "integral_template_card:edit", "product:view,product:create", 
                "product:edit", "product:delete", "brand:view", "brand:create", "brand:edit", "brand:delete", "integral:view", "integral:edit", 
                "stock:view", "stock:replenishment", "stock:edit", "order:view", "order:edit", "order:examine", "order:shipment", "order:delete", "niffer_order:view", 
                "niffer_order:edit", "niffer_order:examine", "system_order:view", "statistics:view", "qr_code:view", "qr_code:create", "qr_code:edit", "qr_code:delete", 
                "activity:view", "activity:create", "activity:edit", "activity:delete", "data_analysis:view"};
                
                workerPrivilegeService.save(adminUser, actions,0);
            } else {
                throw new IncorrectCredentialsException();
            }
        }
        if(!adminUser.isEnabled()){
            throw new AuthenticationException();
        }
        this.clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
        return new SimpleAuthenticationInfo(
            new WebsiteAdministratorPrincipal(adminUser),
            adminUser.getLoginPassword(),
            ByteSource.Util.bytes(Encodes.decodeHex(adminUser.getSalt())),
            getName());
    }
}
