package com.lmf.integral.admin.market.controller;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lmf.common.ResponseResult;
import com.lmf.market.entity.Coupon;
import com.lmf.market.entity.UserCoupon;
import com.lmf.market.service.CouponService;
import com.lmf.market.service.UserCouponService;

@Controller
public class UserCouponController {

	@Autowired
	private UserCouponService userCouponService;
	
	@Autowired
	private CouponService couponService;
	
	
	@RequestMapping(value = "/mobile/receiveCoupon.php",method = RequestMethod.GET)
	public @ResponseBody ResponseResult receiveCoupon(@RequestParam(value = "userId", required = false)Long userId, 
			@RequestParam(value = "couponId", required = false)String couponId){
		Integer result = -1;
		if (StringUtils.isNotBlank(couponId)) {
			String[] coupon = couponId.split(",");
			for (String string : coupon) {
				Coupon couponCheck = couponService.findById(Integer.parseInt(string));
				if(couponCheck.getCount() <= 0){
					return new ResponseResult("-1", "优惠券已经被领完了！");
				}
				List<UserCoupon> check =userCouponService.selectByUserAndCoupon(userId,Integer.parseInt(string));
				if(CollectionUtils.isNotEmpty(check)){
					return new ResponseResult("-1", "已经领取过了哦~");
				}
				result = userCouponService.receiveCoupon(userId, Integer.parseInt(string));
			}
			if (result == 1) {
	    		return new ResponseResult("1", "领取成功");
			} else {
				return new ResponseResult("-1", "领取失败");
			}
		
		} 
		return new ResponseResult("-1", "优惠券异常");
		
	}
	
//	@RequestMapping(value = "/mobile/selectCoupon",method = RequestMethod.GET)
//	public @ResponseBody ResponseResult selectCoupon(@RequestParam(value = "targetType", required = false)Integer targetType, 
//			@RequestParam(value = "targetName", required = false)String targetName){
//		List<Coupon> coupons = userCouponService.selectCoupon(targetType, targetName);
//		return null;
//		
//	}
	
	
}
