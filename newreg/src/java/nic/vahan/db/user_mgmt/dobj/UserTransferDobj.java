/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.dobj;

import java.io.Serializable;

/**
 *
 * @author tranC103
 */
public class UserTransferDobj implements Serializable {

    private String stateCode;
    private int offCode;
    private String userName;
    private long userCode;
    private String emailID;
    private String offName;
    private String designation;
    private long mobileNo;
    private String userID;
    private String officePh;
    private String adhaarNo;
    private String userCatg;
    private int oldOffCode;

    /**
     * @return the stateCode
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * @param stateCode the stateCode to set
     */
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    /**
     * @return the offCode
     */
    public int getOffCode() {
        return offCode;
    }

    /**
     * @param offCode the offCode to set
     */
    public void setOffCode(int offCode) {
        this.offCode = offCode;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the emailID
     */
    public String getEmailID() {
        return emailID;
    }

    /**
     * @param emailID the emailID to set
     */
    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    /**
     * @return the offName
     */
    public String getOffName() {
        return offName;
    }

    /**
     * @param offName the offName to set
     */
    public void setOffName(String offName) {
        this.offName = offName;
    }

    /**
     * @return the designation
     */
    public String getDesignation() {
        return designation;
    }

    /**
     * @param designation the designation to set
     */
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    /**
     * @return the mobileNo
     */
    public long getMobileNo() {
        return mobileNo;
    }

    /**
     * @param mobileNo the mobileNo to set
     */
    public void setMobileNo(long mobileNo) {
        this.mobileNo = mobileNo;
    }

    /**
     * @return the userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * @return the officePh
     */
    public String getOfficePh() {
        return officePh;
    }

    /**
     * @param officePh the officePh to set
     */
    public void setOfficePh(String officePh) {
        this.officePh = officePh;
    }

    /**
     * @return the adhaarNo
     */
    public String getAdhaarNo() {
        return adhaarNo;
    }

    /**
     * @param adhaarNo the adhaarNo to set
     */
    public void setAdhaarNo(String adhaarNo) {
        this.adhaarNo = adhaarNo;
    }

    /**
     * @return the userCode
     */
    public long getUserCode() {
        return userCode;
    }

    /**
     * @param userCode the userCode to set
     */
    public void setUserCode(long userCode) {
        this.userCode = userCode;
    }

    /**
     * @return the userCatg
     */
    public String getUserCatg() {
        return userCatg;
    }

    /**
     * @param userCatg the userCatg to set
     */
    public void setUserCatg(String userCatg) {
        this.userCatg = userCatg;
    }

    /**
     * @return the oldOffCode
     */
    public int getOldOffCode() {
        return oldOffCode;
    }

    /**
     * @param oldOffCode the oldOffCode to set
     */
    public void setOldOffCode(int oldOffCode) {
        this.oldOffCode = oldOffCode;
    }
}
