/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.dobj;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

/**
 *
 * @author tranC103
 */
public class DealerMasterDobj implements Serializable {

    private String dealerCode = "";
    private String stateCode;
    private int offCode;
    private String dealerName;
    private String dealerRegnNo;
    private String dealerAdd1;
    private String dealerAdd2;
    private int dealerDistrict;
    private Integer dealerPincode;
    private Date dealerValidUpto;
    private String enteredBy;
    private String enteredOn;
    private String userCatg;
    private String dealerStateCode;
    private int dealerOffCode;
    private String tin_NO;
    private TreeMap<String, String> dtMap = new TreeMap<>();
    private String contactNo;
    private String emailId;
    private String maker;
    private String vchClass;
    private String applicantRelation;
    private boolean individual;
    private String officeName;
    private String stateName;
    private boolean registrationMarkAuth;

    public TreeMap<String, String> getDtMap() {
        return dtMap;
    }

    public void setDtMap(TreeMap<String, String> dtMap) {
        this.dtMap = dtMap;
    }

    public String getUserCatg() {
        return userCatg;
    }

    public void setUserCatg(String userCatg) {
        this.userCatg = userCatg;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public int getDealerOffCode() {
        return dealerOffCode;
    }

    public void setDealerOffCode(int dealerOffCode) {
        this.dealerOffCode = dealerOffCode;
    }

    public String getDealerCode() {
        return dealerCode;
    }

    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }

    public int getOffCode() {
        return offCode;
    }

    public void setOffCode(int offCode) {
        this.offCode = offCode;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getDealerRegnNo() {
        return dealerRegnNo;
    }

    public void setDealerRegnNo(String dealerRegnNo) {
        this.dealerRegnNo = dealerRegnNo;
    }

    public String getDealerStateCode() {
        return dealerStateCode;
    }

    public void setDealerStateCode(String dealerStateCode) {
        this.dealerStateCode = dealerStateCode;
    }

    /**
     * @return the dealerAdd1
     */
    public String getDealerAdd1() {
        return dealerAdd1;
    }

    /**
     * @param dealerAdd1 the dealerAdd1 to set
     */
    public void setDealerAdd1(String dealerAdd1) {
        this.dealerAdd1 = dealerAdd1;
    }

    /**
     * @return the dealerAdd2
     */
    public String getDealerAdd2() {
        return dealerAdd2;
    }

    /**
     * @param dealerAdd2 the dealerAdd2 to set
     */
    public void setDealerAdd2(String dealerAdd2) {
        this.dealerAdd2 = dealerAdd2;
    }

    /**
     * @return the dealerDistrict
     */
    public int getDealerDistrict() {
        return dealerDistrict;
    }

    /**
     * @param dealerDistrict the dealerDistrict to set
     */
    public void setDealerDistrict(int dealerDistrict) {
        this.dealerDistrict = dealerDistrict;
    }

    /**
     * @return the dealerPincode
     */
    public Integer getDealerPincode() {
        return dealerPincode;
    }

    /**
     * @param dealerPincode the dealerPincode to set
     */
    public void setDealerPincode(Integer dealerPincode) {
        this.dealerPincode = dealerPincode;
    }

    /**
     * @return the dealerValidUpto
     */
    public Date getDealerValidUpto() {
        return dealerValidUpto;
    }

    /**
     * @param dealerValidUpto the dealerValidUpto to set
     */
    public void setDealerValidUpto(Date dealerValidUpto) {
        this.dealerValidUpto = dealerValidUpto;
    }

    /**
     * @return the enteredBy
     */
    public String getEnteredBy() {
        return enteredBy;
    }

    /**
     * @param enteredBy the enteredBy to set
     */
    public void setEnteredBy(String enteredBy) {
        this.enteredBy = enteredBy;
    }

    /**
     * @return the enteredOn
     */
    public String getEnteredOn() {
        return enteredOn;
    }

    /**
     * @param enteredOn the enteredOn to set
     */
    public void setEnteredOn(String enteredOn) {
        this.enteredOn = enteredOn;
    }

    public String getTin_NO() {
        return tin_NO;
    }

    public void setTin_NO(String tin_NO) {
        this.tin_NO = tin_NO;
    }

    /**
     * @return the registrationMarkAuth
     */
    public boolean isRegistrationMarkAuth() {
        return registrationMarkAuth;
    }

    /**
     * @param registrationMarkAuth the registrationMarkAuth to set
     */
    public void setRegistrationMarkAuth(boolean registrationMarkAuth) {
        this.registrationMarkAuth = registrationMarkAuth;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getVchClass() {
        return vchClass;
    }

    public void setVchClass(String vchClass) {
        this.vchClass = vchClass;
    }

    public String getApplicantRelation() {
        return applicantRelation;
    }

    public void setApplicantRelation(String applicantRelation) {
        this.applicantRelation = applicantRelation;
    }

    public boolean isIndividual() {
        return individual;
    }

    public void setIndividual(boolean individual) {
        this.individual = individual;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
