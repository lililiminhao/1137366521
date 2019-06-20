package com.lmf.integral.admin.market.controller;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.ResponseResult;
import com.lmf.common.util.CommonUtil;
import com.lmf.common.util.PagerSpec;
import com.lmf.market.entity.FenxiaoMoneyLog;
import com.lmf.market.entity.SecondFenxiaoMoneyLog;
import com.lmf.market.repository.vo.MoneyLogCriteria;
import com.lmf.market.service.FenxiaoMoneyLogService;
import com.lmf.market.service.SecondFenxiaoMoneyLogService;
import com.lmf.website.entity.Website;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Controller
public class FenxiaoMoneyLogController {

	@Autowired
	private FenxiaoMoneyLogService fenxiaoMoneyLogService;

	@Autowired
	private SecondFenxiaoMoneyLogService secondFenxiaoMoneyLogService;
	@RequiresPermissions(value={"yongjin:smk_view","yongjin:smk_view"},logical=Logical.OR)
	@RequestMapping(value = "/admin/fenxiao/moneyLogList.php", method = RequestMethod.GET)
	public @ResponseBody ResponseResult pageQuery(
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestParam(value = "pageNum", required = false)Integer pageNum,
			@RequestParam(value = "type", required = false) String type,
			@PagerSpecDefaults(pageSize = 20, maxPageSize = 100, sort = "createTime.desc") PagerSpec page,
			Model model) {
		page.setCurrentPage(pageNum);
		StringBuilder link = new StringBuilder(
				"/jdvop/admin/statistics/couponList.php?page=[:page]");
		MoneyLogCriteria criteria = new MoneyLogCriteria();
		if (StringUtils.isNotBlank(key)) {
			link.append("&ky=").append(key);
			model.addAttribute("key", key);
			criteria.key = key;
		}
		if (StringUtils.isNotBlank(startTime)) {
			model.addAttribute("startTime", startTime);
			link.append("&st=").append(startTime);
			criteria.startTime = startTime;
		}
		if (StringUtils.isNotBlank(endTime)) {
			model.addAttribute("endTime", endTime);
			link.append("&et=").append(endTime);
			criteria.endTime = endTime;
		}
		if (StringUtils.isNotBlank(type)) {
			model.addAttribute("type", type);
			link.append("&type=").append(type);
			criteria.type = type;
		}

		Page<FenxiaoMoneyLog> fenxiaoMoneyLogs = fenxiaoMoneyLogService.select(
				criteria, page);
		model.addAttribute("link", link.toString());
		return new ResponseResult("1", "",fenxiaoMoneyLogs);
	}

	@RequiresPermissions(value={"yongjin:fenxiao_export","yongjin:smk_export"},logical=Logical.OR)
	@RequestMapping(value = "/admin/fenxiao/exportLog.php", method = RequestMethod.GET)
	public @ResponseBody void smkPaymentExport(
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "startTime", required = false) String startDate,
			@RequestParam(value = "endTime", required = false) Date endDate,
			@RequestParam(value = "type", required = false) String type,
			@PagerSpecDefaults(pageSize = 999999999, sort = "time.desc") PagerSpec page,
			Website website, HttpServletResponse response)
			throws UnsupportedEncodingException, Exception {
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        StringBuilder sbd  =   new StringBuilder();
        if(StringUtils.isNotBlank(startDate) && endDate == null){
            sbd.append(sd.format(startDate));
        }else if(endDate != null && StringUtils.isBlank(startDate)){
            sbd.append(sd.format(endDate));
        }else if(StringUtils.isNotBlank(startDate) && endDate != null  && !startDate.equals(endDate)){
            String st = sd.format(startDate);
            String et = sd.format(endDate);
            sbd.append(st).append("-").append(et);
        }else if(StringUtils.isNotBlank(startDate) && endDate != null && startDate.equals(endDate)){
            sbd.append(sd.format(startDate));
        }  
        sbd.append(website.getName()).append("佣金明细");
        String title = sbd.toString();
        response.setContentType("application/force-download");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.addHeader("Content-Type", "application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", new StringBuilder("attachment;filename=").append(URLEncoder.encode(title, "UTF-8")).append(".xls").toString());
        try (OutputStream out = response.getOutputStream()) {
            WritableWorkbook workbook = Workbook.createWorkbook(out);
            WritableSheet  sheet = workbook.createSheet("佣金数据", 0);
            //设置列宽
            sheet.setColumnView(0, 20);//订单编号
            sheet.setColumnView(1, 16);//交易流水号
            sheet.setColumnView(2, 20);//支付方式
            sheet.setColumnView(3, 16);//需付款金额
            sheet.setColumnView(4, 16);//最终支付金额
            sheet.setColumnView(5, 20);//支付时间  or  完成时间
            sheet.setColumnView(6, 35);//支付时间  or  完成时间
            sheet.setColumnView(7, 10);//支付时间  or  完成时间
            sheet.setColumnView(8, 24);//支付时间  or  完成时间
            sheet.setColumnView(9, 20);//支付时间  or  完成时间

            
            sheet.getSettings().setVerticalFreeze(1);

            //初始化标题
            WritableFont titleWft = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
            titleWft.setColour(Colour.BROWN);
            WritableCellFormat titleFmt = new WritableCellFormat(titleWft);
            titleFmt.setBackground(Colour.GRAY_25);
            titleFmt.setAlignment(Alignment.CENTRE);
            titleFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
			WritableCellFormat titleFmt1 = new WritableCellFormat();
            titleFmt1.setAlignment(Alignment.LEFT);
            WritableCellFormat titleFmt2 = new WritableCellFormat();
            titleFmt1.setAlignment(Alignment.RIGHT);
            WritableCellFormat priceFmt = new WritableCellFormat(new NumberFormat("#0.00"));

			//写标题
			sheet.addCell(new Label(0, 0, "一级分销商姓名", titleFmt));
			sheet.addCell(new Label(1, 0, "一级分销商手机", titleFmt));
			sheet.addCell(new Label(2, 0, "一级分销商对应佣金", titleFmt));
			sheet.addCell(new Label(3, 0, "二级分销商姓名", titleFmt));
			sheet.addCell(new Label(4, 0, "二级分销商手机", titleFmt));
			sheet.addCell(new Label(5, 0, "二级分销商对应佣金", titleFmt));
			sheet.addCell(new Label(6, 0, "分销商明细", titleFmt));
			sheet.addCell(new Label(7, 0, "佣金类型", titleFmt));
			sheet.addCell(new Label(8, 0, "时间", titleFmt));
			sheet.addCell(new Label(9, 0, "市民卡对应佣金", titleFmt));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (endDate != null) {
                Calendar date = Calendar.getInstance();
                date.setTime(endDate);
                date = CommonUtil.filled(date);
                endDate = sdf.parse(sdf.format(date.getTime())); 
            }
            
            MoneyLogCriteria criteria = new MoneyLogCriteria();
    		if (StringUtils.isNotBlank(key)) {
    			criteria.key = key;
    		}
    		if (StringUtils.isNotBlank(startDate)) {
    			criteria.startTime = startDate;
    		}
    		if (endDate != null) {
    			criteria.endTime = sdf.format(endDate);
    		}
    		if (StringUtils.isNotBlank(type)) {
    			criteria.type = type;
    		}
    		page.setCurrentPage(1);
			Page<SecondFenxiaoMoneyLog> fenxiaoMoneyLogs=secondFenxiaoMoneyLogService.findExport(criteria,page);
			int row = 1;
			for (SecondFenxiaoMoneyLog log : fenxiaoMoneyLogs) {
				if(StringUtils.isBlank(log.getFirstfenxiaoName())||log.getFirstfenxiaoName().equals("0")){
					sheet.addCell(new Label(0, row, "无",titleFmt1));
				}else {
					sheet.addCell(new Label(0, row, log.getFirstfenxiaoName()));
				}
				if(StringUtils.isBlank(log.getFirstMobile())||log.getFirstMobile().equals("0")){
					sheet.addCell(new Label(1, row, "无",titleFmt1));
				}else{
					sheet.addCell(new Label(1, row, log.getFirstMobile()));
				}
				if(log.getFirstMoney()==0.0){
					sheet.addCell(new Label(2, row, "无",titleFmt1));
				}else{
					sheet.addCell(new jxl.write.Number(2, row, log.getFirstMoney(), priceFmt));
				}
				if(StringUtils.isBlank(log.getSecondfenxiaoName())||log.getSecondfenxiaoName().equals("0")){
					sheet.addCell(new Label(3, row, "无",titleFmt2));
				}else{
					sheet.addCell(new Label(3, row, log.getSecondfenxiaoName()));
				}
				if(StringUtils.isBlank(log.getSecondMobile())||log.getSecondMobile().equals("0")){
					sheet.addCell(new Label(4, row, "无",titleFmt2));
				}else{
					sheet.addCell(new Label(4, row,log.getSecondMobile()));
				}
				if(log.getSecondMoney()==0.0){
					sheet.addCell(new Label(5, row, "无",titleFmt1));
				}else{
					sheet.addCell(new jxl.write.Number(5, row, log.getSecondMoney(), priceFmt));
				}
				sheet.addCell(new Label(6, row, log.getDetail()));
				sheet.addCell(new Label(7, row, log.getType()));
				sheet.addCell(new Label(8, row, sdf.format( log.getCreateTime())));
				if(log.getSmkMoney()==0.0){
					sheet.addCell(new Label(9, row, "无",titleFmt1));
				}else{
					sheet.addCell(new jxl.write.Number(9, row, log.getSmkMoney(), priceFmt));
				}
				++row;
			}
			workbook.write();
			workbook.close();
        }
	}

}
