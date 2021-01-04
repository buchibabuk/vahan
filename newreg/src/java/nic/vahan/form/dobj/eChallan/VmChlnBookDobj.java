/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;

/**
 *
 * @author nic
 */
public class VmChlnBookDobj implements Cloneable, Serializable {

    private int usercode;
    private String book_no;
    private String user_name;
    private int chln_frm;
    private int chln_upto;
    private int curr_chln_no;
    private String expired;
    private String iss_dt;
    private String iss_by;
    private int off_cd;
    private String issue_date;

    public VmChlnBookDobj(int usercode, String book_no, int chln_frm, int chln_upto, int curr_chln_no, String expired, String iss_dt, String iss_by, int off_cd, String user_name) {

        this.usercode = usercode;
        this.book_no = book_no;
        this.chln_frm = chln_frm;
        this.chln_upto = chln_upto;
        this.curr_chln_no = curr_chln_no;
        this.expired = expired;
        this.iss_dt = iss_dt;
        this.iss_by = iss_by;
        this.off_cd = off_cd;
        this.user_name = user_name;
    }

    public VmChlnBookDobj() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public String getBook_no() {
        return book_no;
    }

    public void setBook_no(String book_no) {
        this.book_no = book_no;
    }

    public int getChln_frm() {
        return chln_frm;
    }

    public void setChln_frm(int chln_frm) {
        this.chln_frm = chln_frm;
    }

    public int getChln_upto() {
        return chln_upto;
    }

    public void setChln_upto(int chln_upto) {
        this.chln_upto = chln_upto;
    }

    public int getCurr_chln_no() {
        return curr_chln_no;
    }

    public void setCurr_chln_no(int curr_chln_no) {
        this.curr_chln_no = curr_chln_no;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public String getIss_by() {
        return iss_by;
    }

    public void setIss_by(String iss_by) {
        this.iss_by = iss_by;
    }

    public int getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    public String getIssue_date() {
        return issue_date;
    }

    public void setIssue_date(String issue_date) {
        this.issue_date = issue_date;
    }

    public String getIss_dt() {
        return iss_dt;
    }

    public void setIss_dt(String iss_dt) {
        this.iss_dt = iss_dt;
    }

    public int getUsercode() {
        return usercode;
    }

    public void setUsercode(int usercode) {
        this.usercode = usercode;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
