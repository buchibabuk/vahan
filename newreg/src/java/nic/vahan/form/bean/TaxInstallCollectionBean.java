/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import nic.vahan.form.bean.permit.PermitPanelBean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.FeeDobj;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.Fee_Pay_Dobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.TaxInstallCollectionDobj;
import nic.vahan.form.dobj.Tax_Pay_Dobj;
import nic.vahan.form.dobj.common.UserIDAndPasswordForOnlinePaymentDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.TaxInstallCollectionImpl;
import nic.vahan.form.impl.TaxInstallmentConfigImpl;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author ankur
 */
@ManagedBean(name = "taxInstallCollectionBean")
@ViewScoped
public class TaxInstallCollectionBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static Logger LOGGER = Logger.getLogger(TaxInstallCollectionBean.class);
    private List<TaxFormPanelBean> listTaxForm = new ArrayList();
    private String regn_no;
    private Owner_dobj ownerDobj = new Owner_dobj();
    private OwnerImpl ownerImpl = null;
    private OwnerDetailsDobj ownerDtlsDobj;
    private boolean renderAllPanel = false;
    private boolean disable = false;
    private Long userChrg;
    private TaxFormPanelBean taxFormBean = null;
    private PaymentCollectionBean paymentCollectionBean = null;
    private boolean btn_save = true;
    private String filerefNo = "";
    private String orderIssueBy = "";
    private String orderNo = "";
    private String orderDate = "";
    private ArrayList list_vh_class = new ArrayList();
    private ArrayList list_vm_catg = new ArrayList();
    private ArrayList list_maker_model = new ArrayList();
    private TaxInstallCollectionDobj taxInstallCollectionDobj = new TaxInstallCollectionDobj();
    private TaxInstallCollectionDobj taxInstallPaidDobj = new TaxInstallCollectionDobj();
    private List<TaxInstallCollectionDobj> installmentPaidDetaillist = new ArrayList();
    private List<TaxInstallCollectionDobj> installmentPendingDetaillist = new ArrayList();
    private Long totalDraftAmount;
    private Long totalBalanceAmount;
    private Long excessAmount;
    private Long totalTaxAmount = 0L;
    private Long totalUserChrg = 0l;
    private String generatedRcptNo = null;
    private PermitPanelBean permitPanelBean = null;
    @ManagedProperty(value = "#{rcpt_bean}")
    private ReceiptMasterBean rcpt_bean;
    private boolean renderUserChargesAmountPanel = false;
    List<TaxInstallCollectionDobj> dobjlist = new ArrayList();
    List<TaxInstallCollectionDobj> listTaxDobj = new ArrayList<>();
    private boolean chechbox = true;
    private Long totalPenalty = 0L;
    private boolean renderPushBackSeatPanel = false;
    private String paymentTypeBtn = "";
    private String onlineUserCredentialmsg = "";
    private boolean renderOnlinePayBtn = true;
    private boolean renderUserAndPassword = false;
    private boolean renderSaveButton = true;
    private boolean renderOnlineCancelBtn = false;
    private String applNo = "";
    private SessionVariables sessionVariables = null;

    @PostConstruct
    public void postConstruct() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null
                || sessionVariables.getStateCodeSelected() == null
                || sessionVariables.getOffCodeSelected() == 0) {
            return;
        }
        ownerImpl = new OwnerImpl();
        paymentCollectionBean = new PaymentCollectionBean();
        setPermitPanelBean(new PermitPanelBean());
        String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        getList_vh_class().clear();
        for (int i = 0; i < data.length; i++) {
            getList_vh_class().add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_VCH_CATG.getData();
        for (int i = 0; i < data.length; i++) {
            getList_vm_catg().add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_MODELS.getData();
        for (int i = 0; i < data.length; i++) {

            getList_maker_model().add(new SelectItem(data[i][1], data[i][2]));
        }
    }

    public void validateForm(String payType) throws Exception {
        this.paymentTypeBtn = payType;
        boolean allGood = false;

        // Checking for Payment Mode
        if (Utility.isNullOrBlank(getPaymentCollectionBean().getPayment_mode())
                || "-1".equalsIgnoreCase(getPaymentCollectionBean().getPayment_mode())) {

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please select payment mode", "Please select payment mode");
            FacesContext.getCurrentInstance().addMessage(null, message);
            allGood = true;
            return;
        }

        setTotalDraftAmount(0l);
        if (!getPaymentCollectionBean().getPayment_mode().equals("C")) {

            for (nic.vahan.form.dobj.PaymentCollectionDobj payDobj : getPaymentCollectionBean().getPaymentlist()) {
                if (!(payDobj.validateDobj())) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, payDobj.getValidationMessage(), payDobj.getValidationMessage());
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    allGood = true;
                    break;
                } else {
                    setTotalDraftAmount((Long) getTotalDraftAmount() + Long.parseLong(payDobj.getAmount()));
                }
            }
            if (totalTaxAmount < getTotalDraftAmount()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Excess Total Instrument Amount (Rs." + (getTotalDraftAmount() - totalTaxAmount) + "/- must be adjusted in Installment fine amount"));
                return;
            }
            if ("SK".contains(Util.getUserStateCode()) && !allGood) {
                if (getTotalDraftAmount() < getTotalTaxAmount()) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Paying amount must be equal or greater then of total payable amount"));
                    return;
                }
            }
        } else {
            if ("SK".contains(Util.getUserStateCode())) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please select Mixed Mode for payment. Because Cash payment mode in not allowed in Sikkim"));
                allGood = true;
                return;
            }
        }

        if (paymentTypeBtn.equalsIgnoreCase("OnlinePayment") && !getPaymentCollectionBean().getPayment_mode().equals("C")) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please select cash payment mode for online payment.", "Please select cash payment mode for online payment.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }

        if (!allGood) {
            if (getTotalDraftAmount() <= getTotalTaxAmount()) {
                setTotalBalanceAmount(getTotalTaxAmount() - getTotalDraftAmount());
            } else {
                setTotalBalanceAmount((Long) 0l);
                setExcessAmount(getTotalDraftAmount() - getTotalTaxAmount());
            }
            setInstallmentPendingDetaillist(TaxInstallCollectionImpl.getPendingInstallmentList(regn_no, taxInstallCollectionDobj, ownerDobj));
            if (totalTaxAmount == 0) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Total Installment Amount is Zero, select atleast One Installment."));
                return;
            }
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("tax_insta_payment_subview:confirmationPopupp");
            PrimeFaces.current().executeScript("PF('confDlgTax').show()");
        }

    }

    public void onRowEdit(RowEditEvent event) {
        TaxInstallCollectionDobj ddd = new TaxInstallCollectionDobj();
        Date date = ((TaxInstallCollectionDobj) event.getObject()).getPayDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        int srno = Integer.parseInt((((TaxInstallCollectionDobj) event.getObject()).getSerialno()));
        int postion = installmentPendingDetaillist.indexOf((TaxInstallCollectionDobj) event.getObject());
        installmentPendingDetaillist.get(postion).setPaydueDate(sdf.format(date));
    }

    public void onRowCancel(RowEditEvent event) {
        int srno = Integer.parseInt((((TaxInstallCollectionDobj) event.getObject()).getSerialno()));
        installmentPendingDetaillist.remove(((TaxInstallCollectionDobj) event.getObject()));//.remove((TaxInstallCollectionDobj) event.getObject());
    }

    public void regnNo_focusLost() {
        if (Utility.isNullOrBlank(regn_no)) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Enter Vehicle Number!", "Please Enter Vehicle Number!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }

        regn_no = regn_no.toUpperCase();
        listTaxForm.clear();
        try {
            if (ownerImpl != null) {
                ownerDtlsDobj = ownerImpl.getOwnerDetails(this.regn_no.trim(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                if (ownerDtlsDobj == null || !ownerDtlsDobj.getState_cd().equals(Util.getUserStateCode())
                        || ownerDtlsDobj.getOff_cd() != Util.getSelectedSeat().getOff_cd()
                        || !(ownerDtlsDobj.getStatus().equals("A") || ownerDtlsDobj.getStatus().equals("Y"))) {
                    throw new VahanException("Either Invalid vehicle Registration No or You are not authorized to collect the MV Tax for this vehicle no.");
                }
                Owner_dobj owDobj = ownerImpl.getOwnerDobj(ownerDtlsDobj);
                setOwnerDobj(owDobj);
                setInstallmentPaidDetaillist(TaxInstallCollectionImpl.getPaidInstallist(regn_no, taxInstallPaidDobj));
                setInstallmentPendingDetaillist(TaxInstallCollectionImpl.getPendingInstallmentList(regn_no, taxInstallCollectionDobj, ownerDobj));

                if (!installmentPendingDetaillist.isEmpty()) {
                    filerefNo = installmentPendingDetaillist.get(0).getFileRefNo();
                    orderIssueBy = installmentPendingDetaillist.get(0).getOrderIssBy();
                    orderNo = installmentPendingDetaillist.get(0).getOrderNo();
                    orderDate = installmentPendingDetaillist.get(0).getOrderDate();
                    owDobj.setAppl_no(installmentPendingDetaillist.get(0).getAppl_no());
                } else {
                    if (!installmentPaidDetaillist.isEmpty()) {
                        filerefNo = installmentPaidDetaillist.get(0).getFileRefNo();
                        orderIssueBy = installmentPaidDetaillist.get(0).getOrderIssBy();
                        orderNo = installmentPaidDetaillist.get(0).getOrderNo();
                        orderDate = installmentPaidDetaillist.get(0).getOrderDate();
                    } else {
                        FacesMessage message = null;
                        message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle Installment Details Not Found!", "Vehicle Installment Details Not Found!");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        return;
                    }
                }

                if (getOwnerDobj() != null) {
                    setRenderAllPanel(true);
                    setDisable(true);
                    PassengerPermitDetailDobj permitDob = TaxServer_Impl.getPermitInfoForSavedTax(ownerDobj);
                    if (permitDob == null) {
                        TaxInstallmentConfigImpl instaximpl = new TaxInstallmentConfigImpl();
                        permitDob = instaximpl.getPermitBaseOnTaxPermitInfo(ownerDobj.getState_cd(), ownerDobj.getOff_cd(), regn_no);
                    }
                    if (permitDob != null) {
                        getPermitPanelBean().setPermitDobj(permitDob);
                    }
                    VehicleParameters vehicleParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj, permitDob);
                    listTaxForm = TaxServer_Impl.getListTaxForm(owDobj, vehicleParameters);

                    if (permitDob != null && permitDob.getOtherCriteria() != null && !permitDob.getOtherCriteria().isEmpty() && !permitDob.getOtherCriteria().equalsIgnoreCase("0")) {
                        ownerDobj.setOther_criteria(Integer.parseInt(permitDob.getOtherCriteria()));
                    }

                    if (Util.getTmConfiguration() != null && Util.getTmConfiguration().getTmConfigOnlineDobj() != null) {
                        if (Util.getTmConfiguration().getTmConfigOnlineDobj().isTax_installment()) {
                            UserIDAndPasswordForOnlinePaymentDobj obj = new FeeImpl().getUserIDAndPasswordForOnlinePayment(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), ownerDtlsDobj.getApplNo(), ownerDtlsDobj.getOwner_name(), ownerDtlsDobj.getOwnerIdentity().getMobile_no(), regn_no, "R");
                            if (obj != null) {
                                setOnlineUserCredentialmsg("Online Payment Credentials User ID is : <font color=#008001>" + obj.getAppl_no() + "</font> & Password :  <font color=#008001>" + obj.getUser_pwd() + "</font>");
                                applNo = obj.getAppl_no();
                                setRenderOnlinePayBtn(false);
                                setRenderSaveButton(false);
                                setInstallmentPendingDetaillist(TaxInstallCollectionImpl.getOnlineInstallmentList(applNo, sessionVariables.getStateCodeSelected()));
                                totalTaxAmount = (long) installmentPendingDetaillist.get(0).getTaxAmountInst1();
                                if (CommonUtils.isNullOrBlank(obj.getTransactionNo())) {
                                    setRenderOnlineCancelBtn(true);
                                } else {
                                    setRenderOnlineCancelBtn(false);
                                }
                            }
                        }
                    } else {
                        throw new VahanException("Problem in loading configuration file, please re-login !!!");
                    }
                } else {
                    FacesMessage message = null;
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle Details Not Found!", "Vehicle Details Not Found!");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }

            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            reset();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            reset();
        }
    }

    public void updateTotalPayableAmount() {
        setTotalTaxAmount(0L);

        setTotalTaxAmount(getTotalTaxAmount());
    }

    public String cancelOnlinePayment() {
        try {
            boolean flag = ServerUtil.cancelOnlinePayment(this.applNo, TableConstants.ONLINE_TAX);
            if (flag) {
                reset();
                JSFUtils.showMessagesInDialog("Info !!!", "Request to cancel online payment successfull !!!", FacesMessage.SEVERITY_INFO);
                return "/ui/tax/formTaxInstallCollection.xhtml?faces-redirect=true";
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error occured during cancel Online Payment !!!", "Error occured during cancel Online Payment !!!"));
        }
        return "";
    }

    public String saveInstallDetails() {

        Exception e = null;
        try {
            regn_no = regn_no.toUpperCase();
            String flag = TaxInstallCollectionImpl.reconfigureInstallment(regn_no, installmentPendingDetaillist, ownerDobj, installmentPaidDetaillist.size());
            if (flag.equals("Success")) {
                rcpt_bean.reset();
                listTaxForm.clear();
                installmentPendingDetaillist.clear();
                return "/ui/tax/formTaxInstallReConfigure.xhtml?faces-redirect=true";
            }
        } catch (VahanException ex) {
            e = ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = ex;
        }
        if (e != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }

        return "";
    }

    public String saveTaxDetails() {
        Exception e = null;
        try {

            FeeDraftDobj feeDraftDobj = null;

            if (!Utility.isNullOrBlank(getPaymentCollectionBean().getPayment_mode()) && (!getPaymentCollectionBean().getPayment_mode().equals("-1"))
                    && (!getPaymentCollectionBean().getPayment_mode().equals("C"))) {

                feeDraftDobj = new FeeDraftDobj();
                feeDraftDobj.setCollected_by(Util.getEmpCode());
                feeDraftDobj.setState_cd(Util.getUserStateCode());
                feeDraftDobj.setOff_cd(String.valueOf(Util.getUserOffCode()));
                feeDraftDobj.setDraftPaymentList(getPaymentCollectionBean().getPaymentlist());

            }
            if (totalTaxAmount == 0) {
                throw new VahanException("Total Installment Amount is Zero, select atleast One Installment.");
            }
            Long userCharges = getTotalUserChrg();
            String payMode = getPaymentCollectionBean().getPayment_mode();
            if (this.paymentTypeBtn.equals("CashPayment")) {
                generatedRcptNo = TaxInstallCollectionImpl.saveInstallmentRcpt(userCharges, payMode, feeDraftDobj, dobjlist, ownerDobj, totalTaxAmount, totalPenalty, getPermitPanelBean().getPermitDobj());
                if (!Utility.isNullOrBlank(generatedRcptNo)) {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful Transaction", " Receipt Number Generated : " + generatedRcptNo);
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", generatedRcptNo);
                }
                rcpt_bean.reset();
                listTaxForm.clear();
                setInstallmentPaidDetaillist(TaxInstallCollectionImpl.getPaidInstallist(regn_no, taxInstallPaidDobj));
                setInstallmentPendingDetaillist(TaxInstallCollectionImpl.getPendingInstallmentList(regn_no, taxInstallCollectionDobj, ownerDobj));
                updateTotalPayableAmount();
                // RequestContext req = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("f:tax_insta_payment_subview:tb_showHistrorytaxtable_data");
                return "PrintCashReceiptReport";
            } else if (this.paymentTypeBtn.equals("OnlinePayment")) {
                Fee_Pay_Dobj feePayDobj = null;
                feePayDobj = this.makeTaxPayDobj();
                if (feePayDobj == null) {
                    throw new VahanException("Problem occured duriing Tax calculation !!!");
                }
                String userPwd = new FeeImpl().saveOnlinePaymentData(feePayDobj, totalTaxAmount, null, ownerDobj.getOwner_identity().getMobile_no(), Util.getUserStateCode(), Util.getUserSeatOffCode(), Util.getEmpCode(), TableConstants.TAX_INSTALLMENT, "I", ownerDtlsDobj, false, false);
                if (userPwd != null) {
                    applNo = userPwd.split(",")[0];
                    setRenderOnlineCancelBtn(true);
                    setRenderOnlinePayBtn(false);
                    setRenderSaveButton(false);
                    setOnlineUserCredentialmsg("Online Payment Credentials User ID is : <font color=#008001>" + applNo + "</font> & Password :  <font color=#008001>" + userPwd.split(",")[1] + "</font>");
                    PrimeFaces.current().executeScript("PF('successVar').show();");
                } else {
                    throw new VahanException("Error in Saving Details !!");
                }
            }
        } catch (VahanException ex) {
            e = ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = ex;
        }
        if (e != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
        return "";
    }

    private Fee_Pay_Dobj makeTaxPayDobj() throws VahanException {
        Fee_Pay_Dobj feePayDobj = new Fee_Pay_Dobj();
        long totalPayableTaxAmount = 0l;
        long totalPayablePenaltyAmount = 0l;
        String payUpTo = "";
        List<Tax_Pay_Dobj> taxDobjList = new ArrayList<>();
        try {
            for (TaxInstallCollectionDobj installPayDobj : dobjlist) {
                totalPayableTaxAmount += installPayDobj.getTaxAmountInst();
                totalPayablePenaltyAmount += installPayDobj.getPenalty();
                payUpTo = installPayDobj.getUptodate();
            }

            if (dobjlist.size() > 0) {
                TaxInstallCollectionDobj installPayDobj = dobjlist.get(0);
                Tax_Pay_Dobj taxPayDobj = new Tax_Pay_Dobj();
                taxPayDobj.setPur_cd(58);
                taxPayDobj.setTaxMode("I");
                taxPayDobj.setFinalTaxFrom(installPayDobj.getPaydueDate());
                taxPayDobj.setFinalTaxUpto(payUpTo);
                taxPayDobj.setTotalPaybaleTax(totalPayableTaxAmount);
                taxPayDobj.setTotalPaybaleRebate(0l);
                taxPayDobj.setTotalPaybaleInterest(0l);
                taxPayDobj.setTotalPaybalePenalty(totalPayablePenaltyAmount);
                taxPayDobj.setTotalPaybaleInterest(0l);
                taxPayDobj.setTotalPaybaleSurcharge(0l);
                taxPayDobj.setTotalPaybaleTax1(0l);
                taxPayDobj.setTotalPaybaleTax1(0l);

                DOTaxDetail taxBreakup = new DOTaxDetail(); // Tax breakup details.
                taxBreakup.setTAX_FROM(installPayDobj.getPaydueDate());
                taxBreakup.setTAX_UPTO(installPayDobj.getUptodate());
                taxBreakup.setAMOUNT((double) (totalPayableTaxAmount));
                taxBreakup.setPENALTY((double) totalPayablePenaltyAmount);
                taxBreakup.setPUR_CD(58);
                taxBreakup.setPRV_ADJ(0L);
                taxBreakup.setREBATE(0.0);
                taxBreakup.setSURCHARGE(0.0);
                taxBreakup.setINTEREST(0.0);
                taxBreakup.setAMOUNT1(0.0);
                taxBreakup.setAMOUNT2(0.0);
                taxDobjList.add(taxPayDobj);
                feePayDobj.setListTaxDobj(taxDobjList); // tax details 
                taxPayDobj.getTaxBreakDetails().add(taxBreakup);
                feePayDobj.setTaxInstallDobjList(dobjlist);
                feePayDobj.setCollectedFeeList(new ArrayList<FeeDobj>());
            }
        } catch (Exception ex) {
            feePayDobj = null;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem in making online payment details !!!");
        }
        return feePayDobj;
    }

    public void updatetotalAMt(TaxInstallCollectionDobj dobj) {
        try {

            int serialnoSelected = dobj.getTablesrno() - 1;

            boolean flag = installmentPendingDetaillist.get(serialnoSelected).isSelect();
            if (flag) {
                if (serialnoSelected < installmentPendingDetaillist.size() - 1) {
                    installmentPendingDetaillist.get(serialnoSelected + 1).setDisablecheckbox(false);
                }
            } else {
                for (int a = 0; a < installmentPendingDetaillist.size(); a++) {
                    installmentPendingDetaillist.get(a).setSelect(false);
                    installmentPendingDetaillist.get(a).setDisablecheckbox(true);
                    installmentPendingDetaillist.get(a).setPenalty(0L);
                }

                installmentPendingDetaillist.get(0).setDisablecheckbox(false);
            }
            if (dobj.isSelect()) {
                dobjlist.add(dobj);
                totalTaxAmount += ((dobj.getTaxAmountInst() + dobj.getPenalty()));
                totalPenalty += dobj.getPenalty();
            } else {
                totalTaxAmount = 0L;
                totalPenalty = 0L;
                dobjlist.clear();
            }
        } catch (Exception e) {
        }
    }

    public void reset() {
        setRegn_no("");
        setOwnerDobj(null);
        if (getTaxFormBean() != null) {
            getTaxFormBean().reset();
        }
        if (getPaymentCollectionBean() != null) {
            getPaymentCollectionBean().reset();
        }
        setBtn_save(false);

    }

    @Override
    public String save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String saveAndMoveFile() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    public OwnerImpl getOwnerImpl() {
        return ownerImpl;
    }

    public void setOwnerImpl(OwnerImpl ownerImpl) {
        this.ownerImpl = ownerImpl;
    }

    public boolean isRenderAllPanel() {
        return renderAllPanel;
    }

    public void setRenderAllPanel(boolean renderAllPanel) {
        this.renderAllPanel = renderAllPanel;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public Long getUserChrg() {
        return userChrg;
    }

    public void setUserChrg(Long userChrg) {
        this.userChrg = userChrg;
    }

    public TaxFormPanelBean getTaxFormBean() {
        return taxFormBean;
    }

    public void setTaxFormBean(TaxFormPanelBean taxFormBean) {
        this.taxFormBean = taxFormBean;
    }

    public PaymentCollectionBean getPaymentCollectionBean() {
        return paymentCollectionBean;
    }

    public void setPaymentCollectionBean(PaymentCollectionBean paymentCollectionBean) {
        this.paymentCollectionBean = paymentCollectionBean;
    }

    public boolean isBtn_save() {
        return btn_save;
    }

    public void setBtn_save(boolean btn_save) {
        this.btn_save = btn_save;
    }

    public ArrayList getList_vh_class() {
        return list_vh_class;
    }

    public void setList_vh_class(ArrayList list_vh_class) {
        this.list_vh_class = list_vh_class;
    }

    public ArrayList getList_vm_catg() {
        return list_vm_catg;
    }

    public void setList_vm_catg(ArrayList list_vm_catg) {
        this.list_vm_catg = list_vm_catg;
    }

    public ArrayList getList_maker_model() {
        return list_maker_model;
    }

    public void setList_maker_model(ArrayList list_maker_model) {
        this.list_maker_model = list_maker_model;
    }

    public OwnerDetailsDobj getOwnerDtlsDobj() {
        return ownerDtlsDobj;
    }

    public void setOwnerDtlsDobj(OwnerDetailsDobj ownerDtlsDobj) {
        this.ownerDtlsDobj = ownerDtlsDobj;
    }

    public List<TaxFormPanelBean> getListTaxForm() {
        return listTaxForm;
    }

    public void setListTaxForm(List<TaxFormPanelBean> listTaxForm) {
        this.listTaxForm = listTaxForm;
    }

    public TaxInstallCollectionDobj getTaxInstallCollectionDobj() {
        return taxInstallCollectionDobj;
    }

    public void setTaxInstallCollectionDobj(TaxInstallCollectionDobj taxInstallCollectionDobj) {
        this.taxInstallCollectionDobj = taxInstallCollectionDobj;
    }

    public List<TaxInstallCollectionDobj> getInstallmentPendingDetaillist() {
        return installmentPendingDetaillist;
    }

    public void setInstallmentPendingDetaillist(List<TaxInstallCollectionDobj> installmentPendingDetaillist) {
        this.installmentPendingDetaillist = installmentPendingDetaillist;
    }

    public String getFilerefNo() {
        return filerefNo;
    }

    public void setFilerefNo(String filerefNo) {
        this.filerefNo = filerefNo;
    }

    public String getOrderIssueBy() {
        return orderIssueBy;
    }

    public void setOrderIssueBy(String orderIssueBy) {
        this.orderIssueBy = orderIssueBy;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public List<TaxInstallCollectionDobj> getInstallmentPaidDetaillist() {
        return installmentPaidDetaillist;
    }

    public void setInstallmentPaidDetaillist(List<TaxInstallCollectionDobj> installmentPaidDetaillist) {
        this.installmentPaidDetaillist = installmentPaidDetaillist;
    }

    public TaxInstallCollectionDobj getTaxInstallPaidDobj() {
        return taxInstallPaidDobj;
    }

    public void setTaxInstallPaidDobj(TaxInstallCollectionDobj taxInstallPaidDobj) {
        this.taxInstallPaidDobj = taxInstallPaidDobj;
    }

    /**
     * @return the totalDraftAmount
     */
    public Long getTotalDraftAmount() {
        return totalDraftAmount;
    }

    /**
     * @param totalDraftAmount the totalDraftAmount to set
     */
    public void setTotalDraftAmount(Long totalDraftAmount) {
        this.totalDraftAmount = totalDraftAmount;
    }

    /**
     * @return the totalBalanceAmount
     */
    public Long getTotalBalanceAmount() {
        return totalBalanceAmount;
    }

    /**
     * @param totalBalanceAmount the totalBalanceAmount to set
     */
    public void setTotalBalanceAmount(Long totalBalanceAmount) {
        this.totalBalanceAmount = totalBalanceAmount;
    }

    /**
     * @return the excessAmount
     */
    public Long getExcessAmount() {
        return excessAmount;
    }

    /**
     * @param excessAmount the excessAmount to set
     */
    public void setExcessAmount(Long excessAmount) {
        this.excessAmount = excessAmount;
    }

    /**
     * @return the totalTaxAmount
     */
    public Long getTotalTaxAmount() {
        return totalTaxAmount;
    }

    /**
     * @param totalTaxAmount the totalTaxAmount to set
     */
    public void setTotalTaxAmount(Long totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    /**
     * @return the totalUserChrg
     */
    public Long getTotalUserChrg() {
        return totalUserChrg;
    }

    /**
     * @param totalUserChrg the totalUserChrg to set
     */
    public void setTotalUserChrg(Long totalUserChrg) {
        this.totalUserChrg = totalUserChrg;
    }

    /**
     * @return the permitPanelBean
     */
    public PermitPanelBean getPermitPanelBean() {
        return permitPanelBean;
    }

    /**
     * @param permitPanelBean the permitPanelBean to set
     */
    public void setPermitPanelBean(PermitPanelBean permitPanelBean) {
        this.permitPanelBean = permitPanelBean;
    }

    /**
     * @return the rcpt_bean
     */
    public ReceiptMasterBean getRcpt_bean() {
        return rcpt_bean;
    }

    /**
     * @param rcpt_bean the rcpt_bean to set
     */
    public void setRcpt_bean(ReceiptMasterBean rcpt_bean) {
        this.rcpt_bean = rcpt_bean;
    }

    /**
     * @return the renderUserChargesAmountPanel
     */
    public boolean isRenderUserChargesAmountPanel() {
        return renderUserChargesAmountPanel;
    }

    /**
     * @param renderUserChargesAmountPanel the renderUserChargesAmountPanel to
     * set
     */
    public void setRenderUserChargesAmountPanel(boolean renderUserChargesAmountPanel) {
        this.renderUserChargesAmountPanel = renderUserChargesAmountPanel;
    }

    /**
     * @return the chechbox
     */
    public boolean isChechbox() {
        return chechbox;
    }

    /**
     * @param chechbox the chechbox to set
     */
    public void setChechbox(boolean chechbox) {
        this.chechbox = chechbox;
    }

    /**
     * @return the totalPenalty
     */
    public Long getTotalPenalty() {
        return totalPenalty;
    }

    /**
     * @param totalPenalty the totalPenalty to set
     */
    public void setTotalPenalty(Long totalPenalty) {
        this.totalPenalty = totalPenalty;
    }

    /**
     * @return the renderPushBackSeatPanel
     */
    public boolean isRenderPushBackSeatPanel() {
        return renderPushBackSeatPanel;
    }

    /**
     * @param renderPushBackSeatPanel the renderPushBackSeatPanel to set
     */
    public void setRenderPushBackSeatPanel(boolean renderPushBackSeatPanel) {
        this.renderPushBackSeatPanel = renderPushBackSeatPanel;
    }

    public String getOnlineUserCredentialmsg() {
        return onlineUserCredentialmsg;
    }

    public void setOnlineUserCredentialmsg(String onlineUserCredentialmsg) {
        this.onlineUserCredentialmsg = onlineUserCredentialmsg;
    }

    public boolean isRenderOnlinePayBtn() {
        return renderOnlinePayBtn;
    }

    public void setRenderOnlinePayBtn(boolean renderOnlinePayBtn) {
        this.renderOnlinePayBtn = renderOnlinePayBtn;
    }

    public boolean isRenderUserAndPassword() {
        return renderUserAndPassword;
    }

    public void setRenderUserAndPassword(boolean renderUserAndPassword) {
        this.renderUserAndPassword = renderUserAndPassword;
    }

    public boolean isRenderSaveButton() {
        return renderSaveButton;
    }

    public void setRenderSaveButton(boolean renderSaveButton) {
        this.renderSaveButton = renderSaveButton;
    }

    public boolean isRenderOnlineCancelBtn() {
        return renderOnlineCancelBtn;
    }

    public void setRenderOnlineCancelBtn(boolean renderOnlineCancelBtn) {
        this.renderOnlineCancelBtn = renderOnlineCancelBtn;
    }
}
