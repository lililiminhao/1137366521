package com.lmf.integral.task;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.lmf.market.entity.FenxiaoOrder;
import com.lmf.market.service.FenXiaoOrderService;

public class FenxiaoTask {
	
	@Autowired
	private FenXiaoOrderService fenXiaoOrderService; 
	
	private final Logger    logger  = LoggerFactory.getLogger(FenxiaoTask.class);
	
	
	@Scheduled(cron = "0 0/5 * * * ?")
	//@Scheduled(cron = "0 0 0 * * ?")
	public void unfreeze(){
		logger.info(" \n========================== 开始执行,分销解冻定时任务 ==============================");
		List<FenxiaoOrder> list = fenXiaoOrderService.findToUnfreeze();
		String size = (CollectionUtils.isEmpty(list)? "0" : String.valueOf(list.size()));
		logger.info(" \n========================== 分销解冻查询到记录数"+size+" ==============================");
		if(CollectionUtils.isNotEmpty(list)) {
			for (FenxiaoOrder fenxiaoOrder : list) {
				fenXiaoOrderService.unFreeze(fenxiaoOrder.getId());
			}
		}
	}
}
