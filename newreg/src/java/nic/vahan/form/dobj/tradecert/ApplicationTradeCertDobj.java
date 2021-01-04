/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.tradecert;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author tranC081
 */
public class ApplicationTradeCertDobj implements Serializable {

    private String purCd;
    private long empCd;
    private Integer offCd;
    private String stateCd;
    private String empName;
    private String desigName;
    private String stateName;
    private String officeName;
    private String applNo;
    private String dealerFor;
    private String dealerName;
    private String vehCatgFor;
    private String vehCatgName;
    private String noOfAllowedVehicles;
    private String srNo;
    private String newRenewalTradeCert;
    private String tradeCertNo;
    private Date validUpto;
    private boolean expired;
    /////////////// for printing application
    private String feesCollected;
    private String dealerAddress;
    private String fuelTypeFor;
    private String fuelTypeName;
    private String validUptoAsString;
    private boolean toBeRenew;
    private String status;
    private String applicantRelation;
    private String applicantCategory;
    private String vehClass;
    private boolean rtoSideAppl;
    private boolean stockTransferReq;
    private List<String> selectedDuplicateCertificates;

    public void reset() {
        applNo = "";
        dealerFor = "";
        vehCatgFor = "";
        vehCatgName = "";
        noOfAllowedVehicles = "";
        srNo = "";
        tradeCertNo = "";
        validUpto = null;
        expired = false;
        feesCollected = "";
        dealerAddress = "";
        fuelTypeFor = "";
        fuelTypeName = "";
        validUptoAsString = "";
        toBeRenew = false;
        status = "";
        applicantRelation = "";
        applicantCategory = "";
        vehClass = "";
        stockTransferReq = false;
        selectedDuplicateCertificates = null;
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

    public String getVehCatgName() {
        return vehCatgName;
    }

    public void setVehCatgName(String vehCatgName) {
        this.vehCatgName = vehCatgName;
    }

    public String getNewRenewalTradeCert() {
        return newRenewalTradeCert;
    }

    public void setNewRenewalTradeCert(String newRenewalTradeCert) {
        this.newRenewalTradeCert = newRenewalTradeCert;
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

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getFeesCollected() {
        return feesCollected;
    }

    public void setFeesCollected(String feesCollected) {
        this.feesCollected = feesCollected;
    }

    public String getDealerAddress() {
        return dealerAddress;
    }

    public void setDealerAddress(String dealerAddress) {
        this.dealerAddress = dealerAddress;
    }

    public long getEmpCd() {
        return empCd;
    }

    public void setEmpCd(long empCd) {
        this.empCd = empCd;
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

    public String getValidUptoAsString() {
        return validUptoAsString;
    }

    public void setValidUptoAsString(String validUptoAsString) {
        this.validUptoAsString = validUptoAsString;
    }

    public boolean isToBeRenew() {
        return toBeRenew;
    }

    public void setToBeRenew(boolean toBeRenew) {
        this.toBeRenew = toBeRenew;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplicantRelation() {
        return applicantRelation;
    }

    public void setApplicantRelation(String applicantRelation) {
        this.applicantRelation = applicantRelation;
    }

    public String getApplicantCategory() {
        return applicantCategory;
    }

    public void setApplicantCategory(String applicantCategory) {
        this.applicantCategory = applicantCategory;
    }

    public String getVehClass() {
        return vehClass;
    }

    public void setVehClass(String vehClass) {
        this.vehClass = vehClass;
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

    public List<String> getSelectedDuplicateCertificates() {
        return selectedDuplicateCertificates;
    }

    public void setSelectedDuplicateCertificates(List<String> selectedDuplicateCertificates) {
        this.selectedDuplicateCertificates = selectedDuplicateCertificates;
    }
}
