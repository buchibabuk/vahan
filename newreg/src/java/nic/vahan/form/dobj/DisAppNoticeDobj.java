/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author tranC103
 */
public class DisAppNoticeDobj implements Serializable {

    private String applNo;
    private String regnNo;
    private String offName;
    private String stateName;
    private String ownerName;
    private String address;
    private String purpose;
    private List reasonList;
    private String rcptheading;
    private String entryDate;

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public String getOffName() {
        return offName;
    }

    public void setOffName(String offName) {
        this.offName = offName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List getReasonList() {
        return reasonList;
    }

    public void setReasonList(List reasonList) {
        this.reasonList = reasonList;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getRcptheading() {
        return rcptheading;
    }

    public void setRcptheading(String rcptheading) {
        this.rcptheading = rcptheading;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }
}
