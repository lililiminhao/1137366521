/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.product.controller;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SerializableMultipartFile;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.exceptions.ResourceNotFoundException;
import com.lmf.common.util.PagerSpec;
import com.lmf.common.util.SimpleImageInfo;
import com.lmf.product.entity.Brand;
import com.lmf.product.service.BrandService;
import com.lmf.product.vo.BrandCriteria;
import com.lmf.sys.service.InternalAttachmentService;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
 * @author shenzhixiong
 */
@Controller
public class BrandController {
    
    @Autowired
    private BrandService brandService;
    
    @Autowired
    private InternalAttachmentService internalAttachmentService;
    
    @RequiresPermissions("brand:view")
    @RequestMapping(value = "/admin/brands.php", method = RequestMethod.GET)
    public String  list(@RequestParam(value = "kw", required = false) String kw, 
                        @RequestParam(value = "self", defaultValue = "false") Boolean self, 
                        @PagerSpecDefaults(pageSize = 20, sort = "recommend.desc,time.desc") PagerSpec pager, 
                        Model model) throws UnsupportedEncodingException{
        Page<Brand> brands;
        if(self) {
            brands = brandService.findSelf(new BrandCriteria().withKeyword(kw), pager);
        } else {
            brands = brandService.find(new BrandCriteria().withKeyword(kw), pager);
        }
        StringBuilder link = new StringBuilder("/jdvop/admin/brands.php?page=[:page]");
        if (kw != null && !kw.isEmpty())
        {
            link.append("&kw=").append(URLEncoder.encode(kw, "UTF-8"));
        }
        link.append("&self=").append(self);
        model.addAttribute("brands", brands);
        model.addAttribute("link", link.toString());
        
        model.addAttribute("brandService", brandService);
        model.addAttribute("self", self);
        return "admin/brand/list";
    }
    
    @RequiresPermissions("brand:create")
    @RequestMapping(value = "/admin/brand/add.php", method = RequestMethod.GET)
    public String add(){
        return "admin/brand/form";
    }
    
    @RequiresPermissions("brand:create")
    @RequestMapping(value = "/admin/brand/add.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse add(@RequestParam("name") String brandName,
                                                @RequestParam(value =  "englishName", required = false) String englishName,
                                                @RequestParam(value =  "description", required = false) String description,
                                                @RequestParam(value =  "thumbnail", required = false) String thumbnail){
        if(brandName == null || brandName.isEmpty()) {
            return new SimpleJsonResponse(false, "品牌名称不能为空");
        }
         
        if(brandService.exists(brandName)) {
            return new SimpleJsonResponse(false, "品牌名称已存在，请重新输入！");
        }
        
        Brand brand = new Brand();
        
        if (thumbnail != null && !thumbnail.isEmpty() && thumbnail.trim().length() > 0) {
             brand.setThumbnail(thumbnail);
        }
        brand.setName(brandName);
        brand.setEnglishName(englishName);
        brand.setDescription(description);
        brand.setDeleted(false);
        brand.setSelf(true);
        brandService.save(brand);
        
        return new SimpleJsonResponse(true, null);
    }
    
    @RequiresPermissions("brand:edit")
    @RequestMapping(value = "/admin/brand/edit.php",  method = RequestMethod.GET)
    public String edit(@RequestParam("id") int id, Model model){
        Brand brand = brandService.findOne(id);
        if(brand == null ){
            throw new ResourceNotFoundException();
        }
        model.addAttribute("brand", brand);
        return "admin/brand/form";
    }
    
    @RequiresPermissions("brand:edit")
    @RequestMapping(value = "/admin/brand/edit.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse edit(@RequestParam("id") int id, 
                                                @RequestParam("name") String brandName,
                                                @RequestParam(value =  "englishName", required = false) String englishName,
                                                @RequestParam(value =  "description", required = false) String description,
                                                @RequestParam(value =  "thumbnail", required = false) String thumbnail){
        Brand brand = brandService.findOne(id);
        if(brand == null || !brand.isSelf()){
            return new SimpleJsonResponse(false, "抱歉，您只能操作自有品牌");
        }
        if(brandName == null || brandName.isEmpty()){
            return new SimpleJsonResponse(false, "品牌名称不能为空");
        }
        if (thumbnail != null && !thumbnail.isEmpty() && thumbnail.trim().length() > 0) {
             brand.setThumbnail(thumbnail);
        }
        brand.setName(brandName);
        brand.setEnglishName(englishName);
        brand.setDescription(description);
        
        brandService.save(brand);
        return new SimpleJsonResponse(true, null);
    }
    
    @RequiresPermissions("brand:delete")
    @RequestMapping(value = "/admin/brand/delete.php", method = RequestMethod.GET)
    public @ResponseBody SimpleJsonResponse delete(@RequestParam("id") int id){
        Brand brand = brandService.findOne(id);
        if(brand == null || !brand.isSelf()){
            return new SimpleJsonResponse(false, "抱歉，您只能操作自有品牌");
        }
        
        if(!brandService.isDeleteable(id)) {
            return new SimpleJsonResponse(false, "抱歉，该品牌下面存在产品，无法删除");
        }
        
        brandService.delete(brand);
        return new SimpleJsonResponse(true, null);
    }
    
    @RequestMapping(value = "/admin/brand/image/uploead.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public  @ResponseBody SimpleJsonResponse uploeadImage(@RequestParam(value = "thumbnail_upload") MultipartFile file) throws IOException
    {
        byte[]  bytes       = file.getBytes();
        SimpleImageInfo sif;
        try {
            sif = new SimpleImageInfo(bytes);
        } catch (IOException exp) {
            return new SimpleJsonResponse(false, "您上传的文件不是合法的图片格式"+exp);
        }
        if (sif.getWidth() != 160 || sif.getHeight() != 160)
        {
            return new SimpleJsonResponse(false, "您上传的图片文件尺寸不合法，必须是 160 * 160 像素的");
        }
        String  src    = internalAttachmentService.save(new SerializableMultipartFile(file));
        return  new SimpleJsonResponse(true, src);
    }
    
}
