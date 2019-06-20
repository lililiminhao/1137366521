/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.task;

import com.lmf.common.Page;
import com.lmf.market.entity.Coupon;
import com.lmf.market.entity.FenxiaoOrder;
import com.lmf.market.entity.UserCoupon;
import com.lmf.market.service.CouponService;
import com.lmf.market.service.FenXiaoOrderService;
import com.lmf.market.service.UserCouponService;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.enums.OrderStatus;
import com.lmf.order.service.OrderService;
import com.lmf.order.vo.OrderCriteria;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 创建订单后, 24小时内未支付则系统自动关闭订单
 * @author Mine
 */
public class CloseWaitingPayOrderTask {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    FenXiaoOrderService fenXiaoOrderService;
    
    @Autowired 
    private CouponService couponService;
    
    @Autowired 
    private UserCouponService usercouponService;
    
    @Autowired
    private TimeZone    tz;
    
    private final Logger    logger  = LoggerFactory.getLogger(CloseWaitingPayOrderTask.class);
    
    @Scheduled(fixedDelay =  60 * 60 * 1000, initialDelay = 30 * 60 * 1000)
    public void closeWaitingPayTask()
    {
        logger.info(" \n========================== 开始执行, 系统自动关闭24小时逾期未付款订单 ==============================");
        OrderCriteria params = new OrderCriteria().withStatus(OrderStatus.waiting_pay);
        long endTime = Calendar.getInstance(tz).getTimeInMillis() - (24 * 60 * 60 * 1000);
        Date endDate = new Date(endTime);
        params.withCreateTime(null, endDate);
        Page<ShoppingOrder> orderPage = orderService.find(null, null, params, null);
        long successCount = 0;
        if(orderPage != null && orderPage.hasContent()){
            logger.info(" \n==================== 总计{}条订单需要自动关闭 ====================", orderPage.getContent().size());
            for(ShoppingOrder order : orderPage.getContent()){
                try{
                    orderService.systemCloseWaitingPayOrder(order);
                    //清除分销商冻结额度
                    List<FenxiaoOrder> fenxiaoLists = fenXiaoOrderService.findByOrderId(order.getId());
                    if(CollectionUtils.isNotEmpty(fenxiaoLists)){
                    	for (FenxiaoOrder fenxiaoOrder : fenxiaoLists) {
                    		fenXiaoOrderService.clearFreezeMoney(fenxiaoOrder.getId());
						}
                    	//修改状态
                    	fenXiaoOrderService.del(order.getId());
                    }
                    //返还优惠券
                    if(order.getCouponId() != null && order.getCouponId() != 0 ){
                    	Coupon coupon = couponService.findById(order.getCouponId());
                    	if(coupon!=null){
                    		List<UserCoupon> userAndCoupon = usercouponService.selectByUserAndCoupon(order.getUserId().longValue(), coupon.getId());
                    		if(CollectionUtils.isNotEmpty(userAndCoupon)){
                    			for (UserCoupon userCoupon : userAndCoupon) {
                    				if(userCoupon.getEndTime().after(new Date())){
                    					userCoupon.setStatus(1);
                    				}else{
                    					userCoupon.setStatus(-1);
                    				}
                    				usercouponService.edit(userCoupon);
								}
                    		}
                    	}
                    }
                    
                    successCount ++;
                }catch(Exception ex){
                    logger.error("\n自动关闭逾期未付款订单失败, 订单号{}, 失败原因{}", order.getKey(), ex.getMessage());
                }
            }
        }
        logger.info(" \n========================== 执行完毕, 成功关闭{}条逾期未付款订单 ==============================", successCount);
    }
    
}
