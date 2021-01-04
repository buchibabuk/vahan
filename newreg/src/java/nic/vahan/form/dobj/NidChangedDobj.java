/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author ASHOK
 */
public class NidChangedDobj implements Serializable {

    private String state_cd;
    private int off_cd;
    private String regn_no;
    private Date old_fit_nid;
    private Date new_fit_nid;
    private String reason;
    private Timestamp moved_on;
    private String moved_by;

    /**
     * @return the state_cd
     */
    public String getState_cd() {
        return state_cd;
    }

    /**
     * @param state_cd the state_cd to set
     */
    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    /**
     * @return the off_cd
     */
    public int getOff_cd() {
        return off_cd;
    }

    /**
     * @param off_cd the off_cd to set
     */
    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    /**
     * @return the regn_no
     */
    public String getRegn_no() {
        return regn_no;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    /**
     * @return the old_fit_nid
     */
    public Date getOld_fit_nid() {
        return old_fit_nid;
    }

    /**
     * @param old_fit_nid the old_fit_nid to set
     */
    public void setOld_fit_nid(Date old_fit_nid) {
        this.old_fit_nid = old_fit_nid;
    }

    /**
     * @return the new_fit_nid
     */
    public Date getNew_fit_nid() {
        return new_fit_nid;
    }

    /**
     * @param new_fit_nid the new_fit_nid to set
     */
    public void setNew_fit_nid(Date new_fit_nid) {
        this.new_fit_nid = new_fit_nid;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the moved_on
     */
    public Timestamp getMoved_on() {
        return moved_on;
    }

    /**
     * @param moved_on the moved_on to set
     */
    public void setMoved_on(Timestamp moved_on) {
        this.moved_on = moved_on;
    }

    /**
     * @return the moved_by
     */
    public String getMoved_by() {
        return moved_by;
    }

    /**
     * @param moved_by the moved_by to set
     */
    public void setMoved_by(String moved_by) {
        this.moved_by = moved_by;
    }
}
