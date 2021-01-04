/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author tranC094
 */
public class FitnessDobj implements Cloneable, Serializable {

    private String appl_no;
    private String regn_no;
    private String chasi_no;
    private Date fit_chk_dt;
    private String fit_chk_dt_descr;
    private String fit_chk_tm;
    private String fit_result;
    private Date fit_valid_to;
    private String fit_valid_to_descr;
    private int fit_off_cd1;
    private int fit_off_cd2;
    private String remark;
    private String pucc_no;
    private Date pucc_val;
    private String fare_mtr_no;
    private Date op_dt;
    private String op_dt_descr;
    private String state_cd;
    private int off_cd;
    private Date fit_nid;
    private String fit_nid_descr;
    private ArrayList list_parameters;
    private String fit_officer_name1;
    private String fit_officer_name2;
    private Date movedOn;
    private String state_name;
    private String off_name;

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
     * @return the chasi_no
     */
    public String getChasi_no() {
        return chasi_no;
    }

    /**
     * @param chasi_no the chasi_no to set
     */
    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
    }

    /**
     * @return the fit_chk_dt
     */
    public Date getFit_chk_dt() {
        return fit_chk_dt;
    }

    /**
     * @param fit_chk_dt the fit_chk_dt to set
     */
    public void setFit_chk_dt(Date fit_chk_dt) {
        this.fit_chk_dt = fit_chk_dt;
    }

    /**
     * @return the fit_result
     */
    public String getFit_result() {
        return fit_result;
    }

    /**
     * @param fit_result the fit_result to set
     */
    public void setFit_result(String fit_result) {
        this.fit_result = fit_result;
    }

    /**
     * @return the fit_valid_to
     */
    public Date getFit_valid_to() {
        return fit_valid_to;
    }

    /**
     * @param fit_valid_to the fit_valid_to to set
     */
    public void setFit_valid_to(Date fit_valid_to) {
        this.fit_valid_to = fit_valid_to;
    }

    /**
     * @return the fit_off_cd1
     */
    public int getFit_off_cd1() {
        return fit_off_cd1;
    }

    /**
     * @param fit_off_cd1 the fit_off_cd1 to set
     */
    public void setFit_off_cd1(int fit_off_cd1) {
        this.fit_off_cd1 = fit_off_cd1;
    }

    /**
     * @return the fit_off_cd2
     */
    public int getFit_off_cd2() {
        return fit_off_cd2;
    }

    /**
     * @param fit_off_cd2 the fit_off_cd2 to set
     */
    public void setFit_off_cd2(int fit_off_cd2) {
        this.fit_off_cd2 = fit_off_cd2;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the pucc_no
     */
    public String getPucc_no() {
        return pucc_no;
    }

    /**
     * @param pucc_no the pucc_no to set
     */
    public void setPucc_no(String pucc_no) {
        this.pucc_no = pucc_no;
    }

    /**
     * @return the pucc_val
     */
    public Date getPucc_val() {
        return pucc_val;
    }

    /**
     * @param pucc_val the pucc_val to set
     */
    public void setPucc_val(Date pucc_val) {
        this.pucc_val = pucc_val;
    }

    /**
     * @return the fare_mtr_no
     */
    public String getFare_mtr_no() {
        return fare_mtr_no;
    }

    /**
     * @param fare_mtr_no the fare_mtr_no to set
     */
    public void setFare_mtr_no(String fare_mtr_no) {
        this.fare_mtr_no = fare_mtr_no;
    }

    /**
     * @return the op_dt
     */
    public Date getOp_dt() {
        return op_dt;
    }

    /**
     * @param op_dt the op_dt to set
     */
    public void setOp_dt(Date op_dt) {
        this.op_dt = op_dt;
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
     * @return the fit_chk_tm
     */
    public String getFit_chk_tm() {
        return fit_chk_tm;
    }

    /**
     * @param fit_chk_tm the fit_chk_tm to set
     */
    public void setFit_chk_tm(String fit_chk_tm) {
        this.fit_chk_tm = fit_chk_tm;
    }

    /**
     * @return the list_parameters
     */
    public ArrayList getList_parameters() {
        return list_parameters;
    }

    /**
     * @param list_parameters the list_parameters to set
     */
    public void setList_parameters(ArrayList list_parameters) {
        this.list_parameters = list_parameters;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the fit_chk_dt_descr
     */
    public String getFit_chk_dt_descr() {
        return fit_chk_dt_descr;
    }

    /**
     * @param fit_chk_dt_descr the fit_chk_dt_descr to set
     */
    public void setFit_chk_dt_descr(String fit_chk_dt_descr) {
        this.fit_chk_dt_descr = fit_chk_dt_descr;
    }

    /**
     * @return the fit_valid_to_descr
     */
    public String getFit_valid_to_descr() {
        return fit_valid_to_descr;
    }

    /**
     * @param fit_valid_to_descr the fit_valid_to_descr to set
     */
    public void setFit_valid_to_descr(String fit_valid_to_descr) {
        this.fit_valid_to_descr = fit_valid_to_descr;
    }

    /**
     * @return the fit_nid
     */
    public Date getFit_nid() {
        return fit_nid;
    }

    /**
     * @param fit_nid the fit_nid to set
     */
    public void setFit_nid(Date fit_nid) {
        this.fit_nid = fit_nid;
    }

    /**
     * @return the fit_nid_descr
     */
    public String getFit_nid_descr() {
        return fit_nid_descr;
    }

    /**
     * @param fit_nid_descr the fit_nid_descr to set
     */
    public void setFit_nid_descr(String fit_nid_descr) {
        this.fit_nid_descr = fit_nid_descr;
    }

    /**
     * @return the fit_officer_name1
     */
    public String getFit_officer_name1() {
        return fit_officer_name1;
    }

    /**
     * @param fit_officer_name1 the fit_officer_name1 to set
     */
    public void setFit_officer_name1(String fit_officer_name1) {
        this.fit_officer_name1 = fit_officer_name1;
    }

    /**
     * @return the fit_officer_name2
     */
    public String getFit_officer_name2() {
        return fit_officer_name2;
    }

    /**
     * @param fit_officer_name2 the fit_officer_name2 to set
     */
    public void setFit_officer_name2(String fit_officer_name2) {
        this.fit_officer_name2 = fit_officer_name2;
    }

    /**
     * @return the movedOn
     */
    public Date getMovedOn() {
        return movedOn;
    }

    /**
     * @param movedOn the movedOn to set
     */
    public void setMovedOn(Date movedOn) {
        this.movedOn = movedOn;
    }

    /**
     * @return the state_name
     */
    public String getState_name() {
        return state_name;
    }

    /**
     * @param state_name the state_name to set
     */
    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    /**
     * @return the off_name
     */
    public String getOff_name() {
        return off_name;
    }

    /**
     * @param off_name the off_name to set
     */
    public void setOff_name(String off_name) {
        this.off_name = off_name;
    }

    /**
     * @return the op_dt_descr
     */
    public String getOp_dt_descr() {
        return op_dt_descr;
    }

    /**
     * @param op_dt_descr the op_dt_descr to set
     */
    public void setOp_dt_descr(String op_dt_descr) {
        this.op_dt_descr = op_dt_descr;
    }
}
