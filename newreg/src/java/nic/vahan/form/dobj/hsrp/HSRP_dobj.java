/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.hsrp;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class HSRP_dobj implements Serializable {

    private String state_cd;
    private int off_cd;
    private String appl_no;
    private String regn_no;
    private String hsrp_flag;
    private String emp_cd;
    private Date op_dt;
    private String hsrp_no_front;
    private String hsrp_no_back;
    private String hsrp_fix_dt;
    private float hsrp_fix_amt;
    private String hsrp_amt_taken_on;
    private Date hsrp_op_dt;
    private boolean hsrp;
    private String hsrpReason;
    private Date hsrp_fixed_dt;
    private Date hsrp_amt_taken_on_dt;

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
     * @return the hsrp_flag
     */
    public String getHsrp_flag() {
        return hsrp_flag;
    }

    /**
     * @param hsrp_flag the hsrp_flag to set
     */
    public void setHsrp_flag(String hsrp_flag) {
        this.hsrp_flag = hsrp_flag;
    }

    /**
     * @return the emp_cd
     */
    public String getEmp_cd() {
        return emp_cd;
    }

    /**
     * @param emp_cd the emp_cd to set
     */
    public void setEmp_cd(String emp_cd) {
        this.emp_cd = emp_cd;
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
     * @return the hsrp_no_front
     */
    public String getHsrp_no_front() {
        return hsrp_no_front;
    }

    /**
     * @param hsrp_no_front the hsrp_no_front to set
     */
    public void setHsrp_no_front(String hsrp_no_front) {
        this.hsrp_no_front = hsrp_no_front;
    }

    /**
     * @return the hsrp_no_back
     */
    public String getHsrp_no_back() {
        return hsrp_no_back;
    }

    /**
     * @param hsrp_no_back the hsrp_no_back to set
     */
    public void setHsrp_no_back(String hsrp_no_back) {
        this.hsrp_no_back = hsrp_no_back;
    }

    /**
     * @return the hsrp_fix_dt
     */
    public String getHsrp_fix_dt() {
        return hsrp_fix_dt;
    }

    /**
     * @param hsrp_fix_dt the hsrp_fix_dt to set
     */
    public void setHsrp_fix_dt(String hsrp_fix_dt) {
        this.hsrp_fix_dt = hsrp_fix_dt;
    }

    /**
     * @return the hsrp_fix_amt
     */
    public float getHsrp_fix_amt() {
        return hsrp_fix_amt;
    }

    /**
     * @param hsrp_fix_amt the hsrp_fix_amt to set
     */
    public void setHsrp_fix_amt(float hsrp_fix_amt) {
        this.hsrp_fix_amt = hsrp_fix_amt;
    }

    /**
     * @return the hsrp_amt_taken_on
     */
    public String getHsrp_amt_taken_on() {
        return hsrp_amt_taken_on;
    }

    /**
     * @param hsrp_amt_taken_on the hsrp_amt_taken_on to set
     */
    public void setHsrp_amt_taken_on(String hsrp_amt_taken_on) {
        this.hsrp_amt_taken_on = hsrp_amt_taken_on;
    }

    /**
     * @return the hsrp_op_dt
     */
    public Date getHsrp_op_dt() {
        return hsrp_op_dt;
    }

    /**
     * @param hsrp_op_dt the hsrp_op_dt to set
     */
    public void setHsrp_op_dt(Date hsrp_op_dt) {
        this.hsrp_op_dt = hsrp_op_dt;
    }

    /**
     * @return the hsrp
     */
    public boolean isHsrp() {
        return hsrp;
    }

    /**
     * @param hsrp the hsrp to set
     */
    public void setHsrp(boolean hsrp) {
        this.hsrp = hsrp;
    }

    /**
     * @return the hsrpReason
     */
    public String getHsrpReason() {
        return hsrpReason;
    }

    /**
     * @param hsrpReason the hsrpReason to set
     */
    public void setHsrpReason(String hsrpReason) {
        this.hsrpReason = hsrpReason;
    }

    /**
     * @return the hsrp_fixed_dt
     */
    public Date getHsrp_fixed_dt() {
        return hsrp_fixed_dt;
    }

    /**
     * @param hsrp_fixed_dt the hsrp_fixed_dt to set
     */
    public void setHsrp_fixed_dt(Date hsrp_fixed_dt) {
        this.hsrp_fixed_dt = hsrp_fixed_dt;
    }

    /**
     * @return the hsrp_amt_taken_on_dt
     */
    public Date getHsrp_amt_taken_on_dt() {
        return hsrp_amt_taken_on_dt;
    }

    /**
     * @param hsrp_amt_taken_on_dt the hsrp_amt_taken_on_dt to set
     */
    public void setHsrp_amt_taken_on_dt(Date hsrp_amt_taken_on_dt) {
        this.hsrp_amt_taken_on_dt = hsrp_amt_taken_on_dt;
    }
}
