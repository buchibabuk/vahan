/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;

/**
 *
 * @author R Gautam
 */
public class PermitPaidFeeDtlsDobj implements Serializable {

    private int fees;
    private int fine;
    private String rcpt_no;
    private String regn_no;
    private String rcpt_dt;
    private String collected_by;
    private String state_cd;
    private int off_cd;
    private String purpose;
    private int purCd;

    public int getFees() {
        return fees;
    }

    public void setFees(int fees) {
        this.fees = fees;
    }

    public int getFine() {
        return fine;
    }

    public void setFine(int fine) {
        this.fine = fine;
    }

    public String getRcpt_no() {
        return rcpt_no;
    }

    public void setRcpt_no(String rcpt_no) {
        this.rcpt_no = rcpt_no;
    }

    public String getRcpt_dt() {
        return rcpt_dt;
    }

    public void setRcpt_dt(String rcpt_dt) {
        this.rcpt_dt = rcpt_dt;
    }

    public String getCollected_by() {
        return collected_by;
    }

    public void setCollected_by(String collected_by) {
        this.collected_by = collected_by;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public int getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * @return the purCd
     */
    public int getPurCd() {
        return purCd;
    }

    /**
     * @param purCd the purCd to set
     */
    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }
}
