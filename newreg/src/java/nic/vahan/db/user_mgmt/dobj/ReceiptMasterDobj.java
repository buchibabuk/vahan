/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.dobj;

/**
 *
 * @author tranC103
 */
public class ReceiptMasterDobj {

    private String stateCode;
    private int offCode;
    private String rcptPrefix;
    private long rcptStart;
    private long rcptEnd;
    private long rcptCurrent;
    private long rcptUserCode = -1;
    private String expiredFlag;
    private String userName;

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
     * @return the rcptPrefix
     */
    public String getRcptPrefix() {
        return rcptPrefix;
    }

    /**
     * @param rcptPrefix the rcptPrefix to set
     */
    public void setRcptPrefix(String rcptPrefix) {
        this.rcptPrefix = rcptPrefix;
    }

    /**
     * @return the rcptStart
     */
    public long getRcptStart() {
        return rcptStart;
    }

    /**
     * @param rcptStart the rcptStart to set
     */
    public void setRcptStart(long rcptStart) {
        this.rcptStart = rcptStart;
    }

    /**
     * @return the rcptEnd
     */
    public long getRcptEnd() {
        return rcptEnd;
    }

    /**
     * @param rcptEnd the rcptEnd to set
     */
    public void setRcptEnd(long rcptEnd) {
        this.rcptEnd = rcptEnd;
    }

    /**
     * @return the rcptCurrent
     */
    public long getRcptCurrent() {
        return rcptCurrent;
    }

    /**
     * @param rcptCurrent the rcptCurrent to set
     */
    public void setRcptCurrent(long rcptCurrent) {
        this.rcptCurrent = rcptCurrent;
    }

    /**
     * @return the rcptUserCode
     */
    public long getRcptUserCode() {
        return rcptUserCode;
    }

    /**
     * @param rcptUserCode the rcptUserCode to set
     */
    public void setRcptUserCode(long rcptUserCode) {
        this.rcptUserCode = rcptUserCode;
    }

    /**
     * @return the expiredFlag
     */
    public String getExpiredFlag() {
        return expiredFlag;
    }

    /**
     * @param expiredFlag the expiredFlag to set
     */
    public void setExpiredFlag(String expiredFlag) {
        this.expiredFlag = expiredFlag;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
