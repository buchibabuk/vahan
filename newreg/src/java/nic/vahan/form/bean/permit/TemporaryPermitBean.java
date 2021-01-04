/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.facelets.FaceletContext;
import nic.java.util.DateUtils;
import nic.java.util.DateUtilsException;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.eapplication.EApplicationDobj;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.PaymentCollectionBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import static nic.vahan.form.bean.permit.TemporaryPermitBean.ONE_DAY_MILLIS;
import static nic.vahan.form.bean.permit.TemporaryPermitBean.TEMP_PMT_COUNT;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.dobj.permit.PreviousTempPermitDtlsDobj;
import nic.vahan.form.dobj.permit.PreviousTempPermitDtlsList;
import nic.vahan.form.dobj.permit.SpecialRoutePermitDobj;
import nic.vahan.form.dobj.permit.TemporaryPermitDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.FeeDraftimpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.permit.PermitDetailImpl;
import nic.vahan.form.impl.permit.TemporaryPermitImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "tmp_pmt")
@ViewScoped
public class TemporaryPermitBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(TemporaryPermitBean.class);
    private String regn_no;
    private String pmt_no;
    private PaymentCollectionBean paymentBean = new PaymentCollectionBean();
    private Date valid_from;
    public static final long ONE_DAY_MILLIS = Long.valueOf(86400 * 1000);
    private Date currentDate = new Date();
    private Date dateRange = new Date(currentDate.getTime() + (ONE_DAY_MILLIS * 31));
    private final static String DT_DATEFORMAT = "dd-MMM-yyyy";
    public final static int TEMP_PMT_COUNT = 3;
    private String minDate;
    private String maxDate;
    private String totalAmt;
    private String app_no_msg;
    private String header;
    private boolean route_status;
    private boolean renew_temp;
    private boolean temp_pmt_type;
    private boolean spl_pmt_route;
    private boolean temp_state_as_region;
    private List stateList = new ArrayList();
    private String pmt_type;
    private String pmt_catg = "";
    private String callPageRedirect;
    private List period_bean = new ArrayList();
    private boolean period_disable = true;
    private boolean disable_pmt_type = false;
    private boolean renderedTempPmtDlls = false;
    private boolean render_app_disapp = false;
    private boolean visible_Route = false;
    private boolean visible_area = false;
    private boolean disable_region = false;
    private boolean render_Temp_Permit_Fee = false;
    private boolean render_Temp_Permit_type = false;
    private boolean render_save_Details = false;
    private boolean render_vehicle = true;
    private List pmt_type_list = new ArrayList();
    private List pmtCategory_list = new ArrayList();
    private List<PermitRouteList> actionSource = new ArrayList<>();
    private List<PermitRouteList> actionTarget = new ArrayList<>();
    private List<PermitRouteList> areaActionSource = new ArrayList<>();
    private List<PermitRouteList> areaActionTarget = new ArrayList<>();
    private DualListModel<PermitRouteList> routeManage;
    private DualListModel<PermitRouteList> areaManage;
    private String via_route = "";
    TemporaryPermitImpl temp_impl = null;
    TemporaryPermitDobj temp_dobj = null;
    TemporaryPermitDobj prv_temp_dobj = null;
    @ManagedProperty(value = "#{Permit_Dtls_bean}")
    PermitDetailBean pmt_Bean = null;
    PermitDetailDobj pmt_dobj = null;
    private Owner_dobj ownerDobj;
    PermitOwnerDetailDobj owner_dobj = null;
    private OwnerImpl ownerImpl = null;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    FeeDraftimpl feeDraftImpl = null;
    String appl_no = "";
    private String prv_appl_no = "";
    private String masterLayout = "/masterLayoutPage.xhtml";
    // Nitin KUmar 21-01-2016 Begin 
    private List<OwnerDetailsDobj> dupRegnList = new ArrayList<>();
    private OwnerDetailsDobj ownerDetail;
    // Nitin KUmar 21-01-2016 End
    private List<PreviousTempPermitDtlsList> priviousTempDataTable = null;
    private boolean priviousTempDataShow = false;
    private int purCode = 0;
    Map<String, String> confige = null;
    Map<String, String> tempConfige = null;
    Map<String, String> splConfige = null;
    @ManagedProperty(value = "#{pmtDtlsBean}")
    private PermitCheckDetailsBean pmtCheckDtsl;
    private List arrayInsCmpy = new ArrayList();
    private List arrayInsType = new ArrayList();
    @ManagedProperty(value = "#{splRouteBean}")
    private SpecialRoutePermitBean spl_route_Bean;
    private boolean render_spl_route = false;
    private boolean render_spl_passenger = false;
    private ArrayList<SpecialRoutePermitDobj> prv_spl_route = null;
    private ArrayList<SpecialRoutePermitDobj> spl_route_list = null;
    private List<String> selectedStates;
    private String selectedStatesNames;
    private String prvSelectedStatesNames;
    @ManagedProperty(value = "#{splPassengerBean}")
    private SpecialPassengerDetailBean spl_passenger_Bean;
    @ManagedProperty(value = "#{splRouteDtlsDesc}")
    private SpecialRouteDtlsDescBean splRouteDtlsDesc;
    private boolean render_spl_route_desc = false;
    PassengerPermitDetailDobj Pass_dobj = null;
    private boolean parmanemt_pmt_valid = false;
    private boolean renderRlengthServicePanal = false;
    private List ser_type = new ArrayList();
    PassengerPermitDetailImpl passImp = null;
    private boolean spl_vh_class = false;
    private boolean renderPeriodAtVerify = false;
    boolean renderGoodsPassengerTax = false;
    private boolean spl_route_area = false;
    private boolean renderDocUploadTab = false;
    private String dmsUrl = "";

    public TemporaryPermitBean() {
        super();
        try {
            confige = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
            tempConfige = CommonPermitPrintImpl.getTmTempPmtStateConfiguration(Util.getUserStateCode());
            splConfige = CommonPermitPrintImpl.getTmSpecialPmtStateConfiguration(Util.getUserStateCode());
            temp_pmt_type = Boolean.parseBoolean(tempConfige.get("temp_pmt_type").toString());
            route_status = Boolean.parseBoolean(tempConfige.get("temp_route_area").toString());
            spl_route_area = Boolean.parseBoolean(splConfige.get("spl_route_area").toString());
            render_spl_passenger = false;
            renderGoodsPassengerTax = Boolean.parseBoolean(confige.get("render_goods_passanger_tax"));
            spl_vh_class = Boolean.parseBoolean(confige.get("allow_special_vh_class"));
            String renew_temp_pmt = tempConfige.get("temp_renew_pmt").toString();
            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            if (isCondition(FormulaUtils.replaceTagPermitValues(renew_temp_pmt, parameters), "TemporaryPermitBean")) {
                setRenew_temp(true);
            }
            if (isCondition(FormulaUtils.replaceTagPermitValues(tempConfige.get("permanent_permit_valid").toString(), parameters), "TemporaryPermitBean()")) {
                setParmanemt_pmt_valid(true);
            }
            parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            parameters.setPUR_CD(appl_details.getPur_cd());
            if (isCondition(FormulaUtils.replaceTagPermitValues(confige.get("show_uploaded_document"), parameters), "TemporaryPermitBean()")) {
                renderDocUploadTab = true;
            } else {
                renderDocUploadTab = false;
            }
            spl_pmt_route = Boolean.parseBoolean(splConfige.get("spl_pmt_route").toString());

            String[][] data = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
            stateList = (List) ((ArrayList) MasterTableFiller.stateList).clone();

            for (int i = 0; i < data.length; i++) {
                if (("D").equalsIgnoreCase(data[i][0]) || ("W").equalsIgnoreCase(data[i][0]) || ("M").equalsIgnoreCase(data[i][0])) {
                    period_bean.add(new SelectItem(data[i][0], data[i][1]));
                }
            }

            data = MasterTableFiller.masterTables.vm_service_type.getData();
            for (int i = 0; i < data.length; i++) {
                ser_type.add(new SelectItem(data[i][0], data[i][1]));
            }

            data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
            for (int i = 0; i < data.length; i++) {
                pmt_type_list.add(new SelectItem(data[i][0], data[i][1]));
            }
            data = MasterTableFiller.masterTables.VM_ICCODE.getData();
            for (int i = 0; i < data.length; i++) {
                arrayInsCmpy.add(new SelectItem(data[i][0], data[i][1]));
            }
            data = MasterTableFiller.masterTables.VM_INSTYP.getData();
            for (int i = 0; i < data.length; i++) {
                arrayInsType.add(new SelectItem(data[i][0], data[i][1]));
            }
            routeManage = new DualListModel<>(actionSource, actionTarget);
            areaManage = new DualListModel<>(areaActionSource, areaActionTarget);
            if (String.valueOf(appl_details.getCurrent_action_cd()) == null) {
                if (getAppl_details() == null) {
                    if (String.valueOf(appl_details.getCurrent_action_cd()) == null) {
                        masterLayout = "/ui/eapplication/masterLayoutEapplication.xhtml";
                        render_Temp_Permit_Fee = true;
                        renderPeriodAtVerify = true;
                        render_save_Details = false;
                        render_vehicle = false;
                        temp_dobj = new TemporaryPermitDobj();
                        header = "Temporary Permit";
                        render = true;
                        FaceletContext faceletContext = (FaceletContext) FacesContext.getCurrentInstance().getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
                        EApplicationDobj eAppObj = (EApplicationDobj) faceletContext.getAttribute("eApp");
                        setRegn_no(eAppObj.getRegn_no());
                        temp_dobj.setPur_cd(eAppObj.getPur_cd());
                        SimpleDateFormat sdf = new SimpleDateFormat(DT_DATEFORMAT);
                        minDate = sdf.format(currentDate);
                        maxDate = sdf.format(dateRange);
                        return;
                    }
                }
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat(DT_DATEFORMAT);
                minDate = sdf.format(currentDate);
                maxDate = sdf.format(dateRange);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    @PostConstruct
    public void init() {
        try {
            if (String.valueOf(appl_details.getCurrent_action_cd()) == null) {
                if (getAppl_details() == null) {
                    render_save_Details = false;
                    render_vehicle = false;
                    int pur_cd = temp_dobj.getPur_cd();
                    if (pur_cd == TableConstants.VM_PMT_TEMP_PUR_CD) {
                        header = "Temporary Permit";
                    } else if (pur_cd == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                        header = "Special Permit";
                    }
                    render = true;
                }
            } else {

                render_save_Details = true;
                temp_dobj = new TemporaryPermitDobj();
                temp_impl = new TemporaryPermitImpl();
                String action_cd = String.valueOf(appl_details.getCurrent_action_cd());
                if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_APPL))) {
                    header = "Special Permit Entry";
                    purCode = TableConstants.VM_PMT_SPECIAL_PUR_CD;
                } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_VERIFICATION))) {
                    header = "Special Permit VERIFICATION";
                    purCode = TableConstants.VM_PMT_SPECIAL_PUR_CD;
                } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_APPROVAL))) {
                    header = "Special Permit APPROVAL";
                    purCode = TableConstants.VM_PMT_SPECIAL_PUR_CD;
                } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_APPL))) {
                    header = "Temporary Permit Entry";
                    purCode = TableConstants.VM_PMT_TEMP_PUR_CD;
                } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_VERIFICATION))) {
                    header = "Temporary Permit VERIFICATION";
                    purCode = TableConstants.VM_PMT_TEMP_PUR_CD;
                } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_RENEW_TEMP_VERIFICATION))) {
                    header = "Renew Temporary Permit VERIFICATION";
                    purCode = TableConstants.VM_PMT_RENEW_TEMP_PUR_CD;
                } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_APPROVAL))) {
                    header = "Temporary Permit APPROVAL";
                    purCode = TableConstants.VM_PMT_TEMP_PUR_CD;
                } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_RENEW_TEMP_APPROVAL))) {
                    header = "Renew Temporary Permit APPROVAL";
                    purCode = TableConstants.VM_PMT_RENEW_TEMP_PUR_CD;
                } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_APPL))
                        && !CommonUtils.isNullOrBlank(appl_details.getAppl_no())) {
                    header = "Temporary Permit Re-Entry";
                    purCode = TableConstants.VM_PMT_TEMP_PUR_CD;
                }

                if ((action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_APPL))
                        || action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_APPL)))
                        && !CommonUtils.isNullOrBlank(appl_details.getAppl_no())) {
                    render_vehicle = false;
                    render_save_Details = false;
                    render_app_disapp = true;
                    renderedTempPmtDlls = true;
                    appl_no = appl_details.getAppl_no();
                    String regn_no = appl_details.getRegn_no();
                    try {
                        setTemp_dobj(temp_impl.getVa_Permit_Details(appl_no));
                        get_route_detail();
                        prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                        prv_temp_dobj = (TemporaryPermitDobj) temp_dobj.clone();
                        setRegn_no(regn_no);
                        ownerDobj = appl_details.getOwnerDobj();
                        render_Temp_Permit_Fee = true;
                        renderPeriodAtVerify = true;
                    } catch (CloneNotSupportedException e) {
                        LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    }
                }

                if ((action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_VERIFICATION)))
                        || (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_APPROVAL)))
                        || (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_RENEW_TEMP_VERIFICATION)))
                        || (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_RENEW_TEMP_APPROVAL)))
                        || (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_VERIFICATION)))
                        || (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_APPROVAL)))) {

                    if (appl_details.getOwnerDobj() == null) {
                        owner_dobj = new PermitOwnerDetailDobj();
                        owner_dobj = new PermitOwnerDetailImpl().set_VA_Owner_permit_to_dobj(null, appl_details.getRegn_no());
                        ownerDobj = new PermitOwnerDetailImpl().getPermitOwnerData(owner_dobj);
                    } else {
                        ownerDobj = appl_details.getOwnerDobj();
                    }

                    priviousTempDataTable = temp_impl.getPreviouseTempDtls(priviousTempDataTable, appl_details.getRegn_no(), purCode);
                    if (priviousTempDataTable != null && priviousTempDataTable.size() > 0) {
                        priviousTempDataShow = true;
                    }

                    if (purCode == TableConstants.VM_PMT_SPECIAL_PUR_CD && Util.getUserStateCode().equalsIgnoreCase("MH")) {
                        splRouteDtlsDesc.verifiedList(temp_impl.getVaSplRouteDetail(appl_details.getAppl_no()), Util.getUserStateCode());
                        if (splRouteDtlsDesc.getSplRouteDobjList().size() > 0) {
                            splRouteDtlsDesc.setDisableMenuCheck(true);
                            setRender_spl_route_desc(true);
                        }
                    }
                    if (spl_pmt_route && ((action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_VERIFICATION))) || (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_APPROVAL))))) {
                        setRender_spl_route(true);
                        spl_route_list = new ArrayList<>();
                        spl_route_Bean.setRenderfield(false);
                        spl_route_Bean.setDisableTable(true);
                        spl_route_list = temp_impl.getVaSplRouteDetail(appl_details.getAppl_no());
                        spl_route_Bean.setSpecialRouteList(spl_route_list);
                        spl_route_Bean.setCheckPassengerList(spl_route_list.get(0).isPsnger_list());
                        if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_APPROVAL))) {
                            spl_route_Bean.setDisableTable(false);
                            spl_route_Bean.setDisablePassanger(true);
                        }
                        try {
                            prv_spl_route = new ArrayList<>();
                            for (SpecialRoutePermitDobj r : spl_route_list) {
                                prv_spl_route.add((SpecialRoutePermitDobj) r.clone());
                            }
                        } catch (CloneNotSupportedException e) {
                            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                        }
                    }
                    render_vehicle = false;
                    render_save_Details = false;
                    render_app_disapp = true;
                    render_Temp_Permit_Fee = true;
                    renderedTempPmtDlls = true;
                    appl_no = appl_details.getAppl_no();
                    String regn_no = appl_details.getRegn_no();
                    try {
                        setTemp_dobj(temp_impl.getVa_Permit_Details(appl_no));
                        get_route_detail();
                        prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                        prv_temp_dobj = (TemporaryPermitDobj) temp_dobj.clone();
                        setRegn_no(regn_no);
                        // render_Temp_Permit_Fee = false;
                        renderPeriodAtVerify = false;
                        pmtCheckDtsl.getAlldetails(ownerDobj.getRegn_no(), null, ownerDobj.getState_cd(), ownerDobj.getOff_cd());
                    } catch (CloneNotSupportedException e) {
                        LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public void get_route_detail() {
        if (!tempConfige.isEmpty()) {
            temp_state_as_region = purCode == TableConstants.VM_PMT_TEMP_PUR_CD ? Boolean.parseBoolean(tempConfige.get("temp_state_as_region").toString()) : false;
            if (purCode == TableConstants.VM_PMT_TEMP_PUR_CD && (route_status || temp_state_as_region)) {
                routeCodeDetail(appl_details.getAppl_no(), TableList.VA_TEMP_PERMIT_ROUTE, Util.getUserStateCode(), Util.getUserOffCode());
                String str = temp_impl.getRegion_Area(appl_details.getAppl_no(), TableList.VA_TEMP_PERMIT_ROUTE);
                if (!CommonUtils.isNullOrBlank(str)) {
                    String[] temp = str.split(",");
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < temp.length; i++) {
                        stringBuilder.append("(").append("'" + temp[i] + "'").append("),");
                    }
                    if (temp.length > 0 && JSFUtils.isNumeric(String.valueOf(temp[0].charAt(0)))) {
                        areaCodeDetail(Util.getUserStateCode(), stringBuilder.substring(0, stringBuilder.length() - 1), Util.getUserOffCode());
                        if (renew_temp) {
                            setDisable_region(true);
                        }
                        visible_Route = true;
                        visible_area = true;
                    } else if (temp.length > 0 && JSFUtils.isAlphabet(String.valueOf(temp[0].charAt(0)))) {
                        selectedStates = new ArrayList<String>(Arrays.asList(temp));
                        onSelectState();
                        prvSelectedStatesNames = selectedStatesNames;
                        if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_TEMP_APPROVAL) {
                            setDisable_region(true);
                        }
                    }
                }
            } else {
                visible_Route = false;
                visible_area = false;
            }

            if (purCode == TableConstants.VM_PMT_SPECIAL_PUR_CD && spl_route_area) {
                routeCodeDetail(appl_details.getAppl_no(), TableList.VA_TEMP_PERMIT_ROUTE, Util.getUserStateCode(), Util.getUserOffCode());
                String str = temp_impl.getRegion_Area(appl_details.getAppl_no(), TableList.VA_TEMP_PERMIT_ROUTE);
                String[] temp = str.split(",");
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < temp.length; i++) {
                    stringBuilder.append("(").append("'" + temp[i] + "'").append("),");
                }
                if (temp.length > 0 && JSFUtils.isNumeric(String.valueOf(temp[0].charAt(0)))) {
                    areaCodeDetail(Util.getUserStateCode(), stringBuilder.substring(0, stringBuilder.length() - 1), Util.getUserOffCode());

                    visible_Route = true;
                    visible_area = true;
                } else if (temp.length > 0 && JSFUtils.isAlphabet(String.valueOf(temp[0].charAt(0)))) {
                    selectedStates = new ArrayList<String>(Arrays.asList(temp));
                    onSelectState();
                    prvSelectedStatesNames = selectedStatesNames;
                    if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_SPECIAL_APPROVAL) {
                        setDisable_region(true);
                    }
                }
            }

            if (temp_pmt_type) {
                setPmt_type(getTemp_dobj().getPmt_type());
                pmtCategory_list.clear();
                String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
                for (int j = 0; j < data.length; j++) {
                    if (data[j][0].equalsIgnoreCase(Util.getUserStateCode())
                            && Integer.parseInt(data[j][3]) == Integer.parseInt(getTemp_dobj().getPmt_type())) {
                        pmtCategory_list.add(new SelectItem(data[j][1], data[j][2]));
                    }
                }
                setPmt_catg(getTemp_dobj().getPmt_catg());
                render_Temp_Permit_type = true;
                if (pmtCategory_list.size() > 0 && getPmt_catg().equalsIgnoreCase("-1")) {
                    disable_pmt_type = false;
                } else {
                    disable_pmt_type = true;
                }
            } else {
                render_Temp_Permit_type = false;
                disable_pmt_type = false;
            }
        }
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

    public void onSelectPermitType() {
        pmtCategory_list.clear();
        String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
        for (int j = 0; j < data.length; j++) {
            if (data[j][0].equalsIgnoreCase(Util.getUserStateCode())
                    && Integer.parseInt(data[j][3]) == Integer.parseInt(getPmt_type())) {
                pmtCategory_list.add(new SelectItem(data[j][1], data[j][2]));
            }
        }
        if (purCode == TableConstants.VM_PMT_TEMP_PUR_CD && Util.getUserStateCode().equalsIgnoreCase("KL")) {
            if (Integer.parseInt(getPmt_type()) == TableConstants.STAGE_CARRIAGE_PERMIT) {
                renderRlengthServicePanal = true;
            } else {
                renderRlengthServicePanal = false;
            }
        }
    }

    private void resetOnGetDetail() {
        ownerDetail = null;
        valid_from = null;
        pmt_dobj = new PermitDetailDobj();
        ownerDobj = new Owner_dobj();
        temp_impl = new TemporaryPermitImpl();
        priviousTempDataTable = null;
        priviousTempDataShow = false;
        renderRlengthServicePanal = false;
        temp_dobj.setPeriod_mode("-1");
        temp_dobj.setPeriod_in_no("");
        renderedTempPmtDlls = false;
        period_disable = true;
    }

    public void get_Details_On_Blur() {

        resetOnGetDetail();
        FacesMessage message = null;
        InsDobj insDobj = null;
        String special_pmt_type = "";
        TemporaryPermitDobj dobj = new TemporaryPermitDobj();
        try {            
            //nitin kumar - 21-01-2015 BEGIN 
            OwnerImpl owner_Impl = new OwnerImpl();
            List<OwnerDetailsDobj> ownerDetailsDobjList = owner_Impl.getOwnerDetailsList(getRegn_no().toUpperCase().trim(), null);
            ownerDetailsDobjList = owner_Impl.getStateWiseOwnerList(ownerDetailsDobjList, Util.getUserStateCode());
            if (ownerDetailsDobjList != null && ownerDetailsDobjList.size() == 1) {
                ownerDetail = ownerDetailsDobjList.get(0);
                renderPeriodAtVerify = true;
            } else if (ownerDetailsDobjList != null && ownerDetailsDobjList.size() >= 2) {
                dupRegnList = ownerDetailsDobjList;
                PrimeFaces.current().executeScript("PF('varDupRegNo').show()");
                return;
            }
            if (ownerDetail == null) {
                ownerDobj = null;
                pmt_dobj = null;
                priviousTempDataShow = false;
                render_Temp_Permit_Fee = false;
                renderPeriodAtVerify = false;
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number Not Found in Currnet Office"));
                return;
            }
            if (purCode == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                renderRlengthServicePanal = Boolean.parseBoolean(splConfige.get("spl_tax_on_rlength").toString());
            } else {
                renderRlengthServicePanal = Boolean.parseBoolean(tempConfige.get("temp_tax_on_rlength").toString());
            }

            if (spl_pmt_route && purCode == TableConstants.VM_PMT_SPECIAL_PUR_CD && !(TableConstants.VH_CLASS_ALLOWED_SPL_PMT_OR.contains("," + String.valueOf(ownerDetail.getVh_class()) + ","))) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "This Vehicle is not Allow for Special Permit"));
                return;
            }

            //nitin kumar  - 21-01-2015 - END
            ownerImpl = new OwnerImpl();
            if (getRegn_no() != null) {
                ownerDobj = ownerImpl.getOwnerDobj(ownerDetail);//set_Owner_appl_db_to_dobj(getRegn_no().toUpperCase(), null, null, 2);
                pmt_dobj = PermitDetailImpl.getPermitdetails(CommonPermitPrintImpl.getPmtNoThroughVtPermit(getRegn_no().toUpperCase()));
                pmtCheckDtsl.getAlldetails(ownerDobj.getRegn_no(), insDobj, ownerDobj.getState_cd(), ownerDobj.getOff_cd());
                priviousTempDataTable = temp_impl.getPreviouseTempDtls(priviousTempDataTable, getRegn_no().toUpperCase(), purCode);
                Pass_dobj = new PassengerPermitDetailDobj();
                if (pmt_dobj != null) {
                    Pass_dobj.setPmt_type_code(String.valueOf(pmt_dobj.getPmt_type()));
                    Pass_dobj.setPmtCatg(String.valueOf(pmt_dobj.getPmt_catg()));
                    Pass_dobj.setServices_TYPE(String.valueOf(pmt_dobj.getService_type()));
                }
                VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(ownerDobj, Pass_dobj, 0, 0, 0, 0, 0, 0, 0, 0);
                parameters.setPUR_CD(purCode);
                if (purCode == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                    String periodMode = FormulaUtils.getSqlFormulaValue(FormulaUtils.replaceTagPermitValues(splConfige.get("spl_period_mode"), parameters), "spl_period_mode_get_Details_On_Blur");
                    for (int i = 0; i < period_bean.size(); i++) {
                        if (!periodMode.contains(((SelectItem) period_bean.get(i)).getValue().toString())) {
                            period_bean.remove(i);
                        }
                    }
                } else {
                    String periodMode = FormulaUtils.getSqlFormulaValue(FormulaUtils.replaceTagPermitValues(tempConfige.get("temp_period_mode"), parameters), "temp_period_mode_get_Details_On_Blur");
                    for (int i = 0; i < period_bean.size(); i++) {
                        if (!periodMode.contains(((SelectItem) period_bean.get(i)).getValue().toString())) {
                            period_bean.remove(i);
                        }
                    }
                }
                if (isCondition(FormulaUtils.replaceTagPermitValues(splConfige.get("route_allow_for_special_tax"), parameters), "get_Details_On_Blur")) {
                    render_spl_route_desc = true;
                }
                if (priviousTempDataTable != null) {
                    priviousTempDataShow = true;
                    if (renew_temp) {
                        dobj = temp_impl.getTempPermitDetials(getRegn_no().toUpperCase());
                        if (purCode == TableConstants.VM_PMT_TEMP_PUR_CD || purCode == TableConstants.VM_PMT_RENEW_TEMP_PUR_CD) {
                            Date current_dt = new Date();
                            if (priviousTempDataTable.size() > 0) {
                                temp_dobj.setGoods_to_carry(dobj.getGoods_to_carry());
                                temp_dobj.setPurpose(dobj.getPurpose());
                                temp_dobj.setRoute_fr(dobj.getRoute_fr());
                                temp_dobj.setRoute_to(dobj.getRoute_to());
                                temp_dobj.setVia(dobj.getVia());
                                String valid_upto = priviousTempDataTable.get(0).getDtlsList().get(0).getValid_upto();
                                setPmt_no(priviousTempDataTable.get(0).getDtlsList().get(0).getPmt_no());
                                setPrv_appl_no(priviousTempDataTable.get(0).getDtlsList().get(0).getAppl_no());
                                int permit_count = temp_impl.getCountTempPmtNo(getRegn_no().toUpperCase(), getPmt_no());
                                if (permit_count == TEMP_PMT_COUNT && Util.getUserStateCode().equalsIgnoreCase("AS")) {
                                    priviousTempDataShow = false;
                                    message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "Already taken three times Temporary permit");
                                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                                    return;
                                }
                                Date date = CommonPermitPrintImpl.getDateDD_MMM_YYYY(valid_upto);
                                if (date.after(current_dt)) {
                                    message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "Previous Temporary permit is not expired");
                                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                                    return;
                                }
                                if (tempConfige.get("temp_pmt_type").equalsIgnoreCase("true")) {
                                    render_Temp_Permit_type = true;
                                    String trans_catg = "";
                                    pmt_type_list.clear();
                                    String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
                                    for (int i = 0; i < data.length; i++) {
                                        if ((String.valueOf(ownerDobj.getVh_class())).equalsIgnoreCase(data[i][0])) {
                                            trans_catg = data[i][3];
                                        }
                                    }
                                    if (spl_vh_class && trans_catg.equalsIgnoreCase("S")) {
                                        special_pmt_type = new PassengerPermitDetailImpl().getPmtTypeForSplVhClass(TableConstants.VM_PMT_TEMP_PUR_CD, Integer.valueOf(ownerDobj.getVh_class()));
                                    }

                                    data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
                                    for (int i = 0; i < data.length; i++) {

                                        if (spl_vh_class && trans_catg.equalsIgnoreCase("S")) {
                                            if (special_pmt_type.contains(data[i][0])) {
                                                pmt_type_list.add(new SelectItem(data[i][0], data[i][1]));
                                            }
                                        } else if (data[i][2].equalsIgnoreCase(trans_catg)) {
                                            pmt_type_list.add(new SelectItem(data[i][0], data[i][1]));
                                        }
                                    }
                                    setPmt_type(priviousTempDataTable.get(0).getDtlsList().get(0).getPmt_type());
                                    pmtCategory_list.clear();
                                    data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
                                    for (int j = 0; j < data.length; j++) {
                                        if (data[j][0].equalsIgnoreCase(Util.getUserStateCode())
                                                && Integer.parseInt(data[j][3]) == Integer.parseInt(priviousTempDataTable.get(0).getDtlsList().get(0).getPmt_type())) {
                                            pmtCategory_list.add(new SelectItem(data[j][1], data[j][2]));
                                        }
                                    }
                                    setPmt_catg(priviousTempDataTable.get(0).getDtlsList().get(0).getPmt_catg());
                                    if (getPmt_type().equalsIgnoreCase("") || getPmt_catg().equalsIgnoreCase("-1")) {
                                        disable_pmt_type = false;
                                    } else {
                                        disable_pmt_type = true;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    priviousTempDataShow = false;
                }
                if (ownerDobj != null) {
                    Map<String, String> OffAllotResult = PermitDetailImpl.getOffAllotmentResult(Util.getUserStateCode(), Util.getUserOffCode(), ownerDobj.getOff_cd());
                    if (Util.getUserStateCode().equalsIgnoreCase(ownerDobj.getState_cd()) && "true".equalsIgnoreCase(OffAllotResult.get("offAllowed"))) {
                        temp_state_as_region = purCode == TableConstants.VM_PMT_TEMP_PUR_CD ? Boolean.parseBoolean(tempConfige.get("temp_state_as_region").toString()) : false;
                        if (!tempConfige.isEmpty()) {
                            if (purCode == TableConstants.VM_PMT_TEMP_PUR_CD && route_status) {
                                routeCodeDetail("", TableList.VA_TEMP_PERMIT_ROUTE, Util.getUserStateCode(), Util.getUserOffCode());
                                areaCodeDetail(Util.getUserStateCode(), "", Util.getUserOffCode());
                                if (renew_temp) {
                                    routeCodeDetail(getPrv_appl_no(), TableList.VT_TEMP_PERMIT_ROUTE, Util.getUserStateCode(), Util.getUserOffCode());
                                    String str = temp_impl.getRegion_Area(getPrv_appl_no(), TableList.VT_TEMP_PERMIT_ROUTE);
                                    String[] temp = str.split(",");
                                    StringBuilder stringBuilder = new StringBuilder();
                                    for (int i = 0; i < temp.length; i++) {
                                        stringBuilder.append("(").append("'" + temp[i] + "'").append("),");
                                    }
                                    areaCodeDetail(Util.getUserStateCode(), stringBuilder.substring(0, stringBuilder.length() - 1), Util.getUserOffCode());
                                    if (!areaActionTarget.isEmpty() || !actionTarget.isEmpty()) {
                                        disable_region = true;
                                    }
                                }
                                visible_Route = true;
                                visible_area = true;
                            } else {
                                visible_Route = false;
                                visible_area = false;
                            }

                            if (purCode == TableConstants.VM_PMT_TEMP_PUR_CD && !renew_temp && Util.getUserStateCode().equalsIgnoreCase("AS")) {
                                priviousTempDataTable = null;
                            }
                            if (temp_pmt_type && priviousTempDataTable == null) {
                                render_Temp_Permit_type = true;
                                String trans_catg = "";
                                pmt_type_list.clear();
                                String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
                                for (int i = 0; i < data.length; i++) {
                                    if ((String.valueOf(ownerDobj.getVh_class())).equalsIgnoreCase(data[i][0])) {
                                        trans_catg = data[i][3];
                                    }
                                }

                                if (spl_vh_class && trans_catg.equalsIgnoreCase("S")) {
                                    special_pmt_type = new PassengerPermitDetailImpl().getPmtTypeForSplVhClass(TableConstants.VM_PMT_TEMP_PUR_CD, Integer.valueOf(ownerDobj.getVh_class()));
                                }

                                data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
                                for (int i = 0; i < data.length; i++) {

                                    if (spl_vh_class && trans_catg.equalsIgnoreCase("S")) {
                                        if (special_pmt_type.contains(data[i][0])) {
                                            pmt_type_list.add(new SelectItem(data[i][0], data[i][1]));
                                        }
                                    } else if (data[i][2].equalsIgnoreCase(trans_catg)) {
                                        pmt_type_list.add(new SelectItem(data[i][0], data[i][1]));
                                    }
                                }
                                pmtCategory_list.clear();
                                data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
                                for (int j = 0; j < data.length; j++) {
                                    if (data[j][0].equalsIgnoreCase(Util.getUserStateCode())) {
                                        pmtCategory_list.add(new SelectItem(data[j][1], data[j][2]));
                                    }
                                }
                                setPmt_catg("");
                                setPmt_type("");

                            }
                            if (temp_pmt_type && pmt_dobj != null) {
                                render_Temp_Permit_type = true;
                                pmtCategory_list.clear();
                                String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
                                for (int j = 0; j < data.length; j++) {
                                    if (data[j][0].equalsIgnoreCase(Util.getUserStateCode()) && Integer.parseInt(data[j][3]) == pmt_dobj.getPmt_type()) {
                                        pmtCategory_list.add(new SelectItem(data[j][1], data[j][2]));
                                    }
                                }
                                setPmt_catg(String.valueOf(pmt_dobj.getPmt_catg()));
                                setPmt_type(String.valueOf(pmt_dobj.getPmt_type()));
                                disable_pmt_type = true;
                            }
                            if (purCode == TableConstants.VM_PMT_TEMP_PUR_CD && temp_pmt_type && pmt_dobj == null && priviousTempDataTable != null) {
                                render_Temp_Permit_type = true;
                            }
                        }

                        if (!splConfige.isEmpty()) {
                            if (purCode == TableConstants.VM_PMT_SPECIAL_PUR_CD && spl_route_area) {
                                routeCodeDetail("", TableList.VA_TEMP_PERMIT_ROUTE, Util.getUserStateCode(), Util.getUserOffCode());
                                areaCodeDetail(Util.getUserStateCode(), "", Util.getUserOffCode());
                                visible_Route = true;
                                visible_area = true;
                            }
                        }

                        if (isParmanemt_pmt_valid()) {
                            if ((pmt_dobj != null) && (pmt_dobj.getState_cd().equalsIgnoreCase("KA")) && purCode == TableConstants.VM_PMT_TEMP_PUR_CD
                                    && (pmt_dobj.getPmt_type() == TableConstants.STAGE_CARRIAGE_PERMIT || pmt_dobj.getPmt_type() == Integer.valueOf(TableConstants.GOODS_PERMIT))) {
                                showPreviousPermitDtls(pmt_dobj);
                            } else if ((pmt_dobj != null) && (pmt_dobj.getState_cd().equalsIgnoreCase("KA")) && purCode == TableConstants.VM_PMT_SPECIAL_PUR_CD
                                    && (pmt_dobj.getPmt_type() != TableConstants.STAGE_CARRIAGE_PERMIT || pmt_dobj.getPmt_type() != Integer.valueOf(TableConstants.GOODS_PERMIT))) {
                                showPreviousPermitDtls(pmt_dobj);
                            } else if (pmt_dobj != null && !(pmt_dobj.getState_cd().equalsIgnoreCase("KA"))) {
                                showPreviousPermitDtls(pmt_dobj);
                            } else {
                                ownerDobj = null;
                                pmt_dobj = null;
                                priviousTempDataShow = false;
                                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "Vehicle don't have a Permanent Permit");
                                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                            }
                        } else {
                            if (pmt_dobj != null) {
                                showPreviousPermitDtls(pmt_dobj);
                            } else {
                                render_Temp_Permit_Fee = true;
                                renderPeriodAtVerify = true;
                            }
                        }
                    } else {
                        ownerDobj = null;
                        pmt_dobj = null;
                        priviousTempDataShow = false;
                        message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "Vehicle details not found");
                        FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                    }
                    temp_state_as_region = (ownerDobj == null && pmt_dobj == null && priviousTempDataShow == false) ? false : temp_state_as_region;
                } else {
                    ownerDobj = null;
                    pmt_dobj = null;
                    priviousTempDataShow = false;
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Vehicle details not found");
                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                }
            } else {
                ownerDobj = null;
                pmt_dobj = null;
                priviousTempDataShow = false;
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error", "Please Enter any vehicle No.");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            }
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            ownerDobj = null;
            pmt_dobj = null;
            priviousTempDataShow = false;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "There is some problem in connection");
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void showPreviousPermitDtls(PermitDetailDobj pmt_dobj) {

        boolean aitpDraftFound = false;
        boolean render_payment_table_aitp = Boolean.valueOf(confige.get("render_payment_table_aitp"));
        if (purCode == TableConstants.VM_PMT_SPECIAL_PUR_CD && pmt_dobj.getRegn_no() != null && render_payment_table_aitp) {
            aitpDraftFound = temp_impl.isExistInVtInstrumentAITP(pmt_dobj.getRegn_no());
        }

        if ((pmt_dobj.getPmt_type() == Integer.valueOf(TableConstants.AITP)) && !aitpDraftFound) {
            ownerDobj = null;
            pmt_dobj = null;
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "This vehicle already have a All India Tourist Permit.");
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
        } else {
            pmt_Bean.permitComponentReadOnly(true);
            pmt_Bean.set_Permit_Dtls_dobj_to_bean(pmt_dobj);
            String action_cd = String.valueOf(appl_details.getCurrent_action_cd());
            render_Temp_Permit_Fee = true;
            if ((action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_VERIFICATION)))
                    || (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_APPROVAL)))
                    || (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_VERIFICATION)))
                    || (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_APPROVAL)))) {
                render_Temp_Permit_Fee = false;
                renderPeriodAtVerify = false;
            }
        }
    }

    public void areaCodeDetail(String state_cd, String stringBuilder, int off_cd) {
        temp_impl = new TemporaryPermitImpl();
        areaActionSource.clear();
        areaActionTarget.clear();
        if (CommonUtils.isNullOrBlank(stringBuilder) || stringBuilder.isEmpty() || ("('')").equalsIgnoreCase(stringBuilder)) {
            Map<String, String> routeMap = temp_impl.getTargetAreaMap(state_cd, off_cd);
            for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                areaActionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
        } else {
            String tempStr = stringBuilder;
            tempStr = tempStr.replace("('", "").replace("')", "");
            String[] temp = tempStr.split(",");
            if (JSFUtils.isNumeric(temp[0])) {
                Map<String, String> routeMap = temp_impl.getSourcesAreaMapWithStrBuil(stringBuilder, state_cd, off_cd);
                for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                    areaActionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
                }
                Map<String, String> mapRouteList = temp_impl.getTargetAreaMapWithStrBuil(stringBuilder, state_cd, off_cd);
                for (Map.Entry<String, String> entry : mapRouteList.entrySet()) {
                    areaActionTarget.add(new PermitRouteList(entry.getKey(), entry.getValue()));
                }
            }
        }
        areaManage = new DualListModel<PermitRouteList>(areaActionSource, areaActionTarget);

    }

    public void routeCodeDetail(String Appl_no, String TableName, String state_cd, int off_cd) {
        boolean flage = false;

        temp_impl = new TemporaryPermitImpl();
        actionSource.clear();
        actionTarget.clear();
        Map<String, String> routeMap = temp_impl.getSourcesRouteMap(Appl_no, TableName, state_cd, off_cd);
        for (Map.Entry<String, String> entry : routeMap.entrySet()) {
            actionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
        }
        Map<String, String> mapRouteList = temp_impl.getTargetRouteMap(Appl_no, TableName, state_cd, off_cd);
        for (Map.Entry<String, String> entry : mapRouteList.entrySet()) {
            flage = true;
            actionTarget.add(new PermitRouteList(entry.getKey(), entry.getValue()));
        }
        setVia_route("");
        if (flage) {
            setVia_route(temp_impl.getRouteViaRouteList(Appl_no, state_cd, TableName));
        }
        routeManage = new DualListModel<PermitRouteList>(actionSource, actionTarget);
    }

    public void onTransfer() {
        temp_impl = new TemporaryPermitImpl();
        setVia_route(temp_impl.getRouteVia(routeManage.getTarget(), Util.getUserStateCode()));

    }

    public void saveDetailinVa_Temp_Permit() {
        FacesMessage message = null;
        try {            
            InsDobj ins_dobj = null;
            String taxExem = ServerUtil.getTmConfigurationParameters(Util.getUserStateCode()).getTax_exemption();
            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            parameters.setOWNER_CD(ownerDobj.getOwner_cd());
            parameters.setVH_CLASS(ownerDobj.getVh_class());
            parameters.setPUR_CD(purCode);
            parameters.setOTHER_CRITERIA(ownerDobj.getOther_criteria());
            if (Boolean.parseBoolean(confige.get("insaurance_exempted"))) {
                if (isCondition(FormulaUtils.replaceTagPermitValues(confige.get("insaurance_condition"), parameters), "saveDetailinVa_Temp_Permit")) {
                    pmtCheckDtsl.getDtlsDobj().setExemptedIns(true);
                }
            }
            if (Boolean.parseBoolean(confige.get("tax_exempted"))) {
                if (isCondition(FormulaUtils.replaceTagPermitValues(taxExem, fillVehicleParametersFromDobj(ownerDobj)), "saveDetailinVa_Temp_Permit")) {
                    pmtCheckDtsl.getDtlsDobj().setExemptedTax(true);
                } else if (isCondition(FormulaUtils.replaceTagPermitValues(taxExem, parameters), "saveDetailinVa_Temp_Permit")) {
                    pmtCheckDtsl.getDtlsDobj().setExemptedTax(true);
                }
            }

            if (isCondition(FormulaUtils.replaceTagPermitValues(confige.get("fitness_exempted"), parameters), "save_details")) {
                pmtCheckDtsl.getDtlsDobj().setExemptedFitness(true);
            }

            passImp = new PassengerPermitDetailImpl();
            String vhClassAllowed = passImp.getVhClassAllowedForTaxExempt(purCode);
            if (purCode == TableConstants.VM_PMT_TEMP_PUR_CD && (!CommonUtils.isNullOrBlank(getPmt_type())) && getPmt_type().equals(String.valueOf(TableConstants.STAGE_CARRIAGE_PERMIT)) && (!CommonUtils.isNullOrBlank(vhClassAllowed)) && vhClassAllowed.contains(String.valueOf(ownerDobj.getVh_class()))) {
                pmtCheckDtsl.getDtlsDobj().setExemptedTax(true);
            }
            if (Boolean.parseBoolean(confige.get("exempt_echallan"))) {
                pmtCheckDtsl.getDtlsDobj().setExemptedChallan(true);
            }
            String pmtMsg = CommonPermitPrintImpl.checkPmtValidation(pmtCheckDtsl.getDtlsDobj());            
            if (Util.getUserStateCode().equals("KL") || (Util.getUserStateCode().equals("PY") && purCode == TableConstants.VM_PMT_SPECIAL_PUR_CD)) {                
                if (!CommonUtils.isNullOrBlank(pmtMsg) && pmtMsg.equalsIgnoreCase("Please pay MV Tax first.")) {                    
                    pmtMsg = CommonPermitPrintImpl.checkPmtGracePeriod(pmtCheckDtsl.getDtlsDobj(), Integer.parseInt(getPmt_type()));                    
                }
            }
            if (pmtMsg != null && isCondition(FormulaUtils.replaceTagPermitValues(tempConfige.get("tax_valid_on_verify"), parameters), "saveDetailinVa_Temp_Permit") && pmtMsg.equalsIgnoreCase("Please pay MV Tax first.")) {
                pmtMsg = null;
            }
            if (renderGoodsPassengerTax && CommonUtils.isNullOrBlank(pmtMsg)) {
                pmtMsg = CommonPermitPrintImpl.checkGoodsPassengerTaxValidation(pmtCheckDtsl.getDtlsDobj());
            }
            if (spl_pmt_route && purCode == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                if (spl_route_Bean.getSpecialRouteList() == null || spl_route_Bean.getSpecialRouteList().isEmpty()) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information ", "Enter Route Detail");
                    PrimeFaces.current().dialog().showMessageDynamic(message);
                    return;
                }
                if (!spl_route_Bean.isCheckPassengerList()) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information ", "Check Passenger List");
                    PrimeFaces.current().dialog().showMessageDynamic(message);
                    return;
                }
                if (spl_route_Bean.getSpecialRouteList().size() > 0) {
                    for (int i = 0; i < spl_route_Bean.getSpecialRouteList().size() - 1; i++) {
                        if ((!spl_route_Bean.getSpecialRouteList().get(i + 1).getRoute_fr().trim().equalsIgnoreCase(spl_route_Bean.getSpecialRouteList().get(i).getRoute_to().trim()))) {
                            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information", "Route Origin and Destination mis-matched");
                            PrimeFaces.current().dialog().showMessageDynamic(message);
                            return;
                        }
                    }
                    if (spl_route_Bean.getSpecialRouteList().size() > 1 && !spl_route_Bean.getSpecialRouteList().get(0).getRoute_fr().equalsIgnoreCase(spl_route_Bean.getSpecialRouteList().get(spl_route_Bean.getSpecialRouteList().size() - 1).getRoute_to())) {
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information", "Route Origin and Destination not matched");
                        PrimeFaces.current().dialog().showMessageDynamic(message);
                        return;
                    }
                    if (!spl_route_Bean.getSpecialRouteList().get(0).getRoute_fr().toUpperCase().trim().equalsIgnoreCase(temp_dobj.getRoute_fr().toUpperCase().trim())
                            || !spl_route_Bean.getSpecialRouteList().get(0).getRoute_fr().toUpperCase().trim().equalsIgnoreCase(temp_dobj.getRoute_to().toUpperCase().trim())) {
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information", "Travel From and Travel Upto must be same as Route Origin");
                        PrimeFaces.current().dialog().showMessageDynamic(message);
                        return;
                    }

                    if (temp_dobj.getValid_from() != null && temp_dobj.getValid_upto() != null) {
                        try {
                            int day = (int) TimeUnit.DAYS.convert(temp_dobj.getValid_upto().getTime() - temp_dobj.getValid_from().getTime(), TimeUnit.MILLISECONDS) + 1;
                            if (spl_route_Bean.getSpecialRouteList().size() != day) {
                                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information", "Routes not shedule as per valid permit Days");
                                PrimeFaces.current().dialog().showMessageDynamic(message);
                                return;
                            }
                            String last_date = DateUtils.getLastOfMonthDate(temp_dobj.getPrv_valid_to());
                            Date tax = JSFUtils.getStringToDateddMMMyyyy(pmtCheckDtsl.dtlsDobj.getTaxUpto());
                            Date permit = JSFUtils.getStringToDate(last_date);
                            if (!pmtCheckDtsl.getDtlsDobj().isExemptedTax() && permit.after(tax)) {
                                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information", "Tax is not Valid from upto Date");
                                PrimeFaces.current().dialog().showMessageDynamic(message);
                                return;
                            }
                        } catch (DateUtilsException ex) {
                            LOGGER.error("saveDetailinVa_Temp_Permit()" + ex.toString() + " " + ex.getStackTrace()[0]);
                        }
                    }
                }

            }
            if (render_spl_passenger && purCode == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                if (spl_passenger_Bean.getPassengerList() == null || spl_passenger_Bean.getPassengerList().isEmpty()
                        || CommonUtils.isNullOrBlank(spl_passenger_Bean.getPassengerList().get(0).getName())
                        || CommonUtils.isNullOrBlank(spl_passenger_Bean.getPassengerList().get(0).getGender())
                        || Integer.parseInt(spl_passenger_Bean.getPassengerList().get(0).getAge()) <= 0) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information ", "Enter Passenger Detail");
                    PrimeFaces.current().dialog().showMessageDynamic(message);
                    return;
                }
                if (spl_passenger_Bean.getPassengerList() != null
                        && spl_passenger_Bean.getPassengerList().size() > ownerDetail.getSeat_cap()) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information ",
                            "Passenger detail entry should not be more than seating capacity("
                            + ownerDetail.getSeat_cap() + ")");
                    PrimeFaces.current().dialog().showMessageDynamic(message);
                    return;
                }

            }

            temp_impl = new TemporaryPermitImpl();
            if (temp_dobj.getValid_from() == null) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Select the valid from Date ", "Select the valid from Date");
            } else if (priviousTempDataTable != null && purCode == TableConstants.VM_PMT_SPECIAL_PUR_CD && priviousTempDataTable.get(0).getDtlsList().size() > 0 && temp_dobj.getValid_from().before(DateUtils.parseDate(priviousTempDataTable.get(0).getDtlsList().get(0).getValid_upto()))) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Previous permit is valid So select From date after ", "Previous permit is valid So select From date after :" + priviousTempDataTable.get(0).getDtlsList().get(0).getValid_upto());
            } else if (("-1").equalsIgnoreCase(temp_dobj.getPeriod_mode())) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please Select the period mode ", "Please Select the period mode");
            } else if (("").equalsIgnoreCase(temp_dobj.getPeriod_in_no())) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please fill the No. of period ", "Please fill the No. of period");
            } else if (("").equalsIgnoreCase(temp_dobj.getRoute_fr())) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please fill the travel from", "Please fill the travel from");
            } else if (("").equalsIgnoreCase(temp_dobj.getRoute_to())) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please fill the travel upto", "Please fill the travel upto");
            } else if (renderRlengthServicePanal && temp_dobj.getRoute_length() == 0) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please Enter the route length", "Please Enter the route length");
            } else if (renderRlengthServicePanal && (temp_dobj.getService_type() == 0 || temp_dobj.getService_type() == -1)) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please select the service type", "Please select the service type");
            } else if (("").equalsIgnoreCase(temp_dobj.getPurpose())) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please fill the Purpose of Journey", "Please fill the Purpose of Journey");
            } else if (isParmanemt_pmt_valid() && pmt_dobj.getValid_upto().getTime() < currentDate.getTime()) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Permanent permit is expired.");
            } else if (!CommonUtils.isNullOrBlank(pmtMsg)) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", pmtMsg);
            } else if (temp_pmt_type && pmt_dobj == null
                    && getPmt_type().equalsIgnoreCase("-1") && (purCode == TableConstants.VM_PMT_TEMP_PUR_CD || purCode == TableConstants.VM_PMT_RENEW_TEMP_PUR_CD)) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please Select Permit Type ", "Please Select Permit Type");
            } else if (temp_pmt_type && pmt_dobj == null
                    && !getPmt_type().equalsIgnoreCase("-1") && pmtCategory_list.size() > 0 && getPmt_catg().equalsIgnoreCase("-1")
                    && (purCode == TableConstants.VM_PMT_TEMP_PUR_CD || purCode == TableConstants.VM_PMT_RENEW_TEMP_PUR_CD)) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please Select Permit Category ", "Please Select Permit Category");
            } else {
                if (!pmtCheckDtsl.getDtlsDobj().isExemptedIns() && pmtCheckDtsl.getDtlsDobj().isInsSaveData()) {
                    ins_dobj = new InsDobj();
                    ins_dobj.setAppl_no("");
                    ins_dobj.setIns_from(CommonPermitPrintImpl.getDateDD_MMM_YYYY(pmtCheckDtsl.getDtlsDobj().getInsFrom()));
                    ins_dobj.setIns_upto(CommonPermitPrintImpl.getDateDD_MMM_YYYY(pmtCheckDtsl.getDtlsDobj().getInsUpto()));
                    ins_dobj.setComp_cd(Integer.valueOf(CommonPermitPrintImpl.getValueForSelectedList((ArrayList) arrayInsCmpy, pmtCheckDtsl.getDtlsDobj().getInsCmpyNo())));
                    ins_dobj.setIns_type(Integer.valueOf(CommonPermitPrintImpl.getValueForSelectedList((ArrayList) arrayInsType, pmtCheckDtsl.getDtlsDobj().getInsType())));
                    ins_dobj.setPolicy_no(pmtCheckDtsl.getDtlsDobj().getInsPolicyNo());
                }
                temp_dobj.setRegn_no(getRegn_no().toString());
                temp_dobj.setState_cd(Util.getUserStateCode());
                temp_dobj.setOff_cd(Util.getUserOffCode());
                if (route_status || temp_state_as_region || spl_route_area) {
                    String region = "";
                    if (selectedStates != null && (!selectedStates.isEmpty()) && areaManage != null && areaManage.getTarget().size() > 0) {
                        message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Please select either Region/Area OR State Details.");
                        PrimeFaces.current().dialog().showMessageDynamic(message);
                        return;
                    }
                    List area = (temp_state_as_region && !selectedStates.isEmpty()) ? selectedStates : areaManage.getTarget();
                    List route = routeManage.getTarget();
                    if ((purCode == TableConstants.VM_PMT_TEMP_PUR_CD && area.isEmpty() && route.isEmpty() && route_status) || (purCode == TableConstants.VM_PMT_SPECIAL_PUR_CD && area.isEmpty() && route.isEmpty() && spl_route_area)) {
                        message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Please select any Route/Area Details.");
                        PrimeFaces.current().dialog().showMessageDynamic(message);
                        return;
                    }

                    if (area != null && area.size() > 0) {
                        for (Object s : area) {
                            if (s instanceof String) {
                                region = region + ((String) s) + ",";
                            } else if (s instanceof PermitRouteList) {
                                region = region + ((PermitRouteList) s).getKey() + ",";
                            }
                        }
                    }
                    temp_dobj.setRegion_covered(region);
                }
                if (temp_pmt_type && pmt_dobj == null) {
                    temp_dobj.setPmt_type(getPmt_type());
                    temp_dobj.setPmt_catg(getPmt_catg());
                }
                if (temp_pmt_type && pmt_dobj == null) {
                    Pass_dobj = new PassengerPermitDetailDobj();
                    Pass_dobj.setPmt_type_code(getPmt_type());
                    Pass_dobj.setPmtCatg(getPmt_catg());
                } else if (temp_pmt_type && pmt_dobj != null) {
                    Pass_dobj = new PassengerPermitDetailDobj();
                    Pass_dobj.setPmt_type_code(String.valueOf(pmt_dobj.getPmt_type()));
                    Pass_dobj.setPmtCatg(String.valueOf(pmt_dobj.getPmt_catg()));
                }
                String[] values = CommonPermitPrintImpl.getPmtValidateMsg(Util.getUserStateCode(), TableConstants.VM_PMT_TEMP_PUR_CD, ownerDobj, Pass_dobj);
                if (values != null && !CommonUtils.isNullOrBlank(values[1])) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", values[1]);
                    PrimeFaces.current().dialog().showMessageDynamic(message);
                    return;
                }
                String appl_no = temp_impl.savefeeDetails(pmt_dobj, temp_dobj, routeManage.getTarget(), isRoute_status() || temp_state_as_region || isSpl_route_area(), isRenew_temp(), isSpl_pmt_route(), priviousTempDataTable, ins_dobj, spl_route_Bean, isRender_spl_passenger(), spl_passenger_Bean, splRouteDtlsDesc, renderRlengthServicePanal);
                printDialogBox(appl_no);
            }
            if (message != null) {
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            }
        } catch (VahanException e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            PrimeFaces.current().executeScript("PF('bui').hide()");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "There is some problem while saving data");
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
        }

    }

    public void printDialogBox(String app_no, String recpt_no) {
        callPageRedirect = "RecptTime";
        if (("").equalsIgnoreCase(app_no)) {
            setApp_no_msg("There is some problem to save data");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        } else {
            setApp_no_msg("Receipt No " + recpt_no + " generated for Application No " + app_no + ". Please note down the Receipt / Application No for future reference.");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        }
    }

    public void changeDateFieldToResetValue() {
        boolean valid = true;
        String message = null;
        if (valid_from != null) {
            if (priviousTempDataTable != null && priviousTempDataTable.size() > 0 && Util.getUserStateCode().equals("PY")) {
                for (PreviousTempPermitDtlsList obj : priviousTempDataTable) {
                    if (obj != null) {
                        for (PreviousTempPermitDtlsDobj ob : obj.getDtlsList()) {
                            if ((valid_from.before(JSFUtils.getStringToDateddMMMyyyy(ob.getValid_upto())))) {
                                message = "Your Selected date is between your previous Special permit No " + ob.getPmt_no() + "  Valid from " + ob.getValid_from() + " To " + ob.getValid_upto() + " First Surrender/Cancel this permit then apply for new permit";
                                valid = false;
                                break;
                            }
                        }
                    }
                    if (!valid) {
                        break;
                    }
                }
            }
            if (valid && null == message) {
                temp_dobj.setPeriod_mode("-1");
                temp_dobj.setPeriod_in_no("");
                renderedTempPmtDlls = false;
                period_disable = true;
            } else {
                render_Temp_Permit_Fee = false;
                renderPeriodAtVerify = false;
                valid_from = null;
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert !!!", message));
            }
        }
    }

    public void printDialogBox(String app_no) {
        callPageRedirect = "ApplTime";
        if (("").equalsIgnoreCase(app_no)) {
            setApp_no_msg("Application Number is not genrated");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        } else {
            setApp_no_msg("Application Number generated :" + app_no + ". Please note down the Application No for future reference.");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        }
    }

    public String redirect() {
        String return_location = "";
        try {
            if (("ApplTime").equalsIgnoreCase(callPageRedirect)) {
                return_location = "/ui/permit/form_Temporary_Permit.xhtml?faces-redirect=true";
            } else {
                return_location = "seatwork";
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return return_location;
    }

    public void onSelectState() {
        selectedStatesNames = "";
        for (String state_cd : selectedStates) {
            selectedStatesNames = selectedStatesNames + MasterTableFiller.state.get(state_cd).getStateDescr() + "\n";
        }
    }

    public PermitDetailBean getPmt_Bean() {
        return pmt_Bean;
    }

    public void setPmt_Bean(PermitDetailBean pmt_Bean) {
        this.pmt_Bean = pmt_Bean;
    }

    public PermitDetailDobj getPmt_dobj() {
        return pmt_dobj;
    }

    public void setPmt_dobj(PermitDetailDobj pmt_dobj) {
        this.pmt_dobj = pmt_dobj;
    }

    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    public OwnerImpl getOwnerImpl() {
        return ownerImpl;
    }

    public void setOwnerImpl(OwnerImpl ownerImpl) {
        this.ownerImpl = ownerImpl;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public Date getDateRange() {
        return dateRange;
    }

    public void setDateRange(Date dateRange) {
        this.dateRange = dateRange;
    }

    public String getMinDate() {
        return minDate;
    }

    public void setMinDate(String minDate) {
        this.minDate = minDate;
    }

    public String getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(String maxDate) {
        this.maxDate = maxDate;
    }

    public String getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(String totalAmt) {
        this.totalAmt = totalAmt;
    }

    public Date getValid_from() {
        return valid_from;
    }

    public void setValid_from(Date valid_from) {
        this.valid_from = valid_from;
    }

    public PaymentCollectionBean getPaymentBean() {
        return paymentBean;
    }

    public void setPaymentBean(PaymentCollectionBean paymentBean) {
        this.paymentBean = paymentBean;
    }

    public String getApp_no_msg() {
        return app_no_msg;
    }

    public void setApp_no_msg(String app_no_msg) {
        this.app_no_msg = app_no_msg;
    }

    public TemporaryPermitDobj getTemp_dobj() {
        return temp_dobj;
    }

    public void setTemp_dobj(TemporaryPermitDobj temp_dobj) {
        this.temp_dobj = temp_dobj;
    }

    @Override
    public String save() {
        try {
            temp_impl = new TemporaryPermitImpl();
            if (!temp_state_as_region || (selectedStates != null && selectedStates.size() > 0)) {
                temp_impl.saveOnly(route_status || temp_state_as_region, isSpl_pmt_route(), appl_no, spl_route_Bean, temp_dobj, routeManage.getTarget(), temp_state_as_region ? selectedStates : areaManage.getTarget(), ComparisonBeanImpl.changedDataContents(compareChanges()));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "seatwork";
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        if (prv_temp_dobj == null) {
            return getCompBeanList();
        }
        getCompBeanList().clear();
        Compare("Period Mode", prv_temp_dobj.getPeriod_mode(), temp_dobj.getPeriod_mode(), (ArrayList) getCompBeanList());
        Compare("Period", prv_temp_dobj.getPeriod_in_no(), temp_dobj.getPeriod_in_no(), (ArrayList) getCompBeanList());
        Compare("Permit From Date", prv_temp_dobj.getValid_from(), temp_dobj.getValid_from(), (ArrayList) getCompBeanList());
        Compare("Permit Upto Date", prv_temp_dobj.getValid_upto(), temp_dobj.getValid_upto(), (ArrayList) getCompBeanList());
        Compare("Travel From", prv_temp_dobj.getRoute_fr().toUpperCase(), temp_dobj.getRoute_fr().toUpperCase(), (ArrayList) getCompBeanList());
        Compare("Travel Upto", prv_temp_dobj.getRoute_to().toUpperCase(), temp_dobj.getRoute_to().toUpperCase(), (ArrayList) getCompBeanList());
        Compare("Purpose of Journey", prv_temp_dobj.getPurpose().toUpperCase(), temp_dobj.getPurpose().toUpperCase(), (ArrayList) getCompBeanList());
        Compare("Via", prv_temp_dobj.getVia(), temp_dobj.getVia(), (ArrayList) getCompBeanList());
        Compare("Good to carry", prv_temp_dobj.getGoods_to_carry(), temp_dobj.getGoods_to_carry(), (ArrayList) getCompBeanList());
        Compare("Area Details", areaActionTarget, areaManage.getTarget(), (ArrayList) getCompBeanList());
        Compare("Route Details", actionTarget, routeManage.getTarget(), (ArrayList) getCompBeanList());
        Compare("Permit Category", prv_temp_dobj.getPmt_catg(), getPmt_catg(), (ArrayList) getCompBeanList());
        if (spl_pmt_route && purCode == TableConstants.VM_PMT_SPECIAL_PUR_CD && (prv_spl_route != null || prv_spl_route.size() > 0)) {
            ServerUtil.CompareSpacialRoute("Special Route Details", prv_spl_route, spl_route_Bean.getSpecialRouteList(), (ArrayList) getCompBeanList());
        }
        if (temp_state_as_region && purCode == TableConstants.VM_PMT_TEMP_PUR_CD) {
            ServerUtil.Compare("State Details", prvSelectedStatesNames, selectedStatesNames, (ArrayList) getCompBeanList());
        }
        return getCompBeanList();
    }

    @Override
    public String saveAndMoveFile() {
        FacesMessage message = null;
        String action_cd = String.valueOf(appl_details.getCurrent_action_cd());
        String return_location = "";
        List areaDetails = null;
        try {
            if (route_status || temp_state_as_region || spl_route_area) {
                if (selectedStates != null && (!selectedStates.isEmpty()) && areaManage != null && areaManage.getTarget().size() > 0) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Please select either Region/Area OR State Details.");
                    PrimeFaces.current().dialog().showMessageDynamic(message);
                    return "";
                }
                areaDetails = (temp_state_as_region && selectedStates != null && !selectedStates.isEmpty()) ? selectedStates : areaManage.getTarget();
                List route = routeManage.getTarget();
                if ((appl_details.getPur_cd() == TableConstants.VM_PMT_TEMP_PUR_CD && areaDetails.isEmpty() && route.isEmpty() && route_status) || (appl_details.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD && areaDetails.isEmpty() && route.isEmpty() && spl_route_area)) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Please select any Route/Area Details.");
                    PrimeFaces.current().dialog().showMessageDynamic(message);
                    return "";
                }
            }
            if (temp_pmt_type && (appl_details.getPur_cd() == TableConstants.VM_PMT_TEMP_PUR_CD || appl_details.getPur_cd() == TableConstants.VM_PMT_RENEW_TEMP_PUR_CD)) {
                if (getPmt_type().equalsIgnoreCase("-1") || getPmt_type().equals(null)) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Please select Permit Type.");
                    PrimeFaces.current().dialog().showMessageDynamic(message);
                    return "";
                }
                if (getPmtCategory_list().size() > 0 && !getPmt_type().equalsIgnoreCase("-1") && getPmt_catg().equalsIgnoreCase("-1")) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Please select Permit Category.");
                    PrimeFaces.current().dialog().showMessageDynamic(message);
                    return "";
                }
            }

            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            parameters.setOWNER_CD(ownerDobj.getOwner_cd());
            parameters.setVH_CLASS(ownerDobj.getVh_class());
            parameters.setPUR_CD(appl_details.getPur_cd());
            parameters.setOTHER_CRITERIA(ownerDobj.getOther_criteria());
            if (Boolean.parseBoolean(confige.get("insaurance_exempted"))) {
                if (isCondition(FormulaUtils.replaceTagPermitValues(confige.get("insaurance_condition"), parameters), "saveDetailinVa_Temp_Permit")) {
                    pmtCheckDtsl.getDtlsDobj().setExemptedIns(true);
                }
            }
            if (!pmtCheckDtsl.getDtlsDobj().isExemptedIns() && !(pmtCheckDtsl.getDtlsDobj().isInsValid()) && appl_details.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Your vehicle insurance is expired.");
                PrimeFaces.current().dialog().showMessageDynamic(message);
                return "";
            }

            if ((!spl_route_Bean.isCheckPassengerList()) && appl_details.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD && spl_pmt_route) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please Check Passenger List");
                PrimeFaces.current().dialog().showMessageDynamic(message);
                return "";
            }
            if (spl_route_Bean.getSpecialRouteList().size() > 0 && appl_details.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD && spl_pmt_route) {
                for (int i = 0; i < spl_route_Bean.getSpecialRouteList().size() - 1; i++) {
                    if ((!spl_route_Bean.getSpecialRouteList().get(i + 1).getRoute_fr().trim().equalsIgnoreCase(spl_route_Bean.getSpecialRouteList().get(i).getRoute_to().trim()))) {
                        message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Info", "Route Origin and Destination mis-matched");
                        PrimeFaces.current().dialog().showMessageDynamic(message);
                        return "";
                    }
                }
                if (spl_route_Bean.getSpecialRouteList().size() > 1 && !spl_route_Bean.getSpecialRouteList().get(0).getRoute_fr().equalsIgnoreCase(spl_route_Bean.getSpecialRouteList().get(spl_route_Bean.getSpecialRouteList().size() - 1).getRoute_to())) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Info", "Route Origin and Destination not matched");
                    PrimeFaces.current().dialog().showMessageDynamic(message);
                    return "";
                }
                if (!spl_route_Bean.getSpecialRouteList().get(0).getRoute_fr().toUpperCase().trim().equalsIgnoreCase(temp_dobj.getRoute_fr().toUpperCase().trim())
                        || !spl_route_Bean.getSpecialRouteList().get(0).getRoute_fr().toUpperCase().trim().equalsIgnoreCase(temp_dobj.getRoute_to().toUpperCase().trim())) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information", "Travel From and Travel Upto Must be same as Route Origin");
                    PrimeFaces.current().dialog().showMessageDynamic(message);
                    return "";
                }
            }

            if (appl_details.getPur_cd() == TableConstants.VM_PMT_TEMP_PUR_CD && isCondition(FormulaUtils.replaceTagPermitValues(tempConfige.get("tax_valid_on_verify"), parameters), "saveAndMoveFile")) {
                String tax_upto = null;
                Map<String, String> taxPaid = new HashMap<>();
                taxPaid = ServerUtil.taxPaidInfo(false, temp_dobj.getRegn_no(), TableConstants.TM_ROAD_TAX);
                if (taxPaid != null) {
                    for (Map.Entry<String, String> entry : taxPaid.entrySet()) {
                        if (entry.getKey().equalsIgnoreCase("tax_upto")) {
                            tax_upto = entry.getValue();
                        }
                    }
                    if (tax_upto == null || (tax_upto != null && DateUtils.compareDates(temp_dobj.getValid_upto(), DateUtils.parseDate(tax_upto)) == 2)) {
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information", "Tax is invalid.");
                        PrimeFaces.current().dialog().showMessageDynamic(message);
                        return "";
                    }
                }
            }
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());

            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                if ((action_cd == null ? (String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_VERIFICATION)) == null : action_cd.equals(String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_VERIFICATION)))
                        || (action_cd == null ? (String.valueOf(TableConstants.TM_ROLE_PMT_RENEW_TEMP_VERIFICATION)) == null : action_cd.equals(String.valueOf(TableConstants.TM_ROLE_PMT_RENEW_TEMP_VERIFICATION)))
                        || (action_cd == null ? (String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_VERIFICATION)) == null : action_cd.equals(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_VERIFICATION)))
                        || (action_cd == null ? (String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_APPL)) == null : action_cd.equals(String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_APPL)))
                        || (action_cd == null ? (String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_APPL)) == null : action_cd.equals(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_APPL)))
                        || (action_cd == null ? (String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_APPL)) == null : action_cd.equals(String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_APPL)))
                        && !CommonUtils.isNullOrBlank(appl_details.getAppl_no())) {
                    temp_dobj.setPmt_catg(getPmt_catg());
                    temp_impl = new TemporaryPermitImpl();
                    temp_impl.saveAndmove(status, isRoute_status() || temp_state_as_region, isSpl_pmt_route(), spl_route_Bean, appl_no, temp_dobj, areaDetails, routeManage.getTarget(), ComparisonBeanImpl.changedDataContents(compareChanges()));
                    if (status.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                        return_location = disapprovalPrint();
                    } else {
                        return_location = "seatwork";
                    }

                }
                if ((action_cd == null ? (String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_APPROVAL)) == null : action_cd.equals(String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_APPROVAL)))
                        || (action_cd == null ? (String.valueOf(TableConstants.TM_ROLE_PMT_RENEW_TEMP_APPROVAL)) == null : action_cd.equals(String.valueOf(TableConstants.TM_ROLE_PMT_RENEW_TEMP_APPROVAL)))
                        || (action_cd == null ? (String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_APPROVAL)) == null : action_cd.equals(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_APPROVAL)))) {
                    temp_dobj.setPur_cd(appl_details.getPur_cd());
                    temp_dobj.setRegn_no(appl_details.getRegn_no());
                    temp_impl.saveWithApprove(status, isRoute_status() || temp_state_as_region || isSpl_route_area(), isSpl_pmt_route() || render_spl_route_desc, isRenew_temp(), appl_no, temp_dobj, areaDetails, routeManage.getTarget(), ComparisonBeanImpl.changedDataContents(compareChanges()));
                    if (status.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                        return_location = disapprovalPrint();
                    } else {
                        return_location = "seatwork";
                    }
                }
            }

            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                status.setPrev_action_cd_selected(app_disapp_dobj.getPre_action_code());
                temp_impl.rebackStatus(status, isRoute_status() || temp_state_as_region, isSpl_pmt_route(), spl_route_Bean, routeManage.getTarget(), temp_state_as_region ? selectedStates : areaManage.getTarget(), ComparisonBeanImpl.changedDataContents(compareChanges()), temp_dobj, appl_no);
                return_location = "seatwork";
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return return_location;
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
        FacesMessage message = null;
        Exception e = null;
        InsDobj ins_dobj = null;
        Date currentDate = new Date();
        try {
            Map<String, String> confige = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
            temp_impl = new TemporaryPermitImpl();
            if (temp_dobj.getValid_from() == null) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Select the valid from Date ", "Select the valid from Date");
            } else if (("-1").equalsIgnoreCase(temp_dobj.getPeriod_mode())) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please Select the period mode ", "Please Select the period mode");
            } else if (("").equalsIgnoreCase(temp_dobj.getPeriod_in_no())) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please fill the No. of period ", "Please fill the No. of period");
            } else if (("").equalsIgnoreCase(temp_dobj.getRoute_fr())) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please fill the travel from", "Please fill the travel from");
            } else if (("").equalsIgnoreCase(temp_dobj.getRoute_to())) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please fill the travel upto", "Please fill the travel upto");
            } else if (("").equalsIgnoreCase(temp_dobj.getPurpose())) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please fill the Purpose of Journey", "Please fill the Purpose of Journey");
            } else if (isParmanemt_pmt_valid() && pmt_dobj.getValid_upto().getTime() < currentDate.getTime()) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Permanent permit is expired.");
            } else {
                if (pmtCheckDtsl.getDtlsDobj().isInsSaveData()) {
                    ins_dobj = new InsDobj();
                    ins_dobj.setAppl_no("");
                    ins_dobj.setIns_from(CommonPermitPrintImpl.getDateDD_MMM_YYYY(pmtCheckDtsl.getDtlsDobj().getInsFrom()));
                    ins_dobj.setIns_upto(CommonPermitPrintImpl.getDateDD_MMM_YYYY(pmtCheckDtsl.getDtlsDobj().getInsUpto()));
                    ins_dobj.setComp_cd(Integer.valueOf(CommonPermitPrintImpl.getValueForSelectedList((ArrayList) arrayInsCmpy, pmtCheckDtsl.getDtlsDobj().getInsCmpyNo())));
                    ins_dobj.setIns_type(Integer.valueOf(CommonPermitPrintImpl.getValueForSelectedList((ArrayList) arrayInsType, pmtCheckDtsl.getDtlsDobj().getInsType())));
                    ins_dobj.setPolicy_no(pmtCheckDtsl.getDtlsDobj().getInsPolicyNo());
                }
                temp_dobj.setRegn_no(getRegn_no().toString());
                temp_dobj.setOff_cd(getOwnerDobj().getOff_cd());
                temp_dobj.setState_cd(getOwnerDobj().getState_cd());
                generated_appl_no = temp_impl.savefeeDetails(pmt_dobj, temp_dobj, routeManage.getTarget(), isRoute_status() || isSpl_route_area(), isRenew_temp(), isSpl_pmt_route(), priviousTempDataTable, ins_dobj, spl_route_Bean, isRender_spl_passenger(), spl_passenger_Bean, splRouteDtlsDesc, renderRlengthServicePanal);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Application Submitted successfully. Application No. :", generated_appl_no));
            }
            if (message != null) {
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            }
        } catch (Exception ex) {
            e = ex;
        }
        if (e != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application is not Submitted", "Failed..."));
        }
    }

    public List getPeriod_bean() {
        return period_bean;
    }

    public void setPeriod_bean(List period_bean) {
        this.period_bean = period_bean;
    }

    public boolean isPeriod_disable() {
        return period_disable;
    }

    public void setPeriod_disable(boolean period_disable) {
        this.period_disable = period_disable;
    }

    public boolean isRender_app_disapp() {
        return render_app_disapp;
    }

    public boolean isRenderedTempPmtDlls() {
        return renderedTempPmtDlls;
    }

    public void setRenderedTempPmtDlls(boolean renderedTempPmtDlls) {
        this.renderedTempPmtDlls = renderedTempPmtDlls;
    }

    public void setRender_app_disapp(boolean render_app_disapp) {
        this.render_app_disapp = render_app_disapp;
    }

    public boolean isRender_save_Details() {
        return render_save_Details;
    }

    public void setRender_save_Details(boolean render_save_Details) {
        this.render_save_Details = render_save_Details;
    }

    public void check_Time_Period() {
        if (render_spl_route_desc) {
            splRouteDtlsDesc.reset();
        }
        String p_mode = temp_dobj.getPeriod_mode().toString();
        if (("W").equalsIgnoreCase(p_mode) || ("D").equalsIgnoreCase(p_mode) || ("M").equalsIgnoreCase(p_mode)) {
            period_disable = false;
            temp_dobj.setPeriod_in_no("");
            renderedTempPmtDlls = false;
        } else {
            period_disable = true;
            temp_dobj.setPeriod_in_no("");
            renderedTempPmtDlls = false;
        }
    }

    public void validFrom_And_ValidUpto() {
        FacesMessage message = null;
        boolean flag = false;
        try {
            temp_impl = new TemporaryPermitImpl();

            int[] validity = temp_impl.getValidDaysWeeks(Util.getUserStateCode());
            if (validity != null) {
                if (getValid_from() == null || ("0").equalsIgnoreCase(temp_dobj.getPeriod_in_no())) {
                    message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Select the valid from Date", "Select the valid from Date");
                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                    renderedTempPmtDlls = false;
                } else {
                    Date validFrom = null;
                    Date validUpto = null;
                    if (("D").equalsIgnoreCase(temp_dobj.getPeriod_mode().toString())) {
                        if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_TEMP_APPL) {
                            flag = (((Integer.valueOf(temp_dobj.getPeriod_in_no())) <= validity[0] ? true : false) && ((Integer.valueOf(temp_dobj.getPeriod_in_no())) >= validity[4] ? true : false));
                        } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_SPECIAL_APPL) {
                            flag = (((Integer.valueOf(temp_dobj.getPeriod_in_no())) <= validity[2] ? true : false) && ((Integer.valueOf(temp_dobj.getPeriod_in_no())) >= validity[6] ? true : false));
                        }
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(getValid_from());
                        validFrom = cal.getTime();
                        cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(temp_dobj.getPeriod_in_no()) - 1);
                        validUpto = cal.getTime();
                        if (spl_pmt_route && appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_SPECIAL_APPL) {
                            setRender_spl_route(true);
                            spl_route_Bean.setValid_upto(validUpto);
                            spl_route_Bean.getSpl_route_dobj().setValid_from(validFrom);
                            if (validFrom.equals(validUpto)) {
                                spl_route_Bean.getSpl_route_dobj().setSrl_no(1);
                            }
                        }
                    }
                    if (("W").equalsIgnoreCase(temp_dobj.getPeriod_mode())) {
                        if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_TEMP_APPL) {
                            flag = (((Integer.valueOf(temp_dobj.getPeriod_in_no())) <= validity[1] ? true : false) && ((Integer.valueOf(temp_dobj.getPeriod_in_no())) >= validity[5] ? true : false));
                        } else if ((appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_SPECIAL_APPL)) {
                            flag = (((Integer.valueOf(temp_dobj.getPeriod_in_no())) <= validity[3] ? true : false) && ((Integer.valueOf(temp_dobj.getPeriod_in_no())) >= validity[7] ? true : false));
                        }
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(getValid_from());
                        validFrom = cal.getTime();
                        cal.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(temp_dobj.getPeriod_in_no()));
                        cal.add(Calendar.DAY_OF_WEEK, -1);
                        validUpto = cal.getTime();
                        if (spl_pmt_route && appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_SPECIAL_APPL) {
                            setRender_spl_route(true);
                            spl_route_Bean.setValid_upto(validUpto);
                            spl_route_Bean.getSpl_route_dobj().setValid_from(validFrom);
                        }
                    }

                    if (("M").equalsIgnoreCase(temp_dobj.getPeriod_mode())) {
                        if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_TEMP_APPL) {
                            flag = (((Integer.valueOf(temp_dobj.getPeriod_in_no())) <= validity[8] ? true : false) && ((Integer.valueOf(temp_dobj.getPeriod_in_no())) >= 0 ? true : false));
                        }
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(getValid_from());
                        validFrom = cal.getTime();
                        cal.add(Calendar.MONTH, Integer.parseInt(temp_dobj.getPeriod_in_no()));
                        cal.add(Calendar.DAY_OF_MONTH, -1);
                        validUpto = cal.getTime();
                    }

                    if (flag) {

                        renderedTempPmtDlls = true;
                        temp_dobj.setValid_from(validFrom);
                        temp_dobj.setValid_upto(validUpto);
                        SimpleDateFormat sdf = new SimpleDateFormat(DT_DATEFORMAT);
                        temp_dobj.setPrv_valid_fr(sdf.format(validFrom));
                        temp_dobj.setPrv_valid_to(sdf.format(validUpto));
                        splRouteDtlsDesc.setWithInState(true);
                        splRouteDtlsDesc.onClickWithInState();
                        splRouteDtlsDesc.setJourneyState(Util.getUserStateCode());
                        splRouteDtlsDesc.setTemp_valid_from(temp_dobj.getValid_from());
                        splRouteDtlsDesc.setTemp_valid_upto(temp_dobj.getValid_upto());
                    } else {
                        renderedTempPmtDlls = false;
                        message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Information", "Period Invalid");
                        FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                    }
                }
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information", "Not Define Period in Data Base");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            }
        } catch (VahanException e) {
            LOGGER.error(Util.getUserStateCode() + "-" + e);
        }
    }

    public String getCallPageRedirect() {
        return callPageRedirect;
    }

    public void setCallPageRedirect(String callPageRedirect) {
        this.callPageRedirect = callPageRedirect;
    }

    public boolean isRender_Temp_Permit_Fee() {
        return render_Temp_Permit_Fee;
    }

    public void setRender_Temp_Permit_Fee(boolean render_Temp_Permit_Fee) {
        this.render_Temp_Permit_Fee = render_Temp_Permit_Fee;
    }

    public boolean isRender_vehicle() {
        return render_vehicle;
    }

    public void setRender_vehicle(boolean render_vehicle) {
        this.render_vehicle = render_vehicle;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
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

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
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

    public String getVia_route() {
        return via_route;
    }

    public void setVia_route(String via_route) {
        this.via_route = via_route;
    }

    public boolean isVisible_Route() {
        return visible_Route;
    }

    public void setVisible_Route(boolean visible_Route) {
        this.visible_Route = visible_Route;
    }

    public boolean isVisible_area() {
        return visible_area;
    }

    public void setVisible_area(boolean visible_area) {
        this.visible_area = visible_area;
    }

    public List<OwnerDetailsDobj> getDupRegnList() {
        return dupRegnList;
    }

    public void setDupRegnList(List<OwnerDetailsDobj> dupRegnList) {
        this.dupRegnList = dupRegnList;
    }

    public List<PreviousTempPermitDtlsList> getPriviousTempDataTable() {
        return priviousTempDataTable;
    }

    public void setPriviousTempDataTable(List<PreviousTempPermitDtlsList> priviousTempDataTable) {
        this.priviousTempDataTable = priviousTempDataTable;
    }

    public boolean isPriviousTempDataShow() {
        return priviousTempDataShow;
    }

    public void setPriviousTempDataShow(boolean priviousTempDataShow) {
        this.priviousTempDataShow = priviousTempDataShow;
    }

    public int getPurCode() {
        return purCode;
    }

    public void setPurCode(int purCode) {
        this.purCode = purCode;
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

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public boolean isRender_Temp_Permit_type() {
        return render_Temp_Permit_type;
    }

    public void setRender_Temp_Permit_type(boolean render_Temp_Permit_type) {
        this.render_Temp_Permit_type = render_Temp_Permit_type;
    }

    public String getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(String pmt_type) {
        this.pmt_type = pmt_type;
    }

    public String getPmt_catg() {
        return pmt_catg;
    }

    public void setPmt_catg(String pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    public boolean isDisable_pmt_type() {
        return disable_pmt_type;
    }

    public void setDisable_pmt_type(boolean disable_pmt_type) {
        this.disable_pmt_type = disable_pmt_type;
    }

    public String getPmt_no() {
        return pmt_no;
    }

    public void setPmt_no(String pmt_no) {
        this.pmt_no = pmt_no;
    }

    public boolean isDisable_region() {
        return disable_region;
    }

    public void setDisable_region(boolean disable_region) {
        this.disable_region = disable_region;
    }

    public boolean isRoute_status() {
        return route_status;
    }

    public void setRoute_status(boolean route_status) {
        this.route_status = route_status;
    }

    public boolean isRenew_temp() {
        return renew_temp;
    }

    public void setRenew_temp(boolean renew_temp) {
        this.renew_temp = renew_temp;
    }

    public boolean isTemp_pmt_type() {
        return temp_pmt_type;
    }

    public void setTemp_pmt_type(boolean temp_pmt_type) {
        this.temp_pmt_type = temp_pmt_type;
    }

    public List getPmt_type_list() {
        return pmt_type_list;
    }

    public void setPmt_type_list(List pmt_type_list) {
        this.pmt_type_list = pmt_type_list;
    }

    public List getPmtCategory_list() {
        return pmtCategory_list;
    }

    public void setPmtCategory_list(List pmtCategory_list) {
        this.pmtCategory_list = pmtCategory_list;
    }

    public String getPrv_appl_no() {
        return prv_appl_no;
    }

    public void setPrv_appl_no(String prv_appl_no) {
        this.prv_appl_no = prv_appl_no;
    }

    public boolean isRender_spl_route() {
        return render_spl_route;
    }

    public void setRender_spl_route(boolean render_spl_route) {
        this.render_spl_route = render_spl_route;
    }

    public SpecialRoutePermitBean getSpl_route_Bean() {
        return spl_route_Bean;
    }

    public void setSpl_route_Bean(SpecialRoutePermitBean spl_route_Bean) {
        this.spl_route_Bean = spl_route_Bean;
    }

    public boolean isSpl_pmt_route() {
        return spl_pmt_route;
    }

    public void setSpl_pmt_route(boolean spl_pmt_route) {
        this.spl_pmt_route = spl_pmt_route;
    }

    /**
     * @return the temp_state_as_region
     */
    public boolean isTemp_state_as_region() {
        return temp_state_as_region;
    }

    /**
     * @return the stateList
     */
    public List getStateList() {
        return stateList;
    }

    /**
     * @param stateList the stateList to set
     */
    public void setStateList(List stateList) {
        this.stateList = stateList;
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

    public boolean isRender_spl_passenger() {
        return render_spl_passenger;
    }

    public void setRender_spl_passenger(boolean render_spl_passenger) {
        this.render_spl_passenger = render_spl_passenger;
    }

    public SpecialPassengerDetailBean getSpl_passenger_Bean() {
        return spl_passenger_Bean;
    }

    public void setSpl_passenger_Bean(SpecialPassengerDetailBean spl_passenger_Bean) {
        this.spl_passenger_Bean = spl_passenger_Bean;
    }

    public SpecialRouteDtlsDescBean getSplRouteDtlsDesc() {
        return splRouteDtlsDesc;
    }

    public void setSplRouteDtlsDesc(SpecialRouteDtlsDescBean splRouteDtlsDesc) {
        this.splRouteDtlsDesc = splRouteDtlsDesc;
    }

    public boolean isRender_spl_route_desc() {
        return render_spl_route_desc;
    }

    public void setRender_spl_route_desc(boolean render_spl_route_desc) {
        this.render_spl_route_desc = render_spl_route_desc;
    }

    public boolean isParmanemt_pmt_valid() {
        return parmanemt_pmt_valid;
    }

    public void setParmanemt_pmt_valid(boolean parmanemt_pmt_valid) {
        this.parmanemt_pmt_valid = parmanemt_pmt_valid;
    }

    /**
     * @return the ser_type
     */
    public List getSer_type() {
        return ser_type;
    }

    /**
     * @param ser_type the ser_type to set
     */
    public void setSer_type(List ser_type) {
        this.ser_type = ser_type;
    }

    public boolean isRenderRlengthServicePanal() {
        return renderRlengthServicePanal;
    }

    public void setRenderRlengthServicePanal(boolean renderRlengthServicePanal) {
        this.renderRlengthServicePanal = renderRlengthServicePanal;
    }

    public boolean isRenderPeriodAtVerify() {
        return renderPeriodAtVerify;
    }

    public void setRenderPeriodAtVerify(boolean renderPeriodAtVerify) {
        this.renderPeriodAtVerify = renderPeriodAtVerify;
    }

    public boolean isSpl_route_area() {
        return spl_route_area;
    }

    public void setSpl_route_area(boolean spl_route_area) {
        this.spl_route_area = spl_route_area;
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

}
