/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nic.vahan.form.bean.TaxFormPanelBean;

public class OwnerDetailsDobj implements Serializable {

    private String state_cd;
    private String state_name;
    private int off_cd;
    private String off_name;
    private String regn_no;
    private String regn_dt;
    private String regnDateDescr;
    private String purchase_dt;
    private Date purchase_date;
    private String purchaseDateDescr;
    private int owner_sr;
    private String owner_name;
    private String f_name;
    private String c_add1;
    private String c_add2;
    private String c_add3;
    private int c_district;
    private String c_district_name;
    private String c_state;
    private String c_state_name;
    private int c_pincode;
    private String p_add1;
    private String p_add2;
    private String p_add3;
    private int p_district;
    private String p_district_name;
    private String p_state;
    private String p_state_name;
    private int p_pincode;
    private int owner_cd;
    private String owner_cd_descr;
    private String regn_type;
    private String regn_type_descr;
    private int vh_class;
    private String vh_class_desc;
    private String chasi_no;
    private String eng_no;
    private int maker;
    private String maker_name;
    private String model_cd;
    private String model_name;
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
    private String fuel_descr;
    private String color;
    private int manu_mon;
    private int manu_yr;
    private int norms;
    private String norms_descr;
    private int wheelbase;
    private Float cubic_cap;
    private Float floor_area;
    private String ac_fitted;
    private String audio_fitted;
    private String video_fitted;
    private String vch_purchase_as;
    private String vch_catg;
    private String dealer_cd;
    private String dlr_name;
    private String dlr_add1;
    private String dlr_add2;
    private String dlr_add3;
    private String dlr_city;
    private String dlr_district;
    private String dlr_pincode;
    private int sale_amt;
    private String laser_code;
    private String garage_add;
    private int length;
    private int width;
    private int height;
    private String regn_upto;
    private String regnUptoDescr;
    private String fit_upto;
    private String fitUptoDescr;
    private int annual_income;
    private String op_dt;
    private String imported_vch;
    private int other_criteria;
    private String status;
    private int vehType;
    private String vehTypeDescr;
    private OwnerIdentificationDobj ownerIdentity;
    private InsDobj insDobj;
    private BlackListedVehicleDobj blackListedVehicleDobj;
    private String vch_purchase_as_code;
    private String vch_type;
    private int pmt_type;
    private int pmt_catg;
    private String rqrd_tax_modes;
    private List permitTypeList = new ArrayList();
    private List PermitCategoryList = new ArrayList();
    private List<TaxFormPanelBean> taxModList = new ArrayList();
    private boolean vehAgeExpire;
    private Owner_temp_dobj ownerTempDobj;
    private boolean renderedGCW;
    private boolean pushBkSeatRender = false;
    private int push_bk_seat = 0;
    private int ordinary_seat = 0;
    private String statusDescr;
    private String lastRcptDt;
    private int numberOfTyres;
    private FitnessDobj fitnessDobj;
    private String moved_on;
    private List<Trailer_dobj> listTrailerDobj;
    private AxleDetailsDobj axleDobj;
    private String modelNameOnTAC = "";
    private String applNo = "";
    private HpaDobj hpaDobj;
    private String moveHPADetails = "false";

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
     * @return the off_name
     */
    public String getOff_name() {
        return off_name;
    }

    /**
     * @param off_name the off_name to set
     */
    public void setOff_name(String off_name) {
        this.off_name = off_name;
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
     * @return the purchase_dt
     */
    public String getPurchase_dt() {
        return purchase_dt;
    }

    /**
     * @param purchase_dt the purchase_dt to set
     */
    public void setPurchase_dt(String purchase_dt) {
        this.purchase_dt = purchase_dt;
    }

    /**
     * @return the owner_sr
     */
    public int getOwner_sr() {
        return owner_sr;
    }

    /**
     * @param owner_sr the owner_sr to set
     */
    public void setOwner_sr(int owner_sr) {
        this.owner_sr = owner_sr;
    }

    /**
     * @return the owner_name
     */
    public String getOwner_name() {
        return owner_name;
    }

    /**
     * @param owner_name the owner_name to set
     */
    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
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
     * @return the c_add1
     */
    public String getC_add1() {
        return c_add1;
    }

    /**
     * @param c_add1 the c_add1 to set
     */
    public void setC_add1(String c_add1) {
        this.c_add1 = c_add1;
    }

    /**
     * @return the c_add2
     */
    public String getC_add2() {
        return c_add2;
    }

    /**
     * @param c_add2 the c_add2 to set
     */
    public void setC_add2(String c_add2) {
        this.c_add2 = c_add2;
    }

    /**
     * @return the c_add3
     */
    public String getC_add3() {
        return c_add3;
    }

    /**
     * @param c_add3 the c_add3 to set
     */
    public void setC_add3(String c_add3) {
        this.c_add3 = c_add3;
    }

    /**
     * @return the c_district
     */
    public int getC_district() {
        return c_district;
    }

    /**
     * @param c_district the c_district to set
     */
    public void setC_district(int c_district) {
        this.c_district = c_district;
    }

    /**
     * @return the c_district_name
     */
    public String getC_district_name() {
        return c_district_name;
    }

    /**
     * @param c_district_name the c_district_name to set
     */
    public void setC_district_name(String c_district_name) {
        this.c_district_name = c_district_name;
    }

    /**
     * @return the c_state
     */
    public String getC_state() {
        return c_state;
    }

    /**
     * @param c_state the c_state to set
     */
    public void setC_state(String c_state) {
        this.c_state = c_state;
    }

    /**
     * @return the c_state_name
     */
    public String getC_state_name() {
        return c_state_name;
    }

    /**
     * @param c_state_name the c_state_name to set
     */
    public void setC_state_name(String c_state_name) {
        this.c_state_name = c_state_name;
    }

    /**
     * @return the c_pincode
     */
    public int getC_pincode() {
        return c_pincode;
    }

    /**
     * @param c_pincode the c_pincode to set
     */
    public void setC_pincode(int c_pincode) {
        this.c_pincode = c_pincode;
    }

    /**
     * @return the p_add1
     */
    public String getP_add1() {
        return p_add1;
    }

    /**
     * @param p_add1 the p_add1 to set
     */
    public void setP_add1(String p_add1) {
        this.p_add1 = p_add1;
    }

    /**
     * @return the p_add2
     */
    public String getP_add2() {
        return p_add2;
    }

    /**
     * @param p_add2 the p_add2 to set
     */
    public void setP_add2(String p_add2) {
        this.p_add2 = p_add2;
    }

    /**
     * @return the p_add3
     */
    public String getP_add3() {
        return p_add3;
    }

    /**
     * @param p_add3 the p_add3 to set
     */
    public void setP_add3(String p_add3) {
        this.p_add3 = p_add3;
    }

    /**
     * @return the p_district
     */
    public int getP_district() {
        return p_district;
    }

    /**
     * @param p_district the p_district to set
     */
    public void setP_district(int p_district) {
        this.p_district = p_district;
    }

    /**
     * @return the p_district_name
     */
    public String getP_district_name() {
        return p_district_name;
    }

    /**
     * @param p_district_name the p_district_name to set
     */
    public void setP_district_name(String p_district_name) {
        this.p_district_name = p_district_name;
    }

    /**
     * @return the p_state
     */
    public String getP_state() {
        return p_state;
    }

    /**
     * @param p_state the p_state to set
     */
    public void setP_state(String p_state) {
        this.p_state = p_state;
    }

    /**
     * @return the p_state_name
     */
    public String getP_state_name() {
        return p_state_name;
    }

    /**
     * @param p_state_name the p_state_name to set
     */
    public void setP_state_name(String p_state_name) {
        this.p_state_name = p_state_name;
    }

    /**
     * @return the p_pincode
     */
    public int getP_pincode() {
        return p_pincode;
    }

    /**
     * @param p_pincode the p_pincode to set
     */
    public void setP_pincode(int p_pincode) {
        this.p_pincode = p_pincode;
    }

    /**
     * @return the owner_cd
     */
    public int getOwner_cd() {
        return owner_cd;
    }

    /**
     * @param owner_cd the owner_cd to set
     */
    public void setOwner_cd(int owner_cd) {
        this.owner_cd = owner_cd;
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
     * @return the vh_class_desc
     */
    public String getVh_class_desc() {
        return vh_class_desc;
    }

    /**
     * @param vh_class_desc the vh_class_desc to set
     */
    public void setVh_class_desc(String vh_class_desc) {
        this.vh_class_desc = vh_class_desc;
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
     * @return the maker_name
     */
    public String getMaker_name() {
        return maker_name;
    }

    /**
     * @param maker_name the maker_name to set
     */
    public void setMaker_name(String maker_name) {
        this.maker_name = maker_name;
    }

    /**
     * @return the model_cd
     */
    public String getModel_cd() {
        return model_cd;
    }

    /**
     * @param model_cd the model_cd to set
     */
    public void setModel_cd(String model_cd) {
        this.model_cd = model_cd;
    }

    /**
     * @return the model_name
     */
    public String getModel_name() {
        return model_name;
    }

    /**
     * @param model_name the model_name to set
     */
    public void setModel_name(String model_name) {
        this.model_name = model_name;
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
     * @return the fuel_descr
     */
    public String getFuel_descr() {
        return fuel_descr;
    }

    /**
     * @param fuel_descr the fuel_descr to set
     */
    public void setFuel_descr(String fuel_descr) {
        this.fuel_descr = fuel_descr;
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
    public int getManu_mon() {
        return manu_mon;
    }

    /**
     * @param manu_mon the manu_mon to set
     */
    public void setManu_mon(int manu_mon) {
        this.manu_mon = manu_mon;
    }

    /**
     * @return the manu_yr
     */
    public int getManu_yr() {
        return manu_yr;
    }

    /**
     * @param manu_yr the manu_yr to set
     */
    public void setManu_yr(int manu_yr) {
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
     * @return the norms_descr
     */
    public String getNorms_descr() {
        return norms_descr;
    }

    /**
     * @param norms_descr the norms_descr to set
     */
    public void setNorms_descr(String norms_descr) {
        this.norms_descr = norms_descr;
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
    public Float getCubic_cap() {
        return cubic_cap;
    }

    /**
     * @param cubic_cap the cubic_cap to set
     */
    public void setCubic_cap(Float cubic_cap) {
        this.cubic_cap = cubic_cap;
    }

    /**
     * @return the floor_area
     */
    public Float getFloor_area() {
        return floor_area;
    }

    /**
     * @param floor_area the floor_area to set
     */
    public void setFloor_area(Float floor_area) {
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
     * @return the dlr_name
     */
    public String getDlr_name() {
        return dlr_name;
    }

    /**
     * @param dlr_name the dlr_name to set
     */
    public void setDlr_name(String dlr_name) {
        this.dlr_name = dlr_name;
    }

    /**
     * @return the dlr_add1
     */
    public String getDlr_add1() {
        return dlr_add1;
    }

    /**
     * @param dlr_add1 the dlr_add1 to set
     */
    public void setDlr_add1(String dlr_add1) {
        this.dlr_add1 = dlr_add1;
    }

    /**
     * @return the dlr_add2
     */
    public String getDlr_add2() {
        return dlr_add2;
    }

    /**
     * @param dlr_add2 the dlr_add2 to set
     */
    public void setDlr_add2(String dlr_add2) {
        this.dlr_add2 = dlr_add2;
    }

    /**
     * @return the dlr_add3
     */
    public String getDlr_add3() {
        return dlr_add3;
    }

    /**
     * @param dlr_add3 the dlr_add3 to set
     */
    public void setDlr_add3(String dlr_add3) {
        this.dlr_add3 = dlr_add3;
    }

    /**
     * @return the dlr_city
     */
    public String getDlr_city() {
        return dlr_city;
    }

    /**
     * @param dlr_city the dlr_city to set
     */
    public void setDlr_city(String dlr_city) {
        this.dlr_city = dlr_city;
    }

    /**
     * @return the dlr_district
     */
    public String getDlr_district() {
        return dlr_district;
    }

    /**
     * @param dlr_district the dlr_district to set
     */
    public void setDlr_district(String dlr_district) {
        this.dlr_district = dlr_district;
    }

    /**
     * @return the dlr_pincode
     */
    public String getDlr_pincode() {
        return dlr_pincode;
    }

    /**
     * @param dlr_pincode the dlr_pincode to set
     */
    public void setDlr_pincode(String dlr_pincode) {
        this.dlr_pincode = dlr_pincode;
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
    public String getRegn_upto() {
        return regn_upto;
    }

    /**
     * @param regn_upto the regn_upto to set
     */
    public void setRegn_upto(String regn_upto) {
        this.regn_upto = regn_upto;
    }

    /**
     * @return the fit_upto
     */
    public String getFit_upto() {
        return fit_upto;
    }

    /**
     * @param fit_upto the fit_upto to set
     */
    public void setFit_upto(String fit_upto) {
        this.fit_upto = fit_upto;
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
     * @return the op_dt
     */
    public String getOp_dt() {
        return op_dt;
    }

    /**
     * @param op_dt the op_dt to set
     */
    public void setOp_dt(String op_dt) {
        this.op_dt = op_dt;
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
     * @return the vehType
     */
    public int getVehType() {
        return vehType;
    }

    /**
     * @param vehType the vehType to set
     */
    public void setVehType(int vehType) {
        this.vehType = vehType;
    }

    /**
     * @return the ownerIdentity
     */
    public OwnerIdentificationDobj getOwnerIdentity() {
        return ownerIdentity;
    }

    /**
     * @param ownerIdentity the ownerIdentity to set
     */
    public void setOwnerIdentity(OwnerIdentificationDobj ownerIdentity) {
        this.ownerIdentity = ownerIdentity;
    }

    /**
     * @return the regn_type_descr
     */
    public String getRegn_type_descr() {
        return regn_type_descr;
    }

    /**
     * @param regn_type_descr the regn_type_descr to set
     */
    public void setRegn_type_descr(String regn_type_descr) {
        this.regn_type_descr = regn_type_descr;
    }

    /**
     * @return the owner_cd_descr
     */
    public String getOwner_cd_descr() {
        return owner_cd_descr;
    }

    /**
     * @param owner_cd_descr the owner_cd_descr to set
     */
    public void setOwner_cd_descr(String owner_cd_descr) {
        this.owner_cd_descr = owner_cd_descr;
    }

    /**
     * @return the insDobj
     */
    public InsDobj getInsDobj() {
        return insDobj;
    }

    /**
     * @param insDobj the insDobj to set
     */
    public void setInsDobj(InsDobj insDobj) {
        this.insDobj = insDobj;
    }

    /**
     * @return the regnDateDescr
     */
    public String getRegnDateDescr() {
        return regnDateDescr;
    }

    /**
     * @param regnDateDescr the regnDateDescr to set
     */
    public void setRegnDateDescr(String regnDateDescr) {
        this.regnDateDescr = regnDateDescr;
    }

    /**
     * @return the purchaseDateDescr
     */
    public String getPurchaseDateDescr() {
        return purchaseDateDescr;
    }

    /**
     * @param purchaseDateDescr the purchaseDateDescr to set
     */
    public void setPurchaseDateDescr(String purchaseDateDescr) {
        this.purchaseDateDescr = purchaseDateDescr;
    }

    /**
     * @return the vch_purchase_as_code
     */
    public String getVch_purchase_as_code() {
        return vch_purchase_as_code;
    }

    /**
     * @param vch_purchase_as_code the vch_purchase_as_code to set
     */
    public void setVch_purchase_as_code(String vch_purchase_as_code) {
        this.vch_purchase_as_code = vch_purchase_as_code;
    }

    /**
     * @return the vehTypeDescr
     */
    public String getVehTypeDescr() {
        return vehTypeDescr;
    }

    /**
     * @param vehTypeDescr the vehTypeDescr to set
     */
    public void setVehTypeDescr(String vehTypeDescr) {
        this.vehTypeDescr = vehTypeDescr;
    }

    /**
     * @return the vch_type
     */
    public String getVch_type() {
        return vch_type;
    }

    /**
     * @param vch_type the vch_type to set
     */
    public void setVch_type(String vch_type) {
        this.vch_type = vch_type;
    }

    /**
     * @return the regnUptoDescr
     */
    public String getRegnUptoDescr() {
        return regnUptoDescr;
    }

    /**
     * @param regnUptoDescr the regnUptoDescr to set
     */
    public void setRegnUptoDescr(String regnUptoDescr) {
        this.regnUptoDescr = regnUptoDescr;
    }

    /**
     * @return the fitUptoDescr
     */
    public String getFitUptoDescr() {
        return fitUptoDescr;
    }

    /**
     * @param fitUptoDescr the fitUptoDescr to set
     */
    public void setFitUptoDescr(String fitUptoDescr) {
        this.fitUptoDescr = fitUptoDescr;
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
     * @return the rqrd_tax_modes
     */
    public String getRqrd_tax_modes() {
        return rqrd_tax_modes;
    }

    /**
     * @param rqrd_tax_modes the rqrd_tax_modes to set
     */
    public void setRqrd_tax_modes(String rqrd_tax_modes) {
        this.rqrd_tax_modes = rqrd_tax_modes;
    }

    /**
     * @return the permitTypeList
     */
    public List getPermitTypeList() {
        return permitTypeList;
    }

    /**
     * @param permitTypeList the permitTypeList to set
     */
    public void setPermitTypeList(List permitTypeList) {
        this.permitTypeList = permitTypeList;
    }

    /**
     * @return the PermitCategoryList
     */
    public List getPermitCategoryList() {
        return PermitCategoryList;
    }

    /**
     * @param PermitCategoryList the PermitCategoryList to set
     */
    public void setPermitCategoryList(List PermitCategoryList) {
        this.PermitCategoryList = PermitCategoryList;
    }

    /**
     * @return the taxModList
     */
    public List<TaxFormPanelBean> getTaxModList() {
        return taxModList;
    }

    /**
     * @param taxModList the taxModList to set
     */
    public void setTaxModList(List<TaxFormPanelBean> taxModList) {
        this.taxModList = taxModList;
    }

    /**
     * @return the purchase_date
     */
    public Date getPurchase_date() {
        return purchase_date;
    }

    /**
     * @param purchase_date the purchase_date to set
     */
    public void setPurchase_date(Date purchase_date) {
        this.purchase_date = purchase_date;
    }

    /**
     * @return the blackListedVehicleDobj
     */
    public BlackListedVehicleDobj getBlackListedVehicleDobj() {
        return blackListedVehicleDobj;
    }

    /**
     * @param blackListedVehicleDobj the blackListedVehicleDobj to set
     */
    public void setBlackListedVehicleDobj(BlackListedVehicleDobj blackListedVehicleDobj) {
        this.blackListedVehicleDobj = blackListedVehicleDobj;
    }

    /**
     * @return the vehAgeExpire
     */
    public boolean isVehAgeExpire() {
        return vehAgeExpire;
    }

    /**
     * @param vehAgeExpire the vehAgeExpire to set
     */
    public void setVehAgeExpire(boolean vehAgeExpire) {
        this.vehAgeExpire = vehAgeExpire;
    }

    /**
     * @return the ownerTempDobj
     */
    public Owner_temp_dobj getOwnerTempDobj() {
        return ownerTempDobj;
    }

    /**
     * @param ownerTempDobj the ownerTempDobj to set
     */
    public void setOwnerTempDobj(Owner_temp_dobj ownerTempDobj) {
        this.ownerTempDobj = ownerTempDobj;
    }

    public boolean isRenderedGCW() {
        return renderedGCW;
    }

    public void setRenderedGCW(boolean renderedGCW) {
        this.renderedGCW = renderedGCW;
    }

    public String getStatusDescr() {
        return statusDescr;
    }

    public void setStatusDescr(String statusDescr) {
        this.statusDescr = statusDescr;
    }

    /**
     * @return the lastRcptDt
     */
    public String getLastRcptDt() {
        return lastRcptDt;
    }

    /**
     * @param lastRcptDt the lastRcptDt to set
     */
    public void setLastRcptDt(String lastRcptDt) {
        this.lastRcptDt = lastRcptDt;
    }

    /**
     * @return the numberOfTyres
     */
    public int getNumberOfTyres() {
        return numberOfTyres;
    }

    /**
     * @param numberOfTyres the numberOfTyres to set
     */
    public void setNumberOfTyres(int numberOfTyres) {
        this.numberOfTyres = numberOfTyres;
    }

    /**
     * @return the fitnessDobj
     */
    public FitnessDobj getFitnessDobj() {
        return fitnessDobj;
    }

    /**
     * @param fitnessDobj the fitnessDobj to set
     */
    public void setFitnessDobj(FitnessDobj fitnessDobj) {
        this.fitnessDobj = fitnessDobj;
    }

    public String getMoved_on() {
        return moved_on;
    }

    public void setMoved_on(String moved_on) {
        this.moved_on = moved_on;
    }

    public int getPush_bk_seat() {
        return push_bk_seat;
    }

    public void setPush_bk_seat(int push_bk_seat) {
        this.push_bk_seat = push_bk_seat;
    }

    public int getOrdinary_seat() {
        return ordinary_seat;
    }

    public void setOrdinary_seat(int ordinary_seat) {
        this.ordinary_seat = ordinary_seat;
    }

    public boolean isPushBkSeatRender() {
        return pushBkSeatRender;
    }

    public void setPushBkSeatRender(boolean pushBkSeatRender) {
        this.pushBkSeatRender = pushBkSeatRender;
    }

    /**
     * @return the listTrailerDobj
     */
    public List<Trailer_dobj> getListTrailerDobj() {
        return listTrailerDobj;
    }

    /**
     * @param listTrailerDobj the listTrailerDobj to set
     */
    public void setListTrailerDobj(List<Trailer_dobj> listTrailerDobj) {
        this.listTrailerDobj = listTrailerDobj;
    }

    /**
     * @return the axleDobj
     */
    public AxleDetailsDobj getAxleDobj() {
        return axleDobj;
    }

    /**
     * @param axleDobj the axleDobj to set
     */
    public void setAxleDobj(AxleDetailsDobj axleDobj) {
        this.axleDobj = axleDobj;
    }

    /**
     * @return the modelNameOnTAC
     */
    public String getModelNameOnTAC() {
        return modelNameOnTAC;
    }

    /**
     * @param modelNameOnTAC the modelNameOnTAC to set
     */
    public void setModelNameOnTAC(String modelNameOnTAC) {
        this.modelNameOnTAC = modelNameOnTAC;
    }

    /**
     * @return the applNo
     */
    public String getApplNo() {
        return applNo;
    }

    /**
     * @param applNo the applNo to set
     */
    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    /**
     * @return the hpaDobj
     */
    public HpaDobj getHpaDobj() {
        return hpaDobj;
    }

    /**
     * @param hpaDobj the hpaDobj to set
     */
    public void setHpaDobj(HpaDobj hpaDobj) {
        this.hpaDobj = hpaDobj;
    }

    /**
     * @return the moveHPADetails
     */
    public String getMoveHPADetails() {
        return moveHPADetails;
    }

    /**
     * @param moveHPADetails the moveHPADetails to set
     */
    public void setMoveHPADetails(String moveHPADetails) {
        this.moveHPADetails = moveHPADetails;
    }
}
