/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.tradecert;

import java.util.Date;
import java.util.List;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.impl.FeeImpl;

/**
 *
 * @author tranC081
 */
public class ApplicationFeeTradeCertDobj {

    private String purCd;
    private long empCd;
    private Integer offCd;
    private String stateCd;
    private String empName;
    private String desigName;
    private String stateName;
    private String applNo;
    private String vehTypeFor;
    private String vehClassFor;
    private String dealerFor;
    private String dealerName;
    private String vehCatgFor;
    private String vehCatgName;
    private String noOfAllowedVehicles;
    private String srNo;
    private String newRenewalTradeCert;
    private String sectionSrNoToViewFeeSlab;
    private String amount;
    private String paymentMode;
    private String description1;
    private String description2;
    private int fee1; //// FEE
    private Double fee2;  ///// TAX
    private int fine1; ///// FINE
    private int fine2;
    private int total1;
    private int total2;
    private String grandTotal;
    private String number;
    private String bankName;
    private String branchName;
    private String draftChequeChallanAmount;
    private Date draftChequeChallanDate;
    private String flag;
    private String receiptNumber;
    private String receiptDateAsString;
    private String feeCollected;
    private String taxCollected;
    private String fineCollected;
    private Long cashAmtForApplRcptMapping;
    private FeeDraftDobj feeDraftDobj;
    private String officeName;
    private Date receiptDate;
    private String purpose;
    private List<ApplicationTradeCertDobj> tradeCerFeeRecieptSubList;
    private Double surcharge;
    private Double serviceCharge;
    private boolean serviceChargeToBeRendered;
    private String tradeCertNo;
    private Date validUpto;
    private FeeImpl.PaymentGenInfo paymentGenInfo;
    private String fuelTypeFor;
    private String fuelTypeName;
    private Double fuelTax;
    private Double fuelTaxCollected;
    private String validUptoAsString;
    private Double miscFee;
    private String status;
    private String extraVehiclesSoldLastYr;
    private String applicantCategory;
    private Double transactionFee;
    private boolean stockTransferApplicable;
    private int manualFeeReceiptAmount;
    private String manualFeeReceiptRemark;
    private String manualFeeReceiptNumber;
    private String manualFeeReceiptDate;

    public ApplicationFeeTradeCertDobj() {
        newRenewalTradeCert = "New_Trade_Certificate_Fee";
        extraVehiclesSoldLastYr = "0";
        applicantCategory = TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER;
    }

    public void reset() {
        applNo = "";
        vehTypeFor = "";
        vehClassFor = "";
        dealerFor = "";
        dealerName = "";
        vehCatgFor = "";
        vehCatgName = "";
        noOfAllowedVehicles = "";
        srNo = "";
        amount = "";
        paymentMode = "";
        description1 = "";
        description2 = "";
        fee1 = 0;
        fee2 = 0.0;
        fine1 = 0;
        fine2 = 0;
        total1 = 0;
        total2 = 0;

        grandTotal = "";
        number = "";
        bankName = "";
        branchName = "";
        draftChequeChallanAmount = "";
        draftChequeChallanDate = new Date();
        flag = "";
        receiptNumber = "";
        taxCollected = "0";
        feeCollected = "0";
        fineCollected = "0";
        cashAmtForApplRcptMapping = 0L;
        feeDraftDobj = null;
        officeName = "";
        receiptDate = null;

        serviceCharge = 0.0;
        serviceChargeToBeRendered = false;
        tradeCertNo = "";
        validUpto = null;
        paymentGenInfo = null;
        fuelTypeFor = "";
        fuelTypeName = "";
        fuelTax = 0.0;
        fuelTaxCollected = 0.0;
        validUptoAsString = "";
        miscFee = 0.0;
        status = "";
        extraVehiclesSoldLastYr = "0";
        applicantCategory = TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER;
        transactionFee = 0.0;
        stockTransferApplicable = false;
        manualFeeReceiptAmount = 0;
    }

    public String getPurCd() {
        return purCd;
    }

    public void setPurCd(String purCd) {
        this.purCd = purCd;
    }

    public Integer getOffCd() {
        return offCd;
    }

    public void setOffCd(Integer offCd) {
        this.offCd = offCd;
    }

    public String getStateCd() {
        return stateCd;
    }

    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getDesigName() {
        return desigName;
    }

    public void setDesigName(String desigName) {
        this.desigName = desigName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getVehTypeFor() {
        return vehTypeFor;
    }

    public void setVehTypeFor(String vehTypeFor) {
        this.vehTypeFor = vehTypeFor;
    }

    public String getVehClassFor() {
        return vehClassFor;
    }

    public void setVehClassFor(String vehClassFor) {
        this.vehClassFor = vehClassFor;
    }

    public String getDealerFor() {
        return dealerFor;
    }

    public void setDealerFor(String dealerFor) {
        this.dealerFor = dealerFor;
    }

    public String getVehCatgFor() {
        return vehCatgFor;
    }

    public void setVehCatgFor(String vehCatgFor) {
        this.vehCatgFor = vehCatgFor;
    }

    public String getNoOfAllowedVehicles() {
        return noOfAllowedVehicles;
    }

    public void setNoOfAllowedVehicles(String noOfAllowedVehicles) {
        this.noOfAllowedVehicles = noOfAllowedVehicles;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public String getSrNo() {
        return srNo;
    }

    public void setSrNo(String srNo) {
        this.srNo = srNo;
    }

    public String getNewRenewalTradeCert() {
        return newRenewalTradeCert;
    }

    public void setNewRenewalTradeCert(String newRenewalTradeCert) {
        this.newRenewalTradeCert = newRenewalTradeCert;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getDescription1() {
        return description1;
    }

    public void setDescription1(String description1) {
        this.description1 = description1;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public int getFee1() {
        return fee1;
    }

    public void setFee1(int fee1) {
        this.fee1 = fee1;
    }

    public int getFine1() {
        return fine1;
    }

    public void setFine1(int fine1) {
        this.fine1 = fine1;
    }

    public int getFine2() {
        return fine2;
    }

    public void setFine2(int fine2) {
        this.fine2 = fine2;
    }

    public int getTotal1() {
        return total1;
    }

    public void setTotal1(int total1) {
        this.total1 = total1;
    }

    public int getTotal2() {
        return total2;
    }

    public void setTotal2(int total2) {
        this.total2 = total2;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getDraftChequeChallanAmount() {
        return draftChequeChallanAmount;
    }

    public void setDraftChequeChallanAmount(String draftChequeChallanAmount) {
        this.draftChequeChallanAmount = draftChequeChallanAmount;
    }

    public Date getDraftChequeChallanDate() {
        return draftChequeChallanDate;
    }

    public void setDraftChequeChallanDate(Date draftChequeChallanDate) {
        this.draftChequeChallanDate = draftChequeChallanDate;
    }

    public String getFeeCollected() {
        return feeCollected;
    }

    public void setFeeCollected(String feeCollected) {
        this.feeCollected = feeCollected;
    }

    public String getFineCollected() {
        return fineCollected;
    }

    public void setFineCollected(String fineCollected) {
        this.fineCollected = fineCollected;
    }

    public String getSectionSrNoToViewFeeSlab() {
        return sectionSrNoToViewFeeSlab;
    }

    public void setSectionSrNoToViewFeeSlab(String sectionSrNoToViewFeeSlab) {
        this.sectionSrNoToViewFeeSlab = sectionSrNoToViewFeeSlab;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getVehCatgName() {
        return vehCatgName;
    }

    public void setVehCatgName(String vehCatgName) {
        this.vehCatgName = vehCatgName;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public FeeDraftDobj getFeeDraftDobj() {
        return feeDraftDobj;
    }

    public void setFeeDraftDobj(FeeDraftDobj feeDraftDobj) {
        this.feeDraftDobj = feeDraftDobj;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public List<ApplicationTradeCertDobj> getTradeCerFeeRecieptSubList() {
        return tradeCerFeeRecieptSubList;
    }

    public void setTradeCerFeeRecieptSubList(List<ApplicationTradeCertDobj> tradeCerFeeRecieptSubList) {
        this.tradeCerFeeRecieptSubList = tradeCerFeeRecieptSubList;
    }

    public String getReceiptDateAsString() {
        return receiptDateAsString;
    }

    public void setReceiptDateAsString(String receiptDateAsString) {
        this.receiptDateAsString = receiptDateAsString;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Double getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(Double surcharge) {
        this.surcharge = surcharge;
    }

    public Double getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(Double serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public boolean isServiceChargeToBeRendered() {
        return serviceChargeToBeRendered;
    }

    public void setServiceChargeToBeRendered(boolean serviceChargeToBeRendered) {
        this.serviceChargeToBeRendered = serviceChargeToBeRendered;
    }

    public long getEmpCd() {
        return empCd;
    }

    public void setEmpCd(long empCd) {
        this.empCd = empCd;
    }

    public String getTradeCertNo() {
        return tradeCertNo;
    }

    public void setTradeCertNo(String tradeCertNo) {
        this.tradeCertNo = tradeCertNo;
    }

    public Date getValidUpto() {
        return validUpto;
    }

    public void setValidUpto(Date validUpto) {
        this.validUpto = validUpto;
    }

    public String getTaxCollected() {
        return taxCollected;
    }

    public void setTaxCollected(String taxCollected) {
        this.taxCollected = taxCollected;
    }

    public Long getCashAmtForApplRcptMapping() {
        return cashAmtForApplRcptMapping;
    }

    public void setCashAmtForApplRcptMapping(Long cashAmtForApplRcptMapping) {
        this.cashAmtForApplRcptMapping = cashAmtForApplRcptMapping;
    }

    public FeeImpl.PaymentGenInfo getPaymentGenInfo() {
        return paymentGenInfo;
    }

    public void setPaymentGenInfo(FeeImpl.PaymentGenInfo paymentGenInfo) {
        this.paymentGenInfo = paymentGenInfo;
    }

    public String getFuelTypeFor() {
        return fuelTypeFor;
    }

    public void setFuelTypeFor(String fuelTypeFor) {
        this.fuelTypeFor = fuelTypeFor;
    }

    public String getFuelTypeName() {
        return fuelTypeName;
    }

    public void setFuelTypeName(String fuelTypeName) {
        this.fuelTypeName = fuelTypeName;
    }

    public Double getFuelTax() {
        return fuelTax;
    }

    public void setFuelTax(Double fuelTax) {
        this.fuelTax = fuelTax;
    }

    public Double getFuelTaxCollected() {
        return fuelTaxCollected;
    }

    public void setFuelTaxCollected(Double fuelTaxCollected) {
        this.fuelTaxCollected = fuelTaxCollected;
    }

    public String getValidUptoAsString() {
        return validUptoAsString;
    }

    public void setValidUptoAsString(String validUptoAsString) {
        this.validUptoAsString = validUptoAsString;
    }

    public Double getMiscFee() {
        return miscFee;
    }

    public void setMiscFee(Double miscFee) {
        this.miscFee = miscFee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getFee2() {
        return fee2;
    }

    public void setFee2(Double fee2) {
        this.fee2 = fee2;
    }

    public String getExtraVehiclesSoldLastYr() {
        return extraVehiclesSoldLastYr;
    }

    public void setExtraVehiclesSoldLastYr(String extraVehiclesSoldLastYr) {
        this.extraVehiclesSoldLastYr = extraVehiclesSoldLastYr;
    }

    public String getApplicantCategory() {
        return applicantCategory;
    }

    public void setApplicantCategory(String applicantCategory) {
        this.applicantCategory = applicantCategory;
    }

    public Double getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(Double transactionFee) {
        this.transactionFee = transactionFee;
    }

    public boolean isStockTransferApplicable() {
        return stockTransferApplicable;
    }

    public void setStockTransferApplicable(boolean stockTransferApplicable) {
        this.stockTransferApplicable = stockTransferApplicable;
    }

    public int getManualFeeReceiptAmount() {
        return manualFeeReceiptAmount;
    }

    public void setManualFeeReceiptAmount(int manualFeeReceiptAmount) {
        this.manualFeeReceiptAmount = manualFeeReceiptAmount;
    }

    public String getManualFeeReceiptRemark() {
        return manualFeeReceiptRemark;
    }

    public void setManualFeeReceiptRemark(int manualFeeReceiptAmount, String manualFeeReceiptNumber, String manualFeeReceiptDate) {
        this.manualFeeReceiptRemark = "RS. " + manualFeeReceiptAmount + " /- ADJUSTED AGAINST PREVIOUSLY PAID VIDE RECEIPT NUMBER " + manualFeeReceiptNumber + " Dated " + manualFeeReceiptDate;
    }

    public String getManualFeeReceiptNumber() {
        return manualFeeReceiptNumber;
    }

    public void setManualFeeReceiptNumber(String manualFeeReceiptNumber) {
        this.manualFeeReceiptNumber = manualFeeReceiptNumber;
    }

    public String getManualFeeReceiptDate() {
        return manualFeeReceiptDate;
    }

    public void setManualFeeReceiptDate(String manualFeeReceiptDate) {
        this.manualFeeReceiptDate = manualFeeReceiptDate;
    }
}
