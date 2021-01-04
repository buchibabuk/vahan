/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author acer
 */
public class PermitLeaseDobj implements Serializable, Cloneable {

    private String state_cd;
    private int off_cd;
    private String appl_no;
    private String regn_no;
    private String rcpt_no;
    private int pur_cd;
    private String pmt_no;
    private int pmt_type;
    private String period_mode;
    private int period;
    private Date valid_from;
    private Date valid_upto;
    private String owner_name;
    private String f_name;
    private String leaseRegnNo;
    private String address;
    private String city;
    private String pincode;
    private String validFromInString;
    private String validUptoInString;

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

    public String getPmt_no() {
        return pmt_no;
    }

    public void setPmt_no(String pmt_no) {
        this.pmt_no = pmt_no;
    }

    public int getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(int pmt_type) {
        this.pmt_type = pmt_type;
    }

    public String getPeriod_mode() {
        return period_mode;
    }

    public void setPeriod_mode(String period_mode) {
        this.period_mode = period_mode;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
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

    public String getLeaseRegnNo() {
        return leaseRegnNo;
    }

    public void setLeaseRegnNo(String leaseRegnNo) {
        this.leaseRegnNo = leaseRegnNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getValidFromInString() {
        return validFromInString;
    }

    public void setValidFromInString(String validFromInString) {
        this.validFromInString = validFromInString;
    }

    public String getValidUptoInString() {
        return validUptoInString;
    }

    public void setValidUptoInString(String validUptoInString) {
        this.validUptoInString = validUptoInString;
    }
}
