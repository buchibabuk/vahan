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
 * @author tranC105
 */
public class CompoundingInOfficeDobj implements Serializable, Cloneable {

    private String regnNo;
    private String chasiNo;
    private String vhClass;
    private String accusedName;
    private String ownerName;
    private String standCap;
    private String seatCap;
    private String sleepCap;
    private String fuel;
    private String challanPlace;
    private String challanOfficer;
    private String color;
    private String ladenWt;
    private String stateCd;
    private String rtoCd;
    private String ncCrNo;
    private String challanDt;
    private String challanTime;
    private String challanNo;
    private String bookNo;
    private String miscFee = "0";
    private String courtName;
    private String hearDate;
    private String impndDate;
    private String impndPlace;
    private String seizureNo;
    private String contactOfficer;
    private String compoundFee;
    private String onroadrecptno;
    private String totalFee = "0";
    private String stateOfVeh;
    private String rtoOfVeh;
    private String settleChalTax = "0";
    private String settleChalPenalty = "0";
    private String settleChalInterest = "0";
    private String settleFee = "0";
    private String accuCateg;
    private String vhClassDescr;
    private String vehImpound;
    private String applicationNo;
    private String courtRecieptNo;
    private String courtPaidAmount;
    private Date courtRecieptDate;
    private String CourtDecision;
    private String advcompfee;
    boolean cbxFeePaidAtCOurt;
    private boolean cbxFeePaidAtAuthority;
    private String taxFrom;
    private String taxUpto;
    private int offencePenalty;
    private String authortyOffence;
    private String authorties;
    private String authortyDecision;
    private String authpenalty;
    private String authortySection;
    private Date authortyReferDate;
    private Date authDecisionDate;
    private String taxType;
    private String reason;
    private String coming_from;
    private String going_to;

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
     * @return the vhClass
     */
    public String getVhClass() {
        return vhClass;
    }

    /**
     * @param vhClass the vhClass to set
     */
    public void setVhClass(String vhClass) {
        this.vhClass = vhClass;
    }

    /**
     * @return the accusedName
     */
    public String getAccusedName() {
        return accusedName;
    }

    /**
     * @param accusedName the accusedName to set
     */
    public void setAccusedName(String accusedName) {
        this.accusedName = accusedName;
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
     * @return the challanPlace
     */
    public String getChallanPlace() {
        return challanPlace;
    }

    /**
     * @param challanPlace the challanPlace to set
     */
    public void setChallanPlace(String challanPlace) {
        this.challanPlace = challanPlace;
    }

    /**
     * @return the challanOfficer
     */
    public String getChallanOfficer() {
        return challanOfficer;
    }

    /**
     * @param challanOfficer the challanOfficer to set
     */
    public void setChallanOfficer(String challanOfficer) {
        this.challanOfficer = challanOfficer;
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
     * @return the ladenWt
     */
    public String getLadenWt() {
        return ladenWt;
    }

    /**
     * @param ladenWt the ladenWt to set
     */
    public void setLadenWt(String ladenWt) {
        this.ladenWt = ladenWt;
    }

    /**
     * @return the stateCd
     */
    public String getStateCd() {
        return stateCd;
    }

    /**
     * @param stateCd the stateCd to set
     */
    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    /**
     * @return the rtoCd
     */
    public String getRtoCd() {
        return rtoCd;
    }

    /**
     * @param rtoCd the rtoCd to set
     */
    public void setRtoCd(String rtoCd) {
        this.rtoCd = rtoCd;
    }

    /**
     * @return the ncCrNo
     */
    public String getNcCrNo() {
        return ncCrNo;
    }

    /**
     * @param ncCrNo the ncCrNo to set
     */
    public void setNcCrNo(String ncCrNo) {
        this.ncCrNo = ncCrNo;
    }

    /**
     * @return the challanDt
     */
    public String getChallanDt() {
        return challanDt;
    }

    /**
     * @param challanDt the challanDt to set
     */
    public void setChallanDt(String challanDt) {
        this.challanDt = challanDt;
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
     * @return the challanNo
     */
    public String getChallanNo() {
        return challanNo;
    }

    /**
     * @param challanNo the challanNo to set
     */
    public void setChallanNo(String challanNo) {
        this.challanNo = challanNo;
    }

    /**
     * @return the bookNo
     */
    public String getBookNo() {
        return bookNo;
    }

    /**
     * @param bookNo the bookNo to set
     */
    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    /**
     * @return the miscFee
     */
    public String getMiscFee() {
        return miscFee;
    }

    /**
     * @param miscFee the miscFee to set
     */
    public void setMiscFee(String miscFee) {
        this.miscFee = miscFee;
    }

    /**
     * @return the courtName
     */
    public String getCourtName() {
        return courtName;
    }

    /**
     * @param courtName the courtName to set
     */
    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    /**
     * @return the hearDate
     */
    public String getHearDate() {
        return hearDate;
    }

    /**
     * @param hearDate the hearDate to set
     */
    public void setHearDate(String hearDate) {
        this.hearDate = hearDate;
    }

    /**
     * @return the impndDate
     */
    public String getImpndDate() {
        return impndDate;
    }

    /**
     * @param impndDate the impndDate to set
     */
    public void setImpndDate(String impndDate) {
        this.impndDate = impndDate;
    }

    /**
     * @return the impndPlace
     */
    public String getImpndPlace() {
        return impndPlace;
    }

    /**
     * @param impndPlace the impndPlace to set
     */
    public void setImpndPlace(String impndPlace) {
        this.impndPlace = impndPlace;
    }

    /**
     * @return the seizureNo
     */
    public String getSeizureNo() {
        return seizureNo;
    }

    /**
     * @param seizureNo the seizureNo to set
     */
    public void setSeizureNo(String seizureNo) {
        this.seizureNo = seizureNo;
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
     * @return the compoundFee
     */
    public String getCompoundFee() {
        return compoundFee;
    }

    /**
     * @param compoundFee the compoundFee to set
     */
    public void setCompoundFee(String compoundFee) {
        this.compoundFee = compoundFee;
    }

    /**
     * @return the totalFee
     */
    public String getTotalFee() {
        return totalFee;
    }

    /**
     * @param totalFee the totalFee to set
     */
    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    /**
     * @return the stateOfVeh
     */
    public String getStateOfVeh() {
        return stateOfVeh;
    }

    /**
     * @param stateOfVeh the stateOfVeh to set
     */
    public void setStateOfVeh(String stateOfVeh) {
        this.stateOfVeh = stateOfVeh;
    }

    /**
     * @return the rtoOfVeh
     */
    public String getRtoOfVeh() {
        return rtoOfVeh;
    }

    /**
     * @param rtoOfVeh the rtoOfVeh to set
     */
    public void setRtoOfVeh(String rtoOfVeh) {
        this.rtoOfVeh = rtoOfVeh;
    }

    /**
     * @return the settleChalTax
     */
    public String getSettleChalTax() {
        return settleChalTax;
    }

    /**
     * @param settleChalTax the settleChalTax to set
     */
    public void setSettleChalTax(String settleChalTax) {
        this.settleChalTax = settleChalTax;
    }

    /**
     * @return the settleChalPenalty
     */
    public String getSettleChalPenalty() {
        return settleChalPenalty;
    }

    /**
     * @param settleChalPenalty the settleChalPenalty to set
     */
    public void setSettleChalPenalty(String settleChalPenalty) {
        this.settleChalPenalty = settleChalPenalty;
    }

    /**
     * @return the settleChalInterest
     */
    public String getSettleChalInterest() {
        return settleChalInterest;
    }

    /**
     * @param settleChalInterest the settleChalInterest to set
     */
    public void setSettleChalInterest(String settleChalInterest) {
        this.settleChalInterest = settleChalInterest;
    }

    /**
     * @return the settleFee
     */
    public String getSettleFee() {
        return settleFee;
    }

    /**
     * @param settleFee the settleFee to set
     */
    public void setSettleFee(String settleFee) {
        this.settleFee = settleFee;
    }

    /**
     * @return the accuCateg
     */
    public String getAccuCateg() {
        return accuCateg;
    }

    /**
     * @param accuCateg the accuCateg to set
     */
    public void setAccuCateg(String accuCateg) {
        this.accuCateg = accuCateg;
    }

    /**
     * @return the vhClassDescr
     */
    public String getVhClassDescr() {
        return vhClassDescr;
    }

    /**
     * @param vhClassDescr the vhClassDescr to set
     */
    public void setVhClassDescr(String vhClassDescr) {
        this.vhClassDescr = vhClassDescr;
    }

    /**
     * @return the vehImpound
     */
    public String getVehImpound() {
        return vehImpound;
    }

    /**
     * @param vehImpound the vehImpound to set
     */
    public void setVehImpound(String vehImpound) {
        this.vehImpound = vehImpound;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public String getCourtRecieptNo() {
        return courtRecieptNo;
    }

    public void setCourtRecieptNo(String courtRecieptNo) {
        this.courtRecieptNo = courtRecieptNo;
    }

    public String getCourtPaidAmount() {
        return courtPaidAmount;
    }

    public void setCourtPaidAmount(String courtPaidAmount) {
        this.courtPaidAmount = courtPaidAmount;
    }

    public Date getCourtRecieptDate() {
        return courtRecieptDate;
    }

    public void setCourtRecieptDate(Date courtRecieptDate) {
        this.courtRecieptDate = courtRecieptDate;
    }

    public String getCourtDecision() {
        return CourtDecision;
    }

    public void setCourtDecision(String CourtDecision) {
        this.CourtDecision = CourtDecision;
    }

    public boolean isCbxFeePaidAtCOurt() {
        return cbxFeePaidAtCOurt;
    }

    public void setCbxFeePaidAtCOurt(boolean cbxFeePaidAtCOurt) {
        this.cbxFeePaidAtCOurt = cbxFeePaidAtCOurt;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getOnroadrecptno() {
        return onroadrecptno;
    }

    public void setOnroadrecptno(String onroadrecptno) {
        this.onroadrecptno = onroadrecptno;
    }

    public String getAdvcompfee() {
        return advcompfee;
    }

    public void setAdvcompfee(String advcompfee) {
        this.advcompfee = advcompfee;
    }

    public String getTaxFrom() {
        return taxFrom;
    }

    public void setTaxFrom(String taxFrom) {
        this.taxFrom = taxFrom;
    }

    public String getTaxUpto() {
        return taxUpto;
    }

    public void setTaxUpto(String taxUpto) {
        this.taxUpto = taxUpto;
    }

    public int getOffencePenalty() {
        return offencePenalty;
    }

    public void setOffencePenalty(int offencePenalty) {
        this.offencePenalty = offencePenalty;
    }

    /**
     * @return the cbxFeePaidAtAuthority
     */
    public boolean isCbxFeePaidAtAuthority() {
        return cbxFeePaidAtAuthority;
    }

    /**
     * @param cbxFeePaidAtAuthority the cbxFeePaidAtAuthority to set
     */
    public void setCbxFeePaidAtAuthority(boolean cbxFeePaidAtAuthority) {
        this.cbxFeePaidAtAuthority = cbxFeePaidAtAuthority;
    }

    /**
     * @return the authortyOffence
     */
    public String getAuthortyOffence() {
        return authortyOffence;
    }

    /**
     * @param authortyOffence the authortyOffence to set
     */
    public void setAuthortyOffence(String authortyOffence) {
        this.authortyOffence = authortyOffence;
    }

    /**
     * @return the authorties
     */
    public String getAuthorties() {
        return authorties;
    }

    /**
     * @param authorties the authorties to set
     */
    public void setAuthorties(String authorties) {
        this.authorties = authorties;
    }

    /**
     * @return the authortyDecision
     */
    public String getAuthortyDecision() {
        return authortyDecision;
    }

    /**
     * @param authortyDecision the authortyDecision to set
     */
    public void setAuthortyDecision(String authortyDecision) {
        this.authortyDecision = authortyDecision;
    }

    /**
     * @return the authpenalty
     */
    public String getAuthpenalty() {
        return authpenalty;
    }

    /**
     * @param authpenalty the authpenalty to set
     */
    public void setAuthpenalty(String authpenalty) {
        this.authpenalty = authpenalty;
    }

    /**
     * @return the authortySection
     */
    public String getAuthortySection() {
        return authortySection;
    }

    /**
     * @param authortySection the authortySection to set
     */
    public void setAuthortySection(String authortySection) {
        this.authortySection = authortySection;
    }

    /**
     * @return the authortyReferDate
     */
    public Date getAuthortyReferDate() {
        return authortyReferDate;
    }

    /**
     * @param authortyReferDate the authortyReferDate to set
     */
    public void setAuthortyReferDate(Date authortyReferDate) {
        this.authortyReferDate = authortyReferDate;
    }

    /**
     * @return the authDecisionDate
     */
    public Date getAuthDecisionDate() {
        return authDecisionDate;
    }

    /**
     * @param authDecisionDate the authDecisionDate to set
     */
    public void setAuthDecisionDate(Date authDecisionDate) {
        this.authDecisionDate = authDecisionDate;
    }

    /**
     * @return the taxType
     */
    public String getTaxType() {
        return taxType;
    }

    /**
     * @param taxType the taxType to set
     */
    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getComing_from() {
        return coming_from;
    }

    public void setComing_from(String coming_from) {
        this.coming_from = coming_from;
    }

    public String getGoing_to() {
        return going_to;
    }

    public void setGoing_to(String going_to) {
        this.going_to = going_to;
    }
}
