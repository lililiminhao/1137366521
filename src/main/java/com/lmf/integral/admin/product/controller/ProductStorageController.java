/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.product.controller;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.enums.OwnerType;
import com.lmf.common.util.PagerSpec;
import com.lmf.common.util.StringsUtil;
import com.lmf.enums.ProductStatus;
import com.lmf.product.entity.Brand;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.ProductCate;
import com.lmf.product.entity.ProductStorage;
import com.lmf.product.entity.StorageUnit;
import com.lmf.product.service.BrandService;
import com.lmf.product.service.ProductCateService;
import com.lmf.product.service.ProductService;
import com.lmf.product.service.ProductStorageService;
import com.lmf.product.service.StorageUnitService;
import com.lmf.product.vo.ProductCriteria;
import com.lmf.product.vo.SimpleStorageSummary;
import com.lmf.website.entity.WebsiteAdministrator;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
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
 * 产品库存管理
 * @author shenzhixiong
 */
@Controller
public class ProductStorageController {
    
    @Autowired 
    private ProductService productService;
    
    @Autowired
    private StorageUnitService storageUnitService;
    
    @Autowired
    private ProductStorageService productStorageService; 
    
    @Autowired
    private BrandService    brandService;
    
    @Autowired
    private ProductCateService productCateService;
    
    private final static Logger logger  = LoggerFactory.getLogger(ProductStorageController.class);
    
    @RequiresPermissions("stock:view")
    @RequestMapping(value = "/admin/product/storages.php", method = RequestMethod.GET)
    public String  list(@RequestParam(value = "kw", required = false) String keyword,
                        @RequestParam(value = "status", defaultValue = "selling") ProductStatus status,
                        @RequestParam(value = "brand", required = false) Brand brand,
                        @RequestParam(value = "cate", required = false) ProductCate cate,
                        @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager,
                        WebsiteAdministrator admin,
                        Model model) throws UnsupportedEncodingException
    {   
        StringBuilder   link = new StringBuilder("/jdvop/admin/product/storages.php?page=[:page]");
        if(keyword != null && !keyword.isEmpty()){
            link.append("&kw=").append(keyword);
        }
        if(status != null){
            link.append("&status=").append(status.name());
        }
        if(brand != null){
            link.append("&brand=").append(brand.getId());
        }
        if(cate != null){
            link.append("&cate=").append(cate.getId());
        }
        ProductCriteria criteria = new ProductCriteria().withKeyword(keyword).withStatus(status).withBrand(brand).withCate(cate).withSelf(true);
        Page<Product> products = null;
        if(admin.isProvider()){
            products = productService.find(OwnerType.provider, admin.getId(), criteria, pager);
        } else {
            products = productService.find(criteria, pager);
        }
        if(products.hasContent())
        {
            Map<Integer, StorageUnit> skuMap = new HashMap<>();
            List<Integer> productIds = new ArrayList();
            for(Product product : products.getContent()){
                productIds.add(product.getId());
                
                //查询该产品的SKU
                StorageUnit sku = productService.findStorageUnits(product).get(0);
                skuMap.put(sku.getProductId(), sku);
            }
            Map<Integer, SimpleStorageSummary> storageSummary = productStorageService.findStorageSummaries(productIds);
            model.addAttribute("storageSummary", storageSummary);
            model.addAttribute("skuMap", skuMap);
        }
        model.addAttribute("products", products);
        model.addAttribute("rootCates", productCateService.rootCates());
        model.addAttribute("brands", brandService.findSelf(null, null));
        model.addAttribute("status", ProductStatus.values());
        model.addAttribute("link", link.toString());
        model.addAttribute("admin", admin);
        model.addAttribute("productStorageService", productStorageService);
        
        return "/admin/storage/list";
    }
    
    @RequiresPermissions("stock:edit")
    @RequestMapping(value = "/admin/product/storage/replenish.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse replenish(@RequestParam(value = "id") int skuId,
                                                      @RequestParam(value = "quantityNum") int quantityNum,
                                                      WebsiteAdministrator admin)
    {
        StorageUnit sku = storageUnitService.findOne(skuId);
        if(sku == null) {
            return new SimpleJsonResponse(false, "数据异常，操作失败");
        }
        Product product = productService.findOne(sku.getProductId());
        if(product == null){
            return new SimpleJsonResponse(false, "数据异常,操作失败");
        }
        if(admin.isProvider() && !product.belongsTo(admin)) {
            return new SimpleJsonResponse(false, "您无法操作不属于您的产品");
        }
        
        productStorageService.replenish(sku.getId(), quantityNum);
        
        return new SimpleJsonResponse(true, "/admin/product/storages.php");
    }
    
    @RequiresPermissions("stock:edit")
    @RequestMapping(value = "/admin/product/storage/resetStorage.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse resetStorage(@RequestParam(value = "id") int skuId,
                                                      @RequestParam(value = "storageNum") int storageNum,
                                                      WebsiteAdministrator admin)
    {
        StorageUnit sku = storageUnitService.findOne(skuId);
        if(sku == null) {
            return new SimpleJsonResponse(false, "数据异常，操作失败");
        }
        Product product = productService.findOne(sku.getProductId());
        if(product == null){
            return new SimpleJsonResponse(false, "数据异常,操作失败");
        }
        if(admin.isProvider() && !product.belongsTo(admin)) {
            return new SimpleJsonResponse(false, "您无法操作不属于您的产品");
        }
        
        productStorageService.setStorageNum(sku.getId(), storageNum);
        
        return new SimpleJsonResponse(true, "/product/storages.php");
    }
    
    @RequiresPermissions("stock:export")
    @RequestMapping(value = "/admin/product/storage/export.php", method = RequestMethod.GET)
    public @ResponseBody void export(@RequestParam(value = "kw", required = false) String keyword,
                                     @RequestParam(value = "status", defaultValue = "selling") ProductStatus status,
                                     @RequestParam(value = "brand", required = false) Brand brand,
                                     @RequestParam(value = "cate", required = false) ProductCate cate,
                                     WebsiteAdministrator admin,
                                     HttpServletResponse response) throws IOException, WriteException
    {   
        //准备Excel数据
        ProductCriteria criteria = new ProductCriteria().withSelf(true).withKeyword(keyword).withBrand(brand).withCate(cate).withStatus(status);
        Page<Product>   products = null;
        if(admin.isProvider()){
            products    = productService.find(OwnerType.provider, admin.getId(), criteria, null);
        } else {
            products    = productService.find(criteria, null);
        }
        response.setContentType("application/force-download");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.addHeader("Content-Type", "application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", new StringBuilder("attachment;filename=").append(URLEncoder.encode("产品库存导出", "UTF-8")).append(".xls").toString());
        
        try (OutputStream out   = response.getOutputStream())
        {
            WritableWorkbook   workbook = Workbook.createWorkbook(out);
            WritableSheet   sheet   = workbook.createSheet("导出产品库存列表", 0);
            //设置列宽
            sheet.setColumnView(0, 8);//产品ID
            sheet.setColumnView(1, 8);//SKU ID
            sheet.setColumnView(2, 30);//产品名称
            sheet.setColumnView(3, 20);//产品型号
            sheet.setColumnView(4, 24);//产品品牌
            sheet.setColumnView(5, 14);//系统编码
            sheet.setColumnView(6, 14);//商家编码
            sheet.setColumnView(7, 14);//国际条码
            sheet.setColumnView(8, 10);//销售量	
            sheet.setColumnView(9, 10);//锁定数量	
            sheet.setColumnView(10, 10);//运输数量	
            sheet.setColumnView(11, 10);//库存余量
            sheet.setColumnView(12, 20);//补货数量
            
            sheet.getSettings().setVerticalFreeze(1);
            
            //初始化标题
            WritableFont   titleWft  = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
            titleWft.setColour(Colour.BROWN);
            WritableCellFormat titleFmt = new WritableCellFormat(titleWft);
            titleFmt.setBackground(Colour.GRAY_25);      
            
            WritableCellFormat intFmt   = new WritableCellFormat(new NumberFormat("####0"));
            
            sheet.addCell(new Label(0, 0, "产品ID", titleFmt));
            sheet.addCell(new Label(1, 0, "SKU ID", titleFmt));
            sheet.addCell(new Label(2, 0, "产品名称", titleFmt));
            sheet.addCell(new Label(3, 0, "产品型号", titleFmt));
            sheet.addCell(new Label(4, 0, "产品品牌", titleFmt));
            sheet.addCell(new Label(5, 0, "系统编码", titleFmt));
            sheet.addCell(new Label(6, 0, "商家编码", titleFmt));
            sheet.addCell(new Label(7, 0, "国际条码", titleFmt));
            sheet.addCell(new Label(8, 0, "销售量", titleFmt));
            sheet.addCell(new Label(9, 0, "锁定数量", titleFmt));
            sheet.addCell(new Label(10, 0, "运输数量", titleFmt));
            sheet.addCell(new Label(11, 0, "库存余量", titleFmt));
            sheet.addCell(new Label(12, 0, "补货数量(可编辑)", titleFmt));
            
            if(products.hasContent())
            {
                int row = 0;
                for(Product product : products.getContent())
                {
                    ++ row;
                    List<StorageUnit> currentSkus = productService.findStorageUnits(product);
                    for(StorageUnit currentSku : currentSkus) {
                        ProductStorage currentStorage = productStorageService.findOne(currentSku.getId());

                        sheet.addCell(new jxl.write.Number(0, row, product.getId(), intFmt));
                        sheet.addCell(new jxl.write.Number(1, row, currentSku.getId(), intFmt));
                        sheet.addCell(new Label(2, row, product.getName()));
                        sheet.addCell(new Label(3, row, product.getProductCode()));
                        sheet.addCell(new Label(4, row, product.getBrand().getName()));
                        sheet.addCell(new Label(5, row, currentSku.getBarCode()));
                        String outerBarCode = currentSku.getOuterBarCode();
                        String internationalBarCode = currentSku.getInternationalBarCode();
                        if(outerBarCode == null || outerBarCode.isEmpty())
                        {
                            outerBarCode = "无";
                        }
                        if(internationalBarCode == null || internationalBarCode.isEmpty())
                        {
                            internationalBarCode = "无";
                        }
                        sheet.addCell(new Label(6, row, outerBarCode));
                        sheet.addCell(new Label(7, row, internationalBarCode));
                        sheet.addCell(new jxl.write.Number(8, row, currentStorage.getSoldedNum(), intFmt));
                        sheet.addCell(new jxl.write.Number(9, row, currentStorage.getLockedNum(), intFmt));
                        sheet.addCell(new jxl.write.Number(10, row, currentStorage.getTransitNum(), intFmt));
                        sheet.addCell(new jxl.write.Number(11, row, currentStorage.getUseableNum(), intFmt));
                        sheet.addCell(new jxl.write.Number(12, row, 0, intFmt));
                    }
                }
            }
            workbook.write();
            workbook.close();
        }
    }
    
    @RequiresPermissions("stock:edit")
    @RequestMapping(value = "/admin/product/storage/import.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public  @ResponseBody  SimpleJsonResponse<String> importStorage(@RequestParam(value = "excelFile") MultipartFile file, 
                                                                    final WebsiteAdministrator admin, 
                                                                    final HttpSession session) throws IOException, BiffException {
        try (InputStream ins    = file.getInputStream())
        {
            Workbook    workbook    = Workbook.getWorkbook(ins);
            int sheetSize   = workbook.getNumberOfSheets();
            Product product = null ;
            for (int i = 0; i < sheetSize; ++ i)
            {
                Sheet   sheet   = workbook.getSheet(i);
                int rows    = sheet.getRows();
                int columns = sheet.getColumns();
                if (columns != 13)
                {
                    continue;
                }
                for (int r = 1; r < rows; ++ r)
                {
                    int productId = getIntCellValue(sheet.getCell(0, r));
                    int skuId   =   getIntCellValue(sheet.getCell(1, r));
                    if(productId <= 0 || skuId <= 0){
                        if (logger.isWarnEnabled()) {
                            logger.warn("Import ProdctStorage have skiped row, productId{}, skuId{}", productId, skuId);
                        }  
                        continue;
                    }
                    try{
                        product = productService.findOne(productId);
                        if(product == null){
                            if (logger.isWarnEnabled()) {
                                logger.warn("Import ProdctStorage have skiped row, product{}", product);
                            }  
                            continue;
                        }
                        if(admin.isProvider()) {
                            if(!product.belongsTo(admin)){
                                if (logger.isWarnEnabled()) {
                                    logger.warn("Import ProdctStorage have skiped row, productId{}, skuId{}", productId, skuId);
                                }  
                                continue;
                            }
                        }
                        StorageUnit sku = storageUnitService.findOne(skuId);
                        if(product.getId() != sku.getProductId()){
                            if (logger.isWarnEnabled()) {
                                logger.warn("Import ProdctStorage have skiped row, productId{}, skuId{}", productId, skuId);
                            }  
                            continue;
                        }
                        String outerBarCode = sheet.getCell(6, r).getContents().trim();
                        String internationalBarCode = sheet.getCell(7, r).getContents().trim();
                        if(outerBarCode.isEmpty() || internationalBarCode.isEmpty())
                        {
                            if (logger.isWarnEnabled()) {
                                logger.warn("Import ProdctStorage have skiped row, outerBarCode{}, internationalBarCode{}", outerBarCode, internationalBarCode);
                            }  
                            continue;
                        }
                        sku.setOuterBarCode(outerBarCode);
                        sku.setInternationalBarCode(internationalBarCode);
                        
                        int defectiveNum = getIntCellValue(sheet.getCell(11, r));
                        int quantity = getIntCellValue(sheet.getCell(12, r));
                        
                        productStorageService.modify(sku, quantity, defectiveNum, 0, 0, 0);
                    }catch(Throwable t){
                        if (logger.isWarnEnabled()) {
                            logger.warn("Import ProdctStorage have skiped row, product{}", product);
                        }
                    }
                }
            }
            workbook.close();
        }catch(Throwable t){
            if (logger.isWarnEnabled()) {
                logger.warn(null, t);
            }
            return new SimpleJsonResponse<>(false, "您上传的文件不是合法的Excel2003~2007格式，请检查后缀名是.xls");
        }
        return new SimpleJsonResponse<>(true, null);
    }
    
    
    private static int getIntCellValue(Cell cell)
    {
        String  content = cell.getContents().trim();
        Integer tmp     = StringsUtil.toInteger(content);
        if (tmp == null)
        {
            return 0;
        }
        return tmp;
    }
}
