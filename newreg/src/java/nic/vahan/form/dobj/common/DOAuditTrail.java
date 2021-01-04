/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.common;

import java.io.Serializable;

/**
 *
 * @author tranC111
 */
public class DOAuditTrail implements Serializable {

    private int mId;
    private String mUserNameId;
    private String mCreatedOn;
    private String mIpAddress;
    private String mActionType;
    private String mStatus;

    public DOAuditTrail() {
        mId = -1;
        this.mUserNameId = "";
        this.mCreatedOn = "";
        this.mIpAddress = "";
        this.mActionType = "";
        this.mStatus = "";

    }

    public DOAuditTrail(String userNameId, String ipAddress, String actionType, String status) {
        this.mUserNameId = userNameId;
        this.mIpAddress = ipAddress;
        this.mActionType = actionType;
        this.mStatus = status;

    }

    /**
     * @return the mId
     */
    public int getmId() {
        return mId;
    }

    /**
     * @param mId the mId to set
     */
    public void setmId(int mId) {
        this.mId = mId;
    }

    /**
     * @return the mUserNameId
     */
    public String getmUserNameId() {
        return mUserNameId;
    }

    /**
     * @param mUserNameId the mUserNameId to set
     */
    public void setmUserNameId(String mUserNameId) {
        this.mUserNameId = mUserNameId;
    }

    /**
     * @return the mCreatedOn
     */
    public String getmCreatedOn() {
        return mCreatedOn;
    }

    /**
     * @param mCreatedOn the mCreatedOn to set
     */
    public void setmCreatedOn(String mCreatedOn) {
        this.mCreatedOn = mCreatedOn;
    }

    /**
     * @return the mIpAddress
     */
    public String getmIpAddress() {
        return mIpAddress;
    }

    /**
     * @param mIpAddress the mIpAddress to set
     */
    public void setmIpAddress(String mIpAddress) {
        this.mIpAddress = mIpAddress;
    }

    /**
     * @return the mActionType
     */
    public String getmActionType() {
        return mActionType;
    }

    /**
     * @param mActionType the mActionType to set
     */
    public void setmActionType(String mActionType) {
        this.mActionType = mActionType;
    }

    /**
     * @return the mStatus
     */
    public String getmStatus() {
        return mStatus;
    }

    /**
     * @param mStatus the mStatus to set
     */
    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }
}
