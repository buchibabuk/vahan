/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Kunal maiti
 */
public class AuditRecoveryDobj implements Serializable {

    /**
     * @return the applNo
     */
    public String getApplNo() {
        return applNo;
    }

    /**
     * @param applNo the applNo to set
     */
    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }
    private String regn_no;
    private int amount;
    private String para_no;
    private String para_year;
    private String objection;
    private Date from_dt;
    private Date to_dt;
    private String deal_cd;
    private Date op_date;
    private String paid_deal_cd;
    private Date paid_op_date;
    private String rcpt_no;
    private String audit_ty;
    private String paid_by;
    private int pay_amt;
    private Date pay_dt;
    private String reconcil_flag;
    private String state_cd;
    private int off_cd;
    private int sl_no;
    private String applNo;
    private String audit_tyDesc;

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
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * @return the para_no
     */
    public String getPara_no() {
        return para_no;
    }

    /**
     * @param para_no the para_no to set
     */
    public void setPara_no(String para_no) {
        this.para_no = para_no;
    }

    /**
     * @return the objection
     */
    public String getObjection() {
        return objection;
    }

    /**
     * @param objection the objection to set
     */
    public void setObjection(String objection) {
        this.objection = objection;
    }

    /**
     * @return the from_dt
     */
    public Date getFrom_dt() {
        return from_dt;
    }

    /**
     * @param from_dt the from_dt to set
     */
    public void setFrom_dt(Date from_dt) {
        this.from_dt = from_dt;
    }

    /**
     * @return the to_dt
     */
    public Date getTo_dt() {
        return to_dt;
    }

    /**
     * @param to_dt the to_dt to set
     */
    public void setTo_dt(Date to_dt) {
        this.to_dt = to_dt;
    }

    /**
     * @return the deal_cd
     */
    public String getDeal_cd() {
        return deal_cd;
    }

    /**
     * @param deal_cd the deal_cd to set
     */
    public void setDeal_cd(String deal_cd) {
        this.deal_cd = deal_cd;
    }

    /**
     * @return the op_date
     */
    public Date getOp_date() {
        return op_date;
    }

    /**
     * @param op_date the op_date to set
     */
    public void setOp_date(Date op_date) {
        this.op_date = op_date;
    }

    /**
     * @return the paid_deal_cd
     */
    public String getPaid_deal_cd() {
        return paid_deal_cd;
    }

    /**
     * @param paid_deal_cd the paid_deal_cd to set
     */
    public void setPaid_deal_cd(String paid_deal_cd) {
        this.paid_deal_cd = paid_deal_cd;
    }

    /**
     * @return the paid_op_date
     */
    public Date getPaid_op_date() {
        return paid_op_date;
    }

    /**
     * @param paid_op_date the paid_op_date to set
     */
    public void setPaid_op_date(Date paid_op_date) {
        this.paid_op_date = paid_op_date;
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
     * @return the audit_ty
     */
    public String getAudit_ty() {
        return audit_ty;
    }

    /**
     * @param audit_ty the audit_ty to set
     */
    public void setAudit_ty(String audit_ty) {
        this.audit_ty = audit_ty;
    }

    /**
     * @return the paid_by
     */
    public String getPaid_by() {
        return paid_by;
    }

    /**
     * @param paid_by the paid_by to set
     */
    public void setPaid_by(String paid_by) {
        this.paid_by = paid_by;
    }

    /**
     * @return the pay_amt
     */
    public int getPay_amt() {
        return pay_amt;
    }

    /**
     * @param pay_amt the pay_amt to set
     */
    public void setPay_amt(int pay_amt) {
        this.pay_amt = pay_amt;
    }

    /**
     * @return the pay_dt
     */
    public Date getPay_dt() {
        return pay_dt;
    }

    /**
     * @param pay_dt the pay_dt to set
     */
    public void setPay_dt(Date pay_dt) {
        this.pay_dt = pay_dt;
    }

    /**
     * @return the reconcil_flag
     */
    public String getReconcil_flag() {
        return reconcil_flag;
    }

    /**
     * @param reconcil_flag the reconcil_flag to set
     */
    public void setReconcil_flag(String reconcil_flag) {
        this.reconcil_flag = reconcil_flag;
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
     * @return the para_year
     */
    public String getPara_year() {
        return para_year;
    }

    /**
     * @param para_year the para_year to set
     */
    public void setPara_year(String para_year) {
        this.para_year = para_year;
    }

    /**
     * @return the sl_no
     */
    public int getSl_no() {
        return sl_no;
    }

    /**
     * @param sl_no the sl_no to set
     */
    public void setSl_no(int sl_no) {
        this.sl_no = sl_no;
    }

    /**
     * @return the audit_tyDesc
     */
    public String getAudit_tyDesc() {
        return audit_tyDesc;
    }

    /**
     * @param audit_tyDesc the audit_tyDesc to set
     */
    public void setAudit_tyDesc(String audit_tyDesc) {
        this.audit_tyDesc = audit_tyDesc;
    }
}
