package com.lmf.integral.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.lmf.integral.service.WebsiteProxyService;
import com.lmf.order.entity.OrderOperationLog;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.repository.ShoppingOrderDao;
import com.lmf.order.service.OrderOperationLogService;
import com.lmf.order.service.OrderService;
import com.lmf.system.sdk.service.SystemOrderService;
import com.lmf.system.sdk.service.impl.SystemOrderServiceImpl;
import com.lmf.system.sdk.vo.OrderCreateResult;
import com.lmf.website.entity.Website;

/**
* Title: PushOrderTask
* TODO:  临时推送特定订单到云中鹤
* @author qi  
* @date 2019年1月10日
 */
public class PushOrderTask {
    
	private final Logger logger = LoggerFactory.getLogger(SystemOrderServiceImpl.class);
	
	@Autowired
    private WebsiteProxyService websiteProxyService;
	@Autowired
    private OrderService orderService;
	@Autowired
	private SystemOrderService systemOrderService;
	@Autowired
    private OrderOperationLogService orderOperationLogService;
	@Autowired
    private ShoppingOrderDao shoppingOrderDao;
	
    //@Scheduled(cron = "0 0/10 * * * ?")
    public void pushOrderToYZH(){
    	
    	logger.info("***********************开始推送订单给云中鹤***********************");
    	
    	//待推送订单key集合
    	List<String> list = getOrderKeyList();
    	
    	//正式环境
        Website website = websiteProxyService.getWebsite();
        logger.info("临时推单到云中鹤，获取website信息："+website.getToken()+"__"+website.getId());
        //测试环境
//    	Website website = new Website();
//    	website.setId(1087);
//    	website.setToken("VQGkjKyGjga8UnfvMtVgZrNsehHeyw");
        
    	if(list!=null && list.size()>0) 
    	{
    		logger.info("临时推单到云中鹤，获取订单个数："+list.size());
    		for (int i = 0; i < list.size(); i++) {
//        		ShoppingOrder order = orderService.findOne("533898930");
    			ShoppingOrder order = orderService.findOne(list.get(i));
    			if(order==null) 
    			{
    				continue;
    			}
    			logger.info("临时推单到云中鹤，获取到订单信息，订单key："+order.getKey()+"，订单系统信息"+order.getSystemOrderInfo());
    			if(order.getSystemOrderInfo()!=null) 
    			{
    				continue;
    			}
            	try {
        			OrderCreateResult result = systemOrderService.createOrder(website, order);
        			logger.info("临时推单到云中鹤，反馈："+result+"，订单key："+order.getKey());
        			if(result != null) {
        				logger.info("**************************云中鹤*****************************"+result.getOrderKey());
                        //设置系统订单信息
                        Map<String, Object> info = new HashMap();
                        info.put("order_key", result.getOrderKey());
                        info.put("order_total_price", result.getTotalPrice());
                        info.put("order_product_price", result.getProductPrice());
                        info.put("order_shipment_fee", result.getShipmentFee());

                        shoppingOrderDao.setSystemOrderInfo(order.getId(), info);
                        logger.info("临时推单到云中鹤，设置系统订单信息成功，订单key："+order.getKey());

                        //记录订单操作日志
                        OrderOperationLog   log = new OrderOperationLog(); 
                        log.setOrderId(order.getId());
                        Map<String, Object> ext = new HashMap<>();
                        ext.put("workerID", "-1");
                        ext.put("workerName", "系统自动处理");
                        ext.put("operateIP", "127.0.0.1");
                        ext.put("operation", "系统商品， 由系统自动发货。");
                        log.setExt(ext);
                        orderOperationLogService.create(log);
                        logger.info("临时推单到云中鹤，记录订单操作日志成功，订单key："+order.getKey());
                    } else {
                    	logger.info("The create SystemOrder failed, order creation failed.");
                    }
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}

			}
    	}
    	
    	logger.info("=======临时推单结束=======");
    }
    
    private List<String> getOrderKeyList()
    {
    	List<String> list = new ArrayList<>();
    	/*list.add("4093011673");*/
    	list.add("4093213071");
    	list.add("4102188349");
    	return list;
    }
    
}
