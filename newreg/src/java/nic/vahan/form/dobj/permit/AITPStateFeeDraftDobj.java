/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.util.Date;
import nic.vahan.common.jsf.utils.DateUtil;

/**
 *
 * @author DELL
 */
public class AITPStateFeeDraftDobj implements Serializable {

    private String state_cd;
    private int off_cd;
    private String appl_no;
    private int sr_no;
    private String regn_no;
    private String pay_state_cd;
    private String pay_state_descr;
    private String instrument_type;
    private String instrument_no;
    private Date instrument_dt;
    private Date recieved_dt;
    private long instrument_amt = 0l;
    private String bank_code;
    private String bank_name;
    private String branch_name;
    private String payable_to;
    private String periodFromDt;
    private String periodToDate;
    private Date max_draft_date = null;
    private Date min_draft_date = DateUtil.parseDate(DateUtil.getCurrentDate());
    private String instrument_dtString = null;
    private String recieved_dtString = null;
    private int pur_cd;

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

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public int getSr_no() {
        return sr_no;
    }

    public void setSr_no(int sr_no) {
        this.sr_no = sr_no;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getPay_state_cd() {
        return pay_state_cd;
    }

    public void setPay_state_cd(String pay_state_cd) {
        this.pay_state_cd = pay_state_cd;
    }

    public String getInstrument_type() {
        return instrument_type;
    }

    public void setInstrument_type(String instrument_type) {
        this.instrument_type = instrument_type;
    }

    public String getInstrument_no() {
        return instrument_no;
    }

    public void setInstrument_no(String instrument_no) {
        this.instrument_no = instrument_no;
    }

    public Date getInstrument_dt() {
        return instrument_dt;
    }

    public void setInstrument_dt(Date instrument_dt) {
        this.instrument_dt = instrument_dt;
    }

    public long getInstrument_amt() {
        return instrument_amt;
    }

    public void setInstrument_amt(long instrument_amt) {
        this.instrument_amt = instrument_amt;
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

    public String getPayable_to() {
        return payable_to;
    }

    public void setPayable_to(String payable_to) {
        this.payable_to = payable_to;
    }

    public Date getRecieved_dt() {
        return recieved_dt;
    }

    public void setRecieved_dt(Date recieved_dt) {
        this.recieved_dt = recieved_dt;
    }

    public String getPay_state_descr() {
        return pay_state_descr;
    }

    public void setPay_state_descr(String pay_state_descr) {
        this.pay_state_descr = pay_state_descr;
    }

    public Date getMax_draft_date() {
        return max_draft_date;
    }

    public void setMax_draft_date(Date max_draft_date) {
        this.max_draft_date = max_draft_date;
    }

    public Date getMin_draft_date() {
        return min_draft_date;
    }

    public void setMin_draft_date(Date min_draft_date) {
        this.min_draft_date = min_draft_date;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getInstrument_dtString() {
        return instrument_dtString;
    }

    public void setInstrument_dtString(String instrument_dtString) {
        this.instrument_dtString = instrument_dtString;
    }

    public String getRecieved_dtString() {
        return recieved_dtString;
    }

    public void setRecieved_dtString(String recieved_dtString) {
        this.recieved_dtString = recieved_dtString;
    }

    public String getPeriodFromDt() {
        return periodFromDt;
    }

    public void setPeriodFromDt(String periodFromDt) {
        this.periodFromDt = periodFromDt;
    }

    public String getPeriodToDate() {
        return periodToDate;
    }

    public void setPeriodToDate(String periodToDate) {
        this.periodToDate = periodToDate;
    }

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }
}
