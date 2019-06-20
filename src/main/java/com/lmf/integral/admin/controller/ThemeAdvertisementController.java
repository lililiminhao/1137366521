/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.controller;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SerializableMultipartFile;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.enums.OwnerType;
import com.lmf.common.exceptions.PermissionDeniedException;
import com.lmf.common.util.PagerSpec;
import com.lmf.common.util.PagerUtil;
import com.lmf.common.util.SimpleImageInfo;
import com.lmf.enterprise.service.EnterpriseExclusiveProductPoolEntryService;
import com.lmf.enums.ProductStatus;
import com.lmf.integral.SystemConfig;
import com.lmf.product.entity.Brand;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.ProductCate;
import com.lmf.product.service.BrandService;
import com.lmf.product.service.ProductCateService;
import com.lmf.product.service.ProductService;
import com.lmf.product.vo.BrandCriteria;
import com.lmf.product.vo.ProductCriteria;
import com.lmf.sys.service.InternalAttachmentService;
import com.lmf.website.entity.SpecialActivity;
import com.lmf.website.enums.SpecialActivityType;
import com.lmf.website.service.SpecialActivityService;
import com.lmf.website.theme.v2.BlockDef;
import com.lmf.website.theme.v2.BlockType;
import com.lmf.website.theme.v2.BlockValue;
import com.lmf.website.theme.v2.PageBlockDef;
import com.lmf.website.theme.v2.PageBlockType;
import com.lmf.website.theme.v2.PageSkeleton;
import com.lmf.website.theme.v2.PageSkeleton.PageBlock;
import com.lmf.website.theme.v2.def.BookletsBlockDef;
import com.lmf.website.theme.v2.def.ProductsBlockDef;
import com.lmf.website.theme.v2.def.SliderBlockDef;
import com.lmf.website.theme.v2.def.TextBlockDef;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
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
 * @author shenzhixiong
 */
@Controller
public class ThemeAdvertisementController {
    
    @Autowired
    private ThemeManager themeManager;
    
    @Autowired 
    private ProductService productService;
    
    @Autowired
    private InternalAttachmentService internalAttachmentService;
    
    @Autowired
    private BrandService brandService;
    
    @Autowired
    private ProductCateService productCateService;
    
    @Autowired
    private SystemConfig systemConfig;
    
    @Autowired
    private SpecialActivityService specialActivityService;
    
    @Autowired
    private EnterpriseExclusiveProductPoolEntryService enterpriseExclusiveProductPoolEntryService;
    
    @RequestMapping(value = "/admin/website/skeleton.php", method = RequestMethod.GET)
    public String advertisement(Model model)
    {
        //初始化已经保存的配置
        PageSkeleton pageSkeleton = themeManager.getPageSkeleton();
        if(pageSkeleton != null)
        {
            PageBlockDef header = themeManager.getThemeDef(PageBlockType.header, pageSkeleton.getHeader().getBlockKey());
            List<PageSkeleton.PageBlock> pageBlocks = pageSkeleton.getBodyBlocks();
            LinkedHashMap<Integer, PageBlockDef> blockMap = new LinkedHashMap<>();
            int index = 0;
            for(PageSkeleton.PageBlock ppb : pageBlocks)
            {
                PageBlockDef block = themeManager.getThemeDef(PageBlockType.block, ppb.getBlockKey());
                if(block != null){
                    blockMap.put(index, block);
                }
                index++;
            }

            PageBlockDef footer = themeManager.getThemeDef(PageBlockType.footer, pageSkeleton.getFooter().getBlockKey());

            model.addAttribute("header", header);
            model.addAttribute("blockMap", blockMap);
            model.addAttribute("footer", footer);
            model.addAttribute("colorTheme", pageSkeleton.getColorTheme());
        }
        return "/admin/website/setting/page_skeleton";
    }
    
    
    
    @RequestMapping(value = "/admin/website/skeleton/blockAdv.php", method = RequestMethod.GET)
    public String advSetting(@RequestParam("key") String key,
                            @RequestParam("type") PageBlockType type,
                            @RequestParam(value="index", defaultValue = "0") int index,
                            Model model)
    {
        PageSkeleton pageSkeleton = themeManager.getPageSkeleton();
        if(pageSkeleton != null){
            PageSkeleton.PageBlock blockData = null;
            PageBlockDef blockDef = null;
            if(PageBlockType.header == type){
                blockDef = themeManager.getThemeDef(PageBlockType.header, key);
                blockData = pageSkeleton.getHeader();
                model.addAttribute("block", blockDef);
            }else if(PageBlockType.block == type){
                blockDef = themeManager.getThemeDef(PageBlockType.block, key);
                List<PageSkeleton.PageBlock> bodys = pageSkeleton.getBodyBlocks();
                blockData = bodys.get(index);
                model.addAttribute("block", blockDef);
            }else if(PageBlockType.footer == type){
                blockDef = themeManager.getThemeDef(PageBlockType.footer, key);
                blockData = pageSkeleton.getFooter();
                model.addAttribute("block", blockDef);
            }
            
            //检查是否配置过广告数据
            boolean flag = true;
            if(blockDef != null && blockData != null){
                Map<String, BlockValue> blockValues = blockData.getBlockValues();
                for(BlockDef def : blockDef.getBlockDefs())
                {
                    String currentKey = def.getKey();
                    BlockValue bv = blockValues.get(currentKey);
                    if(def.type() == BlockType.image){
                        ImageBlockValue imgValue = (ImageBlockValue)bv;
                        if(imgValue.getUrl() == null || imgValue.getUrl().trim().isEmpty()){
                            flag = false;
                            break;
                        }
                    }else if(def.type() == BlockType.slider){
                        SliderBlockValue sbv = (SliderBlockValue)bv;
                        if(sbv.getEntries() == null || sbv.getEntries().isEmpty()){
                            flag = false;
                            break;
                        }
                    }else if(def.type() == BlockType.text){
                        TextBlockValue tbv = (TextBlockValue)bv;
                        if(tbv.getText() == null || tbv.getText().isEmpty()){
                            flag = false;
                            break;
                        }
                    }else if(def.type() == BlockType.products){
                        ProductsBlockValue pbv = (ProductsBlockValue)bv;
                        if(pbv.getValue() == null || pbv.getValue().isEmpty()){
                            flag = false;
                            break;
                        }
                    }
                
                }
            }else{
                flag = false;
            }
            model.addAttribute("flag", flag);
            
            //在页面上还原已经保存的数据
            if(blockDef != null && blockData != null){
                Map<String, List<ImageBlockValue>> imageMap = new HashMap<>();
                Map<String, List<TextBlockValue>> textMap = new HashMap<>();
                Map<String, List<SliderBlockValue>> sliderMap = new HashMap<>();
                Map<String, List<BookletsBlockValue>> bookletMap = new HashMap<>();
                Map<String, List<ProductsBlockValue>> productMap = new HashMap<>();
                Map<String, BlockValue> blockValues = blockData.getBlockValues();
                for(BlockDef def : blockDef.getBlockDefs()){
                    String currentKey = def.getKey();
                    BlockValue bv = blockValues.get(currentKey);
                    if(def.type() == BlockType.image){
                        List<ImageBlockValue> imgList = imageMap.get(currentKey);
                        if(imgList == null){
                            imgList = new ArrayList();
                            imageMap.put(currentKey, imgList);
                        }
                        imgList.add((ImageBlockValue)bv);
                    }else if(def.type() == BlockType.slider){
                        List<SliderBlockValue> sliderList = sliderMap.get(currentKey);
                        if(sliderList == null){
                            sliderList = new ArrayList<>();
                            sliderMap.put(currentKey, sliderList);
                        }
                        sliderList.add((SliderBlockValue)bv);
                    }else if(def.type() == BlockType.text){
                        List<TextBlockValue> textList = textMap.get(currentKey);
                        if(textList == null){
                            textList = new ArrayList<>();
                            textMap.put(currentKey, textList);
                        }
                        textList.add((TextBlockValue)bv);
                    }else if(def.type() == BlockType.booklets){
                        List<BookletsBlockValue> bookletList = bookletMap.get(currentKey);
                        if(bookletList == null){
                            bookletList = new ArrayList<>();
                            bookletMap.put(currentKey, bookletList);
                        }
                        bookletList.add((BookletsBlockValue)bv);
                    }else if(def.type() == BlockType.products){
                        List<ProductsBlockValue> productList = productMap.get(currentKey);
                        if(productList == null){
                            productList = new ArrayList<>();
                            productMap.put(currentKey, productList);
                        }
                        productList.add((ProductsBlockValue)bv);
                    }
                }
                
                model.addAttribute("imageMap", imageMap);
                model.addAttribute("textMap", textMap);
                model.addAttribute("sliderMap", sliderMap);
                model.addAttribute("bookletMap", bookletMap);
                model.addAttribute("productMap", productMap);
            }
            model.addAttribute("skeleton", pageSkeleton);
        }
        model.addAttribute("type", type);
        model.addAttribute("key", key);
        model.addAttribute("index", index);
        return "/admin/website/setting/blockAdv";
    }
    
    
    @RequestMapping(value = "/admin/website/skeleton/blockAdv.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse websiteSettingEntry(@RequestParam("key") String key,
                                                                @RequestParam("type") PageBlockType type,
                                                                @RequestParam(value = "image[]", required = false) String[] images,
                                                                @RequestParam(value = "imageLink[]", required = false) String[] imagelinks,
                                                                @RequestParam(value = "entry[]", required = false) String[] entries,
                                                                @RequestParam(value = "text[]", required = false) String[] texts,
                                                                @RequestParam(value = "textLink[]", required = false) String[] textLinks,
                                                                @RequestParam(value="index", defaultValue = "0") int index,
                                                                Model model, HttpSession session)
    {
        PageSkeleton pageSkeleton = themeManager.getPageSkeleton();
        if(pageSkeleton == null){
            throw new PermissionDeniedException();
        }
        
        //初始化数据
        Map<String, List<Object>> imageMap = getSplitDataMap(images);
        Map<String, List<Object>> linkMap  = getSplitDataMap(imagelinks);
        Map<String, List<Object>> entryMap = getSplitDataMap(entries);
        Map<String, List<Object>> textMap = getSplitDataMap(texts);
        Map<String, List<Object>> textLinkMap = getSplitDataMap(textLinks);
        
        if(PageBlockType.header == type){
            PageBlockDef headerDef = themeManager.getThemeDef(PageBlockType.header, key);
            if(headerDef == null){
                return new SimpleJsonResponse(false, "模板不存在或已经被删除,请刷新后重新操作");
            }
            List<BlockDef> blockDefs = headerDef.getBlockDefs();
            SimpleJsonResponse result = initAdvertisementData(blockDefs, imageMap, linkMap, entryMap, textMap, textLinkMap);
            if(!result.isIsOk()){
                return result;
            }
            Map<String, BlockValue> bvMap = (Map<String, BlockValue>)result.getMessage();
            PageSkeleton.PageBlock header = pageSkeleton.new PageBlock();
            header.setBlockKey(key);
            header.setBlockValues(bvMap);
            pageSkeleton.setHeader(header);
        }else if(PageBlockType.block == type){
            PageBlockDef blockDef = themeManager.getThemeDef(PageBlockType.block, key);
            if(blockDef == null){
                return new SimpleJsonResponse(false, "模板不存在或已经被删除,请刷新后重新操作");
            }
            List<BlockDef> blockDefs = blockDef.getBlockDefs();
            SimpleJsonResponse result = initAdvertisementData(blockDefs, imageMap, linkMap, entryMap, textMap, textLinkMap);
            if(!result.isIsOk()){
                return result;
            }
            Map<String, BlockValue> bvMap = (Map<String, BlockValue>)result.getMessage();
            PageSkeleton.PageBlock block  = pageSkeleton.new PageBlock();
            block.setBlockKey(key);
            block.setBlockValues(bvMap);
            List<PageSkeleton.PageBlock> bodyBlocks = pageSkeleton.getBodyBlocks();
            bodyBlocks.add(index, block); //在index处增加一个Block
            bodyBlocks.remove(index + 1); //移除旧的Block
            pageSkeleton.setBodyBlocks(bodyBlocks);
        }else if(PageBlockType.footer == type){
            PageBlockDef headerDef = themeManager.getThemeDef(PageBlockType.footer, key);
            if(headerDef == null){
                return new SimpleJsonResponse(false, "模板不存在或已经被删除,请刷新后重新操作");
            }
            List<BlockDef> blockDefs = headerDef.getBlockDefs();
            SimpleJsonResponse result = initAdvertisementData(blockDefs, imageMap, linkMap, entryMap, textMap, textLinkMap);
            if(!result.isIsOk()){
                return result;
            }
            Map<String, BlockValue> bvMap = (Map<String, BlockValue>)result.getMessage();
            PageSkeleton.PageBlock footer = pageSkeleton.new PageBlock();
            footer.setBlockKey(key);
            footer.setBlockValues(bvMap);
            pageSkeleton.setFooter(footer);
        }
        themeManager.save(pageSkeleton);
        return new SimpleJsonResponse(true, "/admin/website/skeleton.php");
    }
    
    
    @RequestMapping(value = "/admin/website/block/chooseProducts.php", method = RequestMethod.GET)
    public String chooseProducts(@RequestParam(value = "kw", required = false) String keyword,
    							@RequestParam(value = "from", required = false) String from,
                                @RequestParam(value = "brand", required = false) Brand brand,
                                @RequestParam(value = "cate", required = false) ProductCate cate,
                                @RequestParam(value = "minPrice", required = false) Double minPrice,
                                @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                                @RequestParam(value = "maxSize", defaultValue = "1") int maxSize,
                                @RequestParam(value = "minSize", defaultValue = "1") int minSize,
                                @RequestParam(value = "id[]", required = false) Integer[] pids,
                                @RequestParam(value = "enableOverseas", required = false) Boolean enableOverseas,
                                @RequestParam(value = "type", required = false) OwnerType type,
                                @RequestParam(value = "specialId", required = false) Integer specialId, //专题Id
                                @PagerSpecDefaults(pageSize = 10, sort = "time.desc") PagerSpec pager,
                                Model model)
    {
    	StringBuilder link;
    	if(specialId != null){
    		link = new StringBuilder("/jdvop/admin/website/block/loadProducts.php?page=[:page]&specialId="+specialId);
        }else{
        	link = new StringBuilder("/jdvop/admin/website/block/loadProducts.php?page=[:page]");
        }
        
        if(keyword != null && !keyword.trim().isEmpty()){
            link.append("&kw=").append(keyword);
        }
        if(from !=null){
        	link.append("&from=").append(from);
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
        
        Page<Product>   products;
        ProductCriteria criteria = new ProductCriteria().withBrand(brand)
                .withCate(cate).withPrice(minPrice, maxPrice).withKeyword(keyword)
                .withStatus(ProductStatus.selling).withEnableOverseas(enableOverseas).withOwnerType(type);
        //根据专题活动ID 查找数据
        if(specialId != null){
        	SpecialActivity special = specialActivityService.findOne(specialId);
        	if(special.getType().equals(SpecialActivityType.enterpriseSpecial)){ //企业专题
        		//根据专题活动ID 查找专题下的企业商品池数据
        		List<Integer> productIds = enterpriseExclusiveProductPoolEntryService.findProductIds(special.getProductPoolId());
        		if(!productIds.isEmpty()){
        			criteria = criteria.withProductIds(productIds);
        			products = productService.find(criteria, pager);
        		} else{
        			products = null; 
        		}
        	} else {
        		products = productService.find(criteria, pager);
        	}
        } else {
        	products = productService.find(criteria, pager);
        }
     
        
       
        if(pids != null && pids.length > 0){
            List<Product> selectedProducts = productService.find(Arrays.asList(pids));
            model.addAttribute("selectedProducts", selectedProducts);
        }
        model.addAttribute("from", from);
        model.addAttribute("products", products);
        model.addAttribute("brands", brandService.findProductBrands(null, ProductStatus.selling, new BrandCriteria().withKeyword(keyword).withDeleted(false).withRequireImage(false), null));
        model.addAttribute("cates", productCateService.rootCates());
        model.addAttribute("link", link.toString());
        model.addAttribute("maxSize", maxSize);
        model.addAttribute("minSize", minSize);
        model.addAttribute("ownerTypes", OwnerType.values());
        model.addAttribute("specialId", specialId);
        return "/admin/website/setting/choose_product";
    }
    
    @RequestMapping(value = "/admin/website/block/loadProducts.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public  @ResponseBody  Map<String, String>  loadProducts(@RequestParam(value = "kw", required = false) String keyword,
                                                            @RequestParam(value = "brand", required = false) Brand brand,
                                                            @RequestParam(value = "cate", required = false) ProductCate cate,
                                                            @RequestParam(value = "minPrice", required = false) Double minPrice,
                                                            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                                                            @RequestParam(value = "type", required = false) OwnerType type,
                                                            @RequestParam(value = "enableOverseas", required = false) Boolean enableOverseas,
                                                            @RequestParam(value = "specialId", required = false) Integer specialId, //专题Id
                                                            @PagerSpecDefaults(pageSize = 10, sort = "time.desc") PagerSpec pager) throws UnsupportedEncodingException 
    {
    	
    	 ProductCriteria criteria = new ProductCriteria().withBrand(brand)
                 .withCate(cate).withPrice(minPrice, maxPrice).withKeyword(keyword)
                 .withStatus(ProductStatus.selling).withEnableOverseas(enableOverseas).withOwnerType(type);
         //根据专题活动ID 查找数据
    	 Page<Product>   products;
    	 if(specialId != null){
         	SpecialActivity special = specialActivityService.findOne(specialId);
         	if(special.getType().equals(SpecialActivityType.enterpriseSpecial)){ //企业专题
         		//根据专题活动ID 查找专题下的企业商品池数据
         		List<Integer> productIds = enterpriseExclusiveProductPoolEntryService.findProductIds(special.getProductPoolId());
         		if(!productIds.isEmpty()){
         			criteria = criteria.withProductIds(productIds);
         			products = productService.find(criteria, pager);
         		} else{
         			products = null; 
         		}
         	} else {
         		products = productService.find(criteria, pager);
         	}
         } else {
         	products = productService.find(criteria, pager);
         }
    	 
        StringBuilder url;
        if(specialId != null){
        	url = new StringBuilder("/jdvop/admin/website/block/loadProducts.php?page=[:page]&specialId="+specialId);
        }else{
           url = new StringBuilder("/jdvop/admin/website/block/loadProducts.php?page=[:page]");
        }
        
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
        if(enableOverseas != null) {
            url.append("&enableOverseas=").append(enableOverseas);
        }
        if (type != null) {
            url.append("&type=").append(type.name());
        }
        Map<String, String> result  = new HashMap<>();
        if(products != null){
        	result.put("contentHtml", renderProduct(products));
            
            String  pageHtml    = PagerUtil.createPagenation(url.toString(), products.getPagerSpec(), 2, "_self");
            result.put("pageHtml", pageHtml);
        }
        
        return result;
    }
    
    @RequestMapping(value = "/admin/website/block/getProducts.php", method = RequestMethod.GET)
    public @ResponseBody SimpleJsonResponse getProducts(@RequestParam(value = "products[]", required = false) Integer[] pids,
                                                        Model model)
    {
        if(pids != null && pids.length > 0){
            List<Product> bpList = productService.find(Arrays.asList(pids));
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
        if(product.getOwnerType() == OwnerType.jingdong || product.getOwnerType() == OwnerType.system) {
            buffer.append("<img id=\"pd_image\" src=\"").append(product.getThumbnailImage()).append("\" width=\"160\" height=\"160\" alt=\"").append(product.getName()).append("\"/>");
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
        buffer.append("<p class=\"text ffa orange\">").append(" 经销价：¥").append(product.getRetailPrice()).append("</p>");
        buffer.append("</a>");
        buffer.append("<span class=\"ico-selected\"></span>");
        if(product.getOwnerType() == OwnerType.enterprise){
            buffer.append("<label class=\"ico_own\"></label>");
        }
        if(product.getOwnerType() == OwnerType.provider){
            buffer.append("<label class=\"ico_gys\"></label>");
        }
        if(product.getOwnerType() == OwnerType.system){
            buffer.append("<label class=\"ico_xt\"></label>");
        }
        
        buffer.append("</li>");
    }
    
    
    @RequestMapping(value = "/admin/website/img/upload.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse<String> uploadImage(@RequestParam(value = "filedData") MultipartFile file,
                                                                @RequestParam(value = "width", defaultValue = "0") int width,
                                                                @RequestParam(value = "height", defaultValue = "0") int  height,
                                                                @RequestParam(value = "size", defaultValue = "0") int size) throws IOException 
    {
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
    
    
    private Map<String, List<Object>> getSplitDataMap(String[] datas){
        if(datas == null || datas.length == 0){
            return new HashMap<>();
        }
        Map<String, List<Object>> imgMap = new HashMap<>();
        for(String item : datas){
            if(item == null || item.trim().isEmpty()){
                continue;
            }
            String data[] = item.split("#");
            if(data.length != 2){
                continue;
            }
            String key      = data[0];
            String dt   = data[1];
            List<Object> result = imgMap.get(key);
            if(result == null){
                result = new ArrayList<>();
                imgMap.put(key, result);
            }
            result.add(dt);
        }
        return imgMap;
    }
    
    private SimpleJsonResponse initAdvertisementData(List<BlockDef> blockDefs,
                                                     Map<String, List<Object>> imageMap, 
                                                     Map<String, List<Object>> linkMap, 
                                                     Map<String, List<Object>> entryMap,
                                                     Map<String, List<Object>> textMap,
                                                     Map<String, List<Object>> textLinkMap)
    {
        Map<String, BlockValue> bvMap = new HashMap<>();
        for(BlockDef def : blockDefs){
            if(BlockType.image == def.type())
            {
                if(imageMap == null || linkMap == null){
                    return new SimpleJsonResponse(false, "数据异常,似乎还有图片没有配置完毕");
                }
                List<Object> ibvUrls  = imageMap.get(def.getKey());
                List<Object> ibvLinks = linkMap.get(def.getKey());
                if(ibvUrls == null){
                    return new SimpleJsonResponse(false, "数据异常,您似乎还没有配置图片");
                }
                if(ibvLinks == null || ibvUrls.size() != ibvLinks.size()){
                    return new SimpleJsonResponse(false, "数据异常,图片个数与图片链接地址不配置");
                }
                ImageBlockValue ibv = new ImageBlockValue();
                ibv.setLinkTo((String)ibvLinks.get(0));
                ibv.setUrl((String)ibvUrls.get(0));
                bvMap.put(def.getKey(), ibv);
            }else if(BlockType.slider == def.type())
            {
                if(imageMap == null || linkMap == null){
                    return new SimpleJsonResponse(false, "数据异常,似乎还有图片没有配置完毕");
                }
                SliderBlockDef sbd = (SliderBlockDef)def;
                List<Object> ibvUrls  = imageMap.get(def.getKey());
                List<Object> ibvLinks = linkMap.get(def.getKey());
                if(sbd.getMinEntries() != 0){
                    if(ibvUrls == null || ibvUrls.size() < sbd.getMinEntries()){
                        return new SimpleJsonResponse(false, "数据异常,图片个数不得小于" + sbd.getMinEntries() + "个");
                    }
                    if(ibvUrls.size() > sbd.getMaxEntries()){
                        return new SimpleJsonResponse(false, "数据异常,图片个数不得大于" + sbd.getMaxEntries()+ "个");
                    }
                }
                if(ibvLinks == null || ibvUrls.size() != ibvLinks.size()){
                    return new SimpleJsonResponse(false, "数据异常,图片个数与图片链接地址不配置");
                }
                SliderBlockValue sbv = new SliderBlockValue();
                List<SliderBlockValue.Entry> ses = new ArrayList<>();
                for(int i = 0; i < ibvUrls.size(); i++){
                    SliderBlockValue.Entry e = sbv.new Entry();
                    e.setImage((String)ibvUrls.get(i));
                    e.setLinkTo((String)ibvLinks.get(i));
                    ses.add(e);
                }
                sbv.setEntries(ses);
                bvMap.put(def.getKey(), sbv);
            }else if(BlockType.products == def.type()){
                if(entryMap == null || entryMap.isEmpty()){
                   return new SimpleJsonResponse(false, "数据异常,您似乎还没有配置产品广告"); 
                }
                List<Object> productIds = entryMap.get(def.getKey());
                ProductsBlockDef pbd = (ProductsBlockDef)def;
                if(pbd.getMinEntries() != 0){
                    if(productIds == null || productIds.size() < pbd.getMinEntries()){
                        return new SimpleJsonResponse(false, "数据异常,产品广告个数不得小于" + pbd.getMaxEntries()+ "个");
                    }
                    if(productIds.size() > pbd.getMaxEntries()){
                        return new SimpleJsonResponse(false, "数据异常,产品广告个数不得大于" + pbd.getMaxEntries()+ "个");
                    }
                }
                ProductsBlockValue pbv = new ProductsBlockValue();
                List<Integer> pids = new ArrayList<>();
                for(Object id : productIds){
                    pids.add(Integer.parseInt((String)id));
                }
                pbv.setValue(pids);
                bvMap.put(def.getKey(), pbv);
            }else if(BlockType.booklets == def.type()){
                if(entryMap == null || entryMap.isEmpty()){
                   return new SimpleJsonResponse(false, "数据异常,您似乎还没有配置产品广告"); 
                }
                List<Object> bookletIds = entryMap.get(def.getKey());
                BookletsBlockDef bbd = (BookletsBlockDef)def;
                if(bbd.getMinEntries() != 0){
                    if(bookletIds == null || bookletIds.size() < bbd.getMinEntries()){
                        return new SimpleJsonResponse(false, "数据异常,礼册广告个数不得小于" + bbd.getMaxEntries()+ "个");
                    }
                    if(bookletIds.size() > bbd.getMaxEntries()){
                        return new SimpleJsonResponse(false, "数据异常,礼册广告个数不得大于" + bbd.getMaxEntries()+ "个");
                    }
                }
                BookletsBlockValue bbv = new BookletsBlockValue();
                List<Integer> bids = new ArrayList<>();
                for(Object id : bookletIds){
                    bids.add(Integer.parseInt((String)id));
                }
                bbv.setValue(bids);
                bvMap.put(def.getKey(), bbv);
            }else{
                //text
                if(textMap == null || textMap.isEmpty()){
                    return new SimpleJsonResponse(false, "数据异常,您似乎还没有配置标题文字"); 
                }
                List<Object> titles = textMap.get(def.getKey());
                List<Object> links  = textLinkMap.get(def.getKey());
                TextBlockDef tbd = (TextBlockDef)def;
                if(titles == null){
                    return new SimpleJsonResponse(false, "数据异常,您似乎还没有配置标题文字");
                }
                if(!tbd.isNoLink()){
                   if(links == null || links.isEmpty()){
                       return new SimpleJsonResponse(false, "数据异常,您似乎还没有配置标题链接");
                   } 
                }
                TextBlockValue tbv = new TextBlockValue();
                if(tbd.isNoLink()){
                    tbv.setLinkTo("#");
                }else{
                    tbv.setLinkTo((String)links.get(0));
                }
                tbv.setText((String)titles.get(0));
                bvMap.put(def.getKey(), tbv);
            }
        }
        return new SimpleJsonResponse(true, bvMap);
    }
}
