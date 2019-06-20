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
import com.lmf.common.exceptions.PermissionDeniedException;
import com.lmf.common.util.CommonUtil;
import com.lmf.common.util.PagerSpec;
import com.lmf.common.util.ValidUtil;
import com.lmf.enums.ProductStatus;
import com.lmf.market.entity.FenxiaoOrder;
import com.lmf.market.service.CouponService;
import com.lmf.market.service.FenXiaoOrderService;
import com.lmf.order.entity.*;
import com.lmf.order.enums.BankType;
import com.lmf.order.enums.NifferOrderEntryType;
import com.lmf.order.enums.NifferOrderStatus;
import com.lmf.order.enums.OrderStatus;
import com.lmf.order.service.NifferOrderService;
import com.lmf.order.service.OrderService;
import com.lmf.order.service.PayFailOrderLogService;
import com.lmf.order.vo.NifferOrderCriteria;
import com.lmf.product.entity.Brand;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.ProductCate;
import com.lmf.product.entity.StorageUnit;
import com.lmf.product.service.BrandService;
import com.lmf.product.service.ProductCateService;
import com.lmf.product.service.ProductService;
import com.lmf.product.service.StorageUnitService;
import com.lmf.product.vo.ProductCriteria;
import com.lmf.sys.entity.GeoRegion;
import com.lmf.sys.entity.ShipmentCompany;
import com.lmf.sys.enums.RegionType;
import com.lmf.sys.service.GeoRegionService;
import com.lmf.sys.service.ShipmentCompanyService;
import com.lmf.system.sdk.enums.AfterSaleType;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteAdministrator;
import com.lmf.website.service.WebsiteAdministratorService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author shenzhixiong
 */
@Controller
public class OrderNifferController {
    
    @Autowired
    private GeoRegionService    geoRegionService;
    
    @Autowired
    private NifferOrderService  nifferOrderService;
    
    @Autowired
    private OrderService    orderService;

    @Autowired
    private StorageUnitService  storageUnitService;
    
    @Autowired
    private ShipmentCompanyService  shipmentCompanyService;
    
    @Autowired
    private ProductService  productService;
    
    @Autowired
    private BrandService brandService;
    
    @Autowired
    private ProductCateService productCateService; 
    
    @Autowired
    private WebsiteAdministratorService websiteAdministratorService;
    
    @Autowired
    private CouponService couponService;
    
    @Autowired
    private FenXiaoOrderService fenXiaoOrderService;
    
    @Autowired
    private PayFailOrderLogService payFailOrderLogService;
    
    private final Logger LOGGER = LoggerFactory.getLogger(OrderNifferController.class);
    
    @RequiresPermissions(value={"order_after_sale:view","system_order_after_sale:view"},logical= Logical.OR)
    @RequestMapping(value = "/admin/order/niffers.php", method = RequestMethod.GET)
    public String niffer(@RequestParam(value = "kw", required = false) String keyword,
                        @RequestParam(value = "shipmentId", required = false) Integer shipmentId,
                        @RequestParam(value = "nifferStatus", required = false) NifferOrderStatus status,
                        @RequestParam(value = "type", required = false) AfterSaleType afterSaleType,
                        @RequestParam(value = "ownerType", required = false) OwnerType ownerType,
                        @RequestParam(value = "isSystem", required = false) boolean isSystem,
                        @RequestParam(value = "st", required = false) Date startTime,
                        @RequestParam(value = "et", required = false) Date endTime,
                        @PagerSpecDefaults(pageSize = 20, sort = "applyTime.desc") PagerSpec pager,
                        WebsiteAdministrator admin,
                        Website website, Model model) throws ParseException
    {
        StringBuilder link = new StringBuilder("/jdvop/admin/order/niffers.php?page=[:page]");
        if(keyword != null && !keyword.isEmpty()){
            link.append("&kw=").append(keyword);
        }
        if(shipmentId != null && shipmentId > 0){
            link.append("&shipmentId=").append(shipmentId);
        } 
        if(afterSaleType != null){
            link.append("&type=").append(afterSaleType.name());
        } 
         if(ownerType != null){
            link.append("&ownerType=").append(ownerType.name());
        } 
        link.append("&isSystem=").append(isSystem);
        if(status != null){
            link.append("&nifferStatus=").append(status.name());
            model.addAttribute("nifferStatus", status.name());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(startTime != null){
            link.append("&st=").append(sdf.format(startTime));
            model.addAttribute("st", sdf.format(startTime));
        }
        if(endTime != null){
            Calendar date = Calendar.getInstance();
            date.setTime(endTime);
            date = CommonUtil.filled(date);
            endTime = sdf.parse(sdf.format(date.getTime()));
            model.addAttribute("et", sdf.format(endTime));
            link.append("&et=").append(sdf.format(date.getTime()));
        }
        Page<NifferOrder> nifferOrders;
        if (admin.isProvider()) {
            nifferOrders = nifferOrderService.findByprovider(new NifferOrderCriteria().withKeyword(keyword).withShipmentCompanyId(shipmentId).withStatus(status).withApplyTime(new Range<>(startTime, endTime)).withIsSystem(isSystem).withAfterSaleType(afterSaleType).withOwnerType(ownerType), pager,admin.getId());
        } else {
             nifferOrders = nifferOrderService.find(new NifferOrderCriteria().withKeyword(keyword).withShipmentCompanyId(shipmentId).withStatus(status).withApplyTime(new Range<>(startTime, endTime)).withIsSystem(isSystem).withAfterSaleType(afterSaleType).withOwnerType(ownerType), pager);
        }
        Map<Long, Boolean> refundDataMap = new HashMap<>();
        if (nifferOrders.hasContent()) {
            for (NifferOrder item : nifferOrders) {
                if (nifferOrderService.findEntries(item).isEmpty()) {
                    //没有任何退出换入商品
                    refundDataMap.put(item.getId(), false);
                } else {
                    refundDataMap.put(item.getId(), true);
                }
            }
            model.addAttribute("refundDataMap", refundDataMap);
        }
        model.addAttribute("ownerType", ownerType);
        model.addAttribute("isSystem", isSystem);
        model.addAttribute("nifferOrders", nifferOrders);
        model.addAttribute("asoTypes", AfterSaleType.values());
        model.addAttribute("status", NifferOrderStatus.values());
        model.addAttribute("link", link.toString());
        model.addAttribute("orderService",orderService);
        model.addAttribute("providerMap", websiteAdministratorService.findAllProvider());
        return "admin/order/niffer/list";
    }
    @RequiresPermissions(value={"order_after_sale:audit","system_order_after_sale:audit"},logical= Logical.OR)
    @RequestMapping(value = "/admin/order/niffer/accept.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse acceptReturnOrNifferOreder(@RequestParam("id") long returnOrderId, 
                                                                       WebsiteAdministrator admin, 
                                                                       HttpServletRequest request, 
                                                                       Model model)
    {
        NifferOrder rno   = nifferOrderService.findOne(returnOrderId);
        if (rno.getStatus() != NifferOrderStatus.waiting_accept)
        {
            throw new PermissionDeniedException();
        }
        
        nifferOrderService.accept(rno, admin, request.getRemoteAddr());
        
        return new SimpleJsonResponse(true, "操作成功");
    }    
    
    //拒绝请求
    @RequiresPermissions(value={"order_after_sale:audit","system_order_after_sale:audit"},logical= Logical.OR)
    @RequestMapping(value = "/admin/order/niffer/refuse.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse refuseReturnOrNifferOreder(@RequestParam("id") long returnOrderId, 
                                                                      @RequestParam( value = "remark") String remark,   
                                                                      WebsiteAdministrator admin, HttpServletRequest request, Model model)
    {
        NifferOrder rno   = nifferOrderService.findOne(returnOrderId);
        if (rno.getStatus() == NifferOrderStatus.waiting_audit || rno.getStatus() == NifferOrderStatus.waiting_accept)
        {
            if(remark == null || remark.isEmpty()){
                return new SimpleJsonResponse(false, "请填写备注信息");
            }

            nifferOrderService.refuse(rno, admin, remark, request.getRemoteAddr());
            return new SimpleJsonResponse(true, "操作成功");
        }else{
           throw new PermissionDeniedException(); 
        }
    }
    
    @RequiresPermissions(value={"order_after_sale:confirm_refund","system_order_after_sale:confirm_refund"},logical= Logical.OR)
    @RequestMapping(value = "/admin/order/niffer/refund.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse refund(@RequestParam("id") long returnOrderId, 
                                                                       WebsiteAdministrator admin, 
                                                                       HttpServletRequest request, 
                                                                       Model model)
    {
        NifferOrder rno   = nifferOrderService.findOne(returnOrderId);
        try {
            nifferOrderService.doRefund(rno, admin, request.getRemoteAddr());
        } catch (Exception e) {
        	//打印错误日志
        	e.printStackTrace();
            return new SimpleJsonResponse(false, e.getMessage());
        }
        return new SimpleJsonResponse(true, "操作成功");
    }    
    
    @RequiresPermissions(value={"order_after_sale:view","system_order_after_sale:view"},logical= Logical.OR)
    @RequestMapping(value = "/admin/order/niffer.php", method = RequestMethod.GET)
    public  String orderNiffer(@RequestParam("id") long returnOrderId, Website website, Model model)
    {
        NifferOrder rno   = nifferOrderService.findOne(returnOrderId);
        
        ShoppingOrder   order   = orderService.findOne(rno.getOriginalOrderId());
        
        //商品价格
        List<OrderEntry> orderEntryList = orderService.findEntries(order);
        Map<Integer, Double> entryPriceMap = new HashMap<>();
        List<Integer> storageUnitIdList = new ArrayList<>();
        List<Integer> productIdList = new ArrayList<>();
        for(OrderEntry entry:orderEntryList){
            entryPriceMap.put(entry.getStorageUnitId(), entry.getSoldPrice());
            storageUnitIdList.add(entry.getStorageUnitId());
            productIdList.add((int)entry.getSourceObjectId());
        }
        Map<Integer, Product> productMap = productService.findAsMap(productIdList);
        Map<Integer, StorageUnit> storageUnitMap = storageUnitService.find(storageUnitIdList);
        
        //统计退换订单之外的商品价格信息
        List<NifferOrderEntry> entryList = nifferOrderService.findEntries(rno);
        for(NifferOrderEntry item : entryList){
            if(!entryPriceMap.containsKey(item.getStorageUnitId())){
                StorageUnit sku = storageUnitMap.get(item.getStorageUnitId());
                if(sku==null){
                    sku = storageUnitService.findOne(item.getStorageUnitId());
                }
                        
                Product product = productMap.get(sku.getProductId());
                if(product==null){
                    product = productService.findOne(sku.getProductId());
                }
                entryPriceMap.put(item.getStorageUnitId(), product.getRetailPrice());
            }
        }
        
        
        Map<NifferOrderEntryType, List<NifferOrderEntry>> entryMap = nifferOrderService.findEntriesAsMap(rno);
        model.addAttribute("productMap", productMap);
        model.addAttribute("storageUnitMap", storageUnitMap);
        model.addAttribute("nifferOrder", rno);
        model.addAttribute("order", order);
        model.addAttribute("shipmentLog", orderService.findShipmentLog(order));
        if(order.isSplit()){
            ShoppingOrder parentOrder = orderService.findOne(order.getOriginalOrderId());
            model.addAttribute("payLog", orderService.findPayLog(parentOrder));
        }else{
            model.addAttribute("payLog", orderService.findPayLog(order));
        }
        model.addAttribute("shipCompanies", shipmentCompanyService.all());
        model.addAttribute("geoRegionService", geoRegionService);
        model.addAttribute("bankType", BankType.values());
        model.addAttribute("entries", orderEntryList);
        model.addAttribute("inEntries", entryMap.get(NifferOrderEntryType.in));
        model.addAttribute("outEntries", entryMap.get(NifferOrderEntryType.out));
        model.addAttribute("entryPriceMap", entryPriceMap);
        model.addAttribute("storageUnitService", storageUnitService);
        model.addAttribute("productService", productService);
        return "admin/order/niffer/view";
    }
    
    @RequestMapping(value = "/admin/order/niffer/create.php", method = RequestMethod.GET)
    public  String  add(@RequestParam("id") long orderId, Model model)
    {
        ShoppingOrder   order   = orderService.findOne(orderId);
        List<OrderEntry> orderEntryList = orderService.findEntries(order);
         List<Integer> storageUnitIdList = new ArrayList<>();
        List<Integer> productIdList = new ArrayList<>();
        for(OrderEntry entry:orderEntryList){
            storageUnitIdList.add(entry.getStorageUnitId());
            productIdList.add((int)entry.getSourceObjectId());
        }
        
        List<NifferOrder> rno = nifferOrderService.find(order);
        List<Map<String, Object>> historyDataList = new ArrayList<>();
        for (NifferOrder ro : rno)
        {
            if (ro.getStatus() != NifferOrderStatus.accepted  && ro.getStatus() != NifferOrderStatus.declined )
            {
                model.addAttribute("nifferOrder", ro);
                Map<NifferOrderEntryType, List<NifferOrderEntry>> entries = nifferOrderService.findEntriesAsMap(ro);
                model.addAttribute("inEntries", entries.get(NifferOrderEntryType.in));
                model.addAttribute("outEntries", entries.get(NifferOrderEntryType.out));
            }
        }
        Map<Integer, Product> productMap = productService.findAsMap(productIdList);
        Map<Integer, StorageUnit> storageUnitMap = storageUnitService.find(storageUnitIdList);
        model.addAttribute("productMap", productMap);
        model.addAttribute("storageUnitMap", storageUnitMap);
        model.addAttribute("order", order);
        model.addAttribute("historyDataList", historyDataList);
        model.addAttribute("shipmentLog", orderService.findShipmentLog(order));
        model.addAttribute("payLog", orderService.findPayLog(order));
        model.addAttribute("shipCompanies", shipmentCompanyService.all());
        model.addAttribute("geoRegionService", geoRegionService);
        model.addAttribute("bankType", BankType.values());
        model.addAttribute("entries", orderEntryList);
        model.addAttribute("storageUnitService", storageUnitService);
        model.addAttribute("productService", productService);
        
        return "admin/order/niffer/form";
    }
    
    @RequestMapping(value = "/admin/order/niffer/create.php", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public  @ResponseBody SimpleJsonResponse add(@Valid NifferOrder nifferOrder,
                                                 BindingResult bindingResult,
                                                 @RequestParam("region[]") GeoRegion[] region,
                                                 @RequestParam("orderStatus") OrderStatus orderStatus,
                                                 @RequestParam(value = "storageUnitId[]", required = false) Integer[] storageUnitIds, 
                                                 @RequestParam(value = "productId[]", required = false) Integer[] productIds, 
                                                 @RequestParam(value = "entryType[]", required = false) NifferOrderEntryType[] entryTypes,
                                                 @RequestParam(value = "amount[]", required = false) Integer[] amounts, 
                                                 WebsiteAdministrator admin, 
                                                 HttpServletRequest request,
                                                 Model model)
    {
        ShoppingOrder   order   = orderService.findOne(nifferOrder.getOriginalOrderId());
        if(!(order.getDeliveryObjectType().equals(OwnerType.provider) || order.getDeliveryObjectType().equals(OwnerType.enterprise))){
            return new SimpleJsonResponse(false, "系统订单和京东订单不允许进行此操作！");
        }
        FieldError  fe  = ValidUtil.ignoreFieldErrors(bindingResult, new String[]{"shipmentFee"});
        if (fe != null)
        {
            return new SimpleJsonResponse(false, fe.getDefaultMessage());
        }
        
        if(region.length < 3 || (region.length == 4 && region[3] == null)) {
            return new SimpleJsonResponse(false, "请填写客户所在省市区");
        }
        
         if(storageUnitIds == null || storageUnitIds.length < 1) {
            return new SimpleJsonResponse(false, "您没有选择任何退入或者换出的商品！");
        }
        if(storageUnitIds.length != entryTypes.length || storageUnitIds.length != amounts.length || entryTypes.length != amounts.length) {
            return new SimpleJsonResponse(false, "数据错误，请检查！");
        }
        
        NifferOrder clearOrder    = new NifferOrder();
        clearOrder.setConsumerProvince(region[0]);
        clearOrder.setConsumerCity(region[1]);
        clearOrder.setConsumerCounty(region[2]);
        if(region.length == 4) {
            clearOrder.setConsumerTown(region[3]);
        }
        clearOrder.setStatus(NifferOrderStatus.waiting_audit);
        clearOrder.setApplyRemoteAddr(request.getRemoteAddr());
        clearOrder.setAddress(nifferOrder.getAddress());
        clearOrder.setConsumerName(nifferOrder.getConsumerName());
        clearOrder.setConsumerMobile(nifferOrder.getConsumerMobile());
        clearOrder.setConsumerPhone(nifferOrder.getConsumerPhone());
        clearOrder.setRemark(nifferOrder.getRemark());
        clearOrder.setRemarkByWorker(nifferOrder.getRemarkByWorker());
        clearOrder.setShipmentCompany(nifferOrder.getShipmentCompany());
        if (nifferOrder.getShipmentOrder() != null && !nifferOrder.getShipmentOrder().isEmpty())
        {
            clearOrder.setShipmentOrder(nifferOrder.getShipmentOrder());
        }
        clearOrder.setOriginalOrderId(nifferOrder.getOriginalOrderId());
        clearOrder.setIsSystem(false);
        clearOrder.setType(AfterSaleType.niffer);
        List<NifferOrderEntry> nifferOrderEntryList = new ArrayList<>();
        if(storageUnitIds.length > 0){
            for(int i = 0; i < storageUnitIds.length; i ++){
                NifferOrderEntry entry = new NifferOrderEntry();
                entry.setStorageUnitId(storageUnitIds[i]);
                entry.setAmount(amounts[i]);
                entry.setEntryType(entryTypes[i]);
                entry.setProductId(productIds[i]);
                nifferOrderEntryList.add(entry);
            }
        }
        
        if(OrderStatus.completed == orderStatus){
            nifferOrderService.applyNifferForCompletedOrder(order, clearOrder, nifferOrderEntryList, admin, request.getRemoteAddr());
        }else{
            nifferOrderService.applyNiffer(order, clearOrder, nifferOrderEntryList, admin, request.getRemoteAddr());
        }
        return new SimpleJsonResponse(true, "退换货单保存成功!");
    }
    
//    @RequiresPermissions(value={"order_after_sale:audit","system_order_after_sale: audit"},logical= Logical.OR)
    @RequestMapping(value = "/admin/order/niffer/audit.php", method = RequestMethod.GET)
    public  String nifferAudit(@RequestParam("id") long returnOrderId, WebsiteAdministrator admin, Model model)
    {
        NifferOrder rno   = nifferOrderService.findOne(returnOrderId);
        if (rno.getStatus() != NifferOrderStatus.waiting_audit)
        {
            throw new PermissionDeniedException();
        }
        
        ShoppingOrder   order   = orderService.findOne(rno.getOriginalOrderId());
        //商品价格
        List<OrderEntry> orderEntryList = orderService.findEntries(order);
        Map<Integer, Double> entryPriceMap = new HashMap<>();
        List<Integer> storageUnitIdList = new ArrayList<>();
        List<Integer> productIdList = new ArrayList<>();
        for(OrderEntry entry:orderEntryList){
            entryPriceMap.put(entry.getStorageUnitId(), entry.getSoldPrice());
            storageUnitIdList.add(entry.getStorageUnitId());
            productIdList.add((int)entry.getSourceObjectId());
        }
        Map<Integer, Product> productMap = productService.findAsMap(productIdList);
        Map<Integer, StorageUnit> storageUnitMap = storageUnitService.find(storageUnitIdList);
        
        Map<NifferOrderEntryType, List<NifferOrderEntry>> entryMap = nifferOrderService.findEntriesAsMap(rno);
        if(order.isSplit()){
            ShoppingOrder parentOrder = orderService.findOne(order.getOriginalOrderId());
            model.addAttribute("payLog", orderService.findPayLog(parentOrder));
        }else{
            model.addAttribute("payLog", orderService.findPayLog(order));
        }
         model.addAttribute("productMap", productMap);
        model.addAttribute("storageUnitMap", storageUnitMap);
        model.addAttribute("nifferOrder", rno);
        model.addAttribute("order", order);
        model.addAttribute("shipmentLog", orderService.findShipmentLog(order));
        model.addAttribute("shipCompanies", shipmentCompanyService.all());
        model.addAttribute("geoRegionService", geoRegionService);
        model.addAttribute("bankType", BankType.values());
        model.addAttribute("inEntries", entryMap.get(NifferOrderEntryType.in));
        model.addAttribute("outEntries", entryMap.get(NifferOrderEntryType.out));
        model.addAttribute("entries", orderEntryList);
        model.addAttribute("entryPriceMap", entryPriceMap);
        model.addAttribute("storageUnitService", storageUnitService);
        model.addAttribute("productService", productService);
        return "admin/order/niffer/form";
    }
    /**
     * 退款单编辑页面
     * @param returnOrderId
     * @param admin
     * @param model
     * @return 
     */
//    @RequiresPermissions(value={"order_after_sale:edit","system_order_after_sale: edit"},logical= Logical.OR)
    @RequestMapping(value = "/admin/order/refund/audit.php", method = RequestMethod.GET)
    public  String refundAudit(@RequestParam("id") long returnOrderId, WebsiteAdministrator admin, Model model)
    {
        NifferOrder rno   = nifferOrderService.findOne(returnOrderId);
        if (rno.getStatus() != NifferOrderStatus.waiting_audit)
        {
            throw new PermissionDeniedException();
        }
        
        ShoppingOrder   order   = orderService.findOne(rno.getOriginalOrderId());
        //商品价格
        List<OrderEntry> orderEntryList = orderService.findEntries(order);
        Map<Integer, Double> entryPriceMap = new HashMap<>();
        List<Integer> storageUnitIdList = new ArrayList<>();
        List<Integer> productIdList = new ArrayList<>();
        for(OrderEntry entry:orderEntryList){
            entryPriceMap.put(entry.getStorageUnitId(), entry.getSoldPrice());
            storageUnitIdList.add(entry.getStorageUnitId());
            productIdList.add((int)entry.getSourceObjectId());
        }
        Map<Integer, Product> productMap = productService.findAsMap(productIdList);
        Map<Integer, StorageUnit> storageUnitMap = storageUnitService.find(storageUnitIdList);
        
        Map<NifferOrderEntryType, List<NifferOrderEntry>> entryMap = nifferOrderService.findEntriesAsMap(rno);
        
        double shipmentFee = 0.0;
        //如果订单不包邮，则判断以前是否退过运费，如果没有，则克服可以在前端输入所退邮费
        if(order.getShipmentFee()>0){
            List<NifferOrder> nifferOrderList = nifferOrderService.find(order);
            if (nifferOrderList!=null && !nifferOrderList.isEmpty()) {
                if(!nifferOrderList.stream().anyMatch(p->p.getType().equals(AfterSaleType.returned)&&(p.getStatus().equals(NifferOrderStatus.processing_refund)||p.getStatus().equals(NifferOrderStatus.waiting_finance)||p.getStatus().equals(NifferOrderStatus.accepted))&&p.getShipmentFee()>0)){
                    shipmentFee = order.getShipmentFee();
                }
            }
        }
        OrderPayLog log = orderService.findPayLog(order);
        if(log == null){
        	if(order.isSplit()){
                ShoppingOrder parentOrder = orderService.findOne(order.getOriginalOrderId());
                model.addAttribute("payLog", orderService.findPayLog(parentOrder));
            }else{
                model.addAttribute("payLog", orderService.findPayLog(order));
            }
        }else{
        	model.addAttribute("payLog", log);
        }
        
        model.addAttribute("productMap", productMap);
        model.addAttribute("storageUnitMap", storageUnitMap);
        model.addAttribute("nifferOrder", rno);
        model.addAttribute("shipmentFee", shipmentFee);
         model.addAttribute("order", order);
        model.addAttribute("bankType", BankType.values());
        model.addAttribute("inEntries", entryMap.get(NifferOrderEntryType.in));
        model.addAttribute("outEntries", entryMap.get(NifferOrderEntryType.out));
        model.addAttribute("entries", orderEntryList);
        model.addAttribute("entryPriceMap", entryPriceMap);
        model.addAttribute("storageUnitService", storageUnitService);
        model.addAttribute("productService", productService);
        return "admin/order/refund/form";
    }

    //编辑换货单的保存   
//    @RequiresPermissions(value={"order_after_sale:audit","system_order_after_sale: audit"},logical= Logical.OR)
    @RequestMapping(value = "/admin/order/niffer/audit.php", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public  @ResponseBody SimpleJsonResponse nifferAudit(@RequestParam(value = "id") Long id,
                                                  @RequestParam(value = "originalOrderId") Long originalOrderId,
                                                  @RequestParam(value = "orderStatus") OrderStatus status,
                                                  @RequestParam(value = "consumerName", required = false) String consumerName,
                                                  @RequestParam(value = "consumerMobile", required = false) String consumerMobile,
                                                  @RequestParam("region[]") GeoRegion[] region,
                                                  @RequestParam(value = "address", required = false) String address,
                                                  @RequestParam(value = "shipmentCompany", required = false) Integer shipmentCompanyId,
                                                  @RequestParam(value = "shipmentOrder", required = false) String shipmentOrder,
                                                  @RequestParam(value = "remarkByWorker", required = false) String remarkByWorker,
                                                  @RequestParam(value = "remark", required = false) String remark,
                                                  @RequestParam(value = "storageUnitId[]", required = false) Integer[] storageUnitIds, 
                                                   @RequestParam(value = "productId[]", required = false) Integer[] productIds, 
                                                  @RequestParam(value = "entryType[]", required = false) NifferOrderEntryType[] entryTypes,
                                                  @RequestParam(value = "amount[]", required = false) Integer[] amounts, 
                                                  WebsiteAdministrator admin,
                                                  HttpServletRequest request,
                                                  Model model) {
        NifferOrder nifferOrder   = nifferOrderService.findOne(id);
        if (nifferOrder.getStatus() != NifferOrderStatus.waiting_audit)
        {
            return new SimpleJsonResponse(false, "您只能编辑等待审核状态的换货单,当前状态为:" + nifferOrder.getStatus().getDescription());
        }
        ShoppingOrder   order   = orderService.findOne(nifferOrder.getOriginalOrderId());
        if(!nifferOrder.getType().equals(AfterSaleType.niffer)){
            return new SimpleJsonResponse(false, "只有换货售后单才可执行该操作！");
        }
        if(consumerName == null || consumerName.trim().isEmpty()) {
            return new SimpleJsonResponse(false, "请填写退货人姓名！");
        }
        nifferOrder.setConsumerName(consumerName);
        
        if(consumerMobile == null) {
            return new SimpleJsonResponse(false, "请填写客户手机号码！");
        } else {
            if(!ValidUtil.isMobile(consumerMobile.trim())) {
                return new SimpleJsonResponse(false, "请填写正确的客户手机号码！");
            }
        }
        nifferOrder.setConsumerMobile(consumerMobile.trim());
        for (GeoRegion r : region)
        {
            if (r != null)
            {
                if(r.getType() == RegionType.province ) {
                    nifferOrder.setConsumerProvince(r);
                } else if(r.getType() == RegionType.city) {
                    nifferOrder.setConsumerCity(r);
                } else if(r.getType() == RegionType.county) {
                    nifferOrder.setConsumerCounty(r);
                } else if(r.getType() == RegionType.town) {
                    nifferOrder.setConsumerTown(r);
                }
            }
        }
        if (region.length < 3) {
            return new SimpleJsonResponse(false, "请填写客户所在省市区");
        }
        
        if(address == null || address.trim().isEmpty()) {
            return new SimpleJsonResponse(false, "请输入详细地址！");
        }
        nifferOrder.setAddress(address);
        
        if(storageUnitIds == null || storageUnitIds.length < 1) {
            return new SimpleJsonResponse(false, "您没有选择任何退入或者换出的商品！");
        }
        if(storageUnitIds.length != entryTypes.length || storageUnitIds.length != amounts.length || entryTypes.length != amounts.length) {
            return new SimpleJsonResponse(false, "数据错误，请检查！");
        }
        
        nifferOrder.setApplyRemoteAddr(request.getRemoteAddr());
        nifferOrder.setRemark(remark);
        nifferOrder.setRemarkByWorker(remarkByWorker);
        if(shipmentCompanyId!=null){
            ShipmentCompany company = shipmentCompanyService.findOne(shipmentCompanyId);
            nifferOrder.setShipmentCompany(company);
            if (shipmentOrder != null && !shipmentOrder.trim().isEmpty())
            {
                nifferOrder.setShipmentOrder(shipmentOrder);
            }
        }
        List<NifferOrderEntry> nifferOrderEntryList = new ArrayList<>();
        if(storageUnitIds.length > 0){
            for(int i = 0; i < storageUnitIds.length; i ++){
                NifferOrderEntry entry = new NifferOrderEntry();
                entry.setStorageUnitId(storageUnitIds[i]);
                entry.setAmount(amounts[i]);
                entry.setEntryType(entryTypes[i]);
                entry.setProductId(productIds[i]);
                nifferOrderEntryList.add(entry);
            }
        }
        try {
            nifferOrderService.audit(nifferOrder,admin, request.getRemoteAddr(),order, nifferOrderEntryList);   
        } catch (Exception e) {
        	//打印错误日志
        	e.printStackTrace();
            return new SimpleJsonResponse(false, e.getMessage());
        }
        return new SimpleJsonResponse(true, "审核通过");
    }
    
    //编辑退款单的保存   
//    @RequiresPermissions("order_after_sale:audit")
    @RequestMapping(value = "/admin/order/refund/audit.php", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public  @ResponseBody SimpleJsonResponse refundAudit(@RequestParam(value = "id") Long id,
                                                  @RequestParam(value = "originalOrderId") Long originalOrderId,
                                                  @RequestParam(value = "consumerName", required = false) String consumerName,
                                                  @RequestParam(value = "consumerMobile", required = false) String consumerMobile,
                                                  @RequestParam(value = "remarkByWorker", required = false) String remarkByWorker,
                                                  @RequestParam(value = "remark", required = false) String remark,
                                                  @RequestParam(value = "refundAmount", required = true) Double refundAmount,
                                                  @RequestParam(value = "shipmentFee", required = false) Double shipmentFee,
                                                  WebsiteAdministrator admin,
                                                  HttpServletRequest request,
                                                  Model model) {
        NifferOrder nifferOrder   = nifferOrderService.findOne(id);
        if (nifferOrder.getStatus() != NifferOrderStatus.waiting_audit)
        {
            return new SimpleJsonResponse(false, "您只能操作等待审核状态的退款单,当前状态为:" + nifferOrder.getStatus().getDescription());
        }
        ShoppingOrder   order   = orderService.findOne(nifferOrder.getOriginalOrderId());
        
        if(!nifferOrder.getType().equals(AfterSaleType.returned)){
            return new SimpleJsonResponse(false, "只有退款售后单才可执行该操作！");
        }
        if(refundAmount==null){
            return new SimpleJsonResponse(false, "退款金额不可为空！");
        }
        if(refundAmount<=0){
            return new SimpleJsonResponse(false, "退款金额必须大于0元！");
        }
        if(refundAmount>nifferOrder.getRefundAmount()){
            return new SimpleJsonResponse(false, "退款金额不能大于实际购买金额！");
        }
        shipmentFee = shipmentFee==null?0:shipmentFee;
        if(shipmentFee<0){
            return new SimpleJsonResponse(false, "所退邮费不能小于0元！");
        }
        if(consumerName == null || consumerName.trim().isEmpty()) {
            return new SimpleJsonResponse(false, "请填写退款人姓名！");
        }
        if(consumerMobile == null) {
            return new SimpleJsonResponse(false, "请填写退款人手机号码！");
        } else {
            if(!ValidUtil.isMobile(consumerMobile.trim())) {
                return new SimpleJsonResponse(false, "请填写正确的退款人手机号码！");
            }
        }
        nifferOrder.setRefundAmount(refundAmount);
        nifferOrder.setShipmentFee(shipmentFee);
        
        nifferOrder.setConsumerName(consumerName);
        nifferOrder.setConsumerMobile(consumerMobile.trim());
        nifferOrder.setApplyRemoteAddr(request.getRemoteAddr());
        nifferOrder.setRemark(remark);
        nifferOrder.setRemarkByWorker(remarkByWorker);
        
      //清除分销商冻结额度
        List<FenxiaoOrder> fenxiaoLists = fenXiaoOrderService.findByOrderId(nifferOrder.getOriginalOrderId());
        if(CollectionUtils.isNotEmpty(fenxiaoLists)){
        	for (FenxiaoOrder fenxiaoOrder : fenxiaoLists) {
        		fenXiaoOrderService.clearFreezeMoney(fenxiaoOrder.getId());
			}
        	//修改状态
        	fenXiaoOrderService.del(order.getId());
        }
        try {
            nifferOrderService.audit(nifferOrder,admin, request.getRemoteAddr(),order, null);    
        } catch (Exception e) {
        	//打印错误日志
        	e.printStackTrace();
            return new SimpleJsonResponse(false, e.getMessage());
        }
        return new SimpleJsonResponse(true, "审核通过");
    }
    
    //编辑中的选择退入商品还是换出商品
    @RequestMapping(value = "/admin/order/return/chooseNifferEntry.php", method = RequestMethod.GET)
    public String addEntry(@RequestParam("id") long orderId,
                           @RequestParam(value = "kw", required = false) String keyword,
                           @RequestParam(value = "c", required = false) ProductCate cate,
                           @RequestParam(value = "b", required = false) Brand brand,
                           @RequestParam(value = "sp", required = false) Double smallPrice,
                           @RequestParam(value = "mp", required = false) Double maxPrice,
                           @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager,
                           WebsiteAdministrator admin, Model model) throws UnsupportedEncodingException
    {
        Page<Product> products = productService.find(new ProductCriteria().withKeyword(keyword).withCate(cate).withBrand(brand).withSelf(true).withStatus(ProductStatus.selling).withPrice(smallPrice, maxPrice).withDeleted(Boolean.FALSE), pager);
        List<Brand> brands =  brandService.findAll();
        
        StringBuilder link = new StringBuilder("/jdvop/admin/order/return/chooseNifferEntry.php?page=[:page]&id=").append(orderId);
        if(keyword != null && !keyword.isEmpty()){
            link.append("&kw=").append(keyword);
        }
        if(cate != null && cate.getId() > 0) {
            link.append("&c=").append(cate.getId());
        }
        if(brand != null && brand.getId() > 0){
            link.append("&b=").append(brand.getId());
        }
        if(smallPrice != null && smallPrice > 0D){
            link.append("&sp=").append(smallPrice);
        }
        if(maxPrice != null && maxPrice > 0D){
            link.append("&mp=").append(maxPrice);
        }
        
        model.addAttribute("products", products);
        model.addAttribute("brands", brands);
        model.addAttribute("link", link.toString());
        model.addAttribute("orderId", orderId);
        model.addAttribute("cates",  productCateService.rootCates());
        return "admin/order/niffer/chooseNifferEntry";
    }
    
    //选择退入商品中的（添加产品）
    @RequestMapping(value = "/admin/order/return/getNifferEntry.php", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> getNifferEntry(@RequestParam("id") int productId, Model model)
    {
        Product product = productService.findOne(productId);
        StorageUnit su = productService.findStorageUnits(product).get(0);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("product", product);
        dataMap.put("su", su);
        return dataMap;
    }
    
    
    /**
     * 退款单编辑页面
     * @param
     * @param admin
     * @param model
     * @return 
     */
//     @RequiresPermissions("order_after_sale:edit")
    @RequestMapping(value = "/admin/order/refund/create.php", method = RequestMethod.GET)
    public  String refundCreate(@RequestParam("id") long originalOrderId, WebsiteAdministrator admin, Model model)
    {
         PayFailOrderLog payFailOrderLog =  payFailOrderLogService.findByOrderId(originalOrderId);
         if(payFailOrderLog==null){
        	 throw new PermissionDeniedException( "该订单为非异常订单，不能操作退款！");
         }
         ShoppingOrder   order   = orderService.findOne(originalOrderId);
    	 if (order == null || order.isNiffer() || order.isDeprecated() ) {
             throw new PermissionDeniedException( "该订单不允许退款操作！");
         }
    	  if (Arrays.asList(OrderStatus.after_sale_service,OrderStatus.after_sale_completed).contains(order.getStatus())) {
         	 throw new PermissionDeniedException( "该订单在退款中或者已退款！");
          }
         if (!Arrays.asList(OrderStatus.waiting_shipment,OrderStatus.waiting_confirmed,OrderStatus.completed).contains(order.getStatus())) {
        	 throw new PermissionDeniedException(  "该订单不允许退款操作！");
         }
       
        //商品价格
        List<OrderEntry> orderEntryList = orderService.findEntries(order);
        Map<Integer, Double> entryPriceMap = new HashMap<>();
        List<Integer> storageUnitIdList = new ArrayList<>();
        List<Integer> productIdList = new ArrayList<>();
        for(OrderEntry entry:orderEntryList){
            entryPriceMap.put(entry.getStorageUnitId(), entry.getSoldPrice());
            storageUnitIdList.add(entry.getStorageUnitId());
            productIdList.add((int)entry.getSourceObjectId());
        }
        Map<Integer, Product> productMap = productService.findAsMap(productIdList);
        Map<Integer, StorageUnit> storageUnitMap = storageUnitService.find(storageUnitIdList);
        
        double shipmentFee = 0.0;
        //如果订单不包邮，则判断以前是否退过运费，如果没有，则克服可以在前端输入所退邮费
        if(order.getShipmentFee()>0){
            List<NifferOrder> nifferOrderList = nifferOrderService.find(order);
            if (nifferOrderList!=null && !nifferOrderList.isEmpty()) {
            	throw new PermissionDeniedException("该订单在退款中或已退款！");
            }
            shipmentFee = order.getShipmentFee();
        }
        double refundAmount = 0.0;
        for (OrderEntry orderEntry : orderEntryList) {
            //退款金额取购入时的价格
            refundAmount+=orderEntry.getSoldPrice() * orderEntry.getAmount();
        }
        if(order.isSplit()){
            ShoppingOrder parentOrder = orderService.findOne(order.getOriginalOrderId());
            model.addAttribute("payLog", orderService.findPayLog(parentOrder));
        }else{
            model.addAttribute("payLog", orderService.findPayLog(order));
        }
        model.addAttribute("productMap", productMap);
        model.addAttribute("storageUnitMap", storageUnitMap);
        model.addAttribute("refundAmount", refundAmount);
        model.addAttribute("shipmentFee", shipmentFee);
         model.addAttribute("order", order);
        model.addAttribute("bankType", BankType.values());
        model.addAttribute("entries", orderEntryList);
        model.addAttribute("entryPriceMap", entryPriceMap);
        model.addAttribute("storageUnitService", storageUnitService);
        model.addAttribute("productService", productService);
        return "admin/order/refund/create";
    }

    
	@RequestMapping(value = "/admin/order/niffer/refundForExceptionOrder.php", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
	public @ResponseBody SimpleJsonResponse refundForExceptionOrder(
			@RequestParam(value = "originalOrderId") long originalOrderId,
			@RequestParam(value = "consumerName", required = false) String consumerName,
			@RequestParam(value = "consumerMobile", required = false) String consumerMobile,
			@RequestParam(value = "remarkByWorker", required = false) String remarkByWorker,
			@RequestParam(value = "remark", required = false) String remark,
			@RequestParam(value = "refundAmount", required = true) Double refundAmount,
			@RequestParam(value = "shipmentFee", required = false) Double shipmentFee,
			WebsiteAdministrator admin, HttpServletRequest request, Model model) {
		if(refundAmount==null){
            return new SimpleJsonResponse(false, "退款金额不可为空！");
        }
        if(refundAmount<=0){
            return new SimpleJsonResponse(false, "退款金额必须大于0元！");
        }
        shipmentFee = shipmentFee==null?0:shipmentFee;
        if(shipmentFee<0){
            return new SimpleJsonResponse(false, "所退邮费不能小于0元！");
        }
        if(consumerName == null || consumerName.trim().isEmpty()) {
            return new SimpleJsonResponse(false, "请填写退款人姓名！");
        }
        if(consumerMobile == null) {
            return new SimpleJsonResponse(false, "请填写退款人手机号码！");
        } else {
            if(!ValidUtil.isMobile(consumerMobile.trim())) {
                return new SimpleJsonResponse(false, "请填写正确的退款人手机号码！");
            }
        }
    	ShoppingOrder order = orderService.findOne(originalOrderId);
        if (order == null || order.isNiffer() || order.isDeprecated() ) {
            return new SimpleJsonResponse<String>(false, "该订单不允许退款操作！");
        }
        if (Arrays.asList(OrderStatus.after_sale_service,OrderStatus.after_sale_completed).contains(order.getStatus())) {
            return new SimpleJsonResponse<String>(false, "该订单在退款中或者已退款！");
        }
        if (!Arrays.asList(OrderStatus.waiting_shipment,OrderStatus.waiting_confirmed,OrderStatus.completed).contains(order.getStatus())) {
            return new SimpleJsonResponse<String>(false, "该订单不允许退款操作！");
        }
        PayFailOrderLog payFailOrderLog =  payFailOrderLogService.findByOrderId(originalOrderId);
        if(payFailOrderLog==null){
        	 return new SimpleJsonResponse<String>(false, "该订单为非异常订单，不能操作退款！");
        }
        if(shipmentFee>order.getShipmentFee()){
            return new SimpleJsonResponse(false, "退款邮费不能大于实际邮费金额！");
        }
        NifferOrder nifferOrder = new NifferOrder();
        nifferOrder.setOriginalOrderId(order.getId());
        nifferOrder.setStatus(NifferOrderStatus.waiting_finance);
        nifferOrder.setShipmentFee(0d);
        nifferOrder.setConsumerName(consumerName);
        nifferOrder.setConsumerMobile(consumerMobile);
        nifferOrder.setConsumerProvince(order.getProvince());
        nifferOrder.setConsumerCity(order.getCity());
        nifferOrder.setConsumerCounty(order.getCounty());
        nifferOrder.setConsumerTown(order.getTown());
        nifferOrder.setAddress(order.getReceiverAddr());
        nifferOrder.setApplyTime(new Date());
        nifferOrder.setRemarkByWorker(remarkByWorker);
        nifferOrder.setRemark(remark);
        nifferOrder.setType(AfterSaleType.returned);
        nifferOrder.setReason("订单异常");
        nifferOrder.setIsSystem(OwnerType.system.equals(order.getDeliveryObjectType())||OwnerType.jingdong.equals(order.getDeliveryObjectType()));
        if(nifferOrder.getIsSystem()){
        	 nifferOrder.setOpenStatus(NifferOrderStatus.accepted);
        }
        List<NifferOrderEntry> nifferOrderEntryList = new ArrayList();
        List<OrderEntry> orderEntryList = orderService.findEntries(order);
        NifferOrderEntry inOrderEntry =  null;
        double maxRefundAmount = 0.0;
        for (OrderEntry orderEntry : orderEntryList) {
            inOrderEntry = new NifferOrderEntry();
            inOrderEntry.setStorageUnitId(orderEntry.getStorageUnitId());
            inOrderEntry.setAmount(orderEntry.getAmount());
            inOrderEntry.setEntryType(NifferOrderEntryType.in);
            inOrderEntry.setProductId((int)orderEntry.getSourceObjectId());
            nifferOrderEntryList.add(inOrderEntry);
            //退款金额取购入时的价格
            maxRefundAmount+=orderEntry.getSoldPrice() * orderEntry.getAmount();
        }
        if(refundAmount>maxRefundAmount){
            return new SimpleJsonResponse(false, "退款金额不能大于实际购买金额！");
        }
        nifferOrder.setRefundAmount(refundAmount);
        nifferOrder.setShipmentFee(shipmentFee);
        try {
	        nifferOrderService.applyAndRefund(order, nifferOrder, nifferOrderEntryList, admin, request.getRemoteAddr());
        } catch (Exception e) {
        	//打印错误日志
        	e.printStackTrace();
            return new SimpleJsonResponse(false,e.getMessage());
        }
        return new SimpleJsonResponse(true, "退款成功！");
    }
    
	@RequestMapping(value = "/admin/order/niffer/checkRefund.php", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
	public @ResponseBody SimpleJsonResponse checkRefund(
			@RequestParam(value = "id") long originalOrderId,
			WebsiteAdministrator admin, HttpServletRequest request, Model model) {

    	ShoppingOrder order = orderService.findOne(originalOrderId);
        if (order == null || order.isNiffer() || order.isDeprecated() ) {
            return new SimpleJsonResponse<String>(false, "该订单不允许退款操作！");
        }
        if (Arrays.asList(OrderStatus.after_sale_service,OrderStatus.after_sale_completed).contains(order.getStatus())) {
            return new SimpleJsonResponse<String>(false, "该订单在退款中或者已退款！");
        }
        if (!Arrays.asList(OrderStatus.waiting_shipment,OrderStatus.waiting_confirmed,OrderStatus.completed).contains(order.getStatus())) {
            return new SimpleJsonResponse<String>(false, "该订单不允许退款操作！");
        }
        PayFailOrderLog payFailOrderLog =  payFailOrderLogService.findByOrderId(originalOrderId);
        if(payFailOrderLog==null){
        	 return new SimpleJsonResponse<String>(false, "该订单为非异常订单，不能操作退款！");
        }
        return new SimpleJsonResponse(true, "允许退款!");
    }
	
    private boolean isEmpty(String temp){
        return temp == null || temp.isEmpty();
    }
}
