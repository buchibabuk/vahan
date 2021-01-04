/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

/**
 *
 * @author nic
 */
public class PrintRcptParticularDtls {

    private String amount;
    private String surcharge;
    private String penalty;
    private String dateFrom;
    private String dateUpto;
    private String purpose;
    private String empName;
    private int total;
    private String grandTotal;

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
     * @return the statecd
     */
    public String getEmpName() {
        return empName;
    }

    /**
     * @param statecd the statecd to set
     */
    public void setEmpName(String empName) {
        this.empName = empName;
    }

    /**
     * @return the grandTotal
     */
    public String getGrandTotal() {
        return grandTotal;
    }

    /**
     * @param grandTotal the grandTotal to set
     */
    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }
}
