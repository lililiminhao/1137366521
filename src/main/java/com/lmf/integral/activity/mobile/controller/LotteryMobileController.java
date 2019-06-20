/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.activity.mobile.controller;

import com.lmf.activity.entity.AwardLog;
import com.lmf.activity.enums.AwardStatus;
import com.lmf.activity.service.AwardLogService;
import com.lmf.activity.vo.AwardLogCriteria;
import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.exceptions.PermissionDeniedException;
import com.lmf.common.util.PagerSpec;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.service.OrderService;
import com.lmf.website.entity.WebsiteUser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author liuqi
 */
@Controller
public class LotteryMobileController {
    
    @Autowired
    private AwardLogService awardLogService;
        
    @Autowired
    private OrderService orderService;
    
    @RequestMapping(value="/my/lottery/mobile/myAwards.php",method = RequestMethod.GET)
    public String myAwards(WebsiteUser websiteUser, @PagerSpecDefaults(pageSize = 10, sort = "create_time.desc") PagerSpec pager, Model model) {
        if(websiteUser == null || websiteUser.getId() <= 0){
            throw new PermissionDeniedException();
        }
        Long userId = websiteUser.getId();
        Page<AwardLog> page = awardLogService.find(new AwardLogCriteria().withUserId(userId.intValue()), pager);
        List<AwardLog> list = page.getContent();
        if(list != null){
            Map map = new HashMap() ;
            for(AwardLog log: list) {
                if(log.getAwardStatus() == AwardStatus.enter_order_process){
                    Integer orderId = (Integer)log.getExt().get("orderId");
                    if(orderId != null){
                        ShoppingOrder order = orderService.findOne(orderId);
                        if(order != null){
                            map.put(orderId, order.getStatus());
                        }
                    }
                }
            }
            model.addAttribute("orderStatus", map);
        }
        model.addAttribute("myAwards", page);
        return "/user_center/awards";
    }
    
    
    
    //我的奖品 功能
    @RequestMapping(value="/my/lottery/mobile/myAwards.php", params = "ajax",method = RequestMethod.GET)
    public @ResponseBody Map ajaxAwards(WebsiteUser websiteUser, @PagerSpecDefaults(pageSize = 10, sort = "create_time.desc") PagerSpec pager, Model model) {
        if(websiteUser == null || websiteUser.getId() <= 0){
            throw new PermissionDeniedException();
        }
        Map data = new HashMap();
        Long userId = websiteUser.getId();
        Page<AwardLog> page = awardLogService.find(new AwardLogCriteria().withUserId(userId.intValue()), pager);
        List<AwardLog> list = page.getContent();
        if(list != null){
            Map map = new HashMap() ;
            for(AwardLog log: list) {
                if(log.getAwardStatus() == AwardStatus.enter_order_process){
                    Integer orderId = (Integer)log.getExt().get("orderId");
                    if(orderId != null){
                        ShoppingOrder order = orderService.findOne(orderId);
                        if(order != null){
                            map.put(orderId, order.getStatus());
                        }
                    }
                }
            }
            data.put("orderStatus", map);
        }
        data.put("page", page);
        return data;
    }
}
