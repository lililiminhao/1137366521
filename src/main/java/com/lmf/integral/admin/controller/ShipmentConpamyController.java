/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.controller;

import com.lmf.sys.service.ShipmentCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author shenzhixiong
 */
@Controller
public class ShipmentConpamyController {
    
    @Autowired
    private ShipmentCompanyService shipmentCompanyService;
            
    @RequestMapping(value = "/admin/shimpentCompanys.php", method = RequestMethod.GET)
    public String list(Model model) {
        
        model.addAttribute("companys", shipmentCompanyService.all());
        
        return "website/shipmentCompany/list";
    }
    
}
