/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.tradecert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author tranC081
 */
public class PublicApplicationTradeCertDobj implements Serializable, Cloneable {

    private String purCd;
    private long empCd;
    private Integer offCd;
    private String stateCd;
    private String empName;
    private String desigName;
    private String stateName;
    private String officeName;
    private String applNo;
    private String vehClassFor;
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
    private String dealerAddress;
    private String applicantName;
    private String applicantRelation;
    private String applicantAddress1;
    private String applicantAddress2;
    private String applicantCategory;
    private String noOfTradeCertificateReqFromApplicant;
    private String vehClassAppliedFromApplicant;
    private String earlierTradeCertificateNumber;
    private String applicantState;
    private String applicantOffice;
    private String applicantDistrict;
    private String applicantMobileNumber;
    private String applicantPincode;
    private String applicantMailId;
    private Date applicantValidUpto;
    private String individualOrCompany;
    private List selectedApplicantManufacturerList;
    private List selectedApplicantManufacturerListForEx;
    private ArrayList vehClassAffiliatedByApplicantList;
    private ArrayList vehClassAffiliatedByApplicantListForEx;
    private String applicantCode;
    private String applicantRegnNo = "";
    private String applicantTinNo;
    private String validUptoDateString;
    private String landLineNumber;
    private String showRoomName;
    private String showRoomAddress1;
    private String showRoomAddress2;
    private String showRoomDistrict;
    private String showRoomPincode;
    private String LOIAuthorisationNo;
    private Date LOIAuthorisationDate;
    private String LOIAuthorisationDateString;
    private String status;
    private String applicantBranchName;
    private Date validFrom;
    private Date issueDate;
    private String noOfVehiclesUsed;
    private final Map<String, String> selectedManufacturerMapFromSession;
    private final Map<String, String> selectedVehClassMapFromSession;
    private String extraVehiclesSoldLastYr;
    private String noOfCertificateGotLastYr;

    public PublicApplicationTradeCertDobj() {
        selectedApplicantManufacturerList = new ArrayList();
        selectedApplicantManufacturerListForEx = new ArrayList();
        vehClassAffiliatedByApplicantList = new ArrayList();
        vehClassAffiliatedByApplicantListForEx = new ArrayList();
        selectedManufacturerMapFromSession = new HashMap<>();
        selectedVehClassMapFromSession = new HashMap<>();
        extraVehiclesSoldLastYr = "0";
        noOfCertificateGotLastYr = "0";
    }

    public void reset() {
        applNo = "";
        vehClassFor = "";
        dealerFor = "";
        vehCatgFor = "";
        vehCatgName = "";
        noOfAllowedVehicles = "";
        srNo = "";
        tradeCertNo = "";
        validUpto = null;
        expired = false;
        dealerAddress = "";
        applicantName = "";
        applicantRelation = "";
        applicantAddress1 = "";
        applicantAddress2 = "";
        applicantCategory = "";
        noOfTradeCertificateReqFromApplicant = "";
        vehClassAppliedFromApplicant = "";
        earlierTradeCertificateNumber = "";
        applicantState = "";
        applicantOffice = "";
        applicantDistrict = "";
        applicantMobileNumber = "";
        applicantPincode = "";
        applicantMailId = "";
        applicantValidUpto = null;
        individualOrCompany = "Company";
        selectedApplicantManufacturerList.clear();
        vehClassAffiliatedByApplicantList.clear();
        selectedApplicantManufacturerListForEx.clear();
        vehClassAffiliatedByApplicantListForEx.clear();
        applicantCode = "";
        landLineNumber = "";
        showRoomName = "";
        showRoomAddress1 = "";
        showRoomAddress2 = "";
        showRoomDistrict = "";
        showRoomPincode = "";
        LOIAuthorisationNo = "";
        LOIAuthorisationDate = null;
        status = "";
        noOfVehiclesUsed = "";
        validFrom = null;
        issueDate = null;
        LOIAuthorisationDateString = "";
        validUptoDateString = "";
        selectedManufacturerMapFromSession.clear();
        selectedVehClassMapFromSession.clear();
        noOfCertificateGotLastYr = "0";
        extraVehiclesSoldLastYr = "0";
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

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantRelation() {
        return applicantRelation;
    }

    public void setApplicantRelation(String applicantRelation) {
        this.applicantRelation = applicantRelation;
    }

    public String getApplicantAddress1() {
        return applicantAddress1;
    }

    public void setApplicantAddress1(String applicantAddress1) {
        this.applicantAddress1 = applicantAddress1;
    }

    public String getApplicantAddress2() {
        return applicantAddress2;
    }

    public void setApplicantAddress2(String applicantAddress2) {
        if (applicantAddress2 != null && applicantAddress2.trim().length() < 35) {
            this.applicantAddress2 = " " + applicantAddress2;
        } else {
            this.applicantAddress2 = applicantAddress2;
        }
    }

    public String getApplicantCategory() {
        return applicantCategory;
    }

    public void setApplicantCategory(String applicantCategory) {
        this.applicantCategory = applicantCategory;
    }

    public String getNoOfTradeCertificateReqFromApplicant() {
        return noOfTradeCertificateReqFromApplicant;
    }

    public void setNoOfTradeCertificateReqFromApplicant(String noOfTradeCertificateReqFromApplicant) {
        this.noOfTradeCertificateReqFromApplicant = noOfTradeCertificateReqFromApplicant;
    }

    public String getVehClassAppliedFromApplicant() {
        return vehClassAppliedFromApplicant;
    }

    public void setVehClassAppliedFromApplicant(String vehClassAppliedFromApplicant) {
        this.vehClassAppliedFromApplicant = vehClassAppliedFromApplicant;
    }

    public String getEarlierTradeCertificateNumber() {
        return earlierTradeCertificateNumber;
    }

    public void setEarlierTradeCertificateNumber(String earlierTradeCertificateNumber) {
        this.earlierTradeCertificateNumber = earlierTradeCertificateNumber;
    }

    public String getApplicantState() {
        return applicantState;
    }

    public void setApplicantState(String applicantState) {
        this.applicantState = applicantState;
    }

    public String getApplicantOffice() {
        return applicantOffice;
    }

    public void setApplicantOffice(String applicantOffice) {
        this.applicantOffice = applicantOffice;
    }

    public String getApplicantDistrict() {
        return applicantDistrict;
    }

    public void setApplicantDistrict(String applicantDistrict) {
        this.applicantDistrict = applicantDistrict;
    }

    public String getApplicantPincode() {
        return applicantPincode;
    }

    public void setApplicantPincode(String applicantPincode) {
        this.applicantPincode = applicantPincode;
    }

    public Date getApplicantValidUpto() {
        return applicantValidUpto;
    }

    public void setApplicantValidUpto(Date applicantValidUpto) {
        this.applicantValidUpto = applicantValidUpto;
    }

    public String getIndividualOrCompany() {
        return individualOrCompany;
    }

    public void setIndividualOrCompany(String individualOrCompany) {
        this.individualOrCompany = individualOrCompany;
    }

    public String getApplicantMobileNumber() {
        return applicantMobileNumber;
    }

    public void setApplicantMobileNumber(String applicantMobileNumber) {
        this.applicantMobileNumber = applicantMobileNumber;
    }

    public String getApplicantMailId() {
        return applicantMailId;
    }

    public void setApplicantMailId(String applicantMailId) {
        this.applicantMailId = applicantMailId;
    }

    public List getSelectedApplicantManufacturerList() {
        return selectedApplicantManufacturerList;
    }

    public void setSelectedApplicantManufacturerList(List selectedApplicantManufacturerList) {
        this.selectedApplicantManufacturerList = selectedApplicantManufacturerList;
    }

    public ArrayList getVehClassAffiliatedByApplicantList() {
        return vehClassAffiliatedByApplicantList;
    }

    public void setVehClassAffiliatedByApplicantList(ArrayList vehClassAffiliatedByApplicantList) {
        this.vehClassAffiliatedByApplicantList = vehClassAffiliatedByApplicantList;
    }

    public String getApplicantRegnNo() {
        return applicantRegnNo;
    }

    public String getApplicantTinNo() {
        return applicantTinNo;
    }

    public void setApplicantRegnNo(String applicantRegnNo) {
        this.applicantRegnNo = applicantRegnNo;
    }

    public void setApplicantTinNo(String applicantTinNo) {
        this.applicantTinNo = applicantTinNo;
    }

    @Override
    public PublicApplicationTradeCertDobj clone() throws CloneNotSupportedException {
        final PublicApplicationTradeCertDobj clone;
        try {
            clone = (PublicApplicationTradeCertDobj) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw ex;
        }
        clone.purCd = this.purCd;
        clone.empCd = this.empCd;
        clone.offCd = this.offCd;
        clone.stateCd = this.stateCd;
        clone.empName = this.empName;
        clone.desigName = this.desigName;
        clone.stateName = this.stateName;
        clone.officeName = this.officeName;
        clone.applNo = this.applNo;
        clone.vehClassFor = this.vehClassFor;
        clone.dealerFor = this.dealerFor;
        clone.dealerName = this.dealerName;
        clone.vehCatgFor = this.vehCatgFor;
        clone.vehCatgName = this.vehCatgName;
        clone.noOfAllowedVehicles = this.noOfAllowedVehicles;
        clone.srNo = this.srNo;
        clone.newRenewalTradeCert = this.newRenewalTradeCert;
        clone.tradeCertNo = this.tradeCertNo;
        clone.validUpto = this.validUpto;
        clone.expired = this.expired;
        /////////////// for printing application
        clone.dealerAddress = this.dealerAddress;
        clone.applicantName = this.applicantName;
        clone.applicantRelation = this.applicantRelation;
        clone.applicantAddress1 = this.applicantAddress1;
        clone.applicantAddress2 = this.applicantAddress2;
        clone.applicantCategory = this.applicantCategory;
        clone.noOfTradeCertificateReqFromApplicant = this.noOfTradeCertificateReqFromApplicant;
        clone.vehClassAppliedFromApplicant = this.vehClassAppliedFromApplicant;
        clone.earlierTradeCertificateNumber = this.earlierTradeCertificateNumber;
        clone.applicantState = this.applicantState;
        clone.applicantOffice = this.applicantOffice;
        clone.applicantDistrict = this.applicantDistrict;
        clone.applicantMobileNumber = this.applicantMobileNumber;
        clone.applicantPincode = this.applicantPincode;
        clone.applicantMailId = this.applicantMailId;
        clone.applicantValidUpto = this.applicantValidUpto;
        clone.individualOrCompany = this.individualOrCompany;
        clone.selectedApplicantManufacturerList = this.selectedApplicantManufacturerList;
        clone.vehClassAffiliatedByApplicantList = this.vehClassAffiliatedByApplicantList;
        clone.applicantCode = this.applicantCode;
        clone.applicantRegnNo = this.applicantRegnNo;
        clone.applicantTinNo = this.applicantTinNo;
        clone.landLineNumber = this.landLineNumber;
        clone.showRoomName = this.showRoomName;
        clone.showRoomAddress1 = this.showRoomAddress1;
        clone.showRoomAddress2 = this.showRoomAddress2;
        clone.showRoomDistrict = this.showRoomDistrict;
        clone.showRoomPincode = this.showRoomPincode;
        clone.LOIAuthorisationNo = this.LOIAuthorisationNo;
        clone.LOIAuthorisationDate = this.LOIAuthorisationDate;
        clone.status = this.status;
        clone.noOfVehiclesUsed = this.noOfVehiclesUsed;
        clone.validFrom = this.validFrom;
        clone.issueDate = this.issueDate;
        clone.LOIAuthorisationDateString = this.LOIAuthorisationDateString;
        clone.extraVehiclesSoldLastYr = this.extraVehiclesSoldLastYr;
        clone.noOfCertificateGotLastYr = this.noOfCertificateGotLastYr;
        return clone;
    }

    public String getValidUptoDateString() {
        return validUptoDateString;
    }

    public void setValidUptoDateString(String validUptoDateString) {
        this.validUptoDateString = validUptoDateString;
    }

    public String getApplicantCode() {
        return applicantCode;
    }

    public void setApplicantCode(String applicantCode) {
        this.applicantCode = applicantCode;
    }

    public List getSelectedApplicantManufacturerListForEx() {
        return selectedApplicantManufacturerListForEx;
    }

    public void setSelectedApplicantManufacturerListForEx(List selectedApplicantManufacturerListForEx) {
        this.selectedApplicantManufacturerListForEx = selectedApplicantManufacturerListForEx;
    }

    public ArrayList getVehClassAffiliatedByApplicantListForEx() {
        return vehClassAffiliatedByApplicantListForEx;
    }

    public void setVehClassAffiliatedByApplicantListForEx(ArrayList vehClassAffiliatedByApplicantListForEx) {
        this.vehClassAffiliatedByApplicantListForEx = vehClassAffiliatedByApplicantListForEx;
    }

    public String getLandLineNumber() {
        return landLineNumber;
    }

    public void setLandLineNumber(String landLineNumber) {
        this.landLineNumber = landLineNumber;
    }

    public String getShowRoomName() {
        return showRoomName;
    }

    public void setShowRoomName(String showRoomName) {
        this.showRoomName = showRoomName;
    }

    public String getShowRoomAddress1() {
        return showRoomAddress1;
    }

    public void setShowRoomAddress1(String showRoomAddress1) {
        this.showRoomAddress1 = showRoomAddress1;
    }

    public String getShowRoomAddress2() {
        return showRoomAddress2;
    }

    public void setShowRoomAddress2(String showRoomAddress2) {
        this.showRoomAddress2 = showRoomAddress2;
    }

    public String getShowRoomDistrict() {
        return showRoomDistrict;
    }

    public void setShowRoomDistrict(String showRoomDistrict) {
        this.showRoomDistrict = showRoomDistrict;
    }

    public String getShowRoomPincode() {
        return showRoomPincode;
    }

    public void setShowRoomPincode(String showRoomPincode) {
        this.showRoomPincode = showRoomPincode;
    }

    public String getLOIAuthorisationNo() {
        return LOIAuthorisationNo;
    }

    public void setLOIAuthorisationNo(String LOIAuthorisationNo) {
        this.LOIAuthorisationNo = LOIAuthorisationNo;
    }

    public Date getLOIAuthorisationDate() {
        return LOIAuthorisationDate;
    }

    public void setLOIAuthorisationDate(Date LOIAuthorisationDate) {
        this.LOIAuthorisationDate = LOIAuthorisationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplicantBranchName() {
        return applicantBranchName;
    }

    public void setApplicantBranchName(String applicantBranchName) {
        this.applicantBranchName = applicantBranchName;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getNoOfVehiclesUsed() {
        return noOfVehiclesUsed;
    }

    public void setNoOfVehiclesUsed(String noOfVehiclesUsed) {
        this.noOfVehiclesUsed = noOfVehiclesUsed;
    }

    public String getLOIAuthorisationDateString() {
        return LOIAuthorisationDateString;
    }

    public void setLOIAuthorisationDateString(String LOIAuthorisationDateString) {
        this.LOIAuthorisationDateString = LOIAuthorisationDateString;
    }

    public Map<String, String> getSelectedManufacturerMapFromSession() {
        return selectedManufacturerMapFromSession;
    }

    public Map<String, String> getSelectedVehClassMapFromSession() {
        return selectedVehClassMapFromSession;
    }

    public String getExtraVehiclesSoldLastYr() {
        return extraVehiclesSoldLastYr;
    }

    public void setExtraVehiclesSoldLastYr(String extraVehiclesSoldLastYr) {
        this.extraVehiclesSoldLastYr = extraVehiclesSoldLastYr;
    }

    public String getNoOfCertificateGotLastYr() {
        return noOfCertificateGotLastYr;
    }

    public void setNoOfCertificateGotLastYr(String noOfCertificateGotLastYr) {
        this.noOfCertificateGotLastYr = noOfCertificateGotLastYr;
    }
}
