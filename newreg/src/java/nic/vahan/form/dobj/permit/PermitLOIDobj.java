/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;

/**
 *
 * @author hcl
 */
public class PermitLOIDobj implements Serializable {

    private String regn_no;
    private String appl_no;
    private String owner_name;
    private String f_name;
    private int pmt_type;
    private String pmt_type_descr;
    private int pmt_catg;
    private String pmt_catg_descr;
    private String period_mode;
    private String period_descr;
    private int period;
    private String op_dt;
    private String dlNo;
    private String dlStatus;
    private String dob;
    private String psvNo;
    private String maker_descr;
    private String model_descr;
    
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

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public int getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(int pmt_type) {
        this.pmt_type = pmt_type;
    }

    public String getPmt_type_descr() {
        return pmt_type_descr;
    }

    public void setPmt_type_descr(String pmt_type_descr) {
        this.pmt_type_descr = pmt_type_descr;
    }

    public int getPmt_catg() {
        return pmt_catg;
    }

    public void setPmt_catg(int pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    public String getPmt_catg_descr() {
        return pmt_catg_descr;
    }

    public void setPmt_catg_descr(String pmt_catg_descr) {
        this.pmt_catg_descr = pmt_catg_descr;
    }

    public String getPeriod_mode() {
        return period_mode;
    }

    public void setPeriod_mode(String period_mode) {
        this.period_mode = period_mode;
    }

    public String getPeriod_descr() {
        return period_descr;
    }

    public void setPeriod_descr(String period_descr) {
        this.period_descr = period_descr;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getOp_dt() {
        return op_dt;
    }

    public void setOp_dt(String op_dt) {
        this.op_dt = op_dt;
    }

    public String getDlNo() {
        return dlNo;
    }

    public void setDlNo(String dlNo) {
        this.dlNo = dlNo;
    }

    public String getDlStatus() {
        return dlStatus;
    }

    public void setDlStatus(String dlStatus) {
        this.dlStatus = dlStatus;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPsvNo() {
        return psvNo;
    }

    public void setPsvNo(String psvNo) {
        this.psvNo = psvNo;
    }

    public String getMaker_descr() {
        return maker_descr;
    }

    public void setMaker_descr(String maker_descr) {
        this.maker_descr = maker_descr;
    }

    public String getModel_descr() {
        return model_descr;
    }

    public void setModel_descr(String model_descr) {
        this.model_descr = model_descr;
    }
}
