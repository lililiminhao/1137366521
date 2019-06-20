/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.controller;

import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.PagerSpec;
import com.lmf.product.service.ProductService;
import com.lmf.sys.service.ShipmentFeeSettingsExtService;
import com.lmf.website.entity.Role;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteAdministrator;
import com.lmf.website.entity.WorkerPrivilege;
import com.lmf.website.service.RoleService;
import com.lmf.website.service.WebsiteAdministratorService;
import com.lmf.website.service.WorkerPrivilegeService;
import com.lmf.website.vo.WebsiteAdministratorCriteria;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 *  供应商管理
 * @author wangdong
 */
@Controller
public class WebsiteProviderController {

    @Autowired
    private RoleService roleService;
    

    @Autowired
    private WorkerPrivilegeService workerPrivilegeService;
    
    @Autowired
    private ShipmentFeeSettingsExtService shipmentFeeSettingsService;
    
    @Autowired
    private ProductService productService;

    @Autowired
    private WebsiteAdministratorService websiteAdministratorService;
    
    //供应商列表
    @RequiresPermissions("provide:view")
    @RequestMapping(value="/admin/website/provider.php", method = RequestMethod.GET)
    public String list(@RequestParam(value = "kw", required = false) String keyword,
                       @RequestParam(value = "enabled", defaultValue = "true") Boolean isEnabled,
                    @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager,
                    WebsiteAdministrator admin, Model model) throws UnsupportedEncodingException
    {
        StringBuilder link = new StringBuilder("/jdvop/admin/website/provider.php?page=[:page]");
        if (keyword != null && !keyword.isEmpty()) {
            link.append("&kw=").append(URLEncoder.encode(keyword, "UTF-8"));
            model.addAttribute("kw", keyword);
        }
        if(isEnabled != null) {
            link.append("&enabled=").append(isEnabled);
            model.addAttribute("enabled", isEnabled);
        }
        model.addAttribute("admin", admin);
        model.addAttribute("adminstrators", websiteAdministratorService.find(new WebsiteAdministratorCriteria().withKeyword(keyword).withProvider(true).withEnabled(isEnabled), pager));
        model.addAttribute("shipmentFeeSettingsExtService", shipmentFeeSettingsService);
        model.addAttribute("link", link.toString());
        
        return  "admin/provider/list";
    }
    
    //新增供应商页面
    @RequiresPermissions("provide:create")
    @RequestMapping(value="/admin/website/provider/add.php", method = RequestMethod.GET)
    public String add(Model model) {
        List<Role> roles = roleService.findByRoleType(3);
        model.addAttribute("roles", roles);
        model.addAttribute("shipmentFeeSettings", shipmentFeeSettingsService.find());
        return "admin/provider/addProvider";
    }
    
    //新增供应商页面Post
    @RequiresPermissions("provide:create")
    @RequestMapping(value="/admin/website/provider/add.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse<String> add(@RequestParam("loginName") String loginName,
                                                        @RequestParam("password") String loginPassword,
                                                        @RequestParam(value = "roleId",required = false) Integer roleId,
                                                        @RequestParam(value = "address")String address,
                                                        @RequestParam(value = "workerName", required = false) String workerName,
                                                        @RequestParam(value = "mobile", required = false) String mobile,
                                                        @RequestParam(value = "email", required = false) String email,
                                                        @RequestParam(value = "wechat", required = false) String wechat,
                                                        @RequestParam(value = "staffName", required = false) String staffName,
                                                        @RequestParam("settingId") Integer settingId, //模板ID
                                                        @RequestParam(value = "enabled", defaultValue = "true") boolean enabled,
                                                        Website website)
    {
        if(roleId == null){
            return new SimpleJsonResponse(false, "供应商角色名称不能为空");
        }
        if(loginPassword.length() < 6 || loginPassword.length() > 32){
            return new SimpleJsonResponse<>(false,"请输入长度为6~32位的密码");
        }
        if(loginName == null){
            return new SimpleJsonResponse<>(false,"用户名不能为空");
        }
        //判断供应商的名称是否重复 因为WebsiteAdministrator 里面loginName存储的是workName的值
        if(websiteAdministratorService.exists(loginName)){
            return new SimpleJsonResponse(false,"该供应商名称已经存在");
        }
        WebsiteAdministrator admin = new WebsiteAdministrator();
        admin.setLoginName(loginName); //市民卡暂时供应商不提供登录 所以登录名字暂时为供应商名称
        admin.setLoginPassword(loginPassword);
        admin.setWorkerName(workerName); //供应商名称
        admin.setEnabled(enabled);
        admin.setProvider(true);
        admin.setAddress(address);
        admin.setMobile(mobile);
        admin.setWebsite(website);
        admin.setEmail(email);
        admin.setSettingId(settingId);
        admin.setWechat(wechat);
        admin.setStaffName(staffName);
        Role byId = roleService.findById(roleId);
        if(byId!=null){
            admin.setRoleName(byId.getRoleName());
        }else {
            admin.setRoleName("暂无角色！");
        }
        admin = websiteAdministratorService.save(admin, true);
        //暂时分派默认权限
//        String[] actions = {"product:view", "product:create", "product:edit", "product:delete", "order:view", "order:shipment", "stock:view", "stock:replenishment"};
        workerPrivilegeService.saveProvider(admin, null,roleId);
        return new SimpleJsonResponse<>(true, null);
    }
    
    //去编辑供应商的页面
    @RequiresPermissions("provide:edit")
    @RequestMapping(value = "/admin/website/provider/edit.php" , method = RequestMethod.GET)
    public String edit(@RequestParam("adminId") int id,Model model)
    {
        WebsiteAdministrator admin = websiteAdministratorService.findOne(id);
        List<Role> roles = roleService.findByRoleType(3);
        WorkerPrivilege privilege = workerPrivilegeService.findByAdminId(id);

        if(privilege!= null){
            model.addAttribute("roleId", privilege.getRoleId());
        }
        model.addAttribute("roles", roles);
        model.addAttribute("shipmentFeeSettings", shipmentFeeSettingsService.find());
        model.addAttribute("admin", admin);
        return "admin/provider/form";
    }
    
    //去编辑供应商的页面post
    @RequiresPermissions("provide:edit")
    @RequestMapping(value = "/admin/website/provider/edit.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse<String> edit(@RequestParam("id") Integer id,
                                                         @RequestParam(value = "password",required = false) String loginPassword,
                                                         @RequestParam(value = "changePwd", defaultValue = "false") boolean changePwd,
                                                         @RequestParam(value = "roleId",required = false) Integer roleId,
                                                         @RequestParam(value = "address")String address,
                                                         @RequestParam(value = "workerName", required = false) String workerName,
                                                         @RequestParam(value = "mobile", required = false) String mobile,
                                                         @RequestParam(value = "email", required = false) String email,
                                                         @RequestParam(value = "wechat", required = false) String wechat,
                                                         @RequestParam(value = "staffName", required = false) String staffName,
                                                         @RequestParam("settingId") Integer settingId, //模板ID
                                                         @RequestParam(value = "enabled", defaultValue = "true") boolean enabled,
                                                         Website website
                                                )
    {
        //根据供应商ID 查找数据
        WebsiteAdministrator admin = websiteAdministratorService.findOne(id);
        if(admin == null){
            return new SimpleJsonResponse(false, "操作的供应商不存在");
        }
        admin.setLoginName(admin.getLoginName());
        if(changePwd){
        	Subject subject = SecurityUtils.getSubject();
        	if(!subject.isPermitted("provide:changPassword")){
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
        admin.setWorkerName(workerName); //供应商名称
        admin.setEmail(email);
        admin.setAddress(address);
        admin.setMobile(mobile);
        admin.setWebsite(website);
        admin.setSettingId(settingId);
        admin.setEnabled(enabled);
        admin.setWechat(wechat);
        admin.setStaffName(staffName);
        Role byId = roleService.findById(roleId);
        if(byId!=null){
            admin.setRoleName(byId.getRoleName());
        }else {
            admin.setRoleName("暂无角色！");
        }
        workerPrivilegeService.saveProvider(admin,null,roleId);
        websiteAdministratorService.save(admin, changePwd);
        return new SimpleJsonResponse<>(true, null);
        
    }
    
    //禁用供应商名称
    @RequiresPermissions("provide:endUse")
    @RequestMapping(value = "/admin/website/provider/setEnabled.php", method = RequestMethod.GET)
    public @ResponseBody SimpleJsonResponse setEnabled(@RequestParam("id") Integer id,
                                                       @RequestParam("enabled") boolean enabled)
    {
        //根据ID 查找供应商数据
        WebsiteAdministrator admin = websiteAdministratorService.findOne(id);
        if(admin == null){
            return new SimpleJsonResponse(false, "操作的供应商不存在");
        }
        
        admin.setEnabled(enabled);  //禁用or启用

        websiteAdministratorService.setEnabled(id, enabled);   //禁用
        if(enabled){
            return new SimpleJsonResponse(true, "启用成功！");
        }else{
            return new SimpleJsonResponse(true, "禁用成功！");
        }
    }

    @RequiresPermissions("provide:delete")
    @RequestMapping(value = "/admin/website/provider/delete.php", method = RequestMethod.GET)
    public @ResponseBody SimpleJsonResponse delete(@RequestParam("adminId") Integer adminId){

        websiteAdministratorService.setEnabled(adminId,false);
        WorkerPrivilege privilege = workerPrivilegeService.findByAdminId(adminId);
        if(privilege != null){
            workerPrivilegeService.delByRoleId(privilege.getRoleId());
        }
        return new SimpleJsonResponse(true, "删除成功！");
    }
    
}
