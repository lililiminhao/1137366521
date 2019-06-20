/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.activity.controller;

import com.lmf.activity.entity.FlashSaleProduct;
import com.lmf.activity.enums.FlashSaleActivityStatus;
import com.lmf.activity.service.LimitExchangeService;
import com.lmf.activity.vo.FlashSaleProductCriteria;
import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.enums.OwnerType;
import com.lmf.common.exceptions.PermissionDeniedException;
import com.lmf.common.util.MobileSupportUtil;
import com.lmf.common.util.PagerSpec;
import com.lmf.enums.ProductStatus;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.market.service.CouponService;
import com.lmf.market.service.UserCouponService;
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
import com.lmf.sys.entity.GeoRegion;
import com.lmf.sys.entity.ShipmentCompany;
import com.lmf.sys.service.GeoRegionService;
import com.lmf.sys.service.ShipmentCompanyService;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.entity.WebsiteUserAddress;
import com.lmf.website.service.WebsiteUserAddressService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
 * 限时兑换业务 PC客户端 
 * @author LIUQI
 */
@Controller
public class LimitExchangeController {
    
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
    private ProductDescriptionService  productDescriptionService;
    
    @Autowired
    private ProductImageService productImageService;
    
    @Autowired
    private UserCouponService userCouponservice;
    
    /*获取限时兑换产品列表*/
    @RequestMapping(value = "/activity/limitExchange/list.php", method = RequestMethod.GET)
    public String page(@PagerSpecDefaults(pageSize = 24, sort = "end_time.desc") PagerSpec pager,
                        Website website, WebsiteUser websiteUser, Model model)throws IOException{
        FlashSaleProductCriteria criteria = new FlashSaleProductCriteria().withFlashSaleActivityStatus(FlashSaleActivityStatus.be_doing);
        Page<FlashSaleProduct> page = limitExchangeService.find(criteria, pager);
        if(page != null && page.hasContent()){          
            for(FlashSaleProduct pf : page)
            {
                limitExchangeService.setIsLimitOverToFlashProduct(pf, website);
            }
            model.addAttribute("page", page);
        }
        model.addAttribute("link", "/jdvop/activity/limitExchange/list.php?page=[:page]");
        return "/activity/limit_exchange/list";
    }
    
    /* 获取 产品详情*/
    @RequestMapping(value = "/activity/limitExchange/view.php", method = RequestMethod.GET)
    public String getView(Website website,
                            HttpServletRequest request,
                            @RequestParam("fpid") Long fpid, 
                            Model model) throws IOException{
        //添加限时兑换产品到- - - - - 看看其他 - - - - -在页面显示 add by liuqi
        Page<FlashSaleProduct> fpPage = limitExchangeService.find(new FlashSaleProductCriteria().withFlashSaleActivityStatus(FlashSaleActivityStatus.be_doing), null);
        if(fpPage != null && fpPage.hasContent()){
            model.addAttribute("fps", fpPage.getContent());
        }
        FlashSaleProduct flashProduct = limitExchangeService.findOne(fpid);
        Product product = productService.findOne(flashProduct.getProductId());
        limitExchangeService.setIsLimitOverToFlashProduct(flashProduct, website);
        model.addAttribute("flashProduct", flashProduct);
        model.addAttribute("product", product);
        model.addAttribute("isStock", orderHelper.checkExistStockBeforeBuy(product, website, request, model));
        List<ProductImage> productImageList = productImageService.find(product);    //产品图片
        ProductDescription productDescription   = productDescriptionService.findOne(product.getId());   //产品详情
        model.addAttribute("productImageList", productImageList);
        model.addAttribute("productDescription", productDescription);
        boolean isSelling = true;
        if(product.getRetailPrice() < 0.01) {
            isSelling = false; 
        } else if(product.getStatus() != ProductStatus.selling) {
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
            return "/activity/limit_exchange/jd_view";
        }
        return "/activity/limit_exchange/view";
    }
    
    /**
     * 立即购买
     * @param request
     * @param returnUrl
     * @param pid
     * @param amount
     * @param addressId
     * @param websiteUser
     * @param model
     * @return 
     */
    @RequestMapping(value = "/activity/limitExchange/order.php", method = RequestMethod.GET)
    public String order(HttpServletRequest request,
            @RequestParam(value = "returnUrl", required = false) String returnUrl,
            @RequestParam(value = "pid", required = false) Long pid,
            @RequestParam(value = "amount", required = false) Integer amount,
            @RequestParam(value = "addressId", required = false) Integer addressId,
            WebsiteUser websiteUser, Model model){
        if(websiteUser == null || websiteUser.getId() <= 0){
            //如果当前未登陆,则跳转至登陆页面
            returnUrl = "/activity/limitExchange/view.php?fpid=" + pid;
            return "redirect:/login.php?retUrl=" + returnUrl;
        }
        HttpSession session = request.getSession(true);
        if(pid == null || amount == null){
            if(session.getAttribute("pid") == null || session.getAttribute("amount") == null)
                throw new RuntimeException();
            pid = (Long) session.getAttribute("pid");
            amount = (Integer) session.getAttribute("amount");
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
        FlashSaleProduct flashProduct = limitExchangeService.findOne(pid);
        Product product = productService.findOne(flashProduct.getProductId());
        model.addAttribute("product", product);
        model.addAttribute("flashProduct", flashProduct);
        model.addAttribute("amount", amount);
        session.setAttribute("pid", pid);
        session.setAttribute("amount", amount);
        return "/activity/limit_exchange/order";
    }
    
    /**
     * 点击 立即购买时 check
     * @param flashPid
     * @param amount
     * @param websiteUser
     * @return 
     */
    @RequestMapping(value = "/activity/limitExchange/checkBeforeSubmit.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse checkBeforeSubmit(@RequestParam("fpid") Long flashPid, 
            @RequestParam(value = "amount", required = false) Integer amount,
            WebsiteUser websiteUser){
        FlashSaleProduct fp = limitExchangeService.findOne(flashPid);
        //check每人的购物次数，检查产品限制数量是否满了
        Map<Boolean, String>  checkRs = limitExchangeService.checkFlashProductStockBeforeSubmit(fp, amount, websiteUser.getId());
        String msg = checkRs.get(false);
        if(msg != null && !msg.isEmpty()) {
            return new SimpleJsonResponse(false, msg);
        }
        return new SimpleJsonResponse(true, "");
    }
    
    
    /*产品下单*/
    @RequestMapping(value = "/activity/limitExchange/submit.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse submit(@RequestParam("fpid") Long flashPid,				
                                                    @RequestParam(value = "myAddressId") Long addressId,
                                                    @RequestParam(value = "amount[]") Integer[] amounts,
                                                    HttpServletRequest request,
                                                    WebsiteUser websiteUser,
                                                    Website website,
                                                    UserDetail currentUser) throws IOException{
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
        int amount = amounts[0];
        FlashSaleProduct fp = limitExchangeService.findOne(flashPid);
        if(amount * fp.getDisplayDiscountPrice() * website.getRatio() > currentUser.getIntegral()){
            return new SimpleJsonResponse(false, "积分不足，请再接再厉");
        }
        //check每人的购物次数，检查产品限制数量是否满了
        Map<Boolean, String>  checkRs = limitExchangeService.checkFlashProductStockBeforeSubmit(fp, amount, websiteUser.getId());
        String msg = checkRs.get(false);
        if(msg != null && !msg.isEmpty()) {
            return new SimpleJsonResponse(false, msg);
        }
        //check产品库存 网站存款
        Map<Boolean, String>  result = orderHelper.checkOrderBeforeSubmit(website, fp.getProductId() , amount, address);
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
        if(OwnerType.system == product.getOwnerType() || OwnerType.jingdong == product.getOwnerType()){
            order.setHasSystem(OrderWithSystem.all);
            order.setStatus(OrderStatus.waiting_confirmed);
        }else{
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
        double displayDiscountPrice = fp.getDisplayDiscountPrice();
        order.setTotalCost(amount * displayDiscountPrice * website.getRatio());
        order.setNeedPay(amount * displayDiscountPrice * website.getRatio());
        order.setShipmentCompany(shipmentCompany);
        order.setDeliveryDateType(DeliveryDateType.any);
        try {
            OrderEntry entry = new OrderEntry();
            StorageUnit storageUnit = productService.findStorageUnits(product).get(0);
            entry.setStorageUnitId(storageUnit.getId());
            entry.setSourceEntryType(OrderEntrySourceType.limit_exchange);//来源
            entry.setSourceObjectId(product.getId());
            entry.setSoldPrice(displayDiscountPrice);
            entry.setAmount(amount);
            
            limitExchangeService.createOrder(flashPid, order, entry, websiteUser, website);
        } catch (Exception e) {
            return new SimpleJsonResponse(false, "订单提交失败");
        }
        return new SimpleJsonResponse(true, order.getId());
    }
}