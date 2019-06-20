package com.lmf.integral.controller;

import com.lmf.enterprise.entity.Enterprise;
import com.lmf.enterprise.entity.EnterpriseEmployee;
import com.lmf.enterprise.entity.EnterpriseUserMap;
import com.lmf.enterprise.service.EnterpriseEmployeeService;
import com.lmf.enterprise.service.EnterpriseService;
import com.lmf.enterprise.service.EnterpriseUserMapService;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.market.entity.Coupon;
import com.lmf.market.entity.UserCoupon;
import com.lmf.market.entity.VisitLog;
import com.lmf.market.repository.CouponDao;
import com.lmf.market.service.CouponService;
import com.lmf.market.service.UserCouponService;
import com.lmf.website.entity.SpecialActivity;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteCustomBlock;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.enums.SpecialActivityType;
import com.lmf.website.service.SpecialActivityService;
import com.lmf.website.service.WebsiteCustomBlockService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author shenzhixiong
 *
 */
@Controller("mobileSpecialActivityController")
public class SpecialActivityController {
	
	@Autowired
    private SpecialActivityService specialActivityService;
	
	@Autowired
    private WebsiteCustomBlockService websiteCustomBlockService;
	
	@Autowired
    private EnterpriseService enterpriseService;
	
	@Autowired
    private EnterpriseUserMapService enterpriseUserMapService;
    
    @Autowired
    private EnterpriseEmployeeService enterpriseEmployeeService;
    
    @Autowired
    private CouponService couponService;
    
    @Autowired
    private UserCouponService usercouponService;
	
	//进入专题活动
    @RequestMapping(value="/specialActivity.php", method = RequestMethod.GET)
    public String specialActivity(@RequestParam(value = "id", required = false) Integer specialActivityId,
                                   Website website, Model model,HttpServletRequest request)
    {
        //根据专题活动ID 查找数据
        SpecialActivity activity = specialActivityService.findOne(specialActivityId);
        if(activity == null){
        	model.addAttribute("activity", activity);
            return "specialActivity";
        }
        
        //根据专题活动 查找block 数据
        List<WebsiteCustomBlock> blocks = websiteCustomBlockService.findCustomBlock(specialActivityId);
        if(activity.getOnLineTime() != null || activity.getOffLineTime() != null){
			if(activity.getOffLineTime().before(new Date())){ //下线时间在当前之前
				String line = "Off_line";
				specialActivityService.setStatus(specialActivityId, line);
			}
		}
        
        
        //给block添加优惠券
        if(CollectionUtils.isNotEmpty(blocks)){
        	for (WebsiteCustomBlock websiteCustomBlock : blocks) {
        		if(StringUtils.isNotBlank(websiteCustomBlock.getCouponCodes())){
        			List<Coupon> coupons = new ArrayList<Coupon>();
        			String[] couponCodes = websiteCustomBlock.getCouponCodes().split(",");
        			for (String code : couponCodes) {
        				Coupon coupon = couponService.findByCode(code);
        				if(coupon != null){
        					HttpSession session = request.getSession();
            			    UserDetail currentUser = (UserDetail) session.getAttribute("currentUser");
            			    if(currentUser != null){
            			    	List<UserCoupon> selectByUserAndCoupon = usercouponService.selectByUserAndCoupon((long)currentUser.getUserId(), coupon.getId());
            			    	if(CollectionUtils.isNotEmpty(selectByUserAndCoupon)){
            			    		coupon.setHasRevice(1);
            			    	}
            			    }
        				}
        				coupons.add(coupon);
        				
        			}
        			
        			websiteCustomBlock.setCoupon(coupons);
        		}
    		}
        }
       
       
        model.addAttribute("blocks", blocks);
        model.addAttribute("special", activity);// 专题活动
		model.addAttribute("date", Calendar.getInstance().getTime());
        return "specialActivity";
    }

    //企业专题进入
    @RequestMapping(value="/enterpriseActivity.php", method = RequestMethod.GET)
    public String enterpriseActivity(@RequestParam(value ="enterpriseId", required = false)Integer enterpriseId,
                                	 Website website, WebsiteUser user, Model model) {
        //根据enterpriseId 查找企业数据
        Enterprise enterprise = enterpriseService.getOneById(enterpriseId);
        if(enterprise == null || enterprise.getSpecialActivityId() == null || enterprise.getSpecialActivityId() < 1){
        	return "index";  //跳转到首页
        }
        
        //根据专题活动ID 查找数据
        SpecialActivity activity = specialActivityService.findOne(enterprise.getSpecialActivityId());
        if(activity == null || activity.getType() != SpecialActivityType.enterpriseSpecial){
            return "index";   //跳转首页
        }
        
        if (user == null || user.getId() <= 0) {
            //如果当前未登陆,则跳转至登陆页面
            return "redirect:/login.php?retUrl=/enterpriseActivity.php?enterpriseId="+enterpriseId;
        }
        
        //根据用户ID 查找数据
        EnterpriseUserMap userMap = enterpriseUserMapService.getOneByUserId(user.getId());
        if(userMap == null){
        	userMap = new EnterpriseUserMap();
        	EnterpriseEmployee employee = enterpriseEmployeeService.selectOneByMobile(user.getMobile());
        	if(employee == null){
        		employee = new EnterpriseEmployee();
            	employee.setName(user.getNickName());
            	employee.setMobile(user.getMobile());
            	employee.setEnterpriseId(enterpriseId);  
            	employee = enterpriseEmployeeService.insert(employee);
        	}
        	
        	//创建企业员工的同事  insert 这个表 EnterpriseUserMap 数据
        	userMap.setUserId(user.getId());
        	userMap.setEnterpriseEmployeeId(employee.getId());
        	enterpriseUserMapService.save(userMap);
        }else{
        	//查询企业该用户 跟访问的企业ID是否匹配
        	EnterpriseEmployee employee = enterpriseEmployeeService.selectOne(userMap.getEnterpriseEmployeeId());
        	if(employee.getEnterpriseId() != enterpriseId.longValue()){
        		return "index";   //不匹配 跳转首页
        	}
        }
        
       //根据专题活动 查找block 数据
        List<WebsiteCustomBlock> blocks = websiteCustomBlockService.findCustomBlock(activity.getId());
        if(activity.getOnLineTime() != null || activity.getOffLineTime() != null){
			if(activity.getOffLineTime().before(new Date())){ //下线时间在当前之前
				String line = "Off_line";
				specialActivityService.setStatus(activity.getId(), line);
			}
		}
        
        model.addAttribute("blocks", blocks);
        model.addAttribute("special", activity);// 专题活动
		model.addAttribute("date", Calendar.getInstance().getTime());
    	
    	return "specialActivity";
    }
    

    @RequestMapping(value = "/admin/special/ajax.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public @ResponseBody List<SpecialActivity> childs(@RequestParam("parentID") int parentId,
                                                      @RequestParam(value = "type", defaultValue = "enterpriseSpecial") SpecialActivityType type)
    {
        if(parentId == 0){
            return Collections.EMPTY_LIST;
        }
        List<SpecialActivity> specialActivities = specialActivityService.find(parentId, type);
        if (specialActivities == null)
        {
            specialActivities  = Collections.EMPTY_LIST;
        }
        return specialActivities;
    }

}

