package com.lmf.integral.admin.order.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.Range;
import com.lmf.common.util.LMFDateUtil;
import com.lmf.common.util.PagerSpec;
import com.lmf.order.entity.PayFailOrderLog;
import com.lmf.order.entity.ShiminkaPayTransactionDisposeLog;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.enums.OrderPayType;
import com.lmf.order.enums.PayFailOrderStatus;
import com.lmf.order.service.OrderService;
import com.lmf.order.service.ShiminkaPayTransactionDisposeLogService;

@Controller
public class ShiminkaTransactionDisposeOrderLogController {

	@Autowired
	private ShiminkaPayTransactionDisposeLogService shiminkaPayTransactionDisposeLogService;
	
	@Autowired
	private OrderService orderService;
	
	@RequestMapping(value = "/admin/order/transactionDispose/list.php", method = RequestMethod.GET)
    public String list(@RequestParam(value = "kwd", required = false) String keyword,
                       @RequestParam(value = "status", defaultValue = "waiting_operate") PayFailOrderStatus status,
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
        
        Page<ShiminkaPayTransactionDisposeLog> logPage = shiminkaPayTransactionDisposeLogService.find(keyword, status, pager);
        
        if(logPage.hasContent()){
            //查询订单信息及网站信息
            List<Long> orderIds = new ArrayList<>();
            for(ShiminkaPayTransactionDisposeLog log:logPage.getContent()){
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
        
        return "admin/order/transaction_dispose/list";
    }
	
}

