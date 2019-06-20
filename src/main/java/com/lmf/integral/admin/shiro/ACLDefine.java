/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.admin.shiro;

/**
 *
 * @author zhangxinling
 */
public enum ACLDefine {
    /*SYSTEM_ACL("system", "系统配置", new ACLAction[]{
        new ACLAction("edit", "编辑")
    }),
    ACCOUNT_ACL("account", "账号管理", new ACLAction[]{
        new ACLAction("view", "查看"),
        new ACLAction("create", "创建"),
        new ACLAction("edit", "编辑")
    }),
    
    PROVIDE_ACL("provide", "供应商管理", new ACLAction[]{
        new ACLAction("view", "查看"),
        new ACLAction("edit", "编辑"),
        new ACLAction("create", "新增"),
        new ACLAction("delete", "删除")
    }),
    SHIPMENT_FEE_ACL("shipment_fee", "快递价格模板", new ACLAction[]{
        new ACLAction("view", "查看"),
        new ACLAction("edit", "编辑"),
        new ACLAction("delete", "删除")
    }),
    ENTERPRISE_ACL("enterprise_list", "企业管理", new ACLAction[]{
		new ACLAction("view","查看"),
        new ACLAction("create","创建"),
        new ACLAction("edit","编辑"),
        new ACLAction("delete","删除"),
    }),
    ENTERPRISE_EMPlOYEE_ACL("enterprise_employee", "企业员工管理", new ACLAction[]{
		new ACLAction("view","查看"),
        new ACLAction("create","创建"),
        new ACLAction("edit","编辑"),
        new ACLAction("delete","删除"),
    }),
    ENTERPRISE_EXCLUSIVE_ACL("enterprise_exclusive", "企业专享池管理", new ACLAction[]{
		new ACLAction("view","查看"),
        new ACLAction("create","创建"),
        new ACLAction("edit","编辑"),
        new ACLAction("delete","删除"),
    }),
    MOBILE_TEMPLATE_ACL("mobile_template", "移动模板配置", new ACLAction[]{
        new ACLAction("edit", "编辑")
    }),
    CUSTOM_CATE_ACL("custom_cate", "自定义分类", new ACLAction[]{
        new ACLAction("view", "查看"),
        new ACLAction("edit", "编辑"),
        new ACLAction("create", "新增"),
        new ACLAction("delete", "删除")
    }),
    PRODUCT_ACL("product", "产品管理", new ACLAction[]{
        new ACLAction("view", "查看"),
        new ACLAction("create", "发布新产品"),
        new ACLAction("edit", "编辑"),
        new ACLAction("delete", "删除")
    }),
    BRAND_ACL("brand", "品牌管理", new ACLAction[]{
        new ACLAction("view", "查看"),
        new ACLAction("create", "新建"),
        new ACLAction("edit", "编辑"),
        new ACLAction("delete", "删除")
    }),
    STOCK_ACL("stock", "库存管理", new ACLAction[]{
        new ACLAction("view", "查看"),
//        new ACLAction("replenishment", "补货"),
        new ACLAction("edit", "库存维护")
    }),
    ORDER_ACL("order", "订单管理", new ACLAction[]{
        new ACLAction("view", "查看"),
        new ACLAction("edit", "编辑"),
        new ACLAction("examine", "订单审核"),
        new ACLAction("shipment", "发货")
//        new ACLAction("delete", "删除")
    }),
    
    ORDER_AFTER_SALE_ACL("order_after_sale", "售后管理", new ACLAction[]{
        new ACLAction("view", "查看"),
        new ACLAction("audit", "客服审核"),
        new ACLAction("confirm_returned", "仓库确认收货"),
        new ACLAction("confirm_refund", "财务确认退款"),
        new ACLAction("edit", "编辑")
    }),
    
    SYSTEM_ORDER_ACL("system_order", "系统订单", new ACLAction[]{
        new ACLAction("view", "查看")
    }),
    STATISTICS_ACL("statistics", "数据统计", new ACLAction[]{
        new ACLAction("view", "查看")
    });*/
    
	 ACCOUNT_ACL("account", "管理员列表", new ACLAction[]{
		        new ACLAction("view", "查看"),
		        new ACLAction("create", "创建"),
				new ACLAction("delete", "删除"),
		        new ACLAction("edit", "编辑"),
				new ACLAction("startUse", "启用"),
				new ACLAction("endUse", "禁用"),
				new ACLAction("changPassword", "修改密码"),
		    }),
			
			
		 PROVIDE_ACL("provide", "供应商管理", new ACLAction[]{
		        new ACLAction("view", "查看"),
		        new ACLAction("edit", "编辑"),
		        new ACLAction("create", "新增"),
		        new ACLAction("delete", "删除"),
				new ACLAction("startUse", "启用"),
				new ACLAction("endUse", "禁用"),
				new ACLAction("changPassword", "修改密码"),
				new ACLAction("shipment", "运费模板"),
		    }),
			FENXIAOUSER_ACL("fenxiao_user", "分销商列表", new ACLAction[]{
		        new ACLAction("view", "查看"),
		        new ACLAction("create", "创建"),
				new ACLAction("delete", "删除"),
		        new ACLAction("edit", "编辑"),
				new ACLAction("import", "导入"),
				new ACLAction("unbind", "解绑"),
		    }),
			
			FENXIAORANK_ACL("fenxiao_rank", "分销商等级", new ACLAction[]{
		        new ACLAction("view", "查看"),
		        new ACLAction("create", "创建"),
				new ACLAction("delete", "删除"),
		        new ACLAction("edit", "编辑"),
		    }),
			ENTERPRISE_ACL("enterprise_list", "企业列表", new ACLAction[]{
				new ACLAction("view","查看"),
		        new ACLAction("create","创建"),
		        new ACLAction("edit","编辑"),
		        new ACLAction("delete","删除"),
				new ACLAction("importEmployee","导入员工"),
				new ACLAction("importQuan","导入提货卷"),
		    }),
			 ENTERPRISE_EMPlOYEE_ACL("enterprise_employee", "员工列表", new ACLAction[]{
				new ACLAction("view","查看"),
		        new ACLAction("create","创建"),
		        new ACLAction("edit","编辑"),
		        new ACLAction("delete","删除"),
				new ACLAction("importEmployee","导入员工"),
				new ACLAction("unbind", "解绑"),
		    }),
			
			ENTERPRISE_EXCLUSIVE_ACL("enterprise_exclusive", "专享池列表", new ACLAction[]{
				new ACLAction("view","查看"),
		        new ACLAction("create","创建"),
		        new ACLAction("edit","编辑"),
		        new ACLAction("delete","删除"),
				new ACLAction("startUse", "启用"),
				new ACLAction("endUse", "禁用"),
				new ACLAction("view_product", "查看关联产品"),
				new ACLAction("import_product", "导入关联产品"),
				new ACLAction("edit_product", "编辑关联产品"),
		    }),
			
			COMMEN_ACTIVITY_ACL("commen_activity", "商城普通专题", new ACLAction[]{
				new ACLAction("view","查看"),
		        new ACLAction("create","创建"),
		        new ACLAction("edit","编辑"),
		        new ACLAction("delete","删除"),
				new ACLAction("online","上线"),
				new ACLAction("offline", "下线"),
				new ACLAction("peizhi", "页面配置"),
		    }),
			
			ENTERPRISE_ACTIVITY_ACL("enterprise_activity", "企业专享专题", new ACLAction[]{
				new ACLAction("view","查看"),
		        new ACLAction("create","创建"),
		        new ACLAction("edit","编辑"),
		        new ACLAction("delete","删除"),
				new ACLAction("online","上线"),
				new ACLAction("offline", "下线"),
				new ACLAction("peizhi", "页面配置"),
		    }),
			
			INDEX_MODEL_ACL("index_model", "首页模板配置", new ACLAction[]{
		        new ACLAction("edit","编辑"),
		    }),
			
			FENXIAO_MODEL_ACL("fenxiao_model", "分销模板配置", new ACLAction[]{
		        new ACLAction("edit","编辑"),
		    }),
			
			ENTERPRISE_MODEL_ACL("enterprise_model", "企业专区配置", new ACLAction[]{
		        new ACLAction("edit","编辑"),
		    }),
			
			 PRODUCT_ACL("product", "产品列表", new ACLAction[]{
		        new ACLAction("view", "查看"),
		        new ACLAction("create", "发布新产品"),
		        new ACLAction("edit", "编辑"),
		        new ACLAction("delete", "删除"),
				new ACLAction("online","上线"),
				new ACLAction("offline", "下线"),
				new ACLAction("edit_all", "批量编辑"),
		        new ACLAction("delete_all", "批量删除"),
				new ACLAction("online_all","批量上线"),
				new ACLAction("offline_all", "批量下线"),
				new ACLAction("import", "导入产品"),
				new ACLAction("export", "导出产品"),
				new ACLAction("brand", "品牌管理"),
				new ACLAction("type", "系统分类"),
		    }),
			
			STOCK_ACL("stock", "库存维护", new ACLAction[]{
		        new ACLAction("view", "查看"),
				 new ACLAction("export", "导出"),
		        new ACLAction("replenishment", "补货"),
		        new ACLAction("edit", "库存维护")
		    }),
			
			FENXIAO_PRODUCT_ACL("fenxiao_product", "分销产品", new ACLAction[]{
					new ACLAction("delete", "删除"),
					new ACLAction("view", "查看"),
					 new ACLAction("create", "新建"),
					new ACLAction("edit", "单个设置比例"),
				 new ACLAction("edit_all", "批量设置比例"),
		       
		    }),
			BRAND_ACL("brand", "品牌管理", new ACLAction[]{
		        new ACLAction("view", "查看"),
		        new ACLAction("create", "添加"),
		        new ACLAction("edit", "编辑"),
		        new ACLAction("delete", "删除")
		    }),
			
			SYSTEM_BRAND_ACL("custom_cate", "系统分类列表", new ACLAction[]{
		        new ACLAction("view", "查看"),
		        new ACLAction("create", "新建"),
		        new ACLAction("edit", "编辑"),
		        new ACLAction("delete", "删除")
		    }),
			
			ZIDINGYI_BRAND_ACL("zdy_brand", "自定义分类列表", new ACLAction[]{
		        new ACLAction("view", "查看"),
		        new ACLAction("create", "新建"),
		        new ACLAction("edit", "编辑"),
		        new ACLAction("delete", "删除"),
				new ACLAction("startUse", "启用"),
				new ACLAction("endUse", "禁用"),
		    }),
			
			ORDER_ACL("order", "订单管理", new ACLAction[]{
		        new ACLAction("view", "查看"),
		        new ACLAction("edit", "编辑"),
		        new ACLAction("examine", "订单审核"),
		        new ACLAction("shipment", "发货"),
		        new ACLAction("export", "导出"),
		    }),
			
			ORDER_AFTER_SALE_ACL("order_after_sale", "售后订单", new ACLAction[]{
		        new ACLAction("view", "查看"),
		        new ACLAction("audit", "客服审核"),
//		        new ACLAction("confirm_returned", "仓库确认收货"),
		        new ACLAction("confirm_refund", "财务确认退款"),
		        new ACLAction("edit", "编辑"),
		    }),
			
			 SYSTEM_ORDER_ACL("system_order", "系统订单", new ACLAction[]{
		        new ACLAction("view", "查看"),
		        new ACLAction("export", "导出"),
		    }),
			
			SYSTEM_ORDER_AFTER_SALE__ACL("system_order_after_sale", "系统售后订单", new ACLAction[]{
					new ACLAction("view", "查看"),
					new ACLAction("audit", "客服审核"),
					new ACLAction("confirm_refund", "财务确认退款"),
					new ACLAction("edit", "编辑")
		    }),

			APPLY_EXCEPTION_ACL("apply_order_exception", "支付异常订单", new ACLAction[]{
					new ACLAction("view", "查看"),
					new ACLAction("processed", "标记处理"),
					new ACLAction("refund", "退款")
			}),
			TRANSACTION_PROCESSING_ACL("apply_order_transaction", "交易处理中订单", new ACLAction[]{
					new ACLAction("view", "查看"),
			}),

			DUIZHANG_ACL("duizhang", "对账日志", new ACLAction[]{
		        new ACLAction("pay_view", "支付查看"),
				 new ACLAction("pay_export", "支付导出"),
				 new ACLAction("refund_view", "退款查看"),
				 new ACLAction("refund_export", "退款导出"),
		    }),
			YONGJIN_ACL("yongjin", "佣金日志", new ACLAction[]{
		        new ACLAction("fenxiao_view", "分销商查看"),
				 new ACLAction("fenxiao_export", "分销商导出"),
				 new ACLAction("smk_view", "市民卡查看"),
				 new ACLAction("smk_export", "市民卡导出"),
		    }),
			
			COUPON_ACL("coupon", "优惠券列表", new ACLAction[]{
		        new ACLAction("view", "查看"),
		        new ACLAction("create", "新建"),
		        new ACLAction("edit", "编辑"),
		        new ACLAction("delete", "删除"),
		    }),
			
			VOUCHER_ACL("voucher", "提货券列表列表", new ACLAction[]{
		        new ACLAction("view", "查看"),
//		        new ACLAction("create", "新建"),
		        new ACLAction("edit", "编辑"),
		        new ACLAction("delete", "删除"),
				new ACLAction("import", "导入"),
		    }),
	 	STATISTICS_ACL("statistics", "数据统计", new ACLAction[]{
		        new ACLAction("view", "查看")
		    }),
	 MARKET("market", "营销管理", new ACLAction[]{
		        new ACLAction("view", "查看")
		    });
			
    private final String  key;
    
    private final String  name;
    
    private final ACLAction[] action;

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public ACLAction[] getAction() {
        return action;
    }

    private ACLDefine(String key, String name, ACLAction[] action) {
        this.key = key;
        this.name = name;
        this.action = action;
    }    
}
