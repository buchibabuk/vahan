/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;

/**
 *
 * @author nicsi
 */
public class CompoundingTaxDetailsDobj implements Serializable {

    private String taxtype = "";
    private String taxUpto = "";
    private String taxFrom = "";
    private String taxAmount = "";
    private String penaltyAmount = "";
    private String adCfPenalty = "";
    private String taxOnroadPay = "";
    private String penaltyOnRoadPay = "";
    private String recieptCompTax = "";
    private String recieptDateCompTax = "";
    private String taxtypeDesc = "";
    private String showtaxfrom;
    private String showtaxupto;
    private String showtaxrcptdate;

    public CompoundingTaxDetailsDobj(String taxtype, String taxtypeDesc, String taxUpto, String taxFrom, String taxAmount, String penaltyAmount, String recieptCompTax, String recieptDateCompTax, String taxOnroadPay, String penaltyOnRoadPay, String showtaxupto, String showtaxfrom, String showtaxrcptdate) {
        this.taxtype = taxtype;
        this.taxUpto = taxUpto;
        this.taxFrom = taxFrom;
        this.taxAmount = taxAmount;
        this.penaltyAmount = penaltyAmount;
        this.taxOnroadPay = taxOnroadPay;
        this.penaltyOnRoadPay = penaltyOnRoadPay;
        this.recieptCompTax = recieptCompTax;
        this.recieptDateCompTax = recieptDateCompTax;
        this.taxtypeDesc = taxtypeDesc;
        this.showtaxupto = showtaxupto;
        this.showtaxfrom = showtaxfrom;
        this.showtaxrcptdate = showtaxrcptdate;
    }

    public CompoundingTaxDetailsDobj() {
    }

    public String getTaxtype() {
        return taxtype;
    }

    public void setTaxtype(String taxtype) {
        this.taxtype = taxtype;
    }

    public String getTaxUpto() {
        return taxUpto;
    }

    public void setTaxUpto(String taxUpto) {
        this.taxUpto = taxUpto;
    }

    public String getTaxFrom() {
        return taxFrom;
    }

    public void setTaxFrom(String taxFrom) {
        this.taxFrom = taxFrom;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setPenaltyAmount(String penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    /**
     * @return the adCfPenalty
     */
    public String getAdCfPenalty() {
        return adCfPenalty;
    }

    /**
     * @param adCfPenalty the adCfPenalty to set
     */
    public void setAdCfPenalty(String adCfPenalty) {
        this.adCfPenalty = adCfPenalty;
    }

    /**
     * @return the taxOnroadPay
     */
    public String getTaxOnroadPay() {
        return taxOnroadPay;
    }

    /**
     * @param taxOnroadPay the taxOnroadPay to set
     */
    public void setTaxOnroadPay(String taxOnroadPay) {
        this.taxOnroadPay = taxOnroadPay;
    }

    /**
     * @return the penaltyOnRoadPay
     */
    public String getPenaltyOnRoadPay() {
        return penaltyOnRoadPay;
    }

    /**
     * @param penaltyOnRoadPay the penaltyOnRoadPay to set
     */
    public void setPenaltyOnRoadPay(String penaltyOnRoadPay) {
        this.penaltyOnRoadPay = penaltyOnRoadPay;
    }

    /**
     * @return the recieptCompTax
     */
    public String getRecieptCompTax() {
        return recieptCompTax;
    }

    /**
     * @param recieptCompTax the recieptCompTax to set
     */
    public void setRecieptCompTax(String recieptCompTax) {
        this.recieptCompTax = recieptCompTax;
    }

    /**
     * @return the recieptDateCompTax
     */
    public String getRecieptDateCompTax() {
        return recieptDateCompTax;
    }

    /**
     * @param recieptDateCompTax the recieptDateCompTax to set
     */
    public void setRecieptDateCompTax(String recieptDateCompTax) {
        this.recieptDateCompTax = recieptDateCompTax;
    }

    public String getTaxtypeDesc() {
        return taxtypeDesc;
    }

    public void setTaxtypeDesc(String taxtypeDesc) {
        this.taxtypeDesc = taxtypeDesc;
    }

    public String getShowtaxfrom() {
        return showtaxfrom;
    }

    public void setShowtaxfrom(String showtaxfrom) {
        this.showtaxfrom = showtaxfrom;
    }

    public String getShowtaxupto() {
        return showtaxupto;
    }

    public void setShowtaxupto(String showtaxupto) {
        this.showtaxupto = showtaxupto;
    }

    public String getShowtaxrcptdate() {
        return showtaxrcptdate;
    }

    public void setShowtaxrcptdate(String showtaxrcptdate) {
        this.showtaxrcptdate = showtaxrcptdate;
    }
}
