package com.lmf.integral.admin.enterprise.controller;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.multipart.MultipartFile;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.PagerSpec;
import com.lmf.common.util.ValidUtil;
import com.lmf.enterprise.entity.Enterprise;
import com.lmf.enterprise.entity.EnterpriseEmployee;
import com.lmf.enterprise.entity.EnterprisePackageVoucher;
import com.lmf.enterprise.service.EnterpriseEmployeeService;
import com.lmf.enterprise.service.EnterprisePackageVoucherService;
import com.lmf.enterprise.service.EnterpriseService;
import com.lmf.enterprise.service.EnterpriseUserMapService;
import com.lmf.enterprise.vo.EnterpriseEmployeeCriteria;
import com.lmf.enterprise.vo.EnterpriseEmployeeVoucherCriteria;
import com.lmf.website.entity.UserDefinedCate;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteAdministrator;
import com.lmf.website.service.UserDefinedCateService;

import jxl.Sheet;
import jxl.Workbook;

@Controller("enterpriseEmployeeVoucherController")
public class EnterpriseEmployeeVoucherController {
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
	private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private EnterpriseEmployeeService enterpriseEmployeeService;

	@Autowired
	private EnterprisePackageVoucherService enterprisePackageVoucherService;
	
	@Autowired
	private EnterpriseUserMapService enterpriseUserMapService;
	
	@Autowired
    private EnterpriseService enterpriseService;
	
	@Autowired
	private UserDefinedCateService userDefinedCateService;

	/**
	 * 提货券列表
	 * @param pager
	 * @param key
	 * @param enterpriseId
	 * @param status
	 * @param beginTime
	 * @param endTime
	 * @param model
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequiresPermissions("voucher:view")
	@RequestMapping(value = "/admin/enterpriseEmployeeVoucher.php", method = RequestMethod.GET)
	public String list(@PagerSpecDefaults(pageSize = 20, sort = "id.desc") PagerSpec pager,
			@RequestParam(value = "key", required = false) String key,
			//所属企业
			@RequestParam(value = "enterpriseId", required = false) Integer enterpriseId,
			//提货券状态
			@RequestParam(value = "status", required = false) Integer status,
			//开始时间
			@RequestParam(value = "beginTime", required = false) Date beginTime,
			//结束时间
			@RequestParam(value = "endTime", required = false) Date endTime,
			Model model)
			throws UnsupportedEncodingException {
		StringBuilder link = new StringBuilder("/jdvop/admin/enterpriseEmployeeVoucher.php?page=[:page]");

		EnterpriseEmployeeVoucherCriteria criteria = new EnterpriseEmployeeVoucherCriteria();
		boolean processOr = false;
		if (StringUtils.isNotBlank(key)) {
			criteria.or().andEmployeeNameLike(key).or().andEmployeePhoneLike(key).or().andTicketNameLike(key);
			String str = criteria.toString(false);
			str = str.replace("WHERE ", "");
			str = " (" + str + ") ";
			criteria.where = new StringBuilder(str);
			link.append("&key=").append(URLEncoder.encode(key, "UTF-8"));
            model.addAttribute("key", key);
            processOr = true;
		}
		boolean replace = false;
		if(enterpriseId!=null&&enterpriseId.intValue()>0) {
			replace = true;
			criteria.andEnterpriseIdEquals(enterpriseId);
			link.append("&enterpriseId=").append(enterpriseId);
            model.addAttribute("enterpriseId", enterpriseId);
		}
		
		if(status!=null&&status.intValue()!=-100) {
			link.append("&status=").append(status);
            model.addAttribute("status", status);
		}
		//状态筛选status   -1 未开始 0未使用  1已过期  2已使用
		if(status!=null&&status.intValue()==2) {
			replace = true;
			criteria.andStatusEquals(-1);
		}else {
			if(status!=null&&status.intValue()==-1) {
				//未开始
				replace = true;
				Date now = new Date();
				criteria.andStatusEquals(1);
				criteria.andBeginTimeGreaterThen(now);
				criteria.andEndTimeGreaterThen(now);
			}
			if(status!=null&&status.intValue()==0) {
				//未使用
				replace = true;
				Date now = new Date();
				criteria.andStatusEquals(1);
				criteria.andBeginTimeLessEqualsThen(now);
				criteria.andEndTimeGreaterThen(now);
			}
			if(status!=null&&status.intValue()==1) {
				//已过期
				replace = true;
				Date now = new Date();
				criteria.andStatusEquals(1);
				criteria.andBeginTimeLessThen(now);
				criteria.andEndTimeLessThen(now);
			}
		}
		
		
		if(beginTime!=null) {
			replace = true;
			criteria.andBeginTimeLessEqualsThen(beginTime);
			link.append("&beginTime=").append(simpleDateFormat2.format(beginTime));
            model.addAttribute("beginTime", simpleDateFormat2.format(beginTime));
            //同时设置结束时间
            criteria.andEndTimeGreaterThenForPlusDay(beginTime);
		}
		
		if(endTime!=null) {
			replace = true;
			criteria.andEndTimeGreaterThenForPlusDay(endTime);
			link.append("&endTime=").append(simpleDateFormat2.format(endTime));
            model.addAttribute("endTime", simpleDateFormat2.format(endTime));
            if(beginTime==null) {
            	criteria.andBeginTimeLessEqualsThen(endTime);
            }
		}
		
		criteria.andDataFlagEquals(1);
		
		String sss = criteria.toString(false);
		if(replace&&processOr) {
			sss = sss.replace("WHERE ", "");
			if(sss.endsWith(")")) {
				sss=sss.substring(0, sss.length()-1);
			}
			criteria.isMultiple=false;
			criteria.where = new StringBuilder(sss);
		}
		
		Page<EnterprisePackageVoucher> enterprisePackageVouchers = enterprisePackageVoucherService.selectByPage(pager,
				criteria.setOrderBy("id desc"));
		for (EnterprisePackageVoucher enterprisePackageVoucher : enterprisePackageVouchers) {
			 if(enterprisePackageVoucher.getStatus()!=null&&enterprisePackageVoucher.getStatus().intValue()==-1) {
				 enterprisePackageVoucher.setStatus(2);
			 }else if(enterprisePackageVoucher.getStatus()!=null&&enterprisePackageVoucher.getStatus().intValue()==1){
				 Date now = new Date();
				 if(enterprisePackageVoucher.getBeginTime().after(now)&&enterprisePackageVoucher.getEndTime().after(now)) {
					 enterprisePackageVoucher.setStatus(-1);
				 }
				 if(enterprisePackageVoucher.getBeginTime().before(now)&&enterprisePackageVoucher.getEndTime().after(now)) {
					 enterprisePackageVoucher.setStatus(0);
				 }
				 if(enterprisePackageVoucher.getBeginTime().before(now)&&enterprisePackageVoucher.getEndTime().before(now)) {
					 enterprisePackageVoucher.setStatus(1);
				 }
			 }
		}
		List<Enterprise> enterpriseList = enterpriseService.list(null);
        model.addAttribute("enterpriseList",enterpriseList);
		model.addAttribute("enterpriseService",enterpriseService);
		model.addAttribute("enterprisePackageVouchers", enterprisePackageVouchers);
		model.addAttribute("link",link.toString());
		model.addAttribute("simpleDateFormat2",simpleDateFormat2);
		
		return "/admin/enterprise/enterprise_employee_voucher/list";
	}
	
    //删除
    @ResponseBody
    @RequiresPermissions("voucher:delete")
    @RequestMapping(value = "/admin/enterpriseEmployeeVoucher/delete.php", method = RequestMethod.GET)
    public SimpleJsonResponse<String> deletePost(@RequestParam(value = "id", required = false)Long id,
                                              Model model) {
    	enterprisePackageVoucherService.removeById(id);
        return new SimpleJsonResponse<>(true, null);
    }
    
    //批量导入页面
    @RequiresPermissions("voucher:import")
    @RequestMapping(value = "/admin/enterpriseEmployeeVoucher/batchInsert.php", method = RequestMethod.GET)
    public String batchInsert(WebsiteAdministrator admin, Model model) {

        List<Enterprise> enterpriseList = enterpriseService.list(null);

        model.addAttribute("enterpriseList",enterpriseList);
        model.addAttribute("admin", admin);
        return "admin/enterprise/enterprise_employee_voucher/batch_insert";
    }

	// 批量导入提货券
    @RequiresPermissions("voucher:import")
	@RequestMapping(value = "/admin/enterpriseEmployeeVoucher/batchInsert.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public @ResponseBody SimpleJsonResponse batchShipment(@RequestParam(value = "excelFile") MultipartFile file,
			WebsiteAdministrator admin, @RequestParam(value = "id", required = false) Integer enterpriseId,
			Website website, HttpServletRequest request) {
		Map<String, Object> msgMap = new HashMap<>();
		List<Map<String, String>> failMsgList = new ArrayList<>();
		int successCount = 0;
		try (InputStream ins = file.getInputStream()) {
			Workbook workbook = Workbook.getWorkbook(ins);
			int sheetSize = workbook.getNumberOfSheets();
			for (int i = 0; i < sheetSize; ++i) {
				Sheet sheet = workbook.getSheet(i);
				int rows = sheet.getRows();
				// 编号
				// String serialNumber = "";
				// 员工姓名
				String employeeName = "";
				// *员工手机号
				String mobileNO = "";
				// *套餐名称（即企业专区下属的二级分类名称）
				String packageName = "";
				// *提货券名称
				String voucherName = "";
				// *数量
				Integer num = 0;
				// *提货券开始时间
				String startTime = "";
				// *提货券截止时间
				String endTime = "";

				// 循环取出单元格的内容
				for (int r = 1; r < rows; ++r) {
					Map<String, String> failMsg = new HashMap<>();
					try {
						employeeName = sheet.getCell(1, r).getContents();// 员工姓名
						mobileNO = sheet.getCell(2, r).getContents();// *员工手机号
						packageName = sheet.getCell(3, r).getContents();// *套餐名称（即企业专区下属的二级分类名称）
						voucherName = sheet.getCell(4, r).getContents();// *提货券名称
						String strNum = sheet.getCell(5, r).getContents();
						startTime = sheet.getCell(6, r).getContents();// *提货券开始时间
						endTime = sheet.getCell(7, r).getContents();// *提货券截止时间
						if (StringUtils.isBlank(mobileNO) && StringUtils.isBlank(packageName)
								&& StringUtils.isBlank(voucherName) && StringUtils.isBlank(strNum)
								&& StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime)) {
							continue;
						}
						// 数据异常跳过
						boolean result = valueCheck(mobileNO, "mobileNO", true, failMsg, "员工手机号不能为空");
						result = result && valueCheck(packageName, "packageName", false, failMsg, "套餐名称不能为空");
						result = result && valueCheck(voucherName, "voucherName", false, failMsg, "提货券名称不能为空");
						result = result && valueCheck(strNum, "strNum", false, failMsg, "数量不能为空");
						result = result && valueCheck(startTime, "startTime", false, failMsg, "提货券开始时间不能为空");
						result = result && valueCheck(endTime, "endTime", false, failMsg, "提货券截止时间不能为空");
						
						if(StringUtils.isNotBlank(packageName)) {
							Page<UserDefinedCate> catList = userDefinedCateService.find(packageName, true, null);
							String msgExist = StringUtils.isBlank(failMsg.get("msg")) ? "" : failMsg.get("msg");
							boolean pass = false;
							if(CollectionUtils.isNotEmpty(catList.getContent())) {
								for (UserDefinedCate userDefinedCate : catList.getContent()) {
									if(userDefinedCate.getParentId()!=null&&userDefinedCate.getParentId().intValue()>0) {
										pass = true;
										break;
									}
								}
							}
							if(!pass) {
								String msg = "不存在和套餐名称相同的二级分类";
								failMsg.put("msg", StringUtils.isBlank(msgExist) ? msg : msgExist + " " + msg);
								result = false;
							}
						}
						if (!result) {
							failMsgList.add(failMsg);
							continue;
						}
						strNum = strNum.trim();
						if (StringUtils.isNotBlank(strNum) && StringUtils.isNumeric(strNum)) {
							num = Integer.valueOf(strNum);// *数量
							if (num.intValue() <= 0) {
								failMsg.put("msg", "填写的数量必须大于0");
								failMsgList.add(failMsg);
								continue;
							}
						} else {
							// 如果数字不正确直接忽略
							failMsg.put("msg", "填写的数量格式不正确");
							failMsgList.add(failMsg);
							continue;
						}
						EnterprisePackageVoucher enterpriseEmployeeVoucher = new EnterprisePackageVoucher();
						try {
							Date s = simpleDateFormat.parse(startTime);
							enterpriseEmployeeVoucher.setBeginTime(s);
						} catch (Exception e) {
							failMsg.put("msg", "提货券开始时间格式错误！例子：2018.10.10");
							continue;
						}
						try {
							Date e = simpleDateFormat.parse(endTime);
							long time = e.getTime()+1000*(3600*24-1);
							Date saveTime = new Date(time);
							enterpriseEmployeeVoucher.setEndTime(saveTime);
						} catch (Exception e) {
							failMsg.put("msg", "提货券截止时间格式错误！例子：2018.10.10");
							continue;
						}

						enterpriseEmployeeVoucher.setDataFlag(1);
						enterpriseEmployeeVoucher.setStatus(1);
						enterpriseEmployeeVoucher.setEmployeeName(employeeName);
						enterpriseEmployeeVoucher.setUseable(1);
						enterpriseEmployeeVoucher.setUsed(0);
						enterpriseEmployeeVoucher.setPackageName(packageName);
						enterpriseEmployeeVoucher.setTicketName(voucherName);
						enterpriseEmployeeVoucher.setQuantity(1);
						enterpriseEmployeeVoucher.setCreateTime(new Date());
						enterpriseEmployeeVoucher.setEmployeePhone(mobileNO);

						EnterpriseEmployee enterpriseEmployee = enterpriseEmployeeService.selectOneByMobile(mobileNO);
						if (enterpriseEmployee == null) {
							failMsg.put("msg", "此号码" + mobileNO + "找不到相关手机的企业用户，请确认已经导入企业用户！");
							failMsgList.add(failMsg);
							continue;
						}
						if(!StringUtils.equals(employeeName, enterpriseEmployee.getName())) {
							failMsg.put("msg", "员工姓名不一致，员工表姓名为：" + enterpriseEmployee.getName());
							failMsgList.add(failMsg);
							continue;
						}
						enterpriseEmployeeVoucher.setEmployeeId(enterpriseEmployee.getId());
						if(enterpriseEmployee.getEnterpriseId()!=null&&enterpriseId!=null
								&& enterpriseEmployee.getEnterpriseId().intValue()!=enterpriseId.intValue()) {
							failMsg.put("msg", "此用户非选择的企业下属员工，请确认员工的所属企业！");
							failMsgList.add(failMsg);
							continue;
						}
						enterpriseEmployeeVoucher.setEnterpriseId(enterpriseId);
						for (int k = 0; k < num; k++) {
							enterprisePackageVoucherService.insert(enterpriseEmployeeVoucher);
							successCount++;
						}
					} catch (Exception e) {
						failMsg.put("msg", "企业员工添加失败");
						failMsgList.add(failMsg);
						continue;
					}
				}
			}
			msgMap.put("successCount", successCount);
			msgMap.put("failMsgList", failMsgList);
		} catch (Exception e) {
			return new SimpleJsonResponse<>(false, "您上传的文件不是合法的Excel2003~2007格式，请检查后缀名是否为.xls");
		}
		return new SimpleJsonResponse<>(true, msgMap);
	}

	private boolean valueCheck(String value, String cloumnName, boolean isMobile, Map<String, String> failMsg,
			String msg) {
		failMsg.put(cloumnName, value);
		String msgExist = StringUtils.isBlank(failMsg.get("msg")) ? "" : failMsg.get("msg");
		if (StringUtils.isBlank(value)) {
			failMsg.put("msg", StringUtils.isBlank(msgExist) ? msg : msgExist + " " + msg);
			return false;
		}
		if (isMobile) {
			if (!ValidUtil.isMobile(value)) {
				failMsg.put("msg", StringUtils.isBlank(msgExist) ? "手机号码格式不正确！" : msgExist + " " + "手机号码格式不正确！");
				return false;
			}
		}
		return true;
	}
}
