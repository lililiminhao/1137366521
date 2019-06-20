/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.task;

import com.lmf.order.entity.NifferOrder;
import com.lmf.order.enums.NifferOrderStatus;
import com.lmf.order.service.NifferOrderService;
import com.lmf.system.sdk.service.SystemReturnNifferOrderService;
import com.lmf.system.sdk.vo.AfterSaleOrderDetailResult;
import com.lmf.website.entity.Website;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 *
 * @author wenguanhai
 */
public class SystemAfterSaleTask {

    @Autowired
    private NifferOrderService nifferOrderService;
    @Autowired
    private SystemReturnNifferOrderService systemReturnNifferOrderService;
    @Autowired
    private Website website;

    private final Logger   logger  = LoggerFactory.getLogger(SystemAfterSaleTask.class);
    
    @Scheduled(fixedDelay = 30 * 60 * 1000, initialDelay = 5 * 60 * 1000) //30分钟执行一次，延迟5分钟执行
    public void synAfterSaleStatus() throws UnsupportedEncodingException {
        List<NifferOrder> nifferOrderList = nifferOrderService.findSystemAcceptedAndDeclinedList();
        if (nifferOrderList != null) {
            logger.info("--------------------------------开始同步平台产品售后状态--------------------------------------------");
            AfterSaleOrderDetailResult afterSaleOrderDetailResult = null;
            try {
                for (NifferOrder nifferOrder : nifferOrderList) {
                    if(nifferOrder.getOpenServiceOrder()==null || nifferOrder.getOpenServiceOrder().isEmpty()){
                        continue;
                    }
                    afterSaleOrderDetailResult = systemReturnNifferOrderService.getAfterSaleOrderDetail(website, nifferOrder.getOpenServiceOrder());
                    if (afterSaleOrderDetailResult != null) {
                        String systemAfterSaleOrderStatus = afterSaleOrderDetailResult.getStatus();
                        NifferOrderStatus nifferOrderStatus = null;
                        if ("waiting_confirmed".equals(systemAfterSaleOrderStatus)) {
                            nifferOrderStatus = NifferOrderStatus.waiting_accept;
                        } else if ("completed".equals(systemAfterSaleOrderStatus)) {
                            nifferOrderStatus = NifferOrderStatus.accepted;
                        } else if ("waiting_return".equals(systemAfterSaleOrderStatus)) {
                            nifferOrderStatus = NifferOrderStatus.waiting_audit;
                        } else {
                            nifferOrderStatus = Enum.valueOf(NifferOrderStatus.class, systemAfterSaleOrderStatus);
                        }
                        if (!nifferOrder.getOpenStatus().equals(nifferOrderStatus)) {
                            nifferOrderService.setOpenStatus(nifferOrder, nifferOrderStatus);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("同步平台换货订单状态异常", e);
            }finally{
                logger.info("--------------------------------结束同步平台产品售后状态--------------------------------------------");
            }

        }
    }
}
