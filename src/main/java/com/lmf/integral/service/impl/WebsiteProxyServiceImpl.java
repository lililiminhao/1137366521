/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.service.impl;

import com.lmf.integral.SystemConfig;
import com.lmf.integral.service.WebsiteProxyService;
import com.lmf.product.entity.ProductCate;
import com.lmf.product.service.ProductCateService;
import com.lmf.website.entity.NavigationItem;
import com.lmf.website.entity.Website;
import com.lmf.website.service.WebsiteService;
import com.lmf.website.theme.v2.PageBlockDef;
import com.lmf.website.theme.v2.PageBlockType;
import com.lmf.website.theme.v2.PageSkeleton;
import com.lmf.website.theme.v2.manager.ThemeManager;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author shenzhixiong
 */
public class WebsiteProxyServiceImpl implements WebsiteProxyService {

    private final Map<Integer, ProductCate>  websiteProductCateCache  = new HashMap<>();
    
    private final Map<Website, NavigationItem[]>  navigationCache   = new ConcurrentHashMap<>();
    
    private volatile EnumMap<PageBlockType, Map<String, PageBlockDef>>  pageBlockDefCache;
    
    private final Map<Website, PageSkeleton>  websiteThemeV2SkeletonCache   = new ConcurrentHashMap<>();
    
    private final static Logger logger  = LoggerFactory.getLogger(WebsiteProxyServiceImpl.class);
    
    @Autowired
    private SystemConfig   systemConfig;
    
    @Autowired
    private ProductCateService  productCateService;
    
    @Autowired
    private ThemeManager        themeManager;
    
    @Autowired
    private Website website;
    
    @Autowired
    private WebsiteService websiteService;
    
    @Override
    public Website getWebsite() {
        return website;
    }

    @Override
    public Set<ProductCate> rootCates() {
        Map<Integer, ProductCate>   allCates    = getWebsiteProductCateMap();
        Set<ProductCate>  result   = new HashSet<>();
        for (Map.Entry<Integer, ProductCate> e : allCates.entrySet()) {
            ProductCate c   = e.getValue();
            if (!c.isDeleted() && (c.getParentId() == null || c.getParentId() == 0 )) {
                result.add(c);
            }
        }
        return  result;
    }

    @Override
    public PageSkeleton getV2ThemeSkeleton() {
        PageSkeleton   skeleton = websiteThemeV2SkeletonCache.get(website);
        if (skeleton == null)
        {
            skeleton    = themeManager.getPageSkeleton();
            if (skeleton != null)
            {
                websiteThemeV2SkeletonCache.put(website, skeleton);
            }
        }
        return skeleton;
    }
    
    @Override
    public PageBlockDef getV2ThemeDef(PageBlockType blockType, String key) {
        EnumMap<PageBlockType, Map<String, PageBlockDef>> ref = pageBlockDefCache;
        if (ref == null)
        {
            ref   = reloadV2ThemeBlockDef();
        }
        Map<String, PageBlockDef>  emm  = ref.get(blockType);
        if (emm == null)
        {
            return null;
        }
        return emm.get(key);
    }
    
    private synchronized EnumMap<PageBlockType, Map<String, PageBlockDef>> reloadV2ThemeBlockDef()
    {
        EnumMap<PageBlockType, Map<String, PageBlockDef>> registeredBlockDefs = new EnumMap<>(PageBlockType.class);
        for (PageBlockType pbt : PageBlockType.values())
        {
            Map<String, PageBlockDef>  leaf = registeredBlockDefs.get(pbt);
            if (leaf == null)
            {
                leaf   = new HashMap<>();
                registeredBlockDefs.put(pbt, leaf);
            }
            for (PageBlockDef bpd : themeManager.getThemeDefs(pbt))
            {
                leaf.put(bpd.getKey(), bpd);
            }
        }
        return pageBlockDefCache   = registeredBlockDefs;
    }
    

    @Override
    public void reloadConfig() {
        if (logger.isInfoEnabled()) {
            logger.info("============= website engine is start reload ================");
        }
        Website newWebsite  = websiteService.findOne(systemConfig.getWebsiteId());
        if(newWebsite != null) {
            website.setName(newWebsite.getName());
            website.setLogo(newWebsite.getLogo());
            website.setToken(newWebsite.getToken());
            website.setRatio(newWebsite.getRatio());
            website.setLastModifyTime(newWebsite.getLastModifyTime());
            website.setIcpNo(newWebsite.getIcpNo());
            website.setBriefDescription(newWebsite.getBriefDescription());
            website.setCustomerServiceQQ(newWebsite.getCustomerServiceQQ());
            website.setCustomerServiceHotLine(newWebsite.getCustomerServiceHotLine());
            website.setQrcode(newWebsite.getQrcode());
            website.setShipmentFeeSettingId(newWebsite.getShipmentFeeSettingId());
            website.setShipmentCompanyId(newWebsite.getShipmentCompanyId());
            website.setEnabledErp(newWebsite.isEnabledErp());
            website.setExt(newWebsite.getExt());
        }
        
        reloadV2ThemeBlockDef();
        websiteThemeV2SkeletonCache.clear();//清空所有的广告缓存，使其自动reload
        navigationCache.clear();//清空顶部导航栏配置，让其自动reloadnavigationCache
        if (logger.isInfoEnabled()) {
            logger.info("============= website engine is end reload ================");
        }
    }
    
    @Override
    public NavigationItem[] getNavigationItems() {
        NavigationItem[]  result    = navigationCache.get(website);
        if (result == null) {
            result  = websiteService.getWebsiteNavigations();
            if (result == null) {
                result  = new NavigationItem[0];
            }
            navigationCache.put(website, result);
        }
        return result;
    }
    
    private Map<Integer, ProductCate>  getWebsiteProductCateMap()
    {
        Map<Integer, ProductCate>  root = websiteProductCateCache;
        if (root == null)
        {
            root    = productCateService.allAsMap();
        }
        return root;
    }
    
}
