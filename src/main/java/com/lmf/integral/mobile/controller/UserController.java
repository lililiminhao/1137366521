/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.mobile.controller;

import com.lmf.common.SerializableMultipartFile;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.SimpleImageInfo;
import com.lmf.enterprise.entity.Enterprise;
import com.lmf.enterprise.entity.EnterpriseUserMap;
import com.lmf.enterprise.service.EnterpriseExclusiveProductPoolService;
import com.lmf.enterprise.service.EnterpriseService;
import com.lmf.enterprise.service.EnterpriseUserMapService;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.market.entity.FenxiaoUser;
import com.lmf.market.service.FenXiaoUserService;
import com.lmf.order.enums.OrderStatus;
import com.lmf.order.service.OrderService;
import com.lmf.sys.service.InternalAttachmentService;
import com.lmf.website.entity.SpecialActivity;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.enums.SpecialStatus;
import com.lmf.website.service.SpecialActivityService;
import com.lmf.website.service.WebsiteUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author shenzhixiong
 */
@Controller("mobileUserController")
public class UserController {
    
    @Autowired
    private OrderService    orderService;
    
    @Autowired
    private WebsiteUserService websiteUserService;
    
    @Autowired
    private InternalAttachmentService internalAttachmentService;

    @Autowired
    private SpecialActivityService specialActivityService;

    @Autowired
    private EnterpriseUserMapService enterpriseUserMapService;

    @Autowired
    private EnterpriseService enterpriseService;
    
    @Autowired
    private FenXiaoUserService fenXiaoUserService;

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @RequestMapping(value = "/my/mobile/userCenter.php",method = RequestMethod.GET)
    public String userCenter(@RequestParam(value = "retUrl", required = false) String returnUrl, WebsiteUser user, Model model)
    {
        //用户找到企业 通过企业找到对应的活动和产品池
        if (user != null && user.getId() > 0) {
        	EnterpriseUserMap enterpriseUserMap = enterpriseUserMapService.getOneByUserId(user.getId());
            if(enterpriseUserMap != null && enterpriseUserMap.getProductPoolId() != null && enterpriseUserMap.getProductPoolId() > 0){
                Enterprise enterprise = enterpriseService.getOneById(enterpriseUserMap.getEnterpriseId());
                SpecialActivity specialActivity = specialActivityService.findOne(enterprise.getSpecialActivityId());
                if(specialActivity != null && specialActivity.getStatus() == SpecialStatus.on_line) {
                    model.addAttribute("enterpriseId", enterprise.getId());
                    model.addAttribute("specialActivityId", specialActivity.getId());
                }
            }
        }
        FenxiaoUser fenxiao = fenXiaoUserService.findByMobile(user.getMobile());
        if(fenxiao == null || (fenxiao.getStatus()!=null&&fenxiao.getStatus().intValue() == -1)){
        	model.addAttribute("fenxiao", -1);
        }else {
        	model.addAttribute("fenxiao", 1);
		}
        
        
        Map<OrderStatus, Long>  statusStatics   = orderService.getOrderStatics(user, null);
        model.addAttribute("waitingShipment", statusStatics.get(OrderStatus.waiting_shipment));
        model.addAttribute("okOrderCount", statusStatics.get(OrderStatus.completed));
        model.addAttribute("confirmOrderCount", statusStatics.get(OrderStatus.waiting_confirmed));
        model.addAttribute("user", websiteUserService.findOne(user.getId()));
        return "user_center/record";
    }



    @RequestMapping(value = "/mobile/user/image/uploead.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public  @ResponseBody SimpleJsonResponse uploeadImage(@RequestParam(value = "image") MultipartFile file, UserDetail currentUser) throws IOException
    {
        byte[]  bytes       = file.getBytes();
        SimpleImageInfo sif;
        try {
            sif = new SimpleImageInfo(bytes);
        } catch (IOException exp) {
            return new SimpleJsonResponse(false, "您上传的文件不是合法的图片格式" + exp);
        }
        String  src    = internalAttachmentService.save(new SerializableMultipartFile(file));
        WebsiteUser user = websiteUserService.findOne(currentUser.getUserId());
        user.setHeadPortrait(src);
        websiteUserService.save(user, false);
        return  new SimpleJsonResponse(true, src);
    }
    
    //  头像图片裁剪  
    @RequestMapping(value = "/user/cutHeadImage.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse cutImage(Model model, WebsiteUser websiteUser,
                                                     @RequestParam("image") MultipartFile file,
                                                     @RequestParam(value = "x", required = false) Integer x,
                                                     @RequestParam(value = "y", required = false) Integer y,
                                                     @RequestParam(value = "width", required = false) Integer width,
                                                     @RequestParam(value = "height", required = false) Integer height) throws IOException {

        if (websiteUser == null) {
             return new SimpleJsonResponse(false, "登录已过期，请重新登录");
        }
        
        if (file == null || file.isEmpty()) {
             return new SimpleJsonResponse(false, "请选择要上传的图片文件");
        }
        if (file.getSize() > 10485760) {
             return new SimpleJsonResponse(false, "图片大小不能超过10M");
        }
        if (x == null || y == null || width == null || height == null) {
            return new SimpleJsonResponse(false, "参数错误，图片上传失败");
        }

        x = x < 0 ? 0 : x;
        y = y < 0 ? 0 : y;
        width = width < 0 ? 0 : width;
        height = height < 0 ? 0 : height;
        
        try {
            String imgUrl = websiteUserService.editHeadPortrait(websiteUser, new SerializableMultipartFile(file), x, y, width, height);
            return new SimpleJsonResponse(true, imgUrl);
        } catch (Exception e) {
            logger.error("修改头像图片上传失败editHeadPortrait");
            logger.error(e.getMessage());
            return new SimpleJsonResponse(false, "图片上传失败");
        }
    }
    
    @RequestMapping(value = "/my/mobile/logout.php",method = RequestMethod.GET)
    public @ResponseBody SimpleJsonResponse logout(HttpServletRequest req,HttpServletResponse response){
    	HttpSession session = req.getSession(false);
    	if(session != null){
            session.removeAttribute("currentUser");
        }
    	Cookie cookie = new Cookie("wap_token", null);
    	cookie.setPath("/");
    	cookie.setMaxAge(0);
    	response.addCookie(cookie);
    	return new SimpleJsonResponse(true, "退出成功");
    }
    
}
