/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PermitDetailImpl;
import nic.vahan.form.impl.permit.PrintPermitDocInXhtmlImpl;
import nic.vahan.server.CommonUtils;


import org.apache.log4j.Logger;

/**
 *
 * @author tranC075
 */
@ManagedBean(name = "permitDtls")
@ViewScoped
public class PermitDisplayDetailBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(PermitDisplayDetailBean.class);
    private String applicationNo = "";
    private String permitNo = "";
    private String validFrom = "";
    private String regnNo = "";
    private String validUpto = "";
    private String purCd = "";
    private String permitType = "";
    private String pmtCatg = "";
    private String goodToCarry = "";
    private String journeyPur = "";
    private String parking = "";
    private String replaceDt = "";
    private boolean showAuthDetails = false;
    private boolean showTempPermitDetails = false;
    private String authNo = "";
    private String authFrom = "";
    private String authTo = "";
    private String permitCd;
    private PermitDetailDobj tmp_Dobj = null;
    private List<PassengerPermitDetailDobj> routedata = new ArrayList<>();
    private String region_covered;
    private String state_cd = "";
    private String off_cd = "";
    private boolean otherRouteAllow;

    public boolean setRegNo(String regNo) {
        boolean isPermitVehicle = false;//for checking if vehicle has issued permit or not        
        try {
            reset();
            PermitDetailDobj dobj = PermitDetailImpl.getPermitdetailsFromRegnNo(regNo);
            tmp_Dobj = PermitDetailImpl.getTempPermitdetailsFromRegnNo(regNo);
            if (tmp_Dobj != null) {
                showTempPermitDetails = true;
                isPermitVehicle = true;

            }
            if (dobj != null) {
                Map stateConfig = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
                otherRouteAllow = Boolean.parseBoolean(stateConfig.get("other_office_route_allow").toString());
                isPermitVehicle = true;
                applicationNo = dobj.getApplicationNo().toUpperCase();
                permitNo = dobj.getPmt_no();
                validFrom = JSFUtils.convertToStandardDateFormat(dobj.getValid_from());
                validUpto = JSFUtils.convertToStandardDateFormat(dobj.getValid_upto());
                purCd = dobj.getPurCdDesc();
                permitType = dobj.getPmt_type_desc();
                pmtCatg = dobj.getPmt_catg_desc();
                goodToCarry = dobj.getGoodsToCarry();
                journeyPur = dobj.getJourney();
                parking = dobj.getParking();
                permitCd = String.valueOf(dobj.getPmt_type());
                state_cd = dobj.getState_cd();
                off_cd = String.valueOf(dobj.getOff_cd());
                replaceDt = JSFUtils.convertToStandardDateFormat(dobj.getReplaceDt());
                if (permitCd.equals(TableConstants.NATIONAL_PERMIT) || permitCd.equals(TableConstants.AITP)) {
                    showAuthDetails = true;
                    PermitDetailDobj dobjs = PermitDetailImpl.getAuthDetails(regNo);
                    if (dobjs != null) {
                        authNo = dobjs.getAuthNo();
                        authFrom = dobjs.getAuthFrom();
                        authTo = dobjs.getAuthTo();
                    }
                } else if (!CommonUtils.isNullOrBlank(state_cd) && !CommonUtils.isNullOrBlank(off_cd) && !CommonUtils.isNullOrBlank(applicationNo)
                        && (permitCd.equals(TableConstants.CC_PERMIT) || permitCd.equals(TableConstants.GOODS_PERMIT) || permitCd.equals(TableConstants.PSV_PERMIT) || permitCd.equals(String.valueOf(TableConstants.STAGE_CARRIAGE_PERMIT)))) {
                    PrintPermitDocInXhtmlImpl pmtDocImpl = new PrintPermitDocInXhtmlImpl();
                    if (otherRouteAllow && !CommonUtils.isNullOrBlank(permitCd) && permitCd.equals(String.valueOf(TableConstants.STAGE_CARRIAGE_PERMIT))) {
                        setRoutedata(pmtDocImpl.getRouteData(applicationNo, state_cd, Integer.parseInt(off_cd), TableList.vt_permit_route, otherRouteAllow));
                    } else {
                        setRoutedata(pmtDocImpl.getRouteData(applicationNo, state_cd, Integer.parseInt(off_cd), TableList.vt_permit_route, false));
                    }
                    setRegion_covered(pmtDocImpl.getRegionDetail(applicationNo, state_cd, Integer.parseInt(off_cd), TableList.VT_PERMIT));
                }
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

        return isPermitVehicle;
    }

    public void reset() {
        applicationNo = "";
        permitNo = "";
        validFrom = "";
        regnNo = "";
        validUpto = "";
        purCd = "";
        permitType = "";
        pmtCatg = "";
        goodToCarry = "";
        journeyPur = "";
        parking = "";
        replaceDt = "";
        state_cd = "";
        off_cd = "";
    }

    /**
     * @return the applicationNo
     */
    public String getApplicationNo() {
        return applicationNo;
    }

    /**
     * @param applicationNo the applicationNo to set
     */
    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    /**
     * @return the permitNo
     */
    public String getPermitNo() {
        return permitNo;
    }

    /**
     * @param permitNo the permitNo to set
     */
    public void setPermitNo(String permitNo) {
        this.permitNo = permitNo;
    }

    /**
     * @return the validFrom
     */
    public String getValidFrom() {
        return validFrom;
    }

    /**
     * @param validFrom the validFrom to set
     */
    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the validUpto
     */
    public String getValidUpto() {
        return validUpto;
    }

    /**
     * @param validUpto the validUpto to set
     */
    public void setValidUpto(String validUpto) {
        this.validUpto = validUpto;
    }

    /**
     * @return the purCd
     */
    public String getPurCd() {
        return purCd;
    }

    /**
     * @param purCd the purCd to set
     */
    public void setPurCd(String purCd) {
        this.purCd = purCd;
    }

    /**
     * @return the permitType
     */
    public String getPermitType() {
        return permitType;
    }

    /**
     * @param permitType the permitType to set
     */
    public void setPermitType(String permitType) {
        this.permitType = permitType;
    }

    /**
     * @return the pmtCatg
     */
    public String getPmtCatg() {
        return pmtCatg;
    }

    /**
     * @param pmtCatg the pmtCatg to set
     */
    public void setPmtCatg(String pmtCatg) {
        this.pmtCatg = pmtCatg;
    }

    /**
     * @return the goodToCarry
     */
    public String getGoodToCarry() {
        return goodToCarry;
    }

    /**
     * @param goodToCarry the goodToCarry to set
     */
    public void setGoodToCarry(String goodToCarry) {
        this.goodToCarry = goodToCarry;
    }

    /**
     * @return the journeyPur
     */
    public String getJourneyPur() {
        return journeyPur;
    }

    /**
     * @param journeyPur the journeyPur to set
     */
    public void setJourneyPur(String journeyPur) {
        this.journeyPur = journeyPur;
    }

    /**
     * @return the parking
     */
    public String getParking() {
        return parking;
    }

    /**
     * @param parking the parking to set
     */
    public void setParking(String parking) {
        this.parking = parking;
    }

    /**
     * @return the replaceDt
     */
    public String getReplaceDt() {
        return replaceDt;
    }

    /**
     * @param replaceDt the replaceDt to set
     */
    public void setReplaceDt(String replaceDt) {
        this.replaceDt = replaceDt;
    }

    /**
     * @return the showAuthDetails
     */
    public boolean isShowAuthDetails() {
        return showAuthDetails;
    }

    /**
     * @param showAuthDetails the showAuthDetails to set
     */
    public void setShowAuthDetails(boolean showAuthDetails) {
        this.showAuthDetails = showAuthDetails;
    }

    /**
     * @return the authNo
     */
    public String getAuthNo() {
        return authNo;
    }

    /**
     * @param authNo the authNo to set
     */
    public void setAuthNo(String authNo) {
        this.authNo = authNo;
    }

    /**
     * @return the authFrom
     */
    public String getAuthFrom() {
        return authFrom;
    }

    /**
     * @param authFrom the authFrom to set
     */
    public void setAuthFrom(String authFrom) {
        this.authFrom = authFrom;
    }

    /**
     * @return the authTo
     */
    public String getAuthTo() {
        return authTo;
    }

    /**
     * @param authTo the authTo to set
     */
    public void setAuthTo(String authTo) {
        this.authTo = authTo;
    }

    /**
     * @return the permitCd
     */
    public String getPermitCd() {
        return permitCd;
    }

    /**
     * @param permitCd the permitCd to set
     */
    public void setPermitCd(String permitCd) {
        this.permitCd = permitCd;
    }

    public boolean isShowTempPermitDetails() {
        return showTempPermitDetails;
    }

    public void setShowTempPermitDetails(boolean showTempPermitDetails) {
        this.showTempPermitDetails = showTempPermitDetails;
    }

    public PermitDetailDobj getTmp_Dobj() {
        return tmp_Dobj;
    }

    public void setTmp_Dobj(PermitDetailDobj tmp_Dobj) {
        this.tmp_Dobj = tmp_Dobj;
    }

    public List<PassengerPermitDetailDobj> getRoutedata() {
        return routedata;
    }

    public void setRoutedata(List<PassengerPermitDetailDobj> routedata) {
        this.routedata = routedata;
    }

    public String getRegion_covered() {
        return region_covered;
    }

    public void setRegion_covered(String region_covered) {
        this.region_covered = region_covered;
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

    public boolean isOtherRouteAllow() {
        return otherRouteAllow;
    }

    public void setOtherRouteAllow(boolean otherRouteAllow) {
        this.otherRouteAllow = otherRouteAllow;
    }
}
