/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.misc.controller;

import com.lmf.sys.entity.GeoRegion;
import com.lmf.sys.service.GeoRegionService;
import java.util.Collections;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author zhouwei
 */
@Controller
public class RegionController {
    
    @Autowired
    private GeoRegionService  geoRegionService;
    
    @RequestMapping(value = "/region/ajax.php", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public  @ResponseBody Set<GeoRegion>  childs(@RequestParam("parentId") int regionId)
    {
        if (regionId <= 0)
        {
            return geoRegionService.findAllProvince();
        }
        GeoRegion   thizRegion  = geoRegionService.findOne(regionId);
        if (thizRegion == null)
        {
            return Collections.EMPTY_SET;
        }
        Set<GeoRegion>  childs  = thizRegion.getChilds();
        if (childs == null)
        {
            return Collections.EMPTY_SET;
        }
        return childs;
    }
    
}
