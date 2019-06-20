/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.velocity;

import com.lmf.common.enums.OwnerType;
import com.lmf.integral.SystemConfig;
import com.lmf.product.entity.Product;
import com.lmf.website.entity.WebsiteCustomBlock;
import com.lmf.website.entity.WebsiteCustomBlockEntry;
import com.lmf.website.theme.v2.BlockType;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.List;
import java.util.Map;
/**
 *
 * @author yuanhu
 */
@DefaultKey("wxRenderTool")
public class VelocityThemeWXRenderTool {
    
    private ChainedContext  cctx;
    
    private SystemConfig systemConfig;
    
    public  void init(Object ctx)
    {
        cctx    = (ChainedContext) ctx;
        WebApplicationContext  wctx = WebApplicationContextUtils.getWebApplicationContext(cctx.getServletContext());
        systemConfig = wctx.getBean(SystemConfig.class);
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
    
    //公告
    public  String  getNotice(WebsiteCustomBlock wcb) {
        WebsiteCustomBlockEntry entry = getCustomBlock(wcb);
        StringBuilder noticeBuilder = new StringBuilder();
        noticeBuilder.append("<dd class='js-dragsort' id='notice_").append(wcb.getRenderSerial()).append("'>");
        noticeBuilder.append("<input type=\"hidden\" name=\"blockId[]\" value='").append(wcb.getBlockId()).append("' />");
        noticeBuilder.append("<div class='custom-notice'>");
        noticeBuilder.append("<div class='text js-autoRoll'><i>公告：</i>");
        if(entry != null) {
            noticeBuilder.append("<input type=\"hidden\" name=\"noticeContent\" value='").append(entry.getDescription()).append("'/>");
            noticeBuilder.append("<div class='con'><p id='textMove'>").append(entry.getDescription()).append("</p></div>").append("</div></div>");
        } else {
            noticeBuilder.append("<div class='con'><p id='textMove'>请填写内容，如果文字过长则将会自动滚动。</p></div></div></div>");
        }
        noticeBuilder.append("<div class=\"actions-wrap\">");
        noticeBuilder.append("<span class=\"js-btnEdit\" key=\"notice\" rev='").append(wcb.getRenderSerial()).append("'>编辑</span>");
        noticeBuilder.append("<span class=\"js-addContent\">加内容</span><span class=\"js-btnDel\">删除</span></div>");
        noticeBuilder.append("</dd>");
        return noticeBuilder.toString();
    }
    
    //产品搜索
    public  String  getSearchBox(WebsiteCustomBlock wcb) {
        StringBuilder searchBuilder = new StringBuilder();
        searchBuilder.append("<dd class='js-dragsort' id='notice_").append(wcb.getRenderSerial()).append("'>");
        searchBuilder.append("<input type=\"hidden\" name=\"blockId[]\" value='").append(wcb.getBlockId()).append("' />");
        searchBuilder.append("<div class=\"a-search-wrap search-wrap\">");
        searchBuilder.append("<div class=\"search-frm\">");
        searchBuilder.append("<input type=\"search\" class=\"search\" id=\"search\" autocomplete=\"off\" placeholder=\"搜索全部商品\"/>");
        searchBuilder.append("<a class=\"clear hide js-clearSearch\" href=\"javascript:;\" >x</a>");
        searchBuilder.append("</div>");
        searchBuilder.append("<div class=\"btn-wrap\">");
        searchBuilder.append("<input type=\"submit\" value=\"搜索\" class=\"hide\"/>");
        searchBuilder.append("<a href=\"javascript:;\" id=\"searchBtn\" class=\" hd_search_btn_blue\">搜索</a>");
        searchBuilder.append("</div></div>");
        searchBuilder.append("<div class=\"actions-wrap\">");
        searchBuilder.append("<span rev='").append(wcb.getRenderSerial()).append("'>编辑</span>");
        searchBuilder.append("<span class=\"js-addContent\">加内容</span><span class=\"js-btnDel\">删除</span></div>");
        searchBuilder.append("</dd>");
        return searchBuilder.toString();
    }
    
    //图片广告
    public  String  getImage(WebsiteCustomBlock wcb) {
        WebsiteCustomBlockEntry entry = getCustomBlock(wcb);
        StringBuilder imageBuilder = new StringBuilder();
        imageBuilder.append("<dd class=\"js-dragsort\" id='slider_").append(wcb.getRenderSerial()).append("'>");
        imageBuilder.append("<input type=\"hidden\" name=\"blockId[]\" value='").append(wcb.getBlockId()).append("' />");
        imageBuilder.append("<input type=\"hidden\" name=\"bloKey\" value='").append(wcb.getBloKey()).append("' />");
        if(entry != null) {
            imageBuilder.append("<div class=\"alone-img\"><img src='").append(systemConfig.getImageHost()).append(entry.getData()).append("'></div>");
        } else {
            imageBuilder.append("<div class=\"alone-img\"><img src=\"/jdvop/images/wx/morenimg1.jpg\"></div>");
        }
        imageBuilder.append("<div class=\"actions-wrap\">");
        imageBuilder.append("<span class=\"js-btnEdit\" key=\"image\" rev='").append(wcb.getRenderSerial()).append("'>编辑</span>");
        imageBuilder.append("<span class=\"js-addContent\">加内容</span><span class=\"js-btnDel\">删除</span></div>");
        imageBuilder.append("</dd>");
        return imageBuilder.toString();
    }
    
    //轮播广告
    public  String  getSlider(WebsiteCustomBlock ewcb) {
        List<WebsiteCustomBlockEntry> entrys = getCustomBlocks(ewcb);
        StringBuilder sliderBuilder = new StringBuilder();
        sliderBuilder.append("<dd class=\"js-dragsort\" id='slider_").append(ewcb.getRenderSerial()).append("'>");
        sliderBuilder.append("<input type=\"hidden\" name=\"blockId[]\" value='").append(ewcb.getBlockId()).append("' />");
        sliderBuilder.append("<input type=\"hidden\" name=\"bloKey\" value='").append(ewcb.getBloKey()).append("' />");
        sliderBuilder.append("<div class=\"main-banner js-main-banner\">");
        sliderBuilder.append("<ul class=\"swiper-wrapper js-slider-ul\">");
        if(entrys != null && entrys.size() > 0) {
            for(WebsiteCustomBlockEntry entry: entrys) {
                sliderBuilder.append("<li class=\"swiper-slide\"><img src='").append(systemConfig.getImageHost()).append(entry.getData()).append("'></li>");
            }
        } else {
            sliderBuilder.append("<li class=\"swiper-slide\"><img src=\"/jdvop/images/admin/wx/morenimg1.jpg\"></li>");
            sliderBuilder.append("<li class=\"swiper-slide\"><img src=\"/jdvop/images/admin/wx/morenimg1.jpg\"></li>");
            sliderBuilder.append("<li class=\"swiper-slide\"><img src=\"/jdvop/images/admin/wx/morenimg1.jpg\"></li>");
        }
        sliderBuilder.append("</ul>");
        sliderBuilder.append("<div class=\"swiper-pagination\"></div>");
        sliderBuilder.append("</div>");
        sliderBuilder.append("<div class=\"actions-wrap\">");
        sliderBuilder.append("<span class=\"js-btnEdit\" key=\"slider\" rev='").append(ewcb.getRenderSerial()).append("'>编辑</span>");
        sliderBuilder.append("<span class=\"js-addContent\">加内容</span><span class=\"js-btnDel\">删除</span></div>");
        sliderBuilder.append("</dd>");
        return sliderBuilder.toString();
    }
    
    //图片导航
    public  String  getNavigation(WebsiteCustomBlock wcb) {
        List<WebsiteCustomBlockEntry> entrys = getCustomBlocks(wcb);
        StringBuilder sliderBuilder = new StringBuilder();
        sliderBuilder.append("<dd class=\"js-dragsort\" id='nav_").append(wcb.getRenderSerial()).append("'>");
        sliderBuilder.append("<input type=\"hidden\" name=\"blockId[]\" value='").append(wcb.getBlockId()).append("' />");
        if(entrys != null && entrys.size() > 0) {
            sliderBuilder.append("<div class=\"a-module-1 webkitbox-h js-nav-div\">");
            for(WebsiteCustomBlockEntry entry: entrys) {
                sliderBuilder.append("<div class=\"flex1 list\"><img src='").append(systemConfig.getImageHost()).append(entry.getData()).append("'></div>");
            }
        } else {
            sliderBuilder.append("<div class=\"a-module-1 webkitbox-h js-nav-div\">");
            sliderBuilder.append("<div class=\"flex1 list\"><img src='/jdvop/images/admin/wx/test-img.jpg'></div>");
            sliderBuilder.append("<div class=\"flex1 list\"><img src='/jdvop/images/admin/wx/test-img.jpg'></div>");
        }
        sliderBuilder.append("</div>");
        sliderBuilder.append("<div class=\"actions-wrap\">");
        sliderBuilder.append("<span class=\"js-btnEdit\" key=\"navigation\" rev='").append(wcb.getRenderSerial()).append("'>编辑</span>");
        sliderBuilder.append("<span class=\"js-addContent\">加内容</span><span class=\"js-btnDel\">删除</span></div>");
        sliderBuilder.append("</dd>");
        return sliderBuilder.toString();
    }
    
    //商品列表
    public  String  getProducts(WebsiteCustomBlock wcb, Map<Integer, Product> productMap, Integer ratio) {
        if(productMap == null || productMap.isEmpty()){
            return "";
        }
        List<Integer> ids = getCustomBlocks(wcb);
        java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#0.00");
        StringBuilder productBuilder = new StringBuilder();
        productBuilder.append("<dd class='js-dragsort' id='product_").append(wcb.getRenderSerial()).append("'>");
        productBuilder.append("<input type=\"hidden\" name=\"blockId[]\" value='").append(wcb.getBlockId()).append("' />");
        if(null != wcb.getBloKey()) switch (wcb.getBloKey()) {
            case "PRODUCT_BLOCK_1":
            	if(StringUtils.isNotBlank(wcb.getCouponCodes())){
           		 productBuilder.append("<div class=\"a-module-3 layout-grid2 clearfix\" id=\"product-module\" couponcode="+wcb.getCouponCodes()+">");
           	}else{
           		 productBuilder.append("<div class=\"a-module-3 layout-grid2 clearfix\" id=\"product-module\" couponcode=''>");
           	}
                productBuilder.append("<p class=\"a-title tc\">").append(wcb.getExt().get("model_title")).append("</p>");
                
                productBuilder.append("<div class=\"quan_wrapper\"><div class=\"quan_item\"></div><div class=\"quan_item\"></div><div class=\"quan_item\"></div></div>");
                
                
                productBuilder.append("<ul class=\"js-product-ul\">");
                if(ids != null && ids.size() > 0) {
                    for(Integer id: ids) {
                        if(productMap.get(id).getOwnerType() == OwnerType.jingdong || productMap.get(id).getOwnerType() == OwnerType.system)
                        {
                            productBuilder.append("<li><p class=\"img\">");
                            if(productMap.get(id).getOwnerType() == OwnerType.jingdong)
                            {
                                productBuilder.append("<span class=\"ico-jdsm\"> <!--京东icon--> </span>");
                            }
                            productBuilder.append("<img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else {
                            productBuilder.append("<li><p class=\"img\"><img src='").append(systemConfig.getImageHost()).append("/middle").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        }
                        productBuilder.append("<p class=\"text\"><span class=\"name js-name\">").append(productMap.get(id).getName()).append("</span><em class=\"price red js-price\">&yen;");
                        productBuilder.append(df.format(ratio * productMap.get(id).getRetailPrice())).append("</em></p></a></li>");
                    }
                } else {
                    productBuilder.append("<li><p class=\"img\"><img src=\"/jdvop/images/admin/wx/test-img.jpg\"  alt=\"产品名称\"/></p>");
                    productBuilder.append("<p class=\"text\"><span class=\"name js-name\">Dibea/地贝 智能扫地机器人家用智能吸尘器 </span>");
                    productBuilder.append("<em class=\"price red js-price\">&yen;6690.00.00.00</em></p></li>");
                    productBuilder.append("<li><p class=\"img\"><img src=\"/jdvop/images/admin/wx/test-img.jpg\"  alt=\"产品名称\"/></p>");
                    productBuilder.append("<p class=\"text\"><span class=\"name js-name\">Dibea/地贝 智能扫地机器人家用智能吸尘器 </span>");
                    productBuilder.append("<em class=\"price red js-price\">&yen;6690.00.00.00</em></p></li>");
                    productBuilder.append("<li><p class=\"img\"><img src=\"/jdvop/images/admin/wx/test-img.jpg\"  alt=\"产品名称\"/></p>");
                    productBuilder.append("<p class=\"text\"><span class=\"name js-name\">Dibea/地贝 智能扫地机器人家用智能吸尘器 </span>");
                    productBuilder.append("<em class=\"price red js-price\">&yen;6690.00.00.00</em></p></li>");
                }   productBuilder.append("</ul>");
                productBuilder.append("</div>");
                break;
            case "PRODUCT_BLOCK_2":
            	if(StringUtils.isNotBlank(wcb.getCouponCodes())){
            		 productBuilder.append("<div class=\"a-module-3 layout-grid2 clearfix\" id=\"product-module\" couponcode="+wcb.getCouponCodes()+">");
            	}else{
            		 productBuilder.append("<div class=\"a-module-3 layout-grid2 clearfix\" id=\"product-module\" couponcode=''>");
            	}
               
                productBuilder.append("<p class=\"a-title tc\">").append(wcb.getExt().get("model_title")).append("</p>");
                
                productBuilder.append(
                		"<div id=\"quan_list\">"
	                		+ "<div class=\"quan_wrapper\">"
		                		+ "<div class=\"quan_item\">"
		                		+"<div class=\"q_l\"><p>￥<span>10</span></p><p>满100可用</p></div>"
		                        +"<div class=\"q_r\">立即领取</div>"
		                		+ "</div>"
		                		+ "<div class=\"quan_item\">"
		                		+"<div class=\"q_l\"><p>￥<span>10</span></p><p>满100可用</p></div>"
		                        +"<div class=\"q_r\">立即领取</div>"
		                		+ "</div>"
		                		+ "<div class=\"quan_item\">"
		                		+"<div class=\"q_l\"><p>￥<span>10</span></p><p>满100可用</p></div>"
		                        +"<div class=\"q_r\">立即领取</div>"
		                		+ "</div>"
	                		+ "</div>"
                		+ "</div>"
                		);
                
                productBuilder.append("<ul class=\"js-product-ul\">");
                if(ids != null && ids.size() > 0) {
                    for(Integer id: ids) {
                        if(productMap.get(id).getOwnerType() == OwnerType.jingdong || productMap.get(id).getOwnerType() == OwnerType.system)
                        {
                            productBuilder.append("<li><p class=\"img\">");
                            if(productMap.get(id).getOwnerType() == OwnerType.jingdong)
                            {
                                productBuilder.append("<span class=\"ico-jdsm\"> <!--京东icon--> </span>");
                            }
                            productBuilder.append("<img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else {
                            productBuilder.append("<li><p class=\"img\"><img src='").append(systemConfig.getImageHost()).append("/middle").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        }
                        productBuilder.append("<p class=\"text\"><span class=\"name js-name\">").append(productMap.get(id).getName()).append("</span><em class=\"price red js-price\">&yen;");
                        productBuilder.append(df.format(ratio * productMap.get(id).getRetailPrice())).append("</em></p></a></li>");
                    }
                } else {
                    productBuilder.append("<li><p class=\"img\"><img src=\"/jdvop/images/admin/wx/test-img.jpg\"  alt=\"产品名称\"/></p>");
                    productBuilder.append("<p class=\"text\"><span class=\"name js-name\">Dibea/地贝 智能扫地机器人家用智能吸尘器 </span>");
                    productBuilder.append("<em class=\"price red js-price\">&yen;6690.00.00.00</em></p></li>");
                    productBuilder.append("<li><p class=\"img\"><img src=\"/jdvop/images/admin/wx/test-img.jpg\"  alt=\"产品名称\"/></p>");
                    productBuilder.append("<p class=\"text\"><span class=\"name js-name\">Dibea/地贝 智能扫地机器人家用智能吸尘器 </span>");
                    productBuilder.append("<em class=\"price red js-price\">&yen;6690.00.00.00</em></p></li>");
                    productBuilder.append("<li><p class=\"img\"><img src=\"/jdvop/images/admin/wx/test-img.jpg\"  alt=\"产品名称\"/></p>");
                    productBuilder.append("<p class=\"text\"><span class=\"name js-name\">Dibea/地贝 智能扫地机器人家用智能吸尘器 </span>");
                    productBuilder.append("<em class=\"price red js-price\">&yen;6690.00.00.00</em></p></li>");
                    productBuilder.append("<li><p class=\"img\"><img src=\"/jdvop/images/admin/wx/test-img.jpg\"  alt=\"产品名称\"/></p>");
                    productBuilder.append("<p class=\"text\"><span class=\"name js-name\">Dibea/地贝 智能扫地机器人家用智能吸尘器 </span>");
                    productBuilder.append("<em class=\"price red js-price\">&yen;6690.00.00.00</em></p></li>");
                }   productBuilder.append("</ul>");
                productBuilder.append("</div>");
                break;
            case "PRODUCT_BLOCK_4":
            	if(StringUtils.isNotBlank(wcb.getCouponCodes())){
           		 productBuilder.append("<div class=\"a-module-3 layout-grid2 clearfix\" id=\"product-module\" couponcode="+wcb.getCouponCodes()+">");
           	}else{
           		 productBuilder.append("<div class=\"a-module-3 layout-grid2 clearfix\" id=\"product-module\" couponcode=''>");
           	}
                productBuilder.append("<p class=\"a-title tc\">").append(wcb.getExt().get("model_title")).append("</p>");
                
                productBuilder.append("<div class=\"quan_wrapper\"><div class=\"quan_item\"></div><div class=\"quan_item\"></div><div class=\"quan_item\"></div></div>");
                
                productBuilder.append("<ul class=\"js-product-ul\">");
                if(ids != null && ids.size() > 0) {
                    for(Integer id: ids) {
                        if(productMap.get(id).getOwnerType() == OwnerType.jingdong || productMap.get(id).getOwnerType() == OwnerType.system)
                        {
                            productBuilder.append("<li><p class=\"img\">");
                            if(productMap.get(id).getOwnerType() == OwnerType.jingdong)
                            {
                                productBuilder.append("<span class=\"ico-jdsm\"> <!--京东icon--> </span>");
                            }
                            productBuilder.append("<img src='").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        } else {
                            productBuilder.append("<li><p class=\"img\"><img src='").append(systemConfig.getImageHost()).append("/middle").append(productMap.get(id).getThumbnailImage()).append("' class=\"js-img\"/></p>");
                        }
                        productBuilder.append("<p class=\"text\"><span class=\"name js-name\">").append(productMap.get(id).getName()).append("</span><em class=\"price red js-price\">&yen;");
                        productBuilder.append(df.format(ratio * productMap.get(id).getRetailPrice())).append("</em></p></a></li>");
                    }
                } else {
                    productBuilder.append("<li><p class=\"img\"><img src=\"/jdvop/images/admin/wx/test-img.jpg\"  alt=\"产品名称\"/></p>");
                    productBuilder.append("<p class=\"text\"><span class=\"name js-name\">Dibea/地贝 智能扫地机器人家用智能吸尘器 </span>");
                    productBuilder.append("<em class=\"price red js-price\">&yen;6690.00.00.00</em></p></li>");
                    productBuilder.append("<li><p class=\"img\"><img src=\"/jdvop/images/admin/wx/test-img.jpg\"  alt=\"产品名称\"/></p>");
                    productBuilder.append("<p class=\"text\"><span class=\"name js-name\">Dibea/地贝 智能扫地机器人家用智能吸尘器 </span>");
                    productBuilder.append("<em class=\"price red js-price\">&yen;6690.00.00.00</em></p></li>");
                    productBuilder.append("<li><p class=\"img\"><img src=\"/jdvop/images/admin/wx/test-img.jpg\"  alt=\"产品名称\"/></p>");
                    productBuilder.append("<p class=\"text\"><span class=\"name js-name\">Dibea/地贝 智能扫地机器人家用智能吸尘器 </span>");
                    productBuilder.append("<em class=\"price red js-price\">&yen;6690.00.00.00</em></p></li>");
                }   productBuilder.append("</ul>");
                productBuilder.append("</div>");
                break;
        }
        productBuilder.append("<div class=\"actions-wrap\"><span class=\"js-btnEdit\" key=\"product\" rev='").append(wcb.getRenderSerial());
        productBuilder.append("' rel='").append(wcb.getBloKey());
        if(ids != null && ids.size() > 0) {
            productBuilder.append("' snum='").append(ids.size()).append("'>编辑</span>");
        } else {
            productBuilder.append("' snum='6'>编辑</span>");
        }
        productBuilder.append("<span class=\"js-addContent\">加内容</span><span class=\"js-btnDel\">删除</span></div>");
        productBuilder.append("</dd>");
        return productBuilder.toString();
    }
    
}
