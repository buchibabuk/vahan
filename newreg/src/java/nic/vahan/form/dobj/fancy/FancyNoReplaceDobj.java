/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.fancy;

import java.io.Serializable;

/**
 *
 * @author nicsi
 */
public class FancyNoReplaceDobj implements Serializable {

    private boolean assignRetainNo;
    private String assignRetainRegnNo;
    private boolean assignAdvanceNunber;
    private String assignAdvanceNO;
    private String remarks;
    private String rcpt_no;
    private int purCode;
    private String applNo;

    public boolean isAssignRetainNo() {
        return assignRetainNo;
    }

    public void setAssignRetainNo(boolean assignRetainNo) {
        this.assignRetainNo = assignRetainNo;
    }

    public String getAssignRetainRegnNo() {
        return assignRetainRegnNo;
    }

    public void setAssignRetainRegnNo(String assignRetainRegnNo) {
        this.assignRetainRegnNo = assignRetainRegnNo;
    }

    public boolean isAssignAdvanceNunber() {
        return assignAdvanceNunber;
    }

    public void setAssignAdvanceNunber(boolean assignAdvanceNunber) {
        this.assignAdvanceNunber = assignAdvanceNunber;
    }

    public String getAssignAdvanceNO() {
        return assignAdvanceNO;
    }

    public void setAssignAdvanceNO(String assignAdvanceNO) {
        this.assignAdvanceNO = assignAdvanceNO;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRcpt_no() {
        return rcpt_no;
    }

    public void setRcpt_no(String rcpt_no) {
        this.rcpt_no = rcpt_no;
    }

    public int getPurCode() {
        return purCode;
    }

    public void setPurCode(int purCode) {
        this.purCode = purCode;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }
}
