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
public class ReflectiveTapeDobj implements Serializable, Cloneable {

    private String applNo = null;
    private String stateCd = null;
    private int offCd = 0;
    private String regn_no = null;
    private String certificateNo = null;
    private Date fitmentDate = null;
    private String manuName = null;
    private boolean isDisable_certificateNo = false;
    private boolean isDisable_fitmentDate = false;
    private boolean isDisable_manuName = false;
    private boolean reflectiveTapeInsertUpdateFlag;

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
     * @return the regn_no
     */
    public String getRegn_no() {
        return regn_no;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    /**
     * @return the certificateNo
     */
    public String getCertificateNo() {
        return certificateNo;
    }

    /**
     * @param certificateNo the certificateNo to set
     */
    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    /**
     * @return the fitmentDate
     */
    public Date getFitmentDate() {
        return fitmentDate;
    }

    /**
     * @param fitmentDate the fitmentDate to set
     */
    public void setFitmentDate(Date fitmentDate) {
        this.fitmentDate = fitmentDate;
    }

    /**
     * @return the manuName
     */
    public String getManuName() {
        return manuName;
    }

    /**
     * @param manuName the manuName to set
     */
    public void setManuName(String manuName) {
        this.manuName = manuName;
    }

    @Override
    public ReflectiveTapeDobj clone() throws CloneNotSupportedException {
        return (ReflectiveTapeDobj) super.clone();
    }

    /**
     * @return the isDisable_fitmentDate
     */
    public boolean isIsDisable_fitmentDate() {
        return isDisable_fitmentDate;
    }

    /**
     * @param isDisable_fitmentDate the isDisable_fitmentDate to set
     */
    public void setIsDisable_fitmentDate(boolean isDisable_fitmentDate) {
        this.isDisable_fitmentDate = isDisable_fitmentDate;
    }

    /**
     * @return the isDisable_certificateNo
     */
    public boolean isIsDisable_certificateNo() {
        return isDisable_certificateNo;
    }

    /**
     * @param isDisable_certificateNo the isDisable_certificateNo to set
     */
    public void setIsDisable_certificateNo(boolean isDisable_certificateNo) {
        this.isDisable_certificateNo = isDisable_certificateNo;
    }

    /**
     * @return the isDisable_manuName
     */
    public boolean isIsDisable_manuName() {
        return isDisable_manuName;
    }

    /**
     * @param isDisable_manuName the isDisable_manuName to set
     */
    public void setIsDisable_manuName(boolean isDisable_manuName) {
        this.isDisable_manuName = isDisable_manuName;
    }

    public boolean isReflectiveTapeInsertUpdateFlag() {
        return reflectiveTapeInsertUpdateFlag;
    }

    public void setReflectiveTapeInsertUpdateFlag(boolean reflectiveTapeInsertUpdateFlag) {
        this.reflectiveTapeInsertUpdateFlag = reflectiveTapeInsertUpdateFlag;
    }
    
}
