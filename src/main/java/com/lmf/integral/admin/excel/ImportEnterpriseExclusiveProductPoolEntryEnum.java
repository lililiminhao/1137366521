/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.excel;

import com.lmf.common.enums.OwnerType;
import com.lmf.excel.util.info.enums.api.ExcelImportInfoEnumApi;
import com.lmf.excel.util.model.ImportColParam;
import java.util.Date;

/**
 * excel2utils导出参数例子
 *
 * @author Administrator
 */
public enum ImportEnterpriseExclusiveProductPoolEntryEnum implements ExcelImportInfoEnumApi {

    productId( 0, new ImportColParam<Integer>("productId", Integer.class, null, true, false)),
    productName( 1, new ImportColParam<String>("productName", String.class, null, true)),
    productCode( 2, new ImportColParam<String>("productCode", String.class, null, true)),
    ownerType( 3, new ImportColParam<OwnerType>("ownerType", OwnerType.class, null, true)),
    brandName( 4, new ImportColParam<String>("brandName", String.class, null, true)),
    retailPrice( 5, new ImportColParam<Double>("retailPrice", Double.class, null, true)),
    systemPrice( 6, new ImportColParam<Double>("systemPrice", Double.class, null, true)),
    serviceChargeRatio(7, new ImportColParam<Double>("serviceChargeRatio", Double.class, null, true)),
    exclusivePrice( 8, new ImportColParam<Double>("exclusivePrice", Double.class, null, true)),
    exclusiveBillingPrice( 9, new ImportColParam<Double>("exclusiveBillingPrice", Double.class, null)),
    exclusiveRatio( 10, new ImportColParam<Double>("exclusiveRatio", Double.class, null)); 

    private ImportEnterpriseExclusiveProductPoolEntryEnum( int index, ImportColParam importColParam) {
        this.index = index;
        this.importColParam = importColParam;
    }

    private final Integer index;
    private final ImportColParam importColParam;

  
    @Override
    public Integer getIndex() {
        return index;
    }

    @Override
    public ImportColParam getImportColParam() {
        return importColParam;
    }

}
