/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;

/**
 *
 * @author tranC105
 */
public class TaxSettlementDobj implements Serializable {

    private String slNo;
    private String taxType;
    private String taxFrom;
    private String taxUpto;
    private String setComPenalty;
    private String setComTax;
    private String setComInterst;

    public TaxSettlementDobj(String slNo, String taxType, String taxFrom, String taxUpto, String setComPenalty, String setComTax, String setComInterst) {
        this.slNo = slNo;
        this.taxType = taxType;
        this.taxFrom = taxFrom;
        this.setComTax = setComTax;
        this.setComPenalty = setComPenalty;
        this.taxUpto = taxUpto;
        this.setComInterst = setComInterst;
    }

    public TaxSettlementDobj() {
    }

    public String getSlNo() {
        return slNo;
    }

    public void setSlNo(String slNo) {
        this.slNo = slNo;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getTaxFrom() {
        return taxFrom;
    }

    public void setTaxFrom(String taxFrom) {
        this.taxFrom = taxFrom;
    }

    public String getTaxUpto() {
        return taxUpto;
    }

    public void setTaxUpto(String taxUpto) {
        this.taxUpto = taxUpto;
    }

    public String getSetComPenalty() {
        return setComPenalty;
    }

    public void setSetComPenalty(String setComPenalty) {
        this.setComPenalty = setComPenalty;
    }

    public String getSetComTax() {
        return setComTax;
    }

    public void setSetComTax(String setComTax) {
        this.setComTax = setComTax;
    }

    public String getSetComInterst() {
        return setComInterst;
    }

    public void setSetComInterst(String setComInterst) {
        this.setComInterst = setComInterst;
    }
}
