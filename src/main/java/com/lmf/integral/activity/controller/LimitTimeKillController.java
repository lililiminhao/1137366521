/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.activity.controller;

import com.lmf.activity.entity.ActivityProductUser;
import com.lmf.activity.entity.FlashSaleActivity;
import com.lmf.activity.entity.FlashSaleProduct;
import com.lmf.activity.service.ActivityProductUserService;
import com.lmf.activity.service.LimitExchangeService;
import com.lmf.activity.service.TimeSpikeService;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.enums.OwnerType;
import com.lmf.common.exceptions.PermissionDeniedException;
import com.lmf.common.util.MobileSupportUtil;
import com.lmf.enums.ProductStatus;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.order.entity.OrderEntry;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.enums.DeliveryDateType;
import com.lmf.order.enums.OrderEntrySourceType;
import com.lmf.order.enums.OrderStatus;
import com.lmf.order.enums.OrderWithSystem;
import com.lmf.order.service.OrderHelper;
import com.lmf.order.service.OrderService;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.ProductDescription;
import com.lmf.product.entity.ProductImage;
import com.lmf.product.entity.StorageUnit;
import com.lmf.product.service.ProductDescriptionService;
import com.lmf.product.service.ProductImageService;
import com.lmf.product.service.ProductService;
import com.lmf.product.service.ProductStorageService;
import com.lmf.sys.entity.GeoRegion;
import com.lmf.sys.entity.ShipmentCompany;
import com.lmf.sys.service.GeoRegionService;
import com.lmf.sys.service.ShipmentCompanyService;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.entity.WebsiteUserAddress;
import com.lmf.website.service.WebsiteUserAddressService;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
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
 *
 * @author zhixiong.wu
 * 限时秒杀
 */
@Controller
public class LimitTimeKillController {
    
    @Autowired
    private TimeSpikeService timeSpikeService;
    
    @Autowired
    private LimitExchangeService limitExchangeService;
    @Autowired
    private WebsiteUserAddressService websiteUserAddressService;
    
    @Autowired
    private ShipmentCompanyService shipmentCompanyService;
        
    @Autowired
    private GeoRegionService geoRegionService;
        
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderHelper orderHelper;
    
    @Autowired
    private OrderService orderService;
        
    @Autowired
    private ProductDescriptionService  productDescriptionService;
    
    @Autowired
    private ProductImageService productImageService;
    
    @Autowired
    private ProductStorageService productStorageService;
    
    @Autowired
    private ActivityProductUserService acuService;
    
    private Map<Long,List<String>> isMap = new HashMap<Long,List<String>>();
    
    @RequestMapping(value = "/activity/toLimitTimeKillView.php", method = RequestMethod.GET) 
    public String toLimitTimeKillView(WebsiteUser websiteUser,
                                      @RequestParam(value="day",required=false) String day,
                                      Website website,
                                      HttpServletRequest request,
                                      Model model){
        
        SimpleDateFormat sbf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat hh = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        if(day==null||"".equals(day)){//today
            
            Date today = new Date();
            String todayTime = sbf.format(today);
            //今日秒杀的 活动
            List<FlashSaleActivity> activityProductsToday = timeSpikeService.find(todayTime);
            
            for(FlashSaleActivity a:activityProductsToday){
                
                Long id = a.getId();
                List<FlashSaleProduct> product = timeSpikeService.findOne(id);
                int num = 0;
                for(FlashSaleProduct p:product){
                    num+=p.getRemaindAmount();//如果一个活动下面的所有产品剩余数量都为0 则代表此活动已结束
                }
                a.setNum(num);
            }
            model.addAttribute("productsToday", activityProductsToday);
            //直接传递service 可以再页面直接调用接口中的方法
            model.addAttribute("timeSpikeService", timeSpikeService);
            model.addAttribute("nowDate",new Date());
            model.addAttribute("orderHelper",orderHelper);//去检查产品的库存
            model.addAttribute("website",website);
            model.addAttribute("request",request);
            model.addAttribute("model",model);
            model.addAttribute("todayTime",todayTime);
            model.addAttribute("productService",productService);
            return "/activity/timeSpike/seckill_today";
        }else{//tormory
            
            Calendar c=Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DATE,+1);
            Date date = c.getTime();
            String todayTime = sbf.format(date);
            
            List<FlashSaleActivity> activityProductsToday = timeSpikeService.find(todayTime);
            model.addAttribute("productsToday", activityProductsToday);
            //直接传递service 可以再页面直接调用接口中的方法
            model.addAttribute("timeSpikeService", timeSpikeService);
            model.addAttribute("productService", productService);
            
            model.addAttribute("orderHelper",orderHelper);//去检查产品的库存
            model.addAttribute("website",website);
            model.addAttribute("request",request);
            model.addAttribute("model",model);
            model.addAttribute("todayTime",todayTime);
            if(activityProductsToday.size()!=0){
                model.addAttribute("nowDate",activityProductsToday.get(0).getEndTime());
            }
            
            return "/activity/timeSpike/seckill_tomorrow";
        }
        
        
    }
    
    /***
     * 产品详情
     * @param productId
     * @param aid
     * @param todayTime 详情页面看看其他根据此时间查询
     * @param website
     * @param request
     * @param model
     * @return 
     */
    @RequestMapping(value="/activity/toTimeSpikeDetail.php",method=RequestMethod.GET)
    public String toTimeSpikeDetail(@RequestParam(value = "productId",required=false) Long productId,
                                    @RequestParam(value = "aid",required=false) Long aid,
                                    @RequestParam(value = "time",required=false) String todayTime,
                                    Website website,
                                    HttpServletRequest request,
                                    Model model){
        
        List<FlashSaleActivity> activityProductsToday = timeSpikeService.find(todayTime);
        
        model.addAttribute("productsToday", activityProductsToday);
        model.addAttribute("todayTime", todayTime);
        model.addAttribute("timeSpikeService", timeSpikeService);
        FlashSaleProduct flashProduct = timeSpikeService.findFlashSaleProduct(productId);
        //根据活动id查询活动
        FlashSaleActivity activity = timeSpikeService.findFlashSaleActivity(aid);
        
        Product product = productService.findOne(flashProduct.getProductId());
       
        //修改活动的浏览次数
        timeSpikeService.updateVisitNum(aid);
        try {
            model.addAttribute("isStock", orderHelper.checkExistStockBeforeBuy(product, website, request, model));
        } catch (IOException ex) {
            Logger.getLogger(LimitTimeKillController.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<ProductImage> productImageList = productImageService.find(product);    //产品图片
        ProductDescription productDescription   = productDescriptionService.findOne(product.getId());   //产品详情
        model.addAttribute("flashProduct", flashProduct);
        model.addAttribute("product", product);
        model.addAttribute("nowDate", new Date());
        model.addAttribute("productImageList", productImageList);
        model.addAttribute("productDescription", productDescription);
        model.addAttribute("activity", activity);
        model.addAttribute("aid", aid);
        boolean isSelling = true;
        if(product.getStatus() != ProductStatus.selling) {
            isSelling = false;
        }
        model.addAttribute("isSelling", isSelling);
        if(product.getOwnerType() == OwnerType.jingdong) {
            GeoRegion province = null, city = null, county = null, town = null;
            //京东地址集合
            Set<GeoRegion> provinces = geoRegionService.findAllProvince();
            if (provinces != null && provinces.size() > 0) {
                //从cookie中取用户上次使用的地址
                Cookie[] cookies = request.getCookies();
                Integer provinceCode = 0, cityCode = 0, countyCode = 0;
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (null != cookie.getName()) switch (cookie.getName()) {
                            case "provinceCode":
                                provinceCode = Integer.parseInt(cookie.getValue());
                                break;
                            case "cityCode":
                                cityCode = Integer.parseInt(cookie.getValue());
                                break;
                            case "countyCode":
                                countyCode = Integer.parseInt(cookie.getValue());
                                break;
                        }
                    }
                }
                if (provinceCode != 0) {
                    province = geoRegionService.findOne(provinceCode);
                    model.addAttribute("provinceCode", provinceCode);
                } else {
                    province = (GeoRegion) provinces.toArray()[0];
                }
                model.addAttribute("provinces", provinces);
                List<GeoRegion> citys = geoRegionService.childs(province);
                if (citys != null && citys.size() > 0) {
                    if (cityCode != 0) {
                        city = geoRegionService.findOne(cityCode);
                        model.addAttribute("cityCode", cityCode);
                    } else {
                        city = citys.get(0);
                    }
                    model.addAttribute("citys", citys);
                    List<GeoRegion> countys = geoRegionService.childs(city);
                    if (countys != null && countys.size() > 0) {
                        if (countyCode != 0) {
                            county = geoRegionService.findOne(countyCode);
                            model.addAttribute("countyCode", countyCode);
                        } else {
                            county = countys.get(0);
                        }
                        model.addAttribute("countys", countys);
                    }
                }
            }
            return "/activity/timeSpike/seckillDetail_jd";
        }
        
        return "/activity/timeSpike/seckillDetail";
    }
    
    /***
     * 跳到下单的页面
     * @param request
     * @param returnUrl 重定向的URL
     * @param productId 产品id
     * @param aid 活动id
     * @param amount 购买数量
     * @param addressId 地址id
     * @param websiteUser 用户
     * @param model 作用域
     * @return 
     */
    @RequestMapping(value="/activity/toTimeSpikeOrder.php",method=RequestMethod.GET)
    public String toTimeSpikeOrder(HttpServletRequest request,
            @RequestParam(value = "returnUrl", required = false) String returnUrl,
            @RequestParam(value = "productId", required = false) Long productId,
            @RequestParam(value = "aid", required = false) Long aid,
            @RequestParam(value = "amount", required = false) Integer amount,
            @RequestParam(value = "time",required=false) String todayTime,
            @RequestParam(value = "addressId", required = false) Integer addressId,
            WebsiteUser websiteUser, Model model){
        
        if(websiteUser == null || websiteUser.getId() <= 0){
            //如果当前未登陆,则跳转至登陆页面
            returnUrl = "/activity/toTimeSpikeDetail.php?productId=" + productId + "&aid="+aid+"&time="+todayTime;
            model.addAttribute("retUrl", returnUrl);
            return "/login";
        }
        
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
        FlashSaleProduct flashProduct = limitExchangeService.findOne(productId);
        
        Product product = productService.findOne(flashProduct.getProductId());
        model.addAttribute("product", product);
        model.addAttribute("activityId", flashProduct.getActivityId());
        model.addAttribute("flashProduct", flashProduct);
        model.addAttribute("amount", 1);
//        session.setAttribute("pid", pid);
//        session.setAttribute("amount", amount);
        return "/activity/timeSpike/seckillOrder";
        
    }
    
    /****
     * 提交订单
     * @param flashPid 产品id
     * @param addressId 地址id
     * @param amount 购买的数量
     * @param request
     * @param websiteUser 用户
     * @param website 网站
     * @param currentUser
     * @return 
     */
    @RequestMapping(value="/activity/submitOrder.php",method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse submitOrder(@RequestParam("fpid") Long flashPid,
                                                    @RequestParam(value = "myAddressId") Long addressId,
                                                    @RequestParam(value = "amount") Integer amount,
                                                    @RequestParam(value = "activityId") Integer activityId,
                                                    HttpServletRequest request,
                                                    WebsiteUser websiteUser,
                                                    Website website,
                                                    UserDetail currentUser){
        List<String> array = new ArrayList<String>();
        List<String> newArray = new ArrayList<String>();
        if(websiteUser == null || websiteUser.getId() <= 0){
            throw new PermissionDeniedException();
        }
        WebsiteUserAddress address = websiteUserAddressService.findOne(addressId);
        if(address == null || address.getId() < 0){
            return new SimpleJsonResponse(false, "地址信息不存在或已经被删除,请您重新选择送货地址");
        }
        if(address.getUserId() != websiteUser.getId()){
            throw new PermissionDeniedException();
        }
        if(address.getMobile() == null || address.getMobile().isEmpty()){
            if(address.getPhone() == null || address.getPhone().isEmpty()){
                return new SimpleJsonResponse(false, "手机号码和固定电话必须填写一个,请补全您的送货地址信息");
            }
        }
        
        //check积分
        FlashSaleProduct fp = limitExchangeService.findOne(flashPid);
        if(amount * fp.getDisplayDiscountPrice() * website.getRatio() > currentUser.getIntegral()){
            return new SimpleJsonResponse(false, "积分不足，请再接再厉");
        }
        //check产品库存 网站存款
        Map<Boolean, String>  result = new HashMap<Boolean, String>();
        try {
            result = orderHelper.checkOrderBeforeSubmit(website, fp.getProductId() , amount, address);
        } catch (IOException ex) {
            Logger.getLogger(LimitTimeKillController.class.getName()).log(Level.SEVERE, null, ex);
        }
        String message = result.get(false);
        if(message != null && !message.isEmpty()) {
            return new SimpleJsonResponse(false, message);
        }
        //创建订单
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
        Product product = productService.findOne(fp.getProductId());
        order.setDeliveryObjectType(product.getOwnerType());
        if(product.getOwnerType() == OwnerType.provider) {
            order.setDeliveryObjectId(product.getOwnerId());
        }
        if(OwnerType.system == product.getOwnerType() || OwnerType.jingdong == product.getOwnerType()){//如果是系统或者京东订单 显示确认收货
            order.setHasSystem(OrderWithSystem.all);
            order.setStatus(OrderStatus.waiting_confirmed);
        }else{//如果是 自有或者供应商 则显示客服审核
            order.setHasSystem(OrderWithSystem.none);
            order.setStatus(OrderStatus.waiting_audit);
        }
        ShipmentCompany shipmentCompany = null;
        if(website.getShipmentCompanyId() != null) {
            shipmentCompany = shipmentCompanyService.findOne(website.getShipmentCompanyId());
        }
        //开始设置默认仓库以及默认的发货物流公司
        if (shipmentCompany == null) {
            shipmentCompany = product.getPreferShipmentCompany();
        }
        order.setShipmentCompany(shipmentCompany);
        order.setTotalCost(amount * fp.getDisplayDiscountPrice()*website.getRatio());
        order.setNeedPay(amount * fp.getDisplayDiscountPrice()*website.getRatio());
        order.setShipmentCompany(shipmentCompany);
        order.setDeliveryDateType(DeliveryDateType.any);
        try {
            OrderEntry entry = new OrderEntry();
            StorageUnit storageUnit = productService.findStorageUnits(product).get(0);
            entry.setStorageUnitId(storageUnit.getId());
            entry.setSourceEntryType(OrderEntrySourceType.second_kill);//来源
            entry.setSourceObjectId(product.getId());
            entry.setSoldPrice(fp.getDisplayDiscountPrice());
            entry.setAmount(amount);
            /***此处下单产品思路：
             * 1.同一活动下的同一产品 同个用户只能购买一次
             * 2.不同活动下的同一产品 同个用户可以 分别买一次
            ***/
            ActivityProductUser acu = acuService.find(websiteUser.getId().intValue(), activityId, fp.getProductId());
            //如果为空 则用户没有买过同一活动下的同一产品 即可提交订单
            if(acu==null){
                ActivityProductUser acuObject = new ActivityProductUser();
                //将id保存
                acuObject.setActivityId(activityId);
                acuObject.setProductId(fp.getProductId());
                acuObject.setUserId(websiteUser.getId().intValue());
                acuService.save(acuObject);
                timeSpikeService.createOrder(flashPid, order, entry, websiteUser, website);
                //提交订单成功后会减掉一个商品
                timeSpikeService.reduceNum(flashPid);
            }else{
                return new SimpleJsonResponse(false, "抱歉,同一活动下的 同一件产品每人限购一件！");
            }
            
        } catch (IOException ex) {
            return new SimpleJsonResponse(false, "订单提交失败");
        }
        return new SimpleJsonResponse(true, order.getId());
    }
}
