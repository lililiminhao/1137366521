/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.secuity;

import java.io.Serializable;

/**
 *
 * @author shenzhixiong
 */
public class FinanceSettlementInfo implements Serializable {
    
    private double  chargedAmount;          //预存金额 
    
    private double  creditTotal;            //授信额度
    
    private double  creditUsed;             //已使用授信额度
    
    public FinanceSettlementInfo(){
        
    }
    
    public FinanceSettlementInfo(double chargedAmount, double creditTotal, double creditUsed) {
        this.chargedAmount = chargedAmount;
        this.creditTotal = creditTotal;
        this.creditUsed  = creditUsed;
    }

    public double getChargedAmount() {
        return chargedAmount;
    }

    public void setChargedAmount(double chargedAmount) {
        this.chargedAmount = chargedAmount;
    }

    public double getCreditTotal() {
        return creditTotal;
    }

    public void setCreditTotal(double creditTotal) {
        this.creditTotal = creditTotal;
    }

    public double getCreditUsed() {
        return creditUsed;
    }

    public void setCreditUsed(double creditUsed) {
        this.creditUsed = creditUsed;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.chargedAmount) ^ (Double.doubleToLongBits(this.chargedAmount) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.creditTotal) ^ (Double.doubleToLongBits(this.creditTotal) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.creditUsed) ^ (Double.doubleToLongBits(this.creditUsed) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FinanceSettlementInfo other = (FinanceSettlementInfo) obj;
        if (Double.doubleToLongBits(this.chargedAmount) != Double.doubleToLongBits(other.chargedAmount)) {
            return false;
        }
        if (Double.doubleToLongBits(this.creditTotal) != Double.doubleToLongBits(other.creditTotal)) {
            return false;
        }
        if (Double.doubleToLongBits(this.creditUsed) != Double.doubleToLongBits(other.creditUsed)) {
            return false;
        }
        return true;
    }
    
    
}
