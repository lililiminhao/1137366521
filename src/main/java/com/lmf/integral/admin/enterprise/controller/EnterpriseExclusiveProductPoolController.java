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
import com.lmf.enterprise.entity.EnterpriseExclusiveProductPool;
import com.lmf.enterprise.service.EnterpriseExclusiveProductPoolEntryService;
import com.lmf.enterprise.service.EnterpriseExclusiveProductPoolService;
import com.lmf.enterprise.vo.EnterpriseExclusiveProductPoolCriteria;
import com.lmf.website.entity.SpecialActivity;
import com.lmf.website.enums.SpecialActivityType;
import com.lmf.website.service.SpecialActivityService;

import org.apache.shiro.authz.annotation.Logical;
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
import java.util.List;

/**
 * 企业专享价商品池管理
 *
 * @author lianwukun
 */
@Controller("enterpriseExclusiveProductPoolController")
public class EnterpriseExclusiveProductPoolController {

    @Autowired
    private EnterpriseExclusiveProductPoolService productPoolService;

    @Autowired
    private EnterpriseExclusiveProductPoolEntryService productPoolEntryService;

    @Autowired
    private SpecialActivityService specialActivityService;

    //企业专享池列表
    @RequiresPermissions("enterprise_exclusive:view")
    @RequestMapping(value = "/admin/enterpriseExclusiveProductPool.php", method = RequestMethod.GET)
    public String list(@PagerSpecDefaults(pageSize = 20, sort = "id.desc") PagerSpec pager,
                       EnterpriseExclusiveProductPool productPool,
                       Model model) throws UnsupportedEncodingException {
        StringBuilder link = new StringBuilder("/jdvop/admin/enterpriseExclusiveProductPool.php?page=[:page]");
        EnterpriseExclusiveProductPoolCriteria criteria = new EnterpriseExclusiveProductPoolCriteria();

        if(productPool.getName() != null && !productPool.getName().isEmpty()){
            criteria.andNameLike("%"+productPool.getName()+"%");
            link.append("&name=").append(URLEncoder.encode(productPool.getName(), "UTF-8"));
            model.addAttribute("name", productPool.getName());
        }
        if (productPool.getEnable() != null) {
            criteria.andEnableEquals(productPool.getEnable());
            link.append("&enable=").append(URLEncoder.encode(productPool.getName(), "UTF-8"));
            model.addAttribute("enable", productPool.getEnable());
        }
        criteria.setOrderBy("id desc");
        Page<EnterpriseExclusiveProductPool> productPools = productPoolService.selectByPage(pager, criteria);
        model.addAttribute("productPoolService",productPoolService);
        model.addAttribute("productPools", productPools);
        model.addAttribute("link",link.toString());
        return "/admin/enterprise/enterprise_exclusive_product_pool/list";
    }

    //编辑企业专享池页面
    @RequiresPermissions("enterprise_exclusive:edit")
    @RequestMapping(value = "/admin/enterpriseExclusiveProductPool/edit.php", method = RequestMethod.GET)
    public String edit(@RequestParam(value = "id", required = false)Integer id,
                       Model model) {
        EnterpriseExclusiveProductPool productPool = productPoolService.selectOne(id);
        model.addAttribute("productPool", productPool);
        return "/admin/enterprise/enterprise_exclusive_product_pool/form";
    }

    //编辑企业专享池
    @RequiresPermissions("enterprise_exclusive:edit")
    @ResponseBody
    @RequestMapping(value = "/admin/enterpriseExclusiveProductPool/edit.php", method = RequestMethod.POST)
    public SimpleJsonResponse<String> editPost(EnterpriseExclusiveProductPool productPool,Model model) {
        List<SpecialActivity> specialActivities = specialActivityService.find(productPool.getId(), SpecialActivityType.enterpriseSpecial);
        if(specialActivities.size()>0 && productPool.getEnable() == false){
            return new SimpleJsonResponse<>(false, "专享活动在使用该专享池，不能禁用!");
        }
        boolean exists = productPoolService.exists(new EnterpriseExclusiveProductPoolCriteria().andIdNotEquals(productPool.getId()).andNameEquals(productPool.getName()));
        if(exists){
            return new SimpleJsonResponse<>(false, "该企业专享池已存在");
        }
        try {
            productPoolService.updateById(productPool);
        } catch (Exception e) {
            e.printStackTrace();
            return new SimpleJsonResponse<>(false, "更新失败");
        }
        return new SimpleJsonResponse<>(true, null);
    }

    //新增企业专享池
    @RequiresPermissions("enterprise_exclusive:create")
    @RequestMapping(value = "/admin/enterpriseExclusiveProductPool/add.php", method = RequestMethod.POST)
    @ResponseBody
    public SimpleJsonResponse<String> addPost( EnterpriseExclusiveProductPool productPool,
                                              Model model) {
    	EnterpriseExclusiveProductPoolCriteria criteria = new EnterpriseExclusiveProductPoolCriteria();
    	//查找系统共享池
    	if(productPool!=null&&productPool.getType()!=null&&productPool.getType().intValue()==2) {
    		criteria.andTypeEquals(2);
    		boolean exists = productPoolService.exists(criteria);
    		if(exists){
                return new SimpleJsonResponse<>(false, "系统专享池已存在，且只能有一个。");
            }
    	}
    	//查找同名共享池
    	criteria = new EnterpriseExclusiveProductPoolCriteria();
    	criteria.andNameEquals(productPool.getName());
        boolean exists = productPoolService.exists(criteria);
        if(exists){
            return new SimpleJsonResponse<>(false, "该企业专享池已存在");
        }
        EnterpriseExclusiveProductPool insert = productPoolService.insert(productPool);
        if(insert.getId()>0){
            return new SimpleJsonResponse<>(true, null);
        }else{
            return new SimpleJsonResponse<>(false, "添加失败");
        }
    }

    //编辑企业专享池启用或禁用
    @ResponseBody
    @RequiresPermissions(value={"enterprise_exclusive:startUse","enterprise_exclusive:endUse"},logical=Logical.OR)
    @RequestMapping(value = "/admin/enterpriseExclusiveProductPool/setEnable.php", method = RequestMethod.GET)
    public SimpleJsonResponse<String> setEnable(EnterpriseExclusiveProductPool productPool){
        List<SpecialActivity> specialActivities = specialActivityService.find(productPool.getId(), SpecialActivityType.enterpriseSpecial);
        if(specialActivities.size()>0 && productPool.getEnable() == false){
            return new SimpleJsonResponse<>(false, "专享活动在使用该专享池，不能禁用!");
        }
        try {
            productPoolService.updateById(productPool);
        } catch (Exception e) {
            e.printStackTrace();
            return new SimpleJsonResponse<>(false, "更新失败");
        }
        return new SimpleJsonResponse<>(true, null);
    }


    //删除企业专享池
    @ResponseBody
    @RequiresPermissions("enterprise_exclusive:delete")
    @RequestMapping(value = "/admin/enterpriseExclusiveProductPool/delete.php", method = RequestMethod.GET)
    public SimpleJsonResponse<String> deletePost(EnterpriseExclusiveProductPool productPool,
                                              Model model) {
        List<SpecialActivity> specialActivities = specialActivityService.find(productPool.getId(), SpecialActivityType.enterpriseSpecial);
        if(specialActivities.size()>0){
            return new SimpleJsonResponse<>(false, "专享活动在使用该专享池，不能删除!");
        }
        try {
            productPoolService.deleteById(productPool.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return new SimpleJsonResponse<>(false, "删除失败");
        }
        return new SimpleJsonResponse<>(true, null);
    }





}
