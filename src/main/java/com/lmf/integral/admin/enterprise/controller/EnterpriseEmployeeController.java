/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.enterprise.controller;

import com.lmf.common.Page;
import com.lmf.common.PagerSpecDefaults;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.PagerSpec;
import com.lmf.common.util.ValidUtil;
import com.lmf.enterprise.entity.Enterprise;
import com.lmf.enterprise.entity.EnterpriseEmployee;
import com.lmf.enterprise.entity.EnterpriseUserMap;
import com.lmf.enterprise.service.EnterpriseEmployeeService;
import com.lmf.enterprise.service.EnterpriseService;
import com.lmf.enterprise.service.EnterpriseUserMapService;
import com.lmf.enterprise.vo.EnterpriseEmployeeCriteria;
import com.lmf.website.entity.Website;
import com.lmf.website.entity.WebsiteAdministrator;

import jxl.Sheet;
import jxl.Workbook;

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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业员工管理
 *
 * @author lianwukun
 */
@Controller("enterpriseEmployeeController")
public class EnterpriseEmployeeController {

    @Autowired
    private EnterpriseEmployeeService enterpriseEmployeeService;

    @Autowired
    private EnterpriseUserMapService enterpriseUserMapService;

    @Autowired
    private EnterpriseService enterpriseService;

    //企业员工列表
    @RequiresPermissions("enterprise_employee:view")
    @RequestMapping(value = "/admin/enterpriseEmployee.php", method = RequestMethod.GET)
    public String list(@PagerSpecDefaults(pageSize = 20, sort = "id.desc") PagerSpec pager,
                       @RequestParam(value = "name", required = false)String name,
                       @RequestParam(value = "enterpriseId", required = false)Integer enterpriseId,
                       Model model) throws UnsupportedEncodingException {
        EnterpriseEmployeeCriteria criteria = new EnterpriseEmployeeCriteria();
        if (name != null && !name.isEmpty()) {
            criteria.andNameLike("%"+name+"%");
        }
        if (enterpriseId != null && enterpriseId != 0) {
            criteria.andEnterpriseIdEquals(enterpriseId);
        }
        Page<EnterpriseEmployee> employees = enterpriseEmployeeService.selectByPage(pager,criteria.setOrderBy("id desc"));
        model.addAttribute("employees", employees);
        StringBuilder link = new StringBuilder("/jdvop/admin/enterpriseEmployee.php?page=[:page]");
        if (name != null && !name.isEmpty()) {
            link.append("&name=").append(URLEncoder.encode(name, "UTF-8"));
            model.addAttribute("name", name);
        }
        if (enterpriseId != null && enterpriseId !=0){
            link.append("&enterpriseId=").append(enterpriseId);
            model.addAttribute("enterpriseId", enterpriseId);
        }
        List<Enterprise> enterpriseList = enterpriseService.list(null);
        model.addAttribute("enterpriseUserMapService",enterpriseUserMapService);
        model.addAttribute("enterpriseList",enterpriseList);
        model.addAttribute("enterpriseService",enterpriseService);
        model.addAttribute("link",link.toString());
        return "/admin/enterprise/enterprise_employee/list";
    }


    //编辑员工页面
    @RequiresPermissions("enterprise_employee:edit")
    @RequestMapping(value = "/admin/enterpriseEmployee/edit.php", method = RequestMethod.GET)
    public String edit(@RequestParam(value = "id", required = false)Long id,
                       @RequestParam(value = "enterpriseId", required = false)Long enterpriseId,
                       Model model) {
        EnterpriseEmployee enterpriseEmployee = enterpriseEmployeeService.selectOne(id);
        List<Enterprise> enterpriseList = enterpriseService.list(null);
        model.addAttribute("enterpriseList",enterpriseList);
        model.addAttribute("enterpriseId",enterpriseId);
        model.addAttribute("employee", enterpriseEmployee);
        return "/admin/enterprise/enterprise_employee/form";
    }

    //编辑员工
    @RequiresPermissions("enterprise_employee:edit")
    @RequestMapping(value = "/admin/enterpriseEmployee/edit.php", method = RequestMethod.POST)
    @ResponseBody
    public SimpleJsonResponse<String> editPost(EnterpriseEmployee employee,
                                               Model model) {
        if(!ValidUtil.isMobile(employee.getMobile())) {
            return new SimpleJsonResponse(false, "请输入正确的手机号码！");
        }
        enterpriseEmployeeService.updateById(employee);
        return new SimpleJsonResponse<>(true, null);
    }

    //新增企业员工
    @RequestMapping(value = "/admin/enterpriseEmployee/add.php", method = RequestMethod.POST)
    @ResponseBody
    @RequiresPermissions("enterprise_employee:create")
    public SimpleJsonResponse<String> addPost(EnterpriseEmployee employee,
                                               Model model) {
        //用户已存在
        Boolean exists = enterpriseEmployeeService.exists(new EnterpriseEmployeeCriteria().andMobileEquals(employee.getMobile()).andEnterpriseIdEquals(employee.getEnterpriseId()));
        if(exists){
            return new SimpleJsonResponse<>(false, "该企业已存在该企业员工");
        }
        Boolean existsOther = enterpriseEmployeeService.exists(new EnterpriseEmployeeCriteria().andMobileEquals(employee.getMobile()).andEnterpriseIdNotEquals(employee.getEnterpriseId()));
        if(existsOther){
            return new SimpleJsonResponse<>(false, "其他企业已存在该企业员工");
        }
        if(!ValidUtil.isMobile(employee.getMobile())) {
            return new SimpleJsonResponse(false, "请输入正确的手机号码！");
        }
        enterpriseEmployeeService.insert(employee);
        return new SimpleJsonResponse<>(true, null);
    }

    //解除员工与用户绑定
    @ResponseBody
    @RequiresPermissions("enterprise_employee:unbind")
    @RequestMapping(value = "/admin/enterpriseEmployee/unbind.php", method = RequestMethod.GET)
    public SimpleJsonResponse<String> unbind(@RequestParam(value = "id")long id,
                                                 Model model) {
        EnterpriseUserMap enterpriseUserMap = enterpriseUserMapService.getOneByEnterpriseEmployeeId(id);
        if (enterpriseUserMap == null) {
            return new SimpleJsonResponse<>(false, "该员工没有绑定用户");
        }
        enterpriseUserMapService.unbind(id);
        return new SimpleJsonResponse<>(true, "解除员工与用户绑定绑定成功");
    }

    //删除企业员工
    @ResponseBody
    @RequiresPermissions("enterprise_employee:delete")
    @RequestMapping(value = "/admin/enterpriseEmployee/delete.php", method = RequestMethod.GET)
    public SimpleJsonResponse<String> deletePost(@RequestParam(value = "id", required = false)Long id,
                                              Model model) {
        enterpriseEmployeeService.deleteById(id);
        return new SimpleJsonResponse<>(true, null);
    }

    //批量删除企业员工
    @RequestMapping(value = "/admin/enterpriseEmployee/batchDelete.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse delete(@RequestParam("id[]") Long [] ids)
    {
        if (ids.length < 1) {
            return new SimpleJsonResponse(false, "请选择您要操作的企业员工");
        }
        for (long id : ids) {
            enterpriseEmployeeService.deleteById(id);
        }
        return new SimpleJsonResponse(true, null);
    }


    //批量企业员工
    @RequiresPermissions("enterprise_list:importEmployee")
    @RequestMapping(value = "/admin/enterpriseEmployee/batchInsert.php", method = RequestMethod.GET)
    public String batchInsert(WebsiteAdministrator admin, Model model) {

        List<Enterprise> enterpriseList = enterpriseService.list(null);

        model.addAttribute("enterpriseList",enterpriseList);
        model.addAttribute("admin", admin);
        return "admin/enterprise/enterprise_employee/batch_insert";
    }


    //批量导入企业员工
    @RequiresPermissions("enterprise_list:importEmployee")
    @RequestMapping(value = "/admin/enterpriseEmployee/batchInsert.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse batchShipment(@RequestParam(value = "excelFile") MultipartFile file,
                                                          WebsiteAdministrator admin,
                                                          @RequestParam(value = "id", required = false) Integer enterpriseId,
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
                //循环取出单元格的内容
                for(int r = 1; r < rows; ++r){
                    Map<String,String> failMsg = new HashMap<>();
                    try{
                        employeeName = sheet.getCell(0, r).getContents();//员工姓名
                        mobile = sheet.getCell(1, r).getContents(); //员工手机号码

                        //数据异常跳过
                        if((employeeName == null || employeeName.isEmpty()) && (mobile == null || mobile.isEmpty())){
                            continue;
                        }
                        if(mobile == null || mobile.isEmpty()){
                            failMsg.put("name",employeeName);
                            failMsg.put("mobile",mobile);
                            failMsg.put("msg","手机号码不能为空");
                            failMsgList.add(failMsg);
                            continue;
                        }
                        if(employeeName == null || employeeName.isEmpty()){
                            failMsg.put("name",employeeName);
                            failMsg.put("mobile",mobile);
                            failMsg.put("msg","用户名不能为空");
                            failMsgList.add(failMsg);
                            continue;
                        }
                        if(!ValidUtil.isMobile(mobile)) {
                            failMsg.put("name",employeeName);
                            failMsg.put("mobile",mobile);
                            failMsg.put("msg","错误的手机号码格式");
                            failMsgList.add(failMsg);
                            continue;
                        }
                        //用户已存在
                        Boolean exists = enterpriseEmployeeService.exists(new EnterpriseEmployeeCriteria().andMobileEquals(mobile).andEnterpriseIdEquals(enterpriseId));
                        if(exists){
                            failMsg.put("name",employeeName);
                            failMsg.put("mobile",mobile);
                            failMsg.put("msg","企业员工已存在");
                            failMsgList.add(failMsg);
                            continue;
                        }
                        Boolean existsOther = enterpriseEmployeeService.exists(new EnterpriseEmployeeCriteria().andMobileEquals(mobile).andEnterpriseIdNotEquals(enterpriseId));
                        if(existsOther){
                            failMsg.put("name",employeeName);
                            failMsg.put("mobile",mobile);
                            failMsg.put("msg","其他企业已存在该企业员工");
                            failMsgList.add(failMsg);
                            continue;
                        }
                        EnterpriseEmployee enterpriseEmployee = new EnterpriseEmployee();
                        enterpriseEmployee.setName(employeeName);
                        enterpriseEmployee.setMobile(mobile);
                        enterpriseEmployee.setEnterpriseId(enterpriseId);
                        enterpriseEmployeeService.insert(enterpriseEmployee);
                        successCount++;
                    }catch (Exception e) {
                        failMsg.put("name",employeeName);
                        failMsg.put("mobile",mobile);
                        failMsg.put("msg","企业员工添加失败");
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
