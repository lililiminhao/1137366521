/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.misc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmf.common.Constant;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.CommonUtil;
import com.lmf.extend.pay.smkpay.SmkpayUtil;
import com.lmf.extend.pay.smkpay.enums.Mertxtype;
import com.lmf.extend.pay.smkpay.json.SmkQueryRequestJson;
import com.lmf.extend.pay.smkpay.pojo.SmkpayOrderRequest;
import com.lmf.extend.pay.smkpay.util.CertificateCoder;
import com.lmf.integral.SystemConfig;
import com.lmf.order.entity.OrderEntry;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.enums.OrderPayType;
import com.lmf.order.enums.OrderStatus;
import com.lmf.order.service.OrderService;
import com.lmf.product.entity.Product;
import com.lmf.product.service.ProductService;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteUser;

/**
 * 市民卡支付控制类
 *
 * @author shenzhixiong
 */
@Controller
public class SmkpayController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;
    
    @Autowired
    private SmkpayUtil smkpayUtil;
    
    @Autowired
    private SystemConfig systemConfig;

    @Autowired
    private TimeZone tz;
    
    private final Logger logger = LoggerFactory.getLogger(SmkpayController.class);

    private final JavaType mapType;

    private final ObjectMapper mapper = new ObjectMapper();

    public SmkpayController() {
        mapType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
    }

    @RequestMapping(value = "/my/order/shiminkaPay/topay.php")
    public @ResponseBody SimpleJsonResponse toPay(@RequestParam("id") long orderId, WebsiteUser websiteUser) throws Exception {
        ShoppingOrder order = orderService.findOne(orderId);
        if(order == null) {
            logger.error("Order data is null!");
            return new SimpleJsonResponse(false, "系统异常，请刷新后重新尝试！");
        }
        if (!Objects.equals(order.getUserId(), websiteUser.getId().intValue())) {
            logger.error("The order does not belong to the current user!");
            return new SimpleJsonResponse(false, "系统异常，请刷新后重新尝试！");
        }
        if(OrderStatus.waiting_pay != order.getStatus()) {
            return new SimpleJsonResponse(false, "您已支付过此订单了，无需再次支付！");
        }
        SmkpayOrderRequest request = new SmkpayOrderRequest();
        request.sendClient = systemConfig.getSendClient();
        request.sendChl = systemConfig.getSendChl();
        request.callBackUrl = systemConfig.getHostName() + "/jdvop/shiminkaPay/payReturn.php";
        request.userId = websiteUser.getLoginName();
        
        SmkpayOrderRequest.Order orderResult = request.new Order();
        orderResult.merCode = systemConfig.getMerCode();
        orderResult.storeid = "";
        
        Calendar cal = Calendar.getInstance(tz);
        Date dateTime = cal.getTime();
        orderResult.dateTime = dateTime;
        
        StringBuilder orderInfo = new StringBuilder();
        
        int totalNum = 0;
        List<OrderEntry> entries = orderService.findEntries(order);
        int entrySize = entries.size();
        for (int i = 0; i < entrySize; ++i) {
            OrderEntry e = entries.get(i);
            totalNum += e.getAmount();
            if (i == 0) {
                Product product = productService.findOne(new Long(e.getSourceObjectId()).intValue());
                if(product.getName().length() < 15) {
                	orderInfo.append(product.getName());
                } else {
                	orderInfo.append(product.getName().substring(0, 15) + "...");
                }
                orderInfo.append('×').append(e.getAmount()).append(';');
            }
        }
        if (totalNum > 1) {
            orderInfo.append("...，共 ").append(totalNum).append(" 件");
        }
        
        orderResult.goods = orderInfo.toString();
        orderResult.cardnumber = "";
        orderResult.amount = CommonUtil.price2Percent(order.getNeedPay());
        orderResult.mertxtypeid = Mertxtype.PSTX;
        orderResult.orderNo = order.getKey();
        request.order = orderResult;
        orderResult.mersign = CertificateCoder.sign(request.getSign(), systemConfig.getPfxPassword());
        
        String resultParams = smkpayUtil.submitOrder(systemConfig.getApiHost(), request);
        
        return new SimpleJsonResponse(true, systemConfig.getWapSubmitUrl() + resultParams);
    }
    
    @RequestMapping(value = "/shiminkaPay/payReturn.php", method = RequestMethod.GET)
    public String payReturn(@RequestParam("status") String status, @RequestParam("orderNo") String orderNo, Website website, Model model,
                            HttpServletRequest request) throws IOException {
        logger.info("====支付完毕，请求来源referer："+ request.getHeader("referer"));
        if("02".equals(status)) {
            model.addAttribute("message", "支付失败，请您重新支付！");
            return "shopping/shiminka_pay/pay_fail";
        }
        
        ShoppingOrder order = orderService.findOne(orderNo);
        if(order == null) {
            model.addAttribute("message", "系统异常，请稍后在尝试！");
            return "shopping/shiminka_pay/pay_fail";
        }
        logger.info("====支付完毕，回掉请求状态："+status+"，订单key："+orderNo+"=======");
        logger.info("====支付完毕，环境判断："+systemConfig.isDevelopEnvironment()+"，订单状态："+order.getStatus()+"=======");
        if(order.getStatus() == OrderStatus.waiting_pay) 
        {
        	logger.info("====支付完毕，订单状态判断：true========");
        }else {
        	logger.info("====支付完毕，订单状态判断：false========");
        }
        
        if (systemConfig.isDevelopEnvironment() && order.getStatus() == OrderStatus.waiting_pay) {
            try {
            	logger.info("==========进入payReturn订单处理===========");
                orderService.shiminkaPayNotify(order, website, "0123456789", request.getRemoteAddr());
            } catch(Exception e) {
                ShoppingOrder shoppingOrder = orderService.findOne(orderNo);
                logger.error("异步回调返回失败结果集，shiminkaPayNotify()发生异常！订单号{}", shoppingOrder.getKey());

                //记录异常订单信息
                orderService.payNotifyFailRecordOrder(shoppingOrder, OrderPayType.shiminka_pay, "0123456789", request.getRemoteAddr());
            }
        }
        return "redirect:/my/mobile/orders.php";
    }
    
    @RequestMapping(value = "/smkpay/aycOrderNotify.php", method = RequestMethod.POST)
    public @ResponseBody String orderNotify(@RequestParam(name = "reqSeq", required = false) String reqSeq,
                                            @RequestParam(name = "merCode", required = false) String merCode,
                                            @RequestParam(name = "serialNo", required = false) String serialNo,
                                            @RequestParam(name = "orderNo", required = false) String orderNo,
                                            @RequestParam(name = "amount", required = false) String amount,
                                            @RequestParam(name = "status", required = false) String status,
                                            @RequestParam(name = "respCode", required = false) String respCode,
                                            @RequestParam(name = "respDesc", required = false) String respDesc,
                                            @RequestParam(name = "sign", required = false) String sign,
                                            HttpServletRequest request, Website website) throws IOException, Exception {
        logger.info("=====================进入aycnotify============");
    	Map<String, String> result = new HashMap<>(4);
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("reqSeq", reqSeq);
        requestMap.put("merCode", merCode);
        requestMap.put("serialNo", serialNo);
        requestMap.put("orderNo", orderNo);
        requestMap.put("amount", amount);
        requestMap.put("status", status);
        requestMap.put("sign", sign);
        //参数校验
        SimpleJsonResponse<String> checkRequest = checkRequest(requestMap);
        if(checkRequest.isIsOk()){
             //TODO 签名验证
            ShoppingOrder shoppingOrder = orderService.findOne(orderNo);
            if (shoppingOrder == null) {
            	logger.error("市民卡异步通知，无相关订单信息！");
                result.put("return_code", "02");
                result.put("return_msg", "无相关订单信息！");
            } else {
                boolean success = true;
                if(status.equals("01")) {
                    SmkQueryRequestJson queryJson = new SmkQueryRequestJson();
                    queryJson.reqSeq = reqSeq;
                    queryJson.merCode = systemConfig.getMerCode();
                    queryJson.orderNo = orderNo;
                    queryJson.channelNo = "004";
                    queryJson.serialNo = serialNo;
                    queryJson.tradeType = String.valueOf(Constant.ZERO);
                    logger.info("-----------------------市民卡支付异步通知交易查询接口签名参数:{}", queryJson.getMersign());
                    queryJson.sign = CertificateCoder.sign(queryJson.getMersign(), systemConfig.getPfxPassword());
                    logger.info("------------------------市民卡支付异步通知调用交易查询接口反查，请求参数：" + queryJson);
                    Map<String, Object> queryOrderResponse = smkpayUtil.queryOrder(systemConfig.getSmkpayRefundUrl() + "query", queryJson);
                    logger.info("------------------------市民卡支付异步通知调用交易查询接口反查，结果：" + queryOrderResponse);
                    if(queryOrderResponse != null && queryOrderResponse.get("status") != null && queryOrderResponse.get("status").equals("01")) {
                        if(shoppingOrder.getStatus() == OrderStatus.waiting_pay || shoppingOrder.getStatus() == OrderStatus.transaction_dispose) {
                            try {
                                orderService.shiminkaPayNotify(shoppingOrder, website, serialNo, request.getRemoteAddr());
                            } catch(Exception e) {
                                logger.error("异步回调返回失败结果集，shiminkaPayNotify()发生异常！订单号{}", shoppingOrder.getKey(), e);

                                //记录异常订单信息
                                orderService.payNotifyFailRecordOrder(shoppingOrder, OrderPayType.shiminka_pay, requestMap.get("serialNo").toString(), request.getRemoteAddr());
                            }
                        } else {
                            success = false;
                            result.put("return_code", "02");
                            result.put("return_msg", "订单状态异常！");
                            logger.error("市民卡异步通知，订单状态异常！");
                        }
                    } else {
                        if(queryOrderResponse != null && queryOrderResponse.get("status") != null) {
                        	if(queryOrderResponse.get("status").equals("03")) {
                        		try {
                        			orderService.shiminkaPayTransactionDispose(shoppingOrder, serialNo, request.getRemoteAddr(), "");
                        		} catch (Exception e) {
                        			logger.error("市民卡支付异步回调，写入交易处理中日志异常", e);
                        		}
                        	} else {
                    			logger.error("市民卡支付异步回调返回,查询订单非成功状态，状态码{}", queryOrderResponse.get("status"));
                        	}
                        } else {
                            logger.error("市民卡支付异步通知时，查询订单返回空结果集");
                        }
                    }
                } else {
                    success = false;
                    logger.error("异步回调返回失败结果集！状态码{}", status);
                }
                if(success) {
                    result.put("return_code", "00");
                    result.put("return_msg", "操作成功！");
                }
            }
        }else{
            result.put("return_code", "02");
            result.put("return_msg", checkRequest.getMessage());
        }
        try {
            return mapper.writeValueAsString(result);
        } catch (JsonProcessingException ex) {
            logger.error("处理市民卡支付结果异步回调异常，返回响应错误！\n", ex);
        }
        return null;
    }
    

    /**
     * 校验支付结果异步通知的参数
     *
     * @param requestMap
     * @return
     */
    private SimpleJsonResponse<String> checkRequest(Map<String, Object> requestMap) {
        if (requestMap.size() <= 0) {
            return new SimpleJsonResponse(false, "请输入请求参数！");
        } else {
            String[] paramNames = {"reqSeq", "merCode", "serialNo", "orderNo", "amount", "status", "sign"};
            String[] paramDesc = {"请求流水", "商户号", "交易流水号", "订单号", "订单金额", "支付状态", "签名"};
            for (int index = 0; index < paramNames.length; index++) {
                if (requestMap.get(paramNames[index]) == null) {
                    return new SimpleJsonResponse(false, paramDesc[index] + "不可为空！");
                }
            }
        }
        return new SimpleJsonResponse(true, "参数校验成功！");
    }
    
    
    
}
