/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model;

import nic.vahan5.reg.rest.model.permit.PermitPanelBeanModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.NewVehicleFeebean;
import nic.vahan.form.bean.TaxFormPanelBean;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.FeeDobj;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.Fee_Pay_Dobj;
import nic.vahan.form.dobj.ManualReceiptEntryDobj;
import nic.vahan.form.dobj.OnlinePayDobj;
import nic.vahan.form.dobj.OtherStateVehDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.TaxExemptiondobj;
import nic.vahan.form.dobj.TempRegDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.Trailer_dobj;
import nic.vahan.form.dobj.configuration.TmConfigurationReceipts;
import nic.vahan.form.dobj.dealer.PaymentGatewayDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import static nic.vahan.server.ServerUtil.Compare;
import nic.vahan5.reg.commonutils.FormulaUtilities;
import nic.vahan5.reg.form.impl.EpayImplementation;
import nic.vahan5.reg.form.impl.ExemptionFeeFineImplementation;

/**
 *
 * @author Kartikey Singh
 */
public class NewVehicleFeeBeanModel {

    private String appl_no;
    private Owner_dobj ownerDobj;
    private OwnerDetailsDobj ownerDetailsDobj;
//    FeeImpl feeImpl;
//    OwnerImpl ownerImpl;
    private List<TaxFormPanelBean> listTaxForm;
    private List<TaxFormPanelBean> listTaxFormBackup;
    private List list_vm_catg;
    private List list_maker_model;
    private FormFeePanelBeanModel feePanelBeanModel;
    private TaxFormPanelBean taxBean;
    private PaymentCollectionBeanModel paymentBeanModel;
    private PermitPanelBeanModel permitPanelBeanModel;
    private long totalAmountPayable;
//    FeeDraftimpl feeDraftImpl;
//    PermitImpl permitImpl;
    private int pur_cd;
    private boolean newRegistration;
    private boolean tempRegistration;
    private String regn_no;
    private String taxMode;
    private List list_vh_class;
    private String pageLoadingFailed;
    private String taxpanel;
    private String printConfDlgTax;
    private boolean renderTaxPanel;
    private String btn_print_label;
    private boolean btn_print;
    private boolean btn_save;
    private String generatedRcpt;
    String printRcptNo;
    private boolean buttonPanelVisibility;
    private String receiptStickyNote;
    private List<PaymentGatewayDobj> addToCartStatusCount;
    private ManualReceiptEntryDobj manualRcptDobjTemp;
    private String chasino;
    Map map;
    private String applno;
    // Afzal;
    private List<FeeDobj> feeCollectionLists;
    private boolean renderPermitPanel;
    private Long userChrg;
    private boolean renderUserChargesAmountPanel;
    private List<FeeDobj> listTransWise;
    private Long totalUserChrg;
    private boolean taxExemption;
    private boolean taxInstallment;
    private boolean renderTaxExemption;
    private boolean renderSmartCardFeePanel;
    private boolean renderTaxInstallment;
    private String taxInstallMode;
    private Long smartCardFee;
    private Long smartCardChrg;
    private String rcptNoPopup;
    private int actionCode;
    private boolean showInRto;
    private Date currentDate;
    private TempRegDobj tempReg;
    private boolean blnDisableRegnTypeTemp;
    private boolean renderTempregPanel;
    private List list_c_state;
    private List list_office_to;
    private List list_dealer_cd;
    private OtherStateVehDobj otherStateVehDobj;
    private boolean disableOtherState;
    private boolean renderFeePanelLabel;
    private boolean marqueManualReceipt;
    private boolean renderManualReceiptMessage;
    private String FEE_ACTION;
    private String permitRefreshDtl;
    private String permitTaxDtl;
    private String permitFeeDtl;
    private boolean renderCdPanel;
    private String seriesAvailMess;
    private boolean isSeriesAvail;
    private String fancyOrRetenRegnMess;
    private boolean isFancyOrReten;
    private boolean autoRunTaxListener;
    private boolean renderModelCost;
    private boolean disableSaveButton;
    private boolean disableRevertBackButton;
    private boolean renderOnlinePayBtn;
    private boolean btnAddToCart;
    //private boolean btnAddToCartOnlinePay;
    private String paymentTypeBtn;
    private boolean renderCancelPayment;
    private boolean renderUserAndPasswored;
    private boolean renderPushBackSeatPanel;
    private boolean isManualReceiptRecord;
    private String onlineUserCredentialmsg;
    private List<DOTaxDetail> taxDescriptionList;
    private List list_Pincode;
    private String pinStatus;
    private boolean confirmDialogRenderButton;
//    private SessionVariables sessionVariables;
    private List<Trailer_dobj> listTrailerDobj;
    private boolean rendertempFeeAmount;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private String appl_dt;
    
    // Added these to send data from REST controller back to NEwRegnFeebean
    private Fee_Pay_Dobj feePayDobj;
    private FeeDraftDobj feeDeaftDobj;
    private String[] rcptNo;
    private String userPwd;
    private List<OnlinePayDobj> payDobj;
    private TmConfigurationReceipts tmConfigurationReceipts;

    public List<ComparisonBean> compareChanges() throws VahanException {
        PassengerPermitDetailDobj permitDobjCom = permitPanelBeanModel.getPermitDobjPrev();
        if (permitDobjCom == null) {
            return compBeanList;
        }
        compBeanList.clear();
        Compare("Permit Type", permitDobjCom.getPmt_type(), ownerDobj.getPmt_type() + "", compBeanList);
        Compare("Permit Catg", permitDobjCom.getPmtCatg(), ownerDobj.getPmt_catg() + "", compBeanList);
        Compare("Permit ServiceType", permitDobjCom.getServices_TYPE(), ownerDobj.getServicesType(), compBeanList);
        return compBeanList;
    }

    public NewVehicleFeeBeanModel() {
    }

    public NewVehicleFeeBeanModel(NewVehicleFeebean newVehicleFeeBean) {
        this.appl_no = newVehicleFeeBean.getAppl_no();
        this.ownerDobj = newVehicleFeeBean.getOwnerDobj();
        this.ownerDetailsDobj = newVehicleFeeBean.getOwnerDetailsDobj();
//        this.feeImpl = newVehicleFeeBean.getFeeImpl();
//        this.ownerImpl = newVehicleFeeBean.getOwnerImpl();
        this.listTaxForm = newVehicleFeeBean.getListTaxForm();
        this.listTaxFormBackup = newVehicleFeeBean.getListTaxFormBackup();
        this.list_vm_catg = newVehicleFeeBean.getList_vm_catg();
        this.list_maker_model = newVehicleFeeBean.getList_maker_model();
        if (newVehicleFeeBean.getFeePanelBean() != null) {
            this.feePanelBeanModel = new FormFeePanelBeanModel(newVehicleFeeBean.getFeePanelBean());
        }
        this.taxBean = newVehicleFeeBean.getTaxBean();
        if (newVehicleFeeBean.getPaymentBean() != null) {
            this.paymentBeanModel = new PaymentCollectionBeanModel(newVehicleFeeBean.getPaymentBean());
        }
        if (newVehicleFeeBean.getPermitPanelBean() != null) {
            this.permitPanelBeanModel = new PermitPanelBeanModel(newVehicleFeeBean.getPermitPanelBean());
        }
        this.totalAmountPayable = newVehicleFeeBean.getTotalAmountPayable();
//        this.feeDraftImpl = newVehicleFeeBean.getFeeDraftImpl();
//        this.permitImpl = newVehicleFeeBean.getPermitImpl();
        this.pur_cd = newVehicleFeeBean.getPur_cd();
        this.newRegistration = newVehicleFeeBean.isNewRegistration();
        this.tempRegistration = newVehicleFeeBean.isTempRegistration();
        this.regn_no = newVehicleFeeBean.getRegn_no();
        this.taxMode = newVehicleFeeBean.getTaxMode();
        this.list_vh_class = newVehicleFeeBean.getList_vh_class();
        this.pageLoadingFailed = newVehicleFeeBean.getPageLoadingFailed();
        this.taxpanel = newVehicleFeeBean.getTaxpanel();
        this.printConfDlgTax = newVehicleFeeBean.getPrintConfDlgTax();
        this.renderTaxPanel = newVehicleFeeBean.isRenderTaxPanel();
        this.btn_print_label = newVehicleFeeBean.getBtn_print_label();
        this.btn_print = newVehicleFeeBean.isBtn_print();
        this.btn_save = newVehicleFeeBean.isBtn_save();
        this.generatedRcpt = newVehicleFeeBean.getGeneratedRcpt();
        this.printRcptNo = newVehicleFeeBean.getPrintRcptNo();
        this.buttonPanelVisibility = newVehicleFeeBean.isButtonPanelVisibility();
        this.receiptStickyNote = newVehicleFeeBean.getReceiptStickyNote();
        this.addToCartStatusCount = newVehicleFeeBean.getAddToCartStatusCount();
        this.manualRcptDobjTemp = newVehicleFeeBean.getManualRcptDobjTemp();
        this.chasino = newVehicleFeeBean.getChasino();
//        this.map = newVehicleFeeBean.getMap();
        this.applno = newVehicleFeeBean.getApplno();
        this.feeCollectionLists = newVehicleFeeBean.getFeeCollectionLists();
        this.renderPermitPanel = newVehicleFeeBean.isRenderPermitPanel();
        this.userChrg = newVehicleFeeBean.getUserChrg();
        this.renderUserChargesAmountPanel = newVehicleFeeBean.isRenderUserChargesAmountPanel();
        this.listTransWise = newVehicleFeeBean.getListTransWise();
        this.totalUserChrg = newVehicleFeeBean.getTotalUserChrg();
        this.taxExemption = newVehicleFeeBean.isTaxExemption();
        this.taxInstallment = newVehicleFeeBean.isTaxInstallment();
        this.renderTaxExemption = newVehicleFeeBean.isRenderTaxExemption();
        this.renderSmartCardFeePanel = newVehicleFeeBean.isRenderSmartCardFeePanel();
        this.renderTaxInstallment = newVehicleFeeBean.isRenderTaxInstallment();
        this.taxInstallMode = newVehicleFeeBean.getTaxInstallMode();
        this.smartCardFee = newVehicleFeeBean.getSmartCardFee();
        this.smartCardChrg = newVehicleFeeBean.getSmartCardChrg();
        this.rcptNoPopup = newVehicleFeeBean.getRcptNoPopup();
        this.actionCode = newVehicleFeeBean.getActionCode();
        this.showInRto = newVehicleFeeBean.isShowInRto();
        this.currentDate = newVehicleFeeBean.getCurrentDate();
        this.tempReg = newVehicleFeeBean.getTempReg();
        this.blnDisableRegnTypeTemp = newVehicleFeeBean.isBlnDisableRegnTypeTemp();
        this.renderTempregPanel = newVehicleFeeBean.isRenderTempregPanel();
        this.list_c_state = newVehicleFeeBean.getList_c_state();
        this.list_office_to = newVehicleFeeBean.getList_office_to();
        this.list_dealer_cd = newVehicleFeeBean.getList_dealer_cd();
        this.otherStateVehDobj = newVehicleFeeBean.getOtherStateVehDobj();
        this.disableOtherState = newVehicleFeeBean.isDisableOtherState();
        this.renderFeePanelLabel = newVehicleFeeBean.isRenderFeePanelLabel();
        this.marqueManualReceipt = newVehicleFeeBean.isMarqueManualReceipt();
        this.renderManualReceiptMessage = newVehicleFeeBean.isRenderManualReceiptMessage();
        this.FEE_ACTION = newVehicleFeeBean.getFEE_ACTION();
        this.permitRefreshDtl = newVehicleFeeBean.getPermitRefreshDtl();
        this.permitTaxDtl = newVehicleFeeBean.getPermitTaxDtl();
        this.permitFeeDtl = newVehicleFeeBean.getPermitFeeDtl();
        this.renderCdPanel = newVehicleFeeBean.isRenderCdPanel();
        this.seriesAvailMess = newVehicleFeeBean.getSeriesAvailMess();
        this.isSeriesAvail = newVehicleFeeBean.isIsSeriesAvail();
        this.fancyOrRetenRegnMess = newVehicleFeeBean.getFancyOrRetenRegnMess();
        this.isFancyOrReten = newVehicleFeeBean.isIsFancyOrReten();
        this.autoRunTaxListener = newVehicleFeeBean.isAutoRunTaxListener();
        this.renderModelCost = newVehicleFeeBean.isRenderModelCost();
        this.disableSaveButton = newVehicleFeeBean.isDisableSaveButton();
        this.disableRevertBackButton = newVehicleFeeBean.isDisableRevertBackButton();
        this.renderOnlinePayBtn = newVehicleFeeBean.isRenderOnlinePayBtn();
        this.btnAddToCart = newVehicleFeeBean.isBtnAddToCart();
        this.paymentTypeBtn = newVehicleFeeBean.getPaymentTypeBtn();
        this.renderCancelPayment = newVehicleFeeBean.isRenderCancelPayment();
        this.renderUserAndPasswored = newVehicleFeeBean.isRenderUserAndPasswored();
        this.renderPushBackSeatPanel = newVehicleFeeBean.isRenderPushBackSeatPanel();
        this.isManualReceiptRecord = newVehicleFeeBean.isIsManualReceiptRecord();
        this.onlineUserCredentialmsg = newVehicleFeeBean.getOnlineUserCredentialmsg();
        this.taxDescriptionList = newVehicleFeeBean.getTaxDescriptionList();
        this.list_Pincode = newVehicleFeeBean.getList_Pincode();
        this.pinStatus = newVehicleFeeBean.getPinStatus();
        this.confirmDialogRenderButton = newVehicleFeeBean.isConfirmDialogRenderButton();
//        this.sessionVariables = newVehicleFeeBean.getSessionVariables();
        this.listTrailerDobj = newVehicleFeeBean.getListTrailerDobj();
        this.rendertempFeeAmount = newVehicleFeeBean.isRendertempFeeAmount();
        this.appl_dt = newVehicleFeeBean.getAppl_dt();
    }

    public void setNewVehicleFeeBeanFromModel(NewVehicleFeebean newVehicleFeeBean) {
        newVehicleFeeBean.setAppl_no(this.getAppl_no());
        newVehicleFeeBean.setOwnerDobj(this.getOwnerDobj());
        newVehicleFeeBean.setOwnerDetailsDobj(this.getOwnerDetailsDobj());
//        newVehicleFeeBean.setFeeImpl(this.getFeeImpl());
//        newVehicleFeeBean.setOwnerImpl(this.getOwnerImpl());
        // Doesn;t get changed in REST
        // Need to find a solution for this though
//        newVehicleFeeBean.setListTaxForm(this.getListTaxForm());
        newVehicleFeeBean.setListTaxFormBackup(this.getListTaxFormBackup());
        // Doesn;t get changed in REST
//        newVehicleFeeBean.setList_vm_catg(this.getList_vm_catg());
//        newVehicleFeeBean.setList_maker_model(this.getList_maker_model());
        if (this.getFeePanelBeanModel() != null) {
            this.getFeePanelBeanModel().setFormFeePanelBeanFromModel(newVehicleFeeBean.getFeePanelBean());
        }
        newVehicleFeeBean.setTaxBean(this.getTaxBean());
        if (this.getPaymentBeanModel() != null) {
            this.getPaymentBeanModel().setPaymentCollectionBeanFromModel(newVehicleFeeBean.getPaymentBean());
        }
        if (this.getPermitPanelBeanModel() != null) {
            this.getPermitPanelBeanModel().setPermitPanelBeanFromModel(newVehicleFeeBean.getPermitPanelBean());            
        }
        newVehicleFeeBean.setTotalAmountPayable(this.getTotalAmountPayable());
//        newVehicleFeeBean.setFeeDraftImpl(this.getFeeDraftImpl());
//        newVehicleFeeBean.setPermitImpl(this.getPermitImpl());
        newVehicleFeeBean.setPur_cd(this.getPur_cd());
        newVehicleFeeBean.setNewRegistration(this.isNewRegistration());
        newVehicleFeeBean.setTempRegistration(this.isTempRegistration());
        newVehicleFeeBean.setRegn_no(this.getRegn_no());
        newVehicleFeeBean.setTaxMode(this.getTaxMode());
//        newVehicleFeeBean.setList_vh_class(this.getList_vh_class());
        newVehicleFeeBean.setPageLoadingFailed(this.getPageLoadingFailed());
        newVehicleFeeBean.setTaxpanel(this.getTaxpanel());
        newVehicleFeeBean.setPrintConfDlgTax(this.getPrintConfDlgTax());
        newVehicleFeeBean.setRenderTaxPanel(this.isRenderTaxPanel());
        newVehicleFeeBean.setBtn_print_label(this.getBtn_print_label());
        newVehicleFeeBean.setBtn_print(this.isBtn_print());
        newVehicleFeeBean.setBtn_save(this.isBtn_save());
        newVehicleFeeBean.setGeneratedRcpt(this.getGeneratedRcpt());
        newVehicleFeeBean.setPrintRcptNo(this.getPrintRcptNo());
        newVehicleFeeBean.setButtonPanelVisibility(this.isButtonPanelVisibility());
        newVehicleFeeBean.setReceiptStickyNote(this.getReceiptStickyNote());
        newVehicleFeeBean.setAddToCartStatusCount(this.getAddToCartStatusCount());
        newVehicleFeeBean.setManualRcptDobjTemp(this.getManualRcptDobjTemp());
        newVehicleFeeBean.setChasino(this.getChasino());
//        newVehicleFeeBean.setmap(this.getMap());
        newVehicleFeeBean.setApplno(this.getApplno());
        newVehicleFeeBean.setFeeCollectionLists(this.getFeeCollectionLists());
        newVehicleFeeBean.setRenderPermitPanel(this.isRenderPermitPanel());
        newVehicleFeeBean.setUserChrg(this.getUserChrg());
        newVehicleFeeBean.setRenderUserChargesAmountPanel(this.isRenderUserChargesAmountPanel());
        newVehicleFeeBean.setListTransWise(this.getListTransWise());
        newVehicleFeeBean.setTotalUserChrg(this.getTotalUserChrg());
        newVehicleFeeBean.setTaxExemption(this.isTaxExemption());
        newVehicleFeeBean.setTaxInstallment(this.isTaxInstallment());
        newVehicleFeeBean.setRenderTaxExemption(this.isRenderTaxExemption());
        newVehicleFeeBean.setRenderSmartCardFeePanel(this.isRenderSmartCardFeePanel());
        newVehicleFeeBean.setRenderTaxInstallment(this.isRenderTaxInstallment());
        newVehicleFeeBean.setTaxInstallMode(this.getTaxInstallMode());
        newVehicleFeeBean.setSmartCardFee(this.getSmartCardFee());
        newVehicleFeeBean.setSmartCardChrg(this.getSmartCardChrg());
        newVehicleFeeBean.setRcptNoPopup(this.getRcptNoPopup());
        newVehicleFeeBean.setActionCode(this.getActionCode());
        newVehicleFeeBean.setShowInRto(this.isShowInRto());
        newVehicleFeeBean.setCurrentDate(this.getCurrentDate());
        newVehicleFeeBean.setTempReg(this.getTempReg());
        newVehicleFeeBean.setBlnDisableRegnTypeTemp(this.isBlnDisableRegnTypeTemp());
        newVehicleFeeBean.setRenderTempregPanel(this.isRenderTempregPanel());
//        newVehicleFeeBean.setList_c_state(this.getList_c_state());
//        newVehicleFeeBean.setList_office_to(this.getList_office_to());
//        newVehicleFeeBean.setList_dealer_cd(this.getList_dealer_cd());
        newVehicleFeeBean.setOtherStateVehDobj(this.getOtherStateVehDobj());
        newVehicleFeeBean.setDisableOtherState(this.isDisableOtherState());
        newVehicleFeeBean.setRenderFeePanelLabel(this.isRenderFeePanelLabel());
        newVehicleFeeBean.setMarqueManualReceipt(this.isMarqueManualReceipt());
        newVehicleFeeBean.setRenderManualReceiptMessage(this.isRenderManualReceiptMessage());
        newVehicleFeeBean.setFEE_ACTION(this.getFEE_ACTION());
        newVehicleFeeBean.setPermitRefreshDtl(this.getPermitRefreshDtl());
        newVehicleFeeBean.setPermitTaxDtl(this.getPermitTaxDtl());
        newVehicleFeeBean.setPermitFeeDtl(this.getPermitFeeDtl());
        newVehicleFeeBean.setRenderCdPanel(this.isRenderCdPanel());
        newVehicleFeeBean.setSeriesAvailMess(this.getSeriesAvailMess());
        newVehicleFeeBean.setIsSeriesAvail(this.isIsSeriesAvail());
        newVehicleFeeBean.setFancyOrRetenRegnMess(this.getFancyOrRetenRegnMess());
        newVehicleFeeBean.setIsFancyOrReten(this.isIsFancyOrReten());
        newVehicleFeeBean.setAutoRunTaxListener(this.isAutoRunTaxListener());
        newVehicleFeeBean.setRenderModelCost(this.isRenderModelCost());
        newVehicleFeeBean.setDisableSaveButton(this.isDisableSaveButton());
        newVehicleFeeBean.setDisableRevertBackButton(this.isDisableRevertBackButton());
        newVehicleFeeBean.setRenderOnlinePayBtn(this.isRenderOnlinePayBtn());
        newVehicleFeeBean.setBtnAddToCart(this.isBtnAddToCart());
        newVehicleFeeBean.setPaymentTypeBtn(this.getPaymentTypeBtn());
        newVehicleFeeBean.setRenderCancelPayment(this.isRenderCancelPayment());
        newVehicleFeeBean.setRenderUserAndPasswored(this.isRenderUserAndPasswored());
        newVehicleFeeBean.setRenderPushBackSeatPanel(this.isRenderPushBackSeatPanel());
        newVehicleFeeBean.setIsManualReceiptRecord(this.isIsManualReceiptRecord());
        newVehicleFeeBean.setOnlineUserCredentialmsg(this.getOnlineUserCredentialmsg());
        newVehicleFeeBean.setTaxDescriptionList(this.getTaxDescriptionList());
//        newVehicleFeeBean.setList_Pincode(this.getList_Pincode());
        newVehicleFeeBean.setPinStatus(this.getPinStatus());
        newVehicleFeeBean.setConfirmDialogRenderButton(this.isConfirmDialogRenderButton());
//        newVehicleFeeBean.setsessionVariables(this.getSessionVariables());
        newVehicleFeeBean.setListTrailerDobj(this.getListTrailerDobj());
        newVehicleFeeBean.setRendertempFeeAmount(this.isRendertempFeeAmount());
        newVehicleFeeBean.setAppl_dt(this.getAppl_dt());
    }
    
    public void setFinePenaltyDetails(String applNo, String stateCode) throws VahanException {
        FeeDobj dobj = null;
        ExemptionFeeFineImplementation exemImpl = new ExemptionFeeFineImplementation();
        List<TaxExemptiondobj> taxExemList = exemImpl.getExemptionDetails(applNo, stateCode);
        for (TaxExemptiondobj exemdobj : taxExemList) {
            dobj = new FeeDobj();
            dobj.setPurCd(exemdobj.getPur_cd());
            dobj.setFineAmount(-exemdobj.getExemAmount());
            dobj.setFeeAmount(0l);
            dobj.setDisableDropDown(true);
            dobj.setReadOnlyFee(true);
            dobj.setReadOnlyFine(true);
            getFeeCollectionLists().add(dobj);
            getFeePanelBeanModel().getFeeCollectionList().add(dobj);
        }
    }
    
    public void updateTotalPayableAmount(TmConfigurationDobj tmConfigDobj, SessionVariablesModel sessionVariablesModel) {
        long finalTaxAmount = 0;
        setTotalAmountPayable(0);
        List<Integer> listPurCd = new ArrayList<>();
        boolean feeFineExem = false;
        boolean taxPenaltyExem = false;
        boolean taxInterestExem = false;
        boolean manualFeeReceipt = false;

        for (FeeDobj dobj : feeCollectionLists) {
            if (dobj.getPurCd() == TableConstants.FEE_FINE_EXEMTION) {
                feeFineExem = true;
            }
            if (dobj.getPurCd() == TableConstants.TAX_PENALTY_EXEMTION) {
                taxPenaltyExem = true;
            }
            if (dobj.getPurCd() == TableConstants.TAX_INTEREST_EXEMTION) {
                taxInterestExem = true;
            }
            if (dobj.getPurCd() == TableConstants.VM_MAST_MANUAL_RECEIPT) {
                manualFeeReceipt = true;
            }
        }
        if (feeFineExem || taxPenaltyExem || taxInterestExem || manualFeeReceipt) {
            setTotalAmountPayable(getTotalAmountPayable() + getFeePanelBeanModel().getTotalAmount());
        } else {
            if (getFeePanelBeanModel().getTotalAmount() > 0) {
                setTotalAmountPayable(getTotalAmountPayable() + getFeePanelBeanModel().getTotalAmount());
            }
        }

        if (renderTaxPanel) {
            for (TaxFormPanelBean bean : listTaxForm) {
                finalTaxAmount = finalTaxAmount + bean.getFinalTaxAmount();
                if (bean.getFinalTaxAmount() > 0) {
                    listPurCd.add(bean.getPur_cd());
                }
            }
        }

        if (EpayImplementation.getServiceChargeType(tmConfigDobj, sessionVariablesModel.getStateCodeSelected(), sessionVariablesModel.getUserCatgForLoggedInUser(), sessionVariablesModel.getSelectedWork().getPur_cd()) != null) {
            for (FeeDobj fee : getFeeCollectionLists()) {
                listPurCd.add(fee.getPurCd());
            }

            if (getSmartCardFee() != null) {
                listPurCd.add(TableConstants.VM_TRANSACTION_MAST_SMART_CARD_FEE);
            }
            VehicleParameters param = FormulaUtilities.fillVehicleParametersFromDobj(ownerDobj, sessionVariablesModel);
            param.setNEW_VCH("Y");
            Long userCharges = EpayImplementation.getUserChargesFee(ownerDobj, listPurCd, param, sessionVariablesModel, tmConfigDobj);
            setTotalUserChrg(userCharges);
            setRenderUserChargesAmountPanel(true);
        } else {
            setRenderUserChargesAmountPanel(false);
        }

        if (getSmartCardFee() != null) {
            setTotalAmountPayable(getTotalAmountPayable() + finalTaxAmount + getTotalUserChrg() + getSmartCardFee());
        } else {
            setTotalAmountPayable(getTotalAmountPayable() + finalTaxAmount + getTotalUserChrg());
        }
    }
    
    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    public OwnerDetailsDobj getOwnerDetailsDobj() {
        return ownerDetailsDobj;
    }

    public void setOwnerDetailsDobj(OwnerDetailsDobj ownerDetailsDobj) {
        this.ownerDetailsDobj = ownerDetailsDobj;
    }

    public List<TaxFormPanelBean> getListTaxForm() {
        return listTaxForm;
    }

    public void setListTaxForm(List<TaxFormPanelBean> listTaxForm) {
        this.listTaxForm = listTaxForm;
    }

    public List<TaxFormPanelBean> getListTaxFormBackup() {
        return listTaxFormBackup;
    }

    public void setListTaxFormBackup(List<TaxFormPanelBean> listTaxFormBackup) {
        this.listTaxFormBackup = listTaxFormBackup;
    }

    public List getList_vm_catg() {
        return list_vm_catg;
    }

    public void setList_vm_catg(List list_vm_catg) {
        this.list_vm_catg = list_vm_catg;
    }

    public List getList_maker_model() {
        return list_maker_model;
    }

    public void setList_maker_model(List list_maker_model) {
        this.list_maker_model = list_maker_model;
    }

    public TaxFormPanelBean getTaxBean() {
        return taxBean;
    }

    public void setTaxBean(TaxFormPanelBean taxBean) {
        this.taxBean = taxBean;
    }

    public FormFeePanelBeanModel getFeePanelBeanModel() {
        return feePanelBeanModel;
    }

    public void setFeePanelBeanModel(FormFeePanelBeanModel feePanelBeanModel) {
        this.feePanelBeanModel = feePanelBeanModel;
    }

    public PaymentCollectionBeanModel getPaymentBeanModel() {
        return paymentBeanModel;
    }

    public void setPaymentBeanModel(PaymentCollectionBeanModel paymentBeanModel) {
        this.paymentBeanModel = paymentBeanModel;
    }

    public PermitPanelBeanModel getPermitPanelBeanModel() {
        return permitPanelBeanModel;
    }

    public void setPermitPanelBeanModel(PermitPanelBeanModel permitPanelBeanModel) {
        this.permitPanelBeanModel = permitPanelBeanModel;
    }

    public long getTotalAmountPayable() {
        return totalAmountPayable;
    }

    public void setTotalAmountPayable(long totalAmountPayable) {
        this.totalAmountPayable = totalAmountPayable;
    }

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    public boolean isNewRegistration() {
        return newRegistration;
    }

    public void setNewRegistration(boolean newRegistration) {
        this.newRegistration = newRegistration;
    }

    public boolean isTempRegistration() {
        return tempRegistration;
    }

    public void setTempRegistration(boolean tempRegistration) {
        this.tempRegistration = tempRegistration;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getTaxMode() {
        return taxMode;
    }

    public void setTaxMode(String taxMode) {
        this.taxMode = taxMode;
    }

    public List getList_vh_class() {
        return list_vh_class;
    }

    public void setList_vh_class(List list_vh_class) {
        this.list_vh_class = list_vh_class;
    }

    public String getPageLoadingFailed() {
        return pageLoadingFailed;
    }

    public void setPageLoadingFailed(String pageLoadingFailed) {
        this.pageLoadingFailed = pageLoadingFailed;
    }

    public String getTaxpanel() {
        return taxpanel;
    }

    public void setTaxpanel(String taxpanel) {
        this.taxpanel = taxpanel;
    }

    public String getPrintConfDlgTax() {
        return printConfDlgTax;
    }

    public void setPrintConfDlgTax(String printConfDlgTax) {
        this.printConfDlgTax = printConfDlgTax;
    }

    public boolean isRenderTaxPanel() {
        return renderTaxPanel;
    }

    public void setRenderTaxPanel(boolean renderTaxPanel) {
        this.renderTaxPanel = renderTaxPanel;
    }

    public String getBtn_print_label() {
        return btn_print_label;
    }

    public void setBtn_print_label(String btn_print_label) {
        this.btn_print_label = btn_print_label;
    }

    public boolean isBtn_print() {
        return btn_print;
    }

    public void setBtn_print(boolean btn_print) {
        this.btn_print = btn_print;
    }

    public boolean isBtn_save() {
        return btn_save;
    }

    public void setBtn_save(boolean btn_save) {
        this.btn_save = btn_save;
    }

    public String getGeneratedRcpt() {
        return generatedRcpt;
    }

    public void setGeneratedRcpt(String generatedRcpt) {
        this.generatedRcpt = generatedRcpt;
    }

    public String getPrintRcptNo() {
        return printRcptNo;
    }

    public void setPrintRcptNo(String printRcptNo) {
        this.printRcptNo = printRcptNo;
    }

    public boolean isButtonPanelVisibility() {
        return buttonPanelVisibility;
    }

    public void setButtonPanelVisibility(boolean buttonPanelVisibility) {
        this.buttonPanelVisibility = buttonPanelVisibility;
    }

    public String getReceiptStickyNote() {
        return receiptStickyNote;
    }

    public void setReceiptStickyNote(String receiptStickyNote) {
        this.receiptStickyNote = receiptStickyNote;
    }

    public List<PaymentGatewayDobj> getAddToCartStatusCount() {
        return addToCartStatusCount;
    }

    public void setAddToCartStatusCount(List<PaymentGatewayDobj> addToCartStatusCount) {
        this.addToCartStatusCount = addToCartStatusCount;
    }

    public ManualReceiptEntryDobj getManualRcptDobjTemp() {
        return manualRcptDobjTemp;
    }

    public void setManualRcptDobjTemp(ManualReceiptEntryDobj manualRcptDobjTemp) {
        this.manualRcptDobjTemp = manualRcptDobjTemp;
    }

    public String getChasino() {
        return chasino;
    }

    public void setChasino(String chasino) {
        this.chasino = chasino;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public String getApplno() {
        return applno;
    }

    public void setApplno(String applno) {
        this.applno = applno;
    }

    public List<FeeDobj> getFeeCollectionLists() {
        return feeCollectionLists;
    }

    public void setFeeCollectionLists(List<FeeDobj> feeCollectionLists) {
        this.feeCollectionLists = feeCollectionLists;
    }

    public boolean isRenderPermitPanel() {
        return renderPermitPanel;
    }

    public void setRenderPermitPanel(boolean renderPermitPanel) {
        this.renderPermitPanel = renderPermitPanel;
    }

    public Long getUserChrg() {
        return userChrg;
    }

    public void setUserChrg(Long userChrg) {
        this.userChrg = userChrg;
    }

    public boolean isRenderUserChargesAmountPanel() {
        return renderUserChargesAmountPanel;
    }

    public void setRenderUserChargesAmountPanel(boolean renderUserChargesAmountPanel) {
        this.renderUserChargesAmountPanel = renderUserChargesAmountPanel;
    }

    public List<FeeDobj> getListTransWise() {
        return listTransWise;
    }

    public void setListTransWise(List<FeeDobj> listTransWise) {
        this.listTransWise = listTransWise;
    }

    public Long getTotalUserChrg() {
        return totalUserChrg;
    }

    public void setTotalUserChrg(Long totalUserChrg) {
        this.totalUserChrg = totalUserChrg;
    }

    public boolean isTaxExemption() {
        return taxExemption;
    }

    public void setTaxExemption(boolean taxExemption) {
        this.taxExemption = taxExemption;
    }

    public boolean isTaxInstallment() {
        return taxInstallment;
    }

    public void setTaxInstallment(boolean taxInstallment) {
        this.taxInstallment = taxInstallment;
    }

    public boolean isRenderTaxExemption() {
        return renderTaxExemption;
    }

    public void setRenderTaxExemption(boolean renderTaxExemption) {
        this.renderTaxExemption = renderTaxExemption;
    }

    public boolean isRenderSmartCardFeePanel() {
        return renderSmartCardFeePanel;
    }

    public void setRenderSmartCardFeePanel(boolean renderSmartCardFeePanel) {
        this.renderSmartCardFeePanel = renderSmartCardFeePanel;
    }

    public boolean isRenderTaxInstallment() {
        return renderTaxInstallment;
    }

    public void setRenderTaxInstallment(boolean renderTaxInstallment) {
        this.renderTaxInstallment = renderTaxInstallment;
    }

    public String getTaxInstallMode() {
        return taxInstallMode;
    }

    public void setTaxInstallMode(String taxInstallMode) {
        this.taxInstallMode = taxInstallMode;
    }

    public Long getSmartCardFee() {
        return smartCardFee;
    }

    public void setSmartCardFee(Long smartCardFee) {
        this.smartCardFee = smartCardFee;
    }

    public Long getSmartCardChrg() {
        return smartCardChrg;
    }

    public void setSmartCardChrg(Long smartCardChrg) {
        this.smartCardChrg = smartCardChrg;
    }

    public String getRcptNoPopup() {
        return rcptNoPopup;
    }

    public void setRcptNoPopup(String rcptNoPopup) {
        this.rcptNoPopup = rcptNoPopup;
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    public boolean isShowInRto() {
        return showInRto;
    }

    public void setShowInRto(boolean showInRto) {
        this.showInRto = showInRto;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public TempRegDobj getTempReg() {
        return tempReg;
    }

    public void setTempReg(TempRegDobj tempReg) {
        this.tempReg = tempReg;
    }

    public boolean isBlnDisableRegnTypeTemp() {
        return blnDisableRegnTypeTemp;
    }

    public void setBlnDisableRegnTypeTemp(boolean blnDisableRegnTypeTemp) {
        this.blnDisableRegnTypeTemp = blnDisableRegnTypeTemp;
    }

    public boolean isRenderTempregPanel() {
        return renderTempregPanel;
    }

    public void setRenderTempregPanel(boolean renderTempregPanel) {
        this.renderTempregPanel = renderTempregPanel;
    }

    public List getList_c_state() {
        return list_c_state;
    }

    public void setList_c_state(List list_c_state) {
        this.list_c_state = list_c_state;
    }

    public List getList_office_to() {
        return list_office_to;
    }

    public void setList_office_to(List list_office_to) {
        this.list_office_to = list_office_to;
    }

    public List getList_dealer_cd() {
        return list_dealer_cd;
    }

    public void setList_dealer_cd(List list_dealer_cd) {
        this.list_dealer_cd = list_dealer_cd;
    }

    public OtherStateVehDobj getOtherStateVehDobj() {
        return otherStateVehDobj;
    }

    public void setOtherStateVehDobj(OtherStateVehDobj otherStateVehDobj) {
        this.otherStateVehDobj = otherStateVehDobj;
    }

    public boolean isDisableOtherState() {
        return disableOtherState;
    }

    public void setDisableOtherState(boolean disableOtherState) {
        this.disableOtherState = disableOtherState;
    }

    public boolean isRenderFeePanelLabel() {
        return renderFeePanelLabel;
    }

    public void setRenderFeePanelLabel(boolean renderFeePanelLabel) {
        this.renderFeePanelLabel = renderFeePanelLabel;
    }

    public boolean isMarqueManualReceipt() {
        return marqueManualReceipt;
    }

    public void setMarqueManualReceipt(boolean marqueManualReceipt) {
        this.marqueManualReceipt = marqueManualReceipt;
    }

    public boolean isRenderManualReceiptMessage() {
        return renderManualReceiptMessage;
    }

    public void setRenderManualReceiptMessage(boolean renderManualReceiptMessage) {
        this.renderManualReceiptMessage = renderManualReceiptMessage;
    }

    public String getFEE_ACTION() {
        return FEE_ACTION;
    }

    public void setFEE_ACTION(String FEE_ACTION) {
        this.FEE_ACTION = FEE_ACTION;
    }

    public String getPermitRefreshDtl() {
        return permitRefreshDtl;
    }

    public void setPermitRefreshDtl(String permitRefreshDtl) {
        this.permitRefreshDtl = permitRefreshDtl;
    }

    public String getPermitTaxDtl() {
        return permitTaxDtl;
    }

    public void setPermitTaxDtl(String permitTaxDtl) {
        this.permitTaxDtl = permitTaxDtl;
    }

    public String getPermitFeeDtl() {
        return permitFeeDtl;
    }

    public void setPermitFeeDtl(String permitFeeDtl) {
        this.permitFeeDtl = permitFeeDtl;
    }

    public boolean isRenderCdPanel() {
        return renderCdPanel;
    }

    public void setRenderCdPanel(boolean renderCdPanel) {
        this.renderCdPanel = renderCdPanel;
    }

    public String getSeriesAvailMess() {
        return seriesAvailMess;
    }

    public void setSeriesAvailMess(String seriesAvailMess) {
        this.seriesAvailMess = seriesAvailMess;
    }

    public boolean isIsSeriesAvail() {
        return isSeriesAvail;
    }

    public void setIsSeriesAvail(boolean isSeriesAvail) {
        this.isSeriesAvail = isSeriesAvail;
    }

    public String getFancyOrRetenRegnMess() {
        return fancyOrRetenRegnMess;
    }

    public void setFancyOrRetenRegnMess(String fancyOrRetenRegnMess) {
        this.fancyOrRetenRegnMess = fancyOrRetenRegnMess;
    }

    public boolean isIsFancyOrReten() {
        return isFancyOrReten;
    }

    public void setIsFancyOrReten(boolean isFancyOrReten) {
        this.isFancyOrReten = isFancyOrReten;
    }

    public boolean isAutoRunTaxListener() {
        return autoRunTaxListener;
    }

    public void setAutoRunTaxListener(boolean autoRunTaxListener) {
        this.autoRunTaxListener = autoRunTaxListener;
    }

    public boolean isRenderModelCost() {
        return renderModelCost;
    }

    public void setRenderModelCost(boolean renderModelCost) {
        this.renderModelCost = renderModelCost;
    }

    public boolean isDisableSaveButton() {
        return disableSaveButton;
    }

    public void setDisableSaveButton(boolean disableSaveButton) {
        this.disableSaveButton = disableSaveButton;
    }

    public boolean isDisableRevertBackButton() {
        return disableRevertBackButton;
    }

    public void setDisableRevertBackButton(boolean disableRevertBackButton) {
        this.disableRevertBackButton = disableRevertBackButton;
    }

    public boolean isRenderOnlinePayBtn() {
        return renderOnlinePayBtn;
    }

    public void setRenderOnlinePayBtn(boolean renderOnlinePayBtn) {
        this.renderOnlinePayBtn = renderOnlinePayBtn;
    }

    public boolean isBtnAddToCart() {
        return btnAddToCart;
    }

    public void setBtnAddToCart(boolean btnAddToCart) {
        this.btnAddToCart = btnAddToCart;
    }

    public String getPaymentTypeBtn() {
        return paymentTypeBtn;
    }

    public void setPaymentTypeBtn(String paymentTypeBtn) {
        this.paymentTypeBtn = paymentTypeBtn;
    }

    public boolean isRenderCancelPayment() {
        return renderCancelPayment;
    }

    public void setRenderCancelPayment(boolean renderCancelPayment) {
        this.renderCancelPayment = renderCancelPayment;
    }

    public boolean isRenderUserAndPasswored() {
        return renderUserAndPasswored;
    }

    public void setRenderUserAndPasswored(boolean renderUserAndPasswored) {
        this.renderUserAndPasswored = renderUserAndPasswored;
    }

    public boolean isRenderPushBackSeatPanel() {
        return renderPushBackSeatPanel;
    }

    public void setRenderPushBackSeatPanel(boolean renderPushBackSeatPanel) {
        this.renderPushBackSeatPanel = renderPushBackSeatPanel;
    }

    public boolean isIsManualReceiptRecord() {
        return isManualReceiptRecord;
    }

    public void setIsManualReceiptRecord(boolean isManualReceiptRecord) {
        this.isManualReceiptRecord = isManualReceiptRecord;
    }

    public String getOnlineUserCredentialmsg() {
        return onlineUserCredentialmsg;
    }

    public void setOnlineUserCredentialmsg(String onlineUserCredentialmsg) {
        this.onlineUserCredentialmsg = onlineUserCredentialmsg;
    }

    public List<DOTaxDetail> getTaxDescriptionList() {
        return taxDescriptionList;
    }

    public void setTaxDescriptionList(List<DOTaxDetail> taxDescriptionList) {
        this.taxDescriptionList = taxDescriptionList;
    }

    public List getList_Pincode() {
        return list_Pincode;
    }

    public void setList_Pincode(List list_Pincode) {
        this.list_Pincode = list_Pincode;
    }

    public String getPinStatus() {
        return pinStatus;
    }

    public void setPinStatus(String pinStatus) {
        this.pinStatus = pinStatus;
    }

    public boolean isConfirmDialogRenderButton() {
        return confirmDialogRenderButton;
    }

    public void setConfirmDialogRenderButton(boolean confirmDialogRenderButton) {
        this.confirmDialogRenderButton = confirmDialogRenderButton;
    }

    public List<Trailer_dobj> getListTrailerDobj() {
        return listTrailerDobj;
    }

    public void setListTrailerDobj(List<Trailer_dobj> listTrailerDobj) {
        this.listTrailerDobj = listTrailerDobj;
    }

    public boolean isRendertempFeeAmount() {
        return rendertempFeeAmount;
    }

    public void setRendertempFeeAmount(boolean rendertempFeeAmount) {
        this.rendertempFeeAmount = rendertempFeeAmount;
    }

    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    public Fee_Pay_Dobj getFeePayDobj() {
        return feePayDobj;
    }

    public void setFeePayDobj(Fee_Pay_Dobj feePayDobj) {
        this.feePayDobj = feePayDobj;
    }

    public FeeDraftDobj getFeeDeaftDobj() {
        return feeDeaftDobj;
    }

    public void setFeeDeaftDobj(FeeDraftDobj feeDeaftDobj) {
        this.feeDeaftDobj = feeDeaftDobj;
    }

    public String[] getRcptNo() {
        return rcptNo;
    }

    public void setRcptNo(String[] rcptNo) {
        this.rcptNo = rcptNo;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public List<OnlinePayDobj> getPayDobj() {
        return payDobj;
    }

    public void setPayDobj(List<OnlinePayDobj> payDobj) {
        this.payDobj = payDobj;
    }

    public String getAppl_dt() {
        return appl_dt;
    }

    public void setAppl_dt(String appl_dt) {
        this.appl_dt = appl_dt;
    }

    public TmConfigurationReceipts getTmConfigurationReceipts() {
        return tmConfigurationReceipts;
    }

    public void setTmConfigurationReceipts(TmConfigurationReceipts tmConfigurationReceipts) {
        this.tmConfigurationReceipts = tmConfigurationReceipts;
    }
    
}
