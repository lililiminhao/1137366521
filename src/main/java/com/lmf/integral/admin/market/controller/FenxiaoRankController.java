package com.lmf.integral.admin.market.controller;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.ResponseResult;
import com.lmf.common.util.PagerSpec;
import com.lmf.market.entity.FenxiaoRank;
import com.lmf.market.entity.FenxiaoUser;
import com.lmf.market.repository.vo.FenxiaoUserCriteria;
import com.lmf.market.service.FenXiaoUserService;
import com.lmf.market.service.FenxiaoRankService;


@Controller("fenxiaoRankController")
public class FenxiaoRankController {

	@Autowired
	private FenxiaoRankService  fenxiaoRankService;

	@Autowired
	private FenXiaoUserService  fenxiaoUserService;

	/**
	 * 新增分销等级
	 * @param rankName
	 * @param percent
	 * @return
	 */
	@RequiresPermissions("fenxiao_rank:create")
	@RequestMapping(value = "/admin/fenxiao/addRank.php", method = RequestMethod.POST)
	public @ResponseBody ResponseResult addRank(@RequestParam(value = "rankName", required = false)String rankName,
												@RequestParam(value = "percent", required = false)Float percent,
												@RequestParam(value = "smkPercent", required = false)Float smkPercent,
												@RequestParam(value = "secondHierarchy",required = false)Float secondHierarchy){
		if(percent > 100){
			return new ResponseResult("-1", "一级分销商佣金比例不能大于100");
		}else if(percent < 0){
			return new ResponseResult("-1", "一级分销商佣金比例不能小于0");
		}
		if(smkPercent > 100){
			return new ResponseResult("-1", "市民卡佣金比例不能大于100");
		}else if(smkPercent < 0){
			return new ResponseResult("-1", "市民卡佣金比例不能小于0");
		}
		if(secondHierarchy > 100){
			return new ResponseResult("-1", "二级分销商佣金比例不能大于100");
		}else if(secondHierarchy < 0){
			return new ResponseResult("-1", "二级分销商佣金比例不能小于0");
		}
		FenxiaoRank fenxiaoRank = fenxiaoRankService.findByName(rankName);
		if(fenxiaoRank!= null){
			return new ResponseResult("-1", "有重复等级名称");
		}
		int result = fenxiaoRankService.addRank(rankName,percent,smkPercent,secondHierarchy);
		if(result == 1){
			return new ResponseResult("1", "新增成功");
		}else {
			return new ResponseResult("-1", "新增失败");
		}
	}

	/**
	 * 编辑分销等级
	 * @param rankName
	 * @param percent
	 * @return
	 */
	@RequiresPermissions("fenxiao_rank:edit")
	@RequestMapping(value = "/admin/fenxiao/editRank.php", method = RequestMethod.POST)
	public @ResponseBody ResponseResult editRank(@RequestParam(value = "rankName", required = false)String rankName,
												 @RequestParam(value = "id", required = false)Integer id,
												 @RequestParam(value = "percent", required = false)Float percent,
												 @RequestParam(value = "smkPercent", required = false)Float smkPercent,
												 @RequestParam(value = "secondHierarchy",required = false)Float secondHierarchy){
		FenxiaoRank rank = fenxiaoRankService.findById(id);
		if(rank == null){
			return new ResponseResult("-1", "无此等级！");
		}
		if(percent > 100){
			return new ResponseResult("-1", "佣金比例不能大于100");
		}else if(percent < 0){
			return new ResponseResult("-1", "佣金比例不能小于0");
		}
		if(secondHierarchy > 100){
			return new ResponseResult("-1", "二级分销商佣金比例不能大于100");
		}else if(secondHierarchy < 0){
			return new ResponseResult("-1", "二级分销商佣金比例不能小于0");
		}
		if(secondHierarchy>percent){
			return new ResponseResult("-1","二级分销商佣金不能大于一级分销商佣金");
		}
		FenxiaoRank fenxiaoRank = fenxiaoRankService.findByName(rankName);
		if(fenxiaoRank != null && fenxiaoRank.getId() != id){
			return new ResponseResult("-1", "有重复等级名称");
		}
		FenxiaoRank newRank = new FenxiaoRank();
		newRank.setId(id);
		newRank.setName(rankName);
		newRank.setPercent(percent);
		newRank.setSmkPercent(smkPercent);
		newRank.setSecondHierarchy(secondHierarchy);
		int result = fenxiaoRankService.editRank(newRank);
		if(result == 1){
			return new ResponseResult("1", "编辑成功");
		}else {
			return new ResponseResult("-1", "编辑失败");
		}
	}

	/**
	 * 删除分销等级
	 * @return
	 */
	@RequiresPermissions("fenxiao_rank:delete")
	@RequestMapping(value = "/admin/fenxiao/delRank.php", method = RequestMethod.GET)
	public @ResponseBody ResponseResult editRank(
			@RequestParam(value = "id", required = false)Integer id
	){
		FenxiaoRank rank = fenxiaoRankService.findById(id);
		List<FenxiaoUser> lists = fenxiaoUserService.findByRank(id);
		if(CollectionUtils.isNotEmpty(lists)){
			return new ResponseResult("-1", "此等级已有分销商使用，不能删除！");
		}
		if(rank == null){
			return new ResponseResult("-1", "无此等级！");
		}
		int result = fenxiaoRankService.delRank(id);
		if(result == 1){
			return new ResponseResult("1", "删除成功");
		}else {
			return new ResponseResult("-1", "删除失败");
		}
	}

	/**
	 * 查询所有等级
	 * @param rankName
	 * @param percent
	 * @return
	 */
	@RequestMapping(value = "/admin/fenxiao/findAllRank.php", method = RequestMethod.GET)
	public @ResponseBody ResponseResult findAll(){
		List<FenxiaoRank> ranks = fenxiaoRankService.findAll();
		if(CollectionUtils.isNotEmpty(ranks)){
			return new ResponseResult("1", "查询成功",ranks);
		}else {
			return new ResponseResult("-1", "查询失败");
		}
	}

	/**
	 * 查看等级
	 * @param rankName
	 * @param percent
	 * @return
	 */
	@RequiresPermissions("fenxiao_rank:view")
	@RequestMapping(value = "/admin/fenxiao/showRank.php", method = RequestMethod.GET)
	public @ResponseBody ResponseResult showRank(
			@RequestParam(value = "id", required = false)Integer id){
		FenxiaoRank ranks = fenxiaoRankService.findById(id);
		if(ranks != null){
			return new ResponseResult("1", "查询成功",ranks);
		}else {
			return new ResponseResult("-1", "查询失败");
		}
	}

	/**分销等级分页查询
	 * @param key
	 * @param startTime
	 * @param endTime
	 * @param rankId
	 * @param pager
	 * @param model
	 * @return
	 */
	@RequiresPermissions("fenxiao_rank:view")
	@RequestMapping(value = "/admin/fenxiao/fenxiaoRankList.php", method = RequestMethod.GET)
	public  @ResponseBody ResponseResult pageQuery(
			@RequestParam(value = "pageNum", required = false)Integer pageNum,
			@PagerSpecDefaults(pageSize = 20, maxPageSize = 100, sort = "createTime.desc") PagerSpec page,
			Model model) {
		page.setCurrentPage(pageNum);
		Page<FenxiaoRank> fenxiaoUser = fenxiaoRankService.pageQuery(page);
		return new ResponseResult("1", "查询成功",fenxiaoUser);
	}
}
