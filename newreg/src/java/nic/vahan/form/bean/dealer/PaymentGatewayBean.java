/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.dealer;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.Util;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import nic.java.util.CommonUtils;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.CryptographyAES;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.dealer.PaymentGatewayDobj;
import nic.vahan.form.impl.Home_Impl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.dealer.PaymentGatewayImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

@ManagedBean(name = "payment_gateway_bean")
@ViewScoped
public class PaymentGatewayBean implements Serializable {

    private List<PaymentGatewayDobj> applicationNoAndAmountDetail;
    private long totalPayableAmount;
    private int actionCode;
    private boolean makePaymentButtonVisibility = false;
    private boolean verifyButtonVisibility = false;
    private boolean paymentGatewayPanelVisibility = false;
    private String paymentId;
    private List<PaymentGatewayDobj> cartList;
    private boolean removeRollBack = false;
    private List<PaymentGatewayDobj> payGatewayDetailList;
    private static final Logger LOGGER = Logger.getLogger(PaymentGatewayBean.class);
    private String applicationNumberList = "";
    private String rollBackApplList = "";
    private String purCdList = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SessionVariables sessionVariables = null;

    // constructor used to get the amount detail per each application number and action_code
    public PaymentGatewayBean() {
        applicationNoAndAmountDetail = new ArrayList<PaymentGatewayDobj>();
        cartList = new ArrayList<PaymentGatewayDobj>();
        payGatewayDetailList = new ArrayList<PaymentGatewayDobj>();
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getOffCodeSelected() == 0) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }

            String userCd = Util.getEmpCode();
            if (userCd != null) {
                cartList = ServerUtil.getCartList(Long.parseLong(userCd), sessionVariables.getOffCodeSelected(), sessionVariables.getStateCodeSelected());
            }
        } catch (VahanException ve) {
            JSFUtils.showMessage(ve.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void getPaymentGatewayList(PaymentGatewayDobj cartObj) {
        PaymentGatewayImpl payment_gateway_impl = new PaymentGatewayImpl();
        int newRegnFeeAndTempFee = 0;
        FacesMessage message = null;

        try {
            paymentGatewayPanelVisibility = true;
            paymentId = cartObj.getPaymentId();

            if (paymentId != null && !paymentId.equals("")) {
                purCdList = payment_gateway_impl.getPurCdList(paymentId);

                if (purCdList != null && (purCdList.contains("'" + String.valueOf(TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) + "'") || purCdList.contains("'" + String.valueOf(TableConstants.VM_TRANSACTION_MAST_TEMP_REG) + "'") || purCdList.contains("'" + String.valueOf(TableConstants.VM_TRANSACTION_MAST_FIT_CERT) + "'"))) {
                    newRegnFeeAndTempFee++;
                }

                if (newRegnFeeAndTempFee <= 0) {
                    throw new VahanException("Not valid for Payment");
                }

                SeatAllotedDetails selectedSeat = (SeatAllotedDetails) Util.getSelectedSeat();
                totalPayableAmount = 0L;
                rollBackApplList = "";
                applicationNumberList = "";
                applicationNoAndAmountDetail = payment_gateway_impl.getApplicationNoAndAmountDetail(paymentId, purCdList);
                if (!applicationNoAndAmountDetail.isEmpty()) {
                    this.commonData(applicationNoAndAmountDetail);
                    if (paymentId.equals("New Cart")) {
                        this.commonVisibility(rollBackApplList);
                    } else {
                        verifyButtonVisibility = true;
                        makePaymentButtonVisibility = false;
                        removeRollBack = false;
                    }

                    if (selectedSeat != null && !selectedSeat.equals("")) {
                        actionCode = selectedSeat.getAction_cd();
                    }

                    if (totalPayableAmount == 0) {
                        makePaymentButtonVisibility = false;
                    }
                } else {
                    throw new VahanException("Technical Problem. Please contact Administartor!");
                }
            } else {
                throw new VahanException("Details are not available for this transaction No!");
            }
        } catch (VahanException e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Something went wrong. Please contact system Administrator", "Something went wrong. Please contact system Administrator");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    // used to get the details of fine and tax save at the time of Add To Cart. 
    public void getDetail(PaymentGatewayDobj detailPaymentGateway) {
        PaymentGatewayImpl payment_gateway_impl = new PaymentGatewayImpl();
        try {
            payGatewayDetailList = payment_gateway_impl.getDetail(detailPaymentGateway.getApplNo());
            if (payGatewayDetailList.isEmpty()) {
                throw new VahanException("Details not found for " + detailPaymentGateway.getApplNo() + " Please Try Again!");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    // used to rollback to previous action code
    public void rollBackToPreviousActionCode(PaymentGatewayDobj removeFromPaymentGateway) throws VahanException {
        PaymentGatewayImpl payment_gateway_impl = new PaymentGatewayImpl();
        Status_dobj status_dobj = new Status_dobj();
        int prevAcCode = 0;
        Exception ex = null;

        try {
            String applNo = removeFromPaymentGateway.getApplNo();
            if (removeFromPaymentGateway.getDlrPurCd() != 0 && (removeFromPaymentGateway.getDlrPurCd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || removeFromPaymentGateway.getDlrPurCd() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE)) {
                prevAcCode = ServerUtil.getPreviousActionCode(actionCode, removeFromPaymentGateway.getDlrPurCd(), applNo, null);
                status_dobj.setPur_cd(removeFromPaymentGateway.getDlrPurCd());
            }
            status_dobj.setPrev_action_cd_selected(prevAcCode);
            status_dobj.setAppl_no(removeFromPaymentGateway.getApplNo());
            status_dobj.setStatus(TableConstants.STATUS_REVERT);
            status_dobj.setSeat_cd(TableConstants.STATUS_REVERT);
            payment_gateway_impl.rollbackToPreviousActionCode(status_dobj, applNo);
            purCdList = payment_gateway_impl.getPurCdList(paymentId);
            rollBackApplList = "";
            applicationNumberList = "";
            totalPayableAmount = 0L;
            applicationNoAndAmountDetail = payment_gateway_impl.getApplicationNoAndAmountDetail(paymentId, purCdList);
            if (!applicationNoAndAmountDetail.isEmpty()) {
                this.commonData(applicationNoAndAmountDetail);
                this.commonVisibility(rollBackApplList);
            } else {
                makePaymentButtonVisibility = false;
            }
        } catch (VahanException e) {
            ex = e;
        } catch (Exception e) {
            ex = e;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

        if (ex != null) {
            FacesMessage message = null;
            if (ex instanceof VahanException) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", ex.getMessage());
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", "Can't RollBack For " + removeFromPaymentGateway.getApplNo() + ",Please contact Administrator!");
            }
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void commonVisibility(String rollBackApplList) throws VahanException {
        try {
            if (!rollBackApplList.isEmpty()) {
                makePaymentButtonVisibility = false;
                verifyButtonVisibility = false;
                removeRollBack = true;
                rollBackApplList = rollBackApplList.substring(0, rollBackApplList.length() - 1);
                throw new VahanException("Applications ( " + rollBackApplList + ") have been added into Payment Cart One Day before, please first rollback the applications for recalculate the Fine/Penalty and then add again to CART for payment.");
            } else {
                verifyButtonVisibility = false;
                makePaymentButtonVisibility = true;
                removeRollBack = true;
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void commonData(List<PaymentGatewayDobj> applicationNoAndAmountDetail) throws ParseException, Exception {
        Date currentDate = dateFormat.parse(dateFormat.format(new Date()));
        for (PaymentGatewayDobj payObj : applicationNoAndAmountDetail) {
            totalPayableAmount = totalPayableAmount + payObj.getTtlAmount();
            applicationNumberList += "'" + payObj.getApplNo() + "',";
            java.util.Date opDate = new java.util.Date(payObj.getOpDate().getTime());
            if (paymentId.equals("New Cart")) {
                if (dateFormat.parse(dateFormat.format(opDate)).before(currentDate)) {
                    rollBackApplList += "'" + payObj.getApplNo() + "',";
                }
            }
        }
        applicationNumberList = applicationNumberList.substring(0, applicationNumberList.length() - 1);
    }

    public String makePayment() {
        PaymentGatewayImpl payment_gateway_impl = new PaymentGatewayImpl();
        String vahan_pgi_url = "";
        String ipPath = "";
        String return_path = "";
        CryptographyAES aes = new CryptographyAES();
        OwnerImpl ownerImpl = new OwnerImpl();
        Owner_dobj ownerDobj = null;
        int noOfApplsForDealerPayment = 0;
        try {
            if (totalPayableAmount > 0) {
                int cartCount = ServerUtil.getAddToCartStatusCount();
                TmConfigurationDobj tmConfDobj = Util.getTmConfiguration();
                if (tmConfDobj != null) {
                    noOfApplsForDealerPayment = tmConfDobj.getNoOfApplsForDealerPayment();
                } else {
                    throw new VahanException("Something went wrong!!!");
                }

                if (cartCount > noOfApplsForDealerPayment) {
                    throw new VahanException("Cart should contains only " + noOfApplsForDealerPayment + " Application, please Rollback excess added applications to initiate Payment...");
                }

                // Check Dealer block status.
                if (sessionVariables != null && sessionVariables.getEmpCodeLoggedIn() != null) {
                    Long user_cd = Long.parseLong(sessionVariables.getEmpCodeLoggedIn());
                    String dealerCd = ServerUtil.getDealerCodeByUserCd(user_cd, sessionVariables.getStateCodeSelected());
                    if (!CommonUtils.isNullOrBlank(dealerCd)) {
                        String dealerBlockingReason = Home_Impl.getDealerBlockUnBlockStatus(dealerCd);
                        if (!CommonUtils.isNullOrBlank(dealerBlockingReason)) {
                            throw new VahanException("Your user-id has been blocked by the respective registering authority due to " + dealerBlockingReason + ". However, You can not make fresh payment.");
                        }
                    }
                    String dealerTradeValidity = ServerUtil.getDealerTradeCertificateDetails(dealerCd, null, sessionVariables.getStateCodeSelected(), tmConfDobj);
                    if (!CommonUtils.isNullOrBlank(dealerTradeValidity)) {
                        throw new VahanException(dealerTradeValidity);
                    }
                }

                for (PaymentGatewayDobj payObj : applicationNoAndAmountDetail) {
                    ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj(null, payObj.getApplNo().toUpperCase(), "", payObj.getDlrPurCd());
                    VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
                    vehParameters.setAPPL_DATE(payObj.getAppl_dt());
                    vehParameters.setPUR_CD(payObj.getDlrPurCd());
                    vehParameters.setACTION_CD(actionCode);
                    String isCriteriaMatchMsg = ServerUtil.isNewRegnNotAllowed(vehParameters);
                    if (isCriteriaMatchMsg != null && !isCriteriaMatchMsg.isEmpty()) {
                        throw new VahanException(isCriteriaMatchMsg + " for Application No " + payObj.getApplNo() + " Please Rollback to make Payment");
                    }
                    boolean isValidForRegn = ServerUtil.validateVehicleNorms(ownerDobj, payObj.getDlrPurCd(), vehParameters, tmConfDobj.getTmConfigDealerDobj());
                    if (!isValidForRegn) {
                        throw new VahanException("State Transport Department has not authorized you to deposit Cart Payment for Application No " + payObj.getApplNo() + " for Registration of '" + MasterTableFiller.masterTables.VM_VH_CLASS.getDesc(ownerDobj.getVh_class() + "") + "' with emission norms " + MasterTableFiller.masterTables.VM_NORMS.getDesc(ownerDobj.getNorms() + "") + ", please contact respective Registering Authority regarding this.");
                    }
                }

                vahan_pgi_url = ServerUtil.getVahanPgiUrl(TableConstants.VAHAN_PGI_CONNTYPE);
                String trans_no = payment_gateway_impl.getTransactionNumber(Util.getUserStateCode(), applicationNumberList);
                ipPath = ServerUtil.getIpPath();
                String return_dealer_url = null;
                try {
                    if (trans_no.equals("")) {
                        throw new VahanException("PaymentId not generated due to technical error.");
                    } else {
                        return_dealer_url = ipPath + "/vahan/ui/dealer/form_printReceipt.xhtml";
                        String data = "payment_id=" + trans_no + "|" + "state_cd=" + Util.getUserStateCode() + "|" + "user_cd=" + Util.getEmpCode() + "|" + "return_url=" + return_dealer_url + "|" + "nic_merchant=" + TableConstants.VAHAN_PGI_MERCHANT_CODE + "|" + "total_payable_amount=" + totalPayableAmount + "|" + "off_cd=" + Util.getSelectedSeat().getOff_cd();
                        System.out.println(DateUtils.getDateInDDMMYYYY_HHMMSS(new java.util.Date()) + " FirstPayment: paymentId:" + trans_no + ", ApplNos:" + applicationNumberList + ", Amt:" + totalPayableAmount);
                        data = aes.getEncriptedString(data);
                        String ip = vahan_pgi_url + data;
                        setSessionValue(trans_no);
                        HttpServletResponse hres = ((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse());
                        hres.sendRedirect(ip);
                    }
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    FacesMessage message = null;
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
            }

        } catch (VahanException vmex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vmex.getMessage(), vmex.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return return_path;
    }

    public String doubleVerification() {
        CryptographyAES aes = new CryptographyAES();
        PaymentGatewayImpl payment_gateway_impl = new PaymentGatewayImpl();
        String return_path = "";
        String ipPath = "";
        String vahan_pgi_url = "";
        try {
            if (totalPayableAmount > 0) {
                vahan_pgi_url = ServerUtil.getVahanPgiUrl(TableConstants.VAHAN_PGI_CHECK_FAIL_CONNTYPE);
                ipPath = ServerUtil.getIpPath();
                verifyButtonVisibility = true;
                String return_url = null;
                return_url = ipPath + "/vahan/ui/dealer/form_printReceipt.xhtml";
                String data = "payment_id=" + paymentId + "|" + "state_cd=" + Util.getUserStateCode() + "|" + "total_payable_amount=" + totalPayableAmount + "|" + "return_url=" + return_url + "|" + "nic_merchant=" + TableConstants.VAHAN_PGI_MERCHANT_CODE + "|" + "off_cd=" + Util.getSelectedSeat().getOff_cd();
                System.out.println(DateUtils.getDateInDDMMYYYY_HHMMSS(new java.util.Date()) + " D-Verify: paymentId:" + paymentId + ", ApplNos:" + applicationNumberList + ", Amt:" + totalPayableAmount);
                data = aes.getEncriptedString(data);
                String ip = vahan_pgi_url + data;
                setSessionValue(paymentId);
                HttpServletResponse hres = ((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse());
                hres.sendRedirect(ip);

            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return return_path;
    }

    private void setSessionValue(String trans_no) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.setAttribute("GrandTotal", totalPayableAmount);
        session.setAttribute("TransactionId", trans_no);
        session.setAttribute("DealerPayment", true);
        session.setAttribute("PaymentVerificationType", TableConstants.DEALER_PAYMENT_VERIFICATION);
    }

    /**
     * @return the applicationNoAndAmountDetail
     */
    public List<PaymentGatewayDobj> getApplicationNoAndAmountDetail() {
        return applicationNoAndAmountDetail;
    }

    /**
     * @param applicationNoAndAmountDetail the applicationNoAndAmountDetail to
     * set
     */
    public void setApplicationNoAndAmountDetail(List<PaymentGatewayDobj> applicationNoAndAmountDetail) {
        this.applicationNoAndAmountDetail = applicationNoAndAmountDetail;
    }

    /**
     * @return the totalPayableAmount
     */
    public long getTotalPayableAmount() {
        return totalPayableAmount;
    }

    /**
     * @param totalPayableAmount the totalPayableAmount to set
     */
    public void setTotalPayableAmount(long totalPayableAmount) {
        this.totalPayableAmount = totalPayableAmount;
    }

    /**
     * @return the makePaymentButtonVisibility
     */
    public boolean isMakePaymentButtonVisibility() {
        return makePaymentButtonVisibility;
    }

    /**
     * @param makePaymentButtonVisibility the makePaymentButtonVisibility to set
     */
    public void setMakePaymentButtonVisibility(boolean makePaymentButtonVisibility) {
        this.makePaymentButtonVisibility = makePaymentButtonVisibility;
    }

    /**
     * @return the verifyButtonVisibility
     */
    public boolean isVerifyButtonVisibility() {
        return verifyButtonVisibility;
    }

    /**
     * @param verifyButtonVisibility the verifyButtonVisibility to set
     */
    public void setVerifyButtonVisibility(boolean verifyButtonVisibility) {
        this.verifyButtonVisibility = verifyButtonVisibility;
    }

    /**
     * @return the paymentId
     */
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * @param paymentId the paymentId to set
     */
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    /**
     * @return the cartList
     */
    public List<PaymentGatewayDobj> getCartList() {
        return cartList;
    }

    /**
     * @param cartList the cartList to set
     */
    public void setCartList(List<PaymentGatewayDobj> cartList) {
        this.cartList = cartList;
    }

    /**
     * @return the paymentGatewayPanelVisibility
     */
    public boolean isPaymentGatewayPanelVisibility() {
        return paymentGatewayPanelVisibility;
    }

    /**
     * @param paymentGatewayPanelVisibility the paymentGatewayPanelVisibility to
     * set
     */
    public void setPaymentGatewayPanelVisibility(boolean paymentGatewayPanelVisibility) {
        this.paymentGatewayPanelVisibility = paymentGatewayPanelVisibility;
    }

    /**
     * @return the disableRollBack
     */
    public boolean isRemoveRollBack() {
        return removeRollBack;
    }

    /**
     * @param disableRollBack the disableRollBack to set
     */
    public void setRemoveRollBack(boolean removeRollBack) {
        this.removeRollBack = removeRollBack;
    }

    /**
     * @return the payGatewayDetailList
     */
    public List<PaymentGatewayDobj> getPayGatewayDetailList() {
        return payGatewayDetailList;
    }

    /**
     * @param payGatewayDetailList the payGatewayDetailList to set
     */
    public void setPayGatewayDetailList(List<PaymentGatewayDobj> payGatewayDetailList) {
        this.payGatewayDetailList = payGatewayDetailList;
    }

    /**
     * @return the applicationNumberList
     */
    public String getApplicationNumberList() {
        return applicationNumberList;
    }

    /**
     * @param applicationNumberList the applicationNumberList to set
     */
    public void setApplicationNumberList(String applicationNumberList) {
        this.applicationNumberList = applicationNumberList;
    }

    /**
     * @return the purCdList
     */
    public String getPurCdList() {
        return purCdList;
    }

    /**
     * @param purCdList the purCdList to set
     */
    public void setPurCdList(String purCdList) {
        this.purCdList = purCdList;
    }

    /**
     * @return the rollBackApplList
     */
    public String getRollBackApplList() {
        return rollBackApplList;
    }

    /**
     * @param rollBackApplList the rollBackApplList to set
     */
    public void setRollBackApplList(String rollBackApplList) {
        this.rollBackApplList = rollBackApplList;
    }
}
