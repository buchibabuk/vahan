/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.tradecert;

import java.awt.print.PrinterJob;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.print.PrintService;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.TaxUtils;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.tradecert.ApplicationFeeTradeCertDobj;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.impl.tradecert.ApplicationFeeTradeCertImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.db.TableConstants;
import nic.vahan.eapplication.OnlinePaymentImpl;
import nic.vahan.form.bean.FormFeePanelBean;
import nic.vahan.form.bean.PaymentCollectionBean;
import nic.vahan.form.bean.ReceiptMasterBean;
import nic.vahan.form.bean.TaxFormPanelBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.bean.permit.PermitPanelBean;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.FeeDobj;
import nic.vahan.form.dobj.Fee_Pay_Dobj;
import nic.vahan.form.dobj.Tax_Pay_Dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.tradecert.ApplicationTradeCertDobj;
import nic.vahan.form.impl.EpayImpl;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.tradecert.IssueTradeCertImpl;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
//import tax.web.service.VahanTaxParameters;
import nic.vahan.form.dobj.tax.VahanTaxParameters;

/**
 *
 * @author tranC081
 */
@ManagedBean(name = "applicationFeeTradeCert", eager = true)
@ViewScoped
public class ApplicationFeeTradeCertBean extends AbstractApplBean implements Serializable {

    private ApplicationFeeTradeCertDobj applicationFeeTradeCertDobj;
    private static final Logger LOGGER = Logger.getLogger(ApplicationFeeTradeCertBean.class);
    private FormFeePanelBean feePanelBean = new FormFeePanelBean();
    private final List listPaymentMode;
    private final Map mapSectionsSrNoOfSelectedApplication;
    private boolean disableTradeCertFor;
    private boolean renderDraftDetails;
    private boolean renderFeeDetails;
    private boolean renderAmountDetailsPanel;
    private final List<ApplicationFeeTradeCertDobj> applicationSectionsList;
    private final List<TaxFormPanelBean> applicationfeeList;
    private int noOfVehGrandTotal;
    private double totalFeeCollected;
    private double totalAmount;
    private double totalTaxCollected;
    private double totalFuelTax;
    private double serviceCharge;
    private double transactionCharge;
    private double totalFineCollected;
    private int totalSurcharge;
    private String printRcptNo;
    private boolean notValid;
    private boolean visibleApplicationDetailPanel;
    private boolean dissableChooseNewRenewTradeCertificateOption;
    private boolean visibleTradeCertificateApplicationPanel;
    private boolean disableSave;
    private boolean disablePrint;
    private String applNoToBeSetInVtFee;
    private PaymentCollectionBean paymentBean = new PaymentCollectionBean();
    private boolean renderUserChargesAmountPanel;
    @ManagedProperty(value = "#{rcpt_bean}")
    private ReceiptMasterBean rcpt_bean;
    private boolean duplicate;
    private String tradeCertType;
    private boolean tradeCertNoNotBlank;
    private String rcptNoPopup;
    private boolean fixCashModeForDealerAdmin;
    private String saveCaption;
    private String serviceChargeInString;
    private String transactionChargeInString;
    private String totalAmountInString;
    private int balanceTax;
    private boolean displayFuel;
    private final SessionVariables sessionvariables;
    private String miscAmount;
    private boolean renderMiscFeeAmount;
    private boolean renderTransactionCharge;
    private boolean btn_print = false;
    private boolean confirmDialogRenderButton = true;
    private String paymentTypeBtn = "";
    private long totalAmountPayable = 0;
    private List<FeeDobj> feeCollectionLists = null;
    private List<TaxFormPanelBean> listTaxForm = new ArrayList();
    private boolean renderTaxPanel = false;
    private PermitPanelBean permitPanelBean;
    private int pur_cd = 0;
    private boolean renderOnlinePayBtn = false;
    private boolean renderCancelPayment = false;
    private boolean btn_save = true;
    private boolean renderUserAndPasswored = false;
    private String appl_no;
    FeeImpl feeImpl = null;
    private List<Tax_Pay_Dobj> feePayDobjList = new ArrayList<>();
    private String paymentType;
    private String onlineUserCredentialmsg;
    private String mobileNo;
    private boolean onlinePaymentInitiated;
    private boolean doNotShowNoOfVehicles;
    private TmConfigurationDobj tmConfigDobj;
    private boolean stockTransferReq;

    public ApplicationFeeTradeCertBean() {

        applicationFeeTradeCertDobj = new ApplicationFeeTradeCertDobj();
        applicationSectionsList = new ArrayList<>();
        applicationfeeList = new ArrayList<>();
        listPaymentMode = new ArrayList();
        mapSectionsSrNoOfSelectedApplication = new HashMap();
        fixCashModeForDealerAdmin = true;
        saveCaption = "Save";
        appl_no = Util.getSelectedSeat().getAppl_no();
        sessionvariables = new SessionVariables();
        try {
            tmConfigDobj = Util.getTmConfiguration();
        } catch (VahanException ex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            return;
        }
        if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {   /// for GUJRAT FUEL-TYPE work
            displayFuel = true;
        }
        if (tmConfigDobj.getTmTradeCertConfigDobj().isNoOfVehiclesNotToBeShown()) {   /// for Chhattisgarh 
            doNotShowNoOfVehicles = true;
        }
        try {
            if (Util.getTmConfiguration() != null) {
                if (Util.getTmConfiguration().isOnlinePayment()) {
                    renderOnlinePayBtn = true;
                    boolean onlineData = new FeeImpl().getOnlinePayData(appl_no);
                    if (onlineData) {
                        onlinePaymentInitiated = true;
                        renderOnlinePayBtn = false;
                        Object[] obj = new FeeImpl().getUserIDAndPassword(appl_no);
                        renderCancelPayment = true;
                        if (obj != null && obj.length > 0) {
                            String userInfo = "Online Payment Credentials User ID is : " + appl_no + " & Password : " + obj[0];
                            onlineUserCredentialmsg = userInfo;
                            renderUserAndPasswored = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    @PostConstruct
    public void init() {

        fillLoginDetails(applicationFeeTradeCertDobj);
        reset();
        this.applicationFeeTradeCertDobj.setNewRenewalTradeCert("New_Trade_Certificate_Fee");
        this.visibleTradeCertificateApplicationPanel = true;

        fillApplicationDataFromSession();

        if (this.appl_details.getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_DUP) {
            this.duplicate = true;
        }

        if (rcpt_bean.getBook_rcpt_no() == null || rcpt_bean.getBook_rcpt_no().equals("")) {
            setRcptNoPopup("There is no Current Receipt No Assigned.");
            // RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().executeScript("PF('dlgRcptNoPopup').show()");
            return;
        }

        saveCaption = "Save";

    }

    public String getPrintRcptNo() {
        return printRcptNo;
    }

    public void setPrintRcptNo(String printRcptNo) {
        this.printRcptNo = printRcptNo;
    }

    public boolean isNotValid() {
        return notValid;
    }

    public void setNotValid(boolean notValid) {
        this.notValid = notValid;
    }

    private void fillApplicationSectionsList() {
        this.applicationSectionsList.clear();
        this.applicationfeeList.clear();
        this.noOfVehGrandTotal = 0;
        Double feeCollected = 0.0;
        Double taxCollected = 0.0;
        Double fuelTaxCollected = 0.0;
        Double fineCollected = 0.0;
        int surchargeCollected = 0;
        List<Integer> listPurCd = new ArrayList<>();
        if (!mapSectionsSrNoOfSelectedApplication.isEmpty()) {
            int index = 1;
            for (Object keyObj : mapSectionsSrNoOfSelectedApplication.keySet()) {
                String key = (String) keyObj;
                ApplicationFeeTradeCertDobj dobj = (ApplicationFeeTradeCertDobj) mapSectionsSrNoOfSelectedApplication.get(key);
                dobj.setSrNo((index++) + "");
                this.applicationSectionsList.add(dobj);
                this.noOfVehGrandTotal += Integer.valueOf(dobj.getNoOfAllowedVehicles());
                fillLoginDetails(dobj);
                setVtFeeFlag(dobj);
                Map<String, Integer> mp = calculateTotalFeewithTax(dobj);
                List<DOTaxDetail> doTaxDetailList = null;
                if (tmConfigDobj.getTmTradeCertConfigDobj().isTaxToBeCalculatedWebService()) {   //// for Gujarat and Rajasthan
                    try {
                        doTaxDetailList = callTaxService(dobj);
                    } catch (VahanException ex) {
                        LOGGER.error("Exception occured while retriving tax from tax service. [message:{" + ex.getMessage() + "}]");
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Trade certificate fee application generation status...", ex.getMessage()));
                        return;
                    }
                }
                if (mp.get("TRADE_CERT_FEE") != null) {
                    dobj.setFee1(mp.get("TRADE_CERT_FEE"));
                }

                if (mp.get("TRADE_CERT_TAX") != null) {
                    dobj.setFee2(new Double(mp.get("TRADE_CERT_TAX")));
                    try {
                        if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelTaxApplicable()) {   //// for Gujarat 
                            dobj.setFuelTax(calculateTax(doTaxDetailList));
                        } else if (tmConfigDobj.getTmTradeCertConfigDobj().isTaxToBeCalculatedWebService() && !tmConfigDobj.getTmTradeCertConfigDobj().isFuelTaxApplicable()) {  /// for Rajasthan 
                            dobj.setFee2(calculateTax(doTaxDetailList));
                        }
                        if (tmConfigDobj.getTmTradeCertConfigDobj().isStockTransferReq() && dobj.isStockTransferApplicable()) {   //// Rajasthan [for stock transfer Requirement]
                            dobj.setFee2(dobj.getFee2() / 4);
                            stockTransferReq = true;
                        }
                    } catch (VahanException ex) {
                        LOGGER.error("Exception occured while setting tax. [message:{" + ex.getMessage() + "}]");
                    }
                } else {
                    dobj.setFee2(0.0);
                }

                if (mp.get("TRADE_CERT_SURCHARGE") != null) {
                    dobj.setSurcharge(new Double(mp.get("TRADE_CERT_SURCHARGE")));
                    if (tmConfigDobj.getTmTradeCertConfigDobj().isSurchargeToBeCalculatedViaWebService()) {   //// for Rajasthan
                        try {
                            dobj.setSurcharge(calculateSurcharge(doTaxDetailList));
                        } catch (VahanException ex) {
                            LOGGER.error("Exception occured while setting tax. [message:{" + ex.getMessage() + "}]");
                        }
                    }
                } else {
                    dobj.setSurcharge(0.0);
                }

                int noOfVehicles = Integer.valueOf(dobj.getNoOfAllowedVehicles());
                int noOfIllegitimateCertificates = 0;
                if (tmConfigDobj.getTmTradeCertConfigDobj().isIllegitimateTradeCertApplicable() && tradeCertType.equalsIgnoreCase("RENEW") && !CommonUtils.isNullOrBlank(dobj.getExtraVehiclesSoldLastYr())) {   /// for Odisha
                    noOfIllegitimateCertificates = Integer.valueOf(dobj.getExtraVehiclesSoldLastYr());
                }

                Double fee = 0.0;
                Double tax = 0.0;
                Double fuelTax = 0.0;
                if ((Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_NEW) && !this.applicationFeeTradeCertDobj.getStateCd().equalsIgnoreCase("RJ")) {   ////NEW , RENEW (per vehicle)

                    /////////////////////////// Calculating fees /////////////////////////////////
                    if (tmConfigDobj.getTmTradeCertConfigDobj().isIllegitimateTradeCertApplicable()) { // for Odisha
                        if (noOfIllegitimateCertificates < 0) {
                            fee = Double.valueOf(dobj.getFee1() * noOfVehicles);
                        } else {
                            fee = Double.valueOf(dobj.getFee1() * (noOfIllegitimateCertificates + noOfVehicles));
                        }
                    } else {
                        fee = Double.valueOf(dobj.getFee1() * noOfVehicles);
                    }
                    ///////////////////////// Calculating tax ////////////////////////////////////

                    if (tmConfigDobj.getTmTradeCertConfigDobj().isYardFeeApplicable()) {  ///// for punjab 

                        String[] vehCatgArrFor0to100Slab = {"2WIC", "2WN", "2WT"};
                        List vehCatgListFor0to100Slab = Arrays.asList(vehCatgArrFor0to100Slab);
                        if (vehCatgListFor0to100Slab.contains(dobj.getVehCatgFor())) {
                            int modVal = noOfVehicles % 100;
                            int multiplicativeVal = (int) noOfVehicles / 100;
                            if (modVal == 0) {
                                tax = Double.valueOf(multiplicativeVal * dobj.getFee2());
                            } else if (modVal > 0) {
                                tax = Double.valueOf((multiplicativeVal + 1) * dobj.getFee2());
                            }
                        } else {
                            int modVal = noOfVehicles % 50;
                            int multiplicativeVal = (int) noOfVehicles / 50;
                            if (modVal == 0) {
                                tax = Double.valueOf(multiplicativeVal * dobj.getFee2());
                            } else if (modVal > 0) {
                                tax = Double.valueOf((multiplicativeVal + 1) * dobj.getFee2());
                            }
                        }
                    } else if (tmConfigDobj.getTmTradeCertConfigDobj().isIllegitimateTradeCertApplicable()
                            && tmConfigDobj.getTmTradeCertConfigDobj().isTaxApplicableForIllegitimateTradeCert()) { /// for odisha
                        if (noOfIllegitimateCertificates < 0
                                || TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER.equalsIgnoreCase(dobj.getApplicantCategory())) {
                            tax = Double.valueOf(dobj.getFee2() * noOfVehicles);
                        } else {
                            tax = Double.valueOf(dobj.getFee2() * (noOfIllegitimateCertificates + noOfVehicles));
                        }
                    } else { ///////// all other states 
                        tax = Double.valueOf(dobj.getFee2() * noOfVehicles);
                    }
                    if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelTaxApplicable()) {   /// for GUJRAT FUEL-TYPE work
                        fuelTax = dobj.getFuelTax() * noOfVehicles;
                    }

                } else if (tmConfigDobj.getTmTradeCertConfigDobj().isTaxToBeCalculatedWebService()
                        && !tmConfigDobj.getTmTradeCertConfigDobj().isFuelTaxApplicable()) {        /// Rajasthan (new or renew cases)
                    fee = Double.valueOf(dobj.getFee1());
                    tax = Double.valueOf(dobj.getFee2());
                } else {  ///////// DUPLICATE  (per certificate)  .......... all states
                    fee = Double.valueOf(dobj.getFee1());
                    dobj.setFee2(0.0);
                    tax = Double.valueOf(dobj.getFee2());
                }

                fuelTaxCollected += fuelTax;
                feeCollected += fee;
                taxCollected += tax;

                //////////////////////////////// Calculating surcharge ////////////////////////////////////
                int surcharg = 0;
                if (dobj.getSurcharge() != null) {
                    surcharg = (int) (double) dobj.getSurcharge();
                }

                surchargeCollected = surcharg + surchargeCollected;
                dobj.setSurcharge(new Double(surcharg));
                dobj.setFeeCollected(String.valueOf(fee).split("\\.")[0]);
                dobj.setTaxCollected(String.valueOf(Integer.valueOf(String.valueOf(tax).split("\\.")[0]) + fuelTax));
                dobj.setFuelTaxCollected(fuelTax);
                //  fineCollected += calculateTotalFine(dobj);   ///// FINE COLLECTED THROUGH UI
                int purcd = dobj.getPurCd() == null || dobj.getPurCd().equals("") ? 0 : Integer.parseInt(dobj.getPurCd());
                listPurCd.add(purcd);

                try {
                    Object[] manualFeeReceiptDtlsArr = ApplicationFeeTradeCertImpl.getManualFeeReceiptDtls(dobj.getApplNo());
                    if (manualFeeReceiptDtlsArr != null && manualFeeReceiptDtlsArr.length > 0) {
                        dobj.setManualFeeReceiptAmount((Integer) manualFeeReceiptDtlsArr[0]);
                        dobj.setManualFeeReceiptNumber((String) manualFeeReceiptDtlsArr[1]);
                        try {
                            dobj.setManualFeeReceiptDate(JSFUtils.getDateInDD_MMM_YYYY(DateUtils.getDateInDDMMYYYY((Date) manualFeeReceiptDtlsArr[2])));
                        } catch (ParseException ex) {
                            LOGGER.error("Exception occured while formatting manual fee receipt date. [message:{" + ex.getMessage() + "}]");
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Trade certificate fee application generation status...", ex.getMessage()));
                            return;
                        }
                        dobj.setManualFeeReceiptRemark(dobj.getManualFeeReceiptAmount(), dobj.getManualFeeReceiptNumber(), dobj.getManualFeeReceiptDate());
                    }
                } catch (VahanException ex) {
                    LOGGER.error("Exception occured while retriving manual fee receipt amount. [message:{" + ex.getMessage() + "}]");
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Trade certificate fee application generation status...", ex.getMessage()));
                    return;
                }
            }

            //////////////////////////////// Calculating service charge ////////////////////////////////////
            if (EpayImpl.getServiceChargeType() != null) {
                VehicleParameters parameters = FormulaUtils.fillVehicleParametersFromDobj(null);
                parameters.setPUR_CD(Util.getSelectedSeat().getPur_cd());
                Long userCharges = EpayImpl.getUserChargesFee(null, listPurCd, parameters);
                if (tmConfigDobj.getTmTradeCertConfigDobj().isServiceChargeMultiplyByNoOfTradeCert()) {  //// for Tamil Nadu
                    this.serviceCharge = userCharges * noOfVehGrandTotal;
                } else {
                    this.serviceCharge = userCharges;
                }
                setRenderUserChargesAmountPanel(true);
            } else {
                setRenderUserChargesAmountPanel(false);

                if (tmConfigDobj.getTmTradeCertConfigDobj().isServiceChargeHardCoded()) {   //// for Chandigarh
                    this.serviceCharge = 30 * noOfVehGrandTotal;
                    setRenderUserChargesAmountPanel(true);
                }
            }

            try {
                this.transactionCharge = ApplicationFeeTradeCertImpl.getTransactionFeeDetail();
                this.renderTransactionCharge = true;
            } catch (VahanException ex) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Problem occurred while fetching transaction fee. " + TableConstants.SomthingWentWrong, "Problem occurred while fetching transaction fee. " + TableConstants.SomthingWentWrong);
                FacesContext.getCurrentInstance().addMessage(null, message);
            }

            fillApplicationSectionsListBlock1(feeCollected, taxCollected, fuelTaxCollected, fineCollected, surchargeCollected);
            this.serviceChargeInString = String.valueOf(this.serviceCharge).split("\\.")[0];
            //Amit Nic Lko
            this.transactionChargeInString = String.valueOf(this.transactionCharge).split("\\.")[0];
            this.totalAmountInString = String.valueOf(this.totalAmount).split("\\.")[0];
            dissableChooseNewRenewTradeCertificateOption = true;
            disableSave = false;
            renderAmountDetailsPanel = true;
        } else {
            dissableChooseNewRenewTradeCertificateOption = false;
            disableSave = true;
            renderAmountDetailsPanel = false;
        }
    }

    private List<DOTaxDetail> callTaxService(ApplicationFeeTradeCertDobj dobj) throws VahanException {
        VahanTaxParameters vehicleTaxParameters = fillTaxParametersFromDobj(dobj);
        return TaxServer_Impl.callTaxService(vehicleTaxParameters);
    }

    private Double calculateTax(List<DOTaxDetail> doTaxDetailList) throws VahanException {
        return retrieveTax(doTaxDetailList, "T");
    }

    private Double calculateSurcharge(List<DOTaxDetail> doTaxDetailList) throws VahanException {
        return retrieveTax(doTaxDetailList, "S");
    }

    private Double retrieveTax(List<DOTaxDetail> doTaxDetailList, String type) throws VahanException {
        Double value = 0.0;
        if (doTaxDetailList != null) {
            doTaxDetailList = TaxUtils.sortTaxDetails(doTaxDetailList);
            for (DOTaxDetail doTax : doTaxDetailList) {
                if ("T".equals(type)) {   //// Tax
                    value = (value + doTax.getAMOUNT());
                } else if ("S".equals(type)) {
                    value = (value + doTax.getSURCHARGE());
                }
            }
        }
        return value;
    }

    private void fillApplicationSectionsListBlock1(Double feeCollected, Double taxCollected, Double fuelTaxCollected, Double fineCollected, int surchargeCollected) {

        this.applicationFeeTradeCertDobj = (ApplicationFeeTradeCertDobj) mapSectionsSrNoOfSelectedApplication.get("1");
        this.totalAmount = feeCollected + this.serviceCharge + taxCollected + surchargeCollected + fuelTaxCollected + this.transactionCharge;
        if (this.applicationFeeTradeCertDobj.getManualFeeReceiptAmount() != 0) {
            this.totalAmount = this.totalAmount - this.applicationFeeTradeCertDobj.getManualFeeReceiptAmount();
        }
        this.totalFeeCollected = feeCollected + this.serviceCharge;
        this.totalTaxCollected = taxCollected;
        this.totalFuelTax = fuelTaxCollected;
        this.totalFineCollected = fineCollected;
        this.totalSurcharge = surchargeCollected;
        this.applicationFeeTradeCertDobj.setPaymentMode("C");
        if (tmConfigDobj.getTmTradeCertConfigDobj().isFeeToBeHardCoded()) {   /// for Chhattisgarh
            int countVegCatg = 0;
            for (Object keyObj : mapSectionsSrNoOfSelectedApplication.keySet()) {
                String key = (String) keyObj;
                ApplicationFeeTradeCertDobj dobj = (ApplicationFeeTradeCertDobj) mapSectionsSrNoOfSelectedApplication.get(key);
                dobj.setFeeCollected("200");
                countVegCatg++;
            }
            this.totalFeeCollected = 200.0 * countVegCatg;
            this.totalAmount = this.totalFeeCollected;
        }
    }

    private Map<String, Integer> calculateTotalFeewithTax(ApplicationFeeTradeCertDobj applicationFeeTradeCertDobj) {
        ApplicationFeeTradeCertImpl applicationFeeTradeCertImpl = new ApplicationFeeTradeCertImpl();

        Map<String, Integer> mp = null;
        try {
            if (tmConfigDobj.getTmTradeCertConfigDobj().isFeeAsPerVehicleClass()) {   //// for ODISHA
                mp = applicationFeeTradeCertImpl.getFeeDetailOR(this.applicationFeeTradeCertDobj.getStateCd(), this.applicationFeeTradeCertDobj.getPurCd(), applicationFeeTradeCertDobj.getVehCatgFor());
            } else {
                mp = applicationFeeTradeCertImpl.getFeeDetail(this.applicationFeeTradeCertDobj.getStateCd(), this.applicationFeeTradeCertDobj.getPurCd(), applicationFeeTradeCertDobj.getVehCatgFor());
                if (mp == null || mp.isEmpty()) {   /// change purpose code from '51' (dealerAdmin) to '21'(new/renew)
                    mp = applicationFeeTradeCertImpl.getFeeDetail(this.applicationFeeTradeCertDobj.getStateCd(), TableConstants.VM_TRANSACTION_TRADE_CERT_NEW + "", applicationFeeTradeCertDobj.getVehCatgFor());
                }
            }
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return mp;
    }

    public void reset() {
        mapSectionsSrNoOfSelectedApplication.clear();
        resetPaymentMode();
        this.noOfVehGrandTotal = 0;
        balanceTax = 0;
        applicationSectionsList.clear();
        visibleApplicationDetailPanel = false;
        visibleTradeCertificateApplicationPanel = false;
        dissableChooseNewRenewTradeCertificateOption = false;
        renderFeeDetails = false;
        renderDraftDetails = false;
        renderAmountDetailsPanel = false;
        disableSave = true;
        disablePrint = true;
        if (this.applicationFeeTradeCertDobj != null) {
            this.applicationFeeTradeCertDobj.reset();
        }
        duplicate = false;
        tradeCertType = "";
        tradeCertNoNotBlank = false;
        setMiscAmount("");
        renderMiscFeeAmount = false;
        renderTransactionCharge = false;
        stockTransferReq = false;
    }

    private void resetPaymentMode() {
        listPaymentMode.clear();
        listPaymentMode.add(new SelectItem("-1", "Select"));
        listPaymentMode.add(new SelectItem("C", "Cash"));
        listPaymentMode.add(new SelectItem("D", "Draft"));
        listPaymentMode.add(new SelectItem("Q", "Cheque"));
        listPaymentMode.add(new SelectItem("L", "Challan"));

    }

    public String back() {
        reset();
        return "home";
    }

    public void validateForm(String payType) {
        this.paymentType = payType;
        FacesMessage message = null;
        notValid = false;

        if (Utility.isNullOrBlank(this.applicationFeeTradeCertDobj.getPaymentMode())
                || "-1".equalsIgnoreCase(this.applicationFeeTradeCertDobj.getPaymentMode())) {

            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select 'payment mode' in which payment for the trade certificate has to be made.", "Please select 'payment mode' in which payment for the trade certificate has to be made.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            notValid = true;
        }
        if (!getPaymentBean().getPayment_mode().equals("C")) {
            for (PaymentCollectionDobj payDobj : getPaymentBean().getPaymentlist()) {
                if (!(notValid = payDobj.validateDobj())) {
                    message = new FacesMessage(FacesMessage.SEVERITY_WARN, payDobj.getValidationMessage(), payDobj.getValidationMessage());
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    notValid = true;
                    break;
                } else {
                    notValid = false;
                }
            }

        }
        if (!notValid) {
            //modified according to balanceTax
            calculateBalanceAmount(this.getTotalAmount() + fetchTotalFineCollection() + balanceTax);
        }

        if (!notValid) {
            // RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("new_trade_cert_application_fee_subview:confirmationPopup");
            PrimeFaces.current().executeScript("PF('confDlgTradeCert').show()");
        }

    }

    private void validateForMiscHead(double grandTotalCollected, double totalInstrumentAmount) {
        if (totalInstrumentAmount > grandTotalCollected) {
            int difference = (int) (totalInstrumentAmount - grandTotalCollected);
            this.setMiscAmount(difference + "");
            this.renderMiscFeeAmount = true;
            PrimeFaces.current().ajax().update("new_trade_cert_application_fee_subview:amount_details");
        }
    }

    public void calculateBalanceAmount(double totalAmount) {
        double totalBal = 0;
        for (PaymentCollectionDobj dobj : getPaymentBean().getPaymentlist()) {
            if (dobj.getAmount() != null) {
                totalBal = totalBal + Double.parseDouble(dobj.getAmount());
            }
        }
        if ((totalAmount - totalBal) < 0) {
            getPaymentBean().setBalanceAmount(0);
            validateForMiscHead(totalAmount, totalBal);
        } else {
            double amount = totalAmount - totalBal;
            long balance = (long) amount;
            getPaymentBean().setBalanceAmount(balance);

        }
        getPaymentBean().setAmountInCash((long) totalBal);
    }

    public void resetMiscFee() {
        setMiscAmount("");
        this.renderMiscFeeAmount = false;
    }

    public String save() {

        long checkTotalAmount = 0L;
        String returnLocation = "";
        ApplicationFeeTradeCertImpl applicationFeeTradeCertImpl = null;
        String rcptNo[] = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");

        //modification according to balance tax
        totalTaxCollected = totalTaxCollected + balanceTax;
        try {
            if (notValid) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "There are some problems in saving fee collection data for trade certificate application section.", "There are some problems to save fee collection data for trade certificate application section.");
                FacesContext.getCurrentInstance().addMessage(null, message);

            } else {
                applicationFeeTradeCertImpl = new ApplicationFeeTradeCertImpl();
                settingDataInCurrentDobj();
                FeeDraftDobj feeDraftDobj = null;

                if ("cash".equals(paymentType)) {
                    if (!"C".equals(this.paymentBean.getPayment_mode())) {   ////// Mix Mode

                        int totalPaymentAmountThroughInstruments = 0;

                        int feeCollectedInt = Integer.valueOf(this.applicationFeeTradeCertDobj.getFeeCollected()) + Integer.valueOf(this.applicationFeeTradeCertDobj.getTaxCollected());
                        for (PaymentCollectionDobj paymentCollectionDobj : paymentBean.getPaymentlist()) {
                            totalPaymentAmountThroughInstruments += Long.valueOf(paymentCollectionDobj.getAmount());
                        }
                        feeDraftDobj = new FeeDraftDobj();
                        feeDraftDobj.setFlag("A");
                        feeDraftDobj.setAppl_no(this.applicationFeeTradeCertDobj.getApplNo());
                        feeDraftDobj.setCollected_by(Util.getEmpCode());
                        feeDraftDobj.setState_cd(Util.getUserStateCode());
                        feeDraftDobj.setOff_cd(String.valueOf(Util.getUserOffCode()));
                        feeDraftDobj.setDraftPaymentList(getPaymentBean().getPaymentlist());
                        if (feeCollectedInt > totalPaymentAmountThroughInstruments) {
                            this.applicationFeeTradeCertDobj.setCashAmtForApplRcptMapping((long) (feeCollectedInt - totalPaymentAmountThroughInstruments));
                            //      feeDraftDobj.setExcess_amount(0);
                        } else {
                            // feeDraftDobj.setExcess_amount(totalPaymentAmountThroughInstruments - feeCollectedInt);
                            this.applicationFeeTradeCertDobj.setCashAmtForApplRcptMapping(0L);
                        }
                        this.applicationFeeTradeCertDobj.setFeeDraftDobj(feeDraftDobj);

                    } else {
                        if (tmConfigDobj.isNoCashPayment()) {
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cash payment not allowed for " + this.applicationFeeTradeCertDobj.getStateName() + " state.", "Cash payment not allowed for " + this.applicationFeeTradeCertDobj.getStateName() + " state.");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            return "";
                        }
                    }
                }

                this.applicationFeeTradeCertDobj.setServiceCharge(this.serviceCharge);
                if (this.renderMiscFeeAmount && !CommonUtils.isNullOrBlank(this.miscAmount)) {
                    this.applicationFeeTradeCertDobj.setMiscFee(Double.valueOf(this.miscAmount));
                }
                if (this.renderTransactionCharge && !CommonUtils.isNullOrBlank(this.transactionChargeInString)) {
                    this.applicationFeeTradeCertDobj.setTransactionFee(Double.valueOf(this.transactionChargeInString));
                }
                this.applicationFeeTradeCertDobj.setSurcharge(new Double(totalSurcharge));
                this.applicationFeeTradeCertDobj.setServiceChargeToBeRendered(this.renderUserChargesAmountPanel);
                this.applicationFeeTradeCertDobj.setFeeCollected(String.valueOf(totalFeeCollected - this.serviceCharge).split("\\.")[0]);
                this.applicationFeeTradeCertDobj.setTaxCollected((Integer.valueOf(String.valueOf(totalTaxCollected).split("\\.")[0]) + Integer.valueOf(String.valueOf(this.totalFuelTax).split("\\.")[0])) + "");
                this.applicationFeeTradeCertDobj.setFineCollected(String.valueOf(fetchTotalFineCollection()).split("\\.")[0]);
                this.applicationFeeTradeCertDobj.setPaymentMode(this.paymentBean.getPayment_mode());

                Fee_Pay_Dobj feePayDobj = new Fee_Pay_Dobj();

                List<FeeDobj> feeDobjs = new ArrayList<>();
                int[] purCdArrForFee = {Integer.valueOf(this.applicationFeeTradeCertDobj.getPurCd()), TableConstants.VM_TRANSACTION_MAST_USER_CHARGES};
                for (int purCd : purCdArrForFee) {
                    FeeDobj feeDobj = new FeeDobj();
                    if (TableConstants.VM_TRANSACTION_MAST_USER_CHARGES == purCd) {
                        long serviceChargeInLong = Long.valueOf(serviceChargeInString);
                        if (serviceChargeInLong == 0L) {
                            continue;
                        }
                        feeDobj.setFeeAmount(serviceChargeInLong);
                        feeDobj.setFineAmount(0L);
                    } else {
                        long feeAmountInLong = Long.valueOf(this.applicationFeeTradeCertDobj.getFeeCollected());
                        long fineAmountInLong = Long.valueOf(this.applicationFeeTradeCertDobj.getFineCollected());
                        if (feeAmountInLong == 0L) {
                            continue;
                        }
                        feeDobj.setFeeAmount(feeAmountInLong);
                        feeDobj.setFineAmount(fineAmountInLong);
                    }
                    feeDobj.setPurCd(purCd);
                    feeDobjs.add(feeDobj);
                }
                feePayDobj.setCollectedFeeList(feeDobjs);

                List<Tax_Pay_Dobj> tax_Pay_Dobjs = new ArrayList<>();
                int[] purCdArrForTax = {TableConstants.TRADE_CERTIFICATE_TAX, TableConstants.TRADE_CERTIFICATE_SURCHARGE};
                for (int purCd : purCdArrForTax) {
                    Tax_Pay_Dobj tax_Pay_Dobj = new Tax_Pay_Dobj();
                    String taxAmountString = String.valueOf(this.applicationFeeTradeCertDobj.getTaxCollected()).split("\\.")[0];
                    if (TableConstants.TRADE_CERTIFICATE_SURCHARGE == purCd) {
                        long surchargeInLong = Long.valueOf(totalSurcharge);
                        if (surchargeInLong == 0L) {
                            continue;
                        }
                        tax_Pay_Dobj.setFinalTaxAmount(surchargeInLong);
                    } else {
                        long taxAmountInLong = Long.valueOf(taxAmountString);
                        if (taxAmountInLong == 0L) {
                            continue;
                        }
                        tax_Pay_Dobj.setFinalTaxAmount(taxAmountInLong);
                    }
                    tax_Pay_Dobj.setTotalPaybaleSurcharge(0);
                    if (TableConstants.TRADE_CERTIFICATE_TAX == purCd) {
                        tax_Pay_Dobj.setTotalPaybaleTax(Long.valueOf(taxAmountString));
                    } else {
                        tax_Pay_Dobj.setTotalPaybaleTax(totalSurcharge);
                    }
                    tax_Pay_Dobj.setFinalTaxFrom(sdf.format(new Date()));
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    cal.add(Calendar.YEAR, 1);
                    Date taxPayDobjUptoDate = cal.getTime();
                    tax_Pay_Dobj.setFinalTaxUpto(sdf.format(taxPayDobjUptoDate));
                    tax_Pay_Dobj.setTaxMode("Y");
                    tax_Pay_Dobj.setPur_cd(purCd);
                    tax_Pay_Dobjs.add(tax_Pay_Dobj);
                }

                feePayDobj.setListTaxDobj(tax_Pay_Dobjs);
                FeeImpl feeImpl = new FeeImpl();
                FeeImpl.PaymentGenInfo paymentGenInfo = feeImpl.getPaymentInfo(feePayDobj, feeDraftDobj);
                this.applicationFeeTradeCertDobj.setPaymentGenInfo(paymentGenInfo);
                if (getPaymentBean().getBalanceAmount() != 0) {
                    this.applicationFeeTradeCertDobj.getPaymentGenInfo().setCashAmt(getPaymentBean().getBalanceAmount());
                }
                /////////////////////////////////////////////////////////////////////////

                String saveReturn = "";

                if ("cash".equals(paymentType)) {
                    saveReturn = applicationFeeTradeCertImpl.save(this.applicationFeeTradeCertDobj, null);

                    if (saveReturn.contains("SUCCESS")) {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Receipt No: '" + applicationFeeTradeCertDobj.getReceiptNumber() + "' successfully generated after collection of the fee for the trade certificate application.", "Receipt No: '" + applicationFeeTradeCertDobj.getReceiptNumber() + "' successfully generated after collection of the fee for the trade certificate application.");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        postSaveOperation(saveReturn);
                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", applicationFeeTradeCertDobj.getReceiptNumber());
                        rcpt_bean.reset();
                        return "PrintCashReceiptReport";
                    } else {
                        rcpt_bean.reset();
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "There are some problems in saving fee collection data for trade certificate application section due to '" + saveReturn.split(":")[1] + "'", "There are some problems in saving fee collection data for trade certificate application section due to '" + saveReturn.split(":")[1] + "'");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                    }
                } else if ("onlinePayment".equals(paymentType)) {

                    if (CommonUtils.isNullOrBlank(this.mobileNo)
                            || (!CommonUtils.isNullOrBlank(this.mobileNo) && this.mobileNo.length() < 10)) {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter valid 10 digits mobile number to send user-credentials for online payment.", "Please enter valid 10 digits mobile number to send user-credentials for online payment.");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        return "";
                    }

                    if (feePayDobj.getCollectedFeeList() != null && !feePayDobj.getCollectedFeeList().isEmpty()) {
                        for (FeeDobj dobj : feePayDobj.getCollectedFeeList()) {
                            Long finalFeeAmount = dobj.getTotalAmount();
                            checkTotalAmount = checkTotalAmount + finalFeeAmount;
                        }
                    }

                    List<Tax_Pay_Dobj> taxList = feePayDobj.getListTaxDobj();
                    if (taxList != null && !taxList.isEmpty()) {
                        for (Tax_Pay_Dobj taxdobj : taxList) {
                            Long finalTaxAmount = taxdobj.getFinalTaxAmount();
                            checkTotalAmount = checkTotalAmount + finalTaxAmount;
                        }
                    }
                    String userPwd = feeImpl.saveOnlinePaymentData(feePayDobj, checkTotalAmount, appl_no, Long.valueOf(getMobileNo()), this.applicationFeeTradeCertDobj.getStateCd(), this.applicationFeeTradeCertDobj.getOffCd(), sessionvariables.getEmpCodeLoggedIn(), null, null, null, false, false);
                    if (userPwd != null) {
                        String userInfo = "Online Payment Credentials User ID : " + appl_no + " & Password :  " + userPwd;
                        setOnlineUserCredentialmsg(userInfo);
                        setRenderUserAndPasswored(true);
                        setRenderCancelPayment(true);
                        setRenderOnlinePayBtn(false);
                        PrimeFaces.current().ajax().update("new_trade_cert_application_fee_subview:onlinePaymentdialog");
                        PrimeFaces.current().executeScript("PF('onlinePaymentvar').show();");
                    } else {
                        throw new VahanException("Error occured during saving of details for online payment...");
                    }
                }

            }
        } catch (VahanException ve) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "There are some problems in saving fee collection data for trade certificate application section due to " + ve.getMessage(), "There are some problems in saving fee collection data for trade certificate application section due to " + ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert", TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return returnLocation;

    }

    private void settingDataInCurrentDobj() {
        this.applicationFeeTradeCertDobj.setFeeCollected(String.valueOf(totalFeeCollected).split("\\.")[0]);
        this.applicationFeeTradeCertDobj.setTaxCollected(String.valueOf(totalTaxCollected).split("\\.")[0]);
        this.applicationFeeTradeCertDobj.setFineCollected(String.valueOf(totalFineCollected).split("\\.")[0]);
        this.applicationFeeTradeCertDobj.setApplNo(applNoToBeSetInVtFee);
    }

    private void postSaveOperation(String saveReturn) {

        String receiptNo = saveReturn.substring(saveReturn.lastIndexOf(":") + 1);
        this.applicationFeeTradeCertDobj.setReceiptNumber(receiptNo);
        this.disableSave = true;
        disablePrint = false;

        PrimeFaces.current().ajax().update("new_trade_cert_application_fee_subview:appl_no_pnl");
        PrimeFaces.current().ajax().update("new_trade_cert_application_fee_subview:amount_details");
    }

    public ApplicationFeeTradeCertDobj getApplicationFeeTradeCertDobj() {
        return applicationFeeTradeCertDobj;
    }

    //Note: Change this to proper usage using Util.getSelectedSeat
    private void fillLoginDetails(ApplicationFeeTradeCertDobj dobj) {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        dobj.setPurCd(String.valueOf(this.appl_details.getPur_cd()));
        dobj.setEmpCd((Long) sessionMap.get("emp_cd"));
        dobj.setOffCd(Util.getSelectedSeat().getOff_cd());
        dobj.setStateCd((String) sessionMap.get("state_cd"));
        dobj.setStateName((String) sessionMap.get("state_name"));
        dobj.setEmpName((String) sessionMap.get("emp_name"));
        dobj.setDesigName((String) sessionMap.get("desig_name"));

    }

    private void setVtFeeFlag(ApplicationFeeTradeCertDobj applicationFeeTradeCertDobj) {
        String chooseNewRenewTradeCertOption = this.applicationFeeTradeCertDobj.getNewRenewalTradeCert();
        if ("New_Trade_Certificate_Fee".equals(chooseNewRenewTradeCertOption)) {
            applicationFeeTradeCertDobj.setFlag("N");
        } else {
            applicationFeeTradeCertDobj.setFlag("R");
        }
    }

    public boolean isDissableChooseNewRenewTradeCertificateOption() {
        return dissableChooseNewRenewTradeCertificateOption;
    }

    public boolean isVisibleTradeCertificateApplicationPanel() {
        return visibleTradeCertificateApplicationPanel;
    }

    public List<ApplicationFeeTradeCertDobj> getApplicationSectionsList() {
        return applicationSectionsList;
    }

    public int getNoOfVehGrandTotal() {
        return noOfVehGrandTotal;
    }

    public boolean isVisibleApplicationDetailPanel() {
        return visibleApplicationDetailPanel;
    }

    public void setApplicationFeeTradeCertDobj(ApplicationFeeTradeCertDobj applicationFeeTradeCertDobj) {
        if (!mapSectionsSrNoOfSelectedApplication.isEmpty()) {
            this.applicationFeeTradeCertDobj = (ApplicationFeeTradeCertDobj) mapSectionsSrNoOfSelectedApplication.get("1");
        } else {
            this.applicationFeeTradeCertDobj = applicationFeeTradeCertDobj;
        }

    }

    public List getListPaymentMode() {
        return listPaymentMode;
    }

    public boolean isDisableTradeCertFor() {
        return disableTradeCertFor;
    }

    public boolean isRenderDraftDetails() {
        return renderDraftDetails;
    }

    public boolean isRenderFeeDetails() {
        return renderFeeDetails;
    }

    public boolean isRenderAmountDetailsPanel() {
        return renderAmountDetailsPanel;
    }

    public boolean isDisableSave() {
        return disableSave;
    }

    public double getTotalFeeCollected() {
        return totalFeeCollected;
    }

    public double getTotalFineCollected() {
        return totalFineCollected;
    }

    public FormFeePanelBean getFeePanelBean() {
        return feePanelBean;
    }

    public void setFeePanelBean(FormFeePanelBean feePanelBean) {
        this.feePanelBean = feePanelBean;
    }

    public PaymentCollectionBean getPaymentBean() {
        return paymentBean;
    }

    public void setPaymentBean(PaymentCollectionBean paymentBean) {
        this.paymentBean = paymentBean;
    }

    private void fillApplicationDataFromSession() {
        SeatAllotedDetails seatAllotedDetails = Util.getSelectedSeat();
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        this.applicationFeeTradeCertDobj.setApplNo(seatAllotedDetails.getAppl_no());

        if (!"-1".equals(this.applicationFeeTradeCertDobj.getApplNo())) {
            dissableChooseNewRenewTradeCertificateOption = true;
            renderFeeDetails = false;
            renderDraftDetails = false;
            renderAmountDetailsPanel = false;
            mapSectionsSrNoOfSelectedApplication.clear();
            ApplicationFeeTradeCertImpl applicationFeeTradeCertImpl = new ApplicationFeeTradeCertImpl();
            IssueTradeCertImpl issueTradeCertImpl = new IssueTradeCertImpl();
            try {
                applicationFeeTradeCertImpl.getAllSrNoForSelectedApplication(this.applicationFeeTradeCertDobj.getApplNo(), mapSectionsSrNoOfSelectedApplication);
            } catch (VahanException ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
            if (!mapSectionsSrNoOfSelectedApplication.isEmpty()) {
                for (Object keyObj : mapSectionsSrNoOfSelectedApplication.keySet()) {
                    ApplicationFeeTradeCertDobj applicationFeeTradeCertDobjFrom = (ApplicationFeeTradeCertDobj) mapSectionsSrNoOfSelectedApplication.get(keyObj);
                    this.applicationFeeTradeCertDobj.setDealerFor(applicationFeeTradeCertDobjFrom.getDealerFor());
                    this.applicationFeeTradeCertDobj.setDealerName(applicationFeeTradeCertDobjFrom.getDealerName());
                    applNoToBeSetInVtFee = this.applicationFeeTradeCertDobj.getApplNo();
                    paymentBean.setPayment_mode(getPaymentBean().getPayment_mode());

                    try {

                        String tradeCertNo = issueTradeCertImpl.getTradeCertNo(applicationFeeTradeCertDobjFrom.getVehCatgFor(), applicationFeeTradeCertDobjFrom.getDealerFor(), applicationFeeTradeCertDobjFrom.getFuelTypeFor());
                        applicationFeeTradeCertDobjFrom.setTradeCertNo(tradeCertNo);

                        Date validUptoDate = issueTradeCertImpl.getValidUptoOnTradeCertNo(tradeCertNo);
                        applicationFeeTradeCertDobjFrom.setValidUpto(validUptoDate);
                        if (validUptoDate != null) {
                            applicationFeeTradeCertDobjFrom.setValidUptoAsString(format.format(validUptoDate));
                        }
                        if (tradeCertNo != null && !"".equals(tradeCertNo)) {
                            tradeCertNoNotBlank = true;
                        }
                        if (Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_DUP) {
                            tradeCertType = "DUPLICATE";
                        } else if (Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_NEW && tradeCertNo != null && !"".equals(tradeCertNo)) {
                            if (applicationFeeTradeCertDobjFrom.getStatus() != null && applicationFeeTradeCertDobjFrom.getStatus().equals("E")) { /// E: Edit Trade Certificate
                                tradeCertType = "EDIT";
                            } else {
                                tradeCertType = "RENEW";
                            }
                        } else {
                            tradeCertType = "NEW";
                        }
                    } catch (VahanException ex) {
                        LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                    }
                }
            }
            fillApplicationSectionsList();
            if (!this.applicationSectionsList.isEmpty()) {
                this.visibleApplicationDetailPanel = true;
            } else {
                this.visibleApplicationDetailPanel = false;

            }
        } else {
            reset();
        }
        visibleTradeCertificateApplicationPanel = true;

    }

    public void confirmRecieptPrint() {
        //RequestContext rc = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('printFeeReciept').show()");
    }

    public String printFeeRecieptDetails() {
        if (applicationFeeTradeCertDobj.getReceiptNumber() == null) {
            printRcptNo = rcpt_bean.getBook_no() + (rcpt_bean.getCurrent_rcpt_no() - 1);
        } else {
            printRcptNo = applicationFeeTradeCertDobj.getReceiptNumber();
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", printRcptNo);
        return "PrintCashReceiptReport";
    }

    public static PrintService choosePrinter() {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        if (printJob.printDialog()) {
            return printJob.getPrintService();
        } else {
            return null;
        }
    }

    public List<ApplicationFeeTradeCertDobj> getDataBeanList(List<ApplicationFeeTradeCertDobj> masterDobjs) {
        List<ApplicationTradeCertDobj> list = new ArrayList<>();
        ApplicationTradeCertDobj applicationTradeCertDobj = null;
        ApplicationFeeTradeCertDobj tradeFeePrintReceiptMasterDobj = new ApplicationFeeTradeCertDobj();
        Utility utility = new Utility();
        this.totalFeeCollected = 0;
        int srNo = 1;
        for (int i = 0; i < masterDobjs.size(); i++) {
            applicationTradeCertDobj = new ApplicationTradeCertDobj();
            applicationTradeCertDobj.setSrNo((srNo++) + "");
            applicationTradeCertDobj.setNoOfAllowedVehicles(masterDobjs.get(i).getNoOfAllowedVehicles());
            applicationTradeCertDobj.setVehCatgName(masterDobjs.get(i).getVehCatgName());
            applicationTradeCertDobj.setFuelTypeName(masterDobjs.get(i).getFuelTypeName());
            applicationTradeCertDobj.setExpired(false);

            list.add(applicationTradeCertDobj);
        }
        this.totalFeeCollected += Double.valueOf(masterDobjs.get(0).getAmount());
        int grandTotal = (int) this.totalFeeCollected;
        tradeFeePrintReceiptMasterDobj.setGrandTotal(utility.ConvertNumberToWords(grandTotal));
        tradeFeePrintReceiptMasterDobj.setTotal1(grandTotal);
        tradeFeePrintReceiptMasterDobj.setPurpose(masterDobjs.get(0).getPurpose());
        tradeFeePrintReceiptMasterDobj.setAmount(masterDobjs.get(0).getAmount());
        tradeFeePrintReceiptMasterDobj.setSurcharge(0.0);
        tradeFeePrintReceiptMasterDobj.setFineCollected(String.valueOf(0.0).split("\\.")[0]);
        tradeFeePrintReceiptMasterDobj.setStateName(masterDobjs.get(0).getStateName());
        tradeFeePrintReceiptMasterDobj.setOfficeName(masterDobjs.get(0).getOfficeName());
        tradeFeePrintReceiptMasterDobj.setReceiptNumber(masterDobjs.get(0).getReceiptNumber());
        tradeFeePrintReceiptMasterDobj.setReceiptDateAsString(masterDobjs.get(0).getReceiptDateAsString());
        tradeFeePrintReceiptMasterDobj.setApplNo(masterDobjs.get(0).getApplNo());
        tradeFeePrintReceiptMasterDobj.setDealerName(masterDobjs.get(0).getDealerName());
        tradeFeePrintReceiptMasterDobj.setTradeCerFeeRecieptSubList(list);

        List<ApplicationFeeTradeCertDobj> dataBeanList = new ArrayList<>();
        dataBeanList.add(tradeFeePrintReceiptMasterDobj);

        return dataBeanList;

    }

    public boolean isDisablePrint() {
        return disablePrint;
    }

    public ReceiptMasterBean getRcpt_bean() {
        return rcpt_bean;
    }

    public void setRcpt_bean(ReceiptMasterBean rcpt_bean) {
        this.rcpt_bean = rcpt_bean;
    }

    public double getServiceCharge() {
        return serviceCharge;
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

    public boolean isDuplicate() {
        return duplicate;
    }

    public String getTradeCertType() {
        return tradeCertType;
    }

    public boolean isTradeCertNoNotBlank() {
        return tradeCertNoNotBlank;
    }

    /**
     * @return the rcptNoPopup
     */
    public String getRcptNoPopup() {
        return rcptNoPopup;
    }

    /**
     * @param rcptNoPopup the rcptNoPopup to set
     */
    public void setRcptNoPopup(String rcptNoPopup) {
        this.rcptNoPopup = rcptNoPopup;
    }

    public boolean isFixCashModeForDealerAdmin() {
        return fixCashModeForDealerAdmin;
    }

    public void setFixCashModeForDealerAdmin(boolean fixCashModeForDealerAdmin) {
        this.fixCashModeForDealerAdmin = fixCashModeForDealerAdmin;
    }

    public String getSaveCaption() {
        return saveCaption;
    }

    public double getTotalTaxCollected() {
        return totalTaxCollected;
    }

    public void setTotalTaxCollected(double totalTaxCollected) {
        this.totalTaxCollected = totalTaxCollected;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getServiceChargeInString() {
        return serviceChargeInString;
    }

    public String getTransactionChargeInString() {
        return transactionChargeInString;
    }

    public String getTotalAmountInString() {
        return totalAmountInString;
    }

    private Double fetchTotalFineCollection() {
        Double totalFineCollection = 0.0;
        if (!mapSectionsSrNoOfSelectedApplication.isEmpty()) {
            for (Object keyObj : mapSectionsSrNoOfSelectedApplication.keySet()) {
                String key = (String) keyObj;
                ApplicationFeeTradeCertDobj dobj = (ApplicationFeeTradeCertDobj) mapSectionsSrNoOfSelectedApplication.get(key);
                totalFineCollection += dobj.getFine1();

            }
        }
        return totalFineCollection;
    }

    public String getCancelPayment() {
        OnlinePaymentImpl onImpl = new OnlinePaymentImpl();
        boolean flag;
        long user_cd = 0;
        try {
            Object[] obj = new FeeImpl().getUserIDAndPassword(appl_no);
            if (obj != null && obj.length > 0) {
                user_cd = (long) obj[1];
                if (CommonUtils.isNullOrBlank(onImpl.getTransactionNumber(appl_no))) {
                    flag = onImpl.getPaymentRevertBack(user_cd + "", getAppl_no(), null);
                    if (flag) {
                        setOnlinePaymentInitiated(false);
                        setRenderOnlinePayBtn(true);
                        setRenderCancelPayment(false);
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

    /**
     * @return the balanceTax
     */
    public int getBalanceTax() {
        return balanceTax;
    }

    /**
     * @param balanceTax the balanceTax to set
     */
    public void setBalanceTax(int balanceTax) {
        this.balanceTax = balanceTax;
    }

    public boolean isDisplayFuel() {
        return displayFuel;
    }

    public VahanTaxParameters fillTaxParametersFromDobj(ApplicationFeeTradeCertDobj dobj) throws VahanException {
        final List vchCatgLstNT = Arrays.asList(new String[]{"HMV", "LMV", "MMV", "3WN", "2WN", "2WIC", "4WIC"});
        VahanTaxParameters taxParameters = new VahanTaxParameters();
        try {

            if (dobj == null) {
                return taxParameters;
            }
            if (tmConfigDobj.getTmTradeCertConfigDobj().isTaxToBeCalculatedWebService()
                    && tmConfigDobj.getTmTradeCertConfigDobj().isFuelTaxApplicable()) {   //// for Gujarat
                taxParameters.setFUEL(Integer.valueOf(dobj.getFuelTypeFor()));
                if (vchCatgLstNT.contains(dobj.getVehCatgFor())) {  //// for Non Transport
                    taxParameters.setVCH_TYPE(2);
                    taxParameters.setVH_CLASS(2);
                } else {                                            ///// for Transport
                    taxParameters.setVCH_TYPE(1);
                    taxParameters.setVH_CLASS(51);
                }
            } else if (tmConfigDobj.getTmTradeCertConfigDobj().isTaxToBeCalculatedWebService()
                    && !tmConfigDobj.getTmTradeCertConfigDobj().isFuelTaxApplicable()) { //// for Rajasthan

                taxParameters.setREGN_DATE(DateUtils.getCurrentDate());
                taxParameters.setTAX_DUE_FROM_DATE(DateUtils.getCurrentDate());
                final List vchCatgLstForHalfTax = Arrays.asList(new String[]{"2WT", "2WN", "2WIC", "4WIC"});
                if (vchCatgLstNT.contains(dobj.getVehCatgFor())) {  //// for Non Transport
                    taxParameters.setVCH_TYPE(2);
                    if (vchCatgLstForHalfTax.contains(dobj.getVehCatgFor())) {   ///// slab_code 150
                        taxParameters.setVH_CLASS(2);
                    } else {                                                       ///// slab_code 147
                        taxParameters.setVH_CLASS(21);
                    }

                } else {                                            ///// for Transport
                    taxParameters.setVCH_TYPE(1);
                    if (vchCatgLstForHalfTax.contains(dobj.getVehCatgFor())) {   ///// slab_code 149
                        taxParameters.setVH_CLASS(51);
                    } else {                                                       ///// slab_code 146
                        taxParameters.setVH_CLASS(59);
                    }
                }

            }
            taxParameters.setREGN_DATE(DateUtils.getCurrentDate());
            taxParameters.setTAX_DUE_FROM_DATE(DateUtils.getCurrentDate());
            taxParameters.setREGN_TYPE("N");
            taxParameters.setTAX_MODE("Y");
            taxParameters.setVCH_CATG(dobj.getVehCatgFor());
            taxParameters.setSTATE_CD(dobj.getStateCd());
            taxParameters.setPUR_CD(89);
            taxParameters.setLD_WT(Integer.valueOf(dobj.getNoOfAllowedVehicles()));

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Exception occured while setting tax parameters. [message:{" + TableConstants.SomthingWentWrong + "}]");
        }
        return taxParameters;
    }

    public Double getTotalFuelTax() {
        return totalFuelTax;
    }

    public int getTotalSurcharge() {
        return totalSurcharge;
    }

    public void displayTotalAmount() {
        int miscFee = 0;
        if (!CommonUtils.isNullOrBlank(this.miscAmount)) {
            miscFee = Integer.valueOf(this.miscAmount);
        }
        this.totalAmountInString = String.valueOf(this.getTotalAmount() + fetchTotalFineCollection() + balanceTax + miscFee).split("\\.")[0];
    }

    public String getApplNoToBeSetInVtFee() {
        return applNoToBeSetInVtFee;
    }

    public void setApplNoToBeSetInVtFee(String applNoToBeSetInVtFee) {
        this.applNoToBeSetInVtFee = applNoToBeSetInVtFee;
    }

    public String getMiscAmount() {
        return miscAmount;
    }

    public void setMiscAmount(String miscAmount) {
        this.miscAmount = miscAmount;
    }

    public boolean isRenderMiscFeeAmount() {
        return renderMiscFeeAmount;
    }

    /**
     * @return the btn_print
     */
    public boolean isBtn_print() {
        return btn_print;
    }

    /**
     * @param btn_print the btn_print to set
     */
    public void setBtn_print(boolean btn_print) {
        this.btn_print = btn_print;
    }

    /**
     * @return the confirmDialogRenderButton
     */
    public boolean isConfirmDialogRenderButton() {
        return confirmDialogRenderButton;
    }

    /**
     * @param confirmDialogRenderButton the confirmDialogRenderButton to set
     */
    public void setConfirmDialogRenderButton(boolean confirmDialogRenderButton) {
        this.confirmDialogRenderButton = confirmDialogRenderButton;
    }

    /**
     * @return the paymentTypeBtn
     */
    public String getPaymentTypeBtn() {
        return paymentTypeBtn;
    }

    /**
     * @param paymentTypeBtn the paymentTypeBtn to set
     */
    public void setPaymentTypeBtn(String paymentTypeBtn) {
        this.paymentTypeBtn = paymentTypeBtn;
    }

    /**
     * @return the totalAmountPayable
     */
    public long getTotalAmountPayable() {
        return totalAmountPayable;
    }

    /**
     * @param totalAmountPayable the totalAmountPayable to set
     */
    public void setTotalAmountPayable(long totalAmountPayable) {
        this.totalAmountPayable = totalAmountPayable;
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
     * @return the listTaxForm
     */
    public List<TaxFormPanelBean> getListTaxForm() {
        return listTaxForm;
    }

    /**
     * @param listTaxForm the listTaxForm to set
     */
    public void setListTaxForm(List<TaxFormPanelBean> listTaxForm) {
        this.listTaxForm = listTaxForm;
    }

    /**
     * @return the renderTaxPanel
     */
    public boolean isRenderTaxPanel() {
        return renderTaxPanel;
    }

    /**
     * @param renderTaxPanel the renderTaxPanel to set
     */
    public void setRenderTaxPanel(boolean renderTaxPanel) {
        this.renderTaxPanel = renderTaxPanel;
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
     * @return the pur_cd
     */
    public int getPur_cd() {
        return pur_cd;
    }

    /**
     * @param pur_cd the pur_cd to set
     */
    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
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
     * @return the renderCancelPayment
     */
    public boolean isRenderCancelPayment() {
        return renderCancelPayment;
    }

    /**
     * @param renderCancelPayment the renderCancelPayment to set
     */
    public void setRenderCancelPayment(boolean renderCancelPayment) {
        this.renderCancelPayment = renderCancelPayment;
    }

    /**
     * @return the btn_save
     */
    public boolean isBtn_save() {
        return btn_save;
    }

    /**
     * @param btn_save the btn_save to set
     */
    public void setBtn_save(boolean btn_save) {
        this.btn_save = btn_save;
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
     * @return the feePayDobjList
     */
    public List<Tax_Pay_Dobj> getFeePayDobjList() {
        return feePayDobjList;
    }

    /**
     * @param feePayDobjList the feePayDobjList to set
     */
    public void setFeePayDobjList(List<Tax_Pay_Dobj> feePayDobjList) {
        this.feePayDobjList = feePayDobjList;
    }

    /**
     * @return the paymentType
     */
    public String getPaymentType() {
        return paymentType;
    }

    /**
     * @param paymentType the paymentType to set
     */
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
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
     * @return the mobileNo
     */
    public String getMobileNo() {
        return mobileNo;
    }

    /**
     * @param mobileNo the mobileNo to set
     */
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public boolean isOnlinePaymentInitiated() {
        return onlinePaymentInitiated;
    }

    public void setOnlinePaymentInitiated(boolean onlinePaymentInitiated) {
        this.onlinePaymentInitiated = onlinePaymentInitiated;
    }

    public boolean isDoNotShowNoOfVehicles() {
        return doNotShowNoOfVehicles;
    }

    public void setDoNotShowNoOfVehicles(boolean doNotShowNoOfVehicles) {
        this.doNotShowNoOfVehicles = doNotShowNoOfVehicles;
    }

    public boolean isStockTransferReq() {
        return stockTransferReq;
    }

    public void setStockTransferReq(boolean stockTransferReq) {
        this.stockTransferReq = stockTransferReq;
    }
}
