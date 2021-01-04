/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.tax;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author ASHOK
 */
public class TaxDobj implements Serializable {

    private String regn_no;
    private String tax_mode;
    private String payment_mode;
    private int tax_amt;
    private int tax_fine;
    private String rcpt_no;
    private Timestamp rcpt_dt;
    private Date tax_from;
    private Date tax_upto;
    private int pur_cd;
    private String flag;
    private String collected_by;
    private String state_cd;
    private int off_cd;

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
     * @return the tax_mode
     */
    public String getTax_mode() {
        return tax_mode;
    }

    /**
     * @param tax_mode the tax_mode to set
     */
    public void setTax_mode(String tax_mode) {
        this.tax_mode = tax_mode;
    }

    /**
     * @return the payment_mode
     */
    public String getPayment_mode() {
        return payment_mode;
    }

    /**
     * @param payment_mode the payment_mode to set
     */
    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    /**
     * @return the tax_amt
     */
    public int getTax_amt() {
        return tax_amt;
    }

    /**
     * @param tax_amt the tax_amt to set
     */
    public void setTax_amt(int tax_amt) {
        this.tax_amt = tax_amt;
    }

    /**
     * @return the tax_fine
     */
    public int getTax_fine() {
        return tax_fine;
    }

    /**
     * @param tax_fine the tax_fine to set
     */
    public void setTax_fine(int tax_fine) {
        this.tax_fine = tax_fine;
    }

    /**
     * @return the rcpt_no
     */
    public String getRcpt_no() {
        return rcpt_no;
    }

    /**
     * @param rcpt_no the rcpt_no to set
     */
    public void setRcpt_no(String rcpt_no) {
        this.rcpt_no = rcpt_no;
    }

    /**
     * @return the rcpt_dt
     */
    public Timestamp getRcpt_dt() {
        return rcpt_dt;
    }

    /**
     * @param rcpt_dt the rcpt_dt to set
     */
    public void setRcpt_dt(Timestamp rcpt_dt) {
        this.rcpt_dt = rcpt_dt;
    }

    /**
     * @return the tax_from
     */
    public Date getTax_from() {
        return tax_from;
    }

    /**
     * @param tax_from the tax_from to set
     */
    public void setTax_from(Date tax_from) {
        this.tax_from = tax_from;
    }

    /**
     * @return the tax_upto
     */
    public Date getTax_upto() {
        return tax_upto;
    }

    /**
     * @param tax_upto the tax_upto to set
     */
    public void setTax_upto(Date tax_upto) {
        this.tax_upto = tax_upto;
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
     * @return the flag
     */
    public String getFlag() {
        return flag;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(String flag) {
        this.flag = flag;
    }

    /**
     * @return the collected_by
     */
    public String getCollected_by() {
        return collected_by;
    }

    /**
     * @param collected_by the collected_by to set
     */
    public void setCollected_by(String collected_by) {
        this.collected_by = collected_by;
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
