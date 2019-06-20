/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.controller;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.enums.OwnerType;
import com.lmf.common.exceptions.PermissionDeniedException;
import com.lmf.common.exceptions.ResourceNotFoundException;
import com.lmf.common.util.PagerSpec;
import com.lmf.common.util.ValidUtil;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.order.entity.*;
import com.lmf.order.enums.NifferOrderEntryType;
import com.lmf.order.enums.NifferOrderStatus;
import com.lmf.order.enums.OrderStatus;
import com.lmf.order.service.NifferOrderService;
import com.lmf.order.service.OrderService;
import com.lmf.order.vo.NifferOrderCriteria;
import com.lmf.order.vo.OrderCriteria;
import com.lmf.product.entity.Product;
import com.lmf.product.service.ProductService;
import com.lmf.product.service.StorageUnitService;
import com.lmf.sys.entity.ShipmentCompany;
import com.lmf.sys.service.ShipmentCompanyService;
import com.lmf.system.sdk.service.SystemOrderService;
import com.lmf.system.sdk.vo.OrderTrackResult;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsitePayMethod;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.service.WebsiteService;
import com.lmf.website.service.WebsiteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author shenzhixiong
 */
@Controller
public class OrderController {
    
    @Autowired
    private ProductService  productService;
    
    @Autowired
    private OrderService    orderService;
    
    @Autowired
    private StorageUnitService  storageUnitService;
    
    @Autowired
    private WebsiteService  websiteService;
    
    @Autowired
    private NifferOrderService nifferOrderService;
    
    @Autowired
    private WebsiteUserService websiteUserService;
    
    @Autowired
    private ShipmentCompanyService shipmentCompanyService;
    
    @Autowired
    private SystemOrderService systemOrderService;
    
    @RequestMapping(value = "/my/orders.php", method = RequestMethod.GET)
    public String orders(@RequestParam(value = "status", required = false) OrderStatus orderStatus,
                         @PagerSpecDefaults(pageSize = 10, sort = "time.desc") PagerSpec pager,
                         WebsiteUser websiteUser, Website website,
                         Model model) {  
        StringBuilder sbd = new StringBuilder("/jdvop/my/orders.php?page=[:page]");
        if(orderStatus != null){
            sbd.append("&status=").append(orderStatus.name());
        }
        OrderStatus[] status = null;

        if(orderStatus != null){
            status =  new OrderStatus[]{orderStatus};
        }
        Page<ShoppingOrder> orderPager = orderService.find(websiteUser, new OrderCriteria().withStatus(status), pager);
        List<ShoppingOrder> orderList = orderPager.getContent();
        
        Map<Long, Boolean> returnOrNifferMap = new HashMap<>();
        //用于标识该笔订单是否可以退换货
        if(orderList != null && !orderList.isEmpty()){
            for(ShoppingOrder order:orderList){                
                List<NifferOrder> dataList = nifferOrderService.find(order);
                if(dataList == null || dataList.isEmpty()){
                    //从来没有进行过退换货,允许
                    returnOrNifferMap.put(order.getId(), true);
                }else{
                    boolean falg = true;
                    for(NifferOrder item : dataList){
                        if(item.getStatus() != NifferOrderStatus.accepted && item.getStatus() != NifferOrderStatus.declined){
                            //存在退换货未完成的订单,不允许再次退换货操做
                            falg = false;
                            break;
                        }
                    }
                    returnOrNifferMap.put(order.getId(), falg);
                }
            }
        }
        
        //获取订单状态统计 
        Map<OrderStatus, Long>  statusStatics   = orderService.getOrderStatics(websiteUser, null);
        model.addAttribute("okOrderCount", statusStatics.get(OrderStatus.completed));
        model.addAttribute("confirmOrderCount", statusStatics.get(OrderStatus.waiting_confirmed));
        model.addAttribute("waitPayOrderCount", statusStatics.get(OrderStatus.waiting_pay));
        
        model.addAttribute("orderList", orderList);
        model.addAttribute("returnOrNifferMap", returnOrNifferMap);
        model.addAttribute("link", sbd.toString());
        model.addAttribute("pager", orderPager.getPagerSpec());
        model.addAttribute("orderService", orderService);
        model.addAttribute("productService", productService);
        model.addAttribute("storageUnitService", storageUnitService);
        model.addAttribute("returnedOrNifferOrderService", nifferOrderService);
        return "user_center/orders";
    }
    
    //换货单
    @RequestMapping(value = "/my/nifferOrders.php", method = RequestMethod.GET)
    public String  nifferOrders(@RequestParam(value = "status", required = false) OrderStatus orderStatus,
                                @PagerSpecDefaults(pageSize = 15, sort = "applyTime.desc") PagerSpec pager,
                                WebsiteUser websiteUser, Website website,
                                Model model)
    {
        StringBuilder sbd = new StringBuilder("/jdvop/my/nifferOrders.php?page=[:page]");
        if(orderStatus != null){
            sbd.append("&status=").append(orderStatus.name());
        }
        Page<NifferOrder> dataPage = nifferOrderService.find(new NifferOrderCriteria().withUserId(websiteUser.getId()), pager);
        Map<Long, Boolean>  refundDataMap = new HashMap<>();            //用于前段页面区分直接退款和退换货单数据
        Map<Long, ShoppingOrder> orderDataMap = new HashMap<>();            //用于存放订单数据
        if(dataPage.hasContent()){
            for(NifferOrder item : dataPage.getContent()){
                if(nifferOrderService.findEntries(item).isEmpty()){
                    //没有任何退出换入商品
                    refundDataMap.put(item.getId(), false);
                }else{
                    refundDataMap.put(item.getId(), true);
                }
                orderDataMap.put(item.getId(), orderService.findOne(item.getOriginalOrderId()));
            }
            model.addAttribute("refundDataMap", refundDataMap);
            model.addAttribute("orderDataMap", orderDataMap);
        }
        
        model.addAttribute("dataPage", dataPage);
        model.addAttribute("link", sbd.toString());
        model.addAttribute("pager", dataPage.getPagerSpec());
        
        return "user_center/niffer_orders";
    }
    

    @RequestMapping(value = "/my/order/{orderID}.php", method = RequestMethod.GET)
    public String viewOrder(@PathVariable("orderID") long orderID, Website website, WebsiteUser websiteUser, Model model) throws IOException
    {
        ShoppingOrder   order   = orderService.findOne(orderID);
        if (websiteUser == null || order == null || !Objects.equals((long)order.getUserId(),websiteUser.getId()))
        {
            throw new PermissionDeniedException();
        }
        if (order.getStatus() == OrderStatus.waiting_pay)
        {
            List<WebsitePayMethod>  payMethods  = websiteService.findAllPayMethods();
            model.addAttribute("payMethods", payMethods);
        }
        //快递详情
        if(order.getDeliveryObjectType() == OwnerType.enterprise || order.getDeliveryObjectType() == OwnerType.provider) {
            OrderShipmentLog shipmentLog = orderService.findShipmentLog(order);
            if(shipmentLog != null){
                model.addAttribute("shipmentLog", shipmentLog);
            }
        } else {
            if(order.getStatus() != OrderStatus.waiting_pay) {
                OrderTrackResult track = systemOrderService.findOrderTrack(website, order);
                if(track != null) {
                    model.addAttribute("sod", track);
                }
            }
        }
        model.addAttribute("order", order);
        model.addAttribute("entries", orderService.findEntries(order));
        model.addAttribute("storageUnitService", storageUnitService);
        model.addAttribute("productService", productService);
        return "user_center/order_detail";
    }
    
    
    //申请换货
    @RequestMapping(value = "/my/order/niffer.php", method = RequestMethod.GET)
    public String niffer(@RequestParam("id") long orderId,  WebsiteUser websiteUser, Model model)
    {
        ShoppingOrder order = orderService.findOne(orderId);
        if(websiteUser == null || order == null || order.getStatus() != OrderStatus.waiting_confirmed){
           throw new ResourceNotFoundException(); 
        }
        
        if (!Objects.equals((long)order.getUserId(), websiteUser.getId()))
        {
            throw new PermissionDeniedException();
        }
        
        List<OrderEntry> entryList = orderService.findEntries(order);
        
        model.addAttribute("order", order);
        model.addAttribute("entryList", entryList);
        model.addAttribute("user", websiteUser);
        model.addAttribute("shipmentCompanys", shipmentCompanyService.all());
        return "user_center/apply_niffer";
    }
    
    @RequestMapping(value = "/my/order/niffer.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse  niffer(@RequestParam("id") long orderId,
                                                    @RequestParam("selected[]") boolean[] selected,
                                                    @RequestParam("product_id[]") int[] productId,
                                                    @RequestParam("sui[]") int[] storageUnitIds,
                                                    @RequestParam("amount[]") int[] amounts,
                                                    @RequestParam("remark") String remark,
                                                    @RequestParam("consumerName") String consumerName,
                                                    @RequestParam("consumerMobile") String consumerMobile,
                                                    WebsiteUser websiteUser, Model model, HttpServletRequest request)
    {
        ShoppingOrder order = orderService.findOne(orderId);
        if(order == null || order.getStatus() != OrderStatus.waiting_confirmed){
            return new SimpleJsonResponse(false, "数据错误，请勿非法操作！");
        }
        
        if (websiteUser == null || !Objects.equals((long)order.getUserId(), websiteUser.getId())) {
            return new SimpleJsonResponse(false, "数据错误，请勿非法操作！");   
        }
        List<NifferOrder> olds = nifferOrderService.findByOriginalOrderId(order.getId());
        if(olds != null && !olds.isEmpty())
        {
            for(NifferOrder rno : olds){
                NifferOrderStatus currentStatus = rno.getStatus();
                if(NifferOrderStatus.accepted != currentStatus && NifferOrderStatus.declined != currentStatus){
                    return new SimpleJsonResponse(false, "您已申请换货，请勿重复申请！");
                }
            }
        }
        
        NifferOrder nifferOrder = new NifferOrder();
        nifferOrder.setOriginalOrderId(order.getId());
        nifferOrder.setStatus(NifferOrderStatus.waiting_audit);
        nifferOrder.setShipmentFee(0d);
        if(consumerName == null || consumerName.isEmpty()){
            nifferOrder.setConsumerName(order.getReceiverName());
        }else{
            nifferOrder.setConsumerName(consumerName);
        }
        if(consumerMobile == null || consumerMobile.isEmpty()){
            nifferOrder.setConsumerMobile(order.getReceiverMobile());
        }else{
            if(!ValidUtil.isMobile(consumerMobile)) {
                return new SimpleJsonResponse(false, "请输入正确的手机号码！");
            }
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
        
        List<NifferOrderEntry> entryList = new ArrayList<>();
        for(int i = 0; i < selected.length; i++ )
        {
            if(selected[i]){
                NifferOrderEntry inOrderEntry = new NifferOrderEntry();
                inOrderEntry.setStorageUnitId(storageUnitIds[i]);
                inOrderEntry.setProductId(productId[i]);
                inOrderEntry.setAmount(amounts[i]);
                inOrderEntry.setEntryType(NifferOrderEntryType.in);
                entryList.add(inOrderEntry);
                //如果为换货,则同时生成一笔换货商品数据
                 NifferOrderEntry outOrderEntry = new NifferOrderEntry();
                 outOrderEntry.setStorageUnitId(storageUnitIds[i]);
                 outOrderEntry.setProductId(productId[i]);
                 outOrderEntry.setAmount(amounts[i]);
                 outOrderEntry.setEntryType(NifferOrderEntryType.out);
                 entryList.add(outOrderEntry);
            }
        }
        
        nifferOrderService.applyNiffer(order, websiteUser, nifferOrder, entryList, request.getRemoteAddr());
        return new SimpleJsonResponse(true , "");
    }
    
    @RequestMapping(value = "/my/order/applyNifferOk.php", method = RequestMethod.GET)
    public String applyOk() {
        return "user_center/apply_niffer_ok";
    }
    
    //换货详情
    @RequestMapping(value = "/my/order/niffer{id}.php", method = RequestMethod.GET)
    public String nifferView(@PathVariable("id") int nifferOrderId, WebsiteUser websiteUser, Model model)
    {
       NifferOrder nifferOrder = nifferOrderService.findOne(nifferOrderId);
       if(nifferOrder == null ){
           throw new ResourceNotFoundException();
       }

       ShoppingOrder order = orderService.findOne(nifferOrder.getOriginalOrderId());
       if (websiteUser == null || order == null || !Objects.equals((long)order.getUserId(), websiteUser.getId()))
       {
           throw new PermissionDeniedException();
       }

       //新的订单是否已经发货
       if(nifferOrder.getNewCreatedOrderId() != null && nifferOrder.getNewCreatedOrderId() > 0){
           ShoppingOrder newOrder = orderService.findOne(nifferOrder.getNewCreatedOrderId());
           OrderShipmentLog shipmentLog = orderService.findShipmentLog(newOrder);
           if(shipmentLog != null){
               model.addAttribute("shipmentLog", shipmentLog);
           }
       }

       List<NifferOrderEntry> entrys = nifferOrderService.findEntries(nifferOrder);

       model.addAttribute("nifferOrder", nifferOrder);
       model.addAttribute("entrys", entrys);   //换出
       model.addAttribute("order", order);
       return "user_center/niffer_order_view";
    }
     
    //确认收货
    @RequestMapping(value = "/my/confirmReceipt/{orderID}.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse confirmReceiptOrder(@PathVariable("orderID") int orderID,
    															WebsiteUser websiteUser, 
    															HttpServletRequest request){
        
        if (websiteUser == null) {
            throw new PermissionDeniedException("当前登录信息已失效");
        }  
        ShoppingOrder   order   = orderService.findOne(orderID);
        if(order == null || !Objects.equals((long)order.getUserId(), websiteUser.getId())) {
            return new SimpleJsonResponse(false, "订单不存在或已经被删除");
        }
        if(order.getStatus() != OrderStatus.waiting_confirmed) {
            return new SimpleJsonResponse(false, "订单状态异常,操作失败");
        }
        orderService.confirmReceiptOrder(order, websiteUser, request.getRemoteAddr());
        
        return new SimpleJsonResponse(true, "操作成功");
    }
    
    
    //删除订单
    @RequestMapping(value = "/my/order/delete_{orderID}.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse delete(@PathVariable("orderID") int orderID,
                                                   Website website,
                                                   HttpSession session, 
                                                   WebsiteUser websiteUser, 
                                                   UserDetail currentUser)
    {
        ShoppingOrder order = orderService.findOne(orderID);
        if (order == null || websiteUser == null || !Objects.equals((long)order.getUserId(), websiteUser.getId()))
        {
            throw new PermissionDeniedException();
        }
        
        if(OrderStatus.waiting_audit != order.getStatus()){
            return new SimpleJsonResponse(false, "订单状态异常,删除订单操作失败");
        }
        
        orderService.delete(order, website, websiteUser);
        WebsiteUser user = websiteUserService.findOne(websiteUser.getId());
        currentUser.setIntegral(user.getIntegral());
        session.setAttribute("currentUser", currentUser); 
        return new SimpleJsonResponse(true, "订单删除成功");
    }
    
    @RequestMapping(value = "/my/order/niffer/shimpentInfo.php", method = RequestMethod.GET)
    public String shimpentInfo(@RequestParam("id") Integer id, Model model) {
        NifferOrder nifferOrder = nifferOrderService.findOne(id);
        if(nifferOrder == null) {
            throw new RuntimeException();
        }
        model.addAttribute("nifferOrder", nifferOrder);
        model.addAttribute("shipmentCompanys", shipmentCompanyService.all());
        return "user_center/shimpent_info";
    }
    
    @RequestMapping(value = "/my/order/niffer/shimpentInfo.php", method = RequestMethod.POST) 
    public @ResponseBody SimpleJsonResponse shimpentInfo(@RequestParam("id") Integer id,
                                                         @RequestParam("shimpentCompanyId") Integer shimpentCompanyId,
                                                         @RequestParam("shimpentOrder") String shimpentOrder) {
        
        NifferOrder nifferOrder = nifferOrderService.findOne(id);
        if(nifferOrder == null) {
            return new SimpleJsonResponse(false, "数据错误，请勿非法操作");
        }
        
        ShipmentCompany company = shipmentCompanyService.findOne(shimpentCompanyId);
        if(company == null) {
            return new SimpleJsonResponse(false, "数据错误，快递公司不存在！");
        }
        
        nifferOrder.setShipmentCompany(company);
        nifferOrder.setShipmentOrder(shimpentOrder);
        nifferOrderService.save(nifferOrder, null);
        return new SimpleJsonResponse(true, null);
    }
    
    @RequestMapping(value = "/my/order/choosePay.php", method = RequestMethod.GET)
    public String choosePayType(@RequestParam("id") long orderId, WebsiteUser websiteUser, Model model)
    {
        ShoppingOrder order = orderService.findOne(orderId);
        if(order == null) {
            return "redirect:/my/orders.php";
        }
        if (!Objects.equals(order.getUserId(), websiteUser.getId().intValue())) {
            return "redirect:/my/orders.php";
        }
        if(OrderStatus.waiting_pay != order.getStatus()){
            return "redirect:/my/order/" + order.getId() + ".php";
        }
        
        List<OrderEntry> entries = orderService.findEntries(order);
        List<Integer>  pids = new ArrayList<>();
        for(OrderEntry oe : entries){
            pids.add(new Long(oe.getSourceObjectId()).intValue());
        }
        Map<Integer, Product> productMap = productService.findAsMap(pids);
        
        model.addAttribute("order", order);
        model.addAttribute("entries", entries);
        model.addAttribute("productMap", productMap);
        
        return "order/choose_pay_method";
    }
    
    //关闭超时订单
    @RequestMapping(value = "/my/order/closeOrder.php", method=RequestMethod.POST,produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse systemCloseWaitingPayOrder(@RequestParam("id") long id, WebsiteUser websiteUser)
    {
        ShoppingOrder order = orderService.findOne(id);
        if(order == null || !Objects.equals((long)order.getUserId(), websiteUser.getId()))
        {
            return new SimpleJsonResponse(false, "操作失败！");
        }
        
        orderService.systemCloseWaitingPayOrder(order);
        return new SimpleJsonResponse(true, null);
    }
    
}
