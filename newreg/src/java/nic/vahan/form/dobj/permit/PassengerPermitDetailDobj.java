/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class PassengerPermitDetailDobj implements Cloneable, Serializable {

    //Owner Detail Dobj
    private String applNo;
    private String regnNo;
    private String numberOfTrips;
    private String pmtCatg;
    private String paction;
    private String pmt_type;
    private String paction_code;
    private String pmt_type_code;
    private String pmt_catg_code;
    private String period;
    private String period_mode;
    private String services_TYPE;
    private String rout_code;
    private String domain_CODE;
    private String start_POINT;
    private String end_of_point;
    private String floc;
    private String tloc;
    private String via;
    private String region_covered;
    private String parking;
    private String other_REGION;
    private String joreny_PURPOSE;
    private String other_REGION_ROUT;
    private String goods_TO_CARRY;
    private String state_cd;
    private String off_cd;
    //permit Details
    private String pmt_no;
    private Date valid_from;
    private Date valid_upto;
    private String offer_no;
    private String order_no;
    private String order_by;
    private Date order_dt;
    private String remarks;
    private String appDisboolenValue;
    private String rout_length;
    private Date replaceDate;
    private String validFromInString;
    private String validUptoInString;
    private String replaceDateInString;
    private String otherCriteria;
    private String allotedFlag;
    private String multiRegion = "false";
    private int multiDoc = 0;
    private boolean hypth;
    private String noOfAdvUnits = null;
    private String transferForexempted;
    private String offerletter = "false";
    private int transPurCd = 0;
    private String dupPmtReason;
    private String opDateInString;
    private String permitOfficeName;
    private String orderDtInString;
    private String domainCovered;
    private String pmtOwnerName;
    private String applDateInString;
    private String npAuthNo;
    private String routeFlag;
    private String issuePmtDateDescr;
    private Date issuePmtDate;
    private Date op_dt;
    private String changeData;
    private String nhOverlapping;
    private int nhOverlappingLength;
    private String registered_off_name;
    private String state_cd_to;
    private String[] regionCoveredArr;
    private String state_covered;
    private String rcpt_no;

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public String getNumberOfTrips() {
        return numberOfTrips;
    }

    public void setNumberOfTrips(String numberOfTrips) {
        this.numberOfTrips = numberOfTrips;
    }

    public String getPmtCatg() {
        return pmtCatg;
    }

    public void setPmtCatg(String pmtCatg) {
        this.pmtCatg = pmtCatg;
    }

    public String getPaction() {
        return paction;
    }

    public void setPaction(String paction) {
        this.paction = paction;
    }

    public String getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(String pmt_type) {
        this.pmt_type = pmt_type;
    }

    public String getPaction_code() {
        return paction_code;
    }

    public void setPaction_code(String paction_code) {
        this.paction_code = paction_code;
    }

    public String getPmt_type_code() {
        return pmt_type_code;
    }

    public void setPmt_type_code(String pmt_type_code) {
        this.pmt_type_code = pmt_type_code;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getPeriod_mode() {
        return period_mode;
    }

    public void setPeriod_mode(String period_mode) {
        this.period_mode = period_mode;
    }

    public String getServices_TYPE() {
        return services_TYPE;
    }

    public void setServices_TYPE(String services_TYPE) {
        this.services_TYPE = services_TYPE;
    }

    public String getRout_code() {
        return rout_code;
    }

    public void setRout_code(String rout_code) {
        this.rout_code = rout_code;
    }

    public String getDomain_CODE() {
        return domain_CODE;
    }

    public void setDomain_CODE(String domain_CODE) {
        this.domain_CODE = domain_CODE;
    }

    public String getStart_POINT() {
        return start_POINT;
    }

    public void setStart_POINT(String start_POINT) {
        this.start_POINT = start_POINT;
    }

    public String getEnd_of_point() {
        return end_of_point;
    }

    public void setEnd_of_point(String end_of_point) {
        this.end_of_point = end_of_point;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public String getOther_REGION() {
        return other_REGION;
    }

    public void setOther_REGION(String other_REGION) {
        this.other_REGION = other_REGION;
    }

    public String getJoreny_PURPOSE() {
        return joreny_PURPOSE;
    }

    public void setJoreny_PURPOSE(String joreny_PURPOSE) {
        this.joreny_PURPOSE = joreny_PURPOSE;
    }

    public String getOther_REGION_ROUT() {
        return other_REGION_ROUT;
    }

    public void setOther_REGION_ROUT(String other_REGION_ROUT) {
        this.other_REGION_ROUT = other_REGION_ROUT;
    }

    public String getGoods_TO_CARRY() {
        return goods_TO_CARRY;
    }

    public void setGoods_TO_CARRY(String goods_TO_CARRY) {
        this.goods_TO_CARRY = goods_TO_CARRY;
    }

    public String getFloc() {
        return floc;
    }

    public void setFloc(String floc) {
        this.floc = floc;
    }

    public String getTloc() {
        return tloc;
    }

    public void setTloc(String tloc) {
        this.tloc = tloc;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public String getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(String off_cd) {
        this.off_cd = off_cd;
    }

    public String getPmt_no() {
        return pmt_no;
    }

    public void setPmt_no(String pmt_no) {
        this.pmt_no = pmt_no;
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

    public String getOffer_no() {
        return offer_no;
    }

    public void setOffer_no(String offer_no) {
        this.offer_no = offer_no;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getOrder_by() {
        return order_by;
    }

    public void setOrder_by(String order_by) {
        this.order_by = order_by;
    }

    public Date getOrder_dt() {
        return order_dt;
    }

    public void setOrder_dt(Date order_dt) {
        this.order_dt = order_dt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRegion_covered() {
        return region_covered;
    }

    public void setRegion_covered(String region_covered) {
        this.region_covered = region_covered;
    }

    public String getAppDisboolenValue() {
        return appDisboolenValue;
    }

    public void setAppDisboolenValue(String appDisboolenValue) {
        this.appDisboolenValue = appDisboolenValue;
    }

    public String getRout_length() {
        return rout_length;
    }

    public void setRout_length(String rout_length) {
        this.rout_length = rout_length;
    }

    public Date getReplaceDate() {
        return replaceDate;
    }

    public void setReplaceDate(Date replaceDate) {
        this.replaceDate = replaceDate;
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

    public String getReplaceDateInString() {
        return replaceDateInString;
    }

    public void setReplaceDateInString(String replaceDateInString) {
        this.replaceDateInString = replaceDateInString;
    }

    public String getOtherCriteria() {
        return otherCriteria;
    }

    public void setOtherCriteria(String otherCriteria) {
        this.otherCriteria = otherCriteria;
    }

    public String getMultiRegion() {
        return multiRegion;
    }

    public void setMultiRegion(String multiRegion) {
        this.multiRegion = multiRegion;
    }

    public String getAllotedFlag() {
        return allotedFlag;
    }

    public void setAllotedFlag(String allotedFlag) {
        this.allotedFlag = allotedFlag;
    }

    public int getMultiDoc() {
        return multiDoc;
    }

    public void setMultiDoc(int multiDoc) {
        this.multiDoc = multiDoc;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        boolean ret = false;

        if (obj == null || !(obj instanceof PassengerPermitDetailDobj)) {
            return false;
        }
        PassengerPermitDetailDobj dbj = (PassengerPermitDetailDobj) obj;
        if (this == obj) {
        }

        return ret;
    }

    @Override
    public String toString() {
        String toStringReturn = applNo + " : " + regnNo + " : "
                + numberOfTrips + " : " + pmtCatg + " : "
                + paction + " : " + pmt_type + " : "
                + paction_code + " : " + pmt_type_code + " : "
                + period + " : " + period_mode + " : "
                + services_TYPE + " : " + rout_code + " : "
                + domain_CODE + " : " + start_POINT + " : "
                + end_of_point + " : " + floc + " : "
                + tloc + " : " + region_covered + " : " + parking + " : "
                + other_REGION + " : " + joreny_PURPOSE + " : "
                + other_REGION_ROUT + " : " + goods_TO_CARRY + " : "
                + state_cd + " : " + off_cd + " : " + pmt_no + " : "
                + valid_from + " : " + valid_upto + " : "
                + offer_no + " : " + order_no + " : "
                + order_by + " : " + order_dt + " : "
                + remarks + " : " + appDisboolenValue + " : "
                + rout_length + " : " + replaceDate + " : "
                + validFromInString + " : " + validUptoInString + " : "
                + replaceDateInString + " : " + otherCriteria;
        return toStringReturn.replace("null", "");
    }

    public boolean isHypth() {
        return hypth;
    }

    public void setHypth(boolean hypth) {
        this.hypth = hypth;
    }

    /**
     * @return the noOfAdvUnits
     */
    public String getNoOfAdvUnits() {
        return noOfAdvUnits;
    }

    /**
     * @param noOfAdvUnits the noOfAdvUnits to set
     */
    public void setNoOfAdvUnits(String noOfAdvUnits) {
        this.noOfAdvUnits = noOfAdvUnits;
    }

    public String getTransferForexempted() {
        return transferForexempted;
    }

    public void setTransferForexempted(String transferForexempted) {
        this.transferForexempted = transferForexempted;
    }

    public String getOfferletter() {
        return offerletter;
    }

    public void setOfferletter(String offerletter) {
        this.offerletter = offerletter;
    }

    public String getPmt_catg_code() {
        return pmt_catg_code;
    }

    public void setPmt_catg_code(String pmt_catg_code) {
        this.pmt_catg_code = pmt_catg_code;
    }

    /**
     * @return the transPurCd
     */
    public int getTransPurCd() {
        return transPurCd;
    }

    /**
     * @param transPurCd the transPurCd to set
     */
    public void setTransPurCd(int transPurCd) {
        this.transPurCd = transPurCd;
    }

    public String getDupPmtReason() {
        return dupPmtReason;
    }

    public void setDupPmtReason(String dupPmtReason) {
        this.dupPmtReason = dupPmtReason;
    }

    public String getOpDateInString() {
        return opDateInString;
    }

    public void setOpDateInString(String opDateInString) {
        this.opDateInString = opDateInString;
    }

    /**
     * @return the permitOfficeName
     */
    public String getPermitOfficeName() {
        return permitOfficeName;
    }

    /**
     * @param permitOfficeName the permitOfficeName to set
     */
    public void setPermitOfficeName(String permitOfficeName) {
        this.permitOfficeName = permitOfficeName;
    }

    public String getOrderDtInString() {
        return orderDtInString;
    }

    public void setOrderDtInString(String orderDtInString) {
        this.orderDtInString = orderDtInString;
    }

    public String getDomainCovered() {
        return domainCovered;
    }

    public void setDomainCovered(String domainCovered) {
        this.domainCovered = domainCovered;
    }

    public String getPmtOwnerName() {
        return pmtOwnerName;
    }

    public void setPmtOwnerName(String pmtOwnerName) {
        this.pmtOwnerName = pmtOwnerName;
    }

    public String getApplDateInString() {
        return applDateInString;
    }

    public void setApplDateInString(String applDateInString) {
        this.applDateInString = applDateInString;
    }

    public String getNpAuthNo() {
        return npAuthNo;
    }

    public void setNpAuthNo(String npAuthNo) {
        this.npAuthNo = npAuthNo;
    }

    public String getIssuePmtDateDescr() {
        return issuePmtDateDescr;
    }

    public void setIssuePmtDateDescr(String issuePmtDateDescr) {
        this.issuePmtDateDescr = issuePmtDateDescr;
    }

    public Date getIssuePmtDate() {
        return issuePmtDate;
    }

    public void setIssuePmtDate(Date issuePmtDate) {
        this.issuePmtDate = issuePmtDate;
    }

    public String getRouteFlag() {
        return routeFlag;
    }

    public void setRouteFlag(String routeFlag) {
        this.routeFlag = routeFlag;
    }

    public Date getOp_dt() {
        return op_dt;
    }

    public void setOp_dt(Date op_dt) {
        this.op_dt = op_dt;
    }

    public String getChangeData() {
        return changeData;
    }

    public void setChangeData(String changeData) {
        this.changeData = changeData;
    }

    public String getNhOverlapping() {
        return nhOverlapping;
    }

    public void setNhOverlapping(String nhOverlapping) {
        this.nhOverlapping = nhOverlapping;
    }

    public int getNhOverlappingLength() {
        return nhOverlappingLength;
    }

    public void setNhOverlappingLength(int nhOverlappingLength) {
        this.nhOverlappingLength = nhOverlappingLength;
    }

    public String getRegistered_off_name() {
        return registered_off_name;
    }

    public void setRegistered_off_name(String registered_off_name) {
        this.registered_off_name = registered_off_name;
    }

    public String getState_cd_to() {
        return state_cd_to;
    }

    public void setState_cd_to(String state_cd_to) {
        this.state_cd_to = state_cd_to;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String[] getRegionCoveredArr() {
        return regionCoveredArr;
    }

    public void setRegionCoveredArr(String[] regionCoveredArr) {
        this.regionCoveredArr = regionCoveredArr;
    }

    public String getState_covered() {
        return state_covered;
    }

    public void setState_covered(String state_covered) {
        this.state_covered = state_covered;
    }

    public String getRcpt_no() {
        return rcpt_no;
    }

    public void setRcpt_no(String rcpt_no) {
        this.rcpt_no = rcpt_no;
    }
}
