/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.activity.controller;

import com.lmf.activity.entity.FlashSaleProduct;
import com.lmf.activity.enums.FlashSaleActivityStatus;
import com.lmf.activity.service.LimitExchangeService;
import com.lmf.activity.vo.FlashSaleProductCriteria;
import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.enums.OwnerType;
import com.lmf.common.util.PagerSpec;
import com.lmf.enums.ProductStatus;
import com.lmf.product.entity.Brand;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.ProductCate;
import com.lmf.product.service.BrandService;
import com.lmf.product.service.ProductCateService;
import com.lmf.product.service.ProductService;
import com.lmf.product.vo.ProductCriteria;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 限时兑换业务后台管理
 * @author liuqi
 */
@Controller("adminLimitExchangeController")
public class LimitExchangeController {
    
    @Autowired
    private ProductService productService;
        
    @Autowired
    private BrandService brandService;
    
    @Autowired
    private ProductCateService productCateService;
    
    @Autowired
    private LimitExchangeService limitExchangeService;
    
    /* 限时活动产品列表 */
    @RequestMapping(value = "/admin/activity/limitExchange/list.php", method = RequestMethod.GET)
    public String page(@PagerSpecDefaults(pageSize = 20, sort = "end_time.desc") PagerSpec pager,
            @RequestParam(value = "status", defaultValue = "be_doing") FlashSaleActivityStatus status,
            @RequestParam(value = "c", required = false) ProductCate cate,
            @RequestParam(value = "kw", required = false) String keyword,
            @RequestParam(value = "type", required = false) OwnerType type,
            Model model){
        StringBuilder link = new StringBuilder("/jdvop/admin/activity/limitExchange/list.php?page=[:page]");
        if(keyword != null && !keyword.isEmpty()){
            link.append("&kw=").append(keyword);
        }
        if(status != null){
            link.append("&status=").append(status.name());
        }
        if(type != null){
            link.append("&type=").append(type.name());
        }
        if(cate != null){
            link.append("&c=").append(cate.getId());
        }
        
        model.addAttribute("link", link.toString());
        model.addAttribute("status", status);
        model.addAttribute("cate", cate);
        model.addAttribute("kw", keyword);
        model.addAttribute("type", type);
        model.addAttribute("productCateService", productCateService);
        FlashSaleProductCriteria criteria = new FlashSaleProductCriteria().withKeyword(keyword).withProductCate(cate).withFlashSaleActivityStatus(status).withOwnerType(type);
        Page<FlashSaleProduct> page = limitExchangeService.find(criteria, pager);
        model.addAttribute("page", page);
        return "/admin/activity/limit_exchange/list";
    }
    
    /* 触发 《添加产品》 按钮 */
    @RequestMapping(value = "/admin/activity/limitExchange/addProduct.php", method = RequestMethod.GET)
    public String chooseProducts(@RequestParam(value = "maxSize", defaultValue = "20") int maxSize,
                                 @RequestParam(value = "minSize", defaultValue = "1") int minSize,
                                 @PagerSpecDefaults(pageSize = 10, sort = "time.desc") PagerSpec pager,
                                 Model model){
        String link = "/jdvop/admin/website/block/loadProducts.php?page=[:page]";
        Page<Product> products = productService.find(new ProductCriteria().withStatus(ProductStatus.selling).withDeleted(Boolean.FALSE), pager);
        Page<Brand> brands = brandService.findProductBrands(null, ProductStatus.selling, null, null);
        model.addAttribute("cates", productCateService.findOne(0).getChilds());
        model.addAttribute("brands", brands);
        model.addAttribute("products", products);
        model.addAttribute("link", link);
        model.addAttribute("maxSize", maxSize);
        model.addAttribute("minSize", minSize);
        model.addAttribute("ownerTypes", OwnerType.values());
        return "admin/activity/limit_exchange/choose_product";
    }
    
    /**
     * 选好产品后 跳转到修改折扣价格 页面
     * @param pids
     * @param model
     * @return 
     */
    @RequestMapping(value = "/admin/activity/limitExchange/modifyProductView.php", method = RequestMethod.GET)
    public String modifyProductView(@RequestParam(value = "pids[]") Integer[] pids, Model model){
        List<Product> selectedProducts = productService.find(Arrays.asList(pids));
        model.addAttribute("selectedProducts", selectedProducts);
        return "admin/activity/limit_exchange/modify_view";
    }
    
    /**
     * 修改折扣价格页面 保存触发
     * @param products
     * @return 
     */
    @RequestMapping(value = "/admin/activity/limitExchange/createProducts.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse saveProducts(@RequestBody List<FlashSaleProduct> products){
        //判断 是否 与进行中和未开始的产品 有重复的产品， 如果有删去产品
        limitExchangeService.create(products);
        return new SimpleJsonResponse(true, "");
    }
    
    /* 修改单个产品 */
    @RequestMapping(value = "/admin/activity/limitExchange/saveProduct.php", method = RequestMethod.POST , produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse saveProduct(@RequestParam("pid") Long pid,
                                @RequestParam("discountPrice") Double discountPrice,
                                @RequestParam("originalPrice") Double originalPrice,
                                @RequestParam("limitNum") Integer limitNum,
                                @RequestParam("buyNumPerPerson") Integer buyNumPerPerson,
                                @RequestParam("startTime") Date startTime,
                                @RequestParam("endTime") Date endTime){
        FlashSaleProduct product = new FlashSaleProduct();
        product.setId(pid);
        product.setDisplayDiscountPrice(discountPrice);
        product.setDisplayOriginalPrice(originalPrice);
        product.setDiscountRate(discountPrice/originalPrice);
        product.setLimitNum(limitNum);
        product.setBuyNumPerPerson(buyNumPerPerson);
        product.setStartTime(new Timestamp(startTime.getTime()));
        product.setEndTime(new Timestamp(endTime.getTime()));
        limitExchangeService.save(product);
        return new SimpleJsonResponse(true, "");
    }
    
    /**
     * 删去单个产品
     * @param pid
     * @return 
     */
    @RequestMapping(value = "/admin/activity/limitExchange/deleteProduct.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse deleteProduct(@RequestParam(value = "id") Integer pid){
        limitExchangeService.delete(pid);
        return new SimpleJsonResponse(true, "");
    }
    
    /**
     * 删去多个产品
     * @param pids
     * @return 
     */
    @RequestMapping(value = "/admin/activity/limitExchange/shelvesOff.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse shelvesOffProduct(@RequestParam(value = "ids[]") Integer[] pids){
        limitExchangeService.delete(pids);
        return new SimpleJsonResponse(true, "");
    }
    
    
    @RequestMapping(value = "/admin/activity/limitExchange/checkRepeatProducts.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse checkRepeatProducts(@RequestBody List<FlashSaleProduct> products){
        boolean hasRepeat = limitExchangeService.checkRepeatProductWhenCreate(products);
        if(hasRepeat){
            return new SimpleJsonResponse(false, "");
        }else{
            return new SimpleJsonResponse(true, "");
        }
    }
    
}
