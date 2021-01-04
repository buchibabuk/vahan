/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.fancy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.eapplication.OnlinePaymentImpl;
import nic.vahan.form.bean.PaymentCollectionBean;
import nic.vahan.form.bean.PrintRcptParticularDtls;
import nic.vahan.form.bean.ReceiptMasterBean;
import nic.vahan.form.bean.CommonChargesForAllBean;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.FeeDobj;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.Fee_Pay_Dobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;
import nic.vahan.form.impl.FeeDraftimpl;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.fancy.AdvanceRegnFeeImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "advancefancy")
@ViewScoped
public class AdvanceRegnFeeBean extends AdvanceRegnNo_dobj implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(AdvanceRegnFeeBean.class);
    private ArrayList listC_village = null;
    private ArrayList listC_taluk = null;
    private ArrayList listC_dist = null;
    private String SELECT_LABEL1 = "Reservation of New Registration Number";
    private String SELECT_LABEL2 = "Reservation of Lapsed Registration Number";
    private String SELECT_LABEL3 = "Payment of Balance of Auction Money";
    private String SELECT_VALUE1 = "1";
    private String SELECT_VALUE2 = "2";
    private String SELECT_VALUE3 = "3";
    private String rb_select_one = SELECT_VALUE1;//default Value
    private boolean saved_success = false;
    private boolean rcpt_view = false;
    private boolean bal_rcpt_view = false;
    private boolean blnAlreadyReg = false;
    private boolean blnTotalAmt = false;
    private boolean blnAlreadyRecp = true;
    private boolean blnCheckAlreadyReg = false;
    private boolean blnchkAdvanceRegn = true;
    transient FeeDraftimpl feeDraftImpl = null;
    @ManagedProperty(value = "#{rcpt_bean}")
    private ReceiptMasterBean rcpt_bean;
    String rcpt = null;
    private PaymentCollectionBean paymentBean;
    private transient List<PrintRcptParticularDtls> prntRecieptSubList;
    private int grandTotal;
    private String statename;
    private String rtooffice;
    private String receiptno;
    private String vhowner;
    private String regnno;
    private String vhclass;
    private String receiptdate;
    private String chasino;
    String regnType = "0";
    String vehClass = "0";
    int ownerCd;
    @ManagedProperty(value = "#{commonCharges}")
    private CommonChargesForAllBean commonCharges;
    private boolean showCommonChargesForAll;
    private long commonChargesAmt = 0;
    long reseveAmount = 0;
    int pmtType = 0;
    private String onlineUserCredentialmsg = "";
    private boolean renderOnlinePayBtn = false;
    private boolean renderSaveBtn = true;
    private boolean renderCancelPaymentBtn = false;
    private boolean renderUserAndPasswored = false;
    private boolean onlinePayment = false;

    @PostConstruct
    public void init() {
        try {
            TmConfigurationDobj tmConf = Util.getTmConfiguration();
            statename = Util.getSession().getAttribute("state_cd").toString();
            listC_dist = new ArrayList();
            paymentBean = new PaymentCollectionBean();
            String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
            for (int i = 0; i < data.length; i++) {
                if (data[i][2].trim().equals(Util.getUserStateCode())) {
                    listC_dist.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
            boolean flag = AdvanceRegnFeeImpl.getCheckFancyConfiguration(statename);
            if (flag == false) {
                setBlnchkAdvanceRegn(Boolean.TRUE);
                blnCheckAlreadyReg = true;
                blnAlreadyReg = true;
                blnAlreadyRecp = false;

            } else {
                setBlnchkAdvanceRegn(Boolean.FALSE);
                blnCheckAlreadyReg = false;
                blnAlreadyReg = false;
                blnAlreadyRecp = true;
                reservListener("0", "0");
            }
            setGetSerieslist(AdvanceRegnFeeImpl.getSeriesAndRunningNumber(Util.getUserStateCode(), Util.getUserOffCode()));
            try {
                boolean checkAmountEditable = Util.getTmConfiguration().isFancyFeeEditable();
                if (checkAmountEditable == true) {
                    setBlnTotalAmt(false);
                } else {
                    setBlnTotalAmt(true);
                }
            } catch (Exception ee) {
                LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            }
            if (tmConf != null) {
                if (tmConf.isOnlinePayment()) {
                    onlinePayment = true;
                    renderOnlinePayBtn = true;
                    renderSaveBtn = true;
                    renderCancelPaymentBtn = false;
                    renderUserAndPasswored = false;
                }
            }
        } catch (VahanException ve) {
            JSFUtils.showMessagesInDialog("Alert !!!", ve.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void lsnCheckAlreadyRegistered(AjaxBehaviorEvent actionEvent) {
        reset();

        if (blnCheckAlreadyReg) {
            blnAlreadyReg = false;
            blnAlreadyRecp = true;
        } else {
            blnAlreadyReg = true;
            blnAlreadyRecp = false;
        }
    }

    public void getDialogTable() {
        Exception e = null;
        try {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            session.removeAttribute("AdvanceAvailableNumberList");
            String seriesName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("seriesName");
            String runningNumber = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("runningNumber");
            int rNumber = Integer.parseInt(runningNumber);
            setAvailNumbers(AdvanceRegnFeeImpl.getAvailableNumbers(Util.getUserStateCode(), Util.getUserOffCode(), seriesName, rNumber));
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("AdvanceAvailableNumberList", getAvailNumbers());
            Map<String, Object> options = new HashMap<String, Object>();
            options.put("resizable", false);
            options.put("draggable", false);
            options.put("modal", true);
            PrimeFaces.current().dialog().openDynamic("formAdvanceAvailableNumbers", options, null);
        } catch (Exception ex) {
            e = ex;
        }
        if (e != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage(), e.getMessage()));
        }

    }

    public void regnoFocusLost(AjaxBehaviorEvent actionEvent) throws VahanException {
        boolean flg = false;
        Exception e = null;
        try {
            String regnNo = getRegn_no();
            if ((getRegn_no() == null) || getRegn_no().equals("")) {
                throw new VahanException("Please Enter Registration  Number");

            } else if (getRegn_no().length() < 8) {
                throw new VahanException("Please Enter Proper Registration Number");
            }
            String seriesPart = regnNo.substring(0, regnNo.length() - 4);
            String numPart = regnNo.substring(regnNo.length() - 4, regnNo.length());
            if (sanitizationNumberPart(numPart).equalsIgnoreCase("0000")) {
                throw new VahanException("Registration Number not valid.");
            }
            setRegn_no(sanitizationRegnNo(seriesPart, numPart));
            String off_cd = Util.getSession().getAttribute("selected_off_cd").toString().trim();
            String state_cd = Util.getSession().getAttribute("state_cd").toString();
            int offcd = Integer.parseInt(off_cd);
            if (Integer.parseInt(vehClass) != 0) {
                if (ServerUtil.isTransport(Integer.parseInt(vehClass), null)) {
                    regnType = "1";
                } else {
                    regnType = "2";
                }
            }
            reseveAmount = AdvanceRegnFeeImpl.getAmount(getRegn_no(), state_cd, offcd, regnType, vehClass, ownerCd, pmtType);
            boolean flag = AdvanceRegnFeeImpl.verifyFancyRegnNo(getRegn_no(), state_cd, offcd, regnType, vehClass, getRegn_appl_no(), pmtType);
            if (flag) {
                if (AdvanceRegnFeeImpl.isNumberBooked(getRegn_no())) {
                    flg = true;
                }
                //Fancy not allowed in office when eAuction running in state
                if ("HP".contains(Util.getUserStateCode())) {
                    String check = AdvanceRegnFeeImpl.checkFancyNumber(getRegn_no(), state_cd, regnType, vehClass, pmtType);
                    if (!check.equalsIgnoreCase("")) {
                        flg = true;
                        setTotal_amt(0);
                        reseveAmount = 0;
                    }
                }
                if (!flg) {
                    //Get Fancy Fee
                    int fancyAmt = AdvanceRegnFeeImpl.getAmount(getRegn_no(), state_cd, offcd, regnType, vehClass, ownerCd, pmtType);
                    setTotal_amt(fancyAmt);
                    reseveAmount = fancyAmt;
                    int servicesCharge = AdvanceRegnFeeImpl.getServicesCharges(state_cd, regnType, vehClass);
                    setUserCharge(servicesCharge);
                    if (getRegn_appl_no() != null && ("HR").contains(Util.getUserStateCode())) {
                        ArrayList<Status_dobj> purList = ServerUtil.applicationStatusByApplNo(getRegn_appl_no(), state_cd);
                        for (Status_dobj serdobj : purList) {
                            if (serdobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO) {
                                String check = AdvanceRegnFeeImpl.checkFancyNumber(serdobj.getRegn_no(), state_cd, regnType, vehClass, pmtType);
                                String newCheck = AdvanceRegnFeeImpl.checkFancyNumber(getRegn_no(), state_cd, regnType, vehClass, pmtType);
                                int totalFee = 0;
                                int fee1;
                                int fee2;
                                int oldNumberFancyRate;
                                if (!check.equalsIgnoreCase("")) {
                                    if (!newCheck.equalsIgnoreCase("")) {
                                        fee1 = AdvanceRegnFeeImpl.getOutOfTurnFee("OOTF", state_cd, regnType, vehClass);
                                        fee2 = AdvanceRegnFeeImpl.getOutOfTurnFee("NOCF", state_cd, regnType, vehClass);
                                        oldNumberFancyRate = AdvanceRegnFeeImpl.getAmount(serdobj.getRegn_no(), state_cd, offcd, regnType, vehClass, ownerCd, pmtType);
                                        if (oldNumberFancyRate == fancyAmt) {
                                            totalFee = fee2;
                                            setTotal_amt(totalFee);
                                            reseveAmount = getTotal_amt();
                                        } else {
                                            if (fancyAmt > oldNumberFancyRate) {
                                                totalFee = fancyAmt - oldNumberFancyRate;
                                            }
                                            totalFee += fee1 + fee2;
                                            setTotal_amt(totalFee);
                                            reseveAmount = getTotal_amt();
                                        }
                                    } else {
                                        totalFee = AdvanceRegnFeeImpl.getOutOfTurnFee("NOCF", state_cd, regnType, vehClass);
                                        setTotal_amt(totalFee);
                                        reseveAmount = getTotal_amt();
                                    }
                                } else if (!newCheck.equalsIgnoreCase("")) {
                                    fee2 = AdvanceRegnFeeImpl.getOutOfTurnFee("NOCF", state_cd, regnType, vehClass);
                                    totalFee = fee2 + fancyAmt;
                                    setTotal_amt(totalFee);
                                    reseveAmount = getTotal_amt();
                                } else {
                                    fee1 = AdvanceRegnFeeImpl.getOutOfTurnFee("OOTF", state_cd, regnType, vehClass);
                                    fee2 = AdvanceRegnFeeImpl.getOutOfTurnFee("NOCF", state_cd, regnType, vehClass);
                                    totalFee = fee1 + fee2;
                                    setTotal_amt(totalFee);
                                    reseveAmount = getTotal_amt();
                                }

                            }
                        }
                    }
                    boolean checkAmountEditable = Util.getTmConfiguration().isFancyFeeEditable();
                    if (checkAmountEditable == true) {
                        setBlnTotalAmt(false);
                    } else {
                        setBlnTotalAmt(true);
                    }
                }
            }
            if (isOnlinePayment()) {
                this.renderBtnOnBasisOfOnlinePayment(null, getRegn_no());
            }
        } catch (NumberFormatException ex) {
            e = ex;
        } catch (VahanException ve) {
            e = ve;
        } catch (Exception ee) {
            e = ee;
        }

        if (e != null) {
            showCommonChargesForAll = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage(), e.getMessage()));
            resetRegnAmount();
        }
    }

    public void applnoFocusLost(AjaxBehaviorEvent actionEvent) {

        Exception e = null;
        try {
            String state_cd = Util.getSession().getAttribute("state_cd").toString();
            int offcd = Integer.parseInt(Util.getSession().getAttribute("selected_off_cd").toString().trim());

            if ((getRegn_appl_no() == null) || getRegn_appl_no().equals("")) {
                throw new VahanException("Please Enter Application Number");

            } else {
                String error = AdvanceRegnFeeImpl.checkApplNo(getRegn_appl_no());
                if (!error.equalsIgnoreCase("")) {
                    throw new VahanException(error);
                }
            }
            Owner_dobj owner_dobj = null;
            AdvanceRegnNo_dobj dobj = AdvanceRegnFeeImpl.get_fancy_appl_no(getRegn_appl_no(), state_cd, offcd);
            if (dobj != null) {
                setOwner_name(dobj.getOwner_name());
                setF_name(dobj.getF_name());
                setC_add1(dobj.getC_add1());
                setC_add2(dobj.getC_add2());
                setC_district(dobj.getC_district());
                setC_pincode(dobj.getC_pincode());
                pmtType = dobj.getPmtType();
                regnType = dobj.getRegnType();
                vehClass = dobj.getVehClass();
                setChasisNo(dobj.getChasisNo());
                setVch_catg(dobj.getVch_catg());
                setMobile_no(dobj.getMobile_no());
                ownerCd = dobj.getOwnerCd();
                owner_dobj = ServerUtil.fillOwnerDobjFromAdvanceDobj(dobj);
            }
            if (ServerUtil.isTransport(Integer.parseInt(vehClass), null)) {
                regnType = "1";
            } else {
                regnType = "2";
            }

            boolean flag = AdvanceRegnFeeImpl.getCheckFancyConfiguration(state_cd);
            if (flag == false) {
                reservListener(regnType, vehClass);
            }
            // for transaction charges 
            commonCharges.getCommaonCharges(owner_dobj);
            if (commonCharges.getCommon_charge_list() != null) {
                showCommonChargesForAll = true;
            } else {
                showCommonChargesForAll = false;
            }
            boolean checkAmountEditable = Util.getTmConfiguration().isFancyFeeEditable();
            if (checkAmountEditable == true) {
                setBlnTotalAmt(false);
            } else {
                setBlnTotalAmt(true);
            }

            if (isOnlinePayment()) {
                this.renderBtnOnBasisOfOnlinePayment(getRegn_appl_no(), null);
            }
            // for transaction charges 
        } catch (VahanException ve) {
            e = ve;
        } catch (Exception ee) {
            e = ee;
        }

        if (e != null) {
            showCommonChargesForAll = false;
            reset();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage(), e.getMessage()));
        }
    }

    public void renderBtnOnBasisOfOnlinePayment(String regnApplNo, String regnNo) throws VahanException, Exception {
        String payApplno = new AdvanceRegnFeeImpl().getPaymentApplicationNumber(regnApplNo, regnNo);
        if (!CommonUtils.isNullOrBlank(payApplno)) {
            boolean onlineData = new FeeImpl().getOnlinePayData(payApplno);
            if (onlineData) {
                setRenderOnlinePayBtn(false);
                setRenderSaveBtn(false);
                Object[] obj = new FeeImpl().getUserIDAndPassword(payApplno);
                if (obj != null && obj.length > 0) {
                    String userInfo = "Online Payment Credentials User ID is : " + payApplno + " & Password : " + obj[0];
                    setRenderCancelPaymentBtn(true);
                    setOnlineUserCredentialmsg(userInfo);
                    setRenderUserAndPasswored(true);
                } else {
                    setRenderCancelPaymentBtn(true);
                }
            }
        }
    }

    public String saveFancyNo(String payType) {
        Exception e = null;
        long totalBal = 0;
        try {

            String errMsg = validate();
            if (errMsg != null && !"".equalsIgnoreCase(errMsg)) {
                throw new VahanException(errMsg);
            } else {
                if (getRegn_appl_no() != null && !getRegn_appl_no().equalsIgnoreCase("")) {
                    String checkApplErr = AdvanceRegnFeeImpl.checkApplNo(getRegn_appl_no());
                    if (!checkApplErr.equalsIgnoreCase("")) {
                        throw new VahanException(checkApplErr);
                    }
                }
                String state_cd = Util.getSession().getAttribute("state_cd").toString();
                int offcd = Integer.parseInt(Util.getSession().getAttribute("selected_off_cd").toString().trim());
                FeeDraftDobj feeDraftDobj = null;
                String pay_mode = getPaymentBean().getPayment_mode();
                ServerUtil.validateNoCashPayment(payType, pay_mode);

                if (payType.equalsIgnoreCase("OnlinePayment") && !getPaymentBean().getPayment_mode().equals("C")) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please select cash payment mode for online payment.", "Please select cash payment mode for online payment.");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return "";
                }
                if (!Utility.isNullOrBlank(getPaymentBean().getPayment_mode()) && (!getPaymentBean().getPayment_mode().equals("-1"))
                        && (!getPaymentBean().getPayment_mode().equals("C"))) {
                    feeDraftDobj = new FeeDraftDobj();
                    //String pay_mode = getPaymentBean().getPayment_mode();
                    feeDraftDobj.setFlag("A");
                    feeDraftDobj.setCollected_by(Util.getEmpCode());
                    feeDraftDobj.setState_cd(Util.getUserStateCode());
                    feeDraftDobj.setOff_cd(String.valueOf(Util.getUserOffCode()));
                    feeDraftDobj.setDraftPaymentList(getPaymentBean().getPaymentlist());
                    if ("SK".contains(state_cd)) {
                        for (PaymentCollectionDobj payDobj : getPaymentBean().getPaymentlist()) {
                            totalBal = totalBal + Long.parseLong(payDobj.getAmount());
                        }
                        if (totalBal < (getTotal_amt() + getUserCharge())) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Paying amount must be equal or greater then of total payable amount"));
                            return "";
                        }
                    }
                } else {
                    if ("SK".contains(state_cd)) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please select Mixed Mode for payment. Because Cash payment mode in not allowed in Sikkim"));
                        return "";
                    }
                }

                if (payType.equals("OnlinePayment")) {
                    Fee_Pay_Dobj feePayDobj = new Fee_Pay_Dobj();
                    List<FeeDobj> feeCollectionLists = new ArrayList<>();

                    FeeDobj choiceNoFee = new FeeDobj();
                    choiceNoFee.setFeeAmount((long) this.getTotal_amt());
                    choiceNoFee.setFineAmount((long) 0l);
                    choiceNoFee.setPurCd(TableConstants.VM_TRANSACTION_MAST_CHOICE_NO);
                    feeCollectionLists.add(choiceNoFee);

                    if (this.getUserCharge() > 0L) {
                        FeeDobj userChargeFee = new FeeDobj();
                        userChargeFee.setFeeAmount((long) this.getUserCharge());
                        userChargeFee.setFineAmount((long) 0l);
                        userChargeFee.setPurCd(TableConstants.VM_TRANSACTION_MAST_USER_CHARGES);
                        feeCollectionLists.add(userChargeFee);
                    }

                    if (commonCharges.getCommon_charge_list() != null && !commonCharges.getCommon_charge_list().isEmpty()) {
                        for (EpayDobj ePayDobj : commonCharges.getCommon_charge_list()) {
                            FeeDobj commonChargesFee = new FeeDobj();
                            commonChargesFee.setFeeAmount((long) ePayDobj.getE_TaxFee());
                            commonChargesFee.setFineAmount((long) 0l);
                            commonChargesFee.setPurCd(ePayDobj.getPurCd());
                            feeCollectionLists.add(commonChargesFee);
                        }
                    }

                    feePayDobj.setCollectedFeeList(feeCollectionLists);

                    long checkTotalAmount = 0L;
                    if (feePayDobj.getCollectedFeeList() != null && !feePayDobj.getCollectedFeeList().isEmpty()) {
                        for (FeeDobj dobj : feePayDobj.getCollectedFeeList()) {
                            Long totalAmount = dobj.getTotalAmount();
                            checkTotalAmount = checkTotalAmount + totalAmount;
                        }
                    }

                    OwnerDetailsDobj detailsDobj = new OwnerDetailsDobj();
                    detailsDobj.setRegn_no(this.getRegn_no().toUpperCase());
                    detailsDobj.setOwner_name(this.getOwner_name().toUpperCase());
                    detailsDobj.setF_name(this.getF_name().toUpperCase());
                    detailsDobj.setC_add1(this.getC_add1().toUpperCase());
                    detailsDobj.setC_add2(this.getC_add2().toUpperCase());
                    detailsDobj.setC_district(this.getC_district());
                    detailsDobj.setC_pincode(this.getC_pincode());
                    OwnerIdentificationDobj identityDobj = new OwnerIdentificationDobj();
                    detailsDobj.setOwnerIdentity(identityDobj);
                    detailsDobj.getOwnerIdentity().setMobile_no(this.getMobile_no());
                    if (this.getRegn_appl_no() != null) {
                        detailsDobj.setApplNo(this.getRegn_appl_no().toUpperCase());
                    }
                    if (this.getChasisNo() == null || this.getChasisNo().equalsIgnoreCase("")) {
                        detailsDobj.setChasi_no("NA");  ////////////For Chasi no i.e not available in case of advance Regn No   
                    } else {
                        detailsDobj.setChasi_no(this.getChasisNo());
                    }
                    if (vehClass != null) {
                        detailsDobj.setVh_class(Integer.parseInt(vehClass));
                    }

                    new AdvanceRegnFeeImpl().validateAdvanceRegistrationNumber(state_cd, offcd, this.getRegn_no().toUpperCase());
                    String userPwd = new FeeImpl().saveOnlinePaymentData(feePayDobj, checkTotalAmount, getRegn_appl_no(), getMobile_no(), state_cd, offcd, Util.getEmpCode(), TableConstants.ONLINE_FANCY, TableConstants.ONLINE_FANCY, detailsDobj, false, false);
                    if (userPwd != null) {
                        String userInfo = "Online Payment Credentials User ID & Password :  " + userPwd;
                        setOnlineUserCredentialmsg(userInfo);
                        PrimeFaces.current().ajax().update("frm_fancy_advance:onlinePaymentdialog");
                        PrimeFaces.current().executeScript("PF('bui_fancy_advance').show();");
                        PrimeFaces.current().executeScript("PF('onlinePaymentvar').show();");
                        setRenderUserAndPasswored(true);
                        setRenderSaveBtn(false);
                        setRenderOnlinePayBtn(false);
                        setRenderCancelPaymentBtn(true);
                    } else {
                        throw new VahanException("Error in Saving Details !!");
                    }
                } else {
                    rcpt = AdvanceRegnFeeImpl.saveFancyDetail(this, state_cd, offcd, feeDraftDobj, commonCharges.getCommon_charge_list(), pay_mode);
                    setRecp_no(rcpt);
                    setRcpt_view(true);

                    setSaved_success(true);
                    rcpt_bean.reset();
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", rcpt);
                    return "PrintCashReceiptReport";
                }
            }
        } catch (VahanException ve) {
            e = ve;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            e = ee;
        }

        if (e != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage(), e.getMessage()));
        }
        return "";
    }

    public String getCancelPayment() {
        OnlinePaymentImpl onImpl = new OnlinePaymentImpl();
        boolean flag = false;
        String password = "";
        long user_cd = 0;
        try {
            String payApplno = new AdvanceRegnFeeImpl().getPaymentApplicationNumber(getRegn_appl_no(), getRegn_no());
            Object[] obj = new FeeImpl().getUserIDAndPassword(payApplno);
            if (obj != null && obj.length > 0) {
                password = (String) obj[0];
                user_cd = (long) obj[1];
                if (CommonUtils.isNullOrBlank(onImpl.getTransactionNumber(payApplno))) {
                    flag = onImpl.getPaymentRevertBack(user_cd + "", payApplno, TableConstants.ONLINE_FANCY);
                    if (flag) {
                        setRenderSaveBtn(true);
                        setRenderOnlinePayBtn(true);
                        setRenderCancelPaymentBtn(false);
                        setRenderUserAndPasswored(false);
                    }
                } else {
                    throw new VahanException("Payment has been initiated, you can not Cancel Online Payment");
                }
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Alert !!!", e.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "";
    }

    public String validate() throws VahanException {
        String errorMessage = null;
//        if (!blnCheckAlreadyReg) {
//            if (getRegn_appl_no().equalsIgnoreCase("")) {
//                errorMessage = "Please Enter Application Number";
//            }
//        } else 
        if (getRegn_no() == null || getRegn_no().equalsIgnoreCase("")) {
            errorMessage = "Please Enter Registration Number";
        } else if (getOwner_name() == null || getOwner_name().equalsIgnoreCase("")) {
            errorMessage = "Please Enter Owner Name";
        } else if (getF_name() == null || getF_name().equalsIgnoreCase("")) {
            errorMessage = "Please Enter Father Name";
        } else if (getC_add1() == null || getC_add1().equalsIgnoreCase("")) {
            errorMessage = "Please Enter Address Properly";
        } else if (getC_add2() == null || getC_add2().equalsIgnoreCase("")) {
            errorMessage = "Please Enter Address Properly";
        } else if (getC_pincode() == 0) {
            errorMessage = "Please Enter Pincode Properly";
        } else if (getMobile_no() == 0L) {
            errorMessage = "Please Enter Mobile Number Properly";
        } else if (getPaymentBean().getPayment_mode().equals("-1")) {
            errorMessage = "Please Select Payment Mode";
        } else if (!Util.getUserStateCode().equalsIgnoreCase("ML") && getTotal_amt() <= 0L) {
            errorMessage = "Payment Amount can not be zero.";
        } else if (getTotal_amt() < reseveAmount) {
            errorMessage = "Payment Amount can not be less than Reserve Amount.";
        }
        return errorMessage;

    }

    public void validateField(String fldName, String fieldValue, StringBuffer bf) {
        if (fieldValue == null || fieldValue.trim().equals("")) {
            bf.append("Empty " + fldName);
            bf.append("\n");
        }
    }

    public void reset() {
        setRegn_no("");
        setRegn_appl_no("");
        setOwner_name("");
        setF_name("");
        setMobile_no(0L);
        setC_pincode(0);
        setC_add1("");
        setC_add2("");
        setRecp_no("");
        rcpt_bean.reset();
        paymentBean.reset();
        setTotal_amt(0);
        try {
            boolean checkAmountEditable = Util.getTmConfiguration().isFancyFeeEditable();
            if (checkAmountEditable) {
                setBlnTotalAmt(false);
            } else {
                setBlnTotalAmt(true);
            }
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
        }
    }

    public void resetRegnAmount() {
        setRegn_no("");
        setTotal_amt(0);
    }

    public static String sanitizationRegnNo(String seriPart, String numPart) throws VahanException {
        String finalNumber = "";
        try {
            int numberPart = Integer.parseInt(numPart);
            if (numberPart > 0) {
                numPart = sanitizationNumberPart(numPart.trim());
                if (seriPart.trim().length() == 6) {
                    finalNumber = seriPart + String.valueOf(numPart);
                } else if (seriPart.trim().length() == 5) {
                    finalNumber = seriPart + "" + String.valueOf(numPart);
                } else if (seriPart.trim().length() == 4) {
                    finalNumber = seriPart + "" + String.valueOf(numPart);
                } else if (seriPart.trim().length() == 3) {
                    finalNumber = seriPart + "" + String.valueOf(numPart);
                }
            } else {
                throw new VahanException("Please Enter Proper Last Four digit Registration Number [" + numPart + "]");
            }
        } catch (Exception e) {
            throw new VahanException("Please Enter Proper Last Four digit Registration Number [" + numPart + "]");
        }
        return finalNumber;
    }

    public static String sanitizationNumberPart(String numberPart) {
        String finalNumber = "";
        if (numberPart.trim().length() == 4) {
            finalNumber = numberPart;
        } else if (numberPart.trim().length() == 3) {
            finalNumber = "0" + String.valueOf(numberPart);
        } else if (numberPart.trim().length() == 2) {
            finalNumber = "00" + String.valueOf(numberPart);
        } else if (numberPart.trim().length() == 1) {
            finalNumber = "000" + String.valueOf(numberPart);
        }
        return finalNumber;
    }

    public String printReceipt() {
        if (getRecp_no() == null) {
            setRecp_no(rcpt_bean.getBook_no() + (rcpt_bean.getCurrent_rcpt_no() - 1));
        } else {
            setRecp_no(rcpt);
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", getRecp_no());
        return "PrintCashReceiptReport";
    }

    public String reloadPage() {
        return null;
    }

    public void reservListener(String regnType, String vehClass) {
        setAvailReservedNumbers(AdvanceRegnFeeImpl.getFancyNumbers(Util.getUserStateCode(), regnType, vehClass));
    }

    public boolean isBal_rcpt_view() {
        return bal_rcpt_view;
    }

    public void setBal_rcpt_view(boolean bal_rcpt_view) {
        this.bal_rcpt_view = bal_rcpt_view;
    }

    public boolean isSaved_success() {
        return saved_success;
    }

    public void setSaved_success(boolean saved_success) {
        this.saved_success = saved_success;
    }

    public void setSuucessMessageFalse() {
        this.saved_success = false;
    }

    public boolean isRcpt_view() {
        return rcpt_view;
    }

    public void setRcpt_view(boolean rcpt_view) {
        this.rcpt_view = rcpt_view;
    }

    public String getRb_select_one() {
        return rb_select_one;
    }

    public void setRb_select_one(String rb_select_one) {
        this.rb_select_one = rb_select_one;
    }

    public ArrayList getListC_village() {
        return listC_village;
    }

    public void setListC_village(ArrayList listC_village) {
        this.listC_village = listC_village;
    }

    public ArrayList getListC_taluk() {
        return listC_taluk;
    }

    public void setListC_taluk(ArrayList listC_taluk) {
        this.listC_taluk = listC_taluk;
    }

    public ArrayList getListC_dist() {
        return listC_dist;
    }

    public void setListC_dist(ArrayList listC_dist) {
        this.listC_dist = listC_dist;
    }

    /**
     * @return the SELECT_LABEL1
     */
    public String getSELECT_LABEL1() {
        return SELECT_LABEL1;
    }

    /**
     * @param SELECT_LABEL1 the SELECT_LABEL1 to set
     */
    public void setSELECT_LABEL1(String SELECT_LABEL1) {
        this.SELECT_LABEL1 = SELECT_LABEL1;
    }

    /**
     * @return the SELECT_LABEL2
     */
    public String getSELECT_LABEL2() {
        return SELECT_LABEL2;
    }

    /**
     * @param SELECT_LABEL2 the SELECT_LABEL2 to set
     */
    public void setSELECT_LABEL2(String SELECT_LABEL2) {
        this.SELECT_LABEL2 = SELECT_LABEL2;
    }

    /**
     * @return the SELECT_LABEL3
     */
    public String getSELECT_LABEL3() {
        return SELECT_LABEL3;
    }

    /**
     * @param SELECT_LABEL3 the SELECT_LABEL3 to set
     */
    public void setSELECT_LABEL3(String SELECT_LABEL3) {
        this.SELECT_LABEL3 = SELECT_LABEL3;
    }

    /**
     * @return the SELECT_VALUE1
     */
    public String getSELECT_VALUE1() {
        return SELECT_VALUE1;
    }

    /**
     * @param SELECT_VALUE1 the SELECT_VALUE1 to set
     */
    public void setSELECT_VALUE1(String SELECT_VALUE1) {
        this.SELECT_VALUE1 = SELECT_VALUE1;
    }

    /**
     * @return the SELECT_VALUE2
     */
    public String getSELECT_VALUE2() {
        return SELECT_VALUE2;
    }

    /**
     * @param SELECT_VALUE2 the SELECT_VALUE2 to set
     */
    public void setSELECT_VALUE2(String SELECT_VALUE2) {
        this.SELECT_VALUE2 = SELECT_VALUE2;
    }

    /**
     * @return the SELECT_VALUE3
     */
    public String getSELECT_VALUE3() {
        return SELECT_VALUE3;
    }

    /**
     * @param SELECT_VALUE3 the SELECT_VALUE3 to set
     */
    public void setSELECT_VALUE3(String SELECT_VALUE3) {
        this.SELECT_VALUE3 = SELECT_VALUE3;
    }

    public ReceiptMasterBean getRcpt_bean() {
        return rcpt_bean;
    }

    public void setRcpt_bean(ReceiptMasterBean rcpt_bean) {
        this.rcpt_bean = rcpt_bean;
    }

    /**
     * @return the blnAlreadyReg
     */
    public boolean isBlnAlreadyReg() {
        return blnAlreadyReg;
    }

    /**
     * @param blnAlreadyReg the blnAlreadyReg to set
     */
    public void setBlnAlreadyReg(boolean blnAlreadyReg) {
        this.blnAlreadyReg = blnAlreadyReg;
    }

    /**
     * @return the blnCheckAlreadyReg
     */
    public boolean isBlnCheckAlreadyReg() {
        return blnCheckAlreadyReg;
    }

    /**
     * @param blnCheckAlreadyReg the blnCheckAlreadyReg to set
     */
    public void setBlnCheckAlreadyReg(boolean blnCheckAlreadyReg) {
        this.blnCheckAlreadyReg = blnCheckAlreadyReg;
    }

    /**
     * @return the paymentBean
     */
    public PaymentCollectionBean getPaymentBean() {
        return paymentBean;
    }

    /**
     * @param paymentBean the paymentBean to set
     */
    public void setPaymentBean(PaymentCollectionBean paymentBean) {
        this.paymentBean = paymentBean;
    }

    /**
     * @return the blnAlreadyRecp
     */
    public boolean isBlnAlreadyRecp() {
        return blnAlreadyRecp;
    }

    /**
     * @param blnAlreadyRecp the blnAlreadyRecp to set
     */
    public void setBlnAlreadyRecp(boolean blnAlreadyRecp) {
        this.blnAlreadyRecp = blnAlreadyRecp;
    }

    /**
     * @return the prntRecieptSubList
     */
    public List<PrintRcptParticularDtls> getPrntRecieptSubList() {
        return prntRecieptSubList;
    }

    /**
     * @param prntRecieptSubList the prntRecieptSubList to set
     */
    public void setPrntRecieptSubList(List<PrintRcptParticularDtls> prntRecieptSubList) {
        this.prntRecieptSubList = prntRecieptSubList;
    }

    /**
     * @return the statename
     */
    public String getStatename() {
        return statename;
    }

    /**
     * @param statename the statename to set
     */
    public void setStatename(String statename) {
        this.statename = statename;
    }

    /**
     * @return the rtooffice
     */
    public String getRtooffice() {
        return rtooffice;
    }

    /**
     * @param rtooffice the rtooffice to set
     */
    public void setRtooffice(String rtooffice) {
        this.rtooffice = rtooffice;
    }

    /**
     * @return the receiptno
     */
    public String getReceiptno() {
        return receiptno;
    }

    /**
     * @param receiptno the receiptno to set
     */
    public void setReceiptno(String receiptno) {
        this.receiptno = receiptno;
    }

    /**
     * @return the vhowner
     */
    public String getVhowner() {
        return vhowner;
    }

    /**
     * @param vhowner the vhowner to set
     */
    public void setVhowner(String vhowner) {
        this.vhowner = vhowner;
    }

    /**
     * @return the regnno
     */
    public String getRegnno() {
        return regnno;
    }

    /**
     * @param regnno the regnno to set
     */
    public void setRegnno(String regnno) {
        this.regnno = regnno;
    }

    /**
     * @return the vhclass
     */
    public String getVhclass() {
        return vhclass;
    }

    /**
     * @param vhclass the vhclass to set
     */
    public void setVhclass(String vhclass) {
        this.vhclass = vhclass;
    }

    /**
     * @return the receiptdate
     */
    public String getReceiptdate() {
        return receiptdate;
    }

    /**
     * @param receiptdate the receiptdate to set
     */
    public void setReceiptdate(String receiptdate) {
        this.receiptdate = receiptdate;
    }

    /**
     * @return the chasino
     */
    public String getChasino() {
        return chasino;
    }

    /**
     * @param chasino the chasino to set
     */
    public void setChasino(String chasino) {
        this.chasino = chasino;
    }

    /**
     * @return the blnchkAdvanceRegn
     */
    public boolean isBlnchkAdvanceRegn() {
        return blnchkAdvanceRegn;
    }

    /**
     * @param blnchkAdvanceRegn the blnchkAdvanceRegn to set
     */
    public void setBlnchkAdvanceRegn(boolean blnchkAdvanceRegn) {
        this.blnchkAdvanceRegn = blnchkAdvanceRegn;
    }

    public boolean isShowCommonChargesForAll() {
        return showCommonChargesForAll;
    }

    public void setShowCommonChargesForAll(boolean showCommonChargesForAll) {
        this.showCommonChargesForAll = showCommonChargesForAll;
    }

    public CommonChargesForAllBean getCommonCharges() {
        return commonCharges;
    }

    public void setCommonCharges(CommonChargesForAllBean commonCharges) {
        this.commonCharges = commonCharges;
    }

    public long getCommonChargesAmt() {
        return commonChargesAmt;
    }

    public void setCommonChargesAmt(long commonChargesAmt) {
        this.commonChargesAmt = commonChargesAmt;
    }

    /**
     * @return the blnTotalAmt
     */
    public boolean isBlnTotalAmt() {
        return blnTotalAmt;
    }

    /**
     * @param blnTotalAmt the blnTotalAmt to set
     */
    public void setBlnTotalAmt(boolean blnTotalAmt) {
        this.blnTotalAmt = blnTotalAmt;
    }

    /**
     * @return the onlineUserCredentialmsg
     */
    public String getOnlineUserCredentialmsg() {
        return onlineUserCredentialmsg;
    }

    /**
     * @param onlineUserCredentialmsg the onlineUserCredentialmsg to set
     */
    public void setOnlineUserCredentialmsg(String onlineUserCredentialmsg) {
        this.onlineUserCredentialmsg = onlineUserCredentialmsg;
    }

    /**
     * @return the renderOnlinePayBtn
     */
    public boolean isRenderOnlinePayBtn() {
        return renderOnlinePayBtn;
    }

    /**
     * @param renderOnlinePayBtn the renderOnlinePayBtn to set
     */
    public void setRenderOnlinePayBtn(boolean renderOnlinePayBtn) {
        this.renderOnlinePayBtn = renderOnlinePayBtn;
    }

    /**
     * @return the renderSaveBtn
     */
    public boolean isRenderSaveBtn() {
        return renderSaveBtn;
    }

    /**
     * @param renderSaveBtn the renderSaveBtn to set
     */
    public void setRenderSaveBtn(boolean renderSaveBtn) {
        this.renderSaveBtn = renderSaveBtn;
    }

    /**
     * @return the renderCancelPaymentBtn
     */
    public boolean isRenderCancelPaymentBtn() {
        return renderCancelPaymentBtn;
    }

    /**
     * @param renderCancelPaymentBtn the renderCancelPaymentBtn to set
     */
    public void setRenderCancelPaymentBtn(boolean renderCancelPaymentBtn) {
        this.renderCancelPaymentBtn = renderCancelPaymentBtn;
    }

    /**
     * @return the renderUserAndPasswored
     */
    public boolean isRenderUserAndPasswored() {
        return renderUserAndPasswored;
    }

    /**
     * @param renderUserAndPasswored the renderUserAndPasswored to set
     */
    public void setRenderUserAndPasswored(boolean renderUserAndPasswored) {
        this.renderUserAndPasswored = renderUserAndPasswored;
    }

    /**
     * @return the onlinePayment
     */
    public boolean isOnlinePayment() {
        return onlinePayment;
    }

    /**
     * @param onlinePayment the onlinePayment to set
     */
    public void setOnlinePayment(boolean onlinePayment) {
        this.onlinePayment = onlinePayment;
    }
}
