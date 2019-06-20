/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.enterprise.controller;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.PagerSpec;
import com.lmf.enterprise.entity.Enterprise;
import com.lmf.enterprise.entity.EnterpriseExclusiveProductPool;
import com.lmf.enterprise.service.EnterpriseEmployeeService;
import com.lmf.enterprise.service.EnterpriseExclusiveProductPoolService;
import com.lmf.enterprise.service.EnterpriseService;
import com.lmf.enterprise.vo.EnterpriseEmployeeCriteria;
import com.lmf.enterprise.vo.EnterpriseExclusiveProductPoolCriteria;
import com.lmf.website.entity.SpecialActivity;
import com.lmf.website.enums.SpecialActivityType;
import com.lmf.website.service.SpecialActivityService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 企业管理
 *
 * @author lianwukun
 */
@Controller("enterpriseController")
public class EnterpriseController {

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private EnterpriseEmployeeService enterpriseEmployeeService;

    @Autowired
    private EnterpriseExclusiveProductPoolService productPoolService;

    @Autowired
    private SpecialActivityService specialActivityService;

    //专享企业列表
    @RequiresPermissions("enterprise_list:view")
    @RequestMapping(value = "/admin/enterprise.php", method = RequestMethod.GET)
    public String list(@PagerSpecDefaults(pageSize = 20, sort = "id.desc") PagerSpec pager,
                       Enterprise enterprise,
                       Model model) throws UnsupportedEncodingException {
        Page<Enterprise> enterprises = enterpriseService.list(enterprise, pager);
        Map<Integer, SpecialActivity> activityMap = specialActivityService.find(SpecialActivityType.enterpriseSpecial);
        Map<Integer, EnterpriseExclusiveProductPool> productPoolMap = productPoolService.selectAllToMap();

        model.addAttribute("enterprises", enterprises);
        model.addAttribute("activityMap", activityMap);
        model.addAttribute("productPoolMap", productPoolMap);
        StringBuilder link = new StringBuilder("/jdvop/admin/enterprise.php?page=[:page]");
        if (enterprise.getName() != null) {
            link.append("&name=").append(URLEncoder.encode(enterprise.getName(), "UTF-8"));
            model.addAttribute("name", enterprise.getName());
        }
        model.addAttribute("link",link.toString());
        return "/admin/enterprise/list";
    }

    //编辑企业页面
    @RequiresPermissions("enterprise_list:edit")
    @RequestMapping(value = "/admin/enterprise/edit.php", method = RequestMethod.GET)
    public String edit(@RequestParam(value = "id", required = false)Integer id,
                       Model model) {
        List<SpecialActivity> specialActivities = new ArrayList<>();
        Enterprise enterpriseR = enterpriseService.getOneById(id);
        Page<EnterpriseExclusiveProductPool> productPools = productPoolService.selectByPage(null, new EnterpriseExclusiveProductPoolCriteria().andEnableEquals(true));
        if(enterpriseR != null){
            specialActivities = specialActivityService.find(Math.toIntExact(enterpriseR.getProductPoolId()), SpecialActivityType.enterpriseSpecial);
        }
        model.addAttribute("specialActivities",specialActivities);
        model.addAttribute("productPools", productPools);
        model.addAttribute("enterprise", enterpriseR);
        return "/admin/enterprise/form";
    }

    //编辑企业
    @RequiresPermissions("enterprise_list:edit")
    @RequestMapping(value = "/admin/enterprise/edit.php", method = RequestMethod.POST)
    @ResponseBody
    public SimpleJsonResponse<String> editPost(Enterprise enterprise,
                                        Model model) {
        boolean exists = enterpriseService.exists(enterprise.getId(), enterprise.getName());
        if(exists){
            return new SimpleJsonResponse<>(false, "该企业名已存在");
        }
        List<Enterprise> enterpriseList = new ArrayList<>();
        enterpriseList.add(enterprise);
        int updateNum = enterpriseService.update(enterpriseList);
        if(updateNum>0){
            return new SimpleJsonResponse<>(true, null);
        }else{
            return new SimpleJsonResponse<>(false, "更新失败");
        }
    }

    //新增企业
    @RequiresPermissions("enterprise_list:create")
    @RequestMapping(value = "/admin/enterprise/add.php", method = RequestMethod.POST)
    @ResponseBody
    public SimpleJsonResponse<String> addPost(Enterprise enterprise,
                                              Model model) {
        List<Enterprise> enterpriseList = new ArrayList<>();
        boolean exists = enterpriseService.exists(enterprise.getName());
        if(exists){
            return new SimpleJsonResponse<>(false, "该企业名已存在");
        }
        enterpriseList.add(enterprise);
        int updateNum = enterpriseService.save(enterpriseList);
        if(updateNum>0){
            return new SimpleJsonResponse<>(true, null);
        }else{
            return new SimpleJsonResponse<>(false, "添加失败");
        }
    }

    //删除企业
    @ResponseBody
    @RequiresPermissions("enterprise_list:delete")
    @RequestMapping(value = "/admin/enterprise/delete.php", method = RequestMethod.GET)
    public SimpleJsonResponse<String> deletePost(Enterprise enterprise,
                                              Model model) {
        EnterpriseEmployeeCriteria criteria = new EnterpriseEmployeeCriteria();
        criteria.andEnterpriseIdEquals(enterprise.getId());
        Boolean exists = enterpriseEmployeeService.exists(criteria);
        if(exists){
            return new SimpleJsonResponse<>(false, "删除失败，该企业有员工，不能删除!");
        }
        int updateNum = enterpriseService.removeById(enterprise.getId());
        if(updateNum>0){
            return new SimpleJsonResponse<>(true, null);
        }else{
            return new SimpleJsonResponse<>(false, "删除失败");
        }
    }


}
