/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;

/**
 *
 * @author hcl
 */
public class PreviousTempPermitDtlsDobj implements Serializable {

    private String route_fr;
    private String route_to;
    private String valid_from;
    private String valid_upto;
    private int days;
    private String issue_dt;
    private String pmt_type;
    private String pmt_catg;
    private String pmt_no;
    private String appl_no;

    public PreviousTempPermitDtlsDobj(String route_fr, String route_to, String valid_from, String valid_upto, int days, String issue_dt, String pmt_type, String pmt_catg, String pmt_no, String appl_no) {
        this.route_fr = route_fr;
        this.route_to = route_to;
        this.valid_from = valid_from;
        this.valid_upto = valid_upto;
        this.days = days;
        this.issue_dt = issue_dt;
        this.pmt_type = pmt_type;
        this.pmt_catg = pmt_catg;
        this.pmt_no = pmt_no;
        this.appl_no = appl_no;
    }

    public String getRoute_fr() {
        return route_fr;
    }

    public void setRoute_fr(String route_fr) {
        this.route_fr = route_fr;
    }

    public String getRoute_to() {
        return route_to;
    }

    public void setRoute_to(String route_to) {
        this.route_to = route_to;
    }

    public String getValid_from() {
        return valid_from;
    }

    public void setValid_from(String valid_from) {
        this.valid_from = valid_from;
    }

    public String getValid_upto() {
        return valid_upto;
    }

    public void setValid_upto(String valid_upto) {
        this.valid_upto = valid_upto;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getIssue_dt() {
        return issue_dt;
    }

    public void setIssue_dt(String issue_dt) {
        this.issue_dt = issue_dt;
    }

    public String getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(String pmt_type) {
        this.pmt_type = pmt_type;
    }

    public String getPmt_catg() {
        return pmt_catg;
    }

    public void setPmt_catg(String pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    public String getPmt_no() {
        return pmt_no;
    }

    public void setPmt_no(String pmt_no) {
        this.pmt_no = pmt_no;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }
}