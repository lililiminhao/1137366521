/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.order.controller;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.enums.OwnerType;
import com.lmf.common.exceptions.PermissionDeniedException;
import com.lmf.common.json.JsonService;
import com.lmf.common.tuple.BinaryTuple;
import com.lmf.common.tuple.TernaryTuple;
import com.lmf.common.util.LMFIOUtil;
import com.lmf.common.util.PagerSpec;
import com.lmf.enterprise.entity.Enterprise;
import com.lmf.enterprise.service.EnterpriseService;
import com.lmf.order.entity.*;
import com.lmf.order.enums.NifferOrderStatus;
import com.lmf.order.enums.OrderStatus;
import com.lmf.order.service.NifferOrderService;
import com.lmf.order.service.OrderPayLogService;
import com.lmf.order.service.OrderService;
import com.lmf.order.service.OrderShipmentLogService;
import com.lmf.order.vo.OrderCriteria;
import com.lmf.order.vo.OrderSeparationEntry;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.StorageUnit;
import com.lmf.product.service.ProductService;
import com.lmf.product.service.StorageUnitService;
import com.lmf.sys.entity.GeoRegion;
import com.lmf.sys.entity.ShipmentCompany;
import com.lmf.sys.service.GeoRegionService;
import com.lmf.sys.service.ShipmentCompanyService;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteAdministrator;
import com.lmf.website.service.WebsiteAdministratorService;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.Boolean;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author shenzhixiong
 */
@Controller
public class OrderStockoutController {
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private GeoRegionService geoRegionService;
    
    @Autowired
    private ShipmentCompanyService shipmentCompanyService;
    
    @Autowired
    private StorageUnitService storageUnitService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private JsonService jsonService;
    
    @Autowired
    private OrderPayLogService orderPayLogService;
    
    @Autowired
    private NifferOrderService nifferOrderService;
    
    @Autowired
    private OrderShipmentLogService orderShipmentLogService;
    
    @Autowired
    private WebsiteAdministratorService websiteAdministratorService;

    @Autowired
    private EnterpriseService enterpriseService;
    
    @Autowired
    private TimeZone    tz;
    
    @Qualifier("executor")
    @Autowired
    private TaskExecutor    taskExecutor;
    
    private final static Logger logger  = LoggerFactory.getLogger(OrderStockoutController.class);
    
    @ModelAttribute("provinces")
    public Set<GeoRegion> provinces()
    {
        return geoRegionService.findAllProvince();
    }
    
    @ModelAttribute("shipmentCompanys")
    public List<ShipmentCompany> shipmentCompanys(){
        return shipmentCompanyService.all();
    }
    
    @RequestMapping(value = "/admin/order/stockout/updateShipmentOrder.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse updateShipmentOrder(@RequestParam("id") long orderId,
                                                                @RequestParam("shipmentCompanyId") int shipmentId,
                                                                @RequestParam("shipmentOrder") String shipmentOrder,
                                                                WebsiteAdministrator admin, HttpServletRequest request)
    {
        ShoppingOrder order = orderService.findOne(orderId);
        if(order == null){
            return new SimpleJsonResponse(false, "订单不存在或已经被删除");
        }
        
        if(order.getStatus() != OrderStatus.waiting_confirmed){
            return new SimpleJsonResponse(false, "只能编辑等待等待确认收货订单的快递单号");
        }
        
        ShipmentCompany shipmentCompany = shipmentCompanyService.findOne(shipmentId);
        if(shipmentCompany == null){
            return new SimpleJsonResponse(false, "该订单未指定快递公司或指定的快递公司无效,发货失败!");
        }
        
        if(shipmentOrder == null || shipmentOrder.isEmpty() || shipmentOrder.length() < 5){
            return new SimpleJsonResponse(false, "该订单指定的快递单号无效,发货失败");
        }
        
        OrderShipmentLog osl = orderShipmentLogService.findOne(order);
        osl.setShipmentCompany(shipmentCompany);
        osl.setShipmentOrder(shipmentOrder.trim());
        
        orderShipmentLogService.updateShipmentOrder(osl, admin, request.getRemoteAddr());
        return new SimpleJsonResponse(true, "快递信息修改成功");
    }
    
    @RequestMapping(value = "/admin/orders/deliveriedOrders.php", method = RequestMethod.GET)
    public String deliveriedOrders(@RequestParam(value = "kw", required = false) String keyword,
                                   @RequestParam(value = "province", required = false) GeoRegion province,
                                   @RequestParam(value = "shipment", required = false) ShipmentCompany shipmentCompany,
                                   @RequestParam(value = "st", required = false) Date startDate,
                                   @RequestParam(value = "et", required = false) Date endDate,
                                   @RequestParam(value = "enterpriseId", required = false) Integer enterpriseId,//企业ID
                                   @RequestParam(value = "isEnterprise", required = false) Boolean isEnterprise,//是否为企业专享订单
                                   @PagerSpecDefaults(pageSize = 20, sort = "shipmentTime.desc") PagerSpec pager,
                                   Model model, WebsiteAdministrator admin) throws UnsupportedEncodingException 
    {
        StringBuilder link = new StringBuilder("/jdvop/admin/orders/deliveriedOrders.php?page=[:page]");
        if (keyword != null && !keyword.isEmpty()) {
            link.append("&kw=").append(URLEncoder.encode(keyword, "UTF-8"));
        }
        if (province != null) {
            link.append("&province=").append(province.getId());
        }
        if (shipmentCompany != null) {
            link.append("&shipment=").append(shipmentCompany.getId());
        }
        if (enterpriseId != null) {
            model.addAttribute("enterpriseId", enterpriseId);
            link.append("&enterpriseId=").append(enterpriseId);
        }
        if (isEnterprise != null) {
            model.addAttribute("isEnterprise", isEnterprise);
            link.append("&isEnterprise=").append(isEnterprise);
        }
        if (endDate != null)
        {
            Calendar    cal = Calendar.getInstance(tz);
            cal.setTime(endDate);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            endDate = cal.getTime();
        }
        OrderCriteria criteria = new OrderCriteria()
            .withShipmentCompany(shipmentCompany)
            .withKeyword(keyword)
            .withRegion(province)
            .withShipmentTime(startDate, endDate)
            .withStatus(OrderStatus.completed, OrderStatus.waiting_confirmed)
            .withEnterpriseId(enterpriseId)
            .withEnterprise(isEnterprise);
        Page<BinaryTuple<ShoppingOrder, OrderShipmentLog>> orders = null;
        if(admin.isProvider()) {
            orders = orderService.findOrderAndShipmentLog(OwnerType.provider, admin.getId(), criteria, pager);
        } else {
            orders = orderService.findOrderAndShipmentLog(null, null, criteria, pager);
        }
        if (orders.hasContent())
        {
            List<ShoppingOrder> orderList   = new ArrayList<>(orders.getContent().size());
            for (BinaryTuple<ShoppingOrder, OrderShipmentLog> order : orders)
            {
                orderList.add(order.getValue1());
            }
            Map<ShoppingOrder, List<NifferOrder>> allReturnOrNifferorders = nifferOrderService.find(orderList);
            //用于标识该笔订单是否可以退换货
            Map<Long, Boolean> returnOrNifferMap = new HashMap<>();         
            for(ShoppingOrder order : orderList){
                List<NifferOrder> dataList = allReturnOrNifferorders.get(order);
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
            model.addAttribute("returnOrNifferMap", returnOrNifferMap);
            model.addAttribute("paylogMap", orderPayLogService.findAsMap(orderList));
        }
        List<Enterprise> enterpriseList = enterpriseService.list(null);
        Map<Integer, Enterprise> enterpriseMap = enterpriseService.map();
        model.addAttribute("enterpriseList",enterpriseList);
        model.addAttribute("enterpriseMap",enterpriseMap);
        model.addAttribute("admin", admin);
        model.addAttribute("orders", orders);
        model.addAttribute("link", link.toString());
        //查询所有供应商信息
        model.addAttribute("providerMap", websiteAdministratorService.findAllProvider());
        return "admin/order/stockout/deliveriedOrders";
    }
    
    //待发货订单
    @RequestMapping(value = "/admin/orders/waitingShipmentOrders.php", method = RequestMethod.GET)
    public String waitingShipmentOrders(@RequestParam(value = "kw", required = false) String keyword,
                                        @RequestParam(value = "province", required = false) GeoRegion province,
                                        @RequestParam(value = "shipment", required = false) ShipmentCompany shipmentCompany,
                                        @RequestParam(value = "st", required = false) Date startCreateTime,
                                        @RequestParam(value = "et", required = false) Date endCreateTime,
                                        @RequestParam(value = "providerId", required = false) Integer providerId, //供应商ID
                                        @RequestParam(value = "enterpriseId", required = false) Integer enterpriseId,//企业ID
                                        @RequestParam(value = "isEnterprise", required = false) Boolean isEnterprise,//是否为企业专享订单
                                        @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager,
                                        WebsiteAdministrator admin,
                                        Model model) throws UnsupportedEncodingException {
        OrderCriteria criteria = new OrderCriteria().withShipmentCompany(shipmentCompany).withKeyword(keyword)
                .withRegion(province).withCreateTime(startCreateTime, endCreateTime).withStatus(OrderStatus.waiting_shipment)
                .withProviderId(providerId).withEnterpriseId(enterpriseId).withEnterprise(isEnterprise);;
        Page<ShoppingOrder> orders = null;
        if(admin.isProvider()){
            orders = orderService.find(OwnerType.provider, admin.getId(), criteria, pager);
        } else {
            orders = orderService.findSelfAndProvider(criteria, pager);
        }
        StringBuilder link = new StringBuilder("/jdvop/admin/orders/waitingShipmentOrders.php?page=[:page]");
        if (keyword != null && !keyword.isEmpty()) {
            link.append("&kw=").append(URLEncoder.encode(keyword, "UTF-8"));
        }
        if(providerId != null && providerId > 0) {
        	model.addAttribute("providerId", providerId);
        	link.append("&providerId=").append(providerId);
        }
        if (province != null) {
            link.append("&province=").append(province.getId());
        }
        if (shipmentCompany != null) {
            link.append("&shipment=").append(shipmentCompany.getId());
        }
        if (enterpriseId != null) {
            model.addAttribute("enterpriseId", enterpriseId);
            link.append("&enterpriseId=").append(enterpriseId);
        }
        if (isEnterprise != null) {
            model.addAttribute("isEnterprise", isEnterprise);
            link.append("&isEnterprise=").append(isEnterprise);
        }
                
        Calendar cal = Calendar.getInstance(tz);
        Date endDates = cal.getTime();
        cal.add(Calendar.MONTH, -2);
        Date startDates= cal.getTime();

        List<Enterprise> enterpriseList = enterpriseService.list(null);
        Map<Integer, Enterprise> enterpriseMap = enterpriseService.map();
        model.addAttribute("enterpriseList",enterpriseList);
        model.addAttribute("enterpriseMap",enterpriseMap);
        model.addAttribute("startDate", startDates);
        model.addAttribute("endDate", endDates);
        model.addAttribute("orders", orders);
        model.addAttribute("shipmentCompanys", shipmentCompanyService.all());
        //查询所有供应商信息
        model.addAttribute("providerMap", websiteAdministratorService.findAllProvider());
        model.addAttribute("link", link.toString());
        return "admin/order/stockout/waitingShipmentOrders";
    }
    
    //等待发货订单导出
    @RequestMapping(value="/admin/order/doExportExcel.php" , method = RequestMethod.POST)
    public @ResponseBody void exportWaitingShipmentOrders(@RequestParam(value="kw", required = false) String kw, //关键字
                                        @RequestParam(value = "status", required = false) OrderStatus status, //订单状态
                                        @RequestParam("timeType") String timeType,  //时间内
                                        @RequestParam("st") Date startDate, //开始时间
                                        @RequestParam("et") Date endDate,  //结束时间
                                        @RequestParam(value = "ownerType", defaultValue = "provider") String type,  //订单类型
                                        @RequestParam(value = "providerId", required = false) Integer providerId, //供应商ID
                                        @RequestParam(value = "prov", required = false) GeoRegion prov,//省份
                                        WebsiteAdministrator admin,
                                        Website website, HttpServletResponse response) throws WriteException, IOException
    {
        if (website.getRatio() == null || website.getRatio() <= 0) {
            throw new RuntimeException();
        }
        
        Page<TernaryTuple<ShoppingOrder, OrderShipmentLog, OrderPayLog>> orders;
        PagerSpec pager = new PagerSpec(1, 0, 999999999, new PagerSpec.Order[]{new PagerSpec.Order("time", PagerSpec.DRECTION.desc)});
        OrderCriteria criteria = new OrderCriteria().withKeyword(kw).withSystemDeprecated(false).withOwnerType(OwnerType.provider).withStatus(OrderStatus.waiting_shipment).withProviderId(providerId);
       
        orders = orderService.findOrderWithShipmentLogAndPayLog(criteria, pager);
        response.setContentType("application/force-download");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.addHeader("Content-Type", "application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", new StringBuilder("attachment;filename=").append(URLEncoder.encode("订单导出", "UTF-8")).append(".xls").toString());
        
        //资料准备齐开始写Excel了
        try(OutputStream out = response.getOutputStream()){
            WritableWorkbook workbook = Workbook.createWorkbook(out);
            WritableSheet sheet = workbook.createSheet("导出订单列表", 0);
            
            //设置列宽
            sheet.setColumnView(0, 15);//订单号
            sheet.setColumnView(1, 20);//供应商
            sheet.setColumnView(2, 20);//下单时间
            sheet.setColumnView(3, 10);//商品ID 
            sheet.setColumnView(4, 15);//商品型号
            sheet.setColumnView(5, 25);//商品名称
            sheet.setColumnView(6, 12);//单价
            sheet.setColumnView(7, 12);//购买量
            sheet.setColumnView(8, 20);//收货人
            sheet.setColumnView(9, 22);//身份证号码
            sheet.setColumnView(10, 20);//收货人电话
            sheet.setColumnView(11, 10);//省
            sheet.setColumnView(12, 10);//市
            sheet.setColumnView(13, 15);//县/区
            sheet.setColumnView(14, 30);//收货地址
            sheet.setColumnView(15, 30);//订单留言
            sheet.setColumnView(16, 15);//父订单号
            
            
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            sheet.addCell(new Label(0, 0, "订单号", titleFmt));
            sheet.addCell(new Label(1, 0, "供应商", titleFmt));
            sheet.addCell(new Label(2, 0, "下单时间", titleFmt));
            sheet.addCell(new Label(3, 0, "商品ID", titleFmt));
            sheet.addCell(new Label(4, 0, "商品型号", titleFmt));
            sheet.addCell(new Label(5, 0, "商品名称", titleFmt));
            sheet.addCell(new Label(6, 0, "单价", titleFmt));
            sheet.addCell(new Label(7, 0, "购买量", titleFmt));
            sheet.addCell(new Label(8, 0, "收货人", titleFmt));
            sheet.addCell(new Label(9, 0, "身份证号码", titleFmt));
            sheet.addCell(new Label(10, 0, "收货人电话", titleFmt));
            sheet.addCell(new Label(11, 0, "省", titleFmt));
            sheet.addCell(new Label(12, 0, "市", titleFmt));
            sheet.addCell(new Label(13, 0, "县/区", titleFmt));
            sheet.addCell(new Label(14, 0, "收货地址", titleFmt));
            sheet.addCell(new Label(15, 0, "订单留言", titleFmt));
            sheet.addCell(new Label(16, 0, "父订单号", titleFmt));

            //准备导出Excel所需要的数据
            Map<ShoppingOrder, List<OrderEntry>> allEntries = null;
            Map<Integer, StorageUnit> allStorageUnits = null;
            Map<Integer, Product> allProducts = null;
            List<ShoppingOrder> originalShoppingOrders = new ArrayList<>();
            if(orders.hasContent()){
                Set<Long> originalOrderIdList = new HashSet<>();
                List<ShoppingOrder> allOrders = new ArrayList<>();
                for (TernaryTuple<ShoppingOrder, OrderShipmentLog, OrderPayLog> tuple : orders) {
                    allOrders.add(tuple.getValue1());
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

                //把原单号ID提取出来  然后根据原单ID集合查出原始订单
                for (ShoppingOrder shoppingOrder : allOrders) {
                    if(shoppingOrder.isSplit() == true){
                        originalOrderIdList.add(shoppingOrder.getOriginalOrderId());
                    }
                }
                if(originalOrderIdList.size() > 0){
                    originalShoppingOrders = orderService.find(originalOrderIdList);
                }

                int row = 1;
                for(TernaryTuple<ShoppingOrder, OrderShipmentLog, OrderPayLog> tuple : orders){
                    ShoppingOrder order = tuple.getValue1();
                    List<OrderEntry> entries = allEntries.get(order);
                    for(OrderEntry entry : entries){
                        StorageUnit storageUnit = allStorageUnits.get(entry.getStorageUnitId());
                        Product product = null;
                        if (storageUnit != null) {
                            product = allProducts.get(storageUnit.getProductId());
                        }
                        sheet.addCell(new Label(0, row, order.getKey()));
                        if(product != null && product.getOwnerId() != null){
                            //根据产品数据 查找供应商信息
                            WebsiteAdministrator administrator =   websiteAdministratorService.findOne(product.getOwnerId());
                            if(product.getOwnerType().equals(OwnerType.provider) && administrator != null){
                                sheet.addCell(new Label(1, row, administrator.getWorkerName())); //供应商名字
                            }else{
                                sheet.addCell(new Label(1, row, "")); //供应商名字
                            }
                        }
                        sheet.addCell(new Label(2, row, sdf.format(order.getCreateTime()))); //下单时间
                        if(product != null){
                            sheet.addCell(new jxl.write.Number(3, row, product.getId(), intFmt)); //商品ID
                            sheet.addCell(new Label(4, row,product.getProductCode())); //商品型号
                            sheet.addCell(new Label(5, row,product.getName())); //商品名称
                        }
                        sheet.addCell(new jxl.write.Number(6, row, entry.getSoldPrice(), priceFmt));//购买单价
                        sheet.addCell(new jxl.write.Number(7, row, entry.getAmount(), intFmt)); //够买数量
                        sheet.addCell(new Label(8, row, order.getReceiverName())); //收件人
                        sheet.addCell(new Label(9, row, order.getReceiverIdentityCard())); //身份证号码
                        if (order.getReceiverMobile() == null || order.getReceiverMobile().isEmpty()) {
                            sheet.addCell(new Label(10, row, order.getReceiverPhone()));
                        } else if (order.getReceiverPhone() == null || order.getReceiverPhone().isEmpty()) {
                            sheet.addCell(new Label(10, row, order.getReceiverMobile()));
                        } else {
                            sheet.addCell(new Label(10, row, new StringBuilder(order.getReceiverMobile()).append('/').append(order.getReceiverPhone()).toString()));
                        }
                        sheet.addCell(new Label(11, row, order.getProvince().getName())); //省份
                        sheet.addCell(new Label(12, row, order.getCity().getName())); //市
                        if (order.getTown() != null) {
                            sheet.addCell(new Label(13, row, order.getCounty().getName() + order.getTown().getName(), titleFmt)); //区县
                        } else {
                            sheet.addCell(new Label(13, row, order.getCounty().getName())); //区县
                        }
                        //收货地址（包含省、市、区）
                        String address = order.getProvince().getName() + order.getCity().getName() + order.getCounty().getName();
                        if (order.getTown() != null) {
                            sheet.addCell(new Label(14, row, address + order.getTown().getName() + order.getReceiverAddr())); //收货地址
                        } else {
                            sheet.addCell(new Label(14, row, address + order.getReceiverAddr()));
                        }
                        sheet.addCell(new Label(15, row, order.getUserRemark())); //订单留言
                        //父订单号
                        if(order.isSplit() == true && order.getOriginalOrderId() != null){
                            for (ShoppingOrder originalShoppingOrder : originalShoppingOrders) {
                                if(order.getOriginalOrderId().equals(originalShoppingOrder.getId())){
                                    sheet.addCell(new Label(16, row, originalShoppingOrder.getKey()));
                                }
                            }
                        }
                        ++row;
                    }
                }
            }
            workbook.write();
            workbook.close();
        }
    }
    
    @RequestMapping(value = "/admin/order/stockout/doShipments.php", method = RequestMethod.POST)
    public String doShipments(@RequestParam("id[]") Long[] orderIds,
                              Model model) throws UnsupportedEncodingException {
        List<ShoppingOrder> orders  = orderService.find(Arrays.asList(orderIds));
        List<ShoppingOrder> filteredOrders  = new ArrayList<>(orders.size());
        for (ShoppingOrder order : orders)
        {
            if (order.getStatus() == OrderStatus.waiting_shipment)
            {
                filteredOrders.add(order);
            }
        }
        if (!filteredOrders.isEmpty())
        {
            List<OrderSeparationEntry> separationEntries    = orderService.findSeparationEntries(filteredOrders);
            model.addAttribute("separationEntries", separationEntries);
        }
        model.addAttribute("filteredOrders", filteredOrders);
        return "admin/order/stockout/doShipments";
    }
    
    @RequestMapping(value = "/order/doShipmentFeedbackBatch.php", method = RequestMethod.GET)
    public  String  doShipmentFeedbackBatch(Model model) {
        return "admin/order/stockout/doShipmentFeedbackBatch";
    }
    
    @RequestMapping(value = "/order/exportWatingShipmentOrders.php", method = RequestMethod.POST)
    public  @ResponseBody void  exportWatingShipmentOrders(@RequestParam("id[]") Long[] ids,
                                                           @RequestParam(value = "format", defaultValue = "lmf") String format,
                                                           HttpServletResponse response,
                                                           Model model) throws IOException, WriteException
    {
        List<ShoppingOrder> orders  = orderService.find(Arrays.asList(ids));
        List<ShoppingOrder> filteredOrders  = new ArrayList<>(orders.size());
        for (ShoppingOrder order : orders)
        {
            if (order.getStatus() == OrderStatus.waiting_shipment)
            {
                filteredOrders.add(order);
            }
        }
        if (filteredOrders.isEmpty())
        {
            return;
        }
        String  fileName    = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance(tz).getTime()) + "日导入发货订单";
        if (format.equals("xls"))
        {
            fileName    = URLEncoder.encode(fileName + ".xls", "UTF-8");
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        } else {
            fileName    = URLEncoder.encode(fileName + ".lmf", "UTF-8");
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        }
        try (OutputStream out = response.getOutputStream()){
            if (format.equals("xls"))
            {
                writeOrdersExcel(filteredOrders, out);
            } else {
                LMFIOUtil.writeInt(out, 0x04010203);
                LMFIOUtil.writeInt(out, 0x78B7C3D8);
                writeOrdersFormatLMF(out, filteredOrders);
            }
        }
    }

    /*
     * 物流单信息
     */
    @RequestMapping(value = "/admin/order/stockout/logistics.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String orderLogistics(@RequestParam("orderId") int[] ordersId, Model model) throws UnsupportedEncodingException {

        List<ShoppingOrder> orderList = new ArrayList<>();
        for (int id : ordersId) {
            ShoppingOrder order = orderService.findOne(id);
            if (order == null) {
                throw new PermissionDeniedException();
            } else {
                orderList.add(order);
            }
        }
        
        model.addAttribute("orders", orderList);
        return "admin/order/stockout/logistics";
    }
    
    //批量导入发货结果 JSON格式
    @RequestMapping(value = "/admin/order/stockout/importSendOutForJson.php", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public @ResponseBody Progress importSendOutForJson(@RequestParam("file_upload") MultipartFile file, 
                                                       final Website website,
                                                       final WebsiteAdministrator admin, 
                                                       final HttpServletRequest request,
                                                       final HttpSession session) {
        try{
            String  content = new String(file.getBytes(), "UTF-8");
            final List<Object> rootData = jsonService.toList(content);
            final Progress    progress    = new Progress(rootData.size(), 0);
            session.setAttribute("BATCH_STOCKOUT_PROGRESS", progress);
            taskExecutor.execute(new Runnable(){
                @Override
                public void run() {
                    if(rootData != null && !rootData.isEmpty())
                    {
                        for (Object obj:rootData)
                        {
                            ++ progress.progress;
                            Map<String, Object> item = (Map<String, Object>)obj;
                            long    orderId     = Long.valueOf(item.get("OrderId").toString());
                            String  orderKey    = String.valueOf(item.get("OrderKey"));
                            ShoppingOrder order = orderService.findOne(orderId);
                            if(order != null  && order.getKey().equals(orderKey) &&  order.getStatus() == OrderStatus.waiting_shipment)
                            {
                                int     shipmentCode    = Integer.valueOf(item.get("ShipmentCode").toString());
                                String  shipmentOrder   = String.valueOf(item.get("ShipmentOrder"));
                                ShipmentCompany shipmentCompany = shipmentCompanyService.findOne(shipmentCode);
                                orderService.setToShipment(order, admin, website, shipmentCompany, shipmentOrder, request.getRemoteAddr());
                            }
                        }
                    }
                    session.removeAttribute("BATCH_STOCKOUT_PROGRESS");
                }
            });
            return progress;
        } catch (Throwable exp){
            if (logger.isWarnEnabled())
            {
                logger.warn(null, exp);
            }
        }
        return new Progress(0, 0);
    }
    
    //批量导入发货结果 Excel格式
    @RequestMapping(value = "/admin/order/stockout/importSendOutForExcel.php", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public @ResponseBody Progress importSendOutForExcel(@RequestParam("file_upload") MultipartFile file, 
                                                        final Website website,
                                                        final WebsiteAdministrator admin, 
                                                        final HttpServletRequest request, final HttpSession session) throws IOException, BiffException
    {
        try (InputStream ins    = file.getInputStream())
        {
            Workbook    workbook    = Workbook.getWorkbook(ins);
            int sheetSize   = workbook.getNumberOfSheets();
            
            final List<StockoutData> result  = new ArrayList<>();
            for (int i = 0; i < sheetSize; ++ i)
            {
                Sheet   sheet   = workbook.getSheet(i);
                
                int rows    = sheet.getRows();
                int columns = sheet.getColumns();
                if (columns < 4)
                {
                    continue;
                }
                for (int r = 1; r < rows; ++ r)
                {
                    StockoutData   data = new StockoutData();
                    data.orderKey            = sheet.getCell(0, r).getContents().trim();
                    data.shipmentCompanyName = sheet.getCell(2, r).getContents().trim();
                    data.shipmentOrder       = sheet.getCell(3, r).getContents().trim();
                    result.add(data);
                }
            }
            workbook.close();
            
            final Progress    progress    = new Progress(result.size(), 0);
            session.setAttribute("BATCH_STOCKOUT_PROGRESS", progress);
            taskExecutor.execute(new Runnable(){
                @Override
                public void run() {
                    Map<String, ShipmentCompany> cacheMap = new HashMap<>();
                    for(StockoutData item : result){
                        ++ progress.progress;
                        ShoppingOrder order = orderService.findOne(item.orderKey);
                        if(order != null  && order.getStatus() == OrderStatus.waiting_shipment)
                        {
                            String  shipmentCompanyName   = item.shipmentCompanyName;
                            ShipmentCompany shipmentCompany = cacheMap.get(shipmentCompanyName);
                            if(shipmentCompany == null){
                                shipmentCompany = shipmentCompanyService.findOne(shipmentCompanyName);
                                if(shipmentCompany == null){
                                    shipmentCompany = findShipmentCompany(shipmentCompanyName);
                                    if(shipmentCompany != null){
                                        cacheMap.put(shipmentCompanyName, shipmentCompany);
                                    }
                                }else{
                                    cacheMap.put(shipmentCompanyName, shipmentCompany);
                                }
                            }
                            if(shipmentCompany == null){
                               continue;
                            }
                            orderService.setToShipment(order, admin, website, shipmentCompany, item.shipmentOrder, request.getRemoteAddr());
                        }
                    }
                    session.removeAttribute("BATCH_UPDATING_PROGRESS");
                }
            });
            return progress;
        }
    }
    
    @RequestMapping(value = "/admin/order/stockout/progress.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public  @ResponseBody Progress progress(HttpSession session)
    {
        Progress progress  = (Progress) session.getAttribute("BATCH_STOCKOUT_PROGRESS");
        if (progress == null)
        {
            return new Progress(100, 100);
        }
        return progress;
    }
    
    private void writeOrdersFormatLMF(OutputStream out, List<ShoppingOrder> orders) throws IOException
    {
        Map<ShoppingOrder, List<OrderEntry>>  entries   = orderService.findEntries(orders);
        //准备获取相关联的SKU信息
        Set<Integer>  storageUnitId    = new HashSet<>();
        for (List<OrderEntry> l : entries.values())
        {
            for (OrderEntry oe : l)
            {
                storageUnitId.add(oe.getStorageUnitId());
            }
        }
        Map<Integer, StorageUnit>  storageUnits = storageUnitService.find(storageUnitId);
        //准备获取相关联的产品信息
        Set<Integer>  productId = new HashSet<>();
        for (StorageUnit sut : storageUnits.values())
        {
            productId.add(sut.getProductId());
        }
        Map<Integer, Product>   products = productService.findAsMap(productId);

        //开始写入快递公司信息
        List<ShipmentCompany>  shipmentCompanies    = shipmentCompanyService.all();
        LMFIOUtil.writeInt(out, shipmentCompanies.size());
        for (ShipmentCompany scp : shipmentCompanies)
        {
            LMFIOUtil.writeString(out, "ID");
            LMFIOUtil.writeInt(out, scp.getId());
            LMFIOUtil.writeString(out, "Name");
            LMFIOUtil.writeString(out, scp.getName());
            LMFIOUtil.writeString(out, "Kuaidi100Code");
            LMFIOUtil.writeString(out, scp.getKuaidi100Code());
        }

        //开始写入文件
        LMFIOUtil.writeInt(out, orders.size());//写入总数
        for (ShoppingOrder sod : orders)
        {
            List<OrderEntry> orderEntries   = entries.get(sod);
            if (orderEntries == null || orderEntries.isEmpty())
            {
                LMFIOUtil.writeInt(out, 0x00);
                continue;
            } else {
                LMFIOUtil.writeInt(out, 0x01);
            }

            //写入订单基本信息
            LMFIOUtil.writeString(out, "OrderID");
            LMFIOUtil.writeLong(out, sod.getId());
            LMFIOUtil.writeString(out, "OrderKey");
            LMFIOUtil.writeString(out, sod.getKey());
            LMFIOUtil.writeString(out, "ReceiverName");
            LMFIOUtil.writeString(out, sod.getReceiverName());
            LMFIOUtil.writeString(out, "ReceiverMobile");
            LMFIOUtil.writeString(out, sod.getReceiverMobile());
            LMFIOUtil.writeString(out, "ReceiverPhone");
            LMFIOUtil.writeString(out, sod.getReceiverPhone());
            LMFIOUtil.writeString(out, "Province");
            LMFIOUtil.writeString(out, sod.getProvince().getName());
            LMFIOUtil.writeString(out, "City");
            LMFIOUtil.writeString(out, sod.getCity().getName());
            LMFIOUtil.writeString(out, "County");
            LMFIOUtil.writeString(out, sod.getCounty().getName());
            if(sod.getTown() != null) {
                LMFIOUtil.writeString(out, sod.getTown().getName());
            } else {
                LMFIOUtil.writeString(out, null);
            }
            LMFIOUtil.writeString(out, "ReceiverAddr");
            LMFIOUtil.writeString(out, sod.getReceiverAddr());
            LMFIOUtil.writeString(out, "Remark");
            LMFIOUtil.writeString(out, sod.getRemark());
            LMFIOUtil.writeString(out, "ShipmentCode");
            LMFIOUtil.writeInt(out, sod.getShipmentCompany() == null ? 0 : sod.getShipmentCompany().getId());

            //写入订单SKU信息
            LMFIOUtil.writeInt(out, orderEntries.size());
            for (OrderEntry ent : orderEntries)
            {
                StorageUnit sku = storageUnits.get(ent.getStorageUnitId());
                Product product = products.get(sku.getProductId());
                LMFIOUtil.writeString(out, "SkuId");
                LMFIOUtil.writeInt(out, sku.getId());
                LMFIOUtil.writeString(out, "BarCode");
                switch(sku.getEffectBarType())
                {
                    case bar_code:
                        LMFIOUtil.writeString(out, sku.getBarCode());
                        break;
                    case outer_bar_code:
                        LMFIOUtil.writeString(out, sku.getOuterBarCode());
                        break;
                    case international_bar_code:
                        LMFIOUtil.writeString(out, sku.getInternationalBarCode());
                        break;
                    default:
                        LMFIOUtil.writeString(out, "未知类型");
                }
                LMFIOUtil.writeString(out, "Property1");
                LMFIOUtil.writeString(out, sku.getProperty1());
                LMFIOUtil.writeString(out, "Property2");
                LMFIOUtil.writeString(out, sku.getProperty2());
                LMFIOUtil.writeString(out, "ProductId");
                LMFIOUtil.writeInt(out, product.getId());
                LMFIOUtil.writeString(out, "ProductName");
                LMFIOUtil.writeString(out, product.getName());
                LMFIOUtil.writeString(out, "Brand");
                LMFIOUtil.writeString(out, product.getBrand().getName());
                LMFIOUtil.writeString(out, "ProductCode");
                LMFIOUtil.writeString(out, product.getProductCode());
                LMFIOUtil.writeString(out, "ProductPlace");
                LMFIOUtil.writeString(out, product.getProductPlace());
                LMFIOUtil.writeString(out, "Amount");
                LMFIOUtil.writeInt(out, ent.getAmount());
                LMFIOUtil.writeString(out, "Thumbnail");
                LMFIOUtil.writeString(out, product.getThumbnailImage());
            }
        }
    }
    
    private void writeOrdersExcel(List<ShoppingOrder> orders, OutputStream out) throws IOException, WriteException
    {
        Map<ShoppingOrder, List<OrderEntry>>  entries   = orderService.findEntries(orders);
        //准备获取相关联的SKU信息
        Set<Integer>  storageUnitId    = new HashSet<>();
        for (List<OrderEntry> l : entries.values())
        {
            for (OrderEntry oe : l)
            {
                storageUnitId.add(oe.getStorageUnitId());
            }
        }
        Map<Integer, StorageUnit>  storageUnits = storageUnitService.find(storageUnitId);
        //准备获取相关联的产品信息
        Set<Integer>  productId = new HashSet<>();
        for (StorageUnit sut : storageUnits.values())
        {
            productId.add(sut.getProductId());
        }
        Map<Integer, Product>   products = productService.findAsMap(productId);
        
        WritableWorkbook   workbook = Workbook.createWorkbook(out);
        WritableSheet   sheet   = workbook.createSheet("订单列表", 0);

        //设置列宽
        sheet.setColumnView(0, 10);
        sheet.setColumnView(1, 10);
        sheet.setColumnView(2, 18);
        sheet.setColumnView(3, 18);
        sheet.setColumnView(4, 18);
        sheet.setColumnView(5, 18);
        sheet.setColumnView(6, 42);
        sheet.setColumnView(7, 22);
        sheet.setColumnView(8, 18);
        sheet.setColumnView(9, 18);
        sheet.setColumnView(10, 32);
        sheet.setColumnView(11, 18);
        sheet.setColumnView(12, 18);
        sheet.setColumnView(13, 12);
        sheet.setColumnView(14, 12);
        sheet.setColumnView(15, 12);//数量
        sheet.setColumnView(16, 32);//备注
        sheet.getSettings().setVerticalFreeze(1);
        sheet.getSettings().setProtected(true);

        //初始化标题
        WritableFont   titleWft  = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
        titleWft.setColour(Colour.BROWN);
        WritableCellFormat titleFmt = new WritableCellFormat(titleWft);
        titleFmt.setBackground(Colour.GRAY_25);
        
        WritableCellFormat intFmt   = new WritableCellFormat(new NumberFormat("#####"));
        
        sheet.addCell(new Label(0, 0, "订单ID", titleFmt));
        sheet.addCell(new Label(1, 0, "订单Key", titleFmt));
        sheet.addCell(new Label(2, 0, "收件人姓名", titleFmt));
        sheet.addCell(new Label(3, 0, "省份", titleFmt));
        sheet.addCell(new Label(4, 0, "市", titleFmt));
        sheet.addCell(new Label(5, 0, "区", titleFmt));
        sheet.addCell(new Label(6, 0, "详细地址", titleFmt));
        sheet.addCell(new Label(7, 0, "手机号/电话", titleFmt));
        sheet.addCell(new Label(8, 0, "发货仓库", titleFmt));
        sheet.addCell(new Label(9, 0, "快递公司", titleFmt));
        
        sheet.addCell(new Label(10, 0, "商品名称", titleFmt));
        sheet.addCell(new Label(11, 0, "品牌", titleFmt));
        sheet.addCell(new Label(12, 0, "型号", titleFmt));
        sheet.addCell(new Label(13, 0, "SKU属性", titleFmt));
        sheet.addCell(new Label(14, 0, "SKU条码", titleFmt));
        sheet.addCell(new Label(15, 0, "数量", titleFmt));
        sheet.addCell(new Label(16, 0, "备注", titleFmt));

        int row = 1;
        for (ShoppingOrder order : orders)
        {
            List<OrderEntry> orderEntries    = entries.get(order);
            int entrySize   = orderEntries.size();
            if (entrySize == 0)
            {
                continue;
            }
            if (entrySize > 1)
            {
                //合并0 ~ 9 列的单元格
                for (int col = 0; col <= 9; ++ col)
                {
                    sheet.mergeCells(col, row, col, row + entrySize - 1);
                }
                sheet.mergeCells(16, row, 16, row + entrySize - 1);
            }
            sheet.addCell(new jxl.write.Number(0, row, order.getId(), intFmt));
            sheet.addCell(new Label(1, row, order.getKey()));
            sheet.addCell(new Label(2, row, order.getReceiverName()));
            sheet.addCell(new Label(3, row, order.getProvince().getName()));
            sheet.addCell(new Label(4, row, order.getCity().getName()));
            sheet.addCell(new Label(5, row, order.getCounty().getName()));
            if(order.getTown() != null) {
                sheet.addCell(new Label(6, row, order.getTown().getName() + order.getReceiverAddr()));
            } else {
                sheet.addCell(new Label(6, row, order.getReceiverAddr()));
            }
            if (order.getReceiverMobile() == null || order.getReceiverMobile().isEmpty())
            {
                sheet.addCell(new Label(7, row, order.getReceiverPhone()));
            } else if (order.getReceiverPhone() == null || order.getReceiverPhone().isEmpty()) {
                sheet.addCell(new Label(7, row, order.getReceiverMobile()));
            } else {
                sheet.addCell(new Label(7, row, new StringBuilder(order.getReceiverMobile()).append('/').append(order.getReceiverPhone()).toString()));
            }
            sheet.addCell(new Label(8, row, order.getShipmentCompany() == null ? "未指定物流公司" : order.getShipmentCompany().getName()));
            sheet.addCell(new Label(9, row, order.getRemark()));
            
            for (int i = 0; i < entrySize; ++ i)
            {
                OrderEntry  entry   = orderEntries.get(i);
                StorageUnit sku = storageUnits.get(entry.getStorageUnitId());
                Product product = products.get(sku.getProductId());
                sheet.addCell(new Label(10, row + i, product.getName()));
                sheet.addCell(new Label(11, row + i, product.getBrand().getName()));
                sheet.addCell(new Label(12, row + i, product.getProductCode()));
                sheet.addCell(new Label(13, row + i, sku.getProperty1()));
                switch(sku.getEffectBarType())
                {
                    case bar_code:
                        sheet.addCell(new Label(14, row + i, sku.getBarCode()));
                        break;
                    case outer_bar_code:
                        sheet.addCell(new Label(14, row + i, sku.getOuterBarCode()));
                        break;
                    case international_bar_code:
                        sheet.addCell(new Label(14, row + i, sku.getInternationalBarCode()));
                        break;
                    default:
                        sheet.addCell(new Label(14, row + i, "未知的条码类型"));
                }
                sheet.addCell(new jxl.write.Number(15, row + i, entry.getAmount(), intFmt));
            }
            row += entrySize;
        }
        workbook.write();
        workbook.close();
    }
    
    public static class Progress implements Serializable
    {
        public  int total;
        public  int progress;

        public Progress(int total, int progress) {
            this.total = total;
            this.progress = progress;
        }
    }
    
    public static class StockoutData implements Serializable
    {
        public String orderKey;
        
        public String shipmentCompanyName;
        
        public String shipmentOrder;
    }
    
    private ShipmentCompany findShipmentCompany(String key)
    {
        if(Kuaidi100CodeMap.containsValue(key)){
            return shipmentCompanyService.findOneViaKuaidi100Code(key);
        }
        return null;
    }
    
    private final static Map<String, String>  Kuaidi100CodeMap   = new HashMap<String, String>(){
        {
            put("圆通速递", "yuantong");
            put("圆通", "yuantong");
            put("圆通快递", "yuantong");
            put("顺丰速运", "shunfeng");
            put("顺丰", "shunfeng");
            put("顺丰快递", "shunfeng");
            put("申通", "shentong");
            put("申通快递", "shentong");
            put("天天快递", "tiantian");
            put("天天", "tiantian");
            put("中通快递", "zhongtong");
            put("中通", "zhongtong");
            put("韵达快递", "yunda");
            put("韵达", "yunda");
            put("EMS", "ems");
            put("ems", "ems");
        }
    };
}
