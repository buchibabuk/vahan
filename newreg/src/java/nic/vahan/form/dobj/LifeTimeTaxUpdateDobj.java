/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

/**
 *
 * @author nicsi
 */
public class LifeTimeTaxUpdateDobj {

    private String empCode;
    private String stateCd;
    private String remark;
    private int offcd;
    private long taxAmt;
    private String regnNo;
    private String applNo;

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

    public long getTaxAmt() {
        return taxAmt;
    }

    public void setTaxAmt(long taxAmt) {
        this.taxAmt = taxAmt;
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
}
