package com.lmf.integral.admin.market.controller;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.ResponseResult;
import com.lmf.common.util.PagerSpec;
import com.lmf.integral.admin.shiro.WebsiteAdministratorPrincipal;
import com.lmf.market.entity.Coupon;
import com.lmf.market.repository.vo.CouponCriteria;
import com.lmf.market.service.CouponService;
import com.lmf.market.service.UserCouponService;
import com.lmf.market.vo.UserCenterCoupon;
import com.lmf.product.vo.OpenApiProductVo;
import com.lmf.website.entity.SpecialActivity;
import com.lmf.website.entity.UserDefinedCate;
import com.lmf.website.entity.WebsiteAdministrator;
import com.lmf.website.service.UserDefinedCateService;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Controller
public class CouponController {

	@Autowired
	private CouponService couponService;
	@Autowired
	private UserCouponService userCouponService;
	
	@Autowired
	private UserDefinedCateService userDefinedCateService;
	
    /**
     *  添加优惠券
     * @param type
     * @param targetId
     * @param targetType
     * @param name
     * @param startTime
     * @param endTime
     * @param amount
     * @param usable
     * @param targetName
     * @param couponCode
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = "/admin/coupon/add.php", method = RequestMethod.POST)
    public @ResponseBody ResponseResult add(@RequestParam(value = "type", required = false)Integer type, 
    		@RequestParam(value = "targetId", required = false)Integer targetId, 
    		@RequestParam(value = "targetType", required = false)Integer targetType,
    		@RequestParam(value = "name", required = false)String name,
    		@RequestParam(value = "startTime", required = false)String startTime, 
    		@RequestParam(value = "endTime", required = false)String endTime, 
    		@RequestParam(value = "amount", required = false)String amount, 
    		@RequestParam(value = "count", required = false)Integer count, 
    		@RequestParam(value = "usable", required = false)String usable,
    		@RequestParam(value = "targetName", required = false)String targetName,
    		@RequestParam(value = "couponCode", required = false)String couponCode) throws ParseException {
    	if (type == null || type == 0) {
			return new ResponseResult("-1", "优惠券所属类型不能为空");
		}
    	if (StringUtils.isBlank(amount)) {
			return new ResponseResult("-1", "优惠券金额不能为空");
		}
    	
    	if (count == null) {
			return new ResponseResult("-1", "请填写优惠券数量");
		}
    	if (targetId == null || targetId == 0) {
			return new ResponseResult("-1", "优惠券所属范围不能为空");
		}
    	if(StringUtils.isEmpty(targetName)){
			return new ResponseResult("-1", "优惠券所属范围名称不能为空");
		}
    	
    	if(type == 1){
    		if(StringUtils.isBlank(usable)&& "0".equals(usable) ){
    			return new ResponseResult("-1", "优惠券满减金额不得为0！");
    		}
    		if (!usable.matches("^\\d+$$")) {
    			return new ResponseResult("-1", "满减使用条件为整数");
    		}
    		if(new BigDecimal(amount).compareTo(new BigDecimal(usable))==1){
        		return new ResponseResult("-1", "优惠券金额不得大于其使用条件！");
        	}
    	}
    	
    	Coupon coupon = new Coupon();
		
		coupon.setAmount(new BigDecimal(amount));
		coupon.setCouponCode(couponCode);
		coupon.setName(name);
		coupon.setTargetId(targetId);
		coupon.setTargetName(targetName);
		coupon.setCount(count);
		coupon.setTargetType(targetType);
		coupon.setDataFlag(1);
		if(StringUtils.isBlank(usable)){
			coupon.setUsable(0);
		}else{
			coupon.setUsable(Integer.parseInt(usable));
		}
		
		coupon.setType(type);
		coupon.setCreateTime(new Date());
		coupon.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime+":00"));
		coupon.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime+":59")); 
		if(coupon.getStartTime().after(coupon.getEndTime())){
			return new ResponseResult("-1", "开始时间不得早于结束时间！");
		}
		int result  = couponService.add(coupon);
		
		
		if (result == 1) {
			return new ResponseResult("1", "新增成功");
		} else {
			return new ResponseResult("-1", "新增失败");
		}
	}
    
    /**删除
     * @param id
     * @return
     */
	@RequiresPermissions("coupon:delete")
    @RequestMapping(value = "/admin/coupon/del.php", method = RequestMethod.GET)
    public @ResponseBody ResponseResult del(Integer id) {
    	int result = couponService.del(id);
    	if (result == 1) {
    		return new ResponseResult("1", "删除成功");
		} else {
			return new ResponseResult("-1", "删除异常");
		}
    }
    
    /**
     * @param 跳转到新增界面
     * @return
     */
	@RequiresPermissions("coupon:create")
    @RequestMapping(value = "/admin/statistics/addCoupon.php", method = RequestMethod.GET)
    public String addRedit(Model model) {
    	String couponCode = couponService.createCode();
        model.addAttribute("couponCode",couponCode);
		return "/admin/statistics/addCoupon";
    }
    
    /**
     * 编辑
     * @param id
     * @param type
     * @param targetId
     * @param targetType
     * @param name
     * @param startTime
     * @param endTime
     * @param amount
     * @param usable
     * @param targetName
     * @param couponCode
     * @return
     * @throws ParseException
     */
    @RequiresPermissions("coupon:edit")
    @RequestMapping(value = "/admin/coupon/edit.php", method = RequestMethod.POST)
    public @ResponseBody ResponseResult edit(
    		@RequestParam(value = "id", required = false)Integer id, 
    		@RequestParam(value = "type", required = false)Integer type, 
    		@RequestParam(value = "targetId", required = false)Integer targetId, 
    		@RequestParam(value = "targetType", required = false)Integer targetType,
    		@RequestParam(value = "name", required = false)String name,
    		@RequestParam(value = "startTime", required = false)String startTime, 
    		@RequestParam(value = "endTime", required = false)String endTime, 
    		@RequestParam(value = "amount", required = false)String amount, 
    		@RequestParam(value = "usable", required = false)Integer usable,
    		@RequestParam(value = "targetName", required = false)String targetName,
    		@RequestParam(value = "couponCode", required = false)String couponCode) throws ParseException {
    	Coupon coupon = new Coupon();
    	coupon.setId(id);
		coupon.setAmount(new BigDecimal(amount));
		coupon.setCouponCode(couponCode);
		coupon.setName(name);
		coupon.setTargetId(targetId);
		coupon.setTargetName(targetName);
		coupon.setTargetType(targetType);
		coupon.setDataFlag(1);
		coupon.setUsable(usable);
		coupon.setType(type);
		coupon.setCreateTime(new Date());
		if(startTime.length() == 10){
			coupon.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime+" 00:00:00"));
			coupon.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime+" 00:59:59")); 
		}else{
			coupon.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime+":00"));
			coupon.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime+":59")); 
		}
		
    	int result = couponService.edit(coupon);
    	if (result == 1) {
    		return new ResponseResult("1", "编辑成功");
		} else {
			return new ResponseResult("-1", "编辑异常");
		}
    }
    
    /**
     * 新增优惠券时查询分类
     * @param targetName
     * @param targetType 1：指定商家可用。2：指定分类可用。3：指定商品可用。4：指定专题可用
     * @return
     */
    @RequestMapping(value = "/admin/coupon/findTarget.php", method = RequestMethod.GET)
    public @ResponseBody ResponseResult findTarget(@RequestParam(value = "targetName", required = false)String targetName,
    		@RequestParam(value = "targetType", required = false)Integer targetType,Model model) {
    	if(targetType == 1){
    		List<WebsiteAdministrator> websiteAdministrators = couponService.findShop(targetName);
    		return new ResponseResult("1", "", websiteAdministrators);
    	}else if (targetType == 3){
    		List<OpenApiProductVo> openApiProductVos = couponService.findGoods(targetName);
    		return new ResponseResult("1", "", openApiProductVos);
		}else if (targetType == 4){
			List<SpecialActivity> specialActivitys = couponService.findSpecial(targetName);
			return new ResponseResult("1", "", specialActivitys);
		}
    	return new ResponseResult("-1", "参数异常");
    }
    
    /**
     * 父级目录
     * @param model
     * @return
     */
    @RequestMapping(value = "/admin/coupon/findParentSort.php", method = RequestMethod.GET)
    public @ResponseBody ResponseResult findParentSort(Model model) {
    	List<UserDefinedCate> parent = couponService.findSort();
    	return new ResponseResult("1", "",parent);
    }
    
    /**子目录
     * @param parentId
     * @return
     */
    @RequestMapping(value = "/admin/coupon/findChildSort.php", method = RequestMethod.GET)
    public @ResponseBody ResponseResult findChildSort(@RequestParam(value = "parentId", required = false)Integer parentId) {
    	List<UserDefinedCate> child = couponService.findChildSort(parentId);
    	return new ResponseResult("1", "",child);
    }
    
    /**分页查询
     * @param key
     * @param startTime
     * @param endTime
     * @param pager
     * @param model
     * @return
     */
    @RequiresPermissions("coupon:view")
    @RequestMapping(value = "/admin/statistics/couponList.php", method = RequestMethod.GET)
    public String pageQuery(@RequestParam(value = "key", required = false)String key,
    		@RequestParam(value = "startTime", required = false)String startTime,
    		@RequestParam(value = "status", required = false)String status,
    		@RequestParam(value = "targetType", required = false)String targetType,
    		@RequestParam(value = "endTime", required = false)String endTime,
    		@PagerSpecDefaults(pageSize = 20, maxPageSize = 100, sort = "createTime.desc") PagerSpec pager,Model model) {
		StringBuilder link = new StringBuilder(
				"/jdvop/admin/statistics/couponList.php?page=[:page]");
		CouponCriteria criteria = new CouponCriteria();
		if (StringUtils.isNotBlank(key)) {
			link.append("&ky=").append(key);
			model.addAttribute("key", key);
			criteria.key = key;
		}
		
		if (StringUtils.isNotBlank(status)) {
			link.append("&status=").append(status);
			model.addAttribute("status", status);
			criteria.status = status;
		}
		
		if (StringUtils.isNotBlank(targetType)) {
			link.append("&targetType=").append(targetType);
			model.addAttribute("targetType", targetType);
			criteria.targetType = Integer.parseInt(targetType);
		}
		if (StringUtils.isNotBlank(startTime)) {
			model.addAttribute("startTime", startTime);
			link.append("&st=").append(startTime);
			criteria.startTime = startTime;
		}
		if (StringUtils.isNotBlank(endTime)) {
			model.addAttribute("endTime", endTime);
			link.append("&et=").append(endTime);
			criteria.endTime = endTime;
		}
		Subject cu  = SecurityUtils.getSubject();
		Object  obj = cu.getPrincipal();
		String name=null;
		Boolean flag=true;
		if (obj != null && obj instanceof WebsiteAdministratorPrincipal)
		{
			name = ((WebsiteAdministratorPrincipal) obj).getWorkerName();
			flag = ((WebsiteAdministratorPrincipal) obj).isProvider();
		}
    	Page<Coupon> coupons = couponService.pageQuery(name,flag,criteria,pager);
    	model.addAttribute("link",link.toString());
    	model.addAttribute("coupons",coupons);
    	return "/admin/statistics/couponList";
    }
    
    /**编辑 查看界面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/admin/statistics/showCoupon.php", method = RequestMethod.GET)
    public String showCoupon(@RequestParam(value = "id", required = false)Integer id,Model model,
    		@RequestParam(value = "watch")Integer watch) {
    	Coupon coupon = couponService.findById(id);
    	if(coupon != null && coupon.getTargetType() == 2){
    		UserDefinedCate childcat = userDefinedCateService.findOne(coupon.getTargetId());
        	UserDefinedCate fathercat = userDefinedCateService.findOne(childcat.getParentId());
        	model.addAttribute("childcat", childcat);
        	model.addAttribute("fathercat", fathercat);
    	}
    	model.addAttribute("watch", watch);
    	Date da = coupon.getEndTime();
    	model.addAttribute(coupon);
    	model.addAttribute("couponCode","");
    	return "/admin/statistics/addCoupon";
    }

    //*****************************个人中心优惠券查询***************************
	@RequestMapping(value = "")
	public  ResponseResult userCoupon(Integer userId,Integer status){
    	if (userId != null){
    		if (status != null){
    			List<UserCenterCoupon> userCoupons = userCouponService.queryUserCouponByStatus(userId,status);
				return new ResponseResult("1","查询成功",userCoupons);
    		}else {
    			//List<UserCoupon> userCoupons = userCouponService.selectUserCoupon(userId);
				return new ResponseResult("1","查询成功");
			}
		}else {
			return new ResponseResult("-1","参数异常");
		}
	}


}
