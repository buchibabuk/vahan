/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.hsrp;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class HSRPFileUploadDobj implements Serializable {

    private int serialNo = 0;
    private String stateCd;
    private int offCd;
    private String applNo;
    private String regnNo;
    private String hsrpFlag;
    private String empCd;
    private Date opDt;
    private String hsrpNoFront;
    private String hsrpNoBack;
    private String hsrpFixDt;
    private String hsrpFixAmt;
    private String hsrpAmtTakenOn;
    private Date hsrpOpDt;
    private String chassisNo = "";
    private String engNo = "";
    private String vehicleClass = "";
    private String maker = "";
    private String model = "";
    private String ownerName = "";
    private String dealer = "";
    private String reciptNo = "";
    private String star = "";
    private String seperator = "";
    private int srNo = 0;
    private String curDate;

    /**
     * @return the stateCd
     */
    public String getStateCd() {
        return stateCd;
    }

    /**
     * @param stateCd the stateCd to set
     */
    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    /**
     * @return the offCd
     */
    public int getOffCd() {
        return offCd;
    }

    /**
     * @param offCd the offCd to set
     */
    public void setOffCd(int offCd) {
        this.offCd = offCd;
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
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the hsrpFlag
     */
    public String getHsrpFlag() {
        return hsrpFlag;
    }

    /**
     * @param hsrpFlag the hsrpFlag to set
     */
    public void setHsrpFlag(String hsrpFlag) {
        this.hsrpFlag = hsrpFlag;
    }

    /**
     * @return the empCd
     */
    public String getEmpCd() {
        return empCd;
    }

    /**
     * @param empCd the empCd to set
     */
    public void setEmpCd(String empCd) {
        this.empCd = empCd;
    }

    /**
     * @return the opDt
     */
    public Date getOpDt() {
        return opDt;
    }

    /**
     * @param opDt the opDt to set
     */
    public void setOpDt(Date opDt) {
        this.opDt = opDt;
    }

    /**
     * @return the hsrpNoFront
     */
    public String getHsrpNoFront() {
        return hsrpNoFront;
    }

    /**
     * @param hsrpNoFront the hsrpNoFront to set
     */
    public void setHsrpNoFront(String hsrpNoFront) {
        this.hsrpNoFront = hsrpNoFront;
    }

    /**
     * @return the hsrpNoBack
     */
    public String getHsrpNoBack() {
        return hsrpNoBack;
    }

    /**
     * @param hsrpNoBack the hsrpNoBack to set
     */
    public void setHsrpNoBack(String hsrpNoBack) {
        this.hsrpNoBack = hsrpNoBack;
    }

    /**
     * @return the hsrpOpDt
     */
    public Date getHsrpOpDt() {
        return hsrpOpDt;
    }

    /**
     * @param hsrpOpDt the hsrpOpDt to set
     */
    public void setHsrpOpDt(Date hsrpOpDt) {
        this.hsrpOpDt = hsrpOpDt;
    }

    /**
     * @return the chassisNo
     */
    public String getChassisNo() {
        return chassisNo;
    }

    /**
     * @param chassisNo the chassisNo to set
     */
    public void setChassisNo(String chassisNo) {
        this.chassisNo = chassisNo;
    }

    /**
     * @return the engNo
     */
    public String getEngNo() {
        return engNo;
    }

    /**
     * @param engNo the engNo to set
     */
    public void setEngNo(String engNo) {
        this.engNo = engNo;
    }

    /**
     * @return the vehicleClass
     */
    public String getVehicleClass() {
        return vehicleClass;
    }

    /**
     * @param vehicleClass the vehicleClass to set
     */
    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    /**
     * @return the maker
     */
    public String getMaker() {
        return maker;
    }

    /**
     * @param maker the maker to set
     */
    public void setMaker(String maker) {
        this.maker = maker;
    }

    /**
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(String model) {
        this.model = model;
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

    /**
     * @return the dealer
     */
    public String getDealer() {
        return dealer;
    }

    /**
     * @param dealer the dealer to set
     */
    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    /**
     * @return the reciptNo
     */
    public String getReciptNo() {
        return reciptNo;
    }

    /**
     * @param reciptNo the reciptNo to set
     */
    public void setReciptNo(String reciptNo) {
        this.reciptNo = reciptNo;
    }

    /**
     * @return the star
     */
    public String getStar() {
        return star;
    }

    /**
     * @param star the star to set
     */
    public void setStar(String star) {
        this.star = star;
    }

    /**
     * @return the seperator
     */
    public String getSeperator() {
        return seperator;
    }

    /**
     * @param seperator the seperator to set
     */
    public void setSeperator(String seperator) {
        this.seperator = seperator;
    }

    /**
     * @return the srNo
     */
    public int getSrNo() {
        return srNo;
    }

    /**
     * @param srNo the srNo to set
     */
    public void setSrNo(int srNo) {
        this.srNo = srNo;
    }

    /**
     * @return the curDate
     */
    public String getCurDate() {
        return curDate;
    }

    /**
     * @param curDate the curDate to set
     */
    public void setCurDate(String curDate) {
        this.curDate = curDate;
    }

    /**
     * @return the hsrpFixDt
     */
    public String getHsrpFixDt() {
        return hsrpFixDt;
    }

    /**
     * @param hsrpFixDt the hsrpFixDt to set
     */
    public void setHsrpFixDt(String hsrpFixDt) {
        this.hsrpFixDt = hsrpFixDt;
    }

    /**
     * @return the hsrpFixAmt
     */
    public String getHsrpFixAmt() {
        return hsrpFixAmt;
    }

    /**
     * @param hsrpFixAmt the hsrpFixAmt to set
     */
    public void setHsrpFixAmt(String hsrpFixAmt) {
        this.hsrpFixAmt = hsrpFixAmt;
    }

    /**
     * @return the hsrpAmtTakenOn
     */
    public String getHsrpAmtTakenOn() {
        return hsrpAmtTakenOn;
    }

    /**
     * @param hsrpAmtTakenOn the hsrpAmtTakenOn to set
     */
    public void setHsrpAmtTakenOn(String hsrpAmtTakenOn) {
        this.hsrpAmtTakenOn = hsrpAmtTakenOn;
    }

    /**
     * @return the serialNo
     */
    public int getSerialNo() {
        return serialNo;
    }

    /**
     * @param serialNo the serialNo to set
     */
    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }
}
