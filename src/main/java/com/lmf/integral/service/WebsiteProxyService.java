/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.service;

import com.lmf.product.entity.ProductCate;
import com.lmf.website.entity.NavigationItem;
import com.lmf.website.entity.Website;
import com.lmf.website.theme.v2.PageBlockDef;
import com.lmf.website.theme.v2.PageBlockType;
import com.lmf.website.theme.v2.PageSkeleton;
import java.util.Set;

/**
 *
 * @author zhangxinling
 */
public interface WebsiteProxyService {
    
    public  Website         getWebsite();
    
    public  Set<ProductCate>   rootCates();
    
    public  PageSkeleton    getV2ThemeSkeleton();
    
    public  PageBlockDef    getV2ThemeDef(PageBlockType blockType, String key);
    
    public  void    reloadConfig();
    
    public NavigationItem[] getNavigationItems();
    
}
