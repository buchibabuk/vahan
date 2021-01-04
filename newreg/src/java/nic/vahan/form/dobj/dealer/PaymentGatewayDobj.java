/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.dealer;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author kaptan singh
 */
public class PaymentGatewayDobj implements Serializable {

    private String applNo;
    private Long amount;
    private Long ttlAmount;
    private String regnNo;
    private String chassisNo;
    private Long penalty;
    private String periodUpto;
    private String periodFrom;
    private String receiptNo;
    private int purCd;
    private String transactionNo;
    private String purpose;
    private String taxMode;
    private String ownerName;
    private String mobileNumber;
    private String emailId;
    private int offCode;
    private String stateCd;
    private String paymentId;
    private String totalNoOfApplicationsIncart;
    private int fuel;
    private Date opDate;
    private int dlrPurCd;
    private String applicationNumberList;
    private String reason;
    private String bankReferenceNo;
    private String bankVerifiedDate;
    private String pgiTransId;
    private String appl_dt;;
//    /**
//     * @return the amount
//     */

    public Long getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(Long amount) {
        this.amount = amount;
    }
//
//    /**
//     * @return the penalty
//     */

    public Long getPenalty() {
        return penalty;
    }

    /**
     * @param penalty the penalty to set
     */
    public void setPenalty(Long penalty) {
        this.penalty = penalty;
    }

    /**
     * @return the ttlAmount
     */
    public Long getTtlAmount() {
        return ttlAmount;
    }

    /**
     * @param ttlAmount the ttlAmount to set
     */
    public void setTtlAmount(Long ttlAmount) {
        this.ttlAmount = ttlAmount;
    }
//
//    /**
//     * @return the receiptNo
//     */

    public String getReceiptNo() {
        return receiptNo;
    }

    /**
     * @param receiptNo the receiptNo to set
     */
    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }
//

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

//   /**
//     * @return the regnNo
//     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }
//
//    /**
//     * @return the chassisNo
//     */

    public String getChassisNo() {
        return chassisNo;
    }

    /**
     * @param chassisNo the chassisNo to set
     */
    public void setChassisNo(String chassisNo) {
        this.chassisNo = chassisNo;
    }
//
//    /**
//     * @return the periodUpto
//     */

    public String getPeriodUpto() {
        return periodUpto;
    }

    /**
     * @param periodUpto the periodUpto to set
     */
    public void setPeriodUpto(String periodUpto) {
        this.periodUpto = periodUpto;
    }
//
//    /**
//     * @return the periodFrom
//     */

    public String getPeriodFrom() {
        return periodFrom;
    }

    /**
     * @param periodFrom the periodFrom to set
     */
    public void setPeriodFrom(String periodFrom) {
        this.periodFrom = periodFrom;
    }
//
//    /**
//     * @return the purCd
//     */

    public int getPurCd() {
        return purCd;
    }

    /**
     * @param purCd the purCd to set
     */
    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }
//

    /**
     * @return the transactionNo
     */
    public String getTransactionNo() {
        return transactionNo;
    }

    /**
     * @param transactionNo the transactionNo to set
     */
    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }
//
//    /**
//     * @return the purpose
//     */

    public String getPurpose() {
        return purpose;
    }

    /**
     * @param purpose the purpose to set
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
//
//    /**
//     * @return the taxMode
//     */

    public String getTaxMode() {
        return taxMode;
    }

    /**
     * @param taxMode the taxMode to set
     */
    public void setTaxMode(String taxMode) {
        this.taxMode = taxMode;
    }
//
//    /**
//     * @return the ownerName
//     */

    public String getOwnerName() {
        return ownerName;
    }

    /**
     * @param ownerName the ownerName to set
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
//
//    /**
//     * @return the mobileNumber
//     */

    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * @param mobileNumber the mobileNumber to set
     */
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
//
//    /**
//     * @return the emailId
//     */

    public String getEmailId() {
        return emailId;
    }

    /**
     * @param emailId the emailId to set
     */
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

//    /**
//     * @return the offCode
//     */
    public int getOffCode() {
        return offCode;
    }

    /**
     * @param offCode the offCode to set
     */
    public void setOffCode(int offCode) {
        this.offCode = offCode;
    }
//

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
     * @return the paymentId
     */
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * @param paymentId the paymentId to set
     */
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    /**
     * @return the totalNoOfApplicationsIncart
     */
    public String getTotalNoOfApplicationsIncart() {
        return totalNoOfApplicationsIncart;
    }

    /**
     * @param totalNoOfApplicationsIncart the totalNoOfApplicationsIncart to set
     */
    public void setTotalNoOfApplicationsIncart(String totalNoOfApplicationsIncart) {
        this.totalNoOfApplicationsIncart = totalNoOfApplicationsIncart;
    }

    /**
     * @return the fuel
     */
    public int getFuel() {
        return fuel;
    }

    /**
     * @param fuel the fuel to set
     */
    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    /**
     * @return the opDate
     */
    public Date getOpDate() {
        return opDate;
    }

    /**
     * @param opDate the opDate to set
     */
    public void setOpDate(Date opDate) {
        this.opDate = opDate;
    }

    /**
     * @return the dlrPurCd
     */
    public int getDlrPurCd() {
        return dlrPurCd;
    }

    /**
     * @param dlrPurCd the dlrPurCd to set
     */
    public void setDlrPurCd(int dlrPurCd) {
        this.dlrPurCd = dlrPurCd;
    }

    /**
     * @return the applicationNumberList
     */
    public String getApplicationNumberList() {
        return applicationNumberList;
    }

    /**
     * @param applicationNumberList the applicationNumberList to set
     */
    public void setApplicationNumberList(String applicationNumberList) {
        this.applicationNumberList = applicationNumberList;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the bankReferenceNo
     */
    public String getBankReferenceNo() {
        return bankReferenceNo;
    }

    /**
     * @param bankReferenceNo the bankReferenceNo to set
     */
    public void setBankReferenceNo(String bankReferenceNo) {
        this.bankReferenceNo = bankReferenceNo;
    }

    /**
     * @return the bankVerifiedDate
     */
    public String getBankVerifiedDate() {
        return bankVerifiedDate;
    }

    /**
     * @param bankVerifiedDate the bankVerifiedDate to set
     */
    public void setBankVerifiedDate(String bankVerifiedDate) {
        this.bankVerifiedDate = bankVerifiedDate;
    }

    /**
     * @return the pgiTransId
     */
    public String getPgiTransId() {
        return pgiTransId;
    }

    /**
     * @param pgiTransId the pgiTransId to set
     */
    public void setPgiTransId(String pgiTransId) {
        this.pgiTransId = pgiTransId;
    }

    public String getAppl_dt() {
        return appl_dt;
    }

    public void setAppl_dt(String appl_dt) {
        this.appl_dt = appl_dt;
    }
}
