/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.admin;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author nicsi
 */
public class VehicleOfficeChangeDobj implements Serializable {

    int newOffCd;
    String chassiNo;
    String regnNo;
    String remark;
    String requestedBy;
    Date reguestedDate;

    public int getNewOffCd() {
        return newOffCd;
    }

    public void setNewOffCd(int newOffCd) {
        this.newOffCd = newOffCd;
    }

    public String getChassiNo() {
        return chassiNo;
    }

    public void setChassiNo(String chassiNo) {
        this.chassiNo = chassiNo;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Date getReguestedDate() {
        return reguestedDate;
    }

    public void setReguestedDate(Date reguestedDate) {
        this.reguestedDate = reguestedDate;
    }

}
