/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author kaptan singh
 */
public class ScrappedVehicleDobj implements Serializable, Cloneable {

    private String appl_no;
    private String regn_no;
    private String agency_name;
    private String agency_address;
    private String no_dues_cert_no;
    private Date no_dues_issue_dt;
    private String scrap_cert_no;
    private Date scrap_cert_issue_dt;
    private String scrap_reason;
    boolean retain_regn_no;
    private String state_cd;
    private int off_cd;
    private String loi_no;
    private String old_regn_no;
    private String old_chasi_no;
    private String new_regn_no;
    private String new_chasi_no;
    private String op_dt;
    private String regn_appl_no;

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getAgency_name() {
        return agency_name;
    }

    public void setAgency_name(String agency_name) {
        this.agency_name = agency_name;
    }

    public String getAgency_address() {
        return agency_address;
    }

    public void setAgency_address(String agency_address) {
        this.agency_address = agency_address;
    }

    public Date getNo_dues_issue_dt() {
        return no_dues_issue_dt;
    }

    public void setNo_dues_issue_dt(Date no_dues_issue_dt) {
        this.no_dues_issue_dt = no_dues_issue_dt;
    }

    public Date getScrap_cert_issue_dt() {
        return scrap_cert_issue_dt;
    }

    public void setScrap_cert_issue_dt(Date scrap_cert_issue_dt) {
        this.scrap_cert_issue_dt = scrap_cert_issue_dt;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getScrap_reason() {
        return scrap_reason;
    }

    public void setScrap_reason(String scrap_reason) {
        this.scrap_reason = scrap_reason;
    }

    public boolean isRetain_regn_no() {
        return retain_regn_no;
    }

    public void setRetain_regn_no(boolean retain_regn_no) {
        this.retain_regn_no = retain_regn_no;
    }

    public String getNo_dues_cert_no() {
        return no_dues_cert_no;
    }

    public void setNo_dues_cert_no(String no_dues_cert_no) {
        this.no_dues_cert_no = no_dues_cert_no;
    }

    public String getScrap_cert_no() {
        return scrap_cert_no;
    }

    public void setScrap_cert_no(String scrap_cert_no) {
        this.scrap_cert_no = scrap_cert_no;
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

    /**
     * @return the loi_no
     */
    public String getLoi_no() {
        return loi_no;
    }

    /**
     * @param loi_no the loi_no to set
     */
    public void setLoi_no(String loi_no) {
        this.loi_no = loi_no;
    }

    /**
     * @return the old_regn_no
     */
    public String getOld_regn_no() {
        return old_regn_no;
    }

    /**
     * @param old_regn_no the old_regn_no to set
     */
    public void setOld_regn_no(String old_regn_no) {
        this.old_regn_no = old_regn_no;
    }

    /**
     * @return the old_chasi_no
     */
    public String getOld_chasi_no() {
        return old_chasi_no;
    }

    /**
     * @param old_chasi_no the old_chasi_no to set
     */
    public void setOld_chasi_no(String old_chasi_no) {
        this.old_chasi_no = old_chasi_no;
    }

    /**
     * @return the new_regn_no
     */
    public String getNew_regn_no() {
        return new_regn_no;
    }

    /**
     * @param new_regn_no the new_regn_no to set
     */
    public void setNew_regn_no(String new_regn_no) {
        this.new_regn_no = new_regn_no;
    }

    /**
     * @return the new_chasi_no
     */
    public String getNew_chasi_no() {
        return new_chasi_no;
    }

    /**
     * @param new_chasi_no the new_chasi_no to set
     */
    public void setNew_chasi_no(String new_chasi_no) {
        this.new_chasi_no = new_chasi_no;
    }

    /**
     * @return the op_dt
     */
    public String getOp_dt() {
        return op_dt;
    }

    /**
     * @param op_dt the op_dt to set
     */
    public void setOp_dt(String op_dt) {
        this.op_dt = op_dt;
    }

    /**
     * @return the regn_appl_no
     */
    public String getRegn_appl_no() {
        return regn_appl_no;
    }

    /**
     * @param regn_appl_no the regn_appl_no to set
     */
    public void setRegn_appl_no(String regn_appl_no) {
        this.regn_appl_no = regn_appl_no;
    }
}
