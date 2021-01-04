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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.user_mgmt.dobj.UserAuthorityDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.permit.AITPStateFeeDraftDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.dobj.permit.PermitAdministratorDobj;
import nic.vahan.form.dobj.permit.PermitShowFeeDetailDobj;
import nic.vahan.form.dobj.permit.VmPermitRouteDobj;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.form.impl.permit.PermitAdministratorImpl;
import nic.vahan.form.impl.permit.PermitDetailImpl;
import nic.vahan.form.impl.permit.PrintPermitDocInXhtmlImpl;
import nic.vahan.form.impl.permit.SurrenderPermitImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Naman Jain
 */
@ManagedBean(name = "adminBean")
@ViewScoped
public class PermitAdministratorBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PermitAdministratorBean.class);
    private PermitAdministratorDobj adminDobj;
    private List pmtTypeList = new ArrayList();
    private List pmtCatgList = new ArrayList();
    private List serTypeList = new ArrayList();
    private String userInfo = "";
    private String transportCatg = "";
    private PermitOwnerDetailDobj ownerDobj;
    private PermitOwnerDetailImpl ownerImpl;
    @ManagedProperty(value = "#{pmt_owner_dtls}")
    private PermitOwnerDetailBean ownerBean;
    PermitAdministratorImpl adminImpl;
    private PermitAdministratorDobj prvAdminDobj;
    private List<PermitAdministratorDobj> compairValue = new ArrayList<>();
    private DualListModel<PermitRouteList> areaManage;
    private List<PermitRouteList> areaActionSource = new ArrayList<>();
    private List<PermitRouteList> areaActionTarget = new ArrayList<>();
    private DualListModel<PermitRouteList> routeManage;
    private List<PermitRouteList> routeActionSource = new ArrayList<>();
    private List<PermitRouteList> routeActionTarget = new ArrayList<>();
    private String via_route = "";
    private int no_of_trip = 0;
    int[] permitIssuingAuthCode;
    private boolean noOfTripsrendered = false;
    private List purCdList = new ArrayList();
    private String errorMsg = "";
    UserAuthorityDobj authorityDobj = null;
    private List stateList = new ArrayList();
    private List<String> selectedStates;
    private String selectedStatesNames;
    private boolean renderRouteArea = false;
    private boolean disable_region = false;
    private List regionCoveredListNP = new ArrayList();
    private boolean showRegionCovered = false;
    private int pmtIssueAuthCode = 0;
    private boolean pmtIssueAuthCodeFound = false;
    private Map<String, String> stateConfigMap = null;
    private boolean multiApplAllow = false;
    private boolean multiApplCondition = false;
    private List<AITPStateFeeDraftDobj> paymentList = new ArrayList<>();
    private String header_name;
    private List bank_list = new ArrayList();
    private List instrumentList = new ArrayList();
    private List state_list = new ArrayList();
    private boolean render_payment_table_aitp = false;
    @ManagedProperty(value = "#{Permit_Dtls_bean}")
    private PermitDetailBean permit_Dtls_bean;
    private boolean showNpDetails = false;
    private Date vehicleAgeUpto = DateUtil.parseDate(DateUtil.getCurrentDate());
    private boolean otherRouteAllow = false;
    private String[] selectedOffice;
    private List officeList = new ArrayList();
    private List<VmPermitRouteDobj> selectedRouteList;
    private List<VmPermitRouteDobj> route_list = new ArrayList<>();
    private boolean showOtherRoute = false;

    public PermitAdministratorBean() {
        permitIssuingAuthCode = CommonPermitPrintImpl.getPermitIssuingOffCd(Util.getUserOffCode(), Util.getUserStateCode());
        areaManage = new DualListModel<>(areaActionSource, areaActionTarget);
        routeManage = new DualListModel<>(routeActionSource, routeActionTarget);
        adminDobj = new PermitAdministratorDobj();
        stateList = (List) ((ArrayList) MasterTableFiller.stateList).clone();
        String[][] data = MasterTableFiller.masterTables.vm_service_type.getData();
        authorityDobj = Util.getUserAuthority();
        stateConfigMap = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
        multiApplAllow = Boolean.parseBoolean(stateConfigMap.get("multi_appl_allow"));
        render_payment_table_aitp = Boolean.parseBoolean(stateConfigMap.get("render_payment_table_aitp"));
        otherRouteAllow = Boolean.parseBoolean(stateConfigMap.get("other_office_route_allow"));
        for (int i = 0; i < data.length; i++) {
            serTypeList.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_FRESH_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD))) {
                purCdList.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
        data = MasterTableFiller.masterTables.TM_BANK.getData();
        bank_list.add(new SelectItem("-1", "Select Bank"));
        for (int i = 0; i < data.length; i++) {
            bank_list.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.TM_STATE.getData();
        for (int i = 0; i < data.length; i++) {
            state_list.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.TM_OFFICE.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][13].equalsIgnoreCase(Util.getUserStateCode()) && TableConstants.OTHER_OFFICE_ROUTE_ALLOWED_FOR_PERMIT.contains(data[i][0])) {
                officeList.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
        String[][] instrmentType = MasterTableFiller.masterTables.TM_INSTRUMENTS.getData();
        instrumentList = new ArrayList();
        instrumentList.add(new SelectItem("-1", "Select"));
        for (int i = 0; i < instrmentType.length; i++) {
            if (instrmentType[i][0].equals("D") || instrmentType[i][0].equals("L")) {
                instrumentList.add(new SelectItem(instrmentType[i][0], instrmentType[i][1]));
            }
        }
        Map<String, String> routeMap = new PassengerPermitDetailImpl().getSourcesAreaMapWithStrBuil("0", Util.getUserStateCode(), Util.getUserOffCode());
        regionCoveredListNP.clear();
        regionCoveredListNP.add(new SelectItem("0", "SELECT ANY AREA/REGION"));
        for (Map.Entry<String, String> entry : routeMap.entrySet()) {
            regionCoveredListNP.add(new SelectItem(entry.getKey(), entry.getValue()));
        }

    }

    public void onSelectPermitType() {
        pmtCatgList.clear();
        try {
            String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
            if (authorityDobj != null) {
                if (authorityDobj.getSelectedPermitCatg().size() < 1) {
                    throw new VahanException("You are not authorized to modify.");
                } else if (authorityDobj.getSelectedPermitCatg().size() == 1) {
                    for (Object obj : authorityDobj.getSelectedPermitCatg()) {
                        if (String.valueOf(obj).equalsIgnoreCase("ANY")) {
                            for (int i = 0; i < data.length; i++) {
                                if (data[i][0].equalsIgnoreCase(Util.getUserStateCode())
                                        && data[i][3].equalsIgnoreCase(String.valueOf(adminDobj.getPmt_type()))) {
                                    pmtCatgList.add(new SelectItem(data[i][1], data[i][2]));
                                }
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < data.length; j++) {
                        for (Object obj : authorityDobj.getSelectedPermitCatg()) {
                            if (data[j][0].equalsIgnoreCase(Util.getUserStateCode())
                                    && Integer.parseInt(data[j][3]) == adminDobj.getPmt_type()
                                    && data[j][1].equalsIgnoreCase(String.valueOf(obj))) {
                                pmtCatgList.add(new SelectItem(data[j][1], data[j][2]));
                            }
                        }
                    }
                }

                if (pmtCatgList.size() <= 0) {
                    throw new VahanException("You are not authorized to modify.");
                }
            }
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            JSFUtils.showMessage(TableConstants.SomthingWentWrong);
        }
    }

    public void onSelectState() {
        selectedStatesNames = "";
        for (String state_cd : getSelectedStates()) {
            selectedStatesNames = getSelectedStatesNames() + MasterTableFiller.state.get(state_cd).getStateDescr() + "\n";
        }
    }

    public void pmtGetDetails() {
        FacesMessage message = null;
        String str = "";
        try {
            ownerImpl = new PermitOwnerDetailImpl();
            ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj(userInfo.toUpperCase(), null);
            String[] pmtNoType = CommonPermitPrintImpl.getPmtNoAndPmtTypeThroughVtPermit(userInfo.toUpperCase());
            String pmt_no = pmtNoType[0];
            String pmt_type = pmtNoType[1];
            int pmt_off_cd = CommonPermitPrintImpl.getPmtOffCdThroughVtPermit(pmt_no);
            boolean pmtSurr = SurrenderPermitImpl.getvhPermit_statueRegnno(userInfo.toUpperCase(), pmt_no);
            if (ownerDobj == null) {
                throw new VahanException("Owner Details missing. Please contact to system administrator.");
            } else if (pmt_off_cd == 0) {
                throw new VahanException("Permit Office Details not Found. Please contact to system administrator.");
            }

            VehicleParameters vchparameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            vchparameters.setPERMIT_TYPE(Integer.parseInt(pmt_type));
            if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("multi_appl_condition"), vchparameters), "save_details")) {
                multiApplCondition = true;
            }

            for (int i = 0; i < permitIssuingAuthCode.length; i++) {
                if (permitIssuingAuthCode[i] == pmt_off_cd) {
                    pmtIssueAuthCode = permitIssuingAuthCode[i];
                    pmtIssueAuthCodeFound = true;
                    break;
                }
            }

            Map<String, String> OffAllotResult = PermitDetailImpl.getOffAllotmentResult(Util.getUserStateCode(), Util.getUserOffCode(), ownerDobj.getOff_cd());
            if ((ownerDobj != null
                    && "true".equalsIgnoreCase(OffAllotResult.get("offAllowed"))
                    && !pmtSurr && pmtIssueAuthCodeFound) || (multiApplAllow && multiApplCondition && pmtIssueAuthCodeFound && Util.getUserOffCode() == pmt_off_cd)) {
                fillCombo();
                prvAdminDobj = new PermitAdministratorDobj();
                adminImpl = new PermitAdministratorImpl();
                ownerBean.setValueinDOBJ(ownerDobj);
                if (!CommonUtils.isNullOrBlank(pmt_no)) {
                    adminDobj = adminImpl.getPermitDtlsInDobj(pmt_no);
                    Owner_dobj ownDobj = new Owner_dobj();
                    ownDobj.setState_cd(ownerDobj.getState_cd());
                    ownDobj.setFuel(ownerDobj.getFuelCd());
                    ownDobj.setRegn_dt(ownerDobj.getRegnDt());
                    ownDobj.setPmt_type(adminDobj.getPmt_type());
                    ownDobj.setPmt_catg(adminDobj.getPmt_catg());
                    int vehAge = new FitnessImpl().getVehAgeValidity(ownDobj);
                    if (vehAge != 99) {
                        vehicleAgeUpto = ServerUtil.dateRange(ownDobj.getRegn_dt(), vehAge, 0, -1);
                    } else {
                        vehicleAgeUpto = ServerUtil.dateRange(ownDobj.getRegn_dt(), 30, 0, -1);
                    }
                    if (vehicleAgeUpto != null && adminDobj != null && adminDobj.getValid_upto() != null && vehicleAgeUpto.before(adminDobj.getValid_upto())) {
                        this.vehicleAgeUpto = ServerUtil.dateRange(adminDobj.getValid_upto(), 0, 0, 0);
                    }

                    setRenderRouteArea(adminDobj.getPmt_type() == Integer.parseInt(TableConstants.AITP));
                    setShowRegionCovered(adminDobj.getPmt_type() == Integer.parseInt(TableConstants.AITP));
                    if (adminDobj.getPmt_catg() != 0) {
                        onSelectPermitType();
                    }
                    if (adminDobj.getPmt_type() == Integer.parseInt(TableConstants.NATIONAL_PERMIT)) {
                        permit_Dtls_bean.setNationalPermitAuthDetails(adminDobj.getRegn_no());
                        if (!CommonUtils.isNullOrBlank(permit_Dtls_bean.getNp_auth_no())) {
                            setShowNpDetails(true);
                        }
                    }

                    prvAdminDobj = (PermitAdministratorDobj) adminDobj.clone();
                    str = adminDobj.getRegion_covered();
                    if (isRender_payment_table_aitp() && isRenderRouteArea()) {
                        PrintPermitDocInXhtmlImpl impl = new PrintPermitDocInXhtmlImpl();
                        List<AITPStateFeeDraftDobj> draftDetails = impl.getAitpPaymentData(adminDobj.getRegn_no());
                        if (draftDetails != null && draftDetails.size() > 0) {
                            setPaymentList(draftDetails);
                        }
                    }
                    if (!CommonUtils.isNullOrBlank(str)) {
                        String[] temp = str.split(",");
                        if (JSFUtils.isAlphabet(temp[0])) {
                            selectedStatesNames = "";
                            if (selectedStates == null) {
                                selectedStates = new ArrayList<>();
                            }
                            for (int j = 0; j < temp.length; j++) {
                                selectedStates.add(temp[j]);
                                selectedStatesNames = getSelectedStatesNames() + MasterTableFiller.state.get(temp[j]).getStateDescr() + "\n";
                            }
                        }
                    }
                }
                if (otherRouteAllow && adminDobj.getPmt_type() == TableConstants.STAGE_CARRIAGE_PERMIT) {
                    showOtherRoute = true;
                } else {
                    showOtherRoute = false;
                }
                if (CommonUtils.isNullOrBlank(str)) {
                    areaDetailfiller(Util.getUserStateCode(), str, pmtIssueAuthCode);
                    routeCodeDetail(prvAdminDobj.getAppl_no(), TableList.vt_permit_route, Util.getUserStateCode(), pmtIssueAuthCode, showOtherRoute);
                } else {
                    String[] temp = str.split(",");
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int j = 0; j < temp.length; j++) {
                        stringBuilder.append("(").append("'").append(temp[j]).append("'").append("),");
                    }
                    areaDetailfiller(Util.getUserStateCode(), stringBuilder.substring(0, stringBuilder.length() - 1), pmtIssueAuthCode);
                    routeCodeDetail(prvAdminDobj.getAppl_no(), TableList.vt_permit_route, Util.getUserStateCode(), pmtIssueAuthCode, showOtherRoute);
                }
                if (showOtherRoute && selectedRouteList != null && selectedRouteList.size() > 0) {
                    prvAdminDobj.setOtherOfficeRouteList(selectedRouteList);
                }

            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Vehicle/Permit Not Found");
                if (pmtSurr) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Permit is already surrender");
                }
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            }
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Regn_no:- " + userInfo.toUpperCase() + " / " + e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage(TableConstants.SomthingWentWrong);
        }
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

    public void fillCombo() throws VahanException {
        transportCatg = "";
        pmtTypeList.clear();
        String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        for (int i = 0; i < data.length; i++) {
            if (ownerDobj.getVh_class() == Integer.valueOf(data[i][0])) {
                transportCatg = data[i][3];
                break;
            }
        }
        if (CommonUtils.isNullOrBlank(transportCatg)) {
            throw new VahanException("This Vehicle is not Transpost Vehicle");
        } else if (authorityDobj == null) {
            throw new VahanException("User not authorized to any modify");
        } else {
            pmtTypeList.clear();
            data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
            if (authorityDobj.getPermitType().size() == 1) {
                for (Object obj : authorityDobj.getPermitType()) {
                    if (String.valueOf(obj).equalsIgnoreCase("ANY")) {
                        for (int i = 0; i < data.length; i++) {
                            if (data[i][2].equalsIgnoreCase(transportCatg)) {
                                pmtTypeList.add(new SelectItem(data[i][0], data[i][1]));
                            }
                        }
                    } else {
                        for (int i = 0; i < data.length; i++) {
                            if (data[i][2].equalsIgnoreCase(transportCatg) && data[i][0].equalsIgnoreCase(String.valueOf(obj))) {
                                pmtTypeList.add(new SelectItem(data[i][0], data[i][1]));
                                break;
                            }
                        }
                    }
                }
            } else {
                for (int i = 0; i < data.length; i++) {
                    for (Object obj : authorityDobj.getPermitType()) {
                        if (data[i][2].equalsIgnoreCase(transportCatg) && data[i][0].equalsIgnoreCase(String.valueOf(obj))) {
                            pmtTypeList.add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                }
            }
            if (pmtTypeList.size() <= 0) {
                throw new VahanException("You are not authorized to modify.");
            }
        }
    }

    public void checkRegnNo() {
        FacesMessage message = null;
        adminImpl = new PermitAdministratorImpl();
        if (CommonUtils.isNullOrBlank(prvAdminDobj.getRegn_no())) {
            prvAdminDobj.setRegn_no("");
        }
        if (!prvAdminDobj.getRegn_no().equalsIgnoreCase(adminDobj.getRegn_no())) {
            if (adminImpl.checkRegn_Pmt_Appl_NoValidOrNot(adminDobj.getRegn_no(), "", "")) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "This Vehicle No. already exist in Table");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            }
        }
    }

    public void checkPmtNo() {
        FacesMessage message = null;
        adminImpl = new PermitAdministratorImpl();
        if (CommonUtils.isNullOrBlank(prvAdminDobj.getPmt_no())) {
            prvAdminDobj.setPmt_no("");
        }
        if (!prvAdminDobj.getPmt_no().equalsIgnoreCase(adminDobj.getPmt_no())) {
            if (adminImpl.checkRegn_Pmt_Appl_NoValidOrNot("", adminDobj.getPmt_no(), "")) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "This Permit No. already exist in Table");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            }
        }
    }

    public void checkApplNo() {
        FacesMessage message = null;
        adminImpl = new PermitAdministratorImpl();
        if (CommonUtils.isNullOrBlank(prvAdminDobj.getAppl_no())) {
            prvAdminDobj.setAppl_no("");
        }
        if (!prvAdminDobj.getAppl_no().equalsIgnoreCase(adminDobj.getAppl_no())) {
            if (adminImpl.checkRegn_Pmt_Appl_NoValidOrNot("", "", adminDobj.getAppl_no())) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "This Application No. already exist in Table");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            }
        }
    }

    public void areaDetailfiller(String state_cd, String stringBuilder, int off_cd) {
        PassengerPermitDetailImpl passImp = new PassengerPermitDetailImpl();
        areaActionSource.clear();
        areaActionTarget.clear();
        if (CommonUtils.isNullOrBlank(stringBuilder) || stringBuilder.isEmpty() || ("('')").equalsIgnoreCase(stringBuilder)) {
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

    public void routeCodeDetail(String Appl_no, String TableName, String state_cd, int off_cd, boolean otherOfficeRoute) {
        try {
            boolean flage = false;
            noOfTripsrendered = false;
            PassengerPermitDetailImpl passImp = new PassengerPermitDetailImpl();
            routeActionSource.clear();
            routeActionTarget.clear();
            Map<String, String> routeMap = passImp.getSourcesRouteMap(Appl_no, TableName, state_cd, off_cd, otherOfficeRoute);
            for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                routeActionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
            Map<String, String> mapRouteList = passImp.getTargetRouteMap(Appl_no, TableName, state_cd, off_cd, otherOfficeRoute);
            for (Map.Entry<String, String> entry : mapRouteList.entrySet()) {
                flage = true;
                routeActionTarget.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
            setVia_route("");
            if (flage) {
                setVia_route(passImp.getRouteViaRouteList(Appl_no, state_cd, TableName, off_cd, otherOfficeRoute));
                setNo_of_trip(no_of_trip);
                noOfTripsrendered = true;
                setNoOfTripsrendered(noOfTripsrendered);
            }
            routeManage = new DualListModel<PermitRouteList>(routeActionSource, routeActionTarget);
            if (otherOfficeRoute) {
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

    public void onTransfer() {
        PassengerPermitDetailImpl passImp = new PassengerPermitDetailImpl();
        setVia_route(passImp.getRouteVia(routeManage.getTarget(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd()));
        if (getVia_route().isEmpty()) {
            noOfTripsrendered = false;
        } else {
            noOfTripsrendered = true;
        }
    }

    public void compairList() {
        String msg = checkDetails();
        String oldValue = "";
        String newValue = "";
        if (!CommonUtils.isNullOrBlank(msg)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", msg);
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            return;
        }
        compairValue.clear();
        if (CommonUtils.isNullOrBlank(prvAdminDobj.getRegn_no())) {
            prvAdminDobj.setRegn_no("");
        }
        compair("Vehicle No", prvAdminDobj.getRegn_no(), adminDobj.getRegn_no().toUpperCase(), compairValue);
        if (CommonUtils.isNullOrBlank(prvAdminDobj.getPmt_no())) {
            prvAdminDobj.setPmt_no("");
        }
        compair("Permit No", prvAdminDobj.getPmt_no(), adminDobj.getPmt_no().toUpperCase(), compairValue);
        if (CommonUtils.isNullOrBlank(prvAdminDobj.getRcpt_no())) {
            prvAdminDobj.setRcpt_no("");
        }
        compair("Receipt No", prvAdminDobj.getRcpt_no(), adminDobj.getRcpt_no().toUpperCase(), compairValue);
        if (CommonUtils.isNullOrBlank(prvAdminDobj.getAppl_no())) {
            prvAdminDobj.setAppl_no("");
        }
        compair("Application No.", prvAdminDobj.getAppl_no(), adminDobj.getAppl_no().toUpperCase(), compairValue);

        if (prvAdminDobj.getValid_from() == null) {
            compair("Valid From", "", CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(adminDobj.getValid_from()).toUpperCase(), compairValue);
        } else {
            compair("Valid From", CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(prvAdminDobj.getValid_from()), CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(adminDobj.getValid_from()).toUpperCase(), compairValue);
        }

        if (prvAdminDobj.getValid_upto() == null) {
            compair("Valid Upto", "", CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(adminDobj.getValid_upto()).toUpperCase(), compairValue);
        } else {
            compair("Valid Upto", CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(prvAdminDobj.getValid_upto()), CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(adminDobj.getValid_upto()).toUpperCase(), compairValue);
        }

        if (prvAdminDobj.getIssue_dt() == null) {
            compair("Permit Issue Date", "", CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(adminDobj.getIssue_dt()).toUpperCase(), compairValue);
        } else {
            compair("Permit Issue Date", CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(prvAdminDobj.getIssue_dt()), CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(adminDobj.getIssue_dt()).toUpperCase(), compairValue);
        }

        if (prvAdminDobj.getReplace_date() == null) {
            if (adminDobj.getReplace_date() == null) {
                compair("Permit Replacement Date", "", "", compairValue);
            } else {
                compair("Permit Replacement Date", "", CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(adminDobj.getReplace_date()).toUpperCase(), compairValue);
            }
        } else {
            compair("Permit Replacement Date", CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(prvAdminDobj.getReplace_date()), CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(adminDobj.getReplace_date()).toUpperCase(), compairValue);
        }

        if (CommonUtils.isNullOrBlank(prvAdminDobj.getParking())) {
            prvAdminDobj.setParking("");
        }
        compair("Parking Place", prvAdminDobj.getParking(), adminDobj.getParking().toUpperCase(), compairValue);
        if (CommonUtils.isNullOrBlank(prvAdminDobj.getJorney_purpose())) {
            prvAdminDobj.setJorney_purpose("");
        }
        compair("Purpose of Journey", prvAdminDobj.getJorney_purpose(), adminDobj.getJorney_purpose().toUpperCase(), compairValue);
        if (CommonUtils.isNullOrBlank(prvAdminDobj.getGoods_to_carry())) {
            prvAdminDobj.setGoods_to_carry("");
        }
        compair("Goods to Carry", prvAdminDobj.getGoods_to_carry(), adminDobj.getGoods_to_carry().toUpperCase(), compairValue);
        compair("Permit Category", String.valueOf(prvAdminDobj.getPmt_catg()), String.valueOf(adminDobj.getPmt_catg()), compairValue);
        for (PermitRouteList s : areaActionTarget) {
            oldValue = oldValue + s.getKey() + ",";
        }
        List area = renderRouteArea ? selectedStates : areaManage.getTarget();
        for (Object s : area) {
            newValue = newValue + (String) s + ",";
        }
        compair("Area Details", oldValue, newValue, compairValue);
        oldValue = "";
        newValue = "";
        for (PermitRouteList s : routeActionTarget) {
            oldValue = oldValue + s.getKey() + ",";
        }
        for (Object ss : routeManage.getTarget()) {
            newValue = newValue + (String) ss + ",";
        }
        compair("Route Details", oldValue, newValue, compairValue);
        if (showOtherRoute && getSelectedRouteList() != null && getSelectedRouteList().size() > 0) {
            oldValue = "";
            newValue = "";
            if (prvAdminDobj.getOtherOfficeRouteList() != null) {
                for (VmPermitRouteDobj s : prvAdminDobj.getOtherOfficeRouteList()) {
                    oldValue = oldValue + s.getRoute_code() + ",";
                }
            }
            for (VmPermitRouteDobj ss : getSelectedRouteList()) {
                newValue = newValue + ss.getRoute_code() + ",";
            }
            compair("Other Office Route Details", oldValue, newValue, compairValue);
        }

        if (!CommonUtils.isNullOrBlank(String.valueOf(prvAdminDobj.getNo_of_trips())) && !CommonUtils.isNullOrBlank(String.valueOf(adminDobj.getNo_of_trips()))) {
            compair("Number of Trip", String.valueOf(prvAdminDobj.getNo_of_trips()), String.valueOf(adminDobj.getNo_of_trips()), compairValue);
        }
        PrimeFaces.current().executeScript("PF('compairVar').show();");
        PrimeFaces.current().ajax().update("permitForm:compair");
    }

    public void compair(String purpose, String oldValue, String newValue, List<PermitAdministratorDobj> compairValue) {
        if (!oldValue.equalsIgnoreCase(newValue)) {
            PermitAdministratorDobj dobj = new PermitAdministratorDobj();
            dobj.setPurpose(purpose);
            dobj.setOldValue(oldValue);
            dobj.setNewValue(newValue);
            compairValue.add(dobj);
        }
    }

    public String checkDetails() {
        String msg = "";
        if (CommonUtils.isNullOrBlank(adminDobj.getRegn_no())) {
            msg = "Please enter the Registration Number";
        }

        if (CommonUtils.isNullOrBlank(adminDobj.getAppl_no())) {
            msg = "Please enter the Application Number";
        }

        if (CommonUtils.isNullOrBlank(adminDobj.getPmt_no())) {
            msg = "Please enter the Permit Number";
        }

        if (CommonUtils.isNullOrBlank(adminDobj.getRcpt_no())) {
            msg = "Please enter the Receipt Number";
        }

        if (adminDobj.getPmt_type() == -1) {
            msg = "Please select any Permit Type";
        }

        if (adminDobj.getIssue_dt() == null) {
            msg = "Please enter the Issue Date";
        }

        if (adminDobj.getValid_from() == null) {
            msg = "Please enter the Valid From";
        }

        if (adminDobj.getValid_upto() == null) {
            msg = "Please enter the Valid Upto";
        }
        return msg;
    }

    public String savePmtDetails() {
        FacesMessage message = null;
        String returnUrl = "";
        String region = "";
        try {
            String pendingApplication = ServerUtil.applicationStatusForPermit(adminDobj.getRegn_no().toUpperCase(), Util.getUserStateCode());
            if (pmtIssueAuthCode == 0) {
                throw new VahanException("Permit office Details not found for this Vehicle");
            }
            if (CommonUtils.isNullOrBlank(pendingApplication)) {
                adminImpl = new PermitAdministratorImpl();
                List area = renderRouteArea ? selectedStates : areaManage.getTarget();
                for (Object s : area) {
                    region = region + s + ",";
                }
                if (renderRouteArea && render_payment_table_aitp && selectedStates != null && selectedStates.size() != paymentList.size()) {
                    throw new VahanException("Permit State and Payment States name count are different, please check.");
                }
                if (renderRouteArea && render_payment_table_aitp && paymentList.size() > 0 && selectedStates != null && selectedStates.size() > 0 && selectedStates.size() == paymentList.size()) {
                    String state_name = "";
                    for (String s : selectedStates) {
                        state_name = state_name + s + ",";
                    }
                    for (int i = 0; i < paymentList.size(); i++) {
                        if (!state_name.contains(paymentList.get(i).getPay_state_cd())) {
                            throw new VahanException("Permit States and Payment States name details not Match.");
                        }
                    }
                }
                adminDobj.setRegion_covered(region);
                if (!CommonUtils.isNullOrBlank(adminDobj.getRegionCoveredNP()) && !adminDobj.getRegionCoveredNP().equals("0")) {
                    adminDobj.setRegion_covered(adminDobj.getRegionCoveredNP() + ",");
                }

                if (!CommonUtils.isNullOrBlank(adminDobj.getRegionCoveredNP()) && region.length() > 0 && (adminDobj.getRegionCoveredNP().length() > 0 && !adminDobj.getRegionCoveredNP().equals("0"))) {
                    JSFUtils.showMessage("Please Select Either States or Region");
                    return "";
                }
                adminDobj.setOff_cd(pmtIssueAuthCode);

                boolean flag = adminImpl.saveDetailsInVtTables(adminDobj, prvAdminDobj, routeManage.getTarget(), compairValue, showNpDetails, paymentList, selectedRouteList);
                if (flag) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Successfully Save your deails");
                    PrimeFaces.current().executeScript("PF('compairVar').hide();");
                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                    returnUrl = "/ui/permit/permitAdminstratorForm.xhtml?faces-redirect=true";
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Data is not Save");
                    PrimeFaces.current().executeScript("PF('compairVar').hide();");
                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                }
            } else {
                setErrorMsg(pendingApplication);
                PrimeFaces.current().ajax().update("pendingApplicationID");
                PrimeFaces.current().executeScript("PF('pendingAppication').show();");
            }
        } catch (VahanException e) {
            PrimeFaces.current().executeScript("PF('compairVar').hide();");
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage(TableConstants.SomthingWentWrong);
        }
        return returnUrl;
    }

    public void validateInstrumentNumber(AjaxBehaviorEvent event) {

        AITPStateFeeDraftDobj selectedDobj = (AITPStateFeeDraftDobj) event.getComponent().getAttributes().get("paymentDobj");
        String error_message = null;
        if (selectedDobj.getInstrument_type() == null || selectedDobj.getInstrument_type().equals("-1")) {
            error_message = "Please Select Instrument Type";
        }
        if (selectedDobj.getBank_code() == null || selectedDobj.getBank_code().equals("-1")) {
            error_message = "Please Select Bank Name";
        }
        if (selectedDobj.getInstrument_amt() == 0) {
            error_message = "Please enter amount";
        }
        String dupInstrument = null;
        if (dupInstrument != null) {
            error_message = dupInstrument;
        }
        int commonInstrumentsNo = 0;
        for (AITPStateFeeDraftDobj dobj : getPaymentList()) {
            if (dobj.getInstrument_no() != null && selectedDobj.getInstrument_no() != null && dobj.getInstrument_no().equals(selectedDobj.getInstrument_no())
                    && dobj.getBank_code() != null && selectedDobj.getInstrument_no() != null && dobj.getBank_code().equals(dobj.getBank_code())
                    && dobj.getInstrument_no() != null && selectedDobj.getInstrument_no() != null && dobj.getInstrument_no().equals(selectedDobj.getInstrument_no())) {
                commonInstrumentsNo++;
            }
        }
        if (commonInstrumentsNo > 1) {
            error_message = "Duplicate Instrument Number";
        }
        if (error_message != null) {
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", error_message));
            selectedDobj.setInstrument_no("");
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

    public void addNewRow(AITPStateFeeDraftDobj payment) {
        String mode = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("actionmode");
        try {
            if ("add".equalsIgnoreCase(mode)) {
                if (paymentList.size() == 7) {
                    JSFUtils.showMessagesInDialog("Information", "Maximum number of Fees heads collection reached!", FacesMessage.SEVERITY_WARN);
                } else {
                    paymentList.add(new AITPStateFeeDraftDobj());
                    PrimeFaces.current().ajax().update("permitForm:payment_panel");
                    PrimeFaces.current().executeScript("PF('bui').hide();");
                }

            } else if ("minus".equalsIgnoreCase(mode)) {
                int lastIndex = paymentList.lastIndexOf(payment);
                if (lastIndex == 0 && paymentList.size() == 1) {
                    paymentList.clear();
                    paymentList.add(new AITPStateFeeDraftDobj());
                    PrimeFaces.current().ajax().update("permitForm:payment_panel");
                    PrimeFaces.current().executeScript("PF('bui').hide();");
                } else {
                    paymentList.remove(lastIndex);
                    PrimeFaces.current().ajax().update("permitForm:payment_panel");
                    PrimeFaces.current().executeScript("PF('bui').hide();");
                }
            }
        } catch (Exception e) {
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Something went wrong"));
        }

    }

    public PermitAdministratorDobj getAdminDobj() {
        return adminDobj;
    }

    public void setAdminDobj(PermitAdministratorDobj adminDobj) {
        this.adminDobj = adminDobj;
    }

    public List getPmtTypeList() {
        return pmtTypeList;
    }

    public void setPmtTypeList(List pmtTypeList) {
        this.pmtTypeList = pmtTypeList;
    }

    public List getPmtCatgList() {
        return pmtCatgList;
    }

    public void setPmtCatgList(List pmtCatgList) {
        this.pmtCatgList = pmtCatgList;
    }

    public List getSerTypeList() {
        return serTypeList;
    }

    public void setSerTypeList(List serTypeList) {
        this.serTypeList = serTypeList;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public PermitOwnerDetailDobj getOwnerDobj() {
        return ownerDobj;
    }

    public void setOwnerDobj(PermitOwnerDetailDobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    public PermitOwnerDetailImpl getOwnerImpl() {
        return ownerImpl;
    }

    public void setOwnerImpl(PermitOwnerDetailImpl ownerImpl) {
        this.ownerImpl = ownerImpl;
    }

    public PermitOwnerDetailBean getOwnerBean() {
        return ownerBean;
    }

    public void setOwnerBean(PermitOwnerDetailBean ownerBean) {
        this.ownerBean = ownerBean;
    }

    public String getTransportCatg() {
        return transportCatg;
    }

    public void setTransportCatg(String transportCatg) {
        this.transportCatg = transportCatg;
    }

    public PermitAdministratorDobj getPrvAdminDobj() {
        return prvAdminDobj;
    }

    public void setPrvAdminDobj(PermitAdministratorDobj prvAdminDobj) {
        this.prvAdminDobj = prvAdminDobj;
    }

    public List<PermitAdministratorDobj> getCompairValue() {
        return compairValue;
    }

    public void setCompairValue(List<PermitAdministratorDobj> compairValue) {
        this.compairValue = compairValue;
    }

    public DualListModel<PermitRouteList> getAreaManage() {
        return areaManage;
    }

    public void setAreaManage(DualListModel<PermitRouteList> areaManage) {
        this.areaManage = areaManage;
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

    public DualListModel<PermitRouteList> getRouteManage() {
        return routeManage;
    }

    public void setRouteManage(DualListModel<PermitRouteList> routeManage) {
        this.routeManage = routeManage;
    }

    public List<PermitRouteList> getRouteActionSource() {
        return routeActionSource;
    }

    public void setRouteActionSource(List<PermitRouteList> routeActionSource) {
        this.routeActionSource = routeActionSource;
    }

    public List<PermitRouteList> getRouteActionTarget() {
        return routeActionTarget;
    }

    public void setRouteActionTarget(List<PermitRouteList> routeActionTarget) {
        this.routeActionTarget = routeActionTarget;
    }

    public String getVia_route() {
        return via_route;
    }

    public void setVia_route(String via_route) {
        this.via_route = via_route;
    }

    public boolean isNoOfTripsrendered() {
        return noOfTripsrendered;
    }

    public void setNoOfTripsrendered(boolean noOfTripsrendered) {
        this.noOfTripsrendered = noOfTripsrendered;
    }

    public List getPurCdList() {
        return purCdList;
    }

    public void setPurCdList(List purCdList) {
        this.purCdList = purCdList;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public UserAuthorityDobj getAuthorityDobj() {
        return authorityDobj;
    }

    public void setAuthorityDobj(UserAuthorityDobj authorityDobj) {
        this.authorityDobj = authorityDobj;
    }

    public boolean isDisable_region() {
        return disable_region;
    }

    public void setDisable_region(boolean disable_region) {
        this.disable_region = disable_region;
    }

    /**
     * @return the stateList
     */
    public List getStateList() {
        return stateList;
    }

    /**
     * @return the selectedStatesNames
     */
    public String getSelectedStatesNames() {
        return selectedStatesNames;
    }

    /**
     * @return the selectedStates
     */
    public List<String> getSelectedStates() {
        return selectedStates;
    }

    /**
     * @param selectedStates the selectedStates to set
     */
    public void setSelectedStates(List<String> selectedStates) {
        this.selectedStates = selectedStates;
    }

    public boolean isRenderRouteArea() {
        return renderRouteArea;
    }

    public void setRenderRouteArea(boolean renderRouteArea) {
        this.renderRouteArea = renderRouteArea;
    }

    public int getNo_of_trip() {
        return no_of_trip;
    }

    public void setNo_of_trip(int no_of_trip) {
        this.no_of_trip = no_of_trip;
    }

    public List getRegionCoveredListNP() {
        return regionCoveredListNP;
    }

    public void setRegionCoveredListNP(List regionCoveredListNP) {
        this.regionCoveredListNP = regionCoveredListNP;
    }

    public boolean isShowRegionCovered() {
        return showRegionCovered;
    }

    public void setShowRegionCovered(boolean showRegionCovered) {
        this.showRegionCovered = showRegionCovered;
    }

    public int getPmtIssueAuthCode() {
        return pmtIssueAuthCode;
    }

    public void setPmtIssueAuthCode(int pmtIssueAuthCode) {
        this.pmtIssueAuthCode = pmtIssueAuthCode;
    }

    public boolean isPmtIssueAuthCodeFound() {
        return pmtIssueAuthCodeFound;
    }

    public void setPmtIssueAuthCodeFound(boolean pmtIssueAuthCodeFound) {
        this.pmtIssueAuthCodeFound = pmtIssueAuthCodeFound;
    }

    public boolean isMultiApplAllow() {
        return multiApplAllow;
    }

    public void setMultiApplAllow(boolean multiApplAllow) {
        this.multiApplAllow = multiApplAllow;
    }

    public boolean isMultiApplCondition() {
        return multiApplCondition;
    }

    public void setMultiApplCondition(boolean multiApplCondition) {
        this.multiApplCondition = multiApplCondition;
    }

    public List<AITPStateFeeDraftDobj> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<AITPStateFeeDraftDobj> paymentList) {
        this.paymentList = paymentList;
    }

    public String getHeader_name() {
        return header_name;
    }

    public void setHeader_name(String header_name) {
        this.header_name = header_name;
    }

    public List getBank_list() {
        return bank_list;
    }

    public void setBank_list(List bank_list) {
        this.bank_list = bank_list;
    }

    public List getInstrumentList() {
        return instrumentList;
    }

    public void setInstrumentList(List instrumentList) {
        this.instrumentList = instrumentList;
    }

    public boolean isRender_payment_table_aitp() {
        return render_payment_table_aitp;
    }

    public void setRender_payment_table_aitp(boolean render_payment_table_aitp) {
        this.render_payment_table_aitp = render_payment_table_aitp;
    }

    public List getState_list() {
        return state_list;
    }

    public void setState_list(List state_list) {
        this.state_list = state_list;
    }

    public PermitDetailBean getPermit_Dtls_bean() {
        return permit_Dtls_bean;
    }

    public void setPermit_Dtls_bean(PermitDetailBean permit_Dtls_bean) {
        this.permit_Dtls_bean = permit_Dtls_bean;
    }

    public boolean isShowNpDetails() {
        return showNpDetails;
    }

    public void setShowNpDetails(boolean showNpDetails) {
        this.showNpDetails = showNpDetails;
    }

    public Date getVehicleAgeUpto() {
        return vehicleAgeUpto;
    }

    public void setVehicleAgeUpto(Date vehicleAgeUpto) {
        this.vehicleAgeUpto = vehicleAgeUpto;
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

    public List getOfficeList() {
        return officeList;
    }

    public void setOfficeList(List officeList) {
        this.officeList = officeList;
    }

    public boolean isOtherRouteAllow() {
        return otherRouteAllow;
    }

    public void setOtherRouteAllow(boolean otherRouteAllow) {
        this.otherRouteAllow = otherRouteAllow;
    }

    public List<VmPermitRouteDobj> getSelectedRouteList() {
        return selectedRouteList;
    }

    public void setSelectedRouteList(List<VmPermitRouteDobj> selectedRouteList) {
        this.selectedRouteList = selectedRouteList;
    }

    public String[] getSelectedOffice() {
        return selectedOffice;
    }

    public void setSelectedOffice(String[] selectedOffice) {
        this.selectedOffice = selectedOffice;
    }
}
