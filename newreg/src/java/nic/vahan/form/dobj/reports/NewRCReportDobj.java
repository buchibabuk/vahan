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
public class NewRCReportDobj implements Serializable {

    private String stateName;
    //private boolean watermark;
    private String offName;
    private String regnNo;
    private String regnDt;
    private String purchaseDt;
    private String isexempted;
    private String ownerName;
    private String ownership;
    private int ownerSrno;
    private int srno;
    private String fatherName;
    private String currFullAddr;
    private String perFullAddr;
    private String govVeh;
    private String regnType;
    private String vchDesc;
    private String chasiNo;
    private String engNo;
    private String bodyType;
    private String makerName;
    private String modelName;
    private String noofCyl;
    private String hpower;
    private String seatCap;
    private String standCap;
    private String sleepCap;
    private String ldwt;
    private String unldwt;
    private String gcw;
    private String fuelDesc;
    private String color;
    private String manuMnt;
    private String manuYr;
    private String normDesc;
    private String wheelBase;
    private String mobile;
    private String cubicCap;
    private String floorArea;
    private String acfitted;
    private String videoFitted;
    private String audioFitted;
    private String vehPurchAS;
    private String vehCatg;
    private String dealerAddress;
    private String saleAmt;
    private String garageAddr;
    private String length;
    private String width;
    private String height;
    private String regnUpto;
    private String fitUpto;
    private String annualInm;
    private String impVeh;
    private String fdesc;
    private String rdesc;
    private String odesc;
    private String tdesc;
    private String fweight;
    private String rweight;
    private String oweight;
    private String tweight;
    private String trChasino;
    private String trBodyType;
    private String trldwt;
    private String trunldwt;
    private String hyptFromdt;
    private String trfdesc;
    private String trrdesc;
    private String trodesc;
    private String trtdesc;
    private String trfweight;
    private String trrweight;
    private String troweight;
    private String trtweight;
    private String fncrFullAddr;
    private String taxFrom;
    private String taxUpto;
    private String taxAmt;
    private String rcptno;
    private String opdate;
    private String hsrpfront;
    private String hsrpback;
    private String subHeader;
    private String header;
    private boolean istransport;
    private String dealercd;
    private boolean istransportWithSemTrailler;
    private boolean isfncr;
    private String printedon;
    private String purposeDesc;
    private String printedSign;
    private String otherCriteria;
    private String serviceType;
    private boolean isServiceType;
    private int fuel;
    private int rcCount;
    private boolean pageBreakforHR;
    private boolean pageBreakExceptHR;
    private String smartcardFrontPanel;
    private String smartcardBackPanel;
    private boolean ishypothecated;
    private String fncrname;
    private boolean nontransport;
    private boolean iscng;
    private String kitCylno;
    private String stateCD;
    private String linkVehicleno;
    private String permitCatg;
    private String transactionDate;
    private boolean showTransactionDate;
    private String preOwner;
    private String oldState;
    private String preRegno;
    private String entryDate;
    private String transferDate;
    private String conversionDate;
    private String qrText;
    private byte[] userSign;
    private boolean isUserSignExist;
    private String topMarginOnPlasticRC;
    private String image_background;
    private String image_logo;
    private boolean show_image_background;
    private boolean show_image_logo;
    private boolean isvch_purchase_as;
    private String showhideborder;
    private boolean showRoadSafetySlogan;
    private VmRoadSafetySloganPrintDobj roadSafetySloganDobj = null;

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
     * @return the regnDt
     */
    public String getRegnDt() {
        return regnDt;
    }

    /**
     * @param regnDt the regnDt to set
     */
    public void setRegnDt(String regnDt) {
        this.regnDt = regnDt;
    }

    /**
     * @return the purchaseDt
     */
    public String getPurchaseDt() {
        return purchaseDt;
    }

    /**
     * @param purchaseDt the purchaseDt to set
     */
    public void setPurchaseDt(String purchaseDt) {
        this.purchaseDt = purchaseDt;
    }

    /**
     * @return the isexempted
     */
    public String getIsexempted() {
        return isexempted;
    }

    /**
     * @param isexempted the isexempted to set
     */
    public void setIsexempted(String isexempted) {
        this.isexempted = isexempted;
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
     * @return the currFullAddr
     */
    public String getCurrFullAddr() {
        return currFullAddr;
    }

    /**
     * @param currFullAddr the currFullAddr to set
     */
    public void setCurrFullAddr(String currFullAddr) {
        this.currFullAddr = currFullAddr;
    }

    /**
     * @return the perFullAddr
     */
    public String getPerFullAddr() {
        return perFullAddr;
    }

    /**
     * @param perFullAddr the perFullAddr to set
     */
    public void setPerFullAddr(String perFullAddr) {
        this.perFullAddr = perFullAddr;
    }

    /**
     * @return the govVeh
     */
    public String getGovVeh() {
        return govVeh;
    }

    /**
     * @param govVeh the govVeh to set
     */
    public void setGovVeh(String govVeh) {
        this.govVeh = govVeh;
    }

    /**
     * @return the regnType
     */
    public String getRegnType() {
        return regnType;
    }

    /**
     * @param regnType the regnType to set
     */
    public void setRegnType(String regnType) {
        this.regnType = regnType;
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
     * @return the engNo
     */
    public String getEngNo() {
        return engNo;
    }

    /**
     * @param engNo the engNo to set
     */
    public void setEngNo(String engNo) {
        this.engNo = engNo;
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
     * @return the modelName
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * @param modelName the modelName to set
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * @return the noofCyl
     */
    public String getNoofCyl() {
        return noofCyl;
    }

    /**
     * @param noofCyl the noofCyl to set
     */
    public void setNoofCyl(String noofCyl) {
        this.noofCyl = noofCyl;
    }

    /**
     * @return the hpower
     */
    public String getHpower() {
        return hpower;
    }

    /**
     * @param hpower the hpower to set
     */
    public void setHpower(String hpower) {
        this.hpower = hpower;
    }

    /**
     * @return the seatCap
     */
    public String getSeatCap() {
        return seatCap;
    }

    /**
     * @param seatCap the seatCap to set
     */
    public void setSeatCap(String seatCap) {
        this.seatCap = seatCap;
    }

    /**
     * @return the standCap
     */
    public String getStandCap() {
        return standCap;
    }

    /**
     * @param standCap the standCap to set
     */
    public void setStandCap(String standCap) {
        this.standCap = standCap;
    }

    /**
     * @return the sleepCap
     */
    public String getSleepCap() {
        return sleepCap;
    }

    /**
     * @param sleepCap the sleepCap to set
     */
    public void setSleepCap(String sleepCap) {
        this.sleepCap = sleepCap;
    }

    /**
     * @return the ldwt
     */
    public String getLdwt() {
        return ldwt;
    }

    /**
     * @param ldwt the ldwt to set
     */
    public void setLdwt(String ldwt) {
        this.ldwt = ldwt;
    }

    /**
     * @return the unldwt
     */
    public String getUnldwt() {
        return unldwt;
    }

    /**
     * @param unldwt the unldwt to set
     */
    public void setUnldwt(String unldwt) {
        this.unldwt = unldwt;
    }

    /**
     * @return the gcw
     */
    public String getGcw() {
        return gcw;
    }

    /**
     * @param gcw the gcw to set
     */
    public void setGcw(String gcw) {
        this.gcw = gcw;
    }

    /**
     * @return the fuelDesc
     */
    public String getFuelDesc() {
        return fuelDesc;
    }

    /**
     * @param fuelDesc the fuelDesc to set
     */
    public void setFuelDesc(String fuelDesc) {
        this.fuelDesc = fuelDesc;
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
     * @return the manuMnt
     */
    public String getManuMnt() {
        return manuMnt;
    }

    /**
     * @param manuMnt the manuMnt to set
     */
    public void setManuMnt(String manuMnt) {
        this.manuMnt = manuMnt;
    }

    /**
     * @return the manuYr
     */
    public String getManuYr() {
        return manuYr;
    }

    /**
     * @param manuYr the manuYr to set
     */
    public void setManuYr(String manuYr) {
        this.manuYr = manuYr;
    }

    /**
     * @return the normDesc
     */
    public String getNormDesc() {
        return normDesc;
    }

    /**
     * @param normDesc the normDesc to set
     */
    public void setNormDesc(String normDesc) {
        this.normDesc = normDesc;
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
     * @return the cubicCap
     */
    public String getCubicCap() {
        return cubicCap;
    }

    /**
     * @param cubicCap the cubicCap to set
     */
    public void setCubicCap(String cubicCap) {
        this.cubicCap = cubicCap;
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
     * @return the acfitted
     */
    public String getAcfitted() {
        return acfitted;
    }

    /**
     * @param acfitted the acfitted to set
     */
    public void setAcfitted(String acfitted) {
        this.acfitted = acfitted;
    }

    /**
     * @return the videoFitted
     */
    public String getVideoFitted() {
        return videoFitted;
    }

    /**
     * @param videoFitted the videoFitted to set
     */
    public void setVideoFitted(String videoFitted) {
        this.videoFitted = videoFitted;
    }

    /**
     * @return the audioFitted
     */
    public String getAudioFitted() {
        return audioFitted;
    }

    /**
     * @param audioFitted the audioFitted to set
     */
    public void setAudioFitted(String audioFitted) {
        this.audioFitted = audioFitted;
    }

    /**
     * @return the vehPurchAS
     */
    public String getVehPurchAS() {
        return vehPurchAS;
    }

    /**
     * @param vehPurchAS the vehPurchAS to set
     */
    public void setVehPurchAS(String vehPurchAS) {
        this.vehPurchAS = vehPurchAS;
    }

    /**
     * @return the vehCatg
     */
    public String getVehCatg() {
        return vehCatg;
    }

    /**
     * @param vehCatg the vehCatg to set
     */
    public void setVehCatg(String vehCatg) {
        this.vehCatg = vehCatg;
    }

    /**
     * @return the dealerAddress
     */
    public String getDealerAddress() {
        return dealerAddress;
    }

    /**
     * @param dealerAddress the dealerAddress to set
     */
    public void setDealerAddress(String dealerAddress) {
        this.dealerAddress = dealerAddress;
    }

    /**
     * @return the saleAmt
     */
    public String getSaleAmt() {
        return saleAmt;
    }

    /**
     * @param saleAmt the saleAmt to set
     */
    public void setSaleAmt(String saleAmt) {
        this.saleAmt = saleAmt;
    }

    /**
     * @return the garageAddr
     */
    public String getGarageAddr() {
        return garageAddr;
    }

    /**
     * @param garageAddr the garageAddr to set
     */
    public void setGarageAddr(String garageAddr) {
        this.garageAddr = garageAddr;
    }

    /**
     * @return the length
     */
    public String getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(String length) {
        this.length = length;
    }

    /**
     * @return the width
     */
    public String getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public String getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(String height) {
        this.height = height;
    }

    /**
     * @return the regnUpto
     */
    public String getRegnUpto() {
        return regnUpto;
    }

    /**
     * @param regnUpto the regnUpto to set
     */
    public void setRegnUpto(String regnUpto) {
        this.regnUpto = regnUpto;
    }

    /**
     * @return the fitUpto
     */
    public String getFitUpto() {
        return fitUpto;
    }

    /**
     * @param fitUpto the fitUpto to set
     */
    public void setFitUpto(String fitUpto) {
        this.fitUpto = fitUpto;
    }

    /**
     * @return the annualInm
     */
    public String getAnnualInm() {
        return annualInm;
    }

    /**
     * @param annualInm the annualInm to set
     */
    public void setAnnualInm(String annualInm) {
        this.annualInm = annualInm;
    }

    /**
     * @return the impVeh
     */
    public String getImpVeh() {
        return impVeh;
    }

    /**
     * @param impVeh the impVeh to set
     */
    public void setImpVeh(String impVeh) {
        this.impVeh = impVeh;
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
     * @return the trChasino
     */
    public String getTrChasino() {
        return trChasino;
    }

    /**
     * @param trChasino the trChasino to set
     */
    public void setTrChasino(String trChasino) {
        this.trChasino = trChasino;
    }

    /**
     * @return the trBodyType
     */
    public String getTrBodyType() {
        return trBodyType;
    }

    /**
     * @param trBodyType the trBodyType to set
     */
    public void setTrBodyType(String trBodyType) {
        this.trBodyType = trBodyType;
    }

    /**
     * @return the trldwt
     */
    public String getTrldwt() {
        return trldwt;
    }

    /**
     * @param trldwt the trldwt to set
     */
    public void setTrldwt(String trldwt) {
        this.trldwt = trldwt;
    }

    /**
     * @return the trunldwt
     */
    public String getTrunldwt() {
        return trunldwt;
    }

    /**
     * @param trunldwt the trunldwt to set
     */
    public void setTrunldwt(String trunldwt) {
        this.trunldwt = trunldwt;
    }

    /**
     * @return the hyptFromdt
     */
    public String getHyptFromdt() {
        return hyptFromdt;
    }

    /**
     * @param hyptFromdt the hyptFromdt to set
     */
    public void setHyptFromdt(String hyptFromdt) {
        this.hyptFromdt = hyptFromdt;
    }

    /**
     * @return the trfdesc
     */
    public String getTrfdesc() {
        return trfdesc;
    }

    /**
     * @param trfdesc the trfdesc to set
     */
    public void setTrfdesc(String trfdesc) {
        this.trfdesc = trfdesc;
    }

    /**
     * @return the trrdesc
     */
    public String getTrrdesc() {
        return trrdesc;
    }

    /**
     * @param trrdesc the trrdesc to set
     */
    public void setTrrdesc(String trrdesc) {
        this.trrdesc = trrdesc;
    }

    /**
     * @return the trodesc
     */
    public String getTrodesc() {
        return trodesc;
    }

    /**
     * @param trodesc the trodesc to set
     */
    public void setTrodesc(String trodesc) {
        this.trodesc = trodesc;
    }

    /**
     * @return the trtdesc
     */
    public String getTrtdesc() {
        return trtdesc;
    }

    /**
     * @param trtdesc the trtdesc to set
     */
    public void setTrtdesc(String trtdesc) {
        this.trtdesc = trtdesc;
    }

    /**
     * @return the trfweight
     */
    public String getTrfweight() {
        return trfweight;
    }

    /**
     * @param trfweight the trfweight to set
     */
    public void setTrfweight(String trfweight) {
        this.trfweight = trfweight;
    }

    /**
     * @return the trrweight
     */
    public String getTrrweight() {
        return trrweight;
    }

    /**
     * @param trrweight the trrweight to set
     */
    public void setTrrweight(String trrweight) {
        this.trrweight = trrweight;
    }

    /**
     * @return the troweight
     */
    public String getTroweight() {
        return troweight;
    }

    /**
     * @param troweight the troweight to set
     */
    public void setTroweight(String troweight) {
        this.troweight = troweight;
    }

    /**
     * @return the trtweight
     */
    public String getTrtweight() {
        return trtweight;
    }

    /**
     * @param trtweight the trtweight to set
     */
    public void setTrtweight(String trtweight) {
        this.trtweight = trtweight;
    }

    /**
     * @return the fncrFullAddr
     */
    public String getFncrFullAddr() {
        return fncrFullAddr;
    }

    /**
     * @param fncrFullAddr the fncrFullAddr to set
     */
    public void setFncrFullAddr(String fncrFullAddr) {
        this.fncrFullAddr = fncrFullAddr;
    }

    /**
     * @return the taxFrom
     */
    public String getTaxFrom() {
        return taxFrom;
    }

    /**
     * @param taxFrom the taxFrom to set
     */
    public void setTaxFrom(String taxFrom) {
        this.taxFrom = taxFrom;
    }

    /**
     * @return the taxUpto
     */
    public String getTaxUpto() {
        return taxUpto;
    }

    /**
     * @param taxUpto the taxUpto to set
     */
    public void setTaxUpto(String taxUpto) {
        this.taxUpto = taxUpto;
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
     * @return the opdate
     */
    public String getOpdate() {
        return opdate;
    }

    /**
     * @param opdate the opdate to set
     */
    public void setOpdate(String opdate) {
        this.opdate = opdate;
    }

    /**
     * @return the hsrpfront
     */
    public String getHsrpfront() {
        return hsrpfront;
    }

    /**
     * @param hsrpfront the hsrpfront to set
     */
    public void setHsrpfront(String hsrpfront) {
        this.hsrpfront = hsrpfront;
    }

    /**
     * @return the hsrpback
     */
    public String getHsrpback() {
        return hsrpback;
    }

    /**
     * @param hsrpback the hsrpback to set
     */
    public void setHsrpback(String hsrpback) {
        this.hsrpback = hsrpback;
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
     * @return the printedon
     */
    public String getPrintedon() {
        return printedon;
    }

    /**
     * @param printedon the printedon to set
     */
    public void setPrintedon(String printedon) {
        this.printedon = printedon;
    }

    /**
     * @return the istransport
     */
    public boolean isIstransport() {
        return istransport;
    }

    /**
     * @param istransport the istransport to set
     */
    public void setIstransport(boolean istransport) {
        this.istransport = istransport;
    }

    /**
     * @return the ownerSrno
     */
    public int getOwnerSrno() {
        return ownerSrno;
    }

    /**
     * @param ownerSrno the ownerSrno to set
     */
    public void setOwnerSrno(int ownerSrno) {
        this.ownerSrno = ownerSrno;
    }

    /**
     * @return the srno
     */
    public int getSrno() {
        return srno;
    }

    /**
     * @param srno the srno to set
     */
    public void setSrno(int srno) {
        this.srno = srno;
    }

    /**
     * @return the istransportWithSemTrailler
     */
    public boolean isIstransportWithSemTrailler() {
        return istransportWithSemTrailler;
    }

    /**
     * @param istransportWithSemTrailler the istransportWithSemTrailler to set
     */
    public void setIstransportWithSemTrailler(boolean istransportWithSemTrailler) {
        this.istransportWithSemTrailler = istransportWithSemTrailler;
    }

    /**
     * @return the isfncr
     */
    public boolean isIsfncr() {
        return isfncr;
    }

    /**
     * @param isfncr the isfncr to set
     */
    public void setIsfncr(boolean isfncr) {
        this.isfncr = isfncr;
    }

    /**
     * @return the dealercd
     */
    public String getDealercd() {
        return dealercd;
    }

    /**
     * @param dealercd the dealercd to set
     */
    public void setDealercd(String dealercd) {
        this.dealercd = dealercd;
    }

    /**
     * @return the printedSign
     */
    public String getPrintedSign() {
        return printedSign;
    }

    /**
     * @param printedSign the printedSign to set
     */
    public void setPrintedSign(String printedSign) {
        this.printedSign = printedSign;
    }

    /**
     * @return the purposeDesc
     */
    public String getPurposeDesc() {
        return purposeDesc;
    }

    /**
     * @param purposeDesc the purposeDesc to set
     */
    public void setPurposeDesc(String purposeDesc) {
        this.purposeDesc = purposeDesc;
    }

    /**
     * @return the ownership
     */
    public String getOwnership() {
        return ownership;
    }

    /**
     * @param ownership the ownership to set
     */
    public void setOwnership(String ownership) {
        this.ownership = ownership;
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
     * @return the transferDate
     */
    public String getTransferDate() {
        return transferDate;
    }

    /**
     * @param transferDate the transferDate to set
     */
    public void setTransferDate(String transferDate) {
        this.transferDate = transferDate;
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
     * @return the otherCriteria
     */
    public String getOtherCriteria() {
        return otherCriteria;
    }

    /**
     * @param otherCriteria the otherCriteria to set
     */
    public void setOtherCriteria(String otherCriteria) {
        this.otherCriteria = otherCriteria;
    }

    /**
     * @return the serviceType
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * @param serviceType the serviceType to set
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * @return the isServiceType
     */
    public boolean isIsServiceType() {
        return isServiceType;
    }

    /**
     * @param isServiceType the isServiceType to set
     */
    public void setIsServiceType(boolean isServiceType) {
        this.isServiceType = isServiceType;
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
     * @return the rcCount
     */
    public int getRcCount() {
        return rcCount;
    }

    /**
     * @param rcCount the rcCount to set
     */
    public void setRcCount(int rcCount) {
        this.rcCount = rcCount;
    }

    /**
     * @return the pageBreakforHR
     */
    public boolean isPageBreakforHR() {
        return pageBreakforHR;
    }

    /**
     * @param pageBreakforHR the pageBreakforHR to set
     */
    public void setPageBreakforHR(boolean pageBreakforHR) {
        this.pageBreakforHR = pageBreakforHR;
    }

    /**
     * @return the pageBreakExceptHR
     */
    public boolean isPageBreakExceptHR() {
        return pageBreakExceptHR;
    }

    /**
     * @param pageBreakExceptHR the pageBreakExceptHR to set
     */
    public void setPageBreakExceptHR(boolean pageBreakExceptHR) {
        this.pageBreakExceptHR = pageBreakExceptHR;
    }

    /**
     * @return the smartcardFrontPanel
     */
    public String getSmartcardFrontPanel() {
        return smartcardFrontPanel;
    }

    /**
     * @param smartcardFrontPanel the smartcardFrontPanel to set
     */
    public void setSmartcardFrontPanel(String smartcardFrontPanel) {
        this.smartcardFrontPanel = smartcardFrontPanel;
    }

    /**
     * @return the smartcardBackPanel
     */
    public String getSmartcardBackPanel() {
        return smartcardBackPanel;
    }

    /**
     * @param smartcardBackPanel the smartcardBackPanel to set
     */
    public void setSmartcardBackPanel(String smartcardBackPanel) {
        this.smartcardBackPanel = smartcardBackPanel;
    }

    /**
     * @return the ishypothecated
     */
    public boolean isIshypothecated() {
        return ishypothecated;
    }

    /**
     * @param ishypothecated the ishypothecated to set
     */
    public void setIshypothecated(boolean ishypothecated) {
        this.ishypothecated = ishypothecated;
    }

    /**
     * @return the fncrname
     */
    public String getFncrname() {
        return fncrname;
    }

    /**
     * @param fncrname the fncrname to set
     */
    public void setFncrname(String fncrname) {
        this.fncrname = fncrname;
    }

    /**
     * @return the nontransport
     */
    public boolean isNontransport() {
        return nontransport;
    }

    /**
     * @param nontransport the nontransport to set
     */
    public void setNontransport(boolean nontransport) {
        this.nontransport = nontransport;
    }

    /**
     * @return the iscng
     */
    public boolean isIscng() {
        return iscng;
    }

    /**
     * @param iscng the iscng to set
     */
    public void setIscng(boolean iscng) {
        this.iscng = iscng;
    }

    /**
     * @return the kitCylno
     */
    public String getKitCylno() {
        return kitCylno;
    }

    /**
     * @param kitCylno the kitCylno to set
     */
    public void setKitCylno(String kitCylno) {
        this.kitCylno = kitCylno;
    }

    /**
     * @return the stateCD
     */
    public String getStateCD() {
        return stateCD;
    }

    /**
     * @param stateCD the stateCD to set
     */
    public void setStateCD(String stateCD) {
        this.stateCD = stateCD;
    }

    public String getLinkVehicleno() {
        return linkVehicleno;
    }

    public void setLinkVehicleno(String linkVehicleno) {
        this.linkVehicleno = linkVehicleno;
    }

    /**
     * @return the permitCatg
     */
    public String getPermitCatg() {
        return permitCatg;
    }

    /**
     * @param permitCatg the permitCatg to set
     */
    public void setPermitCatg(String permitCatg) {
        this.permitCatg = permitCatg;
    }

    /**
     * @return the transactionDate
     */
    public String getTransactionDate() {
        return transactionDate;
    }

    /**
     * @param transactionDate the transactionDate to set
     */
    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * @return the showTransactionDate
     */
    public boolean isShowTransactionDate() {
        return showTransactionDate;
    }

    /**
     * @param showTransactionDate the showTransactionDate to set
     */
    public void setShowTransactionDate(boolean showTransactionDate) {
        this.showTransactionDate = showTransactionDate;
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
     * @return the topMarginOnPlasticRC
     */
    public String getTopMarginOnPlasticRC() {
        return topMarginOnPlasticRC;
    }

    /**
     * @param topMarginOnPlasticRC the topMarginOnPlasticRC to set
     */
    public void setTopMarginOnPlasticRC(String topMarginOnPlasticRC) {
        this.topMarginOnPlasticRC = topMarginOnPlasticRC;
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
     * @return the isvch_purchase_as
     */
    public boolean isIsvch_purchase_as() {
        return isvch_purchase_as;
    }

    /**
     * @param isvch_purchase_as the isvch_purchase_as to set
     */
    public void setIsvch_purchase_as(boolean isvch_purchase_as) {
        this.isvch_purchase_as = isvch_purchase_as;
    }

    /**
     * @return the showhideborder
     */
    public String getShowhideborder() {
        return showhideborder;
    }

    /**
     * @param showhideborder the showhideborder to set
     */
    public void setShowhideborder(String showhideborder) {
        this.showhideborder = showhideborder;
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
}
