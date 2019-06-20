/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.controller;

import com.lmf.common.SerializableMultipartFile;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.SimpleImageInfo;
import com.lmf.market.entity.Coupon;
import com.lmf.market.service.CouponService;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.ProductCate;
import com.lmf.product.service.ProductCateService;
import com.lmf.product.service.ProductService;
import com.lmf.sys.service.InternalAttachmentService;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteCustomBlock;
import com.lmf.website.entity.WebsiteCustomBlockEntry;
import com.lmf.website.service.WebsiteCustomEnterpriseBlockService;
import com.lmf.website.theme.v2.BlockType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
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
 * @author yuanhu
 */
@Controller
public class CustomEnterpriseThemeController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private ProductCateService productCateService;
    
    @Autowired
    private InternalAttachmentService internalAttachmentService;

    @Autowired
    private WebsiteCustomEnterpriseBlockService websiteCustomBlockEnterpriseService;
    
    @Autowired
    CouponService couponService;
    
    /*
     * 自定义模板
     */
    @RequiresPermissions("enterprise_model:edit")
    @RequestMapping(value = "/admin/customEnterprise.php", method = RequestMethod.GET)
    public String wechatSetting(@RequestParam(value = "customId", required = false) Integer customId,
                                Website website, Model model, HttpSession session) 
    {
        List<WebsiteCustomBlock> blocks = websiteCustomBlockEnterpriseService.find();
        //blocks中待删除的ID集合
        List<Integer> blockIds = new ArrayList<Integer>();
        //如果block是产品类型并且内容为空，则进行删除操作
        for (WebsiteCustomBlock block : blocks) {
            if(block.getBlockType() == BlockType.products && block.getContent() == null){
                websiteCustomBlockEnterpriseService.delByBlockId(block.getBlockId());
                blockIds.add(block.getBlockId());
            }
        }
        //遍历要删除的ID，再遍历要删除的实体集合。删除List集合中的元素只能用迭代器，其他方式会报错
        for (Integer blockId : blockIds) {
            Iterator<WebsiteCustomBlock> it = blocks.iterator();
            while(it.hasNext()){
                WebsiteCustomBlock websiteCustomBlock = it.next();
                if(websiteCustomBlock.getBlockId().equals(blockId)){
                    it.remove();
                }
            }
        }
        
        model.addAttribute("blocks", blocks);
        model.addAttribute("cates", productCateService.findAll());
        return "admin/website/custom_enterprise_edit";
    }
    
    //产品搜索
    @RequestMapping(value = "/admin/customEnterprise/modifySearchBox.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse modifySearchBox(@RequestParam(value = "bloKey") String bloKey,
                                                            @RequestParam(value = "blockId", required = false) Integer blockId,
                                                            @RequestParam(value ="specialActivityId", required =false) Integer specialActivityId) 
    {
        WebsiteCustomBlock websiteCustomBlock = new WebsiteCustomBlock();
        websiteCustomBlock.setBlockType(BlockType.text);//商品搜索暂给text类型
        websiteCustomBlock.setBloKey(bloKey);
        websiteCustomBlock.setRenderSerial(1);
        if(specialActivityId != null && specialActivityId > 0){
        	websiteCustomBlock.setSpecialActivityId(specialActivityId);
        }else{
        	websiteCustomBlock.setSpecialActivityId(-1);
        }
        
        websiteCustomBlock = websiteCustomBlockEnterpriseService.create(websiteCustomBlock);
        return new SimpleJsonResponse(true, websiteCustomBlock);
    }
    
    //公告栏
    @RequestMapping(value = "/admin/customEnterprise/modifyText.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse modifyText(@RequestParam(value = "bloKey") String bloKey,
                                                       @RequestParam(value = "blockId", required = false) Integer blockId,
                                                       @RequestParam(value = "type", defaultValue = "create") String type,
                                                       @RequestParam(value = "text", required = false) String text, 
                                                       @RequestParam(value ="specialActivityId", required =false) Integer specialActivityId) 
    {
        WebsiteCustomBlock websiteCustomBlock = new WebsiteCustomBlock();
        if("edit".equals(type)) {
            websiteCustomBlock = websiteCustomBlockEnterpriseService.findOne(blockId);
            WebsiteCustomBlockEntry textEntry = new WebsiteCustomBlockEntry();
            if (text.isEmpty()) {
                return new SimpleJsonResponse<>(false, "请填写公告栏信息");
            }
            textEntry.setDescription(text);
            websiteCustomBlock.setContent(textEntry);
            websiteCustomBlockEnterpriseService.save(websiteCustomBlock);
        } else {
            websiteCustomBlock.setBlockType(BlockType.text);
            websiteCustomBlock.setBloKey(bloKey);
            websiteCustomBlock.setRenderSerial(1);
            if(specialActivityId != null && specialActivityId > 0){
            	websiteCustomBlock.setSpecialActivityId(specialActivityId);
            }else{
            	websiteCustomBlock.setSpecialActivityId(-1);
            }
            
            websiteCustomBlock = websiteCustomBlockEnterpriseService.create(websiteCustomBlock);
        }
        return new SimpleJsonResponse(true, websiteCustomBlock);
    }
    
    @RequestMapping(value = "/admin/customEnterprise/modifyImage.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse modifyImage(@RequestParam(value = "bloKey") String bloKey,
                                                        @RequestParam(value = "blockId", required = false) Integer blockId,
                                                        @RequestParam(value = "type", defaultValue = "create") String type,
                                                        @RequestParam(value = "cateType[]", required = false) String[] cateType,
                                                        @RequestParam(value = "partner_cate[]", required = false) String[] partner_cate,
                                                        @RequestParam(value = "child_cate[]", required = false) String[] child_cate,
                                                        @RequestParam(value = "data[]", required = false) String[] data,
                                                        @RequestParam(value = "linkTo[]", required = false) String[] linkTo,
                                                        @RequestParam(value = "description[]", defaultValue = "") String[] description) 
    {
        WebsiteCustomBlock websiteCustomBlock = new WebsiteCustomBlock();
        if("edit".equals(type)) {
            websiteCustomBlock = websiteCustomBlockEnterpriseService.findOne(blockId);
            WebsiteCustomBlockEntry imageEntry = new WebsiteCustomBlockEntry();
            if (data[0].isEmpty() || !internalAttachmentService.exists(data[0])) {
                return new SimpleJsonResponse<>(false, "存储引擎提示您指定的图片路径不是合法的图片");
            }
            imageEntry.setLext1(cateType[0]);
            switch (cateType[0]) {
                case "cate":
                    if(!"-".equals(child_cate[0])) {
                        imageEntry.setLinkTo("/jdvop/mobile/products.php?c=" + child_cate[0]);
                        imageEntry.setLext2(child_cate[0]);
                    } else {
                        imageEntry.setLinkTo("/jdvop/mobile/products.php?c=" + partner_cate[0]);
                        imageEntry.setLext2(partner_cate[0]);
                    }
                    break;
                case "product":
                    imageEntry.setLinkTo("/jdvop/mobile/products.php");
                    break;
                default:
                    if (linkTo[0].isEmpty()) {
                        return new SimpleJsonResponse<>(false, "网站广告的链接是必须填写的");
                    }
                    imageEntry.setLinkTo(linkTo[0]);
            }
            imageEntry.setData(data[0]);
            if(description.length > 0) {
                imageEntry.setDescription(description[0]);
            }
            websiteCustomBlock.setContent(imageEntry);
            websiteCustomBlock.setBlockType(BlockType.image);
            websiteCustomBlock.setBloKey(bloKey);
            websiteCustomBlockEnterpriseService.save(websiteCustomBlock);
        } else {
            websiteCustomBlock.setBlockType(BlockType.image);
            websiteCustomBlock.setBloKey(bloKey);
            websiteCustomBlock.setRenderSerial(1);
            websiteCustomBlock.setSpecialActivityId(-1);
            websiteCustomBlock = websiteCustomBlockEnterpriseService.create(websiteCustomBlock);
        }
        return new SimpleJsonResponse(true, websiteCustomBlock);
    }
    
    @RequestMapping(value = "/admin/customEnterprise/modifyProductBox.php", method = RequestMethod.GET)
    public @ResponseBody SimpleJsonResponse modifyProductBox(@RequestParam(value = "bloKey") String bloKey,
                                                             @RequestParam(value = "blockId", required = false) Integer blockId,
                                                             @RequestParam(value = "type", defaultValue = "create") String type,
                                                             @RequestParam(value = "backImag", required = false) String backImag,
                                                             @RequestParam(value = "couponCodes", required = false) String couponCodes,
                                                             @RequestParam(value = "model_title", required = false) String title,
                                                             @RequestParam(value = "product_num", defaultValue = "6") Integer showNum,
                                                             @RequestParam(value = "product_check", defaultValue = "PRODUCT_BLOCK_2") String showType,
                                                             @RequestParam(value = "product_id[]", required = false) List<Integer> productData,
                                                             @RequestParam(value ="specialActivityId", required =false) Integer specialActivityId) 
    {
    	if(StringUtils.isNotBlank(couponCodes)){
    		String[] result = couponCodes.split(",");
        	for (String string : result) {
    			Coupon coupon = couponService.findByCode(string);
    			if(coupon == null){
    				return new SimpleJsonResponse(false, "请检查优惠券编码！");
    			}else {
    				if (coupon.getTargetType() != 4){
    					return new SimpleJsonResponse(false, "请检查优惠券编码！");
    				}else if(!coupon.getTargetId().equals(specialActivityId) ){
    					return new SimpleJsonResponse(false, "优惠券编码不属于此专题！");
    				}
    					
    			}
    		}
    	}
    		
        WebsiteCustomBlock websiteCustomBlock = new WebsiteCustomBlock();
        if("edit".equals(type)) {
            websiteCustomBlock = websiteCustomBlockEnterpriseService.findOne(blockId);
            Map<String, Object> ext = websiteCustomBlock.getExt();
            if(ext == null){
                ext = new HashMap<>();
            }else{
                ext = new HashMap<>(websiteCustomBlock.getExt());
            }
            ext.put("model_title", title);
            websiteCustomBlock.setExt(ext);
            websiteCustomBlock.setBackImag(backImag);
            websiteCustomBlock.setCouponCodes(couponCodes);
            websiteCustomBlock.setBloKey(bloKey);
            websiteCustomBlock.setContent(productData);
            websiteCustomBlockEnterpriseService.save(websiteCustomBlock);
        } else {
            Map<String, Object> ext = new HashMap<>();
            ext.put("model_title", title);
            websiteCustomBlock.setExt(ext);
            websiteCustomBlock.setBackImag(backImag);
            websiteCustomBlock.setCouponCodes(couponCodes);
            websiteCustomBlock.setBloKey(bloKey);
            websiteCustomBlock.setBlockType(BlockType.products);
            websiteCustomBlock.setRenderSerial(1);
            if(specialActivityId != null && specialActivityId >0){
            	websiteCustomBlock.setSpecialActivityId(specialActivityId);
            }else{
            	websiteCustomBlock.setSpecialActivityId(-1);
            }
            websiteCustomBlock = websiteCustomBlockEnterpriseService.create(websiteCustomBlock);
        }
        return new SimpleJsonResponse(true, websiteCustomBlock);
    }
    
    //图片广告
    @RequestMapping(value = "/admin/customEnterprise/modifySlider.php", method = RequestMethod.GET)
    public @ResponseBody SimpleJsonResponse modifySlider(@RequestParam(value = "bloKey") String bloKey,
                                                         @RequestParam(value = "blockId", required = false) Integer blockId,
                                                         @RequestParam(value = "type", defaultValue = "create") String type,
                                                         @RequestParam(value = "data[]", required = false) String[] data,
                                                         @RequestParam(value = "linkTo[]", required = false) String[] linkTo,
                                                         @RequestParam(value = "cateType[]", required = false) String[] cateType,
                                                         @RequestParam(value = "partner_cate[]", required = false) String[] partner_cate,
                                                         @RequestParam(value = "child_cate[]", required = false) String[] child_cate,
                                                         @RequestParam(value = "description[]", required = false) String[] description,
                                                         @RequestParam(value ="specialActivityId", required =false) Integer specialActivityId) 
    {
        WebsiteCustomBlock websiteCustomBlock = new WebsiteCustomBlock();
        if("edit".equals(type)) {//编辑模块
            websiteCustomBlock = websiteCustomBlockEnterpriseService.findOne(blockId);
            List<WebsiteCustomBlockEntry> sliderEntrys = new ArrayList<>();
            int cate_count = 0;
            for(int i=0; i<linkTo.length; i++) {
                WebsiteCustomBlockEntry sliderEntry = new WebsiteCustomBlockEntry();
                switch (cateType[i]) {
                    case "cate":
                        if(!"-".equals(child_cate[cate_count])) {
                            sliderEntry.setLinkTo("/jdvop/mobile/products.php?c=" + child_cate[cate_count]);
                            sliderEntry.setLext2(child_cate[cate_count]);
                        } else {
                            sliderEntry.setLinkTo("/jdvop/mobile/products.php?c=" + partner_cate[cate_count]);
                            sliderEntry.setLext2(partner_cate[cate_count]);
                        }
                        cate_count ++;
                        break;
                    case "product":
                        sliderEntry.setLinkTo("/jdvop/mobile/products.php");
                        break;
                    default:
                        if (data[i].isEmpty()) {
                            return new SimpleJsonResponse<>(false, "请上传第【" + i + "】个广告图片");
                        }
                        if(linkTo[i].isEmpty()) {
                            return new SimpleJsonResponse<>(false, "请输入第【" + i + "】个广告链接地址");
                        }
                        sliderEntry.setLinkTo(linkTo[i]);
                }
                sliderEntry.setData(data[i]);
                sliderEntry.setDescription(description[i]);
                sliderEntry.setLext1(cateType[i]);
                sliderEntrys.add(sliderEntry);
            }
            websiteCustomBlock.setContent(sliderEntrys);
            websiteCustomBlock.setBlockType(BlockType.slider);
            websiteCustomBlockEnterpriseService.save(websiteCustomBlock);
        } else {//第一次创建模块
            websiteCustomBlock.setBloKey(bloKey);
            websiteCustomBlock.setRenderSerial(1);
            websiteCustomBlock.setBlockType(BlockType.slider);
            if(specialActivityId != null && specialActivityId >0){
            	websiteCustomBlock.setSpecialActivityId(specialActivityId);
            }else{
            	websiteCustomBlock.setSpecialActivityId(-1);
            }
            
            websiteCustomBlock = websiteCustomBlockEnterpriseService.create(websiteCustomBlock);
        }
        return new SimpleJsonResponse(true, websiteCustomBlock);
    }
    
    //保存自定义配置
    @RequestMapping(value = "/admin/customEnterprise/save.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse save(@RequestParam(value = "blockId[]", required = false) Integer[] blockId)
    {
        if(blockId == null || blockId.length == 0) {
            return new SimpleJsonResponse(false, "您还没有任何配置，请配置后保存！");
        }
        for(int i = 0;i < blockId.length; i++) {
            WebsiteCustomBlock websiteCustomBlock = websiteCustomBlockEnterpriseService.findOne(blockId[i]);
            websiteCustomBlock.setRenderSerial(i+1);
            websiteCustomBlock.setSpecialActivityId(-1);
            websiteCustomBlockEnterpriseService.save(websiteCustomBlock);
        }
        return new SimpleJsonResponse(true, null); 
    }
    
    
    @RequestMapping(value = "/admin/customEnterprise/upload.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse<String> uploadImage(@RequestParam(value = "Filedata") MultipartFile file,
                                                                @RequestParam("width") int width,
                                                                @RequestParam("height") int height,
                                                                @RequestParam(value = "type", defaultValue = "slider") String type,
                                                                Website website) throws IOException 
    {
        if (file.isEmpty()) {
            return new SimpleJsonResponse(false, "请上传合法的图片文件");
        }
        if (website == null) {
            return new SimpleJsonResponse<>(false, "非法的文件上传请求");
        }
        byte[] bytes = file.getBytes();
        if("slider".equals(type)) {
            SimpleImageInfo sif;
            try {
                sif = new SimpleImageInfo(bytes);
                if (width > 0 && sif.getWidth() != width) {
                    return new SimpleJsonResponse(false, "你上传的图片尺寸不合法！尺寸应为640 * " + height);
                }
                if (height > 0 && sif.getHeight() != height) {
                    return new SimpleJsonResponse(false, "你上传的图片尺寸不合法！尺寸应为640 * " + height);
                }
            } catch (IOException exp) {
                return new SimpleJsonResponse(false, "您上传的文件不是合法的图片格式");
            }
        }
        String path = internalAttachmentService.save(new SerializableMultipartFile(bytes, file.getName(), file.getOriginalFilename(), file.getContentType(), file.getSize()));
        return new SimpleJsonResponse(true, path);
    }
    
    //删除自定义模板中指定模块
    @RequestMapping(value = "/admin/customEnterprise/delete.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse<String> deleteToBlock(@RequestParam("id") int blockId)
    {
        WebsiteCustomBlock customBlock = websiteCustomBlockEnterpriseService.findOne(blockId);
        if (customBlock == null)
        {
            return new SimpleJsonResponse<>(false, "删除的内容不存在!");
        }
        websiteCustomBlockEnterpriseService.delByBlockId(blockId);
        return new SimpleJsonResponse<>(true, null);
    }
    
    @RequestMapping(value = "/admin/customEnterprise/cates.php", method = RequestMethod.GET)
    public @ResponseBody List<Map<String, Object>> list()
    {
        List<Map<String, Object>> result = new ArrayList<>();
        List<ProductCate> pudcList = productCateService.findAll();
        for(ProductCate cate : pudcList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cate.getId());
            map.put("name", cate.getName());
            map.put("partnerId", (cate.getParentId() != null) ? cate.getParentId() : 0);
            result.add(map);
            
            if(cate.getParentId() != null)
            {
                List<ProductCate> allChilds = productCateService.allChilds(cate);
                for(ProductCate childCate : allChilds) {
                    map = new HashMap<>();
                    map.put("id", childCate.getId());
                    map.put("name", childCate.getName());
                    map.put("partnerId", (childCate.getParentId() != null) ? childCate.getParentId() : 0);
                    result.add(map);
                }
            }
        }
        return result;
    }
    
    //获取对应子分类
    @RequestMapping(value = "/admin/customEnterprise/childCates.php", method = RequestMethod.GET)
    public @ResponseBody List<Map<String, Object>> childList(@RequestParam(value = "cateId") int id)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        ProductCate cate = productCateService.findOne(id);
        if(cate.getParentId() == null) 
        {
            List<ProductCate> pudcList = productCateService.allChilds(cate);
            for(ProductCate c : pudcList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", c.getId());
                map.put("name", c.getName());
                result.add(map);
            }
        }
        return result;
    }
    
    @RequestMapping(value = "/admin/customEnterprise/getCustomBlocks.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public @ResponseBody List<Map<String, Object>> list(@RequestParam(value = "id") int blockId, Website website)
    {
        WebsiteCustomBlock customBlock = websiteCustomBlockEnterpriseService.findOne(blockId);
        List<Map<String, Object>> result = new ArrayList<>();
        if(customBlock.getBlockType() == BlockType.products) {
            List<Integer> ids = (List) customBlock.getContent();
            if(ids != null && ids.size() > 0) {
                for (Integer id : ids) {
                    Map<String, Object> pdMap = new HashMap<>();
                    Product product = productService.findOne(id);
                    pdMap.put("id", product.getId());
                    pdMap.put("name", product.getName());
                    pdMap.put("thumbnailImage", product.getThumbnailImage());
                    pdMap.put("ownerType", product.getOwnerType());
                    pdMap.put("retailPrice", product.getRetailPrice());
                    result.add(pdMap);
                }
            }
        } else {
            List<WebsiteCustomBlockEntry> entrys = (List) customBlock.getContent();
            if(entrys != null && entrys.size() > 0) {
                for (WebsiteCustomBlockEntry entry : entrys) {
                    Map<String, Object> entryMap = new HashMap<>();
                    entryMap.put("data", entry.getData());
                    entryMap.put("linkTo", entry.getLinkTo());
                    entryMap.put("description", (entry.getDescription() != null) ? entry.getDescription() : "");
                    entryMap.put("selectFlag", entry.getLext1());
                    
                    if(entry.getLext2() != null && !"-".equals(entry.getLext2())) {
                        ProductCate cate = productCateService.findOne(Integer.parseInt(entry.getLext2()));
                        if(cate != null) {
                            entryMap.put("partner_cate", (cate.getParentId() != null) ? cate.getParentId() : cate.getId());
                            entryMap.put("child_cate", (cate.getParentId() != null) ? cate.getId() : 0);
                        } else {
                            entryMap.put("partner_cate", 0);
                            entryMap.put("child_cate", 0);
                        }
                    }
                    result.add(entryMap);
                }
            }
        }
        return result;
    }
    
    @RequestMapping(value = "/admin/customEnterprise/getCustomBlock.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public @ResponseBody Map<String, Object> map(@RequestParam(value = "id") int blockId)
    {
        WebsiteCustomBlock customBlock = websiteCustomBlockEnterpriseService.findOne(blockId);
        Map<String, Object> result = new HashMap<>();
        if(customBlock.getBlockType() == BlockType.image) {
            WebsiteCustomBlockEntry entry = (WebsiteCustomBlockEntry) customBlock.getContent();
            result.put("data", entry.getData());
            result.put("linkTo", entry.getLinkTo());
            result.put("description", (entry.getDescription() != null) ? entry.getDescription() : "");
            result.put("selectFlag", entry.getLext1());
            if(entry.getLext2() != null && !"-".equals(entry.getLext2())) {
                ProductCate pudc = productCateService.findOne(Integer.parseInt(entry.getLext2()));
                if(pudc != null) {
                    result.put("partner_cate", (pudc.getParentId() != null) ? pudc.getParentId() : pudc.getId());
                    result.put("child_cate", (pudc.getParentId() != null) ? pudc.getId() : 0);
                    result.put("child_cate_name", (pudc.getParentId() != null) ? pudc.getName() : "");
                } else {
                    result.put("partner_cate", 0);
                    result.put("child_cate", 0);
                    result.put("child_cate_name", "");
                }
            }
        } 
        return result;
    }

}
