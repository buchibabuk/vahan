/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author nicsi
 */
public class CompoundingFeeDobj implements Serializable {

    private String accuseType;
    private String compoundingFee;
    private String balanceFee;
    private String advanceCompFee;
    private String recieptNo;
    private String recieptDate;
    private String fromDt;
    private String uptoDt;
    private String amount;
    private String feetaxDesc;
    private boolean select;

    public CompoundingFeeDobj(String accuseType, String compoundingFee, String balanceFee, String advanceCompFee, String recieptNo, String recieptDate) {
        this.accuseType = accuseType;
        this.compoundingFee = compoundingFee;
        this.balanceFee = balanceFee;
        this.advanceCompFee = advanceCompFee;
        this.recieptNo = recieptNo;
        this.recieptDate = recieptDate;

    }

    public CompoundingFeeDobj() {
    }

    @Override
    public String toString() {
        return "DOCompoundingFee{" + "accuseType=" + accuseType + ", compoundingFee=" + compoundingFee + ", balanceFee=" + balanceFee + ", advanceCompFee=" + advanceCompFee + ", recieptNo=" + recieptNo + ", recieptDate=" + recieptDate + ", fromDt=" + fromDt + ", uptoDt=" + uptoDt + ", amount=" + amount + ", feetaxDesc=" + feetaxDesc + ", select=" + select + '}';
    }

    public String getAccuseType() {
        return accuseType;
    }

    public void setAccuseType(String accuseType) {
        this.accuseType = accuseType;
    }

    public String getRecieptNo() {
        return recieptNo;
    }

    public void setRecieptNo(String recieptNo) {
        this.recieptNo = recieptNo;
    }

    public String getRecieptDate() {
        return recieptDate;
    }

    public void setRecieptDate(String recieptDate) {
        this.recieptDate = recieptDate;
    }

    /**
     * @return the compoundingFee
     */
    public String getCompoundingFee() {
        return compoundingFee;
    }

    /**
     * @param compoundingFee the compoundingFee to set
     */
    public void setCompoundingFee(String compoundingFee) {
        this.compoundingFee = compoundingFee;
    }

    /**
     * @return the balanceFee
     */
    public String getBalanceFee() {
        return balanceFee;
    }

    /**
     * @param balanceFee the balanceFee to set
     */
    public void setBalanceFee(String balanceFee) {
        this.balanceFee = balanceFee;
    }

    /**
     * @return the advanceCompFee
     */
    public String getAdvanceCompFee() {
        return advanceCompFee;
    }

    /**
     * @param advanceCompFee the advanceCompFee to set
     */
    public void setAdvanceCompFee(String advanceCompFee) {
        this.advanceCompFee = advanceCompFee;
    }

    /**
     * @return the fromDt
     */
    public String getFromDt() {
        return fromDt;
    }

    /**
     * @param fromDt the fromDt to set
     */
    public void setFromDt(String fromDt) {
        this.fromDt = fromDt;
    }

    /**
     * @return the uptoDt
     */
    public String getUptoDt() {
        return uptoDt;
    }

    /**
     * @param uptoDt the uptoDt to set
     */
    public void setUptoDt(String uptoDt) {
        this.uptoDt = uptoDt;
    }

    /**
     * @return the amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * @return the feetaxDesc
     */
    public String getFeetaxDesc() {
        return feetaxDesc;
    }

    /**
     * @param feetaxDesc the feetaxDesc to set
     */
    public void setFeetaxDesc(String feetaxDesc) {
        this.feetaxDesc = feetaxDesc;
    }

    /**
     * @return the select
     */
    public boolean isSelect() {
        return select;
    }

    /**
     * @param select the select to set
     */
    public void setSelect(boolean select) {
        this.select = select;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CompoundingFeeDobj other = (CompoundingFeeDobj) obj;
        if ((this.accuseType == null) ? (other.accuseType != null) : !this.accuseType.equals(other.accuseType)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.accuseType);
        return hash;
    }
}
