/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.velocity;

import java.text.DecimalFormat;
import org.apache.velocity.tools.config.DefaultKey;

/**
 *
 * @author shenzhixiong
 */
@DefaultKey("stringFormat")
public class StringFormatTool {
    
    private final DecimalFormat thdFmt = new DecimalFormat("HHD0000000");
    private final DecimalFormat tkdFmt = new DecimalFormat("TKD0000000");
    
    public String StringTOHHD(Long id){        
        return thdFmt.format(id);
    }
    
    public String StringTOTKD(Long id){
       return tkdFmt.format(id);
    }
}
