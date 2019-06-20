package com.lmf.integral.mobile.market.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.ResponseResult;
import com.lmf.common.util.PagerSpec;
import com.lmf.enums.ProductStatus;
import com.lmf.market.entity.FenxiaoOrder;
import com.lmf.market.service.FenXiaoOrderService;
import com.lmf.market.service.FenXiaoProductService;
import com.lmf.market.service.FenXiaoUserService;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.service.OrderService;
import com.lmf.product.entity.Brand;
import com.lmf.product.entity.Product;
import com.lmf.product.vo.ProductCriteria;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteUser;

@Controller("mobileFenXiaoOrderController")
public class FenXiaoOrderController {
	
	@Autowired
	private FenXiaoProductService fenXiaoProductService; 
	
	@Autowired
	private FenXiaoOrderService fenXiaoOrderService; 
	
	@Autowired
	private FenXiaoUserService fenXiaoUserService;
	
	@Autowired
	private OrderService orderService;
	
	
	/**
	 * 个人中心 我的订单
	 * @param pager
	 * @param userId
	 * @param website
	 * @param model
	 * @param websiteUser
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/mobile/fenxiao/orders.php", method = RequestMethod.GET)
	public String list(
			@PagerSpecDefaults(pageSize = 10, sort = "time.desc") PagerSpec page,
			@RequestParam(value = "userId", required = false)Integer userId,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,//第几页
			Website website, Model model, WebsiteUser websiteUser,
			HttpServletRequest request) throws UnsupportedEncodingException {
		page.setCurrentPage(pageNum);
		Page<FenxiaoOrder> orderPage = fenXiaoOrderService.findByUserId(page,userId);
		List<ShoppingOrder> orderList = new ArrayList<ShoppingOrder>();
		List<FenxiaoOrder> fenxiaoList = orderPage.getContent();
		for (FenxiaoOrder fenxiaoOrder : fenxiaoList) {
			ShoppingOrder order = orderService.findOne(fenxiaoOrder.getOrderId());
			order.setFenxiaoMoney(fenxiaoOrder.getUserMoney());
			orderList.add(order);
		}
		model.addAttribute("orderList", orderList);
 		return "/mobile/fenxiao/order";
	}
	
}
