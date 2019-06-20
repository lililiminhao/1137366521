/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.controller;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.Range;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.PagerSpec;
import com.lmf.common.util.PagerUtil;
import com.lmf.openapi.entity.ForwardActivity;
import com.lmf.openapi.entity.ForwardGenerateLog;
import com.lmf.openapi.service.ForwardActivityService;
import com.lmf.openapi.service.ForwardGenerateLogService;
import com.lmf.openapi.vo.QRCodeActivityCriteria;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.service.WebsiteUserService;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author yh
 */
@Controller("adminForwardActivityController")
public class ForwardActivityController {
    
    @Autowired
    private TimeZone    tz;
    
    @Autowired
    private WebsiteUserService websiteUserService;
    
    @Autowired
    private ForwardActivityService forwardActivityService;
    
    @Autowired
    private ForwardGenerateLogService forGenerateLogService;
    
    @RequestMapping(value = "/admin/forward/list.php", method = RequestMethod.GET)
    public String list(@RequestParam(value = "kw", required = false) String keyword,
                       @RequestParam(value = "st", required = false) Date startTime,
                       @RequestParam(value = "et", required = false) Date endTime,
                       @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager,
                       Model model) throws UnsupportedEncodingException
    {
        Page<ForwardActivity> activitys = forwardActivityService.find(new QRCodeActivityCriteria().withKeyword(keyword).withActiveDate(new Range(startTime, null)).withExpireDate(null, endTime), pager);
        StringBuilder link = new StringBuilder("/jdvop/admin/forward/list.php?page=[:page]");
        if (keyword != null && !keyword.isEmpty())        
        {
            link.append("&kw=").append(URLEncoder.encode(keyword, "UTF-8"));
            model.addAttribute("kw", keyword);
        }
        
        if(startTime != null){
            link.append("&st=").append(startTime);
            model.addAttribute("st", startTime);
        }
        if(endTime != null){
            link.append("&et=").append(endTime);
            model.addAttribute("et", endTime);
        }
        model.addAttribute("tz", tz);
        model.addAttribute("link", link.toString());
        model.addAttribute("activitys", activitys);
        model.addAttribute("forGenerateLogService", forGenerateLogService);
        
        return "admin/forward_activities/list";
    }
    
    @RequestMapping(value = "/admin/forward/add.php", method = RequestMethod.GET)
    public String add(Model model)
    {
        return "admin/forward_activities/form";
    }
    
    @RequestMapping(value = "/admin/forward/add.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse<String> add(@RequestParam(value = "name", required = false) String name,
                                                        @RequestParam(value = "integral", required = false) Integer integral,
                                                        @RequestParam(value = "forwardUrl", required = false) String forwardUrl,
                                                        @RequestParam(value = "imageUrl", required = false) String imageUrl,
                                                        @RequestParam(value = "activityTime", required = false) Date activeDate,
                                                        @RequestParam(value = "expireTime", required = false) Date expireDate,
                                                        @RequestParam(value = "description", required = false) String description,
                                                        Website website)
    {
        if(name.trim().isEmpty()) {
            return new SimpleJsonResponse(false, "请输入活动名称！");
        } else {
            if(forwardActivityService.exists(name)){
                return new SimpleJsonResponse(false, "您已添加过该活动了，请勿重复添加");
            }
        }
        if(website.getExt() == null || website.getRatio() == null) {
            return new SimpleJsonResponse(false, "您尚未配置积分比例，请先配置积分比例！");
        }
        
        if(integral == null || integral <= 0){
            return new SimpleJsonResponse(false, "请输入奖励积分！");
        }
        
        if(activeDate == null) {
            return new SimpleJsonResponse(false, "请选择开始时间");
        }
        if(expireDate == null) {
            return new SimpleJsonResponse(false, "请选择结束时间");
        }
        if(expireDate.before(activeDate)) {
            return new SimpleJsonResponse(false, "活动结束时间不能小于开始时间！");
        }
        
        if(forwardUrl.trim().isEmpty()) {
            return new SimpleJsonResponse(false, "请填写需要转发的链接地址！");
        }
        if(forwardUrl.trim().contains("http://") || forwardUrl.trim().contains("https://"))
        {
            
        } else {
            forwardUrl = "http://" + forwardUrl;
        }
        
        if(activeDate.getTime() > expireDate.getTime()){
            return new SimpleJsonResponse(false, "开始时间不得大于截止时间");
        }
        
        ForwardActivity activity = new ForwardActivity();
        activity.setName(name);
        activity.setIntegral(integral);
        activity.setForwardUrl(forwardUrl);
        activity.setImage(imageUrl);
        activity.setDescription(description);
        activity.setActivityTime(activeDate);
        activity.setExpireTime(expireDate);
        forwardActivityService.save(activity);
        
        return new SimpleJsonResponse(true, null);
    }
    
    @RequestMapping(value = "/admin/forward/edit.php", method = RequestMethod.GET)
    public String edit(@RequestParam("id") long id, Model model)
    {
        model.addAttribute("activity", forwardActivityService.findOne(id));
        return "admin/forward_activities/form";
    }
    
    @RequestMapping(value = "/admin/forward/edit.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse<String> edit(@RequestParam("id") long id,
                                                         @RequestParam(value = "name", required = false) String name,
                                                         @RequestParam(value = "integral", required = false) Integer integral,
                                                         @RequestParam(value = "forwardUrl", required = false) String forwardUrl,
                                                         @RequestParam(value = "imageUrl", required = false) String imageUrl,
                                                         @RequestParam(value = "activityTime", required = false) Date activeDate,
                                                         @RequestParam(value = "expireTime", required = false) Date expireDate,
                                                         @RequestParam(value = "description", required = false) String description,
                                                         Website website)
    {
        ForwardActivity activity = forwardActivityService.findOne(id);
        if(activity == null) {
            return new SimpleJsonResponse(false, "数据错误，请重新操作");
        }
        if(name.trim().isEmpty()) {
            return new SimpleJsonResponse(false, "请输入活动名称！");
        } else {
            if(!activity.getName().trim().equals(name.trim())) {
                if(forwardActivityService.exists(name)){
                    return new SimpleJsonResponse(false, "您已添加过该活动了，请勿重复添加");
                }
            }
        }
        if(website.getExt() == null || website.getRatio() == null) {
            return new SimpleJsonResponse(false, "您尚未配置积分比例，请先配置积分比例！");
        }
        
        if(integral == null || integral <= 0){
            return new SimpleJsonResponse(false, "请输入奖励积分！");
        }
        
        if(activeDate == null) {
            return new SimpleJsonResponse(false, "请选择开始时间！");
        }
        if(expireDate == null) {
            return new SimpleJsonResponse(false, "请选择结束时间！");
        }
        if(expireDate.before(activeDate)) {
            return new SimpleJsonResponse(false, "活动结束时间不能小于开始时间！");
        }
        
        if(forwardUrl.trim().isEmpty()) {
            return new SimpleJsonResponse(false, "请填写需要转发的链接地址！");
        }
        if(forwardUrl.trim().contains("http://") || forwardUrl.trim().contains("https://"))
        {
            
        } else {
            forwardUrl = "http://" + forwardUrl;
        }
        
        if(activeDate.getTime() > expireDate.getTime()){
            return new SimpleJsonResponse(false, "开始时间不得大于截止时间");
        }
        
        activity.setName(name);
        activity.setIntegral(integral);
        activity.setForwardUrl(forwardUrl);
        activity.setImage(imageUrl);
        activity.setDescription(description);
        activity.setActivityTime(activeDate);
        activity.setExpireTime(expireDate);
        forwardActivityService.save(activity);
        
        return new SimpleJsonResponse(true, null);
    }
    
    @RequestMapping(value = "/admin/forward/delete.php", method = RequestMethod.GET)
    public @ResponseBody SimpleJsonResponse delete(@RequestParam("id") long id)
    {
        ForwardActivity activity = forwardActivityService.findOne(id);
        if(activity == null){
            return new SimpleJsonResponse(false, "数据错误，请重新操作");
        }
        forwardActivityService.delete(id);
        
        return new SimpleJsonResponse(true, null);
    }
    
    @RequestMapping(value = "/admin/forward/view.php", method = RequestMethod.GET)
    public String view(@RequestParam("id") long id,
                       @PagerSpecDefaults(pageSize = 10, sort = "time.desc") PagerSpec pager,
                       Model model)
    {
        ForwardActivity activity = forwardActivityService.findOne(id);
        model.addAttribute("activity", activity);
        
        long count = forGenerateLogService.count(id);
        model.addAttribute("count", count);
        
        Page<ForwardGenerateLog> consumeLogs = forGenerateLogService.find(id, pager);
        model.addAttribute("consumeLogs", consumeLogs);
        model.addAttribute("websiteUserService", websiteUserService);
        
        return "admin/forward_activities/view";
    }
    
    @RequestMapping(value = "/admin/forward/ajaxConsumeLogs.php", method = RequestMethod.GET, produces =  "text/html;charset=utf-8")
    public @ResponseBody Map<String, String> ajaxConsumeLogs(@RequestParam(value = "id")  Long id, Model model,
                                                             @PagerSpecDefaults(pageSize = 10, sort = "time.desc") PagerSpec pager)
    {
        
        String url = "/jdvop/admin/forward/ajaxConsumeLogs.php?id=" + id + "&page=[:page]";
        Page<ForwardGenerateLog> consumeLogs = forGenerateLogService.find(id, pager);
        String content = renderConsumeLogs(consumeLogs.getContent());
        
        String  link    = PagerUtil.createPagenation(url, consumeLogs.getPagerSpec(), 6, "_self");
        Map<String, String> ret = new HashMap<>();
        ret.put("content", content);
        ret.put("link", link);
        return ret;
    }
    
    private String renderConsumeLogs (List<ForwardGenerateLog> logs){
        
        StringBuilder sbd = new StringBuilder();
        for (ForwardGenerateLog log : logs)
        {
            renderConsumeLog(sbd, log);
        }
        return sbd.toString();
    }
    
    private StringBuilder renderConsumeLog(StringBuilder sbd, ForwardGenerateLog log)
    {
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        WebsiteUser user = websiteUserService.findOne(log.getUserId());
        sbd.append("<tr>");
        sbd.append("<td class=\"tc\">").append(user.getId()).append("</td>");
        sbd.append("<td class=\"tc\">").append(user.getNickName()).append("</td>");
        sbd.append("<td class=\"tc\">").append(sdf.format(log.getCreateTime())).append("</td>");
        sbd.append("<td class=\"tc\">").append(log.getIntegral()).append("</td>");
        sbd.append("</tr>");
        return sbd;
    }
    
}
