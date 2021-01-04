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
public class TempRCReportDobj implements Serializable {

    private String reportHeading;
    private String reportSubHeading;
    private String offName;
    private String tempRegnNo;
    private String ownerName;
    private String fatherName;
    private String ownerAddress;
    private String vchDesc;
    private String makerName;
    private String bodyType;
    private String seatingCap;
    private String color;
    private String engineNo;
    private String chasiNo;
    private String fincierDtls = null;
    private String tempRegnFrom;
    private String tempRegnUpto;
    private String feeAmt;
    private String feeRectNo;
    private String feeRcptDate;
    private String taxAmt;
    private String taxRcptNo;
    private String taxFromdate;
    private String taxUptoDate;
    private String printedOn;
    private String statecdto;
    private String offcdto;
    private String purpose;
    private String bodybuilding;
    private String tmpmsg;
    private String offNameto;
    private String paddress;
    private String bodybildaddress;
    private String issueDate;
    private String tax1Amt = null;
    private String tax2Amt = null;
    private String fineAmt;
    private String rebateAmt;
    private String surchargeAmt;
    private String penaltyAmt;
    private String interestAmt;
    private String stateName;
    private boolean notKAState;
    private String tempRCHeading;
    private String image_background;
    private String image_logo;
    private boolean show_image_background;
    private boolean show_image_logo;
    private String remarks = null;
    private String makerModelName;
    private byte[] userSign;
    private boolean isUserSignExist;
    private String appl_no;
    private boolean showRoadSafetySlogan;
    private VmRoadSafetySloganPrintDobj roadSafetySloganDobj = null;
    /*
     * Added by imroz khan
     */
    private String qrText;

    /**
     * @return the reportHeading
     */
    public String getReportHeading() {
        return reportHeading;
    }

    /**
     * @param reportHeading the reportHeading to set
     */
    public void setReportHeading(String reportHeading) {
        this.reportHeading = reportHeading;
    }

    /**
     * @return the reportSubHeading
     */
    public String getReportSubHeading() {
        return reportSubHeading;
    }

    /**
     * @param reportSubHeading the reportSubHeading to set
     */
    public void setReportSubHeading(String reportSubHeading) {
        this.reportSubHeading = reportSubHeading;
    }

    /**
     * @return the offName
     */
    public String getOffName() {
        return offName;
    }

    /**
     * @param offName the offName to set
     */
    public void setOffName(String offName) {
        this.offName = offName;
    }

    /**
     * @return the tempRegnNo
     */
    public String getTempRegnNo() {
        return tempRegnNo;
    }

    /**
     * @param tempRegnNo the tempRegnNo to set
     */
    public void setTempRegnNo(String tempRegnNo) {
        this.tempRegnNo = tempRegnNo;
    }

    /**
     * @return the ownerName
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * @param ownerName the ownerName to set
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * @return the ownerAddress
     */
    public String getOwnerAddress() {
        return ownerAddress;
    }

    /**
     * @param ownerAddress the ownerAddress to set
     */
    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    /**
     * @return the vchDesc
     */
    public String getVchDesc() {
        return vchDesc;
    }

    /**
     * @param vchDesc the vchDesc to set
     */
    public void setVchDesc(String vchDesc) {
        this.vchDesc = vchDesc;
    }

    /**
     * @return the makerName
     */
    public String getMakerName() {
        return makerName;
    }

    /**
     * @param makerName the makerName to set
     */
    public void setMakerName(String makerName) {
        this.makerName = makerName;
    }

    /**
     * @return the bodyType
     */
    public String getBodyType() {
        return bodyType;
    }

    /**
     * @param bodyType the bodyType to set
     */
    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    /**
     * @return the seatingCap
     */
    public String getSeatingCap() {
        return seatingCap;
    }

    /**
     * @param seatingCap the seatingCap to set
     */
    public void setSeatingCap(String seatingCap) {
        this.seatingCap = seatingCap;
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
     * @return the engineNo
     */
    public String getEngineNo() {
        return engineNo;
    }

    /**
     * @param engineNo the engineNo to set
     */
    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    /**
     * @return the chasiNo
     */
    public String getChasiNo() {
        return chasiNo;
    }

    /**
     * @param chasiNo the chasiNo to set
     */
    public void setChasiNo(String chasiNo) {
        this.chasiNo = chasiNo;
    }

    /**
     * @return the fincierDtls
     */
    public String getFincierDtls() {
        return fincierDtls;
    }

    /**
     * @param fincierDtls the fincierDtls to set
     */
    public void setFincierDtls(String fincierDtls) {
        this.fincierDtls = fincierDtls;
    }

    /**
     * @return the tempRegnFrom
     */
    public String getTempRegnFrom() {
        return tempRegnFrom;
    }

    /**
     * @param tempRegnFrom the tempRegnFrom to set
     */
    public void setTempRegnFrom(String tempRegnFrom) {
        this.tempRegnFrom = tempRegnFrom;
    }

    /**
     * @return the tempRegnUpto
     */
    public String getTempRegnUpto() {
        return tempRegnUpto;
    }

    /**
     * @param tempRegnUpto the tempRegnUpto to set
     */
    public void setTempRegnUpto(String tempRegnUpto) {
        this.tempRegnUpto = tempRegnUpto;
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
     * @return the feeRectNo
     */
    public String getFeeRectNo() {
        return feeRectNo;
    }

    /**
     * @param feeRectNo the feeRectNo to set
     */
    public void setFeeRectNo(String feeRectNo) {
        this.feeRectNo = feeRectNo;
    }

    /**
     * @return the feeRcptDate
     */
    public String getFeeRcptDate() {
        return feeRcptDate;
    }

    /**
     * @param feeRcptDate the feeRcptDate to set
     */
    public void setFeeRcptDate(String feeRcptDate) {
        this.feeRcptDate = feeRcptDate;
    }

    /**
     * @return the taxAmt
     */
    public String getTaxAmt() {
        return taxAmt;
    }

    /**
     * @param taxAmt the taxAmt to set
     */
    public void setTaxAmt(String taxAmt) {
        this.taxAmt = taxAmt;
    }

    /**
     * @return the taxRcptNo
     */
    public String getTaxRcptNo() {
        return taxRcptNo;
    }

    /**
     * @param taxRcptNo the taxRcptNo to set
     */
    public void setTaxRcptNo(String taxRcptNo) {
        this.taxRcptNo = taxRcptNo;
    }

    /**
     * @return the taxFromdate
     */
    public String getTaxFromdate() {
        return taxFromdate;
    }

    /**
     * @param taxFromdate the taxFromdate to set
     */
    public void setTaxFromdate(String taxFromdate) {
        this.taxFromdate = taxFromdate;
    }

    /**
     * @return the taxUptoDate
     */
    public String getTaxUptoDate() {
        return taxUptoDate;
    }

    /**
     * @param taxUptoDate the taxUptoDate to set
     */
    public void setTaxUptoDate(String taxUptoDate) {
        this.taxUptoDate = taxUptoDate;
    }

    /**
     * @return the printedOn
     */
    public String getPrintedOn() {
        return printedOn;
    }

    /**
     * @param printedOn the printedOn to set
     */
    public void setPrintedOn(String printedOn) {
        this.printedOn = printedOn;
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
     * @return the statecdto
     */
    public String getStatecdto() {
        return statecdto;
    }

    /**
     * @param statecdto the statecdto to set
     */
    public void setStatecdto(String statecdto) {
        this.statecdto = statecdto;
    }

    /**
     * @return the offcdto
     */
    public String getOffcdto() {
        return offcdto;
    }

    /**
     * @param offcdto the offcdto to set
     */
    public void setOffcdto(String offcdto) {
        this.offcdto = offcdto;
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
     * @return the bodybuilding
     */
    public String getBodybuilding() {
        return bodybuilding;
    }

    /**
     * @param bodybuilding the bodybuilding to set
     */
    public void setBodybuilding(String bodybuilding) {
        this.bodybuilding = bodybuilding;
    }

    /**
     * @return the tmpmsg
     */
    public String getTmpmsg() {
        return tmpmsg;
    }

    /**
     * @param tmpmsg the tmpmsg to set
     */
    public void setTmpmsg(String tmpmsg) {
        this.tmpmsg = tmpmsg;
    }

    /**
     * @return the offNameto
     */
    public String getOffNameto() {
        return offNameto;
    }

    /**
     * @param offNameto the offNameto to set
     */
    public void setOffNameto(String offNameto) {
        this.offNameto = offNameto;
    }

    /**
     * @return the paddress
     */
    public String getPaddress() {
        return paddress;
    }

    /**
     * @param paddress the paddress to set
     */
    public void setPaddress(String paddress) {
        this.paddress = paddress;
    }

    /**
     * @return the bodybildaddress
     */
    public String getBodybildaddress() {
        return bodybildaddress;
    }

    /**
     * @param bodybildaddress the bodybildaddress to set
     */
    public void setBodybildaddress(String bodybildaddress) {
        this.bodybildaddress = bodybildaddress;
    }

    /**
     * @return the fatherName
     */
    public String getFatherName() {
        return fatherName;
    }

    /**
     * @param fatherName the fatherName to set
     */
    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    /**
     * @return the issueDate
     */
    public String getIssueDate() {
        return issueDate;
    }

    /**
     * @param issueDate the issueDate to set
     */
    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    /**
     * @return the tax1Amt
     */
    public String getTax1Amt() {
        return tax1Amt;
    }

    /**
     * @param tax1Amt the tax1Amt to set
     */
    public void setTax1Amt(String tax1Amt) {
        this.tax1Amt = tax1Amt;
    }

    /**
     * @return the tax2Amt
     */
    public String getTax2Amt() {
        return tax2Amt;
    }

    /**
     * @param tax2Amt the tax2Amt to set
     */
    public void setTax2Amt(String tax2Amt) {
        this.tax2Amt = tax2Amt;
    }

    /**
     * @return the fineAmt
     */
    public String getFineAmt() {
        return fineAmt;
    }

    /**
     * @param fineAmt the fineAmt to set
     */
    public void setFineAmt(String fineAmt) {
        this.fineAmt = fineAmt;
    }

    /**
     * @return the rebateAmt
     */
    public String getRebateAmt() {
        return rebateAmt;
    }

    /**
     * @param rebateAmt the rebateAmt to set
     */
    public void setRebateAmt(String rebateAmt) {
        this.rebateAmt = rebateAmt;
    }

    /**
     * @return the surchargeAmt
     */
    public String getSurchargeAmt() {
        return surchargeAmt;
    }

    /**
     * @param surchargeAmt the surchargeAmt to set
     */
    public void setSurchargeAmt(String surchargeAmt) {
        this.surchargeAmt = surchargeAmt;
    }

    /**
     * @return the penaltyAmt
     */
    public String getPenaltyAmt() {
        return penaltyAmt;
    }

    /**
     * @param penaltyAmt the penaltyAmt to set
     */
    public void setPenaltyAmt(String penaltyAmt) {
        this.penaltyAmt = penaltyAmt;
    }

    /**
     * @return the interestAmt
     */
    public String getInterestAmt() {
        return interestAmt;
    }

    /**
     * @param interestAmt the interestAmt to set
     */
    public void setInterestAmt(String interestAmt) {
        this.interestAmt = interestAmt;
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
     * @return the notKAState
     */
    public boolean isNotKAState() {
        return notKAState;
    }

    /**
     * @param notKAState the notKAState to set
     */
    public void setNotKAState(boolean notKAState) {
        this.notKAState = notKAState;
    }

    /**
     * @return the tempRCHeading
     */
    public String getTempRCHeading() {
        return tempRCHeading;
    }

    /**
     * @param tempRCHeading the tempRCHeading to set
     */
    public void setTempRCHeading(String tempRCHeading) {
        this.tempRCHeading = tempRCHeading;
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
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * @return the makerModelName
     */
    public String getMakerModelName() {
        return makerModelName;
    }

    /**
     * @param makerModelName the makerModelName to set
     */
    public void setMakerModelName(String makerModelName) {
        this.makerModelName = makerModelName;
    }

    /**
     * @return the userSign
     */
    public byte[] getUserSign() {
        return userSign;
    }

    /**
     * @param userSign the userSign to set
     */
    public void setUserSign(byte[] userSign) {
        this.userSign = userSign;
    }

    /**
     * @return the isUserSignExist
     */
    public boolean isIsUserSignExist() {
        return isUserSignExist;
    }

    /**
     * @param isUserSignExist the isUserSignExist to set
     */
    public void setIsUserSignExist(boolean isUserSignExist) {
        this.isUserSignExist = isUserSignExist;
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
