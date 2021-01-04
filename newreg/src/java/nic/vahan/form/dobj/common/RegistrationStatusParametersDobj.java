/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.common;

import java.io.Serializable;

/**
 *
 * @author Ashok
 */
public class RegistrationStatusParametersDobj implements Serializable {

    private String regnNo;
    private boolean insuranceStatus;
    private boolean vtOwnerStatus;
    private String vtOwnerDescr;
    private String blackListDescr;
    private boolean blackListStatus;
    private boolean contactStatus;
    private boolean taxStatus;
    private boolean puccStatus;
    private boolean fitnessStatus;
    private String permitSurrenderStatus;

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
     * @return the insuranceStatus
     */
    public boolean isInsuranceStatus() {
        return insuranceStatus;
    }

    /**
     * @param insuranceStatus the insuranceStatus to set
     */
    public void setInsuranceStatus(boolean insuranceStatus) {
        this.insuranceStatus = insuranceStatus;
    }

    /**
     * @return the contactStatus
     */
    public boolean isContactStatus() {
        return contactStatus;
    }

    /**
     * @param contactStatus the contactStatus to set
     */
    public void setContactStatus(boolean contactStatus) {
        this.contactStatus = contactStatus;
    }

    /**
     * @return the taxStatus
     */
    public boolean isTaxStatus() {
        return taxStatus;
    }

    /**
     * @param taxStatus the taxStatus to set
     */
    public void setTaxStatus(boolean taxStatus) {
        this.taxStatus = taxStatus;
    }

    /**
     * @return the puccStatus
     */
    public boolean isPuccStatus() {
        return puccStatus;
    }

    /**
     * @param puccStatus the puccStatus to set
     */
    public void setPuccStatus(boolean puccStatus) {
        this.puccStatus = puccStatus;
    }

    /**
     * @return the fitnessStatus
     */
    public boolean isFitnessStatus() {
        return fitnessStatus;
    }

    /**
     * @param fitnessStatus the fitnessStatus to set
     */
    public void setFitnessStatus(boolean fitnessStatus) {
        this.fitnessStatus = fitnessStatus;
    }

    /**
     * @return the vtOwnerStatus
     */
    public boolean isVtOwnerStatus() {
        return vtOwnerStatus;
    }

    /**
     * @param vtOwnerStatus the vtOwnerStatus to set
     */
    public void setVtOwnerStatus(boolean vtOwnerStatus) {
        this.vtOwnerStatus = vtOwnerStatus;
    }

    /**
     * @return the vtOwnerDescr
     */
    public String getVtOwnerDescr() {
        return vtOwnerDescr;
    }

    /**
     * @param vtOwnerDescr the vtOwnerDescr to set
     */
    public void setVtOwnerDescr(String vtOwnerDescr) {
        this.vtOwnerDescr = vtOwnerDescr;
    }

    /**
     * @return the permitSurrenderStatus
     */
    public String getPermitSurrenderStatus() {
        return permitSurrenderStatus;
    }

    /**
     * @param permitSurrenderStatus the permitSurrenderStatus to set
     */
    public void setPermitSurrenderStatus(String permitSurrenderStatus) {
        this.permitSurrenderStatus = permitSurrenderStatus;
    }

    public String getBlackListDescr() {
        return blackListDescr;
    }

    public void setBlackListDescr(String blackListDescr) {
        this.blackListDescr = blackListDescr;
    }

    public boolean isBlackListStatus() {
        return blackListStatus;
    }

    public void setBlackListStatus(boolean blackListStatus) {
        this.blackListStatus = blackListStatus;
    }
}
