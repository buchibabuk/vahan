/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import javax.servlet.http.HttpSession;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.user_mgmt.dobj.UserAuthorityDobj;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.HypothecationDetailsBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.dobj.permit.PermitHomeAuthDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.AITPStateCoveredDobj;
import nic.vahan.form.dobj.permit.PermitFeeDobj;
import nic.vahan.form.dobj.permit.PermitPaidFeeDtlsDobj;
import nic.vahan.form.dobj.permit.PermitReservationDobj;
import nic.vahan.form.dobj.permit.PermitTimeTableDobj;
import nic.vahan.form.dobj.permit.VmPermitRouteDobj;
import nic.vahan.form.impl.ApproveImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.NonUseImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.permit.PermitDetailImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.form.impl.permit.PermitHomeAuthImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PermitCheckDetailsImpl;
import nic.vahan.form.impl.permit.PermitFeeImpl;
import nic.vahan.form.impl.permit.PermitLOIImpl;
import nic.vahan.form.impl.permit.PermitPaidFeeDtlsImpl;
import nic.vahan.form.impl.permit.PermitReservationImpl;
import nic.vahan.form.impl.permit.SurrenderPermitImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.inputtextarea.InputTextarea;
import org.primefaces.component.selectmanymenu.SelectManyMenu;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.PrimeFaces;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "PasPermitDtls")
@ViewScoped
public final class PassengerPermitDetailBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(PassengerPermitDetailBean.class);
    @ManagedProperty(value = "#{approveImpl}")
    private ApproveImpl approveImpl;
    boolean showHide;
    boolean eApplshowHide;
    PermitOwnerDetailDobj owner_dobj = null;
    PermitOwnerDetailImpl ownerImpl = null;
    PermitDetailDobj pmtDobj = null;
    PermitDetailImpl pmtImpl = null;
    private String app_disapp = "A";
    private String order_by = null;
    private String order_no = null;
    private Date order_dt = null;
    private String reason = null;
    private boolean disableOrderDtls = false;
    private boolean reasonRendered = true;
    private int selectOneRadio = 1;
    private boolean regn_know = true;
    private String via_route = "";
    private SelectOneMenu domain = new SelectOneMenu();
    private int pmtcatg = -1;
    private boolean pmtCatgDisable = false;
    private String paction_code;
    private String pmt_type_code;
    private List pmt_mast = new ArrayList();
    private List period = new ArrayList();
    private List paction = new ArrayList();
    private List domainList = new ArrayList();
    private List ser_type = new ArrayList();
    private List pmtCategory = new ArrayList();
    private List route_mast = new ArrayList();
    private List area_mast = new ArrayList();
    private DualListModel<PermitRouteList> routeManage;
    private DualListModel<PermitRouteList> areaManage;
    private List<PermitRouteList> actionSource = new ArrayList<>();
    private List<PermitRouteList> actionTarget = new ArrayList<>();
    private List<PermitRouteList> areaActionSource = new ArrayList<>();
    private List<PermitRouteList> areaActionTarget = new ArrayList<>();
    private List<PermitRouteList> prvactionTarget = new ArrayList<>();
    private DualListModel<PermitRouteList> otherStateRouteManage;
    private List<PermitRouteList> otherStateActionSource = new ArrayList<>();
    private List<PermitRouteList> otherStateActionTarget = new ArrayList<>();
    private String user_regn_no = "";
    private String offerNumber = "";
    private boolean offerNumberRender = false;
    private boolean renderGetDetails = true;
    private String app_no_msg;
    private String regn_no = "";
    private boolean regnNoDisable = false;
    private InputText pmt_type = new InputText();
    private InputText floc = new InputText();
    private InputText tloc = new InputText();
    private String parking = null;
    private boolean parkingDisabled = false;
    private InputText other_REGION = new InputText();
    private InputText appl_NO = new InputText();
    private String joreny_PURPOSE = null;
    private boolean jorenyPurposedDisabled = false;
    private InputText other_REGION_ROUT = new InputText();
    private String goodsToCarry = null;
    private boolean goodsToCarryDisabled = false;
    private Integer periodValue;
    private boolean periodValueDisable = false;
    private String header;
    //private Panel approval_Panel = new Panel();
    private boolean offerApprovalPanelRendered = false;
    private String rto_CD;
    private InputText number_OF_TRIPS = new InputText();
    private InputTextarea selectManyRoute = new InputTextarea();
    private List<String> route_map;
    private int pmtPurCode;
    private String periodMode = "-1";
    private boolean periodModeDisable = false;
    private int pmtType;
    private boolean pmtTypeDisable = false;
    private int services_type = -1;
    private boolean servicesTypeDisable = false;
    private boolean but_function = true;
    private String succ_fail = "";
    PassengerPermitDetailImpl passImp = null;
    private SelectManyMenu routeCode = new SelectManyMenu();
    private PassengerPermitDetailDobj permit_detail_dobj = new PassengerPermitDetailDobj();
    private PassengerPermitDetailDobj permit_detail_dobj_prv;
    private PermitOwnerDetailDobj permit_owner_detail_dobj = new PermitOwnerDetailDobj();
    private PermitOwnerDetailDobj permit_owner_detail_dobj_prv;
    private boolean disableRoutDtls = false;
    private boolean disableAreaDtls = false;
    @ManagedProperty(value = "#{pmt_owner_dtls}")
    private PermitOwnerDetailBean ownerBean;
    @ManagedProperty(value = "#{Permit_Dtls_bean}")
    private PermitDetailBean permit_Dtls_bean;
    @ManagedProperty(value = "#{hypothecationDetails_bean}")
    private HypothecationDetailsBean hypothecationDetails_bean;
    private List<ComparisonBean> compBeanList = new ArrayList<ComparisonBean>();
    private InputText pmt_num = new InputText();
    private InputText val_from = new InputText();
    private InputText val_upto = new InputText();
    boolean pmt_render;
    private List<ComparisonBean> prevChangedDataList;
    private String masterLayout = "/masterLayoutPage.xhtml";
    //private boolean visible_Route_area = true;
    private boolean visibleAreaDtls = true;
    private boolean visibleRouteDtls = true;
    private PermitHomeAuthDobj auth_dobj = new PermitHomeAuthDobj();
    private PermitHomeAuthDobj auth_dobj_psv;
    PermitHomeAuthImpl auth_impl;
    private boolean visibleAuthDetails = false;
    private int tabIndex;
    private String errorShowMsg = "";
    UserAuthorityDobj authorityDobj = null;
    private String permissionVhClass = "";
    private String permissionPermitType = "";
    private String permissionPermitCatg = "";
    private int permitIssuingAuthCode = 0;
    private boolean disableOtherVehicleDtls = false;
    @ManagedProperty(value = "#{pmtDtlsBean}")
    private PermitCheckDetailsBean pmtCheckDtsl;
    private List arrayInsCmpy = new ArrayList();
    private List arrayInsType = new ArrayList();
    private Date dateOfReplacement = null;
    private Date currenDate = DateUtil.parseDate(DateUtil.getCurrentDate());
    private Date minReplacementDate = DateUtil.parseDate(DateUtil.getCurrentDate());
    private Date maxLimitOfReplacementDate = null;
    private boolean replacementDateDisable = false;
    private boolean noOfTripsrendered = false;
    private boolean notFillHomeAuth = false;
    private String new_o_name = "";
    private String new_f_name = "";
    private String new_vh_class = "";
    private String new_regn_no = "";
    private String offerLetterNo = "";
    private boolean offLTRender = false;
    private boolean regnNoRender = false;
    private boolean pmtDialogeVisible = false;
    private List newVhClassType = new ArrayList();
    private boolean disableRegnNo = true;
    // Nitin KUmar 21-01-2016 Begin 
    private OwnerDetailsDobj ownerDetail;
    // Nitin KUmar 21-01-2016 End
    List<PermitReservationDobj> resList = new ArrayList();
    int selectedVehClass;
    private boolean disableNpDetails;
    private String pmtSubType;
    private boolean pmtSubTypeRender = false;
    private boolean renderPrintDocument = false;
    private String msgOfPrintDocument;
    private String valueOfCommandButton;
    private String validityMsg;
    @ManagedProperty(value = "#{paidFeeDtls}")
    private PermitPaidFeeDtlsBean paidFeeDtls;
    private List regionCoveredListNP = new ArrayList();
    private String prvAuthoDtls = "";
    private String msgForRemarks = "";
    private boolean renderhypth = false;
    private String feeMsgIfPaidAtTymOfRegn = null;
    private String applNoForAck;
    //manoj select region at new registration time 
    private String regn_domain = "";
    private Date pmt_valid_from;
    private boolean render_valid_from = false;
    private int routeLength = 0;
    private String vahanMessages = null;
    private SessionVariables sessionVariables = null;
    private Map<String, String> stateConfigMap = null;
    private int havingOfferLetter = 3;
    @ManagedProperty(value = "#{StateCovered}")
    private AITPStateCoveredBean statecoveredBean;
    private String taxExem;
    private List<PassengerPermitDetailDobj> multiPermitDetails = new ArrayList<>();
    private PassengerPermitDetailDobj selectedValue = null;
    private String permit_no = "";
    private int no_of_trip = 0;
    private boolean multiApplAllow = false;
    private boolean showRegionCovered = false;
    private int regionCovered;
    private boolean showOwnerDLAndVoterId;
    private boolean allowSpecialVhClass;
    private boolean spl_vh_class = false;
    private String pmt_type_allowed = "";
    private boolean otherRouteAllow = false;
    private String[] selectedOffice;
    private List officeList = new ArrayList();
    private List<VmPermitRouteDobj> selectedRouteList;
    private List<VmPermitRouteDobj> route_list = new ArrayList<>();
    private boolean showOtherRoute = false;
    private String state_cd;
    private boolean taxExemptForKL = false;
    private boolean renderAuthPeriod = false;
    private String auth_periodMode = "";
    private int auth_period = 0;
    private List auth_period_list = new ArrayList();
    private boolean disableAuthPeriod = false;
    private boolean render_payment_table_aitp = false;
    private boolean visibleInterStateRouteDtls = false;
    private List<SelectItem> stateToList = new ArrayList<>();
    private String state_to = null;
    private String via_route_oth = "";
    private boolean taxDependOnPmt = true;
    private boolean showTimeTableDetails = false;
    private boolean showTimeTable = false;
    @ManagedProperty(value = "#{pmttimetable}")
    private PermitTimeTableBean pmtTimeTableBean;
    private List<PermitTimeTableDobj> prevTimeTableList = new ArrayList<>();
    private boolean renderDocUploadTab = false;
    private String dmsUrl = "";
    private boolean renderGoodsPassengerTax = false;
    private String selectedRouteString = "";
    private boolean exempt_echallan = false;

    public PassengerPermitDetailBean() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
            vahanMessages = "Session time Out";
            return;
        }
        resList = null;
        state_cd = sessionVariables.getStateCodeSelected();
        if (permitIssuingAuthCode == 0) {
            permitIssuingAuthCode = sessionVariables.getOffCodeSelected();
        }

        authorityDobj = Util.getUserAuthority();
        routeManage = new DualListModel<>(actionSource, actionTarget);
        areaManage = new DualListModel<>(areaActionSource, areaActionTarget);
        String[][] data = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
        for (int i = 0; i < data.length; i++) {
            if (("M").equalsIgnoreCase(data[i][0]) || ("Y").equalsIgnoreCase(data[i][0])) {
                period.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
        data = MasterTableFiller.masterTables.vm_service_type.getData();
        for (int i = 0; i < data.length; i++) {
            ser_type.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
        for (int i = 0; i < data.length; i++) {
            pmt_mast.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_ICCODE.getData();
        for (int i = 0; i < data.length; i++) {
            arrayInsCmpy.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_INSTYP.getData();
        for (int i = 0; i < data.length; i++) {
            arrayInsType.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        for (int i = 0; i < data.length; i++) {
            newVhClassType.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.TM_OFFICE.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][13].equalsIgnoreCase(Util.getUserStateCode()) && TableConstants.OTHER_OFFICE_ROUTE_ALLOWED_FOR_PERMIT.contains(data[i][0])) {
                officeList.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
        stateConfigMap = CommonPermitPrintImpl.getVmPermitStateConfiguration(sessionVariables.getStateCodeSelected());
        multiApplAllow = Boolean.parseBoolean(stateConfigMap.get("multi_appl_allow"));
        spl_vh_class = Boolean.parseBoolean(stateConfigMap.get("allow_special_vh_class"));
        otherRouteAllow = Boolean.parseBoolean(stateConfigMap.get("other_office_route_allow"));
        render_payment_table_aitp = Boolean.parseBoolean(stateConfigMap.get("render_payment_table_aitp"));
        taxDependOnPmt = Boolean.parseBoolean(stateConfigMap.get("tax_depend_on_pmt"));
        renderGoodsPassengerTax = Boolean.parseBoolean(stateConfigMap.get("render_goods_passanger_tax"));
        exempt_echallan = Boolean.parseBoolean(stateConfigMap.get("exempt_echallan"));

        if (spl_vh_class) {
            setAllowSpecialVhClass(true);
        }
        try {
            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            parameters.setPUR_CD(appl_details.getPur_cd());
            if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("show_uploaded_document"), parameters), "init")) {
                renderDocUploadTab = true;
            } else {
                renderDocUploadTab = false;
            }
            if ((sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_VERIFICATION)
                    || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_APPROVAL)
                    || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_VERIFICATION)
                    || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPROVE)
                    || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_RENWAL_VERIFICATION)
                    || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_RENWAL_APPROVE)
                    || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY)
                    || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_SCRUTINY)
                    && (sessionVariables.getActionCodeSelected() != 0)) {
                String applNo = appl_details.getAppl_no();
                passImp = new PassengerPermitDetailImpl();
                auth_impl = new PermitHomeAuthImpl();
                ownerImpl = new PermitOwnerDetailImpl();
                permit_detail_dobj = passImp.set_permit_appl_db_to_dobj(applNo);
                permit_owner_detail_dobj = ownerImpl.set_VA_Owner_permit_to_dobj(applNo, appl_details.getRegn_no());
                auth_dobj = auth_impl.select_IN_VA_HOME_AUTH(applNo);
                if (permit_detail_dobj == null) {
                    permit_detail_dobj = new PassengerPermitDetailDobj();
                    permit_owner_detail_dobj = new PermitOwnerDetailDobj();
                } else {
                    permit_detail_dobj_prv = (PassengerPermitDetailDobj) permit_detail_dobj.clone();
                    permit_owner_detail_dobj_prv = (PermitOwnerDetailDobj) permit_owner_detail_dobj.clone();
                }
                if (auth_dobj != null) {
                    auth_dobj_psv = (PermitHomeAuthDobj) auth_dobj.clone();
                } else {
                    auth_dobj_psv = new PermitHomeAuthDobj();
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        setPeriodValueDisable(true);
    }

    @PostConstruct
    public void init() {
        try {
            if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                    || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
                vahanMessages = "Session time Out.";
                return;
            }
            getNumber_OF_TRIPS().setRendered(false);
            but_function = true;
            setApp_disapp("A");
            owner_dobj = new PermitOwnerDetailDobj();
            ownerBean.getPmt_dobj().setFlage(true);
            reasonRendered = false;

            if (sessionVariables.getActionCodeSelected() == 0) {
                showHide = false;
                eApplshowHide = true;
            } else {
                if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY
                        && CommonUtils.isNullOrBlank(appl_details.getAppl_no())) {
                    setHeader("PERMIT APPLICATION/OFFER ENTRY");
                    showHide = true;
                    eApplshowHide = true;
                    taxExem = ServerUtil.getTmConfigurationParameters(Util.getUserStateCode()).getTax_exemption();
                } else if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY
                        && !CommonUtils.isNullOrBlank(appl_details.getAppl_no())) {
                    setHeader("PERMIT APPLICATION/OFFER RE-ENTRY");
                    prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                    bt_fill_report_for_verify();
                    showHide = false;
                    eApplshowHide = false;
                    replacementDateDisable = false;
                } else if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_VERIFICATION || sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_SCRUTINY) {
                    if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_VERIFICATION) {
                        setHeader("PERMIT APPLICATION/OFFER VERIFICATION");
                    } else if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_SCRUTINY) {
                        setHeader("PERMIT APPLICATION/OFFER SCRUTINY");

                    }
                    prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                    bt_fill_report_for_verify();
                    showHide = false;
                    eApplshowHide = false;
                    pmtTypeDisable = true;
                    setPmtCatgDisable(true);
                    disableOtherVehicleDtls = false;
                } else if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_VERIFICATION) {
                    setHeader("PERMIT VERIFICATION");
                    disableAuthPeriod = true;
                    prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                    validityMsg = CommonPermitPrintImpl.getPmtValidityMsg(sessionVariables.getStateCodeSelected(), getAppl_details().getPur_cd(), getAppl_details().getAppl_no());
                    if (("NEW").equalsIgnoreCase(getAppl_details().getRegn_no())) {
                        PermitFeeImpl pmt_fee_impl = new PermitFeeImpl();
                        String db_offerLetterNo = pmt_fee_impl.getOffereLetterNo(appl_details.getAppl_no());
                        if (CommonUtils.isNullOrBlank(db_offerLetterNo)) {
                            offLTRender = false;
                            regnNoRender = true;
                            PassengerPermitDetailDobj permit_dobj = new PassengerPermitDetailDobj();
                            permit_dobj = passImp.set_permit_appl_db_to_dobj(appl_details.getAppl_no());
                            auth_dobj = auth_impl.select_IN_VA_HOME_AUTH(appl_details.getAppl_no());
                            if (auth_dobj == null) {
                                auth_dobj = new PermitHomeAuthDobj();
                                notFillHomeAuth = true;
                            }
                            List<AITPStateCoveredDobj> stateList = new ArrayList<>();
                            if (permit_dobj != null && permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.AITP) && !CommonUtils.isNullOrBlank(permit_dobj.getRegion_covered())) {
                                String[] aitpSelState = permit_dobj.getRegion_covered().split(",");
                                visibleAuthDetails = true;
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
                                if (stateList.isEmpty()) {
                                    Map<String, String> routeMap = passImp.getTargetAreaMapWithStrBuil(permit_dobj.getRegion_covered().replace(",", ""), sessionVariables.getStateCodeSelected(), permitIssuingAuthCode);
                                    for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                                        regionCoveredListNP.add(new SelectItem(entry.getKey(), entry.getValue()));
                                    }
                                }
                            }
                        } else {
                            offLTRender = true;
                            regnNoRender = false;
                        }
                        PrimeFaces.current().ajax().update("newRegnDtls");
                        pmtDialogeVisible = true;
                    } else {
                        bt_fill_report_for_verify();
                    }
                    showHide = false;
                    eApplshowHide = false;
                    replacementDateDisable = false;
                    applPermitReadOnly(true);

                } else if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_OFFER_VERIFICATION) {
                    setHeader("PERMIT OFFER VERIFICATION");
                    prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                    validityMsg = CommonPermitPrintImpl.getPmtValidityMsg(sessionVariables.getStateCodeSelected(), getAppl_details().getPur_cd(), getAppl_details().getAppl_no());
                    PermitFeeImpl pmt_fee_impl = new PermitFeeImpl();
                    String db_offerLetterNo = pmt_fee_impl.getOffereLetterNo(appl_details.getAppl_no());
                    if (CommonUtils.isNullOrBlank(db_offerLetterNo)) {
                        offLTRender = false;
                        regnNoRender = true;
                    } else {
                        offLTRender = true;
                        regnNoRender = false;
                    }
                    PrimeFaces.current().ajax().update("newRegnDtls");
                    pmtDialogeVisible = true;
                    showHide = false;
                    eApplshowHide = false;
                    replacementDateDisable = false;
                    applPermitReadOnly(true);

                } else if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_RENWAL_VERIFICATION) {
                    prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                    validityMsg = CommonPermitPrintImpl.getPmtValidityMsg(sessionVariables.getStateCodeSelected(), getAppl_details().getPur_cd(), getAppl_details().getAppl_no());
                    setHeader("RENEWAL PERMIT VERIFICATION");
                    setPermit_no(permit_detail_dobj.getPmt_no());
                    PassengerPermitDetailDobj permit_dobj = passImp.set_vt_permit_regnNo_to_dobj(getAppl_details().getRegn_no(), getPermit_no());
                    permit_dobj.setPaction(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD));
                    set_renewal_Permit_Detail_dobj_to_bean(permit_dobj);
                    applPermitReadOnly(true);
                    prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                    bt_fill_report_for_verify();
                    showHide = false;
                    eApplshowHide = false;
                    replacementDateDisable = true;
                    pmt_render = true;
                    showOtherRoute = false;
                } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_APPLICATION_APPROVAL) {
                    prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                    setHeader("PERMIT APPLICATION/OFFER APPROVE");
                    bt_fill_report_for_verify();
                    offerApprovalPanelRendered = true;
                    showHide = false;
                    eApplshowHide = false;
                    pmtTypeDisable = true;
                    setPmtCatgDisable(true);
                    disableOtherVehicleDtls = false;
                } else if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPROVE) {
                    prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                    validityMsg = CommonPermitPrintImpl.getPmtValidityMsg(sessionVariables.getStateCodeSelected(), getAppl_details().getPur_cd(), getAppl_details().getAppl_no());
                    setHeader("PERMIT APPROVE");
                    disableAuthPeriod = true;
                    bt_fill_report_for_verify();
                    if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("OR")) {
                        setRender_valid_from(true);
                    }
                    applPermitReadOnly(true);
                    showHide = false;
                    eApplshowHide = false;
                    replacementDateDisable = true;
                } else if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_RENWAL_APPROVE) {
                    prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                    validityMsg = CommonPermitPrintImpl.getPmtValidityMsg(sessionVariables.getStateCodeSelected(), getAppl_details().getPur_cd(), getAppl_details().getAppl_no());
                    setHeader("RENEWAL PERMIT APPROVE");
                    setPermit_no(permit_detail_dobj.getPmt_no());
                    PassengerPermitDetailDobj permit_dobj = passImp.set_vt_permit_regnNo_to_dobj(getAppl_details().getRegn_no(), getPermit_no());
                    permit_dobj.setPaction(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD));
                    set_renewal_Permit_Detail_dobj_to_bean(permit_dobj);
                    setRender_valid_from(false);
                    bt_fill_report_for_verify();
                    applPermitReadOnly(true);
                    showHide = false;
                    eApplshowHide = false;
                    replacementDateDisable = true;
                    pmt_render = true;
                    showOtherRoute = false;
                }

                stateToList.clear();
                List list = new PassengerPermitDetailImpl().getStateToFromRouteMaster(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                String[][] data = MasterTableFiller.masterTables.TM_STATE.getData();
                for (int i = 0; i < data.length; i++) {
                    if (list.contains(data[i][0])) {
                        stateToList.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }

            }
        } catch (Exception e) {
        }
    }

    public void getResetAllFields() {
        but_function = true;
        ownerBean.setValueReset();
        ownerBean.getPmt_dobj().setFlage(true);
        setPeriodMode("-1");
        setPmtType(-1);
        setPmtcatg(-1);
        setPeriodValue(null);
        setVia_route("");
        setDateOfReplacement(null);
        setParking("");
        setGoodsToCarry("");
        setJoreny_PURPOSE("");
        routeManage.getSource().clear();
        routeManage.getTarget().clear();
        areaManage.getSource().clear();
        areaManage.getTarget().clear();
    }

    public void bt_get_Details() {
        tabIndex = 0;
        getResetAllFields();
        FacesMessage message;
        SurrenderPermitImpl surrImpl = new SurrenderPermitImpl();
        String Registration_no = getUser_regn_no().toUpperCase();
        InsDobj insDobj = null;
        HpaImpl hpa_Impl = new HpaImpl();
        int pmtOffCd = 0;
        int renewPmtType = 0;

        try {
            //nitin kumar - 21-01-2015 BEGIN 
            if (getSelectOneRadio() == havingOfferLetter) {
                bt_fill_report_for_verify();
                pmtTypeDisable = true;
                setPmtCatgDisable(true);
                disableOtherVehicleDtls = false;
                return;
            }
            OwnerImpl owner_Impl = new OwnerImpl();
            List<OwnerDetailsDobj> ownerDetailsDobjList = owner_Impl.getOwnerDetailsList(Registration_no.trim(), null);
            ownerDetailsDobjList = owner_Impl.getStateWiseOwnerList(ownerDetailsDobjList, sessionVariables.getStateCodeSelected());
            if (ownerDetailsDobjList != null && ownerDetailsDobjList.size() == 1) {
                ownerDetail = ownerDetailsDobjList.get(0);
            } else if (ownerDetailsDobjList != null && ownerDetailsDobjList.size() >= 2) {
                ownerBean.setDupRegnList(ownerDetailsDobjList);
                //PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Can't Inward Application due to Duplicate Registration No Found for Current State"));
                PrimeFaces.current().executeScript("PF('varDupRegNo').show()");
                return;
            }
            if (ownerDetail == null) {
                but_function = true;
                disableOtherVehicleDtls = false;
                ownerBean.setValueReset();
                ownerBean.getPmt_dobj().setFlage(true);
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number Not Found in Current Office"));
                return;
            }
            boolean multiPmtAllow = Boolean.parseBoolean(stateConfigMap.get("multi_pmt_allow_on_veh"));
            String surrMsg = surrImpl.permitSurrMsg(Registration_no, multiPmtAllow);
            if (!CommonUtils.isNullOrBlank(surrMsg)) {
                showApplPendingDialogBox(surrMsg);
                return;
            }
            passImp = new PassengerPermitDetailImpl();
            owner_dobj = new PermitOwnerDetailDobj();
            ownerImpl = new PermitOwnerDetailImpl();
            PermitOwnerDetailDobj dobj = ownerBean.getOwnerDetailsDobj(ownerDetail);
            //nitin kumar  - 21-01-2015 - END

            if (!(dobj.getOwnerStatus().equalsIgnoreCase("A")
                    || dobj.getOwnerStatus().equalsIgnoreCase("Y"))) {
                throw new VahanException("Vehicle is not in active stage. Please contact with administrator.");
            }
            if (dobj != null) {
                if ("OR".equalsIgnoreCase(dobj.getState_cd())) {
                    boolean isNonUse = NonUseImpl.nonUseDetailsExist(dobj.getRegn_no(), dobj.getState_cd());
                    if (isNonUse) {

                        message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle is in NonUse . Please Restore/ Remove Vehicle From NonUse!", "Vehicle is in NonUse . Please Restore/ Remove Vehicle From NonUse!!");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        return;
                    }
                }
                pmtCheckDtsl.getAlldetails(Registration_no, insDobj, dobj.getState_cd(), dobj.getOff_cd());
                if (exempt_echallan) {
                    pmtCheckDtsl.getDtlsDobj().setExemptedChallan(true);
                }
                String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
                setMaxLimitOfReplacementDate(dobj.getReplaceDateByVtOwner());
                int vhClassType = 0;
                boolean vhClassFlag = false;
                for (int i = 0; i < data.length; i++) {
                    if ((String.valueOf(dobj.getVh_class())).equalsIgnoreCase(data[i][0])) {
                        vhClassType = Integer.valueOf(data[i][2]);
                    }
                }
                if (authorityDobj != null) {
                    if (authorityDobj.getSelectedVehClass().size() == 1) {
                        for (Object obj : authorityDobj.getSelectedVehClass()) {
                            permissionVhClass = String.valueOf(obj);
                        }
                    }
                }
                if (authorityDobj == null || ("Any").equalsIgnoreCase(permissionVhClass)) {
                    vhClassFlag = true;
                } else {
                    for (Object obj : authorityDobj.getSelectedVehClass()) {
                        if ((String.valueOf(obj)).equalsIgnoreCase(String.valueOf(dobj.getVh_class()))) {
                            vhClassFlag = true;
                            break;
                        }
                    }
                }
                Map<String, String> OffAllotResult = PermitDetailImpl.getOffAllotmentResult(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), dobj.getOff_cd());
                String OffAllowedString = PermitDetailImpl.getOffAllowedString(sessionVariables.getStateCodeSelected());

                boolean flag = passImp.getvt_statueRegnno(Registration_no);
                if (!flag) {
                    PassengerPermitDetailDobj pmt_dobj = passImp.set_vt_permit_regnNo_to_dobj(Registration_no, "");
                    pmtOffCd = Integer.parseInt(pmt_dobj.getOff_cd());
                    renewPmtType = Integer.parseInt(pmt_dobj.getPmt_type());
                }
                Map<String, String> routeMap = passImp.getSourcesAreaMapWithStrBuil("0", sessionVariables.getStateCodeSelected(), permitIssuingAuthCode);
                regionCoveredListNP.clear();
                regionCoveredListNP.add(new SelectItem("0", "SELECT ANY AREA/REAGION"));
                for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                    regionCoveredListNP.add(new SelectItem(entry.getKey(), entry.getValue()));
                }

                if (flag) {
                    feeMsgIfPaidAtTymOfRegn = CommonPermitPrintImpl.getPmtFeePaidAtTymOfRegn(sessionVariables.getStateCodeSelected(), Registration_no.toUpperCase(), null);
                    if (dobj != null && dobj.getState_cd().equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                            && vhClassType == TableConstants.VM_VEHTYPE_TRANSPORT && vhClassFlag && (("true").equalsIgnoreCase(OffAllotResult.get("offAllowed")) || (OffAllowedString.contains("," + sessionVariables.getOffCodeSelected() + ",") && multiApplAllow))) {
                        if ((OffAllotResult.get("purAllowed") == null) || !(OffAllotResult.get("purAllowed").contains(String.valueOf(TableConstants.VM_PMT_FRESH_PUR_CD)))) {
                            throw new VahanException("Office not allowed the facility of new permit application.");
                        }

                        applPermitReadOnly(false);
                        disableOtherVehicleDtls = true;
                        replacementDateDisable = false;
                        but_function = false;
                        ownerBean.getPmt_dobj().setFlage(true);
                        ownerBean.setValueinDOBJ(dobj);
                        routeCodeDetail("NULL", TableList.vt_permit_route, sessionVariables.getStateCodeSelected(), permitIssuingAuthCode, false);
                        areaCodeDetail(sessionVariables.getStateCodeSelected(), "", permitIssuingAuthCode);
                        setRegn_no(ownerBean.getRegn_no());
                        pmt_render = false;
                        paction.clear();
                        data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
                        int i = 0;
                        for (i = 0; i < data.length; i++) {
                            if (Integer.parseInt(data[i][0]) == TableConstants.VM_PMT_FRESH_PUR_CD
                                    || Integer.parseInt(data[i][0]) == TableConstants.VM_PMT_APPLICATION_PUR_CD) {
                                paction.add(new SelectItem(data[i][0], data[i][1]));
                                break;
                            }
                        }
                        setPmtPurCode(TableConstants.VM_PMT_FRESH_PUR_CD);

                        if (Boolean.getBoolean(stateConfigMap.get("allowed_sta_rto_relation"))
                                && (getSelectOneRadio() == 1
                                || getSelectOneRadio() == 2)) {
                            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                            setPmtPurCode(new PassengerPermitDetailImpl().getPurCdFormStateConfig(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("sta_rto_condition"), parameters)));
                        }

                        if (CommonPermitPrintImpl.isVehicleSurrInPermanentMode(Registration_no)) {
                            getTaxPmtDtls(ownerBean.getRegn_no(), dobj.getVh_class(), null);
                        }

                        if (!CommonUtils.isNullOrBlank(Registration_no) || !Registration_no.equalsIgnoreCase("NEW")) {
                            List<HpaDobj> hypth = hpa_Impl.getHypoDetails(null, TableConstants.VM_TRANSACTION_MAST_REM_HYPO, Registration_no, true, dobj.getState_cd());
                            hypothecationDetails_bean.setListHypthDetails(hypth);
                            if (hypth.size() > 0) {
                                setRenderhypth(true);
                            }
                        }
//                        if (CommonPermitPrintImpl.isVehicleSurrInPermanentMode(Registration_no) && passImp.regnNoSpecialOrderRequest(getRegn_no().getValue().toString().toUpperCase(), TableConstants.VM_PMT_FRESH_PUR_CD)) {
//                            getTaxPmtDtls(ownerBean.getRegn_no(), dobj.getVh_class());
//                        }

                        if (spl_vh_class) {
                            setAllowSpecialVhClass(true);
                        }
                        if ("KL".equalsIgnoreCase(sessionVariables.getStateCodeSelected()) && getPmtPurCode() == TableConstants.VM_PMT_FRESH_PUR_CD) {
                            passImp = new PassengerPermitDetailImpl();
                            String vhClassAllowed = passImp.getVhClassAllowedForTaxExempt(getPmtPurCode());
                            if (vhClassAllowed.contains(ownerBean.getVh_class())) {
                                setTaxExemptForKL(true);
                            }
                        }
                    } else {
                        if (!(dobj != null && dobj.getState_cd().equalsIgnoreCase(sessionVariables.getStateCodeSelected()))) {
                            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert", "Invalid Vehicle");
                        } else if (!(vhClassType == TableConstants.VM_VEHTYPE_TRANSPORT)) {
                            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert", "This vehicle is Non Transport.");
                        } else if (!vhClassFlag) {
                            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert", "This Vehicle Class is not valid for this logged-in user.");
                        } else if (("false").equalsIgnoreCase(OffAllotResult.get("offAllowed"))) {
                            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registered Office of this vehicle is not mapped to this Office.");
                        } else {
                            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert", "Invalid Vehicle");
                        }
                        FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                        ownerBean.setValueReset();
                        ownerBean.getPmt_dobj().setFlage(true);
                    }
                } else {
                    if (dobj != null && dobj.getState_cd().equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                            && vhClassType == TableConstants.VM_VEHTYPE_TRANSPORT && vhClassFlag && (("true").equalsIgnoreCase(OffAllotResult.get("offAllowed")) || (("false").equalsIgnoreCase(OffAllotResult.get("offAllowed")) && sessionVariables.getStateCodeSelected().equalsIgnoreCase("UP") && sessionVariables.getOffCodeSelected() == pmtOffCd) || (multiApplAllow && sessionVariables.getOffCodeSelected() == pmtOffCd && renewPmtType == TableConstants.STAGE_CARRIAGE_PERMIT))) {
                        if ((OffAllotResult.get("purAllowed") == null) || !(OffAllotResult.get("purAllowed").contains(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD))) && !(sessionVariables.getStateCodeSelected().equalsIgnoreCase("UP"))) {
                            throw new VahanException("Office not allowed the facility of renew permit application.");
                        }
                        disableOtherVehicleDtls = true;
                        ownerBean.getPmt_dobj().setFlage(true);
                        ownerBean.setValueinDOBJ(dobj);
                        pmt_render = true;
                        paction.clear();
                        replacementDateDisable = true;
                        data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
                        int i = 0;
                        for (i = 0; i < data.length; i++) {
                            if (Integer.parseInt(data[i][0]) == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
                                paction.add(new SelectItem(data[i][0], data[i][1]));
                                break;
                            }
                        }
                        setRegnNoDisable(true);
                        setRegn_no(ownerBean.getRegn_no());
                        setShowTimeTableDetails(false);
                        PassengerPermitDetailDobj permit_dobj = passImp.set_vt_permit_regnNo_to_dobj(Registration_no, "");
                        if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("PB") && permit_dobj != null && (permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.GOODS_PERMIT) || permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT))) {
                            multiPermitDetails = passImp.getCurrentPermitDetails(sessionVariables.getStateCodeSelected(), Registration_no);
                            if (multiPermitDetails.size() > 1) {
                                PrimeFaces.current().ajax().update("no_Of_pmt");
                                PrimeFaces.current().executeScript("PF('noOfPmt').show();");
                            }
                        } else {
                            set_renewal_Permit_Detail_dobj_to_bean(permit_dobj);
                            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                            parameters.setPERMIT_TYPE(Integer.parseInt(permit_dobj.getPmt_type()));
                            parameters.setPERMIT_SUB_CATG(Integer.parseInt(permit_dobj.getPmtCatg()));
                            parameters.setPUR_CD(TableConstants.VM_PMT_RENEWAL_PUR_CD);
                            if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("update_owner_identification"), parameters), "bt_get_Details")) {
                                setShowOwnerDLAndVoterId(true);
                                //     PrimeFaces.current().ajax().update("permit_details voter_id dl_no");
                            }
                        }
                        setPmtPurCode(Integer.valueOf(data[i][0]));

                        List<HpaDobj> hypth = hpa_Impl.getHypoDetails(null, TableConstants.VM_TRANSACTION_MAST_REM_HYPO, Registration_no, true, sessionVariables.getStateCodeSelected());
                        hypothecationDetails_bean.setListHypthDetails(hypth);
                        if (hypth.size() > 0) {
                            setRenderhypth(true);
                        }
                        freezePeriod(getPmtPurCode(), Integer.valueOf(permit_dobj.getPmt_type()), 0, null, renderhypth, dobj.getOwner_cd());
                        OwnerImpl impl = new OwnerImpl();
                        Owner_dobj ownDobj = impl.getOwnerDobj(ownerDetail);
                        if (TableConstants.NATIONAL_PERMIT.equalsIgnoreCase(permit_dobj.getPmt_type())
                                || TableConstants.AITP.equalsIgnoreCase(permit_dobj.getPmt_type())) {
                            onSelectPermitType("");
                            setShowRegionCovered(true);
                            PermitHomeAuthImpl PrvAuthImpl = new PermitHomeAuthImpl();
                            PermitHomeAuthDobj PrvAuthDobj = PrvAuthImpl.getSelectInVtPermit(ownerBean.getRegn_no().toUpperCase());
                            if (PrvAuthDobj != null) {
                                if (!CommonUtils.isNullOrBlank(permit_dobj.getRegion_covered())) {
                                    String str = permit_dobj.getRegion_covered();
                                    String[] temp = str.split(",");
                                    if (JSFUtils.isNumeric(temp[0])) {
                                        setRegionCovered(Integer.parseInt(temp[0]));
                                    }
                                }
                                setPrvAuthoDtls("Previous Authorization No : " + PrvAuthDobj.getAuthNo()
                                        + " , Authorization Validity : " + CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(PrvAuthDobj.getAuthFrom())
                                        + " To " + CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(PrvAuthDobj.getAuthUpto()) + ".");
                            } else {
                                setPrvAuthoDtls("");
                            }
                        } else {
                            visibleAreaDtls = true;
                            visibleRouteDtls = true;
                            VehicleParameters vchparameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                            vchparameters.setPERMIT_TYPE(Integer.parseInt(permit_dobj.getPmt_type()));
                            vchparameters.setPERMIT_SUB_CATG(Integer.parseInt(permit_dobj.getPmtCatg()));;
                            if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("show_other_state_route"), vchparameters), "checkReservation")) {
                                visibleInterStateRouteDtls = true;
                            } else {
                                visibleInterStateRouteDtls = false;
                            }
                            if (otherRouteAllow && Integer.parseInt(permit_dobj.getPmt_type()) == TableConstants.STAGE_CARRIAGE_PERMIT
                                    && passImp.isOtherOfficeRoute(permit_dobj.getApplNo(), TableList.vt_permit_route, sessionVariables.getStateCodeSelected(), permitIssuingAuthCode)) {
                                showOtherRoute = true;
                            } else {
                                showOtherRoute = false;
                            }
                            String stateTo = passImp.getStateTofromVaPermitRoute(permit_dobj.getApplNo(), TableList.vt_permit_route);
                            setState_to(stateTo);
                            otherStateRouteCodeDetail(permit_dobj.getApplNo(), TableList.vt_permit_route, sessionVariables.getStateCodeSelected(), permitIssuingAuthCode, state_to);
                            routeCodeDetail(permit_dobj.getApplNo(), TableList.vt_permit_route, sessionVariables.getStateCodeSelected(), permitIssuingAuthCode, showOtherRoute);
                            selectedRouteString = "";
                            if (showOtherRoute && selectedRouteList != null && selectedRouteList.size() > 0) {
                                for (VmPermitRouteDobj selectDobj : selectedRouteList) {
                                    selectedRouteString = selectedRouteString + "[" + selectDobj.getOff_cd_descr() + "]" + selectDobj.getRoute_code() + ":" + selectDobj.getFrom_loc() + "-" + selectDobj.getVia() + "-" + selectDobj.getTo_loc() + "\n";
                                }
                                showOtherRoute = false;
                            }
                            if (permit_dobj.getRegion_covered() != null) {
                                String str = permit_dobj.getRegion_covered();
                                String[] temp = str.split(",");
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int j = 0; j < temp.length; j++) {
                                    stringBuilder.append("(").append("'").append(temp[j]).append("'").append("),");
                                }
                                areaCodeDetail(sessionVariables.getStateCodeSelected(), stringBuilder.substring(0, stringBuilder.length() - 1), permitIssuingAuthCode);
                            }
                        }
                        if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("OR") && TableConstants.GCW_VEH_CLASS.contains("," + ownDobj.getVh_class() + ",")) {
                            if (ownDobj.getGcw() == 0) {
                                ownDobj.setGcw(ownerImpl.setGcwWeight(ownDobj));
                            }
                        }
                        String[] values = CommonPermitPrintImpl.getPmtValidateMsg(sessionVariables.getStateCodeSelected(), TableConstants.VM_PMT_RENEWAL_PUR_CD, ownDobj, permit_dobj);
                        if (values != null && !CommonUtils.isNullOrBlank(values[1])) {
                            throw new VahanException(values[1]);
                        }
                        but_function = false;
                        applPermitReadOnly(true);
                    } else {
                        if (!(dobj != null && dobj.getState_cd().equalsIgnoreCase(sessionVariables.getStateCodeSelected()))) {
                            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert", "Invalid Vehicle");
                        } else if (!(vhClassType == TableConstants.VM_VEHTYPE_TRANSPORT)) {
                            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert", "This vehicle is Non Transport.");
                        } else if (!vhClassFlag) {
                            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert", "This Vehicle Class is not valid for this logged-in user.");
                        } else if (("false").equalsIgnoreCase(OffAllotResult.get("offAllowed"))) {
                            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert", "Registered Office of this vehicle is not mapped to this Office.");
                        } else {
                            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert", "Invalid Vehicle");
                        }
                        FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                        ownerBean.setValueReset();
                        ownerBean.getPmt_dobj().setFlage(true);
                    }
                }
            } else {
                ownerBean.setValueReset();
                ownerBean.getPmt_dobj().setFlage(true);
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Owner Detail not found");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            }
        } catch (VahanException e) {
            but_function = true;
            disableOtherVehicleDtls = false;
            ownerBean.setValueReset();
            ownerBean.getPmt_dobj().setFlage(true);
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information : ", e.getMessage());
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            but_function = true;
            disableOtherVehicleDtls = false;
            ownerBean.setValueReset();
            ownerBean.getPmt_dobj().setFlage(true);
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please provide the valid Registration number", null);
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void showTimeTablePage() throws CloneNotSupportedException {
        try {
            if (getNo_of_trip() <= 0) {
                setShowTimeTable(false);
                throw new VahanException("Please enter no of trips");
            }
            if (routeManage.getTarget().size() == 0) {
                throw new VahanException("Please Select any Route");
            }
            if (routeManage.getTarget().size() == 2) {
                throw new VahanException("Time Table Allow for only one Route");
            }
            String[] route_details = passImp.getRoutedetail(routeManage.getTarget(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());

            if (route_details != null && this.showTimeTable) {

                if (routeManage.getTarget().size() == 1) {
                    pmtTimeTableBean.getTimeTableList().clear();
                    for (String via : route_details[route_details.length - 1].split(",")) {
                        PermitTimeTableDobj dobj = new PermitTimeTableDobj();
                        dobj.setState_cd(sessionVariables.getStateCodeSelected());
                        dobj.setOff_cd(sessionVariables.getOffCodeSelected());
                        dobj.setRoute_cd(route_details[0]);
                        dobj.setStoppage(via);

                        int index = pmtTimeTableBean.getTimeTableList().size() / 2;

                        pmtTimeTableBean.getTimeTableList().add(index, dobj);
                        if (!dobj.getStoppage().equals(route_details[2])) {
                            pmtTimeTableBean.getTimeTableList().add(index + 1, ((PermitTimeTableDobj) dobj.clone()));
                        }
                    }
                }

                int j = 0;
                List<PermitTimeTableDobj> clonedList = new ArrayList<PermitTimeTableDobj>(pmtTimeTableBean.getTimeTableList());
                for (int i = 0; i < getNo_of_trip() - 1; i++) {
                    for (PermitTimeTableDobj dobj : clonedList) {
                        if (j == 0) {
                            j++;
                            continue;
                        }
                        pmtTimeTableBean.getTimeTableList().add((PermitTimeTableDobj) dobj.clone());
                    }
                    j = 0;
                }

                if (pmtTimeTableBean.getTimeTableList().size() > 0) {
                    pmtTimeTableBean.getTimeTableList().get(0).setDay(1);
                    pmtTimeTableBean.getTimeTableList().get(0).setDisableDay(true);
                    pmtTimeTableBean.getTimeTableList().get(0).setRoute_fr_time("00:00");
                    pmtTimeTableBean.getTimeTableList().get(0).setDisableFromTime(true);
                    pmtTimeTableBean.getTimeTableList().get(pmtTimeTableBean.getTimeTableList().size() - 1).setRoute_to_time("00:00");
                    pmtTimeTableBean.getTimeTableList().get(pmtTimeTableBean.getTimeTableList().size() - 1).setDisableToTime(true);
                }
                setShowTimeTable(true);
            } else {
                setShowTimeTable(false);
            }

        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", new FacesMessage(FacesMessage.SEVERITY_INFO, "", ex.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void bt_get_details_by_name() throws VahanException {
        // RequestContext context = RequestContext.getCurrentInstance();
        ownerImpl = new PermitOwnerDetailImpl();
        owner_dobj = ownerImpl.set_VA_Owner_permit_to_dobj(appl_details.getAppl_no(), "NEW");
        ownerBean.setValueinDOBJ(owner_dobj);
        passImp = new PassengerPermitDetailImpl();
        PassengerPermitDetailDobj permit_dobj = new PassengerPermitDetailDobj();
        permit_dobj = passImp.set_permit_appl_db_to_dobj(appl_details.getAppl_no());
        permit_dobj.setRegnNo(getNew_regn_no().toUpperCase());
        set_Passenger_Permit_Detail_dobj_to_bean(permit_dobj);
        String stateTo = passImp.getStateTofromVaPermitRoute(appl_details.getAppl_no(), TableList.va_permit_route);
        setState_to(stateTo);
        otherStateRouteCodeDetail(appl_details.getAppl_no(), TableList.va_permit_route, sessionVariables.getStateCodeSelected(), permitIssuingAuthCode, state_to);
        routeCodeDetail(appl_details.getAppl_no(), TableList.va_permit_route, sessionVariables.getStateCodeSelected(), permitIssuingAuthCode, false);
        PrimeFaces.current().ajax().update("All_detail:route_details");
        String str = permit_dobj.getRegion_covered();
        String[] temp = str.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < temp.length; i++) {
            stringBuilder.append("(").append("'" + temp[i] + "'").append("),");
        }
        if (temp != null && temp.length > 0 && JSFUtils.isNumeric(temp[0])) {
            areaCodeDetail(sessionVariables.getStateCodeSelected(), stringBuilder.substring(0, stringBuilder.length() - 1), permitIssuingAuthCode);
        }
        pmtCheckDtsl.getAlldetails(getNew_regn_no().toUpperCase(), null, owner_dobj.getState_cd(), owner_dobj.getOff_cd());
        disableOtherVehicleDtls = true;
        but_function = false;

    }

    public void bt_fill_report_for_verify() {
        disableOtherVehicleDtls = true;
        InsDobj insDobj = null;
        PassengerPermitDetailDobj permit_dobj = null;
        //RequestContext context = RequestContext.getCurrentInstance();
        String sessionApplNo = null, sessionRegnNo = null;
        try {

            if (getSelectOneRadio() == havingOfferLetter) {
                if (CommonUtils.isNullOrBlank(getOfferNumber().toUpperCase())) {
                    throw new VahanException("Please enter the offer no.");
                }
                Map<String, String> information = new PassengerPermitDetailImpl().getRegnNoWithApplNo(sessionVariables.getStateCodeSelected(), getOfferNumber().toUpperCase());
                sessionApplNo = information.get("appl_no");
                sessionRegnNo = information.get("regn_no");;
            } else {
                sessionApplNo = appl_details.getAppl_no();
                sessionRegnNo = appl_details.getRegn_no();

            }

            if (CommonUtils.isNullOrBlank(sessionApplNo) || CommonUtils.isNullOrBlank(sessionRegnNo)) {
                throw new VahanException("Application/Regn No. not found. Please Contact to system administrator.");
            }
            if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_APPLICATION_APPROVAL) {
                setDisableOrderDtls(true);
                if (Util.getUserName().length() > 29) {
                    setOrder_by(Util.getUserName().substring(0, 29));
                } else {
                    setOrder_by(Util.getUserName());
                }
                setOrder_dt(new Date());
                setOrder_no(new PermitLOIImpl().readOnlyPermitUniqueNo(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), 0, 0, "A"));
            }
            owner_dobj = new PermitOwnerDetailDobj();
            ownerImpl = new PermitOwnerDetailImpl();
            permit_dobj = new PassengerPermitDetailDobj();
            passImp = new PassengerPermitDetailImpl();
            owner_dobj = ownerImpl.set_VA_Owner_permit_to_dobj(sessionApplNo, sessionRegnNo);
            pmtCheckDtsl.getAlldetails(sessionRegnNo, insDobj, owner_dobj.getState_cd(), owner_dobj.getOff_cd());
            if (exempt_echallan) {
                pmtCheckDtsl.getDtlsDobj().setExemptedChallan(true);
            }
            ownerBean.setValueinDOBJ(owner_dobj);
            if (("NEW").equalsIgnoreCase(sessionRegnNo)) {
                ownerBean.getPmt_dobj().setFlage(false);
            } else {
                ownerBean.getPmt_dobj().setFlage(true);
            }
            permit_owner_detail_dobj_prv = (PermitOwnerDetailDobj) owner_dobj.clone();
            permit_dobj = passImp.set_permit_appl_db_to_dobj(sessionApplNo);
            if (permit_dobj == null) {
                throw new VahanException("Permit details not found. Please contact to system administrator");
            }

            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            parameters.setPERMIT_TYPE(Integer.parseInt(permit_dobj.getPmt_type()));
            parameters.setPERMIT_SUB_CATG(Integer.parseInt(permit_dobj.getPmtCatg()));
            parameters.setPUR_CD(Integer.parseInt(permit_dobj.getPaction()));
            if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("update_owner_identification"), parameters), "bt_fill_report_for_verify")) {
                setShowOwnerDLAndVoterId(true);
                //   PrimeFaces.current().ajax().update("permit_details voter_id dl_no");
            }
            if (getSelectOneRadio() == havingOfferLetter) {
                permit_dobj.setPaction("26");
                if (appl_details.getPur_cd() == 0) {
                    appl_details.setPur_cd(TableConstants.VM_PMT_FRESH_PUR_CD);
                }
            }
            set_Passenger_Permit_Detail_dobj_to_bean(permit_dobj);
            int freezePmtType = 0;
            if (permit_dobj != null && !CommonUtils.isNullOrBlank(permit_dobj.getPmt_type())) {
                freezePmtType = Integer.valueOf(permit_dobj.getPmt_type());
            }
            freezePeriod(getPmtPurCode(), freezePmtType, 0, permit_dobj, renderhypth, owner_dobj.getOwner_cd());
            paidFeeDtls.getListOfPaidPmtFee(sessionApplNo);
            if ((permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)
                    || permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.AITP))
                    && (appl_details.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD)) {
                visibleAreaDtls = false;
                visibleRouteDtls = false;
                visibleAuthDetails = true;
                auth_impl = new PermitHomeAuthImpl();
                regionCoveredListNP.clear();
                List<AITPStateCoveredDobj> stateList = new ArrayList<>();
                if (!CommonUtils.isNullOrBlank(permit_dobj.getRegion_covered())) {
                    String[] aitpSelState = permit_dobj.getRegion_covered().split(",");
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
                    if (stateList.isEmpty()) {
                        Map<String, String> routeMap = passImp.getTargetAreaMapWithStrBuil(permit_dobj.getRegion_covered().replace(",", ""), sessionVariables.getStateCodeSelected(), permitIssuingAuthCode);
                        for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                            regionCoveredListNP.add(new SelectItem(entry.getKey(), entry.getValue()));
                        }
                    }
                }
                if (CommonUtils.isNullOrBlank(permit_dobj.getRegion_covered())
                        || !stateList.isEmpty()) {
                    regionCoveredListNP.add(new SelectItem("0", "SELECT ANY AREA/REGION"));
                    Map<String, String> routeMap = passImp.getSourcesAreaMapWithStrBuil("0", sessionVariables.getStateCodeSelected(), permitIssuingAuthCode);
                    for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                        regionCoveredListNP.add(new SelectItem(entry.getKey(), entry.getValue()));
                    }
                }
                auth_dobj = auth_impl.select_IN_VA_HOME_AUTH(sessionApplNo);
                if (Util.getUserStateCode().equals("KL") && appl_details.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD) {
                    passImp = new PassengerPermitDetailImpl();
                    String vhClassAllowed = passImp.getVhClassAllowedForTaxExempt(getPmtPurCode());
                    if (vhClassAllowed.contains(ownerBean.getVh_class())) {
                        setTaxExemptForKL(true);
                    }
                }
                if (permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT) && !(CommonUtils.isNullOrBlank(permit_dobj.getRegnNo())) && !permit_dobj.getRegnNo().equalsIgnoreCase("NEW")) {
                    permit_Dtls_bean.setNationalPermitAuthDetails(permit_dobj.getRegnNo().toUpperCase().trim());
                    setDisableNpDetails(true);
                } else {
                    setDisableNpDetails(false);
                }
                if (auth_dobj == null && !isTaxExemptForKL()) {
                    auth_dobj = new PermitHomeAuthDobj();
                    notFillHomeAuth = true;
                } else if (auth_dobj == null && isTaxExemptForKL()) {
                    auth_dobj = auth_impl.getVaHomeAuthDetails(sessionApplNo);
                    if (auth_dobj == null) {
                        auth_dobj = new PermitHomeAuthDobj();
                        notFillHomeAuth = true;
                    } else {
                        auth_dobj.setApplNo(auth_dobj.getApplNo());
                        auth_dobj.setAuthFrom(auth_dobj.getAuthFrom());
                        auth_dobj.setAuthUpto(auth_dobj.getAuthUpto());
                    }
                } else {
                    auth_dobj.setApplNo(auth_dobj.getApplNo());
                    auth_dobj.setAuthFrom(auth_dobj.getAuthFrom());
                    auth_dobj.setAuthUpto(auth_dobj.getAuthUpto());
                }

                if (isRender_payment_table_aitp()) {
                    statecoveredBean.setPaymentList(passImp.getAitpPaymentDetails(sessionApplNo));
                }

                getRouteManage().getSource().clear();
                getRouteManage().getTarget().clear();
                getAreaManage().getSource().clear();
                getAreaManage().getTarget().clear();
            } else if ((permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)
                    || permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.AITP))
                    && (appl_details.getPur_cd() == TableConstants.VM_PMT_RENEWAL_PUR_CD)) {
                regionCoveredListNP.clear();
                regionCoveredListNP.add(new SelectItem("0", "SELECT ANY AREA/REGION"));
                Map<String, String> routeMap = passImp.getSourcesAreaMapWithStrBuil("0", sessionVariables.getStateCodeSelected(), permitIssuingAuthCode);
                for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                    regionCoveredListNP.add(new SelectItem(entry.getKey(), entry.getValue()));
                }
                visibleAreaDtls = false;
                visibleRouteDtls = false;
                visibleAuthDetails = false;
                visibleInterStateRouteDtls = false;
                getRouteManage().getSource().clear();
                getRouteManage().getTarget().clear();
                getAreaManage().getSource().clear();
                getAreaManage().getTarget().clear();
                if (getOtherStateRouteManage() != null && getOtherStateRouteManage().getSource() != null) {
                    getOtherStateRouteManage().getSource().clear();
                }
                if (getOtherStateRouteManage() != null && getOtherStateRouteManage().getTarget() != null) {
                    getOtherStateRouteManage().getTarget().clear();
                }
            } else {
                visibleAreaDtls = true;
                visibleRouteDtls = true;
                VehicleParameters vchparameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                vchparameters.setPERMIT_TYPE(Integer.parseInt(permit_dobj.getPmt_type()));
                vchparameters.setPERMIT_SUB_CATG(Integer.parseInt(permit_dobj.getPmtCatg()));;
                if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("show_other_state_route"), vchparameters), "bt_fill_report_for_verify")) {
                    visibleInterStateRouteDtls = true;
                } else {
                    visibleInterStateRouteDtls = false;
                }
                routeCodeDetail(sessionApplNo, TableList.va_permit_route, sessionVariables.getStateCodeSelected(), permitIssuingAuthCode, Integer.parseInt(permit_dobj.getPmt_type()) == TableConstants.STAGE_CARRIAGE_PERMIT && otherRouteAllow);
                String stateTo = passImp.getStateTofromVaPermitRoute(sessionApplNo, TableList.va_permit_route);
                setState_to(stateTo);
                otherStateRouteCodeDetail(sessionApplNo, TableList.va_permit_route, sessionVariables.getStateCodeSelected(), permitIssuingAuthCode, state_to);
                selectedRouteString = "";
                if (appl_details.getPur_cd() == TableConstants.VM_PMT_RENEWAL_PUR_CD && otherRouteAllow && selectedRouteList != null && selectedRouteList.size() > 0) {
                    for (VmPermitRouteDobj selectDobj : selectedRouteList) {
                        selectedRouteString = selectedRouteString + "[" + selectDobj.getOff_cd_descr() + "]" + selectDobj.getRoute_code() + ":" + selectDobj.getFrom_loc() + "-" + selectDobj.getVia() + "-" + selectDobj.getTo_loc() + "\n";
                    }
                    setShowOtherRoute(false);
                }
                PrimeFaces.current().ajax().update("All_detail:route_details");

                String str = permit_dobj.getRegion_covered();
                String[] temp = str.split(",");
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < temp.length; i++) {
                    stringBuilder.append("(").append("'" + temp[i] + "'").append("),");
                }
                areaCodeDetail(sessionVariables.getStateCodeSelected(), stringBuilder.substring(0, stringBuilder.length() - 1), permitIssuingAuthCode);

                VehicleParameters vh_parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                vh_parameters.setPERMIT_TYPE(Integer.parseInt(permit_dobj.getPmt_type()));
                if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("show_time_table"), vh_parameters), "onSelectPermitType")) {
                    setShowTimeTableDetails(true);
                }

                String routeVia = "";
                if (!CommonUtils.isNullOrBlank(getVia_route())) {
                    routeVia = getVia_route();
                } else {
                    routeVia = getVia_route_oth();
                }
                String[] arrRouteVia = routeVia.split(" ");
                for (int i = 1; i <= arrRouteVia.length - 1; i++) {
                    getPmtTimeTableBean().getRouteViaList().add(new SelectItem(arrRouteVia[i], arrRouteVia[i]));
                }
                if (isShowTimeTableDetails()) {
                    getPmtTimeTableBean().setTimeTableList(passImp.getPmtTimeTable(sessionRegnNo, sessionApplNo, sessionVariables.getStateCodeSelected()));
                    setPrevTimeTableList(passImp.getPmtTimeTable(sessionRegnNo, sessionApplNo, sessionVariables.getStateCodeSelected()));
                    if (getPmtTimeTableBean().getTimeTableList().size() > 0 && sessionVariables.getActionCodeSelected() != TableConstants.TM_ROLE_PMT_APPROVE) {
                        pmtTimeTableBean.getTimeTableList().get(0).setDisableDay(true);
                        pmtTimeTableBean.getTimeTableList().get(0).setRoute_fr_time("00:00:00");
                        pmtTimeTableBean.getTimeTableList().get(0).setDisableFromTime(true);
                        pmtTimeTableBean.getTimeTableList().get(pmtTimeTableBean.getTimeTableList().size() - 1).setRoute_to_time("00:00:00");
                        pmtTimeTableBean.getTimeTableList().get(pmtTimeTableBean.getTimeTableList().size() - 1).setDisableToTime(true);
                        setShowTimeTable(true);
                    } else if (getPmtTimeTableBean().getTimeTableList().size() > 0 && sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPROVE) {
                        for (int i = 0; i < getPmtTimeTableBean().getTimeTableList().size(); i++) {
                            pmtTimeTableBean.getTimeTableList().get(i).setDisableDay(true);
                            pmtTimeTableBean.getTimeTableList().get(i).setDisableFromTime(true);
                            pmtTimeTableBean.getTimeTableList().get(i).setDisableToTime(true);
                        }
                        setShowTimeTable(true);
                    }
                }

                if (Boolean.parseBoolean(stateConfigMap.get("pmt_reservation_allow")) && (routeManage.getTarget().size() > 0 || areaManage.getTarget().size() > 0)) {
                    String route_cd = "0";
                    String region = "";
                    passImp = new PassengerPermitDetailImpl();
                    if (routeManage.getTarget().size() > 0) {
                        route_cd = routeManage.getTarget().get(0).getKey();
                    } else if (areaManage.getTarget().size() > 0) {
                        List<PermitRouteList> area = areaManage.getTarget();
                        for (PermitRouteList ss : area) {
                            region = region + (String) ss.getKey() + ",";
                        }
                    }
                    resList = null;
                    if (routeManage.getTarget().size() > 0 && (getPmtType() != -1 || getPmtType() != 0) && (getPmtcatg() != -1 || getPmtcatg() != 0)) {
                        resList = PermitReservationImpl.getPermitReservationDtls(sessionVariables.getStateCodeSelected(), getPmtType(), getPmtcatg(), ownerBean.getOwner_catg(), ownerBean.getFuel_type(), 0, sessionVariables.getOffCodeSelected(), route_cd);
                    } else if (areaManage.getTarget().size() > 0 && (getPmtType() != -1 || getPmtType() != 0) && (getPmtcatg() != -1 || getPmtcatg() != 0)) {
                        int reservRegion = passImp.checkMultiRegion(region, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                        if (reservRegion == 99) {
                            resList = PermitReservationImpl.getPermitReservationDtls(sessionVariables.getStateCodeSelected(), getPmtType(), getPmtcatg(), ownerBean.getOwner_catg(), ownerBean.getFuel_type(), reservRegion, sessionVariables.getOffCodeSelected(), "0");
                        }
                    }
                }

                PrimeFaces.current().ajax().update("All_detail:area_details");

            }
            if (spl_vh_class) {
                setAllowSpecialVhClass(true);
            }
            if (permit_dobj != null && appl_details.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD && permit_dobj.getPmt_type().equals(TableConstants.AITP)) {
                VehicleParameters vchparameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                vchparameters.setPERMIT_TYPE(Integer.parseInt(permit_dobj.getPmt_type()));
                vchparameters.setPUR_CD(appl_details.getPur_cd());;
                if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("show_period_on_aitp_auth"), vchparameters), "onSelectPermitType")) {
                    renderAuthPeriod = true;
                    notFillHomeAuth = true;
                }
            }
            if (renderAuthPeriod) {
                String[] strArr = passImp.getAuthPeriod(sessionApplNo);
                if (strArr != null) {
                    auth_periodMode = (String) strArr[0];
                    check_Time_Period_auth();
                    auth_period = Integer.parseInt(strArr[1]);
                }
            }
            but_function = false;
            pmtSubType = CommonPermitPrintImpl.getPmtSubType(sessionApplNo);
            setPmtSubTypeRender(true);
            if (otherRouteAllow && Integer.parseInt(permit_dobj.getPmt_type()) == TableConstants.STAGE_CARRIAGE_PERMIT) {
                showOtherRoute = true;
            } else {
                showOtherRoute = false;
            }

            if (dateOfReplacement != null && minReplacementDate != null && dateOfReplacement.before(minReplacementDate)) {
                this.minReplacementDate = ServerUtil.dateRange(dateOfReplacement, 0, 0, 0);
            }

            if (getDateOfReplacement() == null && sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_VERIFICATION) {
                OwnerImpl owner_Impl = new OwnerImpl();
                List<OwnerDetailsDobj> ownerDetailsDobjList = owner_Impl.getOwnerDetailsList(permit_dobj.getRegnNo(), null);
                ownerDetailsDobjList = owner_Impl.getStateWiseOwnerList(ownerDetailsDobjList, sessionVariables.getStateCodeSelected());
                if (ownerDetailsDobjList != null && ownerDetailsDobjList.size() == 1) {
                    ownerDetail = ownerDetailsDobjList.get(0);
                }
                permit_dobj.setPmt_type_code(permit_dobj.getPmt_type());
                setReplacementDate(permit_dobj);
            }
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Regn No :- " + sessionRegnNo + "/" + e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void set_Passenger_Permit_Detail_dobj_to_bean(PassengerPermitDetailDobj permit_dobj) {
        if (permit_dobj != null) {
            pmtCategory.clear();
            this.setRegn_no(permit_dobj.getRegnNo());
            if (CommonUtils.isNullOrBlank(permit_dobj.getPeriod_mode())) {
                this.setPeriodMode("-1");
            } else {
                this.setPeriodMode(permit_dobj.getPeriod_mode());
            }
            if (CommonUtils.isNullOrBlank(permit_dobj.getPeriod())) {
                this.setPeriodValue(null);
            } else {
                this.setPeriodValue(Integer.valueOf(permit_dobj.getPeriod()));
            }

            if (CommonUtils.isNullOrBlank(permit_dobj.getPmt_type())) {
                this.setPmtType(-1);
            } else {
                this.setPmtType(Integer.valueOf(permit_dobj.getPmt_type()));
            }

            this.setPmtPurCode(Integer.valueOf(permit_dobj.getPaction()));
            String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
            if (authorityDobj != null) {
                if (authorityDobj.getSelectedPermitCatg().size() == 1) {
                    for (Object obj : authorityDobj.getSelectedPermitCatg()) {
                        permissionPermitCatg = String.valueOf(obj);
                    }
                }
            }
            if (authorityDobj == null || ("Any").equalsIgnoreCase(permissionPermitCatg)) {
                for (int j = 0; j < data.length; j++) {
                    if (data[j][0].equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                            && Integer.parseInt(data[j][3]) == Integer.parseInt(permit_dobj.getPmt_type())) {
                        pmtCategory.add(new SelectItem(data[j][1], data[j][2]));
                    }
                }
            } else {
                for (int j = 0; j < data.length; j++) {
                    for (Object obj : authorityDobj.getSelectedPermitCatg()) {
                        if (data[j][0].equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                                && Integer.parseInt(data[j][3]) == Integer.parseInt(permit_dobj.getPmt_type())
                                && data[j][1].equalsIgnoreCase(String.valueOf(obj))) {
                            pmtCategory.add(new SelectItem(data[j][1], data[j][2]));
                        }
                    }
                }
            }
            if (CommonUtils.isNullOrBlank(permit_dobj.getPmtCatg())) {
                this.setPmtcatg(-1);
            } else {
                this.setPmtcatg(Integer.valueOf(permit_dobj.getPmtCatg()));
            }
            this.getDomain().setValue(permit_dobj.getDomain_CODE());
            if (CommonUtils.isNullOrBlank(permit_dobj.getServices_TYPE())) {
                this.setServices_type(-1);
            } else {
                this.setServices_type(Integer.valueOf(permit_dobj.getServices_TYPE()));
            }
            if (!CommonUtils.isNullOrBlank(permit_dobj.getNumberOfTrips())) {
                setNo_of_trip(Integer.parseInt(permit_dobj.getNumberOfTrips()));
            }
            setGoodsToCarry(permit_dobj.getGoods_TO_CARRY());
            setJoreny_PURPOSE(permit_dobj.getJoreny_PURPOSE());
            setParking(permit_dobj.getParking());
            data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
            int i = 0;
            for (i = 0; i < data.length; i++) {
                if (Integer.parseInt(data[i][0]) == Integer.parseInt(permit_dobj.getPaction())) {
                    paction.add(new SelectItem(data[i][0], data[i][1]));
                    break;
                }
            }
            if (("M").equalsIgnoreCase(permit_dobj.getPeriod_mode()) || ("Y").equalsIgnoreCase(permit_dobj.getPeriod_mode())) {
                setPeriodValueDisable(false);
            }
            this.getNumber_OF_TRIPS().setValue(permit_dobj.getNumberOfTrips());
            setDateOfReplacement(permit_dobj.getReplaceDate());

            if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY
                    && !CommonUtils.isNullOrBlank(appl_details.getAppl_no())) {
                if (permit_dobj.getAllotedFlag().equalsIgnoreCase("O")) {
                    String[][] dat = MasterTableFiller.masterTables.VM_PERMIT_OBJECTION.getData();
                    String[] listOfObjection = permit_dobj.getRemarks().split(",");
                    for (int n = 0; n < dat.length; n++) {
                        for (int m = 0; m < listOfObjection.length; m++) {
                            if (listOfObjection[m].equalsIgnoreCase(dat[n][1])
                                    && dat[n][0].equalsIgnoreCase(sessionVariables.getStateCodeSelected())) {
                                msgForRemarks += dat[n][2] + ",";
                            }
                        }
                    }
                    msgForRemarks = "Objection Is : " + msgForRemarks.substring(0, (msgForRemarks.length() - 1)) + ".";
                } else if (permit_dobj.getAllotedFlag().equalsIgnoreCase("D")) {
                    msgForRemarks = "Reason Is :" + permit_dobj.getRemarks() + ".";
                }
            }
//        if (permit_dobj.getReplaceDate() == null) {
//            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "", "Vehicle replacement of date is Empty");
//            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
//        }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", TableConstants.SomthingWentWrong));
        }
    }

    public String redirectFrom() {
        return "/ui/permit/form_passenger_permit_detail.xhtml?faces-redirect=true";
    }

    public String printAck() {
        HttpSession ses = Util.getSession();
        ses.setAttribute("permitPrintDocPermitType", String.valueOf(getPmtType()));
        ses.setAttribute("permitPrintDocApplNo", applNoForAck);
        ses.setAttribute("permitPrintDocPurCd", String.valueOf(getPmtPurCode()));
        ses.setAttribute("permitPrintDocRegnNo", getRegn_no());
        ses.setAttribute("permitPrintDocID", "11");
        ses.setAttribute("OfferRedirect", "");
        return "/ui/permitreports/PermitAcknowledgement.xhtml?faces-redirect=true";
    }

    public void set_renewal_Permit_Detail_dobj_to_bean(PassengerPermitDetailDobj permit_dobj) {
        if ((TableConstants.NATIONAL_PERMIT.equalsIgnoreCase(permit_dobj.getPmt_type())
                || TableConstants.AITP.equalsIgnoreCase(permit_dobj.getPmt_type()))
                && !(CommonUtils.isNullOrBlank(appl_details.getRegn_no()))) {
            PermitHomeAuthImpl PrvAuthImpl = new PermitHomeAuthImpl();
            PermitHomeAuthDobj PrvAuthDobj = PrvAuthImpl.getSelectInVtPermit(appl_details.getRegn_no().toUpperCase());
            if (PrvAuthDobj != null) {
                if (!CommonUtils.isNullOrBlank(permit_dobj.getRegion_covered())) {
                    setShowRegionCovered(true);
                    String str = permit_dobj.getRegion_covered();
                    String[] temp = str.split(",");
                    if (JSFUtils.isNumeric(temp[0])) {
                        setRegionCovered(Integer.parseInt(temp[0]));
                    }
                }
                setPrvAuthoDtls("Previous Authorization No : " + PrvAuthDobj.getAuthNo()
                        + " , Authorization Validity : " + CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(PrvAuthDobj.getAuthFrom())
                        + " To " + CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(PrvAuthDobj.getAuthUpto()) + ".");
            } else {
                setPrvAuthoDtls("");
            }
        }
        this.getDomain().setValue(permit_dobj.getDomain_CODE());
        if (CommonUtils.isNullOrBlank(permit_dobj.getServices_TYPE())) {
            this.setServices_type(-1);
        } else {
            this.setServices_type(Integer.valueOf(permit_dobj.getServices_TYPE()));
        }
        setGoodsToCarry(permit_dobj.getGoods_TO_CARRY());
        setJoreny_PURPOSE(permit_dobj.getJoreny_PURPOSE());
        setParking(permit_dobj.getParking());
        this.setPmtPurCode(Integer.valueOf(permit_dobj.getPaction()));
        this.getNumber_OF_TRIPS().setValue(permit_dobj.getNumberOfTrips());
        this.setDateOfReplacement(permit_dobj.getReplaceDate());
        if (CommonUtils.isNullOrBlank(permit_dobj.getPmt_type())) {
            this.setPmtType(-1);
        } else {
            this.setPmtType(Integer.valueOf(permit_dobj.getPmt_type()));
        }
        String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
        if (authorityDobj != null) {
            if (authorityDobj.getSelectedPermitCatg().size() == 1) {
                for (Object obj : authorityDobj.getSelectedPermitCatg()) {
                    permissionPermitCatg = String.valueOf(obj);
                }
            }
        }
        if (authorityDobj == null || ("Any").equalsIgnoreCase(permissionPermitCatg)) {
            for (int j = 0; j < data.length; j++) {
                if (data[j][0].equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                        && Integer.parseInt(data[j][3]) == Integer.parseInt(permit_dobj.getPmt_type())) {
                    pmtCategory.add(new SelectItem(data[j][1], data[j][2]));
                }
            }
        } else {
            for (int j = 0; j < data.length; j++) {
                for (Object obj : authorityDobj.getSelectedPermitCatg()) {
                    if (data[j][0].equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                            && Integer.parseInt(data[j][3]) == Integer.parseInt(permit_dobj.getPmt_type())
                            && data[j][1].equalsIgnoreCase(String.valueOf(obj))) {
                        pmtCategory.add(new SelectItem(data[j][1], data[j][2]));
                    }
                }
            }
        }
        if (CommonUtils.isNullOrBlank(permit_dobj.getPmtCatg())) {
            this.setPmtcatg(-1);
        } else {
            this.setPmtcatg(Integer.valueOf(permit_dobj.getPmtCatg()));
        }
        permit_Dtls_bean.permitComponentReadOnly(true);
        pmtDobj = PermitDetailImpl.getPermitdetails(permit_dobj.getPmt_no());
        permit_Dtls_bean.set_Permit_Dtls_dobj_to_bean(pmtDobj);
        pmt_render = true;
    }

    public void routeCodeDetail(String Appl_no, String TableName, String state_cd, int off_cd, boolean otherOffRouteAllow) {
        try {
            boolean flage = false;
            noOfTripsrendered = false;
            passImp = new PassengerPermitDetailImpl();
            actionSource.clear();
            actionTarget.clear();
            Map<String, String> routeMap = passImp.getSourcesRouteMap(Appl_no, TableName, state_cd, off_cd, otherOffRouteAllow);
            for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                actionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
            Map<String, String> mapRouteList = passImp.getTargetRouteMap(Appl_no, TableName, state_cd, off_cd, otherOffRouteAllow);
            for (Map.Entry<String, String> entry : mapRouteList.entrySet()) {
                flage = true;
                actionTarget.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
            setVia_route("");
            if (flage) {
                setVia_route(passImp.getRouteViaRouteList(Appl_no, state_cd, TableName, off_cd, otherOffRouteAllow));
                setRouteLength(passImp.getRouteLength(null, Appl_no, TableName, state_cd, off_cd));
                noOfTripsrendered = true;
            }
            routeManage = new DualListModel<PermitRouteList>(actionSource, actionTarget);
            if (otherOffRouteAllow) {
                StringBuilder stringBuilder = new StringBuilder();
                String strValue = "";
                Map<String, String> selectedRouteMap = passImp.getOtherOffRouteCode(Appl_no, state_cd, TableName, off_cd);
                Set<String> keySet = selectedRouteMap.keySet();
                Object[] objKey = keySet.toArray();
                selectedOffice = new String[objKey.length];
                for (int i = 0; i < objKey.length; i++) {
                    selectedOffice[i] = objKey[i].toString();
                }
                for (int j = 0; j < selectedOffice.length; j++) {
                    stringBuilder.append("(").append("'").append(selectedOffice[j]).append("'").append("),");
                }

                if (stringBuilder.length() > 0) {
                    strValue = stringBuilder.substring(0, stringBuilder.length() - 1);
                } else {
                    strValue = "0";
                }

                Map<String, VmPermitRouteDobj> routeMaps = passImp.getAllRoutesForPermit(state_cd, strValue);
                if (routeMap != null) {
                    for (VmPermitRouteDobj dobj : routeMaps.values()) {
                        getRoute_list().add(dobj);
                        for (String off : selectedOffice) {
                            String route_cd = selectedRouteMap.get(off);
                            if (off.equals(String.valueOf(dobj.getOff_code())) && ("," + route_cd + ",").contains("," + dobj.getRoute_code() + ",") && TableConstants.OTHER_OFFICE_ROUTE_ALLOWED_FOR_PERMIT.contains(off)) {
                                if (selectedRouteList == null) {
                                    selectedRouteList = new ArrayList<VmPermitRouteDobj>();
                                }
                                selectedRouteList.add(dobj);
                            }
                        }
                    }
                }
            }

        } catch (VahanException e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
        }
    }

    public void otherStateRouteCodeDetail(String Appl_no, String TableName, String state_cd, int off_cd, String state_to) {
        try {
            boolean flage = false;
            passImp = new PassengerPermitDetailImpl();
            otherStateActionSource.clear();
            otherStateActionTarget.clear();
            Map<String, String> routeMap = passImp.getOtherStateSourcesRouteMap(Appl_no, TableName, state_cd, off_cd, state_to);
            for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                otherStateActionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
            Map<String, String> mapRouteList = passImp.getOtherStateTargetRouteMap(Appl_no, TableName, state_cd, off_cd, state_to);
            for (Map.Entry<String, String> entry : mapRouteList.entrySet()) {
                flage = true;
                otherStateActionTarget.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
            setVia_route_oth("");
            if (flage) {
                setVia_route_oth(passImp.getRouteViaRouteList(Appl_no, state_cd, TableName, off_cd, false));
                noOfTripsrendered = true;
            }
            otherStateRouteManage = new DualListModel<PermitRouteList>(otherStateActionSource, otherStateActionTarget);

        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
        }
    }

    public void areaCodeDetail(String state_cd, String stringBuilder, int off_cd) {
        passImp = new PassengerPermitDetailImpl();
        areaActionSource.clear();
        areaActionTarget.clear();
        if (CommonUtils.isNullOrBlank(stringBuilder) || stringBuilder.equals("-1") || stringBuilder.isEmpty() || ("('')").equalsIgnoreCase(stringBuilder)) {
            Map<String, String> routeMap = passImp.getTargetAreaMap(state_cd, off_cd);
            for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                areaActionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
        } else {
            String tempStr = stringBuilder;
            tempStr = tempStr.replace("('", "").replace("')", "");
            String[] temp = tempStr.split(",");
            if (JSFUtils.isNumeric(temp[0])) {
                Map<String, String> routeMap = passImp.getSourcesAreaMapWithStrBuil(stringBuilder, state_cd, off_cd);
                for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                    areaActionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
                }
                Map<String, String> mapRouteList = passImp.getTargetAreaMapWithStrBuil(stringBuilder, state_cd, off_cd);
                for (Map.Entry<String, String> entry : mapRouteList.entrySet()) {
                    areaActionTarget.add(new PermitRouteList(entry.getKey(), entry.getValue()));
                }
            }
        }
        areaManage = new DualListModel<PermitRouteList>(areaActionSource, areaActionTarget);

    }

    public void check_Time_Period() {
        if (("M").equalsIgnoreCase(getPeriodMode()) || ("Y").equalsIgnoreCase(getPeriodMode())) {
            setPeriodValue(null);
            setPeriodValueDisable(false);
        } else if (("L").equalsIgnoreCase(getPeriodMode())) {
            setPeriodValue(0);
            setPeriodValueDisable(true);
        } else {
            setPeriodValue(null);
            setPeriodValueDisable(true);
        }
    }

    public void checkReservation() {
        try {

            if (getRegn_no().trim().equalsIgnoreCase("NEW") && (!Boolean.parseBoolean(stateConfigMap.get("pmt_reservation_allow")))
                    && (getPmtType() != -1 || getPmtType() != 0) && (getPmtcatg() != -1 || getPmtcatg() != 0)) {
                resList = PermitReservationImpl.getPermitReservationDtls(sessionVariables.getStateCodeSelected(), getPmtType(), getPmtcatg(), ownerBean.getOwner_catg(), 0, 0, 0, "0");
            }
            PassengerPermitDetailDobj parametersPmtDobj = new PassengerPermitDetailDobj();
            if (getPmtType() == -1 || getPmtType() == 0) {
                parametersPmtDobj.setPmt_type_code("-1");
            } else {
                parametersPmtDobj.setPmt_type_code(String.valueOf(getPmtType()));
            }
            if (getPmtcatg() == -1 || getPmtcatg() == 0) {
                parametersPmtDobj.setPmtCatg("-1");
            } else {
                parametersPmtDobj.setPmtCatg(String.valueOf(getPmtcatg()));
            }
            setReplacementDate(parametersPmtDobj);
            if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("TN") && getPmtType() == TableConstants.STAGE_CARRIAGE_PERMIT) {
                int owner_cd = 0;
                if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_VERIFICATION || sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPROVE) {
                    if (permit_owner_detail_dobj.getOwner_cd() > 0) {
                        owner_cd = permit_owner_detail_dobj.getOwner_cd();
                    }
                } else {
                    owner_cd = ownerDetail == null ? 0 : ownerDetail.getOwner_cd();
                }
                freezePeriod(getPmtPurCode(), getPmtType(), getPmtcatg(), null, renderhypth, owner_cd);
                period.clear();
                String[][] data = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
                for (int i = 0; i < data.length; i++) {
                    if (("M").equalsIgnoreCase(data[i][0]) || ("Y").equalsIgnoreCase(data[i][0]) || ("L").equalsIgnoreCase(data[i][0])) {
                        period.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
            }
            try {
                Map<String, Boolean> visibleRestrictionAreaRoute = new PassengerPermitDetailImpl().visibleRestrictionAreaRoute(sessionVariables.getStateCodeSelected(), getPmtPurCode(), getPmtType(), getPmtcatg());
                if (visibleRestrictionAreaRoute != null) {
                    visibleAreaDtls = visibleRestrictionAreaRoute.get("show_region_dtls");
                    visibleRouteDtls = visibleRestrictionAreaRoute.get("show_route_dtls");
                }
            } catch (VahanException e) {
                JSFUtils.showMessage(e.getMessage());
            }

            VehicleParameters vchparameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            vchparameters.setPERMIT_TYPE(Integer.parseInt(parametersPmtDobj.getPmt_type_code()));
            vchparameters.setPERMIT_SUB_CATG(Integer.parseInt(parametersPmtDobj.getPmtCatg()));;
            if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("show_other_state_route"), vchparameters), "checkReservation")) {
                visibleInterStateRouteDtls = true;
            } else {
                visibleInterStateRouteDtls = false;
            }
            if (CommonUtils.isNullOrBlank(appl_details.getAppl_no())) {
                otherStateRouteCodeDetail("NULL", TableList.vt_permit_route, sessionVariables.getStateCodeSelected(), permitIssuingAuthCode, state_to);
            }
        } catch (Exception e) {
            resList = null;
        }
    }

    public void setReplacementDate(PassengerPermitDetailDobj parametersPmtDobj) {
        try {
            if (!CommonUtils.isNullOrBlank(parametersPmtDobj.getRegion_covered())) {
                String str = parametersPmtDobj.getRegion_covered();
                String[] temp = str.split(",");
                if (temp.length > 0) {
                    parametersPmtDobj.setRegion_covered(String.valueOf(temp.length));
                }
            }
            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, parametersPmtDobj, 0, 0, 0, 0, 0, 0, 0, 0);
            PassengerPermitDetailImpl impl = new PassengerPermitDetailImpl();
            if (ownerDetail != null) {
                parameters.setVH_CLASS(ownerDetail.getVh_class());
            }
            Map<String, String> replacementDtls = impl.getReplacementDtls(sessionVariables.getStateCodeSelected(), getPmtPurCode(), parameters);
            if (replacementDtls != null) {
                if (replacementDtls.get("period_mode").contains("Y") && ownerDetail != null && !CommonUtils.isNullOrBlank(ownerDetail.getRegn_dt())) {
                    setDateOfReplacement(ServerUtil.dateRange(JSFUtils.getStringToDateyyyyMMdd(ownerDetail.getRegn_dt()), Integer.valueOf(replacementDtls.get("period")), 0, -1));
                    setReplacementDateDisable(true);
                }
            } else if (getPmtPurCode() == TableConstants.VM_PMT_RENEWAL_PUR_CD && null != getDateOfReplacement()) {
                setDateOfReplacement(getDateOfReplacement());
                setReplacementDateDisable(true);
            } else {
                setDateOfReplacement(null);
                setReplacementDateDisable(false);
            }
        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void save_details() {
        FacesMessage message = null;
        InsDobj ins_dobj = null;
        Owner_dobj own_dobj = new Owner_dobj();
        ownerImpl = new PermitOwnerDetailImpl();
        boolean multiApplCondition = false;
        boolean allowPendingFitness = false;
        boolean fitnessApplFound = false;
        boolean fitnessValid = false;
        try {

            List listRouteTime = getPmtTimeTableBean().getTimeTableList();
            if (listRouteTime.size() > 0) {
                formatRouteTimeTable(listRouteTime);
            }
            if (getPmtPurCode() == 0) {
                throw new VahanException("Invalid purpose found");
            }
            if ((getPmtPurCode() == TableConstants.VM_PMT_FRESH_PUR_CD || getPmtPurCode() == TableConstants.VM_PMT_APPLICATION_PUR_CD) && getPmtType() == TableConstants.STAGE_CARRIAGE_PERMIT && showTimeTableDetails && !showTimeTable) {
                throw new VahanException("Please Click on check Box for Time Table Entry");
            }
            String app_no = "";
            String errorMsg = "";
            String pendingApplication = ServerUtil.applicationStatusForPermit(getRegn_no(), sessionVariables.getStateCodeSelected());
            VehicleParameters vchparameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            vchparameters.setPERMIT_TYPE(getPmtType());
            if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("multi_appl_condition"), vchparameters), "save_details")) {
                multiApplCondition = true;
            }

            if (getPmtPurCode() == TableConstants.VM_PMT_RENEWAL_PUR_CD && !CommonUtils.isNullOrBlank(pendingApplication)) {
                fitnessApplFound = ServerUtil.applicationStatusForFitness(getRegn_no(), sessionVariables.getStateCodeSelected());
            }
            if (getPmtPurCode() == TableConstants.VM_PMT_RENEWAL_PUR_CD && pmtCheckDtsl.getDtlsDobj() != null && !CommonUtils.isNullOrBlank(pmtCheckDtsl.getDtlsDobj().getFitValidTo())) {
                fitnessValid = (DateUtils.compareDates(DateUtils.getCurrentLocalDate(), CommonPermitPrintImpl.getDateDD_MMM_YYYY(pmtCheckDtsl.getDtlsDobj().getFitValidTo())) <= 1);
            }
            vchparameters.setPUR_CD(getPmtPurCode());
            if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("allow_pending_fitness"), vchparameters), "save_details") && fitnessApplFound && fitnessValid) {
                allowPendingFitness = true;
            }

            if (CommonUtils.isNullOrBlank(pendingApplication) || (multiApplAllow && multiApplCondition && getPmtPurCode() == TableConstants.VM_PMT_FRESH_PUR_CD) || (getPmtPurCode() == TableConstants.VM_PMT_RENEWAL_PUR_CD && allowPendingFitness)) {
                if (!(getRegn_no().equalsIgnoreCase("NEW"))) {
                    VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                    parameters.setOWNER_CD(ownerBean.getOwner_cd());
                    if (CommonUtils.isNullOrBlank(ownerBean.getVh_class())) {
                        throw new VahanException("Vehicle class not found.");
                    }
                    parameters.setVH_CLASS(Integer.parseInt(ownerBean.getVh_class()));
                    parameters.setPUR_CD(getPmtPurCode());
                    parameters.setFUEL(ownerBean.getFuel_type());
                    if (ownerDetail != null && !CommonUtils.isNullOrBlank(String.valueOf(ownerDetail.getOther_criteria()))) {
                        parameters.setOTHER_CRITERIA(ownerDetail.getOther_criteria());
                    }
                    if (Boolean.parseBoolean(stateConfigMap.get("insaurance_exempted"))) {
                        if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("insaurance_condition"), parameters), "save_details")) {
                            pmtCheckDtsl.getDtlsDobj().setExemptedIns(true);
                        }
                    }
                    if (Boolean.parseBoolean(stateConfigMap.get("tax_exempted"))) {
                        if (isCondition(FormulaUtils.replaceTagPermitValues(taxExem, parameters), "save_details")) {
                            pmtCheckDtsl.getDtlsDobj().setExemptedTax(true);
                        }
                    }
                    if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("fitness_exempted"), parameters), "save_details")) {
                        pmtCheckDtsl.getDtlsDobj().setExemptedFitness(true);
                    }
                    if (isTaxExemptForKL() && getPmtPurCode() == TableConstants.VM_PMT_FRESH_PUR_CD) {
                        pmtCheckDtsl.getDtlsDobj().setExemptedTax(true);
                    }

                    errorMsg = checkPmtValidation();
                    if (Util.getUserStateCode().equals("KL") && getPmtPurCode() == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
                        if (!CommonUtils.isNullOrBlank(errorMsg) && errorMsg.equalsIgnoreCase("Please pay MV Tax first.")) {
                            errorMsg = CommonPermitPrintImpl.checkPmtGracePeriod(pmtCheckDtsl.getDtlsDobj(), getPmtType());
                        }
                    }

                    if (CommonUtils.isNullOrBlank(errorMsg) && renderGoodsPassengerTax) {
                        errorMsg = CommonPermitPrintImpl.checkGoodsPassengerTaxValidation(pmtCheckDtsl.getDtlsDobj());
                    }

                    if (pmtCheckDtsl.getDtlsDobj() != null && pmtCheckDtsl.getDtlsDobj().isInsSaveData() && !pmtCheckDtsl.getDtlsDobj().isExemptedIns()) {
                        if (CommonUtils.isNullOrBlank(pmtCheckDtsl.getDtlsDobj().getInsFrom())
                                || CommonUtils.isNullOrBlank(pmtCheckDtsl.getDtlsDobj().getInsUpto())) {
                            throw new VahanException("Please fill the vehile insurance details");
                        }
                        ins_dobj = new InsDobj();
                        ins_dobj.setAppl_no(app_no);
                        ins_dobj.setIns_from(CommonPermitPrintImpl.getDateDD_MMM_YYYY(pmtCheckDtsl.getDtlsDobj().getInsFrom()));
                        ins_dobj.setIns_upto(CommonPermitPrintImpl.getDateDD_MMM_YYYY(pmtCheckDtsl.getDtlsDobj().getInsUpto()));
                        if (CommonUtils.isNullOrBlank(pmtCheckDtsl.getDtlsDobj().getInsCmpyNo())) {
                            throw new VahanException("Please fill the vehile insurance company");
                        } else {
                            ins_dobj.setComp_cd(Integer.valueOf(CommonPermitPrintImpl.getValueForSelectedList((ArrayList) arrayInsCmpy, pmtCheckDtsl.getDtlsDobj().getInsCmpyNo())));
                        }
                        if (CommonUtils.isNullOrBlank(pmtCheckDtsl.getDtlsDobj().getInsType())) {
                            throw new VahanException("Please fill the vehile insurance type");
                        } else {
                            ins_dobj.setIns_type(Integer.valueOf(CommonPermitPrintImpl.getValueForSelectedList((ArrayList) arrayInsType, pmtCheckDtsl.getDtlsDobj().getInsType())));
                        }
                        ins_dobj.setPolicy_no(pmtCheckDtsl.getDtlsDobj().getInsPolicyNo());
                        ins_dobj.setIdv(Long.parseLong(pmtCheckDtsl.getDtlsDobj().getInsIdv()));
                    }
                }
                if (!(getRegn_no().equalsIgnoreCase("NEW")) && sessionVariables.getStateCodeSelected().equalsIgnoreCase("RJ")) {
                    FitnessImpl fitImpl = new FitnessImpl();
                    own_dobj.setState_cd(sessionVariables.getStateCodeSelected());
                    own_dobj.setRegn_dt(DateUtils.parseDate(ownerBean.getRegn_dt()));
                    own_dobj.setPmt_type(getPmtType());
                    own_dobj.setPmt_catg((getPmtcatg() != -1 || getPmtcatg() != 0) ? getPmtcatg() : 0);
                    own_dobj.setFuel(ownerBean.getFuel_type());
                    int vehAge = fitImpl.getVehAgeValidity(own_dobj);
                    if (vehAge != 99) {
                        Date maxValidUpto = ServerUtil.dateRange(own_dobj.getRegn_dt(), vehAge, 0, -1);
                        if (maxValidUpto != null && maxValidUpto.compareTo(new Date()) <= 0) {
                            throw new VahanException("Vehicle age of this vehicle is expired.");
                        }
                    }
                }
                passImp = new PassengerPermitDetailImpl();
                if (getPmtPurCode() == TableConstants.VM_PMT_FRESH_PUR_CD
                        || getPmtPurCode() == TableConstants.VM_PMT_APPLICATION_PUR_CD) {
                    if (resList != null) {
                        if (resList.size() > 1) {
                            errorMsg = "Permit application is not genrated as reservation process is not clear, Please contact administrator department ";
                        } else if (getRegn_no().equalsIgnoreCase("NEW") || Boolean.parseBoolean(stateConfigMap.get("pmt_reservation_allow"))) {
                            for (PermitReservationDobj dobj : resList) {
                                if (dobj.getRunning_no() >= dobj.getMax_no()) {
                                    errorMsg = "Reservation under this category already reached to upper limit.";
                                }
                            }
                        }
                    }
                    if (CommonUtils.isNullOrBlank(errorMsg)) {
                        owner_dobj = new PermitOwnerDetailDobj();
                        PassengerPermitDetailDobj passPermit = set_data_in_dobj();
                        if (passPermit != null) {
                            if (!(passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)
                                    || passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.AITP))) {
                                auth_dobj = null;
                            } else if ((passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)
                                    || passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.AITP)) && notFillHomeAuth) {
                                if (auth_dobj.getRegionCoveredNP() <= 0) {
                                    passPermit.setRegion_covered("");
                                } else {
                                    passPermit.setRegion_covered(String.valueOf(auth_dobj.getRegionCoveredNP()) + ",");
                                }
                                auth_dobj = null;
                            }
                            if (!(getRegn_no().equalsIgnoreCase("NEW"))) {
                                owner_dobj = ownerImpl.set_VA_Owner_permit_to_dobj(null, passPermit.getRegnNo());
                                //  owner_dobj = ownerBean.setValueinDOBJ();
                            } else {
                                owner_dobj = ownerBean.setValueinDOBJ();
                            }
                            own_dobj = ownerImpl.getPermitOwnerData(owner_dobj);
                            if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("OR") && TableConstants.GCW_VEH_CLASS.contains("," + own_dobj.getVh_class() + ",")) {
                                if (own_dobj.getGcw() == 0) {
                                    own_dobj.setGcw(ownerImpl.setGcwWeight(own_dobj));
                                }
                            }
                            String[] values = CommonPermitPrintImpl.getPmtValidateMsg(sessionVariables.getStateCodeSelected(), TableConstants.VM_PMT_FRESH_PUR_CD, own_dobj, passPermit);
                            if (values != null && !CommonUtils.isNullOrBlank(values[1])) {
                                throw new VahanException(values[1]);
                            }

                            if (pmtCategory.size() > 0 && (getPmtcatg() == 0 || getPmtcatg() == -1)) {
                                throw new VahanException("Please select permit category");
                            }

                            if (owner_dobj.getVh_class() == -1) {
                                throw new VahanException("Vehicle Class not found , Please select Vehicle Class");
                            }

                            if (pmtCategory.size() > 0 && !CommonUtils.isNullOrBlank(passPermit.getPmtCatg()) && !CommonUtils.isNullOrBlank(passPermit.getPmt_type_code()) && passImp.getpmtCatgstatus(Integer.parseInt(passPermit.getPmt_type_code()), Integer.parseInt(passPermit.getPmtCatg()))) {
                                throw new VahanException("Please select permit category according to permit type");
                            }
                            if ("DL,UP,GJ".contains(sessionVariables.getStateCodeSelected()) && passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)) {
                                PermitHomeAuthDobj np_dobj = ServerUtil.getPermitDetailsFromNp(passPermit.getRegnNo().toUpperCase().trim());
                                if (np_dobj != null && !CommonUtils.isNullOrBlank(np_dobj.getAuthNo()) && DateUtils.compareDates(np_dobj.getAuthUpto(), new Date()) == 1) {
                                    auth_dobj = new PermitHomeAuthDobj();
                                    JSFUtils.showMessagesInDialog("Information", "First Cancel NP Permit from NP-Portal", FacesMessage.SEVERITY_ERROR);
                                    return;
                                }
                            }
                            if (renderAuthPeriod && (auth_periodMode.equals("-1") || auth_period == -1)) {
                                auth_dobj = new PermitHomeAuthDobj();
                                JSFUtils.showMessagesInDialog("Information", "Please select Auth Period", FacesMessage.SEVERITY_ERROR);
                                return;
                            }

                            app_no = passImp.get_ApplicationNumber(owner_dobj, passPermit, (otherStateRouteManage != null && otherStateRouteManage.getTarget().size() > 0) ? otherStateRouteManage.getTarget() : routeManage.getTarget(), auth_dobj, ins_dobj, getOfferNumber(), getStatecoveredBean(), multiApplAllow, multiApplCondition, isShowOwnerDLAndVoterId(), renderAuthPeriod, auth_periodMode, auth_period, selectedRouteList, listRouteTime);

                            printDialogBox(app_no);
                        }
                    } else {
                        throw new VahanException(errorMsg);
                    }
                } else if (getPmtPurCode() == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
                    if (CommonUtils.isNullOrBlank(errorMsg)) {
                        if (CommonPermitPrintImpl.takeRenewalOfPermit_After(sessionVariables.getStateCodeSelected(), permit_Dtls_bean.getRegn_no(), permit_Dtls_bean.getPmt_no())) {
                            owner_dobj = new PermitOwnerDetailDobj();
                            PassengerPermitDetailDobj passPermit = set_data_in_dobj();
                            if (passPermit != null) {
                                owner_dobj = ownerImpl.set_VA_Owner_permit_to_dobj(null, passPermit.getRegnNo());

//                                String vehAgeDetails = CommonPermitPrintImpl.checkVehicleAge(passPermit.getReplaceDate(), TableConstants.VM_PMT_RENEWAL_PUR_CD);
//                                if (!CommonUtils.isNullOrBlank(vehAgeDetails)) {
//                                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", vehAgeDetails);
//                                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
//                                    return;
//                                }
                                if ((passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)
                                        || passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.AITP))
                                        && CommonUtils.isNullOrBlank(getPrvAuthoDtls())) {
                                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Authorization Details not fetch. Please Contact to system administrator", "Authorization Details not fetch. Please Contact to system administrator");
                                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                                    PrimeFaces.current().executeScript("PF('bui').hide()");
                                } else {
                                    owner_dobj.setDl_no(ownerBean.getDl_no());
                                    owner_dobj.setVoter_id(ownerBean.getVoter_id());

                                    app_no = passImp.get_ApplicationNumber(owner_dobj, passPermit, (otherStateRouteManage != null && otherStateRouteManage.getTarget() != null && otherStateRouteManage.getTarget().size() > 0) ? otherStateRouteManage.getTarget() : routeManage.getTarget(), null, ins_dobj, getOfferNumber(), getStatecoveredBean(), multiApplAllow, multiApplCondition, isShowOwnerDLAndVoterId(), renderAuthPeriod, auth_periodMode, auth_period, selectedRouteList, listRouteTime);
                                    printDialogBox(app_no);
                                }
                            }
                        } else {
                            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Your Permit is Not Expire", "Your Permit is Not Expire");
                            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                            PrimeFaces.current().executeScript("PF('bui').hide()");
                        }
                    } else {
                        throw new VahanException(errorMsg);
                    }
                }
            } else {
                showApplPendingDialogBox(pendingApplication);
            }
        } catch (VahanException e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", e.getMessage());
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            PrimeFaces.current().executeScript("PF('bui').hide()");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " Pur_cd : " + getPmtPurCode() + " " + sessionVariables.getStateCodeSelected() + e.getStackTrace()[0]);
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "There is some problem while saving data");
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
        }
    }

    public void formatRouteTimeTable(List<PermitTimeTableDobj> listRouteTime) throws VahanException {
        Date date1 = null;
        Date date2 = null;
        String newstr = null;
        String originalString = null;
        SimpleDateFormat formatter1 = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");
        int day1 = 0;
        int day2 = 0;
        try {
            for (int i = 0; i < listRouteTime.size(); i++) {
                PermitTimeTableDobj pmtTimeTableDobj = (PermitTimeTableDobj) listRouteTime.get(i);
                day1 = pmtTimeTableDobj.getDay();
                if (day1 == 0) {
                    throw new VahanException("Please enter day number for " + pmtTimeTableDobj.getStoppage());
                }

                if (i == 0 && day1 > 1) {
                    throw new VahanException("Please enter correct day number for " + pmtTimeTableDobj.getStoppage());
                }

                originalString = pmtTimeTableDobj.getRoute_fr_time();
                if (CommonUtils.isNullOrBlank(originalString)) {
                    throw new VahanException("Please select Arrival Time for " + pmtTimeTableDobj.getStoppage());
                }
                if (originalString.equals("00:00") || originalString.equals("00:00:00")) {
                    pmtTimeTableDobj.setRoute_fr_time(originalString);
                    date1 = null;
                } else {
                    date1 = formatter1.parse(originalString);
                    newstr = formatter2.format(date1);
                    pmtTimeTableDobj.setRoute_fr_time(newstr);
                }
                originalString = pmtTimeTableDobj.getRoute_to_time();
                if (CommonUtils.isNullOrBlank(originalString)) {
                    throw new VahanException("Please select Departure Time for " + pmtTimeTableDobj.getStoppage());
                }
                if (originalString.equals("00:00") || originalString.equals("00:00:00")) {
                    pmtTimeTableDobj.setRoute_to_time(originalString);
                    date2 = null;
                } else {
                    date2 = formatter1.parse(originalString);
                    newstr = formatter2.format(date2);
                    pmtTimeTableDobj.setRoute_to_time(newstr);
                }
                if (date1 != null && date2 != null && date1.after(date2)) {
                    throw new VahanException("Departure Time should be greater than Arrival Time for " + pmtTimeTableDobj.getStoppage());
                }

                for (int j = i + 1; j < listRouteTime.size(); j++) {
                    PermitTimeTableDobj pmtTimeTableDobj1 = (PermitTimeTableDobj) listRouteTime.get(j);
                    day2 = pmtTimeTableDobj1.getDay();
                    Date next_fr_dt = null;
                    Date next_to_dt = null;
                    String dt_str1 = "";
                    String dt_str2 = "";
                    if (day2 == 0) {
                        throw new VahanException("Please enter day number for " + pmtTimeTableDobj1.getStoppage());
                    }
                    if (day1 > day2) {
                        throw new VahanException("Previous day should be less than next day for " + pmtTimeTableDobj1.getStoppage());
                    }
                    if (day2 > day1 + 1) {
                        PermitTimeTableDobj pmtTimeTableDobj2 = (PermitTimeTableDobj) listRouteTime.get(j - 1);
                        int prevday = pmtTimeTableDobj2.getDay();
                        if (prevday != day2 - 1) {
                            throw new VahanException("Please enter the correct day number for " + pmtTimeTableDobj1.getStoppage());
                        }
                    }
                    if (day2 == day1) {
                        dt_str1 = pmtTimeTableDobj1.getRoute_fr_time();
                        if (CommonUtils.isNullOrBlank(dt_str1)) {
                            throw new VahanException("Please select Arrival Time for " + pmtTimeTableDobj1.getStoppage());
                        }
                        next_fr_dt = formatter1.parse(dt_str1);

                        dt_str2 = pmtTimeTableDobj1.getRoute_to_time();
                        if (CommonUtils.isNullOrBlank(dt_str2)) {
                            throw new VahanException("Please select Departure Time for " + pmtTimeTableDobj1.getStoppage());
                        }
                        if (dt_str2.equals("00:00") || dt_str2.equals("00:00:00")) {
                            next_to_dt = null;
                        } else {
                            next_to_dt = formatter1.parse(dt_str2);
                        }
                        if (date1 != null && next_fr_dt != null && date1.after(next_fr_dt)) {
                            throw new VahanException("Previous arrival time should be less than next arrival time for " + pmtTimeTableDobj1.getStoppage());
                        }

                        if (date1 != null && next_to_dt != null && date1.after(next_to_dt)) {
                            throw new VahanException("Previous arrival time should be less than next departure time for " + pmtTimeTableDobj1.getStoppage());
                        }

                        if (date2 != null && next_fr_dt != null && date2.after(next_fr_dt)) {
                            throw new VahanException("Previous departure time should be less than next arrival time for " + pmtTimeTableDobj1.getStoppage());
                        }

                        if (date2 != null && next_to_dt != null && date2.after(next_to_dt)) {
                            throw new VahanException("Previous departure time should be less than next departure time for " + pmtTimeTableDobj1.getStoppage());
                        }

                        if (next_fr_dt != null && next_to_dt != null && next_fr_dt.after(next_to_dt)) {
                            throw new VahanException("Departure Time should be greater than Arrival Time for " + pmtTimeTableDobj1.getStoppage());
                        }

                    } else if (day2 > day1) {
                        dt_str1 = pmtTimeTableDobj1.getRoute_fr_time();
                        if (CommonUtils.isNullOrBlank(dt_str1)) {
                            throw new VahanException("Please select Arrival Time for " + pmtTimeTableDobj1.getStoppage());
                        }

                        dt_str2 = pmtTimeTableDobj1.getRoute_to_time();
                        if (CommonUtils.isNullOrBlank(dt_str2)) {
                            throw new VahanException("Please select Departure Time for " + pmtTimeTableDobj1.getStoppage());
                        }
                        //  day1 = day2;
                    }
                }

            }
        } catch (Exception ex) {
            throw new VahanException(ex.getMessage());
        }
    }

    public String checkPmtValidation() {
        String msg = null;
        msg = CommonPermitPrintImpl.checkPmtValidation(pmtCheckDtsl.getDtlsDobj());
        return msg;
    }

    public void printDialogBox(String app_no) {
        if (("").equalsIgnoreCase(app_no)) {
            setApp_no_msg("Application Number is not genrated");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        } else {
            setApplNoForAck(app_no);
            setApp_no_msg("Application Number generated :" + app_no + ". Please note down the Application No for future reference.");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        }
    }

    public void showApplPendingDialogBox(String pendingApplication) {
        setApp_no_msg(pendingApplication);
        PrimeFaces.current().ajax().update("app_num_id");
        PrimeFaces.current().executeScript("PF('appNum').show();");
    }

    public String update_details(Status_dobj status_dobj, String CompairChange, String action_cd, boolean otherOffRouteAllow, List<VmPermitRouteDobj> otherOffRoutecode, List listRouteTime) throws VahanException {
        String return_location = "";
        try {
            String appl_no = appl_details.getAppl_no();
            passImp = new PassengerPermitDetailImpl();
            PassengerPermitDetailDobj passPermit = setInDOBJ();
            owner_dobj = new PermitOwnerDetailDobj();
            owner_dobj = ownerBean.setValueinDOBJ();

            if (!CommonUtils.isNullOrBlank(getPermit_detail_dobj().getNumberOfTrips())) {
                passPermit.setNumberOfTrips(getPermit_detail_dobj().getNumberOfTrips());
            }

            if (!(passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)
                    || passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.AITP))) {
                auth_dobj = null;
                if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_APPLICATION_APPROVAL))
                        || action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_APPLICATION_VERIFICATION))) {
                    List area = areaManage.getTarget();
                    List route = routeManage.getTarget();
                    List otherStateRoute = otherStateRouteManage != null ? otherStateRouteManage.getTarget() : new ArrayList();

                    if (area.isEmpty() && route.isEmpty() && otherStateRoute.isEmpty()) {
                        throw new VahanException("Please select any Route/Area Details.");
                    }

                    if (!route.isEmpty() && !otherStateRoute.isEmpty()) {
                        throw new VahanException("Please select either state route or InterState route Details.");
                    }

                }
            } else if ((passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT))
                    || (passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.AITP))) {
                if (auth_dobj == null || auth_dobj.getRegionCoveredNP() <= 0) {
                    passPermit.setRegion_covered("");
                } else {
                    passPermit.setRegion_covered(String.valueOf(auth_dobj.getRegionCoveredNP()) + ",");
                }
                if (passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.AITP)
                        && CommonUtils.isNullOrBlank(passPermit.getRegion_covered())
                        && statecoveredBean.getStateList().size() > 0) {
                    passPermit.setRegion_covered(statecoveredBean.getStateList().toString().subSequence(1, statecoveredBean.getStateList().toString().length() - 1) + ",");
                }
                if (notFillHomeAuth) {
                    auth_dobj = null;
                }
//                if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("DL")
//                        && passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)) {
//                    PermitHomeAuthDobj auth_dobj_permit = ServerUtil.getTemporaryNPDetails(regn_no, sessionVariables.getStateCodeSelected(), appl_no);
//                    if (auth_dobj_permit != null) {
//                        PermitHomeAuthDobj auth_dobj_np = ServerUtil.getPermitDetailsFromNp(regn_no);
//                        if (auth_dobj_np != null) {
//                            if (!(auth_dobj_permit.getAuthFrom().equals(auth_dobj_np.getAuthFrom())
//                                    && auth_dobj_permit.getAuthUpto().equals(auth_dobj_np.getAuthUpto()))) {
//                                FacesContext.getCurrentInstance().addMessage("paymentMessageId", new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Frist Pay the MORTH FEES From NP Portal"));
//                                return "";
//                            }
//                        }
//                    }
//                }
            }

            String value = "";
            if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_OFFER_VERIFICATION))) {
                value = passImp.updateOfferDetailsInAllTables(owner_dobj, passPermit, auth_dobj, routeManage.getTarget(), appl_no, status_dobj, CompairChange, action_cd, getNew_regn_no(), isShowOwnerDLAndVoterId(), otherOffRouteAllow, otherOffRoutecode);
            } else {
                value = passImp.update_in_all_tables(owner_dobj, passPermit, auth_dobj, (otherStateRouteManage != null && otherStateRouteManage.getTarget() != null && otherStateRouteManage.getTarget().size() > 0) ? otherStateRouteManage.getTarget() : routeManage.getTarget(), appl_no, status_dobj, CompairChange, action_cd, getNew_regn_no(), isShowOwnerDLAndVoterId(), renderAuthPeriod, auth_periodMode, auth_period, statecoveredBean, otherOffRouteAllow, otherOffRoutecode, listRouteTime);
            }
            if (!CommonUtils.isNullOrBlank(value) && sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_APPROVAL && !sessionVariables.getStateCodeSelected().equalsIgnoreCase("TR")) {
                setMsgOfPrintDocument("Offer Letter Is Genrated. The offer letter no. is " + value);
                setValueOfCommandButton("Print Offer Letter");
                PrimeFaces.current().ajax().update("printDocument");
                PrimeFaces.current().executeScript("PF('successDialog').show();");
                return_location = "";
            } else if (status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING) && CommonUtils.isNullOrBlank(value)) {
                return_location = disapprovalPrint();
            } else {
                return_location = "seatwork";
            }
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", new FacesMessage(FacesMessage.SEVERITY_INFO, "", ex.getMessage()));
            return_location = "";
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return return_location;
    }

    public String approveThePermit(Status_dobj status_dobj, PassengerPermitDetailDobj passPermit) {
        String redirectPage = "";
        try {
            String appl_no = appl_details.getAppl_no();
            OwnerImpl ownImpl = new OwnerImpl();
            Owner_dobj ownDobj = ownImpl.set_Owner_appl_db_to_dobj(appl_details.getRegn_no(), null, null, 0);
            passImp.approval_the_permit(appl_no, status_dobj, ownDobj, passPermit, renderAuthPeriod, auth_periodMode, String.valueOf(auth_period), statecoveredBean, (showTimeTableDetails && getPmtTimeTableBean().getTimeTableList().size() > 0));
            if (status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING)) {
                redirectPage = disapprovalPrint();
            } else {
                redirectPage = "seatwork";
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Information", e.getMessage(), FacesMessage.SEVERITY_INFO);
            redirectPage = "";
        } catch (Exception e) {
            JSFUtils.showMessagesInDialog("Error", e.getMessage(), FacesMessage.SEVERITY_ERROR);
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            redirectPage = "";
        }
        return redirectPage;
    }

    public void check_Time_Period_auth() {
        auth_period_list.clear();
        if (("M").equalsIgnoreCase(getAuth_periodMode())) {
            auth_period_list.add(0, "3");
            auth_period_list.add(1, "6");
        } else if (("Y").equalsIgnoreCase(getAuth_periodMode())) {
            auth_period_list.add(0, "1");
        } else if (("D").equalsIgnoreCase(getAuth_periodMode())) {
            auth_period_list.add(0, "30");
        }
    }

    public void reback(Status_dobj status, String CompairChange) {
        try {
            String appl_no = appl_details.getAppl_no();
            passImp = new PassengerPermitDetailImpl();
            PassengerPermitDetailDobj passPermit = setInDOBJ();
            owner_dobj = new PermitOwnerDetailDobj();
            owner_dobj = ownerBean.setValueinDOBJ();
            passImp.rebackStatus(owner_dobj, status, CompairChange, passPermit, routeManage.getTarget(), appl_no, otherRouteAllow, selectedRouteList);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void editPmtTyeAndCatg() {
        setPmtTypeDisable(false);
        setPmtCatgDisable(false);
    }

    public String approveRenwalOfPermit(Status_dobj status_dobj, PassengerPermitDetailDobj passPermit) {
        String redirectPage = "";
        try {
            String appl_no = appl_details.getAppl_no();
            OwnerImpl ownImpl = new OwnerImpl();
            if (passPermit == null) {
                throw new VahanException("There are some problem featching the permit details.");
            }
            Owner_dobj ownDobj = ownImpl.set_Owner_appl_db_to_dobj(appl_details.getRegn_no(), null, null, 0);
            passImp.approval_Renewal_of_permit(appl_no, status_dobj, ownDobj, passPermit);
            if (status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING)) {
                redirectPage = disapprovalPrint();
            } else {
                redirectPage = "seatwork";
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Information", e.getMessage(), FacesMessage.SEVERITY_INFO);
            redirectPage = "";
        } catch (Exception e) {
            JSFUtils.showMessagesInDialog("Exception", e.getMessage(), FacesMessage.SEVERITY_ERROR);
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            redirectPage = "";
        }
        return redirectPage;
    }

    public void applPermitReadOnly(boolean flag) {
        int purCd = 0;
        setRegnNoDisable(flag);
        if (getPmtPurCode() == 0) {
            purCd = appl_details.getPur_cd();
        } else {
            purCd = getPmtPurCode();
        }
        if (!((purCd == TableConstants.VM_PMT_RENEWAL_PUR_CD)
                && sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY)) {
            setPeriodModeDisable(flag);
            setPeriodValueDisable(flag);
            goodsToCarryDisabled = flag;
            parkingDisabled = flag;
            jorenyPurposedDisabled = flag;
        }
        if ((purCd == TableConstants.VM_PMT_RENEWAL_PUR_CD)
                && (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_RENWAL_VERIFICATION) && sessionVariables.getStateCodeSelected().equalsIgnoreCase("KL")) {
            goodsToCarryDisabled = false;
            parkingDisabled = false;
            jorenyPurposedDisabled = false;
        }
        setPmtTypeDisable(flag);
        this.getDomain().setDisabled(flag);
        if (areaManage.getTarget().size() > 0) {
            setDisableAreaDtls(flag);
            setDisableRoutDtls(flag);
        }
        if (routeManage.getTarget().size() > 0) {
            setDisableAreaDtls(flag);
            setDisableRoutDtls(flag);
        }
        this.setServicesTypeDisable(flag);
        this.getNumber_OF_TRIPS().setDisabled(flag);
        this.getRouteCode().setDisabled(flag);
        if ((purCd == TableConstants.VM_PMT_RENEWAL_PUR_CD)
                && (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY)
                && (getPmtcatg() == -1 || getPmtcatg() == 0)) {
            setPmtCatgDisable(!flag);
        } else {
            setPmtCatgDisable(flag);
        }
        if ((purCd == TableConstants.VM_PMT_APPLICATION_PUR_CD || purCd == TableConstants.VM_PMT_FRESH_PUR_CD)
                && (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY)) {
            setPmtCatgDisable(!flag);
        }
    }

    public PassengerPermitDetailDobj set_data_in_dobj() {
        FacesMessage message = null;
        PassengerPermitDetailDobj passPermit = null;
        try {
            passPermit = new PassengerPermitDetailDobj();
            passPermit.setRegnNo(this.getRegn_no());
            passPermit.setPmt_no(this.getPermit_no());
            if (getPmtType() == -1 || getPmtType() == 0) {
                throw new VahanException("Plesase select the Permit Type.");
            }
            if (this.getPeriodValue() == null || (this.getPeriodValue() == 0 && !("L").equalsIgnoreCase(getPeriodMode()))) {
                throw new VahanException("Period is empty.");
            }
            if (("M").equalsIgnoreCase(getPeriodMode()) || ("Y").equalsIgnoreCase(getPeriodMode()) || ("L").equalsIgnoreCase(getPeriodMode())) {
                passPermit.setPeriod_mode(getPeriodMode().toUpperCase());
            } else {
                throw new VahanException("Plesase select the Period.");
            }
            if (getPmtPurCode() == -1) {
                throw new VahanException("Plesase select the Permit Action.");
            }
            passPermit.setReplaceDate(getDateOfReplacement());
            passPermit.setState_cd(sessionVariables.getStateCodeSelected());
            passPermit.setOff_cd(Integer.toString(sessionVariables.getOffCodeSelected()));
            if (CommonUtils.isNullOrBlank(getGoodsToCarry())) {
                passPermit.setGoods_TO_CARRY("");
            } else {
                passPermit.setGoods_TO_CARRY(getGoodsToCarry().toUpperCase());
            }

            if (CommonUtils.isNullOrBlank(getJoreny_PURPOSE())) {
                passPermit.setJoreny_PURPOSE("");
            } else {
                passPermit.setJoreny_PURPOSE(getJoreny_PURPOSE().toUpperCase());
            }

            if (getNumber_OF_TRIPS().getValue() == null) {
                passPermit.setNumberOfTrips("0");
            } else {
                passPermit.setNumberOfTrips(((String) getNumber_OF_TRIPS().getValue()).toUpperCase());
            }
            if (CommonUtils.isNullOrBlank(parking)) {
                passPermit.setParking("");
            } else {
                passPermit.setParking(getParking().toUpperCase());
            }
            if (getNo_of_trip() == 0) {
                passPermit.setNumberOfTrips("0");
            } else {
                passPermit.setNumberOfTrips(String.valueOf(getNo_of_trip()));
            }
            passPermit.setPaction_code(String.valueOf(getPmtPurCode()));
            if (getPeriodValue() == null) {
                passPermit.setPeriod("0");
            } else {
                passPermit.setPeriod(String.valueOf(getPeriodValue()));
            }

            passPermit.setPmt_type_code(String.valueOf(getPmtType()));
            if (getPmtcatg() != -1 || getPmtcatg() != 0) {
                passPermit.setPmtCatg(String.valueOf(getPmtcatg()));
            } else {
                passPermit.setPmtCatg("-1");
            }
            passPermit.setServices_TYPE(String.valueOf(getServices_type()));
            passPermit.setRegnNo(getRegn_no().toUpperCase());
            if (isRender_valid_from() && sessionVariables.getStateCodeSelected().equalsIgnoreCase("OR")) {
                passPermit.setValid_from(getPmt_valid_from());
            }
            if (getPmtPurCode() == TableConstants.VM_PMT_RENEWAL_PUR_CD && (getPmtType() == Integer.valueOf(TableConstants.AITP) || getPmtType() == Integer.valueOf(TableConstants.NATIONAL_PERMIT))) {
                passPermit.setRegion_covered(getRegionCovered() + ",");
            }
            if ((getPmtType() != Integer.valueOf(TableConstants.AITP))
                    && (getPmtType() != Integer.valueOf(TableConstants.NATIONAL_PERMIT))) {
                String region = "";
                List area = areaManage.getTarget();
                List route = routeManage.getTarget();
                List otherStateRoute = otherStateRouteManage != null ? otherStateRouteManage.getTarget() : new ArrayList();

                if (area.isEmpty() && route.isEmpty() && otherStateRoute.isEmpty()) {
                    throw new VahanException("Please select any Route/Area Details.");
                }

                if (!route.isEmpty() && !otherStateRoute.isEmpty()) {
                    throw new VahanException("Please select either state route or InterState route Details.");
                }

                Map<String, Boolean> routeRegionRestriction = new PassengerPermitDetailImpl().multiRouteRegionRestriction(sessionVariables.getStateCodeSelected(), getPmtPurCode());
                if (routeRegionRestriction != null) {
                    if (routeRegionRestriction.get("multi_region_allowed") && (area.size() > 1)) {
                        throw new VahanException("Please select only one area. Multiple region/area not allowed.");
                    }
                    if (routeRegionRestriction.get("multi_route_allowed") && (route.size() > 1)) {
                        throw new VahanException("Please select only one route. Multiple route not allowed.");
                    }
                }
                for (Object s : area) {
                    if (s instanceof String) {
                        region = region + (String) s + ",";
                    } else if (s instanceof PermitRouteList) {
                        region = region + ((PermitRouteList) s).getKey() + ",";
                    }
                }
                passPermit.setRegion_covered(region);
            }

            if (TableConstants.VM_PMT_FRESH_PUR_CD == Integer.valueOf(pmtPurCode)
                    && !this.notFillHomeAuth
                    && (getPmtType() == Integer.valueOf(TableConstants.AITP)
                    || getPmtType() == Integer.valueOf(TableConstants.NATIONAL_PERMIT))) {
                if (CommonUtils.isNullOrBlank(auth_dobj.getAuthNo())) {
                    throw new VahanException("Please fill the authorization number.");
                }
                if (auth_dobj.getAuthFrom() == null) {
                    throw new VahanException("Please fill the authorization form date.");
                }
                if (auth_dobj.getAuthUpto() == null) {
                    throw new VahanException("Please fill the authorization upto date.");
                }
            }
            if (getPmtType() == Integer.valueOf(TableConstants.AITP)
                    && ((auth_dobj != null) && (auth_dobj.getRegionCoveredNP() != 0))
                    && !statecoveredBean.getStateList().isEmpty()) {
                throw new VahanException("Allowed only one criteria select b/w Area Details and Travelling state zone");
            }
        } catch (VahanException ex) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "", ex.getMessage());
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            return null;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0] + "Regn no--" + this.getRegn_no());
            return null;
        }
        return passPermit;
    }

    public PassengerPermitDetailDobj setInDOBJ() throws VahanException {
        PassengerPermitDetailDobj passPermit = null;
        try {
            passPermit = new PassengerPermitDetailDobj();
            passPermit.setDomain_CODE(((String) this.getDomain().getValue()).toUpperCase());
            passPermit.setRegnNo(this.getRegn_no());
            passPermit.setOff_cd(Integer.toString(sessionVariables.getOffCodeSelected()));
            passPermit.setState_cd(sessionVariables.getStateCodeSelected());
            if (!CommonUtils.isNullOrBlank(getGoodsToCarry())) {
                passPermit.setGoods_TO_CARRY(getGoodsToCarry().toUpperCase());
            }
            if (!CommonUtils.isNullOrBlank(getJoreny_PURPOSE())) {
                passPermit.setJoreny_PURPOSE(getJoreny_PURPOSE().toUpperCase());
            }
            if (this.getPmtPurCode() != 0 && this.getPmtPurCode() != -1) {
                passPermit.setPaction_code(String.valueOf(this.getPmtPurCode()));
            }
            if (!CommonUtils.isNullOrBlank(parking)) {
                passPermit.setParking(getParking().toUpperCase());
            }
            passPermit.setPeriod(String.valueOf(this.getPeriodValue()));
            passPermit.setPeriod_mode(this.getPeriodMode().toUpperCase());
            passPermit.setPmt_type_code(String.valueOf(this.getPmtType()));
            if (getPmtcatg() == -1 || getPmtcatg() == 0) {
                passPermit.setPmtCatg("-1");
            } else {
                passPermit.setPmtCatg(String.valueOf(getPmtcatg()));
            }
            passPermit.setServices_TYPE(String.valueOf(this.getServices_type()));
            passPermit.setPaction(String.valueOf(this.getPmtPurCode()));
            String region = "";
            List area = areaManage.getTarget();
            for (Object ss : area) {
                if (ss instanceof String) {
                    region = region + (String) ss + ",";
                } else if (ss instanceof PermitRouteList) {
                    region = region + ((PermitRouteList) ss).getKey() + ",";
                }
            }
            if (CommonUtils.isNullOrBlank(region) && auth_dobj != null && auth_dobj.getRegionCoveredNP() > 0) {
                region = auth_dobj.getRegionCoveredNP() + ",";
            }
            passPermit.setRegion_covered(region);
            passPermit.setNumberOfTrips(String.valueOf(getNo_of_trip()));
            if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_APPROVAL) {
                passPermit.setAppDisboolenValue(this.getApp_disapp());
                passPermit.setOrder_by(this.getOrder_by());
                passPermit.setOrder_no(this.getOrder_no());
                passPermit.setOrder_dt(this.getOrder_dt());
                if (CommonUtils.isNullOrBlank(getReason())) {
                    passPermit.setRemarks("");
                } else {
                    passPermit.setRemarks(this.getReason());
                }
            }

            passPermit.setReplaceDate(this.dateOfReplacement);
            if ((areaManage.getTarget().isEmpty() && routeManage.getTarget().isEmpty() && (otherStateRouteManage != null && otherStateRouteManage.getTarget().isEmpty()))
                    && ((getPmtType() != Integer.valueOf(TableConstants.AITP))
                    && (getPmtType() != Integer.valueOf(TableConstants.NATIONAL_PERMIT)))) {
                throw new VahanException("Please select any Route/Area Details.");
            }
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", new FacesMessage(FacesMessage.SEVERITY_INFO, "", ex.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return passPermit;
    }

    public String printDocument() {
        String redirect = "";
        try {
            int purCd = 0;
            PrintPermitBean printPmtBean = new PrintPermitBean();
            PassengerPermitDetailDobj passPermit = setInDOBJ();
            if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_APPROVAL) {
                purCd = TableConstants.VM_PMT_APPLICATION_PUR_CD;
            }
            redirect = printPmtBean.permitDocumentAtApproval(appl_details.getAppl_no(), appl_details.getRegn_no(), purCd, Integer.valueOf(passPermit.getPmt_type_code()));
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", new FacesMessage(FacesMessage.SEVERITY_INFO, "", ex.getMessage()));
        }
        return redirect;
    }

    public void openModifyUploadedDocumentService() {
        try {

            String appl_no = appl_details.getAppl_no();

            dmsUrl = ServerUtil.getVahanPgiUrl(TableConstants.DMS_CON_VER);
            dmsUrl = dmsUrl.replace("ApplNo", appl_no);
            dmsUrl = dmsUrl.replace("ApplStatus", TableConstants.DOCUMENT_MODIFY_STATUS);
            dmsUrl = dmsUrl + TableConstants.SECURITY_KEY;
            PrimeFaces.current().ajax().update("test_opnFrame");
            PrimeFaces.current().executeScript("PF('ifrmDlg').show()");

        } catch (Exception ex) {
            LOGGER.error(ex);
        }

    }

    public String full_reset() {
        return "Passenger_Permit_Details";
    }

    public List getDomainList() {
        return domainList;
    }

    public void setDomainList(List domainList) {
        this.domainList = domainList;
    }

    public SelectOneMenu getDomain() {
        return domain;
    }

    public void setDomain(SelectOneMenu domain) {
        this.domain = domain;
    }

    public List getPmtCategory() {
        return pmtCategory;
    }

    public void setPmtCategory(List pmtCategory) {
        this.pmtCategory = pmtCategory;
    }

    public List getSer_type() {
        return ser_type;
    }

    public void setSer_type(List ser_type) {
        this.ser_type = ser_type;
    }

    public List geta() {
        return route_mast;
    }

    public void setRoute_mast(List route_mast) {
        this.route_mast = route_mast;
    }

    public int getPmtcatg() {
        return pmtcatg;
    }

    public void setPmtcatg(int pmtcatg) {
        this.pmtcatg = pmtcatg;
    }

    public InputText getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(InputText pmt_type) {
        this.pmt_type = pmt_type;
    }

    public InputText getFloc() {
        return floc;
    }

    public void setFloc(InputText floc) {
        this.floc = floc;
    }

    public InputText getTloc() {
        return tloc;
    }

    public void setTloc(InputText tloc) {
        this.tloc = tloc;
    }

    public int getServices_type() {
        return services_type;
    }

    public void setServices_type(int services_type) {
        this.services_type = services_type;
    }

    public InputText getNumber_OF_TRIPS() {
        return number_OF_TRIPS;
    }

    public void setNumber_OF_TRIPS(InputText number_OF_TRIPS) {
        this.number_OF_TRIPS = number_OF_TRIPS;
    }

    public InputText getOther_REGION() {
        return other_REGION;
    }

    public void setOther_REGION(InputText other_REGION) {
        this.other_REGION = other_REGION;
    }

    public InputText getAppl_NO() {
        return appl_NO;
    }

    public void setAppl_NO(InputText appl_NO) {
        this.appl_NO = appl_NO;
    }

    public InputText getOther_REGION_ROUT() {
        return other_REGION_ROUT;
    }

    public void setOther_REGION_ROUT(InputText other_REGION_ROUT) {
        this.other_REGION_ROUT = other_REGION_ROUT;
    }

    public String getRto_CD() {
        return rto_CD;
    }

    public void setRto_CD(String rto_CD) {
        this.rto_CD = rto_CD;
    }

    public String getPaction_code() {
        return paction_code;
    }

    public void setPaction_code(String paction_code) {
        this.paction_code = paction_code;
    }

    public String getPmt_type_code() {
        return pmt_type_code;
    }

    public void setPmt_type_code(String pmt_type_code) {
        this.pmt_type_code = pmt_type_code;
    }

    public boolean isBut_function() {
        return but_function;
    }

    public void setBut_function(boolean but_function) {
        this.but_function = but_function;
    }

    public String getSucc_fail() {
        return succ_fail;
    }

    public void setSucc_fail(String succ_fail) {
        this.succ_fail = succ_fail;
    }

    public List getPmt_mast() {
        return pmt_mast;
    }

    public void setPmt_mast(List pmt_mast) {
        this.pmt_mast = pmt_mast;
    }

    public List getPaction() {
        return paction;
    }

    public void setPaction(List paction) {
        this.paction = paction;
    }

    public int getPmtPurCode() {
        return pmtPurCode;
    }

    public void setPmtPurCode(int pmtPurCode) {
        this.pmtPurCode = pmtPurCode;
    }

    public int getPmtType() {
        return pmtType;
    }

    public void setPmtType(int pmtType) {
        this.pmtType = pmtType;
    }

    public List<String> getRoute_map() {
        return route_map;
    }

    public void setRoute_map(List<String> route_map) {
        this.route_map = route_map;
    }

    public InputTextarea getSelectManyRoute() {
        return selectManyRoute;
    }

    public void setSelectManyRoute(InputTextarea selectManyRoute) {
        this.selectManyRoute = selectManyRoute;
    }

    public List getPeriod() {
        return period;
    }

    public void setPeriod(List period) {
        this.period = period;
    }

    public String getPeriodMode() {
        return periodMode;
    }

    public void setPeriodMode(String periodMode) {
        this.periodMode = periodMode;
    }

    public Integer getPeriodValue() {
        return periodValue;
    }

    public void setPeriodValue(Integer periodValue) {
        this.periodValue = periodValue;
    }

    public String getUser_regn_no() {
        return user_regn_no;
    }

    public void setUser_regn_no(String user_regn_no) {
        this.user_regn_no = user_regn_no;
    }

    public PermitOwnerDetailDobj getOwner_dobj() {
        return owner_dobj;
    }

    public void setOwner_dobj(PermitOwnerDetailDobj owner_dobj) {
        this.owner_dobj = owner_dobj;
    }

    public PermitOwnerDetailBean getOwnerBean() {
        return ownerBean;
    }

    public void setOwnerBean(PermitOwnerDetailBean ownerBean) {
        this.ownerBean = ownerBean;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getApp_no_msg() {
        return app_no_msg;
    }

    public void setApp_no_msg(String app_no_msg) {
        this.app_no_msg = app_no_msg;
    }

    public boolean isShowHide() {
        return showHide;
    }

    public void setShowHide(boolean showHide) {
        this.showHide = showHide;
    }

    public SelectManyMenu getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(SelectManyMenu routeCode) {
        this.routeCode = routeCode;
    }

    public ApproveImpl getApproveImpl() {
        return approveImpl;
    }

    public void setApproveImpl(ApproveImpl approveImpl) {
        this.approveImpl = approveImpl;
    }

    public PassengerPermitDetailDobj getPermit_detail_dobj_prv() {
        return permit_detail_dobj_prv;
    }

    public void setPermit_detail_dobj_prv(PassengerPermitDetailDobj permit_detail_dobj_prv) {
        this.permit_detail_dobj_prv = permit_detail_dobj_prv;
    }

    public InputText getPmt_num() {
        return pmt_num;
    }

    public void setPmt_num(InputText pmt_num) {
        this.pmt_num = pmt_num;
    }

    public InputText getVal_from() {
        return val_from;
    }

    public void setVal_from(InputText val_from) {
        this.val_from = val_from;
    }

    public InputText getVal_upto() {
        return val_upto;
    }

    public void setVal_upto(InputText val_upto) {
        this.val_upto = val_upto;
    }

    public boolean isPmt_render() {
        return pmt_render;
    }

    public void setPmt_render(boolean pmt_render) {
        this.pmt_render = pmt_render;
    }

    public PermitDetailDobj getPmtDobj() {
        return pmtDobj;
    }

    public void setPmtDobj(PermitDetailDobj pmtDobj) {
        this.pmtDobj = pmtDobj;
    }

    public PermitDetailImpl getPmtImpl() {
        return pmtImpl;
    }

    public void setPmtImpl(PermitDetailImpl pmtImpl) {
        this.pmtImpl = pmtImpl;
    }

    public PermitDetailBean getPermit_Dtls_bean() {
        return permit_Dtls_bean;
    }

    public void setPermit_Dtls_bean(PermitDetailBean permit_Dtls_bean) {
        this.permit_Dtls_bean = permit_Dtls_bean;
    }

    public DualListModel<PermitRouteList> getRouteManage() {
        return routeManage;
    }

    public void setRouteManage(DualListModel<PermitRouteList> routeManage) {
        this.routeManage = routeManage;
    }

    public DualListModel<PermitRouteList> getAreaManage() {
        return areaManage;
    }

    public void setAreaManage(DualListModel<PermitRouteList> areaManage) {
        this.areaManage = areaManage;
    }

    public List<PermitRouteList> getActionSource() {
        return actionSource;
    }

    public void setActionSource(List<PermitRouteList> actionSource) {
        this.actionSource = actionSource;
    }

    public List<PermitRouteList> getActionTarget() {
        return actionTarget;
    }

    public void setActionTarget(List<PermitRouteList> actionTarget) {
        this.actionTarget = actionTarget;
    }

    public List<PermitRouteList> getAreaActionSource() {
        return areaActionSource;
    }

    public void setAreaActionSource(List<PermitRouteList> areaActionSource) {
        this.areaActionSource = areaActionSource;
    }

    public List<PermitRouteList> getAreaActionTarget() {
        return areaActionTarget;
    }

    public void setAreaActionTarget(List<PermitRouteList> areaActionTarget) {
        this.areaActionTarget = areaActionTarget;
    }

    public PassengerPermitDetailImpl getPassImp() {
        return passImp;
    }

    public void setPassImp(PassengerPermitDetailImpl passImp) {
        this.passImp = passImp;
    }

    public boolean isDisableRoutDtls() {
        return disableRoutDtls;
    }

    public void setDisableRoutDtls(boolean disableRoutDtls) {
        this.disableRoutDtls = disableRoutDtls;
    }

    @Override
    public String save() {
        String result = "";
        try {
            Map map = (Map) Util.getSession().getAttribute("seat_map");
            String appl_no = map.get("appl_no").toString();
            int action_cd = sessionVariables.getActionCodeSelected();
            passImp = new PassengerPermitDetailImpl();
            if (getTabIndex() == 0) {
                throw new VahanException("Please See Permit Details First, Then only you can save or move Application.");
            }
            PassengerPermitDetailDobj passPermit = setInDOBJ();
            owner_dobj = new PermitOwnerDetailDobj();
            owner_dobj = ownerBean.setValueinDOBJ();
            passImp.stayOnTheSameStage(owner_dobj, passPermit, routeManage.getTarget(), appl_no, ComparisonBeanImpl.changedDataContents(compareChanges()), getNew_regn_no(), action_cd, otherRouteAllow, selectedRouteList);
            result = "seatwork";
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", ex.getMessage()));
            result = "";
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            result = "";
        }
        return result;
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        try {
            if (permit_detail_dobj_prv == null) {
                return getCompBeanList();
            }
            getCompBeanList().clear();
            if (permit_owner_detail_dobj_prv.getOwner_name() != null && ownerBean.getOwner_name() != null) {
                Compare("Owner Name", permit_owner_detail_dobj_prv.getOwner_name(), ownerBean.getOwner_name(), (ArrayList) getCompBeanList());
            }
            if (permit_owner_detail_dobj_prv.getF_name() != null && ownerBean.getF_name() != null) {
                Compare("Father/Husband's Name", permit_owner_detail_dobj_prv.getF_name(), ownerBean.getF_name(), (ArrayList) getCompBeanList());
            }

            Compare("Vehicle Class", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getVh_class_array(), String.valueOf(permit_owner_detail_dobj_prv.getVh_class())), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getVh_class_array(), ownerBean.getVh_class()), (ArrayList) getCompBeanList());

            if (String.valueOf(permit_owner_detail_dobj_prv.getMobile_no()) != null && ownerBean.getMobile_no() != null) {
                Compare("Mobile No.", String.valueOf(permit_owner_detail_dobj_prv.getMobile_no()), ownerBean.getMobile_no(), (ArrayList) getCompBeanList());
            }
            if (permit_owner_detail_dobj_prv.getEmail_id() != null && ownerBean.getEmail_id() != null) {
                Compare("Email ID", permit_owner_detail_dobj_prv.getEmail_id(), ownerBean.getEmail_id(), (ArrayList) getCompBeanList());
            }
            if (permit_owner_detail_dobj_prv.getC_add1() != null && ownerBean.getC_add1() != null) {
                Compare("Current House No. & Street Name", permit_owner_detail_dobj_prv.getC_add1(), ownerBean.getC_add1(), (ArrayList) getCompBeanList());
            }
            if (permit_owner_detail_dobj_prv.getC_add2() != null && ownerBean.getC_add2() != null) {
                Compare("Current Village/Town/City ", permit_owner_detail_dobj_prv.getC_add2(), ownerBean.getC_add2(), (ArrayList) getCompBeanList());
            }
            if (permit_owner_detail_dobj_prv.getC_add3() != null && ownerBean.getC_add3() != null) {
                Compare("Current Landmark/Police Station", permit_owner_detail_dobj_prv.getC_add3(), ownerBean.getC_add3(), (ArrayList) getCompBeanList());
            }

            Compare("Current State", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_c_state(), permit_owner_detail_dobj_prv.getC_state()), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_c_state(), ownerBean.getC_state()), (ArrayList) getCompBeanList());
            Compare("Current District", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_c_district(), String.valueOf(permit_owner_detail_dobj_prv.getC_district())), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_c_district(), ownerBean.getC_district()), (ArrayList) getCompBeanList());

            if (String.valueOf(permit_owner_detail_dobj_prv.getC_pincode()) != null && ownerBean.getC_pincode() != null) {
                Compare("Current Pin Code", String.valueOf(permit_owner_detail_dobj_prv.getC_pincode()), ownerBean.getC_pincode(), (ArrayList) getCompBeanList());
            }
            if (permit_owner_detail_dobj_prv.getP_add1() != null && ownerBean.getP_add1() != null) {
                Compare("Permanent House No. & Street Name", permit_owner_detail_dobj_prv.getP_add1(), ownerBean.getP_add1(), (ArrayList) getCompBeanList());
            }
            if (permit_owner_detail_dobj_prv.getP_add2() != null && ownerBean.getP_add2() != null) {
                Compare("Permanent Village/Town/City ", permit_owner_detail_dobj_prv.getP_add2(), ownerBean.getP_add2(), (ArrayList) getCompBeanList());
            }
            if (permit_owner_detail_dobj_prv.getP_add3() != null && ownerBean.getP_add3() != null) {
                Compare("Permanent Landmark/Police Station", permit_owner_detail_dobj_prv.getP_add3(), ownerBean.getP_add3(), (ArrayList) getCompBeanList());
            }
            Compare("Permanent State", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_p_state(), permit_owner_detail_dobj_prv.getP_state()), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_p_state(), ownerBean.getP_state()), (ArrayList) getCompBeanList());
            Compare("Permanent District", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_p_district(), String.valueOf(permit_owner_detail_dobj_prv.getP_district())), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_p_district(), ownerBean.getP_district()), (ArrayList) getCompBeanList());
            if (String.valueOf(permit_owner_detail_dobj_prv.getP_pincode()) != null && ownerBean.getP_pincode() != null) {
                Compare("Permanent Pin Code", String.valueOf(permit_owner_detail_dobj_prv.getP_pincode()), ownerBean.getP_pincode(), (ArrayList) getCompBeanList());
            }
            Compare("Permit Type", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) pmt_mast, permit_detail_dobj_prv.getPmt_type()), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) pmt_mast, String.valueOf(getPmtType())), (ArrayList) getCompBeanList());
            Compare("Permit Category", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) pmtCategory, permit_detail_dobj_prv.getPmtCatg()), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) pmtCategory, String.valueOf(getPmtcatg())), (ArrayList) getCompBeanList());
            if (permit_detail_dobj_prv.getPeriod() != null && getPeriodValue() != null) {
                Compare("Period", permit_detail_dobj_prv.getPeriod(), String.valueOf(getPeriodValue()), (ArrayList) getCompBeanList());
            }
            Compare("Period Mode", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) period, permit_detail_dobj_prv.getPeriod_mode()), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) period, getPeriodMode().toUpperCase()), (ArrayList) getCompBeanList());
            //Compare("Number Of Trips", permit_detail_dobj_prv.getNumberOfTrips(), getNumber_OF_TRIPS().getValue().toString().toUpperCase(), (ArrayList) getCompBeanList());
            //Compare("Services Type", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ser_type, permit_detail_dobj_prv.getServices_TYPE()), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ser_type, getServices_type().getValue().toString()), (ArrayList) getCompBeanList());
            if (permit_detail_dobj_prv.getGoods_TO_CARRY() != null && (CommonUtils.isNullOrBlank(getGoodsToCarry()))) {
                Compare("Goods To Carry", permit_detail_dobj_prv.getGoods_TO_CARRY(), getGoodsToCarry().toUpperCase(), (ArrayList) getCompBeanList());
            }
            if (permit_detail_dobj_prv.getJoreny_PURPOSE() != null && (!CommonUtils.isNullOrBlank(getJoreny_PURPOSE()))) {
                Compare("Joreny Purpose", permit_detail_dobj_prv.getJoreny_PURPOSE(), getJoreny_PURPOSE().toUpperCase(), (ArrayList) getCompBeanList());
            }
            if (permit_detail_dobj_prv.getParking() != null && (!CommonUtils.isNullOrBlank(getParking()))) {
                Compare("Parking", permit_detail_dobj_prv.getParking(), getParking().toUpperCase(), (ArrayList) getCompBeanList());
            }
            if (areaActionTarget != null || areaManage.getTarget() != null) {
                Compare("Area Details", areaActionTarget, areaManage.getTarget(), (ArrayList) getCompBeanList());
            }
            if (actionTarget != null || routeManage.getTarget() != null) {
                Compare("Route Details", actionTarget, routeManage.getTarget(), (ArrayList) getCompBeanList());
            }
            if (permit_detail_dobj_prv.getReplaceDate() != null || dateOfReplacement != null) {
                Compare("Date Of Replacement", permit_detail_dobj_prv.getReplaceDate(), dateOfReplacement, (ArrayList) getCompBeanList());
            }
            if (permit_detail_dobj_prv.getNumberOfTrips() != null) {
                Compare("Trips", permit_detail_dobj_prv.getNumberOfTrips(), String.valueOf(getNo_of_trip()), (ArrayList) getCompBeanList());
            }
            if (auth_dobj != null && (getPmtType() == Integer.valueOf(TableConstants.AITP)
                    || getPmtType() == Integer.valueOf(TableConstants.NATIONAL_PERMIT))) {
                if (CommonUtils.isNullOrBlank(auth_dobj.getAuthNo())) {
                    auth_dobj.setApplNo(null);
                }
                if (auth_dobj_psv.getAuthNo() != null && auth_dobj.getAuthNo() != null) {
                    Compare("Authorisation No", auth_dobj_psv.getAuthNo(), auth_dobj.getAuthNo(), (ArrayList) getCompBeanList());
                }
                if (auth_dobj_psv.getAuthFrom() != null && auth_dobj.getAuthFrom() != null) {
                    Compare("Authorisation From", auth_dobj_psv.getAuthFrom(), auth_dobj.getAuthFrom(), (ArrayList) getCompBeanList());
                }
                if (auth_dobj_psv.getAuthUpto() != null && auth_dobj.getAuthUpto() != null) {
                    Compare("Authorisation Upto", auth_dobj_psv.getAuthUpto(), auth_dobj.getAuthUpto(), (ArrayList) getCompBeanList());
                }
                if (permit_detail_dobj_prv.getRegion_covered() != null && auth_dobj.getRegionCoveredNP() > 0) {
                    Compare("Area Details", permit_detail_dobj_prv.getRegion_covered(), String.valueOf(auth_dobj.getRegionCoveredNP()), (ArrayList) getCompBeanList());
                }
            }

            if (!prevTimeTableList.isEmpty() && !getPmtTimeTableBean().getTimeTableList().isEmpty()) {
                Compare("TimeTable", prevTimeTableList, getPmtTimeTableBean().getTimeTableList(), (ArrayList) getCompBeanList(), "TT");
            }

        } catch (Exception e) {
            LOGGER.error("Regn No : " + permit_owner_detail_dobj_prv.getRegn_no() + "--" + e.toString() + " " + e.getStackTrace()[0]);
        }
        return getCompBeanList();
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        try {
            if (CommonUtils.isNullOrBlank(getApp_disapp_dobj().getNew_status())) {
                JSFUtils.showMessagesInDialog("Information", "Please seclect any option to move your file..", FacesMessage.SEVERITY_INFO);
                return null;
            }
            if (getTabIndex() == 0 && sessionVariables.getActionCodeSelected() != TableConstants.TM_ROLE_PMT_RENWAL_APPROVE) {
                JSFUtils.showMessagesInDialog("Information", "Please See permit Details First, Then only you can save or move application.", FacesMessage.SEVERITY_INFO);
                return null;
            }
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_VERIFICATION
                        || sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_VERIFICATION
                        || sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_RENWAL_VERIFICATION
                        || sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_SCRUTINY
                        || sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_RENWAL_SCRUTINY
                        || sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_APPROVAL
                        || sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_OFFER_VERIFICATION) {
                    if (Util.getUserStateCode().equals("KL") && getPmtPurCode() == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
                        String errorMsg = CommonPermitPrintImpl.checkPmtGracePeriod(pmtCheckDtsl.getDtlsDobj(), getPmtType());
                        if (!CommonUtils.isNullOrBlank(errorMsg)) {
                            throw new VahanException(errorMsg);
                        }
                    }
                    if ((sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_VERIFICATION
                            || sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_RENWAL_VERIFICATION) && (!CommonUtils.isNullOrBlank(getRegn_no())) && (!getRegn_no().equalsIgnoreCase("NEW")) && Util.getUserStateCode().equalsIgnoreCase("UP")) {
                        VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                        parameters.setOWNER_CD(ownerBean.getOwner_cd());
                        if (Boolean.parseBoolean(stateConfigMap.get("insaurance_exempted"))) {
                            if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("insaurance_condition"), parameters), "save_details")) {
                                pmtCheckDtsl.getDtlsDobj().setExemptedIns(true);
                            }
                        }
                        String errorMsg = checkPmtValidation();
                        if (!CommonUtils.isNullOrBlank(errorMsg)) {
                            throw new VahanException(errorMsg);
                        }
                    }

                    if (renderAuthPeriod && (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_SCRUTINY)
                            && (auth_periodMode.equals("-1") || auth_period == -1)) {
                        auth_dobj = new PermitHomeAuthDobj();
                        throw new VahanException("Please select Auth Period");
                    }

                    List listRouteTime = getPmtTimeTableBean().getTimeTableList();
                    if (listRouteTime.size() > 0) {
                        formatRouteTimeTable(listRouteTime);
                    }
                    return_location = update_details(status, ComparisonBeanImpl.changedDataContents(compareChanges()), String.valueOf(appl_details.getCurrent_action_cd()), otherRouteAllow, selectedRouteList, listRouteTime);

                }
                if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY
                        && !CommonUtils.isNullOrBlank(appl_details.getAppl_no())) {
                    String appl_no = appl_details.getAppl_no();
                    passImp = new PassengerPermitDetailImpl();
                    PassengerPermitDetailDobj passPermit = set_data_in_dobj();
                    if (auth_dobj != null
                            && (getPmtType() == Integer.valueOf(TableConstants.AITP)
                            || getPmtType() == Integer.valueOf(TableConstants.NATIONAL_PERMIT))) {
                        passPermit.setRegion_covered(auth_dobj.getRegionCoveredNP() + ",");
                    }
                    owner_dobj = new PermitOwnerDetailDobj();
                    owner_dobj = ownerBean.setValueinDOBJ();
                    List listRouteTime = getPmtTimeTableBean().getTimeTableList();
                    if (listRouteTime.size() > 0) {
                        formatRouteTimeTable(listRouteTime);
                    }
                    passImp.update_in_all_tables(owner_dobj, passPermit, auth_dobj, (otherStateRouteManage != null && otherStateRouteManage.getTarget() != null && otherStateRouteManage.getTarget().size() > 0) ? otherStateRouteManage.getTarget() : routeManage.getTarget(), appl_no, status, ComparisonBeanImpl.changedDataContents(compareChanges()), String.valueOf(appl_details.getCurrent_action_cd()), getNew_regn_no(), isShowOwnerDLAndVoterId(), renderAuthPeriod, auth_periodMode, auth_period, statecoveredBean, otherRouteAllow, selectedRouteList, listRouteTime);
                    return_location = "seatwork";
                }
                if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPROVE) {
                    if (Util.getUserStateCode().equalsIgnoreCase("UP")) {
                        VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                        parameters.setOWNER_CD(ownerBean.getOwner_cd());
                        if (Boolean.parseBoolean(stateConfigMap.get("insaurance_exempted"))) {
                            if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("insaurance_condition"), parameters), "save_details")) {
                                pmtCheckDtsl.getDtlsDobj().setExemptedIns(true);
                            }
                        }
                        String errorMsg = checkPmtValidation();
                        if (!CommonUtils.isNullOrBlank(errorMsg)) {
                            throw new VahanException(errorMsg);
                        }
                    }
                    if (isRender_valid_from() && getPmt_valid_from() == null) {
                        JSFUtils.showMessagesInDialog("Empty Valid from date", "Please Select permit valid from Date", FacesMessage.SEVERITY_ERROR);
                        return_location = "";
                    } else {
                        PassengerPermitDetailDobj passPermit = set_data_in_dobj();
                        if ("DL,UP,GJ".contains(sessionVariables.getStateCodeSelected()) && passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)) {
                            PermitHomeAuthDobj auth_dobj_permit = ServerUtil.getTemporaryNPDetails(appl_details.getRegn_no(), Util.getUserStateCode(), appl_details.getAppl_no());
                            if (auth_dobj_permit != null) {
                                PermitHomeAuthDobj auth_dobj_np = ServerUtil.getPermitDetailsFromNp(appl_details.getRegn_no());
                                if (auth_dobj_np != null) {
                                    if ("UP,GJ".contains(sessionVariables.getStateCodeSelected()) && (!(auth_dobj_permit.getAuthFrom().equals(auth_dobj_np.getAuthFrom())
                                            && auth_dobj_permit.getAuthUpto().equals(auth_dobj_np.getAuthUpto()))) && !auth_dobj_permit.getNpVerifyStatus().equalsIgnoreCase("A")) {
                                        throw new VahanException("Please visit the portal https://vahan.parivahan.gov.in/npermit/ first for payment of applicable MoRTH Composite Fee.");
                                    } else if ("DL".contains(sessionVariables.getStateCodeSelected())) {
                                        long dayDiff = 0;
                                        if (auth_dobj_np.getAuthUpto().after(auth_dobj_permit.getAuthUpto())) {
                                            dayDiff = DateUtils.getDate1MinusDate2_Days(auth_dobj_permit.getAuthUpto(), auth_dobj_np.getAuthUpto());
                                        } else {
                                            dayDiff = DateUtils.getDate1MinusDate2_Days(auth_dobj_np.getAuthUpto(), auth_dobj_permit.getAuthUpto());
                                        }
                                        if ((!auth_dobj_permit.getNpVerifyStatus().equalsIgnoreCase("A")) && dayDiff > 30) {
                                            throw new VahanException("Please visit the portal https://vahan.parivahan.gov.in/npermit/ first for payment of applicable MoRTH Composite Fee.");
                                        }
                                    }
                                } else {
                                    throw new VahanException("Please visit the portal https://vahan.parivahan.gov.in/npermit/ first for payment of applicable MoRTH Composite Fee.");
                                }
                            }
                        }
                        return_location = approveThePermit(status, passPermit);
                    }
                }
                if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_RENWAL_APPROVE) {
                    PassengerPermitDetailDobj passPermit = set_data_in_dobj();
                    if (Util.getUserStateCode().equals("KL") && getPmtPurCode() == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
                        String errorMsg = CommonPermitPrintImpl.checkPmtGracePeriod(pmtCheckDtsl.getDtlsDobj(), getPmtType());
                        if (!CommonUtils.isNullOrBlank(errorMsg)) {
                            throw new VahanException(errorMsg);
                        }
                    }
                    if (Util.getUserStateCode().equalsIgnoreCase("UP")) {
                        String errorMsg = checkPmtValidation();
                        if (!CommonUtils.isNullOrBlank(errorMsg)) {
                            throw new VahanException(errorMsg);
                        }
                    }
                    return_location = approveRenwalOfPermit(status, passPermit);
                }
            }
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                status.setPrev_action_cd_selected(app_disapp_dobj.getPre_action_code());
                reback(status, ComparisonBeanImpl.changedDataContents(compareChanges()));
                return_location = "seatwork";
            }
        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", new FacesMessage(FacesMessage.SEVERITY_INFO, "", e.getMessage()));
            JSFUtils.showMessagesInDialog("Information", e.getMessage(), FacesMessage.SEVERITY_ERROR);
            return_location = "";
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return return_location;
    }

    public PassengerPermitDetailDobj getPermit_detail_dobj() {
        return permit_detail_dobj;
    }

    public void setPermit_detail_dobj(PassengerPermitDetailDobj permit_detail_dobj) {
        this.permit_detail_dobj = permit_detail_dobj;
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
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    @Override
    public void saveEApplication() {
        String generated_appl_no = null;
        try {
            FacesMessage message = null;
            passImp = new PassengerPermitDetailImpl();
            if (getPmtPurCode() == TableConstants.VM_PMT_FRESH_PUR_CD) {
                PassengerPermitDetailDobj passPermit = set_data_in_dobj();
                if (passPermit != null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Application Submitted successfully. Application No. :" + generated_appl_no, "SUCCESS..."));
                }
            } else if (getPmtPurCode() == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
                Date date = new Date();
                if (date.getTime() > pmtDobj.getValid_upto().getTime()) {
                    PassengerPermitDetailDobj passPermit = set_data_in_dobj();
                    if (passPermit != null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Application Submitted successfully. Application No. :" + generated_appl_no, "SUCCESS..."));
                    }
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Your Permit is Not Expire", "Your Permit is Not Expire");
                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application is not Submitted", "Failed..."));
        }
    }

    /**
     * @return the masterLayout
     */
    public String getMasterLayout() {
        return masterLayout;
    }

    /**
     * @param masterLayout the masterLayout to set
     */
    public void setMasterLayout(String masterLayout) {
        this.masterLayout = masterLayout;
    }

    public boolean iseApplshowHide() {
        return eApplshowHide;
    }

    public void seteApplshowHide(boolean eApplshowHide) {
        this.eApplshowHide = eApplshowHide;
    }

    public void changeReasonmodule() {

        if (("A").equalsIgnoreCase(getApp_disapp())) {
            reasonRendered = false;
        } else if (("D").equalsIgnoreCase(getApp_disapp())) {
            reasonRendered = true;
        }
    }

    public void editOrderInfo() {
        setDisableOrderDtls(false);
        setOrder_by("");
        setOrder_dt(null);
        setOrder_no("");
    }

    public void booleanRegisterVehicle(AjaxBehaviorEvent event) {
        owner_dobj = new PermitOwnerDetailDobj();
        if (getSelectOneRadio() == 1
                || getSelectOneRadio() == havingOfferLetter) {
            tabIndex = 0;
            getRouteManage().getSource().clear();
            getRouteManage().getTarget().clear();
            getAreaManage().getSource().clear();
            getAreaManage().getTarget().clear();
            setVia_route("");
            but_function = true;
            if (getSelectOneRadio() == 1) {
                regn_know = renderGetDetails = true;
                offerNumberRender = false;
            } else if (getSelectOneRadio() == havingOfferLetter) {
                offerNumberRender = renderGetDetails = true;
                regn_know = false;
            }
            setUser_regn_no("");
            ownerBean.setValueReset();
            ownerBean.getPmt_dobj().setFlage(true);
            disableOtherVehicleDtls = false;
        } else if (getSelectOneRadio() == 2) {
            getRouteManage().getSource().clear();
            getRouteManage().getTarget().clear();
            getAreaManage().getSource().clear();
            getAreaManage().getTarget().clear();
            setVia_route("");
            tabIndex = 0;
            but_function = false;
            setRegn_no("NEW");
            paction.clear();
            String[][] data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
            int i = 0;
            for (i = 0; i < data.length; i++) {
                if (Integer.parseInt(data[i][0]) == TableConstants.VM_PMT_FRESH_PUR_CD
                        || Integer.parseInt(data[i][0]) == TableConstants.VM_PMT_APPLICATION_PUR_CD) {
                    paction.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
            this.setPmtPurCode(TableConstants.VM_PMT_FRESH_PUR_CD);
            if (Boolean.parseBoolean(stateConfigMap.get("allowed_sta_rto_relation"))) {
                try {
                    VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                    setPmtPurCode(new PassengerPermitDetailImpl().getPurCdFormStateConfig(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("sta_rto_condition"), parameters)));
                } catch (VahanException ex) {
                    JSFUtils.showMessagesInDialog("Information", ex.getMessage(), FacesMessage.SEVERITY_INFO);
                }
            }
            regn_know = false;
            offerNumberRender = renderGetDetails = false;
            setUser_regn_no("");
            ownerBean.setValueReset();
            ownerBean.getPmt_dobj().setFlage(false);
        }
        disableOtherVehicleDtls = false;
    }

    public PermitOwnerDetailImpl getOwnerImpl() {
        return ownerImpl;
    }

    public void setOwnerImpl(PermitOwnerDetailImpl ownerImpl) {
        this.ownerImpl = ownerImpl;
    }

    public String getApp_disapp() {
        return app_disapp;
    }

    public void setApp_disapp(String app_disapp) {
        this.app_disapp = app_disapp;
    }

    public List<PermitRouteList> getPrvactionTarget() {
        return prvactionTarget;
    }

    public void setPrvactionTarget(List<PermitRouteList> prvactionTarget) {
        this.prvactionTarget = prvactionTarget;
    }

    public String getOrder_by() {
        return order_by;
    }

    public void setOrder_by(String order_by) {
        this.order_by = order_by;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getOrder_dt() {
        return order_dt;
    }

    public void setOrder_dt(Date order_dt) {
        this.order_dt = order_dt;
    }

    public boolean isReasonRendered() {
        return reasonRendered;
    }

    public void setReasonRendered(boolean reasonRendered) {
        this.reasonRendered = reasonRendered;
    }

    public int getSelectOneRadio() {
        return selectOneRadio;
    }

    public void setSelectOneRadio(int selectOneRadio) {
        this.selectOneRadio = selectOneRadio;
    }

    public boolean isRegn_know() {
        return regn_know;
    }

    public void setRegn_know(boolean regn_know) {
        this.regn_know = regn_know;
    }

    public boolean isDisableAreaDtls() {
        return disableAreaDtls;
    }

    public void setDisableAreaDtls(boolean disableAreaDtls) {
        this.disableAreaDtls = disableAreaDtls;
    }

    public void onTransfer() {
        try {
            String route_cd = "";
            if (Boolean.parseBoolean(stateConfigMap.get("pmt_reservation_allow"))) {
                passImp = new PassengerPermitDetailImpl();
                List route = routeManage.getTarget();
                for (Object code : route) {
                    route_cd = (String) code;
                }
                resList = null;
                if (!CommonUtils.isNullOrBlank(route_cd) && route != null && (getPmtType() != -1 || getPmtType() != 0) && (getPmtcatg() != -1 || getPmtcatg() != 0)) {
                    resList = PermitReservationImpl.getPermitReservationDtls(sessionVariables.getStateCodeSelected(), getPmtType(), getPmtcatg(), ownerBean.getOwner_catg(), ownerBean.getFuel_type(), 0, sessionVariables.getOffCodeSelected(), route_cd);
                }
            }
            passImp = new PassengerPermitDetailImpl();
            setVia_route(passImp.getRouteVia(routeManage.getTarget(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected()));
            setRouteLength(passImp.getRouteLength(routeManage.getTarget(), null, null, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected()));
            if (getVia_route().isEmpty()) {
                noOfTripsrendered = false;
            } else {
                noOfTripsrendered = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
        }
    }

    public void onTransferOtherStateRoute() {
        try {
            String route_cd = "";
            passImp = new PassengerPermitDetailImpl();
            if (Boolean.parseBoolean(stateConfigMap.get("pmt_reservation_allow"))) {
                passImp = new PassengerPermitDetailImpl();
                List route = otherStateRouteManage.getTarget();
                for (Object code : route) {
                    route_cd = (String) code;
                }
                resList = null;
                if (!CommonUtils.isNullOrBlank(route_cd) && route != null && (getPmtType() != -1 || getPmtType() != 0) && (getPmtcatg() != -1 || getPmtcatg() != 0)) {
                    resList = PermitReservationImpl.getPermitReservationDtls(sessionVariables.getStateCodeSelected(), getPmtType(), getPmtcatg(), 0, ownerBean.getFuel_type(), 0, sessionVariables.getOffCodeSelected(), route_cd);
                }
            }
            setVia_route_oth(passImp.getRouteVia(otherStateRouteManage.getTarget(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected()));
            if (getVia_route_oth().isEmpty()) {
                noOfTripsrendered = false;
            } else {
                noOfTripsrendered = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
        }
    }

    public void onAreaTransfer() {
        try {
            String region = "";
            if (Boolean.parseBoolean(stateConfigMap.get("pmt_reservation_allow"))) {
                passImp = new PassengerPermitDetailImpl();
                List area = areaManage.getTarget();
                for (Object ss : area) {
                    if (ss instanceof String) {
                        region = region + (String) ss + ",";
                    }
                }
                resList = null;
                int reservRegion = passImp.checkMultiRegion(region, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                if (reservRegion == 99 && area != null && (getPmtType() != -1 || getPmtType() != 0) && (getPmtcatg() != -1 || getPmtcatg() != 0)) {
                    resList = PermitReservationImpl.getPermitReservationDtls(sessionVariables.getStateCodeSelected(), getPmtType(), getPmtcatg(), ownerBean.getOwner_catg(), ownerBean.getFuel_type(), reservRegion, sessionVariables.getOffCodeSelected(), "0");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
        }
    }

    public void onSelectPermitType(String domain) {
        auth_dobj = new PermitHomeAuthDobj();
        pmtCategory.clear();
        if (getSelectOneRadio() == 1
                || getSelectOneRadio() == 2) {
            getRouteManage().getSource().clear();
            getRouteManage().getTarget().clear();
            getAreaManage().getSource().clear();
            getAreaManage().getTarget().clear();
        }
        String authRegnNo = "";
        String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
        String permit_type;
        if (getPmtType() == 0) {
            permit_type = "-1";
        } else {
            permit_type = String.valueOf(getPmtType());
        }
        PassengerPermitDetailDobj parametersPmtDobj = new PassengerPermitDetailDobj();
        if (getPmtType() == 0 || getPmtType() == -1) {
            parametersPmtDobj.setPmt_type_code("-1");
        } else {
            parametersPmtDobj.setPmt_type_code(String.valueOf(getPmtType()));
        }
        setReplacementDate(parametersPmtDobj);
        freezePeriod(getPmtPurCode(), Integer.valueOf(permit_type), 0, null, renderhypth, ownerDetail == null ? 0 : ownerDetail.getOwner_cd());
        if (authorityDobj != null) {
            if (authorityDobj.getSelectedPermitCatg().size() == 1) {
                for (Object obj : authorityDobj.getSelectedPermitCatg()) {
                    permissionPermitCatg = String.valueOf(obj);
                }
            }
        }
        if (authorityDobj == null || ("Any").equalsIgnoreCase(permissionPermitCatg)) {
            for (int j = 0; j < data.length; j++) {
                if (data[j][0].equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                        && Integer.parseInt(data[j][3]) == Integer.parseInt(permit_type)) {
                    pmtCategory.add(new SelectItem(data[j][1], data[j][2]));
                }
            }
        } else {
            for (int j = 0; j < data.length; j++) {
                for (Object obj : authorityDobj.getSelectedPermitCatg()) {
                    if (data[j][0].equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                            && Integer.parseInt(data[j][3]) == Integer.parseInt(permit_type)
                            && data[j][1].equalsIgnoreCase(String.valueOf(obj))) {
                        pmtCategory.add(new SelectItem(data[j][1], data[j][2]));
                    }
                }
            }
        }
        if (((permit_type.equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)
                || permit_type.equalsIgnoreCase(TableConstants.AITP)))
                && (pmtPurCode == TableConstants.VM_PMT_FRESH_PUR_CD
                || pmtPurCode == TableConstants.VM_PMT_APPLICATION_PUR_CD)) {
            auth_impl = new PermitHomeAuthImpl();

            if (CommonUtils.isNullOrBlank(getUser_regn_no())) {
                authRegnNo = getRegn_no().toUpperCase();
            } else {
                authRegnNo = getUser_regn_no().toUpperCase();
            }
            auth_dobj = auth_impl.getVT_TAXdetails(authRegnNo);
            if (auth_dobj == null && !authRegnNo.equalsIgnoreCase("NEW") && !isTaxExemptForKL()) {
                errorShowMsg = "Tax is Invalid. Please Contact the respective Registering Authority.";
                PrimeFaces.current().ajax().update("in_Tax");
                PrimeFaces.current().executeScript("PF('inValidTax').show();");
                but_function = true;
                return;
            }
            if (isTaxExemptForKL()) {
                auth_dobj = new PermitHomeAuthDobj();
            }
            visibleAreaDtls = false;
            visibleRouteDtls = false;
            visibleAuthDetails = true;
            but_function = false;
            Map<String, String> routeMap = passImp.getSourcesAreaMapWithStrBuil("0", sessionVariables.getStateCodeSelected(), permitIssuingAuthCode);
            regionCoveredListNP.clear();
            regionCoveredListNP.add(new SelectItem("0", "SELECT ANY AREA/REAGION"));
            for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                regionCoveredListNP.add(new SelectItem(entry.getKey(), entry.getValue()));
            }
            getRouteManage().getSource().clear();
            getRouteManage().getTarget().clear();
            getAreaManage().getSource().clear();
            getAreaManage().getTarget().clear();
            setVia_route("");
        } else if ((permit_type.equalsIgnoreCase(TableConstants.NATIONAL_PERMIT) || permit_type.equalsIgnoreCase(TableConstants.AITP))
                && pmtPurCode == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
            visibleAreaDtls = false;
            visibleRouteDtls = false;
            visibleAuthDetails = false;
            getRouteManage().getSource().clear();
            getRouteManage().getTarget().clear();
            getAreaManage().getSource().clear();
            getAreaManage().getTarget().clear();
            setVia_route("");
        } else if (getSelectOneRadio() == 1
                || getSelectOneRadio() == 2) {
            visibleAreaDtls = true;
            visibleRouteDtls = true;
            visibleAuthDetails = false;
            routeCodeDetail("NULL", TableList.vt_permit_route, sessionVariables.getStateCodeSelected(), permitIssuingAuthCode, false);
            areaCodeDetail(sessionVariables.getStateCodeSelected(), domain, permitIssuingAuthCode);
            setVia_route("");
        }
//Getting NAtional Permit Authorision Details: Begin
        if (permit_type.equalsIgnoreCase(TableConstants.NATIONAL_PERMIT) && !(CommonUtils.isNullOrBlank(authRegnNo) || ("").equalsIgnoreCase(authRegnNo)) && !authRegnNo.equalsIgnoreCase("NEW")) {
            try {
                permit_Dtls_bean.setNationalPermitAuthDetails(authRegnNo.toUpperCase().trim());
                setDisableNpDetails(true);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        } else {
            setDisableNpDetails(false);
        }
        try {
            Map<String, Boolean> visibleRestrictionAreaRoute = new PassengerPermitDetailImpl().visibleRestrictionAreaRoute(sessionVariables.getStateCodeSelected(), getPmtPurCode(), getPmtType(), getPmtcatg());
            if (visibleRestrictionAreaRoute != null) {
                visibleAreaDtls = visibleRestrictionAreaRoute.get("show_region_dtls");
                visibleRouteDtls = visibleRestrictionAreaRoute.get("show_route_dtls");
            }

            if (permit_type.equals(TableConstants.AITP) && pmtPurCode == TableConstants.VM_PMT_FRESH_PUR_CD) {
                VehicleParameters vchparameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                vchparameters.setPERMIT_TYPE(Integer.parseInt(permit_type));
                vchparameters.setPUR_CD(pmtPurCode);
                if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("show_period_on_aitp_auth"), vchparameters), "onSelectPermitType")) {
                    renderAuthPeriod = true;
                    notFillHomeAuth = true;
                }
            }

            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            parameters.setPERMIT_TYPE(Integer.parseInt(permit_type));
            if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("show_time_table"), parameters), "onSelectPermitType")) {
                setShowTimeTableDetails(true);
            }

        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        }
        //Getting NAtional Permit Authorision Details: End

        if (otherRouteAllow && Integer.parseInt(permit_type) == TableConstants.STAGE_CARRIAGE_PERMIT) {
            showOtherRoute = true;
        } else {
            showOtherRoute = false;
        }
        resList = null;
    }

    public void freezePeriod(int pur_cd, int pmt_type, int pmt_catg, PassengerPermitDetailDobj permit_dobj, boolean hypth, int owner_cd) {
        try {
            String[] values = CommonPermitPrintImpl.getPmtValidity(pur_cd, pmt_type, pmt_catg, hypth, owner_cd);
            if (values != null && !CommonUtils.isNullOrBlank(values[0]) && !CommonUtils.isNullOrBlank(values[1])) {
                setPeriodMode(values[0]);
                setPeriodModeDisable(true);
                setPeriodValue(Integer.valueOf(values[1]));
                setPeriodValueDisable(true);
            } else if (permit_dobj == null) {
                period.clear();
                String[][] data = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
                for (int i = 0; i < data.length; i++) {
                    if (("M").equalsIgnoreCase(data[i][0]) || ("Y").equalsIgnoreCase(data[i][0])) {
                        period.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
                if (selectOneRadio != havingOfferLetter) {
                    setPeriodModeDisable(false);
                    setPeriodValue(null);
                    setPeriodValueDisable(true);
                } else {
                    setPeriodModeDisable(true);
                    setPeriodValueDisable(true);
                }
            }
        } catch (Exception e) {
            period.clear();
            String[][] data = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
            for (int i = 0; i < data.length; i++) {
                if (("M").equalsIgnoreCase(data[i][0]) || ("Y").equalsIgnoreCase(data[i][0])) {
                    period.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
            setPeriodModeDisable(false);
            setPeriodValue(null);
            setPeriodValueDisable(true);
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public String getVia_route() {
        return via_route;
    }

    public void setVia_route(String via_route) {
        this.via_route = via_route;
    }

    public List getArea_mast() {
        return area_mast;
    }

    public void setArea_mast(List area_mast) {
        this.area_mast = area_mast;
    }

    public void onTabChange(TabChangeEvent event) {
        String transport_catg = "";
        String pmt_type = "";
        if (ownerBean != null && ownerBean.getVh_class() != null) {
            if (("Permit Detail").equalsIgnoreCase(event.getTab().getTitle()) && selectedVehClass != Integer.valueOf(ownerBean.getVh_class())) {
                selectedVehClass = Integer.valueOf(ownerBean.getVh_class());
                if (selectedVehClass > 0) {
                    String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
                    for (int i = 0; i < data.length; i++) {
                        if (data[i][0].equalsIgnoreCase(ownerBean.getVh_class())) {
                            transport_catg = data[i][3];
                            break;
                        }
                    }
                    if (("P").equalsIgnoreCase(transport_catg)) {
                        getNumber_OF_TRIPS().setRendered(false);
                    } else {
                        getNumber_OF_TRIPS().setRendered(true);
                    }
                    data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
                    pmt_mast.clear();

                    if (authorityDobj != null) {
                        if (authorityDobj.getPermitType().size() == 1) {
                            for (Object obj : authorityDobj.getPermitType()) {
                                permissionPermitType = String.valueOf(obj);
                            }
                        }
                    }
                    if (isAllowSpecialVhClass() && transport_catg.equalsIgnoreCase("S")) {
                        pmt_type = passImp.getPmtTypeForSplVhClass(getPmtPurCode(), Integer.valueOf(ownerBean.getVh_class()));
                    }
                    if (authorityDobj == null || ("Any").equalsIgnoreCase(permissionPermitType)) {
                        for (int i = 0; i < data.length; i++) {
                            if (isAllowSpecialVhClass() && transport_catg.equalsIgnoreCase("S")) {
                                if (pmt_type.contains(data[i][0])) {
                                    pmt_mast.add(new SelectItem(data[i][0], data[i][1]));
                                }
                            } else if (data[i][2].equalsIgnoreCase(transport_catg)) {
                                pmt_mast.add(new SelectItem(data[i][0], data[i][1]));
                            }
                        }
                    } else {
                        for (int i = 0; i < data.length; i++) {
                            for (Object obj : authorityDobj.getPermitType()) {
                                if (isAllowSpecialVhClass() && transport_catg.equalsIgnoreCase("S")) {
                                    if (pmt_type.contains(data[i][0])) {
                                        pmt_mast.add(new SelectItem(data[i][0], data[i][1]));
                                    }
                                } else if (data[i][2].equalsIgnoreCase(transport_catg) && data[i][0].equalsIgnoreCase(String.valueOf(obj))) {
                                    pmt_mast.add(new SelectItem(data[i][0], data[i][1]));
                                }
                            }
                        }
                    }
                    if (selectedValue != null && selectedValue.getPaction_code().equalsIgnoreCase((String.valueOf(TableConstants.VM_PMT_FRESH_PUR_CD)))) {
                        pmt_mast.clear();
                        for (int i = 0; i < data.length; i++) {
                            if (isAllowSpecialVhClass() && transport_catg.equalsIgnoreCase("S")) {
                                if (pmt_type.contains(data[i][0])) {
                                    pmt_mast.add(new SelectItem(data[i][0], data[i][1]));
                                }
                            } else if (data[i][0].equalsIgnoreCase(selectedValue.getPmt_type())) {
                                pmt_mast.add(new SelectItem(data[i][0], data[i][1]));
                            }
                        }
                    }
                    if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY
                            && getPmtPurCode() == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
                        pmtCategory.clear();
                        int permit_type = getPmtType();
                        data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
                        if (authorityDobj != null) {
                            if (authorityDobj.getSelectedPermitCatg().size() == 1) {
                                for (Object obj : authorityDobj.getSelectedPermitCatg()) {
                                    permissionPermitCatg = String.valueOf(obj);
                                }
                            }
                        }
                        if (authorityDobj == null || ("Any").equalsIgnoreCase(permissionPermitCatg)) {
                            for (int j = 0; j < data.length; j++) {
                                if (data[j][0].equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                                        && Integer.parseInt(data[j][3]) == permit_type) {
                                    pmtCategory.add(new SelectItem(data[j][1], data[j][2]));
                                }
                            }
                        } else {
                            for (int j = 0; j < data.length; j++) {
                                for (Object obj : authorityDobj.getSelectedPermitCatg()) {
                                    if (data[j][0].equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                                            && Integer.parseInt(data[j][3]) == permit_type
                                            && data[j][1].equalsIgnoreCase(String.valueOf(obj))) {
                                        pmtCategory.add(new SelectItem(data[j][1], data[j][2]));
                                    }
                                }
                            }
                        }
                    }

                    if ((sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY
                            || sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_OFFER_VERIFICATION)
                            && getPmtPurCode() == TableConstants.VM_PMT_FRESH_PUR_CD) {
                        if (!CommonUtils.isNullOrBlank(getRegn_domain())) {
                            onSelectPermitType(getRegn_domain());
                        } else {
                            onSelectPermitType("");
                        }
                    }
                }
            }
        }
        checkReservation();
        if (!taxDependOnPmt && (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY) && getPmtPurCode() == TableConstants.VM_PMT_FRESH_PUR_CD) {
            showPermitDetailsPaidFeeAtNewRegn(ownerBean.getRegn_no(), sessionVariables.getStateCodeSelected(), 0, null);
            VehicleParameters parameters = new VehicleParameters();
            parameters.setPERMIT_TYPE(getPmtType());
            if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("show_time_table"), parameters), "onSelectPermitType")) {
                setShowTimeTableDetails(true);
            }
        }
    }

    public void checkOffNumber() {
        String offerLetterDocId = "3";
        List<PermitPaidFeeDtlsDobj> paidFeeList = null;
        try {
            String db_offerLetterNo = new PermitFeeImpl().getOffereLetterNo(appl_details.getAppl_no());
            boolean offerLetterPrint = CommonPermitPrintImpl.checkPermitPrintPending(appl_details.getAppl_no(), offerLetterDocId);
            if (offerLetterPrint) {
                JSFUtils.showMessagesInDialog("Message", "Please print the offer letter first.", FacesMessage.SEVERITY_ERROR);
                return;
            } else if (db_offerLetterNo.equalsIgnoreCase(offerLetterNo)) {
                VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                paidFeeList = new PermitPaidFeeDtlsImpl().getListOfPaidFee(appl_details.getAppl_no());
                int offerValidityDays = Integer.parseInt(stateConfigMap.get("offer_validity_days"));
                passImp = new PassengerPermitDetailImpl();
                int dateDiff = passImp.getNoOfDaysAfterFee(appl_details.getAppl_no());
                if (dateDiff > offerValidityDays && stateConfigMap != null && paidFeeList != null) {
                    parameters.setTAX_DUE_FROM_DATE(DateUtil.convertStringDDMMMYYYYToDDMMYYYY(paidFeeList.get(0).getRcpt_dt()));
                    if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("allow_loi_after_expired"), parameters), "PassengerPermitDetailBean()")) {
                        regnNoRender = true;
                    } else {
                        regnNoRender = false;
                        JSFUtils.showMessagesInDialog("Information", "Offer validity is expired ", FacesMessage.SEVERITY_ERROR);
                    }
                } else {
                    regnNoRender = true;
                }
            } else {
                JSFUtils.showMessagesInDialog("Message", "Offer letter no. not match. Please check the document.", FacesMessage.SEVERITY_ERROR);
                return;
            }

        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Message", e.getMessage(), FacesMessage.SEVERITY_ERROR);
            return;
        } catch (Exception e) {
            JSFUtils.showMessagesInDialog("Message", e.getMessage(), FacesMessage.SEVERITY_ERROR);
            return;
        }
        return;
    }

    public void check_regn_no() {
        disableRegnNo = true;
        PermitFeeImpl pmt_fee_impl = new PermitFeeImpl();
        PermitFeeDobj pmt_fee_dobj = null;
        try {
            boolean OfferExist = new PermitLOIImpl().recodeExistInVaPmtOfferApp(appl_details.getAppl_no());
            if (OfferExist) {
                JSFUtils.showMessagesInDialog("Information", "First LOI/offer approve and print offer letter", FacesMessage.SEVERITY_INFO);
                return;
            }
            pmt_fee_dobj = pmt_fee_impl.get_regnNo_details(getNew_regn_no().toUpperCase(), sessionVariables.getActionCodeSelected());
            if (pmt_fee_dobj != null) {
                pmtCheckDtsl.getAlldetails(getNew_regn_no().toUpperCase(), null, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                if (Boolean.parseBoolean(stateConfigMap.get("exempt_echallan"))) {
                    pmtCheckDtsl.getDtlsDobj().setExemptedChallan(true);
                }
                String errorMsg = checkPmtValidation();
                if (!CommonUtils.isNullOrBlank(pmt_fee_dobj.getPermit_no())) {
                    JSFUtils.showMessagesInDialog("Invalid Vehicle", "This vehicle already have a permit.", FacesMessage.SEVERITY_INFO);
                    return;
                }
                if (!CommonUtils.isNullOrBlank(errorMsg)) {
                    JSFUtils.showMessagesInDialog(errorMsg, errorMsg, FacesMessage.SEVERITY_INFO);
                    return;
                }
                passImp = new PassengerPermitDetailImpl();
                String[] owner = passImp.vtPermitOwner(appl_details.getAppl_no());
                if (owner != null && ((owner[1].equalsIgnoreCase(pmt_fee_dobj.getNew_vh_class())))
                        && (owner[0].trim().equalsIgnoreCase(pmt_fee_dobj.getNew_o_name().trim()))) {
                    setNew_f_name((pmt_fee_dobj.getNew_f_name()).toUpperCase());
                    setNew_o_name((pmt_fee_dobj.getNew_o_name()).toUpperCase());
                    setNew_vh_class(pmt_fee_dobj.getNew_vh_class());
                    if (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_VERIFICATION || sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_OFFER_VERIFICATION) {
                        bt_get_details_by_name();
                    }
                    disableRegnNo = false;

                } else {
                    setNew_f_name("");
                    setNew_o_name("");
                    setNew_vh_class("-1");
                    JSFUtils.showMessagesInDialog("Infoemation", "Owner Name/Vehicle Class dose not match.", FacesMessage.SEVERITY_INFO);
                }
            } else {
                setNew_f_name("");
                setNew_o_name("");
                setNew_vh_class("-1");
                JSFUtils.showMessagesInDialog("In-Valid Vehicle", "This Registration No is not valid ", FacesMessage.SEVERITY_ERROR);
            }
        } catch (VahanException ex) {
            JSFUtils.showMessage(ex.getMessage());
        } catch (Exception e) {
            JSFUtils.showMessagesInDialog("Error", TableConstants.SomthingWentWrong, FacesMessage.SEVERITY_ERROR);
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void getPermitDetailThroughDataTable() {
        FacesMessage message;
        HpaImpl hpa_Impl = new HpaImpl();
        PermitOwnerDetailDobj dobjs = ownerBean.getOwnerDetailsDobj(ownerDetail);
        try {
            if (multiPermitDetails.size() > 1) {
                if (selectedValue.getPaction_code().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD))) {
                    if (!CommonUtils.isNullOrBlank(selectedValue.getPmt_no())) {
                        setPermit_no(selectedValue.getPmt_no());
                    }
                    PassengerPermitDetailDobj permit_dobj = passImp.set_vt_permit_regnNo_to_dobj(selectedValue.getRegnNo(), selectedValue.getPmt_no());
                    set_renewal_Permit_Detail_dobj_to_bean(permit_dobj);
                    setPmtPurCode(Integer.valueOf(selectedValue.getPaction_code()));
                    freezePeriod(getPmtPurCode(), Integer.valueOf(permit_dobj.getPmt_type()), 0, null, renderhypth, dobjs.getOwner_cd());
                    OwnerImpl impl = new OwnerImpl();
                    Owner_dobj ownDobj = impl.getOwnerDobj(ownerDetail);
                    if (TableConstants.NATIONAL_PERMIT.equalsIgnoreCase(permit_dobj.getPmt_type())) {
                        onSelectPermitType("");
                        PermitHomeAuthImpl PrvAuthImpl = new PermitHomeAuthImpl();
                        PermitHomeAuthDobj PrvAuthDobj = PrvAuthImpl.getSelectInVtPermit(ownerBean.getRegn_no().toUpperCase());
                        if (PrvAuthDobj != null) {
                            setPrvAuthoDtls("Previous Authorization No : " + PrvAuthDobj.getAuthNo()
                                    + " , Authorization Validity : " + CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(PrvAuthDobj.getAuthFrom())
                                    + " To " + CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(PrvAuthDobj.getAuthUpto()) + ".");
                        } else {
                            setPrvAuthoDtls("");
                        }
                    } else {
                        visibleAreaDtls = true;
                        visibleRouteDtls = true;
                        routeCodeDetail(permit_dobj.getApplNo(), TableList.vt_permit_route, sessionVariables.getStateCodeSelected(), permitIssuingAuthCode, false);
                        if (permit_dobj.getRegion_covered() != null) {
                            String str = permit_dobj.getRegion_covered();
                            String[] temp = str.split(",");
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int j = 0; j < temp.length; j++) {
                                stringBuilder.append("(").append("'").append(temp[j]).append("'").append("),");
                            }
                            areaCodeDetail(sessionVariables.getStateCodeSelected(), stringBuilder.substring(0, stringBuilder.length() - 1), permitIssuingAuthCode);
                        }
                    }

                    String[] values = CommonPermitPrintImpl.getPmtValidateMsg(sessionVariables.getStateCodeSelected(), TableConstants.VM_PMT_RENEWAL_PUR_CD, ownDobj, permit_dobj);
                    if (values != null && !CommonUtils.isNullOrBlank(values[1])) {
                        throw new VahanException(values[1]);
                    }
                    but_function = false;
                    applPermitReadOnly(true);
                } else if (selectedValue.getPaction_code().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_FRESH_PUR_CD))) {
                    SurrenderPermitImpl surrImpl = new SurrenderPermitImpl();
                    String surrMsg = surrImpl.permitSurrPrvMsg(selectedValue.getRegnNo(), Integer.parseInt(selectedValue.getPmt_type()));
                    if (!CommonUtils.isNullOrBlank(surrMsg)) {
                        showApplPendingDialogBox(surrMsg);
                        return;
                    }
                    String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
                    int vhClassType = 0;
                    boolean vhClassFlag = false;
                    for (int i = 0; i < data.length; i++) {
                        if ((String.valueOf(dobjs.getVh_class())).equalsIgnoreCase(data[i][0])) {
                            vhClassType = Integer.valueOf(data[i][2]);
                        }
                    }
                    if (authorityDobj != null) {
                        if (authorityDobj.getSelectedVehClass().size() == 1) {
                            for (Object obj : authorityDobj.getSelectedVehClass()) {
                                permissionVhClass = String.valueOf(obj);
                            }
                        }
                    }
                    if (authorityDobj == null || ("Any").equalsIgnoreCase(permissionVhClass)) {
                        vhClassFlag = true;
                    } else {
                        for (Object obj : authorityDobj.getSelectedVehClass()) {
                            if ((String.valueOf(obj)).equalsIgnoreCase(String.valueOf(dobjs.getVh_class()))) {
                                vhClassFlag = true;
                                break;
                            }
                        }
                    }

                    Map<String, String> OffAllotResult = PermitDetailImpl.getOffAllotmentResult(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), ownerDetail.getOff_cd());
                    feeMsgIfPaidAtTymOfRegn = CommonPermitPrintImpl.getPmtFeePaidAtTymOfRegn(sessionVariables.getStateCodeSelected(), selectedValue.getRegnNo(), null);
                    if (dobjs != null && dobjs.getState_cd().equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                            && vhClassType == TableConstants.VM_VEHTYPE_TRANSPORT && vhClassFlag && ("true").equalsIgnoreCase(OffAllotResult.get("offAllowed"))) {
                        if ((OffAllotResult.get("purAllowed") == null) || !(OffAllotResult.get("purAllowed").contains(String.valueOf(TableConstants.VM_PMT_FRESH_PUR_CD)))) {
                            throw new VahanException("Office not allowed the facility of new permit application.");
                        }
                        applPermitReadOnly(false);
                        setPmtCatgDisable(false);
                        disableOtherVehicleDtls = true;
                        replacementDateDisable = false;
                        but_function = false;
                        ownerBean.getPmt_dobj().setFlage(true);
                        ownerBean.setValueinDOBJ(dobjs);
                        routeCodeDetail("NULL", TableList.vt_permit_route, sessionVariables.getStateCodeSelected(), permitIssuingAuthCode, false);
                        areaCodeDetail(sessionVariables.getStateCodeSelected(), "", permitIssuingAuthCode);
                        setRegn_no(ownerBean.getRegn_no());
                        pmt_render = false;
                        paction.clear();
                        data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
                        int i = 0;
                        for (i = 0; i < data.length; i++) {
                            if (Integer.parseInt(data[i][0]) == TableConstants.VM_PMT_FRESH_PUR_CD
                                    || Integer.parseInt(data[i][0]) == TableConstants.VM_PMT_APPLICATION_PUR_CD) {
                                paction.add(new SelectItem(data[i][0], data[i][1]));
                                break;
                            }
                        }
                        setPmtPurCode(TableConstants.VM_PMT_FRESH_PUR_CD);
                        if (Boolean.getBoolean(stateConfigMap.get("allowed_sta_rto_relation"))
                                && (getSelectOneRadio() == 1
                                || getSelectOneRadio() == 2)) {
                            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                            setPmtPurCode(new PassengerPermitDetailImpl().getPurCdFormStateConfig(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("sta_rto_condition"), parameters)));
                        }

                        if (CommonPermitPrintImpl.isVehicleSurrInPermanentMode(selectedValue.getRegnNo())) {
                            getTaxPmtDtls(selectedValue.getRegnNo(), dobjs.getVh_class(), selectedValue.getPmt_type());
                        }

                        if (!CommonUtils.isNullOrBlank(selectedValue.getRegnNo()) || !selectedValue.getRegnNo().equalsIgnoreCase("NEW")) {
                            List<HpaDobj> hypth = hpa_Impl.getHypoDetails(null, TableConstants.VM_TRANSACTION_MAST_REM_HYPO, selectedValue.getRegnNo(), true, dobjs.getState_cd());
                            hypothecationDetails_bean.setListHypthDetails(hypth);
                            if (hypth.size() > 0) {
                                setRenderhypth(true);
                            }
                        }

                    } else {
                        if (!(dobjs != null && dobjs.getState_cd().equalsIgnoreCase(sessionVariables.getStateCodeSelected()))) {
                            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert", "Invalid Vehicle");
                        } else if (!(vhClassType == TableConstants.VM_VEHTYPE_TRANSPORT)) {
                            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert", "This vehicle is Non Transport.");
                        } else if (!vhClassFlag) {
                            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert", "This Vehicle Class is not valid for this logged-in user.");
                        } else if (("false").equalsIgnoreCase(OffAllotResult.get("offAllowed"))) {
                            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registered Office of this vehicle is not mapped to this RTO.");
                        } else {
                            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert", "Invalid Vehicle");
                        }
                        FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                        ownerBean.setValueReset();
                        ownerBean.getPmt_dobj().setFlage(true);
                    }
                }
            }
        } catch (VahanException e) {
            but_function = true;
            disableOtherVehicleDtls = false;
            ownerBean.setValueReset();
            ownerBean.getPmt_dobj().setFlage(true);
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information : ", e.getMessage());
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            but_function = true;
            disableOtherVehicleDtls = false;
            ownerBean.setValueReset();
            ownerBean.getPmt_dobj().setFlage(true);
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please provide the valid Registration number", null);
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void onSelectStateTo() {
        otherStateRouteCodeDetail("NULL", TableList.vt_permit_route, sessionVariables.getStateCodeSelected(), permitIssuingAuthCode, state_to);
    }

    public void onSelectRoute(AjaxBehaviorEvent event) {
        try {
            Object dobj;
            if (event instanceof org.primefaces.event.UnselectEvent) {
                dobj = ((org.primefaces.event.UnselectEvent) event).getObject();

            } else if (event instanceof org.primefaces.event.SelectEvent) {
                dobj = ((org.primefaces.event.SelectEvent) event).getObject();

            } else {
                throw new VahanException("Unknown Selection Of Route");
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Information", e.getMessage(), FacesMessage.SEVERITY_INFO);
        } catch (Exception e) {
            LOGGER.error("ApplyFreshPermitBean.onSelectRoute() = " + e.getMessage());

        }
    }

    public void onSelectRouteUnknowing(AjaxBehaviorEvent event) {
        try {
            if (event instanceof org.primefaces.event.UnselectEvent) {
                throw new VahanException("Unknown Selection Of Route");
            } else if (event instanceof org.primefaces.event.SelectEvent) {
                throw new VahanException("Unknown Selection Of Route");
            } else if (event instanceof org.primefaces.event.ToggleEvent) {
                throw new VahanException("Unknown Selection Of Route");
            } else if (event instanceof org.primefaces.event.ToggleSelectEvent) {
                throw new VahanException("Unknown Selection Of Route");
            } else {
                throw new VahanException("Unknown Selection Of Route");
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Information", e.getMessage(), FacesMessage.SEVERITY_INFO);
        } catch (Exception e) {
            LOGGER.error("ApplyFreshPermitBean.onSelectRoute() = " + e.getMessage());

        }
    }

    public void onSelectOfficeName() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < selectedOffice.length; j++) {
                stringBuilder.append("(").append("'").append(selectedOffice[j]).append("'").append("),");
            }
            Map<String, VmPermitRouteDobj> routeMap = PassengerPermitDetailImpl.getAllRoutesForPermit(Util.getUserStateCode(), stringBuilder.substring(0, stringBuilder.length() - 1));
            if (routeMap != null) {
                for (VmPermitRouteDobj dobj : routeMap.values()) {
                    getRoute_list().add(dobj);
                }
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Information", e.getMessage(), FacesMessage.SEVERITY_INFO);
        }
    }

    public void getTaxPmtDtls(String regnNo, int vhClass, String pmt_type) throws VahanException {
        PermitCheckDetailsImpl impl = null;
        authorityDobj.getSelectedPermitCatg();
        authorityDobj.getPermitType();
        String[] codeList = ServerUtil.getFieldsReqForTax(sessionVariables.getStateCodeSelected(), vhClass);
        if (!regnNo.equalsIgnoreCase("NEW")) {
            impl = new PermitCheckDetailsImpl();
            String[] taxPmtDetls = impl.getPermitDetailsOnTaxBasedOn(regnNo, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), pmt_type);
            if (CommonUtils.isNullOrBlank(taxPmtDetls[0])) {
                setPmtType(-1);
                pmtTypeDisable = false;
            } else if (authorityDobj.getPermitType() == null || authorityDobj.getPermitType().size() < 1) {
                throw new VahanException("You are not authorized to modify.");
            } else if (codeList != null) {
                for (int i = 0; i < codeList.length; i++) {
                    if (codeList[i].equalsIgnoreCase(TableConstants.VM_PMT_TYPE_IN_TAX_CODE) && authorityDobj.getPermitType().size() == 1) {
                        for (Object obj : authorityDobj.getPermitType()) {
                            if (String.valueOf(obj).equalsIgnoreCase("ANY") || String.valueOf(obj).equalsIgnoreCase(taxPmtDetls[0])) {
                                if (CommonUtils.isNullOrBlank(taxPmtDetls[0])) {
                                    setPmtType(-1);
                                } else {
                                    setPmtType(Integer.valueOf(taxPmtDetls[0]));
                                }
                                pmtTypeDisable = true;
                                break;
                            }
                        }
                    } else if (codeList[i].equalsIgnoreCase(TableConstants.VM_PMT_TYPE_IN_TAX_CODE) && authorityDobj.getPermitType().size() > 1) {
                        for (Object obj : authorityDobj.getPermitType()) {
                            if (String.valueOf(obj).equalsIgnoreCase(taxPmtDetls[0])) {
                                if (CommonUtils.isNullOrBlank(taxPmtDetls[0])) {
                                    setPmtType(-1);
                                } else {
                                    setPmtType(Integer.valueOf(taxPmtDetls[0]));
                                }
                                pmtTypeDisable = true;
                                break;
                            }
                        }
                    }
                }
            } else {
                setPmtType(-1);
                pmtTypeDisable = false;
            }

            if (CommonUtils.isNullOrBlank(taxPmtDetls[1])) {
                this.setPmtcatg(-1);
                this.setPmtCatgDisable(false);
            } else if (authorityDobj.getSelectedPermitCatg() == null || authorityDobj.getSelectedPermitCatg().size() < 1) {
                throw new VahanException("You are not authorized to modify.");
            } else if (codeList != null) {
                for (int i = 0; i < codeList.length; i++) {
                    if (codeList[i].equalsIgnoreCase(TableConstants.VM_PMT_CATG_IN_TAX_CODE) && authorityDobj.getSelectedPermitCatg().size() == 1) {
                        for (Object obj : authorityDobj.getPermitType()) {
                            if (String.valueOf(obj).equalsIgnoreCase("ANY") || String.valueOf(obj).equalsIgnoreCase(taxPmtDetls[1])) {
                                if (CommonUtils.isNullOrBlank(taxPmtDetls[0])) {
                                    setPmtType(-1);
                                } else {
                                    setPmtType(Integer.valueOf(taxPmtDetls[0]));
                                }
                                pmtTypeDisable = true;
                                String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
                                for (int j = 0; j < data.length; j++) {
                                    if (data[j][0].equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                                            && data[j][3].equalsIgnoreCase(taxPmtDetls[0])) {
                                        pmtCategory.add(new SelectItem(data[j][1], data[j][2]));
                                    }
                                }
                                this.setPmtcatg(Integer.valueOf(taxPmtDetls[1]));
                                this.setPmtCatgDisable(true);
                                break;
                            }
                        }
                    } else if (codeList[i].equalsIgnoreCase(TableConstants.VM_PMT_CATG_IN_TAX_CODE) && authorityDobj.getSelectedPermitCatg().size() > 1) {
                        for (Object obj1 : authorityDobj.getSelectedPermitCatg()) {
                            if (CommonUtils.isNullOrBlank(taxPmtDetls[0])) {
                                setPmtType(-1);
                            } else {
                                setPmtType(Integer.valueOf(taxPmtDetls[0]));
                            }
                            pmtTypeDisable = true;
                            String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
                            for (int j = 0; j < data.length; j++) {
                                if (data[j][0].equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                                        && data[j][3].equalsIgnoreCase(taxPmtDetls[0])
                                        && data[j][1].equalsIgnoreCase(String.valueOf(obj1))) {
                                    pmtCategory.add(new SelectItem(data[j][1], data[j][2]));
                                }
                            }
                            this.setPmtcatg(Integer.valueOf(taxPmtDetls[1]));
                            this.setPmtCatgDisable(true);
                            break;
                        }
                    } else if (!CommonUtils.isNullOrBlank(taxPmtDetls[1]) && Integer.parseInt(taxPmtDetls[1]) != 0) {
                        for (Object obj1 : authorityDobj.getSelectedPermitCatg()) {
                            String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
                            for (int j = 0; j < data.length; j++) {
                                if (data[j][0].equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                                        && data[j][3].equalsIgnoreCase(taxPmtDetls[0])
                                        && data[j][1].equalsIgnoreCase(String.valueOf(obj1))) {
                                    pmtCategory.add(new SelectItem(data[j][1], data[j][2]));
                                }
                            }
                            this.setPmtcatg(Integer.valueOf(taxPmtDetls[1]));
                        }
                    }
                }
            } else {
                this.setPmtcatg(-1);
                this.setPmtCatgDisable(false);
            }

            if (!CommonUtils.isNullOrBlank(taxPmtDetls[5])) {
                onSelectPermitType(taxPmtDetls[5]);
                setRegn_domain(taxPmtDetls[5]);
            } else {
                onSelectPermitType("");
            }
        }
    }

    public void showPermitDetailsPaidFeeAtNewRegn(String regn_no, String state_cd, int off_cd, String pmt_type) {
        PermitCheckDetailsImpl pmtDtlsImpl = new PermitCheckDetailsImpl();
        String[] taxPmtDetls = pmtDtlsImpl.getPermitDetailsOnTaxBasedOn(regn_no, state_cd, off_cd, null);
        pmtCategory.clear();
        if (CommonUtils.isNullOrBlank(taxPmtDetls[0])) {
            this.setPmtType(-1);
        } else {
            this.setPmtType(Integer.parseInt(taxPmtDetls[0]));
            this.setPmtTypeDisable(true);
        }

        if (CommonUtils.isNullOrBlank(taxPmtDetls[1])) {
            this.setPmtcatg(-1);
            pmtCategory.clear();
            String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equalsIgnoreCase(Util.getUserStateCode())
                        && data[i][3].equalsIgnoreCase(taxPmtDetls[0])) {
                    pmtCategory.add(new SelectItem(data[i][1], data[i][2]));
                }
            }
        } else {
            String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equalsIgnoreCase(Util.getUserStateCode())
                        && data[i][3].equalsIgnoreCase(taxPmtDetls[0])) {
                    pmtCategory.add(new SelectItem(data[i][1], data[i][2]));
                }
            }
            this.setPmtcatg(Integer.parseInt(taxPmtDetls[1]));
            this.setPmtCatgDisable(true);
        }

        if (CommonUtils.isNullOrBlank(taxPmtDetls[2]) || ("-1").equals(taxPmtDetls[2]) || ("0").equals(taxPmtDetls[2])) {
            this.setServices_type(-1);
        } else {
            this.setServices_type(Integer.parseInt(taxPmtDetls[2]));
            this.setServicesTypeDisable(true);
        }

        String[] taxPmtDetlsVha = pmtDtlsImpl.getPermitDetailsFromVhaNewRegn(regn_no, state_cd);
        if (!CommonUtils.isNullOrBlank(taxPmtDetlsVha[0])) {
            if (this.getPmtType() == -1) {
                this.setPmtType(Integer.parseInt(taxPmtDetlsVha[0]));
                this.setPmtTypeDisable(true);
            }

            if (this.getPmtcatg() == -1) {
                if (CommonUtils.isNullOrBlank(taxPmtDetlsVha[1])) {
                    this.setPmtcatg(-1);
                    pmtCategory.clear();
                    String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
                    for (int i = 0; i < data.length; i++) {
                        if (data[i][0].equalsIgnoreCase(Util.getUserStateCode())
                                && data[i][3].equalsIgnoreCase(taxPmtDetlsVha[0])) {
                            pmtCategory.add(new SelectItem(data[i][1], data[i][2]));
                        }
                    }
                } else {
                    String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
                    for (int i = 0; i < data.length; i++) {
                        if (data[i][0].equalsIgnoreCase(Util.getUserStateCode())
                                && data[i][3].equalsIgnoreCase(taxPmtDetlsVha[0])) {
                            pmtCategory.add(new SelectItem(data[i][1], data[i][2]));
                        }
                    }
                    this.setPmtcatg(Integer.parseInt(taxPmtDetlsVha[1]));
                    this.setPmtCatgDisable(true);
                }
            }

            if (this.getServices_type() == -1) {
                if (CommonUtils.isNullOrBlank(taxPmtDetlsVha[2]) || ("-1").equals(taxPmtDetlsVha[2]) || ("0").equals(taxPmtDetlsVha[2])) {
                    this.setServices_type(-1);
                } else {
                    this.setServices_type(Integer.parseInt(taxPmtDetlsVha[2]));
                    this.setServicesTypeDisable(true);
                }
            }

            if (!CommonUtils.isNullOrBlank(taxPmtDetlsVha[3])) {
                taxPmtDetlsVha[3] = taxPmtDetlsVha[3].substring(0, taxPmtDetlsVha[3].length() - 1);
                areaCodeDetail(sessionVariables.getStateCodeSelected(), taxPmtDetlsVha[3], permitIssuingAuthCode);
                this.setDisableAreaDtls(true);
            }
        }
    }

    public void dateSelectEvent() {
        auth_dobj.setAuthUpto(ServerUtil.dateRange(auth_dobj.getAuthFrom(), 1, 0, -1));
    }

    public PermitOwnerDetailDobj getPermit_owner_detail_dobj() {
        return permit_owner_detail_dobj;
    }

    public void setPermit_owner_detail_dobj(PermitOwnerDetailDobj permit_owner_detail_dobj) {
        this.permit_owner_detail_dobj = permit_owner_detail_dobj;
    }

    public PermitOwnerDetailDobj getPermit_owner_detail_dobj_prv() {
        return permit_owner_detail_dobj_prv;
    }

    public void setPermit_owner_detail_dobj_prv(PermitOwnerDetailDobj permit_owner_detail_dobj_prv) {
        this.permit_owner_detail_dobj_prv = permit_owner_detail_dobj_prv;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public PermitHomeAuthDobj getAuth_dobj() {
        return auth_dobj;
    }

    public void setAuth_dobj(PermitHomeAuthDobj auth_dobj) {
        this.auth_dobj = auth_dobj;
    }

    public boolean isVisibleAuthDetails() {
        return visibleAuthDetails;
    }

    public void setVisibleAuthDetails(boolean visibleAuthDetails) {
        this.visibleAuthDetails = visibleAuthDetails;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public String getErrorShowMsg() {
        return errorShowMsg;
    }

    public void setErrorShowMsg(String errorShowMsg) {
        this.errorShowMsg = errorShowMsg;
    }

    public PermitCheckDetailsBean getPmtCheckDtsl() {
        return pmtCheckDtsl;
    }

    public void setPmtCheckDtsl(PermitCheckDetailsBean pmtCheckDtsl) {
        this.pmtCheckDtsl = pmtCheckDtsl;
    }

    public boolean isDisableOtherVehicleDtls() {
        return disableOtherVehicleDtls;
    }

    public void setDisableOtherVehicleDtls(boolean disableOtherVehicleDtls) {
        this.disableOtherVehicleDtls = disableOtherVehicleDtls;
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

    public Date getDateOfReplacement() {
        return dateOfReplacement;
    }

    public void setDateOfReplacement(Date dateOfReplacement) {
        this.dateOfReplacement = dateOfReplacement;
    }

    public Date getCurrenDate() {
        return currenDate;
    }

    public void setCurrenDate(Date currenDate) {
        this.currenDate = currenDate;
    }

    public Date getMaxLimitOfReplacementDate() {
        return maxLimitOfReplacementDate;
    }

    public void setMaxLimitOfReplacementDate(Date maxLimitOfReplacementDate) {
        this.maxLimitOfReplacementDate = maxLimitOfReplacementDate;
    }

    public boolean isReplacementDateDisable() {
        return replacementDateDisable;
    }

    public void setReplacementDateDisable(boolean replacementDateDisable) {
        this.replacementDateDisable = replacementDateDisable;
    }

    public PermitHomeAuthDobj getAuth_dobj_psv() {
        return auth_dobj_psv;
    }

    public void setAuth_dobj_psv(PermitHomeAuthDobj auth_dobj_psv) {
        this.auth_dobj_psv = auth_dobj_psv;
    }

    public boolean isNoOfTripsrendered() {
        return noOfTripsrendered;
    }

    public void setNoOfTripsrendered(boolean noOfTripsrendered) {
        this.noOfTripsrendered = noOfTripsrendered;
    }

    public void notFillHomeAuthDetails(AjaxBehaviorEvent event) {
        auth_dobj.setAuthNo("");
        auth_dobj.setAuthFrom(null);
        auth_dobj.setAuthUpto(null);
    }

    public boolean isNotFillHomeAuth() {
        return notFillHomeAuth;
    }

    public void setNotFillHomeAuth(boolean notFillHomeAuth) {
        this.notFillHomeAuth = notFillHomeAuth;
    }

    public String getNew_o_name() {
        return new_o_name;
    }

    public void setNew_o_name(String new_o_name) {
        this.new_o_name = new_o_name;
    }

    public String getNew_f_name() {
        return new_f_name;
    }

    public void setNew_f_name(String new_f_name) {
        this.new_f_name = new_f_name;
    }

    public String getNew_vh_class() {
        return new_vh_class;
    }

    public void setNew_vh_class(String new_vh_class) {
        this.new_vh_class = new_vh_class;
    }

    public String getNew_regn_no() {
        return new_regn_no;
    }

    public void setNew_regn_no(String new_regn_no) {
        this.new_regn_no = new_regn_no;
    }

    public String getOfferLetterNo() {
        return offerLetterNo;
    }

    public void setOfferLetterNo(String offerLetterNo) {
        this.offerLetterNo = offerLetterNo;
    }

    public boolean isOffLTRender() {
        return offLTRender;
    }

    public void setOffLTRender(boolean offLTRender) {
        this.offLTRender = offLTRender;
    }

    public boolean isRegnNoRender() {
        return regnNoRender;
    }

    public void setRegnNoRender(boolean regnNoRender) {
        this.regnNoRender = regnNoRender;
    }

    public boolean isPmtDialogeVisible() {
        return pmtDialogeVisible;
    }

    public void setPmtDialogeVisible(boolean pmtDialogeVisible) {
        this.pmtDialogeVisible = pmtDialogeVisible;
    }

    public List getNewVhClassType() {
        return newVhClassType;
    }

    public void setNewVhClassType(List newVhClassType) {
        this.newVhClassType = newVhClassType;
    }

    public boolean isDisableRegnNo() {
        return disableRegnNo;
    }

    public void setDisableRegnNo(boolean disableRegnNo) {
        this.disableRegnNo = disableRegnNo;
    }

    public List<PermitReservationDobj> getResList() {
        return resList;
    }

    public void setResList(List<PermitReservationDobj> resList) {
        this.resList = resList;
    }

    public boolean isDisableNpDetails() {
        return disableNpDetails;
    }

    public void setDisableNpDetails(boolean disableNpDetails) {
        this.disableNpDetails = disableNpDetails;
    }

    public String getPmtSubType() {
        return pmtSubType;
    }

    public void setPmtSubType(String pmtSubType) {
        this.pmtSubType = pmtSubType;
    }

    public boolean isPmtSubTypeRender() {
        return pmtSubTypeRender;
    }

    public void setPmtSubTypeRender(boolean pmtSubTypeRender) {
        this.pmtSubTypeRender = pmtSubTypeRender;
    }

    public boolean isRenderPrintDocument() {
        return renderPrintDocument;
    }

    public void setRenderPrintDocument(boolean renderPrintDocument) {
        this.renderPrintDocument = renderPrintDocument;
    }

    public String getMsgOfPrintDocument() {
        return msgOfPrintDocument;
    }

    public void setMsgOfPrintDocument(String msgOfPrintDocument) {
        this.msgOfPrintDocument = msgOfPrintDocument;
    }

    public String getValueOfCommandButton() {
        return valueOfCommandButton;
    }

    public void setValueOfCommandButton(String valueOfCommandButton) {
        this.valueOfCommandButton = valueOfCommandButton;
    }

    public PermitPaidFeeDtlsBean getPaidFeeDtls() {
        return paidFeeDtls;
    }

    public void setPaidFeeDtls(PermitPaidFeeDtlsBean paidFeeDtls) {
        this.paidFeeDtls = paidFeeDtls;
    }

    public String getValidityMsg() {
        return validityMsg;
    }

    public void setValidityMsg(String validityMsg) {
        this.validityMsg = validityMsg;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public boolean isParkingDisabled() {
        return parkingDisabled;
    }

    public void setParkingDisabled(boolean parkingDisabled) {
        this.parkingDisabled = parkingDisabled;
    }

    public String getJoreny_PURPOSE() {
        return joreny_PURPOSE;
    }

    public void setJoreny_PURPOSE(String joreny_PURPOSE) {
        this.joreny_PURPOSE = joreny_PURPOSE;
    }

    public boolean isJorenyPurposedDisabled() {
        return jorenyPurposedDisabled;
    }

    public void setJorenyPurposedDisabled(boolean jorenyPurposedDisabled) {
        this.jorenyPurposedDisabled = jorenyPurposedDisabled;
    }

    public String getGoodsToCarry() {
        return goodsToCarry;
    }

    public void setGoodsToCarry(String goodsToCarry) {
        this.goodsToCarry = goodsToCarry;
    }

    public boolean isGoodsToCarryDisabled() {
        return goodsToCarryDisabled;
    }

    public void setGoodsToCarryDisabled(boolean goodsToCarryDisabled) {
        this.goodsToCarryDisabled = goodsToCarryDisabled;
    }

    public List getRegionCoveredListNP() {
        return regionCoveredListNP;
    }

    public void setRegionCoveredListNP(List regionCoveredListNP) {
        this.regionCoveredListNP = regionCoveredListNP;
    }

    public String getPrvAuthoDtls() {
        return prvAuthoDtls;
    }

    public void setPrvAuthoDtls(String prvAuthoDtls) {
        this.prvAuthoDtls = prvAuthoDtls;
    }

    public String getMsgForRemarks() {
        return msgForRemarks;
    }

    public void setMsgForRemarks(String msgForRemarks) {
        this.msgForRemarks = msgForRemarks;
    }

    public HypothecationDetailsBean getHypothecationDetails_bean() {
        return hypothecationDetails_bean;
    }

    public void setHypothecationDetails_bean(HypothecationDetailsBean hypothecationDetails_bean) {
        this.hypothecationDetails_bean = hypothecationDetails_bean;
    }

    public boolean isRenderhypth() {
        return renderhypth;
    }

    public void setRenderhypth(boolean renderhypth) {
        this.renderhypth = renderhypth;
    }

    public String getFeeMsgIfPaidAtTymOfRegn() {
        return feeMsgIfPaidAtTymOfRegn;
    }

    public void setFeeMsgIfPaidAtTymOfRegn(String feeMsgIfPaidAtTymOfRegn) {
        this.feeMsgIfPaidAtTymOfRegn = feeMsgIfPaidAtTymOfRegn;
    }

    public String getApplNoForAck() {
        return applNoForAck;
    }

    public void setApplNoForAck(String applNoForAck) {
        this.applNoForAck = applNoForAck;
    }

    public Date getPmt_valid_from() {
        return pmt_valid_from;
    }

    public void setPmt_valid_from(Date pmt_valid_from) {
        this.pmt_valid_from = pmt_valid_from;
    }

    public boolean isRender_valid_from() {
        return render_valid_from;
    }

    public void setRender_valid_from(boolean render_valid_from) {
        this.render_valid_from = render_valid_from;
    }

    public String getRegn_domain() {
        return regn_domain;
    }

    public void setRegn_domain(String regn_domain) {
        this.regn_domain = regn_domain;
    }

    public int getRouteLength() {
        return routeLength;
    }

    public void setRouteLength(int routeLength) {
        this.routeLength = routeLength;
    }

    public boolean isVisibleAreaDtls() {
        return visibleAreaDtls;
    }

    public void setVisibleAreaDtls(boolean visibleAreaDtls) {
        this.visibleAreaDtls = visibleAreaDtls;
    }

    public boolean isVisibleRouteDtls() {
        return visibleRouteDtls;
    }

    public void setVisibleRouteDtls(boolean visibleRouteDtls) {
        this.visibleRouteDtls = visibleRouteDtls;
    }

    public boolean isOfferApprovalPanelRendered() {
        return offerApprovalPanelRendered;
    }

    public void setOfferApprovalPanelRendered(boolean offerApprovalPanelRendered) {
        this.offerApprovalPanelRendered = offerApprovalPanelRendered;
    }

    public boolean isRegnNoDisable() {
        return regnNoDisable;
    }

    public void setRegnNoDisable(boolean regnNoDisable) {
        this.regnNoDisable = regnNoDisable;
    }

    public boolean isPmtTypeDisable() {
        return pmtTypeDisable;
    }

    public void setPmtTypeDisable(boolean pmtTypeDisable) {
        this.pmtTypeDisable = pmtTypeDisable;
    }

    public String getVahanMessages() {
        return vahanMessages;
    }

    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    public boolean isPeriodModeDisable() {
        return periodModeDisable;
    }

    public void setPeriodModeDisable(boolean periodModeDisable) {
        this.periodModeDisable = periodModeDisable;
    }

    public boolean isPeriodValueDisable() {
        return periodValueDisable;
    }

    public void setPeriodValueDisable(boolean periodValueDisable) {
        this.periodValueDisable = periodValueDisable;
    }

    public boolean isPmtCatgDisable() {
        return pmtCatgDisable;
    }

    public void setPmtCatgDisable(boolean pmtCatgDisable) {
        this.pmtCatgDisable = pmtCatgDisable;
    }

    public boolean isServicesTypeDisable() {
        return servicesTypeDisable;
    }

    public void setServicesTypeDisable(boolean servicesTypeDisable) {
        this.servicesTypeDisable = servicesTypeDisable;
    }

    public boolean isDisableOrderDtls() {
        return disableOrderDtls;
    }

    public void setDisableOrderDtls(boolean disableOrderDtls) {
        this.disableOrderDtls = disableOrderDtls;
    }

    public String getOfferNumber() {
        return offerNumber;
    }

    public void setOfferNumber(String offerNumber) {
        this.offerNumber = offerNumber;
    }

    public boolean isOfferNumberRender() {
        return offerNumberRender;
    }

    public void setOfferNumberRender(boolean offerNumberRender) {
        this.offerNumberRender = offerNumberRender;
    }

    public boolean isRenderGetDetails() {
        return renderGetDetails;
    }

    public void setRenderGetDetails(boolean renderGetDetails) {
        this.renderGetDetails = renderGetDetails;
    }

    public AITPStateCoveredBean getStatecoveredBean() {
        return statecoveredBean;
    }

    public void setStatecoveredBean(AITPStateCoveredBean statecoveredBean) {
        this.statecoveredBean = statecoveredBean;
    }

    public String getTaxExem() {
        return taxExem;
    }

    public void setTaxExem(String taxExem) {
        this.taxExem = taxExem;
    }

    public List<PassengerPermitDetailDobj> getMultiPermitDetails() {
        return multiPermitDetails;
    }

    public void setMultiPermitDetails(List<PassengerPermitDetailDobj> multiPermitDetails) {
        this.multiPermitDetails = multiPermitDetails;
    }

    public PassengerPermitDetailDobj getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(PassengerPermitDetailDobj selectedValue) {
        this.selectedValue = selectedValue;
    }

    public String getPermit_no() {
        return permit_no;
    }

    public void setPermit_no(String permit_no) {
        this.permit_no = permit_no;
    }

    public int getNo_of_trip() {
        return no_of_trip;
    }

    public void setNo_of_trip(int no_of_trip) {
        this.no_of_trip = no_of_trip;
    }

    public boolean isMultiApplAllow() {
        return multiApplAllow;
    }

    public void setMultiApplAllow(boolean multiApplAllow) {
        this.multiApplAllow = multiApplAllow;
    }

    public boolean isShowRegionCovered() {
        return showRegionCovered;
    }

    public void setShowRegionCovered(boolean showRegionCovered) {
        this.showRegionCovered = showRegionCovered;
    }

    public int getRegionCovered() {
        return regionCovered;
    }

    public void setRegionCovered(int regionCovered) {
        this.regionCovered = regionCovered;
    }

    public boolean isShowOwnerDLAndVoterId() {
        return showOwnerDLAndVoterId;
    }

    public void setShowOwnerDLAndVoterId(boolean showOwnerDLAndVoterId) {
        this.showOwnerDLAndVoterId = showOwnerDLAndVoterId;
    }

    public boolean isAllowSpecialVhClass() {
        return allowSpecialVhClass;
    }

    public void setAllowSpecialVhClass(boolean allowSpecialVhClass) {
        this.allowSpecialVhClass = allowSpecialVhClass;
    }

    public String getPmt_type_allowed() {
        return pmt_type_allowed;
    }

    public void setPmt_type_allowed(String pmt_type_allowed) {
        this.pmt_type_allowed = pmt_type_allowed;
    }

    public String[] getSelectedOffice() {
        return selectedOffice;
    }

    public void setSelectedOffice(String[] selectedOffice) {
        this.selectedOffice = selectedOffice;
    }

    public List getOfficeList() {
        return officeList;
    }

    public void setOfficeList(List officeList) {
        this.officeList = officeList;
    }

    public List<VmPermitRouteDobj> getSelectedRouteList() {
        return selectedRouteList;
    }

    public void setSelectedRouteList(List<VmPermitRouteDobj> selectedRouteList) {
        this.selectedRouteList = selectedRouteList;
    }

    public List<VmPermitRouteDobj> getRoute_list() {
        return route_list;
    }

    public void setRoute_list(List<VmPermitRouteDobj> route_list) {
        this.route_list = route_list;
    }

    public boolean isShowOtherRoute() {
        return showOtherRoute;
    }

    public void setShowOtherRoute(boolean showOtherRoute) {
        this.showOtherRoute = showOtherRoute;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public boolean isTaxExemptForKL() {
        return taxExemptForKL;
    }

    public void setTaxExemptForKL(boolean taxExemptForKL) {
        this.taxExemptForKL = taxExemptForKL;
    }

    public String getAuth_periodMode() {
        return auth_periodMode;
    }

    public void setAuth_periodMode(String auth_periodMode) {
        this.auth_periodMode = auth_periodMode;
    }

    public int getAuth_period() {
        return auth_period;
    }

    public void setAuth_period(int auth_period) {
        this.auth_period = auth_period;
    }

    public List getAuth_period_list() {
        return auth_period_list;
    }

    public void setAuth_period_list(List auth_period_list) {
        this.auth_period_list = auth_period_list;
    }

    public boolean isDisableAuthPeriod() {
        return disableAuthPeriod;
    }

    public void setDisableAuthPeriod(boolean disableAuthPeriod) {
        this.disableAuthPeriod = disableAuthPeriod;
    }

    public boolean isRenderAuthPeriod() {
        return renderAuthPeriod;
    }

    public void setRenderAuthPeriod(boolean renderAuthPeriod) {
        this.renderAuthPeriod = renderAuthPeriod;
    }

    public boolean isRender_payment_table_aitp() {
        return render_payment_table_aitp;
    }

    public void setRender_payment_table_aitp(boolean render_payment_table_aitp) {
        this.render_payment_table_aitp = render_payment_table_aitp;
    }

    public boolean isVisibleInterStateRouteDtls() {
        return visibleInterStateRouteDtls;
    }

    public void setVisibleInterStateRouteDtls(boolean visibleInterStateRouteDtls) {
        this.visibleInterStateRouteDtls = visibleInterStateRouteDtls;
    }

    public String getState_to() {
        return state_to;
    }

    public void setState_to(String state_to) {
        this.state_to = state_to;
    }

    public DualListModel<PermitRouteList> getOtherStateRouteManage() {
        return otherStateRouteManage;
    }

    public void setOtherStateRouteManage(DualListModel<PermitRouteList> otherStateRouteManage) {
        this.otherStateRouteManage = otherStateRouteManage;
    }

    public List<PermitRouteList> getOtherStateActionSource() {
        return otherStateActionSource;
    }

    public void setOtherStateActionSource(List<PermitRouteList> otherStateActionSource) {
        this.otherStateActionSource = otherStateActionSource;
    }

    public List<PermitRouteList> getOtherStateActionTarget() {
        return otherStateActionTarget;
    }

    public void setOtherStateActionTarget(List<PermitRouteList> otherStateActionTarget) {
        this.otherStateActionTarget = otherStateActionTarget;
    }

    public List<SelectItem> getStateToList() {
        return stateToList;
    }

    public void setStateToList(List<SelectItem> stateToList) {
        this.stateToList = stateToList;
    }

    public String getVia_route_oth() {
        return via_route_oth;
    }

    public void setVia_route_oth(String via_route_oth) {
        this.via_route_oth = via_route_oth;
    }

    public boolean isTaxDependOnPmt() {
        return taxDependOnPmt;
    }

    public void setTaxDependOnPmt(boolean taxDependOnPmt) {
        this.taxDependOnPmt = taxDependOnPmt;
    }

    public boolean isShowTimeTable() {
        return showTimeTable;
    }

    public void setShowTimeTable(boolean showTimeTable) {
        this.showTimeTable = showTimeTable;
    }

    public PermitTimeTableBean getPmtTimeTableBean() {
        return pmtTimeTableBean;
    }

    public void setPmtTimeTableBean(PermitTimeTableBean pmtTimeTableBean) {
        this.pmtTimeTableBean = pmtTimeTableBean;
    }

    public boolean isShowTimeTableDetails() {
        return showTimeTableDetails;
    }

    public void setShowTimeTableDetails(boolean showTimeTableDetails) {
        this.showTimeTableDetails = showTimeTableDetails;
    }

    public List<PermitTimeTableDobj> getPrevTimeTableList() {
        return prevTimeTableList;
    }

    public void setPrevTimeTableList(List<PermitTimeTableDobj> prevTimeTableList) {
        this.prevTimeTableList = prevTimeTableList;
    }

    public boolean isRenderDocUploadTab() {
        return renderDocUploadTab;
    }

    public void setRenderDocUploadTab(boolean renderDocUploadTab) {
        this.renderDocUploadTab = renderDocUploadTab;
    }

    public String getDmsUrl() {
        return dmsUrl;
    }

    public void setDmsUrl(String dmsUrl) {
        this.dmsUrl = dmsUrl;
    }

    public boolean isRenderGoodsPassengerTax() {
        return renderGoodsPassengerTax;
    }

    public void setRenderGoodsPassengerTax(boolean renderGoodsPassengerTax) {
        this.renderGoodsPassengerTax = renderGoodsPassengerTax;
    }

    public Date getMinReplacementDate() {
        return minReplacementDate;
    }

    public void setMinReplacementDate(Date minReplacementDate) {
        this.minReplacementDate = minReplacementDate;
    }

    public String getSelectedRouteString() {
        return selectedRouteString;
    }

    public void setSelectedRouteString(String selectedRouteString) {
        this.selectedRouteString = selectedRouteString;
    }

    public boolean isExempt_echallan() {
        return exempt_echallan;
    }

    public void setExempt_echallan(boolean exempt_echallan) {
        this.exempt_echallan = exempt_echallan;
    }
}
