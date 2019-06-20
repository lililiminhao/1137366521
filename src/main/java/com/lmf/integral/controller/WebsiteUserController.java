/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.controller;

import com.lmf.activity.service.LotteryService;
import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.enums.Gender;
import com.lmf.common.util.PagerSpec;
import com.lmf.common.util.ValidUtil;
import com.lmf.order.service.OrderService;
import com.lmf.sys.entity.GeoRegion;
import com.lmf.sys.service.GeoRegionService;
import com.lmf.website.entity.IntegralConsumeLog;
import com.lmf.website.entity.IntegralGenerateLog;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.entity.WebsiteUserAddress;
import com.lmf.website.enums.IntegralConsumeType;
import com.lmf.website.service.IntegralService;
import com.lmf.website.service.WebsiteUserAddressService;
import com.lmf.website.service.WebsiteUserService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class WebsiteUserController {
    
    @Autowired
    private WebsiteUserService websiteUserService;
    
    @Autowired
    private IntegralService integralService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private WebsiteUserAddressService websiteUserAddressService;
    
    @Autowired
    private GeoRegionService geoRegionService;
    
    @Autowired
    private LotteryService lotteryService;
    
    @RequestMapping(value = "/my/profile.php", method = RequestMethod.GET)
    public String  myInformation(WebsiteUser websiteUser, Model model)
    {
        if(websiteUser == null){
            return "login";
        }  
        List<WebsiteUserAddress> listUserAddress = websiteUserAddressService.find(websiteUser);
        WebsiteUserAddress websiteUserAddress = null;
        if (listUserAddress != null && listUserAddress.size() > 0) {
            for (WebsiteUserAddress addr : listUserAddress) {
                if (addr.isAsDefault()) {
                    websiteUserAddress = addr;
                }
            }
        }
        model.addAttribute("websiteUserAddress", websiteUserAddress);
        model.addAttribute("user", websiteUser);
        return "user_center/information";
    }
    
    @RequestMapping(value = "/my/profile/edit.php", method = RequestMethod.GET)
    public String  editInformation(WebsiteUser user, Model model)
    {
        model.addAttribute("user", user);
        List<WebsiteUserAddress> listUserAddress = websiteUserAddressService.find(user);
        WebsiteUserAddress userAddress = null;
        if (listUserAddress != null && listUserAddress.size() > 0) {
            for (WebsiteUserAddress addr : listUserAddress) {
                if (addr.isAsDefault()) {
                    userAddress = addr;
                }
            }
        }
        model.addAttribute("provinces", geoRegionService.findAllProvince());
        model.addAttribute("geoRegionService", geoRegionService);
        model.addAttribute("userAddress", userAddress);
        model.addAttribute("genders", Gender.values());
        return "user_center/information_form";
    }
    
    @RequestMapping(value = "/my/profile/edit.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse editInformation(@RequestParam(value = "nickName", required = false) String nickName,
                                                            @RequestParam(value = "gender", required = false) Gender gender,
                                                            @RequestParam(value = "birthday", required = false) Date birthday,
                                                            @RequestParam(value = "mobile", required = false) String mobile,
                                                            @RequestParam(value ="email", required = false) String email,
                                                            @RequestParam(value = "phone", required = false) String phone,
                                                            @RequestParam(value = "address", required = false) String address,
                                                            @RequestParam(value = "userAddressId", required = false) Long userAddressId,
                                                            @RequestParam("regions[]") GeoRegion[] regions,
                                                            WebsiteUser user, Model model)
    {
        user.setNickName(nickName);
        if(mobile != null && !mobile.isEmpty()){
            if(!ValidUtil.isMobile(mobile)){
                return new SimpleJsonResponse(false, "请填写正确的手机号码");
            }
            user.setMobile(mobile);
        }
        user.setGender(gender);
        user.setDate1(birthday);
        
        if(email != null && !email.isEmpty()){
            if(!ValidUtil.isEmail(email)){
                return new SimpleJsonResponse(false, "请填写正确的邮箱地址");
            }
            user.setEmail(email);
        } 
        if(regions != null && regions.length > 1) {
            if(regions.length < 3 && regions[1] == null) {
                return new SimpleJsonResponse(false, "请选择完整地址！");
            } else if(regions[2] == null) {
                return new SimpleJsonResponse(false, "请选择完整地址！");
            } else if(regions.length == 4 && regions[3] == null) {
                return new SimpleJsonResponse(false, "请选择完整地址！");
            }
            WebsiteUserAddress userAddress = null;
            if (userAddressId != null && userAddressId > 0) {
                userAddress = websiteUserAddressService.findOne(userAddressId);
            }
            if (userAddress == null) {
                userAddress = new WebsiteUserAddress();
            }

            userAddress.setUserId(user.getId());
            userAddress.setProvince(regions[0]);
            userAddress.setCity(regions[1]);
            userAddress.setCounty(regions[2]);
            if(regions.length > 3) {
                userAddress.setTown(regions[3]);
            }
            userAddress.setAddress(address);
            userAddress.setAsDefault(true);
            userAddress.setReceiverName(user.getLoginName());
            userAddress.setMobile(mobile);
            websiteUserAddressService.save(user,userAddress);
        } else {
            if (userAddressId != null && userAddressId > 0) {
                WebsiteUserAddress userAddress = websiteUserAddressService.findOne(userAddressId);
                userAddress.setAsDefault(false);
                websiteUserAddressService.save(user,userAddress);
            } 
        }
        user = websiteUserService.save(user, false);
        model.addAttribute("user", user);
        return new SimpleJsonResponse(true, "数据保存成功");
    }
    
    @RequestMapping(value = "/my/consume/integrals.php", method = RequestMethod.GET)
    public String  myConsumeIntegral(@PagerSpecDefaults(pageSize = 12, sort = "time.desc") PagerSpec pager, WebsiteUser user, Model model)
    {
        String link = "/jdvop/my/consume/integrals.php?page=[:page]";
        Page<IntegralConsumeLog> pageLog = integralService.findConsumeLog(user, null, pager);
        
        model.addAttribute("user", user);
        model.addAttribute("consumeLogs", pageLog);
        model.addAttribute("link", link);
        model.addAttribute("orderService",orderService);
        model.addAttribute("lotteryService", lotteryService);
        
        return "user_center/consume_integral";
    }
    
    @RequestMapping(value = "/my/ajax/generate/integrals.php", method = RequestMethod.GET, params = "ajax", produces = "text/html;charset=UTF-8")
    public @ResponseBody Map<String, Object> ajaxGenerateIntegral(@PagerSpecDefaults(pageSize = 12, sort = "time.desc") PagerSpec pager, WebsiteUser user, Model model)
    {
        
        Page<IntegralGenerateLog> generateLogs =  integralService.findGenerateLog(user, null, null, pager);
        
        String content = renderGenerateLogs(generateLogs.getContent());
        
        Map<String, Object> ret = new HashMap<>();
        ret.put("content", content);
        ret.put("pagerSpec", generateLogs.getPagerSpec());
        return ret;
    }
    
    @RequestMapping(value = "/my/ajax/consume/integrals.php", method = RequestMethod.GET, params = "ajax", produces = "text/html;charset=UTF-8")
    public @ResponseBody Map<String, Object> ajaxConsumeIntegral(@PagerSpecDefaults(pageSize = 12, sort = "time.desc") PagerSpec pager, WebsiteUser user, Model model)
    {
        Page<IntegralConsumeLog> consumeLogs =  integralService.findConsumeLog(user, null, pager);
        
        String content = renderConsumeLogs(consumeLogs.getContent());
        
        Map<String, Object> ret = new HashMap<>();
        ret.put("content", content);
        ret.put("pagerSpec", consumeLogs.getPagerSpec());
        return ret;
    }
    
    private String renderGenerateLogs (List<IntegralGenerateLog> logs){
        
        StringBuilder sbd = new StringBuilder();
        for (IntegralGenerateLog log : logs)
        {
            renderGenerateLog(sbd, log);
        }
        return sbd.toString();
    }
    
    
    private StringBuilder renderGenerateLog(StringBuilder sbd, IntegralGenerateLog log){
        
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        sbd.append("<tr>");
        sbd.append("<td class=\"tc\">").append(sdf.format(log.getCreateTime())).append("</td>");
        sbd.append("<td class=\"tc\">").append(log.getDeltaAmount()).append("</td>");
        sbd.append("<td class=\"tc\">").append("<span>").append(log.getGenerateType().getDescription()).append("</span>").append("</td>");
        if(log.getReason() != null){
            sbd.append("<td class=\"tc\">").append(log.getReason()).append("</td>");
        }else{
            sbd.append("<td class=\"tc\"></td>");
        }
        
        sbd.append("</tr>");
        return sbd;
    }
    
    private String renderConsumeLogs (List<IntegralConsumeLog> logs){
        
        StringBuilder sbd = new StringBuilder();
        for (IntegralConsumeLog log : logs)
        {
            renderConsumeLog(sbd, log);
        }
        return sbd.toString();
    }
    
    
    private StringBuilder renderConsumeLog(StringBuilder sbd, IntegralConsumeLog log){
        
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sbd.append("<tr>");
        sbd.append("<td class=\"p145p\">").append(log.getConsumeType().getDescription()).append("</td>");
        sbd.append("<td class=\"p145p\">");
        if(log.getConsumeType() == IntegralConsumeType.orders){
            sbd.append("<a href = \"/my/mobile/order/").append(log.getConsumeId()).append(".php\" class=\"blue\">").append(orderService.findOne(log.getConsumeId()).getKey()).append("</a>");
        } else if(log.getConsumeType() == IntegralConsumeType.lottery){
            sbd.append(lotteryService.findOne(log.getConsumeId().intValue()).getLotteryName());
        }
        sbd.append("</td>");
        sbd.append("<td class=\"tc\">");
        sbd.append(sdf.format(log.getConsumeTime()));
        sbd.append("</td>");
        sbd.append("<td class=\"tc\">-");
        sbd.append(log.getDeltaAmount());
        sbd.append("</td>");
        sbd.append("</tr>");
        return sbd;
    }
    
}
