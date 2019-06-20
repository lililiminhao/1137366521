/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.product.controller;

import com.lmf.common.SimpleJsonResponse;
import com.lmf.product.entity.ProductCate;
import com.lmf.product.service.ProductCateService;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author shenzhixiong
 */

@Controller
public class ProductCateController {
    
    @Autowired
    private ProductCateService productCateService;
    
    @RequestMapping(value = "/admin/productCates.php", method = RequestMethod.GET)
    public String list(@RequestParam(value = "parentId", required = false) Integer parentCateId,
                       Model model){
        if(parentCateId != null && parentCateId > 0){
            ProductCate parentCate = productCateService.findOne(parentCateId);
            if(parentCate != null){
                model.addAttribute("parentCate", parentCate);
                model.addAttribute("cates", parentCate.getChilds());
            }
        } else {
            Set<ProductCate> cates = productCateService.rootCates();
            model.addAttribute("cates", cates);
        }
        model.addAttribute("allCates", productCateService.allAsMap());
        model.addAttribute("productCateService", productCateService);
        return "admin/product/cate/list";
    }
    
    @RequestMapping(value = "/admin/cate/parents.php", method = RequestMethod.GET)
    public String parents(@RequestParam(value = "parentId", required = false) Integer parentCateId, Model model){
        model.addAttribute("productCateService", productCateService);
        model.addAttribute("cates", productCateService.siblings(parentCateId));
        return "admin/product/cate/list";
    }
    
    @RequestMapping(value = "/admin/product/cate/ajaxChilds.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public @ResponseBody List<ProductCate> childs(@RequestParam("parentID") int parentId)
    {
        List<ProductCate>    childs  = productCateService.childs(parentId);
        if (childs == null)
        {
            childs  = Collections.EMPTY_LIST;
        }
        return childs;
    }
    
}
