package nic.vahan.form.bean.permit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.user_mgmt.dobj.UserAuthorityDobj;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.form.impl.permit.PermitAdministratorImpl;
import nic.vahan.form.impl.permit.PermitDetailImpl;
import nic.vahan.server.CommonUtils;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Manoj
 */
@ManagedBean(name = "offerAdminBean")
@ViewScoped
public class PermitOfferAdminBean {

    private static final Logger LOGGER = Logger.getLogger(PermitOfferAdminBean.class);
    private PassengerPermitDetailDobj pmt_dobj;
    private List pmtTypeList = new ArrayList();
    private List pmtCatgList = new ArrayList();
    private List serTypeList = new ArrayList();
    private List period = new ArrayList();
    private String appl_no = "";
    private String transportCatg = "";
    private PermitOwnerDetailDobj ownerDobj;
    private PermitOwnerDetailImpl ownerImpl;
    @ManagedProperty(value = "#{pmt_owner_dtls}")
    private PermitOwnerDetailBean ownerBean;
    PermitAdministratorImpl adminImpl;
    PermitOwnerDetailImpl owner_impl;
    PassengerPermitDetailImpl passImp;
    private PassengerPermitDetailDobj prv_pmt_dobj;
    private PermitOwnerDetailDobj prv_ownerDobj;
    private List<ComparisonBean> compBeanList = new ArrayList<ComparisonBean>();
    private DualListModel<PermitRouteList> areaManage;
    private List<PermitRouteList> areaActionSource = new ArrayList<>();
    private List<PermitRouteList> areaActionTarget = new ArrayList<>();
    private DualListModel<PermitRouteList> routeManage;
    private List<PermitRouteList> routeActionSource = new ArrayList<>();
    private List<PermitRouteList> routeActionTarget = new ArrayList<>();
    private String via_route = "";
    int permitIssuingAuthCode = 0;
    private boolean noOfTripsrendered = false;
    private List purCdList = new ArrayList();
    private String errorMsg = "";
    UserAuthorityDobj authorityDobj = null;
    private String vahanMessages = null;
    private SessionVariables sessionVariables = null;
    private boolean renderSaveBtn = false;

    public PermitOfferAdminBean() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
            vahanMessages = "Session time Out";
            return;
        }
        passImp = new PassengerPermitDetailImpl();
        permitIssuingAuthCode = CommonPermitPrintImpl.getOffCdPermissionByOfficer(sessionVariables.getOffCodeSelected(), sessionVariables.getStateCodeSelected());
        areaManage = new DualListModel<>(areaActionSource, areaActionTarget);
        routeManage = new DualListModel<>(routeActionSource, routeActionTarget);
        pmt_dobj = new PassengerPermitDetailDobj();
        String[][] data = MasterTableFiller.masterTables.vm_service_type.getData();
        authorityDobj = Util.getUserAuthority();
        for (int i = 0; i < data.length; i++) {
            serTypeList.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
        for (int i = 0; i < data.length; i++) {
            if (("M").equalsIgnoreCase(data[i][0]) || ("Y").equalsIgnoreCase(data[i][0])) {
                period.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
        data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_FRESH_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD))) {
                purCdList.add(new SelectItem(data[i][0], data[i][1]));
            }
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
                                if (data[i][0].equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                                        && data[i][3].equalsIgnoreCase(pmt_dobj.getPmt_type())) {
                                    pmtCatgList.add(new SelectItem(data[i][1], data[i][2]));
                                }
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < data.length; j++) {
                        for (Object obj : authorityDobj.getSelectedPermitCatg()) {
                            if (data[j][0].equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                                    && Integer.parseInt(data[j][3]) == Integer.parseInt(pmt_dobj.getPmt_type())
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
        }
    }

    public void pmtGetDetails() {
        FacesMessage message = null;
        String str = "";
        try {
            String appl_no = getAppl_no().toUpperCase().trim();
            adminImpl = new PermitAdministratorImpl();
            boolean status = adminImpl.checkApplDetailStatus(appl_no);
            if (status) {
                owner_impl = new PermitOwnerDetailImpl();
                ownerDobj = owner_impl.set_VA_Owner_permit_to_dobj(appl_no, "NEW");
                Map<String, String> OffAllotResult = PermitDetailImpl.getOffAllotmentResult(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), ownerDobj.getOff_cd());
                if (ownerDobj != null
                        && "true".equalsIgnoreCase(OffAllotResult.get("offAllowed"))) {
                    fillCombo();
                    prv_pmt_dobj = new PassengerPermitDetailDobj();
                    ownerBean.setValueinDOBJ(ownerDobj);
                    ownerBean.getPmt_dobj().setFlage(false);
                    pmt_dobj = passImp.set_permit_appl_db_to_dobj(appl_no);
                    renderSaveBtn = pmt_dobj != null ? true : false;
                    pmt_dobj.setPaction_code(pmt_dobj.getPaction());
                    pmt_dobj.setPmt_type_code(pmt_dobj.getPmt_type());
                    if (Integer.parseInt(pmt_dobj.getPmtCatg()) != 0) {
                        onSelectPermitType();
                    }
                    prv_pmt_dobj = (PassengerPermitDetailDobj) pmt_dobj.clone();
                    prv_ownerDobj = (PermitOwnerDetailDobj) ownerDobj.clone();
                    str = pmt_dobj.getRegion_covered();
                    if (CommonUtils.isNullOrBlank(str)) {
                        areaDetailfiller(sessionVariables.getStateCodeSelected(), str, permitIssuingAuthCode);
                        routeCodeDetail(appl_no, TableList.va_permit_route, sessionVariables.getStateCodeSelected(), permitIssuingAuthCode);
                    } else {
                        String[] temp = str.split(",");
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < temp.length; j++) {
                            stringBuilder.append("(").append("'").append(temp[j]).append("'").append("),");
                        }
                        areaDetailfiller(sessionVariables.getStateCodeSelected(), stringBuilder.substring(0, stringBuilder.length() - 1), permitIssuingAuthCode);
                        routeCodeDetail(appl_no, TableList.va_permit_route, sessionVariables.getStateCodeSelected(), permitIssuingAuthCode);
                    }
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Vehicle/Permit Not Found");
                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                }
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Applcation Number is Not Valid");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);

            }
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Appl_no:- " + appl_no.toUpperCase() + " / " + e.toString() + " " + e.getStackTrace()[0]);
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
        if (CommonUtils.isNullOrBlank(transportCatg) && ownerDobj.getVh_class() != -1) {
            throw new VahanException("This Vehicle is not Transpost Vehicle");
        } else if (authorityDobj == null) {
            throw new VahanException("User not authorized to any modify");
        } else {
            pmtTypeList.clear();
            data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
            if (authorityDobj.getPermitType().size() == 1 && ownerDobj.getVh_class() != -1) {
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
            } else if (pmtTypeList.size() <= 0 && ownerDobj.getVh_class() == -1) {
                for (Object obj : authorityDobj.getPermitType()) {
                    if (String.valueOf(obj).equalsIgnoreCase("ANY")) {
                        for (int i = 0; i < data.length; i++) {
                            pmtTypeList.add(new SelectItem(data[i][0], data[i][1]));
                        }
                    } else {
                        for (int i = 0; i < data.length; i++) {
                            if (data[i][0].equalsIgnoreCase(String.valueOf(obj))) {
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

    public void areaDetailfiller(String state_cd, String stringBuilder, int off_cd) {
        areaActionSource.clear();
        areaActionTarget.clear();
        if (stringBuilder.isEmpty() || ("('')").equalsIgnoreCase(stringBuilder)) {
            Map<String, String> routeMap = passImp.getTargetAreaMap(state_cd, off_cd);
            for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                areaActionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
        } else {
            Map<String, String> routeMap = passImp.getSourcesAreaMapWithStrBuil(stringBuilder, state_cd, off_cd);
            for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                areaActionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
            Map<String, String> mapRouteList = passImp.getTargetAreaMapWithStrBuil(stringBuilder, state_cd, off_cd);
            for (Map.Entry<String, String> entry : mapRouteList.entrySet()) {
                areaActionTarget.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
        }
        areaManage = new DualListModel<PermitRouteList>(areaActionSource, areaActionTarget);
    }

    public void routeCodeDetail(String Appl_no, String TableName, String state_cd, int off_cd) {
        boolean flage = false;
        noOfTripsrendered = false;
        routeActionSource.clear();
        routeActionTarget.clear();
        Map<String, String> routeMap = passImp.getSourcesRouteMap(Appl_no, TableName, state_cd, off_cd, false);
        for (Map.Entry<String, String> entry : routeMap.entrySet()) {
            routeActionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
        }
        Map<String, String> mapRouteList = passImp.getTargetRouteMap(Appl_no, TableName, state_cd, off_cd, false);
        for (Map.Entry<String, String> entry : mapRouteList.entrySet()) {
            flage = true;
            routeActionTarget.add(new PermitRouteList(entry.getKey(), entry.getValue()));
        }
        setVia_route("");
        if (flage) {
            setVia_route(passImp.getRouteViaRouteList(Appl_no, state_cd, TableName, off_cd, false));
            noOfTripsrendered = true;
        }
        routeManage = new DualListModel<PermitRouteList>(routeActionSource, routeActionTarget);
    }

    public void onTransfer() {
        setVia_route(passImp.getRouteVia(routeManage.getTarget(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected()));
        if (getVia_route().isEmpty()) {
            noOfTripsrendered = false;
        } else {
            noOfTripsrendered = true;
        }
    }

    public List<ComparisonBean> compareChanges() {

        try {
            if (prv_pmt_dobj == null && prv_ownerDobj == null) {
                return getCompBeanList();
            }
            String msg = checkDetails();
            if (!CommonUtils.isNullOrBlank(msg)) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", msg);
                FacesContext.getCurrentInstance().addMessage("Id", message);
                return null;
            }

            compBeanList.clear();
            Compare("Owner Name", prv_ownerDobj.getOwner_name().toUpperCase(), ownerBean.getOwner_name().toUpperCase(), getCompBeanList());
            Compare("Father Name", prv_ownerDobj.getF_name().toUpperCase(), ownerBean.getF_name().toUpperCase(), (ArrayList) getCompBeanList());
            Compare("Vehicle Class", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getVh_class_array(), String.valueOf(prv_ownerDobj.getVh_class())), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getVh_class_array(), String.valueOf(ownerBean.getVh_class())), (ArrayList) getCompBeanList());
            Compare("Laden weight", String.valueOf(prv_ownerDobj.getLd_wt()), ownerBean.getLd_wt(), (ArrayList) getCompBeanList());
            Compare("UnLaden weight", String.valueOf(prv_ownerDobj.getUnld_wt()), ownerBean.getUnld_wt(), (ArrayList) getCompBeanList());
            if (String.valueOf(prv_ownerDobj.getMobile_no()) != null) {
                Compare("Mobile No.", String.valueOf(prv_ownerDobj.getMobile_no()), String.valueOf(ownerBean.getMobile_no()), (ArrayList) getCompBeanList());
            }
            if (prv_ownerDobj.getEmail_id() != null) {
                Compare("Email ID", prv_ownerDobj.getEmail_id(), ownerBean.getEmail_id(), (ArrayList) getCompBeanList());
            }
            if (prv_ownerDobj.getC_add1() != null) {
                Compare("Current House No. & Street Name", prv_ownerDobj.getC_add1(), ownerBean.getC_add1(), (ArrayList) getCompBeanList());
            }
            if (prv_ownerDobj.getC_add2() != null) {
                Compare("Current Village/Town/City ", prv_ownerDobj.getC_add2(), ownerBean.getC_add2(), (ArrayList) getCompBeanList());
            }
            if (prv_ownerDobj.getC_add3() != null) {
                Compare("Current Landmark/Police Station", prv_ownerDobj.getC_add3(), ownerBean.getC_add3(), (ArrayList) getCompBeanList());
            }

            Compare("Current State", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_c_state(), prv_ownerDobj.getC_state()), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_c_state(), ownerBean.getC_state()), (ArrayList) getCompBeanList());
            Compare("Current District", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_c_district(), String.valueOf(prv_ownerDobj.getC_district())), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_c_district(), String.valueOf(ownerBean.getC_district())), (ArrayList) getCompBeanList());

            if (String.valueOf(prv_ownerDobj.getC_pincode()) != null) {
                Compare("Current Pin Code", String.valueOf(prv_ownerDobj.getC_pincode()), String.valueOf(ownerBean.getC_pincode()), (ArrayList) getCompBeanList());
            }
            if (prv_ownerDobj.getP_add1() != null) {
                Compare("Permanent House No. & Street Name", prv_ownerDobj.getP_add1(), ownerBean.getP_add1(), (ArrayList) getCompBeanList());
            }
            if (prv_ownerDobj.getP_add2() != null) {
                Compare("Permanent Village/Town/City ", prv_ownerDobj.getP_add2(), ownerBean.getP_add2(), (ArrayList) getCompBeanList());
            }
            if (prv_ownerDobj.getP_add3() != null) {
                Compare("Permanent Landmark/Police Station", prv_ownerDobj.getP_add3(), ownerBean.getP_add3(), (ArrayList) getCompBeanList());
            }
            Compare("Permanent State", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_p_state(), prv_ownerDobj.getP_state()), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_p_state(), ownerBean.getP_state()), (ArrayList) getCompBeanList());
            Compare("Permanent District", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_p_district(), String.valueOf(prv_ownerDobj.getP_district())), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) ownerBean.getList_p_district(), String.valueOf(ownerBean.getP_district())), (ArrayList) getCompBeanList());
            if (String.valueOf(prv_ownerDobj.getP_pincode()) != null) {
                Compare("Permanent Pin Code", String.valueOf(prv_ownerDobj.getP_pincode()), String.valueOf(ownerBean.getP_pincode()), (ArrayList) getCompBeanList());
            }
            Compare("Permit Type", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) pmtTypeList, prv_pmt_dobj.getPmt_type()), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) pmtTypeList, pmt_dobj.getPmt_type()), (ArrayList) getCompBeanList());
            Compare("Permit Category", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) pmtCatgList, prv_pmt_dobj.getPmtCatg()), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) pmtCatgList, pmt_dobj.getPmtCatg()), (ArrayList) getCompBeanList());
            if (prv_pmt_dobj.getPeriod() != null) {
                Compare("Period", prv_pmt_dobj.getPeriod(), pmt_dobj.getPeriod(), (ArrayList) getCompBeanList());
            }
            Compare("Period Mode", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) period, prv_pmt_dobj.getPeriod_mode()), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) period, pmt_dobj.getPeriod_mode()), (ArrayList) getCompBeanList());
            // Compare("Number Of Trips", permit_detail_dobj_prv.getNumberOfTrips(), getNumber_OF_TRIPS().getValue().toString().toUpperCase(), (ArrayList) getCompBeanList());
            Compare("Services Type", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) serTypeList, prv_pmt_dobj.getServices_TYPE()), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) serTypeList, pmt_dobj.getServices_TYPE()), (ArrayList) getCompBeanList());
            if (prv_pmt_dobj.getGoods_TO_CARRY() != null) {
                Compare("Goods To Carry", prv_pmt_dobj.getGoods_TO_CARRY(), pmt_dobj.getGoods_TO_CARRY().toUpperCase(), (ArrayList) getCompBeanList());
            }
            if (prv_pmt_dobj.getJoreny_PURPOSE() != null) {
                Compare("Joreny Purpose", prv_pmt_dobj.getJoreny_PURPOSE(), pmt_dobj.getJoreny_PURPOSE().toUpperCase(), (ArrayList) getCompBeanList());
            }
            if (prv_pmt_dobj.getParking() != null) {
                Compare("Parking", prv_pmt_dobj.getParking(), pmt_dobj.getParking().toUpperCase(), (ArrayList) getCompBeanList());
            }
            if (areaActionTarget != null || areaManage.getTarget() != null) {
                Compare("Area Details", areaActionTarget, areaManage.getTarget(), (ArrayList) getCompBeanList());
            }
            if (routeActionTarget != null || routeManage.getTarget() != null) {
                Compare("Route Details", routeActionTarget, routeManage.getTarget(), (ArrayList) getCompBeanList());
            }
            if (prv_pmt_dobj.getReplaceDate() != null) {
                Compare("Date Of Replacement", prv_pmt_dobj.getReplaceDate(), pmt_dobj.getReplaceDate(), (ArrayList) getCompBeanList());
            }
        } catch (Exception e) {
            LOGGER.error("Appl No : " + prv_pmt_dobj.getApplNo() + "--" + e.getStackTrace()[0]);
        }

        PrimeFaces.current().executeScript("PF('compairVar').show();");
        PrimeFaces.current().ajax().update("compair");
        return getCompBeanList();
    }

    public String checkDetails() {
        String msg = "";
        if (pmt_dobj.getPmt_type().equalsIgnoreCase("-1") || CommonUtils.isNullOrBlank(pmt_dobj.getPmt_type())) {
            msg = "Please Select The permit Type";
        }
        if (pmt_dobj.getPeriod_mode().equalsIgnoreCase("-1") || CommonUtils.isNullOrBlank(pmt_dobj.getPeriod_mode())) {
            msg = "Please Select the Period Mode";
        }
        if (CommonUtils.isNullOrBlank(pmt_dobj.getPeriod())) {
            msg = "Please enter the Period";
        }
        if (ownerBean.getVh_class().equals("-1")) {
            msg = "Please select any Vehicle Class";
        }
        if (CommonUtils.isNullOrBlank(String.valueOf(ownerDobj.getMobile_no()))) {
            msg = "Please enter the Mobile Number";
        }
        if (CommonUtils.isNullOrBlank(ownerDobj.getVch_catg())) {
            msg = "Please select vehicle category";
        }
        return msg;
    }

    public String savePmtDetails() {
        FacesMessage message = null;
        PermitOwnerDetailDobj owner_dobj = null;
        String returnUrl = "";
        String region = "";
        boolean update;
        String appl_no = getAppl_no().toUpperCase();
        try {
            List area = areaManage.getTarget();
            List route_code = routeManage.getTarget();
            for (Object s : area) {
                region = region + s + ",";
            }
            pmt_dobj.setRegion_covered(region);
            pmt_dobj.setAppDisboolenValue("A");
            pmt_dobj.setPmt_type_code(pmt_dobj.getPmt_type());
            owner_dobj = new PermitOwnerDetailDobj();
            owner_dobj = ownerBean.setValueinDOBJ();
            update = passImp.saveOfferDetilsInVaTables(appl_no, owner_dobj, prv_ownerDobj, pmt_dobj, prv_pmt_dobj, route_code, ComparisonBeanImpl.changedDataContents(compareChanges()), false, null);
            if (update) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Successfully Save your deails");
                PrimeFaces.current().executeScript("PF('compairVar').hide();");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                returnUrl = "/ui/permit/permitOfferAdminForm.xhtml?faces-redirect=true";
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Data is not Save");
                PrimeFaces.current().executeScript("PF('compairVar').hide();");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            }

        } catch (VahanException e) {
            PrimeFaces.current().executeScript("PF('compairVar').hide();");
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return returnUrl;
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

//    public PermitAdministratorDobj getPrvAdminDobj() {
//        return prvAdminDobj;
//    }
//
//    public void setPrvAdminDobj(PermitAdministratorDobj prvAdminDobj) {
//        this.prvAdminDobj = prvAdminDobj;
//    }
//    public List<ComparisonBean> getCompairValue() {
//        return compairValue;
//    }
//
//    public void setCompairValue(List<ComparisonBean> compairValue) {
//        this.compairValue = compairValue;
//    }
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

    public PassengerPermitDetailDobj getPmt_dobj() {
        return pmt_dobj;
    }

    public void setPmt_dobj(PassengerPermitDetailDobj pmt_dobj) {
        this.pmt_dobj = pmt_dobj;
    }

    public List getPeriod() {
        return period;
    }

    public void setPeriod(List period) {
        this.period = period;
    }

    public PassengerPermitDetailDobj getPrv_pmt_dobj() {
        return prv_pmt_dobj;
    }

    public void setPrv_pmt_dobj(PassengerPermitDetailDobj prv_pmt_dobj) {
        this.prv_pmt_dobj = prv_pmt_dobj;
    }

    public PermitOwnerDetailDobj getPrv_ownerDobj() {
        return prv_ownerDobj;
    }

    public void setPrv_ownerDobj(PermitOwnerDetailDobj prv_ownerDobj) {
        this.prv_ownerDobj = prv_ownerDobj;
    }

    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    public String getVahanMessages() {
        return vahanMessages;
    }

    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public boolean isRenderSaveBtn() {
        return renderSaveBtn;
    }

    public void setRenderSaveBtn(boolean renderSaveBtn) {
        this.renderSaveBtn = renderSaveBtn;
    }
}
