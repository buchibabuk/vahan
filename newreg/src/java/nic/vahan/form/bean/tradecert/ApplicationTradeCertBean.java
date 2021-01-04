/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.tradecert;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.java.util.CommonUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.FileMovementAbstractApplBean;
import nic.vahan.form.dobj.tradecert.ApplicationTradeCertDobj;
import nic.vahan.form.dobj.tradecert.IssueTradeCertDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.tradecert.ApplicationTradeCertImpl;
import nic.vahan.form.impl.ApproveImpl;
import nic.vahan.form.impl.tradecert.IssueTradeCertImpl;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.Util;
import nic.vahan.server.mail.MailSender;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.apache.commons.httpclient.util.DateUtil;
import org.primefaces.event.CloseEvent;
import static nic.vahan.form.bean.tradecert.VerifyApproveTradeCertBean.dmsTradeCertUploadedDocsUrl;
import static nic.vahan.form.bean.tradecert.VerifyApproveTradeCertBean.DMS_UPLOADED_DOCS_LIMIT;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.tradecert.VerifyApproveTradeCertDobj;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author tranC081
 */
@ManagedBean(name = "applicationTradeCert", eager = true)
@ViewScoped
public class ApplicationTradeCertBean extends FileMovementAbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(ApplicationTradeCertBean.class);
    @ManagedProperty(value = "#{approveImpl}")
    private ApproveImpl approveImpl;
    private ApplicationTradeCertDobj applicationTradeCertDobj;
    private final Map mapSectionsSrNoOfSelectedDealer;
    private final List listApplicationNo;
    private final List listDealerFor;
    private final List listVehCatgFilteredFor;
    private final List listFuelTypeFilteredFor;
    private final List<ApplicationTradeCertDobj> applicationSectionsList;
    private final List<ApplicationTradeCertDobj> newEntryList;
    private final List<ApplicationTradeCertDobj> newApplicationSectionsAddedList;
    private final List<String> listVehCatgAddedNotSavedInVaTradeCertificate;
    private final List<String> listFuelTypeAddedNotSavedInVaTradeCertificate;
    private int noOfVehGrandTotal;
    private String applicationNo;
    private boolean formValidated;
    private boolean notValid;
    private boolean visibleApplicationDetailPanel;
    private boolean visibleNewEntryDetailPanel;
    private boolean vehClassSelected;
    private boolean fuelTypeSelected;
    private boolean disableSave;
    private boolean disableSavePopup;
    private boolean disableAddNew;
    private Map mapVehCatgFilteredFor;
    private Map mapFuelTypeFilteredFor;
    private Map mapDealersFor;
    private boolean dissableChooseNewRenewTradeCertificateOption;
    private boolean showRenewTradeCertificateApplication;
    private boolean showVerifyTradeCertificateApplication;
    private boolean showNewTradeCertificateApplication;
    private boolean showDupTradeCertificateApplication;
    private boolean visibleTradeCertificateApplicationPanel;
    private boolean visibleApplicationVerifyPanel;
    private String chooseNewRenewTradeCertOption;
    private ArrayList<ComparisonBean> compBeanList;
    private boolean renderFileMovement;
    private boolean verifyMode;
    private boolean tradeCertNoNotBlank;
    private String tradeCertType;
    private final Set uniqueMessagesSet;
    private ArrayList expiredVehCatg;
    private ArrayList expiredFuelType;
    private final SessionVariables sessionVariables;
    private boolean displayFuel;
    private final List listOfExhaustedVehCatg;
    private final List vehicleClassCategoryMappingList;
    private String tcNewEditRadiobtn;
    private boolean editActiveTC;
    private boolean displayDealer;
    private List listOfActiveTC;
    private final List listOfCheckedTCRecordsToRenew;
    private boolean doNotShowNoOfVehicles;
    private String currentTimeStampAsMarkHardCopyReceivedInStringFormat;
    private String rejectionReasons;
    private String deficiencyMailContent;
    private String stateNameMentionedInDeficiencyMail;
    private String applicantMailId;
    private String deficiencyMailSubject;
    private String applicantNameInDeficiencyMail;
    private String applicationNoInDeficiencyMail;
    private String applicantPhoneNumber;
    private String dmsFileServerAPIString;  // "http://10.25.97.159:8080/dms-app/search-dms?applno=317013&j_key=vVl/Az1yGsjOAG18WDeScg==&j_securityKey=6D6C069D681B40DBF95CAD7B3ED71BE1djkhf3874394!TZFIMZbTiUtqXMfARJ1DGgyNicWFwYkwTA0ip/Q8Wns=";
    private boolean allDocumentsChecked;
    private TmConfigurationDobj tmConfigDobj;
    private boolean rtoSideAppl;
    private boolean applicationOperated;
    private boolean stockTransferReq;
    private boolean renderStockTransferReqCkbox;
    private final String ENDORSEMENT_CONSTANT_VAL = TableConstants.TRADE_CERT_ENDORSEMENT_CONSTANT_VAL;
    private boolean mailSent;
    private boolean showHardCopyReceivedButton;

    public ApplicationTradeCertBean() {

        applicationTradeCertDobj = new ApplicationTradeCertDobj();

        listDealerFor = new ArrayList();
        listVehCatgFilteredFor = new ArrayList();
        listFuelTypeFilteredFor = new ArrayList();
        applicationSectionsList = new ArrayList<>();
        newEntryList = new ArrayList<>();
        newApplicationSectionsAddedList = new ArrayList<>();
        listVehCatgAddedNotSavedInVaTradeCertificate = new ArrayList<>();
        listFuelTypeAddedNotSavedInVaTradeCertificate = new ArrayList<>();
        vehicleClassCategoryMappingList = new ArrayList();
        listApplicationNo = new ArrayList<>();
        mapSectionsSrNoOfSelectedDealer = new HashMap();
        compBeanList = new ArrayList<>();
        expiredVehCatg = new ArrayList();
        expiredFuelType = new ArrayList();
        uniqueMessagesSet = new HashSet();
        listOfExhaustedVehCatg = new ArrayList();
        sessionVariables = new SessionVariables();
        listOfActiveTC = new ArrayList();
        listOfCheckedTCRecordsToRenew = new ArrayList();
        currentTimeStampAsMarkHardCopyReceivedInStringFormat = "";

    }

    @PostConstruct
    public void init() {

        try {
            tmConfigDobj = Util.getTmConfiguration();
        } catch (VahanException ex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            return;
        }

        if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {   /// for GUJRAT FUEL-TYPE work
            displayFuel = true;
        }
        if (tmConfigDobj.getTmTradeCertConfigDobj().isNoOfVehiclesNotToBeShown()) {   /// for Chhattisgarh 
            doNotShowNoOfVehicles = true;
        }
        if (tmConfigDobj.getTmTradeCertConfigDobj().isStockTransferReq()) {   /// for stock transfer required by other states
            renderStockTransferReqCkbox = true;
        }
        fillLoginDetails(this.applicationTradeCertDobj);
        reset();

        SeatAllotedDetails seatAllotedDetails = Util.getSelectedSeat();

        this.chooseNewRenewTradeCertOption = "New_Trade_Certificate";

        if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_VERIFICATION
                || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_VERIFICATION_DUP
                || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_APPROVE
                || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_APPROVE_DUP) {
            this.applicationTradeCertDobj.setApplNo(seatAllotedDetails.getAppl_no());
            fillApplicationSectionsListVerifyForSelectedApplication(this.applicationTradeCertDobj.getApplNo());
            renderFileMovement = true;
            showNewTradeCertificateApplication = false;
            showDupTradeCertificateApplication = false;
            verifyMode = true;
        }

        this.disableAddNew = false;

        disableSave = true;

        if (tmConfigDobj.getTmTradeCertConfigDobj().isDocumentsUploadNRevert() && !rtoSideAppl) {

            if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_APPL_ENTRY
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_APPL_ENTRY_DUP) {
                JSFUtils.setFacesMessage("Application for trade certificate is not allowed to be inverted as DMS is enabled for " + this.applicationTradeCertDobj.getStateName() + " state, so please apply online.", null, JSFUtils.ERROR);
                setApplicationOperated(true);
                PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
                PrimeFaces.current().executeScript("PF('confirmationPopup').show()");
                PrimeFaces.current().executeScript("PF('receiveApplicationBlocker').show()");
                return;
            }

            //DMS URL fetch by vahan4 dblink table
            try {
                fillDMSDetails();

                DMS_UPLOADED_DOCS_LIMIT = ApplicationTradeCertImpl.getNumberOfDocumentsRequiredToUpload(this.applicationTradeCertDobj.getStateCd(), this.applicationTradeCertDobj.getNewRenewalTradeCert(), this.applicationTradeCertDobj.getApplicantCategory());
                if (!CommonUtils.isNullOrBlank(this.applicationTradeCertDobj.getApplNo())) {

                    try {

                        if (ApplicationTradeCertImpl.isApplicationReverted(applicationTradeCertDobj.getApplNo(), this.applicationTradeCertDobj.getStateCd(), this.applicationTradeCertDobj.getOffCd())) {
                            JSFUtils.setFacesMessage("Application has been reverted by " + ServerUtil.getOfficeName(Util.getUserSeatOffCode(), Util.getUserStateCode()), null, JSFUtils.ERROR);
                            setApplicationOperated(true);
                            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopupOnlineApp");
                            PrimeFaces.current().executeScript("PF('confirmationPopupOnlineApp').show()");
                        }

                        if (ApplicationTradeCertImpl.documentsNotUploaded(this.applicationTradeCertDobj.getApplNo())) {
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
            } catch (Exception ve) {
                LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
                LOGGER.error("ApplicationTradeCertBean.init(...):: EXCEPTION Message:::  " + ve.getMessage() + ", EXCEPTION Cause::: " + ve.getCause());
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application processing status... ", "Exception occured while fetching DMS URL stored in database. {message::[" + ve.getMessage() + "]}"));
            }

        }
    }

    //Note:Please use DMS Utils for Session Details
    private void fillDMSDetails() throws VahanException {
        try {
            ApplicationTradeCertImpl applicationTradeCertImpl = new ApplicationTradeCertImpl();
            Map dmsMap = applicationTradeCertImpl.getDMSUrlMap();
            VerifyApproveTradeCertBean.dmsTradeCertUploadedDocsUrl = (String) dmsMap.get("dms_url_tc");
            //////////// STAGING
            //VerifyApproveTradeCertBean.dmsTradeCertUploadedDocsUrlWithinServer = (String) dmsMap.get("dms_url_ser_tc");   ///// NOT USED BY NETPROPHET TEAM
            //////////// LOCAL/LIVE
            VerifyApproveTradeCertBean.dmsTradeCertUploadedDocsUrlWithinServer = (String) dmsMap.get("dms_url_tc");         /////     USED BY NETPROPHET TEAM 
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            LOGGER.error("VerifyApproveTradeCertBean.init(...):: EXCEPTION Message:::  " + e.getMessage() + ", EXCEPTION Cause::: " + e.getCause());
            throw new VahanException("Exception occured while fetching DMS URL stored in database.");
        }
    }

    private void fillDealers() {

        int offCd = Util.getSelectedSeat().getOff_cd();
        String stateCd = Util.getUserStateCode();

        ApplicationTradeCertImpl applicationTradeCertImpl = new ApplicationTradeCertImpl();
        try {
            if ("Renew_Trade_Certificate".equals(chooseNewRenewTradeCertOption) || isEditActiveTC()) {
                mapDealersFor = applicationTradeCertImpl.getDealerMap(stateCd, offCd, true);
            } else {
                mapDealersFor = applicationTradeCertImpl.getDealerMap(stateCd, offCd, false);
            }
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

        for (Object key : mapDealersFor.keySet()) {
            String keyDealerFor = (String) key;
            String dealerDescr = (String) mapDealersFor.get(key);

            listDealerFor.add(new SelectItem("" + keyDealerFor, dealerDescr));
        }
        Collections.sort(listDealerFor, new ListComparator());

    }

    private void fillApplicationNoRenew() {
        ApplicationTradeCertImpl applicationTradeCertImpl = new ApplicationTradeCertImpl();
        Map mapApplicationFor = null;
        try {
            mapApplicationFor = applicationTradeCertImpl.getApplicationMap();

        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        for (Object keyObj : mapApplicationFor.keySet()) {
            String key = (String) keyObj;
            String descr = (String) mapApplicationFor.get(key);
            listApplicationNo.add(new SelectItem("" + key, descr));
        }
    }

    private void fillApplicationSectionsListRenewOrDup() {
        this.applicationSectionsList.clear();
        this.noOfVehGrandTotal = 0;
        Map<String, Integer> TcNoVchCatgMap = new HashMap<>();
        boolean duplicateValuesExist = false;
        if (!mapSectionsSrNoOfSelectedDealer.isEmpty()) {
            for (Object keyObj : mapSectionsSrNoOfSelectedDealer.keySet()) {
                String key = (String) keyObj;
                ApplicationTradeCertDobj dobj = (ApplicationTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(key);
                if (TcNoVchCatgMap.containsKey(dobj.getTradeCertNo() + ":" + dobj.getVehCatgFor() + ":" + dobj.getFuelTypeFor())) {
                    TcNoVchCatgMap.put(dobj.getTradeCertNo() + ":" + dobj.getVehCatgFor() + ":" + dobj.getFuelTypeFor(), ((Integer) TcNoVchCatgMap.get(dobj.getTradeCertNo() + ":" + dobj.getVehCatgFor() + ":" + dobj.getFuelTypeFor())) + 1);
                    duplicateValuesExist = true;
                } else {
                    TcNoVchCatgMap.put(dobj.getTradeCertNo() + ":" + dobj.getVehCatgFor() + ":" + dobj.getFuelTypeFor(), 1);
                }
                this.applicationSectionsList.add(dobj);
                this.noOfVehGrandTotal += Integer.valueOf(dobj.getNoOfAllowedVehicles());
                fillLoginDetails(dobj);
                String purCd = null;
                if (this.applicationTradeCertDobj.getNewRenewalTradeCert() != null && "Duplicate_Trade_Certificate".equalsIgnoreCase(this.applicationTradeCertDobj.getNewRenewalTradeCert())) {
                    purCd = String.valueOf(TableConstants.VM_TRANSACTION_TRADE_CERT_DUP);
                } else {
                    purCd = String.valueOf(TableConstants.VM_TRANSACTION_TRADE_CERT_NEW);
                }
                dobj.setPurCd(purCd);
            }
            if (duplicateValuesExist) {
                JSFUtils.setFacesMessage("Following duplicate Trade Certificate records have been found:-", null, JSFUtils.ERROR);
                for (String tcRecId : TcNoVchCatgMap.keySet()) {
                    if (((Integer) TcNoVchCatgMap.get(tcRecId)) > 1) {
                        JSFUtils.setFacesMessage(tcRecId, null, JSFUtils.ERROR);
                    }
                }
                JSFUtils.setFacesMessage("Please proceed towards Trade Certificate cancellation and then apply for new Trade Certificate", null, JSFUtils.ERROR);
            }
//            for (Object keyObj : mapSectionsSrNoOfSelectedDealer.keySet()) {
//                this.applicationTradeCertDobj = (ApplicationTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
//                break;
//            }

            dissableChooseNewRenewTradeCertificateOption = true;
            disableSave = false;

        } else {
            dissableChooseNewRenewTradeCertificateOption = false;
            disableSave = true;

        }

    }

    public void fillNewRenewTradeCerticateOption(AjaxBehaviorEvent event) {
        verifyMode = true; //// TO MAKE CHOOSER INVISIBLE
        chooseNewRenewTradeCertOption = this.applicationTradeCertDobj.getNewRenewalTradeCert();
        if (tmConfigDobj.getTmTradeCertConfigDobj().isDuplicateTcNotApplicable() && "Duplicate_Trade_Certificate".equals(chooseNewRenewTradeCertOption)) {  ///for Chhattisgarh
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "'Apply for Duplicate Trade Certificate' has NOT been currently deployed for 'Chhattisgarh' state.", "'Apply for Duplicate Trade Certificate' has NOT been currently deployed for 'Chhattisgarh' state.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            // RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
            PrimeFaces.current().executeScript("PF('confDlgTradeCert').show()");
            return;
        }
        chooseOperation();
    }

    private void chooseOperation() {

        boolean enablingOneTimeChooseOption = false;
        if ("New_Trade_Certificate".equals(chooseNewRenewTradeCertOption)) {
            reset();
            showNewTradeCertificateApplication = true;
            showRenewTradeCertificateApplication = false;
            showDupTradeCertificateApplication = false;
            showVerifyTradeCertificateApplication = false;
            this.applicationTradeCertDobj.setPurCd(TableConstants.VM_TRANSACTION_TRADE_CERT_NEW + "");

            if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {   /// for GUJRAT FUEL-TYPE work
                if (listVehCatgFilteredFor.size() == 1 || listFuelTypeFilteredFor.size() == 1) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select another 'dealer' or choose another option (renew / issue duplicate) as trade certificate(s) has already been issued to all combinations of (dealer[Selected],vehicle categories[All] and fuel type[All]).", "Please select another 'dealer' or choose another option (renew / issue duplicate) as trade certificate(s) has already been issued to all combinations of (dealer[Selected],vehicle categories[All] and fuel type[All]).");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
            } else {
                if (listVehCatgFilteredFor.size() == 1) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select another 'dealer' or choose another option (renew / issue duplicate) as trade certificate(s) has already been issued to all combinations of (dealer[Selected] and vehicle categories[All]).", "Please select another 'dealer' or choose another option (renew / issue duplicate) as trade certificate(s) has already been issued to all combinations of (dealer[Selected] and vehicle categories[All]).");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
            }
            dissableChooseNewRenewTradeCertificateOption = false;
            reset();
            if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
                showNewTradeCertificateApplication = false;
            } else {
                showNewTradeCertificateApplication = true;
            }
            enablingOneTimeChooseOption = true;
            this.applicationTradeCertDobj.setNewRenewalTradeCert("New_Trade_Certificate");
            disableSave = true;
        } else if ("Renew_Trade_Certificate".equals(chooseNewRenewTradeCertOption)) {
            reset();
            showNewTradeCertificateApplication = false;
            showRenewTradeCertificateApplication = true;
            showDupTradeCertificateApplication = false;
            showVerifyTradeCertificateApplication = false;
            this.applicationTradeCertDobj.setPurCd(TableConstants.VM_TRANSACTION_TRADE_CERT_NEW + "");
            this.applicationTradeCertDobj.setNewRenewalTradeCert("Renew_Trade_Certificate");
            disableSave = false;
        } else {
            reset();
            showNewTradeCertificateApplication = false;
            showRenewTradeCertificateApplication = false;
            showDupTradeCertificateApplication = true;
            showVerifyTradeCertificateApplication = false;
            this.applicationTradeCertDobj.setPurCd(TableConstants.VM_TRANSACTION_TRADE_CERT_DUP + "");
            this.applicationTradeCertDobj.setNewRenewalTradeCert("Duplicate_Trade_Certificate");
            disableSave = true;
        }

        if (!enablingOneTimeChooseOption) {
            this.dissableChooseNewRenewTradeCertificateOption = true;
        }

        PrimeFaces.current().ajax().update("new_trade_cert_application_subview:application_sections_panel");
        PrimeFaces.current().ajax().update("new_trade_cert_application_subview:renew_trade_cert");
        PrimeFaces.current().ajax().update("new_trade_cert_application_subview:new_entry_details");
        PrimeFaces.current().ajax().update("new_trade_cert_application_subview:bt_save");
        PrimeFaces.current().ajax().update("new_trade_cert_application_subview:application_sections_panel_renew");
        PrimeFaces.current().ajax().update("new_trade_cert_application_subview:application_sections_panel_dup");
        PrimeFaces.current().ajax().update("new_trade_cert_application_subview:cb_dealer_name");

        if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            // RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
            PrimeFaces.current().executeScript("PF('confDlgTradeCert').show()");
        }
    }

    public void checkVehicleCategorySelected(AjaxBehaviorEvent event) {

        if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {  // GUJRAT FUEL-TYPE WORK
            listFuelTypeFilteredFor.clear();
            fillFuelTypeFiltered(this.applicationTradeCertDobj.getVehCatgFor());
            this.applicationTradeCertDobj.setNoOfAllowedVehicles("");
            this.applicationTradeCertDobj.setFuelTypeFor("-1");
        }
    }

    public void checkDealerSelected(AjaxBehaviorEvent event) {
        resetListOfExhaustedVehCatg();
        dissableChooseNewRenewTradeCertificateOption = true;
        applicationTradeCertDobj.setNoOfAllowedVehicles("");
        applicationTradeCertDobj.setVehCatgFor("");
        resetListVehCatgAddedNotSavedInVaTradeCertificate();
        this.noOfVehGrandTotal = 0;
        applicationSectionsList.clear();
        visibleApplicationDetailPanel = false;
        initializeNewEntryDataTable();
        setVehClassSelected(false);
        this.applicationTradeCertDobj.setSrNo("1");
        this.applicationTradeCertDobj.setNewRenewalTradeCert("New_Trade_Certificate");
        if (isEditActiveTC()) {
            createActiveTCList(this.applicationTradeCertDobj.getDealerFor());
            if (listOfActiveTC.isEmpty()) {
                JSFUtils.showMessage("No active trade certificate(s) found for the selected dealer to be edited.");
                // RequestContext rc = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
                PrimeFaces.current().executeScript("PF('confDlgTradeCert').show()");
                return;
            }
        }
        fillVehCatagories();

        if (this.listVehCatgFilteredFor.size() == 1) {
            if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed() && this.listFuelTypeFilteredFor.size() == 1) {   //// GUJRAT Fuel-Type work)
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select another 'dealer' as trade certificate(s) has already been issued to all combinations of (dealer[Selected],vehicle categories[All] and fuel type[All]).", "Please select another 'dealer' as trade certificate(s) has already been issued to all combinations of (dealer[Selected],vehicle categories[All] and fuel type[All]).");
                FacesContext.getCurrentInstance().addMessage(null, message);
            } else {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select another 'dealer' as trade certificate(s) has already been issued to all combinations of (dealer[Selected] and vehicle categories[All]).", "Please select another 'dealer' as trade certificate(s) has already been issued to all combinations of (dealer[Selected] and vehicle categories[All]).");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            dissableChooseNewRenewTradeCertificateOption = false;
        }
        visibleNewEntryDetailPanel = true;
        this.newEntryList.clear();
        this.newEntryList.add(this.applicationTradeCertDobj);
        this.newApplicationSectionsAddedList.clear();
        this.disableAddNew = false;

        if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
            PrimeFaces.current().executeScript("PF('confDlgTradeCert').show()");
        }
    }

    public void checkDealerSelectedRenew(AjaxBehaviorEvent event) {
        this.listOfCheckedTCRecordsToRenew.clear();
        this.applicationSectionsList.clear();
        this.visibleTradeCertificateApplicationPanel = false;
        fillApplicationSectionsListRenewForSelectedDealer(this.applicationTradeCertDobj.getDealerFor());
        uniqueMessagesSet.clear();

    }

    private void fillApplicationSectionsListVerifyForSelectedApplication(String selectedApplication) {

        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        if (!"-1".equals(this.applicationTradeCertDobj.getApplNo())) {
            dissableChooseNewRenewTradeCertificateOption = true;
            mapSectionsSrNoOfSelectedDealer.clear();
            ApplicationTradeCertImpl applicationTradeCertImpl = new ApplicationTradeCertImpl();
            IssueTradeCertImpl issueTradeCertImpl = new IssueTradeCertImpl();
            try {
                applicationTradeCertImpl.getAllSrNoForSelectedApplication(selectedApplication, mapSectionsSrNoOfSelectedDealer);
            } catch (VahanException ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
            if (!mapSectionsSrNoOfSelectedDealer.isEmpty()) {
                for (Object keyObj : mapSectionsSrNoOfSelectedDealer.keySet()) {
                    ApplicationTradeCertDobj applicationTradeCertDobjFrom = (ApplicationTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
                    rtoSideAppl = applicationTradeCertDobjFrom.isRtoSideAppl();
                    this.applicationTradeCertDobj.setDealerFor(applicationTradeCertDobjFrom.getDealerFor());
                    this.applicationTradeCertDobj.setDealerName(applicationTradeCertDobjFrom.getDealerName());
                    this.applicationTradeCertDobj.setApplicantCategory(applicationTradeCertDobjFrom.getApplicantCategory());
                    this.applicationTradeCertDobj.setNewRenewalTradeCert(applicationTradeCertDobjFrom.getNewRenewalTradeCert());
                    try {
                        String tradeCertNo = issueTradeCertImpl.getTradeCertNo(applicationTradeCertDobjFrom.getVehCatgFor(), applicationTradeCertDobjFrom.getDealerFor(), applicationTradeCertDobjFrom.getFuelTypeFor());
                        applicationTradeCertDobjFrom.setTradeCertNo(tradeCertNo);
                        Date validUptoDate = issueTradeCertImpl.getValidUptoOnTradeCertNo(tradeCertNo);
                        applicationTradeCertDobjFrom.setValidUpto(validUptoDate);
                        if (validUptoDate != null) {
                            applicationTradeCertDobjFrom.setValidUptoAsString(format.format(validUptoDate));
                        }
                        if (tradeCertNo != null && !"".equals(tradeCertNo)) {
                            tradeCertNoNotBlank = true;
                        }
                        if (Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_DUP) {
                            tradeCertType = "DUPLICATE";
                        } else if ((Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_NEW) && (tradeCertNo != null && !"".equals(tradeCertNo))) {
                            if (applicationTradeCertDobjFrom.getStatus() != null && applicationTradeCertDobjFrom.getStatus().equals("E")) { /// E: Edit Trade Certificate
                                tradeCertType = "EDIT";
                            } else {
                                if (TableConstants.TRADE_CERT_ENDORSEMENT_CONSTANT_VAL.equals(applicationTradeCertDobjFrom.getNewRenewalTradeCert())) {
                                    tradeCertType = "ENDORSEMENT";
                                } else {
                                    tradeCertType = "RENEW";
                                }
                            }
                        } else {
                            tradeCertType = "NEW";
                        }
                    } catch (VahanException ex) {
                        LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                    }
                }
            }

            this.applicationSectionsList.clear();
            List list = getListFromMap();
            this.applicationSectionsList.addAll(list);
            if (!this.applicationSectionsList.isEmpty()) {
                this.visibleApplicationVerifyPanel = true;
                showVerifyTradeCertificateApplication = true;
                disableSave = false;
            } else {
                disableSave = true;
                this.visibleApplicationVerifyPanel = false;
                showVerifyTradeCertificateApplication = false;
            }

        } else {
            reset();
        }

    }

    private void fillApplicationSectionsListRenewForSelectedDealer(String selectedDealer) {

        if (!"-1".equals(this.applicationTradeCertDobj.getDealerFor())) {
            dissableChooseNewRenewTradeCertificateOption = true;
            mapSectionsSrNoOfSelectedDealer.clear();
            IssueTradeCertImpl issueTradeCertImpl = new IssueTradeCertImpl();
            try {

                issueTradeCertImpl.getAllExpiredSrNoForSelectedDealerFromVtTradeCert(selectedDealer, null, null, null, mapSectionsSrNoOfSelectedDealer);
                mapIssueTradeCertDobjToApplicationTradeCertDobj();
            } catch (VahanException ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
            if (!mapSectionsSrNoOfSelectedDealer.isEmpty()) {
                for (Object keyObj : mapSectionsSrNoOfSelectedDealer.keySet()) {
                    ApplicationTradeCertDobj applicationTradeCertDobjFrom = (ApplicationTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
                    this.applicationTradeCertDobj.setDealerFor(applicationTradeCertDobjFrom.getDealerFor());
                    this.applicationTradeCertDobj.setDealerName(applicationTradeCertDobjFrom.getDealerName());
                    break;
                }
                fillApplicationSectionsListRenewOrDup();
                if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
                    // RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
                    PrimeFaces.current().executeScript("PF('confDlgTradeCert').show()");
                    return;
                }
                this.visibleTradeCertificateApplicationPanel = true;

            } else {
                this.visibleTradeCertificateApplicationPanel = false;
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No trade Certificate found for renewal, please apply for new trade certificate.", "No trade Certificate found for renewal, please apply for new trade certificate.");
                FacesContext.getCurrentInstance().addMessage(null, message);
                //RequestContext rc = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
                PrimeFaces.current().executeScript("PF('confDlgTradeCert').show()");
            }

        } else {
            reset();
        }
    }

    public void issueDuplicateCertificate() {

        String tradeCertNoTrimmed = applicationTradeCertDobj.getTradeCertNo().trim().toUpperCase();
        applicationTradeCertDobj.setTradeCertNo(tradeCertNoTrimmed);
        if (!CommonUtils.isNullOrBlank(tradeCertNoTrimmed)) {
            dissableChooseNewRenewTradeCertificateOption = true;
            mapSectionsSrNoOfSelectedDealer.clear();
            IssueTradeCertImpl issueTradeCertImpl = new IssueTradeCertImpl();
            try {
                issueTradeCertImpl.getTradeCertDetailsForTradeCertNoFromVtTradeCert(tradeCertNoTrimmed, mapSectionsSrNoOfSelectedDealer);
                mapIssueTradeCertDobjToApplicationTradeCertDobj();
            } catch (VahanException ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
            if (!mapSectionsSrNoOfSelectedDealer.isEmpty()) {
                for (Object keyObj : mapSectionsSrNoOfSelectedDealer.keySet()) {
                    ApplicationTradeCertDobj applicationTradeCertDobjFrom = (ApplicationTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
                    this.applicationTradeCertDobj.setDealerFor(applicationTradeCertDobjFrom.getDealerFor());
                    this.applicationTradeCertDobj.setDealerName(applicationTradeCertDobjFrom.getDealerName());
                    if (!sessionVariables.getStateCodeSelected().equals(applicationTradeCertDobjFrom.getStateCd())) {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "! Trade certificate number might belongs to another state, Please enter valid trade certificate number.", "! Trade certificate number might belongs to another state, Please enter valid trade certificate number.");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        //RequestContext rc = RequestContext.getCurrentInstance();
                        PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
                        PrimeFaces.current().executeScript("PF('confDlgTradeCert').show()");
                        return;
                    }
                    break;
                }
                fillApplicationSectionsListRenewOrDup();
                this.visibleTradeCertificateApplicationPanel = true;

            } else {
                this.visibleTradeCertificateApplicationPanel = false;
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No certificate found with given number, Please enter valid trade certificate number.", "No certificate found with given number, Please enter valid trade certificate number.");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }

        } else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter trade certificate number.", "Please enter trade certificate number.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            // RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
            PrimeFaces.current().executeScript("PF('confDlgTradeCert').show()");
        }
    }

    private void mapIssueTradeCertDobjToApplicationTradeCertDobj() {
        Map temp = new HashMap();
        ApplicationTradeCertDobj applicationTradeCertDobjTo = null;
        IssueTradeCertDobj issueTradeCertDobj = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        int srNo = 0;
        for (Object dobjObjKey : mapSectionsSrNoOfSelectedDealer.keySet()) {

            applicationTradeCertDobjTo = new ApplicationTradeCertDobj();
            issueTradeCertDobj = (IssueTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(dobjObjKey);

            applicationTradeCertDobjTo.setStateCd(issueTradeCertDobj.getStateCd());
            applicationTradeCertDobjTo.setOffCd(issueTradeCertDobj.getOffCd());
            fillLoginDetails(applicationTradeCertDobjTo);
            applicationTradeCertDobjTo.setApplNo(issueTradeCertDobj.getApplNo());
            applicationTradeCertDobjTo.setDealerFor(issueTradeCertDobj.getDealerFor());
            applicationTradeCertDobjTo.setSrNo((++srNo) + "");
            applicationTradeCertDobjTo.setVehCatgName(issueTradeCertDobj.getVehCatgName());
            applicationTradeCertDobjTo.setVehCatgFor(issueTradeCertDobj.getVehCatgFor());
            applicationTradeCertDobjTo.setFuelTypeFor(issueTradeCertDobj.getFuelTypeFor());
            applicationTradeCertDobjTo.setFuelTypeName(issueTradeCertDobj.getFuelTypeName());
            applicationTradeCertDobjTo.setNoOfAllowedVehicles(issueTradeCertDobj.getNoOfAllowedVehicles());
            applicationTradeCertDobjTo.setDealerName(issueTradeCertDobj.getDealerName());
            applicationTradeCertDobjTo.setTradeCertNo(issueTradeCertDobj.getTradeCertNo());
            applicationTradeCertDobjTo.setValidUpto(issueTradeCertDobj.getValidUpto());
            if (issueTradeCertDobj.getValidUpto() != null) {
                applicationTradeCertDobjTo.setValidUptoAsString(format.format(issueTradeCertDobj.getValidUpto()));

            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(issueTradeCertDobj.getValidUpto());
            cal.add(Calendar.DATE, 1);
            cal.add(Calendar.MONTH, -1);
            //Trade Condition 
            if (Util.getUserStateCode().equals("KA")) {
                cal.add(Calendar.MONTH, -1);
            }
            //trade condition end
            Date tradeCertificateExpireDate = cal.getTime();

            if (tmConfigDobj.getTmTradeCertConfigDobj().isNoOfVehiclesNotToBeShown()) { ///// DO NOT SHOW No of Vehicles work for Chhattisgarh
                if (new Date().after(tradeCertificateExpireDate)) {
                    applicationTradeCertDobjTo.setExpired(true);
                    expiredVehCatg.add(applicationTradeCertDobjTo.getVehCatgFor());
                }
            } else {
                if (new Date().after(tradeCertificateExpireDate)
                        || issueTradeCertDobj.getNoOfVehiclesUsed().equals(issueTradeCertDobj.getNoOfAllowedVehicles())) {

                    applicationTradeCertDobjTo.setExpired(true);
                    expiredVehCatg.add(applicationTradeCertDobjTo.getVehCatgFor());
                    if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {  // GUJRAT FUEL-TYPE WORK
                        expiredFuelType.add(applicationTradeCertDobjTo.getFuelTypeFor());
                    }
                }
            }

            temp.put(dobjObjKey, applicationTradeCertDobjTo);

        }
        mapSectionsSrNoOfSelectedDealer.clear();
        mapSectionsSrNoOfSelectedDealer.putAll(temp);

    }

    private void fillVehCatgFiltered() {
        ApplicationTradeCertImpl applicationTradeCertImpl = new ApplicationTradeCertImpl();

        try {
            mapVehCatgFilteredFor = applicationTradeCertImpl.getVehCatgMapForAllVehClasses(this.applicationTradeCertDobj.getDealerFor(), this.applicationTradeCertDobj.getPurCd(), this.applicationTradeCertDobj.getStateCd(), this.listOfActiveTC);
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        if (!mapVehCatgFilteredFor.isEmpty()) {
            listVehCatgFilteredFor.add(new SelectItem("-1", "Select"));
        }
        outer:
        for (Object key : mapVehCatgFilteredFor.keySet()) {
            String keyForVehCatg = (String) key;
            String descr = (String) mapVehCatgFilteredFor.get(key);

            if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {  // GUJRAT FUEL-TYPE WORK
                if (!listFuelTypeAddedNotSavedInVaTradeCertificate.isEmpty()) {
                    for (Object obj : listFuelTypeAddedNotSavedInVaTradeCertificate) {
                        String keyForFuelType = ((String) obj).split(":")[1];
                        if ((!(listVehCatgAddedNotSavedInVaTradeCertificate.contains(keyForVehCatg) && listFuelTypeAddedNotSavedInVaTradeCertificate.contains(keyForVehCatg + ":" + keyForFuelType)))
                                && !listOfExhaustedVehCatg.contains(keyForVehCatg)) {
                            listVehCatgFilteredFor.add(new SelectItem(keyForVehCatg, descr));
                            continue outer;
                        }
                    }
                } else {
                    if (!listVehCatgAddedNotSavedInVaTradeCertificate.contains(keyForVehCatg)) {
                        listVehCatgFilteredFor.add(new SelectItem(keyForVehCatg, descr));
                    }
                }
            } else {
                if (!listVehCatgAddedNotSavedInVaTradeCertificate.contains(keyForVehCatg)) {
                    listVehCatgFilteredFor.add(new SelectItem(keyForVehCatg, descr));
                }
            }

            setVehClassSelected(true);
        }

    }

    private void fillFuelTypeFiltered(String VehCatg) {
        ApplicationTradeCertImpl applicationTradeCertImpl = new ApplicationTradeCertImpl();

        try {
            mapFuelTypeFilteredFor = applicationTradeCertImpl.getFuelTypeMapForDealerAndStateAndVehCategories(this.applicationTradeCertDobj.getDealerFor(), this.applicationTradeCertDobj.getStateCd(), VehCatg, isEditActiveTC());
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        if (!mapFuelTypeFilteredFor.isEmpty()) {
            listFuelTypeFilteredFor.add(new SelectItem("-1", "Select"));
        }
        for (Object key : mapFuelTypeFilteredFor.keySet()) {
            String keyFor = (String) key;
            String descr = (String) mapFuelTypeFilteredFor.get(key);
            if (!listFuelTypeAddedNotSavedInVaTradeCertificate.contains(VehCatg + ":" + keyFor)) {
                listFuelTypeFilteredFor.add(new SelectItem(keyFor, descr));
            }
            fuelTypeSelected = true;
        }

    }

    public void fillVehCatagories() {
        this.noOfVehGrandTotal = 0;

        resetVehCatgFilteredFor();
        applicationSectionsList.clear();
        this.newEntryList.clear();
        fillVehCatgFiltered();

        if (Utility.isNullOrBlank(this.applicationTradeCertDobj.getDealerFor())
                || "-1".equalsIgnoreCase(this.applicationTradeCertDobj.getDealerFor())) {

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select 'dealer' to whom trade certificate is to be issued.", "Please select 'dealer' to whom trade certificate is to be issued.");
            FacesContext.getCurrentInstance().addMessage(null, message);

        }

        initializeNewEntryDataTable();
        setVehClassSelected(false);
        if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {  // GUJRAT FUEL-TYPE WORK
            if (listVehCatgFilteredFor.size() == 1 || listFuelTypeFilteredFor.size() == 1) {
                this.newEntryList.clear();
            }
        } else if (listVehCatgFilteredFor.size() == 1) {
            this.newEntryList.clear();
        }
        if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
            PrimeFaces.current().executeScript("PF('confDlgTradeCert').show()");
        }

    }

    public List getListDealerFor() {
        return listDealerFor;
    }

    public List<ApplicationTradeCertDobj> getApplicationSectionsList() {
        return applicationSectionsList;
    }

    public int getNoOfVehGrandTotal() {
        return noOfVehGrandTotal;
    }

    public boolean isVisibleApplicationDetailPanel() {
        return visibleApplicationDetailPanel;
    }

    public List getListVehCatgFilteredFor() {
        return listVehCatgFilteredFor;
    }

    public List getListFuelTypeFilteredFor() {
        return listFuelTypeFilteredFor;
    }

    public boolean isVisibleNewEntryDetailPanel() {
        return visibleNewEntryDetailPanel;
    }

    public void reset() {
        this.renderFileMovement = false;
        this.formValidated = false;
        this.applicationTradeCertDobj.setNewRenewalTradeCert("New_Trade_Certificate");
        this.applicationTradeCertDobj.reset();
        this.newApplicationSectionsAddedList.clear();
        resetDealer();
        resetListOfExhaustedVehCatg();
        resetVehCatgFilteredFor();
        resetApplicationNo();
        resetListVehCatgAddedNotSavedInVaTradeCertificate();
        this.noOfVehGrandTotal = 0;
        applicationSectionsList.clear();
        this.newEntryList.clear();
        disableSave = true;
        disableSavePopup = false;
        visibleApplicationDetailPanel = false;
        initializeNewEntryDataTable();
        setVehClassSelected(false);
        dissableChooseNewRenewTradeCertificateOption = false;
        showRenewTradeCertificateApplication = false;
        showVerifyTradeCertificateApplication = false;
        showNewTradeCertificateApplication = true;
        showDupTradeCertificateApplication = false;
        this.applicationTradeCertDobj.setDealerName("");
        visibleTradeCertificateApplicationPanel = false;
        visibleApplicationVerifyPanel = false;
        this.mapSectionsSrNoOfSelectedDealer.clear();
        tradeCertNoNotBlank = false;
        tradeCertType = "";
        uniqueMessagesSet.clear();
        this.listOfCheckedTCRecordsToRenew.clear();
    }

    private void resetDealer() {
        listDealerFor.clear();
        fillDealers();
        List tempList = new ArrayList();
        tempList.addAll(listDealerFor);
        listDealerFor.clear();
        listDealerFor.add(new SelectItem("-1", "Select"));
        listDealerFor.addAll(tempList);
    }

    private void resetVehCatgFilteredFor() {
        listVehCatgFilteredFor.clear();
        if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {  // GUJRAT FUEL-TYPE WORK
            resetFuelTypeFilteredFor();
        }
    }

    private void resetFuelTypeFilteredFor() {
        listFuelTypeFilteredFor.clear();
    }

    public String back() {
        reset();
        this.disableAddNew = false;
        return "home";
    }

    public void validateFormRenew(Map mapSectionsSrNoOfSelectedDealer) {

        FacesMessage message = null;
        notValid = false;

        if (!this.doNotShowNoOfVehicles) {
            if (!mapSectionsSrNoOfSelectedDealer.isEmpty() && FacesContext.getCurrentInstance().getMessageList().isEmpty() && appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_APPL_ENTRY) {

                for (Object key : mapSectionsSrNoOfSelectedDealer.keySet()) {

                    applicationTradeCertDobj = (ApplicationTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(key);

                    if (Utility.isNullOrBlank(applicationTradeCertDobj.getNoOfAllowedVehicles())) {
                        uniqueMessagesSet.add("Please enter 'number of vehicles' for which trade certificate is to be issued.");
                        notValid = true;
                    } else {
                        String illegalCharacters = Utility.validateTextForIllegalCharacters(applicationTradeCertDobj.getNoOfAllowedVehicles(), "[0-9]*");
                        if (illegalCharacters != null) {
                            uniqueMessagesSet.add("Illegal characters (" + illegalCharacters.toUpperCase() + ") in 'number of vehicles' field for which trade certificate is to be issued.");
                            notValid = true;
                        } else if (Integer.valueOf(applicationTradeCertDobj.getNoOfAllowedVehicles()) == 0) {
                            uniqueMessagesSet.add("Zero(0) is not acceptable in 'number of vehicles' field for which trade certificate is to be issued.");
                            notValid = true;
                        }
                    }
                }
            }
        }

        if (!notValid && uniqueMessagesSet.isEmpty() && FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            this.formValidated = true;
        } else {
            for (Object messageStringObj : uniqueMessagesSet) {
                String messageString = (String) messageStringObj;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageString, messageString);
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            uniqueMessagesSet.clear();
        }

    }

    public void saveForm() {

        notValid = false;
        if ("Renew_Trade_Certificate".equals(chooseNewRenewTradeCertOption) && this.listOfCheckedTCRecordsToRenew.isEmpty()) {
            JSFUtils.setFacesMessage("! No trade certificate record(s) have been found to be renewed, please go through the backlog entry process...", null, JSFUtils.ERROR);
        }
        if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            disableSavePopup = true;
        } else {
            disableSavePopup = false;
        }

        for (Object keyObj : this.mapSectionsSrNoOfSelectedDealer.keySet()) {
            ApplicationTradeCertDobj dobj = (ApplicationTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
        }

        // RequestContext rc = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationSavePopup");
        PrimeFaces.current().executeScript("PF('confSaveDlgTradeCert').show()");
        this.formValidated = true;

    }

    public FacesMessage[] validateNewApplicationSectionDobj(ApplicationTradeCertDobj applicationTradeCertDobj) {

        FacesMessage[] facesMessages = new FacesMessage[6];
        notValid = false;
        int i = 0;

        if (Utility.isNullOrBlank(applicationTradeCertDobj.getDealerFor())
                || "-1".equalsIgnoreCase(applicationTradeCertDobj.getDealerFor())) {

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select 'dealer' to whom trade certificate is to be issued.", "Please select 'dealer' to whom trade certificate is to be issued.");
            facesMessages[i++] = message;
            notValid = true;
        }

        if (Utility.isNullOrBlank(applicationTradeCertDobj.getVehCatgFor())
                || "-1".equalsIgnoreCase(applicationTradeCertDobj.getVehCatgFor())) {

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select 'vehicle category' for which trade certificate to be issued.", "Please select 'vehicle category' for which trade certificate to be issued.");
            facesMessages[i++] = message;

            notValid = true;
        }

        if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {  // GUJRAT FUEL-TYPE WORK
            if (Utility.isNullOrBlank(applicationTradeCertDobj.getFuelTypeFor())
                    || "-1".equalsIgnoreCase(applicationTradeCertDobj.getFuelTypeFor())) {

                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select 'fuel' for which trade certificate to be issued.", "Please select 'fuel' for which trade certificate to be issued.");
                facesMessages[i++] = message;

                notValid = true;
            }
        }

        if (!this.doNotShowNoOfVehicles) {
            if (Utility.isNullOrBlank(applicationTradeCertDobj.getNoOfAllowedVehicles())) {

                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter 'number of vehicles' for which trade certificate is to be issued.", "Please enter 'number of vehicles' for which trade certificate is to be issued.");
                facesMessages[i++] = message;

                notValid = true;

            } else {

                String illegalCharacters = Utility.validateTextForIllegalCharacters(applicationTradeCertDobj.getNoOfAllowedVehicles(), "[0-9]*");
                if (illegalCharacters != null) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Illegal characters (" + illegalCharacters + ") in 'number of vehicles' field for which trade certificate is to be issued.", "Illegal characters (" + illegalCharacters.toUpperCase() + ") in 'number of vehicles' field for which trade certificate is to be issued.");
                    facesMessages[i++] = message;
                    notValid = true;
                } else if (Integer.valueOf(applicationTradeCertDobj.getNoOfAllowedVehicles()) == 0) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Zero(0) is not acceptable in 'number of vehicles' field for which trade certificate is to be issued.", "Zero(0) is not acceptable in 'number of vehicles' field for which trade certificate is to be issued.");
                    facesMessages[i++] = message;
                    notValid = true;
                }
            }
        } else {
            applicationTradeCertDobj.setNoOfAllowedVehicles("0");
        }

        if (!notValid) {
            this.newApplicationSectionsAddedList.add(applicationTradeCertDobj);
            return new FacesMessage[0];
        } else {
            return facesMessages;
        }
    }

    public void addNewSectionsToApplication() {

        String dealerName = this.mapDealersFor.get(this.applicationTradeCertDobj.getDealerFor()).toString();
        String dealerFor = this.applicationTradeCertDobj.getDealerFor();
        String purCd = this.applicationTradeCertDobj.getPurCd();
        String vehCatgCode = "";

        for (Object dobjObj : this.newEntryList) {
            ApplicationTradeCertDobj dobj = (ApplicationTradeCertDobj) dobjObj;

            FacesMessage[] facesMessageArr = validateNewApplicationSectionDobj(dobj);
            if (facesMessageArr.length > 1) {
                for (int i = 0; i < facesMessageArr.length; i++) {
                    FacesMessage message = facesMessageArr[i];
                    if (message != null) {
                        FacesContext.getCurrentInstance().addMessage(null, message);
                    } else {
                        break;
                    }
                }
                // RequestContext rc = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
                PrimeFaces.current().executeScript("PF('confDlgTradeCert').show()");
                return;
            } else {
                vehCatgCode = dobj.getVehCatgFor();
                dobj.setVehCatgName((String) mapVehCatgFilteredFor.get(dobj.getVehCatgFor()));
                if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {  // GUJRAT FUEL-TYPE WORK
                    dobj.setFuelTypeName((String) mapFuelTypeFilteredFor.get(dobj.getFuelTypeFor()));
                }
                dobj.setDealerName(dealerName);
                listVehCatgAddedNotSavedInVaTradeCertificate.add(dobj.getVehCatgFor());

                if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {  // GUJRAT FUEL-TYPE WORK
                    if (!listFuelTypeAddedNotSavedInVaTradeCertificate.contains(dobj.getVehCatgFor() + ":" + dobj.getFuelTypeFor())) {
                        listFuelTypeAddedNotSavedInVaTradeCertificate.add(dobj.getVehCatgFor() + ":" + dobj.getFuelTypeFor());
                    }
                    List tempList = new ArrayList();
                    tempList.addAll(listFuelTypeFilteredFor);
                    for (int index = 0; index < tempList.size(); index++) {
                        SelectItem item = (SelectItem) (listFuelTypeFilteredFor.get(index));
                        if (item.getValue().equals(dobj.getFuelTypeFor())
                                && item.getLabel().equalsIgnoreCase(dobj.getFuelTypeName())) {
                            tempList.remove(index);
                            break;
                        }
                    }
                    resetFuelTypeFilteredFor();
                    listFuelTypeFilteredFor.addAll(tempList);
                    if (listFuelTypeFilteredFor.size() == 1) {  /// --select-- item
                        resetFuelTypeFilteredFor();
                    }
                }
                if (!this.doNotShowNoOfVehicles) {
                    this.noOfVehGrandTotal += Integer.valueOf(dobj.getNoOfAllowedVehicles());
                }
            }
        }

        addNewSectionsToApplicationBlock1(purCd, dealerFor, dealerName, vehCatgCode);

        visibleApplicationDetailPanel = true;

        this.disableSave = false;

        PrimeFaces.current().ajax().update("new_trade_cert_application_subview:application_sections_panel");
        PrimeFaces.current().ajax().update("new_trade_cert_application_subview:new_entry_details");
        PrimeFaces.current().ajax().update("new_trade_cert_application_subview:bt_save");

    }

    private void addNewSectionsToApplicationBlock1(String purCd, String dealerFor, String dealerName, String vehCatgCode) {
        this.applicationTradeCertDobj = new ApplicationTradeCertDobj();
        fillLoginDetails(this.applicationTradeCertDobj);
        int incrementedSrNo = this.listVehCatgAddedNotSavedInVaTradeCertificate.size() + 1;
        this.applicationTradeCertDobj.setSrNo(String.valueOf(incrementedSrNo));
        this.applicationTradeCertDobj.setDealerFor(dealerFor);
        this.applicationTradeCertDobj.setDealerName(dealerName);
        this.applicationTradeCertDobj.setPurCd(purCd);

        if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {  // GUJRAT FUEL-TYPE WORK
            if (listFuelTypeFilteredFor.isEmpty()) {
                listOfExhaustedVehCatg.add(vehCatgCode);
                resetVehCatgFilteredFor();
                fillVehCatgFiltered();
            } else {
                this.applicationTradeCertDobj.setVehCatgFor(vehCatgCode);
                this.applicationTradeCertDobj.setVehCatgName((String) mapVehCatgFilteredFor.get(vehCatgCode));
            }
        } else {
            resetVehCatgFilteredFor();
            fillVehCatgFiltered();
        }
        initializeNewEntryDataTable();
        setVehClassSelected(false);

        //      if (!listVehCatgFilteredFor.isEmpty()) {
        //          visibleNewEntryDetailPanel = true;
        //      } else {
        visibleNewEntryDetailPanel = false;
        //          this.listDealerFor.remove(dealerFor);
        //      }

    }

    @Override
    public String save() {

        String returnLocation = "";
        ApplicationTradeCertImpl applicationTradeCertImpl = null;
        String saveReturn = "";
        // RequestContext rc = RequestContext.getCurrentInstance();

        try {

            if (notValid || !FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "There are some problems in saving application data for trade certificate.", "There are some problems in saving application data for trade certificate.");
                FacesContext.getCurrentInstance().addMessage(null, message);

            } else {

                applicationTradeCertImpl = new ApplicationTradeCertImpl();

                if (!this.mapSectionsSrNoOfSelectedDealer.isEmpty()) {

                    this.newApplicationSectionsAddedList.clear();
                    List list = getListFromMap();
                    if ("Renew_Trade_Certificate".equals(chooseNewRenewTradeCertOption) && !this.listOfCheckedTCRecordsToRenew.isEmpty()) {
                        list = this.listOfCheckedTCRecordsToRenew;
                    }
                    List tempList = new ArrayList();
                    tempList.addAll(list);
                    if (!list.isEmpty()) {
                        String pendingApplNo = applicationTradeCertImpl.sameApplcationPendingForDealerAndVehicleCategoryAndFuelType(this.applicationTradeCertDobj.getDealerFor(), tempList, expiredFuelType);
                        if (!CommonUtils.isNullOrBlank(pendingApplNo)) {
                            if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {  // GUJRAT FUEL-TYPE WORK
                                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Another previous application [No: " + pendingApplNo + " ] is still pending in process for the given dealer,vehicle category and fuel type.", "Another previous application [No: " + pendingApplNo + " ] is still pending to process for the given dealer,vehicle category and fuel type.");
                                FacesContext.getCurrentInstance().addMessage(null, message);
                            } else {
                                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Another previous application [No: " + pendingApplNo + " ] is still pending in process for the given dealer and vehicle category.", "Another previous application [No: " + pendingApplNo + " ] is still pending to process for the given dealer and vehicle category.");
                                FacesContext.getCurrentInstance().addMessage(null, message);
                            }
                            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
                            PrimeFaces.current().executeScript("PF('confDlgTradeCert').show()");
                            return returnLocation;
                        }
                        this.newApplicationSectionsAddedList.addAll(list);
                    } else {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select another 'dealer' as trade certificate(s) found for the selected dealer has not expired yet.", "Please select another 'dealer' as trade certificate(s) found for the selected dealer has not expired yet.");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
                        PrimeFaces.current().executeScript("PF('confDlgTradeCert').show()");
                        return returnLocation;
                    }
                }
                if (!CommonUtils.isNullOrBlank(getTcNewEditRadiobtn()) && "Edit_Application".equalsIgnoreCase(getTcNewEditRadiobtn())) {
                    for (Object dobjObj : this.newApplicationSectionsAddedList) {
                        ApplicationTradeCertDobj dobj = (ApplicationTradeCertDobj) dobjObj;
                        dobj.setStatus("E");  ////E for edit mode
                    }
                } else if (!CommonUtils.isNullOrBlank(getTcNewEditRadiobtn()) && "New_Application".equalsIgnoreCase(getTcNewEditRadiobtn())) {
                    for (Object dobjObj : this.newApplicationSectionsAddedList) {
                        ApplicationTradeCertDobj dobj = (ApplicationTradeCertDobj) dobjObj;
                        dobj.setStockTransferReq(this.stockTransferReq);
                    }
                }

                saveReturn = applicationTradeCertImpl.save(this.newApplicationSectionsAddedList);
                if (saveReturn.contains("SUCCESS")) {
                    String applNo = saveReturn.substring(saveReturn.lastIndexOf(":") + 1);
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Application '" + applNo + "' successfully submitted for verification.", "Application '" + applNo + "' successfully submitted for verification.");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    //              postSaveOperation();
                    setApplicationOperated(true);
                    PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopupOnlineApp");
                    PrimeFaces.current().executeScript("PF('confirmationPopupOnlineApp').show()");
                    return returnLocation;
                } else {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "There are some problems in saving application data for trade certificate due to '" + saveReturn.split(":")[1] + "'", "There are some problems in saving application data for trade certificate due to '" + saveReturn.split(":")[1] + "'");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
            }
        } catch (VahanException ve) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

        if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
            PrimeFaces.current().executeScript("PF('confDlgTradeCert').show()");
        }
        return returnLocation;

    }

    public void createActiveTCList(String dealerCd) {
        listOfActiveTC.clear();
        String currentDateString = DateUtil.formatDate(new Date(), "dd-MM-yyyy");

        try {
            List tcList = ApplicationTradeCertImpl.fetchTCList(dealerCd);

            for (Object dobjObj : tcList) {
                IssueTradeCertDobj activeTradeCertToEditDobj = (IssueTradeCertDobj) dobjObj;
                if (activeTradeCertToEditDobj != null) {

                    int validUptoYear = Integer.valueOf(activeTradeCertToEditDobj.getValidUptoAsString().split("-")[2]);
                    int validUptoMonth = Integer.valueOf(activeTradeCertToEditDobj.getValidUptoAsString().split("-")[1]);
                    int validUptoDay = Integer.valueOf(activeTradeCertToEditDobj.getValidUptoAsString().split("-")[0]);
                    int currentDateYear = Integer.valueOf(currentDateString.split("-")[2]);
                    int currentDateMonth = Integer.valueOf(currentDateString.split("-")[1]);
                    int currentDateDay = Integer.valueOf(currentDateString.split("-")[0]);

                    boolean isActiveTC = false;
                    if (validUptoYear == currentDateYear) {
                        if (validUptoMonth == currentDateMonth) {
                            if (validUptoDay >= currentDateDay) {
                                isActiveTC = true;
                            }
                        } else if (validUptoMonth > currentDateMonth) {
                            isActiveTC = true;
                        }
                    } else if (validUptoYear > currentDateYear) {
                        isActiveTC = true;
                    }

                    if (activeTradeCertToEditDobj.getTradeCertNo() != null && isActiveTC) {
                        listOfActiveTC.add(activeTradeCertToEditDobj);
                        //visibleEditEntryDetailPanel = true;
                    }
                }
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", ve.getMessage()));
        } catch (Exception ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", TableConstants.SomthingWentWrong));
        }
    }

    private List getListFromMap() {
        List list = new ArrayList();
        this.noOfVehGrandTotal = 0;
        int srNo = 1;
        for (Object keyObj : this.mapSectionsSrNoOfSelectedDealer.keySet()) {
            ApplicationTradeCertDobj dobj = (ApplicationTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
            dobj.setSrNo(srNo + "");
            if ("Renew_Trade_Certificate".equalsIgnoreCase(this.chooseNewRenewTradeCertOption)) {
                if (dobj.isExpired()) {
                    list.add(dobj);
                }
            } else { ////  DUPLICATE/NEW Trade Certificate
                list.add(dobj);
            }
            this.noOfVehGrandTotal += Integer.valueOf(dobj.getNoOfAllowedVehicles());
            srNo++;
        }
        return list;
    }

    public void formToBeValidated() {
        if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            disableSavePopup = false;
        }
        this.formValidated = false;
    }

    private void postSaveOperation() {

        this.formValidated = false;
        this.disableSave = true;
        setDisableSaveButton(true);
        this.disableAddNew = true;
        visibleNewEntryDetailPanel = false;
        chooseOperation();
        // RequestContext rc = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("new_trade_cert_application_subview:new_entry_details");
        PrimeFaces.current().ajax().update("new_trade_cert_application_subview:bt_save");

    }

    public ApplicationTradeCertDobj getApplicationTradeCertDobj() {
        return applicationTradeCertDobj;
    }

    //Note:Please use Utils for Session Details
    private void fillLoginDetails(ApplicationTradeCertDobj dobj) {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        String purCd = "";
        purCd = String.valueOf(TableConstants.VM_TRANSACTION_TRADE_CERT_NEW);
        dobj.setPurCd(purCd);
        dobj.setEmpCd((Long) sessionMap.get("emp_cd"));
        dobj.setOffCd(Util.getUserSeatOffCode());
        dobj.setStateCd((String) sessionMap.get("state_cd"));
        dobj.setStateName((String) sessionMap.get("state_name"));
        dobj.setEmpName((String) sessionMap.get("emp_name"));
        dobj.setDesigName((String) sessionMap.get("desig_name"));

    }

    public boolean isVisibleTradeCertificateApplicationPanel() {
        return visibleTradeCertificateApplicationPanel;
    }

    public boolean isVisibleApplicationVerifyPanel() {
        return visibleApplicationVerifyPanel;
    }

    private void resetListVehCatgAddedNotSavedInVaTradeCertificate() {
        listVehCatgAddedNotSavedInVaTradeCertificate.clear();
        if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {   /// for GUJRAT FUEL-TYPE work
            resetListFuelTypeAddedNotSavedInVaTradeCertificate();
        }
    }

    private void resetListFuelTypeAddedNotSavedInVaTradeCertificate() {
        listFuelTypeAddedNotSavedInVaTradeCertificate.clear();
    }

    private void initializeNewEntryDataTable() {
        if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {  // GUJRAT FUEL-TYPE WORK
            if (isVehClassSelected() && isFuelTypeSelected()) {
                visibleNewEntryDetailPanel = true;
            } else {
                visibleNewEntryDetailPanel = false;
            }
        } else {
            if (isVehClassSelected()) {
                visibleNewEntryDetailPanel = true;
            } else {
                visibleNewEntryDetailPanel = false;
            }
        }

        this.newEntryList.clear();
        this.newEntryList.add(this.applicationTradeCertDobj);
    }

    public List<ApplicationTradeCertDobj> getNewEntryList() {
        return newEntryList;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    private void resetApplicationNo() {
        listApplicationNo.clear();
        listApplicationNo.add(new SelectItem("-1", "Select"));
        fillApplicationNoRenew();
        mapSectionsSrNoOfSelectedDealer.clear();

    }

    public List<ApplicationTradeCertDobj> getNewApplicationSectionsAddedList() {
        return newApplicationSectionsAddedList;
    }

    public void setApplicationTradeCertDobj(ApplicationTradeCertDobj applicationTradeCertDobj) {
        if (!"Duplicate_Trade_Certificate".equals(chooseNewRenewTradeCertOption) && (mapSectionsSrNoOfSelectedDealer != null && !mapSectionsSrNoOfSelectedDealer.isEmpty())) {

            for (Object keyObj : mapSectionsSrNoOfSelectedDealer.keySet()) {

                this.applicationTradeCertDobj = new ApplicationTradeCertDobj();
                mapDobj(this.applicationTradeCertDobj, (ApplicationTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj));

                break;
            }
            if (!this.formValidated) {
                if (uniqueMessagesSet.isEmpty()) {
                    validateFormRenew(mapSectionsSrNoOfSelectedDealer);
                }
            }
        } else if (applicationTradeCertDobj != null) {
            this.applicationTradeCertDobj = applicationTradeCertDobj;
        }
    }

    public boolean isDisableSave() {
        return disableSave;
    }

    public boolean isDisableSavePopup() {
        return disableSavePopup;
    }

    public List getListApplicationNo() {
        return listApplicationNo;
    }

    public boolean isDissableChooseNewRenewTradeCertificateOption() {
        return dissableChooseNewRenewTradeCertificateOption;
    }

    public void setDissableChooseNewRenewTradeCertificateOption(boolean dissableChooseNewRenewTradeCertificateOption) {
        this.dissableChooseNewRenewTradeCertificateOption = dissableChooseNewRenewTradeCertificateOption;
    }

    public boolean isShowRenewTradeCertificateApplication() {
        return showRenewTradeCertificateApplication;
    }

    public boolean isShowVerifyTradeCertificateApplication() {
        return showVerifyTradeCertificateApplication;
    }

    public boolean isShowNewTradeCertificateApplication() {
        return showNewTradeCertificateApplication;
    }

    public void setShowVerifyTradeCertificateApplication(boolean showVerifyTradeCertificateApplication) {
        this.showVerifyTradeCertificateApplication = showVerifyTradeCertificateApplication;
    }

    public void setShowNewTradeCertificateApplication(boolean showNewTradeCertificateApplication) {
        this.showNewTradeCertificateApplication = showNewTradeCertificateApplication;
    }

    public boolean isShowDupTradeCertificateApplication() {
        return showDupTradeCertificateApplication;
    }

    public void setShowDupTradeCertificateApplication(boolean showDupTradeCertificateApplication) {
        this.showDupTradeCertificateApplication = showDupTradeCertificateApplication;
    }

    @Override
    public ArrayList<ComparisonBean> compareChanges() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String saveAndMoveFile() {

        String returnLocation = "";
        String fileMoveReturn = "";

        if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_VERIFICATION || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_VERIFICATION_DUP) {

            if (tmConfigDobj.getTmTradeCertConfigDobj().isDocumentsUploadNRevert()
                    && !"ENDORSEMENT".equals(tradeCertType)
                    && !rtoSideAppl) {  ///// documents_upload_n_revert work
                if (nic.vahan.server.CommonUtils.isNullOrBlank(currentTimeStampAsMarkHardCopyReceivedInStringFormat) && isShowHardCopyReceivedButton()) {
                    JSFUtils.setFacesMessage("! Please receive hard copies of all mandatory documents along with hard copy of deficiency mail (if any)", null, JSFUtils.ERROR);
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

                if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
                    // RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
                    PrimeFaces.current().executeScript("PF('confDlgTradeCert').show()");
                    return "";
                }
            }

            try {
                Status_dobj status = new Status_dobj();
                status.setAppl_dt(appl_details.getAppl_dt());
                status.setAppl_no(appl_details.getAppl_no());
                status.setPur_cd(appl_details.getPur_cd());
                status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
                status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
                status.setStatus(getApp_disapp_dobj().getNew_status());
                status.setCurrent_role(appl_details.getCurrent_role());
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)) {
                    ApplicationTradeCertImpl applicationTradeCertImpl = new ApplicationTradeCertImpl();
                    if (status.getStatus().equals(TableConstants.STATUS_REVERT)) {
                        status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                    }
                    fileMoveReturn = applicationTradeCertImpl.fileMoveToIssueTradeCertificate(status);

                    if (fileMoveReturn.contains("SUCCESS")) {
                        String applNo = fileMoveReturn.substring(fileMoveReturn.lastIndexOf(":") + 1);
                        postSaveOperation();
                        returnLocation = "seatwork";
                    }

                }

            } catch (VahanException ve) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "There are some problems in saving application data for trade certificate due to " + ve.getMessage(), "There are some problems in saving application data for trade certificate due to " + ve.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, message);
                LOGGER.error(ve);

            } catch (Exception ve) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "There are some problems in saving application data for trade certificate due to " + ve.getMessage(), "There are some problems in saving application data for trade certificate due to " + ve.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, message);
                LOGGER.error(ve);
            }
        } else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application data has to be saved first for verification.", "Application data has to be saved first for verification.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
            PrimeFaces.current().executeScript("PF('confDlgTradeCert').show()");
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

    public void displayVehClassCatgMapping() {
        try {
            vehicleClassCategoryMappingList.clear();
            vehicleClassCategoryMappingList.addAll(ApplicationTradeCertImpl.getVehClassCategoryMappingsInList());
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:vehClassCategoryMappingDialog");
            PrimeFaces.current().executeScript("PF('vehClassCategoryMappingDialog').show();");
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", ve.getMessage()));
        } catch (Exception ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", TableConstants.SomthingWentWrong));
        }
    }

    private void mapDobj(ApplicationTradeCertDobj applicationTradeCertDobjTo, ApplicationTradeCertDobj applicationTradeCertDobjFrom) {
        applicationTradeCertDobjTo.setApplNo(applicationTradeCertDobjFrom.getApplNo());
        applicationTradeCertDobjTo.setDealerFor(applicationTradeCertDobjFrom.getDealerFor());
        applicationTradeCertDobjTo.setDealerName(applicationTradeCertDobjFrom.getDealerName());
        applicationTradeCertDobjTo.setDesigName(applicationTradeCertDobjFrom.getDesigName());
        applicationTradeCertDobjTo.setEmpCd(applicationTradeCertDobjFrom.getEmpCd());
        applicationTradeCertDobjTo.setEmpName(applicationTradeCertDobjFrom.getEmpName());
        applicationTradeCertDobjTo.setExpired(applicationTradeCertDobjFrom.isExpired());
        applicationTradeCertDobjTo.setNewRenewalTradeCert(applicationTradeCertDobjFrom.getNewRenewalTradeCert());
        applicationTradeCertDobjTo.setNoOfAllowedVehicles(applicationTradeCertDobjFrom.getNoOfAllowedVehicles());
        applicationTradeCertDobjTo.setOffCd(applicationTradeCertDobjFrom.getOffCd());
        applicationTradeCertDobjTo.setPurCd(applicationTradeCertDobjFrom.getPurCd());
        applicationTradeCertDobjTo.setSrNo(applicationTradeCertDobjFrom.getSrNo());
        applicationTradeCertDobjTo.setStateCd(applicationTradeCertDobjFrom.getStateCd());
        applicationTradeCertDobjTo.setStateName(applicationTradeCertDobjFrom.getStateName());
        applicationTradeCertDobjTo.setTradeCertNo(applicationTradeCertDobjFrom.getTradeCertNo());
        applicationTradeCertDobjTo.setValidUpto(applicationTradeCertDobjFrom.getValidUpto());
        applicationTradeCertDobjTo.setValidUptoAsString(applicationTradeCertDobjFrom.getValidUptoAsString());
        applicationTradeCertDobjTo.setVehCatgFor(applicationTradeCertDobjFrom.getVehCatgFor());
        applicationTradeCertDobjTo.setVehCatgName(applicationTradeCertDobjFrom.getVehCatgName());
        applicationTradeCertDobjTo.setFuelTypeFor(applicationTradeCertDobjFrom.getFuelTypeFor());
        applicationTradeCertDobjTo.setFuelTypeName(applicationTradeCertDobjFrom.getFuelTypeName());
        applicationTradeCertDobjTo.setApplicantCategory(applicationTradeCertDobjFrom.getApplicantCategory());

    }

    public boolean isVerifyMode() {
        return verifyMode;
    }

    public void confirmTradeCertApplicationPrint() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("tcApplNo", applicationSectionsList.get(0).getApplNo());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("applicantType", applicationSectionsList.get(0).getApplicantCategory());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("tcApplMapOfSelectedDealer", mapSectionsSrNoOfSelectedDealer);
        // RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('printTradeCertificateApplication').show()");
    }

    public String printTradeCertApplicationDetails() {
        // RequestContext rc = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('printTradeCertificateApplication').hide()");
        return "PrintTradeCertificateApplication";
    }

    public void tcNewEditListener(AjaxBehaviorEvent event) {
        verifyMode = true; //// TO MAKE CHOOSER INVISIBLE
        String radioBtnvalue = tcNewEditRadiobtn;
        setDisplayDealer(true);
        if (radioBtnvalue.equalsIgnoreCase("New_Application")) {
            setEditActiveTC(false);
        } else if (radioBtnvalue.equalsIgnoreCase("Edit_Application")) {
            setEditActiveTC(true);
        }
        this.applicationTradeCertDobj.setDealerFor("-1");
        this.newApplicationSectionsAddedList.clear();
        this.disableSave = true;
        this.visibleNewEntryDetailPanel = false;
        this.visibleApplicationDetailPanel = false;
    }

    public void addInListToBeRenewedListener(AjaxBehaviorEvent event) {
        if (!this.applicationSectionsList.isEmpty()) {
            List tempList = new ArrayList(this.applicationSectionsList);
            for (Object dobjObj : this.applicationSectionsList) {
                ApplicationTradeCertDobj dobj = (ApplicationTradeCertDobj) dobjObj;
                if (dobj.isToBeRenew()) {
                    this.listOfCheckedTCRecordsToRenew.add(dobj);
                    tempList.remove(dobj);
                }
            }
            this.applicationSectionsList.clear();
            this.applicationSectionsList.addAll(tempList);
            if (applicationSectionsList.isEmpty()) {
                this.visibleTradeCertificateApplicationPanel = false;
            }
        }
    }

    public void removeNewSectionsToApplication(ApplicationTradeCertDobj dobj) {
        String dealerName = this.mapDealersFor.get(dobj.getDealerFor()).toString();
        String dealerFor = dobj.getDealerFor();
        String purCd = dobj.getPurCd();
        String vehCatgCode = dobj.getVehCatgFor();
        if (listVehCatgAddedNotSavedInVaTradeCertificate.contains(dobj.getVehCatgFor())) {
            listVehCatgAddedNotSavedInVaTradeCertificate.remove(dobj.getVehCatgFor());
        }

        if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {  // GUJRAT FUEL-TYPE WORK
            if (listFuelTypeAddedNotSavedInVaTradeCertificate.contains(dobj.getVehCatgFor() + ":" + dobj.getFuelTypeFor())) {
                listFuelTypeAddedNotSavedInVaTradeCertificate.remove(dobj.getVehCatgFor() + ":" + dobj.getFuelTypeFor());
            }
            if (listFuelTypeFilteredFor.isEmpty()) {
                listFuelTypeFilteredFor.add(new SelectItem("-1", "Select"));
            }
            listFuelTypeFilteredFor.add(new SelectItem(dobj.getFuelTypeFor(), dobj.getFuelTypeName()));
            fuelTypeSelected = true;
        }
        if (!this.doNotShowNoOfVehicles) {
            this.noOfVehGrandTotal -= Integer.valueOf(dobj.getNoOfAllowedVehicles());
        }

        this.applicationTradeCertDobj = new ApplicationTradeCertDobj();
        fillLoginDetails(this.applicationTradeCertDobj);
        int incrementedSrNo = this.listVehCatgAddedNotSavedInVaTradeCertificate.size() + 1;
        this.applicationTradeCertDobj.setSrNo(String.valueOf(incrementedSrNo));
        this.applicationTradeCertDobj.setDealerFor(dealerFor);
        this.applicationTradeCertDobj.setDealerName(dealerName);
        this.applicationTradeCertDobj.setPurCd(purCd);

        if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {  // GUJRAT FUEL-TYPE WORK
            listOfExhaustedVehCatg.remove(vehCatgCode);
            this.applicationTradeCertDobj.setVehCatgFor(vehCatgCode);
            this.applicationTradeCertDobj.setVehCatgName((String) mapVehCatgFilteredFor.get(vehCatgCode));
        } else {
            if (listVehCatgFilteredFor.isEmpty()) {
                listVehCatgFilteredFor.add(new SelectItem("-1", "Select"));
            }

            if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {  // GUJRAT FUEL-TYPE WORK
                if (!listFuelTypeAddedNotSavedInVaTradeCertificate.isEmpty()) {
                    for (Object obj : listFuelTypeAddedNotSavedInVaTradeCertificate) {
                        if ((!(listVehCatgAddedNotSavedInVaTradeCertificate.contains(dobj.getVehCatgFor()) && listFuelTypeAddedNotSavedInVaTradeCertificate.contains(dobj.getVehCatgFor() + ":" + dobj.getFuelTypeFor())))
                                && !listOfExhaustedVehCatg.contains(dobj.getVehCatgFor())) {
                            listVehCatgFilteredFor.add(new SelectItem(dobj.getVehCatgFor(), (String) mapVehCatgFilteredFor.get(vehCatgCode)));
                        }
                    }
                } else {
                    if (!listVehCatgAddedNotSavedInVaTradeCertificate.contains(dobj.getVehCatgFor())) {
                        listVehCatgFilteredFor.add(new SelectItem(dobj.getVehCatgFor(), (String) mapVehCatgFilteredFor.get(vehCatgCode)));
                    }
                }
            } else {
                if (!listVehCatgAddedNotSavedInVaTradeCertificate.contains(dobj.getVehCatgFor())) {
                    listVehCatgFilteredFor.add(new SelectItem(dobj.getVehCatgFor(), (String) mapVehCatgFilteredFor.get(vehCatgCode)));
                }
            }
        }
        initializeNewEntryDataTable();
        setVehClassSelected(true);

        if (!listVehCatgFilteredFor.isEmpty()) {
            visibleNewEntryDetailPanel = true;
        } else {
            visibleNewEntryDetailPanel = false;
            this.listDealerFor.remove(dealerFor);
        }

        this.newApplicationSectionsAddedList.remove(dobj);

        if (this.newApplicationSectionsAddedList.isEmpty()) {
            this.visibleApplicationDetailPanel = false;
            visibleNewEntryDetailPanel = true;
            this.disableSave = true;
        }

    }

    public void removeInListToBeRenewedListener(AjaxBehaviorEvent event) {
        if (!this.listOfCheckedTCRecordsToRenew.isEmpty()) {
            List tempList = new ArrayList(this.listOfCheckedTCRecordsToRenew);
            for (Object dobjObj : this.listOfCheckedTCRecordsToRenew) {
                ApplicationTradeCertDobj dobj = (ApplicationTradeCertDobj) dobjObj;
                if (!dobj.isToBeRenew()) {
                    this.applicationSectionsList.add(dobj);
                    tempList.remove(dobj);
                }
            }
            this.listOfCheckedTCRecordsToRenew.clear();
            this.listOfCheckedTCRecordsToRenew.addAll(tempList);
            if (!applicationSectionsList.isEmpty()) {
                this.visibleTradeCertificateApplicationPanel = true;
            }
        }
    }

    public boolean isTradeCertNoNotBlank() {
        return tradeCertNoNotBlank;
    }

    public String getTradeCertType() {
        return tradeCertType;
    }

    public boolean isDisableAddNew() {
        return disableAddNew;
    }

    public boolean isDisplayFuel() {
        return displayFuel;
    }

    public boolean isVehClassSelected() {
        return vehClassSelected;
    }

    public void setVehClassSelected(boolean value) {
        this.vehClassSelected = value;
        if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {   /// for GUJRAT FUEL-TYPE work
            fuelTypeSelected = value;
        }
    }

    public boolean isFuelTypeSelected() {
        return fuelTypeSelected;
    }

    private void resetListOfExhaustedVehCatg() {
        this.listOfExhaustedVehCatg.clear();
    }

    public List getVehicleClassCategoryMappingList() {
        return vehicleClassCategoryMappingList;
    }

    public String getTcNewEditRadiobtn() {
        return tcNewEditRadiobtn;
    }

    public void setTcNewEditRadiobtn(String tcNewEditRadiobtn) {
        this.tcNewEditRadiobtn = tcNewEditRadiobtn;
    }

    public boolean isEditActiveTC() {
        return editActiveTC;
    }

    public void setEditActiveTC(boolean editActiveTC) {
        this.editActiveTC = editActiveTC;
    }

    public boolean isDisplayDealer() {
        return displayDealer;
    }

    public void setDisplayDealer(boolean displayDealer) {
        this.displayDealer = displayDealer;
    }

    public List getListOfCheckedTCRecordsToRenew() {
        return listOfCheckedTCRecordsToRenew;
    }

    public boolean isDoNotShowNoOfVehicles() {
        return doNotShowNoOfVehicles;
    }

    public void setDoNotShowNoOfVehicles(boolean doNotShowNoOfVehicles) {
        this.doNotShowNoOfVehicles = doNotShowNoOfVehicles;
    }

    ////////////////////////////////// DMS UPLOAD UTILITY API  //////////////////////////////////
    public void sendRejectionMessages() throws VahanException {
        try {

            if (generateDeficiencyMailAndSend()) {
                generateSMS();

                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Mail generation status...", "Mail has been sent to the applicant by " + ServerUtil.getOfficeName(Util.getUserSeatOffCode(), Util.getUserStateCode())));
                setMailSent(true);
            }
        } catch (VahanException ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application receive status...", "Error occurred while generating mail and sending through sms. {message[" + ve.getMessage() + "]}"));
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
            sendMail = new MailSender(applicantMailId, message, deficiencyMailSubject, fMessage);
            sendMail.start();
            sendMail.join();
        } catch (Exception ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            throw new VahanException(ve.getMessage());
        }
        if (fMessage.getSummary() != null && fMessage.getSummary().contains("Error while sending deficiency mail")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Mail generation status...", fMessage.getSummary()));
            mailSent = false;
        }
        return mailSent;
    }

    public void generateDeficiencyMailOpeningMatter() {

        this.setRejectionReasons("");

        if (this.applicationTradeCertDobj == null && !mapSectionsSrNoOfSelectedDealer.isEmpty()) {
            for (Object keyObj : mapSectionsSrNoOfSelectedDealer.keySet()) {
                ApplicationTradeCertDobj applicationTradeCertDobjFrom = (ApplicationTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
                this.applicationTradeCertDobj = applicationTradeCertDobjFrom;
                fillLoginDetails(this.applicationTradeCertDobj);
                break;
            }
        }
        applicantNameInDeficiencyMail = this.applicationTradeCertDobj.getDealerName();
        applicationNoInDeficiencyMail = this.applicationTradeCertDobj.getApplNo();
        deficiencyMailContent = "Dear <b>" + applicantNameInDeficiencyMail + "</b>,<br/> "
                + "The following discrepancies in online trade certificate application [ No: <b><i>" + applicationNoInDeficiencyMail + "</i></b> ] have been found which are required to be rectified on urgent basis:- <br/>";
        stateNameMentionedInDeficiencyMail = Util.getSession().getAttribute("state_name").toString();
        try {
            applicantMailId = ApplicationTradeCertImpl.fetchApplicantEmailId(this.applicationTradeCertDobj.getDealerFor(), this.applicationTradeCertDobj.getStateCd(), this.applicationTradeCertDobj.getOffCd(), this.applicationTradeCertDobj.getApplicantCategory());
            deficiencyMailSubject = "[Vahan] Discrepancies have been found in online trade certificate application.";
            applicantPhoneNumber = String.valueOf(ApplicationTradeCertImpl.fetchApplicantContactNo(this.applicationTradeCertDobj.getDealerFor(), this.applicationTradeCertDobj.getStateCd(), this.applicationTradeCertDobj.getOffCd(), this.applicationTradeCertDobj.getApplicantCategory()));
        } catch (Exception ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application receive status...", "Error occurred while generating deficiency mail opening matter. {message[" + ve.getMessage() + "]}"));
        }
        if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
            PrimeFaces.current().executeScript("PF('confirmationPopup').show()");
        }

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

    public void openUploadedDocuments() {
        if (!mapSectionsSrNoOfSelectedDealer.isEmpty() && this.applicationTradeCertDobj == null) {
            for (Object keyObj : mapSectionsSrNoOfSelectedDealer.keySet()) {
                ApplicationTradeCertDobj applicationTradeCertDobjFrom = (ApplicationTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
                this.applicationTradeCertDobj = applicationTradeCertDobjFrom;
                break;
            }
            fillLoginDetails(this.applicationTradeCertDobj);
        }
        if (!CommonUtils.isNullOrBlank(this.applicationTradeCertDobj.getApplNo())) {
            displayDMSUploadUtility(this.applicationTradeCertDobj.getApplNo());
        }
    }

    private void displayDMSUploadUtility(String tempApplNo) {
        try {

            if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE) {
                this.dmsFileServerAPIString = uploadUtils(tempApplNo, "R");
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_VERIFICATION
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_VERIFICATION_DUP) {
                this.dmsFileServerAPIString = uploadUtils(tempApplNo, "V");
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE) {
                this.dmsFileServerAPIString = uploadUtils(tempApplNo, "A");
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
            sendUrl = "?applno=" + tempApplNo + "&status=" + statusFlag + "&state=" + Util.getUserStateCode() + "&j_key=vVl/Az1yGsjOAG18WDeScg==&j_securityKey=6D6C069D681B40DBF95CAD7B3ED71BE1" + checkSumKey + "!TZFIMZbTiUtqXMfARJ1DGgyNicWFwYkwTA0ip/Q8Wns=";
            url = dmsTradeCertUploadedDocsUrl + "dealer-search-within-dms" + sendUrl;
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
                noOfDocumentsUploaded = ApplicationTradeCertImpl.checkStatusOfAllDocuments(this.applicationTradeCertDobj.getApplNo(), "R");
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_VERIFICATION
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_VERIFICATION_DUP) {
                noOfDocumentsUploaded = ApplicationTradeCertImpl.checkStatusOfAllDocuments(this.applicationTradeCertDobj.getApplNo(), "V");
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_APPROVE
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_APPROVE_DUP) {
                noOfDocumentsUploaded = ApplicationTradeCertImpl.checkStatusOfAllDocuments(this.applicationTradeCertDobj.getApplNo(), "A");
            }
            ////////////////////////////////////////////////////////////////////////////////
            if (TableConstants.TRADE_CERT_ENDORSEMENT_CONSTANT_VAL.equals(applicationTradeCertDobj.getNewRenewalTradeCert())) {
                noOfDocumentsUploaded = DMS_UPLOADED_DOCS_LIMIT;
                markHardCopyReceived();
            }

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

        PrimeFaces.current().executeScript("PF('bui_fancy').hide();");
    }

    private boolean checkAllDocumentsHaveBeenVerified() {
        try {

            ///////////////////////// UNCOMMENT TO TEST WITHOUT DMS /////////////////////////
//            int noOfDocumentsUploaded = DMS_UPLOADED_DOCS_LIMIT;
            ////////////////////////////////////////////////////////////////////////////
            ///////////////////////// COMMENT TO TEST WITHOUT DMS /////////////////////////
            int noOfDocumentsUploaded = 0;
            if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE) {
                noOfDocumentsUploaded = ApplicationTradeCertImpl.checkStatusOfAllDocuments(this.applicationTradeCertDobj.getApplNo(), "R");
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_VERIFICATION
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_VERIFICATION_DUP) {
                noOfDocumentsUploaded = ApplicationTradeCertImpl.checkStatusOfAllDocuments(this.applicationTradeCertDobj.getApplNo(), "V");
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_APPROVE
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_APPROVE_DUP) {
                noOfDocumentsUploaded = ApplicationTradeCertImpl.checkStatusOfAllDocuments(this.applicationTradeCertDobj.getApplNo(), "A");
            }
            ////////////////////////////////////////////////////////////////////////////////
            if (TableConstants.TRADE_CERT_ENDORSEMENT_CONSTANT_VAL.equals(applicationTradeCertDobj.getNewRenewalTradeCert())) {
                noOfDocumentsUploaded = DMS_UPLOADED_DOCS_LIMIT;
                markHardCopyReceived();
            }

            return noOfDocumentsUploaded == DMS_UPLOADED_DOCS_LIMIT ? true : false;

        } catch (Exception ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application receive status...", "Error occurred while fetching the status of check for all requisite and mandatory documents from database. {message[" + TableConstants.SomthingWentWrong + "]}"));
        }
        return false;
    }

    public void markHardCopyReceived() {
        currentTimeStampAsMarkHardCopyReceivedInStringFormat = new java.text.SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new java.util.Date());
    }

    public String getCurrentTimeStampAsMarkHardCopyReceivedInStringFormat() {
        return currentTimeStampAsMarkHardCopyReceivedInStringFormat;
    }

    public void setCurrentTimeStampAsMarkHardCopyReceivedInStringFormat(String currentTimeStampAsMarkHardCopyReceivedInStringFormat) {
        this.currentTimeStampAsMarkHardCopyReceivedInStringFormat = currentTimeStampAsMarkHardCopyReceivedInStringFormat;
    }

    public String getRejectionReasons() {
        return rejectionReasons;
    }

    public void setRejectionReasons(String rejectionReasons) {
        this.rejectionReasons = rejectionReasons;
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

    public String getApplicantPhoneNumber() {
        return applicantPhoneNumber;
    }

    public void setApplicantPhoneNumber(String applicantPhoneNumber) {
        this.applicantPhoneNumber = applicantPhoneNumber;
    }

    public String getDeficiencyMailContent() {
        return deficiencyMailContent;
    }

    public void setDeficiencyMailContent(String deficiencyMailContent) {
        this.deficiencyMailContent = deficiencyMailContent;
    }

    public String getDmsFileServerAPIString() {
        return dmsFileServerAPIString;
    }

    public void setDmsFileServerAPIString(String dmsFileServerAPIString) {
        this.dmsFileServerAPIString = dmsFileServerAPIString;
    }

    public boolean isAllDocumentsChecked() {
        return allDocumentsChecked;
    }

    public void setAllDocumentsChecked(boolean allDocumentsChecked) {
        this.allDocumentsChecked = allDocumentsChecked;
    }

    public void revertPendingApplication() {
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
            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopupOnlineApp");
            PrimeFaces.current().executeScript("PF('confirmationPopupOnlineApp').show()");
        } else {
            try {
                VerifyApproveTradeCertDobj dobj = new VerifyApproveTradeCertDobj();
                if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION
                        || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_VERIFICATION
                        || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_VERIFICATION_DUP) {
                    dobj.setVerifierHardCopyReceivedOn(currentTimeStampAsMarkHardCopyReceivedInStringFormat);
                    dobj.setVerifier(Util.getEmpCode());
                    dobj.setVerifierBackwardRemarks("[" + currentTimeStampAsMarkHardCopyReceivedInStringFormat + "]" + "Online application has been reverted back to applicant for modification of documents");
                    dobj.setEmpCd(Util.getEmpCodeLong());
                    dobj.setStateCd(Util.getUserStateCode());
                    dobj.setApplNo(this.applicationTradeCertDobj.getApplNo());
                }
                new ApplicationTradeCertImpl().updateRevertStatusInAuditTrailOnRevertToApplicant(this.applicationTradeCertDobj.getApplNo(), currentTimeStampAsMarkHardCopyReceivedInStringFormat, "M", dobj);
                generateMobileSmsNotification();
                JSFUtils.setFacesMessage("Online application has been reverted back to applicant for the purpose of document modification on the behalf of " + ServerUtil.getOfficeName(Util.getUserSeatOffCode(), Util.getUserStateCode()), null, JSFUtils.INFO);
                setApplicationOperated(true);
            } catch (VahanException ve) {
                LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application receive status...", "Error occurred while reverting the pending application. {message[" + ve.getMessage() + "]}"));
            }
            if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
                //RequestContext rc = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopupOnlineApp");
                PrimeFaces.current().executeScript("PF('confirmationPopupOnlineApp').show()");
            }
        }
        this.rtoSideAppl = false;
    }

    public void generateMobileSmsNotification() throws VahanException {
        String mobile_no_otp = String.valueOf(ApplicationTradeCertImpl.fetchApplicantContactNo(this.applicationTradeCertDobj.getDealerFor(), this.applicationTradeCertDobj.getStateCd(), this.applicationTradeCertDobj.getOffCd(), this.applicationTradeCertDobj.getApplicantCategory()));
        try {

            String smsString = "[Apply For Trade Certificate]: Attached uploaded documents within submitted application are required to be modified. "
                    + "Please use Application No[" + this.applicationTradeCertDobj.getApplNo() + "] "
                    + "and Applicant Code[" + applicationTradeCertDobj.getDealerFor() + "] to open existing application "
                    + "and kindly do the needful at the earliest.";
            ServerUtil.sendSMS(mobile_no_otp, smsString);
        } catch (Exception ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public TmConfigurationDobj getTmConfigDobj() {
        return tmConfigDobj;
    }

    public boolean isRtoSideAppl() {
        return rtoSideAppl;
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

    ///////END DMS CONFIGURATION////////////
    public boolean isStockTransferReq() {
        return stockTransferReq;
    }

    public void setStockTransferReq(boolean stockTransferReq) {
        this.stockTransferReq = stockTransferReq;
    }

    public boolean isRenderStockTransferReqCkbox() {
        return renderStockTransferReqCkbox;
    }

    public void setRenderStockTransferReqCkbox(boolean renderStockTransferReqCkbox) {
        this.renderStockTransferReqCkbox = renderStockTransferReqCkbox;
    }

    public String getENDORSEMENT_CONSTANT_VAL() {
        return ENDORSEMENT_CONSTANT_VAL;
    }

    public boolean isMailSent() {
        return mailSent;
    }

    public void setMailSent(boolean mailSent) {
        this.mailSent = mailSent;
    }

    public boolean isShowHardCopyReceivedButton() {
        return showHardCopyReceivedButton;
    }

    public void setShowHardCopyReceivedButton(boolean showHardCopyReceivedButton) {
        this.showHardCopyReceivedButton = showHardCopyReceivedButton;
    }

    public static class ListComparator implements Comparator<SelectItem> {

        @Override
        public int compare(SelectItem o1, SelectItem o2) {
            return o1.getLabel().compareTo(o2.getLabel());
        }
    }
}
