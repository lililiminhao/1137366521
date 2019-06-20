/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.misc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmf.common.Constant;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.CommonUtil;
import com.lmf.common.util.LMFDateUtil;
import com.lmf.extend.pay.tsmpay.TsmpayUtil;
import com.lmf.extend.pay.tsmpay.enums.PayChannel;
import com.lmf.extend.pay.tsmpay.enums.TerminalType;
import com.lmf.extend.pay.tsmpay.json.QueryOrderRequsetJson;
import com.lmf.extend.pay.tsmpay.json.QueryOrderResponseJson;
import com.lmf.extend.pay.tsmpay.json.SubmitOrderRequestJson;
import com.lmf.extend.pay.tsmpay.json.SubmitOrderResponseJson;
import com.lmf.extend.pay.tsmpay.util.SignGenerator;
import com.lmf.integral.SystemConfig;
import com.lmf.order.entity.NifferOrder;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.enums.NifferOrderStatus;
import com.lmf.order.enums.OrderPayType;
import com.lmf.order.enums.OrderStatus;
import com.lmf.order.service.NifferOrderService;
import com.lmf.order.service.OrderService;
import com.lmf.order.service.RefundOperateService;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteUser;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 众城付系统
 * @author Administrator
 */
@Controller
public class TsmPayController {
    
    @Autowired
    private NifferOrderService nifferOrderService;
    
    @Autowired
    private RefundOperateService refundOperateService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private SystemConfig systemConfig;
    
    @Autowired
    private TsmpayUtil tsmpayUtil;
    
    private final ObjectMapper mapper = new ObjectMapper();

    private final JavaType mapType;
    
    private final Logger logger = LoggerFactory.getLogger(TsmPayController.class);
    
    public TsmPayController() {
        mapType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
    }
    /**
     * 退款异步回调通知
     * @throws ParseException 
     * 
     * @throws IOException 
     */
    @RequestMapping(value = "/tsmpay/refund/notify.php", produces="application/x-www-form-urlencoded")
    public @ResponseBody String refundNotify(@RequestParam(name = "merchantNo", required = false) String merchantNo,
                                            @RequestParam(name = "merchantOrderNo", required = false) String merchantOrderNo,
                                            @RequestParam(name = "refundAmt", required = false) int refundAmt,
                                            @RequestParam(name = "refundNo", required = false) String refundNo,
                                            @RequestParam(name = "refundStatus", required = false) int refundStatus,
                                            @RequestParam(name = "refundFinishTime", required = false) String refundFinishTime,
                                            @RequestParam(name = "sign", required = false) String sign) throws UnsupportedEncodingException, ParseException {
        Map<String, Object> resultMap = new HashMap<>(2);
        Map<String, Object> requestMap = new LinkedHashMap<>();
        requestMap.put("merchantNo", systemConfig.getMerchantNo());
        requestMap.put("merchantOrderNo", merchantOrderNo);
        requestMap.put("refundAmt", refundAmt);
        requestMap.put("refundNo", refundNo);
        requestMap.put("refundStatus", refundStatus);
        requestMap.put("refundFinishTime", refundFinishTime);
        requestMap.put("key", systemConfig.getMerchantKey());
        //签名验证
        boolean isPass = validateSign(requestMap, sign);
        if(!isPass){
            resultMap.put("code", 1);
            resultMap.put("message", "签名验证不通过");
            logger.info("签名验证不通过，参数：{}，签名：{}",toJsonStr(requestMap),sign);
            return toJsonStr(resultMap);
        }
        ShoppingOrder shoppingOrder = orderService.findOne(merchantOrderNo);
        ShoppingOrder originalOrder = shoppingOrder;
        if(shoppingOrder==null){
            resultMap.put("code", 1);
            resultMap.put("message", "订单不存在");
            logger.info("订单不存在，订单号：{}",merchantOrderNo);
            return toJsonStr(resultMap);
        }
        NifferOrder nifferOrder = nifferOrderService.findByAfterSaleOrderKey(refundNo);
        if(nifferOrder==null){
            resultMap.put("code", 1);
            resultMap.put("message", "售后单不存在");
            logger.info("售后单不存在，售后单号：{}",refundNo);
            return toJsonStr(resultMap);
        }
        if(shoppingOrder.isDeprecated()){
            originalOrder = orderService.findOne(nifferOrder.getOriginalOrderId());
            if(!originalOrder.getOriginalOrderId().equals(shoppingOrder.getId())){
                resultMap.put("code", 1);
                resultMap.put("message", "数据错误");
                logger.info("数据错误，订单号：{}，售后单号：{}",merchantOrderNo,refundNo);
                return toJsonStr(resultMap);
            }
        }else{
            if(!shoppingOrder.getId().equals(nifferOrder.getOriginalOrderId())){
                resultMap.put("code", 1);
                resultMap.put("message", "数据错误");
                logger.info("数据错误，订单号：{}，售后单号：{}",merchantOrderNo,refundNo);
                return toJsonStr(resultMap);
            }
        }

        //验证退款单是否已退款
        if(!nifferOrder.getStatus().equals(NifferOrderStatus.processing_refund) && !nifferOrder.getStatus().equals(NifferOrderStatus.waiting_finance)){
            resultMap.put("code", 1);
            resultMap.put("message", "退款状态异常，非退款处理中状态");
            logger.info("退款状态异常，非退款处理中状态，售后单号：{}",refundNo);
            return toJsonStr(resultMap);
        }
        
        try {
            QueryOrderResponseJson queryOrderResponseJson = refundOperateService.queryOrder(merchantOrderNo);
            if(queryOrderResponseJson==null){
                resultMap.put("code", 1);
                resultMap.put("message", "网络异常");
                logger.info("网络异常，订单号：{}",merchantOrderNo);
                return toJsonStr(resultMap);
            }
            if(queryOrderResponseJson.getOrderPayStatus()==Constant.ZERO){
                resultMap.put("code", 1);
                resultMap.put("message", "订单未支付");
                logger.info("订单未支付，订单号：{}",merchantOrderNo);
                return toJsonStr(resultMap);
            }
        } catch (Exception e) {
            resultMap.put("code", 1);
            resultMap.put("message", e.getMessage());
            logger.info(e.getMessage());
            return toJsonStr(resultMap);
        }
        
        
        //验证退款状态：成功、失败
        if(Constant.ONE==refundStatus){
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        	nifferOrder.setAcceptTime(sdf.parse(refundFinishTime));
            nifferOrderService.refundSuccessHandle(nifferOrder, originalOrder);
            resultMap.put("code", 0);
            logger.info("退款成功");
            return toJsonStr(resultMap);
        }else{
            nifferOrderService.setStatus(nifferOrder, NifferOrderStatus.waiting_finance);
            resultMap.put("code", 0);
            logger.info("退款失败");
            return toJsonStr(resultMap);
        }
    }
    
    private String toJsonStr(Map<String, Object> resultMap){
        try {
            return mapper.writeValueAsString(resultMap);
        } catch (JsonProcessingException ex) {
            logger.error("resultMap序列化异常",ex);
        }
       return "{\"code\":1}";
    }
    /**
     * 验证签名
     * @param requestMap
     * @return 验证通过-true,验证不通过-false
     */
    private final boolean validateSign(Map<String, Object> requestMap,String sign) throws UnsupportedEncodingException{
        if(null == sign){
            return false;
        }
        String newSign = SignGenerator.generate(requestMap);
        if(newSign.equals(sign)){
            return true;
        }
        return false;
    }
    
    /**
     * tsmPay发起支付
     * @param orderKey
     * @param payType
     * @param websiteUser
     * @return
     * @throws IOException 
     */
    @RequestMapping(value = "/my/order/tsmPay/topay.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse toPay(@RequestParam(name = "orderKey", required = false) String orderKey,
                                                  WebsiteUser websiteUser) throws IOException {
        
        ShoppingOrder order = orderService.findOne(orderKey);
        if(order == null) {
            logger.error("Order data is null!");
            return new SimpleJsonResponse(false, "系统异常，请刷新后重新尝试！");
        }
        if (!Objects.equals(order.getUserId(), websiteUser.getId().intValue())) {
            logger.error("用户越权操作!");
            return new SimpleJsonResponse(false, "系统异常，请刷新后重新尝试！");
        }
        if(OrderStatus.waiting_pay != order.getStatus()){
            return new SimpleJsonResponse(false, "您已支付过此订单了，无需再次支付！");
        }
        try {
            QueryOrderResponseJson qoResponseJson = queryOrder(order);
            if(qoResponseJson == null || qoResponseJson.getPayUrl() == null) {
                SubmitOrderResponseJson submitOrderResponseJson = sumitOrder(order, OrderPayType.wechat);
                if(submitOrderResponseJson == null) {
                    return new SimpleJsonResponse(false, "网络异常，请重新尝试！");
                }
                if(submitOrderResponseJson.getRespCode() == 0) {
                    if(submitOrderResponseJson.getOrderPayStatus() == 1 || submitOrderResponseJson.getOrderPayStatus() == 2) {
                        return new SimpleJsonResponse(true, submitOrderResponseJson.getPayUrl());
                    } else {
                        logger.warn("----------------------------tsmPay提交支付单失败，错误码:{}", submitOrderResponseJson.getOrderPayStatus());
                        return new SimpleJsonResponse(false, "网络异常，请您刷新当前页面后重新尝试！");
                    }
                }
                return new SimpleJsonResponse(false, submitOrderResponseJson.getRespDesc());
            } else{
                if(qoResponseJson.getOrderPayStatus() == 0) {
                    return new SimpleJsonResponse(true, qoResponseJson.getPayUrl());
                }
            }
        } catch (Exception e) {
            logger.error("tsmPay提交支付单异常", e);
            return new SimpleJsonResponse(false, "网络异常，请您稍后再试！");
        }
        
        return new SimpleJsonResponse(false, "您的订单已经支付成功，无需重新支付！");
    }
    
    @RequestMapping(value = "/my/order/tsmPay/payReturn.php", method = RequestMethod.GET)
    public String payReturn(@RequestParam(name = "orderKey", required = false) String orderKey, HttpServletRequest request) throws IOException {
        ShoppingOrder order = orderService.findOne(orderKey);
        if(order == null) {
            throw new RuntimeException("tsmPay支付回调数据异常，订单为空！");
        }
        //暂未完成此部分功能。
//        if(order.getStatus() == OrderStatus.waiting_pay) {
//            orderService.tsmPayReturn(order, request.getRemoteAddr());
//        }
        
        return "redirect:/my/mobile/orders.php";
    }
    
    
    /**
     * 支付结果异步通知
     * @param merchantNo
     * @param merchantOrderNo
     * @param orderAmt
     * @param userMobile
     * @param orderDesc
     * @param orderLife
     * @param frontendUrl
     * @param backendUrl
     * @param productNo
     * @param terminalType
     * @param orderPayStatus
     * @param orderPayTime
     * @param payChannel
     * @param mpcOrderNo
     * @param pcOrderNo
     * @param pcUserNo
     * @param sign
     * @param request
     * @param website
     * @return 
     */
    @RequestMapping(value = "/tsmPay/payNotify.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody Map<Integer, String> tsmPayNotify(@RequestParam(name = "merchantNo", required = false) String merchantNo,
			                                               @RequestParam(name = "merchantOrderNo", required = false) String merchantOrderNo,
			                                               @RequestParam(name = "orderAmt", required = false) String orderAmt,
			                                               @RequestParam(name = "userMobile", required = false) String userMobile,
			                                               @RequestParam(name = "orderDesc", required = false) String orderDesc,
			                                               @RequestParam(name = "orderLife", required = false) String orderLife,
			                                               @RequestParam(name = "frontendUrl", required = false) String frontendUrl,
				                                           @RequestParam(name = "backendUrl", required = false) String backendUrl,
				                                           @RequestParam(name = "productNo", required = false) String productNo,
				                                           @RequestParam(name = "terminalType", required = false) TerminalType terminalType,
				                                           @RequestParam(name = "orderPayStatus", required = false) String orderPayStatus,
				                                           @RequestParam(name = "orderPayTime", required = false) String orderPayTime,
				                                           @RequestParam(name = "payChannel", required = false) PayChannel payChannel,
				                                           @RequestParam(name = "mpcOrderNo", required = false) String mpcOrderNo,
				                                           @RequestParam(name = "pcOrderNo", required = false) String pcOrderNo,
				                                           @RequestParam(name = "pcUserNo", required = false) String pcUserNo,
				                                           @RequestParam(name = "sign", required = false) String sign,
				                                           HttpServletRequest request,
				                                           Website website) {
         
        Map<Integer, String> result = new HashMap();
        
        ShoppingOrder order = orderService.findOne(merchantOrderNo);
        if(order == null) {
        	logger.error("tsmPay异步通知异常，订单号不存在！接收订单号：{}", merchantOrderNo);
            result.put(500, "订单号不存在");
            return result;
        } else if(order.getStatus() != OrderStatus.waiting_pay && order.getStatus() != OrderStatus.transaction_dispose) {
        	logger.error("tsmPay异步通知异常，订单状态异常！当前状态：{}", order.getStatus().getDescription());
        	result.put(501, "订单状态异常");
            return result;
        } 
        try {
            QueryOrderResponseJson qoResponseJson = queryOrder(order);
            if(qoResponseJson == null || qoResponseJson.getPayUrl() == null) {
            	logger.error("tsmPay异步通知异常，tsmPay支付单不存在！");
                result.put(600, "tsmPay支付单不存在！");
                return result;
            }
            if(orderPayStatus.equals("1") && qoResponseJson.getOrderPayStatus() == 1) {
                if(payChannel == PayChannel.wechat_mp) {
                    try {
                        orderService.tsmPayNotify(order, OrderPayType.wechat, website, mpcOrderNo, request.getRemoteAddr());
                    } catch(Exception e) {
                        logger.error("异步回调返回失败结果集，tsmPayNotify()发生异常！订单号{}", order.getKey(), e);
                         //记录异常订单信息
                        orderService.payNotifyFailRecordOrder(order, OrderPayType.wechat, mpcOrderNo, request.getRemoteAddr());
                    }
                } else {
                	logger.error("tsmPay异步通知异常，商户暂不支持该支付方式，接收支付方式：{}", payChannel.name());
                    result.put(505, "商户暂不支持该支付方式");
                    return result;
                }
            } else {
                logger.info("----------tsmPay异步通知未支付结果，支付状态：{}-----------------", orderPayStatus);
            }
        } catch(Exception e) {
            logger.error("tsmPay支付异步通知异常", e);
            result.put(999, "系统错误，请及时联系商户！");
            return result;
        }
        result.put(0, "");
        return result;
    }
    
    //查询订单
    private QueryOrderResponseJson queryOrder(ShoppingOrder order) throws IOException {
        QueryOrderRequsetJson qoRequsetJson = new QueryOrderRequsetJson();
        qoRequsetJson.merchantNo = systemConfig.getMerchantNo();
        qoRequsetJson.merchantOrderNo = order.getKey();
        qoRequsetJson.timestamp = LMFDateUtil.format(new Date(), "yyyyMMddHHmmss");
        qoRequsetJson.key = systemConfig.getMerchantKey();
        QueryOrderResponseJson responeJson = tsmpayUtil.queryOrder(systemConfig.getTsmpayHost(), qoRequsetJson);
        return responeJson;
    }   
    
    private SubmitOrderResponseJson sumitOrder(ShoppingOrder order, OrderPayType payType) throws IOException {
        SubmitOrderRequestJson requestJson = new SubmitOrderRequestJson();  
        requestJson.merchantNo = systemConfig.getMerchantNo();
        requestJson.merchantOrderNo = order.getKey();
        requestJson.orderAmt = CommonUtil.price2Percent(order.getNeedPay());
        requestJson.userMobile = "";
        requestJson.orderDesc  =  payType.getDescription();
        requestJson.orderLife =   1440;
        requestJson.frontendUrl = systemConfig.getHostName() + "/jdvop/my/order/tsmPay/payReturn.php?orderKey=" + order.getKey();
        requestJson.backendUrl  = systemConfig.getHostName() + "/jdvop/tsmPay/payNotify.php";
        requestJson.productNo = "";
        requestJson.terminalType = TerminalType.wap;
        requestJson.payChannel = PayChannel.wechat_mp;
        requestJson.terminalNo = "";
        requestJson.auth_code  = "";
        requestJson.key = systemConfig.getMerchantKey();

        SubmitOrderResponseJson  responseJson =  tsmpayUtil.submitOrder(systemConfig.getTsmpayHost(), requestJson);
        
        return responseJson;
    }
    
}
