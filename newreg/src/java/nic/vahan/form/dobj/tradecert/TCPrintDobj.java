/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.tradecert;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author nic
 */
public class TCPrintDobj implements Serializable {

    private String applNo;
    private int purCd;
    private String purCdDescr;
    private List<TCPrintDobj> list = new ArrayList();
    private int actionCd;
    private String stateCd;
    private int offCd;
    private String dealerName;
    private String vehCatgs;
    private String tcNo;
    private String printedOn;
    private String printedBy;
    private String noOfVeh;
    private String noOfVehUsed;
    private Date validUpto;
    private Date issueDate;
    private Timestamp opDt;
    private List dobjSubList;
    private String text;
    private String dealerAddress;
    private String validityPeriod;
    private String OfficeName;
    private String fuelTypeName;
    private String validUptoAsString;
    private String fees;
    private String vchClass;
    private String applicantType;
    private String showroomName;
    private String showroomAddress;
    private String applicationType;
    private String vchCatgCode;

    public String getVchCatgCode() {
        return vchCatgCode;
    }

    public void setVchCatgCode(String vchCatgCode) {
        this.vchCatgCode = vchCatgCode;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public int getPurCd() {
        return purCd;
    }

    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }

    public String getPurCdDescr() {
        return purCdDescr;
    }

    public void setPurCdDescr(String purCdDescr) {
        this.purCdDescr = purCdDescr;
    }

    public List<TCPrintDobj> getList() {
        return list;
    }

    public void setList(List<TCPrintDobj> list) {
        this.list = list;
    }

    public int getActionCd() {
        return actionCd;
    }

    public void setActionCd(int actionCd) {
        this.actionCd = actionCd;
    }

    public String getStateCd() {
        return stateCd;
    }

    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    public int getOffCd() {
        return offCd;
    }

    public void setOffCd(int offCd) {
        this.offCd = offCd;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getVehCatgs() {
        return vehCatgs;
    }

    public void setVehCatgs(String vehCatgs) {
        this.vehCatgs = vehCatgs;
    }

    public String getTcNo() {
        return tcNo;
    }

    public void setTcNo(String tcNo) {
        this.tcNo = tcNo;
    }

    public String getPrintedOn() {
        return printedOn;
    }

    public void setPrintedOn(String printedOn) {
        this.printedOn = printedOn;
    }

    public String getPrintedBy() {
        return printedBy;
    }

    public void setPrintedBy(String printedBy) {
        this.printedBy = printedBy;
    }

    public String getNoOfVeh() {
        return noOfVeh;
    }

    public void setNoOfVeh(String noOfVeh) {
        this.noOfVeh = noOfVeh;
    }

    public String getNoOfVehUsed() {
        return noOfVehUsed;
    }

    public void setNoOfVehUsed(String noOfVehUsed) {
        this.noOfVehUsed = noOfVehUsed;
    }

    public Date getValidUpto() {
        return validUpto;
    }

    public void setValidUpto(Date validUpto) {
        this.validUpto = validUpto;
    }

    public Timestamp getOpDt() {
        return opDt;
    }

    public void setOpDt(Timestamp opDt) {
        this.opDt = opDt;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public List getDobjSubList() {
        return dobjSubList;
    }

    public void setDobjSubList(List dobjSubList) {
        this.dobjSubList = dobjSubList;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDealerAddress() {
        return dealerAddress;
    }

    public void setDealerAddress(String dealerAddress) {
        this.dealerAddress = dealerAddress;
    }

    public String getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(String validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public String getOfficeName() {
        return OfficeName;
    }

    public void setOfficeName(String OfficeName) {
        this.OfficeName = OfficeName;
    }

    public String getFuelTypeName() {
        return fuelTypeName;
    }

    public void setFuelTypeName(String fuelTypeName) {
        this.fuelTypeName = fuelTypeName;
    }

    public String getValidUptoAsString() {
        return validUptoAsString;
    }

    public void setValidUptoAsString(String validUptoAsString) {
        this.validUptoAsString = validUptoAsString;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getVchClass() {
        return vchClass;
    }

    public void setVchClass(String vchClass) {
        this.vchClass = vchClass;
    }

    public String getApplicantType() {
        return applicantType;
    }

    public void setApplicantType(String applicantType) {
        this.applicantType = applicantType;
    }

    public String getShowroomName() {
        return showroomName;
    }

    public void setShowroomName(String showroomName) {
        this.showroomName = showroomName;
    }

    public String getShowroomAddress() {
        return showroomAddress;
    }

    public void setShowroomAddress(String showroomAddress) {
        this.showroomAddress = showroomAddress;
    }
}
