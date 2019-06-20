/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.controller;

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
import com.lmf.website.entity.UserDefinedCate;
import com.lmf.website.service.UserDefinedCateService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
 * @author Mine
 */
@Controller
public class UserDefinedCateController {
    
    @Autowired
    private UserDefinedCateService userDefinedCateService;
    
    @Autowired
    private InternalAttachmentService internalAttachmentService;
    
    @Autowired
    private ProductCateService productCateService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private BrandService brandService;
    
    @Autowired
    private SystemConfig systemConfig;
    
    @RequiresPermissions("zdy_brand:view")
    @RequestMapping(value = "/admin/userDefinedCates.php", method = RequestMethod.GET)
    public String list( @RequestParam(value = "parentId", required = false) Integer parentId,
                       Model model)
    {
        Page<UserDefinedCate> rootCates = userDefinedCateService.find(null, null, null);
        model.addAttribute("rootCates", rootCates);
        if(parentId != null && parentId > 0){
            List<UserDefinedCate> cates = userDefinedCateService.findChilds(parentId, null);
            model.addAttribute("cates", cates);
            model.addAttribute("parentId", parentId);
        }else{
            List<UserDefinedCate> cates = userDefinedCateService.rootCates(null);
            model.addAttribute("cates", cates);
        }
        
        
        return "admin/user_defined_cate/list";
    }
    
    @RequiresPermissions("zdy_brand:create")
    @RequestMapping(value = "/admin/userDefinedCate/add.php", method = RequestMethod.GET)
    public String add(@RequestParam(value = "parentId", required = false) Integer parentId,
                      Model model)
    {
        if(parentId != null && parentId > 0){
            UserDefinedCate parentCate = userDefinedCateService.findOne(parentId);
            int sortOrder = userDefinedCateService.findMaxSortOrderByParentId(parentId);
            model.addAttribute("parentCate", parentCate);
            model.addAttribute("parentId", parentCate.getId());
            model.addAttribute("sortOrder", sortOrder <= 0 ? 1 : (sortOrder + 1));
        }else{
            int sortOrder = userDefinedCateService.findMaxSortOrderByParentId(parentId);
            model.addAttribute("sortOrder", sortOrder <= 0 ? 1 : (sortOrder + 1));
        }
        model.addAttribute("userDefinedCateService", userDefinedCateService);
        return "admin/user_defined_cate/form";
    }
    
    @RequiresPermissions("zdy_brand:create")
    @RequestMapping(value = "/admin/userDefinedCate/add.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse add(@RequestParam(value = "name") String name,
                                                @RequestParam(value = "parentId", required = false) Integer parentId,
                                                Model model)
    {
        if(name == null || name.trim().isEmpty()){
            return new SimpleJsonResponse(false, "请填写分类名称");
        }
        UserDefinedCate tag = new UserDefinedCate();
        tag.setName(name);
        tag.setParentId(parentId);
        tag.setEnable(true);
        userDefinedCateService.save(tag);
        
        return new SimpleJsonResponse(true, "数据保存成功");
    }
    
    @RequiresPermissions("zdy_brand:edit")
    @RequestMapping(value = "/admin/userDefinedCate/edit.php", method = RequestMethod.GET)
    public String edit(@RequestParam(value = "id") int id,
                       Model model) {
        UserDefinedCate cate = userDefinedCateService.findOne(id);
        if(cate.getParentId() != null && cate.getParentId() > 0){
            UserDefinedCate parentCate = userDefinedCateService.findOne(cate.getParentId());
            model.addAttribute("parentCate", parentCate);
            model.addAttribute("parentId", parentCate.getId());
        }
        model.addAttribute("cate", cate);
        model.addAttribute("sortOrder", cate.getSortOrder());
        return "admin/user_defined_cate/form";
    }
    
    @RequiresPermissions("zdy_brand:edit")
    @RequestMapping(value = "/admin/userDefinedCate/edit.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse edit(@RequestParam(value = "id") int id,
                                                @RequestParam(value = "name") String name,
                                                @RequestParam(value = "parentId", required = false) Integer parentId,
                                                Model model)
    {
        UserDefinedCate cate = userDefinedCateService.findOne(id);
        if(name == null || name.trim().isEmpty()){
            return new SimpleJsonResponse(false, "请填写分类名称");
        }
        cate.setName(name);
        cate.setParentId(parentId);
        cate.setEnable(true);
        userDefinedCateService.save(cate);
        return new SimpleJsonResponse(true, "数据保存成功");
    }
    
    @RequestMapping(value = "/admin/userDefinedCate/setEnable.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse setEnable(@RequestParam("id") int id, Model model)
    {
        UserDefinedCate cate = userDefinedCateService.findOne(id);
        if(cate == null) {
            return new SimpleJsonResponse(false, "分类不存在，请核实数据！");
        }
        userDefinedCateService.setEnable(cate, !cate.isEnable());
        return new SimpleJsonResponse(true, "操作成功");
    }
    
    @RequiresPermissions("zdy_brand:delete")
    @RequestMapping(value = "/admin/userDefinedCate/delete.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse delete(@RequestParam(value = "id") int id, Model model)
    {
        UserDefinedCate tag = userDefinedCateService.findOne(id);
        List<UserDefinedCate> tagList = userDefinedCateService.findChilds(id, null);
        if(tagList != null && !tagList.isEmpty()){
            return new SimpleJsonResponse(false, "此分类存在下级子分类 , 无法删除.");
        }
        userDefinedCateService.delete(tag);
        return new SimpleJsonResponse(true, "数据删除成功");
    }
    
    @RequestMapping(value = "/admin/userDefinedCate/associate.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse associate(@RequestParam(value = "id") int id,
                                                      @RequestParam(value = "pid[]") Integer[] pids,
                                                      Model model)
    {
        if(pids == null || pids.length <= 0){
            return new SimpleJsonResponse(false, "您没有选择任何产品数据");
        }
        UserDefinedCate tag = userDefinedCateService.findOne(id);
        Map<Integer, Product> bpMap = productService.findAsMap(Arrays.asList(pids));
        List<Product> bpList = new ArrayList<>();
        for(Map.Entry<Integer, Product> item : bpMap.entrySet()){
            Product bp = item.getValue();
            bpList.add(bp);
        }
        userDefinedCateService.save(tag, bpList);
        return new SimpleJsonResponse(true, "数据保存成功");
    }
    
    @RequestMapping(value = "/admin/userDefinedCate/choose.php", method = RequestMethod.GET)
    public String choose(@RequestParam(value = "id") int id,
                        @RequestParam(value = "kw", required = false) String keyword,
                        @RequestParam(value = "b", required = false) Brand brand,
                        @RequestParam(value = "c", required = false) ProductCate cate,
                        @RequestParam(value = "minPrice", required = false) Double minPrice,
                        @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                        @RequestParam(value = "enableOverseas", required = false)Boolean enableOverseas,
                        @RequestParam(value = "type", required = false) OwnerType type,
                        @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager,
                        Model model) throws UnsupportedEncodingException 
     {
        UserDefinedCate userDefinedCate = userDefinedCateService.findOne(id);
        
        Page<Product> products = productService.find(new ProductCriteria().withKeyword(keyword)
                .withBrand(brand).withCate(cate).withPrice(minPrice, maxPrice).withDeleted(Boolean.FALSE)
                .withStatus(ProductStatus.selling).withEnableOverseas(enableOverseas).withOwnerType(type), pager);

        StringBuilder link = new StringBuilder("/jdvop/admin/userDefinedCate/ajaxChoose.php?page=[:page]&id=").append(id);
        if (keyword != null && !keyword.isEmpty()) {
            link.append("&kw=").append(URLEncoder.encode(keyword, "UTF-8"));
        }
        if (brand != null) {
            link.append("&b=").append(brand.getId());
        }
        if (cate != null) {
            link.append("&c=").append(cate.getId());
        }
        if (minPrice != null) {
            link.append("&minPrice=").append(minPrice);
        }
        if (maxPrice != null) {
            link.append("&maxPrice=").append(maxPrice);
        }
        if (enableOverseas != null) {
            link.append("&enableOverseas=").append(enableOverseas);
        }
        if (type != null) {
            link.append("&type=").append(type.name());
        }

        List<Integer> productIds = userDefinedCateService.findProductIds(userDefinedCate);
        if(productIds != null && !productIds.isEmpty()){
            model.addAttribute("productIds", productIds);
        }
        model.addAttribute("products", products);
        model.addAttribute("brands", brandService.findAll());
        model.addAttribute("cate", cate);
        model.addAttribute("link", link.toString());
        model.addAttribute("productCateService", productCateService);
        model.addAttribute("userDefinedCate", userDefinedCate);
        model.addAttribute("userDefinedCateId", id);
        model.addAttribute("ownerTypes", OwnerType.values());
        return "admin/user_defined_cate/products";
    }
    
    @RequestMapping(value = "/admin/userDefinedCate/ajaxChoose.php", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> ajaxChoose(@RequestParam(value = "id") int id,
                                                        @RequestParam(value = "kw", required = false) String keyword,
                                                        @RequestParam(value = "b", required = false) Brand brand,
                                                        @RequestParam(value = "c", required = false) ProductCate cate,
                                                        @RequestParam(value = "minPrice", required = false) Double minPrice,
                                                        @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                                                        @RequestParam(value = "enableOverseas", required = false)Boolean enableOverseas,
                                                        @RequestParam(value = "type", required = false) OwnerType type,
                                                        @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager,
                                                        Model model) throws UnsupportedEncodingException 
     {
        Page<Product> products = productService.find(new ProductCriteria().withKeyword(keyword).withBrand(brand)
                .withCate(cate).withPrice(minPrice, maxPrice).withDeleted(Boolean.FALSE)
                .withStatus(ProductStatus.selling).withEnableOverseas(enableOverseas).withOwnerType(type), pager);
        
        StringBuilder link = new StringBuilder("/jdvop/admin/userDefinedCate/ajaxChoose.php?page=[:page]&id=").append(id);
        if (keyword != null && !keyword.isEmpty()) {
            link.append("&kw=").append(URLEncoder.encode(keyword, "UTF-8"));
        }
        if (brand != null) {
            link.append("&b=").append(brand.getId());
        }
        if (cate != null) {
            link.append("&c=").append(cate.getId());
        }
        if (minPrice != null) {
            link.append("&minPrice=").append(minPrice);
        }
        if (maxPrice != null) {
            link.append("&maxPrice=").append(maxPrice);
        }
        if (enableOverseas != null) {
            link.append("&enableOverseas=").append(enableOverseas);
        }
        if (type != null) {
            link.append("&type=").append(type.name());
        }
        
        Map<String, Object> dataMap = new HashMap<>();
        if(products.hasContent()){
            StringBuilder contentHtml = new StringBuilder();
            for(Product product : products.getContent()){
                contentHtml.append("<li class='js-choose-entry' rel='").append(product.getId()).append("'>");
                contentHtml.append("<p class='text'><label>产品型号").append(product.getProductCode()).append("</label></p>");
                if(product.getOwnerType() == OwnerType.jingdong) {
                    contentHtml.append("<p class='img'><a href='#'><img src=\"/jdvop/images/jd-ico.png\" class=\"jd-ico\"/><img src='").append(product.getThumbnailImage()).append("'/></a></p>");
                    contentHtml.append("<p class='text blue'><a href='#'>").append(product.getName()).append("</a></p>");
                    contentHtml.append("<p class='text'>所属品牌：").append(product.getBrand().getName()).append("</p>");
                } else if(product.getOwnerType() == OwnerType.system){ 
                    contentHtml.append("<p class='img'><a href='#'><img src='").append(product.getThumbnailImage()).append("'/></a></p>");
                    contentHtml.append("<p class='text blue'><a href='#'>").append(product.getName()).append("</a></p>");
                    contentHtml.append("<p class='text'>所属品牌：").append(product.getBrand().getName()).append("</p>");
                }  else {
                    contentHtml.append("<p class='img'><a href='#'><img src='").append(systemConfig.getImageHost()).append("/thumbnail").append(product.getThumbnailImage()).append("'/></a></p>");
                    contentHtml.append("<p class='text blue'><a href='#'>").append(product.getName()).append("</a></p>");
                    contentHtml.append("<p class='text'><i class='fr'>销量：").append(product.getProductStorage().getSoldedNum())
                        .append("</i><i>库存：").append(product.getProductStorage().getUseableNum()).append("</i> </p>");
                }
                contentHtml.append("<p class='text ffa orange'><i class=\"color999\"> 经销价：¥</i>").append(product.getRetailPrice()).append("</p>");
                if(product.getOwnerType() == OwnerType.system){
                    contentHtml.append("<label class=\"ico_xt\">").append("</label>");
                }else if(product.getOwnerType() == OwnerType.provider){
                    contentHtml.append("<label class=\"ico_gys\">").append("</label>");
                }else if(product.getOwnerType() == OwnerType.enterprise){
                    contentHtml.append("<label class=\"ico_own\">").append("</label>");
                }
                contentHtml.append("<span class='ico_selected' style='display: none;'></span></li>");
            }
            dataMap.put("contentHtml", contentHtml);
        }

        String  pageHtml    = PagerUtil.createPagenation(link.toString(), products.getPagerSpec(), 4, "_self");
        dataMap.put("pageHtml", pageHtml);

        return dataMap;
    }
    
    @RequestMapping(value = "/admin/userDefinedCate/image/uploead.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public  @ResponseBody SimpleJsonResponse uploeadImage(@RequestParam(value = "icon_upload") MultipartFile file) throws IOException
    {
        byte[]  bytes       = file.getBytes();
        SimpleImageInfo sif;
        try {
            sif = new SimpleImageInfo(bytes);
        } catch (IOException exp) {
            return new SimpleJsonResponse(false, "您上传的文件不是合法的图片格式"+exp);
        }
        String  src    = internalAttachmentService.save(new SerializableMultipartFile(file));
        return  new SimpleJsonResponse(true, src);
    }
}
