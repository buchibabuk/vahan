/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.controller;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nic.java.util.CommonUtils;
import nic.rto.vahan.common.CryptographyAES;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.dealer.PaymentGatewayDobj;
import nic.vahan.form.impl.Home_Impl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.server.ServerUtil;
import nic.vahan5.reg.commonutils.FormulaUtilities;
import nic.vahan5.reg.form.impl.dealer.PaymentGatewayImplementation;
import nic.vahan5.reg.form.impl.Utility;
import nic.vahan5.reg.rest.model.dealer.PaymentGatewayModel;
import nic.vahan5.reg.rest.model.SessionVariablesModel;
import nic.vahan5.reg.server.ServerUtility;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Ramesh
 */
@RestController
@RequestMapping("/paymentGateway")
public class PaymentGateWayController {

//    private List<PaymentGatewayDobj> payGatewayDetailList;
    private String paymentId;
    private boolean makePaymentButtonVisibility = false;
    private boolean verifyButtonVisibility = false;
    private boolean removeRollBack = false;
    private long totalPayableAmount;
    private String applicationNumberList = "";
    private String rollBackApplList = "";
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @PostMapping("/list")
    public PaymentGatewayModel getPaymentGatewayList(@RequestBody PaymentGatewayModel paymentGatewayModel) throws VahanException {

        PaymentGatewayDobj cartObj = null;
        SessionVariablesModel sessionVariablesModel = null;
        List<PaymentGatewayDobj> applicationNoAndAmountDetail = null;
        int actionCode = 0;


        int newRegnFeeAndTempFee = 0;
        String purCdList = null;
        String emp_cd = null;
        int off_cd = 0;
        String state_cd = null;

        PaymentGatewayImplementation payment_gateway_impl = new PaymentGatewayImplementation();

        cartObj = paymentGatewayModel.getPaymentGatewayDobj();
        paymentId = cartObj.getPaymentId();
        sessionVariablesModel = paymentGatewayModel.getSessionVariables();
        actionCode = sessionVariablesModel.getActionCodeSelected();

        emp_cd = sessionVariablesModel.getEmpCodeLoggedIn();
        off_cd = sessionVariablesModel.getOffCodeSelected();
        state_cd = sessionVariablesModel.getStateCodeSelected();


        if (paymentId != null && !paymentId.equals("")) {
            purCdList = payment_gateway_impl.getPurCdList(paymentId, emp_cd);

            if (purCdList != null && (purCdList.contains("'" + String.valueOf(TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) + "'") || purCdList.contains("'" + String.valueOf(TableConstants.VM_TRANSACTION_MAST_TEMP_REG) + "'") || purCdList.contains("'" + String.valueOf(TableConstants.VM_TRANSACTION_MAST_FIT_CERT) + "'"))) {
                newRegnFeeAndTempFee++;
            }

            if (newRegnFeeAndTempFee <= 0) {
                throw new VahanException("Not valid for Payment");
            }
            totalPayableAmount = 0L;
            rollBackApplList = "";
            applicationNumberList = "";

            applicationNoAndAmountDetail = payment_gateway_impl.getApplicationNoAndAmountDetail(paymentId, purCdList, emp_cd, off_cd);

            if (!applicationNoAndAmountDetail.isEmpty()) {
                try {
                    this.commonData(applicationNoAndAmountDetail);
                } catch (ParseException ex) {
                    Logger.getLogger(PaymentGateWayController.class.getName()).log(Level.SEVERE, null, ex);
                    throw new VahanException(TableConstants.SomthingWentWrong);
                } catch (Exception ex) {
                    Logger.getLogger(PaymentGateWayController.class.getName()).log(Level.SEVERE, null, ex);
                    throw new VahanException(TableConstants.SomthingWentWrong);
                }
                if (paymentId.equals("New Cart")) {
                    this.commonVisibility(rollBackApplList);
                } else {
                    verifyButtonVisibility = true;
                    makePaymentButtonVisibility = false;
                    removeRollBack = false;
                }

                if (totalPayableAmount == 0) {
                    makePaymentButtonVisibility = false;
                }
            } else {
                throw new VahanException("Technical Problem. Please contact Administartor!");
            }

            paymentGatewayModel.setPurCdList(purCdList);
            paymentGatewayModel.setApplicationNoAndAmountDetail(applicationNoAndAmountDetail);

            paymentGatewayModel.setTotalPayableAmount(totalPayableAmount);
            paymentGatewayModel.setVerifyButtonVisibility(verifyButtonVisibility);
            paymentGatewayModel.setMakePaymentButtonVisibility(makePaymentButtonVisibility);
            paymentGatewayModel.setRemoveRollBack(removeRollBack);
            paymentGatewayModel.setRollBackApplList(rollBackApplList);
            paymentGatewayModel.setApplicationNumberList(applicationNumberList);
            paymentGatewayModel.setActionCode(actionCode);
            paymentGatewayModel.setPaymentId(paymentId);
        } else {
            throw new VahanException("Details are not available for this transaction No!");
        }
        return paymentGatewayModel;
    }

    @GetMapping("/detail")
    public PaymentGatewayModel getPaymntGateWayDetail(@RequestParam String applNo) throws VahanException {
        List<PaymentGatewayDobj> payGatewayDetailList = null;
        PaymentGatewayModel model = new PaymentGatewayModel();
        payGatewayDetailList = new PaymentGatewayImplementation().getDetail(applNo);
        model.setPayGatewayDetailList(payGatewayDetailList);

        return model;
    }

    @PostMapping("/rollBackToPreviousActionCode")
    public PaymentGatewayModel rollBackToPreviousActionCode(@RequestBody PaymentGatewayModel paymentGatewayModel, @RequestParam String clientIpAddress, @RequestParam String selectedRoleCode) throws VahanException {
        PaymentGatewayDobj removeFromPaymentGateway = null;
        SessionVariablesModel sessionVariablesModel = null;
        List<PaymentGatewayDobj> applicationNoAndAmountDetail = null;
        int actionCode = 0;

        String emp_cd = null;
        int off_cd = 0;
        String state_cd = null;
        String userCategory = null;
        String purCdList = null;
        removeFromPaymentGateway = paymentGatewayModel.getPaymentGatewayDobj();
        sessionVariablesModel = paymentGatewayModel.getSessionVariables();
        paymentId = removeFromPaymentGateway.getPaymentId();

        actionCode = sessionVariablesModel.getActionCodeSelected();
        emp_cd = sessionVariablesModel.getEmpCodeLoggedIn();
        off_cd = sessionVariablesModel.getOffCodeSelected();
        state_cd = sessionVariablesModel.getStateCodeSelected();
        userCategory = sessionVariablesModel.getUserCatgForLoggedInUser();

        PaymentGatewayImplementation payment_gateway_impl = new PaymentGatewayImplementation();
        Status_dobj status_dobj = new Status_dobj();
        int prevAcCode = 0;
        String applNo = removeFromPaymentGateway.getApplNo();
        if (removeFromPaymentGateway.getDlrPurCd() != 0 && (removeFromPaymentGateway.getDlrPurCd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || removeFromPaymentGateway.getDlrPurCd() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE)) {
            prevAcCode = ServerUtility.getPreviousActionCode(actionCode, removeFromPaymentGateway.getDlrPurCd(), applNo, null, state_cd);
            status_dobj.setPur_cd(removeFromPaymentGateway.getDlrPurCd());
        }
        status_dobj.setPrev_action_cd_selected(prevAcCode);
        status_dobj.setAppl_no(removeFromPaymentGateway.getApplNo());
        status_dobj.setStatus(TableConstants.STATUS_REVERT);
        status_dobj.setSeat_cd(TableConstants.STATUS_REVERT);
        TmConfigurationDobj tmConfigDobj = Utility.getTmConfiguration(null, state_cd);

        payment_gateway_impl.rollbackToPreviousActionCode(status_dobj, applNo, emp_cd, off_cd, state_cd, tmConfigDobj, clientIpAddress, selectedRoleCode, actionCode, userCategory);

        purCdList = payment_gateway_impl.getPurCdList(paymentId, emp_cd);
        paymentGatewayModel.setPurCdList(purCdList);
        rollBackApplList = "";
        applicationNumberList = "";
        totalPayableAmount = 0L;

        applicationNoAndAmountDetail = payment_gateway_impl.getApplicationNoAndAmountDetail(paymentId, purCdList, emp_cd, off_cd);

        if (!applicationNoAndAmountDetail.isEmpty()) {
            try {
                this.commonData(applicationNoAndAmountDetail);
            } catch (ParseException ex) {
                Logger.getLogger(PaymentGateWayController.class.getName()).log(Level.SEVERE, null, ex);
                throw new VahanException(TableConstants.SomthingWentWrong);
            } catch (Exception ex) {
                Logger.getLogger(PaymentGateWayController.class.getName()).log(Level.SEVERE, null, ex);
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
            this.commonVisibility(rollBackApplList);
        } else {
            makePaymentButtonVisibility = false;
        }
        paymentGatewayModel.setPurCdList(purCdList);
        paymentGatewayModel.setApplicationNoAndAmountDetail(applicationNoAndAmountDetail);


        paymentGatewayModel.setTotalPayableAmount(totalPayableAmount);
        paymentGatewayModel.setVerifyButtonVisibility(verifyButtonVisibility);
        paymentGatewayModel.setMakePaymentButtonVisibility(makePaymentButtonVisibility);
        paymentGatewayModel.setRemoveRollBack(removeRollBack);
        paymentGatewayModel.setRollBackApplList(rollBackApplList);
        paymentGatewayModel.setApplicationNumberList(applicationNumberList);


        return paymentGatewayModel;
    }

    @PostMapping("/makePayment")
    public PaymentGatewayModel makePayment(@RequestBody PaymentGatewayModel paymentGatewayModel) throws VahanException, Exception {
        PaymentGatewayImplementation payment_gateway_impl = new PaymentGatewayImplementation();

        String vahan_pgi_url = "";
        OwnerImpl ownerImpl = new OwnerImpl();
        Owner_dobj ownerDobj = null;
        int noOfApplsForDealerPayment = 0;
        String emp_cd = null;
        int off_cd = 0;
        String state_cd = null;

        List<PaymentGatewayDobj> applicationNoAndAmountDetail;
        SessionVariablesModel sessionVariablesModel = null;

        applicationNoAndAmountDetail = paymentGatewayModel.getApplicationNoAndAmountDetail();
        sessionVariablesModel = paymentGatewayModel.getSessionVariables();
        applicationNumberList = paymentGatewayModel.getApplicationNumberList();

        emp_cd = sessionVariablesModel.getEmpCodeLoggedIn();
        off_cd = sessionVariablesModel.getOffCodeSelected();
        state_cd = sessionVariablesModel.getStateCodeSelected();

        int cartCount = ServerUtility.getAddToCartStatusCount(emp_cd, off_cd);
        TmConfigurationDobj tmConfDobj = Utility.getTmConfiguration(null, state_cd);
        if (tmConfDobj != null) {
            noOfApplsForDealerPayment = tmConfDobj.getNoOfApplsForDealerPayment();
        } else {
            throw new VahanException("Something went wrong!!!");
        }

        if (cartCount > noOfApplsForDealerPayment) {
            throw new VahanException("Cart should contains only " + noOfApplsForDealerPayment + " Application, please Rollback excess added applications to initiate Payment...");
        }
        // Check Dealer block status.
        if (sessionVariablesModel != null && sessionVariablesModel.getEmpCodeLoggedIn() != null) {
            String dealerCd = ServerUtility.getDealerCodeByUserCd(Long.parseLong(emp_cd), state_cd);
            if (!CommonUtils.isNullOrBlank(dealerCd)) {
                String dealerBlockingReason = Home_Impl.getDealerBlockUnBlockStatus(dealerCd);
                if (!CommonUtils.isNullOrBlank(dealerBlockingReason)) {
                    throw new VahanException("Your user-id has been blocked by the respective registering authority due to " + dealerBlockingReason + ". However, You can not make fresh payment.");
                }
            }
        }

        for (PaymentGatewayDobj payObj : applicationNoAndAmountDetail) {
            try {
                ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj(null, payObj.getApplNo().toUpperCase(), "", payObj.getDlrPurCd());
            } catch (Exception ex) {
                Logger.getLogger(PaymentGateWayController.class.getName()).log(Level.SEVERE, null, ex);
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
            VehicleParameters vehParameters = FormulaUtilities.fillVehicleParametersFromDobj(ownerDobj, sessionVariablesModel);
            vehParameters.setAPPL_DATE(payObj.getAppl_dt());
            vehParameters.setPUR_CD(payObj.getDlrPurCd());
            vehParameters.setACTION_CD(sessionVariablesModel.getActionCodeSelected());
            String isCriteriaMatchMsg = ServerUtility.isNewRegnNotAllowed(vehParameters);
            if (isCriteriaMatchMsg != null && !isCriteriaMatchMsg.isEmpty()) {
                throw new VahanException(isCriteriaMatchMsg + " for Application No " + payObj.getApplNo() + " Please Rollback to make Payment");
            }
            boolean isValidForRegn = ServerUtil.validateVehicleNorms(ownerDobj, payObj.getDlrPurCd(), vehParameters, tmConfDobj.getTmConfigDealerDobj());
            if (!isValidForRegn) {
                throw new VahanException("State Transport Department has not authorized you to deposit Cart Payment for Application No " + payObj.getApplNo() + " for Registration of '" + MasterTableFiller.masterTables.VM_VH_CLASS.getDesc(ownerDobj.getVh_class() + "") + "' with emission norms " + MasterTableFiller.masterTables.VM_NORMS.getDesc(ownerDobj.getNorms() + "") + ", please contact respective Registering Authority regarding this.");
            }
        }
        vahan_pgi_url = ServerUtility.getVahanPgiUrl(TableConstants.VAHAN_PGI_CONNTYPE);
        String trans_no = payment_gateway_impl.getTransactionNumber(state_cd, applicationNumberList, off_cd, emp_cd);

        paymentGatewayModel.setVahan_pgi_url(vahan_pgi_url);
        paymentGatewayModel.setTrans_no(trans_no);

        return paymentGatewayModel;
    }

    @GetMapping("/doubleVerification")
    public String doubleVerification() throws VahanException {
        String vahan_pgi_url;

        vahan_pgi_url = ServerUtility.getVahanPgiUrl(TableConstants.VAHAN_PGI_CHECK_FAIL_CONNTYPE);

        return vahan_pgi_url;
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
//            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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
}
