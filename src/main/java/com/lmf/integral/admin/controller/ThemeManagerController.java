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
import com.lmf.website.theme.v2.BlockDef;
import com.lmf.website.theme.v2.BlockType;
import com.lmf.website.theme.v2.BlockValue;
import com.lmf.website.theme.v2.PageBlockDef;
import com.lmf.website.theme.v2.PageBlockType;
import com.lmf.website.theme.v2.PageSkeleton;
import com.lmf.website.theme.v2.PageSkeleton.PageBlock;
import com.lmf.website.theme.v2.manager.ThemeManager;
import com.lmf.website.theme.v2.value.BookletsBlockValue;
import com.lmf.website.theme.v2.value.ImageBlockValue;
import com.lmf.website.theme.v2.value.ProductsBlockValue;
import com.lmf.website.theme.v2.value.SliderBlockValue;
import com.lmf.website.theme.v2.value.TextBlockValue;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
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
@Controller("themeManagerController")
public class ThemeManagerController {
    
    @Autowired
    private ThemeManager themeManager;
    
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
    
    @RequestMapping(value = "/admin/website/module.php", method = RequestMethod.GET)
    public String modules(@RequestParam(value = "init", defaultValue = "false") boolean initPageSkeleton,
                          HttpSession session, Model model)
    {
        if(initPageSkeleton)
        {
            //初始化已经保存的配置
            PageSkeleton pageSkeleton = themeManager.getPageSkeleton();
            if(pageSkeleton != null)
            {
                PageBlockDef header = themeManager.getThemeDef(PageBlockType.header, pageSkeleton.getHeader().getBlockKey());
                List<PageSkeleton.PageBlock> pageBlocks = pageSkeleton.getBodyBlocks();
                List<PageBlockDef> blocksList = new ArrayList<>();
                List<String> blockKeys = new ArrayList<>();
                for(PageSkeleton.PageBlock ppb : pageBlocks)
                {
                    PageBlockDef block = themeManager.getThemeDef(PageBlockType.block, ppb.getBlockKey());
                    if(block != null){
                        blocksList.add(block);
                        blockKeys.add(ppb.getBlockKey());
                    }
                }
                
                PageBlockDef footer = themeManager.getThemeDef(PageBlockType.footer, pageSkeleton.getFooter().getBlockKey());
                
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put(SessionKeyFlag.WEBSITE_HEADER, header.getKey());
                dataMap.put(SessionKeyFlag.WEBSITE_BLOCKS, blockKeys);
                dataMap.put(SessionKeyFlag.WEBSITE_FOOTER, footer.getKey());
                session.setAttribute(SessionKeyFlag.WEBSITE_SETTING_DATA, dataMap);
                
                model.addAttribute("header", header);
                model.addAttribute("blockList", blocksList);
                model.addAttribute("footer", footer);
                model.addAttribute("colorTheme", pageSkeleton.getColorTheme());
            }
        }else{
            Map<String, Object> dataMap = (Map)session.getAttribute(SessionKeyFlag.WEBSITE_SETTING_DATA);
            if(dataMap != null)
            {
                if(dataMap.containsKey(SessionKeyFlag.WEBSITE_HEADER)){
                    String headerKey = (String)dataMap.get(SessionKeyFlag.WEBSITE_HEADER);
                    PageBlockDef header = themeManager.getThemeDef(PageBlockType.header, headerKey);
                    if(header != null){
                        model.addAttribute("header", header);
                    }
                }
                
                if(dataMap.containsKey(SessionKeyFlag.WEBSITE_BLOCKS)){
                    List<String> blocksKey = (List)dataMap.get(SessionKeyFlag.WEBSITE_BLOCKS);
                    if(blocksKey != null){
                        List<PageBlockDef> blocksList = new ArrayList<>();
                        for(String key : blocksKey){
                            PageBlockDef block = themeManager.getThemeDef(PageBlockType.block, key);
                            if(block != null){
                                blocksList.add(block);
                            }
                        }
                        if(!blocksList.isEmpty()){
                            model.addAttribute("blockList", blocksList);
                        }
                    }
                }
                
                if(dataMap.containsKey(SessionKeyFlag.WEBSITE_FOOTER)){
                    String footerKey = (String)dataMap.get(SessionKeyFlag.WEBSITE_FOOTER);
                    PageBlockDef footer = themeManager.getThemeDef(PageBlockType.footer, footerKey);
                    if(footer != null){
                        model.addAttribute("footer", footer);
                    }
                }
            }
        }
        
        return "admin/website/setting/module";
    }
    
    @RequestMapping(value = "/admin/website/module/save.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse save(@RequestParam(value = "colorTheme", required = false) String colorTheme,
                                                 @RequestParam(value = "blockKeys[]", required = false) String[] blockKeys, //中间模板样式
                                                 Model model,HttpSession session)
    {
        if(blockKeys == null || blockKeys.length == 0){
            return new SimpleJsonResponse(false, "您似乎还没有选择中间的模板样式");
        }
        Map<String, Object> dataMap = (Map)session.getAttribute(SessionKeyFlag.WEBSITE_SETTING_DATA);
        if(dataMap == null){
            return new SimpleJsonResponse(false, "您似乎还没有选择任何模块");
        }
        if(!dataMap.containsKey(SessionKeyFlag.WEBSITE_HEADER)){
            return new SimpleJsonResponse(false, "您似乎还没有选择头部的模板样式");
        }
        if(!dataMap.containsKey(SessionKeyFlag.WEBSITE_BLOCKS)){
            return new SimpleJsonResponse(false, "您似乎还没有选择中间的模板样式");
        }
        if(!dataMap.containsKey(SessionKeyFlag.WEBSITE_FOOTER)){
            return new SimpleJsonResponse(false, "您似乎还没有选择底部的模板样式");
        }
        //header
        String headerKey = (String)dataMap.get(SessionKeyFlag.WEBSITE_HEADER);
        PageBlockDef headerDef = themeManager.getThemeDef(PageBlockType.header, headerKey);
        if(headerDef == null){
            return new SimpleJsonResponse(false, "数据异常,请刷新后重新操作");
        }
        
        //body
        List<PageBlockDef> blockDefs = new ArrayList<>();
        for(String blockKey : blockKeys){
            PageBlockDef block = themeManager.getThemeDef(PageBlockType.block, blockKey);
            if(block != null){
               blockDefs.add(block);
            }
        }
        if(blockDefs.isEmpty()){
            return new SimpleJsonResponse(false, "数据异常,请刷新后重新操作");
        }
        
        //footer
        String footerKey = (String)dataMap.get(SessionKeyFlag.WEBSITE_FOOTER);
        PageBlockDef footerDef = themeManager.getThemeDef(PageBlockType.footer, footerKey);
        if(footerDef == null){
           return new SimpleJsonResponse(false, "数据异常,请刷新后重新操作"); 
        }
       
        
        //保存配置
        PageSkeleton.PageBlock oldHeader = null;
        PageSkeleton.PageBlock oldFooter = null;
        Map<String, List<PageSkeleton.PageBlock>> oldBlocks = new HashMap<>();
        PageSkeleton skeleton  = themeManager.getPageSkeleton();
        if(skeleton == null){
            skeleton  = new PageSkeleton();
        }else{
            oldHeader = skeleton.getHeader();
            oldFooter = skeleton.getFooter();
            List<PageSkeleton.PageBlock> oldBlockList = skeleton.getBodyBlocks();
            for(PageSkeleton.PageBlock pb : oldBlockList){
                List<PageSkeleton.PageBlock> pbList = oldBlocks.get(pb.getBlockKey());
                if(pbList == null){
                    pbList = new ArrayList<>();
                    oldBlocks.put(pb.getBlockKey(), pbList);
                }
                pbList.add(pb);
            }
        }
        
        PageSkeleton.PageBlock header;
        if(oldHeader != null && oldHeader.getBlockKey().equals(headerKey)){
            //存在此模块
            header = oldHeader;
        }else{
            header = skeleton.new PageBlock();
            List<BlockDef> tempHeaders = headerDef.getBlockDefs();
            Map<String, BlockValue> headerBlockValues = new HashMap<>();
            for(BlockDef def : tempHeaders){
                headerBlockValues.put(def.getKey(), getNewBlockValue(def));
            }
            header.setBlockKey(headerKey);
            header.setBlockValues(headerBlockValues);
        }
        
        List<PageSkeleton.PageBlock> bodys = new ArrayList<>();
        for(PageBlockDef pageDef : blockDefs){
            PageSkeleton.PageBlock body ;
            List<PageSkeleton.PageBlock> sameBlocks = oldBlocks.get(pageDef.getKey());
            if(sameBlocks != null && !sameBlocks.isEmpty()){
                //存在此模块
                body = sameBlocks.get(0);
                sameBlocks.remove(0);
            }else{
                //不存在
                body = skeleton.new PageBlock();
                List<BlockDef> tempbodys = pageDef.getBlockDefs();
                Map<String, BlockValue> bodyBlockValues = new HashMap<>();
                for(BlockDef def : tempbodys){
                    bodyBlockValues.put(def.getKey(), getNewBlockValue(def));
                }
                body.setBlockKey(pageDef.getKey());
                body.setBlockValues(bodyBlockValues);
            }
            bodys.add(body);
        }
        
        //存在此模块
        PageSkeleton.PageBlock footer;
        if(oldFooter != null && oldFooter.getBlockKey().equals(footerKey)){
            //存在此模块 
            footer = oldFooter;
        }else{
            footer = skeleton.new PageBlock();
            List<BlockDef> tempFooters = footerDef.getBlockDefs();
            Map<String, BlockValue> footerBlockValues = new HashMap<>();
            for(BlockDef def : tempFooters){
                footerBlockValues.put(def.getKey(), getNewBlockValue(def));
            }
            footer.setBlockKey(footerDef.getKey());
            footer.setBlockValues(footerBlockValues);
        }
        
        skeleton.setHeader(header);
        skeleton.setBodyBlocks(bodys);
        skeleton.setFooter(footer);
        skeleton.setColorTheme(colorTheme);
        themeManager.save(skeleton);
        
        //清空session数据
        session.removeAttribute(SessionKeyFlag.WEBSITE_SETTING_DATA);
        
        return new SimpleJsonResponse(true, "数据保存成功");
    }
    
    
    
    @RequestMapping(value = "/admin/website/module/header.php", method = RequestMethod.GET)
    public String chooseHeader(Model model)
    {
        Collection<PageBlockDef> pageBlockDefs = themeManager.getThemeDefs(PageBlockType.header);
        model.addAttribute("blocks", pageBlockDefs.toArray());
        return "/admin/website/setting/choose_header";
    }
    
    
    @RequestMapping(value = "/admin/website/module/header.php", method = RequestMethod.POST)
    public String chooseHeader(@RequestParam("key") String key, 
                               HttpSession session, Model model)
    {
        Map<String, Object> dataMap = (Map)session.getAttribute(SessionKeyFlag.WEBSITE_SETTING_DATA);
        if(dataMap == null){
            dataMap = new HashMap<>();
        }
        dataMap.put(SessionKeyFlag.WEBSITE_HEADER, key);
        session.setAttribute(SessionKeyFlag.WEBSITE_SETTING_DATA, dataMap);
        return "redirect:/admin/website/module.php";
    }
    
    
    @RequestMapping(value = "/admin/website/module/choose.php", method = RequestMethod.GET)
    public String chooseModule(Model model)
    {
        Collection<PageBlockDef> pageBlockDefs = themeManager.getThemeDefs(PageBlockType.block);
        model.addAttribute("blocks", pageBlockDefs.toArray());
        return "/admin/website/setting/choose_module";
    }
    
    
    @RequestMapping(value = "/admin/website/module/choose.php", method = RequestMethod.POST)
    public String chooseModule(@RequestParam("key") String key, 
                               HttpSession session, Model model)
    {
        Map<String, Object> dataMap = (Map)session.getAttribute(SessionKeyFlag.WEBSITE_SETTING_DATA);
        if(dataMap == null){
            dataMap = new HashMap<>();
        }
        List<String> blockList = (List)dataMap.get(SessionKeyFlag.WEBSITE_BLOCKS);
        if(blockList == null){
            blockList = new ArrayList<>();
            dataMap.put(SessionKeyFlag.WEBSITE_BLOCKS, blockList);
        }
        blockList.add(key);
        session.setAttribute(SessionKeyFlag.WEBSITE_SETTING_DATA, dataMap);
        return "redirect:/admin/website/module.php";
    }
    
    @RequestMapping(value = "/admin/website/module/footer.php", method = RequestMethod.GET)
    public String chooseFooter(Model model)
    {
        Collection<PageBlockDef> pageBlockDefs = themeManager.getThemeDefs(PageBlockType.footer);
        model.addAttribute("blocks", pageBlockDefs.toArray());
        return "/admin/website/setting/choose_footer";
    }
    
    
    @RequestMapping(value = "/admin/website/module/footer.php", method = RequestMethod.POST)
    public String chooseFooter(@RequestParam("key") String key, 
                               HttpSession session, Model model)
    {
        Map<String, Object> dataMap = (Map)session.getAttribute(SessionKeyFlag.WEBSITE_SETTING_DATA);
        if(dataMap == null){
            dataMap = new HashMap<>();
        }
        dataMap.put(SessionKeyFlag.WEBSITE_FOOTER, key);
        session.setAttribute(SessionKeyFlag.WEBSITE_SETTING_DATA, dataMap);
        return "redirect:/admin/website/module.php";
    }
    
    
    @RequestMapping(value = "/admin/website/module/delete.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse clear(@RequestParam("key") String key,
                                                  @RequestParam("type") PageBlockType type,
                                                  @RequestParam(value = "index", defaultValue = "1") int index,
                                                  HttpSession session)
    {
        Map<String, Object> dataMap = (Map)session.getAttribute(SessionKeyFlag.WEBSITE_SETTING_DATA);
        if(dataMap != null){
            if(PageBlockType.header == type){
                dataMap.remove(SessionKeyFlag.WEBSITE_HEADER);
            }else if(PageBlockType.block == type){
                List<String> blockList = (List)dataMap.get(SessionKeyFlag.WEBSITE_BLOCKS);
                if(blockList != null && blockList.size() >= index){
                    blockList.remove(index-1);
                }
                dataMap.put(SessionKeyFlag.WEBSITE_BLOCKS, blockList);
            }else if(PageBlockType.footer == type){
                dataMap.remove(SessionKeyFlag.WEBSITE_FOOTER);
            }
            session.setAttribute(SessionKeyFlag.WEBSITE_SETTING_DATA, dataMap);
        }
        return new SimpleJsonResponse(true, "操作成功");
    }
    
    @RequestMapping(value = "/admin/website/setting/chooseProducts.php", method = RequestMethod.GET)
    public String chooseProducts(@RequestParam(value = "kw", required = false) String keyword,
                                 @RequestParam(value = "brand", required = false) Brand brand,
                                 @RequestParam(value = "cate", required = false) ProductCate cate,
                                 @RequestParam(value = "minPrice", required = false) Double minPrice,
                                 @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                                 @RequestParam(value = "maxSize", defaultValue = "1") int maxSize,
                                 @RequestParam(value = "minSize", defaultValue = "1") int minSize,
                                 @RequestParam(value = "id[]", required = false) Integer[] pids,
                                 @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager,
                                 Model model){

        StringBuilder link = new StringBuilder("/jdvop/admin/website/setting/loadProducts.php?page=[:page]");
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
        Page<Product> products = productService.find(new ProductCriteria().withStatus(ProductStatus.selling).withDeleted(Boolean.FALSE), pager);;
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
        
    
        return "admin/website/setting/choose_product";
    }
    
    @RequestMapping(value = "/admin/website/setting/loadProducts.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public  @ResponseBody  Map<String, Object>  loadProducts(@RequestParam(value = "kw", required = false) String keyword,
                                                            @RequestParam(value = "brand", required = false) Brand brand,
                                                            @RequestParam(value = "cate", required = false) ProductCate cate,
                                                            @RequestParam(value = "minPrice", required = false) Double minPrice,
                                                            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                                                            @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager) throws UnsupportedEncodingException 
    {
        Page<Product>   products    = productService.find(new ProductCriteria().withKeyword(keyword).withBrand(brand).withCate(cate).withPrice(minPrice, maxPrice).withStatus(ProductStatus.selling), pager);
        StringBuilder url = new StringBuilder("/jdvop/admin/website/setting/loadProducts.php?page=[:page]");
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
    
    @RequestMapping(value = "/admin/website/setting/getProducts.php", method = RequestMethod.GET)
    public @ResponseBody SimpleJsonResponse getProducts(@RequestParam(value = "product_id[]", required = false) Integer[] pids, Model model)
    {
        if(pids != null && pids.length > 0){
            Map<Integer, Product> bpMap = productService.findAsMap(Arrays.asList(pids));
            List<Product> bpList = new ArrayList<>(bpMap.size());
            for(Integer id : pids){
                bpList.add(bpMap.get(id));
            }
            return new SimpleJsonResponse(true, bpList);
        }
        return new SimpleJsonResponse(false, "您还没有选择任何产品数据");
    }
    
    private String  renderProduct(Page<Product> products) {
        StringBuilder html = new StringBuilder();
        for (Product pd : products) {
            renderProduct(html, pd);
        }
        return html.toString();
    }
    
    private void    renderProduct(StringBuilder buffer, Product product) {
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
        buffer.append("</li>");
    }
    
     @RequestMapping(value = "/admin/website/setting/img/upload.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse<String> uploadImage(@RequestParam(value = "filedData") MultipartFile file,
                                                                @RequestParam(value = "width", defaultValue = "0") int width,
                                                                @RequestParam(value = "height", defaultValue = "0") int  height,
                                                                @RequestParam(value = "size", defaultValue = "0") int size) throws IOException {
        if (file.isEmpty()) {
            return new SimpleJsonResponse(false, "请上传合法的图片文件");
        }
        byte[] bytes = file.getBytes();
        SimpleImageInfo sif;
        try {
            sif = new SimpleImageInfo(bytes);
            if (width > 0 && sif.getWidth() != width) {
                return new SimpleJsonResponse(false, "请上传尺寸为" + width + "*" + height + "px" + "的图片");
            }
            if (height > 0 && sif.getHeight() != height) {
                return new SimpleJsonResponse(false, "请上传尺寸为" + width + "*" + height + "px" + "图片");
            }
        } catch (IOException exp) {
            return new SimpleJsonResponse(false, "您上传的文件不是合法的图片格式");
        }
        String path = internalAttachmentService.save(new SerializableMultipartFile(bytes, file.getName(), file.getOriginalFilename(), file.getContentType(), file.getSize()));
        return new SimpleJsonResponse(true, path);
    }
    
    
    private BlockValue getNewBlockValue(BlockDef def){
        if(BlockType.booklets == def.type()){
            BookletsBlockValue bbv = new BookletsBlockValue();
            bbv.setValue(null);
            return bbv;
        }else if(BlockType.products == def.type()){
            ProductsBlockValue pbv = new ProductsBlockValue();
            return pbv;
        }else if(BlockType.image == def.type()){
            ImageBlockValue ibv = new ImageBlockValue();
            return ibv;
        }else if(BlockType.text == def.type()){
            TextBlockValue tbv = new TextBlockValue();
            return tbv;
        }else if(BlockType.slider == def.type()){
            SliderBlockValue sbv = new SliderBlockValue();
            return sbv;
        }else{
            return null;
        }
    }
    
    private static class SessionKeyFlag{
        public static String WEBSITE_SETTING_DATA = "WEBSITE_SETTING_DATA";
        public static String WEBSITE_HEADER = "WEBSITE_HEADER";
        public static String WEBSITE_BLOCKS = "WEBSITE_BLOCKS";
        public static String WEBSITE_FOOTER = "WEBSITE_FOOTER";
    }
}
