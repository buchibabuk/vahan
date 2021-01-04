/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.dealer;

import java.io.Serializable;

/**
 *
 * @author acer
 */
public class TmConfigurationDealerDobj implements Serializable {

    private String stateCd;
    private boolean taxExemptionAllowed;
    private boolean newRegnNotAllowTransVeh;
    private boolean validateHomoSaleAmt;
    private boolean tempRegnFeeWithNewForSameOffices;
    private boolean tempRegnApprovalBeforeNewRegn;
    private boolean paymentAtOffice;
    private String feeTaxAmtZero;
    private boolean tempRegnAllowNonTransVeh;
    private String normsConditionFormulas;
    private boolean offCorrectionAtOffice;
    private boolean exmptServiceChargeInTRC;
    private boolean attachFancyNoAtDealer;
    private boolean allowRegnIfDataFound = false;
    private int tempFlowInNewRegnActionCd;
    private boolean allowInwardTempAnyOffice = false;
    private String allowedOwnerCode;
    private boolean checkHSRPPendency = false;
    private String ownerChoiceConditionFormula;
    private boolean dealerValidityRequired = false;
    private boolean printNewRCAtDealer = false;
    private String regnRestrictionAtDealer;
    private String regnRestrictionMessage;

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
     * @return the taxExemptionAllowed
     */
    public boolean isTaxExemptionAllowed() {
        return taxExemptionAllowed;
    }

    /**
     * @param taxExemptionAllowed the taxExemptionAllowed to set
     */
    public void setTaxExemptionAllowed(boolean taxExemptionAllowed) {
        this.taxExemptionAllowed = taxExemptionAllowed;
    }

    /**
     * @return the newRegnNotAllowTransVeh
     */
    public boolean isNewRegnNotAllowTransVeh() {
        return newRegnNotAllowTransVeh;
    }

    /**
     * @param newRegnNotAllowTransVeh the newRegnNotAllowTransVeh to set
     */
    public void setNewRegnNotAllowTransVeh(boolean newRegnNotAllowTransVeh) {
        this.newRegnNotAllowTransVeh = newRegnNotAllowTransVeh;
    }

    /**
     * @return the validateHomoSaleAmt
     */
    public boolean isValidateHomoSaleAmt() {
        return validateHomoSaleAmt;
    }

    /**
     * @param validateHomoSaleAmt the validateHomoSaleAmt to set
     */
    public void setValidateHomoSaleAmt(boolean validateHomoSaleAmt) {
        this.validateHomoSaleAmt = validateHomoSaleAmt;
    }

    /**
     * @return the tempRegnApprovalBeforeNewRegn
     */
    public boolean isTempRegnApprovalBeforeNewRegn() {
        return tempRegnApprovalBeforeNewRegn;
    }

    /**
     * @param tempRegnApprovalBeforeNewRegn the tempRegnApprovalBeforeNewRegn to
     * set
     */
    public void setTempRegnApprovalBeforeNewRegn(boolean tempRegnApprovalBeforeNewRegn) {
        this.tempRegnApprovalBeforeNewRegn = tempRegnApprovalBeforeNewRegn;
    }

    /**
     * @return the paymentAtOffice
     */
    public boolean isPaymentAtOffice() {
        return paymentAtOffice;
    }

    /**
     * @param paymentAtOffice the paymentAtOffice to set
     */
    public void setPaymentAtOffice(boolean paymentAtOffice) {
        this.paymentAtOffice = paymentAtOffice;
    }

    /**
     * @return the tempRegnFeeWithNewForSameOffices
     */
    public boolean isTempRegnFeeWithNewForSameOffices() {
        return tempRegnFeeWithNewForSameOffices;
    }

    /**
     * @param tempRegnFeeWithNewForSameOffices the
     * tempRegnFeeWithNewForSameOffices to set
     */
    public void setTempRegnFeeWithNewForSameOffices(boolean tempRegnFeeWithNewForSameOffices) {
        this.tempRegnFeeWithNewForSameOffices = tempRegnFeeWithNewForSameOffices;
    }

    /**
     * @return the feeTaxAmtZero
     */
    public String getFeeTaxAmtZero() {
        return feeTaxAmtZero;
    }

    /**
     * @param feeTaxAmtZero the feeTaxAmtZero to set
     */
    public void setFeeTaxAmtZero(String feeTaxAmtZero) {
        this.feeTaxAmtZero = feeTaxAmtZero;
    }

    /**
     * @return the tempRegnAllowNonTransVeh
     */
    public boolean isTempRegnAllowNonTransVeh() {
        return tempRegnAllowNonTransVeh;
    }

    /**
     * @param tempRegnAllowNonTransVeh the tempRegnAllowNonTransVeh to set
     */
    public void setTempRegnAllowNonTransVeh(boolean tempRegnAllowNonTransVeh) {
        this.tempRegnAllowNonTransVeh = tempRegnAllowNonTransVeh;
    }

    /**
     * @return the normsConditionFormulas
     */
    public String getNormsConditionFormulas() {
        return normsConditionFormulas;
    }

    /**
     * @param normsConditionFormulas the normsConditionFormulas to set
     */
    public void setNormsConditionFormulas(String normsConditionFormulas) {
        this.normsConditionFormulas = normsConditionFormulas;
    }

    /**
     * @return the offCorrectionAtOffice
     */
    public boolean isOffCorrectionAtOffice() {
        return offCorrectionAtOffice;
    }

    /**
     * @param offCorrectionAtOffice the offCorrectionAtOffice to set
     */
    public void setOffCorrectionAtOffice(boolean offCorrectionAtOffice) {
        this.offCorrectionAtOffice = offCorrectionAtOffice;
    }

    /**
     * @return the exmptServiceChargeInTRC
     */
    public boolean isExmptServiceChargeInTRC() {
        return exmptServiceChargeInTRC;
    }

    /**
     * @param exmptServiceChargeInTRC the exmptServiceChargeInTRC to set
     */
    public void setExmptServiceChargeInTRC(boolean exmptServiceChargeInTRC) {
        this.exmptServiceChargeInTRC = exmptServiceChargeInTRC;
    }

    /**
     * @return the attachFancyNoAtDealer
     */
    public boolean isAttachFancyNoAtDealer() {
        return attachFancyNoAtDealer;
    }

    /**
     * @param attachFancyNoAtDealer the attachFancyNoAtDealer to set
     */
    public void setAttachFancyNoAtDealer(boolean attachFancyNoAtDealer) {
        this.attachFancyNoAtDealer = attachFancyNoAtDealer;
    }

    /**
     * @return the allowRegnIfDataFound
     */
    public boolean isAllowRegnIfDataFound() {
        return allowRegnIfDataFound;
    }

    /**
     * @param allowRegnIfDataFound the allowRegnIfDataFound to set
     */
    public void setAllowRegnIfDataFound(boolean allowRegnIfDataFound) {
        this.allowRegnIfDataFound = allowRegnIfDataFound;
    }

    /**
     * @return the tempFlowInNewRegnActionCd
     */
    public int getTempFlowInNewRegnActionCd() {
        return tempFlowInNewRegnActionCd;
    }

    /**
     * @param tempFlowInNewRegnActionCd the tempFlowInNewRegnActionCd to set
     */
    public void setTempFlowInNewRegnActionCd(int tempFlowInNewRegnActionCd) {
        this.tempFlowInNewRegnActionCd = tempFlowInNewRegnActionCd;
    }

    /**
     * @return the allowInwardTempAnyOffice
     */
    public boolean isAllowInwardTempAnyOffice() {
        return allowInwardTempAnyOffice;
    }

    /**
     * @param allowInwardTempAnyOffice the allowInwardTempAnyOffice to set
     */
    public void setAllowInwardTempAnyOffice(boolean allowInwardTempAnyOffice) {
        this.allowInwardTempAnyOffice = allowInwardTempAnyOffice;
    }

    /**
     * @return the allowedOwnerCode
     */
    public String getAllowedOwnerCode() {
        return allowedOwnerCode;
    }

    /**
     * @param allowedOwnerCode the allowedOwnerCode to set
     */
    public void setAllowedOwnerCode(String allowedOwnerCode) {
        this.allowedOwnerCode = allowedOwnerCode;
    }

    /**
     * @return the checkHSRPPendency
     */
    public boolean isCheckHSRPPendency() {
        return checkHSRPPendency;
    }

    /**
     * @param checkHSRPPendency the checkHSRPPendency to set
     */
    public void setCheckHSRPPendency(boolean checkHSRPPendency) {
        this.checkHSRPPendency = checkHSRPPendency;
    }

    /**
     * @return the ownerChoiceConditionFormula
     */
    public String getOwnerChoiceConditionFormula() {
        return ownerChoiceConditionFormula;
    }

    /**
     * @param ownerChoiceConditionFormula the ownerChoiceConditionFormula to set
     */
    public void setOwnerChoiceConditionFormula(String ownerChoiceConditionFormula) {
        this.ownerChoiceConditionFormula = ownerChoiceConditionFormula;
    }

    /**
     * @return the dealerValidityRequired
     */
    public boolean isDealerValidityRequired() {
        return dealerValidityRequired;
    }

    /**
     * @param dealerValidityRequired the dealerValidityRequired to set
     */
    public void setDealerValidityRequired(boolean dealerValidityRequired) {
        this.dealerValidityRequired = dealerValidityRequired;
    }

    public boolean isPrintNewRCAtDealer() {
        return printNewRCAtDealer;
    }

    public void setPrintNewRCAtDealer(boolean printNewRCAtDealer) {
        this.printNewRCAtDealer = printNewRCAtDealer;
    }

    public String getRegnRestrictionAtDealer() {
        return regnRestrictionAtDealer;
    }

    public void setRegnRestrictionAtDealer(String regnRestrictionAtDealer) {
        this.regnRestrictionAtDealer = regnRestrictionAtDealer;
    }

    public String getRegnRestrictionMessage() {
        return regnRestrictionMessage;
    }

    public void setRegnRestrictionMessage(String regnRestrictionMessage) {
        this.regnRestrictionMessage = regnRestrictionMessage;
    }
}
