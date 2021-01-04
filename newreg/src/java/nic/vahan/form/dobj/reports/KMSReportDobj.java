/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author tranC114
 */
public class KMSReportDobj implements Serializable {

    private String serialNo;
    private String vehicleNo;
    private String receiptDate;
    private String receiptNo;
    private String purpose;
    private String approvalDate;
    private String kmsDate;
    private String stateName;
    private String toDate;
    private String frmDate;
    private String offName;
    private String opDate;
    private String applNo;
    private Date uptoDate;
    private Date startDate;
    private int year;
    private int month;
    private Map<String, Integer> monthList;
    private List<KMSReportDobj> returnlist = new ArrayList<KMSReportDobj>();
    private String printedDate;
    private String rccardchipno;
    private String drtoNameFirst;
    private String drtoNameSecond;

    /**
     * @return the serialNo
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * @param serialNo the serialNo to set
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
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
     * @return the purpose
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * @param purpose the purpose to set
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * @return the approvalDate
     */
    public String getApprovalDate() {
        return approvalDate;
    }

    /**
     * @param approvalDate the approvalDate to set
     */
    public void setApprovalDate(String approvalDate) {
        this.approvalDate = approvalDate;
    }

    /**
     * @return the kmsDate
     */
    public String getKmsDate() {
        return kmsDate;
    }

    /**
     * @param kmsDate the kmsDate to set
     */
    public void setKmsDate(String kmsDate) {
        this.kmsDate = kmsDate;
    }

    /**
     * @return the stateName
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * @param stateName the stateName to set
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    /**
     * @return the toDate
     */
    public String getToDate() {
        return toDate;
    }

    /**
     * @param toDate the toDate to set
     */
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    /**
     * @return the frmDate
     */
    public String getFrmDate() {
        return frmDate;
    }

    /**
     * @param frmDate the frmDate to set
     */
    public void setFrmDate(String frmDate) {
        this.frmDate = frmDate;
    }

    /**
     * @return the offName
     */
    public String getOffName() {
        return offName;
    }

    /**
     * @param offName the offName to set
     */
    public void setOffName(String offName) {
        this.offName = offName;
    }

    /**
     * @return the returnlist
     */
    public List<KMSReportDobj> getReturnlist() {
        return returnlist;
    }

    /**
     * @param returnlist the returnlist to set
     */
    public void setReturnlist(List<KMSReportDobj> returnlist) {
        this.returnlist = returnlist;
    }

    public String getOpDate() {
        return opDate;
    }

    public void setOpDate(String opDate) {
        this.opDate = opDate;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public Date getUptoDate() {
        return uptoDate;
    }

    public void setUptoDate(Date uptoDate) {
        this.uptoDate = uptoDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Map<String, Integer> getMonthList() {
        return monthList;
    }

    public void setMonthList(Map<String, Integer> monthList) {
        this.monthList = monthList;
    }

    public String getPrintedDate() {
        return printedDate;
    }

    public void setPrintedDate(String printedDate) {
        this.printedDate = printedDate;
    }

    public String getRccardchipno() {
        return rccardchipno;
    }

    public void setRccardchipno(String rccardchipno) {
        this.rccardchipno = rccardchipno;
    }

    public String getDrtoNameFirst() {
        return drtoNameFirst;
    }

    public void setDrtoNameFirst(String drtoNameFirst) {
        this.drtoNameFirst = drtoNameFirst;
    }

    public String getDrtoNameSecond() {
        return drtoNameSecond;
    }

    public void setDrtoNameSecond(String drtoNameSecond) {
        this.drtoNameSecond = drtoNameSecond;
    }
}
