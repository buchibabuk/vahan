/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.dealer;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author pramod
 */
public class PendencyBankDobj implements Serializable {

    private String applNo;
    private String regnNo;
    private String ownerName;
    private String bankName;
    private String ifscCode;
    private String accountNo;
    private String statusCode;
    private String statusDescr;
    private String aadharNo;
    private String bankCd;
    private String regnDtStr;
    private String fatherName;
    private String curAddress;
    private boolean isVerified;
    private int subsidyAmount;
    private String dlLlNo;
    private Date regnDt;

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
     * @return the ownerName
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * @param ownerName the ownerName to set
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * @return the bankName
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * @param bankName the bankName to set
     */
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    /**
     * @return the ifscCode
     */
    public String getIfscCode() {
        return ifscCode;
    }

    /**
     * @param ifscCode the ifscCode to set
     */
    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    /**
     * @return the accountNo
     */
    public String getAccountNo() {
        return accountNo;
    }

    /**
     * @param accountNo the accountNo to set
     */
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    /**
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the aadharNo
     */
    public String getAadharNo() {
        return aadharNo;
    }

    /**
     * @param aadharNo the aadharNo to set
     */
    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    /**
     * @return the bankCd
     */
    public String getBankCd() {
        return bankCd;
    }

    /**
     * @param bankCd the bankCd to set
     */
    public void setBankCd(String bankCd) {
        this.bankCd = bankCd;
    }

    /**
     * @return the statusCode
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode the statusCode to set
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * @return the statusDescr
     */
    public String getStatusDescr() {
        return statusDescr;
    }

    /**
     * @param statusDescr the statusDescr to set
     */
    public void setStatusDescr(String statusDescr) {
        this.statusDescr = statusDescr;
    }

//    /**
//     * @return the regnDt
//     */
//    public String getRegnDt() {
//        return regnDt;
//    }
//
//    /**
//     * @param regnDt the regnDt to set
//     */
//    public void setRegnDt(String regnDt) {
//        this.regnDt = regnDt;
//    }

    /**
     * @return the fatherName
     */
    public String getFatherName() {
        return fatherName;
    }

    /**
     * @param fatherName the fatherName to set
     */
    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    /**
     * @return the curAddress
     */
    public String getCurAddress() {
        return curAddress;
    }

    /**
     * @param curAddress the curAddress to set
     */
    public void setCurAddress(String curAddress) {
        this.curAddress = curAddress;
    }

    /**
     * @return the isVerified
     */
    public boolean isIsVerified() {
        return isVerified;
    }

    /**
     * @param isVerified the isVerified to set
     */
    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    /**
     * @return the subsidyAmount
     */
    public int getSubsidyAmount() {
        return subsidyAmount;
    }

    /**
     * @param subsidyAmount the subsidyAmount to set
     */
    public void setSubsidyAmount(int subsidyAmount) {
        this.subsidyAmount = subsidyAmount;
    }

    /**
     * @return the dlLlNo
     */
    public String getDlLlNo() {
        return dlLlNo;
    }

    /**
     * @param dlLlNo the dlLlNo to set
     */
    public void setDlLlNo(String dlLlNo) {
        this.dlLlNo = dlLlNo;
    }

    /**
     * @return the regnDt
     */
    public Date getRegnDt() {
        return regnDt;
    }

    /**
     * @param regnDt the regnDt to set
     */
    public void setRegnDt(Date regnDt) {
        this.regnDt = regnDt;
    }

    /**
     * @return the regnDtStr
     */
    public String getRegnDtStr() {
        return regnDtStr;
    }

    /**
     * @param regnDtStr the regnDtStr to set
     */
    public void setRegnDtStr(String regnDtStr) {
        this.regnDtStr = regnDtStr;
    }
}
