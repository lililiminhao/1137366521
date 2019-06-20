/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral;

import java.text.SimpleDateFormat;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;

/**
 *
 * @author shenzhixiong
 */
public class JXlUtil {
    
    public static String readDate(Cell cell,Sheet sheet,int cols,int rows){
        String time;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (cell.getType() == CellType.DATE)
        {
            DateCell   dc   = (DateCell) cell;
            time = sdf.format(dc.getDate());
        }else{
            time = sheet.getCell(cols, rows).getContents().trim();
        }
        return time;
    }
}
