/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.activity.controller;

import com.lmf.common.SimpleJsonResponse;
import com.lmf.openapi.entity.QRCode;
import com.lmf.openapi.entity.QRCodeActivity;
import com.lmf.openapi.entity.QRCodeConsumeLog;
import com.lmf.openapi.enums.OpenAPIUserType;
import com.lmf.openapi.enums.QRCodeConsumeType;
import com.lmf.openapi.service.QRCodeActivityService;
import com.lmf.openapi.service.QRCodeConsumeLogService;
import com.lmf.openapi.service.QRCodeService;
import com.lmf.openapi.wechat.entity.WechatUser;
import com.lmf.openapi.wechat.service.WechatUserService;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.enums.TokenStatus;
import java.util.TimeZone;
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
public class QRCodeController {
    
    
    @Autowired
    private QRCodeService qRCodeService;
    
    @Autowired
    private QRCodeActivityService qRCodeActivityService;
    
    @Autowired
    private QRCodeConsumeLogService qRCodeConsumeLogService;
    
    @Autowired
    private TimeZone    tz;
    
    @Autowired
    private WechatUserService wechatUserService;
     
    @RequestMapping(value = "/qRCode/use.php", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public @ResponseBody SimpleJsonResponse use(@RequestParam("token") String token,
                                                Website website,
                                                Model model,
                                                WebsiteUser websiteUser,
                                                HttpSession session,
                                                HttpServletRequest request){
        try {
            WechatUser wechatUser = (WechatUser) session.getAttribute("wechat_user");
            if(wechatUser == null){
                return new SimpleJsonResponse(false, "noBinding");
            }
 
            QRCode qRCode = qRCodeService.findOne(token);
            if(qRCode == null){
                return new SimpleJsonResponse(false, "systemError");
            }

            QRCodeActivity activity = qRCodeActivityService.findOne(qRCode.getActivityId());
            if(activity == null){
                return new SimpleJsonResponse(false, "systemError");
            }
            
            if(qRCode.getStatus() == TokenStatus.used) {
                return new SimpleJsonResponse(false, "delivered");
            }
            
            if (activity.isExpired(tz) || qRCode.getStatus() != TokenStatus.active) {
                return new SimpleJsonResponse(false, "alreadyOver");
            }
            
            if (!activity.isActive(tz))
            {
                return new SimpleJsonResponse(false, "notStarted");
            }
            if(websiteUser != null && wechatUserService.isMapped(wechatUser, websiteUser)) {
                QRCodeConsumeLog csl = qRCodeConsumeLogService.findOne(qRCode.getId(), OpenAPIUserType.wechat, wechatUser.getId(), websiteUser.getId());
                if(csl != null){
                    return new SimpleJsonResponse(false, "delivered");
                }
                if (activity.getConsumeType() == QRCodeConsumeType.per_openapi_user) {
                    if (qRCodeConsumeLogService.hasConsumed(qRCode.getId(), OpenAPIUserType.wechat, wechatUser.getId())) {
                        return new SimpleJsonResponse(false, "delivered");
                    }
                } else if (activity.getConsumeType() == QRCodeConsumeType.per_user) {
                    if (qRCodeConsumeLogService.hasConsumed(qRCode.getId(), websiteUser.getId())) {
                        return new SimpleJsonResponse(false, "delivered");
                    }
                } else if (activity.getConsumeType() == QRCodeConsumeType.fixed_times) {
                    long    consumeTimes    = qRCodeConsumeLogService.getConsumedTimes(qRCode.getId());
                    int maxTimes    = activity.getAllowConsumeTime();
                    if (maxTimes <= consumeTimes) {
                        return new SimpleJsonResponse(false, "picked");
                    }
                }
                qRCodeService.use(websiteUser, wechatUser, qRCode, website, request.getRemoteAddr());
                QRCodeConsumeLog    newCsl = qRCodeConsumeLogService.findOne(qRCode.getId(), OpenAPIUserType.wechat, wechatUser.getId(), websiteUser.getId());
                return new SimpleJsonResponse(true, newCsl.getRewardValue());
            } else {
                QRCodeConsumeLog csl = qRCodeConsumeLogService.findOne(qRCode.getId(), OpenAPIUserType.wechat, wechatUser.getId());
                if(csl != null){
                    return new SimpleJsonResponse(false, "delivered");
                }
                if (activity.getConsumeType() == QRCodeConsumeType.per_openapi_user) {
                    if (qRCodeConsumeLogService.hasConsumed(qRCode.getId(), OpenAPIUserType.wechat, wechatUser.getId())) {
                        return new SimpleJsonResponse(false, "delivered");
                    }
                } else if (activity.getConsumeType() == QRCodeConsumeType.per_user) {
                    return new SimpleJsonResponse(false, "delivered");
                } else if (activity.getConsumeType() == QRCodeConsumeType.fixed_times) {
                    long    consumeTimes    = qRCodeConsumeLogService.getConsumedTimes(qRCode.getId());
                    int maxTimes    = activity.getAllowConsumeTime();
                    if (maxTimes <= consumeTimes) {
                        return new SimpleJsonResponse(false, "picked");
                    }
                }
                qRCodeService.noBindingUse(wechatUser, qRCode, website, request.getRemoteAddr());
                QRCodeConsumeLog    newCsl = qRCodeConsumeLogService.findOne(qRCode.getId(), OpenAPIUserType.wechat, wechatUser.getId());
                return new SimpleJsonResponse(true, newCsl.getRewardValue());
            }
        }catch(Exception e){
            e.printStackTrace();
            return new SimpleJsonResponse(false, "systemError");
        } finally {
            if(session.getAttribute("isActivityLogin") != null) {
                session.removeAttribute("isActivityLogin");
            }
        }
//        if(activity.getRedirectUrl() == null){
//            return new SimpleJsonResponse(true, "");
//        }else{
//            return new SimpleJsonResponse(true, activity.getRedirectUrl());
//        }
    }
    
    
//    @RequestMapping(value = "/my/qRCode/use.php", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
//    public @ResponseBody SimpleJsonResponse use(@RequestParam("token") String token,
//                                                Website website,
//                                                Model model,
//                                                WebsiteUser websiteUser,
//                                                HttpSession session,
//                                                HttpServletRequest request){
//        try {
//            WechatUser wechatUser = (WechatUser) session.getAttribute("wechat_user");
//            if(wechatUser == null){
//                return new SimpleJsonResponse(false, "noBinding");
//            }
// 
//            if(session.getAttribute("isActivityLogin") != null) {
//                boolean isActivityLogin = (boolean) session.getAttribute("isActivityLogin");
//                if(isActivityLogin) {
//                    if(!wechatUserService.isMapped(wechatUser, websiteUser)) {
//                        wechatUserService.map(wechatUser, websiteUser);
//                    }
//                }
//            }
//            QRCode qRCode = qRCodeService.findOne(token);
//            if(qRCode == null){
//                return new SimpleJsonResponse(false, "systemError");
//            }
//
//            QRCodeActivity activity = qRCodeActivityService.findOne(qRCode.getActivityId());
//            if(activity == null){
//                return new SimpleJsonResponse(false, "systemError");
//            }
//            
//            if(qRCode.getStatus() == TokenStatus.used) {
//                return new SimpleJsonResponse(false, "delivered");
//            }
//            
//            if (activity.isExpired(tz) || qRCode.getStatus() != TokenStatus.active) {
//                return new SimpleJsonResponse(false, "alreadyOver");
//            }
//            
//            if (!activity.isActive(tz))
//            {
//                return new SimpleJsonResponse(false, "notStarted");
//            }
//
//            QRCodeConsumeLog    csl = qRCodeConsumeLogService.findOne(qRCode.getId(), OpenAPIUserType.wechat, wechatUser.getId(), websiteUser.getId());
//            if(csl != null){
//                return new SimpleJsonResponse(false, "delivered");
//            }
//
//            if (activity.getConsumeType() == QRCodeConsumeType.per_openapi_user) {
//                if (qRCodeConsumeLogService.hasConsumed(qRCode.getId(), OpenAPIUserType.wechat, wechatUser.getId())) {
//                    return new SimpleJsonResponse(false, "delivered");
//                }
//            } else if (activity.getConsumeType() == QRCodeConsumeType.per_user) {
//                if (qRCodeConsumeLogService.hasConsumed(qRCode.getId(), websiteUser.getId())) {
//                    return new SimpleJsonResponse(false, "delivered");
//                }
//            } else if (activity.getConsumeType() == QRCodeConsumeType.fixed_times) {
//                long    consumeTimes    = qRCodeConsumeLogService.getConsumedTimes(qRCode.getId());
//                int maxTimes    = activity.getAllowConsumeTime();
//                if (maxTimes <= consumeTimes) {
//                    return new SimpleJsonResponse(false, "picked");
//                }
//            }
//            qRCodeService.use(websiteUser, wechatUser, qRCode, website, request.getRemoteAddr());
//            QRCodeConsumeLog    newCsl = qRCodeConsumeLogService.findOne(qRCode.getId(), OpenAPIUserType.wechat, wechatUser.getId(), websiteUser.getId());
//            return new SimpleJsonResponse(true, newCsl.getRewardValue());
//        }catch(Exception e){
//            return new SimpleJsonResponse(false, "systemError");
//        } finally {
//            if(session.getAttribute("isActivityLogin") != null) {
//                session.removeAttribute("isActivityLogin");
//            }
//        }
////        if(activity.getRedirectUrl() == null){
////            return new SimpleJsonResponse(true, "");
////        }else{
////            return new SimpleJsonResponse(true, activity.getRedirectUrl());
////        }
//    }
}
