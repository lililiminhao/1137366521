/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.openapi.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmf.common.ResponseResult;
import com.lmf.common.ResponseStatusCode;
import com.lmf.common.enums.Gender;
import com.lmf.common.util.CommonUtil;
import com.lmf.common.util.Exceptions;
import com.lmf.integral.SystemConfig;
import com.lmf.order.service.OrderService;
import com.lmf.order.vo.OpenApiOrderVO;
import com.lmf.product.service.ProductService;
import com.lmf.product.vo.OpenApiProductVo;
import com.lmf.system.sdk.HttpClientUtils;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.enums.WebsiteLoginMethod;
import com.lmf.website.enums.WebsiteLoginType;
import com.lmf.website.service.WebsiteUserService;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author shenzhixiong
 */
@Controller
public class OpenApiController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private WebsiteUserService websiteUserService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private SystemConfig systemConfig;
    
    @Autowired
    private HttpClient httpClient;
    
    private static final JavaType mapType;

    private static final ObjectMapper mapper = new ObjectMapper();
    
    static {
        mapType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
    }
    
    @RequestMapping(value = "/openapi/order/list.php", method = RequestMethod.GET,produces="application/json;charset=UTF-8")
    public @ResponseBody ResponseResult orderList(@RequestParam(value = "wapToken", required = true) String token, HttpServletResponse httpResponse) {
        if(token == null || token.trim().isEmpty()) {
            return new ResponseResult(ResponseStatusCode.Common.ERROR.code, "参数错误");
        }
        WebsiteUser websiteUser = null;
        if (CommonUtil.isNull(token)) {//未获取到cookie中的wap_token，需要跳转到登录页
            return new ResponseResult(ResponseStatusCode.Common.OK.code, "查询失败，wapToken不能为空");
        } else {
            if(httpResponse != null) {
                httpResponse.setHeader("Access-Control-Allow-Origin", "*");
            }
            Map<String, Object> userInfoMap = doRequest(systemConfig.getApiHost() + "/users/token/" + token + "?system_code=smkjdmall");
            if (userInfoMap != null && Objects.equals(userInfoMap.get("code"), "0000")) {
                Map<String, Object> data = (Map) userInfoMap.get("data");
                String userId = data.get("user_id").toString();
                websiteUser = websiteUserService.findOne(WebsiteLoginType.loginName, userId);
                if (websiteUser == null) {//不存在，进行新增用户操作
                    Map<String, Object> baseInfoMap = doRequest(systemConfig.getApiHost() + "/users/" + token + "/userinfo?system_code=smkjdmall");
                    WebsiteUser user = new WebsiteUser();
                    user.setLoginName(userId);
                    user.setLoginPassword("0");
                    if (Objects.equals(baseInfoMap.get("code"), "0000")) {
                        Map<String, Object> baseInfo = (Map) baseInfoMap.get("data");
                        user.setMobile(baseInfo.get("mobile") != null ? baseInfo.get("mobile").toString() : null);
                        user.setNickName(baseInfo.get("nickname") != null ? baseInfo.get("nickname").toString() : "");
                        user.setHeadPortrait(baseInfo.get("user_photo") != null ? baseInfo.get("user_photo").toString() : "");
                    }
                    user.setMethod(WebsiteLoginMethod.password);
                    user.setType(WebsiteLoginType.loginName);
                    user.setEnabled(true);
                    user.setGender(Gender.unkown);
                    websiteUser = websiteUserService.save(user, false);
                }
            } else {
                return new ResponseResult(ResponseStatusCode.Common.ERROR.code, "查询失败，wap_token不能为空");
            }
        }
        
        if(websiteUser == null){
            return new ResponseResult(ResponseStatusCode.User.USER_NOT_EXIST.code, ResponseStatusCode.User.USER_NOT_EXIST.description);
        }
        List<OpenApiOrderVO> apiOrderVOList = orderService.queryOrderListForOpenApi(websiteUser.getId().intValue(), systemConfig.getImageHost());
        return new ResponseResult(ResponseStatusCode.Common.OK.code, "查询成功",apiOrderVOList);
    }
    
    @RequestMapping(value = "/openapi/product/list.php", method = RequestMethod.GET,produces="application/json;charset=UTF-8")
    public @ResponseBody ResponseResult productList(@RequestParam(value = "kwd", required = true) String kwd, HttpServletResponse httpResponse) throws UnsupportedEncodingException {
        if(httpResponse != null){
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        }
        kwd = URLDecoder.decode(kwd, "UTF-8");
        if(kwd == null || kwd.trim().isEmpty()) {
            return new ResponseResult(ResponseStatusCode.Common.OK.code, "查询成功",Collections.EMPTY_LIST);
        }
        List<OpenApiProductVo> apiProductVoList = productService.findProductListForOpenApi(kwd, systemConfig.getImageHost());
        return new ResponseResult(ResponseStatusCode.Common.OK.code, "查询成功",apiProductVoList);
    }
    
    private Map<String, Object> doRequest(String url) {
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse response = HttpClientUtils.notPorxyExecute(httpClient, get);
            if (response != null) {
                String content = EntityUtils.toString(response.getEntity());
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
