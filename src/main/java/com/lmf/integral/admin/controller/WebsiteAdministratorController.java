/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.controller;

import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.PagerSpec;
import com.lmf.integral.admin.shiro.ACLDefine;
import com.lmf.website.entity.Role;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteAdministrator;
import com.lmf.website.entity.WorkerPrivilege;
import com.lmf.website.service.RoleService;
import com.lmf.website.service.WebsiteAdministratorService;
import com.lmf.website.service.WorkerPrivilegeService;
import com.lmf.website.vo.WebsiteAdministratorCriteria;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 *
 * @author shenzhixiong
 */
@Controller
public class WebsiteAdministratorController {
	 @Autowired
	private RoleService roleService;
	
    @Autowired
    private WorkerPrivilegeService workerPrivilegeService;
    
    @Autowired
    private WebsiteAdministratorService websiteAdministratorService;
   
    @RequiresPermissions("account:view")
    @RequestMapping(value="/admin/website/administrators.php", method = RequestMethod.GET)
    public String  list(@RequestParam(value = "kw", required = false) String keyword,
                        @RequestParam(value = "isProvider", required = false) Boolean isProvider,
                        @RequestParam(value = "enabled", defaultValue = "true") Boolean isEnabled,
                        @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager,
                        WebsiteAdministrator admin,
                        Model model,HttpSession session) throws UnsupportedEncodingException{
        
        StringBuilder link = new StringBuilder("/jdvop/admin/website/administrators.php?page=[:page]");
        if (keyword != null && !keyword.isEmpty()) {
            link.append("&kw=").append(URLEncoder.encode(keyword, "UTF-8"));
            model.addAttribute("kw", keyword);
        }
        
        if(isProvider != null) {
            link.append("&isProvider=").append(isProvider);
            model.addAttribute("isProvider", isProvider);
        }
        
        if(isEnabled != null) {
            link.append("&enabled=").append(isEnabled);
            model.addAttribute("enabled", isEnabled);
        }
        model.addAttribute("admin", admin);
        model.addAttribute("adminstrators", websiteAdministratorService.find(new WebsiteAdministratorCriteria().withKeyword(keyword).withProvider(isProvider).withEnabled(isEnabled), pager));
        model.addAttribute("link", link.toString());
        
        return "admin/website/administrator/list";
    }
    
    @RequiresPermissions("account:create")
    @RequestMapping(value="/admin/website/administrator/add.php", method = RequestMethod.GET)
    public String add(Model model) {
//        model.addAttribute("ACLDefine", ACLDefine.values());
       
        List<Role> roles = roleService.findByRoleType(2);
        model.addAttribute("roles", roles);
        return "admin/website/administrator/addAdmin";
    }
    
    @RequiresPermissions("account:create")
    @Transactional
    @RequestMapping(value = "/admin/website/administrator/add.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse<String> add(@RequestParam("loginName") String loginName,
                                                        @RequestParam("password") String loginPassword,
                                                        @RequestParam(value = "roleId",required = false) Integer roleId,
                                                        @RequestParam(value = "workerName", required = false) String workerName,
                                                        @RequestParam(value = "mobile", required = false) String mobile,
                                                        @RequestParam(value = "email", required = false) String email,
                                                        @RequestParam(value = "wechat", required = false) String wechat,
                                                        @RequestParam(value = "enabled", defaultValue = "true") boolean enabled,
                                                        Website website){
        
    	if(roleId == null){
    		 return new SimpleJsonResponse<>(false,"角色名称不能为空");
    	}
    	if(loginName == null){
            return new SimpleJsonResponse<>(false,"用户名不能为空");
        }
        if(websiteAdministratorService.exists(loginName)){
            return new SimpleJsonResponse<>(false,"用户名已经存在");
        }
        if(loginPassword.length() < 6 || loginPassword.length() > 32){
            return new SimpleJsonResponse<>(false,"请输入长度为6~32位的密码");
        }
        WebsiteAdministrator admin = new WebsiteAdministrator();
        admin.setLoginName(loginName);
        admin.setLoginPassword(loginPassword);
        admin.setWorkerName(workerName);
        admin.setMobile(mobile);
        admin.setEmail(email);
        admin.setAddress(wechat);
        admin.setEnabled(enabled);
        admin.setProvider(false);
        admin.setWebsite(website);
        Role byId = roleService.findById(roleId);
        if(byId!=null){
            admin.setRoleName(byId.getRoleName());
        }else {
            admin.setRoleName("暂无角色！");
        }
        admin = websiteAdministratorService.save(admin, false);
        //暂时分配管理员默认权限
//        String[] actions = {"system:edit", "account:view", "account:create", "account:edit", "account:delete", "pc_template:edit", 
//                "advertisement:edit", "mobile_template:edit", "custom_navigation:edit", "custom_cate:edit", "product:view", "product:create", "product:edit", 
//                "product:delete", "brand:view", "brand:create", "brand:edit", "brand:delete", "stock:view", "stock:replenishment", "stock:edit", "order:view",
//                "order:edit", "order:examine", "order:shipment", "order:delete", "niffer_order:view", "niffer_order:edit", "niffer_order:examine", 
//                "system_order:view", "statistics:view", "activity:view", "activity:create", "activity:edit", "activity:delete"};
        workerPrivilegeService.save(admin, null,roleId);
        return new SimpleJsonResponse<>(true, null);
    }
    
    @RequiresPermissions("account:edit")
    @RequestMapping(value="/admin/website/administrator/edit.php", method = RequestMethod.GET)
    public String edit(@RequestParam("adminId") int id,Model model)
    {
        WebsiteAdministrator admin = websiteAdministratorService.findOne(id);
        Map<String, List<String>> myActions = workerPrivilegeService.findWorkerPrivilegesAsMap(admin);
        WorkerPrivilege privilege = workerPrivilegeService.findByAdminId(id);
        
        if(privilege!= null){
        	 model.addAttribute("roleId", privilege.getRoleId());
        }
        List<Role> roles = roleService.findByRoleType(2);
        model.addAttribute("roles", roles);
        model.addAttribute("admin", admin);
        model.addAttribute("ACLDefine", ACLDefine.values());
        model.addAttribute("myActionMap", myActions);
        
//        if(admin.isProvider()){
//            return "admin/website/administrator/provider";
//        }
        return "admin/website/administrator/form";
    }
    
    @RequiresPermissions("account:edit")
    @RequestMapping(value = "/admin/website/administrator/edit.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse<String> edit(@RequestParam("id") Integer id,
											    		@RequestParam("roleId") Integer roleId,                                           
											    		@RequestParam(value = "password" , required = false) String loginPassword,
                                                        @RequestParam(value = "changePwd", defaultValue = "false") boolean changePwd,
                                                        @RequestParam(value = "workerName", required = false) String workerName,
                                                        @RequestParam(value = "mobile", required = false) String mobile,
                                                        @RequestParam(value = "email", required = false) String email,
                                                        @RequestParam(value = "wechat", required = false) String wechat,
                                                        @RequestParam(value = "enabled", defaultValue = "true") boolean enabled,
                                                        @RequestParam(value = "action[]", required = false) String[] actions)
    {
        WebsiteAdministrator admin = websiteAdministratorService.findOne(id);
        if(admin == null){
            return new SimpleJsonResponse(false, "账号不存在或已经被删除");
        }
        admin.setLoginName(admin.getLoginName());
        if(changePwd){
        	Subject subject = SecurityUtils.getSubject();
        	if(!subject.isPermitted("account:changPassword")){
        		return new SimpleJsonResponse(false, "您没有修改密码的权限！");
        	}
            if(loginPassword == null || loginPassword.trim().isEmpty()){
                return new SimpleJsonResponse(false, "请设置账号密码");
            }
            admin.setLoginPassword(loginPassword);
        }
        if(roleId == null){
   		 return new SimpleJsonResponse<>(false,"角色名称不能为空");
        }
        admin.setWorkerName(workerName);
        admin.setMobile(mobile);
        admin.setEmail(email);
        admin.setAddress(wechat);
        admin.setEnabled(enabled);
        Role byId = roleService.findById(roleId);
        if(byId!=null){
            admin.setRoleName(byId.getRoleName());
        }else {
            admin.setRoleName("暂无角色！");
        }
        WorkerPrivilege workerPrivilege = new WorkerPrivilege();
        workerPrivilege.setActions(actions);
        workerPrivilegeService.save(admin, actions,roleId);
        websiteAdministratorService.save(admin, changePwd);
        return new SimpleJsonResponse<>(true, "设置成功");
    }
    
    @RequiresPermissions("account:delete")
    @RequestMapping(value = "/admin/website/administrator/delete.php", method = RequestMethod.GET)
    public @ResponseBody SimpleJsonResponse delete(@RequestParam("adminId") Integer adminId){
    	websiteAdministratorService.delById(adminId);
    	WorkerPrivilege privilege = workerPrivilegeService.findByAdminId(adminId);
    	if(privilege != null){
    		workerPrivilegeService.delByRoleId(privilege.getRoleId());
    	}
        return new SimpleJsonResponse(true, "删除成功！");
    }

    @RequiresPermissions(value={"account:startUse","account:endUse"},logical=Logical.OR)
    @RequestMapping(value = "/admin/website/administrator/setEnabled.php", method = RequestMethod.GET)
    public @ResponseBody SimpleJsonResponse setEnabled(@RequestParam("ids[]") Integer [] ids,
                                                       @RequestParam("enabled") boolean enabled){
        
        if (ids.length < 1) {
            return new SimpleJsonResponse(false, "请选择您要操作的会员");
        }
        boolean skipError   = true;
        if (ids.length == 1) {
            skipError   = false;
        }
        for (int id : ids) {
            WebsiteAdministrator admin = websiteAdministratorService.findOne(id);
            if(admin == null){
                if(skipError){
                    continue;
                }else{
                    return new SimpleJsonResponse(false, "数据异常，请联系管理员");
                }
            }
            admin.setEnabled(enabled);
            websiteAdministratorService.setEnabled(id, enabled);
        }

        if(enabled){
            return new SimpleJsonResponse(true, "启用成功！");
        }else{
            return new SimpleJsonResponse(true, "禁用成功！");
        }
    }
    
    @RequiresPermissions("account:changPassword")
    @RequestMapping(value = "/admin/account/editPwd.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse editPwd(@RequestParam(value = "oldPwd") String oldPwd,
                                                    @RequestParam(value = "newPwd") String newPwd,
                                                    @RequestParam(value = "confimPwd") String confimPwd,
                                                    WebsiteAdministrator admin) {
        if(!admin.isPasswordValid(oldPwd))
        {
            return new SimpleJsonResponse(false, "您输入的原始密码不正确，请重新输入！");
        }
        if (newPwd.isEmpty() || newPwd.length() < 6 || newPwd.length() > 16)
        {
            return new SimpleJsonResponse(false, "您输入6~16位的新密码");
        }
        if (!newPwd.equals(confimPwd))
        {
            return new SimpleJsonResponse(false, "两次输入的密码不一致，请重新输入");
        }
        admin.setLoginPassword(confimPwd);
        websiteAdministratorService.save(admin, true);
        return new SimpleJsonResponse(true, null);
    }
    
}
