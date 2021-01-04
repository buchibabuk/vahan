/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.eapplication;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.CryptographyAES;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import nic.vahan.form.dobj.AltDobj;
import nic.vahan.form.dobj.ConvDobj;
import nic.vahan.form.dobj.dealer.PaymentGatewayDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.impl.AltImpl;
import nic.vahan.form.impl.ConvImpl;
import nic.vahan.form.impl.DocumentUploadImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nl.captcha.Captcha;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "onlinePaymentBean")
@ViewScoped
public class OnlinePaymentBean implements Serializable {

    private String masterLayout = "/masterLayoutPage.xhtml";
    private String userId = "";
    private String password = "";
    private List<OnlinePaymentDobj> getDetailList = null;
    private boolean showLoginPanel = true;
    private boolean showMakePaymentPanel = false;
    private boolean makePaymentButtonVisibility = false;
    private long totalPayableAmount = 0L;
    private String hiddenRandomNo;
    private boolean verifyButtonVisibility = false;
    private SeatAllotedDetails seatWork = new SeatAllotedDetails();
    private PaymentDobj paySessionDobj = new PaymentDobj();
    private static final Logger LOGGER = Logger.getLogger(OnlinePaymentBean.class);
    private OwnerDetailsDobj ownerDetailsDobj;
    private boolean renderRevertBackPayment = false;
    private String appl_No = "";
    private boolean tradeCertificate;
    private String onlinePaymentType = "freshPay";
    private List<PaymentGatewayDobj> checkFailList = new ArrayList<>();

    public OnlinePaymentBean() {
        masterLayout = "/ui/eapplication/masterLayoutEapplication.xhtml";
        resetRandomNo();
    }

    private void resetRandomNo() {
        hiddenRandomNo = ServerUtil.generateRandomAlphaNumeric(40);
    }

    public void validUser() {
        String createdBy = null;
        String createdDt = null;
        OnlinePaymentImpl owPayImpl = new OnlinePaymentImpl();
        String errorMsg = null;
        Exception ex = null;
        try {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            String captcha = (String) session.getAttribute("captcha");
            Captcha secretcaptcha = (Captcha) session.getAttribute("serverCaptcha");
            if (captcha == null) {
                throw new VahanException("Verification Code is missing");
            } else {
                if (!secretcaptcha.isCorrect(captcha)) {
                    throw new VahanException("Verification code does not match");
                }
            }
            List list = OnlinePaymentImpl.validateUserIdAndPassword(appl_No, password, hiddenRandomNo);
            if (list.size() > 0) {
                userId = (String) list.get(0);
                createdBy = (String) list.get(1);
                createdDt = (String) list.get(2);
                getDetailList = owPayImpl.getDetail(userId, appl_No);
                if (!getDetailList.isEmpty()) {
                    if (authenticateUser(createdDt, appl_No) && "freshPay".equals(onlinePaymentType)) {
                        if (owPayImpl.getPaymentRevertBack(userId, appl_No, getDetailList.get(0).getPaymentType())) {
                            throw new VahanException("Your Login Credentials Expired, Please go to respective RTO to re-generate Credentials for payment.");
                        }
                    } else if ("checkFail".equals(onlinePaymentType)) {
                        checkFailList = OnlinePaymentImpl.getOnlinePaymentFailedList(getDetailList.get(0).getStateCd(), getDetailList.get(0).getOffCode(), appl_No);
                        if (checkFailList == null || checkFailList.isEmpty()) {
                            throw new VahanException("Problem occurred during getting failed transactions details or there is no any failed transaction details against the Application " + appl_No + ".");
                        }
                    }
                    setShowLoginPanel(false);
                    setShowMakePaymentPanel(true);
                }
                if (!getDetailList.isEmpty()) {
                    for (OnlinePaymentDobj payObj : getDetailList) {
                        totalPayableAmount = totalPayableAmount + payObj.getPenalty() + payObj.getAmount();
                    }

                    appl_No = getDetailList.get(0).getApplNo();
                    paySessionDobj.setApplNo(getDetailList.get(0).getApplNo());
                    paySessionDobj.setStateCd(getDetailList.get(0).getStateCd());
                    paySessionDobj.setOffCd(getDetailList.get(0).getOffCode());
                    paySessionDobj.setActionCd(getDetailList.get(0).getActionCode());
                    paySessionDobj.setCreatedBy(createdBy);
                    paySessionDobj.setUserId(userId);
                    paySessionDobj.setTransNo(getDetailList.get(0).getTransactionNo());
                    paySessionDobj.setRegnNo(getDetailList.get(0).getRegnNo());
                    paySessionDobj.setUserCatg(TableConstants.USER_CATG_OFF_STAFF);
                    paySessionDobj.setPaymentType(getDetailList.get(0).getPaymentType());
                    paySessionDobj.setBalanceFeePaySelectedMode(getDetailList.get(0).getBalanceFeePaySelectedMode());
                    paySessionDobj.setPurCd(getDetailList.get(0).getPurCd());

                    if ("TradeRegn".equals(getDetailList.get(0).getRegnNo())) {
                        setTradeCertificate(true);
                    }
                    if (paySessionDobj.getTransNo() == null || paySessionDobj.getTransNo().equalsIgnoreCase("")) {
                        setMakePaymentButtonVisibility(true);
                        setRenderRevertBackPayment(true);
                        setVerifyButtonVisibility(false);
                    } else {
                        setMakePaymentButtonVisibility(false);
                        setVerifyButtonVisibility(true);
                    }

                    if (totalPayableAmount == 0) {
                        makePaymentButtonVisibility = false;
                        renderRevertBackPayment = false;
                    }

                    OwnerImpl ownerImpl = new OwnerImpl();
                    if (!isTradeCertificate() && (CommonUtils.isNullOrBlank(getDetailList.get(0).getRegnNo()) || getDetailList.get(0).getRegnNo().equalsIgnoreCase("NEW") || getDetailList.get(0).getRegnNo().equalsIgnoreCase("TEMPREG") || getDetailList.get(0).getPurCd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) || (!CommonUtils.isNullOrBlank(getDetailList.get(0).getBalanceFeePaySelectedMode()) && "A".equals(getDetailList.get(0).getBalanceFeePaySelectedMode()))) {
                        if (getDetailList.get(0).getPurCd() == TableConstants.VM_PMT_FRESH_PUR_CD && getDetailList.get(0).getRegnNo().equalsIgnoreCase("NEW")) {
                            PermitOwnerDetailDobj PmtOwnerDobj = new PermitOwnerDetailImpl().set_VA_Owner_permit_to_dobj(getDetailList.get(0).getApplNo().toUpperCase(), getDetailList.get(0).getRegnNo());
                            if (PmtOwnerDobj != null) {
                                ownerDetailsDobj = new OwnerDetailsDobj();
                                ownerDetailsDobj.setOwner_name(PmtOwnerDobj.getOwner_name());
                                ownerDetailsDobj.setF_name(PmtOwnerDobj.getF_name());
                                ownerDetailsDobj.setVh_class_desc(PmtOwnerDobj.getVh_class_desc());
                                ownerDetailsDobj.setVh_class(PmtOwnerDobj.getVh_class());
                                ownerDetailsDobj.setVch_catg(PmtOwnerDobj.getVch_catg());
                            }
                        } else {
                            ownerDetailsDobj = new DocumentUploadImpl().getVaOwnerDetailsForDocumentUpload(getDetailList.get(0).getApplNo().toUpperCase(), getDetailList.get(0).getStateCd(), null);
                        }
                    } else if (!isTradeCertificate() && TableConstants.ONLINE_FANCY.equals(getDetailList.get(0).getBalanceFeePaySelectedMode())) {
                        owPayImpl.validateAdvanceRegistrationNumber(paySessionDobj.getStateCd(), paySessionDobj.getRegnNo(), paySessionDobj.getOffCd());
                        String[] arr = owPayImpl.getOwnerDetailsForFancyNoDetails(appl_No);
                        ownerDetailsDobj = new OwnerDetailsDobj();
                        ownerDetailsDobj.setOwner_name(arr[0]);
                        ownerDetailsDobj.setF_name(arr[1]);
                        ownerDetailsDobj.setVh_class_desc(arr[2]);
                    } else if (!isTradeCertificate() && !getDetailList.get(0).getRegnNo().equalsIgnoreCase("NEW") || (!CommonUtils.isNullOrBlank(getDetailList.get(0).getPaymentType()) && TableConstants.TAX_MODE_BALANCE.equals(getDetailList.get(0).getPaymentType()) && "R".equals(getDetailList.get(0).getBalanceFeePaySelectedMode()))) {
                        ConvDobj convdobj = ConvImpl.set_Conversion_appl_db_to_dobj(appl_No, null);
                        AltImpl altImpl = new AltImpl();
                        AltDobj altDobj = altImpl.set_ALT_appl_db_to_dobj(appl_No);
                        ownerDetailsDobj = ownerImpl.getOwnerDetails(getDetailList.get(0).getRegnNo().toUpperCase());
                        if (altDobj != null || convdobj != null) {
                            if (altDobj != null) {
                                ownerDetailsDobj.setVh_class_desc(ServerUtil.getVehicleClassDescr(altDobj.getVh_class()));
                                ownerDetailsDobj.setVch_catg(ServerUtil.getCatgDesc(altDobj.getVch_catg()));
                            }
                            if (convdobj != null) {
                                ownerDetailsDobj.setVh_class_desc(ServerUtil.getVehicleClassDescr(convdobj.getNew_vch_class()));
                                if (!paySessionDobj.getStateCd().equalsIgnoreCase("OR")) {
                                    ownerDetailsDobj.setVch_catg(ServerUtil.getCatgDesc(convdobj.getNew_vch_catg()));
                                }
                                if (ServerUtil.isTransport(convdobj.getNew_vch_class(), null)) {
                                    ownerDetailsDobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR);
                                } else {
                                    ownerDetailsDobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_NON_TRANSPORT_DESCR);
                                }
                            }
                        }
                    }
                } else {
                    JSFUtils.setFacesMessage("Problem in getting Details. Please contact Administrator!!!!", null, JSFUtils.ERROR);
                }
            } else {
                JSFUtils.setFacesMessage("Invalid Login, or Login Credentials Expired. Please contact Administrator!!!!", null, JSFUtils.INFO);
            }
        } catch (VahanException e) {
            JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.WARN);
        } catch (Exception e) {
            ex = e;
            errorMsg = "Invalid Login. Please Try Again!!!";
        }

        if (ex != null) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.setFacesMessage(errorMsg, null, JSFUtils.WARN);
        }
    }

    public String revertBack() {
        String return_path = "";
        try {
            OnlinePaymentImpl owPayImpl = new OnlinePaymentImpl();
            boolean flag = owPayImpl.getPaymentRevertBack(userId, appl_No, paySessionDobj.getPaymentType());
            if (flag) {
                return_path = "/vahan/ui/login/login.xhtml";
            }
        } catch (VahanException ve) {
            JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.WARN);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return return_path;
    }

    public String makePayment() {
        OnlinePaymentImpl payment_gateway_impl = new OnlinePaymentImpl();
        String vahan_pgi_url = "";
        String ipPath = "";
        String return_path = "";
        CryptographyAES aes = new CryptographyAES();
        try {
            if (totalPayableAmount > 0) {
                vahan_pgi_url = ServerUtil.getVahanPgiUrl(TableConstants.VAHAN_PGI_CONNTYPE);
                String trans_no = payment_gateway_impl.updateTransactionNumber(paySessionDobj.getStateCd(), paySessionDobj.getApplNo(), paySessionDobj.getOffCd(), Long.parseLong(userId));
                ipPath = ServerUtil.getIpPath();
                String return_url = null;
                try {
                    if (trans_no.equals("")) {
                        throw new VahanException("PaymentId not generated due to technical error.");
                    } else {
                        return_url = ipPath + "/vahan/ui/eapplication/form_print_online_payment_receipt.xhtml";
                        String data = "payment_id=" + trans_no + "|" + "state_cd=" + paySessionDobj.getStateCd() + "|" + "user_cd=" + userId + "|" + "return_url=" + return_url + "|" + "nic_merchant=" + TableConstants.VAHAN_PGI_MERCHANT_CODE + "|" + "total_payable_amount=" + totalPayableAmount + "|" + "off_cd=" + paySessionDobj.getOffCd();
                        System.out.println(DateUtils.getDateInDDMMYYYY_HHMMSS(new java.util.Date()) + " For Online Payment RTO" + " FirstPayment: paymentId:" + trans_no + ", ApplNos:" + paySessionDobj.getApplNo() + ", Amt:" + totalPayableAmount);
                        data = aes.getEncriptedString(data);
                        String ip = vahan_pgi_url + data;
                        paySessionDobj.setTotalPayableAmount(totalPayableAmount);
                        paySessionDobj.setTransNo(trans_no);
                        setSessionValue(TableConstants.DEALER_PAYMENT_VERIFICATION);
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
        String return_path = "";
        String ipPath = "";
        String vahan_pgi_url = "";
        try {
            if (totalPayableAmount > 0) {
                vahan_pgi_url = ServerUtil.getVahanPgiUrl(TableConstants.VAHAN_PGI_CHECK_FAIL_CONNTYPE);
                ipPath = ServerUtil.getIpPath();
                String return_url = null;
                return_url = ipPath + "/vahan/ui/eapplication/form_print_online_payment_receipt.xhtml";
                String data = "payment_id=" + paySessionDobj.getTransNo() + "|" + "state_cd=" + paySessionDobj.getStateCd() + "|" + "total_payable_amount=" + totalPayableAmount + "|" + "return_url=" + return_url + "|" + "nic_merchant=" + TableConstants.VAHAN_PGI_MERCHANT_CODE + "|" + "off_cd=" + paySessionDobj.getOffCd();
                System.out.println(DateUtils.getDateInDDMMYYYY_HHMMSS(new java.util.Date()) + " For Online Payment RTO" + " D-Verify: paymentId:" + paySessionDobj.getTransNo() + ", ApplNos:" + paySessionDobj.getApplNo() + ", Amt:" + totalPayableAmount);
                data = aes.getEncriptedString(data);
                String ip = vahan_pgi_url + data;
                paySessionDobj.setTotalPayableAmount(totalPayableAmount);
                setSessionValue(TableConstants.DEALER_PAYMENT_VERIFICATION);
                HttpServletResponse hres = ((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse());
                hres.sendRedirect(ip);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return return_path;
    }

    private void setSessionValue(String paymentType) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        setSelectedSeatValue();
        session.setAttribute("paySessionDobj", paySessionDobj);
        session.setAttribute("SelectedSeat", seatWork);
        session.setAttribute("state_cd", paySessionDobj.getStateCd());
        session.setAttribute("off_cd", paySessionDobj.getOffCd());
        session.setAttribute("emp_cd", paySessionDobj.getCreatedBy());
        session.setAttribute("regn_no", paySessionDobj.getRegnNo());
        session.setAttribute("DealerPayment", true);
        session.setAttribute("onlinePayment", true);
        session.setAttribute("user_catg", TableConstants.USER_CATG_OFF_STAFF);
        session.setAttribute("onlinePaymentVerifyType", paymentType);
    }

    private void setSelectedSeatValue() {
        seatWork.setAction_cd(paySessionDobj.getActionCd());
        seatWork.setAppl_no(paySessionDobj.getApplNo());
        seatWork.setOff_cd(paySessionDobj.getOffCd());
    }

    public String reVerifyPayment(PaymentGatewayDobj dobj) {
        String vahan_pgi_url = "";
        String ipPath = "";
        String return_dealer_url = null;
        try {
            // check for application count and amount 
            new OnlinePaymentImpl().checkReVerifyApplicationCountAndAmount(dobj.getPaymentId(), paySessionDobj.getStateCd(), paySessionDobj.getOffCd(), dobj.getApplicationNumberList());
            vahan_pgi_url = ServerUtil.getVahanPgiUrl(TableConstants.VAHAN_PGI_CHECK_FAIL_CONNTYPE);
            ipPath = ServerUtil.getIpPath();
            return_dealer_url = ipPath + "/vahan/ui/eapplication/form_print_online_payment_receipt.xhtml";
            String data = "payment_id=" + dobj.getPaymentId() + "|" + "state_cd=" + paySessionDobj.getStateCd() + "|" + "total_payable_amount=" + dobj.getTtlAmount() + "|" + "return_url=" + return_dealer_url + "|" + "nic_merchant=" + TableConstants.VAHAN_PGI_MERCHANT_CODE + "|" + "off_cd=" + paySessionDobj.getOffCd();
            System.out.println(DateUtils.getDateInDDMMYYYY_HHMMSS(new java.util.Date()) + " D-Verify: paymentId:" + dobj.getPaymentId() + ", ApplNos:" + dobj.getApplicationNumberList() + ", Amt:" + dobj.getTtlAmount());
            data = new CryptographyAES().getEncriptedString(data);
            String ip = vahan_pgi_url + data;
            paySessionDobj.setTotalPayableAmount(dobj.getTtlAmount());
            paySessionDobj.setTransNo(dobj.getPaymentId());
            setSessionValue(TableConstants.DEALER_PAYMENT_REVERIFICATION);
            HttpServletResponse hres = ((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse());
            hres.sendRedirect(ip);// redirect to PGI for re-verify
        } catch (VahanException vmex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vmex.getMessage(), vmex.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Problem occured during re-verify the failed transaction.", "Problem occured during re-verify the failed transaction."));
        }
        return "";
    }

    public void openReverifyDialog() {
        if ("checkFail".equals(onlinePaymentType)) {
            PrimeFaces.current().ajax().update("form_online_pay:online_fee_subview:verify_steps_pnl_id");
            PrimeFaces.current().executeScript("PF('reVerifyVar').show()");
            //RequestContext.getCurrentInstance().update("verify_steps_pnl_id");
            //RequestContext.getCurrentInstance().execute("PF('reVerifyVar').show()");
        }
    }

    /**
     * @return the masterLayout
     */
    public String getMasterLayout() {
        return masterLayout;
    }

    /**
     * @param masterLayout the masterLayout to set
     */
    public void setMasterLayout(String masterLayout) {
        this.masterLayout = masterLayout;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the showLoginPanel
     */
    public boolean isShowLoginPanel() {
        return showLoginPanel;
    }

    /**
     * @param showLoginPanel the showLoginPanel to set
     */
    public void setShowLoginPanel(boolean showLoginPanel) {
        this.showLoginPanel = showLoginPanel;
    }

    /**
     * @return the showMakePaymentPanel
     */
    public boolean isShowMakePaymentPanel() {
        return showMakePaymentPanel;
    }

    /**
     * @param showMakePaymentPanel the showMakePaymentPanel to set
     */
    public void setShowMakePaymentPanel(boolean showMakePaymentPanel) {
        this.showMakePaymentPanel = showMakePaymentPanel;
    }

    /**
     * @return the getDetailList
     */
    public List<OnlinePaymentDobj> getGetDetailList() {
        return getDetailList;
    }

    /**
     * @param getDetailList the getDetailList to set
     */
    public void setGetDetailList(List<OnlinePaymentDobj> getDetailList) {
        this.getDetailList = getDetailList;
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
     * @return the hiddenRandomNo
     */
    public String getHiddenRandomNo() {
        return hiddenRandomNo;
    }

    /**
     * @param hiddenRandomNo the hiddenRandomNo to set
     */
    public void setHiddenRandomNo(String hiddenRandomNo) {
        this.hiddenRandomNo = hiddenRandomNo;
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
     * @return the paySessionDobj
     */
    public PaymentDobj getPaySessionDobj() {
        return paySessionDobj;
    }

    /**
     * @param paySessionDobj the paySessionDobj to set
     */
    public void setPaySessionDobj(PaymentDobj paySessionDobj) {
        this.paySessionDobj = paySessionDobj;
    }

    /**
     * @return the ownerDetailsDobj
     */
    public OwnerDetailsDobj getOwnerDetailsDobj() {
        return ownerDetailsDobj;
    }

    /**
     * @param ownerDetailsDobj the ownerDetailsDobj to set
     */
    public void setOwnerDetailsDobj(OwnerDetailsDobj ownerDetailsDobj) {
        this.ownerDetailsDobj = ownerDetailsDobj;
    }

    public boolean isRenderRevertBackPayment() {
        return renderRevertBackPayment;
    }

    public void setRenderRevertBackPayment(boolean renderRevertBackPayment) {
        this.renderRevertBackPayment = renderRevertBackPayment;
    }

    public String getAppl_No() {
        return appl_No;
    }

    public void setAppl_No(String appl_No) {
        this.appl_No = appl_No;
    }

    /*
     * Returns true if transaction not initiated and user login after one day else false.
     */
    public boolean authenticateUser(String createdDt, String appl_No) throws ParseException, VahanException {
        boolean auth = false;
        OnlinePaymentImpl online = new OnlinePaymentImpl();
        String transactioNo = online.getTransactionNumber(appl_No);
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        String fromDate = createdDt;
        Date date = new Date();
        String toDate = dateformat.format(date);
        Date date1 = dateformat.parse(fromDate);
        Date date2 = dateformat.parse(toDate);
        long diff = date2.getTime() - date1.getTime();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        if (days > 0 && CommonUtils.isNullOrBlank(transactioNo)) {
            auth = true;
        }
        return auth;

    }

    /**
     * @return the tradeCertificate
     */
    public boolean isTradeCertificate() {
        return tradeCertificate;
    }

    /**
     * @param tradeCertificate the tradeCertificate to set
     */
    public void setTradeCertificate(boolean tradeCertificate) {
        this.tradeCertificate = tradeCertificate;
    }

    /**
     * @return the checkFailList
     */
    public List<PaymentGatewayDobj> getCheckFailList() {
        return checkFailList;
    }

    /**
     * @param checkFailList the checkFailList to set
     */
    public void setCheckFailList(List<PaymentGatewayDobj> checkFailList) {
        this.checkFailList = checkFailList;
    }

    /**
     * @return the onlinePaymentType
     */
    public String getOnlinePaymentType() {
        return onlinePaymentType;
    }

    /**
     * @param onlinePaymentType the onlinePaymentType to set
     */
    public void setOnlinePaymentType(String onlinePaymentType) {
        this.onlinePaymentType = onlinePaymentType;
    }
}
