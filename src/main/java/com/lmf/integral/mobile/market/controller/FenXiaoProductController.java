package com.lmf.integral.mobile.market.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPObject;
import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.Range;
import com.lmf.common.ResponseResult;
import com.lmf.common.util.PagerSpec;
import com.lmf.enterprise.entity.EnterpriseExclusiveProductPoolEntry;
import com.lmf.enterprise.service.EnterpriseExclusiveProductPoolEntryService;
import com.lmf.enums.ProductStatus;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.market.entity.Coupon;
import com.lmf.market.entity.VisitLog;
import com.lmf.market.service.FenXiaoOrderService;
import com.lmf.market.service.FenXiaoProductService;
import com.lmf.market.service.FenXiaoUserService;
import com.lmf.product.entity.Brand;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.ProductCate;
import com.lmf.product.service.BrandService;
import com.lmf.product.vo.ProductCriteria;
import com.lmf.website.entity.UserDefinedCate;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteUser;

@Controller("mobileFenXiaoProductController")
public class FenXiaoProductController {

	@Autowired
	private FenXiaoProductService fenXiaoProductService;

	@Autowired
	private FenXiaoOrderService fenXiaoOrderService;

	@Autowired
	private FenXiaoUserService fenXiaoUserService;

	@Autowired
	private BrandService brandService;

	@Autowired
	private EnterpriseExclusiveProductPoolEntryService enterpriseExclusiveProductPoolEntryService;

//	/**
//	 * 个人中心 我的商品
//	 * @param pager
//	 * @param userId
//	 * @param website
//	 * @param model
//	 * @param websiteUser
//	 * @param request
//	 * @return
//	 * @throws UnsupportedEncodingException
//	 */
//	@RequestMapping(value = "/mobile/fenxiao/products.php", method = RequestMethod.GET)
//	public @ResponseBody ResponseResult list(
//			@PagerSpecDefaults(pageSize = 99999, sort = "time.desc") PagerSpec pager,
//			@RequestParam(value = "userId", required = false)Integer userId,
//			@RequestParam(value = "pageNum", required = false) Integer pageNum,//第几页
//			Website website, Model model, WebsiteUser websiteUser,
//			HttpServletRequest request) throws UnsupportedEncodingException {
//		pager.setCurrentPage(pageNum);
//		ProductCriteria criteria = new ProductCriteria().withDeleted(
//				Boolean.FALSE).withStatus(ProductStatus.selling);
//		Page<Product> productPage = fenXiaoProductService.find(criteria, pager,userId);
//		List<Brand> brands = brandService.find(null, null)
//				.getContent();
//		return new ResponseResult("1", "",productPage);
//	}
	
	/**
	 * 个人中心 我的商品
	 * @param pager
	 * @param userId
	 * @param website
	 * @param model
	 * @param websiteUser
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/mobile/fenxiao/products.php", method = RequestMethod.GET)
	@ResponseBody
	public com.fasterxml.jackson.databind.util.JSONPObject list(
			@PagerSpecDefaults(pageSize = 99999, sort = "time.desc") PagerSpec pager,
			@RequestParam(value = "userId", required = false)Integer userId,
			@RequestParam(value = "callback", required = false)String callback,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,//第几页
			Website website, Model model, WebsiteUser websiteUser,
			HttpServletRequest request) throws UnsupportedEncodingException {
		pager.setCurrentPage(pageNum);
		ProductCriteria criteria = new ProductCriteria().withDeleted(
				Boolean.FALSE).withStatus(ProductStatus.selling);
		Page<Product> productPage = fenXiaoProductService.find(criteria, pager,userId);

		String jsonString = JSON.toJSONString(productPage);
		return new com.fasterxml.jackson.databind.util.JSONPObject(callback,jsonString);
	}
	
	
	/**
	 *  我的商品
	 * @param pager
	 * @param userId
	 * @param website
	 * @param model
	 * @param websiteUser
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/mobile/fenxiao/myProducts.php", method = RequestMethod.GET)
	public String list1(
			@PagerSpecDefaults(pageSize = 10000, sort = "time.desc") PagerSpec pager,
			@RequestParam(value = "fenxiaoAndUserId", required = false)Integer userId,
			@RequestParam(value = "bitch", required = false)Integer bitch,
			Website website, Model model, WebsiteUser websiteUser,
			HttpServletRequest request) throws UnsupportedEncodingException {
		pager.setCurrentPage(1);
		ProductCriteria criteria = new ProductCriteria().withDeleted(
				Boolean.FALSE).withStatus(ProductStatus.selling);
		Page<Product> products = fenXiaoProductService.find(criteria, pager,userId);
//		List<Brand> brands = brandService.find(null, null)
//				.getContent();
		  model.addAttribute("products", products);
		  model.addAttribute("fenxiaoAndUserId", userId);
		  if(bitch != null && bitch == 1 && userId != null){
	        	if(websiteUser!=null&&Long.valueOf(userId).intValue()==websiteUser.getId().intValue())
	        	model.addAttribute("slib", 1);
	        }
		return "product/fenxiao_list";
	}
}
