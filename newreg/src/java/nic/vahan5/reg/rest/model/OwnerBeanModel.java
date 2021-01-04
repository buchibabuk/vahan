/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model;

import nic.vahan5.reg.rest.model.dobj.fancy.AdvanceRegnNoDobjModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.user_mgmt.dobj.UserAuthorityDobj;
import nic.vahan.form.bean.OwnerBean;
import nic.vahan.form.bean.fancy.RetenRegnNo_dobj;
import nic.vahan.form.dobj.CdDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.InspectionDobj;
import nic.vahan.form.dobj.OtherStateVehDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Owner_temp_dobj;
import nic.vahan.form.dobj.ReflectiveTapeDobj;
import nic.vahan.form.dobj.SpeedGovernorDobj;
import nic.vahan.form.dobj.TempRegDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;
import nic.vahan.form.dobj.fitness.TmConfigurationFitnessDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import nic.vahan.services.BiolicObj;
import nic.vahan5.reg.form.impl.NewImplementation;
import nic.vahan5.reg.form.impl.OwnerImplementation;
import nic.vahan5.reg.server.ServerUtility;
import org.primefaces.component.calendar.Calendar;

/**
 *
 * @author Kartikey Singh Changed reference of AdvanceRegnNo_dobj to
 * AdvanceRegnNoDobjModel as one of its methods referenced the session object
 */
public class OwnerBeanModel {

    private List list_maker_model;
    private String appl_no;
    private String regn_no;
    private Date regn_dt;
    private Calendar regn_dt_cal;
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
    private boolean disableRegnType;
    private List list_regn_type;
    private List list_owner_cd;
    private List listOwnerCatg;
    private String state_cd;
    private List list_dealer_cd;
    private Date today;
    private Integer other_criteria;
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
    private List<ComparisonBeanModel> compBeanList = new ArrayList<>();
    private Owner_dobj owner_dobj_prv;
    private Owner_dobj ownerDobj;
    private SpeedGovernorDobj speedGovPrev;
    private String vehType;
    private List list_veh_type;
    private Date currentDate;
    private boolean samePermAddress;
    private boolean exArmyVehicle_Visibility_tab;
    private boolean importedVehicle_Visibility_tab;
    private boolean axleDetail_Visibility_tab;
    private boolean cngDetails_Visibility_tab;
    private boolean linked_regnNo_tab;
    private boolean samePermAdd_Rend;
    private Date fit_upto;
    private Date regn_upto;
    private Owner_temp_dobj temp_dobj;
    private Owner_temp_dobj temp_dobj_prev;
    private boolean renderTempOwner;
    private List list_office_to;
    private OwnerIdentificationDobj owner_identification;
    private AdvanceRegnNoDobjModel advanceRegnNoDobj;
    private boolean blnAdvancedRegNo;
    private boolean blnPgAdvancedRegNo;
    private boolean blnDisableCurrentState;
    private TempRegDobj tempReg;
    private TempRegDobj tempReg_prev;
    private boolean blnRegnTypeTemp;
    private boolean blnDisableRegnTypeTemp;
    private UserAuthorityDobj authorityDobj;
    private boolean modelEditable;
    private boolean renderModelEditable;
    private boolean renderModelSelectMenu;
    private boolean disableOwnerSr;
    private boolean disableFName;
    private boolean renderInspectionPanel;
    private boolean renderInspectionPanelNT;
    private boolean isDealer;
    private OtherStateVehDobj otherStateDobj;
    private OtherStateVehDobj otherStateDobjPrev;
    private List list_ac_audio_video_fitted;
    private String advanceRegnAllotted;
    private String regnNoAlloted;
    private boolean regnNoAllotedPanel;
    private String seriesAvailMessage;
    private String AdvRegnRetenRadiobtn;
    private RetenRegnNo_dobj retenRegNoDobj;
    private boolean panelRadioBtn;
    private boolean panelAdvAppdtl;
    private boolean panelAdvRetAppdtl;
    private boolean panelAdvAdddtl;
    private boolean panelAdvRetAdddtl;
    private String advanceAllotedLabel;
    TmConfigurationDobj tmConfDobj;
    private String tradeCertExpireMess;
    private CdDobj cdDobjClone;
    private final String TEMP_OTHER_STATE = "OS";
    private final String TEMP_OTHER_RTO = "OR";
    private final String TEMP_BODY_BUILDING = "BB";
    private final String TEMP_SAME_RTO = "SR";
    private final String OEM_TO_DEALER_LOCATION = "OM";
    private final String DEMONSTRATION_WITHIN_SAME_RTO = "DS";
    private final String DEMONSTRATION_WITHIN_DIFF_RTO = "DO";
    private final String STOCK_TRANSFER = "ST";
    private boolean renderTempBodyBuilding;
    private boolean renderTempOther;
    private List listTempReasons;
    private List list_c_state_to;
    private String isHomologationData;
    private boolean toRetention;
    private boolean permitPanel;
    private boolean isPmtTypeRqrd;
    private boolean isPmtCatgRqrd;
    private boolean disableTempStateTo;
    private boolean disableOwnerCd;
    private boolean disablePurchaseDt;
    private boolean disableOwnerName;
    ///// Vehicle Tab Disable Variables
    private boolean vehicleComponentEditable;
    private boolean dealerSaleAmt;
    private boolean disableMaker;
    private boolean disableMakerModel;
    private boolean disableDealerCd;
    private boolean disableChasiNo;
    private boolean disableFuelType;
    private boolean disableEngineNo;
    private boolean disableSeatCap;
    private boolean disableStandCap;
    private boolean disableSleeperCap;
    private boolean disableNoOfCyl;
    private boolean disableUnLdWtCap;
    private boolean disableLdWtCap;
    private boolean disableHorsePower;
    private boolean disableNorms;
    private boolean disableVchPurchaseAs;
    private boolean disableColor;
    private boolean disableWheelBase;
    private boolean disableCubicCapacity;
    private boolean disableManuMonth;
    private boolean disableManuYr;
    private boolean disableLength;
    private boolean disableWidth;
    private boolean disableHeight;
    private int homoLdWt;
    private int homoUnLdWt;
    private boolean renderValidityPanel;
    private boolean renderTaxPanel;
    private String linkedVehDtls;
    private boolean renderTaxExemption;
    private boolean taxExemption;
    private int purCd;
    private int tempLdWt;
    private int tempUnLdWt;
    private boolean renderSpeedGov;
    private boolean renderIsSpeedGov;
    private boolean disableOtherState;
    private boolean renderPrevCurrentAdd;
    private String prevCurrAdd;
    private Owner_dobj homoDobj;
    private boolean isScrapVehicleNORetain;
    private InspectionDobj inspDobj;
    // for orissa purchase date is current date or one day before.
    private Date minPurchaseDate;
    private boolean renderGCW;
    private String previousRegnType;
    private boolean renderHpDtls;
    private boolean renderVehDtls;
    private boolean renderOwnerDtlsPartialBtn;
    private boolean renderVehicleDtlsPartialBtn;
    private boolean renderChasiOs;
    private String empCodeLoggedIn;
    private String stateCodeSelected;
    private int offCodeSelected;
    private String userCatgForLoggedInUser;
    private int actionCodeSelected;
    private boolean renderNewLoi;
    private List listOwnerCdDept;
    private List listSpeedGovTypes;
    private boolean renderReflectiveTape;
    private boolean renderIsReflectiveTape;
    private ReflectiveTapeDobj reflectiveTapeDobjPrev;
    private boolean disablePermanentAddress;
    private TmConfigurationFitnessDobj tmConfigFitnessDobj;
    private boolean renderGenerateOTPBtn;
    private boolean renderAuthenticateBtn;
    private String sendOTP;
    private String enteredOTP;
    private boolean renderedOTPFields;
    private boolean renderedConfirmBtn;
    private BiolicObj llServiceResponse;
    private String errorMsg;
    private String licenseType;
    private String DOBDate;
    private boolean pushBkSeatRender;
    private boolean disableAdvanceRegnAllotted;
    //For Owner Admin
    private String yearRange = "c-10:c+10";
    private String saleAmtInWords;
    private boolean render_vltd_details;
    private boolean auctionVisibilityTab;
    private boolean renderOwnerDisclaimer;
    private boolean verifyOwnerDisclaimer;
    //addforBS4
    private boolean regnUptoDateDisable;
    private boolean fitUptoDateDisable;
    private boolean registrationDateDisable;
    private boolean renderMultiRegionList;
    private boolean disableMultiRegionList;
    private int offCdToOfOwnerDobj;
    private String retRcptMsg;
    private boolean renderAdvanceRegnPanel = false;

    public OwnerBeanModel() {
    }

    public OwnerBeanModel(OwnerBean ownerBean) {
        this.list_maker_model = ownerBean.getList_maker_model();
        this.appl_no = ownerBean.getAppl_no();
        this.regn_no = ownerBean.getRegn_no();
        this.regn_dt = ownerBean.getRegn_dt();
//        this.regn_dt_cal = ownerBean.getRegn_dt_cal();
        this.list_c_state = ownerBean.getList_c_state();
        this.list_c_district = ownerBean.getList_c_district();
        this.list_adv_state = ownerBean.getList_adv_state();
        this.list_adv_district = ownerBean.getList_adv_district();
        this.list_p_state = ownerBean.getList_p_state();
        this.list_p_district = ownerBean.getList_p_district();
        this.list_vh_class = ownerBean.getList_vh_class();
        this.list_vm_catg = ownerBean.getList_vm_catg();
        this.list_maker = ownerBean.getList_maker();
        this.list_fuel = ownerBean.getList_fuel();
        this.disableRegnType = ownerBean.isDisableRegnType();
        this.list_regn_type = ownerBean.getList_regn_type();
        this.list_owner_cd = ownerBean.getList_owner_cd();
        this.listOwnerCatg = ownerBean.getListOwnerCatg();
        this.state_cd = ownerBean.getState_cd();
        this.list_dealer_cd = ownerBean.getList_dealer_cd();
        this.today = ownerBean.getToday();
        this.other_criteria = ownerBean.getOther_criteria();
        this.list_other_criteria = ownerBean.getList_other_criteria();
        this.imported_veh = ownerBean.getImported_veh();
        this.list_imported_veh = ownerBean.getList_imported_veh();
        this.norms = ownerBean.getNorms();
        this.list_norms = ownerBean.getList_norms();
        this.disableSamePerm = ownerBean.isDisableSamePerm();
        this.vch_purchase_as = ownerBean.getVch_purchase_as();
        this.list_purchase_as = ownerBean.getList_purchase_as();
        this.sale_amt = ownerBean.getSale_amt();
        this.tax_mode = ownerBean.getTax_mode();
        this.list_tax_mode = ownerBean.getList_tax_mode();
        this.annual_income = ownerBean.getAnnual_income();
        this.owner_dobj_prv = ownerBean.getOwner_dobj_prv();
        this.ownerDobj = ownerBean.getOwnerDobj();
        this.speedGovPrev = ownerBean.getSpeedGovPrev();
        this.vehType = ownerBean.getVehType();
        this.list_veh_type = ownerBean.getList_veh_type();
        this.currentDate = ownerBean.getCurrentDate();
        this.samePermAddress = ownerBean.isSamePermAddress();
        this.exArmyVehicle_Visibility_tab = ownerBean.isExArmyVehicle_Visibility_tab();
        this.importedVehicle_Visibility_tab = ownerBean.isImportedVehicle_Visibility_tab();
        this.axleDetail_Visibility_tab = ownerBean.isAxleDetail_Visibility_tab();
        this.cngDetails_Visibility_tab = ownerBean.isCngDetails_Visibility_tab();
        this.linked_regnNo_tab = ownerBean.isLinked_regnNo_tab();
        this.samePermAdd_Rend = ownerBean.isSamePermAdd_Rend();
        this.fit_upto = ownerBean.getFit_upto();
        this.regn_upto = ownerBean.getRegn_upto();
        this.temp_dobj = ownerBean.getTemp_dobj();
        this.temp_dobj_prev = ownerBean.getTemp_dobj_prev();
        this.renderTempOwner = ownerBean.isRenderTempOwner();
        this.list_office_to = ownerBean.getList_office_to();
        this.owner_identification = ownerBean.getOwner_identification();
        this.advanceRegnNoDobj = new AdvanceRegnNoDobjModel(ownerBean.getAdvanceRegNoDobj());
        this.blnAdvancedRegNo = ownerBean.isBlnAdvancedRegNo();
        this.blnPgAdvancedRegNo = ownerBean.isBlnPgAdvancedRegNo();
        this.blnDisableCurrentState = ownerBean.isBlnDisableCurrentState();
        this.tempReg = ownerBean.getTempReg();
        this.tempReg_prev = ownerBean.getTempReg_prev();
        this.blnRegnTypeTemp = ownerBean.isBlnRegnTypeTemp();
        this.blnDisableRegnTypeTemp = ownerBean.isBlnDisableRegnTypeTemp();
        this.authorityDobj = Util.getUserAuthority();
        this.modelEditable = ownerBean.isModelEditable();
        this.renderModelEditable = ownerBean.isRenderModelEditable();
        this.renderModelSelectMenu = ownerBean.isRenderModelSelectMenu();
        this.disableOwnerSr = ownerBean.isDisableOwnerSr();
        this.disableFName = ownerBean.isDisableFName();
        this.renderInspectionPanel = ownerBean.isRenderInspectionPanel();
        this.renderInspectionPanelNT = ownerBean.isRenderInspectionPanelNT();
        this.isDealer = ownerBean.isIsDealer();
        this.otherStateDobj = ownerBean.getOtherStateDobj();
        this.otherStateDobjPrev = ownerBean.getOtherStateDobjPrev();
        this.list_ac_audio_video_fitted = ownerBean.getList_ac_audio_video_fitted();
        this.advanceRegnAllotted = ownerBean.getAdvanceRegnAllotted();
        this.regnNoAlloted = ownerBean.getRegnNoAlloted();
        this.regnNoAllotedPanel = ownerBean.isRegnNoAllotedPanel();
        this.seriesAvailMessage = ownerBean.getSeriesAvailMessage();
        this.AdvRegnRetenRadiobtn = ownerBean.getAdvRegnRetenRadiobtn();
        this.retenRegNoDobj = ownerBean.getRetenRegNoDobj();
        this.panelRadioBtn = ownerBean.isPanelRadioBtn();
        this.panelAdvAppdtl = ownerBean.isPanelAdvAppdtl();
        this.panelAdvRetAppdtl = ownerBean.isPanelAdvRetAppdtl();
        this.panelAdvAdddtl = ownerBean.isPanelAdvAdddtl();
        this.panelAdvRetAdddtl = ownerBean.isPanelAdvRetAdddtl();
        this.advanceAllotedLabel = ownerBean.getAdvanceAllotedLabel();
        this.tradeCertExpireMess = ownerBean.getTradeCertExpireMess();
        this.cdDobjClone = ownerBean.getCdDobjClone();
        this.renderTempBodyBuilding = ownerBean.isRenderTempBodyBuilding();
        this.renderTempOther = ownerBean.isRenderTempOther();
        this.listTempReasons = ownerBean.getListTempReasons();
        this.list_c_state_to = ownerBean.getList_c_state_to();
        this.isHomologationData = ownerBean.getIsHomologationData();
        this.toRetention = ownerBean.isToRetention();
        this.permitPanel = ownerBean.isPermitPanel();
        this.isPmtTypeRqrd = ownerBean.isIsPmtTypeRqrd();
        this.isPmtCatgRqrd = ownerBean.isIsPmtCatgRqrd();
        this.disableTempStateTo = ownerBean.isDisableTempStateTo();
        this.disableOwnerCd = ownerBean.isDisableOwnerCd();
        this.disablePurchaseDt = ownerBean.isDisablePurchaseDt();
        this.disableOwnerName = ownerBean.isDisableOwnerName();
        this.vehicleComponentEditable = ownerBean.isVehicleComponentEditable();
        this.dealerSaleAmt = ownerBean.isDealerSaleAmt();
        this.disableMaker = ownerBean.isDisableMaker();
        this.disableMakerModel = ownerBean.isDisableMakerModel();
        this.disableDealerCd = ownerBean.isDisableDealerCd();
        this.disableChasiNo = ownerBean.isDisableChasiNo();
        this.disableFuelType = ownerBean.isDisableFuelType();
        this.disableEngineNo = ownerBean.isDisableEngineNo();
        this.disableSeatCap = ownerBean.isDisableSeatCap();
        this.disableStandCap = ownerBean.isDisableStandCap();
        this.disableSleeperCap = ownerBean.isDisableSleeperCap();
        this.disableNoOfCyl = ownerBean.isDisableNoOfCyl();
        this.disableUnLdWtCap = ownerBean.isDisableUnLdWtCap();
        this.disableLdWtCap = ownerBean.isDisableLdWtCap();
        this.disableHorsePower = ownerBean.isDisableHorsePower();
        this.disableNorms = ownerBean.isDisableNorms();
        this.disableVchPurchaseAs = ownerBean.isDisableVchPurchaseAs();
        this.disableColor = ownerBean.isDisableColor();
        this.disableWheelBase = ownerBean.isDisableWheelBase();
        this.disableCubicCapacity = ownerBean.isDisableCubicCapacity();
        this.disableManuMonth = ownerBean.isDisableManuMonth();
        this.disableManuYr = ownerBean.isDisableManuYr();
        this.disableLength = ownerBean.isDisableLength();
        this.disableWidth = ownerBean.isDisableWidth();
        this.disableHeight = ownerBean.isDisableWidth();
        this.homoLdWt = ownerBean.getHomoLdWt();
        this.homoUnLdWt = ownerBean.getHomoUnLdWt();
        this.renderValidityPanel = ownerBean.isRenderValidityPanel();
        this.renderTaxPanel = ownerBean.isRenderTaxPanel();
        this.linkedVehDtls = ownerBean.getLinkedVehDtls();
        this.renderTaxExemption = ownerBean.isRenderTaxExemption();
        this.taxExemption = ownerBean.isTaxExemption();
        this.purCd = ownerBean.getPurCd();
        this.tempLdWt = ownerBean.getTempLdWt();
        this.tempUnLdWt = ownerBean.getTempUnLdWt();
        this.renderSpeedGov = ownerBean.isRenderSpeedGov();
        this.renderIsSpeedGov = ownerBean.isRenderIsSpeedGov();
        this.disableOtherState = ownerBean.isDisableOtherState();
        this.renderPrevCurrentAdd = ownerBean.isRenderPrevCurrentAdd();
        this.prevCurrAdd = ownerBean.getPrevCurrAdd();
        this.homoDobj = ownerBean.getHomoDobj();
        this.isScrapVehicleNORetain = ownerBean.isIsScrapVehicleNORetain();
        this.inspDobj = ownerBean.getInspDobj();
        this.minPurchaseDate = ownerBean.getMinPurchaseDate();
        this.renderGCW = ownerBean.isRenderGCW();
        this.previousRegnType = ownerBean.getPreviousRegnType();
        this.renderHpDtls = ownerBean.isRenderHpDtls();
        this.renderVehDtls = ownerBean.isRenderVehDtls();
        this.renderOwnerDtlsPartialBtn = ownerBean.isRenderOwnerDtlsPartialBtn();
        this.renderVehicleDtlsPartialBtn = ownerBean.isRenderVehicleDtlsPartialBtn();
        this.renderChasiOs = ownerBean.isRenderChasiOs();
        this.listOwnerCdDept = ownerBean.getListOwnerCdDept();
        this.listSpeedGovTypes = ownerBean.getListSpeedGovTypes();
        this.renderReflectiveTape = ownerBean.isRenderReflectiveTape();
        this.renderIsReflectiveTape = ownerBean.isRenderIsReflectiveTape();
        this.reflectiveTapeDobjPrev = ownerBean.getReflectiveTapeDobjPrev();
        this.disablePermanentAddress = ownerBean.isDisablePermanentAddress();
        this.tmConfigFitnessDobj = ownerBean.getTmConfigFitnessDobj();
        this.renderGenerateOTPBtn = ownerBean.isRenderGenerateOTPBtn();
        this.renderAuthenticateBtn = ownerBean.isRenderAuthenticateBtn();
        this.sendOTP = ownerBean.getSendOTP();
        this.enteredOTP = ownerBean.getEnteredOTP();
        this.renderedOTPFields = ownerBean.isRenderedOTPFields();
        this.renderedConfirmBtn = ownerBean.isRenderedConfirmBtn();
        this.llServiceResponse = ownerBean.getLlServiceResponse();
        this.errorMsg = ownerBean.getErrorMsg();
        this.licenseType = ownerBean.getLicenseType();
        this.DOBDate = ownerBean.getDOBDate();
        this.pushBkSeatRender = ownerBean.isPushBkSeatRender();
        this.disableAdvanceRegnAllotted = ownerBean.isDisableAdvanceRegnAllotted();
        this.saleAmtInWords = ownerBean.getSaleAmtInWords();
        this.render_vltd_details = ownerBean.isRender_vltd_details();
        this.offCdToOfOwnerDobj = ownerBean.getOffCdToOfOwnerDobj();
    }

    public OwnerBean setOwnerBeanFromModel(OwnerBean ownerBean) {
//        ownerBean.setList_maker_model(this.list_maker_model);
        ownerBean.setAppl_no(this.appl_no);
        ownerBean.setRegn_no(this.regn_no);
        ownerBean.setRegn_dt(this.regn_dt);
//        ownerBean.setRegn_dt_cal(this.regn_dt_cal);
//        ownerBean.setList_c_state(this.list_c_state);
//        ownerBean.setList_c_district(this.list_c_district);
//        ownerBean.setList_adv_state(this.list_adv_state);
//        ownerBean.setList_adv_district(this.list_adv_district);
//        ownerBean.setList_p_state(this.list_p_state);
//        ownerBean.setList_p_district(this.list_p_district);
//        ownerBean.setList_vh_class(this.list_vh_class);
//        ownerBean.setList_vm_catg(this.list_vm_catg);
//        ownerBean.setList_maker(this.list_maker);
//        ownerBean.setList_fuel(this.list_fuel);
        ownerBean.setDisableRegnType(this.disableRegnType);
//        ownerBean.setList_regn_type(this.list_regn_type);
//        ownerBean.setList_owner_cd(this.list_owner_cd);
//        ownerBean.setListOwnerCatg(this.listOwnerCatg);
        ownerBean.setState_cd(this.state_cd);
//        ownerBean.setList_dealer_cd(this.list_dealer_cd);
        ownerBean.setToday(this.today);
        ownerBean.setOther_criteria(this.other_criteria);
//        ownerBean.setList_other_criteria(this.list_other_criteria);
        ownerBean.setImported_veh(this.imported_veh);
//        ownerBean.setList_imported_veh(this.list_imported_veh);
        ownerBean.setNorms(this.norms);
//        ownerBean.setList_norms(this.list_norms);
        ownerBean.setDisableSamePerm(this.disableSamePerm);
        ownerBean.setVch_purchase_as(this.vch_purchase_as);
//        ownerBean.setList_purchase_as(this.list_purchase_as);
        ownerBean.setSale_amt(this.sale_amt);
        ownerBean.setTax_mode(this.tax_mode);
//        ownerBean.setList_tax_mode(this.list_tax_mode);
        ownerBean.setAnnual_income(this.annual_income);
        ownerBean.setOwner_dobj_prv(this.owner_dobj_prv);
        ownerBean.setOwnerDobj(this.ownerDobj);
        ownerBean.setSpeedGovPrev(this.speedGovPrev);
        ownerBean.setVehType(this.vehType);
//        ownerBean.setList_veh_type(this.list_veh_type);
        ownerBean.setCurrentDate(this.currentDate);
        ownerBean.setSamePermAddress(this.samePermAddress);
        ownerBean.setExArmyVehicle_Visibility_tab(this.exArmyVehicle_Visibility_tab);
        ownerBean.setImportedVehicle_Visibility_tab(this.importedVehicle_Visibility_tab);
        ownerBean.setAxleDetail_Visibility_tab(this.axleDetail_Visibility_tab);
        ownerBean.setCngDetails_Visibility_tab(this.cngDetails_Visibility_tab);
        ownerBean.setLinked_regnNo_tab(this.linked_regnNo_tab);
        ownerBean.setSamePermAdd_Rend(this.samePermAdd_Rend);
        ownerBean.setFit_upto(this.fit_upto);
        ownerBean.setRegn_upto(this.regn_upto);
        ownerBean.setTemp_dobj(this.temp_dobj);
        ownerBean.setTemp_dobj_prev(this.temp_dobj_prev);
        ownerBean.setRenderTempOwner(this.renderTempOwner);
//        ownerBean.setList_office_to(this.list_office_to);
        ownerBean.setOwner_identification(this.owner_identification);
        ownerBean.setAdvanceRegNoDobj(AdvanceRegnNoDobjModel.convertModelToDobj(this.advanceRegnNoDobj));
        ownerBean.setBlnAdvancedRegNo(this.blnAdvancedRegNo);
        ownerBean.setBlnPgAdvancedRegNo(this.blnPgAdvancedRegNo);
        ownerBean.setBlnDisableCurrentState(this.blnDisableCurrentState);
        ownerBean.setTempReg(this.tempReg);
        ownerBean.setTempReg_prev(this.tempReg_prev);
        ownerBean.setBlnRegnTypeTemp(this.blnRegnTypeTemp);
        ownerBean.setBlnDisableRegnTypeTemp(this.blnDisableRegnTypeTemp);
        ownerBean.setModelEditable(this.modelEditable);
        ownerBean.setRenderModelEditable(this.renderModelEditable);
        ownerBean.setRenderModelSelectMenu(this.renderModelSelectMenu);
        ownerBean.setDisableOwnerSr(this.disableOwnerSr);
        ownerBean.setDisableFName(this.disableFName);
        ownerBean.setRenderInspectionPanel(this.renderInspectionPanel);
        ownerBean.setRenderInspectionPanelNT(this.renderInspectionPanelNT);
        ownerBean.setIsDealer(this.isDealer);
        ownerBean.setOtherStateDobj(this.otherStateDobj);
        ownerBean.setOtherStateDobjPrev(this.otherStateDobjPrev);
//        ownerBean.setList_ac_audio_video_fitted(this.list_ac_audio_video_fitted);
        ownerBean.setAdvanceRegnAllotted(this.advanceRegnAllotted);
        ownerBean.setRegnNoAlloted(this.regnNoAlloted);
        ownerBean.setRegnNoAllotedPanel(this.regnNoAllotedPanel);
        ownerBean.setSeriesAvailMessage(this.seriesAvailMessage);
        ownerBean.setAdvRegnRetenRadiobtn(this.AdvRegnRetenRadiobtn);
        ownerBean.setRetenRegNoDobj(this.retenRegNoDobj);
        ownerBean.setPanelRadioBtn(this.panelRadioBtn);
        ownerBean.setPanelAdvAppdtl(this.panelAdvAppdtl);
        ownerBean.setPanelAdvRetAppdtl(this.panelAdvRetAppdtl);
        ownerBean.setPanelAdvAdddtl(this.panelAdvAdddtl);
        ownerBean.setPanelAdvRetAdddtl(this.panelAdvRetAdddtl);
        ownerBean.setAdvanceAllotedLabel(this.advanceAllotedLabel);
        ownerBean.setTradeCertExpireMess(this.tradeCertExpireMess);
        ownerBean.setCdDobjClone(this.cdDobjClone);
        ownerBean.setRenderTempBodyBuilding(this.renderTempBodyBuilding);
        ownerBean.setRenderTempOther(this.renderTempOther);
//        ownerBean.setListTempReasons(this.listTempReasons);
//        ownerBean.setList_c_state_to(this.list_c_state_to);
        ownerBean.setIsHomologationData(this.isHomologationData);
        ownerBean.setToRetention(this.toRetention);
        ownerBean.setPermitPanel(this.permitPanel);
        ownerBean.setIsPmtTypeRqrd(this.isPmtTypeRqrd);
        ownerBean.setIsPmtCatgRqrd(this.isPmtCatgRqrd);
        ownerBean.setDisableTempStateTo(this.disableTempStateTo);
        ownerBean.setDisableOwnerCd(this.disableOwnerCd);
        ownerBean.setDisablePurchaseDt(this.disablePurchaseDt);
        ownerBean.setDisableOwnerName(this.disableOwnerName);
        ownerBean.setVehicleComponentEditable(this.vehicleComponentEditable);
        ownerBean.setDealerSaleAmt(this.dealerSaleAmt);
        ownerBean.setDisableMaker(this.disableMaker);
        ownerBean.setDisableMakerModel(this.disableMakerModel);
        ownerBean.setDisableDealerCd(this.disableDealerCd);
        ownerBean.setDisableChasiNo(this.disableChasiNo);
        ownerBean.setDisableFuelType(this.disableFuelType);
        ownerBean.setDisableEngineNo(this.disableEngineNo);
        ownerBean.setDisableSeatCap(this.disableSeatCap);
        ownerBean.setDisableStandCap(this.disableStandCap);
        ownerBean.setDisableSleeperCap(this.disableSleeperCap);
        ownerBean.setDisableNoOfCyl(this.disableNoOfCyl);
        ownerBean.setDisableUnLdWtCap(this.disableUnLdWtCap);
        ownerBean.setDisableLdWtCap(this.disableLdWtCap);
        ownerBean.setDisableHorsePower(this.disableHorsePower);
        ownerBean.setDisableNorms(this.disableNorms);
        ownerBean.setDisableVchPurchaseAs(this.disableVchPurchaseAs);
        ownerBean.setDisableColor(this.disableColor);
        ownerBean.setDisableWheelBase(this.disableWheelBase);
        ownerBean.setDisableCubicCapacity(this.disableCubicCapacity);
        ownerBean.setDisableManuMonth(this.disableManuMonth);
        ownerBean.setDisableManuYr(this.disableManuYr);
        ownerBean.setDisableLength(this.disableLength);
        ownerBean.setDisableWidth(this.disableWidth);
        ownerBean.setDisableWidth(this.disableHeight);
        ownerBean.setHomoLdWt(this.homoLdWt);
        ownerBean.setHomoUnLdWt(this.homoUnLdWt);
        ownerBean.setRenderValidityPanel(this.renderValidityPanel);
        ownerBean.setRenderTaxPanel(this.renderTaxPanel);
        ownerBean.setLinkedVehDtls(this.linkedVehDtls);
        ownerBean.setRenderTaxExemption(this.renderTaxExemption);
        ownerBean.setTaxExemption(this.taxExemption);
        ownerBean.setPurCd(this.purCd);
        ownerBean.setTempLdWt(this.tempLdWt);
        ownerBean.setTempUnLdWt(this.tempUnLdWt);
        ownerBean.setRenderSpeedGov(this.renderSpeedGov);
        ownerBean.setRenderIsSpeedGov(this.renderIsSpeedGov);
        ownerBean.setDisableOtherState(this.disableOtherState);
        ownerBean.setRenderPrevCurrentAdd(this.renderPrevCurrentAdd);
        ownerBean.setPrevCurrAdd(this.prevCurrAdd);
        ownerBean.setHomoDobj(this.homoDobj);
        ownerBean.setIsScrapVehicleNORetain(this.isScrapVehicleNORetain);
        ownerBean.setInspDobj(this.inspDobj);
        ownerBean.setMinPurchaseDate(this.minPurchaseDate);
        ownerBean.setRenderGCW(this.renderGCW);
        ownerBean.setPreviousRegnType(this.previousRegnType);
        ownerBean.setRenderHpDtls(this.renderHpDtls);
        ownerBean.setRenderVehDtls(this.renderVehDtls);
        ownerBean.setRenderOwnerDtlsPartialBtn(this.renderOwnerDtlsPartialBtn);
        ownerBean.setRenderVehicleDtlsPartialBtn(this.renderVehicleDtlsPartialBtn);
        ownerBean.setRenderChasiOs(this.renderChasiOs);
        ownerBean.setRenderNewLoi(this.renderNewLoi);
//        ownerBean.setListOwnerCdDept(this.listOwnerCdDept);
//        ownerBean.setListSpeedGovTypes(this.listSpeedGovTypes);
        ownerBean.setRenderReflectiveTape(this.renderReflectiveTape);
        ownerBean.setRenderIsReflectiveTape(this.renderIsReflectiveTape);
        ownerBean.setReflectiveTapeDobjPrev(this.reflectiveTapeDobjPrev);
        ownerBean.setDisablePermanentAddress(this.disablePermanentAddress);
        ownerBean.setTmConfigFitnessDobj(this.tmConfigFitnessDobj);
        ownerBean.setRenderGenerateOTPBtn(this.renderGenerateOTPBtn);
        ownerBean.setRenderAuthenticateBtn(this.renderAuthenticateBtn);
        ownerBean.setSendOTP(this.sendOTP);
        ownerBean.setEnteredOTP(this.enteredOTP);
        ownerBean.setRenderedOTPFields(this.renderedOTPFields);
        ownerBean.setRenderedConfirmBtn(this.renderedConfirmBtn);
        ownerBean.setLlServiceResponse(this.llServiceResponse);
        ownerBean.setErrorMsg(this.errorMsg);
        ownerBean.setLicenseType(this.licenseType);
        ownerBean.setDOBDate(this.DOBDate);
        ownerBean.setPushBkSeatRender(this.pushBkSeatRender);
        ownerBean.setDisableAdvanceRegnAllotted(this.disableAdvanceRegnAllotted);
        ownerBean.setSaleAmtInWords(this.saleAmtInWords);
        ownerBean.setRender_vltd_details(this.render_vltd_details);
        ownerBean.setOffCdToOfOwnerDobj(this.offCdToOfOwnerDobj);
        return ownerBean;
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

    public void enableForDriveAwayChassisInDealer(boolean flag) {
        disableSeatCap = flag;
        disableStandCap = flag;
        disableSleeperCap = flag;
        disableUnLdWtCap = flag;
        disableLdWtCap = flag;
        disableHeight = flag;
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

    public String allowTempRegistrationForDealer(TmConfigurationDobj tmConf, String userCategory, String userStateCode) {
        String exception = null;
        try {
            if (userCategory != null && userCategory.equals(TableConstants.USER_CATG_DEALER) && tmConf != null && tmConf.isTempFeeInNewRegis()) {
                if (!"RJ".equals(userStateCode) && temp_dobj != null && "OR".equals(temp_dobj.getPurpose()) && userStateCode.equals(temp_dobj.getState_cd_to()) && tmConf.getTmConfigDealerDobj() != null && tmConf.getTmConfigDealerDobj().isTempRegnAllowNonTransVeh() && tmConf.getTmConfigDealerDobj().isNewRegnNotAllowTransVeh()) {
                    if (ServerUtility.getVahan4StartDate(userStateCode, temp_dobj.getOff_cd_to()) != null) {
                        temp_dobj.setOff_cd_to(0);
                        throw new VahanException("VAHAN 4 Implemented in selected office and you are authorized to register the vehicle directly in this RTO. Please select the option 'Entry-New Registration' at Home Page.");
                    }
                } else if (vehType != null && !tmConf.getTmConfigDealerDobj().isTempRegnAllowNonTransVeh() && "OR".equals(temp_dobj.getPurpose()) && TableConstants.VM_VEHTYPE_NON_TRANSPORT == Integer.valueOf(vehType)) {
                    if (ServerUtility.getVahan4StartDate(userStateCode, temp_dobj.getOff_cd_to()) != null) {
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

    public boolean transportCatgValidation(String vehType) {
        boolean flag = false;
        if (tmConfDobj != null && ownerDobj != null && tmConfDobj.getTmConfigDealerDobj() != null && Integer.parseInt(vehType) == TableConstants.VM_VEHTYPE_TRANSPORT && ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_NEW) && purCd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
            if (!tmConfDobj.getTmConfigDealerDobj().isNewRegnNotAllowTransVeh()) {
                flag = true;
                setVehType("-1");
                list_vh_class.clear();
                ownerDobj.setVh_class(-1);
            }
        }
        return flag;
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
            if (userCatgForLoggedInUser.equals(TableConstants.USER_CATG_STATE_ADMIN)) {//forstateadmin
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
            owner_dobj.setRqrd_tax_modes(NewImplementation.getTaxModes(getOwnerDobj()));
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

    public void tradeCertificateValidation(String dealerCd, String vchCatg, TmConfigurationDobj tmConfDobj,
            String userCategory, String userStateCode, int offCode) throws VahanException {
        if (userCategory != null && !userCategory.equals("") && dealerCd != null && !dealerCd.equals("") && vchCatg != null && !vchCatg.equals("") && tmConfDobj != null) {
            boolean isConsiderTradeCert = false;
            if (userCategory.equals(TableConstants.USER_CATG_OFF_STAFF) && tmConfDobj.isConsiderTradeCert()) {
                boolean isRegisteredDealer = ServerUtility.getRegisteredDealerInfo(userStateCode, offCode, dealerCd);
                if (isRegisteredDealer) {
                    isConsiderTradeCert = true;
                }
            }

            if (isConsiderTradeCert || TableConstants.User_Dealer.equals(userCategory)) {
                setTradeCertExpireMess(ServerUtility.getDealerTradeCertificateDetails(dealerCd, vchCatg, userStateCode, tmConfDobj));
                if (!CommonUtils.isNullOrBlank(getTradeCertExpireMess())) {
                    throw new VahanException(getTradeCertExpireMess());
                }
            }
        }
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

    public void validateHomoGCW(Owner_dobj homoDobj, Owner_dobj dobj) throws VahanException {
        if (homoDobj != null && dobj != null && TableConstants.GCW_VEH_CLASS.contains("," + dobj.getVh_class() + ",")) {
            if (homoDobj.getGcw() > dobj.getGcw()) {
                throw new VahanException("GCW should be greater than or equal to Homologation GCW.");
            }
        }
    }

    public void validateTempRegistrationData(Owner_dobj dobj, int purCd, String regnType, SessionVariablesModel sessionVariables) throws VahanException {
        if (tmConfDobj != null && dobj != null && tmConfDobj.isAllowRegnDataFoundForTRC() && purCd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                && TableConstants.VM_REGN_TYPE_TEMPORARY.equals(regnType) && getTempReg() != null && getTempReg_prev() != null
                && (!getTempReg().getTmp_state_cd().equals(getTempReg_prev().getTmp_state_cd()) || getTempReg().getTmp_off_cd() != getTempReg_prev().getTmp_off_cd() || !getTempReg().getTmp_regn_dt().equals(getTempReg_prev().getTmp_regn_dt()))) {
            Owner_dobj owneTmpDobj = new OwnerImplementation().getValidateVtOwnerTempDobj(dobj.getChasi_no(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), tmConfDobj.isCnginfo_from_cngmaker());
            if (owneTmpDobj == null) {
                Date v4StartDate = ServerUtility.getVahan4StartDate(getTempReg().getTmp_state_cd(), getTempReg().getTmp_off_cd(), sessionVariables.getUserCatgForLoggedInUser());
                if (v4StartDate != null && getTempReg().getTmp_regn_dt() != null && getTempReg().getTmp_regn_dt().after(v4StartDate)) {
                    throw new VahanException("Application can't be inwarded as vehicle/chassis details not available in V4.");
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

    public void purchaseDateListener(RetroFittingDetailsBeanModel cngBean, HpaBeanModel hpaBean, InsBeanModel insBean) {
        Date minPurchaseDate = DateUtil.parseDate(DateUtil.getCurrentDate());
        if (ownerDobj.getPurchase_dt() != null) {
            minPurchaseDate = DateUtil.parseDate(DateUtils.getDateInDDMMYYYY(ownerDobj.getPurchase_dt()));
        }
//        RetroFittingDetailsBeanModel cngBean = (RetroFittingDetailsBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("retroFittingDetailsBean");
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
//        HpaBean hpaBean = (HpaBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("hpa_bean");
        if (hpaBean != null) {
            if (hpaBean.getHpaDobj() != null && hpaBean.getHpaDobj().getFrom_dt() != null && hpaBean.getHpaDobj().getFrom_dt().before(minPurchaseDate)) {
                hpaBean.setMinDate(DateUtil.parseDate(DateUtils.getDateInDDMMYYYY(hpaBean.getHpaDobj().getFrom_dt())));
            } else {
                hpaBean.setMinDate(minPurchaseDate);
            }
        }
//        InsBean insBean = (InsBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("ins_bean");
        if (insBean != null) {
            if (insBean.getIns_from() != null && insBean.getIns_from().before(minPurchaseDate)) {
                insBean.setMin_dt(DateUtil.parseDate(DateUtils.getDateInDDMMYYYY(insBean.getIns_from())));
            } else {
                insBean.setMin_dt(ServerUtil.dateRange(minPurchaseDate, 0, 0, -15));
            }
        }
    }

    public void validateInsuranceValidity(InsDobj insDobj) throws VahanException {
        if (insDobj != null && insDobj.isIibData() && insDobj.getIns_upto() != null && insDobj.getIns_upto().compareTo(currentDate) < 0) {
            throw new VahanException("Can't Inward/Proceed Application Because Insurance Validity Expired . The shown insurance details have been uploaded by Insurance Company to Vahan, if any discrepencies found, the Vehicle Owner have to contact respective Insurance Compnay for uploading the Latest Correct Insurance Details on Vahan.");
        }
    }
    
    public List getList_maker_model() {
        return list_maker_model;
    }

    public void setList_maker_model(List list_maker_model) {
        this.list_maker_model = list_maker_model;
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

    public Date getRegn_dt() {
        return regn_dt;
    }

    public void setRegn_dt(Date regn_dt) {
        this.regn_dt = regn_dt;
    }

    public Calendar getRegn_dt_cal() {
        return regn_dt_cal;
    }

    public void setRegn_dt_cal(Calendar regn_dt_cal) {
        this.regn_dt_cal = regn_dt_cal;
    }

    public List getList_c_state() {
        return list_c_state;
    }

    public void setList_c_state(List list_c_state) {
        this.list_c_state = list_c_state;
    }

    public List getList_c_district() {
        return list_c_district;
    }

    public void setList_c_district(List list_c_district) {
        this.list_c_district = list_c_district;
    }

    public List getList_adv_state() {
        return list_adv_state;
    }

    public void setList_adv_state(List list_adv_state) {
        this.list_adv_state = list_adv_state;
    }

    public List getList_adv_district() {
        return list_adv_district;
    }

    public void setList_adv_district(List list_adv_district) {
        this.list_adv_district = list_adv_district;
    }

    public List getList_p_state() {
        return list_p_state;
    }

    public void setList_p_state(List list_p_state) {
        this.list_p_state = list_p_state;
    }

    public List getList_p_district() {
        return list_p_district;
    }

    public void setList_p_district(List list_p_district) {
        this.list_p_district = list_p_district;
    }

    public List getList_vh_class() {
        return list_vh_class;
    }

    public void setList_vh_class(List list_vh_class) {
        this.list_vh_class = list_vh_class;
    }

    public List getList_vm_catg() {
        return list_vm_catg;
    }

    public void setList_vm_catg(List list_vm_catg) {
        this.list_vm_catg = list_vm_catg;
    }

    public List getList_maker() {
        return list_maker;
    }

    public void setList_maker(List list_maker) {
        this.list_maker = list_maker;
    }

    public List getList_fuel() {
        return list_fuel;
    }

    public void setList_fuel(List list_fuel) {
        this.list_fuel = list_fuel;
    }

    public boolean isDisableRegnType() {
        return disableRegnType;
    }

    public void setDisableRegnType(boolean disableRegnType) {
        this.disableRegnType = disableRegnType;
    }

    public List getList_regn_type() {
        return list_regn_type;
    }

    public void setList_regn_type(List list_regn_type) {
        this.list_regn_type = list_regn_type;
    }

    public List getList_owner_cd() {
        return list_owner_cd;
    }

    public void setList_owner_cd(List list_owner_cd) {
        this.list_owner_cd = list_owner_cd;
    }

    public List getListOwnerCatg() {
        return listOwnerCatg;
    }

    public void setListOwnerCatg(List listOwnerCatg) {
        this.listOwnerCatg = listOwnerCatg;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public List getList_dealer_cd() {
        return list_dealer_cd;
    }

    public void setList_dealer_cd(List list_dealer_cd) {
        this.list_dealer_cd = list_dealer_cd;
    }

    public Date getToday() {
        return today;
    }

    public void setToday(Date today) {
        this.today = today;
    }

    public Integer getOther_criteria() {
        return other_criteria;
    }

    public void setOther_criteria(Integer other_criteria) {
        this.other_criteria = other_criteria;
    }

    public List getList_other_criteria() {
        return list_other_criteria;
    }

    public void setList_other_criteria(List list_other_criteria) {
        this.list_other_criteria = list_other_criteria;
    }

    public String getImported_veh() {
        return imported_veh;
    }

    public void setImported_veh(String imported_veh) {
        this.imported_veh = imported_veh;
    }

    public List getList_imported_veh() {
        return list_imported_veh;
    }

    public void setList_imported_veh(List list_imported_veh) {
        this.list_imported_veh = list_imported_veh;
    }

    public int getNorms() {
        return norms;
    }

    public void setNorms(int norms) {
        this.norms = norms;
    }

    public List getList_norms() {
        return list_norms;
    }

    public void setList_norms(List list_norms) {
        this.list_norms = list_norms;
    }

    public boolean isDisableSamePerm() {
        return disableSamePerm;
    }

    public void setDisableSamePerm(boolean disableSamePerm) {
        this.disableSamePerm = disableSamePerm;
    }

    public String getVch_purchase_as() {
        return vch_purchase_as;
    }

    public void setVch_purchase_as(String vch_purchase_as) {
        this.vch_purchase_as = vch_purchase_as;
    }

    public List getList_purchase_as() {
        return list_purchase_as;
    }

    public void setList_purchase_as(List list_purchase_as) {
        this.list_purchase_as = list_purchase_as;
    }

    public Integer getSale_amt() {
        return sale_amt;
    }

    public void setSale_amt(Integer sale_amt) {
        this.sale_amt = sale_amt;
    }

    public String getTax_mode() {
        return tax_mode;
    }

    public void setTax_mode(String tax_mode) {
        this.tax_mode = tax_mode;
    }

    public List getList_tax_mode() {
        return list_tax_mode;
    }

    public void setList_tax_mode(List list_tax_mode) {
        this.list_tax_mode = list_tax_mode;
    }

    public Integer getAnnual_income() {
        return annual_income;
    }

    public void setAnnual_income(Integer annual_income) {
        this.annual_income = annual_income;
    }

    public List<ComparisonBeanModel> getCompBeanList() {
        return compBeanList;
    }

    public void setCompBeanList(List<ComparisonBeanModel> compBeanList) {
        this.compBeanList = compBeanList;
    }

    public Owner_dobj getOwner_dobj_prv() {
        return owner_dobj_prv;
    }

    public void setOwner_dobj_prv(Owner_dobj owner_dobj_prv) {
        this.owner_dobj_prv = owner_dobj_prv;
    }

    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    public SpeedGovernorDobj getSpeedGovPrev() {
        return speedGovPrev;
    }

    public void setSpeedGovPrev(SpeedGovernorDobj speedGovPrev) {
        this.speedGovPrev = speedGovPrev;
    }

    public String getVehType() {
        return vehType;
    }

    public void setVehType(String vehType) {
        this.vehType = vehType;
    }

    public List getList_veh_type() {
        return list_veh_type;
    }

    public void setList_veh_type(List list_veh_type) {
        this.list_veh_type = list_veh_type;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public boolean isSamePermAddress() {
        return samePermAddress;
    }

    public void setSamePermAddress(boolean samePermAddress) {
        this.samePermAddress = samePermAddress;
    }

    public boolean isExArmyVehicle_Visibility_tab() {
        return exArmyVehicle_Visibility_tab;
    }

    public void setExArmyVehicle_Visibility_tab(boolean exArmyVehicle_Visibility_tab) {
        this.exArmyVehicle_Visibility_tab = exArmyVehicle_Visibility_tab;
    }

    public boolean isImportedVehicle_Visibility_tab() {
        return importedVehicle_Visibility_tab;
    }

    public void setImportedVehicle_Visibility_tab(boolean importedVehicle_Visibility_tab) {
        this.importedVehicle_Visibility_tab = importedVehicle_Visibility_tab;
    }

    public boolean isAxleDetail_Visibility_tab() {
        return axleDetail_Visibility_tab;
    }

    public void setAxleDetail_Visibility_tab(boolean axleDetail_Visibility_tab) {
        this.axleDetail_Visibility_tab = axleDetail_Visibility_tab;
    }

    public boolean isCngDetails_Visibility_tab() {
        return cngDetails_Visibility_tab;
    }

    public void setCngDetails_Visibility_tab(boolean cngDetails_Visibility_tab) {
        this.cngDetails_Visibility_tab = cngDetails_Visibility_tab;
    }

    public boolean isLinked_regnNo_tab() {
        return linked_regnNo_tab;
    }

    public void setLinked_regnNo_tab(boolean linked_regnNo_tab) {
        this.linked_regnNo_tab = linked_regnNo_tab;
    }

    public boolean isSamePermAdd_Rend() {
        return samePermAdd_Rend;
    }

    public void setSamePermAdd_Rend(boolean samePermAdd_Rend) {
        this.samePermAdd_Rend = samePermAdd_Rend;
    }

    public Date getFit_upto() {
        return fit_upto;
    }

    public void setFit_upto(Date fit_upto) {
        this.fit_upto = fit_upto;
    }

    public Date getRegn_upto() {
        return regn_upto;
    }

    public void setRegn_upto(Date regn_upto) {
        this.regn_upto = regn_upto;
    }

    public Owner_temp_dobj getTemp_dobj() {
        return temp_dobj;
    }

    public void setTemp_dobj(Owner_temp_dobj temp_dobj) {
        this.temp_dobj = temp_dobj;
    }

    public Owner_temp_dobj getTemp_dobj_prev() {
        return temp_dobj_prev;
    }

    public void setTemp_dobj_prev(Owner_temp_dobj temp_dobj_prev) {
        this.temp_dobj_prev = temp_dobj_prev;
    }

    public boolean isRenderTempOwner() {
        return renderTempOwner;
    }

    public void setRenderTempOwner(boolean renderTempOwner) {
        this.renderTempOwner = renderTempOwner;
    }

    public List getList_office_to() {
        return list_office_to;
    }

    public void setList_office_to(List list_office_to) {
        this.list_office_to = list_office_to;
    }

    public OwnerIdentificationDobj getOwner_identification() {
        return owner_identification;
    }

    public void setOwner_identification(OwnerIdentificationDobj owner_identification) {
        this.owner_identification = owner_identification;
    }

    public AdvanceRegnNoDobjModel getAdvanceRegnNoDobj() {
        return advanceRegnNoDobj;
    }

    public void setAdvanceRegnNoDobj(AdvanceRegnNoDobjModel advanceRegnNoDobj) {
        this.advanceRegnNoDobj = advanceRegnNoDobj;
    }

    public boolean isBlnAdvancedRegNo() {
        return blnAdvancedRegNo;
    }

    public void setBlnAdvancedRegNo(boolean blnAdvancedRegNo) {
        this.blnAdvancedRegNo = blnAdvancedRegNo;
    }

    public boolean isBlnPgAdvancedRegNo() {
        return blnPgAdvancedRegNo;
    }

    public void setBlnPgAdvancedRegNo(boolean blnPgAdvancedRegNo) {
        this.blnPgAdvancedRegNo = blnPgAdvancedRegNo;
    }

    public boolean isBlnDisableCurrentState() {
        return blnDisableCurrentState;
    }

    public void setBlnDisableCurrentState(boolean blnDisableCurrentState) {
        this.blnDisableCurrentState = blnDisableCurrentState;
    }

    public TempRegDobj getTempReg() {
        return tempReg;
    }

    public void setTempReg(TempRegDobj tempReg) {
        this.tempReg = tempReg;
    }

    public TempRegDobj getTempReg_prev() {
        return tempReg_prev;
    }

    public void setTempReg_prev(TempRegDobj tempReg_prev) {
        this.tempReg_prev = tempReg_prev;
    }

    public boolean isBlnRegnTypeTemp() {
        return blnRegnTypeTemp;
    }

    public void setBlnRegnTypeTemp(boolean blnRegnTypeTemp) {
        this.blnRegnTypeTemp = blnRegnTypeTemp;
    }

    public boolean isBlnDisableRegnTypeTemp() {
        return blnDisableRegnTypeTemp;
    }

    public void setBlnDisableRegnTypeTemp(boolean blnDisableRegnTypeTemp) {
        this.blnDisableRegnTypeTemp = blnDisableRegnTypeTemp;
    }

    public UserAuthorityDobj getAuthorityDobj() {
        return authorityDobj;
    }

    public void setAuthorityDobj(UserAuthorityDobj authorityDobj) {
        this.authorityDobj = authorityDobj;
    }

    public boolean isModelEditable() {
        return modelEditable;
    }

    public void setModelEditable(boolean modelEditable) {
        this.modelEditable = modelEditable;
    }

    public boolean isRenderModelEditable() {
        return renderModelEditable;
    }

    public void setRenderModelEditable(boolean renderModelEditable) {
        this.renderModelEditable = renderModelEditable;
    }

    public boolean isRenderModelSelectMenu() {
        return renderModelSelectMenu;
    }

    public void setRenderModelSelectMenu(boolean renderModelSelectMenu) {
        this.renderModelSelectMenu = renderModelSelectMenu;
    }

    public boolean isDisableOwnerSr() {
        return disableOwnerSr;
    }

    public void setDisableOwnerSr(boolean disableOwnerSr) {
        this.disableOwnerSr = disableOwnerSr;
    }

    public boolean isDisableFName() {
        return disableFName;
    }

    public void setDisableFName(boolean disableFName) {
        this.disableFName = disableFName;
    }

    public boolean isRenderInspectionPanel() {
        return renderInspectionPanel;
    }

    public void setRenderInspectionPanel(boolean renderInspectionPanel) {
        this.renderInspectionPanel = renderInspectionPanel;
    }

    public boolean isRenderInspectionPanelNT() {
        return renderInspectionPanelNT;
    }

    public void setRenderInspectionPanelNT(boolean renderInspectionPanelNT) {
        this.renderInspectionPanelNT = renderInspectionPanelNT;
    }

    public boolean isIsDealer() {
        return isDealer;
    }

    public void setIsDealer(boolean isDealer) {
        this.isDealer = isDealer;
    }

    public OtherStateVehDobj getOtherStateDobj() {
        return otherStateDobj;
    }

    public void setOtherStateDobj(OtherStateVehDobj otherStateDobj) {
        this.otherStateDobj = otherStateDobj;
    }

    public OtherStateVehDobj getOtherStateDobjPrev() {
        return otherStateDobjPrev;
    }

    public void setOtherStateDobjPrev(OtherStateVehDobj otherStateDobjPrev) {
        this.otherStateDobjPrev = otherStateDobjPrev;
    }

    public List getList_ac_audio_video_fitted() {
        return list_ac_audio_video_fitted;
    }

    public void setList_ac_audio_video_fitted(List list_ac_audio_video_fitted) {
        this.list_ac_audio_video_fitted = list_ac_audio_video_fitted;
    }

    public String getAdvanceRegnAllotted() {
        return advanceRegnAllotted;
    }

    public void setAdvanceRegnAllotted(String advanceRegnAllotted) {
        this.advanceRegnAllotted = advanceRegnAllotted;
    }

    public String getRegnNoAlloted() {
        return regnNoAlloted;
    }

    public void setRegnNoAlloted(String regnNoAlloted) {
        this.regnNoAlloted = regnNoAlloted;
    }

    public boolean isRegnNoAllotedPanel() {
        return regnNoAllotedPanel;
    }

    public void setRegnNoAllotedPanel(boolean regnNoAllotedPanel) {
        this.regnNoAllotedPanel = regnNoAllotedPanel;
    }

    public String getSeriesAvailMessage() {
        return seriesAvailMessage;
    }

    public void setSeriesAvailMessage(String seriesAvailMessage) {
        this.seriesAvailMessage = seriesAvailMessage;
    }

    public String getAdvRegnRetenRadiobtn() {
        return AdvRegnRetenRadiobtn;
    }

    public void setAdvRegnRetenRadiobtn(String AdvRegnRetenRadiobtn) {
        this.AdvRegnRetenRadiobtn = AdvRegnRetenRadiobtn;
    }

    public RetenRegnNo_dobj getRetenRegNoDobj() {
        return retenRegNoDobj;
    }

    public void setRetenRegNoDobj(RetenRegnNo_dobj retenRegNoDobj) {
        this.retenRegNoDobj = retenRegNoDobj;
    }

    public boolean isPanelRadioBtn() {
        return panelRadioBtn;
    }

    public void setPanelRadioBtn(boolean panelRadioBtn) {
        this.panelRadioBtn = panelRadioBtn;
    }

    public boolean isPanelAdvAppdtl() {
        return panelAdvAppdtl;
    }

    public void setPanelAdvAppdtl(boolean panelAdvAppdtl) {
        this.panelAdvAppdtl = panelAdvAppdtl;
    }

    public boolean isPanelAdvRetAppdtl() {
        return panelAdvRetAppdtl;
    }

    public void setPanelAdvRetAppdtl(boolean panelAdvRetAppdtl) {
        this.panelAdvRetAppdtl = panelAdvRetAppdtl;
    }

    public boolean isPanelAdvAdddtl() {
        return panelAdvAdddtl;
    }

    public void setPanelAdvAdddtl(boolean panelAdvAdddtl) {
        this.panelAdvAdddtl = panelAdvAdddtl;
    }

    public boolean isPanelAdvRetAdddtl() {
        return panelAdvRetAdddtl;
    }

    public void setPanelAdvRetAdddtl(boolean panelAdvRetAdddtl) {
        this.panelAdvRetAdddtl = panelAdvRetAdddtl;
    }

    public String getAdvanceAllotedLabel() {
        return advanceAllotedLabel;
    }

    public void setAdvanceAllotedLabel(String advanceAllotedLabel) {
        this.advanceAllotedLabel = advanceAllotedLabel;
    }

    public TmConfigurationDobj getTmConfDobj() {
        return tmConfDobj;
    }

    public void setTmConfDobj(TmConfigurationDobj tmConfDobj) {
        this.tmConfDobj = tmConfDobj;
    }

    public String getTradeCertExpireMess() {
        return tradeCertExpireMess;
    }

    public void setTradeCertExpireMess(String tradeCertExpireMess) {
        this.tradeCertExpireMess = tradeCertExpireMess;
    }

    public CdDobj getCdDobjClone() {
        return cdDobjClone;
    }

    public void setCdDobjClone(CdDobj cdDobjClone) {
        this.cdDobjClone = cdDobjClone;
    }

    public boolean isRenderTempBodyBuilding() {
        return renderTempBodyBuilding;
    }

    public void setRenderTempBodyBuilding(boolean renderTempBodyBuilding) {
        this.renderTempBodyBuilding = renderTempBodyBuilding;
    }

    public boolean isRenderTempOther() {
        return renderTempOther;
    }

    public void setRenderTempOther(boolean renderTempOther) {
        this.renderTempOther = renderTempOther;
    }

    public List getListTempReasons() {
        return listTempReasons;
    }

    public void setListTempReasons(List listTempReasons) {
        this.listTempReasons = listTempReasons;
    }

    public List getList_c_state_to() {
        return list_c_state_to;
    }

    public void setList_c_state_to(List list_c_state_to) {
        this.list_c_state_to = list_c_state_to;
    }

    public String getIsHomologationData() {
        return isHomologationData;
    }

    public void setIsHomologationData(String isHomologationData) {
        this.isHomologationData = isHomologationData;
    }

    public boolean isToRetention() {
        return toRetention;
    }

    public void setToRetention(boolean toRetention) {
        this.toRetention = toRetention;
    }

    public boolean isPermitPanel() {
        return permitPanel;
    }

    public void setPermitPanel(boolean permitPanel) {
        this.permitPanel = permitPanel;
    }

    public boolean isIsPmtTypeRqrd() {
        return isPmtTypeRqrd;
    }

    public void setIsPmtTypeRqrd(boolean isPmtTypeRqrd) {
        this.isPmtTypeRqrd = isPmtTypeRqrd;
    }

    public boolean isIsPmtCatgRqrd() {
        return isPmtCatgRqrd;
    }

    public void setIsPmtCatgRqrd(boolean isPmtCatgRqrd) {
        this.isPmtCatgRqrd = isPmtCatgRqrd;
    }

    public boolean isDisableTempStateTo() {
        return disableTempStateTo;
    }

    public void setDisableTempStateTo(boolean disableTempStateTo) {
        this.disableTempStateTo = disableTempStateTo;
    }

    public boolean isDisableOwnerCd() {
        return disableOwnerCd;
    }

    public void setDisableOwnerCd(boolean disableOwnerCd) {
        this.disableOwnerCd = disableOwnerCd;
    }

    public boolean isDisablePurchaseDt() {
        return disablePurchaseDt;
    }

    public void setDisablePurchaseDt(boolean disablePurchaseDt) {
        this.disablePurchaseDt = disablePurchaseDt;
    }

    public boolean isDisableOwnerName() {
        return disableOwnerName;
    }

    public void setDisableOwnerName(boolean disableOwnerName) {
        this.disableOwnerName = disableOwnerName;
    }

    public boolean isVehicleComponentEditable() {
        return vehicleComponentEditable;
    }

    public void setVehicleComponentEditable(boolean vehicleComponentEditable) {
        this.vehicleComponentEditable = vehicleComponentEditable;
    }

    public boolean isDealerSaleAmt() {
        return dealerSaleAmt;
    }

    public void setDealerSaleAmt(boolean dealerSaleAmt) {
        this.dealerSaleAmt = dealerSaleAmt;
    }

    public boolean isDisableMaker() {
        return disableMaker;
    }

    public void setDisableMaker(boolean disableMaker) {
        this.disableMaker = disableMaker;
    }

    public boolean isDisableMakerModel() {
        return disableMakerModel;
    }

    public void setDisableMakerModel(boolean disableMakerModel) {
        this.disableMakerModel = disableMakerModel;
    }

    public boolean isDisableDealerCd() {
        return disableDealerCd;
    }

    public void setDisableDealerCd(boolean disableDealerCd) {
        this.disableDealerCd = disableDealerCd;
    }

    public boolean isDisableChasiNo() {
        return disableChasiNo;
    }

    public void setDisableChasiNo(boolean disableChasiNo) {
        this.disableChasiNo = disableChasiNo;
    }

    public boolean isDisableFuelType() {
        return disableFuelType;
    }

    public void setDisableFuelType(boolean disableFuelType) {
        this.disableFuelType = disableFuelType;
    }

    public boolean isDisableEngineNo() {
        return disableEngineNo;
    }

    public void setDisableEngineNo(boolean disableEngineNo) {
        this.disableEngineNo = disableEngineNo;
    }

    public boolean isDisableSeatCap() {
        return disableSeatCap;
    }

    public void setDisableSeatCap(boolean disableSeatCap) {
        this.disableSeatCap = disableSeatCap;
    }

    public boolean isDisableStandCap() {
        return disableStandCap;
    }

    public void setDisableStandCap(boolean disableStandCap) {
        this.disableStandCap = disableStandCap;
    }

    public boolean isDisableSleeperCap() {
        return disableSleeperCap;
    }

    public void setDisableSleeperCap(boolean disableSleeperCap) {
        this.disableSleeperCap = disableSleeperCap;
    }

    public boolean isDisableNoOfCyl() {
        return disableNoOfCyl;
    }

    public void setDisableNoOfCyl(boolean disableNoOfCyl) {
        this.disableNoOfCyl = disableNoOfCyl;
    }

    public boolean isDisableUnLdWtCap() {
        return disableUnLdWtCap;
    }

    public void setDisableUnLdWtCap(boolean disableUnLdWtCap) {
        this.disableUnLdWtCap = disableUnLdWtCap;
    }

    public boolean isDisableLdWtCap() {
        return disableLdWtCap;
    }

    public void setDisableLdWtCap(boolean disableLdWtCap) {
        this.disableLdWtCap = disableLdWtCap;
    }

    public boolean isDisableHorsePower() {
        return disableHorsePower;
    }

    public void setDisableHorsePower(boolean disableHorsePower) {
        this.disableHorsePower = disableHorsePower;
    }

    public boolean isDisableNorms() {
        return disableNorms;
    }

    public void setDisableNorms(boolean disableNorms) {
        this.disableNorms = disableNorms;
    }

    public boolean isDisableVchPurchaseAs() {
        return disableVchPurchaseAs;
    }

    public void setDisableVchPurchaseAs(boolean disableVchPurchaseAs) {
        this.disableVchPurchaseAs = disableVchPurchaseAs;
    }

    public boolean isDisableColor() {
        return disableColor;
    }

    public void setDisableColor(boolean disableColor) {
        this.disableColor = disableColor;
    }

    public boolean isDisableWheelBase() {
        return disableWheelBase;
    }

    public void setDisableWheelBase(boolean disableWheelBase) {
        this.disableWheelBase = disableWheelBase;
    }

    public boolean isDisableCubicCapacity() {
        return disableCubicCapacity;
    }

    public void setDisableCubicCapacity(boolean disableCubicCapacity) {
        this.disableCubicCapacity = disableCubicCapacity;
    }

    public boolean isDisableManuMonth() {
        return disableManuMonth;
    }

    public void setDisableManuMonth(boolean disableManuMonth) {
        this.disableManuMonth = disableManuMonth;
    }

    public boolean isDisableManuYr() {
        return disableManuYr;
    }

    public void setDisableManuYr(boolean disableManuYr) {
        this.disableManuYr = disableManuYr;
    }

    public boolean isDisableLength() {
        return disableLength;
    }

    public void setDisableLength(boolean disableLength) {
        this.disableLength = disableLength;
    }

    public boolean isDisableWidth() {
        return disableWidth;
    }

    public void setDisableWidth(boolean disableWidth) {
        this.disableWidth = disableWidth;
    }

    public boolean isDisableHeight() {
        return disableHeight;
    }

    public void setDisableHeight(boolean disableHeight) {
        this.disableHeight = disableHeight;
    }

    public int getHomoLdWt() {
        return homoLdWt;
    }

    public void setHomoLdWt(int homoLdWt) {
        this.homoLdWt = homoLdWt;
    }

    public int getHomoUnLdWt() {
        return homoUnLdWt;
    }

    public void setHomoUnLdWt(int homoUnLdWt) {
        this.homoUnLdWt = homoUnLdWt;
    }

    public boolean isRenderValidityPanel() {
        return renderValidityPanel;
    }

    public void setRenderValidityPanel(boolean renderValidityPanel) {
        this.renderValidityPanel = renderValidityPanel;
    }

    public boolean isRenderTaxPanel() {
        return renderTaxPanel;
    }

    public void setRenderTaxPanel(boolean renderTaxPanel) {
        this.renderTaxPanel = renderTaxPanel;
    }

    public String getLinkedVehDtls() {
        return linkedVehDtls;
    }

    public void setLinkedVehDtls(String linkedVehDtls) {
        this.linkedVehDtls = linkedVehDtls;
    }

    public boolean isRenderTaxExemption() {
        return renderTaxExemption;
    }

    public void setRenderTaxExemption(boolean renderTaxExemption) {
        this.renderTaxExemption = renderTaxExemption;
    }

    public boolean isTaxExemption() {
        return taxExemption;
    }

    public void setTaxExemption(boolean taxExemption) {
        this.taxExemption = taxExemption;
    }

    public int getPurCd() {
        return purCd;
    }

    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }

    public int getTempLdWt() {
        return tempLdWt;
    }

    public void setTempLdWt(int tempLdWt) {
        this.tempLdWt = tempLdWt;
    }

    public int getTempUnLdWt() {
        return tempUnLdWt;
    }

    public void setTempUnLdWt(int tempUnLdWt) {
        this.tempUnLdWt = tempUnLdWt;
    }

    public boolean isRenderSpeedGov() {
        return renderSpeedGov;
    }

    public void setRenderSpeedGov(boolean renderSpeedGov) {
        this.renderSpeedGov = renderSpeedGov;
    }

    public boolean isRenderIsSpeedGov() {
        return renderIsSpeedGov;
    }

    public void setRenderIsSpeedGov(boolean renderIsSpeedGov) {
        this.renderIsSpeedGov = renderIsSpeedGov;
    }

    public boolean isDisableOtherState() {
        return disableOtherState;
    }

    public void setDisableOtherState(boolean disableOtherState) {
        this.disableOtherState = disableOtherState;
    }

    public boolean isRenderPrevCurrentAdd() {
        return renderPrevCurrentAdd;
    }

    public void setRenderPrevCurrentAdd(boolean renderPrevCurrentAdd) {
        this.renderPrevCurrentAdd = renderPrevCurrentAdd;
    }

    public String getPrevCurrAdd() {
        return prevCurrAdd;
    }

    public void setPrevCurrAdd(String prevCurrAdd) {
        this.prevCurrAdd = prevCurrAdd;
    }

    public Owner_dobj getHomoDobj() {
        return homoDobj;
    }

    public void setHomoDobj(Owner_dobj homoDobj) {
        this.homoDobj = homoDobj;
    }

    public boolean isIsScrapVehicleNORetain() {
        return isScrapVehicleNORetain;
    }

    public void setIsScrapVehicleNORetain(boolean isScrapVehicleNORetain) {
        this.isScrapVehicleNORetain = isScrapVehicleNORetain;
    }

    public InspectionDobj getInspDobj() {
        return inspDobj;
    }

    public void setInspDobj(InspectionDobj inspDobj) {
        this.inspDobj = inspDobj;
    }

    public Date getMinPurchaseDate() {
        return minPurchaseDate;
    }

    public void setMinPurchaseDate(Date minPurchaseDate) {
        this.minPurchaseDate = minPurchaseDate;
    }

    public boolean isRenderGCW() {
        return renderGCW;
    }

    public void setRenderGCW(boolean renderGCW) {
        this.renderGCW = renderGCW;
    }

    public String getPreviousRegnType() {
        return previousRegnType;
    }

    public void setPreviousRegnType(String previousRegnType) {
        this.previousRegnType = previousRegnType;
    }

    public boolean isRenderHpDtls() {
        return renderHpDtls;
    }

    public void setRenderHpDtls(boolean renderHpDtls) {
        this.renderHpDtls = renderHpDtls;
    }

    public boolean isRenderVehDtls() {
        return renderVehDtls;
    }

    public void setRenderVehDtls(boolean renderVehDtls) {
        this.renderVehDtls = renderVehDtls;
    }

    public boolean isRenderOwnerDtlsPartialBtn() {
        return renderOwnerDtlsPartialBtn;
    }

    public void setRenderOwnerDtlsPartialBtn(boolean renderOwnerDtlsPartialBtn) {
        this.renderOwnerDtlsPartialBtn = renderOwnerDtlsPartialBtn;
    }

    public boolean isRenderVehicleDtlsPartialBtn() {
        return renderVehicleDtlsPartialBtn;
    }

    public void setRenderVehicleDtlsPartialBtn(boolean renderVehicleDtlsPartialBtn) {
        this.renderVehicleDtlsPartialBtn = renderVehicleDtlsPartialBtn;
    }

    public boolean isRenderChasiOs() {
        return renderChasiOs;
    }

    public void setRenderChasiOs(boolean renderChasiOs) {
        this.renderChasiOs = renderChasiOs;
    }

    public String getEmpCodeLoggedIn() {
        return empCodeLoggedIn;
    }

    public void setEmpCodeLoggedIn(String empCodeLoggedIn) {
        this.empCodeLoggedIn = empCodeLoggedIn;
    }

    public String getStateCodeSelected() {
        return stateCodeSelected;
    }

    public void setStateCodeSelected(String stateCodeSelected) {
        this.stateCodeSelected = stateCodeSelected;
    }

    public int getOffCodeSelected() {
        return offCodeSelected;
    }

    public void setOffCodeSelected(int offCodeSelected) {
        this.offCodeSelected = offCodeSelected;
    }

    public String getUserCatgForLoggedInUser() {
        return userCatgForLoggedInUser;
    }

    public void setUserCatgForLoggedInUser(String userCatgForLoggedInUser) {
        this.userCatgForLoggedInUser = userCatgForLoggedInUser;
    }

    public int getActionCodeSelected() {
        return actionCodeSelected;
    }

    public void setActionCodeSelected(int actionCodeSelected) {
        this.actionCodeSelected = actionCodeSelected;
    }

    public boolean isRenderNewLoi() {
        return renderNewLoi;
    }

    public void setRenderNewLoi(boolean renderNewLoi) {
        this.renderNewLoi = renderNewLoi;
    }

    public List getListOwnerCdDept() {
        return listOwnerCdDept;
    }

    public void setListOwnerCdDept(List listOwnerCdDept) {
        this.listOwnerCdDept = listOwnerCdDept;
    }

    public List getListSpeedGovTypes() {
        return listSpeedGovTypes;
    }

    public void setListSpeedGovTypes(List listSpeedGovTypes) {
        this.listSpeedGovTypes = listSpeedGovTypes;
    }

    public boolean isRenderReflectiveTape() {
        return renderReflectiveTape;
    }

    public void setRenderReflectiveTape(boolean renderReflectiveTape) {
        this.renderReflectiveTape = renderReflectiveTape;
    }

    public boolean isRenderIsReflectiveTape() {
        return renderIsReflectiveTape;
    }

    public void setRenderIsReflectiveTape(boolean renderIsReflectiveTape) {
        this.renderIsReflectiveTape = renderIsReflectiveTape;
    }

    public ReflectiveTapeDobj getReflectiveTapeDobjPrev() {
        return reflectiveTapeDobjPrev;
    }

    public void setReflectiveTapeDobjPrev(ReflectiveTapeDobj reflectiveTapeDobjPrev) {
        this.reflectiveTapeDobjPrev = reflectiveTapeDobjPrev;
    }

    public boolean isDisablePermanentAddress() {
        return disablePermanentAddress;
    }

    public void setDisablePermanentAddress(boolean disablePermanentAddress) {
        this.disablePermanentAddress = disablePermanentAddress;
    }

    public TmConfigurationFitnessDobj getTmConfigFitnessDobj() {
        return tmConfigFitnessDobj;
    }

    public void setTmConfigFitnessDobj(TmConfigurationFitnessDobj tmConfigFitnessDobj) {
        this.tmConfigFitnessDobj = tmConfigFitnessDobj;
    }

    public boolean isRenderGenerateOTPBtn() {
        return renderGenerateOTPBtn;
    }

    public void setRenderGenerateOTPBtn(boolean renderGenerateOTPBtn) {
        this.renderGenerateOTPBtn = renderGenerateOTPBtn;
    }

    public boolean isRenderAuthenticateBtn() {
        return renderAuthenticateBtn;
    }

    public void setRenderAuthenticateBtn(boolean renderAuthenticateBtn) {
        this.renderAuthenticateBtn = renderAuthenticateBtn;
    }

    public String getSendOTP() {
        return sendOTP;
    }

    public void setSendOTP(String sendOTP) {
        this.sendOTP = sendOTP;
    }

    public String getEnteredOTP() {
        return enteredOTP;
    }

    public void setEnteredOTP(String enteredOTP) {
        this.enteredOTP = enteredOTP;
    }

    public boolean isRenderedOTPFields() {
        return renderedOTPFields;
    }

    public void setRenderedOTPFields(boolean renderedOTPFields) {
        this.renderedOTPFields = renderedOTPFields;
    }

    public boolean isRenderedConfirmBtn() {
        return renderedConfirmBtn;
    }

    public void setRenderedConfirmBtn(boolean renderedConfirmBtn) {
        this.renderedConfirmBtn = renderedConfirmBtn;
    }

    public BiolicObj getLlServiceResponse() {
        return llServiceResponse;
    }

    public void setLlServiceResponse(BiolicObj llServiceResponse) {
        this.llServiceResponse = llServiceResponse;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public String getDOBDate() {
        return DOBDate;
    }

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

    public String getSaleAmtInWords() {
        return saleAmtInWords;
    }

    public void setSaleAmtInWords(String saleAmtInWords) {
        this.saleAmtInWords = saleAmtInWords;
    }

    public boolean isRender_vltd_details() {
        return render_vltd_details;
    }

    public void setRender_vltd_details(boolean render_vltd_details) {
        this.render_vltd_details = render_vltd_details;
    }

    public String getTEMP_OTHER_STATE() {
        return TEMP_OTHER_STATE;
    }

    public String getTEMP_OTHER_RTO() {
        return TEMP_OTHER_RTO;
    }

    public String getTEMP_BODY_BUILDING() {
        return TEMP_BODY_BUILDING;
    }

    public String getTEMP_SAME_RTO() {
        return TEMP_SAME_RTO;
    }

    public String getOEM_TO_DEALER_LOCATION() {
        return OEM_TO_DEALER_LOCATION;
    }

    public String getDEMONSTRATION_WITHIN_SAME_RTO() {
        return DEMONSTRATION_WITHIN_SAME_RTO;
    }

    public String getDEMONSTRATION_WITHIN_DIFF_RTO() {
        return DEMONSTRATION_WITHIN_DIFF_RTO;
    }

    public String getSTOCK_TRANSFER() {
        return STOCK_TRANSFER;
    }

    public boolean isAuctionVisibilityTab() {
        return auctionVisibilityTab;
    }

    public void setAuctionVisibilityTab(boolean auctionVisibilityTab) {
        this.auctionVisibilityTab = auctionVisibilityTab;
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
