/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.controller;

import com.lmf.integral.service.WebsiteProxyService;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.service.WebsiteService;
import com.lmf.website.theme.v2.PageSkeleton;
import javax.servlet.http.HttpSession;
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
public class IndexController {
    
    @Autowired
    private WebsiteProxyService websiteProxyService;
    
    @RequestMapping(value = "/index.php", method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        
        PageSkeleton  skeleton  = websiteProxyService.getV2ThemeSkeleton();
        if (skeleton != null) {
            model.addAttribute("skeleton", skeleton);
        }
        return "index";
    }
    
    @RequestMapping(value = "/enterpriseIndex.php", method = RequestMethod.GET)
    public String enterpriseIndex(Model model, HttpSession session,WebsiteUser websiteUser) {
        
        PageSkeleton  skeleton  = websiteProxyService.getV2ThemeSkeleton();
        if (skeleton != null) {
            model.addAttribute("skeleton", skeleton);
        }
        model.addAttribute("websiteUser", websiteUser);
        return "index_enterprise";
    }
    
}
