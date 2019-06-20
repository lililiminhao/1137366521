package com.lmf.integral.admin.statistics.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lmf.common.Page;
import com.lmf.common.tuple.BinaryTuple;
import com.lmf.common.util.CommonUtil;
import com.lmf.order.entity.OrderPayLog;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.service.OrderService;
import com.lmf.order.vo.OrderCriteria;
import com.lmf.order.vo.OrderReportVO;
import com.lmf.website.entity.Website;

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
import jxl.write.biff.RowsExceededException;

@Controller
public class OrderReportController {
	
	@Autowired
    private OrderService orderService;
	
	@RequiresPermissions("statistics:view")
	@RequestMapping(value="/admin/statistics/report.php", method=RequestMethod.GET)
	public String orderReport(@RequestParam(value = "st", required = false ) Date startTime,
							  @RequestParam(value = "et", required = false) Date endTime,
							  Model model){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<OrderReportVO> orderReportInfo = orderService.findOrderRepot(startTime, endTime);
		model.addAttribute("orderReportInfo", orderReportInfo);
		if(startTime != null){
			model.addAttribute("st", sdf.format(startTime));
		}
		if(endTime != null){
			model.addAttribute("et", sdf.format(endTime));
		}
		return "/admin/statistics/report";
	}
	
	@RequestMapping(value="/admin/statistics/reportExport.php", method = RequestMethod.GET)
	public @ResponseBody void export(@RequestParam(value = "st", required = false ) Date startDate,
			  						 @RequestParam(value = "et", required = false) Date endDate,
			  						 Website website, HttpServletResponse response) throws RowsExceededException, WriteException, IOException, ParseException{
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		
		List<OrderReportVO> orderReportInfo = orderService.findOrderRepot(startDate, endDate);
        
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
		sbd.append(website.getName()).append("订单报表");
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
            sheet.setColumnView(0, 25);//时间
            sheet.setColumnView(1, 25);//订单总数
            sheet.setColumnView(2, 25);//成交金额
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
            sheet.addCell(new Label(0, 0, "时间", titleFmt));
            sheet.addCell(new Label(1, 0, "订单总数", titleFmt));
            sheet.addCell(new Label(2, 0, "成交金额", titleFmt));
            if (endDate != null) {
                Calendar date = Calendar.getInstance();
                date.setTime(endDate);
                date = CommonUtil.filled(date);
                endDate = sd.parse(sd.format(date.getTime())); 
            }
            int row = 1;
            for (OrderReportVO orderReportVO : orderReportInfo) {
                sheet.addCell(new Label(0, row, sd.format( orderReportVO.getDate())));
                sheet.addCell(new jxl.write.Number(1, row, orderReportVO.getOrderTotal()));
                sheet.addCell(new jxl.write.Number(2, row, orderReportVO.getTotalPrice(), priceFmt));
                ++row;
            }
            workbook.write();
            workbook.close();
        }
	}
}
