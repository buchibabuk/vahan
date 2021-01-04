/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author nic
 */
public class OwnerDisclaimerReportDobj implements Serializable {

    private String header;
    private String stateCD;
    private String applNO;
    private String stateName;
    private String offName;
    private String regnNO;
    private String regnDT;
    private String puechaseDT;
    private String isExem;
    private String ownerSrNO;
    private String ownerName;
    private String fname;
    private String currAddress;
    private String perAddress;
    private String ownerCD;
    private String ownerDesc;
    private String isGOV;
    private String regnType;
    private String vchDescr;
    private String chassiNO;
    private String engNO;
    private String maker;
    private String makerName;
    private String modelName;
    private String bodyType;
    private String noOfCyl;
    private String horsepwr;
    private String seatCap;
    private String standCap;
    private String sleepCap;
    private String unLdWt;
    private String ldWt;
    private String gcw;
    private String fuel;
    private String fuelDesc;
    private String color;
    private String manuMnt;
    private String manuYrs;
    private String normsDesc;
    private String wheelBase;
    private String cubCap;
    private String floorArea;
    private String acFitted;
    private String audioFitted;
    private String videoFitted;
    private String vhcPurchaseAs;
    private String vchCatg;
    private String dealerCD;
    private String dealerAddress;
    private String saleAmt;
    private String garageAddress;
    private String length;
    private String width;
    private String height;
    private String regnUpto;
    private String fitUpto;
    private String annulIncome;
    private String impVch;
    private String faxlDesc;
    private String raxlDesc;
    private String oaxlDesc;
    private String taxlDesc;
    private String faxlWeight;
    private String raxlWeight;
    private String oaxlWeight;
    private String taxlWeight;
    private String trChasiNO;
    private String trBodyType;
    private String trLdWt;
    private String trUnldWt;
    private String trFAxlDesc;
    private String trRAxlDesc;
    private String trOAxlDesc;
    private String trTAxlDesc;
    private String trFAxlWeight;
    private String trRAxlWeight;
    private String trOAxlWeight;
    private String trTAxlWeight;
    private String hypthFromDT;
    private String srNO;
    private String fncrAddress;
    private String taxFromDT;
    private String taxAmt;
    private String rcptNO;
    private String taxUpto;
    private String opDT;
    private String insType;
    private String insCompany;
    private String policyNO;
    private String insFrom;
    private String insUpto;
    private String kitManuf;
    private String kitSrNo;
    private String kitType;
    private String fitmentDate;
    private String hydroTestDate;
    private String approvalNo;
    private String approvalDate;
    private String workShop;
    private String cylSrNo;
    private String slNo;
    private String amount;
    private String fine;
    private String head;
    private String totalAmount;
    private String fncrAddressAddition;
    private String fncrAddressTermination;
    private String vehType;
    private String regnNextMonthDate;
    private String regnDate;
    private String opDate;
    private String advanceRegnNo;
    private String toReason;
    private String toWef;
    private String caWef;
    private String retenRegnNo;
    private boolean isAdvanceRegnNoAssign;
    private String surcharge;
    private String interest;
    private String rebate;
    private String validityDate;
    private String pmt_type;
    private boolean ispmt_type;
    private String pmt_catg;
    private boolean ispmt_catg;
    private String panNo;
    private String aadharNo;
    private String passportNo;
    private String voterId;
    private String tax1;
    private String tax2;
    private String image_background;
    private String image_logo;
    private boolean show_image_background;
    private boolean show_image_logo;
    private boolean showRoadSafetySlogan;
    private VmRoadSafetySloganPrintDobj roadSafetySloganDobj = null;
    private boolean renderAttachedTrailerDetails = false;
    private String bsIVRegnReason = null;

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

    /**
     * @return the applNO
     */
    public String getApplNO() {
        return applNO;
    }

    /**
     * @param applNO the applNO to set
     */
    public void setApplNO(String applNO) {
        this.applNO = applNO;
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
     * @return the regnNO
     */
    public String getRegnNO() {
        return regnNO;
    }

    /**
     * @param regnNO the regnNO to set
     */
    public void setRegnNO(String regnNO) {
        this.regnNO = regnNO;
    }

    /**
     * @return the regnDT
     */
    public String getRegnDT() {
        return regnDT;
    }

    /**
     * @param regnDT the regnDT to set
     */
    public void setRegnDT(String regnDT) {
        this.regnDT = regnDT;
    }

    /**
     * @return the puechaseDT
     */
    public String getPuechaseDT() {
        return puechaseDT;
    }

    /**
     * @param puechaseDT the puechaseDT to set
     */
    public void setPuechaseDT(String puechaseDT) {
        this.puechaseDT = puechaseDT;
    }

    /**
     * @return the isExem
     */
    public String getIsExem() {
        return isExem;
    }

    /**
     * @param isExem the isExem to set
     */
    public void setIsExem(String isExem) {
        this.isExem = isExem;
    }

    /**
     * @return the ownerSrNO
     */
    public String getOwnerSrNO() {
        return ownerSrNO;
    }

    /**
     * @param ownerSrNO the ownerSrNO to set
     */
    public void setOwnerSrNO(String ownerSrNO) {
        this.ownerSrNO = ownerSrNO;
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
     * @return the fName
     */
    public String getFname() {
        return fname;
    }

    /**
     * @param fName the fName to set
     */
    public void setFname(String fname) {
        this.fname = fname;
    }

    /**
     * @return the currAddress
     */
    public String getCurrAddress() {
        return currAddress;
    }

    /**
     * @param currAddress the currAddress to set
     */
    public void setCurrAddress(String currAddress) {
        this.currAddress = currAddress;
    }

    /**
     * @return the perAddress
     */
    public String getPerAddress() {
        return perAddress;
    }

    /**
     * @param perAddress the perAddress to set
     */
    public void setPerAddress(String perAddress) {
        this.perAddress = perAddress;
    }

    /**
     * @return the ownerCD
     */
    public String getOwnerCD() {
        return ownerCD;
    }

    /**
     * @param ownerCD the ownerCD to set
     */
    public void setOwnerCD(String ownerCD) {
        this.ownerCD = ownerCD;
    }

    /**
     * @return the ownerDesc
     */
    public String getOwnerDesc() {
        return ownerDesc;
    }

    /**
     * @param ownerDesc the ownerDesc to set
     */
    public void setOwnerDesc(String ownerDesc) {
        this.ownerDesc = ownerDesc;
    }

    /**
     * @return the isGOV
     */
    public String getIsGOV() {
        return isGOV;
    }

    /**
     * @param isGOV the isGOV to set
     */
    public void setIsGOV(String isGOV) {
        this.isGOV = isGOV;
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
     * @return the vchDescr
     */
    public String getVchDescr() {
        return vchDescr;
    }

    /**
     * @param vchDescr the vchDescr to set
     */
    public void setVchDescr(String vchDescr) {
        this.vchDescr = vchDescr;
    }

    /**
     * @return the chassiNO
     */
    public String getChassiNO() {
        return chassiNO;
    }

    /**
     * @param chassiNO the chassiNO to set
     */
    public void setChassiNO(String chassiNO) {
        this.chassiNO = chassiNO;
    }

    /**
     * @return the engNO
     */
    public String getEngNO() {
        return engNO;
    }

    /**
     * @param engNO the engNO to set
     */
    public void setEngNO(String engNO) {
        this.engNO = engNO;
    }

    /**
     * @return the maker
     */
    public String getMaker() {
        return maker;
    }

    /**
     * @param maker the maker to set
     */
    public void setMaker(String maker) {
        this.maker = maker;
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
     * @return the noOfCyl
     */
    public String getNoOfCyl() {
        return noOfCyl;
    }

    /**
     * @param noOfCyl the noOfCyl to set
     */
    public void setNoOfCyl(String noOfCyl) {
        this.noOfCyl = noOfCyl;
    }

    /**
     * @return the horsepwr
     */
    public String getHorsepwr() {
        return horsepwr;
    }

    /**
     * @param horsepwr the horsepwr to set
     */
    public void setHorsepwr(String horsepwr) {
        this.horsepwr = horsepwr;
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
     * @return the unLdWt
     */
    public String getUnLdWt() {
        return unLdWt;
    }

    /**
     * @param unLdWt the unLdWt to set
     */
    public void setUnLdWt(String unLdWt) {
        this.unLdWt = unLdWt;
    }

    /**
     * @return the ldWt
     */
    public String getLdWt() {
        return ldWt;
    }

    /**
     * @param ldWt the ldWt to set
     */
    public void setLdWt(String ldWt) {
        this.ldWt = ldWt;
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
     * @return the fuel
     */
    public String getFuel() {
        return fuel;
    }

    /**
     * @param fuel the fuel to set
     */
    public void setFuel(String fuel) {
        this.fuel = fuel;
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
     * @return the manuYrs
     */
    public String getManuYrs() {
        return manuYrs;
    }

    /**
     * @param manuYrs the manuYrs to set
     */
    public void setManuYrs(String manuYrs) {
        this.manuYrs = manuYrs;
    }

    /**
     * @return the normsDesc
     */
    public String getNormsDesc() {
        return normsDesc;
    }

    /**
     * @param normsDesc the normsDesc to set
     */
    public void setNormsDesc(String normsDesc) {
        this.normsDesc = normsDesc;
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
     * @return the cubCap
     */
    public String getCubCap() {
        return cubCap;
    }

    /**
     * @param cubCap the cubCap to set
     */
    public void setCubCap(String cubCap) {
        this.cubCap = cubCap;
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
     * @return the acFitted
     */
    public String getAcFitted() {
        return acFitted;
    }

    /**
     * @param acFitted the acFitted to set
     */
    public void setAcFitted(String acFitted) {
        this.acFitted = acFitted;
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
     * @return the vhcPurchaseAs
     */
    public String getVhcPurchaseAs() {
        return vhcPurchaseAs;
    }

    /**
     * @param vhcPurchaseAs the vhcPurchaseAs to set
     */
    public void setVhcPurchaseAs(String vhcPurchaseAs) {
        this.vhcPurchaseAs = vhcPurchaseAs;
    }

    /**
     * @return the vchCatg
     */
    public String getVchCatg() {
        return vchCatg;
    }

    /**
     * @param vchCatg the vchCatg to set
     */
    public void setVchCatg(String vchCatg) {
        this.vchCatg = vchCatg;
    }

    /**
     * @return the dealerCD
     */
    public String getDealerCD() {
        return dealerCD;
    }

    /**
     * @param dealerCD the dealerCD to set
     */
    public void setDealerCD(String dealerCD) {
        this.dealerCD = dealerCD;
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
     * @return the garageAddress
     */
    public String getGarageAddress() {
        return garageAddress;
    }

    /**
     * @param garageAddress the garageAddress to set
     */
    public void setGarageAddress(String garageAddress) {
        this.garageAddress = garageAddress;
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
     * @return the annulIncome
     */
    public String getAnnulIncome() {
        return annulIncome;
    }

    /**
     * @param annulIncome the annulIncome to set
     */
    public void setAnnulIncome(String annulIncome) {
        this.annulIncome = annulIncome;
    }

    /**
     * @return the impVch
     */
    public String getImpVch() {
        return impVch;
    }

    /**
     * @param impVch the impVch to set
     */
    public void setImpVch(String impVch) {
        this.impVch = impVch;
    }

    /**
     * @return the fAxlDesc
     */
    public String getFaxlDesc() {
        return faxlDesc;
    }

    /**
     * @param fAxlDesc the fAxlDesc to set
     */
    public void setFaxlDesc(String faxlDesc) {
        this.faxlDesc = faxlDesc;
    }

    /**
     * @return the rAxlDesc
     */
    public String getRaxlDesc() {
        return raxlDesc;
    }

    /**
     * @param rAxlDesc the rAxlDesc to set
     */
    public void setRaxlDesc(String raxlDesc) {
        this.raxlDesc = raxlDesc;
    }

    /**
     * @return the oAxlDesc
     */
    public String getOaxlDesc() {
        return oaxlDesc;
    }

    /**
     * @param oAxlDesc the oAxlDesc to set
     */
    public void setOaxlDesc(String oaxlDesc) {
        this.oaxlDesc = oaxlDesc;
    }

    /**
     * @return the tAxlDesc
     */
    public String getTaxlDesc() {
        return taxlDesc;
    }

    /**
     * @param tAxlDesc the tAxlDesc to set
     */
    public void setTaxlDesc(String taxlDesc) {
        this.taxlDesc = taxlDesc;
    }

    /**
     * @return the fAxlWeight
     */
    public String getFaxlWeight() {
        return faxlWeight;
    }

    /**
     * @param fAxlWeight the fAxlWeight to set
     */
    public void setFaxlWeight(String faxlWeight) {
        this.faxlWeight = faxlWeight;
    }

    /**
     * @return the rAxlWeight
     */
    public String getRaxlWeight() {
        return raxlWeight;
    }

    /**
     * @param rAxlWeight the rAxlWeight to set
     */
    public void setRaxlWeight(String raxlWeight) {
        this.raxlWeight = raxlWeight;
    }

    /**
     * @return the oAxlWeight
     */
    public String getOaxlWeight() {
        return oaxlWeight;
    }

    /**
     * @param oAxlWeight the oAxlWeight to set
     */
    public void setOaxlWeight(String oaxlWeight) {
        this.oaxlWeight = oaxlWeight;
    }

    /**
     * @return the tAxlWeight
     */
    public String getTaxlWeight() {
        return taxlWeight;
    }

    /**
     * @param tAxlWeight the tAxlWeight to set
     */
    public void setTaxlWeight(String taxlWeight) {
        this.taxlWeight = taxlWeight;
    }

    /**
     * @return the trChasiNO
     */
    public String getTrChasiNO() {
        return trChasiNO;
    }

    /**
     * @param trChasiNO the trChasiNO to set
     */
    public void setTrChasiNO(String trChasiNO) {
        this.trChasiNO = trChasiNO;
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
     * @return the trLdWt
     */
    public String getTrLdWt() {
        return trLdWt;
    }

    /**
     * @param trLdWt the trLdWt to set
     */
    public void setTrLdWt(String trLdWt) {
        this.trLdWt = trLdWt;
    }

    /**
     * @return the trUnldWt
     */
    public String getTrUnldWt() {
        return trUnldWt;
    }

    /**
     * @param trUnldWt the trUnldWt to set
     */
    public void setTrUnldWt(String trUnldWt) {
        this.trUnldWt = trUnldWt;
    }

    /**
     * @return the trFAxlDesc
     */
    public String getTrFAxlDesc() {
        return trFAxlDesc;
    }

    /**
     * @param trFAxlDesc the trFAxlDesc to set
     */
    public void setTrFAxlDesc(String trFAxlDesc) {
        this.trFAxlDesc = trFAxlDesc;
    }

    /**
     * @return the trRAxlDesc
     */
    public String getTrRAxlDesc() {
        return trRAxlDesc;
    }

    /**
     * @param trRAxlDesc the trRAxlDesc to set
     */
    public void setTrRAxlDesc(String trRAxlDesc) {
        this.trRAxlDesc = trRAxlDesc;
    }

    /**
     * @return the trOAxlDesc
     */
    public String getTrOAxlDesc() {
        return trOAxlDesc;
    }

    /**
     * @param trOAxlDesc the trOAxlDesc to set
     */
    public void setTrOAxlDesc(String trOAxlDesc) {
        this.trOAxlDesc = trOAxlDesc;
    }

    /**
     * @return the trTAxlDesc
     */
    public String getTrTAxlDesc() {
        return trTAxlDesc;
    }

    /**
     * @param trTAxlDesc the trTAxlDesc to set
     */
    public void setTrTAxlDesc(String trTAxlDesc) {
        this.trTAxlDesc = trTAxlDesc;
    }

    /**
     * @return the trFAxlWeight
     */
    public String getTrFAxlWeight() {
        return trFAxlWeight;
    }

    /**
     * @param trFAxlWeight the trFAxlWeight to set
     */
    public void setTrFAxlWeight(String trFAxlWeight) {
        this.trFAxlWeight = trFAxlWeight;
    }

    /**
     * @return the trRAxlWeight
     */
    public String getTrRAxlWeight() {
        return trRAxlWeight;
    }

    /**
     * @param trRAxlWeight the trRAxlWeight to set
     */
    public void setTrRAxlWeight(String trRAxlWeight) {
        this.trRAxlWeight = trRAxlWeight;
    }

    /**
     * @return the trOAxlWeight
     */
    public String getTrOAxlWeight() {
        return trOAxlWeight;
    }

    /**
     * @param trOAxlWeight the trOAxlWeight to set
     */
    public void setTrOAxlWeight(String trOAxlWeight) {
        this.trOAxlWeight = trOAxlWeight;
    }

    /**
     * @return the trTAxlWeight
     */
    public String getTrTAxlWeight() {
        return trTAxlWeight;
    }

    /**
     * @param trTAxlWeight the trTAxlWeight to set
     */
    public void setTrTAxlWeight(String trTAxlWeight) {
        this.trTAxlWeight = trTAxlWeight;
    }

    /**
     * @return the hypthFromDT
     */
    public String getHypthFromDT() {
        return hypthFromDT;
    }

    /**
     * @param hypthFromDT the hypthFromDT to set
     */
    public void setHypthFromDT(String hypthFromDT) {
        this.hypthFromDT = hypthFromDT;
    }

    /**
     * @return the srNO
     */
    public String getSrNO() {
        return srNO;
    }

    /**
     * @param srNO the srNO to set
     */
    public void setSrNO(String srNO) {
        this.srNO = srNO;
    }

    /**
     * @return the fncrAddress
     */
    public String getFncrAddress() {
        return fncrAddress;
    }

    /**
     * @param fncrAddress the fncrAddress to set
     */
    public void setFncrAddress(String fncrAddress) {
        this.fncrAddress = fncrAddress;
    }

    /**
     * @return the taxFromDT
     */
    public String getTaxFromDT() {
        return taxFromDT;
    }

    /**
     * @param taxFromDT the taxFromDT to set
     */
    public void setTaxFromDT(String taxFromDT) {
        this.taxFromDT = taxFromDT;
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
     * @return the rcptNO
     */
    public String getRcptNO() {
        return rcptNO;
    }

    /**
     * @param rcptNO the rcptNO to set
     */
    public void setRcptNO(String rcptNO) {
        this.rcptNO = rcptNO;
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
     * @return the opDT
     */
    public String getOpDT() {
        return opDT;
    }

    /**
     * @param opDT the opDT to set
     */
    public void setOpDT(String opDT) {
        this.opDT = opDT;
    }

    /**
     * @return the insType
     */
    public String getInsType() {
        return insType;
    }

    /**
     * @param insType the insType to set
     */
    public void setInsType(String insType) {
        this.insType = insType;
    }

    /**
     * @return the insCompany
     */
    public String getInsCompany() {
        return insCompany;
    }

    /**
     * @param insCompany the insCompany to set
     */
    public void setInsCompany(String insCompany) {
        this.insCompany = insCompany;
    }

    /**
     * @return the policyNO
     */
    public String getPolicyNO() {
        return policyNO;
    }

    /**
     * @param policyNO the policyNO to set
     */
    public void setPolicyNO(String policyNO) {
        this.policyNO = policyNO;
    }

    /**
     * @return the insFrom
     */
    public String getInsFrom() {
        return insFrom;
    }

    /**
     * @param insFrom the insFrom to set
     */
    public void setInsFrom(String insFrom) {
        this.insFrom = insFrom;
    }

    /**
     * @return the insUpto
     */
    public String getInsUpto() {
        return insUpto;
    }

    /**
     * @param insUpto the insUpto to set
     */
    public void setInsUpto(String insUpto) {
        this.insUpto = insUpto;
    }

    /**
     * @return the kitManuf
     */
    public String getKitManuf() {
        return kitManuf;
    }

    /**
     * @param kitManuf the kitManuf to set
     */
    public void setKitManuf(String kitManuf) {
        this.kitManuf = kitManuf;
    }

    /**
     * @return the kitSrNo
     */
    public String getKitSrNo() {
        return kitSrNo;
    }

    /**
     * @param kitSrNo the kitSrNo to set
     */
    public void setKitSrNo(String kitSrNo) {
        this.kitSrNo = kitSrNo;
    }

    /**
     * @return the kitType
     */
    public String getKitType() {
        return kitType;
    }

    /**
     * @param kitType the kitType to set
     */
    public void setKitType(String kitType) {
        this.kitType = kitType;
    }

    /**
     * @return the fitmentDate
     */
    public String getFitmentDate() {
        return fitmentDate;
    }

    /**
     * @param fitmentDate the fitmentDate to set
     */
    public void setFitmentDate(String fitmentDate) {
        this.fitmentDate = fitmentDate;
    }

    /**
     * @return the hydroTestDate
     */
    public String getHydroTestDate() {
        return hydroTestDate;
    }

    /**
     * @param hydroTestDate the hydroTestDate to set
     */
    public void setHydroTestDate(String hydroTestDate) {
        this.hydroTestDate = hydroTestDate;
    }

    /**
     * @return the approvalNo
     */
    public String getApprovalNo() {
        return approvalNo;
    }

    /**
     * @param approvalNo the approvalNo to set
     */
    public void setApprovalNo(String approvalNo) {
        this.approvalNo = approvalNo;
    }

    /**
     * @return the approvalDate
     */
    public String getApprovalDate() {
        return approvalDate;
    }

    /**
     * @param approvalDate the approvalDate to set
     */
    public void setApprovalDate(String approvalDate) {
        this.approvalDate = approvalDate;
    }

    /**
     * @return the workShop
     */
    public String getWorkShop() {
        return workShop;
    }

    /**
     * @param workShop the workShop to set
     */
    public void setWorkShop(String workShop) {
        this.workShop = workShop;
    }

    /**
     * @return the cylSrNo
     */
    public String getCylSrNo() {
        return cylSrNo;
    }

    /**
     * @param cylSrNo the cylSrNo to set
     */
    public void setCylSrNo(String cylSrNo) {
        this.cylSrNo = cylSrNo;
    }

    /**
     * @return the slNo
     */
    public String getSlNo() {
        return slNo;
    }

    /**
     * @param slNo the slNo to set
     */
    public void setSlNo(String slNo) {
        this.slNo = slNo;
    }

    /**
     * @return the amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * @return the fine
     */
    public String getFine() {
        return fine;
    }

    /**
     * @param fine the fine to set
     */
    public void setFine(String fine) {
        this.fine = fine;
    }

    /**
     * @return the head
     */
    public String getHead() {
        return head;
    }

    /**
     * @param head the head to set
     */
    public void setHead(String head) {
        this.head = head;
    }

    /**
     * @return the totalAmount
     */
    public String getTotalAmount() {
        return totalAmount;
    }

    /**
     * @param totalAmount the totalAmount to set
     */
    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * @return the fncrAddressAddition
     */
    public String getFncrAddressAddition() {
        return fncrAddressAddition;
    }

    /**
     * @param fncrAddressAddition the fncrAddressAddition to set
     */
    public void setFncrAddressAddition(String fncrAddressAddition) {
        this.fncrAddressAddition = fncrAddressAddition;
    }

    /**
     * @return the fncrAddressTermination
     */
    public String getFncrAddressTermination() {
        return fncrAddressTermination;
    }

    /**
     * @param fncrAddressTermination the fncrAddressTermination to set
     */
    public void setFncrAddressTermination(String fncrAddressTermination) {
        this.fncrAddressTermination = fncrAddressTermination;
    }

    /**
     * @return the vehType
     */
    public String getVehType() {
        return vehType;
    }

    /**
     * @param vehType the vehType to set
     */
    public void setVehType(String vehType) {
        this.vehType = vehType;
    }

    /**
     * @return the regnNextMonthDate
     */
    public String getRegnNextMonthDate() {
        return regnNextMonthDate;
    }

    /**
     * @param regnNextMonthDate the regnNextMonthDate to set
     */
    public void setRegnNextMonthDate(String regnNextMonthDate) {
        this.regnNextMonthDate = regnNextMonthDate;
    }

    /**
     * @return the regnDate
     */
    public String getRegnDate() {
        return regnDate;
    }

    /**
     * @param regnDate the regnDate to set
     */
    public void setRegnDate(String regnDate) {
        this.regnDate = regnDate;
    }

    /**
     * @return the advanceRegnNo
     */
    public String getAdvanceRegnNo() {
        return advanceRegnNo;
    }

    /**
     * @param advanceRegnNo the advanceRegnNo to set
     */
    public void setAdvanceRegnNo(String advanceRegnNo) {
        this.advanceRegnNo = advanceRegnNo;
    }

    /**
     * @return the toReason
     */
    public String getToReason() {
        return toReason;
    }

    /**
     * @param toReason the toReason to set
     */
    public void setToReason(String toReason) {
        this.toReason = toReason;
    }

    /**
     * @return the toWef
     */
    public String getToWef() {
        return toWef;
    }

    /**
     * @param toWef the toWef to set
     */
    public void setToWef(String toWef) {
        this.toWef = toWef;
    }

    /**
     * @return the caWef
     */
    public String getCaWef() {
        return caWef;
    }

    /**
     * @param caWef the caWef to set
     */
    public void setCaWef(String caWef) {
        this.caWef = caWef;
    }

    /**
     * @return the retenRegnNo
     */
    public String getRetenRegnNo() {
        return retenRegnNo;
    }

    /**
     * @param retenRegnNo the retenRegnNo to set
     */
    public void setRetenRegnNo(String retenRegnNo) {
        this.retenRegnNo = retenRegnNo;
    }

    /**
     * @return the isAdvanceRegnNoAssign
     */
    public boolean isIsAdvanceRegnNoAssign() {
        return isAdvanceRegnNoAssign;
    }

    /**
     * @param isAdvanceRegnNoAssign the isAdvanceRegnNoAssign to set
     */
    public void setIsAdvanceRegnNoAssign(boolean isAdvanceRegnNoAssign) {
        this.isAdvanceRegnNoAssign = isAdvanceRegnNoAssign;
    }

    /**
     * @return the surcharge
     */
    public String getSurcharge() {
        return surcharge;
    }

    /**
     * @param surcharge the surcharge to set
     */
    public void setSurcharge(String surcharge) {
        this.surcharge = surcharge;
    }

    /**
     * @return the interest
     */
    public String getInterest() {
        return interest;
    }

    /**
     * @param interest the interest to set
     */
    public void setInterest(String interest) {
        this.interest = interest;
    }

    /**
     * @return the rebate
     */
    public String getRebate() {
        return rebate;
    }

    /**
     * @param rebate the rebate to set
     */
    public void setRebate(String rebate) {
        this.rebate = rebate;
    }

    /**
     * @return the opDate
     */
    public String getOpDate() {
        return opDate;
    }

    /**
     * @param opDate the opDate to set
     */
    public void setOpDate(String opDate) {
        this.opDate = opDate;
    }

    /**
     * @return the validityDate
     */
    public String getValidityDate() {
        return validityDate;
    }

    /**
     * @param validityDate the validityDate to set
     */
    public void setValidityDate(String validityDate) {
        this.validityDate = validityDate;
    }

    /**
     * @return the pmt_type
     */
    public String getPmt_type() {
        return pmt_type;
    }

    /**
     * @param pmt_type the pmt_type to set
     */
    public void setPmt_type(String pmt_type) {
        this.pmt_type = pmt_type;
    }

    /**
     * @return the ispmt_type
     */
    public boolean isIspmt_type() {
        return ispmt_type;
    }

    /**
     * @param ispmt_type the ispmt_type to set
     */
    public void setIspmt_type(boolean ispmt_type) {
        this.ispmt_type = ispmt_type;
    }

    /**
     * @return the pmt_catg
     */
    public String getPmt_catg() {
        return pmt_catg;
    }

    /**
     * @param pmt_catg the pmt_catg to set
     */
    public void setPmt_catg(String pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    /**
     * @return the ispmt_catg
     */
    public boolean isIspmt_catg() {
        return ispmt_catg;
    }

    /**
     * @param ispmt_catg the ispmt_catg to set
     */
    public void setIspmt_catg(boolean ispmt_catg) {
        this.ispmt_catg = ispmt_catg;
    }

    /**
     * @return the panNo
     */
    public String getPanNo() {
        return panNo;
    }

    /**
     * @param panNo the panNo to set
     */
    public void setPanNo(String panNo) {
        this.panNo = panNo;
    }

    /**
     * @return the aadharNo
     */
    public String getAadharNo() {
        return aadharNo;
    }

    /**
     * @param aadharNo the aadharNo to set
     */
    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    /**
     * @return the passportNo
     */
    public String getPassportNo() {
        return passportNo;
    }

    /**
     * @param passportNo the passportNo to set
     */
    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }

    /**
     * @return the voterId
     */
    public String getVoterId() {
        return voterId;
    }

    /**
     * @param voterId the voterId to set
     */
    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    /**
     * @return the tax1
     */
    public String getTax1() {
        return tax1;
    }

    /**
     * @param tax1 the tax1 to set
     */
    public void setTax1(String tax1) {
        this.tax1 = tax1;
    }

    /**
     * @return the tax2
     */
    public String getTax2() {
        return tax2;
    }

    /**
     * @param tax2 the tax2 to set
     */
    public void setTax2(String tax2) {
        this.tax2 = tax2;
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

    public boolean isRenderAttachedTrailerDetails() {
        return renderAttachedTrailerDetails;
    }

    public void setRenderAttachedTrailerDetails(boolean renderAttachedTrailerDetails) {
        this.renderAttachedTrailerDetails = renderAttachedTrailerDetails;
    }

    public String getBsIVRegnReason() {
        return bsIVRegnReason;
    }

    public void setBsIVRegnReason(String bsIVRegnReason) {
        this.bsIVRegnReason = bsIVRegnReason;
    }
}
