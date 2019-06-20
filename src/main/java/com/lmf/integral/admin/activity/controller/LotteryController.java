/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.activity.controller;

import com.lmf.activity.entity.AwardLog;
import com.lmf.activity.entity.Lottery;
import com.lmf.activity.enums.AwardStatus;
import com.lmf.activity.enums.AwardType;
import com.lmf.activity.service.AwardLogService;
import com.lmf.activity.service.LotteryService;
import com.lmf.activity.vo.AwardLogCriteria;
import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SerializableMultipartFile;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.enums.OwnerType;
import com.lmf.common.util.PagerSpec;
import com.lmf.common.util.PagerUtil;
import com.lmf.common.util.SimpleImageInfo;
import com.lmf.enums.ProductStatus;
import com.lmf.integral.SystemConfig;
import com.lmf.product.entity.Brand;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.ProductCate;
import com.lmf.product.service.BrandService;
import com.lmf.product.service.ProductCateService;
import com.lmf.product.service.ProductService;
import com.lmf.product.vo.ProductCriteria;
import com.lmf.sys.service.InternalAttachmentService;
import com.lmf.website.entity.WebsiteUser;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author shenzhixiong
 */
@Controller("adminLotteryController")
public class LotteryController {

    @Autowired
    private LotteryService lotteryService;

    @Autowired
    private AwardLogService awardLogService;
            
    @Autowired
    private InternalAttachmentService internalAttachmentService;
            
    @Autowired
    private ProductService productService;
        
    @Autowired
    private BrandService brandService;
        
    @Autowired
    private ProductCateService productCateService;
    
    @Autowired
    private SystemConfig systemConfig;
    
    /**
     * 查询活动
     * @param pager
     * @param model
     * @return 
     */ 
    @RequestMapping(value = "/admin/lottery/list.php", method = RequestMethod.GET)
    public String list(@PagerSpecDefaults(pageSize = 14, sort = "createTime.desc") PagerSpec pager,Model model) {
        Page<Lottery> lotterys = lotteryService.find(null, null, pager);
        String link = "/jdvop/admin/lottery/list.php?page=[:page]";
        model.addAttribute("link", link);
        model.addAttribute("lotterys", lotterys);
        return "admin/activity/lottery/list";
    }

    /**
     * 获取新增活动页面
     * @return 
     */
    @RequestMapping(value = "/admin/lottery/addView.php", method = RequestMethod.GET)
    public String addPage() {
        return "admin/activity/lottery/view";
    }
    
    /**
     * 获取编辑活动页面
     * @param id
     * @param model
     * @return 
     */
    @RequestMapping(value = "/admin/lottery/editView/{lotteryId}.php", method = RequestMethod.GET)
    public String editPage(@PathVariable("lotteryId") int id, Model model) {
        Lottery lottery = lotteryService.findOne(id);
        model.addAttribute("lottery", lottery);
        return "admin/activity/lottery/view";
    }
    
    /**
     * 保存活动
     * @param lottery
     * @return 
     */
    @RequestMapping(value = "/admin/lottery/save.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse save(@RequestBody Lottery lottery) {
        //如果传来的ID为0，表示新增，否则表示修改
        try{
            if(null ==lottery.getId() || lottery.getId() == 0){
                lotteryService.create(lottery);
            }else{
                lotteryService.edit(lottery);
            }
        }catch(Exception e){
            return new SimpleJsonResponse(false, e.getMessage());
        }
        return new SimpleJsonResponse(true, "");
    }
    
    /**
     * 删去活动
     * @param id
     * @return 
     */
    @RequestMapping(value = "/admin/lottery/delete/{lotteryId}.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse delete(@PathVariable("lotteryId") int id) {
        //进行中的活动点击删除 提示：活动进行中无法删除。请先停止活动
        Lottery lottery = lotteryService.findOne(id);
        Date sysTime = new Date();
        if(sysTime.after(lottery.getBeginTime()) && sysTime.before(lottery.getExpireTime())){
            return new SimpleJsonResponse(false, "活动进行中无法删除。请先停止活动"); 
        }else{
            lotteryService.delete(id);
            return new SimpleJsonResponse(true, "");
        }
    }
    
    @RequestMapping(value = "/admin/lottery/image-upload.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse<String> uploadPcImg(@RequestParam(value = "imageFile") MultipartFile file,
            @RequestParam(value = "width",required = false) Integer width,
            @RequestParam(value = "height",required = false) Integer height) throws IOException{
        SerializableMultipartFile  smf;
        try{
            smf = new SerializableMultipartFile(file);
            SimpleImageInfo sii = new SimpleImageInfo(smf.getBytes());
            if(width!= null && height != null){
                if(!(sii.getWidth() == width && sii.getHeight() == height)){
                    StringBuilder msg = new StringBuilder("您上传的图片文件尺寸不合法，必须是");
                    msg.append(width);msg.append(" * ");msg.append(height);
                    msg.append(" 像素的");
                    return new SimpleJsonResponse(false, msg.toString());
                }
            }
        }catch(IOException e ){
            return new SimpleJsonResponse(false,"您上传的图片文件不合法，请重新上传");
        }
        String  path    = internalAttachmentService.save(smf);
        return new SimpleJsonResponse(true, path);
    }
    
    /*中奖名单*/
    @RequestMapping(value="/admin/lottery/winningList/{lotteryId}.php",method = RequestMethod.GET)
    public String winningList(@PathVariable("lotteryId") int lotteryId, 
            @RequestParam(value="kw",required = false)String keyword, 
            @RequestParam(value="status",required = false)AwardStatus status, 
            @RequestParam(value="type",required = false)AwardType type, 
            @PagerSpecDefaults(pageSize = 20) PagerSpec pager, Model model) throws IOException{
        StringBuilder link = new StringBuilder("/jdvop/admin/lottery/winningList/").append(lotteryId).append(".php?page=[:page]");
        if (keyword != null && !keyword.isEmpty()) {
            link.append("&kw=").append(URLEncoder.encode(keyword, "UTF-8"));
        }
        if (status != null) {
            link.append("&status=").append(status.name());
        }
        if (type != null) {
            link.append("&type=").append(type.name());
        }
        Page<AwardLog> page = awardLogService.find(new AwardLogCriteria().withKeyword(keyword).withLotteryId(lotteryId).withAwardStatus(status).withAwardType(type), pager);
        model.addAttribute("link", link.toString());
        model.addAttribute("winners", page);
        model.addAttribute("lotteryId", lotteryId);
        model.addAttribute("kw",keyword);
        model.addAttribute("status",status);
        model.addAttribute("type",type);
        return  "/admin/activity/lottery/winningList";
    }
    
    /*导出中奖名单*/
    @RequestMapping(value = "/admin/lottery/winningList/export/{lotteryId}.php", method = RequestMethod.GET)
    public @ResponseBody void exportAsExcel(@PathVariable("lotteryId") int lotteryId,
                                              @RequestParam(value = "kw", required = false) String keyword,
                                              @RequestParam(value = "type", required = false) String type,
                                              @RequestParam(value = "status", required = false) String status,
                                              HttpServletResponse response) throws UnsupportedEncodingException, IOException, WriteException {
        AwardLogCriteria criteria = new AwardLogCriteria();
        criteria.withLotteryId(lotteryId);
        criteria.withKeyword(keyword);
        if(!(type == null || "".equals(type))){
            criteria.withAwardType(AwardType.valueOf(type));
        }
        if(!(status == null || "".equals(status))){
            criteria.withAwardStatus(AwardStatus.valueOf(status));
        }
        Page<AwardLog> page = awardLogService.find(criteria, null);
        List<AwardLog> list = page.getContent();
        
        String title = new StringBuilder("导出会员").toString();
        response.setContentType("application/force-download");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.addHeader("Content-Type", "application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", new StringBuilder("attachment;filename=").append(URLEncoder.encode(title, "UTF-8")).append(".xls").toString());
        try (OutputStream out = response.getOutputStream()) {
            WritableWorkbook workbook = Workbook.createWorkbook(out);
            WritableSheet sheet = workbook.createSheet("导出中奖名单", 0);

            //设置列宽
            sheet.setColumnView(0, 20);//中奖者昵称
            sheet.setColumnView(1, 20);//中奖时间
            sheet.setColumnView(2, 20);//奖品名称
            sheet.setColumnView(3, 20);//类型
            sheet.setColumnView(4, 22);//状态
            sheet.setColumnView(5, 22);//收件人
            sheet.setColumnView(6, 20);//收件人电话
            sheet.setColumnView(7, 22);//收货地址
            sheet.setColumnView(8, 15);//备注
            sheet.getSettings().setVerticalFreeze(2);

            //初始化标题
            WritableFont titleWft = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
            titleWft.setColour(Colour.BROWN);
            WritableCellFormat titleFmt = new WritableCellFormat(titleWft);
            titleFmt.setBackground(Colour.GRAY_25);
            titleFmt.setAlignment(Alignment.CENTRE);
            titleFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //写标题
            sheet.mergeCells(0, 0, 0, 1);
            sheet.addCell(new Label(0, 0, "用户名", titleFmt));
            sheet.mergeCells(1, 0, 1, 1);
            sheet.addCell(new Label(1, 0, "中奖时间", titleFmt));
            sheet.mergeCells(2, 0, 2, 1);
            sheet.addCell(new Label(2, 0, "奖品名称", titleFmt));
            sheet.mergeCells(3, 0, 3, 1);
            sheet.addCell(new Label(3, 0, "类型", titleFmt));
            sheet.mergeCells(4, 0, 4, 1);
            sheet.addCell(new Label(4, 0, "状态", titleFmt));
            sheet.mergeCells(5, 0, 5, 1);
            sheet.addCell(new Label(5, 0, "收件人", titleFmt));
            sheet.mergeCells(6, 0, 6, 1);
            sheet.addCell(new Label(6, 0, "收件人电话", titleFmt));
            sheet.mergeCells(7, 0, 7, 1);
            sheet.addCell(new Label(7, 0, "收货地址", titleFmt));
            sheet.mergeCells(8, 0, 8, 1);
            sheet.addCell(new Label(8, 0, "备注", titleFmt));
            int row = 2;
            
            for(AwardLog log: list){
                WebsiteUser user = log.getWebsiteUser();
                sheet.addCell(new Label(0, row, user.getLoginName()));
                sheet.addCell(new Label(1, row, sdf.format(log.getCreateTime())));
                sheet.addCell(new Label(2, row, log.getAwardName()));
                sheet.addCell(new Label(3, row, log.getAwardType().getDescription()));
                sheet.addCell(new Label(4, row, log.getAwardStatus().getDescription()));
                sheet.addCell(new Label(5, row, log.getReceiverName()));
                sheet.addCell(new Label(6, row, log.getReceiverMobile()));
                StringBuilder addr = new StringBuilder();addr.append("");
                addr.append(log.getProvince() == null ? "":log.getProvince().getName());
                addr.append(log.getCity() == null ? "": log.getCity().getName());
                addr.append(log.getCounty()== null ? "":log.getCounty().getName());
                addr.append(log.getTown() == null ? "":log.getTown().getName());
                addr.append(log.getReceiverAddr()==null ? "":log.getReceiverAddr());
                sheet.addCell(new Label(7, row, addr.toString()));
                sheet.addCell(new Label(8, row, log.getRemark()));
                ++row;
            }
            workbook.write();
            workbook.close();
        }
    }
    
    /* 上传库存商品*/
    @RequestMapping(value = "/admin/lottery/chooseProducts.php", method = RequestMethod.GET)
    public String chooseProducts(@RequestParam(value = "openBoxIndex") String openBoxIndex,
                                 @RequestParam(value = "kw", required = false) String keyword,
                                 @RequestParam(value = "brand", required = false) Brand brand,
                                 @RequestParam(value = "cate", required = false) ProductCate cate,
                                 @RequestParam(value = "minPrice", required = false) Double minPrice,
                                 @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                                 @RequestParam(value = "maxSize", defaultValue = "1") int maxSize,
                                 @RequestParam(value = "minSize", defaultValue = "1") int minSize,
                                 @RequestParam(value = "id[]", required = false) Integer[] pids,
                                 @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager,
                                 Model model){
        StringBuilder link = new StringBuilder("/jdvop/admin/activity/lottery/loadProducts.php?page=[:page]");
        if(keyword != null && !keyword.trim().isEmpty()){
            link.append("&kw=").append(keyword);
        }
        if(brand != null){
            link.append("&brand=").append(brand.getId());
        }
        if(cate != null){
            link.append("&cate=").append(cate.getId());
        }
        if(minPrice != null && minPrice >= 0){
            link.append("&minPrice=").append(minPrice);
        }
        if(maxPrice != null && maxPrice >= 0){
            link.append("&maxPrice=").append(maxPrice);
        }
        Page<Product> products = productService.find(new ProductCriteria().withStatus(ProductStatus.selling).withDeleted(Boolean.FALSE).withNoJingdong(Boolean.TRUE), pager);;
        Page<Brand> brands = brandService.findProductBrands(null, ProductStatus.selling, null, null);
        if(pids != null && pids.length > 0){
            List<Product> selectedProducts = productService.find(Arrays.asList(pids));
            model.addAttribute("selectedProducts", selectedProducts);
        }
        model.addAttribute("cates", productCateService.findOne(0).getChilds());
        model.addAttribute("brands", brands);
        model.addAttribute("products", products);
        model.addAttribute("link", link.toString());
        model.addAttribute("maxSize", maxSize);
        model.addAttribute("minSize", minSize);
        model.addAttribute("openBoxIndex", openBoxIndex);
        model.addAttribute("ownerTypes", OwnerType.values());
        return "admin/activity/lottery/choose_product";
    }
    
    @RequestMapping(value = "/admin/activity/lottery/loadProducts.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public  @ResponseBody  Map<String, Object>  loadProducts(@RequestParam(value = "kw", required = false) String keyword,
                                                            @RequestParam(value = "brand", required = false) Brand brand,
                                                            @RequestParam(value = "cate", required = false) ProductCate cate,
                                                            @RequestParam(value = "minPrice", required = false) Double minPrice,
                                                            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                                                            @RequestParam(value = "type", required = false) OwnerType type,
                                                            @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager) throws UnsupportedEncodingException 
    {
        ProductCriteria productCriteria = new ProductCriteria().withOwnerType(type).withKeyword(keyword).withBrand(brand).withCate(cate).withPrice(minPrice, maxPrice).withStatus(ProductStatus.selling).withNoJingdong(Boolean.TRUE);
        Page<Product> products = productService.find(productCriteria, pager);
        StringBuilder url = new StringBuilder("/jdvop/admin/activity/lottery/loadProducts.php?page=[:page]");
        if (keyword != null && !keyword.isEmpty()) {
            url.append("&kw=").append(URLEncoder.encode(keyword, "UTF-8"));
        }
        if (brand != null) {
            url.append("&brand=").append(brand.getId());
        }
        if (cate != null) {
            url.append("&cate=").append(cate.getId());
        }
        if (minPrice != null && minPrice > 0) {
            url.append("&minPrice=").append(minPrice);
        }
        if (maxPrice != null && maxPrice > 0) {
            url.append("&maxPrice=").append(maxPrice);
        }
        Map<String, Object> result  = new HashMap<>();
        result.put("contentHtml", renderProduct(products));
        
        String  pageHtml    = PagerUtil.createPagenation(url.toString(), products.getPagerSpec(), 2, "_self");
        result.put("pageHtml", pageHtml);
        return result;
    }
    
    /* 标记发奖*/
    @RequestMapping(value = "/admin/lottery/tagAward.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse tagAward(@RequestParam(value = "id") long logId, @RequestParam(value = "remark") String remark){
        //修改备注 修改状态修改为已发放
        AwardLog log = awardLogService.findOne(logId);
        log.setAwardStatus(AwardStatus.has_sent);
        log.setRemark(remark);
        awardLogService.setStatus(log);
        return new SimpleJsonResponse(true, "");
    }
    
    private String  renderProduct(Page<Product> products) {
        StringBuilder html = new StringBuilder();
        for (Product pd : products) {
            renderProduct(html, pd);
        }
        return html.toString();
    }
    
    private void  renderProduct(StringBuilder buffer, Product product) {
        buffer.append("<li rel=\"").append(product.getId()).append("\">");
        buffer.append("<a href=\"#\" class=\"tooltip\" title=\"点击选中！O(∩_∩)O~\">");
        buffer.append("<p class=\"text\"> <label>产品型号 ").append(product.getProductCode()).append("</label></p>");
        buffer.append("<p class=\"img\">");
        if(product.getOwnerType() == OwnerType.jingdong || product.getOwnerType() == OwnerType.system){
            buffer.append("<img src='").append(product.getThumbnailImage()).append("' width=\"160\" height=\"160\" alt=\"").append(product.getName()).append("\"/>");
            if(product.getOwnerType() == OwnerType.jingdong)
            {
                buffer.append("<img src=\"/jdvop/images/jd-ico.png\" class=\"jd-ico\"/>");
            }
        } else {
            buffer.append("<img src=\"").append(systemConfig.getImageHost()).append("/middle").append(product.getThumbnailImage()).append("\" width=\"160\" height=\"160\" alt=\"").append(product.getName()).append("\"/>");
        }
        buffer.append("</p>");
        buffer.append("<p class=\"text blue product-title\">").append(product.getName()).append("</p>");
        if(product.getOwnerType() == OwnerType.enterprise || product.getOwnerType() == OwnerType.provider) {
            buffer.append("<p class=\"text\"><i class=\"fr\">销量：").append(product.getProductStorage().getSoldedNum()).append("</i><i>库存：").append(product.getProductStorage().getUseableNum()).append("</i> </p>");
        } else {
            buffer.append("<p class=\"text\">所属品牌：").append(product.getBrand().getName()).append("</p>");
        }
        buffer.append("<p class=\"text ffa orange\">").append("<i class=\"color999\"> ¥</i>").append(String.format("%.2f", product.getRetailPrice())).append("</p>");
        buffer.append("</a>");
        buffer.append("<span class=\"ico-selected\"></span>");
        if(product.getOwnerType() == OwnerType.enterprise) {
            buffer.append("<label class=\"ico_own\"></label>");
        } else if(product.getOwnerType() == OwnerType.provider) {
            buffer.append("<label class=\"ico_gys\"></label>");
        } else if(product.getOwnerType() == OwnerType.system) {
            buffer.append("<label class=\"ico_xt\"></label>");
        }
        buffer.append("</li>");
    }
}