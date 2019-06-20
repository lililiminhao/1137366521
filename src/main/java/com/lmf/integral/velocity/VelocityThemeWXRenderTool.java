/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.velocity;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.lmf.activity.service.LimitExchangeService;
import com.lmf.common.enums.OwnerType;
import com.lmf.enterprise.entity.EnterpriseExclusiveProductPoolEntry;
import com.lmf.enterprise.entity.EnterpriseUserMap;
import com.lmf.enterprise.service.EnterpriseExclusiveProductPoolEntryService;
import com.lmf.enterprise.service.EnterpriseUserMapService;
import com.lmf.integral.SystemConfig;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.market.entity.Coupon;
import com.lmf.product.entity.Product;
import com.lmf.product.service.ProductService;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteCustomBlock;
import com.lmf.website.entity.WebsiteCustomBlockEntry;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.service.UserDefinedCateService;
import com.lmf.website.theme.v2.BlockType;

/**
 *
 * @author yuanhu
 */
public class VelocityThemeWXRenderTool {
    
    private ChainedContext  cctx;
    
    private Website website;
    
    private ProductService productService;
    
    private SystemConfig systemConfig;
    
    private UserDetail  ud;
    
    private EnterpriseUserMapService enterpriseUserMapService;
    
    private EnterpriseExclusiveProductPoolEntryService enterpriseExclusiveProductPoolEntryService;
    
    private UserDefinedCateService userDefinedCateService;
    
    public  void init(Object ctx)
    {
        cctx    = (ChainedContext) ctx;
        WebApplicationContext  wctx = WebApplicationContextUtils.getWebApplicationContext(cctx.getServletContext());
        website                     = (Website)cctx.getRequest().getAttribute("website");
        productService          = wctx.getBean(ProductService.class);
        systemConfig = wctx.getBean(SystemConfig.class);
        enterpriseUserMapService 	= wctx.getBean(EnterpriseUserMapService.class);
        enterpriseExclusiveProductPoolEntryService 	= wctx.getBean(EnterpriseExclusiveProductPoolEntryService.class);
        userDefinedCateService = wctx.getBean(UserDefinedCateService.class);
        HttpSession session = cctx.getRequest().getSession(false);
        if (session != null)
        {
            ud  = (UserDetail) session.getAttribute("currentUser");
        }
    }
    
    public  final WebsiteCustomBlockEntry getCustomBlock(WebsiteCustomBlock ewcb)
    {
        if (ewcb != null)
        {
            BlockType    type    = ewcb.getBlockType();
            if (type == BlockType.text || type == BlockType.image) {
                return (WebsiteCustomBlockEntry) ewcb.getContent();
            }
        }
        return null;
    }
    
    public  final List  getCustomBlocks(WebsiteCustomBlock ewcb)
    {
        if (ewcb != null)
        {
            BlockType    type    = ewcb.getBlockType();
            if (type == BlockType.slider || type == BlockType.products) {
                return (List) ewcb.getContent();
            }
        }
        return null;
    }
    
    //公告栏
    public  String  getNotice(WebsiteCustomBlock ewcb) {
        WebsiteCustomBlockEntry entry = getCustomBlock(ewcb);
        StringBuilder noticeBuilder = new StringBuilder();
        if(entry != null) {
            noticeBuilder.append("<div class=\"custom-notice\">");
            noticeBuilder.append("<div class=\"text js-autoRoll\"><i>公告：</i>");
            noticeBuilder.append("<div class=\"con\">");
            noticeBuilder.append("<p id=\"textMove\">").append(entry.getDescription()).append("</p>");
            noticeBuilder.append("</div>");
            noticeBuilder.append("</div>");
            noticeBuilder.append("<span class=\"btn-del js-del\"></span>");
            noticeBuilder.append("</div>");
        }
        return noticeBuilder.toString();
    }
    
    //产品搜索
    public  String  getSearchBox(WebsiteCustomBlock ewcb) {
        StringBuilder searchBuilder = new StringBuilder();
        searchBuilder.append("<div class=\"a-search-wrap search-wrap\">");
        searchBuilder.append("<form id=\"form1\" action=\"/jdvop/mobile/products.php\" method=\"GET\">");
        searchBuilder.append("<div class=\"search-frm\">");
        searchBuilder.append("<input type=\"text\" class=\"search\" name=\"k\" autocomplete=\"off\" placeholder=\"搜索全部商品\"/>");
        searchBuilder.append("<a class=\"clear hide js-clearSearch\" href=\"javascript:;\" >x</a>");
        searchBuilder.append("</div>");
        searchBuilder.append("<div class=\"btn-wrap\">");
        searchBuilder.append("<input type=\"submit\" value=\"搜索\" class=\"hide\"/>");
        searchBuilder.append("<a href=\"javascript:;\" id=\"searchBtn\" class=\" hd_search_btn_blue\">搜索</a>");
        searchBuilder.append("</div>");
        searchBuilder.append("</form>");
        searchBuilder.append("</div>");
        return searchBuilder.toString();
    }
    
    //图片广告
    public  String  getImage(WebsiteCustomBlock ewcb) {
        WebsiteCustomBlockEntry entry = getCustomBlock(ewcb);
        StringBuilder imageBuilder = new StringBuilder();
        if(entry != null) {
            imageBuilder.append("<div class=\"main-banner\">");
            imageBuilder.append("<a href='").append(entry.getLinkTo()).append("'>");
            imageBuilder.append("<div class=\"alone-img\"><img src='").append(systemConfig.getImageHost()).append(entry.getData()).append("'></div>");
            imageBuilder.append("</a>");
            imageBuilder.append("</div>");
        }
        return imageBuilder.toString();
    }
    
    //轮播广告
    public  String  getSlider(WebsiteCustomBlock ewcb) {
        List<WebsiteCustomBlockEntry> entrys = getCustomBlocks(ewcb);
        StringBuilder sliderBuilder = new StringBuilder();
        if(entrys != null && entrys.size() > 0) {
            sliderBuilder.append("<div class=\"main-banner js-main-banner\" id='bannnerImg'>");
            sliderBuilder.append("<ul class=\"swiper-wrapper\">");
            for(WebsiteCustomBlockEntry entry: entrys) {
                sliderBuilder.append("<li class=\"swiper-slide\">");
                //临时上线修改
                sliderBuilder.append("<a href='").append(entry.getLinkTo().replace("https://mall.96225.com", "")).append("'>");
                //sliderBuilder.append("<a href='").append(entry.getLinkTo()).append("'>");
                sliderBuilder.append("<img src='").append(systemConfig.getImageHost()).append(entry.getData()).append("'/>");
                sliderBuilder.append("</a>");
                sliderBuilder.append("</li>");
            }
            sliderBuilder.append("</ul>");
            sliderBuilder.append("<div class=\"swiper-pagination\"></div>");
            sliderBuilder.append("</div>");
        }
        return sliderBuilder.toString();
    }
    
    //图片导航
    public  String  getNavigation(WebsiteCustomBlock ewcb) {
        List<WebsiteCustomBlockEntry> entrys = getCustomBlocks(ewcb);
        StringBuilder sliderBuilder = new StringBuilder();
        if(entrys != null && entrys.size() > 0) {
            sliderBuilder.append("<div class=\"a-module-1 webkitbox-h\" id='aM1'>");
            for(WebsiteCustomBlockEntry entry: entrys) {
                sliderBuilder.append("<div class=\"flex1 list\">");
                sliderBuilder.append("<a href='").append(entry.getLinkTo().replace("http://mall.96225.com", "")).append("'>");
                sliderBuilder.append("<img src='").append(systemConfig.getImageHost()).append(entry.getData()).append("'>");
                sliderBuilder.append("</a>");
                sliderBuilder.append("</div>");
            }
            sliderBuilder.append("</div>");
        }
        return sliderBuilder.toString();
    }
    
    //产品列表
    public  String  getProduct(WebsiteCustomBlock wcb, Map<Integer, Product> productMap, Integer ratio) {
        List<Integer> ids = getCustomBlocks(wcb);
        StringBuilder productBuilder = new StringBuilder();
        if(ids != null && ids.size() > 0) {
            java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#0");
            if(null != wcb.getBloKey()) switch (wcb.getBloKey()) {
                case "PRODUCT_BLOCK_1":
                    productBuilder.append("<div class=\"a-module-2 layout-list\" id='aM2'>");
                    productBuilder.append("<p class=\"a-title\">").append(wcb.getExt().get("model_title")).append("</p>");
                    productBuilder.append("<ul>");
                    for(Integer id: ids) {
                        productBuilder.append("<li><a href=\"/jdvop/product/").append(productMap.get(id).getId()).append(".php\">");
                        if(productMap.get(id).getOwnerType() == OwnerType.jingdong)
                        {
                            productBuilder.append("<p class=\"img\"><span class=\"ico-jdsm\"> <!--京东icon--> </span><img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else if(productMap.get(id).getOwnerType() == OwnerType.system) {
                            productBuilder.append("<p class=\"img\"><img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else {
                            productBuilder.append("<p class=\"img\"><img src='").append(systemConfig.getImageHost()).append("/middle").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        }
                        productBuilder.append("<p class=\"text\"><span class=\"name js-name\">").append(productMap.get(id).getName()).append("</span><em class=\"price js-price\"><i class=\"red\">");
                        productBuilder.append("¥").append(getProductSoldPrice(productMap.get(id))).append("</i></em></p></a></li>");
                    }   productBuilder.append("</ul>");
                    productBuilder.append("</div>");
                    break;
                case "PRODUCT_BLOCK_2":
                    productBuilder.append("<div class=\"a-module-3 layout-grid  clearfix\" id='aM3'>");
                    productBuilder.append("<p class=\"a-title\">").append(wcb.getExt().get("model_title")).append("</p>");
                    

                    if(CollectionUtils.isNotEmpty(wcb.getCoupon())){
                    	productBuilder.append("<div id=\"quan_list\">");
                        productBuilder.append("<div class=\"quan_wrapper\">");
                    	for (Coupon coupon : wcb.getCoupon()) {
                    		if(coupon.getType() ==1){
                                productBuilder.append("<div class=\"quan_item\" couponid=\""+coupon.getId()+"\">");
                                productBuilder.append("<div class=\"q_l\"><p>￥<span>"+coupon.getAmount()+"</span></p><p>满"+coupon.getUsable()+"可用</p></div>");
                                if(coupon.getHasRevice() == 1){
                                	productBuilder.append("<div class=\"q_r have\">已领取</div>");
                                }else {
                                	productBuilder.append("<div class=\"q_r\">立即领取</div>");
								}
                                productBuilder.append("</div>");
                                
                    		}else if(coupon.getType() ==2){
                                productBuilder.append("<div class=\"quan_item\" couponid=\""+coupon.getId()+"\">");
                                productBuilder.append("<div class=\"q_l\"><p>￥<span>"+coupon.getAmount()+"</span></p><p>直减"+coupon.getAmount()+"元</p></div>");
                                if(coupon.getHasRevice() == 1){
                                	productBuilder.append("<div class=\"q_r have\">已领取</div>");
                                }else {
                                	productBuilder.append("<div class=\"q_r\">立即领取</div>");
								}
                                productBuilder.append("</div>");
                                
                    		}else if(coupon.getType() == 3){
                                
                                productBuilder.append("<div class=\"quan_item\" couponid=\""+coupon.getId()+"\">");
                                productBuilder.append("<div class=\"q_l\"><p><span>"+coupon.getAmount()+"</span>折</p><p>折扣券</p></div>");
                                if(coupon.getHasRevice() == 1){
                                	productBuilder.append("<div class=\"q_r have\">已领取</div>");
                                }else {
                                	productBuilder.append("<div class=\"q_r\">立即领取</div>");
								}
                                productBuilder.append("</div>");
                                
                    		}
                    		
						}
                    	productBuilder.append("</div>");
                        productBuilder.append("</div>");
                    	
                    }
                    
                    productBuilder.append("<ul>");
                    for(Integer id: ids) {
                        productBuilder.append("<li><a href=\"/jdvop/product/").append(productMap.get(id).getId()).append(".php\">");
                        if(productMap.get(id).getOwnerType() == OwnerType.jingdong)
                        {
                            productBuilder.append("<p class=\"img\"><span class=\"ico-jdsm\"> <!--京东icon--> </span><img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else if(productMap.get(id).getOwnerType() == OwnerType.system) {
                            productBuilder.append("<p class=\"img\"><img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else {
                            productBuilder.append("<p class=\"img\"><img src='").append(systemConfig.getImageHost()).append("/middle").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        }
                        productBuilder.append("<p class=\"text\"><span class=\"name js-name\">").append(productMap.get(id).getName()).append("</span><em class=\"price red js-price\">");
                        productBuilder.append("¥").append(getProductSoldPrice(productMap.get(id))).append("</em></p></a></li>");
                    }   productBuilder.append("</ul>");
                    productBuilder.append("</div>");
                    break;
                case "PRODUCT_BLOCK_3":
                    productBuilder.append("<div class=\"a-module-4 layout-grid  clearfix\"  id='aM4'>");
                    productBuilder.append("<p class=\"a-title\">").append(wcb.getExt().get("model_title")).append("</p>");
                   

                    if(CollectionUtils.isNotEmpty(wcb.getCoupon())){
                    	productBuilder.append("<div id=\"quan_list\">");
                        productBuilder.append("<div class=\"quan_wrapper\">");
                    	for (Coupon coupon : wcb.getCoupon()) {
                    		if(coupon.getType() ==1){
                                productBuilder.append("<div class=\"quan_item\" couponid=\""+coupon.getId()+"\">");
                                productBuilder.append("<div class=\"q_l\"><p>￥<span>"+coupon.getAmount()+"</span></p><p>满"+coupon.getUsable()+"可用</p></div>");
                                if(coupon.getHasRevice() == 1){
                                	productBuilder.append("<div class=\"q_r have\">已领取</div>");
                                }else {
                                	productBuilder.append("<div class=\"q_r\">立即领取</div>");
								}
                                productBuilder.append("</div>");
                                
                    		}else if(coupon.getType() ==2){
                                productBuilder.append("<div class=\"quan_item\" couponid=\""+coupon.getId()+"\">");
                                productBuilder.append("<div class=\"q_l\"><p>￥<span>"+coupon.getAmount()+"</span></p><p>直减"+coupon.getAmount()+"元</p></div>");
                                if(coupon.getHasRevice() == 1){
                                	productBuilder.append("<div class=\"q_r have\">已领取</div>");
                                }else {
                                	productBuilder.append("<div class=\"q_r\">立即领取</div>");
								}
                                productBuilder.append("</div>");
                                
                    		}else if(coupon.getType() == 3){
                                
                                productBuilder.append("<div class=\"quan_item\" couponid=\""+coupon.getId()+"\">");
                                productBuilder.append("<div class=\"q_l\"><p><span>"+coupon.getAmount()+"</span>折</p><p>折扣券</p></div>");
                                if(coupon.getHasRevice() == 1){
                                	productBuilder.append("<div class=\"q_r have\">已领取</div>");
                                }else {
                                	productBuilder.append("<div class=\"q_r\">立即领取</div>");
								}
                                productBuilder.append("</div>");
                                
                    		}
                    		
						}
                    	productBuilder.append("</div>");
                        productBuilder.append("</div>");
                    	
                    }
                    
                    productBuilder.append("<ul>");
                    for(Integer id: ids) {
                        productBuilder.append("<li><a href=\"/jdvop/product/").append(productMap.get(id).getId()).append(".php\">");
                        if(productMap.get(id).getOwnerType() == OwnerType.jingdong)
                        {
                            productBuilder.append("<p class=\"img\"><span class=\"ico-jdsm\"> <!--京东icon--> </span><img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else if(productMap.get(id).getOwnerType() == OwnerType.system) {
                            productBuilder.append("<p class=\"img\"><img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else {
                            productBuilder.append("<p class=\"img\"><img src='").append(systemConfig.getImageHost()).append("/middle").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        }
                        productBuilder.append("<p class=\"text\"><span class=\"name js-name\">").append(productMap.get(id).getName()).append("</span><em class=\"price red js-price\">");
                        productBuilder.append("¥").append(getProductSoldPrice(productMap.get(id))).append("</em></p></a></li>");
                    }   productBuilder.append("</ul>");
                    productBuilder.append("</div>");
                    break;
                case "PRODUCT_BLOCK_4":
                    productBuilder.append("<div class=\"a-module-5 layout-grid clearfix\" id='aM5'>");
                    productBuilder.append("<p class=\"a-title\">").append(wcb.getExt().get("model_title")).append("</p>");
                   

                    if(CollectionUtils.isNotEmpty(wcb.getCoupon())){
                    	productBuilder.append("<div id=\"quan_list\">");
                        productBuilder.append("<div class=\"quan_wrapper\">");
                    	for (Coupon coupon : wcb.getCoupon()) {
                    		if(coupon.getType() ==1){
                                productBuilder.append("<div class=\"quan_item\" couponid=\""+coupon.getId()+"\">");
                                productBuilder.append("<div class=\"q_l\"><p>￥<span>"+coupon.getAmount()+"</span></p><p>满"+coupon.getUsable()+"可用</p></div>");
                                if(coupon.getHasRevice() == 1){
                                	productBuilder.append("<div class=\"q_r have\">已领取</div>");
                                }else {
                                	productBuilder.append("<div class=\"q_r\">立即领取</div>");
								}
                                productBuilder.append("</div>");
                                
                    		}else if(coupon.getType() ==2){
                                productBuilder.append("<div class=\"quan_item\" couponid=\""+coupon.getId()+"\">");
                                productBuilder.append("<div class=\"q_l\"><p>￥<span>"+coupon.getAmount()+"</span></p><p>直减"+coupon.getAmount()+"元</p></div>");
                                if(coupon.getHasRevice() == 1){
                                	productBuilder.append("<div class=\"q_r have\">已领取</div>");
                                }else {
                                	productBuilder.append("<div class=\"q_r\">立即领取</div>");
								}
                                productBuilder.append("</div>");
                                
                    		}else if(coupon.getType() == 3){
                                
                                productBuilder.append("<div class=\"quan_item\" couponid=\""+coupon.getId()+"\">");
                                productBuilder.append("<div class=\"q_l\"><p><span>"+coupon.getAmount()+"</span>折</p><p>折扣券</p></div>");
                                if(coupon.getHasRevice() == 1){
                                	productBuilder.append("<div class=\"q_r have\">已领取</div>");
                                }else {
                                	productBuilder.append("<div class=\"q_r\">立即领取</div>");
								}
                                productBuilder.append("</div>");
                                
                    		}
                    		
						}
                    	productBuilder.append("</div>");
                        productBuilder.append("</div>");
                    	
                    }
                    
                    productBuilder.append("<ul>");
                    for(Integer id: ids) {
                        productBuilder.append("<li><a href=\"/jdvop/product/").append(productMap.get(id).getId()).append(".php\">");
                        if(productMap.get(id).getOwnerType() == OwnerType.jingdong)
                        {
                            productBuilder.append("<p class=\"img\"><span class=\"ico-jdsm\"> <!--京东icon--> </span><img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else if(productMap.get(id).getOwnerType() == OwnerType.system){
                            productBuilder.append("<p class=\"img\"><img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else {
                            productBuilder.append("<p class=\"img\"><img src='").append(systemConfig.getImageHost()).append("/middle").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        }
                        productBuilder.append("<p class=\"text\"><span class=\"name js-name\">").append(productMap.get(id).getName()).append("</span><em class=\"price red js-price\">");
                        productBuilder.append("¥").append(getProductSoldPrice(productMap.get(id))).append("</em></p></a></li>");
                    }   productBuilder.append("</ul>");
                    productBuilder.append("</div>");
                    break;
            }
        }
        return productBuilder.toString();
    }
    
  //产品列表
    public  String  getProduct(WebsiteCustomBlock wcb, Map<Integer, Product> productMap, Integer ratio, WebsiteUser websiteUser) {
        List<Integer> ids = getCustomBlocks(wcb);
        StringBuilder productBuilder = new StringBuilder();
        if(ids != null && ids.size() > 0) {
            java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#0");
            if(null != wcb.getBloKey()) switch (wcb.getBloKey()) {
                case "PRODUCT_BLOCK_1":
                    productBuilder.append("<div class=\"a-module-2 layout-list\" id='aM2'>");
                    productBuilder.append("<p class=\"a-title\">").append(wcb.getExt().get("model_title")).append("</p>");
                    productBuilder.append("<ul>");
                    for(Integer id: ids) {
                    	EnterpriseExclusiveProductPoolEntry productPoolEntry = enterpriseExclusiveProductPoolEntryService.selectProductPoolEntry(websiteUser, id);
                    	if(productPoolEntry==null) {
                    		continue;
                    	}
                    	boolean isEnterpriseZoneProduct = userDefinedCateService.isEnterpriseZoneProduct(id);
                    	if(!isEnterpriseZoneProduct) {
                    		continue;
                    	}
                        productBuilder.append("<li><a href=\"/jdvop/product/").append(productMap.get(id).getId()).append(".php\">");
                        if(productMap.get(id).getOwnerType() == OwnerType.jingdong)
                        {
                            productBuilder.append("<p class=\"img\"><span class=\"ico-jdsm\"> <!--京东icon--> </span><img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else if(productMap.get(id).getOwnerType() == OwnerType.system) {
                            productBuilder.append("<p class=\"img\"><img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else {
                            productBuilder.append("<p class=\"img\"><img src='").append(systemConfig.getImageHost()).append("/middle").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        }
                        productBuilder.append("<p class=\"text\"><span class=\"name js-name\">").append(productMap.get(id).getName()).append("</span><em class=\"price js-price\"><i class=\"red\">");
                        productBuilder.append("¥").append(getProductSoldPrice(productMap.get(id))).append("</i></em></p></a></li>");
                    }   productBuilder.append("</ul>");
                    productBuilder.append("</div>");
                    break;
                case "PRODUCT_BLOCK_2":
                    productBuilder.append("<div class=\"a-module-3 layout-grid  clearfix\" id='aM3'>");
                    productBuilder.append("<p class=\"a-title\">").append(wcb.getExt().get("model_title")).append("</p>");
                    

                    if(CollectionUtils.isNotEmpty(wcb.getCoupon())){
                    	productBuilder.append("<div id=\"quan_list\">");
                        productBuilder.append("<div class=\"quan_wrapper\">");
                    	for (Coupon coupon : wcb.getCoupon()) {
                    		if(coupon.getType() ==1){
                                productBuilder.append("<div class=\"quan_item\" couponid=\""+coupon.getId()+"\">");
                                productBuilder.append("<div class=\"q_l\"><p>￥<span>"+coupon.getAmount()+"</span></p><p>满"+coupon.getUsable()+"可用</p></div>");
                                if(coupon.getHasRevice() == 1){
                                	productBuilder.append("<div class=\"q_r have\">已领取</div>");
                                }else {
                                	productBuilder.append("<div class=\"q_r\">立即领取</div>");
								}
                                productBuilder.append("</div>");
                                
                    		}else if(coupon.getType() ==2){
                                productBuilder.append("<div class=\"quan_item\" couponid=\""+coupon.getId()+"\">");
                                productBuilder.append("<div class=\"q_l\"><p>￥<span>"+coupon.getAmount()+"</span></p><p>直减"+coupon.getAmount()+"元</p></div>");
                                if(coupon.getHasRevice() == 1){
                                	productBuilder.append("<div class=\"q_r have\">已领取</div>");
                                }else {
                                	productBuilder.append("<div class=\"q_r\">立即领取</div>");
								}
                                productBuilder.append("</div>");
                                
                    		}else if(coupon.getType() == 3){
                                
                                productBuilder.append("<div class=\"quan_item\" couponid=\""+coupon.getId()+"\">");
                                productBuilder.append("<div class=\"q_l\"><p><span>"+coupon.getAmount()+"</span>折</p><p>折扣券</p></div>");
                                if(coupon.getHasRevice() == 1){
                                	productBuilder.append("<div class=\"q_r have\">已领取</div>");
                                }else {
                                	productBuilder.append("<div class=\"q_r\">立即领取</div>");
								}
                                productBuilder.append("</div>");
                                
                    		}
                    		
						}
                    	productBuilder.append("</div>");
                        productBuilder.append("</div>");
                    	
                    }
                    
                    productBuilder.append("<ul>");
                    for(Integer id: ids) {
                    	EnterpriseExclusiveProductPoolEntry productPoolEntry = enterpriseExclusiveProductPoolEntryService.selectProductPoolEntry(websiteUser, id);
                    	if(productPoolEntry==null) {
                    		continue;
                    	}
                    	boolean isEnterpriseZoneProduct = userDefinedCateService.isEnterpriseZoneProduct(id);
                    	if(!isEnterpriseZoneProduct) {
                    		continue;
                    	}
                        productBuilder.append("<li><a href=\"/jdvop/product/").append(productMap.get(id).getId()).append(".php\">");
                        if(productMap.get(id).getOwnerType() == OwnerType.jingdong)
                        {
                            productBuilder.append("<p class=\"img\"><span class=\"ico-jdsm\"> <!--京东icon--> </span><img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else if(productMap.get(id).getOwnerType() == OwnerType.system) {
                            productBuilder.append("<p class=\"img\"><img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else {
                            productBuilder.append("<p class=\"img\"><img src='").append(systemConfig.getImageHost()).append("/middle").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        }
                        productBuilder.append("<p class=\"text\"><span class=\"name js-name\">").append(productMap.get(id).getName()).append("</span><em class=\"price red js-price\">");
                        productBuilder.append("¥").append(getProductSoldPrice(productMap.get(id))).append("</em></p></a></li>");
                    }   productBuilder.append("</ul>");
                    productBuilder.append("</div>");
                    break;
                case "PRODUCT_BLOCK_3":
                    productBuilder.append("<div class=\"a-module-4 layout-grid  clearfix\"  id='aM4'>");
                    productBuilder.append("<p class=\"a-title\">").append(wcb.getExt().get("model_title")).append("</p>");
                   

                    if(CollectionUtils.isNotEmpty(wcb.getCoupon())){
                    	productBuilder.append("<div id=\"quan_list\">");
                        productBuilder.append("<div class=\"quan_wrapper\">");
                    	for (Coupon coupon : wcb.getCoupon()) {
                    		if(coupon.getType() ==1){
                                productBuilder.append("<div class=\"quan_item\" couponid=\""+coupon.getId()+"\">");
                                productBuilder.append("<div class=\"q_l\"><p>￥<span>"+coupon.getAmount()+"</span></p><p>满"+coupon.getUsable()+"可用</p></div>");
                                if(coupon.getHasRevice() == 1){
                                	productBuilder.append("<div class=\"q_r have\">已领取</div>");
                                }else {
                                	productBuilder.append("<div class=\"q_r\">立即领取</div>");
								}
                                productBuilder.append("</div>");
                                
                    		}else if(coupon.getType() ==2){
                                productBuilder.append("<div class=\"quan_item\" couponid=\""+coupon.getId()+"\">");
                                productBuilder.append("<div class=\"q_l\"><p>￥<span>"+coupon.getAmount()+"</span></p><p>直减"+coupon.getAmount()+"元</p></div>");
                                if(coupon.getHasRevice() == 1){
                                	productBuilder.append("<div class=\"q_r have\">已领取</div>");
                                }else {
                                	productBuilder.append("<div class=\"q_r\">立即领取</div>");
								}
                                productBuilder.append("</div>");
                                
                    		}else if(coupon.getType() == 3){
                                
                                productBuilder.append("<div class=\"quan_item\" couponid=\""+coupon.getId()+"\">");
                                productBuilder.append("<div class=\"q_l\"><p><span>"+coupon.getAmount()+"</span>折</p><p>折扣券</p></div>");
                                if(coupon.getHasRevice() == 1){
                                	productBuilder.append("<div class=\"q_r have\">已领取</div>");
                                }else {
                                	productBuilder.append("<div class=\"q_r\">立即领取</div>");
								}
                                productBuilder.append("</div>");
                                
                    		}
                    		
						}
                    	productBuilder.append("</div>");
                        productBuilder.append("</div>");
                    	
                    }
                    
                    productBuilder.append("<ul>");
                    for(Integer id: ids) {
                    	EnterpriseExclusiveProductPoolEntry productPoolEntry = enterpriseExclusiveProductPoolEntryService.selectProductPoolEntry(websiteUser, id);
                    	if(productPoolEntry==null) {
                    		continue;
                    	}
                    	boolean isEnterpriseZoneProduct = userDefinedCateService.isEnterpriseZoneProduct(id);
                    	if(!isEnterpriseZoneProduct) {
                    		continue;
                    	}
                        productBuilder.append("<li><a href=\"/jdvop/product/").append(productMap.get(id).getId()).append(".php\">");
                        if(productMap.get(id).getOwnerType() == OwnerType.jingdong)
                        {
                            productBuilder.append("<p class=\"img\"><span class=\"ico-jdsm\"> <!--京东icon--> </span><img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else if(productMap.get(id).getOwnerType() == OwnerType.system) {
                            productBuilder.append("<p class=\"img\"><img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else {
                            productBuilder.append("<p class=\"img\"><img src='").append(systemConfig.getImageHost()).append("/middle").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        }
                        productBuilder.append("<p class=\"text\"><span class=\"name js-name\">").append(productMap.get(id).getName()).append("</span><em class=\"price red js-price\">");
                        productBuilder.append("¥").append(getProductSoldPrice(productMap.get(id))).append("</em></p></a></li>");
                    }   productBuilder.append("</ul>");
                    productBuilder.append("</div>");
                    break;
                case "PRODUCT_BLOCK_4":
                    productBuilder.append("<div class=\"a-module-5 layout-grid clearfix\" id='aM5'>");
                    productBuilder.append("<p class=\"a-title\">").append(wcb.getExt().get("model_title")).append("</p>");
                   

                    if(CollectionUtils.isNotEmpty(wcb.getCoupon())){
                    	productBuilder.append("<div id=\"quan_list\">");
                        productBuilder.append("<div class=\"quan_wrapper\">");
                    	for (Coupon coupon : wcb.getCoupon()) {
                    		if(coupon.getType() ==1){
                                productBuilder.append("<div class=\"quan_item\" couponid=\""+coupon.getId()+"\">");
                                productBuilder.append("<div class=\"q_l\"><p>￥<span>"+coupon.getAmount()+"</span></p><p>满"+coupon.getUsable()+"可用</p></div>");
                                if(coupon.getHasRevice() == 1){
                                	productBuilder.append("<div class=\"q_r have\">已领取</div>");
                                }else {
                                	productBuilder.append("<div class=\"q_r\">立即领取</div>");
								}
                                productBuilder.append("</div>");
                                
                    		}else if(coupon.getType() ==2){
                                productBuilder.append("<div class=\"quan_item\" couponid=\""+coupon.getId()+"\">");
                                productBuilder.append("<div class=\"q_l\"><p>￥<span>"+coupon.getAmount()+"</span></p><p>直减"+coupon.getAmount()+"元</p></div>");
                                if(coupon.getHasRevice() == 1){
                                	productBuilder.append("<div class=\"q_r have\">已领取</div>");
                                }else {
                                	productBuilder.append("<div class=\"q_r\">立即领取</div>");
								}
                                productBuilder.append("</div>");
                                
                    		}else if(coupon.getType() == 3){
                                
                                productBuilder.append("<div class=\"quan_item\" couponid=\""+coupon.getId()+"\">");
                                productBuilder.append("<div class=\"q_l\"><p><span>"+coupon.getAmount()+"</span>折</p><p>折扣券</p></div>");
                                if(coupon.getHasRevice() == 1){
                                	productBuilder.append("<div class=\"q_r have\">已领取</div>");
                                }else {
                                	productBuilder.append("<div class=\"q_r\">立即领取</div>");
								}
                                productBuilder.append("</div>");
                                
                    		}
                    		
						}
                    	productBuilder.append("</div>");
                        productBuilder.append("</div>");
                    	
                    }
                    
                    productBuilder.append("<ul>");
                    for(Integer id: ids) {
                    	EnterpriseExclusiveProductPoolEntry productPoolEntry = enterpriseExclusiveProductPoolEntryService.selectProductPoolEntry(websiteUser, id);
                    	if(productPoolEntry==null) {
                    		continue;
                    	}
                    	boolean isEnterpriseZoneProduct = userDefinedCateService.isEnterpriseZoneProduct(id);
                    	if(!isEnterpriseZoneProduct) {
                    		continue;
                    	}
                        productBuilder.append("<li><a href=\"/jdvop/product/").append(productMap.get(id).getId()).append(".php\">");
                        if(productMap.get(id).getOwnerType() == OwnerType.jingdong)
                        {
                            productBuilder.append("<p class=\"img\"><span class=\"ico-jdsm\"> <!--京东icon--> </span><img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else if(productMap.get(id).getOwnerType() == OwnerType.system){
                            productBuilder.append("<p class=\"img\"><img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else {
                            productBuilder.append("<p class=\"img\"><img src='").append(systemConfig.getImageHost()).append("/middle").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        }
                        productBuilder.append("<p class=\"text\"><span class=\"name js-name\">").append(productMap.get(id).getName()).append("</span><em class=\"price red js-price\">");
                        productBuilder.append("¥").append(getProductSoldPrice(productMap.get(id))).append("</em></p></a></li>");
                    }   productBuilder.append("</ul>");
                    productBuilder.append("</div>");
                    break;
            }
        }
        return productBuilder.toString();
    }
    
	/* add by lq  */
    public String getProductSoldPrice(Product product) {
        DecimalFormat df = new DecimalFormat("#0.00");
        if (website != null && product != null) {
            Product bp = productService.findOne(product.getId());
            if (bp != null) {
            	if(ud != null && ud.getUserId() > 0) {
            		EnterpriseUserMap userMap = enterpriseUserMapService.getOneByUserId(ud.getUserId());
        	    	if(userMap != null && userMap.getProductPoolId() != null && userMap.getProductPoolId() > 0) {
        	    		EnterpriseExclusiveProductPoolEntry entry = enterpriseExclusiveProductPoolEntryService.selectOne(userMap.getProductPoolId(), product.getId());
        	    		if(entry != null) {
        	    			product.setRetailPrice(entry.getExclusivePrice());
        	    		}
        	    	}
            	}
                String integral = df.format(product.getRetailPrice());
                return integral;
            }
        }
        return "0.00";
    }
}
