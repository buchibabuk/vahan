/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.Dealer;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.OwnerBean;
import nic.vahan.form.bean.InsBean;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.server.ServerUtil;
import nic.vahan.form.bean.*;
import nic.vahan.form.dobj.*;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import nic.java.util.DateUtils;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.State;
import nic.vahan.form.bean.DocumentUploadBean;
import nic.vahan.form.dobj.dealer.PendencyBankDobj;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;
import nic.vahan.form.impl.admin.NocVerificationImpl;
import nic.vahan.form.impl.permit.PermitImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.form.dobj.fitness.TmConfigurationFitnessDobj;
import nic.vahan.form.impl.dealer.PendencyBankDetailImpl;
import nic.vahan.services.insurance.InsuranceDetailService;
import nic.vahan.services.insurance.dobj.InsuranceInfoDobj;
import nic.vahan5.reg.rest.config.SpringContext;
import nic.vahan5.reg.rest.model.AxleBeanModel;
import nic.vahan5.reg.rest.model.ComparisonBeanModel;
import nic.vahan5.reg.rest.model.ContextMessageModel;
import nic.vahan5.reg.rest.model.DocumentUploadBeanModel;
import nic.vahan5.reg.rest.model.ExArmyBeanModel;
import nic.vahan5.reg.rest.model.FitnessBeanModel;
import nic.vahan5.reg.rest.model.HpaBeanModel;
import nic.vahan5.reg.rest.model.ImportedVehicleBeanModel;
import nic.vahan5.reg.rest.model.InsBeanModel;
import nic.vahan5.reg.rest.model.OwnerBeanModel;
import nic.vahan5.reg.rest.model.RetroFittingDetailsBeanModel;
import nic.vahan5.reg.rest.model.SessionVariablesModel;
import nic.vahan5.reg.rest.model.TrailerBeanModel;
import nic.vahan5.reg.rest.model.WorkBenchModel;
import nic.vahan5.reg.rest.model.WrapperModel;
import nic.vahan5.util.VahanUtil;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@ManagedBean(name = "workBench")
@ViewScoped
public class WorkBench implements Serializable {

    private WebClient.Builder webClientBuilder = SpringContext.getBean(WebClient.Builder.class);
    private static final Logger LOGGER = Logger.getLogger(WorkBench.class);
    @ManagedProperty(value = "#{approveImpl}")
    private ApproveImpl approveImpl;
    private int ROLE_CD;
    private String APPL_NO;
    private String PUR_CD;
    private String PURPOSE;
    private String OFFICE_REMARK;
    private String PUBLIC_REMARK;
    private String REGN_NO;
    private String FANCY_REGN_NO = null;
    private String APPL_DT;
    private String CUR_STATUS;//Current Application Status(N,C,D)
    private int ACTION_CDOE;
    private String COUNTER_ID;
    private String includeSrcURL = "defaultworkbench.xhtml";
    private boolean sub_panel = true;
    private boolean tabView = false;
    private boolean trailer_tab = true;
    private Map<String, Object> prevRoleLabelVale;
    private boolean main_panal_visibililty = false;
    private boolean tmp_veh_dtls = false;
    private boolean hypothecated = false;
    private boolean flag = true;
    private boolean otherStateVehicle;
    private boolean otherDistrictVehicle;
    private boolean blnScrappedVehicle;
    private boolean blnScrappedVehiclePanel;
    private String chasiNo;
    private String engineNo;
    private String regnType;
    private String regnNo;
    private List listRegnType;
    private List stateList;
    private List officeList;
    private List assignedReasonList;
    private boolean disAppPrint = false;
    private OtherStateVehDobj otherStateVehDobj = new OtherStateVehDobj();
    @ManagedProperty(value = "#{owner_bean}")
    private OwnerBean owner_bean;
    @ManagedProperty(value = "#{ins_bean}")
    private InsBean ins_bean;
    private boolean renderApplNoGenMessage;
    private boolean renderPrintDiscalimerButton;
    private String applNoGenMessage;
    private String RETEN_REGN_NO = null;
    private boolean vehicleHypothecatedDisable = false;
    private boolean renderCDVehicle;
    private EpayDobj checkFeeTax = null;
    private EpayDobj checkPaidFeeTax = null;
    private boolean renderCheckFeeTax = false;
    private boolean renderCheckFeeTaxTab = false;
    private boolean renderAppDisapp = true;
    private boolean autoRunCheckFeeTax = false;
    private boolean renderAdvanceNoOption = true;
    private boolean renderScrapCheckBoxOption;
    private boolean isVehicleNoScrapRetain;
    private boolean renderScrapRegnNo;
    private boolean renderExemptionOD;
    private StringBuilder exemptionOD = new StringBuilder("");
    private List<OwnerDetailsDobj> tempRegnDetailsList = null;
    private String nocDtlsMsg;
    private boolean renderPartialButton;
    private boolean regnTypeOSorORTO;
    private String vahanMessages = null;
    private String getDtlsBtnLabel = "Get Details";
    private SessionVariables sessionVariables = null;
    private boolean documentUploadShow = false;
    private String dmsFileUploadUrl;
    private String successUplodedMsg;
    private boolean showBankDetails = false;
    private PendencyBankDobj bankSubsidyDetail = null;
    private List bankNameList = new ArrayList();
    private boolean renderDocumentUploadBtn = false;
    @ManagedProperty(value = "#{documentUploadBean}")
    private DocumentUploadBean documentUpload_bean;
    private boolean renderTempStateOffPanel = false;
    private boolean renderVltdDialog = false;
    private boolean renderDMSMesgDialog = false;
    private boolean renderCheckFeeTaxButton = false;
    @ManagedProperty(value = "#{ownerChoiceNoBean}")
    private OwnerChoiceNoBean ownerChoiceNoBean;
    private boolean renderOwnerChoiceNoPanel = false;
    private boolean enableDisableTempRegnDetails = false;
    private String vehicleRegnPrefix;
    private boolean renderFasTagDialog = false;
    private boolean isFasTagInstalled = false;
    private boolean renderFastag = false;
    private boolean isTransportVehicle = true;
    private String vehRegnPlateColorCode = "";
    private String fancyRcptNo = null;

    public WorkBench() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getOffCodeSelected() == 0
                    || sessionVariables.getEmpCodeLoggedIn() == null
                    || sessionVariables.getUserCatgForLoggedInUser() == null) {
                vahanMessages = "Something went wrong, Please try again...";
                return;
            }

            listRegnType = new ArrayList();
            stateList = new ArrayList();
            officeList = new ArrayList();
            assignedReasonList = new ArrayList();
            List<TaxExemptiondobj> exemptionList = null;

            String user_catg = sessionVariables.getUserCatgForLoggedInUser();
            TmConfigurationDobj dobj = Util.getTmConfiguration();

            String[][] data;
            data = MasterTableFiller.masterTables.VM_REGN_TYPE.getData();

            if (user_catg.equals(TableConstants.User_Dealer)) {
                for (int i = 0; i < data.length; i++) {
                    if (data[i][0].equals(TableConstants.VM_REGN_TYPE_NEW)) {
                        listRegnType.add(new SelectItem(data[i][0], data[i][1]));
                        regnType = "N";
                    }
                    if (dobj.isTempRegnToNewRegnAtDealer()) {
                        if (data[i][0].equals(TableConstants.VM_REGN_TYPE_TEMPORARY)) {
                            listRegnType.add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                }
            } else {
                for (int i = 0; i < data.length; i++) {
                    listRegnType.add(new SelectItem(data[i][0], data[i][1]));
                }
            }

            stateList = MasterTableFiller.getStateList();

            data = MasterTableFiller.masterTables.TM_OFFICE.getData();
            for (int i = 0; i < data.length; i++) {
                officeList.add(new SelectItem(data[i][0], data[i][1]));
            }

            Map map = (Map) Util.getSession().getAttribute("seat_map");
            if (map == null) {
                return;
            }
            if (map.get("appl_no") != null) {
                this.APPL_NO = map.get("appl_no").toString();
            } else {
                this.APPL_NO = Util.getSelectedSeat().getAppl_no();
            }

            if (map.get("pur_code") != null) {
                this.PUR_CD = map.get("pur_code").toString();
            } else {
                this.PUR_CD = String.valueOf(Util.getSelectedSeat().getPur_cd());
            }

            if (map.get("actionCode") != null) {
                this.ACTION_CDOE = Integer.parseInt(map.get("actionCode").toString().trim());
                this.ROLE_CD = Integer.parseInt(map.get("actionCode").toString().substring(3));// for extracting role_code from action_code
            } else {
                this.ACTION_CDOE = Util.getSelectedSeat().getAction_cd();
                this.ROLE_CD = Integer.parseInt(String.valueOf(Util.getSelectedSeat().getAction_cd()).substring(3));// for extracting role_code from action_code
            }

            if (map.get("appl_dt") != null) {
                this.APPL_DT = map.get("appl_dt") != null ? map.get("appl_dt").toString() : "";
            } else if (Util.getSelectedSeat().getAppl_dt() != null) {
                this.APPL_DT = Util.getSelectedSeat().getAppl_dt();
            } else {
                this.APPL_DT = "";
            }

            if (map.get("office_remark") != null) {
                this.OFFICE_REMARK = map.get("office_remark").toString();
            } else {
                this.OFFICE_REMARK = Util.getSelectedSeat().getOffice_remark();
            }

            if (map.get("public_remark") != null) {
                this.PUBLIC_REMARK = map.get("public_remark").toString();
            } else {
                this.PUBLIC_REMARK = Util.getSelectedSeat().getRemark_for_public();
            }
            //this.PUBLIC_REMARK = map.get("public_remark").toString();
            if (map.get("Purpose") != null) {
                this.PURPOSE = map.get("Purpose").toString();
            } else {
                this.PURPOSE = Util.getSelectedSeat().getPurpose_descr();
            }

            if (map.get("regn_no") != null) {
                this.REGN_NO = map.get("regn_no").toString();
            } else {
                this.REGN_NO = Util.getSelectedSeat().getRegn_no();
            }

            if (map.get("cur_status") != null) {
                this.CUR_STATUS = map.get("cur_status").toString();
            } else {
                this.CUR_STATUS = Util.getSelectedSeat().getStatus();
            }

            this.prevRoleLabelVale = ServerUtil.getPrevRole(ACTION_CDOE, Integer.parseInt(PUR_CD), sessionVariables.getStateCodeSelected());

            if (Util.getSession().getAttribute("selected_cntr_id") != null) {
                this.COUNTER_ID = Util.getSession().getAttribute("selected_cntr_id").toString();
            }

            if (CUR_STATUS.trim().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING)) {
                setDisAppPrint(true);
                setAssignedReasonList(ServerUtil.getReasonsForHolding(APPL_NO));
            } else {
                setDisAppPrint(false);
            }
            exemptionList = new ExemptionFeeFineImpl().getExemptedDetails(ServerUtil.getLatestRcptNo(APPL_NO, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected()));
            if (exemptionList != null && !exemptionList.isEmpty()) {
                renderExemptionOD = true;
                for (TaxExemptiondobj obj : exemptionList) {
                    exemptionOD.append("Order No: ").append(obj.getPermissionNo()).append("/Order Date: ").append(obj.getPermissionDt()).append("/Reason: ").append(obj.getPerpose());
                    break;
                }
                for (TaxExemptiondobj obj : exemptionList) {
                    exemptionOD.append("<br>").append(obj.getExemHead()).append(": ").append(obj.getExemAmount());
                }
            }
            proccessWork();
        } catch (VahanException ve) {
            vahanMessages = ve.getMessage();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanMessages = TableConstants.SomthingWentWrong;
        }
    }

    @PostConstruct // it will call after the constructor of the class
    public void init() {
        try {
            if (prevRoleLabelVale != null && prevRoleLabelVale.isEmpty()) {
                approveImpl.getRevertBack().setItemDisabled(true);
            }
            main_panal_visibililty = true;
            if ((TableConstants.TM_ROLE_NEW_APPL == ACTION_CDOE || TableConstants.TM_ROLE_DEALER_NEW_APPL == ACTION_CDOE || TableConstants.TM_ROLE_NEW_APPL_TEMP == ACTION_CDOE || TableConstants.TM_ROLE_DEALER_TEMP_APPL == ACTION_CDOE) && APPL_NO != null && !APPL_NO.equals("")) {
                main_panal_visibililty = true;
            }
            if (owner_bean.isRenderVehicleDtlsPartialBtn()) {
                main_panal_visibililty = false;
            }
            if ((TableConstants.TM_ROLE_NEW_APPL == ACTION_CDOE || TableConstants.TM_ROLE_DEALER_NEW_APPL == ACTION_CDOE || TableConstants.TM_ROLE_NEW_APPL_TEMP == ACTION_CDOE || TableConstants.TM_ROLE_DEALER_TEMP_APPL == ACTION_CDOE) && APPL_NO.equals("")) {
                main_panal_visibililty = false;
            }
            initRender();
            if (PUR_CD == null || PUR_CD.isEmpty() || !PUR_CD.matches("[0-9]*")) {
                throw new VahanException("Error in getting details.Please Try Again");
            }
            if ((Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE && ACTION_CDOE != TableConstants.TM_ROLE_DEALER_NEW_APPL) || (Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE && ACTION_CDOE != TableConstants.TM_ROLE_DEALER_TEMP_APPL) || (Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && ACTION_CDOE != TableConstants.TM_ROLE_NEW_APPL)) {
                if (documentUpload_bean != null && documentUpload_bean.getDocDescrList() != null && !documentUpload_bean.getDocDescrList().isEmpty() && documentUpload_bean.isDocumentUploadShow() && documentUpload_bean.isRenderUiBasedDMSDocPanel()) {
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("redirectTo", "workbench");
                    setDocumentUploadShow(true);
                }

                if (documentUpload_bean != null && documentUpload_bean.getUploadedList() != null && !documentUpload_bean.getUploadedList().isEmpty() && documentUpload_bean.isDocumentUploadShow() && documentUpload_bean.isRenderApiBasedDMSDocPanel()) {
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("redirectTo", "workbench");
                    setDocumentUploadShow(true);
                }
            }
            if (TableConstants.USER_CATG_DEALER.equals(sessionVariables.getUserCatgForLoggedInUser()) && owner_bean != null
                    && owner_bean.getOwnerDobj() != null && Util.getTmConfiguration() != null && Util.getTmConfiguration().getTmConfigDealerDobj() != null) {
                VehicleParameters vehParameters = fillVehicleParametersFromDobj(owner_bean.getOwnerDobj());
                if (isCondition(replaceTagValues(Util.getTmConfiguration().getTmConfigDealerDobj().getOwnerChoiceConditionFormula(), vehParameters), "workbench")) {
                    renderOwnerChoiceNoTab(owner_bean.getOwnerDobj());
                }
            }
            if (!CommonUtils.isNullOrBlank(APPL_NO) && ACTION_CDOE == TableConstants.TM_ROLE_DEALER_VERIFICATION) {
                Map<String, String> choiceDetails = OwnerChoiceNoImpl.getOwnerChoiceRegnNoDetails(APPL_NO);
                if (choiceDetails != null && !choiceDetails.isEmpty() && owner_bean != null) {
                    owner_bean.setVehicleComponentEditable(true);
                }
            }
            if (enableDisableTempRegnDetails && owner_bean != null && owner_bean.getTempReg() != null) {
                owner_bean.setBlnDisableRegnTypeTemp(true);
            }
            if ("KL".equals(sessionVariables.getStateCodeSelected()) && hypothecated
                    && (Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE)) {
                vehicleHypothecatedDisable = true;
            }
        } catch (VahanException ve) {
            vahanMessages = ve.getMessage();
        }
    }

    public void initRender() {
        FacesContext ctx = FacesContext.getCurrentInstance();
//        if (Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
//                && (TableConstants.TM_ROLE_INSPECTION == ROLE_CD) || TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL == ACTION_CDOE) {
//            UIComponent panel = (UIComponent) ctx.getViewRoot().findComponent("masterLayout:workbench_tabview:owner_details_tab");
//            owner_bean.disableAll(panel.getChildren(), true);
//        }

        try {
            if (this.regnNo != null && !this.regnNo.trim().isEmpty()) {
                NocDobj nocDobj = new NocImpl().set_NOC_appl_db_to_dobj(null, this.regnNo.trim().toUpperCase());

                if (nocDobj != null) {
                    Date vahan4StartDate = ServerUtil.getVahan4StartDate(nocDobj.getState_cd(), nocDobj.getOff_cd());
                    if (vahan4StartDate != null) {
                        if (regnType != null && regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                            NocDobj nocEndorseDobj = ServerUtil.getNocEndorsementData(this.regnNo.trim().toUpperCase(), null);
                            if (nocEndorseDobj != null && (nocEndorseDobj.getState_cd().equalsIgnoreCase(sessionVariables.getStateCodeSelected()) && nocEndorseDobj.getOff_cd() == sessionVariables.getOffCodeSelected())) {
                                owner_bean.disableForHomologationData(false);
                            } else {
                                owner_bean.disableForHomologationData(true);
                            }
                        }
                    }
                }
            }

            if (owner_bean.getOwnerDobj() != null && (owner_bean.getOwnerDobj().getRegn_type().equals(TableConstants.VM_REGN_TYPE_SCRAPPED) || owner_bean.getOwnerDobj().getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY))) {
                ScrappedVehicleImpl scrapImpl = new ScrappedVehicleImpl();
                ScrappedVehicleDobj scrapDobj = scrapImpl.getScrappedInformationApplNo(APPL_NO);
                if (scrapDobj != null) {
                    isVehicleNoScrapRetain = true;
                }
                if (owner_bean.getOwnerDobj().getRegn_type().equals(TableConstants.VM_REGN_TYPE_SCRAPPED) || isIsVehicleNoScrapRetain()) {
                    scrapDobj = scrapImpl.getOldScrappedInformation(regnNo, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                    owner_bean.setDisableOwnerName(true);
                    owner_bean.setDisableFName(true);
                    owner_bean.setDisableOwnerCd(true);
                    UIComponent panel = (UIComponent) ctx.getViewRoot().findComponent("masterLayout:workbench_tabview:owner_details_tab");
                    if (panel != null) {
                        owner_bean.disableScrapVehicle(panel.getChildren(), true);
                    }
                    owner_bean.setDisableSamePerm(true);

                    if (scrapDobj != null) {
                        if (scrapDobj.getScrap_reason().trim().equals("3")) {
                            owner_bean.setDisableOwnerName(false);
                        }
                    }
                } else {
                    owner_bean.setDisableOwnerName(false);
                    owner_bean.setDisableFName(false);
                    owner_bean.setDisableOwnerCd(false);
                    UIComponent panel = (UIComponent) ctx.getViewRoot().findComponent("masterLayout:workbench_tabview:owner_details_tab");
                    if (panel != null) {
                        owner_bean.disableScrapVehicle(panel.getChildren(), false);
                    }
                    owner_bean.setDisableSamePerm(false);
                    owner_bean.tempPurposeListner(null);
                    if (scrapDobj != null) {
                        if (scrapDobj.getScrap_reason().trim().equals("3")) {
                            owner_bean.setDisableOwnerName(true);
                        }
                    }
                }
            }

            if (owner_bean.getOwnerDobj() != null) {
                if (owner_bean.getOwnerDobj().getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                    isTransportVehicle = false;
                }
                vehRegnPlateColorCode = ServerUtil.getRegnColorCode(owner_bean.getOwnerDobj(), Integer.parseInt(PUR_CD));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
        }
    }

    public void disableAllFields() {
        if (TableConstants.TM_NEW_RC_APPROVAL == ACTION_CDOE) {
            //UIComponent insPanel = (UIComponent) FacesContext.getCurrentInstance().getViewRoot().findComponent("masterLayout:workbench_tabview:HypothecationOwner");
            UIComponent panel = (UIComponent) FacesContext.getCurrentInstance().getViewRoot().findComponent("masterLayout:workbench_tabview:veh_info_tab");
//            UIComponent panelOwnerDetails = (UIComponent) FacesContext.getCurrentInstance().getViewRoot().findComponent("masterLayout:workbench_tabview:owner_details_tab");
            if (panel != null) {
                owner_bean.disableAll(panel.getChildren(), true);
            }
//            if (panelOwnerDetails != null) {
//                owner_bean.disableAll(panelOwnerDetails.getChildren(), true);
//            }
//            if (insPanel != null) {
//                owner_bean.disableAll(insPanel.getChildren(), true);
//            }
        }
    }

    public void proccessWork() {
        try {
            OwnerBean owner_bean = new OwnerBean();
            InsBean ins_bean = new InsBean();
            HpaBean hpa_bean = new HpaBean();
            ExArmyBean exArmyBean = new ExArmyBean();
            ImportedVehicleBean imp_bean = new ImportedVehicleBean();
            AxleBean axle_bean = new AxleBean();
            RetroFittingDetailsBean cng_bean = new RetroFittingDetailsBean();
            boolean isHomologationData = false;
            TmConfigurationDobj tmConf = Util.getTmConfiguration();
            Trailer_bean trailer_bean = new Trailer_bean();
            Owner_dobj owner_dobj = null;
            Owner_dobj ownerHomo = null;
            int pur_cd = Integer.parseInt(PUR_CD);
            renderOwnerChoiceNoPanel = false;
            if (owner_bean.getOwnerDobj() == null || sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getOffCodeSelected() == 0
                    || sessionVariables.getEmpCodeLoggedIn() == null) {
                throw new VahanException("Something went wrong. Please try again.");
            }
            if (TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE == pur_cd
                    || TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE == pur_cd
                    || TableConstants.VM_TRANSACTION_MAST_TEMP_REG == pur_cd || TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE == pur_cd) { //for temporary registration

                //filling the owner and vehicle details form ///////////////////
                OwnerImpl owner_Impl = new OwnerImpl();
                owner_dobj = owner_Impl.set_Owner_appl_db_to_dobj_with_state_off_cd(REGN_NO, APPL_NO, "", pur_cd);
                if (owner_dobj != null && owner_dobj.getVehType() == TableConstants.VM_VEHTYPE_TRANSPORT) {
                    NewImpl new_Impl = new NewImpl();
                    String servictype = new_Impl.getServiceTypeFromVaPermitNew(owner_dobj);
                    owner_dobj.setServicesType(servictype);
                    String[] region_covered = new_Impl.getRegionCoveredFromVaPermitNew(owner_dobj);
                    owner_dobj.setRegion_covered(region_covered);
                }

                if (owner_dobj != null) {
                    VehicleParameters vchparameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                    vchparameters.setREGN_NO(owner_dobj.getRegn_no());
                    if (isCondition(FormulaUtils.replaceTagPermitValues(tmConf.getShowPermitMultiRegion(), vchparameters), "processWork()")) {
                        owner_bean.setRenderMultiRegionList(true);
                    }
                }

                PermitImpl permitImpl = new PermitImpl();
                if (!CommonUtils.isNullOrBlank(APPL_NO) && tmConf.isNew_reg_loi() && PUR_CD.equalsIgnoreCase(String.valueOf(TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE)) && owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_NEW) && owner_dobj.getVh_class() == 57) {
                    String newPermitLoi = permitImpl.getNewLoiDetails(APPL_NO);
                    if (newPermitLoi != null) {
                        owner_bean.getOwnerDobj().setNewLoiNo(newPermitLoi);
                        owner_bean.setRenderNewLoi(true);
                        owner_bean.setDisableOwnerName(true);
                        owner_bean.setDisableFName(true);
                        owner_bean.setDisableOwnerCd(true);
                        owner_bean.setVehicleComponentEditable(true);
                        FacesContext ctx = FacesContext.getCurrentInstance();
                        UIComponent panel = (UIComponent) ctx.getViewRoot().findComponent("masterLayout:workbench_tabview:owner_details_tab");
                        if (panel != null) {
                            owner_bean.disableScrapVehicle(panel.getChildren(), true);
                        }
                        owner_bean.setDisableSamePerm(true);

                    }

                }

                if (sessionVariables.getSelectedWork().getAction_cd() == TableConstants.TM_ROLE_DEALER_NEW_APPL
                        || sessionVariables.getSelectedWork().getAction_cd() == TableConstants.TM_ROLE_NEW_APPL
                        || sessionVariables.getSelectedWork().getAction_cd() == TableConstants.TM_ROLE_NEW_APPL_TEMP
                        || sessionVariables.getSelectedWork().getAction_cd() == TableConstants.TM_ROLE_DEALER_TEMP_APPL) {
//                    renderPartialButton = true;
                    owner_bean.setRenderVehicleDtlsPartialBtn(true);
                }
                if (owner_dobj != null) {
                    owner_bean.getOwner_identification().setDisableAdhaar(false);
                    if (owner_dobj.getVh_class() != 0 && !(owner_dobj.getVh_class() == TableConstants.ERICKSHAW_VCH_CLASS && Util.getUserStateCode().equalsIgnoreCase("DL"))) {
                        owner_bean.getOwner_identification().setAadhar_no("");
                        owner_bean.getOwner_identification().setDisableAdhaar(true);
                    }
                    owner_bean.setRenderHpDtls(true);
                    owner_bean.setRenderVehDtls(true);
                    regnType = owner_dobj.getRegn_type();
                    chasiNo = owner_dobj.getChasi_no();
                    if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE) || owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                        regnTypeOSorORTO = true;
                    } else {
                        chasiNo = owner_dobj.getChasi_no();
                    }
                    if (chasiNo != null) {
                        String blackListedDetails = new BlackListedVehicleImpl().checkChassisNoForBlackList(owner_dobj.getChasi_no().toUpperCase());
                        if (blackListedDetails != null) {
                            throw new VahanException(blackListedDetails);
                        }
                    }
                    if (!CommonUtils.isNullOrBlank(owner_dobj.getEng_no()) && owner_dobj.getEng_no().length() >= 5) {
                        engineNo = owner_dobj.getEng_no().substring(owner_dobj.getEng_no().length() - 5, owner_dobj.getEng_no().length());;
                    }
                    if (tmConf != null && tmConf.getTmConfigDmsDobj() != null && tmConf.getTmConfigDmsDobj().getPurCd().contains("," + PUR_CD + ",") && TableConstants.VM_REGN_TYPE_NEW.equals(regnType) && (tmConf.getTmConfigDmsDobj().getDocUploadAllotedOff().contains("ALL") || tmConf.getTmConfigDmsDobj().getDocUploadAllotedOff().contains("," + sessionVariables.getOffCodeSelected() + ","))) {
                        if (tmConf.getTmConfigDmsDobj().getUploadActionCd().contains("," + ACTION_CDOE + ",") && !tmConf.getTmConfigDmsDobj().isDocsUploadAtOffice() && TableConstants.USER_CATG_OFF_STAFF.equals(sessionVariables.getUserCatgForLoggedInUser())) {
                            setRenderDocumentUploadBtn(true);
                        } else if ((tmConf.getTmConfigDmsDobj().isDocsFolwRequired() || tmConf.getTmConfigDmsDobj().isDocsUploadAtOffice()) && tmConf.getTmConfigDmsDobj().getUploadActionCd().contains("," + ACTION_CDOE + ",") && "KL".equals(sessionVariables.getStateCodeSelected())) {
                            setRenderDMSMesgDialog(true);
                            PrimeFaces.current().ajax().update("doc_upload_message");
                            PrimeFaces.current().executeScript("PF('doc_upload_dlg_var').show();");

                        }
                    }

                }
                if (tmConf != null && tmConf.getTmConfigDealerDobj() != null && TableConstants.USER_CATG_DEALER.equals(sessionVariables.getUserCatgForLoggedInUser()) && !tmConf.getTmConfigDealerDobj().isAttachFancyNoAtDealer()) {
                    setRenderAdvanceNoOption(false);
                }
                Map<String, String> choiceDetails = OwnerChoiceNoImpl.getOwnerChoiceRegnNoDetails(APPL_NO);
                if (choiceDetails != null && !choiceDetails.isEmpty()) {
                    renderAdvanceNoOption = false;
                }

                Owner_dobj owneTmpDobj = null;

                if (!regnTypeOSorORTO) {
                    if (owner_dobj != null && owner_dobj.getAuctionDobj() == null) {
                        owneTmpDobj = owner_Impl.getValidateVtOwnerTempDobj(chasiNo);
                    }
                }
                if (owneTmpDobj != null && owner_dobj != null) {
                    owner_dobj.setTempReg(owneTmpDobj.getTempReg());
                    enableDisableTempRegnDetails = true;
                }

                if (owner_dobj != null && ",4,10,16,".contains("," + owner_dobj.getNorms() + ",")
                        && !CommonUtils.isNullOrBlank(owner_dobj.getRegn_type()) && (TableConstants.VM_REGN_TYPE_NEW.equals(owner_dobj.getRegn_type()) || TableConstants.VM_REGN_TYPE_EXARMY.equals(owner_dobj.getRegn_type())
                        || TableConstants.VM_REGN_TYPE_TEMPORARY.equals(owner_dobj.getRegn_type()) || TableConstants.VM_REGN_TYPE_CD.equals(owner_dobj.getRegn_type()))) {
                    owner_bean.setRenderOwnerDisclaimer(true);
                }

                if (owner_dobj != null && owner_dobj.getChasi_no() != null
                        && ((pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE)
                        && (TableConstants.TM_ROLE_INSPECTION == ROLE_CD
                        || TableConstants.TM_NEW_RC_FITNESS_INSPECTION == ACTION_CDOE
                        || TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL == ACTION_CDOE
                        || TableConstants.TM_NEW_RC_VERIFICATION == ACTION_CDOE
                        || TableConstants.TM_NEW_RC_APPROVAL == ACTION_CDOE))) {
                    setRenderVltdDialog(false);
                    VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(owner_dobj);
                    if (tmConf != null && isCondition(replaceTagValues(tmConf.getVltd_condition_formula(), vehParameters), "getVltd_condition_formula")) {
                        VehicleTrackingDetailsImpl vehicleTrackingDetailsImpl = new VehicleTrackingDetailsImpl();
                        if (owner_dobj.getAppl_no() != null) {
                            owner_dobj.setVehicleTrackingDetailsDobj(vehicleTrackingDetailsImpl.getVehicleTrackingDetailsByAppl_no(owner_dobj.getAppl_no(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected()));
                        }
                        if (owner_dobj.getVehicleTrackingDetailsDobj() == null) {
                            owner_dobj.setVehicleTrackingDetailsDobj(vehicleTrackingDetailsImpl.getVehicleTrackingDetailsByChasiOrRegn_no(owner_dobj.getChasi_no(), owner_dobj.getRegn_no()));
                        }
                        if (owner_dobj.getVehicleTrackingDetailsDobj() != null) {
                            owner_bean.setRender_vltd_details(true);
                            owner_dobj.getVehicleTrackingDetailsDobj().setAppl_no(APPL_NO);
                        }
                        if (owner_dobj.getVehicleTrackingDetailsDobj() == null) {
                            TmConfigurationFitnessDobj tmConfigFitnessDobj = new FitnessImpl().getFitnessConfiguration(sessionVariables.getStateCodeSelected());
                            if (tmConfigFitnessDobj != null && isCondition(replaceTagValues(tmConfigFitnessDobj.getCheck_vltd(), vehParameters), "getCheck_Vltd")) {
                                throw new VahanException("The Vehicle does not have a VLT device installed/activated as per CMVR 125 H. Kindly check the VLTD portal for fitment/activation of VLT device and approve the same before registration of the vehicle.");
                            } else {
                                setRenderVltdDialog(true);
                                //RequestContext rc = RequestContext.getCurrentInstance();
                                //PrimeFaces.current().ajax().update("opvltd");
                                PrimeFaces.current().executeScript("PF('dlgvltd').show()");
                            }
                        }
                    }
                    //nitin kumar/ for getting vltd info 29-01-2019
                }
                // FASTag
                if (owner_dobj != null) {
                    VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(owner_dobj);
                    if (tmConf != null && tmConf.getTmConfigurationFasTag() != null && isCondition(replaceTagValues(tmConf.getTmConfigurationFasTag().getFasTagCondition(), vehParameters), "getFasTagCondition")) {
                        renderFastag = true;
                        if (!CommonUtils.isNullOrBlank(owner_dobj.getFasTagId())) {
                            isFasTagInstalled = true;
                        }
                    }
                }

                owner_bean.set_Owner_appl_dobj_to_bean(owner_dobj);
                owner_bean.setOwner_dobj_prv(owner_dobj);//for holding current dobj for using in the comparison.
                OwnerIdentificationImpl identificationImpl = new OwnerIdentificationImpl();
                TmConfigurationOwnerIdentificationDobj tmConfigOwnerId = identificationImpl.getTmConfigurationOwnerIdentification(sessionVariables.getStateCodeSelected());
                if (tmConfigOwnerId != null) {
                    if (owner_bean.getOwner_identification() == null) {
                        OwnerIdentificationDobj identificationDobj = new OwnerIdentificationDobj();
                        owner_bean.setOwner_identification(identificationDobj);
                    }

                    VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(owner_dobj);
                    if (owner_dobj != null && isCondition(replaceTagValues(tmConfigOwnerId.getPan_card_mandatory(), vehParameters), "getPan_card_mandatory")) {
                        owner_bean.getOwner_identification().setPan_card_mandatory(true);
                    }
                }

                if (owner_dobj != null) {
                    if (owner_dobj.getLaser_code() != null && !owner_dobj.getLaser_code().equals("")
                            && owner_dobj.getLaser_code().equals(TableConstants.HOMOLOGATION_DATA)) {
                        isHomologationData = true;
                        owner_bean.setIsHomologationData(TableConstants.HOMOLOGATION_DATA);
                        owner_bean.setPurCd(pur_cd);
                        owner_bean.setTempLdWt(owner_dobj.getLd_wt());
                        owner_bean.setTempUnLdWt(owner_dobj.getUnld_wt());
                        Owner_dobj ldUnLdWt = ServerUtil.getLdUnLdWtFromHomologation(owner_dobj.getMaker_model(), owner_dobj.getMaker());
                        if (ldUnLdWt != null) {
                            owner_bean.setHomoLdWt(ldUnLdWt.getLd_wt());
                            owner_bean.setHomoUnLdWt(ldUnLdWt.getUnld_wt());
                        }
                        Owner_dobj homoData = ServerUtil.getHomologationData(APPL_NO);
                        if (homoData != null) {
                            owner_bean.setHomoDobj(homoData);
                        }
                        if (chasiNo != null && engineNo != null) {
                            boolean homoEngValidate = true;
                            if (owneTmpDobj != null && Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && TableConstants.VM_REGN_TYPE_TEMPORARY.equals(regnType)) {
                                homoEngValidate = false;
                            }
//                          ownerHomo = ServerUtil.getOnwerDobjFromHomologation(chasiNo.toUpperCase().trim(), engineNo.toUpperCase().trim(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), homoEngValidate);
                            // cas service ticket validation
                            HttpSession session = null;
                            session = Util.getSession();
                            String serviceTicket = null;
                            /*iboolean casFlag = Boolean.parseBoolean((String) session.getAttribute("cas_flag"));
                            if (casFlag) {
                                String casTicket = (String) session.getAttribute("casTicket");
                                CASTicketDetails details = new CASAuthentication().homologationCasServiceTicket(casTicket);
                                if (details.getStatusCode() != 200) {
                                    throw new VahanException("Error while fetching details from Homologation portal. Please try again later.");
                                }
                                serviceTicket = details.getResonseTicket();
                            }*/

                            String restUrl = new VahanUtil().getHomologationUrl() + "?chasiNo=" + chasiNo
                                    + "&engineNo=" + engineNo + "&stateCodeSelected=" + sessionVariables.getStateCodeSelected()
                                    + "&offCodeSelected=" + sessionVariables.getOffCodeSelected() + "&homoEngValidate=" + homoEngValidate + "&serviceTicket=" + serviceTicket;
                            try {
                                ownerHomo = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .build()
                                        .get()
                                        .uri(restUrl)
                                        .retrieve()
                                        .bodyToMono(Owner_dobj.class)
                                        .block();
                                if (ownerHomo == null) {
                                    throw new VahanException("Error while fetching details from Homologation portal. Please try again later.");
                                }
                            } catch (WebClientResponseException e) {
                                LOGGER.error(e.getMessage());
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage());
                            }
                        }

                        if (ownerHomo != null && !CommonUtils.isNullOrBlank(ownerHomo.getModelManuLocCodeDescr())) {
                            owner_bean.getOwnerDobj().setModelManuLocCodeDescr(ownerHomo.getModelManuLocCodeDescr());
                            if (homoData == null) {
                                owner_bean.setHomoDobj(ownerHomo);
                            }
                        }
//                        if (!CommonUtils.isNullOrBlank(Util.getUserCategory()) && Util.getUserCategory().equalsIgnoreCase(TableConstants.USER_CATG_DEALER)) {
//                            if (ownerHomo != null) {
//                                tmConf = Util.getTmConfiguration();
//                                boolean isExShoowroomPriceDisable = tmConf.isEx_showroom_price_homologation();
//                                if (isExShoowroomPriceDisable) {
//                                    if (ownerHomo.getSale_amt() == 0) {
//                                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Inventory detail found but"
//                                                + " Ex-Showroom Price has not been uploaded for the state " + ServerUtil.getStateNameByStateCode(Util.getUserStateCode()) + " <br/> "
//                                                + " UMRN/Model Code: " + ownerHomo.getMaker_model() + " | "
//                                                + " Color Code : " + ownerHomo.getColorCode() + " | "
//                                                + " Feature Code : " + ownerHomo.getFeatureCode() + " "));
//                                        tabView = false;
//                                        return;
//                                    } else {
//                                        owner_bean.setDealerSaleAmt(true);
//                                    }
//                                }
//
////                            List<SelectItem> makerList = owner_Impl.getAssignedMakerList(user_cd);
////                            boolean found = false;
////                            for (SelectItem item : makerList) {
////                                if (item.getValue().toString().trim().equals(String.valueOf(ownerHomo.getMaker()))) {
////                                    found = true;
////                                    break;
////                                }
////                            }
////                            if (!found) {
////                                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "User is not permitted"
////                                        + " to register mentioned maker. "));
////                                tabView = false;
////                                return;
////                            }
////
//                                owner_bean.setIsHomologationData(TableConstants.HOMOLOGATION_DATA);
//                                owner_bean.disableDealerFields(ownerHomo.getManu_mon(), ownerHomo.getManu_yr());
//                                owner_bean.setHomoLdWt(ownerHomo.getLd_wt());
//                                owner_bean.setHomoUnLdWt(ownerHomo.getUnld_wt());
//                                owner_bean.setHomoDobj(ownerHomo);
//
//
//                            } else {
//                                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Inventory details against this Chassis No/Engine No(last 5 chars) Combination"
//                                        + " is not uploaded by the manufacturer."
//                                        + " Please contact respective manufacturer to upload the inventory."));
//                                tabView = false;
//                                return;
//                            }
//                        }
                    }

                    if (owner_dobj.getSpeedGovernorDobj() != null) {
                        SpeedGovernorDobj sg = (SpeedGovernorDobj) owner_dobj.getSpeedGovernorDobj().clone();
                        owner_bean.setSpeedGovPrev(sg);
                        owner_bean.setRenderSpeedGov(true);
                    }

                    if (owner_dobj.getReflectiveTapeDobj() != null) {
                        ReflectiveTapeDobj refDobg = owner_dobj.getReflectiveTapeDobj().clone();
                        owner_bean.setReflectiveTapeDobjPrev(refDobg);
                        owner_bean.setRenderReflectiveTape(true);;
                    }
                }

                //for showing old details of temporary registration on same chasi no
                if (owner_dobj != null && owner_dobj.getRegn_type() != null && owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_TEMPORARY)) {
                    OwnerImpl ownerImpl = new OwnerImpl();
                    tempRegnDetailsList = ownerImpl.getTempRegistrationDetailsList(owner_dobj.getChasi_no());
                }

                if (owner_dobj != null && (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                        || owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))) {

                    if (owner_dobj.getOtherStateVehDobj() != null) {
                        otherStateVehDobj = owner_dobj.getOtherStateVehDobj();
                        owner_bean.setOtherStateDobj(otherStateVehDobj);
                        owner_bean.setOtherStateDobjPrev((OtherStateVehDobj) otherStateVehDobj.clone());
                        regnNo = otherStateVehDobj.getOldRegnNo();
                    }

                    if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                        otherDistrictVehicle = true;
                    } else {
                        otherStateVehicle = true;
                    }

                }

                if (owner_dobj != null && owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CD)) {
                    renderCDVehicle = true;
                    CdDobj cdDobjPrev = null;
                    if (owner_dobj.getCdDobj() != null) {
                        cdDobjPrev = (CdDobj) owner_dobj.getCdDobj().clone();
                    } else {
                        cdDobjPrev = new CdDobj();
                        CdDobj dobj = new CdDobj();
                        dobj.setState_cd(sessionVariables.getStateCodeSelected());
                        dobj.setOff_cd(sessionVariables.getOffCodeSelected());
                        owner_bean.getOwnerDobj().setCdDobj(dobj);
                    }
                    owner_bean.setCdDobjClone(cdDobjPrev);
                }

                if (owner_bean.getOwnerDobj() != null && owner_bean.getOwnerDobj().getVh_class() > 0) {
                    String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
                    for (int i = 0; i < data.length; i++) {
                        if (data[i][0].trim().equals(String.valueOf(owner_bean.getOwnerDobj().getVh_class()))) {
                            if (data[i][2].equals(String.valueOf(TableConstants.VM_VEHTYPE_TRANSPORT))) {
                                owner_bean.setVehType(String.valueOf(TableConstants.VM_VEHTYPE_TRANSPORT));
                                break;
                            } else {
                                owner_bean.setVehType(String.valueOf(TableConstants.VM_VEHTYPE_NON_TRANSPORT));
                                break;
                            }
                        }
                    }
                    owner_bean.fillComboBoxesForApplication();
                }

                // show inspection Details for KL              
                if ("KL,KA".contains(Util.getUserStateCode()) && sessionVariables.getSelectedWork() != null && owner_bean.getVch_purchase_as() != null && "B".equalsIgnoreCase(owner_bean.getVch_purchase_as())
                        && (sessionVariables.getSelectedWork().getAction_cd() == TableConstants.TM_NEW_RC_VERIFICATION
                        || sessionVariables.getSelectedWork().getAction_cd() == TableConstants.TM_NEW_RC_APPROVAL)) {
                    FitnessImpl fitnessImpl = new FitnessImpl();
                    InspectionDobj inspDobj = fitnessImpl.getVaInspectionDobj(APPL_NO);
                    if (inspDobj != null) {
                        inspDobj.setDisableInsDetail(true);
                        owner_bean.getOwnerDobj().setInspectionDobj(inspDobj);
                        owner_bean.setInspectionDobjToBean(inspDobj);
                        owner_bean.setRenderInspectionPanelNT(true);
                    } else {
                        owner_bean.setRenderInspectionPanelNT(false);
                    }
                }
                //End

                //setting values of inspection for non transport vehicle
                if (sessionVariables.getSelectedWork() != null
                        && owner_bean.getVehType().equalsIgnoreCase(String.valueOf(TableConstants.VM_VEHTYPE_NON_TRANSPORT))
                        && (sessionVariables.getSelectedWork().getAction_cd() == TableConstants.TM_NEW_RC_FITNESS_INSPECTION
                        || sessionVariables.getSelectedWork().getAction_cd() == TableConstants.TM_NEW_RC_APPROVAL)) {
                    FitnessImpl fitnessImpl = new FitnessImpl();
                    InspectionDobj inspDobj = fitnessImpl.getVaInspectionDobj(APPL_NO);
                    if (owner_dobj != null) {
                        owner_bean.getOwnerDobj().setInspectionDobj(inspDobj);
                        owner_bean.setInspectionDobjToBean(inspDobj);
                    }
                }

                if (sessionVariables.getSelectedWork() != null && (TableConstants.VM_TRANSACTION_MAST_TEMP_REG == pur_cd || TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE == pur_cd)) {
                    FitnessImpl fitnessImpl = new FitnessImpl();
                    InspectionDobj inspDobj = fitnessImpl.getVaInspectionDobj(APPL_NO);
                    if (owner_dobj != null && inspDobj != null) {
                        owner_bean.getOwnerDobj().setInspectionDobj(inspDobj);
                        owner_bean.setInspectionDobjToBean(inspDobj);
                    }
                }

                if (owner_bean.getOwnerDobj() != null && owner_bean.getOwnerDobj().getVch_catg() != null) {
                    if (owner_bean.getOwnerDobj().getVch_catg().equals(TableConstants.TWO_WHEELER_NON_TRANSPORT) || owner_bean.getOwnerDobj().getVch_catg().equals(TableConstants.TWO_WHEELER_TRANSPORT)) {
                        owner_bean.getList_ac_audio_video_fitted().clear();
                        owner_bean.getList_ac_audio_video_fitted().add(new SelectItem("N", "NO"));
                    }
                }

                //for setting advacne registration no to the user before Approval
                if (TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE == pur_cd || TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE == pur_cd) {

                    if (tempRegnDetailsList != null && !tempRegnDetailsList.isEmpty()) {
                        for (int i = 0; i < tempRegnDetailsList.size(); i++) {
                            if (tempRegnDetailsList.get(i).getOwnerTempDobj() != null
                                    && tempRegnDetailsList.get(i).getOwnerTempDobj().getPurpose() != null
                                    && tempRegnDetailsList.get(i).getOwnerTempDobj().getPurpose().equalsIgnoreCase(TableConstants.STOCK_TRANSFER)) {
                                tempRegnDetailsList.remove(i);//Remove Record of Stock Transfer for Display
                            }
                        }
                    }

                    if (APPL_NO != null && !APPL_NO.equals("")) {
                        //String advanceRegnNo = NewImpl.getAdvanceRegnNo(APPL_NO);
                        AdvanceRegnNo_dobj advDobj = NewImpl.getAdvanceFeeDetailsMessage(APPL_NO);
                        if (advDobj != null) {
                            FANCY_REGN_NO = advDobj.getRegn_no();
                            fancyRcptNo = advDobj.getRecp_no();
                            String msg = "";
                            if (REGN_NO.equals("NEW")) {
                                msg = "Vehicle Registration No " + FANCY_REGN_NO + " will be allotted (as per booked Fancy/Advance Receipt: " + advDobj.getRecp_no()
                                        + " Amount: " + advDobj.getTotal_amt()
                                        + " )";
                            } else {
                                msg = "Vehicle Registration No " + FANCY_REGN_NO + " is allotted (as per booked Fancy/Advance Receipt: " + advDobj.getRecp_no()
                                        + " Amount: " + advDobj.getTotal_amt()
                                        + " )";
                            }
                            owner_bean.setRegnNoAlloted(msg);
                            owner_bean.setRegnNoAllotedPanel(true);
                            owner_bean.disableFancyNumberFields();
                        }
                    }
                }

                if (TableConstants.VM_TRANSACTION_MAST_TEMP_REG == pur_cd || TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE == pur_cd || TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE == pur_cd) {
                    String advanceRetenNo = NewImpl.getAdvanceRetenNo(APPL_NO);
                    if (advanceRetenNo != null) {
                        RETEN_REGN_NO = advanceRetenNo;
                    }
                }

                FacesContext.getCurrentInstance().getViewRoot().getViewMap().put("owner_bean", owner_bean);

            }
            if (owner_bean.getOwnerDobj() != null && owner_bean.getOwnerDobj().getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION)) {
                if (owner_bean.getOwnerDobj().getAuctionDobj() != null) {
                    this.setForAuctionVehicle(owner_bean.getOwnerDobj().getAuctionDobj(), owner_dobj.getPurchase_dt(), owner_bean, owner_dobj, false);
                }
            }
            ///////////////////////////// Filling Ex_Army Details /////////////////
            if (owner_bean.getOwnerDobj() != null && owner_bean.getOwnerDobj().getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_EXARMY)) {
                ExArmyDobj exArmy_dobj = ExArmyImpl.setExArmyDetails_db_to_dobj(APPL_NO, null, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                if (exArmy_dobj != null) {
                    exArmyBean.setDobj_To_Bean(exArmy_dobj);
                    owner_bean.setExArmyVehicle_Visibility_tab(true);
                    exArmyBean.setExArmy_dobj_prv(exArmy_dobj);
                    FacesContext.getCurrentInstance().getViewRoot().getViewMap().put("exArmyBean", exArmyBean);
                }
            }
            /////////////////////// Filling Imported Vehicle Details //////////////
            if (owner_bean.getImported_veh() != null && owner_bean.getImported_veh().trim().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_IMPORTED_YES)) {

                ImportedVehicleDobj imp_dobj = ImportedVehicleImpl.setImpVehDetails_db_to_dobj(APPL_NO, null, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                if (imp_dobj != null) {
                    imp_bean.setDobj_to_Bean(imp_dobj);
                    owner_bean.setImportedVehicle_Visibility_tab(true);
                    imp_bean.setImp_dobj_prv(imp_dobj);
                    FacesContext.getCurrentInstance().getViewRoot().getViewMap().put("importedVehicleBean", imp_bean);
                }
            }
            /////////////////////// Filling Axle Details //////////////
            if (Integer.parseInt(owner_bean.getVehType()) == TableConstants.VM_VEHTYPE_TRANSPORT) {

                AxleDetailsDobj axle_dobj = AxleImpl.setAxleVehDetails_db_to_dobj(APPL_NO, null, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                if (axle_dobj != null) {
                    axle_bean.setDobj_To_Bean(axle_dobj);
                    owner_bean.setAxleDetail_Visibility_tab(true);
                    axle_bean.setAxle_dobj_prv(axle_dobj);
                    FacesContext.getCurrentInstance().getViewRoot().getViewMap().put("axleBean", axle_bean);

                }
            }

            /////////////////////// Filling Retro Fitting Details //////////////    
            if (owner_bean.getOwnerDobj().getFuel() > 0) {
                int fuel_type = owner_bean.getOwnerDobj().getFuel();
                if (fuel_type == TableConstants.VM_FUEL_CNG_TYPE
                        || fuel_type == TableConstants.VM_FUEL_TYPE_PETROL_CNG
                        || fuel_type == TableConstants.VM_FUEL_TYPE_LPG
                        || fuel_type == TableConstants.VM_FUEL_TYPE_PETROL_LPG) {
                    RetroFittingDetailsDobj cng_dobj = RetroFittingDetailsImpl.setCngDetails_db_to_dobj(APPL_NO);
                    owner_bean.setCngDetails_Visibility_tab(true);
                    if (cng_dobj != null) {
                        cng_bean.setDobj_To_Bean(cng_dobj);
                        cng_bean.setCng_dobj_prv(cng_dobj);
                        FacesContext.getCurrentInstance().getViewRoot().getViewMap().put("retroFittingDetailsBean", cng_bean);
                        owner_bean.purchaseDateListener();
                    }
                }
            }

            ///////////////////////////// Filling Insurance Details ///////////////
            InsDobj ins_dobj = null;
            if (otherStateVehDobj != null && otherStateVehDobj.getOldRegnNo() != null) {
                InsuranceDetailService detailService = new InsuranceDetailService();
                if (owner_dobj != null) {
                    ins_dobj = detailService.getInsuranceDetailsByService(otherStateVehDobj.getOldRegnNo(), owner_dobj.getState_cd(), owner_dobj.getOff_cd());
                }
            } else {
                InsImpl ins_Impl = new InsImpl();
                ins_dobj = ins_Impl.set_ins_dtls_db_to_dobj(null, APPL_NO, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
            }
            if (ins_dobj != null) {
                ins_bean.set_Ins_dobj_to_bean(ins_dobj);
                ins_bean.setIns_dobj_prv(ins_dobj);//for holding current dobj for using in the comparison.
                if (ins_dobj.getIns_type() == Integer.parseInt(TableConstants.INS_TYPE_NA)) {
                    ins_bean.insTypeListener();
                }
                if (ins_dobj.isIibData()) {
                    ins_bean.componentReadOnly(false);
                }
                if (sessionVariables.getStateCodeSelected() != null && !sessionVariables.getStateCodeSelected().equals("") && sessionVariables.getStateCodeSelected().equals("OR")) {
                    ins_bean.setMin_dt(owner_bean.getOwnerDobj().getPurchase_dt());
                }
                FacesContext.getCurrentInstance().getViewRoot().getViewMap().put("ins_bean", ins_bean);
            }

            ///////////////////////////// Filling Hypothecation Details ///////////////
            HpaImpl hpa_Impl = new HpaImpl();
            HpaDobj hpa_dobj = hpa_Impl.set_HPA_appl_db_to_dobj(APPL_NO, REGN_NO, pur_cd, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
            hpa_bean.setMinDate(owner_bean.getOwnerDobj().getPurchase_dt());
            if (hpa_dobj != null) {
                hpa_bean.setHpaDobj(hpa_dobj);
                hpa_bean.setHpa_dobj_prv((HpaDobj) hpa_bean.getHpaDobj().clone());//for holding current dobj for using in the comparison.
                hpa_bean.StateFncrListener();
                hypothecated = true;//for displaying filled hypothecated information

            } else {
                flag = false;
            }
            FacesContext.getCurrentInstance().getViewRoot().getViewMap().put("hpa_bean", hpa_bean);

            ///////////////////////////// Filling Commercial Trailer Details ///////////////
            Trailer_Impl trailer_impl = new Trailer_Impl();
            Trailer_dobj trailer_dobj = trailer_impl.set_trailer_dtls_to_dobj(APPL_NO, REGN_NO, pur_cd);
            trailer_bean.set_trailer_dobj_to_bean(trailer_dobj);
            trailer_bean.setTrailer_dobj_prv(trailer_dobj);//for holding current dobj for using in the comparison.
            owner_bean.getOwnerDobj().setListTrailerDobj(trailer_impl.set_trailer_dtls_to_dobjList(APPL_NO, null, pur_cd));
            FacesContext.getCurrentInstance().getViewRoot().getViewMap().put("trailer_bean", trailer_bean);

            ///////////////////////////////////////////////////////////////////////////
            //disable all components start point
            if (pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                owner_bean.ownerComponentEditable(true);
                owner_bean.setDisableFName(false);
            } else {
                owner_bean.ownerComponentEditable(false);
            }

            if (pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                    || pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                if (hpa_bean != null) {
                    if ("OR".equalsIgnoreCase(sessionVariables.getStateCodeSelected())) {
                        hpa_bean.componentEditable(true);
                    } else {
                        hpa_bean.componentEditable(false);
                    }
                }
                if (trailer_bean != null) {
                    trailer_bean.componentEditable(true);
                }

            }

            //disable all components end point
            ///////////////////////////// Filling Referal Documents ///////////////
            REF_DOC_Impl ref_doc_impl = new REF_DOC_Impl();
            ArrayList<REF_DOC_PURPOSE_dobj> imagePurposeList = ref_doc_impl.transactionAllRequiredDocList(pur_cd, APPL_NO);
            ref_doc_impl = null; // Nullify Object , no longer required....

            if (imagePurposeList != null) {
                REF_DOC_bean ref_doc_bean = new REF_DOC_bean();
                ref_doc_bean.set_REF_DOC_dobj_to_bean(null, imagePurposeList);
                FacesContext.getCurrentInstance().getViewRoot().getViewMap().put("ref_doc_bean", ref_doc_bean);
            }
            ///////////////////////////////////////////////////////////////////////////
            ///////////////////////////////////////////////////////////////////////////
            includeSrcURL = "defaultworkbench.xhtml";
            sub_panel = true;
            owner_bean.setRenderInspectionPanel(false);
            String user_catg = sessionVariables.getUserCatgForLoggedInUser();

            if (user_catg == null || user_catg.isEmpty()) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }

            switch (pur_cd) {

                case TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE:
                    sub_panel = false;
                    if ((TableConstants.TM_ROLE_ENTRY == ROLE_CD || TableConstants.TM_ROLE_VERIFICATION == ROLE_CD
                            || TableConstants.TM_ROLE_APPROVAL == ROLE_CD || ACTION_CDOE == TableConstants.TM_ROLE_NEW_APPL)
                            && owner_bean.getAppl_no() != null && !owner_bean.getAppl_no().equals("")
                            && !regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                        this.vehicleRegnSeriesMsg(owner_bean, owner_dobj, user_catg, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                    }

                    if (!user_catg.equals(TableConstants.User_Dealer)) {
                        owner_bean.setRenderModelEditable(true);
                        owner_bean.setModelEditable(false);
                        owner_bean.setRenderModelSelectMenu(true);
                    }
                    if (owner_bean.getOwnerDobj() != null && owner_bean.getOwnerDobj().getRegn_type() != null
                            && (owner_bean.getOwnerDobj().getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                            || owner_bean.getOwnerDobj().getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))) {
                        owner_bean.setDisableOwnerSr(false);
                        if (otherStateVehDobj != null) {
                            State curSt = MasterTableFiller.state.get(otherStateVehDobj.getOldStateCD());
                            if (curSt != null) {
                                officeList.clear();
                                officeList.addAll(curSt.getOffice());
                            }
                        }

                    }

                    if (owner_bean.getOwnerDobj() != null && owner_bean.getOwnerDobj().getRegn_type() != null
                            && owner_bean.getOwnerDobj().getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY)) {
                        this.fetchTemporaryRegisteredDetails(owner_dobj, owner_bean);
                    }

                    if (TableConstants.TM_ROLE_ENTRY == ROLE_CD || TableConstants.TM_ROLE_VERIFICATION == ROLE_CD
                            || TableConstants.TM_ROLE_APPROVAL == ROLE_CD) {

                        if (isHomologationData) {
                            this.rToLevelHomologationFields(owner_bean);
                        }
                        this.disableEnableFields(owner_bean, owner_dobj, ins_dobj, ins_bean, hpa_bean);
                        this.renderMakerModel(owner_bean, owner_dobj);
                        this.disableFuelType(owner_bean);
                        flag = true;
                        tabView = true;
                    }

                    //for inspection in case of New Registration
                    if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                            && (TableConstants.TM_ROLE_INSPECTION == ROLE_CD || TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL == ACTION_CDOE)) {

                        if (isHomologationData) {
                            this.rToLevelHomologationFields(owner_bean);
                        }
                        this.renderMakerModel(owner_bean, owner_dobj);
                        this.fitnessCode(owner_bean, owner_dobj, hpa_dobj, owner_bean.getOwnerDobj().getRegn_type(), user_catg, isHomologationData);
                        this.disableFuelType(owner_bean);
                        tabView = true;
                        //for handling old data for temp.registration
                        if ("OR".equalsIgnoreCase(sessionVariables.getStateCodeSelected())) {
                            owner_bean.setBlnDisableRegnTypeTemp(false);
                        }
                    }

                    if (ROLE_CD == TableConstants.TM_ROLE_SCRUTINY_E_PAY) {
                        sub_panel = true;
                        EpayBean epayBean = new EpayBean();
                        epayBean.setBeanListFromDobj(EpayImpl.getPurCD_E_payments(APPL_NO, REGN_NO, pur_cd));
                        long eTot = 0;
                        long actTot = 0;
                        for (int i = 0; i < epayBean.getList().size(); i++) {
                            EpayBean objBean = epayBean.getList().get(i);
                            eTot = eTot + objBean.getE_total();
                            actTot = actTot + objBean.getAct_total();
                        }
                        epayBean.setE_total(eTot);
                        epayBean.setAct_total(actTot);
                        if (eTot != actTot) {
                            epayBean.setApplicantInformed(true);
                        }
                        FacesContext.getCurrentInstance().getViewRoot().getViewMap().put("ePay", epayBean);
                        includeSrcURL = "/ui/form_check_e_amount.xhtml";
                    }

                    if (ACTION_CDOE == TableConstants.TM_ROLE_NEW_APPL && owner_bean.getAppl_no() == null) {
                        if (isHomologationData) {
                            this.rToLevelHomologationFields(owner_bean);
                        }

                        owner_bean.ownerComponentEditable(true);
                        owner_bean.setDisableFName(false);
                        ins_bean.componentEditable(true);
                        hpa_bean.componentEditable(true);
                        if (regnType != null && !regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                                && !regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                            owner_bean.setDisableChasiNo(true);
                        }
                        owner_bean.getOwnerDobj().setOwner_sr(1);
                        this.trailer_tab = false;
                        flag = true;
                    }

                    if (owner_bean.getOwnerDobj() != null
                            && (owner_bean.getOwnerDobj().getRegn_type().equals(TableConstants.VM_REGN_TYPE_SCRAPPED) || (owner_bean.getOwnerDobj().getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY)))) {
                        ScrappedVehicleImpl scrapImpl = new ScrappedVehicleImpl();
                        ScrappedVehicleDobj scrapDobj = scrapImpl.getScrappedInformationApplNo(APPL_NO);
                        if (scrapDobj != null) {
                            owner_bean.getOwnerDobj().setScrappedVehicleDobj(scrapDobj);
                            owner_bean.setDisableOwnerName(true);
                            owner_bean.setDisableFName(true);
                            owner_bean.setDisableOwnerCd(true);
                            blnScrappedVehicle = true;
                        }
                    }

                    if (owner_bean.getOwnerDobj() != null
                            && owner_bean.getOwnerDobj().getSpeedGovernorDobj() != null) {
                        owner_bean.setRenderSpeedGov(true);
                    }

                    if (owner_bean.getOwnerDobj() != null
                            && owner_bean.getOwnerDobj().getReflectiveTapeDobj() != null) {
                        owner_bean.setRenderReflectiveTape(true);
                    }

                    if (ACTION_CDOE == TableConstants.TM_ROLE_NEW_APPL && owner_bean.getAppl_no() != null && !owner_bean.getAppl_no().equals("")) {
                        if (isHomologationData) {
                            this.rToLevelHomologationFields(owner_bean);
                        }
                        this.disableEnableFields(owner_bean, owner_dobj, ins_dobj, ins_bean, hpa_bean);
                        if (!owner_bean.isRenderVehicleDtlsPartialBtn()) {
                            this.renderMakerModel(owner_bean, owner_dobj);
                        }
                        owner_bean.setRenderInspectionPanel(false);
                        flag = true;
                        tabView = true;
                    }

                    ////////////////////////////////////////////////////////////////////////
                    break;

                case TableConstants.VM_TRANSACTION_MAST_TEMP_REG:

                    sub_panel = false;
                    setForTempRegnType();
                    Owner_temp_dobj temp_dobj = null;
                    if (owner_dobj != null) {
                        temp_dobj = owner_dobj.getDob_temp();
                        if (temp_dobj != null) {
                            try {
                                owner_bean.setTemp_dobj_prev((Owner_temp_dobj) temp_dobj.clone());
                            } catch (CloneNotSupportedException ex) {
                                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                            }
                        }

                    } else {
                        temp_dobj = new Owner_temp_dobj();
                    }

                    owner_bean.setTemp_dobj(temp_dobj);
                    owner_bean.setForTempRegnType();

                    if (!user_catg.equals(TableConstants.User_Dealer)) {
                        owner_bean.setRenderModelEditable(true);
                        owner_bean.setModelEditable(false);
                        owner_bean.setRenderModelSelectMenu(true);
                    }

                    if (owner_bean.getOwnerDobj() != null
                            && owner_bean.getOwnerDobj().getSpeedGovernorDobj() != null) {
                        owner_bean.setRenderSpeedGov(true);
                    }

                    if (owner_bean.getOwnerDobj() != null
                            && owner_bean.getOwnerDobj().getReflectiveTapeDobj() != null) {
                        owner_bean.setRenderReflectiveTape(true);
                    }

                    if (TableConstants.TM_ROLE_ENTRY == ROLE_CD || TableConstants.TM_ROLE_VERIFICATION == ROLE_CD
                            || TableConstants.TM_ROLE_APPROVAL == ROLE_CD || TableConstants.TM_TMP_RC_FITNESS_INSPECTION == ACTION_CDOE) {

                        if (isHomologationData) {
                            this.rToLevelHomologationFields(owner_bean);
                        }
                        this.renderMakerModel(owner_bean, owner_dobj);
                        owner_bean.setDisableDealerCd(true);
                        this.disableEnableFields(owner_bean, owner_dobj, ins_dobj, ins_bean, hpa_bean);
                        flag = true;
                        tabView = true;
                    }

                    if (TableConstants.TM_TMP_RC_FITNESS_INSPECTION == ACTION_CDOE) {

                        if (isHomologationData) {
                            this.rToLevelHomologationFields(owner_bean);
                        }
                        owner_bean.ownerComponentEditable(false);
                        if (owner_dobj != null) {
                            if (owner_dobj.getOwner_cd() != TableConstants.OWNER_CODE_INDIVIDUAL) {
                                owner_bean.setDisableFName(true);
                            } else {
                                owner_bean.setDisableFName(false);
                            }
                        } else {
                            owner_bean.setDisableFName(false);
                        }
                        //this.fitnessCode(owner_bean, owner_dobj, hpa_dobj, owner_bean.getOwnerDobj().getRegn_type(), user_catg, isHomologationData);
                    }

                    if (ROLE_CD == TableConstants.TM_ROLE_SCRUTINY_E_PAY) {
                        sub_panel = true;
                        EpayBean epayBean = new EpayBean();
                        epayBean.setBeanListFromDobj(EpayImpl.getPurCD_E_payments(APPL_NO, REGN_NO, pur_cd));
                        long eTot = 0;
                        long actTot = 0;
                        for (int i = 0; i < epayBean.getList().size(); i++) {
                            EpayBean objBean = epayBean.getList().get(i);
                            eTot = eTot + objBean.getE_total();
                            actTot = actTot + objBean.getAct_total();
                        }
                        epayBean.setE_total(eTot);
                        epayBean.setAct_total(actTot);
                        if (eTot != actTot) {
                            epayBean.setApplicantInformed(true);
                        }
                        FacesContext.getCurrentInstance().getViewRoot().getViewMap().put("ePay", epayBean);
                        includeSrcURL = "/ui/form_check_e_amount.xhtml";
                    }

                    if (ACTION_CDOE == TableConstants.TM_ROLE_NEW_APPL_TEMP && owner_bean.getAppl_no() == null) {
                        if (isHomologationData) {
                            this.rToLevelHomologationFields(owner_bean);
                        }
                        this.disableEnableFieldsForNewAndTemp(owner_bean, ins_bean, hpa_bean);
                        this.trailer_tab = false;
                        flag = true;
                    }
                    if (ACTION_CDOE == TableConstants.TM_ROLE_NEW_APPL_TEMP && owner_bean.getAppl_no() != null && !owner_bean.getAppl_no().equals("")) {
                        if (isHomologationData) {
                            this.rToLevelHomologationFields(owner_bean);
                        }
                        this.disableEnableFields(owner_bean, owner_dobj, ins_dobj, ins_bean, hpa_bean);
//                        owner_bean.setDisableDealerCd(true);
                        flag = true;
                        tabView = true;
                    }

                    ////////////////////////////////////////////////////////////////////////
                    break;

                case 49:
                    break;

                case TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE:
                    sub_panel = false;

                    if (ACTION_CDOE == TableConstants.TM_ROLE_DEALER_NEW_APPL && owner_bean.getAppl_no() == null) {
                        this.disableEnableFieldsForNewAndTemp(owner_bean, ins_bean, hpa_bean);
                        this.trailer_tab = false;
                        flag = true;
                    } else if (ACTION_CDOE == TableConstants.TM_ROLE_DEALER_VERIFICATION || ACTION_CDOE == TableConstants.TM_NEW_RC_VERIFICATION || ACTION_CDOE == TableConstants.TM_NEW_RC_APPROVAL || ACTION_CDOE == TableConstants.TM_ROLE_DEALER_APPROVAL || ACTION_CDOE == TableConstants.TM_ROLE_DEALER_NEW_APPL && owner_bean.getAppl_no() != null && !owner_bean.getAppl_no().equals("") && owner_dobj != null) {
                        flag = true;
                        this.dealerFields(owner_bean);
                        if (tmConf.getTmConfigOtpDobj().isOwner_mobile_verify_with_otp()) {
                            owner_bean.getOwner_identification().setMobileNoEditable(true);
                        }
                        this.vehicleRegnSeriesMsg(owner_bean, owner_dobj, user_catg, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                        if (tmConf != null && tmConf.isTempFeeInNewRegis() && owner_dobj != null && owner_dobj.getAppl_no() != null) {
                            checkForTabView(owner_dobj.getAppl_no());
                        } else {
                            tabView = true;
                        }
                        if (owner_bean.getOwnerDobj() != null && owner_bean.getOwnerDobj().getRegn_type() != null
                                && owner_bean.getOwnerDobj().getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY)) {
                            this.fetchTemporaryRegisteredDetails(owner_dobj, owner_bean);
                        }
                        if (tmConf != null && owner_dobj != null && Util.getSelectedSeat() != null && tmConf.isConsiderFMSDealer() && owner_dobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_NEW) && (ACTION_CDOE == TableConstants.TM_NEW_RC_VERIFICATION || ACTION_CDOE == TableConstants.TM_NEW_RC_APPROVAL)) {
                            boolean isValidForFms = ServerUtil.checkApplRegnDateForFms(APPL_NO, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), owner_dobj.getDealer_cd());
                            if (!isValidForFms) {
                                List fmsVerify = ServerUtil.fMSVerification(APPL_NO, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), owner_dobj.getDealer_cd(), owner_dobj.getNorms());
                                if (fmsVerify.isEmpty()) {
                                    throw new VahanException("File is not submitted by dealer yet.So you can't Verify/Approve this file.");
                                }
                            }
                        }
                        if (owner_dobj != null) {
                            String[] dealerDetails = ServerUtil.checkDealerAuthForAllOff(owner_dobj.getState_cd(), owner_dobj.getDealer_cd());
                            if (dealerDetails != null && Boolean.parseBoolean(dealerDetails[3])) {
                                owner_bean.getList_dealer_cd().add(new SelectItem(dealerDetails[0], dealerDetails[1]));
                            }

                        }
                    } else if (ACTION_CDOE == TableConstants.TM_NEW_RC_FITNESS_INSPECTION || TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL == ACTION_CDOE) {
                        if (owner_bean.getList_maker_model().isEmpty()) {
                            owner_bean.setModelEditable(true);
                            owner_bean.setRenderModelSelectMenu(false);
                        }
                        this.dealerFields(owner_bean);
                        owner_bean.ownerComponentEditable(false);
                        if (owner_dobj != null) {
                            if (owner_dobj.getOwner_cd() != TableConstants.OWNER_CODE_INDIVIDUAL) {
                                owner_bean.setDisableFName(true);
                            } else {
                                owner_bean.setDisableFName(false);
                            }
                        } else {
                            owner_bean.setDisableFName(false);
                        }

                        if (tmConf != null && tmConf.isTempFeeInNewRegis() && owner_dobj.getAppl_no() != null) {
                            checkForTabView(owner_dobj.getAppl_no());
                        } else {
                            tabView = true;
                        }
                        this.fitnessCode(owner_bean, owner_dobj, hpa_dobj, owner_bean.getOwnerDobj().getRegn_type(), user_catg, isHomologationData);

                        if (tmConf != null && owner_dobj != null && Util.getSelectedSeat() != null && tmConf.isConsiderFMSDealer() && owner_dobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_NEW)) {
                            List fmsVerify = ServerUtil.fMSVerification(APPL_NO, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), owner_dobj.getDealer_cd(), owner_dobj.getNorms());
                            if (fmsVerify.isEmpty()) {
                                throw new VahanException("File is not submitted by dealer yet.So you can't Verify/Approve this file.");
                            }
                        }

                        if (owner_dobj != null) {
                            String[] dealerDetails = ServerUtil.checkDealerAuthForAllOff(owner_dobj.getState_cd(), owner_dobj.getDealer_cd());
                            if (dealerDetails != null && Boolean.parseBoolean(dealerDetails[3])) {
                                owner_bean.getList_dealer_cd().add(new SelectItem(dealerDetails[0], dealerDetails[1]));
                            }
                        }
                    }
                    if (!CommonUtils.isNullOrBlank(APPL_NO)) {
                        this.getBankDetails(owner_bean.getOwnerDobj().getVh_class(), APPL_NO, owner_bean.getOwnerDobj().getOwner_cd());
                    }
                    break;

                case TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE:
                    sub_panel = false;
                    setForTempRegnType();
                    Owner_temp_dobj dealer_temp_dobj = null;
                    if (owner_dobj != null) {
                        dealer_temp_dobj = owner_dobj.getDob_temp();
                        if (dealer_temp_dobj != null) {
                            try {
                                owner_bean.setTemp_dobj_prev((Owner_temp_dobj) dealer_temp_dobj.clone());
                            } catch (CloneNotSupportedException ex) {
                                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                            }
                        }

                    } else {
                        dealer_temp_dobj = new Owner_temp_dobj();
                    }

                    owner_bean.setTemp_dobj(dealer_temp_dobj);
                    owner_bean.setForTempRegnType();

                    if (ACTION_CDOE == TableConstants.TM_ROLE_DEALER_TEMP_APPL && owner_bean.getAppl_no() == null) {
                        this.disableEnableFieldsForNewAndTemp(owner_bean, ins_bean, hpa_bean);
                        this.trailer_tab = false;
                        flag = true;
                    } else if (ACTION_CDOE == TableConstants.TM_ROLE_DEALER_TEMP_VERIFICATION || ACTION_CDOE == TableConstants.TM_TMP_ROLE_ENTRY || ACTION_CDOE == TableConstants.TM_TMP_RC_VERIFICATION || ACTION_CDOE == TableConstants.TM_TMP_RC_APPROVAL || ACTION_CDOE == TableConstants.TM_ROLE_DEALER_TEMP_APPROVAL || ACTION_CDOE == TableConstants.TM_ROLE_DEALER_TEMP_APPL && owner_bean.getAppl_no() != null && !owner_bean.getAppl_no().equals("")) {
                        tabView = true;
                        flag = true;
                        this.dealerFields(owner_bean);
                    } else if (TableConstants.TM_TMP_RC_FITNESS_INSPECTION == ACTION_CDOE) {
                        tabView = true;
                        this.dealerFields(owner_bean);
                        owner_bean.ownerComponentEditable(false);
                        if (owner_dobj != null) {
                            if (owner_dobj.getOwner_cd() != TableConstants.OWNER_CODE_INDIVIDUAL) {
                                owner_bean.setDisableFName(true);
                            } else {
                                owner_bean.setDisableFName(false);
                            }
                        } else {
                            owner_bean.setDisableFName(false);
                        }
                    }
                    if (owner_dobj != null) {
                        String[] dealerDetails = ServerUtil.checkDealerAuthForAllOff(owner_dobj.getState_cd(), owner_dobj.getDealer_cd());
                        if (dealerDetails != null && Boolean.parseBoolean(dealerDetails[3])) {
                            owner_bean.getList_dealer_cd().add(new SelectItem(dealerDetails[0], dealerDetails[1]));
                        }
                    }
                    break;
            }

            if (owner_dobj != null && owner_dobj.getVh_class() != 0) {
                if (ServerUtil.isTransport(owner_dobj.getVh_class(), null)) {
                    owner_bean.setDisableLdWtCap(false);
                    owner_bean.setDisableUnLdWtCap(false);
                } else {
                    if (isHomologationData) {
                        if (user_catg != null && user_catg.trim().equals(TableConstants.User_Dealer)) {
                            owner_bean.setDisableLdWtCap(true);
                            owner_bean.setDisableUnLdWtCap(true);
                        } else {
                            if (owner_dobj.getVch_purchase_as() != null && !owner_dobj.getVch_purchase_as().equals(TableConstants.PURCHASE_AS_CHASIS)) {
                                owner_bean.setDisableLdWtCap(true);
                                owner_bean.setDisableUnLdWtCap(true);
                            } else {
                                owner_bean.setDisableLdWtCap(false);
                                owner_bean.setDisableUnLdWtCap(false);
                            }
                        }
                    }
                }
            }

            if (pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE && "DL".equals(sessionVariables.getStateCodeSelected()) && owner_bean.getOwnerDobj() != null && owner_bean.getOwnerDobj().getVh_class() == TableConstants.ERICKSHAW_VCH_CLASS && owner_bean.getOwnerDobj().getOwner_cd() == TableConstants.OWNER_CODE_INDIVIDUAL) {
                owner_bean.setVehicleComponentEditable(true);
                owner_bean.setDisableOwnerName(true);
                owner_bean.setDisableFName(true);
                owner_bean.setBlnAdvancedRegNo(true);
                if (owner_dobj != null && owner_dobj.getOwner_identity() != null && !CommonUtils.isNullOrBlank(owner_dobj.getOwner_identity().getDl_no())) {
                    owner_bean.getOwner_identification().setDlDisabled(true);
                } else {
                    owner_bean.getOwner_identification().setDlDisabled(false);
                }
                owner_bean.getOwner_identification().setMobileNoEditable(true);
            }
            if (tmConf != null && tmConf.getTmConfigOtpDobj() != null && tmConf.getTmConfigOtpDobj().isOwner_mobile_verify_with_otp()) {
                if (owner_bean.getOwner_identification().getMobile_no() != 0) {
                    owner_bean.getOwner_identification().setMobileNoEditable(true);
                } else {
                    owner_bean.getOwner_identification().setMobileNoEditable(false);
                }
            }
            if (owner_dobj != null) {
                if (owner_dobj.getManu_mon() <= 0) {
                    owner_bean.setDisableManuMonth(false);
                }
                if (owner_dobj.getManu_yr() <= 0) {
                    owner_bean.setDisableManuYr(false);
                }
                if (owner_dobj.getNorms() <= 0) {
                    owner_bean.setDisableNorms(false);
                }
                if (CommonUtils.isNullOrBlank(owner_dobj.getVch_purchase_as()) || owner_dobj.getVch_purchase_as().equals("-1")) {
                    owner_bean.setDisableVchPurchaseAs(false);
                }
                owner_bean.setSaleAmtInWords("Rs. " + new Utility().ConvertNumberToWords(owner_dobj.getSale_amt()).toLowerCase());
            }
            VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(owner_dobj);
            if (vehParameters != null && getOtherStateVehDobj() != null && getOtherStateVehDobj().getOldOffCD() != null) {
                vehParameters.setPREV_OFF_CD(getOtherStateVehDobj().getOldOffCD());
                vehParameters.setLOGIN_OFF_CD(Util.getSelectedSeat().getOff_cd());
            }
            if (Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                    || Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                if (sessionVariables.getActionCodeSelected() == TableConstants.TM_NEW_RC_VERIFICATION) {
                    if (regnType != null && !regnType.isEmpty() && (regnType.equals(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE) && (isCondition(replaceTagValues(Util.getTmConfiguration().getOther_rto_number_change(), vehParameters), "Fancy No Assignment Other/RTO")))) {
                        owner_bean.setBlnPgAdvancedRegNo(true);
                    }
                }
            }

            if (ACTION_CDOE == TableConstants.TM_TMP_RC_APPROVAL || ACTION_CDOE == TableConstants.TM_ROLE_DEALER_TEMP_APPROVAL
                    || ACTION_CDOE == TableConstants.TM_TMP_RC_VERIFICATION || ACTION_CDOE == TableConstants.TM_NEW_RC_VERIFICATION || ACTION_CDOE == TableConstants.TM_NEW_RC_FITNESS_INSPECTION) {
                setRenderCheckFeeTaxButton(true);

            }
            if (ACTION_CDOE == TableConstants.TM_NEW_RC_APPROVAL || ACTION_CDOE == TableConstants.TM_ROLE_DEALER_APPROVAL) {
                renderCheckFeeTaxTab = true;
                renderAppDisapp = false;
                autoRunCheckFeeTax = true;
            }
            // set minimum from date for insurance/Retro/CNG
            owner_bean.purchaseDateListener();
        } catch (VahanException vex) {
            vahanMessages = vex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            System.out.print("Workbench StackTrace: ");
            e.printStackTrace();
            LOGGER.error("Appl No:" + APPL_NO + ",Regn Type:" + regnType + ": " + e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        } catch (Exception e) {
            LOGGER.error("Appl No:" + APPL_NO + ",Regn Type:" + regnType + ": " + e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public String approveActionPerformed() {
        String newStatus;
        try {
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getOffCodeSelected() == 0 || Util.getTmConfiguration() == null) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }

            SessionVariablesModel sessionVariablesModel = new SessionVariablesModel(sessionVariables);
            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();

            ComparisonBeanModel comparisonBeanModel = new ComparisonBeanModel();

            Status_dobj statusDobj = new Status_dobj();
            WorkBenchModel workBenchModel = new WorkBenchModel();
            workBenchModel.setACTION_CDOE(ACTION_CDOE);
            workBenchModel.setCOUNTER_ID(COUNTER_ID);
            workBenchModel.setROLE_CD(ROLE_CD);
            workBenchModel.setPUR_CD(PUR_CD);
            workBenchModel.setREGN_NO(REGN_NO);
            workBenchModel.setCheckFeeTax(checkFeeTax);
            workBenchModel.setHypothecated(hypothecated);

            statusDobj.setAppl_dt(APPL_DT);
            statusDobj.setAppl_no(APPL_NO);
            statusDobj.setEmp_cd(Long.parseLong(sessionVariables.getEmpCodeLoggedIn()));
            statusDobj.setOff_cd(sessionVariables.getOffCodeSelected());
            statusDobj.setState_cd(sessionVariables.getStateCodeSelected());
            statusDobj.setOffice_remark(approveImpl.getOffice_remark().getValue() == null ? " " : approveImpl.getOffice_remark().getValue().toString());
            statusDobj.setPublic_remark(approveImpl.getPublic_remark().getValue() == null ? " " : approveImpl.getPublic_remark().getValue().toString());
            if (approveImpl.getNewStatus().getValue() == null) {
                throw new VahanException("Please Select File Movement Type from Available Options for File Movement.");
            }
            newStatus = approveImpl.getNewStatus().getValue().toString();//for selectng radiobutton            
            statusDobj.setStatus(newStatus);
            int purcd = Integer.parseInt(PUR_CD);
            statusDobj.setPur_cd(purcd);

            HpaBean hpa_bean = null;
            HpaDobj hpa_dobj = null;
            InsBean ins_bean = null;
            InsDobj ins_dobj = null;
            Trailer_bean trailer_bean = null;
            Trailer_dobj trailer_dobj = null;
            ExArmyBean exArmyBean = null;
            ExArmyDobj exArmyDobj = null;
            ImportedVehicleBean imp_bean = null;
            ImportedVehicleDobj imp_dobj = null;
            AxleBean axle_bean = null;
            AxleDetailsDobj axle_dobj = null;
            RetroFittingDetailsBean cng_bean = null;
            RetroFittingDetailsDobj cng_dobj = null;
            String hypothecationStatus = null;
            OwnerBean owner_bean = null;
            FitnessBean fitness_bean = null;
            Owner_dobj owner_dobj = null;
            String changedData = null;
            String generatedRegnNo = null;
            List<ComparisonBean> ins_chage_List = null;
            List<ComparisonBean> compBeanList = null;

            WrapperModel wrapperModel = new WrapperModel();
            wrapperModel.setSessionVariables(sessionVariablesModel);
            wrapperModel.setTmConfigDobj(tmConfigDobj);
            wrapperModel.setStatusDobj(statusDobj);
            wrapperModel.setWorkBench(workBenchModel);
            wrapperModel.setComparisonBean(comparisonBeanModel);

            hpa_bean = (HpaBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("hpa_bean");
            wrapperModel.setHpaBeanModel(new HpaBeanModel(hpa_bean));

            ins_bean = (InsBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("ins_bean");
            wrapperModel.setIns_bean(new InsBeanModel(ins_bean));

            trailer_bean = (Trailer_bean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("trailer_bean");
            wrapperModel.setTrailerBeanModel(new TrailerBeanModel(trailer_bean));

            owner_bean = (OwnerBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("owner_bean");
            OwnerBeanModel ownerBeanModel = new OwnerBeanModel();
            ownerBeanModel.setOwnerDobj(owner_bean.set_Owner_appl_bean_to_dobj());
            ownerBeanModel.setOwner_identification(owner_bean.getOwner_identification());
            ownerBeanModel.setHomoDobj(owner_bean.getHomoDobj());
            ownerBeanModel.setTemp_dobj(owner_bean.getTemp_dobj());
            ownerBeanModel.setTempReg(owner_bean.getTempReg());
            ownerBeanModel.setRenderInspectionPanel(owner_bean.isRenderInspectionPanel());
            wrapperModel.setOwnerBean(ownerBeanModel);

            exArmyBean = (ExArmyBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("exArmyBean");
            wrapperModel.setExArmyBean(new ExArmyBeanModel(exArmyBean));

            imp_bean = (ImportedVehicleBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("importedVehicleBean");
            wrapperModel.setImportedVehicleBean(new ImportedVehicleBeanModel(imp_bean));

            axle_bean = (AxleBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("axleBean");
            wrapperModel.setAxleBean(new AxleBeanModel(axle_bean));

            cng_bean = (RetroFittingDetailsBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("retroFittingDetailsBean");
            wrapperModel.setCngDetailsBean(new RetroFittingDetailsBeanModel(cng_bean));

            EpayBean epay = (EpayBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("ePay");
            wrapperModel.setEpayBean(epay);

            fitness_bean = (FitnessBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("fitness_bean");
            FitnessBeanModel fitnessBeanModel = new FitnessBeanModel(fitness_bean);
            if (ROLE_CD == TableConstants.TM_ROLE_INSPECTION || TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL == ACTION_CDOE) { //this is used for inspection
                if (owner_bean.isRenderInspectionPanel())//; getInspection_panel().isRendered())
                {
                    Object[] fitnessArray = fitness_bean.makeDobjFromBean();
                    fitnessBeanModel.setFitnessDobj((FitnessDobj) fitnessArray[0]);
                    fitnessBeanModel.setAllYesNoList((ArrayList) fitnessArray[1]);
//                    fitness_dobj =  fitness_bean.makeDobjFromBean()[0];
//                    fitCheckList = (ArrayList) fitness_bean.makeDobjFromBean()[1];
//                    owner_dobj.setFit_dt(fitness_bean.getFitnessDobj().getFit_valid_to());
                }
            }

//            fitnessBeanModel.setFitnessDobj(fitness_bean.getFitnessDobj());
            // fitnessBeanModel.setFitness_dobj_prv(fitness_bean.getFitness_dobj_prv());
            wrapperModel.setFitnessBeanModel(fitnessBeanModel);
            wrapperModel.setDocumentUploadBeanModel(new DocumentUploadBeanModel(documentUpload_bean));
//            
            compBeanList = owner_bean.compareChagnes();
            changedData = ComparisonBeanImpl.changedDataContents(compBeanList);
            comparisonBeanModel.setChanged_data(changedData);

            String approveImplPrevAction = statusDobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_REVERT) ? approveImpl.getPrevAction().getValue().toString() : "";
            String baseUrl = VahanUtil.getBaseUrl();
            final String ROOT_URI_approveActionPerformed = baseUrl + "/newregn/approveActionPerformed?clientIpAddress=" + Util.getClientIpAdress()
                    + "&selectedRoleCode=" + (String) Util.getSession().getAttribute("selected_role_cd")
                    + "&otherStateVehOldOffCd=" + (otherStateVehDobj != null ? (otherStateVehDobj.getOldOffCD() != null ? otherStateVehDobj.getOldOffCD() : Integer.valueOf(0)) : Integer.valueOf(0))
                    + "&approveImplPrevAction=" + approveImplPrevAction;

            wrapperModel = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build()
                    .post()
                    .uri(ROOT_URI_approveActionPerformed)
                    .body(Mono.just(wrapperModel), WrapperModel.class)
                    .retrieve()
                    .bodyToMono(WrapperModel.class)
                    .block();
            if (wrapperModel == null) {
                throw new VahanException("Something went wrong when trying to fetch data from REST API");
            }

            // Check for messages in FacesContext or RequestContext.                // Retirn if required.
            ContextMessageModel context = wrapperModel.getContextMessageModel();
            if (context != null) {                
                if (context.getMessageContext().equals(ContextMessageModel.MessageContext.REQUEST) && context.isIsReturn()) {                  
                    PrimeFaces.current().ajax().update(context.getMessage1());
                    throw new VahanException(context.getMessage2());
                }
            }
            newStatus = wrapperModel.getStatusDobj().getStatus();
            purcd = wrapperModel.getStatusDobj().getPur_cd();
            hypothecationStatus = wrapperModel.getHypothecationStatus();

            OwnerBeanModel ownerBean = wrapperModel.getOwnerBean();
            owner_dobj = ownerBean.getOwnerDobj();

            FitnessBeanModel fitnessBeanModel1 = wrapperModel.getFitnessBeanModel();
            fitness_bean.setFitnessDobj(fitnessBeanModel1.getFitnessDobj());

            documentUpload_bean.setEmailMessage(wrapperModel.getDocumentUploadBeanModel().getEmailMessage());
            // NJeed to test/verify this
            documentUpload_bean.setRenderedMailButton(wrapperModel.getDocumentUploadBeanModel().isRenderedMailButton());

            if (purcd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || purcd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || purcd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || purcd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {

                if (newStatus.equals(TableConstants.STATUS_COMPLETE)
                        || newStatus.equals(TableConstants.STATUS_REVERT)
                        || newStatus.equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    compBeanList = owner_bean.compareChagnes();
                    //for storing the changed data into vha_changed_data
                    changedData = ComparisonBeanImpl.changedDataContents(compBeanList);
                    if (hypothecationStatus != null && !hypothecationStatus.equals("")) {
                        changedData = changedData + hypothecationStatus;
                    }
                    if (newStatus.equals(TableConstants.STATUS_COMPLETE)) {
                        // FASTag 
                        VehicleParameters vehParameter = FormulaUtils.fillVehicleParametersFromDobj(owner_dobj);
                        TmConfigurationDobj tmConfDobj = Util.getTmConfiguration();
                        if (tmConfDobj != null && tmConfDobj.getTmConfigurationFasTag() != null && isCondition(replaceTagValues(tmConfDobj.getTmConfigurationFasTag().getFasTagCondition(), vehParameter), "getFasTagCondition") && !isFasTagInstalled) {
                            if (tmConfDobj.getTmConfigurationFasTag().isFasTagMandatory()) {
                                renderFasTagDialog = true;
                                PrimeFaces.current().ajax().update("fasTag_panel");
                                PrimeFaces.current().executeScript("PF('fastag_dialog_var').show()");
                                return "";
                            }
                        }
                    }
                }
            }

            if (purcd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || purcd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {

                if (newStatus.equals(TableConstants.STATUS_COMPLETE)
                        || newStatus.equals(TableConstants.STATUS_REVERT)
                        || newStatus.equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    /*
                     FitnessDobj fitness_dobj = null;
                     ArrayList fitCheckList = null;
                     fitness_dobj = fitnessBeanModel1.getFitnessDobj();

                     if (ROLE_CD == TableConstants.TM_ROLE_INSPECTION || TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL == ACTION_CDOE) { //this is used for inspection
                     if (owner_bean.isRenderInspectionPanel())//; getInspection_panel().isRendered())
                     {
                     fitness_dobj = (FitnessDobj) fitness_bean.makeDobjFromBean()[0];
                     fitCheckList = (ArrayList) fitness_bean.makeDobjFromBean()[1];
                     owner_dobj.setFit_dt(fitness_bean.getFitnessDobj().getFit_valid_to());
                     // owner_dobj.setPucc_centreno(fitness_dobj.getPucc_no());
                     // owner_dobj.setPucc_upto(fitness_dobj.getPucc_val());

                     }
                     int fit_upto = FitnessImpl.getNewFitnessUpto(owner_dobj);
                     if (fit_upto > 0) {
                     Date fitUpto = DateUtils.addToDate(new Date(), 3, fit_upto);
                     fitUpto = DateUtils.addToDate(fitUpto, 1, -1);
                     owner_dobj.setFit_upto(fitUpto);
                     }
                     }

                     if (statusDobj.getVehicleParameters() != null && owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                     statusDobj.getVehicleParameters().setLOGIN_OFF_CD(Util.getSelectedSeat().getOff_cd());
                     if (otherStateVehDobj != null && otherStateVehDobj.getOldOffCD() != null) {
                     statusDobj.getVehicleParameters().setPREV_OFF_CD(otherStateVehDobj.getOldOffCD());
                     }
                     }

                     //=================WEB SERVICES FOR NEXTSTAGE START=========//
                     ServerUtility.webServiceForNextStage(statusDobj, newStatus, COUNTER_ID, APPL_NO, ACTION_CDOE, purcd, approveImpl);
                     //===============WEB SERVICES FOR NEXTSTAGE END============//
                     */

                    if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                            || owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                        // if at verification level changes chassi no
                        NocDobj noc_dobj = ServerUtil.getChasiNoExist(owner_dobj.getChasi_no());

                        if (noc_dobj != null) {
                            if (noc_dobj.getVt_owner_status().equals("N")) {
                                Date vahan4StartDate = ServerUtil.getVahan4StartDate(noc_dobj.getState_to(), noc_dobj.getOff_to());
                                if (vahan4StartDate == null) {
                                    if (noc_dobj.getState_cd().equals(sessionVariables.getStateCodeSelected())
                                            && sessionVariables.getOffCodeSelected() == noc_dobj.getOff_cd()
                                            || noc_dobj.getState_to().equals(sessionVariables.getStateCodeSelected())
                                            && sessionVariables.getOffCodeSelected() == noc_dobj.getOff_to()) {
                                        throw new VahanException(TableConstants.NOC_ENDORSEMENT);
                                    } else if (!noc_dobj.getState_to().equals(sessionVariables.getStateCodeSelected())
                                            || sessionVariables.getOffCodeSelected() != noc_dobj.getOff_to()) {
                                        NocDobj nocVerifiedData = ServerUtil.getNocVerifiedData(REGN_NO, owner_dobj.getChasi_no());
                                        if (nocVerifiedData == null) {
                                            throw new VahanException(TableConstants.NOC_VERIFICATION);
                                        }
                                    }
                                } else {
                                    if (!noc_dobj.getState_to().equals(sessionVariables.getStateCodeSelected()) || sessionVariables.getOffCodeSelected() != noc_dobj.getOff_to()) {
                                        State stateCd = MasterTableFiller.state.get(noc_dobj.getState_to());
                                        String offCdLabel = null;
                                        if (stateCd != null) {
                                            List<SelectItem> listOff = stateCd.getOffice();
                                            for (SelectItem off : listOff) {
                                                if (off.getValue().toString().equals(String.valueOf(noc_dobj.getOff_to()))) {
                                                    offCdLabel = off.getLabel();
                                                    break;
                                                }
                                            }
                                        }
                                        throw new VahanException("Vehicle has NOC issued to state : " + stateCd.getStateDescr() + " Office: " + offCdLabel);
                                    }
                                }
                            } else {
                                // if vt_owner status not equal to N and chassisno already exist bcz on that chassi no registration can be don using regntype is other statew/ other rto if vt_owner status equal to N
                                throw new VahanException("Vehicle is already Registered!!!");
                            }
                        }
                        this.otherStateVehDobj.setStateCD(sessionVariables.getStateCodeSelected());
                        this.otherStateVehDobj.setOffCD(sessionVariables.getOffCodeSelected());
                        owner_dobj.setOtherStateVehDobj(this.otherStateVehDobj);
                    }
                }
            }

            if (purcd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || purcd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
            }
            if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("GA") && ACTION_CDOE == TableConstants.TM_NEW_RC_APPROVAL && newStatus.equals(TableConstants.STATUS_COMPLETE) && !CommonUtils.isNullOrBlank(generatedRegnNo)) {
                HttpSession ses = Util.getSession();
                ses.setAttribute("regn_no", generatedRegnNo);
                return "regnNoacknowledgement";
            }

        } catch (WebClientResponseException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getResponseBodyAsString(), e.getResponseBodyAsString()));
            return null;
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, vex.getMessage(), vex.getMessage()));
            return null;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return null;
        }

        if (newStatus.equals(TableConstants.STATUS_DISPATCH_PENDING)) {
            return "disAppNotice";
        } else {
            return "home";
        }
    }

    public void feeCompare() {
        try {
            renderCheckFeeTax = true;
            renderAppDisapp = true;
            renderCheckFeeTaxTab = true;
            renderCheckFeeTaxButton = false;
            checkFeeTax = EpayImpl.getPurCDPaymentsNewRegistration(owner_bean.set_Owner_appl_bean_to_dobj(),
                    APPL_NO, null, Util.getSelectedSeat().getPur_cd(), hypothecated);
            Thread feesTaxTread = null;
            String BASE_URI = "http://127.0.0.1:{port}/vahantaxws/webresources/";
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            int serverPort = request.getLocalPort();
            String baseURL = BASE_URI.replace("{port}", serverPort + "");
//            feesTaxTread = new Thread(new ApprovalTaxThread(owner_bean.set_Owner_appl_bean_to_dobj(), APPL_NO,
//                    REGN_NO, Util.getSelectedSeat().getPur_cd(), hypothecated, checkFeeTax, baseURL, Util.getUserStateCode(),
//                    Util.getSelectedSeat().getOff_cd(), Util.getSelectedSeat(), Util.getUserCategory(), Util.getEmpCode()), "fessTaxThread");
//            feesTaxTread.start();
            renderCheckFeeTax = true;
            if (ACTION_CDOE != TableConstants.TM_NEW_RC_APPROVAL && ACTION_CDOE != TableConstants.TM_ROLE_DEALER_APPROVAL && checkFeeTax != null) {
                checkFeeTax.setVerificationCheckBox(false);
            }
        } catch (VahanException vex) {
        } catch (Exception exception) {
            LOGGER.error(exception.toString() + " " + exception.getStackTrace()[0]);
        }
        autoRunCheckFeeTax = false;
    }

    public void viewComparison(ActionEvent ev) {

        Map<String, Object> options = new HashMap<String, Object>();
        options.put("modal", true);
        options.put("draggable", false);
        options.put("resizable", false);
        PrimeFaces.current().dialog().openDynamic("form_comparison.xhtml", options, null);

    }

    /*
     *  used to show Vehicle Registration Series Message 
     * valid for both RTO Login and Dealer Login
     */
    public void vehicleRegnSeriesMsg(OwnerBean owner_bean, Owner_dobj owner_dobj, String user_catg, String stateCode, int offCode) throws VahanException, Exception {
        if (FANCY_REGN_NO == null && RETEN_REGN_NO == null) {
            String regnNoAllotted = ServerUtil.getRegnNoAllotedDetail(APPL_NO, stateCode, offCode);
            if (regnNoAllotted != null && !regnNoAllotted.equals("")) {
                String msg = "";
                if (REGN_NO.equals("NEW")) {
                    msg = "Vehicle Registration No " + regnNoAllotted + " will be allotted (from the running series)";
                } else {
                    msg = "Vehicle Registration No " + regnNoAllotted + " is allotted (from the running series)";
                }
                owner_bean.setRegnNoAlloted(msg);
                owner_bean.setRegnNoAllotedPanel(true);
            } else {
                String seriesAvailMessage = null;
                if (owner_dobj != null && owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE) && Util.getTmConfiguration().isRegnNoNotAssignOthState()) {
                    seriesAvailMessage = "Vehicle Registration No " + owner_dobj.getRegn_no() + " will be  allotted";
                    owner_bean.setSeriesAvailMessage(seriesAvailMessage);
                } else if (owner_dobj != null && owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION) && Util.getTmConfiguration().isRegnNoNotAssignOthState() && owner_bean.getOwnerDobj().getAuctionDobj() != null && owner_bean.getOwnerDobj().getAuctionDobj().getRegnNo() != null && !owner_bean.getOwnerDobj().getAuctionDobj().getRegnNo().equals("NEW")) {
                    seriesAvailMessage = "Vehicle Registration No " + owner_bean.getOwnerDobj().getAuctionDobj().getRegnNo() + " will be  allotted";
                    owner_bean.setSeriesAvailMessage(seriesAvailMessage);
                } else {
                    VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(owner_dobj);
                    seriesAvailMessage = ServerUtil.getAvailablePrefixSeries(vehParameters);
                    if (!seriesAvailMessage.equals(TableConstants.SERIES_EXHAUST_MESSAGE) && !seriesAvailMessage.equals("")) {
                        vehicleRegnPrefix = seriesAvailMessage;
                        seriesAvailMessage = "Vehicle Registration No will be Generated from the Series " + seriesAvailMessage + ".";
                        owner_bean.setSeriesAvailMessage(seriesAvailMessage);
                    }
                }
                owner_bean.setAdvanceRegnAllotted("No");
                if (user_catg.equals(TableConstants.User_Dealer)) {
                    owner_bean.setBlnPgAdvancedRegNo(true);
                } else {
                    if (owner_bean.getOwnerDobj() != null && owner_bean.getOwnerDobj().getRegn_type() != null && !owner_bean.getOwnerDobj().getRegn_type().equals("") && owner_bean.getOwnerDobj().getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                        if (!owner_bean.isToRetention()) {
                            owner_bean.setBlnPgAdvancedRegNo(false);
                        } else {
                            owner_bean.setBlnPgAdvancedRegNo(true);
                        }
                    } else {
                        owner_bean.setBlnPgAdvancedRegNo(true);
                    }
                }
            }
        } else if (RETEN_REGN_NO != null) {
            String msg = "";
            if (REGN_NO.equals("NEW")) {
                msg = "Vehicle Registration No " + RETEN_REGN_NO + " will be allotted (as per Retention No)";
            } else {
                msg = "Vehicle Registration No " + RETEN_REGN_NO + " is allotted (as per Retention No)";
            }
            owner_bean.setRegnNoAlloted(msg);
            owner_bean.setRegnNoAllotedPanel(true);
        }
//        else {
//            String msg = "";
//            if (REGN_NO.equals("NEW")) {
//                msg = "Vehicle Registration No " + FANCY_REGN_NO + " will be allotted (as per booked Fancy/Advance Registration No)";
//            } else {
//                msg = "Vehicle Registration No " + FANCY_REGN_NO + " is allotted (as per booked Fancy/Advance Registration No)";
//            }
//            owner_bean.setRegnNoAlloted(msg);
//            owner_bean.setRegnNoAllotedPanel(true);
//            owner_bean.disableFancyNumberFields();
//        }
    }

    /*
     * used to disable and enable field when application no is not generated and used in Rto Temporary Case and Dealer Both case TEMp and New case.
     */
    public void disableEnableFieldsForNewAndTemp(OwnerBean owner_bean, InsBean ins_bean, HpaBean hpa_bean) {
        owner_bean.ownerComponentEditable(true);
        owner_bean.setDisableFName(false);
        ins_bean.componentEditable(true);
        hpa_bean.componentEditable(true);
        owner_bean.setDisableChasiNo(true);
        owner_bean.getOwnerDobj().setOwner_sr(1);
    }

    /*
     * code for fitness in Both RTO and Dealer Temporary and New Registratio
     */
    public void fitnessCode(OwnerBean owner_bean, Owner_dobj owner_dobj, HpaDobj hpa_dobj, String regnType, String userCatg, boolean isHomologationData) throws VahanException, Exception {
        owner_bean.setSamePermAdd_Rend(false);
        FitnessImpl fitness_Impl = new FitnessImpl();
        FitnessDobj fitness_dobj = fitness_Impl.set_Fitness_appl_db_to_dobj(null, APPL_NO);
        if (fitness_dobj != null) {
            FitnessBean fitness_bean = (FitnessBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("fitness_bean");
            if (fitness_bean == null) {
                fitness_bean = new FitnessBean();
            }
            fitness_bean.set_Fitness_appl_dobj_to_bean(fitness_dobj);
            fitness_bean.setFitness_dobj_prv(fitness_dobj);//for holding current dobj for using in the comparison.
            FacesContext.getCurrentInstance().getViewRoot().getViewMap().put("fitness_bean", fitness_bean);
        }
        if (owner_bean.getVehType().trim().equals(String.valueOf(TableConstants.VM_VEHTYPE_TRANSPORT))) {
            owner_bean.fitnessPanelLoader();
            if (hpa_dobj != null) {
                setVehicleHypothecatedDisable(true);
            }

            if (sessionVariables.getStateCodeSelected() != null && sessionVariables.getStateCodeSelected().equals("JH")) {
                String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
                String transCatg = null;
                for (int i = 0; i < data.length; i++) {
                    if (data[i][0].equalsIgnoreCase(String.valueOf(owner_dobj.getVh_class()))) {
                        transCatg = data[i][3];
                        break;
                    }
                }
                if (transCatg != null && transCatg.equals("P")) {
                    owner_bean.setDisableSeatCap(false);
                } else {
                    if (isHomologationData) {
                        if (userCatg != null && !userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                            if (owner_bean.getVch_purchase_as().equals(TableConstants.PURCHASE_AS_CHASIS)) {
                                owner_bean.setDisableSeatCap(false);
                            } else {
                                owner_bean.setDisableSeatCap(true);
                            }
                        } else {
                            owner_bean.setDisableSeatCap(true);
                        }
                    } else {
                        owner_bean.setDisableSeatCap(false);
                    }
                }
            }
        } else {
            if (FitnessImpl.getNewFitnessUpto(owner_dobj) >= 15) {
                owner_bean.setRenderInspectionPanel(false);
            } else {
                owner_bean.setRenderInspectionPanel(true);
            }
        }
    }

    /*
     * used to disable and enable fields after application no is generated and used only in RTO Login
     */
    public void disableEnableFields(OwnerBean owner_bean, Owner_dobj owner_dobj, InsDobj ins_dobj, InsBean ins_bean, HpaBean hpa_bean) throws VahanException, Exception {
        owner_bean.ownerComponentEditable(true);
        if (owner_dobj != null) {
            if (owner_dobj.getOwner_cd() != TableConstants.OWNER_CODE_INDIVIDUAL) {
                owner_bean.setDisableFName(true);

            } else {
                owner_bean.setDisableFName(false);
            }
        } else {
            owner_bean.setDisableFName(false);
        }
        if (ins_dobj != null && ins_dobj.getIns_type() == Integer.parseInt(TableConstants.INS_TYPE_NA)) {
            ins_bean.insTypeListener();
        } else if (ins_dobj != null && !ins_dobj.isIibData()) {
            ins_bean.componentEditable(true);
        }
        hpa_bean.componentEditable(true);
    }

    /*
     * used for maker model field in RTO Login
     */
    public void renderMakerModel(OwnerBean owner_bean, Owner_dobj owner_dobj) {
        if (owner_bean.getList_maker_model().isEmpty()
                || !OwnerImpl.isMakerModelInDb(owner_dobj == null ? 0 : owner_dobj.getMaker(),
                        owner_dobj == null ? "" : owner_dobj.getMaker_model())) {
            owner_bean.setModelEditable(true);
            owner_bean.setRenderModelSelectMenu(false);

        }
    }

    /*
     * Only for Delhi state : used to enable fuel Type for transport case in Temporary Registration Case
     */
    public void disableFuelType(OwnerBean owner_bean) throws VahanException, Exception {
        if (owner_bean.getOwnerDobj() != null && owner_bean.getOwnerDobj().getRegn_type() != null && owner_bean.getOwnerDobj().getVh_class() != 0
                && owner_bean.getOwnerDobj().getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY)) {
            if (ServerUtil.isTransport(owner_bean.getOwnerDobj().getVh_class(), owner_bean.getOwnerDobj()) && sessionVariables.getStateCodeSelected() != null && sessionVariables.getStateCodeSelected().equalsIgnoreCase("DL")) {
                owner_bean.setDisableFuelType(false);
            }
        }
    }

    public void showDetails() {

        String baseUrl = VahanUtil.getBaseUrl();
        Exception exception = null;
        String message = null;
        boolean isDealer = false;
        boolean disableCurrentState = false;
        boolean isNewVehicleFitness = false;
        ScrappedVehicleDobj scrapDobj = null;

        try {
            HttpSession session = null;
            // CASAuthentication casAuthentication = new CASAuthentication();
            session = Util.getSession();
            String serviceTicket = null;
            /*boolean casFlag = Boolean.parseBoolean((String) session.getAttribute("cas_flag"));
              if (casFlag) {
                String casTicket = (String) session.getAttribute("casTicket");
                CASTicketDetails details = new CASAuthentication().homologationCasServiceTicket(casTicket);
                if (details.getStatusCode() != 200) {
                    vahanMessages = TableConstants.SomthingWentWrong;
                    return;
                }
                serviceTicket = details.getResonseTicket();
            }*/
            Owner_dobj owner_dobj = null;
            Owner_dobj owneTmpDobj = null;
            Owner_dobj ownerScrap = null;
            Owner_dobj ownerHomo = null;
            NocDobj nocDobj = null;

            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getOffCodeSelected() == 0
                    || sessionVariables.getEmpCodeLoggedIn() == null) {
                return;
            }
            SessionVariablesModel sessionVariablesModel = new SessionVariablesModel(sessionVariables);
            String user_catg = Util.getUserCategory();

            WorkBenchModel workBenchModel = new WorkBenchModel();
            workBenchModel.setAPPL_NO(APPL_NO);
            workBenchModel.setROLE_CD(ROLE_CD);
            workBenchModel.setChasiNo(chasiNo);
            workBenchModel.setEngineNo(engineNo);
            workBenchModel.setPUR_CD(PUR_CD);
            workBenchModel.setRegnType(regnType);
            workBenchModel.setRegnTypeOSorORTO(regnTypeOSorORTO);
            workBenchModel.setRegnNo(regnNo);

            owner_bean.setPurCd(Integer.parseInt(PUR_CD));

            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();

            //cas service ticket validation
            final String ROOT_URI_showDetails = baseUrl + "/newregn/showDetails?serviceTicket=" + serviceTicket;

            OwnerBeanModel ownerBeanModel = new OwnerBeanModel(owner_bean);

            WrapperModel wrapperModel = new WrapperModel();
            wrapperModel.setWorkBench(workBenchModel);
            wrapperModel.setSessionVariables(sessionVariablesModel);
            wrapperModel.setTmConfigDobj(tmConfigDobj);
            wrapperModel.setOwnerBean(ownerBeanModel);
            
            RetroFittingDetailsBean cngBean = (RetroFittingDetailsBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("retroFittingDetailsBean");            
            HpaBean hpaBean = (HpaBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("hpa_bean");
            wrapperModel.setCngDetailsBean(new RetroFittingDetailsBeanModel(cngBean));
            wrapperModel.setHpaBeanModel(new HpaBeanModel(hpaBean));
            wrapperModel.setIns_bean(new InsBeanModel(ins_bean));

            wrapperModel = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build()
                    .post()
                    .uri(ROOT_URI_showDetails)
                    .body(Mono.just(wrapperModel), WrapperModel.class)
                    .retrieve()
                    .bodyToMono(WrapperModel.class)
                    .block();
            if (wrapperModel == null) {
                throw new VahanException("Error while fetching details from Homologation portal. Please try again later.");
            }

            OwnerBeanModel obm = wrapperModel.getOwnerBean();
//            owner_dobj = obm.getOwnerDobj();
            workBenchModel = wrapperModel.getWorkBench();

            if (obm.getHomoDobj() != null) {
                ownerHomo = obm.getHomoDobj();
            }
            owner_bean = obm.setOwnerBeanFromModel(owner_bean);

            setTrailer_tab(workBenchModel.isTrailer_tab());
            setOtherDistrictVehicle(workBenchModel.isOtherDistrictVehicle());
            setOtherStateVehicle(workBenchModel.isOtherStateVehicle());
            setRenderCDVehicle(workBenchModel.isRenderCDVehicle());
            setBlnScrappedVehicle(workBenchModel.isBlnScrappedVehicle());
            setRenderAppDisapp(workBenchModel.isRenderAppDisapp());
            
            wrapperModel.getIns_bean().populateBeanFromModel(ins_bean);
            wrapperModel.getCngDetailsBean().populateBeanFromModel(cngBean);
            wrapperModel.getHpaBeanModel().populateBeanFromModel(hpaBean);

            if (!CommonUtils.isNullOrBlank(chasiNo) && !CommonUtils.isNullOrBlank(engineNo)) {
                if (wrapperModel.getCngDetailsBean() != null) {
                    FacesContext.getCurrentInstance().getViewRoot().getViewMap().put(workBenchModel.getFacesMessage(), wrapperModel.getCngDetailsBean());
                    owner_bean.purchaseDateListener();
                }
                owner_bean.setVehicleComponentEditable(wrapperModel.getOwnerBean().isVehicleComponentEditable());
                //checking pending print of Chassis Fitness Certificate
                String isPending = workBenchModel.getPending();
                if (!CommonUtils.isNullOrBlank(isPending)) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, isPending, isPending));
                    PrimeFaces.current().ajax().update("msgDialog");
                    PrimeFaces.current().executeScript("PF('messageDialog').show();");
                    return;
                }
            }
            FitnessImpl fitnessImpl = new FitnessImpl();
            TmConfigurationFitnessDobj tmConfigurationFitnessDobj = fitnessImpl.getFitnessConfiguration(sessionVariables.getStateCodeSelected());
            if (!isNewVehicleFitness
                    && TableConstants.VM_VEHTYPE_TRANSPORT == owner_bean.getOwnerDobj().getVehType()
                    && tmConfigurationFitnessDobj.isNewVehicleFitness()
                    && (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_NEW) || regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_TEMPORARY))
                    && (Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE)) {

                throw new VahanException("New Module is Introduce For  New Vehicle  Fitness ,Please Use  [ Chassis No: " + chasiNo + "], [Engine No:" + engineNo + "] in  New Vehicle  Fitness Module ");
            }

            if (user_catg != null && user_catg.trim().equals(TableConstants.User_Dealer)) {
                if (tmConfigDobj != null && owner_bean != null && owner_bean.getOwnerDobj().getRegn_type().equals(TableConstants.VM_REGN_TYPE_NEW) && tmConfigDobj.isConsiderFMSDealer()) {
                    if (!CommonUtils.isNullOrBlank(workBenchModel.getPendingAppls())) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Alert!", "Files physically not sent to RTO/ARTO from " + tmConfigDobj.getFmsGraceDays() + " working days after successful payment. first clear at your end then go to RTO/ARTO to submit the files for pending Application(s) :-" + workBenchModel.getPendingAppls()));
                        return;
                    }
                }

                if (owner_dobj != null
                        && !this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_SCRAPPED)
                        && !isNewVehicleFitness) {
                    if (owneTmpDobj != null && owneTmpDobj.getDob_temp() != null && owneTmpDobj.getDob_temp().getPurpose() != null
                            && !("BB".contains(owneTmpDobj.getDob_temp().getPurpose())
                            && (owner_dobj.getState_cd().equals(sessionVariables.getStateCodeSelected())
                            && owner_dobj.getOff_cd() == sessionVariables.getOffCodeSelected())
                            || (owneTmpDobj.getDob_temp().getState_cd_to().equals(sessionVariables.getStateCodeSelected())
                            && owneTmpDobj.getDob_temp().getOff_cd_to() == sessionVariables.getOffCodeSelected()))) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Alert!", "Entry is Already Done for this Chassis No: " + this.chasiNo.trim().toUpperCase() + "," + (!"NEW".equals(owner_dobj.getRegn_no()) ? "[Regn.No: " + owner_dobj.getRegn_no() + "]" : "") + "," + (owner_dobj.getAppl_no() != null ? "[Appl.No: " + owner_dobj.getAppl_no() + "]" : "")));
                        return;
                    }
                }
                if (ownerHomo != null) {
                    if (workBenchModel.isExShoowroomPriceDisable()) {
                        if (ownerHomo.getSale_amt() == 0) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Inventory detail found but"
                                    + " Ex-Showroom Price has not been uploaded for the state " + ServerUtil.getStateNameByStateCode(sessionVariables.getStateCodeSelected()) + " <br/> "
                                    + " UMRN/Model Code: " + ownerHomo.getMaker_model() + " | "
                                    + " Color Code : " + ownerHomo.getColorCode() + " | "
                                    + " Feature Code : " + ownerHomo.getFeatureCode() + " "));
                            tabView = false;
                            return;
                        }
                    }
                    if (!workBenchModel.isFound()) {
                        /*\
                         * @UpdatedBy Kartikey
                         * Changed for testing for dealer
                         * @Original tabView = false;
                         * Do it for 2 dealers: wb31vdlrcon and wb31vdlrcon1
                         */
//                        if (sessionVariables.getUserIdForLoggedInUser().equals("wb31vdlrcon")
//                                || sessionVariables.getUserIdForLoggedInUser().equals("wb31vdlrcon1")
//                                || sessionVariables.getUserIdForLoggedInUser().equals("dlvdlrstaff")) {
//                            tabView = true;
//                        } else {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "User is not permitted"
                                + " to register mentioned maker. "));
                        tabView = false;

                        return;
                    }
//                    }
                } else {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Inventory details against this Chassis No/Engine No(last 5 chars) Combination"
                            + " is not uploaded by the manufacturer."
                            + " Please contact respective manufacturer to upload the inventory."));
                    tabView = false;
                    return;
                }
            }
            if (!this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                    && !this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)
                    && !this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_SCRAPPED)
                    && (this.chasiNo.trim() == null || this.engineNo.trim() == null || (this.engineNo.trim().length() != 5 && owner_dobj != null && (!TableConstants.ERICK_HOMO_VCH_CLASS.contains("," + owner_dobj.getHomoVchClass() + ","))))) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Invalid CHASSIS/ENGINE No"));
                return;
            }
            if ((this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                    || this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))
                    && (this.regnNo == null || this.chasiNo == null || this.chasiNo.length() != 5)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Invalid Registration/Chassis No"));
                return;
            }

            OtherStateVehImpl otherStateVehImpl = new OtherStateVehImpl();
            if (!isDealer) {
                if (this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                        || this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                    NocDobj nocEndorseDobj = ServerUtil.getNocEndorsementData(regnNo.trim().toUpperCase(), null);
                    NocDobj nocVerificationDobj = ServerUtil.getNocVerifiedData(regnNo.trim().toUpperCase(), null);
                    if (nocDobj != null && nocEndorseDobj == null && nocVerificationDobj == null) {
                        if ((nocDobj.getState_to().equalsIgnoreCase(sessionVariables.getStateCodeSelected()) && nocDobj.getOff_to() == 0)
                                || (nocDobj.getState_to().equalsIgnoreCase(sessionVariables.getStateCodeSelected()) && nocDobj.getOff_to() != sessionVariables.getOffCodeSelected())) {
                            nocDtlsMsg = "You are not Authorized to register this vehicle as NOC has been issued to Office " + workBenchModel.getOffCdLabel() + "(" + workBenchModel.getStLabel() + ")"
                                    + " from " + workBenchModel.getOffCdFromLabel() + "(" + workBenchModel.getStFromLabel() + ")" + ".<br/> If wrongly entered the office name by issuing authority, Please do the NOC Verification of this Vehicle/Chassis No.!!!";
                            PrimeFaces.current().executeScript("PF('nocAdmin').show()");
                            return;
                        }
                    }
                    if (owner_dobj != null) {
                        //  boolean isValidChassiNo = OwnerImpl.checkLast5ChassisChar(owner_dobj.getChasi_no(), chasiNo);
                        if (!workBenchModel.isIsValidChassiNo()) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN,
                                    "Alert!", "Invalid Combination with Chassis No (Last 5 Chars): " + this.chasiNo.trim().toUpperCase()));
                            return;
                        }
                    }
                }

                if (owner_dobj != null
                        && !this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                        && !this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)
                        && !this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_SCRAPPED)
                        && !this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION)
                        && !isNewVehicleFitness) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Alert!", "Entry is Already Done for this CHASSIS NO: " + this.chasiNo.trim().toUpperCase() + ", " + (!"NEW".equals(owner_dobj.getRegn_no()) ? "[Regn.No: " + owner_dobj.getRegn_no() + "]" : "[Office: " + ServerUtil.getOfficeName(owner_dobj.getOff_cd(), owner_dobj.getState_cd()) + "]") + ", " + (owner_dobj.getAppl_no() != null ? "[Appl.No: " + owner_dobj.getAppl_no() + "]" : "")));
                    return;
                }

//                if ((owner_dobj == null
//                        && !this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
//                        && !this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) || (isNewVehicleFitness && ownerHomo != null)) {
//
//                    if (!isNewVehicleFitness) {
//                        owner_dobj = ownerHomo;//= ServerUtil.getOnwerDobjFromHomologation(chasiNo.toUpperCase().trim(), engineNo.toUpperCase().trim());     
//                    }
//
//                    if (ownerHomo != null) {
////                        isDetailsFromHomologation = true;
////                        message = "Some of Vehicle Details are filled from HOMOLOGATION";
////                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
////                        disableSaleAmount = false;
//                        owner_bean.setIsHomologationData(TableConstants.HOMOLOGATION_DATA);
//                        owner_bean.setHomoLdWt(ownerHomo.getLd_wt());
//                        owner_bean.setHomoUnLdWt(ownerHomo.getUnld_wt());
//                        owner_bean.setHomoDobj(ownerHomo);
//                    }
//                }
                owner_dobj = obm.getOwnerDobj();
            }

            if (this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_TEMPORARY)
                    && ((Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE)
                    || (Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE && tmConfigDobj.isTempRegnToNewRegnAtDealer())
                    || (owneTmpDobj != null && owneTmpDobj.getDob_temp() != null && owneTmpDobj.getDob_temp().getPurpose() != null && owneTmpDobj.getDob_temp().getPurpose().equalsIgnoreCase("BB") && (Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE)))) {

                // condition for dealer
                if (user_catg != null && user_catg.equals(TableConstants.USER_CATG_DEALER)) {
                    if (owneTmpDobj == null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Temporary Registration details not found for this Chassis No " + chasiNo + " or not authorized to register this vehicle as Temporary Registered Vehicle.", "Temporary Registration details not found for this Chassis No " + chasiNo));
                        return;
                    }
                    if (workBenchModel.getDealerCd() != null && owneTmpDobj != null && owneTmpDobj.getDealer_cd() != null && !workBenchModel.getDealerCd().equals(owneTmpDobj.getDealer_cd())) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Invalid dealer for Registration", "Invalid dealer for Registration"));
                        return;
                    }
                }
                if (owneTmpDobj.getDob_temp() != null && owneTmpDobj.getDob_temp().getPurpose() != null
                        && !owneTmpDobj.getDob_temp().getPurpose().equalsIgnoreCase("OM")) {
                    message = "Vehicle Details are filled from Temporary Registered Vehicle";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
                    InsDobj insDobj = owneTmpDobj.getInsDobj();
                    if (insDobj != null) {
                        ins_bean.set_Ins_dobj_to_bean(insDobj);
                        if (insDobj.getIns_type() == Integer.parseInt(TableConstants.INS_TYPE_NA)) {
                            ins_bean.insTypeListener();
                        }
                    }
                }
            }
            if (owner_dobj != null) {
                if ((this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                        || this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))
                        && !owner_dobj.getStatus().equalsIgnoreCase(TableConstants.VT_NOC_ISSUE_STATUS)) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "NOC is not Available for this Registration!!!", "NOC is not Available for this Registration!!!"));
                    return;
                }
                if ((this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                        || this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))
                        && owner_dobj.getStatus().equalsIgnoreCase(TableConstants.VT_NOC_ISSUE_STATUS)) {
                    if (nocDobj != null) {
                        if (nocDobj.getState_to().equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                                && nocDobj.getOff_to() == sessionVariables.getOffCodeSelected()) {
                            if (this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                                if (!otherStateVehDobj.getOldStateCD().equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                                        || otherStateVehDobj.getOldOffCD() == sessionVariables.getOffCodeSelected()) {
                                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                                            "NOC invalid for vehicle " + regnNo, "NOC invalid for vehicle " + regnNo));
                                    return;
                                }

                            } else if (this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)) {
                                if (otherStateVehDobj.getOldStateCD().equalsIgnoreCase(sessionVariables.getStateCodeSelected())) {
                                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                                            "NOC invalid for vehicle " + regnNo, "NOC invalid for vehicle " + regnNo));
                                    return;
                                }
                            }
                        }
                    }
                }
                String[][] dataMap = null;
                ArrayList list_maker_model = null;
                dataMap = MasterTableFiller.masterTables.VM_MODELS.getData();
                list_maker_model = new ArrayList();
                for (int i = 0; i < dataMap.length; i++) {
                    if (dataMap[i][1].equals(owner_dobj.getMaker_model())) {
                        list_maker_model.add(new SelectItem(dataMap[i][1], dataMap[i][2]));
                        break;
                    }
                }

                owner_bean.set_Owner_appl_dobj_to_bean(owner_dobj);
                owner_bean.setList_maker_model(list_maker_model);
                owner_bean.setBlnDisableCurrentState(disableCurrentState);
                if (owner_bean.isBlnRegnTypeTemp()) {
                    owner_bean.setForNewTempRegnType();
                }

                if (!isNewVehicleFitness) {
                    if (owner_dobj.getFuel() == TableConstants.VM_FUEL_CNG_TYPE
                            || owner_dobj.getFuel() == TableConstants.VM_FUEL_TYPE_PETROL_CNG
                            || owner_dobj.getFuel() == TableConstants.VM_FUEL_TYPE_LPG
                            || owner_dobj.getFuel() == TableConstants.VM_FUEL_TYPE_PETROL_LPG) {
                        PrimeFaces.current().ajax().update("workbench_tabview:cngDetails");
                        FacesContext.getCurrentInstance().getViewRoot().getViewMap().put(workBenchModel.getFacesMessage(), wrapperModel.getCngDetailsBean());

                    }
                }
                //-----------Filling Axle Details ---------------------
                FacesContext.getCurrentInstance().getViewRoot().getViewMap().put(workBenchModel.getFacesMessage(), wrapperModel.getAxleBean());

                //HPA Details
                if (owner_dobj.getListHpaDobj() != null) {
                    if (!owner_dobj.getListHpaDobj().isEmpty() && owner_dobj.getListHpaDobj().get(0) != null) {
                        FacesContext.getCurrentInstance().getViewRoot().getViewMap().put(workBenchModel.getFacesMessage(), wrapperModel.getHpaBeanModel());
                    }
                }
                /////////////////////// Filling Imported Vehicle Details //////////////
                if (owner_bean.getImported_veh() != null && owner_bean.getImported_veh().trim().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_IMPORTED_YES)) {

                    if (owner_dobj.getImp_Dobj() != null) {
                        FacesContext.getCurrentInstance().getViewRoot().getViewMap().put(workBenchModel.getFacesMessage(), wrapperModel.getImportedVehicleBean());
                    }
                }
            } else if (!regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                    && !regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Inventory Details for this CHASSIS(" + this.chasiNo + ")/ENGINE No is not Uploaded by Manufacturer.Please Contact to the Administrator."));
                tabView = true;//this is need to be removed when project is live
                owner_bean.getOwnerDobj().setChasi_no(this.chasiNo);
                //return;
            }
            // Added by Kartikey
            if (owner_bean.getOwnerDobj() != null) {
                owner_bean.getOwnerDobj().setRegn_type(regnType);
            }
            // Added by Gagan
            tabView = workBenchModel.isTabView();

            if (this.regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_SCRAPPED) || isIsVehicleNoScrapRetain()) {
                if (ownerScrap != null) {
                    owner_bean.setOwnerDobj(setForScrappedVehicle(owner_bean.getOwnerDobj(), ownerScrap));
                    owner_bean.getOwnerDobj().setScrappedVehicleDobj(scrapDobj);
                    owner_bean.setVehType("" + ownerScrap.getVehType());
                    owner_bean.vehTypeListener();
                    owner_bean.getOwnerDobj().setVh_class(ownerScrap.getVh_class());
                    owner_bean.vehClassListener();
//                    owner_bean.vehCatgListener(null);
                    owner_bean.getOwnerDobj().setVch_catg(ownerScrap.getVch_catg());
                    owner_bean.setVehicleComponentEditable(true);
                    owner_bean.setIsScrapVehicleNORetain(isIsVehicleNoScrapRetain());
                } else {
                    throw new VahanException("Can't Find Scrap Details For This Vehicle");
                }
            }
        } catch (WebClientResponseException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getResponseBodyAsString(), e.getResponseBodyAsString()));
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception e) {
            exception = e;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

        if (exception != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void modifyNocDetails() {
        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect(ec.getRequestContextPath() + "/vahan/ui/admin/formNocAdmin.xhtml?regnNo=" + regnNo);
            return;
        } catch (IOException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void fetchTemporaryRegisteredDetails(Owner_dobj owner_dobj, OwnerBean owner_bean) throws Exception {
        TempRegDobj temP = owner_dobj.getTempReg();
        TempRegDobj temPClone = null;
        if (temP == null) {
            temP = new TempRegDobj();
            owner_dobj.setTempReg(temP);
        }
        temPClone = (TempRegDobj) temP.clone();
        owner_bean.setTempReg(temP);
        owner_bean.setTempReg_prev(temPClone);
        owner_bean.setBlnRegnTypeTemp(true);

        if (owner_dobj.getTempReg().getTmp_state_cd() != null) {
            owner_bean.getList_office_to().clear();
            String[][] data = MasterTableFiller.masterTables.TM_OFFICE.getData();
            for (int i = 0; i < data.length; i++) {
                if (data[i][13].equals(owner_dobj.getTempReg().getTmp_state_cd())) {
                    owner_bean.getList_office_to().add(new SelectItem(data[i][0], data[i][1]));
                }
            }

            List<Dealer> listDealer = ServerUtil.getDealerList(owner_dobj.getTempReg().getTmp_state_cd(), owner_dobj.getTempReg().getTmp_off_cd());
            if (listDealer == null || listDealer.isEmpty()) {
                listDealer = ServerUtil.getDealerList(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
            }
            owner_bean.getList_dealer_cd().clear();
            for (Dealer dl : listDealer) {
                owner_bean.getList_dealer_cd().add(new SelectItem(dl.getDealer_cd(), dl.getDealer_name()));
            }
        }
    }

    private Owner_dobj setForScrappedVehicle(Owner_dobj dobj, Owner_dobj scrapDobj) {
        if (scrapDobj == null) {
            return dobj;
        }
        owner_bean.setOwner_identification(scrapDobj.getOwner_identity());
        dobj.setOwner_identity(scrapDobj.getOwner_identity());
        dobj.setOwner_name(scrapDobj.getOwner_name());
        dobj.setF_name(scrapDobj.getF_name());
        dobj.setOwner_cd(scrapDobj.getOwner_cd());
        dobj.setOwner_sr(1);
        dobj.setC_add1(scrapDobj.getC_add1());
        dobj.setC_add2(scrapDobj.getC_add2());
        dobj.setC_add3(scrapDobj.getC_add3());
        dobj.setC_state(scrapDobj.getC_state());
        dobj.setC_pincode(scrapDobj.getC_pincode());
        dobj.setC_district(scrapDobj.getC_district());
        dobj.setP_add1(scrapDobj.getP_add1());
        dobj.setP_add2(scrapDobj.getP_add2());
        dobj.setP_add3(scrapDobj.getP_add3());
        dobj.setP_state(scrapDobj.getP_state());
        dobj.setP_pincode(scrapDobj.getP_pincode());
        dobj.setP_district(scrapDobj.getP_district());
        State curSt = MasterTableFiller.state.get(scrapDobj.getP_state());
        owner_bean.setList_p_district(curSt.getDisrict());
        return dobj;
    }

    public void checkForTabView(String applNo) throws VahanException, Exception {
        boolean validForTempDelete = false;
        ArrayList<Status_dobj> statusDobjList = ServerUtil.applicationStatusByApplNo(applNo, sessionVariables.getStateCodeSelected());
        if (statusDobjList != null && !statusDobjList.isEmpty() && statusDobjList.size() == 2) {
            validForTempDelete = true;
        } else {
            tabView = true;
        }

        if (validForTempDelete) {
            boolean validRegn = TempRegImpl.checkApprovalStatusOfTempAppl(applNo, TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE, TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE);
            if (validRegn) {
                tabView = true;
            }
        }
    }

    public void regnTypeChangeListener(AjaxBehaviorEvent event) {
        try {
            TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
            this.chasiNo = "";
            this.regnNo = "";
            this.engineNo = "";
            otherDistrictVehicle = false;
            otherStateVehicle = false;
            blnScrappedVehicle = false;
            renderScrapCheckBoxOption = false;
            blnScrappedVehiclePanel = false;
            renderScrapRegnNo = false;
            regnTypeOSorORTO = false;
            renderTempStateOffPanel = false;
            getDtlsBtnLabel = "Get Details";
            if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_NEW)) {
                getDtlsBtnLabel = "Get Details from Homologation Portal";
            } else if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)) {
                otherStateVehicle = true;
                regnTypeOSorORTO = true;
            } else if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                otherDistrictVehicle = true;
                regnTypeOSorORTO = true;
            } else if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_SCRAPPED)) {
                blnScrappedVehiclePanel = true;
                renderScrapRegnNo = true;
            } else if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_TEMPORARY)) {
                blnScrappedVehiclePanel = true;
                String user_catg = Util.getUserCategory();
                getDtlsBtnLabel = "Get Details from Homologation Portal";
                if (configurationDobj != null && configurationDobj.isScrap_veh_no_retain() && !user_catg.equals(TableConstants.User_Dealer)) {
                    renderScrapCheckBoxOption = true;
                }
            }

            if (configurationDobj != null) {
                if (Integer.parseInt(PUR_CD) != TableConstants.VM_TRANSACTION_MAST_TEMP_REG && Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                        && !TableConstants.VM_REGN_TYPE_NEW.equals(regnType) && !TableConstants.VM_REGN_TYPE_CD.equals(regnType) && !TableConstants.VM_REGN_TYPE_EXARMY.equals(regnType)) {
                    if ((TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE.equals(regnType) || TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE.equals(regnType)) && configurationDobj.isAllowRegnDataFoundForOS_OR()) {
                        renderTempStateOffPanel = true;
                        Owner_temp_dobj dobj = owner_bean.getTemp_dobj();
                        if (dobj == null) {
                            dobj = new Owner_temp_dobj();
                            owner_bean.setTemp_dobj(dobj);
                        }
                    }
                }
            }
        } catch (VahanException ve) {
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void regnNumListener(AjaxBehaviorEvent event) {
        try {
            Owner_dobj ownerDo = null;
            boolean isNocVerified = new NocVerificationImpl().checkNocVerified(regnNo);
            if (!isNocVerified) {
                if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)) {
                    ownerDo = new OwnerImpl().set_Owner_appl_db_to_dobj(regnNo, null, null, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
                    if (ownerDo != null && ownerDo.getState_cd().equalsIgnoreCase(sessionVariables.getStateCodeSelected())) {
                        this.regnNo = "";
                        throw new VahanException("Invalid Registration Number for Other State Registration");
                    }
                } else if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                    ownerDo = new OwnerImpl().set_Owner_appl_db_to_dobj(regnNo, null, null, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
                    if (ownerDo != null && !ownerDo.getState_cd().equalsIgnoreCase(sessionVariables.getStateCodeSelected())) {
                        this.regnNo = "";
                        throw new VahanException("Invalid Registration Number for Other RTO Registration");
                    }
                }
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            return;
        }
    }

    public void scrapCheckBoxListner() {
        if (isIsVehicleNoScrapRetain() == true) {
            renderScrapRegnNo = true;
        } else {
            renderScrapRegnNo = false;
        }
    }

    public void updateRtoFromStateListener(AjaxBehaviorEvent even) {

        Map<Object, Object> office = ServerUtil.getOfficeListOfState(this.otherStateVehDobj.getOldStateCD());
        officeList.clear();

        for (Map.Entry<Object, Object> entry : office.entrySet()) {
            int off_cd = (int) entry.getKey();
            String off_name = (String) entry.getValue();
            officeList.add(new SelectItem(off_cd, off_name));
        }
    }

    public void nocVerifiedData(String regnNo, OtherStateVehImpl otherStateVehImpl) throws VahanException {
        NocDobj nocVerificationdobj = ServerUtil.getNocVerifiedData(regnNo, null);
        this.otherStateVehDobj = otherStateVehImpl.setNocVerificationDetailsToOtherStateVeh(nocVerificationdobj);
        //Temporary Solution...Need to Fix it
        Map<Object, Object> office = ServerUtil.getOfficeListOfState(this.otherStateVehDobj.getOldStateCD());
        officeList.clear();
        for (Map.Entry<Object, Object> entry : office.entrySet()) {
            int off_cd = (int) entry.getKey();
            String off_name = (String) entry.getValue();
            officeList.add(new SelectItem(off_cd, off_name));
        }

    }

    public void dealerFields(OwnerBean owner_bean) throws VahanException {
        owner_bean.disableForHomologationData(true);
        TmConfigurationDobj tmConf = Util.getTmConfiguration();
        boolean isExShoowroomPriceDisable = tmConf.isEx_showroom_price_homologation();
        if (isExShoowroomPriceDisable && !tmConf.getTmConfigDealerDobj().isValidateHomoSaleAmt()) {
            owner_bean.setDealerSaleAmt(true);
        } else {
            owner_bean.setDealerSaleAmt(false);
        }
        owner_bean.setDisableColor(true);
        owner_bean.setDisableManuMonth(true);
        owner_bean.setDisableManuYr(true);
        owner_bean.setIsDealer(true);
        owner_bean.setDisableDealerCd(true);
        if (owner_bean.getOwnerDobj().getRegn_type() != null && owner_bean.getOwnerDobj().getRegn_type().equals("N")
                && owner_bean.getVch_purchase_as() != null && owner_bean.getVch_purchase_as().equals(TableConstants.PURCHASE_AS_CHASIS)) {
            owner_bean.setDisableVchPurchaseAs(false);
        }
    }

    public void rToLevelHomologationFields(OwnerBean owner_bean) throws VahanException {
        owner_bean.setRenderModelEditable(false);
        //owner_bean.vehicleComponentEditableHomologation(false);
        owner_bean.disableForHomologationData(true);
        owner_bean.setDisableManuMonth(true);
        owner_bean.setDisableManuYr(true);
        if (owner_bean.getVch_purchase_as().equals(TableConstants.PURCHASE_AS_CHASIS)) {
            owner_bean.enableForDriveAwayChassisInDealer(false);
        }
        if (owner_bean.getOwnerDobj().getRegn_type() != null && owner_bean.getOwnerDobj().getRegn_type().equals("N")
                && owner_bean.getVch_purchase_as() != null && owner_bean.getVch_purchase_as().equals(TableConstants.PURCHASE_AS_CHASIS)) {
            owner_bean.setDisableVchPurchaseAs(false);
        }

        if (owner_bean.getSale_amt() > 0) {
            if (Util.getTmConfiguration() != null && Util.getTmConfiguration().isEx_showroom_price_homologation() && Util.getTmConfiguration().getTmConfigDealerDobj() != null
                    && !Util.getTmConfiguration().getTmConfigDealerDobj().isValidateHomoSaleAmt()
                    && !(owner_bean.getOwnerDobj().getOwner_cd() == TableConstants.VEH_TYPE_STATE_GOVT || owner_bean.getOwnerDobj().getOwner_cd() == TableConstants.VEH_TYPE_GOVT)) {
                owner_bean.setDealerSaleAmt(true);
            }
        }
    }

    public void checkSaleAmount(int homoSaleAmt, int saleAmt) throws VahanException {
        if (homoSaleAmt > saleAmt) {
            if (ACTION_CDOE != TableConstants.TM_NEW_RC_APPROVAL && ACTION_CDOE != TableConstants.TM_ROLE_DEALER_APPROVAL
                    && ACTION_CDOE != TableConstants.TM_ROLE_DEALER_TEMP_APPROVAL && ACTION_CDOE != TableConstants.TM_TMP_RC_APPROVAL) {
                owner_bean.setDealerSaleAmt(false);
                PrimeFaces.current().ajax().update("workbench_tabview:sale_amt");
                throw new VahanException(TableConstants.SALE_AMT_ERR_MESS + " {Rs." + homoSaleAmt + "/-} Now Modify Sale Amount.");
            }
        }
    }

    public void checkSeatCap(int homoSeatCap, int seatCap) throws VahanException {
        if (homoSeatCap > seatCap) {
            throw new VahanException(TableConstants.SEAT_CAP_ERR_MESS + " {" + homoSeatCap + "}");
        }
    }

    public void getBankDetails(int vhClass, String applNo, int ownerCd) throws VahanException {
        if (sessionVariables.getStateCodeSelected() != null && applNo != null && "DL".equals(sessionVariables.getStateCodeSelected()) && ownerCd == TableConstants.OWNER_CODE_INDIVIDUAL) {
            if (vhClass != 0 && TableConstants.ERICKSHAW_VCH_CLASS == vhClass) {
                bankSubsidyDetail = new PendencyBankDetailImpl().getBankSubsidyData(applNo, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                showBankDetails = true;
                String[][] bankData = MasterTableFiller.masterTables.TM_BANK.getData();
                for (int i = 0; i < bankData.length; i++) {
                    bankNameList.add(new SelectItem(bankData[i][0], bankData[i][1]));
                }
            }

        }
    }

    public PendencyBankDobj getBankSubsidyDetailsDobj(Owner_dobj owner_dobj, PendencyBankDobj bankSubsidy) {
        if (owner_dobj != null && bankSubsidy != null && !"".equals(bankSubsidy) && "DL".equals(sessionVariables.getStateCodeSelected()) && owner_dobj.getVh_class() == TableConstants.ERICKSHAW_VCH_CLASS
                && Integer.valueOf(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE && owner_dobj.getOwner_cd() == TableConstants.OWNER_CODE_INDIVIDUAL) {
            bankSubsidyDetail.setAadharNo(owner_dobj.getOwner_identity().getAadhar_no());
            bankSubsidyDetail.setDlLlNo(owner_dobj.getOwner_identity().getDl_no());
            bankSubsidyDetail.setApplNo(APPL_NO);
            bankSubsidyDetail.setStatusCode(TableConstants.SUBSIDY_PENDING_STATUS);
        }
        return bankSubsidyDetail;
    }

    public void renderOwnerChoiceNoTab(Owner_dobj owner_dobj) throws VahanException {
        try {
            if (TableConstants.USER_CATG_DEALER.equals(sessionVariables.getUserCatgForLoggedInUser()) && !CommonUtils.isNullOrBlank(APPL_NO) && Integer.valueOf(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                Map<String, String> choiceDetails = OwnerChoiceNoImpl.getOwnerChoiceRegnNoDetails(APPL_NO);
                if (ACTION_CDOE == TableConstants.TM_ROLE_DEALER_VERIFICATION) {
                    if (choiceDetails != null && !choiceDetails.isEmpty()) {
                        ownerChoiceNoBean.setPrefixRegnSeries(choiceDetails.get("regn_no").substring(0, choiceDetails.get("regn_no").length() - 4));
                        ownerChoiceNoBean.setSufixRegnNo(choiceDetails.get("regn_no").substring(choiceDetails.get("regn_no").length() - 4, choiceDetails.get("regn_no").length()));
                        ownerChoiceNoBean.setRenderClearChoiceNo(true);
                        ownerChoiceNoBean.setRegnChoiceAmount(Integer.parseInt(choiceDetails.get("choice_fees")));
                        ownerChoiceNoBean.setVehicleRegnGenType("P");
                        ownerChoiceNoBean.setRenderChoiceNopanel(true);
                        renderOwnerChoiceNoPanel = true;
                        owner_bean.setVehicleComponentEditable(true);
                    } else {
                        renderOwnerChoiceNoPanel = true;
                        ownerChoiceNoBean.setPrefixRegnSeries(vehicleRegnPrefix);
                    }
                }
                ownerChoiceNoBean.setApplNo(APPL_NO);
                ownerChoiceNoBean.setOwnerDobj(owner_dobj);
                ownerChoiceNoBean.setRenderSuccessChoiceDialog(false);
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem occurred during rendering Owner Choice Number details.");
        }

    }

    public void otherStateOffCdListender(AjaxBehaviorEvent event) {
        try {
            int oldOffCd = (int) ((javax.faces.component.html.HtmlSelectOneMenu) event.getSource()).getValue();
            if (oldOffCd != 0 && (TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE.equals(regnType) || TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE.equals(regnType)
                    && Util.getTmConfiguration() != null && Util.getTmConfiguration().isAllowRegnDataFoundForOS_OR()) && owner_bean.getOwnerDobj() != null) {
                List<OwnerDetailsDobj> ownerDetails = new OwnerImpl().getRegnFromChassisNo(owner_bean.getOwnerDobj().getChasi_no());
                if (ownerDetails == null || ownerDetails.isEmpty()) {
                    Date v4StartDate = ServerUtil.getVahan4StartDate(otherStateVehDobj.getOldStateCD(), oldOffCd);
                    if (v4StartDate != null) {
                        otherStateVehDobj.setOldOffCD(0);
                        JSFUtils.showMessagesInDialog("Alert !!", "Vehicle can not Register for selected Office " + ServerUtil.getOfficeName(oldOffCd, otherStateVehDobj.getOldStateCD()) + ", as vehicle details for OtherState/Other RTO not found in V4.", FacesMessage.SEVERITY_WARN);
                    }
                }
            }
        } catch (VahanException ve) {
            JSFUtils.showMessagesInDialog("Alert !!", ve.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Error !!!", "Problem occurred during getting the Other State/Other RTO vehicle details.", FacesMessage.SEVERITY_ERROR);
        }
    }

    public void setForAuctionVehicle(AuctionDobj auctionDobj, Date purchaseDate, OwnerBean owner_bean, Owner_dobj owner_dobj, boolean clearAddressDetail) {
        if (auctionDobj != null) {
            owner_bean.setAuctionVisibilityTab(true);
            auctionDobj.setPurchaseDate(purchaseDate);
            auctionDobj.setDisableAuctionPanel(true);
            if (clearAddressDetail) {
                if (owner_dobj != null) {
                    owner_dobj.setOwner_name("");
                    owner_dobj.setF_name("");
                    owner_dobj.setOwner_cd(-1);
                    owner_dobj.setDealer_cd("0");
                    owner_dobj.setC_add1(null);
                    owner_dobj.setC_add2(null);
                    owner_dobj.setC_add3(null);
                    owner_dobj.setC_state(sessionVariables.getStateCodeSelected());
                    owner_dobj.setC_district(-1);
                    owner_dobj.setC_pincode(0);
                    owner_dobj.setP_add1(null);
                    owner_dobj.setP_add2(null);
                    owner_dobj.setP_add3(null);
                    owner_dobj.setP_state(sessionVariables.getStateCodeSelected());
                    owner_dobj.setP_district(-1);
                    owner_dobj.setP_pincode(0);
                    owner_dobj.getOwner_identity().setMobile_no(0l);
                    owner_dobj.getOwner_identity().setOwnerCatg(-1);
                    owner_dobj.getOwner_identity().setAadhar_no(null);
                    owner_dobj.getOwner_identity().setDl_no(null);
                    owner_dobj.getOwner_identity().setEmail_id(null);
                    owner_dobj.getOwner_identity().setPan_no(null);
                    owner_dobj.getOwner_identity().setPassport_no(null);
                    owner_dobj.getOwner_identity().setRation_card_no(null);
                    owner_dobj.getOwner_identity().setVoter_id(null);
                    owner_dobj.setOwner_sr((owner_dobj.getOwner_sr() + 1));
                }
            }
            owner_bean.getOwnerDobj().setAuctionDobj(auctionDobj);
        }
    }

    /**
     * @return the ROLE_CD
     */
    public int getROLE_CD() {
        return ROLE_CD;
    }

    /**
     * @param ROLE_CD the ROLE_CD to set
     */
    public void setROLE_CD(int ROLE_CD) {
        this.ROLE_CD = ROLE_CD;
    }

    /**
     * @return the APPL_NO
     */
    public String getAPPL_NO() {
        return APPL_NO;
    }

    /**
     * @param APPL_NO the APPL_NO to set
     */
    public void setAPPL_NO(String APPL_NO) {
        this.APPL_NO = APPL_NO;
    }

    /**
     * @return the PUR_CD
     */
    public String getPUR_CD() {
        return PUR_CD;
    }

    /**
     * @param PUR_CD the PUR_CD to set
     */
    public void setPUR_CD(String PUR_CD) {
        this.PUR_CD = PUR_CD;
    }

    /**
     * @param approveImpl the approveImpl to set
     */
    public void setApproveImpl(ApproveImpl approveImpl) {
        this.approveImpl = approveImpl;
    }

    /**
     * @return the PURPOSE
     */
    public String getPURPOSE() {
        return PURPOSE;
    }

    /**
     * @param PURPOSE the PURPOSE to set
     */
    public void setPURPOSE(String PURPOSE) {
        this.PURPOSE = PURPOSE;
    }

    /**
     * @return the OFFICE_REMARK
     */
    public String getOFFICE_REMARK() {
        return OFFICE_REMARK;
    }

    /**
     * @param OFFICE_REMARK the OFFICE_REMARK to set
     */
    public void setOFFICE_REMARK(String OFFICE_REMARK) {
        this.OFFICE_REMARK = OFFICE_REMARK;
    }

    /**
     * @return the PUBLIC_REMARK
     */
    public String getPUBLIC_REMARK() {
        return PUBLIC_REMARK;
    }

    /**
     * @param PUBLIC_REMARK the PUBLIC_REMARK to set
     */
    public void setPUBLIC_REMARK(String PUBLIC_REMARK) {
        this.PUBLIC_REMARK = PUBLIC_REMARK;
    }

    /**
     * @return the REGN_NO
     */
    public String getREGN_NO() {
        return REGN_NO;
    }

    /**
     * @param REGN_NO the REGN_NO to set
     */
    public void setREGN_NO(String REGN_NO) {
        this.REGN_NO = REGN_NO;
    }

    /**
     * @return the APPL_DT
     */
    public String getAPPL_DT() {
        return APPL_DT;
    }

    /**
     * @param APPL_DT the APPL_DT to set
     */
    public void setAPPL_DT(String APPL_DT) {
        this.APPL_DT = APPL_DT;
    }

    public String getIncludeSrcURL() {
        return includeSrcURL;
    }

    /**
     * @param includeSrcURL the includeSrcURL to set
     */
    public void setIncludeSrcURL(String includeSrcURL) {
        this.includeSrcURL = includeSrcURL;
    }

    /**
     * @return the sub_panel
     */
    public boolean getSub_panel() {
        return sub_panel;
    }

    /**
     * @param sub_panel the sub_panel to set
     */
    public void setSub_panel(boolean sub_panel) {
        this.sub_panel = sub_panel;
    }

    /**
     * @return the trailer_tab
     */
    public boolean getTrailer_tab() {
        return trailer_tab;
    }

    /**
     * @param trailer_tab the trailer_tab to set
     */
    public void setTrailer_tab(boolean trailer_tab) {
        this.trailer_tab = trailer_tab;
    }

    public String getCUR_STATUS() {
        return CUR_STATUS;
    }

    public void setCUR_STATUS(String CUR_STATUS) {
        this.CUR_STATUS = CUR_STATUS;
    }

    /**
     * @return the prevRoleLabelVale
     */
    public Map<String, Object> getPrevRoleLabelVale() {
        return prevRoleLabelVale;
    }

    /**
     * @param prevRoleLabelVale the prevRoleLabelVale to set
     */
    public void setPrevRoleLabelVale(Map<String, Object> prevRoleLabelVale) {
        this.prevRoleLabelVale = prevRoleLabelVale;
    }

    /**
     * @return the ACTION_CDOE
     */
    public int getACTION_CDOE() {
        return ACTION_CDOE;
    }

    /**
     * @param ACTION_CDOE the ACTION_CDOE to set
     */
    public void setACTION_CDOE(int ACTION_CDOE) {
        this.ACTION_CDOE = ACTION_CDOE;
    }

    /**
     * @return the COUNTER_ID
     */
    public String getCOUNTER_ID() {
        return COUNTER_ID;
    }

    /**
     * @param COUNTER_ID the COUNTER_ID to set
     */
    public void setCOUNTER_ID(String COUNTER_ID) {
        this.COUNTER_ID = COUNTER_ID;
    }

    public boolean isMain_panal_visibililty() {
        return main_panal_visibililty;
    }

    public void setMain_panal_visibililty(boolean main_panal_visibililty) {
        this.main_panal_visibililty = main_panal_visibililty;
    }

    /**
     * @return the tmp_veh_dtls
     */
    public boolean isTmp_veh_dtls() {
        return tmp_veh_dtls;
    }

    /**
     * @param tmp_veh_dtls the tmp_veh_dtls to set
     */
    public void setTmp_veh_dtls(boolean tmp_veh_dtls) {
        this.tmp_veh_dtls = tmp_veh_dtls;
    }

    /**
     * @return the hypothecated
     */
    public boolean isHypothecated() {
        return hypothecated;
    }

    /**
     * @param hypothecated the hypothecated to set
     */
    public void setHypothecated(boolean hypothecated) {
        this.hypothecated = hypothecated;
    }

    /**
     * @return the flag
     */
    public boolean isFlag() {
        return flag;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    /**
     * @return the chasiNo
     */
    public String getChasiNo() {
        return chasiNo;
    }

    /**
     * @param chasiNo the chasiNo to set
     */
    public void setChasiNo(String chasiNo) {
        this.chasiNo = chasiNo;
    }

    /**
     * @return the engineNo
     */
    public String getEngineNo() {
        return engineNo;
    }

    /**
     * @param engineNo the engineNo to set
     */
    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    /**
     * @return the tabView
     */
    public boolean isTabView() {
        return tabView;
    }

    /**
     * @param tabView the tabView to set
     */
    public void setTabView(boolean tabView) {
        this.tabView = tabView;
    }

    /**
     * @param owner_bean the owner_bean to set
     */
    public void setOwner_bean(OwnerBean owner_bean) {
        this.owner_bean = owner_bean;
    }

    /**
     * @return the listRegnType
     */
    public List getListRegnType() {
        return listRegnType;
    }

    /**
     * @param listRegnType the listRegnType to set
     */
    public void setListRegnType(List listRegnType) {
        this.listRegnType = listRegnType;
    }

    /**
     * @return the regnType
     */
    public String getRegnType() {
        return regnType;
    }

    /**
     * @param regnType the regnType to set
     */
    public void setRegnType(String regnType) {
        this.regnType = regnType;
    }

    /**
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the otherStateVehicle
     */
    public boolean isOtherStateVehicle() {
        return otherStateVehicle;
    }

    /**
     * @param otherStateVehicle the otherStateVehicle to set
     */
    public void setOtherStateVehicle(boolean otherStateVehicle) {
        this.otherStateVehicle = otherStateVehicle;
    }

    /**
     * @return the otherStateVehDobj
     */
    public OtherStateVehDobj getOtherStateVehDobj() {
        return otherStateVehDobj;
    }

    /**
     * @param otherStateVehDobj the otherStateVehDobj to set
     */
    public void setOtherStateVehDobj(OtherStateVehDobj otherStateVehDobj) {
        this.otherStateVehDobj = otherStateVehDobj;
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
     * @return the officeList
     */
    public List getOfficeList() {
        return officeList;
    }

    /**
     * @param officeList the officeList to set
     */
    public void setOfficeList(List officeList) {
        this.officeList = officeList;
    }

    private void setForTempRegnType() {
        listRegnType.clear();
        listRegnType.add(new SelectItem(TableConstants.VM_REGN_TYPE_TEMPORARY, "Temporary Registration"));

    }

    public boolean isDisAppPrint() {
        return disAppPrint;
    }

    public void setDisAppPrint(boolean disAppPrint) {
        this.disAppPrint = disAppPrint;
    }

    /**
     * @return the renderApplNoGenMessage
     */
    public boolean isRenderApplNoGenMessage() {
        return renderApplNoGenMessage;
    }

    /**
     * @param renderApplNoGenMessage the renderApplNoGenMessage to set
     */
    public void setRenderApplNoGenMessage(boolean renderApplNoGenMessage) {
        this.renderApplNoGenMessage = renderApplNoGenMessage;
    }

    /**
     * @return the applNoGenMessage
     */
    public String getApplNoGenMessage() {
        return applNoGenMessage;
    }

    /**
     * @param applNoGenMessage the applNoGenMessage to set
     */
    public void setApplNoGenMessage(String applNoGenMessage) {
        this.applNoGenMessage = applNoGenMessage;
    }

    /**
     * @return the renderPrintDiscalimerButton
     */
    public boolean isRenderPrintDiscalimerButton() {
        return renderPrintDiscalimerButton;
    }

    /**
     * @param renderPrintDiscalimerButton the renderPrintDiscalimerButton to set
     */
    public void setRenderPrintDiscalimerButton(boolean renderPrintDiscalimerButton) {
        this.renderPrintDiscalimerButton = renderPrintDiscalimerButton;
    }

    /**
     * @param ins_bean the ins_bean to set
     */
    public void setIns_bean(InsBean ins_bean) {
        this.ins_bean = ins_bean;
    }

    public List getAssignedReasonList() {
        return assignedReasonList;
    }

    public void setAssignedReasonList(List assignedReasonList) {
        this.assignedReasonList = assignedReasonList;
    }

    /**
     * @return the FANCY_REGN_NO
     */
    public String getFANCY_REGN_NO() {
        return FANCY_REGN_NO;
    }

    /**
     * @param FANCY_REGN_NO the FANCY_REGN_NO to set
     */
    public void setFANCY_REGN_NO(String FANCY_REGN_NO) {
        this.FANCY_REGN_NO = FANCY_REGN_NO;
    }

    /**
     * @return the otherDistrictVehicle
     */
    public boolean isOtherDistrictVehicle() {
        return otherDistrictVehicle;
    }

    /**
     * @param otherDistrictVehicle the otherDistrictVehicle to set
     */
    public void setOtherDistrictVehicle(boolean otherDistrictVehicle) {
        this.otherDistrictVehicle = otherDistrictVehicle;
    }

    /**
     * @return the RETEN_REGN_NO
     */
    public String getRETEN_REGN_NO() {
        return RETEN_REGN_NO;
    }

    /**
     * @param RETEN_REGN_NO the RETEN_REGN_NO to set
     */
    public void setRETEN_REGN_NO(String RETEN_REGN_NO) {
        this.RETEN_REGN_NO = RETEN_REGN_NO;
    }

    /**
     * @return the vehicleHypothecatedDisable
     */
    public boolean isVehicleHypothecatedDisable() {
        return vehicleHypothecatedDisable;
    }

    /**
     * @param vehicleHypothecatedDisable the vehicleHypothecatedDisable to set
     */
    public void setVehicleHypothecatedDisable(boolean vehicleHypothecatedDisable) {
        this.vehicleHypothecatedDisable = vehicleHypothecatedDisable;
    }

    /**
     * @return the renderCDVehicle
     */
    public boolean isRenderCDVehicle() {
        return renderCDVehicle;
    }

    /**
     * @param renderCDVehicle the renderCDVehicle to set
     */
    public void setRenderCDVehicle(boolean renderCDVehicle) {
        this.renderCDVehicle = renderCDVehicle;
    }

    /**
     * @return the blnScrappedVehicle
     */
    public boolean isBlnScrappedVehicle() {
        return blnScrappedVehicle;
    }

    /**
     * @param blnScrappedVehicle the blnScrappedVehicle to set
     */
    public void setBlnScrappedVehicle(boolean blnScrappedVehicle) {
        this.blnScrappedVehicle = blnScrappedVehicle;
    }

    /**
     * @return the checkFeeTax
     */
    public EpayDobj getCheckFeeTax() {
        return checkFeeTax;
    }

    /**
     * @param checkFeeTax the checkFeeTax to set
     */
    public void setCheckFeeTax(EpayDobj checkFeeTax) {
        this.checkFeeTax = checkFeeTax;
    }

    /**
     * @return the checkPaidFeeTax
     */
    public EpayDobj getCheckPaidFeeTax() {
        return checkPaidFeeTax;
    }

    /**
     * @param checkPaidFeeTax the checkPaidFeeTax to set
     */
    public void setCheckPaidFeeTax(EpayDobj checkPaidFeeTax) {
        this.checkPaidFeeTax = checkPaidFeeTax;
    }

    /**
     * @return the renderCheckFeeTax
     */
    public boolean isRenderCheckFeeTax() {
        return renderCheckFeeTax;
    }

    /**
     * @param renderCheckFeeTax the renderCheckFeeTax to set
     */
    public void setRenderCheckFeeTax(boolean renderCheckFeeTax) {
        this.renderCheckFeeTax = renderCheckFeeTax;
    }

    /**
     * @return the renderCheckFeeTaxTab
     */
    public boolean isRenderCheckFeeTaxTab() {
        return renderCheckFeeTaxTab;
    }

    /**
     * @param renderCheckFeeTaxTab the renderCheckFeeTaxTab to set
     */
    public void setRenderCheckFeeTaxTab(boolean renderCheckFeeTaxTab) {
        this.renderCheckFeeTaxTab = renderCheckFeeTaxTab;
    }

    /**
     * @return the renderAppDisapp
     */
    public boolean isRenderAppDisapp() {
        return renderAppDisapp;
    }

    /**
     * @param renderAppDisapp the renderAppDisapp to set
     */
    public void setRenderAppDisapp(boolean renderAppDisapp) {
        this.renderAppDisapp = renderAppDisapp;
    }

    /**
     * @return the autoRunCheckFeeTax
     */
    public boolean isAutoRunCheckFeeTax() {
        return autoRunCheckFeeTax;
    }

    /**
     * @param autoRunCheckFeeTax the autoRunCheckFeeTax to set
     */
    public void setAutoRunCheckFeeTax(boolean autoRunCheckFeeTax) {
        this.autoRunCheckFeeTax = autoRunCheckFeeTax;
    }

    /**
     * @return the renderAdvanceNoOption
     */
    public boolean isRenderAdvanceNoOption() {
        return renderAdvanceNoOption;
    }

    /**
     * @param renderAdvanceNoOption the renderAdvanceNoOption to set
     */
    public void setRenderAdvanceNoOption(boolean renderAdvanceNoOption) {
        this.renderAdvanceNoOption = renderAdvanceNoOption;
    }

    public boolean isRenderScrapCheckBoxOption() {
        return renderScrapCheckBoxOption;
    }

    public void setRenderScrapCheckBoxOption(boolean renderScrapCheckBoxOption) {
        this.renderScrapCheckBoxOption = renderScrapCheckBoxOption;
    }

    public boolean isIsVehicleNoScrapRetain() {
        return isVehicleNoScrapRetain;
    }

    public void setIsVehicleNoScrapRetain(boolean isVehicleNoScrapRetain) {
        this.isVehicleNoScrapRetain = isVehicleNoScrapRetain;
    }

    public boolean isRenderScrapRegnNo() {
        return renderScrapRegnNo;
    }

    public void setRenderScrapRegnNo(boolean renderScrapRegnNo) {
        this.renderScrapRegnNo = renderScrapRegnNo;
    }

    public boolean isBlnScrappedVehiclePanel() {
        return blnScrappedVehiclePanel;
    }

    public void setBlnScrappedVehiclePanel(boolean blnScrappedVehiclePanel) {
        this.blnScrappedVehiclePanel = blnScrappedVehiclePanel;
    }

    /**
     * @return the renderExemptionOD
     */
    public boolean isRenderExemptionOD() {
        return renderExemptionOD;
    }

    /**
     * @param renderExemptionOD the renderExemptionOD to set
     */
    public void setRenderExemptionOD(boolean renderExemptionOD) {
        this.renderExemptionOD = renderExemptionOD;
    }

    /**
     * @return the exemptionOD
     */
    public StringBuilder getExemptionOD() {
        return exemptionOD;
    }

    /**
     * @param exemptionOD the exemptionOD to set
     */
    public void setExemptionOD(StringBuilder exemptionOD) {
        this.exemptionOD = exemptionOD;
    }

    /**
     * @return the tempRegnDetailsList
     */
    public List<OwnerDetailsDobj> getTempRegnDetailsList() {
        return tempRegnDetailsList;
    }

    /**
     * @param tempRegnDetailsList the tempRegnDetailsList to set
     */
    public void setTempRegnDetailsList(List<OwnerDetailsDobj> tempRegnDetailsList) {
        this.tempRegnDetailsList = tempRegnDetailsList;
    }

    /**
     * @return the nocDtlsMsg
     */
    public String getNocDtlsMsg() {
        return nocDtlsMsg;
    }

    /**
     * @param nocDtlsMsg the nocDtlsMsg to set
     */
    public void setNocDtlsMsg(String nocDtlsMsg) {
        this.nocDtlsMsg = nocDtlsMsg;
    }

    /**
     * @return the vahanMessages
     */
    public String getVahanMessages() {
        return vahanMessages;
    }

    /**
     * @param vahanMessages the vahanMessages to set
     */
    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    /**
     * @return the renderPartialButton
     */
    public boolean isRenderPartialButton() {
        return renderPartialButton;
    }

    /**
     * @param renderPartialButton the renderPartialButton to set
     */
    public void setRenderPartialButton(boolean renderPartialButton) {
        this.renderPartialButton = renderPartialButton;
    }

    /**
     * @return the regnTypeOSorORTO
     */
    public boolean isRegnTypeOSorORTO() {
        return regnTypeOSorORTO;
    }

    /**
     * @param regnTypeOSorORTO the regnTypeOSorORTO to set
     */
    public void setRegnTypeOSorORTO(boolean regnTypeOSorORTO) {
        this.regnTypeOSorORTO = regnTypeOSorORTO;
    }

    /**
     * @return the getDtlsBtnLabel
     */
    public String getGetDtlsBtnLabel() {
        return getDtlsBtnLabel;
    }

    /**
     * @param getDtlsBtnLabel the getDtlsBtnLabel to set
     */
    public void setGetDtlsBtnLabel(String getDtlsBtnLabel) {
        this.getDtlsBtnLabel = getDtlsBtnLabel;
    }

    /**
     * @return the documentUploadShow
     */
    public boolean isDocumentUploadShow() {
        return documentUploadShow;
    }

    /**
     * @param documentUploadShow the documentUploadShow to set
     */
    public void setDocumentUploadShow(boolean documentUploadShow) {
        this.documentUploadShow = documentUploadShow;
    }

    /**
     * @return the dmsFileUploadUrl
     */
    public String getDmsFileUploadUrl() {
        return dmsFileUploadUrl;
    }

    /**
     * @param dmsFileUploadUrl the dmsFileUploadUrl to set
     */
    public void setDmsFileUploadUrl(String dmsFileUploadUrl) {
        this.dmsFileUploadUrl = dmsFileUploadUrl;
    }

    /**
     * @return the successUplodedMsg
     */
    public String getSuccessUplodedMsg() {
        return successUplodedMsg;
    }

    /**
     * @param successUplodedMsg the successUplodedMsg to set
     */
    public void setSuccessUplodedMsg(String successUplodedMsg) {
        this.successUplodedMsg = successUplodedMsg;
    }

    /**
     * @return the showBankDetails
     */
    public boolean isShowBankDetails() {
        return showBankDetails;
    }

    /**
     * @param showBankDetails the showBankDetails to set
     */
    public void setShowBankDetails(boolean showBankDetails) {
        this.showBankDetails = showBankDetails;
    }

    /**
     * @return the bankSubsidyDetail
     */
    public PendencyBankDobj getBankSubsidyDetail() {
        return bankSubsidyDetail;
    }

    /**
     * @param bankSubsidyDetail the bankSubsidyDetail to set
     */
    public void setBankSubsidyDetail(PendencyBankDobj bankSubsidyDetail) {
        this.bankSubsidyDetail = bankSubsidyDetail;
    }

    /**
     * @return the bankNameList
     */
    public List getBankNameList() {
        return bankNameList;
    }

    /**
     * @param bankNameList the bankNameList to set
     */
    public void setBankNameList(List bankNameList) {
        this.bankNameList = bankNameList;
    }

    /**
     * @return the renderDocumentUploadBtn
     */
    public boolean isRenderDocumentUploadBtn() {
        return renderDocumentUploadBtn;
    }

    /**
     * @param renderDocumentUploadBtn the renderDocumentUploadBtn to set
     */
    public void setRenderDocumentUploadBtn(boolean renderDocumentUploadBtn) {
        this.renderDocumentUploadBtn = renderDocumentUploadBtn;
    }

    /**
     * @return the documentUpload_bean
     */
    public DocumentUploadBean getDocumentUpload_bean() {
        return documentUpload_bean;
    }

    /**
     * @param documentUpload_bean the documentUpload_bean to set
     */
    public void setDocumentUpload_bean(DocumentUploadBean documentUpload_bean) {
        this.documentUpload_bean = documentUpload_bean;
    }

    /**
     * @return the renderTempStateOffPanel
     */
    public boolean isRenderTempStateOffPanel() {
        return renderTempStateOffPanel;
    }

    /**
     * @param renderTempStateOffPanel the renderTempStateOffPanel to set
     */
    public void setRenderTempStateOffPanel(boolean renderTempStateOffPanel) {
        this.renderTempStateOffPanel = renderTempStateOffPanel;
    }

    public boolean isRenderVltdDialog() {
        return renderVltdDialog;
    }

    public void setRenderVltdDialog(boolean renderVltdDialog) {
        this.renderVltdDialog = renderVltdDialog;
    }

    /**
     * @return the renderDMSMesgDialog
     */
    public boolean isRenderDMSMesgDialog() {
        return renderDMSMesgDialog;
    }

    /**
     * @param renderDMSMesgDialog the renderDMSMesgDialog to set
     */
    public void setRenderDMSMesgDialog(boolean renderDMSMesgDialog) {
        this.renderDMSMesgDialog = renderDMSMesgDialog;
    }

    /**
     * @return the renderCheckFeeTaxButton
     */
    public boolean isRenderCheckFeeTaxButton() {
        return renderCheckFeeTaxButton;
    }

    /**
     * @param renderCheckFeeTaxButton the renderCheckFeeTaxButton to set
     */
    public void setRenderCheckFeeTaxButton(boolean renderCheckFeeTaxButton) {
        this.renderCheckFeeTaxButton = renderCheckFeeTaxButton;
    }

    /**
     * @return the ownerChoiceNoBean
     */
    public OwnerChoiceNoBean getOwnerChoiceNoBean() {
        return ownerChoiceNoBean;
    }

    /**
     * @param ownerChoiceNoBean the ownerChoiceNoBean to set
     */
    public void setOwnerChoiceNoBean(OwnerChoiceNoBean ownerChoiceNoBean) {
        this.ownerChoiceNoBean = ownerChoiceNoBean;
    }

    /**
     * @return the renderOwnerChoiceNoPanel
     */
    public boolean isRenderOwnerChoiceNoPanel() {
        return renderOwnerChoiceNoPanel;
    }

    /**
     * @param renderOwnerChoiceNoPanel the renderOwnerChoiceNoPanel to set
     */
    public void setRenderOwnerChoiceNoPanel(boolean renderOwnerChoiceNoPanel) {
        this.renderOwnerChoiceNoPanel = renderOwnerChoiceNoPanel;
    }

    /**
     * @return the renderFasTagDialog
     */
    public boolean isRenderFasTagDialog() {
        return renderFasTagDialog;
    }

    /**
     * @param renderFasTagDialog the renderFasTagDialog to set
     */
    public void setRenderFasTagDialog(boolean renderFasTagDialog) {
        this.renderFasTagDialog = renderFasTagDialog;
    }

    public boolean isIsFasTagInstalled() {
        return isFasTagInstalled;
    }

    public void setIsFasTagInstalled(boolean isFasTagInstalled) {
        this.isFasTagInstalled = isFasTagInstalled;
    }

    public boolean isRenderFastag() {
        return renderFastag;
    }

    public void setRenderFastag(boolean renderFastag) {
        this.renderFastag = renderFastag;
    }

    public boolean isIsTransportVehicle() {
        return isTransportVehicle;
    }

    public void setIsTransportVehicle(boolean isTransportVehicle) {
        this.isTransportVehicle = isTransportVehicle;
    }

    public String getVehRegnPlateColorCode() {
        return vehRegnPlateColorCode;
    }

    public void setVehRegnPlateColorCode(String vehRegnPlateColorCode) {
        this.vehRegnPlateColorCode = vehRegnPlateColorCode;
    }

    public String getFancyRcptNo() {
        return fancyRcptNo;
    }

    public void setFancyRcptNo(String fancyRcptNo) {
        this.fancyRcptNo = fancyRcptNo;
    }
}
