/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author hcl
 */
public class CounterSignatureDobj implements Serializable, Cloneable {

    private String state_cd_from;
    private int off_cd_from;
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
    private int region;
    private String maker_name;
    private String model_name;
    private int no_of_trips;
    private Date count_valid_upto = null;
    private Date count_valid_from = null;
    private String state_to;
    private int off_cd_to;
    private int pmt_catg;
    private String off_to_name;
    private String cs_auth_no;

    public String getState_cd_from() {
        return state_cd_from;
    }

    public void setState_cd_from(String state_cd_from) {
        this.state_cd_from = state_cd_from;
    }

    public int getOff_cd_from() {
        return off_cd_from;
    }

    public void setOff_cd_from(int off_cd_from) {
        this.off_cd_from = off_cd_from;
    }

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

    public Date getCount_valid_upto() {
        return count_valid_upto;
    }

    public void setCount_valid_upto(Date count_valid_upto) {
        this.count_valid_upto = count_valid_upto;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public String getMaker_name() {
        return maker_name;
    }

    public void setMaker_name(String maker_name) {
        this.maker_name = maker_name;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public int getNo_of_trips() {
        return no_of_trips;
    }

    public void setNo_of_trips(int no_of_trips) {
        this.no_of_trips = no_of_trips;
    }

    public Date getCount_valid_from() {
        return count_valid_from;
    }

    public void setCount_valid_from(Date count_valid_from) {
        this.count_valid_from = count_valid_from;
    }

    public String getState_to() {
        return state_to;
    }

    public void setState_to(String state_to) {
        this.state_to = state_to;
    }

    public int getOff_cd_to() {
        return off_cd_to;
    }

    public void setOff_cd_to(int off_cd_to) {
        this.off_cd_to = off_cd_to;
    }

    public int getPmt_catg() {
        return pmt_catg;
    }

    public void setPmt_catg(int pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    public String getOff_to_name() {
        return off_to_name;
    }

    public void setOff_to_name(String off_to_name) {
        this.off_to_name = off_to_name;
    }

    public String getCs_auth_no() {
        return cs_auth_no;
    }

    public void setCs_auth_no(String cs_auth_no) {
        this.cs_auth_no = cs_auth_no;
    }
}
