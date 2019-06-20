/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.statistics.controller;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.Range;
import com.lmf.common.util.CommonUtil;
import com.lmf.common.util.PagerSpec;
import com.lmf.order.enums.NifferOrderStatus;
import com.lmf.order.service.NifferOrderService;
import com.lmf.order.vo.OrderSeparationEntry;
import com.lmf.sys.service.GeoRegionService;
import com.lmf.website.entity.Website;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 *
 * @author shenzhixiong
 */
@Controller
public class ReturnProductStatisticsController {

    @Autowired
    private GeoRegionService geoRegionService;

    @Autowired
    private NifferOrderService nifferOrderService;
    
    @RequestMapping(value = "/admin/statistics/returnProductStatistics.php", method = RequestMethod.GET)
    public String list(@RequestParam(value = "kw", required = false) String keyword,
                                          @RequestParam(value = "st", required = false) Date startDate,
                                          @RequestParam(value = "et", required = false) Date endDate,
                                          @RequestParam(value = "timeType", required = false) String timeType,
                                          @PagerSpecDefaults(pageSize = 20, maxPageSize = 100, checkSortFields = true, sort = "time.desc") PagerSpec pager,
                                          Website website,Model model) throws ParseException, UnsupportedEncodingException {
        StringBuilder link = new StringBuilder("/jdvop/admin/statistics/returnProductStatistics.php?page=[:page]");

        if (keyword != null) {
            link.append("&kw=").append(URLEncoder.encode(keyword, "UTF-8"));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (startDate != null) {
            model.addAttribute("st", sdf.format(startDate));
            link.append("&st").append(sdf.format(startDate));
        }
        if (endDate != null) {
            model.addAttribute("et", sdf.format(endDate));
            link.append("&et").append(sdf.format(endDate));
            Calendar date = Calendar.getInstance();
            date.setTime(endDate);
            date = CommonUtil.filled(date);
            endDate = sdf.parse(sdf.format(date.getTime()));
        }
        Page<OrderSeparationEntry> orderSeparationEntrys;
        if (timeType != null && "confimTime".equals(timeType)) {
            orderSeparationEntrys = nifferOrderService.findReturnedProductsByOriginalConfirmTime(null, new Range<>(startDate, endDate), pager);
        } else {
            orderSeparationEntrys = nifferOrderService.findReturnedProductsByAcceptTime(null, new Range<>(startDate, endDate), pager);
        }
        
        model.addAttribute("orderSeparationEntrys", orderSeparationEntrys);
        model.addAttribute("geoRegions", geoRegionService.findAllProvince());
        model.addAttribute("timeType", timeType);
        model.addAttribute("returnedOrderStatus", NifferOrderStatus.values());
        model.addAttribute("returnedOrNifferOrderService", nifferOrderService);
        model.addAttribute("link", link.toString());
        return "/admin/statistics/returned_products";
    }
    
    
     @RequestMapping(value = "/admin/statistics/returnedexport.php", method = RequestMethod.GET)
    public @ResponseBody void exportAsExcel(@RequestParam(value = "st", required = false) Date startDate,
                                            @RequestParam(value = "et", required = false) Date endDate,
                                            @RequestParam(value = "timeType", required = false) String timeType,
                                            Website website,HttpServletResponse response) throws UnsupportedEncodingException, IOException, WriteException, ParseException {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sbd = new StringBuilder();
        if(startDate != null && endDate == null){
            sbd.append(sd.format(startDate));
        }else if(endDate != null && startDate == null){
            sbd.append(sd.format(endDate));
        }else if(startDate != null && endDate != null && !startDate.equals(endDate)){
            String st = sd.format(startDate);
            String et = sd.format(endDate);
            sbd.append(st).append("-").append(et);
        }else if(startDate != null && endDate != null && startDate.equals(endDate)){
            sbd.append(sd.format(startDate));
        }  
        sbd.append(website.getName()).append("退货单明细");
        String title = sbd.toString();
        response.setContentType("application/force-download");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.addHeader("Content-Type", "application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", new StringBuilder("attachment;filename=").append(URLEncoder.encode(title, "UTF-8")).append(".xls").toString());
        try (OutputStream out = response.getOutputStream()) {
            WritableWorkbook workbook = Workbook.createWorkbook(out);
            WritableSheet sheet = workbook.createSheet("导出退货单", 0);

            //设置列宽
           sheet.setColumnView(0, 20);//条码
            sheet.setColumnView(1, 10);//产品ID
            sheet.setColumnView(2, 30);//产品名称
            sheet.setColumnView(3, 25);//品牌
            sheet.setColumnView(4, 20);//产品类型
            sheet.setColumnView(5, 20);//SKU_ID
            sheet.setColumnView(6, 20);//SKU属性1
            sheet.setColumnView(7, 20);//SKU属性2
            sheet.setColumnView(8, 15);//退货数量

            sheet.getSettings().setVerticalFreeze(1);

            //初始化标题
            WritableFont titleWft = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
            titleWft.setColour(Colour.BROWN);
            WritableCellFormat titleFmt = new WritableCellFormat(titleWft);
            titleFmt.setBackground(Colour.GRAY_25);
            titleFmt.setAlignment(Alignment.CENTRE);
            titleFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
            WritableCellFormat priceFmt = new WritableCellFormat(new NumberFormat("##,###.00"));
            WritableCellFormat intFmt = new WritableCellFormat(new NumberFormat("#####"));

            //写标题
            sheet.addCell(new Label(0, 0, "条码", titleFmt));
            sheet.addCell(new Label(1, 0, "产品ID", titleFmt));
            sheet.addCell(new Label(2, 0, "产品名称", titleFmt));
            sheet.addCell(new Label(3, 0, "品牌", titleFmt));
            sheet.addCell(new Label(4, 0, "产品类型", titleFmt));
            sheet.addCell(new Label(5, 0, "SKU_ID", titleFmt));
            sheet.addCell(new Label(6, 0, "SKU属性1", titleFmt));
            sheet.addCell(new Label(7, 0, "SKU属性2", titleFmt));
            sheet.addCell(new Label(8, 0, "退货数量", titleFmt));
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (endDate != null) {
                Calendar date = Calendar.getInstance();
                date.setTime(endDate);
                date = CommonUtil.filled(date);
                endDate = sd.parse(sdf.format(date.getTime()));
            }
            Page<OrderSeparationEntry> orderSeparationEntrys;
            if ("confimTime".equals(timeType)) {
                orderSeparationEntrys = nifferOrderService.findReturnedProductsByOriginalConfirmTime(null, new Range<>(startDate, endDate), null);
            } else {
                orderSeparationEntrys = nifferOrderService.findReturnedProductsByAcceptTime(null, new Range<>(startDate, endDate), null);
            }
            if (orderSeparationEntrys != null) {
                int row = 1;
                for (OrderSeparationEntry ose : orderSeparationEntrys) {
                    sheet.addCell(new Label(0, row, ose.getSkuBarCode()));
                    sheet.addCell(new jxl.write.Number(1, row, ose.getProductId(), intFmt));
                    sheet.addCell(new Label(2, row, ose.getProductName()));
                    if (ose.getBrand() != null) {
                        sheet.addCell(new Label(3, row, ose.getBrand().getName()));
                    }
                    sheet.addCell(new Label(4, row, ose.getCate().getName()));
                    sheet.addCell(new jxl.write.Number(5,row,ose.getSkuId()));
                    sheet.addCell(new Label(6,row,ose.getSkuProperty1()));
                    sheet.addCell(new Label(7,row,ose.getSkuProperty2()));
                    sheet.addCell(new jxl.write.Number(8, row, ose.getAmount()));
                    ++row;
                }
            }
            workbook.write();
            workbook.close();
        }
    }
    
    
    
}
