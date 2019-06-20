package com.lmf.integral.admin.market.controller;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.ResponseResult;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.PagerSpec;
import com.lmf.common.util.ValidUtil;
import com.lmf.market.entity.FenxiaoRank;
import com.lmf.market.entity.FenxiaoUser;
import com.lmf.market.entity.Relationship;
import com.lmf.market.repository.RelationshipDao;
import com.lmf.market.repository.vo.FenxiaoUserCriteria;
import com.lmf.market.service.FenXiaoOrderService;
import com.lmf.market.service.FenXiaoProductService;
import com.lmf.market.service.FenXiaoUserService;
import com.lmf.market.service.FenxiaoRankService;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteUser;
import com.lmf.website.service.WebsiteUserService;

import jxl.Sheet;
import jxl.Workbook;

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

import javax.servlet.http.HttpServletRequest;

import java.io.InputStream;
import java.util.*;

@Controller("fenXiaoUserController")
public class FenXiaoUserController {

	
	@Autowired
	private FenXiaoProductService fenXiaoProductService; 
	
	@Autowired
	private FenXiaoOrderService fenXiaoOrderService; 
	
	@Autowired
	private FenXiaoUserService fenXiaoUserService;
	
	@Autowired
	private WebsiteUserService websiteUserService;
	
	@Autowired
	private FenxiaoRankService fenxiaoRankService;

	@Autowired
	private RelationshipDao relationshipDao;
	
	/**
	 * 新增分销商
	 * @param
	 * @param
	 * @return
	 */
	@RequiresPermissions("fenxiao_user:create")
	@RequestMapping(value = "/admin/fenxiao/addUser.php", method = RequestMethod.POST)
	public @ResponseBody ResponseResult addFenxiaoUser(@RequestParam(value = "userName", required = false)String userName,
			@RequestParam(value = "mobile", required = false)String mobile,
			@RequestParam(value = "rankId", required = false)Integer rankId){
		if(rankId == null || rankId == 0){
			return new ResponseResult("-1", "请选择分销等级！");
		}
		FenxiaoUser user2 = fenXiaoUserService.findByMobile(mobile);
		if(user2 != null&&user2.getStatus()!=-1&&user2.getDataFlag()!=-1){
            return new ResponseResult("-1", "该手机号码已被使用！");
        }
        FenxiaoUser user3 = fenXiaoUserService.findByMobileDel(mobile);
        if(user3!=null){
			int result1=fenXiaoUserService.bindFenxiaoUser1(user3.getId(),user3.getRankId());
            if(result1==1){
                return new ResponseResult("1", "新增成功");
            }else{
                return new ResponseResult("-1", "新增失败");
            }
		}
		FenxiaoUser user = new FenxiaoUser();
		user.setCreateTime(new Date());
		user.setFreezeMoney(0.00);
		user.setMobile(mobile);
		user.setMoney(0.00);
		user.setRankId(rankId);
		user.setStatus(0);
		user.setParentID(0);
		user.setUserName(userName);
		
		WebsiteUser websiteUser = websiteUserService.findByMobile(mobile);
		if(websiteUser==null) {
			return new ResponseResult("-1", "新增失败,手机号码对应的账号不存在！");
		}
		user.setUserId(websiteUser.getId().intValue());
		int result = fenXiaoUserService.addFenxiaoUser(user);
		if(result == 1){
			return new ResponseResult("1", "新增成功");
		}else {
			return new ResponseResult("-1", "新增失败");
		}
	}
	
	/**
	 * 编辑分销商
	 * @param
	 * @param
	 * @return
	 */
	@RequiresPermissions("fenxiao_user:edit")
	@RequestMapping(value = "/admin/fenxiao/editUser.php", method = RequestMethod.POST)
	public @ResponseBody ResponseResult editFenxiaoUser(@RequestParam(value = "userName", required = false)String userName,
			@RequestParam(value = "mobile", required = false)String mobile,
			@RequestParam(value = "id", required = false)Integer id,
			@RequestParam(value = "rankId", required = false)Integer rankId){
//		if(ValidUtil.isMobile(mobile)){
//			return new ResponseResult("-1", "手机号码格式不正确");
//		}
		if(rankId == null || rankId == 0){
			return new ResponseResult("-1", "请选择分销等级！");
		}
		FenxiaoUser user2 = fenXiaoUserService.findByMobile(mobile);
		if(user2 != null && user2.getId().intValue() != id.intValue()){
			return new ResponseResult("-1", "该手机号码已被使用！");
		}
		FenxiaoUser user = fenXiaoUserService.findById(id);
		if(user == null){
			return new ResponseResult("-1", "查无此用户！");
		}
		user.setMobile(mobile);
		user.setRankId(rankId);
		user.setUserName(userName);
		int result = fenXiaoUserService.editFenxiaoUser(user);
		if(result == 1){
			return new ResponseResult("1", "修改成功");
		}else {
			return new ResponseResult("-1", "修改失败");
		}
	}
	
	
	/**
	 * 删除分销商
	 * @param
	 * @param
	 * @return
	 */
	@RequiresPermissions("fenxiao_user:delete")
	@RequestMapping(value = "/admin/fenxiao/delUser.php", method = RequestMethod.POST)
	public @ResponseBody ResponseResult delFenxiaoUser(
			@RequestParam(value = "id", required = false)Integer id
			){
		int result = fenXiaoUserService.deleteFenxiaoUser(id);
		FenxiaoUser user=fenXiaoUserService.findById(id);
		if(user.getParentID()==0){
			List<Relationship>list=relationshipDao.findbyfenxiaoID(user.getUserId());
			for(Relationship r:list){
				relationshipDao.deletebyfenxiaoID(r.getUserID().intValue());
				relationshipDao.deletebyfenxiaoID(r.getFenxiaoID());
			}
		}else{
			relationshipDao.deletebyuserID(user.getUserId());
			List<Relationship>list=relationshipDao.findbyfenxiaoID(user.getUserId());
			for(Relationship r:list){
				relationshipDao.deletebyuserID(r.getUserID().intValue());
			}
		}
		if(result == 1){
			return new ResponseResult("1", "删除成功");
		}else {
			return new ResponseResult("-1", "删除失败");
		}
	}
	
	/**
	 * 查看分销商
	 * @param
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/admin/fenxiao/showUser.php", method = RequestMethod.GET)
	public @ResponseBody ResponseResult showFenxiaoUser(
			@RequestParam(value = "id", required = false)Integer id
			){
		FenxiaoUser user = fenXiaoUserService.findById(id);
		if(user != null){
			return new ResponseResult("1", "查询成功",user);
		}else {
			return new ResponseResult("-1", "查询失败");
		}
	}
	
	/**
	 * 解绑分销商
	 * @param
	 * @param
	 * @return
	 */
	@RequiresPermissions("fenxiao_user:unbind")
	@RequestMapping(value = "/admin/fenxiao/unBindFenxiaoUser.php", method = RequestMethod.GET)
	public @ResponseBody ResponseResult unBindFenxiaoUser(
			@RequestParam(value = "id", required = false)Integer id
			){
		FenxiaoUser user = fenXiaoUserService.findById(id);
		
		if(user == null){
			return new ResponseResult("-1", "查询失败");
		}
		int result =fenXiaoUserService.unBindFenxiaoUser(id);
		if(result==1){
			return new ResponseResult("1", "解绑成功");
		}else {
			return new ResponseResult("-1", "解绑失败");
		}
	}
	
	
	/**
	 * @return
	 * 新增分销商页面映射
	 */
	@RequestMapping(value = "/admin/website/addFenxiao.php", method = RequestMethod.GET)
    public String addSaler(@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "watch", required = false) String watch,Model model) {
		model.addAttribute("id",id);
		model.addAttribute("watch",watch);
		return "/admin/website/fenxiao/add_fenxiao";
    }
	
	/**
	 * @return
	 * 导入分销商页面映射
	 */
	@RequestMapping(value = "/admin/website/import_out_fenxiao.php", method = RequestMethod.GET)
    public String importSaler() {
    	
		return "/admin/website/fenxiao/importInto_fenxiao";
    }
	
	/**
	 * @return
	 * 分销商佣金页面映射
	 */
	@RequestMapping(value = "/admin/website/admin_fenxiao_money.php", method = RequestMethod.GET)
    public String fenxiao_money() {
    	
		return "/admin/statistics/fenxiao/fenxiao_money";
    }
    //分销提现页面映射
    @RequestMapping(value = "/admin/website/admin_tixian_money.php", method = RequestMethod.GET)
    public String tixian_money() {

		return "/admin/statistics/fenxiao/tixian_money";
    }
	
	/**
	 * @return
	 * 市民卡佣金页面映射
	 */
	@RequestMapping(value = "/admin/website/admin_smk_money.php", method = RequestMethod.GET)
    public String smk_money() {
    	
		return "/admin/statistics/fenxiao/smk_money";
    }
	
	/**
	 * @return
	 * 分销商列表页面映射
	 */
	@RequestMapping(value = "/admin/website/list_fenxiao.php", method = RequestMethod.GET)
    public String salerList() {
		return "/admin/website/fenxiao/list";
    }
	

	/**
	 * @return
	 * 分销等级列表页面映射
	 */
	@RequestMapping(value = "/admin/website/list_fenxiao_rank.php", method = RequestMethod.GET)
	public String fenxiao_rank(){
		return "/admin/website/fenxiao_rank/list";
	}
	
	/**
	 * @return
	 * 新增分销等级页面映射
	 */
	@RequestMapping(value = "/admin/website/form_fenxiao_rank.php", method = RequestMethod.GET)
	public String form_fenxiao_rank(@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "watch", required = false) String watch,Model model){
		model.addAttribute("id",id);
		model.addAttribute("watch",watch);
		return "/admin/website/fenxiao_rank/form";
	}
	
	/**
	 * 分页查询分销商
	 * @param key
	 * @param startTime
	 * @param endTime
	 * @param rankId
	 * @param model
	 * @return
	 */
	@RequiresPermissions("fenxiao_user:view")
	@RequestMapping(value = "/admin/fenxiao/fenxiaoUserList.php", method = RequestMethod.POST)
	public @ResponseBody ResponseResult pageQuery(
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestParam(value = "rankId", required = false) Integer rankId,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@PagerSpecDefaults(pageSize = 20, maxPageSize = 100, sort = "createTime.desc") PagerSpec page,
			Model model) {
		page.setCurrentPage(pageNum);
		FenxiaoUserCriteria criteria = new FenxiaoUserCriteria();
		if (StringUtils.isNotBlank(key)) {
			criteria.key = key;
		}
		if (StringUtils.isNotBlank(startTime)) {
			criteria.startTime = startTime+":00";
		}
		if (StringUtils.isNotBlank(endTime)) {
			criteria.endTime = endTime+":59";
		}
		if (rankId != null && rankId != 0) {
			criteria.rankId = rankId;
		}
		Page<FenxiaoUser> fenxiaoUser = fenXiaoUserService.pageQuery(criteria,page);
    	return new ResponseResult("1", "查询成功",fenxiaoUser);
	}
	
	//批量导入分销商
	@RequiresPermissions("fenxiao_user:import")
    @RequestMapping(value = "/admin/fenxiao/batchInsert.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse batchShipment(@RequestParam(value = "excelFile") MultipartFile file,
                                                          Website website,
                                                          HttpServletRequest request) {
        Map<String,Object> msgMap = new HashMap<>();
        List<Map> failMsgList = new ArrayList<>();
        int successCount = 0;
        try(InputStream ins = file.getInputStream()){
            Workbook workbook = Workbook.getWorkbook(ins);
            int sheetSize = workbook.getNumberOfSheets();
            for(int i = 0; i < sheetSize; ++i){
                Sheet sheet = workbook.getSheet(i);
                int rows = sheet.getRows();
                int columns = sheet.getColumns();
                String mobile = "";
                String employeeName = "";
                String rankName = "";
                //循环取出单元格的内容
                for(int r = 1; r < rows; ++r){
                    Map<String,String> failMsg = new HashMap<>();
                    try{
                        employeeName = sheet.getCell(1, r).getContents();//员工姓名
                        mobile = sheet.getCell(2, r).getContents(); //员工手机号码
                        rankName = sheet.getCell(3, r).getContents(); //员工分销等级
                        //数据异常跳过
                        if((employeeName == null || employeeName.isEmpty()) && (mobile == null || mobile.isEmpty())){
                            continue;
                        }
                        if(mobile == null || mobile.isEmpty()){
                            failMsg.put("name",employeeName);
                            failMsg.put("mobile",mobile);
                            failMsg.put("rankName",rankName);
                            failMsg.put("msg","手机号码不能为空");
                            failMsgList.add(failMsg);
                            continue;
                        }
                        if(employeeName == null || employeeName.isEmpty()){
                            failMsg.put("name",employeeName);
                            failMsg.put("mobile",mobile);
                            failMsg.put("rankName",rankName);
                            failMsg.put("msg","用户名不能为空");
                            failMsgList.add(failMsg);
                            continue;
                        }
                        if(rankName == null || rankName.isEmpty()){
                            failMsg.put("name",employeeName);
                            failMsg.put("mobile",mobile);
                            failMsg.put("rankName",rankName);
                            failMsg.put("msg","分销等级不能为空");
                            failMsgList.add(failMsg);
                            continue;
                        }
                        if(!ValidUtil.isMobile(mobile)) {
                            failMsg.put("name",employeeName);
                            failMsg.put("mobile",mobile);
                            failMsg.put("rankName",rankName);
                            failMsg.put("msg","错误的手机号码格式");
                            failMsgList.add(failMsg);
                            continue;
                        }
                        //用户已存在
                        Boolean exists = fenXiaoUserService.exists(mobile);
                        if(exists){
                            failMsg.put("name",employeeName);
                            failMsg.put("mobile",mobile);
                            failMsg.put("rankName",rankName);
                            failMsg.put("msg","分销用户已存在");
                            failMsgList.add(failMsg);
                            continue;
                        }
                        FenxiaoRank rank = fenxiaoRankService.findByName(rankName);
                        if(rank == null){
                        	 failMsg.put("name",employeeName);
                             failMsg.put("mobile",mobile);
                             failMsg.put("rankName",rankName);
                             failMsg.put("msg","分销等级不存在或已经删除");
                             failMsgList.add(failMsg);
                             continue;
                        }
                        FenxiaoUser user1=fenXiaoUserService.findByMobileDel(mobile);
                        if(user1!=null){
                            int result1=fenXiaoUserService.bindFenxiaoUser1(user1.getId(),rank.getId());
							successCount++;
							if(result1 ==1){
								msgMap.put("successCount",successCount);
							}else{
								failMsg.put("msg","导入分销商失败!");
								failMsgList.add(failMsg);
								msgMap.put("failMsgList",failMsgList);
							}
                        }else{
                            FenxiaoUser user = new FenxiaoUser();
                            user.setCreateTime(new Date());
                            user.setFreezeMoney(0.00);
                            user.setUserName(employeeName);
                            user.setParentID(0);
                            WebsiteUser websiteUser = websiteUserService.findByMobile(mobile);
                            if(websiteUser==null) {
                                failMsg.put("name",employeeName);
                                failMsg.put("mobile",mobile);
                                failMsg.put("rankName",rankName);
                                failMsg.put("msg","找不到手机号对应的网站用户");
                                failMsgList.add(failMsg);
                                continue;
                            }
                            user.setMobile(mobile);
                            user.setMoney(0.00);
                            user.setRankId(rank.getId());
                            user.setStatus(0);
                            user.setUserId(websiteUser.getId().intValue());
                            fenXiaoUserService.addFenxiaoUser(user);
                            successCount++;
                        }

                    }catch (Exception e) {
                        failMsg.put("name",employeeName);
                        failMsg.put("mobile",mobile);
                        failMsg.put("rankName",rankName);
                        failMsg.put("msg","分销用户添加失败");
                        failMsgList.add(failMsg);
                        continue;
                    }
                }
            }
            msgMap.put("successCount",successCount);
            msgMap.put("failMsgList",failMsgList);
        }catch (Exception e) {
            return new SimpleJsonResponse<>(false, "您上传的文件不是合法的Excel2003~2007格式，请检查后缀名是否为.xls");
        }
        return new SimpleJsonResponse<>(true, msgMap);
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
}
