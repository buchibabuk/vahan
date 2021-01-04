/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;

/**
 *
 * @author tranC107
 */
public class CollectionSummaryDobj implements Serializable {

    private String collectedBy;
    private String cancelBy;
    private String instrumentType;
    private int noOfRcpt;
    private long amount;
    private long grandTotal;
    private int totalNoOfReceipt;

    /**
     * @return the collectedBy
     */
    public String getCollectedBy() {
        return collectedBy;
    }

    /**
     * @param collectedBy the collectedBy to set
     */
    public void setCollectedBy(String collectedBy) {
        this.collectedBy = collectedBy;
    }

    /**
     * @return the instrumentType
     */
    public String getInstrumentType() {
        return instrumentType;
    }

    /**
     * @param instrumentType the instrumentType to set
     */
    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }

    /**
     * @return the noOfRcpt
     */
    public int getNoOfRcpt() {
        return noOfRcpt;
    }

    /**
     * @param noOfRcpt the noOfRcpt to set
     */
    public void setNoOfRcpt(int noOfRcpt) {
        this.noOfRcpt = noOfRcpt;
    }

    /**
     * @return the amount
     */
    public long getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(long amount) {
        this.amount = amount;
    }

    /**
     * @return the cancelBy
     */
    public String getCancelBy() {
        return cancelBy;
    }

    /**
     * @param cancelBy the cancelBy to set
     */
    public void setCancelBy(String cancelBy) {
        this.cancelBy = cancelBy;
    }

    /**
     * @return the grandTotal
     */
    public long getGrandTotal() {
        return grandTotal;
    }

    /**
     * @param grandTotal the grandTotal to set
     */
    public void setGrandTotal(long grandTotal) {
        this.grandTotal = grandTotal;
    }

    /**
     * @return the totalNoOfReceipt
     */
    public int getTotalNoOfReceipt() {
        return totalNoOfReceipt;
    }

    /**
     * @param totalNoOfReceipt the totalNoOfReceipt to set
     */
    public void setTotalNoOfReceipt(int totalNoOfReceipt) {
        this.totalNoOfReceipt = totalNoOfReceipt;
    }
}
