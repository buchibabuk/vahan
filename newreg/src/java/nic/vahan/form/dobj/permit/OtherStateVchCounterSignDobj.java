/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.util.Date;

/**
 *
 * @author acer
 */
public class OtherStateVchCounterSignDobj {

    private String state_cd;
    private int off_cd;
    private String state_cd_from;
    private String regn_no;
    private String pmt_no;
    private Date pmt_valid_upto;
    private int vacancy_no;
    private String pmtvaliduptostring;
    private int pur_cd;
    private String purpose;
    
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

    public String getState_cd_from() {
        return state_cd_from;
    }

    public void setState_cd_from(String state_cd_from) {
        this.state_cd_from = state_cd_from;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getPmt_no() {
        return pmt_no;
    }

    public void setPmt_no(String pmt_no) {
        this.pmt_no = pmt_no;
    }

    public Date getPmt_valid_upto() {
        return pmt_valid_upto;
    }

    public void setPmt_valid_upto(Date pmt_valid_upto) {
        this.pmt_valid_upto = pmt_valid_upto;
    }

    public int getVacancy_no() {
        return vacancy_no;
    }

    public void setVacancy_no(int vacancy_no) {
        this.vacancy_no = vacancy_no;
    }

    public String getPmtvaliduptostring() {
        return pmtvaliduptostring;
    }

    public void setPmtvaliduptostring(String pmtvaliduptostring) {
        this.pmtvaliduptostring = pmtvaliduptostring;
    }

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
