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

/**
 *
 * @author tranC094
 */
public class Tax_Pay_Dobj implements Serializable {

    private List<DOTaxDetail> taxGroupDetails;
    private List<DOTaxDetail> taxBreakDetails;
    private List<PaymentCollectionDobj> paymntCollectionBean;
    private String state_cd;
    private Date op_dt;
    private Integer off_cd; // rto_cd
    private String deal_cd;
    private Long totalAmount;
    private String regnNo;
    private String applNo;
    private Date rcptDate;
    private String payMode;
    private String rcptNo;
    private String paymentMode;
    //-----
    private String finalTaxFrom = null;
    private String finalTaxUpto = null;
    private long finalTaxAmount = 0;
    private String taxMode = null;
    private int pur_cd = 0;
    private long totalPaybaleTax = 0;
    private long totalPaybalePenalty = 0;
    private long totalPaybaleSurcharge = 0;
    private long totalPaybaleRebate = 0;
    private long totalPaybaleInterest = 0;
    private int noOfAdvUnits = 1;
    private long totalPaybaleTax1 = 0;
    private long totalPaybaleTax2 = 0;
    private long previousAdj = 0;
    private String taxCeaseDate = null;

    public List<DOTaxDetail> getTaxGroupDetails() {
        if (taxGroupDetails == null) {
            taxGroupDetails = new ArrayList<DOTaxDetail>();
        }
        return taxGroupDetails;
    }

    public void setTaxGroupDetails(List<DOTaxDetail> taxGroupDetails) {
        this.taxGroupDetails = taxGroupDetails;
    }

    public List<DOTaxDetail> getTaxBreakDetails() {
        if (taxBreakDetails == null) {
            taxBreakDetails = new ArrayList<DOTaxDetail>();
        }
        return taxBreakDetails;
    }

    public List<PaymentCollectionDobj> getPaymntCollectionBean() {
        if (paymntCollectionBean == null) {
            paymntCollectionBean = new ArrayList<PaymentCollectionDobj>();
        }
        return paymntCollectionBean;
    }

    public void setPaymntCollectionBean(List<PaymentCollectionDobj> paymntCollectionBean) {
        this.paymntCollectionBean = paymntCollectionBean;
    }

    public void setTaxBreakDetails(List<DOTaxDetail> taxBreakDetails) {
        this.taxBreakDetails = taxBreakDetails;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public Date getOp_dt() {
        return op_dt;
    }

    public void setOp_dt(Date op_dt) {
        this.op_dt = op_dt;
    }

    public Integer getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(Integer off_cd) {
        this.off_cd = off_cd;
    }

    public String getDeal_cd() {
        return deal_cd;
    }

    public void setDeal_cd(String deal_cd) {
        this.deal_cd = deal_cd;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public Date getRcptDate() {
        return rcptDate;
    }

    public void setRcptDate(Date rcptDate) {
        this.rcptDate = rcptDate;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getRcptNo() {
        return rcptNo;
    }

    public void setRcptNo(String rcptNo) {
        this.rcptNo = rcptNo;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    /**
     * @return the finalTaxFrom
     */
    public String getFinalTaxFrom() {
        return finalTaxFrom;
    }

    /**
     * @param finalTaxFrom the finalTaxFrom to set
     */
    public void setFinalTaxFrom(String finalTaxFrom) {
        this.finalTaxFrom = finalTaxFrom;
    }

    /**
     * @return the finalTaxUpto
     */
    public String getFinalTaxUpto() {
        return finalTaxUpto;
    }

    /**
     * @param finalTaxUpto the finalTaxUpto to set
     */
    public void setFinalTaxUpto(String finalTaxUpto) {
        this.finalTaxUpto = finalTaxUpto;
    }

    /**
     * @return the finalTaxAmount
     */
    public long getFinalTaxAmount() {
        return finalTaxAmount;
    }

    /**
     * @param finalTaxAmount the finalTaxAmount to set
     */
    public void setFinalTaxAmount(long finalTaxAmount) {
        this.finalTaxAmount = finalTaxAmount;
    }

    /**
     * @return the taxMode
     */
    public String getTaxMode() {
        return taxMode;
    }

    /**
     * @param taxMode the taxMode to set
     */
    public void setTaxMode(String taxMode) {
        this.taxMode = taxMode;
    }

    /**
     * @return the pur_cd
     */
    public int getPur_cd() {
        return pur_cd;
    }

    /**
     * @param pur_cd the pur_cd to set
     */
    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    /**
     * @return the totalPaybaleTax
     */
    public long getTotalPaybaleTax() {
        return totalPaybaleTax;
    }

    /**
     * @param totalPaybaleTax the totalPaybaleTax to set
     */
    public void setTotalPaybaleTax(long totalPaybaleTax) {
        this.totalPaybaleTax = totalPaybaleTax;
    }

    /**
     * @return the totalPaybalePenalty
     */
    public long getTotalPaybalePenalty() {
        return totalPaybalePenalty;
    }

    /**
     * @param totalPaybalePenalty the totalPaybalePenalty to set
     */
    public void setTotalPaybalePenalty(long totalPaybalePenalty) {
        this.totalPaybalePenalty = totalPaybalePenalty;
    }

    /**
     * @return the totalPaybaleSurcharge
     */
    public long getTotalPaybaleSurcharge() {
        return totalPaybaleSurcharge;
    }

    /**
     * @param totalPaybaleSurcharge the totalPaybaleSurcharge to set
     */
    public void setTotalPaybaleSurcharge(long totalPaybaleSurcharge) {
        this.totalPaybaleSurcharge = totalPaybaleSurcharge;
    }

    /**
     * @return the totalPaybaleRebate
     */
    public long getTotalPaybaleRebate() {
        return totalPaybaleRebate;
    }

    /**
     * @param totalPaybaleRebate the totalPaybaleRebate to set
     */
    public void setTotalPaybaleRebate(long totalPaybaleRebate) {
        this.totalPaybaleRebate = totalPaybaleRebate;
    }

    /**
     * @return the totalPaybaleInterest
     */
    public long getTotalPaybaleInterest() {
        return totalPaybaleInterest;
    }

    /**
     * @param totalPaybaleInterest the totalPaybaleInterest to set
     */
    public void setTotalPaybaleInterest(long totalPaybaleInterest) {
        this.totalPaybaleInterest = totalPaybaleInterest;
    }

    /**
     * @return the noOfAdvUnits
     */
    public int getNoOfAdvUnits() {
        return noOfAdvUnits;
    }

    /**
     * @param noOfAdvUnits the noOfAdvUnits to set
     */
    public void setNoOfAdvUnits(int noOfAdvUnits) {
        this.noOfAdvUnits = noOfAdvUnits;
    }

    /**
     * @return the totalPaybaleTax1
     */
    public long getTotalPaybaleTax1() {
        return totalPaybaleTax1;
    }

    /**
     * @param totalPaybaleTax1 the totalPaybaleTax1 to set
     */
    public void setTotalPaybaleTax1(long totalPaybaleTax1) {
        this.totalPaybaleTax1 = totalPaybaleTax1;
    }

    /**
     * @return the totalPaybaleTax2
     */
    public long getTotalPaybaleTax2() {
        return totalPaybaleTax2;
    }

    /**
     * @param totalPaybaleTax2 the totalPaybaleTax2 to set
     */
    public void setTotalPaybaleTax2(long totalPaybaleTax2) {
        this.totalPaybaleTax2 = totalPaybaleTax2;
    }

    /**
     * @return the previousAdj
     */
    public long getPreviousAdj() {
        return previousAdj;
    }

    /**
     * @param previousAdj the previousAdj to set
     */
    public void setPreviousAdj(long previousAdj) {
        this.previousAdj = previousAdj;
    }

    /**
     * @return the TaxCeaseDate
     */
    public String getTaxCeaseDate() {
        return taxCeaseDate;
    }

    /**
     * @param TaxCeaseDate the TaxCeaseDate to set
     */
    public void setTaxCeaseDate(String taxCeaseDate) {
        this.taxCeaseDate = taxCeaseDate;
    }
}
