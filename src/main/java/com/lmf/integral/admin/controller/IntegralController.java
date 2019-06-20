/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.controller;

import com.lmf.activity.entity.Lottery;
import com.lmf.activity.service.LotteryService;
import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.Range;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.CommonUtil;
import com.lmf.common.util.ExcelReaderUtil;
import com.lmf.common.util.PagerSpec;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.service.OrderService;
import com.lmf.website.entity.IntegralConsumeLog;
import com.lmf.website.entity.IntegralGenerateLog;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteAdministrator;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.enums.IntegralConsumeType;
import com.lmf.website.enums.IntegralGenerateType;
import com.lmf.website.enums.WebsiteLoginType;
import com.lmf.website.service.IntegralService;
import com.lmf.website.service.WebsiteAdministratorService;
import com.lmf.website.service.WebsiteUserService;
import com.lmf.website.vo.WebsiteUserCriteria;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jxl.Sheet;
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
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

/**
 *
 * @author shenzhixiong
 */
@Controller
public class IntegralController {
    
    @Autowired
    private WebsiteUserService websiteUserService;
    
    @Autowired
    private IntegralService websiteIntegralService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private LotteryService lotteryService;
    
    private final static Logger logger  = LoggerFactory.getLogger(IntegralController.class);
    
    @RequestMapping(value = "/admin/integral/consumeIntegralLogs.php",method = RequestMethod.GET)
    public String integralConsumeLogs(@RequestParam(value = "st", required = false) Date startDate,
                                      @RequestParam(value = "et", required = false) Date endDate,
                                      @PagerSpecDefaults(pageSize = 20, maxPageSize = 100, checkSortFields = true) PagerSpec pager,
                                      Website website,
                                      Model model) throws ParseException{
        StringBuilder link = new StringBuilder("/jdvop/admin/integral/consumeIntegralLogs.php?page=[:page]");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (startDate != null) {
            model.addAttribute("st", sdf.format(startDate));
            link.append("&st=").append(sdf.format(startDate));
        }
        if (endDate != null) {
            model.addAttribute("et", sdf.format(endDate));
            Calendar date = Calendar.getInstance();
            date.setTime(endDate);
            date = CommonUtil.filled(date);
            endDate = sdf.parse(sdf.format(date.getTime()));
            link.append("&et=").append(sdf.format(endDate));
        }
        Page<WebsiteUser> websiteUsers = websiteUserService.find(null, null);
        List<Long> ids = new ArrayList();
        for(WebsiteUser user : websiteUsers){
            ids.add(user.getId());
        } 
        Page<IntegralConsumeLog> consumeLogs = null;
        if(ids.size() > 0){
            consumeLogs = websiteIntegralService.findConsumeLog(ids, new Range(startDate, endDate), pager);
        }
        model.addAttribute("consumeLogs", consumeLogs);
        model.addAttribute("websiteUserService",websiteUserService);
        model.addAttribute("orderService",orderService);
        model.addAttribute("lotteryService", lotteryService);
        model.addAttribute("link", link.toString());
        return "admin/integral/log/consumeLogs";
    }
    
    
    @RequestMapping(value = "/admin/integral/consumeIntegralExport.php", method = RequestMethod.GET)
    public @ResponseBody void consumeIntegralExport(@RequestParam(value = "st", required = false) Date startDate,
                                                    @RequestParam(value = "et", required = false) Date endDate,
                                                    Website website,
                                                    HttpServletResponse response) throws UnsupportedEncodingException, IOException, WriteException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sbd = new StringBuilder();
        if(startDate != null && endDate == null){
            sbd.append(sdf.format(startDate));
        }else if(endDate != null && startDate == null){
            sbd.append(sdf.format(endDate));
        }else if(startDate != null && endDate != null && !startDate.equals(endDate)){
            String st = sdf.format(startDate);
            String et = sdf.format(endDate);
            sbd.append(st).append("-").append(et);
        }else if(startDate != null && endDate != null && startDate.equals(endDate)){
            sbd.append(sdf.format(startDate));
        }          
        sbd.append(website.getName()).append("积分消费记录明细");
        String title = sbd.toString();
        response.setContentType("application/force-download");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.addHeader("Content-Type", "application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", new StringBuilder("attachment;filename=").append(URLEncoder.encode(title, "UTF-8")).append(".xls").toString());
        try (OutputStream out = response.getOutputStream()) {
            WritableWorkbook workbook = Workbook.createWorkbook(out);
            WritableSheet sheet = workbook.createSheet("导出积分消费记录", 0);

            //设置列宽
            sheet.setColumnView(0, 15);//用户名
            sheet.setColumnView(1, 15);//消费类型
            sheet.setColumnView(1, 15);//消费类型
            sheet.setColumnView(2, 30);//消费时间
            sheet.setColumnView(3, 15);//积分
            
            sheet.getSettings().setVerticalFreeze(1);

            //初始化标题
            WritableFont titleWft = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
            titleWft.setColour(Colour.BROWN);
            WritableCellFormat titleFmt = new WritableCellFormat(titleWft);
            titleFmt.setBackground(Colour.GRAY_25);
            titleFmt.setAlignment(Alignment.CENTRE);
            titleFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
            WritableCellFormat intFmt = new WritableCellFormat(new NumberFormat("#####"));

            //写标题
            sheet.addCell(new Label(0, 0, "用户名", titleFmt));
            sheet.addCell(new Label(1, 0, "消费类型", titleFmt));
            sheet.addCell(new Label(2, 0, "消费来源", titleFmt));
            sheet.addCell(new Label(3, 0, "消费时间", titleFmt));
            sheet.addCell(new Label(4, 0, "积分", titleFmt));
            
            Page<WebsiteUser> websiteUsers = websiteUserService.find(null, null);
            List<Long> ids = new ArrayList();
            for(WebsiteUser user : websiteUsers){
                ids.add(user.getId());
            }
            Page<IntegralConsumeLog> integralConsumeLogs = websiteIntegralService.findConsumeLog(ids, new Range(startDate,endDate), null);
            if (integralConsumeLogs != null) {
                int row = 1;
                for (IntegralConsumeLog log : integralConsumeLogs) {
                    WebsiteUser user = websiteUserService.findOne(log.getUserId());
                    if(user != null){
                       sheet.addCell(new Label(0, row,user.getLoginName()));
                    }else{
                       sheet.addCell(new Label(0, row,"该数据是错误数据，请联系管理员"));
                    }
                    sheet.addCell(new Label(1, row, log.getConsumeType().getDescription()));
                    if(log.getConsumeType() == IntegralConsumeType.orders) {
                        ShoppingOrder order = orderService.findOne(log.getConsumeId());
                        if(order != null){
                            sheet.addCell(new Label(2, row,order.getKey()));
                        }else{
                            sheet.addCell(new Label(2, row,"该数据是错误数据，请联系管理员"));
                        }
                    } else if(log.getConsumeType() == IntegralConsumeType.lottery){
                        Lottery lottery = lotteryService.findOne(log.getConsumeId().intValue());
                        if(lottery != null){
                            sheet.addCell(new Label(2, row, lottery.getLotteryName()));
                        }else{
                            sheet.addCell(new Label(2, row,"该数据是错误数据，请联系管理员"));
                        }
                    }
                    
                    
                    sheet.addCell(new Label(3, row, sdf.format(log.getConsumeTime())));
                    sheet.addCell(new jxl.write.Number(4, row, log.getDeltaAmount(),intFmt));
                    ++row;
                }
            }
            workbook.write();
            workbook.close();
        }
    }
    
    @RequestMapping(value = "/admin/websiteUser/integral/export.php", method = RequestMethod.GET)
    public @ResponseBody void exportAsExcelByIntegral(@RequestParam(value = "kw", required = false) String keyword,
                                                      @RequestParam(value = "enabled", defaultValue = "true") Boolean enabled,
                                                      Website website, WebsiteAdministrator admin, HttpServletResponse response) throws UnsupportedEncodingException, IOException, WriteException {
        
        
        List<WebsiteUser> websiteUsers = websiteUserService.find(new WebsiteUserCriteria().withKeyword(keyword).withEnabled(enabled), null).getContent();

        String title = new StringBuilder(admin.getLoginName()).append(" 导出会员").toString();
        response.setContentType("application/force-download");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.addHeader("Content-Type", "application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", new StringBuilder("attachment;filename=").append(URLEncoder.encode(title, "UTF-8")).append(".xls").toString());
        try (OutputStream out = response.getOutputStream()) {
            WritableWorkbook workbook = Workbook.createWorkbook(out);
            WritableSheet sheet = workbook.createSheet("导出会员", 0);
            
            //设置列宽
            sheet.setColumnView(0, 25);//登录用户名
            sheet.setColumnView(1, 25);//昵称
            sheet.setColumnView(2, 25);//手机
            sheet.setColumnView(3, 25);//是否启用
            sheet.setColumnView(4, 20);//增加积分
            sheet.getSettings().setVerticalFreeze(2);

            //初始化标题
            WritableFont titleWft = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
            titleWft.setColour(Colour.BROWN);
            WritableCellFormat titleFmt = new WritableCellFormat(titleWft);
            titleFmt.setBackground(Colour.GRAY_25);
            titleFmt.setAlignment(Alignment.CENTRE);
            titleFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
            HSSFWorkbook wb = new HSSFWorkbook();    
            HSSFFont font = wb.createFont();

            //写标题
            sheet.mergeCells(0, 0, 0, 1);
            sheet.addCell(new Label(0, 0, "登录用户名（不可编辑）", titleFmt));
            sheet.mergeCells(1, 0, 1, 1);
            sheet.addCell(new Label(1, 0, "昵称（不可编辑）", titleFmt));
            sheet.mergeCells(2, 0, 2, 1);
            sheet.addCell(new Label(2, 0, "手机（不可编辑）", titleFmt));
            sheet.mergeCells(3, 0, 3, 1);
            sheet.addCell(new Label(3, 0, "是否启用（不可编辑）", titleFmt));
            sheet.mergeCells(4, 0, 4, 1);
            sheet.addCell(new Label(4, 0, "增加积分", titleFmt));
            int row = 2;
            for (WebsiteUser user : websiteUsers) {
                sheet.addCell(new Label(0, row, user.getLoginName()));
                sheet.addCell(new Label(1, row, user.getNickName()));
                sheet.addCell(new Label(2, row, user.getMobile()));
                if(user.isEnabled()) {
                    sheet.addCell(new Label(3, row, "是"));
                }else{
                    sheet.addCell(new Label(3, row, "否"));
                }
                ++row;
            }
            workbook.write();
            workbook.close();
        }
    }
    
}
