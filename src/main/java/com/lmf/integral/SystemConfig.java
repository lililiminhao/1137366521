package com.lmf.integral;


/**
 * 系统配置
 * @author zhouwei
 */
public class SystemConfig {
    
    private boolean developEnvironment;
    private String  imageHost;
    private int     websiteId;
    private String  vhost;
    private String  hostName;   //本项目配置的域名
    private String  loginUrl;  //统一登录平台访问路径
    private String  apiHost;   //API接口访问域名
    
    //tsmpay支付相关
    private String  tsmpayHost;	 		
    private String  merchantNo;     //商户号
    private String  merchantKey;	//商户秘钥
    
    //smkpay支付相关
    private String  wapSubmitUrl;
    private String  wapPostUrl;
    private String  sendChl;//渠道号
    private String  sendClient;//渠道秘钥
    private String  merCode;//商户号
    private String  smkpayRefundUrl;// 查询，对账单下载，退款请求路径
    
    //微信提现相关
    private String appId;
	private String mchId;
    private String apiKey;
    private String appsecret;
    private String keyPath;
    
    //RSA签名相关
    private String  pfxPassword;//签名证书密码

    //其他
    private int afterCompletedOrderForEnableAftersaleDays;//完成订单多少天内允许允许申请售后
    
    public boolean isDevelopEnvironment() {
        return developEnvironment;
    }

    public void setDevelopEnvironment(boolean developEnvironment) {
        this.developEnvironment = developEnvironment;
    }
    
    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

    public int getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(int websiteId) {
        this.websiteId = websiteId;
    }

    public String getVhost() {
        return vhost;
    }

    public void setVhost(String vhost) {
        this.vhost = vhost;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getApiHost() {
        return apiHost;
    }

    public void setApiHost(String apiHost) {
        this.apiHost = apiHost;
    }

    public String getTsmpayHost() {
        return tsmpayHost;
    }

    public void setTsmpayHost(String tsmpayHost) {
        this.tsmpayHost = tsmpayHost;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getMerchantKey() {
        return merchantKey;
    }

    public void setMerchantKey(String merchantKey) {
        this.merchantKey = merchantKey;
    }

    public String getWapSubmitUrl() {
        return wapSubmitUrl;
    }

    public void setWapSubmitUrl(String wapSubmitUrl) {
        this.wapSubmitUrl = wapSubmitUrl;
    }

    public String getAppId() {
		return appId;
	}

	public String getMchId() {
		return mchId;
	}

	public String getApiKey() {
		return apiKey;
	}

	public String getAppsecret() {
		return appsecret;
	}

	public String getKeyPath() {
		return keyPath;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}

	public void setKeyPath(String keyPath) {
		this.keyPath = keyPath;
	}
    
    public String getWapPostUrl() {
        return wapPostUrl;
    }

    public void setWapPostUrl(String wapPostUrl) {
        this.wapPostUrl = wapPostUrl;
    }

    public String getSendChl() {
        return sendChl;
    }

    public void setSendChl(String sendChl) {
        this.sendChl = sendChl;
    }

    public String getSendClient() {
        return sendClient;
    }

    public void setSendClient(String sendClient) {
        this.sendClient = sendClient;
    }

    public String getMerCode() {
        return merCode;
    }

    public void setMerCode(String merCode) {
        this.merCode = merCode;
    }

    public String getSmkpayRefundUrl() {
        return smkpayRefundUrl;
    }

    public void setSmkpayRefundUrl(String smkpayRefundUrl) {
        this.smkpayRefundUrl = smkpayRefundUrl;
    }

    public String getPfxPassword() {
        return pfxPassword;
    }

    public void setPfxPassword(String pfxPassword) {
        this.pfxPassword = pfxPassword;
    }

	public int getAfterCompletedOrderForEnableAftersaleDays() {
		return afterCompletedOrderForEnableAftersaleDays;
	}

	public void setAfterCompletedOrderForEnableAftersaleDays(int afterCompletedOrderForEnableAftersaleDays) {
		this.afterCompletedOrderForEnableAftersaleDays = afterCompletedOrderForEnableAftersaleDays;
	}

}
