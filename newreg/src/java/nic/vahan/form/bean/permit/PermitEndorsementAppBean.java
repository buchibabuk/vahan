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
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.AITPStateCoveredDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitHomeAuthDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.dobj.permit.VmPermitRouteDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.permit.PermitEndorsementAppImpl;
import nic.vahan.form.impl.permit.PermitDetailImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DualListModel;

/**
 *
 * @author nicsi
 */
@ManagedBean(name = "pmtEndorsement")
@ViewScoped
public class PermitEndorsementAppBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(PermitEndorsementAppBean.class);
    boolean showHide;
    boolean eApplshowHide;
    PermitOwnerDetailDobj owner_dobj = null;
    PermitOwnerDetailImpl ownerImpl = null;
    PermitDetailDobj pmtDobj = null;
    PermitDetailImpl pmtImpl = null;
    private String via_route = "";
    private String domain;
    private String pmtcatg;
    private String paction_code;
    private String pmt_type_code;
    private List pmt_mast = new ArrayList();
    private List purCd = new ArrayList();
    private List ser_type = new ArrayList();
    private List pmtCategory = new ArrayList();
    private List route_mast = new ArrayList();
    private List area_mast = new ArrayList();
    private List<PassengerPermitDetailDobj> prv_route_list = new ArrayList();
    private List<PassengerPermitDetailDobj> next_route_list = new ArrayList();
    private DualListModel<PermitRouteList> routeManage;
    private DualListModel<PermitRouteList> areaManage;
    private List<PermitRouteList> actionSource = new ArrayList<>();
    private List<PermitRouteList> actionTarget = new ArrayList<>();
    private List<PermitRouteList> actionSourceByFlag = new ArrayList<>();
    private List<PermitRouteList> actionTargetByFlag = new ArrayList<>();
    private List<PermitRouteList> areaActionSource = new ArrayList<>();
    private List<PermitRouteList> areaActionTarget = new ArrayList<>();
    private List<PermitRouteList> prvactionTarget = new ArrayList<>();
    private String user_regn_no;
    private String app_no_msg;
    private String regn_no;
    private String pmt_type;
    private String floc;
    private String start_point;
    private String tloc;
    private String parking;
    private String appl_NO;
    private String joreny_PURPOSE;
    private String goods_TO_CARRY;
    private String header = "";
    private String number_OF_TRIPS;
    private List<String> route_map;
    private String sel_paction = "";
    private String sel_pmt_mast;
    private String services_type;
    private boolean but_function = true;
    PassengerPermitDetailImpl passImpl = null;
    PermitEndorsementAppImpl pmtEndoImpl = null;
    private String routeCode;
    private PassengerPermitDetailDobj permit_detail_dobj = new PassengerPermitDetailDobj();
    private PassengerPermitDetailDobj permit_detail_dobj_prv;
    @ManagedProperty(value = "#{pmt_owner_dtls}")
    private PermitOwnerDetailBean ownerBean;
    @ManagedProperty(value = "#{Permit_Dtls_bean}")
    private PermitDetailBean permit_Dtls_bean;
    private List<ComparisonBean> compBeanList = new ArrayList<ComparisonBean>();
    private String region_covereds;
    private List<ComparisonBean> prevChangedDataList;
    private String masterLayout = "/masterLayoutPage.xhtml";
    private boolean visible_Route_area = false;
    private int tabIndex;
    private String errorShowMsg = "";
    private boolean disableOtherVehicleDtls = false;
    @ManagedProperty(value = "#{pmtDtlsBean}")
    private PermitCheckDetailsBean pmtCheckDtsl;
    private List arrayInsCmpy = new ArrayList();
    private List arrayInsType = new ArrayList();
    private Date currenDate = new Date();
    private boolean noOfTripsrendered = false;
    private boolean panel_visible = false;
    private Date issue_dt;
    private Date valid_from;
    private Date valid_upto;
    private String pmt_no;
    // Nitin KUmar 21-01-2016 Begin 
    private OwnerDetailsDobj ownerDetail;
    // Nitin KUmar 21-01-2016 End
    private String route_flag;
    private String state_cd;
    private DualListModel<PermitRouteList> routeManageByFlag;
    private String via_route_flag = "";
    private String applNoForAck;
    @ManagedProperty(value = "#{StateCovered}")
    private AITPStateCoveredBean statecoveredBean;
    private boolean visible_route_dtls = false;
    private boolean visible_area = false;
    private PermitHomeAuthDobj auth_dobj = new PermitHomeAuthDobj();
    private boolean renderRouteFlag = false;
    private boolean allowRouteFlag = false;
    private Map<String, String> stateConfigMap = null;
    private String[] selectedOffice;
    private List officeList = new ArrayList();
    private List<VmPermitRouteDobj> selectedRouteList;
    private List<VmPermitRouteDobj> route_list = new ArrayList<>();
    private boolean showOtherRoute = false;
    private boolean otherRouteAllow = false;
    PassengerPermitDetailImpl passImp = null;
    private boolean modifyTrip = false;
    private boolean disableRoute = false;
    private boolean disableRouteWithFlag = false;
    private boolean renderModifyTrips = false;
    private boolean disableOtherOffRoute = false;
    private String vahanMessages = null;
    private SessionVariables sessionVariables = null;

    public PermitEndorsementAppBean() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
            vahanMessages = "Session time Out";
            return;
        }
        routeManage = new DualListModel<>(actionSource, actionTarget);
        areaManage = new DualListModel<>(areaActionSource, areaActionTarget);
        routeManageByFlag = new DualListModel<>(actionSourceByFlag, actionTargetByFlag);

        String[][] data;

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

        data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
        for (int i = 0; i < data.length; i++) {
            if (Integer.parseInt(data[i][0]) == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD) {
                purCd.add(new SelectItem(data[i][0], data[i][1]));
                sel_paction = data[i][0];
                break;
            }
        }

        data = MasterTableFiller.masterTables.TM_OFFICE.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][13].equalsIgnoreCase(Util.getUserStateCode()) && TableConstants.OTHER_OFFICE_ROUTE_ALLOWED_FOR_PERMIT.contains(data[i][0])) {
                officeList.add(new SelectItem(data[i][0], data[i][1]));
            }
        }

        stateConfigMap = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
        otherRouteAllow = Boolean.parseBoolean(stateConfigMap.get("other_office_route_allow"));
        renderModifyTrips = Boolean.parseBoolean(stateConfigMap.get("render_modify_trips"));
    }

    @PostConstruct
    public void init() {
        try {
            but_function = true;
            owner_dobj = new PermitOwnerDetailDobj();
            owner_dobj.setDisable(true);
            this.state_cd = Util.getUserStateCode();
            prv_route_list.clear();

            if (String.valueOf(appl_details.getCurrent_action_cd()) == null) {
                showHide = false;
                eApplshowHide = true;
            } else {
                String action_cd = String.valueOf(appl_details.getCurrent_action_cd());
                if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_VARIATION_ENDORSEMENT_APPL))
                        && CommonUtils.isNullOrBlank(appl_details.getAppl_no())) {
                    header = "PERMIT ENDORSEMENT ENTRY";
                    showHide = true;
                    eApplshowHide = true;
                } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_VARIATION_ENDORSEMENT_APPL))
                        && !CommonUtils.isNullOrBlank(appl_details.getAppl_no())) {
                    header = "PERMIT ENDORSEMENT RE-ENTRY";
                    prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                    panel_visible = true;
                    bt_fill_report_for_verify();
                    showHide = false;
                    eApplshowHide = false;
                    but_function = false;
                } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_VARIATION_ENDORSEMENT_VERIFICATION))
                        && !CommonUtils.isNullOrBlank(appl_details.getAppl_no())) {
                    header = "PERMIT ENDORSEMENT VARIFICATION";
                    prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                    panel_visible = true;
                    bt_fill_report_for_verify();
                    showHide = false;
                    eApplshowHide = false;
                    but_function = false;
                    renderModifyTrips = false;
                } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_VARIATION_ENDORSEMENT_APPROVAL))
                        && !CommonUtils.isNullOrBlank(appl_details.getAppl_no())) {
                    header = "PERMIT ENDORSEMENT APPROVAL";
                    prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                    panel_visible = true;
                    bt_fill_report_for_verify();
                    showHide = false;
                    eApplshowHide = false;
                    but_function = false;
                    renderModifyTrips = false;
                }
            }
        } catch (Exception e) {
        }
    }

    public void get_PemritDetail() {
        tabIndex = 0;
        but_function = false;
        owner_dobj.setDisable(false);
        disableOtherVehicleDtls = true;
        prv_route_list.clear();
        FacesMessage message;

        InsDobj insDobj = null;

        try {

            //nitin kumar - 21-01-2015 BEGIN 
            this.state_cd = Util.getUserStateCode();
            OwnerImpl owner_Impl = new OwnerImpl();
            String Regn_No = CommonPermitPrintImpl.getRegnNoinVtPermit(getUser_regn_no().toUpperCase(), Util.getUserStateCode());
            List<OwnerDetailsDobj> ownerDetailsDobjList = owner_Impl.getOwnerDetailsList(Regn_No.toUpperCase().trim(), null);
            ownerDetailsDobjList = owner_Impl.getStateWiseOwnerList(ownerDetailsDobjList, Util.getUserStateCode());

            if (ownerDetailsDobjList != null && ownerDetailsDobjList.size() == 1) {
                ownerDetail = ownerDetailsDobjList.get(0);
            } else if (ownerDetailsDobjList != null && ownerDetailsDobjList.size() >= 2) {
                ownerBean.setDupRegnList(ownerDetailsDobjList);
                //PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Can't Inward Application due to Duplicate Registration No Found for Current State"));
                PrimeFaces.current().executeScript("PF('varDupRegNo').show()");
                return;
            }

            if (ownerDetail == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number Not Found in Currnet Office"));
                return;
            }

            //nitin kumar  - 21-01-2015 - END
//            String Regn_No = CommonPermitPrintImpl.getRegnNoinVtPermit(getUser_regn_no().toUpperCase());
            pmtEndoImpl = new PermitEndorsementAppImpl();
            passImpl = new PassengerPermitDetailImpl();
            if (!passImpl.getvt_statueRegnno(Regn_No)) {
                ownerImpl = new PermitOwnerDetailImpl();
                //nitin kumar
//                PermitOwnerDetailDobj dobj = ownerImpl.set_Owner_appl_db_to_dobj(Regn_No);
                PermitOwnerDetailDobj dobj = ownerBean.getOwnerDetailsDobj(ownerDetail);
                //nitin kumar
                pmtCheckDtsl.getAlldetails(Regn_No, insDobj, dobj.getState_cd(), dobj.getOff_cd());
                permit_detail_dobj = PermitEndorsementAppImpl.getPermitdetails(Regn_No.toUpperCase().trim());

                Map<String, Boolean> visibleRestrictionAreaRoute = new PassengerPermitDetailImpl().visibleRestrictionAreaRoute(Util.getUserStateCode(), Integer.parseInt(getSel_paction()), Integer.parseInt(permit_detail_dobj.getPmt_type()), Integer.parseInt(permit_detail_dobj.getPmtCatg()));

                if (dobj != null && permit_detail_dobj != null && dobj.getState_cd().equalsIgnoreCase(Util.getUserStateCode())) {
                    if (permit_detail_dobj.getPmt_type().equalsIgnoreCase(TableConstants.AITP) || permit_detail_dobj.getPmt_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)) {
                        auth_dobj = pmtEndoImpl.getAuthDetails(permit_detail_dobj.getRegnNo(), permit_detail_dobj.getPmt_no());
                        setPrv_route_list(pmtEndoImpl.getOtherDetail(permit_detail_dobj));
                        setPmt_type(permit_detail_dobj.getPmt_type());
                        if (visibleRestrictionAreaRoute != null && permit_detail_dobj.getPmt_type().equalsIgnoreCase(TableConstants.AITP)) {
                            visible_area = visibleRestrictionAreaRoute.get("show_region_dtls");
                            visible_route_dtls = visibleRestrictionAreaRoute.get("show_route_dtls");
                        }
                        if (visible_area) {
                            areaCodeDetail(Util.getUserStateCode(), "", Util.getUserOffCode());
                            prv_route_list.clear();
                            setPrv_route_list(pmtEndoImpl.getRouteAndOtherData(Util.getUserStateCode(), Util.getUserOffCode(), permit_detail_dobj, appl_details.getCurrent_action_cd()));
                            visible_Route_area = visible_area;
                        }
                        List<AITPStateCoveredDobj> stateList = new ArrayList<>();
                        if (!CommonUtils.isNullOrBlank(permit_detail_dobj.getRegion_covered())) {
                            String[] aitpSelState = permit_detail_dobj.getRegion_covered().split(",");
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

                    } else {
                        visible_Route_area = true;
                        visible_route_dtls = true;
                        setPrv_route_list(pmtEndoImpl.getRouteAndOtherData(Util.getUserStateCode(), Util.getUserOffCode(), permit_detail_dobj, appl_details.getCurrent_action_cd()));
                        routeCodeDetail("NULL", TableList.vt_permit_route, Util.getUserStateCode(), Util.getUserOffCode(), null, otherRouteAllow);
                        areaCodeDetail(Util.getUserStateCode(), "", Util.getUserOffCode());
                        VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                        parameters.setPERMIT_TYPE(Integer.parseInt(permit_detail_dobj.getPmt_type()));
                        if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("route_flag_condition"), parameters), "get_PemritDetail")) {
                            setAllowRouteFlag(true);
                        }
                    }
                    ownerBean.setValueinDOBJ(dobj);
                    dobj.setDisable(false);
                    ownerBean.setFlag_Disable(false);
                    setPermitDtlsdobjtobean(permit_detail_dobj);
                    if (otherRouteAllow && Integer.parseInt(permit_detail_dobj.getPmt_type()) == TableConstants.STAGE_CARRIAGE_PERMIT) {
                        showOtherRoute = true;
                    } else {
                        showOtherRoute = false;
                    }

                } else {
                    JSFUtils.setFacesMessage("Invalid No.", "Please enter correct Number", JSFUtils.ERROR);
                    permit_Dtls_bean.permit_ResetValue();
                    ownerBean.setValueReset();
                    return;
                }
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please provide the valid Registration number", "Please provide the valid Registration number !");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
        } catch (VahanException e) {
            but_function = true;
            disableOtherVehicleDtls = false;
            ownerBean.setValueReset();
            owner_dobj.setDisable(true);
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
        } catch (Exception e) {
            but_function = true;
            disableOtherVehicleDtls = false;
            ownerBean.setValueReset();
            owner_dobj.setDisable(true);
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please provide the valid Registration number", "Please provide the valid Registration number !");
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void getPrevRouteAndTripDetails() {
        pmtEndoImpl = new PermitEndorsementAppImpl();
        if (isModifyTrip()) {
            routeCodeDetail(permit_detail_dobj.getApplNo(), TableList.vt_permit_route, Util.getUserStateCode(), Util.getUserOffCode(), null, otherRouteAllow);
            number_OF_TRIPS = pmtEndoImpl.getNoOfTrip(permit_detail_dobj.getApplNo());
            setDisableRoute(true);
            List list = pmtEndoImpl.getVtRouteCode(Util.getUserStateCode(), Util.getUserOffCode(), permit_detail_dobj.getApplNo());
            for (int i = 0; i < list.size(); i++) {
                String route_code = (String) list.get(i);
                String route_flag = pmtEndoImpl.getRouteFlagByCode(Util.getUserStateCode(), Util.getUserOffCode(), route_code);

                this.route_flag = route_flag;
                if (route_flag != null && !route_flag.equalsIgnoreCase("")) {
                    break;
                }
            }
            if (CommonUtils.isNullOrBlank(this.route_flag)) {
                this.route_flag = "A";
            }
            routeCodeDetailByFlag(permit_detail_dobj.getApplNo(), TableList.vt_permit_route, Util.getUserStateCode(), Util.getUserOffCode(), this.route_flag);
            setDisableRouteWithFlag(true);
            setDisableOtherOffRoute(true);
        } else {
            setDisableRoute(false);
            setDisableRouteWithFlag(false);
            setDisableOtherOffRoute(false);
            routeCodeDetail("NULL", TableList.vt_permit_route, Util.getUserStateCode(), Util.getUserOffCode(), null, otherRouteAllow);
            routeCodeDetailByFlag("NULL", TableList.vt_permit_route, Util.getUserStateCode(), Util.getUserOffCode(), this.route_flag);
            route_list = new ArrayList<>();
            number_OF_TRIPS = "";
        }
    }

    public void save_details() {
        FacesMessage message = null;
        InsDobj ins_dobj = null;
        try {
            String app_no = "";
            String errorMsg = "";
            pmtEndoImpl = new PermitEndorsementAppImpl();
            String pendingApplication = ServerUtil.applicationStatusForPermit(permit_detail_dobj.getRegnNo().toUpperCase(), Util.getUserStateCode());
            if (CommonUtils.isNullOrBlank(errorMsg) && CommonUtils.isNullOrBlank(pendingApplication)) {
                PassengerPermitDetailDobj passPermit = set_data_in_dobj(permit_detail_dobj);
                if (pmtCheckDtsl.getDtlsDobj().isInsSaveData()) {
                    ins_dobj = new InsDobj();
                    ins_dobj.setAppl_no(app_no);
                    if (CommonUtils.isNullOrBlank(pmtCheckDtsl.getDtlsDobj().getInsFrom())) {
                        throw new VahanException("Insurance From Date is missing.");
                    }
                    ins_dobj.setIns_from(CommonPermitPrintImpl.getDateDD_MMM_YYYY(pmtCheckDtsl.getDtlsDobj().getInsFrom()));
                    if (CommonUtils.isNullOrBlank(pmtCheckDtsl.getDtlsDobj().getInsUpto())) {
                        throw new VahanException("Insurance Upto Date is missing.");
                    }
                    ins_dobj.setIns_upto(CommonPermitPrintImpl.getDateDD_MMM_YYYY(pmtCheckDtsl.getDtlsDobj().getInsUpto()));
                    ins_dobj.setComp_cd(Integer.valueOf(CommonPermitPrintImpl.getValueForSelectedList((ArrayList) arrayInsCmpy, pmtCheckDtsl.getDtlsDobj().getInsCmpyNo())));
                    ins_dobj.setIns_type(Integer.valueOf(CommonPermitPrintImpl.getValueForSelectedList((ArrayList) arrayInsType, pmtCheckDtsl.getDtlsDobj().getInsType())));
                    ins_dobj.setPolicy_no(pmtCheckDtsl.getDtlsDobj().getInsPolicyNo());
                    ins_dobj.setIdv(Long.parseLong(pmtCheckDtsl.getDtlsDobj().getInsIdv()));
                }
                List routeByFlag = routeManageByFlag.getTarget();
                List route = routeManage.getTarget();
                route.addAll(routeByFlag);
                if (passPermit != null && (!CommonUtils.isNullOrBlank(passPermit.getPmt_type_code())) && passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.AITP) && statecoveredBean.getStateList() != null && statecoveredBean.getStateList().size() > 0
                        && areaManage != null && areaManage.getTarget() != null && areaManage.getTarget().size() > 0) {
                    throw new VahanException("Please select either area or states.");
                }
                if (passPermit == null || CommonUtils.isNullOrBlank(passPermit.getPaction_code())) {
                    throw new VahanException("Purpose details are not found");
                }
                Map<String, Boolean> routeRegionRestriction = new PassengerPermitDetailImpl().multiRouteRegionRestriction(Util.getUserStateCode(), Integer.parseInt(passPermit.getPaction_code()));
                if (routeRegionRestriction != null) {
                    if (!routeRegionRestriction.get("multi_region_allowed") && (areaManage.getTarget() != null && areaManage.getTarget().size() > 1)) {
                        throw new VahanException("Multiple region/area not allowed. Please select only one area.");
                    }
                }
                if (!CommonUtils.isNullOrBlank(passPermit.getPmt_type_code()) && (passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.AITP) || passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT))) {
                    if (auth_dobj == null || CommonUtils.isNullOrBlank(auth_dobj.getAuthNo())) {
                        throw new VahanException("Authorization details not found.");
                    }
                }

                boolean tripExtended = true;
                if (!CommonUtils.isNullOrBlank(passPermit.getPmt_type_code()) && (Integer.parseInt(passPermit.getPmt_type_code()) == TableConstants.STAGE_CARRIAGE_PERMIT)) {
                    String prev_number_OF_TRIPS = pmtEndoImpl.getNoOfTrip(permit_detail_dobj.getApplNo());
                    if (!(CommonUtils.isNullOrBlank(prev_number_OF_TRIPS) || CommonUtils.isNullOrBlank(number_OF_TRIPS)) && isModifyTrip()) {
                        if (Integer.parseInt(number_OF_TRIPS) <= Integer.parseInt(prev_number_OF_TRIPS)) {
                            tripExtended = false;
                        }
                    }
                }

                app_no = pmtEndoImpl.get_ApplicationNumber(passPermit, routeManage.getTarget(), ins_dobj, selectedRouteList, tripExtended);
                printDialogBox(app_no);
                setApplNoForAck(app_no);
                setPmt_type_code(passPermit.getPmt_type_code());
                setPaction_code(passPermit.getPaction_code());
            } else if (!CommonUtils.isNullOrBlank(pendingApplication)) {
                showApplPendingDialogBox(pendingApplication);
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", errorMsg);
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            }
        } catch (VahanException e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", e.getMessage());
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "regn_no " + permit_detail_dobj.getRegnNo().toUpperCase() + e.getStackTrace()[0]);
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "There is some problem while saving data");
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
        }
    }

    public void showApplPendingDialogBox(String pendingApplication) {
        setApp_no_msg(pendingApplication);
        PrimeFaces.current().ajax().update("app_num_id");
        PrimeFaces.current().executeScript("PF('appNum').show();");
    }

    public void bt_fill_report_for_verify() {
        prv_route_list.clear();
        disableOtherVehicleDtls = true;
        InsDobj insDobj = null;
        PassengerPermitDetailDobj permit_dobj = null;
        try {
            owner_dobj = new PermitOwnerDetailDobj();
            ownerImpl = new PermitOwnerDetailImpl();
            permit_dobj = new PassengerPermitDetailDobj();
            pmtEndoImpl = new PermitEndorsementAppImpl();
            passImpl = new PassengerPermitDetailImpl();
            owner_dobj = ownerImpl.set_VA_Owner_permit_to_dobj(appl_details.getAppl_no(), appl_details.getRegn_no());
            pmtCheckDtsl.getAlldetails(appl_details.getRegn_no(), insDobj, owner_dobj.getState_cd(), owner_dobj.getOff_cd());
            ownerBean.setValueinDOBJ(owner_dobj);
            owner_dobj.setDisable(true);
            permit_dobj = passImpl.set_permit_appl_db_to_dobj(appl_details.getAppl_no());
            permit_detail_dobj_prv = (PassengerPermitDetailDobj) permit_dobj.clone();
            permit_detail_dobj = pmtEndoImpl.set_prv_permit_appl_to_dobj(appl_details.getRegn_no());
            setPermitDtlsdobjtobean(permit_detail_dobj);
            set_Passenger_Permit_Detail_dobj_to_bean(permit_dobj);
            Map<String, Boolean> visibleRestrictionAreaRoute = new PassengerPermitDetailImpl().visibleRestrictionAreaRoute(Util.getUserStateCode(), Integer.parseInt(getSel_paction()), Integer.parseInt(permit_detail_dobj.getPmt_type()), Integer.parseInt(permit_detail_dobj.getPmtCatg()));

            if (permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)
                    || permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.AITP)) {
                visible_Route_area = false;
                visible_route_dtls = false;

                if (visibleRestrictionAreaRoute != null && permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.AITP)) {
                    visible_area = visibleRestrictionAreaRoute.get("show_region_dtls");
                    visible_route_dtls = visibleRestrictionAreaRoute.get("show_route_dtls");
                    //madhurendra 17-5-19
                    visible_Route_area = visible_area;
                }
                getRouteManage().getSource().clear();
                getRouteManage().getTarget().clear();
                getAreaManage().getSource().clear();
                getAreaManage().getTarget().clear();
                setPrv_route_list(pmtEndoImpl.getOtherDetail(permit_detail_dobj));
                if (visible_area) {
                    String str = permit_dobj.getRegion_covered();
                    String[] temp = str.split(",");
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < temp.length; i++) {
                        stringBuilder.append("(").append("'" + temp[i] + "'").append("),");
                    }
                    if (JSFUtils.isNumeric(temp[0])) {
                        areaCodeDetail(Util.getUserStateCode(), stringBuilder.substring(0, stringBuilder.length() - 1), Util.getUserOffCode());
                    } else {
                        areaCodeDetail(Util.getUserStateCode(), "", Util.getUserOffCode());
                    }
                }
                setPmt_type(permit_dobj.getPmt_type());
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
                }
            } else {
                visible_Route_area = true;
                visible_route_dtls = true;
                VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                parameters.setPERMIT_TYPE(Integer.parseInt(permit_dobj.getPmt_type()));
                if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("route_flag_condition"), parameters), "get_PemritDetail")) {
                    setAllowRouteFlag(true);
                }
                setPrv_route_list(pmtEndoImpl.getRouteAndOtherData(Util.getUserStateCode(), Util.getUserOffCode(), permit_detail_dobj, appl_details.getCurrent_action_cd()));
                routeCodeDetail(appl_details.getAppl_no(), TableList.va_permit_route, Util.getUserStateCode(), Util.getUserOffCode(), null, otherRouteAllow);
                List list = pmtEndoImpl.getRouteCode(Util.getUserStateCode(), Util.getUserOffCode(), appl_details.getAppl_no());
                for (int i = 0; i < list.size(); i++) {
                    String route_code = (String) list.get(i);
                    String route_flag = pmtEndoImpl.getRouteFlagByCode(Util.getUserStateCode(), Util.getUserOffCode(), route_code);

                    this.route_flag = route_flag;
                    if (route_flag != null && !route_flag.equalsIgnoreCase("")) {
                        break;
                    }
                }
                if (CommonUtils.isNullOrBlank(this.route_flag)) {
                    this.route_flag = "A";
                }
                routeCodeDetailByFlag(appl_details.getAppl_no(), TableList.va_permit_route, Util.getUserStateCode(), Util.getUserOffCode(), this.route_flag);
                String str = permit_dobj.getRegion_covered();
                String[] temp = str.split(",");
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < temp.length; i++) {
                    stringBuilder.append("(").append("'" + temp[i] + "'").append("),");
                }
                areaCodeDetail(Util.getUserStateCode(), stringBuilder.substring(0, stringBuilder.length() - 1), Util.getUserOffCode());
            }
            but_function = false;

            if (otherRouteAllow && Integer.parseInt(permit_dobj.getPmt_type()) == TableConstants.STAGE_CARRIAGE_PERMIT) {
                showOtherRoute = true;
            } else {
                showOtherRoute = false;
            }

            if (!CommonUtils.isNullOrBlank(permit_dobj.getPmt_type()) && (Integer.parseInt(permit_dobj.getPmt_type()) == TableConstants.STAGE_CARRIAGE_PERMIT)) {
                String prev_route_code = getPrv_route_list().get(0).getRout_code();
                if (!CommonUtils.isNullOrBlank(prev_route_code)) {
                    prev_route_code = prev_route_code.substring(0, prev_route_code.length() - 1);
                    List new_route_list = routeManage.getTarget();
                    PermitRouteList new_route_code = (PermitRouteList) new_route_list.get(0);
                    if ((prev_route_code.equalsIgnoreCase(new_route_code.getKey()))) {
                        setDisableRouteWithFlag(true);
                        setDisableRoute(true);
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public PassengerPermitDetailDobj set_data_in_dobj(PassengerPermitDetailDobj dobj) {
        FacesMessage message = null;
        PassengerPermitDetailDobj passPermit = null;
        try {
            passPermit = new PassengerPermitDetailDobj();
            passPermit.setRegnNo(getUser_regn_no().toUpperCase());
            passPermit.setRemarks(dobj.getRemarks());
            passPermit.setReplaceDate(dobj.getReplaceDate());
            passPermit.setState_cd(Util.getUserStateCode());
            passPermit.setPeriod_mode("N");
            passPermit.setApplNo(dobj.getApplNo());
            passPermit.setOff_cd(Integer.toString(Util.getUserOffCode()));
            if (getGoods_TO_CARRY() == null) {
                passPermit.setGoods_TO_CARRY("");
            } else {
                passPermit.setGoods_TO_CARRY(((String) getGoods_TO_CARRY()).toUpperCase());
            }

            if (getJoreny_PURPOSE() == null) {
                passPermit.setJoreny_PURPOSE("");
            } else {
                passPermit.setJoreny_PURPOSE(((String) getJoreny_PURPOSE()).toUpperCase());
            }

            if (CommonUtils.isNullOrBlank(number_OF_TRIPS)) {
                passPermit.setNumberOfTrips("0");
            } else {
                passPermit.setNumberOfTrips(((String) getNumber_OF_TRIPS()).toUpperCase());
            }

            if (getParking() == null) {
                passPermit.setParking("");
            } else {
                passPermit.setParking(((String) getParking()).toUpperCase());
            }

            passPermit.setPaction_code((String) getSel_paction());
            passPermit.setPeriod("0");
            passPermit.setPmt_type_code(((String) getSel_pmt_mast()).toUpperCase());
            passPermit.setPmtCatg(((String) getPmtcatg()).toUpperCase());
            if (getServices_type() == null) {
                passPermit.setServices_TYPE("-1");
            } else {
                passPermit.setServices_TYPE(((String) getServices_type()).toUpperCase());
            }
            passPermit.setRegnNo(((String) getUser_regn_no()).toUpperCase());
            if (!(((String) getSel_pmt_mast()).equalsIgnoreCase(TableConstants.AITP)
                    || ((String) getSel_pmt_mast()).equalsIgnoreCase(TableConstants.NATIONAL_PERMIT))) {
                String region = "";
                List area = areaManage.getTarget();
                List route = routeManage.getTarget();
                if (area.isEmpty() && route.isEmpty()) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Please select any Route/Area Details.");
                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                    return null;
                }
                for (Object s : area) {
                    region = region + s + ",";
                }
                passPermit.setRegion_covered(region);
            }
            if ((getSel_pmt_mast().equalsIgnoreCase(TableConstants.AITP)) || (getSel_pmt_mast().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT))) {
                if (CommonUtils.isNullOrBlank(passPermit.getRegion_covered()) && statecoveredBean.getStateList() != null && statecoveredBean.getStateList().size() > 0) {
                    passPermit.setRegion_covered(statecoveredBean.getStateList().toString().subSequence(1, statecoveredBean.getStateList().toString().length() - 1) + ",");
                } else if (areaManage.getTarget() != null) {
                    List area = areaManage.getTarget();
                    String region = "";
                    for (Object s : area) {
                        region = region + s + ",";
                    }
                    passPermit.setRegion_covered(region);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return passPermit;
    }

    public void set_Passenger_Permit_Detail_dobj_to_bean(PassengerPermitDetailDobj permit_dobj) {
        //  purCd.clear();
        regn_no = permit_dobj.getRegnNo();
        domain = permit_dobj.getDomain_CODE();
        services_type = permit_dobj.getServices_TYPE();
        goods_TO_CARRY = permit_dobj.getGoods_TO_CARRY();
        joreny_PURPOSE = permit_dobj.getJoreny_PURPOSE();
        parking = permit_dobj.getParking();
        sel_paction = permit_dobj.getPaction();
        number_OF_TRIPS = permit_dobj.getNumberOfTrips();

    }

    public void printDialogBox(String app_no) {
        if (("").equalsIgnoreCase(app_no)) {
            setApp_no_msg("Application Number is not genrated");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        } else {
            setApp_no_msg("Application Number generated :" + app_no + ". Please note down the Application No for future reference.");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        }
    }

    public void routeCodeDetail(String Appl_no, String TableName, String state_cd, int off_cd, String route_flag, boolean otherOffRouteAllow) {
        try {
            boolean flage = false;
            noOfTripsrendered = false;
            pmtEndoImpl = new PermitEndorsementAppImpl();
            actionSource.clear();
            actionTarget.clear();
            Map<String, String> routeMap = pmtEndoImpl.getSourcesRouteMap(Appl_no, TableName, state_cd, off_cd, route_flag);
            for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                actionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
            Map<String, String> mapRouteList = pmtEndoImpl.getTargetRouteMap(Appl_no, TableName, state_cd, off_cd, otherRouteAllow);
            for (Map.Entry<String, String> entry : mapRouteList.entrySet()) {
                flage = true;
                actionTarget.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
            setVia_route("");
            if (flage) {
                setVia_route(pmtEndoImpl.getRouteViaRouteList(Appl_no, state_cd, TableName, off_cd, otherRouteAllow));
                noOfTripsrendered = true;
            }
            routeManage = new DualListModel<PermitRouteList>(actionSource, actionTarget);
            passImp = new PassengerPermitDetailImpl();
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
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
        }
    }

    public void areaCodeDetail(String state_cd, String stringBuilder, int off_cd) {
        pmtEndoImpl = new PermitEndorsementAppImpl();
        areaActionSource.clear();
        areaActionTarget.clear();
        if (CommonUtils.isNullOrBlank(stringBuilder) || stringBuilder.isEmpty() || ("('')").equalsIgnoreCase(stringBuilder)) {
            Map<String, String> routeMap = pmtEndoImpl.getTargetAreaMap(state_cd, off_cd);
            for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                areaActionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
        } else {
            String tempStr = stringBuilder;
            tempStr = tempStr.replace("('", "").replace("')", "");
            String[] temp = tempStr.split(",");
            if (JSFUtils.isNumeric(temp[0])) {
                Map<String, String> routeMap = pmtEndoImpl.getSourcesAreaMapWithStrBuil(stringBuilder, state_cd, off_cd);
                for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                    areaActionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
                }
                Map<String, String> mapRouteList = pmtEndoImpl.getTargetAreaMapWithStrBuil(stringBuilder, state_cd, off_cd);
                for (Map.Entry<String, String> entry : mapRouteList.entrySet()) {
                    areaActionTarget.add(new PermitRouteList(entry.getKey(), entry.getValue()));
                }
            }
        }
        areaManage = new DualListModel<PermitRouteList>(areaActionSource, areaActionTarget);
    }

    public void setPermitDtlsdobjtobean(PassengerPermitDetailDobj dobj) {
        //setIssue_dt(dobj.getIssue_date());
        setPmt_no(dobj.getPmt_no());
        setValid_from(dobj.getValid_from());
        setValid_upto(dobj.getValid_upto());
        pmtCategory.clear();
        String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equalsIgnoreCase(Util.getUserStateCode())
                    && Integer.parseInt(data[i][3]) == Integer.parseInt(dobj.getPmt_type())
                    && Integer.parseInt(data[i][1]) == Integer.parseInt(dobj.getPmtCatg())) {
                pmtCategory.add(new SelectItem(data[i][1], data[i][2]));
                break;
            }
        }
        pmt_mast.clear();
        data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
        for (int i = 0; i < data.length; i++) {
            if (Integer.parseInt(data[i][0]) == Integer.parseInt(dobj.getPmt_type())) {
                pmt_mast.add(new SelectItem(data[i][0], data[i][1]));
                break;
            }
        }
        setUser_regn_no(dobj.getRegnNo());
        setRegn_no(dobj.getRegnNo());
        setSel_pmt_mast(dobj.getPmt_type());
        setPmtcatg(dobj.getPmtCatg());
        setServices_type(dobj.getServices_TYPE());
    }

    public void onTransfer() {
        pmtEndoImpl = new PermitEndorsementAppImpl();
        if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_VARIATION_ENDORSEMENT_VERIFICATION
                || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_VARIATION_ENDORSEMENT_APPROVAL) {
            setVia_route(pmtEndoImpl.getRouteVia(routeManage.getTarget(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected()));
        } else {
            setVia_route(pmtEndoImpl.getRouteVia(routeManage.getTarget(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected()));
        }
        if (getVia_route().isEmpty()) {
            noOfTripsrendered = false;
        } else {
            noOfTripsrendered = true;
        }
    }

    @Override
    public String save() {
        try {
            pmtEndoImpl = new PermitEndorsementAppImpl();
            PassengerPermitDetailDobj passPermit = set_data_in_dobj(permit_detail_dobj);
            pmtEndoImpl.stayOnTheSameStage(passPermit, routeManage.getTarget(), appl_details.getAppl_no(), ComparisonBeanImpl.changedDataContents(compareChanges()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "seatwork";
    }

    @Override
    public List<ComparisonBean> compareChanges() {

        try {
            if (permit_detail_dobj_prv == null) {
                return getCompBeanList();
            }
            getCompBeanList().clear();
            Compare("Goods To Carry", permit_detail_dobj_prv.getGoods_TO_CARRY(), getGoods_TO_CARRY().toString().toUpperCase(), (ArrayList) getCompBeanList());
            Compare("Joreny Purpose", permit_detail_dobj_prv.getJoreny_PURPOSE(), getJoreny_PURPOSE().toString().toUpperCase(), (ArrayList) getCompBeanList());
            Compare("Parking", permit_detail_dobj_prv.getParking(), getParking().toString().toUpperCase(), (ArrayList) getCompBeanList());
            Compare("Area Details", areaActionTarget, areaManage.getTarget(), (ArrayList) getCompBeanList());
            List routeByFlag = routeManageByFlag.getTarget();
            List route = routeManage.getTarget();
            route.addAll(routeByFlag);
            Compare("Route Details", actionTargetByFlag, actionTarget, routeManage.getTarget(), (ArrayList) getCompBeanList());
            Compare("Service Type", permit_detail_dobj_prv.getServices_TYPE(), getServices_type().toString().toUpperCase(), (ArrayList) getCompBeanList());
            if (!CommonUtils.isNullOrBlank(permit_detail_dobj_prv.getNumberOfTrips()) && !CommonUtils.isNullOrBlank(getNumber_OF_TRIPS())) {
                Compare("Number of Trip", permit_detail_dobj_prv.getNumberOfTrips(), getNumber_OF_TRIPS().toString(), (ArrayList) getCompBeanList());
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return getCompBeanList();

    }

    @Override
    public String saveAndMoveFile() {
        String action_cd = (String) String.valueOf(appl_details.getCurrent_action_cd());
        String return_location = "";
        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                if ((action_cd == null ? (String.valueOf(TableConstants.TM_ROLE_PMT_VARIATION_ENDORSEMENT_VERIFICATION)) == null : action_cd.equals(String.valueOf(TableConstants.TM_ROLE_PMT_VARIATION_ENDORSEMENT_VERIFICATION)))
                        || (action_cd == null ? (String.valueOf(TableConstants.TM_ROLE_PMT_VARIATION_ENDORSEMENT_APPL)) == null : action_cd.equals(String.valueOf(TableConstants.TM_ROLE_PMT_VARIATION_ENDORSEMENT_APPL)))) {
                    pmtEndoImpl = new PermitEndorsementAppImpl();
                    PassengerPermitDetailDobj passPermit = set_data_in_dobj(permit_detail_dobj);
                    if (passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.AITP) && statecoveredBean.getStateList() != null && statecoveredBean.getStateList().size() > 0
                            && areaManage.getTarget() != null && areaManage.getTarget().size() > 0) {
                        throw new VahanException("Please select either area or states.");
                    }
                    Map<String, Boolean> routeRegionRestriction = new PassengerPermitDetailImpl().multiRouteRegionRestriction(Util.getUserStateCode(), Integer.parseInt(passPermit.getPaction_code()));
                    if (routeRegionRestriction != null) {
                        if (!routeRegionRestriction.get("multi_region_allowed") && (areaManage.getTarget() != null && areaManage.getTarget().size() > 1)) {
                            throw new VahanException("Multiple region/area not allowed. Please select only one area.");
                        }
                    }
                    return_location = pmtEndoImpl.update_in_tables(passPermit, routeManage.getTarget(), appl_details.getAppl_no(), status, ComparisonBeanImpl.changedDataContents(compareChanges()), otherRouteAllow, selectedRouteList);
                    if (status.getStatus().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING) && return_location.equalsIgnoreCase("fail")) {
                        return_location = disapprovalPrint();
                    }
                }
                if ((action_cd == null ? (String.valueOf(TableConstants.TM_ROLE_PMT_VARIATION_ENDORSEMENT_APPROVAL)) == null : action_cd.equals(String.valueOf(TableConstants.TM_ROLE_PMT_VARIATION_ENDORSEMENT_APPROVAL)))) {
                    PassengerPermitDetailDobj passPermit = set_data_in_dobj(permit_detail_dobj);
                    List routeByFlag = routeManageByFlag.getTarget();
                    List route = routeManage.getTarget();
                    route.addAll(routeByFlag);
                    return_location = pmtEndoImpl.endorsementPermitApproval(appl_details.getAppl_no(), routeManage.getTarget(), appl_details.getRegn_no(), status, passPermit, otherRouteAllow, selectedRouteList);
                    if (status.getStatus().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING) && CommonUtils.isNullOrBlank(return_location)) {
                        return_location = disapprovalPrint();
                    }
                }
            }
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                status.setPrev_action_cd_selected(app_disapp_dobj.getPre_action_code());
                reback(status, ComparisonBeanImpl.changedDataContents(compareChanges()));
                return_location = "seatwork";
            }
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage(TableConstants.SomthingWentWrong);
        }
        return return_location;
    }

    public void reback(Status_dobj status, String CompairChange) {
        try {
            String appl_no = appl_details.getAppl_no();
            pmtEndoImpl = new PermitEndorsementAppImpl();
            PassengerPermitDetailDobj passPermit = set_data_in_dobj(permit_detail_dobj);
            owner_dobj = new PermitOwnerDetailDobj();
            owner_dobj = ownerBean.setValueinDOBJ();
            pmtEndoImpl.rebackStatus(owner_dobj, status, CompairChange, passPermit, routeManage.getTarget(), appl_no);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void getRouteCodeDetails() {
        routeCodeDetailByFlag("NULL", TableList.vt_permit_route, Util.getUserStateCode(), Util.getUserOffCode(), this.route_flag);
    }

    public void routeCodeDetailByFlag(String Appl_no, String TableName, String state_cd, int off_cd, String route_flag) {
        boolean flage = false;
        noOfTripsrendered = true;
        pmtEndoImpl = new PermitEndorsementAppImpl();

        actionSourceByFlag.clear();
        actionTargetByFlag.clear();

        Map<String, String> routeMap = pmtEndoImpl.getSourcesRouteMap(Appl_no, TableName, state_cd, off_cd, route_flag);
        for (Map.Entry<String, String> entry : routeMap.entrySet()) {
            actionSourceByFlag.add(new PermitRouteList(entry.getKey(), entry.getValue()));
        }

        Map<String, String> mapRouteList = pmtEndoImpl.getTargetRouteMapByFlag(Appl_no, TableName, state_cd, off_cd);
        for (Map.Entry<String, String> entry : mapRouteList.entrySet()) {
            flage = true;
            actionTargetByFlag.add(new PermitRouteList(entry.getKey(), entry.getValue()));
        }

        setVia_route_flag("");
        if (flage) {
            setVia_route_flag(pmtEndoImpl.getRouteViaRouteListByFlag(Appl_no, state_cd, TableName, off_cd));
            noOfTripsrendered = true;
        }

        routeManageByFlag = new DualListModel<PermitRouteList>(actionSourceByFlag, actionTargetByFlag);
    }

    public void onTransferByFlag() {
        pmtEndoImpl = new PermitEndorsementAppImpl();

        if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_VARIATION_ENDORSEMENT_VERIFICATION
                || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_VARIATION_ENDORSEMENT_APPROVAL) {
            setVia_route_flag(pmtEndoImpl.getRouteVia(routeManageByFlag.getTarget(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected()));
        } else {
            setVia_route_flag(pmtEndoImpl.getRouteVia(routeManageByFlag.getTarget(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected()));
        }
        if (getVia_route_flag().isEmpty()) {
            noOfTripsrendered = false;
        } else {
            noOfTripsrendered = true;
        }
    }

    public String printAck() {
        HttpSession ses = Util.getSession();
        ses.setAttribute("permitPrintDocPermitType", String.valueOf(getPmt_type_code()));
        ses.setAttribute("permitPrintDocApplNo", getApplNoForAck());
        ses.setAttribute("permitPrintDocPurCd", String.valueOf(getPaction_code()));
        ses.setAttribute("permitPrintDocRegnNo", getRegn_no());
        ses.setAttribute("permitPrintDocID", "11");
        ses.setAttribute("OfferRedirect", "");
        return "/ui/permitreports/PermitAcknowledgement.xhtml?faces-redirect=true";

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

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isShowHide() {
        return showHide;
    }

    public void setShowHide(boolean showHide) {
        this.showHide = showHide;
    }

    public boolean iseApplshowHide() {
        return eApplshowHide;
    }

    public void seteApplshowHide(boolean eApplshowHide) {
        this.eApplshowHide = eApplshowHide;
    }

    public PermitOwnerDetailDobj getOwner_dobj() {
        return owner_dobj;
    }

    public void setOwner_dobj(PermitOwnerDetailDobj owner_dobj) {
        this.owner_dobj = owner_dobj;
    }

    public PermitOwnerDetailImpl getOwnerImpl() {
        return ownerImpl;
    }

    public void setOwnerImpl(PermitOwnerDetailImpl ownerImpl) {
        this.ownerImpl = ownerImpl;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPmtcatg() {
        return pmtcatg;
    }

    public void setPmtcatg(String pmtcatg) {
        this.pmtcatg = pmtcatg;
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

    public List getPmt_mast() {
        return pmt_mast;
    }

    public void setPmt_mast(List pmt_mast) {
        this.pmt_mast = pmt_mast;
    }

    public List getPurCd() {
        return purCd;
    }

    public void setPurCd(List purCd) {
        this.purCd = purCd;
    }

    public List getSer_type() {
        return ser_type;
    }

    public void setSer_type(List ser_type) {
        this.ser_type = ser_type;
    }

    public List getPmtCategory() {
        return pmtCategory;
    }

    public void setPmtCategory(List pmtCategory) {
        this.pmtCategory = pmtCategory;
    }

    public List getRoute_mast() {
        return route_mast;
    }

    public void setRoute_mast(List route_mast) {
        this.route_mast = route_mast;
    }

    public List getArea_mast() {
        return area_mast;
    }

    public void setArea_mast(List area_mast) {
        this.area_mast = area_mast;
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

    public List<PermitRouteList> getPrvactionTarget() {
        return prvactionTarget;
    }

    public void setPrvactionTarget(List<PermitRouteList> prvactionTarget) {
        this.prvactionTarget = prvactionTarget;
    }

    public String getUser_regn_no() {
        return user_regn_no;
    }

    public void setUser_regn_no(String user_regn_no) {
        this.user_regn_no = user_regn_no;
    }

    public String getApp_no_msg() {
        return app_no_msg;
    }

    public void setApp_no_msg(String app_no_msg) {
        this.app_no_msg = app_no_msg;
    }

    public String getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(String pmt_type) {
        this.pmt_type = pmt_type;
    }

    public String getFloc() {
        return floc;
    }

    public void setFloc(String floc) {
        this.floc = floc;
    }

    public String getStart_point() {
        return start_point;
    }

    public void setStart_point(String start_point) {
        this.start_point = start_point;
    }

    public String getTloc() {
        return tloc;
    }

    public void setTloc(String tloc) {
        this.tloc = tloc;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public String getAppl_NO() {
        return appl_NO;
    }

    public void setAppl_NO(String appl_NO) {
        this.appl_NO = appl_NO;
    }

    public String getJoreny_PURPOSE() {
        return joreny_PURPOSE;
    }

    public void setJoreny_PURPOSE(String joreny_PURPOSE) {
        this.joreny_PURPOSE = joreny_PURPOSE;
    }

    public String getGoods_TO_CARRY() {
        return goods_TO_CARRY;
    }

    public void setGoods_TO_CARRY(String goods_TO_CARRY) {
        this.goods_TO_CARRY = goods_TO_CARRY;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getNumber_OF_TRIPS() {
        return number_OF_TRIPS;
    }

    public void setNumber_OF_TRIPS(String number_OF_TRIPS) {
        this.number_OF_TRIPS = number_OF_TRIPS;
    }

    public List<String> getRoute_map() {
        return route_map;
    }

    public void setRoute_map(List<String> route_map) {
        this.route_map = route_map;
    }

    public String getSel_paction() {
        return sel_paction;
    }

    public void setSel_paction(String sel_paction) {
        this.sel_paction = sel_paction;
    }

    public String getSel_pmt_mast() {
        return sel_pmt_mast;
    }

    public void setSel_pmt_mast(String sel_pmt_mast) {
        this.sel_pmt_mast = sel_pmt_mast;
    }

    public String getServices_type() {
        return services_type;
    }

    public void setServices_type(String services_type) {
        this.services_type = services_type;
    }

    public boolean isBut_function() {
        return but_function;
    }

    public void setBut_function(boolean but_function) {
        this.but_function = but_function;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public PermitOwnerDetailBean getOwnerBean() {
        return ownerBean;
    }

    public void setOwnerBean(PermitOwnerDetailBean ownerBean) {
        this.ownerBean = ownerBean;
    }

    public PermitDetailBean getPermit_Dtls_bean() {
        return permit_Dtls_bean;
    }

    public void setPermit_Dtls_bean(PermitDetailBean permit_Dtls_bean) {
        this.permit_Dtls_bean = permit_Dtls_bean;
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    public String getMasterLayout() {
        return masterLayout;
    }

    public void setMasterLayout(String masterLayout) {
        this.masterLayout = masterLayout;
    }

    public boolean isVisible_Route_area() {
        return visible_Route_area;
    }

    public void setVisible_Route_area(boolean visible_Route_area) {
        this.visible_Route_area = visible_Route_area;
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

    public boolean isDisableOtherVehicleDtls() {
        return disableOtherVehicleDtls;
    }

    public void setDisableOtherVehicleDtls(boolean disableOtherVehicleDtls) {
        this.disableOtherVehicleDtls = disableOtherVehicleDtls;
    }

    public Date getCurrenDate() {
        return currenDate;
    }

    public void setCurrenDate(Date currenDate) {
        this.currenDate = currenDate;
    }

    public boolean isNoOfTripsrendered() {
        return noOfTripsrendered;
    }

    public void setNoOfTripsrendered(boolean noOfTripsrendered) {
        this.noOfTripsrendered = noOfTripsrendered;
    }

    public boolean isPanel_visible() {
        return panel_visible;
    }

    public void setPanel_visible(boolean panel_visible) {
        this.panel_visible = panel_visible;
    }

    public PermitEndorsementAppImpl getPmtEndoImpl() {
        return pmtEndoImpl;
    }

    public void setPmtEndoImpl(PermitEndorsementAppImpl pmtEndoImpl) {
        this.pmtEndoImpl = pmtEndoImpl;
    }

    public PassengerPermitDetailDobj getPermit_detail_dobj() {
        return permit_detail_dobj;
    }

    public void setPermit_detail_dobj(PassengerPermitDetailDobj permit_detail_dobj) {
        this.permit_detail_dobj = permit_detail_dobj;
    }

    public Date getIssue_dt() {
        return issue_dt;
    }

    public void setIssue_dt(Date issue_dt) {
        this.issue_dt = issue_dt;
    }

    public Date getValid_from() {
        return valid_from;
    }

    public void setValid_from(Date valid_from) {
        this.valid_from = valid_from;
    }

    public Date getValid_upto() {
        return valid_upto;
    }

    public void setValid_upto(Date valid_upto) {
        this.valid_upto = valid_upto;
    }

    public String getPmt_no() {
        return pmt_no;
    }

    public void setPmt_no(String pmt_no) {
        this.pmt_no = pmt_no;
    }

    public PermitCheckDetailsBean getPmtCheckDtsl() {
        return pmtCheckDtsl;
    }

    public void setPmtCheckDtsl(PermitCheckDetailsBean pmtCheckDtsl) {
        this.pmtCheckDtsl = pmtCheckDtsl;
    }

    public List<PassengerPermitDetailDobj> getPrv_route_list() {
        return prv_route_list;
    }

    public void setPrv_route_list(List<PassengerPermitDetailDobj> prv_route_list) {
        this.prv_route_list = prv_route_list;
    }

    public String getRegion_covereds() {
        return region_covereds;
    }

    public void setRegion_covereds(String region_covereds) {
        this.region_covereds = region_covereds;
    }

    public List<PassengerPermitDetailDobj> getNext_route_list() {
        return next_route_list;
    }

    public void setNext_route_list(List<PassengerPermitDetailDobj> next_route_list) {
        this.next_route_list = next_route_list;
    }

    public String getVia_route() {
        return via_route;
    }

    public void setVia_route(String via_route) {
        this.via_route = via_route;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getRoute_flag() {
        return route_flag;
    }

    public void setRoute_flag(String route_flag) {
        this.route_flag = route_flag;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public DualListModel<PermitRouteList> getRouteManageByFlag() {
        return routeManageByFlag;
    }

    public void setRouteManageByFlag(DualListModel<PermitRouteList> routeManageByFlag) {
        this.routeManageByFlag = routeManageByFlag;
    }

    public String getVia_route_flag() {
        return via_route_flag;
    }

    public void setVia_route_flag(String via_route_flag) {
        this.via_route_flag = via_route_flag;
    }

    public List<PermitRouteList> getActionSourceByFlag() {
        return actionSourceByFlag;
    }

    public void setActionSourceByFlag(List<PermitRouteList> actionSourceByFlag) {
        this.actionSourceByFlag = actionSourceByFlag;
    }

    public List<PermitRouteList> getActionTargetByFlag() {
        return actionTargetByFlag;
    }

    public void setActionTargetByFlag(List<PermitRouteList> actionTargetByFlag) {
        this.actionTargetByFlag = actionTargetByFlag;
    }

    public String getApplNoForAck() {
        return applNoForAck;
    }

    public void setApplNoForAck(String applNoForAck) {
        this.applNoForAck = applNoForAck;
    }

    public AITPStateCoveredBean getStatecoveredBean() {
        return statecoveredBean;
    }

    public void setStatecoveredBean(AITPStateCoveredBean statecoveredBean) {
        this.statecoveredBean = statecoveredBean;
    }

    public boolean isVisible_area() {
        return visible_area;
    }

    public void setVisible_area(boolean visible_area) {
        this.visible_area = visible_area;
    }

    public boolean isVisible_route_dtls() {
        return visible_route_dtls;
    }

    public void setVisible_route_dtls(boolean visible_route_dtls) {
        this.visible_route_dtls = visible_route_dtls;
    }

    public PermitHomeAuthDobj getAuth_dobj() {
        return auth_dobj;
    }

    public void setAuth_dobj(PermitHomeAuthDobj auth_dobj) {
        this.auth_dobj = auth_dobj;
    }

    public boolean isRenderRouteFlag() {
        return renderRouteFlag;
    }

    public void setRenderRouteFlag(boolean renderRouteFlag) {
        this.renderRouteFlag = renderRouteFlag;
    }

    public boolean isAllowRouteFlag() {
        return allowRouteFlag;
    }

    public void setAllowRouteFlag(boolean allowRouteFlag) {
        this.allowRouteFlag = allowRouteFlag;
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

    public boolean isOtherRouteAllow() {
        return otherRouteAllow;
    }

    public void setOtherRouteAllow(boolean otherRouteAllow) {
        this.otherRouteAllow = otherRouteAllow;
    }

    public boolean isModifyTrip() {
        return modifyTrip;
    }

    public void setModifyTrip(boolean modifyTrip) {
        this.modifyTrip = modifyTrip;
    }

    public boolean isDisableRoute() {
        return disableRoute;
    }

    public void setDisableRoute(boolean disableRoute) {
        this.disableRoute = disableRoute;
    }

    public boolean isRenderModifyTrips() {
        return renderModifyTrips;
    }

    public void setRenderModifyTrips(boolean renderModifyTrips) {
        this.renderModifyTrips = renderModifyTrips;
    }

    public boolean isDisableRouteWithFlag() {
        return disableRouteWithFlag;
    }

    public void setDisableRouteWithFlag(boolean disableRouteWithFlag) {
        this.disableRouteWithFlag = disableRouteWithFlag;
    }

    public boolean isDisableOtherOffRoute() {
        return disableOtherOffRoute;
    }

    public void setDisableOtherOffRoute(boolean disableOtherOffRoute) {
        this.disableOtherOffRoute = disableOtherOffRoute;
    }

    public String getVahanMessages() {
        return vahanMessages;
    }

    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }
}
