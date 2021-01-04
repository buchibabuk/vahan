/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tranC114
 */
public class PendingReportDobj implements Serializable {

    private String serialNo;
    private String applNo;
    private String applDate;
    private String recptNo;
    private String vehicleNo;
    private String ownerName;
    private String trans;
    private String dataentryOn;
    private String approvalStatus;
    private String purpose;
    private String status;
    private String remark;
    private String entered_On;
    private String entered_by;
    private String delay_days;
    private String actionDescr;
    private String file_movement_slno;
    private String srNo;
    private String kms_dt;
    private List<PendingReportDobj> returnlist = new ArrayList<PendingReportDobj>();

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getRecptNo() {
        return recptNo;
    }

    public void setRecptNo(String recptNo) {
        this.recptNo = recptNo;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public String getDataentryOn() {
        return dataentryOn;
    }

    public void setDataentryOn(String dataentryOn) {
        this.dataentryOn = dataentryOn;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEntered_On() {
        return entered_On;
    }

    public void setEntered_On(String entered_On) {
        this.entered_On = entered_On;
    }

    public String getEntered_by() {
        return entered_by;
    }

    public void setEntered_by(String entered_by) {
        this.entered_by = entered_by;
    }

    public String getDelay_days() {
        return delay_days;
    }

    public void setDelay_days(String delay_days) {
        this.delay_days = delay_days;
    }

    public String getSrNo() {
        return srNo;
    }

    public void setSrNo(String srNo) {
        this.srNo = srNo;
    }

    public List<PendingReportDobj> getReturnlist() {
        return returnlist;
    }

    public void setReturnlist(List<PendingReportDobj> returnlist) {
        this.returnlist = returnlist;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public String getApplDate() {
        return applDate;
    }

    public void setApplDate(String applDate) {
        this.applDate = applDate;
    }

    public String getActionDescr() {
        return actionDescr;
    }

    public void setActionDescr(String actionDescr) {
        this.actionDescr = actionDescr;
    }

    public String getFile_movement_slno() {
        return file_movement_slno;
    }

    public void setFile_movement_slno(String file_movement_slno) {
        this.file_movement_slno = file_movement_slno;
    }

    public String getKms_dt() {
        return kms_dt;
    }

    public void setKms_dt(String kms_dt) {
        this.kms_dt = kms_dt;
    }
}
