package com.lmf.integral.admin.market.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lmf.common.ResponseResult;
import com.lmf.market.entity.FenxiaoOrder;
import com.lmf.market.service.FenXiaoOrderService;
import com.lmf.market.service.FenXiaoProductService;
import com.lmf.market.service.FenXiaoUserService;

@Controller("fenXiaoOrderController")
public class FenXiaoOrderController {

	@Autowired
	private FenXiaoProductService fenXiaoProductService; 
	
	@Autowired
	private FenXiaoOrderService fenXiaoOrderService; 
	
	@Autowired
	private FenXiaoUserService fenXiaoUserService;
	
//	@RequestMapping(value = "/mobile/fenxiao/123.php", method = RequestMethod.GET)
//	public @ResponseBody ResponseResult name() {
//		List<FenxiaoOrder> findToUnfreeze = fenXiaoOrderService.findToUnfreeze();
//		return new ResponseResult("-1", "请选择商品",findToUnfreeze);
//	}
//	
//	@RequestMapping(value = "/mobile/fenxiao/1234.php", method = RequestMethod.GET)
//	public @ResponseBody ResponseResult name2() {
//		fenXiaoOrderService.changeStatus("2161585900");
//		return new ResponseResult("-1", "请选择商品");
//	}
	
}
