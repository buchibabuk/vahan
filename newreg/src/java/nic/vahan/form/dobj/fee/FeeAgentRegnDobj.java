/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.fee;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import nic.vahan.form.dobj.FeeDobj;
import nic.vahan.form.dobj.Tax_Pay_Dobj;
import nic.vahan.form.dobj.common.Draft_dobj;

/**
 *
 * @author nicsi
 */
public class FeeAgentRegnDobj implements Serializable {

    private String purCd;
    private long empCd;
    private String ownName;
    private String fname;
    private String mobileNo;
    private int feeType;
    private long fees;
    private long fine;
    private String appl_no;
    private int c_district;
    private int c_pincode;
    private int p_pincode;
    private long balGrandTotalAmount;
    private String recpeNo;
    private int grandTotal;
    private String printedDate;
    private String grandTotalInWords;
    private String city;
    private String currAdd1;
    private String currAdd2;
    private String pcity;
    private String pcurrAdd1;
    private String pcurrAdd2;
    private int p_district;
    private Date validFrom;
    private Date validUpTo;
    private String counter;
    private String licence_No = "";
    private String stateName;
    private String offName;
    private String stateCD;
    private Date rcptDt;
    private String purpose = "";
    private String descPcode = "Issue New Agent Licence";
    private String paymentMode;
    private List<Draft_dobj> collectedDrafts;
    private List<FeeDobj> collectedFeeList;
    private List<Tax_Pay_Dobj> listTaxDobj;
    private String newRenewalAgentLicence = "New_Agent_Licence";
    private String placeOfBusiness;
    public List<Tax_Pay_Dobj> getListTaxDobj() {
        return listTaxDobj;
    }

    public void setListTaxDobj(List<Tax_Pay_Dobj> listTaxDobj) {
        this.listTaxDobj = listTaxDobj;
    }

    public List<Draft_dobj> getCollectedDrafts() {
        return collectedDrafts;
    }

    public void setCollectedDrafts(List<Draft_dobj> collectedDrafts) {
        this.collectedDrafts = collectedDrafts;
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

    public String getOwnName() {
        return ownName;
    }

    public void setOwnName(String ownName) {
        this.ownName = ownName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public long getBalGrandTotalAmount() {
        return balGrandTotalAmount;
    }

    public void setBalGrandTotalAmount(long balGrandTotalAmount) {
        this.balGrandTotalAmount = balGrandTotalAmount;
    }

    public String getRecpeNo() {
        return recpeNo;
    }

    public void setRecpeNo(String recpeNo) {
        this.recpeNo = recpeNo;
    }

    public int getFeeType() {
        return feeType;
    }

    public void setFeeType(int feeType) {
        this.feeType = feeType;
    }

    public int getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(int grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getGrandTotalInWords() {
        return grandTotalInWords;
    }

    public void setGrandTotalInWords(String grandTotalInWords) {
        this.grandTotalInWords = grandTotalInWords;
    }

    public String getPrintedDate() {
        return printedDate;
    }

    public void setPrintedDate(String printedDate) {
        this.printedDate = printedDate;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public int getC_pincode() {
        return c_pincode;
    }

    public void setC_pincode(int c_pincode) {
        this.c_pincode = c_pincode;
    }

    public String getCurrAdd1() {
        return currAdd1;
    }

    public void setCurrAdd1(String currAdd1) {
        this.currAdd1 = currAdd1;
    }

    public String getCurrAdd2() {
        return currAdd2;
    }

    public void setCurrAdd2(String currAdd2) {
        this.currAdd2 = currAdd2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getC_district() {
        return c_district;
    }

    public void setC_district(int c_district) {
        this.c_district = c_district;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidUpTo() {
        return validUpTo;
    }

    public void setValidUpTo(Date validUpTo) {
        this.validUpTo = validUpTo;
    }

    public String getLicence_No() {
        return licence_No;
    }

    public void setLicence_No(String licence_No) {
        this.licence_No = licence_No;
    }

    public String getOffName() {
        return offName;
    }

    public void setOffName(String offName) {
        this.offName = offName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getPurCd() {
        return purCd;
    }

    public void setPurCd(String purCd) {
        this.purCd = purCd;
    }

    public long getEmpCd() {
        return empCd;
    }

    public void setEmpCd(long empCd) {
        this.empCd = empCd;
    }

    public String getStateCD() {
        return stateCD;
    }

    public void setStateCD(String stateCD) {
        this.stateCD = stateCD;
    }

    public Date getRcptDt() {
        return rcptDt;
    }

    public void setRcptDt(Date rcptDt) {
        this.rcptDt = rcptDt;
    }

    public String getPcity() {
        return pcity;
    }

    public void setPcity(String pcity) {
        this.pcity = pcity;
    }

    public String getPcurrAdd1() {
        return pcurrAdd1;
    }

    public void setPcurrAdd1(String pcurrAdd1) {
        this.pcurrAdd1 = pcurrAdd1;
    }

    public String getPcurrAdd2() {
        return pcurrAdd2;
    }

    public void setPcurrAdd2(String pcurrAdd2) {
        this.pcurrAdd2 = pcurrAdd2;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getP_pincode() {
        return p_pincode;
    }

    public void setP_pincode(int p_pincode) {
        this.p_pincode = p_pincode;
    }

    public int getP_district() {
        return p_district;
    }

    public void setP_district(int p_district) {
        this.p_district = p_district;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public String getDescPcode() {
        return descPcode;
    }

    public void setDescPcode(String descPcode) {
        this.descPcode = descPcode;
    }

    public long getFees() {
        return fees;
    }

    public void setFees(long fees) {
        this.fees = fees;
    }

    public long getFine() {
        return fine;
    }

    public void setFine(long fine) {
        this.fine = fine;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getNewRenewalAgentLicence() {
        return newRenewalAgentLicence;
    }

    public void setNewRenewalAgentLicence(String newRenewalAgentLicence) {
        this.newRenewalAgentLicence = newRenewalAgentLicence;
    }

    public String getPlaceOfBusiness() {
        return placeOfBusiness;
    }

    public void setPlaceOfBusiness(String placeOfBusiness) {
        this.placeOfBusiness = placeOfBusiness;
    }
}
