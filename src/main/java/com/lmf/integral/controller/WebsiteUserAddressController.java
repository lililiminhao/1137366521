/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.controller;

import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.exceptions.PermissionDeniedException;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.sys.entity.GeoRegion;
import com.lmf.sys.service.GeoRegionService;
import com.lmf.website.entity.Website;
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
@Controller
public class WebsiteUserAddressController {
    
    @Autowired
    private WebsiteUserService websiteUserService;
    
    @Autowired
    private WebsiteUserAddressService websiteUserAddressService;
    
    @Autowired
    private GeoRegionService    geoRegionService;
     
    @RequestMapping(value = "/my/address.php", method = RequestMethod.GET)
    public String address(@RequestParam(value = "isSubmitOrder",required = false) Boolean isSubmitOrder,HttpServletRequest request,Website website, Model model)
    {
        if(isSubmitOrder != null){
            model.addAttribute("isSubmitOrder",isSubmitOrder);
        }
        HttpSession session = request.getSession(true);
        UserDetail currentUser = (UserDetail) session.getAttribute("currentUser");
        WebsiteUser websiteUser = websiteUserService.findOne(WebsiteLoginType.loginName, currentUser.getLoginName());
        List<WebsiteUserAddress>   addressList  = websiteUserAddressService.find(websiteUser);
        model.addAttribute("addressList", addressList);
        model.addAttribute("provinces", geoRegionService.findAllProvince());
        return "user_center/address";
    }
    
    @RequestMapping(value = "/my/address/add.php", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public @ResponseBody SimpleJsonResponse add(@RequestParam("name") String receiverName,
                                                @RequestParam("regions[]") int[] regions,
                                                @RequestParam("address") String address,
                                                @RequestParam("mobile") String mobile,
                                                @RequestParam("phone") String phone,
                                                UserDetail userDetail, 
                                                WebsiteUser websiteUser,
                                                HttpSession session)
    {
        if(userDetail == null || userDetail.getUserId() <= 0){
            throw new PermissionDeniedException();
        }
        
        SimpleJsonResponse checkInfo = checkUserAddress(receiverName, regions, address, mobile, phone);
        if(!checkInfo.isIsOk()){
            return checkInfo;
        }
        
        WebsiteUserAddress websiteUserAddress = new WebsiteUserAddress();
        websiteUserAddress.setUserId(userDetail.getUserId());
        websiteUserAddress.setPhone(phone.trim());
        websiteUserAddress.setMobile(mobile.trim());
        websiteUserAddress.setReceiverName(receiverName.trim());
        websiteUserAddress.setAddress(address.trim());
        websiteUserAddress.setAsDefault(false);
        
        websiteUserAddress.setProvince(geoRegionService.findOne(regions[0]));
        websiteUserAddress.setCity(geoRegionService.findOne(regions[1]));
        websiteUserAddress.setCounty(geoRegionService.findOne(regions[2]));
        if(regions.length > 3) {
            GeoRegion town = geoRegionService.findOne(regions[3]);
            if(town == null) {
                return new SimpleJsonResponse(false, "请选择所在街道！");
            }
            websiteUserAddress.setTown(town);
        }
        
        websiteUserAddress = websiteUserAddressService.save(websiteUser,websiteUserAddress);
        
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("address", websiteUserAddress);
        
        return new SimpleJsonResponse(true, dataMap);
    }
    
    @RequestMapping(value = "/my/address/edit.php", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public @ResponseBody SimpleJsonResponse edit(@RequestParam("addressId") int id,
                                                 @RequestParam("name") String receiverName,
                                                 @RequestParam("regions[]") int[] regions,
                                                 @RequestParam("address") String address,
                                                 @RequestParam("mobile") String mobile,
                                                 @RequestParam("phone") String phone,
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
        
        SimpleJsonResponse checkInfo = checkUserAddress(receiverName, regions, address, mobile, phone);
        if(!checkInfo.isIsOk()){
            return checkInfo;
        }
        
        websiteUserAddress.setUserId(websiteUser.getId());
        websiteUserAddress.setPhone(phone.trim());
        websiteUserAddress.setMobile(mobile.trim());
        websiteUserAddress.setReceiverName(receiverName.trim());
        websiteUserAddress.setProvince(geoRegionService.findOne(regions[0]));
        websiteUserAddress.setCity(geoRegionService.findOne(regions[1]));
        websiteUserAddress.setCounty(geoRegionService.findOne(regions[2]));
        if(regions.length > 3) {
            if(geoRegionService.findOne(regions[3]) == null) {
                return new SimpleJsonResponse(false, "请选择所在街道！");
            }
            websiteUserAddress.setTown(geoRegionService.findOne(regions[3]));
        } else {
            websiteUserAddress.setTown(null);
        }
        websiteUserAddress.setAddress(address.trim());
        websiteUserAddressService.save(websiteUser,websiteUserAddress);
        
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("address", websiteUserAddress);
                
        return new SimpleJsonResponse(true, dataMap);
    }
    
    @RequestMapping(value = "/my/address/view.php", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public @ResponseBody SimpleJsonResponse view(@RequestParam("id") long id, UserDetail userDetail)
    {
        if(userDetail == null || userDetail.getUserId() <= 0){
            throw new PermissionDeniedException();
        }
        
        WebsiteUserAddress websiteUserAddress = websiteUserAddressService.findOne(id);
        if(userDetail.getUserId() != websiteUserAddress.getUserId()){
            throw new PermissionDeniedException();
        }
       
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("citys", geoRegionService.siblings(websiteUserAddress.getCity()));
        dataMap.put("countys", geoRegionService.siblings(websiteUserAddress.getCounty()));
        if(websiteUserAddress.getTown() != null) {
            dataMap.put("towns", geoRegionService.siblings(websiteUserAddress.getTown()));
        }
        dataMap.put("address", websiteUserAddress);
        
        return new SimpleJsonResponse(true, dataMap);
    }
    
    @RequestMapping(value = "/my/address/delete.php", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public @ResponseBody SimpleJsonResponse addressDelete(@RequestParam("id") long id,Website website ,HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);
        UserDetail currentUser = (UserDetail) session.getAttribute("currentUser");
        WebsiteUser websiteUser = websiteUserService.findOne(WebsiteLoginType.loginName,currentUser.getLoginName());
        WebsiteUserAddress websiteUserAddress  = websiteUserAddressService.findOne(id);
        if(websiteUserAddress == null) {
            return new SimpleJsonResponse(false, "该地址已被删除，请刷新当前页面！");
        }
        if (websiteUserAddress.getUserId() != websiteUser.getId())
        {
            throw new PermissionDeniedException();
        }
        websiteUserAddressService.delete(websiteUserAddress);
        return new SimpleJsonResponse(true, null);
    }
    
    private SimpleJsonResponse checkUserAddress(String receiverName, int[] regions, String address, String mobile, String phone){
        if(receiverName.trim().isEmpty()){
            return new SimpleJsonResponse(false, "请填写收货人姓名！");
        }
        if(mobile.isEmpty() && phone.isEmpty()){
            return new SimpleJsonResponse(false, "请填写收货人的联系方式！");
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
    
    //设置为默认地址
    @RequestMapping(value = "/my/address/asDefault.php", method = RequestMethod.POST,produces = "text/html;charset=utf-8")
    public @ResponseBody SimpleJsonResponse addressAsDefault(@RequestParam("id") long id,Website website ,WebsiteUser websiteUser, HttpServletRequest request)
    {
        WebsiteUserAddress websiteUserAddress  = websiteUserAddressService.findOne(id);
        if (websiteUserAddress.getUserId() != websiteUser.getId())
        {
            throw new PermissionDeniedException();
        }
        websiteUserAddress.setAsDefault(true);
        websiteUserAddressService.save(websiteUser, websiteUserAddress);
        return new SimpleJsonResponse(true, null);
    }
    
}
