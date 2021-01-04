/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ASHOK
 */
public class TaxBasedOnDobj implements Serializable {

    private String state_cd;
    private int off_cd;
    private String regn_no;
    private String rcpt_no;
    private Date regn_dt;
    private Date purchase_dt;
    private String regn_type;
    private int vh_class;
    private String chasi_no;
    private String eng_no;
    private int maker;
    private String maker_model;
    private String body_type;
    private int no_cyl;
    private Float hp;
    private int seat_cap;
    private int stand_cap;
    private int sleeper_cap;
    private int unld_wt;
    private int ld_wt;
    private int gcw;
    private int fuel;
    private String color;
    private Integer manu_mon;
    private Integer manu_yr;
    private int norms;
    private int wheelbase;
    private float cubic_cap;
    private float floor_area;
    private String ac_fitted;
    private String audio_fitted;
    private String video_fitted;
    private String vch_purchase_as;
    private String vch_catg;
    private String dealer_cd;
    private int sale_amt;
    private String laser_code;
    private String garage_add;
    private int length;
    private int width;
    private int height;
    private Date regn_upto;
    private Date fit_upto;
    private String tax_mode;
    private int annual_income;
    private String imported_vch = "N";
    private int other_criteria;
    private int fin_yr_sale_amt;
    private int pmt_type;
    private int pmt_catg;
    private int service_type;
    private int route_class;
    private int route_length;
    private int no_of_trips;
    private int domain_cd;
    private int distance_run_in_quarter;

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
     * @return the off_cd
     */
    public int getOff_cd() {
        return off_cd;
    }

    /**
     * @param off_cd the off_cd to set
     */
    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    /**
     * @return the rcpt_no
     */
    public String getRcpt_no() {
        return rcpt_no;
    }

    /**
     * @param rcpt_no the rcpt_no to set
     */
    public void setRcpt_no(String rcpt_no) {
        this.rcpt_no = rcpt_no;
    }

    /**
     * @return the regn_dt
     */
    public Date getRegn_dt() {
        return regn_dt;
    }

    /**
     * @param regn_dt the regn_dt to set
     */
    public void setRegn_dt(Date regn_dt) {
        this.regn_dt = regn_dt;
    }

    /**
     * @return the purchase_dt
     */
    public Date getPurchase_dt() {
        return purchase_dt;
    }

    /**
     * @param purchase_dt the purchase_dt to set
     */
    public void setPurchase_dt(Date purchase_dt) {
        this.purchase_dt = purchase_dt;
    }

    /**
     * @return the regn_type
     */
    public String getRegn_type() {
        return regn_type;
    }

    /**
     * @param regn_type the regn_type to set
     */
    public void setRegn_type(String regn_type) {
        this.regn_type = regn_type;
    }

    /**
     * @return the vh_class
     */
    public int getVh_class() {
        return vh_class;
    }

    /**
     * @param vh_class the vh_class to set
     */
    public void setVh_class(int vh_class) {
        this.vh_class = vh_class;
    }

    /**
     * @return the chasi_no
     */
    public String getChasi_no() {
        return chasi_no;
    }

    /**
     * @param chasi_no the chasi_no to set
     */
    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
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
     * @return the maker
     */
    public int getMaker() {
        return maker;
    }

    /**
     * @param maker the maker to set
     */
    public void setMaker(int maker) {
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
     * @return the no_cyl
     */
    public int getNo_cyl() {
        return no_cyl;
    }

    /**
     * @param no_cyl the no_cyl to set
     */
    public void setNo_cyl(int no_cyl) {
        this.no_cyl = no_cyl;
    }

    /**
     * @return the hp
     */
    public Float getHp() {
        return hp;
    }

    /**
     * @param hp the hp to set
     */
    public void setHp(Float hp) {
        this.hp = hp;
    }

    /**
     * @return the seat_cap
     */
    public int getSeat_cap() {
        return seat_cap;
    }

    /**
     * @param seat_cap the seat_cap to set
     */
    public void setSeat_cap(int seat_cap) {
        this.seat_cap = seat_cap;
    }

    /**
     * @return the stand_cap
     */
    public int getStand_cap() {
        return stand_cap;
    }

    /**
     * @param stand_cap the stand_cap to set
     */
    public void setStand_cap(int stand_cap) {
        this.stand_cap = stand_cap;
    }

    /**
     * @return the sleeper_cap
     */
    public int getSleeper_cap() {
        return sleeper_cap;
    }

    /**
     * @param sleeper_cap the sleeper_cap to set
     */
    public void setSleeper_cap(int sleeper_cap) {
        this.sleeper_cap = sleeper_cap;
    }

    /**
     * @return the unld_wt
     */
    public int getUnld_wt() {
        return unld_wt;
    }

    /**
     * @param unld_wt the unld_wt to set
     */
    public void setUnld_wt(int unld_wt) {
        this.unld_wt = unld_wt;
    }

    /**
     * @return the ld_wt
     */
    public int getLd_wt() {
        return ld_wt;
    }

    /**
     * @param ld_wt the ld_wt to set
     */
    public void setLd_wt(int ld_wt) {
        this.ld_wt = ld_wt;
    }

    /**
     * @return the gcw
     */
    public int getGcw() {
        return gcw;
    }

    /**
     * @param gcw the gcw to set
     */
    public void setGcw(int gcw) {
        this.gcw = gcw;
    }

    /**
     * @return the fuel
     */
    public int getFuel() {
        return fuel;
    }

    /**
     * @param fuel the fuel to set
     */
    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return the manu_mon
     */
    public Integer getManu_mon() {
        return manu_mon;
    }

    /**
     * @param manu_mon the manu_mon to set
     */
    public void setManu_mon(Integer manu_mon) {
        this.manu_mon = manu_mon;
    }

    /**
     * @return the manu_yr
     */
    public Integer getManu_yr() {
        return manu_yr;
    }

    /**
     * @param manu_yr the manu_yr to set
     */
    public void setManu_yr(Integer manu_yr) {
        this.manu_yr = manu_yr;
    }

    /**
     * @return the norms
     */
    public int getNorms() {
        return norms;
    }

    /**
     * @param norms the norms to set
     */
    public void setNorms(int norms) {
        this.norms = norms;
    }

    /**
     * @return the wheelbase
     */
    public int getWheelbase() {
        return wheelbase;
    }

    /**
     * @param wheelbase the wheelbase to set
     */
    public void setWheelbase(int wheelbase) {
        this.wheelbase = wheelbase;
    }

    /**
     * @return the cubic_cap
     */
    public float getCubic_cap() {
        return cubic_cap;
    }

    /**
     * @param cubic_cap the cubic_cap to set
     */
    public void setCubic_cap(float cubic_cap) {
        this.cubic_cap = cubic_cap;
    }

    /**
     * @return the floor_area
     */
    public float getFloor_area() {
        return floor_area;
    }

    /**
     * @param floor_area the floor_area to set
     */
    public void setFloor_area(float floor_area) {
        this.floor_area = floor_area;
    }

    /**
     * @return the ac_fitted
     */
    public String getAc_fitted() {
        return ac_fitted;
    }

    /**
     * @param ac_fitted the ac_fitted to set
     */
    public void setAc_fitted(String ac_fitted) {
        this.ac_fitted = ac_fitted;
    }

    /**
     * @return the audio_fitted
     */
    public String getAudio_fitted() {
        return audio_fitted;
    }

    /**
     * @param audio_fitted the audio_fitted to set
     */
    public void setAudio_fitted(String audio_fitted) {
        this.audio_fitted = audio_fitted;
    }

    /**
     * @return the video_fitted
     */
    public String getVideo_fitted() {
        return video_fitted;
    }

    /**
     * @param video_fitted the video_fitted to set
     */
    public void setVideo_fitted(String video_fitted) {
        this.video_fitted = video_fitted;
    }

    /**
     * @return the vch_purchase_as
     */
    public String getVch_purchase_as() {
        return vch_purchase_as;
    }

    /**
     * @param vch_purchase_as the vch_purchase_as to set
     */
    public void setVch_purchase_as(String vch_purchase_as) {
        this.vch_purchase_as = vch_purchase_as;
    }

    /**
     * @return the vch_catg
     */
    public String getVch_catg() {
        return vch_catg;
    }

    /**
     * @param vch_catg the vch_catg to set
     */
    public void setVch_catg(String vch_catg) {
        this.vch_catg = vch_catg;
    }

    /**
     * @return the dealer_cd
     */
    public String getDealer_cd() {
        return dealer_cd;
    }

    /**
     * @param dealer_cd the dealer_cd to set
     */
    public void setDealer_cd(String dealer_cd) {
        this.dealer_cd = dealer_cd;
    }

    /**
     * @return the sale_amt
     */
    public int getSale_amt() {
        return sale_amt;
    }

    /**
     * @param sale_amt the sale_amt to set
     */
    public void setSale_amt(int sale_amt) {
        this.sale_amt = sale_amt;
    }

    /**
     * @return the laser_code
     */
    public String getLaser_code() {
        return laser_code;
    }

    /**
     * @param laser_code the laser_code to set
     */
    public void setLaser_code(String laser_code) {
        this.laser_code = laser_code;
    }

    /**
     * @return the garage_add
     */
    public String getGarage_add() {
        return garage_add;
    }

    /**
     * @param garage_add the garage_add to set
     */
    public void setGarage_add(String garage_add) {
        this.garage_add = garage_add;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return the regn_upto
     */
    public Date getRegn_upto() {
        return regn_upto;
    }

    /**
     * @param regn_upto the regn_upto to set
     */
    public void setRegn_upto(Date regn_upto) {
        this.regn_upto = regn_upto;
    }

    /**
     * @return the fit_upto
     */
    public Date getFit_upto() {
        return fit_upto;
    }

    /**
     * @param fit_upto the fit_upto to set
     */
    public void setFit_upto(Date fit_upto) {
        this.fit_upto = fit_upto;
    }

    /**
     * @return the tax_mode
     */
    public String getTax_mode() {
        return tax_mode;
    }

    /**
     * @param tax_mode the tax_mode to set
     */
    public void setTax_mode(String tax_mode) {
        this.tax_mode = tax_mode;
    }

    /**
     * @return the annual_income
     */
    public int getAnnual_income() {
        return annual_income;
    }

    /**
     * @param annual_income the annual_income to set
     */
    public void setAnnual_income(int annual_income) {
        this.annual_income = annual_income;
    }

    /**
     * @return the imported_vch
     */
    public String getImported_vch() {
        return imported_vch;
    }

    /**
     * @param imported_vch the imported_vch to set
     */
    public void setImported_vch(String imported_vch) {
        this.imported_vch = imported_vch;
    }

    /**
     * @return the other_criteria
     */
    public int getOther_criteria() {
        return other_criteria;
    }

    /**
     * @param other_criteria the other_criteria to set
     */
    public void setOther_criteria(int other_criteria) {
        this.other_criteria = other_criteria;
    }

    /**
     * @return the fin_yr_sale_amt
     */
    public int getFin_yr_sale_amt() {
        return fin_yr_sale_amt;
    }

    /**
     * @param fin_yr_sale_amt the fin_yr_sale_amt to set
     */
    public void setFin_yr_sale_amt(int fin_yr_sale_amt) {
        this.fin_yr_sale_amt = fin_yr_sale_amt;
    }

    /**
     * @return the pmt_type
     */
    public int getPmt_type() {
        return pmt_type;
    }

    /**
     * @param pmt_type the pmt_type to set
     */
    public void setPmt_type(int pmt_type) {
        this.pmt_type = pmt_type;
    }

    /**
     * @return the pmt_catg
     */
    public int getPmt_catg() {
        return pmt_catg;
    }

    /**
     * @param pmt_catg the pmt_catg to set
     */
    public void setPmt_catg(int pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    /**
     * @return the service_type
     */
    public int getService_type() {
        return service_type;
    }

    /**
     * @param service_type the service_type to set
     */
    public void setService_type(int service_type) {
        this.service_type = service_type;
    }

    /**
     * @return the route_class
     */
    public int getRoute_class() {
        return route_class;
    }

    /**
     * @param route_class the route_class to set
     */
    public void setRoute_class(int route_class) {
        this.route_class = route_class;
    }

    /**
     * @return the route_length
     */
    public int getRoute_length() {
        return route_length;
    }

    /**
     * @param route_length the route_length to set
     */
    public void setRoute_length(int route_length) {
        this.route_length = route_length;
    }

    /**
     * @return the no_of_trips
     */
    public int getNo_of_trips() {
        return no_of_trips;
    }

    /**
     * @param no_of_trips the no_of_trips to set
     */
    public void setNo_of_trips(int no_of_trips) {
        this.no_of_trips = no_of_trips;
    }

    /**
     * @return the domain_cd
     */
    public int getDomain_cd() {
        return domain_cd;
    }

    /**
     * @param domain_cd the domain_cd to set
     */
    public void setDomain_cd(int domain_cd) {
        this.domain_cd = domain_cd;
    }

    /**
     * @return the distance_run_in_quarter
     */
    public int getDistance_run_in_quarter() {
        return distance_run_in_quarter;
    }

    /**
     * @param distance_run_in_quarter the distance_run_in_quarter to set
     */
    public void setDistance_run_in_quarter(int distance_run_in_quarter) {
        this.distance_run_in_quarter = distance_run_in_quarter;
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
}
