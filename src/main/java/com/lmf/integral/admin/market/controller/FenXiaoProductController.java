package com.lmf.integral.admin.market.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import com.lmf.common.enums.OwnerType;
import com.lmf.common.util.PagerSpec;
import com.lmf.enums.ProductStatus;
import com.lmf.market.entity.FenxiaoProduct;
import com.lmf.market.service.FenXiaoOrderService;
import com.lmf.market.service.FenXiaoProductService;
import com.lmf.market.service.FenXiaoUserService;
import com.lmf.product.entity.Brand;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.ProductCate;
import com.lmf.product.service.BrandService;
import com.lmf.product.service.ProductCateService;
import com.lmf.product.service.ProductService;
import com.lmf.product.service.ProductStorageService;
import com.lmf.product.vo.ProductCriteria;
import com.lmf.product.vo.SimpleStorageSummary;
import com.lmf.website.entity.WebsiteAdministrator;
import com.lmf.website.service.WebsiteAdministratorService;
import com.lmf.website.vo.WebsiteAdministratorCriteria;

@Controller("fenXiaoProductController")
public class FenXiaoProductController {

	@Autowired
	private FenXiaoProductService fenXiaoProductService;

	@Autowired
	private FenXiaoOrderService fenXiaoOrderService;

	@Autowired
	private FenXiaoUserService fenXiaoUserService;
	
	@Autowired
    private ProductCateService  productCateService;

	@Autowired
	private WebsiteAdministratorService websiteAdministratorService;
	
	@Autowired
    private BrandService brandService;
	
	@Autowired
    private ProductStorageService productStorageService;
    
	@Autowired
    private ProductService  productService;

	/**
	 * 新增分销商品
	 * 
	 * @param rankName
	 * @param percent
	 * @return
	 */
	@RequiresPermissions("fenxiao_product:create")
	@RequestMapping(value = "/admin/fenxiao/addProduct.php", method = RequestMethod.POST)
	public @ResponseBody ResponseResult addFenxiaoUser(
			@RequestParam(value = "productIds", required = false) String productIds,
			@RequestParam(value = "percent", required = false) Float percent) {
		if (StringUtils.isBlank(productIds)) {
			return new ResponseResult("-1", "请选择商品");
		}
		String[] ids = productIds.split(",");
		int result = 0;
		for (String productId : ids) {
			FenxiaoProduct findByProduct = fenXiaoProductService.findByProduct(Integer.parseInt(productId));
			if(findByProduct !=null){
				return new ResponseResult("-1", "新增失败,已存在商品ID为"+productId+"的分销商品");
			}
			if (percent == null || percent == 0) {
				result = result
						+ fenXiaoProductService.add(
								Integer.parseInt(productId), 100f);
			} else {
				result = result
						+ fenXiaoProductService.add(
								Integer.parseInt(productId), percent);
			}

		}
		if (result == ids.length) {
			return new ResponseResult("1", "新增成功");
		} else {
			return new ResponseResult("-1", "新增失败");
		}
	}

	/**
	 * 修改销商品分销比例
	 * 
	 * @param rankName
	 * @param percent
	 * @return
	 */
	@RequiresPermissions("fenxiao_product:edit_all")
	@RequestMapping(value = "/admin/fenxiao/editProduct.php", method = RequestMethod.POST)
	public @ResponseBody ResponseResult editOne(
			@RequestParam(value = "productIds", required = false) String productIds,
			@RequestParam(value = "percent", required = false) Float percent) {
		if (StringUtils.isBlank(productIds)) {
			return new ResponseResult("-1", "请选择商品");
		}
		String [] productIdList= productIds.split(",");
		for (String productId : productIdList) {
			fenXiaoProductService.editOne(Integer.parseInt(productId),
					percent);
		}
		
		return new ResponseResult("1", "修改成功");
			
	}
	
	/**
	 * 删除分销商品
	 * 
	 * @param rankName
	 * @param percent
	 * @return
	 */
	@RequiresPermissions("fenxiao_product:delete")
	@RequestMapping(value = "/admin/fenxiao/delProduct.php", method = RequestMethod.POST)
	public @ResponseBody ResponseResult delProduct(
			@RequestParam(value = "productIds", required = false) String productIds
			) {
		if (StringUtils.isBlank(productIds)) {
			return new ResponseResult("-1", "请选择商品");
		}
		String [] productIdList= productIds.split(",");
		for (String productId : productIdList) {
			int result = fenXiaoProductService.delProduct(Integer.parseInt(productId));
		}
		
		return new ResponseResult("1", "取消关联成功！");
			
	}
	
	@RequiresPermissions("fenxiao_product:view")
	@RequestMapping(value = "/admin/product/fenxiao_list.php", method = RequestMethod.GET)
	public String list(
			@RequestParam(value = "kw", required = false) String keyword,
			@RequestParam(value = "status", defaultValue = "selling") ProductStatus status,
			@RequestParam(value = "b", required = false) Brand brand,
			@RequestParam(value = "c", required = false) ProductCate cate,
			@RequestParam(value = "sp", required = false) Double minPrice,
			@RequestParam(value = "mp", required = false) Double maxPrice,
			@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "self", required = false) Boolean self,
			@RequestParam(value = "type", required = false) OwnerType type,
			@RequestParam(value = "enableOverseas", required = false) Boolean enableOverseas, // 是否跨境
			@RequestParam(value = "p", required = false, defaultValue = "0") int provider,
			//@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager,
			WebsiteAdministrator admin, Model model)
			throws UnsupportedEncodingException {
//		if(pageNum == null || pageNum == 0){
//			pager.setCurrentPage(1);
//		}else{
//			pager.setCurrentPage(pageNum);
//		}
		Page<WebsiteAdministrator> websiteAdministrator = websiteAdministratorService
				.find(new WebsiteAdministratorCriteria().withKeyword(keyword)
						.withProvider(true).withEnabled(true), null);

		ProductCriteria criteria = new ProductCriteria().withKeyword(keyword)
				.withBrand(brand).withCate(cate).withPrice(minPrice, maxPrice)
				.withStatus(status).withDeleted(Boolean.FALSE).withSelf(self)
				.withEnableOverseas(enableOverseas).withProviderId(provider);

		Page<Product> products = null;
			criteria.withOwnerType(type);
			products = productService.findFenxiaoProduct(criteria, pager);

		List<Brand> brands = brandService.find(null, null).getContent();

		StringBuilder link = new StringBuilder(
				"/jdvop/admin/product/fenxiao_list.php?page=[:page]");
		if (keyword != null && !keyword.isEmpty()) {
			link.append("&kw=").append(URLEncoder.encode(keyword, "UTF-8"));
		}
		if (status != null) {
			link.append("&status=").append(status.name());
			model.addAttribute("status", status.name());
		}
		if (brand != null) {
			link.append("&b=").append(brand.getId());
		}
		if (cate != null) {
			link.append("&c=").append(cate.getId());
		}

		if (self != null) {
			link.append("&self=").append(self);
			model.addAttribute("self", self);
		}

		if (type != null) {
			link.append("&type=").append(type.name());
			model.addAttribute("ownerType", type.name());
		}

		if (minPrice != null) {
			link.append("&sp=").append(minPrice);
			model.addAttribute("sp", minPrice);
		}

		if (maxPrice != null) {
			link.append("&mp=").append(maxPrice);
			model.addAttribute("mp", maxPrice);
		}
		if (provider != 0) {
			link.append("&p=").append(provider);
		}

		// 查询库存
		if (products != null) {
			if (products.hasContent()) {
				List<Integer> productIds = new ArrayList<>();
				for (Product product : products.getContent()) {
					productIds.add(product.getId());
				}
				Map<Integer, SimpleStorageSummary> storageSummary = productStorageService
						.findStorageSummaries(productIds);
				model.addAttribute("storageSummary", storageSummary);
			}
		}

		model.addAttribute("products", products);
		model.addAttribute("brands", brands);
		model.addAttribute("cate", cate);
		model.addAttribute("link", link.toString());
		model.addAttribute("from", from);
		
		model.addAttribute("productCateService", productCateService);
		model.addAttribute("productService", productService);
		model.addAttribute("ownerTypes", OwnerType.values());
		model.addAttribute("admin", admin);
		model.addAttribute("websiteAdministrator", websiteAdministrator);
		return "/admin/product/fenxiao/list";
		//return new ResponseResult("-1", "新增失败",products);
	}

	/**
	 * @return
	 * 关联产品页面映射
	 */
	@RequiresPermissions("fenxiao_product:create")
	@RequestMapping(value = "/admin/product/fenxiao_catch.php", method = RequestMethod.GET)
    public String catchFenxiaoProduct() {
		return "/admin/product/fenxiao/catch_fenxiao_product";
    }
	
//	/**
//	 * @return
//	 * 分销产品列表页面映射
//	 */
//	@RequestMapping(value = "/admin/product/fenxiao_list.php", method = RequestMethod.GET)
//    public String fenxiaoProductList() {
//    	
//		return "/admin/product/fenxiao/list";
//    }
	
	/**
	 * @return
	 * 商品分销比例页面映射
	 */
	@RequiresPermissions("fenxiao_product:edit_all")
	@RequestMapping(value = "/jdvop/admin/product/fenxiao_product_rate.php", method = RequestMethod.GET)
    public String fenxiaoProductRate() {
    	
		return "/admin/product/fenxiao/fenxiao_product_rate";
    }
	
}
