/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.mobile.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmf.activity.entity.FlashSaleProduct;
import com.lmf.activity.enums.FlashSaleActivityStatus;
import com.lmf.activity.service.LimitExchangeService;
import com.lmf.activity.vo.FlashSaleProductCriteria;
import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.Exceptions;
import com.lmf.common.util.MobileSupportUtil;
import com.lmf.common.util.PagerSpec;
import com.lmf.enums.ProductStatus;
import com.lmf.integral.WechatUtil;
import com.lmf.openapi.entity.ForwardActivity;
import com.lmf.openapi.service.ForwardActivityService;
import com.lmf.openapi.service.ForwardGenerateLogService;
import com.lmf.product.entity.Product;
import com.lmf.product.service.ProductService;
import com.lmf.product.vo.ProductCriteria;
import com.lmf.system.sdk.HttpClientUtils;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.service.WebsiteUserService;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author yuanhu
 */
@Controller
public class ForwardActivityController {
    
    @Autowired
    private TimeZone    tz;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private WebsiteUserService websiteUserService;
    
    @Autowired
    private ForwardGenerateLogService forGenerateLogService;
    
    @Autowired
    private ForwardActivityService forwardActivityService;
    
    @Autowired
    private LimitExchangeService limitExchangeService;
        
    @Autowired
    private HttpClient  httpClient;
    
    private final JavaType   mapType;
    
    private final ObjectMapper  mapper  = new ObjectMapper();
    
    public  ForwardActivityController()
    {
        mapType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
    }
    
    @RequestMapping(value = "/forward/list.php", method = RequestMethod.GET)
    public String list(@PagerSpecDefaults(pageSize = 15, sort = "time.desc") PagerSpec pager, HttpServletRequest request, 
                                          WebsiteUser user, Model model) throws UnsupportedEncodingException, IOException
    {
        //进入移动端转发且必须是已登录状态
        if(MobileSupportUtil.isMobile(request)) {
            if(user == null) {
                return "redirect:/login.php?retUrl=/forward/list.php";
            }
            model.addAttribute("user", websiteUserService.findOne(user.getId()));
        }
        Page<ForwardActivity> activitys = forwardActivityService.find(null, pager);
        model.addAttribute("activitys", activitys);
        model.addAttribute("tz", tz);
        model.addAttribute("forConsumeLogService", forGenerateLogService);
        
        //商品推荐
        Page<Product> products =  productService.find(new ProductCriteria().withDeleted(Boolean.FALSE).withStatus(ProductStatus.selling), new PagerSpec(1, 0, 5));
        model.addAttribute("products", products);
        //只允许微信转发
        if (MobileSupportUtil.isWechat(request)) {
            
            String timestamp = String.valueOf(WechatUtil.getTimeStampSecond());
            String noncestr  = WechatUtil.getNonceStr(16);
            
            Map<String, String> signParamMap = new HashMap<>();
            signParamMap.put("jsapi_ticket", getJSPTicket());
            signParamMap.put("noncestr", noncestr);
            signParamMap.put("timestamp", timestamp);
            String url =  request.getRequestURL().toString();
            signParamMap.put("url", url);
            String signParams = WechatUtil.getParamsOrderByASCII(signParamMap);
            String signature  = WechatUtil.SH1(signParams);

            Map<String, String> wxConfigParamMap = new HashMap<>();
            wxConfigParamMap.put("appId", "wx2034661caef486d9");
            wxConfigParamMap.put("signature", signature);
            wxConfigParamMap.put("nonceStr", noncestr);
            wxConfigParamMap.put("timestamp", timestamp);
            
            model.addAttribute("paramMap2", wxConfigParamMap);
            
        }
        model.addAttribute("isWechat", MobileSupportUtil.isWechat(request));
        //添加限时兑换产品到model  在页面显示 add by liuqi
        Page<FlashSaleProduct> fpPage = limitExchangeService.find(new FlashSaleProductCriteria().withFlashSaleActivityStatus(FlashSaleActivityStatus.be_doing), null);
        if(fpPage != null && fpPage.hasContent()){
            model.addAttribute("fps", fpPage.getContent());
        }

        return "activity/forward";
    }
    
    @RequestMapping(value = "/forward/success.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse forwardSuccess(@RequestParam("aid") long activityId,
                                                           @RequestParam("uid") long uid,
                                                           HttpServletRequest request)
    {
        ForwardActivity activity = forwardActivityService.findOne(activityId);
        if(activity == null) 
        {
            return new SimpleJsonResponse(false, "操作失败，请重新操作！");
        }
        forGenerateLogService.forward(activity, uid, request.getRemoteAddr());
        return new SimpleJsonResponse(true , null);
    }
    
    public String getJSPTicket() {
        String  url = "http://wechat.fygift.com/getJsapiTicket.php?lmfToken=lmf89Xy7dkLxmq6XyMnp";
        Map<String, Object> resultMap  = doRequest(url);
        String ticket = "";
        if(resultMap != null) {
            if("true".equals((String) resultMap.get("RESPONSE_STATUS"))) {
                ticket = (String) resultMap.get("RESULT_DATA");
            }
        }
        return ticket;
    }
    
    Map<String, Object> doRequest(String url)
    {
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse    response  = HttpClientUtils.execute(httpClient, get);
            if(response != null)
            {
                String  content = EntityUtils.toString(response.getEntity());
                return mapper.readValue(content, mapType);
            }
        } catch (IOException exp) {
            throw Exceptions.unchecked(exp);
        } finally {
            get.abort();
        }
        return null;
    }
}
