/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

public class NdcDobj implements Serializable, Cloneable {

    private String regn_no;
    private String dl_no;
    private String badge_no;
    private String state_to;
    private int off_to;
    private String ncrb_no;
    private Date ndc_dt = new Date();
    private String ndc_no;
    private String pmt_no;
    private String appl_no;
    private String state_cd;
    private int off_cd;
    private int pur_cd;
    private String remark;
    private Date min_dt = new Date();

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getDl_no() {
        return dl_no;
    }

    public void setDl_no(String dl_no) {
        this.dl_no = dl_no;
    }

    public String getBadge_no() {
        return badge_no;
    }

    public void setBadge_no(String badge_no) {
        this.badge_no = badge_no;
    }

    public String getState_to() {
        return state_to;
    }

    public void setState_to(String state_to) {
        this.state_to = state_to;
    }

    public int getOff_to() {
        return off_to;
    }

    public void setOff_to(int off_to) {
        this.off_to = off_to;
    }

    public String getNcrb_no() {
        return ncrb_no;
    }

    public void setNcrb_no(String ncrb_no) {
        this.ncrb_no = ncrb_no;
    }

    public Date getNdc_dt() {
        return ndc_dt;
    }

    public void setNdc_dt(Date ndc_dt) {
        this.ndc_dt = ndc_dt;
    }

    public String getNdc_no() {
        return ndc_no;
    }

    public void setNdc_no(String ndc_no) {
        this.ndc_no = ndc_no;
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

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getMin_dt() {
        return min_dt;
    }

    public void setMin_dt(Date min_dt) {
        this.min_dt = min_dt;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
