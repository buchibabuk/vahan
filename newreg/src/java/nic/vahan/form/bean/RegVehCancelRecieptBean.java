/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import dao.UserDAO;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.impl.ApplicationInwardImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.SmsMailOTPImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.RegVehCancelReceiptImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

@ViewScoped
@ManagedBean(name = "regVehCancelRecieptBean")
public class RegVehCancelRecieptBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(RegVehCancelRecieptBean.class);
    private String regn_no;
    private String appl_no;
    private String rcptNo;
    private String reason;
    private List<Integer> selectedPurposeCode;
    private String message = null;
    private boolean render = false;
    private Map<String, Integer> purCodeList = null;
    private List<Map<String, Integer>> receiptList = null;
    private OwnerDetailsDobj ownerDetail;
    @ManagedProperty(value = "#{ins_bean}")
    private InsBean ins_bean;
    @ManagedProperty(value = "#{hypothecationDetails_bean}")
    private HypothecationDetailsBean hypothecationDetails_bean;
    int totalFee = 0;
    Integer offCd = null;
    String stateCd = null;
    boolean advanceRegn = false;
    String userCd = null;
    private String cancelRcptOtp = null;
    private String enterRcptOtp = null;

    public RegVehCancelRecieptBean() {
        stateCd = Util.getUserStateCode();
        offCd = Util.getSelectedSeat().getOff_cd();
        userCd = Util.getEmpCode();
    }

    public void showDetails() {

        String INVALID_USER = "U";
        String SAME_DATE = "D";
        String APPROVED_STATUS = "A";
        String ONLINE_TAX_MODE = "I";
        int pur_cd = 0;
        advanceRegn = false;
        String alertMsg = null;
        boolean isRegnNoAvailable = false;
        boolean fitnessApprovalStatus = false;
        TmConfigurationDobj configDobj = null;

        if (this.rcptNo.trim() == null || this.rcptNo.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please Enter Valid Receipt Number"));
            return;
        }

        try {
            RegVehCancelReceiptImpl impl = new RegVehCancelReceiptImpl();
            receiptList = impl.transactionListOfCancelReciept(rcptNo, stateCd, offCd, Util.getEmpCode(), Util.getSelectedSeat().getAction_cd());

            if (receiptList == null || receiptList.isEmpty() || receiptList.get(0) == null || receiptList.get(0).isEmpty()) {

                //check for advance registration no without fee 
                if (Util.getSelectedSeat().getAction_cd() == TableConstants.CANCEL_CASH_RCPT_ADMIN) {
                    AdvanceRegnNo_dobj advanceRegnNoDobj = impl.getAdvanceRegnNoDobj(rcptNo, stateCd, offCd);
                    if (advanceRegnNoDobj != null && advanceRegnNoDobj.getTotal_amt() == 0) {
                        totalFee = (int) advanceRegnNoDobj.getTotal_amt();
                        ApplicationInwardImpl applicationInwardImpl = new ApplicationInwardImpl();
                        String isApplApproved = applicationInwardImpl.isApplApprovedByApplNo(advanceRegnNoDobj.getRegn_appl_no(), TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE, offCd, stateCd);
                        if (isApplApproved != null && isApplApproved.equalsIgnoreCase(TableConstants.STATUS_APPROVED)) {
                            alertMsg = "You Can't Cancel this Receipt due to Application No " + advanceRegnNoDobj.getRegn_appl_no() + " Mapped with this Receipt is Approved.";
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, alertMsg, alertMsg));
                            return;
                        }
                        advanceRegn = true;
                    } else {
                        alertMsg = "Invalid Receipt or Receipt not Found";
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", alertMsg));
                        return;
                    }
                } else {
                    alertMsg = "Invalid Receipt or Receipt not Found (if Total Fees is Zero then Only Office Administrator Can Cancel this Receipt)";
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", alertMsg));
                    return;
                }
            } else { // Receipt Cancellation Normal Cases Except Advacne Registration Without Fee
                appl_no = impl.getApplNo(rcptNo, stateCd, offCd);
                configDobj = Util.getTmConfiguration();

                if (appl_no != null) {
                    regn_no = impl.getRegNo(appl_no, rcptNo, stateCd, offCd);
                }

                purCodeList = receiptList.get(0);
                boolean isBalanceFeeTaxReceipt = impl.isBalanceFeeTaxCollectionReceipt(appl_no, rcptNo, stateCd, offCd);
                Map<String, Integer> map = purCodeList;
                for (Map.Entry<String, Integer> entry : map.entrySet()) {

                    if (entry.getKey().equalsIgnoreCase(INVALID_USER)) {
                        alertMsg = "You are not Authorized to Cancel this Receipt!";
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, alertMsg, alertMsg));
                        return;
                    }

                    if (entry.getKey().equalsIgnoreCase(SAME_DATE)) {
                        alertMsg = "You can Cancel this Receipt only on the Same Day. Please go for Application Dispose!";
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, alertMsg, alertMsg));
                        return;
                    }

                    if (entry.getKey().equalsIgnoreCase(APPROVED_STATUS)) {
                        alertMsg = "One of the Transaction has been Approved for this Receipt!";
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, alertMsg, alertMsg));
                        return;
                    }

                    if (entry.getKey().equalsIgnoreCase(ONLINE_TAX_MODE)) {
                        alertMsg = "Cancellation of this Receipt Can't be Performed Reason is Receipt Generated through Online Mode";
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, alertMsg, alertMsg));
                        return;
                    }

                    if (entry.getValue() == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                        fitnessApprovalStatus = impl.getFitnessApprovalStatus(appl_no, TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL, stateCd, offCd);
                        if (fitnessApprovalStatus) {
                            alertMsg = "Cancellation of this Receipt Can't be Performed due to Application No [" + appl_no + "] is Approved for Fitness / Inspection";
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, alertMsg, alertMsg));
                            return;
                        }
                    }

                    if (configDobj != null && configDobj.getRegn_gen_type().equalsIgnoreCase(TableConstants.NO_GEN_TYPE_MIX_P)) {
                        String regnAlloted = ServerUtil.getRegnNoAllotedDetail(appl_no, stateCd, offCd);
                        if (!CommonUtils.isNullOrBlank(regnAlloted)) {
                            alertMsg = "Registration No " + regnAlloted.toUpperCase() + " is already Generated.Fee Can't be Cancelled.Please Contact to Office Administrator!!!";
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, alertMsg, alertMsg));
                            return;
                        }
                    }

                    if (entry.getValue() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                            || entry.getValue() == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {

                        if (!this.regn_no.isEmpty() && !regn_no.equalsIgnoreCase("NEW") && !regn_no.equalsIgnoreCase("TEMPREG") && Util.getSelectedSeat().getAction_cd() != TableConstants.CANCEL_CASH_RCPT_ADMIN) {

                            alertMsg = "Registration No " + regn_no.toUpperCase() + " is already Generated.Fee Can't be Cancelled.Please Contact to Office Administrator!!!";
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, alertMsg, alertMsg));
                            return;
                        }

                        if (!this.regn_no.isEmpty() && !regn_no.equalsIgnoreCase("NEW") && !regn_no.equalsIgnoreCase("TEMPREG") && Util.getSelectedSeat().getAction_cd() == TableConstants.CANCEL_CASH_RCPT_ADMIN) {
                            alertMsg = "Registration No " + regn_no.toUpperCase() + " is already Generated.This Registration No will be used for Other New Application after Cancellation of this Receipt!!!";
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, alertMsg, alertMsg));
                        }

                        pur_cd = entry.getValue();
                    }

                    if (entry.getValue() == TableConstants.VM_TRANSACTION_MAST_CHOICE_NO) {

                        //for allowing access of receipt cancel to the admin if the total fee is zero
                        if (Util.getSelectedSeat().getAction_cd() == TableConstants.CANCEL_CASH_RCPT_ADMIN) {
                            String approvedStatus = impl.getApprovedStatus(rcptNo, stateCd, offCd);
                            if (approvedStatus != null && approvedStatus.equalsIgnoreCase(TableConstants.STATUS_APPROVED)) {
                                alertMsg = "Receipt Can not be Cancel due to One of the Transaction has been Approved for this Receipt!";
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, alertMsg, alertMsg));
                                return;
                            }
                        } else {
                            String advanceRegnStatus = impl.getAdvanceRegnStatus(rcptNo, stateCd, offCd);
                            if (advanceRegnStatus != null && !advanceRegnStatus.trim().isEmpty()) {
                                String advanceRegnNo = impl.getRegNo(advanceRegnStatus, null, stateCd, offCd);
                                if (advanceRegnNo != null && !advanceRegnNo.equalsIgnoreCase("NEW")) {
                                    alertMsg = "You Can't Cancel this Receipt due to Advance Registration is Processed with Application No " + advanceRegnStatus;
                                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, alertMsg, alertMsg));
                                    return;
                                }
                                appl_no = advanceRegnStatus;
                            }
                        }
                        advanceRegn = true;
                    }

                    //following condition used for skipping constraints which are not in vt_owner table
                    if (entry.getValue() == TableConstants.VM_TRANSACTION_TRADE_CERT_NEW
                            || entry.getValue() == TableConstants.VM_TRANSACTION_TRADE_CERT_DUP) {
                        isRegnNoAvailable = true;
                    }
                    //following condition used for only Mizoram for pur_cd 360 & 361
                    if ((CommonUtils.isNullOrBlank(regn_no) && isBalanceFeeTaxReceipt) || (stateCd.equalsIgnoreCase("MZ") && (entry.getValue() == TableConstants.VM_TRANSACTION_CARRIER_REGN
                            || entry.getValue() == TableConstants.VM_TRANSACTION_CARRIER_RENEWAL))) {
                        isRegnNoAvailable = true;
                    }

                }
            }

            //if pur_cd=0 in case of when there is no fee for new registration like in UK for other state case registration
            if (pur_cd == 0) {
                pur_cd = impl.getPurCd(this.appl_no.trim(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
            }
            OwnerImpl owner_Impl = new OwnerImpl();
            if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS
                    || pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                ownerDetail = owner_Impl.getVaOwnerDetails(this.appl_no.trim(), stateCd, offCd);
            } else if ((pur_cd == 0 && impl.getPurCdFromVaDetails(this.appl_no.trim(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd()) == TableConstants.VM_PMT_FRESH_PUR_CD) && this.regn_no.trim().equalsIgnoreCase("NEW")) {
                PermitOwnerDetailImpl pmt_owner = new PermitOwnerDetailImpl();
                PermitOwnerDetailDobj pmt_dobj = pmt_owner.set_VA_Owner_permit_to_dobj(this.appl_no.trim(), this.regn_no.trim());
                if (pmt_dobj != null) {
                    ownerDetail = new OwnerDetailsDobj();
                    ownerDetail.setRegn_no(pmt_dobj.getRegn_no());
                    ownerDetail.setOwner_name(pmt_dobj.getOwner_name());
                    ownerDetail.setF_name(pmt_dobj.getF_name());
                    ownerDetail.setVh_class(pmt_dobj.getVh_class());
                    ownerDetail.setC_add1(pmt_dobj.getC_add1());
                    ownerDetail.setC_add2(pmt_dobj.getC_add2());
                    ownerDetail.setC_add3(pmt_dobj.getC_add3());
                    ownerDetail.setC_district(pmt_dobj.getC_district());
                    ownerDetail.setC_pincode(pmt_dobj.getC_pincode());
                    ownerDetail.setC_state(pmt_dobj.getC_state());
                    ownerDetail.setP_add1(pmt_dobj.getP_add1());
                    ownerDetail.setP_add2(pmt_dobj.getP_add2());
                    ownerDetail.setP_add3(pmt_dobj.getP_add3());
                    ownerDetail.setP_district(pmt_dobj.getP_district());
                    ownerDetail.setP_pincode(pmt_dobj.getP_pincode());
                    ownerDetail.setP_state(pmt_dobj.getP_state());
                }
            } else {
                if (this.regn_no != null && !this.regn_no.isEmpty() && !this.regn_no.trim().equalsIgnoreCase("NEW") && !regn_no.equalsIgnoreCase("TEMPREG")) {
                    /*ApplicationDisposeImpl permitImpl = new ApplicationDisposeImpl();
                     String pmtOffCd = permitImpl.getlistOfOffCdForPermit(appl_no, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                     if (CommonUtils.isNullOrBlank(pmtOffCd)) {
                     pmtOffCd = String.valueOf(Util.getSelectedSeat().getOff_cd());
                     }*/
                    ownerDetail = owner_Impl.getOwnerDetails(this.regn_no.trim());
                    if (ownerDetail == null) {
                        ownerDetail = owner_Impl.getVaOwnerDetails(this.appl_no.trim(), stateCd, offCd);
                    }
                } else if (ownerDetail == null && this.regn_no != null && !this.regn_no.isEmpty() && !this.regn_no.trim().equalsIgnoreCase("NEW") && !regn_no.equalsIgnoreCase("TEMPREG")) {
                    ownerDetail = owner_Impl.getVaOwnerDetails(this.appl_no.trim(), stateCd, offCd);
                }
            }

            if (ownerDetail == null && !advanceRegn && !isRegnNoAvailable) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number Not Found against this Application No"));
                return;
            }

            if (ownerDetail != null) {

                //for Owner Identification Fields disallow typing
                if (ownerDetail.getOwnerIdentity() != null) {
                    ownerDetail.getOwnerIdentity().setFlag(true);
                    ownerDetail.getOwnerIdentity().setMobileNoEditable(true);
                }

                //#################### Insurance Details Filler Start ##############
                InsDobj ins_dobj = null;
                if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                    ins_dobj = InsImpl.set_ins_dtls_db_to_dobj(null, appl_no, stateCd, offCd);
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
                if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                    hypth = hpa_Impl.getHypoDetails(this.appl_no.trim(), TableConstants.VM_TRANSACTION_MAST_REM_HYPO, null, true, Util.getUserStateCode());
                } else {
                    hypth = hpa_Impl.getHypoDetails(null, TableConstants.VM_TRANSACTION_MAST_REM_HYPO, this.regn_no.trim(), true, ownerDetail.getState_cd());
                }
                hypothecationDetails_bean.setListHypthDetails(hypth);
                //################### Hypothecation Details Filler End #############
            }
            ins_bean.componentReadOnly(false);
            render = true;

        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", ve.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error!", TableConstants.SomthingWentWrong));
        }
    }

    public void sendOtpMailAndCancelReceipt(String otpType) {
        try {
            TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
            if (tmConfigurationDobj != null && tmConfigurationDobj.getTmConfigOtpDobj() != null && tmConfigurationDobj.getTmConfigOtpDobj().isRcpt_cancel_with_otp()) {
                boolean isverified = UserDAO.getVerificationDetails(userCd);
                if (isverified) {
                    if (otpType != null && otpType.equals("sendOtp") || otpType.equals("resendOtp")) {
                        cancelRcptOtp = SmsMailOTPImpl.sendOTPorMail(userCd, "OTP for cancelation of Receipt No. " + rcptNo + ".", otpType, cancelRcptOtp, "OTP for cancelation of Receipt No.  " + rcptNo + ".");
                        if (cancelRcptOtp != null && !cancelRcptOtp.equals("")) {
                            PrimeFaces.current().ajax().update("otp_confirmation");
                            PrimeFaces.current().executeScript("PF('otp_confrm').show()");
                        } else {
                            throw new VahanException("Unable to generate OTP and send it.");
                        }
                    } else if (otpType.equals("confirmOtp")) {
                        if (cancelRcptOtp.equals(enterRcptOtp)) {
                            cancellationOfRcpt();
                            //PrimeFaces.current().ajax().update("panelCancelReceipt formCancelReceipt msg otp_text otp_confirmation");
                            PrimeFaces.current().executeScript("PF('otp_confrm').hide()");
                        } else {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Invalid OTP, Please enter correct OTP", "Invalid OTP, Please enter correct OTP"));
                        }
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "OTP is mandatory for Receipt Cancellation and Your Mobile no is not verified, please verify first from UPDATE PROFILE module.", "OTP is mandatory for Receipt Cancellation and Your Mobile no is not verified, please verify first from UPDATE PROFILE module."));
                }
            } else {
                cancellationOfRcpt();
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ve.getMessage(), ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void cancellationOfRcpt() {
        Exception e = null;
        RegVehCancelReceiptImpl impl = new RegVehCancelReceiptImpl();
        String paymentMode = null;
        try {
            if (!receiptList.isEmpty() && receiptList.size() > 1 && receiptList.get(1) != null) {
                purCodeList = receiptList.get(1);
                Map<String, Integer> map = purCodeList;
                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase(TableConstants.PaymentMode_MIX)) {
                        paymentMode = TableConstants.PaymentMode_MIX;
                        break;
                    }
                }
            }
            impl.cancelReceipt(rcptNo, reason, appl_no, Util.getEmpCode(), stateCd, offCd, ownerDetail, paymentMode, totalFee, Util.getSelectedSeat().getAction_cd(), advanceRegn);
        } catch (SQLException sqle) {
            e = sqle;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (VahanException ve) {
            message = "Receipt can not be Cancelled due to " + ve.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
            return;
        } catch (Exception ex) {
            e = ex;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        if (e != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exception Occured - Receipt can not be Cancelled!", "Exception Occured - Receipt can not be Cancelled!"));
            return;
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Receipt Cancelled Successfully for Receipt Number " + rcptNo, "Receipt Cancelled Successfully for Receipt Number " + rcptNo));
        render = false;
        rcptNo = "";
        reason = "";

        PrimeFaces.current().ajax().update("formCancelReceipt");
        PrimeFaces.current().ajax().update("panelOwnerInfo");
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
    public List<Integer> getSelectedPurposeCode() {
        return selectedPurposeCode;
    }

    /**
     * @param selectedPurposeCode the selectedPurposeCode to set
     */
    public void setSelectedPurposeCode(List<Integer> selectedPurposeCode) {
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
     * @return the rcptNo
     */
    public String getRcptNo() {
        return rcptNo;
    }

    /**
     * @param rcptNo the rcptNo to set
     */
    public void setRcptNo(String rcptNo) {
        this.rcptNo = rcptNo;
    }

    /**
     * @return the receiptList
     */
    public List<Map<String, Integer>> getReceiptList() {
        return receiptList;
    }

    /**
     * @param receiptList the receiptList to set
     */
    public void setReceiptList(List<Map<String, Integer>> receiptList) {
        this.receiptList = receiptList;
    }

    /**
     * @return the cancelRcptOtp
     */
    public String getCancelRcptOtp() {
        return cancelRcptOtp;
    }

    /**
     * @param cancelRcptOtp the cancelRcptOtp to set
     */
    public void setCancelRcptOtp(String cancelRcptOtp) {
        this.cancelRcptOtp = cancelRcptOtp;
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
