package com.lmf.integral.admin.market.controller;

import com.colorfulflorist.smk.market.entity.Cash;
import com.colorfulflorist.smk.market.entity.FenxiaoUser;
import com.colorfulflorist.smk.market.mapper.FenxiaoUserMapper;
import com.colorfulflorist.smk.market.service.CashService;
import com.colorfulflorist.smk.website.entity.WebsiteUsers;
import com.colorfulflorist.smk.website.service.WebsiteUsersService;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.ResponseResult;
import com.lmf.common.util.PagerSpec;
import com.lmf.integral.SystemConfig;
import com.lmf.integral.WechatPayUtil;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.integral.service.WxService;
import com.lmf.integral.task.CloseWaitingPayOrderTask;
import com.lmf.market.repository.vo.CashCriteria;
import com.lmf.market.service.FenXiaoUserService;
import com.lmf.market.vo.CashDTO;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Map;

@Controller("adminCashController")
public class CashController {

	@Autowired
	private CashService cashService;
	
	@Autowired
	private com.lmf.market.service.CashService oldCashService; 

	@Autowired
	private FenxiaoUserMapper fenxiaoUserMapper;
	
	@Autowired
	private FenXiaoUserService fenXiaoUserService;

	@Autowired
	private SystemConfig systemConfig;
	
	private final Logger    logger  = LoggerFactory.getLogger(CloseWaitingPayOrderTask.class);

	@RequestMapping(value = "/admin/cash/pageQuery.php", method = RequestMethod.POST)
	public @ResponseBody ResponseResult pageQuery(
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "level", required = false) Integer level,
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "hierarchy", required = false) Integer hierarchy,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@PagerSpecDefaults(pageSize = 20, maxPageSize = 100, sort = "createTime.desc") PagerSpec page,Model model) {
		page.setCurrentPage(pageNum);
		CashCriteria criteria = new CashCriteria();
		if (StringUtils.isNotBlank(key)) {
			criteria.key = key;
		}
		if (StringUtils.isNotBlank(startTime)) {
			criteria.startTime = startTime+":00";
		}
		if (StringUtils.isNotBlank(endTime)) {
			criteria.endTime = endTime+":59";
		}
		if (level != null && level != 0) {
			criteria.level = level;
		}
		if (status != null ) {
			criteria.status = status;
		}
		if (hierarchy !=null ){
			criteria.hierarchy=hierarchy;
		}
		com.lmf.common.Page<CashDTO> cashes = oldCashService.pageQuery(criteria,page);
		return new ResponseResult("1", "", cashes);

	}

	/**
	 * 提现审核通过
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/admin/cash/permission.php", method = RequestMethod.GET)
	public @ResponseBody ResponseResult permission(
			@RequestParam(value = "id") Integer id,
			HttpServletRequest request) {
		Cash cash = cashService.getById(id);
		String addr = request.getRemoteAddr();
		if (cash == null) {
			return new ResponseResult("-1", "没有查到此单号");
		} else {
			com.lmf.market.entity.FenxiaoUser fenxiaoUser1 = fenXiaoUserService.findByUserId(cash
					.getUserId());
			FenxiaoUser fenxiaoUser = fenxiaoUserMapper.selectById(fenxiaoUser1.getId());
			if (fenxiaoUser == null || fenxiaoUser.getDataFlag() == -1) {
				return new ResponseResult("-1", "没有查到此分销商");
			}
			Map<String, Object> result = WechatPayUtil.wxCashPayResult(
					cash.getAmount(), addr, cash,cash.getOpenId());
			if (result == null || result.get("data") == null) {
				return new ResponseResult("-1", "", result);
			}
			if (result.get("code") == "200") {
				cashService.permission(fenxiaoUser,cash,addr);
				return new ResponseResult("1", "", result);
			} else {
				return new ResponseResult("-1", "", result);
			}
		}
	}

	/**
	 * 提现审核拒绝
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/admin/cash/refuse.php", method = RequestMethod.GET)
	@ResponseBody
	public ResponseResult refuse(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request) {
		Cash cash = cashService.getById(id);
		String addr = request.getRemoteAddr();
		if (cash == null) {
			return new ResponseResult("-1", "没有查到此单号");
		} else {
			com.lmf.market.entity.FenxiaoUser fenxiaoUser1 = fenXiaoUserService.findByUserId(cash
					.getUserId());
			FenxiaoUser fenxiaoUser = fenxiaoUserMapper.selectById(fenxiaoUser1.getId());
			if (fenxiaoUser == null || fenxiaoUser.getDataFlag() == -1) {
				return new ResponseResult("-1", "没有查到此分销商");
			}
			cashService.refuse(fenxiaoUser,cash,addr);
			return new ResponseResult("1", "已驳回");
		}
	}

	/**
	 * 获取用户OPENID
	 * 
	 * @param
	 * @return
	 * @throws WxErrorException
	 */
	@RequestMapping(value = "/openid/redirectUrl.php", method = RequestMethod.GET)
	public String getOpenId(
			@RequestParam(value = "openId") String openId,
			HttpServletRequest request,Model model) throws WxErrorException {
		HttpSession session = request.getSession();
		UserDetail currentUser = (UserDetail) session.getAttribute("currentUser");
		if(currentUser != null){
			com.lmf.market.entity.FenxiaoUser fenxiaoUser1 = fenXiaoUserService.findByUserId(currentUser.getUserId());
			if(fenxiaoUser1 !=null){
				model.addAttribute("userId", fenxiaoUser1.getUserId());
				model.addAttribute("money", fenxiaoUser1.getMoney());
				model.addAttribute("phone", fenxiaoUser1.getMobile());
			}
		}
		model.addAttribute("openId", openId);
		session.setAttribute("openId", openId);
		return "mobile/fenxiao/withdraw";
	}
}
