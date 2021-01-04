/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ASHOK
 */
public class InspectionDobj implements Cloneable, Serializable {

    private String appl_no;
    private String regn_no;
    private String remark;
    private Date op_dt;
    private String state_cd;
    private int off_cd;
    private Date insp_dt;
    private Date minDt = new Date();
    private Date maxDt = new Date();
    private int fit_off_cd1;
    private boolean disableInsDetail;

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
     * @return the insp_dt
     */
    public Date getInsp_dt() {
        return insp_dt;
    }

    /**
     * @param insp_dt the insp_dt to set
     */
    public void setInsp_dt(Date insp_dt) {
        this.insp_dt = insp_dt;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the minDt
     */
    public Date getMinDt() {
        return minDt;
    }

    /**
     * @param minDt the minDt to set
     */
    public void setMinDt(Date minDt) {
        this.minDt = minDt;
    }

    /**
     * @return the maxDt
     */
    public Date getMaxDt() {
        return maxDt;
    }

    /**
     * @param maxDt the maxDt to set
     */
    public void setMaxDt(Date maxDt) {
        this.maxDt = maxDt;
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

    public boolean isDisableInsDetail() {
        return disableInsDetail;
    }

    public void setDisableInsDetail(boolean disableInsDetail) {
        this.disableInsDetail = disableInsDetail;
    }
}
