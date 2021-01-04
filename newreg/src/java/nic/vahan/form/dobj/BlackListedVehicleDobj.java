/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nic.vahan.common.jsf.utils.DateUtil;

/**
 *
 * @author Afzal
 */
public class BlackListedVehicleDobj implements Serializable {

    private String state_cd;
    private String actiontaken;
    private String FirNo;
    private String PoliceStation;
    private String complain;
    private String complainDesc;
    private Date complain_dt = DateUtil.parseDate(DateUtil.getCurrentDate());
    private String entered_by;
    private String userName = "";
    private Date action_dt = DateUtil.parseDate(DateUtil.getCurrentDate());
    private String complaindt;
    private String actiondt;
    private String showComplaindt;
    private String regn_no;
    private String regin_no;
    private String chasi_no;
    private int complain_type;
    private Date FirDate = DateUtil.parseDate(DateUtil.getCurrentDate());
    private int off_cd;
    private String deletecomplaindt;
    private List<BlackListedVehicleDobj> dolist = new ArrayList();
    private String stateName;
    private String officeName;
    private String blocked_by;
    private String released_by;
    private Long compounding_amt;
    private int vh_class;
    private String underSection;
    private int district;
    private String colour;
    private Date order_dt = DateUtil.parseDate(DateUtil.getCurrentDate());
    private String printed_on;
    private String district_name;
    private String entered_on;
    private String vh_class_descr;
    private String rcpt_heading;
    private String rcpt_subheading;
    private String display_order_dt;
    private String court_order_no;
    private boolean untracedReportOfBlacklistedForSwappingUse;

    public String getPoliceStation() {
        return PoliceStation;
    }

    public void setPoliceStation(String PoliceStation) {
        this.PoliceStation = PoliceStation;
    }

    public String getActiontaken() {
        return actiontaken;
    }

    public void setActiontaken(String actiontaken) {
        this.actiontaken = actiontaken;
    }

    public int getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    public int getComplain_type() {
        return complain_type;
    }

    public void setComplain_type(int complain_type) {
        this.complain_type = complain_type;
    }

    public Date getAction_dt() {
        return action_dt;
    }

    public void setAction_dt(Date action_dt) {
        this.action_dt = action_dt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getComplain_dt() {
        return complain_dt;
    }

    public void setComplain_dt(Date complain_dt) {
        this.complain_dt = complain_dt;
    }

    public List<BlackListedVehicleDobj> getDolist() {
        return dolist;
    }

    public void setDolist(List<BlackListedVehicleDobj> dolist) {
        this.dolist = dolist;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getChasi_no() {
        return chasi_no;
    }

    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
    }

    public String getFirNo() {
        return FirNo;
    }

    public void setFirNo(String FirNo) {
        this.FirNo = FirNo;
    }

    public Date getFirDate() {
        return FirDate;
    }

    public void setFirDate(Date FirDate) {
        this.FirDate = FirDate;
    }

    public String getComplain() {
        return complain;
    }

    public void setComplain(String complain) {
        this.complain = complain;
    }

    public String getEntered_by() {
        return entered_by;
    }

    public void setEntered_by(String entered_by) {
        this.entered_by = entered_by;
    }

    /**
     * @return the complaindt
     */
    public String getComplaindt() {
        return complaindt;
    }

    /**
     * @param complaindt the complaindt to set
     */
    public void setComplaindt(String complaindt) {
        this.complaindt = complaindt;
    }

    /**
     * @return the complainDesc
     */
    public String getComplainDesc() {
        return complainDesc;
    }

    /**
     * @param complainDesc the complainDesc to set
     */
    public void setComplainDesc(String complainDesc) {
        this.complainDesc = complainDesc;
    }

    /**
     * @return the shoeComplaindt
     */
    public String getShowComplaindt() {
        return showComplaindt;
    }

    /**
     * @param shoeComplaindt the shoeComplaindt to set
     */
    public void setShowComplaindt(String showComplaindt) {
        this.showComplaindt = showComplaindt;
    }

    /**
     * @return the actiondt
     */
    public String getActiondt() {
        return actiondt;
    }

    /**
     * @param actiondt the actiondt to set
     */
    public void setActiondt(String actiondt) {
        this.actiondt = actiondt;
    }

    /**
     * @return the regin_no
     */
    public String getRegin_no() {
        return regin_no;
    }

    /**
     * @param regin_no the regin_no to set
     */
    public void setRegin_no(String regin_no) {
        this.regin_no = regin_no;
    }

    /**
     * @return the deletecomplaindt
     */
    public String getDeletecomplaindt() {
        return deletecomplaindt;
    }

    /**
     * @param deletecomplaindt the deletecomplaindt to set
     */
    public void setDeletecomplaindt(String deletecomplaindt) {
        this.deletecomplaindt = deletecomplaindt;
    }

    /**
     * @return the stateName
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * @param stateName the stateName to set
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    /**
     * @return the officeName
     */
    public String getOfficeName() {
        return officeName;
    }

    /**
     * @param officeName the officeName to set
     */
    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    /**
     * @return the blocked_by
     */
    public String getBlocked_by() {
        return blocked_by;
    }

    /**
     * @param blocked_by the blocked_by to set
     */
    public void setBlocked_by(String blocked_by) {
        this.blocked_by = blocked_by;
    }

    /**
     * @return the released_by
     */
    public String getReleased_by() {
        return released_by;
    }

    /**
     * @param released_by the released_by to set
     */
    public void setReleased_by(String released_by) {
        this.released_by = released_by;
    }

    /**
     * @return the compounding_amt
     */
    public Long getCompounding_amt() {
        return compounding_amt;
    }

    /**
     * @param compounding_amt the compounding_amt to set
     */
    public void setCompounding_amt(Long compounding_amt) {
        this.compounding_amt = compounding_amt;
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
     * @return the underSection
     */
    public String getUnderSection() {
        return underSection;
    }

    /**
     * @param underSection the underSection to set
     */
    public void setUnderSection(String underSection) {
        this.underSection = underSection;
    }

    /**
     * @return the district
     */
    public int getDistrict() {
        return district;
    }

    /**
     * @param district the district to set
     */
    public void setDistrict(int district) {
        this.district = district;
    }

    /**
     * @return the colour
     */
    public String getColour() {
        return colour;
    }

    /**
     * @param colour the colour to set
     */
    public void setColour(String colour) {
        this.colour = colour;
    }

    /**
     * @return the order_dt
     */
    public Date getOrder_dt() {
        return order_dt;
    }

    /**
     * @param order_dt the order_dt to set
     */
    public void setOrder_dt(Date order_dt) {
        this.order_dt = order_dt;
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
     * @return the district_name
     */
    public String getDistrict_name() {
        return district_name;
    }

    /**
     * @param district_name the district_name to set
     */
    public void setDistrict_name(String district_name) {
        this.district_name = district_name;
    }

    /**
     * @return the entered_on
     */
    public String getEntered_on() {
        return entered_on;
    }

    /**
     * @param entered_on the entered_on to set
     */
    public void setEntered_on(String entered_on) {
        this.entered_on = entered_on;
    }

    /**
     * @return the vh_class_descr
     */
    public String getVh_class_descr() {
        return vh_class_descr;
    }

    /**
     * @param vh_class_descr the vh_class_descr to set
     */
    public void setVh_class_descr(String vh_class_descr) {
        this.vh_class_descr = vh_class_descr;
    }

    /**
     * @return the rcpt_heading
     */
    public String getRcpt_heading() {
        return rcpt_heading;
    }

    /**
     * @param rcpt_heading the rcpt_heading to set
     */
    public void setRcpt_heading(String rcpt_heading) {
        this.rcpt_heading = rcpt_heading;
    }

    /**
     * @return the rcpt_subheading
     */
    public String getRcpt_subheading() {
        return rcpt_subheading;
    }

    /**
     * @param rcpt_subheading the rcpt_subheading to set
     */
    public void setRcpt_subheading(String rcpt_subheading) {
        this.rcpt_subheading = rcpt_subheading;
    }

    /**
     * @return the display_order_dt
     */
    public String getDisplay_order_dt() {
        return display_order_dt;
    }

    /**
     * @param display_order_dt the display_order_dt to set
     */
    public void setDisplay_order_dt(String display_order_dt) {
        this.display_order_dt = display_order_dt;
    }

    /**
     * @return the court_order_no
     */
    public String getCourt_order_no() {
        return court_order_no;
    }

    /**
     * @param court_order_no the court_order_no to set
     */
    public void setCourt_order_no(String court_order_no) {
        this.court_order_no = court_order_no;
    }

    /**
     * @return the untracedReportOfBlacklistedForSwappingUse
     */
    public boolean isUntracedReportOfBlacklistedForSwappingUse() {
        return untracedReportOfBlacklistedForSwappingUse;
    }

    /**
     * @param untracedReportOfBlacklistedForSwappingUse the
     * untracedReportOfBlacklistedForSwappingUse to set
     */
    public void setUntracedReportOfBlacklistedForSwappingUse(boolean untracedReportOfBlacklistedForSwappingUse) {
        this.untracedReportOfBlacklistedForSwappingUse = untracedReportOfBlacklistedForSwappingUse;
    }
}
