package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.permit.VmPermitCatgDobj;
import nic.vahan.form.dobj.permit.VmPermitRouteDobj;
import nic.vahan.form.dobj.permit.VmPermitRegionDobj;
import nic.vahan.form.dobj.permit.VmPermitFeeMasterDobj;
import nic.vahan.form.dobj.permit.VmPermitFeeStateConfig;
import nic.vahan.form.dobj.permit.VmPermitStateConfig;
import nic.vahan.form.dobj.permit.VmPermitStateMap;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.MasterPermitTableImpl;
import nic.vahan.server.CommonUtils;

import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author manoj
 */
@ManagedBean(name = "master_permit_bean")
@ViewScoped
public class MasterPermitBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(MasterPermitBean.class);
    private static List masterTableList = new ArrayList();
    private VmPermitCatgDobj pmt_dobj = new VmPermitCatgDobj();
    private VmPermitRouteDobj pmt_route_dobj = new VmPermitRouteDobj();
    private VmPermitRegionDobj pmt_region_dobj = new VmPermitRegionDobj();
    private VmPermitStateMap pmt_state_map_dobj = new VmPermitStateMap();
    private VmPermitFeeMasterDobj pmt_fee_dobj = new VmPermitFeeMasterDobj();
    private VmPermitFeeStateConfig pmt_fee_state_dobj = new VmPermitFeeStateConfig();
    private VmPermitStateConfig pmt_state_config_dobj = new VmPermitStateConfig();
    MasterPermitTableImpl impl = null;
    private String masterValue = "";
    private String dialogHeader = "select";
    private String widgetVar = "select";
    private String errLabelMsg = "";
    private String state_cd = "";
    private String pmtcode = "";
    private String code = "";
    private String[] selectedDocid;
    private String[] selectedSurPurCd = new String[]{"ANY"};
    private String route_code = "";
    private String descr = "";
    private String pmt_type = "";
    private String flag_value = "";
    private String off_code = "";
    private String messageText;
    private String floc = "";
    private String via = "";
    private String tloc = "";
    private String length = "";
    private String region_cover = "";
    private String permitDocument = "";
    private String region = "";
    private String msg = "";
    private String purcode = "";
    private String prmtype = "";
    private String permitcatg = "";
    private String lvh = "";
    private String uvh = "";
    private String lseat = "";
    private String useat = "";
    private String lunldwt = "";
    private String uunldwt = "";
    private String lldwt = "";
    private String uldwt = "";
    private String period = "";
    private String per_region = "";
    private String per_period = "";
    private String fee = "";
    private String fine = "";
    private String pmt_type_flag = "";
    private String pmt_catg_flag = "";
    private String vh_class_flag = "";
    private String seat_cap_flag = "";
    private String unld_wt_flag = "";
    private String ld_wt_flag = "";
    private String temp_days_valid_upto = "";
    private String temp_weeks_valid_upto = "";
    private String spec_days_valid_upto = "";
    private String spec_weeks_valid_upto = "";
    private String sc_renew_before_days = "";
    private String cc_renew_before_days = "";
    private String ai_renew_before_days = "";
    private String psv_renew_before_days = "";
    private String gp_renew_before_days = "";
    private String np_renew_before_days = "";
    private String sc_rule_heading = "";
    private String sc_formno_heading = "";
    private String cc_rule_heading = "";
    private String cc_formno_heading = "";
    private String aitp_rule_heading = "";
    private String aitp_formno_heading = "";
    private String psvp_rule_heading = "";
    private String psvp_formno_heading = "";
    private String gp_rule_heading = "";
    private String gp_formno_heading = "";
    private String np_rule_heading = "";
    private String np_formno_heading = "";
    private String temp_rule_heading = "";
    private String temp_formno_heading = "";
    private String spec_rule_heading = "";
    private String spec_formno_heading = "";
    private String auth_aitp_rule_heading = "";
    private String auth_aitp_formno_heading = "";
    private String auth_np_rule_heading = "";
    private String auth_np_formno_heading = "";
    private String note_in_footer = "";
    private String temp_days_valid_from = "";
    private String temp_weeks_valid_from = "";
    private String spec_days_valid_from = "";
    private String spec_weeks_valid_from = "";
    private int sc_renew_after_days;
    private int cc_renew_after_days;
    private int ai_renew_after_days;
    private int psv_renew_after_days;
    private int gp_renew_after_days;
    private int np_renew_after_days;
    private String renewal_of_permit_valid_from_flag = "A";
    private String temp_route_area = "FALSE";
    private String genrate_ol_appl = "FALSE";
    private String permanent_permit_valid = "FALSE";
    private String allowed_surr_pur_cd = "ANY";
    private String temp_pmt_type = "FALSE";
    private String renew_temp_pmt = "FALSE";
    private String spl_pmt_route = "FALSE";
    private static List masterPermitTableList = new ArrayList();
    private List permitList = new ArrayList();
    private List officeList = new ArrayList();
    // private List perposeList = new ArrayList();
    private List permitPurposeList = new ArrayList();
    private List permittypeList = new ArrayList();
    private List catgdata = new ArrayList();
    private List vhclassList = new ArrayList();
    private String permitcode = "";
    private String purposecode = "";
    private String createUpdateTitle = "";
    private boolean panalhide = false;
    private boolean panalhideroute = false;
    private boolean panalhideregion = false;
    private boolean panalhidefee = false;
    private boolean panalhidedoc = false;
    private boolean panalfeeconfigure = false;
    private boolean panalStateconfig = false;
    private String selectedPurpose = "";
    private String selectedPermtType = "";
    private List permitTypeList = new ArrayList();
    private List<VmPermitCatgDobj> testData = new ArrayList<>();
    private List<VmPermitRegionDobj> regionmaster = new ArrayList<>();
    private List<VmPermitStateMap> docmaster = new ArrayList<>();
    private List<VmPermitStateMap> docfilter;
    private List<VmPermitFeeMasterDobj> feemaster = new ArrayList<>();
    private List<VmPermitFeeMasterDobj> feefilter;
    private List<VmPermitRouteDobj> routemaster = new ArrayList<>();
    private List<VmPermitFeeStateConfig> feeState = new ArrayList<>();
    private List<VmPermitFeeStateConfig> feeStatefilter;
    private List<VmPermitStateConfig> stateConfig = new ArrayList<>();
    private List docList = new ArrayList();
    private List sur_pur_list = new ArrayList();
    public static final String partA = "1";
    public static final String partB = "2";
    public static final String offerLetter = "3";
    public static final String permitAuthrisation = "5";
    public static final String tempPermit = "6";
    public static final String specialPermit = "7";
    public boolean panelFees_Slab_Render = false;
    public boolean masterButton = true;
    HttpSession session = null;
    String user_catg;
    private String route_flag;
    private String new_route_code;
    private String new_tloc;
    private boolean shownewroute = false;
    private boolean disableexistroute = false;
    private String nhOverlapping = "";
    private String nhOverlappingLength = "";
    private List stateList = new ArrayList<>();
    private String state_to = null;
    private Map<String, String> stateConfigMap = null;
    private boolean show_state_to = false;
    private boolean shownewfrom = false;
    private String new_floc;

    public MasterPermitBean() {
    }

    @PostConstruct
    public void Init() {
        try {
            impl = new MasterPermitTableImpl();
            fillcombodata();
            masterPermitTableList.clear();
            user_catg = Util.getUserCategory();
            List tempList = new ArrayList();
            tempList.add("Select");
            if (user_catg.equalsIgnoreCase("S")) {
                tempList.add("PERMIT_CATEGORY");
                tempList.add("PERMIT_DOC_STATE_MAP");
                tempList.add("PERMIT_FEE");
                tempList.add("PERMIT_FEE_STATE_CONFIGURATION");
                tempList.add("PERMIT_STATE_CONFIGURATION");
                tempList.add("PERMIT_FEE_SLAB");
            } else if (user_catg.equalsIgnoreCase("A") || (Util.getUserStateCode().equalsIgnoreCase("AS") && user_catg.equalsIgnoreCase("L"))) {
                tempList.add("PERMIT_ROUTE_MASTER");
                tempList.add("PERMIT_REGION");

            }
            masterPermitTableList.addAll(tempList);
            tempList.clear();
            panelFees_Slab_Render = false;
            setState_cd(Util.getUserStateCode());
            stateConfigMap = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("show_state_to_in_routemaster"), parameters), "init")) {
                setShow_state_to(true);
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void onRowEdit(RowEditEvent event) {
        errLabelMsg = "";
        String testvalue = getMasterValue();
        try {
            if (testvalue.equalsIgnoreCase("PERMIT_CATEGORY")) {
                try {
                    VmPermitCatgDobj dobj = (VmPermitCatgDobj) event.getObject();
                    if (dobj.getDescription().equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Description", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setDescription(dobj.getDescription().toUpperCase());
                    }
                    if (dobj.getPermit_code() == -1) {
                        JSFUtils.showMessagesInDialog("Message", "Please Select Permit Type", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setPermit_code(dobj.getPermit_code());
                    }
                    boolean flag = impl.permitCategoryRecord(dobj, "updateData");
                    if (flag) {
                        reloadData();
                        JSFUtils.showMessagesInDialog("Message", "Data updated sucessfully", FacesMessage.SEVERITY_INFO);
                    } else {
                        reloadData();
                        JSFUtils.showMessagesInDialog("Message", "Data not updated", FacesMessage.SEVERITY_INFO);
                    }
                    reset();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }

            } else if (testvalue.equalsIgnoreCase("PERMIT_ROUTE_MASTER")) {
                try {
                    VmPermitRouteDobj dobj = (VmPermitRouteDobj) event.getObject();
                    if (dobj.getFrom_loc().equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter From Location", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setFrom_loc(dobj.getFrom_loc().toUpperCase());
                    }
                    if (dobj.getVia().equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Via Detail", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setVia(dobj.getVia().toUpperCase());
                    }
                    if (!CommonUtils.isNullOrBlank(dobj.getVia())) {
                        String[] temp = dobj.getVia().split(",");
                        for (int i = 0; i < temp.length; i++) {
                            if (temp[i].length() > 50) {
                                errLabelMsg = "Please Enter upto 50 character in one via(stoppage)";
                                return;
                            }
                        }
                    }
                    if (dobj.getTo_loc().equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter To Location", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setTo_loc(dobj.getTo_loc().toUpperCase());
                    }
                    if (String.valueOf(dobj.getLength()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Route Length", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setLength(dobj.getLength());
                    }
                    if (String.valueOf(dobj.getRegion_cover()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Region Covered", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setRegion_cover(dobj.getRegion_cover());
                    }

                    boolean flag = impl.permitRouteRecord(dobj, "updateData");
                    if (flag) {
                        reloadData();
                        JSFUtils.showMessagesInDialog("Message", "Data updated sucessfully", FacesMessage.SEVERITY_INFO);

                    } else {
                        reloadData();
                        JSFUtils.showMessagesInDialog("Message", "Data not updated ", FacesMessage.SEVERITY_INFO);
                    }
                    reset();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }

            } else if (testvalue.equalsIgnoreCase("PERMIT_REGION")) {
                try {
                    VmPermitRegionDobj dobj = (VmPermitRegionDobj) event.getObject();
                    if (dobj.getRegion().equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Region Detail", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setRegion(dobj.getRegion().toUpperCase());
                    }
                    if (String.valueOf(dobj.getRegion_covered()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Region Covered", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setRegion_covered(dobj.getRegion_covered());
                    }
                    boolean flag = impl.permitRegionRecord(dobj, "updateData");
                    if (flag) {
                        reloadData();
                        JSFUtils.showMessagesInDialog("Message", "Data updated sucessfully", FacesMessage.SEVERITY_INFO);

                    } else {
                        reloadData();
                        JSFUtils.showMessagesInDialog("Message", "Data not updated", FacesMessage.SEVERITY_INFO);
                    }
                    reset();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }

            } else if (testvalue.equalsIgnoreCase("PERMIT_FEE")) {
                try {
                    VmPermitFeeMasterDobj dobj = (VmPermitFeeMasterDobj) event.getObject();
                    if (String.valueOf(dobj.getL_unld_wt()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Lower Unladen weight", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setL_unld_wt(dobj.getL_unld_wt());
                    }
                    if (String.valueOf(dobj.getU_unld_wt()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Upper Unladen weight", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setU_unld_wt(dobj.getU_unld_wt());
                    }
                    if (String.valueOf(dobj.getL_ld_wt()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Lower Laden weight", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setL_ld_wt(dobj.getL_ld_wt());
                    }
                    if (String.valueOf(dobj.getU_ld_wt()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Upper Laden weight", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setU_ld_wt(dobj.getU_ld_wt());
                    }
                    if (String.valueOf(dobj.getFee()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Fees", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setFee(dobj.getFee());
                    }
                    if (String.valueOf(dobj.getFine()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Fine", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setFine(dobj.getFine());
                    }
                    boolean flag = impl.permitFeeRecord(dobj, "updateData");
                    if (flag) {
                        reloadData();
                        JSFUtils.showMessagesInDialog("Message", "Data updated sucessfully", FacesMessage.SEVERITY_INFO);
                    } else {
                        reloadData();
                        JSFUtils.showMessagesInDialog("Message", "Data not updated", FacesMessage.SEVERITY_INFO);
                    }
                    reset();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }

            } else if (testvalue.equalsIgnoreCase("PERMIT_FEE_STATE_CONFIGURATION")) {
                try {
                    VmPermitFeeStateConfig dobj = (VmPermitFeeStateConfig) event.getObject();
                    boolean flag = impl.permitFeeStateConfig(dobj, "updateData");
                    if (flag) {
                        reloadData();
                        JSFUtils.showMessagesInDialog("Message", "Data updated sucessfully", FacesMessage.SEVERITY_INFO);
                    } else {
                        reloadData();
                        JSFUtils.showMessagesInDialog("Message", "Data not updated", FacesMessage.SEVERITY_INFO);
                    }
                    reset();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }

            } else if (testvalue.equalsIgnoreCase("PERMIT_DOC_STATE_MAP")) {
                try {
                    VmPermitStateMap dobj = (VmPermitStateMap) event.getObject();
                    String arr[] = dobj.getDocumentList();
                    StringBuilder sbr = new StringBuilder();
                    if (arr.length == 0) {
                        JSFUtils.showMessagesInDialog("Message", "Please Select Document", FacesMessage.SEVERITY_INFO);
                        setDocmaster(impl.getStateMapDoc());
                        return;
                    }
                    if (dobj.getPurpose_code() == TableConstants.VM_PMT_FRESH_PUR_CD && dobj.getPermit_code() == Integer.parseInt(TableConstants.AITP)
                            || dobj.getPurpose_code() == TableConstants.VM_PMT_FRESH_PUR_CD && dobj.getPermit_code() == Integer.parseInt(TableConstants.NATIONAL_PERMIT)
                            || dobj.getPurpose_code() == TableConstants.VM_PMT_RENEWAL_PUR_CD && dobj.getPermit_code() == Integer.parseInt(TableConstants.AITP)
                            || dobj.getPurpose_code() == TableConstants.VM_PMT_RENEWAL_PUR_CD && dobj.getPermit_code() == Integer.parseInt(TableConstants.NATIONAL_PERMIT)
                            || dobj.getPurpose_code() == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD && dobj.getPermit_code() == Integer.parseInt(TableConstants.AITP)
                            || dobj.getPurpose_code() == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD && dobj.getPermit_code() == Integer.parseInt(TableConstants.NATIONAL_PERMIT)) {
                        List<String> wordList = Arrays.asList(dobj.getDocumentList());
                        if (!wordList.contains(permitAuthrisation)) {
                            JSFUtils.showMessagesInDialog("Message", "Permit Authorisation compalsory in case of AITP and National Permit", FacesMessage.SEVERITY_INFO);
                            return;
                        }
                    }
                    if (!dobj.getDocumentid().equals("")) {
                        dobj.setDocumentid(null);
                    }
                    for (String s : arr) {
                        sbr.append(s);
                        sbr.append(",");
                    }
                    dobj.setDocumentid(sbr.toString());
                    boolean flag = impl.permitDocMapRecord(dobj, "updateData");
                    if (flag) {
                        reloadData();
                        JSFUtils.showMessagesInDialog("Message", "Data updated sucessfully", FacesMessage.SEVERITY_INFO);
                    } else {
                        reloadData();
                        JSFUtils.showMessagesInDialog("Message", "Data not updated", FacesMessage.SEVERITY_INFO);
                    }
                    reset();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }

            } else if (testvalue.equalsIgnoreCase("PERMIT_STATE_CONFIGURATION")) {
                try {
                    VmPermitStateConfig dobj = (VmPermitStateConfig) event.getObject();
                    String arr[] = dobj.getSurPurList();
                    StringBuilder sbr = new StringBuilder();
                    if (arr.length == 0) {
                        JSFUtils.showMessagesInDialog("Message", "Please Select Surrender Purpose", FacesMessage.SEVERITY_INFO);
                        setStateConfig(impl.getStateConfigMaster(sur_pur_list));
                        return;
                    }
                    if (String.valueOf(dobj.getTemp_days_valid_upto()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Temp day valid upto", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setTemp_days_valid_upto(dobj.getTemp_days_valid_upto());
                    }
                    if (String.valueOf(dobj.getTemp_weeks_valid_upto()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Temp weeks valid upto", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setTemp_weeks_valid_upto(dobj.getTemp_weeks_valid_upto());
                    }
                    if (String.valueOf(dobj.getSpec_days_valid_upto()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Special day valid upto", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setSpec_days_valid_upto(dobj.getSpec_days_valid_upto());
                    }
                    if (String.valueOf(dobj.getSpec_weeks_valid_upto()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Special Week valid upto", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setSpec_weeks_valid_upto(dobj.getSpec_weeks_valid_upto());
                    }
                    if (String.valueOf(dobj.getSc_renew_before_days()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Sc Renew before days", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setSc_renew_before_days(dobj.getSc_renew_before_days());
                    }
                    if (String.valueOf(dobj.getCc_renew_before_days()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Cc renew before days", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setCc_renew_before_days(dobj.getCc_renew_before_days());
                    }
                    if (String.valueOf(dobj.getAi_renew_before_days()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Ai renew before days", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setAi_renew_before_days(dobj.getAi_renew_before_days());
                    }
                    if (String.valueOf(dobj.getPsv_renew_before_days()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Psv renew before days", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setPsv_renew_before_days(dobj.getPsv_renew_before_days());
                    }
                    if (String.valueOf(dobj.getGp_renew_before_days()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Gp renew before days", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setGp_renew_before_days(dobj.getGp_renew_before_days());
                    }
                    if (String.valueOf(dobj.getNp_renew_before_days()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Np renew before days", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setNp_renew_before_days(dobj.getNp_renew_before_days());
                    }
                    if (String.valueOf(dobj.getSc_renew_after_days()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Sc renew after days", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setSc_renew_after_days(dobj.getSc_renew_after_days());
                    }
                    if (String.valueOf(dobj.getCc_renew_after_days()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Cc renew after days", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setCc_renew_after_days(dobj.getCc_renew_after_days());
                    }
                    if (String.valueOf(dobj.getAi_renew_after_days()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Aitp renew after days", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setAi_renew_after_days(dobj.getAi_renew_after_days());
                    }
                    if (String.valueOf(dobj.getPsv_renew_after_days()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Psvp renew after days", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setPsv_renew_after_days(dobj.getPsv_renew_after_days());
                    }
                    if (String.valueOf(dobj.getGp_renew_after_days()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Gp renew after days", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setGp_renew_after_days(dobj.getGp_renew_after_days());
                    }
                    if (String.valueOf(dobj.getNp_renew_after_days()).equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Np renew after days", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setNp_renew_after_days(dobj.getNp_renew_after_days());
                    }
                    if (dobj.getRenewal_of_permit_valid_from_flag().equalsIgnoreCase("")) {
                        JSFUtils.showMessagesInDialog("Message", "Please Enter Renewal of permit valid flag", FacesMessage.SEVERITY_ERROR);
                        return;
                    } else {
                        dobj.setRenewal_of_permit_valid_from_flag(dobj.getRenewal_of_permit_valid_from_flag());
                    }

                    for (String s : arr) {
                        sbr.append(s);
                        sbr.append(",");
                    }
                    dobj.setAllowed_surr_pur_cd(sbr.toString());
                    boolean flag = impl.permitStateConfig(dobj, "updateData");
                    if (flag) {
                        reloadData();
                        JSFUtils.showMessagesInDialog("Message", "Data updated sucessfully", FacesMessage.SEVERITY_INFO);
                    } else {
                        reloadData();
                        JSFUtils.showMessagesInDialog("Message", "Data not updated", FacesMessage.SEVERITY_INFO);
                    }
                    reset();

                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

    }

    public void onRowDelete(RowEditEvent event) {

        if (masterValue.equalsIgnoreCase("PERMIT_CATEGORY")) {

            try {
                VmPermitCatgDobj dobj = (VmPermitCatgDobj) event.getObject();
                boolean flag = impl.deletePermitCatgRecord(dobj);
                if (flag) {
                    reloadData();
                    JSFUtils.showMessagesInDialog("Message", "Data delete sucessfully", FacesMessage.SEVERITY_INFO);
                } else {
                    reloadData();
                    JSFUtils.showMessagesInDialog("Message", "Data not deleted", FacesMessage.SEVERITY_INFO);
                }
                reset();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        } else if (masterValue.equalsIgnoreCase("PERMIT_ROUTE_MASTER")) {

            try {
                VmPermitRouteDobj dobj = (VmPermitRouteDobj) event.getObject();
                boolean flag = impl.deletePmtRouteRecord(dobj);
                if (flag) {
                    reloadData();
                    JSFUtils.showMessagesInDialog("Message", "Data delete sucessfully", FacesMessage.SEVERITY_INFO);
                } else {
                    reloadData();
                    JSFUtils.showMessagesInDialog("Message", "Data not deleted", FacesMessage.SEVERITY_INFO);
                }
                reset();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        } else if (masterValue.equalsIgnoreCase("PERMIT_REGION")) {
            try {
                VmPermitRegionDobj dobj = (VmPermitRegionDobj) event.getObject();
                boolean flag = impl.deletePmtRegionRecord(dobj);
                if (flag) {
                    reloadData();
                    JSFUtils.showMessagesInDialog("Message", "Data delete sucessfully", FacesMessage.SEVERITY_INFO);
                } else {
                    reloadData();
                    JSFUtils.showMessagesInDialog("Message", "Data not sucessfully", FacesMessage.SEVERITY_INFO);
                }
                reset();

            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        } else if (masterValue.equalsIgnoreCase("PERMIT_FEE")) {

            try {
                VmPermitFeeMasterDobj dobj = (VmPermitFeeMasterDobj) event.getObject();
                boolean flag = impl.deletePmtFeeRecord(dobj);
                if (flag) {
                    reloadData();
                    if (feefilter != null) {
                        feefilter.clear();
                    }
                    JSFUtils.showMessagesInDialog("Message", "Data delete sucessfully", FacesMessage.SEVERITY_INFO);
                } else {
                    reloadData();
                    JSFUtils.showMessagesInDialog("Message", "Data not deleted", FacesMessage.SEVERITY_INFO);
                }
                reset();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }

        } else if (masterValue.equalsIgnoreCase("PERMIT_DOC_STATE_MAP")) {

            try {
                VmPermitStateMap dobj = (VmPermitStateMap) event.getObject();
                boolean flag = impl.deletePmtDocMapRecord(dobj);
                if (flag) {
                    reloadData();
                    if (docfilter != null) {
                        docfilter.clear();
                    }
                    JSFUtils.showMessagesInDialog("Message", "Data delete sucessfully", FacesMessage.SEVERITY_INFO);
                } else {
                    reloadData();
                    JSFUtils.showMessagesInDialog("Message", "Data not deleted", FacesMessage.SEVERITY_INFO);
                }
                reset();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }

        } else if (masterValue.equalsIgnoreCase("PERMIT_FEE_STATE_CONFIGURATION")) {

            try {
                VmPermitFeeStateConfig dobj = (VmPermitFeeStateConfig) event.getObject();
                boolean flag = impl.deletePmtFeeStateConfig(dobj);
                if (flag) {
                    reloadData();
                    if (feeStatefilter != null) {
                        feeStatefilter.clear();
                    }
                    JSFUtils.showMessagesInDialog("Message", "Data delete sucessfully", FacesMessage.SEVERITY_INFO);
                } else {
                    reloadData();
                    JSFUtils.showMessagesInDialog("Message", "Data not deleted", FacesMessage.SEVERITY_INFO);
                }
                reset();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }

        } else if (masterValue.equalsIgnoreCase("PERMIT_STATE_CONFIGURATION")) {

            try {
                VmPermitStateConfig dobj = (VmPermitStateConfig) event.getObject();
                boolean flag = impl.deletePmtStateConfig(dobj);
                if (flag) {
                    reloadData();
                    JSFUtils.showMessagesInDialog("Message", "Data delete sucessfully", FacesMessage.SEVERITY_INFO);
                } else {
                    reloadData();
                    JSFUtils.showMessagesInDialog("Message", "Data not deleted", FacesMessage.SEVERITY_INFO);
                }
                reset();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public void showTableData() {
        try {
            masterButton = true;
            panelFees_Slab_Render = false;
            testData.clear();
            routemaster.clear();
            regionmaster.clear();
            feemaster.clear();
            docmaster.clear();
            String testvalue = getMasterValue();
            if (!masterValue.equalsIgnoreCase("Select")) {
                if (testvalue.equalsIgnoreCase("PERMIT_CATEGORY")) {
                    setPanalhide(true);
                    setPanalhideroute(false);
                    setPanalhideregion(false);
                    setPanalhidedoc(false);
                    setPanalhidefee(false);
                    setPanalfeeconfigure(false);
                    setPanalStateconfig(false);
                    setTestData(impl.pmtCatgData());

                } else if (testvalue.equalsIgnoreCase("PERMIT_ROUTE_MASTER")) {
                    setPanalhide(false);
                    setPanalhideregion(false);
                    setPanalhideroute(true);
                    setPanalhidedoc(false);
                    setPanalhidefee(false);
                    setPanalfeeconfigure(false);
                    setPanalStateconfig(false);
                    setRoutemaster(impl.getRouteData());

                } else if (testvalue.equalsIgnoreCase("PERMIT_REGION")) {
                    setPanalhide(false);
                    setPanalhideroute(false);
                    setPanalhideregion(true);
                    setPanalhidedoc(false);
                    setPanalhidefee(false);
                    setPanalfeeconfigure(false);
                    setPanalStateconfig(false);
                    setRegionmaster(impl.getRegionData());

                } else if (testvalue.equalsIgnoreCase("PERMIT_ROUTE_MASTER")) {
                    setPanalhideroute(false);
                    setPanalhide(false);
                    setPanalhideregion(false);
                    setPanalhidedoc(false);
                    setPanalhidefee(false);
                    setPanalfeeconfigure(false);
                    setPanalStateconfig(false);
                } else if (testvalue.equalsIgnoreCase("PERMIT_REGION")) {
                    setPanalhideregion(false);
                    setPanalhide(false);
                    setPanalhideroute(false);
                    setPanalhidedoc(false);
                    setPanalhidefee(false);
                    setPanalfeeconfigure(false);
                    setPanalStateconfig(false);

                } else if (testvalue.equalsIgnoreCase("PERMIT_FEE")) {
                    setPanalhide(false);
                    setPanalhideroute(false);
                    setPanalhideregion(false);
                    setPanalhidedoc(false);
                    setPanalhidefee(true);
                    setPanalStateconfig(false);
                    setPanalfeeconfigure(false);
                    setFeemaster(impl.getFeeData());

                } else if (testvalue.equalsIgnoreCase("PERMIT_DOC_STATE_MAP")) {
                    docList.clear();
                    setPanalhide(false);
                    setPanalhideroute(false);
                    setPanalhideregion(false);
                    setPanalhidefee(false);
                    setPanalhidedoc(true);
                    setPanalStateconfig(false);
                    setPanalfeeconfigure(false);
                    setDocmaster(impl.getStateMapDoc());

                } else if (testvalue.equalsIgnoreCase("PERMIT_FEE_STATE_CONFIGURATION")) {
                    permitPurposeList.clear();
                    String[][] data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
                    permitPurposeList.add(new SelectItem("-1", "Select Purpose"));
                    for (int i = 0; i < data.length; i++) {
                        if (data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_APPLICATION_PUR_CD)) || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_FRESH_PUR_CD)) || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD)) || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_DUPLICATE_PUR_CD))
                                || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TEMP_PUR_CD)) || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_SPECIAL_PUR_CD)) || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD)) || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_REPLACE_VEH_PUR_CD))
                                || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_PUR_CD)) || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD)) || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD))
                                || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD)) || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD))
                                || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD)) || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_SURRENDER_PUR_CD))) {
                            permitPurposeList.add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                    setPanalhide(false);
                    setPanalhideroute(false);
                    setPanalhideregion(false);
                    setPanalhidefee(false);
                    setPanalhidedoc(false);
                    setPanalfeeconfigure(true);
                    setPanalStateconfig(false);
                    setFeeState(impl.getFeeStateConfig());

                } else if (testvalue.equalsIgnoreCase("PERMIT_STATE_CONFIGURATION")) {
                    setPanalhide(false);
                    setPanalhideroute(false);
                    setPanalhideregion(false);
                    setPanalhidefee(false);
                    setPanalhidedoc(false);
                    setPanalfeeconfigure(false);
                    setPanalStateconfig(true);
                    setStateConfig(impl.getStateConfigMaster(sur_pur_list));

                } else if (testvalue.equalsIgnoreCase("PERMIT_FEE_SLAB")) {
                    masterButton = false;
                    setPanalhide(false);
                    setPanalhideroute(false);
                    setPanalhideregion(false);
                    setPanalhidedoc(false);
                    setPanalhidefee(false);
                    setPanalfeeconfigure(false);
                    setPanalStateconfig(false);
                    panelFees_Slab_Render = true;
                    PrimeFaces.current().ajax().update("masterAddButton Alldialog");
                }

            } else {
                JSFUtils.showMessage("Please select any table");
                setPanalhide(false);
                setPanalhideroute(false);
                setPanalhideregion(false);
                setPanalhidefee(false);
                setPanalhidedoc(false);
                setPanalfeeconfigure(false);
                setPanalStateconfig(false);
                return;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

    }

    public void fillcombodata() throws VahanException {
        String[][] data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
        permitList.add(new SelectItem("-1", " Select "));
        for (int i = 0; i < data.length; i++) {
            permitList.add(new SelectItem(data[i][0], data[i][1]));
        }

        data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
        permittypeList.add(new SelectItem("-1", "Select Permit"));
        for (int i = 0; i < data.length; i++) {
            permittypeList.add(new SelectItem(data[i][0], data[i][1]));
        }

        data = MasterTableFiller.masterTables.TM_OFFICE.getData();
        officeList.add(new SelectItem("0", "--Select Office--"));
        for (int i = 0; i < data.length; i++) {
            if (data[i][13].equalsIgnoreCase(Util.getUserStateCode())) {
                officeList.add(new SelectItem(data[i][0], data[i][1]));
            }
        }

        data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
        permitPurposeList.add(new SelectItem("-1", "Select Purpose"));
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_APPLICATION_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_FRESH_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_DUPLICATE_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TEMP_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_SPECIAL_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_REPLACE_VEH_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_SURRENDER_PUR_CD))) {
                permitPurposeList.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
        data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equalsIgnoreCase(Util.getUserStateCode())) {
                catgdata.add(new SelectItem(data[i][1], data[i][2]));
            }
        }
        data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        for (int i = 0; i < data.length; i++) {
            if (Integer.parseInt(data[i][2]) == TableConstants.VM_VEHTYPE_TRANSPORT) {
                vhclassList.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
        data = MasterTableFiller.masterTables.VM_PERMIT_DOCUMENTS.getData();
        for (int i = 0; i < data.length; i++) {
            docList.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
        sur_pur_list.add(new SelectItem("ANY", "ANY"));
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_PUR_CD)) || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD)) || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_CA_PUR_CD)) || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_CANCELATION_PUR_CD)) || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_CANCELATION_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_REPLACE_VEH_PUR_CD)) || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD)) || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD))) {
                sur_pur_list.add(new SelectItem(data[i][0], data[i][1]));
            }
        }

        data = MasterTableFiller.masterTables.TM_STATE.getData();
        for (int i = 0; i < data.length; i++) {
            stateList.add(new SelectItem(data[i][0], data[i][1]));
        }
    }

    public void onchangePmtType() {
        catgdata.clear();
        String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
        for (int j = 0; j < data.length; j++) {
            if (data[j][0].equalsIgnoreCase(Util.getUserStateCode()) && Integer.parseInt(data[j][3]) == Integer.parseInt(getPermitcode())) {
                catgdata.add(new SelectItem(data[j][1], data[j][2]));
            }
        }
    }

    public void hideandshowpanel(String str) {
        reset();
        errLabelMsg = "";
        if (masterValue.equalsIgnoreCase("PERMIT_CATEGORY")) {
            str = "PERMIT.vm_permit_catg";
        } else if (masterValue.equalsIgnoreCase("PERMIT_ROUTE_MASTER")) {
            str = "PERMIT.vm_route_master";
        } else if (masterValue.equalsIgnoreCase("PERMIT_REGION")) {
            str = "PERMIT.vm_region";
        } else if (masterValue.equalsIgnoreCase("PERMIT_FEE")) {
            str = "PERMIT.vm_permit_fee";
        } else if (masterValue.equalsIgnoreCase("PERMIT_DOC_STATE_MAP")) {
            str = "PERMIT.vm_permit_doc_state_map";
        } else if (masterValue.equalsIgnoreCase("PERMIT_FEE_STATE_CONFIGURATION")) {
            str = "PERMIT.vm_permit_fee_state_configuration";
        } else if (masterValue.equalsIgnoreCase("PERMIT_STATE_CONFIGURATION")) {
            str = "PERMIT.vm_permit_state_configuration";
        }

        if (!masterValue.equalsIgnoreCase("")) {
            docList.clear();
            PrimeFaces.current().executeScript("PF('" + str + "').show()");
            if (str.trim().equalsIgnoreCase("PERMIT.VM_PERMIT_CATG")) {
                createUpdateTitle = "Add New Record In Permit Category";
            } else if (str.trim().equalsIgnoreCase("PERMIT.VM_REGION")) {
                createUpdateTitle = "Add New Record In Region Master";
            } else if (str.trim().equalsIgnoreCase("PERMIT.VM_ROUTE_MASTER")) {
                createUpdateTitle = "Add New Record In Permit Route Master";
            } else if (str.trim().equalsIgnoreCase("PERMIT.vm_permit_doc_state_map")) {
                createUpdateTitle = "Add New Record In Permit State Document Map";
            } else if (str.trim().equalsIgnoreCase("PERMIT.vm_permit_fee")) {
                createUpdateTitle = "Add New Record In Permit Fee Master";
            } else if (str.trim().equalsIgnoreCase("PERMIT.vm_permit_fee_state_configuration")) {
                createUpdateTitle = "Add New Record In Permit Fee State Configuration";
            } else if (str.trim().equalsIgnoreCase("PERMIT.vm_permit_state_configuration")) {
                createUpdateTitle = "Add New Record In Permit State Configuration";
            }
        } else {
            messageText = "Please Select any Table From Drop down";
            PrimeFaces.current().executeScript("PF('" + str + "').show()");
        }
    }

    public void conditionCheck() {
        String pur_code = getPurposecode();
        docList.clear();
        String[][] data = MasterTableFiller.masterTables.VM_PERMIT_DOCUMENTS.getData();

        if (pur_code.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TEMP_PUR_CD))) {
            permitList.clear();
            permitList.add(new SelectItem("0", "0"));
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals(tempPermit)) {
                    docList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        } else if (pur_code.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_SPECIAL_PUR_CD))) {
            permitList.clear();
            permitList.add(new SelectItem("0", "0"));
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals(specialPermit)) {
                    docList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        } else if (pur_code.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD))) {
            permitList.clear();
            permitList.add(new SelectItem("0", "0"));
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals(permitAuthrisation)) {
                    docList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        } else if (pur_code.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD)) || pur_code.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_FRESH_PUR_CD)) || pur_code.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD))) {
            permitList.clear();
            String[][] data1 = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
            permitList.add(new SelectItem("Select", "Select"));
            for (int i = 0; i < data1.length; i++) {
                permitList.add(new SelectItem(data1[i][0], data1[i][1]));
            }
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals(partA) || data[i][0].equals(partB)) {
                    docList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }

        } else if (pur_code.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_APPLICATION_PUR_CD))) {
            permitList.clear();
            permitList.add(new SelectItem("0", "0"));
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals(offerLetter)) {
                    docList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        }

    }

    public void permitTypeCheck() {
        String pur_code = getPurposecode();
        String pmt_types = getPmtcode();
        String[][] data = MasterTableFiller.masterTables.VM_PERMIT_DOCUMENTS.getData();
        if (pur_code.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_FRESH_PUR_CD)) && pmt_types.equalsIgnoreCase(String.valueOf(TableConstants.AITP))
                || pur_code.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_FRESH_PUR_CD)) && pmt_types.equalsIgnoreCase(String.valueOf(TableConstants.NATIONAL_PERMIT))
                || pur_code.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD)) && pmt_types.equalsIgnoreCase(String.valueOf(TableConstants.AITP))
                || pur_code.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD)) && pmt_types.equalsIgnoreCase(String.valueOf(TableConstants.NATIONAL_PERMIT))
                || pur_code.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD)) && pmt_types.equalsIgnoreCase(String.valueOf(TableConstants.AITP))
                || pur_code.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD)) && pmt_types.equalsIgnoreCase(String.valueOf(TableConstants.NATIONAL_PERMIT))) {
            docList.clear();
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals(partA) || data[i][0].equals(partB) || data[i][0].equals(permitAuthrisation)) {
                    docList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        } else {
            docList.clear();
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals(partA) || data[i][0].equals(partB)) {
                    docList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        }
    }

    public void purposeTypeCheck() {
        String pur_code = getPurcode();
        permittypeList.clear();
        if (pur_code.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_DUPLICATE_PUR_CD))
                || pur_code.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_SPECIAL_PUR_CD))
                || pur_code.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TEMP_PUR_CD))
                || pur_code.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_REPLACE_VEH_PUR_CD))
                || pur_code.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD))) {
            permittypeList.add(new SelectItem("0", "0"));

        } else {
            String[][] data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
            permittypeList.add(new SelectItem("-1", " Select Permit"));
            for (int i = 0; i < data.length; i++) {
                permittypeList.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
    }

    public void addNewRecordTable() {
        errLabelMsg = "";
        if (masterValue.equalsIgnoreCase("PERMIT_CATEGORY")) {
            try {
                pmt_dobj.setState_code(getState_cd());
                pmt_dobj.setCcode(getCode());
                pmt_dobj.setDescription(getDescr().toUpperCase());
                if (getPermitcode().equals("-1") || getPermitcode().equals("0")) {
                    JSFUtils.showMessage("Please Select Permit Type");
                    return;
                } else {
                    pmt_dobj.setPermit_code(Integer.parseInt(getPermitcode()));
                }
                pmt_dobj.setFlag(Boolean.parseBoolean(getFlag_value()));
                boolean flag = impl.checkDuplicatePermitCategory(pmt_dobj.getCcode(), pmt_dobj.getState_code());
                if (flag) {
                    errLabelMsg = "Code already exist";
                    JSFUtils.showMessage(errLabelMsg);
                } else {
                    boolean flag_pmt_catg = impl.permitCategoryRecord(pmt_dobj, "insertData");
                    if (flag_pmt_catg) {
                        errLabelMsg = "Data save sucessfully";
                        JSFUtils.showMessage(errLabelMsg);
                        reloadData();
                    } else {
                        errLabelMsg = "Data not saved";
                        JSFUtils.showMessage(errLabelMsg);
                    }
                    reset();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        } else if (masterValue.equalsIgnoreCase("PERMIT_ROUTE_MASTER")) {
            errLabelMsg = "";
            try {
                pmt_route_dobj.setState_code(getState_cd());
                pmt_route_dobj.setOff_code(Integer.parseInt(getOff_code()));
                pmt_route_dobj.setRoute_code(getRoute_code().toUpperCase());
                pmt_route_dobj.setFrom_loc(getFloc().toUpperCase());
                if (!CommonUtils.isNullOrBlank(getVia())) {
                    String[] temp = getVia().split(",");
                    for (int i = 0; i < temp.length; i++) {
                        if (temp[i].length() > 50) {
                            errLabelMsg = "Please Enter upto 50 character in one via(stoppage)";
                            return;
                        }
                    }
                }

                pmt_route_dobj.setVia(getVia().toUpperCase());
                pmt_route_dobj.setTo_loc(getTloc().toUpperCase());
                pmt_route_dobj.setLength(Integer.parseInt(getLength()));
                pmt_route_dobj.setRegion_cover(Integer.parseInt(getRegion_cover()));
                pmt_route_dobj.setRoute_flag(getRoute_flag());
                pmt_route_dobj.setState_to(getState_to());
                if (!CommonUtils.isNullOrBlank(getNhOverlapping())) {
                    pmt_route_dobj.setNhOverlapping(getNhOverlapping().toUpperCase());
                }
                if (!CommonUtils.isNullOrBlank(getNhOverlappingLength())) {
                    pmt_route_dobj.setNhOverlappingLength(Integer.parseInt(getNhOverlappingLength()));
                }

                if (getRoute_flag() != null && !getRoute_flag().trim().equals("")) {
                    pmt_route_dobj.setNew_route_code(getRoute_code().toUpperCase());
                    pmt_route_dobj.setRoute_code(getNew_route_code().toUpperCase());
                }
                if (getRoute_flag() != null && (getRoute_flag().equals("D") || getRoute_flag().equals("C"))) {
                    pmt_route_dobj.setFrom_loc(getNew_floc().toUpperCase());                   
                }
                if (getRoute_flag() != null && (getRoute_flag().equals("E") || getRoute_flag().equals("C"))) {                    
                    pmt_route_dobj.setTo_loc(getNew_tloc().toUpperCase());
                }

                boolean flag = impl.checkDuplicatePermitRoutes(pmt_route_dobj);
                if (getNew_route_code() != null && !getNew_route_code().trim().equals("")) {
                    if (getRoute_flag() == null || getRoute_flag().equals("")) {
                        errLabelMsg = "Please Select The Route Flag";
                        return;
                    }
                }
                if (getRoute_flag() != null && !getRoute_flag().equals("")) {
                    if (getNew_route_code() == null || getNew_route_code().trim().equals("")) {
                        errLabelMsg = "Enter The New Route Code";
                        return;
                    }
                }
                if (getRoute_flag() != null && (getRoute_flag().equals("D") || getRoute_flag().equals("C"))) {
                    if (getNew_floc() == null || getNew_floc().trim().equals("")) {
                        errLabelMsg = "Enter The New From Location";
                        return;
                    }
                }
                if (getRoute_flag() != null && (getRoute_flag().equals("E") || getRoute_flag().equals("C"))) {
                    if (getNew_tloc() == null || getNew_tloc().trim().equals("")) {
                        errLabelMsg = "Enter The New Route To Location";
                        return;
                    }
                }

                if (!CommonUtils.isNullOrBlank(pmt_route_dobj.getNhOverlapping()) && pmt_route_dobj.getNhOverlappingLength() == 0) {
                    errLabelMsg = "Enter The Overlapping Length";
                    return;
                }

                if (flag) {
                    errLabelMsg = "Route Code already exist";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", errLabelMsg));
                } else {
                    errLabelMsg = "";
                    boolean flag_pmt_route = impl.permitRouteRecord(pmt_route_dobj, "insertData");
                    if (flag_pmt_route) {
                        errLabelMsg = "Data save sucessfully";
                        JSFUtils.showMessage(errLabelMsg);
                        reloadData();
                    } else {
                        errLabelMsg = "Data not saved";
                        JSFUtils.showMessage(errLabelMsg);
                    }
                    reset();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        } else if (masterValue.equalsIgnoreCase("PERMIT_REGION")) {
            errLabelMsg = "";
            try {
                pmt_region_dobj.setState_code(getState_cd());
                pmt_region_dobj.setOff_code(Integer.parseInt(getOff_code()));
                pmt_region_dobj.setCode(Integer.parseInt(getCode()));
                pmt_region_dobj.setRegion(getRegion());
                pmt_region_dobj.setRegion_covered(Integer.parseInt(getRegion_cover()));
                boolean flag = impl.checkDuplicatePermitRegion(pmt_region_dobj.getCode(), pmt_region_dobj.getState_code(), pmt_region_dobj.getOff_code());
                if (flag) {
                    errLabelMsg = "Region Code already exist";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", errLabelMsg));
                } else {
                    errLabelMsg = "";
                    boolean flag_pmt_region = impl.permitRegionRecord(pmt_region_dobj, "insertData");
                    if (flag_pmt_region) {
                        errLabelMsg = "Data save sucessfully";
                        JSFUtils.showMessage(errLabelMsg);
                        reloadData();
                    } else {
                        reloadData();
                        errLabelMsg = "Data not saved";
                        JSFUtils.showMessage(errLabelMsg);
                    }
                    reset();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        } else if (masterValue.equalsIgnoreCase("PERMIT_FEE")) {
            errLabelMsg = "";
            try {
                pmt_fee_dobj.setState_code(getState_cd());
                if (Integer.parseInt(getPurcode()) == -1) {
                    JSFUtils.showMessage("Please Select Purpose");
                    return;
                } else {
                    pmt_fee_dobj.setPurpose_code(Integer.parseInt(getPurcode()));
                }
                if (Integer.parseInt(getPermitcode()) == -1) {
                    JSFUtils.showMessage("Please Select Permit Type");
                    return;
                } else {
                    pmt_fee_dobj.setPermit_type(Integer.parseInt(getPermitcode()));
                }
                pmt_fee_dobj.setPermit_catg(Integer.parseInt(getPermitcatg()));
                if (Integer.parseInt(getLvh()) == -1) {
                    JSFUtils.showMessage("Please Select Lower Vehicle Class");
                    return;
                } else {
                    pmt_fee_dobj.setL_vh_class(Integer.parseInt(getLvh()));
                }
                if (Integer.parseInt(getUvh()) == -1) {
                    JSFUtils.showMessage("Please Select Upper Vehicle Class");
                    return;
                } else {
                    pmt_fee_dobj.setU_vh_class(Integer.parseInt(getUvh()));
                }
                pmt_fee_dobj.setL_seat_cap(Integer.parseInt(getLseat()));
                pmt_fee_dobj.setU_seat_cap(Integer.parseInt(getUseat()));
                pmt_fee_dobj.setL_unld_wt(Integer.parseInt(getLunldwt()));
                pmt_fee_dobj.setU_unld_wt(Integer.parseInt(getUunldwt()));
                pmt_fee_dobj.setL_ld_wt(Integer.parseInt(getLldwt()));
                pmt_fee_dobj.setU_ld_wt(Integer.parseInt(getUldwt()));
                pmt_fee_dobj.setPer_region(getPer_region());
                pmt_fee_dobj.setPer_period(getPer_period());
                pmt_fee_dobj.setFee(Integer.parseInt(getFee()));
                pmt_fee_dobj.setFine(Integer.parseInt(getFine()));
                boolean flag = impl.checkDuplicatePermitFees(pmt_fee_dobj);
                if (flag) {
                    errLabelMsg = "Data already exist";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", errLabelMsg));
                } else {
                    errLabelMsg = "";
                    boolean flag_pmt_fee = impl.permitFeeRecord(pmt_fee_dobj, "insertData");
                    if (flag_pmt_fee) {
                        errLabelMsg = "Data save sucessfully";
                        JSFUtils.showMessage(errLabelMsg);
                        reloadData();
                    } else {
                        errLabelMsg = "Data not saved";
                        JSFUtils.showMessage(errLabelMsg);
                        reloadData();
                    }
                    reset();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        } else if (masterValue.equalsIgnoreCase("PERMIT_DOC_STATE_MAP")) {
            errLabelMsg = "";
            try {
                pmt_state_map_dobj.setState_code(getState_cd());
                if (Integer.parseInt(getPurposecode()) == -1) {
                    JSFUtils.showMessage("Please Select Purpose Type");
                    return;
                } else {
                    pmt_state_map_dobj.setPurpose_code(Integer.parseInt(getPurposecode()));
                }
                if (getPmtcode().equalsIgnoreCase("select")) {
                    JSFUtils.showMessage("Please Select Permit Type");
                    return;
                } else {
                    pmt_state_map_dobj.setPermit_code(Integer.parseInt(getPmtcode()));
                }
                if (getSelectedDocid().length > 0) {
                    if (pmt_state_map_dobj.getPurpose_code() == TableConstants.VM_PMT_FRESH_PUR_CD && pmt_state_map_dobj.getPermit_code() == Integer.parseInt(TableConstants.AITP)
                            || pmt_state_map_dobj.getPurpose_code() == TableConstants.VM_PMT_FRESH_PUR_CD && pmt_state_map_dobj.getPermit_code() == Integer.parseInt(TableConstants.NATIONAL_PERMIT)
                            || pmt_state_map_dobj.getPurpose_code() == TableConstants.VM_PMT_RENEWAL_PUR_CD && pmt_state_map_dobj.getPermit_code() == Integer.parseInt(TableConstants.AITP)
                            || pmt_state_map_dobj.getPurpose_code() == TableConstants.VM_PMT_RENEWAL_PUR_CD && pmt_state_map_dobj.getPermit_code() == Integer.parseInt(TableConstants.NATIONAL_PERMIT)
                            || pmt_state_map_dobj.getPurpose_code() == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD && pmt_state_map_dobj.getPermit_code() == Integer.parseInt(TableConstants.AITP)
                            || pmt_state_map_dobj.getPurpose_code() == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD && pmt_state_map_dobj.getPermit_code() == Integer.parseInt(TableConstants.NATIONAL_PERMIT)) {
                        List<String> wordList = Arrays.asList(getSelectedDocid());
                        if (!wordList.contains(permitAuthrisation)) {
                            JSFUtils.showMessage("Permit Authorisation compalsory in case of AITP and National Permit");
                            return;
                        }
                    }
                    StringBuilder commandBuilder = new StringBuilder();
                    for (String s : getSelectedDocid()) {
                        commandBuilder.append(s);
                        commandBuilder.append(",");
                    }

                    permitDocument = commandBuilder.toString();
                    pmt_state_map_dobj.setDocumentid(permitDocument);
                } else {
                    JSFUtils.showMessage("Please Select Documents");
                    return;
                }
                boolean flag = impl.checkDuplicateDocStateMap(pmt_state_map_dobj.getPurpose_code(), pmt_state_map_dobj.getPermit_code());
                if (flag) {
                    errLabelMsg = "Data already exist";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", errLabelMsg));
                } else {
                    errLabelMsg = "";
                    boolean flag_doc_map = impl.permitDocMapRecord(pmt_state_map_dobj, "insertData");
                    if (flag_doc_map) {
                        errLabelMsg = "Data save sucessfully";
                        JSFUtils.showMessage(errLabelMsg);
                        reloadData();
                    } else {
                        errLabelMsg = "Data not saved";
                        JSFUtils.showMessage(errLabelMsg);
                        reloadData();
                    }
                    reset();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        } else if (masterValue.equalsIgnoreCase("PERMIT_FEE_STATE_CONFIGURATION")) {
            errLabelMsg = "";
            try {
                pmt_fee_state_dobj.setState_cd(getState_cd());
                if (Integer.parseInt(getPurcode()) == -1) {
                    JSFUtils.showMessage("Please Select Purpose");
                    return;
                } else {
                    pmt_fee_state_dobj.setPur_cd(Integer.parseInt(getPurcode()));
                }
                if (Integer.parseInt(getPermitcode()) == -1) {
                    JSFUtils.showMessage("Please Select Permit Type");
                    return;
                } else {
                    pmt_fee_state_dobj.setPmt_type(Integer.parseInt(getPermitcode()));
                }
                pmt_fee_state_dobj.setPmt_type_flag(Boolean.parseBoolean(getPmt_type_flag()));
                pmt_fee_state_dobj.setPmt_catg_flag(Boolean.parseBoolean(getPmt_catg_flag()));
                pmt_fee_state_dobj.setVh_class_flag(Boolean.parseBoolean(getVh_class_flag()));
                pmt_fee_state_dobj.setSeat_cap_flag(Boolean.parseBoolean(getSeat_cap_flag()));
                pmt_fee_state_dobj.setUnld_wt_flag(Boolean.parseBoolean(getUnld_wt_flag()));
                pmt_fee_state_dobj.setLd_wt_flag(Boolean.parseBoolean(getLd_wt_flag()));
                pmt_fee_state_dobj.setPer_period_flag(Boolean.parseBoolean(getPer_period()));
                pmt_fee_state_dobj.setPer_region_flag(Boolean.parseBoolean(getPer_region()));
                boolean flag = impl.checkDupPermitFeeConfig(pmt_fee_state_dobj);
                if (flag) {
                    errLabelMsg = "Data already exist";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", errLabelMsg));
                } else {
                    errLabelMsg = "";
                    boolean flag_state_config = impl.permitFeeStateConfig(pmt_fee_state_dobj, "insertData");
                    if (flag_state_config) {
                        errLabelMsg = "Data save sucessfully";
                        JSFUtils.showMessage(errLabelMsg);
                        reloadData();
                    } else {
                        errLabelMsg = "Data not saved";
                        JSFUtils.showMessage(errLabelMsg);
                        reloadData();
                    }
                    reset();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        } else if (masterValue.equalsIgnoreCase("PERMIT_STATE_CONFIGURATION")) {
            errLabelMsg = "";
            try {
                StringBuilder commandBuilder = new StringBuilder();
                for (String s : getSelectedSurPurCd()) {
                    commandBuilder.append(s);
                    commandBuilder.append(",");
                }
                pmt_state_config_dobj.setState_cd(getState_cd());
                pmt_state_config_dobj.setTemp_route_area(Boolean.parseBoolean(getTemp_route_area()));
                pmt_state_config_dobj.setGenrate_ol_appl(Boolean.parseBoolean(getGenrate_ol_appl()));
                pmt_state_config_dobj.setPermanent_permit_valid(Boolean.parseBoolean(getPermanent_permit_valid()));
                pmt_state_config_dobj.setTemp_days_valid_upto(Integer.parseInt(getTemp_days_valid_upto()));
                pmt_state_config_dobj.setTemp_weeks_valid_upto(Integer.parseInt(getTemp_weeks_valid_upto()));
                pmt_state_config_dobj.setSpec_days_valid_upto(Integer.parseInt(getSpec_days_valid_upto()));
                pmt_state_config_dobj.setSpec_weeks_valid_upto(Integer.parseInt(getSpec_weeks_valid_upto()));
                pmt_state_config_dobj.setSc_renew_before_days(Integer.parseInt(getSc_renew_before_days()));
                pmt_state_config_dobj.setCc_renew_before_days(Integer.parseInt(getCc_renew_before_days()));
                pmt_state_config_dobj.setAi_renew_before_days(Integer.parseInt(getAi_renew_before_days()));
                pmt_state_config_dobj.setPsv_renew_before_days(Integer.parseInt(getPsv_renew_before_days()));
                pmt_state_config_dobj.setGp_renew_before_days(Integer.parseInt(getGp_renew_before_days()));
                pmt_state_config_dobj.setNp_renew_before_days(Integer.parseInt(getNp_renew_before_days()));
                pmt_state_config_dobj.setSc_rule_heading(getSc_rule_heading());
                pmt_state_config_dobj.setSc_formno_heading(getSc_formno_heading());
                pmt_state_config_dobj.setCc_rule_heading(getCc_rule_heading());
                pmt_state_config_dobj.setCc_formno_heading(getCc_formno_heading());
                pmt_state_config_dobj.setAitp_rule_heading(getAitp_rule_heading());
                pmt_state_config_dobj.setAitp_formno_heading(getAitp_formno_heading());
                pmt_state_config_dobj.setPsvp_rule_heading(getPsvp_rule_heading());
                pmt_state_config_dobj.setPsvp_formno_heading(getPsvp_formno_heading());
                pmt_state_config_dobj.setGp_rule_heading(getGp_rule_heading());
                pmt_state_config_dobj.setGp_formno_heading(getGp_formno_heading());
                pmt_state_config_dobj.setNp_rule_heading(getNp_rule_heading());
                pmt_state_config_dobj.setNp_formno_heading(getNp_formno_heading());
                pmt_state_config_dobj.setTemp_rule_heading(getTemp_rule_heading());
                pmt_state_config_dobj.setTemp_formno_heading(getTemp_formno_heading());
                pmt_state_config_dobj.setSpec_rule_heading(getSpec_rule_heading());
                pmt_state_config_dobj.setSpec_formno_heading(getSpec_formno_heading());
                pmt_state_config_dobj.setAuth_aitp_rule_heading(getAuth_aitp_rule_heading());
                pmt_state_config_dobj.setAuth_aitp_formno_heading(getAuth_aitp_formno_heading());
                pmt_state_config_dobj.setAuth_np_rule_heading(getAuth_np_rule_heading());
                pmt_state_config_dobj.setAuth_np_formno_heading(getAuth_np_formno_heading());
                pmt_state_config_dobj.setNote_in_footer(getNote_in_footer());
                pmt_state_config_dobj.setTemp_days_valid_from(Integer.parseInt(getTemp_days_valid_from()));
                pmt_state_config_dobj.setTemp_weeks_valid_from(Integer.parseInt(getTemp_weeks_valid_from()));
                pmt_state_config_dobj.setSpec_days_valid_from(Integer.parseInt(getSpec_days_valid_from()));
                pmt_state_config_dobj.setSpec_weeks_valid_from(Integer.parseInt(getSpec_weeks_valid_from()));
                pmt_state_config_dobj.setSc_renew_after_days(getSc_renew_after_days());
                pmt_state_config_dobj.setCc_renew_after_days(getCc_renew_after_days());
                pmt_state_config_dobj.setAi_renew_after_days(getAi_renew_after_days());
                pmt_state_config_dobj.setPsv_renew_after_days(getPsv_renew_after_days());
                pmt_state_config_dobj.setGp_renew_after_days(getGp_renew_after_days());
                pmt_state_config_dobj.setNp_renew_after_days(getNp_renew_after_days());
                pmt_state_config_dobj.setRenewal_of_permit_valid_from_flag(getRenewal_of_permit_valid_from_flag());
                pmt_state_config_dobj.setAllowed_surr_pur_cd(commandBuilder.toString());
                pmt_state_config_dobj.setTemp_pmt_type(Boolean.parseBoolean(getTemp_pmt_type()));
                pmt_state_config_dobj.setRenew_temp_pmt(Boolean.parseBoolean(getRenew_temp_pmt()));
                pmt_state_config_dobj.setSpl_pmt_route(Boolean.parseBoolean(getSpl_pmt_route()));
                boolean flag = impl.checkDupPermitStateConfig(pmt_state_config_dobj);
                if (flag) {
                    errLabelMsg = "Data already exist";
                    JSFUtils.showMessagesInDialog("Message", errLabelMsg, FacesMessage.SEVERITY_WARN);
                } else {
                    errLabelMsg = "";
                    boolean flag_state_config = impl.permitStateConfig(pmt_state_config_dobj, "insertData");
                    if (flag_state_config) {
                        errLabelMsg = "Data save sucessfully";
                        JSFUtils.showMessage(errLabelMsg);
                        reloadData();
                    } else {
                        errLabelMsg = "Data not saved";
                        JSFUtils.showMessage(errLabelMsg);
                        reloadData();
                    }
                    reset();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public void checkDataExistorNot(String code) {
        String state_code = Util.getUserStateCode().toUpperCase();
        errLabelMsg = "";
        try {
            if (masterValue.equalsIgnoreCase("PERMIT_CATEGORY")) {
                boolean flag = impl.checkDuplicatePermitCategory(code.toUpperCase(), state_code);
                if (flag) {
                    errLabelMsg = "Category Code already exist";
                } else {
                    errLabelMsg = "";
                }
            } else if (masterValue.equalsIgnoreCase("PERMIT_REGION")) {
                boolean flag = impl.checkDuplicatePermitRegion(Integer.parseInt(code.toUpperCase()), state_code, Util.getUserOffCode());
                if (flag) {
                    errLabelMsg = "Region Code already exist";
                } else {
                    errLabelMsg = "";
                }
            } else if (masterValue.equalsIgnoreCase("PERMIT_ROUTE_MASTER")) {
                boolean flag = impl.checkDuplicatePermitRoute(code.toUpperCase(), state_code, Util.getUserOffCode());
                if (flag) {
                    errLabelMsg = "Route Code already exist";
                } else {
                    errLabelMsg = "";
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

    }

    public void getRouteDetailsByRouteCode(String code) {
        try {
            reset();
            VmPermitRouteDobj pmt_route_dobj = impl.getPermitRouteByCode(code.toUpperCase(), getState_cd(), Integer.parseInt(getOff_code()));
            if (!CommonUtils.isNullOrBlank(pmt_route_dobj.getState_code())) {
                setState_cd(pmt_route_dobj.getState_code());
            }
            if (pmt_route_dobj.getOff_code() != 0) {
                setOff_code(pmt_route_dobj.getOff_code() + "");
            }
            setRoute_code(pmt_route_dobj.getRoute_code());
            setFloc(pmt_route_dobj.getFrom_loc());
            setTloc(pmt_route_dobj.getTo_loc());
            setLength(pmt_route_dobj.getLength() + "");
            setRegion_cover(pmt_route_dobj.getRegion_cover() + "");
            setVia(pmt_route_dobj.getVia());
            setNhOverlapping(pmt_route_dobj.getNhOverlapping());
            setNhOverlappingLength(pmt_route_dobj.getNhOverlappingLength() + "");
            if (pmt_route_dobj.getRoute_code() != null && !pmt_route_dobj.getRoute_code().equals("")) {
                this.disableexistroute = true;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

    }

    public void reloadData() {
        try {
            errLabelMsg = "";
            testData.clear();
            routemaster.clear();
            regionmaster.clear();
            feemaster.clear();
            docmaster.clear();
            feeState.clear();
            stateConfig.clear();
            if (!masterValue.equalsIgnoreCase("Select")) {
                if (masterValue.equalsIgnoreCase("PERMIT_CATEGORY")) {
                    setPanalhide(true);
                    setPanalhideroute(false);
                    setPanalhideregion(false);
                    setPanalhidedoc(false);
                    setPanalhidefee(false);
                    setPanalStateconfig(false);
                    setPanalfeeconfigure(false);
                    setTestData(impl.pmtCatgData());

                } else if (masterValue.equalsIgnoreCase("PERMIT_ROUTE_MASTER")) {
                    setPanalhide(false);
                    setPanalhideregion(false);
                    setPanalhideroute(true);
                    setPanalhidedoc(false);
                    setPanalhidefee(false);
                    setPanalStateconfig(false);
                    setPanalfeeconfigure(false);
                    setRoutemaster(impl.getRouteData());

                } else if (masterValue.equalsIgnoreCase("PERMIT_REGION")) {
                    setPanalhide(false);
                    setPanalhideroute(false);
                    setPanalhideregion(true);
                    setPanalhidedoc(false);
                    setPanalhidefee(false);
                    setPanalStateconfig(false);
                    setPanalfeeconfigure(false);
                    setRegionmaster(impl.getRegionData());

                } else if (masterValue.equalsIgnoreCase("PERMIT_FEE")) {
                    setPanalhide(false);
                    setPanalhideroute(false);
                    setPanalhideregion(false);
                    setPanalhidedoc(false);
                    setPanalhidefee(true);
                    setPanalStateconfig(false);
                    setPanalfeeconfigure(false);
                    setFeemaster(impl.getFeeData());

                } else if (masterValue.equalsIgnoreCase("PERMIT_DOC_STATE_MAP")) {
                    setPanalhide(false);
                    setPanalhideroute(false);
                    setPanalhideregion(false);
                    setPanalhidefee(false);
                    setPanalhidedoc(true);
                    setPanalStateconfig(false);
                    setPanalfeeconfigure(false);
                    setDocmaster(impl.getStateMapDoc());

                } else if (masterValue.equalsIgnoreCase("PERMIT_FEE_STATE_CONFIGURATION")) {
                    setPanalhide(false);
                    setPanalhideroute(false);
                    setPanalhideregion(false);
                    setPanalhidefee(false);
                    setPanalhidedoc(false);
                    setPanalStateconfig(false);
                    setPanalfeeconfigure(true);
                    setFeeState(impl.getFeeStateConfig());

                } else if (masterValue.equalsIgnoreCase("PERMIT_STATE_CONFIGURATION")) {
                    setPanalhide(false);
                    setPanalhideroute(false);
                    setPanalhideregion(false);
                    setPanalhidefee(false);
                    setPanalhidedoc(false);
                    setPanalStateconfig(true);
                    setPanalfeeconfigure(false);
                    setStateConfig(impl.getStateConfigMaster(sur_pur_list));
                }
            } else {
                JSFUtils.showMessage("Please Select Any Permit Table");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

    }

    public void closeDialogBox() {
        String closeDailogBox = masterValue;
        PrimeFaces.current().executeScript("PF('" + closeDailogBox + "').hide()");
    }

    public void callReset() {
        reset();
    }

    private void reset() {
        setCode("");
        setSelectedDocid(null);
        setState_cd(Util.getUserStateCode());
        setRoute_code("");
        setDescr("");
        setPermitcode("-1");
        setFlag_value("-1");
        setOff_code(String.valueOf(Util.getUserOffCode()));
        setFloc("");
        setVia("");
        setTloc("");
        setLength("");
        setRegion_cover("");
        setRegion("");
        setPurcode("-1");
        setPermitcatg("");
        setLvh("");
        setUvh("");
        setLldwt("");
        setUldwt("");
        setLseat("");
        setUseat("");
        setLunldwt("");
        setUunldwt("");
        setFee("");
        setFine("");
        setPer_period("-1");
        setPer_region("-1");
        setPmt_type_flag("-1");
        setPmt_catg_flag("-1");
        setVh_class_flag("-1");
        setSeat_cap_flag("-1");
        setUnld_wt_flag("-1");
        setLd_wt_flag("-1");
        setTemp_days_valid_upto("");
        setTemp_weeks_valid_upto("");
        setSpec_days_valid_upto("");
        setSpec_weeks_valid_upto("");
        setSc_renew_before_days("");
        setCc_renew_before_days("");
        setAi_renew_before_days("");
        setPsv_renew_before_days("");
        setGp_renew_before_days("");
        setNp_renew_before_days("");
        setSc_rule_heading("");
        setSc_formno_heading("");
        setCc_rule_heading("");
        setCc_formno_heading("");
        setAitp_rule_heading("");
        setAitp_formno_heading("");
        setPsvp_rule_heading("");
        setPsvp_formno_heading("");
        setGp_rule_heading("");
        setGp_formno_heading("");
        setNp_rule_heading("");
        setNp_formno_heading("");
        setTemp_rule_heading("");
        setTemp_formno_heading("");
        setSpec_rule_heading("");
        setSpec_formno_heading("");
        setAuth_aitp_rule_heading("");
        setAuth_aitp_formno_heading("");
        setAuth_np_rule_heading("");
        setAuth_np_formno_heading("");
        setNote_in_footer("");
        setTemp_days_valid_from("");
        setTemp_weeks_valid_from("");
        setSpec_days_valid_from("");
        setSpec_weeks_valid_from("");
        setSc_renew_after_days(0);
        setCc_renew_after_days(0);
        setAi_renew_after_days(0);
        setPsv_renew_after_days(0);
        setGp_renew_after_days(0);
        setNp_renew_after_days(0);
        setRenewal_of_permit_valid_from_flag("A");
        setTemp_route_area("FALSE");
        setPurposecode("");
        setPermanent_permit_valid("FALSE");
        setGenrate_ol_appl("FALSE");
        setPmtcode("");
        setRoute_flag("");
        setNew_route_code("");
        setNew_tloc("");
        setShownewroute(false);
        setDisableexistroute(false);
        setNhOverlapping("");
        setNhOverlappingLength("");
        setNew_floc("");
        setShownewfrom(false);
    }

    public void showNewRouteTo() {
        if (this.route_flag.equals("E") || this.route_flag.equals("C")) {
            this.shownewroute = true;
        } else {
            this.shownewroute = false;
        }
        if (this.route_flag.equals("D") || this.route_flag.equals("C")) {
            this.shownewfrom = true;
        } else {
            this.shownewfrom = false;
        }
    }

    public static List getMasterTableList() {
        return masterTableList;
    }

    public static void setMasterTableList(List masterTableList) {
        MasterPermitBean.masterTableList = masterTableList;
    }

    public String getMasterValue() {
        return masterValue;
    }

    public void setMasterValue(String masterValue) {
        this.masterValue = masterValue;
    }

    public String getDialogHeader() {
        return dialogHeader;
    }

    public void setDialogHeader(String dialogHeader) {
        this.dialogHeader = dialogHeader;
    }

    public String getWidgetVar() {
        return widgetVar;
    }

    public void setWidgetVar(String widgetVar) {
        this.widgetVar = widgetVar;
    }

    public String getErrLabelMsg() {
        return errLabelMsg;
    }

    public void setErrLabelMsg(String errLabelMsg) {
        this.errLabelMsg = errLabelMsg;
    }

    public String getCreateUpdateTitle() {
        return createUpdateTitle;
    }

    public void setCreateUpdateTitle(String createUpdateTitle) {
        this.createUpdateTitle = createUpdateTitle;
    }

    public String getPermitcode() {
        return permitcode;
    }

    public void setPermitcode(String permitcode) {
        this.permitcode = permitcode;
    }

    public List getPermitList() {
        return permitList;
    }

    public void setPermitList(List permitList) {
        this.permitList = permitList;
    }

    public List getMasterPermitTableList() {
        return masterPermitTableList;
    }

    public void setMasterPermitTableList(List masterPermitTableList) {
        this.masterPermitTableList = masterPermitTableList;
    }

    public String getPurposecode() {
        return purposecode;
    }

    public void setPurposecode(String purposecode) {
        this.purposecode = purposecode;
    }

    public boolean isPanalhide() {
        return panalhide;
    }

    public void setPanalhide(boolean panalhide) {
        this.panalhide = panalhide;
    }

    public List getPermitPurposeList() {
        return permitPurposeList;
    }

    public void setPermitPurposeList(List permitPurposeList) {
        this.permitPurposeList = permitPurposeList;
    }

    public String getSelectedPurpose() {
        return selectedPurpose;
    }

    public void setSelectedPurpose(String selectedPurpose) {
        this.selectedPurpose = selectedPurpose;
    }

    public String getSelectedPermtType() {
        return selectedPermtType;
    }

    public void setSelectedPermtType(String selectedPermtType) {
        this.selectedPermtType = selectedPermtType;
    }

    public List getPermitTypeList() {
        return permitTypeList;
    }

    public void setPermitTypeList(List permitTypeList) {
        this.permitTypeList = permitTypeList;
    }

    public List getPermittypeList() {
        return permittypeList;
    }

    public void setPermittypeList(List permittypeList) {
        this.permittypeList = permittypeList;
    }

    public VmPermitCatgDobj getPmt_dobj() {
        return pmt_dobj;
    }

    public void setPmt_dobj(VmPermitCatgDobj pmt_dobj) {
        this.pmt_dobj = pmt_dobj;
    }

    public VmPermitRouteDobj getPmt_route_dobj() {
        return pmt_route_dobj;
    }

    public void setPmt_route_dobj(VmPermitRouteDobj pmt_route_dobj) {
        this.pmt_route_dobj = pmt_route_dobj;
    }

    public VmPermitRegionDobj getPmt_region_dobj() {
        return pmt_region_dobj;
    }

    public void setPmt_region_dobj(VmPermitRegionDobj pmt_region_dobj) {
        this.pmt_region_dobj = pmt_region_dobj;
    }

    public VmPermitStateMap getPmt_state_map_dobj() {
        return pmt_state_map_dobj;
    }

    public void setPmt_state_map_dobj(VmPermitStateMap pmt_state_map_dobj) {
        this.pmt_state_map_dobj = pmt_state_map_dobj;
    }

    public VmPermitFeeMasterDobj getPmt_fee_dobj() {
        return pmt_fee_dobj;
    }

    public void setPmt_fee_dobj(VmPermitFeeMasterDobj pmt_fee_dobj) {
        this.pmt_fee_dobj = pmt_fee_dobj;
    }

    public boolean isPanalhidedoc() {
        return panalhidedoc;
    }

    public void setPanalhidedoc(boolean panalhidedoc) {
        this.panalhidedoc = panalhidedoc;
    }

    public boolean isPanalhidefee() {
        return panalhidefee;
    }

    public void setPanalhidefee(boolean panalhidefee) {
        this.panalhidefee = panalhidefee;
    }

    public boolean isPanalhideregion() {
        return panalhideregion;
    }

    public void setPanalhideregion(boolean panalhideregion) {
        this.panalhideregion = panalhideregion;
    }

    public boolean isPanalhideroute() {
        return panalhideroute;
    }

    public void setPanalhideroute(boolean panalhideroute) {
        this.panalhideroute = panalhideroute;
    }

    public List<VmPermitStateMap> getDocmaster() {
        return docmaster;
    }

    public void setDocmaster(List<VmPermitStateMap> docmaster) {
        this.docmaster = docmaster;
    }

    public List<VmPermitFeeMasterDobj> getFeemaster() {
        return feemaster;
    }

    public void setFeemaster(List<VmPermitFeeMasterDobj> feemaster) {
        this.feemaster = feemaster;
    }

    public List<VmPermitRegionDobj> getRegionmaster() {
        return regionmaster;
    }

    public void setRegionmaster(List<VmPermitRegionDobj> regionmaster) {
        this.regionmaster = regionmaster;
    }

    public List<VmPermitRouteDobj> getRoutemaster() {
        return routemaster;
    }

    public void setRoutemaster(List<VmPermitRouteDobj> routemaster) {
        this.routemaster = routemaster;
    }

    public List<VmPermitCatgDobj> getTestData() {
        return testData;
    }

    public void setTestData(List<VmPermitCatgDobj> testData) {
        this.testData = testData;
    }

    public List getOfficeList() {
        return officeList;
    }

    public void setOfficeList(List officeList) {
        this.officeList = officeList;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public static void setLOGGER(Logger LOGGER) {
        MasterPermitBean.LOGGER = LOGGER;
    }

    public MasterPermitTableImpl getImpl() {
        return impl;
    }

    public void setImpl(MasterPermitTableImpl impl) {
        this.impl = impl;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(String pmt_type) {
        this.pmt_type = pmt_type;
    }

    public String getFlag_value() {
        return flag_value;
    }

    public void setFlag_value(String flag_value) {
        this.flag_value = flag_value;
    }

    public String getOff_code() {
        return off_code;
    }

    public void setOff_code(String off_code) {
        this.off_code = off_code;
    }

    public String getFloc() {
        return floc;
    }

    public void setFloc(String floc) {
        this.floc = floc;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getTloc() {
        return tloc;
    }

    public void setTloc(String tloc) {
        this.tloc = tloc;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPurcode() {
        return purcode;
    }

    public void setPurcode(String purcode) {
        this.purcode = purcode;
    }

    public String getPrmtype() {
        return prmtype;
    }

    public void setPrmtype(String prmtype) {
        this.prmtype = prmtype;
    }

    public String getPermitcatg() {
        return permitcatg;
    }

    public void setPermitcatg(String permitcatg) {
        this.permitcatg = permitcatg;
    }

    public String getLvh() {
        return lvh;
    }

    public void setLvh(String lvh) {
        this.lvh = lvh;
    }

    public String getUvh() {
        return uvh;
    }

    public void setUvh(String uvh) {
        this.uvh = uvh;
    }

    public String getLseat() {
        return lseat;
    }

    public void setLseat(String lseat) {
        this.lseat = lseat;
    }

    public String getUseat() {
        return useat;
    }

    public void setUseat(String useat) {
        this.useat = useat;
    }

    public String getLunldwt() {
        return lunldwt;
    }

    public void setLunldwt(String lunldwt) {
        this.lunldwt = lunldwt;
    }

    public String getUunldwt() {
        return uunldwt;
    }

    public void setUunldwt(String uunldwt) {
        this.uunldwt = uunldwt;
    }

    public String getLldwt() {
        return lldwt;
    }

    public void setLldwt(String lldwt) {
        this.lldwt = lldwt;
    }

    public String getUldwt() {
        return uldwt;
    }

    public void setUldwt(String uldwt) {
        this.uldwt = uldwt;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getFine() {
        return fine;
    }

    public void setFine(String fine) {
        this.fine = fine;
    }

    public String getRegion_cover() {
        return region_cover;
    }

    public void setRegion_cover(String region_cover) {
        this.region_cover = region_cover;
    }

    public String getRoute_code() {
        return route_code;
    }

    public void setRoute_code(String route_code) {
        this.route_code = route_code;
    }

    public String getPer_region() {
        return per_region;
    }

    public void setPer_region(String per_region) {
        this.per_region = per_region;
    }

    public String getPer_period() {
        return per_period;
    }

    public List<VmPermitFeeMasterDobj> getFeefilter() {
        return feefilter;
    }

    public void setFeefilter(List<VmPermitFeeMasterDobj> feefilter) {
        this.feefilter = feefilter;
    }

    public void setPer_period(String per_period) {
        this.per_period = per_period;
    }

    public boolean isPanalfeeconfigure() {
        return panalfeeconfigure;
    }

    public void setPanalfeeconfigure(boolean panalfeeconfigure) {
        this.panalfeeconfigure = panalfeeconfigure;
    }

    public List<VmPermitFeeStateConfig> getFeeState() {
        return feeState;
    }

    public void setFeeState(List<VmPermitFeeStateConfig> feeState) {
        this.feeState = feeState;
    }

    public List<VmPermitStateConfig> getStateConfig() {
        return stateConfig;
    }

    public void setStateConfig(List<VmPermitStateConfig> stateConfig) {
        this.stateConfig = stateConfig;
    }

    public boolean isPanalStateconfig() {
        return panalStateconfig;
    }

    public void setPanalStateconfig(boolean panalStateconfig) {
        this.panalStateconfig = panalStateconfig;
    }

    public String getPmt_type_flag() {
        return pmt_type_flag;
    }

    public void setPmt_type_flag(String pmt_type_flag) {
        this.pmt_type_flag = pmt_type_flag;
    }

    public String getPmt_catg_flag() {
        return pmt_catg_flag;
    }

    public void setPmt_catg_flag(String pmt_catg_flag) {
        this.pmt_catg_flag = pmt_catg_flag;
    }

    public String getVh_class_flag() {
        return vh_class_flag;
    }

    public void setVh_class_flag(String vh_class_flag) {
        this.vh_class_flag = vh_class_flag;
    }

    public String getSeat_cap_flag() {
        return seat_cap_flag;
    }

    public void setSeat_cap_flag(String seat_cap_flag) {
        this.seat_cap_flag = seat_cap_flag;
    }

    public String getUnld_wt_flag() {
        return unld_wt_flag;
    }

    public void setUnld_wt_flag(String unld_wt_flag) {
        this.unld_wt_flag = unld_wt_flag;
    }

    public String getLd_wt_flag() {
        return ld_wt_flag;
    }

    public void setLd_wt_flag(String ld_wt_flag) {
        this.ld_wt_flag = ld_wt_flag;
    }

    public VmPermitFeeStateConfig getPmt_fee_state_dobj() {
        return pmt_fee_state_dobj;
    }

    public void setPmt_fee_state_dobj(VmPermitFeeStateConfig pmt_fee_state_dobj) {
        this.pmt_fee_state_dobj = pmt_fee_state_dobj;
    }

    public String getPermitDocument() {
        return permitDocument;
    }

    public void setPermitDocument(String permitDocument) {
        this.permitDocument = permitDocument;
    }

    public String getTemp_days_valid_upto() {
        return temp_days_valid_upto;
    }

    public void setTemp_days_valid_upto(String temp_days_valid_upto) {
        this.temp_days_valid_upto = temp_days_valid_upto;
    }

    public String getTemp_weeks_valid_upto() {
        return temp_weeks_valid_upto;
    }

    public void setTemp_weeks_valid_upto(String temp_weeks_valid_upto) {
        this.temp_weeks_valid_upto = temp_weeks_valid_upto;
    }

    public String getSpec_days_valid_upto() {
        return spec_days_valid_upto;
    }

    public void setSpec_days_valid_upto(String spec_days_valid_upto) {
        this.spec_days_valid_upto = spec_days_valid_upto;
    }

    public String getSpec_weeks_valid_upto() {
        return spec_weeks_valid_upto;
    }

    public void setSpec_weeks_valid_upto(String spec_weeks_valid_upto) {
        this.spec_weeks_valid_upto = spec_weeks_valid_upto;
    }

    public String getSc_renew_before_days() {
        return sc_renew_before_days;
    }

    public void setSc_renew_before_days(String sc_renew_before_days) {
        this.sc_renew_before_days = sc_renew_before_days;
    }

    public String getCc_renew_before_days() {
        return cc_renew_before_days;
    }

    public void setCc_renew_before_days(String cc_renew_before_days) {
        this.cc_renew_before_days = cc_renew_before_days;
    }

    public String getAi_renew_before_days() {
        return ai_renew_before_days;
    }

    public void setAi_renew_before_days(String ai_renew_before_days) {
        this.ai_renew_before_days = ai_renew_before_days;
    }

    public String getPsv_renew_before_days() {
        return psv_renew_before_days;
    }

    public void setPsv_renew_before_days(String psv_renew_before_days) {
        this.psv_renew_before_days = psv_renew_before_days;
    }

    public String getGp_renew_before_days() {
        return gp_renew_before_days;
    }

    public void setGp_renew_before_days(String gp_renew_before_days) {
        this.gp_renew_before_days = gp_renew_before_days;
    }

    public String getNp_renew_before_days() {
        return np_renew_before_days;
    }

    public void setNp_renew_before_days(String np_renew_before_days) {
        this.np_renew_before_days = np_renew_before_days;
    }

    public String getSc_rule_heading() {
        return sc_rule_heading;
    }

    public void setSc_rule_heading(String sc_rule_heading) {
        this.sc_rule_heading = sc_rule_heading;
    }

    public String getSc_formno_heading() {
        return sc_formno_heading;
    }

    public void setSc_formno_heading(String sc_formno_heading) {
        this.sc_formno_heading = sc_formno_heading;
    }

    public String getCc_rule_heading() {
        return cc_rule_heading;
    }

    public void setCc_rule_heading(String cc_rule_heading) {
        this.cc_rule_heading = cc_rule_heading;
    }

    public String getCc_formno_heading() {
        return cc_formno_heading;
    }

    public void setCc_formno_heading(String cc_formno_heading) {
        this.cc_formno_heading = cc_formno_heading;
    }

    public String getAitp_rule_heading() {
        return aitp_rule_heading;
    }

    public void setAitp_rule_heading(String aitp_rule_heading) {
        this.aitp_rule_heading = aitp_rule_heading;
    }

    public String getAitp_formno_heading() {
        return aitp_formno_heading;
    }

    public void setAitp_formno_heading(String aitp_formno_heading) {
        this.aitp_formno_heading = aitp_formno_heading;
    }

    public String getPsvp_rule_heading() {
        return psvp_rule_heading;
    }

    public void setPsvp_rule_heading(String psvp_rule_heading) {
        this.psvp_rule_heading = psvp_rule_heading;
    }

    public String getPsvp_formno_heading() {
        return psvp_formno_heading;
    }

    public void setPsvp_formno_heading(String psvp_formno_heading) {
        this.psvp_formno_heading = psvp_formno_heading;
    }

    public String getGp_rule_heading() {
        return gp_rule_heading;
    }

    public void setGp_rule_heading(String gp_rule_heading) {
        this.gp_rule_heading = gp_rule_heading;
    }

    public String getGp_formno_heading() {
        return gp_formno_heading;
    }

    public void setGp_formno_heading(String gp_formno_heading) {
        this.gp_formno_heading = gp_formno_heading;
    }

    public String getNp_rule_heading() {
        return np_rule_heading;
    }

    public void setNp_rule_heading(String np_rule_heading) {
        this.np_rule_heading = np_rule_heading;
    }

    public String getNp_formno_heading() {
        return np_formno_heading;
    }

    public void setNp_formno_heading(String np_formno_heading) {
        this.np_formno_heading = np_formno_heading;
    }

    public String getTemp_rule_heading() {
        return temp_rule_heading;
    }

    public void setTemp_rule_heading(String temp_rule_heading) {
        this.temp_rule_heading = temp_rule_heading;
    }

    public String getTemp_formno_heading() {
        return temp_formno_heading;
    }

    public void setTemp_formno_heading(String temp_formno_heading) {
        this.temp_formno_heading = temp_formno_heading;
    }

    public String getSpec_rule_heading() {
        return spec_rule_heading;
    }

    public void setSpec_rule_heading(String spec_rule_heading) {
        this.spec_rule_heading = spec_rule_heading;
    }

    public String getSpec_formno_heading() {
        return spec_formno_heading;
    }

    public void setSpec_formno_heading(String spec_formno_heading) {
        this.spec_formno_heading = spec_formno_heading;
    }

    public String getAuth_aitp_rule_heading() {
        return auth_aitp_rule_heading;
    }

    public void setAuth_aitp_rule_heading(String auth_aitp_rule_heading) {
        this.auth_aitp_rule_heading = auth_aitp_rule_heading;
    }

    public String getAuth_aitp_formno_heading() {
        return auth_aitp_formno_heading;
    }

    public void setAuth_aitp_formno_heading(String auth_aitp_formno_heading) {
        this.auth_aitp_formno_heading = auth_aitp_formno_heading;
    }

    public String getAuth_np_rule_heading() {
        return auth_np_rule_heading;
    }

    public void setAuth_np_rule_heading(String auth_np_rule_heading) {
        this.auth_np_rule_heading = auth_np_rule_heading;
    }

    public String getAuth_np_formno_heading() {
        return auth_np_formno_heading;
    }

    public void setAuth_np_formno_heading(String auth_np_formno_heading) {
        this.auth_np_formno_heading = auth_np_formno_heading;
    }

    public String getNote_in_footer() {
        return note_in_footer;
    }

    public void setNote_in_footer(String note_in_footer) {
        this.note_in_footer = note_in_footer;
    }

    public String getTemp_days_valid_from() {
        return temp_days_valid_from;
    }

    public void setTemp_days_valid_from(String temp_days_valid_from) {
        this.temp_days_valid_from = temp_days_valid_from;
    }

    public String getTemp_weeks_valid_from() {
        return temp_weeks_valid_from;
    }

    public void setTemp_weeks_valid_from(String temp_weeks_valid_from) {
        this.temp_weeks_valid_from = temp_weeks_valid_from;
    }

    public String getSpec_days_valid_from() {
        return spec_days_valid_from;
    }

    public int getSc_renew_after_days() {
        return sc_renew_after_days;
    }

    public void setSc_renew_after_days(int sc_renew_after_days) {
        this.sc_renew_after_days = sc_renew_after_days;
    }

    public int getCc_renew_after_days() {
        return cc_renew_after_days;
    }

    public void setCc_renew_after_days(int cc_renew_after_days) {
        this.cc_renew_after_days = cc_renew_after_days;
    }

    public int getAi_renew_after_days() {
        return ai_renew_after_days;
    }

    public void setAi_renew_after_days(int ai_renew_after_days) {
        this.ai_renew_after_days = ai_renew_after_days;
    }

    public int getPsv_renew_after_days() {
        return psv_renew_after_days;
    }

    public void setPsv_renew_after_days(int psv_renew_after_days) {
        this.psv_renew_after_days = psv_renew_after_days;
    }

    public int getGp_renew_after_days() {
        return gp_renew_after_days;
    }

    public void setGp_renew_after_days(int gp_renew_after_days) {
        this.gp_renew_after_days = gp_renew_after_days;
    }

    public int getNp_renew_after_days() {
        return np_renew_after_days;
    }

    public void setNp_renew_after_days(int np_renew_after_days) {
        this.np_renew_after_days = np_renew_after_days;
    }

    public void setSpec_days_valid_from(String spec_days_valid_from) {
        this.spec_days_valid_from = spec_days_valid_from;
    }

    public String getSpec_weeks_valid_from() {
        return spec_weeks_valid_from;
    }

    public void setSpec_weeks_valid_from(String spec_weeks_valid_from) {
        this.spec_weeks_valid_from = spec_weeks_valid_from;
    }

    public String getRenewal_of_permit_valid_from_flag() {
        return renewal_of_permit_valid_from_flag;
    }

    public void setRenewal_of_permit_valid_from_flag(String renewal_of_permit_valid_from_flag) {
        this.renewal_of_permit_valid_from_flag = renewal_of_permit_valid_from_flag;
    }

    public String getTemp_route_area() {
        return temp_route_area;
    }

    public void setTemp_route_area(String temp_route_area) {
        this.temp_route_area = temp_route_area;
    }

    public VmPermitStateConfig getPmt_state_config_dobj() {
        return pmt_state_config_dobj;
    }

    public void setPmt_state_config_dobj(VmPermitStateConfig pmt_state_config_dobj) {
        this.pmt_state_config_dobj = pmt_state_config_dobj;
    }

    public String getGenrate_ol_appl() {
        return genrate_ol_appl;
    }

    public void setGenrate_ol_appl(String genrate_ol_appl) {
        this.genrate_ol_appl = genrate_ol_appl;
    }

    public String getPmtcode() {
        return pmtcode;
    }

    public void setPmtcode(String pmtcode) {
        this.pmtcode = pmtcode;
    }

    public List<VmPermitStateMap> getDocfilter() {
        return docfilter;
    }

    public void setDocfilter(List<VmPermitStateMap> docfilter) {
        this.docfilter = docfilter;
    }

    public List<VmPermitFeeStateConfig> getFeeStatefilter() {
        return feeStatefilter;
    }

    public void setFeeStatefilter(List<VmPermitFeeStateConfig> feeStatefilter) {
        this.feeStatefilter = feeStatefilter;
    }

    public String getPermanent_permit_valid() {
        return permanent_permit_valid;
    }

    public void setPermanent_permit_valid(String permanent_permit_valid) {
        this.permanent_permit_valid = permanent_permit_valid;
    }

    public List getCatgdata() {
        return catgdata;
    }

    public void setCatgdata(List catgdata) {
        this.catgdata = catgdata;
    }

    public List getVhclassList() {
        return vhclassList;
    }

    public void setVhclassList(List vhclassList) {
        this.vhclassList = vhclassList;
    }

    public List getDocList() {
        return docList;
    }

    public void setDocList(List docList) {
        this.docList = docList;
    }

    public String[] getSelectedDocid() {
        return selectedDocid;
    }

    public void setSelectedDocid(String[] selectedDocid) {
        this.selectedDocid = selectedDocid;
    }

    public String getAllowed_surr_pur_cd() {
        return allowed_surr_pur_cd;
    }

    public void setAllowed_surr_pur_cd(String allowed_surr_pur_cd) {
        this.allowed_surr_pur_cd = allowed_surr_pur_cd;
    }

    public String getTemp_pmt_type() {
        return temp_pmt_type;
    }

    public void setTemp_pmt_type(String temp_pmt_type) {
        this.temp_pmt_type = temp_pmt_type;
    }

    public String getRenew_temp_pmt() {
        return renew_temp_pmt;
    }

    public void setRenew_temp_pmt(String renew_temp_pmt) {
        this.renew_temp_pmt = renew_temp_pmt;
    }

    public List getSur_pur_list() {
        return sur_pur_list;
    }

    public void setSur_pur_list(List sur_pur_list) {
        this.sur_pur_list = sur_pur_list;
    }

    public String[] getSelectedSurPurCd() {
        return selectedSurPurCd;
    }

    public void setSelectedSurPurCd(String[] selectedSurPurCd) {
        this.selectedSurPurCd = selectedSurPurCd;
    }

    public boolean isPanelFees_Slab_Render() {
        return panelFees_Slab_Render;
    }

    public void setPanelFees_Slab_Render(boolean panelFees_Slab_Render) {
        this.panelFees_Slab_Render = panelFees_Slab_Render;
    }

    public boolean isMasterButton() {
        return masterButton;
    }

    public void setMasterButton(boolean masterButton) {
        this.masterButton = masterButton;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSpl_pmt_route() {
        return spl_pmt_route;
    }

    public void setSpl_pmt_route(String spl_pmt_route) {
        this.spl_pmt_route = spl_pmt_route;
    }

    public String getRoute_flag() {
        return route_flag;
    }

    public void setRoute_flag(String route_flag) {
        this.route_flag = route_flag;
    }

    public String getNew_route_code() {
        return new_route_code;
    }

    public void setNew_route_code(String new_route_code) {
        this.new_route_code = new_route_code;
    }

    public String getNew_tloc() {
        return new_tloc;
    }

    public void setNew_tloc(String new_tloc) {
        this.new_tloc = new_tloc;
    }

    public boolean isShownewroute() {
        return shownewroute;
    }

    public void setShownewroute(boolean shownewroute) {
        this.shownewroute = shownewroute;
    }

    public boolean isDisableexistroute() {
        return disableexistroute;
    }

    public void setDisableexistroute(boolean disableexistroute) {
        this.disableexistroute = disableexistroute;
    }

    public String getNhOverlapping() {
        return nhOverlapping;
    }

    public void setNhOverlapping(String nhOverlapping) {
        this.nhOverlapping = nhOverlapping;
    }

    public String getNhOverlappingLength() {
        return nhOverlappingLength;
    }

    public void setNhOverlappingLength(String nhOverlappingLength) {
        this.nhOverlappingLength = nhOverlappingLength;
    }

    public List getStateList() {
        return stateList;
    }

    public void setStateList(List stateList) {
        this.stateList = stateList;
    }

    public String getState_to() {
        return state_to;
    }

    public void setState_to(String state_to) {
        this.state_to = state_to;
    }

    public boolean isShow_state_to() {
        return show_state_to;
    }

    public void setShow_state_to(boolean show_state_to) {
        this.show_state_to = show_state_to;
    }

    public boolean isShownewfrom() {
        return shownewfrom;
    }

    public void setShownewfrom(boolean shownewfrom) {
        this.shownewfrom = shownewfrom;
    }

    public String getNew_floc() {
        return new_floc;
    }

    public void setNew_floc(String new_floc) {
        this.new_floc = new_floc;
    }
}
