/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

public class CaDobj implements Cloneable, Serializable {

    private String state_cd;
    private int off_cd;
    private String appl_no;
    private String regn_no;
    private Date from_dt;
    private String c_add1;
    private String c_add2;
    private String c_add3;
    private String c_state;
    private String c_state_name;
    private int c_district;
    private String c_district_name;
    private Integer c_pincode;
    private String p_add1;
    private String p_add2;
    private String p_add3;
    private int p_district;
    private String p_district_name;
    private String p_state;
    private String p_state_name;
    private Integer p_pincode;

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
     * @return the c_add1
     */
    public String getC_add1() {
        return c_add1;
    }

    /**
     * @param c_add1 the c_add1 to set
     */
    public void setC_add1(String c_add1) {
        this.c_add1 = c_add1;
    }

    /**
     * @return the c_add2
     */
    public String getC_add2() {
        return c_add2;
    }

    /**
     * @param c_add2 the c_add2 to set
     */
    public void setC_add2(String c_add2) {
        this.c_add2 = c_add2;
    }

    /**
     * @return the c_add3
     */
    public String getC_add3() {
        return c_add3;
    }

    /**
     * @param c_add3 the c_add3 to set
     */
    public void setC_add3(String c_add3) {
        this.c_add3 = c_add3;
    }

    /**
     * @return the c_state
     */
    public String getC_state() {
        return c_state;
    }

    /**
     * @param c_state the c_state to set
     */
    public void setC_state(String c_state) {
        this.c_state = c_state;
    }

    /**
     * @return the c_district
     */
    public int getC_district() {
        return c_district;
    }

    /**
     * @param c_district the c_district to set
     */
    public void setC_district(int c_district) {
        this.c_district = c_district;
    }

    /**
     * @return the c_pincode
     */
    public Integer getC_pincode() {
        return c_pincode;
    }

    /**
     * @param c_pincode the c_pincode to set
     */
    public void setC_pincode(Integer c_pincode) {
        this.c_pincode = c_pincode;
    }

    /**
     * @return the p_add1
     */
    public String getP_add1() {
        return p_add1;
    }

    /**
     * @param p_add1 the p_add1 to set
     */
    public void setP_add1(String p_add1) {
        this.p_add1 = p_add1;
    }

    /**
     * @return the p_add2
     */
    public String getP_add2() {
        return p_add2;
    }

    /**
     * @param p_add2 the p_add2 to set
     */
    public void setP_add2(String p_add2) {
        this.p_add2 = p_add2;
    }

    /**
     * @return the p_add3
     */
    public String getP_add3() {
        return p_add3;
    }

    /**
     * @param p_add3 the p_add3 to set
     */
    public void setP_add3(String p_add3) {
        this.p_add3 = p_add3;
    }

    /**
     * @return the p_district
     */
    public int getP_district() {
        return p_district;
    }

    /**
     * @param p_district the p_district to set
     */
    public void setP_district(int p_district) {
        this.p_district = p_district;
    }

    /**
     * @return the p_state
     */
    public String getP_state() {
        return p_state;
    }

    /**
     * @param p_state the p_state to set
     */
    public void setP_state(String p_state) {
        this.p_state = p_state;
    }

    /**
     * @return the p_pincode
     */
    public Integer getP_pincode() {
        return p_pincode;
    }

    /**
     * @param p_pincode the p_pincode to set
     */
    public void setP_pincode(Integer p_pincode) {
        this.p_pincode = p_pincode;
    }

    /**
     * @return the c_state_name
     */
    public String getC_state_name() {
        return c_state_name;
    }

    /**
     * @param c_state_name the c_state_name to set
     */
    public void setC_state_name(String c_state_name) {
        this.c_state_name = c_state_name;
    }

    /**
     * @return the c_district_name
     */
    public String getC_district_name() {
        return c_district_name;
    }

    /**
     * @param c_district_name the c_district_name to set
     */
    public void setC_district_name(String c_district_name) {
        this.c_district_name = c_district_name;
    }

    /**
     * @return the p_district_name
     */
    public String getP_district_name() {
        return p_district_name;
    }

    /**
     * @param p_district_name the p_district_name to set
     */
    public void setP_district_name(String p_district_name) {
        this.p_district_name = p_district_name;
    }

    /**
     * @return the p_state_name
     */
    public String getP_state_name() {
        return p_state_name;
    }

    /**
     * @param p_state_name the p_state_name to set
     */
    public void setP_state_name(String p_state_name) {
        this.p_state_name = p_state_name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
