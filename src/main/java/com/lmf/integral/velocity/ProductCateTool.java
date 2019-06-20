/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.velocity;

import com.lmf.integral.service.WebsiteProxyService;
import com.lmf.product.entity.ProductCate;
import com.lmf.product.service.ProductCateService;
import com.lmf.website.entity.Website;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.InvalidScope;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author zhouwei
 */
@DefaultKey("productCateTool")
@InvalidScope({Scope.APPLICATION})
public class ProductCateTool{
    
    private ProductCateService  productCateService;
    
    private Website website;
    
    private ChainedContext  cctx;
    
    public  void init(Object ctx) {
        cctx    = (ChainedContext) ctx;
        WebApplicationContext   wctx = WebApplicationContextUtils.getWebApplicationContext(cctx.getServletContext());
        productCateService      = wctx.getBean(ProductCateService.class);
    }
    
    public ProductCate asProductCate(int cateId)
    {
        return productCateService.findOne(cateId);
    }
    
    public Set<ProductCate>  getSiblings(int cateId)
    {
        return productCateService.siblings(cateId);
    }
    
    public List<ProductCate>  getAllChilds(int cateId)
    {
        ProductCate cate = productCateService.findOne(cateId);
        return productCateService.allChilds(cate);
    }
    
    public Set<ProductCate>    rootCates()
    {
        return productCateService.rootCates();
    }
    
    public Map<Integer, ProductCate> getRootCateMap()
    {
        //所有一级分类
        Map<Integer, ProductCate> rootCateMap = new HashMap<>();
        Set<ProductCate> productCateSet = productCateService.rootCates();
        for(ProductCate cate:productCateSet){
            rootCateMap.put(cate.getId(), cate);
        }
        return rootCateMap;
    }
}
