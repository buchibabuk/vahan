/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.tradecert;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.FileMovementAbstractApplBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.tradecert.TradeCertificateDobj;
import nic.vahan.form.dobj.tradecert.TradeCertificateDobj.ListOfFeeDetail;
import nic.vahan.form.dobj.tradecert.VerifyApproveTradeCertDobj;
import nic.vahan.form.impl.tradecert.ApplicationTradeCertImpl;
import nic.vahan.form.impl.ApproveImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import nic.vahan.server.mail.MailSender;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author tranC081
 */
@ManagedBean(name = "verifyApproveTradeCertBean", eager = true)
@ViewScoped
public class VerifyApproveTradeCertBean extends FileMovementAbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(VerifyApproveTradeCertBean.class);
    @ManagedProperty(value = "#{approveImpl}")
    private ApproveImpl approveImpl;
    private VerifyApproveTradeCertDobj verifyApproveTradeCertDobj;
    private Map mapSectionsSrNoOfSelectedDealer;
    private List<VerifyApproveTradeCertDobj> applicationSectionsList;
    private int noOfVehGrandTotal;
    private ArrayList<ComparisonBean> compBeanList;
    private boolean renderFileMovement;
    private boolean tradeCertNoNotBlank;
    private String tradeCertType;
    private String rejectionReasons;
    private String deficiencyMailContent;
    private String stateNameMentionedInDeficiencyMail;
    private String applicantMailId;
    private String deficiencyMailSubject;
    private String applicantNameInDeficiencyMail;
    private String applicationNoInDeficiencyMail;
    private String applicantPhoneNumber;
    private boolean renderVerifyOnlineApplication;
    private boolean renderReceiveOnlineApplication;
    private boolean renderApproveOnlineApplication;
    private boolean detailsShown;
    private String currentTimeStampAsMarkHardCopyReceivedInStringFormat;
    private String dmsFileServerAPIString;  // "http://10.25.97.159:8080/dms-app/search-dms?applno=317013&j_key=vVl/Az1yGsjOAG18WDeScg==&j_securityKey=6D6C069D681B40DBF95CAD7B3ED71BE1djkhf3874394!TZFIMZbTiUtqXMfARJ1DGgyNicWFwYkwTA0ip/Q8Wns=";
    private boolean allDocumentsChecked;
    private String backwardRemark;
    private String loginUser;
    public static String dmsTradeCertUploadedDocsUrl;
    public static int DMS_UPLOADED_DOCS_LIMIT;
    private long totalAmount;
    private String totalAmountInCommaSeperatedString;
    public static String dmsTradeCertUploadedDocsUrlWithinServer;
    private TradeCertificateDobj tradeDobj;
    private List<TradeCertificateDobj.ListOfFeeDetail> dobjList;
    private boolean displayCalculatedFeeAndOtherFeeVariables;
    private TmConfigurationDobj tmConfigDobj;
    private List listOfAllInspectionOfficiers;
    private Date currentDate;
    private String inspectionBy;
    private String inspectionByAsString;
    private Date inspectionOn;
    private String inspectionOnAsString;
    private String inspectionRemark;
    private boolean renderInspectionPanel;
    private boolean hardCopiesReceived;
    private boolean applicationOperated;
    private boolean showHardCopyReceivedButton;
    private boolean mailSent;

    public VerifyApproveTradeCertBean() {
        try {
            verifyApproveTradeCertDobj = new VerifyApproveTradeCertDobj();
            applicationSectionsList = new ArrayList<>();
            mapSectionsSrNoOfSelectedDealer = new HashMap();
            compBeanList = new ArrayList<>();
            currentTimeStampAsMarkHardCopyReceivedInStringFormat = "";
            dobjList = new ArrayList<>();
            listOfAllInspectionOfficiers = getListOfInspectionOfficiers();
            currentDate = new Date();
            try {
                tmConfigDobj = Util.getTmConfiguration();
                if (tmConfigDobj.getTmTradeCertConfigDobj().isInspectionDtlsReq()) {   //////// for UP
                    renderInspectionPanel = true;
                }
            } catch (VahanException ex) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        } catch (Exception ex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            LOGGER.error(ex.getStackTrace()[0] + " " + ex.getMessage());
        }
    }

    @PostConstruct
    public void init() {
        try {

            this.getApp_disapp_dobj().setRenderHold(false);
            tradeCertType = "NEW";
            renderFileMovement = true;
            fillLoginDetails(this.verifyApproveTradeCertDobj);

            try {

                loginUser = this.verifyApproveTradeCertDobj.getEmpCd() + "";
                showApplicationDtls(Util.getSelectedSeat().getAppl_no());
                Map<String, Object> mpPreviousActions = getApp_disapp_dobj().getPrevRoleLabelValue();

                switch (appl_details.getCurrent_action_cd()) {
                    case TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE:
                        renderReceiveOnlineApplication = true;
                        break;
                    case TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION:
                        renderVerifyOnlineApplication = true;
                        break;
                    case TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE:
                        renderApproveOnlineApplication = true;
                        if (tmConfigDobj.getTmTradeCertConfigDobj().isVerifyRoleOnDmsPage()) {  ////// verify_role_on_dms_page work for states except  UP and Bihar
                            for (Iterator<Map.Entry<String, Object>> it = mpPreviousActions.entrySet().iterator(); it.hasNext();) {
                                Map.Entry<String, Object> entry = it.next();
                                if (entry.getValue().equals(String.valueOf(TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE))) {
                                    it.remove();
                                }
                            }
                        }
                        break;
                }

                if (tmConfigDobj.getTmTradeCertConfigDobj().isDocumentsUploadNRevert()) {   ///// documents_upload_n_revert work for states except Chhattisgarh
                    //DMS URL fetch by vahan4 dblink table
                    fillDMSDetails();
                    DMS_UPLOADED_DOCS_LIMIT = ApplicationTradeCertImpl.getNumberOfDocumentsRequiredToUpload(this.verifyApproveTradeCertDobj.getStateCd(), this.verifyApproveTradeCertDobj.getNewRenewalTradeCert(), this.verifyApproveTradeCertDobj.getApplicantType());

                    if (CommonUtils.isNullOrBlank(this.verifyApproveTradeCertDobj.getTempApplNo())) {
                        this.verifyApproveTradeCertDobj.setTempApplNo(ApplicationTradeCertImpl.fetchTemporaryApplNoFromOnlineSchemaVhaTradeCertAppl(this.verifyApproveTradeCertDobj.getDealerFor(), this.verifyApproveTradeCertDobj.getApplNo()));
                    }

                    try {
                        if (ApplicationTradeCertImpl.isApplicationReverted(this.verifyApproveTradeCertDobj.getApplNo(), this.verifyApproveTradeCertDobj.getStateCd(), this.verifyApproveTradeCertDobj.getOffCd())) {
                            JSFUtils.setFacesMessage("Application has been reverted by " + ServerUtil.getOfficeName(Util.getUserSeatOffCode(), Util.getUserStateCode()), null, JSFUtils.ERROR);
                            setApplicationOperated(true);
                            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
                            PrimeFaces.current().executeScript("PF('confirmationPopup').show()");
                            PrimeFaces.current().executeScript("PF('receiveApplicationBlocker').show()");
                        }

                        if (ApplicationTradeCertImpl.documentsNotUploaded(this.verifyApproveTradeCertDobj.getTempApplNo())) {
                            JSFUtils.setFacesMessage("Required documents have not been attached with the application from the applicant.", null, JSFUtils.ERROR);
                            setApplicationOperated(true);
                            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
                            PrimeFaces.current().executeScript("PF('confirmationPopup').show()");
                            PrimeFaces.current().executeScript("PF('receiveApplicationBlocker').show()");
                            return;
                        }
                    } catch (VahanException ve) {
                        if (!ve.getMessage().contains("No record found in trade certificate audit trail table for the given application")) {
                            throw ve;
                        }
                    }

                    setAllDocumentsChecked(checkAllDocumentsHaveBeenVerified());

                    if (!tmConfigDobj.getTmTradeCertConfigDobj().isAllowFacelessService()) {
                        setShowHardCopyReceivedButton(true);
                    }
                }
            } catch (VahanException ve) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application receive status...", ve.getMessage()));
            } catch (Exception ve) {
                LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
                LOGGER.error("VerifyApproveTradeCertBean.init(...):: EXCEPTION Message:::  " + ve.getMessage() + ", EXCEPTION Cause::: " + ve.getCause());
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Application processing status... ", TableConstants.SomthingWentWrong));
            }
        } catch (Exception ex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            LOGGER.error(ex.getStackTrace()[0] + " " + ex.getMessage());
        }

    }

    //Note:Please use DMS Utils for Session Details
    private void fillDMSDetails() throws VahanException {
        try {
            ApplicationTradeCertImpl applicationTradeCertImpl = new ApplicationTradeCertImpl();
            Map dmsMap = applicationTradeCertImpl.getDMSUrlMap();
            VerifyApproveTradeCertBean.dmsTradeCertUploadedDocsUrl = (String) dmsMap.get("dms_url_tc");
            //////////// STAGING
            //VerifyApproveTradeCertBean.dmsTradeCertUploadedDocsUrlWithinServer = (String) dmsMap.get("dms_url_ser_tc");  ///// NOT USED BY NETPROPHET TEAM
            //////////// LOCAL/LIVE
            VerifyApproveTradeCertBean.dmsTradeCertUploadedDocsUrlWithinServer = (String) dmsMap.get("dms_url_tc");        /////     USED BY NETPROPHET TEAM
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            LOGGER.error("VerifyApproveTradeCertBean.init(...):: EXCEPTION Message:::  " + e.getMessage() + ", EXCEPTION Cause::: " + e.getCause());
            throw new VahanException("Exception occured while fetching DMS URL stored in database.");
        }
    }

    public void showApplicationDtls(String applNo) {
        this.setRejectionReasons("");
        this.setBackwardRemark("");
        this.setCurrentTimeStampAsMarkHardCopyReceivedInStringFormat("");
        fillApplicationSectionsListVerifyForSelectedApplication(applNo, true);
    }

    private void fillApplicationSectionsListVerifyForSelectedApplication(String selectedApplication, boolean addressToBeFull) {

        mapSectionsSrNoOfSelectedDealer.clear();
        this.verifyApproveTradeCertDobj = null;
        ApplicationTradeCertImpl applicationTradeCertImpl = new ApplicationTradeCertImpl();
        try {
            applicationTradeCertImpl.getAllSrNoForOnlineApplication(selectedApplication, mapSectionsSrNoOfSelectedDealer, addressToBeFull);
            if (!mapSectionsSrNoOfSelectedDealer.isEmpty()) {
                for (Object keyObj : mapSectionsSrNoOfSelectedDealer.keySet()) {
                    VerifyApproveTradeCertDobj applicationTradeCertDobjFrom = (VerifyApproveTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
                    this.verifyApproveTradeCertDobj = applicationTradeCertDobjFrom;
                    if (renderInspectionPanel && !"0".equals(this.verifyApproveTradeCertDobj.getInspectionBy())) {
                        setInspectionBy(this.verifyApproveTradeCertDobj.getInspectionBy());
                        setInspectionByFromListOfAllInspectionOfficiers(this.verifyApproveTradeCertDobj.getInspectionBy());
                        setInspectionOn(this.verifyApproveTradeCertDobj.getInspectionOn());
                        setInspectionOnAsString(new SimpleDateFormat("dd-MMM-yyyy").format(this.verifyApproveTradeCertDobj.getInspectionOn()));
                        setInspectionRemark(this.verifyApproveTradeCertDobj.getInspectionRemark());
                    }
                    if (this.verifyApproveTradeCertDobj.getNewRenewalTradeCert().equalsIgnoreCase("N")) {
                        this.tradeCertType = "NEW";
                    } else if (this.verifyApproveTradeCertDobj.getNewRenewalTradeCert().equalsIgnoreCase("R")) {
                        this.tradeCertType = "RENEW";
                    } else if (this.verifyApproveTradeCertDobj.getNewRenewalTradeCert().equalsIgnoreCase("D")) {
                        this.tradeCertType = "DUPLICATE";
                    }

                    fillLoginDetails(this.verifyApproveTradeCertDobj);
                    generateDeficiencyMailOpeningMatter();
                    break;
                }

            }

            this.applicationSectionsList.clear();
            List list = getListFromMap();
            this.applicationSectionsList.addAll(list);
            calculateFeeAndDisplayFeeSection();
            detailsShown = true;
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Application processing status... ", "Exception in fetching data for online application for trade certificate. {message::[" + ve.getMessage() + "]}"));
            return;
        }
    }

    public String click() {
        if (isApplicationOperated()) {
            return "seatwork";
        }
        return "";
    }

    public boolean isApplicationOperated() {
        return applicationOperated;
    }

    public void setApplicationOperated(boolean applicationOperated) {
        this.applicationOperated = applicationOperated;
    }

    public List<VerifyApproveTradeCertDobj> getApplicationSectionsList() {
        return applicationSectionsList;
    }

    public int getNoOfVehGrandTotal() {
        return noOfVehGrandTotal;
    }

    public void reset() {

        this.renderFileMovement = false;

        this.verifyApproveTradeCertDobj.setNewRenewalTradeCert("New_Trade_Certificate");
        this.verifyApproveTradeCertDobj.reset();

        this.noOfVehGrandTotal = 0;
        applicationSectionsList.clear();

        this.verifyApproveTradeCertDobj.setDealerName("");

        this.mapSectionsSrNoOfSelectedDealer.clear();
        tradeCertNoNotBlank = false;
        tradeCertType = "";

    }

    public String back() {
        reset();
        return "home";
    }

    @Override
    public String save() {

        String returnLocation = "";

        returnLocation = "seatwork";

        return returnLocation;

    }

    private List getListFromMap() {
        List list = new ArrayList();
        this.noOfVehGrandTotal = 0;

        for (Object keyObj : this.mapSectionsSrNoOfSelectedDealer.keySet()) {
            VerifyApproveTradeCertDobj dobj = (VerifyApproveTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
            list.add(dobj);
            this.noOfVehGrandTotal += Integer.valueOf(dobj.getNoOfAllowedVehicles());
        }
        return list;
    }

    public VerifyApproveTradeCertDobj getVerifyApproveTradeCertDobj() {
        return verifyApproveTradeCertDobj;
    }

    //Note:Please use Utils for Session Details
    private void fillLoginDetails(VerifyApproveTradeCertDobj dobj) {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        String purCd = "";
        purCd = String.valueOf(TableConstants.VM_TRANSACTION_TRADE_CERT_ONLINE_APPLICATION);  /// 51 : online dealer entry
        dobj.setPurCd(purCd);
        dobj.setEmpCd((Long) sessionMap.get("emp_cd"));
        dobj.setOffCd(Util.getUserSeatOffCode());
        dobj.setStateCd((String) sessionMap.get("state_cd"));
        dobj.setStateName((String) sessionMap.get("state_name"));
        dobj.setEmpName((String) sessionMap.get("emp_name"));
        dobj.setDesigName((String) sessionMap.get("desig_name"));

    }

    public void setVerifyApproveTradeCertDobj(VerifyApproveTradeCertDobj verifyApproveTradeCertDobj) {
        this.verifyApproveTradeCertDobj = verifyApproveTradeCertDobj;
    }

    @Override
    public ArrayList<ComparisonBean> compareChanges() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String saveAndMoveFile() {

        TransactionManager tmgr = null;
        String returnLocation = "";
        String fileMoveReturn = "";
        String auditTrailReturn = "";
        String vaTradeCertAuditTrailStatus = "";
        String vaTradeCertStatus = "";
        String currentTradeCertStatus = "";
        String saveReturn = "";
        try {
            if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE) {
                vaTradeCertStatus = "R";
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION) {
                vaTradeCertStatus = "V";
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE) {
                vaTradeCertStatus = "A";
            }
            if (!mapSectionsSrNoOfSelectedDealer.isEmpty() && this.verifyApproveTradeCertDobj == null) {
                for (Object keyObj : mapSectionsSrNoOfSelectedDealer.keySet()) {
                    VerifyApproveTradeCertDobj applicationTradeCertDobjFrom = (VerifyApproveTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
                    this.verifyApproveTradeCertDobj = applicationTradeCertDobjFrom;
                    break;
                }
                fillLoginDetails(this.verifyApproveTradeCertDobj);
                if (renderInspectionPanel) {
                    this.verifyApproveTradeCertDobj.setInspectionBy(inspectionBy);
                    this.verifyApproveTradeCertDobj.setInspectionOn(inspectionOn);
                    this.verifyApproveTradeCertDobj.setInspectionRemark(inspectionRemark);
                }
                String temporaryApplNo = ApplicationTradeCertImpl.fetchTemporaryApplNoFromOnlineSchemaVhaTradeCertAppl(this.verifyApproveTradeCertDobj.getDealerFor(), this.verifyApproveTradeCertDobj.getApplNo());
                this.verifyApproveTradeCertDobj.setTempApplNo(temporaryApplNo);
            }

            if (tmConfigDobj.getTmTradeCertConfigDobj().isDocumentsUploadNRevert()) {  //// documents_upload_n_revert work

                if (!isAllDocumentsChecked() && !CommonUtils.isNullOrBlank(this.verifyApproveTradeCertDobj.getTempApplNo())) {
                    boolean reqDocsUploaded = ApplicationTradeCertImpl.isRequiredDocsUploaded(this.verifyApproveTradeCertDobj.getTempApplNo());
                    if (reqDocsUploaded) {
                        if (CommonUtils.isNullOrBlank(currentTimeStampAsMarkHardCopyReceivedInStringFormat) && isShowHardCopyReceivedButton()) {
                            JSFUtils.setFacesMessage("! Please receive hard copies of all 19 documents along with hard copy of deficiency mail (if any)", null, JSFUtils.ERROR);
                            JSFUtils.setFacesMessage("and press 'Hard Copy Physically Received' before submitting the online pending application.", null, JSFUtils.ERROR);
                        }

                        ///// CHECK FOR DMS TICKS IN CASE OF FORWARD
                        if (!isAllDocumentsChecked() && getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)) {
                            JSFUtils.setFacesMessage("! User can not forward the online application to next seat, unless all documents have been checked.", null, JSFUtils.WARN);
                        }

                        ///// CHECK FOR DMS TICKS IN CASE OF REVERT
                        if (isAllDocumentsChecked() && getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                            JSFUtils.setFacesMessage("! Application cannot be reverted back if all attached requisite and mandatory documents are checked", null, JSFUtils.WARN);
                            JSFUtils.setFacesMessage("or ticked. Kindly remove the check/tick with respect to the document selected for modification.", null, JSFUtils.WARN);
                        }
                    }
                }

                if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE
                        && getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                        && renderInspectionPanel) {
                    if (this.verifyApproveTradeCertDobj.getInspectionBy() == null
                            || (this.verifyApproveTradeCertDobj.getInspectionBy() != null && "".equals(this.verifyApproveTradeCertDobj.getInspectionBy()))
                            || (this.verifyApproveTradeCertDobj.getInspectionBy() != null && "-SELECT-".equals(this.verifyApproveTradeCertDobj.getInspectionBy()))) {
                        JSFUtils.setFacesMessage("! Please select 'Inspection By' as the name of the officer who have conducted the inspection activity.", null, JSFUtils.ERROR);

                    }
                    if (this.verifyApproveTradeCertDobj.getInspectionOn() == null
                            || (this.verifyApproveTradeCertDobj.getInspectionOn() != null && "".equals(this.verifyApproveTradeCertDobj.getInspectionOn()))) {
                        JSFUtils.setFacesMessage("! Please select 'Inspection On' as the date on which the inspection activity was conducted.", null, JSFUtils.ERROR);

                    }
                    if (this.verifyApproveTradeCertDobj.getInspectionRemark() == null
                            || (this.verifyApproveTradeCertDobj.getInspectionRemark() != null && "".equals(this.verifyApproveTradeCertDobj.getInspectionRemark()))) {
                        JSFUtils.setFacesMessage("! Please enter 'Inspection Remark' as the comment on the inspection activity which was conducted.", null, JSFUtils.ERROR);

                    }
                }

                if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
                    PrimeFaces.current().executeScript("PF('confirmationPopup').show()");
                    return "";
                }
            }

            tmgr = new TransactionManager("ApplicationTradeCertImpl.fileMoveToVerifyOnlineApplication()");
            ApplicationTradeCertImpl applicationTradeCertImpl = new ApplicationTradeCertImpl();
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE
                    && getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)) {
                status.setStatus(TableConstants.STATUS_APPROVED);
            }
            status.setCurrent_role(appl_details.getCurrent_role());

            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                    || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)
                    || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE && renderInspectionPanel) {
                    saveReturn = applicationTradeCertImpl.attachInspectionDetailsWithApplication(this.verifyApproveTradeCertDobj, tmgr);
                    if (saveReturn.contains("SUCCESS")) {
                        fileMoveReturn = applicationTradeCertImpl.fileMoveToSubmitOnlineApplication(status, tmgr);
                    } else {
                        JSFUtils.setFacesMessage("! Failure in saving inspection details." + TableConstants.SomthingWentWrong, null, JSFUtils.ERROR);
                        if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
                            //RequestContext rc = RequestContext.getCurrentInstance();
                            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
                            PrimeFaces.current().executeScript("PF('confirmationPopup').show()");
                            return "";
                        }
                    }
                } else {
                    fileMoveReturn = applicationTradeCertImpl.fileMoveToSubmitOnlineApplication(status, tmgr);
                }

                if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE) {
                    currentTradeCertStatus = "P";
                } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION) {
                    currentTradeCertStatus = "R";
                } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE) {
                    currentTradeCertStatus = "V";
                }

                if (fileMoveReturn.contains("SUCCESS")) {

                    boolean remarksUpdatedInAuditTrail = false;

                    String field = "";
                    try {

                        ///////////////// SET TRADE CERTIFICATE AUDIT TRAIL FORWARD REMARKS //////////////////
                        if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)) {
                            if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE) {
                                field = "receiver_forward_remarks";
                                this.verifyApproveTradeCertDobj.setReceiverForwardRemarks(getApp_disapp_dobj().getOffice_remark());
                                vaTradeCertAuditTrailStatus = "R";
                                vaTradeCertStatus = "R";
                                this.verifyApproveTradeCertDobj.setReceiver(loginUser);
                            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION) {
                                field = "verifier_forward_remarks";
                                this.verifyApproveTradeCertDobj.setVerifierForwardRemarks(getApp_disapp_dobj().getOffice_remark());
                                vaTradeCertAuditTrailStatus = "V";
                                vaTradeCertStatus = "V";
                                this.verifyApproveTradeCertDobj.setVerifier(loginUser);
                            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE) {
                                field = "approver_forward_remarks";
                                this.verifyApproveTradeCertDobj.setApproverForwardRemarks(getApp_disapp_dobj().getOffice_remark());
                                vaTradeCertAuditTrailStatus = "A";  ///// TRADE CERTIFICATE ISSUED BY APPROVER
                                vaTradeCertStatus = "A";
                                this.verifyApproveTradeCertDobj.setApprover(loginUser);
                            }
                        } else if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                            vaTradeCertAuditTrailStatus = "M";
                            if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION) {
                                field = "verifier_backward_remarks";
                                this.verifyApproveTradeCertDobj.setVerifierBackwardRemarks(getApp_disapp_dobj().getOffice_remark());
                                this.verifyApproveTradeCertDobj.setVerifier(loginUser);
                            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE) {
                                field = "approver_backward_remarks";
                                this.verifyApproveTradeCertDobj.setApproverBackwardRemarks(getApp_disapp_dobj().getOffice_remark());
                                this.verifyApproveTradeCertDobj.setApprover(loginUser);
                            }
                        }

                        this.verifyApproveTradeCertDobj.setStatus(vaTradeCertAuditTrailStatus);
                        this.verifyApproveTradeCertDobj.setCurrentTimeStampAsMarkHardCopyReceivedInStringFormat(currentTimeStampAsMarkHardCopyReceivedInStringFormat);
                        auditTrailReturn = new ApplicationTradeCertImpl().updateAuditTrailFieldForOnlineApplication(tmgr, this.verifyApproveTradeCertDobj, field, appl_details.getCurrent_action_cd());
                        remarksUpdatedInAuditTrail = true;

                        if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {   /////// INSERT NEW ITERATION IN TRADE CERTIFICATE AUDIT TRAIL 

                            if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION) {

                                if (status.getPrev_action_cd_selected() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE) {
                                    vaTradeCertStatus = "P";
                                    vaTradeCertAuditTrailStatus = "P";
                                    applicationTradeCertImpl.insertIntoVhaTradeCertAuditTrail(tmgr, this.verifyApproveTradeCertDobj);
                                    applicationTradeCertImpl.updateVaTradeCertAuditTrail(tmgr, this.verifyApproveTradeCertDobj, vaTradeCertAuditTrailStatus);
                                }

                            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE) {

                                if (status.getPrev_action_cd_selected() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE) {
                                    vaTradeCertStatus = "P";
                                    vaTradeCertAuditTrailStatus = "P";
                                    applicationTradeCertImpl.insertIntoVhaTradeCertAuditTrail(tmgr, this.verifyApproveTradeCertDobj);
                                    applicationTradeCertImpl.updateVaTradeCertAuditTrail(tmgr, this.verifyApproveTradeCertDobj, vaTradeCertAuditTrailStatus);
                                } else if (status.getPrev_action_cd_selected() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION) {
                                    vaTradeCertStatus = "R";
                                    vaTradeCertAuditTrailStatus = "R";
                                    applicationTradeCertImpl.insertIntoVhaTradeCertAuditTrail(tmgr, this.verifyApproveTradeCertDobj);
                                    applicationTradeCertImpl.updateVaTradeCertAuditTrail(tmgr, this.verifyApproveTradeCertDobj, vaTradeCertAuditTrailStatus);
                                }

                            }
                        }

                    } catch (VahanException ve) {
                        LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application receive status...", "Error occurred while updating forwarding remarks in the audit trail for the pending application. {message[" + ve.getMessage() + "]}"));
                    }
                    if (fileMoveReturn.contains("SUCCESS") && auditTrailReturn.contains("SUCCESS") && remarksUpdatedInAuditTrail) {

                        /////////////// FOR SINGLE VEHICLE CATEGORY IN TRADE CERTIFICATE APPLICATION...........
                        ////////////// ADDING VALUE OF VEHICLE CATEGORY FROM FETCHED APPLICATION DATA
                        List<VerifyApproveTradeCertDobj> dobjListTemp = new ArrayList<>();
                        if (tmConfigDobj.getTmTradeCertConfigDobj().isApplToHaveSingleVehCatg()) {  /// FOR DELHI & OTHERS ............ EXCEPT FOR UP & Bihar
                            for (Object keyObj : mapSectionsSrNoOfSelectedDealer.keySet()) {
                                VerifyApproveTradeCertDobj applicationTradeCertDobjFrom = (VerifyApproveTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
                                this.verifyApproveTradeCertDobj.setVehCatgFor(applicationTradeCertDobjFrom.getVehCatgFor());
                                break;
                            }
                            dobjListTemp.add(this.verifyApproveTradeCertDobj);
                            saveReturn = new ApplicationTradeCertImpl().submitOnlineApplication(dobjListTemp, vaTradeCertStatus, tmgr, tradeCertType.trim());
                        } else {
                            for (Object keyObj : mapSectionsSrNoOfSelectedDealer.keySet()) {
                                VerifyApproveTradeCertDobj applicationTradeCertDobjFrom = (VerifyApproveTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
                                dobjListTemp.add(applicationTradeCertDobjFrom);
                            }
                            saveReturn = new ApplicationTradeCertImpl().submitOnlineApplication(dobjListTemp, vaTradeCertStatus, tmgr, tradeCertType.trim());
                        }

                        if (saveReturn.contains("SUCCESS")) {
                            if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                                returnLocation = disapprovalPrint();
                            } else {
                                returnLocation = "seatwork";
                            }
                        } else {
                            JSFUtils.setFacesMessage("There are some problems in submitting online application for trade certificates due to {message[Status Not Updated]}", null, JSFUtils.ERROR);
                        }

                    }

                }

            }

        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application receive status...", "Error occurred while submitting to [" + getStatusDescription(vaTradeCertStatus, true) + "] stage. {message[" + ve.getMessage() + "]}"));
        } catch (Exception ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application receive status...", "Error occurred while submitting to [" + getStatusDescription(vaTradeCertStatus, true) + "] stage. {message[" + ve.getMessage() + "]}"));
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application receive status...", "Error occurred while releasing transaction manager [" + getStatusDescription(currentTradeCertStatus, true) + "] stage. {message[" + ex.getMessage() + "]}"));
            }
        }
        if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
            PrimeFaces.current().executeScript("PF('confirmationPopup').show()");
        }
        return returnLocation;

    }

    @Override
    public ArrayList<ComparisonBean> getCompBeanList() {
        return this.compBeanList;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ApproveImpl getApproveImpl() {
        return approveImpl;
    }

    public void setApproveImpl(ApproveImpl approveImpl) {
        this.approveImpl = approveImpl;
    }

    public boolean isRenderFileMovement() {
        return renderFileMovement;
    }

    public boolean isTradeCertNoNotBlank() {
        return tradeCertNoNotBlank;
    }

    public String getTradeCertType() {
        return tradeCertType;
    }

    public String getRejectionReasons() {
        return rejectionReasons;
    }

    public void setRejectionReasons(String rejectionReasons) {
        this.rejectionReasons = rejectionReasons;
    }

    public String getDeficiencyMailContent() {
        return deficiencyMailContent;
    }

    public void setDeficiencyMailContent(String deficiencyMailContent) {
        this.deficiencyMailContent = deficiencyMailContent;
    }

    public String getStateNameMentionedInDeficiencyMail() {
        return stateNameMentionedInDeficiencyMail;
    }

    public void setStateNameMentionedInDeficiencyMail(String stateNameMentionedInDeficiencyMail) {
        this.stateNameMentionedInDeficiencyMail = stateNameMentionedInDeficiencyMail;
    }

    public String getApplicantMailId() {
        return applicantMailId;
    }

    public void setApplicantMailId(String applicantMailId) {
        this.applicantMailId = applicantMailId;
    }

    public String getDeficiencyMailSubject() {
        return deficiencyMailSubject;
    }

    public void setDeficiencyMailSubject(String deficiencyMailSubject) {
        this.deficiencyMailSubject = deficiencyMailSubject;
    }

    public String getApplicantNameInDeficiencyMail() {
        return applicantNameInDeficiencyMail;
    }

    public void setApplicantNameInDeficiencyMail(String applicantNameInDeficiencyMail) {
        this.applicantNameInDeficiencyMail = applicantNameInDeficiencyMail;
    }

    public String getApplicationNoInDeficiencyMail() {
        return applicationNoInDeficiencyMail;
    }

    public void setApplicationNoInDeficiencyMail(String applicationNoInDeficiencyMail) {
        this.applicationNoInDeficiencyMail = applicationNoInDeficiencyMail;
    }

    public String getApplicantPhoneNumber() {
        return applicantPhoneNumber;
    }

    public void setApplicantPhoneNumber(String applicantPhoneNumber) {
        this.applicantPhoneNumber = applicantPhoneNumber;
    }

    public boolean isRenderVerifyOnlineApplication() {
        return renderVerifyOnlineApplication;
    }

    public void setRenderVerifyOnlineApplication(boolean renderVerifyOnlineApplication) {
        this.renderVerifyOnlineApplication = renderVerifyOnlineApplication;
    }

    public boolean isRenderReceiveOnlineApplication() {
        return renderReceiveOnlineApplication;
    }

    public void setRenderReceiveOnlineApplication(boolean renderReceiveOnlineApplication) {
        this.renderReceiveOnlineApplication = renderReceiveOnlineApplication;
    }

    public boolean isDetailsShown() {
        return detailsShown;
    }

    public void setDetailsShown(boolean detailsShown) {
        this.detailsShown = detailsShown;
    }

    public String getCurrentTimeStampAsMarkHardCopyReceivedInStringFormat() {
        return currentTimeStampAsMarkHardCopyReceivedInStringFormat;
    }

    public void setCurrentTimeStampAsMarkHardCopyReceivedInStringFormat(String currentTimeStampAsMarkHardCopyReceivedInStringFormat) {
        this.currentTimeStampAsMarkHardCopyReceivedInStringFormat = currentTimeStampAsMarkHardCopyReceivedInStringFormat;
    }

    public boolean isRenderApproveOnlineApplication() {
        return renderApproveOnlineApplication;
    }

    public void setRenderApproveOnlineApplication(boolean renderApproveOnlineApplication) {
        this.renderApproveOnlineApplication = renderApproveOnlineApplication;
    }

    public String getDmsFileServerAPIString() {
        return dmsFileServerAPIString;
    }

    public boolean isAllDocumentsChecked() {
        return allDocumentsChecked;
    }

    public String getBackwardRemark() {
        return backwardRemark;
    }

    public void setBackwardRemark(String backwardRemark) {
        this.backwardRemark = backwardRemark;
    }

    public void sendRejectionMessages() throws VahanException {
        try {

            if (generateDeficiencyMailAndSend()) {
                generateSMS();

                ///////////////// UPDATE TRADE CERTIFICATE AUDIT TRAIL //////////////////
                if (!mapSectionsSrNoOfSelectedDealer.isEmpty() && this.verifyApproveTradeCertDobj == null) {
                    for (Object keyObj : mapSectionsSrNoOfSelectedDealer.keySet()) {
                        VerifyApproveTradeCertDobj applicationTradeCertDobjFrom = (VerifyApproveTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
                        this.verifyApproveTradeCertDobj = applicationTradeCertDobjFrom;
                        break;
                    }
                    fillLoginDetails(this.verifyApproveTradeCertDobj);
                }
                String field = "";
                String status = "MG";  /// Mail Generated
                if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE) {
                    this.verifyApproveTradeCertDobj.setReceiverDeficiencyMailContent(this.rejectionReasons);
                    field = "receiver_deficiency_mail_content";
                    this.verifyApproveTradeCertDobj.setReceiver(loginUser);
                } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION) {
                    this.verifyApproveTradeCertDobj.setVerifierDeficiencyMailContent(this.rejectionReasons);
                    field = "verifier_deficiency_mail_content";
                    this.verifyApproveTradeCertDobj.setVerifier(loginUser);
                } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE) {
                    this.verifyApproveTradeCertDobj.setApproverDeficiencyMailContent(this.rejectionReasons);
                    field = "approver_deficiency_mail_content";
                    this.verifyApproveTradeCertDobj.setApprover(loginUser);
                }
                this.verifyApproveTradeCertDobj.setStatus(status);
                new ApplicationTradeCertImpl().updateAuditTrailFieldForOnlineApplication(null, this.verifyApproveTradeCertDobj, field, appl_details.getCurrent_action_cd());
                /////////////////////////////////////////////////////////////////////////

                JSFUtils.setFacesMessage("Mail has been sent to the applicant on [" + getStatusDescription(status, true) + "] stage by " + ServerUtil.getOfficeName(Util.getUserSeatOffCode(), Util.getUserStateCode()), null, JSFUtils.INFO);
                setMailSent(true);
            }
        } catch (VahanException ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application receive status...", "Error occurred while generating mail and sending through sms. {message[" + ve.getMessage() + "]}"));
        }
        if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
            PrimeFaces.current().executeScript("PF('confirmationPopup').show()");
        }
    }

    public boolean generateDeficiencyMailAndSend() throws VahanException {
        FacesMessage fMessage = new FacesMessage();
        MailSender sendMail = null;
        boolean mailSent = true;

        if (rejectionReasons.length() > 400) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Mail generation status...", "Error occurred while generating mail as the length of content entered exceeds the maximum limit of 400 characters."));
            return false;
        } else if (rejectionReasons != null && !rejectionReasons.equals("")
                && (rejectionReasons.contains("|") || rejectionReasons.contains("="))) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Mail generation status...", "Error occurred while generating mail as the content contains = or | as invalid characters."));
            return false;
        }

        String message = "Dear " + applicantNameInDeficiencyMail + ",<br/><br/>"
                + "The following discrepancies in online trade certificate application [No: " + applicationNoInDeficiencyMail + "] have been found which are required to be rectified on urgent basis:- <br/><br/>"
                + rejectionReasons + "<br/><br/>"
                + "Best Regards,<br/>"
                + "Transport Department Government Of " + stateNameMentionedInDeficiencyMail + "<br/><br/><br/><br/>"
                + " *** NOTE: THIS IS A SYSTEM GENERATED EMAIL, PLEASE DO NOT REPLY *** ";

        try {
            //NICMailer.doSendMail(message, applicantMailId, deficiencyMailSubject);

            sendMail = new MailSender(applicantMailId, message, deficiencyMailSubject, fMessage);
            sendMail.start();
            sendMail.join();
        } catch (Exception ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            throw new VahanException(ve.getMessage());
        }
        if (fMessage.getSummary() != null && fMessage.getSummary().contains("Error while sending deficiency mail")) {
            JSFUtils.setFacesMessage("! " + fMessage.getSummary(), null, JSFUtils.ERROR);
            mailSent = false;
        }
        return mailSent;
    }

    public void generateDeficiencyMailOpeningMatter() {

        this.setRejectionReasons("");

        if (this.verifyApproveTradeCertDobj == null && !mapSectionsSrNoOfSelectedDealer.isEmpty()) {
            for (Object keyObj : mapSectionsSrNoOfSelectedDealer.keySet()) {
                VerifyApproveTradeCertDobj applicationTradeCertDobjFrom = (VerifyApproveTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
                this.verifyApproveTradeCertDobj = applicationTradeCertDobjFrom;
                fillLoginDetails(this.verifyApproveTradeCertDobj);
                break;
            }
        }
        applicantNameInDeficiencyMail = this.verifyApproveTradeCertDobj.getDealerMasterDobj().getDealerName();
        applicationNoInDeficiencyMail = this.verifyApproveTradeCertDobj.getApplNo();
        deficiencyMailContent = "Dear <b>" + applicantNameInDeficiencyMail + "</b>,<br/> "
                + "The following discrepancies in online trade certificate application [ No: <b><i>" + applicationNoInDeficiencyMail + "</i></b> ] have been found which are required to be rectified on urgent basis:- <br/>";
        stateNameMentionedInDeficiencyMail = this.verifyApproveTradeCertDobj.getDealerMasterDobj().getStateName().trim();
        applicantMailId = this.verifyApproveTradeCertDobj.getDealerMasterDobj().getEmailId().trim();
        deficiencyMailSubject = "[Vahan] Discrepancies have been found in online trade certificate application.";
        applicantPhoneNumber = this.verifyApproveTradeCertDobj.getDealerMasterDobj().getContactNo();

    }

    public void generateSMS() throws VahanException {

        try {
            String smsString = "[Apply For Trade Certificate]: Discrepancies in online trade certificate application [ No:" + applicationNoInDeficiencyMail + " ] have been found which are required to be rectified on urgent basis. For details please check mail in [ mailId: " + applicantMailId + " ] account.";
            ServerUtil.sendSMS(applicantPhoneNumber, smsString);
        } catch (Exception ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            throw new VahanException(ve.getMessage());
        }
    }

    public void markHardCopyReceived() {
        currentTimeStampAsMarkHardCopyReceivedInStringFormat = new java.text.SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new java.util.Date());
        setHardCopiesReceived(true);
        if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE) {
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:markHardCopyReceivedOutputPanel");
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:hardCopyPhysicallyReceivedTimeStamp");
        } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION) {
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:markHardCopyReceivedOutputPanel_for_verify");
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:hardCopyPhysicallyReceivedTimeStamp_for_verify");
        } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE) {
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:markHardCopyReceivedOutputPanel_for_approve");
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:hardCopyPhysicallyReceivedTimeStamp_for_approve");
        }

        PrimeFaces.current().executeScript("PF('bui').hide()");

    }

    public void preRevertOperation() {
        String vaTradeCertStatus = "";
        try {
            if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE) {
                vaTradeCertStatus = "R";
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION) {
                vaTradeCertStatus = "V";
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE) {
                vaTradeCertStatus = "A";
            }
            if (!mapSectionsSrNoOfSelectedDealer.isEmpty() && this.verifyApproveTradeCertDobj == null) {
                for (Object keyObj : mapSectionsSrNoOfSelectedDealer.keySet()) {
                    VerifyApproveTradeCertDobj applicationTradeCertDobjFrom = (VerifyApproveTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
                    this.verifyApproveTradeCertDobj = applicationTradeCertDobjFrom;
                    break;
                }
                fillLoginDetails(this.verifyApproveTradeCertDobj);
            }
            String temporaryApplNo = ApplicationTradeCertImpl.fetchTemporaryApplNoFromOnlineSchemaVhaTradeCertAppl(this.verifyApproveTradeCertDobj.getDealerFor(), this.verifyApproveTradeCertDobj.getApplNo());
            this.verifyApproveTradeCertDobj.setTempApplNo(temporaryApplNo);
            boolean reqDocsUploaded = ApplicationTradeCertImpl.isRequiredDocsUploaded(this.verifyApproveTradeCertDobj.getTempApplNo());
            if (reqDocsUploaded) {
                if (CommonUtils.isNullOrBlank(currentTimeStampAsMarkHardCopyReceivedInStringFormat) && isShowHardCopyReceivedButton()) {
                    JSFUtils.setFacesMessage("! Please receive hard copies of all mandatory documents along with hard copy of deficiency mail (if any)", null, JSFUtils.ERROR);
                    JSFUtils.setFacesMessage("and press 'Hard Copy Physically Received' before submitting the online pending application.", null, JSFUtils.ERROR);
                }
                ///// CHECK FOR DMS TICKS IN CASE OF REVERT
                if (isAllDocumentsChecked()) {
                    JSFUtils.setFacesMessage("! Application cannot be reverted back if all attached requisite and mandatory documents are checked", null, JSFUtils.WARN);
                    JSFUtils.setFacesMessage("or ticked. Kindly remove the check/tick with respect to the document selected for modification.", null, JSFUtils.WARN);
                }

                if (!isMailSent()) {
                    JSFUtils.setFacesMessage("! Please compose and send deficiency mail to applicant before reverting the application.", null, JSFUtils.ERROR);
                }
                if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
                    PrimeFaces.current().executeScript("PF('confirmationPopup').show()");
                } else {
                    this.setBackwardRemark("");
                    PrimeFaces.current().executeScript("PF('backwardGeneratePopupTC').show()");
                }
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application receive status...", "Error occurred while submitting to [" + getStatusDescription(vaTradeCertStatus, true) + "] stage. {message[" + ve.getMessage() + "]}"));
        }
        PrimeFaces.current().executeScript("PF('bui_revert_application').hide()");
    }

    public String revertPendingApplication() {
        try {

            ///////////////// UPDATE TRADE CERTIFICATE AUDIT TRAIL //////////////////
            if (!mapSectionsSrNoOfSelectedDealer.isEmpty() && this.verifyApproveTradeCertDobj == null) {
                for (Object keyObj : mapSectionsSrNoOfSelectedDealer.keySet()) {
                    VerifyApproveTradeCertDobj applicationTradeCertDobjFrom = (VerifyApproveTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
                    this.verifyApproveTradeCertDobj = applicationTradeCertDobjFrom;
                    break;
                }
                fillLoginDetails(this.verifyApproveTradeCertDobj);
            }
            String field = "";
            String status = "";
            if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE) {
                field = "receiver_backward_remarks";
                this.verifyApproveTradeCertDobj.setReceiverBackwardRemarks(this.backwardRemark);
                status = "M";   ///// TO BE MODIFIED BY APPLICANT
                this.verifyApproveTradeCertDobj.setReceiver(loginUser);
            }
            if (CommonUtils.isNullOrBlank(this.backwardRemark)) {
                JSFUtils.setFacesMessage("! Please enter reasons for reverting back to [" + getStatusDescription(status, true) + "] stage.", null, JSFUtils.ERROR);
            } else {
                this.setBackwardRemark("");
                this.verifyApproveTradeCertDobj.setStatus(status);
                this.verifyApproveTradeCertDobj.setCurrentTimeStampAsMarkHardCopyReceivedInStringFormat(currentTimeStampAsMarkHardCopyReceivedInStringFormat);
                new ApplicationTradeCertImpl().updateAuditTrailFieldForOnlineApplication(null, this.verifyApproveTradeCertDobj, field, appl_details.getCurrent_action_cd());
                generateMobileSmsNotification();
                JSFUtils.setFacesMessage("Online application has been revert back to [" + getStatusDescription(status, true) + "] stage on the behalf of " + ServerUtil.getOfficeName(Util.getUserSeatOffCode(), Util.getUserStateCode()), null, JSFUtils.INFO);
                return "seatwork";
            }

        } catch (VahanException ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application receive status...", "Error occurred while reverting the pending application. {message[" + ve.getMessage() + "]}"));
        }
        if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
            PrimeFaces.current().executeScript("PF('confirmationPopup').show()");
        }
        return "";
    }

    ////////////////////////////////// DMS UPLOAD UTILITY API  //////////////////////////////////
    public void openUploadedDocuments() {
        try {
            if (!mapSectionsSrNoOfSelectedDealer.isEmpty() && this.verifyApproveTradeCertDobj == null) {
                for (Object keyObj : mapSectionsSrNoOfSelectedDealer.keySet()) {
                    VerifyApproveTradeCertDobj applicationTradeCertDobjFrom = (VerifyApproveTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
                    this.verifyApproveTradeCertDobj = applicationTradeCertDobjFrom;
                    break;
                }
                fillLoginDetails(this.verifyApproveTradeCertDobj);
            }
            if (CommonUtils.isNullOrBlank(this.verifyApproveTradeCertDobj.getTempApplNo())) {
                this.verifyApproveTradeCertDobj.setTempApplNo(new ApplicationTradeCertImpl().fetchTemporaryApplNoFromOnlineSchemaVhaTradeCertAppl(this.verifyApproveTradeCertDobj.getDealerFor(), this.verifyApproveTradeCertDobj.getApplNo()));
            }
            displayDMSUploadUtility(this.verifyApproveTradeCertDobj.getTempApplNo());

        } catch (VahanException ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application receive status...", "Error occurred while opening attached documents in this respective application. {message[" + ve.getMessage() + "]}"));
        }
    }

    private void displayDMSUploadUtility(String tempApplNo) {
        try {

            if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE) {
                this.dmsFileServerAPIString = uploadUtils(tempApplNo, "R");
                PrimeFaces.current().ajax().update("new_trade_cert_application_subview:scandocument");
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION) {
                this.dmsFileServerAPIString = uploadUtils(tempApplNo, "V");
                //     PrimeFaces.current().ajax().update("new_trade_cert_application_subview:scandocument_for_verify");
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE) {
                this.dmsFileServerAPIString = uploadUtils(tempApplNo, "A");
                //    PrimeFaces.current().ajax().update("new_trade_cert_application_subview:scandocument_for_approve");
            }
            PrimeFaces.current().executeScript("PF('ShowImage').show();");
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", ve.getMessage()));
        }
    }

    public static String uploadUtils(String tempApplNo, String statusFlag) throws VahanException {
        String url = "";
        try {
            String sendUrl = "";
            String checkSumKey = "djkhf3874394";
            sendUrl = "?applno=" + tempApplNo + "&status=" + statusFlag + "&j_key=vVl/Az1yGsjOAG18WDeScg==&j_securityKey=6D6C069D681B40DBF95CAD7B3ED71BE1" + checkSumKey + "!TZFIMZbTiUtqXMfARJ1DGgyNicWFwYkwTA0ip/Q8Wns=";
            url = dmsTradeCertUploadedDocsUrl + "dealer-search-within-dms" + sendUrl;
            LOGGER.info("DMS URL: " + url);
            return url;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void handleShowImageClose(CloseEvent ce) {
        try {

            ///////////////////////// UNCOMMENT TO TEST WITHOUT DMS /////////////////////////
//            int noOfDocumentsUploaded = DMS_UPLOADED_DOCS_LIMIT;
            ////////////////////////////////////////////////////////////////////////////
            ///////////////////////// COMMENT TO TEST WITHOUT DMS /////////////////////////      
            int noOfDocumentsUploaded = 0;
            if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE) {
                noOfDocumentsUploaded = ApplicationTradeCertImpl.checkStatusOfAllDocuments(this.verifyApproveTradeCertDobj.getTempApplNo(), "R");
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION) {
                noOfDocumentsUploaded = ApplicationTradeCertImpl.checkStatusOfAllDocuments(this.verifyApproveTradeCertDobj.getTempApplNo(), "V");
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE) {
                noOfDocumentsUploaded = ApplicationTradeCertImpl.checkStatusOfAllDocuments(this.verifyApproveTradeCertDobj.getTempApplNo(), "A");
            }
            ////////////////////////////////////////////////////////////////////////////////

            if (noOfDocumentsUploaded == DMS_UPLOADED_DOCS_LIMIT) {
                setAllDocumentsChecked(true);
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Application receive status...", "All the requisite and mandatory documents have been checked."));
            } else {
                setAllDocumentsChecked(false);
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application receive status...", "Not all requisite and mandatory documents have been checked."));
            }
        } catch (Exception ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application receive status...", "Error occurred while fetching the status of check for all requisite and mandatory documents from database. {message[" + TableConstants.SomthingWentWrong + "]}"));
        }

        PrimeFaces.current().ajax().update("new_trade_cert_application_subview:controlButtonsReceiveApplicationPanel");
        PrimeFaces.current().executeScript("PF('documentViewerBlocker').hide();");
    }

    private boolean checkAllDocumentsHaveBeenVerified() {
        try {

            ///////////////////////// UNCOMMENT TO TEST WITHOUT DMS /////////////////////////
//            int noOfDocumentsUploaded = DMS_UPLOADED_DOCS_LIMIT;
            ////////////////////////////////////////////////////////////////////////////
            ///////////////////////// COMMENT TO TEST WITHOUT DMS /////////////////////////
            int noOfDocumentsUploaded = 0;
            if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE) {
                noOfDocumentsUploaded = ApplicationTradeCertImpl.checkStatusOfAllDocuments(this.verifyApproveTradeCertDobj.getTempApplNo(), "R");
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION) {
                noOfDocumentsUploaded = ApplicationTradeCertImpl.checkStatusOfAllDocuments(this.verifyApproveTradeCertDobj.getTempApplNo(), "V");
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE) {
                noOfDocumentsUploaded = ApplicationTradeCertImpl.checkStatusOfAllDocuments(this.verifyApproveTradeCertDobj.getTempApplNo(), "A");
            }
            ////////////////////////////////////////////////////////////////////////////////

            return noOfDocumentsUploaded == DMS_UPLOADED_DOCS_LIMIT ? true : false;

        } catch (Exception ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application receive status...", "Error occurred while fetching the status of check for all requisite and mandatory documents from database. {message[" + TableConstants.SomthingWentWrong + "]}"));
        }
        return false;
    }
    ///////END DMS CONFIGURATION////////////

    public void tradeCertValidFromDateChangeListener(SelectEvent event) {

        Calendar calValidFrom = Calendar.getInstance();
        Calendar currDate = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();

        calValidFrom.setTime(this.verifyApproveTradeCertDobj.getValidFromDate());
        cal.setTime(this.verifyApproveTradeCertDobj.getValidFromDate());
        currDate.setTime(new Date());

        if (currDate.before(calValidFrom)) {
            this.verifyApproveTradeCertDobj.setValidFromDate(currDate.getTime());
        }

        cal.add(Calendar.YEAR, 1);
        cal.add(Calendar.DATE, -1);
        this.verifyApproveTradeCertDobj.setValidUptoDate(cal.getTime());
    }

    public void generateMobileSmsNotification() throws VahanException {
        String mobile_no_otp = this.verifyApproveTradeCertDobj.getDealerMasterDobj().getContactNo();
        try {
            String smsString = "[Apply For Trade Certificate]: Attached uploaded documents within submitted application are required to be modified. "
                    + "Please use Application No[" + this.verifyApproveTradeCertDobj.getApplNo() + "] "
                    + "and Applicant Code[" + this.verifyApproveTradeCertDobj.getDealerMasterDobj().getDealerCode() + "] to open existing application "
                    + "and kindly do the needful at the earliest.";
            ServerUtil.sendSMS(mobile_no_otp, smsString);
        } catch (Exception ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public static String getStatusDescription(String currentStatus, boolean shortForm) {
        if (currentStatus.equals("P")) {  ///// Pending
            currentStatus = "Online Application Pending";
            if (shortForm) {
                currentStatus = "Pending";
            }
        } else if (currentStatus.equals("R")) { //// Received
            currentStatus = "Online Application Received";
            if (shortForm) {
                currentStatus = "Received";
            }
        } else if (currentStatus.equals("V")) {  //// Verified
            currentStatus = "Online Application Verified";
            if (shortForm) {
                currentStatus = "Verified";
            }
        } else if (currentStatus.equals("A")) {  //// Approved
            currentStatus = "Online Application Approved";
            if (shortForm) {
                currentStatus = "Approved";
            }
        } else if (currentStatus.equals("M")) {  ///// Modified By Applicant
            currentStatus = "Online Application To Be Modified By Applicant";
            if (shortForm) {
                currentStatus = "Modified By Applicant";
            }
        } else if (currentStatus.equals("MG")) {  ///// Mail Generated
            currentStatus = "Mail Generated For Online Application";
            if (shortForm) {
                currentStatus = "Mail Generated";
            }
        }
        return currentStatus;
    }

    public void setAllDocumentsChecked(boolean allDocumentsChecked) {
        this.allDocumentsChecked = allDocumentsChecked;
    }

    private void calculateFeeAndDisplayFeeSection() throws VahanException {
        try {
            this.noOfVehGrandTotal = 0;
            this.totalAmount = 0L;
            if (tmConfigDobj.getTmTradeCertConfigDobj().isFeeWithTaxPlusOtherVariables()) {   /// fee_with_tax_plus_other_variables work
                dobjList.clear();
                int totalFeeAmount = 0;
                int totalTaxAmount = 0;
                int totalSurcharge = 0;
                for (Object dobjObj : this.applicationSectionsList) {
                    VerifyApproveTradeCertDobj verifyApproveTradeCertDobjObj = (VerifyApproveTradeCertDobj) dobjObj;
                    tradeDobj = new TradeCertificateDobj();
                    tradeDobj.setApplicant_state_cd(verifyApproveTradeCertDobjObj.getStateCd());
                    tradeDobj.setApplicant_off_cd(verifyApproveTradeCertDobjObj.getOffCd() + "");
                    tradeDobj.setApplicant_cd(verifyApproveTradeCertDobjObj.getDealerFor());
                    tradeDobj.setApplicant_name(verifyApproveTradeCertDobjObj.getDealerMasterDobj().getDealerName());
                    TradeCertificateDobj.ListOfFeeDetail feeDobj = tradeDobj.new ListOfFeeDetail();
                    feeDobj.setVehCatgCode(verifyApproveTradeCertDobjObj.getVehCatgFor());
                    feeDobj.setNo_of_tc_required(Integer.valueOf(verifyApproveTradeCertDobjObj.getNoOfAllowedVehicles()));
                    feeDobj.setVch_class_appliedDescr(verifyApproveTradeCertDobjObj.getVehCatgName());
                    this.noOfVehGrandTotal += Integer.valueOf(verifyApproveTradeCertDobjObj.getNoOfAllowedVehicles());
                    dobjList.add(feeDobj);
                    generateFees(tradeDobj, dobjList, tradeCertType.toLowerCase());
                    totalFeeAmount += tradeDobj.getTotalFeeAmount();
                    totalTaxAmount += tradeDobj.getTotalTaxAmount();
                    totalSurcharge += tradeDobj.getTotalSurcharge();
                    this.totalAmount += tradeDobj.getTotalAmount();
                    tradeDobj.setTotalAmount(Integer.valueOf(this.totalAmount + ""));
                    verifyApproveTradeCertDobjObj.setFeesCollected(getFeeInCommaSeperatedFormat(this.totalAmount + ""));
                }
                tradeDobj.setTotalNoOfVehicles(noOfVehGrandTotal);
                tradeDobj.setTotalFeeAmount(totalFeeAmount);
                tradeDobj.setTotalTaxAmount(totalTaxAmount);
                tradeDobj.setTotalSurcharge(totalSurcharge);
                displayCalculatedFeeAndOtherFeeVariables = true;
            } else if (tmConfigDobj.getTmTradeCertConfigDobj().isFeeToBeHardCoded()) {  /// Chhattisgarh work (fee = 200) fix
                for (Object dobjObj : this.applicationSectionsList) {
                    VerifyApproveTradeCertDobj verifyApproveTradeCertDobjObj = (VerifyApproveTradeCertDobj) dobjObj;
                    int fee = 200;
                    verifyApproveTradeCertDobjObj.setFeesCollected(getFeeInCommaSeperatedFormat(fee + ""));
                    this.totalAmount += fee;
                    this.noOfVehGrandTotal += Integer.valueOf(verifyApproveTradeCertDobjObj.getNoOfAllowedVehicles());
                }
            } else {
                for (Object dobjObj : this.applicationSectionsList) {
                    VerifyApproveTradeCertDobj verifyApproveTradeCertDobjObj = (VerifyApproveTradeCertDobj) dobjObj;
                    int fee = getFeeFromFeeSlab(verifyApproveTradeCertDobjObj);
                    verifyApproveTradeCertDobjObj.setFeesCollected(getFeeInCommaSeperatedFormat(fee + ""));
                    this.totalAmount += fee;
                    this.noOfVehGrandTotal += Integer.valueOf(verifyApproveTradeCertDobjObj.getNoOfAllowedVehicles());
                }
            }
            this.totalAmountInCommaSeperatedString = getFeeInCommaSeperatedFormat(this.totalAmount + "");
            this.verifyApproveTradeCertDobj.setGrandTotalInWords(new Utility().ConvertNumberToWords((int) this.totalAmount));
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    private int getFeeFromFeeSlab(VerifyApproveTradeCertDobj dobj) throws VahanException {
        int fee = 0;
        String orginalVehCatg = dobj.getVehCatgFor();
        if (this.verifyApproveTradeCertDobj.getNewRenewalTradeCert().equalsIgnoreCase("DUPLICATE")) {
            dobj.setPurCd("22");
        } else {
            dobj.setPurCd("21");
        }
        if ("ERIK".equals(dobj.getVehCatgFor()) || "ECRT".equals(dobj.getVehCatgFor())) {
            dobj.setVehCatgFor("3WT");
        }
        fee = ApplicationTradeCertImpl.getPurposeCodeFeeForTradeCertificate(dobj);
        dobj.setPurCd(TableConstants.VM_TRANSACTION_TRADE_CERT_ONLINE_APPLICATION + "");
        dobj.setVehCatgFor(orginalVehCatg);
        fee = fee * Integer.valueOf(dobj.getNoOfAllowedVehicles());
        return fee;
    }

    private void generateFees(TradeCertificateDobj tradeDobj, List<TradeCertificateDobj.ListOfFeeDetail> dobjList, String radioButtonValue) {
        try {
            ApplicationTradeCertImpl impl = new ApplicationTradeCertImpl();
            if (radioButtonValue.equals("renew")) {
                String vchCatg = "";
                for (TradeCertificateDobj.ListOfFeeDetail dobj : dobjList) {
                    vchCatg = vchCatg + ",'" + dobj.getVehCatgCode() + "'";
                }
                vchCatg = vchCatg.substring(1, vchCatg.length());
                vchCatg = impl.getAlreadyAppliedVehicle(tradeDobj, vchCatg);
                if (!CommonUtils.isNullOrBlank(vchCatg)) {
                    vchCatg = vchCatg.substring(0, vchCatg.length() - 2);
                    JSFUtils.setFacesMessage("Please remove the vehicle category which are already applied: " + vchCatg, null, JSFUtils.WARN);
                    return;
                }
            }
            if (radioButtonValue.equals("duplicate")) {
                for (TradeCertificateDobj.ListOfFeeDetail dobj : dobjList) {
                    dobj.setNo_of_tc_required(dobj.getPreviousNoOfTcs());
                }
            }
            impl.generatingFees(tradeDobj, dobjList, radioButtonValue);
            tradeDobj.setRupeesInWords(new Utility().ConvertNumberToWords(tradeDobj.getTotalAmount()));
        } catch (VahanException ve) {
            LOGGER.error("[ED applicant:" + tradeDobj.getApplicant_name() + "] " + ve.toString() + " " + ve.getStackTrace()[0]);
            JSFUtils.setFacesMessage("There may be some problem occurs while generating Fees.", null, JSFUtils.WARN);
        }
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public String getTotalAmountInCommaSeperatedString() {
        return totalAmountInCommaSeperatedString;
    }

    public static String getFeeInCommaSeperatedFormat(String toFormat) throws VahanException {
        String temp = "";
        long number = 0l;
        try {
            number = Long.parseLong(toFormat);
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            temp = formatter.format(number);
        } catch (Exception nfe) {
            LOGGER.error(nfe.toString() + " " + nfe.getStackTrace()[0]);
            LOGGER.error("PublicApplicationTradeCertBean.getFeeInCommaSeperatedFormat:: EXCEPTION Message:::  " + nfe.getMessage() + ", EXCEPTION Cause::: " + nfe.getCause());
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return temp;
    }

    public TradeCertificateDobj getTradeDobj() {
        return tradeDobj;
    }

    public void setTradeDobj(TradeCertificateDobj tradeDobj) {
        this.tradeDobj = tradeDobj;
    }

    public boolean isDisplayCalculatedFeeAndOtherFeeVariables() {
        return displayCalculatedFeeAndOtherFeeVariables;
    }

    public void setDisplayCalculatedFeeAndOtherFeeVariables(boolean displayCalculatedFeeAndOtherFeeVariables) {
        this.displayCalculatedFeeAndOtherFeeVariables = displayCalculatedFeeAndOtherFeeVariables;
    }

    public List<TradeCertificateDobj.ListOfFeeDetail> getDobjList() {
        return dobjList;
    }

    private static List getListOfInspectionOfficiers() {
        Map mapOfAllInspectionOfficiers = null;
        List listOfAllInspectionOfficiers = new ArrayList<>();
        int offCd = Util.getSelectedSeat().getOff_cd();
        String stateCd = Util.getUserStateCode();
        ApplicationTradeCertImpl applicationTradeCertImpl = new ApplicationTradeCertImpl();
        try {
            mapOfAllInspectionOfficiers = applicationTradeCertImpl.getMapOfInspectionOfficiers(stateCd, offCd);
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        for (Object key : mapOfAllInspectionOfficiers.keySet()) {
            Integer userCd = (Integer) key;
            String userName = (String) mapOfAllInspectionOfficiers.get(key);

            listOfAllInspectionOfficiers.add(new SelectItem("" + userCd, userName));
        }
        Collections.sort(listOfAllInspectionOfficiers, new ApplicationTradeCertBean.ListComparator());
        return listOfAllInspectionOfficiers;
    }

    public List getListOfAllInspectionOfficiers() {
        return listOfAllInspectionOfficiers;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public String getInspectionBy() {
        return inspectionBy;
    }

    public void setInspectionBy(String inspectionBy) {
        this.inspectionBy = inspectionBy;
    }

    public Date getInspectionOn() {
        return inspectionOn;
    }

    public void setInspectionOn(Date inspectionOn) {
        this.inspectionOn = inspectionOn;
    }

    public String getInspectionRemark() {
        return inspectionRemark;
    }

    public void setInspectionRemark(String inspectionRemark) {
        this.inspectionRemark = inspectionRemark;
    }

    public String getInspectionOnAsString() {
        return inspectionOnAsString;
    }

    public void setInspectionOnAsString(String inspectionOnAsString) {
        this.inspectionOnAsString = inspectionOnAsString;
    }

    private void setInspectionByFromListOfAllInspectionOfficiers(String inspectedByCode) {
        for (Object selectItemObject : listOfAllInspectionOfficiers) {
            SelectItem selectItem = (SelectItem) selectItemObject;
            if (selectItem.getValue().equals(inspectedByCode)) {
                setInspectionByAsString(selectItem.getLabel());
                return;
            }
        }
    }

    public String getInspectionByAsString() {
        return inspectionByAsString;
    }

    public void setInspectionByAsString(String inspectionByAsString) {
        this.inspectionByAsString = inspectionByAsString;
    }

    public boolean isRenderInspectionPanel() {
        return renderInspectionPanel;
    }

    public boolean isHardCopiesReceived() {
        return hardCopiesReceived;
    }

    public void setHardCopiesReceived(boolean hardCopiesReceived) {
        this.hardCopiesReceived = hardCopiesReceived;
    }

    /**
     * @return the showHardCopyReceivedButton
     */
    public boolean isShowHardCopyReceivedButton() {
        return showHardCopyReceivedButton;
    }

    /**
     * @param showHardCopyReceivedButton the showHardCopyReceivedButton to set
     */
    public void setShowHardCopyReceivedButton(boolean showHardCopyReceivedButton) {
        this.showHardCopyReceivedButton = showHardCopyReceivedButton;
    }

    public boolean isMailSent() {
        return mailSent;
    }

    public void setMailSent(boolean mailSent) {
        this.mailSent = mailSent;
    }

}
