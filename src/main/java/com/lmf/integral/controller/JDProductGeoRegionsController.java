/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.controller;

import com.lmf.common.SimpleJsonResponse;
import com.lmf.integral.CookieUtil;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.StorageUnit;
import com.lmf.product.service.ProductService;
import com.lmf.product.service.StorageUnitService;
import com.lmf.sys.entity.GeoRegion;
import com.lmf.sys.enums.RegionType;
import com.lmf.sys.service.GeoRegionService;
import com.lmf.system.sdk.service.SystemProductService;
import com.lmf.website.entity.Website;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author shenzhixiong
 */
@Controller
public class JDProductGeoRegionsController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private StorageUnitService storageUnitService;
    
    @Autowired
    private GeoRegionService geoRegionService;
    
    @Autowired
    private SystemProductService systemProductService;
    
    private final static int secondsToLive = 60 * 60 * 24;
    
    @RequestMapping(value = "/product/ajaxJdAddress.php", method = RequestMethod.GET)
    public @ResponseBody SimpleJsonResponse<Map<String, Object>> ajaxJdAddress(@RequestParam(value = "code", required = false) int code,
                                                                               @RequestParam(value = "productId", required = false) int productId,
                                                                               @RequestParam(value = "amount", required = false) Integer amount,
                                                                               Website website,
                                                                               HttpServletResponse reponse) {
        Map<String, Object> result = new HashMap<>();
        if (productId < 0) {
            result.put("message", "商品ID为空！请刷新重试！");
            return new SimpleJsonResponse<>(false, result);
        }
        
        if (code < 0) {
            result.put("message", "您未选中地理位置！请刷新重试！");
            return new SimpleJsonResponse<>(false, result);
        }
        
        if (amount == null || amount <= 0) {
            amount = 1;
        }

        //获取商品
        Product product = productService.findOne(productId);
        if (product == null || product.getSystemProductId() == null) {
            result.put("message", "该商品不存在！请刷新重试！");
            return new SimpleJsonResponse<>(false, result);
        }
        
        //获取当前选中地址
        GeoRegion currGeoRegion = geoRegionService.findOne(code);
        if (currGeoRegion == null) {
            result.put("message", "您选中的地理位置不存在！请刷新重试！");
            return new SimpleJsonResponse<>(false, result);
        }
        Map<String, Object> resultMap = getJdGeoRegion(currGeoRegion);
        String address = "";
        if (!resultMap.isEmpty()) {
            if (resultMap.containsKey("province")) {
                GeoRegion province = (GeoRegion) resultMap.get("province");
                if(province != null) {
                    address += province.getId();
                    CookieUtil.addCookie(reponse, "provinceCode", "" + province.getId(), secondsToLive);
                }
            }
            if (resultMap.containsKey("city")) {
                GeoRegion city = (GeoRegion) resultMap.get("city");
                if(city != null) {
                    address += "_" + city.getId();
                    CookieUtil.addCookie(reponse, "cityCode", "" + city.getId(), secondsToLive);
                }
            }
            if (resultMap.containsKey("county")) {
                GeoRegion county = (GeoRegion) resultMap.get("county");
                if(county != null) {
                    address += "_" + county.getId();
                    CookieUtil.addCookie(reponse, "countyCode", "" + county.getId(), secondsToLive);
                }
            }
            if (resultMap.containsKey("town")) {
                GeoRegion town = (GeoRegion) resultMap.get("town");
            }
            if("".equals(address)) {
                result.put("message", "您选中的地理位置不存在！请刷新重试！");
                return new SimpleJsonResponse<>(false, result);
            }   
        }
        //还要检查库存
        try {
            Map<String, Object> stockResult = systemProductService.productStock(product.getSystemProductId(), amount, address + "_0", website);
            if (stockResult != null) {
                if(stockResult.get("stock_status") != null) {
                    boolean status = (boolean) stockResult.get("stock_status");
                    if(status) {
                        resultMap.put("hasUseab", true);
                        resultMap.put("jdProductStock", "有货，下单立即发货");
                    } else {
                        resultMap.put("hasUseab", false);
                        resultMap.put("jdProductStock", "无货，不可下单");
                    }
                }
                 else {
                    resultMap.put("hasUseab", false);
                    resultMap.put("jdProductStock", "无货，不可下单");
                }
            } else {
                resultMap.put("hasUseab", false);
                resultMap.put("jdProductStock", "无货，不可下单");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return new SimpleJsonResponse<>(true, resultMap);
    }
    
    @RequestMapping(value = "/shoppingCart/ajaxJdAddress.php", method = RequestMethod.GET)
    public @ResponseBody SimpleJsonResponse<Map<String, Object>> ajaxJdAddress(@RequestParam(value = "entry[]", required = false) int[] entryIds,
                                                                               @RequestParam(value = "code", required = false) int code, 
                                                                               Website website,
                                                                               HttpServletResponse reponse) {
        Map<String, Object> result = new HashMap<>();

        if (entryIds == null || entryIds.length <= 0) {
            result.put("message", "商品ID为空！请刷新重试！");
            return new SimpleJsonResponse<>(false, result);
        }

        if (code < 0) {
            result.put("message", "您未选中地理位置！请刷新重试！");
            return new SimpleJsonResponse<>(false, result);
        }

        //获取当前选中地址
        GeoRegion currGeoRegion = geoRegionService.findOne(code);
        if (currGeoRegion == null) {
            result.put("message", "您选中的地理位置不存在！请刷新重试！");
            return new SimpleJsonResponse<>(false, result);
        }
        
        //将用户选择的地址信息存入cookie中
        if (currGeoRegion.getType() == RegionType.province) {
            CookieUtil.addCookie(reponse, "provinceCode", "" + currGeoRegion.getId(), secondsToLive);
        } else if (currGeoRegion.getType() == RegionType.city) {
            CookieUtil.addCookie(reponse, "cityCode", "" + currGeoRegion.getId(), secondsToLive);
        } else if (currGeoRegion.getType() == RegionType.county) {
            CookieUtil.addCookie(reponse, "countyCode", "" + currGeoRegion.getId(), secondsToLive);
        } else if (currGeoRegion.getType() == RegionType.town) {
            CookieUtil.addCookie(reponse, "townCode", "" + currGeoRegion.getId(), secondsToLive);
        }
        
        //镇级地址改变的时候不再查询下级地址
        if (currGeoRegion.getType() != RegionType.town) {
            //获取下级地址
            List<GeoRegion> childs = new ArrayList(currGeoRegion.getChilds());
            result.put("childs", childs);
        }

        return new SimpleJsonResponse<>(true, result);
    }
    
    /* ajax获取京东库存情况 */
    @RequestMapping(value = "/product/ajaxJdProductStorage.php", method = RequestMethod.GET)
    public @ResponseBody SimpleJsonResponse<Map<String, Object>> ajaxJdProductStorage(Website website,
                            @RequestParam(value = "skuId", required = false) Integer skuId,
                            @RequestParam(value = "num", required = false) Integer num, 
                            @RequestParam(value = "province", required = false) Integer jdProvince, 
                            @RequestParam(value = "city", required = false) Integer jdCity, 
                            @RequestParam(value = "county", required = false) Integer jdCounty) {
        Map<String, Object> result = new HashMap<>();
        if (skuId == null || skuId <= 0) {
            result.put("message", "该商品不存在！请刷新重试！");
            return new SimpleJsonResponse<>(false, result);
        }
        
        StorageUnit storageUnit = storageUnitService.findOne(skuId);
        if(storageUnit == null || storageUnit.getProductId() <= 0) {
            result.put("message", "该商品不存在！请刷新重试！");
            return new SimpleJsonResponse<>(false, result);
        }
        
        Product product = productService.findOne(storageUnit.getProductId());
        if (product == null || product.getSystemProductId() == null) {
            result.put("message", "该商品不存在！请刷新重试！");
            return new SimpleJsonResponse<>(false, result);
        }
        
        if (jdProvince == null || jdProvince == 0 || jdCity == null || jdCity == 0) {
            result.put("message", "京东地址信息不全！请刷新重试！");
            return new SimpleJsonResponse<>(false, result);
        }
        //检查库存
        try {
            GeoRegion province = geoRegionService.findOne(jdProvince);
            GeoRegion city = geoRegionService.findOne(jdCity);
            GeoRegion county = null;
            List<GeoRegion> countys = new ArrayList(city.getChilds());
            
            if (!countys.isEmpty() && (jdCounty == null || jdCity == 0)) {
                result.put("message", "京东地址信息不全！请刷新重试！");
                return new SimpleJsonResponse<>(false, result);
            } else if (countys.isEmpty() && jdCounty != null && jdCity > 0) {
                result.put("message", "京东地址信息出现异常！请刷新重试！");
                return new SimpleJsonResponse<>(false, result);
            } else {
                if (jdCounty != null && jdCounty > 0) {
                    county = geoRegionService.findOne(jdCounty);
                }
            }
            String address = province.getId() + "_" + city.getId() + "_" + county.getId();
            
            Map<String, Object> stockResult = systemProductService.productStock(product.getSystemProductId(), num, address + "_0", website);
            if (stockResult != null) {
                if(stockResult.get("stock_status") != null) {
                    result.put("jdProductStock", "有货，免运费");
                    result.put("hasUseab", true);
                    return new SimpleJsonResponse(true, result);
                } else {
                    result.put("jdProductStock", "无货，不可下单");
                    result.put("hasUseab", false);
                    return new SimpleJsonResponse(true, result);
                }
            } else {
                result.put("jdProductStock", "该商品已下架");
                result.put("hasUseab", false);
                return new SimpleJsonResponse(true, result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        result.put("message", "未查询到京东库存！");
        return new SimpleJsonResponse<>(false, result);
    }
    
    private Map<String, Object> getJdGeoRegion(GeoRegion currGeoRegion) {
        Map<String, Object> result = new HashMap<>();
        
        //京东四级地址(省级、市级、县级、镇级)，检查库存使用
        GeoRegion province = null, city = null, county = null, town = null;
        
        //如果当前地址是最低一级(镇级)地址，就不再查询下级地址了
        if (currGeoRegion.getType() != RegionType.county) {
            //获取下级地址
            List<GeoRegion> geoRegions = geoRegionService.childs(currGeoRegion);
            if (geoRegions != null && !geoRegions.isEmpty()) {
                //拼接显示地址下拉框的options
                String optionGeoRegions = "";
                for (GeoRegion gr : geoRegions) {
                    optionGeoRegions += "<option value=\"" + gr.getId() + "\">" + gr.getName() + "</option>";
                }
                //当前地址是省级地址
                if (currGeoRegion.getType() == RegionType.province) {
                    //为省级地址赋值
                    province = currGeoRegion;
                    //省级地址变动，市级地址下拉框默认显示第一个市级地址
                    city = geoRegionService.findOne(geoRegions.get(0).getId());
                    //存入市级地址
                    result.put("citys", optionGeoRegions);
                    optionGeoRegions = "";
                    //县级地址跟着变动
                    List<GeoRegion> countys = geoRegionService.childs(geoRegions.get(0));
                    if (countys != null && !countys.isEmpty()) {
                        //省级地址变动，县级地址下拉框默认显示第一个市级地址下面的第一个县级地址
                        county = geoRegionService.findOne(countys.get(0).getId());
                        for (GeoRegion gr : countys) {
                            optionGeoRegions += "<option value=\"" + gr.getId() + "\">" + gr.getName() + "</option>";
                        }
                        //存入县级地址
                        result.put("countys", optionGeoRegions);
                        optionGeoRegions = "";
                        //镇级地址跟着变动
                        List<GeoRegion> towns = geoRegionService.childs(countys.get(0));
                        if (towns != null && !towns.isEmpty()) {
                            //省级地址变动，镇级地址下拉框默认显示第一个市级地址下面的第一个县级地址下面的第一个镇级地址
                            town = geoRegionService.findOne(towns.get(0).getId());
                            for (GeoRegion gr : towns) {
                                optionGeoRegions += "<option value=\"" + gr.getId() + "\">" + gr.getName() + "</option>";
                            }
                            //存入镇级地址
                            result.put("towns", optionGeoRegions);
                        }
                    }
                } else if (currGeoRegion.getType() == RegionType.city) {
                    //当前地址是市级地址
                    city = currGeoRegion;
                    //省级地址根据市级地址的父级查询
                    province = geoRegionService.findOne(city.getParentId());
                    //市级地址变动，县级地址下拉框默认显示第一个县级地址
                    county = geoRegionService.findOne(geoRegions.get(0).getId());
                    //存入县级地址
                    result.put("countys", optionGeoRegions);
                    optionGeoRegions = "";
                    //镇级地址跟着变动
                    List<GeoRegion> towns = geoRegionService.childs(geoRegions.get(0));
                    if (towns != null && !towns.isEmpty()) {
                        //市级地址变动，镇级地址下拉框默认显示第一个县级地址下面的第一个镇级地址
                        town = geoRegionService.findOne(towns.get(0).getId());
                        for (GeoRegion gr : towns) {
                            optionGeoRegions += "<option value=\"" + gr.getId() + "\">" + gr.getName() + "</option>";
                        }
                        //存入镇级地址
                        result.put("towns", optionGeoRegions);
                    }
                } else if (currGeoRegion.getType() == RegionType.county) {
                    //当前地址是县级地址
                    county = currGeoRegion;
                    city = geoRegionService.findOne(county.getParentId());
                    province = geoRegionService.findOne(city.getParentId());
                    town = geoRegionService.findOne(geoRegions.get(0).getId());
                    //存入镇级地址
                    result.put("towns", optionGeoRegions);
                }
            } else { 
                
                
            }
        } else {
            //如果当前地址是最低一级(镇级)地址，则依次根据父级地址查询出县、市、省级地址
            county = currGeoRegion;
            city = geoRegionService.findOne(county.getParentId());
            province = geoRegionService.findOne(city.getParentId());
        }
        
        //依次存入四级地址返回
        result.put("province", province);
        result.put("city", city);
        result.put("county", county);
        result.put("town", town);
        
        return result;
    }
    
}
