/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.tradecert;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nic.vahan.db.user_mgmt.dobj.DealerMasterDobj;

/**
 *
 * @author tranC081
 */
public class VerifyApproveTradeCertDobj implements Serializable {

    private String purCd;
    private long empCd;
    private Integer offCd;
    private String stateCd;
    private String empName;
    private String desigName;
    private String stateName;
    private String officeName;
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
    private String tradeCertNo;
    private Date validUpto;
    private boolean expired;
    /////////////// for printing application
    private String feesCollected;
    private String dealerAddress;
    private String noOfVehiclesUsed;
    private Date validDt;
    private Date issueDt;
    private String fee;
    private String issueDateAsString;
    private String validityPeriod;
    private List<IssueTradeCertDobj> tradeCerSubList;
    private String issueDtAsString;
    private String validDtAsString;
    private String validUptoAsString;
    private DealerMasterDobj dealerMasterDobj;
    private String status;  //P: Pending, R: Received online, V: Verified , A: Approved , RX: Receiver Discard , VX: Receiver Discard , AX: Receiver Discard
    private String currentStatus;  //P: Pending, R: Received online, V: Verified , A: Approved , RX: Receiver Discard , VX: Receiver Discard , AX: Receiver Discard
    private int iterationCounter;
    private String receiverHardCopyReceivedOn;
    private String receiver;
    private String receiverForwardRemarks;
    private String receiverBackwardRemarks;
    private String receiverDeficiencyMailContent;
    private String verifierHardCopyReceivedOn;
    private String verifier;
    private String verifierForwardRemarks;
    private String verifierBackwardRemarks;
    private String verifierDeficiencyMailContent;
    private String approverHardCopyReceivedOn;
    private String approver;
    private String approverForwardRemarks;
    private String approverBackwardRemarks;
    private String approverDeficiencyMailContent;
    private String currentTimeStampAsMarkHardCopyReceivedInStringFormat;
    private String tempApplNo;
    private Date validUptoDate;
    private Date validFromDate;
    private Date issueDate;
    private final List vehClassAffiliatedByApplicantList;
    private final List vehClassAffiliatedByApplicantListForEx;
    private final List selectedApplicantManufacturerList;
    private final List selectedApplicantManufacturerListForEx;
    private String applicantType;
    private String branchName;
    private String landLineNo;
    private final Map<String, String> selectedManufacturerMapFromSession;
    private String extraVehiclesSoldLastYr;
    private String grandTotalInWords;
    private String originalTradeCertNo;
    private String tabIndex;
    private String applicantTypeDescription;
    private String inspectionBy;
    private Date inspectionOn;
    private String inspectionRemark;

    public VerifyApproveTradeCertDobj() {
        newRenewalTradeCert = "New_Trade_Certificate";

        validDt = new Date();

        issueTradeCertDobjBlock1();

        issueDt = new Date();
        vehClassAffiliatedByApplicantList = new ArrayList();
        vehClassAffiliatedByApplicantListForEx = new ArrayList();
        selectedApplicantManufacturerList = new ArrayList();
        selectedApplicantManufacturerListForEx = new ArrayList();
        selectedManufacturerMapFromSession = new HashMap<>();

    }

    private void issueTradeCertDobjBlock1() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1);
        cal.add(Calendar.YEAR, 1);
        validUpto = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        validUptoAsString = sdf.format(validDt);
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
        expired = false;
        feesCollected = "";
        dealerAddress = "";
        dealerMasterDobj = null;
        status = null;
        iterationCounter = 0;
        receiverHardCopyReceivedOn = null;
        receiver = null;
        receiverForwardRemarks = null;
        receiverBackwardRemarks = null;
        receiverDeficiencyMailContent = null;
        verifierHardCopyReceivedOn = null;
        verifier = null;
        verifierForwardRemarks = null;
        verifierBackwardRemarks = null;
        verifierDeficiencyMailContent = null;
        approverHardCopyReceivedOn = null;
        approver = null;
        approverForwardRemarks = null;
        approverBackwardRemarks = null;
        approverDeficiencyMailContent = null;
        currentTimeStampAsMarkHardCopyReceivedInStringFormat = null;
        tempApplNo = null;
        currentStatus = null;
        validUptoDate = null;
        validFromDate = null;
        issueDate = null;
        selectedApplicantManufacturerList.clear();
        vehClassAffiliatedByApplicantList.clear();
        vehClassAffiliatedByApplicantListForEx.clear();
        selectedApplicantManufacturerListForEx.clear();
        applicantType = "";
        selectedManufacturerMapFromSession.clear();
        extraVehiclesSoldLastYr = "";
        grandTotalInWords = "";
        originalTradeCertNo = "";
        tabIndex = "";
        inspectionBy = "";
        inspectionOn = null;
        inspectionRemark = "";
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

    public String getNoOfVehiclesUsed() {
        return noOfVehiclesUsed;
    }

    public Date getValidDt() {
        return validDt;
    }

    public Date getIssueDt() {
        return issueDt;
    }

    public String getFee() {
        return fee;
    }

    public String getIssueDateAsString() {
        return issueDateAsString;
    }

    public String getValidityPeriod() {
        return validityPeriod;
    }

    public List<IssueTradeCertDobj> getTradeCerSubList() {
        return tradeCerSubList;
    }

    public String getIssueDtAsString() {
        return issueDtAsString;
    }

    public String getValidDtAsString() {
        return validDtAsString;
    }

    public String getValidUptoAsString() {
        return validUptoAsString;
    }

    public DealerMasterDobj getDealerMasterDobj() {
        return dealerMasterDobj;
    }

    public void setDealerMasterDobj(DealerMasterDobj dealerMasterDobj) {
        this.dealerMasterDobj = dealerMasterDobj;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIterationCounter() {
        return iterationCounter;
    }

    public void setIterationCounter(int iterationCounter) {
        this.iterationCounter = iterationCounter;
    }

    public String getCurrentTimeStampAsMarkHardCopyReceivedInStringFormat() {
        return currentTimeStampAsMarkHardCopyReceivedInStringFormat;
    }

    public void setCurrentTimeStampAsMarkHardCopyReceivedInStringFormat(String currentTimeStampAsMarkHardCopyReceivedInStringFormat) {
        this.currentTimeStampAsMarkHardCopyReceivedInStringFormat = currentTimeStampAsMarkHardCopyReceivedInStringFormat;
    }

    public String getReceiverHardCopyReceivedOn() {
        return receiverHardCopyReceivedOn;
    }

    public void setReceiverHardCopyReceivedOn(String receiverHardCopyReceivedOn) {
        this.receiverHardCopyReceivedOn = receiverHardCopyReceivedOn;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverForwardRemarks() {
        return receiverForwardRemarks;
    }

    public void setReceiverForwardRemarks(String receiverForwardRemarks) {
        this.receiverForwardRemarks = receiverForwardRemarks;
    }

    public String getReceiverBackwardRemarks() {
        return receiverBackwardRemarks;
    }

    public void setReceiverBackwardRemarks(String receiverBackwardRemarks) {
        this.receiverBackwardRemarks = receiverBackwardRemarks;
    }

    public String getReceiverDeficiencyMailContent() {
        return receiverDeficiencyMailContent;
    }

    public void setReceiverDeficiencyMailContent(String receiverDeficiencyMailContent) {
        this.receiverDeficiencyMailContent = receiverDeficiencyMailContent;
    }

    public String getVerifierHardCopyReceivedOn() {
        return verifierHardCopyReceivedOn;
    }

    public void setVerifierHardCopyReceivedOn(String verifierHardCopyReceivedOn) {
        this.verifierHardCopyReceivedOn = verifierHardCopyReceivedOn;
    }

    public String getVerifier() {
        return verifier;
    }

    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }

    public String getVerifierForwardRemarks() {
        return verifierForwardRemarks;
    }

    public void setVerifierForwardRemarks(String verifierForwardRemarks) {
        this.verifierForwardRemarks = verifierForwardRemarks;
    }

    public String getVerifierBackwardRemarks() {
        return verifierBackwardRemarks;
    }

    public void setVerifierBackwardRemarks(String verifierBackwardRemarks) {
        this.verifierBackwardRemarks = verifierBackwardRemarks;
    }

    public String getVerifierDeficiencyMailContent() {
        return verifierDeficiencyMailContent;
    }

    public void setVerifierDeficiencyMailContent(String verifierDeficiencyMailContent) {
        this.verifierDeficiencyMailContent = verifierDeficiencyMailContent;
    }

    public String getApproverHardCopyReceivedOn() {
        return approverHardCopyReceivedOn;
    }

    public void setApproverHardCopyReceivedOn(String approverHardCopyReceivedOn) {
        this.approverHardCopyReceivedOn = approverHardCopyReceivedOn;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getApproverForwardRemarks() {
        return approverForwardRemarks;
    }

    public void setApproverForwardRemarks(String approverForwardRemarks) {
        this.approverForwardRemarks = approverForwardRemarks;
    }

    public String getApproverBackwardRemarks() {
        return approverBackwardRemarks;
    }

    public void setApproverBackwardRemarks(String approverBackwardRemarks) {
        this.approverBackwardRemarks = approverBackwardRemarks;
    }

    public String getApproverDeficiencyMailContent() {
        return approverDeficiencyMailContent;
    }

    public void setApproverDeficiencyMailContent(String approverDeficiencyMailContent) {
        this.approverDeficiencyMailContent = approverDeficiencyMailContent;
    }

    public String getTempApplNo() {
        return tempApplNo;
    }

    public void setTempApplNo(String tempApplNo) {
        this.tempApplNo = tempApplNo;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Date getValidUptoDate() {
        return validUptoDate;
    }

    public void setValidUptoDate(Date validUptoDate) {
        this.validUptoDate = validUptoDate;
    }

    public Date getValidFromDate() {
        return validFromDate;
    }

    public void setValidFromDate(Date validFromDate) {
        this.validFromDate = validFromDate;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public List getVehClassAffiliatedByApplicantListForEx() {
        return vehClassAffiliatedByApplicantListForEx;
    }

    public List getSelectedApplicantManufacturerListForEx() {
        return selectedApplicantManufacturerListForEx;
    }

    public void setValidUptoAsString(String validUptoAsString) {
        this.validUptoAsString = validUptoAsString;
    }

    public String getApplicantType() {
        return applicantType;
    }

    public void setApplicantType(String applicantType) {
        this.applicantType = applicantType;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getLandLineNo() {
        return landLineNo;
    }

    public void setLandLineNo(String landLineNo) {
        this.landLineNo = landLineNo;
    }

    public Map<String, String> getSelectedManufacturerMapFromSession() {
        return selectedManufacturerMapFromSession;
    }

    public String getExtraVehiclesSoldLastYr() {
        return extraVehiclesSoldLastYr;
    }

    public void setExtraVehiclesSoldLastYr(String extraVehiclesSoldLastYr) {
        this.extraVehiclesSoldLastYr = extraVehiclesSoldLastYr;
    }

    public String getGrandTotalInWords() {
        return grandTotalInWords;
    }

    public void setGrandTotalInWords(String grandTotalInWords) {
        this.grandTotalInWords = grandTotalInWords;
    }

    public void setNoOfVehiclesUsed(String noOfVehiclesUsed) {
        this.noOfVehiclesUsed = noOfVehiclesUsed;
    }

    public List getVehClassAffiliatedByApplicantList() {
        return vehClassAffiliatedByApplicantList;
    }

    public List getSelectedApplicantManufacturerList() {
        return selectedApplicantManufacturerList;
    }

    public String getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(String tabIndex) {
        this.tabIndex = tabIndex;
    }

    public String getOriginalTradeCertNo() {
        return originalTradeCertNo;
    }

    public void setOriginalTradeCertNo(String originalTradeCertNo) {
        this.originalTradeCertNo = originalTradeCertNo;
    }

    public String getApplicantTypeDescription() {
        return applicantTypeDescription;
    }

    public void setApplicantTypeDescription(String applicantTypeDescription) {
        this.applicantTypeDescription = applicantTypeDescription;
    }

    public String getInspectionBy() {
        return inspectionBy;
    }

    public void setInspectionBy(String inspectionBy) {
        this.inspectionBy = inspectionBy;
    }

    public Date getInspectionOn() {
        return inspectionOn;
    }

    public void setInspectionOn(Date inspectionOn) {
        this.inspectionOn = inspectionOn;
    }

    public String getInspectionRemark() {
        return inspectionRemark;
    }

    public void setInspectionRemark(String inspectionRemark) {
        this.inspectionRemark = inspectionRemark;
    }
}
