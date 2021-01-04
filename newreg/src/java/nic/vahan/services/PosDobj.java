/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services;

import java.io.Serializable;

/**
 *
 * @author ASHOK
 */
public class PosDobj implements Serializable {

    private String stateCd;
    private int offCd;
    private String appKey;
    private String username;
    private String externalRefNumber;
    private String status;
    private String success;
    private String tid;
    private String rrNumber;
    private String authCode;
    private long totalAmount;
    private String txnId;
    private String chargeSlipDate;
    private String cardLastFourDigit;
    private long postingDate;
    private String instrumentCode;
    private String serviceResponseCode;
    private String invoiceNumber;
    private String applNo;

    /**
     * @return the appKey
     */
    public String getAppKey() {
        return appKey;
    }

    /**
     * @param appKey the appKey to set
     */
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the externalRefNumber
     */
    public String getExternalRefNumber() {
        return externalRefNumber;
    }

    /**
     * @param externalRefNumber the externalRefNumber to set
     */
    public void setExternalRefNumber(String externalRefNumber) {
        this.externalRefNumber = externalRefNumber;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the tid
     */
    public String getTid() {
        return tid;
    }

    /**
     * @param tid the tid to set
     */
    public void setTid(String tid) {
        this.tid = tid;
    }

    /**
     * @return the rrNumber
     */
    public String getRrNumber() {
        return rrNumber;
    }

    /**
     * @param rrNumber the rrNumber to set
     */
    public void setRrNumber(String rrNumber) {
        this.rrNumber = rrNumber;
    }

    /**
     * @return the authCode
     */
    public String getAuthCode() {
        return authCode;
    }

    /**
     * @param authCode the authCode to set
     */
    public void setAuthCode(String authCode) {
        this.authCode = authCode;
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

    /**
     * @return the txnId
     */
    public String getTxnId() {
        return txnId;
    }

    /**
     * @param txnId the txnId to set
     */
    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    /**
     * @return the chargeSlipDate
     */
    public String getChargeSlipDate() {
        return chargeSlipDate;
    }

    /**
     * @param chargeSlipDate the chargeSlipDate to set
     */
    public void setChargeSlipDate(String chargeSlipDate) {
        this.chargeSlipDate = chargeSlipDate;
    }

    /**
     * @return the cardLastFourDigit
     */
    public String getCardLastFourDigit() {
        return cardLastFourDigit;
    }

    /**
     * @param cardLastFourDigit the cardLastFourDigit to set
     */
    public void setCardLastFourDigit(String cardLastFourDigit) {
        this.cardLastFourDigit = cardLastFourDigit;
    }

    /**
     * @return the postingDate
     */
    public long getPostingDate() {
        return postingDate;
    }

    /**
     * @param postingDate the postingDate to set
     */
    public void setPostingDate(long postingDate) {
        this.postingDate = postingDate;
    }

    /**
     * @return the success
     */
    public String getSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(String success) {
        this.success = success;
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
     * @return the instrumentCode
     */
    public String getInstrumentCode() {
        return instrumentCode;
    }

    /**
     * @param instrumentCode the instrumentCode to set
     */
    public void setInstrumentCode(String instrumentCode) {
        this.instrumentCode = instrumentCode;
    }

    /**
     * @return the serviceResponseCode
     */
    public String getServiceResponseCode() {
        return serviceResponseCode;
    }

    /**
     * @param serviceResponseCode the serviceResponseCode to set
     */
    public void setServiceResponseCode(String serviceResponseCode) {
        this.serviceResponseCode = serviceResponseCode;
    }

    /**
     * @return the invoiceNumber
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * @param invoiceNumber the invoiceNumber to set
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
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
}
