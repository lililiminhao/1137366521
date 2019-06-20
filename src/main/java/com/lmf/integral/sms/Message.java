package com.lmf.integral.sms;


import java.util.HashMap;
import java.util.Map;

import com.lmf.market.enums.SignName;
import com.lmf.market.enums.SmsTemplateType;

public class Message {
	private SmsTemplateType smsTemplateType;
	private String phoneNumber;
	private SignName signName;
	private Map<String, String> params;

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public void addParam(String key, String value) {
		if (params == null) {
			params = new HashMap<>();
		}
		params.put(key, value);
	}

	public SmsTemplateType getSmsTemplateType() {
		return smsTemplateType;
	}

	public void setSmsTemplateType(SmsTemplateType smsTemplateType) {
		this.smsTemplateType = smsTemplateType;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public SignName getSignName() {
		return signName;
	}

	public void setSignName(SignName signName) {
		this.signName = signName;
	}
}
