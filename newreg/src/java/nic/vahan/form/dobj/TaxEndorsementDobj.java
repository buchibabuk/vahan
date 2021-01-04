/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author nicsi
 */
public class TaxEndorsementDobj implements Serializable, Cloneable {
    //No of Quarter

    private Date endorsFromDate = new Date();
    private Date endorsUpto;
    private Date withEffectDate;
    private String modMnulAuto;
    private long NoofQuarter;
    private long taxRate;
    private String remark;
    private String stateCd;
    private int offCd;
    private String empCode;
    private String regnNo;
    private int serial_no;
    private String applNo;

    public Date getEndorsFromDate() {
        return endorsFromDate;
    }

    public void setEndorsFromDate(Date endorsFromDate) {
        this.endorsFromDate = endorsFromDate;
    }

    public Date getEndorsUpto() {
        return endorsUpto;
    }

    public void setEndorsUpto(Date endorsUpto) {
        this.endorsUpto = endorsUpto;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public Date getWithEffectDate() {
        return withEffectDate;
    }

    public void setWithEffectDate(Date withEffectDate) {
        this.withEffectDate = withEffectDate;
    }

    public String getModMnulAuto() {
        return modMnulAuto;
    }

    public void setModMnulAuto(String modMnulAuto) {
        this.modMnulAuto = modMnulAuto;
    }

    public long getNoofQuarter() {
        return NoofQuarter;
    }

    public void setNoofQuarter(long NoofQuarter) {
        this.NoofQuarter = NoofQuarter;
    }

    public long getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(long taxRate) {
        this.taxRate = taxRate;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(int serial_no) {
        this.serial_no = serial_no;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }
}
