/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.eapplication;

import java.io.Serializable;

public class PaymentDobj implements Serializable {

    private long totalPayableAmount;
    private String transNo;
    private String createdBy;
    private String userId, applNo, stateCd, regnNo, paymentType, balanceFeePaySelectedMode;
    private int actionCd, offCd, purCd;
    private String userCatg;

    public long getTotalPayableAmount() {
        return totalPayableAmount;
    }

    public void setTotalPayableAmount(long totalPayableAmount) {
        this.totalPayableAmount = totalPayableAmount;
    }

    public String getTransNo() {
        return transNo;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getActionCd() {
        return actionCd;
    }

    public void setActionCd(int actionCd) {
        this.actionCd = actionCd;
    }

    /**
     * @param transNo the transNo to set
     */
    public void setTransNo(String transNo) {
        this.transNo = transNo;
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
     * @return the stateCd
     */
    public String getStateCd() {
        return stateCd;
    }

    /**
     * @param stateCd the stateCd to set
     */
    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    /**
     * @return the offCd
     */
    public int getOffCd() {
        return offCd;
    }

    /**
     * @param offCd the offCd to set
     */
    public void setOffCd(int offCd) {
        this.offCd = offCd;
    }

    /**
     * @return the purCd
     */
    public int getPurCd() {
        return purCd;
    }

    /**
     * @param purCd the purCd to set
     */
    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the userCatg
     */
    public String getUserCatg() {
        return userCatg;
    }

    /**
     * @param userCatg the userCatg to set
     */
    public void setUserCatg(String userCatg) {
        this.userCatg = userCatg;
    }

    /**
     * @return the paymentType
     */
    public String getPaymentType() {
        return paymentType;
    }

    /**
     * @param paymentType the paymentType to set
     */
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    /**
     * @return the balanceFeePaySelectedMode
     */
    public String getBalanceFeePaySelectedMode() {
        return balanceFeePaySelectedMode;
    }

    /**
     * @param balanceFeePaySelectedMode the balanceFeePaySelectedMode to set
     */
    public void setBalanceFeePaySelectedMode(String balanceFeePaySelectedMode) {
        this.balanceFeePaySelectedMode = balanceFeePaySelectedMode;
    }
}
