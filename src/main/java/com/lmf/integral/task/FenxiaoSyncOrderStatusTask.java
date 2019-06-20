package com.lmf.integral.task;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.lmf.market.entity.FenxiaoOrder;
import com.lmf.market.service.FenXiaoOrderService;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.enums.OrderStatus;
import com.lmf.order.service.OrderService;

public class FenxiaoSyncOrderStatusTask {

	@Autowired
	private FenXiaoOrderService fenXiaoOrderService;

	@Autowired
	private OrderService orderService;

	private final Logger logger = LoggerFactory.getLogger(FenxiaoSyncOrderStatusTask.class);

	@Scheduled(cron = "0 0/5 * * * ?")
	public void unfreeze() {
		logger.info(" \n========================== 开始执行,订单状态同步任务 ==============================");
		List<FenxiaoOrder> list = fenXiaoOrderService.findAllNotProcess();
		String size = (CollectionUtils.isEmpty(list) ? "0" : String.valueOf(list.size()));
		logger.info(" \n========================== 订单状态同步任务查询到记录数" + size + " ==============================");
		if (CollectionUtils.isNotEmpty(list)) {
			for (FenxiaoOrder fenxiaoOrder : list) {
				ShoppingOrder order = orderService.findOne(fenxiaoOrder.getOrderId().longValue());
				if (order != null && (order.getStatus() == OrderStatus.completed
						|| order.getStatus() == OrderStatus.after_sale_completed)) {
					fenxiaoOrder.setStatus("已付款");
					fenxiaoOrder.setUpdateTime(new Date());
					fenXiaoOrderService.edit(fenxiaoOrder);
				}
			}
		}
	}
}
