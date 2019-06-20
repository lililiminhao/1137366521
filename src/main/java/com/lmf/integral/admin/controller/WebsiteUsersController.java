package com.lmf.integral.admin.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.colorfulflorist.smk.enterprise.entity.EnterpriseExclusiveProductPoolEntry;
import com.colorfulflorist.smk.enterprise.mapper.EnterpriseExclusiveProductPoolEntryMapper;
import com.colorfulflorist.smk.enterprise.service.EnterpriseExclusiveProductPoolEntryService;
import com.colorfulflorist.smk.website.service.WebsiteUsersService;

/**
 * 这个类是测试mybatis-plus的，并没有什么实际用处
 * @author zhanghongliang
 *
 */
@Controller
public class WebsiteUsersController {
	@Autowired
    private WebsiteUsersService websiteUsersService;
	@Autowired
	private  EnterpriseExclusiveProductPoolEntryService enterpriseExclusiveProductPoolEntryService;
	
	@Autowired
	private  EnterpriseExclusiveProductPoolEntryMapper enterpriseExclusiveProductPoolEntryMapper;
	
	
	@RequestMapping(value = "/admin/websiteUser/test1.php",method = RequestMethod.GET,produces = "text/html;charset=UTF-8")
    public @ResponseBody Object getWebsiteUsersById(){
		//ActiveRecord方式
		EnterpriseExclusiveProductPoolEntry ob = new  EnterpriseExclusiveProductPoolEntry();
		EnterpriseExclusiveProductPoolEntry entry = ob.selectById(789);
		//Service方式
		EnterpriseExclusiveProductPoolEntry entryService= enterpriseExclusiveProductPoolEntryService.getById(789);
		//WebsiteUsers users = websiteUsersService.getById(178924L);
		//Mapper方式
		EnterpriseExclusiveProductPoolEntry entryMapper = enterpriseExclusiveProductPoolEntryMapper.selectById(789);
		
		Page<EnterpriseExclusiveProductPoolEntry> page = new Page<>(1,20);
		QueryWrapper<EnterpriseExclusiveProductPoolEntry> queryWrapper = new QueryWrapper<EnterpriseExclusiveProductPoolEntry>().gt("id", 789L);
		IPage<EnterpriseExclusiveProductPoolEntry> pageResult = enterpriseExclusiveProductPoolEntryService.page(page, queryWrapper);
		
		Map<String,Object> map = new HashMap<>();
		map.put("entry", entry);
		map.put("entryService", entryService);
		map.put("entryMapper", entryMapper);
		map.put("pageResult", pageResult);
        return map;
    }
}
