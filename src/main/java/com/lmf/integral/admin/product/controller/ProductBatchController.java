/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.product.controller;

import com.lmf.common.Page;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.enums.OwnerType;
import com.lmf.common.exceptions.ResourceNotFoundException;
import com.lmf.common.util.ExcelReaderUtil;
import com.lmf.enums.ProductStatus;
import com.lmf.product.entity.Brand;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.ProductCate;
import com.lmf.product.entity.ProductDescription;
import com.lmf.product.entity.ProductImage;
import com.lmf.product.entity.ProductPropertyValue;
import com.lmf.product.entity.StorageUnit;
import com.lmf.product.enums.StorageUnitEffectBarType;
import com.lmf.product.enums.StorageUnitStatus;
import com.lmf.product.service.BrandService;
import com.lmf.product.service.ProductCateService;
import com.lmf.product.service.ProductDescriptionService;
import com.lmf.product.service.ProductService;
import com.lmf.product.vo.ProductCriteria;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteAdministrator;
import com.lmf.website.service.WebsiteAdministratorService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
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

import org.apache.shiro.authz.annotation.RequiresPermissions;
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
@Controller("adminProductBatchController")
public class ProductBatchController {
    
    @Autowired
    private ProductCateService  productCateService;
    
    @Autowired
    private ProductService  productService;
    
    @Autowired
    private BrandService brandService;
    
    @Autowired
    private ProductDescriptionService productDescriptionService;
    
    @Autowired
    private WebsiteAdministratorService websiteAdministratorService;
    
    private final static Logger logger  = LoggerFactory.getLogger(ProductController.class);
    
    @RequestMapping(value = "/admin/product/batchUpdate.php", method = RequestMethod.GET)
    public String batchUpdate(WebsiteAdministrator admin, Model model)
    {
        model.addAttribute("admin", admin);
        return "admin/product/batch_update";
    }
    
    @RequiresPermissions("product:export")
    @RequestMapping(value = "/admin/product/export.php", method = RequestMethod.GET)
    public @ResponseBody void exportAsExcel(@RequestParam(value = "kw", required = false) String keyword,
                                            @RequestParam(value = "b", required = false) Brand brand,
                                            @RequestParam(value = "c", required = false) ProductCate cate,
                                            @RequestParam(value = "sp", required = false) Double minPrice,
                                            @RequestParam(value = "mp", required = false) Double maxPrice,
                                            @RequestParam(value = "status", defaultValue = "selling") ProductStatus status,
                                            @RequestParam(value = "self", required = false) Boolean self,
                                            @RequestParam(value = "type", required = false) OwnerType type,
                                            Website website, WebsiteAdministrator admin,
                                            HttpServletResponse response) throws UnsupportedEncodingException, IOException, WriteException
    {
        ProductCriteria criteria = new ProductCriteria().withKeyword(keyword).withBrand(brand).withCate(cate).withSelf(self).withStatus(status).withPrice(minPrice, maxPrice).withDeleted(Boolean.FALSE);
        Page<Product> products  = null;
        if(admin.isProvider())
        {
            products = productService.find(OwnerType.provider, admin.getId(), criteria, null);
        } else {
            criteria.withOwnerType(type);
            products = productService.find(criteria, null);
        }
        
        if(products == null){
            throw new ResourceNotFoundException();
        }
        
        String  title   = new StringBuilder(website.getName()).append(" 导出产品").toString();
        response.setContentType("application/force-download");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.addHeader("Content-Type", "application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", new StringBuilder("attachment;filename=").append(URLEncoder.encode(title, "UTF-8")).append(".xls").toString());
        try (OutputStream    out = response.getOutputStream()) {
            WritableWorkbook   workbook = Workbook.createWorkbook(out);
            WritableSheet   sheet   = workbook.createSheet("导出产品", 0);
            
            //设置列宽
            sheet.setColumnView(0, 10);//产品编号
            sheet.setColumnView(1, 40);//产品名称
            sheet.setColumnView(2, 16);//品牌
            sheet.setColumnView(3, 16);//产品型号
            sheet.setColumnView(4, 10);//市场价
            sheet.setColumnView(5, 10);//售价
            sheet.setColumnView(6, 12);//产品分类
            sheet.setColumnView(7, 12);//产品分类1
            sheet.setColumnView(8, 12);//产品分类2
            sheet.setColumnView(9, 60);//产品卖点
            sheet.setColumnView(10, 6);//长
            sheet.setColumnView(11, 6);//宽
            sheet.setColumnView(12, 6);//高
            sheet.setColumnView(13, 8);//净重
            sheet.setColumnView(14, 10);//物流重量
            sheet.setColumnView(15, 10);//扣点
            
            sheet.getSettings().setVerticalFreeze(2);
            
            //初始化标题
            WritableFont   titleWft  = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
            titleWft.setColour(Colour.BROWN);
            WritableCellFormat titleFmt = new WritableCellFormat(titleWft);
            titleFmt.setBackground(Colour.GRAY_25);  
            titleFmt.setAlignment(Alignment.CENTRE);
            titleFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
            WritableCellFormat priceFmt   = new WritableCellFormat(new NumberFormat("#0.00"));
            WritableCellFormat intFmt     = new WritableCellFormat(new NumberFormat("#####"));
            
            //写标题
            sheet.mergeCells(0, 0, 0, 1);
            sheet.addCell(new Label(0, 0, "产品ID", titleFmt));
            sheet.mergeCells(1, 0, 1, 1);
            sheet.addCell(new Label(1, 0, "产品名称", titleFmt));
            sheet.mergeCells(2, 0, 2, 1);
            sheet.addCell(new Label(2, 0, "品牌", titleFmt));
            sheet.mergeCells(3, 0, 3, 1);
            sheet.addCell(new Label(3, 0, "产品型号", titleFmt));
            sheet.mergeCells(4, 0, 4, 1);
            sheet.addCell(new Label(4, 0, "市场价", titleFmt));
            sheet.mergeCells(5, 0, 5, 1);
            sheet.addCell(new Label(5, 0, "售价", titleFmt));
            sheet.mergeCells(6, 0, 8, 0);
            sheet.addCell(new Label(6, 0, "产品分类", titleFmt));
            sheet.addCell(new Label(6, 1, "一级分类", titleFmt));
            sheet.addCell(new Label(7, 1, "二级分类", titleFmt));
            sheet.addCell(new Label(8, 1, "三级分类", titleFmt));
            sheet.mergeCells(9, 0, 9, 1);
            sheet.addCell(new Label(9, 0, "产品卖点", titleFmt));
            sheet.mergeCells(10, 0, 12, 0);
            sheet.addCell(new Label(10, 0, "规格", titleFmt));
            sheet.addCell(new Label(10, 1, "长", titleFmt));
            sheet.addCell(new Label(11, 1, "宽", titleFmt));
            sheet.addCell(new Label(12, 1, "高", titleFmt));
            sheet.mergeCells(13, 0, 14, 0);
            sheet.addCell(new Label(13, 0, "重量", titleFmt));
            sheet.addCell(new Label(13, 1, "净重", titleFmt));
            sheet.addCell(new Label(14, 1, "物流重量", titleFmt));
            sheet.mergeCells(15, 0, 15, 1);             //扣点
            sheet.addCell(new Label(15, 0, "扣点", titleFmt)); 
            
            int row = 2;
            for (Product product : products)
            {
                sheet.addCell(new jxl.write.Number(0, row, product.getId(), intFmt));
                sheet.addCell(new Label(1, row, product.getName()));
                if (product.getBrand() != null)
                {
                    sheet.addCell(new Label(2, row, product.getBrand().getName()));
                }
                sheet.addCell(new Label(3, row, product.getProductCode()));
                sheet.addCell(new jxl.write.Number(4, row, product.getMarketPrice(), priceFmt));
                sheet.addCell(new jxl.write.Number(5, row, product.getRetailPrice(), priceFmt));
                if(product.getCate() != null) {
                    List<ProductCate>   cates   = productCateService.parents(product.getCate());
                    Collections.reverse(cates);
                    int size    = cates.size();
                    if (size >= 1)
                    {
                        sheet.addCell(new Label(6, row, cates.get(0).getName()));
                    }
                    if (size >= 2)
                    {
                        sheet.addCell(new Label(7, row, cates.get(1).getName()));
                    }
                    if (size >= 3)
                    {
                        sheet.addCell(new Label(8, row, cates.get(1).getName()));
                    }
                }
                if(OwnerType.jingdong != product.getOwnerType()) {
                    sheet.addCell(new Label(9, row, product.getFeatures()));
                }
                
                List<ProductPropertyValue>  propertyValues  = productService.findExtraPropertyValues(product);
                if (propertyValues != null && !propertyValues.isEmpty())
                {
                    Map<String, String> property    = toMap(propertyValues);
                    if (null != property.get("LENGTH"))
                    {
                        sheet.addCell(new Label(10, row, property.get("LENGTH")));
                    }
                    if (null != property.get("WIDTH"))
                    {
                        sheet.addCell(new Label(11, row, property.get("WIDTH")));
                    }
                    if (null != property.get("HEIGHT"))
                    {
                        sheet.addCell(new Label(12, row, property.get("HEIGHT")));
                    }
                    if (null != property.get("WEIGHT"))
                    {
                        sheet.addCell(new Label(13, row, property.get("WEIGHT")));
                    }
                    if (null != property.get("SHIPMENT_WEIGHT"))
                    {
                        sheet.addCell(new Label(14, row, property.get("SHIPMENT_WEIGHT")));
                    }
                }
                //扣点
                if(product.getServiceChargeRatio() != null) {
                    sheet.addCell(new jxl.write.Number(15, row, product.getServiceChargeRatio(), priceFmt));
                }
                
                ++ row;
            }
            workbook.write();
            workbook.close();
        }
    }
    //导出自有产品
    @RequiresPermissions("product:export")
    @RequestMapping(value = "/admin/product/exportSelfProduct.php", method = RequestMethod.GET)
    public @ResponseBody void exportSelfProduct(WebsiteAdministrator admin, HttpServletResponse response) throws UnsupportedEncodingException, IOException, WriteException
    {
        ProductCriteria criteria = new ProductCriteria().withSelf(true).withDeleted(Boolean.FALSE);
        
        Page<Product> products = productService.find(OwnerType.provider, null, criteria, null);
        
        if(products == null){
            throw new ResourceNotFoundException();
        }
        
        String  title   = "编辑自有产品";
        response.setContentType("application/force-download");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.addHeader("Content-Type", "application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", new StringBuilder("attachment;filename=").append(URLEncoder.encode(title, "UTF-8")).append(".xls").toString());
        try (OutputStream    out = response.getOutputStream()) {
            WritableWorkbook   workbook = Workbook.createWorkbook(out);
            WritableSheet   sheet   = workbook.createSheet("导出产品", 0);
            
            //设置列宽
            sheet.setColumnView(0, 12);//产品编号
            sheet.setColumnView(1, 40);//产品名称
            sheet.setColumnView(2, 16);//品牌
            sheet.setColumnView(3, 16);//产品型号
            sheet.setColumnView(4, 10);//市场价
            sheet.setColumnView(5, 10);//售价
            sheet.setColumnView(6, 60);//产品卖点
            sheet.setColumnView(7, 12);//产地
            sheet.setColumnView(8, 10);//是否上架
            sheet.setColumnView(9, 10);//服务费
            sheet.setColumnView(10, 45);//供应商名字
            sheet.setColumnView(11, 20);//供应商市场价
            sheet.setColumnView(12, 20);//供应商零售价
            sheet.setColumnView(13, 30);//是否境外
            sheet.setColumnView(14, 30);//税率
            sheet.setColumnView(15, 30);//是否限定区域
            
            sheet.getSettings().setVerticalFreeze(2);
            
            //初始化标题
            WritableFont   titleWft  = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
            titleWft.setColour(Colour.BROWN);
            WritableCellFormat titleFmt = new WritableCellFormat(titleWft);
            titleFmt.setBackground(Colour.GRAY_25);  
            titleFmt.setAlignment(Alignment.CENTRE);
            titleFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
            WritableCellFormat priceFmt   = new WritableCellFormat(new NumberFormat("#0.00"));
            WritableCellFormat intFmt     = new WritableCellFormat(new NumberFormat("#####"));
            
            //写标题
            sheet.addCell(new Label(0, 0, "产品ID（不可编辑）", titleFmt));
            sheet.addCell(new Label(1, 0, "产品名称", titleFmt));
            sheet.addCell(new Label(2, 0, "品牌", titleFmt));
            sheet.addCell(new Label(3, 0, "产品型号", titleFmt));
            sheet.addCell(new Label(4, 0, "市场价", titleFmt));
            sheet.addCell(new Label(5, 0, "售价", titleFmt));
            sheet.addCell(new Label(6, 0, "产品卖点", titleFmt));
            sheet.addCell(new Label(7, 0, "产地", titleFmt));
            sheet.addCell(new Label(8, 0, "是否上架", titleFmt));
            sheet.addCell(new Label(9, 0, "服务费", titleFmt));
            sheet.addCell(new Label(10, 0, "供应商名称", titleFmt));
            sheet.addCell(new Label(11, 0, "供应商市场价", titleFmt));
            sheet.addCell(new Label(12, 0, "供应商零售价",titleFmt));
            sheet.addCell(new Label(13, 0, "是否境外", titleFmt));
            sheet.addCell(new Label(14, 0, "税率", titleFmt));
            sheet.addCell(new Label(15, 0, "是否限定区域",titleFmt));
            
            int row = 1;
            for (Product product : products)
            {
                sheet.addCell(new jxl.write.Number(0, row, product.getId(), intFmt));
                sheet.addCell(new Label(1, row, product.getName()));
                if (product.getBrand() != null)
                {
                    sheet.addCell(new Label(2, row, product.getBrand().getName()));
                }
                sheet.addCell(new Label(3, row, product.getProductCode()));
                sheet.addCell(new jxl.write.Number(4, row, product.getMarketPrice(), priceFmt));
                sheet.addCell(new jxl.write.Number(5, row, product.getRetailPrice(), priceFmt));
                sheet.addCell(new Label(6, row, product.getFeatures()));
                sheet.addCell(new Label(7, row, product.getProductPlace()));
                if(product.getStatus() == ProductStatus.selling) {
                    sheet.addCell(new Label(8, row, "是"));
                } else {
                    sheet.addCell(new Label(8, row, "否"));
                }
                if(product.getServiceChargeRatio() != null) {
                    sheet.addCell(new jxl.write.Number(9, row, product.getServiceChargeRatio(), priceFmt));  //扣点
                }
                
                //根据产品的ownerId 查找供应商名字（因为产品创建的时候的ownerID 如果是供应商产品 则存储的是供应商的ID ）
                if(product.getOwnerType().equals(OwnerType.provider)){
                    WebsiteAdministrator administrator = websiteAdministratorService.findOne(product.getOwnerId());
                    if(administrator != null){
                        sheet.addCell(new Label(10, row, administrator.getWorkerName())); //供应商名称
                    }else{
                        sheet.addCell(new Label(10, row, ""));
                    }
                }else{
                    sheet.addCell(new Label(10, row, ""));
                }
                
                sheet.addCell(new jxl.write.Number(11, row, product.getProvider_marketPrice(), priceFmt));  //供应商市场价
                sheet.addCell(new jxl.write.Number(12, row, product.getProvider_retailPrice(), priceFmt));  //供应商零售价
                
                //是否境外
                if(product.isEnableOverseas()){
                    sheet.addCell(new Label(13, row, "是"));
                }else{
                    sheet.addCell(new Label(13, row, "否"));
                }
                //税率
                if(product.getTaxRate() >= 0){
                    sheet.addCell(new jxl.write.Number(14, row, product.getTaxRate(), priceFmt));
                }
                
                //是否限定区域
                if(product.isEnableLimitedArea()){
                    sheet.addCell(new Label(15, row, "是"));
                }else{
                    sheet.addCell(new Label(15, row, "否"));
                }
                
                ++ row;
            }
            workbook.write();
            workbook.close();
        }
    }
    @RequiresPermissions("product:export")
    @RequestMapping(value = "/admin/product/exportSystemProduct.php", method = RequestMethod.GET)
    public @ResponseBody void exportUpdateAsExcel(HttpServletResponse response) throws UnsupportedEncodingException, IOException, WriteException
    {
        ProductCriteria criteria    = new ProductCriteria().withSelf(false).withDeleted(Boolean.FALSE);
        Page<Product> products    = productService.find(criteria, null);
        
        if(products == null){
            throw new ResourceNotFoundException();
        }
        
        String  title   = new StringBuilder("批量编辑系统产品资料").toString();
        response.setContentType("application/force-download");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.addHeader("Content-Type", "application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", new StringBuilder("attachment;filename=").append(URLEncoder.encode(title, "UTF-8")).append(".xls").toString());
        try (OutputStream    out = response.getOutputStream()) {
            WritableWorkbook   workbook = Workbook.createWorkbook(out);
            WritableSheet   sheet   = workbook.createSheet("导出产品", 0);
            
            //设置列宽
            sheet.setColumnView(0, 12);//产品编号
            sheet.setColumnView(1, 40);//产品名称
            sheet.setColumnView(2, 16);//品牌
            sheet.setColumnView(3, 16);//产品型号
            sheet.setColumnView(4, 10);//市场价
            sheet.setColumnView(5, 20);//售价
            sheet.setColumnView(6, 20);//系统售价
            sheet.setColumnView(7, 60);//产品卖点
            sheet.setColumnView(8, 12);//产地
            sheet.setColumnView(9, 20);//是否上架
            sheet.setColumnView(10, 15); //扣点
            
            sheet.getSettings().setVerticalFreeze(2);
            
            //初始化标题
            WritableFont   titleWft  = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
            titleWft.setColour(Colour.BROWN);
            WritableCellFormat titleFmt = new WritableCellFormat(titleWft);
            titleFmt.setBackground(Colour.GRAY_25);  
            titleFmt.setAlignment(Alignment.CENTRE);
            titleFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
            WritableCellFormat priceFmt   = new WritableCellFormat(new NumberFormat("#0.00"));
            WritableCellFormat intFmt     = new WritableCellFormat(new NumberFormat("#"));
            
            //写标题
            sheet.addCell(new Label(0, 0, "产品ID", titleFmt));
            sheet.addCell(new Label(1, 0, "产品名称（可编辑）", titleFmt));
            sheet.addCell(new Label(2, 0, "品牌", titleFmt));
            sheet.addCell(new Label(3, 0, "产品型号", titleFmt));
            sheet.addCell(new Label(4, 0, "市场价", titleFmt));
            sheet.addCell(new Label(5, 0, "售价（可编辑）", titleFmt));
            sheet.addCell(new Label(6, 0, "系统售价", titleFmt));
            sheet.addCell(new Label(7, 0, "产品卖点", titleFmt));
            sheet.addCell(new Label(8, 0, "产地", titleFmt));
            sheet.addCell(new Label(9, 0, "是否上架（可编辑）", titleFmt));
            sheet.addCell(new Label(10, 0, "扣点（可编辑）", titleFmt));  //扣点
            
            int row = 1;
            for (Product product : products)
            {
                sheet.addCell(new jxl.write.Number(0, row, product.getId(), intFmt));
                sheet.addCell(new Label(1, row, product.getName()));
                if (product.getBrand() != null)
                {
                    sheet.addCell(new Label(2, row, product.getBrand().getName()));
                }
                sheet.addCell(new Label(3, row, product.getProductCode()));
                sheet.addCell(new jxl.write.Number(4, row, product.getMarketPrice(), priceFmt));
                sheet.addCell(new jxl.write.Number(5, row, product.getRetailPrice(), priceFmt));
                sheet.addCell(new jxl.write.Number(6, row, product.getSystemPrice(), priceFmt));
                sheet.addCell(new Label(7, row, product.getFeatures()));
                sheet.addCell(new Label(8, row, product.getProductPlace()));
                if(product.getStatus() == ProductStatus.selling) {
                    sheet.addCell(new Label(9, row, "是"));
                } else {
                    sheet.addCell(new Label(9, row, "否"));
                }
                sheet.addCell(new jxl.write.Number(10, row, product.getServiceChargeRatio(), priceFmt));
                
                ++ row;
            }
            workbook.write();
            workbook.close();
        }
    }
    
    //批量上传自有产品
    @RequiresPermissions("product:import")
    @RequestMapping(value = "/admin/product/batch/import4Create.php", method = RequestMethod.POST) 
    public @ResponseBody SimpleJsonResponse batchCreate(@RequestParam(value = "excelFile") MultipartFile file,  WebsiteAdministrator admin) {
        
        if (file == null || file.isEmpty()) {
            return new SimpleJsonResponse<>(false, "没有文件被上传");
        }
        int successCount = 0; 
        try (InputStream ins    = file.getInputStream())
        {
            Workbook    workbook    = Workbook.getWorkbook(ins);
            int sheetSize   = workbook.getNumberOfSheets();
            
            Product product = null;
            for (int i = 0; i < sheetSize; ++ i)
            {
                Sheet   sheet   = workbook.getSheet(i);
                
                int rows    = sheet.getRows();
                int columns = sheet.getColumns();
                if (columns != 15) {
                    continue;
                }
                for (int r = 1; r < rows; ++ r)
                {
                    try {
                        product = new Product();
                        String name = sheet.getCell(0, r).getContents().trim();
                        String brandName = sheet.getCell(1, r).getContents().trim();
                        if (name.isEmpty() || brandName.isEmpty()) {
                            if (logger.isWarnEnabled()) {
                                logger.warn("Batch update Product skiped row, name {}, brandName {}", name, brandName);
                            }   
                            continue;
                        }
                        product.setName(name);
                        Brand   brand   = brandService.findOne(brandName);
                        if (brand == null) { 
                            brand   = new Brand();
                            brand.setName(brandName);
                            brand.setSelf(true);
                            brand   = brandService.save(brand);
                        } 
                        product.setBrand(brand);
                        
                        String productCode = sheet.getCell(2, r).getContents().trim().toUpperCase();
                        if(productCode.isEmpty()){
                            if (logger.isWarnEnabled()) {
                                logger.warn("Batch Update Product skiped row, productCode {}", productCode);
                            }   
                            continue;
                        }
                        product.setProductCode(productCode);
                        if(productService.exists(admin, brand, product.getProductCode())){
                            if (logger.isWarnEnabled()) {
                                logger.warn("Batch update Product skiped row, the product has exited.");
                            }  
                            continue;
                        }
                        Double marketPrice     = ExcelReaderUtil.readDouble(sheet.getCell(3, r));
                        Double retailPrice     = ExcelReaderUtil.readDouble(sheet.getCell(4, r));
                        
                        if((marketPrice > 0 && marketPrice < 9999999)) {
                            if(admin.isProvider()) {
                                product.setProvider_marketPrice(marketPrice);
                            }
                            product.setMarketPrice(marketPrice);
                        } else {
                            continue;
                        }
                        if(retailPrice > 0 && retailPrice < 9999999) {
                            if(admin.isProvider()) {
                                product.setProvider_retailPrice(retailPrice);
                            }
                            product.setRetailPrice(retailPrice);
                        } else {
                            continue;
                        }

                        product.setFeatures(sheet.getCell(5, r).getContents().trim());
                        product.setProductPlace(sheet.getCell(6, r).getContents().trim());
                        //供应商
                        String providerName = sheet.getCell(7, r).getContents().trim();
                        if(providerName == null){
                            continue;
                        }else{
                            //根据输入的供应商名字查找数据
                            WebsiteAdministrator administrator = websiteAdministratorService.findOne(providerName);
                            if(administrator == null){ //输入的供应商没有数据 也continue出去
                                continue;
                            }else{
                                product.setOwnerId(administrator.getId());
                                product.setOwnerType(OwnerType.provider); 
                            }
                        }
                        //供应商市场价和供应商零售价
                        Double ProviderMarketPrice = ExcelReaderUtil.readDouble(sheet.getCell(8, r));
                        if(ProviderMarketPrice == null || ProviderMarketPrice <= 0){
                            continue;
                        }else{
                            product.setProvider_marketPrice(ProviderMarketPrice);
                        }
                        Double ProviderRetailPrice = ExcelReaderUtil.readDouble(sheet.getCell(9, r));
                        if(ProviderRetailPrice == null || ProviderRetailPrice <= 0){
                            continue;
                        }else{
                            product.setProvider_retailPrice(ProviderRetailPrice);
                        }
                        
                        Boolean shelf = ExcelReaderUtil.readBoolean(sheet.getCell(10, r));
                        if (shelf != null && shelf) {
                            product.setStatus(ProductStatus.selling);
                        } else {
                            product.setStatus(ProductStatus.basic_editing);
                        }
                        
                        //服务费
                        Double serviceChargeRatio = ExcelReaderUtil.readDouble(sheet.getCell(11, r));
                        if(serviceChargeRatio != null){
                            if(serviceChargeRatio > 0.1 && serviceChargeRatio <100){
                                product.setServiceChargeRatio(serviceChargeRatio);
                            }
                        }
                        //是否境外
                        product.setEnableOverseas(getBooleanCellValue(sheet.getCell(12, r)));
                        //税率
                        Double taxRate = ExcelReaderUtil.readDouble(sheet.getCell(13, r));
                        if(taxRate != null){
                            if(taxRate > 0 && taxRate < 100){
                                product.setTaxRate(taxRate);
                            }
                        }
                        
                        //是否限定区域
                        product.setEnableLimitedArea(getBooleanCellValue(sheet.getCell(14, r)));
                        
                        List<StorageUnit>   skuList = new ArrayList<>();
                        StorageUnit storageUnit = new StorageUnit();
                        storageUnit.setEffectBarType(StorageUnitEffectBarType.bar_code);
                        storageUnit.setProperty1("实物");
                        storageUnit.setStatus(StorageUnitStatus.selling);
                        skuList.add(storageUnit);
                        
                        productService.saveSelfProduct(admin, product, new ProductDescription(0, ""), Collections.<ProductImage>emptyList(), skuList);
                        successCount++;
                    } catch(Throwable t){
                        if (logger.isWarnEnabled()) {
                            logger.warn("Batch update Product skiped row, Product  Exception", product.getId());
                        }
                    }
                }
            }
            workbook.close();
        }catch(Throwable t){
            if (logger.isWarnEnabled()) {
                logger.warn(null, t);
            }
            return new SimpleJsonResponse<>(false, "您上传的文件数据不合法，请核实数据重新上传！");
        }
        return new SimpleJsonResponse<>(true, successCount);
    }
    
    //批量编辑自有产品
    @RequiresPermissions("product:import")
    @RequestMapping(value = "/admin/product/batch/import4UpdateSelf.php", method = RequestMethod.POST) 
    public @ResponseBody SimpleJsonResponse batchUpdateSelf(@RequestParam(value = "excelFile") MultipartFile file,  WebsiteAdministrator admin) {
        
        if (file == null || file.isEmpty()) {
            return new SimpleJsonResponse<>(false, "没有文件被上传");
        }
        int successCount = 0; 
        try (InputStream ins    = file.getInputStream())
        {
            Workbook    workbook    = Workbook.getWorkbook(ins);
            int sheetSize   = workbook.getNumberOfSheets();
            
            Product product = null;
            for (int i = 0; i < sheetSize; ++ i)
            {
                Sheet   sheet   = workbook.getSheet(i);
                
                int rows    = sheet.getRows();
                int columns = sheet.getColumns();
                if (columns != 16)
                {
                    continue;
                }
                for (int r = 1; r < rows; ++ r)
                {
                    Integer id  = ExcelReaderUtil.readInt(sheet.getCell(0, r));
                    if (id != null && id > 0) {
                        try {
                            product = productService.findOne(id);
                            if(product == null){
                                if (logger.isWarnEnabled()) {
                                    logger.warn("Batch update Product have skiped row, product{}", product);
                                }
                                continue;
                            }
                           
                            String name = sheet.getCell(1, r).getContents().trim();
                            String brandName = sheet.getCell(2, r).getContents().trim();
                            if (name.isEmpty() || brandName.isEmpty())
                            {
                                if (logger.isWarnEnabled()) {
                                    logger.warn("Batch update Product skiped row, name {}, brandName {}", name, brandName);
                                }   
                                continue;
                            }
                            product.setName(name);

                            Brand   brand   = brandService.findOne(brandName);
                            if (brand == null) { 
                                brand   = new Brand();
                                brand.setName(brandName);
                                brand.setSelf(true);
                                brand   = brandService.save(brand);
                            } 
                            product.setBrand(brand);
                            String productCode = sheet.getCell(3, r).getContents().trim().toUpperCase();
                            if(productCode.isEmpty()){
                                if (logger.isWarnEnabled()) {
                                    logger.warn("Batch Update Product skiped row, productCode {}", productCode);
                                }   
                                continue;
                            }
                            if(productService.exists(admin, brand, product.getProductCode(), product.getId())){
                                if (logger.isWarnEnabled()) {
                                    logger.warn("Batch update Product skiped row, the product has exited.");
                                }  
                                continue;
                            }
                            product.setProductCode(productCode);

                            Double marketPrice     = ExcelReaderUtil.readDouble(sheet.getCell(4, r));
                            Double retailPrice     = ExcelReaderUtil.readDouble(sheet.getCell(5, r));
                            if((marketPrice > 0 && marketPrice < 9999999)) {
                                if(admin.isProvider()) {
                                    product.setProvider_marketPrice(marketPrice);
                                } else {
                                    product.setMarketPrice(marketPrice);
                                }
                            } else {
                                continue;
                            }
                            if(retailPrice > 0 && retailPrice < 9999999) {
                                if(admin.isProvider()) {
                                    product.setProvider_retailPrice(retailPrice);
                                } else {
                                    product.setRetailPrice(retailPrice);
                                }
                            } else {
                                continue;
                            }

                            product.setFeatures(sheet.getCell(6, r).getContents().trim());
                            product.setProductPlace(sheet.getCell(7, r).getContents().trim());
                            
                            Boolean shelf = ExcelReaderUtil.readBoolean(sheet.getCell(8, r), false);
                            if (shelf)  {
                                product.setStatus(ProductStatus.selling);
                            } else {
                                product.setStatus(ProductStatus.basic_editing);
                            }
                            product.setServiceChargeRatio(ExcelReaderUtil.readDouble(sheet.getCell(9, r), 0));
                            
                            //供应商
                            String  providerName = sheet.getCell(10, r).getContents().trim(); //获取输入的供应商名称
                            if(providerName == null){ //供应商名称不能为空
                                continue;
                            }else{
                                WebsiteAdministrator administrator = websiteAdministratorService.findOne(providerName);
                                if(administrator == null){ //如果为空 说明输入的信息不存在
                                    continue;
                                }else{
                                    product.setOwnerId(administrator.getId()); //存储供应商名称ID 
                                    product.setOwnerType(OwnerType.provider); 
                                }
                            }
                            //供应商市场价
                            product.setProvider_marketPrice(ExcelReaderUtil.readDouble(sheet.getCell(11, r)));
                            //供应商零售价
                            product.setProvider_retailPrice(ExcelReaderUtil.readDouble(sheet.getCell(12, r)));
                            
                            
                            //是否境外
                            product.setEnableOverseas(getBooleanCellValue(sheet.getCell(13, r)));
                            //税率
                            product.setTaxRate(ExcelReaderUtil.readDouble(sheet.getCell(14, r)));
                            //是否限定区域
                            product.setEnableLimitedArea(getBooleanCellValue(sheet.getCell(15, r)));
                            
                            productService.saveSelfProduct(admin, product, productDescriptionService.findOne(product.getId()), productService.findProductImages(product), productService.findStorageUnits(product));
                            successCount++;
                        } catch(Throwable t){
                            if (logger.isWarnEnabled()) {
                                logger.warn("Batch update Product skiped row, Product  Exception", product.getId());
                            }
                        }
                    }
                }
            }
            workbook.close();
        }catch(Throwable t){
            t.printStackTrace();
            if (logger.isWarnEnabled()) {
                logger.warn(null, t);
            }
            return new SimpleJsonResponse<>(false, "您上传的文件数据不合法，请核实数据重新上传！");
        }
        return new SimpleJsonResponse<>(true, successCount);
    }
    @RequiresPermissions("product:import")
    @RequestMapping(value = "/admin/product/batch/import4UpdateSystem.php", method = RequestMethod.POST) 
    public @ResponseBody SimpleJsonResponse batchUpdateSystem(@RequestParam(value = "excelFile") MultipartFile file,  WebsiteAdministrator admin) {
        
        if (file == null || file.isEmpty()) {
            return new SimpleJsonResponse<>(false, "没有文件被上传");
        }
        
        try (InputStream ins    = file.getInputStream())
        {
            Workbook    workbook    = Workbook.getWorkbook(ins);
            int sheetSize   = workbook.getNumberOfSheets();
            
            Product product = null;
            for (int i = 0; i < sheetSize; ++ i)
            {
                Sheet   sheet   = workbook.getSheet(i);
                
                int rows    = sheet.getRows();
                int columns = sheet.getColumns();
                if (columns != 11)
                {
                    continue;
                }
                for (int r = 1; r < rows; ++ r)
                {
                    Integer id  = ExcelReaderUtil.readInt(sheet.getCell(0, r));
                    if (id > 0) {
                        try {
                            product = productService.findOne(id);
                            if(product == null){
                                if (logger.isWarnEnabled()) {
                                    logger.warn("Batch update Product have skiped row, product{}", product);
                                }
                                continue;
                            }
                            if(admin.isProvider()) {
                                if (logger.isWarnEnabled()) {
                                    logger.warn("Batch update Product have skiped row, You can not modify do not belong their products", product);
                                }
                                continue;
                            }
                            if(product.getOwnerType() != OwnerType.system && product.getOwnerType() != OwnerType.jingdong) {
                                 if (logger.isWarnEnabled()) {
                                    logger.warn("Batch update Product have skiped row, You can not modify do not belong their products", product);
                                }
                                continue;
                            }
                            String name = sheet.getCell(1, r).getContents().trim();
                            if (name.isEmpty())
                            {
                                if (logger.isWarnEnabled()) {
                                    logger.warn("Batch update Product skiped row, name {}", name);
                                }   
                                continue;
                            }
                            product.setName(name);
                            Double retailPrice     = ExcelReaderUtil.readDouble(sheet.getCell(5, r));
                            if(retailPrice > 0 && retailPrice < 9999999) {
                                product.setRetailPrice(retailPrice);
                            } else {
                                continue;
                            }
                            product.setRetailPrice(retailPrice);
                            Boolean shelf = ExcelReaderUtil.readBoolean(sheet.getCell(9, r), false);
                            if (shelf)  {
                                product.setStatus(ProductStatus.selling);
                            } else {
                                product.setStatus(ProductStatus.basic_editing);
                            }
                             product.setServiceChargeRatio(ExcelReaderUtil.readDouble(sheet.getCell(10, r), 0));
                            productService.saveSelfProduct(admin, product, productDescriptionService.findOne(product.getId()), productService.findProductImages(product), productService.findStorageUnits(product));
                            
                        } catch(Throwable t){
                            if (logger.isWarnEnabled()) {
                                logger.warn("Batch update Product skiped row, Product  Exception", product.getId());
                            }
                        }
                    }
                }
            }
            workbook.close();
        }catch(Throwable t){
            if (logger.isWarnEnabled()) {
                logger.warn(null, t);
            }
            return new SimpleJsonResponse<>(false, "您上传的文件数据不合法，请核实数据重新上传！");
        }
        return new SimpleJsonResponse<>(true, null);
    }
    
    private static void setProductProperty(int productId, List<ProductPropertyValue> properties, String key, String value)
    {
        for (ProductPropertyValue ppv : properties)
        {
            if (ppv.getPropertyKey().equals(key))
            {
                ppv.setValue(value);
                return;
            }
        }
        ProductPropertyValue    ppv = new ProductPropertyValue();
        ppv.setProductId(productId);
        ppv.setPropertyKey(key);
        ppv.setValue(value);
        properties.add(ppv);
    }
    
    private Map<String, String> toMap(List<ProductPropertyValue> properties)
    {
        Map<String, String> ret = new HashMap<>();
        for (ProductPropertyValue ppv : properties)
        {
            ret.put(ppv.getPropertyKey(), ppv.getValue());
        }
        return ret;
    }
    
    private static boolean getBooleanCellValue(Cell cell)
    {
        String  content = cell.getContents().trim();
        if(content.equalsIgnoreCase("Y") || content.equals("是") || content.equalsIgnoreCase("Yes"))
        {
            return  true;
        }
        return false;
    }
}
