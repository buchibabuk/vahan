/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.util.Date;

/**
 *
 * @author acer
 */
public class RenewalDobj implements Cloneable {

    private String applNo;
    private String regnNo;
    private Date oldFitDt;
    private Date newFitDt;
    private String inspectedBy;
    private Date inspectedDt;
    private String opDt;
    private String stateCd;
    private int offCd;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the applNo
     */
    public String getApplNo() {
        return applNo;
    }

    /**
     * @param applNo the applNo to set
     */
    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

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
     * @return the oldFitDt
     */
    public Date getOldFitDt() {
        return oldFitDt;
    }

    /**
     * @param oldFitDt the oldFitDt to set
     */
    public void setOldFitDt(Date oldFitDt) {
        this.oldFitDt = oldFitDt;
    }

    /**
     * @return the newFitDt
     */
    public Date getNewFitDt() {
        return newFitDt;
    }

    /**
     * @param newFitDt the newFitDt to set
     */
    public void setNewFitDt(Date newFitDt) {
        this.newFitDt = newFitDt;
    }

    /**
     * @return the inspectedBy
     */
    public String getInspectedBy() {
        return inspectedBy;
    }

    /**
     * @param inspectedBy the inspectedBy to set
     */
    public void setInspectedBy(String inspectedBy) {
        this.inspectedBy = inspectedBy;
    }

    /**
     * @return the inspectedDt
     */
    public Date getInspectedDt() {
        return inspectedDt;
    }

    /**
     * @param inspectedDt the inspectedDt to set
     */
    public void setInspectedDt(Date inspectedDt) {
        this.inspectedDt = inspectedDt;
    }

    /**
     * @return the opDt
     */
    public String getOpDt() {
        return opDt;
    }

    /**
     * @param opDt the opDt to set
     */
    public void setOpDt(String opDt) {
        this.opDt = opDt;
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
}
