package com.lmf.integral.admin.market.controller;

import com.lmf.common.ResponseResult;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.market.entity.Coupon;
import com.lmf.market.entity.UserCoupon;
import com.lmf.market.service.CouponService;
import com.lmf.market.service.MobileCouponService;
import com.lmf.market.service.UserCouponService;
import com.lmf.market.vo.UserCenterCoupon;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class MobileCouponController {

	
	@Autowired
	private CouponService couponService;
	
	@Autowired
	private MobileCouponService mobileCouponService;
	
	@Autowired
	private UserCouponService userCouponService;
	
	@RequestMapping(value = "/mobile/product/findCoupon.php", method = RequestMethod.GET)
	public @ResponseBody ResponseResult findProductCoupon(@RequestParam(value = "targetId", required = false)Integer targetId,HttpServletRequest request){
		List<Coupon> coupon = null;
		HttpSession session = request.getSession();
		UserDetail currentUser = (UserDetail) session.getAttribute("currentUser");
		if (currentUser != null) {
			coupon = mobileCouponService.findProductCoupon2(targetId,currentUser.getUserId());
		} else {
			coupon = mobileCouponService.findProductCoupon2(targetId,0);
		}
		if(coupon!=null&&CollectionUtils.isNotEmpty(coupon)){
			return new ResponseResult("1" ,"",coupon);
		}else{
			return new ResponseResult("1",null,null);
		}
	}
	
	@RequestMapping(value = "/my/mobile/mycoupon.php", method = RequestMethod.GET)
	public String mycouponRedit(){
		return "/mobile/user_center/my_coupon";
	}
	
	/**
	 * @param request
	 * @param type 1 未使用  0 已使用  -1 已过期
	 * @return
	 */
	@RequestMapping(value = "/my/mobile/findCoupon.php", method = RequestMethod.GET)
	public @ResponseBody ResponseResult mycoupon(HttpServletRequest request,
			@RequestParam(value = "type", required = false)Integer type){
		HttpSession session = request.getSession();
		UserDetail currentUser = (UserDetail) session.getAttribute("currentUser");
		if (currentUser != null){
			List<UserCenterCoupon> userCoupons = userCouponService.queryUserCouponByStatus(currentUser.getUserId(),type);
			if(CollectionUtils.isEmpty(userCoupons)){
				return new ResponseResult("-1","你还没有优惠券！");
			}
    		return new ResponseResult("1","查询成功",userCoupons);
		}else {
			return new ResponseResult("-1","尚未登陆");
		}
	}
	
	/**
	 * @param request 专题优惠券
	 * @param activityId
	 * @return
	 */
	@RequestMapping(value = "/my/mobile/activityCoupon.php", method = RequestMethod.GET)
	public @ResponseBody ResponseResult specialCoupon(HttpServletRequest request,
			@RequestParam(value = "activityId", required = false)Integer activityId){
		HttpSession session = request.getSession();
		UserDetail currentUser = (UserDetail) session.getAttribute("currentUser");
		List<Coupon>  coupons =couponService.findByTarget(4, activityId);
		if (currentUser == null){
			return new ResponseResult("1","",coupons);
		}else {
			for (Coupon coupon : coupons) {
				List<UserCoupon> selectByUserAndCoupon = userCouponService.selectByUserAndCoupon((long)currentUser.getUserId(), coupon.getId());
				if(CollectionUtils.isNotEmpty(selectByUserAndCoupon)){
					coupon.setHasRevice(1);
				}
			}
			return new ResponseResult("-1","",coupons);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
