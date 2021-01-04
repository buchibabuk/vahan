/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.tradecert;

import java.io.Serializable;

/**
 *
 * @author nic
 */
public class ApplicationAuditTrailTradeCertDobj implements Serializable {

    private String applicationNo = "";
    private String iterationCounter;
    private String applicant = "";
    private String applicantForwardRemarks = "";
    private String receiverHardCopyReceivedOn = "";
    private String receiver = "";
    private String receiverForwardRemarks = "";
    private String receiverBackwardRemarks = "";
    private String receiverDeficiencyMailContent = "";
    private String verifierHardCopyReceivedOn = ""; //         
    private String verifier = "";
    private String verifierForwardRemarks = "";
    private String verifierBackwardRemarks = "";
    private String verifierDeficiencyMailContent = "";
    private String approverHardCopyReceivedOn = "";
    private String approver = "";
    private String approverForwardRemarks = "";
    private String approverBackwardRemarks = "";
    private String approverDeficiencyMailContent = "";
    private String currentStatus = "";

    /**
     * @return the applicationNo
     */
    public String getApplicationNo() {
        return applicationNo;
    }

    /**
     * @param applicationNo the applicationNo to set
     */
    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    /**
     * @return the iterationCounter
     */
    public String getIterationCounter() {
        return iterationCounter;
    }

    /**
     * @param iterationCounter the iterationCounter to set
     */
    public void setIterationCounter(String iterationCounter) {
        this.iterationCounter = iterationCounter;
    }

    /**
     * @return the receiverHardCopyReceivedOn
     */
    public String getReceiverHardCopyReceivedOn() {
        return receiverHardCopyReceivedOn;
    }

    /**
     * @param receiverHardCopyReceivedOn the receiverHardCopyReceivedOn to set
     */
    public void setReceiverHardCopyReceivedOn(String receiverHardCopyReceivedOn) {
        this.receiverHardCopyReceivedOn = receiverHardCopyReceivedOn;
    }

    /**
     * @return the receiver
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * @param receiver the receiver to set
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * @return the receiverForwardRemarks
     */
    public String getReceiverForwardRemarks() {
        return receiverForwardRemarks;
    }

    /**
     * @param receiverForwardRemarks the receiverForwardRemarks to set
     */
    public void setReceiverForwardRemarks(String receiverForwardRemarks) {
        this.receiverForwardRemarks = receiverForwardRemarks;
    }

    /**
     * @return the receiverbackwardRemarks
     */
    public String getReceiverBackwardRemarks() {
        return receiverBackwardRemarks;
    }

    /**
     * @param receiverbackwardRemarks the receiverbackwardRemarks to set
     */
    public void setReceiverBackwardRemarks(String receiverBackwardRemarks) {
        this.receiverBackwardRemarks = receiverBackwardRemarks;
    }

    /**
     * @return the receiverDeficiencyMailContent
     */
    public String getReceiverDeficiencyMailContent() {
        return receiverDeficiencyMailContent;
    }

    /**
     * @param receiverDeficiencyMailContent the receiverDeficiencyMailContent to
     * set
     */
    public void setReceiverDeficiencyMailContent(String receiverDeficiencyMailContent) {
        this.receiverDeficiencyMailContent = receiverDeficiencyMailContent;
    }

    /**
     * @return the verifierHardCopyReceivedOn
     */
    public String getVerifierHardCopyReceivedOn() {
        return verifierHardCopyReceivedOn;
    }

    /**
     * @param verifierHardCopyReceivedOn the verifierHardCopyReceivedOn to set
     */
    public void setVerifierHardCopyReceivedOn(String verifierHardCopyReceivedOn) {
        this.verifierHardCopyReceivedOn = verifierHardCopyReceivedOn;
    }

    /**
     * @return the verifier
     */
    public String getVerifier() {
        return verifier;
    }

    /**
     * @param verifier the verifier to set
     */
    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }

    /**
     * @return the verifierForwardRemarks
     */
    public String getVerifierForwardRemarks() {
        return verifierForwardRemarks;
    }

    /**
     * @param verifierForwardRemarks the verifierForwardRemarks to set
     */
    public void setVerifierForwardRemarks(String verifierForwardRemarks) {
        this.verifierForwardRemarks = verifierForwardRemarks;
    }

    /**
     * @return the verifierBackwardRemarks
     */
    public String getVerifierBackwardRemarks() {
        return verifierBackwardRemarks;
    }

    /**
     * @param verifierBackwardRemarks the verifierBackwardRemarks to set
     */
    public void setVerifierBackwardRemarks(String verifierBackwardRemarks) {
        this.verifierBackwardRemarks = verifierBackwardRemarks;
    }

    /**
     * @return the verifierDeficiencyMailContent
     */
    public String getVerifierDeficiencyMailContent() {
        return verifierDeficiencyMailContent;
    }

    /**
     * @param verifierDeficiencyMailContent the verifierDeficiencyMailContent to
     * set
     */
    public void setVerifierDeficiencyMailContent(String verifierDeficiencyMailContent) {
        this.verifierDeficiencyMailContent = verifierDeficiencyMailContent;
    }

    /**
     * @return the approverHardCopyReceivedOn
     */
    public String getApproverHardCopyReceivedOn() {
        return approverHardCopyReceivedOn;
    }

    /**
     * @param approverHardCopyReceivedOn the approverHardCopyReceivedOn to set
     */
    public void setApproverHardCopyReceivedOn(String approverHardCopyReceivedOn) {
        this.approverHardCopyReceivedOn = approverHardCopyReceivedOn;
    }

    /**
     * @return the approver
     */
    public String getApprover() {
        return approver;
    }

    /**
     * @param approver the approver to set
     */
    public void setApprover(String approver) {
        this.approver = approver;
    }

    /**
     * @return the approverForwardRemarks
     */
    public String getApproverForwardRemarks() {
        return approverForwardRemarks;
    }

    /**
     * @param approverForwardRemarks the approverForwardRemarks to set
     */
    public void setApproverForwardRemarks(String approverForwardRemarks) {
        this.approverForwardRemarks = approverForwardRemarks;
    }

    /**
     * @return the approverBackwardRemarks
     */
    public String getApproverBackwardRemarks() {
        return approverBackwardRemarks;
    }

    /**
     * @param approverBackwardRemarks the approverBackwardRemarks to set
     */
    public void setApproverBackwardRemarks(String approverBackwardRemarks) {
        this.approverBackwardRemarks = approverBackwardRemarks;
    }

    /**
     * @return the approverDeficiencyMailContent
     */
    public String getApproverDeficiencyMailContent() {
        return approverDeficiencyMailContent;
    }

    /**
     * @param approverDeficiencyMailContent the approverDeficiencyMailContent to
     * set
     */
    public void setApproverDeficiencyMailContent(String approverDeficiencyMailContent) {
        this.approverDeficiencyMailContent = approverDeficiencyMailContent;
    }

    /**
     * @return the currentStatus
     */
    public String getCurrentStatus() {
        return currentStatus;
    }

    /**
     * @param currentStatus the currentStatus to set
     */
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getApplicantForwardRemarks() {
        return applicantForwardRemarks;
    }

    public void setApplicantForwardRemarks(String applicantForwardRemarks) {
        this.applicantForwardRemarks = applicantForwardRemarks;
    }
}
