/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.fancy;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author nic
 */
public class RetenRegnNo_dobj implements Serializable, Cloneable {

    private String state_cd;
    private int off_cd;
    private String off_desc;
    private String recp_no;
    private String regn_appl_no;
    private String regn_no;
    private String owner_name;
    private String f_name;
    private String c_add1;
    private String c_add2;
    private String c_add3;
    private int c_district;
    private String chasisNo;
    private Integer c_pincode;
    private String c_state;
    private Long mobile_no;
    private long total_amt;
    private String reason;
    private String appl_no;
    private Date appl_date;
    ///fancy///////
    private boolean assignFancyNumber;
    private String assignFancyRegnNumber;

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
     * @return the off_desc
     */
    public String getOff_desc() {
        return off_desc;
    }

    /**
     * @param off_desc the off_desc to set
     */
    public void setOff_desc(String off_desc) {
        this.off_desc = off_desc;
    }

    /**
     * @return the recp_no
     */
    public String getRecp_no() {
        return recp_no;
    }

    /**
     * @param recp_no the recp_no to set
     */
    public void setRecp_no(String recp_no) {
        this.recp_no = recp_no;
    }

    /**
     * @return the regn_appl_no
     */
    public String getRegn_appl_no() {
        return regn_appl_no;
    }

    /**
     * @param regn_appl_no the regn_appl_no to set
     */
    public void setRegn_appl_no(String regn_appl_no) {
        this.regn_appl_no = regn_appl_no;
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
     * @return the owner_name
     */
    public String getOwner_name() {
        return owner_name;
    }

    /**
     * @param owner_name the owner_name to set
     */
    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    /**
     * @return the f_name
     */
    public String getF_name() {
        return f_name;
    }

    /**
     * @param f_name the f_name to set
     */
    public void setF_name(String f_name) {
        this.f_name = f_name;
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
     * @return the chasisNo
     */
    public String getChasisNo() {
        return chasisNo;
    }

    /**
     * @param chasisNo the chasisNo to set
     */
    public void setChasisNo(String chasisNo) {
        this.chasisNo = chasisNo;
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
     * @return the mobile_no
     */
    public Long getMobile_no() {
        return mobile_no;
    }

    /**
     * @param mobile_no the mobile_no to set
     */
    public void setMobile_no(Long mobile_no) {
        this.mobile_no = mobile_no;
    }

    /**
     * @return the total_amt
     */
    public long getTotal_amt() {
        return total_amt;
    }

    /**
     * @param total_amt the total_amt to set
     */
    public void setTotal_amt(long total_amt) {
        this.total_amt = total_amt;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

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
     * @return the appl_date
     */
    public Date getAppl_date() {
        return appl_date;
    }

    /**
     * @param appl_date the appl_date to set
     */
    public void setAppl_date(Date appl_date) {
        this.appl_date = appl_date;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean isAssignFancyNumber() {
        return assignFancyNumber;
    }

    public void setAssignFancyNumber(boolean assignFancyNumber) {
        this.assignFancyNumber = assignFancyNumber;
    }

    public String getAssignFancyRegnNumber() {
        return assignFancyRegnNumber;
    }

    public void setAssignFancyRegnNumber(String assignFancyRegnNumber) {
        this.assignFancyRegnNumber = assignFancyRegnNumber;
    }
}