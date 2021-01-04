/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;

/**
 *
 * @author nic
 */
public class CashRecieptSubListDobj implements Serializable {

    private String amount;
    private String surcharge;
    private String excessAmt;
    private String cashAmt;
    private String tax;
    private String exempted;
    private String prv_adjustment;
    private String rebate;
    private String interest;
    private String penalty;
    private String dateFrom;
    private String dateUpto;
    private String purpose = "";
    private int total;
    private String empName;
    private Boolean blnissurcharge;
    private Boolean blnisexcessAmt;
    private Boolean blniscashAmt;
    private Boolean blnisrebate;
    private Boolean blnisexempted;
    private Boolean blnisprv_adjustment;
    private Boolean blnisinterest;
    private Boolean blnispenalty;
    private Boolean blnisfine;
    private Integer purCd = -1;

    /**
     * @return the amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * @return the surcharge
     */
    public String getSurcharge() {
        return surcharge;
    }

    /**
     * @param surcharge the surcharge to set
     */
    public void setSurcharge(String surcharge) {
        this.surcharge = surcharge;
    }

    /**
     * @return the penalty
     */
    public String getPenalty() {
        return penalty;
    }

    /**
     * @param penalty the penalty to set
     */
    public void setPenalty(String penalty) {
        this.penalty = penalty;
    }

    /**
     * @return the dateFrom
     */
    public String getDateFrom() {
        return dateFrom;
    }

    /**
     * @param dateFrom the dateFrom to set
     */
    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    /**
     * @return the dateUpto
     */
    public String getDateUpto() {
        return dateUpto;
    }

    /**
     * @param dateUpto the dateUpto to set
     */
    public void setDateUpto(String dateUpto) {
        this.dateUpto = dateUpto;
    }

    /**
     * @return the purpose
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * @param purpose the purpose to set
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * @return the empName
     */
    public String getEmpName() {
        return empName;
    }

    /**
     * @param empName the empName to set
     */
    public void setEmpName(String empName) {
        this.empName = empName;
    }

    /**
     * @return the blnissurcharge
     */
    public Boolean getBlnissurcharge() {
        return blnissurcharge;
    }

    /**
     * @param blnissurcharge the blnissurcharge to set
     */
    public void setBlnissurcharge(Boolean blnissurcharge) {
        this.blnissurcharge = blnissurcharge;
    }

    /**
     * @return the tax
     */
    public String getTax() {
        return tax;
    }

    /**
     * @param tax the tax to set
     */
    public void setTax(String tax) {
        this.tax = tax;
    }

    /**
     * @return the exempted
     */
    public String getExempted() {
        return exempted;
    }

    /**
     * @param exempted the exempted to set
     */
    public void setExempted(String exempted) {
        this.exempted = exempted;
    }

    /**
     * @return the prv_adjustment
     */
    public String getPrv_adjustment() {
        return prv_adjustment;
    }

    /**
     * @param prv_adjustment the prv_adjustment to set
     */
    public void setPrv_adjustment(String prv_adjustment) {
        this.prv_adjustment = prv_adjustment;
    }

    /**
     * @return the rebate
     */
    public String getRebate() {
        return rebate;
    }

    /**
     * @param rebate the rebate to set
     */
    public void setRebate(String rebate) {
        this.rebate = rebate;
    }

    /**
     * @return the interest
     */
    public String getInterest() {
        return interest;
    }

    /**
     * @param interest the interest to set
     */
    public void setInterest(String interest) {
        this.interest = interest;
    }

    /**
     * @return the blnisrebate
     */
    public Boolean getBlnisrebate() {
        return blnisrebate;
    }

    /**
     * @param blnisrebate the blnisrebate to set
     */
    public void setBlnisrebate(Boolean blnisrebate) {
        this.blnisrebate = blnisrebate;
    }

    /**
     * @return the blnisexempted
     */
    public Boolean getBlnisexempted() {
        return blnisexempted;
    }

    /**
     * @param blnisexempted the blnisexempted to set
     */
    public void setBlnisexempted(Boolean blnisexempted) {
        this.blnisexempted = blnisexempted;
    }

    /**
     * @return the blnisprv_adjustment
     */
    public Boolean getBlnisprv_adjustment() {
        return blnisprv_adjustment;
    }

    /**
     * @param blnisprv_adjustment the blnisprv_adjustment to set
     */
    public void setBlnisprv_adjustment(Boolean blnisprv_adjustment) {
        this.blnisprv_adjustment = blnisprv_adjustment;
    }

    /**
     * @return the blnisinterest
     */
    public Boolean getBlnisinterest() {
        return blnisinterest;
    }

    /**
     * @param blnisinterest the blnisinterest to set
     */
    public void setBlnisinterest(Boolean blnisinterest) {
        this.blnisinterest = blnisinterest;
    }

    /**
     * @return the blnispenalty
     */
    public Boolean getBlnispenalty() {
        return blnispenalty;
    }

    /**
     * @param blnispenalty the blnispenalty to set
     */
    public void setBlnispenalty(Boolean blnispenalty) {
        this.blnispenalty = blnispenalty;
    }

    /**
     * @return the purCd
     */
    public Integer getPurCd() {
        return purCd;
    }

    /**
     * @param purCd the purCd to set
     */
    public void setPurCd(Integer purCd) {
        this.purCd = purCd;
    }

    /**
     * @return the blnisfine
     */
    public Boolean getBlnisfine() {
        return blnisfine;
    }

    /**
     * @param blnisfine the blnisfine to set
     */
    public void setBlnisfine(Boolean blnisfine) {
        this.blnisfine = blnisfine;
    }

    public String getExcessAmt() {
        return excessAmt;
    }

    public void setExcessAmt(String excessAmt) {
        this.excessAmt = excessAmt;
    }

    public String getCashAmt() {
        return cashAmt;
    }

    public void setCashAmt(String cashAmt) {
        this.cashAmt = cashAmt;
    }

    public Boolean getBlnisexcessAmt() {
        return blnisexcessAmt;
    }

    public void setBlnisexcessAmt(Boolean blnisexcessAmt) {
        this.blnisexcessAmt = blnisexcessAmt;
    }

    public Boolean getBlniscashAmt() {
        return blniscashAmt;
    }

    public void setBlniscashAmt(Boolean blniscashAmt) {
        this.blniscashAmt = blniscashAmt;
    }
}
