/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.admin;

import dao.UserDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.db.Dealer;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.State;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.AxleBean;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.DocumentUploadBean;
import nic.vahan.form.bean.ExArmyBean;
import nic.vahan.form.bean.HpaBean;
import nic.vahan.form.bean.ImportedVehicleBean;
import nic.vahan.form.bean.InsBean;
import nic.vahan.form.bean.OwnerBean;
import nic.vahan.form.bean.RetroFittingDetailsBean;
import nic.vahan.form.bean.Trailer_bean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.ExArmyDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.ImportedVehicleDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OtherStateVehDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Owner_temp_dobj;
import nic.vahan.form.dobj.ReflectiveTapeDobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.SpeedGovernorDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TempRegDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.Trailer_dobj;
import nic.vahan.form.dobj.smartcard.SmartCardDobj;
import nic.vahan.form.impl.ApplicationInwardImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.NewImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.SmsMailOTPImpl;
import nic.vahan.form.impl.Trailer_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.admin.OwnerAdminImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.primefaces.PrimeFaces;
import nic.vahan.form.dobj.DocumentDetailsDobj;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "ownerAdmin")
@ViewScoped
public class RegistrationOwnerAdminBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private boolean renderHypth = false;
    private boolean isHypth = false;
    @ManagedProperty(value = "#{owner_bean}")
    private OwnerBean ownerBean;
    private InsBean insBean = new InsBean();
    private HpaBean hpaBean = new HpaBean();
    private OtherStateVehDobj otherStateVehDobj = new OtherStateVehDobj();
    @ManagedProperty(value = "#{retroFittingDetailsBean}")
    private RetroFittingDetailsBean cNG_Details_Bean;
    @ManagedProperty(value = "#{importedVehicleBean}")
    private ImportedVehicleBean importedVehicle_Bean;
    @ManagedProperty(value = "#{axleBean}")
    private AxleBean axleBean;
    @ManagedProperty(value = "#{exArmyBean}")
    private ExArmyBean exArmyBean;
    private List stateList = new ArrayList();
    private List officeList = new ArrayList();
    private List<ChangesByUser> listPreviousChanges = new ArrayList<>();
    private boolean otherStateVehicle = false;
    private boolean otherDistrictVehicle = false;
    private boolean renderTab = false;
    private List<ComparisonBean> listChanges = new ArrayList<>();
    //private List<ComparisonBean> opt_remark_list = new ArrayList<ComparisonBean>();
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private boolean disableRegnNo = false;
    private int regVehType = 1;
    private String regVehTypeDescr = "";
    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(RegistrationOwnerAdminBean.class);
    private String remarks;
    private FitnessDobj fitnessDobj = null;
    private SmartCardDobj smartCardDobj = null;
    private boolean remarksFlag;
    private String admin_Remarks;
    private String prv_admin_Remarks;
    private String APPL_NO;
    private SessionVariables sessionVariables = null;
    private boolean render_vehicle_type = false;
    private OwnerIdentificationDobj owner_identity_prev;
    private boolean flag_first_time_inward;
    private boolean render_btm;
    private boolean render_file_move;
    private List<ComparisonBean> prevChangedDataList = new ArrayList<>();
    private String disposeRcptOtp = null;
    private String enterRcptOtp = null;
    private boolean prev_renderSpeedGov = false;
    private boolean prev_renderReflectiveTape = false;
    private RegistrationOwnerAdminDobj registrationOwnerAdminDobj = null;
    private Status_dobj status_approve;
    private String applnoPrevTempRegn = "";
    private String prevremarks = "";
    private boolean temporyRegnDLRFlag;
    private boolean trailer_tab = true;
    @ManagedProperty(value = "#{trailer_bean}")
    private Trailer_bean trailer_bean;
    private String vahanMessages = null;
    private int tmpOffCode;
    private String tmpStateCode;
    private String hpRemarks;
    private boolean forwardToApplNo;
    private boolean forwardApplNoChckRender;
    @ManagedProperty(value = "#{documentUploadBean}")
    private DocumentUploadBean documentUploadBean;
    private boolean documentUploadShow = false;
    private String msgforStateAdmin = "I hereby confirm that this application will go to State Admin for final approval";

    @PostConstruct
    public void init() {
        //add
        try {
            regVehTypeDescr = Util.getLocaleMsg("registerVeh");
            vahanMessages = null;
            listPreviousChanges.clear();
            flag_first_time_inward = false;
            OwnerAdminImpl adminImpl = new OwnerAdminImpl();
            this.APPL_NO = appl_details.getAppl_no();
            sessionVariables = new SessionVariables();
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || (sessionVariables.getOffCodeSelected() == 0 && !Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN))//forstateadmin
                    || sessionVariables.getEmpCodeLoggedIn() == null) {
                vahanMessages = Util.getLocaleSessionMsg();
                return;
            }
            if (APPL_NO != null) {
                if (sessionVariables.getSelectedWork().getAction_cd() == TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_VERIFY || sessionVariables.getSelectedWork().getAction_cd() == TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_ENTRY) {
                    forwardApplNoChckRender = true;
                }
                if (forwardApplNoChckRender && "KL".equals(Util.getUserStateCode())) {
                    msgforStateAdmin = "I hereby confirm that this application will go to RTO Admin for final approval";
                }
                Map<Object, Object> map = adminImpl.check_RegistrationType(appl_details.getRegn_no());
                if (map != null) {
                    regVehType = (int) map.get("regVehType");
                    applnoPrevTempRegn = (String) map.get("appl_no");
                    tmpOffCode = (int) map.get("off_cd");
                    tmpStateCode = (String) map.get("state_cd");
                }
                State state = new State();
                render_btm = false;
                render_file_move = true;

                Owner_dobj ownDobj = adminImpl.getRegistrationDetailsafterinward(appl_details.getRegn_no(), APPL_NO, regVehType);
                //add DLR TEMP
                if (regVehType == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                    setTrailer_tab(false);
                    String ownerApplNo = adminImpl.getOwnerApplicationForTempRegn(applnoPrevTempRegn, Util.getUserStateCode(), TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
                    if (ownerApplNo != null) {
                        ownDobj.setCheckVaownerTempFlag(true);
                    }

                }
                //end
                if (Util.getUserStateCode().equals("GA") && (Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN)) && regVehType != TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                    String remks = new OwnerAdminImpl().getRemarks(appl_details.getRegn_no(), Util.getUserStateCode(), Util.getUserOffCode());
                    setAdmin_Remarks(remks);
                    setPrv_admin_Remarks(remks);
                    setRemarksFlag(true);
                }
                if (regVehType == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                    //############################ Check for smart Card Pending for KMS start ############################
                    smartCardDobj = ServerUtil.getSmartCardDetailsFromRcBeToBo(appl_details.getRegn_no());
                    ApplicationInwardImpl inwardImpl = new ApplicationInwardImpl();
                    boolean isFitnessRequired = inwardImpl.getFitnessRequiredFor(ownDobj, Util.getTmConfiguration());
                    if (isFitnessRequired) {
                        FitnessImpl fitImpl = new FitnessImpl();
                        fitnessDobj = fitImpl.set_Fitness_appl_db_to_dobj(appl_details.getRegn_no(), null);
                    }
                }

                ownerBean.setDisableRegnType(false);
                ownerBean.setDisableOwnerSr(false);
                ownerBean.setRenderValidityPanel(true);
                ownerBean.setRenderTaxPanel(false);

                if (ownDobj.getOwner_cd() != TableConstants.OWNER_CODE_INDIVIDUAL) {
                    ownerBean.setDisableFName(true);
                } else {
                    ownerBean.setDisableFName(false);
                }
                if (ownerBean.getList_maker_model().isEmpty()
                        || !OwnerImpl.isMakerModelInDb(ownDobj.getMaker(), ownDobj.getMaker_model())) {
                    ownerBean.setModelEditable(true);
                    ownerBean.setRenderModelSelectMenu(false);
                }
                if (regVehType == TableConstants.VM_TRANSACTION_MAST_TEMP_REG && ownDobj.getDob_temp() != null) {
                    ownerBean.setRenderValidityPanel(false);
                    ownerBean.setRenderTempOwner(true);
                    ownerBean.setTemp_dobj_prev((Owner_temp_dobj) ownDobj.getDob_temp().clone());
                    ownerBean.setTemp_dobj(ownDobj.getDob_temp());
                    if (ownDobj.getDob_temp().getState_cd_to() != null) {
                        state = MasterTableFiller.state.get(ownDobj.getDob_temp().getState_cd_to());
                        ownerBean.setList_office_to(state.getOffice());
                        if (Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN) && Util.getSelectedSeat().getPur_cd() == TableConstants.STATEADMIN_OWNER_DATA_CHANGE) {
                            ownerBean.setOffCdToOfOwnerDobj(ownDobj.getOff_cd());
                        }
                        ownerBean.setForTempRegnType();
                    }
                }
                ownDobj.setSpeedGovernerList(ownerBean.getOwnerDobj().getSpeedGovernerList());
                ownerBean.setOwnerDobj(ownDobj);
                ownerBean.set_Owner_appl_dobj_to_bean(ownDobj);
                ownerBean.setOwner_dobj_prv((Owner_dobj) ownDobj.clone());
                ownerBean.setOtherStateDobj(ownDobj.getOtherStateVehDobj());
                ownerBean.setRenderModelEditable(true);
                if (ownDobj.getOtherStateVehDobj() != null) {
                    ownerBean.setOtherStateDobjPrev((OtherStateVehDobj) ownDobj.getOtherStateVehDobj().clone());
                    otherStateVehDobj = ownDobj.getOtherStateVehDobj();
                    stateList = MasterTableFiller.getStateList();
                    State oldState = MasterTableFiller.state.get(ownDobj.getOtherStateVehDobj().getOldStateCD());
                    if (oldState != null) {
                        officeList = oldState.getOffice();
                    }
                    setOtherStateVehicle(true);
                }

                //add
                if (ownDobj.getOwner_identity() != null) {
                    setOwner_identity_prev((OwnerIdentificationDobj) ownDobj.getOwner_identity().clone());
                }
                //end

                if (ownDobj.getListHpaDobj() != null && ownDobj.getListHpaDobj() != null) {
                    HpaDobj hpaDobj = ownDobj.getListHpaDobj().get(0);
                    hpaBean.setHpaDobj(hpaDobj);
                    hpaBean.setHpa_dobj_prv((HpaDobj) hpaDobj.clone());
                    hpaBean.StateFncrListener();
                    renderHypth = true;
                    isHypth = true;
                }

                if (hpaBean != null) {//for setting minimum date purchase date when updating Hypothecation Details
                    hpaBean.setMinDate(ownDobj.getPurchase_dt());
                }

                if (ownDobj.getAxleDobj() != null) {
                    axleBean.setDobj_To_Bean(ownDobj.getAxleDobj());
                    axleBean.setAxle_dobj_prv((AxleDetailsDobj) ownDobj.getAxleDobj().clone());
                    ownerBean.setAxleDetail_Visibility_tab(true);
                }

                if (ownDobj.getTempReg() != null) {
                    ownerBean.setTempReg(ownDobj.getTempReg());
                    ownerBean.setTempReg_prev((TempRegDobj) ownDobj.getTempReg().clone());
                    //ownerBean.setList_office_to();
                    State curSt = MasterTableFiller.state.get(ownDobj.getTempReg().getTmp_state_cd());
                    ownerBean.setList_office_to(curSt.getOffice());
                    ownerBean.setBlnRegnTypeTemp(true);
                }

                if (ownDobj.getInsDobj() != null) {
                    if (ownDobj.getInsDobj().isIibData()) {
                        insBean.setDisable(true);
                    }
                    insBean.set_Ins_dobj_to_bean(ownDobj.getInsDobj());
                    insBean.setIns_dobj_prv((InsDobj) ownDobj.getInsDobj().clone());

                    if (ownDobj.getInsDobj().getIns_type() == Integer.parseInt(TableConstants.INS_TYPE_NA)) {
                        insBean.insTypeListener();
                    }
                }

                if (ownDobj.getExArmy_dobj() != null) {
                    exArmyBean.setDobj_To_Bean(ownDobj.getExArmy_dobj());
                    exArmyBean.setExArmy_dobj_prv((ExArmyDobj) ownDobj.getExArmy_dobj());
                    ownerBean.setExArmyVehicle_Visibility_tab(true);
                }

                if (ownDobj.getCng_dobj() != null) {
                    cNG_Details_Bean.setDobj_To_Bean(ownDobj.getCng_dobj());
                    cNG_Details_Bean.setCng_dobj_prv((RetroFittingDetailsDobj) ownDobj.getCng_dobj().clone());
                    ownerBean.setCngDetails_Visibility_tab(true);
                }

                if (ownDobj.getImp_Dobj() != null) {
                    importedVehicle_Bean.setDobj_to_Bean(ownDobj.getImp_Dobj());
                    importedVehicle_Bean.setImp_dobj_prv((ImportedVehicleDobj) ownDobj.getImp_Dobj());
                    ownerBean.setImportedVehicle_Visibility_tab(true);
                }

                if (ownDobj.getSpeedGovernorDobj() != null) {
                    ownerBean.setRenderIsSpeedGov(true);
                    ownerBean.setRenderSpeedGov(true);
                    setPrev_renderSpeedGov(true);
                    ownerBean.setSpeedGovPrev((SpeedGovernorDobj) ownDobj.getSpeedGovernorDobj().clone());
                }

                if (ownDobj.getReflectiveTapeDobj() != null) {
                    ownerBean.setRenderIsReflectiveTape(true);
                    ownerBean.setRenderReflectiveTape(true);
                    setPrev_renderReflectiveTape(true);
                    ownerBean.setReflectiveTapeDobjPrev(ownDobj.getReflectiveTapeDobj().clone());
                }
                listPreviousChanges = adminImpl.getModificationOnRegNoByUser(APPL_NO);

                String appremarks = "";
                for (int i = 0; i < listPreviousChanges.size(); i++) {
                    ChangesByUser bean = listPreviousChanges.get(i);
                    String str = bean.getChanged_data().replace("&nbsp; <font color=\"red\">|</font> &nbsp;", "|");
                    String remk[] = str.split("\\|");
                    if (remk[remk.length - 1].contains("[AdminRemarks-")) {
                        appremarks = remk[remk.length - 1].replace("[AdminRemarks-", "").replace("]", "");
                        if (!appremarks.equals("")) {
                            prevremarks = appremarks;
                            setRemarks(appremarks.trim());
                            break;
                        }
                    }
                }
                //Add For Trailer
                if (ownDobj.getTrailerDobj() != null) {
                    trailer_bean.setTrailer_dobj_prv(ownDobj.getTrailerDobj());
                    trailer_bean.set_trailer_dobj_to_bean(ownDobj.getTrailerDobj());
                }
                ownerBean.setPreviousRegnType(ownDobj.getRegn_type());
                disableRegnNo = true;
                renderTab = true;
                render_vehicle_type = false;
                ownerBean.setYearRange("c-10:+100");
                //forstateadmin
                if (Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN)) {
                    List<Dealer> listDealer = ServerUtil.getDealerList(Util.getUserStateCode(), ownDobj.getOff_cd());
                    ownerBean.getList_dealer_cd().clear();
                    for (Dealer dl : listDealer) {
                        ownerBean.getList_dealer_cd().add(new SelectItem(dl.getDealer_cd(), dl.getDealer_name()));
                    }
                }
                //end
                if (regVehType == TableConstants.VM_TRANSACTION_MAST_TEMP_REG && ownDobj.getDob_temp() != null && tmpOffCode != 0 && !CommonUtils.isNullOrBlank(tmpStateCode)) {
                    ownerBean.getDealerofOwnerTemp(ownDobj.getDealer_cd(), tmpStateCode, tmpOffCode);
                }
                if (ownDobj != null && regVehType == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                    String[] dealerDetails = ServerUtil.checkDealerAuthForAllOff(ownDobj.getState_cd(), ownDobj.getDealer_cd());
                    if (dealerDetails != null && Boolean.parseBoolean(dealerDetails[3])) {
                        ownerBean.getList_dealer_cd().add(new SelectItem(dealerDetails[0], dealerDetails[1]));
                    }
                }
                boolean inVahan4 = adminImpl.checkRegnBeforeVahan4(ownDobj.getChasi_no(), ownDobj.getRegn_dt(), ownDobj.getState_cd(), ownDobj.getOff_cd());
                if (inVahan4 && Util.getSelectedSeat().getAction_cd() != TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_ENTRY
                        && Util.getSelectedSeat().getAction_cd() != TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_VERIFY
                        && Util.getSelectedSeat().getAction_cd() != TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_APPROVAL
                        && Util.getSelectedSeat().getAction_cd() != TableConstants.TM_RTOADMIN_OWNER_DATA_CHANGE_APPROVAL) {
                    setDisableFieldsHomologation();
                }
                String reason = ServerUtil.checkBSIVChassisAllowedForReg(ownDobj.getChasi_no());
                if (!CommonUtils.isNullOrBlank(reason)) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Info !!!", "<font style='color:red;font-weight: bold;'>You can not change the Chassis/Engine number due to :<br/>" + reason + "</font>"));
                    ownerBean.setDisableChasiNo(true);
                    ownerBean.setDisableEngineNo(true);

                }
                //Document upload for PB
                int purCd = Util.getSelectedSeat().getPur_cd();
                int ACTION_CDOE = Util.getSelectedSeat().getAction_cd();
                if (Util.getUserStateCode().equals("PB") && purCd == TableConstants.ADMIN_OWNER_DATA_CHANGE && (ACTION_CDOE != TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_APPROVAL || ACTION_CDOE != TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_VERIFY)) {
                    if (documentUploadBean != null && documentUploadBean.getDocDescrList() != null && !documentUploadBean.getDocDescrList().isEmpty() && documentUploadBean.isDocumentUploadShow() && documentUploadBean.isRenderUiBasedDMSDocPanel()) {
                        setDocumentUploadShow(true);
                    }
                }
                if (Util.getUserStateCode().equals("PB") && purCd == TableConstants.ADMIN_OWNER_DATA_CHANGE && ACTION_CDOE == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_VERIFY) {
                    if (documentUploadBean != null && documentUploadBean.getDocDescrList() != null && !documentUploadBean.getDocDescrList().isEmpty() && documentUploadBean.isDocumentUploadShow() && documentUploadBean.isRenderUiBasedDMSDocPanel()) {
                        documentUploadBean.setUploadOwnerAdminDoc(true);//Document upload during Verify
                    }
                }

                if (Util.getUserStateCode().equals("PB") && purCd == TableConstants.ADMIN_OWNER_DATA_CHANGE) {
                    if ((ACTION_CDOE == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_VERIFY || ACTION_CDOE == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_APPROVAL) && documentUploadBean != null && documentUploadBean.getDocDetailsList() != null && !documentUploadBean.getDocDetailsList().isEmpty() && documentUploadBean.isDocumentUploadShow() && documentUploadBean.isRenderApiBasedDMSDocPanel()) {
                        setDocumentUploadShow(true);
                    }
                }
                //end Document upload for PB
                setMinDateForForm();
            } else {
                render_vehicle_type = true;

            }
        } catch (VahanException ve) {
            vahanMessages = Util.getLocaleSomthingMsg();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " [ApplNo-" + appl_details.getAppl_no() + ",RegnNo-" + appl_details.getRegn_no() + ",PurCd-" + appl_details.getPur_cd() + "] " + ex.getStackTrace()[0]);
            vahanMessages = Util.getLocaleSomthingMsg();
        }

    }

    //end
    public void updateRtoFromStateListener(AjaxBehaviorEvent even) {
        Map<Object, Object> office = ServerUtil.getOfficeListOfState(this.otherStateVehDobj.getOldStateCD());
        officeList.clear();
        for (Map.Entry<Object, Object> entry : office.entrySet()) {
            int off_cd = (int) entry.getKey();
            String off_name = (String) entry.getValue();
            officeList.add(new SelectItem(off_cd, off_name));
        }
    }

    public String sendOtpMailForApproval(String otpType) {
        String msg = "";
        try {
            boolean isverified = UserDAO.getVerificationDetails(Util.getEmpCode());
            if (isverified) {
                if (otpType != null && otpType.equals("sendOtp") || otpType.equals("resendOtp")) {
                    disposeRcptOtp = SmsMailOTPImpl.sendOTPorMail(Util.getEmpCode(), "OTP for Admin Modification of Vehicle No. " + appl_details.getRegn_no() + ".", otpType, disposeRcptOtp, "OTP for Admin Modification of Vehicle No.  " + appl_details.getRegn_no() + ".");
                    if (disposeRcptOtp != null && !disposeRcptOtp.equals("")) {
                        PrimeFaces.current().ajax().update("otp_confirmation");
                        PrimeFaces.current().executeScript("PF('otp_confrm').show()");
                    } else {
                        throw new VahanException(Util.getLocaleSomthingMsg() + ", " + Util.getLocaleMsg("unableOtp"));
                    }
                } else if (otpType.equals("confirmOtp")) {
                    if (!CommonUtils.isNullOrBlank(disposeRcptOtp) && !CommonUtils.isNullOrBlank(enterRcptOtp) && disposeRcptOtp.equals(enterRcptOtp)) {
                        boolean flag = false;
                        try {
                            if (registrationOwnerAdminDobj != null && APPL_NO != null) {
                                OwnerAdminImpl impl = new OwnerAdminImpl();
                                impl.saveChangesafterinward(registrationOwnerAdminDobj, Util.getEmpCode(), APPL_NO, hpRemarks);
                            }
                        } catch (VahanException vex) {
                            throw vex;
                        } catch (Exception ex) {
                            LOGGER.error("Owner-Admin-Appl-No:" + APPL_NO + ex.toString() + " " + ex.getStackTrace()[0]);
                            throw new VahanException(Util.getLocaleSomthingMsg());
                        }

                        PrimeFaces.current().ajax().update("otp_confirmation");
                        PrimeFaces.current().ajax().update("otp_text");
                        PrimeFaces.current().executeScript("PF('otp_confrm').hide()");
                        return "seatwork";

                    } else {
                        msg = Util.getLocaleMsg("otp_invalid");
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
                        return "";
                    }
                }
            } else {
                msg = Util.getLocaleMsg("verifyMobile");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
                return "";
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ve.getMessage(), ve.getMessage()));

        } catch (Exception e) {
            msg = Util.getLocaleSomthingMsg();
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
        }
        return "";
    }

    public void showDetails() {
        Exception e = null;
        String regnNo = getOwnerBean().getOwnerDobj().getRegn_no();
        OwnerAdminImpl adminImpl = new OwnerAdminImpl();
        State state = new State();
        remarks = null;
        try {
            if (sessionVariables.getSelectedWork().getAction_cd() == TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_ENTRY) {
                forwardApplNoChckRender = true;
            }
            if (forwardApplNoChckRender && "KL".equals(Util.getUserStateCode())) {
                msgforStateAdmin = "I hereby confirm that this application will go to RTO Admin for final approval";
            }
            boolean isblacklistedvehicle;
            TmConfigurationDobj tmConfig = Util.getTmConfiguration();
            if (regVehType == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                isblacklistedvehicle = ServerUtil.isPurposeCodeOnBlacklistedVehicle(regnNo, Util.getSelectedSeat().getAction_cd(), Util.getUserStateCode(), tmConfig);
                if (isblacklistedvehicle) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", Util.getLocaleMsg("blackList")));
                    return;
                }
            }
            String isPending = adminImpl.checkPending(regnNo, Util.getUserStateCode());
            if (isPending != null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, isPending, isPending));
                PrimeFaces.current().ajax().update("msgDialog");
                PrimeFaces.current().executeScript("PF('messageDialog').show();");
                return;
            }
            //ADD Temp Regn Declaration
            Owner_dobj ownDobj = null;
            if (regVehType == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                setTrailer_tab(false);
                Map<Object, Object> map = adminImpl.check_RegistrationType(regnNo);
                if (map != null) {
                    regVehType = (int) map.get("regVehType");
                    applnoPrevTempRegn = (String) map.get("appl_no");
                    tmpOffCode = (int) map.get("off_cd");
                    tmpStateCode = (String) map.get("state_cd");
                }
                String ownerApplNo = adminImpl.getOwnerApplicationForTempRegn(applnoPrevTempRegn, Util.getUserStateCode(), TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
                if (ownerApplNo != null) {
                    ownDobj = adminImpl.getRegistrationDetailsafterinward(regnNo, ownerApplNo, regVehType);
                    if (ownDobj.getDob_temp() == null) {
                        ownDobj.setDob_temp(adminImpl.getVTOwnerTemporyRegnDtls(ownerApplNo));
                    }
                    ownDobj.setRegn_no(regnNo);
                    ownDobj.setCheckVaownerTempFlag(true);
                }
            }
            if (ownDobj == null) {
                ownDobj = adminImpl.getRegistrationDetails(regnNo, regVehType);
            }

            if (ownDobj == null) {
                String msg = Util.getLocaleMsg("regn_not_found");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
                return;
            }
            if (Util.getUserStateCode().equals("GA") && (Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN)) && regVehType != TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                String remks = new OwnerAdminImpl().getRemarks(regnNo, Util.getUserStateCode(), Util.getUserOffCode());
                setAdmin_Remarks(remks);
                setPrv_admin_Remarks(remks);
                setRemarksFlag(true);
            }
            ownerBean.setDisableRegnType(false);
            ownerBean.setDisableOwnerSr(false);
            ownerBean.setRenderValidityPanel(true);
            ownerBean.setRenderTaxPanel(false);
            if (ownDobj != null) {
                if (regVehType == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                    //############################ Check for smart Card Pending for KMS start ############################
                    smartCardDobj = ServerUtil.getSmartCardDetailsFromRcBeToBo(regnNo);
                    if (smartCardDobj != null) {
                        PrimeFaces.current().executeScript("PF('smartCardDialog').show()");
                    }
                    //############################ Check for smart Card Pending for KMS end ############################

                    ApplicationInwardImpl inwardImpl = new ApplicationInwardImpl();
                    boolean isFitnessRequired = inwardImpl.getFitnessRequiredFor(ownDobj, Util.getTmConfiguration());
                    if (isFitnessRequired) {
                        FitnessImpl fitImpl = new FitnessImpl();
                        fitnessDobj = fitImpl.set_Fitness_appl_db_to_dobj(regnNo, null);
                    }
                }

                if (ownDobj.getOwner_cd() != TableConstants.OWNER_CODE_INDIVIDUAL) {
                    ownerBean.setDisableFName(true);
                } else {
                    ownerBean.setDisableFName(false);
                }

                if (regVehType == TableConstants.VM_TRANSACTION_MAST_TEMP_REG && ownDobj.getDob_temp() != null) {
                    ownerBean.setRenderValidityPanel(false);
                    ownerBean.setRenderTempOwner(true);
                    ownerBean.setTemp_dobj_prev((Owner_temp_dobj) ownDobj.getDob_temp().clone());
                    ownerBean.setTemp_dobj(ownDobj.getDob_temp());
                    if (ownDobj.getDob_temp().getState_cd_to() != null) {
                        state = MasterTableFiller.state.get(ownDobj.getDob_temp().getState_cd_to());
                        ownerBean.setList_office_to(state.getOffice());
                        ownerBean.setForTempRegnType();
                    }
                }

            } else {
                ownerBean.setDisableFName(false);
            }

            ownDobj.setSpeedGovernerList(ownerBean.getOwnerDobj().getSpeedGovernerList());
            ownerBean.setOwnerDobj(ownDobj);
            ownerBean.set_Owner_appl_dobj_to_bean(ownDobj);
            ownerBean.setOwner_dobj_prv((Owner_dobj) ownDobj.clone());
            ownerBean.setOtherStateDobj(ownDobj.getOtherStateVehDobj());
            ownerBean.setRenderModelEditable(true);

            if (ownerBean.getList_maker_model().isEmpty()
                    || !OwnerImpl.isMakerModelInDb(ownDobj.getMaker(), ownDobj.getMaker_model())) {
                ownerBean.setModelEditable(true);
                ownerBean.setRenderModelSelectMenu(false);
            }

            if (ownDobj.getOtherStateVehDobj() != null) {
                ownerBean.setOtherStateDobjPrev((OtherStateVehDobj) ownDobj.getOtherStateVehDobj().clone());
                otherStateVehDobj = ownDobj.getOtherStateVehDobj();
                stateList = MasterTableFiller.getStateList();
                State oldState = MasterTableFiller.state.get(ownDobj.getOtherStateVehDobj().getOldStateCD());
                if (oldState != null) {
                    officeList = oldState.getOffice();
                }
                setOtherStateVehicle(true);
            }
            if (ownDobj.getOwner_identity() != null) {
                setOwner_identity_prev((OwnerIdentificationDobj) ownDobj.getOwner_identity().clone());
            }

            if (ownDobj.getListHpaDobj() != null) {
                HpaDobj hpaDobj = ownDobj.getListHpaDobj().get(0);
                hpaBean.setHpaDobj(hpaDobj);
                hpaBean.setHpa_dobj_prv((HpaDobj) hpaDobj.clone());
                hpaBean.StateFncrListener();
                renderHypth = true;
                isHypth = true;
            }

            if (hpaBean != null) {//for setting minimum date purchase date when updating Hypothecation Details
                hpaBean.setMinDate(ownDobj.getPurchase_dt());
            }

            if (ownDobj.getAxleDobj() != null) {
                axleBean.setDobj_To_Bean(ownDobj.getAxleDobj());
                axleBean.setAxle_dobj_prv((AxleDetailsDobj) ownDobj.getAxleDobj().clone());
                ownerBean.setAxleDetail_Visibility_tab(true);
            }

            if (ownDobj.getTempReg() != null) {
                ownerBean.setTempReg(ownDobj.getTempReg());
                ownerBean.setTempReg_prev((TempRegDobj) ownDobj.getTempReg().clone());
                State curSt = MasterTableFiller.state.get(ownDobj.getTempReg().getTmp_state_cd());
                if (curSt != null) {
                    ownerBean.setList_office_to(curSt.getOffice());
                }
                ownerBean.setBlnRegnTypeTemp(true);
            }

            if (ownDobj.getInsDobj() != null) {
                if (ownDobj.getInsDobj().isIibData()) {
                    insBean.setDisable(true);
                }
                insBean.set_Ins_dobj_to_bean(ownDobj.getInsDobj());
                insBean.setIns_dobj_prv((InsDobj) ownDobj.getInsDobj().clone());

                if (ownDobj.getInsDobj().getIns_type() == Integer.parseInt(TableConstants.INS_TYPE_NA)) {
                    insBean.insTypeListener();
                }
            }

            if (ownDobj.getExArmy_dobj() != null) {
                exArmyBean.setDobj_To_Bean(ownDobj.getExArmy_dobj());
                exArmyBean.setExArmy_dobj_prv((ExArmyDobj) ownDobj.getExArmy_dobj());
                ownerBean.setExArmyVehicle_Visibility_tab(true);
            }

            if (ownDobj.getCng_dobj() != null) {
                cNG_Details_Bean.setDobj_To_Bean(ownDobj.getCng_dobj());
                cNG_Details_Bean.setCng_dobj_prv((RetroFittingDetailsDobj) ownDobj.getCng_dobj().clone());
                ownerBean.setCngDetails_Visibility_tab(true);
            }

            if (ownDobj.getImp_Dobj() != null) {
                importedVehicle_Bean.setDobj_to_Bean(ownDobj.getImp_Dobj());
                importedVehicle_Bean.setImp_dobj_prv((ImportedVehicleDobj) ownDobj.getImp_Dobj());
                ownerBean.setImportedVehicle_Visibility_tab(true);
            }

            if (ownDobj.getSpeedGovernorDobj() != null) {
                ownerBean.setRenderIsSpeedGov(true);
                ownerBean.setRenderSpeedGov(true);
                setPrev_renderSpeedGov(true);
                ownerBean.setSpeedGovPrev((SpeedGovernorDobj) ownDobj.getSpeedGovernorDobj().clone());
            }

            if (ownDobj.getReflectiveTapeDobj() != null) {
                ownerBean.setRenderIsReflectiveTape(true);
                ownerBean.setRenderReflectiveTape(true);
                setPrev_renderReflectiveTape(true);
                ownerBean.setReflectiveTapeDobjPrev(ownDobj.getReflectiveTapeDobj().clone());
            }
            //Add For Trailer

            if (ownDobj.getTrailerDobj() != null) {
                trailer_bean.setTrailer_dobj_prv(ownDobj.getTrailerDobj());
                trailer_bean.set_trailer_dobj_to_bean(ownDobj.getTrailerDobj());
            }
            listPreviousChanges = adminImpl.getModificationOnRegNoByUser(regnNo);
            ownerBean.setPreviousRegnType(ownDobj.getRegn_type());
            ownerBean.setYearRange("c-10:c+100");
            if (regVehType == TableConstants.VM_TRANSACTION_MAST_TEMP_REG && ownDobj.getDob_temp() != null && tmpOffCode != 0 && !CommonUtils.isNullOrBlank(tmpStateCode)) {
                ownerBean.getDealerofOwnerTemp(ownDobj.getDealer_cd(), tmpStateCode, tmpOffCode);
            }
            if (ownDobj != null && regVehType == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                String[] dealerDetails = ServerUtil.checkDealerAuthForAllOff(ownDobj.getState_cd(), ownDobj.getDealer_cd());
                if (dealerDetails != null && Boolean.parseBoolean(dealerDetails[3])) {
                    ownerBean.getList_dealer_cd().add(new SelectItem(dealerDetails[0], dealerDetails[1]));
                }
            }

//            if (Util.getTmConfiguration() != null && !Util.getTmConfiguration().isChangeOwnerAdminFlow()) {
//                setDisableFieldsHomologation();
//            }
            boolean inVahan4 = adminImpl.checkRegnBeforeVahan4(ownDobj.getChasi_no(), ownDobj.getRegn_dt(), ownDobj.getState_cd(), ownDobj.getOff_cd());
            if (inVahan4 && Util.getSelectedSeat().getAction_cd() != TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_ENTRY) {
                setDisableFieldsHomologation();
            }

            String reason = ServerUtil.checkBSIVChassisAllowedForReg(ownDobj.getChasi_no());
            if (!CommonUtils.isNullOrBlank(reason)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Info !!!", "<font style='color:red;font-weight: bold;'>You can not change the Chassis/Engine number due to :<br/>" + reason + "</font>"));
                ownerBean.setDisableChasiNo(true);
                ownerBean.setDisableEngineNo(true);
            }
            setMinDateForForm();
        } catch (CloneNotSupportedException cl) {
            e = new VahanException(Util.getLocaleMsg("cloning"));
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
            return;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            e = new VahanException(Util.getLocaleSomthingMsg());
        }
        if (e != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        } else {
            setRenderTab(true);
            setDisableRegnNo(true);
            render_vehicle_type = false;
            render_btm = true;
        }
    }

    @Override
    public String save() {
        Exception e = null;
        OwnerAdminImpl impl = new OwnerAdminImpl();
        Owner_dobj ownerDobj = null;
        String regnNo = getOwnerBean().getOwnerDobj().getRegn_no();
        try {
            if (remarks == null || remarks.trim().length() < 5) {
                throw new VahanException(Util.getLocaleMsg("rmk_field"));
            }
            if (!prevremarks.equals("") && remarks.equals(prevremarks)) {
                remarks = "";
            }
            if (!forwardToApplNo && sessionVariables.getSelectedWork().getAction_cd() == TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_ENTRY) {
                throw new VahanException("I hereby confirm that this application will go to State Admin for final approval.");
            }

            if (appl_details.getAppl_no() == null || APPL_NO.isEmpty()) {
                flag_first_time_inward = true;
            }
            ownerDobj = ownerBean.set_Owner_appl_bean_to_dobj();
            ownerDobj.setRegn_dt(ownerBean.getRegn_dt());
            listChanges.clear();
            ownerDobj.setCheckVaownerTempFlag(ownerBean.getOwnerDobj().isCheckVaownerTempFlag());
            if (regVehType == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                if (trailer_bean != null) {
                    ownerDobj.setTrailerDobj(trailer_bean.setTrailerBeanToDobj());
                }
                if (ownerDobj.getTrailerDobj() != null && ownerDobj.getTrailerDobj().getChasi_no() != null && !ownerDobj.getTrailerDobj().getChasi_no().trim().isEmpty()) {
                    Trailer_Impl.validationTrailer(ownerDobj.getTrailerDobj());
                }
                if (trailer_bean != null && trailer_bean.getTrailer_dobj_prv() == null && ownerDobj.getTrailerDobj() != null && ownerDobj.getTrailerDobj().getChasi_no() != null && !ownerDobj.getTrailerDobj().getChasi_no().trim().isEmpty()) {
                    Trailer_dobj validateTrailerChassisDobj = Trailer_Impl.checkTrailerChassis_trailer(ownerDobj.getTrailerDobj().getChasi_no());
                    if (validateTrailerChassisDobj != null) {
                        throw new VahanException(Util.getLocaleMsg("dupTrailerChasis") + " " + validateTrailerChassisDobj.getDup_chassis() + " " + Util.getLocaleMsg("againstRegn") + " " + validateTrailerChassisDobj.getRegn_no() + " " + Util.getLocaleMsg("user_state") + " " + ServerUtil.getStateNameByStateCode(validateTrailerChassisDobj.getState_cd()) + " " + Util.getLocaleMsg("andOffice") + " " + ServerUtil.getOfficeName(validateTrailerChassisDobj.getOff_cd(), validateTrailerChassisDobj.getState_cd()));
                    }
                }
                if (trailer_bean != null && trailer_bean.getTrailer_dobj_prv() != null && ownerDobj.getTrailerDobj() != null && ownerDobj.getTrailerDobj().getChasi_no() != null
                        && !ownerDobj.getTrailerDobj().getChasi_no().trim().isEmpty() && trailer_bean.getTrailer_dobj_prv().getChasi_no() != null
                        && !trailer_bean.getTrailer_dobj_prv().getChasi_no().trim().isEmpty() && !ownerDobj.getTrailerDobj().getChasi_no().trim().equals(trailer_bean.getTrailer_dobj_prv().getChasi_no().trim())) {
                    Trailer_dobj validateTrailerChassisDobj = Trailer_Impl.checkTrailerChassis_trailer(ownerDobj.getTrailerDobj().getChasi_no());
                    if (validateTrailerChassisDobj != null) {
                        throw new VahanException(Util.getLocaleMsg("dupTrailerChasis") + " " + validateTrailerChassisDobj.getDup_chassis() + " " + Util.getLocaleMsg("againstRegn") + " " + validateTrailerChassisDobj.getRegn_no() + " " + Util.getLocaleMsg("user_state") + " " + ServerUtil.getStateNameByStateCode(validateTrailerChassisDobj.getState_cd()) + " " + Util.getLocaleMsg("andOffice") + " " + ServerUtil.getOfficeName(validateTrailerChassisDobj.getOff_cd(), validateTrailerChassisDobj.getState_cd()));
                    }
                }
            }
            updateOrInsertIntoVH(ownerDobj, regnNo);
            String applno = impl.ownerAdminapplicationInward(APPL_NO, ownerDobj, listChanges, regVehType, remarks, admin_Remarks, hpRemarks);

            if (applno != null || !applno.isEmpty()) {
                flag_first_time_inward = false;
            }
            if (appl_details.getAppl_no() == null || APPL_NO.isEmpty()) {
                flag_first_time_inward = true;
            }

            render_btm = false;
            setRenderTab(false);
            listPreviousChanges.clear();
            ownerBean.setDisablePurchaseDt(false);
            if (flag_first_time_inward) {
                String facesMessages = Util.getLocaleMsg("applnogen") + " : [ " + applno + " ]";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                PrimeFaces.current().ajax().update("msgDialog");
                PrimeFaces.current().executeScript("PF('messageDialog').show();");
                return "";
            }
            return "seatwork";
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
            return null;
        } catch (Exception ee) {
            LOGGER.error("Regn-No:" + regnNo + "-" + ee.toString() + " " + ee.getStackTrace()[0]);
            e = ee;
        }

        if (e != null) {
            String mgs = Util.getLocaleSomthingMsg();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mgs, mgs));
            return null;
        }
        return "";
    }

    @Override
    public List<ComparisonBean> compareChanges() {

        try {
            listChanges.clear();

            listChanges.addAll(compareChagnes_owner());
            listChanges.addAll(compareChagnes_owner_identity());
            listChanges.addAll(hpaBean.compareChagnes());
            listChanges.addAll(insBean.compareChagnes());
            listChanges.addAll(compareChagnes_OtherStateVeh());
            listChanges.addAll(compareChange_TempRegn());

            listChanges.addAll(axleBean.compareChagnes());
            listChanges.addAll(exArmyBean.compareChagnes());

            listChanges.addAll(importedVehicle_Bean.compareChagnes()); //  
            listChanges.addAll(cNG_Details_Bean.compareChagnes());
            listChanges.addAll(compareChange_SpeedGov());
            listChanges.addAll(compareChange_ReflectiveTape());
            listChanges.addAll(trailer_bean.compareChagnes());

            if (Util.getUserStateCode().equals("GA") && (Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN)) && regVehType != TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                if (!admin_Remarks.equalsIgnoreCase(prv_admin_Remarks)) {
                    Compare("Admin Remarks -", getPrv_admin_Remarks(), this.admin_Remarks, listChanges);
                }
            }
            return listChanges;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return null;
    }

    @Override
    public String saveAndMoveFile() {
        Exception e = null;
        OwnerAdminImpl impl = new OwnerAdminImpl();
        Owner_dobj ownerDobj = null;
        flag_first_time_inward = false;
        listChanges.clear();
        try {
            if (!prevremarks.equals("") && remarks.equals(prevremarks)) {
                remarks = "";
            }
            //
            if (!forwardToApplNo && (Util.getSelectedSeat().getAction_cd() == TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_VERIFY || sessionVariables.getSelectedWork().getAction_cd() == TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_ENTRY)) {
                throw new VahanException("I hereby confirm that this application will go to State Admin for final approval.");
            }
            if (APPL_NO != null && Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_APPROVAL && getAppl_details().getPur_cd() == TableConstants.ADMIN_OWNER_DATA_CHANGE) {
                String msg = OwnerAdminImpl.checkEntryVerifyAndApproveByUserCd(APPL_NO, Util.getEmpCodeLong(), getAppl_details().getPur_cd(), Util.getSelectedSeat().getAction_cd());
                if (!CommonUtils.isNullOrBlank(msg)) {
                    throw new VahanException(msg);
                }
            }
            ownerDobj = ownerBean.set_Owner_appl_bean_to_dobj();
            ownerDobj.setRegn_dt(ownerBean.getRegn_dt());
            Status_dobj status = new Status_dobj();
            ownerDobj.setCheckVaownerTempFlag(ownerBean.getOwnerDobj().isCheckVaownerTempFlag());
            if (regVehType == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                if (trailer_bean != null) {
                    ownerDobj.setTrailerDobj(trailer_bean.setTrailerBeanToDobj());
                }
                if (ownerDobj.getTrailerDobj() != null && ownerDobj.getTrailerDobj().getChasi_no() != null && !ownerDobj.getTrailerDobj().getChasi_no().trim().isEmpty()) {
                    Trailer_Impl.validationTrailer(ownerDobj.getTrailerDobj());
                }
                if (trailer_bean != null && trailer_bean.getTrailer_dobj_prv() == null && ownerDobj.getTrailerDobj() != null && ownerDobj.getTrailerDobj().getChasi_no() != null && !ownerDobj.getTrailerDobj().getChasi_no().trim().isEmpty()) {
                    Trailer_dobj validateTrailerChassisDobj = Trailer_Impl.checkTrailerChassis_trailer(ownerDobj.getTrailerDobj().getChasi_no());
                    if (validateTrailerChassisDobj != null) {
                        throw new VahanException(Util.getLocaleMsg("dupTrailerChasis") + " " + validateTrailerChassisDobj.getDup_chassis() + " " + Util.getLocaleMsg("againstRegn") + " " + validateTrailerChassisDobj.getRegn_no() + " " + Util.getLocaleMsg("user_state") + " " + ServerUtil.getStateNameByStateCode(validateTrailerChassisDobj.getState_cd()) + " " + Util.getLocaleMsg("andOffice") + " " + ServerUtil.getOfficeName(validateTrailerChassisDobj.getOff_cd(), validateTrailerChassisDobj.getState_cd()));
                    }
                }
                if (trailer_bean != null && trailer_bean.getTrailer_dobj_prv() != null && ownerDobj.getTrailerDobj() != null && ownerDobj.getTrailerDobj().getChasi_no() != null
                        && !ownerDobj.getTrailerDobj().getChasi_no().trim().isEmpty() && trailer_bean.getTrailer_dobj_prv().getChasi_no() != null
                        && !trailer_bean.getTrailer_dobj_prv().getChasi_no().trim().isEmpty() && !ownerDobj.getTrailerDobj().getChasi_no().trim().equals(trailer_bean.getTrailer_dobj_prv().getChasi_no().trim())) {
                    Trailer_dobj validateTrailerChassisDobj = Trailer_Impl.checkTrailerChassis_trailer(ownerDobj.getTrailerDobj().getChasi_no());
                    if (validateTrailerChassisDobj != null) {
                        throw new VahanException(Util.getLocaleMsg("dupTrailerChasis") + " " + validateTrailerChassisDobj.getDup_chassis() + " " + Util.getLocaleMsg("againstRegn") + " " + validateTrailerChassisDobj.getRegn_no() + " " + Util.getLocaleMsg("user_state") + " " + ServerUtil.getStateNameByStateCode(validateTrailerChassisDobj.getState_cd()) + " " + Util.getLocaleMsg("andOffice") + " " + ServerUtil.getOfficeName(validateTrailerChassisDobj.getOff_cd(), validateTrailerChassisDobj.getState_cd()));
                    }
                }
            }
            if (APPL_NO != null) {
                TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
                status.setAppl_dt(getAppl_details().getAppl_dt());
                status.setAppl_no(getAppl_details().getAppl_no());
                status.setPur_cd(getAppl_details().getPur_cd());
                status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
                status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
                status.setStatus(getApp_disapp_dobj().getNew_status());
                status.setAction_cd(getApp_disapp_dobj().getPre_action_code());
                status.setCurrent_role(appl_details.getCurrent_action_cd());
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                        || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)
                        || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    if (Util.getUserStateCode().equals("PB") && getApp_disapp_dobj().getNew_status().equalsIgnoreCase(TableConstants.STATUS_COMPLETE) && tmConfigurationDobj != null && tmConfigurationDobj.getTmConfigDmsDobj() != null && tmConfigurationDobj.getTmConfigDmsDobj().getPurCd().contains("," + getAppl_details().getPur_cd() + ",") && (tmConfigurationDobj.getTmConfigDmsDobj().getDocUploadAllotedOff().contains("ALL") || tmConfigurationDobj.getTmConfigDmsDobj().getDocUploadAllotedOff().contains("," + sessionVariables.getOffCodeSelected() + ","))) {
                        if (documentUploadBean != null && documentUploadBean.isRenderUiBasedDMSDocPanel() && documentUploadBean.getDocDescrList() != null && !documentUploadBean.getDocDescrList().isEmpty() && documentUploadBean.getDocDescrList().size() != documentUploadBean.getTotalCountUploadDoc() && appl_details.getCurrent_action_cd() == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_VERIFY) {
                            throw new VahanException("Please Uplaod verify documents before application verification.");
                        }
                    }

                    if (Util.getUserStateCode().equals("PB") && getApp_disapp_dobj().getNew_status().equalsIgnoreCase(TableConstants.STATUS_COMPLETE) && tmConfigurationDobj != null && tmConfigurationDobj.getTmConfigDmsDobj() != null && tmConfigurationDobj.getTmConfigDmsDobj().getPurCd().contains("," + getAppl_details().getPur_cd() + ",") && (tmConfigurationDobj.getTmConfigDmsDobj().getDocUploadAllotedOff().contains("ALL") || tmConfigurationDobj.getTmConfigDmsDobj().getDocUploadAllotedOff().contains("," + sessionVariables.getOffCodeSelected() + ","))) {
                        if (documentUploadBean != null && documentUploadBean.isRenderApiBasedDMSDocPanel() && tmConfigurationDobj.getTmConfigDmsDobj().getVerfyActionCd().contains("," + appl_details.getCurrent_action_cd() + ",") || tmConfigurationDobj.getTmConfigDmsDobj().getApproveActionCd().contains("," + appl_details.getCurrent_action_cd() + ",")) {
                            List<DocumentDetailsDobj> docsDetailsList = documentUploadBean.getUploadedList();
                            if (docsDetailsList != null && !docsDetailsList.isEmpty() && docsDetailsList.size() > 0) {
                                documentUploadBean.setEmailMessage("");
                                for (DocumentDetailsDobj docsList : docsDetailsList) {
                                    if (!docsList.isDocVerified()) {
                                        documentUploadBean.setEmailMessage(docsList.getCatName() + "," + documentUploadBean.getEmailMessage());
                                    }
                                }
                                if (!documentUploadBean.getEmailMessage().isEmpty()) {
                                    throw new VahanException("Either Verify documents or modify Documents !!!.");
                                }
                            }
                        }
                        if (appl_details.getCurrent_action_cd() == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_VERIFY && documentUploadBean.getUploadedList() != null && documentUploadBean.getMandatoryList() != null && !documentUploadBean.getUploadedList().isEmpty() && !documentUploadBean.getMandatoryList().isEmpty()) {
                            documentUploadBean.callDocumentApiToGetDocListToBeUploadAtVerification();
                            if (documentUploadBean.getUploadedList().size() >= documentUploadBean.getMandatoryList().size()) {
                                for (DocumentDetailsDobj dtls : documentUploadBean.getMandatoryList()) {
                                    if (!documentUploadBean.getUploadedList().contains(dtls)) {
                                        throw new VahanException(TableConstants.MANDATORY_DOCUMENTS_NOT_UPLOAD_ERR_MESSG + getAppl_details().getAppl_no() + "");
                                    }
                                }
                            } else {
                                throw new VahanException("Uploaded documents Total: " + documentUploadBean.getUploadedList().size() + " is less then Mandatory documents Total: " + documentUploadBean.getMandatoryList().size());
                            }
                        }
                    }
                    status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                    updateOrInsertIntoVH(ownerDobj, ownerDobj.getRegn_no());
                    status_approve = impl.updateOrInsertStatus(ownerDobj, ownerDobj.getRegn_no(), status, APPL_NO, listChanges, admin_Remarks, regVehType, remarks, hpRemarks);
                    if (status_approve != null) {
                        if ((status_approve.getCurrent_role() == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_APPROVAL || status_approve.getCurrent_role() == TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_APPROVAL
                                || status_approve.getCurrent_role() == TableConstants.TM_RTOADMIN_OWNER_DATA_CHANGE_APPROVAL)
                                && !status_approve.getStatus().equals(TableConstants.STATUS_REVERT)
                                && !status_approve.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                            String changedata_appl_no = "";
                            for (int i = 0; i < listPreviousChanges.size(); i++) {
                                ChangesByUser bean = listPreviousChanges.get(i);
                                String str = bean.getChanged_data().replace("&nbsp; <font color=\"red\">|</font> &nbsp;", "|");
                                changedata_appl_no = changedata_appl_no + str;
                            }
                            registrationOwnerAdminDobj = new RegistrationOwnerAdminDobj();
                            if (APPL_NO != null) {
                                ownerDobj.setAppl_no(appl_details.getAppl_no());
                                registrationOwnerAdminDobj.setAdmin_Remarks(admin_Remarks);
                                registrationOwnerAdminDobj.setChangedata_appl_no(changedata_appl_no);
                                registrationOwnerAdminDobj.setIsHypth(isHypth);
                                registrationOwnerAdminDobj.setOwnerDobj(ownerDobj);
                                registrationOwnerAdminDobj.setListChanges(listChanges);
                                registrationOwnerAdminDobj.setRegVehType(regVehType);
                                registrationOwnerAdminDobj.setRemarks(remarks);
                                registrationOwnerAdminDobj.setSmartCardDobj(smartCardDobj);
                                registrationOwnerAdminDobj.setStatus_dobj(status_approve);
                                registrationOwnerAdminDobj.setPrevTempApplno(applnoPrevTempRegn);

                                if (tmConfigurationDobj != null && tmConfigurationDobj.isOtpforOwnerAdmin()) {
                                    sendOtpMailForApproval("sendOtp");
                                    return "";
                                } else {
                                    if (registrationOwnerAdminDobj != null && APPL_NO != null) {
                                        impl.saveChangesafterinward(registrationOwnerAdminDobj, Util.getEmpCode(), APPL_NO, hpRemarks);
                                        return "seatwork";
                                    }
                                }
                            }
                        } else {
                            return "seatwork";
                        }
                    } else {
                        throw new VahanException(Util.getLocaleSomthingMsg());
                    }
                }
            }

        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
            return null;
        } catch (Exception ee) {
            LOGGER.error("Owner-Admin-Appl-No:" + APPL_NO + ee.toString() + " " + ee.getStackTrace()[0]);
            e = ee;
        }

        if (e != null) {
            String mgs = Util.getLocaleSomthingMsg();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mgs, mgs));
            return null;
        }
        listPreviousChanges = impl.getModificationOnRegNoByUser(APPL_NO);
        setRenderTab(false);
        return "";
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return listChanges;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    @Override
    public void saveEApplication() {
    }

    private void updateOrInsertIntoVH(Owner_dobj ownerDobj, String regnNo) throws VahanException, Exception {
        ownerDobj.setInsertUpdateFlag(false);
        ownerDobj.setInsertUpdateHpaFlag(false);
        ownerDobj.setDeleteUpdateHpaFlag(false);
        ownerDobj.setTrailerDeleteFlag(false);
        if (ownerDobj.getOwner_identity() != null) {
            ownerDobj.getOwner_identity().setInsertUpdateIdentification(false);
        }
        if (ownerDobj.getInsDobj() != null) {
            ownerDobj.getInsDobj().setInsertUpdateInsurnaceFlag(false);
        }
        ownerDobj.setDeleteUpdaeOtherStateFlag(false);
        if (ownerDobj.getOtherStateVehDobj() != null) {
            ownerDobj.getOtherStateVehDobj().setInserUpdaeOtherStateFlag(false);
        }
        ownerDobj.setTempRegndeleteFlag(false);
        if (ownerDobj.getTempReg() != null) {
            ownerDobj.getTempReg().setTempRegnInsertFlag(false);
        }
        ownerDobj.setAxelDeleteFlag(false);
        if (ownerDobj.getAxleDobj() != null) {
            ownerDobj.getAxleDobj().setAxelInsertUpdateFlag(false);
        }
        ownerDobj.setExArmyDeleteFlag(false);

        if (ownerDobj.getExArmy_dobj() != null) {
            ownerDobj.getExArmy_dobj().setExArmyInsertUpdateFlag(false);
        }
        ownerDobj.setImportedVehDeleteFlag(false);
        if (ownerDobj.getImp_Dobj() != null) {
            ownerDobj.getImp_Dobj().setImportedVehInsertUpdateFlag(false);
        }
        ownerDobj.setRetrofitDeleteFlag(false);
        if (ownerDobj.getCng_dobj() != null) {
            ownerDobj.getCng_dobj().setRetrofitInsertUpdateFlag(false);
        }
        ownerDobj.setSpeedGovDeleteFlag(false);
        if (ownerDobj.getSpeedGovernorDobj() != null) {
            ownerDobj.getSpeedGovernorDobj().setSpeedGovInsertUpdateFlag(false);
        }
        ownerDobj.setReflectiveTapeDeleteFlag(false);
        if (ownerDobj.getReflectiveTapeDobj() != null) {
            ownerDobj.getReflectiveTapeDobj().setReflectiveTapeInsertUpdateFlag(false);
        }
        if (ownerDobj.getTrailerDobj() != null) {
            ownerDobj.getTrailerDobj().setInsertUpdateTrailerFlag(false);
        }

        ownerDobj.setAppl_no(APPL_NO);
        OwnerAdminImpl own = new OwnerAdminImpl();
        listChanges.clear();
        if (isHypth) {
            List<HpaDobj> list = new ArrayList<>();
            list.add(hpaBean.getHpaDobj());
            ownerDobj.setListHpaDobj(list);
        }
        if (isHypth && ownerDobj.getListHpaDobj() != null && !ownerDobj.getListHpaDobj().isEmpty()) {
            List<ComparisonBean> list_hpa = hpaBean.compareChagnes();
            if (flag_first_time_inward || (hpaBean.getHpa_dobj_prv() == null && ownerDobj.getListHpaDobj() != null && !ownerDobj.getListHpaDobj().isEmpty())) {
                listChanges.addAll(list_hpa);
                hpRemarks = "[Hypothecation details inserted]";
                ownerDobj.setInsertUpdateHpaFlag(true);
                ownerDobj.setDeleteUpdateHpaFlag(false);
            } else if (list_hpa.size() > 0) {
                listChanges.addAll(list_hpa);
                ComparisonBean comparisonBean = new ComparisonBean();
                comparisonBean.setFields("Hypothecation is Updated");
                comparisonBean.setOld_value("Old");
                comparisonBean.setNew_value("New");
                listChanges.add(comparisonBean);
                ownerDobj.setInsertUpdateHpaFlag(true);
                ownerDobj.setDeleteUpdateHpaFlag(false);
            }
        } else if (!isHypth && hpaBean.getHpa_dobj_prv() != null && ownerDobj.getListHpaDobj() == null) {
            //delete Hypothicate
            hpRemarks = "[Hypothecation details deleted]";
            ownerDobj.setInsertUpdateHpaFlag(false);
            ownerDobj.setDeleteUpdateHpaFlag(true);
        }

        if (isOtherStateVehicle()) {
            ownerDobj.setOtherStateVehDobj(otherStateVehDobj);
        }

        //other state vehicle
        if (!ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE) && ownerBean.getOtherStateDobjPrev() != null && ownerDobj.getOtherStateVehDobj() == null) {
            ownerDobj.setDeleteUpdaeOtherStateFlag(true);
        }
        if (ownerDobj.getOtherStateVehDobj() != null) {
            List<ComparisonBean> list_other_state = compareChagnes_OtherStateVeh();
            if (flag_first_time_inward || (ownerBean.getOtherStateDobjPrev() == null && ownerDobj.getOtherStateVehDobj() != null)) {
                listChanges.addAll(list_other_state);
                ownerDobj.getOtherStateVehDobj().setInserUpdaeOtherStateFlag(true);
            } else if (list_other_state.size() > 0) {
                listChanges.addAll(list_other_state);
                ownerDobj.getOtherStateVehDobj().setInserUpdaeOtherStateFlag(true);
            }
        }

        if (ownerBean.isAxleDetail_Visibility_tab()) {
            ownerDobj.setAxleDobj(axleBean.setBean_To_Dobj());
        }

        //Insert into Axle
        if (ownerDobj.getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT && ownerBean.getOwner_dobj_prv().getVehType() != ownerDobj.getVehType()) {
            ownerDobj.setAxelDeleteFlag(true);
        }
        if (ownerDobj.getAxleDobj() != null) {

            List<ComparisonBean> list_axle = axleBean.compareChagnes();
            if (flag_first_time_inward || (axleBean.getAxle_dobj_prv() == null && ownerDobj.getAxleDobj() != null)) {
                listChanges.addAll(list_axle);
                ownerDobj.getAxleDobj().setAxelInsertUpdateFlag(true);
            } else if (list_axle.size() > 0) {
                listChanges.addAll(list_axle);
                ownerDobj.getAxleDobj().setAxelInsertUpdateFlag(true);
            }

        }

        if (ownerBean.isBlnRegnTypeTemp()) {
            ownerDobj.setTempReg(ownerBean.getTempReg());
        }

        //Insert into TempRegn
        if (!ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY) && ownerDobj.getTempReg() == null && ownerBean.getTempReg_prev() != null && ownerBean.getOwner_dobj_prv().getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY)) {
            ownerDobj.setTempRegndeleteFlag(true);
        }
        if (ownerDobj.getTempReg() != null) {
            List<ComparisonBean> list_tempRegn = compareChange_TempRegn();
            if (flag_first_time_inward || (ownerBean.getTempReg_prev() == null && ownerDobj.getTempReg() != null)) {
                listChanges.addAll(list_tempRegn);
                ownerDobj.getTempReg().setTempRegnInsertFlag(true);
            } else if (list_tempRegn.size() > 0) {
                listChanges.addAll(list_tempRegn);
                ownerDobj.getTempReg().setTempRegnInsertFlag(true);
            }
        }

        ownerDobj.setInsDobj(insBean.set_InsBean_to_dobj());

        // add insert into insurance
        List<ComparisonBean> list_ins = insBean.compareChagnes();
        if (flag_first_time_inward || (insBean.getIns_dobj_prv() == null && ownerDobj.getInsDobj() != null)) {
            listChanges.addAll(list_ins);
            ownerDobj.getInsDobj().setInsertUpdateInsurnaceFlag(true);
        } else if (list_ins.size() > 0) {
            listChanges.addAll(list_ins);
            ownerDobj.getInsDobj().setInsertUpdateInsurnaceFlag(true);
        }
        if (ownerBean.isExArmyVehicle_Visibility_tab()) {
            ownerDobj.setExArmy_dobj(exArmyBean.setExArmyBean_To_Dobj());

        }
//Insert into ExArmy
        if (!ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_EXARMY) && ownerDobj.getExArmy_dobj() == null && exArmyBean.getExArmy_dobj_prv() != null && ownerBean.getOwner_dobj_prv().getRegn_type().equals(TableConstants.VM_REGN_TYPE_EXARMY)) {
            ownerDobj.setExArmyDeleteFlag(true);
        }
        if (ownerDobj.getExArmy_dobj() != null) {

            List<ComparisonBean> list_exArmy = exArmyBean.compareChagnes();
            if (flag_first_time_inward || (exArmyBean.getExArmy_dobj_prv() == null && ownerDobj.getExArmy_dobj() != null)) {
                listChanges.addAll(list_exArmy);
                ownerDobj.getExArmy_dobj().setExArmyInsertUpdateFlag(true);
            } else if (list_exArmy.size() > 0) {
                listChanges.addAll(list_exArmy);
                ownerDobj.getExArmy_dobj().setExArmyInsertUpdateFlag(true);
            }
        }

        if (ownerBean.isCngDetails_Visibility_tab()) {
            ownerDobj.setCng_dobj(cNG_Details_Bean.setBean_To_Dobj());
        }
        //Insert inot Retrofiting
        int fuel_type = ownerDobj.getFuel();
        int prev_fuel_typ = ownerBean.getOwner_dobj_prv().getFuel();
        if ((fuel_type != TableConstants.VM_FUEL_CNG_TYPE
                && fuel_type != TableConstants.VM_FUEL_TYPE_PETROL_CNG
                && fuel_type != TableConstants.VM_FUEL_TYPE_LPG
                && fuel_type != TableConstants.VM_FUEL_TYPE_PETROL_LPG) && (fuel_type != prev_fuel_typ) && ownerDobj.getCng_dobj() == null && cNG_Details_Bean.getCng_dobj_prv() != null) {
            ownerDobj.setRetrofitDeleteFlag(true);
        }
        if (ownerDobj.getCng_dobj() != null) {
            List<ComparisonBean> list_cng_details = cNG_Details_Bean.compareChagnes();
            if (flag_first_time_inward || (cNG_Details_Bean.getCng_dobj_prv() == null && ownerDobj.getCng_dobj() != null)) {
                listChanges.addAll(list_cng_details);
                ownerDobj.getCng_dobj().setRetrofitInsertUpdateFlag(true);
            } else if (list_cng_details.size() > 0) {
                listChanges.addAll(list_cng_details);
                ownerDobj.getCng_dobj().setRetrofitInsertUpdateFlag(true);
            }
        }

        if (ownerBean.isImportedVehicle_Visibility_tab()) {
            ownerDobj.setImp_Dobj(importedVehicle_Bean.setBean_to_Dobj());
        }

        //Insert inot Imported
        if (!TableConstants.VM_REGN_TYPE_IMPORTED_YES.equalsIgnoreCase(ownerDobj.getImported_vch()) && ownerBean.getOwner_dobj_prv().getImported_vch() != ownerDobj.getImported_vch() && ownerDobj.getImp_Dobj() == null) {
            ownerDobj.setImportedVehDeleteFlag(true);
        }
        if (ownerDobj.getImp_Dobj() != null) {
            List<ComparisonBean> list_importVech = importedVehicle_Bean.compareChagnes();
            if (flag_first_time_inward || (importedVehicle_Bean.getImp_dobj_prv() == null && ownerDobj.getImp_Dobj() != null)) {
                listChanges.addAll(list_importVech);
                ownerDobj.getImp_Dobj().setImportedVehInsertUpdateFlag(true);
            } else if (list_importVech.size() > 0) {
                listChanges.addAll(list_importVech);
                ownerDobj.getImp_Dobj().setImportedVehInsertUpdateFlag(true);
            }
        }

        if (regVehType == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                && ownerDobj.getRegn_upto() != null && ownerDobj.getRegn_dt() != null
                && DateUtils.compareDates(ownerDobj.getRegn_upto(), ownerDobj.getRegn_dt()) <= 1) {
            throw new VahanException(Util.getLocaleMsg("validRegnDate"));
        }

        if (Util.getUserStateCode().equals("GA") && (Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN)) && regVehType != TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
            if (!admin_Remarks.equalsIgnoreCase(prv_admin_Remarks)) {
                Compare("Admin Remarks -", getPrv_admin_Remarks(), this.admin_Remarks, listChanges);
            }
        }

        if (regVehType == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                && ownerDobj.getFit_upto() != null && ownerDobj.getRegn_dt() != null
                && DateUtils.compareDates(ownerDobj.getFit_upto(), ownerDobj.getRegn_dt()) <= 1) {
            throw new VahanException(Util.getLocaleMsg("validFitDate"));
        } else if (regVehType == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                && ownerDobj.getFit_upto() != null && ownerDobj.getRegn_dt() != null) {
            if (fitnessDobj != null
                    && DateUtils.compareDates(ownerDobj.getFit_upto(), fitnessDobj.getFit_valid_to()) != 0) {
                fitnessDobj.setRemark("Fitness Upto and Nid Changed by Admin");
                fitnessDobj.setFit_valid_to(ownerDobj.getFit_upto());
                fitnessDobj.setFit_nid(ServerUtil.dateRange(ownerDobj.getFit_upto(), 0, 0, Util.getTmConfiguration().getNid_days()));
                ownerDobj.setFitnessDobj(fitnessDobj);
            }
        }

        //Delete Link vehicle Details
        if (!CommonUtils.isNullOrBlank(ownerBean.getOwner_dobj_prv().getLinkedRegnNo()) && CommonUtils.isNullOrBlank(ownerBean.getOwnerDobj().getLinkedRegnNo())) {
            ownerDobj.setInsertDeletelinkVehicle(true);
        }
        List<ComparisonBean> list_owner = compareChagnes_owner();
        if (flag_first_time_inward || (ownerBean.getOwner_dobj_prv() == null && ownerBean.getOwnerDobj() != null)) {
            listChanges.addAll(list_owner);
            ownerDobj.setInsertUpdateFlag(true);
        } else if (list_owner.size() > 0) {
            ownerDobj.setInsertUpdateFlag(true);
            listChanges.addAll(list_owner);
        }

        // add owner identification 
        if (ownerDobj.getOwner_identity() != null) {
            List<ComparisonBean> list_owner_identity = compareChagnes_owner_identity();
            OwnerIdentificationDobj ownerIde = ownerDobj.getOwner_identity();
            ownerIde.setAppl_no(APPL_NO);
            ownerIde.setRegn_no(regnNo);
            if (flag_first_time_inward || (getOwner_identity_prev() == null && ownerDobj.getOwner_identity() != null)) {
                listChanges.addAll(list_owner_identity);
                ownerDobj.getOwner_identity().setInsertUpdateIdentification(true);
            } else if (list_owner_identity.size() > 0) {
                listChanges.addAll(list_owner_identity);
                ownerDobj.getOwner_identity().setInsertUpdateIdentification(true);
            }
        }

        //Insert into Governor
        SpeedGovernorDobj speedGVr = ownerDobj.getSpeedGovernorDobj();
        if (!ownerBean.isRenderSpeedGov() && speedGVr == null && ownerBean.isRenderSpeedGov() != isPrev_renderSpeedGov() && ownerBean.getSpeedGovPrev() != null) {
            ownerDobj.setSpeedGovDeleteFlag(true);
        }
        if (speedGVr != null) {
            List<ComparisonBean> list_speed_gov = compareChange_SpeedGov();
            if (flag_first_time_inward || (ownerBean.getSpeedGovPrev() == null && speedGVr != null)) {
                listChanges.addAll(list_speed_gov);
                ownerDobj.getSpeedGovernorDobj().setSpeedGovInsertUpdateFlag(true);
            } else if (list_speed_gov.size() > 0) {
                listChanges.addAll(list_speed_gov);
                ownerDobj.getSpeedGovernorDobj().setSpeedGovInsertUpdateFlag(true);
            }
        }

        //insert into  ReflectiveTape
        ReflectiveTapeDobj reflectTap = ownerDobj.getReflectiveTapeDobj();
        if (!ownerBean.isRenderReflectiveTape() && reflectTap == null && ownerBean.isRenderReflectiveTape() != isPrev_renderReflectiveTape() && ownerBean.getReflectiveTapeDobjPrev() != null) { //!ownerBean.isRenderIsReflectiveTape() && 
            ownerDobj.setReflectiveTapeDeleteFlag(true);
        }
        if (reflectTap != null) {
            List<ComparisonBean> list_ref_tap = compareChange_ReflectiveTape();
            if (flag_first_time_inward || (ownerBean.getReflectiveTapeDobjPrev() == null && reflectTap != null)) {
                listChanges.addAll(list_ref_tap);
                ownerDobj.getReflectiveTapeDobj().setReflectiveTapeInsertUpdateFlag(true);
            } else if (list_ref_tap.size() > 0) {
                listChanges.addAll(list_ref_tap);
                ownerDobj.getReflectiveTapeDobj().setReflectiveTapeInsertUpdateFlag(true);
            }

        }

        if (regVehType == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && trailer_bean != null && trailer_bean.getTrailer_dobj_prv() != null && ownerDobj.getTrailerDobj() != null
                && ownerDobj.getTrailerDobj().getChasi_no().trim().isEmpty() && trailer_bean.getTrailer_dobj_prv().getChasi_no() != null
                && !trailer_bean.getTrailer_dobj_prv().getChasi_no().trim().isEmpty()) {
            ownerDobj.setTrailerDeleteFlag(true);
        }
        if (regVehType == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && ownerDobj.getTrailerDobj() != null && ownerDobj.getTrailerDobj().getChasi_no() != null && !ownerDobj.getTrailerDobj().getChasi_no().trim().isEmpty()) {
            List<ComparisonBean> trailerList = trailer_bean.compareChagnes();
            listChanges.addAll(trailerList);
            if (flag_first_time_inward || (trailer_bean != null && trailer_bean.getTrailer_dobj_prv() == null && ownerDobj.getTrailerDobj() != null && ownerDobj.getTrailerDobj().getChasi_no() != null && !ownerDobj.getTrailerDobj().getChasi_no().trim().isEmpty())) {
                ownerDobj.getTrailerDobj().setInsertUpdateTrailerFlag(true);
            } else if (trailerList.size() > 0) {
                ownerDobj.getTrailerDobj().setInsertUpdateTrailerFlag(true);
            }
        }
    }

    public List<ComparisonBean> compareChagnes_owner() throws VahanException {
        Owner_dobj owner_dobj = ownerBean.getOwner_dobj_prv(); //getting the dobj from workbench
        compBeanList.clear();
        if (owner_dobj == null) {

            return compBeanList;
        }
        ////////////////////////////vehicle Details start///////////////////
        Compare("Chasi_No", owner_dobj.getChasi_no(), this.ownerBean.getOwnerDobj().getChasi_no(), compBeanList);
        String[][] dataVhclass = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        if (owner_dobj.getVh_class() != ownerBean.getOwnerDobj().getVh_class()) {
            String oldlabel = null;
            String newLabel = null;

            for (int i = 0; i < dataVhclass.length; i++) {

                if (dataVhclass[i][0].equals(String.valueOf(owner_dobj.getVh_class()))) {
                    oldlabel = dataVhclass[i][1];
                }

                if (dataVhclass[i][0].equals(String.valueOf(ownerBean.getOwnerDobj().getVh_class()))) {
                    newLabel = dataVhclass[i][1];
                }
            }

            Compare("Veh Class", oldlabel, newLabel, compBeanList);
        }

        Compare("Eng_No", owner_dobj.getEng_no(), this.ownerBean.getOwnerDobj().getEng_no(), compBeanList);
        Compare("Maker", owner_dobj.getMaker(), this.ownerBean.getOwnerDobj().getMaker(), compBeanList);
        Compare("Maker model", owner_dobj.getMaker_model(), this.ownerBean.getOwnerDobj().getMaker_model(), compBeanList);
        Compare("Bd_type", owner_dobj.getBody_type(), this.ownerBean.getOwnerDobj().getBody_type(), compBeanList);
        Compare("Seat_Cap", owner_dobj.getSeat_cap(), this.ownerBean.getOwnerDobj().getSeat_cap(), compBeanList);
        Compare("Stand_Cap", owner_dobj.getStand_cap(), this.ownerBean.getOwnerDobj().getStand_cap(), compBeanList);
        Compare("Sleeper_Cap", owner_dobj.getSleeper_cap(), this.ownerBean.getOwnerDobj().getSleeper_cap(), compBeanList);
        Compare("No_of_Cyl", owner_dobj.getNo_cyl(), this.ownerBean.getOwnerDobj().getNo_cyl(), compBeanList);
        Compare("ULD", owner_dobj.getUnld_wt(), this.ownerBean.getOwnerDobj().getUnld_wt(), compBeanList);
        Compare("LD", owner_dobj.getLd_wt(), this.ownerBean.getOwnerDobj().getLd_wt(), compBeanList);
        Compare("GCW", owner_dobj.getGcw(), this.ownerBean.getOwnerDobj().getGcw(), compBeanList);
        // Compare("RLW", owner_dobj.getReg_ld_wt(), getReg_ld_wt(), compBeanList);
        Compare("HP", owner_dobj.getHp(), this.ownerBean.getOwnerDobj().getHp(), compBeanList);
        Compare("Norms", owner_dobj.getNorms(), this.ownerBean.getNorms(), compBeanList);
        Compare("Fuel", owner_dobj.getFuel(), this.ownerBean.getOwnerDobj().getFuel(), compBeanList);
        Compare("Color", owner_dobj.getColor(), this.ownerBean.getOwnerDobj().getColor(), compBeanList);
        Compare("WheelBase", owner_dobj.getWheelbase(), this.ownerBean.getOwnerDobj().getWheelbase(), compBeanList);
        Compare("CC", owner_dobj.getCubic_cap(), this.ownerBean.getOwnerDobj().getCubic_cap(), compBeanList);
        Compare("Floor_Area", owner_dobj.getFloor_area(), this.ownerBean.getOwnerDobj().getFloor_area(), compBeanList);
        Compare("AC", owner_dobj.getAc_fitted(), this.ownerBean.getOwnerDobj().getAc_fitted(), compBeanList);
        Compare("Audio", owner_dobj.getAudio_fitted(), this.ownerBean.getOwnerDobj().getAudio_fitted(), compBeanList);
        Compare("Video", owner_dobj.getVideo_fitted(), this.ownerBean.getOwnerDobj().getVideo_fitted(), compBeanList);
        Compare("Manu_Month", owner_dobj.getManu_mon(), this.ownerBean.getOwnerDobj().getManu_mon(), compBeanList);
        Compare("Manu_Year", owner_dobj.getManu_yr(), this.ownerBean.getOwnerDobj().getManu_yr(), compBeanList);
        Compare("Laser_Cd", owner_dobj.getLaser_code(), this.ownerBean.getOwnerDobj().getLaser_code(), compBeanList);
        Compare("Length", owner_dobj.getLength(), this.ownerBean.getOwnerDobj().getLength(), compBeanList);
        Compare("Width", owner_dobj.getWidth(), this.ownerBean.getOwnerDobj().getWidth(), compBeanList);
        Compare("Height", owner_dobj.getHeight(), this.ownerBean.getOwnerDobj().getHeight(), compBeanList);
        Compare("G.Address", owner_dobj.getGarage_add(), this.ownerBean.getOwnerDobj().getGarage_add(), compBeanList);
        Compare("Dealer", owner_dobj.getDealer_cd(), this.ownerBean.getOwnerDobj().getDealer_cd(), compBeanList);

        ////////////////////////////////////////vehicle Detail End//////////
        /////////////////////////owner Detail start/////////////////////////
        //Compare("Registration Date", owner_dobj.getRegn_dt(), getRegn_dt(), compBeanList);
        Compare("Purchase Date", owner_dobj.getPurchase_dt(), ownerBean.getOwnerDobj().getPurchase_dt(), compBeanList);
        Compare("O_Name", owner_dobj.getOwner_name(), ownerBean.getOwnerDobj().getOwner_name(), compBeanList);
        Compare("F_Name", owner_dobj.getF_name(), ownerBean.getOwnerDobj().getF_name(), compBeanList);
        Compare("O_Sr", owner_dobj.getOwner_sr(), ownerBean.getOwnerDobj().getOwner_sr(), compBeanList);
        Compare("OW_CODE", owner_dobj.getOwner_cd(), ownerBean.getOwnerDobj().getOwner_cd(), compBeanList);
        Compare("C_Add1", owner_dobj.getC_add1(), ownerBean.getOwnerDobj().getC_add1(), compBeanList);
        Compare("C_Add2", owner_dobj.getC_add2(), ownerBean.getOwnerDobj().getC_add2(), compBeanList);
        Compare("C_Dist", owner_dobj.getC_district(), ownerBean.getOwnerDobj().getC_district(), compBeanList);
        Compare("C_Add3", owner_dobj.getC_add3(), ownerBean.getOwnerDobj().getC_add3(), compBeanList);
        Compare("C_state", owner_dobj.getC_state(), ownerBean.getOwnerDobj().getC_state(), compBeanList);
        Compare("C_Pin", owner_dobj.getC_pincode(), ownerBean.getOwnerDobj().getC_pincode(), compBeanList);
        Compare("P_Add1", owner_dobj.getP_add1(), ownerBean.getOwnerDobj().getP_add1(), compBeanList);
        Compare("P_Add2", owner_dobj.getP_add2(), ownerBean.getOwnerDobj().getP_add2(), compBeanList);
        Compare("P_Dist", owner_dobj.getP_district(), ownerBean.getOwnerDobj().getP_district(), compBeanList);
        Compare("P_Add3", owner_dobj.getP_add3(), ownerBean.getOwnerDobj().getP_add3(), compBeanList);
        Compare("p_state", owner_dobj.getP_state(), ownerBean.getOwnerDobj().getP_state(), compBeanList);
        Compare("P_Pin", owner_dobj.getP_pincode(), ownerBean.getOwnerDobj().getP_pincode(), compBeanList);
        Compare("Sale Amount", owner_dobj.getSale_amt(), ownerBean.getSale_amt(), compBeanList);
        Compare("Veh Catg", owner_dobj.getVch_catg(), ownerBean.getOwnerDobj().getVch_catg(), compBeanList);
        Compare("Annual Income", owner_dobj.getAnnual_income(), ownerBean.getAnnual_income(), compBeanList);
        Compare("Other Criteria", owner_dobj.getOther_criteria(), ownerBean.getOther_criteria(), compBeanList);
        Compare("Imported Vehicle", owner_dobj.getImported_vch(), ownerBean.getImported_veh(), compBeanList);
        Compare("Purchase As", owner_dobj.getVch_purchase_as(), ownerBean.getVch_purchase_as(), compBeanList);
        Compare("Permit Type", owner_dobj.getPmt_type(), ownerBean.getOwnerDobj().getPmt_type(), compBeanList);
        Compare("Permit Catg", owner_dobj.getPmt_catg(), ownerBean.getOwnerDobj().getPmt_catg(), compBeanList);
        Compare("Regn Type", owner_dobj.getRegn_type(), ownerBean.getOwnerDobj().getRegn_type(), compBeanList);
        if (ownerBean.isRenderTaxPanel()) {
            Compare("Tax Modes", owner_dobj.getRqrd_tax_modes(), NewImpl.getTaxModes(ownerBean.getOwnerDobj()), compBeanList);
        }
        if (ownerBean.isRenderValidityPanel()) {
            if (owner_dobj.getRegn_upto() != null) {
                Compare("Regn Upto Dt", owner_dobj.getRegn_upto(), this.ownerBean.getRegn_upto(), compBeanList);
            }
            if (owner_dobj.getFit_upto() != null) {
                Compare("Fit Upto Dt", owner_dobj.getFit_upto(), this.ownerBean.getFit_upto(), compBeanList);
            }

            if (owner_dobj.getRegn_dt() != null) {
                Compare("Regn Date", owner_dobj.getRegn_dt(), this.ownerBean.getRegn_dt(), compBeanList);
            }
        }
        Compare("Linked Regn Number", owner_dobj.getLinkedRegnNo(), ownerBean.getOwnerDobj().getLinkedRegnNo(), compBeanList);

        if (ownerBean.isPushBkSeatRender()) {
            Compare("Push Back Seat", owner_dobj.getPush_bk_seat(), this.ownerBean.getOwnerDobj().getPush_bk_seat(), compBeanList);
            Compare("Ordinary Seat", owner_dobj.getOrdinary_seat(), this.ownerBean.getOwnerDobj().getOrdinary_seat(), compBeanList);
        }

        if (owner_dobj.getDob_temp() != null && ownerBean.getTemp_dobj_prev() != null) {
            Compare("Temp Purpose", ownerBean.getTemp_dobj_prev().getPurpose(), this.ownerBean.getTemp_dobj().getPurpose(), compBeanList);
            if (this.ownerBean.getTemp_dobj().getBodyBuilding() != null) {
                Compare("Temp Body Building", ownerBean.getTemp_dobj_prev().getBodyBuilding(), this.ownerBean.getTemp_dobj().getBodyBuilding(), compBeanList);
            } else {
                Compare("State To", ownerBean.getTemp_dobj_prev().getState_cd_to(), this.ownerBean.getTemp_dobj().getState_cd_to(), compBeanList);
                Compare("Office To", ownerBean.getTemp_dobj_prev().getOff_cd_to(), this.ownerBean.getTemp_dobj().getOff_cd_to(), compBeanList);
            }
        }

        /////////////////////////////////owner Detail end///////////////////
        return compBeanList;

    }

    public List<ComparisonBean> compareChange_ReflectiveTape() throws VahanException {
        ReflectiveTapeDobj reflect_prev = ownerBean.getReflectiveTapeDobjPrev();
        compBeanList.clear();
        if (reflect_prev == null) {

            return compBeanList;
        }

        if (ownerBean.isRenderIsReflectiveTape() && ownerBean.getOwnerDobj().getReflectiveTapeDobj() != null) {
            if (reflect_prev != null) {
                Compare("Ref Cert No", reflect_prev != null ? reflect_prev.getCertificateNo() : "", ownerBean.getOwnerDobj().getReflectiveTapeDobj().getCertificateNo(), compBeanList);
                Compare("Ref Fitment Date", reflect_prev != null ? reflect_prev.getFitmentDate() : null, ownerBean.getOwnerDobj().getReflectiveTapeDobj().getFitmentDate(), compBeanList);
                Compare("Ref Manu Name", reflect_prev != null ? reflect_prev.getManuName() : "", ownerBean.getOwnerDobj().getReflectiveTapeDobj().getManuName(), compBeanList);
            }
        }
        return compBeanList;
    }

    public List<ComparisonBean> compareChange_SpeedGov() throws VahanException {
        SpeedGovernorDobj speedGVr_prev = ownerBean.getSpeedGovPrev();
        compBeanList.clear();
        if (speedGVr_prev == null) {

            return compBeanList;
        }

        if (ownerBean.isRenderSpeedGov() && ownerBean.getOwnerDobj().getSpeedGovernorDobj() != null) {
            if (speedGVr_prev != null) {
                Compare("Speed Governor No", speedGVr_prev != null ? speedGVr_prev.getSg_no() : "", ownerBean.getOwnerDobj().getSpeedGovernorDobj().getSg_no(), compBeanList);
                Compare("Speed Governor Fitted At", speedGVr_prev != null ? speedGVr_prev.getSg_fitted_at() : "", ownerBean.getOwnerDobj().getSpeedGovernorDobj().getSg_fitted_at(), compBeanList);
                Compare("Speed Governor Fitted On", speedGVr_prev != null ? speedGVr_prev.getSg_fitted_on() : null, ownerBean.getOwnerDobj().getSpeedGovernorDobj().getSg_fitted_on(), compBeanList);
                Compare("Speed Governor Type", speedGVr_prev.getSgGovType(), ownerBean.getOwnerDobj().getSpeedGovernorDobj().getSgGovType(), compBeanList);
                Compare("Speed Governor Type Approval No", speedGVr_prev != null ? speedGVr_prev.getSgTypeApprovalNo() : "", ownerBean.getOwnerDobj().getSpeedGovernorDobj().getSgTypeApprovalNo(), compBeanList);
                Compare("Speed Governor Test Report No", speedGVr_prev != null ? speedGVr_prev.getSgTestReportNo() : "", ownerBean.getOwnerDobj().getSpeedGovernorDobj().getSgTestReportNo(), compBeanList);
                Compare("Speed Governor Fit Cert No", speedGVr_prev != null ? speedGVr_prev.getSgFitmentCerticateNo() : "", ownerBean.getOwnerDobj().getSpeedGovernorDobj().getSgFitmentCerticateNo(), compBeanList);
            }
        }
        return compBeanList;
    }

    public List<ComparisonBean> compareChange_TempRegn() throws VahanException {

        TempRegDobj tempDobj_prev = ownerBean.getTempReg_prev();
        compBeanList.clear();
        if (tempDobj_prev == null) {
            return compBeanList;
        }

        if (ownerBean.getTempReg() != null && tempDobj_prev != null) {
            Compare("Temp Regn No", tempDobj_prev.getTmp_regn_no(), ownerBean.getTempReg().getTmp_regn_no(), compBeanList);
            Compare("Temp Regn Date", tempDobj_prev.getTmp_regn_dt(), ownerBean.getTempReg().getTmp_regn_dt(), compBeanList);
            Compare("Temp Valid Upto", tempDobj_prev.getTmp_valid_upto(), ownerBean.getTempReg().getTmp_valid_upto(), compBeanList);
            Compare("Temp State ", tempDobj_prev.getTmp_state_cd(), ownerBean.getTempReg().getTmp_state_cd(), compBeanList);
            Compare("Temp Office", tempDobj_prev.getTmp_off_cd(), ownerBean.getTempReg().getTmp_off_cd(), compBeanList);
            Compare("Dealer", tempDobj_prev.getDealer_cd(), ownerBean.getTempReg().getDealer_cd(), compBeanList);

        }
        return compBeanList;
    }

    public List<ComparisonBean> compareChagnes_OtherStateVeh() throws VahanException {
        //ownerBean.compareChagnes();
        OtherStateVehDobj otherStateDobj = ownerBean.getOtherStateDobjPrev();
        compBeanList.clear();
        if (otherStateDobj == null) {
            return compBeanList;
        }

        if (ownerBean.getOtherStateDobj() != null && otherStateDobj != null) {
            Compare("New Regn No", ownerBean.getOtherStateDobjPrev().getNewRegnNo(), ownerBean.getOtherStateDobj().getNewRegnNo(), compBeanList);
            Compare("Old Regn No", ownerBean.getOtherStateDobjPrev().getOldRegnNo(), ownerBean.getOtherStateDobj().getOldRegnNo(), compBeanList);
            Compare("State Entry Date", ownerBean.getOtherStateDobjPrev().getStateEntryDate(), ownerBean.getOtherStateDobj().getStateEntryDate(), compBeanList);
            Compare("Regn Dt", ownerBean.getOwner_dobj_prv().getRegn_dt(), ownerBean.getOwnerDobj().getRegn_dt(), compBeanList);
            Compare("Regn Upto Dt", ownerBean.getOwner_dobj_prv().getRegn_upto(), ownerBean.getOwnerDobj().getRegn_upto(), compBeanList);
            Compare("Fit Upto Dt", ownerBean.getOwner_dobj_prv().getFit_upto(), ownerBean.getOwnerDobj().getFit_upto(), compBeanList);
            Compare("Noc No", ownerBean.getOtherStateDobjPrev().getNocNo(), ownerBean.getOtherStateDobj().getNocNo(), compBeanList);
            Compare("Noc Issue Date", ownerBean.getOtherStateDobjPrev().getNocDate(), ownerBean.getOtherStateDobj().getNocDate(), compBeanList);
            Compare("Old State", ownerBean.getOtherStateDobjPrev().getOldStateCD(), ownerBean.getOtherStateDobj().getOldStateCD(), compBeanList);
            Compare("Old office", ownerBean.getOtherStateDobjPrev().getOldOffCD(), ownerBean.getOtherStateDobj().getOldOffCD(), compBeanList);
            Compare("Reference No", ownerBean.getOtherStateDobjPrev().getNcrbRef(), ownerBean.getOtherStateDobj().getNcrbRef(), compBeanList);
            Compare("Confirm No", ownerBean.getOtherStateDobjPrev().getConfirmRef(), ownerBean.getOtherStateDobj().getConfirmRef(), compBeanList);
        }
        return compBeanList;

    }

    public List<ComparisonBean> compareChagnes_owner_identity() throws VahanException {
        //ownerBean.compareChagnes();
        OwnerIdentificationDobj owner_identity = getOwner_identity_prev();
        compBeanList.clear();
        if (owner_identity == null) {
            return compBeanList;
        }

        Compare("PAN_No", owner_identity.getPan_no(), this.ownerBean.getOwner_identification().getPan_no(), compBeanList);
        Compare("Mobile", owner_identity.getMobile_no() == null ? 0 : owner_identity.getMobile_no(), this.ownerBean.getOwner_identification().getMobile_no(), compBeanList);
        Compare("Email", owner_identity.getEmail_id(), this.ownerBean.getOwner_identification().getEmail_id(), compBeanList);
        Compare("Aadhar", owner_identity.getAadhar_no(), this.ownerBean.getOwner_identification().getAadhar_no(), compBeanList);
        Compare("Passport", owner_identity.getPassport_no(), this.ownerBean.getOwner_identification().getPassport_no(), compBeanList);
        Compare("Ration", owner_identity.getRation_card_no(), this.ownerBean.getOwner_identification().getRation_card_no(), compBeanList);
        Compare("Voter_Id", owner_identity.getVoter_id(), this.ownerBean.getOwner_identification().getVoter_id(), compBeanList);
        Compare("DL", owner_identity.getDl_no(), this.ownerBean.getOwner_identification().getDl_no(), compBeanList);
        Compare("Owner Category", owner_identity.getOwnerCatg(), this.ownerBean.getOwner_identification().getOwnerCatg(), compBeanList);
        Compare("Owner Code Department", owner_identity.getOwnerCdDept(), this.ownerBean.getOwner_identification().getOwnerCdDept(), compBeanList);
        return compBeanList;

    }
    //end

    public void regVehTypeChangeListener() {

        if (regVehType == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
            regVehTypeDescr = Util.getLocaleMsg("registerVeh");
        } else if (regVehType == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
            regVehTypeDescr = Util.getLocaleMsg("tmpVeh");
        }
        ownerBean.getOwnerDobj().setRegn_no("");
    }

    public void otherStateOffCdListender(AjaxBehaviorEvent event) {
    }

    /**
     * @return the renderHypth
     */
    public boolean isRenderHypth() {
        return renderHypth;
    }

    /**
     * @param renderHypth the renderHypth to set
     */
    public void setRenderHypth(boolean renderHypth) {
        this.renderHypth = renderHypth;
    }

    /**
     * @return the ownerBean
     */
    public OwnerBean getOwnerBean() {
        return ownerBean;
    }

    /**
     * @param ownerBean the ownerBean to set
     */
    public void setOwnerBean(OwnerBean ownerBean) {
        this.ownerBean = ownerBean;
    }

    /**
     * @return the insBean
     */
    public InsBean getInsBean() {
        return insBean;
    }

    /**
     * @param insBean the insBean to set
     */
    public void setInsBean(InsBean insBean) {
        this.insBean = insBean;
    }

    /**
     * @return the hpaBean
     */
    public HpaBean getHpaBean() {
        return hpaBean;
    }

    /**
     * @param hpaBean the hpaBean to set
     */
    public void setHpaBean(HpaBean hpaBean) {
        this.hpaBean = hpaBean;
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

    /**
     * @return the cNG_Details_Bean
     */
    public RetroFittingDetailsBean getcNG_Details_Bean() {
        return cNG_Details_Bean;
    }

    /**
     * @param cNG_Details_Bean the cNG_Details_Bean to set
     */
    public void setcNG_Details_Bean(RetroFittingDetailsBean cNG_Details_Bean) {
        this.cNG_Details_Bean = cNG_Details_Bean;
    }

    /**
     * @return the importedVehicle_Bean
     */
    public ImportedVehicleBean getImportedVehicle_Bean() {
        return importedVehicle_Bean;
    }

    /**
     * @param importedVehicle_Bean the importedVehicle_Bean to set
     */
    public void setImportedVehicle_Bean(ImportedVehicleBean importedVehicle_Bean) {
        this.importedVehicle_Bean = importedVehicle_Bean;
    }

    /**
     * @return the axleBean
     */
    public AxleBean getAxleBean() {
        return axleBean;
    }

    /**
     * @param axleBean the axleBean to set
     */
    public void setAxleBean(AxleBean axleBean) {
        this.axleBean = axleBean;
    }

    /**
     * @return the exArmyBean
     */
    public ExArmyBean getExArmyBean() {
        return exArmyBean;
    }

    /**
     * @param exArmyBean the exArmyBean to set
     */
    public void setExArmyBean(ExArmyBean exArmyBean) {
        this.exArmyBean = exArmyBean;
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
     * @return the renderTab
     */
    public boolean isRenderTab() {
        return renderTab;
    }

    /**
     * @param renderTab the renderTab to set
     */
    public void setRenderTab(boolean renderTab) {
        this.renderTab = renderTab;
    }

    /**
     * @return the isHypth
     */
    public boolean isIsHypth() {
        return isHypth;
    }

    /**
     * @param isHypth the isHypth to set
     */
    public void setIsHypth(boolean isHypth) {
        this.isHypth = isHypth;
    }

    /**
     * @return the listPreviousChanges
     */
    public List<ChangesByUser> getListPreviousChanges() {
        return listPreviousChanges;
    }

    /**
     * @param listPreviousChanges the listPreviousChanges to set
     */
    public void setListPreviousChanges(List<ChangesByUser> listPreviousChanges) {
        this.listPreviousChanges = listPreviousChanges;
    }

    /**
     * @return the listChanges
     */
    public List<ComparisonBean> getListChanges() {
        return listChanges;
    }

    /**
     * @param listChanges the listChanges to set
     */
    public void setListChanges(List<ComparisonBean> listChanges) {
        this.listChanges = listChanges;
    }

    /**
     * @return the disableRegnNo
     */
    public boolean isDisableRegnNo() {
        return disableRegnNo;
    }

    /**
     * @param disableRegnNo the disableRegnNo to set
     */
    public void setDisableRegnNo(boolean disableRegnNo) {
        this.disableRegnNo = disableRegnNo;
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
     * @return the regVehType
     */
    public int getRegVehType() {
        return regVehType;
    }

    /**
     * @param regVehType the regVehType to set
     */
    public void setRegVehType(int regVehType) {
        this.regVehType = regVehType;
    }

    /**
     * @return the regVehTypeDescr
     */
    public String getRegVehTypeDescr() {
        return regVehTypeDescr;
    }

    /**
     * @param regVehTypeDescr the regVehTypeDescr to set
     */
    public void setRegVehTypeDescr(String regVehTypeDescr) {
        this.regVehTypeDescr = regVehTypeDescr;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
     * @return the smartCardDobj
     */
    public SmartCardDobj getSmartCardDobj() {
        return smartCardDobj;
    }

    /**
     * @param smartCardDobj the smartCardDobj to set
     */
    public void setSmartCardDobj(SmartCardDobj smartCardDobj) {
        this.smartCardDobj = smartCardDobj;
    }

    /**
     * @return the remarksFlag
     */
    public boolean isRemarksFlag() {
        return remarksFlag;
    }

    /**
     * @param remarksFlag the remarksFlag to set
     */
    public void setRemarksFlag(boolean remarksFlag) {
        this.remarksFlag = remarksFlag;
    }

    public String getAdmin_Remarks() {
        return admin_Remarks;
    }

    public void setAdmin_Remarks(String admin_Remarks) {
        this.admin_Remarks = admin_Remarks;
    }

    public String getPrv_admin_Remarks() {
        return prv_admin_Remarks;
    }

    public void setPrv_admin_Remarks(String prv_admin_Remarks) {
        this.prv_admin_Remarks = prv_admin_Remarks;
    }

    public boolean isRender_vehicle_type() {
        return render_vehicle_type;
    }

    public void setRender_vehicle_type(boolean render_vehicle_type) {
        this.render_vehicle_type = render_vehicle_type;
    }

    public OwnerIdentificationDobj getOwner_identity_prev() {
        return owner_identity_prev;
    }

    public void setOwner_identity_prev(OwnerIdentificationDobj owner_identity_prev) {
        this.owner_identity_prev = owner_identity_prev;
    }

    public boolean isFlag_first_time_inward() {
        return flag_first_time_inward;
    }

    public void setFlag_first_time_inward(boolean flag_first_time_inward) {
        this.flag_first_time_inward = flag_first_time_inward;
    }

    public boolean isRender_btm() {
        return render_btm;
    }

    public void setRender_btm(boolean render_btm) {
        this.render_btm = render_btm;
    }

    public boolean isRender_file_move() {
        return render_file_move;
    }

    public void setRender_file_move(boolean render_file_move) {
        this.render_file_move = render_file_move;
    }

    public String getAPPL_NO() {
        return APPL_NO;
    }

    public void setAPPL_NO(String APPL_NO) {
        this.APPL_NO = APPL_NO;
    }

    public String getDisposeRcptOtp() {
        return disposeRcptOtp;
    }

    public void setDisposeRcptOtp(String disposeRcptOtp) {
        this.disposeRcptOtp = disposeRcptOtp;
    }

    public String getEnterRcptOtp() {
        return enterRcptOtp;
    }

    public void setEnterRcptOtp(String enterRcptOtp) {
        this.enterRcptOtp = enterRcptOtp;
    }

    public boolean isPrev_renderSpeedGov() {
        return prev_renderSpeedGov;
    }

    public void setPrev_renderSpeedGov(boolean prev_renderSpeedGov) {
        this.prev_renderSpeedGov = prev_renderSpeedGov;

    }

    public boolean isPrev_renderReflectiveTape() {
        return prev_renderReflectiveTape;
    }

    public void setPrev_renderReflectiveTape(boolean prev_renderReflectiveTape) {
        this.prev_renderReflectiveTape = prev_renderReflectiveTape;
    }

    public String getPrevremarks() {
        return prevremarks;
    }

    public void setPrevremarks(String prevremarks) {
        this.prevremarks = prevremarks;
    }

    public boolean isTemporyRegnDLRFlag() {
        return temporyRegnDLRFlag;
    }

    public void setTemporyRegnDLRFlag(boolean temporyRegnDLRFlag) {
        this.temporyRegnDLRFlag = temporyRegnDLRFlag;
    }

    public boolean isTrailer_tab() {
        return trailer_tab;
    }

    public void setTrailer_tab(boolean trailer_tab) {
        this.trailer_tab = trailer_tab;
    }

    public Trailer_bean getTrailer_bean() {
        return trailer_bean;
    }

    public void setTrailer_bean(Trailer_bean trailer_bean) {
        this.trailer_bean = trailer_bean;
    }

    public String getVahanMessages() {
        return vahanMessages;
    }

    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    public String getHpRemarks() {
        return hpRemarks;
    }

    public void setHpRemarks(String hpRemarks) {
        this.hpRemarks = hpRemarks;
    }
    //addforBS4S

    public void setDisableFieldsHomologation() throws VahanException {
        ownerBean.disableForHomologationData(true);
        ownerBean.setDisableManuMonth(true);
        ownerBean.setDisableManuYr(true);
        ownerBean.setFitUptoDateDisable(true);
        ownerBean.setRegnUptoDateDisable(true);
        ownerBean.setRegistrationDateDisable(true);
        ownerBean.setDisablePurchaseDt(true);

    }

    public boolean isForwardToApplNo() {
        return forwardToApplNo;
    }

    public void setForwardToApplNo(boolean forwardToApplNo) {
        this.forwardToApplNo = forwardToApplNo;
    }

    public boolean isForwardApplNoChckRender() {
        return forwardApplNoChckRender;
    }

    public void setForwardApplNoChckRender(boolean forwardApplNoChckRender) {
        this.forwardApplNoChckRender = forwardApplNoChckRender;
    }

    public DocumentUploadBean getDocumentUploadBean() {
        return documentUploadBean;
    }

    public void setDocumentUploadBean(DocumentUploadBean documentUploadBean) {
        this.documentUploadBean = documentUploadBean;
    }

    public boolean isDocumentUploadShow() {
        return documentUploadShow;
    }

    public void setDocumentUploadShow(boolean documentUploadShow) {
        this.documentUploadShow = documentUploadShow;
    }

    public void setMinDateForForm() {
        Date minPurchaseDate = DateUtil.parseDate(DateUtil.getCurrentDate());
        Owner_dobj ownerDobjMinDate = getOwnerBean().getOwnerDobj();
        if (ownerDobjMinDate.getPurchase_dt() != null) {
            minPurchaseDate = DateUtil.parseDate(DateUtils.getDateInDDMMYYYY(ownerDobjMinDate.getPurchase_dt()));
        }
        if (cNG_Details_Bean != null) {
            if (cNG_Details_Bean.getCal_install_dt() != null && cNG_Details_Bean.getCal_install_dt().before(minPurchaseDate)) {
                cNG_Details_Bean.setMinDate(DateUtil.parseDate(DateUtils.getDateInDDMMYYYY(cNG_Details_Bean.getCal_install_dt())));
            } else {
                cNG_Details_Bean.setMinDate(minPurchaseDate);
            }
            if (cNG_Details_Bean.getCal_hydro_dt() != null && cNG_Details_Bean.getCal_hydro_dt().before(minPurchaseDate)) {
                cNG_Details_Bean.setMinHydroDate(DateUtil.parseDate(DateUtils.getDateInDDMMYYYY(cNG_Details_Bean.getCal_hydro_dt())));
            } else {
                cNG_Details_Bean.setMinHydroDate(minPurchaseDate);
            }
        }
        if (insBean != null) {
            if (insBean.getIns_from() != null && insBean.getIns_from().before(minPurchaseDate)) {
                insBean.setMin_dt(DateUtil.parseDate(DateUtils.getDateInDDMMYYYY(insBean.getIns_from())));
            } else {
                insBean.setMin_dt(ServerUtil.dateRange(minPurchaseDate, 0, 0, -15));
            }
            if (insBean.getIns_upto() != null) {
                insBean.setMax_dt(DateUtil.parseDate(DateUtils.getDateInDDMMYYYY(insBean.getIns_upto())));
            } else {
                insBean.setMax_dt(minPurchaseDate);
            }
        }
        if (hpaBean != null) {
            if (hpaBean.getHpaDobj() != null && hpaBean.getHpaDobj().getFrom_dt() != null && hpaBean.getHpaDobj().getFrom_dt().before(minPurchaseDate)) {
                hpaBean.setMinDate(DateUtil.parseDate(DateUtils.getDateInDDMMYYYY(hpaBean.getHpaDobj().getFrom_dt())));
            } else {
                hpaBean.setMinDate(minPurchaseDate);
            }
        }

    }

    public String getMsgforStateAdmin() {
        return msgforStateAdmin;
    }

    public void setMsgforStateAdmin(String msgforStateAdmin) {
        this.msgforStateAdmin = msgforStateAdmin;
    }
}
