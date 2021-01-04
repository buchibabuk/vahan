/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.util.Date;
import java.util.List;

/**
 *
 * @author tranC105
 */
public class FeeDraftDobj {

    private String appl_no;
    private String draft_cd;
    private String flag;
    private String draft_num;
    private Date dated;
    private String amount;
    private String bank_code;
    private String branch_name;
    private Date received_dt;
    private String status;
    private Date bank_response_dt;
    private String status_cd;
    private String remarks;
    private String collected_by;
    private String state_cd;
    private String off_cd;
    private String rcpt_no;
    private String chasi_no;
    private int vh_class;
    private String collected_from;
    private int excess_amount = 0;
    private long totalamount = 0l;
    private List<PaymentCollectionDobj> draftPaymentList;

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getDraft_cd() {
        return draft_cd;
    }

    public void setDraft_cd(String draft_cd) {
        this.draft_cd = draft_cd;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getDraft_num() {
        return draft_num;
    }

    public void setDraft_num(String draft_num) {
        this.draft_num = draft_num;
    }

    public Date getDated() {
        return dated;
    }

    public void setDated(Date dated) {
        this.dated = dated;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBank_code() {
        return bank_code;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public Date getReceived_dt() {
        return received_dt;
    }

    public void setReceived_dt(Date received_dt) {
        this.received_dt = received_dt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getBank_response_dt() {
        return bank_response_dt;
    }

    public void setBank_response_dt(Date bank_response_dt) {
        this.bank_response_dt = bank_response_dt;
    }

    public String getStatus_cd() {
        return status_cd;
    }

    public void setStatus_cd(String status_cd) {
        this.status_cd = status_cd;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCollected_by() {
        return collected_by;
    }

    public void setCollected_by(String collected_by) {
        this.collected_by = collected_by;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public String getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(String off_cd) {
        this.off_cd = off_cd;
    }

    public List<PaymentCollectionDobj> getDraftPaymentList() {
        return draftPaymentList;
    }

    public void setDraftPaymentList(List<PaymentCollectionDobj> draftPaymentList) {
        this.draftPaymentList = draftPaymentList;
    }

    public String getRcpt_no() {
        return rcpt_no;
    }

    public void setRcpt_no(String rcpt_no) {
        this.rcpt_no = rcpt_no;
    }

    /**
     * @return the chasi_no
     */
    public String getChasi_no() {
        return chasi_no;
    }

    /**
     * @param chasi_no the chasi_no to set
     */
    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
    }

    /**
     * @return the vh_class
     */
    public int getVh_class() {
        return vh_class;
    }

    /**
     * @param vh_class the vh_class to set
     */
    public void setVh_class(int vh_class) {
        this.vh_class = vh_class;
    }

    /**
     * @return the collected_from
     */
    public String getCollected_from() {
        return collected_from;
    }

    /**
     * @param collected_from the collected_from to set
     */
    public void setCollected_from(String collected_from) {
        this.collected_from = collected_from;
    }

    /**
     * @return the axcess_amount
     */
    public int getExcess_amount() {
        return excess_amount;
    }

    /**
     * @param axcess_amount the axcess_amount to set
     */
    public void setExcess_amount(int excess_amount) {
        this.excess_amount = excess_amount;
    }

    /**
     * @return the totalamount
     */
    public long getTotalamount() {
        return totalamount;
    }

    /**
     * @param totalamount the totalamount to set
     */
    public void setTotalamount(long totalamount) {
        this.totalamount = totalamount;
    }
}
