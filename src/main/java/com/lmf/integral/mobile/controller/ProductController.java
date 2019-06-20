/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.mobile.controller;

import com.lmf.activity.entity.FlashSaleProduct;
import com.lmf.activity.enums.FlashSaleActivityStatus;
import com.lmf.activity.service.LimitExchangeService;
import com.lmf.activity.vo.FlashSaleProductCriteria;
import com.lmf.common.Page;
import com.lmf.common.PageImpl;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.Range;
import com.lmf.common.util.PagerSpec;
import com.lmf.enterprise.entity.EnterpriseExclusiveProductPoolEntry;
import com.lmf.enterprise.service.EnterpriseExclusiveProductPoolEntryService;
import com.lmf.enums.ProductStatus;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.market.entity.Coupon;
import com.lmf.market.entity.VisitLog;
import com.lmf.market.service.CouponService;
import com.lmf.market.service.VisitLogService;
import com.lmf.order.service.OrderStaticsService;
import com.lmf.order.vo.OrderSeparationEntry;
import com.lmf.product.entity.Brand;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.ProductCate;
import com.lmf.product.service.BrandService;
import com.lmf.product.service.ProductCateService;
import com.lmf.product.service.ProductService;
import com.lmf.product.vo.ProductCriteria;
import com.lmf.website.entity.UserDefinedCate;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.service.UserDefinedCateService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 *
 * @author shenzhixiong
 */
@Controller("mobileProductController")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private ProductCateService productCateService; 
    
    @Autowired
    private BrandService brandService;
    
    @Autowired
    private UserDefinedCateService userDefinedCateService;
    
    @Autowired
    private OrderStaticsService orderStaticsService;
    
    @Autowired
    private LimitExchangeService limitExchangeService;
    
    @Autowired
    private CouponService couponService;
    

    @Autowired
    private VisitLogService visitLogService;

    @Autowired
    private EnterpriseExclusiveProductPoolEntryService enterpriseExclusiveProductPoolEntryService;
        
    @RequestMapping(value = "/mobile/products.php", method = RequestMethod.GET)
    public String list(@RequestParam(value = "c", required = false) ProductCate cate,
                       @RequestParam(value = "b", required = false) Brand brand,
                       @RequestParam(value = "providerId", required = false) Integer providerId,
                       @RequestParam(value = "k", required = false) String keyword,
                       @RequestParam(value = "s", required = false) Double minIntegral,
                       @RequestParam(value = "m", required = false) Double maxIntegral,
                       @RequestParam(value = "wpt", required = false) Integer userDefinedCateId,
                       @RequestParam(value = "layout", defaultValue = "list") String layout,
                       @RequestParam(value = "eppId", required = false) Integer productPoolId,
                       @PagerSpecDefaults(pageSize = 10, sort = "time.desc") PagerSpec pager,
                       Website website, Model model, WebsiteUser websiteUser,HttpServletRequest request) throws UnsupportedEncodingException
    {
        ProductCriteria criteria    = new ProductCriteria().withDeleted(Boolean.FALSE).withStatus(ProductStatus.selling);
        if(keyword != null && !keyword.isEmpty()){
            criteria.withKeyword(keyword);
        }
        if(productPoolId != null && productPoolId != 0){
       	 criteria.withProductPoolId(productPoolId);
        }
        if(cate != null){
            criteria.withCate(cate);
            model.addAttribute("c", cate);
        }
        if(userDefinedCateId != null && userDefinedCateId > 0){
            UserDefinedCate udc = userDefinedCateService.findOne(userDefinedCateId);
            criteria.withCate(udc);
        }
        if(brand != null){
            criteria.withBrand(brand);
            model.addAttribute("b", brand);
        }
        //优惠券中去使用接口传参
        if(providerId != null){
            criteria.withProviderId(providerId);
            model.addAttribute("providerId", providerId);
        }
        if ((minIntegral != null && minIntegral >= 0) || (maxIntegral != null && maxIntegral >= 0))
        {
            Range<Double>  price    = new Range<>();
            if (website.getRatio() != null && website.getRatio() > 0)
            {
                if(minIntegral != null && minIntegral >= 0){
                    price.setMin(minIntegral / (double) website.getRatio());
                    model.addAttribute("s", minIntegral);
                }
                if(maxIntegral != null && maxIntegral >= 0){
                    price.setMax(maxIntegral / (double) website.getRatio());
                    model.addAttribute("m", maxIntegral);
                }
            }
            criteria.withPrice(price);
        }
        
        Page<Product> productPage = null;
		if (criteria.userCate != null && criteria.productPoolId != null) {
			ProductCriteria criteriaTemp = new ProductCriteria().withDeleted(Boolean.FALSE)
					.withStatus(ProductStatus.selling);
			criteriaTemp.withCate(criteria.userCate);
			pager.setPerPageNum(1000);
			pager.setCurrentPage(1);
			pager.setTotalPage(1);
			Page<Product> productPage0 = productService.find(criteriaTemp, pager);
			criteriaTemp.userCate = null;
			criteriaTemp.withProductPoolId(criteria.productPoolId);
			Page<Product> productPage1 = productService.find(criteriaTemp, pager);
			Set<Product> set = new HashSet<Product>();
			if (productPage0.hasContent() && productPage1.hasContent()) {
				for (Product product : productPage1.getContent()) {
					if (productPage0.getContent().contains(product)) {
						set.add(product);
					}
				}
			}
			productPage = new PageImpl<Product>(new ArrayList<Product>(set), productPage0.getPagerSpec());
		} else {
			productPage = productService.find(criteria, pager);
		}
        List<Brand>   brands   = brandService.find(null, null).getContent();
		if(productPage.hasContent()) {
        	List<Integer> productIds = new ArrayList<>();
        	for(Product product : productPage.getContent()) {
        		productIds.add(product.getId());
        	}
	        Map<Integer, EnterpriseExclusiveProductPoolEntry> exclusiveProductMap = enterpriseExclusiveProductPoolEntryService.selectProductPoolEntryMap(websiteUser, productIds);
	        model.addAttribute("exclusiveProductMap", exclusiveProductMap);
        }
        
        HttpSession session = request.getSession();
        UserDetail currentUser = (UserDetail) session.getAttribute("currentUser");
        if(currentUser != null){
        	
        	List<VisitLog>  vl  = visitLogService.selectVisiLog((long)currentUser.getUserId(), 2, userDefinedCateId);
        	if(CollectionUtils.isEmpty(vl)){
                List<Coupon> coupons = couponService.findByTarget(2, userDefinedCateId);
                model.addAttribute("coupons",coupons);
                visitLogService.createVisiLog((long)currentUser.getUserId(), 2, userDefinedCateId);
        	}else {
        		model.addAttribute("coupons",null);
			}
        }else {
        	List<Coupon> coupons = couponService.findByTarget(2, userDefinedCateId);
            model.addAttribute("coupons",coupons);
		}
        
        model.addAttribute("layout", layout);
        model.addAttribute("products", productPage);
        model.addAttribute("brands", brands);
        return "product/list";
    }
    
    @RequestMapping(value = "/mobile/products.php", method = RequestMethod.GET, params = "ajax", produces = "text/html;charset=UTF-8")
    public @ResponseBody Page<Product>  ajaxList(@RequestParam(value = "c", required = false) ProductCate cate,
                                                @RequestParam(value = "b", required = false) Brand brand,
                                                @RequestParam(value = "k", required = false) String keyword,
                                                @RequestParam(value = "s", required = false) Double minIntegral,
                                                @RequestParam(value = "providerId", required = false) Integer providerId,
                                                @RequestParam(value = "m", required = false) Double maxIntegral,
                                                @RequestParam(value = "eppId", required = false) Integer productPoolId,
                                                @RequestParam(value = "wpt", required = false) Integer userDefinedCateId,
                                                @PagerSpecDefaults(pageSize = 10, sort = "time.desc") PagerSpec pager,
                                                Website  website, WebsiteUser websiteUser)
    {

        ProductCriteria criteria    = new ProductCriteria().withDeleted(Boolean.FALSE).withStatus(ProductStatus.selling);
        if(keyword != null && !keyword.isEmpty()){
            criteria.withKeyword(keyword);
        }
        if(cate != null){
            criteria.withCate(cate);
        }
        if(productPoolId != null && productPoolId != 0){
          	 criteria.withProductPoolId(productPoolId);
           }
        if(userDefinedCateId != null && userDefinedCateId > 0){
            UserDefinedCate udc = userDefinedCateService.findOne(userDefinedCateId);
            criteria.withCate(udc);
        }
        if(providerId != null){
            criteria.withProviderId(providerId);
        }
        if(brand != null){
            criteria.withBrand(brand);
        }
        if ((minIntegral != null && minIntegral >= 0) || (maxIntegral != null && maxIntegral > 0))
        {
            Range<Double>  price    = new Range<>();
            if (website.getRatio() != null && website.getRatio() > 0) {
                if(minIntegral != null && minIntegral >= 0){
                    price.setMin(minIntegral / (double) website.getRatio());
                }
                if(maxIntegral != null && maxIntegral >= 0){
                    price.setMax(maxIntegral / (double) website.getRatio());
                }
            }
            criteria.withPrice(price);
        }
        Page<Product> productPage = null;
		if (criteria.userCate != null && criteria.productPoolId != null) {
//			ProductCriteria criteriaTemp = new ProductCriteria().withDeleted(Boolean.FALSE)
//					.withStatus(ProductStatus.selling);
//			criteriaTemp.withCate(criteria.userCate);
//			pager.setPerPageNum(1000);
//			pager.setCurrentPage(1);
//			pager.setTotalPage(1);
//			Page<Product> productPage0 = productService.find(criteriaTemp, pager);
//			criteriaTemp.userCate = null;
//			criteriaTemp.withProductPoolId(criteria.productPoolId);
//			Page<Product> productPage1 = productService.find(criteriaTemp, pager);
//			Set<Product> set = new HashSet<Product>();
//			if (productPage0.hasContent() && productPage1.hasContent()) {
//				for (Product product : productPage1.getContent()) {
//					if (productPage0.getContent().contains(product)) {
//						set.add(product);
//					}
//				}
//			}
			productPage = new PageImpl<Product>(new ArrayList<Product>(), pager);
		} else {
			productPage = productService.find(criteria, pager);
		}
        //Page<Product> productPage = productService.find(criteria, pager);
        //企业专享价
        if(productPage!=null&&productPage.hasContent()) {
        	List<Integer> productIds = new ArrayList<>();
        	for(Product product : productPage.getContent()) {
        		productIds.add(product.getId());
        	}
	        Map<Integer, EnterpriseExclusiveProductPoolEntry> exclusiveProductMap = enterpriseExclusiveProductPoolEntryService.selectProductPoolEntryMap(websiteUser, productIds);
	        if(exclusiveProductMap != null && !exclusiveProductMap.isEmpty()) {
		        for (Integer productId : exclusiveProductMap.keySet()) {
		            for (Product product : productPage) {
		                if(product == null) {
		                    continue;
		                }
		                if(productId.equals(product.getId())){
		                    product.setRetailPrice(exclusiveProductMap.get(productId).getExclusivePrice());
		                }
		            }
		        }
	        }
        }
        return productPage;
    }
    
    //商品分类
    @RequestMapping(value = "/mobile/product/category.php", method = RequestMethod.GET)
    public  String  classification(Model model)
    {
        model.addAttribute("rootCates", productCateService.rootCates());
        return "product/category";
    }
    
    @RequestMapping(value = "/mobile/category/sysCates.php", method = RequestMethod.GET)
    public @ResponseBody List<ProductCate> sysCates(@RequestParam(value = "cateId") int id, Website website)
    {
        List<ProductCate> result = new ArrayList<>();
        ProductCate parentCate = productCateService.findOne(id);
        List<ProductCate> childCates = productCateService.allChilds(parentCate);
        for(ProductCate cate : childCates) {
            result.add(cate);
        }
        return result;
    }
    
    @RequestMapping(value = "/mobile/category/definedCates.php", method = RequestMethod.GET)
    public @ResponseBody List<UserDefinedCate> definedCates(@RequestParam(value = "cateId") int id, Website website)
    {
        List<UserDefinedCate> result = new ArrayList<>();
        List<UserDefinedCate> childCates = userDefinedCateService.findChilds(id, true);
        for(UserDefinedCate cate : childCates) {
            result.add(cate);
        }
        return result;
    }
    
    @RequestMapping(value = "/mobile/exchange/list.php", method = RequestMethod.GET)
    public String list(@PagerSpecDefaults(pageSize = 15, sort = "amount.desc") PagerSpec pager,
                       Website website, Model model) {
        Page<OrderSeparationEntry> orderStatics = orderStaticsService.findOrderEntryStaticsWithCreateTime(null, null, null, null, null, pager);
        List<Product> pdList = new ArrayList<>();
        for(OrderSeparationEntry entry: orderStatics) {
            Product bp = productService.findOne(entry.getProductId());
            if(bp != null && bp.getStatus() == ProductStatus.selling) {
                pdList.add(bp);
            }
        }

        model.addAttribute("pdList", pdList);
        //添加限时兑换产品到model  在页面显示 add by liuqi
        Page<FlashSaleProduct> fpPage = limitExchangeService.find(new FlashSaleProductCriteria().withFlashSaleActivityStatus(FlashSaleActivityStatus.be_doing), null);
        if(fpPage != null && fpPage.hasContent()){
            model.addAttribute("fps", fpPage.getContent());
        }
        return "product/exchange_list";
    }
}
