package com.lmf.integral.admin.market.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.ResponseResult;
import com.lmf.common.util.PagerSpec;
import com.lmf.enterprise.entity.EnterprisePackageVoucher;
import com.lmf.enterprise.entity.EnterpriseUserMap;
import com.lmf.enterprise.service.EnterprisePackageVoucherService;
import com.lmf.enterprise.service.EnterpriseUserMapService;
import com.lmf.enterprise.vo.EnterpriseEmployeeVoucherCriteria;
import com.lmf.website.entity.UserDefinedCate;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.service.UserDefinedCateService;

@Controller
public class UserVoucherController {
	
	private SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private EnterprisePackageVoucherService enterprisePackageVoucherService;

	@Autowired
	private EnterpriseUserMapService enterpriseUserMapService;
	
	@Autowired
	private UserDefinedCateService userDefinedCateService;

	@RequestMapping(value = "/my/mobile/listVouchers.php", method = RequestMethod.GET)
	public @ResponseBody ResponseResult listVouchers(WebsiteUser websiteUser, Model model, HttpServletRequest request,
			@PagerSpecDefaults(pageSize = 1000, sort = "id.desc") PagerSpec pager) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		EnterpriseEmployeeVoucherCriteria criteria = new EnterpriseEmployeeVoucherCriteria();
		criteria.andDataFlagEquals(1);
		if(websiteUser==null||websiteUser.getId()==null) {
			return new ResponseResult("-1", "无法获取用户信息", null);
		}
		EnterpriseUserMap map = enterpriseUserMapService.getOneByUserId(websiteUser.getId());
		if(map==null) {
			return new ResponseResult("-1", "无法找到用户企业映射", null);
		}
		criteria.andEmployeeIdEquals(map.getEnterpriseEmployeeId());
		Page<EnterprisePackageVoucher> enterprisePackageVouchers = enterprisePackageVoucherService.selectByPage(pager,
				criteria.setOrderBy("id desc"));
		List<Map> list = new ArrayList<Map>();
		if(enterprisePackageVouchers!=null && CollectionUtils.isNotEmpty(enterprisePackageVouchers.getContent()))
		for (EnterprisePackageVoucher enterprisePackageVoucher : enterprisePackageVouchers.getContent()) {
			if (enterprisePackageVoucher.getStatus() != null && enterprisePackageVoucher.getStatus().intValue() == -1) {
				enterprisePackageVoucher.setStatus(2);
			} else if (enterprisePackageVoucher.getStatus() != null
					&& enterprisePackageVoucher.getStatus().intValue() == 1) {
				Date now = new Date();
				if (enterprisePackageVoucher.getBeginTime().after(now)
						&& enterprisePackageVoucher.getEndTime().after(now)) {
					enterprisePackageVoucher.setStatus(-1);
				}
				if (enterprisePackageVoucher.getBeginTime().before(now)
						&& enterprisePackageVoucher.getEndTime().after(now)) {
					enterprisePackageVoucher.setStatus(0);
				}
				if (enterprisePackageVoucher.getBeginTime().before(now)
						&& enterprisePackageVoucher.getEndTime().before(now)) {
					enterprisePackageVoucher.setStatus(1);
				}
			}
			Map map1 = BeanUtils.describe(enterprisePackageVoucher);
			
			/**
			 * 分类名称
			 */
			Page<UserDefinedCate> catList = userDefinedCateService.find(enterprisePackageVoucher.getPackageName(), true, null);
			Integer wpt = null;
			if(CollectionUtils.isNotEmpty(catList.getContent())) {
				for (UserDefinedCate userDefinedCate : catList.getContent()) {
					if(userDefinedCate.getParentId()!=null&&userDefinedCate.getParentId().intValue()>0) {
						wpt = userDefinedCate.getId();
						break;
					}
				}
			}
			
			map1.put("beginTime", f.format(enterprisePackageVoucher.getBeginTime()));
			map1.put("endTime", f.format(enterprisePackageVoucher.getEndTime()));
			map1.put("productPoolId", map.getProductPoolId());
			map1.put("wpt", wpt);
			list.add(map1);
		}
		return new ResponseResult("1", "查询成功", list);
	}

	/**
	 * @return 提货券列表页面映射
	 */
	@RequestMapping(value = "/my/mobile/catchGoods.php", method = RequestMethod.GET)
	public String catchGoods() {
		return "/mobile/user_center/tigoods";
	}
}
