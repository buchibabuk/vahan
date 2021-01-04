/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.dashBoard;

import nic.vahan.form.impl.SeatAllotedDetails;

/**
 *
 * @author Kartik
 */
public class DashboardDetails {

    private String appl_no, appl_dt, regn_no, status, remarks;
    private int action_cd, purCd, norms, vhClass, pmtType, pmtCatg;
    private String action_descr, purpose_descr, regnType, pendingSince, regnType_descr, nomrs_descr, vhClass_descr, feeType_descr, pmtType_descr, pmtCatg_descr;
    private SeatAllotedDetails selectedSeat;

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
     * @return the appl_dt
     */
    public String getAppl_dt() {
        return appl_dt;
    }

    /**
     * @param appl_dt the appl_dt to set
     */
    public void setAppl_dt(String appl_dt) {
        this.appl_dt = appl_dt;
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
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * @return the action_cd
     */
    public int getAction_cd() {
        return action_cd;
    }

    /**
     * @param action_cd the action_cd to set
     */
    public void setAction_cd(int action_cd) {
        this.action_cd = action_cd;
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

    /**
     * @return the norms
     */
    public int getNorms() {
        return norms;
    }

    /**
     * @param norms the norms to set
     */
    public void setNorms(int norms) {
        this.norms = norms;
    }

    /**
     * @return the vhClass
     */
    public int getVhClass() {
        return vhClass;
    }

    /**
     * @param vhClass the vhClass to set
     */
    public void setVhClass(int vhClass) {
        this.vhClass = vhClass;
    }

    /**
     * @return the pmtType
     */
    public int getPmtType() {
        return pmtType;
    }

    /**
     * @param pmtType the pmtType to set
     */
    public void setPmtType(int pmtType) {
        this.pmtType = pmtType;
    }

    /**
     * @return the pmtCatg
     */
    public int getPmtCatg() {
        return pmtCatg;
    }

    /**
     * @param pmtCatg the pmtCatg to set
     */
    public void setPmtCatg(int pmtCatg) {
        this.pmtCatg = pmtCatg;
    }

    /**
     * @return the action_descr
     */
    public String getAction_descr() {
        return action_descr;
    }

    /**
     * @param action_descr the action_descr to set
     */
    public void setAction_descr(String action_descr) {
        this.action_descr = action_descr;
    }

    /**
     * @return the purpose_descr
     */
    public String getPurpose_descr() {
        return purpose_descr;
    }

    /**
     * @param purpose_descr the purpose_descr to set
     */
    public void setPurpose_descr(String purpose_descr) {
        this.purpose_descr = purpose_descr;
    }

    /**
     * @return the regnType
     */
    public String getRegnType() {
        return regnType;
    }

    /**
     * @param regnType the regnType to set
     */
    public void setRegnType(String regnType) {
        this.regnType = regnType;
    }

    public String getPendingSince() {
        return pendingSince;
    }

    public void setPendingSince(String pendingSince) {
        this.pendingSince = pendingSince;
    }

    public String getNomrs_descr() {
        return nomrs_descr;
    }

    public void setNomrs_descr(String nomrs_descr) {
        this.nomrs_descr = nomrs_descr;
    }

    public String getVhClass_descr() {
        return vhClass_descr;
    }

    public void setVhClass_descr(String vhClass_descr) {
        this.vhClass_descr = vhClass_descr;
    }

    public String getPmtType_descr() {
        return pmtType_descr;
    }

    public void setPmtType_descr(String pmtType_descr) {
        this.pmtType_descr = pmtType_descr;
    }

    public String getPmtCatg_descr() {
        return pmtCatg_descr;
    }

    public void setPmtCatg_descr(String pmtCatg_descr) {
        this.pmtCatg_descr = pmtCatg_descr;
    }

    public String getRegnType_descr() {
        return regnType_descr;
    }

    public void setRegnType_descr(String regnType_descr) {
        this.regnType_descr = regnType_descr;
    }

    public String getFeeType_descr() {
        return feeType_descr;
    }

    public void setFeeType_descr(String feeType_descr) {
        this.feeType_descr = feeType_descr;
    }

    public SeatAllotedDetails getSelectedSeat() {
        return selectedSeat;
    }

    public void setSelectedSeat(SeatAllotedDetails selectedSeat) {
        this.selectedSeat = selectedSeat;
    }
}
