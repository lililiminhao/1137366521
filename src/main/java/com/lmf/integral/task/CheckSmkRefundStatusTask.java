/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.task;

import com.lmf.common.Constant;
import com.lmf.common.Page;
import com.lmf.extend.pay.smkpay.SmkpayUtil;
import com.lmf.extend.pay.smkpay.json.SmkQueryRequestJson;
import com.lmf.extend.pay.smkpay.util.CertificateCoder;
import com.lmf.integral.SystemConfig;
import com.lmf.order.entity.NifferOrder;
import com.lmf.order.entity.OrderPayLog;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.enums.NifferOrderStatus;
import com.lmf.order.enums.OrderPayType;
import com.lmf.order.service.NifferOrderService;
import com.lmf.order.service.OrderService;
import com.lmf.order.vo.NifferOrderCriteria;
import com.lmf.system.sdk.enums.AfterSaleType;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 *
 * @author wenguanhai
 */
public class CheckSmkRefundStatusTask {

    /**
     * 市民卡退款成功状状态码
     */
    private static final String smkRefundSuccRespCode = "00";
    @Autowired
    private NifferOrderService nifferOrderService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SmkpayUtil smkpayUtil;
    @Autowired
    private SystemConfig systemConfig;
    private final Logger LOGGER = LoggerFactory.getLogger(CheckSmkRefundStatusTask.class);

//    @Scheduled(fixedDelay = 3 * 60 * 1000, initialDelay = 1 * 60 * 1000) //2分钟执行一次，延迟5分钟执行
    @Scheduled(cron = "0 0 1 * * ?")  //每日1点检查退款状态
    public void checkRefundStatus() throws UnsupportedEncodingException {
        NifferOrderCriteria nifferOrderCriteria = new NifferOrderCriteria();
        nifferOrderCriteria.withAfterSaleType(AfterSaleType.returned);
        nifferOrderCriteria.withStatus(NifferOrderStatus.processing_refund);
        Page<NifferOrder> nifferOrders = nifferOrderService.find(nifferOrderCriteria, null);
        LOGGER.info("--------------------------------检查市民卡状态为退款中的记录start--------------------------------------------");
        int totalCount=0,succCount = 0;
        if (nifferOrders != null && nifferOrders.hasContent()) {
            List<NifferOrder> nifferOrderList = nifferOrders.getContent();
            totalCount=nifferOrderList.size();
            try {
                ShoppingOrder shoppingOrder = null;
                ShoppingOrder parentShoppingOrder = null;
                OrderPayLog orderPayLog = null;
                for (NifferOrder nifferOrder : nifferOrderList) {
                    shoppingOrder = orderService.findOne(nifferOrder.getOriginalOrderId());
                    if (shoppingOrder.isSplit()) {
                    	parentShoppingOrder = orderService.findOne(shoppingOrder.getOriginalOrderId());
                    }
                    if(parentShoppingOrder != null) {
                    	orderPayLog = orderService.findPayLog(parentShoppingOrder);
                    } else {
                    	orderPayLog = orderService.findPayLog(shoppingOrder);
                    }
                    if (orderPayLog!=null && orderPayLog.isPaied() && orderPayLog.getPayType().equals(OrderPayType.shiminka_pay)) {
                        /**
                         * 如果退款处理中，反查退款信息
                         */
                        SmkQueryRequestJson queryJson = new SmkQueryRequestJson();
                        queryJson.reqSeq = nifferOrder.getAfterSaleOrderKey();
                        queryJson.merCode = systemConfig.getMerCode();
                        queryJson.orderNo = nifferOrder.getAfterSaleOrderKey();
                        queryJson.channelNo="004";
                        queryJson.tradeType = String.valueOf(Constant.ONE);
                        LOGGER.info("-----------------------市民卡支付异步通知交易查询接口签名参数:{}", queryJson.getMersign());
                        queryJson.sign = CertificateCoder.sign(queryJson.getMersign(), systemConfig.getPfxPassword());
                        LOGGER.info("------------------------任务调用市民卡退款状态为退款中的记录，调用交易查询接口反查，请求参数：" + queryJson);
                        Map<String, Object> queryOrderResponse = smkpayUtil.queryOrder(systemConfig.getSmkpayRefundUrl()+"query", queryJson);
                        LOGGER.info("------------------------任务调用市民卡退款状态为退款中的记录，调用交易查询接口反查，响应结果：" + queryOrderResponse);
                        if (queryOrderResponse.get("respCode").equals(smkRefundSuccRespCode)) {//请求成功
                            if (queryOrderResponse.get("status").equals("01")) {//退款成功，直接返回
                                nifferOrderService.refundSuccessHandle(nifferOrder, shoppingOrder);
                                succCount++;
                            }else if(queryOrderResponse.get("status").equals("02")){
                                //退款失败，设还原为待退款
                                nifferOrderService.setStatus(nifferOrder, NifferOrderStatus.waiting_finance);
                                LOGGER.info("检查市民卡状态为退款中的记录，售后单号：{}，更改状态失败原因：{}",nifferOrder.getAfterSaleOrderKey(),queryOrderResponse.get("respDesc"));
                            }else if(queryOrderResponse.get("status").equals("04")){
                                //交易已取消，直接取消售后订单
                                nifferOrderService.setStatus(nifferOrder, NifferOrderStatus.canceld);
                                LOGGER.info("检查市民卡状态为退款中的记录，售后单号：{}，更改状态失败原因：{}",nifferOrder.getAfterSaleOrderKey(),queryOrderResponse.get("respDesc"));
                            }
                        }else{
                            LOGGER.info("检查市民卡状态为退款中的记录，售后单号：{}，更改状态失败原因：{}",nifferOrder.getAfterSaleOrderKey(),queryOrderResponse.get("respDesc"));
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("检查市民卡状态为退款中的记录", e);
            } 
        }
         LOGGER.info("-----检查市民卡状态为退款中的记录，共处理记录：{},更改成功：{}条记录---------",totalCount,succCount);
                LOGGER.info("--------------------------------检查市民卡状态为退款中的记录end--------------------------------------------");
    }
}
