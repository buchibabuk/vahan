/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;

/**
 *
 * @author nicsi
 */
public class InsertCDDataInTableDobj implements Serializable {

    String Chal_date;
    String Chal_time;
    String regn_no;
    String Chal_no;
    String owner_name;
    String address;
    String Chal_place;
    String state;
    String office;
    String op_date;
    String mobile_no;

    public String getChal_date() {
        return Chal_date;
    }

    public void setChal_date(String Chal_date) {
        this.Chal_date = Chal_date;
    }

    public String getChal_time() {
        return Chal_time;
    }

    public void setChal_time(String Chal_time) {
        this.Chal_time = Chal_time;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getChal_no() {
        return Chal_no;
    }

    public void setChal_no(String Chal_no) {
        this.Chal_no = Chal_no;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getChal_place() {
        return Chal_place;
    }

    public void setChal_place(String Chal_place) {
        this.Chal_place = Chal_place;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getOp_date() {
        return op_date;
    }

    public void setOp_date(String op_date) {
        this.op_date = op_date;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }
}
