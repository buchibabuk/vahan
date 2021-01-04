/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.dealer;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import nic.java.util.CommonUtils;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.CryptographyAES;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.OnlinePayDobj;
import nic.vahan.form.impl.OnlinePayImpl;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

@ManagedBean(name = "ereceipt_bean")
@ViewScoped
public class EReceiptBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(EReceiptBean.class);
    private boolean renderReceipt = false;
    private boolean errorTab;
    private String mainHeading = "";
    private String subHeading = "";
    private String transactionNo;
    private String bankTransactionId = "";
    private String transactionDate = "";
    private String regnNo = "";
    private String chassisNo;
    private int transactionAmount;
    private String amountInWords;
    private String bankReferenceNo;
    private String status = "";
    private String failureMessage;
    private String stateName;
    private boolean walletSuccessTab;
    private boolean walletFailureTab;
    private boolean revertTab;
    private String applNoList = "";
    List<OnlinePayDobj> payDobjList = null;
    private String officeName;
    private String dealerName;
    private String qrCodeText;
    private String stateLogo;
    private String imageBackground;
    private boolean renderqrCode;
    private boolean renderPendingPanel = false;
    private boolean renderFailurePanel = false;
    private String statusReason;
    private Timestamp printDate = new Timestamp(new Date().getTime());
    private boolean renderORNotes = false;

    @PostConstruct
    public void init() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        CryptographyAES aes = new CryptographyAES();
        Exception ex = null;
        String response = null;
        String reason = null;
        HashMap returnParams = null;
        Double GrandTotal = 0.0;
        Double sentGrandTotal = 0.0;
        String sentTransactionId = "";
        int actionCode = 0;
        int offCd = 0;
        String userId = null;
        String stateCd = null;
        String paymentVerificationType = null;
        renderReceipt = false;
        try {
            stateCd = Util.getUserStateCode();
            userId = Util.getEmpCode();
            if (userId == null) {
                throw new VahanException("Session timeout, please try again.");
            }
            HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            response = req.getParameter("response");
            returnParams = aes.getReturnParameters(response);
            status = (String) returnParams.get("status");
            transactionNo = (String) returnParams.get("paymentId");
            reason = (String) returnParams.get("status_desc");
            bankTransactionId = (String) returnParams.get("transId");
            mainHeading = ServerUtil.getRcptHeading();
            subHeading = ServerUtil.getRcptSubHeading();

            System.out.println(DateUtils.getDateInDDMMYYYY_HHMMSS(new java.util.Date()) + " Received Data: " + " Status:" + status + ", PaymentId:" + transactionNo + ", Reason:" + reason + ", Grandtotal:" + returnParams.get("total_amount"));
            if (returnParams.get("total_amount") != null) {
                try {
                    GrandTotal = Double.parseDouble(returnParams.get("total_amount").toString());
                } catch (Exception e) {
                    GrandTotal = 0.0;
                }
            }

            if (session.getAttribute("GrandTotal") != null) {
                sentGrandTotal = Double.parseDouble(session.getAttribute("GrandTotal").toString());
            }
            if (session.getAttribute("TransactionId") != null) {
                sentTransactionId = session.getAttribute("TransactionId").toString();
            }

            if (!sentTransactionId.equals(transactionNo) || (status != null && status.equals(TableConstants.VAHAN_PGI_SUCCESS) && !sentGrandTotal.equals(GrandTotal))) {
                String strMessage = "SendData: TransId:" + sentTransactionId + " , Amt:"
                        + sentGrandTotal + " RcvdData: TransId:" + transactionNo + " , Amt:" + GrandTotal;

                System.out.println(DateUtils.getDateInDDMMYYYY_HHMMSS(new java.util.Date()) + " " + strMessage + " Payment not done due to transactionId/amount mismatch.");
                throw new VahanException("Due to some reason receipt could not generated. Please verify again.");
            }

            if (session.getAttribute("PaymentVerificationType") != null) {
                paymentVerificationType = session.getAttribute("PaymentVerificationType").toString();
            } else {
                throw new VahanException("Something Went Wrong.Please Try Again.");
            }

            SeatAllotedDetails selectedSeat = (SeatAllotedDetails) Util.getSelectedSeat();
            if (selectedSeat != null) {
                actionCode = selectedSeat.getAction_cd();
                offCd = selectedSeat.getOff_cd();
            }

            String userCatg = Util.getUserCategory();

            if (status != null && paymentVerificationType != null && status.equals(TableConstants.VAHAN_PGI_SUCCESS) && paymentVerificationType.equals(TableConstants.DEALER_PAYMENT_VERIFICATION)) {
                this.saveSuccessDetails(userId, actionCode, stateCd, offCd, reason, userCatg);
            } else if (status != null && paymentVerificationType != null && status.equals(TableConstants.VAHAN_PGI_FAIL) && paymentVerificationType.equals(TableConstants.DEALER_PAYMENT_VERIFICATION)) {
                this.failureCase(userId, reason, userCatg);
            } else if (status != null && paymentVerificationType != null && status.equals(TableConstants.VAHAN_PGI_PENDING) && paymentVerificationType.equals(TableConstants.DEALER_PAYMENT_VERIFICATION)) {
                this.pendingCase(reason);
            } else if (status != null && paymentVerificationType != null && status.equals(TableConstants.VAHAN_PGI_SUCCESS) && paymentVerificationType.equals(TableConstants.DEALER_PAYMENT_REVERIFICATION)) {
                this.walletSuccessfullCase(userId, reason, stateCd, offCd);
            } else if (status != null && paymentVerificationType != null && status.equals(TableConstants.VAHAN_PGI_FAIL) && paymentVerificationType.equals(TableConstants.DEALER_PAYMENT_REVERIFICATION)) {
                this.walletFailureCase(reason);
            } else {
                throw new VahanException("Unable to proccess your request.Please Try Again.");
            }
        } catch (VahanException e) {
            failureMessage = e.getMessage();
            ex = e;
        } catch (Exception e) {
            failureMessage = e.getMessage();
            ex = e;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (session != null) {
                    session.removeAttribute("DealerPayment");
                    session.removeAttribute("GrandTotal");
                    session.removeAttribute("TransactionId");
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        if (ex != null) {
            errorTab = true;
        }
    }

    public void pendingCase(String reason) throws VahanException {
        OnlinePayImpl owImpl = new OnlinePayImpl();
        payDobjList = owImpl.getPendingCaseDetails(transactionNo);
        status = "Pending (Please read the instruction given below to re-verify the transaction)";
        statusReason = reason;
        getDetails(payDobjList);
        renderPendingPanel = true;
    }

    public void failureCase(String userId, String reason, String userCatg) throws VahanException {
        OnlinePayImpl owImpl = new OnlinePayImpl();
        payDobjList = owImpl.saveDataAfterFailurePayment(userId, transactionNo, reason, userCatg);
        status = "Failed (Please read the instruction given below to re-verify the transaction)";
        statusReason = reason;
        getDetails(payDobjList);
        renderFailurePanel = true;
    }

    public void getDetails(List<OnlinePayDobj> payDobjList) {
        if (payDobjList != null && !payDobjList.isEmpty()) {
            if (payDobjList.size() > 0) {
                for (OnlinePayDobj payObj : payDobjList) {
                    transactionAmount = transactionAmount + payObj.getTransactionAmount();
                    applNoList = payObj.getApplNo() + "," + applNoList;
                }
            }
            if (!CommonUtils.isNullOrBlank(applNoList)) {
                applNoList = applNoList.substring(0, applNoList.length() - 1);
            }
            amountInWords = new Utility().ConvertNumberToWords(transactionAmount);
        }
    }

    public void saveSuccessDetails(String userId, int actionCode, String stateCd, int offCd, String reason, String userCatg) throws VahanException {
        OnlinePayImpl owImpl = new OnlinePayImpl();
        try {
            payDobjList = owImpl.getRecptNoRegnNoAndInsertDataForNewVehicles(userId, transactionNo, actionCode, stateCd, offCd, userCatg);

            if (payDobjList != null && !payDobjList.isEmpty()) {
                if (bankTransactionId == null || bankTransactionId.isEmpty()) {
                    bankTransactionId = payDobjList.get(0).getTransactionNo().toString();
                }
                if (payDobjList.size() > 0) {
                    for (OnlinePayDobj payObj : payDobjList) {
                        transactionAmount = transactionAmount + payObj.getTransactionAmount();
                    }
                }
                this.setReceiptDetails(payDobjList);
                status = reason;
                renderReceipt = true;
                if ("OR".equals(stateCd)) {
                    renderORNotes = true;
                }
            } else {
                throw new VahanException("Unable to proccess your request.Please Try Again");
            }
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error("TransactionNo: " + transactionNo + ": " + ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
    }

    public void walletSuccessfullCase(String userId, String reason, String stateCd, int offCd) throws VahanException {
        OnlinePayImpl owImpl = new OnlinePayImpl();
        Utility utility = new Utility();
        try {
            payDobjList = owImpl.updateFailTransAppls(transactionNo, userId, stateCd, offCd, reason);
            if (payDobjList != null && !payDobjList.isEmpty()) {
                String reVerifyActionType = payDobjList.get(0).getReverifyActionType().toString();
                transactionAmount = payDobjList.get(0).getTransactionAmount();
                applNoList = (payDobjList.get(0).getApplNoList()).replaceAll("'", " ");
                amountInWords = utility.ConvertNumberToWords(transactionAmount);
                if (reVerifyActionType.equals(TableConstants.REVERIFY_WALLET)) {
                    walletSuccessTab = true;
                } else if (reVerifyActionType.equals(TableConstants.REVERIFY_REVERT)) {
                    revertTab = true;
                }
            } else {
                throw new VahanException("Problem in Reverification.");
            }

        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error("TransactionNo: " + transactionNo + ": " + ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
    }

    public void walletFailureCase(String reason) throws VahanException {
        walletFailureTab = true;
        status = "Transaction Failed. Due to " + reason;
    }

    public void setReceiptDetails(List<OnlinePayDobj> OnlinePayDobjList) {
        stateName = payDobjList.get(0).getStateName().toString();
        bankReferenceNo = payDobjList.get(0).getBankReferenceNo().toString();
        if (!CommonUtils.isNullOrBlank(bankReferenceNo)) {
            if (!CommonUtils.isNullOrBlank(payDobjList.get(0).getTreasuryRefNo())) {
                bankReferenceNo = bankReferenceNo + " / " + payDobjList.get(0).getTreasuryRefNo();
            }
        } else {
            bankReferenceNo = payDobjList.get(0).getTreasuryRefNo();
        }
        transactionDate = payDobjList.get(0).getTransactionDate().toString();
        amountInWords = new Utility().ConvertNumberToWords(transactionAmount);
        stateLogo = payDobjList.get(0).getStateLogo();
        imageBackground = payDobjList.get(0).getImageBackground();
        officeName = payDobjList.get(0).getOfficeName();
        dealerName = payDobjList.get(0).getDealerName();
        qrCodeText = "Payment Id:" + transactionNo + " \\nTransaction No: " + bankTransactionId + " \\nTransaction Date: " + transactionDate + " \\nTransaction Amount: " + transactionAmount;
        renderqrCode = true;
    }

    /**
     * @return the walletSuccessTab
     */
    public boolean isWalletSuccessTab() {
        return walletSuccessTab;
    }

    /**
     * @param walletSuccessTab the walletSuccessTab to set
     */
    public void setWalletSuccessTab(boolean walletSuccessTab) {
        this.walletSuccessTab = walletSuccessTab;
    }

    /**
     * @return the walletFailureTab
     */
    public boolean isWalletFailureTab() {
        return walletFailureTab;
    }

    /**
     * @param walletFailureTab the walletFailureTab to set
     */
    public void setWalletFailureTab(boolean walletFailureTab) {
        this.walletFailureTab = walletFailureTab;
    }

    /**
     * @return the revertTab
     */
    public boolean isRevertTab() {
        return revertTab;
    }

    /**
     * @param revertTab the revertTab to set
     */
    public void setRevertTab(boolean revertTab) {
        this.revertTab = revertTab;
    }

    /**
     * @return the applNoList
     */
    public String getApplNoList() {
        return applNoList;
    }

    /**
     * @param applNoList the applNoList to set
     */
    public void setApplNoList(String applNoList) {
        this.applNoList = applNoList;
    }

    /**
     * @return the transaction_no
     */
    public String getTransaction_no() {
        return getTransactionNo();
    }

    /**
     * @param transaction_no the transaction_no to set
     */
    public void setTransaction_no(String transactionNo) {
        this.setTransactionNo(transactionNo);
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
     * @return the officeName
     */
    public String getOfficeName() {
        return officeName;
    }

    /**
     * @param officeName the officeName to set
     */
    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    /**
     * @return the dealerName
     */
    public String getDealerName() {
        return dealerName;
    }

    /**
     * @param dealerName the dealerName to set
     */
    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    /**
     * @return the payDobjList
     */
    public List<OnlinePayDobj> getPayDobjList() {
        return payDobjList;
    }

    /**
     * @param payDobjList the payDobjList to set
     */
    public void setPayDobjList(List<OnlinePayDobj> payDobjList) {
        this.payDobjList = payDobjList;
    }

    /**
     * @return the qrCodeText
     */
    public String getQrCodeText() {
        return qrCodeText;
    }

    /**
     * @param qrCodeText the qrCodeText to set
     */
    public void setQrCodeText(String qrCodeText) {
        this.qrCodeText = qrCodeText;
    }

    /**
     * @return the stateLogo
     */
    public String getStateLogo() {
        return stateLogo;
    }

    /**
     * @param stateLogo the stateLogo to set
     */
    public void setStateLogo(String stateLogo) {
        this.stateLogo = stateLogo;
    }

    /**
     * @return the imageBackground
     */
    public String getImageBackground() {
        return imageBackground;
    }

    /**
     * @param imageBackground the imageBackground to set
     */
    public void setImageBackground(String imageBackground) {
        this.imageBackground = imageBackground;
    }

    /**
     * @return the renderReceipt
     */
    public boolean isRenderReceipt() {
        return renderReceipt;
    }

    /**
     * @param renderReceipt the renderReceipt to set
     */
    public void setRenderReceipt(boolean renderReceipt) {
        this.renderReceipt = renderReceipt;
    }

    /**
     * @return the renderqrCode
     */
    public boolean isRenderqrCode() {
        return renderqrCode;
    }

    /**
     * @param renderqrCode the renderqrCode to set
     */
    public void setRenderqrCode(boolean renderqrCode) {
        this.renderqrCode = renderqrCode;
    }

    /**
     * @return the printDate
     */
    public Timestamp getPrintDate() {
        return printDate;
    }

    /**
     * @param printDate the printDate to set
     */
    public void setPrintDate(Timestamp printDate) {
        this.printDate = printDate;
    }

    /**
     * @return the statusReason
     */
    public String getStatusReason() {
        return statusReason;
    }

    /**
     * @param statusReason the statusReason to set
     */
    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    /**
     * @return the renderPendingPanel
     */
    public boolean isRenderPendingPanel() {
        return renderPendingPanel;
    }

    /**
     * @param renderPendingPanel the renderPendingPanel to set
     */
    public void setRenderPendingPanel(boolean renderPendingPanel) {
        this.renderPendingPanel = renderPendingPanel;
    }

    /**
     * @return the renderFailurePanel
     */
    public boolean isRenderFailurePanel() {
        return renderFailurePanel;
    }

    /**
     * @param renderFailurePanel the renderFailurePanel to set
     */
    public void setRenderFailurePanel(boolean renderFailurePanel) {
        this.renderFailurePanel = renderFailurePanel;
    }

    public boolean isRenderORNotes() {
        return renderORNotes;
    }

    public void setRenderORNotes(boolean renderORNotes) {
        this.renderORNotes = renderORNotes;
    }
}
