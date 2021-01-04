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
public class PermitDtlsByOwNameDobj implements Serializable {

    private String regn_no;
    private String pmt_no;
    private String owner_name;
    private String f_name;
    private String address;
    private String regnDt;
    private String status;

    public PermitDtlsByOwNameDobj(String regn_no, String pmt_no, String owner_name, String f_name, String address, String regnDt, String status) {
        this.regn_no = regn_no;
        this.pmt_no = pmt_no;
        this.owner_name = owner_name;
        this.f_name = f_name;
        this.address = address;
        this.regnDt = regnDt;
        this.status = status;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegnDt() {
        return regnDt;
    }

    public void setRegnDt(String regnDt) {
        this.regnDt = regnDt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
