/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author nicsi
 */
public class RcSurrenderReleaseCancellationDobj implements Serializable, Cloneable {

    private String recieptNo;
    private String vehicleNo;
    private String chassisNo;
    private String ownerName;
    private String address;
    private String address1;
    private String vehicleClass;
    private Date fitnessValidity;
    private Date surrenderDate;
    private Date suspendedUptoDate;
    private String releaseApprovedBy;
    private String releaseFileReferenceNo;
    private Date releaseDate;
    private Date cancellationDate;
    private String cancellationApprovedBy;
    private String cancellationFileReferenceNo;
    private String cancellationReason;
    private String cancellationAuthority;
    private String approvedBy;
    private String fileReferenceNo;
    private String reason;
    private String rcStatus;
    private String rcNo;
    private String permitStatus;
    private String permitNo;
    private String fitnessCerStatus;
    private String fitnessCerNo;
    private String taxExampStatus;
    private String deal_cd;
    private String surAndSus;
    private int rcCancelBy;

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getChassisNo() {
        return chassisNo;
    }

    public void setChassisNo(String chassisNo) {
        this.chassisNo = chassisNo;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVehicleClass() {
        return vehicleClass;
    }

    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    public Date getFitnessValidity() {
        return fitnessValidity;
    }

    public void setFitnessValidity(Date fitnessValidity) {
        this.fitnessValidity = fitnessValidity;
    }

    public Date getSurrenderDate() {
        return surrenderDate;
    }

    public void setSurrenderDate(Date surrenderDate) {
        this.surrenderDate = surrenderDate;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getFileReferenceNo() {
        return fileReferenceNo;
    }

    public void setFileReferenceNo(String fileReferenceNo) {
        this.fileReferenceNo = fileReferenceNo;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRcStatus() {
        return rcStatus;
    }

    public void setRcStatus(String rcStatus) {
        this.rcStatus = rcStatus;
    }

    public String getRcNo() {
        return rcNo;
    }

    public void setRcNo(String rcNo) {
        this.rcNo = rcNo;
    }

    public String getPermitStatus() {
        return permitStatus;
    }

    public void setPermitStatus(String permitStatus) {
        this.permitStatus = permitStatus;
    }

    public String getPermitNo() {
        return permitNo;
    }

    public void setPermitNo(String permitNo) {
        this.permitNo = permitNo;
    }

    public String getFitnessCerStatus() {
        return fitnessCerStatus;
    }

    public void setFitnessCerStatus(String fitnessCerStatus) {
        this.fitnessCerStatus = fitnessCerStatus;
    }

    public String getFitnessCerNo() {
        return fitnessCerNo;
    }

    public void setFitnessCerNo(String fitnessCerNo) {
        this.fitnessCerNo = fitnessCerNo;
    }

    public String getTaxExampStatus() {
        return taxExampStatus;
    }

    public void setTaxExampStatus(String taxExampStatus) {
        this.taxExampStatus = taxExampStatus;
    }

    public String getDeal_cd() {
        return deal_cd;
    }

    public void setDeal_cd(String deal_cd) {
        this.deal_cd = deal_cd;
    }

    public String getRecieptNo() {
        return recieptNo;
    }

    public void setRecieptNo(String recieptNo) {
        this.recieptNo = recieptNo;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Date getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(Date cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public String getCancellationAuthority() {
        return cancellationAuthority;
    }

    public void setCancellationAuthority(String cancellationAuthority) {
        this.cancellationAuthority = cancellationAuthority;
    }

    public String getReleaseApprovedBy() {
        return releaseApprovedBy;
    }

    public void setReleaseApprovedBy(String releaseApprovedBy) {
        this.releaseApprovedBy = releaseApprovedBy;
    }

    public String getReleaseFileReferenceNo() {
        return releaseFileReferenceNo;
    }

    public void setReleaseFileReferenceNo(String releaseFileReferenceNo) {
        this.releaseFileReferenceNo = releaseFileReferenceNo;
    }

    public String getCancellationApprovedBy() {
        return cancellationApprovedBy;
    }

    public void setCancellationApprovedBy(String cancellationApprovedBy) {
        this.cancellationApprovedBy = cancellationApprovedBy;
    }

    public String getCancellationFileReferenceNo() {
        return cancellationFileReferenceNo;
    }

    public void setCancellationFileReferenceNo(String cancellationFileReferenceNo) {
        this.cancellationFileReferenceNo = cancellationFileReferenceNo;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getSurAndSus() {
        return surAndSus;
    }

    public void setSurAndSus(String surAndSus) {
        this.surAndSus = surAndSus;
    }

    /**
     * @return the rcCancelBy
     */
    public int getRcCancelBy() {
        return rcCancelBy;
    }

    /**
     * @param rcCancelBy the rcCancelBy to set
     */
    public void setRcCancelBy(int rcCancelBy) {
        this.rcCancelBy = rcCancelBy;
    }

    public Date getSuspendedUptoDate() {
        return suspendedUptoDate;
    }

    public void setSuspendedUptoDate(Date suspendedUptoDate) {
        this.suspendedUptoDate = suspendedUptoDate;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }
}
