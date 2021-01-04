/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author tranC106
 */
public class TaxClearDobj implements Cloneable, Serializable {

    private String currentdt;
    private Date clear_fr;
    private Date taxclearuptodt;
    private Date recieptDate;
    private String recieptNo;
    private String SRTclearuptodt;
    private String clearby;
    private String orderno;
    private Date orderdt;
    private String remarks;
    private String regnno;
    private String appl_no;
    private int pur_cd;
    private String tcr_no;
    private String rcpt_noold;
    private String tax_fromold;
    private String tax_uptoold;
    private String descrold;
    private String rcpt_dtold;
    private String tax_amtold;
    private String tax_fineold;
    private String rcpt_noHist;
    private String tax_fromHist;
    private String tax_uptoHist;
    private String descrHist;
    private String op_dtHist;
    private String state_cd;
    private int off_cd;
    private String off_name;
    private String fees_Diff_tax;
    private String fine_Diff_tax;
    private String rcpt_no_Diff_tax;
    private String rcpt_dt_Diff_tax;
    boolean showSavePanel = false;
    boolean renderTaxPanel = false;

    public String getCurrentdt() {
        return currentdt;
    }

    public void setCurrentdt(String currentdt) {
        this.currentdt = currentdt;
    }

    public Date getClear_fr() {
        return clear_fr;
    }

    public void setClear_fr(Date clear_fr) {
        this.clear_fr = clear_fr;
    }

    public Date getTaxclearuptodt() {
        return taxclearuptodt;
    }

    public void setTaxclearuptodt(Date taxclearuptodt) {
        this.taxclearuptodt = taxclearuptodt;
    }

    public String getSRTclearuptodt() {
        return SRTclearuptodt;
    }

    public void setSRTclearuptodt(String SRTclearuptodt) {
        this.SRTclearuptodt = SRTclearuptodt;
    }

    public String getClearby() {
        return clearby;
    }

    public void setClearby(String clearby) {
        this.clearby = clearby;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public Date getOrderdt() {
        return orderdt;
    }

    public void setOrderdt(Date orderdt) {
        this.orderdt = orderdt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRegnno() {
        return regnno;
    }

    public void setRegnno(String regnno) {
        this.regnno = regnno;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    public String getTcr_no() {
        return tcr_no;
    }

    public void setTcr_no(String tcr_no) {
        this.tcr_no = tcr_no;
    }

    public String getRcpt_noold() {
        return rcpt_noold;
    }

    public void setRcpt_noold(String rcpt_noold) {
        this.rcpt_noold = rcpt_noold;
    }

    public String getTax_fromold() {
        return tax_fromold;
    }

    public void setTax_fromold(String tax_fromold) {
        this.tax_fromold = tax_fromold;
    }

    public String getTax_uptoold() {
        return tax_uptoold;
    }

    public void setTax_uptoold(String tax_uptoold) {
        this.tax_uptoold = tax_uptoold;
    }

    public String getDescrold() {
        return descrold;
    }

    public void setDescrold(String descrold) {
        this.descrold = descrold;
    }

    public String getRcpt_dtold() {
        return rcpt_dtold;
    }

    public void setRcpt_dtold(String rcpt_dtold) {
        this.rcpt_dtold = rcpt_dtold;
    }

    public String getTax_amtold() {
        return tax_amtold;
    }

    public void setTax_amtold(String tax_amtold) {
        this.tax_amtold = tax_amtold;
    }

    public String getTax_fineold() {
        return tax_fineold;
    }

    public void setTax_fineold(String tax_fineold) {
        this.tax_fineold = tax_fineold;
    }

    /**
     * @return the rcpt_noHist
     */
    public String getRcpt_noHist() {
        return rcpt_noHist;
    }

    /**
     * @param rcpt_noHist the rcpt_noHist to set
     */
    public void setRcpt_noHist(String rcpt_noHist) {
        this.rcpt_noHist = rcpt_noHist;
    }

    /**
     * @return the tax_fromHist
     */
    public String getTax_fromHist() {
        return tax_fromHist;
    }

    /**
     * @param tax_fromHist the tax_fromHist to set
     */
    public void setTax_fromHist(String tax_fromHist) {
        this.tax_fromHist = tax_fromHist;
    }

    /**
     * @return the tax_uptoHist
     */
    public String getTax_uptoHist() {
        return tax_uptoHist;
    }

    /**
     * @param tax_uptoHist the tax_uptoHist to set
     */
    public void setTax_uptoHist(String tax_uptoHist) {
        this.tax_uptoHist = tax_uptoHist;
    }

    /**
     * @return the descrHist
     */
    public String getDescrHist() {
        return descrHist;
    }

    /**
     * @param descrHist the descrHist to set
     */
    public void setDescrHist(String descrHist) {
        this.descrHist = descrHist;
    }

    /**
     * @return the op_dtHist
     */
    public String getOp_dtHist() {
        return op_dtHist;
    }

    /**
     * @param op_dtHist the op_dtHist to set
     */
    public void setOp_dtHist(String op_dtHist) {
        this.op_dtHist = op_dtHist;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getFees_Diff_tax() {
        return fees_Diff_tax;
    }

    public void setFees_Diff_tax(String fees_Diff_tax) {
        this.fees_Diff_tax = fees_Diff_tax;
    }

    public String getFine_Diff_tax() {
        return fine_Diff_tax;
    }

    public void setFine_Diff_tax(String fine_Diff_tax) {
        this.fine_Diff_tax = fine_Diff_tax;
    }

    public String getRcpt_no_Diff_tax() {
        return rcpt_no_Diff_tax;
    }

    public void setRcpt_no_Diff_tax(String rcpt_no_Diff_tax) {
        this.rcpt_no_Diff_tax = rcpt_no_Diff_tax;
    }

    public String getRcpt_dt_Diff_tax() {
        return rcpt_dt_Diff_tax;
    }

    public void setRcpt_dt_Diff_tax(String rcpt_dt_Diff_tax) {
        this.rcpt_dt_Diff_tax = rcpt_dt_Diff_tax;

    }

    public Date getRecieptDate() {
        return recieptDate;
    }

    public void setRecieptDate(Date recieptDate) {
        this.recieptDate = recieptDate;
    }

    public String getRecieptNo() {
        return recieptNo;
    }

    public void setRecieptNo(String recieptNo) {
        this.recieptNo = recieptNo;
    }

    /**
     * @return the off_name
     */
    public String getOff_name() {
        return off_name;
    }

    /**
     * @param off_name the off_name to set
     */
    public void setOff_name(String off_name) {
        this.off_name = off_name;
    }
}
