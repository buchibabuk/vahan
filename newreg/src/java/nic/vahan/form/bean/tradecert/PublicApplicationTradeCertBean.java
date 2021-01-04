/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.tradecert;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.tradecert.PublicApplicationTradeCertDobj;
import nic.vahan.form.impl.tradecert.PublicApplicationTradeCertImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.user_mgmt.dobj.DealerMasterDobj;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.FileMovementAbstractApplBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.tradecert.VerifyApproveTradeCertDobj;
import nic.vahan.form.impl.ApproveImpl;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.tradecert.ApplicationTradeCertImpl;

/**
 *
 * @author tranC081
 */
@ManagedBean(name = "publicApplicationTradeCertBean", eager = true)
@ViewScoped
public class PublicApplicationTradeCertBean extends FileMovementAbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(PublicApplicationTradeCertBean.class);
    @ManagedProperty(value = "#{approveImpl}")
    private ApproveImpl approveImpl;
    private final Map mapSectionsSrNoOfSelectedDealer;
    private PublicApplicationTradeCertDobj publicApplicationTradeCertDobj;
    private final List vehClassList;
    private final List<SelectItem> applicantManufacturerList;
    private final List applicantDistrictList;
    private final List fixedDistrictList;
    private final List applicantOfficeList;
    private boolean company;
    private int noOfVehGrandTotal;
    private boolean visibleApplicationDetailPanel;
    private String applicationNo;
    private final Set affliatedVehicleClassSelectedSet;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private Pattern pattern;
    private Matcher matcher;
    private boolean validationNotSuccessful;
    private boolean addingBasicDetailsDisabled;
    private final Set vehClassAffliatedFinalSet;
    private boolean dissableState;
    private boolean disableField = false;
    private boolean renderTradePanel = false;
    private String applicationModeRadio;
    private String applicationTypeRadio;
    private boolean disableFieldforexist = false;
    private boolean showFinancer;
    private final SessionVariables sessionVariables;
    private boolean renderFileMovement;
    private String currentDate;
    private String applicationType;
    private String applicationFlowSrNo;
    private boolean verifyMode;
    private ArrayList<ComparisonBean> compBeanList;
    private boolean basicDetailsSaved;
    private boolean applicationInVerifyStage;
    private boolean hideApplicantType;
    private final List vehicleClassCategoryMappingList;

    public PublicApplicationTradeCertBean() {
        publicApplicationTradeCertDobj = new PublicApplicationTradeCertDobj();
        vehClassList = new ArrayList();
        applicantDistrictList = new ArrayList();
        fixedDistrictList = new ArrayList();
        applicantOfficeList = new ArrayList();
        applicantManufacturerList = new ArrayList<>();
        affliatedVehicleClassSelectedSet = new HashSet();
        mapSectionsSrNoOfSelectedDealer = new HashMap();
        pattern = Pattern.compile(EMAIL_PATTERN);
        vehClassAffliatedFinalSet = new HashSet();
        sessionVariables = new SessionVariables();
        compBeanList = new ArrayList<ComparisonBean>();
        vehicleClassCategoryMappingList = new ArrayList();
    }

    @PostConstruct
    public void init() {
        applicationNo = "";
        applicationFlowSrNo = "1"; // For generation of new application
        renderTradePanel = true; // Added by Chhavindra
        applicationModeRadio = "Generate_Application";
        applicationTypeRadio = "New_Application";
        company = true;
        this.publicApplicationTradeCertDobj.setTradeCertNo("");
        this.publicApplicationTradeCertDobj.setIndividualOrCompany("Company");
        this.publicApplicationTradeCertDobj.setApplicantCategory(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER);
        this.publicApplicationTradeCertDobj.setApplicantState(sessionVariables.getStateCodeSelected());
        this.publicApplicationTradeCertDobj.setStateCd(sessionVariables.getStateCodeSelected());
        this.publicApplicationTradeCertDobj.setApplicantOffice(sessionVariables.getOffCodeSelected() + "");
        this.publicApplicationTradeCertDobj.setOffCd(sessionVariables.getOffCodeSelected());
        this.publicApplicationTradeCertDobj.setEmpCd(Long.valueOf(sessionVariables.getEmpCodeLoggedIn()));
        fillLoginDetails(this.publicApplicationTradeCertDobj);
        codeForModifySection2Section3Listener();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        currentDate = sdf.format(new Date());
        this.applicationType = "NEW";
        SeatAllotedDetails seatAllotedDetails = Util.getSelectedSeat();

        if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_DEALER_APPL_INSPECT
                || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_DEALER_APPL_VERIFY
                || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_DEALER_APPL_APPROVE) {
            this.publicApplicationTradeCertDobj.setApplNo(seatAllotedDetails.getAppl_no());
            this.publicApplicationTradeCertDobj.setPurCd(seatAllotedDetails.getPur_cd() + "");
            this.applicationFlowSrNo = appl_details.getCurrent_flow_slno() + "";
            fillApplicationSectionsListVerifyForSelectedApplication(this.publicApplicationTradeCertDobj.getApplNo(), true);
            renderFileMovement = true;
            verifyMode = true;
            this.disableFieldforexist = true;
            this.addingBasicDetailsDisabled = true;
            this.visibleApplicationDetailPanel = true;

            Iterator keyIterator = mapSectionsSrNoOfSelectedDealer.keySet().iterator();
            while (keyIterator.hasNext()) {
                PublicApplicationTradeCertDobj dobj = (PublicApplicationTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyIterator.next());
                this.publicApplicationTradeCertDobj = dobj;
                this.publicApplicationTradeCertDobj.setApplNo(seatAllotedDetails.getAppl_no());
                this.publicApplicationTradeCertDobj.setPurCd(seatAllotedDetails.getPur_cd() + "");
                if (TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER.equalsIgnoreCase(this.publicApplicationTradeCertDobj.getApplicantCategory())) {
                    setShowFinancer(true);
                } else {
                    setShowFinancer(false);
                }
            }
            this.addingBasicDetailsDisabled = true;

            if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_DEALER_APPL_VERIFY) {
                this.applicationInVerifyStage = true;
            }
        }

        this.publicApplicationTradeCertDobj.setNewRenewalTradeCert(applicationType);
        try {
            fillAllwheelerVehiclecatg_desclist();
            fillApplicantManufacturerList();
            fillAndSetShowRoomDistrict();
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", "Error occurred during filling of data from master tables[Hint: May be context XML]. {message[" + ve.getMessage() + "]}"));
        }
    }

    ////////////////////////////////////////////
    private void setValidityPanel(VerifyApproveTradeCertDobj dobj) {

        if (Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_NEW) {

            dobj.setValidFromDate(new Date());

            Calendar cal = Calendar.getInstance();
            cal.setTime(dobj.getValidFromDate());
            cal.add(Calendar.DATE, -1);
            cal.add(Calendar.YEAR, 1);
            dobj.setValidUpto(cal.getTime());


            dobj.setIssueDate(new Date());

        }

    }

    private void fillAndSetShowRoomDistrict() {
        this.fixedDistrictList.clear();
        this.fixedDistrictList.addAll(this.applicantDistrictList);
        try {
            int distCd = PublicApplicationTradeCertImpl.fetchDistrictCodeFromOfficeCd(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
            if (distCd != -1) {
                if (showFinancer) {
                    this.publicApplicationTradeCertDobj.setApplicantDistrict(String.valueOf(distCd));
                } else {
                    this.publicApplicationTradeCertDobj.setShowRoomDistrict(String.valueOf(distCd));
                }
            }
        } catch (VahanException ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", "Error occurred during fetching district code from database . {message[" + ve.getMessage() + "]}"));
        }

    }

    private void fillApplicationSectionsListVerifyForSelectedApplication(String selectedApplication, boolean addressToBeFull) {

        mapSectionsSrNoOfSelectedDealer.clear();
        ApplicationTradeCertImpl applicationTradeCertImpl = new ApplicationTradeCertImpl();
        try {
            applicationTradeCertImpl.getAllSrNoForOnlineApplication(selectedApplication, mapSectionsSrNoOfSelectedDealer, addressToBeFull);
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", "Error occurred during fetching of online trade certificate application data from database . {message[" + ex.getMessage() + "]}"));
        }

        if (!mapSectionsSrNoOfSelectedDealer.isEmpty()) {
            Map tempMap = new HashMap();
            for (Object keyObj : mapSectionsSrNoOfSelectedDealer.keySet()) {
                VerifyApproveTradeCertDobj verifyApproveTradeCertDobjFrom = (VerifyApproveTradeCertDobj) mapSectionsSrNoOfSelectedDealer.get(keyObj);
                verifyApproveTradeCertDobjFrom.setEmpCd(Long.valueOf(sessionVariables.getEmpCodeLoggedIn()));
                PublicApplicationTradeCertDobj dobj = new PublicApplicationTradeCertDobj();
                this.fillLoginDetails(dobj);
                if (verifyApproveTradeCertDobjFrom.getNewRenewalTradeCert().equalsIgnoreCase("N")) {
                    verifyApproveTradeCertDobjFrom.setNewRenewalTradeCert("NEW");
                    this.applicationType = "NEW";
                } else if (verifyApproveTradeCertDobjFrom.getNewRenewalTradeCert().equalsIgnoreCase("R")) {
                    verifyApproveTradeCertDobjFrom.setNewRenewalTradeCert("RENEW");
                    this.applicationType = "RENEW";
                } else if (verifyApproveTradeCertDobjFrom.getNewRenewalTradeCert().equalsIgnoreCase("D")) {
                    verifyApproveTradeCertDobjFrom.setNewRenewalTradeCert("DUPLICATE");
                    this.applicationType = "DUPLICATE";
                }
                setValidityPanel(verifyApproveTradeCertDobjFrom);
                mapDobj_From_VerifyAppDobj_To_PublicApplTCDobj(verifyApproveTradeCertDobjFrom, dobj);
                PublicApplicationTradeCertImpl publicApplicationTradeCertImpl = new PublicApplicationTradeCertImpl();
                try {
                    publicApplicationTradeCertImpl.fetchApplicantOtherDetailsFromMaster(dobj);
                } catch (VahanException ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", "Error occurred during fetching of online trade certificate application showroom and LOI data from database . {message[" + ex.getMessage() + "]}"));
                }
                tempMap.put(dobj.getSrNo(), dobj);
            }
            this.mapSectionsSrNoOfSelectedDealer.clear();
            this.mapSectionsSrNoOfSelectedDealer.putAll(tempMap);
        }
    }

    private void mapDobj_From_VerifyAppDobj_To_PublicApplTCDobj(VerifyApproveTradeCertDobj verifyApproveTradeCertDobj, PublicApplicationTradeCertDobj publicApplicationTradeCertDobj) {
        publicApplicationTradeCertDobj.setApplNo(verifyApproveTradeCertDobj.getApplNo());
        publicApplicationTradeCertDobj.setDealerFor(verifyApproveTradeCertDobj.getDealerFor());
        publicApplicationTradeCertDobj.setDealerName(verifyApproveTradeCertDobj.getDealerMasterDobj().getDealerName());
        publicApplicationTradeCertDobj.setDesigName(verifyApproveTradeCertDobj.getDesigName());
        publicApplicationTradeCertDobj.setEmpCd(verifyApproveTradeCertDobj.getEmpCd());
        publicApplicationTradeCertDobj.setEmpName(verifyApproveTradeCertDobj.getEmpName());
        publicApplicationTradeCertDobj.setExpired(verifyApproveTradeCertDobj.isExpired());
        publicApplicationTradeCertDobj.setNewRenewalTradeCert(verifyApproveTradeCertDobj.getNewRenewalTradeCert());
        publicApplicationTradeCertDobj.setNoOfAllowedVehicles(verifyApproveTradeCertDobj.getNoOfAllowedVehicles());
        publicApplicationTradeCertDobj.setOffCd(verifyApproveTradeCertDobj.getOffCd());
        publicApplicationTradeCertDobj.setSrNo(verifyApproveTradeCertDobj.getSrNo());
        publicApplicationTradeCertDobj.setStateCd(verifyApproveTradeCertDobj.getStateCd());
        publicApplicationTradeCertDobj.setStateName(verifyApproveTradeCertDobj.getStateName());
        publicApplicationTradeCertDobj.setTradeCertNo(verifyApproveTradeCertDobj.getTradeCertNo());
        publicApplicationTradeCertDobj.setValidFrom(verifyApproveTradeCertDobj.getValidFromDate());
        publicApplicationTradeCertDobj.setIssueDate(verifyApproveTradeCertDobj.getIssueDate());
        publicApplicationTradeCertDobj.setValidUpto(verifyApproveTradeCertDobj.getValidUpto());
        publicApplicationTradeCertDobj.setValidUptoDateString(verifyApproveTradeCertDobj.getValidUptoAsString());
        publicApplicationTradeCertDobj.setVehCatgFor(verifyApproveTradeCertDobj.getVehCatgFor());
        publicApplicationTradeCertDobj.setVehCatgName(verifyApproveTradeCertDobj.getVehCatgName());
        publicApplicationTradeCertDobj.setApplicantAddress1(verifyApproveTradeCertDobj.getDealerMasterDobj().getDealerAdd1());
        publicApplicationTradeCertDobj.setApplicantAddress2(verifyApproveTradeCertDobj.getDealerMasterDobj().getDealerAdd2());
        publicApplicationTradeCertDobj.setApplicantDistrict(verifyApproveTradeCertDobj.getDealerMasterDobj().getDealerDistrict() + "");
        publicApplicationTradeCertDobj.setApplicantPincode(verifyApproveTradeCertDobj.getDealerMasterDobj().getDealerPincode() + "");
        publicApplicationTradeCertDobj.setApplicantMailId(verifyApproveTradeCertDobj.getDealerMasterDobj().getEmailId());
        publicApplicationTradeCertDobj.setApplicantTinNo(verifyApproveTradeCertDobj.getDealerMasterDobj().getTin_NO());
        publicApplicationTradeCertDobj.setApplicantMobileNumber(verifyApproveTradeCertDobj.getDealerMasterDobj().getContactNo());
        publicApplicationTradeCertDobj.setApplicantName(verifyApproveTradeCertDobj.getDealerMasterDobj().getDealerName());
        publicApplicationTradeCertDobj.setApplicantCode(verifyApproveTradeCertDobj.getDealerFor());
        publicApplicationTradeCertDobj.setNoOfTradeCertificateReqFromApplicant(verifyApproveTradeCertDobj.getNoOfAllowedVehicles());
        publicApplicationTradeCertDobj.getVehClassAffiliatedByApplicantListForEx().clear();
        publicApplicationTradeCertDobj.getVehClassAffiliatedByApplicantListForEx().addAll(verifyApproveTradeCertDobj.getVehClassAffiliatedByApplicantListForEx());
        publicApplicationTradeCertDobj.getSelectedApplicantManufacturerListForEx().clear();
        publicApplicationTradeCertDobj.getSelectedApplicantManufacturerListForEx().addAll(verifyApproveTradeCertDobj.getSelectedApplicantManufacturerListForEx());
        publicApplicationTradeCertDobj.getSelectedManufacturerMapFromSession().clear();
        publicApplicationTradeCertDobj.getSelectedManufacturerMapFromSession().putAll(verifyApproveTradeCertDobj.getSelectedManufacturerMapFromSession());
        if (verifyApproveTradeCertDobj.getDealerMasterDobj().isIndividual()) {
            publicApplicationTradeCertDobj.setIndividualOrCompany("Individual");
        } else {
            publicApplicationTradeCertDobj.setIndividualOrCompany("Company");
        }

        publicApplicationTradeCertDobj.setApplicantRelation(verifyApproveTradeCertDobj.getDealerMasterDobj().getApplicantRelation());
        publicApplicationTradeCertDobj.setApplicantCategory(verifyApproveTradeCertDobj.getApplicantType());
        publicApplicationTradeCertDobj.setLandLineNumber(verifyApproveTradeCertDobj.getLandLineNo());
        publicApplicationTradeCertDobj.setApplicantBranchName(verifyApproveTradeCertDobj.getBranchName());
        publicApplicationTradeCertDobj.setExtraVehiclesSoldLastYr(verifyApproveTradeCertDobj.getExtraVehiclesSoldLastYr());
    }

    private void mapDobj_From_PublicApplTCDobj_To_VerifyAppDobj(PublicApplicationTradeCertDobj publicApplicationTradeCertDobj, VerifyApproveTradeCertDobj verifyApproveTradeCertDobj) {
        verifyApproveTradeCertDobj.setDealerMasterDobj(new DealerMasterDobj());
        verifyApproveTradeCertDobj.setApplNo(publicApplicationTradeCertDobj.getApplNo());
        verifyApproveTradeCertDobj.setDealerFor(publicApplicationTradeCertDobj.getDealerFor());
        verifyApproveTradeCertDobj.setDealerName(publicApplicationTradeCertDobj.getApplicantName());
        verifyApproveTradeCertDobj.getDealerMasterDobj().setDealerName(publicApplicationTradeCertDobj.getApplicantName());
        verifyApproveTradeCertDobj.setDesigName(publicApplicationTradeCertDobj.getDesigName());
        verifyApproveTradeCertDobj.setEmpCd(publicApplicationTradeCertDobj.getEmpCd());
        verifyApproveTradeCertDobj.setEmpName(publicApplicationTradeCertDobj.getEmpName());
        verifyApproveTradeCertDobj.setExpired(publicApplicationTradeCertDobj.isExpired());
        verifyApproveTradeCertDobj.setNewRenewalTradeCert(publicApplicationTradeCertDobj.getNewRenewalTradeCert());
        verifyApproveTradeCertDobj.setNoOfAllowedVehicles(publicApplicationTradeCertDobj.getNoOfAllowedVehicles());
        verifyApproveTradeCertDobj.setExtraVehiclesSoldLastYr(publicApplicationTradeCertDobj.getExtraVehiclesSoldLastYr());
        verifyApproveTradeCertDobj.setOffCd(publicApplicationTradeCertDobj.getOffCd());
        verifyApproveTradeCertDobj.setSrNo(publicApplicationTradeCertDobj.getSrNo());
        verifyApproveTradeCertDobj.setStateCd(publicApplicationTradeCertDobj.getStateCd());
        verifyApproveTradeCertDobj.setStateName(publicApplicationTradeCertDobj.getStateName());
        verifyApproveTradeCertDobj.setTradeCertNo(publicApplicationTradeCertDobj.getTradeCertNo());
        verifyApproveTradeCertDobj.setValidUpto(publicApplicationTradeCertDobj.getValidUpto());
        verifyApproveTradeCertDobj.setValidUptoAsString(publicApplicationTradeCertDobj.getValidUptoDateString());
        verifyApproveTradeCertDobj.setVehCatgFor(publicApplicationTradeCertDobj.getVehCatgFor());
        verifyApproveTradeCertDobj.setVehCatgName(publicApplicationTradeCertDobj.getVehCatgName());
        verifyApproveTradeCertDobj.getDealerMasterDobj().setDealerAdd1(publicApplicationTradeCertDobj.getApplicantAddress1());
        verifyApproveTradeCertDobj.getDealerMasterDobj().setDealerAdd2(publicApplicationTradeCertDobj.getApplicantAddress2());
        verifyApproveTradeCertDobj.getDealerMasterDobj().setDealerDistrict(Integer.valueOf(publicApplicationTradeCertDobj.getApplicantDistrict()));
        verifyApproveTradeCertDobj.getDealerMasterDobj().setDealerPincode(Integer.valueOf(publicApplicationTradeCertDobj.getApplicantPincode()));
        verifyApproveTradeCertDobj.getDealerMasterDobj().setEmailId(publicApplicationTradeCertDobj.getApplicantMailId());
        verifyApproveTradeCertDobj.getDealerMasterDobj().setTin_NO(publicApplicationTradeCertDobj.getApplicantTinNo());
        verifyApproveTradeCertDobj.getDealerMasterDobj().setContactNo(publicApplicationTradeCertDobj.getApplicantMobileNumber());
        verifyApproveTradeCertDobj.setDealerFor(publicApplicationTradeCertDobj.getApplicantCode());
        verifyApproveTradeCertDobj.setNoOfAllowedVehicles(publicApplicationTradeCertDobj.getNoOfTradeCertificateReqFromApplicant());
        verifyApproveTradeCertDobj.getVehClassAffiliatedByApplicantListForEx().clear();
        verifyApproveTradeCertDobj.getVehClassAffiliatedByApplicantListForEx().addAll(publicApplicationTradeCertDobj.getVehClassAffiliatedByApplicantListForEx());
        verifyApproveTradeCertDobj.getSelectedApplicantManufacturerListForEx().clear();
        verifyApproveTradeCertDobj.getSelectedApplicantManufacturerListForEx().addAll(publicApplicationTradeCertDobj.getSelectedApplicantManufacturerListForEx());
        verifyApproveTradeCertDobj.getSelectedManufacturerMapFromSession().clear();
        verifyApproveTradeCertDobj.getSelectedManufacturerMapFromSession().putAll(publicApplicationTradeCertDobj.getSelectedManufacturerMapFromSession());
        if ("Individual".equalsIgnoreCase(publicApplicationTradeCertDobj.getIndividualOrCompany())) {
            verifyApproveTradeCertDobj.getDealerMasterDobj().setIndividual(true);
        } else {
            verifyApproveTradeCertDobj.getDealerMasterDobj().setIndividual(false);
        }

        verifyApproveTradeCertDobj.getDealerMasterDobj().setApplicantRelation(publicApplicationTradeCertDobj.getApplicantRelation());
        verifyApproveTradeCertDobj.setApplicantType(publicApplicationTradeCertDobj.getApplicantCategory());
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        verifyApproveTradeCertDobj.setPurCd(publicApplicationTradeCertDobj.getPurCd());
        verifyApproveTradeCertDobj.setEmpCd((Long) sessionMap.get("emp_cd"));
        verifyApproveTradeCertDobj.setOffCd(Util.getUserSeatOffCode());
        verifyApproveTradeCertDobj.setStateCd((String) sessionMap.get("state_cd"));
        verifyApproveTradeCertDobj.setStateName((String) sessionMap.get("state_name"));
        verifyApproveTradeCertDobj.setEmpName((String) sessionMap.get("emp_name"));
        verifyApproveTradeCertDobj.setDesigName((String) sessionMap.get("desig_name"));
        verifyApproveTradeCertDobj.setOfficeName(ServerUtil.getOfficeName(verifyApproveTradeCertDobj.getOffCd(), verifyApproveTradeCertDobj.getStateCd()));
        verifyApproveTradeCertDobj.setLandLineNo(publicApplicationTradeCertDobj.getLandLineNumber());
        verifyApproveTradeCertDobj.setBranchName(publicApplicationTradeCertDobj.getApplicantBranchName());

    }

    //Note:Please use Utils for Session Details
    private void fillLoginDetails(PublicApplicationTradeCertDobj dobj) {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        String purCd = "";
        if (this.applicationType != null && (this.applicationType.equals("DUPLICATE"))) {
            purCd = String.valueOf(TableConstants.VM_TRANSACTION_TRADE_CERT_DUP);

        } else {
            purCd = String.valueOf(TableConstants.VM_TRANSACTION_TRADE_CERT_NEW);
        }
        dobj.setPurCd(purCd);
        dobj.setEmpCd((Long) sessionMap.get("emp_cd"));
        dobj.setOffCd(Util.getUserSeatOffCode());
        dobj.setStateCd((String) sessionMap.get("state_cd"));
        dobj.setStateName((String) sessionMap.get("state_name"));
        dobj.setEmpName((String) sessionMap.get("emp_name"));
        dobj.setDesigName((String) sessionMap.get("desig_name"));
        dobj.setOfficeName(ServerUtil.getOfficeName(dobj.getOffCd(), dobj.getStateCd()));
    }

    public String back() {
        return CommonUtils.isNullOrBlank(this.applicationNo) ? "" : "seatwork";
    }

    public void officeListener(AjaxBehaviorEvent abe) {
        this.publicApplicationTradeCertDobj.getApplicantOffice();
    }

    public void districtSelectListener(AjaxBehaviorEvent event) {
        try {
            applicantOfficeList.clear();
            applicantOfficeList.addAll(PublicApplicationTradeCertImpl.getOfficeAsPerSelectedStateAndDistrict(publicApplicationTradeCertDobj.getApplicantState(), publicApplicationTradeCertDobj.getApplicantDistrict()));
        } catch (VahanException ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", "Error occurred while getting offices for selected state and district. {message[" + ve.getMessage() + "]}"));
        }
        this.publicApplicationTradeCertDobj.setApplicantOffice("-1");  ///-SELECT-
    }

    public int getNoOfVehGrandTotal() {
        return noOfVehGrandTotal;
    }

    public boolean isVisibleApplicationDetailPanel() {
        return visibleApplicationDetailPanel;
    }

    public FacesMessage[] validateApplicantDetailsAlongWithTCDetails(PublicApplicationTradeCertDobj publicApplicationTradeCertDobj) {
        FacesMessage[] facesMessages = new FacesMessage[50];
        boolean notValid = false;
        Set uniqueMessageSet = new HashSet();
        if (FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            if (Utility.isNullOrBlank(publicApplicationTradeCertDobj.getApplicantName())) {
                uniqueMessageSet.add("! Please enter field 'Name Of Applicant' for whom trade certificate is to be issued.");
                notValid = true;
            }
            if (publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER) && Utility.isNullOrBlank(publicApplicationTradeCertDobj.getShowRoomName())) {
                uniqueMessageSet.add("! Please enter field 'Name Of Show Room' for which trade certificate is to be issued.");
                notValid = true;
            }
            if (publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER) && Utility.isNullOrBlank(publicApplicationTradeCertDobj.getApplicantBranchName())) {
                uniqueMessageSet.add("! Please enter field 'Branch Name' for which trade certificate is to be issued.");
                notValid = true;
            }
            if (!company) {
                if (publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER) && Utility.isNullOrBlank(publicApplicationTradeCertDobj.getApplicantRelation())) {
                    uniqueMessageSet.add("! Please enter field 'Son/Wife/Daughter Of' for which trade certificate is to be issued.");
                    notValid = true;
                }
            }
            if (Utility.isNullOrBlank(publicApplicationTradeCertDobj.getApplicantAddress1())) {
                uniqueMessageSet.add("! Please enter field 'Address Of Applicant' for which trade certificate is to be issued.");
                notValid = true;
            }
            if (publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER) && Utility.isNullOrBlank(publicApplicationTradeCertDobj.getShowRoomAddress1())) {
                uniqueMessageSet.add("! Please enter field 'Address Of Show Room' for which trade certificate is to be issued.");
                notValid = true;
            }

            if (Utility.isNullOrBlank(publicApplicationTradeCertDobj.getApplicantState())) {
                JSFUtils.setFacesMessage("! Please select field 'State Of Applicant' for which trade certificate is to be issued.", null, JSFUtils.ERROR);
                notValid = true;
            } else {
                if (publicApplicationTradeCertDobj.getApplicantState().equalsIgnoreCase("-SELECT-")) {
                    JSFUtils.setFacesMessage("! Please select field 'State Of Applicant' field from where trade certificate is to be issued.", null, JSFUtils.ERROR);
                    notValid = true;
                }
            }

            if (Utility.isNullOrBlank(publicApplicationTradeCertDobj.getApplicantPincode())) {
                uniqueMessageSet.add("! Please enter field 'Pincode Of Applicant' for which trade certificate is to be issued.");
                notValid = true;
            } else {
                if (Long.valueOf(publicApplicationTradeCertDobj.getApplicantPincode()) == 0L) {
                    JSFUtils.setFacesMessage("! Zero ( 0 ) is not acceptable in field 'Pincode Of Applicant'.", null, JSFUtils.ERROR);
                    notValid = true;
                }
            }
            if (publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER) && Utility.isNullOrBlank(publicApplicationTradeCertDobj.getShowRoomPincode())) {
                uniqueMessageSet.add("! Please enter field 'Pincode Of Show Room' for which trade certificate is to be issued.");
                notValid = true;
            } else {
                if (publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER) && Long.valueOf(publicApplicationTradeCertDobj.getApplicantPincode()) == 0L) {
                    JSFUtils.setFacesMessage("! Zero ( 0 ) is not acceptable in field 'Pincode Of Show Room'.", null, JSFUtils.ERROR);
                    notValid = true;
                }
            }
            if (Utility.isNullOrBlank(publicApplicationTradeCertDobj.getApplicantMobileNumber())) {
                uniqueMessageSet.add("! Please enter field 'Mobile Number Of Applicant' for which trade certificate is to be issued.");
                notValid = true;
            } else {
                if (Long.valueOf(publicApplicationTradeCertDobj.getApplicantMobileNumber()) == 0L) {
                    JSFUtils.setFacesMessage("! Zero ( 0 ) is not acceptable in field 'Mobile Number Of Applicant'.", null, JSFUtils.ERROR);
                    notValid = true;
                }
            }

            if (publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER) && Utility.isNullOrBlank(publicApplicationTradeCertDobj.getLandLineNumber())) {
                uniqueMessageSet.add("! Please enter field 'Landline Number Of Applicant' for which trade certificate is to be issued.");
                notValid = true;
            } else {
                if (publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER) && Long.valueOf(publicApplicationTradeCertDobj.getLandLineNumber()) == 0L) {
                    JSFUtils.setFacesMessage("! Zero ( 0 ) is not acceptable in field 'Landline Number Of Applicant'.", null, JSFUtils.ERROR);
                    notValid = true;
                }
            }

//            if (Utility.isNullOrBlank(publicApplicationTradeCertDobj.getApplicantMailId())) {
//                uniqueMessageSet.add("! Please enter field 'Email Of Applicant' for which trade certificate is to be issued.");
//                notValid = true;
//            } else {
            if (publicApplicationTradeCertDobj.getApplicantMailId() != null && !publicApplicationTradeCertDobj.getApplicantMailId().equals("")) {
                matcher = pattern.matcher(publicApplicationTradeCertDobj.getApplicantMailId());
                if (!matcher.matches()) {
                    JSFUtils.setFacesMessage("! Please enter valid mail-Id in field 'Email Of Applicant' field for which trade certificate is to be issued.", null, JSFUtils.ERROR);
                    notValid = true;
                }
            }
//            }

            if (Utility.isNullOrBlank(publicApplicationTradeCertDobj.getApplicantOffice())) {
                JSFUtils.setFacesMessage("! Please select field 'Regional Transport Office For Registration Of Applicant' from where trade certificate is to be issued.", null, JSFUtils.ERROR);
                notValid = true;
            } else {
                if (Integer.valueOf(publicApplicationTradeCertDobj.getApplicantOffice()) == -1) {
                    JSFUtils.setFacesMessage("! Please select field 'Regional Transport Office For Registration Of Applicant' from where trade certificate is to be issued.", null, JSFUtils.ERROR);
                    notValid = true;
                }
            }

            if (publicApplicationTradeCertDobj.getVehClassAffiliatedByApplicantList().isEmpty()) {
                JSFUtils.setFacesMessage("! Please select field 'Category Of Motor Vehicle(s) In Which Authorized To Deal'.", null, JSFUtils.ERROR);
                notValid = true;
            }

            if (publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER) && publicApplicationTradeCertDobj.getSelectedApplicantManufacturerList().isEmpty()) {
                JSFUtils.setFacesMessage("! Please select field 'Manufacturers'.", null, JSFUtils.ERROR);
                notValid = true;
            }
            if (publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER) && verifyTinNo(publicApplicationTradeCertDobj.getApplicantTinNo(), publicApplicationTradeCertDobj.getApplicantState(), publicApplicationTradeCertDobj.getApplicantOffice(), uniqueMessageSet)) {
                notValid = true;
            }
            if (Utility.isNullOrBlank(String.valueOf(publicApplicationTradeCertDobj.getNoOfTradeCertificateReqFromApplicant()))) {
                uniqueMessageSet.add("! Please enter field 'Number Of Trade Certificates Required'.");
                notValid = true;
            } else {
                if (Integer.valueOf(publicApplicationTradeCertDobj.getNoOfTradeCertificateReqFromApplicant()) == 0) {
                    JSFUtils.setFacesMessage("! Zero ( 0 ) is not acceptable in field 'Number Of Trade Certificates Required'.", null, JSFUtils.ERROR);
                    notValid = true;
                }
            }

            if (publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER) && this.applicationType.equalsIgnoreCase("NEW") && Utility.isNullOrBlank(String.valueOf(publicApplicationTradeCertDobj.getLOIAuthorisationNo()))) {
                uniqueMessageSet.add("! Please enter field 'LOI Authorisation No'.");
                notValid = true;
            }


            if (publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER) && this.applicationType.equalsIgnoreCase("NEW") && publicApplicationTradeCertDobj.getLOIAuthorisationDate() == null) {
                uniqueMessageSet.add("! Please enter field 'LOI Authorisation Date'.");
                notValid = true;
            }

            if (this.applicationType.equalsIgnoreCase("RENEW") && CommonUtils.isNullOrBlank(this.publicApplicationTradeCertDobj.getExtraVehiclesSoldLastYr())) {
                uniqueMessageSet.add("! Please enter field 'No of vehicle sold/financed last year'.");
                notValid = true;
            }

        }
        if (!notValid
                && uniqueMessageSet.isEmpty()
                && FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            return new FacesMessage[0];
        } else {
            for (Object messageStringObj : uniqueMessageSet) {
                String messageString = (String) messageStringObj;
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageString, messageString);
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            uniqueMessageSet.clear();
            return facesMessages;
        }
    }

    public boolean verifyTinNo(String tinNo, String stateCd, String offCd, Set uniqueMessageSet) {
        boolean tinNumberExist = false;
        if (!CommonUtils.isNullOrBlank(tinNo)) {
            if (tinNo.length() != 15) {
                uniqueMessageSet.add("! The length of value in field 'GSTIN' is less than required length which is 15, Please enter another value of length 15.");
            }
//            else {
//                if (!stateCd.equalsIgnoreCase("-SELECT-")) {
//                    if (Integer.valueOf(offCd) != -1) {
//                        try {
//                            tinNumberExist = PublicApplicationTradeCertImpl.validateUniqueTinNo(tinNo, stateCd, offCd);
//                        } catch (VahanException ex) {
//                            java.util.logging.Logger.getLogger(PublicApplicationTradeCertBean.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                        if (tinNumberExist && this.applicationType.equals("NEW")) {
//                            uniqueMessageSet.add("! Value in field 'GSTIN' already exist, Please enter another value.");
//                        } else {  ///// for RENEW & DUPLICATE
//                            return false;
//                        }
//                    }
//                }
//            }
        }
//        else {
//            tinNumberExist = true;
//            uniqueMessageSet.add("! Please enter field 'GSTIN' for which trade certificate is to be issued.");
//        }
        return tinNumberExist;
    }

    public void verifyTinNoOnBlur(AjaxBehaviorEvent abe) {
        boolean tinNumberExist = false;
        String tinNo = this.publicApplicationTradeCertDobj.getApplicantTinNo();
        String stateCd = this.publicApplicationTradeCertDobj.getApplicantState();
        String offCd = this.publicApplicationTradeCertDobj.getApplicantOffice();
        if (!stateCd.equalsIgnoreCase("-SELECT-")) {
            if (Integer.valueOf(offCd) != -1) {
                if (!tinNo.equalsIgnoreCase("_______________")) {
                    if (tinNo.contains("_")) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", "! The length of value in field 'GSTIN' is less than required length which is 15, Please enter another value of length 15."));
                    }
//                    else {
//                        try {
//                            tinNumberExist = PublicApplicationTradeCertImpl.validateUniqueTinNo(tinNo, stateCd, offCd);
//                            if (tinNumberExist) {
//                                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", "! Value in field 'GSTIN' already exist, Please enter another value."));
//                            }
//                        } catch (VahanException ex) {
//                            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
//                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", "! Exception occured while fetching 'GSTIN' number from database."));
//                        }
//                    }
                }
//                else {
//                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", "! Please enter field 'GSTIN' for which trade certificate is to be issued."));
//                }
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", "! Please select field 'Regional Transport Office For Registration Of Applicant' from where trade certificate is to be issued."));
            }
        } else {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", "! Please select field 'State Of Applicant' field from where trade certificate is to be issued."));
        }
    }

    public void displayBasicDetailsConfirmationBox() {
        this.validationNotSuccessful = true;
        this.publicApplicationTradeCertDobj.setNewRenewalTradeCert(applicationType);
        formatInputAccordingToDBSpecification();
        validateApplicantDetailsAlongWithTCDetails(this.publicApplicationTradeCertDobj);
        if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            PrimeFaces.current().executeScript("PF('saveBasicDtlsConfirmationPopupTC').show();");
            return;
        } else {
            if (applicationExistForSameApplicantAndVehicleClass()) {
                JSFUtils.setFacesMessage("! Error in saving application as another application already exist for the same combination (applicant and vehicle class).", null, JSFUtils.ERROR);
                PrimeFaces.current().executeScript("PF('saveBasicDtlsConfirmationPopupTC').show();");
                return;
            }
            this.validationNotSuccessful = false;
        }

        JSFUtils.setFacesMessage("! Following below are the applicant basic details which will be saved :- ", null, JSFUtils.INFO);
        JSFUtils.setFacesMessage("Applicant Name::  [" + this.publicApplicationTradeCertDobj.getApplicantName() + "]", null, JSFUtils.INFO);
        if (this.publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)) {
            if (company) {
                JSFUtils.setFacesMessage("Is Company :: [YES]", null, JSFUtils.INFO);
            } else {
                JSFUtils.setFacesMessage("Is Individual :: [YES]", null, JSFUtils.INFO);
            }
        }
        if (!Utility.isNullOrBlank(publicApplicationTradeCertDobj.getApplicantRelation()) && this.publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)) {
            JSFUtils.setFacesMessage("Son/Wife/Daughter Of: :: [" + this.publicApplicationTradeCertDobj.getApplicantRelation() + "]", null, JSFUtils.INFO);
        }
        String districtSelected = "";
        if (!CommonUtils.isNullOrBlank(publicApplicationTradeCertDobj.getApplicantDistrict()) && !publicApplicationTradeCertDobj.getApplicantDistrict().equals("-1")) {
            districtSelected = " , " + PublicApplicationTradeCertImpl.getDistrictNameFromCode(this.publicApplicationTradeCertDobj.getApplicantState(), this.publicApplicationTradeCertDobj.getApplicantDistrict()).toUpperCase();
        }
        JSFUtils.setFacesMessage("Address :: [" + this.publicApplicationTradeCertDobj.getApplicantAddress1() + " , " + this.publicApplicationTradeCertDobj.getApplicantAddress2() + districtSelected + "]", null, JSFUtils.INFO);
        JSFUtils.setFacesMessage("State :: [" + this.publicApplicationTradeCertDobj.getStateName().toUpperCase() + "]", null, JSFUtils.INFO);
        JSFUtils.setFacesMessage("Pincode :: [" + this.publicApplicationTradeCertDobj.getApplicantPincode() + "]", null, JSFUtils.INFO);
        JSFUtils.setFacesMessage("Mobile Number :: [" + this.publicApplicationTradeCertDobj.getApplicantMobileNumber() + "]", null, JSFUtils.INFO);
        if (!CommonUtils.isNullOrBlank(this.publicApplicationTradeCertDobj.getApplicantMailId())) {
            JSFUtils.setFacesMessage("Email :: [" + this.publicApplicationTradeCertDobj.getApplicantMailId() + "]", null, JSFUtils.INFO);
        }
        if (this.publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER)) {
            JSFUtils.setFacesMessage("Branch Name :: [" + this.publicApplicationTradeCertDobj.getApplicantBranchName() + "]", null, JSFUtils.INFO);
            JSFUtils.setFacesMessage("Landline Number :: [" + this.publicApplicationTradeCertDobj.getLandLineNumber() + "]", null, JSFUtils.INFO);
        }

        if (this.publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)) {
            JSFUtils.setFacesMessage("Show Room Name::  [" + this.publicApplicationTradeCertDobj.getShowRoomName() + "]", null, JSFUtils.INFO);
        }
        String showRoomDistrictSelected = "";
        if (!CommonUtils.isNullOrBlank(publicApplicationTradeCertDobj.getShowRoomDistrict()) && !publicApplicationTradeCertDobj.getShowRoomDistrict().equals("-1")) {
            showRoomDistrictSelected = " , " + PublicApplicationTradeCertImpl.getDistrictNameFromCode(this.publicApplicationTradeCertDobj.getApplicantState(), this.publicApplicationTradeCertDobj.getShowRoomDistrict()).toUpperCase();
        }
        if (this.publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)) {
            JSFUtils.setFacesMessage("Show Room Address :: [" + this.publicApplicationTradeCertDobj.getShowRoomAddress1() + " , " + this.publicApplicationTradeCertDobj.getShowRoomAddress2() + showRoomDistrictSelected + "]", null, JSFUtils.INFO);
        }
        JSFUtils.setFacesMessage("Office :: [" + ServerUtil.getOfficeName(this.publicApplicationTradeCertDobj.getOffCd(), this.publicApplicationTradeCertDobj.getStateCd()) + "]", null, JSFUtils.INFO);
        if (!CommonUtils.isNullOrBlank(this.publicApplicationTradeCertDobj.getApplicantTinNo())) {
            JSFUtils.setFacesMessage("GSTIN :: [" + this.publicApplicationTradeCertDobj.getApplicantTinNo() + "]", null, JSFUtils.INFO);
        }
        if (this.publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)) {
            JSFUtils.setFacesMessage("Affliated Manufacturers Which Certify Applicant To Deal:-", null, JSFUtils.INFO);

            StringBuilder list = new StringBuilder();
            list.append("Manufacturer(s) :: { ");
            for (Object selectedManufacturerObj : this.publicApplicationTradeCertDobj.getSelectedApplicantManufacturerList()) {
                String manufacturerName = (String) selectedManufacturerObj;
                if (this.publicApplicationTradeCertDobj.getSelectedManufacturerMapFromSession().get(manufacturerName.trim()) != null) {
                    manufacturerName = this.publicApplicationTradeCertDobj.getSelectedManufacturerMapFromSession().get(manufacturerName.trim()).toString();
                }
                for (Object selectManufobj : applicantManufacturerList) {
                    SelectItem objItem = (SelectItem) selectManufobj;
                    if (objItem.getValue().toString().trim().equalsIgnoreCase(manufacturerName)) {
                        list.append(objItem.getLabel().toString()).append(", ");
                        break;
                    }
                }
            }

            JSFUtils.setFacesMessage(list.substring(0, list.lastIndexOf(",")) + " }", null, JSFUtils.INFO);
        }


        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        if (this.publicApplicationTradeCertDobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)) {
            if (this.publicApplicationTradeCertDobj.getLOIAuthorisationNo() != null && !this.publicApplicationTradeCertDobj.getLOIAuthorisationNo().equals("")) {
                JSFUtils.setFacesMessage("LOI Authorisation No :: [" + this.publicApplicationTradeCertDobj.getLOIAuthorisationNo() + "]", null, JSFUtils.INFO);
            }
            if (this.publicApplicationTradeCertDobj.getLOIAuthorisationDate() != null) {
                JSFUtils.setFacesMessage("LOI Authorisation Date :: [" + sdf.format(this.publicApplicationTradeCertDobj.getLOIAuthorisationDate()) + "]", null, JSFUtils.INFO);
            }
        }

        JSFUtils.setFacesMessage("", null, JSFUtils.INFO);

        JSFUtils.setFacesMessage("! Following below are the trade certificate details which will be saved :- ", null, JSFUtils.INFO);

        for (Object set : this.getVehClassList()) {
            javax.faces.model.SelectItem obj = (javax.faces.model.SelectItem) set;
            if (obj.getValue().toString().trim().equalsIgnoreCase(this.publicApplicationTradeCertDobj.getVehClassAffiliatedByApplicantList().get(0).toString().trim())) {
                JSFUtils.setFacesMessage("Vehicle Class For Which Trade Certificate Is Applied ::  { " + obj.getLabel().toString().trim() + " }", null, JSFUtils.INFO);
                break;
            }
        }

        JSFUtils.setFacesMessage("Number Of Trade Certificates Applied :: [" + this.publicApplicationTradeCertDobj.getNoOfTradeCertificateReqFromApplicant() + "]", null, JSFUtils.INFO);

        if (this.applicationType.equalsIgnoreCase("RENEW")) {
            JSFUtils.setFacesMessage("Number Of Vehicles sold/financed last year :: [" + this.publicApplicationTradeCertDobj.getExtraVehiclesSoldLastYr() + "]", null, JSFUtils.INFO);
        }

        PrimeFaces.current().executeScript("PF('saveBasicDtlsConfirmationPopupTC').show();");
    }

    private boolean applicationExistForSameApplicantAndVehicleClass() {
        try {
            return PublicApplicationTradeCertImpl.applcationPendingForApplicantAndVehicleClass(this.publicApplicationTradeCertDobj);
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", "Error occurred while checking whether another application exists for same applicant and vehicle class."));
            return false;
        }
    }

///////////////////////////////////////////////////////////////////////////////// SAVE ////////////////////////////    
    public void saveDetailsInTemporatyApplication() throws Exception {
        addNewSectionsToApplication();
        disableFieldforexist = true;
        this.visibleApplicationDetailPanel = true;
    }

    public void addNewSectionsToApplication() {
        try {
            addBasicDetailsToApplication();
        } catch (CloneNotSupportedException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", "Error in Cloning PublicApplicationTradeCertDobj Object. {message[" + ex.getMessage() + "]}"));
        } catch (VahanException ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", "Error occurred while saving basic details in the application. {message[" + ve.getMessage() + "]}"));
        }
    }

    private boolean addBasicDetailsToApplication() throws VahanException, CloneNotSupportedException {
        boolean isSaved = false;
        if (saveApplicantTCDtls()) {
            isSaved = true;
        }
        return isSaved;
    }

    public boolean saveApplicantTCDtls() throws VahanException, CloneNotSupportedException {
        boolean isSaved = false;
        this.publicApplicationTradeCertDobj.setSrNo(1 + "");
        this.publicApplicationTradeCertDobj.setStatus("P");
        String vehClassApplied = this.publicApplicationTradeCertDobj.getVehClassAffiliatedByApplicantList().get(0).toString();
        if (!this.applicationType.equals("NEW")) {
            vehClassApplied = this.publicApplicationTradeCertDobj.getSelectedVehClassMapFromSession().get(vehClassApplied).toString();
        }
        this.publicApplicationTradeCertDobj.setVehClassAppliedFromApplicant(vehClassApplied);

        this.publicApplicationTradeCertDobj.setNewRenewalTradeCert(applicationType);
        formatInputAccordingToDBSpecification();
        String saveReturn = "FAILURE";
        if (applicationType.equalsIgnoreCase("NEW")) {
            saveReturn = new PublicApplicationTradeCertImpl().saveBasicDetails(this.publicApplicationTradeCertDobj, true);
        } else {
            saveReturn = new PublicApplicationTradeCertImpl().saveBasicDetails(this.publicApplicationTradeCertDobj, false);
        }

        this.basicDetailsSaved = true;

        if (saveReturn.contains("SUCCESS")) {

            applicationNo = saveReturn.substring(saveReturn.lastIndexOf(":") + 1);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Application '" + applicationNo + "' successfully submitted for inspection.", "Application '" + applicationNo + "' successfully submitted for inspection.");
            FacesContext.getCurrentInstance().addMessage(null, message);

            PrimeFaces.current().ajax().update("new_trade_cert_application_subview:confirmationPopup");
            PrimeFaces.current().executeScript("PF('confDlgTradeCert').show()");

            isSaved = true;
        }

        return isSaved;
    }

    public void applicantVehClassAffiliatedSelectionListener(AjaxBehaviorEvent ajaxBehaviorEvent) {
        applicantVehClassAffiliatedSelectionListenerCode();
    }

    public void singleSelectionImplementationListener(ValueChangeEvent event) {
        List vehClassAffliatedOldValue = (List) event.getOldValue();
        List vehClassAffliatedNewValue = (List) event.getNewValue();
        if (vehClassAffliatedNewValue.size() > 1) {
            vehClassAffliatedNewValue.remove(vehClassAffliatedOldValue.get(0));
        }
        publicApplicationTradeCertDobj.getVehClassAffiliatedByApplicantList().clear();
        publicApplicationTradeCertDobj.getVehClassAffiliatedByApplicantList().addAll(vehClassAffliatedNewValue);

    }

    private void applicantVehClassAffiliatedSelectionListenerCode() {
        if (publicApplicationTradeCertDobj.getVehClassAffiliatedByApplicantList().size() > 0) {
            List vehClassForTC = new ArrayList();
            this.vehClassAffliatedFinalSet.clear();
            for (int i = 0; i < vehClassList.size(); i++) {
                javax.faces.model.SelectItem obj = (javax.faces.model.SelectItem) vehClassList.get(i);
                for (int j = 0; j < publicApplicationTradeCertDobj.getVehClassAffiliatedByApplicantList().size(); j++) {
                    if (obj.getValue().toString().equalsIgnoreCase(publicApplicationTradeCertDobj.getVehClassAffiliatedByApplicantList().get(j).toString())) {
                        if ("Edit_Application".equals(applicationModeRadio)
                                || (this.applicationType.equals("RENEW") || this.applicationType.equals("DUPLICATE"))) {
                            publicApplicationTradeCertDobj.getVehClassAffiliatedByApplicantListForEx().add(obj.getLabel().toString());
                        }
                        break;
                    }
                }
            }

            this.affliatedVehicleClassSelectedSet.clear();
            this.affliatedVehicleClassSelectedSet.addAll(vehClassForTC);
        } else {
            this.affliatedVehicleClassSelectedSet.clear();
        }

        if ("Edit_Application".equals(applicationModeRadio) || (this.applicationType.equals("RENEW") || this.applicationType.equals("DUPLICATE"))) {
            for (Object selectedManufacturerObj : this.publicApplicationTradeCertDobj.getSelectedApplicantManufacturerList()) {
                String manufacturerName = (String) selectedManufacturerObj;
                for (Object selectManufobj : applicantManufacturerList) {
                    SelectItem objItem = (SelectItem) selectManufobj;
                    if (objItem.getValue().toString().trim().equalsIgnoreCase(manufacturerName.trim())) {
                        this.publicApplicationTradeCertDobj.getSelectedApplicantManufacturerListForEx().add(objItem.getLabel().toString());
                        break;
                    }
                }
            }
        }
    }

    public PublicApplicationTradeCertDobj getPublicApplicationTradeCertDobj() {
        return publicApplicationTradeCertDobj;
    }

    public void setPublicApplicationTradeCertDobj(PublicApplicationTradeCertDobj publicApplicationTradeCertDobj) {
        this.publicApplicationTradeCertDobj = publicApplicationTradeCertDobj;
    }

    public List getVehClassList() {
        return vehClassList;
    }

    public void relationFormModificationListener(AjaxBehaviorEvent ajaxBehaviorEvent) {
        if (publicApplicationTradeCertDobj.getIndividualOrCompany().equalsIgnoreCase("Company")) {
            company = true;
        } else {
            company = false;
        }
        publicApplicationTradeCertDobj.setApplicantRelation("");
    }

    private void codeForModifySection2Section3Listener() {
        try {
            applicantDistrictList.clear();
            if ("Generate_Application".equals(applicationModeRadio)) {
                this.publicApplicationTradeCertDobj.setApplicantDistrict("-1");
            }

            List districts = PublicApplicationTradeCertImpl.getDistrictData(publicApplicationTradeCertDobj.getApplicantState());
            for (Object district : districts) {
                SelectItem districtItem = (SelectItem) district;
                applicantDistrictList.add(districtItem);
            }

        } catch (VahanException ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            LOGGER.error("PublicApplicationTradeCertBean.codeForModifySection2Section3Listener:: EXCEPTION Message:::  " + ve.getMessage() + ", EXCEPTION Cause::: " + ve.getCause());
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Application generation status... ", "Exception occured while setting office for trade certificate application. {message::[" + ve.getMessage() + "]}"));
        }
    }

    public List getApplicantDistrictList() {
        return applicantDistrictList;
    }

    public List getApplicantOfficeList() {
        return applicantOfficeList;
    }

    public boolean isCompany() {
        return company;
    }

    private void fillApplicantManufacturerList() throws VahanException {
        try {
            applicantManufacturerList.clear();
            String[][] data = PublicApplicationTradeCertImpl.masterTables.VM_MAKER.getData();
            for (int i = 0, itemCount = 1; i < data.length; i++, itemCount++) {
                applicantManufacturerList.add(new SelectItem(data[i][0], " [" + itemCount + "] " + (data[i][1]).toUpperCase()));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(ex.getMessage());
        }
    }

    public List<SelectItem> getApplicantManufacturerList() {
        return applicantManufacturerList;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public Set getAffliatedVehicleClassSelectedSet() {
        return affliatedVehicleClassSelectedSet;
    }

    public boolean isAddingBasicDetailsDisabled() {
        return addingBasicDetailsDisabled;
    }

    public boolean isValidationNotSuccessful() {
        return validationNotSuccessful;
    }

    public boolean isDissableState() {
        return dissableState;
    }

    private void formatInputAccordingToDBSpecification() {

        this.publicApplicationTradeCertDobj.setApplicantPincode(this.publicApplicationTradeCertDobj.getApplicantPincode().replaceAll(" ", ""));
        if (this.publicApplicationTradeCertDobj.getShowRoomPincode() != null) {
            this.publicApplicationTradeCertDobj.setShowRoomPincode(this.publicApplicationTradeCertDobj.getShowRoomPincode().replaceAll(" ", ""));
        }

        this.publicApplicationTradeCertDobj.setApplicantMobileNumber(this.publicApplicationTradeCertDobj.getApplicantMobileNumber().replaceAll("\\(", ""));
        this.publicApplicationTradeCertDobj.setApplicantMobileNumber(this.publicApplicationTradeCertDobj.getApplicantMobileNumber().replaceAll("\\)", ""));
        this.publicApplicationTradeCertDobj.setApplicantMobileNumber(this.publicApplicationTradeCertDobj.getApplicantMobileNumber().replaceAll("-", ""));
        this.publicApplicationTradeCertDobj.setApplicantMobileNumber(this.publicApplicationTradeCertDobj.getApplicantMobileNumber().replaceAll(" ", ""));

        if (this.publicApplicationTradeCertDobj.getLandLineNumber() != null) {
            this.publicApplicationTradeCertDobj.setLandLineNumber(this.publicApplicationTradeCertDobj.getLandLineNumber().replaceAll("\\(", ""));
            this.publicApplicationTradeCertDobj.setLandLineNumber(this.publicApplicationTradeCertDobj.getLandLineNumber().replaceAll("\\)", ""));
            this.publicApplicationTradeCertDobj.setLandLineNumber(this.publicApplicationTradeCertDobj.getLandLineNumber().replaceAll("-", ""));
            this.publicApplicationTradeCertDobj.setLandLineNumber(this.publicApplicationTradeCertDobj.getLandLineNumber().replaceAll(" ", ""));
        }

        if (this.publicApplicationTradeCertDobj.getNewRenewalTradeCert() != null) {
            if (this.publicApplicationTradeCertDobj.getNewRenewalTradeCert().equalsIgnoreCase("NEW")) {
                this.publicApplicationTradeCertDobj.setNewRenewalTradeCert("N");
            } else if (this.publicApplicationTradeCertDobj.getNewRenewalTradeCert().equalsIgnoreCase("RENEW")) {
                this.publicApplicationTradeCertDobj.setNewRenewalTradeCert("R");
            } else if (this.publicApplicationTradeCertDobj.getNewRenewalTradeCert().equalsIgnoreCase("DUPLICATE")) {
                this.publicApplicationTradeCertDobj.setNewRenewalTradeCert("D");
            }
        }
    }

    /**
     * @return the disableField
     */
    public boolean isDisableField() {
        return disableField;
    }

    /**
     * @param disableField the disableField to set
     */
    public void setDisableField(boolean disableField) {
        this.disableField = disableField;
    }

    /**
     * @return the renderTradePanel
     */
    public boolean isRenderTradePanel() {
        return renderTradePanel;
    }

    /**
     * @param renderTradePanel the renderTradePanel to set
     */
    public void setRenderTradePanel(boolean renderTradePanel) {
        this.renderTradePanel = renderTradePanel;
    }

    public void fillAllwheelerVehiclecatg_desclist() throws VahanException {
        try {
            vehClassList.clear();
            if (this.publicApplicationTradeCertDobj != null && this.publicApplicationTradeCertDobj.getVehClassAffiliatedByApplicantList() != null) {
                this.publicApplicationTradeCertDobj.getVehClassAffiliatedByApplicantList().clear();
                String[][] data = PublicApplicationTradeCertImpl.masterTables.VM_VCH_CLASS.getData();
                if (data != null) {
                    for (int i = 0, itemCount = 1; i < data.length; i++, itemCount++) {
                        vehClassList.add(new SelectItem(data[i][0], "[" + itemCount + "]" + (data[i][1]).toUpperCase()));
                    }
                    publicApplicationTradeCertDobj.getSelectedApplicantManufacturerList().clear();
                    publicApplicationTradeCertDobj.getVehClassAffiliatedByApplicantList().clear();
                }
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(ex.getMessage());
        }
    }

    /**
     * @return the applicationModeRadio
     */
    public String getApplicationModeRadio() {
        return applicationModeRadio;
    }

    /**
     * @param applicationModeRadio the applicationModeRadio to set
     */
    public void setApplicationModeRadio(String applicationModeRadio) {
        this.applicationModeRadio = applicationModeRadio;
    }

    public boolean isDisableFieldforexist() {
        return disableFieldforexist;
    }

    public void setDisableFieldforexist(boolean disableFieldforexist) {
        this.disableFieldforexist = disableFieldforexist;
    }

    public void showFinancerOrDealerListener(AjaxBehaviorEvent e) {
        String applicantCategory = this.publicApplicationTradeCertDobj.getApplicantCategory();

        if (TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER.equalsIgnoreCase(this.publicApplicationTradeCertDobj.getApplicantCategory())) {
            setShowFinancer(true);
        } else {
            setShowFinancer(false);
        }
        this.publicApplicationTradeCertDobj.reset();
        this.publicApplicationTradeCertDobj.setApplicantCategory(applicantCategory);

        this.publicApplicationTradeCertDobj.setApplicantState(sessionVariables.getStateCodeSelected());
        this.publicApplicationTradeCertDobj.setStateCd(sessionVariables.getStateCodeSelected());
        this.publicApplicationTradeCertDobj.setApplicantOffice(sessionVariables.getOffCodeSelected() + "");
        this.publicApplicationTradeCertDobj.setOffCd(sessionVariables.getOffCodeSelected());
        this.publicApplicationTradeCertDobj.setEmpCd(Long.valueOf(sessionVariables.getEmpCodeLoggedIn()));
        fillLoginDetails(this.publicApplicationTradeCertDobj);
        codeForModifySection2Section3Listener();

        fillAndSetShowRoomDistrict();
    }

    public void showTradeCertEntryDailog(AjaxBehaviorEvent event) {
        this.publicApplicationTradeCertDobj.setTradeCertNo("");
        if (!this.applicationType.equalsIgnoreCase("NEW")) {
            if (this.applicationType.equalsIgnoreCase("RENEW")) {
                this.applicationTypeRadio = "Renew_Application";
            } else if (this.applicationType.equalsIgnoreCase("DUPLICATE")) {
                this.applicationTypeRadio = "Duplicate_Application";
            }
            this.hideApplicantType = true;
            PrimeFaces.current().executeScript("PF('trade_cert_no_entry_Dlg').show();");
        } else {
            this.publicApplicationTradeCertDobj = new PublicApplicationTradeCertDobj();
            this.publicApplicationTradeCertDobj.setApplicantCategory(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER);
            this.hideApplicantType = false;
            setDisableFieldforexist(false);
            init();
        }
    }

    ///////////////////////////////////////////// //////////////////////////////////
    /**
     * @return the showFinancer
     */
    public boolean isShowFinancer() {
        return showFinancer;
    }

    /**
     * @param showFinancer the showFinancer to set
     */
    public void setShowFinancer(boolean showFinancer) {
        this.showFinancer = showFinancer;
    }

    public boolean isRenderFileMovement() {
        return renderFileMovement;
    }

    public void setRenderFileMovement(boolean renderFileMovement) {
        this.renderFileMovement = renderFileMovement;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getApplicationFlowSrNo() {
        return applicationFlowSrNo;
    }

    @Override
    public String save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String saveAndMoveFile() {
        String returnLocation = "";
        String fileMoveReturn = "";

        if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_DEALER_APPL_INSPECT
                || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_DEALER_APPL_VERIFY
                || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_DEALER_APPL_APPROVE) {

            try {
                Status_dobj status = new Status_dobj();
                status.setAppl_dt(appl_details.getAppl_dt());
                status.setAppl_no(appl_details.getAppl_no());
                status.setPur_cd(appl_details.getPur_cd());
                status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
                status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
                status.setStatus(getApp_disapp_dobj().getNew_status());
                status.setCurrent_role(appl_details.getCurrent_role());
                status.setEmp_cd(Long.valueOf(sessionVariables.getEmpCodeLoggedIn()));

                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                        || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                    ApplicationTradeCertImpl applicationTradeCertImpl = new ApplicationTradeCertImpl();

                    if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                        status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                    }
                    if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_TRADE_CERT_DEALER_APPL_APPROVE) {
                        List<VerifyApproveTradeCertDobj> dobjList = new ArrayList<>();
                        VerifyApproveTradeCertDobj verifyApproveTradeCertDobj = new VerifyApproveTradeCertDobj();
                        mapDobj_From_PublicApplTCDobj_To_VerifyAppDobj(this.publicApplicationTradeCertDobj, verifyApproveTradeCertDobj);
                        dobjList.add(verifyApproveTradeCertDobj);
                        fileMoveReturn = applicationTradeCertImpl.grantTradeCertificate(dobjList, status);
                        this.applicationNo = status.getAppl_no();
                    } else {
                        applicationTradeCertImpl.fileMoveToIssueTradeCertificate(status);
                        returnLocation = "seatwork";
                    }

                    if (fileMoveReturn.contains("SUCCESS")) {
                        String applNo = fileMoveReturn.substring(fileMoveReturn.lastIndexOf(":") + 1);
                        postSaveOperation();
                        FacesMessage message = null;
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Trade Certificate(s) successfully generated after collection of the fee for the submitted trade certificate application.", "Trade Certificate(s) successfully generated after collection of the fee for the submitted trade certificate application.");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                    }

                }
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_NOT_RECOMMEND)) {
                    ApplicationTradeCertImpl.disposeApplicationForOdisha(status, this.publicApplicationTradeCertDobj.getDealerFor());
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Trade certificate application [No:" + status.getAppl_no() + "] successfully disposed and applicant data deleted from temporary table.", "Trade certificate application [No:" + status.getAppl_no() + "] successfully disposed and applicant data deleted from temporary table.");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    this.applicationNo = status.getAppl_no();
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

    private void postSaveOperation() {
        setDisableSaveButton(true);
    }

    public void confirmTradeCertApplicationPrint() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("tcApplNo", this.publicApplicationTradeCertDobj.getApplNo());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("tcApplMapOfSelectedDealer", mapSectionsSrNoOfSelectedDealer);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("applicantType", this.publicApplicationTradeCertDobj.getApplicantCategory());
            //RequestContext ca = RequestContext.getCurrentInstance();
            PrimeFaces.current().executeScript("PF('printTradeCertificateApplication').show()");
        } catch (Exception ex) {
            LOGGER.error(ex);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Exception occured while setting values in session. [info:{" + ex.getMessage() + "}}]"));
        }
    }

    public String printTradeCertApplicationDetails() {
        //RequestContext rc = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('printTradeCertificateApplication').hide()");
        return "PrintTradeCertificateApplication";
    }

    public void fetchTCDetails() {
        PublicApplicationTradeCertDobj dobj = null;
        try {
            publicApplicationTradeCertDobj.setTradeCertNo(publicApplicationTradeCertDobj.getTradeCertNo().toUpperCase().trim());

            dobj = PublicApplicationTradeCertImpl.getTradeApplicationDataFromTradeCertificateNumber(publicApplicationTradeCertDobj.getTradeCertNo());

            if (dobj != null && dobj.isExpired() && "Duplicate_Application".equalsIgnoreCase(this.applicationTypeRadio)) {
                JSFUtils.showMessage("! Trade certificate has already expired, please enter another T.C Number.");
                return;
            } else if (dobj != null && !dobj.isExpired() && "Renew_Application".equalsIgnoreCase(this.applicationTypeRadio)) {
                JSFUtils.showMessage("! Trade certificate has not yet expired, please enter another T.C Number.");
                return;
            }
        } catch (VahanException ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", "Error occurred while fetching trade certificate details. {message[" + ve.getMessage() + "]}"));
        }
        if (dobj != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            if (dobj.getLOIAuthorisationDate() != null && publicApplicationTradeCertDobj.getApplicantCategory().equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)) {
                dobj.setLOIAuthorisationDateString(sdf.format(dobj.getLOIAuthorisationDate()));
            }
            fillLoginDetails(dobj);
            this.publicApplicationTradeCertDobj = dobj;
            try {

                if (dobj.getApplicantCategory().equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER)) {
                    setShowFinancer(true);
                } else {
                    setShowFinancer(false);
                }
                this.disableFieldforexist = true;

                PrimeFaces.current().executeScript("PF('trade_cert_no_entry_Dlg').hide();");
            } catch (Exception ve) {
                LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application generation status...", "Error occurred while generating OTP and messaging it. {message[" + ve.getMessage() + "]}"));

                setDisableFieldforexist(true);
            }
        } else {
            JSFUtils.showMessage("! Record not found for the entered trade certificate number, please enter another one.");
        }
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return this.compBeanList;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ApproveImpl getApproveImpl() {
        return approveImpl;
    }

    public void setApproveImpl(ApproveImpl approveImpl) {
        this.approveImpl = approveImpl;
    }

    public boolean isVerifyMode() {
        return verifyMode;
    }

    public boolean isBasicDetailsSaved() {
        return basicDetailsSaved;
    }

    public boolean isApplicationInVerifyStage() {
        return applicationInVerifyStage;
    }

    public void setApplicationInVerifyStage(boolean applicationInVerifyStage) {
        this.applicationInVerifyStage = applicationInVerifyStage;
    }

    public List getFixedDistrictList() {
        return fixedDistrictList;
    }

    public boolean isHideApplicantType() {
        return hideApplicantType;
    }

    public void setHideApplicantType(boolean hideApplicantType) {
        this.hideApplicantType = hideApplicantType;
    }
}
