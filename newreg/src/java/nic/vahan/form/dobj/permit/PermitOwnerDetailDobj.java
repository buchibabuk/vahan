package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.util.Date;

public class PermitOwnerDetailDobj implements Serializable, Cloneable {

    private String state_cd;
    private int off_cd;
    private String appl_no;
    private String chasi_no;
    private String regn_no;
    private String owner_name;
    private String f_name;
    private String c_add1;
    private String c_add2;
    private String c_add3;
    private int c_district = -1;
    private int c_pincode = 0;
    private String c_state;
    private String p_add1;
    private String p_add2;
    private String p_add3;
    private int p_district = -1;
    private int p_pincode = 0;
    private String p_state;
    private long mobile_no = 0L;
    private String email_id = "";
    private int vh_class = -1;
    private int seat_cap = 0;
    private int stand_cap;
    private int sleeper_cap;
    private int unld_wt = 0;
    private int ld_wt = 0;
    private int gcw = 0;
    boolean flage = true;
    private int fuelCd;
    private Date regnDt;
    private String engnNo;
    private String makerName;
    private String modelName;
    private String manuYearInString;
    private String regnDateInString;
    private String districtInString;
    private String currentDateInString;
    private Date replaceDateByVtOwner;
    private String previousOwnerName;
    private String previousFatherName;
    private String previousRegnNo;
    private int owner_catg = -1;
    private String vch_catg = "";
    private String ownerStatus;
    private String vh_class_desc = "";
    private String fuel_desc = "";
    private String body_type = "";
    private int owner_cd;
    private String dl_no = "";
    private String voter_id = "";
    private String norms;
    private int other_criteria;

    public void setDisable(boolean disableFalg) {
        this.setFlage(disableFalg);
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

    public String getC_add1() {
        return c_add1;
    }

    public void setC_add1(String c_add1) {
        this.c_add1 = c_add1;
    }

    public String getC_add2() {
        return c_add2;
    }

    public void setC_add2(String c_add2) {
        this.c_add2 = c_add2;
    }

    public String getC_add3() {
        return c_add3;
    }

    public void setC_add3(String c_add3) {
        this.c_add3 = c_add3;
    }

    public int getC_district() {
        return c_district;
    }

    public void setC_district(int c_district) {
        this.c_district = c_district;
    }

    public int getC_pincode() {
        return c_pincode;
    }

    public void setC_pincode(int c_pincode) {
        this.c_pincode = c_pincode;
    }

    public String getC_state() {
        return c_state;
    }

    public void setC_state(String c_state) {
        this.c_state = c_state;
    }

    public String getP_add1() {
        return p_add1;
    }

    public void setP_add1(String p_add1) {
        this.p_add1 = p_add1;
    }

    public String getP_add2() {
        return p_add2;
    }

    public void setP_add2(String p_add2) {
        this.p_add2 = p_add2;
    }

    public String getP_add3() {
        return p_add3;
    }

    public void setP_add3(String p_add3) {
        this.p_add3 = p_add3;
    }

    public int getP_district() {
        return p_district;
    }

    public void setP_district(int p_district) {
        this.p_district = p_district;
    }

    public int getP_pincode() {
        return p_pincode;
    }

    public void setP_pincode(int p_pincode) {
        this.p_pincode = p_pincode;
    }

    public String getP_state() {
        return p_state;
    }

    public void setP_state(String p_state) {
        this.p_state = p_state;
    }

    public long getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(long mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public int getVh_class() {
        return vh_class;
    }

    public void setVh_class(int vh_class) {
        this.vh_class = vh_class;
    }

    public int getSeat_cap() {
        return seat_cap;
    }

    public void setSeat_cap(int seat_cap) {
        this.seat_cap = seat_cap;
    }

    public int getUnld_wt() {
        return unld_wt;
    }

    public void setUnld_wt(int unld_wt) {
        this.unld_wt = unld_wt;
    }

    public int getLd_wt() {
        return ld_wt;
    }

    public void setLd_wt(int ld_wt) {
        this.ld_wt = ld_wt;
    }

    public boolean isFlage() {
        return flage;
    }

    public void setFlage(boolean flage) {
        this.flage = flage;
    }

    public String getChasi_no() {
        return chasi_no;
    }

    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
    }

    public int getFuelCd() {
        return fuelCd;
    }

    public void setFuelCd(int fuelCd) {
        this.fuelCd = fuelCd;
    }

    public Date getRegnDt() {
        return regnDt;
    }

    public void setRegnDt(Date regnDt) {
        this.regnDt = regnDt;
    }

    public String getEngnNo() {
        return engnNo;
    }

    public void setEngnNo(String engnNo) {
        this.engnNo = engnNo;
    }

    public String getMakerName() {
        return makerName;
    }

    public void setMakerName(String makerName) {
        this.makerName = makerName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getManuYearInString() {
        return manuYearInString;
    }

    public void setManuYearInString(String manuYearInString) {
        this.manuYearInString = manuYearInString;
    }

    public String getRegnDateInString() {
        return regnDateInString;
    }

    public void setRegnDateInString(String regnDateInString) {
        this.regnDateInString = regnDateInString;
    }

    public String getDistrictInString() {
        return districtInString;
    }

    public void setDistrictInString(String districtInString) {
        this.districtInString = districtInString;
    }

    public String getCurrentDateInString() {
        return currentDateInString;
    }

    public void setCurrentDateInString(String currentDateInString) {
        this.currentDateInString = currentDateInString;
    }

    public Date getReplaceDateByVtOwner() {
        return replaceDateByVtOwner;
    }

    public void setReplaceDateByVtOwner(Date replaceDateByVtOwner) {
        this.replaceDateByVtOwner = replaceDateByVtOwner;
    }

    public String getPreviousOwnerName() {
        return previousOwnerName;
    }

    public void setPreviousOwnerName(String previousOwnerName) {
        this.previousOwnerName = previousOwnerName;
    }

    public String getPreviousRegnNo() {
        return previousRegnNo;
    }

    public void setPreviousRegnNo(String previousRegnNo) {
        this.previousRegnNo = previousRegnNo;
    }

    public int getOwner_catg() {
        return owner_catg;
    }

    public void setOwner_catg(int owner_catg) {
        this.owner_catg = owner_catg;
    }

    public String getVch_catg() {
        return vch_catg;
    }

    public void setVch_catg(String vch_catg) {
        this.vch_catg = vch_catg;
    }

    public int getStand_cap() {
        return stand_cap;
    }

    public void setStand_cap(int stand_cap) {
        this.stand_cap = stand_cap;
    }

    public int getSleeper_cap() {
        return sleeper_cap;
    }

    public void setSleeper_cap(int sleeper_cap) {
        this.sleeper_cap = sleeper_cap;
    }

    public String getOwnerStatus() {
        return ownerStatus;
    }

    public void setOwnerStatus(String ownerStatus) {
        this.ownerStatus = ownerStatus;
    }

    public String getPreviousFatherName() {
        return previousFatherName;
    }

    public void setPreviousFatherName(String previousFatherName) {
        this.previousFatherName = previousFatherName;
    }

    public String getVh_class_desc() {
        return vh_class_desc;
    }

    public void setVh_class_desc(String vh_class_desc) {
        this.vh_class_desc = vh_class_desc;
    }

    public String getFuel_desc() {
        return fuel_desc;
    }

    public void setFuel_desc(String fuel_desc) {
        this.fuel_desc = fuel_desc;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public int getGcw() {
        return gcw;
    }

    public void setGcw(int gcw) {
        this.gcw = gcw;
    }

    public String getBody_type() {
        return body_type;
    }

    public void setBody_type(String body_type) {
        this.body_type = body_type;
    }

    public int getOwner_cd() {
        return owner_cd;
    }

    public void setOwner_cd(int owner_cd) {
        this.owner_cd = owner_cd;
    }

    public String getDl_no() {
        return dl_no;
    }

    public void setDl_no(String dl_no) {
        this.dl_no = dl_no;
    }

    public String getVoter_id() {
        return voter_id;
    }

    public void setVoter_id(String voter_id) {
        this.voter_id = voter_id;
    }

    public String getNorms() {
        return norms;
    }

    public void setNorms(String norms) {
        this.norms = norms;
    }

    public int getOther_criteria() {
        return other_criteria;
    }

    public void setOther_criteria(int other_criteria) {
        this.other_criteria = other_criteria;
    }
}
