package com.lmf.integral.mobile.market.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.colorfulflorist.smk.market.entity.Cash;
import com.colorfulflorist.smk.market.entity.FenxiaoMoneyLog;
import com.colorfulflorist.smk.market.entity.FenxiaoUser;
import com.colorfulflorist.smk.market.entity.SmsSendRecord;
import com.colorfulflorist.smk.market.mapper.CashMapper;
import com.colorfulflorist.smk.market.mapper.SmsSendRecordMapper;
import com.colorfulflorist.smk.market.service.CashService;
import com.colorfulflorist.smk.market.service.FenxiaoUserService;
import com.lmf.common.ResponseResult;
import com.lmf.integral.SmsUtil;
import com.lmf.integral.WechatPayUtil;
import com.lmf.market.enums.SmsTemplateType;
import com.lmf.market.service.FenXiaoUserService;
import com.lmf.market.vo.PhoneCodeSendResultDTO;
import com.lmf.openapi.enums.PhoneCodeSendResult;

/**
 * @author gcc 提现
 */
@Controller("withdrawCashController")
public class WithdrawCashController {

	@Autowired
	private CashService cashService;
	
	@Autowired
	private CashMapper cashMapper;
	
	@Autowired
	private SmsSendRecordMapper smsSendRecordMapper;
	
	@Autowired
	private FenXiaoUserService fenXiaoUserService;
	
	@Autowired
	private FenxiaoUserService fenxiaoUserService;

	/**
	 * @param 获取验证码
	 * @return
	 */
	@RequestMapping(value = "/mobile/cash/getPhoneCode.php", method = RequestMethod.GET)
	public @ResponseBody ResponseResult getPhoneCode(
			@RequestParam(value = "userPhone", required = false) String userPhone,
			HttpServletRequest request) {
		
		
		com.lmf.market.entity.FenxiaoUser fenxiaoUser = fenXiaoUserService.findByMobile(userPhone);
		if(fenxiaoUser == null){
			return new ResponseResult("-1", "该手机号码不属于分销商");
		}
		String ipAddress = request.getRemoteAddr();
		Map<String, String> map = SmsUtil.newHashMap();
		// 发送手机验证码
//		PhoneCodeSendResultDTO phoneCodeSendResultDTO = SmsUtil.sendSmsCode(userPhone,
//				SmsTemplateType.SMK_CASH, map, 1, ipAddress);
		PhoneCodeSendResultDTO phoneCodeSendResultDTO1= null;
		try {
			phoneCodeSendResultDTO1 = SmsUtil.sendSmsCodeSMK(fenxiaoUser);
		} catch (Exception e) {
			return new ResponseResult("-1", "短信发送失败");
		}
		if(phoneCodeSendResultDTO1 == null){
			return new ResponseResult("-1", "短信发送失败");
		}
		if (phoneCodeSendResultDTO1.getResult() == PhoneCodeSendResult.toomuch) {
			return new ResponseResult("-1", "请勿频繁发送信息");

		} else if (phoneCodeSendResultDTO1.getResult() == PhoneCodeSendResult.success) {
			return new ResponseResult("1", "发送成功", phoneCodeSendResultDTO1);
		} else {
			return new ResponseResult("-1", "短信发送失败!");
		}

	}
	
	
	/**
	 * @param 校验验证码并提交提现审核
	 * @return
	 * @throws Exception 
	 */
	@Transactional
	@RequestMapping(value = "/mobile/cash/submitCash.php", method = RequestMethod.GET)
	public @ResponseBody ResponseResult phoneCodeVerify(
			@RequestParam(value = "userPhone") String userPhone,
			@RequestParam(value = "mobileCode") String mobileCode,
			@RequestParam(value = "userId") Integer userId,
			@RequestParam(value = "amount") Double amount,
			@RequestParam(value = "messageId", required = false) String messageId,
			HttpServletRequest request,HttpSession session) throws Exception {
//		if (amount <100){
//			return new ResponseResult("-1", "提现金额必须大于一百！");
//		}
		com.lmf.market.entity.FenxiaoUser fenxiaoUser1 = fenXiaoUserService.findByUserId(userId);
		if(fenxiaoUser1 == null){
			return new ResponseResult("-1", "无此用户！");
		}
		if(amount > fenxiaoUser1.getMoney()){
			return new ResponseResult("-1", "提现金额必须小于可用金额！");
		}
//		Boolean result = cashService.hasToDaySend(userId);
//		if(result){
//			return new ResponseResult("-1", "每天只能提现一次！");
//		}
		FenxiaoUser fenxiaoUser = fenxiaoUserService.getById(fenxiaoUser1.getId());
		Map<String , Object> map = new HashMap<String, Object>();
		map.put("phone", userPhone);
		map.put("messageId", messageId);
		SmsSendRecord record =smsSendRecordMapper.selectByUserPhoneAndMessageId(map);
		if(record == null){
			return new ResponseResult("-1", "短信验证失败");
		}
		if(!StringUtils.equals(mobileCode, record.getCode()) ){
			return new ResponseResult("-1", "短信验证失败");
		}
		//10分钟之前
//		Date date = new Date(new Date().getTime()-600000);
//		if(date.after(record.getCreateTime())){
//			return new ResponseResult("-1", "短信验证超时");
//		}
//		if(StringUtils.isBlank(openId)){
//			StringBuffer backUrl = request.getRequestURL();
//			String queryString = request.getQueryString();
//			WechatPayUtil.setOpenID(backUrl.append("?"+queryString));
//		}
		String openId = (String) session.getAttribute("openId");
		String addr = request.getRemoteAddr();
		cashService.submitCash(fenxiaoUser,amount,openId,addr);
		return new ResponseResult("1", "提现申请已提交");
	}

	
//	/**
//	 * @param 
//	 * @return
//	 * @throws Exception 
//	 */
//	@RequestMapping(value = "/mobile/cash/setOpenId.php", method = RequestMethod.GET)
//	public ModelAndView setOpenId() throws Exception {
//		String url = WechatPayUtil.setOpenID();
//		return  new ModelAndView(new RedirectView(url));
//	}
	
	@RequestMapping(value = "/mobile/cash/cash.php", method = RequestMethod.GET)
	public @ResponseBody ResponseResult cash(
			@RequestParam(value = "userId") Integer userId,
			@RequestParam(value = "amount") Double amount,
			HttpServletRequest request,HttpSession session) throws Exception {
		com.lmf.market.entity.FenxiaoUser fenxiaoUser1 = fenXiaoUserService.findByUserId(userId);
		if(fenxiaoUser1 == null){
			return new ResponseResult("-1", "无此用户！");
		}
		if(amount > fenxiaoUser1.getMoney()){
			return new ResponseResult("-1", "提现金额必须小于可用金额！");
		}
		FenxiaoUser fenxiaoUser = fenxiaoUserService.getById(fenxiaoUser1.getId());
		String openId = (String) session.getAttribute("openId");
		String addr = request.getRemoteAddr();
		cashService.submitCash(fenxiaoUser,amount,openId,addr);
		return new ResponseResult("1", "提现申请已提交");
	}
	

}
