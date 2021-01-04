/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class PermitCheckDetailsDobj implements Serializable {

    //TAX INFORMATION
    private String taxRcptNo;
    private String taxFrom;
    private String taxUpto;
    private String taxPurCd;
    private String taxCollectedBy;
    private String taxMode;
//FITNESS INFORMATION
    private String fitChkDt;
    private String fitValidTo;
    private String fitOffCd1;
    private String fitOffCd2;
    private String fitRemark;
//CAHLLAN INFORMATION
    private String chalNo;
    private String chalDate;
    private String chalTime;
    private String chalPlace;
    private String chalOfficer;
    private boolean chalPending;
    private boolean chalPendingInService;
//insurance INFORMATION
    private String insType;
    private int insTypeCode;
    private String insFrom;
    private String insUpto;
    private String insPolicyNo;
    private String insCmpyNo;
    private int insCmpyNoCode;
    private String insIdv;
    private boolean insValid;
    private boolean insSaveData;
    private boolean exemptedTax;
    private boolean exemptedIns;
    private boolean exemptedFitness;
    private boolean exemptedChallan;
//Vehicle Black Listed
    private boolean vehicleIsBlackListed;
    private String puccFrom;
    private String puccUpto;
    private String puccCentreno;
    private String puccNo;
//GP TAX INFORMATION
    private String gptaxRcptNo;
    private String gptaxFrom;
    private String gptaxUpto;
    private String gptaxPurCd;
    private String gptaxMode;

    public String getTaxRcptNo() {
        return taxRcptNo;
    }

    public void setTaxRcptNo(String taxRcptNo) {
        this.taxRcptNo = taxRcptNo;
    }

    public String getTaxFrom() {
        return taxFrom;
    }

    public void setTaxFrom(String taxFrom) {
        this.taxFrom = taxFrom;
    }

    public String getTaxUpto() {
        return taxUpto;
    }

    public void setTaxUpto(String taxUpto) {
        this.taxUpto = taxUpto;
    }

    public String getTaxPurCd() {
        return taxPurCd;
    }

    public void setTaxPurCd(String taxPurCd) {
        this.taxPurCd = taxPurCd;
    }

    public String getTaxCollectedBy() {
        return taxCollectedBy;
    }

    public void setTaxCollectedBy(String taxCollectedBy) {
        this.taxCollectedBy = taxCollectedBy;
    }

    public String getFitChkDt() {
        return fitChkDt;
    }

    public void setFitChkDt(String fitChkDt) {
        this.fitChkDt = fitChkDt;
    }

    public String getFitValidTo() {
        return fitValidTo;
    }

    public void setFitValidTo(String fitValidTo) {
        this.fitValidTo = fitValidTo;
    }

    public String getFitOffCd1() {
        return fitOffCd1;
    }

    public void setFitOffCd1(String fitOffCd1) {
        this.fitOffCd1 = fitOffCd1;
    }

    public String getFitOffCd2() {
        return fitOffCd2;
    }

    public void setFitOffCd2(String fitOffCd2) {
        this.fitOffCd2 = fitOffCd2;
    }

    public String getFitRemark() {
        return fitRemark;
    }

    public void setFitRemark(String fitRemark) {
        this.fitRemark = fitRemark;
    }

    public String getChalNo() {
        return chalNo;
    }

    public void setChalNo(String chalNo) {
        this.chalNo = chalNo;
    }

    public String getChalDate() {
        return chalDate;
    }

    public void setChalDate(String chalDate) {
        this.chalDate = chalDate;
    }

    public String getChalTime() {
        return chalTime;
    }

    public void setChalTime(String chalTime) {
        this.chalTime = chalTime;
    }

    public String getChalPlace() {
        return chalPlace;
    }

    public void setChalPlace(String chalPlace) {
        this.chalPlace = chalPlace;
    }

    public String getChalOfficer() {
        return chalOfficer;
    }

    public void setChalOfficer(String chalOfficer) {
        this.chalOfficer = chalOfficer;
    }

    public String getInsType() {
        return insType;
    }

    public void setInsType(String insType) {
        this.insType = insType;
    }

    public String getInsFrom() {
        return insFrom;
    }

    public void setInsFrom(String insFrom) {
        this.insFrom = insFrom;
    }

    public String getInsUpto() {
        return insUpto;
    }

    public void setInsUpto(String insUpto) {
        this.insUpto = insUpto;
    }

    public String getInsPolicyNo() {
        return insPolicyNo;
    }

    public void setInsPolicyNo(String insPolicyNo) {
        this.insPolicyNo = insPolicyNo;
    }

    public String getInsCmpyNo() {
        return insCmpyNo;
    }

    public void setInsCmpyNo(String insCmpyNo) {
        this.insCmpyNo = insCmpyNo;
    }

    public boolean isInsValid() {
        return insValid;
    }

    public void setInsValid(boolean insValid) {
        this.insValid = insValid;
    }

    public boolean isChalPending() {
        return chalPending;
    }

    public void setChalPending(boolean chalPending) {
        this.chalPending = chalPending;
    }

    public boolean isInsSaveData() {
        return insSaveData;
    }

    public void setInsSaveData(boolean insSaveData) {
        this.insSaveData = insSaveData;
    }

    public String getTaxMode() {
        return taxMode;
    }

    public void setTaxMode(String taxMode) {
        this.taxMode = taxMode;
    }

    public String getInsIdv() {
        return insIdv;
    }

    public void setInsIdv(String insIdv) {
        this.insIdv = insIdv;
    }

    public boolean isVehicleIsBlackListed() {
        return vehicleIsBlackListed;
    }

    public void setVehicleIsBlackListed(boolean vehicleIsBlackListed) {
        this.vehicleIsBlackListed = vehicleIsBlackListed;
    }

    /**
     * @return the exemptedInsTax
     */
    public boolean isExemptedTax() {
        return exemptedTax;
    }

    /**
     * @param exemptedInsTax the exemptedInsTax to set
     */
    public void setExemptedTax(boolean exemptedTax) {
        this.exemptedTax = exemptedTax;
    }

    /**
     * @return the exemptedIns
     */
    public boolean isExemptedIns() {
        return exemptedIns;
    }

    /**
     * @param exemptedIns the exemptedIns to set
     */
    public void setExemptedIns(boolean exemptedIns) {
        this.exemptedIns = exemptedIns;
    }

    public boolean isExemptedFitness() {
        return exemptedFitness;
    }

    public void setExemptedFitness(boolean exemptedFitness) {
        this.exemptedFitness = exemptedFitness;
    }

    /**
     * @return the exemptedChallan
     */
    public boolean isExemptedChallan() {
        return exemptedChallan;
    }

    /**
     * @param exemptedChallan the exemptedChallan to set
     */
    public void setExemptedChallan(boolean exemptedChallan) {
        this.exemptedChallan = exemptedChallan;
    }

    public int getInsTypeCode() {
        return insTypeCode;
    }

    public void setInsTypeCode(int insTypeCode) {
        this.insTypeCode = insTypeCode;
    }

    public int getInsCmpyNoCode() {
        return insCmpyNoCode;
    }

    public void setInsCmpyNoCode(int insCmpyNoCode) {
        this.insCmpyNoCode = insCmpyNoCode;
    }

    public String getPuccFrom() {
        return puccFrom;
    }

    public void setPuccFrom(String puccFrom) {
        this.puccFrom = puccFrom;
    }

    public String getPuccUpto() {
        return puccUpto;
    }

    public void setPuccUpto(String puccUpto) {
        this.puccUpto = puccUpto;
    }

    public String getPuccCentreno() {
        return puccCentreno;
    }

    public void setPuccCentreno(String puccCentreno) {
        this.puccCentreno = puccCentreno;
    }

    public String getPuccNo() {
        return puccNo;
    }

    public void setPuccNo(String puccNo) {
        this.puccNo = puccNo;
    }

    public boolean isChalPendingInService() {
        return chalPendingInService;
    }

    public void setChalPendingInService(boolean chalPendingInService) {
        this.chalPendingInService = chalPendingInService;
    }

    public String getGptaxRcptNo() {
        return gptaxRcptNo;
    }

    public void setGptaxRcptNo(String gptaxRcptNo) {
        this.gptaxRcptNo = gptaxRcptNo;
    }

    public String getGptaxFrom() {
        return gptaxFrom;
    }

    public void setGptaxFrom(String gptaxFrom) {
        this.gptaxFrom = gptaxFrom;
    }

    public String getGptaxUpto() {
        return gptaxUpto;
    }

    public void setGptaxUpto(String gptaxUpto) {
        this.gptaxUpto = gptaxUpto;
    }

    public String getGptaxPurCd() {
        return gptaxPurCd;
    }

    public void setGptaxPurCd(String gptaxPurCd) {
        this.gptaxPurCd = gptaxPurCd;
    }

    public String getGptaxMode() {
        return gptaxMode;
    }

    public void setGptaxMode(String gptaxMode) {
        this.gptaxMode = gptaxMode;
    }
}
