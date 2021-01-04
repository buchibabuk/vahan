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
public class SpeedGovernorDobj implements Serializable, Cloneable {

    private String appl_no = null;
    private String state_cd = null;
    private int off_cd = 0;
    private String regn_no = null;
    private String sg_no = null;
    private Date sg_fitted_on = null;
    private String sg_fitted_at = null;
    private String sg_fitted_at_desc = null;
    private int sgGovType = 0;
    private String sgTypeApprovalNo = null;
    private String sgTestReportNo = null;
    private String sgFitmentCerticateNo = null;
    private boolean isDisable_sg_no = false;
    private boolean isDisable_sg_fitted_on = false;
    private boolean isDisable_sg_fitted_at = false;
    private boolean isDisable_sgGovType = false;
    private boolean isDisable_sgTypeApprovalNo = false;
    private boolean isDisable_sgTestReportNo = false;
    private boolean isDisable_sgFitmentCerticateNo = false;
    private boolean speedGovInsertUpdateFlag;

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
     * @return the sg_no
     */
    public String getSg_no() {
        return sg_no;
    }

    /**
     * @param sg_no the sg_no to set
     */
    public void setSg_no(String sg_no) {
        this.sg_no = sg_no;
    }

    /**
     * @return the sg_fitted_on
     */
    public Date getSg_fitted_on() {
        return sg_fitted_on;
    }

    /**
     * @param sg_fitted_on the sg_fitted_on to set
     */
    public void setSg_fitted_on(Date sg_fitted_on) {
        this.sg_fitted_on = sg_fitted_on;
    }

    /**
     * @return the sg_fitted_at
     */
    public String getSg_fitted_at() {
        return sg_fitted_at;
    }

    /**
     * @param sg_fitted_at the sg_fitted_at to set
     */
    public void setSg_fitted_at(String sg_fitted_at) {
        this.sg_fitted_at = sg_fitted_at;
    }

    public String getSg_fitted_at_desc() {
        return sg_fitted_at_desc;
    }

    public void setSg_fitted_at_desc(String sg_fitted_at_desc) {
        this.sg_fitted_at_desc = sg_fitted_at_desc;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the sgGovType
     */
    public int getSgGovType() {
        return sgGovType;
    }

    /**
     * @param sgGovType the sgGovType to set
     */
    public void setSgGovType(int sgGovType) {
        this.sgGovType = sgGovType;
    }

    /**
     * @return the sgTypeApprovalNo
     */
    public String getSgTypeApprovalNo() {
        return sgTypeApprovalNo;
    }

    /**
     * @param sgTypeApprovalNo the sgTypeApprovalNo to set
     */
    public void setSgTypeApprovalNo(String sgTypeApprovalNo) {
        this.sgTypeApprovalNo = sgTypeApprovalNo;
    }

    /**
     * @return the sgTestReportNo
     */
    public String getSgTestReportNo() {
        return sgTestReportNo;
    }

    /**
     * @param sgTestReportNo the sgTestReportNo to set
     */
    public void setSgTestReportNo(String sgTestReportNo) {
        this.sgTestReportNo = sgTestReportNo;
    }

    /**
     * @return the sgFitmentCerticateNo
     */
    public String getSgFitmentCerticateNo() {
        return sgFitmentCerticateNo;
    }

    /**
     * @param sgFitmentCerticateNo the sgFitmentCerticateNo to set
     */
    public void setSgFitmentCerticateNo(String sgFitmentCerticateNo) {
        this.sgFitmentCerticateNo = sgFitmentCerticateNo;
    }

    /**
     * @return the isDisable_sg_no
     */
    public boolean isIsDisable_sg_no() {
        return isDisable_sg_no;
    }

    /**
     * @param isDisable_sg_no the isDisable_sg_no to set
     */
    public void setIsDisable_sg_no(boolean isDisable_sg_no) {
        this.isDisable_sg_no = isDisable_sg_no;
    }

    /**
     * @return the isDisable_sg_fitted_on
     */
    public boolean isIsDisable_sg_fitted_on() {
        return isDisable_sg_fitted_on;
    }

    /**
     * @param isDisable_sg_fitted_on the isDisable_sg_fitted_on to set
     */
    public void setIsDisable_sg_fitted_on(boolean isDisable_sg_fitted_on) {
        this.isDisable_sg_fitted_on = isDisable_sg_fitted_on;
    }

    /**
     * @return the isDisable_sg_fitted_at
     */
    public boolean isIsDisable_sg_fitted_at() {
        return isDisable_sg_fitted_at;
    }

    /**
     * @param isDisable_sg_fitted_at the isDisable_sg_fitted_at to set
     */
    public void setIsDisable_sg_fitted_at(boolean isDisable_sg_fitted_at) {
        this.isDisable_sg_fitted_at = isDisable_sg_fitted_at;
    }

    /**
     * @return the isDisable_sgGovType
     */
    public boolean isIsDisable_sgGovType() {
        return isDisable_sgGovType;
    }

    /**
     * @param isDisable_sgGovType the isDisable_sgGovType to set
     */
    public void setIsDisable_sgGovType(boolean isDisable_sgGovType) {
        this.isDisable_sgGovType = isDisable_sgGovType;
    }

    /**
     * @return the isDisable_sgTypeApprovalNo
     */
    public boolean isIsDisable_sgTypeApprovalNo() {
        return isDisable_sgTypeApprovalNo;
    }

    /**
     * @param isDisable_sgTypeApprovalNo the isDisable_sgTypeApprovalNo to set
     */
    public void setIsDisable_sgTypeApprovalNo(boolean isDisable_sgTypeApprovalNo) {
        this.isDisable_sgTypeApprovalNo = isDisable_sgTypeApprovalNo;
    }

    /**
     * @return the isDisable_sgTestReportNo
     */
    public boolean isIsDisable_sgTestReportNo() {
        return isDisable_sgTestReportNo;
    }

    /**
     * @param isDisable_sgTestReportNo the isDisable_sgTestReportNo to set
     */
    public void setIsDisable_sgTestReportNo(boolean isDisable_sgTestReportNo) {
        this.isDisable_sgTestReportNo = isDisable_sgTestReportNo;
    }

    /**
     * @return the isDisable_sgFitmentCerticateNo
     */
    public boolean isIsDisable_sgFitmentCerticateNo() {
        return isDisable_sgFitmentCerticateNo;
    }

    /**
     * @param isDisable_sgFitmentCerticateNo the isDisable_sgFitmentCerticateNo
     * to set
     */
    public void setIsDisable_sgFitmentCerticateNo(boolean isDisable_sgFitmentCerticateNo) {
        this.isDisable_sgFitmentCerticateNo = isDisable_sgFitmentCerticateNo;
    }

    public boolean isSpeedGovInsertUpdateFlag() {
        return speedGovInsertUpdateFlag;
    }

    public void setSpeedGovInsertUpdateFlag(boolean speedGovInsertUpdateFlag) {
        this.speedGovInsertUpdateFlag = speedGovInsertUpdateFlag;
    }
    
}
