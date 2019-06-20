package com.lmf.integral;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import com.colorfulflorist.smk.market.entity.LogSms;
import com.google.gson.Gson;
import com.lmf.integral.sms.SmsSender;
import com.lmf.market.entity.FenxiaoUser;
import com.lmf.market.enums.SmsTemplateType;
import com.lmf.market.vo.PhoneCodeSendResultDTO;
import com.lmf.market.vo.SmsResultDTO;
import com.lmf.openapi.enums.PhoneCodeSendResult;

public class SmsUtil {

	/**
	 * @param userPhone
	 * @param smstemplate
	 * @param paramsMap
	 * @param cachePrefix
	 * @return
	 */
	public static PhoneCodeSendResultDTO sendSmsCode(String userPhone, SmsTemplateType smstemplate,
                                                      Map<String, String> paramsMap, Integer userId ,String ipAddress) {
        String cachePrefix = smstemplate.name();
        // 生成4位验证码
        int random = 1000 + RandomUtils.nextInt(8999);
        String code = String.valueOf(random);
        paramsMap.put("code", code);
        // 用户id
        if (userId == null || userId < 0) {
            userId = 0;
        }
        // ip
        if (StringUtils.isNotBlank(ipAddress) && ipAddress.indexOf(",") != -1) {
            String[] ipArr = ipAddress.split(",");
            ipAddress = ipArr[0].trim();
        }
        // 用户ip
        String ip = ipAddress;
        PhoneCodeSendResultDTO dto = new PhoneCodeSendResultDTO();
        // 设置发送模板并发送短信
        boolean success = false;
        SmsResultDTO resultDTO = SmsSender.sendSMS(userPhone, code, smstemplate, paramsMap);
        success = resultDTO.getResult().booleanValue();
        LogSms logsms = new LogSms();
        logsms.setSmsSrc(0);
        logsms.setSmsUserId(userId);
        logsms.setSmsPhoneNumber(userPhone);
        logsms.setSmsContent("发送方：Aliyun||模板编号：" + smstemplate.getAliyunCode() + "||参数列表"
                + (new Gson()).toJson(paramsMap));
        logsms.setSmsCode(code);
        logsms.setSmsIp(ip);
        logsms.setSmsFunc(cachePrefix);
        logsms.setCreateTime(new Date());
        String returnCode = success ? "1" : "-1";
        logsms.setSmsReturnCode(returnCode);
        logsms.insert();
        
        if (success) {
            // 缓存10分钟数据
            dto.setResult(PhoneCodeSendResult.success);
            dto.setMessageId(resultDTO.getMessageId());
            return dto;
        } else {
            dto.setResult(PhoneCodeSendResult.fail);
            return dto;
         
            }
        
    }

	public static final <k, v> HashMap<k, v> newHashMap() {
		return new HashMap<k, v>();
	}

	public static PhoneCodeSendResultDTO sendSmsCodeSMK(FenxiaoUser fenxiaoUser) throws Exception {
		String result = createParams(fenxiaoUser);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("sendMsgInfo", result);
		String sendPost = HttpUtil.sendPost("http://192.168.23.70:8081/umpSrv/sendMsg", params);
		
		return null;
	}

	private static String createParams(FenxiaoUser fenxiaoUser) {
		 // 生成4位验证码
		int random = 1000 + RandomUtils.nextInt(8999);
        String code = String.valueOf(random);
		Map<String, String> result = new HashMap<String, String>();
		result.put("channo", "1");
		result.put("branchno", "1");
		result.put("srvid", "1");
		result.put("sendno", "1");
		result.put("userno", "1");
		result.put("password", "1");
		result.put("immedflag", "1");
		result.put("lastsndtime", null);
		result.put("objaddr", fenxiaoUser.getMobile());
		result.put("sfz", null);
		result.put("tmpno", "301");
		result.put("bizType", fenxiaoUser.getUserName().toString());
		result.put("YZM", code);
		result.put("YXQ", "10");
		return URLEncoder.encode(XMLUtil.map2XmlStringForSendMsg(result));
	}
	
}
