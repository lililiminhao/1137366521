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
import com.lmf.common.exceptions.ResourceNotFoundException;
import com.lmf.common.util.PagerSpec;
import com.lmf.enums.ProductStatus;
import com.lmf.product.entity.*;
import com.lmf.product.enums.StorageUnitEffectBarType;
import com.lmf.product.enums.StorageUnitStatus;
import com.lmf.product.service.*;
import com.lmf.product.vo.ProductCriteria;
import com.lmf.product.vo.SimpleStorageSummary;
import com.lmf.sys.entity.ShipmentCompany;
import com.lmf.sys.service.ShipmentCompanyService;
import com.lmf.website.entity.WebsiteAdministrator;
import com.lmf.website.service.WebsiteAdministratorService;
import com.lmf.website.vo.WebsiteAdministratorCriteria;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author shenzhixiong
 */
@Controller("adminProductController")
public class ProductController {
    
    @Autowired
    private ProductCateService  productCateService;
    
    @Autowired
    private ProductService  productService;
    
    @Autowired
    private ProductPropertyValueService productPropertyValueService;
    
    @Autowired
    private ProductStorageService productStorageService;
    
    @Autowired
    private ProductImageService productImageService;
    
    @Autowired
    private BrandService brandService;
    
    @Autowired
    private ShipmentCompanyService shipmentCompanyService;
    
    @Autowired
    private ProductDescriptionService productDescriptionService;
    
    @Autowired
    private WebsiteAdministratorService websiteAdministratorService;
    
    @RequiresPermissions("product:view")
    @RequestMapping(value = "/admin/products.php", method = RequestMethod.GET)
    public String  list(@RequestParam(value = "kw", required = false) String keyword,
                        @RequestParam(value = "status", defaultValue = "selling") ProductStatus status,
                        @RequestParam(value = "b", required = false) Brand brand,
                        @RequestParam(value = "c", required = false) ProductCate cate,
                        @RequestParam(value = "sp", required = false) Double minPrice,
                        @RequestParam(value = "mp", required = false) Double maxPrice,
                        @RequestParam(value = "self", required = false) Boolean self,
                        @RequestParam(value = "type", required = false) OwnerType type,
                        @RequestParam(value = "enableOverseas", required = false) Boolean enableOverseas, //是否跨境
                        @RequestParam(value = "p", required = false, defaultValue = "0") int provider,
                        @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager,
                        WebsiteAdministrator admin, Model model) throws UnsupportedEncodingException
    {   
        
        Page<WebsiteAdministrator> websiteAdministrator = websiteAdministratorService.find(new WebsiteAdministratorCriteria().withKeyword(keyword).withProvider(true).withEnabled(true), null);
        
        ProductCriteria criteria = new ProductCriteria().withKeyword(keyword).withBrand(brand).withCate(cate).withPrice(minPrice, maxPrice).withStatus(status).withDeleted(Boolean.FALSE).withSelf(self).withEnableOverseas(enableOverseas).withProviderId(provider);
        
        Page<Product>  products = null;
        if(admin.isProvider()) {
            products = productService.find(OwnerType.provider, admin.getId(), criteria, pager);
        } else {
            criteria.withOwnerType(type);
            products  = productService.find(criteria, pager);
        }
        
        List<Brand>    brands      = brandService.find(null, null).getContent();
        
        StringBuilder  link = new StringBuilder("/jdvop/admin/products.php?page=[:page]");
        if (keyword != null && !keyword.isEmpty()) {
            link.append("&kw=").append(URLEncoder.encode(keyword, "UTF-8"));
        }
        if (status != null) {
            link.append("&status=").append(status.name());
            model.addAttribute("status", status.name());
        }
        if (brand != null) {
            link.append("&b=").append(brand.getId());
        }
        if(cate != null) {
            link.append("&c=").append(cate.getId());
        }
        
        if(self != null) {
            link.append("&self=").append(self);
            model.addAttribute("self", self);
        }
        
        if(type != null) {
            link.append("&type=").append(type.name());
            model.addAttribute("ownerType", type.name());
        } 
        
        if(minPrice != null) {
            link.append("&sp=").append(minPrice);
            model.addAttribute("sp", minPrice);
        } 
        
        if(maxPrice != null) {
            link.append("&mp=").append(maxPrice);
            model.addAttribute("mp", maxPrice);
        }
        if(provider != 0){
        	link.append("&p=").append(provider);
        }
        
        //查询库存
        if(products != null) {
            if(products.hasContent())
            {
                List<Integer> productIds = new ArrayList();
                for(Product product : products.getContent()){
                    productIds.add(product.getId());
                }
                Map<Integer, SimpleStorageSummary> storageSummary = productStorageService.findStorageSummaries(productIds);
                model.addAttribute("storageSummary", storageSummary);
            }
        }
        
        model.addAttribute("products", products);
        model.addAttribute("brands", brands);
        model.addAttribute("cate", cate);
        model.addAttribute("link", link.toString());
        model.addAttribute("productCateService", productCateService);
        model.addAttribute("productService", productService);
        model.addAttribute("ownerTypes", OwnerType.values());
        model.addAttribute("admin", admin);
        model.addAttribute("websiteAdministrator", websiteAdministrator);
        
//        if(admin.isProvider()) {
//            return "admin/product/provider/list";
//        }
        return "admin/product/list";
    }
    
    @RequiresPermissions("product:create")
    @RequestMapping(value = "/admin/product/add.php", method = RequestMethod.GET)
    public String add(WebsiteAdministrator admin, Model model) 
    {
        model.addAttribute("admin", admin);
        model.addAttribute("brands", brandService.findAll());
        model.addAttribute("productCateService", productCateService);
        model.addAttribute("shipmentCompanys", shipmentCompanyService.all());
        //查询供应商全部信息
        model.addAttribute("providers", websiteAdministratorService.find(new WebsiteAdministratorCriteria().withProvider(true).withEnabled(true), null));
        
        return "admin/product/form";
    }
    
    @RequiresPermissions("product:create")
    @RequestMapping(value = "/admin/product/add.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse add(@Valid Product product, BindingResult resultProduct,
                                                @RequestParam("brand") Brand brand,
                                                @RequestParam("cates[]") ProductCate[] cates,
                                                @RequestParam("header_image[]") int[] headerImageIDs,
                                                @RequestParam(value = "preferShipmentCompanyId", required = false) Integer preferShipmentCompanyId,
                                                @RequestParam(value = "onSellImmediate", defaultValue = "false") Boolean onSellImmediate,
                                                @RequestParam(value = "description", required = false) String description,
                                                @RequestParam(value= "serviceChargeRatio", required = false) Double serviceChargeRatio,   //服务费
//                                              @RequestParam(value = "provider", defaultValue = "false") boolean provider,//是否为供应商产品
                                                @RequestParam(value = "providerId", required =false) Integer providerId,//供应商ID
                                                @RequestParam(value = "providerMarketPrice", required = false) Double providerMarketPrice,//供应商市场价
                                                @RequestParam(value = "providerRetailPrice", required = false) Double providerRetailPrice,//供应商零售价
                                                @RequestParam(value = "provider_GHPrice", required = false) Double providerGHPrice,//供应商供货价
                                                @RequestParam(value = "enableOverseas", defaultValue = "false") Boolean  enableOverseas,//是否海外
                                                @RequestParam(value = "taxRate", required = false) Double taxRate, //税率
                                                @RequestParam(value = "enableLimitedArea", defaultValue = "false") Boolean enableLimitedArea, //是否限定区域
                                                @RequestParam(value = "salesVolume") Integer salesVolume, //暂用销量字段
                                                WebsiteAdministrator admin)
    {
        if (resultProduct.hasFieldErrors())
        {
            for (FieldError fe : resultProduct.getFieldErrors())
            {
                String  field   = fe.getField();
                if (field.equals("productCode")  || field.equals("name") || field.equals("soldUnit"))
                {
                    return new SimpleJsonResponse<>(false, fe.getDefaultMessage());
                }
            }
        }
        
        //判断服务费
        if(serviceChargeRatio != null && (serviceChargeRatio >= 100 || serviceChargeRatio < 0))
        {
            return new SimpleJsonResponse<>(false, "扣点比例不能大于100!");
        }
        
        //判断税率
        if(taxRate != null && (taxRate >= 100 || taxRate < 0)){
            return new SimpleJsonResponse<>(false,"请输入正确的税率！");
        }
        
        product.setProductCode(product.getProductCode().trim().toUpperCase());
        
        ProductCate leafCate = null;//叶子产品分类
        for (int i = cates.length - 1; i >= 0; --i) {
            if (cates[i] == null) {
                continue;
            }
            if (cates[i].isLeaf()) {
                leafCate = cates[i];
                break;//已经找到了叶子
            }
        }
        if (leafCate == null) {
            return new SimpleJsonResponse<>(false, "请选择分类，您选择的分类不是最终分类");
        }    
        
        product.setCate(leafCate);
        
        if(onSellImmediate) {
            product.setStatus(ProductStatus.selling);
        } else {
            product.setStatus(ProductStatus.basic_editing);
        }
        
        if(preferShipmentCompanyId != null && preferShipmentCompanyId > 0) {
            ShipmentCompany company = shipmentCompanyService.findOne(preferShipmentCompanyId);
            if(company != null) {
                product.setPreferShipmentCompany(company);
            }
        }
        List<ProductImage> productImageCollection = new ArrayList<>();
        for (int imageID : headerImageIDs) {
            if (imageID > 0) {
                ProductImage image = productImageService.findOne(imageID);
                productImageCollection.add(image);
            }
        }
        if (productImageCollection.isEmpty()) {
            return new SimpleJsonResponse<>(false, "请至少上传一张产品图片");
        }
        
        ProductDescription productDetail = new ProductDescription(0, description);
        
        //供应商
        if(providerId == null || providerId < 1) {
            return new SimpleJsonResponse<>(false, "请选择具体所属供应商！");
        }
        //判断供应商市场价和供应商零售价 
        if(providerMarketPrice == null || providerMarketPrice <= 0){
            return new SimpleJsonResponse<>(false, "请输入供应商市场价");
        }
        if(providerRetailPrice == null || providerRetailPrice <= 0){
            return new SimpleJsonResponse<>(false, "请输入供应商零售价");
        }
        
        product.setProvider_marketPrice(providerMarketPrice);
        product.setProvider_retailPrice(providerRetailPrice);
        if(providerGHPrice == null){
        	product.setProvider_GHPrice(0);
        }else {
        	product.setProvider_GHPrice(providerGHPrice);
		}
        
        product.setOwnerId(providerId); //存储供应商ID 
        product.setOwnerType(OwnerType.provider);

        StorageUnit storageUnit = new StorageUnit();
        storageUnit.setProperty1("实物");
        storageUnit.setEffectBarType(StorageUnitEffectBarType.bar_code);
        storageUnit.setStatus(StorageUnitStatus.selling);
        List<StorageUnit> storageUnitCollection = new ArrayList<>();
        storageUnitCollection.add(storageUnit);
        //保存销量
        product.setSalesVolume(salesVolume);
        //保存服务费
        product.setServiceChargeRatio(serviceChargeRatio); //服务费
        product.setEnableOverseas(enableOverseas); //是否海外
        if(taxRate != null){
            product.setTaxRate(taxRate); //税率
        }else{
            product.setTaxRate(0.00); 
        }
        product.setEnableLimitedArea(enableLimitedArea); //是否限定区域
        productService.saveSelfProduct(admin, product, productDetail, productImageCollection, storageUnitCollection);
        
        return new SimpleJsonResponse(true, null);
    }
    
    @RequiresPermissions("product:edit")
    @RequestMapping(value = "/admin/product/edit.php", method = RequestMethod.GET)
    public String edit(@RequestParam("id") int productId, WebsiteAdministrator admin, Model model) {
         
        Product product = productService.findOne(productId);
        if(product == null) {
            throw new ResourceNotFoundException();
        }
        List<ProductCate> parentCates = null;
        if(product.getCate() != null) {
            parentCates = productCateService.parents(product.getCate());
            Collections.reverse(parentCates);
        }
        
        ProductDescription productDescription = productDescriptionService.findOne(product.getId());   //产品详情
        Map<String, String> propertyValueMap = productPropertyValueService.findAsMap(product);          //产品属性
        
        List<Brand> brands = brandService.findAll();
        
        model.addAttribute("brands", brands);
        model.addAttribute("productDescription", productDescription);
        model.addAttribute("propertyValueMap", propertyValueMap);
        model.addAttribute("shipmentCompanys", shipmentCompanyService.all());
        model.addAttribute("images", productService.findProductImages(product));
        model.addAttribute("description", productService.getDescription(product));
        model.addAttribute("productCateService", productCateService);
        model.addAttribute("product", product);
        model.addAttribute("parentCates", parentCates);
        model.addAttribute("admin", admin);
        
        //查询供应商信息
        model.addAttribute("providers", websiteAdministratorService.find(new WebsiteAdministratorCriteria().withProvider(true).withEnabled(true), null));
        
        return "admin/product/form";
    }
    
    @RequiresPermissions("product:edit")
    @RequestMapping(value = "/admin/product/edit.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse edit(@Valid Product product, BindingResult result,
                                                @RequestParam("brand") Brand brand,
                                                @RequestParam("cates[]") ProductCate[] cates,
                                                @RequestParam("header_image[]") int[] headerImageIDs,
                                                @RequestParam(value = "onSellImmediate", defaultValue = "false") boolean onSellImmediate,
                                                @RequestParam(value = "preferShipmentCompanyId", required = false) Integer preferShipmentCompanyId,
                                                @RequestParam(value = "description", required = false) String description,
                                                @RequestParam(value = "propertyKey[]", required = false) String[] propertyKeys, //属性key
                                                @RequestParam(value = "propertyValue[]", required = false) String[] propertyValues, //属性value
                                                @RequestParam(value= "serviceChargeRatio", required = false) Double serviceChargeRatio,   //服务费
//                                              @RequestParam(value = "provider", defaultValue = "false") boolean provider,//是否为供应商产品
                                                @RequestParam(value = "providerId", required = false) Integer providerId,//所属供应商
                                                @RequestParam(value = "providerMarketPrice", required = false) Double providerMarketPrice,//供应商市场价
                                                @RequestParam(value = "providerRetailPrice", required = false) Double providerRetailPrice,//供应商零售价
                                                @RequestParam(value = "enableOverseas", defaultValue = "false") boolean enableOverseas, //是否海外
                                                @RequestParam(value = "taxRate" , required = false) Double taxRate, //税率
                                                @RequestParam(value = "enableLimitedArea", defaultValue = "false") Boolean enableLimitedArea, //是否限定区域
                                                WebsiteAdministrator admin)
    {
        if (result.hasFieldErrors()) {
            for (FieldError fe : result.getFieldErrors()) {
                String field = fe.getField();
                if (field.equals("productCode")) {
                    return new SimpleJsonResponse<>(false, fe.getDefaultMessage());
                }
            }
        }
        
        //判断服务费
        if(serviceChargeRatio != null && (serviceChargeRatio >= 100 || serviceChargeRatio < 0))
        {
            return new SimpleJsonResponse<>(false, "扣点比例不能大于100!");
        }
        
        //判断税率
        if(taxRate != null && (taxRate >= 100 || taxRate < 0)){
            return new SimpleJsonResponse<>(false, "请输入正确的税率！");
        }

        //检查该产品是否属于该公司
        Product productOld = productService.findOne(product.getId());
        if(admin.isProvider()) {
            if (!productOld.belongsTo(admin)) {
                return new SimpleJsonResponse<>(false, "无法编辑不属于自己的商品信息");
            }
        }

        //品牌
        brand = brandService.findOne(brand.getName());
        if (brand == null) {
            brand = new Brand();
            brand.setName(brand.getName());
            brand.setDeleted(false);
            brand = brandService.save(brand);
        }
        productOld.setProductCode(product.getProductCode().trim());
        if (!(productOld.getProductCode().trim()).equals(product.getProductCode().trim())) {
            //如果修改了产品型号,则验证该型号是否已经存在
            if(admin.isProvider()) {
                if (productService.exists(OwnerType.provider, admin.getId(), brand, product.getProductCode())) {
                    return new SimpleJsonResponse(false, "该型号产品已存在");
                }
            } else {
                if (productService.exists(admin, brand, product.getProductCode())) {
                    return new SimpleJsonResponse(false, "该型号产品已存在");
                }
            }
        }

        ProductCate leafCate = null;//叶子产品分类
        for (int i = cates.length - 1; i >= 0; --i) {
            if (cates[i] == null) {
                continue;
            }
            if (cates[i].isLeaf()) {
                leafCate = cates[i];
                break;//已经找到了叶子
            }
        }
        if (leafCate == null) {
            return new SimpleJsonResponse<>(false, "请选择分类，您选择的分类不是最终分类");
        }
        List<ProductImage> productImageCollection = new ArrayList<>();
        short order = 0;
        for (int imageID : headerImageIDs) {
            if (imageID > 0) {
                ProductImage image = productImageService.findOne(imageID);
                image.setOrder(order++);
                image.setProductId(product.getId());
                productImageCollection.add(image);
            }
        }
        if (productImageCollection.isEmpty()) {
            return new SimpleJsonResponse<>(false, "请选择分类，请至少上传一张产品图片");
        }
        
        if(product.getMarketPrice() <= 0 || product.getMarketPrice() > 9999999){
            return new SimpleJsonResponse<>(false, "请填写正确的市场价");
        }

        if(product.getRetailPrice() <=0 || product.getRetailPrice() > 9999999){
            return new SimpleJsonResponse<>(false, "请填写正确的零售价");
        }
        
        //供应商
        if(providerId == null || providerId < 1) {
            return new SimpleJsonResponse<>(false, "请选择具体所属供应商！");
        }
        //判断供应商市场价和供应商零售价
        if(providerMarketPrice == null || providerMarketPrice <= 0){
            return new SimpleJsonResponse<>(false, "请输入供应商市场价");
        }
        if(providerRetailPrice == null || providerRetailPrice <= 0){
            return new SimpleJsonResponse<>(false, "请输入供应商零售价");
        }
        
        productOld.setProvider_marketPrice(providerMarketPrice);
        productOld.setProvider_retailPrice(providerRetailPrice);
        productOld.setOwnerId(providerId); //存储供应商ID
        productOld.setOwnerType(OwnerType.provider);
        
        if(preferShipmentCompanyId != null && preferShipmentCompanyId > 0) {
            ShipmentCompany company = shipmentCompanyService.findOne(preferShipmentCompanyId);
            if(company != null) {
                product.setPreferShipmentCompany(company);
            }
        }
        //产品属性
        List<ProductPropertyValue> propertyValueList = new ArrayList();
        if (propertyKeys != null && propertyValues != null) {
            if (propertyKeys.length != propertyValues.length) {
                return new SimpleJsonResponse<>(false, "请填写正确的属性值");
            }
            for (int i = 0; i < propertyKeys.length; ++i) {
                ProductPropertyValue value = new ProductPropertyValue();
                value.setPropertyKey(propertyKeys[i]);
                value.setValue(propertyValues[i]);
                propertyValueList.add(value);
            }

        }
        ProductDescription productDetail = new ProductDescription(product.getId(), description);
        //快速编辑时 产品名称和销售名称一致
        productOld.setCate(leafCate);
        if (onSellImmediate) {
            productOld.setStatus(ProductStatus.selling);
        } else {
            productOld.setStatus(ProductStatus.basic_editing);
        }
        productOld.setProvider_GHPrice(product.getProvider_GHPrice());
        productOld.setCate(leafCate);  // 分类
        productOld.setName(product.getName().trim());   // 产品名
        productOld.setFeatures(product.getFeatures());  // 产品卖点
        productOld.setBrand(brand);     // 品牌
        productOld.setProductCode(product.getProductCode().trim());    // 型号
        productOld.setMarketPrice(product.getMarketPrice());    // 市场价
        productOld.setProductPlace(product.getProductPlace());  // 产品生产产地
        productOld.setRetailPrice(product.getRetailPrice());    //经销价
        productOld.setSalesVolume(product.getSalesVolume());    //销量
        if (product.getPreferShipmentCompany() != null) {
            productOld.setPreferShipmentCompany(product.getPreferShipmentCompany());
        }
        //服务费
        productOld.setServiceChargeRatio(serviceChargeRatio);
        productOld.setEnableOverseas(enableOverseas); //是否海外
        if(taxRate != null){
            productOld.setTaxRate(taxRate);  //税率
        }else{
            productOld.setTaxRate(0.00);  //税率
        }
        productOld.setEnableLimitedArea(enableLimitedArea); //是否限定区域
        productService.saveSelfProduct(admin, productOld, productDetail, productImageCollection, null);
        return new SimpleJsonResponse<>(true, null);
        
    }
    
    @RequiresPermissions("product:view")
    @RequestMapping(value = "/admin/product.php", method = RequestMethod.GET)
    public String view(@RequestParam("id") int productId, WebsiteAdministrator admin, Model model)
    {
        Product product = productService.findOne(productId);
        if (product == null)
        {
            throw new ResourceNotFoundException();
        }
        List<ProductCate> parentCates = null;
        if(product.getCate() != null) {
            parentCates = productCateService.parents(product.getCate());
            Collections.reverse(parentCates);
        }
                
        // 产品属性
        Map<String, String> propertyValueMap = productPropertyValueService.findAsMap(product);
        
        //根据产品信息 查询所属供应商
        if(product.getOwnerType().equals(OwnerType.provider)){ //如果产品为供应商  则进入查询
            model.addAttribute("provider", websiteAdministratorService.findOne(product.getOwnerId()));
        }
        
        //当前产品所拥有的标签
        model.addAttribute("propertyValueMap", propertyValueMap);
        model.addAttribute("images", productService.findProductImages(product));
        model.addAttribute("description", productService.getDescription(product));
        model.addAttribute("storageUnits", productService.findStorageUnits(product));
        model.addAttribute("product", product);
        model.addAttribute("admin", admin);
        model.addAttribute("parentCates", parentCates);
        
        return "admin/product/view";
    }
    
    @RequiresPermissions("product:offline")
    @RequestMapping(value = "/admin/product/shelvesOff.php", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public @ResponseBody SimpleJsonResponse shelvesOff(@RequestParam("ids[]") int[] ids, WebsiteAdministrator admin) {
        
        if (ids.length < 1) {
            return new SimpleJsonResponse(false, "请选择您要下架的产品");
        }
//        if(admin.isProvider()) {
//            return new SimpleJsonResponse(false, "抱歉，您无权限执行此操作！");
//        }
        
        boolean skipError = true;
        if(ids.length == 1) {
            skipError = false;
        }
        
        for (int id : ids) {
            Product product = productService.findOne(id);
            if (product == null) {
                if(skipError) {
                    continue;
                } else {
                    return new SimpleJsonResponse(false, "您要下架的产品[ID=" + id + "]不存在"); 
                }
            }
            if (!product.getStatus().equals(ProductStatus.selling)) {
                if(skipError) {
                    continue;
                } else {
                    return new SimpleJsonResponse(false, "您要下架的产品【" + product.getName() + "】还未上架");
                }
            }
            productService.setStatus(id, ProductStatus.basic_editing);
        }
        return new SimpleJsonResponse(true, null);
    }

    @RequiresPermissions("product:online")
    @RequestMapping(value = "/admin/product/shelvesOn.php", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public @ResponseBody SimpleJsonResponse shelvesOn(@RequestParam("ids[]") int[] ids, WebsiteAdministrator admin) {
        if (ids.length < 1) {
            return new SimpleJsonResponse(false, "请选择您要上架的产品");
        }
        
//        if(admin.isProvider()) {
//            return new SimpleJsonResponse(false, "抱歉，您无权限执行此操作！");
//        }
        
        boolean skipError = true;
        if(ids.length == 1) {
            skipError = false;
        }
        
        for (int id : ids) {
            Product product = productService.findOne(id);
            if (product == null) {
                if(skipError) {
                    continue;
                } else {
                    return new SimpleJsonResponse(false, "您要上架的产品[ID=" + id + "]不存在"); 
                }
            }
            if (product.getStatus().equals(ProductStatus.selling)) {
                if(skipError) {
                    continue;
                } else {
                    return new SimpleJsonResponse(false, "您要上架的产品【" + product.getName() + "】并未下架,无需上架");
                }
            }
            productService.setStatus(id, ProductStatus.selling);
        }
        return new SimpleJsonResponse(true, null);
    }
    
    @RequiresPermissions("product:delete")
    @RequestMapping(value = "/admin/product/delete.php", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public @ResponseBody SimpleJsonResponse delete(@RequestParam("ids[]") int[] ids, WebsiteAdministrator admin) {
        
        if (ids.length < 1) {
            return new SimpleJsonResponse(false, "请指定要删除的产品");
        }
       
        boolean skipError = true;
        if(ids.length == 1) {
            skipError = false;
        }
        
        for (int id : ids) {
            Product product = productService.findOne(id);
            if (product == null) {
                if(skipError) {
                    continue;
                } else {
                    return new SimpleJsonResponse(false, "您删除的产品不存在"); 
                }
            }
            if(admin.isProvider()) {
                if(skipError) {
                    continue;
                } else {
                    if (OwnerType.provider != product.getOwnerType() || !product.belongsTo(admin)) {
                        return new SimpleJsonResponse(false, "您无法操作不属于您的产品,产品名称为:【" + product.getName() + "】");
                    } 
                }
            } else {
                if(OwnerType.enterprise != product.getOwnerType() && OwnerType.provider != OwnerType.provider) {
                     if(skipError) {
                        continue;
                    } else {
                        return new SimpleJsonResponse(false, "您无法操作不属于您的产品,产品名称为:【" + product.getName() + "】");
                    }
                }
            }
            if(product.getStatus() == ProductStatus.selling) {
                if(skipError) {
                    continue;
                } else {
                    return new SimpleJsonResponse(false, "【" + product.getName() + "】 是在售产品，无法被删除！");
                }
            }
            if (!productService.isDeleteable(product)) {
                if(skipError) {
                    continue;
                } else {
                    return new SimpleJsonResponse(false, "【" + product.getName() + "】 存在订单，无法被删除！");
                }
            } 
            productService.markAsDelete(product.getId());
//            productService.delete(product);
        }
        return new SimpleJsonResponse(true, null);
    }
    
    
    @RequestMapping(value = "/admin/product/ajax/brand/add.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public  @ResponseBody SimpleJsonResponse ajaxAddBand(@RequestParam("brandName") String brandName,
                                                         @RequestParam(value =  "englishName", required = false) String englishName,
                                                         @RequestParam(value =  "description", required = false) String description,
                                                         @RequestParam(value =  "thumbnail", required = false) String thumbnail)
    {
        brandName = brandName.trim();
        if (brandName == null || brandName.length() < 1) {
            return new SimpleJsonResponse(false, "请输入产品品牌名称");
        }
        
        if(brandService.exists(brandName)) {
            return new SimpleJsonResponse(false, "品牌名称已存在，请重新输入");
        }
        
        Brand brand = new Brand();
        brand.setName(brandName);
        brand.setSelf(true);
        brand = brandService.save(brand);
        
        return new SimpleJsonResponse(true, brand.getId());
    }
    
    @RequiresPermissions("product:edit")
    @RequestMapping(value = "/admin/product/systemProductEdit.php", method = RequestMethod.POST) 
    public @ResponseBody SimpleJsonResponse systemProductEdit(@RequestParam(value = "id") int id,
                                                              @RequestParam(value = "name", required = false) String name,
                                                              @RequestParam(value = "features", required = false) String features,
                                                              @RequestParam(value = "price", required = false) Double price,
                                                              @RequestParam(value = "serviceChargeRatio", required = false) double serviceChargeRatio,
                                                              @RequestParam(value = "salesVolume", required = false) Integer salesVolume,
                                                              WebsiteAdministrator admin) {
        Product product = productService.findOne(id);
        if(product == null) {
            return new SimpleJsonResponse(false, "温馨提示，请勿非法操作！");
        }
        
        if (price == null || price < 0.01 || price > 99999999) {
            return new SimpleJsonResponse<>(false, "温馨提示，请填写正确的售价！");
        }
        
        product.setRetailPrice(price);
        
        if(name != null && !name.trim().isEmpty()) {
            product.setName(name);
        } 
        //判断服务费
        if(serviceChargeRatio >= 100 || serviceChargeRatio < 0){
            return new SimpleJsonResponse<>(false, "请输入正确的扣点!");
        }
        product.setServiceChargeRatio(serviceChargeRatio);
        product.setSalesVolume(salesVolume);
        productService.save(product);
        
        return new SimpleJsonResponse(true , null);
    }
    
    //新增产品的时候ajx搜素供应商
    @RequestMapping(value = "/admin/product/ajax/loadProviders.php", method = RequestMethod.GET)
    public @ResponseBody SimpleJsonResponse ajaxloadProviders(@RequestParam(value =  "kw", required = false) String kw,
                                    WebsiteAdministrator admin)
    {
        //查询供应商信息
        Page<WebsiteAdministrator> websiteAdministrator = websiteAdministratorService.find(new WebsiteAdministratorCriteria().withKeyword(kw).withProvider(true).withEnabled(true), null);
        if(websiteAdministrator.hasContent()){
            return new SimpleJsonResponse(true, websiteAdministrator.getContent());
        }
        return new SimpleJsonResponse(false, "");
    }
    
}
