/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.tradecert;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.FileMovementAbstractApplBean;
import nic.vahan.form.dobj.tradecert.IssueTradeCertDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.ApproveImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.tradecert.IssueTradeCertImpl;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.tradecert.ApplicationTradeCertImpl;
import nic.vahan.server.CommonUtils;
import static nic.vahan.server.ServerUtil.Compare;
import nic.vahan.server.mail.MailSender;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import static nic.vahan.form.bean.tradecert.ApplicationTradeCertBean.uploadUtils;
import static nic.vahan.form.bean.tradecert.VerifyApproveTradeCertBean.DMS_UPLOADED_DOCS_LIMIT;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author tranC081
 */
@ManagedBean(name = "issueTradeCert", eager = true)
@ViewScoped
public class IssueTradeCertBean extends FileMovementAbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(IssueTradeCertBean.class);
    @ManagedProperty(value = "#{approveImpl}")
    private ApproveImpl approveImpl;
    private IssueTradeCertDobj issueTradeCertDobj;
    private IssueTradeCertDobj issueTradeCertDobjPrev;
    private final List listApplicationNo;
    private final Map mapSectionsSrNoOfSelectedApplication;
    private boolean renderTradeCertValidityDetailsPanel;
    private final List<IssueTradeCertDobj> applicationSectionsList;
    private int noOfVehGrandTotal;
    private boolean notValid;
    private boolean visibleApplicationDetailPanel;
    private boolean dissableChooseNewRenewTradeCertificateOption;
    private boolean visibleTradeCertificateApplicationPanel;
    private boolean disableSave;
    private boolean disablePrint;
    private boolean renew;
    private ArrayList<ComparisonBean> compBeanList;
    private List<ComparisonBean> prevChangedDataList;
    private boolean duplicate;
    private String tradeCertType;
    private String tradeCertificateSaveResponse;
    private boolean tcForEachVehCatg;
    private final SessionVariables sessionVariables;
    private boolean displayFuel;
    private DateFormat format;
    private boolean doNotShowNoOfVehicles;
    private String dmsFileServerAPIString;  // "http://10.25.97.159:8080/dms-app/search-dms?applno=317013&j_key=vVl/Az1yGsjOAG18WDeScg==&j_securityKey=6D6C069D681B40DBF95CAD7B3ED71BE1djkhf3874394!TZFIMZbTiUtqXMfARJ1DGgyNicWFwYkwTA0ip/Q8Wns=";
    private String applicantMailId;
    private String deficiencyMailSubject;
    private String deficiencyMailContent;
    private String rejectionReasons;
    private String stateNameMentionedInDeficiencyMail;
    private String currentTimeStampAsMarkHardCopyReceivedInStringFormat;
    private boolean allDocumentsChecked;
    private String applicantNameInDeficiencyMail;
    private String applicationNoInDeficiencyMail;
    private String applicantPhoneNumber;
    private boolean applicationOperatedOnApprovalStage;
    private TmConfigurationDobj tmConfigDobj;
    private boolean rtoSideAppl;
    private final String ENDORSEMENT_CONSTANT_VAL = TableConstants.TRADE_CERT_ENDORSEMENT_CONSTANT_VAL;
    private boolean showHardCopyReceivedButton;

    public IssueTradeCertBean() {
        super();
        format = new SimpleDateFormat("dd-MMM-yyyy");
        this.issueTradeCertDobj = new IssueTradeCertDobj();

        try {
            this.issueTradeCertDobjPrev = (IssueTradeCertDobj) issueTradeCertDobj.clone();
        } catch (CloneNotSupportedException cnse) {
            LOGGER.error(cnse);
            FacesContext.getCurrentInstance().addMessage("wb_errorMessages", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Technical Error", null));
        }

        listApplicationNo = new ArrayList<>();
        applicationSectionsList = new ArrayList<>();
        mapSectionsSrNoOfSelectedApplication = new HashMap();

        compBeanList = new ArrayList<ComparisonBean>();

        sessionVariables = new SessionVariables();
        try {
            tmConfigDobj = Util.getTmConfiguration();
        } catch (VahanException ex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            return;
        }
        if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {  //// for Gujarat
            displayFuel = true;
        }
        if (tmConfigDobj.getTmTradeCertConfigDobj().isNoOfVehiclesNotToBeShown()) {  //// for Chhattisgarh
            doNotShowNoOfVehicles = true;
        }
        currentTimeStampAsMarkHardCopyReceivedInStringFormat = "";
    }

    @PostConstruct
    public void init() {

        fillLoginDetails(this.issueTradeCertDobj);
        reset();
        this.issueTradeCertDobj.setNewRenewalTradeCert("New_Trade_Certificate");
        this.visibleTradeCertificateApplicationPanel = true;

        fillApplicationDataFromSession();

        if (tmConfigDobj.getTmTradeCertConfigDobj().isDocumentsUploadNRevert() && !rtoSideAppl) { //// documents_upload_n_revert work   for Maharashtra and Delhi

            //DMS URL fetch by vahan4 dblink table
            try {
                fillDMSDetails();
                DMS_UPLOADED_DOCS_LIMIT = ApplicationTradeCertImpl.getNumberOfDocumentsRequiredToUpload(this.issueTradeCertDobj.getStateCd(), this.issueTradeCertDobj.getNewRenewalTradeCert(), this.issueTradeCertDobj.getApplicantCategory());
                if (!CommonUtils.isNullOrBlank(this.issueTradeCertDobj.getApplNo())) {
                    setAllDocumentsChecked(checkAllDocumentsHaveBeenVerified());
                }
            } catch (Exception ve) {
                LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
                LOGGER.error("ApplicationTradeCertBean.init(...):: EXCEPTION Message:::  " + ve.getMessage() + ", EXCEPTION Cause::: " + ve.getCause());
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Application processing status... ", "Exception occured while fetching DMS URL stored in database. {message::[" + ve.getMessage() + "]}"));
            }

            if (!tmConfigDobj.getTmTradeCertConfigDobj().isAllowFacelessService()) {
                setShowHardCopyReceivedButton(true);
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
            //VerifyApproveTradeCertBean.dmsTradeCertUploadedDocsUrlWithinServer = (String) dmsMap.get("dms_url_ser_tc");    ////////  NOT USED BY NETPROPHET TEAM
            //////////// LOCAL/LIVE
            VerifyApproveTradeCertBean.dmsTradeCertUploadedDocsUrlWithinServer = (String) dmsMap.get("dms_url_tc");      ////////      USED BY NETPROPHET TEAM
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            LOGGER.error("VerifyApproveTradeCertBean.init(...):: EXCEPTION Message:::  " + e.getMessage() + ", EXCEPTION Cause::: " + e.getCause());
            throw new VahanException("Exception occured while fetching DMS URL stored in database.");
        }
    }

    public void updateValidUptoDt(AjaxBehaviorEvent event) {
        if (this.issueTradeCertDobj.getValidDt() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(this.issueTradeCertDobj.getValidDt());
            cal.add(Calendar.DATE, -1);
            cal.add(Calendar.YEAR, 1);
            this.issueTradeCertDobj.setValidUpto(cal.getTime());
        } else {
            this.issueTradeCertDobj.setValidUpto(null);
        }
    }

    private void fillApplicationNo() {
        IssueTradeCertImpl issueTradeCertImpl = new IssueTradeCertImpl();
        Map mapApplicationFor = null;
        try {
            mapApplicationFor = issueTradeCertImpl.getApplicationMap(this.issueTradeCertDobj.getPurCd());

        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        for (Object keyObj : mapApplicationFor.keySet()) {
            String key = (String) keyObj;
            String descr = (String) mapApplicationFor.get(key);
            listApplicationNo.add(new SelectItem("" + key, descr));
        }
    }

    public void onRowSelect(SelectEvent event) {

        IssueTradeCertDobj dobjSelected = (IssueTradeCertDobj) event.getObject();
        dobjSelected.getVehCatgFor();

        this.visibleTradeCertificateApplicationPanel = true;
    }

    private void fillApplicationSectionsList() {
        this.applicationSectionsList.clear();
        this.noOfVehGrandTotal = 0;
        if (!mapSectionsSrNoOfSelectedApplication.isEmpty()) {
            int srNo = 1;
            for (Object keyObj : mapSectionsSrNoOfSelectedApplication.keySet()) {
                String key = (String) keyObj;
                IssueTradeCertDobj dobj = (IssueTradeCertDobj) mapSectionsSrNoOfSelectedApplication.get(key);
                this.applicationSectionsList.add(dobj);
                this.noOfVehGrandTotal += Integer.valueOf(dobj.getNoOfAllowedVehicles());
                fillLoginDetails(dobj);
                if (dobj.getValidUpto() != null) {
                    dobj.setValidUptoAsString(format.format(dobj.getValidUpto()));
                }
                dobj.setSrNo(srNo + "");
                srNo++;
            }
            this.issueTradeCertDobj = (IssueTradeCertDobj) mapSectionsSrNoOfSelectedApplication.get("1");

            dissableChooseNewRenewTradeCertificateOption = true;
            disableSave = false;
            renderTradeCertValidityDetailsPanel = true;
        } else {
            dissableChooseNewRenewTradeCertificateOption = false;
            disableSave = true;
            renderTradeCertValidityDetailsPanel = false;
        }

    }

    public String click() {
        if (applicationOperatedOnApprovalStage) {
            return "seatwork";
        }
        return "";
    }

    public void reset() {

        resetApplicationNo();

        this.noOfVehGrandTotal = 0;
        applicationSectionsList.clear();
        visibleApplicationDetailPanel = false;
        visibleTradeCertificateApplicationPanel = false;
        dissableChooseNewRenewTradeCertificateOption = false;
        renderTradeCertValidityDetailsPanel = false;
        disableSave = true;
        disablePrint = true;
        if (this.issueTradeCertDobj != null) {
            this.issueTradeCertDobj.reset();
        }
        tradeCertType = "";
        tradeCertificateSaveResponse = "";
        tcForEachVehCatg = false;
        applicationOperatedOnApprovalStage = false;
    }

    private void resetApplicationNo() {
        listApplicationNo.clear();
        listApplicationNo.add(new SelectItem("-1", "Select"));
        fillApplicationNo();
        //    mapSectionsSrNoOfSelectedApplication.clear();

    }

    public String back() {
        reset();
        return "home";
    }

    public void validateForm() {

        /**
         * Strictly enforced check for renewal of trade certificate only (after
         * its expiry of 1 year)
         *
         * @State: All
         */
//        if ("RENEW".equalsIgnoreCase(tradeCertType)) {
//            for (Object keyObj : this.mapSectionsSrNoOfSelectedApplication.keySet()) {
//                IssueTradeCertDobj dobj = (IssueTradeCertDobj) mapSectionsSrNoOfSelectedApplication.get(keyObj);
//                Date currentDate = new Date();
//                if (dobj.getValidUpto().after(currentDate)) {
//                        JSFUtils.setFacesMessage("! Trade certificate [" + dobj.getTradeCertNo() + "] has not expired, application for renewal can not be approved.", null, JSFUtils.WARN);
//                        notValid = true;
//                }               
//            }
//            if (notValid) {
//                return;
//            }
//        }
        if (tmConfigDobj.getTmTradeCertConfigDobj().isDealerValidUptoPlus1YrAsTcValidUpto()) {   //// dealer_valid_upto_plus_1_yr_as_tc_valid_upto work  for Chhattisgarh
            try {
                IssueTradeCertImpl impl = new IssueTradeCertImpl();
                Date dealerValidUpto = null;
                for (Object keyObj : this.mapSectionsSrNoOfSelectedApplication.keySet()) {
                    IssueTradeCertDobj dobj = (IssueTradeCertDobj) mapSectionsSrNoOfSelectedApplication.get(keyObj);
                    dealerValidUpto = impl.fetchDealerValidityDateFromMaster(dobj.getDealerFor());
                }
                if (!"CG".equals(issueTradeCertDobj.getStateCd())) {
                    if (dealerValidUpto != null) {
                        for (Object keyObj : this.mapSectionsSrNoOfSelectedApplication.keySet()) {
                            IssueTradeCertDobj dobj = (IssueTradeCertDobj) mapSectionsSrNoOfSelectedApplication.get(keyObj);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(dealerValidUpto);
                            dobj.setValidDt(cal.getTime());
                            cal.add(Calendar.YEAR, 1);
                            dobj.setValidUpto(cal.getTime());
                        }
                        notValid = false;
                    } else {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application can not move further because Valid Upto Date is not Found in Database.Please contact to Administrator ", "Application can not move further because Valid Upto Date is not Found in Database.Please contact to Administrator ");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        notValid = true;
                    }
                } else {
                    notValid = false;
                }
            } catch (VahanException ve) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
                FacesContext.getCurrentInstance().addMessage(null, message);
                notValid = true;
            }
        } else {
            notValid = false;
        }

        if (tmConfigDobj.getTmTradeCertConfigDobj().isDocumentsUploadNRevert()
                && !"ENDORSEMENT".equals(tradeCertType)
                && !rtoSideAppl) { ///// documents_upload_n_revert work
            if (nic.vahan.server.CommonUtils.isNullOrBlank(currentTimeStampAsMarkHardCopyReceivedInStringFormat) && isShowHardCopyReceivedButton()) {
                JSFUtils.setFacesMessage("! Please receive hard copies of all mandatory documents along with hard copy of deficiency mail (if any)", null, JSFUtils.ERROR);
                JSFUtils.setFacesMessage("and press 'Hard Copy Physically Received' before submitting the online pending application.", null, JSFUtils.ERROR);
                notValid = true;
            }

            ///// CHECK FOR DMS TICKS IN CASE OF FORWARD
            if (!isAllDocumentsChecked() && getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)) {
                JSFUtils.setFacesMessage("! User can not forward the online application to next seat, unless all documents have been checked.", null, JSFUtils.WARN);
                notValid = true;
            }

            ///// CHECK FOR DMS TICKS IN CASE OF REVERT
            if (isAllDocumentsChecked() && getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                JSFUtils.setFacesMessage("! Application cannot be reverted back if all attached requisite and mandatory documents are checked", null, JSFUtils.WARN);
                JSFUtils.setFacesMessage("or ticked. Kindly remove the check/tick with respect to the document selected for modification.", null, JSFUtils.WARN);
                notValid = true;
            }
        }

    }

    @Override
    public String save() {

        String returnLocation = "";
        try {
            validateForm();
            if (notValid) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "There are some problems in saving data for trade certificate .", "There are some problems in saving data for trade certificate.");
                FacesContext.getCurrentInstance().addMessage(null, message);

            } else {

                returnLocation = "seatwork";

            }
        } catch (Exception ve) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "There are some problems in saving fee collection data for trade certificate application section due to " + ve.getMessage(), "There are some problems in saving fee collection data for trade certificate application section due to " + ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            LOGGER.error(ve);
        }
        return returnLocation;

    }

    private List<IssueTradeCertDobj> getListFromMap() {
        List<IssueTradeCertDobj> list = new ArrayList<>();
        for (Object keyObj : this.mapSectionsSrNoOfSelectedApplication.keySet()) {
            IssueTradeCertDobj dobj = (IssueTradeCertDobj) mapSectionsSrNoOfSelectedApplication.get(keyObj);
            list.add(dobj);
        }
        return list;
    }

    private void setDataIntoDtoList(Map mapSectionsSrNoOfSelectedApplication) {
        for (Object keyObj : mapSectionsSrNoOfSelectedApplication.keySet()) {
            IssueTradeCertDobj dobj = (IssueTradeCertDobj) mapSectionsSrNoOfSelectedApplication.get(keyObj);
            dobj.setValidDt(this.issueTradeCertDobj.getValidDt());
            dobj.setValidUpto(this.issueTradeCertDobj.getValidUpto());
            dobj.setIssueDt(this.issueTradeCertDobj.getIssueDt());
        }
    }

    private void postSaveOperation(String saveReturn) {

        String saveForTC = saveReturn.substring(saveReturn.lastIndexOf(":") + 1).replaceAll("SUCCESS", "");
        if (!renew && !duplicate) {
            this.tradeCertificateSaveResponse = saveForTC;
            if (!saveForTC.contains("/")) {
                this.tcForEachVehCatg = true;
            }
        }

        this.disableSave = true;
        this.disablePrint = false;

        resetApplicationNo();

        setDisableSaveButton(true);

        PrimeFaces.current().ajax().update("new_trade_cert_subview:application_sections_panel");
        PrimeFaces.current().ajax().update("new_trade_cert_subview:appl_no_pnl");

        PrimeFaces.current().ajax().update("new_trade_cert_subview:outPnlSaveAndMoveBack");

    }

    public IssueTradeCertDobj getIssueTradeCertDobj() {
        return this.issueTradeCertDobj;
    }

    private void fillLoginDetails(IssueTradeCertDobj dobj) {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        dobj.setPurCd(Util.getSelectedSeat().getPur_cd() + "");
        dobj.setEmpCd((Long) sessionMap.get("emp_cd"));
        dobj.setOffCd(Util.getUserSeatOffCode());
        dobj.setStateCd((String) sessionMap.get("state_cd"));
        dobj.setStateName((String) sessionMap.get("state_name"));
        dobj.setEmpName((String) sessionMap.get("emp_name"));
        dobj.setDesigName((String) sessionMap.get("desig_name"));

    }

    public boolean isDissableChooseNewRenewTradeCertificateOption() {
        return dissableChooseNewRenewTradeCertificateOption;
    }

    public boolean isVisibleTradeCertificateApplicationPanel() {
        return visibleTradeCertificateApplicationPanel;
    }

    public List getListApplicationNo() {
        return listApplicationNo;
    }

    public List<IssueTradeCertDobj> getApplicationSectionsList() {
        return applicationSectionsList;
    }

    public int getNoOfVehGrandTotal() {
        return noOfVehGrandTotal;
    }

    public boolean isVisibleApplicationDetailPanel() {
        return visibleApplicationDetailPanel;
    }

    public void setIssueTradeCertDobj(IssueTradeCertDobj issueTradeCertDobj) {
        if (!mapSectionsSrNoOfSelectedApplication.isEmpty()) {
            this.issueTradeCertDobj = (IssueTradeCertDobj) mapSectionsSrNoOfSelectedApplication.get("1");
        } else {
            this.issueTradeCertDobj = issueTradeCertDobj;
        }
    }

    public boolean isDisableSave() {
        return disableSave;
    }

    public boolean isRenderTradeCertValidityDetailsPanel() {
        return renderTradeCertValidityDetailsPanel;
    }

    public ApproveImpl getApproveImpl() {
        return approveImpl;
    }

    public void setApproveImpl(ApproveImpl approveImpl) {
        this.approveImpl = approveImpl;
    }

    public IssueTradeCertDobj getIssueTradeCertDobjPrev() {
        return issueTradeCertDobjPrev;
    }

    public void setIssueTradeCertDobjPrev(IssueTradeCertDobj issueTradeCertDobjPrev) {
        this.issueTradeCertDobjPrev = issueTradeCertDobjPrev;
    }

    public ArrayList<ComparisonBean> addToComapreChangesList(ArrayList<ComparisonBean> compBeanListPrevParam) throws VahanException {
        ArrayList<ComparisonBean> compBeanListPrev = compBeanListPrevParam;
        List<ComparisonBean> list = compareChanges();

        if (compBeanListPrev == null) {
            compBeanListPrev = new ArrayList<ComparisonBean>();
        }
        if (!list.isEmpty()) {
            compBeanListPrev.addAll(list);
        }

        return compBeanListPrev;
    }

    @Override
    public ArrayList<ComparisonBean> compareChanges() {

        if (issueTradeCertDobjPrev == null) {
            return getCompBeanList();
        }
        getCompBeanList().clear();

        Compare("validDt", issueTradeCertDobjPrev.getValidDt(), issueTradeCertDobj.getValidDt(), getCompBeanList());
        Compare("issueDt ", issueTradeCertDobjPrev.getIssueDt(), issueTradeCertDobj.getIssueDt(), getCompBeanList());

        return getCompBeanList();

    }

    @Override
    public String saveAndMoveFile() {
        String returnLocation = "";
        try {
            validateForm();
            if (notValid) {
                if (tmConfigDobj.getTmTradeCertConfigDobj().isDocumentsUploadNRevert() && !rtoSideAppl) {  //// documents_upload_n_revert work
                    if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
                        //RequestContext rc = RequestContext.getCurrentInstance();
                        PrimeFaces.current().ajax().update("new_trade_cert_subview:confirmationPopup");
                        PrimeFaces.current().executeScript("PF('confDlgTradeCert').show()");
                        return "";
                    }
                }
                if (FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "There are some problems in saving data for trade certificate .", "There are some problems in saving data for trade certificate.");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
            } else {
                Status_dobj status = new Status_dobj();
                status.setAppl_dt(appl_details.getAppl_dt());
                status.setAppl_no(appl_details.getAppl_no());
                status.setPur_cd(appl_details.getPur_cd());
                status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
                status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
                status.setStatus(getApp_disapp_dobj().getNew_status());
                status.setCurrent_role(appl_details.getCurrent_role());

                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)) {

                    IssueTradeCertImpl impl = new IssueTradeCertImpl();
                    List list = getListFromMap();

                    String saveReturn = impl.saveAndMoveFile(list, status, ComparisonBeanImpl.changedDataContents(compareChanges()), tradeCertType.trim());

                    if (saveReturn.contains("SUCCESS")) {
                        postSaveOperation(saveReturn);
                        FacesMessage message = null;
                        if (!renew && !duplicate) {           /////// New
                            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Trade certificate [" + issueTradeCertDobj.getTradeCertNo() + "] successfully generated", "Trade certificate [" + issueTradeCertDobj.getTradeCertNo() + "] successfully generated ");
                        } else {
                            String statusOfCertificate = "";   ///// Renew 
                            if (Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_NEW) {
                                if (((IssueTradeCertDobj) list.get(0)).getStatus() != null && ((IssueTradeCertDobj) list.get(0)).getStatus().equals("E")) { /// E: Edit Trade Certificate
                                    statusOfCertificate = "edited";
                                } else {
                                    statusOfCertificate = "renewed";
                                }
                            } else {                            ////// Duplicate    
                                statusOfCertificate = "duplicated";
                            }
                            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Trade certificate successfully " + statusOfCertificate, "Trade certificate successfully " + statusOfCertificate);
                        }
                        setApplicationOperatedOnApprovalStage(true);
                        FacesContext.getCurrentInstance().addMessage(null, message);
                    } else {
                        if (saveReturn.contains(":")) {
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "There are some problems in saving data for trade certificate due to '" + saveReturn.split(":")[1] + "'", "There are some problems in saving data for trade certificate due to '" + saveReturn.split(":")[1] + "'");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                        } else {
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "There are some problems in saving data for trade certificate", "There are some problems in saving data for trade certificate");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                        }

                    }
                } else if (status.getStatus().equals(TableConstants.STATUS_REVERT)) {

                    status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());

                    IssueTradeCertImpl impl = new IssueTradeCertImpl();
                    List list = getListFromMap();

                    String saveReturn = impl.saveAndMoveFile(list, status, ComparisonBeanImpl.changedDataContents(compareChanges()), tradeCertType.trim());

                    if (saveReturn.contains("SUCCESS")) {

                        FacesMessage message = null;
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Application for trade certificate reverted back successfully.", "Application for trade certificate reverted back successfully.");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                    } else {
                        if (saveReturn.contains(":")) {
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "There are some problems in reverting back the application for trade certificate due to '" + saveReturn.split(":")[1] + "'", "There are some problems in reverting back the application for trade certificate due to '" + saveReturn.split(":")[1] + "'");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                        } else {
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "There are some problems in reverting back the application for trade certificate", "There are some problems in reverting back the application for trade certificate");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                        }

                    }
                    setApplicationOperatedOnApprovalStage(true);
                }
            }
        } catch (VahanException ve) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("new_trade_cert_subview:confirmationPopup");
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
        return this.prevChangedDataList;
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void fillApplicationDataFromSession() {
        SeatAllotedDetails seatAllotedDetails = Util.getSelectedSeat();
        this.issueTradeCertDobj.setApplNo(seatAllotedDetails.getAppl_no());

        dissableChooseNewRenewTradeCertificateOption = true;
        renderTradeCertValidityDetailsPanel = false;
        mapSectionsSrNoOfSelectedApplication.clear();
        IssueTradeCertImpl issueTradeCertImpl = new IssueTradeCertImpl();
        try {
            issueTradeCertImpl.getAllSrNoForSelectedApplication(this.issueTradeCertDobj.getApplNo(), mapSectionsSrNoOfSelectedApplication);
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        if (!mapSectionsSrNoOfSelectedApplication.isEmpty()) {
            IssueTradeCertDobj issueTradeCertDobjFrom = (IssueTradeCertDobj) mapSectionsSrNoOfSelectedApplication.get("1");
            rtoSideAppl = issueTradeCertDobjFrom.isRtoSideAppl();
            this.issueTradeCertDobj.setDealerFor(issueTradeCertDobjFrom.getDealerFor());
            this.issueTradeCertDobj.setDealerName(issueTradeCertDobjFrom.getDealerName());
            this.issueTradeCertDobj.setApplicantCategory(issueTradeCertDobjFrom.getApplicantCategory());
            this.issueTradeCertDobj.setNewRenewalTradeCert(issueTradeCertDobjFrom.getNewRenewalTradeCert());
            try {
                for (Object dobjObj : mapSectionsSrNoOfSelectedApplication.keySet()) {
                    IssueTradeCertDobj issueTradeCertDobj = (IssueTradeCertDobj) mapSectionsSrNoOfSelectedApplication.get(dobjObj);
                    issueTradeCertImpl.fillTCNoAndNoOfVchUsed(issueTradeCertDobj);
                    if (!CommonUtils.isNullOrBlank(issueTradeCertDobj.getTradeCertNo())) {
                        Date validUptoDate = issueTradeCertImpl.getValidUptoOnTradeCertNo(issueTradeCertDobj.getTradeCertNo());
                        issueTradeCertDobjFrom.setValidUpto(validUptoDate);
                        issueTradeCertDobj.setValidUpto(validUptoDate);
                        if (validUptoDate != null) {
                            issueTradeCertDobjFrom.setValidUptoAsString(format.format(validUptoDate));
                            issueTradeCertDobj.setValidUptoAsString(format.format(validUptoDate));
                        }
                    }
                }
            } catch (VahanException ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }

            if (issueTradeCertDobjFrom.getTradeCertNo() != null && !"".equals(issueTradeCertDobjFrom.getTradeCertNo())
                    && (seatAllotedDetails.getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_NEW)) {
                if (issueTradeCertDobjFrom.getStatus() != null && issueTradeCertDobjFrom.getStatus().equals("E")) {  ////// E: Edit Trade Certificate
                    renew = true;
                    tradeCertType = "EDIT";
                } else {
                    renew = true;
                    if (TableConstants.TRADE_CERT_ENDORSEMENT_CONSTANT_VAL.equals(issueTradeCertDobjFrom.getNewRenewalTradeCert())) {
                        tradeCertType = "ENDORSEMENT";
                    } else {
                        tradeCertType = "RENEW";
                    }
                }
            } else if (issueTradeCertDobjFrom.getTradeCertNo() != null && !"".equals(issueTradeCertDobjFrom.getTradeCertNo())
                    && seatAllotedDetails.getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_DUP) {
                duplicate = true;
                tradeCertType = "DUPLICATE";
            } else {
                tradeCertType = "NEW";
                tradeCertificateSaveResponse = "";
                tcForEachVehCatg = false;
            }
        }

        setValidityPanel(mapSectionsSrNoOfSelectedApplication);

        fillApplicationSectionsList();
        if (!this.applicationSectionsList.isEmpty()) {
            this.visibleApplicationDetailPanel = true;
        } else {
            this.visibleApplicationDetailPanel = false;

        }

        visibleTradeCertificateApplicationPanel = true;

    }

    ////////////////////////////////////////////
    private void setValidityPanel(Map mapApplicationSectionsOnlyInDuplicateCase) {

        this.issueTradeCertDobj.setValidDt(null);
        this.issueTradeCertDobj.setValidUpto(null);
        this.issueTradeCertDobj.setIssueDt(null);
        if (Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_NEW) {
            if (this.issueTradeCertDobj.getValidDt() == null) {
                this.issueTradeCertDobj.setValidDt(new Date());
            }
            if (this.issueTradeCertDobj.getValidDt() != null && this.issueTradeCertDobj.getValidUpto() == null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(this.issueTradeCertDobj.getValidDt());
                cal.add(Calendar.DATE, -1);
                cal.add(Calendar.YEAR, 1);
                this.issueTradeCertDobj.setValidUpto(cal.getTime());

            }
            if (this.issueTradeCertDobj.getIssueDt() == null) {
                this.issueTradeCertDobj.setIssueDt(new Date());
            }
            //Modification in case of renew only for "RJ"
            if (tradeCertType.equals("RENEW") && Util.getUserStateCode().equals("RJ")) {   ///////// modify_validity_for_rajasthan_in_renew  work
                for (Object keyObj : mapApplicationSectionsOnlyInDuplicateCase.keySet()) {
                    String key = (String) keyObj;
                    IssueTradeCertDobj dobj = (IssueTradeCertDobj) mapApplicationSectionsOnlyInDuplicateCase.get(key);
                    IssueTradeCertImpl issueTradeCertImpl = new IssueTradeCertImpl();
                    try {
                        issueTradeCertImpl.getValidityDetailsOnTradeCertNoForDuplicateCase(dobj);
                        Date validFrom = dobj.getValidUpto();
                        Calendar c = Calendar.getInstance();
                        c.setTime(validFrom);
                        c.add(Calendar.DATE, 1); // Adding 1 Day
                        validFrom = c.getTime();
                        c.setTime(validFrom);
//                        c.add(Calendar.YEAR, 1); // Adding 1 Year
//                        c.add(Calendar.DATE, -1);
//                        Date validUpto = c.getTime();
                        dobj.setValidDt(validFrom);
                        dobj.setIssueDt(new Date());
//                        dobj.setValidUpto(validUpto);
//                        if (validUpto != null) {
//                            dobj.setValidUptoAsString(format.format(validUpto));
//                        }
                        dobj.setValidUptoAsString(format.format(dobj.getValidUpto()));
                        // end up here
                    } catch (VahanException ex) {
                        LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                    }
                }
            }

        } else if (Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_DUP) {
            for (Object keyObj : mapApplicationSectionsOnlyInDuplicateCase.keySet()) {
                String key = (String) keyObj;
                IssueTradeCertDobj dobj = (IssueTradeCertDobj) mapApplicationSectionsOnlyInDuplicateCase.get(key);
                IssueTradeCertImpl issueTradeCertImpl = new IssueTradeCertImpl();
                try {
                    issueTradeCertImpl.getValidityDetailsOnTradeCertNoForDuplicateCase(dobj);
                    if (dobj.getValidUpto() != null) {
                        dobj.setValidUptoAsString(format.format(dobj.getValidUpto()));
                    }
                } catch (VahanException ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

    }

    public boolean isRenew() {
        return renew;
    }

    public boolean isDisablePrint() {
        return disablePrint;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public String getTradeCertType() {
        return tradeCertType;
    }

    public void confirmTradeCertPrint() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("tcNo", this.issueTradeCertDobj.getTradeCertNo());
        String tcType = "";
        if (renew) {
            tcType = "RENEWAL";
        } else if (duplicate) {
            tcType = "DUPLICATE";
        } else {
            tcType = "NEW";
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("tcType", tcType);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("tcApplListOfSelectedDealer", applicationSectionsList);
        // RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('printTradeCertificate').show()");
    }

    public void printTradeCertDetails() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/vahan/ui/tradecert/formTradeCertPrint.xhtml");
        } catch (IOException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    ////////////////////////////////// DMS UPLOAD UTILITY API  //////////////////////////////////
    public void sendRejectionMessages() throws VahanException {
        try {

            if (generateDeficiencyMailAndSend()) {
                generateSMS();

                JSFUtils.setFacesMessage("Mail has been sent to the applicant by state transport authority.", null, JSFUtils.INFO);
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

    public void openUploadedDocuments() {
        if (!mapSectionsSrNoOfSelectedApplication.isEmpty() && this.issueTradeCertDobj == null) {
            for (Object keyObj : mapSectionsSrNoOfSelectedApplication.keySet()) {
                IssueTradeCertDobj dobjFrom = (IssueTradeCertDobj) mapSectionsSrNoOfSelectedApplication.get(keyObj);
                this.issueTradeCertDobj = dobjFrom;
                break;
            }
            fillLoginDetails(this.issueTradeCertDobj);
        }
        if (!CommonUtils.isNullOrBlank(this.issueTradeCertDobj.getApplNo())) {
            displayDMSUploadUtility(this.issueTradeCertDobj.getApplNo());
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
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_APPROVE
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_APPROVE_DUP) {
                this.dmsFileServerAPIString = uploadUtils(tempApplNo, "A");
            }
            PrimeFaces.current().executeScript("PF('ShowImage').show();");
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", ve.getMessage()));
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
                noOfDocumentsUploaded = ApplicationTradeCertImpl.checkStatusOfAllDocuments(this.issueTradeCertDobj.getApplNo(), "R");
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_VERIFICATION
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_VERIFICATION_DUP) {
                noOfDocumentsUploaded = ApplicationTradeCertImpl.checkStatusOfAllDocuments(this.issueTradeCertDobj.getApplNo(), "V");
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_APPROVE
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_APPROVE_DUP) {
                noOfDocumentsUploaded = ApplicationTradeCertImpl.checkStatusOfAllDocuments(this.issueTradeCertDobj.getApplNo(), "A");
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

        PrimeFaces.current().executeScript("PF('bui_fancy').hide();");
    }

    public void generateDeficiencyMailOpeningMatter() {

        this.setRejectionReasons("");

        if (this.issueTradeCertDobj == null && !mapSectionsSrNoOfSelectedApplication.isEmpty()) {
            for (Object keyObj : mapSectionsSrNoOfSelectedApplication.keySet()) {
                IssueTradeCertDobj dobjFrom = (IssueTradeCertDobj) mapSectionsSrNoOfSelectedApplication.get(keyObj);
                this.issueTradeCertDobj = dobjFrom;
                fillLoginDetails(this.issueTradeCertDobj);
                break;
            }
        }
        applicantNameInDeficiencyMail = this.issueTradeCertDobj.getDealerName();
        applicationNoInDeficiencyMail = this.issueTradeCertDobj.getApplNo();
        deficiencyMailContent = "Dear <b>" + applicantNameInDeficiencyMail + "</b>,<br/> "
                + "The following discrepancies in online trade certificate application [ No: <b><i>" + applicationNoInDeficiencyMail + "</i></b> ] have been found which are required to be rectified on urgent basis:- <br/>";
        stateNameMentionedInDeficiencyMail = Util.getSession().getAttribute("state_name").toString();
        try {
            applicantMailId = ApplicationTradeCertImpl.fetchApplicantEmailId(this.issueTradeCertDobj.getDealerFor(), this.issueTradeCertDobj.getStateCd(), this.issueTradeCertDobj.getOffCd(), this.issueTradeCertDobj.getApplicantCategory());
            deficiencyMailSubject = "[Vahan] Discrepancies have been found in online trade certificate application.";
            applicantPhoneNumber = String.valueOf(ApplicationTradeCertImpl.fetchApplicantContactNo(this.issueTradeCertDobj.getDealerFor(), this.issueTradeCertDobj.getStateCd(), this.issueTradeCertDobj.getOffCd(), this.issueTradeCertDobj.getApplicantCategory()));
        } catch (Exception ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application receive status...", "Error occurred while generating deficiency mail opening matter. {message[" + ve.getMessage() + "]}"));
        }
        if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            // RequestContext rc = RequestContext.getCurrentInstance();
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

    public void generateSMS() throws VahanException {

        try {

            String smsString = "[Apply For Trade Certificate]: Discrepancies in online trade certificate application [ No:" + applicationNoInDeficiencyMail + " ] have been found which are required to be rectified on urgent basis. For details please check mail in [ mailId: " + applicantMailId + " ] account.";
            ServerUtil.sendSMS(applicantPhoneNumber, smsString);
        } catch (Exception ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            throw new VahanException(ve.getMessage());
        }
    }

    private boolean checkAllDocumentsHaveBeenVerified() {
        try {

            ///////////////////////// UNCOMMENT TO TEST WITHOUT DMS /////////////////////////
//            int noOfDocumentsUploaded = DMS_UPLOADED_DOCS_LIMIT;
            ////////////////////////////////////////////////////////////////////////////
            ///////////////////////// COMMENT TO TEST WITHOUT DMS /////////////////////////
            int noOfDocumentsUploaded = 0;
            if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE) {
                noOfDocumentsUploaded = ApplicationTradeCertImpl.checkStatusOfAllDocuments(this.issueTradeCertDobj.getApplNo(), "R");
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_VERIFICATION
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_VERIFICATION_DUP) {
                noOfDocumentsUploaded = ApplicationTradeCertImpl.checkStatusOfAllDocuments(this.issueTradeCertDobj.getApplNo(), "V");
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_APPROVE
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_APPROVE_DUP) {
                noOfDocumentsUploaded = ApplicationTradeCertImpl.checkStatusOfAllDocuments(this.issueTradeCertDobj.getApplNo(), "A");
            }
            ////////////////////////////////////////////////////////////////////////////////
            if (TableConstants.TRADE_CERT_ENDORSEMENT_CONSTANT_VAL.equals(issueTradeCertDobj.getNewRenewalTradeCert())) {
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

    public String getTradeCertificatesListSize() {
        return tradeCertificateSaveResponse;
    }

    public void setTradeCertificatesListSize(String tradeCertificatesListSize) {
        this.tradeCertificateSaveResponse = tradeCertificatesListSize;
    }

    public boolean isTcForEachVehCatg() {
        return tcForEachVehCatg;
    }

    public void setTcForEachVehCatg(boolean tcForEachVehCatg) {
        this.tcForEachVehCatg = tcForEachVehCatg;
    }

    public boolean isDisplayFuel() {
        return displayFuel;
    }

    public boolean isDoNotShowNoOfVehicles() {
        return doNotShowNoOfVehicles;
    }

    public void setDoNotShowNoOfVehicles(boolean doNotShowNoOfVehicles) {
        this.doNotShowNoOfVehicles = doNotShowNoOfVehicles;
    }

    public String getDmsFileServerAPIString() {
        return dmsFileServerAPIString;
    }

    public void setDmsFileServerAPIString(String dmsFileServerAPIString) {
        this.dmsFileServerAPIString = dmsFileServerAPIString;
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

    public String getDeficiencyMailContent() {
        return deficiencyMailContent;
    }

    public void setDeficiencyMailContent(String deficiencyMailContent) {
        this.deficiencyMailContent = deficiencyMailContent;
    }

    public String getRejectionReasons() {
        return rejectionReasons;
    }

    public void setRejectionReasons(String rejectionReasons) {
        this.rejectionReasons = rejectionReasons;
    }

    public String getStateNameMentionedInDeficiencyMail() {
        return stateNameMentionedInDeficiencyMail;
    }

    public void setStateNameMentionedInDeficiencyMail(String stateNameMentionedInDeficiencyMail) {
        this.stateNameMentionedInDeficiencyMail = stateNameMentionedInDeficiencyMail;
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

    public boolean isAllDocumentsChecked() {
        return allDocumentsChecked;
    }

    public void setAllDocumentsChecked(boolean allDocumentsChecked) {
        this.allDocumentsChecked = allDocumentsChecked;
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

    public TmConfigurationDobj getTmConfigDobj() {
        return tmConfigDobj;
    }

    public boolean isRtoSideAppl() {
        return rtoSideAppl;
    }

    public boolean isApplicationOperatedOnApprovalStage() {
        return applicationOperatedOnApprovalStage;
    }

    public void setApplicationOperatedOnApprovalStage(boolean applicationOperatedOnApprovalStage) {
        this.applicationOperatedOnApprovalStage = applicationOperatedOnApprovalStage;
    }

    public String getENDORSEMENT_CONSTANT_VAL() {
        return ENDORSEMENT_CONSTANT_VAL;
    }

    public boolean isShowHardCopyReceivedButton() {
        return showHardCopyReceivedButton;
    }

    public void setShowHardCopyReceivedButton(boolean showHardCopyReceivedButton) {
        this.showHardCopyReceivedButton = showHardCopyReceivedButton;
    }
}
