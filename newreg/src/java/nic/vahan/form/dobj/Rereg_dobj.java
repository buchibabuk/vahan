/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

public class Rereg_dobj implements Cloneable, Serializable {

    private String appl_no;
    private String old_regn_no;
    private String new_regn_no;
    private String reason;
    private Date op_dt;
    private String state_cd;
    private int off_cd;
    private boolean advRegnNo;
    private boolean retRegnNo;
    private int pmt_type = -1;
    private int pmt_catg = -1;
    private boolean oddEvenOpted;

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getOld_regn_no() {
        return old_regn_no;
    }

    public void setOld_regn_no(String old_regn_no) {
        this.old_regn_no = old_regn_no;
    }

    public String getNew_regn_no() {
        return new_regn_no;
    }

    public void setNew_regn_no(String new_regn_no) {
        this.new_regn_no = new_regn_no;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getOp_dt() {
        return op_dt;
    }

    public void setOp_dt(Date op_dt) {
        this.op_dt = op_dt;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the retRegnNo
     */
    public boolean isRetRegnNo() {
        return retRegnNo;
    }

    /**
     * @param retRegnNo the retRegnNo to set
     */
    public void setRetRegnNo(boolean retRegnNo) {
        this.retRegnNo = retRegnNo;
    }

    /**
     * @return the advRegnNo
     */
    public boolean isAdvRegnNo() {
        return advRegnNo;
    }

    /**
     * @param advRegnNo the advRegnNo to set
     */
    public void setAdvRegnNo(boolean advRegnNo) {
        this.advRegnNo = advRegnNo;
    }

    /**
     * @return the pmt_type
     */
    public int getPmt_type() {
        return pmt_type;
    }

    /**
     * @param pmt_type the pmt_type to set
     */
    public void setPmt_type(int pmt_type) {
        this.pmt_type = pmt_type;
    }

    /**
     * @return the pmt_catg
     */
    public int getPmt_catg() {
        return pmt_catg;
    }

    /**
     * @param pmt_catg the pmt_catg to set
     */
    public void setPmt_catg(int pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    /**
     * @return the oddEvenOpted
     */
    public boolean isOddEvenOpted() {
        return oddEvenOpted;
    }

    /**
     * @param oddEvenOpted the oddEvenOpted to set
     */
    public void setOddEvenOpted(boolean oddEvenOpted) {
        this.oddEvenOpted = oddEvenOpted;
    }
}
