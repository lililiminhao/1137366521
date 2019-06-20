/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.velocity;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.InvalidScope;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.lmf.activity.entity.FlashSaleProduct;
import com.lmf.activity.service.LimitExchangeService;
import com.lmf.common.Page;
import com.lmf.common.Range;
import com.lmf.common.util.PagerSpec;
import com.lmf.enterprise.entity.EnterpriseExclusiveProductPoolEntry;
import com.lmf.enterprise.entity.EnterpriseUserMap;
import com.lmf.enterprise.service.EnterpriseExclusiveProductPoolEntryService;
import com.lmf.enterprise.service.EnterpriseUserMapService;
import com.lmf.enums.ProductStatus;
import com.lmf.integral.SystemConfig;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.product.entity.Brand;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.ProductCate;
import com.lmf.product.entity.ProductImage;
import com.lmf.product.entity.StorageUnit;
import com.lmf.product.service.ProductCateService;
import com.lmf.product.service.ProductService;
import com.lmf.product.service.ProductStorageService;
import com.lmf.product.service.StorageUnitService;
import com.lmf.product.vo.ProductCriteria;
import com.lmf.product.vo.SimpleStorageSummary;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteUser;

/**
 *
 * @author zhangxinling
 */
@DefaultKey("pd")
@InvalidScope({Scope.REQUEST, Scope.SESSION})
public class ProductTool{
    
    private ChainedContext  cctx;
    
    private ProductService  productService;
    
    private ProductCateService productCateService;
    
    private StorageUnitService  storageUnitService;
    
    private ProductStorageService productStorageService;
    
    private Website website;
    
    private SystemConfig systemConfig;
    
    private LimitExchangeService limitExchangeService;
    
    private EnterpriseUserMapService enterpriseUserMapService;
    
    private EnterpriseExclusiveProductPoolEntryService enterpriseExclusiveProductPoolEntryService;
    
    public  void init(Object ctx)
    {
        cctx    = (ChainedContext) ctx;
        
        WebApplicationContext  wctx = WebApplicationContextUtils.getWebApplicationContext(cctx.getServletContext());
        
        productService          = wctx.getBean(ProductService.class);
        storageUnitService          = wctx.getBean(StorageUnitService.class);
        productStorageService       = wctx.getBean(ProductStorageService.class);
        website                     = getWebsite();
        systemConfig                = wctx.getBean(SystemConfig.class);
        limitExchangeService        = wctx.getBean(LimitExchangeService.class);
        enterpriseUserMapService 	= wctx.getBean(EnterpriseUserMapService.class);
        enterpriseExclusiveProductPoolEntryService 	= wctx.getBean(EnterpriseExclusiveProductPoolEntryService.class);
    }
   
    public final ProductService  getProductService()
    {
        return productService;
    }
    
    public final  Website getWebsite()
    {
        if (website == null)
        {
            website = (Website) cctx.getRequest().getAttribute("website");
        }
        return  website;
    }

    public SystemConfig getSystemConfig() {
        return systemConfig;
    }
    
    public  Product asProduct(int id)
    {
        return productService.findOne(id);
    }
    
    public  Product asProductByExclusiveProductPool(int productId, int userId)
    {
    	Product product = productService.findOne(productId);
    	if(userId > 0) {
	    	EnterpriseUserMap userMap = enterpriseUserMapService.getOneByUserId(userId);
	    	if(userMap != null && userMap.getProductPoolId() != null && userMap.getProductPoolId() > 0) {
	    		EnterpriseExclusiveProductPoolEntry entry = enterpriseExclusiveProductPoolEntryService.selectOne(userMap.getProductPoolId(), productId);
	    		if(entry != null) {
	    			product.setRetailPrice(entry.getExclusivePrice());
	    		}
	    	}
    	}
        return product;
    }
    
    public  List<ProductImage> getImages(Product product)
    {
        return productService.findProductImages(product);
    }
    
    public  List<ProductImage> getImages(int id)
    {
        return getImages(asProduct(id));
    }
    
    public List<Product> findByCate(int cateId, int count){
        ProductCate cate = productCateService.findOne(cateId);
        if(cate != null){
            List<Product> bpList = null;
            PagerSpec pager = new PagerSpec(1, 0, count,new PagerSpec.Order[]{new PagerSpec.Order("time", PagerSpec.DRECTION.desc)});
            bpList = productService.find(new ProductCriteria().withCate(cate).withStatus(ProductStatus.selling), pager).getContent();
            return bpList;
        }
        return null;
    }
    
    public  Map<Integer, Product>  findAsMap(List<Integer> ids)
    {
        Map<Integer, Product> mp = new HashMap<>();
        for (Map.Entry<Integer, Product> entry : productService.findAsMap(ids).entrySet())
        {
            mp.put(entry.getKey(), entry.getValue());
        }
        return mp;
    }
    
    public List<Product> findByIds(List<Integer> ids)
    {
        if(ids != null && !ids.isEmpty()){
            return  productService.find(ids);
        }
        return null;
    }
    
    public Product  getByStorageUnitId(int StorageUnitId){
        
        StorageUnit storageUnit =  storageUnitService.findOne(StorageUnitId);
        if(storageUnit != null){
            return productService.findOne(storageUnit.getProductId());
        }
        return null;
    }
    
    public List<StorageUnit> getStorageUnits(int productId){
        return productService.findStorageUnits((Product)asProduct(productId));
    }
    
    public List<Product> getEqualBrandProducts(Brand brand)
    {
        PagerSpec pager = new PagerSpec(1, 0, 5, new PagerSpec.Order[]{new PagerSpec.Order("soldNum", PagerSpec.DRECTION.desc)});
        Page<Product> productList = productService.find(new ProductCriteria().withBrand(brand).withStatus(ProductStatus.selling), pager);
        if(productList != null) {
            return productList.getContent();
        } else {
            return null;
        }
    }
    
    public List<Product> getTwoProducts(double price, Product product)
    {
        Range<Double> r = new Range<>(price * (1 - 0.25), price * (1 + 0.25));
        PagerSpec pager = new PagerSpec(1, 0, 3, new PagerSpec.Order[]{new PagerSpec.Order("soldNum", PagerSpec.DRECTION.desc)});
        Page<Product> productList =  productService.find(new ProductCriteria().withStatus(ProductStatus.selling).withBrand(product.getBrand()), pager);

        if(productList != null) {
            if(productList.getContent().size() > 2) {
                for(Product bp : productList) {
                    if(Objects.equals(bp.getId(), product.getId())){
                        productList.getContent().remove(bp);
                        break;
                    }  
                } 
                if(productList.getContent().size() > 2) {
                    productList.getContent().remove(2);
                }
            }
            return productList.getContent();
        } else {
            return null;
        }
    }
    
    
    public List<Product> getEqualPriceProducts(double price)
    {
        Range<Double> r = new Range<>(price * (1 - 0.25), price * (1 + 0.25));
        PagerSpec pager = new PagerSpec(1, 0, 5, new PagerSpec.Order[]{new PagerSpec.Order("soldNum", PagerSpec.DRECTION.desc)});
        Page<Product> productList =  productService.find(new ProductCriteria().withPrice(r).withStatus(ProductStatus.selling), pager);
        if(productList != null) {
            return productList.getContent();
        } else {
            return null;
        }
    }
    
    public boolean hasUseableNum(int productId)
    {
        if(website != null && !website.isEnabledErp()){
            //没有启用ERP
            return true;
        }
        SimpleStorageSummary storageSummary = productStorageService.findStorageSummary(productId);
        return storageSummary.getUseableNum() > 0;
    }
    
    public String getProductSoldPrice(Product product) {
        DecimalFormat df = new DecimalFormat("#0.00");
        if (website != null && product != null) {
            if(website.getRatio() != null && website.getRatio() > 0){
                Product bp = productService.findOne(product.getId());
                if (bp != null) {
                    String integral =df.format(website.getRatio() * bp.getRetailPrice());
                    return integral;
                }
            }
        }
        return "0.00";
    }
    
    /* add by lq  */
    public Integer getProductSoldPrice2(Product product) {
        if (website != null && product != null) {
            if(website.getRatio() != null && website.getRatio() > 0){
                Product bp = productService.findOne(product.getId());
                if (bp != null) {
                    Double integral = Math.ceil(website.getRatio() * product.getRetailPrice());
                    FlashSaleProduct fp = limitExchangeService.find(product);
                    if(fp != null){
                        integral = Math.ceil(website.getRatio() * fp.getDisplayDiscountPrice());
                    }
                    return integral.intValue();
                }
            }
        }
        return 0;
    }
    
    
}
