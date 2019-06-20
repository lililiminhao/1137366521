/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.activity.controller;

import com.lmf.integral.secuity.UserDetail;
import com.lmf.openapi.wechat.entity.WechatUser;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.service.WebsiteUserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author shenzhixiong
 */
@Controller
public class ActivityController {
    
    private final static Logger logger  = LoggerFactory.getLogger(ActivityController.class);
    
    @Autowired
    private WebsiteUserService websiteUserService;
    
    @RequestMapping(value = "/activity/activity.php", method = RequestMethod.GET)
    public  String activity(@RequestParam("token") String token, 
                            UserDetail currentUser,
                            Website website,
                            Model model,
                            HttpSession session,
                            HttpServletRequest request){
        WechatUser wechatUser = (WechatUser) session.getAttribute("wechat_user");
        if(wechatUser != null) {
            WebsiteUser websiteUser = websiteUserService.findOne(wechatUser);
            if(websiteUser != null) {
                currentUser = new UserDetail(websiteUser.getId().intValue(), websiteUser.getLoginName(), websiteUser.getNickName(),websiteUser.getIntegral());
                session.setAttribute("currentUser", currentUser);
            } 
        }
        
        logger.warn("activity start");
        model.addAttribute("token", token);
        return "activity/step1";
    }
    
}
