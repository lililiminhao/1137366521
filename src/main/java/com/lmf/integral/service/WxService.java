/*
 * Copyright 2015-2016 ColorfulFlorist.com All right reserved. This software is the
 * confidential and proprietary information of ColorfulFlorist.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with ColorfulFlorist.com.
 */
package com.lmf.integral.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lmf.integral.SystemConfig;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;

/**
 * 类WxService.java的实现描述：微信服务
 * @author hongliang.zhanghl 2017年9月19日 下午3:25:21
 */
@Component
public class WxService {
	
	@Autowired
	private SystemConfig systemConfig;
	
	private static WxService wxService;
	
	
	@PostConstruct
    public void init() {
		wxService = this;
    }
	
    private static  WxMpServiceImpl wxMpService;
    /**
     * 网页未登录时进行登录操作
     * @param url
     * @return
     */
    public static String oauth2buildAuthorizationUrl(String url){
    	 WxMpInMemoryConfigStorage  configStorage = new WxMpInMemoryConfigStorage();
         configStorage.setAppId(wxService.systemConfig.getAppId());
         configStorage.setSecret(wxService.systemConfig.getAppsecret());
         wxMpService = new WxMpServiceImpl();
         wxMpService.setWxMpConfigStorage(configStorage);
        return wxMpService.oauth2buildAuthorizationUrl(url,"snsapi_base","SMKWXSERVICE");
    }
    
    public static WxMpOAuth2AccessToken oauth2getAccessToken(String code) throws WxErrorException{
        WxMpOAuth2AccessToken token =wxMpService.oauth2getAccessToken(code);
        return token;
    }
}
