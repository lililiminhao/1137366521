/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.task;

import com.lmf.common.enums.OwnerType;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.StorageUnit;
import com.lmf.product.enums.StorageUnitEffectBarType;
import com.lmf.product.enums.StorageUnitStatus;
import com.lmf.product.service.ProductService;
import com.lmf.system.sdk.service.SystemProductService;
import com.lmf.system.sdk.vo.SystemProduct;
import com.lmf.website.entity.Website;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 *
 * @author shenzhixiong
 */
public class SystemProductReloadTask {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private SystemProductService systemProductService;
    
    @Autowired
    private Website website;
    
    private final Logger    logger  = LoggerFactory.getLogger(SystemProductReloadTask.class);
    
//    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000)  
	@Scheduled(cron = "0 0 3 * * ?") //每日3点刷新
    //@Scheduled(cron = "0 0/5 * * * ? ")//测试五分钟一次
    public void reloadProduct() throws IOException {
        if (logger.isInfoEnabled()) {
            logger.info(" ================== start refresh system products ======================");
        }
        Map<Integer, List<Integer>> pages  = systemProductService.getAllProductIds(website);
        List<Integer> existsList = new ArrayList();
        if(pages != null && !pages.isEmpty()) {
            for(Map.Entry<Integer, List<Integer>> page : pages.entrySet()) {
                for(Integer pid : page.getValue()){
                    SystemProduct systemProduct = systemProductService.getProductDetial(pid, website);
                    if(systemProduct != null) {
                        Product product = productService.findSystemProduct(systemProduct.getSystemProductId());
                        List<StorageUnit> storageUnitCollection = new ArrayList<>();
                        if(product == null) {
                            StorageUnit storageUnit = new StorageUnit();
                            storageUnit.setProperty1("实物");
                            if(systemProduct.getOwnerType() == OwnerType.jingdong) {
                                storageUnit.setProperty2("京东");
                            } else {
                                storageUnit.setProperty2("系统");
                            }
                            storageUnit.setEffectBarType(StorageUnitEffectBarType.bar_code);
                            storageUnit.setStatus(StorageUnitStatus.selling);
                            storageUnitCollection.add(storageUnit);
                        }
                        existsList.add(pid);
                        productService.saveSystemProduct(systemProduct, storageUnitCollection);
                    } 
                }
                if(!existsList.isEmpty()) {
                   productService.setSystemProductStatus(existsList);
                }
                if (logger.isInfoEnabled()) {
                    logger.info("try to refresh system products, productIds size {} fetched {} pageNum", page.getValue().size(), page.getKey());
                }
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info(" ============ end refresh system products ============================");
        }
    } 
}
