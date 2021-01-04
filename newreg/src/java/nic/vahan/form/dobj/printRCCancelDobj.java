/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

public class printRCCancelDobj implements Serializable {

    private String state_cd;
    private String state_name;
    private int off_cd;
    private String off_name;
    private String regn_no;
    private String regn_dt;
    private String chasi_no;
    private String eng_no;
    private int manu_mon;
    private int manu_yr;
    private int vh_class;
    private String vch_catg;
    private String body_type;
    private String tax_upto;
    private String printDate = "";
    private String fit_chk_dt;
    private String vh_class_desc;
    private String reportHeading;
    private String reportSubHeading;
    private String appl_no;

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public int getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    public String getOff_name() {
        return off_name;
    }

    public void setOff_name(String off_name) {
        this.off_name = off_name;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getRegn_dt() {
        return regn_dt;
    }

    public void setRegn_dt(String regn_dt) {
        this.regn_dt = regn_dt;
    }

    public String getChasi_no() {
        return chasi_no;
    }

    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
    }

    public String getEng_no() {
        return eng_no;
    }

    public void setEng_no(String eng_no) {
        this.eng_no = eng_no;
    }

    public int getManu_mon() {
        return manu_mon;
    }

    public void setManu_mon(int manu_mon) {
        this.manu_mon = manu_mon;
    }

    public int getManu_yr() {
        return manu_yr;
    }

    public void setManu_yr(int manu_yr) {
        this.manu_yr = manu_yr;
    }

    public int getVh_class() {
        return vh_class;
    }

    public void setVh_class(int vh_class) {
        this.vh_class = vh_class;
    }

    public String getVch_catg() {
        return vch_catg;
    }

    public void setVch_catg(String vch_catg) {
        this.vch_catg = vch_catg;
    }

    public String getBody_type() {
        return body_type;
    }

    public void setBody_type(String body_type) {
        this.body_type = body_type;
    }

    /**
     * @return the printDate
     */
    public String getPrintDate() {
        return printDate;
    }

    /**
     * @param printDate the printDate to set
     */
    public void setPrintDate(String printDate) {
        this.printDate = printDate;
    }

    /**
     * @return the vh_class_desc
     */
    public String getVh_class_desc() {
        return vh_class_desc;
    }

    /**
     * @param vh_class_desc the vh_class_desc to set
     */
    public void setVh_class_desc(String vh_class_desc) {
        this.vh_class_desc = vh_class_desc;
    }

    public String getReportHeading() {
        return reportHeading;
    }

    public void setReportHeading(String reportHeading) {
        this.reportHeading = reportHeading;
    }

    public String getReportSubHeading() {
        return reportSubHeading;
    }

    public void setReportSubHeading(String reportSubHeading) {
        this.reportSubHeading = reportSubHeading;
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

    public String getTax_upto() {
        return tax_upto;
    }

    public void setTax_upto(String tax_upto) {
        this.tax_upto = tax_upto;
    }

    public String getFit_chk_dt() {
        return fit_chk_dt;
    }

    public void setFit_chk_dt(String fit_chk_dt) {
        this.fit_chk_dt = fit_chk_dt;
    }
}
