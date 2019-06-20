/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.controller;


import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.PagerSpec;
import com.lmf.enterprise.entity.Enterprise;
import com.lmf.enterprise.entity.EnterpriseEmployee;
import com.lmf.enterprise.entity.EnterpriseUserMap;
import com.lmf.enterprise.service.EnterpriseEmployeeService;
import com.lmf.enterprise.service.EnterpriseExclusiveProductPoolService;
import com.lmf.enterprise.service.EnterpriseService;
import com.lmf.enterprise.service.EnterpriseUserMapService;
import com.lmf.enterprise.vo.EnterpriseExclusiveProductPoolCriteria;
import com.lmf.product.service.ProductCateService;
import com.lmf.website.entity.*;
import com.lmf.website.enums.SpecialActivityType;
import com.lmf.website.enums.SpecialStatus;
import com.lmf.website.service.SpecialActivityService;
import com.lmf.website.service.WebsiteCustomBlockService;
import com.lmf.website.theme.v2.BlockType;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 配置 首页专题活动
 * @author wangdong
 */
@Controller
public class SpecialActivityController {
    
    @Autowired
    private SpecialActivityService specialActivityService;
    
    @Autowired
    private ProductCateService productCateService;
    
    @Autowired
    private WebsiteCustomBlockService websiteCustomBlockService;
    
    @Autowired
    private EnterpriseExclusiveProductPoolService enterpriseExclusiveProductPoolService;
    
    @Autowired
    private EnterpriseService enterpriseService;
    
    //首页活动专题列表页面
    @RequiresPermissions("commen_activity:view")
    @RequestMapping(value = "/admin/specialList.php" , method = RequestMethod.GET)
    public String specialList(@RequestParam(value = "kw", required = false) String keyword,
                            @PagerSpecDefaults(pageSize = 10, sort = "time.desc") PagerSpec pager,
                            @RequestParam(value = "type", defaultValue = "commonSpecial") SpecialActivityType type,//专题活动类型
                            WebsiteAdministrator admin, Model model) throws UnsupportedEncodingException
    {
        StringBuilder link = new StringBuilder("/jdvop/admin/specialList.php?page=[:page]&type="+type.name());
        if (keyword != null && !keyword.isEmpty()) {
            link.append("&kw=").append(URLEncoder.encode(keyword, "UTF-8"));
            model.addAttribute("kw", keyword);
        }
        //查找数据
        Page<SpecialActivity> special = specialActivityService.find(keyword, type, pager);
        if(special.hasContent()){
        	for(SpecialActivity sa : special){
        		// 判断活动是否已下线 如果下线时间在当前时间之前 则该专题已下线
        		if (sa.getOnLineTime() != null && sa.getOffLineTime() != null) {
					if (sa.getOffLineTime().before(new Date())) {
						sa.setStatus(SpecialStatus.Off_line); // 下线
						String line = "Off_line";
						specialActivityService.setStatus(sa.getId(), line);
					}else if(sa.getOnLineTime().before(new Date())) {
						sa.setStatus(SpecialStatus.on_line); //上线
						String line = "on_line";
						specialActivityService.setStatus(sa.getId(), line);
					}else{
						sa.setStatus(SpecialStatus.not_on_line); //未上线
						String line = "not_on_line";
						specialActivityService.setStatus(sa.getId(), line);
					}

				}
        		
        	}
        	
        }
        	
        model.addAttribute("special", special);
        model.addAttribute("enterpriseExclusiveProductPoolService", enterpriseExclusiveProductPoolService);
        model.addAttribute("link", link.toString());
        
        //根据专题活动类型 跳转到相应的页面
        if(type.equals(SpecialActivityType.enterpriseSpecial)){ //企业专享专题
        	return "/admin/special_activity/enterprise_list"; 
        	
        }
        
        return "/admin/special_activity/list";
    }
   
    //添加专题活动
    @RequiresPermissions("commen_activity:create")
    @RequestMapping(value = "/admin/addSpecial.php" , method = RequestMethod.GET)
    public String addSpecial(Website website, Model model)
    {
    	
    	//查询全部商品池信息
    	
    	model.addAttribute("productPool", enterpriseExclusiveProductPoolService.selectByPage(null, new EnterpriseExclusiveProductPoolCriteria().andEnableEquals(true)));
        model.addAttribute("cates", productCateService.findAll());
        return "/admin/special_activity/special_from";
    }
    
    //保存专题活动
    @RequiresPermissions("commen_activity:create")
    @RequestMapping(value="/admin/special/specialSave.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse specialSave(@RequestParam(value = "type", required = false) SpecialActivityType type, //专题活动类型
    												   @RequestParam(value = "activityName", required = false)String activityName, //专题活动名称
    												   @RequestParam(value = "productPoolId", required = false)Integer productPoolId, //商品池ID
    												   @RequestParam(value = "onLineTime", required = false) Date onLineTime, // 上线时间
    												   @RequestParam(value ="offLineTime", required = false) Date offLineTime, //下线时间
    												   @RequestParam(value ="remark", required = false )String remark,   //备注
    												   WebsiteAdministrator admin)
    {
    	
    	if(type == null){
    		return new SimpleJsonResponse(false , "专题活动类型数据异常，请重新输入!");
    	}
    	//判断专题活动名称
    	if(activityName == null || activityName.isEmpty()){
    		return new SimpleJsonResponse(false, "专题活动名称不能为空！");
    	}
    	//判断输入的专题活动名称是否存在  不管是普通专题和企业专享专题 名称都不能重复
    	if(specialActivityService.findActivityName(activityName)){
    		return new SimpleJsonResponse(false, "你输入的专题活动名称已存在，请重新输入！");
    	}
    	//判断专题类型为专享价专题的时候   商品池ID不能为空
    	if(type.equals(SpecialActivityType.enterpriseSpecial)){
    		if(productPoolId == null || productPoolId <= 0){
    			return new SimpleJsonResponse(false , "专享价专题商品池信息不能为空！");
    		}
    	}
    	 //下线时间必须大于上线时间
        if(onLineTime != null || offLineTime != null){
        	if(onLineTime!= null && offLineTime == null 
        			|| offLineTime != null && onLineTime == null){
        		return new SimpleJsonResponse(false, "请完善上线时间和下线时间信息！");
        	}
	    	if (onLineTime.equals(offLineTime) || onLineTime.after(offLineTime)) {
				return new SimpleJsonResponse(false, "下线时间必须大于上线时间！");
			}
        }
        
    	//保存专题活动
    	SpecialActivity special   = new SpecialActivity();
    	special.setType(type);
    	special.setActivityName(activityName);
    	if(type.equals(SpecialActivityType.enterpriseSpecial)){ 
    		special.setProductPoolId(productPoolId);
    	}else{
    		special.setProductPoolId(0);
    	}
    	if(onLineTime != null){
    		special.setOnLineTime(onLineTime); //上线时间 
    	}
    	if(offLineTime != null){
    		special.setOffLineTime(offLineTime); //下线时间
    	}
    	if(remark != null){
    		special.setRemark(remark);  //备注
    	}
    	
    	specialActivityService.save(special);  // 保存数据
    	
    	return new SimpleJsonResponse(true, "数据创建成功");
    }
    
    //编辑专题活动
    @RequiresPermissions("commen_activity:edit")
    @RequestMapping(value = "/admin/special/specialEdit.php", method = RequestMethod.GET)
    public String specialEdit(@RequestParam(value = "specialId", required = false) Integer specialId, Model model)
    {
    	//查找专题活动数据
    	SpecialActivity special = specialActivityService.findOne(specialId);
    	if(special == null){
    		throw new RuntimeException("throw new RuntimeException();" + specialId); //专题活动不存在
    	}
    	
    	if(special.getOnLineTime() != null){
    		//编辑专题活动的时候  如果上线的时候 是不能编辑商品池  下线可以编辑
        	if(special.getOnLineTime().before(new Date())){//上线
        		model.addAttribute("pool", false);
        	}
    	}
    	
    	//查询商品池信息
    	model.addAttribute("productPool", enterpriseExclusiveProductPoolService.selectByPage(null, new EnterpriseExclusiveProductPoolCriteria().andEnableEquals(true)));
    	model.addAttribute("special", special);
    	
    	return "/admin/special_activity/special_from";
    	
    }
    
    //编辑专题活动保存
    @RequiresPermissions("commen_activity:edit")
    @RequestMapping(value = "/admin/special/specialEdit.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse specialEdit(@RequestParam(value = "specialId", required = false) Integer specialId,  //专题活动ID
										    		   @RequestParam(value = "type", required = false) SpecialActivityType type, //专题活动类型
													   @RequestParam(value = "activityName", required = false)String activityName, //专题活动名称
													   @RequestParam(value = "productPoolId", required = false)Integer productPoolId, //商品池ID
													   @RequestParam(value = "onLineTime", required = false) Date onLineTimes, // 上线时间
													   @RequestParam(value = "offLineTime", required = false) Date offLineTimes, //下线时间
													   @RequestParam(value ="remark", required = false )String remark,   //备注
													   WebsiteAdministrator admin)
    {
    	//查询专题活动数据
    	SpecialActivity special = specialActivityService.findOne(specialId);
    	if(special == null){
    		return new SimpleJsonResponse(false , "专题数据不存在,请重新操作！");
    	}
    	if(activityName == null || activityName.isEmpty()){
            return new SimpleJsonResponse(false, "专题活动名称不能为空！");
        }
        //判断专题活动名称是否存在
        if(specialActivityService.findActivityName(activityName, specialId)){
            return new SimpleJsonResponse(false, "该专题活动名称已经存在，请重新输入");
        }
        
        //下线时间必须大于上线时间
        if(onLineTimes != null || offLineTimes != null){
        	if(onLineTimes!= null && offLineTimes == null 
        			|| offLineTimes != null && onLineTimes == null){
        		return new SimpleJsonResponse(false, "请完善上线时间和下线时间信息！");
        	}
        	// 上线时间 在当前日期之前
     		if (onLineTimes.before(new Date())) {
     			return new SimpleJsonResponse(false, "请选择大于或者等于当前日期的时间！");
     		}
	    	if (onLineTimes.equals(offLineTimes) || onLineTimes.after(offLineTimes)) {
				return new SimpleJsonResponse(false, "下线时间必须大于上线时间！");
			}
        }
        
        //判断专题类型为专享价专题的时候   商品池ID不能为空
    	if(type.equals(SpecialActivityType.enterpriseSpecial)){
    		if(productPoolId == null || productPoolId <= 0){
    			return new SimpleJsonResponse(false , "专享价专题商品池信息不能为空！");
    		}
    	}
        
        special.setActivityName(activityName); //专题活动名称
        if(onLineTimes != null){
        	special.setOnLineTime(onLineTimes); //上线时间
        }
        if(offLineTimes != null){
        	special.setOffLineTime(offLineTimes); //下线时间
        }
        
        special.setRemark(remark); //备注
        special.setProductPoolId(productPoolId);
        specialActivityService.save(special);
    	
    	return new SimpleJsonResponse(true , "成功编辑数据");
    }
    
    //企业专享专题  详情
    @RequiresPermissions("enterprise_activity:view")
    @RequestMapping(value = "/admin/special/specialView.php" , method = RequestMethod.GET)
    public String sepcialView(@RequestParam(value = "specialId", required = false) Integer specialId,
    										WebsiteAdministrator admin, Model model)
    {
    	//查询企业专享专题
    	SpecialActivity special = specialActivityService.findOne(specialId);
    	if(special == null){
    		throw new RuntimeException("throw new RuntimeException();" + specialId); //专题活动不存在
    	}
    	
    	//根据专题活动  里面商品池ID 查找商品池信息 
    	
    	model.addAttribute("productPool", enterpriseExclusiveProductPoolService.selectOne(special.getProductPoolId()));
    	model.addAttribute("special", special);
    	model.addAttribute("enterprise", enterpriseService.listEnterPrise(specialId));
    	
    	return "/admin/special_activity/special_view";
    }
    
     //保存页面配置
    @RequiresPermissions("index_model:edit")
    @RequestMapping(value = "/admin/special/save.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse save(@RequestParam(value = "blockId[]", required = false) Integer[] blockId,
                                                @RequestParam(value = "activityName", required = false)String activityName, //专题活动名称
                                                @RequestParam(value = "remarkName", required = false) String remarkName)  //专题备注
    {
        if(blockId == null || blockId.length == 0) {
            return new SimpleJsonResponse(false, "您还没有任何配置，请配置后保存！");
        }
        if(activityName == null || activityName.isEmpty()){
            return new SimpleJsonResponse(false, "专题活动名称不能为空！");
        }
        
        //判断专题活动是否存在
        if(specialActivityService.findActivityName(activityName)){
            return new SimpleJsonResponse(false, "你输入的专题活动名称已存在，请重新输入！");
        }
        
        //保存专题活动
        SpecialActivity special = new SpecialActivity();
        special.setActivityName(activityName);
        special.setRemark(remarkName);
        
        specialActivityService.save(special); //保存数据
        

        for(int i = 0;i < blockId.length; i++) {
            WebsiteCustomBlock websiteCustomBlock = websiteCustomBlockService.findOne(blockId[i]);
            websiteCustomBlock.setRenderSerial(i+1);
            
            //根据输入的 专题活动名字 查找数据
            SpecialActivity activity = specialActivityService.findName(activityName.trim());
            if(activity == null){
                return new SimpleJsonResponse(false, "数据异常！");
            }else{
                websiteCustomBlock.setSpecialActivityId(activity.getId()); //保存专题活动ID 
            }
            
            websiteCustomBlockService.save(websiteCustomBlock);
        }
        
        return new SimpleJsonResponse(true, null); 
    }
    
    //编辑自定义配置模板
    @RequiresPermissions("commen_activity:peizhi")
    @RequestMapping(value = "/admin/special/edit.php", method = RequestMethod.GET)
    public String editCustom(@RequestParam(value = "specialActivityId", required = false) Integer specialActivityId,
                            Website website, Model model)
    {
        //根据专题ID 查找数据
        SpecialActivity activity  = specialActivityService.findOne(specialActivityId);
        
        List<WebsiteCustomBlock> blocks = websiteCustomBlockService.findCustomBlock(specialActivityId);
        //blocks中待删除的ID集合
        List<Integer> blockIds = new ArrayList<Integer>();
        //如果block是产品类型并且内容为空，则进行删除操作
        for (WebsiteCustomBlock block : blocks) {
            if(block.getBlockType() == BlockType.products && block.getContent() == null){
                websiteCustomBlockService.delByBlockId(block.getBlockId());
                blockIds.add(block.getBlockId());
            }
        }
        //遍历要删除的ID，再遍历要删除的实体集合。删除List集合中的元素只能用迭代器，其他遍历方式会报错
        for (Integer blockId : blockIds) {
            Iterator<WebsiteCustomBlock> it = blocks.iterator();
            while(it.hasNext()){
                WebsiteCustomBlock websiteCustomBlock = it.next();
                if(websiteCustomBlock.getBlockId().equals(blockId)){
                    it.remove();
                }
            }
        }
        
        model.addAttribute("activity", activity);
        model.addAttribute("blocks", blocks);
        model.addAttribute("cates", productCateService.findAll());
        
        return "/admin/special_activity/from";
    }
    
    //编辑保存
    @RequiresPermissions("commen_activity:peizhi")
    @RequestMapping(value = "/admin/special/edit.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse<String> editCustom(@RequestParam(value = "specialActivityId", required = false) Integer specialActivityId,
                                            @RequestParam(value = "blockId[]", required = false) Integer[] blockId)
    {
        //根据专题活动ID 查找数据
        SpecialActivity activity = specialActivityService.findOne(specialActivityId);
        if(activity == null){
            return new SimpleJsonResponse(false, "专题活动不存在,请重新选择！");
        }
        
        if(blockId == null || blockId.length == 0) {
            return new SimpleJsonResponse(false, "您还没有任何配置，请配置后保存！");
        }
        
        for(int i = 0;i < blockId.length; i++) {
            WebsiteCustomBlock websiteCustomBlock = websiteCustomBlockService.findOne(blockId[i]);
            websiteCustomBlock.setRenderSerial(i+1);
            websiteCustomBlock.setSpecialActivityId(activity.getId()); //保存专题活动ID 
          
            websiteCustomBlockService.save(websiteCustomBlock);
        }
        
        return new SimpleJsonResponse(true, null); 
    }
    
    //专题活动上线
    @RequiresPermissions("commen_activity:online")
    @RequestMapping(value="/admin/special/onLineTime.php", method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse onLineTime(@RequestParam(value = "specialId", required = false) Integer specialId,
													@RequestParam(value = "onLineTime", required = false) Date onLineTime, // 上线时间
													@RequestParam(value = "offLineTime", required = false) Date offLineTime) // 下线时间
    {
    	//根据专题活动ID 查找数据
    	SpecialActivity activity = specialActivityService.findOne(specialId);
        if(activity == null){
            return new SimpleJsonResponse(false, "专题活动不存在,请重新选择！");
        }
    	// 判断上线时间是否为空
 		if (onLineTime == null) {
 			return new SimpleJsonResponse(false, "上线时间不能为空！");
 		}
 		// 判断下线时间是否为空
 		if (offLineTime == null) {
 			return new SimpleJsonResponse(false, "下线时间不能为空！");
 		}
 		// 上线时间 在当前日期之前
		if (onLineTime.before(new Date())) {
			return new SimpleJsonResponse(false, "请选择大于或者等于当前日期的时间！");
		}
		// 下线时间必须大于上线时间
		if (onLineTime.equals(offLineTime) || onLineTime.after(offLineTime)) {
			return new SimpleJsonResponse(false, "下线时间必须大于上线时间！");
		}
		
		activity.setOnLineTime(onLineTime); //上线时间
		activity.setOffLineTime(offLineTime); //下线时间
		activity.setStatus(SpecialStatus.on_line); //上线
		
		specialActivityService.save(activity);  //编辑数据
		
    	return new SimpleJsonResponse(true, "成功上线");
    }
    
    //专题活动下线
    @RequiresPermissions("commen_activity:offline")
    @RequestMapping(value="/admin/special/offLineTime.php" , method = RequestMethod.POST)
    public @ResponseBody SimpleJsonResponse offLineTime(@RequestParam(value = "specialId", required = false) Integer specialId)
    {
    	//根据专题活动ID 查找数据
    	SpecialActivity activity = specialActivityService.findOne(specialId);
        if(activity == null){
            return new SimpleJsonResponse(false, "专题活动不存在,请重新选择！");
        }
        
        activity.setStatus(SpecialStatus.Off_line); //下线状态
        activity.setOffLineTime(new Date()); //下线时间
        
        specialActivityService.save(activity);  //编辑数据
        
    	return new SimpleJsonResponse(true , "成功下线");
    }
    
    //删除专题活动
    @RequestMapping(value = "/admin/special/delete.php", method = RequestMethod.GET)
    public @ResponseBody SimpleJsonResponse specialDelet(@RequestParam(value = "specialActivityId", required = false) Integer specialActivityId,
                                                            Website website)
    {
        //根据专题活动ID查找数据
        SpecialActivity activity = specialActivityService.findOne(specialActivityId);
        if(activity == null){
            return new SimpleJsonResponse(false, "删除的专题活动不存在！");
        }
        //根据活动专题ID 查找对应的模板数据
        List<WebsiteCustomBlock> websiteCustomBlock = websiteCustomBlockService.findCustomBlock(specialActivityId);
        for(WebsiteCustomBlock block : websiteCustomBlock){
            //删除专题活动下 关联的  模板数据
            WebsiteCustomBlock customBlock = websiteCustomBlockService.findOne(block.getBlockId());
            if(customBlock == null){
                return new SimpleJsonResponse(false, "数据异常！");
            }
            websiteCustomBlockService.delByBlockId(block.getBlockId());
        }
        //删除该活动专题
        specialActivityService.delete(specialActivityId);
        
        return new SimpleJsonResponse(true , "成功删除数据！");
    }
    
}
