/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.task;


import com.lmf.sys.entity.GeoRegion;
import com.lmf.sys.enums.RegionType;
import com.lmf.sys.service.GeoRegionService;
import com.lmf.system.sdk.service.SystemGeoRegionsService;
import com.lmf.website.entity.Website;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 京东地址信息定时刷新
 * @author lianwukun
 */
public class RefreshJinDongRegionTask {
    
    @Autowired
    private SystemGeoRegionsService  jdRegionService;
    
    @Autowired
    private GeoRegionService  geoRegionService;
    
    @Autowired
    private Website website;
    
    private final Logger  logger    = LoggerFactory.getLogger(RefreshJinDongRegionTask.class);
    
    @Scheduled(cron = "0 0 2 * * ?")  //每日2点刷新省市信息
    public  void  refreshJingdongRegion() {
        if (logger.isInfoEnabled()) {
            logger.info(" \n================== start refresh jingdong region ======================");
        }
        
        List<GeoRegion> provinces = null;
        try{
            provinces  = jdRegionService.findProvinces(website);
        }catch(Exception ex){
            logger.info(ex.getMessage());
        }
        if(provinces == null || provinces.isEmpty()){
            return;
        }
        for (GeoRegion prov : provinces) 
        {
            //保存当前省份信息
            saveRegion(prov, null, RegionType.province);
            
            //获取下面的城市
            List<GeoRegion> citys = null;
            try{
                citys = jdRegionService.findCitys(website,prov);
            }catch(Exception ex){
                //发生异常跳过
                logger.info(ex.getMessage());
                continue;
            }
            List<Integer> cityIdList = new ArrayList<>();
            for (GeoRegion city : citys) 
            {
                //保存当前市信息
                saveRegion(city, prov.getId(), RegionType.city);
                cityIdList.add(city.getId());
                
                //获取下面的区/县
                List<GeoRegion> countys = null;
                try{
                    countys = jdRegionService.findCountys(website, city);
                }catch(Exception ex){
                    //发生异常跳过
                    logger.info(ex.getMessage());
                    continue;
                }
                List<Integer> countyIdList = new ArrayList<>();
                if (countys != null && !countys.isEmpty()) {
                    for (GeoRegion county : countys) 
                    {
                        //保存区/县信息
                        saveRegion(county, city.getId(), RegionType.county);
                        countyIdList.add(county.getId());
                        
                        //获取下面的乡镇信息
                        List<GeoRegion> towns = null;
                        try{
                            towns = jdRegionService.findTowns(website, county);
                        }catch(Exception ex){
                            //发生异常跳过
                            logger.info(ex.getMessage());
                            continue;
                        }
                        List<Integer> townIdList = new ArrayList<>();
                        if (towns != null && !towns.isEmpty()) {
                            for (GeoRegion town : towns) 
                            {
                                saveRegion(town, county.getId(), RegionType.town);
                                townIdList.add(town.getId());
                            }
                        }
                        if(townIdList.isEmpty()){
                            townIdList.add(-1);
                        }
                        
                        //禁用本次同步更新未出现的乡镇信息
                        geoRegionService.setEnabledByParentCode(county.getId(), townIdList);
                    }
                } else {
                    if (logger.isInfoEnabled()) {
                        logger.warn("try to load counties in city {}, no result returned", city.getId());
                    }
                }
                
                //禁用本次同步更新未出现的区/县
                if(countyIdList.isEmpty()){
                    countyIdList.add(-1);
                }
                geoRegionService.setEnabledByParentCode(city.getId(), countyIdList);
            }
        }
        logger.info(" \n================== end refresh jingdong region ======================");
    }
    
    private void  saveRegion(GeoRegion region, Integer parentCode, RegionType type) {
        try{
            GeoRegion   reg = new GeoRegion();
            reg.setId(region.getId());
            reg.setName(region.getName());
            reg.setParentId(parentCode);
            reg.setEnabled(true);
            reg.setType(type);
            geoRegionService.JdGeoRegionSave(reg);
        }catch(Exception ex){
            logger.info(" \n================== save JD region fail. Exception Message: {} ======================", ex.getMessage());
        }
    }
}
