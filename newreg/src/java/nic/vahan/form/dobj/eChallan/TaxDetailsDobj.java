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
public class TaxDetailsDobj implements Serializable {

    private String slNo;
    private String taxType;
    private String taxFrom;
    private String taxUpto;
    private String cfTax;
    private String cfPeanlty;
    private String cfInterest;
    private String taxRcptNo;
    private String taxRcptDate;

    public TaxDetailsDobj() {
    }

    public TaxDetailsDobj(String slNo, String taxType, String taxFrom, String taxUpto, String cfTax, String cfPeanlty,
            String cfInterest, String taxrcpt, String taxrcptdate) {
        this.slNo = slNo;
        this.taxType = taxType;
        this.taxFrom = taxFrom;
        this.taxUpto = taxUpto;
        this.cfTax = cfTax;
        this.cfPeanlty = cfPeanlty;
        this.cfInterest = cfInterest;
        this.taxRcptNo = taxrcpt;
        this.taxRcptDate = taxrcptdate;

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

    public String getCfTax() {
        return cfTax;
    }

    public void setCfTax(String cfTax) {
        this.cfTax = cfTax;
    }

 

    public String getCfPeanlty() {
        return cfPeanlty;
    }

    public void setCfPeanlty(String cfPeanlty) {
        this.cfPeanlty = cfPeanlty;
    }

  
    public String getCfInterest() {
        return cfInterest;
    }

    public void setCfInterest(String cfInterest) {
        this.cfInterest = cfInterest;
    }

    /**
     * @return the taxRcptNo
     */
    public String getTaxRcptNo() {
        return taxRcptNo;
    }

    /**
     * @param taxRcptNo the taxRcptNo to set
     */
    public void setTaxRcptNo(String taxRcptNo) {
        this.taxRcptNo = taxRcptNo;
    }

    /**
     * @return the taxRcptDate
     */
    public String getTaxRcptDate() {
        return taxRcptDate;
    }

    /**
     * @param taxRcptDate the taxRcptDate to set
     */
    public void setTaxRcptDate(String taxRcptDate) {
        this.taxRcptDate = taxRcptDate;
    }

  

    

   
}
