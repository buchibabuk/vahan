/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.util.Date;

/**
 *
 * @author nicsi
 */
public class CourtCasesDobj {

    private String appl_no;
    private int court_cd;
    private Date hearing_date;
    private String owner_name;
    private String court_name;
    private String regn_no;

    public CourtCasesDobj(String appl_no, int court_cd, Date hearing_date, String owner_name, String court_name, String regn_no) {
        this.appl_no = appl_no;
        this.court_cd = court_cd;
        this.hearing_date = hearing_date;
        this.owner_name = owner_name;
        this.court_name = court_name;
        this.regn_no = regn_no;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public int getCourt_cd() {
        return court_cd;
    }

    public void setCourt_cd(int court_cd) {
        this.court_cd = court_cd;
    }

    public Date getHearing_date() {
        return hearing_date;
    }

    public void setHearing_date(Date hearing_date) {
        this.hearing_date = hearing_date;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getCourt_name() {
        return court_name;
    }

    public void setCourt_name(String court_name) {
        this.court_name = court_name;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }
}
