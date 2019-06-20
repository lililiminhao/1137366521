/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.velocity;

import com.lmf.common.util.PagerSpec;
import org.apache.velocity.tools.config.DefaultKey;

/**
 *
 * @author zhangxinling
 */
@DefaultKey("adminPg")
public class PagerTool {
    
    public static String pager(String url, PagerSpec ps, int delta, String target)
    {
        int totalPage   = ps.getTotalPage();
        int currentPage = ps.getCurrentPage();
        int lowPage     = currentPage - delta;
        int highPage    = currentPage + delta;
        if (highPage > totalPage)
        {
            highPage    = totalPage;
            lowPage     = totalPage - delta * 2;
        }
        if (lowPage < 1)
        {
            lowPage     = 1;
            highPage    = lowPage + delta * 2;
            if (highPage > totalPage)
            {
                highPage    = totalPage;
            }
        }
        int offset  = ps.getOffset();
        
        PagerSpec.Order[]  orders   = ps.getOrder();
        if (orders != null && orders.length > 0)
        {
            StringBuilder   sbd = new StringBuilder();
            int len = orders.length - 1;
            for (int i = 0; i <= len; ++ i)
            {
                sbd.append(orders[i].getField()).append('.').append(orders[i].getDrection().name());
                if (i != len)
                {
                    sbd.append(',');
                }
            }
            if (url.indexOf('?') == -1)
            {
                url = new StringBuilder(url).append('?').append("sort=").append(sbd.toString()).toString();
            } else {
                url = new StringBuilder(url).append('&').append("sort=").append(sbd.toString()).toString();
            }
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("<ul class='multipage clearfix'><li>共(")
                .append(ps.getTotalItems())
                .append(")条</li><li style='margin:0px 20px 0px 15px;'>当前显示 ")
                .append(offset+1)
                .append('-')
                .append(offset + ps.getPerPageNum())
                .append(" 条</li>");
        if (currentPage > 1)
        {
            sb.append("<li><a href='")
                    .append(url.replace("[:page]", "1"))
                    .append("' target='")
                    .append(target)
                    .append("' class='link h-bn'><span class='h-page'>首页</span></a></li>");
            sb.append("<li><a href='")
                    .append(url.replace("[:page]", String.valueOf(currentPage - 1)))
                    .append("' class='link p-bn' target='")
                    .append(target)
                    .append("'><span class='p-page'>前一页</span></a></li>");
        } else {
            sb.append("<li><span class='no-page h-page'>首页</span></li>");
            sb.append("<li><span class='no-page p-page'>前一页</span></li>");
        }
        for (; lowPage <= highPage; ++ lowPage)
        {
            if (lowPage != currentPage)
            {
                sb.append("<li><a href='")
                        .append(url.replace("[:page]", String.valueOf(lowPage)))
                        .append("' class='link' target='")
                        .append(target)
                        .append("'> ")
                        .append(lowPage)
                        .append(" </a></li>");
            } else {
                sb.append("<li><span class='current'> ")
                        .append(lowPage)
                        .append(" </span></li>");
            }
        }
        if (currentPage < totalPage)
        {
            sb.append("<li><a href='")
                    .append(url.replace("[:page]", String.valueOf(currentPage + 1)))
                    .append("' class='link n-bn' target='")
                    .append(target).append("'><span class='n-page'>后一页</span></a></li>");
            
            sb.append("<li class='link'><a href='")
                    .append(url.replace("[:page]", String.valueOf(totalPage)))
                    .append("' class='link e-bn' target='")
                    .append(target)
                    .append("'><span class='e-page'>尾页</span></a>");
        } else {
            sb.append("<li><span class='no-page n-page'>后一页</span></li>");
            sb.append("<li><span class='no-page e-page'>尾页</span></li>");
        }
        sb.append("</ul>");
        
        return sb.toString();   
    }
}
