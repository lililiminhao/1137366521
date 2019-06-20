/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.mobile.controller;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.enums.OwnerType;
import com.lmf.common.enums.SubOwnerType;
import com.lmf.common.exceptions.PermissionDeniedException;
import com.lmf.common.exceptions.ResourceNotFoundException;
import com.lmf.common.util.LMFDateUtil;
import com.lmf.common.util.PagerSpec;
import com.lmf.enterprise.entity.EnterpriseExclusiveProductPoolEntry;
import com.lmf.enterprise.entity.EnterpriseUserMap;
import com.lmf.enterprise.service.EnterpriseExclusiveProductPoolEntryService;
import com.lmf.enterprise.service.EnterpriseUserMapService;
import com.lmf.integral.SystemConfig;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.market.entity.Coupon;
import com.lmf.market.service.CouponService;
import com.lmf.order.entity.NifferOrder;
import com.lmf.order.entity.NifferOrderEntry;
import com.lmf.order.entity.OrderEntry;
import com.lmf.order.entity.OrderOperationLog;
import com.lmf.order.entity.OrderShipmentLog;
import com.lmf.order.entity.ShipmentOrderDetail;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.enums.AfterSaleNifferReason;
import com.lmf.order.enums.AfterSaleReturnReason;
import com.lmf.order.enums.NifferOrderEntryType;
import com.lmf.order.enums.NifferOrderStatus;
import com.lmf.order.enums.OrderStatus;
import com.lmf.order.enums.ShipmentOrderState;
import com.lmf.order.service.NifferOrderService;
import com.lmf.order.service.OrderOperationLogService;
import com.lmf.order.service.OrderService;
import com.lmf.order.service.ShipmentOrderDetailService;
import com.lmf.order.vo.NifferOrderCriteria;
import com.lmf.order.vo.OrderCriteria;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.StorageUnit;
import com.lmf.product.service.ProductService;
import com.lmf.product.service.StorageUnitService;
import com.lmf.system.sdk.enums.AfterSaleType;
import com.lmf.system.sdk.enums.SystemOrderStatus;
import com.lmf.system.sdk.service.SystemOrderService;
import com.lmf.system.sdk.service.SystemReturnNifferOrderService;
import com.lmf.system.sdk.vo.CanAfterSaleStatus;
import com.lmf.system.sdk.vo.OrderTrackResult;
import com.lmf.system.sdk.vo.OrderViewResult;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteUser;

import org.apache.commons.collections.CollectionUtils;
import org.apache.velocity.tools.generic.NumberTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author shenzhixiong
 */
@Controller("mobileOrderController")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private StorageUnitService storageUnitService;

    @Autowired
    private NifferOrderService nifferOrderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SystemOrderService systemOrderService;
    
    @Autowired
    private EnterpriseUserMapService enterpriseUserMapService;
    
    @Autowired
    private EnterpriseExclusiveProductPoolEntryService enterpriseExclusiveProductPoolEntryService;

    @Autowired
    private ShipmentOrderDetailService shipmentOrderDetailService;
    
    @Autowired
    private SystemReturnNifferOrderService systemReturnNifferOrderService;

    @Autowired
    private OrderOperationLogService orderOperationLogService;
    
    @Autowired
    private SystemConfig systemConfig;

    @Autowired
    private CouponService couponService;
    
    private final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @RequestMapping(value = "/my/mobile/orders.php", method = RequestMethod.GET)
    public String orders(@RequestParam(value = "status", required = false) OrderStatus orderStatus,
            @PagerSpecDefaults(pageSize = 999999999, sort = "time.desc") PagerSpec pager,
            WebsiteUser websiteUser, Model model) {
        StringBuilder sbd = new StringBuilder("/jdvop/my/mobile/orders.php?page=[:page]");
        if (orderStatus != null) {
            sbd.append("&status=").append(orderStatus.name());
            model.addAttribute("status", orderStatus.name());
        }
        OrderStatus[] status = null;
        if (orderStatus != null) {
            status = new OrderStatus[]{orderStatus};
        }
        Page<ShoppingOrder> orderPager = orderService.find(websiteUser, new OrderCriteria().withStatus(status), pager);
        List<ShoppingOrder> orderList = orderPager.getContent();
        
        Iterator<ShoppingOrder> iterator = orderList.iterator();
        while (iterator.hasNext()) {
        	//如果是拆分订单的父订单 则不显示
        	ShoppingOrder order = iterator.next();
        	List<ShoppingOrder> list = orderService.findSplitChildOrder1(order, null);
        	if(CollectionUtils.isNotEmpty(list)){
        		iterator.remove();
        	}
        	 //优惠券查询
        	if(order.getCouponId() != null && order.getCouponId()!= 0){
        		   Coupon coupons = couponService.findById(order.getCouponId());
                   order.setCoupon(coupons);
        	}
        }

        Map<Long, Boolean> returnOrNifferMap = new HashMap<>();
        
        model.addAttribute("returnOrNifferMap", returnOrNifferMap);
        model.addAttribute("orderList", orderList);
        model.addAttribute("link", sbd.toString());
        model.addAttribute("pager", orderPager.getPagerSpec());
        model.addAttribute("orderService", orderService);
        model.addAttribute("productService", productService);
        model.addAttribute("storageUnitService", storageUnitService);
        model.addAttribute("returnedOrNifferOrderService", nifferOrderService);
        return "order/list";
    }

    @RequestMapping(value = "/my/mobile/order/{orderID}.php", method = RequestMethod.GET)
    public String viewOrder(@PathVariable("orderID") long orderID,
            @RequestParam(value = "isRefund", defaultValue = "false") Boolean isRefund,
            Website website, WebsiteUser websiteUser, Model model) throws IOException, ParseException {
        ShoppingOrder order = orderService.findOne(orderID);
        Coupon coupons = couponService.findById(order.getCouponId());
        if (coupons!= null){
            BigDecimal amount = coupons.getAmount();
            order.setCouponAmount(amount);
            order.setCoupon(coupons);
        }else{
            order.setCouponAmount(new BigDecimal(0));
        }

        if (websiteUser == null || order == null || !Objects.equals((long) order.getUserId(), websiteUser.getId())) {
            throw new PermissionDeniedException();
        }
        ShoppingOrder originalOrder = null;
        if (order.isSplit()) {
            originalOrder = orderService.findOne(order.getOriginalOrderId());
            if (originalOrder == null) {
                throw new RuntimeException("Method viewOrder(), Data exception!");
            }
        }
        //快递详情
        if (order.getDeliveryObjectType() == OwnerType.enterprise || order.getDeliveryObjectType() == OwnerType.provider) {
            OrderShipmentLog shipmentLog = orderService.findShipmentLog(order);
            if (shipmentLog != null) {
                model.addAttribute("shipmentLog", shipmentLog);
            }
        } else {
            if (order.getStatus() != OrderStatus.canceld && order.getStatus() != OrderStatus.cancled_before_pay && order.getStatus() != OrderStatus.waiting_pay) {
                try {
                    OrderTrackResult track = systemOrderService.findOrderTrack(website, order);
                    if (track != null) {
                        model.addAttribute("sod", track);
                        ShipmentOrderDetail shipmentOrderDetail = shipmentOrderDetailService.findOne(order);
                        if(shipmentOrderDetail == null) {
                        	shipmentOrderDetail = new ShipmentOrderDetail();
                        	Map<String, Object> map = new HashMap<String, Object>();
                            map.put(track.getShipmentName(), track.getShipmentOrder());
                            shipmentOrderDetail.setShipmentInfo(map);
                            shipmentOrderDetail.setStatus(ShipmentOrderState.in_transit);
                            shipmentOrderDetail.setOrderId(orderID);
                            shipmentOrderDetailService.create(shipmentOrderDetail);
                        }
                    }
                } catch (Exception e) {
                    logger.error(null, e);
                }
            }
        }
        if (order.getStatus() != OrderStatus.waiting_pay) {
            if (order.isSplit()) {
                model.addAttribute("orderPayLog", orderService.findPayLog(originalOrder));
            } else {
                model.addAttribute("orderPayLog", orderService.findPayLog(order));
            }
        }

        //订单支付信息  倒计时
        if (order.getStatus() == OrderStatus.waiting_pay) {
            model.addAttribute("expiredTime", order.getCreateTime().getTime() + 60 * 1000 * 60 * 24);
        }

        List<OrderEntry> orderEntryList = orderService.findEntries(order);
        Map<Integer, StorageUnit> storageUnitMap = new HashMap<>();
        Map<Integer, Product> productMap = new HashMap<>();
        Map<Integer, Boolean> returnOrNifferMap = new HashMap<>();
        StorageUnit storageUnit;
        Product product;
        for (OrderEntry orderEntry : orderEntryList) {
            storageUnit = storageUnitService.findOne(orderEntry.getStorageUnitId());
            storageUnitMap.put(storageUnit.getId(), storageUnit);
            product = productService.findOne(storageUnit.getProductId());
            productMap.put(product.getId(), product);
            /**
             * 系统订单是否可换货
             */
            if (OwnerType.system.equals(product.getOwnerType()) && !SubOwnerType.wangyi.equals(product.getSubOwnerType())) {//排除网易严选
                if (order.getStatus() != OrderStatus.canceld && order.getStatus() != OrderStatus.waiting_pay && order.getStatus() != OrderStatus.cancled_before_pay) {
                    if(order.getSystemOrderInfo() != null) {
                    	//查询该产品是否可退换货
                        try {
                        	//只要有一个结果为true，那么都设置为true，因为后面会检查售后单进行二次判断是否可进行换货或退货
                            CanAfterSaleStatus afterSaleStatus = systemReturnNifferOrderService.getCanAfterSaleStatus(website, order.getKey(), product.getSystemProductId(),AfterSaleType.niffer);
                            if(!afterSaleStatus.getStatus()){
                            	 afterSaleStatus = systemReturnNifferOrderService.getCanAfterSaleStatus(website, order.getKey(), product.getSystemProductId(),AfterSaleType.returned);
                            	 returnOrNifferMap.put(product.getId(), afterSaleStatus.getStatus());
                            }else{
                            	returnOrNifferMap.put(product.getId(), afterSaleStatus.getStatus());
                            }
                            
                        } catch (UnsupportedEncodingException ex) {
                        	 throw new RuntimeException(ex.getMessage());
                        }
                    }
                } else {
                    returnOrNifferMap.put(product.getId(), false);
                }
            } else {
            	 returnOrNifferMap.put(product.getId(), allowAftersale(order.getId(), product.getId(), product.getOwnerType(), product.getSubOwnerType()));
            }
        }
        //用于标识该笔订单的商品是否可以退换货
        List<NifferOrder> dataList = nifferOrderService.find(order);
        /**
         * 自有产品或供应商产品：待审核和待发货状态，售后跟订单走
         */
        Map<Long, NifferOrder> orderIdAndnifferOrderMap = new HashMap<>();
        if (dataList != null && !dataList.isEmpty()) {
            Map<Integer, NifferOrder> nifferOrderMap = new HashMap<>();
            
            List<NifferOrderEntry> nifferOrderEntryList = null;
            //倒序排序，首先用最新的售后订单判断，然后跳过历史售后记录
            dataList.sort(new Comparator<NifferOrder>() {
                @Override
                public int compare(NifferOrder o1, NifferOrder o2) {
                    return o2.getApplyTime().compareTo(o1.getApplyTime());
                }
            });
            for (NifferOrder nifferOrderItem : dataList) {
                nifferOrderEntryList = nifferOrderService.findEntries(nifferOrderItem);
                nifferOrderEntryList = nifferOrderEntryList.stream().filter(p -> p.getEntryType().equals(NifferOrderEntryType.in)).collect(Collectors.toList());
                for (NifferOrderEntry nifferOrderEntry : nifferOrderEntryList) {
                    //如果该商品存在退换货未完成,不允许再次退换货操做
                    if (nifferOrderItem.getType().equals(AfterSaleType.returned)
                            //如果是换货，则只有后台拒绝、换货完成和平台拒绝时才可以重新申请换货
                            || (nifferOrderItem.getType().equals(AfterSaleType.niffer) && !(nifferOrderItem.getStatus() == NifferOrderStatus.accepted || nifferOrderItem.getStatus() == NifferOrderStatus.declined || (nifferOrderItem.getIsSystem() && nifferOrderItem.getOpenStatus() == NifferOrderStatus.declined)))) {
                    	//如果该商品的最新一条售后订单记录已做判断，那么接下来的售后订单记录都不再判断 
                    	if (!nifferOrderMap.containsKey(nifferOrderEntry.getProductId())) {
                    		  returnOrNifferMap.put(nifferOrderEntry.getProductId(), false);
                    	  }
                        /**
                         * 自有产品或供应商产品：待审核和待发货状态，售后跟订单走
                         */
                        if ((order.getDeliveryObjectType() == OwnerType.enterprise || order.getDeliveryObjectType() == OwnerType.provider)
                                && (order.getStatus().equals(OrderStatus.waiting_audit) || order.getStatus().equals(OrderStatus.waiting_shipment) || order.getStatus().equals(OrderStatus.after_sale_service))) {
                            if(!orderIdAndnifferOrderMap.containsKey(nifferOrderItem.getOriginalOrderId())){
                                orderIdAndnifferOrderMap.put(nifferOrderItem.getOriginalOrderId(), nifferOrderItem);
                            }
                            break;
                        }
                        if (order.getDeliveryObjectType() == OwnerType.system
                                && order.getStatus().equals(OrderStatus.after_sale_service)) {
                            if(!orderIdAndnifferOrderMap.containsKey(nifferOrderItem.getOriginalOrderId())){
                                orderIdAndnifferOrderMap.put(nifferOrderItem.getOriginalOrderId(), nifferOrderItem);
                            }
                            break;
                        }
                    }
                    //记录该商品最新一次的售后订单记录
                    if (!nifferOrderMap.containsKey(nifferOrderEntry.getProductId())) {
                        nifferOrderMap.put(nifferOrderEntry.getProductId(), nifferOrderItem);
                    }

                }
            }
            model.addAttribute("nifferOrderMap", nifferOrderMap);
        }
     
        model.addAttribute("returnOrNifferMap", returnOrNifferMap);
        model.addAttribute("orderIdAndnifferOrderMap", orderIdAndnifferOrderMap);
        model.addAttribute("order", order);
        model.addAttribute("isRefund", isRefund);
        model.addAttribute("entries", orderEntryList);
        model.addAttribute("storageUnitMap", storageUnitMap);
        model.addAttribute("productMap", productMap);
        return "order/order_detail";
    }

    @RequestMapping(value = "/my/mobile/order/orderTrack.php", method = RequestMethod.GET)
    public String orderTrack(@RequestParam("id") long orderID,
            Website website, WebsiteUser websiteUser, Model model) throws IOException {
        ShoppingOrder order = orderService.findOne(orderID);
        if (websiteUser == null || order == null || !Objects.equals((long) order.getUserId(), websiteUser.getId())) {
            throw new PermissionDeniedException();
        }

        OrderTrackResult track = systemOrderService.findOrderTrack(website, order);
        if (track != null) {
            model.addAttribute("sod", track);
        }

        model.addAttribute("order", order);
        model.addAttribute("entries", orderService.findEntries(order));
        model.addAttribute("storageUnitService", storageUnitService);
        model.addAttribute("productService", productService);
        return "order/order_track";
    }

    @RequestMapping(value = "/mobile/ajaxOrderEntrires.php", method = RequestMethod.GET)
    public @ResponseBody
    List<OrderEntry> ajaxOrderEntrys(@RequestParam(value = "orderId", required = false) Long id) {

        ShoppingOrder order = orderService.findOne(id);
        if (order != null) {
            return orderService.findEntries(order);
        }
        return null;
    }

    @RequestMapping(value = "/mobile/ajaxStorageUnit.php", method = RequestMethod.GET)
    public @ResponseBody
    StorageUnit ajaxStorageUnit(@RequestParam(value = "storageUnitId", required = false) int storageUnitId) {

        StorageUnit storageUnit = storageUnitService.findOne(storageUnitId);
        if (storageUnit != null) {
            return storageUnit;
        }
        return null;
    }

    @RequestMapping(value = "/mobile/ajaxOrderProduct.php", method = RequestMethod.GET)
    public @ResponseBody Product ajaxOrderProduct(@RequestParam(value = "productId", required = false) Integer productId, WebsiteUser websiteUser) {
    	Product product = productService.findOne(productId);
    	if(websiteUser != null && websiteUser.getId() != null && websiteUser.getId() > 0) {
	    	EnterpriseUserMap userMap = enterpriseUserMapService.getOneByUserId(websiteUser.getId());
	    	if(userMap != null && userMap.getProductPoolId() != null && userMap.getProductPoolId() > 0) {
	    		EnterpriseExclusiveProductPoolEntry entry = enterpriseExclusiveProductPoolEntryService.selectOne(userMap.getProductPoolId(), productId);
	    		if(entry != null) {
	    			product.setRetailPrice(entry.getExclusivePrice());
	    		}
	    	}
    	}
        return product;
    }

    @RequestMapping(value = "/mobile/ajaxOrders.php", method = RequestMethod.GET, params = "ajax", produces = "text/html;charset=UTF-8")
    public @ResponseBody
    Page<ShoppingOrder> ajaxList(@RequestParam(value = "status", required = false) OrderStatus orderStatus,
            @PagerSpecDefaults(pageSize = 10, sort = "time.desc", maxPageSize = 20) PagerSpec pager,
            Website website,
            WebsiteUser websiteUser) {
        StringBuilder sbd = new StringBuilder("/jdvop/mobile/ajaxOrders.php?page=[:page]");
        if (orderStatus != null) {
            sbd.append("&status=").append(orderStatus.name());
        }
        OrderStatus[] status = null;
        if (orderStatus != null) {
            status = new OrderStatus[]{orderStatus};
        }
        Page<ShoppingOrder> orderPager = orderService.find(websiteUser, new OrderCriteria().withStatus(status), pager);
        return orderPager;
    }

    @RequestMapping(value = "/my/mobile/nifferOrders.php", method = RequestMethod.GET)
    public String nifferOrders(@RequestParam(value = "status", required = false) OrderStatus orderStatus,
            @PagerSpecDefaults(pageSize = 15, sort = "applyTime.desc") PagerSpec pager,
            WebsiteUser websiteUser, Website website,
            Model model) {
        StringBuilder sbd = new StringBuilder("/jdvop/my/mobile/nifferOrders.php?page=[:page]");
        if (orderStatus != null) {
            sbd.append("&status=").append(orderStatus.name());
        }
        Page<NifferOrder> dataPage = nifferOrderService.find(new NifferOrderCriteria().withUserId(websiteUser.getId()), pager);
        if (dataPage.hasContent()) {
            Map<Long, Boolean> refundDataMap = new HashMap<>();            //用于前段页面区分直接退款和退换货单数据
            Map<Long, ShoppingOrder> orderDataMap = new HashMap<>();            //用于存放订单数据
            Map<Long, Map<NifferOrderEntryType, List<NifferOrderEntry>>> nifferEntriesAsMap = new HashMap<>();
            List<NifferOrderEntry> nifferOrderEntryList;
            for (NifferOrder item : dataPage.getContent()) {
                nifferOrderEntryList = nifferOrderService.findEntries(item);
                if (nifferOrderEntryList.isEmpty()) {
                    //没有任何退出换入商品
                    refundDataMap.put(item.getId(), false);
                } else {
                    refundDataMap.put(item.getId(), true);
                }
                nifferEntriesAsMap.put(item.getId(), nifferOrderService.formatOrderEntryAsMap(nifferOrderEntryList));
                orderDataMap.put(item.getId(), orderService.findOne(item.getOriginalOrderId()));
            }
            model.addAttribute("refundDataMap", refundDataMap);
            model.addAttribute("orderDataMap", orderDataMap);
            model.addAttribute("nifferEntriesAsMap", nifferEntriesAsMap);
        }
        model.addAttribute("nifferOrderEntryType", NifferOrderEntryType.out);
        model.addAttribute("refundOrderEntryType", NifferOrderEntryType.in);
        model.addAttribute("dataPage", dataPage);
        model.addAttribute("link", sbd.toString());
        model.addAttribute("pager", dataPage.getPagerSpec());

        return "order/niffer/niffer_order_list";
    }

    //换货详情
    @RequestMapping(value = "/my/mobile/order/niffer{id}.php", method = RequestMethod.GET)
    public String returnedOrNifferView(@PathVariable("id") int nifferOrderId, WebsiteUser websiteUser, Model model) {
        NifferOrder nifferOrder = nifferOrderService.findOne(nifferOrderId);
        if (nifferOrder == null) {
            throw new ResourceNotFoundException();
        }

        ShoppingOrder order = orderService.findOne(nifferOrder.getOriginalOrderId());
        if (websiteUser == null || order == null || !Objects.equals((long) order.getUserId(), websiteUser.getId())) {
            throw new PermissionDeniedException();
        }
        model.addAttribute("order", order);
        Map<NifferOrderEntryType, List<NifferOrderEntry>> entryMap = nifferOrderService.findEntriesAsMap(nifferOrder);
        model.addAttribute("nifferOrder", nifferOrder);
        if(nifferOrder.getType().equals(AfterSaleType.niffer)){
             //新的订单是否已经发货
            if (nifferOrder.getNewCreatedOrderId() != null && nifferOrder.getNewCreatedOrderId() > 0) {
                ShoppingOrder newOrder = orderService.findOne(nifferOrder.getNewCreatedOrderId());
                OrderShipmentLog shipmentLog = orderService.findShipmentLog(newOrder);
                if (shipmentLog != null) {
                    model.addAttribute("shipmentLog", shipmentLog);
                }
            }
            model.addAttribute("outEntry", entryMap.get(NifferOrderEntryType.out));   //换出
            return "order/niffer/niffer_order_detail";
        }else{
            model.addAttribute("inEntry", entryMap.get(NifferOrderEntryType.in));   //换出
            return "order/aftersale/refund_order_detail";
        }
    }

    private final static String NIFFER_TOKEN ="nifferToken";
    
    //申请售后
    @RequestMapping(value = "/mobile/order/niffer.php", method = RequestMethod.GET)
    public String niffer(@RequestParam("entry_id") long entryId, WebsiteUser websiteUser, Website website,  HttpSession session,Model model) throws UnsupportedEncodingException, IOException, ParseException {

        if(websiteUser==null){
            return "redirect:/mobile/login.php?retUrl="+URLEncoder.encode("/jdvop/mobile/order/niffer.php?entry_id="+entryId,"UTF-8");
        }
        OrderEntry orderEntry = orderService.findOneEntry(entryId);
        ShoppingOrder order = orderService.findOne(orderEntry.getOrderId());
        if (order == null || order.isDeprecated() || !(order.getStatus() == OrderStatus.waiting_confirmed || order.getStatus() == OrderStatus.completed || order.getStatus() == OrderStatus.waiting_audit)) {
            throw new ResourceNotFoundException();
        }

        if (!Objects.equals(order.getUserId().longValue(), websiteUser.getId())) {
            throw new PermissionDeniedException();
        }

        Product product = productService.findOne((int) orderEntry.getSourceObjectId());
        if (product == null) {
            throw new ResourceNotFoundException();
        }

        boolean isCanNiffer = true;
        if (product.getOwnerType().equals(OwnerType.system) && !SubOwnerType.wangyi.equals(product.getSubOwnerType())) {//排除网易严选
            if (order.getStatus() != OrderStatus.canceld
                    && order.getStatus() != OrderStatus.waiting_pay && order.getStatus() != OrderStatus.cancled_before_pay) {
            	//检查是否可以进行换货，这是一个二重检查，在进入该页面前已进行更严密的判断，以及提交申请时也做了严密判断
                CanAfterSaleStatus afterSaleStatus = systemReturnNifferOrderService.getCanAfterSaleStatus(website, order.getKey(), product.getSystemProductId(),AfterSaleType.niffer);
                isCanNiffer = afterSaleStatus.getStatus();
            } else {
                throw new PermissionDeniedException();
            }
        } else if (product.getOwnerType().equals(OwnerType.jingdong)|| SubOwnerType.wangyi.equals(product.getSubOwnerType())) {//网易严选和京东同一处理
            isCanNiffer = false;
        } else {
            if (order.getStatus().equals(OrderStatus.waiting_audit)) {
                isCanNiffer = false;
            }
        }
        model.addAttribute("isCanNiffer", isCanNiffer);

        //如果已申请售后，则跳转到售后详情页面
        List<NifferOrder> olds = nifferOrderService.findByOriginalOrderId(order.getId());
        if (olds != null && !olds.isEmpty()) {
            List<NifferOrderEntry> nifferOrderEntryList = null;
            for (NifferOrder nifferOrderItem : olds) {
                nifferOrderEntryList = nifferOrderService.findEntries(nifferOrderItem);
                nifferOrderEntryList = nifferOrderEntryList.stream().filter(p -> p.getEntryType().equals(NifferOrderEntryType.in)).collect(Collectors.toList());
                for (NifferOrderEntry nifferOrderEntry : nifferOrderEntryList) {
                    //如果该商品存在退换货未完成,不允许再次退换货操做
                    if (product.getId() == nifferOrderEntry.getProductId()
                            //如果是退货退款,则拒绝后才可以重新申请
                            && ((nifferOrderItem.getType().equals(AfterSaleType.returned) && !(nifferOrderItem.getStatus() == NifferOrderStatus.declined || (nifferOrderItem.getIsSystem() && nifferOrderItem.getOpenStatus() == NifferOrderStatus.declined))
                            //如果是换货，则只有后台拒绝、换货完成和平台拒绝时才可以重新申请换货
                            || (nifferOrderItem.getType().equals(AfterSaleType.niffer) && !(nifferOrderItem.getStatus() == NifferOrderStatus.accepted || nifferOrderItem.getStatus() == NifferOrderStatus.declined || (nifferOrderItem.getIsSystem() && nifferOrderItem.getOpenStatus() == NifferOrderStatus.declined)))))) {
                        return "redirect:/mobile/order/afterSale/result.php?originalOrderId=" + order.getId() + "&originalProductId=" + product.getId();
                    }
                }
            }
        }

        model.addAttribute("product", product);
        model.addAttribute("entry", orderEntry);
        model.addAttribute("user", websiteUser);
        model.addAttribute("nifferReasons", AfterSaleNifferReason.values());
        model.addAttribute("returnReasons", AfterSaleReturnReason.values());
        session.setAttribute(NIFFER_TOKEN, System.currentTimeMillis());
        return "/mobile/order/niffer/apply_niffer";
    }

    //自有产品和经销商产品：未发货前申请退款
    @RequestMapping(value = "/mobile/order/applyRefund.php", method = RequestMethod.GET)
    public String applyRefund(@RequestParam("order_id") long orderId, WebsiteUser websiteUser, Website website,   HttpSession session,Model model) throws UnsupportedEncodingException, IOException, ParseException {

        if(websiteUser==null){
            return "redirect:/mobile/login.php?retUrl="+URLEncoder.encode("/jdvop/mobile/order/applyRefund.php?order_id="+orderId,"UTF-8");
        }
        
        ShoppingOrder order = orderService.findOne(orderId);
        /**
         * 未发货之前可以售后
         */
        if (order == null || order.isNiffer() || order.isDeprecated() || !(order.getStatus() == OrderStatus.waiting_audit || order.getStatus() == OrderStatus.waiting_shipment)) {
            throw new ResourceNotFoundException();
        }
        if (order.getDeliveryObjectType() != OwnerType.enterprise && order.getDeliveryObjectType() != OwnerType.provider) {
            throw new PermissionDeniedException("非自有或者供应商产品不能执行此操作");
        }
        if (!Objects.equals(order.getUserId().longValue(), websiteUser.getId())) {
            throw new PermissionDeniedException();
        }
        //如果已申请售后，则跳转到售后详情页面
        List<NifferOrder> nifferOrderList = nifferOrderService.findByOriginalOrderId(order.getId());
        if (nifferOrderList != null && !nifferOrderList.isEmpty()) {
            for (NifferOrder nifferOrderItem : nifferOrderList) {
                if (nifferOrderItem.getStatus() != NifferOrderStatus.declined) {
                    return "redirect:/mobile/order/afterSale/result.php?originalOrderId=" + order.getId();
                }
            }
        }
        List<OrderEntry> orderEntryList = orderService.findEntries(order);
        model.addAttribute("orderEntryList", orderEntryList);
        List<Integer> pruductIdList = new ArrayList<>();
        for (OrderEntry orderEntry : orderEntryList) {
            pruductIdList.add((int)orderEntry.getSourceObjectId());
        }
        Map<Long,Product> productMap = new HashMap<>();
        List<Product> productList = productService.find(pruductIdList);
        for (Product product : productList) {
            productMap.put((long)product.getId(), product);
        }
        model.addAttribute("productMap", productMap);
        model.addAttribute("order", order);
        model.addAttribute("user", websiteUser);
        model.addAttribute("returnReasons", AfterSaleReturnReason.values());
        session.setAttribute(NIFFER_TOKEN, System.currentTimeMillis());
        return "/mobile/order/aftersale/apply_refund";
    }

    //申请换货结果页
    @RequestMapping(value = "/mobile/order/nifferResult.php", method = RequestMethod.GET)
    public String applyRefundOk(@RequestParam("id") long nifferOrderId, UserDetail userDetail, Model model) {
        NifferOrder nifferOrder = nifferOrderService.findOne(nifferOrderId);
        if (nifferOrder == null) {
            throw new ResourceNotFoundException();
        }
        ShoppingOrder order = orderService.findOne(nifferOrder.getOriginalOrderId());
        if (order == null) {
            throw new ResourceNotFoundException();
        }

        if (userDetail == null || !Objects.equals(order.getUserId(), userDetail.getUserId())) {
            throw new PermissionDeniedException();
        }
        model.addAttribute("nifferOrder", nifferOrder);
        return "/mobile/order/niffer/apply_after_sale_ok";
    }

    //退款售后订单结果页面
    @RequestMapping(value = "/mobile/order/afterSale/result.php", method = RequestMethod.GET)
    public String view(@RequestParam("originalOrderId") long originalOrderId, //原订单ID
            @RequestParam(name = "originalProductId",required = false) Integer originalProductId, //原订单申请售后的商品ID
            Website website, Model model, HttpServletRequest req) {
        //根据原订单ID 查找对应的订单数据
        ShoppingOrder originalOrder = orderService.findOne(originalOrderId);
        if (originalOrder == null) {
            throw new RuntimeException("OrderAfterSale：The order(orderId = " + originalOrder.getId() + ") don't exsit.");
        }

        List<OrderEntry> originalEntries = orderService.findEntries(originalOrder);
        if (originalEntries == null || originalEntries.isEmpty()) {
            throw new RuntimeException("OrderAfterSale：The order(orderId = " + originalOrder.getId() + ") entries is empty.");
        }
        String afterSaleUrl = null;
         NifferOrder afterSaleOrder = null;
         Product product = null;
        if(originalProductId!=null){
            OrderEntry originalEntry = null;
            for (OrderEntry oe : originalEntries) {
                if (oe.getSourceObjectId() == (long) originalProductId) {
                    originalEntry = oe;
                    break;
                }
            }

            //商品不属于订单
            if (originalEntry == null) {
                throw new RuntimeException("OrderAfterSale：The product(productId = " + originalProductId + ") does not belong to the order(orderId = " + originalOrder.getId() + ").");
            }
            //根据原订单申请售后的商品ID 查询商品信息
            product = productService.findOne((int) originalEntry.getSourceObjectId());
            if (product == null) {
                throw new RuntimeException("OrderAfterSale：The product(productId = " + originalEntry.getSourceObjectId() + ") is not exist.");
            }
            afterSaleOrder = nifferOrderService.findLastOneByOriginalOrderId(originalOrderId, product.getId());
            afterSaleUrl = "/jdvop/mobile/order/niffer.php?entry_id="+originalEntry.getId();
        }else{
            afterSaleOrder = nifferOrderService.findLastOneByOriginalOrderId(originalOrderId, 0);
            afterSaleUrl = "/jdvop/mobile/order/applyRefund.php?order_id="+originalOrder.getId();
        }
        
        if (afterSaleOrder == null) {
            throw new RuntimeException("OrderAfterSale：The after sale of the order (orderId = " + originalOrderId + ")  is not exist.");
        }
        if (afterSaleOrder.getType().equals(AfterSaleType.niffer)) {
            return "redirect:/my/mobile/order/niffer" + afterSaleOrder.getId() + ".php";
        }

        model.addAttribute("after_sale_url", afterSaleUrl);
        model.addAttribute("aso", afterSaleOrder);

        String viewName = "";
        //如果平台拒绝售后，则直接跳到拒绝页面
        if (NifferOrderStatus.declined.equals(afterSaleOrder.getOpenStatus())) {
            viewName = "view_declined";
        } else {
            switch (afterSaleOrder.getStatus()) {
                case waiting_audit://审核
                    viewName = "view_waiting_audit";
                    break;
                case waiting_finance://等待退款
                case processing_refund://退款处理中
                case accepted://退款完成
                    viewName = "view_refund_completed";
                    break;
                case declined://拒绝
                	boolean allowAftersale = true;
                	if(product!=null){
                		allowAftersale = allowAftersale(originalOrderId, 0, product.getOwnerType(), product.getSubOwnerType());
                	}
                	model.addAttribute("allowAftersale", allowAftersale);
                    viewName = "view_declined";
                    break;
                default:
                    break;
            }
        }

        return "mobile/order/after_sales_result/" + viewName; //根据不同的状态去跳转 相应的页面
    }

    @RequestMapping(value = "/mobile/order/niffer.php", method = RequestMethod.POST)
    public @ResponseBody
    SimpleJsonResponse niffer(@RequestParam(value = "orderId", required = true) long orderId,
            @RequestParam(value = "sui", required = false) int storageUnitId,
            @RequestParam(value = "productId", required = true) int productId,
            @RequestParam(value = "amount", required = true) int amount,
            @RequestParam(value = "reason", required = true) String nifferReason,
            @RequestParam(value = "reason_hide", required = true) String returnedReason,
            @RequestParam(value = "remark", required = false) String remark,
            @RequestParam(value = "consumerName", required = true) String consumerName,
            @RequestParam(value = "afterSaleType", required = true) AfterSaleType afterSaleType,
            @RequestParam(value = "consumerMobile", required = true) String consumerMobile,
            WebsiteUser websiteUser, Website website, Model model,  HttpSession session,HttpServletRequest request) throws UnsupportedEncodingException, IOException, ParseException {
        ShoppingOrder order = orderService.findOne(orderId);
        if (order == null || !(order.getStatus() == OrderStatus.waiting_confirmed || order.getStatus() == OrderStatus.completed)) {
            throw new PermissionDeniedException();
        }
        Object nifferToken = session.getAttribute(NIFFER_TOKEN);
        if(nifferToken==null){
        	return new SimpleJsonResponse(false, "页面过期或重复提交，请刷新页面重试！");
        }
        session.removeAttribute(NIFFER_TOKEN);
        List<OrderEntry> orderEntryList = orderService.findEntries(order);
        OrderEntry orderEntry = null;
        for (OrderEntry orderEntryItem : orderEntryList) {
            if (orderEntryItem.getSourceObjectId() == (long) productId) {
                orderEntry = orderEntryItem;
                break;
            }
        }
        if (orderEntry == null) {
            return new SimpleJsonResponse(false, "该商品不存在，不能进申请售后！");
        }

        if (websiteUser == null || !Objects.equals((long) order.getUserId(), websiteUser.getId())) {
            throw new PermissionDeniedException();
        }
        Product product = productService.findOne(productId);
        if (product == null) {
            throw new ResourceNotFoundException();
        }
        if (product.getOwnerType().equals(OwnerType.system) && !SubOwnerType.wangyi.equals(product.getSubOwnerType())) {//排除网易严选
            if (order.getStatus() != OrderStatus.canceld
                    && order.getStatus() != OrderStatus.waiting_pay && order.getStatus() != OrderStatus.cancled_before_pay) {
            	
            	//查询该产品是否可退换货
                try {
                    CanAfterSaleStatus afterSaleStatus = systemReturnNifferOrderService.getCanAfterSaleStatus(website, order.getKey(), product.getSystemProductId(),afterSaleType);
                    if(!afterSaleStatus.getStatus()){
                    	  return new SimpleJsonResponse(false, "抱歉，该产品不允许申请" + afterSaleType.getDescription() + "！");
                    }
                } catch (UnsupportedEncodingException ex) {
                	 throw new RuntimeException(ex.getMessage());
                }

            } else {
                throw new PermissionDeniedException();
            }
        } else if (product.getOwnerType().equals(OwnerType.jingdong)|| SubOwnerType.wangyi.equals(product.getSubOwnerType())) {//网易严选和京东同一处理
            boolean allowAftersale = allowAftersale(orderId, 0, product.getOwnerType(), product.getSubOwnerType());
            if(!allowAftersale){
            	 return new SimpleJsonResponse(false, "该订单已超过可售后日期！");
            }
            if (AfterSaleType.niffer.equals(afterSaleType)) {
                throw new PermissionDeniedException("京东或严选不支持换货申请");
            }
        } else {
            if (!isCanAfterSaleForSelfProduct(afterSaleType, OrderStatus.completed)) {
                throw new PermissionDeniedException("该状态不支持售后申请");
            }
        }
        NifferOrder nifferOrder = new NifferOrder();
        nifferOrder.setOriginalOrderId(order.getId());
        nifferOrder.setStatus(NifferOrderStatus.waiting_audit);

        nifferOrder.setShipmentFee(0d);

        if (consumerName == null || consumerName.isEmpty()) {
            nifferOrder.setConsumerName(order.getReceiverName());
        } else {
            nifferOrder.setConsumerName(consumerName);
        }
        if (consumerMobile == null || consumerMobile.isEmpty()) {
            nifferOrder.setConsumerMobile(order.getReceiverMobile());
        } else {
            nifferOrder.setConsumerMobile(consumerMobile);
        }
        nifferOrder.setConsumerPhone(order.getReceiverPhone());
        nifferOrder.setConsumerProvince(order.getProvince());
        nifferOrder.setConsumerCity(order.getCity());
        nifferOrder.setConsumerCounty(order.getCounty());
        nifferOrder.setConsumerTown(order.getTown());
        nifferOrder.setAddress(order.getReceiverAddr());
        nifferOrder.setApplyTime(new Date());
        nifferOrder.setRemark(remark);
        nifferOrder.setType(afterSaleType);
        List<NifferOrderEntry> nifferOrderEntryList = new ArrayList();

        NifferOrderEntry inOrderEntry = new NifferOrderEntry();
        inOrderEntry.setStorageUnitId(storageUnitId);
        inOrderEntry.setAmount(amount);
        inOrderEntry.setEntryType(NifferOrderEntryType.in);
        inOrderEntry.setProductId(productId);
        nifferOrderEntryList.add(inOrderEntry);
        //如果为换货,则同时生成一笔换货商品数据
        if (AfterSaleType.niffer.equals(afterSaleType)) {
            NifferOrderEntry outOrderEntry = new NifferOrderEntry();
            outOrderEntry.setStorageUnitId(storageUnitId);
            outOrderEntry.setAmount(amount);
            outOrderEntry.setEntryType(NifferOrderEntryType.out);
            outOrderEntry.setProductId(productId);
            nifferOrderEntryList.add(outOrderEntry);
            nifferOrder.setReason(nifferReason);
        } else {
            if (orderEntry.getAmount() < amount) {
            	session.setAttribute(NIFFER_TOKEN,System.currentTimeMillis());
                return new SimpleJsonResponse(false, "申请退款商品数量不能超过实际购买数量！");
            }
            nifferOrder.setReason(returnedReason);
            
//            //需求 所有退款订单金额都取 总价减去优惠券
//            Double refund = orderEntry.getSoldPrice() * amount - (order.getTotalCost() - order.getNeedPay());
//            refund = refund > 0 ? refund :0;
//            
//            //退款金额取购入时的价格
//            nifferOrder.setRefundAmount(refund);
            //退款金额取购入时的价格(考虑到优惠券)
            nifferOrder.setRefundAmount(order.getNeedPay()-order.getShipmentFee());
        }
        try {
            nifferOrder.setIsSystem(OwnerType.system.equals(product.getOwnerType())&& !SubOwnerType.wangyi.equals(product.getSubOwnerType()));
            if (nifferOrder.getIsSystem()) {
                nifferOrder.setOpenStatus(NifferOrderStatus.waiting_audit);
            }
            nifferOrderService.applyNiffer(order, websiteUser, nifferOrder, nifferOrderEntryList, request.getRemoteAddr());
            return new SimpleJsonResponse(true, nifferOrder.getId());
        } catch (Exception e) {
        	session.setAttribute(NIFFER_TOKEN,System.currentTimeMillis());
            return new SimpleJsonResponse(false, "提交售后失败，请重试！");
        }
    }
    /**
     * 退款跟订单走
     */
    @RequestMapping(value = "/mobile/order/doApplyRefund.php", method = RequestMethod.POST)
    public @ResponseBody
    SimpleJsonResponse doApplyRefund(@RequestParam(value = "orderId", required = true) long orderId,
            @RequestParam(value = "sui[]", required = false) int[] storageUnitIds,
            @RequestParam(value = "productId[]", required = true) int[] productIds,
            @RequestParam(value = "amount[]", required = true) int[] amounts,
            @RequestParam(value = "reason", required = true) String returnedReason,
            @RequestParam(value = "remark", required = false) String remark,
            @RequestParam(value = "afterSaleType", required = true) AfterSaleType afterSaleType,
            WebsiteUser websiteUser, Website website, HttpSession session,Model model, HttpServletRequest request) throws UnsupportedEncodingException, IOException, ParseException {
        
        if(storageUnitIds.length!= productIds.length && productIds.length!=amounts.length){
            throw new RuntimeException("参数错误，请刷新再重试");
        }
        Object nifferToken = session.getAttribute(NIFFER_TOKEN);
        if(nifferToken==null){
        	return new SimpleJsonResponse(false, "页面过期或重复提交，请刷新页面重试！");
        }
        session.removeAttribute(NIFFER_TOKEN);
        
        ShoppingOrder order = orderService.findOne(orderId);
        if (order == null || order.isNiffer() || order.isDeprecated() 
                || !((order.getDeliveryObjectType() == OwnerType.enterprise || order.getDeliveryObjectType() == OwnerType.provider) && (order.getStatus() == OrderStatus.waiting_audit || order.getStatus() == OrderStatus.waiting_shipment))) {
            throw new PermissionDeniedException();
        }
       
        if (websiteUser == null || !Objects.equals((long) order.getUserId(), websiteUser.getId())) {
            throw new PermissionDeniedException();
        }
       
        NifferOrder nifferOrder = new NifferOrder();
        nifferOrder.setOriginalOrderId(order.getId());
        nifferOrder.setStatus(NifferOrderStatus.waiting_audit);
        nifferOrder.setShipmentFee(0d);
        nifferOrder.setConsumerName(order.getReceiverName());
        nifferOrder.setConsumerMobile(order.getReceiverMobile());
        nifferOrder.setConsumerPhone(order.getReceiverPhone());
        nifferOrder.setConsumerProvince(order.getProvince());
        nifferOrder.setConsumerCity(order.getCity());
        nifferOrder.setConsumerCounty(order.getCounty());
        nifferOrder.setConsumerTown(order.getTown());
        nifferOrder.setAddress(order.getReceiverAddr());
        nifferOrder.setApplyTime(new Date());
        nifferOrder.setRemark(remark);
        nifferOrder.setType(AfterSaleType.returned);
        nifferOrder.setReason(returnedReason);
        List<NifferOrderEntry> nifferOrderEntryList = new ArrayList();
        List<OrderEntry> orderEntryList = orderService.findEntries(order);
        NifferOrderEntry inOrderEntry =  null;
        //double refundAmount = 0;
        for (OrderEntry orderEntry : orderEntryList) {
            inOrderEntry = new NifferOrderEntry();
            inOrderEntry.setStorageUnitId(orderEntry.getStorageUnitId());
            inOrderEntry.setAmount(orderEntry.getAmount());
            inOrderEntry.setEntryType(NifferOrderEntryType.in);
            inOrderEntry.setProductId((int)orderEntry.getSourceObjectId());
            nifferOrderEntryList.add(inOrderEntry);
            //退款金额取购入时的价格
           // refundAmount+=orderEntry.getSoldPrice() * orderEntry.getAmount();
        }
        //退款时 取实际支付的金额
        nifferOrder.setRefundAmount(order.getNeedPay()-order.getShipmentFee());
        try {
            nifferOrderService.applyRefundForOrderRefund(order, websiteUser, nifferOrder, nifferOrderEntryList, request.getRemoteAddr());
            return new SimpleJsonResponse(true, nifferOrder.getId());
        } catch (Exception e) {
        	session.setAttribute(NIFFER_TOKEN,System.currentTimeMillis());
            return new SimpleJsonResponse(false, "提交售后失败，请重试！");
        }
    }
    
    private boolean isCanAfterSaleForSelfProduct(AfterSaleType afterSaleType, OrderStatus orderStatus) {
        if (afterSaleType.equals(AfterSaleType.returned) && (orderStatus == OrderStatus.waiting_confirmed || orderStatus == OrderStatus.completed || orderStatus.equals(OrderStatus.waiting_audit) || orderStatus.equals(OrderStatus.waiting_shipment))
                || afterSaleType.equals(AfterSaleType.niffer) && (orderStatus == OrderStatus.waiting_confirmed || orderStatus == OrderStatus.completed)) {
            return true;
        }
        return false;
    }
    /**
     * 判断是否允许售后，目前该方法只做部分判断
     * @param orderId
     * @param productId
     * @param ownerType
     * @param subOwnerType
     * @return
     */
	private boolean allowAftersale(long orderId,int productId, OwnerType ownerType, SubOwnerType subOwnerType) {
		// 如果是京东和网易的商品，则限制订单完成后n（如n=7天）内允许申请售后
		if (OwnerType.jingdong.equals(ownerType)
				|| (OwnerType.system.equals(ownerType) && SubOwnerType.wangyi.equals(subOwnerType))) {
			OrderOperationLog orderOperationLog = orderOperationLogService.findOne(orderId, "确认收货");
			if (orderOperationLog != null) {
				int days = systemConfig.getAfterCompletedOrderForEnableAftersaleDays();
				Date shipmentDate = orderOperationLog.getTime();
				// 当前时间大于最后限制时间,可以申请售后
				if (LMFDateUtil.getDate(shipmentDate, days).before(Calendar.getInstance().getTime())) {
					return false;
				}
			}
		}
		return true;
	}
}
