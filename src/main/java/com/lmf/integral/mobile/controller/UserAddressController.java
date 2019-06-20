/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.mobile.controller;

import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.exceptions.PermissionDeniedException;
import com.lmf.common.util.ValidUtil;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.sys.entity.GeoRegion;
import com.lmf.sys.service.GeoRegionService;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.entity.WebsiteUserAddress;
import com.lmf.website.enums.WebsiteLoginType;
import com.lmf.website.service.WebsiteUserAddressService;
import com.lmf.website.service.WebsiteUserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author shenzhixiong
 */
@Controller("mobileUserAddressController")
public class UserAddressController {
    
    @Autowired
    private WebsiteUserService websiteUserService;

    @Autowired
    private WebsiteUserAddressService websiteUserAddressService;
    
    @Autowired
    private GeoRegionService    geoRegionService;
    
    @RequestMapping(value = "/my/mobile/address.php", method = RequestMethod.GET)
    public String addressView(@RequestParam(value = "isSubmitOrder", defaultValue = "false") Boolean isSubmitOrder,
                                @RequestParam(value = "isAwardSubmitOrder", defaultValue = "false") Boolean isAwardSubmitOrder,
                                @RequestParam(value = "islimitExchangeSubmitOrder", defaultValue = "false") Boolean islimitExchangeSubmitOrder,
                                @RequestParam(value = "islimitTimeKillOrder", defaultValue = "false") Boolean islimitTimeKillOrder,
                                @RequestParam(value = "pid", required=false) Integer pid,
                                HttpServletRequest request, Model model)
    {
        HttpSession session = request.getSession(true);
        UserDetail currentUser = (UserDetail) session.getAttribute("currentUser");
        WebsiteUser websiteUser = websiteUserService.findOne(WebsiteLoginType.loginName, currentUser.getLoginName());
        List<WebsiteUserAddress>   addressList  = websiteUserAddressService.find(websiteUser);
        Map<Long, String>  addrMap    = new HashMap<>();
        for (WebsiteUserAddress addr : addressList)
        {
            StringBuilder sbd = new StringBuilder();
            GeoRegion province = geoRegionService.findOne(addr.getProvince().getId());
            sbd.append(province.getName());
            GeoRegion city = geoRegionService.findOne(addr.getCity().getId());
            sbd.append(city.getName());
            GeoRegion county = geoRegionService.findOne(addr.getCounty().getId());
            sbd.append(county.getName());
            if(addr.getTown() != null) {
                GeoRegion town = geoRegionService.findOne(addr.getTown().getId());
                if(town != null && town.getId() > 0) {
                    sbd.append(town.getName());
                }
            }
            addrMap.put(addr.getId(), sbd.toString());
        }
        model.addAttribute("isSubmitOrder", isSubmitOrder);
        model.addAttribute("isAwardSubmitOrder", isAwardSubmitOrder);
        model.addAttribute("islimitExchangeSubmitOrder", islimitExchangeSubmitOrder);
        model.addAttribute("islimitTimeKillOrder", islimitTimeKillOrder);//限时秒杀
        model.addAttribute("addrMap", addrMap);
        model.addAttribute("addressList", addressList);
        model.addAttribute("pid", pid);
        model.addAttribute("provinceList", geoRegionService.findAllProvince());
        return "user_center/address_list";
    }
    
    @RequestMapping(value = "/my/mobile/address/add.php",method = RequestMethod.GET)
    public String add(@RequestParam(value = "isSubmitOrder", defaultValue = "false") boolean isSubmitOrder, 
                      @RequestParam(value = "isAwardSubmitOrder", defaultValue = "false") Boolean isAwardSubmitOrder,
                      @RequestParam(value = "islimitExchangeSubmitOrder", defaultValue = "false") Boolean islimitExchangeSubmitOrder,
                      @RequestParam(value = "islimitTimeKillOrder", defaultValue = "false") Boolean islimitTimeKillOrder,
                      @RequestParam(value = "pid", required=false) Integer pid,
                      Model model)
    {
        model.addAttribute("isSubmitOrder", isSubmitOrder);
        model.addAttribute("isAwardSubmitOrder", isAwardSubmitOrder);
        model.addAttribute("islimitExchangeSubmitOrder", islimitExchangeSubmitOrder);
        model.addAttribute("islimitTimeKillOrder", islimitTimeKillOrder);//限时秒杀
        model.addAttribute("provinceList", geoRegionService.findAllProvince());
        model.addAttribute("pid",pid);
        return "user_center/address_edit";
    }
    
    @RequestMapping(value = "/my/mobile/address/add.php", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public @ResponseBody SimpleJsonResponse add(@RequestParam("name") String receiverName,
                                                @RequestParam("regions[]") int[] regions,
                                                @RequestParam("address") String address,
                                                @RequestParam("mobile") String mobile,
                                                UserDetail userDetail, 
                                                WebsiteUser websiteUser,
                                                HttpSession session)
    {
        if(userDetail == null || userDetail.getUserId() <= 0){
            throw new PermissionDeniedException();
        }
        
        SimpleJsonResponse checkInfo = checkUserAddress(receiverName, regions, address, mobile);
        if(!checkInfo.isIsOk()){
            return checkInfo;
        }
        
        WebsiteUserAddress websiteUserAddress = new WebsiteUserAddress();
        websiteUserAddress.setUserId(userDetail.getUserId());
        websiteUserAddress.setMobile(mobile.trim());
        websiteUserAddress.setReceiverName(receiverName.trim());
        websiteUserAddress.setProvince(geoRegionService.findOne(regions[0]));
        websiteUserAddress.setCity(geoRegionService.findOne(regions[1]));
        websiteUserAddress.setCounty(geoRegionService.findOne(regions[2]));
        if(regions.length > 3) {
            GeoRegion town = geoRegionService.findOne(regions[3]);
            if(town == null || town.getId() < 1) {
                return new SimpleJsonResponse(false, "请选择所在街道！");
            }
            websiteUserAddress.setTown(geoRegionService.findOne(regions[3]));
        }
        websiteUserAddress.setAddress(address.trim());
        websiteUserAddress.setAsDefault(true);
        
        websiteUserAddress = websiteUserAddressService.save(websiteUser, websiteUserAddress);
        
        return new SimpleJsonResponse(true, websiteUserAddress);
    }
    
    @RequestMapping(value = "/my/mobile/address/edit.php", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String  edit(@RequestParam("id") long id, 
                        @RequestParam(value = "isSubmitOrder", defaultValue = "false") boolean isSubmitOrder,
                        @RequestParam(value = "isAwardSubmitOrder", defaultValue = "false") Boolean isAwardSubmitOrder,
                        @RequestParam(value = "islimitExchangeSubmitOrder", defaultValue = "false") Boolean islimitExchangeSubmitOrder,
                        @RequestParam(value = "islimitTimeKillOrder", defaultValue = "false") Boolean islimitTimeKillOrder,
                        UserDetail userDetail, Model model)
    {
        if(userDetail == null || userDetail.getUserId() <= 0){
            throw new PermissionDeniedException();
        }
        
        WebsiteUserAddress websiteUserAddress = websiteUserAddressService.findOne(id);
        if(userDetail.getUserId() != websiteUserAddress.getUserId()){
            throw new PermissionDeniedException();
        }
        if(isSubmitOrder){
            model.addAttribute("isSubmitOrder", isSubmitOrder);
        }
        model.addAttribute("isAwardSubmitOrder", isAwardSubmitOrder);
        model.addAttribute("islimitExchangeSubmitOrder", islimitExchangeSubmitOrder);
        model.addAttribute("islimitTimeKillOrder", islimitTimeKillOrder);//限时秒杀
        model.addAttribute("address", websiteUserAddress);
        GeoRegion city = geoRegionService.findOne(websiteUserAddress.getCity().getId());
        GeoRegion county = geoRegionService.findOne(websiteUserAddress.getCounty().getId());
        if(websiteUserAddress.getTown() != null) {
            GeoRegion town = geoRegionService.findOne(websiteUserAddress.getTown().getId());
            model.addAttribute("townList", geoRegionService.siblings(town));
        }
        model.addAttribute("provinceList", geoRegionService.findAllProvince());
        model.addAttribute("cityList", geoRegionService.siblings(city));
        model.addAttribute("countyList", geoRegionService.siblings(county));
        return "user_center/address_edit";
    }
    
    @RequestMapping(value = "/my/mobile/address/edit.php", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public @ResponseBody SimpleJsonResponse edit(@RequestParam("addressId") int id,
                                                 @RequestParam("name") String receiverName,
                                                 @RequestParam("regions[]") int[] regions,
                                                 @RequestParam("address") String address,
                                                 @RequestParam("mobile") String mobile,
                                                 WebsiteUser websiteUser, 
                                                 HttpSession session)
    {
        if(websiteUser == null || websiteUser.getId() <= 0){
            throw new PermissionDeniedException();
        }
        
        WebsiteUserAddress websiteUserAddress = websiteUserAddressService.findOne(id);
        if(websiteUserAddress == null || websiteUserAddress.getId() <= 0){
            return new SimpleJsonResponse(false, "地址信息不存在,或已经被删除");
        }
        
        SimpleJsonResponse checkInfo = checkUserAddress(receiverName, regions, address, mobile);
        if(!checkInfo.isIsOk()){
            return checkInfo;
        }
        
        websiteUserAddress.setUserId(websiteUser.getId());
        websiteUserAddress.setMobile(mobile.trim());
        websiteUserAddress.setReceiverName(receiverName.trim());
        websiteUserAddress.setProvince(geoRegionService.findOne(regions[0]));
        websiteUserAddress.setCity(geoRegionService.findOne(regions[1]));
        websiteUserAddress.setCounty(geoRegionService.findOne(regions[2]));
        if(regions.length > 3) {
            GeoRegion town = geoRegionService.findOne(regions[3]);
            if(town == null || town.getId() < 1) {
                return new SimpleJsonResponse(false, "请选择所在街道！");
            }
            websiteUserAddress.setTown(geoRegionService.findOne(regions[3]));
        } else {
            websiteUserAddress.setTown(null);
        }
        websiteUserAddress.setAddress(address.trim());
        
        websiteUserAddress = websiteUserAddressService.save(websiteUser,websiteUserAddress);
        return new SimpleJsonResponse(true, websiteUserAddress);
    }
    
    @RequestMapping(value = "/my/mobile/address/saveIdentityCard.php", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public @ResponseBody SimpleJsonResponse saveIdentityCard(@RequestParam("addressId") int id,
                                                             @RequestParam("identityCard") String identityCard,
                                                             WebsiteUser websiteUser, 
                                                             HttpSession session)
    {
        if(websiteUser == null || websiteUser.getId() <= 0){
            throw new PermissionDeniedException();
        }
        
        WebsiteUserAddress websiteUserAddress = websiteUserAddressService.findOne(id);
        if(websiteUserAddress == null || websiteUserAddress.getId() <= 0){
            return new SimpleJsonResponse(false, "地址信息不存在,或已经被删除");
        }
        
        if(identityCard == null || identityCard.trim().isEmpty()) {
            return new SimpleJsonResponse(false, "请填写身份证信息！");
        }
        if(!ValidUtil.isValidatedAllIdcard(identityCard)) {
            return new SimpleJsonResponse(false, "请输入正确的身份证号码！");
        }
        
        websiteUserAddressService.setIdentityCard(websiteUser, id, identityCard);
        return new SimpleJsonResponse(true, websiteUserAddress);
    }
    
    private SimpleJsonResponse checkUserAddress(String receiverName, int[] regions, String address, String mobile){
        if(receiverName.trim().isEmpty()){
            return new SimpleJsonResponse(false, "请填写收货人姓名！");
        }
        if(mobile.isEmpty()){
            return new SimpleJsonResponse(false, "请填写收货人的联系方式！");
        } 
        if(!ValidUtil.isMobile(mobile)) {
            return new SimpleJsonResponse(false, "请填写正确的手机号码！");
        }
        
        if(regions == null || regions.length < 3){
            return new SimpleJsonResponse(false, "请选择详细的省市信息！");
        }
        if(regions[0] < 1 || geoRegionService.findOne(regions[0]) == null) {
            return new SimpleJsonResponse(false, "请选择所在省份！");
        }
        if(regions[1] < 1 || geoRegionService.findOne(regions[1]) == null) {
            return new SimpleJsonResponse(false, "请选择所在城市！");
        }
        if(regions[2] < 1 || geoRegionService.findOne(regions[2]) == null) {
            return new SimpleJsonResponse(false, "请选择所在地区！");
        }
        
        if(address.isEmpty()){
            return new SimpleJsonResponse(false, "请填写详细地址信息！");
        }
        return new SimpleJsonResponse(true, null);
    }
}