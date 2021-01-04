/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.Dealer;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.user_mgmt.dobj.UserAuthorityDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_temp_dobj;
import nic.vahan.form.dobj.TempRegDobj;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.NewImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.WorkBench;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.PrimeFaces;
import org.apache.log4j.Logger;
import org.primefaces.component.outputlabel.OutputLabel;
import org.primefaces.event.SelectEvent;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.form.bean.fancy.RetenRegnNo_dobj;
import nic.vahan.form.dobj.CdDobj;
import nic.vahan.form.dobj.OtherStateVehDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.user_mgmt.dobj.DealerMasterDobj;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.InspectionDobj;
import nic.vahan.form.dobj.ReflectiveTapeDobj;
import nic.vahan.form.dobj.SpeedGovernorDobj;
import nic.vahan.form.dobj.TmConfigurationOwnerIdentificationDobj;
import nic.vahan.form.dobj.fitness.TmConfigurationFitnessDobj;
import nic.vahan.form.impl.NewVehicleFitnessImpl;
import nic.vahan.form.impl.OwnerChoiceNoImpl;
import nic.vahan.form.impl.permit.PermitImpl;
import nic.vahan.form.impl.OwnerIdentificationImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.services.BiolicObj;
import nic.vahan.services.DlCovObj;
import nic.vahan.services.DlDetObj;
import nic.vahan.services.LlCovObj;
import nic.vahan.services.LlDetObj;
import nic.vahan.services.LlicenceObj;
import nic.vahan.services.ResponceList;
import nic.vahan.services.RetLLDetails;
import nic.vahan5.reg.rest.config.SpringContext;
import nic.vahan5.reg.rest.model.dobj.fancy.AdvanceRegnNoDobjModel;
import nic.vahan5.reg.rest.model.ContextMessageModel;
import nic.vahan5.reg.rest.model.ContextMessageModel.MessageContext;
import nic.vahan5.reg.rest.model.OwnerBeanModel;
import nic.vahan5.reg.rest.model.SessionVariablesModel;
import nic.vahan5.reg.rest.model.WrapperModel;
import nic.vahan5.util.VahanUtil;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@ManagedBean(name = "owner_bean")
@ViewScoped
public class OwnerBean implements Serializable {

    private WebClient.Builder webClientBuilder = SpringContext.getBean(WebClient.Builder.class);
    private static final Logger LOGGER = Logger.getLogger(OwnerBean.class);
    private List list_maker_model;
    private String appl_no = null;
    private String regn_no = null;
    private Date regn_dt = new Date();
    private Calendar regn_dt_cal = new Calendar();
    private List list_c_state;
    private List list_c_district;
    private List list_adv_state;
    private List list_adv_district;
    private List list_p_state;
    private List list_p_district;
    private List list_vh_class;
    private List list_vm_catg;
    private List list_maker;
    private List list_fuel;
    private boolean disableRegnType = true;
    private List list_regn_type;
    private List list_owner_cd;
    private List listOwnerCatg;
    private String state_cd = null;
    private List list_dealer_cd;
    private Date today = new Date();
    private Integer other_criteria = 0;
    private List list_other_criteria;
    private String imported_veh;
    private List list_imported_veh;
    private int norms;
    private List list_norms;
    private boolean disableSamePerm;
    private String vch_purchase_as;
    private List list_purchase_as;
    private Integer sale_amt;
    private String tax_mode;
    private List list_tax_mode;
    private Integer annual_income;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private Owner_dobj owner_dobj_prv;
    private Owner_dobj ownerDobj;
    private SpeedGovernorDobj speedGovPrev = null;
    private String vehType = "-1";
    private List list_veh_type;
    private Date currentDate = DateUtil.parseDate(DateUtil.getCurrentDate());
    private boolean samePermAddress;
    private boolean exArmyVehicle_Visibility_tab = false;
    private boolean importedVehicle_Visibility_tab = false;
    private boolean axleDetail_Visibility_tab = false;
    private boolean cngDetails_Visibility_tab = false;
    private boolean linked_regnNo_tab = false;
    private boolean samePermAdd_Rend = true;
    private Date fit_upto = null;
    private Date regn_upto = null;
    private Owner_temp_dobj temp_dobj;
    private Owner_temp_dobj temp_dobj_prev;
    private boolean renderTempOwner;
    private List list_office_to = new ArrayList();
    private OwnerIdentificationDobj owner_identification = new OwnerIdentificationDobj();
    private AdvanceRegnNo_dobj advanceRegNoDobj = null;
    private boolean blnAdvancedRegNo;
    private boolean blnPgAdvancedRegNo;
    private boolean blnDisableCurrentState = true;
    private TempRegDobj tempReg;
    private TempRegDobj tempReg_prev;
    private boolean blnRegnTypeTemp = false;
    private boolean blnDisableRegnTypeTemp = false;
    private UserAuthorityDobj authorityDobj = null;
    private boolean modelEditable = false;
    private boolean renderModelEditable = false;
    private boolean renderModelSelectMenu = true;
    private boolean disableOwnerSr = true;
    private boolean disableFName = true;
    private boolean renderInspectionPanel = false;
    private boolean renderInspectionPanelNT = false;
    private boolean isDealer = false;
    private OtherStateVehDobj otherStateDobj;
    private OtherStateVehDobj otherStateDobjPrev;
    private List list_ac_audio_video_fitted;
    private String advanceRegnAllotted;
    private String regnNoAlloted;
    private boolean regnNoAllotedPanel;
    private String seriesAvailMessage;
    private String AdvRegnRetenRadiobtn = "";
    private RetenRegnNo_dobj retenRegNoDobj = null;
    private boolean panelRadioBtn;
    private boolean panelAdvAppdtl;
    private boolean panelAdvRetAppdtl;
    private boolean panelAdvAdddtl;
    private boolean panelAdvRetAdddtl;
    private String advanceAllotedLabel;
    TmConfigurationDobj tmConfDobj = null;
    private String tradeCertExpireMess = null;
    private CdDobj cdDobjClone = null;
    private final String TEMP_OTHER_STATE = "OS";
    private final String TEMP_OTHER_RTO = "OR";
    private final String TEMP_BODY_BUILDING = "BB";
    private final String TEMP_SAME_RTO = "SR";
    private final String OEM_TO_DEALER_LOCATION = "OM";
    private final String DEMONSTRATION_WITHIN_SAME_RTO = "DS";
    private final String DEMONSTRATION_WITHIN_DIFF_RTO = "DO";
    private final String STOCK_TRANSFER = "ST";
    private boolean renderTempBodyBuilding = false;
    private boolean renderTempOther = false;
    private List listTempReasons = null;
    private List list_c_state_to;
    private String isHomologationData;
    private boolean toRetention = false;
    private boolean permitPanel;
    private boolean isPmtTypeRqrd;
    private boolean isPmtCatgRqrd;
    private boolean disableTempStateTo = false;
    private boolean disableOwnerCd = false;
    private boolean disablePurchaseDt = false;
    private boolean disableOwnerName;
    ///// Vehicle Tab Disable Variables
    private boolean vehicleComponentEditable = false;
    private boolean dealerSaleAmt = false;
    private boolean disableMaker = false;
    private boolean disableMakerModel = false;
    private boolean disableDealerCd = false;
    private boolean disableChasiNo = false;
    private boolean disableFuelType = false;
    private boolean disableEngineNo = false;
    private boolean disableSeatCap = false;
    private boolean disableStandCap = false;
    private boolean disableSleeperCap = false;
    private boolean disableNoOfCyl = false;
    private boolean disableUnLdWtCap = false;
    private boolean disableLdWtCap = false;
    private boolean disableHorsePower = false;
    private boolean disableNorms = false;
    private boolean disableVchPurchaseAs = false;
    private boolean disableColor = false;
    private boolean disableWheelBase = false;
    private boolean disableCubicCapacity = false;
    private boolean disableManuMonth = false;
    private boolean disableManuYr = false;
    private boolean disableLength = false;
    private boolean disableWidth = false;
    private boolean disableHeight = false;
    private int homoLdWt = 0;
    private int homoUnLdWt = 0;
    private boolean renderValidityPanel = false;
    private boolean renderTaxPanel = true;
    private String linkedVehDtls;
    private boolean renderTaxExemption;
    private boolean taxExemption;
    private int purCd;
    private int tempLdWt = 0;
    private int tempUnLdWt = 0;
    private boolean renderSpeedGov = false;
    private boolean renderIsSpeedGov = false;
    private boolean disableOtherState = false;
    private boolean renderPrevCurrentAdd = false;
    private String prevCurrAdd = null;
    private Owner_dobj homoDobj;
    private boolean isScrapVehicleNORetain;
    private InspectionDobj inspDobj = new InspectionDobj();
    // for orissa purchase date is current date or one day before.
    private Date minPurchaseDate = null;
    private boolean renderGCW = false;
    private String previousRegnType = "";
    private boolean renderHpDtls;
    private boolean renderVehDtls;
    private boolean renderOwnerDtlsPartialBtn;
    private boolean renderVehicleDtlsPartialBtn;
    private boolean renderChasiOs;
    private String empCodeLoggedIn = null;
    private String stateCodeSelected = null;
    private int offCodeSelected;
    private String userCatgForLoggedInUser = null;
    private int actionCodeSelected;
    private boolean renderNewLoi = false;
    private List listOwnerCdDept;
    private List listSpeedGovTypes;
    private boolean renderReflectiveTape = false;
    private boolean renderIsReflectiveTape = false;
    private ReflectiveTapeDobj reflectiveTapeDobjPrev = null;
    private boolean disablePermanentAddress;
    private TmConfigurationFitnessDobj tmConfigFitnessDobj = null;
    private boolean renderGenerateOTPBtn = false;
    private boolean renderAuthenticateBtn = true;
    private String sendOTP;
    private String enteredOTP;
    private boolean renderedOTPFields = false;
    private boolean renderedConfirmBtn = false;
    private BiolicObj llServiceResponse = null;
    private String errorMsg = null;
    private String licenseType;
    private String DOBDate;
    private boolean pushBkSeatRender = false;
    private boolean disableAdvanceRegnAllotted = false;
    //For Owner Admin
    private String yearRange = "c-10:c+10";
    private String saleAmtInWords;
    private boolean render_vltd_details = false;
    private boolean auctionVisibilityTab = false;
    private boolean renderOwnerDisclaimer = false;
    private boolean verifyOwnerDisclaimer = false;
    //addforBS4
    private boolean regnUptoDateDisable = false;
    private boolean fitUptoDateDisable = false;
    private boolean registrationDateDisable = false;
    private boolean renderMultiRegionList = false;
    private boolean disableMultiRegionList = false;
    private int offCdToOfOwnerDobj;
    private String retRcptMsg;
    private boolean renderAdvanceRegnPanel = false;

    public OwnerBean() {

        empCodeLoggedIn = Util.getEmpCode();
        stateCodeSelected = Util.getUserStateCode();
        userCatgForLoggedInUser = Util.getUserCategory();
        if (Util.getSelectedSeat() != null) {
            offCodeSelected = Util.getSelectedSeat().getOff_cd();
            actionCodeSelected = Util.getSelectedSeat().getAction_cd();
        }
        if (empCodeLoggedIn == null || stateCodeSelected == null || userCatgForLoggedInUser == null || (offCodeSelected == 0 && !Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN)) || actionCodeSelected == 0) {//forstateadmin
            return;
        }

        list_c_district = new ArrayList();
        list_c_state = new ArrayList();
        list_p_district = new ArrayList();
        list_p_state = new ArrayList();
        list_owner_cd = new ArrayList();
        list_regn_type = new ArrayList();
        list_dealer_cd = new ArrayList();
        list_fuel = new ArrayList();
        list_vh_class = new ArrayList();
        list_vm_catg = new ArrayList();
        list_maker = new ArrayList();
        list_maker_model = new ArrayList();
        list_adv_state = new ArrayList();
        list_adv_district = new ArrayList();
        list_ac_audio_video_fitted = new ArrayList();
        list_c_state_to = new ArrayList();
        listOwnerCatg = new ArrayList();
        ownerDobj = new Owner_dobj();
        ownerDobj.setSpeedGovernerList(new ArrayList());
        ownerDobj.setRegn_type(TableConstants.VM_REGN_TYPE_NEW);
        listTempReasons = new ArrayList();
        String[][] data;
        list_c_state.clear();
        list_p_state.clear();
        list_adv_state.clear();
        listTempReasons.clear();
        try {
            list_c_state = MasterTableFiller.getStateList();
            list_p_state = MasterTableFiller.getStateList();
            list_adv_state = MasterTableFiller.getStateList();

            //state configuration for fitness according to the States rules
            FitnessImpl fitness_Impl = new FitnessImpl();
            tmConfigFitnessDobj = fitness_Impl.getFitnessConfiguration(stateCodeSelected);
            tmConfDobj = Util.getTmConfiguration();

            if (stateCodeSelected != null) {
                ownerDobj.setC_state(stateCodeSelected);
                list_c_district.clear();
                list_p_district.clear();
                list_c_district = MasterTableFiller.getDistrictList(stateCodeSelected);
                list_c_state.clear();
                list_p_state.clear();
                list_c_state = MasterTableFiller.getStateList();
                list_p_state = MasterTableFiller.getStateList();
                list_c_state_to = MasterTableFiller.getStateList();
                if (stateCodeSelected != null && !stateCodeSelected.equals("")) {
                    minPurchaseDate = DateUtil.parseDate("01-01-1900");
                    if (userCatgForLoggedInUser != null && userCatgForLoggedInUser.equals(TableConstants.USER_CATG_DEALER) && stateCodeSelected.equals("OR") && (actionCodeSelected == (TableConstants.TM_ROLE_DEALER_TEMP_APPL) || actionCodeSelected == (TableConstants.TM_ROLE_DEALER_NEW_APPL))) {
                        minPurchaseDate = ServerUtil.dateRange(currentDate, 0, 0, -1);
                    } else if (userCatgForLoggedInUser != null && userCatgForLoggedInUser.equals(TableConstants.USER_CATG_DEALER) && stateCodeSelected.equals("GJ") && actionCodeSelected == (TableConstants.TM_ROLE_DEALER_TEMP_APPL)) {
                        minPurchaseDate = ServerUtil.dateRange(currentDate, 0, 0, -2);
                    }
                }
            }
            Long user_cd = 0L;

            if (empCodeLoggedIn != null) {
                user_cd = Long.parseLong(empCodeLoggedIn);
            }
            data = MasterTableFiller.masterTables.VM_OWCODE.getData();
            for (int i = 0; i < data.length; i++) {
                if (tmConfDobj != null && tmConfDobj.getTmConfigDealerDobj() != null && !"ALL".equals(tmConfDobj.getTmConfigDealerDobj().getAllowedOwnerCode()) && TableConstants.User_Dealer.equals(userCatgForLoggedInUser)) {
                    if (tmConfDobj.getTmConfigDealerDobj().getAllowedOwnerCode().contains("," + data[i][0] + ",")) {
                        list_owner_cd.add(new SelectItem(data[i][0], data[i][1]));
                    }
                } else {
                    list_owner_cd.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
            data = MasterTableFiller.masterTables.VM_SPEEDGOV_MANUFACTURE.getData();
            for (int i = 0; i < data.length; i++) {
                ownerDobj.getSpeedGovernerList().add(new SelectItem(data[i][0], data[i][1]));
            }
            data = MasterTableFiller.masterTables.VM_REGN_TYPE.getData();
            for (int i = 0; i < data.length; i++) {
                list_regn_type.add(new SelectItem(data[i][0], data[i][1]));
            }
            //Master Filler for Owner Category
            data = MasterTableFiller.masterTables.VM_OWCATG.getData();
            for (int i = 0; i < data.length; i++) {
                listOwnerCatg.add(new SelectItem(data[i][0], data[i][1]));
            }
            //Master Filler for Temp Reason
            data = MasterTableFiller.masterTables.VM_TEMP_REGN_REASON.getData();
            for (int i = 0; i < data.length; i++) {
                if ((data[i][2].split(",")).length > 1) {
                    String[] statecdArray = data[i][2].split(",");
                    for (String statecd : statecdArray) {
                        if (statecd.equals(stateCodeSelected)) {
                            listTempReasons.add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                } else {
                    if (data[i][2].equals(stateCodeSelected) || data[i][2].equals("ALL")) {
                        listTempReasons.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
            }
            if (userCatgForLoggedInUser != null && userCatgForLoggedInUser.equals(TableConstants.User_Dealer)) {
                setAdvanceRegnAllotted("No");
                Map makerAndDealerDetail = OwnerImpl.getDealerDetail(user_cd);
                if (makerAndDealerDetail != null && makerAndDealerDetail.get("dealer_cd") != null && makerAndDealerDetail.get("dealer_name") != null) {
                    list_dealer_cd.add(new SelectItem(makerAndDealerDetail.get("dealer_cd"), makerAndDealerDetail.get("dealer_name").toString()));
                }
            } else {
                List<Dealer> listDealer = ServerUtil.getDealerList(stateCodeSelected, offCodeSelected);
                for (Dealer dl : listDealer) {
                    list_dealer_cd.add(new SelectItem(dl.getDealer_cd(), dl.getDealer_name()));
                }
            }
            list_maker = OwnerImpl.getAssignedMakerList(user_cd);
            list_norms = new ArrayList();
            data = MasterTableFiller.masterTables.VM_NORMS.getData();
            for (int i = 0; i < data.length; i++) {
                list_norms.add(new SelectItem(data[i][0], data[i][1]));
            }
            list_tax_mode = new ArrayList();
            data = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
            for (int i = 0; i < data.length; i++) {
                list_tax_mode.add(new SelectItem(data[i][0], data[i][1]));
            }
            data = MasterTableFiller.masterTables.VM_FUEL.getData();
            for (int i = 0; i < data.length; i++) {
                list_fuel.add(new SelectItem(data[i][0], data[i][1]));
            }
            list_other_criteria = new ArrayList();
            list_other_criteria = MasterTableFiller.getOtherCriteriaList(stateCodeSelected);
            list_imported_veh = new ArrayList();
            list_imported_veh.add(new SelectItem("N", "No"));
            list_imported_veh.add(new SelectItem("Y", "Yes"));
            list_purchase_as = new ArrayList();
            list_purchase_as.add(new SelectItem("B", "Fully Built"));
            list_purchase_as.add(new SelectItem("C", "Drive Away Chasis"));
            list_veh_type = new ArrayList();
            authorityDobj = Util.getUserAuthority();
            data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
            setListSpeedGovTypes(OwnerImpl.getListSpeedGovTypes());

            if (authorityDobj != null) {
                if (authorityDobj.getVehType() == TableConstants.VM_VEHTYPE_TRANSPORT) {
                    list_veh_type.add(new SelectItem("1", "Transport"));
                    axleDetail_Visibility_tab = true;
                    permitPanel = true;
                    getOwnerDobj().setVehType(TableConstants.VM_VEHTYPE_TRANSPORT);
                    vehType = String.valueOf(TableConstants.VM_VEHTYPE_TRANSPORT);
                } else if (authorityDobj.getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                    list_veh_type.add(new SelectItem("2", "Non-Transport"));
                    permitPanel = false;
                    getOwnerDobj().setVehType(TableConstants.VM_VEHTYPE_NON_TRANSPORT);
                    vehType = String.valueOf(TableConstants.VM_VEHTYPE_NON_TRANSPORT);
                } else if (authorityDobj.getVehType() == TableConstants.VM_VEHTYPE_ALL) {
                    list_veh_type.add(new SelectItem("-1", "--SELECT--"));
                    list_veh_type.add(new SelectItem("2", "Non-Transport"));
                    list_veh_type.add(new SelectItem("1", "Transport"));
                }
                if (authorityDobj.getVehType() == TableConstants.VM_VEHTYPE_TRANSPORT
                        || authorityDobj.getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                    list_vh_class.clear();
                    for (int i = 0; i < data.length; i++) {
                        if (data[i][2].equals(String.valueOf(authorityDobj.getVehType()))) {
                            for (Object obj : authorityDobj.getSelectedVehClass()) {
                                if (String.valueOf(obj).equalsIgnoreCase(data[i][0])) {
                                    list_vh_class.add(new SelectItem(data[i][0], data[i][1]));
                                }
                            }
                        }
                    }
                } else if (authorityDobj.getVehType() == TableConstants.VM_VEHTYPE_ALL) {
                    for (int i = 0; i < data.length; i++) {
                        for (Object obj : authorityDobj.getSelectedVehClass()) {
                            if (String.valueOf(obj).equalsIgnoreCase("ANY")) {
                                list_vh_class.add(new SelectItem(data[i][0], data[i][1]));
                            } else {
                                if (String.valueOf(obj).equalsIgnoreCase(data[i][0])) {
                                    list_vh_class.add(new SelectItem(data[i][0], data[i][1]));
                                }
                            }
                        }
                    }
                }
            }
            list_ac_audio_video_fitted.add(new SelectItem("N", "NO"));
            list_ac_audio_video_fitted.add(new SelectItem("Y", "YES"));
            setAdvRegnRetenRadiobtn("advregnno");
            setPanelRadioBtn(false);
            setPanelAdvRetAppdtl(false);
            setPanelAdvAppdtl(true);
            setPanelAdvAdddtl(true);
            if (tmConfDobj != null && tmConfDobj.isTo_retention()) {
                toRetention = true;
                setAdvanceAllotedLabel("Advance Registration / Retention Number Alloted");
            } else {
                setAdvanceAllotedLabel("Advance Registration Number Alloted");
            }

            ownerDobj.getRegionList().clear();
            String[][] data1 = MasterTableFiller.masterTables.VM_REGION.getData();
            for (int i = 0; i < data1.length; i++) {
                if (Util.getUserStateCode().equalsIgnoreCase(data1[i][0]) && Util.getUserOffCode() == Integer.parseInt(data1[i][1])) {
                    ownerDobj.getRegionList().add(new SelectItem(data1[i][2], data1[i][3]));
                }
            }

        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, vex.getMessage(), vex.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    public void onSelectRetenRadiobtn() {

        String radioBtnvalue = AdvRegnRetenRadiobtn;
        if (radioBtnvalue.equals("retenno")) {
            setPanelAdvAppdtl(false);
            setPanelAdvRetAppdtl(true);
            setPanelAdvRetAdddtl(true);
            setPanelAdvAdddtl(false);
            setAdvRegnRetenRadiobtn("retenno");
            setBlnAdvancedRegNo(false);
            RetenRegnNo_dobj dobj = new RetenRegnNo_dobj();
            setRetenRegNoDobj(dobj);
            setAdvanceRegNoDobj(null);

            //PrimeFaces.current().ajax().update("tabviewRender");
            PrimeFaces.current().executeScript("PF('wd_choiceno').show();");
        } else {
            setPanelAdvAppdtl(true);
            setPanelAdvRetAppdtl(false);
            setPanelAdvAdddtl(true);
            setPanelAdvRetAdddtl(false);
            setAdvRegnRetenRadiobtn("advregnno");
            setBlnAdvancedRegNo(true);
            AdvanceRegnNo_dobj dobj = new AdvanceRegnNo_dobj();
            setAdvanceRegNoDobj(dobj);
            setRetenRegNoDobj(null);
            //PrimeFaces.current().ajax().update("tabviewRender");
            PrimeFaces.current().executeScript("PF('wd_choiceno').show();");
        }
    }

    public void purchaseDateListener() {
        Date minPurchaseDate = DateUtil.parseDate(DateUtil.getCurrentDate());
        if (ownerDobj.getPurchase_dt() != null) {
            minPurchaseDate = DateUtil.parseDate(DateUtils.getDateInDDMMYYYY(ownerDobj.getPurchase_dt()));
        }
        RetroFittingDetailsBean cngBean = (RetroFittingDetailsBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("retroFittingDetailsBean");
        if (cngBean != null) {
            if (cngBean.getCal_install_dt() != null && cngBean.getCal_install_dt().before(minPurchaseDate)) {
                cngBean.setMinDate(DateUtil.parseDate(DateUtils.getDateInDDMMYYYY(cngBean.getCal_install_dt())));
            } else {
                cngBean.setMinDate(minPurchaseDate);
            }
            if (cngBean.getCal_hydro_dt() != null && cngBean.getCal_hydro_dt().before(minPurchaseDate)) {
                cngBean.setMinHydroDate(DateUtil.parseDate(DateUtils.getDateInDDMMYYYY(cngBean.getCal_hydro_dt())));
            } else {
                cngBean.setMinHydroDate(minPurchaseDate);
            }
        }
        HpaBean hpaBean = (HpaBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("hpa_bean");
        if (hpaBean != null) {
            if (hpaBean.getHpaDobj() != null && hpaBean.getHpaDobj().getFrom_dt() != null && hpaBean.getHpaDobj().getFrom_dt().before(minPurchaseDate)) {
                hpaBean.setMinDate(DateUtil.parseDate(DateUtils.getDateInDDMMYYYY(hpaBean.getHpaDobj().getFrom_dt())));
            } else {
                hpaBean.setMinDate(minPurchaseDate);
            }
        }
        InsBean insBean = (InsBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("ins_bean");
        if (insBean != null) {
            if (insBean.getIns_from() != null && insBean.getIns_from().before(minPurchaseDate)) {
                insBean.setMin_dt(DateUtil.parseDate(DateUtils.getDateInDDMMYYYY(insBean.getIns_from())));
            } else {
                insBean.setMin_dt(ServerUtil.dateRange(minPurchaseDate, 0, 0, -15));
            }
        }
    }

    public void validatePurchaseDate(Date appl_dt) throws VahanException {
        if (getOwnerDobj().getPurchase_dt() != null && appl_dt != null) {
            if (getOwnerDobj().getPurchase_dt().after(appl_dt)) {
                throw new VahanException("Purchase Date [" + DateUtil.parseDateToString(ownerDobj.getPurchase_dt()) + "] should not be grater than Application Date [" + DateUtil.parseDateToString(appl_dt) + "].");
            }
        } else {
            throw new VahanException("Purchase date should not be blank. please select purchase date.");
        }
    }

    //this method is used for setting the values form database to form_owner_dtls and form_owner_dtls
    public void set_Owner_appl_dobj_to_bean(Owner_dobj owner_dobj) throws VahanException {

        if (owner_dobj == null || this.getOwnerDobj() == null) {
            return;
        }

        this.setAppl_no(owner_dobj.getAppl_no());
        this.setRegn_no(owner_dobj.getRegn_no());
        this.setRegn_dt(owner_dobj.getRegn_dt());
        this.getOwnerDobj().setRegn_dt(owner_dobj.getRegn_dt());
        getOwnerDobj().setPurchase_dt(owner_dobj.getPurchase_dt());
        getOwnerDobj().setOwner_sr(owner_dobj.getOwner_sr());
        getOwnerDobj().setOwner_name(owner_dobj.getOwner_name());
        getOwnerDobj().setF_name(owner_dobj.getF_name());
        getOwnerDobj().setC_add1(owner_dobj.getC_add1());
        getOwnerDobj().setC_add2(owner_dobj.getC_add2());
        this.getOwnerDobj().setC_add3(owner_dobj.getC_add3());
        this.getOwnerDobj().setC_state(owner_dobj.getC_state());
        this.getOwnerDobj().setC_district(owner_dobj.getC_district());
        this.getOwnerDobj().setC_pincode(owner_dobj.getC_pincode());
        this.getOwnerDobj().setP_add1(owner_dobj.getP_add1());
        this.getOwnerDobj().setP_add2(owner_dobj.getP_add2());
        this.getOwnerDobj().setP_add3(owner_dobj.getP_add3());
        this.getOwnerDobj().setP_state(owner_dobj.getP_state());
        this.getOwnerDobj().setC_district_name(owner_dobj.getC_district_name());
        this.getOwnerDobj().setC_state_name(owner_dobj.getC_state_name());

//        String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
//        list_p_district.clear();
//        for (int i = 0; i < data.length; i++) {
//            if (data[i][2].trim().equals(this.getOwnerDobj().getP_state())) {
//                list_p_district.add(new SelectItem(data[i][0], data[i][1]));
//            }
//        }
        this.getOwnerDobj().setP_district(owner_dobj.getP_district());
        this.getOwnerDobj().setP_pincode(owner_dobj.getP_pincode());
        getOwnerDobj().setOwner_cd(owner_dobj.getOwner_cd());
        getOwnerDobj().setRegn_type(owner_dobj.getRegn_type());
        getOwnerDobj().setVh_class(owner_dobj.getVh_class());
        this.getOwnerDobj().setChasi_no(owner_dobj.getChasi_no());
        this.getOwnerDobj().setEng_no(owner_dobj.getEng_no());
        this.getOwnerDobj().setMaker(owner_dobj.getMaker());
        this.getOwnerDobj().setMaker_model(owner_dobj.getMaker_model());
        this.getOwnerDobj().setBody_type(owner_dobj.getBody_type());
        this.getOwnerDobj().setNo_cyl(owner_dobj.getNo_cyl());
        this.getOwnerDobj().setHp(owner_dobj.getHp());
        this.getOwnerDobj().setSeat_cap(owner_dobj.getSeat_cap());
        this.getOwnerDobj().setStand_cap(owner_dobj.getStand_cap());
        this.getOwnerDobj().setSleeper_cap(owner_dobj.getSleeper_cap());
        this.getOwnerDobj().setUnld_wt(owner_dobj.getUnld_wt());
        this.getOwnerDobj().setLd_wt(owner_dobj.getLd_wt());
        this.getOwnerDobj().setGcw(owner_dobj.getGcw());
        this.getOwnerDobj().setFuel(owner_dobj.getFuel());
        this.getOwnerDobj().setColor(owner_dobj.getColor());
        this.getOwnerDobj().setManu_mon(owner_dobj.getManu_mon());
        this.getOwnerDobj().setManu_yr(owner_dobj.getManu_yr());
        this.setNorms(owner_dobj.getNorms());
        this.getOwnerDobj().setWheelbase(owner_dobj.getWheelbase());
        this.getOwnerDobj().setCubic_cap(owner_dobj.getCubic_cap());
        this.getOwnerDobj().setFloor_area(owner_dobj.getFloor_area());
        this.getOwnerDobj().setAc_fitted(owner_dobj.getAc_fitted());
        this.getOwnerDobj().setAudio_fitted(owner_dobj.getAudio_fitted());
        this.getOwnerDobj().setVideo_fitted(owner_dobj.getVideo_fitted());
        this.setVch_purchase_as(owner_dobj.getVch_purchase_as());
        this.getOwnerDobj().setVch_catg(owner_dobj.getVch_catg() != null ? owner_dobj.getVch_catg().trim() : "");
        this.getOwnerDobj().setDealer_cd(owner_dobj.getDealer_cd());
        this.setSale_amt(owner_dobj.getSale_amt());
        this.setAnnual_income(owner_dobj.getAnnual_income());
        this.getOwnerDobj().setLaser_code(owner_dobj.getLaser_code());
        this.getOwnerDobj().setGarage_add(owner_dobj.getGarage_add());
        this.getOwnerDobj().setLength(owner_dobj.getLength());
        this.getOwnerDobj().setWidth(owner_dobj.getWidth());
        this.getOwnerDobj().setHeight(owner_dobj.getHeight());
        this.setState_cd(owner_dobj.getState_cd());
        this.setImported_veh(owner_dobj.getImported_vch());
        this.setOther_criteria(owner_dobj.getOther_criteria());
        this.setFit_upto(owner_dobj.getFit_upto());
        this.setRegn_upto(owner_dobj.getRegn_upto());
        this.getOwnerDobj().setFit_upto(owner_dobj.getFit_upto());
        this.getOwnerDobj().setRegn_upto(owner_dobj.getRegn_upto());
        this.getOwnerDobj().setPmt_type(owner_dobj.getPmt_type());
        this.getOwnerDobj().setPmt_catg(owner_dobj.getPmt_catg());
        this.getOwnerDobj().setServicesType(owner_dobj.getServicesType());
        this.getOwnerDobj().setRegion_covered(owner_dobj.getRegion_covered());
        this.getOwnerDobj().setRqrd_tax_modes(owner_dobj.getRqrd_tax_modes());
        this.getOwnerDobj().setLinkedRegnNo(owner_dobj.getLinkedRegnNo());
        this.getOwnerDobj().setVch_purchase_as(owner_dobj.getVch_purchase_as());
        this.getOwnerDobj().setModelManuLocCode(owner_dobj.getModelManuLocCode());
        this.getOwnerDobj().setModelManuLocCodeDescr(owner_dobj.getModelManuLocCodeDescr());
        this.getOwnerDobj().setModelNameOnTAC(owner_dobj.getModelNameOnTAC());
        this.getOwnerDobj().setNorms(owner_dobj.getNorms());
        this.getOwnerDobj().setSale_amt(owner_dobj.getSale_amt());
        this.getOwnerDobj().setFasTagId(owner_dobj.getFasTagId());
        if (owner_dobj.getOwnerDetailsDo() != null) {
            this.getOwnerDobj().setOwnerDetailsDo(owner_dobj.getOwnerDetailsDo());
        }

        if (owner_dobj.getOwner_identity() != null) {
            this.owner_identification.setMobile_no(owner_dobj.getOwner_identity().getMobile_no());
            this.owner_identification.setEmail_id(owner_dobj.getOwner_identity().getEmail_id());
            this.owner_identification.setPan_no(owner_dobj.getOwner_identity().getPan_no());
            this.owner_identification.setAadhar_no(owner_dobj.getOwner_identity().getAadhar_no());
            this.owner_identification.setPassport_no(owner_dobj.getOwner_identity().getPassport_no());
            this.owner_identification.setRation_card_no(owner_dobj.getOwner_identity().getRation_card_no());
            this.owner_identification.setVoter_id(owner_dobj.getOwner_identity().getVoter_id());
            this.owner_identification.setDl_no(owner_dobj.getOwner_identity().getDl_no());
            this.owner_identification.setOwnerCatg(owner_dobj.getOwner_identity().getOwnerCatg());
            this.owner_identification.setOwnerCdDept(owner_dobj.getOwner_identity().getOwnerCdDept());
            setListOwnerCdDept(OwnerIdentificationImpl.getOwnerCatgDepts(Util.getUserStateCode(), owner_dobj.getOwner_cd()));

        }

        if (owner_dobj.getTempReg() != null) {
            this.tempReg = owner_dobj.getTempReg();
        }
//KL PUSHBACK TARUN
        this.getOwnerDobj().setPush_bk_seat(owner_dobj.getPush_bk_seat());
        this.getOwnerDobj().setOrdinary_seat(owner_dobj.getOrdinary_seat());
        String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].trim().equals(String.valueOf(owner_dobj.getVh_class()))) {
                if (data[i][2].equals(String.valueOf(TableConstants.VM_VEHTYPE_TRANSPORT))) {
                    setVehType(String.valueOf(TableConstants.VM_VEHTYPE_TRANSPORT));
                    break;
                } else {
                    setVehType(String.valueOf(TableConstants.VM_VEHTYPE_NON_TRANSPORT));
                    break;
                }
            }
        }

        if (owner_dobj.getOtherStateVehDobj() != null) {
            getOwnerDobj().setRegn_dt(owner_dobj.getRegn_dt());
            getOwnerDobj().setRegn_upto(owner_dobj.getRegn_upto());
            getOwnerDobj().setFit_upto(owner_dobj.getFit_upto());
        }

        if (owner_dobj.getCdDobj() != null) {
            getOwnerDobj().setCdDobj(owner_dobj.getCdDobj());
        }

        if (owner_dobj.getSpeedGovernorDobj() != null) {
            getOwnerDobj().setSpeedGovernorDobj(owner_dobj.getSpeedGovernorDobj());
        }

        if (owner_dobj.getReflectiveTapeDobj() != null) {
            getOwnerDobj().setReflectiveTapeDobj(owner_dobj.getReflectiveTapeDobj());
        }

        if (owner_dobj.getVehicleTrackingDetailsDobj() != null) {
            getOwnerDobj().setVehicleTrackingDetailsDobj(owner_dobj.getVehicleTrackingDetailsDobj());
        }

        if (owner_dobj.getAuctionDobj() != null) {
            this.getOwnerDobj().setAuctionDobj(owner_dobj.getAuctionDobj());
            this.getOwnerDobj().setRegn_dt(owner_dobj.getRegn_dt());
        }
        vehC_StateListener(null);
        vehP_StateListener(null);
        vehMakerListener(null);
        //vehModelListener(null);
        vehTypeListener();
        vehClassListener();
        //vehCatgListener(null);
    }

    public Owner_dobj setOwnerTempDetails(Owner_dobj ownerDobj, Owner_dobj ownerTempDobj) throws VahanException {
        try {
            ownerDobj.setOwner_name(ownerTempDobj.getOwner_name());
            ownerDobj.setF_name(ownerTempDobj.getF_name());
            ownerDobj.setOwner_cd(ownerTempDobj.getOwner_cd());
            ownerDobj.setDealer_cd(ownerTempDobj.getDealer_cd());
            ownerDobj.setC_add1(ownerTempDobj.getC_add1());
            ownerDobj.setC_add2(ownerTempDobj.getC_add2());
            ownerDobj.setC_add3(ownerTempDobj.getC_add3());
            ownerDobj.setC_state(ownerTempDobj.getC_state());
            ownerDobj.setC_district(ownerTempDobj.getC_district());
            ownerDobj.setC_pincode(ownerTempDobj.getC_pincode());
            ownerDobj.setP_add1(ownerTempDobj.getP_add1());
            ownerDobj.setP_add2(ownerTempDobj.getP_add2());
            ownerDobj.setP_add3(ownerTempDobj.getP_add3());
            ownerDobj.setP_state(ownerTempDobj.getP_state());
            ownerDobj.setP_district(ownerTempDobj.getP_district());
            ownerDobj.setP_pincode(ownerTempDobj.getP_pincode());
            ownerDobj.setVehType(ownerTempDobj.getVehType());
            ownerDobj.setVh_class(ownerTempDobj.getVh_class());
            ownerDobj.setVch_catg(ownerTempDobj.getVch_catg());
            ownerDobj.setRegn_type(ownerTempDobj.getRegn_type());
            ownerDobj.setOther_criteria(ownerTempDobj.getOther_criteria());
            ownerDobj.setPurchase_dt(ownerTempDobj.getPurchase_dt());
            ownerDobj.setImportedVehDeleteFlag(ownerTempDobj.isImportedVehDeleteFlag());
            ownerDobj.setImported_vch(ownerTempDobj.getImported_vch());
            ownerDobj.setTempReg(ownerTempDobj.getTempReg());
            ownerDobj.setOwner_identity(ownerTempDobj.getOwner_identity());
            ownerDobj.setInsDobj(ownerTempDobj.getInsDobj());
            ownerDobj.setCng_dobj(ownerTempDobj.getCng_dobj());
            ownerDobj.setAxleDobj(ownerTempDobj.getAxleDobj());
            ownerDobj.setListHpaDobj(ownerTempDobj.getListHpaDobj());
            ownerDobj.setListTrailerDobj(ownerTempDobj.getListTrailerDobj());
            ownerDobj.setHypothecatedFlag(ownerTempDobj.isHypothecatedFlag());
            ownerDobj.setImp_Dobj(ownerTempDobj.getImp_Dobj());
            ownerDobj.setScrappedVehicleDobj(ownerTempDobj.getScrappedVehicleDobj());
            ownerDobj.setSpeedGovDeleteFlag(ownerTempDobj.isSpeedGovDeleteFlag());
            ownerDobj.setSpeedGovernerList(ownerTempDobj.getSpeedGovernerList());
            ownerDobj.setSpeedGovernorDobj(ownerTempDobj.getSpeedGovernorDobj());
        } catch (Exception e) {
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return ownerDobj;
    }

    public void setComboBoxesForDobj(Owner_dobj dobj) {

        list_p_district = MasterTableFiller.getDistrictList(dobj.getP_state());
        list_c_district = MasterTableFiller.getDistrictList(dobj.getC_state());

        //Fill Model For Maker
        String vehMaker = String.valueOf(dobj.getMaker());
        String[][] data = MasterTableFiller.masterTables.VM_MODELS.getData();
        list_maker_model.clear();
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equals(vehMaker)) {
                list_maker_model.add(new SelectItem(data[i][1], data[i][2] + "-(" + data[i][1] + ")"));
            }
        }

        //
        data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].trim().equals(String.valueOf(dobj.getVh_class()))) {
                if (data[i][2].equals(String.valueOf(TableConstants.VM_VEHTYPE_TRANSPORT))) {
                    setVehType(String.valueOf(TableConstants.VM_VEHTYPE_TRANSPORT));
                    break;
                } else {
                    setVehType(String.valueOf(TableConstants.VM_VEHTYPE_NON_TRANSPORT));
                    break;
                }
            }
        }

        list_vh_class.clear();
        for (int i = 0; i < data.length; i++) {
            if (data[i][2].equals(String.valueOf(getVehType()))) {
                for (Object obj : authorityDobj.getSelectedVehClass()) {
                    if (String.valueOf(obj).equalsIgnoreCase(data[i][0])) {
                        list_vh_class.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
            }
        }

    }

    public void setOwnerBeanFromHomologationToBean(Owner_dobj owner_dobj) {

        if (owner_dobj == null) {
            return;
        }

        this.getOwnerDobj().setChasi_no(owner_dobj.getChasi_no());
        this.getOwnerDobj().setEng_no(owner_dobj.getEng_no());
        this.getOwnerDobj().setMaker(owner_dobj.getMaker());
        this.getOwnerDobj().setMaker_model(owner_dobj.getMaker_model());
        this.setVch_purchase_as(owner_dobj.getVch_purchase_as());
        this.getOwnerDobj().setBody_type(owner_dobj.getBody_type());
        this.getOwnerDobj().setNo_cyl(owner_dobj.getNo_cyl());
        this.getOwnerDobj().setHp(owner_dobj.getHp());
        this.getOwnerDobj().setSeat_cap(owner_dobj.getSeat_cap());
        this.getOwnerDobj().setUnld_wt(owner_dobj.getUnld_wt());
        this.getOwnerDobj().setLd_wt(owner_dobj.getLd_wt());
        this.getOwnerDobj().setFuel(owner_dobj.getFuel());
        this.getOwnerDobj().setColor(owner_dobj.getColor());
        this.getOwnerDobj().setManu_mon(owner_dobj.getManu_mon());
        this.getOwnerDobj().setManu_yr(owner_dobj.getManu_yr());
        this.setNorms(owner_dobj.getNorms());
        this.getOwnerDobj().setWheelbase(owner_dobj.getWheelbase());
        this.getOwnerDobj().setCubic_cap(owner_dobj.getCubic_cap());
        this.getOwnerDobj().setVch_catg(owner_dobj.getVch_catg().trim());
        this.getOwnerDobj().setDealer_cd(owner_dobj.getDealer_cd());
        this.getOwnerDobj().setLength(owner_dobj.getLength());
        this.getOwnerDobj().setWidth(owner_dobj.getWidth());
        this.getOwnerDobj().setHeight(owner_dobj.getHeight());
        this.getOwnerDobj().setPush_bk_seat(ownerDobj.getPush_bk_seat());
        this.getOwnerDobj().setOrdinary_seat(ownerDobj.getOrdinary_seat());
    }//end of setOwnerBeanFromHomologationToBean

    public void set_maker_model_appl_dobj_to_bean(Owner_dobj owner_dobj) {
        if (owner_dobj == null) {
            return;
        }

        this.getOwnerDobj().setChasi_no(owner_dobj.getChasi_no());
        this.getOwnerDobj().setEng_no(owner_dobj.getEng_no());
        this.getOwnerDobj().setMaker(owner_dobj.getMaker());
        this.getOwnerDobj().setMaker_model(owner_dobj.getMaker_model());
        this.getOwnerDobj().setBody_type(owner_dobj.getBody_type());
        this.getOwnerDobj().setNo_cyl(owner_dobj.getNo_cyl());
        this.getOwnerDobj().setHp(owner_dobj.getHp());
        this.getOwnerDobj().setSeat_cap(owner_dobj.getSeat_cap());
        this.getOwnerDobj().setStand_cap(owner_dobj.getStand_cap());
        this.getOwnerDobj().setSleeper_cap(owner_dobj.getSleeper_cap());
        this.getOwnerDobj().setUnld_wt(owner_dobj.getUnld_wt());
        this.getOwnerDobj().setLd_wt(owner_dobj.getLd_wt());
        this.getOwnerDobj().setFuel(owner_dobj.getFuel());
        this.getOwnerDobj().setColor(owner_dobj.getColor());
        this.getOwnerDobj().setManu_mon(owner_dobj.getManu_mon());
        this.getOwnerDobj().setManu_yr(owner_dobj.getManu_yr());
        this.setNorms(owner_dobj.getNorms());
        this.getOwnerDobj().setWheelbase(owner_dobj.getWheelbase());
        this.getOwnerDobj().setCubic_cap(owner_dobj.getCubic_cap());
    }

    public Owner_dobj set_Owner_appl_bean_to_dobj() {

        Owner_dobj owner_dobj = new Owner_dobj();
        OwnerIdentificationDobj ow_id = new OwnerIdentificationDobj();
        owner_dobj.setOwner_identity(ow_id);

        //setting the value from form_ca to dobj
        if (validateForm()) {
            if (this.getAppl_no() != null) {
                owner_dobj.setAppl_no(this.getAppl_no().toString().trim());
                owner_dobj.getOwner_identity().setAppl_no(owner_dobj.getAppl_no());
            }
            if (this.getRegn_no() != null) {
                owner_dobj.setRegn_no(this.getRegn_no().toString().trim());
                owner_dobj.getOwner_identity().setRegn_no(owner_dobj.getRegn_no());
            }
            if (Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN)) {//forstateadmin
                owner_dobj.setOff_cd(getOwnerDobj().getOff_cd());
            } else {
                owner_dobj.setOff_cd(offCodeSelected);
            }

            owner_dobj.setPurchase_dt(getOwnerDobj().getPurchase_dt());
            owner_dobj.setOwner_sr(getOwnerDobj().getOwner_sr());

            if (getOwnerDobj().getOwner_name() != null) {
                owner_dobj.setOwner_name(getOwnerDobj().getOwner_name());
            }
            if (this.owner_identification.getPan_no() != null) {
                owner_dobj.getOwner_identity().setPan_no(this.owner_identification.getPan_no());
            }
            if (this.owner_identification.getEmail_id() != null) {
                owner_dobj.getOwner_identity().setEmail_id(this.owner_identification.getEmail_id().trim());
            }

            owner_dobj.getOwner_identity().setMobile_no(this.owner_identification.getMobile_no());

            if (this.owner_identification.getAadhar_no() != null) {
                owner_dobj.getOwner_identity().setAadhar_no(this.owner_identification.getAadhar_no().trim());
            }

            if (this.owner_identification.getPassport_no() != null) {
                owner_dobj.getOwner_identity().setPassport_no(this.owner_identification.getPassport_no().trim());
            }

            if (this.owner_identification.getRation_card_no() != null) {
                owner_dobj.getOwner_identity().setRation_card_no(this.owner_identification.getRation_card_no().trim());
            }

            if (this.owner_identification.getVoter_id() != null) {
                owner_dobj.getOwner_identity().setVoter_id(this.owner_identification.getVoter_id().trim());
            }

            if (this.owner_identification.getDl_no() != null) {
                owner_dobj.getOwner_identity().setDl_no(this.owner_identification.getDl_no().trim());
            }

            if (this.owner_identification.getOwnerCatg() != 0) {
                owner_dobj.getOwner_identity().setOwnerCatg(this.owner_identification.getOwnerCatg());
            }

            if (this.owner_identification.getOwnerCdDept() != 0) {
                owner_dobj.getOwner_identity().setOwnerCdDept(this.owner_identification.getOwnerCdDept());
            }

            if (getOwnerDobj().getF_name() != null) {
                owner_dobj.setF_name(getOwnerDobj().getF_name());
            }
            if (getOwnerDobj().getC_add1() != null) {
                owner_dobj.setC_add1(getOwnerDobj().getC_add1());
            }
            if (getOwnerDobj().getC_add2() != null) {
                owner_dobj.setC_add2(getOwnerDobj().getC_add2());
            }
            if (getOwnerDobj().getC_add3() != null) {
                owner_dobj.setC_add3(getOwnerDobj().getC_add3());
            }
            owner_dobj.setC_state(getOwnerDobj().getC_state());
            owner_dobj.setC_state_name(getOwnerDobj().getC_state_name());
            owner_dobj.setC_district_name(getOwnerDobj().getC_district_name());
            if (getOwnerDobj().getC_district() != -1) {
                owner_dobj.setC_district(getOwnerDobj().getC_district());
            }
            owner_dobj.setC_pincode(getOwnerDobj().getC_pincode());
            if (getOwnerDobj().getP_add1() != null) {
                owner_dobj.setP_add1(getOwnerDobj().getP_add1().trim());
            }
            if (getOwnerDobj().getP_add2() != null) {
                owner_dobj.setP_add2(getOwnerDobj().getP_add2().trim());
            }
            if (getOwnerDobj().getP_add3() != null) {
                owner_dobj.setP_add3(getOwnerDobj().getP_add3().trim());
            }
            if (getOwnerDobj().getP_state() != null) {
                owner_dobj.setP_state(getOwnerDobj().getP_state().trim());
            }

            owner_dobj.setP_district(getOwnerDobj().getP_district());

            owner_dobj.setP_pincode(getOwnerDobj().getP_pincode());

            if (getOwnerDobj().getOwner_cd() > 0) {
                owner_dobj.setOwner_cd(getOwnerDobj().getOwner_cd());
            }
            if (getOwnerDobj().getRegn_type() != null) {
                owner_dobj.setRegn_type(getOwnerDobj().getRegn_type());
            }
            if (getOwnerDobj().getVh_class() != -1) {
                owner_dobj.setVh_class(getOwnerDobj().getVh_class());
            }

            if (getOwnerDobj().getVehType() > 0) {
                owner_dobj.setVehType(getOwnerDobj().getVehType());
            }

            if (this.getOwnerDobj().getChasi_no() != null) {
                owner_dobj.setChasi_no(this.getOwnerDobj().getChasi_no());
            }
            if (this.getOwnerDobj().getEng_no() != null) {
                owner_dobj.setEng_no(this.getOwnerDobj().getEng_no());
            }
            if (this.getOwnerDobj().getMaker() != -1) {
                owner_dobj.setMaker(this.getOwnerDobj().getMaker());
            }
            if (this.getOwnerDobj().getMaker_model() != null) {
                owner_dobj.setMaker_model(this.getOwnerDobj().getMaker_model());
            }

            if (this.getOwnerDobj().getBody_type() != null) {
                owner_dobj.setBody_type(this.getOwnerDobj().getBody_type());
            }

            owner_dobj.setNo_cyl(this.getOwnerDobj().getNo_cyl());

            owner_dobj.setHp(this.getOwnerDobj().getHp());
            owner_dobj.setSeat_cap(this.getOwnerDobj().getSeat_cap());
            owner_dobj.setStand_cap(this.getOwnerDobj().getStand_cap());
            owner_dobj.setSleeper_cap(this.getOwnerDobj().getSleeper_cap());
            owner_dobj.setUnld_wt(this.getOwnerDobj().getUnld_wt());
            owner_dobj.setLd_wt(this.getOwnerDobj().getLd_wt());
            owner_dobj.setGcw(this.getOwnerDobj().getGcw());

            if (this.getOwnerDobj().getFuel() != -1) {
                owner_dobj.setFuel(this.getOwnerDobj().getFuel());
            }
            if (this.getOwnerDobj().getColor() != null) {
                owner_dobj.setColor(this.getOwnerDobj().getColor());
            }
            if (this.getOwnerDobj().getManu_mon() != null && !this.getOwnerDobj().getManu_mon().equals("")) {
                owner_dobj.setManu_mon(this.getOwnerDobj().getManu_mon());
            }
            if (this.getOwnerDobj().getManu_yr() != null && !this.getOwnerDobj().getManu_yr().equals("")) {
                owner_dobj.setManu_yr(this.getOwnerDobj().getManu_yr());
            }

            if (this.getNorms() != 0) {
                owner_dobj.setNorms(this.getNorms());
            }

            owner_dobj.setWheelbase(this.getOwnerDobj().getWheelbase());
            owner_dobj.setCubic_cap(this.getOwnerDobj().getCubic_cap());
            owner_dobj.setFloor_area(this.getOwnerDobj().getFloor_area());

            if (this.getOwnerDobj().getAc_fitted() != null) {
                owner_dobj.setAc_fitted(this.getOwnerDobj().getAc_fitted());
            }
            if (this.getOwnerDobj().getAudio_fitted() != null) {
                owner_dobj.setAudio_fitted(this.getOwnerDobj().getAudio_fitted());
            }
            if (this.getOwnerDobj().getVideo_fitted() != null) {
                owner_dobj.setVideo_fitted(this.getOwnerDobj().getVideo_fitted());
            }
            owner_dobj.setVch_purchase_as(this.getVch_purchase_as());

            if (this.getOwnerDobj().getVch_catg() != null) {
                owner_dobj.setVch_catg(this.getOwnerDobj().getVch_catg());
            }
            if (this.getOwnerDobj().getDealer_cd() != null) {
                owner_dobj.setDealer_cd(this.getOwnerDobj().getDealer_cd());
            }

            if (this.getSale_amt() != null && this.getSale_amt() > 0) {
                owner_dobj.setSale_amt(this.getSale_amt());
            }

            if (this.getOwnerDobj().getLaser_code() != null) {
                owner_dobj.setLaser_code(this.getOwnerDobj().getLaser_code());
            }
            if (this.getOwnerDobj().getGarage_add() != null) {
                owner_dobj.setGarage_add(this.getOwnerDobj().getGarage_add());
            }

            if (this.getOwnerDobj().getNewLoiNo() != null) {
                owner_dobj.setNewLoiNo(this.getOwnerDobj().getNewLoiNo());
            }

            owner_dobj.setLength(this.getOwnerDobj().getLength());
            owner_dobj.setWidth(this.getOwnerDobj().getWidth());
            owner_dobj.setHeight(this.getOwnerDobj().getHeight());

            owner_dobj.setState_cd(stateCodeSelected);

            if (this.getImported_veh() != null) {
                owner_dobj.setImported_vch(this.getImported_veh());
            }
            owner_dobj.setOther_criteria(this.getOther_criteria());
            if (this.getAnnual_income() != null) {
                owner_dobj.setAnnual_income(this.getAnnual_income());
            }

            owner_dobj.setFit_upto(this.getFit_upto());
            owner_dobj.setRegn_upto(this.getRegn_upto());

            if (otherStateDobj != null) {
                owner_dobj.setFit_upto(getOwnerDobj().getFit_upto());
                owner_dobj.setRegn_dt(getOwnerDobj().getRegn_dt());
                owner_dobj.setRegn_upto(getOwnerDobj().getRegn_upto());
                owner_dobj.setOtherStateVehDobj(otherStateDobj);
            }

            if (renderValidityPanel) {
                owner_dobj.setFit_upto(this.getFit_upto());
                owner_dobj.setRegn_upto(this.getRegn_upto());

            }

            if (renderTempOwner) {
                Owner_temp_dobj temp = getTemp_dobj();
                temp.setAppl_no(owner_dobj.getAppl_no());
                owner_dobj.setDob_temp(temp);
            }

            if (blnRegnTypeTemp) {
                TempRegDobj tempDobj = getTempReg();
                tempDobj.setAppl_no(owner_dobj.getAppl_no());
                tempDobj.setRegn_no(owner_dobj.getRegn_no());
                owner_dobj.setTempReg(tempReg);
            }

            if (this.ownerDobj.getAuctionDobj() != null) {
                owner_dobj.setAuctionDobj(this.ownerDobj.getAuctionDobj());
                owner_dobj.setRegn_dt(this.ownerDobj.getRegn_dt());

            }

            if (getOwnerDobj().getCdDobj() != null) {
                getOwnerDobj().getCdDobj().setApplNo(owner_dobj.getAppl_no());
                getOwnerDobj().getCdDobj().setRegNo(owner_dobj.getRegn_no());
                owner_dobj.setCdDobj(getOwnerDobj().getCdDobj());
            }

            owner_dobj.setScrappedVehicleDobj(getOwnerDobj().getScrappedVehicleDobj());

            owner_dobj.setPmt_type(getOwnerDobj().getPmt_type());
            owner_dobj.setPmt_catg(getOwnerDobj().getPmt_catg());
            owner_dobj.setServicesType(getOwnerDobj().getServicesType());
            owner_dobj.setRegion_covered(getOwnerDobj().getRegion_covered());
            owner_dobj.setRqrd_tax_modes(NewImpl.getTaxModes(getOwnerDobj()));
            owner_dobj.setLinkedRegnNo(getOwnerDobj().getLinkedRegnNo());

            owner_dobj.setSpeedGovernorDobj(getOwnerDobj().getSpeedGovernorDobj());
            owner_dobj.setReflectiveTapeDobj(getOwnerDobj().getReflectiveTapeDobj());
            owner_dobj.setVehicleTrackingDetailsDobj(getOwnerDobj().getVehicleTrackingDetailsDobj());

            if (renderInspectionPanelNT) {
                InspectionDobj inspectionDobj = new InspectionDobj();
                this.ownerDobj.setInspectionDobj(this.inspDobj);
                inspectionDobj.setAppl_no(owner_dobj.getAppl_no());
                inspectionDobj.setRegn_no(owner_dobj.getRegn_no());
                inspectionDobj.setOff_cd(owner_dobj.getOff_cd());
                inspectionDobj.setRemark(this.ownerDobj.getInspectionDobj().getRemark());
                inspectionDobj.setInsp_dt(this.ownerDobj.getInspectionDobj().getInsp_dt());
                inspectionDobj.setState_cd(stateCodeSelected);
                setInspDobj(inspectionDobj);
                owner_dobj.setInspectionDobj(inspectionDobj);
            }
            owner_dobj.setPush_bk_seat(this.getOwnerDobj().getPush_bk_seat());
            owner_dobj.setOrdinary_seat(this.getOwnerDobj().getOrdinary_seat());
            owner_dobj.setListTrailerDobj(getOwnerDobj().getListTrailerDobj());
        }

        return owner_dobj;
    }

    private boolean validateForm() {
        // validate is pending here...
        return true;
    }

    public void regn_TypeSelectEvent() {
        ExArmyBean exArmyBean = (ExArmyBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("exArmyBean");
        if (TableConstants.VM_REGN_TYPE_EXARMY.equalsIgnoreCase(getOwnerDobj().getRegn_type()) && (TableConstants.VM_REGN_TYPE_NEW.equalsIgnoreCase(getPreviousRegnType()) || TableConstants.VM_REGN_TYPE_EXARMY.equalsIgnoreCase(getPreviousRegnType()))) {
            setExArmyVehicle_Visibility_tab(true);//tab enabled
            blnRegnTypeTemp = false;
            tempReg = null;
        } else if (TableConstants.VM_REGN_TYPE_TEMPORARY.equalsIgnoreCase(getOwnerDobj().getRegn_type()) && (TableConstants.VM_REGN_TYPE_EXARMY.equalsIgnoreCase(getPreviousRegnType()) || TableConstants.VM_REGN_TYPE_NEW.equalsIgnoreCase(getPreviousRegnType()))) {
            tempReg = new TempRegDobj();
            getOwner_dobj_prv().setExArmy_dobj(null);
            setExArmyVehicle_Visibility_tab(false);
            blnRegnTypeTemp = true;
            if (exArmyBean != null) {
                exArmyBean.resetExarmyDetails();
            }
        } else if (TableConstants.VM_REGN_TYPE_NEW.equalsIgnoreCase(getOwnerDobj().getRegn_type()) && (TableConstants.VM_REGN_TYPE_EXARMY.equalsIgnoreCase(getPreviousRegnType()) || TableConstants.VM_REGN_TYPE_NEW.equalsIgnoreCase(getPreviousRegnType()))) {
            tempReg = null;
            getOwner_dobj_prv().setExArmy_dobj(null);
            setExArmyVehicle_Visibility_tab(false);
            blnRegnTypeTemp = false;
            if (exArmyBean != null) {
                exArmyBean.resetExarmyDetails();
            }
        } else {
            getOwnerDobj().setRegn_type(getPreviousRegnType());
            JSFUtils.setFacesMessage("You can change only NEW/EXARMY To NEW/EXARMY/TEMP Registration Type ", "You can change only NEW/ExArmy To NEW/EXARMY/TEMP Registration Type ", JSFUtils.INFO);
        }

    }

    public void imp_vehicleSelectEvent(AjaxBehaviorEvent aje) {
        if (TableConstants.VM_REGN_TYPE_IMPORTED_YES.equalsIgnoreCase(imported_veh)) {
            setImportedVehicle_Visibility_tab(true);
        } else {
            setImportedVehicle_Visibility_tab(false);
        }
    }

    public void fuel_TypeSelectEvent() {
        try {
            int fuel_type = this.getOwnerDobj().getFuel();
            if (fuel_type == TableConstants.VM_FUEL_CNG_TYPE
                    || fuel_type == TableConstants.VM_FUEL_TYPE_PETROL_CNG
                    || fuel_type == TableConstants.VM_FUEL_TYPE_LPG
                    || fuel_type == TableConstants.VM_FUEL_TYPE_PETROL_LPG) {
                setCngDetails_Visibility_tab(true);
                purchaseDateListener();
                RetroFittingDetailsBean cngBean = (RetroFittingDetailsBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("retroFittingDetailsBean");
                TmConfigurationDobj dobj = Util.getTmConfiguration();
                if (dobj.isCnginfo_from_cngmaker()) {
                    if (this.ownerDobj.getRegn_type().equalsIgnoreCase("T")) {
                        if (stateCodeSelected.equals("DL")) {
                            cngBean.setDobj_To_Bean(ServerUtil.getCngMakerInfo(this.tempReg.getTmp_regn_no()));
                        } else {
                            cngBean.setDobj_To_Bean(ServerUtil.getCngMakerInfoforAllState(this.tempReg.getTmp_regn_no()));
                        }
                        if (cngBean.isDisable()) {
                            JSFUtils.setFacesMessage("CNG Kit Info filled from CNG Maker", null, JSFUtils.INFO);
                        } else {
                            this.getOwnerDobj().setFuel(-1);
                            setCngDetails_Visibility_tab(false);
                            JSFUtils.setFacesMessage("CNG Kit Info not available. Please make sure cng is fitted through CNGMAKER", null, JSFUtils.INFO);
                        }
                    }
                }
            } else {
                setCngDetails_Visibility_tab(false);
            }

            UIComponent component = FacesContext.getCurrentInstance().getViewRoot().findComponent("masterLayout");
            if (component != null) {
                OutputLabel lab = (OutputLabel) component.findComponent("workbench_tabview:engMoterNo");
                OutputLabel labHp = (OutputLabel) component.findComponent("workbench_tabview:horsePowerLabel");
                if (lab != null && labHp != null) {

                    if (fuel_type == TableConstants.VM_FUEL_TYPE_ELECTRIC) {
                        lab.setValue("Motor No");
                        labHp.setValue("Motor Wattage(kw)");
                    } else {
                        lab.setValue("Engine No");
                        labHp.setValue("Horse Power(BHP)");
                    }
                    PrimeFaces.current().ajax().update("workbench_tabview:engMoterNo");
                    PrimeFaces.current().ajax().update("workbench_tabview:horsePowerLabel");
                }

            }

            if (fuel_type >= 1) {//for loading tax mode based on fuel type
                taxModeLoader(vehType, ownerDobj);
            }
        } catch (VahanException e) {
            JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.ERROR);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.setFacesMessage(TableConstants.SomthingWentWrong, null, JSFUtils.ERROR);
        }
    }

    public void chasis_no_valueChangeListiner(AjaxBehaviorEvent evt) {
        try {

            if (this.getOwnerDobj().getChasi_no() != null || !this.getOwnerDobj().getChasi_no().equals("")) {
                this.getOwnerDobj().setChasi_no(this.getOwnerDobj().getChasi_no().toUpperCase());
                OwnerImpl owner_Impl = new OwnerImpl();
                String appl_no = "";
                Owner_dobj owner_dobj = owner_Impl.set_Owner_appl_db_to_dobj("", "", this.getOwnerDobj().getChasi_no().toUpperCase(), TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
                if (owner_dobj != null) {
                    owner_dobj = owner_Impl.set_Owner_appl_db_to_dobj("", "", this.getOwnerDobj().getChasi_no().toUpperCase(), TableConstants.VM_TRANSACTION_MAST_TEMP_REG);
                }

                if (owner_dobj != null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Already entry done of this chassis number : " + this.getOwnerDobj().getChasi_no().toUpperCase(), null));
                    this.getOwnerDobj().setChasi_no("");
                    // vehicleComponentEditable(false);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void ownerComponentEditable(boolean flag) {

        flag = !flag;
        getRegn_dt_cal().setDisabled(flag);
        setBlnDisableRegnTypeTemp(flag);
    }

    public void ownerComponentReadOnly(boolean flag) {
        flag = !flag;

        this.owner_identification.setFlag(flag);
        this.owner_identification.setMobileNoEditable(flag);
    }

    public void samePermAddressListener() {

        if (isSamePermAddress()) {
            this.getOwnerDobj().setP_add1(getOwnerDobj().getC_add1());
            this.getOwnerDobj().setP_add2(getOwnerDobj().getC_add2());
            this.getOwnerDobj().setP_add3(getOwnerDobj().getC_add3());
            this.getOwnerDobj().setP_pincode(getOwnerDobj().getC_pincode());
            if (getOwnerDobj().getC_state() != null && !getOwnerDobj().getC_state().equals("-1")) {
                getOwnerDobj().setP_state(getOwnerDobj().getC_state());
                String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
                list_p_district.clear();
                for (int i = 0; i < data.length; i++) {
                    if (data[i][2].trim().equals(stateCodeSelected)) {
                        list_p_district.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }

                if (getOwnerDobj().getC_district() != -1) {
                    list_p_district.clear();
                    data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
                    for (int i = 0; i < data.length; i++) {
                        if (getOwnerDobj().getC_state().equalsIgnoreCase(data[i][2])) {
                            list_p_district.add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                    getOwnerDobj().setP_district(getOwnerDobj().getC_district());
                }
            }
        } else {
            getOwnerDobj().setP_add1("");
            getOwnerDobj().setP_add2("");
            getOwnerDobj().setP_district(-1);
            getOwnerDobj().setP_add3("");
            getOwnerDobj().setP_state("-1");
            getOwnerDobj().setP_pincode(0);
        }
    }

    public List<ComparisonBean> addToComapreChangesList(List<ComparisonBean> compBeanListPrev) throws VahanException {

        List<ComparisonBean> list = compareChagnes();

        if (compBeanListPrev == null) {
            compBeanListPrev = new ArrayList<>();
        }
        if (list.size() > 0) {
            compBeanListPrev.addAll(list);
        }

        return compBeanListPrev;
    }

    public List<ComparisonBean> compareChagnes() throws VahanException {
        Owner_dobj owner_dobj = getOwner_dobj_prv(); //getting the dobj from workbench

        if (owner_dobj == null) {
            return compBeanList;
        }
        compBeanList.clear();

        ////////////////////////////vehicle Details start///////////////////
        Compare("Chasi_No", owner_dobj.getChasi_no(), this.getOwnerDobj().getChasi_no(), compBeanList);

        String[][] dataVhclass = MasterTableFiller.masterTables.VM_VH_CLASS.getData();

        if (owner_dobj.getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT && owner_dobj.getVehType() != getOwnerDobj().getVehType()) {
            getOwnerDobj().setPmt_type(-1);
            getOwnerDobj().setPmt_catg(-1);
            getOwnerDobj().setServicesType("");
        }

        if (owner_dobj.getVh_class() != getOwnerDobj().getVh_class()) {
            String oldlabel = null;
            String newLabel = null;

            for (int i = 0; i < dataVhclass.length; i++) {

                if (dataVhclass[i][0].equals(String.valueOf(owner_dobj.getVh_class()))) {
                    oldlabel = dataVhclass[i][1];
                }

                if (dataVhclass[i][0].equals(String.valueOf(getOwnerDobj().getVh_class()))) {
                    newLabel = dataVhclass[i][1];
                }
            }

            Compare("Veh Class", oldlabel, newLabel, compBeanList);
        }

        Compare("Eng_No", owner_dobj.getEng_no(), this.getOwnerDobj().getEng_no(), compBeanList);
        Compare("Maker", owner_dobj.getMaker(), this.getOwnerDobj().getMaker(), compBeanList);
        Compare("Maker model", owner_dobj.getMaker_model(), this.getOwnerDobj().getMaker_model(), compBeanList);
        Compare("Bd_type", owner_dobj.getBody_type(), this.getOwnerDobj().getBody_type(), compBeanList);
        Compare("Seat_Cap", owner_dobj.getSeat_cap(), this.getOwnerDobj().getSeat_cap(), compBeanList);
        Compare("Stand_Cap", owner_dobj.getStand_cap(), this.getOwnerDobj().getStand_cap(), compBeanList);
        Compare("Sleeper_Cap", owner_dobj.getSleeper_cap(), this.getOwnerDobj().getSleeper_cap(), compBeanList);
        Compare("No_of_Cyl", owner_dobj.getNo_cyl(), this.getOwnerDobj().getNo_cyl(), compBeanList);
        Compare("ULD", owner_dobj.getUnld_wt(), this.getOwnerDobj().getUnld_wt(), compBeanList);
        Compare("LD", owner_dobj.getLd_wt(), this.getOwnerDobj().getLd_wt(), compBeanList);
        Compare("GCW", owner_dobj.getGcw(), this.getOwnerDobj().getGcw(), compBeanList);
        Compare("Norms", owner_dobj.getNorms(), this.getNorms(), compBeanList);
        Compare("HP", owner_dobj.getHp(), this.getOwnerDobj().getHp(), compBeanList);
        Compare("Fuel", owner_dobj.getFuel(), this.getOwnerDobj().getFuel(), compBeanList);
        Compare("Color", owner_dobj.getColor(), this.getOwnerDobj().getColor(), compBeanList);
        Compare("WheelBase", owner_dobj.getWheelbase(), this.getOwnerDobj().getWheelbase(), compBeanList);
        Compare("CC", owner_dobj.getCubic_cap(), this.getOwnerDobj().getCubic_cap(), compBeanList);
        Compare("Floor_Area", owner_dobj.getFloor_area(), this.getOwnerDobj().getFloor_area(), compBeanList);
        Compare("AC", owner_dobj.getAc_fitted(), this.getOwnerDobj().getAc_fitted(), compBeanList);
        Compare("Audio", owner_dobj.getAudio_fitted(), this.getOwnerDobj().getAudio_fitted(), compBeanList);
        Compare("Video", owner_dobj.getVideo_fitted(), this.getOwnerDobj().getVideo_fitted(), compBeanList);
        Compare("Manu_Month", owner_dobj.getManu_mon(), this.getOwnerDobj().getManu_mon(), compBeanList);
        Compare("Manu_Year", owner_dobj.getManu_yr(), this.getOwnerDobj().getManu_yr(), compBeanList);
        Compare("Laser_Cd", owner_dobj.getLaser_code(), this.getOwnerDobj().getLaser_code(), compBeanList);
        Compare("Length", owner_dobj.getLength(), this.getOwnerDobj().getLength(), compBeanList);
        Compare("Width", owner_dobj.getWidth(), this.getOwnerDobj().getWidth(), compBeanList);
        Compare("Height", owner_dobj.getHeight(), this.getOwnerDobj().getHeight(), compBeanList);
        Compare("G.Address", owner_dobj.getGarage_add(), this.getOwnerDobj().getGarage_add(), compBeanList);
        Compare("Dealer", owner_dobj.getDealer_cd(), this.getOwnerDobj().getDealer_cd(), compBeanList);

        ////////////////////////////////////////vehicle Detail End//////////
        /////////////////////////owner Detail start/////////////////////////
        //Compare("Registration Date", owner_dobj.getRegn_dt(), getRegn_dt(), compBeanList);
        Compare("Purchase Date", owner_dobj.getPurchase_dt(), getOwnerDobj().getPurchase_dt(), compBeanList);
        Compare("O_Name", owner_dobj.getOwner_name(), getOwnerDobj().getOwner_name(), compBeanList);
        Compare("F_Name", owner_dobj.getF_name(), getOwnerDobj().getF_name(), compBeanList);
        Compare("O_Sr", owner_dobj.getOwner_sr(), getOwnerDobj().getOwner_sr(), compBeanList);
        Compare("OW_CODE", owner_dobj.getOwner_cd(), getOwnerDobj().getOwner_cd(), compBeanList);
        Compare("PAN_No", owner_dobj.getOwner_identity().getPan_no(), this.owner_identification.getPan_no(), compBeanList);
        Compare("Mobile", owner_dobj.getOwner_identity().getMobile_no() == null ? 0 : owner_dobj.getOwner_identity().getMobile_no(), this.owner_identification.getMobile_no(), compBeanList);
        Compare("Email", owner_dobj.getOwner_identity().getEmail_id(), this.owner_identification.getEmail_id(), compBeanList);
        Compare("Aadhar", owner_dobj.getOwner_identity().getAadhar_no(), this.owner_identification.getAadhar_no(), compBeanList);
        Compare("Passport", owner_dobj.getOwner_identity().getPassport_no(), this.owner_identification.getPassport_no(), compBeanList);
        Compare("Ration", owner_dobj.getOwner_identity().getRation_card_no(), this.owner_identification.getRation_card_no(), compBeanList);
        Compare("Voter_Id", owner_dobj.getOwner_identity().getVoter_id(), this.owner_identification.getVoter_id(), compBeanList);
        Compare("DL", owner_dobj.getOwner_identity().getDl_no(), this.owner_identification.getDl_no(), compBeanList);
        Compare("Owner Category", owner_dobj.getOwner_identity().getOwnerCatg(), this.owner_identification.getOwnerCatg(), compBeanList);
        Compare("Owner Code Department", owner_dobj.getOwner_identity().getOwnerCdDept(), this.owner_identification.getOwnerCdDept(), compBeanList);
        Compare("C_Add1", owner_dobj.getC_add1(), getOwnerDobj().getC_add1(), compBeanList);
        Compare("C_Add2", owner_dobj.getC_add2(), getOwnerDobj().getC_add2(), compBeanList);
        Compare("C_Dist", owner_dobj.getC_district(), getOwnerDobj().getC_district(), compBeanList);
        Compare("C_Add3", owner_dobj.getC_add3(), getOwnerDobj().getC_add3(), compBeanList);
        Compare("C_state", owner_dobj.getC_state(), getOwnerDobj().getC_state(), compBeanList);
        Compare("C_Pin", owner_dobj.getC_pincode(), getOwnerDobj().getC_pincode(), compBeanList);
        Compare("P_Add1", owner_dobj.getP_add1(), getOwnerDobj().getP_add1(), compBeanList);
        Compare("P_Add2", owner_dobj.getP_add2(), getOwnerDobj().getP_add2(), compBeanList);
        Compare("P_Dist", owner_dobj.getP_district(), getOwnerDobj().getP_district(), compBeanList);
        Compare("P_Add3", owner_dobj.getP_add3(), getOwnerDobj().getP_add3(), compBeanList);
        Compare("p_state", owner_dobj.getP_state(), getOwnerDobj().getP_state(), compBeanList);
        Compare("P_Pin", owner_dobj.getP_pincode(), getOwnerDobj().getP_pincode(), compBeanList);
        Compare("Sale Amount", owner_dobj.getSale_amt(), getSale_amt(), compBeanList);
        Compare("Veh Catg", owner_dobj.getVch_catg(), getOwnerDobj().getVch_catg(), compBeanList);
        //--
        Compare("Annual Income", owner_dobj.getAnnual_income(), getAnnual_income(), compBeanList);
        Compare("Other Criteria", owner_dobj.getOther_criteria(), getOther_criteria(), compBeanList);
        Compare("Imported Vehicle", owner_dobj.getImported_vch(), getImported_veh(), compBeanList);
        Compare("Purchase As", owner_dobj.getVch_purchase_as(), getVch_purchase_as(), compBeanList);
        Compare("Permit Type", owner_dobj.getPmt_type(), getOwnerDobj().getPmt_type(), compBeanList);
        Compare("Permit Catg", owner_dobj.getPmt_catg(), getOwnerDobj().getPmt_catg(), compBeanList);
        Compare("Permit ServiceType", owner_dobj.getServicesType(), getOwnerDobj().getServicesType(), compBeanList);
        Compare("Permit RegionCovered", owner_dobj.getRegion_covered(), getOwnerDobj().getRegion_covered(), compBeanList);
        Compare("Regn Type", owner_dobj.getRegn_type(), getOwnerDobj().getRegn_type(), compBeanList);
        if (renderTaxPanel) {
            Compare("Tax Modes", owner_dobj.getRqrd_tax_modes(), NewImpl.getTaxModes(getOwnerDobj()), compBeanList);
        }
        if (owner_dobj.getDob_temp() != null && getTemp_dobj_prev() != null) {
            Compare("Temp Purpose", getTemp_dobj_prev().getPurpose(), getTemp_dobj().getPurpose(), compBeanList);
            if (getTemp_dobj().getBodyBuilding() != null) {
                Compare("Temp Body Building", getTemp_dobj_prev().getBodyBuilding(), getTemp_dobj().getBodyBuilding(), compBeanList);
            } else {
                Compare("State To", getTemp_dobj_prev().getState_cd_to(), getTemp_dobj().getState_cd_to(), compBeanList);
                Compare("Office To", getTemp_dobj_prev().getOff_cd_to(), getTemp_dobj().getOff_cd_to(), compBeanList);
            }
        }

        if (owner_dobj.getTempReg() != null && getTempReg_prev() != null) {
            Compare("Temp Regn No", getTempReg_prev().getTmp_regn_no(), getTempReg().getTmp_regn_no(), compBeanList);
            Compare("Temp Regn Date", getTempReg_prev().getTmp_regn_dt(), getTempReg().getTmp_regn_dt(), compBeanList);
            Compare("Temp Valid Upto", getTempReg_prev().getTmp_valid_upto(), getTempReg().getTmp_valid_upto(), compBeanList);
            Compare("Temp State ", getTempReg_prev().getTmp_state_cd(), getTempReg().getTmp_state_cd(), compBeanList);
            Compare("Temp Office", getTempReg_prev().getTmp_off_cd(), getTempReg().getTmp_off_cd(), compBeanList);
            Compare("Dealer", getTempReg_prev().getDealer_cd(), getTempReg().getDealer_cd(), compBeanList);

        }

        if (owner_dobj.getOtherStateVehDobj() != null && getOtherStateDobjPrev() != null) {
            Compare("New Regn No", getOtherStateDobjPrev().getNewRegnNo(), getOtherStateDobj().getNewRegnNo(), compBeanList);
            Compare("Old Regn No", getOtherStateDobjPrev().getOldRegnNo(), getOtherStateDobj().getOldRegnNo(), compBeanList);
            Compare("State Entry Date", getOtherStateDobjPrev().getStateEntryDate(), getOtherStateDobj().getStateEntryDate(), compBeanList);
            Compare("Regn Dt", owner_dobj.getRegn_dt(), getOwnerDobj().getRegn_dt(), compBeanList);
            Compare("Regn Upto Dt", owner_dobj.getRegn_upto(), getOwnerDobj().getRegn_upto(), compBeanList);
            Compare("Fit Upto Dt", owner_dobj.getFit_upto(), getOwnerDobj().getFit_upto(), compBeanList);
        }

        if (owner_dobj.getCdDobj() != null) {
            Compare("CD Regn No", getCdDobjClone().getCdRegnNo(), getOwnerDobj().getCdDobj().getCdRegnNo(), compBeanList);
            Compare("CD Sale Date", getCdDobjClone().getSaleDate(), getOwnerDobj().getCdDobj().getSaleDate(), compBeanList);
        }

        if (renderValidityPanel) {
            if (owner_dobj.getRegn_upto() != null) {
                Compare("Regn Upto Dt", owner_dobj.getRegn_upto(), this.getRegn_upto(), compBeanList);
            }
            if (owner_dobj.getFit_upto() != null) {
                Compare("Fit Upto Dt", owner_dobj.getFit_upto(), this.getFit_upto(), compBeanList);
            }

            if (owner_dobj.getRegn_dt() != null) {
                Compare("Regn Date", owner_dobj.getRegn_dt(), this.getRegn_dt(), compBeanList);
            }
        }
        Compare("Linked Regn Number", owner_dobj.getLinkedRegnNo(), getOwnerDobj().getLinkedRegnNo(), compBeanList);

        if (renderSpeedGov && getOwnerDobj().getSpeedGovernorDobj() != null) {
            if (getSpeedGovPrev() != null) {
                Compare("Speed Governor No", getSpeedGovPrev() != null ? getSpeedGovPrev().getSg_no() : "", getOwnerDobj().getSpeedGovernorDobj().getSg_no(), compBeanList);
                Compare("Speed Governor Fitted At", getSpeedGovPrev() != null ? getSpeedGovPrev().getSg_fitted_at() : "", getOwnerDobj().getSpeedGovernorDobj().getSg_fitted_at(), compBeanList);
                Compare("Speed Governor Fitted On", getSpeedGovPrev() != null ? getSpeedGovPrev().getSg_fitted_on() : null, getOwnerDobj().getSpeedGovernorDobj().getSg_fitted_on(), compBeanList);
                Compare("Speed Governor Type", getSpeedGovPrev().getSgGovType(), getOwnerDobj().getSpeedGovernorDobj().getSgGovType(), compBeanList);
                Compare("Speed Governor Type Approval No", getSpeedGovPrev() != null ? getSpeedGovPrev().getSgTypeApprovalNo() : "", getOwnerDobj().getSpeedGovernorDobj().getSgTypeApprovalNo(), compBeanList);
                Compare("Speed Governor Test Report No", getSpeedGovPrev() != null ? getSpeedGovPrev().getSgTestReportNo() : "", getOwnerDobj().getSpeedGovernorDobj().getSgTestReportNo(), compBeanList);
                Compare("Speed Governor Fit Cert No", getSpeedGovPrev() != null ? getSpeedGovPrev().getSgFitmentCerticateNo() : "", getOwnerDobj().getSpeedGovernorDobj().getSgFitmentCerticateNo(), compBeanList);
            }
        }
        if (pushBkSeatRender) {
            Compare("Push Back Seat", owner_dobj.getPush_bk_seat(), this.getOwnerDobj().getPush_bk_seat(), compBeanList);
            Compare("Ordinary Seat", owner_dobj.getOrdinary_seat(), this.getOwnerDobj().getOrdinary_seat(), compBeanList);
        }

        if (renderReflectiveTape && getOwnerDobj().getReflectiveTapeDobj() != null) {
            if (getReflectiveTapeDobjPrev() != null) {
                Compare("Ref Cert No", getReflectiveTapeDobjPrev() != null ? getReflectiveTapeDobjPrev().getCertificateNo() : "", getOwnerDobj().getReflectiveTapeDobj().getCertificateNo(), compBeanList);
                Compare("Ref Fitment Date", getReflectiveTapeDobjPrev() != null ? getReflectiveTapeDobjPrev().getFitmentDate() : null, getOwnerDobj().getReflectiveTapeDobj().getFitmentDate(), compBeanList);
                Compare("Ref Manu Name", getReflectiveTapeDobjPrev() != null ? getReflectiveTapeDobjPrev().getManuName() : "", getOwnerDobj().getReflectiveTapeDobj().getManuName(), compBeanList);
            }
        }

        /////////////////////////////////owner Detail end///////////////////
        return compBeanList;

    }

    public void vehOwner_cdListener(AjaxBehaviorEvent event) {
        try {
            int ownCd = getOwnerDobj().getOwner_cd();
            if (ownCd != 5 && TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION.equalsIgnoreCase(getOwnerDobj().getRegn_type()) && stateCodeSelected != null && stateCodeSelected.equals("KA")) {
                if (getOwnerDobj().getAuctionDobj() != null && getOwnerDobj().getAuctionDobj().getRegnNo() != null && getOwnerDobj().getAuctionDobj().getRegnNo().equals("NEW")) {
                    this.getOwnerDobj().setOwner_cd(-1);
                    PrimeFaces.current().ajax().update("workbench_tabview:tf_owner_cd");
                    JSFUtils.showMessagesInDialog("Alert !!", "Please Select Ownership Type as state Govt for Unregistered Vehicles.", FacesMessage.SEVERITY_WARN);
                    return;
                }
            }

            //OWNER_CODE_INDIVIDUAL
            if (ownCd != TableConstants.OWNER_CODE_INDIVIDUAL) {
                getOwnerDobj().setF_name("NA");
                setDisableFName(true);

            } else {
                getOwnerDobj().setF_name("");
                setDisableFName(false);
            }

            setListOwnerCdDept(OwnerIdentificationImpl.getOwnerCatgDepts(Util.getUserStateCode(), ownCd));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void stateToListner(AjaxBehaviorEvent event) {
        try {
            String stateTo = getTemp_dobj() != null ? getTemp_dobj().getState_cd_to() : null;
            if (stateTo == null) {
                stateTo = getTemp_dobj() != null ? getTemp_dobj().getTempState_cd_from() : null;
            }
            //OWNER_CODE_INDIVIDUAL
            if (stateTo != null && !stateTo.isEmpty() && !stateTo.equals("0")) {
                list_office_to.clear();
                List<Integer> offTypeCd = Arrays.asList(0, 1, 2);
                list_office_to = ServerUtil.getOfficeBasedOnType(stateTo, offTypeCd);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void tempPurposeListner(AjaxBehaviorEvent event) {
        if (getTemp_dobj() == null) {
            return;
        }
        list_c_state_to = MasterTableFiller.getStateList();

        if (getTemp_dobj().getPurpose() != null && (getTemp_dobj().getPurpose().equals(TEMP_SAME_RTO) || getTemp_dobj().getPurpose().equals(DEMONSTRATION_WITHIN_SAME_RTO))) {
            setRenderTempOther(false);
            setDisableTempStateTo(false);
            getTemp_dobj().setState_cd_to(stateCodeSelected);
            if (Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN) && Util.getSelectedSeat().getPur_cd() == TableConstants.STATEADMIN_OWNER_DATA_CHANGE) {
                getTemp_dobj().setOff_cd_to(offCdToOfOwnerDobj);
            } else {
                getTemp_dobj().setOff_cd_to(offCodeSelected);
            }
            setRenderTempBodyBuilding(false);
            getTemp_dobj().setBodyBuilding(null);
        } else if (getTemp_dobj().getPurpose() != null && (getTemp_dobj().getPurpose().equals(TEMP_BODY_BUILDING) || getTemp_dobj().getPurpose().equals(TEMP_OTHER_RTO) || getTemp_dobj().getPurpose().equals(STOCK_TRANSFER) || getTemp_dobj().getPurpose().equals(DEMONSTRATION_WITHIN_DIFF_RTO))) {
            getTemp_dobj().setState_cd_to(stateCodeSelected);
            setRenderTempOther(true);
            //getTemp_dobj().setBodyBuilding(null);
            stateToListner(event);
            //setDisableTempStateTo(true);
            if (getTemp_dobj().getPurpose().equals(TEMP_BODY_BUILDING)) {
                setRenderTempBodyBuilding(true);
                //getTemp_dobj().setOff_cd_to(offCodeSelected);
            } else {
                setRenderTempBodyBuilding(false);
                getTemp_dobj().setBodyBuilding(null);
            }

            setDisableTempStateTo(true);
            FacesContext ctx = FacesContext.getCurrentInstance();
            UIComponent panel = (UIComponent) ctx.getViewRoot().findComponent("masterLayout:workbench_tabview:op_tempOther_state_to");
            if (panel != null) {
                disableAll(panel.getChildren(), true);
            }

        } else if (getTemp_dobj().getPurpose() != null && (getTemp_dobj().getPurpose().equals(TEMP_OTHER_STATE) || getTemp_dobj().getPurpose().equals(OEM_TO_DEALER_LOCATION))) {
            list_office_to.clear();
            setRenderTempBodyBuilding(false);
            setRenderTempOther(true);
            setDisableTempStateTo(false);
            FacesContext ctx = FacesContext.getCurrentInstance();
            UIComponent panel = (UIComponent) ctx.getViewRoot().findComponent("masterLayout:workbench_tabview:op_tempOther_state_to");
            if (panel != null) {
                disableAll(panel.getChildren(), false);
            }
            getTemp_dobj().setBodyBuilding(null);
            if (TEMP_OTHER_STATE.equals(getTemp_dobj().getPurpose()) && purCd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                for (Object item : list_c_state_to) {
                    SelectItem state = (SelectItem) item;
                    if (state.getValue().toString().equals(stateCodeSelected)) {
                        list_c_state_to.remove(item);
                        break;
                    }
                }
            }
            stateToListner(event);

        } else {
            setRenderTempBodyBuilding(false);
            setRenderTempOther(false);
            setDisableTempStateTo(false);
            FacesContext ctx = FacesContext.getCurrentInstance();
            UIComponent panel = (UIComponent) ctx.getViewRoot().findComponent("masterLayout:workbench_tabview:op_tempOther_state_to");
            if (panel != null) {
                disableAll(panel.getChildren(), false);
            }
        }
    }

    public void stateTempToListner(AjaxBehaviorEvent event) {

        String stateTempTo = getTempReg() != null ? getTempReg().getTmp_state_cd() : null;
        //OWNER_CODE_INDIVIDUAL
        if (stateTempTo != null) {
            list_office_to.clear();
            String[][] data = MasterTableFiller.masterTables.TM_OFFICE.getData();
            for (int i = 0; i < data.length; i++) {
                if (data[i][13].equals(stateTempTo)) {
                    list_office_to.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        }
    }

    public void officeTempToListner(AjaxBehaviorEvent event) {
        int off_cd = getTempReg() != null ? getTempReg().getTmp_off_cd() : 0;
        if (off_cd != 0) {
            List<Dealer> listDealer = ServerUtil.getDealerList(getTempReg().getTmp_state_cd(), getTempReg().getTmp_off_cd());
            if (listDealer == null || listDealer.isEmpty()) {
                listDealer = ServerUtil.getDealerList(stateCodeSelected, offCodeSelected);
            }

            list_dealer_cd.clear();
            for (Dealer dealer : listDealer) {
                list_dealer_cd.add(new SelectItem(dealer.getDealer_cd(), dealer.getDealer_name()));
            }
        }
    }

    public void dealerTempToListner(AjaxBehaviorEvent event) {
        getOwnerDobj().setDealer_cd(getTempReg().getDealer_cd());
    }

    public void vehC_StateListener(AjaxBehaviorEvent event) {

        String sel_c_state = getOwnerDobj().getC_state();
        String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
        list_c_district.clear();
        for (int i = 0; i < data.length; i++) {
            if (data[i][2].equals(sel_c_state)) {
                list_c_district.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
    }

    public void vehP_StateListener(AjaxBehaviorEvent event) {

        String sel_p_state = getOwnerDobj().getP_state();
        String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
        list_p_district.clear();
        for (int i = 0; i < data.length; i++) {
            if (data[i][2].equals(sel_p_state)) {
                list_p_district.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
    }

    public void vehTypeListener() throws VahanException {
        String vehtype = getVehType();//(String) event.getNewValue();
        if (vehtype == null) {
            return;
        }

        if (getVehType().equals("1")) {
            renderIsSpeedGov = true;
            if (tmConfigFitnessDobj != null && tmConfigFitnessDobj.isReflective_tape_allowed()) {
                renderIsReflectiveTape = true;
            }

        }
        ownerDobj.getTaxModList().clear();
        if (tmConfDobj != null && ownerDobj != null && Integer.parseInt(vehtype) == TableConstants.VM_VEHTYPE_NON_TRANSPORT && ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY) && purCd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
            if (Util.getUserCategory() != null && Util.getUserCategory().equals(TableConstants.USER_CATG_DEALER) && tmConfDobj.getTmConfigDealerDobj() != null && !tmConfDobj.getTmConfigDealerDobj().isTempRegnAllowNonTransVeh()) {
                if (tmConfDobj != null && temp_dobj != null && tmConfDobj.isTempFeeInNewRegis() && "OR".equals(temp_dobj.getPurpose()) && Util.getUserStateCode().equals(temp_dobj.getState_cd_to())) {
                    if (ServerUtil.getVahan4StartDate(Util.getUserStateCode(), temp_dobj.getOff_cd_to()) != null) {
                        setVehType("-1");
                        JSFUtils.showMessagesInDialog("Alert !!", "VAHAN 4 Implemented in selected office and you are authorized to register the Non-Transport vehicle directly in this RTO. Please select the option 'Entry-New Registration' at Home Page.", FacesMessage.SEVERITY_WARN);
                        return;
                    }
                }
            }
        }
        boolean flag = this.transportCatgValidation(vehtype);
        if (flag) {
            JSFUtils.showMessagesInDialog("Alert !!", TableConstants.VEH_CATG_ERR_MESS, FacesMessage.SEVERITY_WARN);
            return;
        }
        NewVehicleFitnessImpl newVehicleFitnessImpl = new NewVehicleFitnessImpl();
        boolean isNewVehicleFitness = newVehicleFitnessImpl.checkForPreRegFitness(this.getOwnerDobj().getChasi_no(), this.getOwnerDobj().getEng_no());
        FitnessImpl fitnessImpl = new FitnessImpl();
        TmConfigurationFitnessDobj tmConfigurationFitnessDobj = fitnessImpl.getFitnessConfiguration(stateCodeSelected);
        if (!isNewVehicleFitness
                && this.getOwnerDobj().getFit_upto() == null
                && TableConstants.VM_VEHTYPE_TRANSPORT == Integer.parseInt(vehtype)
                && tmConfigurationFitnessDobj.isNewVehicleFitness()
                && this.getOwnerDobj().getChasi_no() != null
                && this.getOwnerDobj().getEng_no() != null
                && Util.getSelectedSeat() != null
                && Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ROLE_NEW_APPL
                && (getOwnerDobj().getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_NEW) || getOwnerDobj().getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_TEMPORARY))
                && (Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE)) {
            throw new VahanException("New Module is Introduce For  New Vehicle  Fitness ,Please Use  [ Chassis No: " + this.getOwnerDobj().getChasi_no() + "], [Engine No:" + this.getOwnerDobj().getEng_no() + "] in  New Vehicle  Fitness Module ");

        }

        if (Integer.parseInt(vehtype) == TableConstants.VM_VEHTYPE_TRANSPORT) {
            setAxleDetail_Visibility_tab(true);
            setDisableLdWtCap(false);
            setDisableUnLdWtCap(false);
            permitPanel = true;
            getOwnerDobj().setVehType(TableConstants.VM_VEHTYPE_TRANSPORT);
        } else if (Integer.parseInt(vehtype) == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
            setAxleDetail_Visibility_tab(false);
            if (this.getIsHomologationData() != null && !this.getIsHomologationData().equals("") && this.getIsHomologationData().equals(TableConstants.HOMOLOGATION_DATA)) {
                if ((purCd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                        || purCd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE)
                        && (getOwnerDobj().getRegn_type() != null
                        && getOwnerDobj().getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_TEMPORARY))) {
                    if (tempLdWt != 0 && tempUnLdWt != 0) {
                        getOwnerDobj().setLd_wt(tempLdWt);
                        getOwnerDobj().setUnld_wt(tempUnLdWt);
                    }
                } else {
                    getOwnerDobj().setLd_wt(homoLdWt);
                    getOwnerDobj().setUnld_wt(homoUnLdWt);
                }
                if (userCatgForLoggedInUser != null && userCatgForLoggedInUser.equals(TableConstants.User_Dealer)) {
                    setDisableLdWtCap(true);
                    setDisableUnLdWtCap(true);
                } else {
                    if (vch_purchase_as != null && !vch_purchase_as.equals(TableConstants.PURCHASE_AS_CHASIS)) {
                        setDisableLdWtCap(true);
                        setDisableUnLdWtCap(true);
                    } else {
                        setDisableLdWtCap(false);
                        setDisableUnLdWtCap(false);
                    }
                }
            }
            permitPanel = false;
            getOwnerDobj().setVehType(TableConstants.VM_VEHTYPE_NON_TRANSPORT);
        }

        //for not showing permit details
        if (permitPanel) {
            if (purCd != 0) {
                if (purCd != TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                        && purCd != TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                    permitPanel = false;
                }
            } else {
                if (Util.getSelectedSeat() != null && Util.getSelectedSeat().getPur_cd() != TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                        && Util.getSelectedSeat().getPur_cd() != TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                    permitPanel = false;
                }
            }
        }

        String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();

        if (vehtype.equalsIgnoreCase("-1")) {
            return;
        }

        if (authorityDobj == null) {
            throw new VahanException("You Don't have Permission to do this Operation!");
        }

        WorkBench wb = (WorkBench) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("workBench");
        if (wb != null && authorityDobj != null && stateCodeSelected.equals("HR")
                && (wb.getRegnType().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE) || wb.getRegnType().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))) {
            vehtype = String.valueOf(authorityDobj.getVehType());
        }

        list_vh_class.clear();
        for (int i = 0; i < data.length; i++) {
            if (data[i][2].equals(vehtype)) {
                for (Object obj : authorityDobj.getSelectedVehClass()) {
                    if (String.valueOf(obj).equalsIgnoreCase("ANY")) {
                        list_vh_class.add(new SelectItem(data[i][0], data[i][1]));
                    } else {
                        if (String.valueOf(obj).equalsIgnoreCase(data[i][0])) {
                            list_vh_class.add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                }
            }

        }

        if (getOwnerDobj().getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT
                && Util.getSelectedSeat() != null
                && (actionCodeSelected == TableConstants.TM_NEW_RC_FITNESS_INSPECTION)) {
            this.inspDobj.setMinDt(getOwnerDobj().getPurchase_dt());
            renderInspectionPanelNT = true;
        } else {
            renderInspectionPanelNT = false;
        }
        if (actionCodeSelected == TableConstants.TM_TMP_RC_FITNESS_INSPECTION) {
            renderInspectionPanelNT = true;
            renderInspectionPanel = false;
        }

        fitnessPanelLoader();
    }

    public void vehTypeListener_partial() {
        String vehtype = getVehType();//(String) event.getNewValue();
        try {
            if (vehtype == null) {
                return;
            }
            String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
            if (vehtype.equalsIgnoreCase("-1")) {
                return;
            }

            if (authorityDobj == null) {
                throw new VahanException("You Don't have Permission to do this Operation!");
            }
// Check For New Vehicle Fitness-Start
            NewVehicleFitnessImpl newVehicleFitnessImpl = new NewVehicleFitnessImpl();
            boolean isNewVehicleFitness = newVehicleFitnessImpl.checkForPreRegFitness(this.getOwnerDobj().getChasi_no(), this.getOwnerDobj().getEng_no());
            FitnessImpl fitnessImpl = new FitnessImpl();
            TmConfigurationFitnessDobj tmConfigurationFitnessDobj = fitnessImpl.getFitnessConfiguration(stateCodeSelected);
            if (!isNewVehicleFitness
                    && this.getOwnerDobj().getFit_upto() == null
                    && TableConstants.VM_VEHTYPE_TRANSPORT == Integer.parseInt(vehtype)
                    && tmConfigurationFitnessDobj.isNewVehicleFitness()
                    && this.getOwnerDobj().getChasi_no() != null
                    && this.getOwnerDobj().getEng_no() != null
                    && Util.getSelectedSeat() != null
                    && Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ROLE_NEW_APPL
                    && (getOwnerDobj().getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_NEW) || getOwnerDobj().getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_TEMPORARY))
                    && (Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE)) {
                throw new VahanException("New Module is Introduce For  New Vehicle  Fitness ,Please Use  [ Chassis No: " + this.getOwnerDobj().getChasi_no() + "], [Engine No:" + this.getOwnerDobj().getEng_no() + "] in  New Vehicle  Fitness Module ");
            }
// Check For New Vehicle Fitness-End

            // for HR
            boolean flag = this.transportCatgValidation(vehtype);
            if (flag) {
                JSFUtils.showMessagesInDialog("Alert !!", TableConstants.VEH_CATG_ERR_MESS, FacesMessage.SEVERITY_WARN);
                return;
            }
            list_vh_class.clear();
            for (int i = 0; i < data.length; i++) {
                if (data[i][2].equals(vehtype)) {
                    for (Object obj : authorityDobj.getSelectedVehClass()) {
                        if (String.valueOf(obj).equalsIgnoreCase("ANY")) {
                            list_vh_class.add(new SelectItem(data[i][0], data[i][1]));
                        } else {
                            if (String.valueOf(obj).equalsIgnoreCase(data[i][0])) {
                                list_vh_class.add(new SelectItem(data[i][0], data[i][1]));
                            }
                        }
                    }
                }

            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert !!", ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "" + e.getStackTrace());
        }
    }

    public boolean transportCatgValidation(String vehtype) {
        boolean flag = false;
        if (tmConfDobj != null && ownerDobj != null && tmConfDobj.getTmConfigDealerDobj() != null && Integer.parseInt(vehtype) == TableConstants.VM_VEHTYPE_TRANSPORT && ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_NEW) && purCd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
            if (!tmConfDobj.getTmConfigDealerDobj().isNewRegnNotAllowTransVeh()) {
                flag = true;
                setVehType("-1");
                list_vh_class.clear();
                ownerDobj.setVh_class(-1);
            }
        }
        return flag;
    }

    public void vehClassListener() throws VahanException {
        String vehClass = String.valueOf(getOwnerDobj().getVh_class());
        String[][] dataMap = MasterTableFiller.masterTables.VM_VHCLASS_CATG_MAP.getData();
        String[][] dataCatg = MasterTableFiller.masterTables.VM_VCH_CATG.getData();
        list_vm_catg.clear();
        for (int i = 0; i < dataMap.length; i++) {
            if (dataMap[i][0].equals(vehClass)) {
                for (int j = 0; j < dataCatg.length; j++) {
                    if (dataCatg[j][0].equals(dataMap[i][1])) {
                        list_vm_catg.add(new SelectItem(dataCatg[j][0], dataCatg[j][1]));
                    }
                }
            }
        }

        owner_identification.setDisableAdhaar(false);
        if (vehClass != null && !(Integer.parseInt(vehClass) == TableConstants.ERICKSHAW_VCH_CLASS && Util.getUserStateCode().equalsIgnoreCase("DL"))) {
            owner_identification.setAadhar_no("");
            owner_identification.setDisableAdhaar(true);
        }
        if (JSFUtils.findComponentById("masterLayout:workbench_tabview:pn_own_identity", "tf_aadhar")) {
            PrimeFaces.current().ajax().update("workbench_tabview:tf_aadhar");
        }
        if (vehClass != null) {
            axleDetail_Visibility_tab = AxleImpl.isAxelDetailRequired(Integer.parseInt(vehClass), state_cd);
            taxModeLoader(vehType, ownerDobj);
        }

        if (getVehType() != null && getVehType().equals("1")) {
            renderIsSpeedGov = true;
            if (tmConfigFitnessDobj != null && tmConfigFitnessDobj.isReflective_tape_allowed()) {
                renderIsReflectiveTape = true;
            }

        }

        if (TableConstants.GCW_VEH_CLASS.contains("," + vehClass + ",")) {
            renderGCW = true;
        } else {
            renderGCW = false;
        }

        if (Util.getSelectedSeat() != null
                && (actionCodeSelected == TableConstants.TM_NEW_RC_FITNESS_INSPECTION || actionCodeSelected == TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL)) {
            if (this.getIsHomologationData() != null && !this.getIsHomologationData().equals("") && this.getIsHomologationData().equals(TableConstants.HOMOLOGATION_DATA)) {
                if (stateCodeSelected != null && stateCodeSelected.equals("JH")) {
                    if (getVehType() != null && getVehType().equals("1")) {
                        String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
                        String transCatg = null;
                        for (int i = 0; i < data.length; i++) {
                            if (data[i][0].equalsIgnoreCase(vehClass)) {
                                transCatg = data[i][3];
                                break;
                            }
                        }
                        if (transCatg != null && transCatg.equals("P")) {
                            setDisableSeatCap(false);
                        } else {
                            if (homoDobj != null) {
                                getOwnerDobj().setSeat_cap(homoDobj.getSeat_cap());
                            }
                            setDisableSeatCap(true);
                        }
                    } else {
                        if (homoDobj != null) {
                            getOwnerDobj().setSeat_cap(homoDobj.getSeat_cap());
                        }
                        setDisableSeatCap(true);
                    }
                }
            }
        }

        if (permitPanel) {
            //for checking mandatory fields for permit type and permit category
            String taxRqrdField[] = ServerUtil.getFieldsReqForTax(stateCodeSelected, ownerDobj.getVh_class());
            if (taxRqrdField != null && taxRqrdField.length > 0) {
                isPmtTypeRqrd = false;
                isPmtCatgRqrd = false;
                for (int i = 0; i < taxRqrdField.length; i++) {
                    if (taxRqrdField[i].equalsIgnoreCase(TableConstants.VM_PMT_TYPE_IN_TAX_CODE)) {
                        isPmtTypeRqrd = true;
                    }
                    if (taxRqrdField[i].equalsIgnoreCase(TableConstants.VM_PMT_CATG_IN_TAX_CODE)) {
                        isPmtTypeRqrd = true;
                        isPmtCatgRqrd = true;
                        break;
                    }
                }
            }

            //############################Filteration of Permit Type Start###############
            String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
            String transportCatg = null;
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equalsIgnoreCase(String.valueOf(ownerDobj.getVh_class()))) {
                    transportCatg = data[i][3];
                    break;
                }
            }

            if (transportCatg != null) {
                data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
                ownerDobj.getPermitTypeList().clear();
                ownerDobj.getPermitCategoryList().clear();
                for (int i = 0; i < data.length; i++) {
                    if (data[i][2].equalsIgnoreCase(transportCatg)) {
                        ownerDobj.getPermitTypeList().add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
            }
            permitTypeChangeListener();

            //Add for permit service
            // pmt_service_type_list = new ArrayList();
            ownerDobj.getPmtServiceTypeList().clear();
            String[][] data1 = MasterTableFiller.masterTables.vm_service_type.getData();
            ownerDobj.getPmtServiceTypeList().add(new SelectItem("", ""));
            for (int i = 0; i < data1.length; i++) {
                ownerDobj.getPmtServiceTypeList().add(new SelectItem(data1[i][0], data1[i][1]));
            }
            //############################Filteration of Permit Type End###############
        }
        //Owner Category Check for Orissa        
        if ("OR,GA".contains(stateCodeSelected) && owner_identification != null) {
            if (owner_identification.getOwnerCatg() == TableConstants.OWNER_CATG_PHYSICALLY_HANDICAPPED
                    && vehClass.equalsIgnoreCase(TableConstants.INVALID_CARRIAGE)) {
                renderTaxExemption = true;
            } else {
                renderTaxExemption = false;
            }
        }

        if (ownerDobj.getVch_catg() != null
                && ownerDobj.getVh_class() > 0
                && ownerDobj.getRqrd_tax_modes() != null
                && ownerDobj.getRqrd_tax_modes().length() > 0) {

            Map<Integer, String> rqrdTaxModes = NewImpl.getRqrdTaxModes(ownerDobj.getRqrd_tax_modes());

            if (rqrdTaxModes != null && !rqrdTaxModes.isEmpty()) {
                for (int i = 0; i < ownerDobj.getTaxModList().size(); i++) {
                    for (Map.Entry<Integer, String> entry : rqrdTaxModes.entrySet()) {
                        if (entry.getKey() == ownerDobj.getTaxModList().get(i).getPur_cd()) {
                            ownerDobj.getTaxModList().get(i).setTaxMode(entry.getValue());
                            break;
                        }
                    }
                }
            }
        }

        if (actionCodeSelected != TableConstants.TM_ROLE_NEW_APPL
                && actionCodeSelected != TableConstants.TM_ROLE_DEALER_NEW_APPL
                && actionCodeSelected != TableConstants.TM_ROLE_DEALER_TEMP_APPL
                && actionCodeSelected != TableConstants.TM_ROLE_NEW_APPL_TEMP
                && (ownerDobj.getRqrd_tax_modes() == null || ownerDobj.getRqrd_tax_modes().length() <= 0)) {
            renderTaxPanel = false;
        }

        if (!("NL,OR".contains(stateCodeSelected)) && TableConstants.TRAILER_VEH_CLASS.contains("," + vehClass + ",")) {
            setLinked_regnNo_tab(true);
        } else {
            setLinked_regnNo_tab(false);
        }

        if (stateCodeSelected != null && stateCodeSelected.equals("OR")) {
            if (vehClass != null && vehClass.equals(TableConstants.PRIVATE_SERVICE_VEHICLE)) {
                ownerDobj.setStand_cap(0);
                this.setDisableStandCap(true);
            } else {
                this.setDisableStandCap(false);
            }
        }
        //PUSHBACK KL TARUN
        if (Util.getUserStateCode().equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(getOwnerDobj().getVh_class()) + ",")) {
            setPushBkSeatRender(true);
        } else {
            setPushBkSeatRender(false);
            ownerDobj.setPush_bk_seat(0);
            ownerDobj.setOrdinary_seat(0);

        }
    }

    public void validateChoiceNoRegnSeries() throws VahanException {
        WorkBench workbench = null;
        try {
            workbench = (WorkBench) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("workBench");
            if (workbench != null && ownerDobj != null && tmConfDobj != null && tmConfDobj.getTmConfigDealerDobj() != null) {
                VehicleParameters vehParameters = fillVehicleParametersFromDobj(ownerDobj);
                if (isCondition(replaceTagValues(tmConfDobj.getTmConfigDealerDobj().getOwnerChoiceConditionFormula(), vehParameters), "validateChoiceNoRegnSeries")) {
                    if (workbench.getOwnerChoiceNoBean() != null) {
                        String regnSeries = ServerUtil.getAvailablePrefixSeries(vehParameters);
                        workbench.getOwnerChoiceNoBean().setPrefixRegnSeries(regnSeries);
                        workbench.getOwnerChoiceNoBean().setRenderChoiceNopanel(false);
                        workbench.setRenderOwnerChoiceNoPanel(true);
                        workbench.getOwnerChoiceNoBean().setRenderBookedChoiceNoBtn(false);
                        workbench.getOwnerChoiceNoBean().setRenderClearChoiceNo(false);
                        workbench.getOwnerChoiceNoBean().setVehicleRegnGenType("-1");
                        workbench.getOwnerChoiceNoBean().setRegnChoiceAmount(0);
                        workbench.getOwnerChoiceNoBean().setOwnerDobj(ownerDobj);
                        if (workbench.getOwnerChoiceNoBean().getRegnList() != null) {
                            workbench.getOwnerChoiceNoBean().getRegnList().clear();
                        }
                        PrimeFaces.current().ajax().update("workbench_tabview");
                    }
                } else {
                    workbench.setRenderAdvanceNoOption(false);
                    if (workbench.isRenderOwnerChoiceNoPanel()) {
                        workbench.setRenderOwnerChoiceNoPanel(false);
                        PrimeFaces.current().ajax().update("workbench_tabview");
                    }
                }
            }
        } catch (Exception e) {
            throw new VahanException("Problem in getting owner choice number details.");
        }
    }

    public void vehClassListener_partial() throws VahanException {
        String vehClass = String.valueOf(getOwnerDobj().getVh_class());
        owner_identification.setDisableAdhaar(false);
        if (vehClass != null && !(Integer.parseInt(vehClass) == TableConstants.ERICKSHAW_VCH_CLASS && Util.getUserStateCode().equalsIgnoreCase("DL"))) {
            owner_identification.setAadhar_no("");
            owner_identification.setDisableAdhaar(true);
        }
        String[][] dataMap = MasterTableFiller.masterTables.VM_VHCLASS_CATG_MAP.getData();
        String[][] dataCatg = MasterTableFiller.masterTables.VM_VCH_CATG.getData();
        list_vm_catg.clear();
        for (int i = 0; i < dataMap.length; i++) {
            if (dataMap[i][0].equals(vehClass)) {
                for (int j = 0; j < dataCatg.length; j++) {
                    if (dataCatg[j][0].equals(dataMap[i][1])) {
                        list_vm_catg.add(new SelectItem(dataCatg[j][0], dataCatg[j][1]));
                    }
                }
            }
        }
        if (tmConfDobj.isNew_reg_loi() && purCd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && getOwnerDobj().getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_NEW) && getOwnerDobj().getVh_class() == 57) {
            renderNewLoi = true;
        } else {
            renderNewLoi = false;
        }
        if (Util.getUserStateCode().equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(getOwnerDobj().getVh_class()) + ",")) {
            setPushBkSeatRender(true);
        } else {
            setPushBkSeatRender(false);
        }
    }

    public void authenticateLlNo() {
        boolean isTransport = false;
        String licensceNo = owner_identification.getDl_no();
        RetLLDetails llResponse = null;
        ResponceList dlResponse = null;
        try {
            if (licensceNo != null && !licensceNo.isEmpty()) {
                if (!ServerUtil.checkDLIsExist(licensceNo)) {
                    Object response = ServerUtil.getDLOrLLDetails(licensceNo, licenseType, DOBDate);
                    if (response != null) {
                        if (response instanceof RetLLDetails) {
                            llResponse = (RetLLDetails) response;
                        } else if (response instanceof ResponceList) {
                            dlResponse = (ResponceList) response;
                        }
                        if (licenseType.equals("LL")) {
                            if (llResponse != null && llResponse.getErormsg() == null) {
                                LlDetObj dobj = llResponse.getLldetObj();
                                if (dobj != null) {
                                    for (LlCovObj llCatg : dobj.getLlcovs()) {
                                        if (llCatg.getLcCovcd() == 69) {
                                            isTransport = true;
                                            LlicenceObj licenceDobj = dobj.getLlicenceObj();
                                            if (licenceDobj.getLlVldtodt().after(new Date())) {
                                                llServiceResponse = dobj.getBioObj();
                                                renderGenerateOTPBtn = true;
                                                renderAuthenticateBtn = false;
                                                owner_identification.setDlDisabled(true);
                                                //RequestContext rc = RequestContext.getCurrentInstance();
                                                PrimeFaces.current().executeScript("PF('drivingLicense_dlg1').show()");
                                                break;
                                            } else {
                                                throw new VahanException("LL validity expired.");
                                            }
                                        }
                                    }
                                    if (!isTransport) {
                                        throw new VahanException("LL not valid for transport vehicle.");
                                    }
                                } else {
                                    throw new VahanException("Details not found for licence number.");
                                }
                            } else {
                                throw new VahanException("Invalid licence number or DOB.");
                            }
                        } else if (licenseType.equals("DL")) {
                            if (dlResponse != null) {
                                DlDetObj[] ownerDLDetails = dlResponse.getDldetObj();
                                if (ownerDLDetails != null) {
                                    for (DlDetObj dlDetObj : ownerDLDetails) {
                                        String errmsg = dlDetObj.getErormsg();
                                        if (errmsg == null) {
                                            for (DlCovObj dlCatg : dlDetObj.getDlcovs()) {
                                                if ("TR".equals(String.valueOf(dlCatg.getVecatg()).trim())) {
                                                    isTransport = true;
                                                    llServiceResponse = dlDetObj.getBioObj();
                                                    renderGenerateOTPBtn = true;
                                                    renderAuthenticateBtn = false;
                                                    owner_identification.setDlDisabled(true);
                                                    //RequestContext rc = RequestContext.getCurrentInstance();
                                                    PrimeFaces.current().executeScript("PF('drivingLicense_dlg1').show()");
                                                    break;
                                                }
                                            }
                                            if (!isTransport) {
                                                throw new VahanException("DL not valid for transport vehicle.");
                                            }
                                        } else {
                                            throw new VahanException("Invalid licence number or DOB.");
                                        }
                                    }
                                }
                            } else {
                                throw new VahanException("Unable to get the response from DL service. Try again.");
                            }
                        }
                    } else {
                        throw new VahanException("Invalid response or Invalid LL/DL/DOB. Please check.");
                    }
                } else {
                    throw new VahanException("LL/DL no. already used. Please try another no.");
                }
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert !!", ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "" + e.getStackTrace());
        }
    }

    public void resetVehClass() {
        ownerDobj.setVch_catg("-1");
        ownerDobj.setVh_class(-1);
        list_vm_catg.clear();
        sendOTP = "";
        enteredOTP = "";
        DOBDate = "";
        setErrorMsg(null);
        llServiceResponse = null;
        renderGenerateOTPBtn = false;
        renderAuthenticateBtn = true;
        renderedConfirmBtn = false;
        setDisableFName(false);
        setDisableOwnerName(false);
        owner_identification.setDl_no("");
        PrimeFaces.current().ajax().update("workbench_tabview:drivingLicense");
        PrimeFaces.current().executeScript("PF('drivingLicense_dlg1').hide();");
    }

    public void generateOtp(String genType) throws VahanException, Exception {
        String mobileNo = String.valueOf(owner_identification.getMobile_no());
        //RequestContext rcExe = RequestContext.getCurrentInstance();
        try {
            if (mobileNo != null) {
                if (genType != null && genType.equals("generate")) {
                    sendOTP = new ServerUtil().genOTP(mobileNo);
                    String otp_msg = "OTP for VAHAN4 user is " + sendOTP + ". Do not share it with anyone.";
                    ServerUtil.sendOTP(mobileNo, otp_msg, Util.getUserStateCode());
                    renderGenerateOTPBtn = false;
                    renderedConfirmBtn = true;
                    owner_identification.setDl_no(owner_identification.getDl_no());
                    PrimeFaces.current().executeScript("PF('drivingLicense_dlg1').show()");
                } else if (genType != null && genType.equals("regenerate")) {
                    renderedConfirmBtn = true;
                    enteredOTP = "";
                    setErrorMsg(null);
                    owner_identification.setDl_no(owner_identification.getDl_no());
                    String otp_msg = "OTP for VAHAN4 user is " + sendOTP + ". Do not share it with anyone.";
                    ServerUtil.sendOTP(mobileNo, otp_msg, Util.getUserStateCode());
                    PrimeFaces.current().executeScript("PF('drivingLicense_dlg1').show()");
                } else if (genType != null && genType.equals("confirm")) {
                    if (sendOTP.equals(enteredOTP)) {
                        if (llServiceResponse != null) {
                            if (!CommonUtils.isNullOrBlank(llServiceResponse.getBioPermAdd1())) {
                                if (llServiceResponse.getBioPermAdd1().length() >= 35) {
                                    this.ownerDobj.setP_add1(llServiceResponse.getBioPermAdd1().substring(0, 35));
                                } else {
                                    this.ownerDobj.setP_add1(llServiceResponse.getBioPermAdd1());
                                }
                            }
                            if (!CommonUtils.isNullOrBlank(llServiceResponse.getBioPermAdd2())) {
                                if (llServiceResponse.getBioPermAdd2().length() >= 35) {
                                    this.ownerDobj.setP_add2(llServiceResponse.getBioPermAdd2().substring(0, 35));
                                } else {
                                    this.ownerDobj.setP_add2(llServiceResponse.getBioPermAdd2());
                                }
                            }
                            if (!CommonUtils.isNullOrBlank(llServiceResponse.getBioPermAdd3())) {
                                if (llServiceResponse.getBioPermAdd3().length() >= 35) {
                                    this.ownerDobj.setP_add3(llServiceResponse.getBioPermAdd3().substring(0, 35));
                                } else {
                                    this.ownerDobj.setP_add3(llServiceResponse.getBioPermAdd3());
                                }
                            }
                            if (!CommonUtils.isNullOrBlank(llServiceResponse.getBioTempAdd1())) {
                                if (llServiceResponse.getBioTempAdd1().length() >= 35) {
                                    this.ownerDobj.setC_add1(llServiceResponse.getBioTempAdd1().substring(0, 35));
                                } else {
                                    this.ownerDobj.setC_add1(llServiceResponse.getBioTempAdd1());
                                }
                            }
                            if (!CommonUtils.isNullOrBlank(llServiceResponse.getBioTempAdd2())) {
                                if (llServiceResponse.getBioTempAdd2().length() >= 35) {
                                    this.ownerDobj.setC_add2(llServiceResponse.getBioTempAdd2().substring(0, 35));
                                } else {
                                    this.ownerDobj.setC_add2(llServiceResponse.getBioTempAdd2());
                                }
                            }
                            if (!CommonUtils.isNullOrBlank(llServiceResponse.getBioTempAdd3())) {
                                if (llServiceResponse.getBioTempAdd3().length() >= 35) {
                                    this.ownerDobj.setC_add3(llServiceResponse.getBioTempAdd3().substring(0, 35));
                                } else {
                                    this.ownerDobj.setC_add3(llServiceResponse.getBioTempAdd3());
                                }
                            }
                            if (!CommonUtils.isNullOrBlank(llServiceResponse.getBioSwdFullName())) {
                                if (llServiceResponse.getBioSwdFullName().length() >= 35) {
                                    this.ownerDobj.setF_name(llServiceResponse.getBioSwdFullName().substring(0, 35));
                                } else {
                                    this.ownerDobj.setF_name(llServiceResponse.getBioSwdFullName());
                                }
                            }
                            if (!CommonUtils.isNullOrBlank(llServiceResponse.getBioFullName())) {
                                if (llServiceResponse.getBioFullName().length() >= 35) {
                                    this.ownerDobj.setOwner_name(llServiceResponse.getBioFullName().substring(0, 35));
                                } else {
                                    this.ownerDobj.setOwner_name(llServiceResponse.getBioFullName());
                                }
                            }
                            if (llServiceResponse.getBioPermPin() != null && llServiceResponse.getBioPermPin() != 0) {
                                this.ownerDobj.setP_pincode(llServiceResponse.getBioPermPin());
                            }
                            if (llServiceResponse.getBioTempPin() != null && llServiceResponse.getBioTempPin() != 0) {
                                this.ownerDobj.setC_pincode(llServiceResponse.getBioTempPin());
                            }
                            this.ownerDobj.setP_district(-1);
                            this.ownerDobj.setC_district(94);
                            this.ownerDobj.setP_state("-1");
                            this.ownerDobj.setC_state("DL");
                            setBlnAdvancedRegNo(true);
                            owner_identification.setDl_no(owner_identification.getDl_no());
                            owner_identification.setDlDisabled(true);
                            owner_identification.setMobileNoEditable(true);
                            setDisableFName(true);
                            setDisableOwnerName(true);
                            vehicleComponentEditable = true;
                        }
                        PrimeFaces.current().executeScript("PF('drivingLicense_dlg1').hide();");
                        PrimeFaces.current().executeScript("PF('onAuthComplete').show();");
                    } else {
                        setErrorMsg(null);
                        setErrorMsg("Alert!! OTP not matched. Please try again.");

                        PrimeFaces.current().executeScript("PF('drivingLicense_dlg1').show();");
                    }
                }
                System.out.println("OTP :" + sendOTP);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!!", "Mobile no is not valid"));
            }
        } catch (VahanException ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert !!", ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void vchCatgListener_partial() {
        int vehClass = getOwnerDobj().getVh_class();
        if (vehClass != 0 && TableConstants.ERICKSHAW_VCH_CLASS == vehClass && "DL".contains(stateCodeSelected) && TableConstants.USER_CATG_DEALER.equals(Util.getUserCategory())) {
            if (ownerDobj.getOwner_cd() == TableConstants.OWNER_CODE_INDIVIDUAL) {
                //RequestContext rc = RequestContext.getCurrentInstance();
                licenseType = "LL";
                PrimeFaces.current().ajax().update("workbench_tabview:drivingLicense");
                PrimeFaces.current().executeScript("PF('drivingLicense_dlg1').show()");
            }
        }

    }

    public void changeLicenceEvent() {
        renderGenerateOTPBtn = false;
        renderAuthenticateBtn = true;
        renderedConfirmBtn = false;
        DOBDate = "";
        errorMsg = null;
        owner_identification.setDlDisabled(false);
        //RequestContext rcExe = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('drivingLicense_dlg1').show()");
    }

    public void loiListener_partial() {
        try {
            PermitImpl.getVaPermitOwnerDetails(getOwnerDobj().getNewLoiNo(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), getOwnerDobj());
            getOwner_identification().setMobile_no(getOwnerDobj().getOwner_identity().getMobile_no());
            getOwner_identification().setEmail_id(getOwnerDobj().getOwner_identity().getEmail_id());
            getOwner_identification().setOwnerCatg(getOwnerDobj().getOwner_identity().getOwnerCatg());
            setDisableOwnerName(true);
            setDisableFName(true);
            setBlnAdvancedRegNo(true);
            setDisableOwnerCd(true);
            setDisablePermanentAddress(true);
            setVehicleComponentEditable(true);
            getOwner_identification().setOwnerCatgEditable(true);
            getOwner_identification().setMobileNoEditable(false);
            setDisableSamePerm(true);
            if (getOwnerDobj().getNewLoiNo() == null || getOwnerDobj().getNewLoiNo().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid LOI Number", "Invalid LOI Number"));
                return;
            }
        } catch (VahanException e) {
            getOwnerDobj().setNewLoiNo("");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
    }

    public void vehCatgListener(AjaxBehaviorEvent event) {
        try {
            if (TableConstants.USER_CATG_DEALER.equals(Util.getUserCategory())) {
                Map<String, String> choiceNoDetails = OwnerChoiceNoImpl.getOwnerChoiceRegnNoDetails(appl_no);
                if (choiceNoDetails != null && !choiceNoDetails.isEmpty()) {
                    if (ownerDobj != null && owner_dobj_prv != null) {
                        ownerDobj.setVch_catg(owner_dobj_prv.getVch_catg());
                        PrimeFaces.current().ajax().update("workbench_tabview:vh_catg");
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "As Choice No. has been booked, so, you can change the vehicle details. first clear the selected choice number.", "As Choice No. has been booked, so, you can change the vehicle details. first clear the selected choice number."));
                        return;
                    }
                }
                this.validateChoiceNoRegnSeries();
            }
            this.tradeCertificateValidation(this.getOwnerDobj().getDealer_cd(), this.getOwnerDobj().getVch_catg(), tmConfDobj);
            if (this.getOwnerDobj().getVch_catg().equals(TableConstants.TWO_WHEELER_NON_TRANSPORT) || this.getOwnerDobj().getVch_catg().equals(TableConstants.TWO_WHEELER_TRANSPORT)) {
                list_ac_audio_video_fitted.clear();
                list_ac_audio_video_fitted.add(new SelectItem("N", "NO"));
            } else {
                list_ac_audio_video_fitted.clear();
                list_ac_audio_video_fitted.add(new SelectItem("N", "NO"));
                list_ac_audio_video_fitted.add(new SelectItem("Y", "YES"));
            }
            Owner_dobj own_dobj = new Owner_dobj();
            own_dobj.setVch_catg(this.getOwnerDobj().getVch_catg());
            own_dobj.setVh_class(getOwnerDobj().getVh_class());
            own_dobj.setSeat_cap(ownerDobj.getSeat_cap());
            if (getOwnerDobj().getVh_class() > 0) {
                taxModeLoader(vehType, ownerDobj);
            }
        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
    }

    public void tradeCertificateValidation(String dealerCd, String vchCatg, TmConfigurationDobj tmConfDobj) throws VahanException {
        String user_catg = Util.getSession().getAttribute("user_catg").toString();
        if (user_catg != null && !user_catg.equals("") && dealerCd != null && !dealerCd.equals("") && vchCatg != null && !vchCatg.equals("") && tmConfDobj != null) {
            boolean isConsiderTradeCert = false;
            if (user_catg.equals(TableConstants.USER_CATG_OFF_STAFF) && tmConfDobj.isConsiderTradeCert()) {
                boolean isRegisteredDealer = ServerUtil.getRegisteredDealerInfo(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), dealerCd);
                if (isRegisteredDealer) {
                    isConsiderTradeCert = true;
                }
            }

            if (isConsiderTradeCert || TableConstants.User_Dealer.equals(user_catg)) {
                setTradeCertExpireMess(ServerUtil.getDealerTradeCertificateDetails(dealerCd, vchCatg, stateCodeSelected, tmConfDobj));
                if (!CommonUtils.isNullOrBlank(getTradeCertExpireMess())) {
                    throw new VahanException(getTradeCertExpireMess());
                }
            }
        }
    }

    public void seatListener(AjaxBehaviorEvent event) {
        try {
            fitnessPanelLoader();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void ladenWeightTypeListener(AjaxBehaviorEvent event) {
        try {
            if (getOwnerDobj().getLd_wt() > 0) {
                taxModeLoader(vehType, ownerDobj);
            }

            this.checkLadenWeight(getOwnerDobj().getVch_catg(), getOwnerDobj().getLd_wt(), purCd, getOwnerDobj().getVch_purchase_as());

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
    }

    public void acFittedChangeListener(AjaxBehaviorEvent event) {
        try {
            if (getOwnerDobj().getAc_fitted() != null && getOwnerDobj().getAc_fitted().length() > 0) {
                taxModeLoader(vehType, ownerDobj);
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
    }

    public void audioFittedChangeListener(AjaxBehaviorEvent event) {
        try {
            if (getOwnerDobj().getAudio_fitted() != null && getOwnerDobj().getAudio_fitted().length() > 0) {
                taxModeLoader(vehType, ownerDobj);
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
    }

    public void videoFittedChangeListener(AjaxBehaviorEvent event) {
        try {
            if (getOwnerDobj().getVideo_fitted() != null && getOwnerDobj().getVideo_fitted().length() > 0) {
                taxModeLoader(vehType, ownerDobj);
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
    }

    public void otherCriteriaChangeListener(AjaxBehaviorEvent event) {
        try {
            if (other_criteria != null && other_criteria >= 0) {
                ownerDobj.setOther_criteria(other_criteria);
                taxModeLoader(vehType, ownerDobj);
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
    }

    public void purchaseAsChangeListener(AjaxBehaviorEvent event) {
        try {
            if (stateCodeSelected != null && !stateCodeSelected.equals("") && stateCodeSelected.equals("OR")) {
                if (this.getVch_purchase_as() != null && this.getVch_purchase_as().length() > 0) {
                    ownerDobj.setVch_purchase_as(this.getVch_purchase_as());
                    taxModeLoader(vehType, ownerDobj);
                }
            }
            fitnessPanelLoader();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
    }

    public void saleAmountChangeListener(AjaxBehaviorEvent event) {
        try {
            if (sale_amt != null && sale_amt > 0) {
                ownerDobj.setSale_amt(sale_amt);
                taxModeLoader(vehType, ownerDobj);
                OwnerIdentificationImpl ownerIdentificationImpl = new OwnerIdentificationImpl();
                OwnerIdentificationImpl identificationImpl = new OwnerIdentificationImpl();
                TmConfigurationOwnerIdentificationDobj tmConfigOwnerId = identificationImpl.getTmConfigurationOwnerIdentification(Util.getUserStateCode());
                VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
                if (tmConfigOwnerId != null && isCondition(replaceTagValues(tmConfigOwnerId.getPan_card_mandatory(), vehParameters), "getPan_card_mandatory1")) {
                    owner_identification.setPan_card_mandatory(true);
                } else {
                    owner_identification.setPan_card_mandatory(false);
                }
                saleAmtInWords = new Utility().ConvertNumberToWords(sale_amt).toLowerCase() + ". ";
                saleAmtInWords = "Rs. " + saleAmtInWords;
                if (JSFUtils.findComponentById("preRegFitness:work_bench", "sale_amt_word")) {
                    PrimeFaces.current().ajax().update("preRegFitness:work_bench:sale_amt_word");
                }
                PrimeFaces.current().ajax().update("workbench_tabview:sale_amt_word");
                PrimeFaces.current().ajax().update("workbench_tabview:pn_own_identity");
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
    }

    public void taxExemptionListener(AjaxBehaviorEvent event) {
        if (taxExemption && renderTaxPanel) {
            renderTaxPanel = false;
            ownerDobj.getTaxModList().clear();
            ownerDobj.setTaxModList(null);
            ownerDobj.setRqrd_tax_modes(null);
        } else {
            renderTaxPanel = true;
            List<TaxFormPanelBean> taxModList = new ArrayList();
            ownerDobj.setTaxModList(taxModList);
            taxModeLoader(vehType, ownerDobj);
        }
    }

    public void validateMonth(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        int month = 0;
        if (value != null && !value.equals("")) {
            month = Integer.parseInt(value.toString().trim());
        }

        if (month > 12 || month <= 0) {
            FacesMessage msg = new FacesMessage("Invalid Manufacture Month");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateYear(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        int year;
        year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR); //current year

        if (value != null && !value.equals("")) {
            if (value.toString().trim().length() != 4) {
                FacesMessage msg = new FacesMessage("Invalid Manufacture Year");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
            if (Integer.parseInt(value.toString().trim()) > year) {
                FacesMessage msg = new FacesMessage("Manufacture Year Can't Exceed from Currnet Year");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }

            if (this.getOwnerDobj().getManu_mon() == null) {
                FacesMessage msg = new FacesMessage("Blank Manufacture Month");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }

            if (Integer.parseInt(value.toString().trim()) == year) {
                int currMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1; //current month of the year
                if (this.getOwnerDobj().getManu_mon() != null) {
                    if (currMonth < this.getOwnerDobj().getManu_mon()) {
                        FacesMessage msg = new FacesMessage("Manufacture Month Can't Exceed from Current Month of the Year");
                        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                        throw new ValidatorException(msg);
                    }
                }
            }

            if (this.ownerDobj.getPurchase_dt() != null && this.getOwnerDobj().getManu_mon() != null) {
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.clear();
                cal.set(Integer.parseInt(value.toString().trim()), this.getOwnerDobj().getManu_mon() - 1, 1);

                Date manuFDate = cal.getTime();

                if (manuFDate.compareTo(this.ownerDobj.getPurchase_dt()) > 0) {
                    FacesMessage msg = new FacesMessage("Please Check Purchase Date Can't be less than Manufacture Month and Year.");
                    msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                    throw new ValidatorException(msg);
                }
            }
        }
    }

    public void validateLadenWeight(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (this.vehType.equalsIgnoreCase(String.valueOf(TableConstants.VM_VEHTYPE_TRANSPORT))) {
            int ulWeight = this.getOwnerDobj().getUnld_wt();
            if (ulWeight > Integer.parseInt(value.toString().trim())) {
                FacesMessage msg = new FacesMessage("Un-Laden Weight Must Less Than Laden Weight");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }

    public void validateGCW(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        int lWeight = this.getOwnerDobj().getLd_wt();
        if (value != null && Integer.parseInt(value.toString().trim()) != 0 && Integer.parseInt(value.toString().trim()) < lWeight) {
            FacesMessage msg = new FacesMessage("GCW must be greater than or equals to the laden weight.");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validatePermitType(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (permitPanel && value != null) {
            FacesMessage msg = null;
            if (isPmtTypeRqrd && Integer.parseInt(value.toString()) < 0) {
                msg = new FacesMessage("Invalid Permit Type, Please Select Valid Permit Type");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }

    public void validatePermitCatg(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (permitPanel && value != null) {
            FacesMessage msg = null;
            if (isPmtTypeRqrd && Integer.parseInt(value.toString()) < 0) {
                msg = new FacesMessage("Invalid Permit Category, Please Select Valid Permit Category");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }

    public void verifyVehClassForTrailer() {
        try {
            OwnerImpl implObj = new OwnerImpl();
            OwnerDetailsDobj dobj = implObj.getOwnerDetails(this.getOwnerDobj().getLinkedRegnNo(), stateCodeSelected, Util.getUserOffCode());
            if (dobj != null) {
                String vhClass = String.valueOf(dobj.getVh_class());
                linkedVehDtls = "Owner Name:" + dobj.getOwner_name() + " | Vehicle Class: " + dobj.getVh_class_desc();
                if (!TableConstants.TRACTOR_VEH_CLASS.contains("," + vhClass + ",")) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Trailer can not be attached to Vehicle Class of Registration No :" + this.getOwnerDobj().getLinkedRegnNo(), null));
                    this.getOwnerDobj().setLinkedRegnNo(null);
//                } else if (!"RJ,DD".contains(stateCodeSelected)) {
//                    if (this.getOwnerDobj().getVehType() != dobj.getVehType()) {
//                        this.getOwnerDobj().setLinkedRegnNo(null);
//                        linkedVehDtls = "Regn No: " + dobj.getRegn_no() + " | " + linkedVehDtls;
//                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle Type (Transport / Non-Transport) Mismatch (Linked Vehicle & Current Vehicle Should be of Same Vehicle Type)", null));
//                    }
                }
            } else {
                linkedVehDtls = "";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle with Registration No :" + this.getOwnerDobj().getLinkedRegnNo() + " does not exist.", null));
            }
        } catch (VahanException ex) {
            LOGGER.error(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void homoDataListener(AjaxBehaviorEvent event) {
        try {
            OwnerImpl owImpl = new OwnerImpl();
            if (Util.getSelectedSeat() != null
                    && (actionCodeSelected == TableConstants.TM_NEW_RC_ADMIN_APPROVE
                    || actionCodeSelected == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_ENTRY
                    || actionCodeSelected == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_VERIFY
                    || actionCodeSelected == TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_ENTRY
                    || actionCodeSelected == TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_VERIFY
                    || actionCodeSelected == TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_APPROVAL)) {
                //this block will only work in owner admin form
                if (getOwnerDobj() != null && getOwnerDobj().getChasi_no() != null && getOwnerDobj().getEng_no() != null) {
                    ServerUtil.validateChassisEngineCombination(getOwnerDobj().getChasi_no(), getOwnerDobj().getEng_no());
                }
                return;
            }

            if (getOwnerDobj() != null && getOwnerDobj().getChasi_no() != null) {
                Owner_dobj owneTmpDobj = null;
                owneTmpDobj = owImpl.getValidateVtOwnerTempDobj(getOwnerDobj().getChasi_no());
            }
            if (getOwnerDobj().getRegn_type() != null && getOwnerDobj().getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_NEW)) {
                String chasiNo = getOwnerDobj().getChasi_no();
                String chassisNoExist = ServerUtil.getChassisNoExist(chasiNo);
                if (chassisNoExist != null && chassisNoExist.length() > 0 && !chassisNoExist.isEmpty()) {
                    throw new VahanException("Chassis No already exist - " + chassisNoExist);
                }
                Owner_dobj ownerHomo = ServerUtil.getOnwerDobjFromHomologation(chasiNo.trim(), getOwnerDobj().getEng_no(), stateCodeSelected, offCodeSelected, true);
                if (ownerHomo != null) {
                    WorkBench wb = (WorkBench) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("workBench");
                    if (wb != null) {
                        wb.setChasiNo(ownerHomo.getChasi_no());
                    }
                    getOwnerDobj().setEng_no(ownerHomo.getEng_no());
                    getOwnerDobj().setChasi_no(ownerHomo.getChasi_no());
                    getOwnerDobj().setMaker(ownerHomo.getMaker());

                    String[][] dataMap = null;
                    ArrayList list_maker_model = null;
                    dataMap = MasterTableFiller.masterTables.VM_MODELS.getData();
                    list_maker_model = new ArrayList();
                    for (int i = 0; i < dataMap.length; i++) {
                        if (dataMap[i][1].equals(ownerHomo.getMaker_model())) {
                            this.list_maker_model.add(new SelectItem(dataMap[i][1], dataMap[i][2]));
                            break;
                        }
                    }
                    getOwnerDobj().setMaker_model(ownerHomo.getMaker_model());
                    getOwnerDobj().setVch_purchase_as(ownerHomo.getVch_purchase_as());
                    getOwnerDobj().setHp(ownerHomo.getHp());
                    getOwnerDobj().setSeat_cap(ownerHomo.getSeat_cap());
                    getOwnerDobj().setUnld_wt(ownerHomo.getUnld_wt());
                    getOwnerDobj().setLd_wt(ownerHomo.getLd_wt());
                    getOwnerDobj().setGcw(ownerHomo.getGcw());
                    getOwnerDobj().setFuel(ownerHomo.getFuel());
                    getOwnerDobj().setBody_type(ownerHomo.getBody_type());
                    getOwnerDobj().setNo_cyl(ownerHomo.getNo_cyl());
                    getOwnerDobj().setWheelbase(ownerHomo.getWheelbase());
                    getOwnerDobj().setNorms(ownerHomo.getNorms());
                    getOwnerDobj().setDealer_cd(ownerHomo.getDealer_cd());
                    getOwnerDobj().setCubic_cap(ownerHomo.getCubic_cap());
                    getOwnerDobj().setVch_catg(ownerHomo.getVch_catg());
                    getOwnerDobj().setLength(ownerHomo.getLength());
                    getOwnerDobj().setWidth(ownerHomo.getWidth());
                    getOwnerDobj().setHeight(ownerHomo.getHeight());
                    getOwnerDobj().setRegn_dt(ownerHomo.getRegn_dt());
                    getOwnerDobj().setFeatureCode(ownerHomo.getFeatureCode());
                    getOwnerDobj().setColorCode(ownerHomo.getColorCode());
                    getOwnerDobj().setC_state(ownerHomo.getC_state());
                    getOwnerDobj().setManu_mon(ownerHomo.getManu_mon());
                    getOwnerDobj().setManu_yr(ownerHomo.getManu_yr());
                    getOwnerDobj().setColor(ownerHomo.getColor());

                    AxleDetailsDobj axleDobj = new AxleDetailsDobj();
                    getOwnerDobj().setAxleDobj(axleDobj);

                    getOwnerDobj().getAxleDobj().setTf_Front(ownerHomo.getAxleDobj().getTf_Front());
                    getOwnerDobj().getAxleDobj().setTf_Front1(ownerHomo.getAxleDobj().getTf_Front1());
                    getOwnerDobj().getAxleDobj().setTf_Rear(ownerHomo.getAxleDobj().getTf_Rear());
                    getOwnerDobj().getAxleDobj().setTf_Rear1(ownerHomo.getAxleDobj().getTf_Rear1());
                    getOwnerDobj().getAxleDobj().setTf_Other(ownerHomo.getAxleDobj().getTf_Other());
                    getOwnerDobj().getAxleDobj().setTf_Other1(ownerHomo.getAxleDobj().getTf_Other1());
                    getOwnerDobj().getAxleDobj().setTf_Tandem(ownerHomo.getAxleDobj().getTf_Tandem());
                    getOwnerDobj().getAxleDobj().setTf_Tandem1(ownerHomo.getAxleDobj().getTf_Tandem1());

                    getOwnerDobj().setPurchase_dt(ownerHomo.getPurchase_dt());
                    getOwnerDobj().setOwner_sr(ownerHomo.getOwner_sr());

                    if (ownerHomo.getSale_amt() != 0) {
                        this.setSale_amt(ownerHomo.getSale_amt());
                    }
                    this.disableForHomologationData(true);
                    this.setDealerSaleAmt(false);
                    this.setIsHomologationData(TableConstants.HOMOLOGATION_DATA);
                    this.setHomoLdWt(ownerHomo.getLd_wt());
                    this.setHomoUnLdWt(ownerHomo.getUnld_wt());
                    this.setHomoDobj(ownerHomo);
                    if (ownerHomo.getManu_mon() == null || ownerHomo.getManu_mon() == 0) {
                        this.setDisableManuMonth(false);
                    } else {
                        this.setDisableManuMonth(true);
                    }
                    if (ownerHomo.getManu_yr() == null || ownerHomo.getManu_yr() == 0) {
                        this.setDisableManuYr(false);
                    } else {
                        this.setDisableManuYr(true);
                    }
                    this.setRenderModelEditable(false);
                    if (ownerHomo.getVch_purchase_as() != null && ownerHomo.getVch_purchase_as().equals(TableConstants.PURCHASE_AS_CHASIS)) {
                        this.enableForDriveAwayChassisInDealer(false);
                    }
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.HOMOLOGATION_CHASI_NO_MESS, TableConstants.HOMOLOGATION_CHASI_NO_MESS));
                    PrimeFaces.current().ajax().update("chasi_no_new_entry");
                }
            }
            if (getOwnerDobj().getRegn_type() != null && (getOwnerDobj().getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                    || getOwnerDobj().getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))) {
                Owner_dobj chasiOwner = owImpl.set_Owner_appl_db_to_dobj(null, null, getOwnerDobj().getChasi_no(), purCd);
                WorkBench wb = (WorkBench) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("workBench");
                if (wb != null) {
                    if (chasiOwner != null && !chasiOwner.getRegn_no().equalsIgnoreCase(wb.getRegnNo())) {
                        throw new VahanException("Registration number and chassis number not matched");
                    }
                }

            }
        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
            getOwnerDobj().setChasi_no(null);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Technical Problem in Data", "Technical Problem in Data"));
            getOwnerDobj().setChasi_no(null);
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void setInspectionDobjToBean(InspectionDobj dobj) {
        if (dobj == null) {
            return;
        }
        this.ownerDobj.setInspectionDobj(dobj);
        this.inspDobj.setRemark(dobj.getRemark());
        this.inspDobj.setInsp_dt(dobj.getInsp_dt());
        this.inspDobj.setAppl_no(dobj.getAppl_no());
        this.inspDobj.setRegn_no(dobj.getRegn_no());
        this.inspDobj.setOff_cd(dobj.getOff_cd());
        this.inspDobj.setState_cd(dobj.getState_cd());
        this.inspDobj.setDisableInsDetail(dobj.isDisableInsDetail());
    }

    public void checkLadenWeight(String vchCatg, int ladenWeight, int purCd, String vchPurchaseAs) throws VahanException {
        if (!TableConstants.PURCHASE_AS_CHASIS.equals(vchPurchaseAs) && (purCd != TableConstants.VM_TRANSACTION_MAST_TEMP_REG && purCd != TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE)) {
            if (vchCatg != null && vchCatg.equals(TableConstants.LIGHT_GOODS_VEH) && ladenWeight > 7500) {
                throw new VahanException("Laden weight should not be greater than 7500 in case of (LGV).");
            } else if (vchCatg != null && vchCatg.equals(TableConstants.MEDIUM_GOODS_VEH) && (ladenWeight <= 7500 || ladenWeight > 12000)) {
                throw new VahanException("Laden weight should be greater than 7500 and less than or equal to 12000 in case of (MGV).");
            } else if (vchCatg != null && vchCatg.equals(TableConstants.HEAVY_GOODS_VEH) && ladenWeight <= 12000) {
                throw new VahanException("Laden weight should be greater than 12000 in case of (HGV).");
            }
        }
    }

    /**
     * @return the auctionVisibilityTab
     */
    public boolean isAuctionVisibilityTab() {
        return auctionVisibilityTab;
    }

    /**
     * @param auctionVisibilityTab the auctionVisibilityTab to set
     */
    public void setAuctionVisibilityTab(boolean auctionVisibilityTab) {
        this.auctionVisibilityTab = auctionVisibilityTab;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public List getList_c_district() {
        return list_c_district;
    }

    public void setList_c_district(List list_c_district) {
        this.list_c_district = list_c_district;
    }

    /**
     * @return the list_p_district
     */
    public List getList_p_district() {
        return list_p_district;
    }

    /**
     * @param list_p_district the list_p_district to set
     */
    public void setList_p_district(List list_p_district) {
        this.list_p_district = list_p_district;
    }

    /**
     * @return the list_p_state
     */
    public List getList_p_state() {
        return list_p_state;
    }

    /**
     * @param list_p_state the list_p_state to set
     */
    public void setList_p_state(List list_p_state) {
        this.list_p_state = list_p_state;
    }

    /**
     * @return the list_c_state
     */
    public List getList_c_state() {
        return list_c_state;
    }

    /**
     * @param list_c_state the list_c_state to set
     */
    public void setList_c_state(List list_c_state) {
        this.list_c_state = list_c_state;
    }

    /**
     * @return the regn_dt
     */
    public Date getRegn_dt() {
        return regn_dt;
    }

    /**
     * @param regn_dt the regn_dt to set
     */
    public void setRegn_dt(Date regn_dt) {
        this.regn_dt = regn_dt;
    }

    /**
     * @return the today
     */
    public Date getToday() {
        this.today = new Date();
        return today;
    }

    /**
     * @param today the today to set
     */
    public void setToday(Date today) {
        this.today = today;
    }

    /**
     * @return the list_owner_cd
     */
    public List getList_owner_cd() {
        return list_owner_cd;
    }

    /**
     * @param list_owner_cd the list_owner_cd to set
     */
    public void setList_owner_cd(List list_owner_cd) {
        this.list_owner_cd = list_owner_cd;
    }

    /**
     * @return the list_regn_type
     */
    public List getList_regn_type() {
        return list_regn_type;
    }

    /**
     * @param list_regn_type the list_regn_type to set
     */
    public void setList_regn_type(List list_regn_type) {
        this.list_regn_type = list_regn_type;
    }

    /**
     * @return the list_dealer_cd
     */
    public List getList_dealer_cd() {
        return list_dealer_cd;
    }

    /**
     * @param list_dealer_cd the list_dealer_cd to set
     */
    public void setList_dealer_cd(List list_dealer_cd) {
        this.list_dealer_cd = list_dealer_cd;
    }

    /**
     * @return the list_vh_class
     */
    public List getList_vh_class() {
        return list_vh_class;
    }

    /**
     * @param list_vh_class the list_vh_class to set
     */
    public void setList_vh_class(List list_vh_class) {
        this.list_vh_class = list_vh_class;
    }

    /**
     * @return the list_fuel
     */
    public List getList_fuel() {
        return list_fuel;
    }

    /**
     * @param list_fuel the list_fuel to set
     */
    public void setList_fuel(List list_fuel) {
        this.list_fuel = list_fuel;
    }

    /**
     * @return the list_maker_model
     */
    public List getList_maker_model() {
        return list_maker_model;
    }

    /**
     * @param list_maker_model the list_maker_model to set
     */
    public void setList_maker_model(List list_maker_model) {
        this.list_maker_model = list_maker_model;
    }

    /**
     * @return the list_maker
     */
    public List getList_maker() {
        return list_maker;
    }

    /**
     * @param list_maker the list_maker to set
     */
    public void setList_maker(List list_maker) {
        this.list_maker = list_maker;
    }

    /**
     * @return the regn_dt_cal
     */
    public Calendar getRegn_dt_cal() {
        return regn_dt_cal;
    }

    /**
     * @param regn_dt_cal the regn_dt_cal to set
     */
    public void setRegn_dt_cal(Calendar regn_dt_cal) {
        this.regn_dt_cal = regn_dt_cal;
    }

    /**
     * @return the other_criteria
     */
    public Integer getOther_criteria() {
        return other_criteria;
    }

    /**
     * @param other_criteria the other_criteria to set
     */
    public void setOther_criteria(Integer other_criteria) {
        this.other_criteria = other_criteria;
    }

    /**
     * @return the imported_veh
     */
    public String getImported_veh() {
        return imported_veh;
    }

    /**
     * @param imported_veh the imported_veh to set
     */
    public void setImported_veh(String imported_veh) {
        this.imported_veh = imported_veh;
    }

    /**
     * @return the norms
     */
    public int getNorms() {
        return norms;
    }

    /**
     * @param norms the norms to set
     */
    public void setNorms(int norms) {
        this.norms = norms;
    }

    /**
     * @return the vch_purchase_as
     */
    public String getVch_purchase_as() {
        return vch_purchase_as;
    }

    /**
     * @param vch_purchase_as the vch_purchase_as to set
     */
    public void setVch_purchase_as(String vch_purchase_as) {
        this.vch_purchase_as = vch_purchase_as;
    }

    /**
     * @return the sale_amt
     */
    public Integer getSale_amt() {
        return sale_amt;
    }

    /**
     * @param sale_amt the sale_amt to set
     */
    public void setSale_amt(Integer sale_amt) {
        this.sale_amt = sale_amt;
    }

    /**
     * @return the tax_mode
     */
    public String getTax_mode() {
        return tax_mode;
    }

    /**
     * @param tax_mode the tax_mode to set
     */
    public void setTax_mode(String tax_mode) {
        this.tax_mode = tax_mode;
    }

    /**
     * @return the annual_income
     */
    public Integer getAnnual_income() {
        return annual_income;
    }

    /**
     * @param annual_income the annual_income to set
     */
    public void setAnnual_income(Integer annual_income) {
        this.annual_income = annual_income;
    }

    /**
     * @return the list_vm_catg
     */
    public List getList_vm_catg() {
        return list_vm_catg;
    }

    /**
     * @param list_vm_catg the list_vm_catg to set
     */
    public void setList_vm_catg(List list_vm_catg) {
        this.list_vm_catg = list_vm_catg;
    }

    /**
     * @return the list_norms
     */
    public List getList_norms() {
        return list_norms;
    }

    /**
     * @param list_norms the list_norms to set
     */
    public void setList_norms(List list_norms) {
        this.list_norms = list_norms;
    }

    /**
     * @return the list_tax_mode
     */
    public List getList_tax_mode() {
        return list_tax_mode;
    }

    /**
     * @param list_tax_mode the list_tax_mode to set
     */
    public void setList_tax_mode(List list_tax_mode) {
        this.list_tax_mode = list_tax_mode;
    }

    /**
     * @return the list_other_criteria
     */
    public List getList_other_criteria() {
        return list_other_criteria;
    }

    /**
     * @param list_other_criteria the list_other_criteria to set
     */
    public void setList_other_criteria(List list_other_criteria) {
        this.list_other_criteria = list_other_criteria;
    }

    /**
     * @return the list_imported_veh
     */
    public List getList_imported_veh() {
        return list_imported_veh;
    }

    /**
     * @param list_imported_veh the list_imported_veh to set
     */
    public void setList_imported_veh(List list_imported_veh) {
        this.list_imported_veh = list_imported_veh;
    }

    /**
     * @return the list_purchase_as
     */
    public List getList_purchase_as() {
        return list_purchase_as;
    }

    /**
     * @param list_purchase_as the list_purchase_as to set
     */
    public void setList_purchase_as(List list_purchase_as) {
        this.list_purchase_as = list_purchase_as;
    }

    /**
     * @return the list_veh_type
     */
    public List getList_veh_type() {
        return list_veh_type;
    }

    /**
     * @param list_veh_type the list_veh_type to set
     */
    public void setList_veh_type(List list_veh_type) {
        this.list_veh_type = list_veh_type;
    }

    /**
     * @return the vehType
     */
    public String getVehType() {
        return vehType;
    }

    /**
     * @param vehType the vehType to set
     */
    public void setVehType(String vehType) {
        this.vehType = vehType;
    }

    /**
     * @return the compBeanList
     */
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    /**
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    /**
     * @return the owner_dobj_prv
     */
    public Owner_dobj getOwner_dobj_prv() {
        return owner_dobj_prv;
    }

    /**
     * @param owner_dobj_prv the owner_dobj_prv to set
     */
    public void setOwner_dobj_prv(Owner_dobj owner_dobj_prv) {
        this.owner_dobj_prv = owner_dobj_prv;
    }

    public void ownerReadOnly(boolean flag) {

        flag = !flag;

//        getAppl_no().setReadonly(flag);
//        getRegn_no().setReadonly(flag);
//        getRegn_dt_cal().setReadonly(flag);
//        getPurchase_dt_cal().setReadonly(flag);
//        //getOwner_sr().setReadonly(flag);
//        getOwner_name().setReadonly(flag);
//        //getPan_no().setReadonly(flag);
//        getF_name().setReadonly(flag);
//        getC_add1().setReadonly(flag);
//        getC_add2().setReadonly(flag);
//        getC_state().setReadonly(flag);
//        getC_add3().setReadonly(flag);
//        getC_district().setReadonly(flag);
//        getC_pincode().setReadonly(flag);
//        getP_add1().setReadonly(flag);
//        getP_add2().setReadonly(flag);
//        getP_state().setReadonly(flag);
//        getP_add3().setReadonly(flag);
//        getP_district().setReadonly(flag);
//        getP_pincode().setReadonly(flag);
//        getDealer_cd().setReadonly(flag);
//        getRegn_type().setReadonly(flag);
//        getOwner_cd().setReadonly(flag);
        //getEmail().setReadonly(flag);
        //getMob_no().setReadonly(flag);
    }

    /**
     * @return the currentDate
     */
    public Date getCurrentDate() {
        return currentDate;
    }

    /**
     * @param currentDate the currentDate to set
     */
    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    /**
     * @return the samePermAddress
     */
    public boolean isSamePermAddress() {
        return samePermAddress;
    }

    /**
     * @param samePermAddress the samePermAddress to set
     */
    public void setSamePermAddress(boolean samePermAddress) {
        this.samePermAddress = samePermAddress;
    }

    /**
     * @return the exArmyVehicle_Visibility_tab
     */
    public boolean isExArmyVehicle_Visibility_tab() {
        return exArmyVehicle_Visibility_tab;
    }

    /**
     * @param exArmyVehicle_Visibility_tab the exArmyVehicle_Visibility_tab to
     * set
     */
    public void setExArmyVehicle_Visibility_tab(boolean exArmyVehicle_Visibility_tab) {
        this.exArmyVehicle_Visibility_tab = exArmyVehicle_Visibility_tab;
    }

    /**
     * @return the importedVehicle_Visibility_tab
     */
    public boolean isImportedVehicle_Visibility_tab() {
        return importedVehicle_Visibility_tab;
    }

    /**
     * @param importedVehicle_Visibility_tab the importedVehicle_Visibility_tab
     * to set
     */
    public void setImportedVehicle_Visibility_tab(boolean importedVehicle_Visibility_tab) {
        this.importedVehicle_Visibility_tab = importedVehicle_Visibility_tab;
    }

    /**
     * @return the axleDetail_Visibility_tab
     */
    public boolean isAxleDetail_Visibility_tab() {
        return axleDetail_Visibility_tab;
    }

    /**
     * @param axleDetail_Visibility_tab the axleDetail_Visibility_tab to set
     */
    public void setAxleDetail_Visibility_tab(boolean axleDetail_Visibility_tab) {
        this.axleDetail_Visibility_tab = axleDetail_Visibility_tab;
    }

    public void vehMakerListener(AjaxBehaviorEvent event) {
        //String vehMaker = getMaker().getValue().toString();
        String vehMaker = String.valueOf(this.getOwnerDobj().getMaker());
        String[][] dataMap = null;
        try {
            if (event != null) {
                if (this.getOwnerDobj().getMaker() <= 999 && (TableConstants.VM_REGN_TYPE_NEW.equals(getOwnerDobj().getRegn_type()) || TableConstants.VM_REGN_TYPE_TEMPORARY.equals(getOwnerDobj().getRegn_type()))) {
                    JSFUtils.showMessagesInDialog("Warn !!!", "Only Vehicle Class 'Trailer (Agricultural)' can be selected as manufacturer [" + MasterTableFiller.masterTables.VM_MAKER.getDesc(vehMaker) + "] is registered on Homologation Portal and he has not uploaded the inventory details on Homologation Portal against entered Chassis/Engine combination, either you have wrongly entered chassis/engine no combination or this vehicle does not belong to this maker.", FacesMessage.SEVERITY_WARN);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        dataMap = MasterTableFiller.masterTables.VM_MODELS.getData();

        list_maker_model.clear();
        for (int i = 0; i < dataMap.length; i++) {
            if (dataMap[i][0].equals(vehMaker)) {
                list_maker_model.add(new SelectItem(dataMap[i][1], dataMap[i][2] + " -(" + dataMap[i][1] + ")"));

            }
        }

    }

    public void vehModelListener(AjaxBehaviorEvent event) {
        // String vehMaker = getMaker().getValue().toString();
        String vehMaker = String.valueOf(this.getOwnerDobj().getMaker());
        String vehModel = this.getOwnerDobj().getMaker_model();
        String[][] dataMap = MasterTableFiller.masterTables.VM_MODELS.getData();

        String[] modelData = null;
        for (int i = 0; i < dataMap.length; i++) {
            if (dataMap[i][1].equals(vehModel) && dataMap[i][0].equals(vehMaker)) {
                modelData = dataMap[i];
                break;
            }
        }
        this.getOwnerDobj().setModelNameOnTAC(ServerUtil.getMakerModelName(vehModel));
        fillFromSelectedModel(modelData);
    }

    public void fillFromSelectedModel(String[] model) {
        if (model == null) {
            return;
        }
        if (model[4] != null && !model[4].equals("")) {
            setVch_purchase_as(model[4]);
            this.getOwnerDobj().setVch_purchase_as(vch_purchase_as);
        }
        if (model[6] != null && !model[6].equals("")) {
            this.getOwnerDobj().setVch_catg(model[6]);
        }
        if (model[7] != null && !model[7].equals("")) {
            this.getOwnerDobj().setSeat_cap(Integer.parseInt(model[7]));
        }
        if (model[8] != null && !model[8].equals("")) {
            this.getOwnerDobj().setCubic_cap(Float.parseFloat(model[8]));
        }
        if (model[10] != null && !model[10].equals("")) {
            this.getOwnerDobj().setHp(Float.parseFloat(model[10]));
        }
        if (model[13] != null && !model[13].equals("")) {
            this.getOwnerDobj().setUnld_wt(Integer.parseInt(model[13]));
        }
        if (model[14] != null && !model[14].equals("")) {
            this.getOwnerDobj().setLd_wt(Integer.parseInt(model[14]));
        }
        if (model[16] != null && !model[16].equals("")) {
            this.getOwnerDobj().setFuel(Integer.parseInt(model[16]));
        }
        if (model[17] != null && !model[17].equals("")) {
            this.getOwnerDobj().setBody_type(model[17]);
        }
        if (model[18] != null && !model[18].equals("")) {
            this.getOwnerDobj().setNo_cyl(Integer.parseInt(model[18]));
        }
        if (model[19] != null && !model[19].equals("")) {
            this.getOwnerDobj().setWheelbase((Integer.parseInt(model[19])));
        }

        if (model[20] != null && !model[20].equals("")) {
            setNorms(Integer.parseInt(model[20]));
        } else {
            setNorms(0);
        }

        if (model[37] != null && !model[37].equals("")) {
            this.getOwnerDobj().setLength(Integer.parseInt(model[37]));
        }
        if (model[38] != null && !model[38].equals("")) {
            this.getOwnerDobj().setWidth(Integer.parseInt(model[38]));
        }
        if (model[39] != null && !model[39].equals("")) {
            this.getOwnerDobj().setHeight(Integer.parseInt(model[39]));
        }

        UIComponent component = FacesContext.getCurrentInstance().getViewRoot().findComponent("masterLayout");
        if (component != null) {
            OutputLabel lab = (OutputLabel) component.findComponent("workbench_tabview:engMoterNo");
            if (lab != null) {
                if (model[16] != null && !model[16].equals("")) {
                    if (Integer.valueOf(model[16]) == TableConstants.VM_FUEL_TYPE_ELECTRIC) {
                        lab.setValue("Motor No");
                        PrimeFaces.current().ajax().update("workbench_tabview:engMoterNo");
                    }
                    if ((Integer.valueOf(model[16]) == TableConstants.VM_FUEL_TYPE_PETROL_CNG)
                            || (Integer.valueOf(model[16]) == TableConstants.VM_FUEL_CNG_TYPE)
                            || (Integer.valueOf(model[16]) == TableConstants.VM_FUEL_TYPE_LPG)
                            || (Integer.valueOf(model[16]) == TableConstants.VM_FUEL_TYPE_PETROL_LPG)) {
                        setCngDetails_Visibility_tab(true);
                    }
                }
            }
        }
    }

    public void fillComboBoxesForApplication() {
        //Fill Vehicle Class for Vehicle Type
        String vehtype = getVehType();
        String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();

        list_vh_class.clear();

        for (int i = 0; i < data.length; i++) {
            if (data[i][2].equals(vehtype)) {
                for (Object obj : authorityDobj.getSelectedVehClass()) {
                    if (String.valueOf(obj).equalsIgnoreCase("ANY")) {
                        list_vh_class.add(new SelectItem(data[i][0], data[i][1]));
                    } else {
                        if (String.valueOf(obj).equalsIgnoreCase(data[i][0])) {
                            list_vh_class.add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                }
            }
        }

        //Fill Veh Catg for Veh Class
        String vehClass = String.valueOf(getOwnerDobj().getVh_class());
        String[][] dataMap = MasterTableFiller.masterTables.VM_VHCLASS_CATG_MAP.getData();
        String[][] dataCatg = MasterTableFiller.masterTables.VM_VCH_CATG.getData();
        list_vm_catg.clear();
        for (int i = 0; i < dataMap.length; i++) {
            if (dataMap[i][0].equals(vehClass)) {
                for (int j = 0; j < dataCatg.length; j++) {
                    if (dataCatg[j][0].equals(dataMap[i][1])) {
                        list_vm_catg.add(new SelectItem(dataCatg[j][0], dataCatg[j][1]));
                    }
                }
            }
        }

        //Fill Model For Maker
        String vehMaker = String.valueOf(this.getOwnerDobj().getMaker());
        dataMap = MasterTableFiller.masterTables.VM_MODELS.getData();
        list_maker_model.clear();
        for (int i = 0; i < dataMap.length; i++) {
            if (dataMap[i][0].equals(vehMaker)) {
                list_maker_model.add(new SelectItem(dataMap[i][1], dataMap[i][2] + "-(" + dataMap[i][1] + ")"));
            }
        }

    }

    /**
     * @return the samePermAdd_Rend
     */
    public boolean isSamePermAdd_Rend() {
        return samePermAdd_Rend;
    }

    /**
     * @param samePermAdd_Rend the samePermAdd_Rend to set
     */
    public void setSamePermAdd_Rend(boolean samePermAdd_Rend) {
        this.samePermAdd_Rend = samePermAdd_Rend;
    }

    /**
     * @return the cngDetails_Visibility_tab
     */
    public boolean isCngDetails_Visibility_tab() {
        return cngDetails_Visibility_tab;
    }

    /**
     * @param cngDetails_Visibility_tab the cngDetails_Visibility_tab to set
     */
    public void setCngDetails_Visibility_tab(boolean cngDetails_Visibility_tab) {
        this.cngDetails_Visibility_tab = cngDetails_Visibility_tab;
    }

    /**
     * @return the fit_upto
     */
    public Date getFit_upto() {
        return fit_upto;
    }

    /**
     * @param fit_upto the fit_upto to set
     */
    public void setFit_upto(Date fit_upto) {
        this.fit_upto = fit_upto;
    }

    /**
     * @return the regn_upto
     */
    public Date getRegn_upto() {
        return regn_upto;
    }

    /**
     * @param regn_upto the regn_upto to set
     */
    public void setRegn_upto(Date regn_upto) {
        this.regn_upto = regn_upto;
    }

    public void setForTempRegnType() {
        list_regn_type.clear();
        list_regn_type.add(new SelectItem(TableConstants.VM_REGN_TYPE_TEMPORARY, "Temporary Registration"));
        getOwnerDobj().setRegn_type(TableConstants.VM_REGN_TYPE_TEMPORARY);
        setDisableRegnType(true);
        //getRegn_type().setDisabled(true);
        setRenderTempOwner(true);
        if (getTemp_dobj() != null) {
            tempPurposeListner(null);
        }

    }

    public void setForNewTempRegnType() {
        list_regn_type.clear();
        list_regn_type.add(new SelectItem(TableConstants.VM_REGN_TYPE_TEMPORARY, "Temporary Registration"));
        getOwnerDobj().setRegn_type(TableConstants.VM_REGN_TYPE_TEMPORARY);
        setDisableRegnType(true);

        if (getTempReg() != null) {
            if (getTempReg().getTmp_state_cd() != null) {
                list_office_to.clear();
                String[][] data = MasterTableFiller.masterTables.TM_OFFICE.getData();
                for (int i = 0; i < data.length; i++) {
                    if (data[i][13].equals(getTempReg().getTmp_state_cd())) {
                        list_office_to.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }

                List listDealer = ServerUtil.getDealerListSelectItem(getTempReg().getDealer_cd());
                if (listDealer != null) {
                    setList_dealer_cd(listDealer);
                    this.getOwnerDobj().setDealer_cd(getTempReg().getDealer_cd());
                }
            }

        }

    }

    public void validateTempDetails(String stateFrom, int offFrom, String chassisNo) throws VahanException {
        if (!CommonUtils.isNullOrBlank(stateFrom) && offFrom != 0) {
            Date vow4StartDate = ServerUtil.getVahan4StartDate(stateFrom, offFrom);
            if (vow4StartDate != null) {
                Date before3MonOfCurrentDt = ServerUtil.dateRange(new Date(), 0, -3, 0);
                if (before3MonOfCurrentDt != null && vow4StartDate.before(before3MonOfCurrentDt)) {
                    String officeName = ServerUtil.getOfficeName(offFrom, stateFrom);
                    throw new VahanException("VAHAN4.0 has been started in " + officeName + " more then 3 months. Temporary Registration details not found for this Chassis No '" + chassisNo + "' in VAHAN4.0, Manual entry is not allowed. ");
                }
            }
        }
    }

    /**
     * @return the temp_dobj
     */
    public Owner_temp_dobj getTemp_dobj() {
        return temp_dobj;
    }

    /**
     * @param temp_dobj the temp_dobj to set
     */
    public void setTemp_dobj(Owner_temp_dobj temp_dobj) {
        this.temp_dobj = temp_dobj;
    }

    /**
     * @return the temp_dobj_prev
     */
    public Owner_temp_dobj getTemp_dobj_prev() {
        return temp_dobj_prev;
    }

    /**
     * @param temp_dobj_prev the temp_dobj_prev to set
     */
    public void setTemp_dobj_prev(Owner_temp_dobj temp_dobj_prev) {
        this.temp_dobj_prev = temp_dobj_prev;
    }

    /**
     * @return the renderTempOwner
     */
    public boolean isRenderTempOwner() {
        return renderTempOwner;
    }

    /**
     * @param renderTempOwner the renderTempOwner to set
     */
    public void setRenderTempOwner(boolean renderTempOwner) {
        this.renderTempOwner = renderTempOwner;
    }

    /**
     * @return the list_office_to
     */
    public List getList_office_to() {
        return list_office_to;
    }

    /**
     * @param list_office_to the list_office_to to set
     */
    public void setList_office_to(List list_office_to) {
        this.list_office_to = list_office_to;
    }

    /**
     * @return the owner_identification
     */
    public OwnerIdentificationDobj getOwner_identification() {
        return owner_identification;
    }

    /**
     * @param owner_identification the owner_identification to set
     */
    public void setOwner_identification(OwnerIdentificationDobj owner_identification) {
        this.owner_identification = owner_identification;
    }

    /**
     * @return the advanceRegNoDobj
     */
    public AdvanceRegnNo_dobj getAdvanceRegNoDobj() {
        return advanceRegNoDobj;
    }

    /**
     * @param advanceRegNoDobj the advanceRegNoDobj to set
     */
    public void setAdvanceRegNoDobj(AdvanceRegnNo_dobj advanceRegNoDobj) {
        this.advanceRegNoDobj = advanceRegNoDobj;
    }

    /**
     * @return the blnAdvancedRegNo
     */
    public boolean isBlnAdvancedRegNo() {
        return blnAdvancedRegNo;
    }

    /**
     * @param blnAdvancedRegNo the blnAdvancedRegNo to set
     */
    public void setBlnAdvancedRegNo(boolean blnAdvancedRegNo) {
        this.blnAdvancedRegNo = blnAdvancedRegNo;
    }

    /**
     * @return the list_adv_district
     */
    public List getList_adv_district() {
        return list_adv_district;
    }

    /**
     * @param list_adv_district the list_adv_district to set
     */
    public void setList_adv_district(List list_adv_district) {
        this.list_adv_district = list_adv_district;
    }

    /**
     * @return the list_adv_state
     */
    public List getList_adv_state() {
        return list_adv_state;
    }

    /**
     * @param list_adv_state the list_adv_state to set
     */
    public void setList_adv_state(List list_adv_state) {
        this.list_adv_state = list_adv_state;
    }

    public void advanceNoListener() {
        try {
            VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
            WorkBench workBenchBean = (WorkBench) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("workBench");
            if (workBenchBean != null && workBenchBean.getOtherStateVehDobj() != null && vehParameters != null && workBenchBean.getOtherStateVehDobj().getOldOffCD() != null) {
                vehParameters.setPREV_OFF_CD(workBenchBean.getOtherStateVehDobj().getOldOffCD());
                vehParameters.setLOGIN_OFF_CD(Util.getSelectedSeat().getOff_cd());
            }
            Exception e = null;
            if (advanceRegnAllotted.equals("Yes")) {
                if (tmConfDobj != null && tmConfDobj.isTo_retention()) {
                    if (ownerDobj.getRegn_type() != null && !ownerDobj.getRegn_type().equals("") && ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                        setPanelAdvAppdtl(false);
                        setPanelAdvRetAppdtl(true);
                        setPanelAdvRetAdddtl(true);
                        setPanelAdvAdddtl(false);
                        setAdvRegnRetenRadiobtn("retenno");
                        setBlnAdvancedRegNo(false);
                        RetenRegnNo_dobj dobj = new RetenRegnNo_dobj();
                        setRetenRegNoDobj(dobj);
                        setAdvanceRegNoDobj(null);
                        setRenderAdvanceRegnPanel(true);
                        PrimeFaces.current().executeScript("PF('wd_choiceno').show();");
                    } else {
                        setPanelAdvAppdtl(true);
                        setPanelAdvAdddtl(true);
                        setPanelRadioBtn(true);
                        setPanelAdvRetAppdtl(false);
                        setPanelAdvRetAdddtl(false);
                        setAdvRegnRetenRadiobtn("advregnno");
                        setBlnAdvancedRegNo(true);
                        AdvanceRegnNo_dobj dobj = new AdvanceRegnNo_dobj();
                        setAdvanceRegNoDobj(dobj);
                        setRenderAdvanceRegnPanel(true);
                        PrimeFaces.current().executeScript("PF('wd_choiceno').show();");
                    }
                } else {
                    if ((ownerDobj.getRegn_type() != null && !ownerDobj.getRegn_type().equals("")) && (ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE) && (!isCondition(replaceTagValues(Util.getTmConfiguration().getOther_rto_number_change(), vehParameters), "Fancy No Assignment Other/RTO")))) {
                        setBlnPgAdvancedRegNo(false);
                    } else {
                        setBlnAdvancedRegNo(true);
                        AdvanceRegnNo_dobj dobj = new AdvanceRegnNo_dobj();
                        setAdvanceRegNoDobj(dobj);
                        setAdvRegnRetenRadiobtn("advregnno");
                        setRenderAdvanceRegnPanel(true);
                        PrimeFaces.current().executeScript("PF('wd_choiceno').show();");
                    }
                }

            } else {
                setBlnAdvancedRegNo(false);
                setDisableOwnerName(false);
                //getOwnerDobj().setOwner_cd(-1);
                //vehOwner_cdListener(null);
                setRegn_no(null);
                getOwner_identification().setMobileNoEditable(false);
                /*if ((getRetenRegNoDobj() == null && getAdvanceRegNoDobj() != null) || (getAdvanceRegNoDobj() == null && getRetenRegNoDobj() != null)) {
                 //getOwnerDobj().setOwner_name(null);
                 //getOwnerDobj().setF_name(null);
                 //setDisableFName(false);
                 //getOwnerDobj().setC_add1(null);
                 //getC_add1().setDisabled(false);
                 //getOwnerDobj().setC_add2(null);
                 //getC_add2().setDisabled(false);
                 //getOwnerDobj().setC_add3(null);
                 //getC_add3().setDisabled(false);
                 //getOwnerDobj().setC_district(0);
                 //getC_district().setDisabled(false);
                 //getOwnerDobj().setC_pincode(0);
                 //getC_pincode().setDisabled(false);
                 //getOwnerDobj().setC_state(null);
                 //getC_state().setDisabled(false);
                 //getOwner_identification().setMobile_no(null);
                 //PrimeFaces.current().ajax().update("tabviewRender");
                 }*/
                getOwnerDobj().setAdvanceOrRetenNoSelected(false);
                if (getAdvanceRegNoDobj() != null) {
                    setAdvanceRegNoDobj(null);
                }
                if (getRetenRegNoDobj() != null) {
                    setRetenRegNoDobj(null);
                }
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert !!", ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void advanceRcptListener(AjaxBehaviorEvent ajax) {
        String baseUrl = VahanUtil.getBaseUrl();
        try {
            OwnerBeanModel ownerBeanModel = new OwnerBeanModel();
            ownerBeanModel.setAdvRegnRetenRadiobtn(this.getAdvRegnRetenRadiobtn());
            if (AdvRegnRetenRadiobtn.equals("retenno")) {
                ownerBeanModel.setRetenRegNoDobj(this.getRetenRegNoDobj());
            } else {
                ownerBeanModel.setAdvanceRegnNoDobj(new AdvanceRegnNoDobjModel(this.getAdvanceRegNoDobj()));
            }
            ownerBeanModel.setTempReg(this.getTempReg());

            WrapperModel wrapperModel = new WrapperModel();
            wrapperModel.setSessionVariables(new SessionVariablesModel(new SessionVariables()));
            wrapperModel.setOwnerBean(ownerBeanModel);
            wrapperModel.setOwnerDobj(ownerDobj);
            wrapperModel.setTmConfigDobj(tmConfDobj);

            String restUrl = baseUrl + "/receipt/validate";

            // 2. Invoking controller to process business logic
            wrapperModel = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build()
                    .post()
                    .uri(restUrl)
                    .body(Mono.just(wrapperModel), WrapperModel.class)
                    .retrieve()
                    .bodyToMono(WrapperModel.class)
                    .block();

            if (wrapperModel == null) {
                throw new VahanException("Something went wrong when trying to fetch data from REST API");
            } else if (wrapperModel.getContextMessageModel() != null) {
                ContextMessageModel context = wrapperModel.getContextMessageModel();
                if (context.getMessageContext().equals(MessageContext.REQUEST) && context.getMessageSeverity() == null) {
                    setRetRcptMsg(wrapperModel.getOwnerBean().getRetRcptMsg());
                    PrimeFaces.current().executeScript(context.getMessage1());
                }
            }
//            list_adv_district = wrapperModel.getOwnerBean().getList_adv_district();
            // Need to test this code thoroughly
            if (AdvRegnRetenRadiobtn.equals("retenno")) {
                String rcptno = wrapperModel.getOwnerBean().getRetenRegNoDobj().getRecp_no();
                if (rcptno == null || rcptno.isEmpty()) {
                    return;
                }
                setRetenRegNoDobj(wrapperModel.getOwnerBean().getRetenRegNoDobj());

                String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
                list_adv_district.clear();
                for (int i = 0; i < data.length; i++) {
                    if (data[i][2].trim().equals(getRetenRegNoDobj().getState_cd())) {
                        list_adv_district.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
            } else {
                String rcptno = wrapperModel.getOwnerBean().getAdvanceRegnNoDobj().getRecp_no();
                if (rcptno == null || rcptno.isEmpty()) {
                    return;
                }
                setAdvanceRegNoDobj(AdvanceRegnNoDobjModel.convertModelToDobj(wrapperModel.getOwnerBean().getAdvanceRegnNoDobj()));

                String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
                list_adv_district.clear();
                for (int i = 0; i < data.length; i++) {
                    if (data[i][2].trim().equals(getAdvanceRegNoDobj().getState_cd())) {
                        list_adv_district.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
            }
        } catch (WebClientResponseException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getResponseBodyAsString(), "Please Try Again!"));
        } catch (VahanException ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "Please Try Again!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            AdvanceRegnNo_dobj dobj = new AdvanceRegnNo_dobj();
            setAdvanceRegNoDobj(dobj);
            RetenRegnNo_dobj retdobj = new RetenRegnNo_dobj();
            setRetenRegNoDobj(retdobj);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void advanceResetListener(AjaxBehaviorEvent ajax) {

        getAdvanceRegNoDobj().setRecp_no(null);
        getAdvanceRegNoDobj().setRegn_appl_no(null);
        getAdvanceRegNoDobj().setRegn_no(null);
        getAdvanceRegNoDobj().setOwner_name(null);
        getAdvanceRegNoDobj().setF_name(null);
        getAdvanceRegNoDobj().setC_add1(null);
        getAdvanceRegNoDobj().setC_add2(null);
        getAdvanceRegNoDobj().setC_add3(null);
        getAdvanceRegNoDobj().setC_district(0);
        getAdvanceRegNoDobj().setC_pincode(0);
        getAdvanceRegNoDobj().setC_state(null);
        getAdvanceRegNoDobj().setMobile_no(0L);
        getAdvanceRegNoDobj().setTotal_amt(0);
        getOwnerDobj().setAdvanceOrRetenNoSelected(false);
    }

    public void advanceSaveListener(AjaxBehaviorEvent ajax) {
        try {
            //fill details from AdvanceRegnNo_dobj
            if (getAdvanceRegNoDobj() != null && getAdvanceRegNoDobj().getRegn_no() != null) {
                if (getOwnerDobj() != null && getAdvanceRegNoDobj() != null && getOwnerDobj().getOwner_name() != null && getAdvanceRegNoDobj().getOwner_name() != null && !getOwnerDobj().getOwner_name().trim().equalsIgnoreCase(getAdvanceRegNoDobj().getOwner_name().trim())) {
                    setAdvanceRegnAllotted("No");
                    setAdvanceRegNoDobj(null);
                    setBlnAdvancedRegNo(false);
                    getOwnerDobj().setAdvanceOrRetenNoSelected(false);
                    throw new VahanException("Error: Fancy Owner Name must be same  Owner Name");
                }

                setBlnAdvancedRegNo(true);
                setRegn_no(getAdvanceRegNoDobj().getRegn_no());
                getOwnerDobj().setOwner_name(getAdvanceRegNoDobj().getOwner_name());
                setDisableOwnerName(true);
                getOwnerDobj().setF_name(getAdvanceRegNoDobj().getF_name());
                setDisableFName(false);
                getOwnerDobj().setC_add1(getAdvanceRegNoDobj().getC_add1());
                //getC_add1().setDisabled(true);
                getOwnerDobj().setC_add2(getAdvanceRegNoDobj().getC_add2());
                //getC_add2().setDisabled(true);
                getOwnerDobj().setC_add3(getAdvanceRegNoDobj().getC_add3());
                //getC_add3().setDisabled(true);

                getOwnerDobj().setC_district(getAdvanceRegNoDobj().getC_district());
                //getC_district().setDisabled(true);

                getOwnerDobj().setC_pincode(getAdvanceRegNoDobj().getC_pincode());
                //getC_pincode().setDisabled(true);

                getOwnerDobj().setC_state(getAdvanceRegNoDobj().getC_state());
                //getC_state().setDisabled(true);

                getOwner_identification().setMobile_no(getAdvanceRegNoDobj().getMobile_no());
                getOwner_identification().setMobileNoEditable(true);
                getOwnerDobj().setAdvanceOrRetenNoSelected(true);
                //PrimeFaces.current().ajax().update("tabviewRender");

            } else if (getRetenRegNoDobj() != null && getRetenRegNoDobj().getRegn_no() != null) {
                if (getRetenRegNoDobj().getC_district() == 0 || getRetenRegNoDobj().getC_pincode() == 0) {
                    setBlnAdvancedRegNo(false);
                } else {
                    setBlnAdvancedRegNo(true);
                }
                setRegn_no(getRetenRegNoDobj().getRegn_no());
                getOwnerDobj().setOwner_name(getRetenRegNoDobj().getOwner_name());
                if ("HR".contains(stateCodeSelected)) {
                    setDisableOwnerName(false);
                } else {
                    setDisableOwnerName(true);
                }
                getOwnerDobj().setF_name(getRetenRegNoDobj().getF_name());
                setDisableFName(false);
                getOwnerDobj().setC_add1(getRetenRegNoDobj().getC_add1());
                //getC_add1().setDisabled(true);
                getOwnerDobj().setC_add2(getRetenRegNoDobj().getC_add2());
                //getC_add2().setDisabled(true);
                getOwnerDobj().setC_add3(getRetenRegNoDobj().getC_add3());
                //getC_add3().setDisabled(true);

                getOwnerDobj().setC_district(getRetenRegNoDobj().getC_district());
                //getC_district().setDisabled(true);

                getOwnerDobj().setC_pincode(getRetenRegNoDobj().getC_pincode());
                //getC_pincode().setDisabled(true);

                getOwnerDobj().setC_state(getRetenRegNoDobj().getC_state());
                //getC_state().setDisabled(true);
                getOwnerDobj().setAdvanceOrRetenNoSelected(true);

                getOwner_identification().setMobile_no(getRetenRegNoDobj().getMobile_no());
                if (getOwner_identification().getMobile_no() != 0l) {
                    if (tmConfDobj != null && tmConfDobj.getTmConfigOtpDobj() != null && tmConfDobj.getTmConfigOtpDobj().isOwner_mobile_verify_with_otp()
                            && (actionCodeSelected == TableConstants.TM_ROLE_NEW_APPL || actionCodeSelected == TableConstants.TM_ROLE_DEALER_NEW_APPL)) {
                        // do nothing
                    } else {
                        getOwner_identification().setMobileNoEditable(true);
                    }
                }
                //PrimeFaces.current().ajax().update("tabviewRender");

            } else {
                setAdvanceRegNoDobj(null);
                setRetenRegNoDobj(null);
                setBlnAdvancedRegNo(false);
                setAdvanceRegnAllotted("No");
                getOwnerDobj().setAdvanceOrRetenNoSelected(false);
            }
        } catch (VahanException ex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert !!", ex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void disableFancyNumberFields() {
        setBlnAdvancedRegNo(true);
        getOwner_identification().setMobileNoEditable(true);
        setDisableOwnerName(true);
    }

    public void advanceExitListener(AjaxBehaviorEvent ajax) {
        setAdvanceRegNoDobj(null);
        setRetenRegNoDobj(null);
        setBlnAdvancedRegNo(false);
        setAdvanceRegnAllotted("No");
        setRenderAdvanceRegnPanel(false);
        //getOwnerDobj().setOwner_cd(-1);
        //vehOwner_cdListener(null);
    }

    public void renderSelectMenuModelListener(AjaxBehaviorEvent ajax) {
        if (modelEditable) {
            renderModelSelectMenu = false;
        } else {
            renderModelSelectMenu = true;
        }
    }

    public void permitTypeChangeListener() {
        if (ownerDobj.getPmt_type() > 0) {
            ownerDobj.getPermitCategoryList().clear();
            String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
            for (int j = 0; j < data.length; j++) {
                if (data[j][0].equalsIgnoreCase(stateCodeSelected)
                        && Integer.parseInt(data[j][3]) == ownerDobj.getPmt_type()) {
                    ownerDobj.getPermitCategoryList().add(new SelectItem(data[j][1], data[j][2]));
                }
            }
        }
        taxModeLoader(vehType, ownerDobj);
    }

    public void permitCatgChangeListener() {
        taxModeLoader(vehType, ownerDobj);
    }

    public void fitnessPanelLoader() {
        try {
            WorkBench wb = (WorkBench) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("workBench");
            Owner_dobj own_dobj = this.set_Owner_appl_bean_to_dobj();
            FitnessImpl fitness_Impl = new FitnessImpl();
            tmConfigFitnessDobj = fitness_Impl.getFitnessConfiguration(stateCodeSelected);
            VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(own_dobj);

            if (wb != null && (wb.getACTION_CDOE() == TableConstants.TM_NEW_RC_FITNESS_INSPECTION || actionCodeSelected == TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL)) {

                if (FitnessImpl.getNewFitnessUpto(own_dobj) >= 15) {

                    if (wb.isMain_panal_visibililty()) {
                        //wb.getInspection_panel().setRendered(false);
                        this.setRenderInspectionPanel(false);
                    }
                } else {
                    if (wb.isMain_panal_visibililty()) {
                        if (wb.getACTION_CDOE() == TableConstants.TM_NEW_RC_FITNESS_INSPECTION || actionCodeSelected == TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL) {
                            if (tmConfigFitnessDobj != null && isCondition(replaceTagValues(tmConfigFitnessDobj.getAllowInspectionForVeh(), vehParameters), "fitnessPanelLoader")) {
                                this.setRenderInspectionPanelNT(true);
                                this.setRenderInspectionPanel(false);
                            } else {
                                this.setRenderInspectionPanelNT(false);
                                //wb.getInspection_panel().setRendered(true);
                                this.setRenderInspectionPanel(true);
                            }
                        }
                    }
                }
            } else {
                if ((actionCodeSelected == TableConstants.TM_NEW_RC_FITNESS_INSPECTION || actionCodeSelected == TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL)) {
                    if (tmConfigFitnessDobj != null && isCondition(replaceTagValues(tmConfigFitnessDobj.getAllowInspectionForVeh(), vehParameters), "fitnessPanelLoader")) {
                        this.setRenderInspectionPanelNT(true);
                        this.setRenderInspectionPanel(false);
                    } else {
                        this.setRenderInspectionPanelNT(false);
                        //wb.getInspection_panel().setRendered(true);
                        this.setRenderInspectionPanel(true);
                    }
                }
            }
        } catch (VahanException vahException) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Message", vahException.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

    }

    public void taxModeLoader(String vehType, Owner_dobj owner_dobj) {
        try {
            VehicleParameters vehicleParameters = null;
            if (actionCodeSelected != TableConstants.TM_ROLE_NEW_APPL
                    && actionCodeSelected != TableConstants.TM_ROLE_DEALER_NEW_APPL
                    && actionCodeSelected != TableConstants.TM_ROLE_DEALER_TEMP_APPL
                    && actionCodeSelected != TableConstants.TM_ROLE_NEW_APPL_TEMP
                    && (ownerDobj.getRqrd_tax_modes() == null || ownerDobj.getRqrd_tax_modes().length() <= 0)) {
                //do nothing
            } else {
                if (ownerDobj.getTaxModList() != null) {
                    ownerDobj.getTaxModList().clear();
                }
                if (vehType != null && owner_dobj != null && owner_dobj.getVch_catg() != null && owner_dobj.getVh_class() > 0) {
                    vehicleParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
                    vehicleParameters.setNEW_VCH("Y");
                    int purcd = purCd;
                    if (Util.getSelectedSeat().getPur_cd() != 0) {
                        purcd = Util.getSelectedSeat().getPur_cd();
                    }
                    if (purcd == 1 || purcd == 123) {
                        ownerDobj.setTaxModList(TaxServer_Impl.getListTaxFormForNewVehicle(owner_dobj, vehicleParameters, "NEW", purcd));
                    } else {
                        if (ServerUtil.getOfficeCdForDealerTempAppl(appl_no, state_cd, "taxMode") != 0) {
                            vehicleParameters.setTRANSACTION_PUR_CD(TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
                        }
                        ownerDobj.setTaxModList(TaxServer_Impl.getListTaxFormForNewVehicle(owner_dobj, vehicleParameters, "TMP", purcd));
                    }

                } else {
                    if (!CommonUtils.isNullOrBlank(owner_dobj.getAppl_no())) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", "Please Select Vehicle Class and Vehicle Category for Loading Tax Mode(s)"));
                    }
                }
            }

            //rendering of tax exeption check box
            if (Util.getSelectedSeat() != null && actionCodeSelected == TableConstants.TM_ROLE_NEW_APPL
                    && vehicleParameters != null && isCondition(replaceTagValues(Util.getTmConfiguration().getTax_exemption(), vehicleParameters), "taxModeLoader")) {
                renderTaxExemption = true;
            } else {
                renderTaxExemption = false;
            }

            if (renderTaxExemption && Util.getUserStateCode().equals("DL")) {
                this.taxExemption = true;
                renderTaxPanel = false;
                ownerDobj.getTaxModList().clear();
                ownerDobj.setTaxModList(null);
                ownerDobj.setRqrd_tax_modes(null);
            } else {
                this.taxExemption = false;
                renderTaxPanel = true;
            }

        } catch (VahanException vahException) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Message", vahException.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    /**
     * @return the blnPgAdvancedRegNo
     */
    public boolean isBlnPgAdvancedRegNo() {
        return blnPgAdvancedRegNo;
    }

    /**
     * @param blnPgAdvancedRegNo the blnPgAdvancedRegNo to set
     */
    public void setBlnPgAdvancedRegNo(boolean blnPgAdvancedRegNo) {
        this.blnPgAdvancedRegNo = blnPgAdvancedRegNo;
    }

    /**
     * @return the blnDisableCurrentState
     */
    public boolean isBlnDisableCurrentState() {
        return blnDisableCurrentState;
    }

    /**
     * @param blnDisableCurrentState the blnDisableCurrentState to set
     */
    public void setBlnDisableCurrentState(boolean blnDisableCurrentState) {
        this.blnDisableCurrentState = blnDisableCurrentState;
    }

    /**
     * @return the tempReg
     */
    public TempRegDobj getTempReg() {
        return tempReg;
    }

    /**
     * @param tempReg the tempReg to set
     */
    public void setTempReg(TempRegDobj tempReg) {
        this.tempReg = tempReg;
    }

    /**
     * @return the tempReg_prev
     */
    public TempRegDobj getTempReg_prev() {
        return tempReg_prev;
    }

    /**
     * @param tempReg_prev the tempReg_prev to set
     */
    public void setTempReg_prev(TempRegDobj tempReg_prev) {
        this.tempReg_prev = tempReg_prev;
    }

    /**
     * @return the regnTypeTemp
     */
    public boolean isBlnRegnTypeTemp() {
        return blnRegnTypeTemp;
    }

    /**
     * @param regnTypeTemp the regnTypeTemp to set
     */
    public void setBlnRegnTypeTemp(boolean blnRegnTypeTemp) {
        this.blnRegnTypeTemp = blnRegnTypeTemp;
    }

    /**
     * @return the blnDisableRegnTypeTemp
     */
    public boolean isBlnDisableRegnTypeTemp() {
        return blnDisableRegnTypeTemp;
    }

    /**
     * @param blnDisableRegnTypeTemp the blnDisableRegnTypeTemp to set
     */
    public void setBlnDisableRegnTypeTemp(boolean blnDisableRegnTypeTemp) {
        this.blnDisableRegnTypeTemp = blnDisableRegnTypeTemp;
    }

    /**
     * @return the dealerSaleAmt
     */
    public boolean isDealerSaleAmt() {
        return dealerSaleAmt;
    }

    /**
     * @param dealerSaleAmt the dealerSaleAmt to set
     */
    public void setDealerSaleAmt(boolean dealerSaleAmt) {
        this.dealerSaleAmt = dealerSaleAmt;
    }

    /**
     * @return the modelEditable
     */
    public boolean isModelEditable() {
        return modelEditable;
    }

    /**
     * @param modelEditable the modelEditable to set
     */
    public void setModelEditable(boolean modelEditable) {
        this.modelEditable = modelEditable;
    }

    /**
     * @return the renderModelSelectMenu
     */
    public boolean isRenderModelSelectMenu() {
        return renderModelSelectMenu;
    }

    /**
     * @param renderModelSelectMenu the renderModelSelectMenu to set
     */
    public void setRenderModelSelectMenu(boolean renderModelSelectMenu) {
        this.renderModelSelectMenu = renderModelSelectMenu;
    }

    /**
     * @return the renderModelEditable
     */
    public boolean isRenderModelEditable() {
        return renderModelEditable;
    }

    /**
     * @param renderModelEditable the renderModelEditable to set
     */
    public void setRenderModelEditable(boolean renderModelEditable) {
        this.renderModelEditable = renderModelEditable;
    }

    /**
     * @return the disableOwnerSr
     */
    public boolean isDisableOwnerSr() {
        return disableOwnerSr;
    }

    /**
     * @param disableOwnerSr the disableOwnerSr to set
     */
    public void setDisableOwnerSr(boolean disableOwnerSr) {
        this.disableOwnerSr = disableOwnerSr;
    }

    public void inspDateTempChangeListener(SelectEvent event) {
        Date inspDate = (Date) event.getObject();
        try {
            int tempUpto = 1;
            if (tempUpto > 0) {
                Date tempDtUpto = DateUtils.addToDate(inspDate, 2, tempUpto);
                tempDtUpto = DateUtils.addToDate(tempDtUpto, 1, -1);
                if (getTempReg() != null) {
                    getTempReg().setTmp_valid_upto(tempDtUpto);
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void disableAll(List<UIComponent> components, boolean disable) {

        for (UIComponent component : components) {

            if (component.getId().equals("verifyDisCheckValue")) {
                continue;
            }

            if (component instanceof HtmlInputText) {
                ((HtmlInputText) component).setDisabled(disable);
            }

            if (component instanceof HtmlSelectOneMenu) {
                ((HtmlSelectOneMenu) component).setDisabled(disable);
            }

            if (component instanceof HtmlSelectBooleanCheckbox) {
                ((HtmlSelectBooleanCheckbox) component).setDisabled(disable);
            }

            if (component instanceof HtmlSelectOneRadio) {
                ((HtmlSelectOneRadio) component).setDisabled(disable);
            }

            disableAll(component.getChildren(), disable);
        }

        setDisableMultiRegionList(disable);

    }

    public void disableScrapVehicle(List<UIComponent> components, boolean disable) {

        for (UIComponent component : components) {
            if (component.getId().equals("purchase_dt")) {
                continue;
            }

            if (component.getId().equals("tf_owner_name")) {
                continue;
            }

            if (component instanceof HtmlInputText) {
                ((HtmlInputText) component).setDisabled(disable);
            }

            if (component instanceof HtmlSelectOneMenu) {
                ((HtmlSelectOneMenu) component).setDisabled(disable);
            }

            if (component instanceof HtmlSelectBooleanCheckbox) {
                ((HtmlSelectBooleanCheckbox) component).setDisabled(disable);
            }

            if (component instanceof HtmlSelectOneRadio) {
                ((HtmlSelectOneRadio) component).setDisabled(disable);
            }

            disableScrapVehicle(component.getChildren(), disable);
        }
    }

    public void readOnlyComponent(List<UIComponent> components, boolean readOnly) {

        for (UIComponent component : components) {

//        if (component instanceof UICommandButton && component.getClientId().contains("closePanelButton") ) {
//            continue;
//        }
            if (component instanceof HtmlInputText) {
                ((HtmlInputText) component).setReadonly(readOnly);
            }

            if (component instanceof Calendar) {
                ((Calendar) component).setReadonly(readOnly);
            }
//        if (component instanceof UIInplaceInput) {
//            ((UIInplaceInput) component).setEditEvent("none");
//        }
//        if (component instanceof UICommandButton) {
//            ((UICommandButton) component).setDisabled(true);
//        }
            if (component instanceof HtmlSelectOneMenu) {
                ((HtmlSelectOneMenu) component).setReadonly(readOnly);
            }

            readOnlyComponent(component.getChildren(), readOnly);
        }
    }

    public void validateTempRegnDatesUpto(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String tmpRegnPurpose = "";
        FacesMessage msg = null;

        if (tempReg == null || tempReg.getTmp_regn_dt() == null
                || tempReg.getTmp_valid_upto() == null) {
            return;
        }

        WorkBench workBenchBean = (WorkBench) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("workBench");
        if (workBenchBean != null && workBenchBean.getTempRegnDetailsList() != null && !workBenchBean.getTempRegnDetailsList().isEmpty()) {
            int listSize = workBenchBean.getTempRegnDetailsList().size() - 1;
            if (workBenchBean.getTempRegnDetailsList().get(listSize).getOwnerTempDobj() != null) {
                tmpRegnPurpose = workBenchBean.getTempRegnDetailsList().get(listSize).getOwnerTempDobj().getPurpose();
                if (tmpRegnPurpose == null) {
                    tmpRegnPurpose = "";
                }
            }
        }

        if (DateUtils.isAfter(DateUtils.parseDate(tempReg.getTmp_regn_dt()), DateUtils.parseDate(tempReg.getTmp_valid_upto()))) {
            msg = new FacesMessage("Temp Regn Date can't be greater than Upto Date");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

        if (getOwnerDobj().getPurchase_dt() == null) {
            return;
        }

        if (DateUtils.isAfter(DateUtils.parseDate(getOwnerDobj().getPurchase_dt()), DateUtils.parseDate(tempReg.getTmp_regn_dt()))) {
            if (!tmpRegnPurpose.equalsIgnoreCase(TableConstants.STOCK_TRANSFER)) {
                msg = new FacesMessage("Purchase Date can't be greater than Temp Regn Date ");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }

//    public void vehicleComponentEditableHomologation(boolean flag) {
//        flag = !flag;
//
//        disableForHomologationData = flag;
//        disableSeatCap = flag;
//        disableStandCap = !flag;
//        disableSleeperCap = !flag;
//        disableUnLdWtCap = flag;
//        disableLdWtCap = flag;
//        disableHeight = flag;
//        disableFuelType = flag;
//    }
    public void enableForDriveAwayChassisInDealer(boolean flag) {
        disableSeatCap = flag;
        disableStandCap = flag;
        disableSleeperCap = flag;
        disableUnLdWtCap = flag;
        disableLdWtCap = flag;
        disableHeight = flag;
    }

//    public void vehicleComponentEditable(boolean flag) {
//        flag = !flag;
////        vehicleComponentEditable = flag;
//     //   disableSeatCap = flag;
////        disableStandCap = flag;`
////        disableSleeperCap = flag;
////        disableUnLdWtCap = flag;
////        disableLdWtCap = flag;
////        disableHeight = flag;
////        disableColor = flag;
////        disableManuMonth = flag;
////        disableManuYr = flag;
//        getReg_ld_wt().setDisabled(flag);
//        getSeat_type().setDisabled(flag);
//        getSpeedgov_compname().setDisabled(flag);
//        getSpeedgov_no().setDisabled(flag);
//        getSeat_pushback_cap().setDisabled(flag);
//        getSeat_ordinary_cap().setDisabled(flag);
//        //   setDisable(flag);
//        //disableFuelType = flag;
//    }
    public void disableForHomologationData(boolean flag) {
        disableMaker = flag;
        disableMakerModel = flag;
        disableChasiNo = flag;
        disableEngineNo = flag;
        disableNoOfCyl = flag;
        disableHorsePower = flag;
        disableNorms = flag;
        disableVchPurchaseAs = flag;
        disableWheelBase = flag;
        disableCubicCapacity = flag;
        disableLength = flag;
        disableWidth = flag;
        disableSeatCap = flag;
        disableUnLdWtCap = flag;
        disableLdWtCap = flag;
        disableHeight = flag;
        disableFuelType = flag;
    }

    public void disableDealerFields(Integer manuMonth, Integer manuYear) {
        this.setDisableColor(true);
        if (manuMonth != null && manuMonth == 0) {
            this.setDisableManuMonth(false);
        } else {
            this.setDisableManuMonth(true);
        }
        if (manuYear != null && manuYear == 0) {
            this.setDisableManuYr(false);
        } else {
            this.setDisableManuYr(true);
        }
        this.setDisableNorms(true);
        this.setDisableVchPurchaseAs(true);
    }

    public String allowTempRegistrationForDealer() {
        String exception = null;
        try {
            TmConfigurationDobj tmConf = Util.getTmConfiguration();
            if (Util.getUserCategory() != null && Util.getUserCategory().equals(TableConstants.USER_CATG_DEALER) && tmConf != null && tmConf.isTempFeeInNewRegis()) {
                if (!"RJ,PB".contains(Util.getUserStateCode()) && temp_dobj != null && "OR".equals(temp_dobj.getPurpose()) && Util.getUserStateCode().equals(temp_dobj.getState_cd_to()) && tmConf.getTmConfigDealerDobj() != null && tmConf.getTmConfigDealerDobj().isTempRegnAllowNonTransVeh() && tmConf.getTmConfigDealerDobj().isNewRegnNotAllowTransVeh()) {
                    if (ServerUtil.getVahan4StartDate(Util.getUserStateCode(), temp_dobj.getOff_cd_to()) != null) {
                        temp_dobj.setOff_cd_to(0);
                        throw new VahanException("VAHAN 4 Implemented in selected office and you are authorized to register the vehicle directly in this RTO. Please select the option 'Entry-New Registration' at Home Page.");
                    }
                } else if (vehType != null && !tmConf.getTmConfigDealerDobj().isTempRegnAllowNonTransVeh() && "OR".equals(temp_dobj.getPurpose()) && TableConstants.VM_VEHTYPE_NON_TRANSPORT == Integer.valueOf(vehType)) {
                    if (ServerUtil.getVahan4StartDate(Util.getUserStateCode(), temp_dobj.getOff_cd_to()) != null) {
                        temp_dobj.setOff_cd_to(0);
                        throw new VahanException("Temporary Registration not allowed for Non-Transport vehicle within State. Please select the option 'Entry-New Registration' at Home Page.");
                    }
                }
            }

        } catch (VahanException e) {
            exception = e.getMessage();
            JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.ERROR);
        }
        return exception;
    }

    public void validateHomoGCW(Owner_dobj homoDobj, Owner_dobj dobj) throws VahanException {
        if (homoDobj != null && dobj != null && TableConstants.GCW_VEH_CLASS.contains("," + dobj.getVh_class() + ",")) {
            if (homoDobj.getGcw() > dobj.getGcw()) {
                throw new VahanException("GCW should be greater than or equal to Homologation GCW.");
            }
        }
    }

    /**
     * @return the tradeCertExpireMess
     */
    public String getTradeCertExpireMess() {
        return tradeCertExpireMess;
    }

    /**
     * @param tradeCertExpireMess the tradeCertExpireMess to set
     */
    public void setTradeCertExpireMess(String tradeCertExpireMess) {
        this.tradeCertExpireMess = tradeCertExpireMess;
    }

    /**
     * @return the cdDobjClone
     */
    public CdDobj getCdDobjClone() {
        return cdDobjClone;
    }

    /**
     * @param cdDobjClone the cdDobjClone to set
     */
    public void setCdDobjClone(CdDobj cdDobjClone) {
        this.cdDobjClone = cdDobjClone;
    }

    /**
     * @return the renderTempBodyBuilding
     */
    public boolean isRenderTempBodyBuilding() {
        return renderTempBodyBuilding;
    }

    /**
     * @param renderTempBodyBuilding the renderTempBodyBuilding to set
     */
    public void setRenderTempBodyBuilding(boolean renderTempBodyBuilding) {
        this.renderTempBodyBuilding = renderTempBodyBuilding;
    }

    /**
     * @return the renderTempOther
     */
    public boolean isRenderTempOther() {
        return renderTempOther;
    }

    /**
     * @param renderTempOther the renderTempOther to set
     */
    public void setRenderTempOther(boolean renderTempOther) {
        this.renderTempOther = renderTempOther;
    }

    /**
     * @return the listTempReasons
     */
    public List getListTempReasons() {
        return listTempReasons;
    }

    /**
     * @param listTempReasons the listTempReasons to set
     */
    public void setListTempReasons(List listTempReasons) {
        this.listTempReasons = listTempReasons;
    }

    /**
     * @return the disableTempStateTo
     */
    public boolean isDisableTempStateTo() {
        return disableTempStateTo;
    }

    /**
     * @param disableTempStateTo the disableTempStateTo to set
     */
    public void setDisableTempStateTo(boolean disableTempStateTo) {
        this.disableTempStateTo = disableTempStateTo;
    }

    /**
     * @return the list_c_state_to
     */
    public List getList_c_state_to() {
        return list_c_state_to;
    }

    /**
     * @param list_c_state_to the list_c_state_to to set
     */
    public void setList_c_state_to(List list_c_state_to) {
        this.list_c_state_to = list_c_state_to;
    }

    /**
     * @return the isHomologationData
     */
    public String getIsHomologationData() {
        return isHomologationData;
    }

    /**
     * @param isHomologationData the isHomologationData to set
     */
    public void setIsHomologationData(String isHomologationData) {
        this.isHomologationData = isHomologationData;
    }

    /**
     * @return the toRetention
     */
    public boolean isToRetention() {
        return toRetention;
    }

    /**
     * @param toRetention the toRetention to set
     */
    public void setToRetention(boolean toRetention) {
        this.toRetention = toRetention;
    }

    /**
     * @return the listOwnerCatg
     */
    public List getListOwnerCatg() {
        return listOwnerCatg;
    }

    /**
     * @param listOwnerCatg the listOwnerCatg to set
     */
    public void setListOwnerCatg(List listOwnerCatg) {
        this.listOwnerCatg = listOwnerCatg;
    }

    /**
     * @return the disableOwnerCd
     */
    public boolean isDisableOwnerCd() {
        return disableOwnerCd;
    }

    /**
     * @param disableOwnerCd the disableOwnerCd to set
     */
    public void setDisableOwnerCd(boolean disableOwnerCd) {
        this.disableOwnerCd = disableOwnerCd;
    }

    /**
     * @return the disablePurchaseDt
     */
    public boolean isDisablePurchaseDt() {
        return disablePurchaseDt;
    }

    /**
     * @param disablePurchaseDt the disablePurchaseDt to set
     */
    public void setDisablePurchaseDt(boolean disablePurchaseDt) {
        this.disablePurchaseDt = disablePurchaseDt;
    }

    /**
     * @return the disableSamePerm
     */
    public boolean isDisableSamePerm() {
        return disableSamePerm;
    }

    /**
     * @param disableSamePerm the disableSamePerm to set
     */
    public void setDisableSamePerm(boolean disableSamePerm) {
        this.disableSamePerm = disableSamePerm;
    }

    /**
     * @return the disableFuelType
     */
    public boolean isDisableFuelType() {
        return disableFuelType;
    }

    /**
     * @param disableFuelType the disableFuelType to set
     */
    public void setDisableFuelType(boolean disableFuelType) {
        this.disableFuelType = disableFuelType;
    }

    /**
     * @return the permitPanel
     */
    public boolean isPermitPanel() {
        return permitPanel;
    }

    /**
     * @param permitPanel the permitPanel to set
     */
    public void setPermitPanel(boolean permitPanel) {
        this.permitPanel = permitPanel;
    }

    /**
     * @return the isPmtTypeRqrd
     */
    public boolean isIsPmtTypeRqrd() {
        return isPmtTypeRqrd;
    }

    /**
     * @param isPmtTypeRqrd the isPmtTypeRqrd to set
     */
    public void setIsPmtTypeRqrd(boolean isPmtTypeRqrd) {
        this.isPmtTypeRqrd = isPmtTypeRqrd;
    }

    /**
     * @return the isPmtCatgRqrd
     */
    public boolean isIsPmtCatgRqrd() {
        return isPmtCatgRqrd;
    }

    /**
     * @param isPmtCatgRqrd the isPmtCatgRqrd to set
     */
    public void setIsPmtCatgRqrd(boolean isPmtCatgRqrd) {
        this.isPmtCatgRqrd = isPmtCatgRqrd;
    }

    /**
     * @return the disableMaker
     */
    public boolean isDisableMaker() {
        return disableMaker;
    }

    /**
     * @param disableMaker the disableMaker to set
     */
    public void setDisableMaker(boolean disableMaker) {
        this.disableMaker = disableMaker;
    }

    /**
     * @return the disableMakerModel
     */
    public boolean isDisableMakerModel() {
        return disableMakerModel;
    }

    /**
     * @param disableMakerModel the disableMakerModel to set
     */
    public void setDisableMakerModel(boolean disableMakerModel) {
        this.disableMakerModel = disableMakerModel;
    }

    /**
     * @return the disableNoOfCyl
     */
    public boolean isDisableNoOfCyl() {
        return disableNoOfCyl;
    }

    /**
     * @param disableNoOfCyl the disableNoOfCyl to set
     */
    public void setDisableNoOfCyl(boolean disableNoOfCyl) {
        this.disableNoOfCyl = disableNoOfCyl;
    }

    /**
     * @return the disablehorsePower
     */
    public boolean isDisableHorsePower() {
        return disableHorsePower;
    }

    /**
     * @param disablehorsePower the disablehorsePower to set
     */
    public void setDisableHorsePower(boolean disableHorsePower) {
        this.disableHorsePower = disableHorsePower;
    }

    /**
     * @return the disableNorms
     */
    public boolean isDisableNorms() {
        return disableNorms;
    }

    /**
     * @param disableNorms the disableNorms to set
     */
    public void setDisableNorms(boolean disableNorms) {
        this.disableNorms = disableNorms;
    }

    /**
     * @return the disableVchPurchaseAs
     */
    public boolean isDisableVchPurchaseAs() {
        return disableVchPurchaseAs;
    }

    /**
     * @param disableVchPurchaseAs the disableVchPurchaseAs to set
     */
    public void setDisableVchPurchaseAs(boolean disableVchPurchaseAs) {
        this.disableVchPurchaseAs = disableVchPurchaseAs;
    }

    /**
     * @return the disableLength
     */
    public boolean isDisableLength() {
        return disableLength;
    }

    /**
     * @param disableLength the disableLength to set
     */
    public void setDisableLength(boolean disableLength) {
        this.disableLength = disableLength;
    }

    /**
     * @return the disableWidth
     */
    public boolean isDisableWidth() {
        return disableWidth;
    }

    /**
     * @param disableWidth the disableWidth to set
     */
    public void setDisableWidth(boolean disableWidth) {
        this.disableWidth = disableWidth;
    }

    /**
     * @return the disableEngineNo
     */
    public boolean isDisableEngineNo() {
        return disableEngineNo;
    }

    /**
     * @param disableEngineNo the disableEngineNo to set
     */
    public void setDisableEngineNo(boolean disableEngineNo) {
        this.disableEngineNo = disableEngineNo;
    }

    /**
     * @return the disableWheelBase
     */
    public boolean isDisableWheelBase() {
        return disableWheelBase;
    }

    /**
     * @param disableWheelBase the disableWheelBase to set
     */
    public void setDisableWheelBase(boolean disableWheelBase) {
        this.disableWheelBase = disableWheelBase;
    }

    /**
     * @return the disableCubicCapacity
     */
    public boolean isDisableCubicCapacity() {
        return disableCubicCapacity;
    }

    /**
     * @param disableCubicCapacity the disableCubicCapacity to set
     */
    public void setDisableCubicCapacity(boolean disableCubicCapacity) {
        this.disableCubicCapacity = disableCubicCapacity;
    }

    /**
     * @return the ownerDobj
     */
    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    /**
     * @param ownerDobj the ownerDobj to set
     */
    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    /**
     * @return the disableRegnType
     */
    public boolean isDisableRegnType() {
        return disableRegnType;
    }

    /**
     * @param disableRegnType the disableRegnType to set
     */
    public void setDisableRegnType(boolean disableRegnType) {
        this.disableRegnType = disableRegnType;
    }

    /**
     * @return the disableOwnerName
     */
    public boolean isDisableOwnerName() {
        return disableOwnerName;
    }

    /**
     * @param disableOwnerName the disableOwnerName to set
     */
    public void setDisableOwnerName(boolean disableOwnerName) {
        this.disableOwnerName = disableOwnerName;
    }

    /**
     * @return the disableFName
     */
    public boolean isDisableFName() {
        return disableFName;
    }

    /**
     * @param disableFName the disableFName to set
     */
    public void setDisableFName(boolean disableFName) {
        this.disableFName = disableFName;
    }

    /**
     * @return the renderInspectionPanel
     */
    public boolean isRenderInspectionPanel() {
        return renderInspectionPanel;
    }

    /**
     * @param renderInspectionPanel the renderInspectionPanel to set
     */
    public void setRenderInspectionPanel(boolean renderInspectionPanel) {
        this.renderInspectionPanel = renderInspectionPanel;
    }

    /**
     * @return the otherStateDobj
     */
    public OtherStateVehDobj getOtherStateDobj() {
        return otherStateDobj;
    }

    /**
     * @param otherStateDobj the otherStateDobj to set
     */
    public void setOtherStateDobj(OtherStateVehDobj otherStateDobj) {
        this.otherStateDobj = otherStateDobj;
    }

    /**
     * @return the otherStateDobjPrev
     */
    public OtherStateVehDobj getOtherStateDobjPrev() {
        return otherStateDobjPrev;
    }

    /**
     * @param otherStateDobjPrev the otherStateDobjPrev to set
     */
    public void setOtherStateDobjPrev(OtherStateVehDobj otherStateDobjPrev) {
        this.otherStateDobjPrev = otherStateDobjPrev;
    }

    /**
     * @return the isDealer
     */
    public boolean isIsDealer() {
        return isDealer;
    }

    /**
     * @param isDealer the isDealer to set
     */
    public void setIsDealer(boolean isDealer) {
        this.isDealer = isDealer;
    }

    /**
     * @return the list_ac_audio_video_fitted
     */
    public List getList_ac_audio_video_fitted() {
        return list_ac_audio_video_fitted;
    }

    /**
     * @param list_ac_audio_video_fitted the list_ac_audio_video_fitted to set
     */
    public void setList_ac_audio_video_fitted(List list_ac_audio_video_fitted) {
        this.list_ac_audio_video_fitted = list_ac_audio_video_fitted;
    }

    /**
     * @return the advanceRegnAllotted
     */
    public String getAdvanceRegnAllotted() {
        return advanceRegnAllotted;
    }

    /**
     * @param advanceRegnAllotted the advanceRegnAllotted to set
     */
    public void setAdvanceRegnAllotted(String advanceRegnAllotted) {
        this.advanceRegnAllotted = advanceRegnAllotted;
    }

    /**
     * @return the regnNoAlloted
     */
    public String getRegnNoAlloted() {
        return regnNoAlloted;
    }

    /**
     * @param regnNoAlloted the regnNoAlloted to set
     */
    public void setRegnNoAlloted(String regnNoAlloted) {
        this.regnNoAlloted = regnNoAlloted;
    }

    /**
     * @return the regnNoAllotedPanel
     */
    public boolean isRegnNoAllotedPanel() {
        return regnNoAllotedPanel;
    }

    /**
     * @param regnNoAllotedPanel the regnNoAllotedPanel to set
     */
    public void setRegnNoAllotedPanel(boolean regnNoAllotedPanel) {
        this.regnNoAllotedPanel = regnNoAllotedPanel;
    }

    /**
     * @return the seriesAvailMessage
     */
    public String getSeriesAvailMessage() {
        return seriesAvailMessage;
    }

    /**
     * @param seriesAvailMessage the seriesAvailMessage to set
     */
    public void setSeriesAvailMessage(String seriesAvailMessage) {
        this.seriesAvailMessage = seriesAvailMessage;
    }

    /**
     * @return the retenRegNoDobj
     */
    public RetenRegnNo_dobj getRetenRegNoDobj() {
        return retenRegNoDobj;
    }

    /**
     * @param retenRegNoDobj the retenRegNoDobj to set
     */
    public void setRetenRegNoDobj(RetenRegnNo_dobj retenRegNoDobj) {
        this.retenRegNoDobj = retenRegNoDobj;
    }

    /**
     * @return the panelRadioBtn
     */
    public boolean isPanelRadioBtn() {
        return panelRadioBtn;
    }

    /**
     * @param panelRadioBtn the panelRadioBtn to set
     */
    public void setPanelRadioBtn(boolean panelRadioBtn) {
        this.panelRadioBtn = panelRadioBtn;
    }

    /**
     * * @return the AdvRegnRetenRadiobtn
     */
    public String getAdvRegnRetenRadiobtn() {
        return AdvRegnRetenRadiobtn;
    }

    /**
     * @param AdvRegnRetenRadiobtn the AdvRegnRetenRadiobtn to set
     */
    public void setAdvRegnRetenRadiobtn(String AdvRegnRetenRadiobtn) {
        this.AdvRegnRetenRadiobtn = AdvRegnRetenRadiobtn;
    }

    /**
     * @return the vehicleComponentEditable
     */
    public boolean isVehicleComponentEditable() {
        return vehicleComponentEditable;
    }

    /**
     * @param vehicleComponentEditable the vehicleComponentEditable to set
     */
    public void setVehicleComponentEditable(boolean vehicleComponentEditable) {
        this.vehicleComponentEditable = vehicleComponentEditable;
    }

    /**
     * @return the disableChasiNo
     */
    public boolean isDisableChasiNo() {
        return disableChasiNo;
    }

    /**
     * @param disableChasiNo the disableChasiNo to set
     */
    public void setDisableChasiNo(boolean disableChasiNo) {
        this.disableChasiNo = disableChasiNo;
    }

    /**
     * @return the disableDealerCd
     */
    public boolean isDisableDealerCd() {
        return disableDealerCd;
    }

    /**
     * @param disableDealerCd the disableDealerCd to set
     */
    public void setDisableDealerCd(boolean disableDealerCd) {
        this.disableDealerCd = disableDealerCd;
    }

    /**
     * @return the disableManuMonth
     */
    public boolean isDisableManuMonth() {
        return disableManuMonth;
    }

    /**
     * @param disableManuMonth the disableManuMonth to set
     */
    public void setDisableManuMonth(boolean disableManuMonth) {
        this.disableManuMonth = disableManuMonth;
    }

    /**
     * @return the disableManuYr
     */
    public boolean isDisableManuYr() {
        return disableManuYr;
    }

    /**
     * @param disableManuYr the disableManuYr to set
     */
    public void setDisableManuYr(boolean disableManuYr) {
        this.disableManuYr = disableManuYr;
    }

    /**
     * @return the disableColor
     */
    public boolean isDisableColor() {
        return disableColor;
    }

    /**
     * @param disableColor the disableColor to set
     */
    public void setDisableColor(boolean disableColor) {
        this.disableColor = disableColor;
    }

    /**
     * @return the disableSeatCap
     */
    public boolean isDisableSeatCap() {
        return disableSeatCap;
    }

    /**
     * @param disableSeatCap the disableSeatCap to set
     */
    public void setDisableSeatCap(boolean disableSeatCap) {
        this.disableSeatCap = disableSeatCap;
    }

    /**
     * @return the disableStandCap
     */
    public boolean isPanelAdvAppdtl() {
        return panelAdvAppdtl;
    }

    /**
     * @param panelAdvAppdtl the panelAdvAppdtl to set
     */
    public void setPanelAdvAppdtl(boolean panelAdvAppdtl) {
        this.panelAdvAppdtl = panelAdvAppdtl;
    }

    /**
     * @return the panelAdvRetAppdtl
     */
    public boolean isPanelAdvRetAppdtl() {
        return panelAdvRetAppdtl;
    }

    /**
     * @param panelAdvRetAppdtl the panelAdvRetAppdtl to set
     */
    public void setPanelAdvRetAppdtl(boolean panelAdvRetAppdtl) {
        this.panelAdvRetAppdtl = panelAdvRetAppdtl;
    }

    /**
     * @return the panelAdvRetAdddtl
     */
    public boolean isPanelAdvRetAdddtl() {
        return panelAdvRetAdddtl;
    }

    /**
     * @param panelAdvRetAdddtl the panelAdvRetAdddtl to set
     */
    public void setPanelAdvRetAdddtl(boolean panelAdvRetAdddtl) {
        this.panelAdvRetAdddtl = panelAdvRetAdddtl;
    }

    /**
     * @return the panelAdvAdddtl
     */
    public boolean isPanelAdvAdddtl() {
        return panelAdvAdddtl;
    }

    /**
     * @param panelAdvAdddtl the panelAdvAdddtl to set
     */
    public void setPanelAdvAdddtl(boolean panelAdvAdddtl) {
        this.panelAdvAdddtl = panelAdvAdddtl;
    }

    /**
     * @return the disableStandCap
     */
    public boolean isDisableStandCap() {
        return disableStandCap;
    }

    /**
     * @param disableStandCap the disableStandCap to set
     */
    public void setDisableStandCap(boolean disableStandCap) {
        this.disableStandCap = disableStandCap;
    }

    /**
     * @return the disableSleeperCap
     */
    public boolean isDisableSleeperCap() {
        return disableSleeperCap;
    }

    /**
     * @param disableSleeperCap the disableSleeperCap to set
     */
    public void setDisableSleeperCap(boolean disableSleeperCap) {
        this.disableSleeperCap = disableSleeperCap;
    }

    /**
     * @return the disableUnLdWtCap
     */
    public boolean isDisableUnLdWtCap() {
        return disableUnLdWtCap;
    }

    /**
     * @param disableUnLdWtCap the disableUnLdWtCap to set
     */
    public void setDisableUnLdWtCap(boolean disableUnLdWtCap) {
        this.disableUnLdWtCap = disableUnLdWtCap;
    }

    /**
     * @return the disableLdWtCap
     */
    public boolean isDisableLdWtCap() {
        return disableLdWtCap;
    }

    /**
     * @param disableLdWtCap the disableLdWtCap to set
     */
    public void setDisableLdWtCap(boolean disableLdWtCap) {
        this.disableLdWtCap = disableLdWtCap;
    }

    /**
     * @return the disableHeightCap
     */
    public boolean isDisableHeight() {
        return disableHeight;
    }

    /**
     * @param disableHeightCap the disableHeightCap to set
     */
    public void setDisableHeight(boolean disableHeightCap) {
        this.disableHeight = disableHeightCap;
    }

    /**
     * @return the advanceAllotedLabel
     */
    public String getAdvanceAllotedLabel() {
        return advanceAllotedLabel;
    }

    /**
     * @param advanceAllotedLabel the advanceAllotedLabel to set
     */
    public void setAdvanceAllotedLabel(String advanceAllotedLabel) {
        this.advanceAllotedLabel = advanceAllotedLabel;
    }

    /**
     * @return the homoLdWt
     */
    public int getHomoLdWt() {
        return homoLdWt;
    }

    /**
     * @param homoLdWt the homoLdWt to set
     */
    public void setHomoLdWt(int homoLdWt) {
        this.homoLdWt = homoLdWt;
    }

    /**
     * @return the homoUnLdWt
     */
    public int getHomoUnLdWt() {
        return homoUnLdWt;
    }

    /**
     * @param homoUnLdWt the homoUnLdWt to set
     */
    public void setHomoUnLdWt(int homoUnLdWt) {
        this.homoUnLdWt = homoUnLdWt;
    }

    /**
     * @return the renderValidityPanel
     */
    public boolean isRenderValidityPanel() {
        return renderValidityPanel;
    }

    /**
     * @param renderValidityPanel the renderValidityPanel to set
     */
    public void setRenderValidityPanel(boolean renderValidityPanel) {
        this.renderValidityPanel = renderValidityPanel;
    }

    /**
     * @return the renderTaxPanel
     */
    public boolean isRenderTaxPanel() {
        return renderTaxPanel;
    }

    /**
     * @param renderTaxPanel the renderTaxPanel to set
     */
    public void setRenderTaxPanel(boolean renderTaxPanel) {
        this.renderTaxPanel = renderTaxPanel;
    }

    /**
     * @return the linked_regnNo_tab
     */
    public boolean isLinked_regnNo_tab() {
        return linked_regnNo_tab;
    }

    /**
     * @param linked_regnNo_tab the linked_regnNo_tab to set
     */
    public void setLinked_regnNo_tab(boolean linked_regnNo_tab) {
        this.linked_regnNo_tab = linked_regnNo_tab;
    }

    /**
     * @return the linkedVehDtls
     */
    public String getLinkedVehDtls() {
        return linkedVehDtls;
    }

    /**
     * @param linkedVehDtls the linkedVehDtls to set
     */
    public void setLinkedVehDtls(String linkedVehDtls) {
        this.linkedVehDtls = linkedVehDtls;
    }

    /**
     * @return the renderTaxExemption
     */
    public boolean isRenderTaxExemption() {
        return renderTaxExemption;
    }

    /**
     * @param renderTaxExemption the renderTaxExemption to set
     */
    public void setRenderTaxExemption(boolean renderTaxExemption) {
        this.renderTaxExemption = renderTaxExemption;
    }

    /**
     * @return the taxExemption
     */
    public boolean isTaxExemption() {
        return taxExemption;
    }

    /**
     * @param taxExemption the taxExemption to set
     */
    public void setTaxExemption(boolean taxExemption) {
        this.taxExemption = taxExemption;
    }

    /**
     * @return the purCd
     */
    public int getPurCd() {
        return purCd;
    }

    /**
     * @param purCd the purCd to set
     */
    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }

    /**
     * @return the tempLdWt
     */
    public int getTempLdWt() {
        return tempLdWt;
    }

    /**
     * @param tempLdWt the tempLdWt to set
     */
    public void setTempLdWt(int tempLdWt) {
        this.tempLdWt = tempLdWt;
    }

    /**
     * @return the tempUnLdWt
     */
    public int getTempUnLdWt() {
        return tempUnLdWt;
    }

    /**
     * @param tempUnLdWt the tempUnLdWt to set
     */
    public void setTempUnLdWt(int tempUnLdWt) {
        this.tempUnLdWt = tempUnLdWt;
    }

    /**
     * @return the renderSpeedGov
     */
    public boolean isRenderSpeedGov() {
        return renderSpeedGov;
    }

    /**
     * @param renderSpeedGov the renderSpeedGov to set
     */
    public void setRenderSpeedGov(boolean renderSpeedGov) {
        this.renderSpeedGov = renderSpeedGov;
    }

    /**
     * @return the speedGovPrev
     */
    public SpeedGovernorDobj getSpeedGovPrev() {
        return speedGovPrev;
    }

    /**
     * @param speedGovPrev the speedGovPrev to set
     */
    public void setSpeedGovPrev(SpeedGovernorDobj speedGovPrev) {
        this.speedGovPrev = speedGovPrev;
    }

    public void speedGovListener(AjaxBehaviorEvent event) {
        //VehicleParameters veh = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj, null);
        SpeedGovernorDobj sg = null;
        if (renderSpeedGov) {
            setRenderSpeedGov(true);
            sg = new SpeedGovernorDobj();
//            sg.setAppl_no(ownerDobj.getAppl_no());
//            sg.setRegn_no(ownerDobj.getRegn_no());
//            sg.setState_cd(ownerDobj.getState_cd());
//            sg.setOff_cd(ownerDobj.getOff_cd());

            sg.setAppl_no(Util.getSelectedSeat().getAppl_no());
            sg.setRegn_no(Util.getSelectedSeat().getRegn_no());
            sg.setState_cd(stateCodeSelected);
            if (Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN)) {//forstateadmin
                sg.setOff_cd(getOwnerDobj().getOff_cd());
            } else {
                sg.setOff_cd(offCodeSelected);
            }

            getOwnerDobj().setSpeedGovernorDobj(sg);
        } else {
            setRenderSpeedGov(false);
            getOwnerDobj().setSpeedGovernorDobj(sg);
        }

    }

    public void reflectiveTapeListener(AjaxBehaviorEvent event) {
        ReflectiveTapeDobj refTape = null;
        if (renderReflectiveTape) {
            setRenderReflectiveTape(true);
            refTape = new ReflectiveTapeDobj();
            refTape.setApplNo(Util.getSelectedSeat().getAppl_no());
            refTape.setRegn_no(Util.getSelectedSeat().getRegn_no());
            refTape.setStateCd(stateCodeSelected);
            if (Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN)) {//forstateadmin
                refTape.setOffCd(getOwnerDobj().getOff_cd());
            } else {
                refTape.setOffCd(offCodeSelected);
            }
            getOwnerDobj().setReflectiveTapeDobj(refTape);
        } else {
            setRenderReflectiveTape(false);
            getOwnerDobj().setReflectiveTapeDobj(refTape);
        }
    }

    /**
     * @return the renderIsSpeedGov
     */
    public boolean isRenderIsSpeedGov() {
        return renderIsSpeedGov;
    }

    /**
     * @param renderIsSpeedGov the renderIsSpeedGov to set
     */
    public void setRenderIsSpeedGov(boolean renderIsSpeedGov) {
        this.renderIsSpeedGov = renderIsSpeedGov;
    }

    /**
     * @return the disableOtherState
     */
    public boolean isDisableOtherState() {
        return disableOtherState;
    }

    /**
     * @param disableOtherState the disableOtherState to set
     */
    public void setDisableOtherState(boolean disableOtherState) {
        this.disableOtherState = disableOtherState;
    }

    /**
     * @return the renderPrevCurrentAdd
     */
    public boolean isRenderPrevCurrentAdd() {
        return renderPrevCurrentAdd;
    }

    /**
     * @param renderPrevCurrentAdd the renderPrevCurrentAdd to set
     */
    public void setRenderPrevCurrentAdd(boolean renderPrevCurrentAdd) {
        this.renderPrevCurrentAdd = renderPrevCurrentAdd;
    }

    /**
     * @return the prevCurrAdd
     */
    public String getPrevCurrAdd() {
        return prevCurrAdd;
    }

    /**
     * @param prevCurrAdd the prevCurrAdd to set
     */
    public void setPrevCurrAdd(String prevCurrAdd) {
        this.prevCurrAdd = prevCurrAdd;
    }

    /**
     * @return the homoDobj
     */
    public Owner_dobj getHomoDobj() {
        return homoDobj;
    }

    /**
     * @param homoDobj the homoDobj to set
     */
    public void setHomoDobj(Owner_dobj homoDobj) {
        this.homoDobj = homoDobj;
    }

    public boolean isIsScrapVehicleNORetain() {
        return isScrapVehicleNORetain;
    }

    public void setIsScrapVehicleNORetain(boolean isScrapVehicleNORetain) {
        this.isScrapVehicleNORetain = isScrapVehicleNORetain;
    }

    /**
     * @return the renderInspectionPanelNT
     */
    public boolean isRenderInspectionPanelNT() {
        return renderInspectionPanelNT;
    }

    /**
     * @param renderInspectionPanelNT the renderInspectionPanelNT to set
     */
    public void setRenderInspectionPanelNT(boolean renderInspectionPanelNT) {
        this.renderInspectionPanelNT = renderInspectionPanelNT;
    }

    /**
     * @return the inspDobj
     */
    public InspectionDobj getInspDobj() {
        return inspDobj;
    }

    /**
     * @param inspDobj the inspDobj to set
     */
    public void setInspDobj(InspectionDobj inspDobj) {
        this.inspDobj = inspDobj;
    }

    /**
     * @return the minPurchaseDate
     */
    public Date getMinPurchaseDate() {
        return minPurchaseDate;
    }

    /**
     * @param minPurchaseDate the minPurchaseDate to set
     */
    public void setMinPurchaseDate(Date minPurchaseDate) {
        this.minPurchaseDate = minPurchaseDate;
    }

    /**
     * @return the renderGCW
     */
    public boolean isRenderGCW() {
        return renderGCW;
    }

    /**
     * @param renderGCW the renderGCW to set
     */
    public void setRenderGCW(boolean renderGCW) {
        this.renderGCW = renderGCW;
    }

    public String getPreviousRegnType() {
        return previousRegnType;
    }

    public void setPreviousRegnType(String previousRegnType) {
        this.previousRegnType = previousRegnType;
    }

    /**
     * @return the renderHpDtls
     */
    public boolean isRenderHpDtls() {
        return renderHpDtls;
    }

    /**
     * @param renderHpDtls the renderHpDtls to set
     */
    public void setRenderHpDtls(boolean renderHpDtls) {
        this.renderHpDtls = renderHpDtls;
    }

    /**
     * @return the renderVehDtls
     */
    public boolean isRenderVehDtls() {
        return renderVehDtls;
    }

    /**
     * @param renderVehDtls the renderVehDtls to set
     */
    public void setRenderVehDtls(boolean renderVehDtls) {
        this.renderVehDtls = renderVehDtls;
    }

    /**
     * @return the renderOwnerDtlsPartialBtn
     */
    public boolean isRenderOwnerDtlsPartialBtn() {
        return renderOwnerDtlsPartialBtn;
    }

    /**
     * @param renderOwnerDtlsPartialBtn the renderOwnerDtlsPartialBtn to set
     */
    public void setRenderOwnerDtlsPartialBtn(boolean renderOwnerDtlsPartialBtn) {
        this.renderOwnerDtlsPartialBtn = renderOwnerDtlsPartialBtn;
    }

    /**
     * @return the renderVehicleDtlsPartialBtn
     */
    public boolean isRenderVehicleDtlsPartialBtn() {
        return renderVehicleDtlsPartialBtn;
    }

    /**
     * @param renderVehicleDtlsPartialBtn the renderVehicleDtlsPartialBtn to set
     */
    public void setRenderVehicleDtlsPartialBtn(boolean renderVehicleDtlsPartialBtn) {
        this.renderVehicleDtlsPartialBtn = renderVehicleDtlsPartialBtn;
    }

    /**
     * @return the renderChasiOs
     */
    public boolean isRenderChasiOs() {
        return renderChasiOs;
    }

    /**
     * @param renderChasiOs the renderChasiOs to set
     */
    public void setRenderChasiOs(boolean renderChasiOs) {
        this.renderChasiOs = renderChasiOs;
    }

    /**
     * @return the renderNewLoi
     */
    public boolean isRenderNewLoi() {
        return renderNewLoi;
    }

    /**
     * @param renderNewLoi the renderNewLoi to set
     */
    public void setRenderNewLoi(boolean renderNewLoi) {
        this.renderNewLoi = renderNewLoi;
    }

    /**
     * @return the listOwnerCdDept
     */
    public List getListOwnerCdDept() {
        return listOwnerCdDept;
    }

    /**
     * @param listOwnerCdDept the listOwnerCdDept to set
     */
    public void setListOwnerCdDept(List listOwnerCdDept) {
        this.listOwnerCdDept = listOwnerCdDept;
    }

    /**
     * @return the listSpeedGovTypes
     */
    public List getListSpeedGovTypes() {
        return listSpeedGovTypes;
    }

    /**
     * @param listSpeedGovTypes the listSpeedGovTypes to set
     */
    public void setListSpeedGovTypes(List listSpeedGovTypes) {
        this.listSpeedGovTypes = listSpeedGovTypes;
    }

    /**
     * @return the renderReflectiveTape
     */
    public boolean isRenderReflectiveTape() {
        return renderReflectiveTape;
    }

    /**
     * @param renderReflectiveTape the renderReflectiveTape to set
     */
    public void setRenderReflectiveTape(boolean renderReflectiveTape) {
        this.renderReflectiveTape = renderReflectiveTape;
    }

    /**
     * @return the renderIsReflectiveTape
     */
    public boolean isRenderIsReflectiveTape() {
        return renderIsReflectiveTape;
    }

    /**
     * @param renderIsReflectiveTape the renderIsReflectiveTape to set
     */
    public void setRenderIsReflectiveTape(boolean renderIsReflectiveTape) {
        this.renderIsReflectiveTape = renderIsReflectiveTape;
    }

    /**
     * @return the reflectiveTapeDobjPrev
     */
    public ReflectiveTapeDobj getReflectiveTapeDobjPrev() {
        return reflectiveTapeDobjPrev;
    }

    /**
     * @param reflectiveTapeDobjPrev the reflectiveTapeDobjPrev to set
     */
    public void setReflectiveTapeDobjPrev(ReflectiveTapeDobj reflectiveTapeDobjPrev) {
        this.reflectiveTapeDobjPrev = reflectiveTapeDobjPrev;
    }

    public boolean isDisablePermanentAddress() {
        return disablePermanentAddress;
    }

    public void setDisablePermanentAddress(boolean disablePermanentAddress) {
        this.disablePermanentAddress = disablePermanentAddress;
    }

    /**
     * @return the tmConfigFitnessDobj
     */
    public TmConfigurationFitnessDobj getTmConfigFitnessDobj() {
        return tmConfigFitnessDobj;
    }

    /**
     * @param tmConfigFitnessDobj the tmConfigFitnessDobj to set
     */
    public void setTmConfigFitnessDobj(TmConfigurationFitnessDobj tmConfigFitnessDobj) {
        this.tmConfigFitnessDobj = tmConfigFitnessDobj;
    }

    /**
     * @return the renderAuthenticateBtn
     */
    public boolean isRenderAuthenticateBtn() {
        return renderAuthenticateBtn;
    }

    /**
     * @param renderAuthenticateBtn the renderAuthenticateBtn to set
     */
    public void setRenderAuthenticateBtn(boolean renderAuthenticateBtn) {
        this.renderAuthenticateBtn = renderAuthenticateBtn;
    }

    /**
     * @return the renderGenerateOTPBtn
     */
    public boolean isRenderGenerateOTPBtn() {
        return renderGenerateOTPBtn;
    }

    /**
     * @param renderGenerateOTPBtn the renderGenerateOTPBtn to set
     */
    public void setRenderGenerateOTPBtn(boolean renderGenerateOTPBtn) {
        this.renderGenerateOTPBtn = renderGenerateOTPBtn;
    }

    /**
     * @return the renderedOTPFields
     */
    public boolean isRenderedOTPFields() {
        return renderedOTPFields;
    }

    /**
     * @param renderedOTPFields the renderedOTPFields to set
     */
    public void setRenderedOTPFields(boolean renderedOTPFields) {
        this.renderedOTPFields = renderedOTPFields;
    }

    /**
     * @return the sendOTP
     */
    public String getSendOTP() {
        return sendOTP;
    }

    /**
     * @param sendOTP the sendOTP to set
     */
    public void setSendOTP(String sendOTP) {
        this.sendOTP = sendOTP;
    }

    /**
     * @return the enteredOTP
     */
    public String getEnteredOTP() {
        return enteredOTP;
    }

    /**
     * @param enteredOTP the enteredOTP to set
     */
    public void setEnteredOTP(String enteredOTP) {
        this.enteredOTP = enteredOTP;
    }

    /**
     * @return the renderedConfirmBtn
     */
    public boolean isRenderedConfirmBtn() {
        return renderedConfirmBtn;
    }

    /**
     * @param renderedConfirmBtn the renderedConfirmBtn to set
     */
    public void setRenderedConfirmBtn(boolean renderedConfirmBtn) {
        this.renderedConfirmBtn = renderedConfirmBtn;
    }

    /**
     * @return the llServiceResponse
     */
    public BiolicObj getLlServiceResponse() {
        return llServiceResponse;
    }

    /**
     * @param llServiceResponse the llServiceResponse to set
     */
    public void setLlServiceResponse(BiolicObj llServiceResponse) {
        this.llServiceResponse = llServiceResponse;
    }

    /**
     * @return the errorMsg
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * @param errorMsg the errorMsg to set
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * @return the licenseType
     */
    public String getLicenseType() {
        return licenseType;
    }

    /**
     * @param licenseType the licenseType to set
     */
    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    /**
     * @return the DOBDate
     */
    public String getDOBDate() {
        return DOBDate;
    }

    /**
     * @param DOBDate the DOBDate to set
     */
    public void setDOBDate(String DOBDate) {
        this.DOBDate = DOBDate;
    }

    public boolean isPushBkSeatRender() {
        return pushBkSeatRender;
    }

    public void setPushBkSeatRender(boolean pushBkSeatRender) {
        this.pushBkSeatRender = pushBkSeatRender;
    }

    public boolean isDisableAdvanceRegnAllotted() {
        return disableAdvanceRegnAllotted;
    }

    public void setDisableAdvanceRegnAllotted(boolean disableAdvanceRegnAllotted) {
        this.disableAdvanceRegnAllotted = disableAdvanceRegnAllotted;
    }

    public String getYearRange() {
        return yearRange;
    }

    public void setYearRange(String yearRange) {
        this.yearRange = yearRange;
    }

    /**
     * @return the saleAmtInWords
     */
    public String getSaleAmtInWords() {
        return saleAmtInWords;
    }

    /**
     * @param saleAmtInWords the saleAmtInWords to set
     */
    public void setSaleAmtInWords(String saleAmtInWords) {
        this.saleAmtInWords = saleAmtInWords;
    }

    public boolean isRender_vltd_details() {
        return render_vltd_details;
    }

    public void setRender_vltd_details(boolean render_vltd_details) {
        this.render_vltd_details = render_vltd_details;
    }

    public void getDealerofOwnerTemp(String dealerCd, String stateCd, int tmpOffCode) {
        if (!CommonUtils.isNullOrBlank(dealerCd)) {
            DealerMasterDobj dealrDobj = ServerUtil.getDealerDetailsByDealerCode(dealerCd, stateCd);
            if (dealrDobj != null && dealrDobj.getOffCode() == offCodeSelected) {
                return;
            }
            List listDealer = ServerUtil.getDealerListSelectItem(dealerCd);
            if (listDealer != null && !listDealer.isEmpty()) {
                if (!Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN)) {//forstateadmin
                    list_dealer_cd.clear();
                }
                setList_dealer_cd(listDealer);
            } else {
                List<Dealer> listDr = ServerUtil.getDealerList(stateCodeSelected, offCodeSelected);
                list_dealer_cd.clear();
                for (Dealer dealer : listDr) {
                    list_dealer_cd.add(new SelectItem(dealer.getDealer_cd(), dealer.getDealer_name()));
                }

            }

        }
    }

    public void validateTempRegistrationData(Owner_dobj dobj, int purCd, String regnType) throws VahanException {
        if (TableConstants.VM_REGN_TYPE_TEMPORARY.equals(regnType) && (purCd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || purCd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) && getTempReg() != null) {
            Owner_dobj owneTmpDobj = new OwnerImpl().getValidateVtOwnerTempDobj(dobj.getChasi_no());
            if (owneTmpDobj == null) {
                Date v4StartDate = ServerUtil.getVahan4StartDate(getTempReg().getTmp_state_cd(), getTempReg().getTmp_off_cd());
                if (v4StartDate != null) {
                    String officeName = ServerUtil.getOfficeName(getTempReg().getTmp_off_cd(), getTempReg().getTmp_state_cd());
                    throw new VahanException("VAHAN4.0 has been started in " + officeName + " on dated " + JSFUtils.convertToStandardDateFormat(v4StartDate) + ". Temporary Registration details not found for this Chassis No '" + dobj.getChasi_no() + "' in VAHAN4.0, Manual entry is not allowed.");
                }
            }
        }
    }

    public void validateHomoMaker(Owner_dobj dobj) throws VahanException, Exception {
        if (dobj != null && !TableConstants.ALLOW_VH_CHASS_WITH_HOMO_MAKER.contains(dobj.getVh_class() + "") && dobj.getMaker() <= 999
                && (TableConstants.VM_REGN_TYPE_NEW.equals(dobj.getRegn_type()) || TableConstants.VM_REGN_TYPE_TEMPORARY.equals(dobj.getRegn_type()))) {
            throw new VahanException("Only Vehicle Class 'Trailer (Agricultural)' can be selected as manufacturer [" + MasterTableFiller.masterTables.VM_MAKER.getDesc(dobj.getMaker() + "") + "] is registered on Homologation Portal and he has not uploaded the inventory details on Homologation Portal against entered Chassis/Engine combination, either you have wrongly entered chassis/engine no combination or this vehicle does not belong to this maker.");
        }
    }

    public void ownerDisclaimerListener() {
        if (ownerDobj != null && !CommonUtils.isNullOrBlank(ownerDobj.getRegn_type()) && (TableConstants.VM_REGN_TYPE_NEW.equals(ownerDobj.getRegn_type()) || TableConstants.VM_REGN_TYPE_EXARMY.equals(ownerDobj.getRegn_type())
                || TableConstants.VM_REGN_TYPE_TEMPORARY.equals(ownerDobj.getRegn_type()) || TableConstants.VM_REGN_TYPE_CD.equals(ownerDobj.getRegn_type()))) {
            if (",4,10,16,".contains("," + norms + ",")) {
                renderOwnerDisclaimer = true;
            } else {
                renderOwnerDisclaimer = false;
            }
            PrimeFaces.current().ajax().update("workbench_tabview:owner_dis_panel_id");
        }
    }

    public void validateInsuranceValidity(InsDobj insDobj) throws VahanException {
        if (insDobj != null && insDobj.isIibData() && insDobj.getIns_upto() != null && insDobj.getIns_upto().compareTo(currentDate) < 0) {
            throw new VahanException("Can't Inward/Proceed Application Because Insurance Validity Expired . The shown insurance details have been uploaded by Insurance Company to Vahan, if any discrepencies found, the Vehicle Owner have to contact respective Insurance Compnay for uploading the Latest Correct Insurance Details on Vahan.");
        }
    }

    public boolean isRenderOwnerDisclaimer() {
        return renderOwnerDisclaimer;
    }

    public void setRenderOwnerDisclaimer(boolean renderOwnerDisclaimer) {
        this.renderOwnerDisclaimer = renderOwnerDisclaimer;
    }

    public boolean isVerifyOwnerDisclaimer() {
        return verifyOwnerDisclaimer;
    }

    public void setVerifyOwnerDisclaimer(boolean verifyOwnerDisclaimer) {
        this.verifyOwnerDisclaimer = verifyOwnerDisclaimer;
    }

    public boolean isRegnUptoDateDisable() {
        return regnUptoDateDisable;
    }

    public void setRegnUptoDateDisable(boolean regnUptoDateDisable) {
        this.regnUptoDateDisable = regnUptoDateDisable;
    }

    public boolean isFitUptoDateDisable() {
        return fitUptoDateDisable;
    }

    public void setFitUptoDateDisable(boolean fitUptoDateDisable) {
        this.fitUptoDateDisable = fitUptoDateDisable;
    }

    public boolean isRegistrationDateDisable() {
        return registrationDateDisable;
    }

    public void setRegistrationDateDisable(boolean registrationDateDisable) {
        this.registrationDateDisable = registrationDateDisable;
    }

    public boolean isRenderMultiRegionList() {
        return renderMultiRegionList;
    }

    public void setRenderMultiRegionList(boolean renderMultiRegionList) {
        this.renderMultiRegionList = renderMultiRegionList;
    }

    public boolean isDisableMultiRegionList() {
        return disableMultiRegionList;
    }

    public void setDisableMultiRegionList(boolean disableMultiRegionList) {
        this.disableMultiRegionList = disableMultiRegionList;
    }

    public TmConfigurationDobj getTmConfigDobj() {
        return tmConfDobj;
    }

    public int getOffCdToOfOwnerDobj() {
        return offCdToOfOwnerDobj;
    }

    public void setOffCdToOfOwnerDobj(int offCdToOfOwnerDobj) {
        this.offCdToOfOwnerDobj = offCdToOfOwnerDobj;
    }

    public String getRetRcptMsg() {
        return retRcptMsg;
    }

    public void setRetRcptMsg(String retRcptMsg) {
        this.retRcptMsg = retRcptMsg;
    }

    public boolean isRenderAdvanceRegnPanel() {
        return renderAdvanceRegnPanel;
    }

    public void setRenderAdvanceRegnPanel(boolean renderAdvanceRegnPanel) {
        this.renderAdvanceRegnPanel = renderAdvanceRegnPanel;
    }
}
