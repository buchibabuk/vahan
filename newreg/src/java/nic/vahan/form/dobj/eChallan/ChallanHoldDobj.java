/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class ChallanHoldDobj implements Serializable{

    private String challanNo;
    private String applNo;
    private String vehicleNo;
    private String challanPlace;
    private String challanOfficer;
    private String challanDt;
    private String challanTime;
    private String challanHoldStatus;
    private String ownerName;
    private List applNoList = new ArrayList();
    private String holdReason;
    private int holdFee;
    private String holdFrom;
    private String holdUpto;
     private Date minDate =new Date();

   

    /**
     * @return the challanNo
     */
    public String getChallanNo() {
        return challanNo;
    }

    /**
     * @param challanNo the challanNo to set
     */
    public void setChallanNo(String challanNo) {
        this.challanNo = challanNo;
    }

    /**
     * @return the vehicleNo
     */
    public String getVehicleNo() {
        return vehicleNo;
    }

    /**
     * @param vehicleNo the vehicleNo to set
     */
    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
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
     * @return the challanOfficer
     */
    public String getChallanOfficer() {
        return challanOfficer;
    }

    /**
     * @param challanOfficer the challanOfficer to set
     */
    public void setChallanOfficer(String challanOfficer) {
        this.challanOfficer = challanOfficer;
    }

    /**
     * @return the challanDt
     */
    public String getChallanDt() {
        return challanDt;
    }

    /**
     * @param challanDt the challanDt to set
     */
    public void setChallanDt(String challanDt) {
        this.challanDt = challanDt;
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
     * @return the challanHoldStatus
     */
    public String getChallanHoldStatus() {
        return challanHoldStatus;
    }

    /**
     * @param challanHoldStatus the challanHoldStatus to set
     */
    public void setChallanHoldStatus(String challanHoldStatus) {
        this.challanHoldStatus = challanHoldStatus;
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

    @Override
    public String toString() {
        return "ChallanHoldDobj{" + "challanNo=" + challanNo + ", vehicleNo=" + vehicleNo + ", challanPlace=" + challanPlace + ", challanOfficer=" + challanOfficer + ", challanDt=" + challanDt + ", challanTime=" + challanTime + ", challanHoldStatus=" + challanHoldStatus + ", ownerName=" + ownerName + '}';
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
     * @return the applNoList
     */
    public List getApplNoList() {
        return applNoList;
    }

    /**
     * @param applNoList the applNoList to set
     */
    public void setApplNoList(List applNoList) {
        this.applNoList = applNoList;
    }

    /**
     * @return the holdReason
     */
    public String getHoldReason() {
        return holdReason;
    }

    /**
     * @param holdReason the holdReason to set
     */
    public void setHoldReason(String holdReason) {
        this.holdReason = holdReason;
    }

    /**
     * @return the holdFee
     */
    public int getHoldFee() {
        return holdFee;
    }

    /**
     * @param holdFee the holdFee to set
     */
    public void setHoldFee(int holdFee) {
        this.holdFee = holdFee;
    }

    /**
     * @return the holdFrom
     */
    public String getHoldFrom() {
        return holdFrom;
    }

    /**
     * @param holdFrom the holdFrom to set
     */
    public void setHoldFrom(String holdFrom) {
        this.holdFrom = holdFrom;
    }

    /**
     * @return the holdUpto
     */
    public String getHoldUpto() {
        return holdUpto;
    }

    /**
     * @param holdUpto the holdUpto to set
     */
    public void setHoldUpto(String holdUpto) {
        this.holdUpto = holdUpto;
    }

    /**
     * @return the minDate
     */
    public Date getMinDate() {
        return minDate;
    }

    /**
     * @param minDate the minDate to set
     */
    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }
}
