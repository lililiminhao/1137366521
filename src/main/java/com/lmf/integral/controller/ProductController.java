/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.controller;

import com.lmf.activity.entity.FlashSaleProduct;
import com.lmf.activity.enums.FlashSaleActivityStatus;
import com.lmf.activity.service.LimitExchangeService;
import com.lmf.activity.vo.FlashSaleProductCriteria;
import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.Range;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.enums.OwnerType;
import com.lmf.common.enums.ShoppingCartSourceEntryType;
import com.lmf.common.exceptions.ResourceNotFoundException;
import com.lmf.common.util.PagerSpec;
import com.lmf.common.visitlog.VisitLog;
import com.lmf.enterprise.entity.EnterpriseExclusiveProductPoolEntry;
import com.lmf.enterprise.service.EnterpriseExclusiveProductPoolEntryService;
import com.lmf.enterprise.service.EnterprisePackageVoucherService;
import com.lmf.enums.ProductStatus;
import com.lmf.order.entity.OrderEntry;
import com.lmf.order.enums.OrderEntrySourceType;
import com.lmf.product.entity.*;
import com.lmf.product.service.*;
import com.lmf.product.vo.ProductCriteria;
import com.lmf.product.vo.SimpleStorageSummary;
import com.lmf.sys.entity.GeoRegion;
import com.lmf.sys.service.GeoRegionService;
import com.lmf.system.sdk.service.SystemProductService;
import com.lmf.website.entity.UserDefinedCate;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.service.UserDefinedCateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 产品信息
 * @author zhouwei
 */
@Controller
public class ProductController {
    
    @Autowired
    private ProductCateService  productCateService;
    
    @Autowired
    private ProductDescriptionService  productDescriptionService;
    
    @Autowired
    private ProductImageService productImageService;
    
    @Autowired
    private GeoRegionService regionService;
    
    @Autowired
    private ProductStorageService productStorageService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserDefinedCateService userDefinedCateService;
    
    @Autowired
    private SystemProductService systemProductService;
    
    @Autowired
    private LimitExchangeService limitExchangeService;

    @Autowired
    private EnterpriseExclusiveProductPoolEntryService productPoolEntryService;
    
    @Autowired
    private EnterprisePackageVoucherService enterprisePackageVoucherService;
    
    
    @RequestMapping(value = "/products.php", method = RequestMethod.GET)
    public String list(@RequestParam(value = "c",  required = false) ProductCate cate,
                       @RequestParam(value = "b",  required = false) Brand brand,
                       @RequestParam(value = "k",  required = false) String keyword,
                       @RequestParam(value = "s", required = false) Double minIntegral,
                       @RequestParam(value = "m", required = false) Double maxIntegral,
                       @RequestParam(value = "wpt", required = false) Integer userDefinedCateId,
                       @PagerSpecDefaults(pageSize = 30, sort = "hot.desc,time.desc") PagerSpec pager,
                       Website  website,
                       Model model) throws UnsupportedEncodingException
    {         
        StringBuilder  link = new StringBuilder("/jdvop/products.php?page=[:page]");
        ProductCriteria criteria    = new ProductCriteria().withDeleted(Boolean.FALSE).withStatus(ProductStatus.selling);
        if(keyword != null && !keyword.isEmpty()){
            link.append("&k=").append(keyword);
            criteria.withKeyword(keyword);
        } 
        if(cate != null){
            link.append("&c=").append(cate.getId());
            criteria.withCate(cate);
        }
        if(brand != null){
            link.append("&b=").append(brand.getId());
            criteria.withBrand(brand);
        }
        
        if(userDefinedCateId != null && userDefinedCateId > 0){
            UserDefinedCate udc = userDefinedCateService.findOne(userDefinedCateId);
            criteria.withCate(udc);
            link.append("&wpt=").append(userDefinedCateId);
        }
        
        if ((minIntegral != null && minIntegral > 0)
            || (maxIntegral != null && maxIntegral > 0))
        {
            Range<Double>  price    = new Range<>();
            if(minIntegral != null && minIntegral > 0){
                link.append("&s=").append(minIntegral);
                price.setMin(minIntegral / (double) website.getRatio());
            }
            if(maxIntegral != null && maxIntegral > 0){
                link.append("&m=").append(maxIntegral);
                price.setMax(maxIntegral / (double) website.getRatio());
            }
            criteria.withPrice(price);
        }

        
        Page<Product> products =  productService.find(criteria, pager);
         
        //找到当前分类的最顶层分类
        if(cate != null){
            //该分类的最顶层父类
            List<ProductCate> cates = productCateService.parents(cate);
            model.addAttribute("topCate", cates.get(cates.size() - 1));
            model.addAttribute("parentCates", cates);
        }
        model.addAttribute("products", products);
        model.addAttribute("productLink", link.toString());
        //添加限时兑换产品到model  在页面显示 add by liuqi
        Page<FlashSaleProduct> fpPage = limitExchangeService.find(new FlashSaleProductCriteria().withFlashSaleActivityStatus(FlashSaleActivityStatus.be_doing), null);
        if(fpPage != null && fpPage.hasContent()){
            model.addAttribute("fps", fpPage.getContent());
        }
        return "product/list";
    }
    
    @RequestMapping(value = "/product/{productId}.php", method = RequestMethod.GET)
    public String view(@RequestParam(value = "fid", required = false)Long fid,
    		@RequestParam(value = "bitch", required = false)Integer bitch,
    		Model model,Website website,  WebsiteUser websiteUser, VisitLog visitLog, @PathVariable("productId") int productId, HttpServletRequest request) throws IOException
    {
        Product product = productService.findOne(productId);
        if(product == null){
            throw new ResourceNotFoundException();
        } 
        boolean isSelling = true;
        //如果零售价小于0.01或者非售卖状态
        if(product.getRetailPrice() < 0.01 || product.getStatus() != ProductStatus.selling) {
            isSelling = false; 
        }
        
        boolean isStock = true;
        if(product.getOwnerType() == OwnerType.enterprise || product.getOwnerType() == OwnerType.provider) {
            if(website.isEnabledErp()) {
                SimpleStorageSummary storageSummary = productStorageService.findStorageSummary(productId);
                if(storageSummary.getUseableNum() < 1){
                    isStock = false;
                }
            }
        } else {
            String address = "";
            GeoRegion province = null, city = null, county = null, town = null;
            //京东地址集合
            Set<GeoRegion> provinces = regionService.findAllProvince();
            if (provinces != null && provinces.size() > 0) {
                //从cookie中取用户上次使用的地址
                Cookie[] cookies = request.getCookies();
                //市民卡默认浙江 杭州 西湖区
                Integer provinceCode = 15, cityCode = 1213, countyCode = 3411;
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (null != cookie.getName()) switch (cookie.getName()) {
                            case "provinceCode":
                                provinceCode = Integer.parseInt(cookie.getValue());
                                break;
                            case "cityCode":
                                cityCode = Integer.parseInt(cookie.getValue());
                                break;
                            case "countyCode":
                                countyCode = Integer.parseInt(cookie.getValue());
                                break;
                        }
                    }
                }
                if (provinceCode != 0) {
                    province = regionService.findOne(provinceCode);
                    model.addAttribute("provinceCode", provinceCode);
                } else {
                    province = (GeoRegion) provinces.toArray()[0];
                }
                address += province.getId();
                model.addAttribute("provinces", provinces);
                List<GeoRegion> citys = regionService.childs(province);
                if (citys != null && citys.size() > 0) {
                    if (cityCode != 0) {
                        city = regionService.findOne(cityCode);
                        model.addAttribute("cityCode", cityCode);
                    } else {
                        city = citys.get(0);
                    }
                    address += "_" + city.getId();
                    model.addAttribute("citys", citys);
                    List<GeoRegion> countys = regionService.childs(city);
                    if (countys != null && countys.size() > 0) {
                        if (countyCode != 0) {
                            county = regionService.findOne(countyCode);
                            model.addAttribute("countyCode", countyCode);
                        } else {
                            county = countys.get(0);
                        }
                        address += "_" + county.getId();
                        model.addAttribute("countys", countys);
                    }
                }
            }
            if(!"".equals(address)) {
                address +=  "_0";
            }
            Map<String, Object> stockResult = systemProductService.productStock(product.getSystemProductId(), 1, address, website);
            if (stockResult != null) {
                if(stockResult.get("stock_status") != null) {
                    boolean status = (boolean) stockResult.get("stock_status");
                    if(!status) {
                        isStock = false;
                    }
                }
            } else {
                isStock = false;
            }
        }
        isStock = true;
        
        List<ProductImage> productImageList = productImageService.find(product);    //产品图片
        ProductDescription productDescription   = productDescriptionService.findOne(product.getId());   //产品详情
        
        //添加历史记录
        visitLog.addLog(product);
        visitLog.close();
        
        //如果该产品是限时兑换产品 添加限时兑换产品到model  在页面显示
        Page<FlashSaleProduct> fpPage = limitExchangeService.find(new FlashSaleProductCriteria().withFlashSaleActivityStatus(FlashSaleActivityStatus.be_doing).withProductId(productId), null);
        if(fpPage != null && fpPage.hasContent()){
            FlashSaleProduct fp = fpPage.getContent().get(0);
            limitExchangeService.setIsLimitOverToFlashProduct(fp, website);
            model.addAttribute("fp", fp);
        }
        Page<FlashSaleProduct> fpsPage = limitExchangeService.find(new FlashSaleProductCriteria().withFlashSaleActivityStatus(FlashSaleActivityStatus.be_doing), null);
        if(fpsPage != null && fpsPage.hasContent()){
            model.addAttribute("fps", fpsPage.getContent());
        }
        //根据员工和产品ID查找专享价
        EnterpriseExclusiveProductPoolEntry productPoolEntry = productPoolEntryService.selectProductPoolEntry(websiteUser, productId);
        model.addAttribute("productPoolEntry", productPoolEntry);
        model.addAttribute("product", product);
        model.addAttribute("fid", fid == null ? 0 : fid);
        model.addAttribute("productImageList", productImageList);
        model.addAttribute("productDescription", productDescription);
        
        //是否为企业专区大礼包产品,根据这个值显示立即购买
        boolean isEnterpriseZoneProduct = false;
        Integer thisProductTotalVoucher = 0;
		if (productPoolEntry != null && websiteUser != null && websiteUser.getId() > 0) {
			isEnterpriseZoneProduct = userDefinedCateService.isEnterpriseZoneProduct(productId);
			Map<Integer,String> map = userDefinedCateService.findEnterpriseCatMap(productId);
			// 根据用户查询出可兑换券的数量，返回前台，做数量限制
			for(Map.Entry<Integer, String> entry:map.entrySet()) {
				thisProductTotalVoucher += enterprisePackageVoucherService.countUseableByUserPhoneAndProductName(entry.getValue(), websiteUser);
			}
		}
        model.addAttribute("thisProductTotalVoucher", thisProductTotalVoucher);
        model.addAttribute("isEnterpriseZoneProduct", isEnterpriseZoneProduct);
        model.addAttribute("isSelling", isSelling);
        model.addAttribute("isStock", isStock);
        if(bitch != null && bitch == 1 &&fid != null && websiteUser != null){
        	if(websiteUser!=null && fid.equals(websiteUser.getId()))
        	return "fenxiao/share";
        }
        if(product.getOwnerType() == OwnerType.jingdong) {
            return "product/jd_view";
        }
        return "product/view";
    }
    
    /* 判断是否有足够的预存款 */
    @RequestMapping(value = "/product/clearBalance.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse<String> clearBalance(@RequestParam("isSelected[]") boolean[] isSelected,
                                                                @RequestParam("entry[]") int[] entryIds, 
                                                                @RequestParam("sentry[]") int[] sourceEntryIds,
                                                                @RequestParam(value = "stype[]") ShoppingCartSourceEntryType[] sourceEntryTypes,
                                                                @RequestParam(value = "amount[]") int[] amounts,
                                                                @RequestParam(value = "productOwnerType[]") OwnerType[] productOwnerType,
                                                                Website website) {
        if (entryIds == null || entryIds.length == 0) {
            return new SimpleJsonResponse(false, "您未选中任何产品！");
        }
        List<OrderEntry> orderEntries = new ArrayList<>();
        for(int i = 0; i < entryIds.length; i++){
            if(!isSelected[i]){
                continue;
            }
            OrderEntry orderEntry = new OrderEntry();
            orderEntry.setStorageUnitId(entryIds[i]);
            orderEntry.setSourceEntryType(OrderEntrySourceType.valueOf(sourceEntryTypes[i].name()));
            orderEntry.setSourceObjectId(sourceEntryIds[i]);
            orderEntry.setAmount(amounts[i]);
            orderEntries.add(orderEntry);
        }
        return new SimpleJsonResponse<>(true, null);
    }
}
