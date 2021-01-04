/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author AMBRISH
 */
public class HpaDobj implements Cloneable, Serializable {

    private String appl_no;
    private String regn_no;
    private int sr_no;
    private String hp_type;
    private String hp_type_descr;
    private String fncr_name;
    private String fncr_add1;
    private String fncr_add2;
    private String fncr_add3;
    private String fncr_state;
    private String fncr_state_name;
    private int fncr_district;
    private String fncr_district_descr;// private String fncr_district_name;
    private Integer fncr_pincode;
    private Date from_dt = new Date();
    private String from_dt_descr;
    private Date upto_dt = new Date();
    private String upto_dt_descr;
    private Date term_dt;
    private String term_dt_descr;//this can be removed in future if it is not in use
    private Date op_dt;
    private int purCode;
    private String state_cd;
    private int off_cd;

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
     * @return the sr_no
     */
    public int getSr_no() {
        return sr_no;
    }

    /**
     * @param sr_no the sr_no to set
     */
    public void setSr_no(int sr_no) {
        this.sr_no = sr_no;
    }

    /**
     * @return the hp_type
     */
    public String getHp_type() {
        return hp_type;
    }

    /**
     * @param hp_type the hp_type to set
     */
    public void setHp_type(String hp_type) {
        this.hp_type = hp_type;
    }

    /**
     * @return the fncr_name
     */
    public String getFncr_name() {
        return fncr_name;
    }

    /**
     * @param fncr_name the fncr_name to set
     */
    public void setFncr_name(String fncr_name) {
        this.fncr_name = fncr_name;
    }

    /**
     * @return the fncr_add1
     */
    public String getFncr_add1() {
        return fncr_add1;
    }

    /**
     * @param fncr_add1 the fncr_add1 to set
     */
    public void setFncr_add1(String fncr_add1) {
        this.fncr_add1 = fncr_add1;
    }

    /**
     * @return the fncr_add2
     */
    public String getFncr_add2() {
        return fncr_add2;
    }

    /**
     * @param fncr_add2 the fncr_add2 to set
     */
    public void setFncr_add2(String fncr_add2) {
        this.fncr_add2 = fncr_add2;
    }

    /**
     * @return the fncr_district
     */
    public int getFncr_district() {
        return fncr_district;
    }

    /**
     * @param fncr_district the fncr_district to set
     */
    public void setFncr_district(int fncr_district) {
        this.fncr_district = fncr_district;
    }

    /**
     * @return the fncr_pincode
     */
    public Integer getFncr_pincode() {
        return fncr_pincode;
    }

    /**
     * @param fncr_pincode the fncr_pincode to set
     */
    public void setFncr_pincode(Integer fncr_pincode) {
        this.fncr_pincode = fncr_pincode;
    }

    /**
     * @return the from_dt
     */
    public Date getFrom_dt() {
        return from_dt;
    }

    /**
     * @param from_dt the from_dt to set
     */
    public void setFrom_dt(Date from_dt) {
        this.from_dt = from_dt;
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
     * @return the upto_dt
     */
    public Date getUpto_dt() {
        return upto_dt;
    }

    /**
     * @param upto_dt the upto_dt to set
     */
    public void setUpto_dt(Date upto_dt) {
        this.upto_dt = upto_dt;
    }

    /**
     * @return the term_dt
     */
    public Date getTerm_dt() {
        return term_dt;
    }

    /**
     * @param term_dt the term_dt to set
     */
    public void setTerm_dt(Date term_dt) {
        this.term_dt = term_dt;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the fncr_district_descr
     */
    public String getFncr_district_descr() {
        return fncr_district_descr;
    }

    /**
     * @param fncr_district_descr the fncr_district_descr to set
     */
    public void setFncr_district_descr(String fncr_district_descr) {
        this.fncr_district_descr = fncr_district_descr;
    }

    /**
     * @return the hp_type_descr
     */
    public String getHp_type_descr() {
        return hp_type_descr;
    }

    /**
     * @param hp_type_descr the hp_type_descr to set
     */
    public void setHp_type_descr(String hp_type_descr) {
        this.hp_type_descr = hp_type_descr;
    }

    /**
     * @return the from_dt_descr
     */
    public String getFrom_dt_descr() {
        return from_dt_descr;
    }

    /**
     * @param from_dt_descr the from_dt_descr to set
     */
    public void setFrom_dt_descr(String from_dt_descr) {
        this.from_dt_descr = from_dt_descr;
    }

    /**
     * @return the upto_dt_descr
     */
    public String getUpto_dt_descr() {
        return upto_dt_descr;
    }

    /**
     * @param upto_dt_descr the upto_dt_descr to set
     */
    public void setUpto_dt_descr(String upto_dt_descr) {
        this.upto_dt_descr = upto_dt_descr;
    }

    /**
     * @return the term_dt_descr
     */
    public String getTerm_dt_descr() {
        return term_dt_descr;
    }

    /**
     * @param term_dt_descr the term_dt_descr to set
     */
    public void setTerm_dt_descr(String term_dt_descr) {
        this.term_dt_descr = term_dt_descr;
    }

    /**
     * @return the fncr_add3
     */
    public String getFncr_add3() {
        return fncr_add3;
    }

    /**
     * @param fncr_add3 the fncr_add3 to set
     */
    public void setFncr_add3(String fncr_add3) {
        this.fncr_add3 = fncr_add3;
    }

    /**
     * @return the fncr_state
     */
    public String getFncr_state() {
        return fncr_state;
    }

    /**
     * @param fncr_state the fncr_state to set
     */
    public void setFncr_state(String fncr_state) {
        this.fncr_state = fncr_state;
    }

    /**
     * @return the fncr_state_name
     */
    public String getFncr_state_name() {
        return fncr_state_name;
    }

    /**
     * @param fncr_state_name the fncr_state_name to set
     */
    public void setFncr_state_name(String fncr_state_name) {
        this.fncr_state_name = fncr_state_name;
    }

    public int getPurCode() {
        return purCode;
    }

    public void setPurCode(int purCode) {
        this.purCode = purCode;
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
}
