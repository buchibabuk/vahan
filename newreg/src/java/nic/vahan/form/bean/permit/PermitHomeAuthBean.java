/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitHomeAuthDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.AITPStateCoveredDobj;
import nic.vahan.form.dobj.permit.AITPStateFeeDraftDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.permit.PermitDetailImpl;
import nic.vahan.form.impl.permit.PermitHomeAuthImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.form.impl.permit.PrintPermitDocInXhtmlImpl;
import nic.vahan.form.impl.permit.SurrenderPermitImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Administrator
 */
@ManagedBean
@ViewScoped
public class PermitHomeAuthBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(PermitHomeAuthBean.class);
    @ManagedProperty(value = "#{Permit_Dtls_bean}")
    private PermitDetailBean permit_Dtls_bean;
    private String authNo = "";
    private String regnNo = "";
    private String applNo = "";
    private Date authFrom;
    private Date authUpto;
    private Date taxFrom;
    private Date taxUpto;
    private String header;
    private int actionCd = 0;
    private int pmtType = 0;
    private int pmtCatg = 0;
    private PermitHomeAuthImpl impl = null;
    private PermitHomeAuthDobj dobj = null;
    PermitDetailDobj pmtDobj = null;
    PermitDetailImpl pmtImpl = null;
    private String appNoMsg = "";
    private boolean renderFlage = false;
    private boolean appDisappPanel = false;
    private List<ComparisonBean> compBeanList = new ArrayList<ComparisonBean>();
    private List<ComparisonBean> prevChangedDataList;
    private List<PermitHomeAuthDobj> oldAuthDetails = null;
    private boolean previousAuthDtlsRender = false;
    private boolean authFromDisable = false;
    private boolean authUptoDisable = false;
    private boolean disableNpDetails;
    String prvFeeDtls = null;
    @ManagedProperty(value = "#{pmtDtlsBean}")
    private PermitCheckDetailsBean pmtCheckDtsl;
    private List arrayInsCmpy = new ArrayList();
    private List arrayInsType = new ArrayList();
    private PermitOwnerDetailDobj ownDobj = null;
    private PermitOwnerDetailImpl ownImpl = null;
    private boolean agreeToProceed = false;
    private String mainPermitValidUpto;
    @ManagedProperty(value = "#{paidFeeDtls}")
    private PermitPaidFeeDtlsBean paidFeeDtls;
    private Map<String, String> stateConfigMap = null;
    private boolean renderPeriod = false;
    private String auth_periodMode;
    private String auth_period;
    private List period = new ArrayList();
    private boolean disablePeriod = false;
    private boolean setValidityAndProceed = false;
    @ManagedProperty(value = "#{StateCovered}")
    private AITPStateCoveredBean statecoveredBean;
    private boolean render_payment_table_aitp;
    PassengerPermitDetailImpl passImp = null;
    private boolean pending_auth = false;
    private boolean enable_home_auth_on_verify = false;
    private boolean exempt_echallan = false;

    public PermitHomeAuthBean() {
        //getAuthFrom().setMindate(new Date());
        //getAuthFrom().setMaxdate(new Date());
        String[][] data = MasterTableFiller.masterTables.VM_ICCODE.getData();
        for (int i = 0; i < data.length; i++) {
            arrayInsCmpy.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_INSTYP.getData();
        for (int i = 0; i < data.length; i++) {
            arrayInsType.add(new SelectItem(data[i][0], data[i][1]));
        }
        stateConfigMap = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
        render_payment_table_aitp = Boolean.parseBoolean(stateConfigMap.get("render_payment_table_aitp"));
        enable_home_auth_on_verify = Boolean.parseBoolean(stateConfigMap.get("homeauth_renew_enable_verify"));
        exempt_echallan = Boolean.parseBoolean(stateConfigMap.get("exempt_echallan"));
    }

    @PostConstruct
    public void init() {
        permit_Dtls_bean.permitComponentReadOnly(true);
        actionCd = appl_details.getCurrent_action_cd();
        if (actionCd == TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_ENTRY) {
            header = "Renewal of State Home Authorization";
            appDisappPanel = true;
            previousAuthDtlsRender = true;
        } else if (actionCd == TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_VERIFICATION) {
            header = "Renewal of State Home Authorization Verification";
            renderFlage = true;
            get_Details_For_Appl_no();
            appDisappPanel = false;
            previousAuthDtlsRender = false;
            if (enable_home_auth_on_verify) {
                authFromDisable = false;
                authUptoDisable = false;
            } else {
                authFromDisable = true;
                authUptoDisable = true;
            }
        } else if (actionCd == TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_APPROVAL) {
            header = "Renewal of State Home Authorization Approval";
            renderFlage = true;
            get_Details_For_Appl_no();
            appDisappPanel = false;
            previousAuthDtlsRender = false;
            authFromDisable = true;
            authUptoDisable = true;
        }
    }

    public void get_Details() {
        InsDobj insDobj = null;
        FacesMessage message = null;
        String regn_no = "";
        boolean authDtlsFoundInNP = false;
        boolean multiPmtAllow = false;
        ownImpl = new PermitOwnerDetailImpl();
        try {
            SurrenderPermitImpl surrImpl = new SurrenderPermitImpl();
            impl = new PermitHomeAuthImpl();
            multiPmtAllow = Boolean.parseBoolean(stateConfigMap.get("multi_pmt_allow_on_veh"));
            String surrMsg = surrImpl.permitSurrMsg(getRegnNo().trim().toUpperCase(), multiPmtAllow);
            if (!CommonUtils.isNullOrBlank(getRegnNo())) {
                prvFeeDtls = impl.getLastSubmittedFee(Util.getUserStateCode(), getRegnNo().trim().toUpperCase());
            }
            if (!CommonUtils.isNullOrBlank(surrMsg)) {
                showApplPendingDialogBox(surrMsg);
                return;
            }
            String str = ServerUtil.applicationStatusForPermit(getRegnNo().trim().toUpperCase(), Util.getUserStateCode());
            if (!CommonUtils.isNullOrBlank(str)) {
                showApplPendingDialogBox(str);
                return;
            }
            regn_no = getRegnNo().toUpperCase();
            if (multiPmtAllow) {
                pmtDobj = PermitDetailImpl.getPermitdetails(CommonPermitPrintImpl.getPmtNoFromVtPermitForAuth(regn_no, multiPmtAllow));
            } else {
                pmtDobj = PermitDetailImpl.getPermitdetails(CommonPermitPrintImpl.getPmtNoThroughVtPermit(regn_no));
            }

            if (pmtDobj == null) {
                JSFUtils.showMessagesInDialog("Error", "Permit details not found", FacesMessage.SEVERITY_ERROR);
                return;
            }
            if (!(CommonPermitPrintImpl.getPermitAuthAllowed(Util.getUserStateCode(), pmtDobj.getPmt_type(), pmtDobj.getPmt_catg()))) {
                JSFUtils.showMessagesInDialog("Information", "This permit not contain the authorization. So please enter valid number", FacesMessage.SEVERITY_INFO);
                return;
            }

//Getting NAtional Permit Authorision Details: Begin
            if (CommonPermitPrintImpl.getPermitAuthAllowed(Util.getUserStateCode(), pmtDobj.getPmt_type(), pmtDobj.getPmt_catg()) && !(CommonUtils.isNullOrBlank(regn_no) || ("").equalsIgnoreCase(regn_no))) {
                if (pmtDobj.getPmt_type() == Integer.parseInt(TableConstants.NATIONAL_PERMIT)) {
                    permit_Dtls_bean.setNationalPermitAuthDetails(regn_no.toUpperCase().trim());
                    setDisableNpDetails(true);
                }
            } else {
                setDisableNpDetails(false);
            }
//Getting NAtional Permit Authorision Details: End
            if (pmtDobj.getPmt_type() == Integer.parseInt(TableConstants.AITP)) {
                VehicleParameters vchparameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                vchparameters.setPERMIT_TYPE(pmtDobj.getPmt_type());
                vchparameters.setPUR_CD(TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD);
                if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("show_period_on_aitp_auth"), vchparameters), "get_Details")) {
                    renderPeriod = true;
                }
            }

            VehicleParameters vchparameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            vchparameters.setPERMIT_TYPE(pmtDobj.getPmt_type());
            if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("auth_recursive_fee"), vchparameters), "get_Details")) {
                pending_auth = true;
            }

            dobj = impl.getPermitDetails(regn_no, pmtDobj.getPmt_type(), pmtDobj.getPmt_catg());
            if (dobj == null) {
                dobj = impl.getPermitAuthDetailsThrowNP(regn_no, pmtDobj.getPmt_type());
                authDtlsFoundInNP = true;
            }
            ownDobj = ownImpl.set_Owner_appl_db_to_dobj(regn_no.toUpperCase(), null);
            if (ownDobj == null) {
                throw new VahanException("Owner details not found. Please contact to system administrator");
            }
            Map<String, String> OffAllotResult = PermitDetailImpl.getOffAllotmentResult(Util.getUserStateCode(), Util.getUserOffCode(), ownDobj.getOff_cd());
            if (("false").equalsIgnoreCase(OffAllotResult.get("offAllowed"))) {
                JSFUtils.showMessagesInDialog("Information", "Authorisation  Renew from permit issuing authority Only", FacesMessage.SEVERITY_ERROR);
                return;
            }
            boolean home_auth_allowed_flag = CommonPermitPrintImpl.getPermitAuthAllowed(Util.getUserStateCode(), pmtDobj.getPmt_type(), pmtDobj.getPmt_catg());
            pmtCheckDtsl.getAlldetails(ownDobj.getRegn_no().toUpperCase(), insDobj, ownDobj.getState_cd(), ownDobj.getOff_cd());
            if (exempt_echallan) {
                pmtCheckDtsl.getDtlsDobj().setExemptedChallan(true);
            }
            oldAuthDetails = impl.getOldPermitAuthDetails(oldAuthDetails, pmtDobj.getRegn_no());
            if (pmtDobj.getPmt_type() == Integer.parseInt(TableConstants.AITP)) {
                if (dobj != null && pmtDobj != null && dobj.getPrvAuthUpto() != null) {
                    if (dobj.getPrvAuthUpto().getTime() < new Date().getTime()) {
                        permit_Dtls_bean.set_Permit_Dtls_dobj_to_bean(pmtDobj);
                        setTaxFrom(dobj.getTaxFrom());
                        setTaxUpto(dobj.getTaxUpto());
                        setAuthNo(dobj.getAuthNo());
                        setPmtType(pmtDobj.getPmt_type());
                        pmtCatg = pmtDobj.getPmt_catg();
                        if (Boolean.parseBoolean(stateConfigMap.get("auth_valid_from_prv_date_after_expire"))) {
                            setAuthFrom(ServerUtil.dateRange(dobj.getPrvAuthUpto(), 0, 0, 1));
                            setAuthFromDisable(true);
                            setAuthUptoDisable(true);
                        } else {
                            setAuthFrom(ServerUtil.dateRange(new Date(), 0, 0, 0));
                        }
                        if (authDtlsFoundInNP) {
                            setAuthFrom(dobj.getAuthFrom());
                        }
                        if ("UP".contains(Util.getUserStateCode())) {
                            setAuthUpto(ServerUtil.dateRange(dobj.getPrvAuthUpto(), 1, 0, -1));
                            setAuthUptoDisable(true);
                        } else if (authDtlsFoundInNP) {
                            setAuthUpto(dobj.getAuthUpto());
                        } else {
                            setAuthUpto(ServerUtil.dateRange(getAuthFrom(), 1, 0, -1));
                        }
                        if (pending_auth) {
                            setAuthUpto(ServerUtil.dateRange(dobj.getPrvAuthUpto(), 1, 0, 0));
                            while (getAuthUpto().compareTo(new Date()) <= 0) {
                                authUpto = DateUtils.addToDate(getAuthUpto(), DateUtils.YEAR, 1);
                            }
                            setAuthUptoDisable(true);
                        }
                        if (pmtDobj.getPmt_type() == Integer.parseInt(TableConstants.AITP)) {
                            if (pmtDobj.getValid_upto().getTime() < getAuthUpto().getTime()) {
                                PrimeFaces.current().ajax().update("pmtValidityBeforeAuthId");
                                PrimeFaces.current().executeScript("PF('pmtValidityBeforeAuth').show();");
                            }
                        }

                    } else if (impl.takeRenewalOfHomeAuthAfter(Util.getUserStateCode(), pmtDobj.getRegn_no(), pmtDobj.getPmt_type())) {
                        permit_Dtls_bean.set_Permit_Dtls_dobj_to_bean(pmtDobj);
                        setTaxFrom(dobj.getTaxFrom());
                        setTaxUpto(dobj.getTaxUpto());
                        setAuthNo(dobj.getAuthNo());
                        setPmtType(pmtDobj.getPmt_type());
                        pmtCatg = pmtDobj.getPmt_catg();
                        setAuthFrom(ServerUtil.dateRange(dobj.getPrvAuthUpto(), 0, 0, 1));
                        setAuthUpto(ServerUtil.dateRange(getAuthFrom(), 1, 0, -1));
                        authFromDisable = true;
                    } else {
                        oldAuthDetails.clear();
                        JSFUtils.showMessagesInDialog("Information", "Home Authorization validity not Expired. Authorization Valid Upto :- " + CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(dobj.getPrvAuthUpto()), FacesMessage.SEVERITY_INFO);
                    }
                } else {
                    oldAuthDetails.clear();
                    message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Vehicle details not found", "Vehicle details not found");
                    if (dobj == null) {
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Previous authorization details not found", "Previous authorization details not found");
                    }
                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                }
            } else if (pmtDobj.getPmt_type() == Integer.parseInt(TableConstants.NATIONAL_PERMIT) || home_auth_allowed_flag) {
                if (dobj != null && pmtDobj != null && dobj.getPrvAuthUpto() != null) {
                    if (dobj.getPrvAuthUpto().getTime() < new Date().getTime()) {
                        permit_Dtls_bean.set_Permit_Dtls_dobj_to_bean(pmtDobj);
                        setTaxFrom(dobj.getTaxFrom());
                        setTaxUpto(dobj.getTaxUpto());
                        setAuthNo(dobj.getAuthNo());
                        setPmtType(pmtDobj.getPmt_type());
                        pmtCatg = pmtDobj.getPmt_catg();
                        if (Boolean.parseBoolean(stateConfigMap.get("auth_valid_from_prv_date_after_expire"))) {
                            setAuthFrom(ServerUtil.dateRange(dobj.getPrvAuthUpto(), 0, 0, 1));
                            setAuthFromDisable(true);
                            setAuthUptoDisable(true);
                        } else {
                            setAuthFrom(ServerUtil.dateRange(new Date(), 0, 0, 0));
                        }
                        if (authDtlsFoundInNP) {
                            setAuthFrom(dobj.getAuthFrom());
                        }
                        if ("DL,UP,GJ".contains(Util.getUserStateCode())) {
                            setAuthUpto(ServerUtil.dateRange(dobj.getPrvAuthUpto(), 1, 0, -1));
                            setAuthUptoDisable(true);
                        } else if (authDtlsFoundInNP) {
                            setAuthUpto(dobj.getAuthUpto());
                        } else {
                            setAuthUpto(ServerUtil.dateRange(getAuthFrom(), 1, 0, -1));
                        }
                        if (pending_auth) {
                            setAuthUpto(ServerUtil.dateRange(dobj.getPrvAuthUpto(), 1, 0, 0));
                            while (getAuthUpto().compareTo(new Date()) <= 0) {
                                authUpto = DateUtils.addToDate(getAuthUpto(), DateUtils.YEAR, 1);
                            }
                            setAuthUptoDisable(true);
                        }
                        if (pmtDobj.getPmt_type() == Integer.parseInt(TableConstants.NATIONAL_PERMIT)) {
                            if (pmtDobj.getValid_upto().getTime() < getAuthUpto().getTime()) {
                                PrimeFaces.current().ajax().update("pmtValidityBeforeAuthId");
                                PrimeFaces.current().executeScript("PF('pmtValidityBeforeAuth').show();");
                            }
                        }
                    } else if (impl.takeRenewalOfHomeAuthAfter(Util.getUserStateCode(), pmtDobj.getRegn_no(), pmtDobj.getPmt_type())) {
                        permit_Dtls_bean.set_Permit_Dtls_dobj_to_bean(pmtDobj);
                        setTaxFrom(dobj.getTaxFrom());
                        setTaxUpto(dobj.getTaxUpto());
                        setAuthNo(dobj.getAuthNo());
                        setPmtType(pmtDobj.getPmt_type());
                        pmtCatg = pmtDobj.getPmt_catg();
                        setAuthFrom(ServerUtil.dateRange(dobj.getPrvAuthUpto(), 0, 0, 1));
                        setAuthUpto(ServerUtil.dateRange(getAuthFrom(), 1, 0, -1));
                        authFromDisable = true;
                    } else {
                        oldAuthDetails.clear();
                        JSFUtils.showMessagesInDialog("Information", "Home Authorization validity not Expired. Authorization Valid Upto :- " + CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(dobj.getPrvAuthUpto()), FacesMessage.SEVERITY_INFO);
                    }
                } else {
                    oldAuthDetails.clear();
                    message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Vehicle details not found", "Vehicle details not found");
                    if (dobj == null) {
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Previous authorization details not found", "Previous authorization details not found");
                    }
                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                }
            }
        } catch (VahanException e) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
        } catch (Exception ex) {
            LOGGER.error("RegnNo - " + regn_no + "/" + ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void get_Details_For_Appl_no() {
        FacesMessage message = null;
        try {
            InsDobj insDobj = null;
            ownImpl = new PermitOwnerDetailImpl();
            impl = new PermitHomeAuthImpl();
            String appl_no = appl_details.getAppl_no();
            dobj = impl.select_IN_VA_HOME_AUTH(appl_no);
            paidFeeDtls.getListOfPaidPmtFee(appl_no);
            boolean multiPmtAllow = Boolean.parseBoolean(stateConfigMap.get("multi_pmt_allow_on_veh"));
            if (multiPmtAllow && dobj != null) {
                pmtDobj = PermitDetailImpl.getPermitdetails(dobj.getPmtNo());
            } else {
                pmtDobj = PermitDetailImpl.getPermitdetails(CommonPermitPrintImpl.getPmtNoThroughVtPermit(dobj.getRegnNo()));
            }
            if (dobj != null && pmtDobj != null) {
                permit_Dtls_bean.set_Permit_Dtls_dobj_to_bean(pmtDobj);
                setTaxFrom(dobj.getTaxFrom());
                setTaxUpto(dobj.getTaxUpto());
                setAuthNo(dobj.getAuthNo());
                setAuthFrom(dobj.getAuthFrom());
                setAuthUpto(dobj.getAuthUpto());
                setApplNo(dobj.getApplNo());
                setPmtType(pmtDobj.getPmt_type());
                setRegnNo(dobj.getRegnNo());
                if (getPmtType() == Integer.parseInt(TableConstants.AITP)) {
                    VehicleParameters vchparameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                    vchparameters.setPERMIT_TYPE(pmtDobj.getPmt_type());
                    vchparameters.setPUR_CD(TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD);
                    if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("show_period_on_aitp_auth"), vchparameters), "get_Details")) {
                        renderPeriod = true;
                    }
                    if (renderPeriod) {
                        String[] perid = impl.getAuthPeriod(appl_no);
                        setAuth_periodMode(perid[0]);
                        setAuth_period(perid[1]);
                        check_Time_Period();
                        setDisablePeriod(true);
                    }
                }
                pmtCatg = pmtDobj.getPmt_catg();
                ownDobj = ownImpl.set_Owner_appl_db_to_dobj(dobj.getRegnNo().toUpperCase(), null);
                if (ownDobj == null) {
                    throw new VahanException("Owner deatils not in active stage. Please contact to system administrater.");
                }
                pmtCheckDtsl.getAlldetails(dobj.getRegnNo(), insDobj, ownDobj.getState_cd(), ownDobj.getOff_cd());
                if (exempt_echallan) {
                    pmtCheckDtsl.getDtlsDobj().setExemptedChallan(true);
                }
                //fieldDisable(true);
                if (CommonPermitPrintImpl.getPermitAuthAllowed(Util.getUserStateCode(), pmtDobj.getPmt_type(), pmtDobj.getPmt_catg()) && !(CommonUtils.isNullOrBlank(dobj.getRegnNo()))) {
                    if (pmtDobj.getPmt_type() == Integer.parseInt(TableConstants.NATIONAL_PERMIT)) {
                        permit_Dtls_bean.setNationalPermitAuthDetails(dobj.getRegnNo());
                        setDisableNpDetails(true);
//                        if (Util.getUserStateCode().equalsIgnoreCase("DL") && actionCd == TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_VERIFICATION) {
//                            PermitHomeAuthDobj auth_dobj_permit = ServerUtil.getTemporaryNPDetails(appl_details.getRegn_no(), Util.getUserStateCode(), appl_details.getAppl_no());
//                            if (auth_dobj_permit != null) {
//                                PermitHomeAuthDobj auth_dobj_np = ServerUtil.getPermitDetailsFromNp(appl_details.getRegn_no());
//                                if (auth_dobj_np != null) {
//                                    if (!(auth_dobj_permit.getAuthFrom().equals(auth_dobj_np.getAuthFrom())
//                                            && auth_dobj_permit.getAuthUpto().equals(auth_dobj_np.getAuthUpto()))) {
//                                       PrimeFaces.current().executeScript("PF('npfees').show();");
//                                        PrimeFaces.current().ajax().update("abcd");
//                                    }
//                                }
//                            }
//                        }
                    } else {
                        setDisableNpDetails(false);
                    }
                } else {
                    setDisableNpDetails(false);
                }

                if (isRender_payment_table_aitp() && getPmtType() == Integer.parseInt(TableConstants.AITP)) {
                    statecoveredBean.setPaymentList(new PassengerPermitDetailImpl().getAitpPaymentDetails(appl_no));
                    String regionCovered = "";
                    List<AITPStateFeeDraftDobj> list = statecoveredBean.getPaymentList();
                    for (AITPStateFeeDraftDobj dobj : list) {
                        regionCovered += dobj.getPay_state_cd() + ",";
                    }
                    List<AITPStateCoveredDobj> stateList = new ArrayList<>();
                    if (!CommonUtils.isNullOrBlank(regionCovered)) {
                        String[] aitpSelState = regionCovered.split(",");
                        if (aitpSelState.length > 0) {
                            String[][] data = MasterTableFiller.masterTables.TM_STATE.getData();
                            for (int j = 0; j < aitpSelState.length; j++) {
                                for (int i = 0; i < data.length; i++) {
                                    if ((aitpSelState[j].trim()).equalsIgnoreCase(data[i][0])) {
                                        AITPStateCoveredDobj statecoveredBeanObj = new AITPStateCoveredDobj();
                                        statecoveredBeanObj.setStateCd(data[i][0]);
                                        statecoveredBeanObj.setStateName(data[i][1]);
                                        stateList.add(statecoveredBeanObj);
                                        statecoveredBean.setStateList(stateList);
                                    }
                                }
                            }
                        }
                    }
                }

            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Vehicle details not found", "Vehicle details not found");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            }
        } catch (VahanException e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
        }
    }

//    public void fieldDisable(boolean flage) {
//        getTaxFrom().setDisabled(flage);
//        getTaxUpto().setDisabled(flage);
//        getAuthNo().setDisabled(flage);
//        getAuthFrom().setDisabled(flage);
//        getAuthUpto().setDisabled(flage);
//        getApplNo().setDisabled(flage);
//    }
    public void checkValidityBeforeSave() {
        Map<String, String> confige = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
        int pDay = 0;
        if (pmtDobj != null && pmtDobj.getPmt_type() == Integer.valueOf(TableConstants.GOODS_PERMIT)
                && ("HP").contains(Util.getUserStateCode())) {
            pmtDobj.setPmt_type(Integer.valueOf(TableConstants.NATIONAL_PERMIT));
        }
        if (pmtDobj != null && pmtDobj.getPmt_type() == Integer.valueOf(TableConstants.AITP)) {
            pDay = Integer.valueOf(confige.get("ai_renew_after_days"));
        } else if (pmtDobj != null && pmtDobj.getPmt_type() == Integer.valueOf(TableConstants.NATIONAL_PERMIT)) {
            pDay = Integer.valueOf(confige.get("np_renew_after_days"));
        }
        if (pmtDobj != null && ServerUtil.dateRange(new Date(), 0, 0, pDay).getTime() >= pmtDobj.getValid_upto().getTime()) {
            setMainPermitValidUpto(CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(pmtDobj.getValid_upto()));
            PrimeFaces.current().ajax().update("expireSoonId");
            PrimeFaces.current().executeScript("PF('expireSoon').show();");
        } else {
            save_details();
        }
    }

    public void agreeToProceed() {
        if (agreeToProceed) {
            save_details();
        } else {
            JSFUtils.showMessagesInDialog("Error", "Permit is expire in " + CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(pmtDobj.getValid_upto()) + " . So authorization not allowed.", FacesMessage.SEVERITY_INFO);
        }
    }

    public void setValidityAndProceed() {
        if (setValidityAndProceed) {
            setAuthUpto(pmtDobj.getValid_upto());
            PrimeFaces.current().ajax().update("auth_details");
            PrimeFaces.current().executeScript("PF('pmtValidityBeforeAuth').hide();");
        }
    }

    public void save_details() {
        try {
            PermitHomeAuthDobj dobj = new PermitHomeAuthDobj();
            InsDobj ins_dobj = null;
            String msg = CommonPermitPrintImpl.checkPmtValidation(pmtCheckDtsl.getDtlsDobj());
            if (!CommonUtils.isNullOrBlank(msg)) {
                JSFUtils.showMessagesInDialog("Information", msg, FacesMessage.SEVERITY_INFO);
            } else if (pmtType == Integer.parseInt(TableConstants.NATIONAL_PERMIT) && (pmtCatg == 0 || pmtCatg == -1)) {
                JSFUtils.showMessagesInDialog("Information", "Please update permit category through RTO Admin then apply Renew home auth application", FacesMessage.SEVERITY_ERROR);
            } else if (renderPeriod && !CommonUtils.isNullOrBlank(auth_periodMode) && !CommonUtils.isNullOrBlank(auth_period) && (getAuth_periodMode().equals("-1") || getAuth_period().equals("-1"))) {
                JSFUtils.showMessagesInDialog("Information", "Please select Period", FacesMessage.SEVERITY_ERROR);
            } else if (getAuthUpto().getTime() < getAuthFrom().getTime()) {
                JSFUtils.showMessagesInDialog("Error", "Authorization upto always greater than Authorization from date", FacesMessage.SEVERITY_ERROR);
            } else if (ServerUtil.dateRange(getAuthFrom(), 1, 0, -1).getTime() < getAuthUpto().getTime()) {
                JSFUtils.showMessagesInDialog("Error", "Authorization  valid only for one year", FacesMessage.SEVERITY_ERROR);
            } else if (Util.getUserStateCode().equals("UK") && (!CommonUtils.isNullOrBlank(permit_Dtls_bean.getReplace_dt())) && getAuthUpto().after(DateUtils.parseDate(permit_Dtls_bean.getReplace_dt()))) {
                JSFUtils.showMessagesInDialog("Error", "Authorization valid upto can't be greater from Replacement Date", FacesMessage.SEVERITY_ERROR);
            } else if (isRender_payment_table_aitp() && pmtType == Integer.parseInt(TableConstants.AITP) && (statecoveredBean == null || statecoveredBean.getPaymentList() == null || statecoveredBean.getPaymentList().size() <= 0)) {
                JSFUtils.showMessagesInDialog("Error", "Please select state and enter payment details", FacesMessage.SEVERITY_ERROR);
            } else if (pmtDobj != null && pmtDobj.getValid_upto().getTime() >= getAuthUpto().getTime()) {
                if (pmtCheckDtsl.getDtlsDobj().isInsSaveData()) {
                    ins_dobj = new InsDobj();
                    ins_dobj.setAppl_no("");
                    ins_dobj.setIns_from(CommonPermitPrintImpl.getDateDD_MMM_YYYY(pmtCheckDtsl.getDtlsDobj().getInsFrom()));
                    ins_dobj.setIns_upto(CommonPermitPrintImpl.getDateDD_MMM_YYYY(pmtCheckDtsl.getDtlsDobj().getInsUpto()));
                    ins_dobj.setComp_cd(Integer.valueOf(CommonPermitPrintImpl.getValueForSelectedList((ArrayList) arrayInsCmpy, pmtCheckDtsl.getDtlsDobj().getInsCmpyNo())));
                    ins_dobj.setIns_type(Integer.valueOf(CommonPermitPrintImpl.getValueForSelectedList((ArrayList) arrayInsType, pmtCheckDtsl.getDtlsDobj().getInsType())));
                    ins_dobj.setPolicy_no(pmtCheckDtsl.getDtlsDobj().getInsPolicyNo());
                }
                impl = new PermitHomeAuthImpl();
                dobj.setAuthFrom(getAuthFrom());
                dobj.setAuthUpto(getAuthUpto());
                dobj.setAuthNo(getAuthNo().toUpperCase());
                if (dobj == null || CommonUtils.isNullOrBlank(dobj.getAuthNo())) {
                    throw new VahanException("Authorization no is empty. Please fill the authorization no.");
                }
                dobj.setRegnNo(getRegnNo().toUpperCase());
                if (pmtDobj != null && pmtDobj.getPmt_no() != null) {
                    dobj.setPmtNo(pmtDobj.getPmt_no());
                } else {
                    dobj.setPmtNo("PENDING");
                }
                if (pmtDobj != null && String.valueOf(pmtDobj.getPmt_type()).equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)) {
                    PassengerPermitDetailDobj passPmtDobj = new PassengerPermitDetailDobj();
                    passPmtDobj.setRegnNo(pmtDobj.getRegn_no());
                    passPmtDobj.setPmt_type_code(String.valueOf(pmtDobj.getPmt_type()));
                    passPmtDobj.setPmtCatg(String.valueOf(pmtDobj.getPmt_catg()));
                    passPmtDobj.setRegion_covered(pmtDobj.getRegionCovered());
                    Owner_dobj ownerdobj = new Owner_dobj();
                    ownerdobj.setLd_wt(ownDobj.getLd_wt());
                    ownerdobj.setRegn_dt(ownDobj.getRegnDt());
                    String[] values = CommonPermitPrintImpl.getPmtValidateMsg(Util.getUserStateCode(), TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD, ownerdobj, passPmtDobj);
                    if (values != null && !CommonUtils.isNullOrBlank(values[1])) {
                        throw new VahanException(values[1]);
                    }
                }
                dobj.setPurCd(TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD);
                pmtCatg = Util.getUserStateCode().equals("PY") && pmtType == Integer.parseInt(TableConstants.AITP) && pmtCatg == TableConstants.PY_AITP_SZ_CATG ? pmtCatg : 0;
                String appl_no = impl.insert_into_VA_PERMIT_HOME(dobj, getPmtType(), ins_dobj, pmtCatg, renderPeriod, auth_periodMode, auth_period, statecoveredBean);
                printDialogBox(appl_no);
            } else {
                JSFUtils.showMessagesInDialog("Error", "Permit Validity less than for authorization upto.", FacesMessage.SEVERITY_ERROR);
            }

        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Error", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Error", TableConstants.SomthingWentWrong, FacesMessage.SEVERITY_ERROR);
        }
    }

    public void check_Time_Period() {
        period.clear();
        if (("M").equalsIgnoreCase(getAuth_periodMode())) {
            period.add(0, "3");
            period.add(1, "6");
        } else if (("Y").equalsIgnoreCase(getAuth_periodMode())) {
            period.add(0, "1");
        } else if (("D").equalsIgnoreCase(getAuth_periodMode())) {
            period.add(0, "30");
        }
    }

    public void checkAuthDetails() {
        if (renderPeriod && (getAuth_periodMode().equals("-1") || getAuth_period().equals("-1"))) {
            JSFUtils.showMessagesInDialog("Information", "Please select Period", FacesMessage.SEVERITY_ERROR);
        } else if (renderPeriod && !CommonUtils.isNullOrBlank(auth_periodMode) && !CommonUtils.isNullOrBlank(auth_period) && !auth_periodMode.equals("-1") && !auth_period.equals("-1") && DateUtils.compareDates(getAuthFrom(), new Date()) == 0) {
            if (("M").equalsIgnoreCase(getAuth_periodMode()) && getAuth_period().equals("3")) {
                setAuthUpto(ServerUtil.dateRange(getAuthFrom(), 0, 3, -1));
            } else if (("M").equalsIgnoreCase(getAuth_periodMode()) && getAuth_period().equals("6")) {
                setAuthUpto(ServerUtil.dateRange(getAuthFrom(), 0, 6, -1));
            } else if (("D").equalsIgnoreCase(getAuth_periodMode()) && getAuth_period().equals("30")) {
                setAuthUpto(ServerUtil.dateRange(getAuthFrom(), 0, 0, 30));
            } else {
                setAuthUpto(ServerUtil.dateRange(getAuthFrom(), 1, 0, -1));
            }
        } else {
            if (("M").equalsIgnoreCase(getAuth_periodMode()) && getAuth_period().equals("3")) {
                setAuthUpto(ServerUtil.dateRange(getAuthFrom(), 0, 3, -1));
            } else if (("M").equalsIgnoreCase(getAuth_periodMode()) && getAuth_period().equals("6")) {
                setAuthUpto(ServerUtil.dateRange(getAuthFrom(), 0, 6, -1));
            } else if (("D").equalsIgnoreCase(getAuth_periodMode()) && getAuth_period().equals("30")) {
                setAuthUpto(ServerUtil.dateRange(getAuthFrom(), 0, 0, 30));
            } else {
                setAuthUpto(ServerUtil.dateRange(getAuthFrom(), 1, 0, -1));
            }
        }
    }

    public void printDialogBox(String app_no) {
        if (("").equalsIgnoreCase(app_no)) {
            setAppNoMsg("Application Number is not genrated");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        } else {
            setAppNoMsg("Application Number generated :" + app_no + ". Please note down the Application No for future reference.");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        }
        PrimeFaces.current().ajax().update("app_num_id");
    }

    public void showApplPendingDialogBox(String str) {
        setAppNoMsg(str);
        PrimeFaces.current().ajax().update("app_num_id");
        PrimeFaces.current().executeScript("PF('appNum').show();");
    }

    public void closeNpauthDialog() {
        PrimeFaces.current().ajax().update("npauthid");
        PrimeFaces.current().executeScript("PF('npfees').hide();");
    }

    public void dateSelectEvent() {
        setAuthUpto(ServerUtil.dateRange(getAuthFrom(), 1, 0, -1));
        if (renderPeriod) {
            checkAuthDetails();
        }
    }

    @Override
    public String save() {
        return "seatwork";
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        return getCompBeanList();
    }

    @Override
    public String saveAndMoveFile() {
        String action_cd = (String) String.valueOf(appl_details.getCurrent_action_cd());
        if (CommonUtils.isNullOrBlank(action_cd)) {
            JSFUtils.showMessagesInDialog("Info", "Action details not Found.", FacesMessage.SEVERITY_ERROR);
            return "";
        }
        String return_location = "";
        String np_appl_no = "";
        Date np_auth_upto = null;
        try {

            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());

            if (enable_home_auth_on_verify && action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_VERIFICATION)) && getPmtType() == Integer.parseInt(TableConstants.AITP)) {
                if (getAuthUpto().getTime() < getAuthFrom().getTime()) {
                    JSFUtils.showMessagesInDialog("Error", "Authorization upto always greater than Authorization from date", FacesMessage.SEVERITY_ERROR);
                    return "";
                } else if (ServerUtil.dateRange(getAuthFrom(), 1, 0, -1).getTime() < getAuthUpto().getTime()) {
                    JSFUtils.showMessagesInDialog("Error", "Authorization  valid only for one year", FacesMessage.SEVERITY_ERROR);
                    return "";
                } else if (pmtDobj != null && pmtDobj.getValid_upto().getTime() < getAuthUpto().getTime()) {
                    JSFUtils.showMessagesInDialog("Error", "Permit Validity less than for authorization upto.", FacesMessage.SEVERITY_ERROR);
                    return "";
                }
            }

            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                if ((action_cd == null ? (String.valueOf(TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_VERIFICATION)) == null : action_cd.equals(String.valueOf(TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_VERIFICATION)))
                        || (action_cd == null ? (String.valueOf(TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_APPROVAL)) == null : action_cd.equals(String.valueOf(TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_APPROVAL)))) {
                    if (Util.getUserStateCode().equalsIgnoreCase("UP")) {
                        String msg = CommonPermitPrintImpl.checkPmtValidation(pmtCheckDtsl.getDtlsDobj());
                        if (!CommonUtils.isNullOrBlank(msg)) {
                            throw new VahanException(msg);
                        }
                    }
                    if ("DL,UP,GJ".contains(Util.getUserStateCode()) && action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_APPROVAL)) && getPmtType() == Integer.parseInt(TableConstants.NATIONAL_PERMIT)) {
                        PermitHomeAuthDobj permitHomeAuthDobj = ServerUtil.getNPAuthDetailsAtPrint(appl_details.getRegn_no(), appl_details.getAppl_no(), Util.getUserStateCode());
                        Map<String, String> getNpMap = new PrintPermitDocInXhtmlImpl().getNPHomeAuthFromNpPortal(appl_details.getRegn_no(), null);
                        if (getNpMap != null && !getNpMap.isEmpty()) {
                            np_auth_upto = DateUtils.parseDate(getNpMap.get("auth_to"));
                            np_appl_no = getNpMap.get("v4_appl_no");
                            if (permitHomeAuthDobj != null) {
                                long dayDiff = 0;
                                if (np_auth_upto.after(permitHomeAuthDobj.getAuthUpto())) {
                                    dayDiff = DateUtils.getDate1MinusDate2_Days(permitHomeAuthDobj.getAuthUpto(), np_auth_upto);
                                } else {
                                    dayDiff = DateUtils.getDate1MinusDate2_Days(np_auth_upto, permitHomeAuthDobj.getAuthUpto());
                                }
                                if ((!CommonUtils.isNullOrBlank(np_appl_no) && !np_appl_no.equalsIgnoreCase(appl_details.getAppl_no()))) {
                                    if (dayDiff > 30) {
                                        throw new VahanException("Please visit the portal https://vahan.parivahan.gov.in/npermit/ first for payment of applicable MoRTH Composite Fee.");
                                    }
                                } else if ((!permitHomeAuthDobj.getNpVerifyStatus().equalsIgnoreCase("A")) && "DL".contains(Util.getUserStateCode()) && dayDiff > 90) {
                                    throw new VahanException("Please visit the portal https://vahan.parivahan.gov.in/npermit/ first for payment of applicable MoRTH Composite Fee.");
                                } else if ((!permitHomeAuthDobj.getNpVerifyStatus().equalsIgnoreCase("A")) && "UP,GJ".contains(Util.getUserStateCode())) {
                                    throw new VahanException("Please visit the portal https://vahan.parivahan.gov.in/npermit/ first for payment of applicable MoRTH Composite Fee.");
                                }
                            }

                        } else {
                            throw new VahanException("Please visit the portal https://vahan.parivahan.gov.in/npermit/ first for payment of applicable MoRTH Composite Fee.");
                        }
                    }
                    impl.moveAndSave(status, action_cd, appl_details.getAppl_no(), appl_details.getRegn_no(), ownDobj.getMobile_no(), statecoveredBean, enable_home_auth_on_verify, getAuthFrom(), getAuthUpto(), getPmtType());
                    if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_DISPATCH_PENDING) && CommonUtils.isNullOrBlank(return_location)) {
                        return_location = disapprovalPrint();
                    } else {
                        return_location = "seatwork";
                    }
                }
            }
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                status.setPrev_action_cd_selected(app_disapp_dobj.getPre_action_code());
                impl.rebackStatus(status);
                return_location = "seatwork";
            }
        } catch (VahanException ex) {
            JSFUtils.showMessagesInDialog("Information", ex.getMessage(), FacesMessage.SEVERITY_ERROR);
            return_location = "";
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return return_location;
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getActionCd() {
        return actionCd;
    }

    public void setActionCd(int actionCd) {
        this.actionCd = actionCd;
    }

    public PermitHomeAuthImpl getImpl() {
        return impl;
    }

    public PermitDetailDobj getPmtDobj() {
        return pmtDobj;
    }

    public PermitDetailImpl getPmtImpl() {
        return pmtImpl;
    }

    public PermitDetailBean getPermit_Dtls_bean() {
        return permit_Dtls_bean;
    }

    public void setPermit_Dtls_bean(PermitDetailBean permit_Dtls_bean) {
        this.permit_Dtls_bean = permit_Dtls_bean;
    }

    public String getAppNoMsg() {
        return appNoMsg;
    }

    public void setAppNoMsg(String appNoMsg) {
        this.appNoMsg = appNoMsg;
    }

    public boolean isRenderFlage() {
        return renderFlage;
    }

    public void setRenderFlage(boolean renderFlage) {
        this.renderFlage = renderFlage;
    }

    public boolean isApp_disapp_panel() {
        return appDisappPanel;
    }

    public void setAppDisappPanel(boolean appDisappPanel) {
        this.appDisappPanel = appDisappPanel;
    }

    public String getAuthNo() {
        return authNo;
    }

    public void setAuthNo(String authNo) {
        this.authNo = authNo;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public Date getAuthFrom() {
        return authFrom;
    }

    public void setAuthFrom(Date authFrom) {
        this.authFrom = authFrom;
    }

    public Date getAuthUpto() {
        return authUpto;
    }

    public void setAuthUpto(Date authUpto) {
        this.authUpto = authUpto;
    }

    public Date getTaxFrom() {
        return taxFrom;
    }

    public void setTaxFrom(Date taxFrom) {
        this.taxFrom = taxFrom;
    }

    public Date getTaxUpto() {
        return taxUpto;
    }

    public void setTaxUpto(Date taxUpto) {
        this.taxUpto = taxUpto;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public PermitHomeAuthDobj getDobj() {
        return dobj;
    }

    public void setDobj(PermitHomeAuthDobj dobj) {
        this.dobj = dobj;
    }

    public List<PermitHomeAuthDobj> getOldAuthDetails() {
        return oldAuthDetails;
    }

    public void setOldAuthDetails(List<PermitHomeAuthDobj> oldAuthDetails) {
        this.oldAuthDetails = oldAuthDetails;
    }

    public boolean isPreviousAuthDtlsRender() {
        return previousAuthDtlsRender;
    }

    public void setPreviousAuthDtlsRender(boolean previousAuthDtlsRender) {
        this.previousAuthDtlsRender = previousAuthDtlsRender;
    }

    public int getPmtType() {
        return pmtType;
    }

    public void setPmtType(int pmtType) {
        this.pmtType = pmtType;
    }

    public boolean isAuthFromDisable() {
        return authFromDisable;
    }

    public void setAuthFromDisable(boolean authFromDisable) {
        this.authFromDisable = authFromDisable;
    }

    public boolean isAuthUptoDisable() {
        return authUptoDisable;
    }

    public void setAuthUptoDisable(boolean authUptoDisable) {
        this.authUptoDisable = authUptoDisable;
    }

    public boolean isDisableNpDetails() {
        return disableNpDetails;
    }

    public void setDisableNpDetails(boolean disableNpDetails) {
        this.disableNpDetails = disableNpDetails;
    }

    public String getPrvFeeDtls() {
        return prvFeeDtls;
    }

    public void setPrvFeeDtls(String prvFeeDtls) {
        this.prvFeeDtls = prvFeeDtls;
    }

    public PermitCheckDetailsBean getPmtCheckDtsl() {
        return pmtCheckDtsl;
    }

    public void setPmtCheckDtsl(PermitCheckDetailsBean pmtCheckDtsl) {
        this.pmtCheckDtsl = pmtCheckDtsl;
    }

    public List getArrayInsCmpy() {
        return arrayInsCmpy;
    }

    public void setArrayInsCmpy(List arrayInsCmpy) {
        this.arrayInsCmpy = arrayInsCmpy;
    }

    public List getArrayInsType() {
        return arrayInsType;
    }

    public void setArrayInsType(List arrayInsType) {
        this.arrayInsType = arrayInsType;
    }

    public PermitOwnerDetailDobj getOwnDobj() {
        return ownDobj;
    }

    public void setOwnDobj(PermitOwnerDetailDobj ownDobj) {
        this.ownDobj = ownDobj;
    }

    public PermitOwnerDetailImpl getOwnImpl() {
        return ownImpl;
    }

    public void setOwnImpl(PermitOwnerDetailImpl ownImpl) {
        this.ownImpl = ownImpl;
    }

    public boolean isAgreeToProceed() {
        return agreeToProceed;
    }

    public void setAgreeToProceed(boolean agreeToProceed) {
        this.agreeToProceed = agreeToProceed;
    }

    public String getMainPermitValidUpto() {
        return mainPermitValidUpto;
    }

    public void setMainPermitValidUpto(String mainPermitValidUpto) {
        this.mainPermitValidUpto = mainPermitValidUpto;
    }

    public PermitPaidFeeDtlsBean getPaidFeeDtls() {
        return paidFeeDtls;
    }

    public void setPaidFeeDtls(PermitPaidFeeDtlsBean paidFeeDtls) {
        this.paidFeeDtls = paidFeeDtls;
    }

    public boolean isRenderPeriod() {
        return renderPeriod;
    }

    public void setRenderPeriod(boolean renderPeriod) {
        this.renderPeriod = renderPeriod;
    }

    public String getAuth_period() {
        return auth_period;
    }

    public void setAuth_period(String auth_period) {
        this.auth_period = auth_period;
    }

    public String getAuth_periodMode() {
        return auth_periodMode;
    }

    public void setAuth_periodMode(String auth_periodMode) {
        this.auth_periodMode = auth_periodMode;
    }

    public List getPeriod() {
        return period;
    }

    public void setPeriod(List period) {
        this.period = period;
    }

    public boolean isDisablePeriod() {
        return disablePeriod;
    }

    public void setDisablePeriod(boolean disablePeriod) {
        this.disablePeriod = disablePeriod;
    }

    public boolean isSetValidityAndProceed() {
        return setValidityAndProceed;
    }

    public void setSetValidityAndProceed(boolean setValidityAndProceed) {
        this.setValidityAndProceed = setValidityAndProceed;
    }

    public boolean isRender_payment_table_aitp() {
        return render_payment_table_aitp;
    }

    public void setRender_payment_table_aitp(boolean render_payment_table_aitp) {
        this.render_payment_table_aitp = render_payment_table_aitp;
    }

    public AITPStateCoveredBean getStatecoveredBean() {
        return statecoveredBean;
    }

    public void setStatecoveredBean(AITPStateCoveredBean statecoveredBean) {
        this.statecoveredBean = statecoveredBean;
    }

    public boolean isPending_auth() {
        return pending_auth;
    }

    public void setPending_auth(boolean pending_auth) {
        this.pending_auth = pending_auth;
    }

    public boolean isEnable_home_auth_on_verify() {
        return enable_home_auth_on_verify;
    }

    public void setEnable_home_auth_on_verify(boolean enable_home_auth_on_verify) {
        this.enable_home_auth_on_verify = enable_home_auth_on_verify;
    }

    public boolean isExempt_echallan() {
        return exempt_echallan;
    }

    public void setExempt_echallan(boolean exempt_echallan) {
        this.exempt_echallan = exempt_echallan;
    }
}
