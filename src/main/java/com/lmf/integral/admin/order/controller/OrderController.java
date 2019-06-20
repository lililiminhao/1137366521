/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.order.controller;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.Range;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.enums.OwnerType;
import com.lmf.common.tuple.TernaryTuple;
import com.lmf.common.util.CommonUtil;
import com.lmf.common.util.PagerSpec;
import com.lmf.common.util.StringsUtil;
import com.lmf.enterprise.entity.Enterprise;
import com.lmf.enterprise.service.EnterpriseService;
import com.lmf.market.entity.Coupon;
import com.lmf.market.entity.FenxiaoOrder;
import com.lmf.market.entity.UserCoupon;
import com.lmf.market.service.CouponService;
import com.lmf.market.service.FenXiaoOrderService;
import com.lmf.market.service.UserCouponService;
import com.lmf.order.entity.*;
import com.lmf.order.enums.OrderPayType;
import com.lmf.order.enums.OrderStatus;
import com.lmf.order.service.*;
import com.lmf.order.vo.OrderCriteria;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.StorageUnit;
import com.lmf.product.service.ProductService;
import com.lmf.product.service.StorageUnitService;
import com.lmf.sys.entity.GeoRegion;
import com.lmf.sys.entity.ShipmentCompany;
import com.lmf.sys.service.GeoRegionService;
import com.lmf.sys.service.ShipmentCompanyService;
import com.lmf.system.sdk.service.SystemOrderService;
import com.lmf.system.sdk.vo.OrderTrackResult;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteAdministrator;
import com.lmf.website.service.WebsiteAdministratorService;
import com.lmf.website.service.WebsiteUserService;
import com.lmf.website.vo.WebsiteAdministratorCriteria;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.Boolean;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author shenzhixiong
 */
@Controller("adminOrderController")
public class OrderController {

    @Autowired
    private OrderOperationLogService orderOperationLogService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserCouponService usercouponService;

    @Autowired
    private StorageUnitService storageUnitService;

    @Autowired
    private WebsiteUserService websiteUserService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private GeoRegionService geoRegionService;

    @Autowired
    FenXiaoOrderService fenXiaoOrderService;

    @Autowired
    private ShipmentCompanyService shipmentCompanyService;

    @Autowired
    private SystemOrderService systemOrderService;

    @Autowired
    private WebsiteAdministratorService websiteAdministratorService;

    @Autowired
    private NifferOrderService nifferOrderService;

    @Autowired
    private OrderSettlementLogService orderSettlementLogService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private TimeZone tz;

    @Autowired
    private ShipmentOrderDetailService shipmentOrderDetailService;

    private static final  Logger logger = LoggerFactory.getLogger(OrderController.class);

    @RequiresPermissions("order:view")
    @RequestMapping(value = "/admin/orders.php")
    public String list(@RequestParam(value = "kw", required = false) String keyword,
                       @RequestParam(value = "st", required = false) Date startTime,
                       @RequestParam(value = "et", required = false) Date endTime,
                       @RequestParam(value = "prov", required = false) GeoRegion prov,
                       @RequestParam(value = "providerId", required = false) Integer providerId, //供应商ID
                       @RequestParam(value = "status", required = false) String orderStatus,
                       @RequestParam(value = "enterpriseId", required = false) Integer enterpriseId,//企业ID
                       @RequestParam(value = "isEnterprise", required = false) Boolean isEnterprise,//是否为企业专享订单
                       @PagerSpecDefaults(pageSize = 20, maxPageSize = 100, allowSortFileds = {"time", "owner", "lastModifyTime"}, checkSortFields = true, sort = "time.desc") PagerSpec pager,
                       WebsiteAdministrator admin,
                       Website website, Model model) throws UnsupportedEncodingException, ParseException {
        StringBuilder link = new StringBuilder("/jdvop/admin/orders.php?page=[:page]");
        if (keyword != null) {
            link.append("&kw=").append(URLEncoder.encode(keyword, "UTF-8"));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (startTime != null) {
            link.append("&st=").append(sdf.format(startTime));
            model.addAttribute("st", sdf.format(startTime));
        }
        if (endTime != null) {
            Calendar date = Calendar.getInstance();
            date.setTime(endTime);
            date = CommonUtil.filled(date);
            endTime = sdf.parse(sdf.format(date.getTime()));
            model.addAttribute("et", sdf.format(endTime));
            link.append("&et=").append(sdf.format(date.getTime()));
        }


        if (prov != null) {
            model.addAttribute("provValue", prov.getId());
            link.append("&prov=").append(prov.getId());
        }

        if (providerId != null && providerId > 0) {
        	model.addAttribute("providerId", providerId);
        	link.append("&providerId=").append(providerId);
        }

        if (enterpriseId != null) {
            model.addAttribute("enterpriseId", enterpriseId);
            link.append("&enterpriseId=").append(enterpriseId);
        }
        if (isEnterprise != null) {
            model.addAttribute("isEnterprise", isEnterprise);
            link.append("&isEnterprise=").append(isEnterprise);
        }

        OrderStatus[] status = null;
        if (orderStatus != null) {
            switch (orderStatus) {
                case "waiting_audit":
                    status = new OrderStatus[]{OrderStatus.waiting_audit};
                    break;
                case "waiting_shipment":
                    status = new OrderStatus[]{OrderStatus.waiting_shipment};
                    break;
                case "waiting_confirmed":
                    status = new OrderStatus[]{OrderStatus.waiting_confirmed};
                    break;
                case "completed":
                    status = new OrderStatus[]{OrderStatus.completed};
                    break;
                default:
                    break;
            }
            model.addAttribute("status", status);
            link.append("&status=").append(orderStatus);
        }
        OrderCriteria criteria = new OrderCriteria().withKeyword(keyword).withCreateTime(new Range(startTime, endTime)).withRegion(prov).withStatus(status).withProviderId(providerId).withEnterpriseId(enterpriseId).withEnterprise(isEnterprise);
        Page<ShoppingOrder> orders = null;
        if (admin.isProvider()) {
            orders = orderService.find(OwnerType.provider, admin.getId(), criteria, pager);
        } else {
            orders = orderService.findSelfAndProvider(criteria, pager);
        }
        if (orders.hasContent()) {

        	//剔除父订单
        	List<ShoppingOrder> list = orders.getContent();
        	Iterator<ShoppingOrder> iterator = list.iterator();
            while (iterator.hasNext()) {
            	ShoppingOrder shoppingOrder = iterator.next();
        		List<ShoppingOrder> childOrder1 = orderService.findSplitChildOrder1(shoppingOrder, null);
        		if(CollectionUtils.isNotEmpty(childOrder1))
        		{
        			iterator.remove();
        		}
			}

            //查询在列表页中显示产品详情所需数据
            Map<ShoppingOrder, List<OrderEntry>> entries = orderService.findEntries(orders.getContent());
            model.addAttribute("entries", entries);

            //查询本页相关联的SKU数据
            Set<Integer> storageUnitIds = new HashSet<>();
            for (List<OrderEntry> le : entries.values()) {
                for (OrderEntry e : le) {
                    storageUnitIds.add(e.getStorageUnitId());
                }
            }
            Map<Integer, StorageUnit> storageUnits = storageUnitService.find(storageUnitIds);
            model.addAttribute("storageUnits", storageUnits);

            //查询本页相关联的Product数据
            Set<Integer> productIds = new HashSet<>();
            for (StorageUnit sku : storageUnits.values()) {
                productIds.add(sku.getProductId());
            }
            model.addAttribute("products", productService.findAsMap(productIds));

        }

        Calendar cal = Calendar.getInstance(tz);
        Date endDate = cal.getTime();
        cal.add(Calendar.MONTH, -2);
        Date startDate = cal.getTime();

        List<Enterprise> enterpriseList = enterpriseService.list(null);
        Map<Integer, Enterprise> enterpriseMap = enterpriseService.map();
        model.addAttribute("enterpriseList",enterpriseList);
        model.addAttribute("enterpriseMap",enterpriseMap);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("admin", admin);
        model.addAttribute("orders", orders);
        model.addAttribute("link", link.toString());
        model.addAttribute("status", orderStatus);
        model.addAttribute("provinces", geoRegionService.findAllProvince());
        model.addAttribute("shipmentCompanys", shipmentCompanyService.all());
        //查询所有供应商信息
        model.addAttribute("providerMap", websiteAdministratorService.findAllProvider());
        return "admin/order/list";
    }

    @RequiresPermissions("system_order:view")
    @RequestMapping(value = "/admin/order/systemOrders.php")
    public String systemOrders(@RequestParam(value = "kw", required = false) String keyword,
                               @RequestParam(value = "st", required = false) Date startTime,
                               @RequestParam(value = "et", required = false) Date endTime,
                               @RequestParam(value = "prov", required = false) GeoRegion prov,
                               @RequestParam(value = "orderStatus", required = false) OrderStatus orderStatus,
                               @RequestParam(value = "type", defaultValue = "system") OwnerType type,
                               @PagerSpecDefaults(pageSize = 20, maxPageSize = 100, allowSortFileds = {"time", "owner", "lastModifyTime"}, checkSortFields = true, sort = "time.desc") PagerSpec pager,
                               @RequestParam(value = "enterpriseId", required = false) Integer enterpriseId,//企业ID
                               @RequestParam(value = "isEnterprise", required = false) Boolean isEnterprise,//是否为企业专享订单
                               Website website, Model model) throws UnsupportedEncodingException, ParseException {
        StringBuilder link = new StringBuilder("/jdvop/admin/order/systemOrders.php?page=[:page]");
        if (keyword != null) {
            link.append("&kw=").append(URLEncoder.encode(keyword, "UTF-8"));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (startTime != null) {
            link.append("&st=").append(sdf.format(startTime));
            model.addAttribute("st", sdf.format(startTime));
        }
        if (endTime != null) {
            Calendar date = Calendar.getInstance();
            date.setTime(endTime);
            date = CommonUtil.filled(date);
            endTime = sdf.parse(sdf.format(date.getTime()));
            model.addAttribute("et", sdf.format(endTime));
            link.append("&et=").append(sdf.format(date.getTime()));
        }

        if (prov != null) {
            model.addAttribute("provValue", prov.getId());
            link.append("&prov=").append(prov.getId());
        }

        if (orderStatus != null) {
            model.addAttribute("orderStatus", orderStatus);
            link.append("&orderStatus=").append(orderStatus);
        }
        if (enterpriseId != null) {
            model.addAttribute("enterpriseId", enterpriseId);
            link.append("&enterpriseId=").append(enterpriseId);
        }
        if (isEnterprise != null) {
            model.addAttribute("isEnterprise", isEnterprise);
            link.append("&isEnterprise=").append(isEnterprise);
        }

        Page<ShoppingOrder> orders = orderService.find(type, null, new OrderCriteria().withKeyword(keyword).withCreateTime(new Range(startTime, endTime)).withRegion(prov).withStatus(orderStatus).withEnterpriseId(enterpriseId).withEnterprise(isEnterprise), pager);
        if (orders.hasContent()) {

        	//剔除父订单
        	List<ShoppingOrder> list = orders.getContent();
        	Iterator<ShoppingOrder> iterator = list.iterator();
            while (iterator.hasNext()) {
            	ShoppingOrder shoppingOrder = iterator.next();
        		List<ShoppingOrder> childOrder1 = orderService.findSplitChildOrder1(shoppingOrder, null);
        		if(CollectionUtils.isNotEmpty(childOrder1))
        		{
        			iterator.remove();
        		}
			}
            //查询在列表页中显示产品详情所需数据
            Map<ShoppingOrder, List<OrderEntry>> entries = orderService.findEntries(orders.getContent());
            model.addAttribute("entries", entries);

            //查询本页相关联的SKU数据
            Set<Integer> storageUnitIds = new HashSet<>();
            for (List<OrderEntry> le : entries.values()) {
                for (OrderEntry e : le) {
                    storageUnitIds.add(e.getStorageUnitId());
                }
            }
            Map<Integer, StorageUnit> storageUnits = storageUnitService.find(storageUnitIds);
            model.addAttribute("storageUnits", storageUnits);

            //查询本页相关联的Product数据
            Set<Integer> productIds = new HashSet<>();
            for (StorageUnit sku : storageUnits.values()) {
                productIds.add(sku.getProductId());
            }
            if (productIds.size() > 0) {
                model.addAttribute("products", productService.findAsMap(productIds));
            }

        }

        Calendar cal = Calendar.getInstance(tz);
        Date endDate = cal.getTime();
        cal.add(Calendar.MONTH, -2);
        Date startDate = cal.getTime();

        List<Enterprise> enterpriseList = enterpriseService.list(null);
        Map<Integer, Enterprise> enterpriseMap = enterpriseService.map();
        model.addAttribute("enterpriseList",enterpriseList);
        model.addAttribute("enterpriseMap",enterpriseMap);

        model.addAttribute("orderService",orderService);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        model.addAttribute("orders", orders);
        model.addAttribute("link", link.toString());
        model.addAttribute("initOrderStatus", OrderStatus.values());
        model.addAttribute("provinces", geoRegionService.findAllProvince());
        model.addAttribute("shipmentCompanys", shipmentCompanyService.all());
        model.addAttribute("type", type);

        return "admin/order/system";
    }

    @RequiresPermissions("order:examine")
    @RequestMapping(value = "/admin/order/audit.php", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public @ResponseBody SimpleJsonResponse audit(@RequestParam(value = "ids[]", required = false) Long[] ids,
                                                  WebsiteAdministrator admin,
                                                  HttpServletRequest request) throws UnsupportedEncodingException {

        if (ids.length < 1) {
            return new SimpleJsonResponse(false, "请指定至少一条订单进行操作！");
        }

        boolean skipError = true;
        if (ids.length == 1) {
            skipError = false;
        }
        int successCount = 0;
        int failCount = 0;
        for (long id : ids) {
            ShoppingOrder order = orderService.findOne(id);
            if (order == null) {
                if (skipError) {
                    ++failCount;
                    continue;
                } else {
                    return new SimpleJsonResponse(false, "数据错误，请勿非法操作！");
                }
            }

            if (admin.isProvider() && order.isSelf()) {
                if (skipError) {
                    ++failCount;
                    continue;
                } else {
                    return new SimpleJsonResponse(false, "审核失败,无法审核不属于自己的订单!");
                }
            }

            if (!OrderStatus.waiting_audit.equals(order.getStatus())) {
                if (skipError) {
                    continue;
                } else {
                    return new SimpleJsonResponse(false, "审核失败,只能审核状态为等待客服审核的订单!");
                }
            }
            orderService.consumerServiceAudit(order, admin, request.getRemoteAddr());
            ++successCount;
        }

        return new SimpleJsonResponse(true, "您成功审核<font class='green'>" + successCount + "</font>条订单,失败<font class='red'>" + failCount + "</font>条订单！");
    }

    @RequiresPermissions("order:examine")
    @RequestMapping(value = "/admin/order/deaudit.php", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public @ResponseBody SimpleJsonResponse deAuditOrder(@RequestParam(value = "ids[]", required = false) Long[] ids,
                                                         WebsiteAdministrator admin,
                                                         HttpServletRequest request) throws UnsupportedEncodingException {
        if (ids.length < 1) {
            return new SimpleJsonResponse(false, "请指定至少一条订单进行操作！");
        }

        boolean skipError = true;
        if (ids.length == 1) {
            skipError = false;
        }
        int successCount = 0;
        int failCount = 0;
        for (long id : ids) {
            ShoppingOrder order = orderService.findOne(id);
            if (order == null) {
                if (skipError) {
                    ++failCount;
                    continue;
                } else {
                    return new SimpleJsonResponse(false, "数据错误，请勿非法操作！");
                }
            }

            if (admin.isProvider() && order.isSelf()) {
                if (skipError) {
                    ++failCount;
                    continue;
                } else {
                    return new SimpleJsonResponse(false, "审核失败,无法审核不属于自己的订单!");
                }
            }

            if (!OrderStatus.waiting_shipment.equals(order.getStatus())) {
                if (skipError) {
                    continue;
                } else {
                    return new SimpleJsonResponse(false, "审核失败,只能反审状态为已出库的订单!");
                }
            }
            ++successCount;
            orderService.deAudit(order, admin, request.getRemoteAddr());
        }

        return new SimpleJsonResponse(true, "您成功审核<font class='green'>" + successCount + "</font>条订单,失败<font class='red'>" + failCount + "</font>条订单！");
    }

    @RequiresPermissions("order:view")
    @RequestMapping(value = "/admin/order.php", method = RequestMethod.GET)
    public String view(@RequestParam("id") long orderID, Website website, Model model) throws UnsupportedEncodingException, IOException, ParseException {
        ShoppingOrder order = orderService.findOne(orderID);
        if(order.getCouponId() != null){
        	Coupon coupon = couponService.findById(order.getCouponId());
        	if(coupon != null){
        		order.setCoupon(coupon);
        		Double really = order.getTotalCost()-coupon.getAmount().doubleValue();
        		really = really > 0 ? really : 0;
        		model.addAttribute("really", really);
        	}
        }

        if (order.getDeliveryObjectType() == OwnerType.enterprise || order.getDeliveryObjectType() == OwnerType.provider) {
            OrderShipmentLog shipmentLog = orderService.findShipmentLog(order);
            if (shipmentLog != null) {
                model.addAttribute("shipmentLog", shipmentLog);
            }
        } else {
            if (order.getStatus() != OrderStatus.canceld && order.getStatus() != OrderStatus.cancled_before_pay && order.getStatus() != OrderStatus.waiting_pay) {
                OrderTrackResult track = systemOrderService.findOrderTrack(website, order);
                if (track != null) {
                    model.addAttribute("sod", track);
                }
            }
        }

        if (order.getUserId() != null) {
            model.addAttribute("user", websiteUserService.findOne(order.getUserId()));
        }

        OrderPayLog payLog = orderService.findPayLog(order);
        if (payLog != null) {
            model.addAttribute("paylog", payLog);
        } else if (order.isSplit() && order.getOriginalOrderId() != null) {
            ShoppingOrder originalOrder = orderService.findOne(order.getOriginalOrderId());
            if (originalOrder == null) {
                originalOrder = orderService.findOne(order.getOriginalOrderId());
            }
            payLog = orderService.findPayLog(originalOrder);
            if (payLog != null) {
                model.addAttribute("paylog", payLog);
            }
        }
        Map<Integer, Enterprise> enterpriseMap = enterpriseService.map();
        model.addAttribute("enterpriseMap",enterpriseMap);
        model.addAttribute("order", order);
        model.addAttribute("logs", orderOperationLogService.find(order));
        model.addAttribute("entries", orderService.findEntries(order));
        model.addAttribute("orderService",orderService);
        model.addAttribute("productService", productService);
        model.addAttribute("storageUnitService", storageUnitService);
        model.addAttribute("geoRegionService", geoRegionService);
        return "admin/order/view";
    }

    @RequiresPermissions("order:shipment")
    @RequestMapping(value = "/admin/order/setToShipment.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse setToShipment(@RequestParam("id") Long orderId,
                                                          @RequestParam("shipmentOrder") String shipmentOrder,
                                                          @RequestParam("shipmentCompanyId") Integer shipmentCompanyId,
                                                          Website website,
                                                          WebsiteAdministrator admin,
                                                          HttpServletRequest request) {
        ShoppingOrder order = orderService.findOne(orderId);
        if (order == null) {
            return new SimpleJsonResponse(false, "订单不存在或已经被删除");
        }
        if (admin.isProvider()) {
            if (order.getDeliveryObjectType() != OwnerType.provider && !Objects.equals(order.getDeliveryObjectId(), admin.getId())) {
                return new SimpleJsonResponse(false, "您无权操作此订单！");
            }
        }
//        else {
//            if(order.getDeliveryObjectType() != OwnerType.enterprise) {
//                return new SimpleJsonResponse(false, "您无权操作此订单！");
//            }
//        }
        if (shipmentOrder.trim().isEmpty() || shipmentOrder.trim().length() <= 4) {
            return new SimpleJsonResponse(false, "请填写正确的快递单号");
        }

        ShipmentCompany shipmentCompany = shipmentCompanyService.findOne(shipmentCompanyId);

        if (shipmentCompany == null) {
            return new SimpleJsonResponse(false, "请选择快递公司");
        }
        orderService.setToShipment(order, admin, website, shipmentCompany, shipmentOrder, request.getRemoteAddr());
        return new SimpleJsonResponse(true, "发货成功");
    }

    @RequiresPermissions("order:edit")
    @RequestMapping(value = "/admin/order/cancel.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse cancel(@RequestParam("ids[]") Long[] ids,
                                                   Website website,
                                                   WebsiteAdministrator admin,
                                                   HttpServletRequest request) {

        if (ids.length < 1) {
            return new SimpleJsonResponse(false, "请选择您要作废的订单");
        }
        boolean skipError = true;
        if (ids.length == 1) {
            skipError = false;
        }
        for (long id : ids) {
            ShoppingOrder order = orderService.findOne(id);
            if (order == null) {
                if (skipError) {
                    continue;
                } else {
                    return new SimpleJsonResponse(false, "订单不存在");
                }
            }
            if (admin.isProvider()) {
                if (order.getDeliveryObjectType() != OwnerType.provider && !Objects.equals(order.getDeliveryObjectId(), admin.getId())) {
                    if (skipError) {
                        continue;
                    } else {
                        return new SimpleJsonResponse(false, "您无法操作属于您的订单");
                    }
                }
            } else {
                if (order.getDeliveryObjectType() != OwnerType.enterprise && order.getDeliveryObjectType() != OwnerType.provider) {
                    if (skipError) {
                        continue;
                    } else {
                        return new SimpleJsonResponse(false, "您无法操作属于您的订单");
                    }
                }
            }

//            if (order.getStatus() != OrderStatus.waiting_audit) {
//                return new SimpleJsonResponse(false, "只能作废等待客服审核的订单");
//            }
            if (order.getStatus() != OrderStatus.waiting_pay) {
                return new SimpleJsonResponse(false, "只能作废等待买家付款的订单");
            }
            orderService.cancel(order, admin, request.getRemoteAddr());
          //清除分销商冻结额度
            List<FenxiaoOrder> fenxiaoLists = fenXiaoOrderService.findByOrderId(order.getId());
            if(CollectionUtils.isNotEmpty(fenxiaoLists)){
            	for (FenxiaoOrder fenxiaoOrder : fenxiaoLists) {
            		fenXiaoOrderService.clearFreezeMoney(fenxiaoOrder.getId());
				}
            	//修改状态
            	fenXiaoOrderService.del(order.getId());
            }
            List<ShoppingOrder> list = orderService.findSplitChildOrder1(order, null);
            if(CollectionUtils.isNotEmpty(list) && list.size()>1){
            	for (ShoppingOrder childOrder : list) {
                    if(childOrder.getCouponId() != null && childOrder.getCouponId() != 0 ){
                    	Coupon coupon = couponService.findById(childOrder.getCouponId());
                    	if(coupon!=null){
                    		List<UserCoupon> userAndCoupon = usercouponService.selectByUserAndCoupon(childOrder.getUserId().longValue(), coupon.getId());
                    		if(CollectionUtils.isNotEmpty(userAndCoupon)){
                    			for (UserCoupon userCoupon : userAndCoupon) {
                    				if(userCoupon.getEndTime().after(new Date())){
                    					userCoupon.setStatus(1);
                    				}else{
                    					userCoupon.setStatus(-1);
                    				}
                    				usercouponService.edit(userCoupon);
        						}
                    		}
                    	}
                    }
            	}
            }else{
                if(order.getCouponId() != null && order.getCouponId() != 0 ){
                	Coupon coupon = couponService.findById(order.getCouponId());
                	if(coupon!=null){
                		List<UserCoupon> userAndCoupon = usercouponService.selectByUserAndCoupon(order.getUserId().longValue(), coupon.getId());
                		if(CollectionUtils.isNotEmpty(userAndCoupon)){
                			for (UserCoupon userCoupon : userAndCoupon) {
                				if(userCoupon.getEndTime().after(new Date())){
                					userCoupon.setStatus(1);
                				}else{
                					userCoupon.setStatus(-1);
                				}
                				usercouponService.edit(userCoupon);
    						}
                		}
                	}
                }
            }

//            if(order.getCouponId() != null && order.getCouponId() != 0 ){
//            	Coupon coupon = couponService.findById(order.getCouponId());
//            	if(coupon!=null){
//            		List<UserCoupon> userAndCoupon = usercouponService.selectByUserAndCoupon(order.getUserId().longValue(), coupon.getId());
//            		if(CollectionUtils.isNotEmpty(userAndCoupon)){
//            			for (UserCoupon userCoupon : userAndCoupon) {
//            				if(userCoupon.getEndTime().after(new Date())){
//            					userCoupon.setStatus(1);
//            				}else{
//            					userCoupon.setStatus(-1);
//            				}
//            				usercouponService.edit(userCoupon);
//						}
//            		}
//            	}
//            }
//
        }

        return new SimpleJsonResponse(true, "订单作废成功");
    }

    @RequiresPermissions("order:edit")
    @RequestMapping(value = "/admin/order/deCancel.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse deCancel(@RequestParam("ids[]") Long[] ids,
                                                     Website website,
                                                     WebsiteAdministrator admin,
                                                     HttpServletRequest request) throws UnsupportedEncodingException {
        if (ids.length < 1) {
            return new SimpleJsonResponse(false, "请选择您要作废的订单");
        }
        boolean skipError = true;
        if (ids.length == 1) {
            skipError = false;
        }
        for (long id : ids) {
            ShoppingOrder order = orderService.findOne(id);
            if (order == null) {
                if (skipError) {
                    continue;
                } else {
                    return new SimpleJsonResponse(false, "订单不存在");
                }
            }
            if (admin.isProvider()) {
                if (order.getDeliveryObjectType() != OwnerType.provider && !Objects.equals(order.getDeliveryObjectId(), admin.getId())) {
                    if (skipError) {
                        continue;
                    } else {
                        return new SimpleJsonResponse(false, "您无法操作属于您的订单");
                    }
                }
            } else {
                if (order.getDeliveryObjectType() != OwnerType.enterprise && order.getDeliveryObjectType() != OwnerType.provider) {
                    if (skipError) {
                        continue;
                    } else {
                        return new SimpleJsonResponse(false, "您无法操作属于您的订单");
                    }
                }
            }

            if (order.getStatus() != OrderStatus.canceld) {
                return new SimpleJsonResponse(false, "该订单不是作废状态,无法取消");
            }
            orderService.deCancel(order, website, admin, request.getRemoteAddr());
        }

        return new SimpleJsonResponse(true, "/admin/orders.php");
    }

    //高级导出订单
    @RequiresPermissions("order:export")
    @RequestMapping(value = "/admin/order/exportExcel.php", method = RequestMethod.POST)
    public @ResponseBody void exportExcel(@RequestParam(value = "kw", required = false) String keyword,
                                          @RequestParam(value = "status", required = false) OrderStatus status,
                                          @RequestParam(value = "payType", required = false) OrderPayType payType,
                                          @RequestParam("timeType") String timeType,
                                          @RequestParam("st") Date startDate,
                                          @RequestParam("et") Date endDate,
                                          @RequestParam(value = "ownerType", defaultValue = "enterprise") String type,
                                          @RequestParam(value = "providerId", required = false) Integer providerId, //供应商ID
                                          @RequestParam(value = "settle") Boolean settle, //是否已结算
                                          @RequestParam(value="settleExcel", defaultValue = "false") Boolean settleExcel, //是否导出结算表（ture 会新增一列值）
                                          @RequestParam(value = "enterpriseId", required = false) Integer enterpriseId,//企业ID
                                          @RequestParam(value = "isEnterprise", required = false) Boolean isEnterprise,//是否为企业专享订单
                                          WebsiteAdministrator admin,
                                          Website website, HttpServletResponse response) throws UnsupportedEncodingException, IOException, WriteException {
        if (website.getRatio() == null || website.getRatio() <= 0) {
            throw new RuntimeException();
        }
//        if (endDate != null) {
//            Calendar cal = Calendar.getInstance(tz);
//            cal.setTime(endDate);
//            cal.add(Calendar.HOUR, 23);
//            cal.add(Calendar.MINUTE, 59);
//            cal.add(Calendar.SECOND, 59);
//            cal.add(Calendar.MILLISECOND, 999);
//            endDate = cal.getTime();
//        }
        Page<TernaryTuple<ShoppingOrder, OrderShipmentLog, OrderPayLog>> orders;
        PagerSpec pager = new PagerSpec(1, 0, 999999999, new PagerSpec.Order[]{new PagerSpec.Order("time", PagerSpec.DRECTION.desc)});
        OrderCriteria criteria = new OrderCriteria().withKeyword(keyword).withStatus(status).withSystemDeprecated(false).withProviderId(providerId).withSettle(settle).withEnterpriseId(enterpriseId).withEnterprise(isEnterprise);
        if(settleExcel){ //结算订单
        	criteria.withNiffer(false);
        }
        switch (timeType) {
            case "createTime":
                criteria.withCreateTime(startDate, endDate);
                break;
            case "shipmentTime":
                criteria.withShipmentTime(startDate, endDate);
                break;
            case "confirmTime":
                criteria.withConfirmTime(startDate, endDate);
                break;
            default:
                throw new RuntimeException("unkown time type");
        }
        if (admin.isProvider()) {
            orders = orderService.findOrderWithShipmentLogAndPayLog(OwnerType.provider, admin.getId(), criteria, pager);
        } else {
            OwnerType[] ownerTypes;
            switch (type) {
                case "enterprise":
                    ownerTypes = new OwnerType[]{OwnerType.enterprise};
                    break;
                case "provider":
                    ownerTypes = new OwnerType[]{OwnerType.provider};
                    break;
                case "system":
                    ownerTypes = new OwnerType[]{OwnerType.system};
                    break;
                default:
                    ownerTypes = new OwnerType[]{OwnerType.enterprise, OwnerType.provider};
            }
            criteria.withOwnerType(ownerTypes);
            orders = orderService.findOrderWithShipmentLogAndPayLog(criteria, pager);
        }


        //准备导出Excel所需要的数据
        Map<ShoppingOrder, List<OrderEntry>> allEntries = null;
        Map<Integer, StorageUnit> allStorageUnits = null;
        Map<Integer, Product> allProducts = null;
        Map<Long, Map<Integer, Double>> orderProductRefundAmountMap = null;
        Map<Integer, Enterprise> enterpriseMap = null;
        List<ShoppingOrder> originalShoppingOrders = new ArrayList<>();
        if (orders.hasContent()) {
            List<ShoppingOrder> allOrders = new ArrayList<>();
            List<Long> orderIdList = new ArrayList<>();
            Set<Long> originalOrderIdList = new HashSet<>();
            for (TernaryTuple<ShoppingOrder, OrderShipmentLog, OrderPayLog> tuple : orders) {
                allOrders.add(tuple.getValue1());
                orderIdList.add(tuple.getValue1().getId());
            }

            //把原单号ID提取出来  然后根据原单ID集合查出原始订单
            for (ShoppingOrder shoppingOrder : allOrders) {
                if(shoppingOrder.isSplit() == true){
                    originalOrderIdList.add(shoppingOrder.getOriginalOrderId());
                }
            }
            if(originalOrderIdList.size()>0){
                originalShoppingOrders = orderService.find(originalOrderIdList);
            }

            allEntries = orderService.findEntries(allOrders);
            if(!orderIdList.isEmpty()){
                orderProductRefundAmountMap = nifferOrderService.findRefundAmountByOrderIds(orderIdList);
            }
            Set<Integer> storageUnitIds = new HashSet<>();
            for (List<OrderEntry> le : allEntries.values()) {
                for (OrderEntry oe : le) {
                    storageUnitIds.add(oe.getStorageUnitId());
                }
            }
            allStorageUnits = storageUnitService.find(storageUnitIds);

            Set<Integer> productIds = new HashSet<>();
            for (StorageUnit sku : allStorageUnits.values()) {
                productIds.add(sku.getProductId());
            }
            allProducts = productService.findAsMap(productIds);
            enterpriseMap = enterpriseService.map();
        }

        response.setContentType("application/force-download");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.addHeader("Content-Type", "application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", new StringBuilder("attachment;filename=").append(URLEncoder.encode("订单导出", "UTF-8")).append(".xls").toString());

        //资料准备齐开始写Excel了
        try (OutputStream out = response.getOutputStream()) {
            WritableWorkbook workbook = Workbook.createWorkbook(out);
            WritableSheet sheet = workbook.createSheet("导出订单列表", 0);
            //设置列宽
            sheet.setColumnView(0, 15);//订单号
            sheet.setColumnView(1, 20);//供应商
            sheet.setColumnView(2, 12);//订单状态
            sheet.setColumnView(3, 12);//卖家备注
            sheet.setColumnView(4, 10);//订单总价
            sheet.setColumnView(5, 10);//实付金额
            sheet.setColumnView(6, 22);//退款金额
            sheet.setColumnView(7, 20);//物流企业（*注）
            sheet.setColumnView(8, 10);//物流单号
            sheet.setColumnView(9, 17);//收货人
            sheet.setColumnView(10, 20);//收货人电话
            sheet.setColumnView(11, 50);//收货地址（包含省、市、区）
            sheet.setColumnView(12, 10);//省
            sheet.setColumnView(13, 10);//市
            sheet.setColumnView(14, 10);//县/区
            sheet.setColumnView(15, 10);//购买量
            sheet.setColumnView(16, 10);//规格
            sheet.setColumnView(17, 18);//供货价
            sheet.setColumnView(18, 18);//单价
            sheet.setColumnView(19, 18);//商品金额（购买量*单价）
            sheet.setColumnView(20, 18);//扣点%
            sheet.setColumnView(21, 20);//扣点金额（商品金额*扣点）
            sheet.setColumnView(22, 18);//结算金额
            sheet.setColumnView(23, 22);//商品名称
            sheet.setColumnView(24, 18);//商品货号
            sheet.setColumnView(25, 20);//商品属性（是：跨境 否：一般商品）
            sheet.setColumnView(26, 10);//结算状态
            sheet.setColumnView(27, 20);//结算时间
            sheet.setColumnView(28, 20);//订单留言
            sheet.setColumnView(29, 15);//下单时间
            sheet.setColumnView(30, 15);//支付时间
            sheet.setColumnView(31, 15);//确认收货时间
            sheet.setColumnView(32, 15);//配货时间
            sheet.setColumnView(33, 15);//支付方式
			sheet.setColumnView(34, 10);//所属企业名称
			sheet.setColumnView(35, 10);//父订单号
            sheet.setColumnView(34, 15);//来源
            sheet.setColumnView(35, 15);//优惠券面额
            if(settleExcel){ //为true 则新增一列
                sheet.setColumnView(36, 18);//是否结算
            }

            sheet.getSettings().setVerticalFreeze(1);
            sheet.getSettings().setHorizontalFreeze(2);
            sheet.getSettings().setProtected(true);

            //初始化标题
            WritableFont titleWft = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
            titleWft.setColour(Colour.BROWN);
            WritableCellFormat titleFmt = new WritableCellFormat(titleWft);
            titleFmt.setBackground(Colour.GRAY_25);

            WritableCellFormat priceFmt = new WritableCellFormat(new NumberFormat("#0.00"));
            WritableCellFormat intFmt = new WritableCellFormat(new NumberFormat("#"));

            sheet.addCell(new Label(0, 0, "订单号", titleFmt));
            sheet.addCell(new Label(1, 0, "供应商", titleFmt));
            sheet.addCell(new Label(2, 0, "订单状态", titleFmt));
            sheet.addCell(new Label(3, 0, "运费", titleFmt));
            sheet.addCell(new Label(4, 0, "订单总价", titleFmt));
            sheet.addCell(new Label(5, 0, "实付金额", titleFmt));
            sheet.addCell(new Label(6, 0, "退款金额", titleFmt));
            sheet.addCell(new Label(7, 0, "物流企业（*注）", titleFmt));
            sheet.addCell(new Label(8, 0, "物流单号", titleFmt));
            sheet.addCell(new Label(9, 0, "收货人", titleFmt));
            sheet.addCell(new Label(10, 0, "收货人电话", titleFmt));
            sheet.addCell(new Label(11, 0, "收货地址（包含省、市、区）", titleFmt));
            sheet.addCell(new Label(12, 0, "省", titleFmt));
            sheet.addCell(new Label(13, 0, "市", titleFmt));
            sheet.addCell(new Label(14, 0, "县/区", titleFmt));
            sheet.addCell(new Label(15, 0, "购买量", titleFmt));
            sheet.addCell(new Label(16, 0, "规格", titleFmt));
            sheet.addCell(new Label(17, 0, "供货价", titleFmt));
            sheet.addCell(new Label(18, 0, "单价", titleFmt));
            sheet.addCell(new Label(19, 0, "商品金额", titleFmt));
            sheet.addCell(new Label(20, 0, "扣点%", titleFmt));
            sheet.addCell(new Label(21, 0, "扣点金额", titleFmt)); //（商品金额*扣点）
            sheet.addCell(new Label(22, 0, "结算金额", titleFmt));
            sheet.addCell(new Label(23, 0, "商品名称", titleFmt));
            sheet.addCell(new Label(24, 0, "商品货号", titleFmt));
            sheet.addCell(new Label(25, 0, "商品属性", titleFmt)); //（是：跨境 否：一般商品）
            sheet.addCell(new Label(26, 0, "结算状态", titleFmt));
            sheet.addCell(new Label(27, 0, "结算时间", titleFmt));
            sheet.addCell(new Label(28, 0, "订单留言", titleFmt));
            sheet.addCell(new Label(29, 0, "下单时间", titleFmt));
            sheet.addCell(new Label(30, 0, "支付时间", titleFmt));
            sheet.addCell(new Label(31, 0, "确认收货时间", titleFmt));
            sheet.addCell(new Label(32, 0, "配货时间", titleFmt));
            sheet.addCell(new Label(33, 0, "支付方式", titleFmt));
			sheet.addCell(new Label(34, 0, "所属企业名称", titleFmt));
			sheet.addCell(new Label(35, 0, "父订单号", titleFmt));
            sheet.addCell(new Label(34, 0, "来源", titleFmt));
            sheet.addCell(new Label(35, 0, "优惠券面额", titleFmt));
            if(settleExcel){
                sheet.addCell(new Label(36, 0, "是否结算", titleFmt));
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (orders.hasContent()) {
                int row = 1;
                for (TernaryTuple<ShoppingOrder, OrderShipmentLog, OrderPayLog> tuple : orders) {
                    ShoppingOrder order = tuple.getValue1();
                    OrderPayLog log = tuple.getValue3();
                    List<OrderEntry> entries = allEntries.get(order);
                    if(settleExcel && order.isSettle()){
                        continue; //如果已经结算的订单 则直接continue
                    }
                    for (OrderEntry entry : entries) {
                        Product product = null;
                        StorageUnit storageUnit = allStorageUnits.get(entry.getStorageUnitId());
                        if (storageUnit != null) {
                            product = allProducts.get(storageUnit.getProductId());
                        }
                        sheet.addCell(new Label(0, row, order.getKey()));
                        if(product != null && product.getOwnerId() != null){
                            //根据产品数据 查找供应商信息
                            if(admin.isProvider()){
                                WebsiteAdministrator administrator =   websiteAdministratorService.findOne(product.getOwnerId());
                                if(product.getOwnerType().equals(OwnerType.provider) && administrator != null && admin.getWorkerName().equals(administrator.getWorkerName())){
                                    sheet.addCell(new Label(1, row, administrator.getWorkerName())); //供应商名字
                                }else{
                                    sheet.removeRow(row);
                                    continue;
                                }
                            }else{
                                WebsiteAdministrator administrator =   websiteAdministratorService.findOne(product.getOwnerId());
                                if(product.getOwnerType().equals(OwnerType.provider) && administrator != null){
                                    sheet.addCell(new Label(1, row, administrator.getWorkerName())); //供应商名字
                                }else{
                                    sheet.addCell(new Label(1, row, "")); //供应商名字
                                }
                            }
                        }
                        sheet.addCell(new Label(2, row, order.getStatus().getDescription()));
                        sheet.addCell(new jxl.write.Number(3, row, order.getShipmentFee(), priceFmt)); //运费
                        sheet.addCell(new jxl.write.Number(4, row, order.getTotalCost(), priceFmt)); //订单总价
                        sheet.addCell(new jxl.write.Number(5, row, order.getNeedPay(), priceFmt)); //实付金额
                        Double refundAmount = 0.0;
                        if(orderProductRefundAmountMap != null && orderProductRefundAmountMap.containsKey(order.getId())){
                            Map<Integer, Double> productRefundAmountMap = orderProductRefundAmountMap.get(order.getId());
                            if(productRefundAmountMap.get((int)entry.getSourceObjectId()) != null){
                            	refundAmount = productRefundAmountMap.get((int)entry.getSourceObjectId());
                            }
                        }
                        sheet.addCell(new jxl.write.Number(6, row, refundAmount, priceFmt)); //退款金额
                        OrderShipmentLog osl = tuple.getValue2();
                        if (osl != null) {
                            sheet.addCell(new Label(7, row, osl.getShipmentCompany().getName()));//物流公司
                            sheet.addCell(new Label(8, row, osl.getShipmentOrder())); //物流单号
                        }
                        sheet.addCell(new Label(9, row, order.getReceiverName())); //收件人
                        if (order.getReceiverMobile() == null || order.getReceiverMobile().isEmpty()) {
                            sheet.addCell(new Label(10, row, order.getReceiverPhone()));
                        } else if (order.getReceiverPhone() == null || order.getReceiverPhone().isEmpty()) {
                            sheet.addCell(new Label(10, row, order.getReceiverMobile()));
                        } else {
                            sheet.addCell(new Label(10, row, new StringBuilder(order.getReceiverMobile()).append('/').append(order.getReceiverPhone()).toString()));
                        }
                        //收货地址（包含省、市、区）
                        String address = order.getProvince().getName() + order.getCity().getName() + order.getCounty().getName();
                        if (order.getTown() != null) {
                            sheet.addCell(new Label(11, row, address + order.getTown().getName() + order.getReceiverAddr())); //收货地址
                        } else {
                            sheet.addCell(new Label(11, row, address + order.getReceiverAddr()));
                        }
                        sheet.addCell(new Label(12, row, order.getProvince().getName())); //省份
                        sheet.addCell(new Label(13, row, order.getCity().getName())); //市
                        if (order.getTown() != null) {
                            sheet.addCell(new Label(14, row, order.getCounty().getName() + order.getTown().getName(), titleFmt)); //区县
                        } else {
                            sheet.addCell(new Label(14, row, order.getCounty().getName())); //区县
                        }
                        sheet.addCell(new jxl.write.Number(15, row, entry.getAmount(), intFmt)); //够买数量

                        sheet.addCell(new jxl.write.Label(16, row, product.getProductCode())); //规格
                    	sheet.addCell(new jxl.write.Number(17, row, product.getProvider_GHPrice(), priceFmt)); //供货价

                    	sheet.addCell(new jxl.write.Number(18, row, entry.getSoldPrice(), priceFmt)); //单价
                        if(product != null) {
                            //单价*数量 = 商品金额
                            Double productMoney = entry.getSoldPrice()* entry.getAmount();
                            Double serviceChargeRatio = entry.getSoldServiceChargeRatio()/100;
                            sheet.addCell(new jxl.write.Number(19, row, productMoney, priceFmt)); //扣点
                            sheet.addCell(new jxl.write.Number(20, row, serviceChargeRatio, priceFmt)); //扣点
                            //扣点金额 = 商品金额* 扣点
                            sheet.addCell(new jxl.write.Number(21, row, productMoney * serviceChargeRatio, priceFmt));

                            //结算金额  单价-单价*扣点%
                            Double price = entry.getSoldPrice() * serviceChargeRatio;
                            Double settlement = entry.getSoldPrice() - price;
                            sheet.addCell(new jxl.write.Number(22, row, settlement, priceFmt));
                            sheet.addCell(new Label(23, row, product.getName()));  //商品名称
                            sheet.addCell(new jxl.write.Number(24, row, product.getId(), intFmt)); //商品ID
                            if(product.isEnableOverseas()){ //商品属性
                               sheet.addCell(new Label(25, row, "跨境"));
                            }else{
                               sheet.addCell(new Label(25, row, "一般商品"));
                            }
                        }
                        if(order.isSettle() && !settleExcel){
                            sheet.addCell(new Label(26, row, "已结算"));
                            sheet.addCell(new Label(27, row, sdf.format(order.getSettleCreateTime())));//结算时间
                        }else{
                            sheet.addCell(new Label(26, row, "未结算"));
                            sheet.addCell(new Label(27, row, "")); //结算时间
                        }
                        sheet.addCell(new Label(28, row, order.getRemark())); //订单留言
                        sheet.addCell(new Label(29, row, sdf.format(order.getCreateTime()))); //下单时间

                        //判断订单状态  如果是未付款状态 不显示支付时间
                        if (log == null) {
                            sheet.addCell(new Label(30, row, ""));
                        } else {
                            sheet.addCell(new Label(30, row, sdf.format(log.getCreateTime()))); //支付时间
                        }
                        if (osl != null) {
                            if (osl.getConfirmTime() != null) {
                                sheet.addCell(new Label(31, row, sdf.format(osl.getConfirmTime()))); //确认收货时间
                            }
                        } else {
                            sheet.addCell(new Label(31, row, "")); //确认收货时间
                        }
                        sheet.addCell(new Label(32, row, order.getDeliveryDateType().getDescription())); //配送时间
                        //判断订单状态  如果是未付款状态 不显示支付方式
                        if (log == null) {
                            sheet.addCell(new Label(33, row, ""));
                        } else {
                            sheet.addCell(new Label(33, row, log.getPayType().getDescription()));//支付方式
                        }
                        String enterpriseName = "";
                        Enterprise enterprise = enterpriseMap.get(order.getEnterpriseId());
                        if (enterprise != null) {
                            enterpriseName = enterprise.getName();
                        }
                        sheet.addCell(new Label(34, row,enterpriseName));//所属企业名称

                        //父订单号
                        if(order.isSplit() == true && order.getOriginalOrderId() != null){
                            for (ShoppingOrder originalShoppingOrder : originalShoppingOrders) {
                                if(order.getOriginalOrderId().equals(originalShoppingOrder.getId())){
                                    sheet.addCell(new Label(35, row, originalShoppingOrder.getKey()));
                                }
                            }
                        }
                        if(order.getIsFenxiao()){
                        	sheet.addCell(new Label(34, row, "分销"));
                        }else{
                        	sheet.addCell(new Label(34, row, "普通"));
                        }
                        sheet.addCell(new jxl.write.Number(35, row, order.getCouponMoney()));
                        //如果为true 则新增一列是否结算的值
                        if(settleExcel){
                            sheet.addCell(new Label(36, row, ""));
                        }

                        ++row;
                    }
                }
            }
            workbook.write();
            workbook.close();
        }
    }
    @RequiresPermissions("order:export")
    @RequestMapping(value = "/admin/order/system/exportExcel.php", method = RequestMethod.POST)
    public @ResponseBody void systemExportExcel(@RequestParam(value = "kw", required = false) String keyword,
                                                @RequestParam(value = "status", required = false) OrderStatus status,
                                                @RequestParam(value = "payType", required = false) OrderPayType payType,
                                                @RequestParam("timeType") String timeType,
                                                @RequestParam("st") Date startDate,
                                                @RequestParam("et") Date endDate,
                                                @RequestParam(value = "ownerType", defaultValue = "system") String type,
                                                @RequestParam(value = "settle") Boolean settle, //是否已结算
                                                @RequestParam(value="settleExcel", defaultValue = "false") Boolean settleExcel, //是否导出结算表（ture 会新增一列值）
                                                @RequestParam(value = "enterpriseId", required = false) Integer enterpriseId,//企业ID
                                                @RequestParam(value = "isEnterprise", required = false) Boolean isEnterprise,//是否为企业专享订单
                                                WebsiteAdministrator admin,
                                                Website website, HttpServletResponse response) throws UnsupportedEncodingException, IOException, WriteException {
        if (website.getRatio() == null || website.getRatio() <= 0) {
            throw new RuntimeException();
        }
        if (endDate != null) {
            Calendar cal = Calendar.getInstance(tz);
            cal.setTime(endDate);
            cal.add(Calendar.HOUR, 23);
            cal.add(Calendar.MINUTE, 59);
            cal.add(Calendar.SECOND, 59);
            cal.add(Calendar.MILLISECOND, 999);
            endDate = cal.getTime();
        }
        Page<TernaryTuple<ShoppingOrder, OrderShipmentLog, OrderPayLog>> orders;
        PagerSpec pager = new PagerSpec(1, 0, 999999999, new PagerSpec.Order[]{new PagerSpec.Order("time", PagerSpec.DRECTION.desc)});
        OrderCriteria criteria = new OrderCriteria().withKeyword(keyword).withStatus(status).withSystemDeprecated(false).withSettle(settle).withEnterpriseId(enterpriseId).withEnterprise(isEnterprise);
        if(settleExcel){ //结算订单
        	criteria.withNiffer(false);
        }
        switch (timeType) {
            case "createTime":
                criteria.withCreateTime(startDate, endDate);
                break;
            case "shipmentTime":
                criteria.withShipmentTime(startDate, endDate);
                break;
            case "confirmTime":
                criteria.withConfirmTime(startDate, endDate);
                break;
            default:
                throw new RuntimeException("unkown time type");
        }
        if (admin.isProvider()) {
            orders = orderService.findOrderWithShipmentLogAndPayLog(OwnerType.provider, admin.getId(), criteria, pager);
        } else {
            OwnerType[] ownerTypes;
            switch (type) {
                case "jingdong":
                    ownerTypes = new OwnerType[]{OwnerType.jingdong};
                    break;
                case "system":
                    ownerTypes = new OwnerType[]{OwnerType.system};
                    break;
                default:
                    ownerTypes = new OwnerType[]{OwnerType.jingdong, OwnerType.system};
            }
            criteria.withOwnerType(ownerTypes);
            orders = orderService.findOrderWithShipmentLogAndPayLog(criteria, pager);
        }
        //准备导出Excel所需要的数据
        Map<ShoppingOrder, List<OrderEntry>> allEntries = null;
        Map<Integer, StorageUnit> allStorageUnits = null;
        Map<Integer, Product> allProducts = null;
        Map<Long, Map<Integer, Double>> orderProductRefundAmountMap = null;
        Map<Integer, Enterprise> enterpriseMap = null;
        List<ShoppingOrder> originalShoppingOrders = new ArrayList<>();
        if (orders.hasContent()) {
            List<ShoppingOrder> allOrders = new ArrayList<>();
            List<Long> orderIdList = new ArrayList<>();
            Set<Long> originalOrderIdList = new HashSet<>();
            for (TernaryTuple<ShoppingOrder, OrderShipmentLog, OrderPayLog> tuple : orders) {
                allOrders.add(tuple.getValue1());
                orderIdList.add(tuple.getValue1().getId());
            }
             if(!orderIdList.isEmpty()){
                orderProductRefundAmountMap = nifferOrderService.findRefundAmountByOrderIds(orderIdList);
            }
            allEntries = orderService.findEntries(allOrders);

            Set<Integer> storageUnitIds = new HashSet<>();
            for (List<OrderEntry> le : allEntries.values()) {
                for (OrderEntry oe : le) {
                    storageUnitIds.add(oe.getStorageUnitId());
                }
            }
            allStorageUnits = storageUnitService.find(storageUnitIds);

            Set<Integer> productIds = new HashSet<>();
            for (StorageUnit sku : allStorageUnits.values()) {
                productIds.add(sku.getProductId());
            }
            allProducts = productService.findAsMap(productIds);
            enterpriseMap = enterpriseService.map();
            //把原单号ID提取出来  然后根据原单ID集合查出原始订单
            for (ShoppingOrder shoppingOrder : allOrders) {
                if(shoppingOrder.isSplit() == true){
                    originalOrderIdList.add(shoppingOrder.getOriginalOrderId());
                }
            }
            if(originalOrderIdList.size()>0){
                originalShoppingOrders = orderService.find(originalOrderIdList);
            }
        }

        response.setContentType("application/force-download");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.addHeader("Content-Type", "application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", new StringBuilder("attachment;filename=").append(URLEncoder.encode("订单导出", "UTF-8")).append(".xls").toString());

        //资料准备齐开始写Excel了
        try (OutputStream out = response.getOutputStream()) {
            WritableWorkbook workbook = Workbook.createWorkbook(out);
            WritableSheet sheet = workbook.createSheet("导出订单列表", 0);
            //设置列宽
            sheet.setColumnView(0, 15);//订单号
            sheet.setColumnView(1, 20);//供应商
            sheet.setColumnView(2, 12);//订单状态
            sheet.setColumnView(3, 12);//卖家备注
            sheet.setColumnView(4, 10);//订单总价
            sheet.setColumnView(5, 10);//实付金额
            sheet.setColumnView(6, 22);//退款金额
            sheet.setColumnView(7, 20);//物流企业（*注）
            sheet.setColumnView(8, 10);//物流单号
            sheet.setColumnView(9, 17);//收货人
            sheet.setColumnView(10, 20);//收货人电话
            sheet.setColumnView(11, 50);//收货地址（包含省、市、区）
            sheet.setColumnView(12, 10);//省
            sheet.setColumnView(13, 10);//市
            sheet.setColumnView(14, 10);//县/区
            sheet.setColumnView(15, 10);//购买量
            sheet.setColumnView(16, 10);//规格
            sheet.setColumnView(17, 18);//供货价
            sheet.setColumnView(18, 18);//单价
            sheet.setColumnView(19, 18);//商品金额（购买量*单价）
            sheet.setColumnView(20, 18);//扣点%
            sheet.setColumnView(21, 20);//扣点金额（商品金额*扣点）
            sheet.setColumnView(22, 18);//结算金额
            sheet.setColumnView(23, 22);//商品名称
            sheet.setColumnView(24, 18);//商品货号
            sheet.setColumnView(25, 20);//商品属性（是：跨境 否：一般商品）
            sheet.setColumnView(26, 10);//结算状态
            sheet.setColumnView(27, 20);//结算时间
            sheet.setColumnView(28, 20);//订单留言
            sheet.setColumnView(29, 15);//下单时间
            sheet.setColumnView(30, 15);//支付时间
            sheet.setColumnView(31, 15);//确认收货时间
            sheet.setColumnView(32, 15);//配货时间
            sheet.setColumnView(33, 15);//支付方式
			sheet.setColumnView(34, 10);//所属企业名称
			sheet.setColumnView(35, 10);//父订单号
            if(settleExcel){ //为true 则新增一列
                sheet.setColumnView(36, 18);//是否结算
            }

            sheet.getSettings().setVerticalFreeze(1);
            sheet.getSettings().setHorizontalFreeze(2);
            sheet.getSettings().setProtected(true);

            //初始化标题
            WritableFont titleWft = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
            titleWft.setColour(Colour.BROWN);
            WritableCellFormat titleFmt = new WritableCellFormat(titleWft);
            titleFmt.setBackground(Colour.GRAY_25);

            WritableCellFormat priceFmt = new WritableCellFormat(new NumberFormat("#0.00"));
            WritableCellFormat intFmt = new WritableCellFormat(new NumberFormat("#"));
            WritableCellFormat timeFmt = new WritableCellFormat(new DateFormat("yyyy-MM-dd hh:mm:ss"));

            sheet.addCell(new Label(0, 0, "订单号", titleFmt));
            sheet.addCell(new Label(1, 0, "供应商", titleFmt));
            sheet.addCell(new Label(2, 0, "订单状态", titleFmt));
            sheet.addCell(new Label(3, 0, "运费", titleFmt));
            sheet.addCell(new Label(4, 0, "订单总价", titleFmt));
            sheet.addCell(new Label(5, 0, "实付金额", titleFmt));
            sheet.addCell(new Label(6, 0, "退款金额", titleFmt));
            sheet.addCell(new Label(7, 0, "物流企业（*注）", titleFmt));
            sheet.addCell(new Label(8, 0, "物流单号", titleFmt));
            sheet.addCell(new Label(9, 0, "收货人", titleFmt));
            sheet.addCell(new Label(10, 0, "收货人电话", titleFmt));
            sheet.addCell(new Label(11, 0, "收货地址（包含省、市、区）", titleFmt));
            sheet.addCell(new Label(12, 0, "省", titleFmt));
            sheet.addCell(new Label(13, 0, "市", titleFmt));
            sheet.addCell(new Label(14, 0, "县/区", titleFmt));
            sheet.addCell(new Label(15, 0, "购买量", titleFmt));
            sheet.addCell(new Label(16, 0, "规格", titleFmt));
            sheet.addCell(new Label(17, 0, "供货价", titleFmt));
            sheet.addCell(new Label(18, 0, "单价", titleFmt));
            sheet.addCell(new Label(19, 0, "商品金额", titleFmt));
            sheet.addCell(new Label(20, 0, "扣点%", titleFmt));
            sheet.addCell(new Label(21, 0, "扣点金额", titleFmt)); //（商品金额*扣点）
            sheet.addCell(new Label(22, 0, "结算金额", titleFmt));
            sheet.addCell(new Label(23, 0, "商品名称", titleFmt));
            sheet.addCell(new Label(24, 0, "商品货号", titleFmt));
            sheet.addCell(new Label(25, 0, "商品属性", titleFmt)); //（是：跨境 否：一般商品）
            sheet.addCell(new Label(26, 0, "结算状态", titleFmt));
            sheet.addCell(new Label(27, 0, "结算时间", titleFmt));
            sheet.addCell(new Label(28, 0, "订单留言", titleFmt));
            sheet.addCell(new Label(29, 0, "下单时间", titleFmt));
            sheet.addCell(new Label(30, 0, "支付时间", titleFmt));
            sheet.addCell(new Label(31, 0, "确认收货时间", titleFmt));
            sheet.addCell(new Label(32, 0, "配货时间", titleFmt));
            sheet.addCell(new Label(33, 0, "支付方式", titleFmt));
			sheet.addCell(new Label(34, 0, "所属企业名称", titleFmt));
			sheet.addCell(new Label(35, 0, "父订单号", titleFmt));
            if(settleExcel){
                sheet.addCell(new Label(36, 0, "是否结算", titleFmt));
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            if (orders.hasContent()) {
                int row = 1;
                for (TernaryTuple<ShoppingOrder, OrderShipmentLog, OrderPayLog> tuple : orders) {
                    ShoppingOrder order = tuple.getValue1();
                    OrderPayLog log = tuple.getValue3();
                    List<OrderEntry> entries = allEntries.get(order);
//                    if(settleExcel && order.isSettle()&& !order.isSplit() ){
                    if(settleExcel && order.isSettle()){
                        continue; //如果已经结算的订单 则直接continue
                    }
                    for (OrderEntry entry : entries) {
                        StorageUnit storageUnit = allStorageUnits.get(entry.getStorageUnitId());
                        Product product = null;
                        if (storageUnit != null) {
                            product = allProducts.get(storageUnit.getProductId());
                        }
                        sheet.addCell(new Label(0, row, order.getKey())); //订单号
                        //判断如果是系统产品和供应商产品 则供货商默认为 深圳市云中鹤科技股份有限公司
                        if (order.getDeliveryObjectType().equals(OwnerType.system) || order.getDeliveryObjectType().equals(OwnerType.provider)) {
                            sheet.addCell(new Label(1, row, "深圳市云中鹤科技股份有限公司")); //供货商
                        } else if (order.getDeliveryObjectType().equals(OwnerType.enterprise)) { //自有产品 则供货商默认为  杭州市民卡
                            sheet.addCell(new Label(1, row, "杭州市民卡"));
                        } else {
                            sheet.addCell(new Label(1, row, "京东"));
                        }

                        sheet.addCell(new Label(2, row, order.getStatus().getDescription()));
                        sheet.addCell(new jxl.write.Number(3, row, order.getShipmentFee(), priceFmt)); //运费
                        sheet.addCell(new jxl.write.Number(4, row, order.getTotalCost(), priceFmt)); //订单总价
                        sheet.addCell(new jxl.write.Number(5, row, order.getNeedPay(), priceFmt)); //实付金额
                        Double refundAmount = 0.0;
                        if(orderProductRefundAmountMap != null && orderProductRefundAmountMap.containsKey(order.getId())){
                            Map<Integer, Double> productRefundAmountMap = orderProductRefundAmountMap.get(order.getId());
                            refundAmount = productRefundAmountMap.get((int)entry.getSourceObjectId());
                            if(refundAmount==null){
                                refundAmount = 0.0;
                            }
                        }

                        sheet.addCell(new jxl.write.Number(6, row, refundAmount, priceFmt)); //退款金额


                        OrderShipmentLog osl = tuple.getValue2();
                        if (osl != null) {
                            sheet.addCell(new Label(7, row, osl.getShipmentCompany().getName()));//物流公司
                            sheet.addCell(new Label(8, row, osl.getShipmentOrder())); //物流单号
                        }else{
                            ShipmentOrderDetail shipment = shipmentOrderDetailService.findOne(order);
                            if(shipment != null){
                                String shipmentOrder = "";
                                String shipmentName = "";
                                Set<String> get = shipment.getShipmentInfo().keySet();
                                for (String name:get) {
                                    shipmentName = name;
                                    shipmentOrder = (String)shipment.getShipmentInfo().get(shipmentName);
                                }
                                sheet.addCell(new Label(7, row, shipmentName));//物流公司
                                sheet.addCell(new Label(8, row, shipmentOrder)); //物流单号
                            }else{
                                try {
                                	if(order.getStatus() == OrderStatus.waiting_confirmed || order.getStatus() == OrderStatus.completed
                                			|| order.getStatus() == OrderStatus.after_sale_service || order.getStatus() == OrderStatus.after_sale_completed) {
		                                OrderTrackResult track = systemOrderService.findOrderTrack(website,order);
		                                if(track != null){
//                                            ShipmentOrderDetail shipmentOrderDetail = new ShipmentOrderDetail();
//                                            Map<String, Object> map = new HashMap<String, Object>();
//                                            map.put(track.getShipmentName(), track.getShipmentOrder());
//                                            shipmentOrderDetail.setShipmentInfo(map);
//                                            shipmentOrderDetail.setStatus(ShipmentOrderState.in_transit);
//                                            shipmentOrderDetail.setOrderId(order.getId());
//                                            shipmentOrderDetailService.create(shipmentOrderDetail);
		                                    sheet.addCell(new Label(7, row, track.getShipmentName()));//物流公司
		                                    sheet.addCell(new Label(8, row, track.getShipmentOrder())); //物流单号
		                                }
                                	} else {
                                		sheet.addCell(new Label(7, row, ""));//物流公司
                                        sheet.addCell(new Label(8, row, "")); //物流单号
                                	}
                                } catch (Exception e) {
                                	sheet.addCell(new Label(7, row, ""));//物流公司
                                    sheet.addCell(new Label(8, row, "")); //物流单号
                                }
                            }
                        }
                        sheet.addCell(new Label(9, row, order.getReceiverName())); //收件人
                        if (order.getReceiverMobile() == null || order.getReceiverMobile().isEmpty()) {
                            sheet.addCell(new Label(10, row, order.getReceiverPhone()));
                        } else if (order.getReceiverPhone() == null || order.getReceiverPhone().isEmpty()) {
                            sheet.addCell(new Label(10, row, order.getReceiverMobile()));
                        } else {
                            sheet.addCell(new Label(10, row, new StringBuilder(order.getReceiverMobile()).append('/').append(order.getReceiverPhone()).toString()));
                        }
                        //收货地址（包含省、市、区）
                        String address = order.getProvince().getName() + order.getCity().getName() + order.getCounty().getName();
                        if (order.getTown() != null) {
                            sheet.addCell(new Label(11, row, address + order.getTown().getName() + order.getReceiverAddr())); //收货地址
                        } else {
                            sheet.addCell(new Label(11, row, address + order.getReceiverAddr()));
                        }
                        sheet.addCell(new Label(12, row, order.getProvince().getName())); //省份
                        sheet.addCell(new Label(13, row, order.getCity().getName())); //市
                        if (order.getTown() != null) {
                            sheet.addCell(new Label(14, row, order.getCounty().getName() + order.getTown().getName(), titleFmt)); //区县
                        } else {
                            sheet.addCell(new Label(14, row, order.getCounty().getName())); //区县
                        }
                        sheet.addCell(new jxl.write.Number(15, row, entry.getAmount(), intFmt)); //够买数量

                        sheet.addCell(new jxl.write.Label(16, row, product.getProductCode())); //规格
                    	sheet.addCell(new jxl.write.Number(17, row, product.getProvider_GHPrice(), priceFmt)); //供货价

                    	sheet.addCell(new jxl.write.Number(18, row, entry.getSoldPrice(), priceFmt)); //单价
                        if(product != null) {
                            //单价*数量 = 商品金额
                            Double productMoney = entry.getSoldPrice()* entry.getAmount();
                            Double serviceChargeRatio = entry.getSoldServiceChargeRatio()/100;
                            sheet.addCell(new jxl.write.Number(19, row, productMoney, priceFmt)); //扣点
                            sheet.addCell(new jxl.write.Number(20, row, serviceChargeRatio, priceFmt)); //扣点
                            //扣点金额 = 商品金额* 扣点
                            sheet.addCell(new jxl.write.Number(21, row, productMoney * serviceChargeRatio, priceFmt));

                            //结算金额  单价-单价*扣点%
                            Double price = entry.getSoldPrice() * serviceChargeRatio;
                            Double settlement = entry.getSoldPrice() - price;
                            sheet.addCell(new jxl.write.Number(22, row, settlement, priceFmt));
                            sheet.addCell(new Label(23, row, product.getName()));  //商品名称
                            sheet.addCell(new jxl.write.Number(24, row, product.getId(), intFmt)); //商品ID
                            if(product.isEnableOverseas()){ //商品属性
                               sheet.addCell(new Label(25, row, "跨境"));
                            }else{
                               sheet.addCell(new Label(25, row, "一般商品"));
                            }
                        }
                        if(order.isSettle() && !settleExcel){
                            sheet.addCell(new Label(26, row, "已结算"));
                            sheet.addCell(new Label(27, row, sdf.format(order.getSettleCreateTime())));//结算时间
                        }else{
                            sheet.addCell(new Label(26, row, "未结算"));
                            sheet.addCell(new Label(27, row, "")); //结算时间
                        }
                        sheet.addCell(new Label(28, row, order.getRemark())); //订单留言
                        sheet.addCell(new Label(29, row, sdf.format(order.getCreateTime()))); //下单时间

                        //判断订单状态  如果是未付款状态 不显示支付时间
                        if (log == null) {
                            sheet.addCell(new Label(30, row, ""));
                        } else {
                            sheet.addCell(new Label(30, row, sdf.format(log.getCreateTime()))); //支付时间
                        }
                        if (osl != null) {
                            if (osl.getConfirmTime() != null) {
                                sheet.addCell(new Label(31, row, sdf.format(osl.getConfirmTime()))); //确认收货时间
                            }
                        } else {
                            sheet.addCell(new Label(31, row, "")); //确认收货时间
                        }
                        sheet.addCell(new Label(32, row, order.getDeliveryDateType().getDescription())); //配送时间
                        //判断订单状态  如果是未付款状态 不显示支付方式
                        if (log == null) {
                            sheet.addCell(new Label(33, row, ""));
                        } else {
                            sheet.addCell(new Label(33, row, log.getPayType().getDescription()));//支付方式
                        }
                        String enterpriseName = "";
                        Enterprise enterprise = enterpriseMap.get(order.getEnterpriseId());
                        if (enterprise != null) {
                            enterpriseName = enterprise.getName();
                        }
                        sheet.addCell(new Label(34, row,enterpriseName));//所属企业名称

                        //父订单号
                        if(order.isSplit() == true && order.getOriginalOrderId() != null){
                            for (ShoppingOrder originalShoppingOrder : originalShoppingOrders) {
                                if(order.getOriginalOrderId().equals(originalShoppingOrder.getId())){
                                    sheet.addCell(new Label(35, row, originalShoppingOrder.getKey()));
                                }
                            }
                        }

                        //如果为true 则新增一列是否结算的值
                        if(settleExcel){
                            sheet.addCell(new Label(36, row, ""));
                        }
                        ++row;
                    }
                }
            }
            workbook.write();
            workbook.close();
        }
    }

    //批量发货自有订单
    @RequiresPermissions("order:shipment")
    @RequestMapping(value = "/admin/order/toBatchShipment.php" , method = RequestMethod.GET)
    public String toBatchShipment()
    {
        return "/admin/order/batch/toBatchShipment";
    }

    //批量发货自有订单
    @RequiresPermissions("order:shipment")
    @RequestMapping(value = "/admin/order/toBatchShipment.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse batchShipment(@RequestParam(value = "excelFile") MultipartFile file,
                                                          WebsiteAdministrator admin,
                                                          Website website,
                                                          HttpServletRequest request)
    {
        //获取所有快递公司信息, 做匹配
        List<ShipmentCompany> shipmentCompanyList = shipmentCompanyService.all();
        int successCount = 0;
        try(InputStream ins = file.getInputStream()){
            Workbook workbook = Workbook.getWorkbook(ins);
            int sheetSize = workbook.getNumberOfSheets();
            for(int i = 0; i < sheetSize; ++i){
                Sheet sheet = workbook.getSheet(i);
                int rows = sheet.getRows();
                int columns = sheet.getColumns();
                //循环取出单元格的内容
                for(int r = 1; r < rows; ++r){
                    try{
                        String orderKey = sheet.getCell(0, r).getContents();
                        String shipmentCompany = sheet.getCell(2, r).getContents(); //快递公司
                        String kuaidiHao = sheet.getCell(3, r).getContents();    //快递单号
                        if(shipmentCompany == null || kuaidiHao == null || orderKey == null){
                        	logger.info("订单号码、快递单号、快递公司、数据为空 ：行号{}", r);
                            //数据异常跳过
                            continue;
                        }
                        if(shipmentCompany.trim().isEmpty() || kuaidiHao.trim().isEmpty() || orderKey.trim().isEmpty()){
                            //数据异常跳过
                            continue;
                        }
                        ShipmentCompany sc = matchCompany(shipmentCompanyList, shipmentCompany);
                        if(sc == null){

                        	logger.info("不匹配的订单数据:行号{}, 订单号{}, 快递公司{}, ", r, orderKey, shipmentCompany);
                            //快递公司不匹配
                            continue;
                        }
                        ShoppingOrder order = this.orderService.findOne(orderKey.trim());
                        if(order == null || order.getStatus() != OrderStatus.waiting_shipment){
                        	logger.info("导入的订单非等待发货状态的错误数据:行号{}, 订单号{}, 订单状态{}", r, orderKey, order.getStatus());
                            //订单数据不匹配, 或订单已经发货
                            continue;
                        }
                        orderService.setToShipment(order, admin, website, sc, kuaidiHao, request.getRemoteAddr());
                        successCount++;
                    }catch (Exception e) {
                        logger.error("\n com.lmf.integral.admin.order.controller.OrderController.batchShipment: " + e.getMessage());
                    }
                }
            }
        }catch (Exception e) {
            logger.error("com.lmf.integral.admin.order.controller.OrderController.batchShipment" + e);
            return new SimpleJsonResponse<>(false, "您上传的文件不是合法的Excel2003~2007格式，请检查后缀名是否为.xls");
        }
        return new SimpleJsonResponse<>(true, successCount);
    }

    //根据快递公司名称或代码匹配
    private ShipmentCompany matchCompany(List<ShipmentCompany> shipmentCompanyList, String name)
    {
        if(name == null || name.trim().isEmpty()){
            return null;
        }
        for(ShipmentCompany sc : shipmentCompanyList)
        {
            String scName = sc.getName().trim();
            String scCode = sc.getKuaidi100Code();
            if(name.equals(scName) || name.equals(scCode))
            {
                return sc;
            }
        }
        return null;
    }

    //订单结算
    @RequestMapping(value = "/admin/order/toOrderSettleLog.php", method = RequestMethod.GET)
    public String toOrderSettleLog(Model model)
    {
        Calendar cal = Calendar.getInstance(tz);
        Date endDate = cal.getTime();
        cal.add(Calendar.MONTH, -2);
        Date startDate = cal.getTime();

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        //查询所有供应商信息
        model.addAttribute("providers", websiteAdministratorService.find(new WebsiteAdministratorCriteria().withProvider(true).withEnabled(true), null));
        List<Enterprise> enterpriseList = enterpriseService.list(null);
        model.addAttribute("enterpriseList",enterpriseList);
        return "/admin/order/batch/toOrderSettleLog";
    }

    //批量导入订单结算
    @RequestMapping(value = "/admin/order/toOrderSettleLog.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse toOrderSettleLog(@RequestParam(value = "excelFile") MultipartFile file,
                                                        @RequestParam(value = "ownerType", defaultValue = "provider") OwnerType type,
                                                        WebsiteAdministrator admin,
                                                        Website website,
                                                        HttpServletRequest request)
    {
        int successCount = 0;
        try(InputStream ins = file.getInputStream()){
            Workbook workbook = Workbook.getWorkbook(ins);
            int sheetSize = workbook.getNumberOfSheets();
            for(int i = 0; i < sheetSize; ++i){
                Sheet sheet = workbook.getSheet(i);
                int rows = sheet.getRows();
                int columns = sheet.getColumns();
                if(columns != 35){
                    continue;
                }
                //循环出上传的单元格的内容
                OrderSettlementLog log = null;
                for(int r = 1; r < rows; ++r){
                    try{
                        log = new OrderSettlementLog();

                        String orderKey = sheet.getCell(0, r).getContents();//订单编号
                        //查询订单数据
                        ShoppingOrder order = orderService.findOne(orderKey.trim());
                        //判断订单状态  如果不是已完成的订单  直接跳出
                        if(order != null){
                            if(order.getStatus().equals(OrderStatus.completed) || order.getStatus().equals(OrderStatus.waiting_audit)
                                || order.getStatus().equals(OrderStatus.waiting_shipment) || order.getStatus().equals(OrderStatus.waiting_confirmed)){
                                if(!order.isSettle() && type.equals(order.getDeliveryObjectType())){
                                    log.setOrderId(order.getId()); //存储订单ID
                                }else{
                                    continue;
                                }
                            }else{
                                continue;
                            }
                        }else{
                            continue; //为空 订单数据不存在
                        }
                        Double settlement = getDoubleCellValue(sheet.getCell(20, r)); //结算金额
                        if(settlement > 0){
                            log.setSettleMoney(settlement);
                        }else{
                            continue;
                        }
                        //是否结算
                        if(getBooleanCellValue(sheet.getCell(34, r))){
                            log.setSettle(true); //结算
                        }else{
                            continue;
                        }
                        log.setOwnerType(type); //所属者类型
                        log.setOwnerId(order.getDeliveryObjectId());
                        orderSettlementLogService.save(log, admin, request.getRemoteAddr());
                        successCount++;
                    }catch(Throwable t){
                        if (logger.isWarnEnabled()) {
                            logger.warn("Batch update toOrderSettleLog skiped row, toOrderSettleLog exception", t);
                        }
                    }
                }
            }
            workbook.close();
        } catch(Throwable t){
            if (logger.isWarnEnabled()) {
                logger.warn(null, t);
            }
            return new SimpleJsonResponse<>(false, "您上传的文件不是合法的Excel2003~2007格式，请检查后缀名是否为.xls");
        }

        return new SimpleJsonResponse<>(true, successCount);
    }

    private static double getDoubleCellValue(Cell cell)
    {
        String  content = cell.getContents().trim();
        Double  tmp     = StringsUtil.toDouble(content);
        if (tmp == null)
        {
            return 0;
        }
        return tmp;
    }

    private static boolean getBooleanCellValue(Cell cell)
    {
        String  content = cell.getContents().trim();
        if(content.equalsIgnoreCase("Y") || content.equals("是") || content.equalsIgnoreCase("Yes"))
        {
            return  true;
        }
        return false;
    }

}
