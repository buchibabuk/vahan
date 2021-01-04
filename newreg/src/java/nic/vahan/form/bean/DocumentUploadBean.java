/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.nio.file.Files;
import app.eoffice.dsc.service.DscService;
import app.eoffice.dsc.util.XmlUtil;
import app.eoffice.dsc.xml.common.Revocation;
import app.eoffice.dsc.xml.request.RevocationRequest;
import app.eoffice.dsc.xml.response.CertListResponse;
import app.eoffice.dsc.xml.response.ChainCertificates;
import app.eoffice.dsc.xml.response.RegistrationResponse;
import app.eoffice.dsc.xml.response.PdfSignResp;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.DocumentDetailsDobj;
import nic.vahan.form.dobj.DscDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDMS;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.DocumentUploadImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import nic.vahan.server.mail.MailSender;
import nic.vahan.services.DmsDocCheckUtils;
import nic.vahan.services.VTDocumentModel;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
//import org.primefaces.context.RequestContext;
import nic.vahan.form.impl.DscRegistrationImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.services.clients.dobj.DmsFileDetails;
import nic.vahan.services.clients.dobj.NonUploadedList;
import nic.vahan.services.clients.dobj.RequestDto;
import nic.vahan.services.clients.dobj.ResponseDocument;
import nic.vahan.services.clients.dobj.SubCategoryMasterData;
import nic.vahan.services.clients.dobj.SubCategoryMasterDataList;
import nic.vahan.services.clients.dobj.UploadedList;
import org.primefaces.model.UploadedFile;
import org.apache.commons.codec.binary.Base64;
import java.util.Calendar;
import java.util.Date;
import org.primefaces.event.FileUploadEvent;
import nic.java.util.DateUtils;
import nic.vahan.form.dobj.AuctionDobj;
import nic.vahan.form.impl.AuctionImpl;

@ManagedBean(name = "documentUploadBean")
@ViewScoped
public class DocumentUploadBean implements Serializable {

    private String applNo;
    private String dmsFileUploadUrl;
    private String stateCd;
    private int offCd;
    private String userCatg;
    private String successUplodedMsg;
    private String noteMsg;
    private int actionCd;
    private boolean applFieldDisabled = false;
    private List<VTDocumentModel> docDescrList = null;
    private SessionVariables sessionVariables = null;
    private static Logger LOGGER = Logger.getLogger(DocumentUploadBean.class);
    private String redirectTo;
    private String dmsDbLink;
    private boolean documentUploadShow = false;
    private boolean renderedMailButton = false;
    private boolean renderDocumentUploadBtn = false;
    private String emailId;
    private String emailSubject = "Modify Rejected Documents against the Application no : ";
    private String emailMessage = "";
    private String dealerMobileNo;
    private TmConfigurationDMS confgDMS = null;
    private int purCd = 0;
    private OwnerDetailsDobj ownerDetailsDobj = null;
    private long userCode = 0l;
    private boolean renderDocumentPendingBtn = true;
    private String dmsURL, remarks = null;
    private String regnNo, applDt, purposeDescr;
    private boolean uploadOwnerAdminDoc;
    private int totalCountUploadDoc = 0;
    private boolean renderverifyDocUpload;
    //komal work done
    private List<DocumentDetailsDobj> docDetailsList = new ArrayList<>();
    private List<DocumentDetailsDobj> mandatoryList = new ArrayList<>();
    private List<DocumentDetailsDobj> uploadedList = new ArrayList<>();
    private boolean renderApplicationInputPanel;
    private boolean renderApplicatioDetailsAndCarouselPanel;
    private boolean showFileFlowBtn;
    private String displayVerifyBtnText = "";
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
    private List<DscDobj> dscServiceCertificateList = null;
    private String registrationxml;
    private String registrationResponseXml;
    private String pdfSignXML;
    private String pdfSignResponseXML;
    private Map<String, Object> pdfSignxML;
    private boolean dscConnected = false;
    private boolean showDigitalSignLabel;
    private boolean renderApiBasedDMSDocPanel;
    private boolean renderUiBasedDMSDocPanel;
    private DscDobj hiddenDscDobj;
    private boolean digitalSignAllowStateWise = false;
    private int ownerCode;

    public DocumentUploadBean() {
        try {
            if (Util.getTmConfiguration() != null && Util.getTmConfiguration().getTmConfigDmsDobj() != null) {
                confgDMS = Util.getTmConfiguration().getTmConfigDmsDobj();
            } else {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
            if (confgDMS != null) {
                redirectTo = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("redirectTo");
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("redirectTo");
                if (!CommonUtils.isNullOrBlank(redirectTo) && "documentsStatus".equals(redirectTo) || actionCd == TableConstants.TM_ROLE_DEALER_NEW_APPL) {
                    return;
                }
                sessionVariables = new SessionVariables();
                if (sessionVariables == null
                        || sessionVariables.getStateCodeSelected() == null
                        || sessionVariables.getOffCodeSelected() == 0
                        || sessionVariables.getUserCatgForLoggedInUser() == null
                        || sessionVariables.getActionCodeSelected() == 0
                        || sessionVariables.getSelectedWork() == null) {
                    throw new VahanException(TableConstants.SomthingWentWrong);
                } else {
                    stateCd = sessionVariables.getStateCodeSelected();
                    offCd = sessionVariables.getOffCodeSelected();
                    userCatg = sessionVariables.getUserCatgForLoggedInUser();
                    actionCd = sessionVariables.getActionCodeSelected();
                    purCd = sessionVariables.getSelectedWork().getPur_cd();
                    regnNo = sessionVariables.getSelectedWork().getRegn_no();
                    if (!CommonUtils.isNullOrBlank(sessionVariables.getEmpCodeLoggedIn())) {
                        userCode = Long.parseLong(sessionVariables.getEmpCodeLoggedIn());
                    }
                    applDt = sessionVariables.getSelectedWork().getAppl_dt();
                    purposeDescr = sessionVariables.getSelectedWork().getPurpose_descr();
                }
                if (confgDMS.isApiBasedDocUpload()) {
                    renderApiBasedDMSDocPanel = true;
                    renderUiBasedDMSDocPanel = false;

                    if (!TableConstants.DMS_UPLOAD_ACTION_CD.contains(String.valueOf(actionCd))) {
                        return;
                    }
                    if ((confgDMS.getPurCd().contains("," + purCd + ",") || (actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_MODIFY)) && (confgDMS.getDocUploadAllotedOff().contains("ALL") || confgDMS.getDocUploadAllotedOff().contains("," + offCd + ","))) {
                        if (actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_MODIFY) {
                            setApplFieldDisabled(false);
                            renderApplicationInputPanel = true;
                            if (confgDMS.isDigitalSignAllowStateWise()) {
                                this.setDigitalSignAllowStateWise(true);
                                if (confgDMS.getDigitalSignAllowOffWise().contains("," + Util.getUserLoginOffCode() + ",") && Util.getUserCategory().equals(TableConstants.USER_CATG_DEALER)) {
                                    this.setShowDigitalSignLabel(true);
                                    noteMessasge = " .pdf";
                                    boolean isDataExist = ServerUtil.insertIntoDscRegistrationHistory();
                                    if (isDataExist) {
                                        this.setDscConnected(true);
                                        this.setShowDigitalSignPanel(false);
                                    } else {
                                        this.setDscConnected(false);
                                        this.setShowDigitalSignPanel(true);
                                    }
                                }
                            }
                        } else {
                            if (sessionVariables.getSelectedWork() != null && !CommonUtils.isNullOrBlank(sessionVariables.getSelectedWork().getAppl_no())) {
                                // to show pending action
                                redirectTo = "workbench";
                                applNo = sessionVariables.getSelectedWork().getAppl_no();
                                if (actionCd == TableConstants.TM_ROLE_UPLOAD_MODIFY_DOCUMENT || actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_UPLOAD) {
                                    pendingWorkFlowCall = true;
                                } else if (purCd == TableConstants.ADMIN_OWNER_DATA_CHANGE && (actionCd == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_VERIFY || actionCd == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_APPROVAL)) {
                                    if (confgDMS.isDigitalSignAllowStateWise()) {
                                        this.setDigitalSignAllowStateWise(true);
                                    }
                                    this.callDocumentApiToGetUploadedDocList();
                                    if (uploadedList != null && !uploadedList.isEmpty()) {
                                        this.callDocumentApiToGetDocListToBeUploadAtVerification();
                                        if (docDetailsList != null && !docDetailsList.isEmpty()) {
                                            setDocumentUploadShow(true);
                                            this.setDocumentObjectToDisplay();
                                            this.setOwnerDetailsValue();
                                            if (confgDMS.getVerfyActionCd().contains("," + actionCd + ",")) {
                                                this.setDisplayVerifyBtnText("Verify");
                                            } else if (confgDMS.getApproveActionCd().contains("," + actionCd + ",")) {
                                                this.setDisplayVerifyBtnText("Approve");
                                            } else if (confgDMS.getTempApproveActionCd().contains("," + actionCd + ",")) {
                                                this.setDisplayVerifyBtnText("Approve");
                                            }
                                        }
                                    }
                                } else {
                                    // to show modify view verify and approve button   
                                    if (confgDMS.isDigitalSignAllowStateWise()) {
                                        this.setDigitalSignAllowStateWise(true);
                                    }
                                    this.callDocumentApiToGetUploadedDocList();
                                    if (uploadedList != null && !uploadedList.isEmpty()) {
                                        setDocumentUploadShow(true);
                                        this.setVerifyApprovalDocumentObjectToDisplay();
                                        this.setOwnerDetailsValue();
                                        if (confgDMS.getVerfyActionCd().contains("," + actionCd + ",")) {
                                            this.setDisplayVerifyBtnText("Verify");
                                        } else if (confgDMS.getApproveActionCd().contains("," + actionCd + ",")) {
                                            this.setDisplayVerifyBtnText("Approve");
                                        } else if (confgDMS.getTempApproveActionCd().contains("," + actionCd + ",")) {
                                            this.setDisplayVerifyBtnText("Approve");
                                        }
                                    }
                                }
                            } else {
                                throw new VahanException("Invalid application no.");
                            }
                        }
                        if (purCd == TableConstants.ADMIN_OWNER_DATA_CHANGE && actionCd == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_VERIFY) {
                            totalCountUploadDoc = DmsDocCheckUtils.getTotalDocumentCount(stateCd, purCd);
                        }
                    }
                } else {
                    dmsURL = ServerUtil.getVahanPgiUrl(TableConstants.DMS_URL);
                    renderApiBasedDMSDocPanel = false;
                    renderUiBasedDMSDocPanel = true;
                    if (confgDMS.getPurCd().contains("," + purCd + ",") && (confgDMS.getDocUploadAllotedOff().contains("ALL") || confgDMS.getDocUploadAllotedOff().contains("," + offCd + ","))) {
                        if (sessionVariables.getSelectedWork() != null && !CommonUtils.isNullOrBlank(sessionVariables.getSelectedWork().getAppl_no())) {
                            applNo = sessionVariables.getSelectedWork().getAppl_no();
                            noteMsg = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("message");
                            redirectTo = "workbench";
                            setApplFieldDisabled(true);
                            docDescrList = DmsDocCheckUtils.getUploadedDocumentList(applNo);
                            if (docDescrList != null && !docDescrList.isEmpty()) {
                                setDocumentUploadShow(true);
                                if (CommonUtils.isNullOrBlank(dmsURL)) {
                                    if (actionCd == TableConstants.TM_ROLE_UPLOAD_MODIFY_DOCUMENT || actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_UPLOAD) {
                                        renderDocumentPendingBtn = false;
                                    }
                                }

                            }
                        }
                        if (TableConstants.USER_CATG_OFF_STAFF.equals(userCatg) && confgDMS.getUploadActionCd().contains("," + actionCd + ",") && !confgDMS.isDocsUploadAtOffice()) {
                            renderDocumentUploadBtn = true;
                        }
                        if (purCd == TableConstants.ADMIN_OWNER_DATA_CHANGE && actionCd == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_VERIFY) {
                            totalCountUploadDoc = DmsDocCheckUtils.getTotalDocumentCount(stateCd, purCd);
                        }
                    }
                }
            }
        } catch (VahanException ve) {
            JSFUtils.showMessagesInDialog("Alert !!!", ve.getMessage(), FacesMessage.SEVERITY_WARN);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!!", TableConstants.SomthingWentWrong, FacesMessage.SEVERITY_WARN);
        }
    }

    public void callMethodForPendingWork() {
        try {
            noteMsg = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("message");
            if (confgDMS.isDigitalSignAllowStateWise()) {
                this.setDigitalSignAllowStateWise(true);
                if (confgDMS.getDigitalSignAllowOffWise().contains("," + Util.getUserLoginOffCode() + ",") && Util.getUserCategory().equals(TableConstants.USER_CATG_DEALER)) {
                    this.setShowDigitalSignLabel(true);
                    noteMessasge = " .pdf";
                    boolean isDataExist = ServerUtil.insertIntoDscRegistrationHistory();
                    if (isDataExist) {
                        this.setDscConnected(true);
                        this.setShowDigitalSignPanel(false);
                    } else {
                        this.setDscConnected(false);
                        this.setShowDigitalSignPanel(true);
                    }
                }
            }
            this.validateApplAndFileUpload();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!!", TableConstants.SomthingWentWrong, FacesMessage.SEVERITY_WARN);
        }
    }

    public void validateApplAndFileUpload() {
        int fileCount = 0;
        String documentStauts = "";
        OwnerDetailsDobj ownerDobj = null;
        try {
            if ("documentsStatus".equals(redirectTo)) {
                viewUploadedDocuments();
                return;
            }
            if (renderApiBasedDMSDocPanel) {
                if (actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_MODIFY) {
                    if (confgDMS.isDigitalSignAllowStateWise()) {
                        if (confgDMS.getDigitalSignAllowOffWise().contains("," + Util.getUserLoginOffCode() + ",") && Util.getUserCategory().equals(TableConstants.USER_CATG_DEALER)) {
                            DscDobj dscRegisterObj = new DscRegistrationImpl().fetchDscSignerCertDtls();
                            if (dscRegisterObj == null) {
                                throw new VahanException("First connect to DSC before uploading the document!!!");
                            }
                        }
                    }
                    if ((applNo == null || remarks == null) || (applNo.isEmpty() || remarks.isEmpty())) {
                        throw new VahanException("Application No/Remarks can not be blank!!!");
                    }
                }
            }

            if (!CommonUtils.isNullOrBlank(regnNo) && !regnNo.equals("NEW") && TableConstants.VM_TRANSACTION_MAST_AUCTION == purCd) {
                AuctionDobj auctionDobj = new AuctionImpl().getAuctionDetails(regnNo, applNo);
                if (auctionDobj != null && auctionDobj.getAuctionBy() != null && auctionDobj.getAuctionBy().equals("R")) {
                    List<OwnerDetailsDobj> ownerDetailsDobjList = new OwnerImpl().getOwnerDetailsList(regnNo.trim(), null);
                    if (ownerDetailsDobjList == null || ownerDetailsDobjList.isEmpty()) {
                        throw new VahanException("Invalid Registration Number or Registration No not found in the Record");
                    }
                    if (ownerDetailsDobjList.size() == 1) {
                        ownerDobj = ownerDetailsDobjList.get(0);
                    } else if (ownerDetailsDobjList.size() >= 2) {
                        throw new VahanException("Duplicate Record Found!!!");
                    }
                }
            } else {
                ownerDetailsDobj = new DocumentUploadImpl().getVaOwnerDetailsForDocumentUpload(applNo.toUpperCase(), stateCd, null);
                if (ownerDetailsDobj == null && TableConstants.ADMIN_OWNER_DATA_CHANGE == purCd) {
                    ownerDetailsDobj = new DocumentUploadImpl().getVaOwnerTempDtlsForDocUpload(applNo.toUpperCase(), stateCd, null);
                }
            }

            if (renderApiBasedDMSDocPanel) {
                if (actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_MODIFY) {
                    if (ownerDetailsDobj.getOff_cd() != offCd) {
                        throw new VahanException("Application pending Office is different from Selected Office.Can't Modify!!!");
                    }
                }
            }

            ArrayList<Status_dobj> applStatus = ServerUtil.applicationStatusByApplNo(applNo, stateCd);
            if (applStatus != null && !applStatus.isEmpty() && (ownerDetailsDobj != null || ownerDobj != null || TableConstants.VM_TRANSACTION_MAST_AUCTION == purCd)) {
                if (applStatus.size() == 2) {
                    purCd = TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE;
                } else {
                    purCd = applStatus.get(0).getPur_cd();
                }
                if (stateCd != null && stateCd.equalsIgnoreCase("OR")) {
                    if (TableConstants.VM_TRANSACTION_MAST_AUCTION == purCd) {
                        if (ownerDobj != null) {
                            ownerCode = ownerDobj.getOwner_cd();
                        }
                    } else {
                        if (ownerDetailsDobj != null) {
                            ownerCode = ownerDetailsDobj.getOwner_cd();
                        }
                    }
                }
                if (actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_MODIFY && renderUiBasedDMSDocPanel) {
                    docDescrList = DmsDocCheckUtils.getUploadedDocumentList(applNo);
                    if (confgDMS != null && confgDMS.isDocsFolwRequired() && (docDescrList == null || docDescrList.isEmpty())) {
                        throw new VahanException("Documents can be uploaded after payment and will come in pending work on Home Page.");
                    }
                } else if (actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_MODIFY && renderApiBasedDMSDocPanel) {
                    regnNo = ownerDetailsDobj.getRegn_no();
                    applDt = applStatus.get(0).getAppl_dt();
                    purposeDescr = applStatus.get(0).getPurCdDescr();
                    this.callDocumentApiToGetUploadedDocList();
                    if (confgDMS != null && confgDMS.isDocsFolwRequired() && (uploadedList == null || uploadedList.isEmpty())) {
                        throw new VahanException("Documents can be uploaded after payment and will come in pending work on Home Page.");
                    }
                }

                String dealerCd = ServerUtil.getDealerCode(userCode, stateCd, offCd);
                if (TableConstants.USER_CATG_DEALER.equals(userCatg)) {
                    if (dealerCd != null && !dealerCd.equals(ownerDetailsDobj.getDealer_cd()) || (TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE != purCd && TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE != purCd)) {
                        throw new VahanException("You are Not Authorised to upload the documents against this application generated by other dealer.");
                    }
                }
                if (renderUiBasedDMSDocPanel) {
                    switch (userCatg) {
                        case TableConstants.USER_CATG_DEALER:
                            if (docDescrList != null && !docDescrList.isEmpty() && docDescrList.size() > 0) {
                                if (confgDMS.getPurCd().contains("," + purCd + ",") || (confgDMS.getPurCd().contains("," + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + ",") && Util.getTmConfiguration().isTempFeeInNewRegis())) {
                                    if (confgDMS.getTempApproveActionCd().contains("," + actionCd + ",")) {
                                        fileCount = docDescrList.size();
                                        documentStauts = TableConstants.DOCUMENT_TEMP_APPROVAL;
                                    } else if (confgDMS.getApproveActionCd().contains("," + actionCd + ",")) {
                                        fileCount = docDescrList.size();
                                        documentStauts = TableConstants.DOCUMENT_RTO_APPROVAL;
                                    }
                                    if (CommonUtils.isNullOrBlank(dmsURL) && CommonUtils.isNullOrBlank(documentStauts)) {
                                        if (actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_MODIFY) {
                                            fileCount = docDescrList.size();
                                            documentStauts = TableConstants.REJECTED_DOCUMENT_MODIFY_STATUS;
                                        } else {
                                            fileCount = docDescrList.size();
                                            documentStauts = TableConstants.DOCUMENT_MODIFY_STATUS;
                                        }
                                    }
                                }
                            }
                            break;
                        case TableConstants.USER_CATG_OFF_STAFF:
                            if (confgDMS.getPurCd().contains("," + purCd + ",") || (confgDMS.getPurCd().contains("," + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + ",") && Util.getTmConfiguration().isTempFeeInNewRegis())) {
                                if (docDescrList != null && !docDescrList.isEmpty() && docDescrList.size() > 0) {
                                    if (confgDMS.getVerfyActionCd().contains("," + actionCd + ",")) {
                                        fileCount = docDescrList.size();
                                        documentStauts = TableConstants.DOCUMENT_RTO_VERIFICATION;
                                    } else if (confgDMS.getApproveActionCd().contains("," + actionCd + ",")) {
                                        fileCount = docDescrList.size();
                                        documentStauts = TableConstants.DOCUMENT_RTO_APPROVAL;
                                    } else if (confgDMS.getTempApproveActionCd().contains("," + actionCd + ",")) {
                                        fileCount = docDescrList.size();
                                        documentStauts = TableConstants.DOCUMENT_TEMP_APPROVAL;
                                    }
                                    if (CommonUtils.isNullOrBlank(dmsURL) && CommonUtils.isNullOrBlank(documentStauts)) {
                                        fileCount = docDescrList.size();
                                        documentStauts = TableConstants.DOCUMENT_MODIFY_STATUS;
                                    }
                                    break;
                                }
                            }
                            break;
                    }
                    if (!CommonUtils.isNullOrBlank(dmsURL)) {
                        dmsFileUploadUrl = ServerUtil.getVahanPgiUrl(TableConstants.DMS_URL);
                        if (!CommonUtils.isNullOrBlank(dmsFileUploadUrl)) {
                            dmsFileUploadUrl = dmsFileUploadUrl.replace("ApplNo", applNo).replace("state_cd", stateCd).replace("regn_no", ownerDetailsDobj.getRegn_no())
                                    .replace("off_name", ServerUtil.getOfficeName(offCd, stateCd)).replace("pur_cd", purCd + "");
                            if (!CommonUtils.isNullOrBlank(documentStauts)) {
                                dmsFileUploadUrl = dmsFileUploadUrl.replace("dms_status", documentStauts);
                            } else {
                                dmsFileUploadUrl = dmsFileUploadUrl.replace("dms_status", TableConstants.DOCUMENT_MODIFY_STATUS);
                            }
                            dmsFileUploadUrl = dmsFileUploadUrl + TableConstants.SECURITY_KEY;
                            PrimeFaces.current().ajax().update("formDocumentUpload:dmsFileUpload_panel");
                            PrimeFaces.current().executeScript("PF('dmsfileUploaded').show();");
                        } else {
                            throw new VahanException("Problem in getting DMS URL.");
                        }
                    } else {
                        if (fileCount > 0 && !documentStauts.equals("")) {
                            dmsFileUploadUrl = ServerUtil.getVahanPgiUrl(TableConstants.DMS_CON_VER);
                            dmsFileUploadUrl = dmsFileUploadUrl.replace("ApplNo", applNo);
                            dmsFileUploadUrl = dmsFileUploadUrl.replace("ApplStatus", documentStauts);
                        } else if (fileCount == 0 && documentStauts.equals("")) {
                            dmsFileUploadUrl = ServerUtil.getVahanPgiUrl(TableConstants.DMS_CON);
                            dmsFileUploadUrl = dmsFileUploadUrl.replace("ApplNo", applNo);
                        }
                        dmsFileUploadUrl = dmsFileUploadUrl + TableConstants.SECURITY_KEY;
                        if (dmsFileUploadUrl != null) {
                            PrimeFaces.current().executeScript("PF('dmsfileUploaded').show();");
                        }
                    }
                } else if (renderApiBasedDMSDocPanel) {
                    this.callDocumentApiToGetDocListToBeUpload();
                    if (docDetailsList != null && !docDetailsList.isEmpty()) {
                        this.setDocumentObjectToDisplay();
                        this.setOwnerDetailsValue();
                        if (actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_MODIFY) {
                            renderApplicationInputPanel = false;
                            renderApplicatioDetailsAndCarouselPanel = true;
                            setShowFileFlowBtn(false);
                            setShowHomeBtn(true);
                        } else if (actionCd == TableConstants.TM_ROLE_UPLOAD_MODIFY_DOCUMENT || actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_UPLOAD) {
                            renderApplicationInputPanel = false;
                            renderApplicatioDetailsAndCarouselPanel = true;
                            setShowFileFlowBtn(true);
                            setShowHomeBtn(false);
                        }
                    } else {
                        throw new VahanException("Unable to get response from DMS.");
                    }
                }
            } else {
                throw new VahanException("Invalid application no. or application no. already approved");
            }
        } catch (VahanException ve) {
            JSFUtils.showMessagesInDialog("Alert !!!", ve.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!!", TableConstants.SomthingWentWrong, FacesMessage.SEVERITY_WARN);
        }
    }

    public void uploadVerifyOwnerAdminDoc() {
        int fileCount = 0;
        String documentStauts = "";
        OwnerDetailsDobj ownerDobj = null;
        try {
            if (docDescrList.size() == totalCountUploadDoc) {
                throw new VahanException("Verify Document has already been Uploaded!");
            }
            if (!CommonUtils.isNullOrBlank(regnNo) && !regnNo.equals("NEW") && TableConstants.VM_TRANSACTION_MAST_AUCTION == purCd) {
                List<OwnerDetailsDobj> ownerDetailsDobjList = new OwnerImpl().getOwnerDetailsList(regnNo.trim(), null);
                if (ownerDetailsDobjList == null || ownerDetailsDobjList.isEmpty()) {
                    throw new VahanException("Invalid Registration Number or Registration No not found in the Record");
                }
                if (ownerDetailsDobjList.size() == 1) {
                    ownerDobj = ownerDetailsDobjList.get(0);
                } else if (ownerDetailsDobjList.size() >= 2) {
                    throw new VahanException("Duplicate Record Found!!!");
                }
            } else {
                ownerDetailsDobj = new DocumentUploadImpl().getVaOwnerDetailsForDocumentUpload(applNo.toUpperCase(), stateCd, null);
                if (ownerDetailsDobj == null && TableConstants.ADMIN_OWNER_DATA_CHANGE == purCd) {
                    ownerDetailsDobj = new DocumentUploadImpl().getVaOwnerTempDtlsForDocUpload(applNo.toUpperCase(), stateCd, null);
                }
            }
            ArrayList<Status_dobj> applStatus = ServerUtil.applicationStatusByApplNo(applNo, stateCd);
            if (applStatus != null && !applStatus.isEmpty() && (ownerDetailsDobj != null || ownerDobj != null)) {
                if (applStatus.size() == 2) {
                    purCd = TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE;
                } else {
                    purCd = applStatus.get(0).getPur_cd();
                }

                String dealerCd = ServerUtil.getDealerCode(userCode, stateCd, offCd);
                if (TableConstants.USER_CATG_DEALER.equals(userCatg)) {
                    if (dealerCd != null && !dealerCd.equals(ownerDetailsDobj.getDealer_cd()) || (TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE != purCd && TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE != purCd)) {
                        throw new VahanException("You are Not Authorised to upload the documents against this application generated by other dealer.");
                    }
                }
                switch (userCatg) {
                    case TableConstants.USER_CATG_DEALER:
                        if (docDescrList != null && !docDescrList.isEmpty() && docDescrList.size() > 0) {
                            if (confgDMS.getPurCd().contains("," + purCd + ",") || (confgDMS.getPurCd().contains("," + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + ",") && Util.getTmConfiguration().isTempFeeInNewRegis())) {
                                if (confgDMS.getTempApproveActionCd().contains("," + actionCd + ",")) {
                                    fileCount = docDescrList.size();
                                    documentStauts = TableConstants.DOCUMENT_TEMP_APPROVAL;
                                } else if (confgDMS.getApproveActionCd().contains("," + actionCd + ",")) {
                                    fileCount = docDescrList.size();
                                    documentStauts = TableConstants.DOCUMENT_RTO_APPROVAL;
                                }
                                if (CommonUtils.isNullOrBlank(dmsURL) && CommonUtils.isNullOrBlank(documentStauts)) {
                                    if (actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_MODIFY) {
                                        fileCount = docDescrList.size();
                                        documentStauts = TableConstants.REJECTED_DOCUMENT_MODIFY_STATUS;
                                    } else {
                                        fileCount = docDescrList.size();
                                        documentStauts = TableConstants.DOCUMENT_MODIFY_STATUS;
                                    }
                                }
                            }
                        }
                        break;
                    case TableConstants.USER_CATG_OFF_STAFF:
                        if (confgDMS.getPurCd().contains("," + purCd + ",") || (confgDMS.getPurCd().contains("," + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + ",") && Util.getTmConfiguration().isTempFeeInNewRegis())) {
                            if (docDescrList != null && !docDescrList.isEmpty() && docDescrList.size() > 0) {
                                if (confgDMS.getVerfyActionCd().contains("," + actionCd + ",")) {
                                    fileCount = docDescrList.size();
                                    documentStauts = TableConstants.DOCUMENT_RTO_VERIFICATION;
                                } else if (confgDMS.getApproveActionCd().contains("," + actionCd + ",")) {
                                    fileCount = docDescrList.size();
                                    documentStauts = TableConstants.DOCUMENT_RTO_APPROVAL;
                                } else if (confgDMS.getTempApproveActionCd().contains("," + actionCd + ",")) {
                                    fileCount = docDescrList.size();
                                    documentStauts = TableConstants.DOCUMENT_TEMP_APPROVAL;
                                }
                                if (CommonUtils.isNullOrBlank(dmsURL) && CommonUtils.isNullOrBlank(documentStauts)) {
                                    fileCount = docDescrList.size();
                                    documentStauts = TableConstants.DOCUMENT_MODIFY_STATUS;
                                }
                                break;
                            }
                        }
                        break;
                }
                dmsFileUploadUrl = ServerUtil.getVahanPgiUrl(TableConstants.DMS_SARTHI_URL);
                if (dmsFileUploadUrl.isEmpty()) {
                    throw new VahanException("Problem in view documents.");
                }
                if (purCd == TableConstants.ADMIN_OWNER_DATA_CHANGE && totalCountUploadDoc != 0 && docDescrList.size() != totalCountUploadDoc) {
                    renderverifyDocUpload = true;
                }
                dmsFileUploadUrl = dmsFileUploadUrl.replace("ApplNo", applNo);
                dmsFileUploadUrl = dmsFileUploadUrl.replace("state_cd", stateCd);
                dmsFileUploadUrl = dmsFileUploadUrl.replace("regn_no", applStatus.get(0).getRegn_no());
                dmsFileUploadUrl = dmsFileUploadUrl.replace("off_name", ServerUtil.getOfficeName(offCd, stateCd));
                dmsFileUploadUrl = dmsFileUploadUrl.replace("pur_cd", 101 + "");
                dmsFileUploadUrl = dmsFileUploadUrl + "&docFrom=R";
                dmsFileUploadUrl = dmsFileUploadUrl + TableConstants.SECURITY_KEY;
                if (dmsFileUploadUrl != null) {
                    PrimeFaces.current().ajax().update("workbench_tabview:doc_list_panel");
                    PrimeFaces.current().ajax().update("workbench_tabview:dmsFileUpload_panel");
                    PrimeFaces.current().executeScript("PF('dmsfileUploaded').show();");
                }
            } else {
                throw new VahanException("Invalid application no. or application no. already approved");
            }
        } catch (VahanException ve) {
            JSFUtils.showMessagesInDialog("Alert !!!", ve.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!!", TableConstants.SomthingWentWrong, FacesMessage.SEVERITY_WARN);
        }
    }

    public void dialogCloseEventListener() {
        emailMessage = "";
        String noteMsg = "<br/> <span style='color:red;font: bold;'>Note:- Please send mail to modify the rejected documents.</span>";
        try {
            if ("documentsStatus".equals(redirectTo)) {
                return;
            }
            if (purCd == TableConstants.ADMIN_OWNER_DATA_CHANGE) {
                docDescrList = DmsDocCheckUtils.getUploadedDocumentList(applNo);
            } else {
                docDescrList = new DocumentUploadImpl().uploadedDocsDetails(applNo, stateCd, offCd);
            }
            if (docDescrList != null && !docDescrList.isEmpty() && docDescrList.size() > 0) {
                switch (userCatg) {
                    case TableConstants.USER_CATG_DEALER:
                        if (confgDMS.getTempApproveActionCd().contains("," + actionCd + ",")) {
                            setSuccessUplodedMsg(TableConstants.DOCUMENTS_APPROVE_SUCCESS + getApplNo() + ".");
                            for (VTDocumentModel docsList : docDescrList) {
                                if (!docsList.isTemp_doc_approved()) {
                                    throw new VahanException(TableConstants.DOCUMENTS_NOT_APPROVE_MESSG + getApplNo() + "");
                                }
                            }
                        } else {
                            if (actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_UPLOAD && confgDMS.isDocsFolwRequired()) {
                                boolean uploadFlag = DocumentUploadImpl.fileFlowAfterDocumentUpload(applNo, actionCd, purCd, remarks);
                                if (uploadFlag) {
                                    setSuccessUplodedMsg(TableConstants.DOCUMENTS_UPLOAD_SUCCESS + getApplNo());
                                } else {
                                    setSuccessUplodedMsg(TableConstants.DOCUMENTS_NOT_UPLOAD_ERR_MESSG + getApplNo() + "");
                                }
                            } else {
                                setSuccessUplodedMsg(TableConstants.DOCUMENTS_UPLOAD_SUCCESS + getApplNo());
                            }
                        }
                        break;
                    case TableConstants.USER_CATG_OFF_STAFF:
                        if (confgDMS.getUploadActionCd().contains("," + actionCd + ",")) {
                            setSuccessUplodedMsg(TableConstants.DOCUMENTS_UPLOAD_SUCCESS + getApplNo());
                        } else if (docDescrList == null || docDescrList.isEmpty()) {
                            setSuccessUplodedMsg(TableConstants.DOCUMENTS_NOT_UPLOAD_ERR_MESSG + getApplNo() + "");
                        } else if (actionCd == TableConstants.TM_ROLE_UPLOAD_MODIFY_DOCUMENT && confgDMS.isDocsUploadAtOffice()) {
                            boolean uploadFlag = DocumentUploadImpl.fileFlowAfterDocumentUpload(applNo, actionCd, purCd, remarks);
                            if (uploadFlag) {
                                setSuccessUplodedMsg(TableConstants.DOCUMENTS_UPLOAD_SUCCESS + getApplNo());
                            } else {
                                setSuccessUplodedMsg(TableConstants.DOCUMENTS_NOT_UPLOAD_ERR_MESSG + getApplNo() + "");
                            }
                        } else if (confgDMS.getVerfyActionCd().contains("," + actionCd + ",")) {
                            setSuccessUplodedMsg(TableConstants.DOCUMENTS_VERIFY_SUCCESS + getApplNo() + ".");
                            for (VTDocumentModel docsList : docDescrList) {
                                if (!docsList.isDoc_verified()) {
                                    successUplodedMsg = TableConstants.DOCUMENTS_NOT_VERIFY_MESSG + getApplNo();
                                    setEmailMessage(docsList.getDoc_desc() + "," + getEmailMessage());
                                    renderedMailButton = true;
                                    successUplodedMsg = successUplodedMsg + "<br/><br/>" + getEmailMessage();
                                }
                            }
                        } else if (confgDMS.getApproveActionCd().contains("," + actionCd + ",")) {
                            setSuccessUplodedMsg(TableConstants.DOCUMENTS_APPROVE_SUCCESS + getApplNo() + ".");
                            for (VTDocumentModel docsList : docDescrList) {
                                if (!docsList.isDoc_approved()) {
                                    successUplodedMsg = TableConstants.DOCUMENTS_NOT_APPROVE_MESSG + getApplNo();
                                    setEmailMessage(docsList.getDoc_desc() + "," + getEmailMessage());
                                    renderedMailButton = true;
                                    successUplodedMsg = successUplodedMsg + "<br/><br/>" + getEmailMessage();
                                }
                            }
                        } else if (confgDMS.getTempApproveActionCd().contains("," + actionCd + ",")) {
                            setSuccessUplodedMsg(TableConstants.DOCUMENTS_APPROVE_SUCCESS + getApplNo() + ".");
                            for (VTDocumentModel docsList : docDescrList) {
                                if (!docsList.isTemp_doc_approved()) {
                                    successUplodedMsg = TableConstants.DOCUMENTS_NOT_APPROVE_MESSG + getApplNo();
                                    setEmailMessage(docsList.getDoc_desc() + "," + getEmailMessage());
                                    renderedMailButton = true;
                                    successUplodedMsg = successUplodedMsg + "<br/><br/>" + getEmailMessage();
                                }
                            }
                        } else {
                            setSuccessUplodedMsg(TableConstants.DOCUMENTS_UPLOAD_SUCCESS + getApplNo() + ".");
                        }
                        break;
                }

            } else {
                setSuccessUplodedMsg(TableConstants.DOCUMENTS_NOT_UPLOAD_ERR_MESSG + getApplNo() + "");
            }
            if (!CommonUtils.isNullOrBlank(successUplodedMsg)) {
                if (purCd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                    renderedMailButton = false;
                } else if (renderedMailButton) {
                    successUplodedMsg = successUplodedMsg + noteMsg;
                }
                if (renderverifyDocUpload) {
                    setSuccessUplodedMsg(TableConstants.DOCUMENTS_UPLOAD_SUCCESS + getApplNo() + ".");

                }

                if (actionCd == TableConstants.TM_ROLE_NEW_APPL) {
                    if (docDescrList != null && !docDescrList.isEmpty()) {
                        documentUploadShow = true;
                    }
                    PrimeFaces.current().ajax().update("uploadDocumentPanel");
                    PrimeFaces.current().executeScript("PF('dialogUploadMsgVar').show();");
                } else {
                    PrimeFaces.current().executeScript("PF('successUploadDialog').show();");
                }
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Alert !!!", e.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!!", TableConstants.SomthingWentWrong, FacesMessage.SEVERITY_WARN);
        }
    }

    public void showSendMailDialog() {
        String docsMsg = "Dear Applicant, Problem in following documents against the Application no " + applNo + ". Please modify these documents -  ";
        try {
            if (!CommonUtils.isNullOrBlank(emailMessage)) {
                String userData[] = ServerUtil.getDealerAdminMailId(applNo, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                setEmailId(userData[0]);
                setDealerMobileNo(userData[1]);
                setEmailMessage(docsMsg + getEmailMessage());
                setEmailSubject(getEmailSubject() + applNo);
                PrimeFaces.current().ajax().update("workbench_tabview:sent_mail_dialog");
                PrimeFaces.current().executeScript("PF('sendMailDialogVar').show();");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage(TableConstants.SomthingWentWrong);
        }
    }

    public void sentMailToUserToModifyDocuments() {
        try {
            if (ownerDetailsDobj != null && ownerDetailsDobj.getOwnerIdentity() != null && ownerDetailsDobj.getOwnerIdentity().getMobile_no() != 0) {
                String mobileNo = String.valueOf(ownerDetailsDobj.getOwnerIdentity().getMobile_no());
                if (!CommonUtils.isNullOrBlank(mobileNo)) {
                    ServerUtil.sendSMS(mobileNo, emailMessage + "Note:- This is system generated SMS don't reply. Please contact to Dealer.");
                }
            }
            if (!CommonUtils.isNullOrBlank(dealerMobileNo)) {
                ServerUtil.sendSMS(getDealerMobileNo(), emailMessage + "Note:- This is system generated SMS don't reply.");
            }
            if (!CommonUtils.isNullOrBlank(emailId)) {
                MailSender sendMail = new MailSender(emailId, emailMessage + "Note:- This is system generated mail don't reply.", emailSubject);
                sendMail.start();
            }
            renderedMailButton = false;
            PrimeFaces.current().ajax().update("workbench_tabview:sent_mail_dialog");
            PrimeFaces.current().executeScript("PF('sendMailDialogVar').hide();");
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info!!!", "Mail has been sent to dealer to modify documents."));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Error in sending mail.", null, FacesMessage.SEVERITY_ERROR);
        }
    }

    public void viewUploadedDocuments() {
        try {
            List<VTDocumentModel> uploadedDocsList = DmsDocCheckUtils.getUploadedDocumentList(applNo);
            if (uploadedDocsList == null || uploadedDocsList.isEmpty()) {
                throw new VahanException("Documents not uploaded against the Application/Registration No " + applNo);
            }
            dmsFileUploadUrl = ServerUtil.getVahanPgiUrl(TableConstants.DMS_CON_VER);
            if (dmsFileUploadUrl.isEmpty()) {
                throw new VahanException("Problem in view documents.");
            }
            dmsFileUploadUrl = dmsFileUploadUrl.replace("ApplNo", applNo);
            dmsFileUploadUrl = dmsFileUploadUrl.replace("ApplStatus", TableConstants.DOCUMENT_VIEW_STATUS);
            dmsFileUploadUrl = dmsFileUploadUrl + TableConstants.SECURITY_KEY;
            PrimeFaces.current().executeScript("PF('dmsfileUploaded').show();");
        } catch (VahanException ex) {
            JSFUtils.showMessage(ex.getMessage());
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info!!!", ex.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void sendMailDialogCloseListener() {
        renderedMailButton = false;
    }

    public void uploadPendingDocuments() {
        try {
            ArrayList<Status_dobj> applStatus = ServerUtil.applicationStatusByApplNo(applNo, stateCd);
            if (applStatus != null && !applStatus.isEmpty()) {
                List<VTDocumentModel> docListForPendingUpload = DmsDocCheckUtils.getUploadedDocumentList(applNo);
                if (docListForPendingUpload == null || docListForPendingUpload.isEmpty()) {
                    throw new VahanException("Please use Upload/Modify button to upload the documents.");
                }
                dmsFileUploadUrl = ServerUtil.getVahanPgiUrl(TableConstants.DMS_SARTHI_URL);
                if (dmsFileUploadUrl.isEmpty()) {
                    throw new VahanException("Problem in view documents.");
                }
                dmsFileUploadUrl = dmsFileUploadUrl.replace("ApplNo", applNo);
                dmsFileUploadUrl = dmsFileUploadUrl.replace("state_cd", stateCd);
                dmsFileUploadUrl = dmsFileUploadUrl.replace("regn_no", applStatus.get(0).getRegn_no());
                dmsFileUploadUrl = dmsFileUploadUrl.replace("off_name", ServerUtil.getOfficeName(offCd, stateCd));
                dmsFileUploadUrl = dmsFileUploadUrl.replace("pur_cd", applStatus.get(0).getPur_cd() + "");
                dmsFileUploadUrl = dmsFileUploadUrl + TableConstants.SECURITY_KEY;
                PrimeFaces.current().ajax().update("formDocumentUpload:dmsFileUpload_panel");
                PrimeFaces.current().executeScript("PF('dmsfileUploaded').show();");
            } else {
                throw new VahanException("Invalid application no. or application no. already approved");
            }
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void fileFlow() {
        String noteMsg = "<br/> <span style='color:red;font: bold;'>Note:- Please send mail to modify the rejected documents.</span>";
        try {
            if ("documentsStatus".equals(getRedirectTo())) {
                return;
            }

            // this i have to call only if person completes work in one go
            uploadedList = new ArrayList<>();
            this.callDocumentApiToGetUploadedDocList();

            if (uploadedList != null && mandatoryList != null && !uploadedList.isEmpty() && !mandatoryList.isEmpty()) {
                if (uploadedList.size() >= mandatoryList.size()) {
                    for (DocumentDetailsDobj dtls : mandatoryList) {
                        if (!uploadedList.contains(dtls)) {
                            throw new VahanException(TableConstants.MANDATORY_DOCUMENTS_NOT_UPLOAD_ERR_MESSG + getApplNo() + "");
                        }
                    }
                } else {
                    throw new VahanException("Uploaded documents Total: " + uploadedList.size() + " is less then Mandatory documents Total: " + mandatoryList.size());
                }
            } else {
                throw new VahanException("Can't proceed, Please upload Documents !!!");
            }

            if (uploadedList != null && !uploadedList.isEmpty() && uploadedList.size() > 0) {
                if (actionCd == TableConstants.TM_ROLE_UPLOAD_MODIFY_DOCUMENT || actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_UPLOAD) {
                    boolean uploadFlag = DocumentUploadImpl.fileFlowAfterDocumentUpload(getApplNo(), getActionCd(), getPurCd(), getRemarks());
                    ServerUtil.insertIntoDscRegistrationHistory();
                    if (uploadFlag) {
                        setSuccessUplodedMsg(TableConstants.DOCUMENTS_UPLOAD_SUCCESS + getApplNo());
                    } else {
                        throw new VahanException(TableConstants.DOCUMENTS_NOT_UPLOAD_ERR_MESSG + getApplNo() + "");
                    }
                    PrimeFaces.current().ajax().update("formDocumentUpload:successMsg");
                    PrimeFaces.current().executeScript("PF('successDialog').show()");
                }
            } else {
                throw new VahanException(TableConstants.DOCUMENTS_NOT_UPLOAD_ERR_MESSG + getApplNo() + "");
            }

        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Alert !!!", e.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception ex) {
            getLOGGER().error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!!", TableConstants.SomthingWentWrong, FacesMessage.SEVERITY_WARN);
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            UploadedFile file = event.getFile();
            byte[] apiFile = event.getFile().getContents();
            String extension = this.getExtension(file.getFileName());
            if (confgDMS.isDigitalSignAllowStateWise()) {
                if (confgDMS.getDigitalSignAllowOffWise().contains("," + Util.getUserLoginOffCode() + ",") && Util.getUserCategory().equals(TableConstants.USER_CATG_DEALER)) {
                    // && (actionCd != 0 && !"180006,10006,10005".contains(String.valueOf(actionCd)))
                    DscDobj dscRegisterObj = new DscRegistrationImpl().fetchDscSignerCertDtls();
                    if (dscRegisterObj == null) {
                        throw new VahanException("First connect to DSC before uploading the document!!!");
                    }

                    if (selectedDoc.getSubCatg() == null) {
                        throw new VahanException("Please select Sub Category");
                    } else if (selectedDoc.getSubCatg() != null && selectedDoc.getSubCatg().equals("-1")) {
                        throw new VahanException("Please select Sub Category");
                    }
                    if (!(TableConstants.PDF_EXTENSION).contains(extension)) {
                        throw new VahanException("Only PDF are allowed!!!");
                    }
                    selectedDoc.setFile(file);
                    this.signingPdf(file, dscRegisterObj.getSerialNo(), dscRegisterObj.getVendorCd());
                } else {
                    if (selectedDoc.getSubCatg() == null) {
                        throw new VahanException("Please select Sub Category");
                    } else if (selectedDoc.getSubCatg() != null && selectedDoc.getSubCatg().equals("-1")) {
                        throw new VahanException("Please select Sub Category");
                    }
                    if (!"pdf,gif,jpeg,jpg,png,PDF,GIF,JPG,JPEG,PNG".contains(extension)) {
                        throw new VahanException("Invalid File Type!!!");
                    }

                    selectedDoc.setFile(file);
                    selectedDoc.setApiFile(apiFile);
                    if (selectedDoc.isRenderUploadButton()) {
                        this.callDocumentApiForDocumentUpload(selectedDoc);
                    } else if (selectedDoc.isRenderModifyButton()) {
                        this.callDocumentApiForModifyDocumentUpload(selectedDoc);
                    } else {
                        throw new VahanException(TableConstants.SomthingWentWrong);
                    }
                }
            } else {
                if (selectedDoc.getSubCatg() == null) {
                    throw new VahanException("Please select Sub Category");
                } else if (selectedDoc.getSubCatg() != null && selectedDoc.getSubCatg().equals("-1")) {
                    throw new VahanException("Please select Sub Category");
                }
                if (!"pdf,gif,jpeg,jpg,png,PDF,GIF,JPG,JPEG,PNG".contains(extension)) {
                    throw new VahanException("Invalid File Type!!!");
                }

                selectedDoc.setFile(file);
                selectedDoc.setApiFile(apiFile);
                if (selectedDoc.isRenderUploadButton()) {
                    this.callDocumentApiForDocumentUpload(selectedDoc);
                } else if (selectedDoc.isRenderModifyButton()) {
                    this.callDocumentApiForModifyDocumentUpload(selectedDoc);
                } else {
                    throw new VahanException(TableConstants.SomthingWentWrong);
                }
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Alert !!!", e.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception ex) {
            getLOGGER().error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!!", TableConstants.SomthingWentWrong, FacesMessage.SEVERITY_WARN);
        }
    }

    public void callDocumentApiToGetDocListToBeUpload() throws VahanException {
        try {
            this.callDocumentApiToGetNonUploadedListAndUploadedList(false);
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            getLOGGER().error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void callDocumentApiForDocumentUpload(DocumentDetailsDobj docDetails) throws VahanException {
        DmsDocCheckUtils docCheckUtils = new DmsDocCheckUtils();
        try {
            RequestDto requestDto = this.setDetailsInRequestDtoForDocumentUploadAndModify("upload", docDetails);
            RequestDto resDto = docCheckUtils.callDocumentApiForDocumentUploadAndUpdate(requestDto, TableConstants.DMS_UPLOAD_URL);

            if (resDto != null && resDto.getStatusCode() != null && resDto.getMessage() != null && resDto.getStatusCode().equals(TableConstants.DMS_STATUS_CODE) && resDto.getMessage().equals(TableConstants.DMS_STATUS_MESS)) {
                String dmsViewUrl = this.callDocumentApiForViewDocument(resDto.getDmsFileDetails().get(0).getObjectId());
                docDetails.setShowImage(dmsViewUrl);
                docDetails.setRenderUploadButton(false);
                docDetails.setRenderModifyButton(true);
                docDetails.setObjectId(resDto.getDmsFileDetails().get(0).getObjectId());
                docDetails.setDisableSubCatg(true);
                String extension = this.getExtension(resDto.getDmsFileDetails().get(0).getFileName());
                if ((TableConstants.PDF_EXTENSION).contains(extension)) {
                    docDetails.setPdfExtension(true);
                    docDetails.setImageExtension(false);
                } else if ((TableConstants.IMAGE_EXTENSION).contains(extension)) {
                    docDetails.setPdfExtension(false);
                    docDetails.setImageExtension(true);
                }

                if (actionCd == TableConstants.TM_ROLE_UPLOAD_MODIFY_DOCUMENT || actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_UPLOAD || actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_MODIFY) {
                    PrimeFaces.current().ajax().update("formDocumentUpload:carouselId");
                } else {
                    if (actionCd == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_VERIFY || actionCd == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_APPROVAL) {
                        this.callDocumentApiToGetDocListToBeUploadAtVerification();
                    }
                    PrimeFaces.current().ajax().update("workbench_tabview:doc_panel_api");
                }
                FacesMessage msg = new FacesMessage("Upload Successful", resDto.getDmsFileDetails().get(0).getFileName() + " is uploaded.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                throw new VahanException("Upload UnSuccessful," + resDto.getDmsFileDetails().get(0).getFileName() + " is not uploaded due to ." + resDto.getMessage());
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            getLOGGER().error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public String callDocumentApiForViewDocument(String objectId) throws VahanException {
        DmsDocCheckUtils docCheckUtils = new DmsDocCheckUtils();
        String viewResponse = null;
        try {
            viewResponse = docCheckUtils.callDocumentApiForViewDocument(objectId);
            if (viewResponse == null) {
                throw new VahanException("Unable to get response from DMS.");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            getLOGGER().error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return viewResponse;
    }

    public void callDocumentApiForModifyDocumentUpload(DocumentDetailsDobj docDetails) throws VahanException {
        DmsDocCheckUtils docCheckUtils = new DmsDocCheckUtils();
        try {
            RequestDto requestDto = this.setDetailsInRequestDtoForDocumentUploadAndModify("modify", docDetails);
            RequestDto resDto = docCheckUtils.callDocumentApiForDocumentUploadAndUpdate(requestDto, TableConstants.DMS_UPDATE_URL);

            if (resDto != null && resDto.getStatusCode() != null && resDto.getMessage() != null && resDto.getStatusCode().equals(TableConstants.DMS_STATUS_CODE) && resDto.getMessage().equals(TableConstants.DMS_STATUS_MESS)) {
                String dmsViewUrl = this.callDocumentApiForViewDocument(resDto.getKey());
                dmsViewUrl = dmsViewUrl + "?" + Math.random();
                docDetails.setShowImage(dmsViewUrl);
                docDetails.setRenderUploadButton(false);
                docDetails.setRenderModifyButton(true);
                docDetails.setObjectId(resDto.getKey());
                docDetails.setDisableSubCatg(true);
                String extension = this.getExtension(docDetails.getFile().getFileName());
                if ((TableConstants.PDF_EXTENSION).contains(extension)) {
                    docDetails.setPdfExtension(true);
                    docDetails.setImageExtension(false);
                } else if ((TableConstants.IMAGE_EXTENSION).contains(extension)) {
                    docDetails.setPdfExtension(false);
                    docDetails.setImageExtension(true);
                }
                if (actionCd == TableConstants.TM_ROLE_UPLOAD_MODIFY_DOCUMENT || actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_UPLOAD || actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_MODIFY) {
                    PrimeFaces.current().ajax().update("formDocumentUpload:carouselId");
                } else {
                    if (actionCd == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_VERIFY || actionCd == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_APPROVAL) {
                        this.callDocumentApiToGetDocListToBeUploadAtVerification();
                    }
                    PrimeFaces.current().ajax().update("workbench_tabview:doc_panel_api");
                }
                FacesMessage msg = new FacesMessage("Modify Successful", docDetails.getFile().getFileName() + " is updated.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                throw new VahanException("Modify UnSuccessful," + resDto.getDmsFileDetails().get(0).getFileName() + " is not uploaded due to ." + resDto.getMessage());
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            getLOGGER().error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public RequestDto setDetailsInRequestDtoForDocumentUploadAndModify(String action, DocumentDetailsDobj docDetails) throws VahanException {
        RequestDto reqDto = null;
        try {
            if (docDetails != null) {

                reqDto = new RequestDto();
                List<DmsFileDetails> listDmsFile = new ArrayList<DmsFileDetails>();

                DmsFileDetails fileDtls = new DmsFileDetails();
                fileDtls.setFileName(docDetails.getFile().getFileName());
                fileDtls.setApiFile(docDetails.getApiFile());
                fileDtls.setRegNo(docDetails.getRegnNo());
                fileDtls.setUploaded(false);
                fileDtls.setDocCatgId(docDetails.getCatId());
                fileDtls.setDocId(Integer.parseInt(docDetails.getSubCatg()));
                listDmsFile.add(fileDtls);

                reqDto.setJ_key(TableConstants.DMS_J_KEY);
                reqDto.setJ_securityKey(TableConstants.DMS_J_SECURITYKEY);
                reqDto.setState(docDetails.getStateCd());
                reqDto.setApplno(docDetails.getApplNo());
                reqDto.setDmsFileDetails(listDmsFile);
                if (action != null && action.equals("modify")) {
                    reqDto.setKey(docDetails.getObjectId());
                }
            } else {
                throw new VahanException("Problem in getting Document Details");
            }
        } catch (VahanException vex) {
            throw vex;
        }
        return reqDto;
    }

    public void callDocumentApiToGetUploadedDocList() throws VahanException {
        DmsDocCheckUtils docCheckUtils = new DmsDocCheckUtils();
        try {
            if (actionCd != TableConstants.TM_ROLE_UPLOAD_MODIFY_DOCUMENT && actionCd != TableConstants.TM_ROLE_DEALER_DOCUMENT_UPLOAD && actionCd != TableConstants.TM_ROLE_DEALER_DOCUMENT_MODIFY) {
                ArrayList<Status_dobj> applStatus = ServerUtil.applicationStatusByApplNo(getApplNo(), getStateCd());
                if (applStatus != null && !applStatus.isEmpty()) {
                    if (applStatus.size() == 2) {
                        setPurCd(TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
                    } else {
                        setPurCd(applStatus.get(0).getPur_cd());
                    }
                }
            }
            if (stateCd != null && stateCd.equalsIgnoreCase("OR")) {
                ownerDetailsDobj = new DocumentUploadImpl().getVaOwnerDetailsForDocumentUpload(applNo.toUpperCase(), stateCd, null);
                if (ownerDetailsDobj != null) {
                    ownerCode = ownerDetailsDobj.getOwner_cd();
                }
            }
            String offName = ServerUtil.getOfficeName(getOffCd(), getStateCd());
            if (offName != null) {
                offName = offName.replaceAll("\\s", "");
            }
            ResponseDocument docListResponse = docCheckUtils.callDocumentApiToGetDocListToBeUpload(applNo, stateCd, regnNo, offName, purCd, false, ownerCode);
            ArrayList<DocumentDetailsDobj> digitalSignedDocDetails = new DscRegistrationImpl().getDocDigitalSignedDetails(applNo);
            if (docListResponse != null && docListResponse.getUploadedList() != null && !docListResponse.getUploadedList().isEmpty()) {
                for (UploadedList upload : docListResponse.getUploadedList()) {
                    DocumentDetailsDobj docDetails = this.setCommonDataForUploadedList(upload);
                    if ((actionCd == TableConstants.TM_NEW_RC_VERIFICATION || actionCd == TableConstants.TM_NEW_RC_APPROVAL || actionCd == TableConstants.TM_TMP_RC_VERIFICATION || actionCd == TableConstants.TM_TMP_RC_APPROVAL || actionCd == TableConstants.TM_ROLE_DEALER_APPROVAL || actionCd == TableConstants.TM_ROLE_DEALER_TEMP_APPROVAL || actionCd == TableConstants.TM_NEW_RC_FITNESS_INSPECTION || actionCd == TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL)) {
                        docDetails.setRenderModifyButton(false);
                    } else {
                        docDetails.setRenderModifyButton(true);
                    }
                    if (digitalSignedDocDetails != null && !digitalSignedDocDetails.isEmpty() && digitalSignedDocDetails.contains(docDetails)) {
                        docDetails.setDigitallySigned(true);
                    }
                    uploadedList.add(docDetails);
                }
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            getLOGGER().error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void callDocumentApiToGetDocListToBeUploadAtVerification() throws VahanException {
        try {
            docDetailsList = new ArrayList<>();
            mandatoryList = new ArrayList<>();
            uploadedList = new ArrayList<>();
            this.callDocumentApiToGetDocListToBeUpload();
            this.callDocumentApiToGetNonUploadedListAndUploadedList(true);
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            getLOGGER().error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void callDocumentApiToGetNonUploadedListAndUploadedList(boolean docUploadAtRTOAtVerification) throws VahanException, Exception {
        DmsDocCheckUtils docCheckUtils = new DmsDocCheckUtils();
        String offName = ServerUtil.getOfficeName(getOffCd(), getStateCd());
        if (offName != null) {
            offName = offName.replaceAll("\\s", "");
        }
        ResponseDocument docListResponse = docCheckUtils.callDocumentApiToGetDocListToBeUpload(applNo, stateCd, regnNo, offName, purCd, docUploadAtRTOAtVerification, ownerCode);
        if (docListResponse != null && docListResponse.getNonUploadedList() != null && !docListResponse.getNonUploadedList().isEmpty()) {
            for (NonUploadedList nonUpload : docListResponse.getNonUploadedList()) {
                DocumentDetailsDobj docDetails = new DocumentDetailsDobj();
                docDetails.setCatId(nonUpload.getCatId());
                docDetails.setCatName(nonUpload.getCatName());
                docDetails.setMandatory(nonUpload.getMandatory());
                List<SubCategoryMasterData> subCategoryMasterDataList = new ArrayList<>();
                for (SubCategoryMasterDataList scList : nonUpload.getSubcategoryMasterDataList()) {
                    SubCategoryMasterData sc = new SubCategoryMasterData();
                    sc.setSub_cat_id(String.valueOf(scList.getSub_cat_id()));
                    sc.setSub_cat_name(scList.getSub_cat_name());
                    subCategoryMasterDataList.add(sc);
                }
                docDetails.setSubcategoryMasterDataList(subCategoryMasterDataList);
                docDetails.setRenderUploadButton(true);
                docDetails.setRenderModifyButton(false);
                docDetails.setDisableSubCatg(false);
                docDetails.setStateCd(stateCd);
                docDetails.setRegnNo(regnNo);
                docDetails.setApplNo(applNo);
                if (nonUpload.getMandatory() != null && nonUpload.getMandatory().equals("Y")) {
                    mandatoryList.add(docDetails);
                }
                getDocDetailsList().add(docDetails);
            }
        }
        if (docListResponse != null && docListResponse.getUploadedList() != null && !docListResponse.getUploadedList().isEmpty()) {
            ArrayList<DocumentDetailsDobj> digitalSignedDocDetails = new DscRegistrationImpl().getDocDigitalSignedDetails(applNo);
            for (UploadedList upload : docListResponse.getUploadedList()) {
                DocumentDetailsDobj docDetails = this.setCommonDataForUploadedList(upload);
                if (actionCd == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_APPROVAL) {
                    docDetails.setRenderModifyButton(false);
                } else {
                    docDetails.setRenderModifyButton(true);
                }
                if (digitalSignedDocDetails != null && !digitalSignedDocDetails.isEmpty() && digitalSignedDocDetails.contains(docDetails)) {
                    docDetails.setDigitallySigned(true);
                }
                if (upload.getMandatory() != null && upload.getMandatory().equals("Y")) {
                    mandatoryList.add(docDetails);
                }
                uploadedList.add(docDetails);
                getDocDetailsList().add(docDetails);
            }
        }

        if (mandatoryList.size() <= 0) {
            throw new VahanException("Please contact to system Adminstrator to configure the DMS utility.");
        }
    }

    public DocumentDetailsDobj setCommonDataForUploadedList(UploadedList upload) throws VahanException, Exception {
        DocumentDetailsDobj docDetails = new DocumentDetailsDobj();
        List<SubCategoryMasterData> subCategoryMasterDataList = new ArrayList<>();
        docDetails.setCatId(upload.getCatId());
        docDetails.setCatName(upload.getCatName());
        docDetails.setMandatory(upload.getMandatory());
        docDetails.setSubCatg(upload.getSubcategoryMasterData().getSub_cat_id());
        subCategoryMasterDataList.add(upload.getSubcategoryMasterData());
        docDetails.setSubcategoryMasterDataList(subCategoryMasterDataList);
        docDetails.setRenderUploadButton(false);
        docDetails.setDisableSubCatg(true);
        docDetails.setStateCd(stateCd);
        docDetails.setRegnNo(regnNo);
        docDetails.setApplNo(applNo);
        docDetails.setObjectId(upload.getObjectId());
        String dmsViewUrl = this.callDocumentApiForViewDocument(upload.getObjectId());
        dmsViewUrl = dmsViewUrl + "?" + Math.random();
        docDetails.setShowImage(dmsViewUrl);
        docDetails.setDocVerified(false);
        docDetails.setUploadedFileName(upload.getDocUrl());
        String extension = this.getExtension(docDetails.getUploadedFileName());
        if ((TableConstants.PDF_EXTENSION).contains(extension)) {
            docDetails.setPdfExtension(true);
            docDetails.setImageExtension(false);
        } else if ((TableConstants.IMAGE_EXTENSION).contains(extension)) {
            docDetails.setPdfExtension(false);
            docDetails.setImageExtension(true);
        }
        return docDetails;
    }

    public void setDocumentObjectToDisplay() {
        index = 0;
        selectedDoc = docDetailsList.get(0);
        setShowNextBtn(true);
        setShowPrevBtn(true);
        printImageNo = String.valueOf(index + 1) + " of " + String.valueOf(docDetailsList.size());
    }

    public void setVerifyApprovalDocumentObjectToDisplay() {
        index = 0;
        selectedDoc = uploadedList.get(0);
        setShowNextBtn(true);
        setShowPrevBtn(true);
        printImageNo = String.valueOf(index + 1) + " of " + String.valueOf(uploadedList.size());
    }

    public void verifyDocument() {
        boolean isVerifyDoc = selectedDoc.isDocVerified();
        if (isVerifyDoc) {
            this.getNextObjectToDisplay();
        }
    }

    public void getNextObjectToDisplay() {
        index = index + 1;
        if (actionCd == TableConstants.TM_ROLE_UPLOAD_MODIFY_DOCUMENT || actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_UPLOAD || actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_MODIFY || actionCd == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_VERIFY || actionCd == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_APPROVAL) {
            if (index == docDetailsList.size()) {
                index = index - 1;
                JSFUtils.showMessagesInDialog("Information !!!", "No More Documents!!!", FacesMessage.SEVERITY_WARN);
            } else {
                selectedDoc = docDetailsList.get(index);
                setShowNextBtn(true);
                setShowPrevBtn(true);
                printImageNo = String.valueOf(index + 1) + " of " + String.valueOf(docDetailsList.size());
                if (actionCd == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_VERIFY || actionCd == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_APPROVAL) {
                    PrimeFaces.current().ajax().update("workbench_tabview:doc_panel_api");
                } else {
                    PrimeFaces.current().ajax().update("formDocumentUpload:carouselId");
                }
            }
        } else {
            //if (actionCd == TableConstants.TM_NEW_RC_VERIFICATION || actionCd == TableConstants.TM_NEW_RC_APPROVAL || actionCd == TableConstants.TM_TMP_RC_APPROVAL) {
            if (index == uploadedList.size()) {
                index = index - 1;
                JSFUtils.showMessagesInDialog("Information !!!", "No More Documents!!!", FacesMessage.SEVERITY_WARN);
            } else {
                selectedDoc = uploadedList.get(index);
                setShowNextBtn(true);
                setShowPrevBtn(true);
                printImageNo = String.valueOf(index + 1) + " of " + String.valueOf(uploadedList.size());
                PrimeFaces.current().ajax().update("workbench_tabview:doc_panel_api");
            }
        }
    }

    public void getPrevObjectToDisplay() {
        index = index - 1;
        if (actionCd == TableConstants.TM_ROLE_UPLOAD_MODIFY_DOCUMENT || actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_UPLOAD || actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_MODIFY || actionCd == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_VERIFY || actionCd == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_APPROVAL) {
            if (index == -1) {
                index = index + 1;
                JSFUtils.showMessagesInDialog("Information !!!", "No More Documents!!!", FacesMessage.SEVERITY_WARN);
            } else {
                selectedDoc = docDetailsList.get(index);
                setShowNextBtn(true);
                setShowPrevBtn(true);
                printImageNo = String.valueOf(index + 1) + " of " + String.valueOf(docDetailsList.size());
                if (actionCd == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_VERIFY || actionCd == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_APPROVAL) {
                    PrimeFaces.current().ajax().update("workbench_tabview:doc_panel_api");
                } else {
                    PrimeFaces.current().ajax().update("formDocumentUpload:carouselId");
                }
            }
        } else {
            //if (actionCd == TableConstants.TM_NEW_RC_VERIFICATION || actionCd == TableConstants.TM_NEW_RC_APPROVAL || actionCd == TableConstants.TM_TMP_RC_APPROVAL) {
            if (index == -1) {
                index = index + 1;
                JSFUtils.showMessagesInDialog("Information !!!", "No More Documents!!!", FacesMessage.SEVERITY_WARN);
            } else {
                selectedDoc = uploadedList.get(index);
                setShowNextBtn(true);
                setShowPrevBtn(true);
                printImageNo = String.valueOf(index + 1) + " of " + String.valueOf(uploadedList.size());
                PrimeFaces.current().ajax().update("workbench_tabview:doc_panel_api");
            }
        }
    }

    public void setOwnerDetailsValue() throws VahanException {
        DocumentUploadImpl impl = new DocumentUploadImpl();
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        OwnerDetailsDobj ownerDobj = impl.getOwnerDetails(applNo, stateCd, regnNo, purCd);
        if (ownerDobj != null) {
            String ownerDetailsVal = "<b><u>Owner Details</u></b><br/>";
            if (!CommonUtils.isNullOrBlank(ownerDobj.getOwner_name())) {
                ownerDetailsVal = ownerDetailsVal + ownerDobj.getOwner_name() + "<br/> ";
            }
            if (!CommonUtils.isNullOrBlank(ownerDobj.getF_name())) {
                ownerDetailsVal = ownerDetailsVal + "S/W/D of " + ownerDobj.getF_name() + "<br/>";
            }
            if (!CommonUtils.isNullOrBlank(ownerDobj.getC_add1())) {
                ownerDetailsVal = ownerDetailsVal + "R/o " + ownerDobj.getC_add1();
            }

            if (!CommonUtils.isNullOrBlank(ownerDobj.getC_add2())) {
                ownerDetailsVal = ownerDetailsVal + "<br/>" + ownerDobj.getC_add2();
            }
            if (!CommonUtils.isNullOrBlank(ownerDobj.getC_add3())) {
                ownerDetailsVal = ownerDetailsVal + "<br/>" + ownerDobj.getC_add3();
            }

            if (!CommonUtils.isNullOrBlank(ownerDobj.getC_district_name())) {
                ownerDetailsVal = ownerDetailsVal + "<br/>" + ownerDobj.getC_district_name();
            }

            if (!CommonUtils.isNullOrBlank(ownerDobj.getC_state_name())) {
                ownerDetailsVal = ownerDetailsVal + "<br/>" + ownerDobj.getC_state_name() + "-" + ownerDobj.getC_pincode() + "<br/><br/>";
            }

            if (ownerDobj.getInsDobj() != null) {
                ownerDetailsVal = ownerDetailsVal + "<b><u>Insurance Details</u></b><br/>";
                if (!CommonUtils.isNullOrBlank(ownerDobj.getInsDobj().getInsCompName())) {
                    ownerDetailsVal = ownerDetailsVal + "From " + ownerDobj.getInsDobj().getInsCompName() + "<br/> ";
                }
                if (!CommonUtils.isNullOrBlank(ownerDobj.getInsDobj().getPolicy_no())) {
                    ownerDetailsVal = ownerDetailsVal + " vide policy no " + ownerDobj.getInsDobj().getPolicy_no() + "<br/> ";
                }
                if (ownerDobj.getInsDobj().getIns_from() != null) {
                    ownerDetailsVal = ownerDetailsVal + "valid from " + format.format(ownerDobj.getInsDobj().getIns_from());
                }
                if (ownerDobj.getInsDobj().getIns_upto() != null) {
                    ownerDetailsVal = ownerDetailsVal + " to " + format.format(ownerDobj.getInsDobj().getIns_upto()) + "<br/><br/>";
                }

            }

            if (ownerDobj.getHpaDobj() != null) {
                ownerDetailsVal = ownerDetailsVal + "<b><u>Hypothecation Details</u></b><br/>" + ownerDobj.getHpaDobj().getHp_type_descr() + "<br/> " + " from " + ownerDobj.getHpaDobj().getFncr_name() + "<br/><br/>";
            }

            if (ownerDobj.getFitnessDobj() != null) {
                ownerDetailsVal = ownerDetailsVal + "<b><u>PUCC Details</u></b><br/>";
                if (!CommonUtils.isNullOrBlank(ownerDobj.getFitnessDobj().getPucc_no())) {
                    ownerDetailsVal = ownerDetailsVal + ownerDobj.getFitnessDobj().getPucc_no() + "<br/> ";
                }
                if (ownerDobj.getFitnessDobj().getPucc_val() != null) {
                    ownerDetailsVal = ownerDetailsVal + " valid upto " + format.format(ownerDobj.getFitnessDobj().getPucc_val()) + "<br/><br/>";
                }
            }
            this.setOwnerDetails(ownerDetailsVal);
        }
    }

    public String getExtension(String file) throws Exception {
        String[] fileName = file.split("[.]");
        String extension = fileName[fileName.length - 1];
        return extension;
    }

    public void connectToDsc() {
        DscService dscserv = new DscService();
        try {          
            if (getCertificatexml() != null && getCertificatexml().length() > 0) {
                String decodedResp = new String(Base64.decodeBase64(getCertificatexml().getBytes()));
                CertListResponse obj1 = dscserv.parseDscList(decodedResp);

                dscServiceCertificateList = new ArrayList<>();
                ArrayList<DscDobj> dscDobjList = new DscRegistrationImpl().fetchDscRegistrationDtls(null, null, 0, "dscRegistration");
                if (dscDobjList.isEmpty()) {
                    //StringBuffer mess = new StringBuffer("DSC Registration is pending").append("\n").append("Please complete the following process before uploading document").append("\n").append("1)Go to dealer admin login").append("\n").append("2)Register DSC by using DSC-REGISTRATION").append("\n").append("3)Contact respective RTO for DSC Registration Approval");
                    String mess = null;
                    if (Util.getUserCategory() != null && Util.getUserCategory().equals(TableConstants.USER_CATG_DEALER)) {
                        mess = "DSC Registration is pending".concat("\n").concat("Please complete the following process before uploading document").concat("\n").concat("1)Go to dealer admin login").concat("\n").concat("2)Register DSC by using DSC-REGISTRATION").concat("\n").concat("3)Contact respective RTO for DSC Registration Approval");
                    } else if (Util.getUserCategory() != null && Util.getUserCategory().equals(TableConstants.USER_CATG_OFF_STAFF)) {
                        mess = "DSC Registration is pending".concat("\n").concat("Please complete the following process before uploading document").concat("\n").concat("1)Go to office admin login").concat("\n").concat("2)Register DSC by using DSC-REGISTRATION");
                    }
                    throw new VahanException(mess);
                }
                for (CertListResponse.CertificateDetails str : obj1.getCertificates()) {
                    DscDobj dobj = new DscDobj();

//                getLOGGER().info(str.getSerialNumber() + ":   " + str.getNotAfter());
                    String usernm = str.getIssuedTo().substring(str.getIssuedTo().indexOf("CN=") + 3).substring(0, str.getIssuedTo().substring(str.getIssuedTo().indexOf("CN=") + 3).indexOf(","));

                    DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
                    Date date = (Date) formatter.parse(str.getNotAfter());

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    String formatedDate = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE);
                    dobj.setRevocationSt(str.getRevocation());
                    dobj.setCertValidUpto(formatedDate);
                    dobj.setUserName(usernm);
                    dobj.setSerialNo(str.getSerialNumber());
                    if (str.getIssuedBy().indexOf("O=") != -1) {
                        String vendorName = str.getIssuedBy().substring(str.getIssuedBy().indexOf("O=")).substring(0, str.getIssuedBy().substring(str.getIssuedBy().indexOf("O=")).indexOf(","));
                        dobj.setVendorName(vendorName.split("=")[1]);
                    }
                    if (dscDobjList.contains(dobj)) {
                        dobj.setShowActionBtn(true);
                    }
                    dscServiceCertificateList.add(dobj);
                }
            } else {
                throw new VahanException("Problem while connecting to DSC");
            }
        } catch (VahanException ves) {
            JSFUtils.showMessagesInDialog("Alert !!!", ves.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception ex) {
            getLOGGER().error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!!", "Problem while connecting to DSC", FacesMessage.SEVERITY_WARN);
        }
    }

    public void revocStr(DscDobj dob) {
        DscService dscserv = new DscService();
        try {
            // if (dob.getRevocationSt() != null && dob.getRevocationSt().getRevReq() != null && !dob.getRevocationSt().getRevReq().isEmpty() && dob.getRevocationSt().getRevReq().size() > 3) {
            Date dscValidity = new SimpleDateFormat("yyyy-MM-dd").parse(dob.getCertValidUpto());
            if (DateUtils.compareDates(new Date(), dscValidity) == 2) {
                throw new VahanException("DSC with Serial No " + dob.getSerialNo() + " Validity has been expired!!!");
            }
            String crlURL = ServerUtil.getVahanPgiUrl(TableConstants.CRL_SERVICE_URL);
            String revocXmlString = XmlUtil.marshallOjectToXml(Revocation.class, dob.getRevocationSt());
//            getLOGGER().info("=====xmlString======= " + revocXmlString);
            String revokationStatusXML = dscserv.isRevoked(revocXmlString, crlURL, "");// Just Skipped till CRL Implementation
//            getLOGGER().info("revokationStatusXML = " + revokationStatusXML);
            String registrationrequestxml = dscserv.createRegistrationRequest(dob.getSerialNo(), revokationStatusXML);
//            getLOGGER().info("======registrationrequestxml ==== : " + registrationrequestxml);
            setRegistrationxml(registrationrequestxml);
            dob.setRevocstr(revocXmlString);
            PrimeFaces.current().ajax().update("formDocumentUpload:registXMl");
            PrimeFaces.current().executeScript("registerCert();");
//           }else {
//                throw new VahanException("Chain Certificates not found !!!");
//            }
        } catch (VahanException ves) {
            JSFUtils.showMessagesInDialog("Alert !!!", ves.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception ex) {
            getLOGGER().error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!!", "Problem while connecting to DSC", FacesMessage.SEVERITY_WARN);
        }
    }

    public void dscRegistration() {
        DscRegistrationImpl registerImpl = new DscRegistrationImpl();
        DscDobj dscDobj = new DscDobj();
        DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            String decodedResp = new String(Base64.decodeBase64(registrationResponseXml.getBytes()));
//            getLOGGER().info("registrationResponseXml : " + decodedResp);
            RegistrationResponse registerationResObj = (RegistrationResponse) XmlUtil.unmarshalXmlToObject(RegistrationResponse.class, decodedResp);
            if (registerationResObj != null) {
//                getLOGGER().info("status" + registerationResObj.getStatus());
//                getLOGGER().info("serial No" + registerationResObj.getSerialNumber());
//                getLOGGER().info("msg" + registerationResObj.getMsg());
                if (registerationResObj.getStatus() != null && registerationResObj.getStatus().equals("0")) {
                    throw new VahanException(registerationResObj.getMsg());
                }
                dscDobj.setSerialNo(registerationResObj.getSerialNumber());
                dscDobj.setCertlevel(registerationResObj.getCertType());
                dscDobj.setCdpPoint(registerationResObj.getCdpPoint());
                dscDobj.setActiveStatus(registerationResObj.getStatus());
                dscDobj.setUserName(registerationResObj.getAliasName());
                Date validDate = df2.parse(registerationResObj.getNotAfter());
                dscDobj.setCertValidDate(validDate);

                ChainCertificates certificates = registerationResObj.getChainCerts();
                if (certificates != null) {
                    dscDobj.setChainCertificates(certificates.getChainCerts());
                }

                if (registerationResObj.getStatus() != null && registerationResObj.getStatus().equals("1")) {
                    if (registerationResObj.getIssuedBy().indexOf("O=") != -1) {
                        String vendorName = registerationResObj.getIssuedBy().substring(registerationResObj.getIssuedBy().indexOf("O=")).substring(0, registerationResObj.getIssuedBy().substring(registerationResObj.getIssuedBy().indexOf("O=")).indexOf(","));
                        dscDobj.setVendorName(vendorName.split("=")[1]);
                    }
                    DscDobj dscVerifiedDobj = registerImpl.isDscVerified(dscDobj.getSerialNo(), dscDobj.getVendorName());
                    if (dscVerifiedDobj != null) {
                        if (dscVerifiedDobj.isVerifyStatus()) {
                            dscDobj.setVendorCd(dscVerifiedDobj.getVendorCd());
                            boolean isRegistrationDone = registerImpl.insertIntoDscSignerCertDtls(dscDobj);
                            if (isRegistrationDone) {
                                this.setDscConnected(true);
                                this.setShowDigitalSignPanel(false);
                                PrimeFaces.current().ajax().update("formDocumentUpload:doc_digital_sign");
                                JSFUtils.showMessagesInDialog("Info !!!", "DSC Connection Done Successfully!!!", FacesMessage.SEVERITY_INFO);
                            } else {
                                throw new VahanException("Problem while connection of DSC");
                            }
                        } else {
                            throw new VahanException("The DSC SerialNo " + dscVerifiedDobj.getSerialNo() + " registered by Dealer Admin is pending for approval at respective  Registering Authorirty.Please visit for final approval.");
                        }
                    } else {
                        StringBuffer mess = new StringBuffer("DSC Registration is pending \n").append("Please complete the following process before uploading document\n").append("1)Go to dealer admin login\n").append("2)Register DSC by using DSC-REGISTRATION\n").append("3)Contact respective RTO for DSC Registration Approval\n");
                        throw new VahanException(mess.toString());
                    }
                } else {
                    throw new VahanException("Problem while connection of DSC : In Active Status");
                }
            } else {
                throw new VahanException("Problem in getting response for connection of DSC");
            }
        } catch (VahanException ex) {
            JSFUtils.showMessagesInDialog("Alert !!!", ex.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception ex) {
            getLOGGER().error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!!", "Problem while connection of DSC", FacesMessage.SEVERITY_WARN);
        }
    }

    public void signingPdf(UploadedFile uploadedPdf, String serialNo, int vendorCd) throws VahanException {
        DscService dscserv = new DscService();
        DscRegistrationImpl registerImpl = new DscRegistrationImpl();
        try {
            if (serialNo != null && !serialNo.isEmpty()) {
                ArrayList<DscDobj> dscDobj = registerImpl.fetchDscRegistrationDtls("onBasisOfSerialNum", serialNo, vendorCd, "dscRegistration");
                Revocation revocObj = this.fetchDscSignerRevocationObj(dscDobj);
                if (revocObj.getRevReq() != null && !revocObj.getRevReq().isEmpty()) {
                    String crlURL = ServerUtil.getVahanPgiUrl(TableConstants.CRL_SERVICE_URL);
                    String signRevoc = XmlUtil.marshallOjectToXml(Revocation.class, revocObj);
//                    getLOGGER().info("signRevoc==== " + signRevoc);

                    String SignRevokationStatusXML = dscserv.isRevoked(signRevoc, crlURL, "");

//                    getLOGGER().info("SignRevokationStatusXML========= " + SignRevokationStatusXML);
                    Date date = Calendar.getInstance().getTime();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String strDate = dateFormat.format(date);

                    //to write src file
                    String uploadFilePath = ServerUtil.getVahanPgiUrl(TableConstants.UPLOADING_PDF_FILE_PATH);
                    String srcFileName = "srcDMSFile" + userCode + selectedDoc.getCatId() + ".pdf";
                    String srcFilePath = uploadFilePath + srcFileName;
                    File srcFile = new File(srcFilePath);
                    OutputStream srcOut = new BufferedOutputStream(new FileOutputStream(srcFile));
                    srcOut.write(uploadedPdf.getContents());
                    srcOut.flush();
                    srcOut.close();

                    String targetFileName = "targetDMSFileToBeSigned" + userCode + selectedDoc.getCatId() + ".pdf";
                    String targetFilePath = uploadFilePath + targetFileName;
                    File targetFile = new File(targetFilePath);
                    OutputStream targetOut = new BufferedOutputStream(new FileOutputStream(targetFile));
                    targetOut.write(uploadedPdf.getContents());
                    targetOut.flush();
                    targetOut.close();

                    pdfSignxML = dscserv.createDocSignRequest(srcFilePath, targetFilePath, true, null, 0, dscDobj.get(0).getDigitalSignToPrint(), null, serialNo, SignRevokationStatusXML, strDate, null);

                    for (DscDobj dbj : dscDobj) {
                        if (dbj.getCertlevel() != null && dbj.getCertlevel().equals("0")) {
                            setHiddenDscDobj(dbj);
                        }
                    }
//                    getLOGGER().info("pdfSignXML=========== " + pdfSignxML);
                    setPdfSignXML(pdfSignxML.get("pdfSignRequestXml").toString());
//                    getLOGGER().info("getPdfSignXml" + getPdfSignXML());
                    PrimeFaces.current().ajax().update("formDocumentUpload:signxmlpdf");
                    PrimeFaces.current().executeScript("fileSigning();");
                    PrimeFaces.current().ajax().update("formDocumentUpload:signxmlpdfresponse");
                } else {
                    throw new VahanException("Problem while creating Revocation Request for signing of PDF!!");
                }
            } else {
                throw new VahanException("First connect to DSC before uploading the document!!!");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            getLOGGER().error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem while signing of PDF");
        }
    }

    public void signedPDFFile() {
        DscService dscserv = new DscService();
        try {
//            getLOGGER().info("pdfSignResponseXML========== " + pdfSignResponseXML);
//            getLOGGER().info("pdfSignxML======== " + pdfSignxML);

            String pdfDecodeResp = new String(Base64.decodeBase64(pdfSignResponseXML.getBytes()));
            PdfSignResp pdfSignResObj = (PdfSignResp) XmlUtil.unmarshalXmlToObject(PdfSignResp.class, pdfDecodeResp);
            if (pdfSignResObj != null && pdfSignResObj.getStatus() != null && pdfSignResObj.getStatus().equalsIgnoreCase("1")) {
                dscserv.signPdf(pdfDecodeResp, pdfSignxML);

                String uploadFilePath = ServerUtil.getVahanPgiUrl(TableConstants.UPLOADING_PDF_FILE_PATH);
                String srcFileName = "srcDMSFile" + userCode + selectedDoc.getCatId() + ".pdf";
                String srcFilePath = uploadFilePath + srcFileName;

                String targetFileName = "targetDMSFileToBeSigned" + userCode + selectedDoc.getCatId() + ".pdf";
                String targetFilePath = uploadFilePath + targetFileName;

                Path pdfPath = Paths.get(targetFilePath);
                byte[] apiFile = Files.readAllBytes(pdfPath);
                selectedDoc.setApiFile(apiFile);
                if (selectedDoc.isRenderUploadButton()) {
                    this.callDocumentApiForDocumentUpload(selectedDoc);
                } else if (selectedDoc.isRenderModifyButton()) {
                    this.callDocumentApiForModifyDocumentUpload(selectedDoc);
                } else {
                    throw new VahanException(TableConstants.SomthingWentWrong);
                }
                new DscRegistrationImpl().insertDocDetailsApplNoWise(getHiddenDscDobj(), selectedDoc);
                selectedDoc.setDigitallySigned(true);
                File targetFile = new File(targetFilePath);
                File srcFile = new File(srcFilePath);
                targetFile.delete();
                srcFile.delete();
            } else {
                throw new VahanException("Problem while getting signed PDF Document : InActive Status");
            }
        } catch (VahanException ex) {
            JSFUtils.showMessagesInDialog("Alert !!!", ex.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception ex) {
            getLOGGER().error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!!", "Problem while getting signed PDF Document", FacesMessage.SEVERITY_WARN);
        }
    }

    public Revocation fetchDscSignerRevocationObj(ArrayList<DscDobj> dscDobjList) throws VahanException {
        Revocation revocObj = new Revocation();
        List<RevocationRequest> revcReqList = new ArrayList<RevocationRequest>();
        RevocationRequest revocReqObj = null;
        try {
            for (DscDobj dscDobj : dscDobjList) {
                revocReqObj = new RevocationRequest();
                revocReqObj.setSerialNumber(dscDobj.getSerialNo());
                revocReqObj.setCertLevel(dscDobj.getCertlevel());
                revocReqObj.setCdpPoint(dscDobj.getCdpPoint());
                revcReqList.add(revocReqObj);
            }
            if (revcReqList != null && !revcReqList.isEmpty()) {
                revocObj.setRevReq(revcReqList);
            } else {
                throw new VahanException("Problem In getting Details of Chain Certificates");
            }

        } catch (VahanException ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return revocObj;
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
     * @return the dmsFileUploadUrl
     */
    public String getDmsFileUploadUrl() {
        return dmsFileUploadUrl;
    }

    /**
     * @param dmsFileUploadUrl the dmsFileUploadUrl to set
     */
    public void setDmsFileUploadUrl(String dmsFileUploadUrl) {
        this.dmsFileUploadUrl = dmsFileUploadUrl;
    }

    /**
     * @return the userCatg
     */
    public String getUserCatg() {
        return userCatg;
    }

    /**
     * @param userCatg the userCatg to set
     */
    public void setUserCatg(String userCatg) {
        this.userCatg = userCatg;
    }

    /**
     * @return the applFieldDisabled
     */
    public boolean isApplFieldDisabled() {
        return applFieldDisabled;
    }

    /**
     * @param applFieldDisabled the applFieldDisabled to set
     */
    public void setApplFieldDisabled(boolean applFieldDisabled) {
        this.applFieldDisabled = applFieldDisabled;
    }

    /**
     * @return the noteMsg
     */
    public String getNoteMsg() {
        return noteMsg;
    }

    /**
     * @param noteMsg the noteMsg to set
     */
    public void setNoteMsg(String noteMsg) {
        this.noteMsg = noteMsg;
    }

    /**
     * @return the actionCd
     */
    public int getActionCd() {
        return actionCd;
    }

    /**
     * @param actionCd the actionCd to set
     */
    public void setActionCd(int actionCd) {
        this.actionCd = actionCd;
    }

    /**
     * @return the redirectTo
     */
    public String getRedirectTo() {
        return redirectTo;
    }

    /**
     * @param redirectTo the redirectTo to set
     */
    public void setRedirectTo(String redirectTo) {
        this.redirectTo = redirectTo;
    }

    /**
     * @return the dmsDbLink
     */
    public String getDmsDbLink() {
        return dmsDbLink;
    }

    /**
     * @param dmsDbLink the dmsDbLink to set
     */
    public void setDmsDbLink(String dmsDbLink) {
        this.dmsDbLink = dmsDbLink;
    }

    /**
     * @return the documentUploadShow
     */
    public boolean isDocumentUploadShow() {
        return documentUploadShow;
    }

    /**
     * @param documentUploadShow the documentUploadShow to set
     */
    public void setDocumentUploadShow(boolean documentUploadShow) {
        this.documentUploadShow = documentUploadShow;
    }

    /**
     * @return the successUplodedMsg
     */
    public String getSuccessUplodedMsg() {
        return successUplodedMsg;
    }

    /**
     * @param successUplodedMsg the successUplodedMsg to set
     */
    public void setSuccessUplodedMsg(String successUplodedMsg) {
        this.successUplodedMsg = successUplodedMsg;
    }

    /**
     * @return the renderedMailButton
     */
    public boolean isRenderedMailButton() {
        return renderedMailButton;
    }

    /**
     * @param renderedMailButton the renderedMailButton to set
     */
    public void setRenderedMailButton(boolean renderedMailButton) {
        this.renderedMailButton = renderedMailButton;
    }

    /**
     * @return the renderDocumentUploadBtn
     */
    public boolean isRenderDocumentUploadBtn() {
        return renderDocumentUploadBtn;
    }

    /**
     * @param renderDocumentUploadBtn the renderDocumentUploadBtn to set
     */
    public void setRenderDocumentUploadBtn(boolean renderDocumentUploadBtn) {
        this.renderDocumentUploadBtn = renderDocumentUploadBtn;
    }

    /**
     * @return the docDescrList
     */
    public List<VTDocumentModel> getDocDescrList() {
        return docDescrList;
    }

    /**
     * @param docDescrList the docDescrList to set
     */
    public void setDocDescrList(List<VTDocumentModel> docDescrList) {
        this.docDescrList = docDescrList;
    }

    /**
     * @return the emailId
     */
    public String getEmailId() {
        return emailId;
    }

    /**
     * @param emailId the emailId to set
     */
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    /**
     * @return the emailSubject
     */
    public String getEmailSubject() {
        return emailSubject;
    }

    /**
     * @param emailSubject the emailSubject to set
     */
    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    /**
     * @return the emailMessage
     */
    public String getEmailMessage() {
        return emailMessage;
    }

    /**
     * @param emailMessage the emailMessage to set
     */
    public void setEmailMessage(String emailMessage) {
        this.emailMessage = emailMessage;
    }

    /**
     * @return the dealerMobileNo
     */
    public String getDealerMobileNo() {
        return dealerMobileNo;
    }

    /**
     * @param dealerMobileNo the dealerMobileNo to set
     */
    public void setDealerMobileNo(String dealerMobileNo) {
        this.dealerMobileNo = dealerMobileNo;
    }

    /**
     * @return the renderDocumentPendingBtn
     */
    public boolean isRenderDocumentPendingBtn() {
        return renderDocumentPendingBtn;
    }

    /**
     * @param renderDocumentPendingBtn the renderDocumentPendingBtn to set
     */
    public void setRenderDocumentPendingBtn(boolean renderDocumentPendingBtn) {
        this.renderDocumentPendingBtn = renderDocumentPendingBtn;
    }

    /**
     * @return the dmsURL
     */
    public String getDmsURL() {
        return dmsURL;
    }

    /**
     * @param dmsURL the dmsURL to set
     */
    public void setDmsURL(String dmsURL) {
        this.dmsURL = dmsURL;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    /**
     * @return the applDt
     */
    public String getApplDt() {
        return applDt;
    }

    /**
     * @param applDt the applDt to set
     */
    public void setApplDt(String applDt) {
        this.applDt = applDt;
    }

    /**
     * @return the purposeDescr
     */
    public String getPurposeDescr() {
        return purposeDescr;
    }

    /**
     * @param purposeDescr the purposeDescr to set
     */
    public void setPurposeDescr(String purposeDescr) {
        this.purposeDescr = purposeDescr;
    }

    /**
     * @return the renderApiBasedDMSDocPanel
     */
    public boolean isRenderApiBasedDMSDocPanel() {
        return renderApiBasedDMSDocPanel;
    }

    /**
     * @param renderApiBasedDMSDocPanel the renderApiBasedDMSDocPanel to set
     */
    public void setRenderApiBasedDMSDocPanel(boolean renderApiBasedDMSDocPanel) {
        this.renderApiBasedDMSDocPanel = renderApiBasedDMSDocPanel;
    }

    /**
     * @return the LOGGER
     */
    public static Logger getLOGGER() {
        return LOGGER;
    }

    /**
     * @param aLOGGER the LOGGER to set
     */
    public static void setLOGGER(Logger aLOGGER) {
        LOGGER = aLOGGER;
    }

    /**
     * @return the docDetailsList
     */
    public List<DocumentDetailsDobj> getDocDetailsList() {
        return docDetailsList;
    }

    /**
     * @param docDetailsList the docDetailsList to set
     */
    public void setDocDetailsList(List<DocumentDetailsDobj> docDetailsList) {
        this.docDetailsList = docDetailsList;
    }

    /**
     * @return the mandatoryList
     */
    public List<DocumentDetailsDobj> getMandatoryList() {
        return mandatoryList;
    }

    /**
     * @param mandatoryList the mandatoryList to set
     */
    public void setMandatoryList(List<DocumentDetailsDobj> mandatoryList) {
        this.mandatoryList = mandatoryList;
    }

    /**
     * @return the uploadedList
     */
    public List<DocumentDetailsDobj> getUploadedList() {
        return uploadedList;
    }

    /**
     * @param uploadedList the uploadedList to set
     */
    public void setUploadedList(List<DocumentDetailsDobj> uploadedList) {
        this.uploadedList = uploadedList;
    }

    /**
     * @return the renderApplicationInputPanel
     */
    public boolean isRenderApplicationInputPanel() {
        return renderApplicationInputPanel;
    }

    /**
     * @param renderApplicationInputPanel the renderApplicationInputPanel to set
     */
    public void setRenderApplicationInputPanel(boolean renderApplicationInputPanel) {
        this.renderApplicationInputPanel = renderApplicationInputPanel;
    }

    /**
     * @return the renderApplicatioDetailsAndCarouselPanel
     */
    public boolean isRenderApplicatioDetailsAndCarouselPanel() {
        return renderApplicatioDetailsAndCarouselPanel;
    }

    /**
     * @param renderApplicatioDetailsAndCarouselPanel the
     * renderApplicatioDetailsAndCarouselPanel to set
     */
    public void setRenderApplicatioDetailsAndCarouselPanel(boolean renderApplicatioDetailsAndCarouselPanel) {
        this.renderApplicatioDetailsAndCarouselPanel = renderApplicatioDetailsAndCarouselPanel;
    }

    /**
     * @return the showFileFlowBtn
     */
    public boolean isShowFileFlowBtn() {
        return showFileFlowBtn;
    }

    /**
     * @param showFileFlowBtn the showFileFlowBtn to set
     */
    public void setShowFileFlowBtn(boolean showFileFlowBtn) {
        this.showFileFlowBtn = showFileFlowBtn;
    }

    /**
     * @return the displayVerifyBtnText
     */
    public String getDisplayVerifyBtnText() {
        return displayVerifyBtnText;
    }

    /**
     * @param displayVerifyBtnText the displayVerifyBtnText to set
     */
    public void setDisplayVerifyBtnText(String displayVerifyBtnText) {
        this.displayVerifyBtnText = displayVerifyBtnText;
    }

    /**
     * @return the selectedDoc
     */
    public DocumentDetailsDobj getSelectedDoc() {
        return selectedDoc;
    }

    /**
     * @param selectedDoc the selectedDoc to set
     */
    public void setSelectedDoc(DocumentDetailsDobj selectedDoc) {
        this.selectedDoc = selectedDoc;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the showNextBtn
     */
    public boolean isShowNextBtn() {
        return showNextBtn;
    }

    /**
     * @param showNextBtn the showNextBtn to set
     */
    public void setShowNextBtn(boolean showNextBtn) {
        this.showNextBtn = showNextBtn;
    }

    /**
     * @return the showPrevBtn
     */
    public boolean isShowPrevBtn() {
        return showPrevBtn;
    }

    /**
     * @param showPrevBtn the showPrevBtn to set
     */
    public void setShowPrevBtn(boolean showPrevBtn) {
        this.showPrevBtn = showPrevBtn;
    }

    /**
     * @return the printImageNo
     */
    public String getPrintImageNo() {
        return printImageNo;
    }

    /**
     * @param printImageNo the printImageNo to set
     */
    public void setPrintImageNo(String printImageNo) {
        this.printImageNo = printImageNo;
    }

    /**
     * @return the ownerDetails
     */
    public String getOwnerDetails() {
        return ownerDetails;
    }

    /**
     * @param ownerDetails the ownerDetails to set
     */
    public void setOwnerDetails(String ownerDetails) {
        this.ownerDetails = ownerDetails;
    }

    /**
     * @return the showDigitalSignPanel
     */
    public boolean isShowDigitalSignPanel() {
        return showDigitalSignPanel;
    }

    /**
     * @param showDigitalSignPanel the showDigitalSignPanel to set
     */
    public void setShowDigitalSignPanel(boolean showDigitalSignPanel) {
        this.showDigitalSignPanel = showDigitalSignPanel;
    }

    /**
     * @return the pendingWorkFlowCall
     */
    public boolean isPendingWorkFlowCall() {
        return pendingWorkFlowCall;
    }

    /**
     * @param pendingWorkFlowCall the pendingWorkFlowCall to set
     */
    public void setPendingWorkFlowCall(boolean pendingWorkFlowCall) {
        this.pendingWorkFlowCall = pendingWorkFlowCall;
    }

    /**
     * @return the showHomeBtn
     */
    public boolean isShowHomeBtn() {
        return showHomeBtn;
    }

    /**
     * @param showHomeBtn the showHomeBtn to set
     */
    public void setShowHomeBtn(boolean showHomeBtn) {
        this.showHomeBtn = showHomeBtn;
    }

    /**
     * @return the noteMessasge
     */
    public String getNoteMessasge() {
        return noteMessasge;
    }

    /**
     * @param noteMessasge the noteMessasge to set
     */
    public void setNoteMessasge(String noteMessasge) {
        this.noteMessasge = noteMessasge;
    }

    /**
     * @return the certificatexml
     */
    public String getCertificatexml() {
        return certificatexml;
    }

    /**
     * @param certificatexml the certificatexml to set
     */
    public void setCertificatexml(String certificatexml) {
        this.certificatexml = certificatexml;
    }

    /**
     * @return the dscServiceCertificateList
     */
    public List<DscDobj> getDscServiceCertificateList() {
        return dscServiceCertificateList;
    }

    /**
     * @param dscServiceCertificateList the dscServiceCertificateList to set
     */
    public void setDscServiceCertificateList(List<DscDobj> dscServiceCertificateList) {
        this.dscServiceCertificateList = dscServiceCertificateList;
    }

    /**
     * @return the registrationxml
     */
    public String getRegistrationxml() {
        return registrationxml;
    }

    /**
     * @param registrationxml the registrationxml to set
     */
    public void setRegistrationxml(String registrationxml) {
        this.registrationxml = registrationxml;
    }

    /**
     * @return the registrationResponseXml
     */
    public String getRegistrationResponseXml() {
        return registrationResponseXml;
    }

    /**
     * @param registrationResponseXml the registrationResponseXml to set
     */
    public void setRegistrationResponseXml(String registrationResponseXml) {
        this.registrationResponseXml = registrationResponseXml;
    }

    /**
     * @return the pdfSignXML
     */
    public String getPdfSignXML() {
        return pdfSignXML;
    }

    /**
     * @param pdfSignXML the pdfSignXML to set
     */
    public void setPdfSignXML(String pdfSignXML) {
        this.pdfSignXML = pdfSignXML;
    }

    /**
     * @return the pdfSignResponseXML
     */
    public String getPdfSignResponseXML() {
        return pdfSignResponseXML;
    }

    /**
     * @param pdfSignResponseXML the pdfSignResponseXML to set
     */
    public void setPdfSignResponseXML(String pdfSignResponseXML) {
        this.pdfSignResponseXML = pdfSignResponseXML;
    }

    /**
     * @return the pdfSignxML
     */
    public Map<String, Object> getPdfSignxML() {
        return pdfSignxML;
    }

    /**
     * @param pdfSignxML the pdfSignxML to set
     */
    public void setPdfSignxML(Map<String, Object> pdfSignxML) {
        this.pdfSignxML = pdfSignxML;
    }

    /**
     * @return the dscConnected
     */
    public boolean isDscConnected() {
        return dscConnected;
    }

    /**
     * @param dscConnected the dscConnected to set
     */
    public void setDscConnected(boolean dscConnected) {
        this.dscConnected = dscConnected;
    }

    /**
     * @return the showDigitalSignLabel
     */
    public boolean isShowDigitalSignLabel() {
        return showDigitalSignLabel;
    }

    /**
     * @param showDigitalSignLabel the showDigitalSignLabel to set
     */
    public void setShowDigitalSignLabel(boolean showDigitalSignLabel) {
        this.showDigitalSignLabel = showDigitalSignLabel;
    }

    /**
     * @return the purCd
     */
    public int getPurCd() {
        return purCd;
    }

    /**
     * @param purCd the purCd to set
     */
    public void setPurCd(int purCd) {
        this.purCd = purCd;
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
     * @return the ownerDetailsDobj
     */
    public OwnerDetailsDobj getOwnerDetailsDobj() {
        return ownerDetailsDobj;
    }

    /**
     * @param ownerDetailsDobj the ownerDetailsDobj to set
     */
    public void setOwnerDetailsDobj(OwnerDetailsDobj ownerDetailsDobj) {
        this.ownerDetailsDobj = ownerDetailsDobj;
    }

    /**
     * @return the renderUiBasedDMSDocPanel
     */
    public boolean isRenderUiBasedDMSDocPanel() {
        return renderUiBasedDMSDocPanel;
    }

    /**
     * @param renderUiBasedDMSDocPanel the renderUiBasedDMSDocPanel to set
     */
    public void setRenderUiBasedDMSDocPanel(boolean renderUiBasedDMSDocPanel) {
        this.renderUiBasedDMSDocPanel = renderUiBasedDMSDocPanel;
    }

    /**
     * @return the hiddenDscDobj
     */
    public DscDobj getHiddenDscDobj() {
        return hiddenDscDobj;
    }

    /**
     * @param hiddenDscDobj the hiddenDscDobj to set
     */
    public void setHiddenDscDobj(DscDobj hiddenDscDobj) {
        this.hiddenDscDobj = hiddenDscDobj;
    }

    /**
     * @return the digitalSignAllowStateWise
     */
    public boolean isDigitalSignAllowStateWise() {
        return digitalSignAllowStateWise;
    }

    /**
     * @param digitalSignAllowStateWise the digitalSignAllowStateWise to set
     */
    public void setDigitalSignAllowStateWise(boolean digitalSignAllowStateWise) {
        this.digitalSignAllowStateWise = digitalSignAllowStateWise;
    }

    /**
     * @return the ownerCode
     */
    public int getOwnerCode() {
        return ownerCode;
    }

    /**
     * @param ownerCode the ownerCode to set
     */
    public void setOwnerCode(int ownerCode) {
        this.ownerCode = ownerCode;
    }
}
