/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.controller;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.PagerSpec;
import com.lmf.sys.entity.GeoRegion;
import com.lmf.sys.enums.RegionType;
import com.lmf.sys.service.GeoRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 地址管理
 *
 * @author lianwukun
 */
@Controller("geoRegionController")
public class GeoRegionController {

    @Autowired
    private GeoRegionService geoRegionService;

    //地址列表
    @RequestMapping(value = "/admin/geoRegion.php", method = RequestMethod.GET)
    public String list(@PagerSpecDefaults(pageSize = 20, sort = "id.desc") PagerSpec pager,
                       @RequestParam(name = "keyword", required = false) String keyword,
                       @RequestParam(name = "type", required = false) RegionType type,
                       Model model) throws UnsupportedEncodingException {
        Page<GeoRegion> geoRegions = geoRegionService.findAll(keyword, type, pager);
        StringBuilder link = new StringBuilder("/jdvop/admin/geoRegion.php?page=[:page]");
        if (keyword != null) {
            link.append("&keyword=").append(URLEncoder.encode(keyword, "UTF-8"));
            model.addAttribute("keyword", keyword);
        }

        model.addAttribute("geoRegionService",geoRegionService);
        model.addAttribute("geoRegions",geoRegions);
        model.addAttribute("link",link.toString());
        return "/admin/geo_region/list";
    }

    //禁用或启用
    @ResponseBody
    @RequestMapping(value = "/admin/geoRegion/setEnabled.php", method = RequestMethod.GET)
    public SimpleJsonResponse<String> setEnable(@RequestParam(name = "enabled", required = false) boolean enabled,
                                                 @RequestParam(name = "id", required = false) int id,
                                                 Model model) {
        try {
            geoRegionService.setEnabled(id,enabled);
        } catch (Exception e) {
            return new SimpleJsonResponse<>(false, "更新失败");
        }
        return new SimpleJsonResponse<>(true, null);
    }

}
