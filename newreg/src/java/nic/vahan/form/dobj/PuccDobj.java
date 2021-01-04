/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author tranC103
 */
public class PuccDobj implements Serializable {

    private String regnNo;
    private Date puccFrom;
    private Date puccUpto;
    private String puccCentreno;
    private String puccNo;
    private String state_cd;
    private int off_cd;

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
     * @return the puccFrom
     */
    public Date getPuccFrom() {
        return puccFrom;
    }

    /**
     * @param puccFrom the puccFrom to set
     */
    public void setPuccFrom(Date puccFrom) {
        this.puccFrom = puccFrom;
    }

    /**
     * @return the puccUpto
     */
    public Date getPuccUpto() {
        return puccUpto;
    }

    /**
     * @param puccUpto the puccUpto to set
     */
    public void setPuccUpto(Date puccUpto) {
        this.puccUpto = puccUpto;
    }

    /**
     * @return the puccCentreno
     */
    public String getPuccCentreno() {
        return puccCentreno;
    }

    /**
     * @param puccCentreno the puccCentreno to set
     */
    public void setPuccCentreno(String puccCentreno) {
        this.puccCentreno = puccCentreno;
    }

    /**
     * @return the puccNo
     */
    public String getPuccNo() {
        return puccNo;
    }

    /**
     * @param puccNo the puccNo to set
     */
    public void setPuccNo(String puccNo) {
        this.puccNo = puccNo;
    }

    /**
     * @return the state_cd
     */
    public String getState_cd() {
        return state_cd;
    }

    /**
     * @param state_cd the state_cd to set
     */
    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    /**
     * @return the off_cd
     */
    public int getOff_cd() {
        return off_cd;
    }

    /**
     * @param off_cd the off_cd to set
     */
    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }
}
