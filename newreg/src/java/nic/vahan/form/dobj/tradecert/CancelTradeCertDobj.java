/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.tradecert;

import java.io.Serializable;
import java.util.Date;

public class CancelTradeCertDobj implements Cloneable, Serializable {

    private String purCd;
    private String empCd;
    private Integer offCd;
    private String stateCd;
    private String empName;
    private String stateName;
    private String applNo;
    private String dealerName;
    private String vehCatgFor;
    private String vehCatgName;
    private String noOfAllowedVehicles;
    private String srNo;
    private String tradeCertNo;
    private Date validUpto;
    private Date validFrom;
    private Date issueDt;
    private String dealerAddress;
    private boolean isRowRemove;
    private String reasonForCancellation;
    private String orderBy;
    private String orderNo;
    private Date orderDt;
    private String dealerCode;
    private boolean isRowAdd;
    private String tcApplNo;
    private int tcSrNo;
    private String tcAppStatus;

    public String getPurCd() {
        return purCd;
    }

    public void setPurCd(String purCd) {
        this.purCd = purCd;
    }

    public Integer getOffCd() {
        return offCd;
    }

    public void setOffCd(Integer offCd) {
        this.offCd = offCd;
    }

    public String getStateCd() {
        return stateCd;
    }

    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getVehCatgFor() {
        return vehCatgFor;
    }

    public void setVehCatgFor(String vehCatgFor) {
        this.vehCatgFor = vehCatgFor;
    }

    public String getNoOfAllowedVehicles() {
        return noOfAllowedVehicles;
    }

    public void setNoOfAllowedVehicles(String noOfAllowedVehicles) {
        this.noOfAllowedVehicles = noOfAllowedVehicles;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public String getSrNo() {
        return srNo;
    }

    public void setSrNo(String srNo) {
        this.srNo = srNo;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getTradeCertNo() {
        return tradeCertNo;
    }

    public void setTradeCertNo(String tradeCertNo) {
        this.tradeCertNo = tradeCertNo;
    }

    public Date getValidUpto() {
        return validUpto;
    }

    public void setValidUpto(Date validUpto) {
        this.validUpto = validUpto;
    }

    public Date getIssueDt() {
        return issueDt;
    }

    public void setIssueDt(Date issueDt) {
        this.issueDt = issueDt;
    }

    public String getDealerAddress() {
        return dealerAddress;
    }

    public void setDealerAddress(String dealerAddress) {
        this.dealerAddress = dealerAddress;
    }

    public boolean isIsRowRemove() {
        return isRowRemove;
    }

    public void setIsRowRemove(boolean isRowRemove) {
        this.isRowRemove = isRowRemove;
    }

    public String getReasonForCancellation() {
        return reasonForCancellation;
    }

    public void setReasonForCancellation(String reasonForCancellation) {
        this.reasonForCancellation = reasonForCancellation;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Date getOrderDt() {
        return orderDt;
    }

    public void setOrderDt(Date orderDt) {
        this.orderDt = orderDt;
    }

    public String getDealerCode() {
        return dealerCode;
    }

    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public boolean isIsRowAdd() {
        return isRowAdd;
    }

    public void setIsRowAdd(boolean isRowAdd) {
        this.isRowAdd = isRowAdd;
    }

    public String getTcApplNo() {
        return tcApplNo;
    }

    public void setTcApplNo(String tcApplNo) {
        this.tcApplNo = tcApplNo;
    }

    public int getTcSrNo() {
        return tcSrNo;
    }

    public void setTcSrNo(int tcSrNo) {
        this.tcSrNo = tcSrNo;
    }

    public String getEmpCd() {
        return empCd;
    }

    public void setEmpCd(String empCd) {
        this.empCd = empCd;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getVehCatgName() {
        return vehCatgName;
    }

    public void setVehCatgName(String vehCatgName) {
        this.vehCatgName = vehCatgName;
    }

    public String getTcAppStatus() {
        return tcAppStatus;
    }

    public void setTcAppStatus(String tcAppStatus) {
        this.tcAppStatus = tcAppStatus;
    }
}
