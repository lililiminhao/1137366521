package com.lmf.integral.admin.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.ResponseResult;
import com.lmf.common.util.PagerSpec;
import com.lmf.integral.admin.shiro.ACLDefine;
import com.lmf.integral.admin.shiro.WebsiteAdministratorPrincipal;
import com.lmf.market.entity.Coupon;
import com.lmf.market.repository.vo.CouponCriteria;
import com.lmf.website.entity.Role;
import com.lmf.website.entity.WorkerPrivilege;
import com.lmf.website.repository.vo.RoleCriteria;
import com.lmf.website.service.RoleService;
import com.lmf.website.service.WebsiteAdministratorService;
import com.lmf.website.service.WorkerPrivilegeService;


@Controller
public class RoleController {

	@Autowired
	private RoleService roleService;
	
	@Autowired
	private WorkerPrivilegeService workerPrivilegeService;
	
	@Autowired
    private WebsiteAdministratorService websiteAdministratorService;
	
    /**
     *  添加
     */
    @RequestMapping(value = "/admin/role/add.php", method = RequestMethod.POST)
    public @ResponseBody ResponseResult add(@RequestParam(value = "roleType", required = false)Integer roleType, 
    		@RequestParam(value = "roleName", required = false)String roleName,
    		@RequestParam(value = "roleStatus", required = false)Integer roleStatus, 
    		@RequestParam(value = "remark", required = false)String remark) throws ParseException {
    	if (roleType == null || roleType == 0) {
			return new ResponseResult("-1", "角色所属类型不能为空");
		}
    	if (StringUtils.isBlank(roleName)) {
			return new ResponseResult("-1", "角色名称不能为空");
		}
    	
    	if (roleStatus == null) {
			return new ResponseResult("-1", "请选择角色状态");
		}
    	
    	
    	Role role = new Role();
    	role.setCreateTime(new Date());
    	role.setDataFlag(1);
    	role.setRemark(remark);
    	role.setRoleName(roleName);
    	role.setRoleStatus(roleStatus);
    	role.setRoleType(roleType);
		
		int result  = roleService.add(role);
		
		
		if (result == 1) {
			return new ResponseResult("1", "新增成功");
		} else {
			return new ResponseResult("-1", "新增失败");
		}
	}
    
    /**删除
     * @param id
     * @return
     */
    @Transactional
    @RequestMapping(value = "/admin/Role/del.php", method = RequestMethod.GET)
    public @ResponseBody ResponseResult del(Integer id) {
    	int result = roleService.del(id);
    	//删除对应关系
    	workerPrivilegeService.delByRoleId(id);
    	//删除对应管理员
    	List<WorkerPrivilege> list =  workerPrivilegeService.selectByRoleId(id);
    	for (WorkerPrivilege workerPrivilege : list) {
    		websiteAdministratorService.delById(workerPrivilege.getWorkerId());
		}
    	
    	if (result == 1) {
    		return new ResponseResult("1", "删除成功");
		} else {
			return new ResponseResult("-1", "删除异常");
		}
    }
    
    /**
     * @param 跳转到新增界面
     * @return
     */
    @RequestMapping(value = "/admin/website/role/toAdd.php", method = RequestMethod.GET)
    public String toAdd(Model model) {
		return "/admin/website/role/addRole";
    }
    
    /**
     * @param 跳转到编辑界面
     * @return
     */
    @RequestMapping(value = "/admin/website/role/toEdit.php", method = RequestMethod.GET)
    public String toEdit(Model model,Integer id) {
    	Role role = roleService.findById(id);
    	model.addAttribute("role", role);
    	return "/admin/website/role/editRole";
    }
    
    /**
     * @param 跳转到查看界面
     * @return
     */
    @RequestMapping(value = "/admin/website/role/show.php", method = RequestMethod.GET)
    public String show(Model model,Integer id) {
    	Role role = roleService.findById(id);
    	model.addAttribute("role", role);
    	return "/admin/website/role/show";
    }
    
    /**
     * @param 条件查询所有角色
     * @return
     */
    @RequestMapping(value = "/admin/website/role/selectBy.php", method = RequestMethod.GET)
    public @ResponseBody ResponseResult selectBy(@RequestParam(value = "roleType", required = false)Integer roleType,
    		@RequestParam(value = "roleStatus", required = false)Integer roleStatus,
    		@PagerSpecDefaults(pageSize = 200000, maxPageSize = 100, sort = "createTime.desc") PagerSpec pager,Model model) {
		StringBuilder link = new StringBuilder(
				"/jdvop/admin/role/list.php.php?page=[:page]");
		RoleCriteria criteria = new RoleCriteria();
		if (roleType!=null) {
			criteria.roleType = roleType;
		}
		if (roleStatus!=null) {
			criteria.roleStatus = roleType;
		}
    	Page<Role> roles = roleService.pageQuery(criteria,pager);
    	return new ResponseResult("1","",roles);
    }
    
    /**
     * @param 跳转到配置权限
     * @return
     */
    @RequestMapping(value = "/admin/website/role/toPrivileges.php", method = RequestMethod.GET)
    public String privileges(Model model,Integer id) {
    	Role role = roleService.findById(id);
    	Map<String, List<String>> myActions = initActionsAsMap(role.getPrivileges());
    	model.addAttribute("ACLDefine", ACLDefine.values());
    	model.addAttribute("role", role);
    	model.addAttribute("myActionMap", myActions);
    	return "/admin/website/role/form";
    }
    
    /**
     * 编辑
     * @param id
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = "/admin/Role/privileges.php", method = RequestMethod.POST)
    public @ResponseBody ResponseResult privileges(
    		@RequestParam(value = "id", required = false)Integer id,
    		@RequestParam(value = "action[]", required = false) String[] actions) throws ParseException {
    	Role role =roleService.findById(id);
    	if(actions!= null && actions .length>0){
    		role.setPrivileges(actions);
    	}else{
    		String[] newAction = {"noPrivileges"};
    		role.setPrivileges(newAction);
    	}
    	int result = roleService.edit(role);
    	if (result == 1) {
    		return new ResponseResult("1", "编辑成功");
		} else {
			return new ResponseResult("-1", "编辑异常");
		}
    }
    
    /**
     * 编辑
     * @param id
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = "/admin/Role/edit.php", method = RequestMethod.GET)
    public @ResponseBody ResponseResult edit(@RequestParam(value = "roleType", required = false)Integer roleType, 
    		@RequestParam(value = "roleName", required = false)String roleName,
    		@RequestParam(value = "id", required = false)Integer id,
    		@RequestParam(value = "roleStatus", required = false)Integer roleStatus, 
    		@RequestParam(value = "remark", required = false)String remark,
    		@RequestParam(value = "action[]", required = false) String[] actions) throws ParseException {
    	Role role =roleService.findById(id);
    	if(StringUtils.isNotEmpty(remark)){
    		role.setRemark(remark);
    	}
    	if(StringUtils.isNotEmpty(roleName)){
    		role.setRoleName(roleName);
    	}
    	if(roleStatus != null){
    		role.setRoleStatus(roleStatus);
    	}
    	if(roleType != null){
    		role.setRoleType(roleType);
    	}
    	if(actions!= null && actions .length>0){
    		role.setPrivileges(actions);
    	}
    	int result = roleService.edit(role);
    	if (result == 1) {
    		return new ResponseResult("1", "编辑成功");
		} else {
			return new ResponseResult("-1", "编辑异常");
		}
    }
    
    /**分页查询
     * @param key
     * @param startTime
     * @param endTime
     * @param pager
     * @param model
     * @return
     */
    @RequestMapping(value = "/admin/role/list.php", method = RequestMethod.GET)
    public String pageQuery(@RequestParam(value = "key", required = false)String key,
    		@PagerSpecDefaults(pageSize = 20, maxPageSize = 100, sort = "createTime.desc") PagerSpec pager,Model model) {
		StringBuilder link = new StringBuilder(
				"/jdvop/admin/role/list.php.php?page=[:page]");
		RoleCriteria criteria = new RoleCriteria();
//		if (StringUtils.isNotBlank(key)) {
//			link.append("&ky=").append(key);
//			model.addAttribute("key", key);
//			criteria.key = key;
//		}
		
    	Page<Role> roles = roleService.pageQuery(criteria,pager);
    	model.addAttribute("link",link.toString());
    	model.addAttribute("roles",roles);
    	return "/admin/website/role/list";
    }
    
    private Map<String, List<String>> initActionsAsMap(String[] actions){
            if(actions != null && actions.length > 0){
                Map<String, List<String>> actionMap = new LinkedHashMap<>();
                for(String ac : actions){
                    String[] splitActions = ac.split(":|：");
                    if(splitActions.length == 2){
                        String key = splitActions[0];
                        List<String> valueList = actionMap.get(key);
                        if(valueList == null){
                            valueList = new ArrayList<>();
                            actionMap.put(key, valueList);
                        }
                        valueList.add(splitActions[1]);
                    }
                }
                return actionMap;
            }
        return Collections.EMPTY_MAP;
    }
}
