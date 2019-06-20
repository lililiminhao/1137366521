package com.lmf.integral.admin.excel;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author shenzhixiong
 *
 */
public class ExcelUtil {
	
	public static void setExportExcelHeader(HttpServletResponse response,String fileName,String ext)  throws  IOException{
        setExportExcelHeader(response, fileName, ext, null , null);
    }
    public static void setExportExcelHeader(HttpServletResponse response,String fileName,String ext,Date startTime,Date endTime)  throws  IOException{
        String subTitle = "_";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        if(startTime!=null){
            subTitle+=sdf.format(startTime);
        }
        if(endTime!=null){
            subTitle+="-"+sdf.format(endTime);
        }else{
            subTitle+="-"+sdf.format(new Date());
        }
        response.setContentType("application/force-download");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.addHeader("Content-Type", "application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", new StringBuilder("attachment;filename=").append(URLEncoder.encode(fileName+subTitle, "UTF-8")).append(ext).toString());
    }

}

