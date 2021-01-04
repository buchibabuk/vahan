/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import nic.vahan.form.bean.permit.PermitRouteList;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitHomeAuthDobj;

/**
 *
 * @author nic
 */
public class VehicleParticularDobj implements Serializable {

    private String header;
    private String purpose;
    private String transfer_on;
    private String last_ca_on;
    private String last_alt_on;
    private String regn_no;
    private String regn_dt;
    private String owner_serial_no;
    private String owner_name;
    private String father_name;
    private String vhclass_descr;
    private String vehicle_maker;
    private String vehicle_model;
    private String body;
    private String manu_year;
    private String no_of_cylinder;
    private String chassis_no;
    private String engine_no;
    private String h_power;
    private String seat;
    private String stand_cap;
    private String sleep_cap;
    private String Unladen;
    private String Laden_wt;
    private String pr_regn_no;
    private String owner_dtls;
    private String fncr_dtls;
    private String val_upto;
    private String tax_upto;
    private String cubic_capacity;
    private String Color;
    private String Fuel;
    private String policy_no;
    private String ins_from;
    private String ins_upto;
    private String ins_company;
    private String ins_type_desc;
    private String mobile;
    private String email;
    private String fee;
    private String rcpt_dt;
    private String rcpt_no;
    private String off_name;
    private String state_name;
    private String nocNo;
    private String nocDt;
    private String offtoname;
    private String statetoname;
    private String comp_type;
    private String fir_no;
    private String fir_dt;
    private String complain;
    private String comp_dt;
    private String printed_on;
    private String appl_no;
    private String fit_upto;
    private PassengerPermitDetailDobj passPmtDobj = null;
    private PermitHomeAuthDobj authdobj = null;
    private String preOwner;
    private String oldState;
    private String preRegno;
    private String entryDate;
    private String conversionDate;
    private int tax_amt;
    private String vchNorms;
    private String vchStatus;
    private String vchStatusDescr;
    private String subHeader;
    private String floorArea;
    private String wheelBase;
    private String rc_cancel_dtls;
    private String rc_surr_dtls;
    private String hpt_dtls;
    private String kitManufactureDt;
    private String kitManufactureName;
    private String kitHydroDt;
    private String kitDesc;
    private String kitWorkName;
    private String tclNo;
    private String tclDt;
    private String kitNo;
    private String cylNo;
    private boolean isTransport;
    private String fdesc;
    private String rdesc;
    private String odesc;
    private String tdesc;
    private String fweight;
    private String rweight;
    private String oweight;
    private String tweight;
    private String printed_by;
    private String speed_governor_no;
    private boolean isspeed_governor;
    private String sg_fittedDate;
    private String vhaChangedDataAdminRemark;
    private boolean isAdminRemark;
    private String sg_manuName;
    private boolean isLPGDtls;
    private boolean isInsDtls;
    private boolean isHPDtls;
    private boolean isNOCDtls;
    private boolean isBlkDtls;
    private boolean isHPTDtls;
    private boolean isRCSurrDtls;
    private boolean isRCCancelDtls;
    private String linkVehNumber;
    private List<VehicleParticularDobj> hptSubList;
    private String permitAreaDtls;
    private List<PermitRouteList> areaActionSource = new ArrayList<>();
    private String permitFeeDtls;
    private String image_background;
    private String image_logo;
    private boolean show_image_background;
    private boolean show_image_logo;
    private boolean vehicleType;
    private boolean showRoadSafetySlogan;
    private VmRoadSafetySloganPrintDobj roadSafetySloganDobj = null;
    private String pucc_from;
    private String pucc_upto;

    /**
     * @return the transfer_on
     */
    public String getTransfer_on() {
        return transfer_on;
    }

    /**
     * @param transfer_on the transfer_on to set
     */
    public void setTransfer_on(String transfer_on) {
        this.transfer_on = transfer_on;
    }

    /**
     * @return the last_ca_on
     */
    public String getLast_ca_on() {
        return last_ca_on;
    }

    /**
     * @param last_ca_on the last_ca_on to set
     */
    public void setLast_ca_on(String last_ca_on) {
        this.last_ca_on = last_ca_on;
    }

    /**
     * @return the last_alt_on
     */
    public String getLast_alt_on() {
        return last_alt_on;
    }

    /**
     * @param last_alt_on the last_alt_on to set
     */
    public void setLast_alt_on(String last_alt_on) {
        this.last_alt_on = last_alt_on;
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
     * @return the owner_serial_no
     */
    public String getOwner_serial_no() {
        return owner_serial_no;
    }

    /**
     * @param owner_serial_no the owner_serial_no to set
     */
    public void setOwner_serial_no(String owner_serial_no) {
        this.owner_serial_no = owner_serial_no;
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
     * @return the father_name
     */
    public String getFather_name() {
        return father_name;
    }

    /**
     * @param father_name the father_name to set
     */
    public void setFather_name(String father_name) {
        this.father_name = father_name;
    }

    /**
     * @return the vhclass_descr
     */
    public String getVhclass_descr() {
        return vhclass_descr;
    }

    /**
     * @param vhclass_descr the vhclass_descr to set
     */
    public void setVhclass_descr(String vhclass_descr) {
        this.vhclass_descr = vhclass_descr;
    }

    /**
     * @return the vehicle_maker
     */
    public String getVehicle_maker() {
        return vehicle_maker;
    }

    /**
     * @param vehicle_maker the vehicle_maker to set
     */
    public void setVehicle_maker(String vehicle_maker) {
        this.vehicle_maker = vehicle_maker;
    }

    /**
     * @return the vehicle_model
     */
    public String getVehicle_model() {
        return vehicle_model;
    }

    /**
     * @param vehicle_model the vehicle_model to set
     */
    public void setVehicle_model(String vehicle_model) {
        this.vehicle_model = vehicle_model;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return the manu_year
     */
    public String getManu_year() {
        return manu_year;
    }

    /**
     * @param manu_year the manu_year to set
     */
    public void setManu_year(String manu_year) {
        this.manu_year = manu_year;
    }

    /**
     * @return the no_of_cylinder
     */
    public String getNo_of_cylinder() {
        return no_of_cylinder;
    }

    /**
     * @param no_of_cylinder the no_of_cylinder to set
     */
    public void setNo_of_cylinder(String no_of_cylinder) {
        this.no_of_cylinder = no_of_cylinder;
    }

    /**
     * @return the chassis_no
     */
    public String getChassis_no() {
        return chassis_no;
    }

    /**
     * @param chassis_no the chassis_no to set
     */
    public void setChassis_no(String chassis_no) {
        this.chassis_no = chassis_no;
    }

    /**
     * @return the engine_no
     */
    public String getEngine_no() {
        return engine_no;
    }

    /**
     * @param engine_no the engine_no to set
     */
    public void setEngine_no(String engine_no) {
        this.engine_no = engine_no;
    }

    /**
     * @return the h_power
     */
    public String getH_power() {
        return h_power;
    }

    /**
     * @param h_power the h_power to set
     */
    public void setH_power(String h_power) {
        this.h_power = h_power;
    }

    /**
     * @return the seat
     */
    public String getSeat() {
        return seat;
    }

    /**
     * @param seat the seat to set
     */
    public void setSeat(String seat) {
        this.seat = seat;
    }

    /**
     * @return the Unladen
     */
    public String getUnladen() {
        return Unladen;
    }

    /**
     * @param Unladen the Unladen to set
     */
    public void setUnladen(String Unladen) {
        this.Unladen = Unladen;
    }

    /**
     * @return the Laden_wt
     */
    public String getLaden_wt() {
        return Laden_wt;
    }

    /**
     * @param Laden_wt the Laden_wt to set
     */
    public void setLaden_wt(String Laden_wt) {
        this.Laden_wt = Laden_wt;
    }

    /**
     * @return the pr_regn_no
     */
    public String getPr_regn_no() {
        return pr_regn_no;
    }

    /**
     * @param pr_regn_no the pr_regn_no to set
     */
    public void setPr_regn_no(String pr_regn_no) {
        this.pr_regn_no = pr_regn_no;
    }

    /**
     * @return the owner_dtls
     */
    public String getOwner_dtls() {
        return owner_dtls;
    }

    /**
     * @param owner_dtls the owner_dtls to set
     */
    public void setOwner_dtls(String owner_dtls) {
        this.owner_dtls = owner_dtls;
    }

    /**
     * @return the fncr_dtls
     */
    public String getFncr_dtls() {
        return fncr_dtls;
    }

    /**
     * @param fncr_dtls the fncr_dtls to set
     */
    public void setFncr_dtls(String fncr_dtls) {
        this.fncr_dtls = fncr_dtls;
    }

    /**
     * @return the val_upto
     */
    public String getVal_upto() {
        return val_upto;
    }

    /**
     * @param val_upto the val_upto to set
     */
    public void setVal_upto(String val_upto) {
        this.val_upto = val_upto;
    }

    /**
     * @return the tax_upto
     */
    public String getTax_upto() {
        return tax_upto;
    }

    /**
     * @param tax_upto the tax_upto to set
     */
    public void setTax_upto(String tax_upto) {
        this.tax_upto = tax_upto;
    }

    /**
     * @return the cubic_capacity
     */
    public String getCubic_capacity() {
        return cubic_capacity;
    }

    /**
     * @param cubic_capacity the cubic_capacity to set
     */
    public void setCubic_capacity(String cubic_capacity) {
        this.cubic_capacity = cubic_capacity;
    }

    /**
     * @return the Color
     */
    public String getColor() {
        return Color;
    }

    /**
     * @param Color the Color to set
     */
    public void setColor(String Color) {
        this.Color = Color;
    }

    /**
     * @return the Fuel
     */
    public String getFuel() {
        return Fuel;
    }

    /**
     * @param Fuel the Fuel to set
     */
    public void setFuel(String Fuel) {
        this.Fuel = Fuel;
    }

    /**
     * @return the policy_no
     */
    public String getPolicy_no() {
        return policy_no;
    }

    /**
     * @param policy_no the policy_no to set
     */
    public void setPolicy_no(String policy_no) {
        this.policy_no = policy_no;
    }

    /**
     * @return the ins_from
     */
    public String getIns_from() {
        return ins_from;
    }

    /**
     * @param ins_from the ins_from to set
     */
    public void setIns_from(String ins_from) {
        this.ins_from = ins_from;
    }

    /**
     * @return the ins_upto
     */
    public String getIns_upto() {
        return ins_upto;
    }

    /**
     * @param ins_upto the ins_upto to set
     */
    public void setIns_upto(String ins_upto) {
        this.ins_upto = ins_upto;
    }

    /**
     * @return the ins_company
     */
    public String getIns_company() {
        return ins_company;
    }

    /**
     * @param ins_company the ins_company to set
     */
    public void setIns_company(String ins_company) {
        this.ins_company = ins_company;
    }

    /**
     * @return the ins_type_desc
     */
    public String getIns_type_desc() {
        return ins_type_desc;
    }

    /**
     * @param ins_type_desc the ins_type_desc to set
     */
    public void setIns_type_desc(String ins_type_desc) {
        this.ins_type_desc = ins_type_desc;
    }

    /**
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile the mobile to set
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the fee
     */
    public String getFee() {
        return fee;
    }

    /**
     * @param fee the fee to set
     */
    public void setFee(String fee) {
        this.fee = fee;
    }

    /**
     * @return the rcpt_dt
     */
    public String getRcpt_dt() {
        return rcpt_dt;
    }

    /**
     * @param rcpt_dt the rcpt_dt to set
     */
    public void setRcpt_dt(String rcpt_dt) {
        this.rcpt_dt = rcpt_dt;
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
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the purpose
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * @param purpose the purpose to set
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * @return the nocNo
     */
    public String getNocNo() {
        return nocNo;
    }

    /**
     * @param nocNo the nocNo to set
     */
    public void setNocNo(String nocNo) {
        this.nocNo = nocNo;
    }

    /**
     * @return the nocDt
     */
    public String getNocDt() {
        return nocDt;
    }

    /**
     * @param nocDt the nocDt to set
     */
    public void setNocDt(String nocDt) {
        this.nocDt = nocDt;
    }

    /**
     * @return the offtoname
     */
    public String getOfftoname() {
        return offtoname;
    }

    /**
     * @param offtoname the offtoname to set
     */
    public void setOfftoname(String offtoname) {
        this.offtoname = offtoname;
    }

    /**
     * @return the statetoname
     */
    public String getStatetoname() {
        return statetoname;
    }

    /**
     * @param statetoname the statetoname to set
     */
    public void setStatetoname(String statetoname) {
        this.statetoname = statetoname;
    }

    /**
     * @return the comp_type
     */
    public String getComp_type() {
        return comp_type;
    }

    /**
     * @param comp_type the comp_type to set
     */
    public void setComp_type(String comp_type) {
        this.comp_type = comp_type;
    }

    /**
     * @return the fir_no
     */
    public String getFir_no() {
        return fir_no;
    }

    /**
     * @param fir_no the fir_no to set
     */
    public void setFir_no(String fir_no) {
        this.fir_no = fir_no;
    }

    /**
     * @return the fir_dt
     */
    public String getFir_dt() {
        return fir_dt;
    }

    /**
     * @param fir_dt the fir_dt to set
     */
    public void setFir_dt(String fir_dt) {
        this.fir_dt = fir_dt;
    }

    /**
     * @return the complain
     */
    public String getComplain() {
        return complain;
    }

    /**
     * @param complain the complain to set
     */
    public void setComplain(String complain) {
        this.complain = complain;
    }

    /**
     * @return the printed_on
     */
    public String getPrinted_on() {
        return printed_on;
    }

    /**
     * @param printed_on the printed_on to set
     */
    public void setPrinted_on(String printed_on) {
        this.printed_on = printed_on;
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
     * @return the comp_dt
     */
    public String getComp_dt() {
        return comp_dt;
    }

    /**
     * @param comp_dt the comp_dt to set
     */
    public void setComp_dt(String comp_dt) {
        this.comp_dt = comp_dt;
    }

    public String getStand_cap() {
        return stand_cap;
    }

    public void setStand_cap(String stand_cap) {
        this.stand_cap = stand_cap;
    }

    public String getSleep_cap() {
        return sleep_cap;
    }

    public void setSleep_cap(String sleep_cap) {
        this.sleep_cap = sleep_cap;
    }

    public PassengerPermitDetailDobj getPassPmtDobj() {
        return passPmtDobj;
    }

    public void setPassPmtDobj(PassengerPermitDetailDobj passPmtDobj) {
        this.passPmtDobj = passPmtDobj;
    }

    public PermitHomeAuthDobj getAuthdobj() {
        return authdobj;
    }

    public void setAuthdobj(PermitHomeAuthDobj authdobj) {
        this.authdobj = authdobj;
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
     * @return the preOwner
     */
    public String getPreOwner() {
        return preOwner;
    }

    /**
     * @param preOwner the preOwner to set
     */
    public void setPreOwner(String preOwner) {
        this.preOwner = preOwner;
    }

    /**
     * @return the oldState
     */
    public String getOldState() {
        return oldState;
    }

    /**
     * @param oldState the oldState to set
     */
    public void setOldState(String oldState) {
        this.oldState = oldState;
    }

    /**
     * @return the preRegno
     */
    public String getPreRegno() {
        return preRegno;
    }

    /**
     * @param preRegno the preRegno to set
     */
    public void setPreRegno(String preRegno) {
        this.preRegno = preRegno;
    }

    /**
     * @return the entryDate
     */
    public String getEntryDate() {
        return entryDate;
    }

    /**
     * @param entryDate the entryDate to set
     */
    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    /**
     * @return the conversionDate
     */
    public String getConversionDate() {
        return conversionDate;
    }

    /**
     * @param conversionDate the conversionDate to set
     */
    public void setConversionDate(String conversionDate) {
        this.conversionDate = conversionDate;
    }

    /**
     * @return the tax_amt
     */
    public int getTax_amt() {
        return tax_amt;
    }

    /**
     * @param tax_amt the tax_amt to set
     */
    public void setTax_amt(int tax_amt) {
        this.tax_amt = tax_amt;
    }

    /**
     * @return the vchNorms
     */
    public String getVchNorms() {
        return vchNorms;
    }

    /**
     * @param vchNorms the vchNorms to set
     */
    public void setVchNorms(String vchNorms) {
        this.vchNorms = vchNorms;
    }

    public String getVchStatus() {
        return vchStatus;
    }

    public void setVchStatus(String vchStatus) {
        this.vchStatus = vchStatus;
    }

    public String getVchStatusDescr() {
        return vchStatusDescr;
    }

    public void setVchStatusDescr(String vchStatusDescr) {
        this.vchStatusDescr = vchStatusDescr;
    }

    /**
     * @return the subHeader
     */
    public String getSubHeader() {
        return subHeader;
    }

    /**
     * @param subHeader the subHeader to set
     */
    public void setSubHeader(String subHeader) {
        this.subHeader = subHeader;
    }

    /**
     * @return the floorArea
     */
    public String getFloorArea() {
        return floorArea;
    }

    /**
     * @param floorArea the floorArea to set
     */
    public void setFloorArea(String floorArea) {
        this.floorArea = floorArea;
    }

    /**
     * @return the wheelBase
     */
    public String getWheelBase() {
        return wheelBase;
    }

    /**
     * @param wheelBase the wheelBase to set
     */
    public void setWheelBase(String wheelBase) {
        this.wheelBase = wheelBase;
    }

    /**
     * @return the rc_cancel_dtls
     */
    public String getRc_cancel_dtls() {
        return rc_cancel_dtls;
    }

    /**
     * @param rc_cancel_dtls the rc_cancel_dtls to set
     */
    public void setRc_cancel_dtls(String rc_cancel_dtls) {
        this.rc_cancel_dtls = rc_cancel_dtls;
    }

    /**
     * @return the rc_surr_dtls
     */
    public String getRc_surr_dtls() {
        return rc_surr_dtls;
    }

    /**
     * @param rc_surr_dtls the rc_surr_dtls to set
     */
    public void setRc_surr_dtls(String rc_surr_dtls) {
        this.rc_surr_dtls = rc_surr_dtls;
    }

    /**
     * @return the hpt_dtls
     */
    public String getHpt_dtls() {
        return hpt_dtls;
    }

    /**
     * @param hpt_dtls the hpt_dtls to set
     */
    public void setHpt_dtls(String hpt_dtls) {
        this.hpt_dtls = hpt_dtls;
    }

    /**
     * @return the kitManufactureDt
     */
    public String getKitManufactureDt() {
        return kitManufactureDt;
    }

    /**
     * @param kitManufactureDt the kitManufactureDt to set
     */
    public void setKitManufactureDt(String kitManufactureDt) {
        this.kitManufactureDt = kitManufactureDt;
    }

    /**
     * @return the kitManufactureName
     */
    public String getKitManufactureName() {
        return kitManufactureName;
    }

    /**
     * @param kitManufactureName the kitManufactureName to set
     */
    public void setKitManufactureName(String kitManufactureName) {
        this.kitManufactureName = kitManufactureName;
    }

    /**
     * @return the kitHydroDt
     */
    public String getKitHydroDt() {
        return kitHydroDt;
    }

    /**
     * @param kitHydroDt the kitHydroDt to set
     */
    public void setKitHydroDt(String kitHydroDt) {
        this.kitHydroDt = kitHydroDt;
    }

    /**
     * @return the kitDesc
     */
    public String getKitDesc() {
        return kitDesc;
    }

    /**
     * @param kitDesc the kitDesc to set
     */
    public void setKitDesc(String kitDesc) {
        this.kitDesc = kitDesc;
    }

    /**
     * @return the kitWorkName
     */
    public String getKitWorkName() {
        return kitWorkName;
    }

    /**
     * @param kitWorkName the kitWorkName to set
     */
    public void setKitWorkName(String kitWorkName) {
        this.kitWorkName = kitWorkName;
    }

    /**
     * @return the tclNo
     */
    public String getTclNo() {
        return tclNo;
    }

    /**
     * @param tclNo the tclNo to set
     */
    public void setTclNo(String tclNo) {
        this.tclNo = tclNo;
    }

    /**
     * @return the tclDt
     */
    public String getTclDt() {
        return tclDt;
    }

    /**
     * @param tclDt the tclDt to set
     */
    public void setTclDt(String tclDt) {
        this.tclDt = tclDt;
    }

    /**
     * @return the kitNo
     */
    public String getKitNo() {
        return kitNo;
    }

    /**
     * @param kitNo the kitNo to set
     */
    public void setKitNo(String kitNo) {
        this.kitNo = kitNo;
    }

    /**
     * @return the cylNo
     */
    public String getCylNo() {
        return cylNo;
    }

    /**
     * @param cylNo the cylNo to set
     */
    public void setCylNo(String cylNo) {
        this.cylNo = cylNo;
    }

    /**
     * @return the isTransport
     */
    public boolean isIsTransport() {
        return isTransport;
    }

    /**
     * @param isTransport the isTransport to set
     */
    public void setIsTransport(boolean isTransport) {
        this.isTransport = isTransport;
    }

    /**
     * @return the fdesc
     */
    public String getFdesc() {
        return fdesc;
    }

    /**
     * @param fdesc the fdesc to set
     */
    public void setFdesc(String fdesc) {
        this.fdesc = fdesc;
    }

    /**
     * @return the rdesc
     */
    public String getRdesc() {
        return rdesc;
    }

    /**
     * @param rdesc the rdesc to set
     */
    public void setRdesc(String rdesc) {
        this.rdesc = rdesc;
    }

    /**
     * @return the odesc
     */
    public String getOdesc() {
        return odesc;
    }

    /**
     * @param odesc the odesc to set
     */
    public void setOdesc(String odesc) {
        this.odesc = odesc;
    }

    /**
     * @return the tdesc
     */
    public String getTdesc() {
        return tdesc;
    }

    /**
     * @param tdesc the tdesc to set
     */
    public void setTdesc(String tdesc) {
        this.tdesc = tdesc;
    }

    /**
     * @return the fweight
     */
    public String getFweight() {
        return fweight;
    }

    /**
     * @param fweight the fweight to set
     */
    public void setFweight(String fweight) {
        this.fweight = fweight;
    }

    /**
     * @return the rweight
     */
    public String getRweight() {
        return rweight;
    }

    /**
     * @param rweight the rweight to set
     */
    public void setRweight(String rweight) {
        this.rweight = rweight;
    }

    /**
     * @return the oweight
     */
    public String getOweight() {
        return oweight;
    }

    /**
     * @param oweight the oweight to set
     */
    public void setOweight(String oweight) {
        this.oweight = oweight;
    }

    /**
     * @return the tweight
     */
    public String getTweight() {
        return tweight;
    }

    /**
     * @param tweight the tweight to set
     */
    public void setTweight(String tweight) {
        this.tweight = tweight;
    }

    /**
     * @return the printed_by
     */
    public String getPrinted_by() {
        return printed_by;
    }

    /**
     * @param printed_by the printed_by to set
     */
    public void setPrinted_by(String printed_by) {
        this.printed_by = printed_by;
    }

    /**
     * @return the speed_governor_no
     */
    public String getSpeed_governor_no() {
        return speed_governor_no;
    }

    /**
     * @param speed_governor_no the speed_governor_no to set
     */
    public void setSpeed_governor_no(String speed_governor_no) {
        this.speed_governor_no = speed_governor_no;
    }

    /**
     * @return the sg_fittedDate
     */
    public String getSg_fittedDate() {
        return sg_fittedDate;
    }

    /**
     * @param sg_fittedDate the sg_fittedDate to set
     */
    public void setSg_fittedDate(String sg_fittedDate) {
        this.sg_fittedDate = sg_fittedDate;
    }

    /**
     * @return the isspeed_governor
     */
    public boolean isIsspeed_governor() {
        return isspeed_governor;
    }

    /**
     * @param isspeed_governor the isspeed_governor to set
     */
    public void setIsspeed_governor(boolean isspeed_governor) {
        this.isspeed_governor = isspeed_governor;
    }

    /**
     * @return the vhaChangedDataAdminRemark
     */
    public String getVhaChangedDataAdminRemark() {
        return vhaChangedDataAdminRemark;
    }

    /**
     * @param vhaChangedDataAdminRemark the vhaChangedDataAdminRemark to set
     */
    public void setVhaChangedDataAdminRemark(String vhaChangedDataAdminRemark) {
        this.vhaChangedDataAdminRemark = vhaChangedDataAdminRemark;
    }

    /**
     * @return the isAdminRemark
     */
    public boolean isIsAdminRemark() {
        return isAdminRemark;
    }

    /**
     * @param isAdminRemark the isAdminRemark to set
     */
    public void setIsAdminRemark(boolean isAdminRemark) {
        this.isAdminRemark = isAdminRemark;
    }

    /**
     * @return the sg_manuName
     */
    public String getSg_manuName() {
        return sg_manuName;
    }

    /**
     * @param sg_manuName the sg_manuName to set
     */
    public void setSg_manuName(String sg_manuName) {
        this.sg_manuName = sg_manuName;
    }

    /**
     * @return the isLPGDtls
     */
    public boolean isIsLPGDtls() {
        return isLPGDtls;
    }

    /**
     * @param isLPGDtls the isLPGDtls to set
     */
    public void setIsLPGDtls(boolean isLPGDtls) {
        this.isLPGDtls = isLPGDtls;
    }

    /**
     * @return the isInsDtls
     */
    public boolean isIsInsDtls() {
        return isInsDtls;
    }

    /**
     * @param isInsDtls the isInsDtls to set
     */
    public void setIsInsDtls(boolean isInsDtls) {
        this.isInsDtls = isInsDtls;
    }

    /**
     * @return the isHPDtls
     */
    public boolean isIsHPDtls() {
        return isHPDtls;
    }

    /**
     * @param isHPDtls the isHPDtls to set
     */
    public void setIsHPDtls(boolean isHPDtls) {
        this.isHPDtls = isHPDtls;
    }

    /**
     * @return the isNOCDtls
     */
    public boolean isIsNOCDtls() {
        return isNOCDtls;
    }

    /**
     * @param isNOCDtls the isNOCDtls to set
     */
    public void setIsNOCDtls(boolean isNOCDtls) {
        this.isNOCDtls = isNOCDtls;
    }

    /**
     * @return the isBlkDtls
     */
    public boolean isIsBlkDtls() {
        return isBlkDtls;
    }

    /**
     * @param isBlkDtls the isBlkDtls to set
     */
    public void setIsBlkDtls(boolean isBlkDtls) {
        this.isBlkDtls = isBlkDtls;
    }

    /**
     * @return the isHPTDtls
     */
    public boolean isIsHPTDtls() {
        return isHPTDtls;
    }

    /**
     * @param isHPTDtls the isHPTDtls to set
     */
    public void setIsHPTDtls(boolean isHPTDtls) {
        this.isHPTDtls = isHPTDtls;
    }

    /**
     * @return the isRCSurrDtls
     */
    public boolean isIsRCSurrDtls() {
        return isRCSurrDtls;
    }

    /**
     * @param isRCSurrDtls the isRCSurrDtls to set
     */
    public void setIsRCSurrDtls(boolean isRCSurrDtls) {
        this.isRCSurrDtls = isRCSurrDtls;
    }

    /**
     * @return the isRCCancelDtls
     */
    public boolean isIsRCCancelDtls() {
        return isRCCancelDtls;
    }

    /**
     * @param isRCCancelDtls the isRCCancelDtls to set
     */
    public void setIsRCCancelDtls(boolean isRCCancelDtls) {
        this.isRCCancelDtls = isRCCancelDtls;
    }

    /**
     * @return the linkVehNumber
     */
    public String getLinkVehNumber() {
        return linkVehNumber;
    }

    /**
     * @param linkVehNumber the linkVehNumber to set
     */
    public void setLinkVehNumber(String linkVehNumber) {
        this.linkVehNumber = linkVehNumber;
    }

    /**
     * @return the hptSubList
     */
    public List<VehicleParticularDobj> getHptSubList() {
        return hptSubList;
    }

    /**
     * @param hptSubList the hptSubList to set
     */
    public void setHptSubList(List<VehicleParticularDobj> hptSubList) {
        this.hptSubList = hptSubList;
    }

    /**
     * @return the permitAreaDtls
     */
    public String getPermitAreaDtls() {
        return permitAreaDtls;
    }

    /**
     * @param permitAreaDtls the permitAreaDtls to set
     */
    public void setPermitAreaDtls(String permitAreaDtls) {
        this.permitAreaDtls = permitAreaDtls;
    }

    /**
     * @return the areaActionSource
     */
    public List<PermitRouteList> getAreaActionSource() {
        return areaActionSource;
    }

    /**
     * @param areaActionSource the areaActionSource to set
     */
    public void setAreaActionSource(List<PermitRouteList> areaActionSource) {
        this.areaActionSource = areaActionSource;
    }

    /**
     * @return the permitFeeDtls
     */
    public String getPermitFeeDtls() {
        return permitFeeDtls;
    }

    /**
     * @param permitFeeDtls the permitFeeDtls to set
     */
    public void setPermitFeeDtls(String permitFeeDtls) {
        this.permitFeeDtls = permitFeeDtls;
    }

    /**
     * @return the image_background
     */
    public String getImage_background() {
        return image_background;
    }

    /**
     * @param image_background the image_background to set
     */
    public void setImage_background(String image_background) {
        this.image_background = image_background;
    }

    /**
     * @return the image_logo
     */
    public String getImage_logo() {
        return image_logo;
    }

    /**
     * @param image_logo the image_logo to set
     */
    public void setImage_logo(String image_logo) {
        this.image_logo = image_logo;
    }

    /**
     * @return the show_image_background
     */
    public boolean isShow_image_background() {
        return show_image_background;
    }

    /**
     * @param show_image_background the show_image_background to set
     */
    public void setShow_image_background(boolean show_image_background) {
        this.show_image_background = show_image_background;
    }

    /**
     * @return the show_image_logo
     */
    public boolean isShow_image_logo() {
        return show_image_logo;
    }

    /**
     * @param show_image_logo the show_image_logo to set
     */
    public void setShow_image_logo(boolean show_image_logo) {
        this.show_image_logo = show_image_logo;
    }

    /**
     * @return the vehicleType
     */
    public boolean isVehicleType() {
        return vehicleType;
    }

    /**
     * @param vehicleType the vehicleType to set
     */
    public void setVehicleType(boolean vehicleType) {
        this.vehicleType = vehicleType;
    }

    /**
     * @return the showRoadSafetySlogan
     */
    public boolean isShowRoadSafetySlogan() {
        return showRoadSafetySlogan;
    }

    /**
     * @param showRoadSafetySlogan the showRoadSafetySlogan to set
     */
    public void setShowRoadSafetySlogan(boolean showRoadSafetySlogan) {
        this.showRoadSafetySlogan = showRoadSafetySlogan;
    }

    /**
     * @return the roadSafetySloganDobj
     */
    public VmRoadSafetySloganPrintDobj getRoadSafetySloganDobj() {
        return roadSafetySloganDobj;
    }

    /**
     * @param roadSafetySloganDobj the roadSafetySloganDobj to set
     */
    public void setRoadSafetySloganDobj(VmRoadSafetySloganPrintDobj roadSafetySloganDobj) {
        this.roadSafetySloganDobj = roadSafetySloganDobj;
    }

    /**
     * @return the pucc_from
     */
    public String getPucc_from() {
        return pucc_from;
    }

    /**
     * @param pucc_from the pucc_from to set
     */
    public void setPucc_from(String pucc_from) {
        this.pucc_from = pucc_from;
    }

    /**
     * @return the pucc_upto
     */
    public String getPucc_upto() {
        return pucc_upto;
    }

    /**
     * @param pucc_upto the pucc_upto to set
     */
    public void setPucc_upto(String pucc_upto) {
        this.pucc_upto = pucc_upto;
    }
}
