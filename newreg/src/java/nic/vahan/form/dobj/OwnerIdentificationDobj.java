/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class OwnerIdentificationDobj implements Serializable, Cloneable {

    private String state_cd;
    private int off_cd;
    private String appl_no;
    private String regn_no;
    private Long mobile_no = 0L;
    private String email_id = "";
    private String pan_no;
    private String aadhar_no;
    private String passport_no;
    private String ration_card_no;
    private String voter_id;
    private String dl_no;
    private Date verified_on;
    private boolean flag;
    private boolean mobileNoEditable;
    private boolean ownerCatgEditable;
    private int ownerCatg;
    private int ownerCdDept = 0;
    private boolean dlRequired;
    private boolean pan_card_mandatory;
    private boolean dlValidationRequired;
    private boolean dlDisabled = false;
    private boolean insertUpdateIdentification;
    private String mask_aadhar_no;
    private boolean disableAdhaar=true;

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
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
     * @return the mobile_no
     */
    public Long getMobile_no() {
        return mobile_no;
    }

    /**
     * @param mobile_no the mobile_no to set
     */
    public void setMobile_no(Long mobile_no) {
        this.mobile_no = mobile_no;
    }

    /**
     * @return the email_id
     */
    public String getEmail_id() {
        return email_id;
    }

    /**
     * @param email_id the email_id to set
     */
    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    /**
     * @return the pan_no
     */
    public String getPan_no() {
        return pan_no;
    }

    /**
     * @param pan_no the pan_no to set
     */
    public void setPan_no(String pan_no) {
        this.pan_no = pan_no;
    }

    /**
     * @return the aadhar_no
     */
    public String getAadhar_no() {
        return aadhar_no;
    }

    /**
     * @param aadhar_no the aadhar_no to set
     */
    public void setAadhar_no(String aadhar_no) {
        this.aadhar_no = aadhar_no;
    }

    /**
     * @return the passport_no
     */
    public String getPassport_no() {
        return passport_no;
    }

    /**
     * @param passport_no the passport_no to set
     */
    public void setPassport_no(String passport_no) {
        this.passport_no = passport_no;
    }

    /**
     * @return the ration_card_no
     */
    public String getRation_card_no() {
        return ration_card_no;
    }

    /**
     * @param ration_card_no the ration_card_no to set
     */
    public void setRation_card_no(String ration_card_no) {
        this.ration_card_no = ration_card_no;
    }

    /**
     * @return the voter_id
     */
    public String getVoter_id() {
        return voter_id;
    }

    /**
     * @param voter_id the voter_id to set
     */
    public void setVoter_id(String voter_id) {
        this.voter_id = voter_id;
    }

    /**
     * @return the dl_no
     */
    public String getDl_no() {
        return dl_no;
    }

    /**
     * @param dl_no the dl_no to set
     */
    public void setDl_no(String dl_no) {
        this.dl_no = dl_no;
    }

    /**
     * @return the verified_on
     */
    public Date getVerified_on() {
        return verified_on;
    }

    /**
     * @param verified_on the verified_on to set
     */
    public void setVerified_on(Date verified_on) {
        this.verified_on = verified_on;
    }

    /**
     * @return the flag
     */
    public boolean isFlag() {
        return flag;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    /**
     * @return the mobileNoEditable
     */
    public boolean isMobileNoEditable() {
        return mobileNoEditable;
    }

    /**
     * @param mobileNoEditable the mobileNoEditable to set
     */
    public void setMobileNoEditable(boolean mobileNoEditable) {
        this.mobileNoEditable = mobileNoEditable;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the ownerCatg
     */
    public int getOwnerCatg() {
        return ownerCatg;
    }

    /**
     * @param ownerCatg the ownerCatg to set
     */
    public void setOwnerCatg(int ownerCatg) {
        this.ownerCatg = ownerCatg;
    }

    /**
     * @return the ownerCatgEditable
     */
    public boolean isOwnerCatgEditable() {
        return ownerCatgEditable;
    }

    /**
     * @param ownerCatgEditable the ownerCatgEditable to set
     */
    public void setOwnerCatgEditable(boolean ownerCatgEditable) {
        this.ownerCatgEditable = ownerCatgEditable;
    }

    /**
     * @return the ownerCdDept
     */
    public int getOwnerCdDept() {
        return ownerCdDept;
    }

    /**
     * @param ownerCdDept the ownerCdDept to set
     */
    public void setOwnerCdDept(int ownerCdDept) {
        this.ownerCdDept = ownerCdDept;
    }

    /**
     * @return the dlValidationRequired
     */
    public boolean isDlValidationRequired() {
        return dlValidationRequired;
    }

    /**
     * @param dlValidationRequired the dlValidationRequired to set
     */
    public void setDlValidationRequired(boolean dlValidationRequired) {
        this.dlValidationRequired = dlValidationRequired;
    }

    /**
     * @return the dlRequired
     */
    public boolean isDlRequired() {
        return dlRequired;
    }

    /**
     * @param dlRequired the dlRequired to set
     */
    public void setDlRequired(boolean dlRequired) {
        this.dlRequired = dlRequired;
    }

    /**
     * @return the dlDisabled
     */
    public boolean isDlDisabled() {
        return dlDisabled;
    }

    /**
     * @param dlDisabled the dlDisabled to set
     */
    public void setDlDisabled(boolean dlDisabled) {
        this.dlDisabled = dlDisabled;
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

    public boolean isInsertUpdateIdentification() {
        return insertUpdateIdentification;
    }

    public void setInsertUpdateIdentification(boolean insertUpdateIdentification) {
        this.insertUpdateIdentification = insertUpdateIdentification;
    }

    public boolean isPan_card_mandatory() {
        return pan_card_mandatory;
    }

    public void setPan_card_mandatory(boolean pan_card_mandatory) {
        this.pan_card_mandatory = pan_card_mandatory;
    }

    public String getMask_aadhar_no() {
        return mask_aadhar_no;
    }

    public void setMask_aadhar_no(String mask_aadhar_no) {
        this.mask_aadhar_no = mask_aadhar_no;
    }

    /**
     * @return the disableAdhaar
     */
    public boolean isDisableAdhaar() {
        return disableAdhaar;
    }

    /**
     * @param disableAdhaar the disableAdhaar to set
     */
    public void setDisableAdhaar(boolean disableAdhaar) {
        this.disableAdhaar = disableAdhaar;
    }
}
