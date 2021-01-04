/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;
import nic.vahan.common.jsf.utils.DateUtil;

public class NocDobj implements Cloneable, Serializable {

    private String regn_no;
    private String state_to;
    private String rto_to;
    private int off_to;
    private String ncrb_ref;
    private Date noc_dt = new Date();
    private String dispatch_no;
    private String noc_no;
    private String appl_no;
    private String state_cd;
    private int off_cd;
//    Cancellation Variables
    private int pur_cd;
    private Date c_noc_dt = new Date();
    private String file_ref_no;
    private String app_by;
    private String reason;
    private String hptDetails;
    private String state_from;
    private int off_from;
    private String chasiNo;
    private String vt_owner_status;
    private Date min_dt = DateUtil.parseDate(DateUtil.getCurrentDate());
    //add
    private int pur_cd_to;
    private String new_own_name;

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
     * @return the state_to
     */
    public String getState_to() {
        return state_to;
    }

    /**
     * @param state_to the state_to to set
     */
    public void setState_to(String state_to) {
        this.state_to = state_to;
    }

    /**
     * @return the rto_to
     */
    public String getRto_to() {
        return rto_to;
    }

    /**
     * @param rto_to the rto_to to set
     */
    public void setRto_to(String rto_to) {
        this.rto_to = rto_to;
    }

    /**
     * @return the ncrb_ref
     */
    public String getNcrb_ref() {
        return ncrb_ref;
    }

    /**
     * @param ncrb_ref the ncrb_ref to set
     */
    public void setNcrb_ref(String ncrb_ref) {
        this.ncrb_ref = ncrb_ref;
    }

    /**
     * @return the noc_dt
     */
    public Date getNoc_dt() {
        return noc_dt;
    }

    /**
     * @param noc_dt the noc_dt to set
     */
    public void setNoc_dt(Date noc_dt) {
        this.noc_dt = noc_dt;
    }

    /**
     * @return the dispatch_no
     */
    public String getDispatch_no() {
        return dispatch_no;
    }

    /**
     * @param dispatch_no the dispatch_no to set
     */
    public void setDispatch_no(String dispatch_no) {
        this.dispatch_no = dispatch_no;
    }

    /**
     * @return the noc_no
     */
    public String getNoc_no() {
        return noc_no;
    }

    /**
     * @param noc_no the noc_no to set
     */
    public void setNoc_no(String noc_no) {
        this.noc_no = noc_no;
    }

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the c_noc_dt
     */
    public Date getC_noc_dt() {
        return c_noc_dt;
    }

    /**
     * @param c_noc_dt the c_noc_dt to set
     */
    public void setC_noc_dt(Date c_noc_dt) {
        this.c_noc_dt = c_noc_dt;
    }

    /**
     * @return the file_ref_no
     */
    public String getFile_ref_no() {
        return file_ref_no;
    }

    /**
     * @param file_ref_no the file_ref_no to set
     */
    public void setFile_ref_no(String file_ref_no) {
        this.file_ref_no = file_ref_no;
    }

    /**
     * @return the app_by
     */
    public String getApp_by() {
        return app_by;
    }

    /**
     * @param app_by the app_by to set
     */
    public void setApp_by(String app_by) {
        this.app_by = app_by;
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
     * @return the pur_cd
     */
    public int getPur_cd() {
        return pur_cd;
    }

    /**
     * @param pur_cd the pur_cd to set
     */
    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    /**
     * @return the off_to
     */
    public int getOff_to() {
        return off_to;
    }

    /**
     * @param off_to the off_to to set
     */
    public void setOff_to(int off_to) {
        this.off_to = off_to;
    }

    /**
     * @return the hptDetails
     */
    public String getHptDetails() {
        return hptDetails;
    }

    /**
     * @param hptDetails the hptDetails to set
     */
    public void setHptDetails(String hptDetails) {
        this.hptDetails = hptDetails;
    }

    /**
     * @return the state_from
     */
    public String getState_from() {
        return state_from;
    }

    /**
     * @param state_from the state_from to set
     */
    public void setState_from(String state_from) {
        this.state_from = state_from;
    }

    /**
     * @return the off_from
     */
    public int getOff_from() {
        return off_from;
    }

    /**
     * @param off_from the off_from to set
     */
    public void setOff_from(int off_from) {
        this.off_from = off_from;
    }

    /**
     * @return the chasiNo
     */
    public String getChasiNo() {
        return chasiNo;
    }

    /**
     * @param chasiNo the chasiNo to set
     */
    public void setChasiNo(String chasiNo) {
        this.chasiNo = chasiNo;
    }

    /**
     * @return the vt_owner_status
     */
    public String getVt_owner_status() {
        return vt_owner_status;
    }

    /**
     * @param vt_owner_status the vt_owner_status to set
     */
    public void setVt_owner_status(String vt_owner_status) {
        this.vt_owner_status = vt_owner_status;
    }

    /**
     * @return the min_dt
     */
    public Date getMin_dt() {
        return min_dt;
    }

    /**
     * @param min_dt the min_dt to set
     */
    public void setMin_dt(Date min_dt) {
        this.min_dt = min_dt;
    }

    public int getPur_cd_to() {
        return pur_cd_to;
    }

    public void setPur_cd_to(int pur_cd_to) {
        this.pur_cd_to = pur_cd_to;
    }

    public String getNew_own_name() {
        return new_own_name;
    }

    public void setNew_own_name(String new_own_name) {
        this.new_own_name = new_own_name;
    }
}
