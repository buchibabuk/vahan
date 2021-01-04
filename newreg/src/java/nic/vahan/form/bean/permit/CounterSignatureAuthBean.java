/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
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
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.db.TableList;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.CounterSignatureDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitCheckDetailsDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.CounterSignatureAuthImpl;
import nic.vahan.form.impl.permit.CounterSignatureImpl;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.form.impl.permit.PrintPermitDocInXhtmlImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;
import org.primefaces.PrimeFaces;

/**
 *
 * @author hcl
 */
@ManagedBean(name = "conSignAuth")
@ViewScoped
public class CounterSignatureAuthBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(CounterSignatureAuthBean.class);
    private CounterSignatureDobj countDobj = new CounterSignatureDobj();
    private PermitOwnerDetailDobj ownerDobjPrv;
    private CounterSignatureDobj countDobjPrv;
    private List stateList = new ArrayList<>();
    private List offList = new ArrayList<>();
    private List areaTypeList = new ArrayList<>();
    private List pmtTypeList = new ArrayList<>();
    private List periodModeList = new ArrayList<>();
    @ManagedProperty(value = "#{pmt_owner_dtls}")
    private PermitOwnerDetailBean ownerBean;
    PermitOwnerDetailDobj owner_dobj = null;
    private String app_no_msg;
    private String taxExem;
    private String header;
    private boolean panelHideShow = true;
    private boolean counterSignatureDtls = false;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    private Date minDate = DateUtil.parseDate(DateUtil.getCurrentDate());
    private Date maxDate = null;
    private String selectOneRadio = "1";
    PassengerPermitDetailImpl impl = new PassengerPermitDetailImpl();
    ///private boolean renderArea;
    private List<PermitRouteList> actionSource = new ArrayList<>();
    private List<PermitRouteList> actionTarget = new ArrayList<>();
    private DualListModel<PermitRouteList> routeManage;
    private String via_route = "";
    private int routeLength = 0;
    private String vahanMessages = null;
    //private boolean visibleRouteDtls = true;
    //private boolean disableRoutDtls = false;
    private boolean noOfTripsrendered = false;
    private int no_of_trip = 0;
    private boolean disableCountValidUpto = false;
    private boolean regnNoDisable = false;
    private Map<String, String> stateConfigMap = null;
    private SessionVariables sessionVariables = null;
    //private boolean allowReservationInCS = false;
    // private boolean allowRenewalCS = true;
    private List<PassengerPermitDetailDobj> prv_route_list = new ArrayList<>();
    //private boolean renderOwnStateRoute = false;
    private List<CounterSignatureDobj> priviousCSAuthData = null;
    @ManagedProperty(value = "#{pmtDtlsBean}")
    private PermitCheckDetailsBean pmtCheckDtsl;

    public CounterSignatureAuthBean() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
            vahanMessages = "Session time Out";
            return;
        }
        Map<String, String> routeMap = impl.getSourcesAreaMapWithStrBuil("0", Util.getUserStateCode(), Util.getUserOffCode());
        areaTypeList.clear();
        areaTypeList.add(new SelectItem("0", "SELECT ANY AREA/REAGION"));
        for (Map.Entry<String, String> entry : routeMap.entrySet()) {
            areaTypeList.add(new SelectItem(entry.getKey(), entry.getValue()));
        }
        String[][] data = MasterTableFiller.masterTables.TM_STATE.getData();
        for (int i = 0; i < data.length; i++) {
            stateList.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
        for (int i = 0; i < data.length; i++) {
            pmtTypeList.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
        periodModeList.add(new SelectItem("-1", "--- Select Period Mode ---"));
        for (int i = 0; i < data.length; i++) {
            if (("M").equalsIgnoreCase(data[i][0]) || ("Y").equalsIgnoreCase(data[i][0]) || ("D").equalsIgnoreCase(data[i][0]) || ("W").equalsIgnoreCase(data[i][0])) {
                periodModeList.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
        stateConfigMap = CommonPermitPrintImpl.getVmPermitStateConfiguration(sessionVariables.getStateCodeSelected());
    }

    @PostConstruct
    public void init() {
        try {

            if (getAppl_details().getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_COUNTER_SIGNATURE_AUTH_ENTRY
                    && !CommonUtils.isNullOrBlank(getAppl_details().getAppl_no())) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header = "COUNTER SIGNATURE AUTH RE-ENTRY";
                ownerBean.getPmt_dobj().setFlage(false);
                ownerBean.setValueinDOBJ(owner_dobj);
                counterSignatureDtls = false;
                // setVisibleRouteDtls(Boolean.parseBoolean(stateConfigMap.get("show_route_details_cs")));
                // setRenderArea(Boolean.parseBoolean(stateConfigMap.get("show_region_on_cs_permit")));
            } else if (getAppl_details().getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_COUNTER_SIGNATURE_AUTH_ENTRY
                    && CommonUtils.isNullOrBlank(getAppl_details().getAppl_no())) {
                // RequestContext context = RequestContext.getCurrentInstance();
                header = "COUNTER SIGNATURE AUTHARISATION ENTRY";
                counterSignatureDtls = true;
                taxExem = ServerUtil.getTmConfigurationParameters(Util.getUserStateCode()).getTax_exemption();
                // setVisibleRouteDtls(Boolean.parseBoolean(stateConfigMap.get("show_route_details_cs")));
                // setRenderArea(Boolean.parseBoolean(stateConfigMap.get("show_region_on_cs_permit")));
//                if (isVisibleRouteDtls()) {
//                    routeCodeDetail("NULL", TableList.vt_permit_route, Util.getUserStateCode(), Util.getUserOffCode());
//                    context.update("via Length trips");
//                }
            } else if (getAppl_details().getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_COUNTER_SIGNATURE_AUTH_VERIFICATION) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header = "COUNTER SIGNATURE AUTH VERIFICATION";
                fillDtlsVeriApproStage();
                ownerBean.getPmt_dobj().setFlage(false);
                ownerBean.setValueinDOBJ(owner_dobj);
                counterSignatureDtls = false;
//                setVisibleRouteDtls(Boolean.parseBoolean(stateConfigMap.get("show_route_details_cs")));
//                setRenderArea(Boolean.parseBoolean(stateConfigMap.get("show_region_on_cs_permit")));
            } else if (getAppl_details().getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_COUNTER_SIGNATURE_AUTH_APPROVAL) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header = "COUNTER SIGNATURE AUTH APPROVAL";
                fillDtlsVeriApproStage();
                ownerBean.setValueinDOBJ(owner_dobj);
                ownerBean.getPmt_dobj().setFlage(true);
                counterSignatureDtls = true;
//                setVisibleRouteDtls(Boolean.parseBoolean(stateConfigMap.get("show_route_details_cs")));
//                setRenderArea(Boolean.parseBoolean(stateConfigMap.get("show_region_on_cs_permit")));
            }

        } catch (Exception e) {
        }
    }

    public void get_details() {

        CounterSignatureDobj couDobj = null;
        InsDobj insDobj = null;
        try {
            if (CommonUtils.isNullOrBlank(countDobj.getRegn_no()) && ("1").equalsIgnoreCase(getSelectOneRadio())) {
                JSFUtils.showMessagesInDialog("Error", "Please enter registration no.", FacesMessage.SEVERITY_ERROR);
            } else {
                if (!CommonUtils.isNullOrBlank(countDobj.getRegn_no())) {
                    String str = ServerUtil.applicationStatusForPermit(countDobj.getRegn_no().trim().toUpperCase(), Util.getUserStateCode());
                    if (!CommonUtils.isNullOrBlank(str)) {
                        showApplPendingDialogBox(str);
                        return;
                    }
                }
                PermitOwnerDetailImpl ownImpl = new PermitOwnerDetailImpl();
                PassengerPermitDetailImpl passImp = new PassengerPermitDetailImpl();
                PermitOwnerDetailDobj ownDobj = ownImpl.set_Owner_appl_db_to_dobj(countDobj.getRegn_no().toUpperCase(), null);
                PassengerPermitDetailDobj permit_dobj = passImp.set_vt_permit_regnNo_to_dobj(countDobj.getRegn_no().toUpperCase(), "");

                if (permit_dobj != null) {
                    countDobj.setPmt_type(Integer.parseInt(permit_dobj.getPmt_type()));
                    countDobj.setPmt_no(permit_dobj.getPmt_no());
                    countDobj.setValid_from(permit_dobj.getValid_from());
                    countDobj.setValid_upto(permit_dobj.getValid_upto());
                    // setPrv_route_list(pmtEndoImpl.getRouteAndOtherData(permit_dobj.getState_cd(), Integer.parseInt(permit_dobj.getOff_cd()), permit_dobj, TableConstants.TM_ROLE_PMT_COUNTER_SIGNATURE_ENTRY));
                    setPrv_route_list(new PrintPermitDocInXhtmlImpl().getRouteData(permit_dobj.getApplNo(), permit_dobj.getState_cd(), Integer.parseInt(permit_dobj.getOff_cd()), TableList.vt_permit_route, false));
                }

                if (ownDobj != null) {
                    ownerBean.setValueinDOBJ(ownDobj);
                    countDobj.setMaker_name(ownDobj.getMakerName());
                    countDobj.setModel_name(ownDobj.getModelName());
                } else {
                    ownerBean.getPmt_dobj().setFlage(false);
                    countDobj.setMaker_name(null);
                    countDobj.setModel_name(null);
                }
                counterSignatureDtls = false;
                pmtCheckDtsl.getAlldetails(countDobj.getRegn_no().toUpperCase(), insDobj, ownDobj.getState_cd(), ownDobj.getOff_cd());
                regnNoDisable = true;
                // setDisableRoutDtls(true);
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Error", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public final void fillDtlsVeriApproStage() {

        try {
            panelHideShow = false;
            PermitOwnerDetailImpl ownImpl = new PermitOwnerDetailImpl();
            CounterSignatureAuthImpl cgAuthImpl = new CounterSignatureAuthImpl();
            owner_dobj = ownImpl.setVtOwnerDtlsOnlyDisplay(appl_details.getRegn_no(), Util.getUserStateCode());
            pmtCheckDtsl.getAlldetails(appl_details.getRegn_no(), null, owner_dobj.getState_cd(), owner_dobj.getOff_cd());
            if (owner_dobj == null) {
                JSFUtils.showMessagesInDialog("Error", "Owner details not found", FacesMessage.SEVERITY_ERROR);
            } else {

                ownerDobjPrv = new PermitOwnerDetailDobj();
                ownerDobjPrv = (PermitOwnerDetailDobj) owner_dobj.clone();
            }
            CounterSignatureDobj counDobj = cgAuthImpl.getDeatilForApplNo(appl_details.getAppl_no());

            if (counDobj == null) {
                JSFUtils.showMessagesInDialog("Error", "Counter Signature Autharisation details not found", FacesMessage.SEVERITY_ERROR);
            } else {
                if (counDobj.getCount_valid_upto() != null && minDate != null && counDobj.getCount_valid_upto().before(minDate)) {
                    this.minDate = ServerUtil.dateRange(counDobj.getCount_valid_upto(), 0, 0, 0);
                }
                if (maxDate == null) {
                    this.maxDate = ServerUtil.dateRange(counDobj.getCount_valid_upto(), 0, 0, 0);
                }
                PassengerPermitDetailDobj permit_dobj = new PassengerPermitDetailImpl().set_vt_permit_regnNo_to_dobj(appl_details.getRegn_no(), "");
                if (permit_dobj != null) {
                    setPrv_route_list(new PrintPermitDocInXhtmlImpl().getRouteData(permit_dobj.getApplNo(), permit_dobj.getState_cd(), Integer.parseInt(permit_dobj.getOff_cd()), TableList.vt_permit_route, false));
                }
                if (CommonUtils.isNullOrBlank(counDobj.getMaker_name())) {
                    counDobj.setMaker_name(null);
                }
                if (CommonUtils.isNullOrBlank(counDobj.getModel_name())) {
                    counDobj.setModel_name(null);
                }
                setCountDobj(counDobj);
                onSelectState();
                countDobjPrv = new CounterSignatureDobj();
                countDobjPrv = (CounterSignatureDobj) counDobj.clone();
            }
            countDobj.setAppl_no(appl_details.getAppl_no());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void save_details() {
        try {
            checkField(ownerBean.setValueinDOBJ(), countDobj);
            CounterSignatureAuthImpl cgAuthImpl = new CounterSignatureAuthImpl();
            countDobj.setPur_cd(TableConstants.VM_PMT_COUNTER_SIGNATURE_AUTH_PUR_CD);
            String str = ServerUtil.applicationStatusForPermit(countDobj.getRegn_no().toUpperCase(), Util.getUserStateCode());
            if (countDobj.getCount_valid_upto().after(countDobj.getValid_upto())) {
                throw new VahanException("Counter Signature Auth validity can't be greater to Permit validity");
            } else if (CommonUtils.isNullOrBlank(str)) {
                String appl_no = cgAuthImpl.createApplication(ownerBean.setValueinDOBJ(), countDobj);
                printDialogBox(appl_no);
            } else {
                showApplPendingDialogBox(str);
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Exception", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public static String checkPmtValidation(PermitCheckDetailsDobj validDobj) {
        String msg = null;
        if (validDobj != null) {
            if (validDobj.getTaxUpto().equalsIgnoreCase("LIFE TIME") && !CommonUtils.isNullOrBlank(validDobj.getTaxMode()) && ("OLS".contains(validDobj.getTaxMode()))) {
                validDobj.setTaxUpto("31-DEC-2999");
            } else if (validDobj.getTaxUpto().equalsIgnoreCase("LIFE TIME") && !CommonUtils.isNullOrBlank(validDobj.getTaxMode()) && (!"OLS".contains(validDobj.getTaxMode()))) {
                validDobj.setTaxUpto("");
            }
            if (CommonUtils.isNullOrBlank(validDobj.getTaxUpto()) || !(DateUtils.compareDates(DateUtils.getCurrentLocalDate(), CommonPermitPrintImpl.getDateDD_MMM_YYYY(validDobj.getTaxUpto())) <= 1)) {
                msg = validDobj.isExemptedTax() && CommonUtils.isNullOrBlank(validDobj.getTaxUpto()) ? null : "Please pay MV Tax first.";
            } else if (!validDobj.isExemptedFitness() && (CommonUtils.isNullOrBlank(validDobj.getFitValidTo()) || !(DateUtils.compareDates(DateUtils.getCurrentLocalDate(), CommonPermitPrintImpl.getDateDD_MMM_YYYY(validDobj.getFitValidTo())) <= 1))) {
                msg = "Your vehicle fitness is expired.";
            } else if (!validDobj.isExemptedIns() && CommonUtils.isNullOrBlank(validDobj.getInsUpto())) {
                msg = "Please fill the insurance details.";
            } else if (!validDobj.isExemptedIns() && !(validDobj.isInsValid())) {
                msg = "Your vehicle insurance is expired.";
            } else if ((!validDobj.isChalPending() && !validDobj.isExemptedChallan()) || (validDobj.isChalPending() && validDobj.isChalPendingInService())) {
                msg = "Please pay challan first.";
            } else if (validDobj.isVehicleIsBlackListed()) {
                msg = "Vehicle is Black Listed";
            }
        } else {
            msg = "Something went wrong, try again.";
        }
        return msg;
    }

    public void checkField(PermitOwnerDetailDobj ownDobj, CounterSignatureDobj countDobj) throws VahanException {
        String msg = null;
        try {
            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            parameters.setOWNER_CD(ownerBean.getOwner_cd());
            if (CommonUtils.isNullOrBlank(ownerBean.getVh_class())) {
                throw new VahanException("Vehicle class not found.");
            }
            parameters.setVH_CLASS(Integer.parseInt(ownerBean.getVh_class()));
            parameters.setPUR_CD(TableConstants.VM_PMT_COUNTER_SIGNATURE_AUTH_PUR_CD);
            parameters.setFUEL(ownerBean.getFuel_type());
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

            if (CommonUtils.isNullOrBlank(countDobj.getState_to()) || countDobj.getState_to().equalsIgnoreCase("-1")) {
                throw new VahanException("Please select state to");
            } else if (countDobj.getOff_cd_to() == -1 || countDobj.getOff_cd_to() == 0) {
                throw new VahanException("Please select office to");
            } else if (CommonUtils.isNullOrBlank(countDobj.getPmt_no())) {
                throw new VahanException("Please enter permit no.");
            } else if (countDobj.getPmt_type() == -1 || countDobj.getPmt_type() == 0) {
                throw new VahanException("Please enter permit type");
            } else if (countDobj.getPeriod_mode().equalsIgnoreCase("-1") || CommonUtils.isNullOrBlank(countDobj.getPeriod_mode())) {
                throw new VahanException("Please Select Period Mode");
            } else if (countDobj.getPeriod() <= 0) {
                throw new VahanException("Please enter period");
            } else if (countDobj.getValid_from() == null) {
                throw new VahanException("Please enter permit valid from");
            } else if (countDobj.getValid_upto() == null) {
                throw new VahanException("Please enter permit valid upto");
            }
            msg = CommonPermitPrintImpl.checkPmtValidation(pmtCheckDtsl.getDtlsDobj());
            if (msg != null) {
                throw new VahanException(msg);
            }

        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void printDialogBox(String app_no) {
        if (!CommonUtils.isNullOrBlank(app_no)) {
            setApp_no_msg("Application Number generated :" + app_no + ". Please note down the Application No for future reference.");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        } else {
            setApp_no_msg("There are some problem to genrate the application");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        }
    }

    public void showApplPendingDialogBox(String str) {
        setApp_no_msg(str);
        PrimeFaces.current().ajax().update("app_num_id");
        PrimeFaces.current().executeScript("PF('appNum').show();");
    }

    public void onSelectState() {
        String[][] data = MasterTableFiller.masterTables.TM_OFFICE.getData();
        offList.clear();
        // dsdf
        for (int i = 0; i < data.length; i++) {
            if (countDobj.getState_to().equalsIgnoreCase(data[i][13])) {
                offList.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
    }

    public void changePeriodMode() {
        countDobj.setPeriod(0);

    }

    public void blurActionPeriod() {

        Calendar cal = Calendar.getInstance();
        if (("Y").equalsIgnoreCase(countDobj.getPeriod_mode())) {
            cal.add(Calendar.YEAR, countDobj.getPeriod());
            cal.add(Calendar.DAY_OF_YEAR, -1);
        } else if (("M").equalsIgnoreCase(countDobj.getPeriod_mode())) {
            cal.add(Calendar.MONTH, countDobj.getPeriod());
            cal.add(Calendar.DAY_OF_MONTH, -1);
        } else if ("D".equalsIgnoreCase(countDobj.getPeriod_mode())) {
            cal.add(Calendar.DAY_OF_YEAR, countDobj.getPeriod());
            cal.add(Calendar.DAY_OF_YEAR, -1);
        } else if ("W".equalsIgnoreCase(countDobj.getPeriod_mode())) {
            cal.add(Calendar.WEEK_OF_YEAR, countDobj.getPeriod());
            cal.add(Calendar.DAY_OF_WEEK, -1);
        }
        maxDate = cal.getTime();
    }

    @Override
    public String save() {
        CounterSignatureImpl impl = new CounterSignatureImpl();
        try {
            impl.stayOnTheSameStage(appl_details.getAppl_no(), ownerBean.setValueinDOBJ(), countDobj, ComparisonBeanImpl.changedDataContents(compareChanges()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "seatwork";
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        try {
            if (ownerDobjPrv == null || countDobjPrv == null) {
                return getCompBeanList();
            }
            getCompBeanList().clear();
            if (ownerDobjPrv.getOwner_name() != null && ownerBean.getOwner_name() != null) {
                Compare("Owner Name", ownerDobjPrv.getOwner_name().toUpperCase(), ownerBean.getOwner_name().toUpperCase(), (ArrayList) getCompBeanList());
            }
            if (ownerDobjPrv.getF_name() != null && ownerBean.getF_name() != null) {
                Compare("Father/Husband's Name", ownerDobjPrv.getF_name().toUpperCase(), ownerBean.getF_name().toUpperCase(), (ArrayList) getCompBeanList());
            }

            Compare("Vehicle Class", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getVh_class_array(), String.valueOf(ownerDobjPrv.getVh_class())), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getVh_class_array(), ownerBean.getVh_class()), (ArrayList) getCompBeanList());

            if (String.valueOf(ownerDobjPrv.getMobile_no()) != null && ownerBean.getMobile_no() != null) {
                Compare("Mobile No.", String.valueOf(ownerDobjPrv.getMobile_no()), ownerBean.getMobile_no(), (ArrayList) getCompBeanList());
            }
            if (ownerDobjPrv.getEmail_id() != null && ownerBean.getEmail_id() != null) {
                Compare("Email ID", ownerDobjPrv.getEmail_id().toUpperCase(), ownerBean.getEmail_id().toUpperCase(), (ArrayList) getCompBeanList());
            }
            if (ownerDobjPrv.getC_add1() != null && ownerBean.getC_add1() != null) {
                Compare("Current House No. & Street Name", ownerDobjPrv.getC_add1().toUpperCase(), ownerBean.getC_add1().toUpperCase(), (ArrayList) getCompBeanList());
            }
            if (ownerDobjPrv.getC_add2() != null && ownerBean.getC_add2() != null) {
                Compare("Current Village/Town/City ", ownerDobjPrv.getC_add2().toUpperCase(), ownerBean.getC_add2().toUpperCase(), (ArrayList) getCompBeanList());
            }
            if (ownerDobjPrv.getC_add3() != null && ownerBean.getC_add3() != null) {
                Compare("Current Landmark/Police Station", ownerDobjPrv.getC_add3().toUpperCase(), ownerBean.getC_add3().toUpperCase(), (ArrayList) getCompBeanList());
            }

            Compare("Current State", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_c_state(), ownerDobjPrv.getC_state()), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_c_state(), ownerBean.getC_state()), (ArrayList) getCompBeanList());
            Compare("Current District", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_c_district(), String.valueOf(ownerDobjPrv.getC_district())), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_c_district(), ownerBean.getC_district()), (ArrayList) getCompBeanList());

            if (String.valueOf(ownerDobjPrv.getC_pincode()) != null && ownerBean.getC_pincode() != null) {
                Compare("Current Pin Code", String.valueOf(ownerDobjPrv.getC_pincode()), ownerBean.getC_pincode(), (ArrayList) getCompBeanList());
            }
            if (ownerDobjPrv.getP_add1() != null && ownerBean.getP_add1() != null) {
                Compare("Permanent House No. & Street Name", ownerDobjPrv.getP_add1().toUpperCase(), ownerBean.getP_add1().toUpperCase(), (ArrayList) getCompBeanList());
            }
            if (ownerDobjPrv.getP_add2() != null && ownerBean.getP_add2() != null) {
                Compare("Permanent Village/Town/City ", ownerDobjPrv.getP_add2().toUpperCase(), ownerBean.getP_add2().toUpperCase(), (ArrayList) getCompBeanList());
            }
            if (ownerDobjPrv.getP_add3() != null && ownerBean.getP_add3() != null) {
                Compare("Permanent Landmark/Police Station", ownerDobjPrv.getP_add3().toUpperCase(), ownerBean.getP_add3().toUpperCase(), (ArrayList) getCompBeanList());
            }
            Compare("Permanent State", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_p_state(), ownerDobjPrv.getP_state()), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_p_state(), ownerBean.getP_state()), (ArrayList) getCompBeanList());
            Compare("Permanent District", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_p_district(), String.valueOf(ownerDobjPrv.getP_district())), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_p_district(), ownerBean.getP_district()), (ArrayList) getCompBeanList());
            if (String.valueOf(ownerDobjPrv.getP_pincode()) != null && ownerBean.getP_pincode() != null) {
                Compare("Permanent Pin Code", String.valueOf(ownerDobjPrv.getP_pincode()), ownerBean.getP_pincode(), (ArrayList) getCompBeanList());
            }

            Compare("State To", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) getStateList(), countDobjPrv.getState_cd_from()), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) getStateList(), countDobj.getState_cd_from()), (ArrayList) getCompBeanList());
            Compare("Office To", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) getOffList(), String.valueOf(countDobjPrv.getOff_cd_from())), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) getOffList(), String.valueOf(countDobj.getOff_cd_from())), (ArrayList) getCompBeanList());
            Compare("Permit Type", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) getPmtTypeList(), String.valueOf(countDobjPrv.getPmt_type())), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) getPmtTypeList(), String.valueOf(countDobj.getPmt_type())), (ArrayList) getCompBeanList());
            Compare("Permit Number", countDobjPrv.getPmt_no().toUpperCase(), countDobj.getPmt_no().toUpperCase(), (ArrayList) getCompBeanList());
            Compare("Period Mode", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) getPeriodModeList(), countDobjPrv.getPeriod_mode()), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) getPeriodModeList(), countDobj.getPeriod_mode()), (ArrayList) getCompBeanList());
            Compare("Period", String.valueOf(countDobjPrv.getPeriod()), String.valueOf(countDobjPrv.getPeriod()), (ArrayList) getCompBeanList());
            Compare("Maximum Valid Upto", countDobjPrv.getCount_valid_upto(), countDobj.getCount_valid_upto(), (ArrayList) getCompBeanList());
            Compare("Valid From", countDobjPrv.getValid_from(), countDobj.getValid_from(), (ArrayList) getCompBeanList());
            Compare("Valid Upto", countDobjPrv.getValid_upto(), countDobj.getValid_upto(), (ArrayList) getCompBeanList());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return getCompBeanList();
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        CounterSignatureImpl impl = new CounterSignatureImpl();
        CounterSignatureAuthImpl cgAuthImpl = new CounterSignatureAuthImpl();
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
                if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_COUNTER_SIGNATURE_AUTH_VERIFICATION
                        || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_COUNTER_SIGNATURE_AUTH_ENTRY) {
                    return_location = cgAuthImpl.verificationStage(appl_details.getAppl_no(), status, ownerBean.setValueinDOBJ(), countDobj, ComparisonBeanImpl.changedDataContents(compareChanges()));
                }
                if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_COUNTER_SIGNATURE_AUTH_APPROVAL) {
                    countDobj.setPur_cd(appl_details.getPur_cd());
                    return_location = cgAuthImpl.approvalStage(appl_details.getAppl_no(), appl_details.getRegn_no(), status, ownerBean.setValueinDOBJ(), countDobj);
                }
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Exception", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return return_location;
    }

    public void routeCodeDetail(String Appl_no, String TableName, String state_cd, int off_cd) {
        try {
            CounterSignatureImpl impl = new CounterSignatureImpl();
            boolean flage = false;
            noOfTripsrendered = false;
            actionSource.clear();
            actionTarget.clear();
            Map<String, String> routeMap = impl.getSourcesRouteMap(Appl_no, TableName, state_cd, off_cd);
            for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                actionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
            Map<String, String> mapRouteList = impl.getTargetRouteMap(Appl_no, TableName, state_cd, off_cd);
            for (Map.Entry<String, String> entry : mapRouteList.entrySet()) {
                flage = true;
                actionTarget.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
            setVia_route("");
            if (flage) {
                setVia_route(impl.getRouteViaRouteList(Appl_no, state_cd, TableName, off_cd));
                setRouteLength(impl.getRouteLength(null, Appl_no, TableName, state_cd, off_cd));
                noOfTripsrendered = true;
            }
            routeManage = new DualListModel<PermitRouteList>(actionSource, actionTarget);
        } catch (VahanException e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
        }
    }

    public void onTransfer() {
        try {
            CounterSignatureImpl impl = new CounterSignatureImpl();
            String route_cd = "";

            setVia_route(impl.getRouteVia(routeManage.getTarget(), Util.getUserStateCode(), Util.getUserOffCode()));
            setRouteLength(impl.getRouteLength(routeManage.getTarget(), null, null, Util.getUserStateCode(), Util.getUserOffCode()));
            if (getVia_route().isEmpty()) {
                noOfTripsrendered = false;
            } else {
                noOfTripsrendered = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
        }
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

    public CounterSignatureDobj getCountDobj() {
        return countDobj;
    }

    public void setCountDobj(CounterSignatureDobj countDobj) {
        this.countDobj = countDobj;
    }

    public List getStateList() {
        return stateList;
    }

    public void setStateList(List stateList) {
        this.stateList = stateList;
    }

    public List getOffList() {
        return offList;
    }

    public void setOffList(List offList) {
        this.offList = offList;
    }

    public List getPmtTypeList() {
        return pmtTypeList;
    }

    public void setPmtTypeList(List pmtTypeList) {
        this.pmtTypeList = pmtTypeList;
    }

    public List getPeriodModeList() {
        return periodModeList;
    }

    public void setPeriodModeList(List periodModeList) {
        this.periodModeList = periodModeList;
    }

    public PermitOwnerDetailBean getOwnerBean() {
        return ownerBean;
    }

    public void setOwnerBean(PermitOwnerDetailBean ownerBean) {
        this.ownerBean = ownerBean;
    }

    public String getApp_no_msg() {
        return app_no_msg;
    }

    public void setApp_no_msg(String app_no_msg) {
        this.app_no_msg = app_no_msg;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public boolean isPanelHideShow() {
        return panelHideShow;
    }

    public void setPanelHideShow(boolean panelHideShow) {
        this.panelHideShow = panelHideShow;
    }

    public boolean isCounterSignatureDtls() {
        return counterSignatureDtls;
    }

    public void setCounterSignatureDtls(boolean counterSignatureDtls) {
        this.counterSignatureDtls = counterSignatureDtls;
    }

    public PermitOwnerDetailDobj getOwnerDobjPrv() {
        return ownerDobjPrv;
    }

    public void setOwnerDobjPrv(PermitOwnerDetailDobj ownerDobjPrv) {
        this.ownerDobjPrv = ownerDobjPrv;
    }

    public CounterSignatureDobj getCountDobjPrv() {
        return countDobjPrv;
    }

    public void setCountDobjPrv(CounterSignatureDobj countDobjPrv) {
        this.countDobjPrv = countDobjPrv;
    }

    public PermitOwnerDetailDobj getOwner_dobj() {
        return owner_dobj;
    }

    public void setOwner_dobj(PermitOwnerDetailDobj owner_dobj) {
        this.owner_dobj = owner_dobj;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public String getSelectOneRadio() {
        return selectOneRadio;
    }

    public void setSelectOneRadio(String selectOneRadio) {
        this.selectOneRadio = selectOneRadio;
    }

    /**
     * @return the areaTypeList
     */
    public List getAreaTypeList() {
        return areaTypeList;
    }

    /**
     * @param areaTypeList the areaTypeList to set
     */
    public void setAreaTypeList(List areaTypeList) {
        this.areaTypeList = areaTypeList;
    }

    public DualListModel<PermitRouteList> getRouteManage() {
        return routeManage;
    }

    public void setRouteManage(DualListModel<PermitRouteList> routeManage) {
        this.routeManage = routeManage;
    }

    public String getVia_route() {
        return via_route;
    }

    public void setVia_route(String via_route) {
        this.via_route = via_route;
    }

    public int getRouteLength() {
        return routeLength;
    }

    public void setRouteLength(int routeLength) {
        this.routeLength = routeLength;
    }

    public boolean isNoOfTripsrendered() {
        return noOfTripsrendered;
    }

    public void setNoOfTripsrendered(boolean noOfTripsrendered) {
        this.noOfTripsrendered = noOfTripsrendered;
    }

    public int getNo_of_trip() {
        return no_of_trip;
    }

    public void setNo_of_trip(int no_of_trip) {
        this.no_of_trip = no_of_trip;
    }

    public boolean isDisableCountValidUpto() {
        return disableCountValidUpto;
    }

    public void setDisableCountValidUpto(boolean disableCountValidUpto) {
        this.disableCountValidUpto = disableCountValidUpto;
    }

    public List<PassengerPermitDetailDobj> getPrv_route_list() {
        return prv_route_list;
    }

    public void setPrv_route_list(List<PassengerPermitDetailDobj> prv_route_list) {
        this.prv_route_list = prv_route_list;
    }

    public PermitCheckDetailsBean getPmtCheckDtsl() {
        return pmtCheckDtsl;
    }

    public void setPmtCheckDtsl(PermitCheckDetailsBean pmtCheckDtsl) {
        this.pmtCheckDtsl = pmtCheckDtsl;
    }

    public boolean isRegnNoDisable() {
        return regnNoDisable;
    }

    public void setRegnNoDisable(boolean regnNoDisable) {
        this.regnNoDisable = regnNoDisable;
    }

    public List<CounterSignatureDobj> getPriviousCSAuthData() {
        return priviousCSAuthData;
    }

    public void setPriviousCSAuthData(List<CounterSignatureDobj> priviousCSAuthData) {
        this.priviousCSAuthData = priviousCSAuthData;
    }

    public String getTaxExem() {
        return taxExem;
    }

    public void setTaxExem(String taxExem) {
        this.taxExem = taxExem;
    }

    public String getVahanMessages() {
        return vahanMessages;
    }

    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }
}
