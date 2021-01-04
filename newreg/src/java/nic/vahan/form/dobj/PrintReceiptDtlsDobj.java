/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.util.List;
import nic.vahan.form.bean.PrintRcptParticularDtls;

/**
 *
 * @author nic
 */
public class PrintReceiptDtlsDobj {

    private String stateName;
    private String rtoOff;
    private String receiptNo;
    private String ownerName;
    private String regnNo;
    private String vhClass;
    private String receiptDate;
    private String chasino;
    private String vhCatg;
    private String srNo;
    private String rcptNo;
    private int grandTotal;
    private String applNo;
    private List<PrintRcptParticularDtls> prntRecieptSubList;

    /**
     * @return the statetName
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * @param statetName the statetName to set
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    /**
     * @return the rtoOff
     */
    public String getRtoOff() {
        return rtoOff;
    }

    /**
     * @param rtoOff the rtoOff to set
     */
    public void setRtoOff(String rtoOff) {
        this.rtoOff = rtoOff;
    }

    /**
     * @return the receiptNo
     */
    public String getReceiptNo() {
        return receiptNo;
    }

    /**
     * @param receiptNo the receiptNo to set
     */
    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
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
     * @return the cvhClass
     */
    public String getVhClass() {
        return vhClass;
    }

    /**
     * @param cvhClass the cvhClass to set
     */
    public void setVhClass(String vhClass) {
        this.vhClass = vhClass;
    }

    /**
     * @return the receiptDate
     */
    public String getReceiptDate() {
        return receiptDate;
    }

    /**
     * @param receiptDate the receiptDate to set
     */
    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    /**
     * @return the chasino
     */
    public String getChasino() {
        return chasino;
    }

    /**
     * @param chasino the chasino to set
     */
    public void setChasino(String chasino) {
        this.chasino = chasino;
    }

    public String getVhCatg() {
        return vhCatg;
    }

    public void setVhCatg(String vhCatg) {
        this.vhCatg = vhCatg;
    }

    public String getSrNo() {
        return srNo;
    }

    public void setSrNo(String srNo) {
        this.srNo = srNo;
    }

    /**
     * @return the rcptNo
     */
    public String getRcptNo() {
        return rcptNo;
    }

    /**
     * @param rcptNo the rcptNo to set
     */
    public void setRcptNo(String rcptNo) {
        this.rcptNo = rcptNo;
    }

    /**
     * @return the prntRecieptSubList
     */
    public List<PrintRcptParticularDtls> getPrntRecieptSubList() {
        return prntRecieptSubList;
    }

    /**
     * @param prntRecieptSubList the prntRecieptSubList to set
     */
    public void setPrntRecieptSubList(List<PrintRcptParticularDtls> prntRecieptSubList) {
        this.prntRecieptSubList = prntRecieptSubList;
    }

    /**
     * @return the grandTotal
     */
    public int getGrandTotal() {
        return grandTotal;
    }

    /**
     * @param grandTotal the grandTotal to set
     */
    public void setGrandTotal(int grandTotal) {
        this.grandTotal = grandTotal;
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
}
