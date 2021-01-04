/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

public class UserInfoForMsgDobj implements Serializable {

    private String stateCd;
    private String stateName;
    private String offName;
    private String phoneOff;
    private long mobileNo;
    private String emailId;
    private String userName;
    private long userCode;
    private long unreadMessages;
    private long unclosedRequests;
    private String offCd;

    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public void setOffName(String offName) {
        this.offName = offName;
    }

    public void setPhoneOff(String phoneOff) {
        this.phoneOff = phoneOff;
    }

    public void setMobileNo(long mobileNo) {
        this.mobileNo = mobileNo;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserCode(long userCode) {
        this.userCode = userCode;
    }

    public void setUnreadMessages(long unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public void setOffCd(String offCd) {
        this.offCd = offCd;
    }

    public String getStateCd() {
        return stateCd;
    }

    public String getStateName() {
        return stateName;
    }

    public String getOffName() {
        return offName;
    }

    public String getPhoneOff() {
        return phoneOff;
    }

    public long getMobileNo() {
        return mobileNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getUserName() {
        return userName;
    }

    public long getUserCode() {
        return userCode;
    }

    public long getUnreadMessages() {
        return unreadMessages;
    }

    public String getOffCd() {
        return offCd;
    }

    public long getUnclosedRequests() {
        return unclosedRequests;
    }

    public void setUnclosedRequests(long unclosedRequests) {
        this.unclosedRequests = unclosedRequests;
    }
}
