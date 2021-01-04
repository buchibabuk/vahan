/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author tranC095
 */
public class DupDobj implements Cloneable, Serializable {

    private String appl_no;
    private int pur_cd;
    private String regn_no;
    private String reason;
    private String fir_no;
    private Date fir_dt;
    private String police_station;
    private Date op_dt;
    private String state_cd;
    private String pmtDoc;
    private int off_cd;

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getFir_no() {
        return fir_no;
    }

    public void setFir_no(String fir_no) {
        this.fir_no = fir_no;
    }

    public Date getFir_dt() {
        return fir_dt;
    }

    public void setFir_dt(Date fir_dt) {
        this.fir_dt = fir_dt;
    }

    public String getPolice_station() {
        return police_station;
    }

    public void setPolice_station(String police_station) {
        this.police_station = police_station;
    }

    public Date getOp_dt() {
        return op_dt;
    }

    public void setOp_dt(Date op_dt) {
        this.op_dt = op_dt;
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

    public String getPmtDoc() {
        return pmtDoc;
    }

    public void setPmtDoc(String pmtDoc) {
        this.pmtDoc = pmtDoc;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
