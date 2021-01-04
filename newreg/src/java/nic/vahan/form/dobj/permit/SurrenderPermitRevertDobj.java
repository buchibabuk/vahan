/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ankur
 */
public class SurrenderPermitRevertDobj implements Cloneable, Serializable {

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
    private Date valid_from;
    private Date valid_upto;
    private int pmt_type;
    private int pmt_catg;
    private String pmt_catg_desc;
    private String paction_code;
    private String paction;
    private String regnNo;

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

    public Date getValid_from() {
        return valid_from;
    }

    public void setValid_from(Date valid_from) {
        this.valid_from = valid_from;
    }

    public Date getValid_upto() {
        return valid_upto;
    }

    public void setValid_upto(Date valid_upto) {
        this.valid_upto = valid_upto;
    }

    public int getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(int pmt_type) {
        this.pmt_type = pmt_type;
    }

    public int getPmt_catg() {
        return pmt_catg;
    }

    public void setPmt_catg(int pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    public String getPmt_catg_desc() {
        return pmt_catg_desc;
    }

    public void setPmt_catg_desc(String pmt_catg_desc) {
        this.pmt_catg_desc = pmt_catg_desc;
    }

    public String getPaction_code() {
        return paction_code;
    }

    public void setPaction_code(String paction_code) {
        this.paction_code = paction_code;
    }

    public String getPaction() {
        return paction;
    }

    public void setPaction(String paction) {
        this.paction = paction;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }
}
