/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.io.Serializable;

public class PrintPermitShowDetails implements Serializable {

    public String sr, appl_no, appl_dt, regn_no, pmt_no, office_remark, public_remark, move_sr;
    public String offer_no, pmt_type, pur_cd, doc_id, op_dt;
    public String pmt_type_descr,pur_cd_descr,doc_id_descr;
    public PrintPermitShowDetails() {
    }

    public PrintPermitShowDetails(String sr, String appl_no, String appl_dt, String regn_no,
            String pmt_no, String office_remark, String public_remark, String pmt_type, String offer_no) {
        this.sr = sr;
        this.appl_no = appl_no;
        this.appl_dt = appl_dt;
        this.regn_no = regn_no;
        this.pmt_no = pmt_no;
        this.office_remark = office_remark;
        this.public_remark = public_remark;
        this.pmt_type = pmt_type;
        this.offer_no = offer_no;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getAppl_dt() {
        return appl_dt;
    }

    public void setAppl_dt(String appl_dt) {
        this.appl_dt = appl_dt;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getPmt_no() {
        return pmt_no;
    }

    public void setPmt_no(String pmt_no) {
        this.pmt_no = pmt_no;
    }

    public String getOffice_remark() {
        return office_remark;
    }

    public void setOffice_remark(String office_remark) {
        this.office_remark = office_remark;
    }

    public String getPublic_remark() {
        return public_remark;
    }

    public void setPublic_remark(String public_remark) {
        this.public_remark = public_remark;
    }

    public String getSr() {
        return sr;
    }

    public void setSr(String sr) {
        this.sr = sr;
    }

    public String getMove_sr() {
        return move_sr;
    }

    public void setMove_sr(String move_sr) {
        this.move_sr = move_sr;
    }

    public String getOffer_no() {
        return offer_no;
    }

    public void setOffer_no(String offer_no) {
        this.offer_no = offer_no;
    }

    public String getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(String pmt_type) {
        this.pmt_type = pmt_type;
    }

    public String getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(String pur_cd) {
        this.pur_cd = pur_cd;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getOp_dt() {
        return op_dt;
    }

    public void setOp_dt(String op_dt) {
        this.op_dt = op_dt;
    }

    public String getPmt_type_descr() {
        return pmt_type_descr;
    }

    public void setPmt_type_descr(String pmt_type_descr) {
        this.pmt_type_descr = pmt_type_descr;
    }

    public String getPur_cd_descr() {
        return pur_cd_descr;
    }

    public void setPur_cd_descr(String pur_cd_descr) {
        this.pur_cd_descr = pur_cd_descr;
    }

    public String getDoc_id_descr() {
        return doc_id_descr;
    }

    public void setDoc_id_descr(String doc_id_descr) {
        this.doc_id_descr = doc_id_descr;
    }
    
}
