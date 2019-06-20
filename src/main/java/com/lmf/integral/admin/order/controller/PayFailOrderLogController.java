/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.order.controller;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.Range;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.LMFDateUtil;
import com.lmf.common.util.PagerSpec;
import com.lmf.order.entity.PayFailOrderLog;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.enums.OrderPayType;
import com.lmf.order.enums.PayFailOrderStatus;
import com.lmf.order.service.OrderService;
import com.lmf.order.service.PayFailOrderLogService;
import com.lmf.website.entity.Website;
import com.lmf.website.service.WebsiteService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * 支付异常订单列表
 * @author Mine
 */
@Controller
public class PayFailOrderLogController {
    
    @Autowired
    private PayFailOrderLogService payFailOrderLogService;
    
    @Autowired
    private OrderService orderService;
    
//    @RequiresPermissions("order:order_pay_exception")
    @RequestMapping(value = "/admin/order/fail/list.php", method = RequestMethod.GET)
    public String list(@RequestParam(value = "kwd", required = false) String keyword,
                       @RequestParam(value = "status", defaultValue = "waiting_operate") PayFailOrderStatus status,
                       @RequestParam(value = "type", required = false) OrderPayType type,
                       @RequestParam(value = "st", required = false) Date startTime,//起始时间
                       @RequestParam(value = "et", required = false) Date endTime,//结束时间
                       @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager,
                       Model model)
    {
        StringBuilder sbd = new StringBuilder("/jdvop/admin/order/fail/list.php?page=[:page]");
        if(keyword != null){
            sbd.append("&kwd=").append(keyword);
        }
        if(status != null){
            sbd.append("&status=").append(status.name());
        }
        if(type != null){
            sbd.append("&type=").append(type.name());
        }
        if(startTime != null){
            sbd.append("&st=").append(LMFDateUtil.formatYMDHMS(startTime));
        }
        if(startTime != null){
            sbd.append("&et=").append(LMFDateUtil.formatYMDHMS(endTime));
        }
        
        Page<PayFailOrderLog> logPage = payFailOrderLogService.find(keyword, status, type, new Range<>(startTime, endTime), pager);
        
        if(logPage.hasContent()){
            //查询订单信息及网站信息
            List<Long> orderIds = new ArrayList<>();
            for(PayFailOrderLog log:logPage.getContent()){
                orderIds.add(log.getOrderId());
            }
            List<ShoppingOrder> orderList = orderService.find(orderIds);
            if(orderList != null){
                Map<Long, ShoppingOrder> orderMap = new HashMap<>();
                for(ShoppingOrder order : orderList){
                    orderMap.put(order.getId(), order);
                }
                model.addAttribute("orderMap", orderMap);
            }
        }
        
        model.addAttribute("link", sbd.toString());
        model.addAttribute("logPage", logPage);
        model.addAttribute("logStatus", PayFailOrderStatus.values());
        model.addAttribute("payTypes", OrderPayType.values());
        
        return "admin/order/fail/list";
    }
    
    @RequestMapping(value = "/admin/order/fail/completed.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse completed(@RequestParam(value = "id") long logId,
                                                      @RequestParam(value = "remark", required = false) String remark)
    {
        if(logId <= 0){
            return new SimpleJsonResponse(false, "数据异常");
        }
        PayFailOrderLog log = payFailOrderLogService.findById(logId);
        if(log == null){
            return new SimpleJsonResponse(false, "数据不存在或已经被删除.");
        }
        
        log.setSystemRemark(remark);
        payFailOrderLogService.setStatus(log, PayFailOrderStatus.completed);
        
        return new SimpleJsonResponse(true, "操作成功");
    }
}
