/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.controller;

import com.lmf.activity.entity.Lottery;
import com.lmf.activity.service.LotteryService;
import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.PagerSpec;
import com.lmf.common.util.PagerUtil;
import com.lmf.order.service.OrderService;
import com.lmf.website.entity.*;
import com.lmf.website.enums.IntegralConsumeType;
import com.lmf.website.service.*;
import com.lmf.website.vo.WebsiteUserCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author shenzhixiong
 */
@Controller("adminWebsiteUserController")
public class WebsiteUserController {
    
    @Autowired
    private WebsiteUserService websiteUserService;
    
    @Autowired
    private WebsiteUserGroupService websiteUserGroupService;
  
    @Autowired
    private IntegralService websiteIntegralService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private WebsiteUserAddressService websiteUserAddressService;
    
    @Autowired
    private WebsiteAdministratorService websiteAdministratorService;
    
    @Autowired
    private LotteryService lotteryService;
    
    private final static Logger logger  = LoggerFactory.getLogger(WebsiteUserController.class);
    
    @RequestMapping(value = "/admin/websiteUser/list.php", method = RequestMethod.GET)
    public  String list(@RequestParam(value = "kw", required = false) String keyword,
                        @RequestParam(value = "groupId", required = false) Integer groupId,
                        @RequestParam(value = "enabled", defaultValue = "true") Boolean enabled,
                        @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager,
                        Website website, Model model) throws UnsupportedEncodingException{
        
        StringBuilder link = new StringBuilder("/jdvop/admin/websiteUser/list.php?page=[:page]");
        if (keyword != null && !keyword.isEmpty()) {
            link.append("&kw=").append(URLEncoder.encode(keyword, "UTF-8"));
            model.addAttribute("kw", keyword);
        }
        
        if(groupId != null) {
            link.append("&groupId=").append(groupId);
            model.addAttribute("groupId", groupId);
        }
        link.append("&enabled=").append(enabled);
        
        Page<WebsiteUser> websiteUsers = websiteUserService.find(new WebsiteUserCriteria().withKeyword(keyword).withGroupId(groupId).withEnabled(enabled), pager);
        List<Long> websiteUserIds = new ArrayList();
        for(WebsiteUser websiteUser : websiteUsers.getContent()) {
            websiteUserIds.add(websiteUser.getId());
        }
        
        model.addAttribute("link", link.toString());
        model.addAttribute("websiteUsers", websiteUsers);
        model.addAttribute("enabled", enabled);
        model.addAttribute("generateIntegralMap", websiteIntegralService.getGenerateIntegrals(websiteUserIds));
        model.addAttribute("consunmeIntegralMap", websiteIntegralService.getConsunmeIntegrals(websiteUserIds));
        model.addAttribute("websiteUserService", websiteUserService);
        
        model.addAttribute("groups", websiteUserGroupService.findAll());
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
        model.addAttribute("nowTime", sdf.format(new Date()));
        
        return "admin/user/list";
    }
    
    @RequestMapping(value = "/admin/websiteUser/setEnabled.php",method = RequestMethod.GET,produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse setEnabled(@RequestParam("ids[]") Long [] ids,
                                                       @RequestParam("enabled") Boolean enabled,
                                                       Website website){
        if (ids.length < 1) {
            return new SimpleJsonResponse(false, "请选择您要操作的会员");
        }
        boolean skipError   = true;
        if (ids.length == 1) {
            skipError   = false;
        }
        for (long id : ids) {
            WebsiteUser user = websiteUserService.findOne(id);
            if(user == null){
                if(skipError){
                    continue;
                }else{
                    return new SimpleJsonResponse(false, "数据异常，请联系管理员");
                }
            }
            user.setEnabled(enabled);
            websiteUserService.setEnable(user);
        }
        return new SimpleJsonResponse(true, null);
    }
   
    @RequestMapping(value = "/admin/websiteUser/delete.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse delete(@RequestParam("ids[]") Long [] ids,
                                                   Website website,
                                                   WebsiteAdministrator admin,
                                                   HttpServletRequest request)
    {
        if (ids.length < 1) {
            return new SimpleJsonResponse(false, "请选择您要操作的会员");
        }
        boolean skipError   = true;
        if (ids.length == 1) {
            skipError   = false;
        }
        WebsiteUser user;
        for (long id : ids) {
            user = websiteUserService.findOne(id);
            if (user == null ) {
                if(skipError){
                    continue;
                }else{
                    return new SimpleJsonResponse(false, "数据异常，请联系管理员");
                }
            }
            if (!websiteUserService.isDeleteable(user))  {
                if(skipError){
                    continue;
                }else{
                    return new SimpleJsonResponse(false,"该用户可能存在订单等信息，无法被删除！");
                }
            }
            websiteUserService.delete(user, admin, request.getRemoteAddr(), null);
        }
        return new SimpleJsonResponse(true, null);
    }
    
    @RequestMapping(value = "/admin/websiteUser/view.php", method = RequestMethod.GET)
    public  String  view(@RequestParam(value = "id")  Long id, 
                         @PagerSpecDefaults(pageSize = 10, sort = "time.desc") PagerSpec pager,
                         Model model){
        WebsiteUser websiteUser = websiteUserService.findOne(id);
        
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
        
        String link = "/jdvop/admin/websiteUser/view.php?page=[:page]&id=" + websiteUser.getId();
        
        Page<IntegralGenerateLog> generateIntegralLogs = websiteIntegralService.findGenerateLog(websiteUser, null, null, pager);
        model.addAttribute("generateIntegralLogs", generateIntegralLogs);
        
        Page<IntegralConsumeLog> consumeIntegralLogs = websiteIntegralService.findConsumeLog(websiteUser, null, new PagerSpec(1, 0, 10));
        model.addAttribute("consumeIntegralLogs", consumeIntegralLogs);
        
        model.addAttribute("link", link);   
        model.addAttribute("websiteUser",websiteUser);
        model.addAttribute("websiteIntegralService", websiteIntegralService);
        model.addAttribute("orderService", orderService);
        model.addAttribute("websiteAdministratorService", websiteAdministratorService);
        model.addAttribute("websiteUserGroupService", websiteUserGroupService);
        
        return "admin/user/view";
    }
    
    @RequestMapping(value = "/admin/websiteUser/search/ajaxGenerateLogs.php", method = RequestMethod.GET, produces =  "text/html;charset=utf-8")
    public @ResponseBody Map<String, String> ajaxGenerateLogsPagenation(@RequestParam(value = "id")  Long id,
                                                                        Model model,
                                                                        @PagerSpecDefaults(pageSize = 10, sort = "time.desc") PagerSpec pager){
        WebsiteUser websiteUser = websiteUserService.findOne(id);
        
        String url = "/jdvop/admin/websiteUser/search/ajaxGenerateLogs.php?id=" + websiteUser.getId() + "&page=[:page]";
        
        Page<IntegralGenerateLog> generateIntegralLogs = websiteIntegralService.findGenerateLog(websiteUser, null, null, pager);
        
        String content = renderGenerateLogs(generateIntegralLogs.getContent());
        
        String  link    = PagerUtil.createPagenation(url, generateIntegralLogs.getPagerSpec(), 6, "_self");
        Map<String, String> ret = new HashMap<>();
        ret.put("content", content);
        ret.put("link", link);
        return ret;
    }
    
    private String renderGenerateLogs (List<IntegralGenerateLog> logs){
        
        StringBuilder sbd = new StringBuilder();
        Integer allIntegral = 0;
        for (IntegralGenerateLog log : logs)
        {
            allIntegral += log.getDeltaAmount();
            renderGenerateLog(sbd, log);
        }
        sbd.append("<tr><td colspan=\"6\" class=\"fs18p tr pr20\">合计:<i class=\"red ffa fwb\">").append(allIntegral).append("</i>  分</td></tr>");
        return sbd.toString();
    }
    
    
    private StringBuilder renderGenerateLog(StringBuilder sbd, IntegralGenerateLog log){
        
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        sbd.append("<tr>");
        sbd.append("<td class=\"tc\">").append(sdf.format(log.getCreateTime())).append("</td>");
        sbd.append("<td class=\"tc\">").append(log.getDeltaAmount()).append("</td>");
        if(log.getReason() != null){
            sbd.append("<td class=\"tc\">").append(log.getReason()).append("</td>");
        }else{
            sbd.append("<td class=\"tc\"></td>");
        }
        sbd.append("<td class=\"tc\">").append("<span>").append(log.getGenerateType().getDescription()).append("</span>").append("</td>");
        sbd.append("<td class=\"tc\">");
        if(log.getOperationLog().get("OPERATE_WEBSITE_ADMIN_REALNAME") != null) {
            sbd.append(log.getOperationLog().get("OPERATE_WEBSITE_ADMIN_REALNAME"));    
        } else {
            if(log.getOperationLog().get("OPERATE_WEBSITE_ADMIN_LOGIN_NAME") != null) {
                sbd.append(log.getOperationLog().get("OPERATE_WEBSITE_ADMIN_LOGIN_NAME"));
            } else {
                sbd.append("系统");
            }
        }
        sbd.append("</td>");
        sbd.append("<td class=\"tc\"><a href=\"/admin/integral/revokeGenerateIntegral.php?ids[]=$!log.id\" method=\"GET\" class=\"ajax-request\" ok-message=\"reload\" cfm-message=\"您确定要撤回该会员的$!{log.deltaAmount}积分吗？\">撤回积分</a></td>");
        sbd.append("</tr>");
        return sbd;
    }
    
    
    @RequestMapping(value = "/admin/websiteUser/search/ajaxConsumeIntegralLogs.php", method = RequestMethod.GET, produces =  "text/html;charset=utf-8")
    public  @ResponseBody Map<String, String> ajaxConsumeIntegralLogsPagenation(@RequestParam(value = "id")  Long id,
                                                                                Model model,
                                                                                @PagerSpecDefaults(pageSize = 10, sort = "time.desc") PagerSpec pager){
        WebsiteUser websiteUser = websiteUserService.findOne(id);
        
        String url = "/jdvop/admin/websiteUser/search/ajaxConsumeIntegralLogs.php?id=" + websiteUser.getId() + "&page=[:page]";
        
        Page<IntegralConsumeLog> consumeLogs = websiteIntegralService.findConsumeLog(websiteUser, null, pager);
        
        String content = renderConsumeIntegralLog(consumeLogs.getContent());
        
        String  link    = PagerUtil.createPagenation(url, consumeLogs.getPagerSpec(), 6, "_self");
        Map<String, String> ret = new HashMap<>();
        ret.put("content", content);
        ret.put("link", link);
        return ret;
    }
    
    private String renderConsumeIntegralLog (List<IntegralConsumeLog> logs){
        
        StringBuilder sbd = new StringBuilder();
        Integer allIntegral = 0;
        for (IntegralConsumeLog log : logs)
        {
             allIntegral += log.getDeltaAmount();
            renderConsumeIntegralLog(sbd, log);
        }
        sbd.append("<tr><td colspan=\"6\" class=\"fs18p tr pr20\">合计:<i class=\"red ffa fwb\">").append(allIntegral).append("</i>  分</td></tr>");
        return sbd.toString();
    }
    
    
    private StringBuilder renderConsumeIntegralLog(StringBuilder sbd, IntegralConsumeLog log){
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        sbd.append("<tr>");
        sbd.append("<td class=\"tc\">").append(log.getConsumeType().getDescription()).append("</td>");
        if(log.getConsumeType() == IntegralConsumeType.orders) {
            sbd.append("<td class=\"tc\">").append(orderService.findOne(log.getConsumeId()).getKey()).append("</td>");
        } else if(log.getConsumeType() == IntegralConsumeType.lottery) {
            if(log.getConsumeId() != null ){
                Lottery lottery =  lotteryService.findOne(log.getConsumeId().intValue());
                if(lottery != null) {
                    sbd.append("<td class=\"tc\">").append(lottery.getLotteryName()).append("</td>");
                } else {
                    sbd.append("<td class=\"tc\">抽奖活动</td>");
                }
            } 
        }
        sbd.append("<td class=\"tc\">").append(sdf.format(log.getConsumeTime())).append("</td>");
        sbd.append("<td class=\"tc\">").append(log.getDeltaAmount()).append("</td>");
        sbd.append("</tr>");
        
        return sbd;
    }
}
