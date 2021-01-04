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
import nic.vahan.form.dobj.common.Draft_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;

/**
 *
 * @author tranC094
 */
public class Fee_Pay_Dobj implements Serializable {

    private String regnNo;
    private Date rcptDt;
    private String applNo;
    private Date op_dt;
    private String paymentMode;
    private long cash;
    private long excessAmount;
    private boolean isNewCollectionForm;
    private boolean blnTempRegn;
    private List<Draft_dobj> collectedDrafts;
    private List<FeeDobj> collectedFeeList;
    private Tax_Pay_Dobj collectedTaxDobj;
    private String state_cd;
    private Owner_dobj ownerDobj = new Owner_dobj();
    private List<Tax_Pay_Dobj> listTaxDobj;
    private PassengerPermitDetailDobj permitDobj;
    private String feeTypeCategory = "";
    private boolean taxInstallment = false;
    private String taxInstallMode = null;
    private List<TaxExemptiondobj> listExemDobj;
    private String remarks = null;
    private List<TaxInstallCollectionDobj> taxInstallDobjList = new ArrayList<>();

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public Date getRcptDt() {
        return rcptDt;
    }

    public void setRcptDt(Date rcptDt) {
        this.rcptDt = rcptDt;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public Date getOp_dt() {
        return op_dt;
    }

    public void setOp_dt(Date op_dt) {
        this.op_dt = op_dt;
    }

    public List<FeeDobj> getCollectedFeeList() {
        return collectedFeeList;
    }

    public void setCollectedFeeList(List<FeeDobj> collectedFeeList) {
        this.collectedFeeList = collectedFeeList;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public List<Draft_dobj> getCollectedDrafts() {
        return collectedDrafts;
    }

    public void setCollectedDrafts(List<Draft_dobj> collectedDrafts) {
        this.collectedDrafts = collectedDrafts;
    }

    public Tax_Pay_Dobj getCollectedTaxDobj() {
        return collectedTaxDobj;
    }

    public void setCollectedTaxDobj(Tax_Pay_Dobj collectedTaxDobj) {
        this.collectedTaxDobj = collectedTaxDobj;
    }

    public boolean getIsNewCollectionForm() {
        return isNewCollectionForm;
    }

    public void setIsNewCollectionForm(boolean isNewCollectionForm) {
        this.isNewCollectionForm = isNewCollectionForm;
    }

    /**
     * @return the blnTempRegn
     */
    public boolean isBlnTempRegn() {
        return blnTempRegn;
    }

    /**
     * @param blnTempRegn the blnTempRegn to set
     */
    public void setBlnTempRegn(boolean blnTempRegn) {
        this.blnTempRegn = blnTempRegn;
    }

    /**
     * @return the state_cd
     */
    public String getState_cd() {
        return state_cd;
    }

    /**
     * @param state_cd the state_cd to set
     */
    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    /**
     * @return the cash
     */
    public long getCash() {
        return cash;
    }

    /**
     * @param cash the cash to set
     */
    public void setCash(long cash) {
        this.cash = cash;
    }

    /**
     * @return the excessAmount
     */
    public long getExcessAmount() {
        return excessAmount;
    }

    /**
     * @param excessAmount the excessAmount to set
     */
    public void setExcessAmount(long excessAmount) {
        this.excessAmount = excessAmount;
    }

    /**
     * @return the ownerDobj
     */
    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    /**
     * @param ownerDobj the ownerDobj to set
     */
    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    /**
     * @return the listTaxDobj
     */
    public List<Tax_Pay_Dobj> getListTaxDobj() {
        return listTaxDobj;
    }

    /**
     * @param listTaxDobj the listTaxDobj to set
     */
    public void setListTaxDobj(List<Tax_Pay_Dobj> listTaxDobj) {
        this.listTaxDobj = listTaxDobj;
    }

    /**
     * @return the permitDobj
     */
    public PassengerPermitDetailDobj getPermitDobj() {
        return permitDobj;
    }

    /**
     * @param permitDobj the permitDobj to set
     */
    public void setPermitDobj(PassengerPermitDetailDobj permitDobj) {
        this.permitDobj = permitDobj;
    }

    /**
     * @return the feeTypeCategory
     */
    public String getFeeTypeCategory() {
        return feeTypeCategory;
    }

    /**
     * @param feeTypeCategory the feeTypeCategory to set
     */
    public void setFeeTypeCategory(String feeTypeCategory) {
        this.feeTypeCategory = feeTypeCategory;
    }

    /**
     * @return the taxInstallment
     */
    public boolean isTaxInstallment() {
        return taxInstallment;
    }

    /**
     * @param taxInstallment the taxInstallment to set
     */
    public void setTaxInstallment(boolean taxInstallment) {
        this.taxInstallment = taxInstallment;
    }

    /**
     * @return the taxInstallMode
     */
    public String getTaxInstallMode() {
        return taxInstallMode;
    }

    /**
     * @param taxInstallMode the taxInstallMode to set
     */
    public void setTaxInstallMode(String taxInstallMode) {
        this.taxInstallMode = taxInstallMode;
    }

    /**
     * @return the listExemDobj
     */
    public List<TaxExemptiondobj> getListExemDobj() {
        return listExemDobj;
    }

    /**
     * @param listExemDobj the listExemDobj to set
     */
    public void setListExemDobj(List<TaxExemptiondobj> listExemDobj) {
        this.listExemDobj = listExemDobj;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<TaxInstallCollectionDobj> getTaxInstallDobjList() {
        return taxInstallDobjList;
    }

    public void setTaxInstallDobjList(List<TaxInstallCollectionDobj> taxInstallDobjList) {
        this.taxInstallDobjList = taxInstallDobjList;
    }
}
