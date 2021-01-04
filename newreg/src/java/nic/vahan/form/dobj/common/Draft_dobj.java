/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.common;

import java.util.Date;

/**
 *
 * @author AMBRISH
 */
public class Draft_dobj {

    private String appl_no;
    private int draft_cd;
    private String flag;
    private String draft_num;
    private Date dated;
    private Long amount;
    private String bank_code;
    private String branch_name;
    private String received_dt;
    private String status;
    private String bank_response_dt;
    private String status_cd;
    private String remarks;
    private String collected_by;
    private String state_cd;
    private int off_cd;
    private String appl_name = "";
    private long totalAmt = 0L;

    /**
     * @return the appl_no
     */
    public Draft_dobj() {
    }

    public Draft_dobj(Draft_dobj temp) {
        this.appl_no = temp.appl_no;
        this.draft_cd = temp.draft_cd;
        this.flag = temp.flag;
        this.draft_num = temp.draft_num;
        this.dated = temp.dated;
        this.amount = temp.amount;
        this.bank_code = temp.bank_code;
        this.branch_name = temp.branch_name;
        this.received_dt = temp.received_dt;
        this.status = temp.status;
        this.bank_response_dt = temp.bank_response_dt;
        this.status_cd = this.status_cd;
        this.remarks = temp.remarks;
        this.collected_by = temp.collected_by;
        this.state_cd = temp.state_cd;
        this.off_cd = temp.off_cd;
        this.appl_name = temp.appl_name;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" appl_no");
        sb.append(this.appl_no);
        sb.append(" draft_cd");
        sb.append(this.draft_cd);
        sb.append(" flag");
        sb.append(this.flag);
        sb.append(" draft_num");
        sb.append(draft_num);
        sb.append(" dated");
        sb.append(dated);
        sb.append(" amount");
        sb.append(amount);
        sb.append(" bank_code");
        sb.append(bank_code);
        sb.append(" branch_name");
        sb.append(branch_name);
        sb.append(" received_dt");
        sb.append(received_dt);
        sb.append(" status");
        sb.append(status);
        sb.append(" bank_response_dt");
        sb.append(bank_response_dt);
        sb.append(" status_cd");
        sb.append(status_cd);
        sb.append(" remarks");
        sb.append(remarks);
        sb.append(" collected_by");
        sb.append(collected_by);
        sb.append(" state_cd");
        sb.append(state_cd);
        sb.append(" off_cd");
        sb.append(off_cd);
        return sb.toString();
    }

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
     * @return the draft_cd
     */
    public int getDraft_cd() {
        return draft_cd;
    }

    /**
     * @param draft_cd the draft_cd to set
     */
    public void setDraft_cd(int draft_cd) {
        this.draft_cd = draft_cd;
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
     * @return the draft_num
     */
    public String getDraft_num() {
        return draft_num;
    }

    /**
     * @param draft_num the draft_num to set
     */
    public void setDraft_num(String draft_num) {
        this.draft_num = draft_num;
    }

    /**
     * @return the dated
     */
    public Date getDated() {
        return dated;
    }

    /**
     * @param dated the dated to set
     */
    public void setDated(Date dated) {
        this.dated = dated;
    }

    /**
     * @return the amount
     */
    public Long getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(Long amount) {
        this.amount = amount;
    }

    /**
     * @return the bank_code
     */
    public String getBank_code() {
        return bank_code;
    }

    /**
     * @param bank_code the bank_code to set
     */
    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    /**
     * @return the branch_name
     */
    public String getBranch_name() {
        return branch_name;
    }

    /**
     * @param branch_name the branch_name to set
     */
    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    /**
     * @return the received_dt
     */
    public String getReceived_dt() {
        return received_dt;
    }

    /**
     * @param received_dt the received_dt to set
     */
    public void setReceived_dt(String received_dt) {
        this.received_dt = received_dt;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the bank_response_dt
     */
    public String getBank_response_dt() {
        return bank_response_dt;
    }

    /**
     * @param bank_response_dt the bank_response_dt to set
     */
    public void setBank_response_dt(String bank_response_dt) {
        this.bank_response_dt = bank_response_dt;
    }

    /**
     * @return the status_cd
     */
    public String getStatus_cd() {
        return status_cd;
    }

    /**
     * @param status_cd the status_cd to set
     */
    public void setStatus_cd(String status_cd) {
        this.status_cd = status_cd;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    /**
     * @return the appl_name
     */
    public String getAppl_name() {
        return appl_name;
    }

    /**
     * @param appl_name the appl_name to set
     */
    public void setAppl_name(String appl_name) {
        this.appl_name = appl_name;
    }

    /**
     * @return the totalAmt
     */
    public long getTotalAmt() {
        return totalAmt;
    }

    /**
     * @param totalAmt the totalAmt to set
     */
    public void setTotalAmt(long totalAmt) {
        this.totalAmt = totalAmt;
    }
}
