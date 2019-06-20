/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.mobile.controller;

import com.lmf.common.enums.OwnerType;
import java.io.Serializable;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 购物车选择地址时的一个辅助工具
 *
 * @author 张心灵
 */
@Controller
public class ShopCartHelperController {
    
    @RequestMapping(value = "/my/spcarthelper/tochangeaddress.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody String  toChangeAddress(@RequestParam(value = "entry[]", required = false) int[] entryIds,
                                    @RequestParam(value = "type[]", required = false) String[] types,
                                    @RequestParam(value = "sentry[]", required = false) int[] sentrys,
                                    @RequestParam(value = "stype[]", required = false) String[] stypes,
                                    @RequestParam(value = "amount[]", required = false) int[] amount,
                                    @RequestParam(value = "addressId", required = false) Integer address,
                                    @RequestParam(value = "productOwnerType[]") OwnerType[] productOwnerType,
                                    @RequestParam(value = "isSubmitOrder", required = false) Boolean isSubmitOrder,
                                    @RequestParam(value = "fenxiaoUserId[]", required = false) int[] fenxiaoUserIds,
                                    HttpSession session)
    {
        session.setAttribute("spcartHolder", new ShopCartHolder(entryIds, types, sentrys, stypes, amount, productOwnerType,fenxiaoUserIds));
        StringBuilder sbd = new StringBuilder("/jdvop/my/mobile/address.php?isSubmitOrder=true");
        if (address != null && address > 0) 
        {
            sbd.append("&addrId=").append(address);
        }
        return sbd.toString();
    }
    
    @RequestMapping(value = "/my/spcarthelper/returntoshoppingcart.php", method = RequestMethod.GET)
    public  String  returnToShoppingCart(@RequestParam("addressId") long address,
                                         HttpSession session,
                                         Model model)
    {
        model.addAttribute("address", address);
        model.addAttribute("spcartHolder", session.getAttribute("spcartHolder"));
        return "shopping/cart_helper_on_address_selected_return";
    }
    
    public  class ShopCartHolder implements Serializable {
        
        private int[]   entry;
        
        private String[]    type;
        
        private int[]   sentry;
        
        private String[]    stype;
        
        private int[]       amount;
        
        private OwnerType[] productOwnerType;

        private int[] fenxiaoUserIds;

        public ShopCartHolder(int[] entry, String[] type, int[] sentry, String[] stype, int[] amount, OwnerType[] productOwnerType,int[] fenxiaoUserIds) {
            this.entry = entry;
            this.type = type;
            this.sentry = sentry;
            this.stype = stype;
            this.amount = amount;
            this.productOwnerType = productOwnerType;
            this.fenxiaoUserIds = fenxiaoUserIds;
        }

        public int[] getEntry() {
            return entry;
        }

        public void setEntry(int[] entry) {
            this.entry = entry;
        }

        public String[] getType() {
            return type;
        }

        public void setType(String[] type) {
            this.type = type;
        }

        public int[] getSentry() {
            return sentry;
        }

        public void setSentry(int[] sentry) {
            this.sentry = sentry;
        }

        public String[] getStype() {
            return stype;
        }

        public void setStype(String[] stype) {
            this.stype = stype;
        }

        public int[] getAmount() {
            return amount;
        }

        public void setAmount(int[] amount) {
            this.amount = amount;
        }

        public OwnerType[] getProductOwnerType() {
            return productOwnerType;
        }

        public void setProductOwnerType(OwnerType[] productOwnerType) {
            this.productOwnerType = productOwnerType;
        }

        public int[] getFenxiaoUserIds() {
            return fenxiaoUserIds;
        }

        public void setFenxiaoUserIds(int[] fenxiaoUserIds) {
            this.fenxiaoUserIds = fenxiaoUserIds;
        }
        
    }
}
