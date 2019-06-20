package com.lmf.integral.mobile.market.controller;


import com.alibaba.fastjson.JSON;
import com.colorfulflorist.smk.market.entity.SmsSendRecord;
import com.colorfulflorist.smk.market.mapper.SmsSendRecordMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.ResponseResult;
import com.lmf.common.util.PagerSpec;
import com.lmf.integral.SmsUtil;
import com.lmf.integral.SystemConfig;
import com.lmf.market.entity.FenxiaoMoneyLog;
import com.lmf.market.entity.FenxiaoUser;
import com.lmf.market.entity.Relationship;
import com.lmf.market.enums.SmsTemplateType;
import com.lmf.market.repository.FenxiaoRankDao;
import com.lmf.market.repository.RelationshipDao;
import com.lmf.market.repository.vo.MoneyLogCriteria;
import com.lmf.market.service.FenXiaoOrderService;
import com.lmf.market.service.FenXiaoProductService;
import com.lmf.market.service.FenXiaoUserService;
import com.lmf.market.service.FenxiaoMoneyLogService;
import com.lmf.market.vo.PhoneCodeSendResultDTO;
import com.lmf.openapi.enums.PhoneCodeSendResult;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.service.WebsiteUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller("mobileFenXiaoUserController")
public class FenXiaoUserController {

	@Autowired
	private FenXiaoProductService fenXiaoProductService; 
	
	@Autowired
	private FenXiaoOrderService fenXiaoOrderService; 
	
	@Autowired
	private FenXiaoUserService fenXiaoUserService;
	
	@Autowired
	private FenxiaoMoneyLogService fenxiaoMoneyLogService;
	
	@Autowired
    private SystemConfig systemConfig;

	@Autowired
	private SmsSendRecordMapper smsSendRecordMapper;

	@Autowired
	private FenxiaoRankDao fenxiaoRankDao;

	@Autowired
	private WebsiteUserService websiteUserService;

	@Autowired
	private RelationshipDao relationshipDao;

	
	/**
	 * 惠分享 个人中心
	 * @param
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/mobile/fenxiao/userCenter.php", method = RequestMethod.GET)
	public @ResponseBody ResponseResult userCenter(
			@RequestParam(value = "userId", required = false)Integer userId
			){
		FenxiaoUser user = fenXiaoUserService.findByUserId(userId);
		if(user == null){
			return new ResponseResult("-1", "查无此用户！");
		}else{
			return new ResponseResult("1", "",user);
		}
	}
	
	/**
	 * 个人二维码
	 * @param
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/mobile/fenxiao/userQRCode.php", method = RequestMethod.GET)
	public @ResponseBody ResponseResult userQRCode(
			@RequestParam(value = "userId", required = false)Integer userId
			){
			String content = systemConfig.getHostName()+"/jdvop/mobile/fenxiao/myProducts.php?fenxiaoAndUserId="+userId;
			return new ResponseResult("1", "",content);
	}


	/**
	 * 惠分享 佣金明细
	 * @param userId
	 * @param isDetail
	 * @param hierarchy
	 * @param page
	 * @param pageNum
	 * @return
	 */
	@RequestMapping(value = "/mobile/fenxiao/myMoney.php", method = RequestMethod.GET)
	public @ResponseBody ResponseResult myMoney(
			@RequestParam(value = "userId", required = false)Integer userId,
			@RequestParam(value = "isDetail", required = false)Integer isDetail,
			@RequestParam(value = "hierachy", required = false)Integer hierarchy,
			@PagerSpecDefaults(pageSize = 99999, maxPageSize = 100, sort = "createTime.desc") PagerSpec page,
			@RequestParam(value = "pageNum", required = false) Integer pageNum//第几页
			){
		page.setCurrentPage(pageNum);
		MoneyLogCriteria criteria = new MoneyLogCriteria();
		
		if(userId != null && userId != 0 ){
			FenxiaoUser user = fenXiaoUserService.findByUserId(userId);
			if(user == null){
				return new ResponseResult("-1", "用户不存在");
			}
			criteria.userId = userId;
			criteria.mobile = user.getMobile();
			if (isDetail != null && isDetail == 1){
				criteria.detail = "微信";
			}
			if (hierarchy != null){
				criteria.hierarchy = hierarchy;
			}
		}
		Page<FenxiaoMoneyLog> fenxiaoMoneyLogs = fenxiaoMoneyLogService.pageQuery(
				criteria, page);
		return new ResponseResult("1", "",fenxiaoMoneyLogs);
	}

	/**
	 * 惠分享 邀请分销商
	 * @param userMobile
	 * @param
	 * @return
	 *//*
	@RequestMapping(value = "/mobile/fenxiao/fxUserApply.php", method = RequestMethod.GET)
	public JSONPObject fxUserApply(@RequestParam(value = "userMobile", required = false) String userMobile,
								   @RequestParam(value = "callback", required = false)String callback
								   //@RequestParam(value = "userId", required = false)Integer userId
									  ){
		*//*if(rankId == null || rankId == 0){
			return new ResponseResult("-1", "请选择分销等级！");
		}*//*
		String rankName = "T级";
		FenxiaoRank rank = fenxiaoRankDao.findByName(rankName);
		FenxiaoUser user2 = fenXiaoUserService.findByMobile(userMobile);

		FenxiaoUser user = new FenxiaoUser();
		user.setCreateTime(new Date());
		user.setFreezeMoney(0.00);
		user.setMobile(userMobile);
		user.setMoney(0.00);
		user.setRankId(rank.getId());
		user.setStatus(0);
		user.setUserName(userMobile);
		if(user2 != null){
			user.setUserId(user2.getId());
		}
		int result = fenXiaoUserService.addFenxiaoUser(user);
		if(result == 1){
			ResponseResult r1 =  new ResponseResult("1", "新增成功");
			String jsonString = JSON.toJSONString(r1);
			return new JSONPObject(callback,jsonString);
		}else {
			ResponseResult r2 = new ResponseResult("-1", "新增失败");
			String jsonString = JSON.toJSONString(r2);
			return new JSONPObject(callback,jsonString);
		}

	}*/


	/**
	 * 惠分享 邀请分销商,并校验验证码
	 * @param userPhone
	 * @param mobileCode
	 * @param callback
	 * @param messageId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/mobile/fenxiao/fxUserApply.php", method = RequestMethod.GET)
	@ResponseBody
	public JSONPObject phoneCodeVerify(@RequestParam(value = "userPhone", required = false) String userPhone,
									   @RequestParam(value = "mobileCode") String mobileCode,
									   @RequestParam(value = "callback", required = false)String callback,
									   @RequestParam(value = "userId") Integer userId, //分销商id
									   @RequestParam(value = "messageId", required = false) String messageId,
									   HttpServletRequest request) {

		//邀请分销商校验验证码并返回信息 --start--
		/*Map<String , Object> map = new HashMap<String, Object>();
		map.put("phone", userPhone);
		map.put("messageId", messageId);
		SmsSendRecord record =smsSendRecordMapper.selectByUserPhoneAndMessageId(map);
		if(record == null){
			ResponseResult r1 = new ResponseResult("-1", "短信验证失败");
			String jsonString = JSON.toJSONString(r1);
			return new JSONPObject(callback,jsonString);
		}
		if(!StringUtils.equals(mobileCode, record.getCode())){
			ResponseResult r2 = new ResponseResult("-1", "短信验证失败");
			String jsonString = JSON.toJSONString(r2);
			return new JSONPObject(callback,jsonString);
		}
		//10分钟有效期
		Date date = new Date(new Date().getTime()-600000);
		if(date.after(record.getCreateTime())){
			ResponseResult r3 =  new ResponseResult("-1", "短信验证超时");
			//做跨域处理
			String jsonString = JSON.toJSONString(r3);
			return new JSONPObject(callback,jsonString);
		}*/
		// --end--

		//此处默认是二级分销商
		int result = 0;
		FenxiaoUser fxUser = fenXiaoUserService.findByUserId(userId);
		FenxiaoUser user2 = fenXiaoUserService.findByMobile(userPhone);
		FenxiaoUser user3 = fenXiaoUserService.findByMobileDel(userPhone);
		WebsiteUser websiteUser = websiteUserService.findByMobile(userPhone);
		//判断数据是否被彻底逻辑删除
		if(user2 !=null && user2.getStatus() != -1 && user2.getDataFlag() != -1){
			ResponseResult r = new ResponseResult("-1", "该手机号已被邀请过");
			String jsonString = JSON.toJSONString(r);
			return new JSONPObject(callback,jsonString);
		}else if (user3 != null){  //解绑后重新绑定修改状态和删除标识以及分销商id 数据不新增
			int result1=fenXiaoUserService.bindFenxiaoUser1(user3.getId(),fxUser.getRankId());
			if(result1==1){
				Relationship relationship1 = relationshipDao.findbyuserID(websiteUser.getId().intValue());
				if(relationship1 == null){
					Relationship re = new Relationship();
					re.setUserID(websiteUser.getId());
					re.setFenxiaoID(userId);
					re.setRelationship(1);
					relationshipDao.save(re);
				}
				ResponseResult r6 =  new ResponseResult("1", "新增成功");
				String jsonString = JSON.toJSONString(r6);
				return new JSONPObject(callback,jsonString);
			}else{
				ResponseResult r7 = new ResponseResult("-1", "新增失败");
				String jsonString = JSON.toJSONString(r7);
				return new JSONPObject(callback,jsonString);
			}
		}else{
			FenxiaoUser user = new FenxiaoUser();
			user.setCreateTime(new Date());
			user.setFreezeMoney(0.00);
			user.setMobile(userPhone);
			user.setMoney(0.00);
			user.setRankId(fxUser.getRankId());
			user.setStatus(0);
			user.setUserName(userPhone);
			user.setParentID(1);
			if(websiteUser != null){
				user.setUserId(websiteUser.getId().intValue());
			}
			result = fenXiaoUserService.addFenxiaoUser(user);
		}

		if(result == 1){
			Relationship relationship = relationshipDao.findbyuserID(websiteUser.getId().intValue());
			if(relationship!=null&&fxUser.getParentID()==0){
				relationshipDao.update(userId,relationship.getId());
			}
			//没有分销层级关系建立一级二级分销关系
			if(relationship == null){
				Relationship re = new Relationship();
				re.setUserID(websiteUser.getId());
				re.setFenxiaoID(userId);
				re.setRelationship(1);
				relationshipDao.save(re);
			}
			ResponseResult r4 =  new ResponseResult("1", "新增成功");
			String jsonString = JSON.toJSONString(r4);
			return new JSONPObject(callback,jsonString);
		}else {
			ResponseResult r5 = new ResponseResult("-1", "新增失败");
			String jsonString = JSON.toJSONString(r5);
			return new JSONPObject(callback,jsonString);
		}
	}

	

	/**
	 * 邀请分销商 获取验证码
	 * @param userPhone
	 * @param callback
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/mobile/fenxiao/getPhoneCode.php", method = RequestMethod.GET)
	@ResponseBody
	public JSONPObject getPhoneCode(
			@RequestParam(value = "userPhone", required = false) String userPhone,
			@RequestParam(value = "callback", required = false)String callback,
			HttpServletRequest request) {
		String ipAddress = request.getRemoteAddr();
		Map<String, String> map = SmsUtil.newHashMap();
		// 发送手机验证码
		PhoneCodeSendResultDTO phoneCodeSendResultDTO = SmsUtil.sendSmsCode(userPhone,
				SmsTemplateType.SMK_APPLY_FENXIAO, map, 1, ipAddress);

		if (phoneCodeSendResultDTO.getResult() == PhoneCodeSendResult.toomuch) {
			ResponseResult r1 = new ResponseResult("-1", "请勿频繁发送信息");
			String jsonString = JSON.toJSONString(r1);
			return new JSONPObject(callback,jsonString);

		} else if (phoneCodeSendResultDTO.getResult() == PhoneCodeSendResult.success) {
			ResponseResult r2  = new ResponseResult("1", "发送成功", phoneCodeSendResultDTO);
			String jsonString = JSON.toJSONString(r2);
			return new JSONPObject(callback,jsonString);
		} else {
			ResponseResult r3 = new ResponseResult("-1", "短信发送失败!");
			String jsonString = JSON.toJSONString(r3);
			return new JSONPObject(callback,jsonString);
		}

	}

	
	/**
	 * @return
	 * 惠分享个人中心页面映射
	 */
	@RequestMapping(value = "/my/mobile/share.php", method = RequestMethod.GET)
    public String share(Model model){
		model.addAttribute("domain",systemConfig.getHostName());
		
		return "/mobile/fenxiao/index";
    }
	
	/**
	 * @return
	 * 二级分销用户注册页面
	 */
	@RequestMapping(value = "/my/mobile/erjifenxiao.php", method = RequestMethod.GET)
    public String saler2() {
		return "/mobile/fenxiao/register";
    }
	
	/**
	 * @return
	 * 惠分享个人信息页面映射
	 */
	@RequestMapping(value = "/my/mobile/share_person.php", method = RequestMethod.GET)
    public String share_person(@RequestParam(value = "mobile", required = false) String mobile,
    		@RequestParam(value = "rankName", required = false) String rankName,
    		@RequestParam(value = "parentID", required = false) String parentID,
    		@RequestParam(value = "secPer", required = false) String secPer,
			@RequestParam(value = "percent", required = false) String percent,Model model){
		model.addAttribute("mobile",mobile);
		model.addAttribute("rankName",rankName);
		model.addAttribute("percent",percent);
		model.addAttribute("parentID",parentID);
		model.addAttribute("secPer",secPer);
		return "/mobile/fenxiao/person";
    }
	
	/**
	 * @return
	 * 惠分享微信提现页面映射
	 */
	@RequestMapping(value = "/my/mobile/share_withdraw.php", method = RequestMethod.GET)
    public String share_withdraw(@RequestParam(value = "money", required = false) String money,
    		@RequestParam(value = "phone", required = false) String phone,
    		Model model){
		model.addAttribute("money",money);
		model.addAttribute("phone",phone);
		return "/mobile/fenxiao/withdraw";
    }
	
	/**
	 * @return
	 * 惠分享佣金明细页面映射
	 */
	@RequestMapping(value = "/my/mobile/share_money_detail.php", method = RequestMethod.GET)
    public String share_money_detail(@RequestParam(value = "allm", required = false) String allm,WebsiteUser websiteUser,Model model){
		model.addAttribute("allm",allm);
		model.addAttribute("currentUser",websiteUser);
		return "/mobile/fenxiao/money_detail";
    }
	
	/**
	 * @return
	 * 惠分享分销产品页面映射
	 */
	@RequestMapping(value = "/my/mobile/share_products.php", method = RequestMethod.GET)
    public String share_products(){
		return "/mobile/fenxiao/products";
    }
	
	/**
	 * @return
	 * 惠分享订单页面映射
	 */
	@RequestMapping(value = "/my/mobile/share_order.php", method = RequestMethod.GET)
    public String share_order(){
		
		return "/mobile/fenxiao/order";
    }
	
}
