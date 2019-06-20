/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.statistics.controller;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.util.CommonUtil;
import com.lmf.common.util.LMFMathUtils;
import com.lmf.common.util.PagerSpec;
import com.lmf.order.entity.NifferOrder;
import com.lmf.order.entity.NifferOrderStatistics;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.enums.NifferOrderStatus;
import com.lmf.order.enums.OrderPayType;
import com.lmf.order.service.NifferOrderService;
import com.lmf.order.service.OrderService;
import com.lmf.order.vo.NifferOrderCriteria;
import com.lmf.system.sdk.enums.AfterSaleType;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.service.WebsiteUserService;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 退款对账
 * @author lianwukun
 */

@Controller
public class ReturnStatisticsController {

    @Autowired
    private NifferOrderService nifferOrderService;
    
    @RequiresPermissions("duizhang:refund_view")
    @RequestMapping(value = "/admin/statistics/return.php", method = RequestMethod.GET)
    public String list(@RequestParam(value = "st", required = false) Date startDate,
                       @RequestParam(value = "et", required = false) Date endDate,
                       @RequestParam(value = "pt", required = false) OrderPayType payType,
                       @PagerSpecDefaults(pageSize = 20, maxPageSize = 100) PagerSpec pager,
                       Website website,Model model) throws ParseException, UnsupportedEncodingException {

        StringBuilder link = new StringBuilder("/jdvop/admin/statistics/return.php?page=[:page]");
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
        model.addAttribute("nifferOrders", nifferOrderService.findNifferOrderStatics(new NifferOrderCriteria().withAcceptTime(startDate, endDate).withAfterSaleType(AfterSaleType.returned).withStatus(NifferOrderStatus.accepted).withOrderPayType(payType), pager));
        model.addAttribute("link", link.toString());
        return "admin/statistics/return";
    }
    
    @RequiresPermissions("duizhang:refund_export")
    @RequestMapping(value = "/admin/statistics/returnExport.php", method = RequestMethod.GET)
    public @ResponseBody void exportAsExcel(@RequestParam(value = "st", required = false) Date startDate,
                                            @RequestParam(value = "et", required = false) Date endDate,
                                            @RequestParam(value = "pt", required = false) OrderPayType payType,
                                            @PagerSpecDefaults(pageSize = 999999999) PagerSpec pager,
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
        sbd.append(website.getName()).append("退款明细");
        String title = sbd.toString();
        response.setContentType("application/force-download");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.addHeader("Content-Type", "application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", new StringBuilder("attachment;filename=").append(URLEncoder.encode(title, "UTF-8")).append(".xls").toString());
        try (OutputStream out = response.getOutputStream()) {
            WritableWorkbook workbook = Workbook.createWorkbook(out);
            WritableSheet  sheet = workbook.createSheet("退款数据", 0);

            //设置列宽
            sheet.setColumnView(0, 20);//退款编号
            sheet.setColumnView(1, 20);//订单编号
            sheet.setColumnView(2, 30);//退款账号
            sheet.setColumnView(3, 16);//支付金额
            sheet.setColumnView(4, 16);//退款金额
            sheet.setColumnView(5, 16);//支付方式
            sheet.setColumnView(6, 24);//退款完成时间
            
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
            sheet.addCell(new Label(0, 0, "退款编号", titleFmt));
            sheet.addCell(new Label(1, 0, "订单编号", titleFmt));
            sheet.addCell(new Label(2, 0, "退款账号", titleFmt));
            sheet.addCell(new Label(3, 0, "支付金额", titleFmt));
            sheet.addCell(new Label(4, 0, "退款金额", titleFmt));
            sheet.addCell(new Label(5, 0, "支付方式", titleFmt));
            sheet.addCell(new Label(6, 0, "退款完成时间", titleFmt));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (endDate != null) {
                Calendar date = Calendar.getInstance();
                date.setTime(endDate);
                date = CommonUtil.filled(date);
                endDate = sdf.parse(sdf.format(date.getTime())); 
            }
            
            Page<NifferOrderStatistics> nifferOrderPage = nifferOrderService.findNifferOrderStatics(new NifferOrderCriteria().withAcceptTime(startDate, endDate).withAfterSaleType(AfterSaleType.returned).withStatus(NifferOrderStatus.accepted).withOrderPayType(payType), pager);

            int row = 1;
            for (NifferOrderStatistics nifferOrder : nifferOrderPage) {
                sheet.addCell(new Label(0, row, nifferOrder.getAfterSaleOrderKey()));
                sheet.addCell(new Label(1, row, nifferOrder.getOrderKey()));
                sheet.addCell(new Label(2, row, nifferOrder.getLoginName()));
                sheet.addCell(new jxl.write.Number(3, row, nifferOrder.getNeedPay(), priceFmt));
                sheet.addCell(new jxl.write.Number(4, row, LMFMathUtils.add(nifferOrder.getRefundAmount(), nifferOrder.getShipmentFee()), priceFmt));
                sheet.addCell(new Label(5, row, nifferOrder.getPayType().getDescription()));
                sheet.addCell(new Label(6, row, sdf.format(nifferOrder.getAcceptTime())));
                ++row;
            }
            
            workbook.write();
            workbook.close();
        }
    }
    

    
    
}
