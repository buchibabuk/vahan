/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import nic.vahan.form.bean.TaxFormPanelBean;

public class OnlinePayDobj implements Serializable {

    private String receiptNo;
    private String applNo;
    private Owner_dobj ownerDobj;
    private List<OnlinePayDobj> taxBreakUpDetails;
    private List<OnlinePayDobj> feeBreakUpDetails;
    private String pmt_type_code;
    private String pmtCatg;
    private String services_TYPE;
    private String rout_code;
    private String rout_length;
    private String numberOfTrips;
    private String domain_CODE;
    private boolean validForTemp;
    private Double taxBreakUpAmount;
    private Double taxBreakUpAmount1;
    private Double taxBreakUpAmount2;
    private Double taxBreakUpSurcharge;
    private Double taxBreakUpRebate;
    private Double taxBreakUpPenalty;
    private Double taxBreakUpInterest;
    private int srNo;
    private int prevAdjustement;
    private String periodUpto;
    private String periodFrom;
    private String stateCd;
    private int offCode;
    private Long exempted;
    private int purCd;
    private String regnNo;
    private String chassisNo;
    private String transactionNo;
    private Long penalty;
    private String ownerName;
    private Long amount;
    private String mobileNumber;
    private String emailId;
    private String taxMode;
    private String transactionDate;
    private int transactionAmount;
    private String stateName;
    private String bankReferenceNo;
    private String noOfAdvUnits;
    private String applNoList;
    private int applNoCount;
    private String reverifyActionType;
    private String treasuryRefNo;
    private Timestamp paymentDate;
    private List<TaxFormPanelBean> taxFormPanelBeanList = new ArrayList<>();
    private List<EpayDobj> common_fee_chargeList = new ArrayList<>();
    private String officeName;
    private String dealerName;
    private String vhClassDesc;
    private String stateLogo;
    private String imageBackground;
    private long feeBreakUpAmount;
    private long feeBreakUpFine;
    private long feeBreakUpRebate;
    private long feeBreakUpSurcharge;
    private long feeBreakUpInterest;

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
     * @return the ownerDobj
     */
    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    /**
     * @param ownerDobj the ownerDobj to set
     */
    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    /**
     * @return the taxBreakUpDetails
     */
    public List<OnlinePayDobj> getTaxBreakUpDetails() {
        return taxBreakUpDetails;
    }

    /**
     * @param taxBreakUpDetails the taxBreakUpDetails to set
     */
    public void setTaxBreakUpDetails(List<OnlinePayDobj> taxBreakUpDetails) {
        this.taxBreakUpDetails = taxBreakUpDetails;
    }

    /**
     * @return the services_TYPE
     */
    public String getServices_TYPE() {
        return services_TYPE;
    }

    /**
     * @param services_TYPE the services_TYPE to set
     */
    public void setServices_TYPE(String services_TYPE) {
        this.services_TYPE = services_TYPE;
    }

    /**
     * @return the rout_code
     */
    public String getRout_code() {
        return rout_code;
    }

    /**
     * @param rout_code the rout_code to set
     */
    public void setRout_code(String rout_code) {
        this.rout_code = rout_code;
    }

    /**
     * @return the rout_length
     */
    public String getRout_length() {
        return rout_length;
    }

    /**
     * @param rout_length the rout_length to set
     */
    public void setRout_length(String rout_length) {
        this.rout_length = rout_length;
    }

    /**
     * @return the numberOfTrips
     */
    public String getNumberOfTrips() {
        return numberOfTrips;
    }

    /**
     * @param numberOfTrips the numberOfTrips to set
     */
    public void setNumberOfTrips(String numberOfTrips) {
        this.numberOfTrips = numberOfTrips;
    }

    /**
     * @return the domain_CODE
     */
    public String getDomain_CODE() {
        return domain_CODE;
    }

    /**
     * @param domain_CODE the domain_CODE to set
     */
    public void setDomain_CODE(String domain_CODE) {
        this.domain_CODE = domain_CODE;
    }

    /**
     * @return the pmt_type_code
     */
    public String getPmt_type_code() {
        return pmt_type_code;
    }

    /**
     * @param pmt_type_code the pmt_type_code to set
     */
    public void setPmt_type_code(String pmt_type_code) {
        this.pmt_type_code = pmt_type_code;
    }

    /**
     * @return the pmtCatg
     */
    public String getPmtCatg() {
        return pmtCatg;
    }

    /**
     * @param pmtCatg the pmtCatg to set
     */
    public void setPmtCatg(String pmtCatg) {
        this.pmtCatg = pmtCatg;
    }

    /**
     * @return the validForTemp
     */
    public boolean isValidForTemp() {
        return validForTemp;
    }

    /**
     * @param validForTemp the validForTemp to set
     */
    public void setValidForTemp(boolean validForTemp) {
        this.validForTemp = validForTemp;
    }

    /**
     * @return the taxBreakUpAmount
     */
    public Double getTaxBreakUpAmount() {
        return taxBreakUpAmount;
    }

    /**
     * @param taxBreakUpAmount the taxBreakUpAmount to set
     */
    public void setTaxBreakUpAmount(Double taxBreakUpAmount) {
        this.taxBreakUpAmount = taxBreakUpAmount;
    }

    /**
     * @return the taxBreakUpAmount1
     */
    public Double getTaxBreakUpAmount1() {
        return taxBreakUpAmount1;
    }

    /**
     * @param taxBreakUpAmount1 the taxBreakUpAmount1 to set
     */
    public void setTaxBreakUpAmount1(Double taxBreakUpAmount1) {
        this.taxBreakUpAmount1 = taxBreakUpAmount1;
    }

    /**
     * @return the taxBreakUpAmount2
     */
    public Double getTaxBreakUpAmount2() {
        return taxBreakUpAmount2;
    }

    /**
     * @param taxBreakUpAmount2 the taxBreakUpAmount2 to set
     */
    public void setTaxBreakUpAmount2(Double taxBreakUpAmount2) {
        this.taxBreakUpAmount2 = taxBreakUpAmount2;
    }

    /**
     * @return the taxBreakUpSurcharge
     */
    public Double getTaxBreakUpSurcharge() {
        return taxBreakUpSurcharge;
    }

    /**
     * @param taxBreakUpSurcharge the taxBreakUpSurcharge to set
     */
    public void setTaxBreakUpSurcharge(Double taxBreakUpSurcharge) {
        this.taxBreakUpSurcharge = taxBreakUpSurcharge;
    }

    /**
     * @return the taxBreakUpRebate
     */
    public Double getTaxBreakUpRebate() {
        return taxBreakUpRebate;
    }

    /**
     * @param taxBreakUpRebate the taxBreakUpRebate to set
     */
    public void setTaxBreakUpRebate(Double taxBreakUpRebate) {
        this.taxBreakUpRebate = taxBreakUpRebate;
    }

    /**
     * @return the taxBreakUpPenalty
     */
    public Double getTaxBreakUpPenalty() {
        return taxBreakUpPenalty;
    }

    /**
     * @param taxBreakUpPenalty the taxBreakUpPenalty to set
     */
    public void setTaxBreakUpPenalty(Double taxBreakUpPenalty) {
        this.taxBreakUpPenalty = taxBreakUpPenalty;
    }

    /**
     * @return the taxBreakUpInterest
     */
    public Double getTaxBreakUpInterest() {
        return taxBreakUpInterest;
    }

    /**
     * @param taxBreakUpInterest the taxBreakUpInterest to set
     */
    public void setTaxBreakUpInterest(Double taxBreakUpInterest) {
        this.taxBreakUpInterest = taxBreakUpInterest;
    }

    /**
     * @return the srNo
     */
    public int getSrNo() {
        return srNo;
    }

    /**
     * @param srNo the srNo to set
     */
    public void setSrNo(int srNo) {
        this.srNo = srNo;
    }

    /**
     * @return the prevAdjustement
     */
    public int getPrevAdjustement() {
        return prevAdjustement;
    }

    /**
     * @param prevAdjustement the prevAdjustement to set
     */
    public void setPrevAdjustement(int prevAdjustement) {
        this.prevAdjustement = prevAdjustement;
    }

    /**
     * @return the periodUpto
     */
    public String getPeriodUpto() {
        return periodUpto;
    }

    /**
     * @param periodUpto the periodUpto to set
     */
    public void setPeriodUpto(String periodUpto) {
        this.periodUpto = periodUpto;
    }

    /**
     * @return the periodFrom
     */
    public String getPeriodFrom() {
        return periodFrom;
    }

    /**
     * @param periodFrom the periodFrom to set
     */
    public void setPeriodFrom(String periodFrom) {
        this.periodFrom = periodFrom;
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
     * @return the offCode
     */
    public int getOffCode() {
        return offCode;
    }

    /**
     * @param offCode the offCode to set
     */
    public void setOffCode(int offCode) {
        this.offCode = offCode;
    }

    /**
     * @return the exempted
     */
    public Long getExempted() {
        return exempted;
    }

    /**
     * @param exempted the exempted to set
     */
    public void setExempted(Long exempted) {
        this.exempted = exempted;
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
     * @return the chassisNo
     */
    public String getChassisNo() {
        return chassisNo;
    }

    /**
     * @param chassisNo the chassisNo to set
     */
    public void setChassisNo(String chassisNo) {
        this.chassisNo = chassisNo;
    }

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

    /**
     * @return the penalty
     */
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
     * @return the amount
     */
    public Long getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(Long amount) {
        this.amount = amount;
    }

    /**
     * @return the mobileNumber
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * @param mobileNumber the mobileNumber to set
     */
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    /**
     * @return the emailId
     */
    public String getEmailId() {
        return emailId;
    }

    /**
     * @param emailId the emailId to set
     */
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    /**
     * @return the taxMode
     */
    public String getTaxMode() {
        return taxMode;
    }

    /**
     * @param taxMode the taxMode to set
     */
    public void setTaxMode(String taxMode) {
        this.taxMode = taxMode;
    }

    /**
     * @return the transactionDate
     */
    public String getTransactionDate() {
        return transactionDate;
    }

    /**
     * @param transactionDate the transactionDate to set
     */
    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * @return the transactionAmount
     */
    public int getTransactionAmount() {
        return transactionAmount;
    }

    /**
     * @param transactionAmount the transactionAmount to set
     */
    public void setTransactionAmount(int transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    /**
     * @return the stateName
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * @param stateName the stateName to set
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
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
     * @return the noOfAdvUnits
     */
    public String getNoOfAdvUnits() {
        return noOfAdvUnits;
    }

    /**
     * @param noOfAdvUnits the noOfAdvUnits to set
     */
    public void setNoOfAdvUnits(String noOfAdvUnits) {
        this.noOfAdvUnits = noOfAdvUnits;
    }

    /**
     * @return the applNoList
     */
    public String getApplNoList() {
        return applNoList;
    }

    /**
     * @param applNoList the applNoList to set
     */
    public void setApplNoList(String applNoList) {
        this.applNoList = applNoList;
    }

    /**
     * @return the applicationNoCount
     */
    public int getApplNoCount() {
        return applNoCount;
    }

    /**
     * @param applicationNoCount the applicationNoCount to set
     */
    public void setApplNoCount(int applNoCount) {
        this.applNoCount = applNoCount;
    }

    /**
     * @return the reverifyActionType
     */
    public String getReverifyActionType() {
        return reverifyActionType;
    }

    /**
     * @param reverifyActionType the reverifyActionType to set
     */
    public void setReverifyActionType(String reverifyActionType) {
        this.reverifyActionType = reverifyActionType;
    }

    /**
     * @return the treasuryRefNo
     */
    public String getTreasuryRefNo() {
        return treasuryRefNo;
    }

    /**
     * @param treasuryRefNo the treasuryRefNo to set
     */
    public void setTreasuryRefNo(String treasuryRefNo) {
        this.treasuryRefNo = treasuryRefNo;
    }

    /**
     * @return the paymentDate
     */
    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    /**
     * @param paymentDate the paymentDate to set
     */
    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * @return the taxFormPanelBeanList
     */
    public List<TaxFormPanelBean> getTaxFormPanelBeanList() {
        return taxFormPanelBeanList;
    }

    /**
     * @param taxFormPanelBeanList the taxFormPanelBeanList to set
     */
    public void setTaxFormPanelBeanList(List<TaxFormPanelBean> taxFormPanelBeanList) {
        this.taxFormPanelBeanList = taxFormPanelBeanList;
    }

    /**
     * @return the common_fee_chargeList
     */
    public List<EpayDobj> getCommon_fee_chargeList() {
        return common_fee_chargeList;
    }

    /**
     * @param common_fee_chargeList the common_fee_chargeList to set
     */
    public void setCommon_fee_chargeList(List<EpayDobj> common_fee_chargeList) {
        this.common_fee_chargeList = common_fee_chargeList;
    }

    /**
     * @return the officeName
     */
    public String getOfficeName() {
        return officeName;
    }

    /**
     * @param officeName the officeName to set
     */
    public void setOfficeName(String officeName) {
        this.officeName = officeName;
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
     * @return the vhClassDesc
     */
    public String getVhClassDesc() {
        return vhClassDesc;
    }

    /**
     * @param vhClassDesc the vhClassDesc to set
     */
    public void setVhClassDesc(String vhClassDesc) {
        this.vhClassDesc = vhClassDesc;
    }

    /**
     * @return the stateLogo
     */
    public String getStateLogo() {
        return stateLogo;
    }

    /**
     * @param stateLogo the stateLogo to set
     */
    public void setStateLogo(String stateLogo) {
        this.stateLogo = stateLogo;
    }

    /**
     * @return the imageBackground
     */
    public String getImageBackground() {
        return imageBackground;
    }

    /**
     * @param imageBackground the imageBackground to set
     */
    public void setImageBackground(String imageBackground) {
        this.imageBackground = imageBackground;
    }

    /**
     * @return the feeBreakUpDetails
     */
    public List<OnlinePayDobj> getFeeBreakUpDetails() {
        return feeBreakUpDetails;
    }

    /**
     * @param feeBreakUpDetails the feeBreakUpDetails to set
     */
    public void setFeeBreakUpDetails(List<OnlinePayDobj> feeBreakUpDetails) {
        this.feeBreakUpDetails = feeBreakUpDetails;
    }

    /**
     * @return the feeBreakUpAmount
     */
    public long getFeeBreakUpAmount() {
        return feeBreakUpAmount;
    }

    /**
     * @param feeBreakUpAmount the feeBreakUpAmount to set
     */
    public void setFeeBreakUpAmount(long feeBreakUpAmount) {
        this.feeBreakUpAmount = feeBreakUpAmount;
    }

    /**
     * @return the feeBreakUpFine
     */
    public long getFeeBreakUpFine() {
        return feeBreakUpFine;
    }

    /**
     * @param feeBreakUpFine the feeBreakUpFine to set
     */
    public void setFeeBreakUpFine(long feeBreakUpFine) {
        this.feeBreakUpFine = feeBreakUpFine;
    }

    /**
     * @return the feeBreakUpRebate
     */
    public long getFeeBreakUpRebate() {
        return feeBreakUpRebate;
    }

    /**
     * @param feeBreakUpRebate the feeBreakUpRebate to set
     */
    public void setFeeBreakUpRebate(long feeBreakUpRebate) {
        this.feeBreakUpRebate = feeBreakUpRebate;
    }

    /**
     * @return the feeBreakUpSurcharge
     */
    public long getFeeBreakUpSurcharge() {
        return feeBreakUpSurcharge;
    }

    /**
     * @param feeBreakUpSurcharge the feeBreakUpSurcharge to set
     */
    public void setFeeBreakUpSurcharge(long feeBreakUpSurcharge) {
        this.feeBreakUpSurcharge = feeBreakUpSurcharge;
    }

    /**
     * @return the feeBreakUpInterest
     */
    public long getFeeBreakUpInterest() {
        return feeBreakUpInterest;
    }

    /**
     * @param feeBreakUpInterest the feeBreakUpInterest to set
     */
    public void setFeeBreakUpInterest(long feeBreakUpInterest) {
        this.feeBreakUpInterest = feeBreakUpInterest;
    }
}
