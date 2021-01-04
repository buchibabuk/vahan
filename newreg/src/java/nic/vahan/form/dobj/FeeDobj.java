/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author tranC094
 */
public class FeeDobj implements Serializable {

    private Integer purCd = -1;
    private String feeHeadDescr;
    private Long feeAmount;
    private Long fineAmount;
    private Long totalAmount;
    private boolean disableDropDown;
    private String paymentId;
    private String noOfApplications;
    private boolean perRcpt;
    private boolean perTrans;
    private boolean readOnlyFee = true;
    private Date fromDate;
    private Date uptoDate;
    private String fromDateLable;
    private String uptoDateLable;
    private boolean renderFromDate;
    private boolean renderUptoDate;
    private boolean readOnlyFine = false;
    private Date dueDate;
    private String dueDateString;
    private FeeFineExemptionDobj feeFineExemDobj = null;

    public FeeDobj() {
    }

    public FeeDobj(Integer purCd) {
        this.purCd = purCd;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.purCd);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FeeDobj other = (FeeDobj) obj;

        if (this.purCd.intValue() == other.purCd.intValue()) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public String toString() {
        return "Fee_Dobj{" + "purCd=" + purCd + ", feeHeadDescr=" + feeHeadDescr + ", feeAmount=" + feeAmount + ", fineAmount=" + fineAmount + ", totalAmount=" + totalAmount + '}';
    }

    public Integer getPurCd() {
        return purCd;
    }

    public void setPurCd(Integer purCd) {
        this.purCd = purCd;
    }

    public String getFeeHeadDescr() {
        return feeHeadDescr;
    }

    public void setFeeHeadDescr(String feeHeadDescr) {
        this.feeHeadDescr = feeHeadDescr;
    }

    public Long getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(Long feeAmount) {
        this.feeAmount = feeAmount;
    }

    public Long getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(Long fineAmount) {
        this.fineAmount = fineAmount;
    }

    public Long getTotalAmount() {
        if (feeAmount == null) {
            feeAmount = 0l;
        }

        if (fineAmount == null) {
            fineAmount = 0l;
        }
        totalAmount = feeAmount + fineAmount;
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * @return the disableDropDown
     */
    public boolean isDisableDropDown() {
        return disableDropDown;
    }

    /**
     * @param disableDropDown the disableDropDown to set
     */
    public void setDisableDropDown(boolean disableDropDown) {
        this.disableDropDown = disableDropDown;
    }

    /**
     * @return the paymentId
     */
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * @param paymentId the paymentId to set
     */
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    /**
     * @return the noOfApplications
     */
    public String getNoOfApplications() {
        return noOfApplications;
    }

    /**
     * @param noOfApplications the noOfApplications to set
     */
    public void setNoOfApplications(String noOfApplications) {
        this.noOfApplications = noOfApplications;
    }

    /**
     * @return the perRcpt
     */
    public boolean getPerRcpt() {
        return perRcpt;
    }

    /**
     * @param perRcpt the perRcpt to set
     */
    public void setPerRcpt(boolean perRcpt) {
        this.perRcpt = perRcpt;
    }

    /**
     * @return the perTrans
     */
    public boolean getPerTrans() {
        return perTrans;
    }

    /**
     * @param perTrans the perTrans to set
     */
    public void setPerTrans(boolean perTrans) {
        this.perTrans = perTrans;
    }

    /**
     * @return the readOnlyFee
     */
    public boolean isReadOnlyFee() {
        return readOnlyFee;
    }

    /**
     * @param readOnlyFee the readOnlyFee to set
     */
    public void setReadOnlyFee(boolean readOnlyFee) {
        this.readOnlyFee = readOnlyFee;
    }

    /**
     * @return the fromDate
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * @param fromDate the fromDate to set
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * @return the uptoDate
     */
    public Date getUptoDate() {
        return uptoDate;
    }

    /**
     * @param uptoDate the uptoDate to set
     */
    public void setUptoDate(Date uptoDate) {
        this.uptoDate = uptoDate;
    }

    /**
     * @return the renderFromDate
     */
    public boolean isRenderFromDate() {
        return renderFromDate;
    }

    /**
     * @param renderFromDate the renderFromDate to set
     */
    public void setRenderFromDate(boolean renderFromDate) {
        this.renderFromDate = renderFromDate;
    }

    /**
     * @return the renderUptoDate
     */
    public boolean isRenderUptoDate() {
        return renderUptoDate;
    }

    /**
     * @param renderUptoDate the renderUptoDate to set
     */
    public void setRenderUptoDate(boolean renderUptoDate) {
        this.renderUptoDate = renderUptoDate;
    }

    /**
     * @return the fromDateLable
     */
    public String getFromDateLable() {
        if (fromDate != null) {
            return ServerUtil.parseDateToString(fromDate);
        }
        return fromDateLable;
    }

    /**
     * @param fromDateLable the fromDateLable to set
     */
    public void setFromDateLable(String fromDateLable) {

        this.fromDateLable = fromDateLable;
    }

    /**
     * @return the uptoDateLable
     */
    public String getUptoDateLable() {
        if (uptoDate != null) {
            return ServerUtil.parseDateToString(uptoDate);
        }
        return uptoDateLable;
    }

    /**
     * @param uptoDateLable the uptoDateLable to set
     */
    public void setUptoDateLable(String uptoDateLable) {
        this.uptoDateLable = uptoDateLable;
    }

    /**
     * @return the readOnlyFine
     */
    public boolean isReadOnlyFine() {
        return readOnlyFine;
    }

    /**
     * @param readOnlyFine the readOnlyFine to set
     */
    public void setReadOnlyFine(boolean readOnlyFine) {
        this.readOnlyFine = readOnlyFine;
    }

    /**
     * @return the dueDateString
     */
    public String getDueDateString() {
        return dueDateString;
    }

    /**
     * @param dueDateString the dueDateString to set
     */
    public void setDueDateString(String dueDateString) {
        this.dueDateString = dueDateString;
    }

    /**
     * @return the dueDate
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * @param dueDate the dueDate to set
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * @return the feeFineExemDobj
     */
    public FeeFineExemptionDobj getFeeFineExemDobj() {
        return feeFineExemDobj;
    }

    /**
     * @param feeFineExemDobj the feeFineExemDobj to set
     */
    public void setFeeFineExemDobj(FeeFineExemptionDobj feeFineExemDobj) {
        this.feeFineExemDobj = feeFineExemDobj;
    }
}
