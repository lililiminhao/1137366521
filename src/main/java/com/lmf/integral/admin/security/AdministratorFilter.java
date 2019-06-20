/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.security;

import com.lmf.integral.admin.shiro.WebsiteAdministratorPrincipal;
import com.lmf.website.entity.WebsiteAdministrator;
import com.lmf.website.service.WebsiteAdministratorService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 *
 * @author shenzhixiong
 */
public class AdministratorFilter implements Filter  {
    
    @Autowired
    private WebsiteAdministratorService websiteAdministratorService;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest hsr = ( HttpServletRequest )request;
        HttpServletResponse resp    = (HttpServletResponse) response;
        String  contextPath = hsr.getContextPath();
        String  url = hsr.getServletPath();
        WebsiteAdministrator    adminUser  = getAdminUser(hsr);
        if (adminUser == null && url.startsWith("/admin") && !url.startsWith("/admin/login.php") && !url.startsWith("/admin/logout.php"))
        {
            if (contextPath == null || contextPath.isEmpty()) {
                resp.sendRedirect("/jdvop/admin/login.php");
                return;
            } else {
                resp.sendRedirect(contextPath + "/admin/login.php");
                return;
            }
        }else if(url.startsWith("/admin/logout.php")){
            HttpSession session = hsr.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect("/jdvop/admin/login.php");
            return;
        } 
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
    
    
    public WebsiteAdministrator getAdminUser(HttpServletRequest req)
    {
        Subject cu  = SecurityUtils.getSubject();
        Object  obj = cu.getPrincipal();
        if (obj != null && obj instanceof WebsiteAdministratorPrincipal)
        {
            return websiteAdministratorService.findOne(((WebsiteAdministratorPrincipal) obj).getId());
        }
        return null;   
    }
    
}
