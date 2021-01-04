/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author ahana
 */
public class UploadPollutionDataDobj implements Serializable {
    private int srNo;
    private String state_cd;
    private int off_cd;
    private String regn_no;
    private int complain_type;
    private String fir_no;
    private Date fir_dt;
    private String complain;
    private Timestamp complain_dt;
    private String entered_by;

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
     * @return the complain_type
     */
    public int getComplain_type() {
        return complain_type;
    }

    /**
     * @param complain_type the complain_type to set
     */
    public void setComplain_type(int complain_type) {
        this.complain_type = complain_type;
    }

    /**
     * @return the fir_no
     */
    public String getFir_no() {
        return fir_no;
    }

    /**
     * @param fir_no the fir_no to set
     */
    public void setFir_no(String fir_no) {
        this.fir_no = fir_no;
    }

    /**
     * @return the fir_dt
     */
    public Date getFir_dt() {
        return fir_dt;
    }

    /**
     * @param fir_dt the fir_dt to set
     */
    public void setFir_dt(Date fir_dt) {
        this.fir_dt = fir_dt;
    }

    /**
     * @return the complain
     */
    public String getComplain() {
        return complain;
    }

    /**
     * @param complain the complain to set
     */
    public void setComplain(String complain) {
        this.complain = complain;
    }

   
    /**
     * @return the entered_by
     */
    public String getEntered_by() {
        return entered_by;
    }

    /**
     * @param entered_by the entered_by to set
     */
    public void setEntered_by(String entered_by) {
        this.entered_by = entered_by;
    }

    /**
     * @return the complain_dt
     */
    public Timestamp getComplain_dt() {
        return complain_dt;
    }

    /**
     * @param complain_dt the complain_dt to set
     */
    public void setComplain_dt(Timestamp complain_dt) {
        this.complain_dt = complain_dt;
    }

    /**
     * @return the srNo
     */
    public int getSrNo() {
        return srNo;
    }

    /**
     * @param srNo the srNo to set
     */
    public void setSrNo(int srNo) {
        this.srNo = srNo;
    }

}
