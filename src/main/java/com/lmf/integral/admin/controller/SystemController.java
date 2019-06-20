package com.lmf.integral.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
@Controller
public class SystemController {
	

	    /**
	     *  shiro 权限不足重定向
	     */
	    @RequestMapping(value = "/admin/noPermission.php", method = RequestMethod.GET)
	    public String noPermission() {
	    	
	    	return "/admin/noPermission";
		}
}
