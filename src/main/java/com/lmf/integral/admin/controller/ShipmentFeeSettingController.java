/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.controller;

import com.lmf.common.SimpleJsonResponse;
import com.lmf.sys.entity.ShipmentFeeSettingsExt;
import com.lmf.sys.enums.ShipmentFeeStandard;
import com.lmf.sys.service.ShipmentFeeSettingsExtService;
import com.lmf.sys.vo.ShipmentFeeProvinceMap;
import com.lmf.website.service.WebsiteAdministratorService;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author lianwukun
 */
@Controller
public class ShipmentFeeSettingController {
    @Autowired
    private ShipmentFeeSettingsExtService shipmentFeeSettingService;
    
    @Autowired
    private WebsiteAdministratorService websiteAdministratorService;
    
    /**
     * 获取快递模板列表
     * @param model
     * @return 
     */
    @RequiresPermissions("provide:shipment")
    @RequestMapping(value = "/admin/shipmentFeeSettingList.php", method = RequestMethod.GET)
    public String  list(Model model) {
        model.addAttribute("shipmentFeeSettings", shipmentFeeSettingService.find());
        return "admin/website/shipmentFeeSettings/list";
    }
    
    /**
     * 修改快递模板页面
     * @param model
     * @param id
     * @return 
     */
    @RequiresPermissions("provide:shipment")
    @RequestMapping(value = "/admin/shipmentFeeSettingEdit.php", method = RequestMethod.GET)
    public String edit(Model model, @RequestParam(value = "id") int id) {
        model.addAttribute("shipmentFeeSettings", shipmentFeeSettingService.findOne(id));
        model.addAttribute("provinceMap", ShipmentFeeProvinceMap.provinceMap);
        return "admin/website/shipmentFeeSettings/edit";
    }
    
    /**
     * 删除快递模板
     * @param model
     * @param id
     * @return 
     */
    @RequiresPermissions("provide:shipment")
    @RequestMapping(value = "/admin/shipmentFeeSettingDelete.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse delete(Model model, @RequestParam(value = "id") int id) {
        if(websiteAdministratorService.existsBySettingId(id)){
            return new SimpleJsonResponse<>(false, "删除失败,有供应商正在使用这个模板");
        }
        shipmentFeeSettingService.delete(id);
        return new SimpleJsonResponse<>(true, "删除成功");
    }
    
    /**
     * 更新快递模板
     * @param model
     * @param ShipmentFeeSettings
     * @return 
     */
    @RequiresPermissions("shipment_fee:edit")
    @RequestMapping(value = "/admin/shipmentFeeSettingSave.php", method = RequestMethod.POST)
    public  @ResponseBody SimpleJsonResponse<String> save(Model model, ShipmentFeeSettingsExt ShipmentFeeSettings) {
        shipmentFeeSettingService.save(ShipmentFeeSettings);
        return new SimpleJsonResponse<>(true, null);
    }
    
    /**
     * 添加快递模板页面
     * @param model
     * @return 
     */
    @RequestMapping(value = "/admin/shipmentFeeSettingAdd.php", method = RequestMethod.GET)
    public String add(Model model) {
        return "admin/website/shipmentFeeSettings/add";
    }
    
    /**
     * 添加快递模板
     * @param model
     * @param ShipmentFeeSettings
     * @return 
     */
    @RequestMapping(value = "/admin/shipmentFeeSettingAdd.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse<String> addPost(Model model, ShipmentFeeSettingsExt ShipmentFeeSettings) {
        
        if(shipmentFeeSettingService.exists(ShipmentFeeSettings.getName())){    
            return new SimpleJsonResponse<>(false, "该模板名称已存在");
        }
        
        ShipmentFeeSettings.setCreateTime(new Date());
        ShipmentFeeSettings.setPostageFreeThresold(65535.0);
        ShipmentFeeSettings.setStandard(ShipmentFeeStandard.amount);
        Map<String, Double> settings = new HashMap();
        for (Map.Entry<String, String> entry : ShipmentFeeProvinceMap.provinceMap.entrySet()) { 
            settings.put(entry.getKey(), 0.0);
            ShipmentFeeSettings.setSettings(settings);
        }
        shipmentFeeSettingService.save(ShipmentFeeSettings);
        return new SimpleJsonResponse<>(true, null);
    }
    
    
}
