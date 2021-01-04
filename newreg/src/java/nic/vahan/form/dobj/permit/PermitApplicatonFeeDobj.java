/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class PermitApplicatonFeeDobj implements Serializable {

    private String appl_no;
    private String regn_no;
    private String owner_name;
    private String f_name;
    private String vh_class;
    private String pmt_type;
    private String pmt_catg;
    private String Seat_cap;
    private String chasi_no;
    private PassengerPermitDetailDobj pmtDobj = new PassengerPermitDetailDobj();

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
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

    public String getVh_class() {
        return vh_class;
    }

    public void setVh_class(String vh_class) {
        this.vh_class = vh_class;
    }

    public String getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(String pmt_type) {
        this.pmt_type = pmt_type;
    }

    public String getSeat_cap() {
        return Seat_cap;
    }

    public void setSeat_cap(String Seat_cap) {
        this.Seat_cap = Seat_cap;
    }

    public String getPmt_catg() {
        return pmt_catg;
    }

    public void setPmt_catg(String pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    public String getChasi_no() {
        return chasi_no;
    }

    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
    }

    public PassengerPermitDetailDobj getPmtDobj() {
        return pmtDobj;
    }

    public void setPmtDobj(PassengerPermitDetailDobj pmtDobj) {
        this.pmtDobj = pmtDobj;
    }
}
