package com.lmf.integral.admin.excel;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.lmf.excel.util.info.enums.api.ExcelExportInfoEnumApi;
import com.lmf.excel.util.model.ColStyle;

/**
 * excel2utils导出参数
 *
 * @author shenzhixiong
 */
public enum ExportEnterpriseExclusiveProductPoolEntryEnum implements ExcelExportInfoEnumApi {

    productId("产品ID", 0, 2, new ColStyle<Integer>("", null, Integer.class)),
    productName("产品名称", 1, 10, new ColStyle<String>("", null, String.class)),
    productCode("产品型号", 2, 5, new ColStyle<String>("", null, String.class)),
	ownerType("产品来源", 3, 2, new ColStyle<String>("", null, String.class)),
	brandName("产品品牌", 4, 3, new ColStyle<String>("", null, String.class)),
	retailPrice("原经销价", 5, 2, new ColStyle<Double>("#0.00", 0.00, Double.class)),
	systemPrice("原系统售价", 6, 2, new ColStyle<Double>("#0.00", 0.00, Double.class)),
	serviceChargeRatio("原扣点", 7, 2, new ColStyle<Double>("#0.00", 0.00, Double.class)),
	exclusivePrice("专享价（可编辑）", 8, 3, new ColStyle<Double>("#0.00", 0.00, Double.class)),
	exclusiveBillingPrice("专享开单价（可编辑）", 9, 3, new ColStyle<Double>("#0.00", 0.00, Double.class)),
	exclusiveRatio("专享扣点（可编辑）", 10, 3, new ColStyle<Double>("#0.00", 0.00, Double.class));

    private ExportEnterpriseExclusiveProductPoolEntryEnum(String title, Integer index, Integer width, ColStyle colStyle) {
        this.title = title;
        this.index = index;
        this.width = width;
        this.colStyle = colStyle;
    }
    private final String title;
    private final Integer index;
    private final Integer width;
    private final ColStyle colStyle;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Integer getIndex() {
        return index;
    }

    @Override
    public Integer getWidth() {
        return width;
    }

    @Override
    public ColStyle getColStyle() {
        return colStyle;
    }

}
