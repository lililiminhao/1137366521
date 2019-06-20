/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.controller;

import com.lmf.website.entity.WebsiteAdministrator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author shenzhixiong
 */
@Controller("adminIndexController")
public class IndexController {
    
    @RequestMapping(value = "/admin/index.php", method = RequestMethod.GET)
    public String index(WebsiteAdministrator admin, Model model)
    {
        model.addAttribute("admin", admin);
        return "admin/index";
    }
}
