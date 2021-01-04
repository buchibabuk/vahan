/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.fee;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class BalanceCollectionFeeDobj implements Serializable {

    private String userCd;
    private long balFee;
    private long balFine;
    private long balFeeTotalAmount;
    private String applNo;
    private long balTax;
    private long balTaxFine;
    private long balTaxTotal;
    private long cashAmount;
    private long draftAmount;
    private long totalAmount;

    /**
     * @return the userCd
     */
    public String getUserCd() {
        return userCd;
    }

    /**
     * @param userCd the userCd to set
     */
    public void setUserCd(String userCd) {
        this.userCd = userCd;
    }

    /**
     * @return the balFee
     */
    public long getBalFee() {
        return balFee;
    }

    /**
     * @param balFee the balFee to set
     */
    public void setBalFee(long balFee) {
        this.balFee = balFee;
    }

    /**
     * @return the balFine
     */
    public long getBalFine() {
        return balFine;
    }

    /**
     * @param balFine the balFine to set
     */
    public void setBalFine(long balFine) {
        this.balFine = balFine;
    }

    /**
     * @return the balFeeTotalAmount
     */
    public long getBalFeeTotalAmount() {
        return balFeeTotalAmount;
    }

    /**
     * @param balFeeTotalAmount the balFeeTotalAmount to set
     */
    public void setBalFeeTotalAmount(long balFeeTotalAmount) {
        this.balFeeTotalAmount = balFeeTotalAmount;
    }

    /**
     * @return the applNo
     */
    public String getApplNo() {
        return applNo;
    }

    /**
     * @param applNo the applNo to set
     */
    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    /**
     * @return the balTax
     */
    public long getBalTax() {
        return balTax;
    }

    /**
     * @param balTax the balTax to set
     */
    public void setBalTax(long balTax) {
        this.balTax = balTax;
    }

    /**
     * @return the balTaxFine
     */
    public long getBalTaxFine() {
        return balTaxFine;
    }

    /**
     * @param balTaxFine the balTaxFine to set
     */
    public void setBalTaxFine(long balTaxFine) {
        this.balTaxFine = balTaxFine;
    }

    /**
     * @return the balTaxTotal
     */
    public long getBalTaxTotal() {
        return balTaxTotal;
    }

    /**
     * @param balTaxTotal the balTaxTotal to set
     */
    public void setBalTaxTotal(long balTaxTotal) {
        this.balTaxTotal = balTaxTotal;
    }

    /**
     * @return the cashAmount
     */
    public long getCashAmount() {
        return cashAmount;
    }

    /**
     * @param cashAmount the cashAmount to set
     */
    public void setCashAmount(long cashAmount) {
        this.cashAmount = cashAmount;
    }

    /**
     * @return the draftAmount
     */
    public long getDraftAmount() {
        return draftAmount;
    }

    /**
     * @param draftAmount the draftAmount to set
     */
    public void setDraftAmount(long draftAmount) {
        this.draftAmount = draftAmount;
    }

    /**
     * @return the totalAmount
     */
    public long getTotalAmount() {
        return totalAmount;
    }

    /**
     * @param totalAmount the totalAmount to set
     */
    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }
}
