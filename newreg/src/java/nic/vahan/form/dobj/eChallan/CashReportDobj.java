/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class CashReportDobj implements Serializable {

    private String cmpd_rcpt_no;
    private String chal_date;
    private String cmpd_rcpt_date;
    private String Owner_name;
    private String regn_no;
    private String chal_no;
    private int cmpd_amt;
    private int tax;
    private int tax_penalty;
    private int grand_total_tax;
    private int grand_total;
    private String coming_from;
    private String going_to;
    private String off_desc;

    public String getComing_from() {
        return coming_from;
    }

    public void setComing_from(String coming_from) {
        this.coming_from = coming_from;
    }

    public String getGoing_to() {
        return going_to;
    }

    public void setGoing_to(String going_to) {
        this.going_to = going_to;
    }

    /**
     * @return the cmpd_rcpt_no
     */
    public String getCmpd_rcpt_no() {
        return cmpd_rcpt_no;
    }

    /**
     * @param cmpd_rcpt_no the cmpd_rcpt_no to set
     */
    public void setCmpd_rcpt_no(String cmpd_rcpt_no) {
        this.cmpd_rcpt_no = cmpd_rcpt_no;
    }

    /**
     * @return the chal_date
     */
    public String getChal_date() {
        return chal_date;
    }

    /**
     * @param chal_date the chal_date to set
     */
    public void setChal_date(String chal_date) {
        this.chal_date = chal_date;
    }

    /**
     * @return the cmpd_rcpt_date
     */
    public String getCmpd_rcpt_date() {
        return cmpd_rcpt_date;
    }

    /**
     * @param cmpd_rcpt_date the cmpd_rcpt_date to set
     */
    public void setCmpd_rcpt_date(String cmpd_rcpt_date) {
        this.cmpd_rcpt_date = cmpd_rcpt_date;
    }

    /**
     * @return the Owner_name
     */
    public String getOwner_name() {
        return Owner_name;
    }

    /**
     * @param Owner_name the Owner_name to set
     */
    public void setOwner_name(String Owner_name) {
        this.Owner_name = Owner_name;
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
     * @return the chal_no
     */
    public String getChal_no() {
        return chal_no;
    }

    /**
     * @param chal_no the chal_no to set
     */
    public void setChal_no(String chal_no) {
        this.chal_no = chal_no;
    }

    /**
     * @return the cmpd_amt
     */
    public int getCmpd_amt() {
        return cmpd_amt;
    }

    /**
     * @param cmpd_amt the cmpd_amt to set
     */
    public void setCmpd_amt(int cmpd_amt) {
        this.cmpd_amt = cmpd_amt;
    }

    /**
     * @return the tax
     */
    public int getTax() {
        return tax;
    }

    /**
     * @param tax the tax to set
     */
    public void setTax(int tax) {
        this.tax = tax;
    }

    /**
     * @return the tax_penalty
     */
    public int getTax_penalty() {
        return tax_penalty;
    }

    /**
     * @param tax_penalty the tax_penalty to set
     */
    public void setTax_penalty(int tax_penalty) {
        this.tax_penalty = tax_penalty;
    }

    /**
     * @return the grand_total_tax
     */
    public int getGrand_total_tax() {
        return grand_total_tax;
    }

    /**
     * @param grand_total_tax the grand_total_tax to set
     */
    public void setGrand_total_tax(int grand_total_tax) {
        this.grand_total_tax = grand_total_tax;
    }

    /**
     * @return the grand_total
     */
    public int getGrand_total() {
        return grand_total;
    }

    /**
     * @param grand_total the grand_total to set
     */
    public void setGrand_total(int grand_total) {
        this.grand_total = grand_total;
    }

    public String getOff_desc() {
        return off_desc;
    }

    public void setOff_desc(String off_desc) {
        this.off_desc = off_desc;
    }
    
    
    
}
