/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.controller;

import com.lmf.activity.entity.Lottery;
import com.lmf.activity.service.LotteryService;
import com.lmf.common.Page;
import com.lmf.common.SerializableMultipartFile;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.exceptions.ResourceNotFoundException;
import com.lmf.common.util.SimpleImageInfo;
import com.lmf.enums.ProductStatus;
import com.lmf.product.entity.Brand;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.ProductCate;
import com.lmf.product.service.BrandService;
import com.lmf.product.service.ProductCateService;
import com.lmf.product.service.ProductService;
import com.lmf.product.vo.BrandCriteria;
import com.lmf.product.vo.ProductCriteria;
import com.lmf.sys.service.InternalAttachmentService;
import com.lmf.sys.service.ShipmentCompanyService;
import com.lmf.website.entity.NavigationItem;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteAttr;
import com.lmf.website.enums.NavigationType;
import com.lmf.website.service.IntegralService;
import com.lmf.website.service.WebsiteService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class WebsiteController {

    @Autowired
    private WebsiteService websiteService;

    @Autowired
    private ProductCateService productCateService;

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private InternalAttachmentService internalAttachmentService;

    @Autowired
    private ShipmentCompanyService shipmentCompanyService;

    @Autowired
    private LotteryService lotteryService;

    @RequiresPermissions("system:edit")
    @RequestMapping(value = "/admin/website.php", method = RequestMethod.GET)
    public String website(@RequestParam(value = "isHelp", required = false) Boolean isHelp, Website website, Model model) {
        model.addAttribute("website", website);
        model.addAttribute("shimentCompanys", shipmentCompanyService.all());
        return "/admin/website";
    }
    
    @RequiresPermissions("system:edit")
    @RequestMapping(value = "/admin/website.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse website(@RequestParam("name") String name,
            @RequestParam(value = "icpNo", required = false) String icpNo,
            @RequestParam(value = "customerServiceQQ", required = false) String customerServiceQQ,
            @RequestParam(value = "customerServiceHotLine", required = false) String customerServiceHotLine,
            @RequestParam(value = "shipmentCompanyId", required = false) Integer shipmentCompanyId,
            @RequestParam(value = "seoKeyword", required = false) String seoKeyword,
            @RequestParam(value = "seoDescription", required = false) String seoDescription,
            @RequestParam(value = "ratio", required = false) Integer ratio,
            @RequestParam(value = "logo", required = false) String logo,
            @RequestParam(value = "adminLogo", required = false) String adminLogo,
            @RequestParam(value = "isDefinedCate", required = false) boolean isDefinedCate,
            @RequestParam(value = "isForceWechatAuthorize", required = false) boolean isForceWechatAuthorize,
            @RequestParam(value = "appId", required = false) String appId,
            @RequestParam(value = "appSecret", required = false) String appSecret,
            Website website) {
        if (name == null || name.trim().isEmpty()) {
            return new SimpleJsonResponse(false, "请填写网站名称");
        }
        website.setName(name);
        website.setIcpNo(icpNo);
        website.setCustomerServiceHotLine(customerServiceHotLine);

        if (ratio != null && ratio > 0) {
            website.setRatio(ratio);
        }

        if (customerServiceQQ != null && !customerServiceQQ.trim().isEmpty()) {
            String[] qqs = customerServiceQQ.split(",|，");
            website.setCustomerServiceQQ(qqs);
        } else {
            website.setCustomerServiceQQ(null);
        }

        if (!logo.isEmpty()) {
            website.setLogo(logo);
        } else {
            website.setLogo(null);
        }

        if (shipmentCompanyId != null && shipmentCompanyId > 0) {
            website.setShipmentCompanyId(shipmentCompanyId);
        }
        Map<String, Object> ext = website.getExt();
        if (ext == null) {
            ext = new HashMap<>();
        } else {
            ext = new HashMap<>(website.getExt());
        }
        if (adminLogo != null) {
            ext.put("ADMIN_LOGO", adminLogo);
        }

        if (isForceWechatAuthorize) {
            if (appId == null || appId.trim().isEmpty()) {
                return new SimpleJsonResponse<>(false, "请填写微信appId");
            }
            if (appSecret != null && appSecret.trim().isEmpty()) {
                return new SimpleJsonResponse<>(false, "请填写微信appSecret");
            }
            ext.put("WechatCfg.appId", appId);
            ext.put("WechatCfg.appSecret", appSecret);
        }
        ext.put("forceWechatAuthorize", isForceWechatAuthorize);
        website.setExt(ext);

        ext.put("ENABLE_WEBSITE_PRODUCTTAG", isDefinedCate);
        website.setExt(ext);

        websiteService.save(website);
        return new SimpleJsonResponse(true, "数据保存成功");
    }

    @RequestMapping(value = "/admin/navigation.php", method = RequestMethod.GET)
    public String navigation(Website website, Model model) {

        model.addAttribute("types", NavigationType.values());
        model.addAttribute("navigations", websiteService.getWebsiteNavigations());
        model.addAttribute("productService", productService);
        model.addAttribute("productCateService", productCateService);

        model.addAttribute("rootCates", productCateService.rootCates());

        Page<Brand> brands = brandService.findProductBrands(null, ProductStatus.selling, new BrandCriteria().withDeleted(false).withRequireImage(false), null);
        model.addAttribute("brands", brands);

        model.addAttribute("lotterys", lotteryService.find(null, null, null));

        return "/admin/navigation";
    }

    @RequestMapping(value = "/admin/navigation.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse navigation(@RequestParam("name[]") String[] name,
            @RequestParam("linkPage[]") NavigationType[] types,
            @RequestParam(value = "value[]", required = false) String[] value,
            @RequestParam(value = "cate[]", required = false) ProductCate[] cates,
            Website website,
            Model model) {
        if ((name == null || name.length == 0) && (types == null || types.length == 0 || types[0] == null) && (value == null || value.length == 0)) {
            websiteService.saveWebsiteNavigations(null);
            return new SimpleJsonResponse(true, null);
        }
        if (name == null || types == null || value == null) {
            return new SimpleJsonResponse(false, "您存在未填写相关数据的导航栏，请补充！");
        }

        if (name.length != types.length || types.length != value.length) {
            return new SimpleJsonResponse(false, "您存在未填写相关数据的导航栏，请补充！");
        }
        NavigationItem[] items = new NavigationItem[name.length];
        for (short i = 0; i < name.length; i++) {
            NavigationItem item = new NavigationItem();
            if (name[i] == null) {
                return new SimpleJsonResponse(false, "请填写栏目名称");
            }
            if (name[i].trim().isEmpty() || name[i].trim().equals("")) {
                return new SimpleJsonResponse(false, "请填写栏目名称");
            }
            if (types[i] == null) {
                return new SimpleJsonResponse(false, "请选择链接页面");
            }
            item.setName(name[i]);
            item.setType(types[i]);
            switch (types[i]) {
                case product:
                case brandProducts:
                case categoryProducts:
                case column:
                    if (value[i] == null || "".equals(value[i])) {
                        return new SimpleJsonResponse(false, "您存在未填写相关数据的导航栏，请补充！");
                    }
                    Integer id = Integer.parseInt(value[i]);
                    if (types[i] == NavigationType.product) {
                        Product product = productService.findOne(id);
                        if (product == null) {
                            return new SimpleJsonResponse(false, "数据错误,产品不存在");
                        }
                    } else if (types[i] == NavigationType.brandProducts) {
                        if (id < 1) {
                            return new SimpleJsonResponse(false, "请选择品牌！");
                        }
                        Brand brand = brandService.findOne(id);
                        if (brand == null) {
                            return new SimpleJsonResponse(false, "数据错误，品牌不存在");
                        }
                    } else if (types[i] == NavigationType.categoryProducts) {
                        ProductCate cate = productCateService.findOne(id);
                        if (cate == null) {
                            return new SimpleJsonResponse(false, "数据错误，分类不存在");
                        }
                    }
                    item.setTarget(id);
                    break;
                case page:
                    item.setTarget(Long.parseLong(value[i]));
                    break;
                case link:
                    if (value[i] == null || "".equals(value[i])) {
                        return new SimpleJsonResponse(false, "您存在未填写相关数据的导航栏，请补充！");
                    }
                    item.setTarget(value[i]);
                    break;
                case lottery:
                    if (value[i] == null || value[i].trim().isEmpty() || " ".equals(value[i])) {
                        return new SimpleJsonResponse(false, "请选择活动！");
                    }
                    Integer lotteryId = Integer.parseInt(value[i]);
                    Lottery lottery = lotteryService.findOne(lotteryId);
                    if (lottery == null) {
                        return new SimpleJsonResponse(false, "数据错误，活动不存在");
                    }
                    item.setTarget(lotteryId);
                    break;
            }
            items[i] = item;
        }
        websiteService.saveWebsiteNavigations(items);

        return new SimpleJsonResponse(true, null);
    }

    @RequestMapping(value = "/admin/cate/ajax.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public @ResponseBody Set<ProductCate> childs(@RequestParam("parentID") int parentID) {
        Set<ProductCate> childs = productCateService.findOne(parentID).getChilds();
        if (childs == null) {
            childs = Collections.EMPTY_SET;
        }
        return childs;
    }

    @RequestMapping(value = "/admin/ajax/searchProduct.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public @ResponseBody List<Map<String, Object>> ajaxSearchProduct(@RequestParam(value = "kw", required = false) String keyword,
            @RequestParam(value = "b", required = false) Brand brand,
            @RequestParam(value = "cate", required = false) ProductCate cate,
            Website website) {

        Page<Product> products = productService.find(new ProductCriteria().withKeyword(keyword).withBrand(brand).withCate(cate).withStatus(ProductStatus.selling).withDeleted(Boolean.FALSE), null);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Product bp : products) {
            Map<String, Object> t = new HashMap<>();
            t.put("id", bp.getId());
            t.put("name", bp.getName());
            result.add(t);
        }

        return result;

    }

    @RequestMapping(value = "/admin/website/uploadLogo.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse<String> uploadLogo(@RequestParam(value = "Filedata") MultipartFile file) throws IOException {
        SerializableMultipartFile smf;
        try {
            smf = new SerializableMultipartFile(file);
            SimpleImageInfo sii = new SimpleImageInfo(smf.getBytes());
            if (!(sii.getWidth() == 360 && sii.getHeight() == 80)) {
                return new SimpleJsonResponse(false, "您上传的图片文件尺寸不合法，必须是 360 * 80 像素的");
            }
        } catch (IOException e) {
            return new SimpleJsonResponse(false, "您上传的图片文件不合法，请重新上传");
        }
        String path = internalAttachmentService.save(smf);
        return new SimpleJsonResponse(true, path);
    }

    @RequestMapping(value = "/admin/website/uploadAdminLogo.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse<String> uploadAdminLogo(@RequestParam(value = "Filedata") MultipartFile file) throws IOException {
        SerializableMultipartFile smf;
        try {
            smf = new SerializableMultipartFile(file);
            SimpleImageInfo sii = new SimpleImageInfo(smf.getBytes());
            if (!(sii.getWidth() == 160 && sii.getHeight() == 60)) {
                return new SimpleJsonResponse(false, "您上传的图片文件尺寸不合法，必须是 160 * 60 像素的");
            }
        } catch (IOException e) {
            return new SimpleJsonResponse(false, "您上传的图片文件不合法，请重新上传");
        }
        String path = internalAttachmentService.save(smf);
        return new SimpleJsonResponse(true, path);
    }

    @RequestMapping(value = "/admin/websiteAttrEdit.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String edithelp(@RequestParam("key") String key,
            Website website, Model model) {
        if (website == null) {
            throw new ResourceNotFoundException();
        }
        WebsiteAttr websiteAttr = websiteService.findWebsiteAttr(key);
        model.addAttribute("websiteAttr", websiteAttr);
        model.addAttribute("key", key);
        return "admin/website_attr";
    }

    @RequestMapping(value = "/admin/websiteAttrEdit.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse edithelp(@RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "content", required = false) String content,
            Website website, Model model) {
        if (website == null) {
            return new SimpleJsonResponse(false, "website数据丢失，请重新登录！");
        }
        if (key == null || key.isEmpty()) {
            return new SimpleJsonResponse(false, "您未选中要编辑的页面，请刷新重试！");
        }
        WebsiteAttr websiteAttr = websiteService.findWebsiteAttr(key);
        if (websiteAttr == null) {
            websiteAttr = new WebsiteAttr();
            websiteAttr.setKey(key);
        }
        websiteAttr.setValue(content);
        websiteService.saveAttr(websiteAttr);

        return new SimpleJsonResponse(true, "保存成功！");
    }

}
