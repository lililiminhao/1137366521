package com.lmf.integral.sms;

import java.util.Date;
import java.util.Map;







import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.colorfulflorist.smk.market.entity.SmsSendRecord;
import com.lmf.market.enums.SignName;
import com.lmf.market.enums.SmsTemplateType;
import com.lmf.market.vo.SmsResultDTO;


public class SmsSender {

	static final String product = "Dysmsapi";
	static final String domain = "dysmsapi.aliyuncs.com";
	static final String accessKeyId = "LTAIN48vOQvbn2F4";
	static final String accessKeySecret = "ZmZXWCeHAlFUtQpCqHoGCDmIeSWM3j";
	
	/**
	 * 新的发送短信验证码接口
	 * @param msg 包含手机号码,魔板信息,code,其他参数
	 * @return
	 */

	public static SmsResultDTO sendSMSNew(Message msg) {
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");
		SmsResultDTO smsResultDTO = new SmsResultDTO();
		try {
			// 初始化acsClient,暂不支持region化
			IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
			DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
			IAcsClient acsClient = new DefaultAcsClient(profile);
			// 组装请求对象-具体描述见控制台-文档部分内容
			SendSmsRequest request = new SendSmsRequest();
			// 必填:待发送手机号
			request.setPhoneNumbers(msg.getPhoneNumber());
			// 必填:短信签名-可在短信控制台中找到
			request.setSignName(msg.getSignName().getChineseSignName());
			// 必填:短信模板-可在短信控制台中找到
			request.setTemplateCode(msg.getSmsTemplateType().getAliyunCode());
			// 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
			request.setTemplateParam(JSON.toJSONString(msg.getParams()));
			// hint 此处可能会抛出异常，注意catch
			SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

			if (sendSmsResponse != null && "ok".equalsIgnoreCase(sendSmsResponse.getCode())) {
				smsResultDTO.setResult(Boolean.TRUE);
				smsResultDTO.setMessageId(sendSmsResponse.getBizId());
				// 如果发送成功，保存数据到数据库
				SmsSendRecord smsSendRecord = new SmsSendRecord();
				smsSendRecord.setCreateTime(new Date());
				smsSendRecord.setPhone(msg.getPhoneNumber());
				smsSendRecord.setCode(msg.getParams().get("code"));
				smsSendRecord.setMessageId(smsResultDTO.getMessageId());
				smsSendRecord.insert();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return smsResultDTO;
	}

	/**********
	 * 需要准备的参数
	 * 
	 * @param phoneUserRegisterVerfiy
	 **************/
	public static SmsResultDTO sendSMS(String userPhone, String code, SmsTemplateType phoneUserRegisterVerfiy,
			Map<String, String> params) {
		Message m = new Message();
		if (params != null) {
			m.setParams(params);
		}
		m.addParam("code", code);
		m.setPhoneNumber(userPhone);
		m.setSmsTemplateType(phoneUserRegisterVerfiy);
		m.setSignName(SignName.KEKANGAN);
		return sendSMSNew(m);
	}
}
