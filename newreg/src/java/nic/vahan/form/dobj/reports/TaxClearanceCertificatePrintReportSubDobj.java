/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;

/**
 *
 * @author ankur
 */
public class TaxClearanceCertificatePrintReportSubDobj implements Serializable {

    private String clear_fr;
    private String clear_to;
    private String pur_cd;
    private String pur_cd_descr;
    private String pur_cd_descr_vttax;
    private String tax_amt_vttax;
    private String tax_total_vttax;
    private String rcpt_no_vttax;
    private String tax_from_vttax;
    private String tax_upto_vttax;
    private String tax_mode_descr_vttax;
    private String fees_Diff_tax;
    private String fine_Diff_tax;
    private String rcpt_no_Diff_tax;
    private String rcpt_dt_Diff_tax;

    public String getClear_fr() {
        return clear_fr;
    }

    public void setClear_fr(String clear_fr) {
        this.clear_fr = clear_fr;
    }

    public String getClear_to() {
        return clear_to;
    }

    public void setClear_to(String clear_to) {
        this.clear_to = clear_to;
    }

    public String getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(String pur_cd) {
        this.pur_cd = pur_cd;
    }

    public String getPur_cd_descr() {
        return pur_cd_descr;
    }

    public void setPur_cd_descr(String pur_cd_descr) {
        this.pur_cd_descr = pur_cd_descr;
    }

    /**
     * @return the pur_cd_descr_vttax
     */
    public String getPur_cd_descr_vttax() {
        return pur_cd_descr_vttax;
    }

    /**
     * @param pur_cd_descr_vttax the pur_cd_descr_vttax to set
     */
    public void setPur_cd_descr_vttax(String pur_cd_descr_vttax) {
        this.pur_cd_descr_vttax = pur_cd_descr_vttax;
    }

    /**
     * @return the tax_amt_vttax
     */
    public String getTax_amt_vttax() {
        return tax_amt_vttax;
    }

    /**
     * @param tax_amt_vttax the tax_amt_vttax to set
     */
    public void setTax_amt_vttax(String tax_amt_vttax) {
        this.tax_amt_vttax = tax_amt_vttax;
    }

    /**
     * @return the rcpt_no_vttax
     */
    public String getRcpt_no_vttax() {
        return rcpt_no_vttax;
    }

    /**
     * @param rcpt_no_vttax the rcpt_no_vttax to set
     */
    public void setRcpt_no_vttax(String rcpt_no_vttax) {
        this.rcpt_no_vttax = rcpt_no_vttax;
    }

    /**
     * @return the tax_from_vttax
     */
    public String getTax_from_vttax() {
        return tax_from_vttax;
    }

    /**
     * @param tax_from_vttax the tax_from_vttax to set
     */
    public void setTax_from_vttax(String tax_from_vttax) {
        this.tax_from_vttax = tax_from_vttax;
    }

    /**
     * @return the tax_upto_vttax
     */
    public String getTax_upto_vttax() {
        return tax_upto_vttax;
    }

    /**
     * @param tax_upto_vttax the tax_upto_vttax to set
     */
    public void setTax_upto_vttax(String tax_upto_vttax) {
        this.tax_upto_vttax = tax_upto_vttax;
    }

    /**
     * @return the tax_mode_descr_vttax
     */
    public String getTax_mode_descr_vttax() {
        return tax_mode_descr_vttax;
    }

    /**
     * @param tax_mode_descr_vttax the tax_mode_descr_vttax to set
     */
    public void setTax_mode_descr_vttax(String tax_mode_descr_vttax) {
        this.tax_mode_descr_vttax = tax_mode_descr_vttax;
    }

    /**
     * @return the fees_Diff_tax
     */
    public String getFees_Diff_tax() {
        return fees_Diff_tax;
    }

    /**
     * @param fees_Diff_tax the fees_Diff_tax to set
     */
    public void setFees_Diff_tax(String fees_Diff_tax) {
        this.fees_Diff_tax = fees_Diff_tax;
    }

    /**
     * @return the fine_Diff_tax
     */
    public String getFine_Diff_tax() {
        return fine_Diff_tax;
    }

    /**
     * @param fine_Diff_tax the fine_Diff_tax to set
     */
    public void setFine_Diff_tax(String fine_Diff_tax) {
        this.fine_Diff_tax = fine_Diff_tax;
    }

    /**
     * @return the rcpt_no_Diff_tax
     */
    public String getRcpt_no_Diff_tax() {
        return rcpt_no_Diff_tax;
    }

    /**
     * @param rcpt_no_Diff_tax the rcpt_no_Diff_tax to set
     */
    public void setRcpt_no_Diff_tax(String rcpt_no_Diff_tax) {
        this.rcpt_no_Diff_tax = rcpt_no_Diff_tax;
    }

    /**
     * @return the rcpt_dt_Diff_tax
     */
    public String getRcpt_dt_Diff_tax() {
        return rcpt_dt_Diff_tax;
    }

    /**
     * @param rcpt_dt_Diff_tax the rcpt_dt_Diff_tax to set
     */
    public void setRcpt_dt_Diff_tax(String rcpt_dt_Diff_tax) {
        this.rcpt_dt_Diff_tax = rcpt_dt_Diff_tax;
    }

    /**
     * @return the tax_total_vttax
     */
    public String getTax_total_vttax() {
        return tax_total_vttax;
    }

    /**
     * @param tax_total_vttax the tax_total_vttax to set
     */
    public void setTax_total_vttax(String tax_total_vttax) {
        this.tax_total_vttax = tax_total_vttax;
    }
}
