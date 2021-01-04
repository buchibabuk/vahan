/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import dao.UserDAO;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.ApplDisposeVerifyByAdminDobj;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.ExArmyDobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.RestoreDisposeApplicationDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.permit.PermitPaidFeeDtlsDobj;
import nic.vahan.form.impl.ApplicationDisposeImpl;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.ExArmyImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.RestoreDisposeApplicationImpl;
import nic.vahan.form.impl.SmsMailOTPImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.PermitPaidFeeDtlsImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nicsi applicationDisposeBean
 */
@ViewScoped
@ManagedBean(name = "restoreDisposeApplicationBean")
public class RestoreDisposeApplicationBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(RestoreDisposeApplicationBean.class);
    private String regn_no;
    private String appl_no;
    private String stateCd;
    private String empCd;
    private int pur_cd;
    private int offCode;
    private String reason;
    private List<String> selectedPurposeCode;
    private String message = null;
    private boolean render = false;
    private Map<String, Integer> purCodeList = null;
    private OwnerDetailsDobj ownerDetail;
    private InsBean ins_bean = new InsBean();
    private HypothecationDetailsBean hypothecationDetails_bean = new HypothecationDetailsBean();
    @ManagedProperty(value = "#{exArmyBean}")
    private ExArmyBean exArmyBean;
    @ManagedProperty(value = "#{axleBean}")
    private AxleBean axleBean;
    private int action_cd;
    private List<PermitPaidFeeDtlsDobj> feeTaxPaidList = null;
    String userCd = null;
    private String disposeRcptOtp = null;
    private String enterRcptOtp = null;
    RestoreDisposeApplicationDobj restoreDisposeDobj = new RestoreDisposeApplicationDobj();

    public void showDetails() {
        int pur_cd = 0;
        boolean isTradeCert = true;
        boolean isAgentCert = true;
        boolean isCommonCarrierCert = true;
        String msg = "No Message";
        ApplicationDisposeImpl impl = new ApplicationDisposeImpl();
        stateCd = Util.getUserStateCode();
        offCode = Util.getSelectedSeat().getOff_cd();
        empCd = Util.getEmpCode();

        if (this.empCd == null || this.empCd.trim().equalsIgnoreCase("")) {
            msg = Util.getLocaleSessionMsg();
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
            return;
        }
        if (this.appl_no == null || this.appl_no.trim().equalsIgnoreCase("")) {
            msg = Util.getLocaleMsg("apllNoRequiredMsg");
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
            return;
        }
        try {
            RestoreDisposeApplicationImpl restoreDispose = new RestoreDisposeApplicationImpl();
            purCodeList = restoreDispose.listRestoreApplicationDispose(appl_no, stateCd, offCode, Long.parseLong(empCd), Util.getUserCategory());
            if (purCodeList == null || purCodeList.isEmpty()) {
                msg = Util.getLocaleMsg("novalidTrans");
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            regn_no = ApplicationDisposeImpl.getRegNo(appl_no);
            if (CommonUtils.isNullOrBlank(regn_no)) {
                regn_no = RestoreDisposeApplicationImpl.getRegNoOfDisposeApplNo(appl_no, null);
            }

            if (!CommonUtils.isNullOrBlank(regn_no) && !regn_no.equalsIgnoreCase("NEW") && !regn_no.equalsIgnoreCase("TEMPREG")) {
                boolean checkApproved = restoreDispose.checkApproved(appl_no, stateCd, offCode, regn_no);
            }
            OwnerImpl owner_Impl = new OwnerImpl();
            Map<String, Integer> map = purCodeList;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                        || entry.getValue() == TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                        || entry.getValue() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                        || entry.getValue() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE
                        || entry.getValue() == TableConstants.ADMIN_OWNER_DATA_CHANGE
                        || entry.getValue() == TableConstants.STATEADMIN_OWNER_DATA_CHANGE
                        || entry.getValue() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS) {//add
                    ownerDetail = restoreDispose.getVhaOwnerDetails(this.appl_no.trim(), stateCd, offCode);
                    if (ownerDetail != null && regn_no != null && regn_no.trim().length() > 0) {
                        ownerDetail.setRegn_no(regn_no);
                    }
                    pur_cd = entry.getValue();
                    ApplDisposeVerifyByAdminDobj applDisposeVerifyByAdminDobj = impl.getApplicationDisposedVerificationDetails(this.appl_no);
                    if (!regn_no.equalsIgnoreCase("NEW")
                            && !regn_no.equalsIgnoreCase("TEMPREG")
                            && ownerDetail != null
                            && !ownerDetail.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)
                            && !ownerDetail.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                            && pur_cd != TableConstants.ADMIN_OWNER_DATA_CHANGE//addamit
                            && pur_cd != TableConstants.STATEADMIN_OWNER_DATA_CHANGE
                            && applDisposeVerifyByAdminDobj == null) {
                        msg = Util.getLocaleMsg("home_rightpanelregno") + regn_no.toUpperCase() + Util.getLocaleMsg("restore_alrdy");
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, msg, msg));
                        return;
                    }
                }
                if (entry.getValue() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                        || entry.getValue() == TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                        || entry.getValue() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                        || entry.getValue() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                    if (!CommonUtils.isNullOrBlank(regn_no)) {
                        String purCd = TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + "," + TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE;
                        RestoreDisposeApplicationImpl.getRegNoOfDisposeApplNo(appl_no, purCd);
                        restoreDispose.checkChassisNoInVaVtOwner(ownerDetail.getChasi_no());
                    }
                }

                if ((entry.getValue() == TableConstants.VM_TRANSACTION_TRADE_CERT_NEW
                        || entry.getValue() == TableConstants.VM_TRANSACTION_TRADE_CERT_DUP
                        || entry.getValue() == TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD
                        || entry.getValue() == TableConstants.VM_PMT_APPLICATION_PUR_CD
                        || entry.getValue() == TableConstants.VM_PMT_FRESH_PUR_CD
                        || entry.getValue() == TableConstants.AGENT_DETAIL_PUR_CD
                        || entry.getValue() == TableConstants.AGENT_DETAIL_DUP_PUR_CD
                        || entry.getValue() == TableConstants.AGENT_DETAIL_REN_PUR_CD
                        || entry.getValue() == TableConstants.VM_TRANSACTION_CARRIER_REGN
                        || entry.getValue() == TableConstants.VM_TRANSACTION_CARRIER_RENEWAL)
                        && (regn_no.equalsIgnoreCase("NEW"))) {
                    isTradeCert = false;
                    isAgentCert = false;
                    isCommonCarrierCert = false;
                }

            }
            if (ownerDetail != null && ownerDetail.getChasi_no() != null && (pur_cd == TableConstants.ADMIN_OWNER_DATA_CHANGE || pur_cd == TableConstants.STATEADMIN_OWNER_DATA_CHANGE) && !CommonUtils.isNullOrBlank(ServerUtil.getChassisNoExist(ownerDetail.getChasi_no()))) {
                restoreDisposeDobj.setCheckPurcdOwnerAdmin(true);
            }
            if (pur_cd != TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                    && pur_cd != TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                    && pur_cd != TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                    && pur_cd != TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE
                    && !regn_no.equalsIgnoreCase("NEW")
                    && !regn_no.equalsIgnoreCase("TEMPREG")
                    && pur_cd != TableConstants.ADMIN_OWNER_DATA_CHANGE
                    && pur_cd != TableConstants.STATEADMIN_OWNER_DATA_CHANGE) {//addamit
                ownerDetail = owner_Impl.getOwnerDetails(this.regn_no.trim());
            }
            if (ownerDetail == null && isTradeCert && !isAgentCert && !isCommonCarrierCert) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", Util.getLocaleMsg("notFoundAgnstApplno")));
                return;
            }
            if (ownerDetail != null) {
                //for Owner Identification Fields disallow typing
                if (ownerDetail.getOwnerIdentity() != null) {
                    ownerDetail.getOwnerIdentity().setFlag(true);
                    ownerDetail.getOwnerIdentity().setMobileNoEditable(true);
                }
                if (ownerDetail.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_EXARMY)) {
                    ExArmyDobj dobj = null;
                    if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE || pur_cd == TableConstants.ADMIN_OWNER_DATA_CHANGE || pur_cd == TableConstants.STATEADMIN_OWNER_DATA_CHANGE) {
                        dobj = ExArmyImpl.setExArmyDetails_db_to_dobj(null, appl_no, stateCd, offCode);
                    } else {
                        dobj = ExArmyImpl.setExArmyDetails_db_to_dobj(null, regn_no, ownerDetail.getState_cd(), ownerDetail.getOff_cd());
                    }
                    if (dobj == null) {
                        dobj = restoreDispose.setVHAExArmyDetails_db_to_dobj(appl_no, regn_no, stateCd, pur_cd);
                    }
                    if (dobj != null) {
                        exArmyBean.setDobj_To_Bean(dobj);
                    }
                }
                if (ownerDetail.getVehTypeDescr().equalsIgnoreCase(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR)) {
                    AxleDetailsDobj dobj = null;
                    if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE || pur_cd == TableConstants.ADMIN_OWNER_DATA_CHANGE || pur_cd == TableConstants.STATEADMIN_OWNER_DATA_CHANGE) {
                        dobj = AxleImpl.setAxleVehDetails_db_to_dobj(null, appl_no, stateCd, offCode);
                    } else {
                        dobj = AxleImpl.setAxleVehDetails_db_to_dobj(null, regn_no, ownerDetail.getState_cd(), ownerDetail.getOff_cd());
                    }
                    if (dobj == null) {
                        dobj = restoreDispose.setVHAAxleVehDetails_db_to_dobj(appl_no);
                    }
                    if (dobj != null) {
                        axleBean.setDobj_To_Bean(dobj);
                    }
                }
                //#################### Insurance Details Filler Start ##############
                InsDobj ins_dobj = null;
                if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE || pur_cd == TableConstants.ADMIN_OWNER_DATA_CHANGE || pur_cd == TableConstants.STATEADMIN_OWNER_DATA_CHANGE) {
                    ins_dobj = InsImpl.set_ins_dtls_db_to_dobj(null, appl_no, stateCd, offCode);
                } else {
                    ins_dobj = InsImpl.set_ins_dtls_db_to_dobj(this.regn_no.trim(), null, ownerDetail.getState_cd(), ownerDetail.getOff_cd());
                }
                if (ins_dobj == null) {
                    ins_dobj = restoreDispose.set_VHAins_dtls_db_to_dobj(appl_no);
                }
                if (ins_dobj != null) {
                    ins_bean.set_Ins_dobj_to_bean(ins_dobj);
                }
                //#################### Insurance Details Filler End ################

                //################### Hypothecation Details Filler Start ###########
                HpaImpl hpa_Impl = new HpaImpl();
                List<HpaDobj> hypth = null;
                if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE || pur_cd == TableConstants.ADMIN_OWNER_DATA_CHANGE || pur_cd == TableConstants.STATEADMIN_OWNER_DATA_CHANGE) {
                    hypth = hpa_Impl.getHypoDetails(this.appl_no.trim(), TableConstants.VM_TRANSACTION_MAST_REM_HYPO, null, true, stateCd);
                } else {
                    hypth = hpa_Impl.getHypoDetails(null, TableConstants.VM_TRANSACTION_MAST_REM_HYPO, this.regn_no.trim(), true, ownerDetail.getState_cd());
                }
                if (hypth == null || hypth.isEmpty()) {
                    hypth = restoreDispose.getVHAHypoDetails(appl_no, pur_cd);
                }
                hypothecationDetails_bean.setListHypthDetails(hypth);
                //################### Hypothecation Details Filler End #############                
            }

            //for showing paid fee /tax details 
            feeTaxPaidList = new PermitPaidFeeDtlsImpl().getListOfPaidFee(this.appl_no.trim());
            ins_bean.componentReadOnly(false);
            render = true;

        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error!", Util.getLocaleMsg("notFoundAppln")));
        }
    }

    public void reStoreApplications() {
        Exception e = null;
        Status_dobj status = null;
        String msg = null;
        Owner_dobj ownerDobj = null;
        if (selectedPurposeCode.isEmpty()) {
            JSFUtils.showMessagesInDialog("Alert!", Util.getLocaleMsg("chooseOption"), FacesMessage.SEVERITY_WARN);
            return;
        }
        try {
            if (ownerDetail != null && ownerDetail.getState_cd() != null && ownerDetail.getState_cd().trim().length() > 0) {
                stateCd = ownerDetail.getState_cd();
                OwnerImpl ownerImpl = new OwnerImpl();
                ownerDobj = ownerImpl.getOwnerDobj(ownerDetail);
            }
            RestoreDisposeApplicationImpl restoreDispose = new RestoreDisposeApplicationImpl();
            for (int i = 0; i < selectedPurposeCode.size(); i++) {
                status = new Status_dobj();
                status.setAppl_no(appl_no);
                status.setRegn_no(regn_no);
                status.setEmp_cd(Long.parseLong(empCd));
                status.setPur_cd(Integer.parseInt(selectedPurposeCode.get(i)));
                status.setOffice_remark(reason);
                status.setState_cd(stateCd);
                status.setOff_cd(offCode);
                restoreDispose.reStoreDisposeApplications(status, selectedPurposeCode.size(), purCodeList.size(), ownerDobj, restoreDisposeDobj);
            }
        } catch (VahanException vex) {
            e = vex;
        } catch (Exception ex) {
            e = ex;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        if (e != null && e instanceof VahanException) {
            msg = Util.getLocaleMsg("reason_notRestore") + e.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
            render = false;
            appl_no = "";
            reason = "";
            selectedPurposeCode.clear();
            PrimeFaces.current().ajax().update("formRestoreApplNo:panelOwnerInfo");
            return;
        }
        if (e != null) {
            msg = Util.getLocaleMsg("restore_excep");
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
            return;
        }
        msg = Util.getLocaleMsg("restore_success");
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg + appl_no, msg + appl_no));
        render = false;
        appl_no = "";
        reason = "";
        selectedPurposeCode.clear();
        PrimeFaces.current().ajax().update("formRestoreApplNo");
        PrimeFaces.current().ajax().update("formRestoreApplNo:panelOwnerInfo");
    }

    public void sendOtpMailForConfirm(String otpType) {
        try {
            String msg = "";
            TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
            if (tmConfigurationDobj != null && tmConfigurationDobj.getTmConfigOtpDobj() != null && tmConfigurationDobj.getTmConfigOtpDobj().isRestoreDisposeApplicationOtp()) {
                boolean isverified = UserDAO.getVerificationDetails(empCd);
                if (isverified) {
                    if (otpType != null && otpType.equals("sendOtp") || otpType.equals("resendOtp")) {
                        msg = Util.getLocaleMsg("restore_sendotp");
                        disposeRcptOtp = SmsMailOTPImpl.sendOTPorMail(empCd, msg + appl_no + ".", otpType, disposeRcptOtp, msg + appl_no + ".");
                        if (disposeRcptOtp != null && !disposeRcptOtp.equals("")) {
                            PrimeFaces.current().ajax().update("formRestoreApplNo:otp_confirmation");
                            PrimeFaces.current().executeScript("PF('otp_confrm').show()");
                        } else {
                            throw new VahanException(Util.getLocaleMsg("otp_unable"));
                        }
                    } else if (otpType.equals("confirmOtp")) {
                        if (disposeRcptOtp.equals(enterRcptOtp)) {
                            reStoreApplications();
                            PrimeFaces.current().ajax().update("formRestoreApplNo:otp_confirmation");
                            PrimeFaces.current().ajax().update("formRestoreApplNo:msg");
                            PrimeFaces.current().executeScript("PF('otp_confrm').hide()");
                        } else {
                            msg = Util.getLocaleMsg("otp_invalid");
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
                        }
                    }
                } else {
                    msg = Util.getLocaleMsg("restore_mandatory");
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
                }
            } else {
                reStoreApplications();
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ve.getMessage(), ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleSomthingMsg(), Util.getLocaleSomthingMsg()));
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
     * @return the pur_cd
     */
    public int getPur_cd() {
        return pur_cd;
    }

    /**
     * @param pur_cd the pur_cd to set
     */
    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
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
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the selectedPurposeCode
     */
    public List<String> getSelectedPurposeCode() {
        return selectedPurposeCode;
    }

    /**
     * @param selectedPurposeCode the selectedPurposeCode to set
     */
    public void setSelectedPurposeCode(List<String> selectedPurposeCode) {
        this.selectedPurposeCode = selectedPurposeCode;
    }

    /**
     * @return the purCodeList
     */
    public Map<String, Integer> getPurCodeList() {
        return purCodeList;
    }

    /**
     * @param purCodeList the purCodeList to set
     */
    public void setPurCodeList(Map<String, Integer> purCodeList) {
        this.purCodeList = purCodeList;
    }

    /**
     * @param ins_bean the ins_bean to set
     */
    public void setIns_bean(InsBean ins_bean) {
        this.ins_bean = ins_bean;
    }

    public InsBean getIns_bean() {
        return ins_bean;
    }

    /**
     * @param hypothecationDetails_bean the hypothecationDetails_bean to set
     */
    public void setHypothecationDetails_bean(HypothecationDetailsBean hypothecationDetails_bean) {
        this.hypothecationDetails_bean = hypothecationDetails_bean;
    }

    public HypothecationDetailsBean getHypothecationDetails_bean() {
        return hypothecationDetails_bean;
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
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
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
     * @return the action_cd
     */
    public int getAction_cd() {
        return action_cd;
    }

    /**
     * @param action_cd the action_cd to set
     */
    public void setAction_cd(int action_cd) {
        this.action_cd = action_cd;
    }

    /**
     * @return the feeTaxPaidList
     */
    public List<PermitPaidFeeDtlsDobj> getFeeTaxPaidList() {
        return feeTaxPaidList;
    }

    /**
     * @param feeTaxPaidList the feeTaxPaidList to set
     */
    public void setFeeTaxPaidList(List<PermitPaidFeeDtlsDobj> feeTaxPaidList) {
        this.feeTaxPaidList = feeTaxPaidList;
    }

    /**
     * @return the disposeRcptOtp
     */
    public String getCancelRcptOtp() {
        return disposeRcptOtp;
    }

    /**
     * @param disposeRcptOtp the disposeRcptOtp to set
     */
    public void setCancelRcptOtp(String disposeRcptOtp) {
        this.disposeRcptOtp = disposeRcptOtp;
    }

    /**
     * @return the enterRcptOtp
     */
    public String getEnterRcptOtp() {
        return enterRcptOtp;
    }

    /**
     * @param enterRcptOtp the enterRcptOtp to set
     */
    public void setEnterRcptOtp(String enterRcptOtp) {
        this.enterRcptOtp = enterRcptOtp;
    }

    public String getDisposeRcptOtp() {
        return disposeRcptOtp;
    }

    public void setDisposeRcptOtp(String disposeRcptOtp) {
        this.disposeRcptOtp = disposeRcptOtp;
    }
}
