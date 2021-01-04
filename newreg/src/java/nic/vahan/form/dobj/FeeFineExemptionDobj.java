/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author acer
 */
public class FeeFineExemptionDobj implements Serializable {

    private String regnNo;
    private String rcptNo;
    private String stateCd;
    private int offCd;
    private int purCd;
    private long exemFeeAmount;
    private long exemFineAmount;
    private String cancelBy;

    /**
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the rcptNo
     */
    public String getRcptNo() {
        return rcptNo;
    }

    /**
     * @param rcptNo the rcptNo to set
     */
    public void setRcptNo(String rcptNo) {
        this.rcptNo = rcptNo;
    }

    /**
     * @return the stateCd
     */
    public String getStateCd() {
        return stateCd;
    }

    /**
     * @param stateCd the stateCd to set
     */
    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    /**
     * @return the offCd
     */
    public int getOffCd() {
        return offCd;
    }

    /**
     * @param offCd the offCd to set
     */
    public void setOffCd(int offCd) {
        this.offCd = offCd;
    }

    /**
     * @return the purCd
     */
    public int getPurCd() {
        return purCd;
    }

    /**
     * @param purCd the purCd to set
     */
    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }

    /**
     * @return the exemFeeAmount
     */
    public long getExemFeeAmount() {
        return exemFeeAmount;
    }

    /**
     * @param exemFeeAmount the exemFeeAmount to set
     */
    public void setExemFeeAmount(long exemFeeAmount) {
        this.exemFeeAmount = exemFeeAmount;
    }

    /**
     * @return the exemFineAmount
     */
    public long getExemFineAmount() {
        return exemFineAmount;
    }

    /**
     * @param exemFineAmount the exemFineAmount to set
     */
    public void setExemFineAmount(long exemFineAmount) {
        this.exemFineAmount = exemFineAmount;
    }

    /**
     * @return the cancelBy
     */
    public String getCancelBy() {
        return cancelBy;
    }

    /**
     * @param cancelBy the cancelBy to set
     */
    public void setCancelBy(String cancelBy) {
        this.cancelBy = cancelBy;
    }
}
