/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

public class OtherStateVehDobj implements Serializable, Cloneable {

    private String applNo;
    private String oldRegnNo;
    private Integer oldOffCD;
    private String oldStateCD;
    private String ncrbRef;
    private String confirmRef;
    private Date nocDate;
    private String nocNo;
    private Date stateEntryDate;
    private String newRegnNo;
    private String stateCD;
    private int offCD;
    private Date maxDate = new Date();
    private boolean inserUpdaeOtherStateFlag;
    

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
     * @return the oldRegnNo
     */
    public String getOldRegnNo() {
        return oldRegnNo;
    }

    /**
     * @param oldRegnNo the oldRegnNo to set
     */
    public void setOldRegnNo(String oldRegnNo) {
        this.oldRegnNo = oldRegnNo;
    }

    /**
     * @return the oldOffCD
     */
    public Integer getOldOffCD() {
        return oldOffCD;
    }

    /**
     * @param oldOffCD the oldOffCD to set
     */
    public void setOldOffCD(Integer oldOffCD) {
        this.oldOffCD = oldOffCD;
    }

    /**
     * @return the oldStateCD
     */
    public String getOldStateCD() {
        return oldStateCD;
    }

    /**
     * @param oldStateCD the oldStateCD to set
     */
    public void setOldStateCD(String oldStateCD) {
        this.oldStateCD = oldStateCD;
    }

    /**
     * @return the ncrbRef
     */
    public String getNcrbRef() {
        return ncrbRef;
    }

    /**
     * @param ncrbRef the ncrbRef to set
     */
    public void setNcrbRef(String ncrbRef) {
        this.ncrbRef = ncrbRef;
    }

    /**
     * @return the confirmRef
     */
    public String getConfirmRef() {
        return confirmRef;
    }

    /**
     * @param confirmRef the confirmRef to set
     */
    public void setConfirmRef(String confirmRef) {
        this.confirmRef = confirmRef;
    }

    /**
     * @return the nocDate
     */
    public Date getNocDate() {
        return nocDate;
    }

    /**
     * @param nocDate the nocDate to set
     */
    public void setNocDate(Date nocDate) {
        this.nocDate = nocDate;
    }

    /**
     * @return the nocNo
     */
    public String getNocNo() {
        return nocNo;
    }

    /**
     * @param nocNo the nocNo to set
     */
    public void setNocNo(String nocNo) {
        this.nocNo = nocNo;
    }

    /**
     * @return the stateEntryDate
     */
    public Date getStateEntryDate() {
        return stateEntryDate;
    }

    /**
     * @param stateEntryDate the stateEntryDate to set
     */
    public void setStateEntryDate(Date stateEntryDate) {
        this.stateEntryDate = stateEntryDate;
    }

    /**
     * @return the stateCD
     */
    public String getStateCD() {
        return stateCD;
    }

    /**
     * @param stateCD the stateCD to set
     */
    public void setStateCD(String stateCD) {
        this.stateCD = stateCD;
    }

    /**
     * @return the offCD
     */
    public int getOffCD() {
        return offCD;
    }

    /**
     * @param offCD the offCD to set
     */
    public void setOffCD(int offCD) {
        this.offCD = offCD;
    }

    /**
     * @return the newRegnNo
     */
    public String getNewRegnNo() {
        return newRegnNo;
    }

    /**
     * @param newRegnNo the newRegnNo to set
     */
    public void setNewRegnNo(String newRegnNo) {
        this.newRegnNo = newRegnNo;
    }

    /**
     * @return the maxDate
     */
    public Date getMaxDate() {
        return maxDate;
    }

    /**
     * @param maxDate the maxDate to set
     */
    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean isInserUpdaeOtherStateFlag() {
        return inserUpdaeOtherStateFlag;
    }

    public void setInserUpdaeOtherStateFlag(boolean inserUpdaeOtherStateFlag) {
        this.inserUpdaeOtherStateFlag = inserUpdaeOtherStateFlag;
    }

}
