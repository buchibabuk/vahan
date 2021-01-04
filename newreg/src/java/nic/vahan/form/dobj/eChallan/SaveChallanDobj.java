/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author tranC108
 */
public class SaveChallanDobj implements Serializable, Cloneable {

    private String sezNo;
    private String NCCRNo;
    private String bookno;
    private String ownerName;
    private String color;
    private String sleeperCap;
    private String challanNo;
    private String officerNo;
    private String district;
    private String fuel;
    private String vehicleNo;
    private String state;
    private String rto;
    private String chasiNo;
    private String vhClass;
    private String vehicleType;
    private String vhClassCode;
    private String vehno1;
    private String vehno2;
    private String accusCatg;
    private String accuName;
    private Date challanDt;
    private boolean alreadySettled;
    private boolean challanRefToCrt;
    private String vehImpnd;
    private String docImpnd;
    private String time;
    private String challanPlace;
    private String courtName;
    private Date heaaringDt;
    private String compFee;
    private int adCompFee;
    private int totalFee;
    private String challanTime;
    private String seatCap;
    private String ladenWt;
    private String unldnWt;
    private String pmtVal;
    private String fitVal;
    private String taxVal;
    private String insVal;
    private String add1;
    private String add2;
    private String AccuseAddress;
    private String vehImpndPlace;
    private String contactOfficer;
    private Date vehImpdDate;
    private String dlNo;
    private String witnessName;
    private String witnessAdd;
    private String accusedRemarks;
    private String impoundRemarks;
    private String mobileNo;
    private int settledFee;
    private String standCap;
    private String CMPCASE;
    private String date;
    private String placeCh;
    private String dateHear;
    private String chalOff;
    private String officerName;
    private String impndPlace;
    private String impndDate;
    private String chalOffCode;
    private String rtoCode;
    private String stateCode;
    private String hiddenaddressField;
    private String applicationNO;
    private Date onRoadPayRcieptDate;
    private String onRoadPayRcieptNo;
    private String courtCd;
    private String officeCode;
    private String officeStateCode;
    private String disposalChallanRemarks;
    private String disposalChallanDate;
    private String rcieptDateInCompFee;
    private String rcieptNoInCompFee = "";
    private String sezPoliceStation;
    private String sezDistrict;
    private String accuesdPoliceSation;
    private String accuesdCity;
    private int accuesdPincode;
    private String accuesdFlag;
    private String statusOfChallan;
    private String magistrate;
    private int accessPassangerWeight;
    private int overLoadWeight;
    private int typesOfGooods;
    private int projection;
    private int overHang;
    private int accessAnimal;
    private String animalType;
    private boolean overloadVehicle;
    private boolean overAccessPassanger;
    private String commingFrom;
    private String goingTo;
    private boolean checkBoxsettledAtSpot;

    @Override
    public String toString() {
        return "SaveChallanDobj{" + "sezNo=" + sezNo + ", NCCRNo=" + NCCRNo + ", bookno=" + bookno + ", ownerName=" + ownerName + ", color=" + color + ", sleeperCap=" + sleeperCap + ", challanNo=" + challanNo + ", officerNo=" + officerNo + ", district=" + district + ", fuel=" + fuel + ", vehicleNo=" + vehicleNo + ", state=" + state + ", rto=" + rto + ", chasiNo=" + chasiNo + ", vhClass=" + vhClass + ", vehicleType=" + vehicleType + ", vhClassCode=" + vhClassCode + ", vehno1=" + vehno1 + ", vehno2=" + vehno2 + ", accusCatg=" + accusCatg + ", accuName=" + accuName + ", challanDt=" + challanDt + ", alreadySettled=" + alreadySettled + ", challanRefToCrt=" + challanRefToCrt + ", vehImpnd=" + vehImpnd + ", docImpnd=" + docImpnd + ", time=" + time + ", challanPlace=" + challanPlace + ", courtName=" + courtName + ", heaaringDt=" + heaaringDt + ", compFee=" + compFee + ", adCompFee=" + adCompFee + ", totalFee=" + totalFee + ", challanTime=" + challanTime + ", seatCap=" + seatCap + ", ladenWt=" + ladenWt + ", unldnWt=" + unldnWt + ", pmtVal=" + pmtVal + ", fitVal=" + fitVal + ", taxVal=" + taxVal + ", insVal=" + insVal + ", add1=" + add1 + ", add2=" + add2 + ", AccuseAddress=" + AccuseAddress + ", vehImpndPlace=" + vehImpndPlace + ", contactOfficer=" + contactOfficer + ", vehImpdDate=" + vehImpdDate + ", dlNo=" + dlNo + ", witnessName=" + witnessName + ", witnessAdd=" + witnessAdd + ", accusedRemarks=" + accusedRemarks + ", impoundRemarks=" + impoundRemarks + ", mobileNo=" + mobileNo + ", settledFee=" + settledFee + ", standCap=" + standCap + ", CMPCASE=" + CMPCASE + ", date=" + date + ", placeCh=" + placeCh + ", dateHear=" + dateHear + ", chalOff=" + chalOff + ", officerName=" + officerName + ", impndPlace=" + impndPlace + ", impndDate=" + impndDate + ", chalOffCode=" + chalOffCode + ", rtoCode=" + rtoCode + ", stateCode=" + stateCode + ", hiddenaddressField=" + hiddenaddressField + ", applicationNO=" + applicationNO + ", onRoadPayRcieptDate=" + onRoadPayRcieptDate + ", onRoadPayRcieptNo=" + onRoadPayRcieptNo + ", courtCd=" + courtCd + ", officeCode=" + officeCode + ", officeStateCode=" + officeStateCode + ", disposalChallanRemarks=" + disposalChallanRemarks + ", disposalChallanDate=" + disposalChallanDate + ", rcieptDateInCompFee=" + rcieptDateInCompFee + ", rcieptNoInCompFee=" + rcieptNoInCompFee + ", sezPoliceStation=" + sezPoliceStation + ", sezDistrict=" + sezDistrict + ", accuesdPoliceSation=" + accuesdPoliceSation + ", accuesdCity=" + accuesdCity + ", accuesdPincode=" + accuesdPincode + ", accuesdFlag=" + accuesdFlag + ", statusOfChallan=" + statusOfChallan + ", magistrate=" + magistrate + ", checkBoxsettledAtSpot=" + checkBoxsettledAtSpot + '}';
    }

    /**
     * @return the accuName
     */
    public String getAccuName() {
        return accuName;
    }

    /**
     * @return the accusCatg
     */
    public String getAccusCatg() {
        return accusCatg;
    }

    /**
     * @return the adCompFee
     */
    public int getAdCompFee() {
        return adCompFee;
    }

    /**
     * @return the bookno
     */
    public String getBookno() {
        return bookno;
    }

    /**
     * @return the challanPlace
     */
    public String getChallanPlace() {
        return challanPlace;
    }

    /**
     * @return the chasiNo
     */
    public String getChasiNo() {
        return chasiNo;
    }

    /**
     * @return the compFee
     */
    public String getCompFee() {
        return compFee;
    }

    /**
     * @return the courtName
     */
    public String getCourtName() {
        return courtName;
    }

    /**
     * @return the dlNo
     */
    public String getDlNo() {
        return dlNo;
    }

    /**
     * @return the docImpnd
     */
    public String getDocImpnd() {
        return docImpnd;
    }

    /**
     * @return the fitVal
     */
    public String getFitVal() {
        return fitVal;
    }

    /**
     * @return the heaaringDt
     */
    public Date getHeaaringDt() {
        return heaaringDt;
    }

    /**
     * @return the insVal
     */
    public String getInsVal() {
        return insVal;
    }

    /**
     * @return the ladenWt
     */
    public String getLadenWt() {
        return ladenWt;
    }

    /**
     * @return the mobileNo
     */
    public String getMobileNo() {
        return mobileNo;
    }

    /**
     * @return the officerNo
     */
    public String getOfficerNo() {
        return officerNo;
    }

    /**
     * @return the pmtVal
     */
    public String getPmtVal() {
        return pmtVal;
    }

    /**
     * @return the seatCap
     */
    public String getSeatCap() {
        return seatCap;
    }

    /**
     * @return the settledFee
     */
    public int getSettledFee() {
        return settledFee;
    }

    /**
     * @return the standCap
     */
    public String getStandCap() {
        return standCap;
    }

    /**
     * @return the taxVal
     */
    public String getTaxVal() {
        return taxVal;
    }

    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * @return the totalFee
     */
    public int getTotalFee() {
        return totalFee;
    }

    /**
     * @return the unldnWt
     */
    public String getUnldnWt() {
        return unldnWt;
    }

    /**
     * @return the vehImpnd
     */
    public String getVehImpnd() {
        return vehImpnd;
    }

    /**
     * @return the vehImpndPlace
     */
    public String getVehImpndPlace() {
        return vehImpndPlace;
    }

    /**
     * @return the vehicleNo
     */
    public String getVehicleNo() {
        return vehicleNo;
    }

    /**
     * @return the vehno1
     */
    public String getVehno1() {
        return vehno1;
    }

    /**
     * @return the vehno2
     */
    public String getVehno2() {
        return vehno2;
    }

    /**
     * @return the vhClass
     */
    public String getVhClass() {
        return vhClass;
    }

    /**
     * @return the witnessAdd
     */
    public String getWitnessAdd() {
        return witnessAdd;
    }

    /**
     * @return the witnessName
     */
    public String getWitnessName() {
        return witnessName;
    }

    /**
     * @return the alreadySettled
     */
    public boolean isAlreadySettled() {
        return alreadySettled;
    }

    /**
     * @param accuName the accuName to set
     */
    public void setAccuName(String accuName) {
        this.accuName = accuName;
    }

    /**
     * @param accusCatg the accusCatg to set
     */
    public void setAccusCatg(String accusCatg) {
        this.accusCatg = accusCatg;
    }

    /**
     * @param adCompFee the adCompFee to set
     */
    public void setAdCompFee(int adCompFee) {
        this.adCompFee = adCompFee;
    }

    /**
     * @param alreadySettled the alreadySettled to set
     */
    public void setAlreadySettled(boolean alreadySettled) {
        this.alreadySettled = alreadySettled;
    }

    /**
     * @param bookno the bookno to set
     */
    public void setBookno(String bookno) {
        this.bookno = bookno;
    }

    /**
     * @param challanPlace the challanPlace to set
     */
    public void setChallanPlace(String challanPlace) {
        this.challanPlace = challanPlace;
    }

    /**
     * @param chasiNo the chasiNo to set
     */
    public void setChasiNo(String chasiNo) {
        this.chasiNo = chasiNo;
    }

    /**
     * @param compFee the compFee to set
     */
    public void setCompFee(String compFee) {
        this.compFee = compFee;
    }

    /**
     * @param courtName the courtName to set
     */
    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    /**
     * @param dlNo the dlNo to set
     */
    public void setDlNo(String dlNo) {
        this.dlNo = dlNo;
    }

    /**
     * @param docImpnd the docImpnd to set
     */
    public void setDocImpnd(String docImpnd) {
        this.docImpnd = docImpnd;
    }

    /**
     * @param fitVal the fitVal to set
     */
    public void setFitVal(String fitVal) {
        this.fitVal = fitVal;
    }

    /**
     * @param heaaringDt the heaaringDt to set
     */
    public void setHeaaringDt(Date heaaringDt) {
        this.heaaringDt = heaaringDt;
    }

    /**
     * @param insVal the insVal to set
     */
    public void setInsVal(String insVal) {
        this.insVal = insVal;
    }

    /**
     * @param ladenWt the ladenWt to set
     */
    public void setLadenWt(String ladenWt) {
        this.ladenWt = ladenWt;
    }

    /**
     * @param mobileNo the mobileNo to set
     */
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    /**
     * @param officerNo the officerNo to set
     */
    public void setOfficerNo(String officerNo) {
        this.officerNo = officerNo;
    }

    /**
     * @param pmtVal the pmtVal to set
     */
    public void setPmtVal(String pmtVal) {
        this.pmtVal = pmtVal;
    }

    /**
     * @param seatCap the seatCap to set
     */
    public void setSeatCap(String seatCap) {
        this.seatCap = seatCap;
    }

    /**
     * @param settledFee the settledFee to set
     */
    public void setSettledFee(int settledFee) {
        this.settledFee = settledFee;
    }

    /**
     * @param standCap the standCap to set
     */
    public void setStandCap(String standCap) {
        this.standCap = standCap;
    }

    /**
     * @param taxVal the taxVal to set
     */
    public void setTaxVal(String taxVal) {
        this.taxVal = taxVal;
    }

    /**
     * @param time the time to set
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @param totalFee the totalFee to set
     */
    public void setTotalFee(int totalFee) {
        this.totalFee = totalFee;
    }

    /**
     * @param unldnWt the unldnWt to set
     */
    public void setUnldnWt(String unldnWt) {
        this.unldnWt = unldnWt;
    }

    /**
     * @param vehImpnd the vehImpnd to set
     */
    public void setVehImpnd(String vehImpnd) {
        this.vehImpnd = vehImpnd;
    }

    /**
     * @param vehImpndPlace the vehImpndPlace to set
     */
    public void setVehImpndPlace(String vehImpndPlace) {
        this.vehImpndPlace = vehImpndPlace;
    }

    /**
     * @param vehicleNo the vehicleNo to set
     */
    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    /**
     * @param vehno1 the vehno1 to set
     */
    public void setVehno1(String vehno1) {
        this.vehno1 = vehno1;
    }

    /**
     * @param vehno2 the vehno2 to set
     */
    public void setVehno2(String vehno2) {
        this.vehno2 = vehno2;
    }

    /**
     * @param vhClass the vhClass to set
     */
    public void setVhClass(String vhClass) {
        this.vhClass = vhClass;
    }

    /**
     * @param witnessAdd the witnessAdd to set
     */
    public void setWitnessAdd(String witnessAdd) {
        this.witnessAdd = witnessAdd;
    }

    /**
     * @param witnessName the witnessName to set
     */
    public void setWitnessName(String witnessName) {
        this.witnessName = witnessName;
    }

    /**
     * @return the challanNo
     */
    public String getChallanNo() {
        return challanNo;
    }

    /**
     * @return the district
     */
    public String getDistrict() {
        return district;
    }

    /**
     * @param challanNo the challanNo to set
     */
    public void setChallanNo(String challanNo) {
        this.challanNo = challanNo;
    }

    /**
     * @param district the district to set
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the challanTime
     */
    public String getChallanTime() {
        return challanTime;
    }

    /**
     * @param challanTime the challanTime to set
     */
    public void setChallanTime(String challanTime) {
        this.challanTime = challanTime;
    }

    /**
     * @return the challanRefToCrt
     */
    public boolean isChallanRefToCrt() {
        return challanRefToCrt;
    }

    /**
     * @param challanRefToCrt the challanRefToCrt to set
     */
    public void setChallanRefToCrt(boolean challanRefToCrt) {
        this.challanRefToCrt = challanRefToCrt;
    }

    public Date getChallanDt() {
        return challanDt;
    }

    public void setChallanDt(Date challanDt) {
        this.challanDt = challanDt;
    }

    /**
     * @return the contactOfficer
     */
    public String getContactOfficer() {
        return contactOfficer;
    }

    /**
     * @param contactOfficer the contactOfficer to set
     */
    public void setContactOfficer(String contactOfficer) {
        this.contactOfficer = contactOfficer;
    }

    /**
     * @return the vehImpdDate
     */
    public Date getVehImpdDate() {
        return vehImpdDate;
    }

    /**
     * @param vehImpdDate the vehImpdDate to set
     */
    public void setVehImpdDate(Date vehImpdDate) {
        this.vehImpdDate = vehImpdDate;
    }

    /**
     * @return the AccuseAddress
     */
    public String getAccuseAddress() {
        return AccuseAddress;
    }

    /**
     * @param AccuseAddress the AccuseAddress to set
     */
    public void setAccuseAddress(String AccuseAddress) {
        this.AccuseAddress = AccuseAddress;
    }

    /**
     * @return the add1
     */
    public String getAdd1() {
        return add1;
    }

    /**
     * @return the add2
     */
    public String getAdd2() {
        return add2;
    }

    /**
     * @param add1 the add1 to set
     */
    public void setAdd1(String add1) {
        this.add1 = add1;
    }

    /**
     * @param add2 the add2 to set
     */
    public void setAdd2(String add2) {
        this.add2 = add2;
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
     * @return the sleeperCap
     */
    public String getSleeperCap() {
        return sleeperCap;
    }

    /**
     * @param sleeperCap the sleeperCap to set
     */
    public void setSleeperCap(String sleeperCap) {
        this.sleeperCap = sleeperCap;
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
     * @return the accusedRemarks
     */
    public String getAccusedRemarks() {
        return accusedRemarks;
    }

    /**
     * @param accusedRemarks the accusedRemarks to set
     */
    public void setAccusedRemarks(String accusedRemarks) {
        this.accusedRemarks = accusedRemarks;
    }

    /**
     * @return the impoundRemarks
     */
    public String getImpoundRemarks() {
        return impoundRemarks;
    }

    /**
     * @param impoundRemarks the impoundRemarks to set
     */
    public void setImpoundRemarks(String impoundRemarks) {
        this.impoundRemarks = impoundRemarks;
    }

    /**
     * @return the NCCRNo
     */
    public String getNCCRNo() {
        return NCCRNo;
    }

    /**
     * @param NCCRNo the NCCRNo to set
     */
    public void setNCCRNo(String NCCRNo) {
        this.NCCRNo = NCCRNo;
    }

    /**
     * @return the sezNo
     */
    public String getSezNo() {
        return sezNo;
    }

    /**
     * @param sezNo the sezNo to set
     */
    public void setSezNo(String sezNo) {
        this.sezNo = sezNo;
    }

    public String getOfficerName() {
        return officerName;
    }

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }

    public void setCMPCASE(String CMPCASE) {
        this.CMPCASE = CMPCASE;
    }

    /**
     * @return the CMPCASE
     */
    public String getCMPCASE() {
        return CMPCASE;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlaceCh() {
        return placeCh;
    }

    public void setPlaceCh(String placeCh) {
        this.placeCh = placeCh;
    }

    public String getDateHear() {
        return dateHear;
    }

    public void setDateHear(String dateHear) {
        this.dateHear = dateHear;
    }

    public String getChalOff() {
        return chalOff;
    }

    public void setChalOff(String chalOff) {
        this.chalOff = chalOff;
    }

    public String getRto() {
        return rto;
    }

    public void setRto(String rto) {
        this.rto = rto;
    }

    public String getImpndPlace() {
        return impndPlace;
    }

    public void setImpndPlace(String impndPlace) {
        this.impndPlace = impndPlace;
    }

    public String getImpndDate() {
        return impndDate;
    }

    public void setImpndDate(String impndDate) {
        this.impndDate = impndDate;
    }

    /**
     * @return the vhClassCode
     */
    public String getVhClassCode() {
        return vhClassCode;
    }

    /**
     * @param vhClassCode the vhClassCode to set
     */
    public void setVhClassCode(String vhClassCode) {
        this.vhClassCode = vhClassCode;
    }

    /**
     * @return the courtCd
     */
    public String getCourtCd() {
        return courtCd;
    }

    /**
     * @param courtCd the courtCd to set
     */
    public void setCourtCd(String courtCd) {
        this.courtCd = courtCd;
    }

    /**
     * @return the chalOffCode
     */
    public String getChalOffCode() {
        return chalOffCode;
    }

    /**
     * @param chalOffCode the chalOffCode to set
     */
    public void setChalOffCode(String chalOffCode) {
        this.chalOffCode = chalOffCode;
    }

    /**
     * @return the rtoCode
     */
    public String getRtoCode() {
        return rtoCode;
    }

    /**
     * @param rtoCode the rtoCode to set
     */
    public void setRtoCode(String rtoCode) {
        this.rtoCode = rtoCode;
    }

    /**
     * @return the stateCode
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * @param stateCode the stateCode to set
     */
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getHiddenaddressField() {
        return hiddenaddressField;
    }

    public void setHiddenaddressField(String hiddenaddressField) {
        this.hiddenaddressField = hiddenaddressField;
    }

    public String getApplicationNO() {
        return applicationNO;
    }

    public void setApplicationNO(String applicationNO) {
        this.applicationNO = applicationNO;
    }

    public Date getOnRoadPayRcieptDate() {
        return onRoadPayRcieptDate;
    }

    public void setOnRoadPayRcieptDate(Date onRoadPayRcieptDate) {
        this.onRoadPayRcieptDate = onRoadPayRcieptDate;
    }

    public String getOnRoadPayRcieptNo() {
        return onRoadPayRcieptNo;
    }

    public void setOnRoadPayRcieptNo(String onRoadPayRcieptNo) {
        this.onRoadPayRcieptNo = onRoadPayRcieptNo;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getOfficeStateCode() {
        return officeStateCode;
    }

    public void setOfficeStateCode(String officeStateCode) {
        this.officeStateCode = officeStateCode;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getDisposalChallanRemarks() {
        return disposalChallanRemarks;
    }

    public void setDisposalChallanRemarks(String disposalChallanRemarks) {
        this.disposalChallanRemarks = disposalChallanRemarks;
    }

    public String getDisposalChallanDate() {
        return disposalChallanDate;
    }

    public void setDisposalChallanDate(String disposalChallanDate) {
        this.disposalChallanDate = disposalChallanDate;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getRcieptDateInCompFee() {
        return rcieptDateInCompFee;
    }

    public void setRcieptDateInCompFee(String rcieptDateInCompFee) {
        this.rcieptDateInCompFee = rcieptDateInCompFee;
    }

    public String getRcieptNoInCompFee() {
        return rcieptNoInCompFee;
    }

    public void setRcieptNoInCompFee(String rcieptNoInCompFee) {
        this.rcieptNoInCompFee = rcieptNoInCompFee;
    }

    /**
     * @return the sezPoliceStation
     */
    public String getSezPoliceStation() {
        return sezPoliceStation;
    }

    /**
     * @param sezPoliceStation the sezPoliceStation to set
     */
    public void setSezPoliceStation(String sezPoliceStation) {
        this.sezPoliceStation = sezPoliceStation;
    }

    /**
     * @return the sezDistrict
     */
    public String getSezDistrict() {
        return sezDistrict;
    }

    /**
     * @param sezDistrict the sezDistrict to set
     */
    public void setSezDistrict(String sezDistrict) {
        this.sezDistrict = sezDistrict;
    }

    /**
     * @return the accuesdPoliceSation
     */
    public String getAccuesdPoliceSation() {
        return accuesdPoliceSation;
    }

    /**
     * @param accuesdPoliceSation the accuesdPoliceSation to set
     */
    public void setAccuesdPoliceSation(String accuesdPoliceSation) {
        this.accuesdPoliceSation = accuesdPoliceSation;
    }

    /**
     * @return the accuesdCity
     */
    public String getAccuesdCity() {
        return accuesdCity;
    }

    /**
     * @param accuesdCity the accuesdCity to set
     */
    public void setAccuesdCity(String accuesdCity) {
        this.accuesdCity = accuesdCity;
    }

    /**
     * @return the accuesdPincode
     */
    public int getAccuesdPincode() {
        return accuesdPincode;
    }

    /**
     * @param accuesdPincode the accuesdPincode to set
     */
    public void setAccuesdPincode(int accuesdPincode) {
        this.accuesdPincode = accuesdPincode;
    }

    /**
     * @return the accuesdFlag
     */
    public String getAccuesdFlag() {
        return accuesdFlag;
    }

    /**
     * @param accuesdFlag the accuesdFlag to set
     */
    public void setAccuesdFlag(String accuesdFlag) {
        this.accuesdFlag = accuesdFlag;
    }

    /**
     * @return the statusOfChallan
     */
    public String getStatusOfChallan() {
        return statusOfChallan;
    }

    /**
     * @param statusOfChallan the statusOfChallan to set
     */
    public void setStatusOfChallan(String statusOfChallan) {
        this.statusOfChallan = statusOfChallan;
    }

    public String getMagistrate() {
        return magistrate;
    }

    public void setMagistrate(String magistrate) {
        this.magistrate = magistrate;
    }

    /**
     * @return the accessPassangerWeight
     */
    public int getAccessPassangerWeight() {
        return accessPassangerWeight;
    }

    /**
     * @param accessPassangerWeight the accessPassangerWeight to set
     */
    public void setAccessPassangerWeight(int accessPassangerWeight) {
        this.accessPassangerWeight = accessPassangerWeight;
    }

    /**
     * @return the overLoadWeight
     */
    public int getOverLoadWeight() {
        return overLoadWeight;
    }

    /**
     * @param overLoadWeight the overLoadWeight to set
     */
    public void setOverLoadWeight(int overLoadWeight) {
        this.overLoadWeight = overLoadWeight;
    }

    /**
     * @return the typesOfGooods
     */
    public int getTypesOfGooods() {
        return typesOfGooods;
    }

    /**
     * @param typesOfGooods the typesOfGooods to set
     */
    public void setTypesOfGooods(int typesOfGooods) {
        this.typesOfGooods = typesOfGooods;
    }

    /**
     * @return the projection
     */
    public int getProjection() {
        return projection;
    }

    /**
     * @param projection the projection to set
     */
    public void setProjection(int projection) {
        this.projection = projection;
    }

    /**
     * @return the overHang
     */
    public int getOverHang() {
        return overHang;
    }

    /**
     * @param overHang the overHang to set
     */
    public void setOverHang(int overHang) {
        this.overHang = overHang;
    }

    /**
     * @return the accessAnimal
     */
    public int getAccessAnimal() {
        return accessAnimal;
    }

    /**
     * @param accessAnimal the accessAnimal to set
     */
    public void setAccessAnimal(int accessAnimal) {
        this.accessAnimal = accessAnimal;
    }

    /**
     * @return the animalType
     */
    public String getAnimalType() {
        return animalType;
    }

    /**
     * @param animalType the animalType to set
     */
    public void setAnimalType(String animalType) {
        this.animalType = animalType;
    }

    /**
     * @return the overloadVehicle
     */
    public boolean isOverloadVehicle() {
        return overloadVehicle;
    }

    /**
     * @param overloadVehicle the overloadVehicle to set
     */
    public void setOverloadVehicle(boolean overloadVehicle) {
        this.overloadVehicle = overloadVehicle;
    }

    /**
     * @return the overAccessPassanger
     */
    public boolean isOverAccessPassanger() {
        return overAccessPassanger;
    }

    /**
     * @param overAccessPassanger the overAccessPassanger to set
     */
    public void setOverAccessPassanger(boolean overAccessPassanger) {
        this.overAccessPassanger = overAccessPassanger;
    }

    public String getCommingFrom() {
        return commingFrom;
    }

    public void setCommingFrom(String commingFrom) {
        this.commingFrom = commingFrom;
    }

    public String getGoingTo() {
        return goingTo;
    }

    public void setGoingTo(String goingTo) {
        this.goingTo = goingTo;
    }

    /**
     * @return the checkBoxsettledAtSpot
     */
    public boolean isCheckBoxsettledAtSpot() {
        return checkBoxsettledAtSpot;
    }

    /**
     * @param checkBoxsettledAtSpot the checkBoxsettledAtSpot to set
     */
    public void setCheckBoxsettledAtSpot(boolean checkBoxsettledAtSpot) {
        this.checkBoxsettledAtSpot = checkBoxsettledAtSpot;
    }
}
