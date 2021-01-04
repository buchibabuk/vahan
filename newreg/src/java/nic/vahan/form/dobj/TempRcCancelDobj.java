/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class TempRcCancelDobj implements Cloneable, Serializable {

    private String appl_no;
    ;
    private String tempRegnNo;
    private String chassisNo;
    private String ownerName;
    private String FatherName;
    private String address;
    private String engineNo;
    private String vhClass;
    private String bodyType;
    private String state;
    private String purchaseDt;
    private String validFrom;
    private String validUpto;
    private String reasonToCancel;
    private Date cancelDate = new Date();

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
     * @return the FatherName
     */
    public String getFatherName() {
        return FatherName;
    }

    /**
     * @param FatherName the FatherName to set
     */
    public void setFatherName(String FatherName) {
        this.FatherName = FatherName;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the engineNo
     */
    public String getEngineNo() {
        return engineNo;
    }

    /**
     * @param engineNo the engineNo to set
     */
    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    /**
     * @return the vhClass
     */
    public String getVhClass() {
        return vhClass;
    }

    /**
     * @param vhClass the vhClass to set
     */
    public void setVhClass(String vhClass) {
        this.vhClass = vhClass;
    }

    /**
     * @return the bodyType
     */
    public String getBodyType() {
        return bodyType;
    }

    /**
     * @param bodyType the bodyType to set
     */
    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the purchaseDt
     */
    public String getPurchaseDt() {
        return purchaseDt;
    }

    /**
     * @param purchaseDt the purchaseDt to set
     */
    public void setPurchaseDt(String purchaseDt) {
        this.purchaseDt = purchaseDt;
    }

    /**
     * @return the validFrom
     */
    public String getValidFrom() {
        return validFrom;
    }

    /**
     * @param validFrom the validFrom to set
     */
    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * @return the validUpto
     */
    public String getValidUpto() {
        return validUpto;
    }

    /**
     * @param validUpto the validUpto to set
     */
    public void setValidUpto(String validUpto) {
        this.validUpto = validUpto;
    }

    /**
     * @return the reasonToCancel
     */
    public String getReasonToCancel() {
        return reasonToCancel;
    }

    /**
     * @param reasonToCancel the reasonToCancel to set
     */
    public void setReasonToCancel(String reasonToCancel) {
        this.reasonToCancel = reasonToCancel;
    }

    /**
     * @return the cancelDate
     */
    public Date getCancelDate() {
        return cancelDate;
    }

    /**
     * @param cancelDate the cancelDate to set
     */
    public void setCancelDate(Date cancelDate) {
        this.cancelDate = cancelDate;
    }

    /**
     * @return the tempRegnNo
     */
    public String getTempRegnNo() {
        return tempRegnNo;
    }

    /**
     * @param tempRegnNo the tempRegnNo to set
     */
    public void setTempRegnNo(String tempRegnNo) {
        this.tempRegnNo = tempRegnNo;
    }

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }
}
