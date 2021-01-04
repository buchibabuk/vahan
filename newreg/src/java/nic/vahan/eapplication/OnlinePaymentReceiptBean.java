/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.eapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.CryptographyAES;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.TaxFieldLabelBean;
import nic.vahan.form.dobj.OnlinePayDobj;
import nic.vahan.form.impl.OnlinePayImpl;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

@ManagedBean(name = "onlinePayRcpBean")
@ViewScoped
public class OnlinePaymentReceiptBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(OnlinePaymentReceiptBean.class);
    private boolean sucessTab;
    private boolean pendingTab;
    private boolean errorTab;
    private String mainHeading = "";
    private String subHeading = "";
    private String transactionNo;
    private String bankTransactionId = "";
    private String transactionDate = "";
    private String transactionRegnNo = "";
    private String regnNo = "";
    private String chassisNo;
    private int transactionAmount;
    private String amountInWords;
    private String bankReferenceNo;
    private String status = "";
    private String failureMessage;
    private boolean failureTab;
    private String stateName;
    private boolean flagInclude = false;
    private boolean flagIncludefail = false;
    private boolean flagForTC = false;
    private String rcptNo;
    private TaxFieldLabelBean taxFieldLabel = new TaxFieldLabelBean();
    private String leftMenuURL = "/leftMenuPanel.xhtml";

    @PostConstruct
    public void init() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        CryptographyAES aes = new CryptographyAES();
        String response = null;
        HashMap returnParams = null;
        Double GrandTotal = 0.0;
        Double sentGrandTotal = 0.0;
        String sentTransactionId = "";
        Exception ex = null;
        String reason = null;
        String userId = null;
        int actionCode = 0;
        String stateCd = null;
        int offCd = 0;
        boolean flag = true;
        String userCatg = null;
        String selectedBalancePayMode = null;
        String paymentType = null;
        int purCd = 0;
        try {
            HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            response = req.getParameter("response");
            returnParams = aes.getReturnParameters(response);
            status = (String) returnParams.get("status");
            transactionNo = (String) returnParams.get("paymentId");
            reason = (String) returnParams.get("status_desc");
            bankTransactionId = (String) returnParams.get("transId");
            mainHeading = ServerUtil.getRcptHeading();
            subHeading = ServerUtil.getRcptSubHeading();

            System.out.println(DateUtils.getDateInDDMMYYYY_HHMMSS(new java.util.Date()) + "For Online Payment RTO" + " Received Data: " + " Status:" + status + ", PaymentId:" + transactionNo + ", Reason:" + reason + ", Grandtotal:" + returnParams.get("total_amount"));
            if (returnParams.get("total_amount") != null) {
                try {
                    GrandTotal = Double.parseDouble(returnParams.get("total_amount").toString());
                } catch (Exception e) {
                    GrandTotal = 0.0;
                }
            }

            PaymentDobj payDobj = null;
            if (session != null) {
                payDobj = (PaymentDobj) session.getAttribute("paySessionDobj");
                if (payDobj != null) {
                    try {
                        sentGrandTotal = Double.parseDouble(String.valueOf(payDobj.getTotalPayableAmount()));
                    } catch (Exception e) {
                        sentGrandTotal = 0.0;
                    }
                    sentTransactionId = payDobj.getTransNo();
                    userId = payDobj.getUserId();
                    transactionRegnNo = payDobj.getRegnNo();
                    userCatg = payDobj.getUserCatg();
                    selectedBalancePayMode = payDobj.getBalanceFeePaySelectedMode();
                    paymentType = payDobj.getPaymentType();
                    purCd = payDobj.getPurCd();

                    if ("TradeRegn".equals(payDobj.getRegnNo())) {
                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", transactionNo);
                        setFlagForTC(true);
                    }
                }

            } else {
                return;
            }
            SeatAllotedDetails selectedSeat = (SeatAllotedDetails) Util.getSelectedSeat();
            if (selectedSeat != null) {
                actionCode = selectedSeat.getAction_cd();
                offCd = selectedSeat.getOff_cd();
            }

            if (Util.getUserStateCode() != null) {
                stateCd = Util.getUserStateCode();
            }

            if (session.getAttribute("onlinePaymentVerifyType") != null) {
                String paymentVerifyType = session.getAttribute("onlinePaymentVerifyType").toString();
                if (TableConstants.DEALER_PAYMENT_REVERIFICATION.equals(paymentVerifyType)) {
                    switch (status) {
                        case TableConstants.VAHAN_PGI_SUCCESS:
                            OnlinePayImpl.updateReVerifiedTransactionNo(transactionNo, stateCd, userId);
                            break;
                        case TableConstants.VAHAN_PGI_FAIL:
                            throw new VahanException("Transaction(" + transactionNo + ") is failed with reason :" + reason);
                    }
                }
            } else {
                return;
            }

            if (!sentTransactionId.equals(transactionNo) || (status != null && status.equals(TableConstants.VAHAN_PGI_SUCCESS) && !sentGrandTotal.equals(GrandTotal))) {
                String strMessage = "SendData: TransId:" + sentTransactionId + " , Amt:"
                        + sentGrandTotal + " RcvdData: TransId:" + transactionNo + " , Amt:" + GrandTotal;

                System.out.println(strMessage + " Payment not done due to transactionId/amount mismatch.");
                throw new VahanException("Payment not done due to transactionId/amount mismatch.");
            }

            if (status != null && status.equals(TableConstants.VAHAN_PGI_SUCCESS)) {
                saveSuccessDetails(userId, actionCode, stateCd, offCd, reason, userCatg, selectedBalancePayMode, paymentType, purCd);
                setFlagInclude(true);
                setFlagIncludefail(false);
                printCurrentReceipt(rcptNo);
                setLeftMenuURL("");
                flag = false;
            } else if (status != null && status.equals(TableConstants.VAHAN_PGI_FAIL)) {
                failureCase(userId, reason, userCatg);
                setFlagInclude(false);
                setFlagIncludefail(true);
                flag = true;
            } else if (status != null && status.equals(TableConstants.VAHAN_PGI_PENDING)) {
                pendingCase(reason);
                setFlagInclude(false);
                setFlagIncludefail(true);
                flag = true;
            } else {
                flag = true;
                throw new VahanException("Unable to proccess your request.Please Try Again.");
            }
        } catch (VahanException e) {
            failureMessage = e.getMessage();
            ex = e;
        } catch (Exception e) {
            failureMessage = TableConstants.SomthingWentWrong;
            ex = e;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (flag) {
                    if (session != null) {
                        session.removeAttribute("paySessionDobj");
                        session.removeAttribute("SelectedSeat");
                        session.removeAttribute("state_cd");
                        session.removeAttribute("off_cd");
                        session.removeAttribute("emp_cd");
                        session.removeAttribute("regn_no");
                        session.removeAttribute("DealerPayment");
                        session.removeAttribute("onlinePayment");
                        session.removeAttribute("user_catg");
                        session.removeAttribute("onlinePaymentVerifyType");
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        if (ex != null) {
            errorTab = true;
        }
    }

    public String printCurrentReceipt(String rcpt_no) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", rcpt_no);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "";
    }

    public void saveSuccessDetails(String userId, int actionCode, String stateCd, int offCd, String reason, String userCatg, String selectedBalancePayMode, String paymentType, int purCd) throws VahanException {
        OnlinePayImpl owImpl = new OnlinePayImpl();
        Utility utility = new Utility();
        List registrationNoList = new ArrayList<>();
        List chassisNoList = new ArrayList<>();
        List<OnlinePayDobj> payDobjList = null;
        try {
            if (!isFlagForTC() && !(TableConstants.TAX_MODE_BALANCE.equals(paymentType) || TableConstants.TAX_INSTALLMENT.equals(paymentType)) && (CommonUtils.isNullOrBlank(transactionRegnNo) || transactionRegnNo.equalsIgnoreCase("NEW") || transactionRegnNo.equalsIgnoreCase("TEMPREG") || purCd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) && purCd != TableConstants.VM_PMT_FRESH_PUR_CD) {
                payDobjList = owImpl.getRecptNoRegnNoAndInsertDataForNewVehicles(userId, transactionNo, actionCode, stateCd, offCd, userCatg);
            } else if (!isFlagForTC() && !(TableConstants.TAX_MODE_BALANCE.equals(paymentType) || TableConstants.ONLINE_TAX.equals(paymentType) || TableConstants.TAX_INSTALLMENT.equals(paymentType) || TableConstants.ONLINE_FANCY.equals(paymentType) || TableConstants.ONLINE_AUDIT.equals(paymentType)) || (purCd == TableConstants.VM_PMT_FRESH_PUR_CD && transactionRegnNo.equalsIgnoreCase("NEW"))) {
                payDobjList = owImpl.getRecptNoRegnNoAndinsertDataForRegisteredVehicles(userId, transactionNo, actionCode, stateCd, offCd, userCatg, purCd, transactionRegnNo);
            } else if (!isFlagForTC() && !CommonUtils.isNullOrBlank(paymentType) && (paymentType.equals(TableConstants.TAX_MODE_BALANCE) || paymentType.equals(TableConstants.ONLINE_TAX) || TableConstants.TAX_INSTALLMENT.equals(paymentType) || TableConstants.ONLINE_FANCY.equals(paymentType) || TableConstants.ONLINE_AUDIT.equals(paymentType))) {
                payDobjList = owImpl.getRecptNoRegnNoAndinsertDataForOnlineFeeTax(selectedBalancePayMode, userId, transactionNo, actionCode, stateCd, offCd, userCatg, paymentType);
            } else if (isFlagForTC()) {
                payDobjList = owImpl.getRecptNoRegnNoAndInsertDataForTradeCertificate(userId, transactionNo, actionCode, stateCd, offCd, userCatg);
            }
            if (payDobjList != null && !payDobjList.isEmpty()) {
                transactionDate = payDobjList.get(0).getTransactionDate().toString();
                if (bankTransactionId == null || bankTransactionId.isEmpty()) {
                    bankTransactionId = payDobjList.get(0).getTransactionNo().toString();
                }
                stateName = payDobjList.get(0).getStateName().toString();
                bankReferenceNo = payDobjList.get(0).getBankReferenceNo().toString();
                if (payDobjList.size() > 0) {
                    for (OnlinePayDobj payObj : payDobjList) {
                        transactionAmount = transactionAmount + payObj.getTransactionAmount();
                        registrationNoList.add(payObj.getRegnNo());
                        chassisNoList.add(payObj.getChassisNo());
                    }
                }
                chassisNo = chassisNoList.toString().substring(1, chassisNoList.toString().length() - 1);
                regnNo = registrationNoList.toString().substring(1, registrationNoList.toString().length() - 1);
                amountInWords = utility.ConvertNumberToWords(transactionAmount);
                rcptNo = payDobjList.get(0).getReceiptNo();
                status = reason;
                sucessTab = true;
            } else {
                throw new VahanException("Unable to proccess your request.Please Try Again");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception ex) {
            LOGGER.error("TransactionNo: " + transactionNo + ": " + ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
    }

    public void pendingCase(String reason) throws VahanException {
        OnlinePayImpl owImpl = new OnlinePayImpl();
        List<OnlinePayDobj> payDobjList = owImpl.getPendingCaseDetails(transactionNo);
        status = "Please Wait for some time as " + reason;
        pendingTab = true;
        getDetails(payDobjList);
    }

    public void failureCase(String userId, String reason, String userCatg) throws VahanException {
        OnlinePayImpl owImpl = new OnlinePayImpl();
        List<OnlinePayDobj> payDobjList = owImpl.saveDataAfterFailurePayment(userId, transactionNo, reason, userCatg);
        failureTab = true;
        status = "Transaction for Transaction Id - " + transactionNo + " is Failed. Due to " + reason;
        getDetails(payDobjList);
    }

    public void getDetails(List<OnlinePayDobj> payDobjList) {
        List chassisNoList = new ArrayList<>();
        Utility utility = new Utility();
        if (payDobjList != null && !payDobjList.isEmpty()) {
            if (payDobjList.size() > 0) {
                for (OnlinePayDobj payObj : payDobjList) {
                    transactionAmount = transactionAmount + payObj.getTransactionAmount();
                    chassisNoList.add(payObj.getChassisNo());
                }
            }
            chassisNo = chassisNoList.toString().substring(1, chassisNoList.toString().length() - 1);
            amountInWords = utility.ConvertNumberToWords(transactionAmount);
            stateName = payDobjList.get(0).getStateName().toString();
            bankReferenceNo = payDobjList.get(0).getBankReferenceNo().toString();
        }
    }

    /**
     * @return the sucessTab
     */
    public boolean isSucessTab() {
        return sucessTab;
    }

    /**
     * @param sucessTab the sucessTab to set
     */
    public void setSucessTab(boolean sucessTab) {
        this.sucessTab = sucessTab;
    }

    /**
     * @return the pendingTab
     */
    public boolean isPendingTab() {
        return pendingTab;
    }

    /**
     * @param pendingTab the pendingTab to set
     */
    public void setPendingTab(boolean pendingTab) {
        this.pendingTab = pendingTab;
    }

    /**
     * @return the errorTab
     */
    public boolean isErrorTab() {
        return errorTab;
    }

    /**
     * @param errorTab the errorTab to set
     */
    public void setErrorTab(boolean errorTab) {
        this.errorTab = errorTab;
    }

    /**
     * @return the mainHeading
     */
    public String getMainHeading() {
        return mainHeading;
    }

    /**
     * @param mainHeading the mainHeading to set
     */
    public void setMainHeading(String mainHeading) {
        this.mainHeading = mainHeading;
    }

    /**
     * @return the subHeading
     */
    public String getSubHeading() {
        return subHeading;
    }

    /**
     * @param subHeading the subHeading to set
     */
    public void setSubHeading(String subHeading) {
        this.subHeading = subHeading;
    }

    /**
     * @return the transactionNo
     */
    public String getTransactionNo() {
        return transactionNo;
    }

    /**
     * @param transactionNo the transactionNo to set
     */
    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    /**
     * @return the bankTransactionId
     */
    public String getBankTransactionId() {
        return bankTransactionId;
    }

    /**
     * @param bankTransactionId the bankTransactionId to set
     */
    public void setBankTransactionId(String bankTransactionId) {
        this.bankTransactionId = bankTransactionId;
    }

    /**
     * @return the transactionDate
     */
    public String getTransactionDate() {
        return transactionDate;
    }

    /**
     * @param transactionDate the transactionDate to set
     */
    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
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
     * @return the transactionAmount
     */
    public int getTransactionAmount() {
        return transactionAmount;
    }

    /**
     * @param transactionAmount the transactionAmount to set
     */
    public void setTransactionAmount(int transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    /**
     * @return the amountInWords
     */
    public String getAmountInWords() {
        return amountInWords;
    }

    /**
     * @param amountInWords the amountInWords to set
     */
    public void setAmountInWords(String amountInWords) {
        this.amountInWords = amountInWords;
    }

    /**
     * @return the bankReferenceNo
     */
    public String getBankReferenceNo() {
        return bankReferenceNo;
    }

    /**
     * @param bankReferenceNo the bankReferenceNo to set
     */
    public void setBankReferenceNo(String bankReferenceNo) {
        this.bankReferenceNo = bankReferenceNo;
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
     * @return the failureMessage
     */
    public String getFailureMessage() {
        return failureMessage;
    }

    /**
     * @param failureMessage the failureMessage to set
     */
    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    /**
     * @return the failureTab
     */
    public boolean isFailureTab() {
        return failureTab;
    }

    /**
     * @param failureTab the failureTab to set
     */
    public void setFailureTab(boolean failureTab) {
        this.failureTab = failureTab;
    }

    /**
     * @return the stateName
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * @param stateName the stateName to set
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getTransactionRegnNo() {
        return transactionRegnNo;
    }

    public void setTransactionRegnNo(String transactionRegnNo) {
        this.transactionRegnNo = transactionRegnNo;
    }

    public boolean isFlagInclude() {
        return flagInclude;
    }

    public void setFlagInclude(boolean flagInclude) {
        this.flagInclude = flagInclude;
    }

    public boolean isFlagIncludefail() {
        return flagIncludefail;
    }

    public void setFlagIncludefail(boolean flagIncludefail) {
        this.flagIncludefail = flagIncludefail;
    }

    /**
     * @return the flagForTC
     */
    public boolean isFlagForTC() {
        return flagForTC;
    }

    /**
     * @param flagForTC the flagForTC to set
     */
    public void setFlagForTC(boolean flagForTC) {
        this.flagForTC = flagForTC;
    }

    /**
     * @return the taxFieldLabel
     */
    public TaxFieldLabelBean getTaxFieldLabel() {
        return taxFieldLabel;
    }

    /**
     * @param taxFieldLabel the taxFieldLabel to set
     */
    public void setTaxFieldLabel(TaxFieldLabelBean taxFieldLabel) {
        this.taxFieldLabel = taxFieldLabel;
    }

    public String getLeftMenuURL() {
        return leftMenuURL;
    }

    public void setLeftMenuURL(String leftMenuURL) {
        this.leftMenuURL = leftMenuURL;
    }
}
