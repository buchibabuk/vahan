/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author tranC086
 */
public class SurrenderPermitDobj implements Cloneable, Serializable {

    private String regn_no;
    private String state_cd;
    private int off_cd;
    private String appl_no;
    private String pmt_no;
    private Date issue_dt;
    private String rcpt_no;
    private int pur_cd;
    private String region_covered;
    private int service_type;
    private String goods_to_carry;
    private String jorney_purpose;
    private String parking;
    private Date replace_date;
    private Date op_dt;
    private String purpose;
    private String order_no;
    private Date order_dt;
    private String order_by;
    private String new_regn_no;
    private int emp_cd;
    private String remarks;
    private int pmtType;
    private int pmtCatg;
    private Date surrenderDate;
    private String transfer_purpose;
    private int tempspl_purpose;

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

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

    public String getPmt_no() {
        return pmt_no;
    }

    public void setPmt_no(String pmt_no) {
        this.pmt_no = pmt_no;
    }

    public Date getIssue_dt() {
        return issue_dt;
    }

    public void setIssue_dt(Date issue_dt) {
        this.issue_dt = issue_dt;
    }

    public String getRcpt_no() {
        return rcpt_no;
    }

    public void setRcpt_no(String rcpt_no) {
        this.rcpt_no = rcpt_no;
    }

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    public String getRegion_covered() {
        return region_covered;
    }

    public void setRegion_covered(String region_covered) {
        this.region_covered = region_covered;
    }

    public int getService_type() {
        return service_type;
    }

    public void setService_type(int service_type) {
        this.service_type = service_type;
    }

    public String getGoods_to_carry() {
        return goods_to_carry;
    }

    public void setGoods_to_carry(String goods_to_carry) {
        this.goods_to_carry = goods_to_carry;
    }

    public String getJorney_purpose() {
        return jorney_purpose;
    }

    public void setJorney_purpose(String jorney_purpose) {
        this.jorney_purpose = jorney_purpose;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public Date getReplace_date() {
        return replace_date;
    }

    public void setReplace_date(Date replace_date) {
        this.replace_date = replace_date;
    }

    public Date getOp_dt() {
        return op_dt;
    }

    public void setOp_dt(Date op_dt) {
        this.op_dt = op_dt;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public Date getOrder_dt() {
        return order_dt;
    }

    public void setOrder_dt(Date order_dt) {
        this.order_dt = order_dt;
    }

    public String getOrder_by() {
        return order_by;
    }

    public void setOrder_by(String order_by) {
        this.order_by = order_by;
    }

    public String getNew_regn_no() {
        return new_regn_no;
    }

    public void setNew_regn_no(String new_regn_no) {
        this.new_regn_no = new_regn_no;
    }

    public int getEmp_cd() {
        return emp_cd;
    }

    public void setEmp_cd(int emp_cd) {
        this.emp_cd = emp_cd;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getPmtType() {
        return pmtType;
    }

    public void setPmtType(int pmtType) {
        this.pmtType = pmtType;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public Date getSurrenderDate() {
        return surrenderDate;
    }

    public void setSurrenderDate(Date surrenderDate) {
        this.surrenderDate = surrenderDate;
    }

    public String getTransfer_purpose() {
        return transfer_purpose;
    }

    public void setTransfer_purpose(String transfer_purpose) {
        this.transfer_purpose = transfer_purpose;
    }

    public int getTempspl_purpose() {
        return tempspl_purpose;
    }

    public void setTempspl_purpose(int tempspl_purpose) {
        this.tempspl_purpose = tempspl_purpose;
    }

    public int getPmtCatg() {
        return pmtCatg;
    }

    public void setPmtCatg(int pmtCatg) {
        this.pmtCatg = pmtCatg;
    }
}
