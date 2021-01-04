/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.tradecert;

import java.io.Serializable;

/**
 *
 * @author Varun_Kaushik
 */
public class TmConfigurationTradeCertificateDobj implements Serializable {

    private String stateCd;
    private boolean fuelToBeDisplayed;
    private boolean noOfVehiclesNotToBeShown;
    private boolean duplicateTcNotApplicable;
    private boolean documentsUploadNRevert;
    private boolean dealerValidUptoPlus1YrAsTcValidUpto;
    private boolean validFromNextDayOfPrevValidUpto;
    private boolean validUptoInsteadOfValidityPeriod;
    private boolean tcPrintingDataForDealer;
    private boolean vehClassToBeRendered;
    private boolean multipleTcHavingUniqueTcNo;
    private boolean feeWithTaxPlusOtherVariables;
    private boolean usingOnlineSchemaTc;
    private boolean feesNotToBeShownInForm16;
    private boolean verifyRoleOnDmsPage;
    private boolean applToHaveSingleVehCatg;
    private boolean noOfVehicleToBeUpdatedWithoutUsingBalance;
    private boolean subListDataDetailsToBeShownInForm17;
    private boolean taxToBeCalculatedWebService;
    private boolean fuelTaxApplicable;
    private boolean surchargeToBeCalculatedViaWebService;
    private boolean illegitimateTradeCertApplicable;
    private boolean taxApplicableForIllegitimateTradeCert;
    private boolean yardFeeApplicable;
    private boolean serviceChargeMultiplyByNoOfTradeCert;
    private boolean serviceChargeHardCoded;
    private boolean feeToBeHardCoded;
    private boolean feeAsPerVehicleClass;
    private boolean stockTransferReq;
    private boolean doNotConsiderNoOfVchForExpiry;
    private boolean serialNoWithTc;
    private boolean inspectionDtlsReq;
    private boolean tcInitiationInRtoByCode;
    private boolean watermarkReq;
    private boolean showOnlyAdminDealers;
    private boolean dealerMasterNotToBeUpdated;
    private boolean cmvrVchCatgApplicable;
    private boolean allowFacelessService;

    /**
     * @return the duplicateTcNotApplicable
     */
    public boolean isDuplicateTcNotApplicable() {
        return duplicateTcNotApplicable;
    }

    /**
     * @param duplicateTcNotApplicable the duplicateTcNotApplicable to set
     */
    public void setDuplicateTcNotApplicable(boolean duplicateTcNotApplicable) {
        this.duplicateTcNotApplicable = duplicateTcNotApplicable;
    }

    /**
     * @return the documentsUploadNRevert
     */
    public boolean isDocumentsUploadNRevert() {
        return documentsUploadNRevert;
    }

    /**
     * @param documentsUploadNRevert the documentsUploadNRevert to set
     */
    public void setDocumentsUploadNRevert(boolean documentsUploadNRevert) {
        this.documentsUploadNRevert = documentsUploadNRevert;
    }

    /**
     * @return the dealerValidUptoPlus1YrAsTcValidUpto
     */
    public boolean isDealerValidUptoPlus1YrAsTcValidUpto() {
        return dealerValidUptoPlus1YrAsTcValidUpto;
    }

    /**
     * @param dealerValidUptoPlus1YrAsTcValidUpto the
     * dealerValidUptoPlus1YrAsTcValidUpto to set
     */
    public void setDealerValidUptoPlus1YrAsTcValidUpto(boolean dealerValidUptoPlus1YrAsTcValidUpto) {
        this.dealerValidUptoPlus1YrAsTcValidUpto = dealerValidUptoPlus1YrAsTcValidUpto;
    }

    /**
     * @return the validFromNextDayOfPrevValidUpto
     */
    public boolean isValidFromNextDayOfPrevValidUpto() {
        return validFromNextDayOfPrevValidUpto;
    }

    /**
     * @param validFromNextDayOfPrevValidUpto the
     * validFromNextDayOfPrevValidUpto to set
     */
    public void setValidFromNextDayOfPrevValidUpto(boolean validFromNextDayOfPrevValidUpto) {
        this.validFromNextDayOfPrevValidUpto = validFromNextDayOfPrevValidUpto;
    }

    /**
     * @return the validUptoInsteadOfValidityPeriod
     */
    public boolean isValidUptoInsteadOfValidityPeriod() {
        return validUptoInsteadOfValidityPeriod;
    }

    /**
     * @param validUptoInsteadOfValidityPeriod the
     * validUptoInsteadOfValidityPeriod to set
     */
    public void setValidUptoInsteadOfValidityPeriod(boolean validUptoInsteadOfValidityPeriod) {
        this.validUptoInsteadOfValidityPeriod = validUptoInsteadOfValidityPeriod;
    }

    /**
     * @return the tcPrintingDataForDealer
     */
    public boolean isTcPrintingDataForDealer() {
        return tcPrintingDataForDealer;
    }

    /**
     * @param tcPrintingDataForDealer the tcPrintingDataForDealer to set
     */
    public void setTcPrintingDataForDealer(boolean tcPrintingDataForDealer) {
        this.tcPrintingDataForDealer = tcPrintingDataForDealer;
    }

    /**
     * @return the multipleTcHavingUniqueTcNo
     */
    public boolean isMultipleTcHavingUniqueTcNo() {
        return multipleTcHavingUniqueTcNo;
    }

    /**
     * @param multipleTcHavingUniqueTcNo the multipleTcHavingUniqueTcNo to set
     */
    public void setMultipleTcHavingUniqueTcNo(boolean multipleTcHavingUniqueTcNo) {
        this.multipleTcHavingUniqueTcNo = multipleTcHavingUniqueTcNo;
    }

    /**
     * @return the feeWithTaxPlusOtherVariables
     */
    public boolean isFeeWithTaxPlusOtherVariables() {
        return feeWithTaxPlusOtherVariables;
    }

    /**
     * @param feeWithTaxPlusOtherVariables the feeWithTaxPlusOtherVariables to
     * set
     */
    public void setFeeWithTaxPlusOtherVariables(boolean feeWithTaxPlusOtherVariables) {
        this.feeWithTaxPlusOtherVariables = feeWithTaxPlusOtherVariables;
    }

    /**
     * @return the usingOnlineSchemaTc
     */
    public boolean isUsingOnlineSchemaTc() {
        return usingOnlineSchemaTc;
    }

    /**
     * @param usingOnlineSchemaTc the usingOnlineSchemaTc to set
     */
    public void setUsingOnlineSchemaTc(boolean usingOnlineSchemaTc) {
        this.usingOnlineSchemaTc = usingOnlineSchemaTc;
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
     * @return the fuelToBeDisplayed
     */
    public boolean isFuelToBeDisplayed() {
        return fuelToBeDisplayed;
    }

    /**
     * @param fuelToBeDisplayed the fuelToBeDisplayed to set
     */
    public void setFuelToBeDisplayed(boolean fuelToBeDisplayed) {
        this.fuelToBeDisplayed = fuelToBeDisplayed;
    }

    /**
     * @return the noOfVehiclesNotToBeShown
     */
    public boolean isNoOfVehiclesNotToBeShown() {
        return noOfVehiclesNotToBeShown;
    }

    /**
     * @param noOfVehiclesNotToBeShown the noOfVehiclesNotToBeShown to set
     */
    public void setNoOfVehiclesNotToBeShown(boolean noOfVehiclesNotToBeShown) {
        this.noOfVehiclesNotToBeShown = noOfVehiclesNotToBeShown;
    }

    /**
     * @return the feesNotToBeShownInForm16
     */
    public boolean isFeesNotToBeShownInForm16() {
        return feesNotToBeShownInForm16;
    }

    /**
     * @param feesNotToBeShownInForm16 the feesNotToBeShownInForm16 to set
     */
    public void setFeesNotToBeShownInForm16(boolean feesNotToBeShownInForm16) {
        this.feesNotToBeShownInForm16 = feesNotToBeShownInForm16;
    }

    /**
     * @return the vehClassToBeRendered
     */
    public boolean isVehClassToBeRendered() {
        return vehClassToBeRendered;
    }

    /**
     * @param vehClassToBeRendered the vehClassToBeRendered to set
     */
    public void setVehClassToBeRendered(boolean vehClassToBeRendered) {
        this.vehClassToBeRendered = vehClassToBeRendered;
    }

    /**
     * @return the verifyRoleOnDmsPage
     */
    public boolean isVerifyRoleOnDmsPage() {
        return verifyRoleOnDmsPage;
    }

    /**
     * @param verifyRoleOnDmsPage the verifyRoleOnDmsPage to set
     */
    public void setVerifyRoleOnDmsPage(boolean verifyRoleOnDmsPage) {
        this.verifyRoleOnDmsPage = verifyRoleOnDmsPage;
    }

    /**
     * @return the applToHaveSingleVehCatg
     */
    public boolean isApplToHaveSingleVehCatg() {
        return applToHaveSingleVehCatg;
    }

    /**
     * @param applToHaveSingleVehCatg the applToHaveSingleVehCatg to set
     */
    public void setApplToHaveSingleVehCatg(boolean applToHaveSingleVehCatg) {
        this.applToHaveSingleVehCatg = applToHaveSingleVehCatg;
    }

    /**
     * @return the noOfVehicleToBeUpdatedWithoutUsingBalance
     */
    public boolean isNoOfVehicleToBeUpdatedWithoutUsingBalance() {
        return noOfVehicleToBeUpdatedWithoutUsingBalance;
    }

    /**
     * @param noOfVehicleToBeUpdatedWithoutUsingBalance the
     * noOfVehicleToBeUpdatedWithoutUsingBalance to set
     */
    public void setNoOfVehicleToBeUpdatedWithoutUsingBalance(boolean noOfVehicleToBeUpdatedWithoutUsingBalance) {
        this.noOfVehicleToBeUpdatedWithoutUsingBalance = noOfVehicleToBeUpdatedWithoutUsingBalance;
    }

    /**
     * @return the subListDataDetailsToBeShownInForm17
     */
    public boolean isSubListDataDetailsToBeShownInForm17() {
        return subListDataDetailsToBeShownInForm17;
    }

    /**
     * @param subListDataDetailsToBeShownInForm17 the
     * subListDataDetailsToBeShownInForm17 to set
     */
    public void setSubListDataDetailsToBeShownInForm17(boolean subListDataDetailsToBeShownInForm17) {
        this.subListDataDetailsToBeShownInForm17 = subListDataDetailsToBeShownInForm17;
    }

    /**
     * @return the taxToBeCalculatedWebService
     */
    public boolean isTaxToBeCalculatedWebService() {
        return taxToBeCalculatedWebService;
    }

    /**
     * @param taxToBeCalculatedWebService the taxToBeCalculatedWebService to set
     */
    public void setTaxToBeCalculatedWebService(boolean taxToBeCalculatedWebService) {
        this.taxToBeCalculatedWebService = taxToBeCalculatedWebService;
    }

    /**
     * @return the fuelTaxApplicable
     */
    public boolean isFuelTaxApplicable() {
        return fuelTaxApplicable;
    }

    /**
     * @param fuelTaxApplicable the fuelTaxApplicable to set
     */
    public void setFuelTaxApplicable(boolean fuelTaxApplicable) {
        this.fuelTaxApplicable = fuelTaxApplicable;
    }

    /**
     * @return the surchargeToBeCalculatedViaWebService
     */
    public boolean isSurchargeToBeCalculatedViaWebService() {
        return surchargeToBeCalculatedViaWebService;
    }

    /**
     * @param surchargeToBeCalculatedViaWebService the
     * surchargeToBeCalculatedViaWebService to set
     */
    public void setSurchargeToBeCalculatedViaWebService(boolean surchargeToBeCalculatedViaWebService) {
        this.surchargeToBeCalculatedViaWebService = surchargeToBeCalculatedViaWebService;
    }

    /**
     * @return the illegitimateTradeCertApplicable
     */
    public boolean isIllegitimateTradeCertApplicable() {
        return illegitimateTradeCertApplicable;
    }

    /**
     * @param illegitimateTradeCertApplicable the
     * illegitimateTradeCertApplicable to set
     */
    public void setIllegitimateTradeCertApplicable(boolean illegitimateTradeCertApplicable) {
        this.illegitimateTradeCertApplicable = illegitimateTradeCertApplicable;
    }

    /**
     * @return the taxApplicableForIllegitimateTradeCert
     */
    public boolean isTaxApplicableForIllegitimateTradeCert() {
        return taxApplicableForIllegitimateTradeCert;
    }

    /**
     * @param taxApplicableForIllegitimateTradeCert the
     * taxApplicableForIllegitimateTradeCert to set
     */
    public void setTaxApplicableForIllegitimateTradeCert(boolean taxApplicableForIllegitimateTradeCert) {
        this.taxApplicableForIllegitimateTradeCert = taxApplicableForIllegitimateTradeCert;
    }

    /**
     * @return the yardFeeApplicable
     */
    public boolean isYardFeeApplicable() {
        return yardFeeApplicable;
    }

    /**
     * @param yardFeeApplicable the yardFeeApplicable to set
     */
    public void setYardFeeApplicable(boolean yardFeeApplicable) {
        this.yardFeeApplicable = yardFeeApplicable;
    }

    /**
     * @return the serviceChargeMultiplyByNoOfTradeCert
     */
    public boolean isServiceChargeMultiplyByNoOfTradeCert() {
        return serviceChargeMultiplyByNoOfTradeCert;
    }

    /**
     * @param serviceChargeMultiplyByNoOfTradeCert the
     * serviceChargeMultiplyByNoOfTradeCert to set
     */
    public void setServiceChargeMultiplyByNoOfTradeCert(boolean serviceChargeMultiplyByNoOfTradeCert) {
        this.serviceChargeMultiplyByNoOfTradeCert = serviceChargeMultiplyByNoOfTradeCert;
    }

    /**
     * @return the serviceChargeHardCoded
     */
    public boolean isServiceChargeHardCoded() {
        return serviceChargeHardCoded;
    }

    /**
     * @param serviceChargeHardCoded the serviceChargeHardCoded to set
     */
    public void setServiceChargeHardCoded(boolean serviceChargeHardCoded) {
        this.serviceChargeHardCoded = serviceChargeHardCoded;
    }

    /**
     * @return the feeToBeHardCoded
     */
    public boolean isFeeToBeHardCoded() {
        return feeToBeHardCoded;
    }

    /**
     * @param feeToBeHardCoded the feeToBeHardCoded to set
     */
    public void setFeeToBeHardCoded(boolean feeToBeHardCoded) {
        this.feeToBeHardCoded = feeToBeHardCoded;
    }

    /**
     * @return the feeAsPerVehicleClass
     */
    public boolean isFeeAsPerVehicleClass() {
        return feeAsPerVehicleClass;
    }

    /**
     * @param feeAsPerVehicleClass the feeAsPerVehicleClass to set
     */
    public void setFeeAsPerVehicleClass(boolean feeAsPerVehicleClass) {
        this.feeAsPerVehicleClass = feeAsPerVehicleClass;
    }

    /**
     * @return the stockTransferReq
     */
    public boolean isStockTransferReq() {
        return stockTransferReq;
    }

    /**
     * @param stockTransferReq the stockTransferReq to set
     */
    public void setStockTransferReq(boolean stockTransferReq) {
        this.stockTransferReq = stockTransferReq;
    }

    /**
     * @return the doNotConsiderNoOfVchForExpiry
     */
    public boolean isDoNotConsiderNoOfVchForExpiry() {
        return doNotConsiderNoOfVchForExpiry;
    }

    /**
     * @param doNotConsiderNoOfVchForExpiry the doNotConsiderNoOfVchForExpiry to
     * set
     */
    public void setDoNotConsiderNoOfVchForExpiry(boolean doNotConsiderNoOfVchForExpiry) {
        this.doNotConsiderNoOfVchForExpiry = doNotConsiderNoOfVchForExpiry;
    }

    /**
     * @return the inspectionDtlsReq
     */
    public boolean isInspectionDtlsReq() {
        return inspectionDtlsReq;
    }

    /**
     * @param inspectionDtlsReq the inspectionDtlsReq to set
     */
    public void setInspectionDtlsReq(boolean inspectionDtlsReq) {
        this.inspectionDtlsReq = inspectionDtlsReq;
    }

    /**
     * @return the serialNoWithTc
     */
    public boolean isSerialNoWithTc() {
        return serialNoWithTc;
    }

    /**
     * @param serialNoWithTc the serialNoWithTc to set
     */
    public void setSerialNoWithTc(boolean serialNoWithTc) {
        this.serialNoWithTc = serialNoWithTc;
    }

    /**
     * @return the tcInitiationInRtoByCode
     */
    public boolean isTcInitiationInRtoByCode() {
        return tcInitiationInRtoByCode;
    }

    /**
     * @param tcInitiationInRtoByCode the tcInitiationInRtoByCode to set
     */
    public void setTcInitiationInRtoByCode(boolean tcInitiationInRtoByCode) {
        this.tcInitiationInRtoByCode = tcInitiationInRtoByCode;
    }

    /**
     * @return the watermarkReq
     */
    public boolean isWatermarkReq() {
        return watermarkReq;
    }

    /**
     * @param watermarkReq the watermarkReq to set
     */
    public void setWatermarkReq(boolean watermarkReq) {
        this.watermarkReq = watermarkReq;
    }

    /**
     * @return the showOnlyAdminDealers
     */
    public boolean isShowOnlyAdminDealers() {
        return showOnlyAdminDealers;
    }

    /**
     * @param showOnlyAdminDealers the showOnlyAdminDealers to set
     */
    public void setShowOnlyAdminDealers(boolean showOnlyAdminDealers) {
        this.showOnlyAdminDealers = showOnlyAdminDealers;
    }

    /**
     * @return the dealerMasterNotToBeUpdated
     */
    public boolean isDealerMasterNotToBeUpdated() {
        return dealerMasterNotToBeUpdated;
    }

    /**
     * @param dealerMasterNotToBeUpdated the dealerMasterNotToBeUpdated to set
     */
    public void setDealerMasterNotToBeUpdated(boolean dealerMasterNotToBeUpdated) {
        this.dealerMasterNotToBeUpdated = dealerMasterNotToBeUpdated;
    }

    /**
     * @return the cmvrVchCatgApplicable
     */
    public boolean isCmvrVchCatgApplicable() {
        return cmvrVchCatgApplicable;
    }

    /**
     * @param cmvrVchCatgApplicable the cmvrVchCatgApplicable to set
     */
    public void setCmvrVchCatgApplicable(boolean cmvrVchCatgApplicable) {
        this.cmvrVchCatgApplicable = cmvrVchCatgApplicable;
    }

    /**
     * @return the allowFacelessService
     */
    public boolean isAllowFacelessService() {
        return allowFacelessService;
    }

    /**
     * @param allowFacelessService the allowFacelessService to set
     */
    public void setAllowFacelessService(boolean allowFacelessService) {
        this.allowFacelessService = allowFacelessService;
    }

   
    
}
