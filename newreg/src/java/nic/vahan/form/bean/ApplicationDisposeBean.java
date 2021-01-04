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
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.permit.PermitPaidFeeDtlsDobj;
import nic.vahan.form.impl.ApplicationDisposeImpl;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.ExArmyImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.SmsMailOTPImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.ToImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.PermitPaidFeeDtlsImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
//import org.primefaces.context.RequestContext;

@ViewScoped
@ManagedBean(name = "applicationDisposeBean")
public class ApplicationDisposeBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ApplicationDisposeBean.class);
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
    @ManagedProperty(value = "#{ins_bean}")
    private InsBean ins_bean;
    @ManagedProperty(value = "#{hypothecationDetails_bean}")
    private HypothecationDetailsBean hypothecationDetails_bean;
    @ManagedProperty(value = "#{exArmyBean}")
    private ExArmyBean exArmyBean;
    @ManagedProperty(value = "#{axleBean}")
    private AxleBean axleBean;
    private int action_cd;
    private List<PermitPaidFeeDtlsDobj> feeTaxPaidList = null;
    String userCd = null;
    private String disposeRcptOtp = null;
    private String enterRcptOtp = null;

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
            msg = "Session Expired. Please try again.";
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
            return;
        }
        if (this.appl_no == null || this.appl_no.trim().equalsIgnoreCase("")) {
            msg = "Please Enter Valid Application Number";
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
            return;
        }

        try {
            //ApplicationDisposeImpl.checkPmtAppIsApprove(appl_no);
            purCodeList = ApplicationDisposeImpl.listApplicationDispose(appl_no, stateCd, offCode, Long.parseLong(empCd), Util.getUserCategory());

            if (purCodeList == null || purCodeList.isEmpty()) {
                msg = "There is no Valid Transaction for Disposing Against this Application No";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }

            regn_no = ApplicationDisposeImpl.getRegNo(appl_no);
            action_cd = ApplicationDisposeImpl.getActionCode(appl_no);

            OwnerImpl owner_Impl = new OwnerImpl();
            Map<String, Integer> map = purCodeList;

            for (Map.Entry<String, Integer> entry : map.entrySet()) {

                if (entry.getValue() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                        || entry.getValue() == TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                        || entry.getValue() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                        || entry.getValue() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE
                        || entry.getValue() == TableConstants.ADMIN_OWNER_DATA_CHANGE
                        || entry.getValue() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS
                        || entry.getValue() == TableConstants.STATEADMIN_OWNER_DATA_CHANGE) {//addamit

                    ownerDetail = owner_Impl.getVaOwnerDetails(this.appl_no.trim(), stateCd, offCode);
                    if (ownerDetail != null && regn_no != null && regn_no.trim().length() > 0) {
                        ownerDetail.setRegn_no(regn_no);
                    }
                    pur_cd = entry.getValue();

                    //for allowing dispose when office administrator allowed to be disposed.
                    ApplDisposeVerifyByAdminDobj applDisposeVerifyByAdminDobj = impl.getApplicationDisposedVerificationDetails(this.appl_no);

                    if (!regn_no.equalsIgnoreCase("NEW")
                            && !regn_no.equalsIgnoreCase("TEMPREG")
                            && ownerDetail != null
                            && !ownerDetail.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)
                            && !ownerDetail.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                            && pur_cd != TableConstants.ADMIN_OWNER_DATA_CHANGE//addamit
                            && pur_cd != TableConstants.STATEADMIN_OWNER_DATA_CHANGE
                            && applDisposeVerifyByAdminDobj == null) {
                        msg = "Registration No " + regn_no.toUpperCase() + " is Already Generated. You Can not Dispose the Application!";
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, msg, msg));
                        return;
                    }

                    boolean dealerCheckForDispose = impl.dealerCheckForDispose(this.appl_no);
                    if (dealerCheckForDispose) {
                        if (!impl.dealerCheckForTempRegDispose(appl_no)) {
                            dealerCheckForDispose = false;
                        }
                    }
                    if (dealerCheckForDispose && applDisposeVerifyByAdminDobj == null) {
                        msg = "Not Allowed to Dispose Off the Application as payment has been made!";
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, msg, msg));
                        return;
                    }

                    if (entry.getValue() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || entry.getValue() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                        if (action_cd == TableConstants.TM_ROLE_DEALER_CART_PAYMENT) {
                            msg = "Application is Pending at Cart Payment.Please Rollback the Application to Dispose Off!";
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, msg, msg));
                            return;
                        }
//                        boolean dealerCheckForDispose = new ApplicationDisposeImpl().dealerCheckForDispose(this.appl_no);
//                        if (dealerCheckForDispose) {
//                            msg = "Not Allowed to Dispose Off the Application!";
//                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, msg, msg));
//                            return;
//                        }
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
            if (ownerDetail == null && (pur_cd == TableConstants.ADMIN_OWNER_DATA_CHANGE || pur_cd == TableConstants.STATEADMIN_OWNER_DATA_CHANGE)) {
                ownerDetail = owner_Impl.getVaOwnerAdminTempDetails(this.appl_no.trim(), stateCd, offCode);
                if (ownerDetail != null && regn_no != null && regn_no.trim().length() > 0) {
                    ownerDetail.setRegn_no(regn_no);
                }
            }

            if (pur_cd != TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                    && pur_cd != TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                    && pur_cd != TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                    && pur_cd != TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE
                    && !regn_no.equalsIgnoreCase("NEW")
                    && !regn_no.equalsIgnoreCase("TEMPREG")
                    && pur_cd != TableConstants.ADMIN_OWNER_DATA_CHANGE
                    && pur_cd != TableConstants.STATEADMIN_OWNER_DATA_CHANGE) {//addamit

                //Get alloted off_cd for permit --NAMAN JAIN
                /*String offCd = impl.getlistOfOffCdForPermit(appl_no, stateCd, offCode);
                 if (CommonUtils.isNullOrBlank(offCd)) {
                 offCd = String.valueOf(Util.getSelectedSeat().getOff_cd());
                 }*/
//                if (pur_cd != TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD) {
//                    SurrenderPermitImpl surrImpl = new SurrenderPermitImpl();
//                    regn_no = surrImpl.getReAssigmentOldToNew(this.regn_no.trim());
//                }
                ownerDetail = owner_Impl.getOwnerDetails(this.regn_no.trim());

            }

            if (ownerDetail == null && isTradeCert && !isAgentCert && !isCommonCarrierCert) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number Not Found against this Application No"));
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
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error!", "Registration Number Not Found"));
        }

    }

    public void disposeApplications() {

        Exception e = null;
        Status_dobj status = null;
        ApplicationDisposeImpl impl = null;
        boolean isSameDayDispose = false;
        boolean isOnlinePaymentOpted = false;
        String msg = null;
        Owner_dobj ownerDobj = null;

        if (selectedPurposeCode.isEmpty()) {
            JSFUtils.showMessagesInDialog("Alert!", "You have Not Selected any Option", FacesMessage.SEVERITY_WARN);
            return;
        }

        try {
            impl = new ApplicationDisposeImpl();

            isSameDayDispose = impl.disposeSameDayCheck(appl_no, stateCd, offCode);
            if (isSameDayDispose) {
                msg = "You Can't Dispose the Application on Same Day Before Cancellation of the Receipt, go for the Cancellation of the Receipt First or Try Next Day.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
                render = false;
                appl_no = "";
                reason = "";
                selectedPurposeCode.clear();
                // RequestContext.getCurrentInstance().update("panelOwnerInfo");
                return;
            }

            //Online Payment opted check
            isOnlinePaymentOpted = impl.disposeOnlinePaymentOptedCheck(appl_no);
            if (isOnlinePaymentOpted) {
                msg = "Vehicle Owner Opted for Online Payment. For Application Dispose first Cancel the Online Payment Request.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
                render = false;
                appl_no = "";
                reason = "";
                selectedPurposeCode.clear();
                PrimeFaces.current().ajax().update("panelOwnerInfo");
                return;
            }

            //check for disposing the compulsory purpose code if vehicle is hypothecated start
            boolean isHypth = false;
            int mandatoryDisposedListCountforTO = 0;
            int mandatoryDisposedListCountforNOC = 0;

            if (ownerDetail != null && ownerDetail.getState_cd() != null && ownerDetail.getState_cd().trim().length() > 0) {
                stateCd = ownerDetail.getState_cd();
                OwnerImpl ownerImpl = new OwnerImpl();
                ownerDobj = ownerImpl.getOwnerDobj(ownerDetail);
            }

            isHypth = ServerUtil.checkHypthOrNot(regn_no, stateCd);
            if (isHypth) {
                Map<String, Integer> map = purCodeList;
                boolean hptOrHpcSelected = false;
                boolean toSelected = false;
                boolean allPurCdselected = false;
                if (map.size() == selectedPurposeCode.size()) {
                    allPurCdselected = true;
                }
                for (Map.Entry<String, Integer> entry : map.entrySet()) {

                    //checking if mandatory list is selected with Transfer of Ownership(TO) or not
                    if (entry.getValue() == TableConstants.VM_TRANSACTION_MAST_TO) {

                        for (int j = 0; j < selectedPurposeCode.size(); j++) {
                            if (Integer.parseInt(selectedPurposeCode.get(j)) == TableConstants.VM_TRANSACTION_MAST_TO) {
                                toSelected = true;
                            }
                            if (Integer.parseInt(selectedPurposeCode.get(j)) == TableConstants.VM_TRANSACTION_MAST_HPC
                                    || Integer.parseInt(selectedPurposeCode.get(j)) == TableConstants.VM_TRANSACTION_MAST_REM_HYPO) {
                                //mandatoryDisposedListCountforTO++;
                                hptOrHpcSelected = true;
                            }
                        }
                    }

                    if ((toSelected && !hptOrHpcSelected)) {
                        if (!allPurCdselected) {
                            msg = "Vehicle is Hypothecated, You have to Dispose TO with HPT/HPC also.";
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", msg));
                            return;
                        }

                    }

                    //checking if mandatory list is selected with NOC or not
                    if (entry.getValue() == TableConstants.VM_TRANSACTION_MAST_NOC) {
                        boolean blnHpcOrHpt = false;
                        for (int j = 0; j < selectedPurposeCode.size(); j++) {
                            if (Integer.parseInt(selectedPurposeCode.get(j)) == TableConstants.VM_TRANSACTION_MAST_NOC
                                    || Integer.parseInt(selectedPurposeCode.get(j)) == TableConstants.VM_TRANSACTION_MAST_HPC
                                    || Integer.parseInt(selectedPurposeCode.get(j)) == TableConstants.VM_TRANSACTION_MAST_REM_HYPO) {
                                mandatoryDisposedListCountforNOC++;
                            }
                        }
                        ToImpl toImpl = new ToImpl();
                        String purCode = toImpl.statusList(appl_no, regn_no, stateCd);
                        if (purCode.contains("," + TableConstants.VM_TRANSACTION_MAST_NOC + ",") && (purCode.contains("," + TableConstants.VM_TRANSACTION_MAST_HPC + ",") || purCode.contains("," + TableConstants.VM_TRANSACTION_MAST_REM_HYPO + ","))) {
                            blnHpcOrHpt = true;
                        }
                        if (mandatoryDisposedListCountforNOC < 2 && purCodeList.size() > 1 && blnHpcOrHpt) {
                            msg = "Vehicle is Hypothecated, You have to Dispose NOC with HPT/HPC also.";
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", msg));
                            return;
                        }
                    }
                }
            }//check for disposing the compulsory purpose code if vehicle is hypothecated end

            // Checking for application pending at Dealer Cart Payment
            action_cd = ApplicationDisposeImpl.getActionCode(appl_no);
            for (Map.Entry<String, Integer> entry : purCodeList.entrySet()) {
                if (entry.getValue() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || entry.getValue() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                    if (action_cd == TableConstants.TM_ROLE_DEALER_CART_PAYMENT) {
                        msg = "Application is Pending at Cart Payment.Please Rollback the Application to Dispose Off!";
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, msg, msg));
                        return;
                    }
                }
            }

            for (int i = 0; i < selectedPurposeCode.size(); i++) {

                status = new Status_dobj();
                status.setAppl_no(appl_no);
                status.setRegn_no(regn_no);
                status.setEmp_cd(Long.parseLong(empCd));
                status.setPur_cd(Integer.parseInt(selectedPurposeCode.get(i)));
                status.setOffice_remark(reason);
                status.setState_cd(stateCd);
                status.setOff_cd(offCode);
                impl.disposeApplications(status, selectedPurposeCode.size(), purCodeList.size(), ownerDobj);
            }

        } catch (VahanException vex) {
            e = vex;
        } catch (Exception ex) {
            e = ex;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

        if (e != null && e instanceof VahanException) {
            msg = "Application can not be Disposed due to " + e.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
            render = false;
            appl_no = "";
            reason = "";
            selectedPurposeCode.clear();
            PrimeFaces.current().ajax().update("panelOwnerInfo");
            //RequestContext.getCurrentInstance().update("panelOwnerInfo");
            return;
        }

        if (e != null) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exception Occured - Application Could not Disposed", "Exception Occured - Application Could not Disposed"));
            return;
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Transaction Disposed Successfully for Application Number " + appl_no, "Transaction Disposed Successfully for Application Number " + appl_no));
        render = false;
        appl_no = "";
        reason = "";
        selectedPurposeCode.clear();
        PrimeFaces.current().ajax().update("formGenApplNo");
        PrimeFaces.current().ajax().update("panelOwnerInfo");
    }

    public void sendOtpMailAndCancelReceipt(String otpType) {
        try {
            TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
            if (tmConfigurationDobj != null && tmConfigurationDobj.getTmConfigOtpDobj() != null && tmConfigurationDobj.getTmConfigOtpDobj().isAppl_dispose_with_otp()) {
                boolean isverified = UserDAO.getVerificationDetails(empCd);
                if (isverified) {
                    if (otpType != null && otpType.equals("sendOtp") || otpType.equals("resendOtp")) {
                        disposeRcptOtp = SmsMailOTPImpl.sendOTPorMail(empCd, "OTP for Disposing of Application No. " + appl_no + ".", otpType, disposeRcptOtp, "OTP for Disposing of Application No. " + appl_no + ".");
                        if (disposeRcptOtp != null && !disposeRcptOtp.equals("")) {
                            PrimeFaces.current().ajax().update("otp_confirmation");
                            PrimeFaces.current().executeScript("PF('otp_confrm').show()");
                        } else {
                            throw new VahanException("Unable to generate OTP and send it.");
                        }
                    } else if (otpType.equals("confirmOtp")) {
                        if (disposeRcptOtp.equals(enterRcptOtp)) {
                            disposeApplications();
                            //PrimeFaces.current().ajax().update("panelApplInward formGenApplNo msg otp_text otp_confirmation");
                            PrimeFaces.current().executeScript("PF('otp_confrm').hide()");
                        } else {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Invalid OTP, Please enter correct OTP", "Invalid OTP, Please enter correct OTP"));
                        }
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "OTP is mandatory for disposing the application and your Mobile no is not verified, please verify first from UPDATE PROFILE module.", "OTP is mandatory for disposing the application and your Mobile no is not verified, please verify first from UPDATE PROFILE module."));
                }
            } else {
                disposeApplications();
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ve.getMessage(), ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
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
}
