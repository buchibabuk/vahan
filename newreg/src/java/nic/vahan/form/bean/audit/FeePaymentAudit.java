/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.audit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.java.util.CommonUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.eapplication.OnlinePaymentImpl;
import nic.vahan.form.bean.FormFeePanelBean;
import nic.vahan.form.bean.PaymentCollectionBean;
import nic.vahan.form.dobj.AuditRecoveryDobj;
import nic.vahan.form.dobj.FeeDobj;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.Fee_Pay_Dobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.Trailer_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.audit.EntryAuditRecoveryImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 *
 * @author Kunal Maiti
 */
@ManagedBean(name = "feePaymentAudit")
@ViewScoped
public class FeePaymentAudit implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(FeePaymentAudit.class);
    private String regn_no;
    private boolean showRegList = false;
    private boolean showFeePaymentDetails = false;
    private boolean showFeeDetails = false;
//    private ArrayList<OwnerDetailsDobj> regnNameList = null;
    private List<AuditRecoveryDobj> auditRecoveryList = new ArrayList<>();
//    private List listOwnerCatg = new ArrayList();
    private OwnerDetailsDobj ownerDetail;
    private String paraNo;
    private String year;
    private int amount;
    private String objection;
    private Date periodFrom;
    private Date periodTo;
    private String paymentMode;
    private EntryAuditRecoveryImpl entryAuditRecoveryImpl = new EntryAuditRecoveryImpl();
    private AuditRecoveryDobj selectedauditRecovery;
    private boolean regnNoDisabled = false;
    private boolean showDtlsBtnDisabled = false;
    private PaymentCollectionBean paymentBean = new PaymentCollectionBean();
    private FormFeePanelBean feePanelBean = new FormFeePanelBean();
    private List<FeeDobj> feeCollectionLists = null;
    private String onlineUserCredentialmsg = "";
    private boolean renderOnlinePayBtn = false;
    private boolean renderSaveBtn = true;
    private boolean renderCancelPaymentBtn = false;
    private boolean renderUserAndPasswored = false;
    private boolean onlinePayment = false;

    @PostConstruct
    public void Init() {
        try {
//            fillmastercombo();
//            disabledatatablepanels();
            TmConfigurationDobj tmConf = Util.getTmConfiguration();
            String user_catg = ServerUtil.getUserCategory(Long.parseLong(Util.getEmpCode()));
            boolean checkUserPerm = false;
            if (user_catg.equals("S") || (user_catg.equals("A"))) {
                checkUserPerm = true;
            }
            if (checkUserPerm == false) {
//                combofilllist.clear();
                JSFUtils.showMessage("YOU ARE NOT ALLOWED TO ENTER DATA IN MASTER FORM");
            }
            if (tmConf != null) {
                if (tmConf.isOnlinePayment()) {
                    setOnlinePayment(true);
                    setRenderOnlinePayBtn(true);
                    setRenderSaveBtn(true);
                    setRenderCancelPaymentBtn(false);
                    setRenderUserAndPasswored(false);
                }
            }
        } catch (VahanException ve) {
            JSFUtils.showMessagesInDialog("Alert !!!", ve.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void reset() {
        regnNoDisabled = false;
        showDtlsBtnDisabled = false;
        showFeePaymentDetails = false;
        showFeeDetails = false;
    }

//    
//    public String saveFeeDetails() throws VahanException {
//        Exception e = null;
//        Fee_Pay_Dobj feePayDobj = new Fee_Pay_Dobj();
//        feePayDobj.setCollectedFeeList(getFeeCollectionLists());
//
//        // For Tax Collection Information
//        if (renderTaxPanel) {
//            if (listTaxForm.size() > 0) {
//                List<Tax_Pay_Dobj> listTaxDobj = new ArrayList<>();
//
//                for (TaxFormPanelBean bean : listTaxForm) {
//                    if (!bean.getTaxMode().equals("0")) {
//                        Tax_Pay_Dobj taxDobj = new Tax_Pay_Dobj();
//
//                        taxDobj.setFinalTaxAmount(bean.getFinalTaxAmount());
//                        taxDobj.setTotalPaybaleTax(bean.getTotalPaybaleTax());
//                        taxDobj.setTotalPaybalePenalty(bean.getTotalPaybalePenalty());
//                        taxDobj.setTotalPaybaleSurcharge(bean.getTotalPaybaleSurcharge());
//                        taxDobj.setTotalPaybaleRebate(bean.getTotalPaybaleRebate());
//                        taxDobj.setTotalPaybaleInterest(bean.getTotalPaybaleInterest());
//                        taxDobj.setFinalTaxFrom(bean.getFinalTaxFrom());
//                        taxDobj.setFinalTaxUpto(bean.getFinalTaxUpto());
//                        taxDobj.setTaxMode(bean.getTaxMode());
//                        taxDobj.setPur_cd(bean.getPur_cd());
//                        taxDobj.setRegnNo(ownerDobj.getRegn_no());
//                        taxDobj.setPaymentMode(getPaymentBean().getPayment_mode());
//                        taxDobj.setTaxBreakDetails(bean.getTaxDescriptionList());
//                        if (ownerDobj != null) {
//                            taxDobj.setRegnNo(getOwnerDobj().getRegn_no());
//                            taxDobj.setApplNo(getAppl_no());
//                        }
//
//                        taxDobj.setDeal_cd(Util.getEmpCode());
//                        taxDobj.setOff_cd(Util.getSelectedSeat().getOff_cd());
//
//                        taxDobj.setOp_dt(new java.util.Date());
//                        taxDobj.setRcptDate(new java.util.Date());
//                        taxDobj.setApplNo(appl_no);
//                        listTaxDobj.add(taxDobj);
//
//                    }
//
//                }
//                feePayDobj.setListTaxDobj(listTaxDobj);
//                feePayDobj.setPermitDobj(getPermitPanelBean().getPermitDobj());
//            }
//
//        }
//
//        feePayDobj.setOp_dt(new java.util.Date());
//        if (ownerDobj == null) {
//            return "";
//        }
//        feePayDobj.setRegnNo(ownerDobj.getRegn_no());
//        feePayDobj.setApplNo(getAppl_no());
//        feePayDobj.setRcptDt(new java.util.Date());
//        feePayDobj.setPaymentMode(getPaymentBean().getPayment_mode());
//        feePayDobj.setState_cd(ownerDobj.getState_cd());
//        try {
//
//            FeeDraftDobj feeDraftDobj = null;
//
//            if (!Utility.isNullOrBlank(getPaymentBean().getPayment_mode()) && (!getPaymentBean().getPayment_mode().equals("-1"))
//                    && (!getPaymentBean().getPayment_mode().equals("C"))) {
//                feeDraftDobj = new FeeDraftDobj();
//                String pay_mode = getPaymentBean().getPayment_mode();
//                feeDraftDobj.setAppl_no(getAppl_no());
//                feeDraftDobj.setFlag("A");
//                feeDraftDobj.setCollected_by(Util.getEmpCode());
//                feeDraftDobj.setState_cd(getOwnerDobj().getState_cd());
//                feeDraftDobj.setOff_cd(String.valueOf(Util.getUserOffCode()));
//                feeDraftDobj.setDraftPaymentList(getPaymentBean().getPaymentlist());
//
//            }
//            feePayDobj.setOwnerDobj(ownerDobj);
//            VehicleParameters vehParameters = fillVehicleParametersFromDobj(ownerDobj);
//            List<FeeDobj> feeDobjList = getFeeCollectionLists();
//            if (feeDobjList != null) {
//                for (FeeDobj feedobj : feeDobjList) {
//                    if (feedobj.getPurCd() == 99 || feedobj.getPurCd() == 80) {
//                        continue;
//                    }
//                    Long totalAmount = feedobj.getTotalAmount();
//                    int pur_cd = feedobj.getPurCd();
//                    vehParameters.setPUR_CD(pur_cd);
//                    if ((pur_cd != TableConstants.VM_TRANSACTION_MAST_NOC && !isCondition(replaceTagValues(tmConfDobj.getFee_amt_zero(), vehParameters), "saveFeeDetails-1")
//                            && (totalAmount == null || totalAmount == 0L)) || pur_cd <= 0) {
//                        throw new VahanException("Record cannot be saved with zero value for " + ServerUtil.getTaxHead(feedobj.getPurCd()));
//                    }
//                }
//            }
//
//            List<Tax_Pay_Dobj> taxDobjList = feePayDobj.getListTaxDobj();
//            if (taxDobjList != null) {
//                for (Tax_Pay_Dobj taxdobj : taxDobjList) {
//                    int pur_cd = taxdobj.getPur_cd();
//                    vehParameters.setPUR_CD(pur_cd);
//                    Long totalAmount = taxdobj.getFinalTaxAmount();
//                    if (totalAmount <= 0) {
//                        if (!isCondition(replaceTagValues(tmConfDobj.getFee_amt_zero(), vehParameters), "saveFeeDetails-2")
//                                && taxdobj.getTotalPaybaleTax() <= 0 || taxdobj.getFinalTaxAmount() < 0) {
//                            throw new VahanException("Record cannot be saved with zero value for " + ServerUtil.getTaxHead(taxdobj.getPur_cd()));
//                        }
//                    }
//
//                }
//            }
//
//            List<FeeDobj> feeCollectionCloneLists = new ArrayList<>(getFeeCollectionLists());
//            if (isRenderUserChargesAmountPanel()) {
//                FeeDobj userCharge = new FeeDobj();
//                userCharge.setFeeAmount((long) getTotalUserChrg());
//                userCharge.setFineAmount((long) 0l);
//                userCharge.setPurCd(99);
//                getFeePanelBean().getFeeCollectionList().add(userCharge);
//                feeCollectionCloneLists.add(userCharge);
//            }
//            if (isRenderSmartCardFeePanel()) {
//                FeeDobj smtCrdCharge = new FeeDobj();
//                smtCrdCharge.setFeeAmount((long) getSmartCardFee());
//                smtCrdCharge.setFineAmount((long) 0l);
//                smtCrdCharge.setPurCd(80);
//                getFeePanelBean().getFeeCollectionList().add(smtCrdCharge);
//                feeCollectionCloneLists.add(smtCrdCharge);
//            }
//            if (isTaxInstallment()) {
//                feePayDobj.setTaxInstallment(taxInstallment);
//                feePayDobj.setTaxInstallMode(convdobj.getNewTaxMode());
//            }
//            feePayDobj.setCollectedFeeList(feeCollectionCloneLists);
//            String[] applNo = feeImpl.saveFeeDetailsInstrument(feePayDobj, feeDraftDobj);//Details(feePayDobj);
//
//            if (applNo == null) {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Fee Can't be Submitted because One of the Transaction is Pending on Data Entry Level.", "Fee Can't be Submitted because One of the Transaction is Pending on Data Entry Level."));
//                return null;
//            }
//
//            generatedRcpt = applNo[1];
//
//            if (!Utility.isNullOrBlank(generatedRcpt)) {
//                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful Transaction!", "Receipt Number: " + applNo[1]));
//                getFeePanelBean().setReadOnlyFineAmount(true);
//                setRegnno(getOwnerDobj().getRegn_no());
//                reset();
//                setTotalUserChrg(0l);
//                setSmartCardFee(0l);
//                setTotalAmountPayable(0);
//                Util.getSession().removeAttribute("seat_map");
//                RequestContext rc = RequestContext.getCurrentInstance();
//                rc.update("form_registered_vehicle_fee:pg_button");
//                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", generatedRcpt);
//                return "PrintCashReceiptReport";
//            }
//            rcpt_bean.reset();
//        } catch (VahanException ve) {
//            e = ve;
//        } catch (Exception ee) {
//            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
//        }
//        if (e != null) {
//            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
//        }
//        return "";
//    }
    public String saveDetails(String payType) {
        Exception e = null;
        FeeImpl feeImpl = new FeeImpl();
        int pur_cd = TableConstants.VM_FEE_PAYMENT_AUDIT;
        FeeDraftDobj feeDraftDobj = null;
        int amt = 0;
        try {
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

//                feeDraftDobj.setAppl_no(getAppl_no());
                feeDraftDobj.setFlag("A");
                feeDraftDobj.setCollected_by(Util.getEmpCode());
                feeDraftDobj.setState_cd(selectedauditRecovery.getState_cd());
                feeDraftDobj.setOff_cd(String.valueOf(Util.getUserOffCode()));
                feeDraftDobj.setDraftPaymentList(getPaymentBean().getPaymentlist());
                List<PaymentCollectionDobj> list = getPaymentBean().getPaymentlist();
                Iterator<PaymentCollectionDobj> itr = list.iterator();

                while (itr.hasNext()) {
                    PaymentCollectionDobj paymentCollectionDobj = itr.next();
                    amt = amt + Integer.parseInt(paymentCollectionDobj.getAmount());
                }
                if (amt != getAmount()) {
                    JSFUtils.showMessage("Amount mismatch");
                    return null;
                }
            }

//            appl_no = getUniqueApplNo(tmgr, state_cd);
//            vp_appl_rcpt
            selectedauditRecovery.setPay_dt(new Date());
            selectedauditRecovery.setPaid_op_date(new Date());
            selectedauditRecovery.setPaid_deal_cd(Util.getEmpCode());
            selectedauditRecovery.setPay_amt(amt);
            if (payType.equals("OnlinePayment")) {
                Fee_Pay_Dobj feePayDobj = new Fee_Pay_Dobj();
                List<FeeDobj> feeCollectionList = new ArrayList<>();

                FeeDobj auditFee = new FeeDobj();
                auditFee.setFeeAmount((long) selectedauditRecovery.getAmount());
                auditFee.setFineAmount((long) 0l);
                auditFee.setPurCd(pur_cd);
                feeCollectionList.add(auditFee);

                feePayDobj.setCollectedFeeList(feeCollectionList);

                long checkTotalAmount = 0L;
                if (feePayDobj.getCollectedFeeList() != null && !feePayDobj.getCollectedFeeList().isEmpty()) {
                    for (FeeDobj dobj : feePayDobj.getCollectedFeeList()) {
                        Long totalAmount = dobj.getTotalAmount();
                        checkTotalAmount = checkTotalAmount + totalAmount;
                    }
                }
                OwnerDetailsDobj owndobj = entryAuditRecoveryImpl.getOwnerDetails(selectedauditRecovery.getRegn_no());
                String userPwd = feeImpl.saveOnlinePaymentData(feePayDobj, checkTotalAmount, selectedauditRecovery.getApplNo(), owndobj.getOwnerIdentity().getMobile_no(), selectedauditRecovery.getState_cd(), selectedauditRecovery.getOff_cd(), Util.getEmpCode(), TableConstants.ONLINE_AUDIT, TableConstants.ONLINE_AUDIT, owndobj, false, false);
                if (userPwd != null) {
                    String userInfo = "Online Payment Credentials User ID : " + selectedauditRecovery.getApplNo() + " & Password :  " + userPwd;
                    setOnlineUserCredentialmsg(userInfo);
                    PrimeFaces.current().ajax().update("onlinePaymentdialog");
                    PrimeFaces.current().executeScript("PF('ajax_status').show();");
                    PrimeFaces.current().executeScript("PF('onlinePaymentvar').show();");
                } else {
                    throw new VahanException("Error in Saving Details !!");
                }
            } else {
                String val = EntryAuditRecoveryImpl.saveFeePaymentDtls(selectedauditRecovery, getAmount(), pur_cd, pay_mode, feeDraftDobj);
                if (!CommonUtils.isNullOrBlank(val) && !"false".equals(val)) {
                    boolean status = EntryAuditRecoveryImpl.insertFromVA_TOVT_AUDIT_RECOVERY(selectedauditRecovery);
                    if (status) {
                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", val);
                    } else {
                        val = "false";
                    }
                }
                if (!"false".equals(val)) {
                    reset();
                    JSFUtils.showMessage("Fee Payment details saved successfully");
                } else {
                    JSFUtils.showMessage("Fee Payment details saved fail");
                }

                return "PrintCashReceiptReport";
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
            Object[] obj = new FeeImpl().getUserIDAndPassword(selectedauditRecovery.getApplNo());
            if (obj != null && obj.length > 0) {
                password = (String) obj[0];
                user_cd = (long) obj[1];
                if (nic.vahan.server.CommonUtils.isNullOrBlank(onImpl.getTransactionNumber(selectedauditRecovery.getApplNo()))) {
                    flag = onImpl.getPaymentRevertBack(user_cd + "", selectedauditRecovery.getApplNo(), null);
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
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "";
    }

    public void onRowSelect(SelectEvent event) {
        try {
            showFeeDetails = true;
            setAmount(selectedauditRecovery.getAmount());
            renderBtnOnBasisOfOnlinePayment(selectedauditRecovery.getApplNo());
//       System.out.println("selectedauditRecovery.getPara_no()::"+selectedauditRecovery.getPara_no());
//       System.out.println("selectedauditRecovery.getRegnNO::"+selectedauditRecovery.getRegn_no());
//        FacesMessage msg = new FacesMessage("Selected", ((AuditRecoveryDobj) event.getObject()).getPara_no());
//        FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (VahanException ve) {
            JSFUtils.showMessagesInDialog("Alert !!!", ve.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!!", TableConstants.SomthingWentWrong, FacesMessage.SEVERITY_WARN);
        }
    }

    public void onRowUnselect(UnselectEvent event) {
        try {
            showFeeDetails = false;
            setAmount(0);
            renderBtnOnBasisOfOnlinePayment(selectedauditRecovery.getApplNo());
//          System.out.println("selectedauditRecovery.getPara_no()::"+selectedauditRecovery.getPara_no());
//       System.out.println("selectedauditRecovery.getRegnNO::"+selectedauditRecovery.getRegn_no());
//        FacesMessage msg = new FacesMessage("Unselected", ((AuditRecoveryDobj) event.getObject()).getPara_no());
//        FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (VahanException ve) {
            JSFUtils.showMessagesInDialog("Alert !!!", ve.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!!", TableConstants.SomthingWentWrong, FacesMessage.SEVERITY_WARN);
        }
    }

    public void renderBtnOnBasisOfOnlinePayment(String applNo) throws VahanException, Exception {
        TmConfigurationDobj tmConf = Util.getTmConfiguration();
        if (tmConf.isOnlinePayment()) {
            boolean onlineData = new FeeImpl().getOnlinePayData(applNo);
            if (onlineData) {
                setRenderSaveBtn(false);
                setRenderOnlinePayBtn(false);
                Object[] obj = new FeeImpl().getUserIDAndPassword(applNo);
                if (obj != null && obj.length > 0) {
                    String userInfo = "Online Payment Credentials User ID is : " + applNo + " & Password : " + obj[0];
                    setRenderCancelPaymentBtn(true);
                    setOnlineUserCredentialmsg(userInfo);
                    setRenderUserAndPasswored(true);
                } else {
                    setRenderCancelPaymentBtn(true);
                }
            } else {
                setRenderSaveBtn(true);
                setRenderOnlinePayBtn(true);
                setOnlineUserCredentialmsg("");
                setRenderUserAndPasswored(false);
                setRenderCancelPaymentBtn(false);
            }
        }
    }

    public void showDetails() {
        Exception ex = null;
        Trailer_Impl trailer_Impl = new Trailer_Impl();

        if (this.regn_no.trim() == null || this.regn_no.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Please Enter Valid Registration Number"));
            return;
        }
        try {
            //Master Filler for Owner Category
            setAuditRecoveryList(entryAuditRecoveryImpl.getvtAuditRecovery(this.regn_no.trim()));
            if (getAuditRecoveryList().size() > 0) {
                setShowFeePaymentDetails(true);
                regnNoDisabled = true;
                showDtlsBtnDisabled = true;
            } else {
                JSFUtils.showMessage("Fees is Already Paid");
            }
        } catch (Exception exp) {
            ex = exp;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", TableConstants.SomthingWentWrong));
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
     * @return the showRegList
     */
    public boolean isShowRegList() {
        return showRegList;
    }

    /**
     * @param showRegList the showRegList to set
     */
    public void setShowRegList(boolean showRegList) {
        this.showRegList = showRegList;
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
     * @return the paraNo
     */
    public String getParaNo() {
        return paraNo;
    }

    /**
     * @param paraNo the paraNo to set
     */
    public void setParaNo(String paraNo) {
        this.paraNo = paraNo;
    }

    /**
     * @return the periodFrom
     */
    public Date getPeriodFrom() {
        return periodFrom;
    }

    /**
     * @param periodFrom the periodFrom to set
     */
    public void setPeriodFrom(Date periodFrom) {
        this.periodFrom = periodFrom;
    }

    /**
     * @return the periodTo
     */
    public Date getPeriodTo() {
        return periodTo;
    }

    /**
     * @param periodTo the periodTo to set
     */
    public void setPeriodTo(Date periodTo) {
        this.periodTo = periodTo;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * @return the objection
     */
    public String getObjection() {
        return objection;
    }

    /**
     * @param objection the objection to set
     */
    public void setObjection(String objection) {
        this.objection = objection;
    }

    /**
     * @return the year
     */
    public String getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * @return the regnNoDisabled
     */
    public boolean isRegnNoDisabled() {
        return regnNoDisabled;
    }

    /**
     * @param regnNoDisabled the regnNoDisabled to set
     */
    public void setRegnNoDisabled(boolean regnNoDisabled) {
        this.regnNoDisabled = regnNoDisabled;
    }

    /**
     * @return the showDtlsBtnDisabled
     */
    public boolean isShowDtlsBtnDisabled() {
        return showDtlsBtnDisabled;
    }

    /**
     * @param showDtlsBtnDisabled the showDtlsBtnDisabled to set
     */
    public void setShowDtlsBtnDisabled(boolean showDtlsBtnDisabled) {
        this.showDtlsBtnDisabled = showDtlsBtnDisabled;
    }

    /**
     * @return the auditRecoveryList
     */
    public List<AuditRecoveryDobj> getAuditRecoveryList() {
        return auditRecoveryList;
    }

    /**
     * @param auditRecoveryList the auditRecoveryList to set
     */
    public void setAuditRecoveryList(List<AuditRecoveryDobj> auditRecoveryList) {
        this.auditRecoveryList = auditRecoveryList;
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

    /**
     * @return the feeCollectionLists
     */
    public List<FeeDobj> getFeeCollectionLists() {
        return feeCollectionLists;
    }

    /**
     * @param feeCollectionLists the feeCollectionLists to set
     */
    public void setFeeCollectionLists(List<FeeDobj> feeCollectionLists) {
        this.feeCollectionLists = feeCollectionLists;
    }

    /**
     * @return the feePanelBean
     */
    public FormFeePanelBean getFeePanelBean() {
        return feePanelBean;
    }

    /**
     * @param feePanelBean the feePanelBean to set
     */
    public void setFeePanelBean(FormFeePanelBean feePanelBean) {
        this.feePanelBean = feePanelBean;
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
     * @return the paymentMode
     */
    public String getPaymentMode() {
        return paymentMode;
    }

    /**
     * @param paymentMode the paymentMode to set
     */
    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    /**
     * @return the showFeeDetails
     */
    public boolean isShowFeeDetails() {
        return showFeeDetails;
    }

    /**
     * @param showFeeDetails the showFeeDetails to set
     */
    public void setShowFeeDetails(boolean showFeeDetails) {
        this.showFeeDetails = showFeeDetails;
    }

    /**
     * @return the selectedauditRecovery
     */
    public AuditRecoveryDobj getSelectedauditRecovery() {
        return selectedauditRecovery;
    }

    /**
     * @param selectedauditRecovery the selectedauditRecovery to set
     */
    public void setSelectedauditRecovery(AuditRecoveryDobj selectedauditRecovery) {
        this.selectedauditRecovery = selectedauditRecovery;
    }

    /**
     * @return the showFeePaymentDetails
     */
    public boolean isShowFeePaymentDetails() {
        return showFeePaymentDetails;
    }

    /**
     * @param showFeePaymentDetails the showFeePaymentDetails to set
     */
    public void setShowFeePaymentDetails(boolean showFeePaymentDetails) {
        this.showFeePaymentDetails = showFeePaymentDetails;
    }
}
