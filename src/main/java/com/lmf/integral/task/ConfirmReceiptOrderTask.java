/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.task;


import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.lmf.common.Page;
import com.lmf.common.enums.OwnerType;
import com.lmf.common.tuple.BinaryTuple;
import com.lmf.market.service.FenXiaoOrderService;
import com.lmf.order.entity.OrderShipmentLog;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.enums.OrderStatus;
import com.lmf.order.service.OrderService;
import com.lmf.order.vo.OrderCriteria;

/**
 * 订单发货后, 10天逾期未确认收货则系统自动确认收货
 * @author Mine
 */
public class ConfirmReceiptOrderTask {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private FenXiaoOrderService fenXiaoOrderService;
    
    @Autowired
    private TimeZone    tz;
    
    private final Logger    logger  = LoggerFactory.getLogger(ConfirmReceiptOrderTask.class);
    
    @Scheduled(fixedDelay =  2 * 60 * 60 * 1000, initialDelay = 60 * 60 * 1000)
    public void confirmReceiptOrderTask()
    {
        logger.info(" \n========================== 开始执行, 市民卡供应商订单发货后10天逾期未确认收货则系统自动确认收货 ==============================");
        OrderCriteria criteria = new OrderCriteria().withStatus(OrderStatus.waiting_confirmed);
        long endTime = Calendar.getInstance(tz).getTimeInMillis() - (10 * 24 * 60 * 60 * 1000);
        Date endDate = new Date(endTime);
        criteria.withShipmentTime(null, endDate);
        Page<BinaryTuple<ShoppingOrder, OrderShipmentLog>> orderPage = orderService.findOrderAndShipmentLog(OwnerType.provider, null, criteria, null);
        long successCount = 0;
        if(orderPage != null && orderPage.hasContent()){
            logger.info(" \n==================== 总计{}条订单需要自动确认收货 ====================", orderPage.getContent().size());
            for(BinaryTuple<ShoppingOrder, OrderShipmentLog> order : orderPage.getContent()){
                try{
                	fenXiaoOrderService.changeStatus(order.getValue1().getKey());
                    orderService.systemConfirmReceiptOrder(order.getValue1());
                    successCount ++;
                }catch(Exception ex){
                    logger.error("\n自动确认收货失败, 订单号{}, 失败原因{}", order.getValue1().getKey(), ex.getMessage());
                }
            }
        }
        logger.info(" \n========================== 执行完毕, 成功处理{}条逾期未确认收货 ==============================", successCount);
    }
    
    @Scheduled(fixedDelay =  2 * 60 * 60 * 1000, initialDelay = 60 * 60 * 1000)
    public void confirmReceiptSystemOrderTask()
    {
        logger.info(" \n========================== 开始执行, 系统订单下单后10天逾期未确认收货则系统自动确认收货 ==============================");
        OrderCriteria criteria = new OrderCriteria().withStatus(OrderStatus.waiting_confirmed);
        long endTime = Calendar.getInstance(tz).getTimeInMillis() - (10 * 24 * 60 * 60 * 1000);
        Date endDate = new Date(endTime);
        criteria.withCreateTime(null, endDate);
        Page<ShoppingOrder> orderPage = orderService.find(OwnerType.system, null, criteria, null);
        long successCount = 0;
        if(orderPage != null && orderPage.hasContent()){
            logger.info(" \n==================== 总计{}条订单需要自动确认收货 ====================", orderPage.getContent().size());
            for(ShoppingOrder order : orderPage.getContent()){
                try{
                	fenXiaoOrderService.changeStatus(order.getKey());
                    orderService.systemConfirmReceiptOrder(order);
                    successCount ++;
                }catch(Exception ex){
                    logger.error("\n自动确认收货失败, 订单号{}, 失败原因{}", order.getKey(), ex.getMessage());
                }
            }
        }
        logger.info(" \n========================== 执行完毕, 成功处理{}条逾期未确认收货 ==============================", successCount);
    }
    
}
