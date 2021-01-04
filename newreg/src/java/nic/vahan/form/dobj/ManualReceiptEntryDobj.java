/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author DELL
 */
public class ManualReceiptEntryDobj implements Cloneable, Serializable {

    private String trans_appl_no;
    private String applno;
    private String rcptNo;
    //private String amount;
    private long amount;
    private String state_cd;
    private int off_cd;
    private boolean rcpt_used;
    private int pur_cd;
    private String entered_on;
    private Date receipt_dt;
    private String rcpt_dt;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the trans_appl_no
     */
    public String getTrans_appl_no() {
        return trans_appl_no;
    }

    /**
     * @param trans_appl_no the trans_appl_no to set
     */
    public void setTrans_appl_no(String trans_appl_no) {
        this.trans_appl_no = trans_appl_no;
    }

    /**
     * @return the applno
     */
    public String getApplno() {
        return applno;
    }

    /**
     * @param applno the applno to set
     */
    public void setApplno(String applno) {
        this.applno = applno;
    }

    /**
     * @return the rcptNo
     */
    public String getRcptNo() {
        return rcptNo;
    }

    /**
     * @param rcptNo the rcptNo to set
     */
    public void setRcptNo(String rcptNo) {
        this.rcptNo = rcptNo;
    }

    /**
     * @return the rcpt_used
     */
    public boolean isRcpt_used() {
        return rcpt_used;
    }

    /**
     * @param rcpt_used the rcpt_used to set
     */
    public void setRcpt_used(boolean rcpt_used) {
        this.rcpt_used = rcpt_used;
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
     * @return the pur_cd
     */
    public int getPur_cd() {
        return pur_cd;
    }

    /**
     * @param pur_cd the pur_cd to set
     */
    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    /**
     * @return the amount
     */
    public long getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(long amount) {
        this.amount = amount;
    }

    /**
     * @return the entered_on
     */
    public String getEntered_on() {
        return entered_on;
    }

    /**
     * @param entered_on the entered_on to set
     */
    public void setEntered_on(String entered_on) {
        this.entered_on = entered_on;
    }

    /**
     * @return the receipt_dt
     */
    public Date getReceipt_dt() {
        return receipt_dt;
    }

    /**
     * @param receipt_dt the receipt_dt to set
     */
    public void setReceipt_dt(Date receipt_dt) {
        this.receipt_dt = receipt_dt;
    }

    /**
     * @return the rcpt_dt
     */
    public String getRcpt_dt() {
        return rcpt_dt;
    }

    /**
     * @param rcpt_dt the rcpt_dt to set
     */
    public void setRcpt_dt(String rcpt_dt) {
        this.rcpt_dt = rcpt_dt;
    }
}
