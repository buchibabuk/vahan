/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AuctionDobj implements Cloneable, Serializable {

    private String stateCd;
    private int offCd;
    private String applNo;
    private String regnNo = "";
    private String chasiNo;
    private String stateCdFrom;
    private int offCdFrom;
    private Date auctionDate;
    private String firNo;
    private Date firDate;
    private String reason;
    private String orderNo;
    private Date currenDate = new Date();
    private Date purchaseDate;
    private boolean disableAuctionPanel;
    private boolean allowToMoveBlackListedDataToHistory;
    private Date regnDt;
    private String auctionBy;

    /**
     * @return the disableAuctionPanel
     */
    public boolean isDisableAuctionPanel() {
        return disableAuctionPanel;
    }

    /**
     * @param disableAuctionPanel the disableAuctionPanel to set
     */
    public void setDisableAuctionPanel(boolean disableAuctionPanel) {
        this.disableAuctionPanel = disableAuctionPanel;
    }

    /**
     * @return the minDate
     */
    public Date getCurrentDate() {
        return currenDate;
    }

    /**
     * @param minDate the minDate to set
     */
    public void setCurrentDate(Date currenDate) {
        this.currenDate = currenDate;
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
     * @return the applNo
     */
    public String getApplNo() {
        return applNo;
    }

    /**
     * @param applNo the applNo to set
     */
    public void setApplNo(String applNo) {
        this.applNo = applNo;
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
     * @return the stateCdFrom
     */
    public String getStateCdFrom() {
        return stateCdFrom;
    }

    /**
     * @param stateCdFrom the stateCdFrom to set
     */
    public void setStateCdFrom(String stateCdFrom) {
        this.stateCdFrom = stateCdFrom;
    }

    /**
     * @return the offCdFrom
     */
    public int getOffCdFrom() {
        return offCdFrom;
    }

    /**
     * @param offCdFrom the offCdFrom to set
     */
    public void setOffCdFrom(int offCdFrom) {
        this.offCdFrom = offCdFrom;
    }

    /**
     * @return the auctionDate
     */
    public Date getAuctionDate() {
        return auctionDate;
    }

    /**
     * @param auctionDate the auctionDate to set
     */
    public void setAuctionDate(Date auctionDate) {
        this.auctionDate = auctionDate;
    }

    /**
     * @return the firNo
     */
    public String getFirNo() {
        return firNo;
    }

    /**
     * @param firNo the firNo to set
     */
    public void setFirNo(String firNo) {
        this.firNo = firNo;
    }

    /**
     * @return the firDate
     */
    public Date getFirDate() {
        return firDate;
    }

    /**
     * @param firDate the firDate to set
     */
    public void setFirDate(Date firDate) {
        this.firDate = firDate;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the purchaseDate
     */
    public Date getPurchaseDate() {
        return purchaseDate;
    }

    /**
     * @param purchaseDate the purchaseDate to set
     */
    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    /**
     * @return the orderNo
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * @param orderNo the orderNo to set
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * @return the allowToMoveBlackListedDataToHistory
     */
    public boolean isAllowToMoveBlackListedDataToHistory() {
        return allowToMoveBlackListedDataToHistory;
    }

    /**
     * @param allowToMoveBlackListedDataToHistory the
     * allowToMoveBlackListedDataToHistory to set
     */
    public void setAllowToMoveBlackListedDataToHistory(boolean allowToMoveBlackListedDataToHistory) {
        this.allowToMoveBlackListedDataToHistory = allowToMoveBlackListedDataToHistory;
    }

    /**
     * @return the regnDt
     */
    public Date getRegnDt() {
        return regnDt;
    }

    /**
     * @param regnDt the regnDt to set
     */
    public void setRegnDt(Date regnDt) {
        this.regnDt = regnDt;
    }

    /**
     * @return the auctionBy
     */
    public String getAuctionBy() {
        return auctionBy;
    }

    /**
     * @param auctionBy the auctionBy to set
     */
    public void setAuctionBy(String auctionBy) {
        this.auctionBy = auctionBy;
    }

}
