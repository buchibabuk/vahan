/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class CommonPermitFeeDobj implements Serializable {

    private String state_cd;
    private String off_cd;
    private String rcpt_no;
    private String instrument_cd;
    private String regn_no;
    private String payment_mode;
    private String fees;
    private String fine;
    private Date rcpt_dt;
    private int pur_cd;
    private String flag;
    private String collected_by;
    private String sr_no;
    private String instrument_type;
    private String instrument_no;
    private String instrument_dt;
    private String instrument_amt;
    private String bank_code;
    private String branch_name;
    private String received_dt;
    private String status;
    private String bank_response_dt;
    private String status_cd;
    private String appl_no;
    private String remarks;
    private String owner_name;
    private String vh_class;
    private String chasi_no;
    private List<PermitShowFeeDetailDobj> listPmtFeeDetails = new ArrayList<>();

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

    public String getRcpt_no() {
        return rcpt_no;
    }

    public void setRcpt_no(String rcpt_no) {
        this.rcpt_no = rcpt_no;
    }

    public String getInstrument_cd() {
        return instrument_cd;
    }

    public void setInstrument_cd(String instrument_cd) {
        this.instrument_cd = instrument_cd;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getFine() {
        return fine;
    }

    public void setFine(String fine) {
        this.fine = fine;
    }

    public Date getRcpt_dt() {
        return rcpt_dt;
    }

    public void setRcpt_dt(Date rcpt_dt) {
        this.rcpt_dt = rcpt_dt;
    }

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCollected_by() {
        return collected_by;
    }

    public void setCollected_by(String collected_by) {
        this.collected_by = collected_by;
    }

    public String getSr_no() {
        return sr_no;
    }

    public void setSr_no(String sr_no) {
        this.sr_no = sr_no;
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

    public String getInstrument_dt() {
        return instrument_dt;
    }

    public void setInstrument_dt(String instrument_dt) {
        this.instrument_dt = instrument_dt;
    }

    public String getInstrument_amt() {
        return instrument_amt;
    }

    public void setInstrument_amt(String instrument_amt) {
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

    public String getReceived_dt() {
        return received_dt;
    }

    public void setReceived_dt(String received_dt) {
        this.received_dt = received_dt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBank_response_dt() {
        return bank_response_dt;
    }

    public void setBank_response_dt(String bank_response_dt) {
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

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getVh_class() {
        return vh_class;
    }

    public void setVh_class(String vh_class) {
        this.vh_class = vh_class;
    }

    public String getChasi_no() {
        return chasi_no;
    }

    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
    }

    public List<PermitShowFeeDetailDobj> getListPmtFeeDetails() {
        return listPmtFeeDetails;
    }

    public void setListPmtFeeDetails(List<PermitShowFeeDetailDobj> listPmtFeeDetails) {
        this.listPmtFeeDetails = listPmtFeeDetails;
    }
}
