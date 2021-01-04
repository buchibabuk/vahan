/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.tradecert;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author tranC081
 */
public class IssueTradeCertDobj implements Cloneable, Serializable {

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
    private String noOfVehiclesUsed;
    private String newRenewalTradeCert;
    private String tradeCertNo;
    private Date validUpto;
    private Date validDt;
    private Date issueDt;
    private String fee;
    private String dealerAddress;
    private String officeName;
    private String issueDateAsString;
    private String validityPeriod;
    private List<IssueTradeCertDobj> tradeCerSubList;
    private String issueDtAsString;
    private String validDtAsString;
    private String validUptoAsString;
    private String text;
    private String fuelTypeFor;
    private String fuelTypeName;
    private String status;
    private String noOfVehiclePrint;
    private boolean flagNotToPrintNoOfVehicleRange;
    private String applicantCategory;
    private String receiptNumber;
    private boolean rtoSideAppl;
    private boolean stockTransferReq;
    private boolean serialNoWithTC;
    private List<String> selectedDuplicateCertificates;
    private String remark;
    private String mergeApplNo;
    private String newCertNo;

    public IssueTradeCertDobj() {
        newRenewalTradeCert = "New_Trade_Certificate";
        validDt = new Date();

        issueTradeCertDobjBlock1();

        issueDt = new Date();
    }

    private void issueTradeCertDobjBlock1() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1);
        cal.add(Calendar.YEAR, 1);
        validUpto = cal.getTime();

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
        noOfVehiclesUsed = "";
        srNo = "";
        tradeCertNo = "";
        validUpto = new Date();
        validDt = new Date();
        issueDt = new Date();
        issueDateAsString = "";
        validDtAsString = "";
        validUptoAsString = "";
        text = "";
        fuelTypeFor = "";
        fuelTypeName = "";
        status = "";
        flagNotToPrintNoOfVehicleRange = false;
        applicantCategory = "";
        receiptNumber = "";
        stockTransferReq = false;
        serialNoWithTC = false;
        setSelectedDuplicateCertificates(null);
        setRemark("");
        setMergeApplNo("");
        setNewCertNo("");
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

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
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

    public Date getValidDt() {
        return validDt;
    }

    public void setValidDt(Date validDt) {
        this.validDt = validDt;
    }

    public Date getIssueDt() {
        return issueDt;
    }

    public void setIssueDt(Date issueDt) {
        this.issueDt = issueDt;
    }

    public String getNewRenewalTradeCert() {
        return newRenewalTradeCert;
    }

    public void setNewRenewalTradeCert(String newRenewalTradeCert) {
        this.newRenewalTradeCert = newRenewalTradeCert;
    }

    public String getVehCatgName() {
        return vehCatgName;
    }

    public void setVehCatgName(String vehCatgName) {
        this.vehCatgName = vehCatgName;
    }

    public String getNoOfVehiclesUsed() {
        return noOfVehiclesUsed;
    }

    public void setNoOfVehiclesUsed(String noOfVehiclesUsed) {
        this.noOfVehiclesUsed = noOfVehiclesUsed;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getDealerAddress() {
        return dealerAddress;
    }

    public void setDealerAddress(String dealerAddress) {
        this.dealerAddress = dealerAddress;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getIssueDateAsString() {
        return issueDateAsString;
    }

    public void setIssueDateAsString(String issueDateAsString) {
        this.issueDateAsString = issueDateAsString;
    }

    public String getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(String validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public List<IssueTradeCertDobj> getTradeCerSubList() {
        return tradeCerSubList;
    }

    public void setTradeCerSubList(List<IssueTradeCertDobj> tradeCerSubList) {
        this.tradeCerSubList = tradeCerSubList;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getIssueDtAsString() {
        return issueDtAsString;
    }

    public void setIssueDtAsString(String issueDtAsString) {
        this.issueDtAsString = issueDtAsString;
    }

    public String getValidDtAsString() {
        return validDtAsString;
    }

    public void setValidDtAsString(String validDtAsString) {
        this.validDtAsString = validDtAsString;
    }

    public String getValidUptoAsString() {
        return validUptoAsString;
    }

    public void setValidUptoAsString(String validUptoAsString) {
        this.validUptoAsString = validUptoAsString;
    }

    public long getEmpCd() {
        return empCd;
    }

    public void setEmpCd(long empCd) {
        this.empCd = empCd;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFuelTypeName() {
        return fuelTypeName;
    }

    public void setFuelTypeName(String fuelTypeName) {
        this.fuelTypeName = fuelTypeName;
    }

    public String getFuelTypeFor() {
        return fuelTypeFor;
    }

    public void setFuelTypeFor(String fuelTypeFor) {
        this.fuelTypeFor = fuelTypeFor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNoOfVehiclePrint() {
        return noOfVehiclePrint;
    }

    public void setNoOfVehiclePrint(String noOfVehiclePrint) {
        this.noOfVehiclePrint = noOfVehiclePrint;
    }

    public boolean isFlagNotToPrintNoOfVehicleRange() {
        return flagNotToPrintNoOfVehicleRange;
    }

    public void setFlagNotToPrintNoOfVehicleRange(boolean flagNotToPrintNoOfVehicleRange) {
        this.flagNotToPrintNoOfVehicleRange = flagNotToPrintNoOfVehicleRange;
    }

    public String getApplicantCategory() {
        return applicantCategory;
    }

    public void setApplicantCategory(String applicantCategory) {
        this.applicantCategory = applicantCategory;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public boolean isRtoSideAppl() {
        return rtoSideAppl;
    }

    public void setRtoSideAppl(boolean rtoSideAppl) {
        this.rtoSideAppl = rtoSideAppl;
    }

    public boolean isStockTransferReq() {
        return stockTransferReq;
    }

    public void setStockTransferReq(boolean stockTransferReq) {
        this.stockTransferReq = stockTransferReq;
    }

    public boolean isSerialNoWithTC() {
        return serialNoWithTC;
    }

    public void setSerialNoWithTC(boolean serialNoWithTC) {
        this.serialNoWithTC = serialNoWithTC;
    }

    public List<String> getSelectedDuplicateCertificates() {
        return selectedDuplicateCertificates;
    }

    public void setSelectedDuplicateCertificates(List<String> selectedDuplicateCertificates) {
        this.selectedDuplicateCertificates = selectedDuplicateCertificates;
    }

    /**
     * @return the mergeApplNo
     */
    public String getMergeApplNo() {
        return mergeApplNo;
    }

    /**
     * @param mergeApplNo the mergeApplNo to set
     */
    public void setMergeApplNo(String mergeApplNo) {
        this.mergeApplNo = mergeApplNo;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
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
}
