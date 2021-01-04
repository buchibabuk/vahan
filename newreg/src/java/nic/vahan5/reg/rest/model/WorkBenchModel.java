/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model;

import java.util.List;
import java.util.Map;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.OtherStateVehDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.dealer.PendencyBankDobj;

/**
 *
 * @author Kartikey Singh
 */
public class WorkBenchModel {

    private int ROLE_CD;
    private String APPL_NO;
    private String PUR_CD;
    private String PURPOSE;
    private String OFFICE_REMARK;
    private String PUBLIC_REMARK;
    private String REGN_NO;
    private String FANCY_REGN_NO;
    private String APPL_DT;
    private String CUR_STATUS;//Current Application Status(N,C,D)
    private int ACTION_CDOE;
    private String COUNTER_ID;
    private String includeSrcURL = "defaultworkbench.xhtml";
    private boolean sub_panel;
    private boolean tabView;
    private boolean trailer_tab;
    private Map<String, Object> prevRoleLabelVale;
    private boolean main_panal_visibililty;
    private boolean tmp_veh_dtls;
    private boolean hypothecated;
    private boolean flag;
    private boolean otherStateVehicle;
    private boolean otherDistrictVehicle;
    private boolean blnScrappedVehicle;
    private boolean blnScrappedVehiclePanel;
    private String chasiNo;
    private String engineNo;
    private String regnType;
    private String regnNo;
    private List listRegnType;
    private List stateList;
    private List officeList;
    private List assignedReasonList;
    private boolean disAppPrint;
    private OtherStateVehDobj otherStateVehDobj;
    private boolean renderApplNoGenMessage;
    private boolean renderPrintDiscalimerButton;
    private String applNoGenMessage;
    private String RETEN_REGN_NO;
    private boolean vehicleHypothecatedDisable;
    private boolean renderCDVehicle;
    private EpayDobj checkFeeTax;
    private EpayDobj checkPaidFeeTax;
    private boolean renderCheckFeeTax;
    private boolean renderCheckFeeTaxTab;
    private boolean renderAppDisapp;
    private boolean autoRunCheckFeeTax;
    private boolean renderAdvanceNoOption;
    private boolean renderScrapCheckBoxOption;
    private boolean isVehicleNoScrapRetain;
    private boolean renderScrapRegnNo;
    private boolean renderExemptionOD;
    private StringBuilder exemptionOD;
    private List<OwnerDetailsDobj> tempRegnDetailsList;
    private String nocDtlsMsg;
    private boolean renderPartialButton;
    private boolean regnTypeOSorORTO;
    private String vahanMessages = null;
    private String getDtlsBtnLabel = "Get Details";
    private SessionVariables sessionVariables;
    private boolean documentUploadShow;
    private String dmsFileUploadUrl;
    private String successUplodedMsg;
    private boolean showBankDetails;
    private PendencyBankDobj bankSubsidyDetail;
    private List bankNameList;
    private boolean renderDocumentUploadBtn;
    private boolean renderTempStateOffPanel;
    private boolean renderVltdDialog;
    private boolean renderDMSMesgDialog;
    private boolean renderCheckFeeTaxButton;
    private boolean renderOwnerChoiceNoPanel;
    private boolean enableDisableTempRegnDetails;
    private String vehicleRegnPrefix;
    // Added by Kartikey for showDetails()
    private String facesMessage = "";
    private String pending;
    private boolean exShoowroomPriceDisable;
    private String pendingAppls;
    private boolean homoEngValidate;
    private boolean found;
    private boolean isValidChassiNo;
    private String offCdLabel;
    private String offCdFromLabel;
    private String stLabel;
    private String stFromLabel;
    private String dealerCd;
    private Long user_cd;
    private boolean disableCurrentState = false;
    private boolean renderFasTagDialog = false;
    private boolean isFasTagInstalled = false;
    private boolean renderFastag = false;
    private boolean isTransportVehicle = true;
    private String vehRegnPlateColorCode = "";
    private String fancyRcptNo;

    /*
     * Author: Kartikey Singh
     */
    public PendencyBankDobj getBankSubsidyDetailsDobj(Owner_dobj owner_dobj, PendencyBankDobj bankSubsidy, String stateCodeSelected, String purCd, String applNo) {
        if (owner_dobj != null && bankSubsidy != null && !"".equals(bankSubsidy) && "DL".equals(stateCodeSelected) && owner_dobj.getVh_class() == TableConstants.ERICKSHAW_VCH_CLASS
                && Integer.valueOf(purCd) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE && owner_dobj.getOwner_cd() == TableConstants.OWNER_CODE_INDIVIDUAL) {
            bankSubsidyDetail.setAadharNo(owner_dobj.getOwner_identity().getAadhar_no());
            bankSubsidyDetail.setDlLlNo(owner_dobj.getOwner_identity().getDl_no());
            bankSubsidyDetail.setApplNo(applNo);
            bankSubsidyDetail.setStatusCode(TableConstants.SUBSIDY_PENDING_STATUS);
        }
        return bankSubsidyDetail;
    }

//    public void checkSaleAmount(int homoSaleAmt, int saleAmt) throws VahanException {
//        if (homoSaleAmt > saleAmt) {
//            if (ACTION_CDOE != TableConstants.TM_NEW_RC_APPROVAL && ACTION_CDOE != TableConstants.TM_ROLE_DEALER_APPROVAL
//                    && ACTION_CDOE != TableConstants.TM_ROLE_DEALER_TEMP_APPROVAL && ACTION_CDOE != TableConstants.TM_TMP_RC_APPROVAL) {
//                owner_bean.setDealerSaleAmt(false);
//                RequestContext.getCurrentInstance().update("workbench_tabview:sale_amt");
//                throw new VahanException(TableConstants.SALE_AMT_ERR_MESS + " {Rs." + homoSaleAmt + "/-} Now Modify Sale Amount.");
//            }
//        }
//    }
    public int getROLE_CD() {
        return ROLE_CD;
    }

    public void setROLE_CD(int ROLE_CD) {
        this.ROLE_CD = ROLE_CD;
    }

    public String getAPPL_NO() {
        return APPL_NO;
    }

    public void setAPPL_NO(String APPL_NO) {
        this.APPL_NO = APPL_NO;
    }

    public String getPUR_CD() {
        return PUR_CD;
    }

    public void setPUR_CD(String PUR_CD) {
        this.PUR_CD = PUR_CD;
    }

    public String getPURPOSE() {
        return PURPOSE;
    }

    public void setPURPOSE(String PURPOSE) {
        this.PURPOSE = PURPOSE;
    }

    public String getOFFICE_REMARK() {
        return OFFICE_REMARK;
    }

    public void setOFFICE_REMARK(String OFFICE_REMARK) {
        this.OFFICE_REMARK = OFFICE_REMARK;
    }

    public String getPUBLIC_REMARK() {
        return PUBLIC_REMARK;
    }

    public void setPUBLIC_REMARK(String PUBLIC_REMARK) {
        this.PUBLIC_REMARK = PUBLIC_REMARK;
    }

    public String getREGN_NO() {
        return REGN_NO;
    }

    public void setREGN_NO(String REGN_NO) {
        this.REGN_NO = REGN_NO;
    }

    public String getFANCY_REGN_NO() {
        return FANCY_REGN_NO;
    }

    public void setFANCY_REGN_NO(String FANCY_REGN_NO) {
        this.FANCY_REGN_NO = FANCY_REGN_NO;
    }

    public String getAPPL_DT() {
        return APPL_DT;
    }

    public void setAPPL_DT(String APPL_DT) {
        this.APPL_DT = APPL_DT;
    }

    public String getCUR_STATUS() {
        return CUR_STATUS;
    }

    public void setCUR_STATUS(String CUR_STATUS) {
        this.CUR_STATUS = CUR_STATUS;
    }

    public int getACTION_CDOE() {
        return ACTION_CDOE;
    }

    public void setACTION_CDOE(int ACTION_CDOE) {
        this.ACTION_CDOE = ACTION_CDOE;
    }

    public String getCOUNTER_ID() {
        return COUNTER_ID;
    }

    public void setCOUNTER_ID(String COUNTER_ID) {
        this.COUNTER_ID = COUNTER_ID;
    }

    public String getIncludeSrcURL() {
        return includeSrcURL;
    }

    public void setIncludeSrcURL(String includeSrcURL) {
        this.includeSrcURL = includeSrcURL;
    }

    public boolean isSub_panel() {
        return sub_panel;
    }

    public void setSub_panel(boolean sub_panel) {
        this.sub_panel = sub_panel;
    }

    public boolean isTabView() {
        return tabView;
    }

    public void setTabView(boolean tabView) {
        this.tabView = tabView;
    }

    public boolean isTrailer_tab() {
        return trailer_tab;
    }

    public void setTrailer_tab(boolean trailer_tab) {
        this.trailer_tab = trailer_tab;
    }

    public Map<String, Object> getPrevRoleLabelVale() {
        return prevRoleLabelVale;
    }

    public void setPrevRoleLabelVale(Map<String, Object> prevRoleLabelVale) {
        this.prevRoleLabelVale = prevRoleLabelVale;
    }

    public boolean isMain_panal_visibililty() {
        return main_panal_visibililty;
    }

    public void setMain_panal_visibililty(boolean main_panal_visibililty) {
        this.main_panal_visibililty = main_panal_visibililty;
    }

    public boolean isTmp_veh_dtls() {
        return tmp_veh_dtls;
    }

    public void setTmp_veh_dtls(boolean tmp_veh_dtls) {
        this.tmp_veh_dtls = tmp_veh_dtls;
    }

    public boolean isHypothecated() {
        return hypothecated;
    }

    public void setHypothecated(boolean hypothecated) {
        this.hypothecated = hypothecated;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isOtherStateVehicle() {
        return otherStateVehicle;
    }

    public void setOtherStateVehicle(boolean otherStateVehicle) {
        this.otherStateVehicle = otherStateVehicle;
    }

    public boolean isOtherDistrictVehicle() {
        return otherDistrictVehicle;
    }

    public void setOtherDistrictVehicle(boolean otherDistrictVehicle) {
        this.otherDistrictVehicle = otherDistrictVehicle;
    }

    public boolean isBlnScrappedVehicle() {
        return blnScrappedVehicle;
    }

    public void setBlnScrappedVehicle(boolean blnScrappedVehicle) {
        this.blnScrappedVehicle = blnScrappedVehicle;
    }

    public boolean isBlnScrappedVehiclePanel() {
        return blnScrappedVehiclePanel;
    }

    public void setBlnScrappedVehiclePanel(boolean blnScrappedVehiclePanel) {
        this.blnScrappedVehiclePanel = blnScrappedVehiclePanel;
    }

    public String getChasiNo() {
        return chasiNo;
    }

    public void setChasiNo(String chasiNo) {
        this.chasiNo = chasiNo;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public String getRegnType() {
        return regnType;
    }

    public void setRegnType(String regnType) {
        this.regnType = regnType;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public List getListRegnType() {
        return listRegnType;
    }

    public void setListRegnType(List listRegnType) {
        this.listRegnType = listRegnType;
    }

    public List getStateList() {
        return stateList;
    }

    public void setStateList(List stateList) {
        this.stateList = stateList;
    }

    public List getOfficeList() {
        return officeList;
    }

    public void setOfficeList(List officeList) {
        this.officeList = officeList;
    }

    public List getAssignedReasonList() {
        return assignedReasonList;
    }

    public void setAssignedReasonList(List assignedReasonList) {
        this.assignedReasonList = assignedReasonList;
    }

    public boolean isDisAppPrint() {
        return disAppPrint;
    }

    public void setDisAppPrint(boolean disAppPrint) {
        this.disAppPrint = disAppPrint;
    }

    public OtherStateVehDobj getOtherStateVehDobj() {
        return otherStateVehDobj;
    }

    public void setOtherStateVehDobj(OtherStateVehDobj otherStateVehDobj) {
        this.otherStateVehDobj = otherStateVehDobj;
    }

    public boolean isRenderApplNoGenMessage() {
        return renderApplNoGenMessage;
    }

    public void setRenderApplNoGenMessage(boolean renderApplNoGenMessage) {
        this.renderApplNoGenMessage = renderApplNoGenMessage;
    }

    public boolean isRenderPrintDiscalimerButton() {
        return renderPrintDiscalimerButton;
    }

    public void setRenderPrintDiscalimerButton(boolean renderPrintDiscalimerButton) {
        this.renderPrintDiscalimerButton = renderPrintDiscalimerButton;
    }

    public String getApplNoGenMessage() {
        return applNoGenMessage;
    }

    public void setApplNoGenMessage(String applNoGenMessage) {
        this.applNoGenMessage = applNoGenMessage;
    }

    public String getRETEN_REGN_NO() {
        return RETEN_REGN_NO;
    }

    public void setRETEN_REGN_NO(String RETEN_REGN_NO) {
        this.RETEN_REGN_NO = RETEN_REGN_NO;
    }

    public boolean isVehicleHypothecatedDisable() {
        return vehicleHypothecatedDisable;
    }

    public void setVehicleHypothecatedDisable(boolean vehicleHypothecatedDisable) {
        this.vehicleHypothecatedDisable = vehicleHypothecatedDisable;
    }

    public boolean isRenderCDVehicle() {
        return renderCDVehicle;
    }

    public void setRenderCDVehicle(boolean renderCDVehicle) {
        this.renderCDVehicle = renderCDVehicle;
    }

    public EpayDobj getCheckFeeTax() {
        return checkFeeTax;
    }

    public void setCheckFeeTax(EpayDobj checkFeeTax) {
        this.checkFeeTax = checkFeeTax;
    }

    public EpayDobj getCheckPaidFeeTax() {
        return checkPaidFeeTax;
    }

    public void setCheckPaidFeeTax(EpayDobj checkPaidFeeTax) {
        this.checkPaidFeeTax = checkPaidFeeTax;
    }

    public boolean isRenderCheckFeeTax() {
        return renderCheckFeeTax;
    }

    public void setRenderCheckFeeTax(boolean renderCheckFeeTax) {
        this.renderCheckFeeTax = renderCheckFeeTax;
    }

    public boolean isRenderCheckFeeTaxTab() {
        return renderCheckFeeTaxTab;
    }

    public void setRenderCheckFeeTaxTab(boolean renderCheckFeeTaxTab) {
        this.renderCheckFeeTaxTab = renderCheckFeeTaxTab;
    }

    public boolean isRenderAppDisapp() {
        return renderAppDisapp;
    }

    public void setRenderAppDisapp(boolean renderAppDisapp) {
        this.renderAppDisapp = renderAppDisapp;
    }

    public boolean isAutoRunCheckFeeTax() {
        return autoRunCheckFeeTax;
    }

    public void setAutoRunCheckFeeTax(boolean autoRunCheckFeeTax) {
        this.autoRunCheckFeeTax = autoRunCheckFeeTax;
    }

    public boolean isRenderAdvanceNoOption() {
        return renderAdvanceNoOption;
    }

    public void setRenderAdvanceNoOption(boolean renderAdvanceNoOption) {
        this.renderAdvanceNoOption = renderAdvanceNoOption;
    }

    public boolean isRenderScrapCheckBoxOption() {
        return renderScrapCheckBoxOption;
    }

    public void setRenderScrapCheckBoxOption(boolean renderScrapCheckBoxOption) {
        this.renderScrapCheckBoxOption = renderScrapCheckBoxOption;
    }

    public boolean isIsVehicleNoScrapRetain() {
        return isVehicleNoScrapRetain;
    }

    public void setIsVehicleNoScrapRetain(boolean isVehicleNoScrapRetain) {
        this.isVehicleNoScrapRetain = isVehicleNoScrapRetain;
    }

    public boolean isRenderScrapRegnNo() {
        return renderScrapRegnNo;
    }

    public void setRenderScrapRegnNo(boolean renderScrapRegnNo) {
        this.renderScrapRegnNo = renderScrapRegnNo;
    }

    public boolean isRenderExemptionOD() {
        return renderExemptionOD;
    }

    public void setRenderExemptionOD(boolean renderExemptionOD) {
        this.renderExemptionOD = renderExemptionOD;
    }

    public StringBuilder getExemptionOD() {
        return exemptionOD;
    }

    public void setExemptionOD(StringBuilder exemptionOD) {
        this.exemptionOD = exemptionOD;
    }

    public List<OwnerDetailsDobj> getTempRegnDetailsList() {
        return tempRegnDetailsList;
    }

    public void setTempRegnDetailsList(List<OwnerDetailsDobj> tempRegnDetailsList) {
        this.tempRegnDetailsList = tempRegnDetailsList;
    }

    public String getNocDtlsMsg() {
        return nocDtlsMsg;
    }

    public void setNocDtlsMsg(String nocDtlsMsg) {
        this.nocDtlsMsg = nocDtlsMsg;
    }

    public boolean isRenderPartialButton() {
        return renderPartialButton;
    }

    public void setRenderPartialButton(boolean renderPartialButton) {
        this.renderPartialButton = renderPartialButton;
    }

    public boolean isRegnTypeOSorORTO() {
        return regnTypeOSorORTO;
    }

    public void setRegnTypeOSorORTO(boolean regnTypeOSorORTO) {
        this.regnTypeOSorORTO = regnTypeOSorORTO;
    }

    public String getVahanMessages() {
        return vahanMessages;
    }

    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    public String getGetDtlsBtnLabel() {
        return getDtlsBtnLabel;
    }

    public void setGetDtlsBtnLabel(String getDtlsBtnLabel) {
        this.getDtlsBtnLabel = getDtlsBtnLabel;
    }

    public SessionVariables getSessionVariables() {
        return sessionVariables;
    }

    public void setSessionVariables(SessionVariables sessionVariables) {
        this.sessionVariables = sessionVariables;
    }

    public boolean isDocumentUploadShow() {
        return documentUploadShow;
    }

    public void setDocumentUploadShow(boolean documentUploadShow) {
        this.documentUploadShow = documentUploadShow;
    }

    public String getDmsFileUploadUrl() {
        return dmsFileUploadUrl;
    }

    public void setDmsFileUploadUrl(String dmsFileUploadUrl) {
        this.dmsFileUploadUrl = dmsFileUploadUrl;
    }

    public String getSuccessUplodedMsg() {
        return successUplodedMsg;
    }

    public void setSuccessUplodedMsg(String successUplodedMsg) {
        this.successUplodedMsg = successUplodedMsg;
    }

    public boolean isShowBankDetails() {
        return showBankDetails;
    }

    public void setShowBankDetails(boolean showBankDetails) {
        this.showBankDetails = showBankDetails;
    }

    public PendencyBankDobj getBankSubsidyDetail() {
        return bankSubsidyDetail;
    }

    public void setBankSubsidyDetail(PendencyBankDobj bankSubsidyDetail) {
        this.bankSubsidyDetail = bankSubsidyDetail;
    }

    public List getBankNameList() {
        return bankNameList;
    }

    public void setBankNameList(List bankNameList) {
        this.bankNameList = bankNameList;
    }

    public boolean isRenderDocumentUploadBtn() {
        return renderDocumentUploadBtn;
    }

    public void setRenderDocumentUploadBtn(boolean renderDocumentUploadBtn) {
        this.renderDocumentUploadBtn = renderDocumentUploadBtn;
    }

    public boolean isRenderTempStateOffPanel() {
        return renderTempStateOffPanel;
    }

    public void setRenderTempStateOffPanel(boolean renderTempStateOffPanel) {
        this.renderTempStateOffPanel = renderTempStateOffPanel;
    }

    public boolean isRenderVltdDialog() {
        return renderVltdDialog;
    }

    public void setRenderVltdDialog(boolean renderVltdDialog) {
        this.renderVltdDialog = renderVltdDialog;
    }

    public boolean isRenderDMSMesgDialog() {
        return renderDMSMesgDialog;
    }

    public void setRenderDMSMesgDialog(boolean renderDMSMesgDialog) {
        this.renderDMSMesgDialog = renderDMSMesgDialog;
    }

    public boolean isRenderCheckFeeTaxButton() {
        return renderCheckFeeTaxButton;
    }

    public void setRenderCheckFeeTaxButton(boolean renderCheckFeeTaxButton) {
        this.renderCheckFeeTaxButton = renderCheckFeeTaxButton;
    }

    public boolean isRenderOwnerChoiceNoPanel() {
        return renderOwnerChoiceNoPanel;
    }

    public void setRenderOwnerChoiceNoPanel(boolean renderOwnerChoiceNoPanel) {
        this.renderOwnerChoiceNoPanel = renderOwnerChoiceNoPanel;
    }

    public String getVehicleRegnPrefix() {
        return vehicleRegnPrefix;
    }

    public void setVehicleRegnPrefix(String vehicleRegnPrefix) {
        this.vehicleRegnPrefix = vehicleRegnPrefix;
    }

    public String getFacesMessage() {
        return facesMessage;
    }

    public void setFacesMessage(String facesMessage) {
        this.facesMessage = facesMessage;
    }

    public String getPending() {
        return pending;
    }

    public void setPending(String pending) {
        this.pending = pending;
    }

    public boolean isExShoowroomPriceDisable() {
        return exShoowroomPriceDisable;
    }

    public void setExShoowroomPriceDisable(boolean exShoowroomPriceDisable) {
        this.exShoowroomPriceDisable = exShoowroomPriceDisable;
    }

    public String getPendingAppls() {
        return pendingAppls;
    }

    public void setPendingAppls(String pendingAppls) {
        this.pendingAppls = pendingAppls;
    }

    public boolean isHomoEngValidate() {
        return homoEngValidate;
    }

    public void setHomoEngValidate(boolean homoEngValidate) {
        this.homoEngValidate = homoEngValidate;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public boolean isIsValidChassiNo() {
        return isValidChassiNo;
    }

    public void setIsValidChassiNo(boolean isValidChassiNo) {
        this.isValidChassiNo = isValidChassiNo;
    }

    public String getOffCdLabel() {
        return offCdLabel;
    }

    public void setOffCdLabel(String offCdLabel) {
        this.offCdLabel = offCdLabel;
    }

    public String getOffCdFromLabel() {
        return offCdFromLabel;
    }

    public void setOffCdFromLabel(String offCdFromLabel) {
        this.offCdFromLabel = offCdFromLabel;
    }

    public String getStLabel() {
        return stLabel;
    }

    public void setStLabel(String stLabel) {
        this.stLabel = stLabel;
    }

    public String getStFromLabel() {
        return stFromLabel;
    }

    public void setStFromLabel(String stFromLabel) {
        this.stFromLabel = stFromLabel;
    }

    public String getDealerCd() {
        return dealerCd;
    }

    public void setDealerCd(String dealerCd) {
        this.dealerCd = dealerCd;
    }

    public Long getUser_cd() {
        return user_cd;
    }

    public void setUser_cd(Long user_cd) {
        this.user_cd = user_cd;
    }

    public boolean isDisableCurrentState() {
        return disableCurrentState;
    }

    public void setDisableCurrentState(boolean disableCurrentState) {
        this.disableCurrentState = disableCurrentState;
    }

    public boolean isEnableDisableTempRegnDetails() {
        return enableDisableTempRegnDetails;
    }

    /**
     * @return the renderFasTagDialog
     */
    public boolean isRenderFasTagDialog() {
        return renderFasTagDialog;
    }

    /**
     * @param renderFasTagDialog the renderFasTagDialog to set
     */
    public void setRenderFasTagDialog(boolean renderFasTagDialog) {
        this.renderFasTagDialog = renderFasTagDialog;
    }

    public boolean isIsFasTagInstalled() {
        return isFasTagInstalled;
    }

    public void setIsFasTagInstalled(boolean isFasTagInstalled) {
        this.isFasTagInstalled = isFasTagInstalled;
    }

    public boolean isRenderFastag() {
        return renderFastag;
    }

    public void setRenderFastag(boolean renderFastag) {
        this.renderFastag = renderFastag;
    }

    public boolean isIsTransportVehicle() {
        return isTransportVehicle;
    }

    public void setIsTransportVehicle(boolean isTransportVehicle) {
        this.isTransportVehicle = isTransportVehicle;
    }

    public String getVehRegnPlateColorCode() {
        return vehRegnPlateColorCode;
    }

    public void setVehRegnPlateColorCode(String vehRegnPlateColorCode) {
        this.vehRegnPlateColorCode = vehRegnPlateColorCode;
    }

    public String getFancyRcptNo() {
        return fancyRcptNo;
    }

    public void setFancyRcptNo(String fancyRcptNo) {
        this.fancyRcptNo = fancyRcptNo;
    }
}
