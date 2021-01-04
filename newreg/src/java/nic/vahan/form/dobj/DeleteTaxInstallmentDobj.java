/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.util.Date;

/**
 *
 * @author DELL
 */
public class DeleteTaxInstallmentDobj {

    private String empCode;
    private String stateCd;
    private String remark;
    private int offcd;
    private String regnNo;
    private String applNo;
    private String filerefNo = "";
    private String orderIssueBy = "";
    private String orderNo = "";
    private String orderDate = "";
    private Date requestedOn = new Date();
    private String requested_by;

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getStateCd() {
        return stateCd;
    }

    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getOffcd() {
        return offcd;
    }

    public void setOffcd(int offcd) {
        this.offcd = offcd;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public String getFilerefNo() {
        return filerefNo;
    }

    public void setFilerefNo(String filerefNo) {
        this.filerefNo = filerefNo;
    }

    public String getOrderIssueBy() {
        return orderIssueBy;
    }

    public void setOrderIssueBy(String orderIssueBy) {
        this.orderIssueBy = orderIssueBy;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public Date getRequestedOn() {
        return requestedOn;
    }

    public void setRequestedOn(Date requestedOn) {
        this.requestedOn = requestedOn;
    }

    public String getRequested_by() {
        return requested_by;
    }

    public void setRequested_by(String requested_by) {
        this.requested_by = requested_by;
    }
    
}
