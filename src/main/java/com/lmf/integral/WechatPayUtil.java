package com.lmf.integral;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.colorfulflorist.smk.market.entity.Cash;
import com.github.binarywang.wxpay.bean.request.WxEntPayRequest;
import com.github.binarywang.wxpay.bean.result.WxEntPayResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.lmf.integral.service.WxService;

@Component
public class WechatPayUtil {
	
	@Autowired
	private SystemConfig systemConfig;
	
	
	private static WechatPayUtil wechatPayUtil;
	
	@PostConstruct
	public void init(){
		wechatPayUtil = this;
	}

	
	public static WxPayServiceImpl getWxPayService() {
		WxPayServiceImpl wxPayService = new WxPayServiceImpl();
		wxPayService.setConfig(config());
		return wxPayService;
	}

	/**
	 * 初始化微信支付配置对象
	 *
	 * @return
	 */
	public static WxPayConfig config() {
		WxPayConfig payConfig = new WxPayConfig();
		payConfig.setAppId(wechatPayUtil.systemConfig.getAppId());
		payConfig.setMchId(wechatPayUtil.systemConfig.getMchId());
		payConfig.setMchKey(wechatPayUtil.systemConfig.getApiKey());
		payConfig.setSubAppId(null);
		payConfig.setSubMchId(null);
		payConfig.setKeyPath(wechatPayUtil.systemConfig.getKeyPath());
		return payConfig;
	}
	
	/*
     * 微信提现(若微信返回:SYSTEMERROR,则重新发起接口调用)
     *
     * @param amount     单位：分
     * @param deviceInfo 微信设备信息
     * @return
     */
    public static Map<String, Object> wxCashPayResult(int amount ,String addr, Cash cash, String openId) {
    	WxEntPayRequest entPayRequest = new WxEntPayRequest();
        entPayRequest.setAppid(wechatPayUtil.systemConfig.getAppId());
        entPayRequest.setMchId(wechatPayUtil.systemConfig.getMchId());
        entPayRequest.setPartnerTradeNo(cash.getId().toString());
        entPayRequest.setDescription("从市民卡提现");
        entPayRequest.setOpenid(openId);
        entPayRequest.setCheckName("NO_CHECK");
        entPayRequest.setAmount(amount);// 分

        String ipAddress = addr;
        if (StringUtils.isNotBlank(ipAddress) && ipAddress.indexOf(",") != -1) {
            String[] ipArr = ipAddress.split(",");
            ipAddress = ipArr[0].trim();
        }
        if (StringUtils.equals("0:0:0:0:0:0:0:1", ipAddress)) {
            ipAddress = "127.0.0.1";
        }
        entPayRequest.setSpbillCreateIp(ipAddress);

        Map<String, Object> result = new HashMap<>();
        try {
            WxEntPayResult wxEntPayResult = WechatPayUtil.getWxPayService().entPay(entPayRequest);
            if (wxEntPayResult == null) {
                result.put("data", null);
                result.put("code", "");
                result.put("msg", "微信提现返回空值,提现失败!");
                return result;
            }
            String returnCode = wxEntPayResult.getReturnCode();
            String resultCode = wxEntPayResult.getResultCode();
            if (StringUtils.isNotBlank(returnCode) && returnCode.equals("SUCCESS") &&
                    StringUtils.isNotBlank(resultCode) && resultCode.equals("SUCCESS")) {
                result.put("data", wxEntPayResult);
                result.put("code", "200");
                result.put("msg", "恭喜您,微信提现成功了!");
                return result;
            } else {
                if (StringUtils.isNotBlank(returnCode) && returnCode.equals("SUCCESS")) {
                    String errCode = wxEntPayResult.getErrCode();
                    if (StringUtils.isNotBlank(errCode) && "SYSTEMERROR".equals(errCode)) {
                        wxCashPayResult(amount,addr,cash,openId);
                    }
                    if (StringUtils.isNotBlank(resultCode) && resultCode.equals("FAIL")) {
                        result.put("data", wxEntPayResult);
                        result.put("code", wxEntPayResult.getErrCode());
                        result.put("msg", "抱歉,微信提现失败了!");
                        return result;
                    }
                }
            }
        } catch (WxPayException e) {
            e.printStackTrace();
        }
        result.put("data", null);
        result.put("code", "");
        result.put("msg", "微信提现失败!");
        return result;
    }
    
    public static String setOpenID() throws Exception {
    	String url = WxService.oauth2buildAuthorizationUrl("https://admin.colorfulflorist.com/test/wechat/smk/getOpenId");
    	return url;
    }
}
