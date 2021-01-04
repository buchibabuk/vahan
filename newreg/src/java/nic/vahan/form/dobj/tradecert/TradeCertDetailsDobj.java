/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.tradecert;

import java.util.Date;

/**
 *
 * @author tranC107
 */
public class TradeCertDetailsDobj {

    private String stateCd;
    private int offCd;
    private String dealerCd;
    private String vehCatg;
    private String tradeCertNo;
    private int noOfVeh;
    private int noOfVehUsed;
    private Date validUpto;
    private String message;
    private String tradeValidFrom;
    private String tradeValidUpto;

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
     * @return the offCd
     */
    public int getOffCd() {
        return offCd;
    }

    /**
     * @param offCd the offCd to set
     */
    public void setOffCd(int offCd) {
        this.offCd = offCd;
    }

    /**
     * @return the dealerCd
     */
    public String getDealerCd() {
        return dealerCd;
    }

    /**
     * @param dealerCd the dealerCd to set
     */
    public void setDealerCd(String dealerCd) {
        this.dealerCd = dealerCd;
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
     * @return the tradeCertNo
     */
    public String getTradeCertNo() {
        return tradeCertNo;
    }

    /**
     * @param tradeCertNo the tradeCertNo to set
     */
    public void setTradeCertNo(String tradeCertNo) {
        this.tradeCertNo = tradeCertNo;
    }

    /**
     * @return the noOfVeh
     */
    public int getNoOfVeh() {
        return noOfVeh;
    }

    /**
     * @param noOfVeh the noOfVeh to set
     */
    public void setNoOfVeh(int noOfVeh) {
        this.noOfVeh = noOfVeh;
    }

    /**
     * @return the noOfVehUsed
     */
    public int getNoOfVehUsed() {
        return noOfVehUsed;
    }

    /**
     * @param noOfVehUsed the noOfVehUsed to set
     */
    public void setNoOfVehUsed(int noOfVehUsed) {
        this.noOfVehUsed = noOfVehUsed;
    }

    /**
     * @return the validUpto
     */
    public Date getValidUpto() {
        return validUpto;
    }

    /**
     * @param validUpto the validUpto to set
     */
    public void setValidUpto(Date validUpto) {
        this.validUpto = validUpto;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the tradeValidFrom
     */
    public String getTradeValidFrom() {
        return tradeValidFrom;
    }

    /**
     * @param tradeValidFrom the tradeValidFrom to set
     */
    public void setTradeValidFrom(String tradeValidFrom) {
        this.tradeValidFrom = tradeValidFrom;
    }

    /**
     * @return the tradeValidUpto
     */
    public String getTradeValidUpto() {
        return tradeValidUpto;
    }

    /**
     * @param tradeValidUpto the tradeValidUpto to set
     */
    public void setTradeValidUpto(String tradeValidUpto) {
        this.tradeValidUpto = tradeValidUpto;
    }
}
