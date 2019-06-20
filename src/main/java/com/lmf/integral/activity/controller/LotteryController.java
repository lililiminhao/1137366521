/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.activity.controller;

import com.lmf.activity.entity.Award;
import com.lmf.activity.entity.AwardLog;
import com.lmf.activity.entity.Lottery;
import com.lmf.activity.enums.AwardStatus;
import com.lmf.activity.enums.AwardType;
import com.lmf.activity.service.AwardLogService;
import com.lmf.activity.service.LotteryService;
import com.lmf.activity.vo.AwardLogCriteria;
import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.enums.OwnerType;
import com.lmf.common.exceptions.PermissionDeniedException;
import com.lmf.common.util.MobileSupportUtil;
import com.lmf.common.util.PagerSpec;
import com.lmf.enums.ProductStatus;
import com.lmf.order.entity.OrderEntry;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.enums.DeliveryDateType;
import com.lmf.order.enums.OrderEntrySourceType;
import com.lmf.order.enums.OrderStatus;
import com.lmf.order.enums.OrderWithSystem;
import com.lmf.order.service.OrderService;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.StorageUnit;
import com.lmf.product.service.ProductService;
import com.lmf.sys.entity.ShipmentCompany;
import com.lmf.sys.service.GeoRegionService;
import com.lmf.sys.service.ShipmentCompanyService;
import com.lmf.system.sdk.service.SystemFinanceService;
import com.lmf.system.sdk.service.SystemProductService;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.entity.WebsiteUserAddress;
import com.lmf.website.enums.IntegralConsumeType;
import com.lmf.website.service.IntegralService;
import com.lmf.website.service.WebsiteUserAddressService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author liuqi
 */
@Controller
public class LotteryController {
    
    @Autowired
    private LotteryService lotteryService;
    
    @Autowired
    private IntegralService integralService;

    @Autowired
    private AwardLogService awardLogService;
        
    @Autowired
    private WebsiteUserAddressService websiteUserAddressService;
        
    @Autowired
    private GeoRegionService geoRegionService;
    
    @Autowired
    private ProductService productService;
        
    @Autowired
    private SystemProductService systemProductService;
            
    @Autowired
    private SystemFinanceService systemFinanceService;
    
    @Autowired
    private ShipmentCompanyService shipmentCompanyService;
                
    @Autowired
    private OrderService orderService;
            
    private final Logger log = LoggerFactory.getLogger(LotteryController.class);
    
     //获取积分商城抽奖活动页面
     @RequestMapping(value = "/activity/lottery.php", method = RequestMethod.GET)
     public String view(@RequestParam("id") int lotteryId, 
                        @PagerSpecDefaults(pageSize = 4, sort = "create_time.desc") PagerSpec pager, Model model) {
         
        Lottery lottery = lotteryService.findOne(lotteryId);
        if(lottery == null) {
            throw new RuntimeException();
        }
        Page<AwardLog> page = awardLogService.find(new AwardLogCriteria().withLotteryId(lotteryId), pager);
        
        model.addAttribute("winlist", page.getContent());
        model.addAttribute("lottery", lottery);
        return "/activity/lottery/form";
     }
     
     /**
      * 点击 《马上抽奖》
      * @param lotteryId
      * @param websiteUser
      * @param website
      * @param request
      * @return 
      */
     @RequestMapping(value = "/activity/lottery.php", method = RequestMethod.POST)
     public @ResponseBody SimpleJsonResponse draw(@RequestParam("id") int lotteryId, 
                                                  WebsiteUser websiteUser,
                                                  Website website,
                                                  HttpServletRequest request){
        if(websiteUser == null || websiteUser.getId() <= 0){
            Map<String,Object> hashMap = new HashMap<>();
            hashMap.put("nologin", true);
            hashMap.put("retUrl", "/activity/lottery.php?id="+lotteryId);
            return new SimpleJsonResponse(false, hashMap);
        }else{
            //check 活动是否存在
            Lottery lottery = lotteryService.findOne(lotteryId);
            if(lottery == null) {
                return new SimpleJsonResponse(false, "noExist");
            }
            //check 是否有抽奖所需积分
            Integer needInteger = lottery.getNeedIntegral();
            if(websiteUser.getIntegral() < needInteger ){
                return new SimpleJsonResponse(false, "noIntegral");
            }
            //check 是否超过参与限制
            Integer limitPeople = lottery.getLimitPeople();
            if(null != limitPeople && limitPeople != 0){
                long consumeCount = integralService.getCountByConsumeType(websiteUser, IntegralConsumeType.lottery, lotteryId);
                if(consumeCount >= limitPeople){
                    return new SimpleJsonResponse(false, "overlimit");
                }
            }
            Map<String,Long> rs = lotteryService.drawLottery(website, websiteUser, lottery, request.getRemoteAddr());
            long selectBoxIndex = rs.get("selectBoxIndex");
            HashMap<String,Object> hashMap = new HashMap<>();
            
            hashMap.put("NUM", selectBoxIndex);
            for (Award award: lottery.getAwards()) {
                if(award.getBoxIndex() == selectBoxIndex){
                    hashMap.put("WIN", "win");
                    hashMap.put("MSG", award.getName());
                    hashMap.put("IMAGE", award.getImage());
                    hashMap.put("LOGID",rs.get("logId"));
                    AwardType type = award.getType();
                    if(type == AwardType.custom_award){
                        hashMap.put("TYPE","CUSTOM");
                    }
                    if(type == AwardType.exist_award){
                        hashMap.put("TYPE","EXIST");
                    }
                    if(type == AwardType.integral){
                        hashMap.put("TYPE","INTEGRAL");
                    }
                    break;
                }
            }
            return new SimpleJsonResponse(true, hashMap);
        }
     }
     
    /**
     * 客户端 AJAX请求 加载 中奖名单
     * @param lotteryId
     * @param pager
     * @return 
     */
    @RequestMapping(value="/activity/lottery/winningList.php",method = RequestMethod.GET)
    public @ResponseBody SimpleJsonResponse winningList(@RequestParam("id") int lotteryId,
                                                        @PagerSpecDefaults(pageSize = 4, sort = "create_time.desc") PagerSpec pager){
         Page<AwardLog> page = awardLogService.find(new AwardLogCriteria().withLotteryId(lotteryId), pager);
        return new SimpleJsonResponse(true,page.getContent());
    }
    
    //抽中实物后 调到中奖页面
    @RequestMapping(value="/activity/lottery/order.php",method = RequestMethod.GET)
    public String viewOrder(HttpServletRequest request,
            @RequestParam(value = "id", required = false) Integer awardLogId,
            @RequestParam(value = "addressId", required = false) Integer addressId,
            WebsiteUser websiteUser, Model model){
        //当前登陆用户所有地址信息
        List<WebsiteUserAddress> addressList = websiteUserAddressService.find(websiteUser);
        model.addAttribute("addressService", websiteUserAddressService);
        model.addAttribute("addressList", addressList);
        if(addressId != null){
            model.addAttribute("addressId", addressId);
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        boolean isMobile = MobileSupportUtil.isMobile(attributes.getRequest());
        if(!isMobile){
            model.addAttribute("provinces", geoRegionService.findAllProvince());
        }
        //中奖的详细信息
        HttpSession session = request.getSession(true);
        if(awardLogId == null){
            if(session.getAttribute("awardLogId") == null)
                throw new RuntimeException();
            awardLogId = (Integer) session.getAttribute("awardLogId");
        }
        AwardLog awardLog = awardLogService.findOne(awardLogId);
        model.addAttribute("awardLog", awardLog);
        session.setAttribute("awardLogId", awardLogId);
        return "/activity/lottery/order";
    }
    
    //如果是抽中的是实物  填写收货地址提交中奖
    @RequestMapping(value="/activity/lottery/submitOrder.php",method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse submitOrder(HttpServletRequest request,WebsiteUser websiteUser, Website website,
            @RequestParam(value = "myAddressId") Long addressId,
            @RequestParam("awardLogId") long awardLogId){
        if(websiteUser == null || websiteUser.getId() <= 0){
            throw new PermissionDeniedException();
        }
        WebsiteUserAddress address = websiteUserAddressService.findOne(addressId);
        if(address == null || address.getId() < 0){
            return new SimpleJsonResponse(false, "地址信息不存在或已经被删除,请您重新选择送货地址");
        }
        if (address.getAddress() == null || "".equals(address.getAddress()) 
                || address.getReceiverName() ==null || "".equals(address.getReceiverName())) {
            return new SimpleJsonResponse(false, "请补全您的送货地址信息");
        }
        if(address.getUserId() != websiteUser.getId()){
            throw new PermissionDeniedException();
        }
        if(address.getMobile() == null || address.getMobile().isEmpty()){
            if(address.getPhone() == null || address.getPhone().isEmpty()){
                return new SimpleJsonResponse(false, "手机号码和固定电话必须填写一个,请补全您的送货地址信息");
            }
        }
        //如果奖品是自定义商品，  直接修改 中奖名单信息（添加收货地址）
        AwardLog awardLog = awardLogService.findOne(awardLogId);
        awardLog.setCounty(address.getCounty());
        awardLog.setCity(address.getCity());
        awardLog.setTown(address.getTown());
        awardLog.setProvince(address.getProvince());
        awardLog.setReceiverAddr(address.getAddress());
        awardLog.setReceiverName(address.getReceiverName());
        awardLog.setReceiverPhone(address.getPhone());
        awardLog.setReceiverMobile(address.getMobile());
        
        AwardType type = awardLog.getAwardType();
        if(AwardType.custom_award == type){
            awardLog.setAwardStatus(AwardStatus.no_sent);//状态更新为未发送
            awardLogService.save(awardLog);
        }else if(AwardType.exist_award == type){
            Integer productId = Integer.valueOf(awardLog.getExt().get("productId").toString());
            //检查自有产品库存，和供应商产品库存，系统库存
            Map<Boolean, String>  result = checkOrder(productId, website);
            String message = result.get(false);
            if(message != null && !message.isEmpty()) {
                return new SimpleJsonResponse(false, message);
            }
            Product product = productService.findOne(productId);
            //如果奖品是已上传商品，  创建订单，记录日志
            ShoppingOrder order = new ShoppingOrder();
            order.setReceiverName(address.getReceiverName());
            order.setReceiverMobile(address.getMobile());
            order.setReceiverPhone(address.getPhone());
            order.setReceiverAddr(address.getAddress());
            order.setProvince(address.getProvince());
            order.setCity(address.getCity());
            order.setCounty(address.getCounty());
            order.setTown(address.getTown());
            order.setUserId(websiteUser.getId().intValue());
            order.setDeprecated(false);
            order.setRemoteIPAddr(request.getRemoteAddr());
            order.setDeliveryObjectType(product.getOwnerType());
            if(product.getOwnerType() == OwnerType.provider) {
                order.setDeliveryObjectId(product.getOwnerId());
            }
            if(OwnerType.system == product.getOwnerType()){
                order.setHasSystem(OrderWithSystem.all);
                order.setStatus(OrderStatus.waiting_confirmed);
            }else if(OwnerType.provider == product.getOwnerType() || OwnerType.enterprise == product.getOwnerType()){
                order.setHasSystem(OrderWithSystem.none);
                order.setStatus(OrderStatus.waiting_audit);
            }else{//抽奖是没有京东商品的。
                throw new RuntimeException();
            }
            order.setTotalCost(product.getRetailPrice());
            order.setNeedPay(0);//抽中的奖不需要付钱
            order.setDeliveryDateType(DeliveryDateType.any);
            ShipmentCompany shipmentCompany = null;
            if(website.getShipmentCompanyId() != null) {
                shipmentCompany = shipmentCompanyService.findOne(website.getShipmentCompanyId());
            }
            //开始设置默认仓库以及默认的发货物流公司
            if (shipmentCompany == null) {
                shipmentCompany = product.getPreferShipmentCompany();
            }
            order.setShipmentCompany(shipmentCompany);
            try {
                OrderEntry entry = new OrderEntry();
                StorageUnit storageUnit = productService.findStorageUnits(product).get(0);
                entry.setStorageUnitId(storageUnit.getId());
                entry.setSourceEntryType(OrderEntrySourceType.lottery);//来源活动
                entry.setSourceObjectId(product.getId());
                entry.setSoldPrice(product.getRetailPrice());
                entry.setAmount(1);
                lotteryService.createAwardOrder(order, entry, awardLog, website);
            } catch (Exception e) {
                log.error("error" + e.getMessage());
                return new SimpleJsonResponse(false, "订单提交失败");
            }
        }else{//奖品为积分时 是不需要提及订单
            throw new RuntimeException();
        }
        return new SimpleJsonResponse(true, null);
    }
    
    @RequestMapping(value="/activity/lottery/successOrder.php",method = RequestMethod.GET)
    public String successOrder(){
        return "/activity/lottery/order_success";
    }
    
    //当抽中的奖品是库存的产品。提交前先做校验
    private Map<Boolean, String> checkOrder(int productId, Website website){
        Product product = productService.findOne(productId);
        Map<Boolean, String> result = new HashMap();
        result.put(true, null);
        if(product == null){
            result.put(false, "商品不存在，请联系客服");
            return result;
        }
        if(product.getStatus() != ProductStatus.selling) {
            result.put(false, "商品已下架, 请联系客服");
        }
        if(product.getOwnerType() == OwnerType.system){//如果奖品是系统商品  check是否有库存， 网站是否有余额。。  企业和供应商商品不需要校验因为抽中奖的时候已经锁住了
            try {
                Map<String, Object> stockResult = systemProductService.productStock(product.getSystemProductId(), 1, null, website);
                if(stockResult != null) {
                    boolean storageResult = (boolean) stockResult.get("stock_status");
                    if(!storageResult) {
                        result.put(false, product.getName() + "商品已经售完，请联系客服");
                        return result;
                    }
                }
            } catch(Exception e) {
                result.put(false, product.getName() + "商品已经售完，请联系客服");
                return result;
            }
            try {
                Map<String, Object> financeResult = systemFinanceService.balanceRemain(website);
                if(financeResult != null) {
                    double remain = (double) financeResult.get("REMAIN");
                    double creditTotal = (double) financeResult.get("CREDIT_TOTAL");
                    double creditUsed  = (double) financeResult.get("CREDIT_USED");
                    if((remain + creditTotal - creditUsed) < product.getSystemPrice()) {
                        result.put(false, "网站预存款不足，请通知管理员尽快充值！");
                        return result;
                    }
                } else {
                    result.put(false, "网站预存款不足，请通知管理员尽快充值！");
                    return result;
                }
            } catch(Exception e) {
                result.put(false, "网站预存款不足，请通知管理员尽快充值！");
                return result;
            }
        }
       return result;
    }
    
    //我的奖品 功能
    @RequestMapping(value="/my/lottery/myAwards.php",method = RequestMethod.GET)
    public String myAwards(WebsiteUser websiteUser, @PagerSpecDefaults(pageSize = 20, sort = "create_time.desc") PagerSpec pager, Model model) {
        if(websiteUser == null || websiteUser.getId() <= 0){
            throw new PermissionDeniedException();
        }
        Long userId = websiteUser.getId();
        Page<AwardLog> page = awardLogService.find(new AwardLogCriteria().withUserId(userId.intValue()), pager);
        List<AwardLog> list = page.getContent();
        if(list != null){
            Map map = new HashMap() ;
            for(AwardLog log: list) {
                if(log.getAwardStatus() == AwardStatus.enter_order_process){
                    Integer orderId = (Integer)log.getExt().get("orderId");
                    if(orderId != null){
                        ShoppingOrder order = orderService.findOne(orderId);
                        if(order != null){
                            map.put(orderId, order.getStatus());
                        }
                    }
                }
            }
            model.addAttribute("orderStatus", map);
        }
        model.addAttribute("link", "/jdvop/my/lottery/myAwards.php?page=[:page]");
        model.addAttribute("myAwards", page);
        return "/user_center/awards";
    }
}
