/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author nicsi
 */
public class InformationViolationReportDobj implements Serializable {

    private String applicationno;
    private String challanNo;
    private String vehicleNo;
    private Date challanDate;
    private Date opdate;
    private Date hearingDate;
    private String ownerName;
    private List courtcode;
    private String courtName;
    private String recieptHeading;
    private String recieptSubHeading;
    private String authorityName;
    private String offence;
    private Date authorityReferDate;
    private String repotingOfficer;
    private String witnessName;
    private String witnessAdd;
    private String witnessPoliceStation;
    private String challanTime;
    private String challanPlace;
    private String accusedName;
    private String accusedAddress;
    private String AccusedPoliceStation;

    public String getApplicationno() {
        return applicationno;
    }

    public void setApplicationno(String applicationno) {
        this.applicationno = applicationno;
    }

    public String getChallanNo() {
        return challanNo;
    }

    public void setChallanNo(String challanNo) {
        this.challanNo = challanNo;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public Date getChallanDate() {
        return challanDate;
    }

    public void setChallanDate(Date challanDate) {
        this.challanDate = challanDate;
    }

    public Date getOpdate() {
        return opdate;
    }

    public void setOpdate(Date opdate) {
        this.opdate = opdate;
    }

    public Date getHearingDate() {
        return hearingDate;
    }

    public void setHearingDate(Date hearingDate) {
        this.hearingDate = hearingDate;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public List getCourtcode() {
        return courtcode;
    }

    public void setCourtcode(List courtcode) {
        this.courtcode = courtcode;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getRecieptHeading() {
        return recieptHeading;
    }

    public void setRecieptHeading(String recieptHeading) {
        this.recieptHeading = recieptHeading;
    }

    public String getRecieptSubHeading() {
        return recieptSubHeading;
    }

    public void setRecieptSubHeading(String recieptSubHeading) {
        this.recieptSubHeading = recieptSubHeading;
    }

    /**
     * @return the authorityName
     */
    public String getAuthorityName() {
        return authorityName;
    }

    /**
     * @param authorityName the authorityName to set
     */
    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    /**
     * @return the offence
     */
    public String getOffence() {
        return offence;
    }

    /**
     * @param offence the offence to set
     */
    public void setOffence(String offence) {
        this.offence = offence;
    }

    /**
     * @return the authorityReferDate
     */
    public Date getAuthorityReferDate() {
        return authorityReferDate;
    }

    /**
     * @param authorityReferDate the authorityReferDate to set
     */
    public void setAuthorityReferDate(Date authorityReferDate) {
        this.authorityReferDate = authorityReferDate;
    }

    /**
     * @return the repotingOfficer
     */
    public String getRepotingOfficer() {
        return repotingOfficer;
    }

    /**
     * @param repotingOfficer the repotingOfficer to set
     */
    public void setRepotingOfficer(String repotingOfficer) {
        this.repotingOfficer = repotingOfficer;
    }

    /**
     * @return the witnessName
     */
    public String getWitnessName() {
        return witnessName;
    }

    /**
     * @param witnessName the witnessName to set
     */
    public void setWitnessName(String witnessName) {
        this.witnessName = witnessName;
    }

    /**
     * @return the witnessAdd
     */
    public String getWitnessAdd() {
        return witnessAdd;
    }

    /**
     * @param witnessAdd the witnessAdd to set
     */
    public void setWitnessAdd(String witnessAdd) {
        this.witnessAdd = witnessAdd;
    }

    /**
     * @return the challanTime
     */
    public String getChallanTime() {
        return challanTime;
    }

    /**
     * @param challanTime the challanTime to set
     */
    public void setChallanTime(String challanTime) {
        this.challanTime = challanTime;
    }

    /**
     * @return the challanPlace
     */
    public String getChallanPlace() {
        return challanPlace;
    }

    /**
     * @param challanPlace the challanPlace to set
     */
    public void setChallanPlace(String challanPlace) {
        this.challanPlace = challanPlace;
    }

    /**
     * @return the accusedName
     */
    public String getAccusedName() {
        return accusedName;
    }

    /**
     * @param accusedName the accusedName to set
     */
    public void setAccusedName(String accusedName) {
        this.accusedName = accusedName;
    }

    /**
     * @return the accusedAddress
     */
    public String getAccusedAddress() {
        return accusedAddress;
    }

    /**
     * @param accusedAddress the accusedAddress to set
     */
    public void setAccusedAddress(String accusedAddress) {
        this.accusedAddress = accusedAddress;
    }

    /**
     * @return the AccusedPoliceStation
     */
    public String getAccusedPoliceStation() {
        return AccusedPoliceStation;
    }

    /**
     * @param AccusedPoliceStation the AccusedPoliceStation to set
     */
    public void setAccusedPoliceStation(String AccusedPoliceStation) {
        this.AccusedPoliceStation = AccusedPoliceStation;
    }

    /**
     * @return the witnessPoliceStation
     */
    public String getWitnessPoliceStation() {
        return witnessPoliceStation;
    }

    /**
     * @param witnessPoliceStation the witnessPoliceStation to set
     */
    public void setWitnessPoliceStation(String witnessPoliceStation) {
        this.witnessPoliceStation = witnessPoliceStation;
    }
}
