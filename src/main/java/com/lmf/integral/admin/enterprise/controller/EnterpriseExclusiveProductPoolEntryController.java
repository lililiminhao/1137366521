package com.lmf.integral.admin.enterprise.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
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

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.enums.OwnerType;
import com.lmf.common.util.LMFMathUtils;
import com.lmf.common.util.PagerSpec;
import com.lmf.common.util.PagerUtil;
import com.lmf.enterprise.entity.EnterpriseExclusiveProductPool;
import com.lmf.enterprise.entity.EnterpriseExclusiveProductPoolEntry;
import com.lmf.enterprise.service.EnterpriseExclusiveProductPoolEntryService;
import com.lmf.enterprise.service.EnterpriseExclusiveProductPoolService;
import com.lmf.enterprise.vo.EnterpriseExclusiveProductPoolEntryCriteria;
import com.lmf.enterprise.vo.EnterpriseExclusiveProductPoolEntrySelectCriteria;
import com.lmf.enterprise.vo.EnterpriseExclusiveProductPoolEntryVo;
import com.lmf.enums.ProductStatus;
import com.lmf.excel.util.ExcelUtils;
import com.lmf.excel.util.model.ExcelExportParam;
import com.lmf.excel.util.model.ExcelImportParam;
import com.lmf.integral.SystemConfig;
import com.lmf.integral.admin.excel.ExcelUtil;
import com.lmf.integral.admin.excel.ExportEnterpriseExclusiveProductPoolEntryEnum;
import com.lmf.integral.admin.excel.ImportEnterpriseExclusiveProductPoolEntryEnum;
import com.lmf.product.entity.Brand;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.ProductCate;
import com.lmf.product.service.BrandService;
import com.lmf.product.service.ProductCateService;
import com.lmf.product.service.ProductService;
import com.lmf.product.vo.ProductCriteria;

/**
 * 企业专享产品池产品
 * @author shenzhixiong
 *
 */
@Controller("enterpriseExclusiveProductPoolEntryController")
public class EnterpriseExclusiveProductPoolEntryController {

	@Autowired
	private EnterpriseExclusiveProductPoolService productPoolService;
	
	@Autowired
	private ProductCateService productCateService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private SystemConfig systemConfig;
	
	private Logger logger = LoggerFactory.getLogger(EnterpriseExclusiveProductPoolEntryController.class);
	
	@RequiresPermissions("enterprise_exclusive:view_product")
	@RequestMapping(value = "/admin/enterprise/pool/productEntries.php", method = RequestMethod.GET)
	public String list(@RequestParam("poolId") Integer poolId, 
					   @RequestParam(value = "kwd", required = false) String kwd,
					   @RequestParam(value = "cate", required = false) ProductCate cate,
					   @PagerSpecDefaults(pageSize = 20, sort = "id.desc") PagerSpec pagerSpec,
					   Model model) throws UnsupportedEncodingException {
		
		EnterpriseExclusiveProductPool productPool = productPoolService.selectOne(poolId);
		if(productPool == null) {
			throw new RuntimeException("productPool is null");
		}
		
		StringBuilder link = new StringBuilder("/jdvop/admin/enterprise/pool/productEntries.php?page=[:page]&poolId=").append(poolId);
		EnterpriseExclusiveProductPoolEntrySelectCriteria  criteria = new EnterpriseExclusiveProductPoolEntrySelectCriteria();
		if(kwd != null && !kwd.trim().isEmpty()) {
            link.append("&kwd=").append(URLEncoder.encode(kwd, "UTF-8"));
            criteria.withKeyword(kwd);
            model.addAttribute("kwd", kwd);
		}
		
		if(cate != null) {
			link.append("&cate=").append(cate.getId());
            criteria.withProductCate(cate);
            model.addAttribute("cate", cate);
		}
		
		Page<EnterpriseExclusiveProductPoolEntryVo> productPoolEntryies =  productPoolService.findByProductPool(productPool, criteria, pagerSpec);
		model.addAttribute("entries", productPoolEntryies);
		
		model.addAttribute("link", link.toString());
		model.addAttribute("productPool", productPool);
		model.addAttribute("cates", productCateService.rootCates());
		 
		return "admin/enterprise/enterprise_exclusive_product_pool/entries/list";
	}
	
	@RequestMapping(value = "/admin/enterprise/pool/productEntry/batchAdd.php", method = RequestMethod.GET) 
	public String batchAdd(@RequestParam("poolId") Integer poolId,
						   @RequestParam(value = "kwd", required = false) String kwd,
						   @RequestParam(value = "c", required = false) ProductCate cate,
						   @RequestParam(value = "minPrice", required = false) Double minPrice,
	                       @RequestParam(value = "maxPrice", required = false) Double maxPrice,
	                       @RequestParam(value = "enableOverseas", required = false)Boolean enableOverseas,
	                       @RequestParam(value = "ownerType", required = false) OwnerType ownerType,
	                       @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager,
					       Model model) throws UnsupportedEncodingException {
				
		
		EnterpriseExclusiveProductPool productPool = productPoolService.selectOne(poolId);
		if(productPool == null) {
			throw new RuntimeException("productPool is null");
		}
		
		Page<Product> products = productService.find(new ProductCriteria().withKeyword(kwd)
				.withCate(cate).withPrice(minPrice, maxPrice).withDeleted(Boolean.FALSE)
                .withStatus(ProductStatus.selling).withEnableOverseas(enableOverseas).withOwnerType(ownerType), pager);
		
		StringBuilder link = new StringBuilder("/jdvop/admin/enterprise/pool/productEntry/add.php?page=[:page]&poolId=").append(poolId);
		ProductCriteria  criteria = new ProductCriteria();
		if(kwd != null && kwd.trim().isEmpty()) {
            link.append("&kwd=").append(URLEncoder.encode(kwd, "UTF-8"));
            criteria.withKeyword(kwd);
            model.addAttribute("kwd", kwd);
		}
		
		if (minPrice != null) {
            link.append("&minPrice=").append(minPrice);
            model.addAttribute("minPrice", minPrice);
        }
        if (maxPrice != null) {
            link.append("&maxPrice=").append(maxPrice);
            model.addAttribute("maxPrice", maxPrice);
        }
        if (enableOverseas != null) {
            link.append("&enableOverseas=").append(enableOverseas);
            model.addAttribute("enableOverseas", enableOverseas);
        }
        if (ownerType != null) {
            link.append("&ownerType=").append(ownerType.name());
            model.addAttribute("ownerType", ownerType.name());
        }
        
        List<Integer> productIds = productPoolService.findProductIdByPoolId(productPool.getId());
        if(productIds != null && !productIds.isEmpty()) {
            model.addAttribute("productIds", productIds);
        }
		
		model.addAttribute("link", link.toString());
		model.addAttribute("cates", productCateService.rootCates());
		model.addAttribute("ownerTypes", OwnerType.values());
		model.addAttribute("productPool", productPool);
		model.addAttribute("products", products);
		return "admin/enterprise/enterprise_exclusive_product_pool/entries/add";
	}
	
	@RequestMapping(value = "/admin/enterprise/pool/productEntry/batchAdd.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse batchAdd(@RequestParam(value = "poolId") int poolId,
	                                                 @RequestParam(value = "pid[]") Integer[] pids,
	                                                 Model model) {
        if(pids == null || pids.length <= 0){
            return new SimpleJsonResponse(false, "您没有选择任何产品数据");
        }
        EnterpriseExclusiveProductPool productPool = productPoolService.selectOne(poolId);
		if(productPool == null) {
			throw new RuntimeException("productPool is null");
		}
        Map<Integer, Product> bpMap = productService.findAsMap(Arrays.asList(pids));
        Map<Integer, EnterpriseExclusiveProductPoolEntry> entryMap = productPoolService.findAsMap(productPool.getId());
        List<EnterpriseExclusiveProductPoolEntry> poeList = new ArrayList<>();
        EnterpriseExclusiveProductPoolEntry entry;
        for(Map.Entry<Integer, Product> item : bpMap.entrySet()){
        	if(entryMap.containsKey(item.getKey())) {
        		entry = entryMap.get(item.getKey());
        	} else {
        		Product product = item.getValue();
        		entry = new EnterpriseExclusiveProductPoolEntry();
        		entry.setProductPoolId(productPool.getId());
            	entry.setProductId(item.getKey());
            	entry.setExclusivePrice(product.getRetailPrice());
            	entry.setExclusiveBillingPrice(product.getRetailPrice());
            	entry.setExclusiveRatio(product.getServiceChargeRatio());
        	}
        	if(entry != null) {
        		poeList.add(entry);
        	}
        }
        
        productPoolService.saveEntry(productPool, poeList);
        return new SimpleJsonResponse(true, "数据保存成功");
    }
	
	@RequiresPermissions("enterprise_exclusive:edit_product")
	@RequestMapping(value = "/admin/enterprise/pool/productEntry/edit.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse edit(@RequestParam(value = "id") int id,
                                                 @RequestParam(value = "exclusivePrice") double exclusivePrice,
                                                 @RequestParam(value = "exclusiveBillingPrice") double exclusiveBillingPrice,
                                                 @RequestParam(value = "exclusiveRatio") double exclusiveRatio,
                                                 Model model) {
		EnterpriseExclusiveProductPoolEntry entry = productPoolService.findEntry(id);
		if(entry == null) {
			return new SimpleJsonResponse<>(false, "网络延时，请刷新后重新尝试");
		}
		if(exclusivePrice < 0.01 || exclusivePrice > 99999999) {
			return new SimpleJsonResponse<>(false, "请输入正确的专享价！");
		}
		if(exclusiveBillingPrice < 0.01 || exclusiveBillingPrice > 99999999) {
			return new SimpleJsonResponse<>(false, "请输入正确的专享开单价！");
		}
		if(exclusiveRatio < 0 || exclusiveRatio >= 100) {
			return new SimpleJsonResponse<>(false, "请输入正确的专享扣点！");
		}
		
		entry.setExclusivePrice(LMFMathUtils.round(exclusivePrice, 2));
		entry.setExclusiveBillingPrice(LMFMathUtils.round(exclusiveBillingPrice, 2));
		entry.setExclusiveRatio(LMFMathUtils.round(exclusiveRatio, 2));
		
        productPoolService.saveEntry(entry);
        return new SimpleJsonResponse(true, "数据保存成功");
    }
	
	@RequiresPermissions("enterprise_exclusive:import_product")
	@RequestMapping(value = "/admin/enterprise/pool/productEntry/batchImport.php", method = RequestMethod.GET)
	public String batchImport(@RequestParam(value = "poolId") int poolId, Model model) {
		
		EnterpriseExclusiveProductPool productPool = productPoolService.selectOne(poolId);
		if(productPool == null) {
			throw new RuntimeException("productPool is null");
		}
		
		model.addAttribute("productPool", productPool);
		
		return "admin/enterprise/enterprise_exclusive_product_pool/entries/batch_import";
	}
	
	@RequiresPermissions("enterprise_exclusive:import_product")
	@RequestMapping(value = "/admin/enterprise/pool/productEntry/batchImport.php", method = RequestMethod.POST)
	public @ResponseBody SimpleJsonResponse batchImport(@RequestParam(value = "poolId") int poolId,
							  							@RequestParam(value = "excelFile") MultipartFile file) {
		
		EnterpriseExclusiveProductPool productPool = productPoolService.selectOne(poolId);
		if(productPool == null) {
			throw new RuntimeException("productPool is null");
		}
		
		Map<String, Object> dataMap = new HashMap<>();
        try (InputStream ins = file.getInputStream()) {
            ExcelImportParam importParam = new ExcelImportParam(ins, ImportEnterpriseExclusiveProductPoolEntryEnum.class, EnterpriseExclusiveProductPoolEntryVo.class);
            List<EnterpriseExclusiveProductPoolEntryVo> importProductPoolEntryList = ExcelUtils.Instance.INSTANCE.importExcel(importParam);
            if (importProductPoolEntryList != null && importProductPoolEntryList.size() > 0) {
            	int totalCount = importProductPoolEntryList.size();
                int[] productIds = importProductPoolEntryList.stream().mapToInt(p -> p.getProductId()).toArray();
                List<Integer> productIdList = new ArrayList<>();
                if (productIds.length < 1) {
                	return new SimpleJsonResponse<>(false, "导入Excel格式错误,请检查！");
                }
                List<EnterpriseExclusiveProductPoolEntryVo> errorList = new ArrayList<>();
                double []  exclusivePrices = importProductPoolEntryList.stream().mapToDouble(p -> p.getExclusivePrice()).toArray();
                double []  exclusiveBillingPrices = importProductPoolEntryList.stream().mapToDouble(p -> p.getExclusiveBillingPrice()).toArray();
                double []  exclusiveRatios = importProductPoolEntryList.stream().mapToDouble(p -> p.getExclusiveRatio()).toArray();
                for(int i = 0; i < exclusivePrices.length; i++) {
                	if(exclusivePrices[i] < 0.01 || exclusivePrices [i] > 99999999 ||
            			exclusiveBillingPrices[i] < 0.01 || exclusiveBillingPrices [i] > 99999999 ||
            			exclusiveRatios[i] < 0 || exclusiveRatios [i] >= 100) {
                		errorList.add(importProductPoolEntryList.get(i));
                		continue;
                	} 
                	productIdList.add(productIds[i]);
                }
                
                for(EnterpriseExclusiveProductPoolEntryVo entryVo : errorList) {
                	if(importProductPoolEntryList.contains(entryVo)) {
                		importProductPoolEntryList.remove(entryVo);
                	}
                }
                if(importProductPoolEntryList.size() < 1) {
                	return new SimpleJsonResponse<>(false, "导入Excel失败，无正确数据导入！");
                }
                List<EnterpriseExclusiveProductPoolEntry> notFoundProductList = new ArrayList<>();
                Map<String, Object> resultMap = productPoolService.batchImportEntries(productPool, productIdList, importProductPoolEntryList);
                List<Integer> notFoundList = (List<Integer>) resultMap.get("notFoundList");
                for (Integer productId : notFoundList) {
                    notFoundProductList.add(importProductPoolEntryList.stream().filter(p->p.getProductId().equals(productId)).findFirst().get());
                }
                dataMap.put("notFoundProductList", notFoundProductList);
            	dataMap.put("errorList",errorList);
                dataMap.put("totalCount", totalCount);
                dataMap.put("failCount", Integer.parseInt(resultMap.get("failCount").toString()) + errorList.size());
                dataMap.put("successCount", resultMap.get("successCount"));
                return new SimpleJsonResponse<>(true, dataMap);
            } else {
                return new SimpleJsonResponse<>(false, "导入Excel格式错误,请检查！");
            }
        } catch (Throwable t) {
        	t.printStackTrace();
            return new SimpleJsonResponse<>(false, "导入Excel格式错误,请检查！");
        }
	}
	
	@RequestMapping(value = "/admin/enterprise/pool/productEntry/export.php", method = RequestMethod.GET)
	public @ResponseBody void exportAsExcel(@RequestParam(value = "poolId") int poolId, HttpServletResponse response) throws IOException {
		
		EnterpriseExclusiveProductPool productPool = productPoolService.selectOne(poolId);
		if(productPool == null) {
			throw new RuntimeException("productPool is null");
		}
        String title = productPool.getName() + "-专享池产品";
        ExcelUtil.setExportExcelHeader(response, title, ".xls", null, null);
        
        Page<EnterpriseExclusiveProductPoolEntryVo> entryVos = productPoolService.findByProductPool(productPool, null, null);
        
        try (OutputStream out = response.getOutputStream()) {
            ExcelExportParam excelParam = new ExcelExportParam("专享池产品", entryVos.getContent(), out
                , EnterpriseExclusiveProductPoolEntryVo.class, ExportEnterpriseExclusiveProductPoolEntryEnum.class);
            ExcelUtils.Instance.INSTANCE.exportExcel(excelParam);
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/admin/enterprise/pool/productEntry/exportProducts.php", method = RequestMethod.GET)
	public @ResponseBody void exportAsExcel(@RequestParam(value = "poolId") int poolId, @RequestParam(value = "ownerType", required = false) OwnerType ownerType, HttpServletResponse response) throws IOException {
		
		EnterpriseExclusiveProductPool productPool = productPoolService.selectOne(poolId);
		if(productPool == null) {
			throw new RuntimeException("productPool is null");
		}
		
		Map<Integer, EnterpriseExclusiveProductPoolEntry> entryMap = productPoolService.findAsMap(poolId);
		
        String title = "导出专享池产品资料";
        ExcelUtil.setExportExcelHeader(response, title, ".xls", null, null);
        
        ProductCriteria criteria = new ProductCriteria().withDeleted(Boolean.FALSE).withStatus(ProductStatus.selling);
        Page<Product> products = productService.find(ownerType, null, criteria, null);
        List<EnterpriseExclusiveProductPoolEntryVo> exportntryVos = new ArrayList<>();
        EnterpriseExclusiveProductPoolEntryVo entryVo;
        for(Product product : products) {
        	entryVo = new EnterpriseExclusiveProductPoolEntryVo();
        	entryVo.setProductId(product.getId());
        	entryVo.setProductName(product.getName());
        	entryVo.setProductCode(product.getProductCode());
        	entryVo.setOwnerType(product.getOwnerType());
        	entryVo.setBrandName(product.getBrand().getName());
        	entryVo.setRetailPrice(product.getRetailPrice());
        	entryVo.setSystemPrice(product.getSystemPrice());
        	entryVo.setServiceChargeRatio(product.getServiceChargeRatio());
        	if(entryMap.get(product.getId()) != null) {
        		EnterpriseExclusiveProductPoolEntry entry = entryMap.get(product.getId());
        		entryVo.setExclusivePrice(entry.getExclusivePrice());
        		entryVo.setExclusiveBillingPrice(entry.getExclusiveBillingPrice());
        		entryVo.setExclusiveRatio(entry.getExclusiveRatio());
        	} 
        	exportntryVos.add(entryVo);
        }
        
        try (OutputStream out = response.getOutputStream()) {
            ExcelExportParam excelParam = new ExcelExportParam("专享池产品", exportntryVos, out
                , EnterpriseExclusiveProductPoolEntryVo.class, ExportEnterpriseExclusiveProductPoolEntryEnum.class);
            ExcelUtils.Instance.INSTANCE.exportExcel(excelParam);
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//专享池选择产品页面
    @RequestMapping(value = "/admin/enterpriseExclusiveProductPoolEntry/choose.php", method = RequestMethod.GET)
    public String choose(@RequestParam(value = "id") Integer productPoolId,
                         @RequestParam(value = "kw", required = false) String keyword,
                         @RequestParam(value = "b", required = false) Brand brand,
                         @RequestParam(value = "c", required = false) ProductCate cate,
                         @RequestParam(value = "minPrice", required = false) Double minPrice,
                         @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                         @RequestParam(value = "enableOverseas", required = false)Boolean enableOverseas,
                         @RequestParam(value = "ownerType", required = false) OwnerType ownerType,
                         @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager,
                         Model model) throws UnsupportedEncodingException{
        Page<Product> products = productService.find(new ProductCriteria().withKeyword(keyword)
                .withBrand(brand).withCate(cate).withPrice(minPrice, maxPrice).withDeleted(Boolean.FALSE)
                .withStatus(ProductStatus.selling).withEnableOverseas(enableOverseas).withOwnerType(ownerType), pager);

        StringBuilder link = new StringBuilder("/jdvop/admin/enterpriseExclusiveProductPoolEntry/ajaxChoose.php?page=[:page]&id=").append(productPoolId);
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
        if (ownerType != null) {
            link.append("&ownerType=").append(ownerType.name());
        }
        List<Integer> productIds = productPoolService.findProductIdByPoolId(productPoolId);
        if(productIds != null && !productIds.isEmpty()) {
            model.addAttribute("productIds", productIds);
        }
        model.addAttribute("products", products);
        model.addAttribute("brands", brandService.findAll());
        model.addAttribute("cate", cate);
        model.addAttribute("link", link.toString());
        model.addAttribute("productCateService", productCateService);
        model.addAttribute("productPoolId", productPoolId);
        model.addAttribute("ownerTypes", OwnerType.values());
        return "/admin/enterprise/enterprise_exclusive_product_pool/entries/products";
    }

    //专享池选择产品异步请求产品
    @RequestMapping(value = "/admin/enterpriseExclusiveProductPoolEntry/ajaxChoose.php", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> ajaxChoose(@RequestParam(value = "id") int id,
					                                    @RequestParam(value = "kw", required = false) String keyword,
					                                    @RequestParam(value = "b", required = false) Brand brand,
					                                    @RequestParam(value = "c", required = false) ProductCate cate,
					                                    @RequestParam(value = "minPrice", required = false) Double minPrice,
					                                    @RequestParam(value = "maxPrice", required = false) Double maxPrice,
					                                    @RequestParam(value = "enableOverseas", required = false)Boolean enableOverseas,
					                                    @RequestParam(value = "ownerType", required = false) OwnerType ownerType,
					                                    @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager,
					                                    Model model) throws UnsupportedEncodingException
    {
        Page<Product> products = productService.find(new ProductCriteria().withKeyword(keyword).withBrand(brand)
                .withCate(cate).withPrice(minPrice, maxPrice).withDeleted(Boolean.FALSE)
                .withStatus(ProductStatus.selling).withEnableOverseas(enableOverseas).withOwnerType(ownerType), pager);

        StringBuilder link = new StringBuilder("/jdvop/admin/enterpriseExclusiveProductPoolEntry/ajaxChoose.php?page=[:page]&id=").append(id);
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
        if (ownerType != null) {
            link.append("&ownerType=").append(ownerType.name());
        }

        Map<String, Object> dataMap = new HashMap<>();
        //创建产品HTML内容
        createHtmlContent(products, dataMap);
        String  pageHtml    = PagerUtil.createPagenation(link.toString(), products.getPagerSpec(), 4, "_self");
        dataMap.put("pageHtml", pageHtml);

        return dataMap;
    }


    /**
     * 拼接创建产品翻页HTML内容
     * @param products
     * @param dataMap
     */
    private void createHtmlContent(Page<Product> products, Map<String, Object> dataMap) {
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
    }
    
    @RequestMapping(value = "/admin/enterprise/pool/productEntry/delete.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse delete(@RequestParam(value = "productIds[]") Integer [] productIds,
    											   @RequestParam(value = "poolId") int poolId){
    	
    	EnterpriseExclusiveProductPool productPool = productPoolService.selectOne(poolId);
		if(productPool == null) {
			return new SimpleJsonResponse<>(false, "网络异常，请刷新后重新尝试！");
		}
    	
		if(productIds.length < 1) {
			return new SimpleJsonResponse<>(false, "请选择您需要删除的产品！");
		}
		
		productPoolService.batchDeleteEntries(poolId, Arrays.asList(productIds));
		
    	return new SimpleJsonResponse<>(true, null);
    	
    }
	
}

