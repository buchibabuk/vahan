/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;

/**
 *
 * @author nic
 */
public class FCPrintReportDobj implements Serializable {

    private String header;
    private String stateCD;
    private String applNO;
    private String stateName;
    private String offName;
    private String regnNO;
    private String vchDescr;
    private String fitUpto;
    private String nid;
    private String subHeader;
    private String printed_on;
    private String fitInfecOff1;
    private String fitInfecOff2;
    private boolean isFitInfecOff1;
    private boolean isFitInfecOff2;
    private String fitResult;
    private boolean isfitFailed;
    private String fitFailMsg;
    private String fitCheckDate;
    private String feeAmt;
    private String rcptno;
    private String rcptdt;
    private String eng_no;
    private String body_type;
    private String seat_cap;
    private String manu_yr;
    private String vch_catg;
    private String regn_no;
    private String chasi_no;
    private String qrText;
    private String fcReportLabel;
    private String fcRemark;
    private boolean isFitDoneInOtherState;
    private String inspectingOfficerOneDesig;
    private byte[] signFitOff1;
    private byte[] signFitOff2;
    private boolean isSignFitOff1;
    private boolean isSignFitOff2;
    private String image_background;
    private String image_logo;
    private boolean show_image_background;
    private boolean show_image_logo;
    private String formName;
    private boolean showTextInCaseoftransportvehiclesonly;
    private String offName38A;
    private boolean noteOffName38A;
    private boolean showRoadSafetySlogan;
    private VmRoadSafetySloganPrintDobj roadSafetySloganDobj = null;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getStateCD() {
        return stateCD;
    }

    public void setStateCD(String stateCD) {
        this.stateCD = stateCD;
    }

    public String getApplNO() {
        return applNO;
    }

    public void setApplNO(String applNO) {
        this.applNO = applNO;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getOffName() {
        return offName;
    }

    public void setOffName(String offName) {
        this.offName = offName;
    }

    public String getRegnNO() {
        return regnNO;
    }

    public void setRegnNO(String regnNO) {
        this.regnNO = regnNO;
    }

    public String getVchDescr() {
        return vchDescr;
    }

    public void setVchDescr(String vchDescr) {
        this.vchDescr = vchDescr;
    }

    public String getFitUpto() {
        return fitUpto;
    }

    public void setFitUpto(String fitUpto) {
        this.fitUpto = fitUpto;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
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
     * @return the fitInfecOff1
     */
    public String getFitInfecOff1() {
        return fitInfecOff1;
    }

    /**
     * @param fitInfecOff1 the fitInfecOff1 to set
     */
    public void setFitInfecOff1(String fitInfecOff1) {
        this.fitInfecOff1 = fitInfecOff1;
    }

    /**
     * @return the isFitInfecOff1
     */
    public boolean isIsFitInfecOff1() {
        return isFitInfecOff1;
    }

    /**
     * @param isFitInfecOff1 the isFitInfecOff1 to set
     */
    public void setIsFitInfecOff1(boolean isFitInfecOff1) {
        this.isFitInfecOff1 = isFitInfecOff1;
    }

    /**
     * @return the fitInfecOff2
     */
    public String getFitInfecOff2() {
        return fitInfecOff2;
    }

    /**
     * @param fitInfecOff2 the fitInfecOff2 to set
     */
    public void setFitInfecOff2(String fitInfecOff2) {
        this.fitInfecOff2 = fitInfecOff2;
    }

    /**
     * @return the isFitInfecOff2
     */
    public boolean isIsFitInfecOff2() {
        return isFitInfecOff2;
    }

    /**
     * @param isFitInfecOff2 the isFitInfecOff2 to set
     */
    public void setIsFitInfecOff2(boolean isFitInfecOff2) {
        this.isFitInfecOff2 = isFitInfecOff2;
    }

    /**
     * @return the fitResult
     */
    public String getFitResult() {
        return fitResult;
    }

    /**
     * @param fitResult the fitResult to set
     */
    public void setFitResult(String fitResult) {
        this.fitResult = fitResult;
    }

    /**
     * @return the isfitFailed
     */
    public boolean isIsfitFailed() {
        return isfitFailed;
    }

    /**
     * @param isfitFailed the isfitFailed to set
     */
    public void setIsfitFailed(boolean isfitFailed) {
        this.isfitFailed = isfitFailed;
    }

    /**
     * @return the fitFailMsg
     */
    public String getFitFailMsg() {
        return fitFailMsg;
    }

    /**
     * @param fitFailMsg the fitFailMsg to set
     */
    public void setFitFailMsg(String fitFailMsg) {
        this.fitFailMsg = fitFailMsg;
    }

    /**
     * @return the fitCheckDate
     */
    public String getFitCheckDate() {
        return fitCheckDate;
    }

    /**
     * @param fitCheckDate the fitCheckDate to set
     */
    public void setFitCheckDate(String fitCheckDate) {
        this.fitCheckDate = fitCheckDate;
    }

    /**
     * @return the feeAmt
     */
    public String getFeeAmt() {
        return feeAmt;
    }

    /**
     * @param feeAmt the feeAmt to set
     */
    public void setFeeAmt(String feeAmt) {
        this.feeAmt = feeAmt;
    }

    /**
     * @return the rcptno
     */
    public String getRcptno() {
        return rcptno;
    }

    /**
     * @param rcptno the rcptno to set
     */
    public void setRcptno(String rcptno) {
        this.rcptno = rcptno;
    }

    /**
     * @return the rcptdt
     */
    public String getRcptdt() {
        return rcptdt;
    }

    /**
     * @param rcptdt the rcptdt to set
     */
    public void setRcptdt(String rcptdt) {
        this.rcptdt = rcptdt;
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
     * @return the manu_yr
     */
    public String getManu_yr() {
        return manu_yr;
    }

    /**
     * @param manu_yr the manu_yr to set
     */
    public void setManu_yr(String manu_yr) {
        this.manu_yr = manu_yr;
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
     * @return the qrText
     */
    public String getQrText() {
        return qrText;
    }

    /**
     * @param qrText the qrText to set
     */
    public void setQrText(String qrText) {
        this.qrText = qrText;
    }

    /**
     * @return the fcReportLabel
     */
    public String getFcReportLabel() {
        return fcReportLabel;
    }

    /**
     * @param fcReportLabel the fcReportLabel to set
     */
    public void setFcReportLabel(String fcReportLabel) {
        this.fcReportLabel = fcReportLabel;
    }

    /**
     * @return the fcRemark
     */
    public String getFcRemark() {
        return fcRemark;
    }

    /**
     * @param fcRemark the fcRemark to set
     */
    public void setFcRemark(String fcRemark) {
        this.fcRemark = fcRemark;
    }

    /**
     * @return the isFitDoneInOtherState
     */
    public boolean isIsFitDoneInOtherState() {
        return isFitDoneInOtherState;
    }

    /**
     * @param isFitDoneInOtherState the isFitDoneInOtherState to set
     */
    public void setIsFitDoneInOtherState(boolean isFitDoneInOtherState) {
        this.isFitDoneInOtherState = isFitDoneInOtherState;
    }

    /**
     * @return the inspectingOfficerOneDesig
     */
    public String getInspectingOfficerOneDesig() {
        return inspectingOfficerOneDesig;
    }

    /**
     * @param inspectingOfficerOneDesig the inspectingOfficerOneDesig to set
     */
    public void setInspectingOfficerOneDesig(String inspectingOfficerOneDesig) {
        this.inspectingOfficerOneDesig = inspectingOfficerOneDesig;
    }

    /**
     * @return the signFitOff1
     */
    public byte[] getSignFitOff1() {
        return signFitOff1;
    }

    /**
     * @param signFitOff1 the signFitOff1 to set
     */
    public void setSignFitOff1(byte[] signFitOff1) {
        this.signFitOff1 = signFitOff1;
    }

    /**
     * @return the signFitOff2
     */
    public byte[] getSignFitOff2() {
        return signFitOff2;
    }

    /**
     * @param signFitOff2 the signFitOff2 to set
     */
    public void setSignFitOff2(byte[] signFitOff2) {
        this.signFitOff2 = signFitOff2;
    }

    /**
     * @return the isSignFitOff1
     */
    public boolean isIsSignFitOff1() {
        return isSignFitOff1;
    }

    /**
     * @param isSignFitOff1 the isSignFitOff1 to set
     */
    public void setIsSignFitOff1(boolean isSignFitOff1) {
        this.isSignFitOff1 = isSignFitOff1;
    }

    /**
     * @return the isSignFitOff2
     */
    public boolean isIsSignFitOff2() {
        return isSignFitOff2;
    }

    /**
     * @param isSignFitOff2 the isSignFitOff2 to set
     */
    public void setIsSignFitOff2(boolean isSignFitOff2) {
        this.isSignFitOff2 = isSignFitOff2;
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
     * @return the formName
     */
    public String getFormName() {
        return formName;
    }

    /**
     * @param formName the formName to set
     */
    public void setFormName(String formName) {
        this.formName = formName;
    }

    /**
     * @return the showTextInCaseoftransportvehiclesonly
     */
    public boolean isShowTextInCaseoftransportvehiclesonly() {
        return showTextInCaseoftransportvehiclesonly;
    }

    /**
     * @param showTextInCaseoftransportvehiclesonly the
     * showTextInCaseoftransportvehiclesonly to set
     */
    public void setShowTextInCaseoftransportvehiclesonly(boolean showTextInCaseoftransportvehiclesonly) {
        this.showTextInCaseoftransportvehiclesonly = showTextInCaseoftransportvehiclesonly;
    }

    /**
     * @return the offName38A
     */
    public String getOffName38A() {
        return offName38A;
    }

    /**
     * @param offName38A the offName38A to set
     */
    public void setOffName38A(String offName38A) {
        this.offName38A = offName38A;
    }

    /**
     * @return the noteOffName38A
     */
    public boolean isNoteOffName38A() {
        return noteOffName38A;
    }

    /**
     * @param noteOffName38A the noteOffName38A to set
     */
    public void setNoteOffName38A(boolean noteOffName38A) {
        this.noteOffName38A = noteOffName38A;
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
}
