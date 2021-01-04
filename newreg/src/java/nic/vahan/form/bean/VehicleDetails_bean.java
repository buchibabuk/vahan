/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.Dealer;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.State;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.ExArmyDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.ImportedVehicleDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OtherStateVehDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.PuccDobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.SCDetailsDobj;
import nic.vahan.form.dobj.TempRegDobj;
import nic.vahan.form.dobj.Trailer_dobj;
import nic.vahan.form.dobj.common.RegistrationStatusParametersDobj;
import nic.vahan.form.dobj.eChallan.ChallanReportDobj;
import nic.vahan.form.dobj.hsrp.HSRP_dobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.dobj.pucc.TmConfigurationPUCCdobj;
import nic.vahan.form.impl.ApplRegStatus_Impl;
import nic.vahan.form.impl.ApplicationInwardImpl;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.ExArmyImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.HSRPDetailsImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.ImportedVehicleImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.InsPuccUpdateImpl;
import nic.vahan.form.impl.OtherStateVehImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.RetroFittingDetailsImpl;
import nic.vahan.form.impl.SmartCardImpl;
import nic.vahan.form.impl.TempRegImpl;
import nic.vahan.form.impl.Trailer_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.VehicleTrackingDetailsImpl;
import nic.vahan.form.impl.eChallan.SaveChallanImpl;
import nic.vahan.form.impl.permit.PermitDetailImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import nic.vahan.services.DmsDocCheckUtils;
import nic.vahan.services.VTDocumentModel;
import nic.vahan.services.clients.NcrbWsClient;
import nic.vahan.services.insurance.InsuranceDetailService;
import nic.vahan.services.insurance.dobj.InsuranceInfoDobj;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.primefaces.PrimeFaces;

@ViewScoped
@ManagedBean(name = "vehicleDetails")
public class VehicleDetails_bean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(VehicleDetails_bean.class);
    private OwnerDetailsDobj ownerDetail;
    @ManagedProperty(value = "#{trailer_bean}")
    private Trailer_bean trailer_bean;
    @ManagedProperty(value = "#{ins_bean}")
    private InsBean ins_bean;
    @ManagedProperty(value = "#{hypothecationDetails_bean}")
    private HypothecationDetailsBean hypothecationDetails_bean;
    @ManagedProperty(value = "#{exArmyBean}")
    private ExArmyBean exArmyBean;
    @ManagedProperty(value = "#{axleBean}")
    private AxleBean axleBean;
    private String regn_no;
    private String status;
    private boolean showStatus = false;
    private boolean render = false;
    private FitnessDobj fitnessDobj = null;
    @ManagedProperty(value = "#{hsrp_bean}")
    private HSRPDetailsBean hsrpDetailsBean;
    @ManagedProperty(value = "#{smartcard_bean}")
    private SCDetailsBean scDetailsBean;
    @ManagedProperty(value = "#{vehicleHistory_bean}")
    private VehicleHistoryDetailsBean vehicleHistoryDetailsBean;
    @ManagedProperty(value = "#{challanDetails}")
    private ChallanDetailsBean challanDetails;
    @ManagedProperty(value = "#{permitDtls}")
    private PermitDisplayDetailBean permitDetailBean;
    @ManagedProperty(value = "#{tccReportBean}")
    private TccReportBean tccReportBean;
    private RegistrationStatusParametersDobj regStatus = new RegistrationStatusParametersDobj();
    private boolean smartcardStatus = false;
    private boolean hsrpStatus = false;
    private String searchByValue = "regnNo";
    private String engineNo;
    private String chassisNo;
    private ArrayList<OwnerDetailsDobj> regnNameList = null;
    private boolean showRegList = false;
    private boolean showBack = false;
    private List listOwnerCatg = new ArrayList();
    private boolean isPermitVehicle;
    private boolean documentUploadShow = false;
    private String APPL_NO;
    @ManagedProperty(value = "#{documentUploadBean}")
    private DocumentUploadBean documentUpload_bean;
    private ArrayList<ChallanReportDobj>[] challanReportDobj = new ArrayList[2];
    private ArrayList<ChallanReportDobj> pendinglist = new ArrayList<>();
    private List<ChallanReportDobj> challanlist = new ArrayList<>();
    private ArrayList<ChallanReportDobj> disposelist = new ArrayList<>();
    //For TempRegn
    private TempRegDobj tempReg = new TempRegDobj();
    private boolean blnRegnTypeTemp = false;
    private List list_c_state = new ArrayList();
    private List list_office_to = new ArrayList();
    private List list_dealer_cd = new ArrayList();
    private List officeList = new ArrayList();
    private List stateList = new ArrayList();
    private OtherStateVehDobj otherStateVehDobj;
    private boolean renderTmpRegnDtls;
    private boolean renderOtherStateRegnDtls;
    //for Cng
    private boolean cngDetails_Visibility_tab;
    private RetroFittingDetailsBean retroFittingDetailsBean = new RetroFittingDetailsBean();
    //imported Vehicle
    private ImportedVehicleBean importedVehicle_Bean = new ImportedVehicleBean();
    private boolean importedVehicleVisibilitytab = false;
    private Date regnDate;
    private Date regnUpto;
    private Date fitupto;
    private String vehicleStatusAsPerNCRBDatabase = "";
    private Owner_dobj ownerDobj = new Owner_dobj();
    private Owner_dobj vltdOwnerDobj = null;
    private List listSpeedGovTypes;
    private boolean renderSpeedGov = false;
    private PuccDobj puccDobj = null;
    private String puccNo;
    private String puccUpto;
    private String puccCentreNo;
    private String puccFrom;
    private boolean showNCRBData = false;
    private boolean showPUCCData = false;
    private FitnessDobj tempFitnessDetails = null;
    private boolean renderVltdDialog = false;

    //
    //end
    public VehicleDetails_bean() {
        officeList.clear();
        list_c_state.clear();
        list_office_to.clear();
        list_dealer_cd.clear();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("redirectTo", "documentsStatus");
        list_c_state = MasterTableFiller.getStateList();
    }

    public void showEntryPanel() {
        showRegList = false;
        regn_no = "";
        chassisNo = "";
        engineNo = "";
    }

    public void showRegnFromEng() {
        if (this.engineNo.trim() == null || this.engineNo.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Please Enter Valid Engine Number"));
            return;
        }
        try {
            OwnerImpl owner_Impl = new OwnerImpl();
            regnNameList = (ArrayList<OwnerDetailsDobj>) owner_Impl.getRegnFromEngNo(this.engineNo.trim());
            if (regnNameList.isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Engine No. Not Found"));
                return;
            }
            showRegList = true;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void showRegnFromChassis() {
        if (this.chassisNo.trim() == null || this.chassisNo.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Please Enter Valid Chassis Number"));
            return;
        }
        try {
            OwnerImpl owner_Impl = new OwnerImpl();
            regnNameList = (ArrayList<OwnerDetailsDobj>) owner_Impl.getRegnFromChassisNo(this.chassisNo.trim());
            if (regnNameList.isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Chassis No. Not Found"));
                return;
            }
            showRegList = true;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void getDetails(OwnerDetailsDobj dobj) {
        showBack = true;
        this.regn_no = dobj.getRegn_no();
        showDetails(dobj.getState_cd(), dobj.getOff_cd());
    }

    public void back() {
        render = false;
    }

    public void showAllRegnNos() {

        if (this.regn_no == null || this.regn_no.trim().equalsIgnoreCase("") || this.regn_no.trim().equalsIgnoreCase("NEW")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Please Enter Valid Registration Number"));
            return;
        }

        try {
            OwnerImpl owner_Impl = new OwnerImpl();
            String reassign = owner_Impl.verifyInVhReAssgin(this.regn_no.trim());
            if (reassign != null && !reassign.trim().equalsIgnoreCase("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", reassign));
                return;
            }
            regnNameList = (ArrayList<OwnerDetailsDobj>) owner_Impl.getAllRegnNoFromAllStatesAndRto(this.regn_no.trim());
            if (regnNameList.isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration No. Not Found"));
                return;
            }
            if (regnNameList.size() > 1) {
                showRegList = true;
            } else {
                for (int i = 0; i < regnNameList.size(); i++) {
                    showDetails(regnNameList.get(i).getState_cd(), regnNameList.get(i).getOff_cd());
                }

            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void showDetails(String stateCd, int offCd) {
        list_office_to.clear();
        list_dealer_cd.clear();
        officeList.clear();
        setRenderOtherStateRegnDtls(false);
        setRenderTmpRegnDtls(false);
        setCngDetails_Visibility_tab(false);
        setImportedVehicleVisibilitytab(false);
        setBlnRegnTypeTemp(false);

        Exception ex = null;
        Trailer_Impl trailer_Impl = new Trailer_Impl();
        if (this.regn_no == null || this.regn_no.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Please Enter Valid Registration Number"));
            return;
        }
        try {

            //Master Filler for Owner Category
            String[][] data = MasterTableFiller.masterTables.VM_OWCATG.getData();
            for (int i = 0; i < data.length; i++) {
                listOwnerCatg.add(new SelectItem(data[i][0], data[i][1]));
            }
            OwnerImpl owner_Impl = new OwnerImpl();
            ownerDetail = owner_Impl.getOwnerDetailsWithOffice(this.regn_no.trim(), stateCd, offCd);

            if (ownerDetail == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number Not Found"));
                return;
            }/* else if (!Util.getUserStateCode().equalsIgnoreCase(owner_dobj.getState_cd())) {
             PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number Not Found"));
             return;
             }*/

            Long user_cd = Long.parseLong(Util.getEmpCode());
            String userCatg = Util.getUserCategory();
            if (userCatg != null) {
                if (userCatg.equals(TableConstants.User_Dealer) || userCatg.equals(TableConstants.USER_CATG_DEALER_ADMIN)) {
                    Map makerAndDealerDetail = OwnerImpl.getDealerDetail(user_cd);
                    if (!ownerDetail.getDealer_cd().equals(makerAndDealerDetail.get("dealer_cd"))) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Not a valid Dealer to see the registered vehicle Details."));
                        return;
                    }
                }
            }

            //**Start--for setting checking if there is any temporary fitness exist or not
            FitnessImpl fitnessImpl = new FitnessImpl();
            tempFitnessDetails = fitnessImpl.getVtFitnessTempDetails(regn_no);
            //**End

            //for Owner Identification Fields disallow typing
            if (ownerDetail.getOwnerIdentity() != null) {
                ownerDetail.getOwnerIdentity().setFlag(true);
                ownerDetail.getOwnerIdentity().setMobileNoEditable(true);
                ownerDetail.getOwnerIdentity().setOwnerCatgEditable(true);
            }

            if (ownerDetail.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_EXARMY)) {
                ExArmyDobj dobj = ExArmyImpl.setExArmyDetails_db_to_dobj(null, regn_no, ownerDetail.getState_cd(), ownerDetail.getOff_cd());
                if (dobj != null) {
                    exArmyBean.setDobj_To_Bean(dobj);
                }
            }
            setListSpeedGovTypes(OwnerImpl.getListSpeedGovTypes());
            ownerDobj = (owner_Impl.getVtOwnerDetailsForRegnNoStateCdOffCd(this.regn_no.trim(), stateCd, offCd));
            vltdOwnerDobj = ownerDobj;
            if (ownerDobj.getSpeedGovernorDobj() != null) {
                renderSpeedGov = true;
            }

            if (ownerDetail.getVehTypeDescr().equalsIgnoreCase(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR)) {
                AxleDetailsDobj dobj = AxleImpl.setAxleVehDetails_db_to_dobj(null, regn_no, ownerDetail.getState_cd(), ownerDetail.getOff_cd());
                if (dobj != null) {
                    axleBean.setDobj_To_Bean(dobj);
                }
                FitnessImpl fitImpl = new FitnessImpl();
                fitnessDobj = fitImpl.set_Fitness_appl_db_to_dobj_withStateCdOffCd(regn_no, null, ownerDetail.getState_cd(), ownerDetail.getOff_cd());
            }

            smartcardStatus = SmartCardImpl.isSmartCard(ownerDetail.getState_cd(), ownerDetail.getOff_cd());
            if (hsrpDetailsBean != null) {
                HSRP_dobj hSRP_dobj = hsrpDetailsBean.setRegNo(this.regn_no.trim(), ownerDetail.getState_cd(), ownerDetail.getOff_cd());
                if (hSRP_dobj != null) {
                    hsrpStatus = true;
                } else {
                    hsrpStatus = false;
                }
            }

            if (scDetailsBean != null) {
                SCDetailsDobj sCDetailsDobj = scDetailsBean.setRegNo(this.regn_no.trim(), ownerDetail.getState_cd(), ownerDetail.getOff_cd(), smartcardStatus);
                if (sCDetailsDobj != null) {
                    smartcardStatus = true;
                } else {
                    smartcardStatus = false;
                }
            }

            if (vehicleHistoryDetailsBean != null) {
                vehicleHistoryDetailsBean.setRegNo(this.regn_no.trim(), ownerDetail.getVh_class(), ownerDetail.getState_cd(), ownerDetail.getOff_cd());
            }
            if (getChallanDetails() != null) {
                getChallanDetails().setRegNo(this.regn_no.trim());
            }
            if (permitDetailBean != null && ownerDetail.getVehType() == TableConstants.VM_VEHTYPE_TRANSPORT) {
                isPermitVehicle = permitDetailBean.setRegNo(this.regn_no.trim());
            }
            if (tccReportBean != null) {
                tccReportBean.setRegNo(this.regn_no.trim(), ownerDetail.getVh_class(), ownerDetail.getState_cd(), ownerDetail.getOff_cd());
            }
            Trailer_dobj trailer_dobj = trailer_Impl.set_trailer_dtls_to_dobj("", this.regn_no.trim(), 0);
            if (trailer_dobj != null) {
                trailer_bean.set_trailer_dobj_to_bean(trailer_dobj);
            }

            HpaImpl hpa_Impl = new HpaImpl();
            List<HpaDobj> hypth = hpa_Impl.getHypothecationList(this.regn_no.trim(), ownerDetail.getState_cd(), ownerDetail.getOff_cd());
            hypothecationDetails_bean.setListHypthDetails(hypth);

            regStatus = ServerUtil.fillRegStatusParameters(ownerDetail);

            if (!isPermitVehicle) {//for permit surrender status
                PermitDetailDobj permitDetailDobj = PermitDetailImpl.getPermitSurrenderDetails(regn_no, stateCd);
                if (permitDetailDobj != null) {
                    regStatus.setPermitSurrenderStatus(permitDetailDobj.getTransactionPurCdDescr());//permit surrender reason
                }
            }
            challanReportDobj = SaveChallanImpl.fetchDetailsOfChallan(ownerDetail.getRegn_no());
            ApplicationInwardImpl applicationInwardImpl = new ApplicationInwardImpl();
            challanlist = applicationInwardImpl.getEChallanInfo(ownerDetail.getRegn_no());
            if (challanReportDobj != null) {
                pendinglist = challanReportDobj[0];
                disposelist = challanReportDobj[1];
            }
            //AddFor TempRegn
            if (ownerDetail.getRegn_type().equals(TableConstants.VT_VEH_CATG_TEMP_REG)) {
                TempRegDobj tempRegDetls = new TempRegImpl().getVtTempRegnDtl(regn_no, ownerDetail.getState_cd(), ownerDetail.getOff_cd());
                if (tempRegDetls != null) {
                    setTempReg(tempRegDetls);
//                    setBlnRegnTypeTemp(true);

                    String stateTempTo = getTempReg() != null ? getTempReg().getTmp_state_cd() : null;
                    //OWNER_CODE_INDIVIDUAL
                    if (stateTempTo != null) {
                        list_office_to.clear();
                        String[][] data1 = MasterTableFiller.masterTables.TM_OFFICE.getData();
                        for (int i = 0; i < data1.length; i++) {
                            if (data1[i][13].equals(stateTempTo)) {
                                list_office_to.add(new SelectItem(data1[i][0], data1[i][1]));
                            }
                        }
                    }

                    int off_cd = getTempReg() != null ? getTempReg().getTmp_off_cd() : 0;
                    if (off_cd != 0) {
                        List<Dealer> listDealer = ServerUtil.getDealerList(getTempReg().getTmp_state_cd(), getTempReg().getTmp_off_cd());
                        if (listDealer == null || listDealer.isEmpty()) {
                            listDealer = ServerUtil.getDealerList(stateCd, offCd);
                        }
                        list_dealer_cd.clear();
                        for (Dealer dealer : listDealer) {
                            list_dealer_cd.add(new SelectItem(dealer.getDealer_cd(), dealer.getDealer_name()));
                        }

                    }

                    setRenderTmpRegnDtls(true);

                }
            }
            otherStateVehDobj = new OtherStateVehImpl().setVTOtherVehicleDetailsToDobj(regn_no);
            if (otherStateVehDobj != null) {
                stateList = MasterTableFiller.getStateList();
                State oldState = MasterTableFiller.state.get(otherStateVehDobj.getOldStateCD());
                if (oldState != null) {
                    officeList = oldState.getOffice();
                }
                if (!CommonUtils.isNullOrBlank(ownerDetail.getRegn_dt())) {//dd/MM/yyyy
                    Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(ownerDetail.getRegn_dt());
                    setRegnDate(date1);
                }
                if (!CommonUtils.isNullOrBlank(ownerDetail.getFit_upto())) {//dd/MM/yyyy
                    Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(ownerDetail.getFit_upto());
                    setFitupto(date2);
                }
                if (!CommonUtils.isNullOrBlank(ownerDetail.getRegn_upto())) {//dd/MM/yyyy
                    Date date3 = new SimpleDateFormat("yyyy-MM-dd").parse(ownerDetail.getRegn_upto());
                    setRegnUpto(date3);
                }
                setRenderOtherStateRegnDtls(true);

            }

            //Cng Details
            RetroFittingDetailsDobj cng_dobj = RetroFittingDetailsImpl.setCngDetails_db_to_dobj_VT(regn_no, ownerDetail.getState_cd(), ownerDetail.getOff_cd());
            if (cng_dobj != null) {
                retroFittingDetailsBean.setDobj_To_Bean(cng_dobj);
                setCngDetails_Visibility_tab(true);
            }

            //imported Vehicle
            ImportedVehicleDobj imp_Dobj = null;
            if (TableConstants.VM_REGN_TYPE_IMPORTED_YES.equalsIgnoreCase(ownerDetail.getImported_vch())) {
                imp_Dobj = new ImportedVehicleImpl().setImpVehDetails_db_to_dobj(null, regn_no, ownerDetail.getState_cd(), ownerDetail.getOff_cd());
                if (imp_Dobj != null) {
                    importedVehicle_Bean.setDobj_to_Bean(imp_Dobj);
                    setImportedVehicleVisibilitytab(true);
                }

            }

            VehicleTrackingDetailsImpl vehicleTrackingDetailsImpl = new VehicleTrackingDetailsImpl();
            if (ownerDobj != null && ownerDobj.getChasi_no() != null) {
                vltdOwnerDobj.setVehicleTrackingDetailsDobj(vehicleTrackingDetailsImpl.getVLTDDetailsByRegnNo(ownerDobj.getRegn_no(), ownerDobj.getState_cd(), ownerDobj.getOff_cd()));
                if (vltdOwnerDobj.getVehicleTrackingDetailsDobj() != null) {
                    setRenderVltdDialog(true);
                } else {
                    vltdOwnerDobj.setVehicleTrackingDetailsDobj(vehicleTrackingDetailsImpl.getVehicleTrackingDetailsByChasiOrRegn_no(null, ownerDobj.getRegn_no()));
                    if (vltdOwnerDobj.getVehicleTrackingDetailsDobj() != null) {
                        setRenderVltdDialog(true);
                    } else {
                        vltdOwnerDobj.setVehicleTrackingDetailsDobj(vehicleTrackingDetailsImpl.getVehicleTrackingDetailsByChasiOrRegn_no(ownerDobj.getChasi_no(), null));
                        if (vltdOwnerDobj.getVehicleTrackingDetailsDobj() != null) {
                            setRenderVltdDialog(true);
                        } else {
                            setRenderVltdDialog(false);
                        }
                    }
                }
            }

            if (isRenderOtherStateRegnDtls() || isRenderTmpRegnDtls() || isCngDetails_Visibility_tab() || isImportedVehicleVisibilitytab()) {

                setBlnRegnTypeTemp(true);
            }
            ins_bean.componentReadOnly(false);
            ins_bean.setGovtVehFlag(true);
            trailer_bean.componentReadOnly(false);
            render = true;

            // Uploaded Document View
            if (Util.getTmConfiguration() != null && Util.getTmConfiguration().getTmConfigDmsDobj() != null && Util.getUserStateCode() != null && Util.getUserSeatOffCode() != null && Util.getUserStateCode().equals(ownerDetail.getState_cd()) && ((Util.getUserSeatOffCode() == ownerDetail.getOff_cd() && TableConstants.USER_CATG_OFFICE_ADMIN.equals(Util.getUserCategory()) && (Util.getTmConfiguration().getTmConfigDmsDobj().getDocUploadAllotedOff().contains("ALL") || Util.getTmConfiguration().getTmConfigDmsDobj().getDocUploadAllotedOff().contains("," + Util.getUserSeatOffCode() + ","))) || TableConstants.USER_CATG_STATE_ADMIN.equals(Util.getUserCategory()))) {
                getDocumentUploadApplNo(ownerDetail.getRegn_no(), stateCd, offCd);
            }
        } catch (VahanException ve) {
            ex = ve;
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage()));
        } catch (Exception exp) {
            ex = exp;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", TableConstants.SomthingWentWrong));
        }
    }

    public void getDocumentUploadApplNo(String regnNo, String stateCd, int offCd) throws Exception {
        List<VTDocumentModel> uploadDocList = new ArrayList<>();
        String appl_no = ApplRegStatus_Impl.getApplNoByRegnNo(regnNo, stateCd, offCd, Util.getUserCategory());
        if (!CommonUtils.isNullOrBlank(appl_no)) {
            uploadDocList = DmsDocCheckUtils.getUploadedDocumentList(appl_no);
        }
        if (!uploadDocList.isEmpty() && uploadDocList.size() > 0) {
            documentUpload_bean.setApplNo(appl_no);
            documentUploadShow = true;
        } else if ("PB".equals(stateCd)) {
            boolean isFlag = ApplRegStatus_Impl.checkVahanBacklogRegnNo(regnNo, stateCd, offCd, Util.getUserCategory());
            if (isFlag) {
                documentUpload_bean.setApplNo(regnNo);
                documentUploadShow = true;
            }
        }
    }

    public void updateNCRBData() {
        if (ownerDetail != null) {
            getNCRBData();
        }
    }

    public void updateInsuranceData() {
        if (ownerDetail != null) {
            getInsuranceData();
        }
    }

    public void updatePuccData() {
        if (ownerDetail != null) {
            getPuccData();
        }
    }

    public void getInsuranceData() {
        try {
            InsuranceDetailService detailService = new InsuranceDetailService();
            InsDobj ins_dobj = detailService.getInsuranceDetailsByService(regn_no, ownerDetail.getState_cd(), ownerDetail.getOff_cd());
            if (ins_dobj != null) {
                ins_bean.set_Ins_dobj_to_bean(ins_dobj);
                ownerDetail.setInsDobj(ins_dobj);

            } else {
                ins_dobj = InsImpl.set_ins_dtls_db_to_dobj(this.regn_no.trim(), null, null, 0);
                if (ins_dobj != null) {
                    ins_bean.set_Ins_dobj_to_bean(ins_dobj);
                    ownerDetail.setInsDobj(ins_dobj);

                }
            }
            if (ins_dobj != null) {
                ownerDetail.setInsDobj(ins_dobj);

                InsImpl insImpl = new InsImpl();
                if (InsImpl.validateOwnerCodeWithInsType(ownerDetail.getOwner_cd(), ownerDetail.getInsDobj().getIns_type())) {
                    if (insImpl.validateInsurance(ownerDetail.getInsDobj())) {
                        regStatus.setInsuranceStatus(true);
                    }
                }
                //RequestContext context = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("ptabid");//Update 
                PrimeFaces.current().ajax().update("panelOwnerInfo");
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", TableConstants.SomthingWentWrong));
        }
    }

    public void getNCRBData() {
        try {
            NcrbWsClient ncrbWS = new NcrbWsClient();
            vehicleStatusAsPerNCRBDatabase = ncrbWS.callNcrbService(this.regn_no.trim());
            if (!vehicleStatusAsPerNCRBDatabase.isEmpty()) {
                showNCRBData = true;
                //RequestContext context = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("ptabid");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", TableConstants.SomthingWentWrong));
        }
    }

    public void getPuccData() {
        InsPuccUpdateImpl puccUpdateImpl = new InsPuccUpdateImpl();
        puccDobj = null;
        OwnerImpl ownerImpl = new OwnerImpl();
        setOwnerDobj(ownerImpl.getOwnerDobj(ownerDetail));
        try {
            VehicleParameters vehParameters = null;
            if (getOwnerDobj() != null) {
                vehParameters = fillVehicleParametersFromDobj(getOwnerDobj());
            }
            TmConfigurationPUCCdobj configurationPUCCdobj = puccUpdateImpl.getTmConfigurationPUCCdobj(ownerDetail.getState_cd());
            if (configurationPUCCdobj != null
                    && configurationPUCCdobj.getExpired_pucc_check() != null
                    && vehParameters != null && configurationPUCCdobj.getExpired_pucc_check() != null
                    && isCondition(replaceTagValues(configurationPUCCdobj.getExpired_pucc_check(), vehParameters), "saveApplicationNo")) {
                puccDobj = puccUpdateImpl.getPuccDetails(regn_no);
                if (puccDobj != null) {
                    SimpleDateFormat date = new SimpleDateFormat("dd-MMM-yyyy");
                    puccCentreNo = puccDobj.getPuccCentreno();
                    puccNo = puccDobj.getPuccNo();
                    if (puccDobj.getPuccFrom() != null) {
                        Date puccFromDt = new SimpleDateFormat("dd-MM-yyyy").parse(DateUtils.parseDate(puccDobj.getPuccFrom()));
                        puccFrom = date.format(puccFromDt);
                    }
                    if (puccDobj.getPuccUpto() != null) {
                        Date puccUptoDt = new SimpleDateFormat("dd-MM-yyyy").parse(DateUtils.parseDate(puccDobj.getPuccUpto()));
                        puccUpto = date.format(puccUptoDt);
                    }
                    setShowPUCCData(true);
                    // RequestContext context = RequestContext.getCurrentInstance();
                    PrimeFaces.current().ajax().update("ptabid");//Update
                }

            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", TableConstants.SomthingWentWrong));
        }
    }

    /**
     * @return the regn_no
     */
    public String getRegn_no() {
        return regn_no;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    /**
     * @return the render
     */
    public boolean isRender() {
        return render;
    }

    /**
     * @param render the render to set
     */
    public void setRender(boolean render) {
        this.render = render;
    }

    /**
     * @param trailer_bean the trailer_bean to set
     */
    public void setTrailer_bean(Trailer_bean trailer_bean) {
        this.trailer_bean = trailer_bean;
    }

    /**
     * @param ins_bean the ins_bean to set
     */
    public void setIns_bean(InsBean ins_bean) {
        this.ins_bean = ins_bean;
    }

    /**
     * @param hypothecationDetails_bean the hypothecationDetails_bean to set
     */
    public void setHypothecationDetails_bean(HypothecationDetailsBean hypothecationDetails_bean) {
        this.hypothecationDetails_bean = hypothecationDetails_bean;
    }

    /**
     * @return the ownerDetail
     */
    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    /**
     * @param ownerDetail the ownerDetail to set
     */
    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    /**
     * @param exArmyBean the exArmyBean to set
     */
    public void setExArmyBean(ExArmyBean exArmyBean) {
        this.exArmyBean = exArmyBean;
    }

    /**
     * @param axleBean the axleBean to set
     */
    public void setAxleBean(AxleBean axleBean) {
        this.axleBean = axleBean;
    }

    /**
     * @return the fitnessDobj
     */
    public FitnessDobj getFitnessDobj() {
        return fitnessDobj;
    }

    /**
     * @param fitnessDobj the fitnessDobj to set
     */
    public void setFitnessDobj(FitnessDobj fitnessDobj) {
        this.fitnessDobj = fitnessDobj;
    }

    /**
     * @return the hsrpDetailsBean
     */
    public HSRPDetailsBean getHsrpDetailsBean() {
        return hsrpDetailsBean;
    }

    /**
     * @param hsrpDetailsBean the hsrpDetailsBean to set
     */
    public void setHsrpDetailsBean(HSRPDetailsBean hsrpDetailsBean) {
        this.hsrpDetailsBean = hsrpDetailsBean;
    }

    /**
     * @return the scDetailsBean
     */
    public SCDetailsBean getScDetailsBean() {
        return scDetailsBean;
    }

    /**
     * @param scDetailsBean the scDetailsBean to set
     */
    public void setScDetailsBean(SCDetailsBean scDetailsBean) {
        this.scDetailsBean = scDetailsBean;
    }

    /**
     * @return the vehicleHistoryDetailsBean
     */
    public VehicleHistoryDetailsBean getVehicleHistoryDetailsBean() {
        return vehicleHistoryDetailsBean;
    }

    /**
     * @param vehicleHistoryDetailsBean the vehicleHistoryDetailsBean to set
     */
    public void setVehicleHistoryDetailsBean(VehicleHistoryDetailsBean vehicleHistoryDetailsBean) {
        this.vehicleHistoryDetailsBean = vehicleHistoryDetailsBean;
    }

    /**
     * @return the showStatus
     */
    public boolean isShowStatus() {
        return showStatus;
    }

    /**
     * @param showStatus the showStatus to set
     */
    public void setShowStatus(boolean showStatus) {
        this.showStatus = showStatus;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the regStatus
     */
    public RegistrationStatusParametersDobj getRegStatus() {
        return regStatus;
    }

    /**
     * @param regStatus the regStatus to set
     */
    public void setRegStatus(RegistrationStatusParametersDobj regStatus) {
        this.regStatus = regStatus;
    }

    /**
     * @return the permitDetailBean
     */
    public PermitDisplayDetailBean getPermitDetailBean() {
        return permitDetailBean;
    }

    /**
     * @param permitDetailBean the permitDetailBean to set
     */
    public void setPermitDetailBean(PermitDisplayDetailBean permitDetailBean) {
        this.permitDetailBean = permitDetailBean;
    }

    /**
     * @return the smartcardStatus
     */
    public boolean isSmartcardStatus() {
        return smartcardStatus;
    }

    /**
     * @param smartcardStatus the smartcardStatus to set
     */
    public void setSmartcardStatus(boolean smartcardStatus) {
        this.smartcardStatus = smartcardStatus;
    }

    /**
     * @return the hsrpStatus
     */
    public boolean isHsrpStatus() {
        return hsrpStatus;
    }

    /**
     * @param hsrpStatus the hsrpStatus to set
     */
    public void setHsrpStatus(boolean hsrpStatus) {
        this.hsrpStatus = hsrpStatus;
    }

    /**
     * @return the searchByValue
     */
    public String getSearchByValue() {
        return searchByValue;
    }

    /**
     * @param searchByValue the searchByValue to set
     */
    public void setSearchByValue(String searchByValue) {
        this.searchByValue = searchByValue;
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
     * @return the chassisNo
     */
    public String getChassisNo() {
        return chassisNo;
    }

    /**
     * @param chassisNo the chassisNo to set
     */
    public void setChassisNo(String chassisNo) {
        this.chassisNo = chassisNo;
    }

    /**
     * @return the regnNameList
     */
    public ArrayList<OwnerDetailsDobj> getRegnNameList() {
        return regnNameList;
    }

    /**
     * @param regnNameList the regnNameList to set
     */
    public void setRegnNameList(ArrayList<OwnerDetailsDobj> regnNameList) {
        this.regnNameList = regnNameList;
    }

    /**
     * @return the showRegList
     */
    public boolean isShowRegList() {
        return showRegList;
    }

    /**
     * @param showRegList the showRegList to set
     */
    public void setShowRegList(boolean showRegList) {
        this.showRegList = showRegList;
    }

    /**
     * @return the showBack
     */
    public boolean isShowBack() {
        return showBack;
    }

    /**
     * @param showBack the showBack to set
     */
    public void setShowBack(boolean showBack) {
        this.showBack = showBack;
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
     * @return the isPermitVehicle
     */
    public boolean isIsPermitVehicle() {
        return isPermitVehicle;
    }

    /**
     * @param isPermitVehicle the isPermitVehicle to set
     */
    public void setIsPermitVehicle(boolean isPermitVehicle) {
        this.isPermitVehicle = isPermitVehicle;
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
     * @return the tccReportBean
     */
    public TccReportBean getTccReportBean() {
        return tccReportBean;
    }

    /**
     * @param tccReportBean the tccReportBean to set
     */
    public void setTccReportBean(TccReportBean tccReportBean) {
        this.tccReportBean = tccReportBean;
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
     * @return the challanReportDobj
     */
    public ArrayList<ChallanReportDobj>[] getChallanReportDobj() {
        return challanReportDobj;
    }

    /**
     * @param challanReportDobj the challanReportDobj to set
     */
    public void setChallanReportDobj(ArrayList<ChallanReportDobj>[] challanReportDobj) {
        this.challanReportDobj = challanReportDobj;
    }

    /**
     * @return the pendinglist
     */
    public ArrayList<ChallanReportDobj> getPendinglist() {
        return pendinglist;
    }

    /**
     * @param pendinglist the pendinglist to set
     */
    public void setPendinglist(ArrayList<ChallanReportDobj> pendinglist) {
        this.pendinglist = pendinglist;
    }

    /**
     * @return the disposelist
     */
    public ArrayList<ChallanReportDobj> getDisposelist() {
        return disposelist;
    }

    /**
     * @param disposelist the disposelist to set
     */
    public void setDisposelist(ArrayList<ChallanReportDobj> disposelist) {
        this.disposelist = disposelist;
    }

    public TempRegDobj getTempReg() {
        return tempReg;
    }

    public void setTempReg(TempRegDobj tempReg) {
        this.tempReg = tempReg;
    }

    public boolean isBlnRegnTypeTemp() {
        return blnRegnTypeTemp;
    }

    public void setBlnRegnTypeTemp(boolean blnRegnTypeTemp) {
        this.blnRegnTypeTemp = blnRegnTypeTemp;
    }

    public List getList_c_state() {
        return list_c_state;
    }

    public void setList_c_state(List list_c_state) {
        this.list_c_state = list_c_state;
    }

    public List getList_office_to() {
        return list_office_to;
    }

    public void setList_office_to(List list_office_to) {
        this.list_office_to = list_office_to;
    }

    public List getList_dealer_cd() {
        return list_dealer_cd;
    }

    public void setList_dealer_cd(List list_dealer_cd) {
        this.list_dealer_cd = list_dealer_cd;
    }

    public List getOfficeList() {
        return officeList;
    }

    public void setOfficeList(List officeList) {
        this.officeList = officeList;
    }

    public List getStateList() {
        return stateList;
    }

    public void setStateList(List stateList) {
        this.stateList = stateList;
    }

    public OtherStateVehDobj getOtherStateVehDobj() {
        return otherStateVehDobj;
    }

    public void setOtherStateVehDobj(OtherStateVehDobj otherStateVehDobj) {
        this.otherStateVehDobj = otherStateVehDobj;
    }

    public boolean isRenderTmpRegnDtls() {
        return renderTmpRegnDtls;
    }

    public void setRenderTmpRegnDtls(boolean renderTmpRegnDtls) {
        this.renderTmpRegnDtls = renderTmpRegnDtls;
    }

    public boolean isRenderOtherStateRegnDtls() {
        return renderOtherStateRegnDtls;
    }

    public void setRenderOtherStateRegnDtls(boolean renderOtherStateRegnDtls) {
        this.renderOtherStateRegnDtls = renderOtherStateRegnDtls;
    }

    public boolean isCngDetails_Visibility_tab() {
        return cngDetails_Visibility_tab;
    }

    public void setCngDetails_Visibility_tab(boolean cngDetails_Visibility_tab) {
        this.cngDetails_Visibility_tab = cngDetails_Visibility_tab;
    }

    public RetroFittingDetailsBean getRetroFittingDetailsBean() {
        return retroFittingDetailsBean;
    }

    public void setRetroFittingDetailsBean(RetroFittingDetailsBean retroFittingDetailsBean) {
        this.retroFittingDetailsBean = retroFittingDetailsBean;
    }

    public ImportedVehicleBean getImportedVehicle_Bean() {
        return importedVehicle_Bean;
    }

    public void setImportedVehicle_Bean(ImportedVehicleBean importedVehicle_Bean) {
        this.importedVehicle_Bean = importedVehicle_Bean;
    }

    public boolean isImportedVehicleVisibilitytab() {
        return importedVehicleVisibilitytab;
    }

    public void setImportedVehicleVisibilitytab(boolean importedVehicleVisibilitytab) {
        this.importedVehicleVisibilitytab = importedVehicleVisibilitytab;
    }

    public Date getRegnDate() {
        return regnDate;
    }

    public void setRegnDate(Date regnDate) {
        this.regnDate = regnDate;
    }

    public Date getRegnUpto() {
        return regnUpto;
    }

    public void setRegnUpto(Date regnUpto) {
        this.regnUpto = regnUpto;
    }

    public Date getFitupto() {
        return fitupto;
    }

    public void setFitupto(Date fitupto) {
        this.fitupto = fitupto;
    }

    /**
     * @return the vehicleStatusAsPerNCRBDatabase
     */
    public String getVehicleStatusAsPerNCRBDatabase() {
        return vehicleStatusAsPerNCRBDatabase;
    }

    /**
     * @param vehicleStatusAsPerNCRBDatabase the vehicleStatusAsPerNCRBDatabase
     * to set
     */
    public void setVehicleStatusAsPerNCRBDatabase(String vehicleStatusAsPerNCRBDatabase) {
        this.vehicleStatusAsPerNCRBDatabase = vehicleStatusAsPerNCRBDatabase;
    }

    /**
     * @return the puccDobj
     */
    public PuccDobj getPuccDobj() {
        return puccDobj;
    }

    /**
     * @param puccDobj the puccDobj to set
     */
    public void setPuccDobj(PuccDobj puccDobj) {
        this.puccDobj = puccDobj;
    }

    /**
     * @return the puccNo
     */
    public String getPuccNo() {
        return puccNo;
    }

    /**
     * @param puccNo the puccNo to set
     */
    public void setPuccNo(String puccNo) {
        this.puccNo = puccNo;
    }

    /**
     * @return the puccUpto
     */
    public String getPuccUpto() {
        return puccUpto;
    }

    /**
     * @param puccUpto the puccUpto to set
     */
    public void setPuccUpto(String puccUpto) {
        this.puccUpto = puccUpto;
    }

    /**
     * @return the puccCentreNo
     */
    public String getPuccCentreNo() {
        return puccCentreNo;
    }

    /**
     * @param puccCentreNo the puccCentreNo to set
     */
    public void setPuccCentreNo(String puccCentreNo) {
        this.puccCentreNo = puccCentreNo;
    }

    /**
     * @return the puccFrom
     */
    public String getPuccFrom() {
        return puccFrom;
    }

    /**
     * @param puccFrom the puccFrom to set
     */
    public void setPuccFrom(String puccFrom) {
        this.puccFrom = puccFrom;
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
     * @return the showNCRBData
     */
    public boolean isShowNCRBData() {
        return showNCRBData;
    }

    /**
     * @param showNCRBData the showNCRBData to set
     */
    public void setShowNCRBData(boolean showNCRBData) {
        this.showNCRBData = showNCRBData;
    }

    /**
     * @return the showPUCCData
     */
    public boolean isShowPUCCData() {
        return showPUCCData;
    }

    /**
     * @param showPUCCData the showPUCCData to set
     */
    public void setShowPUCCData(boolean showPUCCData) {
        this.showPUCCData = showPUCCData;
    }

    public List<ChallanReportDobj> getChallanlist() {
        return challanlist;
    }

    public void setChallanlist(List<ChallanReportDobj> challanlist) {
        this.challanlist = challanlist;
    }

    /**
     * @return the challanDetails
     */
    public ChallanDetailsBean getChallanDetails() {
        return challanDetails;
    }

    /**
     * @param challanDetails the challanDetails to set
     */
    public void setChallanDetails(ChallanDetailsBean challanDetails) {
        this.challanDetails = challanDetails;
    }

    public FitnessDobj getTempFitnessDetails() {
        return tempFitnessDetails;
    }

    public void setTempFitnessDetails(FitnessDobj tempFitnessDetails) {
        this.tempFitnessDetails = tempFitnessDetails;
    }

    /**
     * @return the renderVltdDialog
     */
    public boolean isRenderVltdDialog() {
        return renderVltdDialog;
    }

    /**
     * @param renderVltdDialog the renderVltdDialog to set
     */
    public void setRenderVltdDialog(boolean renderVltdDialog) {
        this.renderVltdDialog = renderVltdDialog;
    }

    /**
     * @return the vltdOwnerDobj
     */
    public Owner_dobj getVltdOwnerDobj() {
        return vltdOwnerDobj;
    }

    /**
     * @param vltdOwnerDobj the vltdOwnerDobj to set
     */
    public void setVltdOwnerDobj(Owner_dobj vltdOwnerDobj) {
        this.vltdOwnerDobj = vltdOwnerDobj;
    }
}
