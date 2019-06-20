/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.activity.controller;

import com.lmf.activity.entity.FlashSaleActivity;
import com.lmf.activity.entity.FlashSaleProduct;
import com.lmf.activity.enums.FlashSaleActivityStatus;
import com.lmf.activity.enums.FlashSaleActivityType;
import com.lmf.activity.service.LimitExchangeService;
import com.lmf.activity.service.TimeSpikeService;
import com.lmf.activity.vo.FlashSaleActivityCriteria;
import com.lmf.common.util.ArrayUtil;
import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.enums.OwnerType;
import com.lmf.common.util.CommonUtil;
import com.lmf.common.util.PagerSpec;
import com.lmf.enums.ProductStatus;
import com.lmf.product.entity.Brand;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.ProductCate;
import com.lmf.product.service.ProductCateService;
import com.lmf.product.service.ProductService;
import com.lmf.product.service.ProductStorageService;
import com.lmf.product.vo.ProductCriteria;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author zhixiong.wu
 * 限时秒杀
 */
@Controller
public class TimeSpikeController {
    
    @Autowired
    private TimeSpikeService timeSpikeService;
    
    //查询所有产品
    @Autowired
    private ProductService  productService;
    
    @Autowired
    private ProductCateService  productCateService;

    @Autowired
    private  LimitExchangeService limitExchangeService;
    
    private static final String modify = "-1";//代表修改
    
    //查询所有限时秒杀的活动
    @RequestMapping(value = "/admin/activity/selectTimeSpike.php", method = RequestMethod.GET) 
    public String selectTimeSpike(@RequestParam(value="activityName",required=false) String activityName,
                                  @RequestParam(value="startTime",required=false) String startTime,
                                  @RequestParam(value="status",required=false) FlashSaleActivityStatus status,
                                  @PagerSpecDefaults(pageSize = 20, sort = "startTime.desc") PagerSpec pager, Model model){
        
        
        StringBuilder sb = new StringBuilder("/jdvop/admin/activity/selectTimeSpike.php?page=[:page]");
        SimpleDateFormat sbf = new SimpleDateFormat("yyyy-MM-dd"); 
        if(!CommonUtil.isNull(activityName)){
            sb.append("&activityName=").append(activityName);
            model.addAttribute("activityName", activityName);
        }else if(!CommonUtil.isNull(startTime)){
            sb.append("&startTime=").append(startTime);
            model.addAttribute("startTime", startTime);
        }else if(status!=null){
            sb.append("&status=").append(status.name());
            model.addAttribute("status", status.name());
        }
        FlashSaleActivityCriteria criteria;
        if(!CommonUtil.isNull(startTime)){
            criteria =new FlashSaleActivityCriteria()
                .withKeyword(activityName).withFlashSaleActivityStatus(status).withStartTime(Timestamp.valueOf(startTime)).withFlashSaleActivityType(FlashSaleActivityType.second_kill);
        }else{
            criteria =new FlashSaleActivityCriteria()
                .withKeyword(activityName).withFlashSaleActivityStatus(status).withStartTime(null).withFlashSaleActivityType(FlashSaleActivityType.second_kill);
        }
        
        Page<FlashSaleActivity> timeSpike = timeSpikeService.findAllFlashSaleActivity(criteria, pager);
        List<FlashSaleActivity> product = timeSpike.getContent();
        
        for(FlashSaleActivity a:product){
                int num = 0;
                Long id = a.getId();
                List<FlashSaleProduct> products = timeSpikeService.findOne(id);
                for(FlashSaleProduct p:products){
                    if(p.getRemaindAmount()!=null){
                         num+=p.getRemaindAmount();//如果一个活动下面的所有产品剩余数量都为0 则代表此活动已结束
                    }
                   
                }
                a.setNum(num);
                timeSpikeService.updateStatus(a);
        }
        
        model.addAttribute("timeSpike", timeSpike);
        model.addAttribute("link", sb.toString());
        model.addAttribute("nowDate",new Date());//系统当前时间
        model.addAttribute("intDateTime",Timestamp.valueOf(sbf.format(new Date())+" 00:00:00"));//系统当前时间
        model.addAttribute("activityStatus", FlashSaleActivityStatus.values());
        model.addAttribute("timeSpikeService", timeSpikeService);
        return "/admin/activity/timeSpike/list";
        
    }
    /****
     * 跳到编辑活动的页面
     * @param keyword 关键字
     * @param status 状态
     * @param brand 
     * @param cate 分类
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param maxSize 页面上显示最多能勾选的商品数
     * @param minSize 页面上显示的最少勾选商品数
     * @param self
     * @param type 商品的分类(系统，京东，自有)
     * @param pager 分页
     * @param model 传递对象的域
     * @return 
     */
    @RequestMapping(value = "/admin/timeSpikeActivity/toSaveOrUpdateView.php", method = RequestMethod.GET) 
    public String toSaveOrUpdateView(@RequestParam(value = "kw", required = false) String keyword,
                        @RequestParam(value = "status", defaultValue = "selling") ProductStatus status,
                        @RequestParam(value = "b", required = false) Brand brand,
                        @RequestParam(value = "cate", required = false) ProductCate cate,
                        @RequestParam(value = "minPrice", required = false) Double minPrice,
                        @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                        @RequestParam(value = "maxSize", defaultValue = "20") int maxSize,
                        @RequestParam(value = "minSize", defaultValue = "1") int minSize,
                        @RequestParam(value = "self", required = false) Boolean self,
                        @RequestParam(value = "type", required = false) OwnerType type,
                        @RequestParam(value = "activityId", required = false) String activityId,
                        @RequestParam(value = "productIds[]", required = false) String[] productIds,
                        @PagerSpecDefaults(pageSize = 20, sort = "time.desc") PagerSpec pager,
                        Model model){
        
        //查询所有的商品
        ProductCriteria criteria    = new ProductCriteria().withKeyword(keyword)
                .withBrand(brand).withCate(cate).withPrice(minPrice, maxPrice)
                .withOwnerType(type).withStatus(ProductStatus.selling).withDeleted(Boolean.FALSE);
        
              
        Page<Product> products = productService.find(criteria, pager);
        //如果不为空，修改
        if(!"-1".equals(activityId)){
            List<FlashSaleProduct> activityProducts  = timeSpikeService.findOne(Long.valueOf(activityId));
            model.addAttribute("productsCheckedModity", activityProducts);
          model.addAttribute("activityId", activityId);
        }else{
           //保存选中的产品(新增活动：选择产品后点击下一页，之前选中的产品没有)
            
           model.addAttribute("activityId", activityId);
        }
          //存储活动id
        String link = "/jdvop/admin/website/block/loadProducts.php?page=[:page]";
        model.addAttribute("products", products);//产品集合
        model.addAttribute("cate", cate);
        model.addAttribute("link", link);
        model.addAttribute("productCateService", productCateService);
        model.addAttribute("productService", productService);
        model.addAttribute("ownerTypes", OwnerType.values());
        model.addAttribute("maxSize", maxSize);
        model.addAttribute("minSize", minSize);
        return "/admin/activity/timeSpike/form";
    }
    
    /***
     * 创建活动 (选好产品后，点击下一步跳转的方法)
     * @param ids
     * @param model
     * @return 
     */
    @RequestMapping(value = "/admin/activity/timeSpike/nextfrom.php", method = RequestMethod.GET) 
    public String toNextView(@RequestParam(value = "pids", required = false) String ids,
                             @RequestParam(value = "activityId", required = false) String activityId,Model model){
        
        
        //根据产品ID 去查询关联的产品
        String [] id = ids.split(",");
        List<Integer> newProductIDs = new ArrayList();
        for(String  pid:id){
            newProductIDs.add(Integer.parseInt(pid));
        }
        List<FlashSaleProduct> productModitys = new ArrayList<FlashSaleProduct>();
        List<Product> products = productService.find(newProductIDs);
        //编辑
        if(!modify.equals(activityId)){
            //先根据用户选择的产品id去查询所有产品，然后再通过产品id查询活动产品表
            for(Product p:products){
                FlashSaleProduct flashSaleProduct = limitExchangeService.find(p.getId().longValue(),Long.valueOf(activityId));
                FlashSaleProduct flashSaleProductObject = new FlashSaleProduct();
                //为空的情况代表 用户重新选择新的产品
                if(flashSaleProduct==null){
                    flashSaleProductObject.setProductId(p.getId());
                    flashSaleProductObject.setThumbnailImage(p.getThumbnailImage());//图片
                    flashSaleProductObject.setName(p.getName());//名称
                    flashSaleProductObject.setDisplayOriginalPrice(p.getRetailPrice());
                    flashSaleProductObject.setSaleNum(0);
                    flashSaleProductObject.setProductCode(p.getProductCode());
                    productModitys.add(flashSaleProductObject);
                //不为空则是活动下面本身有产品
                }else{
                    //更改数量 (已售数量=限制数量-剩余数量)
                    flashSaleProduct.setSaleNum(flashSaleProduct.getLimitNum()-flashSaleProduct.getRemaindAmount());
                    productModitys.add(flashSaleProduct);
                }
            }
             //查询活动名称、时间
             FlashSaleActivity activity =  timeSpikeService.findFlashSaleActivity(Long.valueOf(activityId));
             model.addAttribute("productModitys", productModitys);
             //存储活动id
             model.addAttribute("activityId", activityId);
             model.addAttribute("activity", activity);
             return "/admin/activity/timeSpike/nextFrom_modity";
        }
        model.addAttribute("productsChecked", products);
        model.addAttribute("checkedProductIds", ids);
        return "/admin/activity/timeSpike/nextFrom_add";
    }
    
   
    /***
     * 创建限时秒杀活动
     * @param activity
     * @return 
     */
    @RequestMapping(value = "/admin/activity/timeSpike/createActivity.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse create(@RequestBody FlashSaleActivity activity){
            
            if(activity.getProductList()==null){//修改时间
                timeSpikeService.saveOrUpdate(activity.getProductList(), true, activity);
            }else{
                activity.setTodayTotalFlow(0l);//当天浏览量
                List<FlashSaleProduct> product = activity.getProductList();
                List<Double> price = new ArrayList<Double>();
                for(int i=0;i<product.size();i++){
                    price.add(product.get(i).getDiscountRate());
                }
                activity.setDiscountRate(ArrayUtil.avg(price));//avg
                activity.setType(FlashSaleActivityType.second_kill);
                activity.setStatus(FlashSaleActivityStatus.will_do);
                if(activity.getId()==null){//新增
                    //校验活动名称
                    FlashSaleActivity ac = timeSpikeService.findOne(activity.getActivityName());
                    if(ac!=null){
                        return new SimpleJsonResponse(false,"活动名称已存在，请添加不同的活动名称!");
                    }
                    timeSpikeService.saveOrUpdate(activity.getProductList(), false, activity);
                }else{
                    //校验活动名称
//                    FlashSaleActivity ac = timeSpikeService.findOne(activity.getActivityName());
////                    if(ac!=null){
////                        return new SimpleJsonResponse(false,"活动名称已存在，请添加不同的活动名称!");
////                    }
                    //修改(先删除再新增)
                    timeSpikeService.delete(new Long[]{activity.getId()});
                    timeSpikeService.saveOrUpdate(activity.getProductList(), false, activity);
                }
            }
            return new SimpleJsonResponse(true,"创建秒杀活动成功");
    }
    
    /***
     * 显示活动的详情
     * @param activityId
     * @param model
     * @return 
     */
    @RequestMapping(value = "/admin/activity/timeSpike/createActivity.php", method = RequestMethod.GET)
    public String findActivityDetail(@RequestParam(value="activityId",required=false) String activityId,Model model){
        
        FlashSaleActivity activity = timeSpikeService.findFlashSaleActivity(Long.valueOf(activityId));
        //查询活动下面的产品
        String [] activityIds = activityId.split(",");//活动id
        List<Integer> newactivityIds = new ArrayList();
        for(String  pid:activityIds){
            newactivityIds.add(Integer.parseInt(pid));
        }
        List<FlashSaleProduct> product = timeSpikeService.find(newactivityIds);
        for(FlashSaleProduct p :product){
            p.setSaleNum(p.getLimitNum()-p.getRemaindAmount());
        }
        model.addAttribute("activity", activity);
        model.addAttribute("productDetail", product);
        return "/admin/activity/timeSpike/activity_detail";
    }
    
    @RequestMapping(value = "/admin/activity/timeSpike/delete.php", method = RequestMethod.POST) 
    public @ResponseBody SimpleJsonResponse delete(@RequestParam(value = "activityId", required = false) Long[] activityId){
        timeSpikeService.delete(activityId);
        return new SimpleJsonResponse(true,"操作成功!");
    }
    
    public StringBuilder getParameter(String keyword,ProductStatus status, Brand brand, ProductCate cate,Double minPrice,
                                    Double maxPrice,Boolean self,OwnerType type,Model model,String activityId){
        
        StringBuilder link = new StringBuilder("/jdvop/admin/timeSpikeActivity/toSaveOrUpdateView.php?page=[:page]");
        if (keyword != null && !keyword.isEmpty()) {
            try {
                link.append("&kw=").append(URLEncoder.encode(keyword, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(TimeSpikeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (status != null) {
            link.append("&status=").append(status.name());
            model.addAttribute("status", status.name());
        }
        if (brand != null) {
            link.append("&b=").append(brand.getId());
        }
        if(cate != null) {
            link.append("&cate=").append(cate.getId());
        }
        
        if(self != null) {
            link.append("&self=").append(self);
            model.addAttribute("self", self);
        }
        
        if(type != null) {
            link.append("&type=").append(type.name());
            model.addAttribute("types", type.name());
        } 
        
        if(minPrice != null) {
            link.append("&minPrice=").append(minPrice);
            model.addAttribute("minPrice", minPrice);
        } 
        
        if(maxPrice != null) {
            link.append("&maxPrice=").append(maxPrice);
            model.addAttribute("maxPrice", maxPrice);
        }
        if(activityId != null) {
            link.append("&activityId=").append(activityId);
            model.addAttribute("activityId", activityId);
        }
        return link;
    }
    
}
