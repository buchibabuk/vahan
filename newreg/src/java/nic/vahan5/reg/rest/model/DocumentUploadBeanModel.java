/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nic.vahan.form.bean.DocumentUploadBean;
import nic.vahan.form.dobj.DocumentDetailsDobj;
import nic.vahan.form.dobj.DscDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDMS;
import nic.vahan.services.VTDocumentModel;

/**
 *
 * @author Bhuvan
 */
public class DocumentUploadBeanModel {

    private String applNo;
    private String dmsFileUploadUrl;
    private String stateCd;
    private int offCd;
    private String userCatg;
    private String successUplodedMsg;
    private String noteMsg;
    private int actionCd;
    private List<VTDocumentModel> docDescrList;
    private String dealerMobileNo;
    private TmConfigurationDMS confgDMS;
    private int purCd;
    private OwnerDetailsDobj ownerDetailsDobj;
    private long userCode;
    private String dmsURL;
    private String remarks;
    private String emailMessage;
    private boolean renderedMailButton;
    private String regnNo, applDt, purposeDescr;
    private boolean uploadOwnerAdminDoc;
    private int totalCountUploadDoc;
    private boolean renderverifyDocUpload;
    //komal work done
    private List<DocumentDetailsDobj> docDetailsList;
    private List<DocumentDetailsDobj> mandatoryList;
    private List<DocumentDetailsDobj> uploadedList;
    private boolean renderApplicationInputPanel;
    private boolean renderApplicatioDetailsAndCarouselPanel;
    private boolean showFileFlowBtn;
    private String displayVerifyBtnText;
    private DocumentDetailsDobj selectedDoc;
    private int index;
    private boolean showNextBtn;
    private boolean showPrevBtn;
    private String printImageNo;
    private String ownerDetails;
    private boolean showDigitalSignPanel;
    private boolean pendingWorkFlowCall;
    private boolean showHomeBtn;
    private String noteMessasge = " .png, .jpeg, .jpg, .pdf";
    // Digital Signature related
    private String certificatexml;
    private List<DscDobj> dscServiceCertificateList;
    private String registrationxml;
    private String registrationResponseXml;
    private String pdfSignXML;
    private String pdfSignResponseXML;
    private Map<String, Object> pdfSignxML;
    private boolean dscConnected;
    private boolean showDigitalSignLabel;
    private boolean renderApiBasedDMSDocPanel;
    private boolean renderUiBasedDMSDocPanel;
    private DscDobj hiddenDscDobj;
    private boolean digitalSignAllowStateWise;
    private int ownerCode;
    // Created for REST
    private ArrayList<Status_dobj> applStatus;

    public DocumentUploadBeanModel() {
    }

    public DocumentUploadBeanModel(DocumentUploadBean documentUploadBean) {
        this.applNo = documentUploadBean.getApplNo();
        this.dmsFileUploadUrl = documentUploadBean.getDmsFileUploadUrl();
//        this.stateCd = documentUploadBean.getStateCd();
//        this.offCd = documentUploadBean.getOffCd();
        this.userCatg = documentUploadBean.getUserCatg();
        this.successUplodedMsg = documentUploadBean.getSuccessUplodedMsg();
        this.noteMsg = documentUploadBean.getNoteMsg();
        this.actionCd = documentUploadBean.getActionCd();
        this.docDescrList = documentUploadBean.getDocDescrList();
        this.dealerMobileNo = documentUploadBean.getDealerMobileNo();
//        this.confgDMS = documentUploadBean.getConfgDMS();
//        this.purCd = documentUploadBean.getPurCd();
//        this.ownerDetailsDobj = documentUploadBean.getOwnerDetailsDobj();
//        this.userCode = documentUploadBean.getUserCode();
        this.dmsURL = documentUploadBean.getDmsURL();
        this.remarks = documentUploadBean.getRemarks();
        this.emailMessage = documentUploadBean.getEmailMessage();
        this.renderedMailButton = documentUploadBean.isRenderedMailButton();
        this.regnNo = documentUploadBean.getRegnNo();
        // Changes @24Aug2020
        this.applDt = documentUploadBean.getApplDt();
        this.purposeDescr = documentUploadBean.getPurposeDescr();
        this.uploadOwnerAdminDoc = documentUploadBean.isUploadOwnerAdminDoc();
        this.totalCountUploadDoc = documentUploadBean.getTotalCountUploadDoc();
        this.renderverifyDocUpload = documentUploadBean.isRenderverifyDocUpload();
        this.docDetailsList = documentUploadBean.getDocDetailsList();
        this.mandatoryList = documentUploadBean.getMandatoryList();
        this.uploadedList = documentUploadBean.getUploadedList();
        this.renderApplicationInputPanel = documentUploadBean.isRenderApplicationInputPanel();
        this.renderApplicatioDetailsAndCarouselPanel = documentUploadBean.isRenderApplicatioDetailsAndCarouselPanel();
        this.showFileFlowBtn = documentUploadBean.isShowFileFlowBtn();
        this.displayVerifyBtnText = documentUploadBean.getDisplayVerifyBtnText();
        this.selectedDoc = documentUploadBean.getSelectedDoc();
        this.index = documentUploadBean.getIndex();
        this.showNextBtn = documentUploadBean.isShowNextBtn();
        this.showPrevBtn = documentUploadBean.isShowPrevBtn();
        this.printImageNo = documentUploadBean.getPrintImageNo();
        this.ownerDetails = documentUploadBean.getOwnerDetails();
        this.showDigitalSignPanel = documentUploadBean.isShowDigitalSignPanel();
        this.pendingWorkFlowCall = documentUploadBean.isPendingWorkFlowCall();
        this.showHomeBtn = documentUploadBean.isShowHomeBtn();
        this.certificatexml = documentUploadBean.getCertificatexml();
        this.dscServiceCertificateList = documentUploadBean.getDscServiceCertificateList();
        this.registrationxml = documentUploadBean.getRegistrationxml();
        this.registrationResponseXml = documentUploadBean.getRegistrationResponseXml();
        this.pdfSignXML = documentUploadBean.getPdfSignXML();
        this.pdfSignResponseXML = documentUploadBean.getPdfSignResponseXML();
        this.pdfSignxML = documentUploadBean.getPdfSignxML();
        this.dscConnected = documentUploadBean.isDscConnected();
        this.showDigitalSignLabel = documentUploadBean.isShowDigitalSignLabel();
        this.renderApiBasedDMSDocPanel = documentUploadBean.isRenderApiBasedDMSDocPanel();
        this.renderUiBasedDMSDocPanel = documentUploadBean.isRenderUiBasedDMSDocPanel();
        this.hiddenDscDobj = documentUploadBean.getHiddenDscDobj();
        this.digitalSignAllowStateWise = documentUploadBean.isDigitalSignAllowStateWise();
        this.ownerCode = documentUploadBean.getOwnerCode();
    }    

    public ArrayList<Status_dobj> getApplStatus() {
        return applStatus;
    }

    public void setApplStatus(ArrayList<Status_dobj> applStatus) {
        this.applStatus = applStatus;
    }

    public boolean isRenderedMailButton() {
        return renderedMailButton;
    }

    public void setRenderedMailButton(boolean renderedMailButton) {
        this.renderedMailButton = renderedMailButton;
    }

    public String getEmailMessage() {
        return emailMessage;
    }

    public void setEmailMessage(String emailMessage) {
        this.emailMessage = emailMessage;
    }
    private String docStatus = null;
    private int fileCount = 0;

    public String getDocStatus() {
        return docStatus;
    }

    public void setDocStatus(String docStatus) {
        this.docStatus = docStatus;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    @Override
    public String toString() {
        return "DocumentUpoadBeanModel{" + "applNo=" + applNo + ", dmsFileUploadUrl=" + dmsFileUploadUrl + ", stateCd=" + stateCd + ", offCd=" + offCd + ", userCatg=" + userCatg + ", successUplodedMsg=" + successUplodedMsg + ", noteMsg=" + noteMsg + ", actionCd=" + actionCd + ", docDescrList=" + docDescrList + ", dealerMobileNo=" + dealerMobileNo + ", confgDMS=" + confgDMS + ", purCd=" + purCd + ", ownerDetailsDobj=" + ownerDetailsDobj + ", userCode=" + userCode + ", dmsURL=" + dmsURL + ", remarks=" + remarks + '}';
    }

    public String getDmsFileUploadUrl() {
        return dmsFileUploadUrl;
    }

    public void setDmsFileUploadUrl(String dmsFileUploadUrl) {
        this.dmsFileUploadUrl = dmsFileUploadUrl;
    }

    public String getStateCd() {
        return stateCd;
    }

    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    public int getOffCd() {
        return offCd;
    }

    public void setOffCd(int offCd) {
        this.offCd = offCd;
    }

    public String getUserCatg() {
        return userCatg;
    }

    public void setUserCatg(String userCatg) {
        this.userCatg = userCatg;
    }

    public String getSuccessUplodedMsg() {
        return successUplodedMsg;
    }

    public void setSuccessUplodedMsg(String successUplodedMsg) {
        this.successUplodedMsg = successUplodedMsg;
    }

    public String getNoteMsg() {
        return noteMsg;
    }

    public void setNoteMsg(String noteMsg) {
        this.noteMsg = noteMsg;
    }

    public int getActionCd() {
        return actionCd;
    }

    public void setActionCd(int actionCd) {
        this.actionCd = actionCd;
    }

    public List<VTDocumentModel> getDocDescrList() {
        return docDescrList;
    }

    public void setDocDescrList(List<VTDocumentModel> docDescrList) {
        this.docDescrList = docDescrList;
    }

    public String getDealerMobileNo() {
        return dealerMobileNo;
    }

    public void setDealerMobileNo(String dealerMobileNo) {
        this.dealerMobileNo = dealerMobileNo;
    }

    public TmConfigurationDMS getConfgDMS() {
        return confgDMS;
    }

    public void setConfgDMS(TmConfigurationDMS confgDMS) {
        this.confgDMS = confgDMS;
    }

    public int getPurCd() {
        return purCd;
    }

    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }

    public OwnerDetailsDobj getOwnerDetailsDobj() {
        return ownerDetailsDobj;
    }

    public void setOwnerDetailsDobj(OwnerDetailsDobj ownerDetailsDobj) {
        this.ownerDetailsDobj = ownerDetailsDobj;
    }

    public long getUserCode() {
        return userCode;
    }

    public void setUserCode(long userCode) {
        this.userCode = userCode;
    }

    public String getDmsURL() {
        return dmsURL;
    }

    public void setDmsURL(String dmsURL) {
        this.dmsURL = dmsURL;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public String getApplDt() {
        return applDt;
    }

    public void setApplDt(String applDt) {
        this.applDt = applDt;
    }

    public String getPurposeDescr() {
        return purposeDescr;
    }

    public void setPurposeDescr(String purposeDescr) {
        this.purposeDescr = purposeDescr;
    }

    public boolean isUploadOwnerAdminDoc() {
        return uploadOwnerAdminDoc;
    }

    public void setUploadOwnerAdminDoc(boolean uploadOwnerAdminDoc) {
        this.uploadOwnerAdminDoc = uploadOwnerAdminDoc;
    }

    public int getTotalCountUploadDoc() {
        return totalCountUploadDoc;
    }

    public void setTotalCountUploadDoc(int totalCountUploadDoc) {
        this.totalCountUploadDoc = totalCountUploadDoc;
    }

    public boolean isRenderverifyDocUpload() {
        return renderverifyDocUpload;
    }

    public void setRenderverifyDocUpload(boolean renderverifyDocUpload) {
        this.renderverifyDocUpload = renderverifyDocUpload;
    }

    public List<DocumentDetailsDobj> getDocDetailsList() {
        return docDetailsList;
    }

    public void setDocDetailsList(List<DocumentDetailsDobj> docDetailsList) {
        this.docDetailsList = docDetailsList;
    }

    public List<DocumentDetailsDobj> getMandatoryList() {
        return mandatoryList;
    }

    public void setMandatoryList(List<DocumentDetailsDobj> mandatoryList) {
        this.mandatoryList = mandatoryList;
    }

    public List<DocumentDetailsDobj> getUploadedList() {
        return uploadedList;
    }

    public void setUploadedList(List<DocumentDetailsDobj> uploadedList) {
        this.uploadedList = uploadedList;
    }

    public boolean isRenderApplicationInputPanel() {
        return renderApplicationInputPanel;
    }

    public void setRenderApplicationInputPanel(boolean renderApplicationInputPanel) {
        this.renderApplicationInputPanel = renderApplicationInputPanel;
    }

    public boolean isRenderApplicatioDetailsAndCarouselPanel() {
        return renderApplicatioDetailsAndCarouselPanel;
    }

    public void setRenderApplicatioDetailsAndCarouselPanel(boolean renderApplicatioDetailsAndCarouselPanel) {
        this.renderApplicatioDetailsAndCarouselPanel = renderApplicatioDetailsAndCarouselPanel;
    }

    public boolean isShowFileFlowBtn() {
        return showFileFlowBtn;
    }

    public void setShowFileFlowBtn(boolean showFileFlowBtn) {
        this.showFileFlowBtn = showFileFlowBtn;
    }

    public String getDisplayVerifyBtnText() {
        return displayVerifyBtnText;
    }

    public void setDisplayVerifyBtnText(String displayVerifyBtnText) {
        this.displayVerifyBtnText = displayVerifyBtnText;
    }

    public DocumentDetailsDobj getSelectedDoc() {
        return selectedDoc;
    }

    public void setSelectedDoc(DocumentDetailsDobj selectedDoc) {
        this.selectedDoc = selectedDoc;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isShowNextBtn() {
        return showNextBtn;
    }

    public void setShowNextBtn(boolean showNextBtn) {
        this.showNextBtn = showNextBtn;
    }

    public boolean isShowPrevBtn() {
        return showPrevBtn;
    }

    public void setShowPrevBtn(boolean showPrevBtn) {
        this.showPrevBtn = showPrevBtn;
    }

    public String getPrintImageNo() {
        return printImageNo;
    }

    public void setPrintImageNo(String printImageNo) {
        this.printImageNo = printImageNo;
    }

    public String getOwnerDetails() {
        return ownerDetails;
    }

    public void setOwnerDetails(String ownerDetails) {
        this.ownerDetails = ownerDetails;
    }

    public boolean isShowDigitalSignPanel() {
        return showDigitalSignPanel;
    }

    public void setShowDigitalSignPanel(boolean showDigitalSignPanel) {
        this.showDigitalSignPanel = showDigitalSignPanel;
    }

    public boolean isPendingWorkFlowCall() {
        return pendingWorkFlowCall;
    }

    public void setPendingWorkFlowCall(boolean pendingWorkFlowCall) {
        this.pendingWorkFlowCall = pendingWorkFlowCall;
    }

    public boolean isShowHomeBtn() {
        return showHomeBtn;
    }

    public void setShowHomeBtn(boolean showHomeBtn) {
        this.showHomeBtn = showHomeBtn;
    }

    public String getNoteMessasge() {
        return noteMessasge;
    }

    public void setNoteMessasge(String noteMessasge) {
        this.noteMessasge = noteMessasge;
    }

    public String getCertificatexml() {
        return certificatexml;
    }

    public void setCertificatexml(String certificatexml) {
        this.certificatexml = certificatexml;
    }

    public List<DscDobj> getDscServiceCertificateList() {
        return dscServiceCertificateList;
    }

    public void setDscServiceCertificateList(List<DscDobj> dscServiceCertificateList) {
        this.dscServiceCertificateList = dscServiceCertificateList;
    }

    public String getRegistrationxml() {
        return registrationxml;
    }

    public void setRegistrationxml(String registrationxml) {
        this.registrationxml = registrationxml;
    }

    public String getRegistrationResponseXml() {
        return registrationResponseXml;
    }

    public void setRegistrationResponseXml(String registrationResponseXml) {
        this.registrationResponseXml = registrationResponseXml;
    }

    public String getPdfSignXML() {
        return pdfSignXML;
    }

    public void setPdfSignXML(String pdfSignXML) {
        this.pdfSignXML = pdfSignXML;
    }

    public String getPdfSignResponseXML() {
        return pdfSignResponseXML;
    }

    public void setPdfSignResponseXML(String pdfSignResponseXML) {
        this.pdfSignResponseXML = pdfSignResponseXML;
    }

    public Map<String, Object> getPdfSignxML() {
        return pdfSignxML;
    }

    public void setPdfSignxML(Map<String, Object> pdfSignxML) {
        this.pdfSignxML = pdfSignxML;
    }

    public boolean isDscConnected() {
        return dscConnected;
    }

    public void setDscConnected(boolean dscConnected) {
        this.dscConnected = dscConnected;
    }

    public boolean isShowDigitalSignLabel() {
        return showDigitalSignLabel;
    }

    public void setShowDigitalSignLabel(boolean showDigitalSignLabel) {
        this.showDigitalSignLabel = showDigitalSignLabel;
    }

    public boolean isRenderApiBasedDMSDocPanel() {
        return renderApiBasedDMSDocPanel;
    }

    public void setRenderApiBasedDMSDocPanel(boolean renderApiBasedDMSDocPanel) {
        this.renderApiBasedDMSDocPanel = renderApiBasedDMSDocPanel;
    }

    public boolean isRenderUiBasedDMSDocPanel() {
        return renderUiBasedDMSDocPanel;
    }

    public void setRenderUiBasedDMSDocPanel(boolean renderUiBasedDMSDocPanel) {
        this.renderUiBasedDMSDocPanel = renderUiBasedDMSDocPanel;
    }
}
