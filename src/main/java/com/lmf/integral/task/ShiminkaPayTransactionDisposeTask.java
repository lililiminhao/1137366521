package com.lmf.integral.task;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.lmf.common.Constant;
import com.lmf.common.Page;
import com.lmf.extend.pay.smkpay.SmkpayUtil;
import com.lmf.extend.pay.smkpay.json.SmkQueryRequestJson;
import com.lmf.extend.pay.smkpay.util.CertificateCoder;
import com.lmf.integral.SystemConfig;
import com.lmf.order.entity.ShiminkaPayTransactionDisposeLog;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.enums.OrderPayType;
import com.lmf.order.enums.OrderStatus;
import com.lmf.order.enums.PayFailOrderStatus;
import com.lmf.order.service.OrderService;
import com.lmf.order.service.ShiminkaPayTransactionDisposeLogService;
import com.lmf.website.entity.Website;
import com.lmf.website.service.WebsiteService;

/**
 * 市民卡支付为交易处理中订单处理。
 * @author shenzhixiong
 *
 */
public class ShiminkaPayTransactionDisposeTask {

	@Autowired
	private ShiminkaPayTransactionDisposeLogService shiminkaPayTransactionDisposeLogService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
    private SmkpayUtil smkpayUtil;
	
	@Autowired
	private SystemConfig systemConfig;
	
	@Autowired
	private WebsiteService websiteService;
	
	private final Logger   logger  = LoggerFactory.getLogger(SystemAfterSaleTask.class);
	
	@Scheduled(fixedDelay = 20 * 60 * 1000)
	public void run() throws Exception {
		
		Website website = websiteService.findOne(systemConfig.getWebsiteId());
		
		logger.info(" \n========================== 开始执行, 市民卡支付交易处理中订单处理 ==============================");
		
		Page<ShiminkaPayTransactionDisposeLog> logs = shiminkaPayTransactionDisposeLogService.find(null, PayFailOrderStatus.waiting_operate, null);
		
		int successCount = 0;
		for(ShiminkaPayTransactionDisposeLog log : logs ) {
			ShoppingOrder order = orderService.findOne(log.getOrderId());
			if(order != null && order.getStatus() == OrderStatus.transaction_dispose) {
				SmkQueryRequestJson queryJson = new SmkQueryRequestJson();
	            queryJson.reqSeq = log.getOutTradeOrder();
	            queryJson.merCode = systemConfig.getMerCode();
	            queryJson.orderNo = order.getKey();
	            queryJson.channelNo = "004";
	            queryJson.serialNo = log.getOutTradeOrder();
	            queryJson.tradeType = String.valueOf(Constant.ZERO);
	            logger.info("-----------------------市民卡支付计划任务处理交易处理中，交易查询接口签名参数:{}", queryJson.getMersign());
	            queryJson.sign = CertificateCoder.sign(queryJson.getMersign(), systemConfig.getPfxPassword());
	            logger.info("------------------------市民卡支付计划任务处理交易处理中，调用交易查询接口反查，请求参数：" + queryJson);
	            Map<String, Object> queryOrderResponse = smkpayUtil.queryOrder(systemConfig.getSmkpayRefundUrl() + "query", queryJson);
	            logger.info("------------------------市民卡支付计划任务处理交易处理中，调用交易查询接口反查，结果：" + queryOrderResponse);
	            if(queryOrderResponse != null && queryOrderResponse.get("status") != null && queryOrderResponse.get("status").equals("01")) {
	            	try {
	            		orderService.shiminkaPayTransactionDisposeSystemAffirm(order, website, log.getOutTradeOrder(), log.getRemoteIp());
	            	}catch (Exception e) {
						logger.error("交易处理中计划任务, 处理订单返回失败结果集，shiminkaPayTransactionDisposeSystemAffirm()发生异常！订单号{}", order.getKey(), e);
						
		                //记录异常订单信息
		                orderService.payNotifyFailRecordOrder(order, OrderPayType.shiminka_pay, log.getOutTradeOrder(), log.getRemoteIp());
					}
	            	shiminkaPayTransactionDisposeLogService.setStatus(order.getId(), PayFailOrderStatus.completed);
	            	successCount ++;
	            }
			}
		}
		
		logger.info(" \n========================== 处理完毕, 成功处理{}条交易处理中订单==============================", successCount);
		
	}
	
	
}

