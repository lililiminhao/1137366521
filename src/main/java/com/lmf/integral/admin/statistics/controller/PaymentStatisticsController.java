/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.statistics.controller;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.enums.StaticsPeriod;
import com.lmf.common.tuple.BinaryTuple;
import com.lmf.common.util.CommonUtil;
import com.lmf.common.util.PagerSpec;
import com.lmf.extend.pay.smkpay.SmkpayUtil;
import com.lmf.extend.pay.smkpay.json.SmkSheetDownloadJson;
import com.lmf.extend.pay.smkpay.pojo.SheetDownloadResponse;
import com.lmf.extend.pay.smkpay.util.CertificateCoder;
import com.lmf.extend.pay.tsmpay.TsmpayUtil;
import com.lmf.extend.pay.tsmpay.json.DailySettleResponseJson;
import com.lmf.integral.SystemConfig;
import com.lmf.order.entity.OrderPayLog;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.enums.OrderPayType;
import com.lmf.order.enums.OrderStatus;
import com.lmf.order.service.OrderService;
import com.lmf.order.vo.OrderCriteria;
import com.lmf.sys.entity.GeoRegion;
import com.lmf.website.entity.Website;
import com.lmf.website.service.WebsiteUserService;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author shenzhixiong
 */

@Controller
public class PaymentStatisticsController {

    @Autowired
    private WebsiteUserService websiteUserService;

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private SmkpayUtil smkpayUtil;
    
    @Autowired
    private SystemConfig systemConfig;
    
    @Autowired
    private TsmpayUtil tsmpayUtil;
    
    @RequiresPermissions("duizhang:pay_view")
    @RequestMapping(value = "/admin/statistics/payment.php", method = RequestMethod.GET)
    public String list(@RequestParam(value = "kw", required = false) String keyword,
                       @RequestParam(value = "st", required = false) Date startDate,
                       @RequestParam(value = "et", required = false) Date endDate,
                       @RequestParam(value = "pt", required = false) OrderPayType payType,
                       @PagerSpecDefaults(pageSize = 20, maxPageSize = 100, allowSortFileds = {"time", "owner", "lastModifyTime"}, checkSortFields = true, sort = "time.desc") PagerSpec pager,
                       Website website,Model model) throws ParseException, UnsupportedEncodingException {
         
        StringBuilder link = new StringBuilder("/jdvop/admin/statistics/payment.php?page=[:page]");

        if (keyword != null) {
            link.append("&kw=").append(URLEncoder.encode(keyword, "UTF-8"));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (startDate != null) {
            model.addAttribute("st", sdf.format(startDate));
            link.append("&st=").append(sdf.format(startDate));
        }
        if (endDate != null) {
            model.addAttribute("et", sdf.format(endDate));
            link.append("&et=").append(sdf.format(endDate));
            Calendar date = Calendar.getInstance();
            date.setTime(endDate);
            date = CommonUtil.filled(date);
            endDate = sdf.parse(sdf.format(date.getTime()));
        }
        if(payType != null) {
            link.append("&pt=").append(payType);
            model.addAttribute("payType", payType);
        }
        model.addAttribute("payLogs", orderService.findOrderAndPayLog(new OrderCriteria().withKeyword(keyword).withPayTime(startDate, endDate).withPayType(payType).withPayLogQuery(true), pager));
        model.addAttribute("websiteUserService", websiteUserService);
        model.addAttribute("status", OrderStatus.values());
        model.addAttribute("payTypes", OrderPayType.values());
        model.addAttribute("link", link.toString());

        return "admin/statistics/payment";
    }
    @RequiresPermissions("duizhang:pay_export")
    @RequestMapping(value = "/admin/statistics/payexport.php", method = RequestMethod.GET)
    public @ResponseBody void exportAsExcel(@RequestParam(value = "kw", required = false) String keyword,
                                            @RequestParam(value = "sp", required = false) StaticsPeriod staticsPeriod,
                                            @RequestParam(value = "area", required = false) GeoRegion area,
                                            @RequestParam(value = "pt", required = false) OrderPayType payType,
                                            @RequestParam(value = "status", required = false) OrderStatus[] status,
                                            @RequestParam(value = "st", required = false) Date startDate,
                                            @RequestParam(value = "et", required = false) Date endDate,
                                            @RequestParam(value = "timeType", required = false) String timeType,
                                            @PagerSpecDefaults(pageSize = 999999999, sort = "time.desc") PagerSpec pager,
                                            Website website,HttpServletResponse response) throws UnsupportedEncodingException, IOException, WriteException, ParseException {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sbd  =   new StringBuilder();
        if(startDate != null && endDate == null){
            sbd.append(sd.format(startDate));
        }else if(endDate != null && startDate == null){
            sbd.append(sd.format(endDate));
        }else if(startDate != null && endDate != null  && !startDate.equals(endDate)){
            String st = sd.format(startDate);
            String et = sd.format(endDate);
            sbd.append(st).append("-").append(et);
        }else if(startDate != null && endDate != null && startDate.equals(endDate)){
            sbd.append(sd.format(startDate));
        }  
        sbd.append(website.getName()).append("支付明细");
        String title = sbd.toString();
        response.setContentType("application/force-download");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.addHeader("Content-Type", "application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", new StringBuilder("attachment;filename=").append(URLEncoder.encode(title, "UTF-8")).append(".xls").toString());
        try (OutputStream out = response.getOutputStream()) {
            WritableWorkbook workbook = Workbook.createWorkbook(out);
            WritableSheet  sheet = workbook.createSheet("支付数据", 0);

            //设置列宽
            sheet.setColumnView(0, 20);//订单编号
            sheet.setColumnView(1, 16);//交易流水号
            sheet.setColumnView(2, 16);//支付方式
            sheet.setColumnView(3, 16);//需付款金额
            sheet.setColumnView(4, 16);//最终支付金额
            sheet.setColumnView(5, 24);//支付时间  or  完成时间
            
            
            sheet.getSettings().setVerticalFreeze(1);

            //初始化标题
            WritableFont titleWft = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
            titleWft.setColour(Colour.BROWN);
            WritableCellFormat titleFmt = new WritableCellFormat(titleWft);
            titleFmt.setBackground(Colour.GRAY_25);
            titleFmt.setAlignment(Alignment.CENTRE);
            titleFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
            WritableCellFormat priceFmt = new WritableCellFormat(new NumberFormat("#0.00"));

            //写标题
            sheet.addCell(new Label(0, 0, "订单编号", titleFmt));
            sheet.addCell(new Label(1, 0, "交易流水号", titleFmt));
            sheet.addCell(new Label(2, 0, "支付方式", titleFmt));
            sheet.addCell(new Label(3, 0, "支付金额", titleFmt));
            sheet.addCell(new Label(4, 0, "订单总金额", titleFmt));
            sheet.addCell(new Label(5, 0, "支付时间", titleFmt));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (endDate != null) {
                Calendar date = Calendar.getInstance();
                date.setTime(endDate);
                date = CommonUtil.filled(date);
                endDate = sdf.parse(sdf.format(date.getTime())); 
            }
            Page<BinaryTuple<ShoppingOrder, OrderPayLog>>  payTimeList = orderService.findOrderAndPayLog(new OrderCriteria().withKeyword(keyword).withRegion(area).withPayTime(startDate, endDate).withStatus(status).withPayType(payType).withPayLogQuery(true), pager);
            int row = 1;
            for (BinaryTuple<ShoppingOrder, OrderPayLog> oop : payTimeList) {
                sheet.addCell(new Label(0, row, oop.getValue1().getKey()));
                sheet.addCell(new Label(1, row, oop.getValue2().getOuterOrderKey()));
                sheet.addCell(new Label(2, row, oop.getValue2().getPayType().getDescription()));
                sheet.addCell(new jxl.write.Number(3, row, oop.getValue1().getNeedPay(), priceFmt));
                sheet.addCell(new jxl.write.Number(4, row, oop.getValue1().getTotalCost(), priceFmt));
                sheet.addCell(new Label(5, row, sdf.format( oop.getValue2().getPaiedTime())));
                ++row;
            }
            workbook.write();
            workbook.close();
        }
    }

    
    //市民卡支付对账导出页面
    @RequestMapping(value = "/admin/statistics/smkPaymentExport.php" , method = RequestMethod.GET)
    public String smkPaymentExport(Model model){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1); //得到前一天
        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        model.addAttribute("downloadTime",simpleDateFormat.format(date));
        return "/admin/statistics/smk_payment_export";
    }
    
    @RequestMapping(value = "/admin/statistics/smkPaymentExport.php", method = RequestMethod.POST)
    public @ResponseBody void smkPaymentExport(@RequestParam(value = "st", required = false) Date startDate,
                                            Website website,HttpServletResponse response) throws UnsupportedEncodingException, Exception{
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        StringBuilder sbd  =   new StringBuilder();
        if(startDate != null){
            sbd.append(sd.format(startDate));
        }
        sbd.append(website.getName()).append("市民卡支付对账单");
        String title = sbd.toString();
        response.setContentType("application/force-download");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.addHeader("Content-Type", "application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", new StringBuilder("attachment;filename=").append(URLEncoder.encode(title, "UTF-8")).append(".xls").toString());
        OutputStream out = response.getOutputStream();
        WritableWorkbook workbook = Workbook.createWorkbook(out);
        WritableSheet  sheet = workbook.createSheet("市民卡支付对账单", 0);

        //设置列宽
        sheet.setColumnView(0, 30);//交易流水号
        sheet.setColumnView(1, 30);//订单号
        sheet.setColumnView(2, 20);//金额
        sheet.setColumnView(3, 30);//交易时间
        sheet.setColumnView(4, 16);//状态
        sheet.setColumnView(5, 16);//交易类型

        sheet.getSettings().setVerticalFreeze(1);

        //初始化标题
        WritableFont titleWft = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
        titleWft.setColour(Colour.BROWN);
        WritableCellFormat titleFmt = new WritableCellFormat(titleWft);
        titleFmt.setBackground(Colour.GRAY_25);
        titleFmt.setAlignment(Alignment.CENTRE);
        titleFmt.setVerticalAlignment(VerticalAlignment.CENTRE);

        //写标题
        sheet.addCell(new Label(0, 0, "交易流水号", titleFmt));
        sheet.addCell(new Label(1, 0, "订单号", titleFmt));
        sheet.addCell(new Label(2, 0, "金额(单位:元)", titleFmt));
        sheet.addCell(new Label(3, 0, "交易时间", titleFmt));
        sheet.addCell(new Label(4, 0, "状态", titleFmt));
        sheet.addCell(new Label(5, 0, "交易类型", titleFmt));

        //组装请求数据
        String reqSeq = UUID.randomUUID().toString().replaceAll("-", "");
        SmkSheetDownloadJson queryRequestJson = new SmkSheetDownloadJson();
        queryRequestJson.reqSeq = reqSeq;
        queryRequestJson.merCode = systemConfig.getMerCode();
        queryRequestJson.date = sd.format(startDate);
        queryRequestJson.sign = CertificateCoder.sign(queryRequestJson.getMersign(), systemConfig.getPfxPassword());
        List<SheetDownloadResponse> sheetDownloads = null;
        try {
            sheetDownloads = smkpayUtil.sheetDownload(systemConfig.getSmkpayRefundUrl(), queryRequestJson);
            sheetDownloads.sort(Comparator.comparing(SheetDownloadResponse::getTime));
            int row = 1;
            for (SheetDownloadResponse sheetDownload : sheetDownloads) {
                sheet.addCell(new Label(0, row, sheetDownload.getSerialNo()));
                sheet.addCell(new Label(1, row, sheetDownload.getOrderKey()));
                sheet.addCell(new jxl.write.Number(2, row, sheetDownload.getAmount()));
                sheet.addCell(new Label(3, row, sheetDownload.getTime()));
                sheet.addCell(new Label(4, row, (sheetDownload.getStatus()==1?"成功":"失败" )));
                sheet.addCell(new Label(5, row, (sheetDownload.getTradeType()==0?"支付":"退款")));
                ++row;
            }
        } catch (Exception ex) {
            Logger.getLogger(ReturnStatisticsController.class.getName()).log(Level.SEVERE, "", ex);
        }
        workbook.write();
        workbook.close();
    }
    
    
    //tsm支付对账导出页面
    @RequestMapping(value = "/admin/statistics/tsmPaymentExport.php" , method = RequestMethod.GET)
    public String tsmPaymentExport(Model model){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1); //得到前一天
        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        model.addAttribute("downloadTime",simpleDateFormat.format(date));
        return "/admin/statistics/tsm_payment_export";
    }
    
    @RequestMapping(value = "/admin/statistics/tsmPaymentExport.php", method = RequestMethod.POST)
    public @ResponseBody void tsmPaymentExport(@RequestParam(value = "st", required = false) Date startDate,
                                            Website website,HttpServletResponse response) throws UnsupportedEncodingException, Exception{
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        StringBuilder sbd  =   new StringBuilder();
        if(startDate != null){
            sbd.append(sd.format(startDate));
        }
        sbd.append(website.getName()).append("众城付支付对账单");
        String title = sbd.toString();
        response.setContentType("application/force-download");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.addHeader("Content-Type", "application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", new StringBuilder("attachment;filename=").append(URLEncoder.encode(title, "UTF-8")).append(".xls").toString());
        OutputStream out = response.getOutputStream();
        WritableWorkbook workbook = Workbook.createWorkbook(out);
        WritableSheet  sheet = workbook.createSheet("众城付支付对账单", 0);

        //设置列宽
        sheet.setColumnView(0, 30);//订单号
        sheet.setColumnView(1, 21);//订单金额
        sheet.setColumnView(2, 30);//交易流水号
        sheet.setColumnView(3, 12);//交易类型
        sheet.setColumnView(4, 21);//交易金额
        sheet.setColumnView(5, 21);//交易时间
        sheet.setColumnView(6, 20);//支付方式
        
        sheet.getSettings().setVerticalFreeze(1);

        //初始化标题
        WritableFont titleWft = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
        titleWft.setColour(Colour.BROWN);
        WritableCellFormat titleFmt = new WritableCellFormat(titleWft);
        titleFmt.setBackground(Colour.GRAY_25);
        titleFmt.setAlignment(Alignment.CENTRE);
        titleFmt.setVerticalAlignment(VerticalAlignment.CENTRE);

        //写标题
        sheet.addCell(new Label(0, 0, "订单号", titleFmt));
        sheet.addCell(new Label(1, 0, "订单金额(单位：元)", titleFmt));
        sheet.addCell(new Label(2, 0, "交易流水号", titleFmt));
        sheet.addCell(new Label(3, 0, "交易类型", titleFmt));
        sheet.addCell(new Label(4, 0, "交易金额(单位:元)", titleFmt));
        sheet.addCell(new Label(5, 0, "交易时间", titleFmt));
        sheet.addCell(new Label(6, 0, "支付方式", titleFmt));
        //组装请求数据
        
        try {
            List<DailySettleResponseJson> dailySettles = tsmpayUtil.dailySettle(startDate);
            dailySettles.sort(Comparator.comparing(DailySettleResponseJson :: getOrderPayTime));
            int row = 1;
            for (DailySettleResponseJson dailySettle : dailySettles) {
                sheet.addCell(new Label(0, row, dailySettle.getMerchantOrderNo()));//订单号
                sheet.addCell(new jxl.write.Number(1, row, (double)dailySettle.getOrderAmt()/100));//订单金额
                sheet.addCell(new Label(2, row, dailySettle.getPcOrderNo()));//交易流水号
                sheet.addCell(new Label(3, row, (dailySettle.getTradeType()==0?"支付":"退款" )));//交易类型
                sheet.addCell(new jxl.write.Number(4, row, (double)dailySettle.getTradeAmt()/100));//交易金额
                sheet.addCell(new Label(5, row, dailySettle.getOrderPayTime()));//交易时间
                sheet.addCell(new Label(6, row, dailySettle.getPayChannel().getDescription()));//支付方式
                ++row;
            }
        } catch (Exception ex) {
            Logger.getLogger(ReturnStatisticsController.class.getName()).log(Level.SEVERE, "", ex);
        }
        workbook.write();
        workbook.close();
    }
    
}
