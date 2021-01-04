/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.tradecert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author ManuSri
 */
public class DeduplicateTradeCertDobj {

    private static final Logger LOGGER = Logger.getLogger(DeduplicateTradeCertDobj.class);
    /**
     * Used in Search Query page section
     */
    private Integer rtoOfficeCode;
    private String rtoOfficeName;
    private String stateCd;
    private String dealer;
    private String vehCatg;
    private String dealerChoiceCondition;
    /**
     * Used in TC records display in table section
     */
    private String dealerName;
    private String userId;
    private String vehCatgName;
    private Integer srNo;
    private String applNo;
    private String newApplNo;
    private String certNo;
    private String newCertNo;
    private Date issueDt;
    private String issueDtStr;
    private Integer noOfVeh;
    private Date validUpto;
    private String validUptoStr;
    private Date validFrom;
    private String validFromStr;
    private Double fee;
    private String receiptNo;
    private Date receiptDt;
    private String receiptDtStr;
    private List<String> systemGeneratedTcNumberList;

    /**
     * Default Constructor
     */
    public DeduplicateTradeCertDobj() {
        rtoOfficeCode = null;
        rtoOfficeName = "";
        stateCd = "";
        dealer = "";
        vehCatg = "";
        dealerChoiceCondition = "";
        dealerName = "";
        userId = "";
        vehCatgName = "";
        srNo = null;
        applNo = "";
        newApplNo = "";
        certNo = "";
        newCertNo = "";
        issueDt = null;
        issueDtStr = "";
        noOfVeh = null;
        validUpto = null;
        validUptoStr = "";
        validFrom = null;
        validFromStr = "";
        fee = null;
        receiptNo = "";
        receiptDt = null;
        receiptDtStr = "";
        systemGeneratedTcNumberList = new ArrayList<>();
    }

    /**
     * Reset method
     */
    public void reset() {
        setRtoOfficeCode(null);
        setRtoOfficeName("");
        setDealer("");
        setVehCatg("");
        setDealerChoiceCondition("");
        setDealerName("");
        setUserId("");
        setVehCatgName("");
        setSrNo(null);
        setApplNo("");
        setNewApplNo("");
        setCertNo("");
        setNewCertNo("");
        setIssueDt(null);
        setIssueDtStr("");
        setNoOfVeh(null);
        setValidUpto(null);
        setValidUptoStr("");
        setValidFrom(null);
        setValidFromStr("");
        setFee(null);
        setReceiptNo("");
        setReceiptDt(null);
        setReceiptDtStr("");
        setStateCd("");
        getSystemGeneratedTcNumberList().clear();
    }

    /**
     * @param dobj the dobj to copy
     */
    public DeduplicateTradeCertDobj copy(DeduplicateTradeCertDobj dobj) {
        dobj.setApplNo(this.getApplNo());
        dobj.setNewApplNo(this.getNewApplNo());
        dobj.setCertNo(this.getCertNo());
        dobj.setNewCertNo(this.getNewCertNo());
        dobj.setDealer(this.getDealer());
        dobj.setDealerChoiceCondition(this.getDealerChoiceCondition());
        dobj.setDealerName(this.getDealerName());
        dobj.setUserId(this.getUserId());
        dobj.setIssueDt(this.getIssueDt());
        dobj.setIssueDtStr(this.getIssueDtStr());
        dobj.setNoOfVeh(this.getNoOfVeh());
        dobj.setRtoOfficeCode(this.getRtoOfficeCode());
        dobj.setRtoOfficeName(this.getRtoOfficeName());
        dobj.setStateCd(this.getStateCd());
        dobj.setSrNo(this.getSrNo());
        dobj.setValidFrom(this.getValidFrom());
        dobj.setValidFromStr(this.getValidFromStr());
        dobj.setValidUpto(this.getValidUpto());
        dobj.setValidUptoStr(this.getValidUptoStr());
        dobj.setVehCatg(this.getVehCatg());
        dobj.setVehCatgName(this.getVehCatgName());
        dobj.setFee(this.getFee());
        dobj.setReceiptNo(this.getReceiptNo());
        dobj.setReceiptDt(this.getReceiptDt());
        dobj.setReceiptDtStr(this.getReceiptDtStr());
        dobj.getSystemGeneratedTcNumberList().clear();
        dobj.getSystemGeneratedTcNumberList().addAll(this.getSystemGeneratedTcNumberList());
        return dobj;
    }

    /**
     * @return the rtoOfficeCode
     */
    public Integer getRtoOfficeCode() {
        return rtoOfficeCode;
    }

    /**
     *
     * @param rtoOfficeCode the rtoOfficeCode to set
     */
    public void setRtoOfficeCode(Integer rtoOfficeCode) {
        this.rtoOfficeCode = rtoOfficeCode;
    }

    /**
     * @return the rtoOfficeName
     */
    public String getRtoOfficeName() {
        return rtoOfficeName;
    }

    /**
     * @param rtoOfficeName the rtoOfficeName to set
     */
    public void setRtoOfficeName(String rtoOfficeName) {
        this.rtoOfficeName = rtoOfficeName;
    }

    /**
     * @return the dealer
     */
    public String getDealer() {
        return dealer;
    }

    /**
     * @param dealer the dealer to set
     */
    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    /**
     * @return the vehCatg
     */
    public String getVehCatg() {
        return vehCatg;
    }

    /**
     * @param vehCatg the vehCatg to set
     */
    public void setVehCatg(String vehCatg) {
        this.vehCatg = vehCatg;
    }

    /**
     * @return the dealerChoiceCondition
     */
    public String getDealerChoiceCondition() {
        return dealerChoiceCondition;
    }

    /**
     * @param dealerChoiceCondition the dealerChoiceCondition to set
     */
    public void setDealerChoiceCondition(String dealerChoiceCondition) {
        this.dealerChoiceCondition = dealerChoiceCondition;
    }

    /**
     * @return the dealerName
     */
    public String getDealerName() {
        return dealerName;
    }

    /**
     * @param dealerName the dealerName to set
     */
    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    /**
     * @return the vehCatgName
     */
    public String getVehCatgName() {
        return vehCatgName;
    }

    /**
     * @param vehCatgName the vehCatgName to set
     */
    public void setVehCatgName(String vehCatgName) {
        this.vehCatgName = vehCatgName;
    }

    /**
     * @return the srNo
     */
    public Integer getSrNo() {
        return srNo;
    }

    /**
     * @param srNo the srNo to set
     */
    public void setSrNo(Integer srNo) {
        this.srNo = srNo;
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
     * @return the certNo
     */
    public String getCertNo() {
        return certNo;
    }

    /**
     * @param certNo the certNo to set
     */
    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    /**
     * @return the issueDt
     */
    public Date getIssueDt() {
        return issueDt;
    }

    /**
     * @param issueDt the issueDt to set
     */
    public void setIssueDt(Date issueDt) {
        this.issueDt = issueDt;
    }

    /**
     * @return the noOfVeh
     */
    public Integer getNoOfVeh() {
        return noOfVeh;
    }

    /**
     * @param noOfVeh the noOfVeh to set
     */
    public void setNoOfVeh(Integer noOfVeh) {
        this.noOfVeh = noOfVeh;
    }

    /**
     * @return the validUpto
     */
    public Date getValidUpto() {
        return validUpto;
    }

    /**
     * @param validUpto the validUpto to set
     */
    public void setValidUpto(Date validUpto) {
        this.validUpto = validUpto;
    }

    /**
     * @return the validFrom
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * @param validFrom the validFrom to set
     */
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * @return the issueDtStr
     */
    public String getIssueDtStr() {
        return issueDtStr;
    }

    /**
     * @param issueDtStr the issueDtStr to set
     */
    public void setIssueDtStr(String issueDtStr) {
        this.issueDtStr = issueDtStr;
    }

    /**
     * @return the validUptoStr
     */
    public String getValidUptoStr() {
        return validUptoStr;
    }

    /**
     * @param validUptoStr the validUptoStr to set
     */
    public void setValidUptoStr(String validUptoStr) {
        this.validUptoStr = validUptoStr;
    }

    /**
     * @return the validFromStr
     */
    public String getValidFromStr() {
        return validFromStr;
    }

    /**
     * @param validFromStr the validFromStr to set
     */
    public void setValidFromStr(String validFromStr) {
        this.validFromStr = validFromStr;
    }

    /**
     * @return the newApplNo
     */
    public String getNewApplNo() {
        return newApplNo;
    }

    /**
     * @param newApplNo the newApplNo to set
     */
    public void setNewApplNo(String newApplNo) {
        this.newApplNo = newApplNo;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the fee
     */
    public Double getFee() {
        return fee;
    }

    /**
     * @param fee the fee to set
     */
    public void setFee(Double fee) {
        this.fee = fee;
    }

    /**
     * @return the newCertNo
     */
    public String getNewCertNo() {
        return newCertNo;
    }

    /**
     * @param newCertNo the newCertNo to set
     */
    public void setNewCertNo(String newCertNo) {
        this.newCertNo = newCertNo;
    }

    /**
     * @return the receiptNo
     */
    public String getReceiptNo() {
        return receiptNo;
    }

    /**
     * @param receiptNo the receiptNo to set
     */
    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    /**
     * @return the receiptDt
     */
    public Date getReceiptDt() {
        return receiptDt;
    }

    /**
     * @param receiptDt the receiptDt to set
     */
    public void setReceiptDt(Date receiptDt) {
        this.receiptDt = receiptDt;
    }

    /**
     * @return the receiptDtStr
     */
    public String getReceiptDtStr() {
        return receiptDtStr;
    }

    /**
     * @param receiptDtStr the receiptDtStr to set
     */
    public void setReceiptDtStr(String receiptDtStr) {
        this.receiptDtStr = receiptDtStr;
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
     * @return the systemGeneratedTcNumberList
     */
    public List<String> getSystemGeneratedTcNumberList() {
        return systemGeneratedTcNumberList;
    }

    /**
     * @param systemGeneratedTcNumberList the systemGeneratedTcNumberList to set
     */
    public void setSystemGeneratedTcNumberList(List<String> systemGeneratedTcNumberList) {
        this.systemGeneratedTcNumberList = systemGeneratedTcNumberList;
    }
}
