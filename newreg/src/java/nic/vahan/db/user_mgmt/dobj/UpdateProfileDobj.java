/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.dobj;

import java.io.Serializable;

/**
 *
 * @author tranC103
 */
public class UpdateProfileDobj implements Cloneable, Serializable {

    private Long userCode;
    private String userName;
    private long mobileNo;
    private String offNo;
    private String emailID;
    private String stateCode;
    private int offCode;
    private String user_id;
    private boolean mobile_verify;
    private boolean email_verify;
    private String user_catg;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(long mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getOffNo() {
        return offNo;
    }

    public void setOffNo(String offNo) {
        this.offNo = offNo;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public long getUserCode() {
        return userCode;
    }

    public void setUserCode(long userCode) {
        this.userCode = userCode;
    }

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
     * @return the user_id
     */
    public String getUser_id() {
        return user_id;
    }

    /**
     * @param user_id the user_id to set
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public boolean isMobile_verify() {
        return mobile_verify;
    }

    public void setMobile_verify(boolean mobile_verify) {
        this.mobile_verify = mobile_verify;
    }

    /**
     * @return the email_verify
     */
    public boolean isEmail_verify() {
        return email_verify;
    }

    /**
     * @param email_verify the email_verify to set
     */
    public void setEmail_verify(boolean email_verify) {
        this.email_verify = email_verify;
    }

    /**
     * @return the user_catg
     */
    public String getUser_catg() {
        return user_catg;
    }

    /**
     * @param user_catg the user_catg to set
     */
    public void setUser_catg(String user_catg) {
        this.user_catg = user_catg;
    }
}
