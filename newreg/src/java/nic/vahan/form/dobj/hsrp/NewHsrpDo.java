/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.hsrp;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author tranC087
 */
public class NewHsrpDo implements Serializable, Cloneable {
    
    private String state_cd;
    private String state_name;
    private String rto_cd;
    private String rto_name;
    private String regn_no;
    private String regn_dt;
    private String vh_class;
    private String O_name;
    private String eng_no;
    private String chasis_no;
    private String f_name;
    private String Add1;
    private String Add22;
    private String city;
    private String pincode;
    private String manuf_month;
    private String manuf_year;
    private String maker;
    private String maker_model;
    private String seat_cap;
    private String stand_cap;
    private String ld_wt;
    private String unld_wt;
    private String body_type;
    private String tax_opt;
    private String status="";
    private String firno="";
    private String policestation="";
    private Date firDate= null;
     private HSRP_dobj hsrpInfodobj = new HSRP_dobj();

    /**
     * @return the state_cd
     */
    public String getState_cd() {
        return state_cd;
    }

    /**
     * @param state_cd the state_cd to set
     */
    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    /**
     * @return the state_name
     */
    public String getState_name() {
        return state_name;
    }

    /**
     * @param state_name the state_name to set
     */
    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    /**
     * @return the rto_cd
     */
    public String getRto_cd() {
        return rto_cd;
    }

    /**
     * @param rto_cd the rto_cd to set
     */
    public void setRto_cd(String rto_cd) {
        this.rto_cd = rto_cd;
    }

    /**
     * @return the rto_name
     */
    public String getRto_name() {
        return rto_name;
    }

    /**
     * @param rto_name the rto_name to set
     */
    public void setRto_name(String rto_name) {
        this.rto_name = rto_name;
    }

    /**
     * @return the regn_no
     */
    public String getRegn_no() {
        return regn_no;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    /**
     * @return the regn_dt
     */
    public String getRegn_dt() {
        return regn_dt;
    }

    /**
     * @param regn_dt the regn_dt to set
     */
    public void setRegn_dt(String regn_dt) {
        this.regn_dt = regn_dt;
    }

    /**
     * @return the vh_class
     */
    public String getVh_class() {
        return vh_class;
    }

    /**
     * @param vh_class the vh_class to set
     */
    public void setVh_class(String vh_class) {
        this.vh_class = vh_class;
    }

    /**
     * @return the O_name
     */
    public String getO_name() {
        return O_name;
    }

    /**
     * @param O_name the O_name to set
     */
    public void setO_name(String O_name) {
        this.O_name = O_name;
    }

    /**
     * @return the eng_no
     */
    public String getEng_no() {
        return eng_no;
    }

    /**
     * @param eng_no the eng_no to set
     */
    public void setEng_no(String eng_no) {
        this.eng_no = eng_no;
    }

    /**
     * @return the chasis_no
     */
    public String getChasis_no() {
        return chasis_no;
    }

    /**
     * @param chasis_no the chasis_no to set
     */
    public void setChasis_no(String chasis_no) {
        this.chasis_no = chasis_no;
    }

    /**
     * @return the f_name
     */
    public String getF_name() {
        return f_name;
    }

    /**
     * @param f_name the f_name to set
     */
    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    /**
     * @return the Add1
     */
    public String getAdd1() {
        return Add1;
    }

    /**
     * @param Add1 the Add1 to set
     */
    public void setAdd1(String Add1) {
        this.Add1 = Add1;
    }

    /**
     * @return the Add22
     */
    public String getAdd22() {
        return Add22;
    }

    /**
     * @param Add22 the Add22 to set
     */
    public void setAdd22(String Add22) {
        this.Add22 = Add22;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the pincode
     */
    public String getPincode() {
        return pincode;
    }

    /**
     * @param pincode the pincode to set
     */
    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    /**
     * @return the manuf_month
     */
    public String getManuf_month() {
        return manuf_month;
    }

    /**
     * @param manuf_month the manuf_month to set
     */
    public void setManuf_month(String manuf_month) {
        this.manuf_month = manuf_month;
    }

    /**
     * @return the manuf_year
     */
    public String getManuf_year() {
        return manuf_year;
    }

    /**
     * @param manuf_year the manuf_year to set
     */
    public void setManuf_year(String manuf_year) {
        this.manuf_year = manuf_year;
    }

    /**
     * @return the maker
     */
    public String getMaker() {
        return maker;
    }

    /**
     * @param maker the maker to set
     */
    public void setMaker(String maker) {
        this.maker = maker;
    }

    /**
     * @return the maker_model
     */
    public String getMaker_model() {
        return maker_model;
    }

    /**
     * @param maker_model the maker_model to set
     */
    public void setMaker_model(String maker_model) {
        this.maker_model = maker_model;
    }

    /**
     * @return the seat_cap
     */
    public String getSeat_cap() {
        return seat_cap;
    }

    /**
     * @param seat_cap the seat_cap to set
     */
    public void setSeat_cap(String seat_cap) {
        this.seat_cap = seat_cap;
    }

    /**
     * @return the stand_cap
     */
    public String getStand_cap() {
        return stand_cap;
    }

    /**
     * @param stand_cap the stand_cap to set
     */
    public void setStand_cap(String stand_cap) {
        this.stand_cap = stand_cap;
    }

    /**
     * @return the ld_wt
     */
    public String getLd_wt() {
        return ld_wt;
    }

    /**
     * @param ld_wt the ld_wt to set
     */
    public void setLd_wt(String ld_wt) {
        this.ld_wt = ld_wt;
    }

    /**
     * @return the unld_wt
     */
    public String getUnld_wt() {
        return unld_wt;
    }

    /**
     * @param unld_wt the unld_wt to set
     */
    public void setUnld_wt(String unld_wt) {
        this.unld_wt = unld_wt;
    }

    /**
     * @return the body_type
     */
    public String getBody_type() {
        return body_type;
    }

    /**
     * @param body_type the body_type to set
     */
    public void setBody_type(String body_type) {
        this.body_type = body_type;
    }

    /**
     * @return the tax_opt
     */
    public String getTax_opt() {
        return tax_opt;
    }

    /**
     * @param tax_opt the tax_opt to set
     */
    public void setTax_opt(String tax_opt) {
        this.tax_opt = tax_opt;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the hsrpInfodobj
     */
    public HSRP_dobj getHsrpInfodobj() {
        return hsrpInfodobj;
    }

    /**
     * @param hsrpInfodobj the hsrpInfodobj to set
     */
    public void setHsrpInfodobj(HSRP_dobj hsrpInfodobj) {
        this.hsrpInfodobj = hsrpInfodobj;
    }

    /**
     * @return the firno
     */
    public String getFirno() {
        return firno;
    }

    /**
     * @param firno the firno to set
     */
    public void setFirno(String firno) {
        this.firno = firno;
    }

    /**
     * @return the policestation
     */
    public String getPolicestation() {
        return policestation;
    }

    /**
     * @param policestation the policestation to set
     */
    public void setPolicestation(String policestation) {
        this.policestation = policestation;
    }

    /**
     * @return the firDate
     */
    public Date getFirDate() {
        return firDate;
    }

    /**
     * @param firDate the firDate to set
     */
    public void setFirDate(Date firDate) {
        this.firDate = firDate;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
}
