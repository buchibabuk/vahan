/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.ExArmyDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.ImportedVehicleDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.Trailer_dobj;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.CdImpl;
import nic.vahan.form.impl.RetroFittingDetailsImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.ExArmyImpl;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.ImportedVehicleImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.NewImpl;
import nic.vahan.form.impl.OtherStateVehImpl;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.WorkBench;
import nic.vahan.server.ServerUtil;
import org.primefaces.component.panel.Panel;
import org.primefaces.PrimeFaces;
import org.apache.log4j.Logger;
import nic.vahan.form.impl.permit.PermitImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.form.dobj.dealer.TmConfigurationDealerDobj;
import nic.vahan.form.impl.Trailer_Impl;
import nic.vahan.form.impl.dealer.PendencyBankDetailImpl;
import nic.vahan5.reg.rest.config.SpringContext;
import nic.vahan5.reg.rest.model.dobj.fancy.AdvanceRegnNoDobjModel;
import nic.vahan5.reg.rest.model.AxleBeanModel;
import nic.vahan5.reg.rest.model.ComparisonBeanModel;
import nic.vahan5.reg.rest.model.ContextMessageModel;
import nic.vahan5.reg.rest.model.ContextMessageModel.MessageContext;
import nic.vahan5.reg.rest.model.ContextMessageModel.MessageSeverity;
import nic.vahan5.reg.rest.model.ExArmyBeanModel;
import nic.vahan5.reg.rest.model.HpaBeanModel;
import nic.vahan5.reg.rest.model.ImportedVehicleBeanModel;
import nic.vahan5.reg.rest.model.InsBeanModel;
import nic.vahan5.reg.rest.model.OwnerBeanModel;
import nic.vahan5.reg.rest.model.RetroFittingDetailsBeanModel;
import nic.vahan5.reg.rest.model.SessionVariablesModel;
import nic.vahan5.reg.rest.model.WorkBenchModel;
import nic.vahan5.reg.rest.model.WrapperModel;
import nic.vahan5.util.VahanUtil;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
//import io.netty.channel.*;

@ViewScoped
@ManagedBean(name = "comparisonBean")
public class ComparisonBean implements Serializable {

    private WebClient.Builder webClientBuilder = SpringContext.getBean(WebClient.Builder.class);
    private static Logger LOGGER = Logger.getLogger(ComparisonBean.class);
    @ManagedProperty(value = "#{owner_bean}")
    private OwnerBean owner_bean;
    @ManagedProperty(value = "#{hpa_bean}")
    private HpaBean hpa_bean;
    @ManagedProperty(value = "#{ins_bean}")
    private InsBean ins_bean;
    @ManagedProperty(value = "#{fitness_bean}")
    private FitnessBean fitness_bean;
    @ManagedProperty(value = "#{workBench}")
    private WorkBench workBench;
    @ManagedProperty(value = "#{exArmyBean}")
    private ExArmyBean exArmyBean;
    @ManagedProperty(value = "#{importedVehicleBean}")
    private ImportedVehicleBean importedVehicle_Bean;
    @ManagedProperty(value = "#{axleBean}")
    private AxleBean axleBean;
    @ManagedProperty(value = "#{retroFittingDetailsBean}")
    private RetroFittingDetailsBean cNG_Details_Bean;
    private String fields;
    private String old_value;
    private String new_value;
    private String changed_data;
    private String op_dt;
    private int user;
    private Panel changedByPrevUser = new Panel();
    private ArrayList<ComparisonBean> compBeanList = new ArrayList<ComparisonBean>();
    private List<ComparisonBean> prevChangedDataList;
    private ArrayList<ComparisonBean> compareChagnesList;
    private String APPL_NO;
    private String REGN_NO;
    private String PUR_CD;
    private int ROLE_CD;
    private int ACTION_CODE;
    private long Emp_Cd;
    private String userName;
    private String COUNTER_ID;
    private boolean printDisclaimerButton = false;
    private boolean okButton = false;
    private List<Entry<String, String>> entryDetails;
    private SessionVariables sessionVariables = null;
    @ManagedProperty(value = "#{trailer_bean}")
    private Trailer_bean trailer_bean;
    private String ownerMobileVerifyOtp = null;
    private String enteredOwnerMobVerifyOtp = null;
    TmConfigurationDobj tmConfDobj = null;
    String mobileNoCountMessage = null;

    @PostConstruct
    public void init() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getEmpCodeLoggedIn() == null) {
                return;
            }
            //getting the role code and pupose code,appl_no,regn_no from session
            Map map = (Map) Util.getSession().getAttribute("seat_map");

            if (map != null && map.get("pur_code") != null) {
                this.PUR_CD = (String) map.get("pur_code");
            } else {
                this.PUR_CD = String.valueOf(sessionVariables.getSelectedWork().getPur_cd());
            }

            if (map != null && map.get("actionCode") != null) {
                ACTION_CODE = Integer.parseInt(map.get("actionCode").toString().trim());
                this.ROLE_CD = Integer.parseInt(map.get("actionCode").toString().substring(3));// for extracting role_code from action_code
            } else {
                ACTION_CODE = sessionVariables.getSelectedWork().getAction_cd();
                this.ROLE_CD = Integer.parseInt(String.valueOf(sessionVariables.getSelectedWork().getAction_cd()).substring(3));// for extracting role_code from action_code
            }

            if (map != null && map.get("appl_no") != null) {
                this.APPL_NO = map.get("appl_no").toString();
            } else {
                this.APPL_NO = sessionVariables.getSelectedWork().getAppl_no();
            }

            if (map != null && map.get("regn_no") != null) {
                this.REGN_NO = map.get("regn_no").toString();
            } else {
                this.REGN_NO = sessionVariables.getSelectedWork().getRegn_no();
            }
            Emp_Cd = Long.parseLong(sessionVariables.getEmpCodeLoggedIn());
            prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUser(APPL_NO);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void compareChanges() {
        saveChangedData(false);
        compareChagnesList = compBeanList;
    }

    public void save_ActionListener() {
        String baseUrl = VahanUtil.getBaseUrl();
        String saveType = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("saveType");

        try {
            if (Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && ACTION_CODE == TableConstants.TM_ROLE_NEW_APPL
                    || Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE && ACTION_CODE == TableConstants.TM_ROLE_DEALER_NEW_APPL
                    || Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_TEMP_REG && ACTION_CODE == TableConstants.TM_ROLE_NEW_APPL_TEMP
                    || Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE && ACTION_CODE == TableConstants.TM_ROLE_DEALER_TEMP_APPL) {

                // 1. Preparing objects to be wrapped in the Wrapper Model to be passed to the REST controller
                ComparisonBeanModel comparisonBeanModel = new ComparisonBeanModel(this);
                SessionVariablesModel sessionVariablesModel = new SessionVariablesModel(sessionVariables);

                WorkBenchModel workBenchModel = new WorkBenchModel();
                workBenchModel.setRegnType(this.workBench.getRegnType());
                workBenchModel.setChasiNo(this.workBench.getChasiNo());
                workBenchModel.setEngineNo(this.workBench.getEngineNo());
                workBenchModel.setOtherStateVehDobj(this.workBench.getOtherStateVehDobj());
                workBenchModel.setBankSubsidyDetail(this.workBench.getBankSubsidyDetail());
                workBenchModel.setHypothecated(this.workBench.isHypothecated());

                OwnerBeanModel ownerBeanModel = new OwnerBeanModel();
                ownerBeanModel.setOwnerDobj(this.owner_bean.getOwnerDobj());
                ownerBeanModel.setOwner_dobj_prv(this.owner_bean.getOwner_dobj_prv());

                ownerBeanModel.setRenderOwnerDtlsPartialBtn(this.owner_bean.isRenderOwnerDtlsPartialBtn());
                ownerBeanModel.setIsScrapVehicleNORetain(this.owner_bean.isIsScrapVehicleNORetain());
                ownerBeanModel.setExArmyVehicle_Visibility_tab(this.owner_bean.isExArmyVehicle_Visibility_tab());
                ownerBeanModel.setImportedVehicle_Visibility_tab(this.owner_bean.isImportedVehicle_Visibility_tab());
                ownerBeanModel.setAxleDetail_Visibility_tab(this.owner_bean.isAxleDetail_Visibility_tab());
                ownerBeanModel.setCngDetails_Visibility_tab(this.owner_bean.isCngDetails_Visibility_tab());

                ownerBeanModel.setVch_purchase_as(this.owner_bean.getVch_purchase_as());
                ownerBeanModel.setTempReg(this.owner_bean.getTempReg());
                ownerBeanModel.setIsHomologationData(this.owner_bean.getIsHomologationData());
                ownerBeanModel.setHomoUnLdWt(this.owner_bean.getHomoUnLdWt());
                ownerBeanModel.setHomoDobj(this.owner_bean.getHomoDobj());
                if (this.owner_bean.getAdvanceRegNoDobj() != null) {
                    ownerBeanModel.setAdvanceRegnNoDobj(new AdvanceRegnNoDobjModel(this.owner_bean.getAdvanceRegNoDobj()));
                }
                ownerBeanModel.setRetenRegNoDobj(this.owner_bean.getRetenRegNoDobj());
                ownerBeanModel.setTmConfDobj(this.owner_bean.getTmConfigDobj());                

                Owner_dobj owner_dobj = this.owner_bean.set_Owner_appl_bean_to_dobj();
                InsDobj ins_dobj = this.ins_bean.set_InsBean_to_dobj();

                WrapperModel wrapperModel = new WrapperModel(comparisonBeanModel, sessionVariablesModel, workBenchModel,
                        owner_dobj, ins_dobj, (TmConfigurationDobj) Util.getSession().getAttribute("tmConfig"));
                wrapperModel.setOwnerBean(ownerBeanModel);
                wrapperModel.setHpaBeanModel(new HpaBeanModel((HpaBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("hpa_bean") == null ? this.hpa_bean : (HpaBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("hpa_bean")));
                wrapperModel.setExArmyBean(new ExArmyBeanModel(this.exArmyBean));
                wrapperModel.setImportedVehicleBean(new ImportedVehicleBeanModel((ImportedVehicleBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("importedVehicleBean") == null ? this.importedVehicle_Bean : (ImportedVehicleBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("importedVehicleBean")));
                wrapperModel.setAxleBean(new AxleBeanModel(this.axleBean));
                wrapperModel.setCngDetailsBean(new RetroFittingDetailsBeanModel((RetroFittingDetailsBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("retroFittingDetailsBean") == null ? this.cNG_Details_Bean : (RetroFittingDetailsBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("retroFittingDetailsBean")));                
                wrapperModel.setIns_bean(new InsBeanModel(this.ins_bean));
                
                wrapperModel.setTrailerDobj(FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("trailer_bean") != null ? ((Trailer_bean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("trailer_bean")).setTrailerBeanToDobj() : null);

                String restUrl = baseUrl + "/save/partial?saveType=" + saveType
                        + "&changedData=" + ComparisonBeanImpl.changedDataContents(compBeanList)
                        + "&userLoginOffCode=" + Util.getUserLoginOffCode()
                        + "&clientIpAddress=" + Util.getClientIpAdress()
                        + "&selectedRoleCode=" + (String) Util.getSession().getAttribute("selected_role_cd");

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
                }

                // Check for messages in FacesContext or RequestContext.                // Retirn if required.
                ContextMessageModel context = wrapperModel.getContextMessageModel();
                if (context != null) {
                    Severity severity = context.getMessageSeverity().equals(MessageSeverity.INFO) ? FacesMessage.SEVERITY_INFO : FacesMessage.SEVERITY_ERROR;
                    FacesMessage facesMessage = new FacesMessage(severity, context.getMessage1(), context.getMessage2());
                    if (context.getMessageContext().equals(MessageContext.FACES)) {
                        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                    } else {                        
                        PrimeFaces.current().dialog().showMessageDynamic(facesMessage);
                    }

                    if (context.isIsReturn()) {
                        return;
                    }
                }

                // 3. Setting values back in instance variable/objects from WrapperModel returned by controller.
                this.workBench.setOtherStateVehDobj(wrapperModel.getWorkBench().getOtherStateVehDobj());

                tmConfDobj = wrapperModel.getTmConfigDobj();

                /* 4. Fetch APPL_NO and REGN_NO and populate Owner_Dobj      */
                APPL_NO = wrapperModel.getComparisonBean().getAPPL_NO();
                REGN_NO = wrapperModel.getComparisonBean().getREGN_NO();
                owner_dobj = wrapperModel.getOwnerDobj();

                TmConfigurationDealerDobj dealerConfigDobj = tmConfDobj.getTmConfigDealerDobj();
                if (dealerConfigDobj != null && owner_bean.getHomoDobj() != null) {
                    if (owner_bean.getHomoDobj().getSale_amt() > owner_dobj.getSale_amt()
                            && (owner_dobj.getOwner_cd() == TableConstants.VEH_TYPE_STATE_GOVT || owner_dobj.getOwner_cd() == TableConstants.VEH_TYPE_GOVT)
                            && (ACTION_CODE == TableConstants.TM_ROLE_NEW_APPL
                            || ACTION_CODE == TableConstants.TM_ROLE_NEW_APPL_TEMP
                            || ACTION_CODE == TableConstants.TM_ROLE_DEALER_NEW_APPL
                            || ACTION_CODE == TableConstants.TM_ROLE_DEALER_TEMP_APPL)) {
                        ServerUtil.Compare("Home_sale_amt", owner_bean.getHomoDobj().getSale_amt(), owner_dobj.getSale_amt(), compBeanList);
                    }
                    // Processing for UI component and hence not moved to controller. 
                    if (dealerConfigDobj.isValidateHomoSaleAmt() && owner_dobj.getOwner_cd() != TableConstants.VEH_TYPE_STATE_GOVT && owner_dobj.getOwner_cd() != TableConstants.VEH_TYPE_GOVT) {
                        workBench.checkSaleAmount(owner_bean.getHomoDobj().getSale_amt(), owner_dobj.getSale_amt());
                    }
                }

                if (owner_bean.getRetenRegNoDobj() != null && owner_dobj.getOwner_identity() != null) {
                    ServerUtil.Compare("Mobile_no", owner_bean.getRetenRegNoDobj().getMobile_no(), owner_dobj.getOwner_identity().getMobile_no(), compBeanList);
                }

                /* 5.     Fill vehicle parameters and find the available series number for the vehicle        */
                String seriesAvailMess = wrapperModel.getComparisonBean().getSeriesAvailMess();
                Status_dobj status = wrapperModel.getStatusDobj();

                /*
                 * Can't move
                 * UI Processing part
                 */
                if (!CommonUtils.isNullOrBlank(APPL_NO) && !CommonUtils.isNullOrBlank(saveType) && saveType.equalsIgnoreCase(TableConstants.NEW_APPL_SAVETYPE_PARTIAL)) {
                    Util.getSelectedSeat().setAppl_no(APPL_NO);
                    workBench.setAPPL_NO(APPL_NO);
                    owner_bean.setAppl_no(APPL_NO);
                    owner_dobj.setAppl_no(APPL_NO); // Doesn't affect anything
                    workBench.setTabView(true);
                    Map map = (Map) Util.getSession().getAttribute("seat_map"); // Doesn't affect anything
                    map.put("appl_no", APPL_NO);    // Doesn't affect anything
                    Util.getSelectedSeat().setPur_cd(status.getPur_cd());
                }

                if (Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                    workBench.setRenderPrintDiscalimerButton(true);
                    workBench.setApplNoGenMessage("Application generated successfully. Application No. :" + APPL_NO + "<br/> " + seriesAvailMess);
                } else {
                    workBench.setRenderPrintDiscalimerButton(false);
                    workBench.setApplNoGenMessage("Application generated successfully. Application No. :" + APPL_NO);
                }

                workBench.setRenderApplNoGenMessage(true);
                if (!CommonUtils.isNullOrBlank(saveType) && saveType.equalsIgnoreCase(TableConstants.NEW_APPL_SAVETYPE_PARTIAL)) {
                    PrimeFaces.current().ajax().update("op_showPartialGenApplNo");
                    PrimeFaces.current().executeScript("PF('successDialogPartialGen').show()");
                } else {
                    PrimeFaces.current().ajax().update("op_showGenApplNo");
                    PrimeFaces.current().executeScript("PF('successDialog').show()");
                }
                //END for refreshing the same page after successfull submission of page and generating application no                
            }
        } catch (WebClientResponseException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getResponseBodyAsString(), e.getResponseBodyAsString()));
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        } finally {
        }

    }
    
    public String printDisclaimer() {
        return PrintDocImpl.printOwnerDiscReport("newRegisteredVehicles", "entryFormat");
    }

    public String saveChangedData(boolean blnSave) {
        Exception e = null;
        String return_file = "home";
        String changedData = null;//for storing the changed data into vha_changed_data
        TransactionManager tmgr = null;
        String hypothecationStatus = null;
        compBeanList.clear();
        if (compareChagnesList != null) {
            compareChagnesList.clear();
        }

        try {

            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getOffCodeSelected() == 0) {
                throw new VahanException("Something went wrong, Please try again...");
            }

            tmgr = new TransactionManager("saveChangedData");

            if (Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                    || Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {

                OwnerBean owner_bean = this.owner_bean;
                if (owner_bean != null) {
                    owner_bean.addToComapreChangesList(compBeanList);
                }

                FitnessBean fitness_bean = this.fitness_bean;

                if (fitness_bean != null && owner_bean.isRenderInspectionPanel() //this.workBench.getInspection_panel().isRendered()
                        ) {
                    fitness_bean.addToComapreChangesList(compBeanList);
                }

                InsBean ins_bean = this.ins_bean;
                if (ins_bean != null) {
                    ins_bean.addToComapreChangesList(compBeanList);
                }

                // Ex Army Details
                if (owner_bean.isExArmyVehicle_Visibility_tab()) {
                    ExArmyBean exArmyBean = this.exArmyBean;
                    if (exArmyBean != null) {
                        exArmyBean.addToComapreChangesList(compBeanList);
                    }
                }

                if (owner_bean.isImportedVehicle_Visibility_tab()) {
                    ImportedVehicleBean importedVehicle_Bean = this.importedVehicle_Bean;
                    if (importedVehicle_Bean != null) {
                        importedVehicle_Bean.addToComapreChangesList(compBeanList);
                    }
                }

                if (owner_bean.isAxleDetail_Visibility_tab()) {
                    AxleBean axleBean = this.axleBean;
                    if (axleBean != null) {
                        axleBean.addToComapreChangesList(compBeanList);
                    }
                }

                if (owner_bean.isCngDetails_Visibility_tab()) {
                    RetroFittingDetailsBean cNG_Details_Bean = this.cNG_Details_Bean;
                    if (cNG_Details_Bean != null) {
                        cNG_Details_Bean.addToComapreChangesList(compBeanList);
                    }
                }

                HpaBean hpa_bean = this.hpa_bean;
                if (hpa_bean != null) {
                    hpa_bean.addToComapreChangesList(compBeanList);
                }

                Trailer_bean trailer_bean = this.getTrailer_bean();
                if (trailer_bean != null) {
                    trailer_bean.addToComapreChangesList(compBeanList);
                }

                if (blnSave) {

                    Owner_dobj owner_dobj = owner_bean.set_Owner_appl_bean_to_dobj();
                    changedData = ComparisonBeanImpl.changedDataContents(compBeanList);
                    FitnessDobj fitness_dobj = null;
                    Date fit_dt = null;
                    if (fitness_bean != null) {
                        if (owner_bean.isRenderInspectionPanel()) {
                            fitness_dobj = (FitnessDobj) fitness_bean.makeDobjFromBean()[0];
                            fit_dt = fitness_dobj.getFit_chk_dt();

                            if (fitness_bean.getFitness_dobj_prv() == null || fitness_bean.getCompBeanList().size() > 0) {
                                ArrayList listFitparam = (ArrayList) fitness_bean.makeDobjFromBean()[1];
                                fitness_dobj.setAppl_no(owner_dobj.getAppl_no());
                                FitnessImpl.insertOrUpdateFitness(tmgr, owner_dobj.getRegn_no(), APPL_NO, fitness_dobj, listFitparam, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                            }
                        }

                    }

                    if (owner_bean.isRenderInspectionPanelNT()) {
                        FitnessImpl fitnessImpl = new FitnessImpl();
                        owner_dobj.getInspectionDobj().setFit_off_cd1(Integer.parseInt(sessionVariables.getEmpCodeLoggedIn()));
                        fitnessImpl.insertOrUpdateInspection(tmgr, owner_dobj.getInspectionDobj());
                    }

                    if (owner_dobj.getSpeedGovernorDobj() != null) {
                        FitnessImpl.insertUpdateVaSpeedGovernor(owner_dobj.getSpeedGovernorDobj(), tmgr);
                    } else {
                        FitnessImpl.insertIntoVhaSpeedGovernor(APPL_NO, tmgr);
                        FitnessImpl.deleteVaSpeedGovernor(APPL_NO, tmgr);
                    }

                    if (owner_dobj.getReflectiveTapeDobj() != null) {
                        new FitnessImpl().insertOrUpdateVaReflectiveTape(tmgr, owner_dobj.getReflectiveTapeDobj());
                    } else {
                        new FitnessImpl().insertIntoVhaReflectiveTape(tmgr, APPL_NO, Util.getEmpCode());
                        new FitnessImpl().deleteVaReflectiveTape(APPL_NO, tmgr);;
                    }


                    if (owner_bean.getOwner_dobj_prv() == null || owner_bean.getCompBeanList().size() > 0
                            || (owner_dobj.getFit_dt() != null && fit_dt != null && DateUtils.compareDates(owner_dobj.getFit_dt(), fit_dt) != 0)) {
                        if (fit_dt != null) {
                            owner_dobj.setFit_dt(fit_dt);
                        }
                        NewImpl.insertOrUpdateVaOwner(tmgr, owner_dobj);
                        if (Util.getUserStateCode().equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(owner_dobj.getVh_class()) + ",")) {
                            NewImpl.insertOrUpdateVaOwnerOther(tmgr, owner_dobj);
                        }
                    }

                    if (owner_dobj.getOtherStateVehDobj() != null) {
                        OtherStateVehImpl otherStateVehImpl = new OtherStateVehImpl();
                        otherStateVehImpl.insertUpdateOtherStateVeh(tmgr, owner_dobj.getOtherStateVehDobj());
                    }

                    if (owner_dobj.getCdDobj() != null) {
                        CdImpl cdImpl = new CdImpl();
                        cdImpl.insertUpdateVaCd(owner_dobj.getCdDobj(), tmgr);
                    }

                    InsDobj ins_dobj = ins_bean.set_InsBean_to_dobj();
                    owner_bean.validateInsuranceValidity(ins_dobj);
                    if (InsImpl.validateOwnerCodeWithInsType(owner_dobj.getOwner_cd(), ins_dobj.getIns_type())) {
                        if (ins_bean.getIns_dobj_prv() == null || ins_bean.getCompBeanList().size() > 0) {
                            InsImpl.insertUpdateInsurance(tmgr, APPL_NO, REGN_NO, ins_dobj, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                        }
                    } else {
                        throw new VahanException("Invalid Combination of Ownership Type & Insurance Type.");
                    }

                    if (owner_bean.isExArmyVehicle_Visibility_tab()) {
                        if (exArmyBean.getExArmy_dobj_prv() == null || exArmyBean.getCompBeanList().size() > 0) {
                            ExArmyDobj exArmyDobj = exArmyBean.setExArmyBean_To_Dobj();
                            ExArmyImpl.saveExArmyVehicleDetails_Impl(exArmyDobj, APPL_NO, tmgr);
                        }
                    } else {
                        ExArmyImpl.insertIntoVhaExArmy(tmgr, APPL_NO);
                        ExArmyImpl.deleteFromVaExArmy(tmgr, APPL_NO);
                    }

                    if (owner_bean.isImportedVehicle_Visibility_tab()) {
                        if (importedVehicle_Bean.getImp_dobj_prv() == null || importedVehicle_Bean.getCompBeanList().size() > 0) {
                            ImportedVehicleDobj importedVehicle_Dobj = importedVehicle_Bean.setBean_to_Dobj();
                            ImportedVehicleImpl.saveImportedDetails_Impl(importedVehicle_Dobj, APPL_NO, tmgr);
                        }
                    } else {
                        ImportedVehicleImpl.insertIntoVhaImpVeh(tmgr, APPL_NO);
                        ImportedVehicleImpl.deleteFromVaImp(tmgr, APPL_NO);
                    }

                    if (owner_bean.isAxleDetail_Visibility_tab()) {
                        if (axleBean.getAxle_dobj_prv() == null || axleBean.getCompBeanList().size() > 0) {
                            AxleDetailsDobj axleDetailsDobj = axleBean.setBean_To_Dobj();
                            AxleImpl.saveAxleDetails_Impl(axleDetailsDobj, APPL_NO, tmgr);
                        }
                    } else {
                        AxleImpl.insertIntoVhaAxle(tmgr, APPL_NO);
                        AxleImpl.deleteFromVaAxle(tmgr, APPL_NO);
                    }

                    if (owner_bean.isCngDetails_Visibility_tab()) {
                        if (cNG_Details_Bean.getCng_dobj_prv() == null || cNG_Details_Bean.getCompBeanList().size() > 0) {
                            RetroFittingDetailsDobj cNG_Details_Dobj = cNG_Details_Bean.setBean_To_Dobj();
                            RetroFittingDetailsImpl.saveCngVehicleDetails_Impl(cNG_Details_Dobj, APPL_NO, tmgr);
                        }
                    } else {
                        RetroFittingDetailsImpl.insertIntoVhaCng(tmgr, APPL_NO);
                        RetroFittingDetailsImpl.deleteFromVaRetroFittingDetails(tmgr, APPL_NO);
                    }

                    //**********Save Advanced Number Booking
                    if (owner_bean.getAdvanceRegNoDobj() != null) {
                        owner_bean.getAdvanceRegNoDobj().setRegn_appl_no(APPL_NO);
                        NewImpl.updateAdvanceRegNoDetails(owner_bean.getAdvanceRegNoDobj(), owner_dobj, tmgr);
                    }

                    if (trailer_bean.getTrailer_dobj_prv() == null || trailer_bean.getCompBeanList().size() > 0) {
                        Trailer_dobj trailer_dobj = trailer_bean.setTrailerBeanToDobj();
                        if (trailer_dobj != null && !CommonUtils.isNullOrBlank(trailer_dobj.getChasi_no())) {
                            Trailer_Impl.insertUpdateTrailer(tmgr, APPL_NO, REGN_NO, owner_dobj.getChasi_no(), trailer_dobj);
                        }
                    }

                    if (!CommonUtils.isNullOrBlank(APPL_NO)
                            && owner_dobj.getNewLoiNo() != null
                            && owner_bean.tmConfDobj.isNew_reg_loi()
                            && PUR_CD.equalsIgnoreCase(String.valueOf(TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE))
                            && owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_NEW)
                            && owner_dobj.getVh_class() == 57) {
                        PermitImpl permitImpl = new PermitImpl();
                        permitImpl.saveNewLoiDetails(tmgr, APPL_NO, owner_dobj.getNewLoiNo(), REGN_NO);
                    }

                    FeeImpl feeImpl = new FeeImpl();
                    boolean isVehicleHypothecated = feeImpl.isHypothecated(APPL_NO, Integer.parseInt(PUR_CD));
                    HpaDobj hpa_dobj = null;
                    if (this.workBench.isHypothecated()) {
                        if (hpa_bean.getHpa_dobj_prv() == null || hpa_bean.getCompBeanList().size() > 0) {

                            hpa_dobj = hpa_bean.getHpaDobj();
                            hpa_dobj.setAppl_no(APPL_NO); //
                            hpa_dobj.setRegn_no(REGN_NO);
                            ArrayList lis = new ArrayList();
                            lis.add(hpa_dobj);
                            HpaImpl.insertUpdateHPA(tmgr, lis, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected()); // use hpa_entry_bean
                            if (isVehicleHypothecated == false && hpa_dobj != null) {
                                hypothecationStatus = "HP details inserted";
                            }
                        }
                    } else {
                        HpaImpl.insertDeleteFromVaHpa(tmgr, APPL_NO);
                        if (isVehicleHypothecated == true && hpa_dobj == null) {
                            hypothecationStatus = "HP details deleted";
                        }
                    }

                    if (hypothecationStatus != null && !hypothecationStatus.equals("")) {
                        changedData = changedData + hypothecationStatus;
                    }

                    if (workBench.isShowBankDetails()) {
                        new PendencyBankDetailImpl().saveOrUpdatePendencyBankDtls(tmgr, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), workBench.getBankSubsidyDetailsDobj(owner_dobj, workBench.getBankSubsidyDetail()), sessionVariables.getEmpCodeLoggedIn());
                    }
                    
                    owner_bean.validateTempRegistrationData(owner_dobj, Integer.parseInt(PUR_CD), owner_dobj.getRegn_type());

                    if (compBeanList.size() > 0 || changedData != null && !changedData.equals("")) {
                        ComparisonBeanImpl.updateChangedData(owner_dobj.getAppl_no(), changedData, tmgr);
                    }
                    tmgr.commit();
                }

            } else if (Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {

                OwnerBean owner_bean = this.owner_bean;
                if (owner_bean != null) {
                    owner_bean.addToComapreChangesList(compBeanList);
                }

                FitnessBean fitness_bean = this.fitness_bean;

                if (fitness_bean != null && owner_bean.isRenderInspectionPanel()) {
                    fitness_bean.addToComapreChangesList(compBeanList);
                }

                InsBean ins_bean = this.ins_bean;
                if (ins_bean != null) {
                    ins_bean.addToComapreChangesList(compBeanList);
                }

//                Ex Army Details
                if (owner_bean.isExArmyVehicle_Visibility_tab()) {
                    ExArmyBean exArmyBean = this.exArmyBean;
                    if (exArmyBean != null) {
                        exArmyBean.addToComapreChangesList(compBeanList);
                    }
                }

                if (owner_bean.isImportedVehicle_Visibility_tab()) {
                    ImportedVehicleBean importedVehicle_Bean = this.importedVehicle_Bean;
                    if (importedVehicle_Bean != null) {
                        importedVehicle_Bean.addToComapreChangesList(compBeanList);
                    }
                }

                if (owner_bean.isAxleDetail_Visibility_tab()) {
                    AxleBean axleBean = this.axleBean;
                    if (axleBean != null) {
                        axleBean.addToComapreChangesList(compBeanList);
                    }
                }

                if (owner_bean.isCngDetails_Visibility_tab()) {
                    RetroFittingDetailsBean cNG_Details_Bean = this.cNG_Details_Bean;
                    if (cNG_Details_Bean != null) {
                        cNG_Details_Bean.addToComapreChangesList(compBeanList);
                    }
                }

                HpaBean hpa_bean = this.hpa_bean;
                if (hpa_bean != null) {
                    hpa_bean.addToComapreChangesList(compBeanList);
                }

                if (blnSave) {

                    Owner_dobj owner_dobj = owner_bean.set_Owner_appl_bean_to_dobj();
                    changedData = ComparisonBeanImpl.changedDataContents(compBeanList);
                    FitnessDobj fitness_dobj = null;
                    Date fit_dt = null;
                    if (fitness_bean != null) {

                        if (owner_bean.isRenderInspectionPanel()) {

                            fitness_dobj = (FitnessDobj) fitness_bean.makeDobjFromBean()[0];
                            fit_dt = fitness_dobj.getFit_chk_dt();

                            if (fitness_bean.getFitness_dobj_prv() == null || fitness_bean.getCompBeanList().size() > 0) {
                                ArrayList listFitparam = (ArrayList) fitness_bean.makeDobjFromBean()[1];
                                fitness_dobj.setAppl_no(owner_dobj.getAppl_no());
                                FitnessImpl.insertOrUpdateFitness(tmgr, owner_dobj.getRegn_no(), APPL_NO, fitness_dobj, listFitparam, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                            }
                        }

                    }

                    if (owner_bean.isRenderInspectionPanelNT()) {
                        FitnessImpl fitnessImpl = new FitnessImpl();
                        owner_dobj.getInspectionDobj().setFit_off_cd1(Integer.parseInt(sessionVariables.getEmpCodeLoggedIn()));
                        fitnessImpl.insertOrUpdateInspection(tmgr, owner_dobj.getInspectionDobj());
                    }

                    if (owner_bean.getOwner_dobj_prv() == null || owner_bean.getCompBeanList().size() > 0
                            || (owner_dobj.getFit_dt() != null && fit_dt != null && DateUtils.compareDates(owner_dobj.getFit_dt(), fit_dt) != 0)) {

                        if (fit_dt != null) {
                            owner_dobj.setFit_dt(fit_dt);
                        }
                        NewImpl.insertOrUpdateVaOwner(tmgr, owner_dobj);
                    }

                    if (owner_dobj.getSpeedGovernorDobj() != null) {
                        FitnessImpl.insertUpdateVaSpeedGovernor(owner_dobj.getSpeedGovernorDobj(), tmgr);
                    } else {
                        FitnessImpl.insertIntoVhaSpeedGovernor(APPL_NO, tmgr);
                        FitnessImpl.deleteVaSpeedGovernor(APPL_NO, tmgr);
                    }


                    InsDobj ins_dobj = ins_bean.set_InsBean_to_dobj();
                    if (InsImpl.validateOwnerCodeWithInsType(owner_dobj.getOwner_cd(), ins_dobj.getIns_type())) {
                        if (ins_bean.getIns_dobj_prv() == null || ins_bean.getCompBeanList().size() > 0) {
                            InsImpl.insertUpdateInsurance(tmgr, APPL_NO, REGN_NO, ins_dobj, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                        }
                    } else {
                        throw new VahanException("Invalid Combination of Ownership Type & Insurance Type.");
                    }

                    if (owner_bean.isExArmyVehicle_Visibility_tab()) {
                        if (exArmyBean.getExArmy_dobj_prv() == null || exArmyBean.getCompBeanList().size() > 0) {
                            ExArmyDobj exArmyDobj = exArmyBean.setExArmyBean_To_Dobj();
                            ExArmyImpl.saveExArmyVehicleDetails_Impl(exArmyDobj, APPL_NO, tmgr);
                        }
                    }

                    if (owner_bean.isImportedVehicle_Visibility_tab()) {
                        if (importedVehicle_Bean.getImp_dobj_prv() == null || importedVehicle_Bean.getCompBeanList().size() > 0) {
                            ImportedVehicleDobj importedVehicle_Dobj = importedVehicle_Bean.setBean_to_Dobj();
                            ImportedVehicleImpl.saveImportedDetails_Impl(importedVehicle_Dobj, APPL_NO, tmgr);
                        }
                    }

                    if (owner_bean.isAxleDetail_Visibility_tab()) {
                        if (axleBean.getAxle_dobj_prv() == null || axleBean.getCompBeanList().size() > 0) {
                            AxleDetailsDobj axleDetailsDobj = axleBean.setBean_To_Dobj();
                            AxleImpl.saveAxleDetails_Impl(axleDetailsDobj, APPL_NO, tmgr);
                        }
                    }

                    if (owner_bean.isCngDetails_Visibility_tab()) {
                        if (cNG_Details_Bean.getCng_dobj_prv() == null || cNG_Details_Bean.getCompBeanList().size() > 0) {
                            RetroFittingDetailsDobj cNG_Details_Dobj = cNG_Details_Bean.setBean_To_Dobj();
                            RetroFittingDetailsImpl.saveCngVehicleDetails_Impl(cNG_Details_Dobj, APPL_NO, tmgr);
                        }
                    }

                    FeeImpl feeImpl = new FeeImpl();
                    boolean isVehicleHypothecated = feeImpl.isHypothecated(APPL_NO, Integer.parseInt(PUR_CD));
                    if (this.workBench.isHypothecated()) {
                        if (hpa_bean.getHpa_dobj_prv() == null || hpa_bean.getCompBeanList().size() > 0) {
                            hpa_bean.getHpaDobj().setAppl_no(APPL_NO);
                            hpa_bean.getHpaDobj().setRegn_no(REGN_NO);
                            ArrayList lis = new ArrayList();
                            lis.add(hpa_bean.getHpaDobj());
                            HpaImpl.insertUpdateHPA(tmgr, lis, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected()); // use hpa_entry_bean
                            if (isVehicleHypothecated == false && hpa_bean.getHpaDobj() != null) {
                                hypothecationStatus = "HP details inserted";
                            }
                        }
                    } else {
                        HpaImpl.insertDeleteFromVaHpa(tmgr, APPL_NO);
                        if (isVehicleHypothecated == true && hpa_bean.getHpaDobj() == null) {
                            hypothecationStatus = "HP details deleted";
                        }
                    }

                    if (hypothecationStatus != null && !hypothecationStatus.equals("")) {
                        changedData = changedData + hypothecationStatus;
                    }

                    if (compBeanList.size() > 0 || changedData != null && !changedData.equals("")) {
                        ComparisonBeanImpl.updateChangedData(owner_dobj.getAppl_no(), changedData, tmgr);
                    }
                    tmgr.commit();
                }

            }

            // if no changes done by user then data will not saved
            if (compBeanList.size() <= 0) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "No changed made by you", "No changed made by you"));
                return return_file;
            }

        } catch (VahanException vex) {
            e = vex;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        } catch (Exception ex) {
            e = ex;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Problem while comparing the changes.", "Problem while comparing the changes."));
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

        if (e != null) {
            return null;
        }

        return return_file;
    }

    private String validateHPAEntry(HpaDobj hpaDobj) {
        String errorMsg = "";
        if (hpaDobj == null) {
            return errorMsg = "HPA Details are blank";
        }
        if (CommonUtils.isNullOrBlank(hpaDobj.getRegn_no())) {
            errorMsg = "Blank Registration Number";
        } else if (CommonUtils.isNullOrBlank(hpaDobj.getHp_type())) {
            errorMsg = "Blank HP Type";
        } else if (CommonUtils.isNullOrBlank(hpaDobj.getFncr_name())) {
            errorMsg = "Blank Financer Name";
        } else if (CommonUtils.isNullOrBlank(hpaDobj.getFncr_add1())) {
            errorMsg = "Blank Financer Address";
        } else if (CommonUtils.isNullOrBlank(hpaDobj.getFncr_state())) {
            errorMsg = "Blank Financer State";
        } else if (hpaDobj.getFncr_district() == 0) {
            errorMsg = "Blank Financer District";
        } else if (hpaDobj.getFncr_pincode() == null || hpaDobj.getFncr_pincode() == 0) {
            errorMsg = "Blank Financer Pin Code";
        }
        return errorMsg;
    }

    private String validateExArmyForm(ExArmyBean exArmyBean) {
        String errorMsg = "";
        if (exArmyBean.getTf_Voucher_no().equalsIgnoreCase("")) {
            errorMsg = "Blank Voucher number.";
        } else if (exArmyBean.getTf_VoucherDate() == null) {
            errorMsg = "Blank Voucher Date.";
        } else if (exArmyBean.getTf_POP().equalsIgnoreCase("")) {
            errorMsg = "Blank Place of Purchase.";
        }
        return errorMsg;
    }

    private String validateImpVehicleForm(ImportedVehicleBean importedVehicle_Bean) {
        String errorMsg = "";
        if (importedVehicle_Bean.getCm_country_imp() == 0) {
            errorMsg = "Blank Country Name.";
        } else if (importedVehicle_Bean.getTf_dealer_imp().equalsIgnoreCase("")) {
            errorMsg = "Blank Dealer Name.";
        } else if (importedVehicle_Bean.getTf_foreign_imp().equalsIgnoreCase("")) {
            errorMsg = "Blank Foreign Registration Number.";
        } else if (importedVehicle_Bean.getTf_place_imp().equalsIgnoreCase("")) {
            errorMsg = "Blank Place of RTO Office/Purchase.";
        } else if (importedVehicle_Bean.getTf_YOM_imp() == null) {
            errorMsg = "Blank Year of Manufacture.";
        }
        return errorMsg;
    }

    private String validateAxleForm(AxleBean axleBean) {
        String errorMsg = "";
        if (axleBean.getTf_Front1().equalsIgnoreCase("")) {
            errorMsg = "Blank Front Axle Detail";
        } else if (axleBean.getTf_Rear1().equalsIgnoreCase("")) {
            errorMsg = "Blank Rear Axle Detail";
        } else if (axleBean.getTf_Front() == null) {
            errorMsg = "Blank Front Axle Detail";
        } else if (axleBean.getTf_Rear() == null) {
            errorMsg = "Blank Rear Axle Detail";
        }
        return errorMsg;
    }

    private String validateCngForm(RetroFittingDetailsBean cNG_Details_Bean) {
        String errorMsg = "";
        if (CommonUtils.isNullOrBlank(cNG_Details_Bean.getTf_kit_no())) {
            errorMsg = "Blank Kit Number Detail";
        } else if (CommonUtils.isNullOrBlank(cNG_Details_Bean.getTf_kit_type())) {
            errorMsg = "Blank Kit Type Detail";
        } else if (CommonUtils.isNullOrBlank(cNG_Details_Bean.getTf_workshop())) {
            errorMsg = "Blank Workshop Detail";
        } else if (CommonUtils.isNullOrBlank(cNG_Details_Bean.getTf_license_no())) {
            errorMsg = "Blank Workshop License Number";
        } else if (CommonUtils.isNullOrBlank(cNG_Details_Bean.getTf_manu_name())) {
            errorMsg = "Blank Manufacture Name";
        } else if (CommonUtils.isNullOrBlank(cNG_Details_Bean.getTf_cyc_sr_no())) {
            errorMsg = "Blank Cyclinder Sr. Number";
        } else if (CommonUtils.isNullOrBlank(cNG_Details_Bean.getTf_poll_norms())) {
            errorMsg = "Blank Pollution Norms Details";
        } else if (CommonUtils.isNullOrBlank(cNG_Details_Bean.getTf_approv_lettr_no())) {
            errorMsg = "Blank Approval Letter Number";

        } else if (cNG_Details_Bean.getCal_approv_dt() == null) {
            errorMsg = "Blank Approval Date";
        }
        return errorMsg;
    }

    public String getFields() {
        return fields;
    }

    /**
     * @author Deependra Singh
     */
    public void showPartialSavedDetails() {
        String baseUrl = VahanUtil.getBaseUrl();
        Map<String, String> saveDetails = new TreeMap<String, String>();
        // Setting the object to be sent to the controller
        OwnerBeanModel ownerModel = new OwnerBeanModel();
        if (this.owner_bean.getVehType() != null) {
            ownerModel.setVehType(this.owner_bean.getVehType());
        }
        ownerModel.setSale_amt(this.owner_bean.getSale_amt());
        ownerModel.setOwnerDobj(this.owner_bean.getOwnerDobj());
        ownerModel.getOwnerDobj().setVch_catg(this.owner_bean.getOwnerDobj().getVch_catg());
        ownerModel.setList_vm_catg(this.owner_bean.getList_vm_catg());
        ownerModel.setList_vh_class(this.owner_bean.getList_vh_class());
        ownerModel.getOwnerDobj().setVh_class(this.owner_bean.getOwnerDobj().getVh_class());

        final String ROOT_URI = baseUrl + "/save/final";

        // Calling the controller
        saveDetails = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build()
                .post()
                .uri(ROOT_URI)
                .body(Mono.just(ownerModel), OwnerBeanModel.class)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        //Populating the instance variable object whose values are displayed in the UI
        entryDetails = new ArrayList<>(saveDetails.entrySet());
        if (!entryDetails.isEmpty()) {
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().executeScript("PF('entryDetails_dlg1').show()");
        }
    }

    public void sendOtpAndVerifyOwnerMobileNo(String otpType) throws VahanException {
        String otp_msg = "";
        long mobileNo = owner_bean.getOwner_identification().getMobile_no();
        String mobileno = String.valueOf(mobileNo);
        tmConfDobj = Util.getTmConfiguration();
        try {
            if (tmConfDobj != null && tmConfDobj.getTmConfigOtpDobj() != null && !CommonUtils.isNullOrBlank(mobileno)
                    && (tmConfDobj.getTmConfigOtpDobj().isOwner_mobile_verify_with_otp()
                    && !CommonUtils.isNullOrBlank(otpType) && (ACTION_CODE == TableConstants.TM_ROLE_DEALER_NEW_APPL || ACTION_CODE == TableConstants.TM_ROLE_NEW_APPL
                    || ACTION_CODE == TableConstants.TM_ROLE_DEALER_TEMP_APPL || ACTION_CODE == TableConstants.TM_ROLE_NEW_APPL_TEMP))
                    || (!CommonUtils.isNullOrBlank(mobileNoCountMessage))) {
                switch (otpType) {
                    case "sendOtp":
                        setOwnerMobileVerifyOtp(new ServerUtil().genOTP(mobileno));
                        otp_msg = getOwnerMobileVerifyOtp() + ": OTP for Owner Mobile-No Verification at the time of New Registration. Valid for one time use. Please share with concerned RTO/Dealer staff only.";
                        ServerUtil.sendOTP(mobileno, otp_msg, Util.getUserStateCode());
                        PrimeFaces.current().ajax().update("workbench_tabview:otp_confirmation");
                        PrimeFaces.current().ajax().update("workbench_tabview:otp_dialog");
                        PrimeFaces.current().executeScript("PF('otp_confrm').show()");
                        break;
                    case "resendOtp":
                        if (!CommonUtils.isNullOrBlank(ownerMobileVerifyOtp)) {
                            otp_msg = getOwnerMobileVerifyOtp() + ": OTP for Owner Mobile-No Verification. Valid for one time use. Please share with concerned RTO/Dealer staff only.";
                            ServerUtil.sendOTP(mobileno, otp_msg, Util.getUserStateCode());
                        } else {
                            setOwnerMobileVerifyOtp(new ServerUtil().genOTP(mobileno));
                            otp_msg = getOwnerMobileVerifyOtp() + ": OTP for Owner Mobile-No Verification. Valid for one time use. Please share with concerned RTO/Dealer staff only.";
                            ServerUtil.sendOTP(mobileno, otp_msg, Util.getUserStateCode());
                        }
                        break;
                    case "confirmOtp":
                        if (getOwnerMobileVerifyOtp().equals(getEnteredOwnerMobVerifyOtp())) {
                            mobileNoCountMessage = null;
                            save_ActionListener();
                            //PrimeFaces.current().ajax().update("workbench_tabview:otp_text workbench_tabview:otp_confirmation");
                            PrimeFaces.current().executeScript("PF('otp_confrm').hide()");
                        } else {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Invalid OTP, Please enter correct OTP", "Invalid OTP, Please enter correct OTP"));

                        }
                        break;
                    default:
                        break;
                }
            } else {
                if (tmConfDobj != null && tmConfDobj.getTmConfigOtpDobj() != null && tmConfDobj.getTmConfigOtpDobj().isMobile_no_count_with_otp()) {
                    mobileNoCountMessage = ServerUtil.getMobileNoCountMessage(mobileNo);
                    if (!CommonUtils.isNullOrBlank(mobileNoCountMessage) && (ACTION_CODE == TableConstants.TM_ROLE_DEALER_NEW_APPL || ACTION_CODE == TableConstants.TM_ROLE_NEW_APPL)) {
                        setOwnerMobileVerifyOtp(new ServerUtil().genOTP(mobileno));
                        otp_msg = getOwnerMobileVerifyOtp() + ": OTP for Owner Mobile-No Verification at the time of New Registration. Valid for one time use. Please share with concerned RTO/Dealer staff only.";
                        ServerUtil.sendOTP(mobileno, otp_msg, Util.getUserStateCode());
                        PrimeFaces.current().ajax().update("workbench_tabview:otp_confirmation");
                        PrimeFaces.current().ajax().update("workbench_tabview:otp_dialog");
                        PrimeFaces.current().executeScript("PF('otp_confrm').show()");
                    } else {
                        save_ActionListener();
                    }
                } else {
                    save_ActionListener();
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));

        }
    }

    public void resetRegardingOTPMsg() {
        mobileNoCountMessage = null;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(String fields) {
        this.fields = fields;
    }

    /**
     * @return the old_value
     */
    public String getOld_value() {
        return old_value;
    }

    /**
     * @param old_value the old_value to set
     */
    public void setOld_value(String old_value) {
        this.old_value = old_value;
    }

    /**
     * @return the new_value
     */
    public String getNew_value() {
        return new_value;
    }

    /**
     * @param new_value the new_value to set
     */
    public void setNew_value(String new_value) {
        this.new_value = new_value;
    }

    /**
     * @return the compBeanList
     */
    public ArrayList<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    /**
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(ArrayList<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    /**
     * @return the changed_data
     */
    public String getChanged_data() {
        return changed_data;
    }

    /**
     * @param changed_data the changed_data to set
     */
    public void setChanged_data(String changed_data) {
        this.changed_data = changed_data;
    }

    /**
     * @return the user
     */
    public int getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(int user) {
        this.user = user;
    }

    /**
     * @return the prevChangedDataList
     */
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    /**
     * @param prevChangedDataList the prevChangedDataList to set
     */
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    /**
     * @return the changedByPrevUser
     */
    public Panel getChangedByPrevUser() {
        return changedByPrevUser;
    }

    /**
     * @param changedByPrevUser the changedByPrevUser to set
     */
    public void setChangedByPrevUser(Panel changedByPrevUser) {
        this.changedByPrevUser = changedByPrevUser;
    }

    /**
     * @return the op_dt
     */
    public String getOp_dt() {
        return op_dt;
    }

    /**
     * @param op_dt the op_dt to set
     */
    public void setOp_dt(String op_dt) {
        this.op_dt = op_dt;
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
     * @return the compareChagnesList
     */
    public ArrayList<ComparisonBean> getCompareChagnesList() {
        return compareChagnesList;
    }

    /**
     * @param compareChagnesList the compareChagnesList to set
     */
    public void setCompareChagnesList(ArrayList<ComparisonBean> compareChagnesList) {
        this.compareChagnesList = compareChagnesList;
    }

    /**
     * @param owner_bean the owner_bean to set
     */
    public void setOwner_bean(OwnerBean owner_bean) {
        this.owner_bean = owner_bean;
    }

    /**
     * @param ins_bean the ins_bean to set
     */
    public void setIns_bean(InsBean ins_bean) {
        this.ins_bean = ins_bean;
    }

    /**
     * @param hpa_bean the hpa_bean to set
     */
    public void setHpa_bean(HpaBean hpa_bean) {
        this.hpa_bean = hpa_bean;
    }

    /**
     * @param fitness_bean the fitness_bean to set
     */
    public void setFitness_bean(FitnessBean fitness_bean) {
        this.fitness_bean = fitness_bean;
    }

    /**
     * @param workBench the workBench to set
     */
    public void setWorkBench(WorkBench workBench) {
        this.workBench = workBench;
    }

    /**
     *
     * @param exArmyBean
     */
    public void setExArmyBean(ExArmyBean exArmyBean) {
        this.exArmyBean = exArmyBean;
    }

    public void setImportedVehicle_Bean(ImportedVehicleBean importedVehicle_Bean) {
        this.importedVehicle_Bean = importedVehicle_Bean;
    }

    public void setAxleBean(AxleBean axleBean) {
        this.axleBean = axleBean;
    }

    /**
     * @param cNG_Details_Bean the cNG_Details_Bean to set
     */
    public void setcNG_Details_Bean(RetroFittingDetailsBean cNG_Details_Bean) {
        this.cNG_Details_Bean = cNG_Details_Bean;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the printDisclaimerButton
     */
    public boolean isPrintDisclaimerButton() {
        return printDisclaimerButton;
    }

    /**
     * @param printDisclaimerButton the printDisclaimerButton to set
     */
    public void setPrintDisclaimerButton(boolean printDisclaimerButton) {
        this.printDisclaimerButton = printDisclaimerButton;
    }

    /**
     * @return the okButton
     */
    public boolean isOkButton() {
        return okButton;
    }

    /**
     * @param okButton the okButton to set
     */
    public void setOkButton(boolean okButton) {
        this.okButton = okButton;
    }

    /**
     * @return the entryDetails
     */
    public List<Entry<String, String>> getEntryDetails() {
        return entryDetails;
    }

    /**
     * @param entryDetails the entryDetails to set
     */
    public void setEntryDetails(List<Entry<String, String>> entryDetails) {
        this.entryDetails = entryDetails;
    }

    /**
     * @return the trailer_bean
     */
    public Trailer_bean getTrailer_bean() {
        return trailer_bean;
    }

    /**
     * @param trailer_bean the trailer_bean to set
     */
    public void setTrailer_bean(Trailer_bean trailer_bean) {
        this.trailer_bean = trailer_bean;
    }

    /**
     * @return the ownerMobileVerifyOtp
     */
    public String getOwnerMobileVerifyOtp() {
        return ownerMobileVerifyOtp;
    }

    /**
     * @param ownerMobileVerifyOtp the ownerMobileVerifyOtp to set
     */
    public void setOwnerMobileVerifyOtp(String ownerMobileVerifyOtp) {
        this.ownerMobileVerifyOtp = ownerMobileVerifyOtp;
    }

    /**
     * @return the enteredOwnerMobVerifyOtp
     */
    public String getEnteredOwnerMobVerifyOtp() {
        return enteredOwnerMobVerifyOtp;
    }

    /**
     * @param enteredOwnerMobVerifyOtp the enteredOwnerMobVerifyOtp to set
     */
    public void setEnteredOwnerMobVerifyOtp(String enteredOwnerMobVerifyOtp) {
        this.enteredOwnerMobVerifyOtp = enteredOwnerMobVerifyOtp;
    }

    public String getMobileNoCountMessage() {
        return mobileNoCountMessage;
    }

    public void setMobileNoCountMessage(String mobileNoCountMessage) {
        this.mobileNoCountMessage = mobileNoCountMessage;
    }

    /*
     * Author: kartikey Singh
     * Added to get values in WrapperModel
     */
    public int getACTION_CODE() {
        return ACTION_CODE;
    }

    /*
     * Author: kartikey Singh
     * Added to get values in WrapperModel
     */
    public long getEmp_Cd() {
        return Emp_Cd;
    }

    /*
     * Author: kartikey Singh
     * Added to get values in WrapperModel
     */
    public String getCOUNTER_ID() {
        return COUNTER_ID;
    }
}
