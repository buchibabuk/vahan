/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author tranC094
 */
public class ChallanFeeDobj implements Serializable {

    private String bookNo;
    private String challanNo;
    private String vehicleNo;
    private String accusedCatg;
    private String purposeCd;
    private String acFee;
    private String cFee;
    private String sFee;
    private String courtRecpNo;
    private String courtRecpDt;
    private String recpNo;
    private String recpDt;
    private String dealCd;
    private String opDate;
    private String paidDealCd;
    private String paidOpDate;
    private String aPaidDealCd;
    private String stateCd;
    private String rtoCd;
    private String applicationNo;
    private String cmPaymentMode;
    List<ChallanOwnerTaxDobj> dobjTax = null;

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
     * @return the vehicleNo
     */
    public String getVehicleNo() {
        return vehicleNo;
    }

    /**
     * @param vehicleNo the vehicleNo to set
     */
    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    /**
     * @return the accusedCatg
     */
    public String getAccusedCatg() {
        return accusedCatg;
    }

    /**
     * @param accusedCatg the accusedCatg to set
     */
    public void setAccusedCatg(String accusedCatg) {
        this.accusedCatg = accusedCatg;
    }

    /**
     * @return the purposeCd
     */
    public String getPurposeCd() {
        return purposeCd;
    }

    /**
     * @param purposeCd the purposeCd to set
     */
    public void setPurposeCd(String purposeCd) {
        this.purposeCd = purposeCd;
    }

    /**
     * @return the acFee
     */
    public String getAcFee() {
        return acFee;
    }

    /**
     * @param acFee the acFee to set
     */
    public void setAcFee(String acFee) {
        this.acFee = acFee;
    }

    /**
     * @return the cFee
     */
    public String getcFee() {
        return cFee;
    }

    /**
     * @param cFee the cFee to set
     */
    public void setcFee(String cFee) {
        this.cFee = cFee;
    }

    /**
     * @return the sFee
     */
    public String getsFee() {
        return sFee;
    }

    /**
     * @param sFee the sFee to set
     */
    public void setsFee(String sFee) {
        this.sFee = sFee;
    }

    /**
     * @return the courtRecpNo
     */
    public String getCourtRecpNo() {
        return courtRecpNo;
    }

    /**
     * @param courtRecpNo the courtRecpNo to set
     */
    public void setCourtRecpNo(String courtRecpNo) {
        this.courtRecpNo = courtRecpNo;
    }

    /**
     * @return the courtRecpDt
     */
    public String getCourtRecpDt() {
        return courtRecpDt;
    }

    /**
     * @param courtRecpDt the courtRecpDt to set
     */
    public void setCourtRecpDt(String courtRecpDt) {
        this.courtRecpDt = courtRecpDt;
    }

    /**
     * @return the recpNo
     */
    public String getRecpNo() {
        return recpNo;
    }

    /**
     * @param recpNo the recpNo to set
     */
    public void setRecpNo(String recpNo) {
        this.recpNo = recpNo;
    }

    /**
     * @return the recpDt
     */
    public String getRecpDt() {
        return recpDt;
    }

    /**
     * @param recpDt the recpDt to set
     */
    public void setRecpDt(String recpDt) {
        this.recpDt = recpDt;
    }

    /**
     * @return the dealCd
     */
    public String getDealCd() {
        return dealCd;
    }

    /**
     * @param dealCd the dealCd to set
     */
    public void setDealCd(String dealCd) {
        this.dealCd = dealCd;
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
     * @return the paidDealCd
     */
    public String getPaidDealCd() {
        return paidDealCd;
    }

    /**
     * @param paidDealCd the paidDealCd to set
     */
    public void setPaidDealCd(String paidDealCd) {
        this.paidDealCd = paidDealCd;
    }

    /**
     * @return the paidOpDate
     */
    public String getPaidOpDate() {
        return paidOpDate;
    }

    /**
     * @param paidOpDate the paidOpDate to set
     */
    public void setPaidOpDate(String paidOpDate) {
        this.paidOpDate = paidOpDate;
    }

    /**
     * @return the aPaidDealCd
     */
    public String getaPaidDealCd() {
        return aPaidDealCd;
    }

    /**
     * @param aPaidDealCd the aPaidDealCd to set
     */
    public void setaPaidDealCd(String aPaidDealCd) {
        this.aPaidDealCd = aPaidDealCd;
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

    public List<ChallanOwnerTaxDobj> getDobjTax() {
        return dobjTax;
    }

    public void setDobjTax(List<ChallanOwnerTaxDobj> dobjTax) {
        this.dobjTax = dobjTax;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public String getCmPaymentMode() {
        return cmPaymentMode;
    }

    public void setCmPaymentMode(String cmPaymentMode) {
        this.cmPaymentMode = cmPaymentMode;
    }
}
