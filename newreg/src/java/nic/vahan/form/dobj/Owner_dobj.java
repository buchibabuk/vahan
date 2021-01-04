/*
 * To change this template; choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nic.vahan.form.bean.TaxFormPanelBean;
import nic.vahan.form.bean.fancy.RetenRegnNo_dobj;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;

public class Owner_dobj implements Serializable, Cloneable {

    private String state_cd;
    private int off_cd;
    private String appl_no;
    private String regn_no;
    private Date regn_dt;
    private Date purchase_dt;
    private int owner_sr;
    private String owner_name;
    private String f_name;
    private String c_add1;
    private String c_add2;
    private String c_add3;
    private int c_district;
    private int c_pincode;
    private String c_state;
    private String p_add1;
    private String p_add2;
    private String p_add3;
    private int p_district;
    private int p_pincode;
    private String p_state;
    private int owner_cd;
    private String regn_type = "";
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
    private String ac_fitted = "";
    private String audio_fitted = "";
    private String video_fitted = "";
    private String vch_purchase_as = "";
    private String vch_catg = "";
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
    private String imported_vch;
    private int other_criteria;
    private String status;
    private String op_dt;
    private String c_state_name;
    private String c_district_name;
    private String p_district_name;
    private String p_state_name;
    private String fuel_descr;
    private Date fit_dt = new Date();
    private boolean hypothecatedFlag;
    private OwnerIdentificationDobj owner_identity;
    private Owner_temp_dobj dob_temp;
    private AxleDetailsDobj axleDobj;
    private TempRegDobj tempReg;
    private OtherStateVehDobj otherStateVehDobj;
    private CdDobj cdDobj;
    private String featureCode;
    private String colorCode;
    private List<HpaDobj> listHpaDobj;
    private InsDobj insDobj;
    private Trailer_dobj trailerDobj;
    private ExArmyDobj exArmy_dobj;
    private ImportedVehicleDobj imp_Dobj;
    private RetroFittingDetailsDobj cng_dobj;
    private AdvanceRegnNo_dobj advanceRegNoDobj = null;
    private RetenRegnNo_dobj retenRegNoDobj = null;
    private ScrappedVehicleDobj scrappedVehicleDobj = null;
    private BlackListedVehicleDobj blackListedVehicleDobj;
    private int pmt_type = -1;
    private int pmt_catg = -1;
    private String rqrd_tax_modes;
    private List permitTypeList = new ArrayList();
    private List PermitCategoryList = new ArrayList();
    private List<TaxFormPanelBean> taxModList = new ArrayList();
    private int vehType;
    private String linkedRegnNo;
    private SpeedGovernorDobj speedGovernorDobj = null;
    private boolean vehAgeExpire;
    private Date currentDate = new Date();
    private FitnessDobj fitnessDobj;
    private InspectionDobj inspectionDobj;
    private List speedGovernerList;
    private OwnerDetailsDobj ownerDetailsDo = null;
    private String newLoiNo = null;
    private int numberOfTyres;
    private ReflectiveTapeDobj reflectiveTapeDobj = null;
    private int push_bk_seat;
    private int ordinary_seat;
    private List<Trailer_dobj> listTrailerDobj;
    private String homoVchClass;
    //addamit
    private boolean insertUpdateFlag;
    private boolean insertUpdateHpaFlag;
    private boolean deleteUpdateHpaFlag;
    private boolean deleteUpdaeOtherStateFlag;
    private boolean tempRegndeleteFlag;
    private boolean axelDeleteFlag;
    private boolean exArmyDeleteFlag;
    private boolean importedVehDeleteFlag;
    private boolean retrofitDeleteFlag;
    private boolean speedGovDeleteFlag;
    private boolean reflectiveTapeDeleteFlag;
    private boolean checkVaownerTempFlag;
    private boolean trailerDeleteFlag;
    private String modelManuLocCode;
    private String modelManuLocCodeDescr;
    private boolean insertDeletelinkVehicle;
    private String modelNameOnTAC = "";
    private VehicleTrackingDetailsDobj vehicleTrackingDetailsDobj = null;
    //add for service type
    private String servicesType;
    private List pmtServiceTypeList = new ArrayList();
    private TmConfigurationSwappingDobj tmConfSwappingDobj;
    private boolean advanceOrRetenNoSelected = false;
    private AuctionDobj auctionDobj;
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    private String feeAction;
    private Integer UserOffCd;
    private int purCD;
    private boolean hypo;
    private int actionCd;
    private String empCode;
    private String userCatg;
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////    
    private String[] region_covered;
    private String region_covered_str = "";
    private List regionList = new ArrayList();
    private String fasTagId;

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
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
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
        if (hp == null) {
            this.hp = 0f;
        } else {
            this.hp = hp;
        }
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
    public Float getCubic_cap() {
        return cubic_cap;
    }

    /**
     * @param cubic_cap the cubic_cap to set
     */
    public void setCubic_cap(Float cubic_cap) {
        if (cubic_cap == null) {
            this.cubic_cap = 0f;
        } else {
            this.cubic_cap = cubic_cap;
        }
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
     * @return the fit_dt
     */
    public Date getFit_dt() {
        return fit_dt;
    }

    /**
     * @param fit_dt the fit_dt to set
     */
    public void setFit_dt(Date fit_dt) {
        this.fit_dt = fit_dt;
    }

    /**
     * @return the hypothecatedFlag
     */
    public boolean isHypothecatedFlag() {
        return hypothecatedFlag;
    }

    /**
     * @param hypothecatedFlag the hypothecatedFlag to set
     */
    public void setHypothecatedFlag(boolean hypothecatedFlag) {
        this.hypothecatedFlag = hypothecatedFlag;
    }

    /**
     * @return the owner_identity
     */
    public OwnerIdentificationDobj getOwner_identity() {
        return owner_identity;
    }

    /**
     * @param owner_identity the owner_identity to set
     */
    public void setOwner_identity(OwnerIdentificationDobj owner_identity) {
        this.owner_identity = owner_identity;
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
     * @return the dob_temp
     */
    public Owner_temp_dobj getDob_temp() {
        return dob_temp;
    }

    /**
     * @param dob_temp the dob_temp to set
     */
    public void setDob_temp(Owner_temp_dobj dob_temp) {
        this.dob_temp = dob_temp;
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
     * @return the tempReg
     */
    public TempRegDobj getTempReg() {
        return tempReg;
    }

    /**
     * @param tempReg the tempReg to set
     */
    public void setTempReg(TempRegDobj tempReg) {
        this.tempReg = tempReg;
    }

    /**
     * @return the otherStateVehDobj
     */
    public OtherStateVehDobj getOtherStateVehDobj() {
        return otherStateVehDobj;
    }

    /**
     * @param otherStateVehDobj the otherStateVehDobj to set
     */
    public void setOtherStateVehDobj(OtherStateVehDobj otherStateVehDobj) {
        this.otherStateVehDobj = otherStateVehDobj;
    }

    /**
     * @return the featureCode
     */
    public String getFeatureCode() {
        return featureCode;
    }

    /**
     * @param featureCode the featureCode to set
     */
    public void setFeatureCode(String featureCode) {
        this.featureCode = featureCode;
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
     * @return the exArmy_dobj
     */
    public ExArmyDobj getExArmy_dobj() {
        return exArmy_dobj;
    }

    /**
     * @param exArmy_dobj the exArmy_dobj to set
     */
    public void setExArmy_dobj(ExArmyDobj exArmy_dobj) {
        this.exArmy_dobj = exArmy_dobj;
    }

    /**
     * @return the imp_Dobj
     */
    public ImportedVehicleDobj getImp_Dobj() {
        return imp_Dobj;
    }

    /**
     * @param imp_Dobj the imp_Dobj to set
     */
    public void setImp_Dobj(ImportedVehicleDobj imp_Dobj) {
        this.imp_Dobj = imp_Dobj;
    }

    /**
     * @return the cng_dobj
     */
    public RetroFittingDetailsDobj getCng_dobj() {
        return cng_dobj;
    }

    /**
     * @param cng_dobj the cng_dobj to set
     */
    public void setCng_dobj(RetroFittingDetailsDobj cng_dobj) {
        this.cng_dobj = cng_dobj;
    }

    /**
     * @return the listHpaDobj
     */
    public List<HpaDobj> getListHpaDobj() {
        return listHpaDobj;
    }

    /**
     * @param listHpaDobj the listHpaDobj to set
     */
    public void setListHpaDobj(List<HpaDobj> listHpaDobj) {
        this.listHpaDobj = listHpaDobj;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the colorCode
     */
    public String getColorCode() {
        return colorCode;
    }

    /**
     * @param colorCode the colorCode to set
     */
    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    /**
     * @return the advanceRegNoDobj
     */
    public AdvanceRegnNo_dobj getAdvanceRegNoDobj() {
        return advanceRegNoDobj;
    }

    /**
     * @param advanceRegNoDobj the advanceRegNoDobj to set
     */
    public void setAdvanceRegNoDobj(AdvanceRegnNo_dobj advanceRegNoDobj) {
        this.advanceRegNoDobj = advanceRegNoDobj;
    }

    /**
     * @return the retenRegNoDobj
     */
    public RetenRegnNo_dobj getRetenRegNoDobj() {
        return retenRegNoDobj;
    }

    /**
     * @param retenRegNoDobj the retenRegNoDobj to set
     */
    public void setRetenRegNoDobj(RetenRegnNo_dobj retenRegNoDobj) {
        this.retenRegNoDobj = retenRegNoDobj;
    }

    /**
     * @return the cdDobj
     */
    public CdDobj getCdDobj() {
        return cdDobj;
    }

    /**
     * @param cdDobj the cdDobj to set
     */
    public void setCdDobj(CdDobj cdDobj) {
        this.cdDobj = cdDobj;
    }

    /**
     * @return the scrapDobj
     */
    public ScrappedVehicleDobj getScrappedVehicleDobj() {
        return scrappedVehicleDobj;
    }

    /**
     * @param scrapDobj the scrapDobj to set
     */
    public void setScrappedVehicleDobj(ScrappedVehicleDobj scrapDobj) {
        this.scrappedVehicleDobj = scrapDobj;
    }

    public int getPmt_catg() {
        return pmt_catg;
    }

    public void setPmt_catg(int pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    public int getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(int pmt_type) {
        this.pmt_type = pmt_type;
    }

    public List getPermitCategoryList() {
        return PermitCategoryList;
    }

    public void setPermitCategoryList(List PermitCategoryList) {
        this.PermitCategoryList = PermitCategoryList;
    }

    public List getPermitTypeList() {
        return permitTypeList;
    }

    public void setPermitTypeList(List permitTypeList) {
        this.permitTypeList = permitTypeList;
    }

    public List<TaxFormPanelBean> getTaxModList() {
        return taxModList;
    }

    public void setTaxModList(List<TaxFormPanelBean> taxModList) {
        this.taxModList = taxModList;
    }

    public void setRqrd_tax_modes(String rqrd_tax_modes) {
        this.rqrd_tax_modes = rqrd_tax_modes;
    }

    public String getRqrd_tax_modes() {
        return rqrd_tax_modes;
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
     * @return the linkedRegnNo
     */
    public String getLinkedRegnNo() {
        return linkedRegnNo;
    }

    /**
     * @param linkedRegnNo the linkedRegnNo to set
     */
    public void setLinkedRegnNo(String linkedRegnNo) {
        this.linkedRegnNo = linkedRegnNo;
    }

    /**
     * @return the speedGovernorDobj
     */
    public SpeedGovernorDobj getSpeedGovernorDobj() {
        return speedGovernorDobj;
    }

    /**
     * @param speedGovernorDobj the speedGovernorDobj to set
     */
    public void setSpeedGovernorDobj(SpeedGovernorDobj speedGovernorDobj) {
        this.speedGovernorDobj = speedGovernorDobj;
    }

    /**
     * @return the trailerDobj
     */
    public Trailer_dobj getTrailerDobj() {
        return trailerDobj;
    }

    /**
     * @param trailerDobj the trailerDobj to set
     */
    public void setTrailerDobj(Trailer_dobj trailerDobj) {
        this.trailerDobj = trailerDobj;
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
     * @return the currentDate
     */
    public Date getCurrentDate() {
        return currentDate;
    }

    /**
     * @param currentDate the currentDate to set
     */
    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
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

    /**
     * @return the inspectionDobj
     */
    public InspectionDobj getInspectionDobj() {
        return inspectionDobj;
    }

    /**
     * @param inspectionDobj the inspectionDobj to set
     */
    public void setInspectionDobj(InspectionDobj inspectionDobj) {
        this.inspectionDobj = inspectionDobj;
    }

    /**
     * @return the speedGovernerList
     */
    public List getSpeedGovernerList() {
        return speedGovernerList;
    }

    /**
     * @param speedGovernerList the speedGovernerList to set
     */
    public void setSpeedGovernerList(List speedGovernerList) {
        this.speedGovernerList = speedGovernerList;
    }

    /**
     * @return the ownerDetailsDo
     */
    public OwnerDetailsDobj getOwnerDetailsDo() {
        return ownerDetailsDo;
    }

    /**
     * @param ownerDetailsDo the ownerDetailsDo to set
     */
    public void setOwnerDetailsDo(OwnerDetailsDobj ownerDetailsDo) {
        this.ownerDetailsDo = ownerDetailsDo;
    }

    /**
     * @return the newLoiNo
     */
    public String getNewLoiNo() {
        return newLoiNo;
    }

    /**
     * @param newLoiNo the newLoiNo to set
     */
    public void setNewLoiNo(String newLoiNo) {
        this.newLoiNo = newLoiNo;
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
     * @return the reflectiveTapeDobj
     */
    public ReflectiveTapeDobj getReflectiveTapeDobj() {
        return reflectiveTapeDobj;
    }

    /**
     * @param reflectiveTapeDobj the reflectiveTapeDobj to set
     */
    public void setReflectiveTapeDobj(ReflectiveTapeDobj reflectiveTapeDobj) {
        this.reflectiveTapeDobj = reflectiveTapeDobj;
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
     * @return the homoVchClass
     */
    public String getHomoVchClass() {
        return homoVchClass;
    }

    /**
     * @param homoVchClass the homoVchClass to set
     */
    public void setHomoVchClass(String homoVchClass) {
        this.homoVchClass = homoVchClass;
    }

    public boolean isInsertUpdateFlag() {
        return insertUpdateFlag;
    }

    public void setInsertUpdateFlag(boolean insertUpdateFlag) {
        this.insertUpdateFlag = insertUpdateFlag;
    }

    public boolean isInsertUpdateHpaFlag() {
        return insertUpdateHpaFlag;
    }

    public void setInsertUpdateHpaFlag(boolean insertUpdateHpaFlag) {
        this.insertUpdateHpaFlag = insertUpdateHpaFlag;
    }

    public boolean isDeleteUpdateHpaFlag() {
        return deleteUpdateHpaFlag;
    }

    public void setDeleteUpdateHpaFlag(boolean deleteUpdateHpaFlag) {
        this.deleteUpdateHpaFlag = deleteUpdateHpaFlag;
    }

    public boolean isDeleteUpdaeOtherStateFlag() {
        return deleteUpdaeOtherStateFlag;
    }

    public void setDeleteUpdaeOtherStateFlag(boolean deleteUpdaeOtherStateFlag) {
        this.deleteUpdaeOtherStateFlag = deleteUpdaeOtherStateFlag;
    }

    public boolean isTempRegndeleteFlag() {
        return tempRegndeleteFlag;
    }

    public void setTempRegndeleteFlag(boolean tempRegndeleteFlag) {
        this.tempRegndeleteFlag = tempRegndeleteFlag;
    }

    public boolean isAxelDeleteFlag() {
        return axelDeleteFlag;
    }

    public void setAxelDeleteFlag(boolean axelDeleteFlag) {
        this.axelDeleteFlag = axelDeleteFlag;
    }

    public boolean isExArmyDeleteFlag() {
        return exArmyDeleteFlag;
    }

    public void setExArmyDeleteFlag(boolean exArmyDeleteFlag) {
        this.exArmyDeleteFlag = exArmyDeleteFlag;
    }

    public boolean isImportedVehDeleteFlag() {
        return importedVehDeleteFlag;
    }

    public void setImportedVehDeleteFlag(boolean importedVehDeleteFlag) {
        this.importedVehDeleteFlag = importedVehDeleteFlag;
    }

    public boolean isRetrofitDeleteFlag() {
        return retrofitDeleteFlag;
    }

    public void setRetrofitDeleteFlag(boolean retrofitDeleteFlag) {
        this.retrofitDeleteFlag = retrofitDeleteFlag;
    }

    public boolean isSpeedGovDeleteFlag() {
        return speedGovDeleteFlag;
    }

    public void setSpeedGovDeleteFlag(boolean speedGovDeleteFlag) {
        this.speedGovDeleteFlag = speedGovDeleteFlag;
    }

    public boolean isReflectiveTapeDeleteFlag() {
        return reflectiveTapeDeleteFlag;
    }

    public void setReflectiveTapeDeleteFlag(boolean reflectiveTapeDeleteFlag) {
        this.reflectiveTapeDeleteFlag = reflectiveTapeDeleteFlag;
    }

    public boolean isCheckVaownerTempFlag() {
        return checkVaownerTempFlag;
    }

    public void setCheckVaownerTempFlag(boolean checkVaownerTempFlag) {
        this.checkVaownerTempFlag = checkVaownerTempFlag;
    }

    public boolean isTrailerDeleteFlag() {
        return trailerDeleteFlag;
    }

    public void setTrailerDeleteFlag(boolean trailerDeleteFlag) {
        this.trailerDeleteFlag = trailerDeleteFlag;
    }

    /**
     * @return the modelManuLocCode
     */
    public String getModelManuLocCode() {
        return modelManuLocCode;
    }

    /**
     * @param modelManuLocCode the modelManuLocCode to set
     */
    public void setModelManuLocCode(String modelManuLocCode) {
        this.modelManuLocCode = modelManuLocCode;
    }

    /**
     * @return the modelManuLocCodeDescr
     */
    public String getModelManuLocCodeDescr() {
        return modelManuLocCodeDescr;
    }

    /**
     * @param modelManuLocCodeDescr the modelManuLocCodeDescr to set
     */
    public void setModelManuLocCodeDescr(String modelManuLocCodeDescr) {
        this.modelManuLocCodeDescr = modelManuLocCodeDescr;
    }

    public boolean isInsertDeletelinkVehicle() {
        return insertDeletelinkVehicle;
    }

    public void setInsertDeletelinkVehicle(boolean insertDeletelinkVehicle) {
        this.insertDeletelinkVehicle = insertDeletelinkVehicle;
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

    public VehicleTrackingDetailsDobj getVehicleTrackingDetailsDobj() {
        return vehicleTrackingDetailsDobj;
    }

    public void setVehicleTrackingDetailsDobj(VehicleTrackingDetailsDobj vehicleTrackingDetailsDobj) {
        this.vehicleTrackingDetailsDobj = vehicleTrackingDetailsDobj;
    }

    public List getPmtServiceTypeList() {
        return pmtServiceTypeList;
    }

    public void setPmtServiceTypeList(List pmtServiceTypeList) {
        this.pmtServiceTypeList = pmtServiceTypeList;
    }

    public String getServicesType() {
        return servicesType;
    }

    public void setServicesType(String servicesType) {
        this.servicesType = servicesType;
    }

    /**
     * @return the tmConfSwappingDobj
     */
    public TmConfigurationSwappingDobj getTmConfSwappingDobj() {
        return tmConfSwappingDobj;
    }

    /**
     * @param tmConfSwappingDobj the tmConfSwappingDobj to set
     */
    public void setTmConfSwappingDobj(TmConfigurationSwappingDobj tmConfSwappingDobj) {
        this.tmConfSwappingDobj = tmConfSwappingDobj;
    }

    /**
     * @return the advanceOrRetenNoSelected
     */
    public boolean isAdvanceOrRetenNoSelected() {
        return advanceOrRetenNoSelected;
    }

    /**
     * @param advanceOrRetenNoSelected the advanceOrRetenNoSelected to set
     */
    public void setAdvanceOrRetenNoSelected(boolean advanceOrRetenNoSelected) {
        this.advanceOrRetenNoSelected = advanceOrRetenNoSelected;
    }

    /**
     * @return the auctionDobj
     */
    public AuctionDobj getAuctionDobj() {
        return auctionDobj;
    }

    /**
     * @param auctionDobj the auctionDobj to set
     */
    public void setAuctionDobj(AuctionDobj auctionDobj) {
        this.auctionDobj = auctionDobj;
    }

    /**
     * @return the feeAction
     */
    public String getFeeAction() {
        return feeAction;
    }

    /**
     * @param feeAction the feeAction to set
     */
    public void setFeeAction(String feeAction) {
        this.feeAction = feeAction;
    }

    /**
     * @return the UserOffCd
     */
    public Integer getUserOffCd() {
        return UserOffCd;
    }

    /**
     * @param UserOffCd the UserOffCd to set
     */
    public void setUserOffCd(Integer UserOffCd) {
        this.UserOffCd = UserOffCd;
    }

    /**
     * @return the purCD
     */
    public int getPurCD() {
        return purCD;
    }

    /**
     * @param purCD the purCD to set
     */
    public void setPurCD(int purCD) {
        this.purCD = purCD;
    }

    /**
     * @return the hypo
     */
    public boolean isHypo() {
        return hypo;
    }

    /**
     * @param hypo the hypo to set
     */
    public void setHypo(boolean hypo) {
        this.hypo = hypo;
    }

    /**
     * @return the actionCd
     */
    public int getActionCd() {
        return actionCd;
    }

    /**
     * @param actionCd the actionCd to set
     */
    public void setActionCd(int actionCd) {
        this.actionCd = actionCd;
    }

    /**
     * @return the empCode
     */
    public String getEmpCode() {
        return empCode;
    }

    /**
     * @param empCode the empCode to set
     */
    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    /**
     * @return the userCatg
     */
    public String getUserCatg() {
        return userCatg;
    }

    /**
     * @param userCatg the userCatg to set
     */
    public void setUserCatg(String userCatg) {
        this.userCatg = userCatg;
    }

    public String[] getRegion_covered() {
        return region_covered;
    }

    public void setRegion_covered(String[] region_covered) {
        this.region_covered = region_covered;
    }

    public String getRegion_covered_str() {
        return region_covered_str;
    }

    public void setRegion_covered_str(String region_covered_str) {
        this.region_covered_str = region_covered_str;
    }

    public List getRegionList() {
        return regionList;
    }

    public void setRegionList(List regionList) {
        this.regionList = regionList;
    }

    public String getFasTagId() {
        return fasTagId;
    }

    public void setFasTagId(String fasTagId) {
        this.fasTagId = fasTagId;
    }
    
}
