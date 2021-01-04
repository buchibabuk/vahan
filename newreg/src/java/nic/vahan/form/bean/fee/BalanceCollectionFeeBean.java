/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.fee;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.eapplication.OnlinePaymentImpl;
import nic.vahan.form.bean.FormFeePanelBean;
import nic.vahan.form.bean.PaymentCollectionBean;
import nic.vahan.form.bean.ReceiptMasterBean;
import nic.vahan.form.bean.TaxFormPanelBean;
import nic.vahan.form.bean.common.FileMovementAbstractApplBean;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.FeeDobj;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.Fee_Pay_Dobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.Tax_Pay_Dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.common.UserIDAndPasswordForOnlinePaymentDobj;
import nic.vahan.form.dobj.fee.BalanceCollectionFeeDobj;
import nic.vahan.form.dobj.commoncarrier.DetailCommonCarrierDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.tax.TaxDobj;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import nic.vahan.form.impl.EpayImpl;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.TaxInstallCollectionImpl;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.primefaces.PrimeFaces;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "balanceCollection")
@ViewScoped
public class BalanceCollectionFeeBean extends FileMovementAbstractApplBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(BalanceCollectionFeeBean.class);
    private BalanceCollectionFeeDobj balDob = new BalanceCollectionFeeDobj();
    private PaymentCollectionBean paymentBean = new PaymentCollectionBean();
    private boolean confirmPopUp;
    private List<FeeDobj> feeCollectionLists = new ArrayList();
    private long totalAmountPayable;
    private String regn_no;
    private String appl_no;
    private String ownerName = "";
    private OwnerDetailsDobj ownerDobj;
    private DetailCommonCarrierDobj detailCommonCarrierDobj;
    List<DetailCommonCarrierDobj> ccDobj;
    private boolean btn_save = true;
    private FormFeePanelBean feePanelBean = new FormFeePanelBean();
    private boolean disableApplNo = false;
    private boolean renderVehdetails;
    FeeImpl feeImpl = new FeeImpl();
    private List<SelectItem> purposeCodeList = new ArrayList<>();
    List<String> allowedPurposeCd = new ArrayList<>();
    private List<FeeDobj> listTransWise = new ArrayList<FeeDobj>();
    private boolean renderUserChargesAmountPanel = false;
    private Long totalUserChrg = 0l;
    private Long userChrg;
    @ManagedProperty(value = "#{rcpt_bean}")
    private ReceiptMasterBean rcpt_bean;
    private String generatedRcpt = null;
    String printRcptNo = null;
    private boolean showInRto = true;
    private boolean renderApplNo = true;
    private boolean carrierRegnPanel = false;
    private boolean renderRegnNo = false;
    private boolean otherStatePanel = false;
    private boolean vehicleDtlsPanel = false;
    private boolean ccarrierRegnPanel = false;
    private boolean carrierFlowRegnPanel = false;
    private String selectedOption = "A";
    private boolean disableSelectedOption;
    private boolean renderFeePanelLabel = false;
    private String balfeeremark = "";
    private String[][] data = null;
    private boolean autoRunTaxListener = false;
    private List<FeeDobj> listAll = null;
    private EpayDobj checkFeeTax = null;
    private List<TaxFormPanelBean> list_tax_diff = new ArrayList();
    private List tax_diff_pur_cd = null;
    private boolean isTaxDiffDetails = true;
    private SessionVariables sessionVariables = null;
    private boolean rendertempFeeAmount = false;
    private boolean optionCCarrierRegistration;
    private boolean renderShowButton = true;
    private boolean optionForMizoram = false;
    private boolean renderOnlinePayBtn = false;
    private boolean renderOnlineCancelBtn = false;
    private String paymentType = "";
    private String onlineUserCredentialmsg = "";
    private String paymentMsg = "";
    private Long mobileNo;
    private boolean renderAddTaxBtn = true;
    private boolean optionRePostalFee = false;
    private boolean renderTempRegnNo = false;
    private boolean blackListedCompoundingAmt = false;
    private Owner_dobj owDobj = new Owner_dobj();
    private String taxPaidLabel;
    private String addTaxPaidLabel;
    private String taxClearLabel;
    private String addTaxClearLabel;
    private String taxExempLabel;
    private boolean renderTaxPaid = false;
    private boolean renderAddtaxPaid = false;
    private boolean renderTaxClear = false;
    private boolean renderAddtaxClear = false;
    private boolean renderTaxExemp = false;
    private boolean btn_reset = true;
    private boolean payFeeTaxAndBtnPanel = false;
    private Date vow4Date = null;
    private String vow4_started_message = "";

    public BalanceCollectionFeeBean() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0) {
            //here we can use vahan message 
            return;
        }
        purposeCodeList = new ArrayList();
        data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
        purposeCodeList.add(new SelectItem("-1", "Select Fee"));
        for (int i = 0; i < data.length; i++) {
            if (data[i][6].trim().equalsIgnoreCase("true")) {
                purposeCodeList.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
        vow4Date = ServerUtil.getVahan4StartDate(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
        if (vow4Date != null) {
            vow4_started_message = "VAHAN 4.0 has been started in this office on :" + DateUtil.parseDateToString(vow4Date);
        }
        listAll = EpayImpl.getAllForStateFeeDetails(sessionVariables.getStateCodeSelected());
        for (FeeDobj fee : listAll) {
            purposeCodeList.add(new SelectItem(fee.getPurCd(), fee.getFeeHeadDescr()));
        }

        feePanelBean.setPurposeCodeList(purposeCodeList);
        if (sessionVariables.getSelectedWork().getPur_cd() == TableConstants.VM_TRANSACTION_CARRIER_REGN
                || sessionVariables.getSelectedWork().getPur_cd() == TableConstants.VM_TRANSACTION_CARRIER_RENEWAL
                || sessionVariables.getSelectedWork().getPur_cd() == TableConstants.VM_TRANSACTION_CARRIER_DUPLICATE) {
            setOptionCCarrierRegistration(true);
            setRenderShowButton(false);
            setSelectedOption("F");
            setCarrierFlowRegnPanel(true);
            setRenderApplNo(false);
            setRenderRegnNo(false);
            setRenderTempRegnNo(false);
            setOtherStatePanel(false);
            setRegn_no(null);
            setAppl_no(null);
            setVehicleDtlsPanel(false);
            setOwnerName(sessionVariables.getSelectedWork().getAppl_no());

            for (int i = 0; i < data.length; i++) {
                if (data[i][0].trim().equalsIgnoreCase("359") || data[i][0].trim().equalsIgnoreCase("360")
                        || data[i][0].trim().equalsIgnoreCase("361") || data[i][0].trim().equalsIgnoreCase("362")
                        || data[i][0].trim().equalsIgnoreCase("371") || data[i][0].trim().equalsIgnoreCase("372")
                        || data[i][0].trim().equalsIgnoreCase("378")) {
                    purposeCodeList.add(new SelectItem(getData()[i][0], getData()[i][1]));
                }
            }
            for (FeeDobj fee : listAll) {
                purposeCodeList.add(new SelectItem(fee.getPurCd(), fee.getFeeHeadDescr()));
            }
            isTaxDiffDetails = false;
            showDetails();
        } else {
            setOptionCCarrierRegistration(false);
            if (Util.getUserStateCode().equals("MZ")) {
                setOptionForMizoram(true);
            }
        }
        try {
            TmConfigurationDobj tmConfig = Util.getTmConfiguration();
            if (tmConfig != null && tmConfig.isIs_rc_dispatch_repost_fee()) {
                setOptionRePostalFee(true);
            } else {
                setOptionRePostalFee(false);
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        }
    }

    public void showDetails() {
        setPayFeeTaxAndBtnPanel(true);
        OwnerImpl ownImpl = new OwnerImpl();
        try {
            boolean isblacklistedvehicle;
            TmConfigurationDobj tmConfig = Util.getTmConfiguration();
            if (selectedOption.contains("R")) {
                isblacklistedvehicle = ServerUtil.isPurposeCodeOnBlacklistedVehicle(getRegn_no(), Util.getSelectedSeat().getAction_cd(), Util.getUserStateCode(), tmConfig);
                if (isblacklistedvehicle) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Vehicle No. has been Blacklisted."));
                    return;
                }
            }
            if (selectedOption.contains("A")) {
                if (getAppl_no() == null || getAppl_no().equals("")) {
                    return;
                }
                ownerDobj = null;
                ownerDobj = ownImpl.getOwnerDetailsFromAppl(appl_no, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                if (ownerDobj == null) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Appl No", "Invalid Appl No");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    setAppl_no(null);
                    return;
                }
            } else if (selectedOption.contains("D")) {
                if (getRegn_no() == null || getRegn_no().equals("")) {
                    return;
                }
                ownerDobj = null;
                ownerDobj = ownImpl.getOwnerDetailsFromApplForRePostRC(regn_no, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                if (ownerDobj == null) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Regn No", "Invalid Regn No");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    setRegn_no(null);
                    return;
                }
            } else if (selectedOption.contains("C") || selectedOption.contains("F")) {
                ownerDobj = null;
                if (getOwnerName() == null || getOwnerName().equals("")) {
                    return;
                }
                ownerDobj = new OwnerDetailsDobj();
                ownerDobj.setOwner_name(ownerName);
                ownerDobj.setVch_catg(TableConstants.CARRIERCATEGORY);
                ownerDobj.setVh_class(0);
            } else if (selectedOption.contains("R")) {
                if (getRegn_no() == null || getRegn_no().equals("")) {
                    return;
                }

                ownerDobj = ownImpl.getOwnerDetails(regn_no, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                if (ownerDobj == null) {
                    setRegn_no(null);
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Regn No", "Invalid Regn No");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }
                owDobj = ownImpl.getOwnerDobj(ownerDobj);
            } else if (selectedOption.contains("T")) {
                if (getRegn_no() == null || getRegn_no().equals("")) {
                    return;
                }
                ownerDobj = ownImpl.getTempOwnerDetails(regn_no, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                if (ownerDobj == null) {
                    setRegn_no(null);
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Temproary Regn No", "Invalid Temproary Regn No");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    setPayFeeTaxAndBtnPanel(false);
                    setVehicleDtlsPanel(false);
                    return;
                }
            } else if (selectedOption.equalsIgnoreCase("O")) {
                if (getRegn_no() == null || getRegn_no().equals("") || getOwnerName() == null || getOwnerName().equals("")) {
                    return;
                }
                ownerDobj = ownImpl.getOwnerDetails(regn_no, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                if (ownerDobj != null) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle Belongs to Same State Please Select Other Option..", "Vehicle Belongs to Same State Please Select Other Option..");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    setPayFeeTaxAndBtnPanel(false);
                    setVehicleDtlsPanel(false);
                    return;
                }
                ownerDobj = new OwnerDetailsDobj();
                ownerDobj.setOwner_name(ownerName);
                ownerDobj.setRegn_no(regn_no);
                ownerDobj.setVch_catg(TableConstants.CARRIERCATEGORY);
                ownerDobj.setVh_class(0);
                ownerDobj.setState_cd(sessionVariables.getStateCodeSelected());
                Owner_dobj owner_dobj = new Owner_dobj();
                owner_dobj.setOwner_name(ownerName);
                owner_dobj.setRegn_no(regn_no);
                owner_dobj.setVch_catg(TableConstants.CARRIERCATEGORY);
                owner_dobj.setVh_class(0);
                owner_dobj.setState_cd(sessionVariables.getStateCodeSelected());
                TaxFormPanelBean taxFormPanelBean = new TaxFormPanelBean();
                tax_diff_pur_cd = EpayImpl.getAllTaxListForTaxDiff(owner_dobj, null);
                list_tax_diff.add(taxFormPanelBean);
            }
            //re-calculate fee and tax paid by user...start
            if (selectedOption.contains("A") || selectedOption.contains("R") || selectedOption.contains("T")) {
                Owner_dobj owner_dobj = ownImpl.getOwnerDobj(ownerDobj);
                mobileNo = owner_dobj.getOwner_identity().getMobile_no();
                if (owner_dobj != null && selectedOption.contains("A")) {
                    List<Status_dobj> pendingApplStatusList = ServerUtil.applicationStatusByApplNo(appl_no, sessionVariables.getStateCodeSelected());
                    if (pendingApplStatusList != null && !pendingApplStatusList.isEmpty()) {
                        for (int i = 0; i < pendingApplStatusList.size(); i++) {
                            if (pendingApplStatusList.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                                checkFeeTax = EpayImpl.getPurCdPaymentsRegisteredVehicle(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), owner_dobj, appl_no, null, TableConstants.VM_TRANSACTION_MAST_FIT_CERT, "");
                                break;
                            }
                        }
                    }
                }
                if (owner_dobj != null && (selectedOption.contains("R") || selectedOption.contains("T"))) {
                    List<Status_dobj> pendingApplStatusList = ServerUtil.applicationStatus(regn_no);
                    if (pendingApplStatusList != null && !pendingApplStatusList.isEmpty()) {
                        for (int i = 0; i < pendingApplStatusList.size(); i++) {
                            if (pendingApplStatusList.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                                checkFeeTax = EpayImpl.getPurCdPaymentsRegisteredVehicle(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), owner_dobj, pendingApplStatusList.get(i).getAppl_no(), null, TableConstants.VM_TRANSACTION_MAST_FIT_CERT, "");
                                break;
                            }
                        }
                    }
                }
                if (owner_dobj != null && !(selectedOption.equalsIgnoreCase("O") && (selectedOption.equalsIgnoreCase("C") || selectedOption.equalsIgnoreCase("F"))) && !selectedOption.equalsIgnoreCase("D")) {
                    TaxFormPanelBean taxFormPanelBean = new TaxFormPanelBean();
                    PassengerPermitDetailDobj prePermitDetailDobj = TaxServer_Impl.getPermitInfoForSavedTax(owner_dobj);
                    VehicleParameters vehicleParameters = FormulaUtils.fillVehicleParametersFromDobj(owner_dobj, prePermitDetailDobj);
                    tax_diff_pur_cd = EpayImpl.getAllTaxListForTaxDiff(owner_dobj, vehicleParameters);
                    list_tax_diff.add(taxFormPanelBean);
                    if (selectedOption.equalsIgnoreCase("R")) {

                        List<TaxFormPanelBean> listTaxForm = TaxServer_Impl.getListTaxForm(owner_dobj, vehicleParameters);
                        for (TaxFormPanelBean bean : listTaxForm) {
                            Map<String, String> taxPaidAndClearDetail = TaxServer_Impl.getTaxPaidAndClearDetail(regn_no, bean.getPur_cd());
                            for (Map.Entry<String, String> entry : taxPaidAndClearDetail.entrySet()) {
                                if (entry.getKey().equals("taxpaid")) {
                                    if (entry.getValue() != null && !entry.getValue().equals("")) {
                                        setRenderTaxPaid(true);
                                        setTaxPaidLabel(entry.getValue());
                                    } else {
                                        setRenderTaxPaid(false);
                                    }
                                }
                                if (entry.getKey().equals("addtaxpaid")) {
                                    if (entry.getValue() != null && !entry.getValue().equals("")) {
                                        setRenderAddtaxPaid(true);
                                        setAddTaxPaidLabel(entry.getValue());
                                    } else {
                                        setRenderAddtaxPaid(false);
                                    }
                                }
                                if (entry.getKey().equals("taxclear")) {
                                    if (entry.getValue() != null && !entry.getValue().equals("")) {
                                        setRenderTaxClear(true);
                                        setTaxClearLabel(entry.getValue());
                                    } else {
                                        setRenderTaxClear(false);
                                    }
                                }
                                if (entry.getKey().equals("addtaxclear")) {
                                    if (entry.getValue() != null && !entry.getValue().equals("")) {
                                        setRenderAddtaxClear(true);
                                        setAddTaxClearLabel(entry.getValue());
                                    } else {
                                        setRenderAddtaxClear(false);
                                    }
                                }

                                if (entry.getKey().equals("taxExemp")) {
                                    if (entry.getValue() != null && !entry.getValue().equals("")) {
                                        setRenderTaxExemp(true);
                                        setTaxExempLabel(entry.getValue());
                                    } else {
                                        setRenderTaxExemp(false);
                                    }
                                }

                            }
                        }
                    }
                }
                if (checkFeeTax != null) {
                    checkFeeTax.setVerificationCheckBox(false);
                }
            }//re-calculate fee and tax paid by user...End
            getFeePanelBean().getFeeCollectionList().clear();
            getFeePanelBean().getPayableFeeCollectionList().clear();
            getFeeCollectionLists().clear();
            getFeePanelBean().getPayableFeeCollectionList().addAll(listAll);
            getFeePanelBean().getFeeCollectionList().addAll(listAll);
            getFeeCollectionLists().addAll(listAll);
            FeeDobj collectedFeeDobj = new FeeDobj();
            collectedFeeDobj.setReadOnlyFee(false);
            collectedFeeDobj.setFeeAmount(0L);
            collectedFeeDobj.setFineAmount(0L);
            if (!"TN".contains(Util.getUserStateCode())) {
                collectedFeeDobj.setPurCd((selectedOption.equalsIgnoreCase("C") || selectedOption.equalsIgnoreCase("F")) ? -1 : TableConstants.VM_TRANSACTION_MAST_MISC);
            }
            collectedFeeDobj.setDisableDropDown(false);
            getFeePanelBean().getPayableFeeCollectionList().add(collectedFeeDobj);
            getFeePanelBean().getFeeCollectionList().add(collectedFeeDobj);
            getFeeCollectionLists().add(collectedFeeDobj);
            BlackListedVehicleImpl blimpl = new BlackListedVehicleImpl();
            BlackListedVehicleDobj blackListedDobj = blimpl.getBlacklistedVehicleDetails(regn_no, null);
            if ("TN".contains(Util.getUserStateCode())) {
                if (blackListedDobj != null && owDobj != null && owDobj.getState_cd() != null && owDobj.getState_cd().equals(blackListedDobj.getState_cd()) && blackListedDobj.getOff_cd() == owDobj.getOff_cd()) {
                    if (blackListedDobj.getComplain_type() == TableConstants.BLCompoundingAmtCode) {
                        collectedFeeDobj.setFeeAmount(blackListedDobj.getCompounding_amt());
                        collectedFeeDobj.setPurCd(TableConstants.BLCompoundingAmtCode);
                        collectedFeeDobj.setDisableDropDown(true);
                        collectedFeeDobj.setReadOnlyFee(true);
                        isTaxDiffDetails = false;
                        list_tax_diff.clear();
                        showInRto = false;
                        blackListedCompoundingAmt = true;
                    }
                }
            } else {
                if (blackListedDobj != null && owDobj != null && owDobj.getState_cd() != null && owDobj.getState_cd().equals(blackListedDobj.getState_cd())) {
                    if (blackListedDobj.getComplain_type() == TableConstants.BLCompoundingAmtCode) {
                        collectedFeeDobj.setFeeAmount(blackListedDobj.getCompounding_amt());
                        collectedFeeDobj.setPurCd(TableConstants.BLCompoundingAmtCode);
                        collectedFeeDobj.setDisableDropDown(true);
                        collectedFeeDobj.setReadOnlyFee(true);
                        isTaxDiffDetails = false;
                        list_tax_diff.clear();
                        showInRto = false;
                        blackListedCompoundingAmt = true;
                    }
                }
            }
            getFeePanelBean().calculateTotal();
            calculateTotal();
            updateTotalPayableAmount();
            this.disableApplNo = true;
            disableSelectedOption = true;
            // Online Payment Button 
            if (tmConfig != null && tmConfig.isOnlinePayment() && !"T".equals(selectedOption)) {
                setRenderOnlinePayBtn(true);
                UserIDAndPasswordForOnlinePaymentDobj obj = new FeeImpl().getUserIDAndPasswordForOnlinePayment(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), getAppl_no(), getOwnerName(), getMobileNo(), getRegn_no(), getSelectedOption());
                if (obj != null) {
                    setAppl_no(obj.getAppl_no());
                    String userInfo = "Online Payment Credentials User ID is : " + getAppl_no() + " & Password : " + obj.getUser_pwd();
                    setRenderOnlinePayBtn(false);
                    setBtn_save(false);
                    setBtn_reset(false);
                    setOnlineUserCredentialmsg(userInfo);
                    this.getOnlineFeeTax(getSessionVariables().getStateCodeSelected(), getSessionVariables().getOffCodeSelected(), getAppl_no());
                    setShowInRto(false);
                    setRenderAddTaxBtn(false);
                    if (CommonUtils.isNullOrBlank(new OnlinePaymentImpl().getTransactionNumber(getAppl_no()))) {
                        setRenderOnlineCancelBtn(true);
                    } else {
                        setRenderOnlineCancelBtn(false);
                    }
                }
            }
            PrimeFaces.current().ajax().update("form_registered_vehicle_fee:vehNoPanelGrid");
        } catch (VahanException vex) {
            setPayFeeTaxAndBtnPanel(false);
            setVehicleDtlsPanel(false);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void calculateFee(Integer selectedFeeCode, int vehClass, String vehCatg) throws VahanException {

        if (!vehCatg.equalsIgnoreCase(TableConstants.CARRIERCATEGORY)) {

            if (Utility.isNullOrBlank(vehCatg)) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Unknown Vehicle Category.!", "Unknown Vehicle Category!");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }

            if (selectedFeeCode == -1 || selectedFeeCode == 0) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Unknown purpose code for fee collection.", "Unable to calculate fee!");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
            if (vehClass == -1 || vehClass == 0) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Unknown vehicle class for fee colletion.", "Unable to calculate fee!");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
        }

        FeeDobj selectedFeeObject = new FeeDobj(selectedFeeCode);
        int lastIndex = getFeeCollectionLists().lastIndexOf(selectedFeeObject);
        getFeeCollectionLists().remove(lastIndex);
        if (!getFeeCollectionLists().contains(selectedFeeObject)) {

            Long feeValue = EpayImpl.getPurposeCodeFee(null, vehClass, selectedFeeCode, vehCatg);
            getFeeCollectionLists().remove(selectedFeeObject);
            selectedFeeObject.setFeeAmount(feeValue);
            selectedFeeObject.setFineAmount(0l);
            selectedFeeObject.setTotalAmount(feeValue + selectedFeeObject.getFineAmount());

            getFeeCollectionLists().add(selectedFeeObject);
            if (selectedFeeObject.getPurCd() == TableConstants.VM_TRANSACTION_MAST_SMART_CARD_FEE) {
                selectedFeeObject.setReadOnlyFee(true);
            } else if (selectedFeeObject.getPurCd() == TableConstants.CARRIERSECURITYFEE
                    || selectedFeeObject.getPurCd() == TableConstants.VM_TRANSACTION_CARRIER_REGN
                    || selectedFeeObject.getPurCd() == TableConstants.VM_TRANSACTION_CARRIER_RENEWAL
                    || selectedFeeObject.getPurCd() == TableConstants.VM_TRANSACTION_CARRIER_DUPLICATE
                    || selectedFeeObject.getPurCd() == TableConstants.VM_TRANSACTION_CARRIER_AMENDMENT
                    || selectedFeeObject.getPurCd() == TableConstants.VM_APPEAL_CARRIER_REGN
                    || selectedFeeObject.getPurCd() == TableConstants.VM_PROCESSING_CARRIER_REGN) {
                selectedFeeObject.setReadOnlyFee(true);
            } else {
                selectedFeeObject.setReadOnlyFee(false);
            }
            calculateTotal();
            updateTotalPayableAmount();
            //getFeePanelBean().setEnable(true);

        } else {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Fee Head Already Selected!", "Fee Head Already Selected!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            calculateTotal();
            return;
        }

    }

    public void validateForm(String payType) throws VahanException {
        this.setPaymentType(payType);
        boolean allGood = false;
        long totalBal = 0;
        if (getTotalAmountPayable() == 0) {
            JSFUtils.setFacesMessage("Fee can't be submitted with zero payable amount", "Fee can't be submitted with zero payable amount", JSFUtils.INFO);
            return;
        }
        if ((getFeePanelBean().getPayableFeeCollectionList().isEmpty())) {
            JSFUtils.setFacesMessage("Please select Fee", "Please select Fee", JSFUtils.INFO);
            allGood = true;
            return;
        }
        // Checking for Payment Mode
        if (Utility.isNullOrBlank(getPaymentBean().getPayment_mode())
                || "-1".equalsIgnoreCase(getPaymentBean().getPayment_mode())) {
            JSFUtils.setFacesMessage("Please select payment mode", "Please select payment mode", JSFUtils.INFO);
            allGood = true;
            return;
        }
        try {
            ServerUtil.validateNoCashPayment(paymentType, getPaymentBean().getPayment_mode());
        } catch (VahanException ve) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }

        for (FeeDobj fe : getFeeCollectionLists()) {
            if (fe.getPurCd() == -1 && fe.getTotalAmount() > 0) {
                JSFUtils.setFacesMessage("Please select fee type", "Please select fee type", JSFUtils.INFO);
                allGood = true;
                return;
            } else {
                if ((fe.getPurCd() != -1) && (fe.getTotalAmount() <= 0)) {
                    JSFUtils.setFacesMessage("Fee Head Total Type Can't be Zero", "Fee Head Total Type Can't be Zero", JSFUtils.INFO);
                    allGood = true;
                    return;
                }

            }
        }
        for (TaxFormPanelBean taxDiffList : getList_tax_diff()) {
            if (taxDiffList.getPur_cd() == -1 && taxDiffList.getFinalTaxAmount() > 0) {
                JSFUtils.setFacesMessage("Please select Tax type", "Please select Tax type", JSFUtils.INFO);
                allGood = true;
                return;
            } else if (taxDiffList.getPur_cd() != -1 && taxDiffList.getFinalTaxAmount() <= 0) {
                JSFUtils.setFacesMessage("Tax Head Total Type Can't be Less Then or Equal to Zero ", "Tax Head Total Type Can't be Less Then or Equal to Zero", JSFUtils.INFO);
                allGood = true;
                return;
            } else if (taxDiffList.getPur_cd() == -1 && taxDiffList.getFinalTaxAmount() < 0) {
                JSFUtils.setFacesMessage("Please select Tax type", "Please select Tax type", JSFUtils.INFO);
                allGood = true;
                return;
            }
        }

        if (payType.equalsIgnoreCase("OnlinePayment") && !getPaymentBean().getPayment_mode().equals("C")) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please select cash payment mode for online payment.", "Please select cash payment mode for online payment.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        if (!CommonUtils.isNullOrBlank(paymentType) && getPaymentType().equalsIgnoreCase("onlinePayment") && getTotalAmountPayable() < 10L) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Total payable amount should be greater then or equal to Rs.10/- for Online Payment.", "Total payable amount should be greater then or equal to Rs.10/- for Online Payment.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        // Cash or some thing else
        // hardcode as cash = 4
        if (!(getPaymentBean().getPayment_mode().equals("C"))) {
            for (PaymentCollectionDobj payDobj : getPaymentBean().getPaymentlist()) {
                if (payDobj.getAmount() == null) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Amount of other than Cash Can not be Empty"));
                    return;
                }
                totalBal = totalBal + Long.parseLong(payDobj.getAmount());
                if (!(allGood = payDobj.validateDobj())) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, payDobj.getValidationMessage(), payDobj.getValidationMessage());
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    allGood = true;
                    break;
                } else {
                    allGood = false;
                }
            }
            if (!allGood) {
                if (totalBal > getTotalAmountPayable()) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Excess Total Instrument Amount (Rs." + (totalBal - getTotalAmountPayable()) + "/- must be adjusted in any transaction head amount."));
                    return;
                } else if ("DL,SK".contains(sessionVariables.getStateCodeSelected())) {
                    if (totalBal != getTotalAmountPayable()) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Paying total Instrument Amount Must be equal to Total Payable Amount"));
                        return;
                    }
                }
            }
        } else {
            if ("SK".contains(sessionVariables.getStateCodeSelected())) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please select Mixed Mode for payment. Because Cash payment mode in not allowed in Sikkim"));
                allGood = true;
                return;
            }
        }

        for (PaymentCollectionDobj dobj : getPaymentBean().getPaymentlist()) {
            if (dobj.getInstrument() != null && !dobj.getInstrument().isEmpty()) {
                if ((Util.getTmConfiguration() != null && Util.getTmConfiguration().getTmConfigPayVerifyDobj() != null) && ((dobj.getInstrument().equals(TableConstants.EGRASS_INSTRUMENT_CODE) && Util.getTmConfiguration().getTmConfigPayVerifyDobj().iseGrassVerify()) || (dobj.getInstrument().equals(TableConstants.INSTRUMENT_CODE_BANK_RECEIPT) && Util.getTmConfiguration().getTmConfigPayVerifyDobj().isBankReceiptVerify()))) {
                    if (!dobj.iseGrassVerified()) {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Either you have not verify the EGRASS payment or verification status is unsuccessful",
                                "Either you have not verify the payment or verification status is unsuccessful");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        return;
                    }
                }
            }
        }

        if (!CommonUtils.isNullOrBlank(paymentType) && getPaymentType().equals("onlinePayment")) {
            setPaymentMsg("Total Amount to paid Online ");
        } else {
            setPaymentMsg("Total Amount to be paid in Cash ");
        }
        calculateBalanceAmount(getTotalAmountPayable());
        //RequestContext rc = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('buiVarNoImage').show();");
        PrimeFaces.current().ajax().update("form_registered_vehicle_fee:popup");
        PrimeFaces.current().executeScript("PF('confDlgFee').show()");
    }

    public String saveFeeDetails() {
        Exception e = null;

        Fee_Pay_Dobj feePayDobj = new Fee_Pay_Dobj();
        List<FeeDobj> feeCollectionCloneLists = new ArrayList<>(feeCollectionLists);
        feePayDobj.setCollectedFeeList(feeCollectionCloneLists);
        feePayDobj.setOp_dt(new java.util.Date());
        long checkTotalAmount = 0L;
        if (sessionVariables.getSelectedWork().getPur_cd() == TableConstants.VM_TRANSACTION_CARRIER_REGN
                || sessionVariables.getSelectedWork().getPur_cd() == TableConstants.VM_TRANSACTION_CARRIER_RENEWAL
                || sessionVariables.getSelectedWork().getPur_cd() == TableConstants.VM_TRANSACTION_CARRIER_DUPLICATE) {
            setSelectedOption("F");
        }
        if (selectedOption.equalsIgnoreCase("A") || selectedOption.equalsIgnoreCase("R") || selectedOption.equalsIgnoreCase("D")) {
            feePayDobj.setRegnNo(ownerDobj.getRegn_no());
            feePayDobj.setApplNo(getAppl_no());
            feePayDobj.setState_cd(ownerDobj.getState_cd());
        } else if (selectedOption.equalsIgnoreCase("C")) {
            feePayDobj.setState_cd(sessionVariables.getStateCodeSelected());
            feePayDobj.setRegnNo("");
            feePayDobj.setApplNo(null);
            feePayDobj.getOwnerDobj().setOwner_name(ownerName);
            feePayDobj.setFeeTypeCategory("C");
        } else if (selectedOption.equalsIgnoreCase("F")) {
            feePayDobj.setState_cd(sessionVariables.getStateCodeSelected());
            feePayDobj.setRegnNo("");
            feePayDobj.setApplNo(null);
            feePayDobj.getOwnerDobj().setOwner_name(ownerName);
            feePayDobj.setFeeTypeCategory("F");
        } else if (selectedOption.equalsIgnoreCase("O")) {
            feePayDobj.setState_cd(sessionVariables.getStateCodeSelected());
            feePayDobj.setRegnNo(ownerDobj.getRegn_no());
            feePayDobj.setApplNo(null);
            feePayDobj.getOwnerDobj().setOwner_name(ownerDobj.getOwner_name());
            feePayDobj.setFeeTypeCategory("O");
        } else if (selectedOption.equalsIgnoreCase("T")) {
            feePayDobj.setRegnNo(ownerDobj.getRegn_no());
            feePayDobj.setApplNo(getAppl_no());
            feePayDobj.setState_cd(ownerDobj.getState_cd());
            feePayDobj.setFeeTypeCategory("T");
        }
        feePayDobj.setRcptDt(new java.util.Date());
        feePayDobj.setPaymentMode(getPaymentBean().getPayment_mode());
        try {
            FeeDraftDobj feeDraftDobj = null;
            if (!Utility.isNullOrBlank(getPaymentBean().getPayment_mode()) && (!getPaymentBean().getPayment_mode().equals("-1"))
                    && (!getPaymentBean().getPayment_mode().equals("C"))) {
                feeDraftDobj = new FeeDraftDobj();
                String pay_mode = getPaymentBean().getPayment_mode();
                feeDraftDobj.setAppl_no(selectedOption.matches("[C|O|F]+") ? "" : getAppl_no());
                feeDraftDobj.setFlag("A");
                feeDraftDobj.setCollected_by(Util.getEmpCode());
                feeDraftDobj.setState_cd(selectedOption.matches("[C|O|F]+") ? sessionVariables.getStateCodeSelected() : getOwnerDobj().getState_cd());
                feeDraftDobj.setOff_cd(String.valueOf(Util.getUserOffCode()));
                feeDraftDobj.setDraftPaymentList(getPaymentBean().getPaymentlist());
            }
            List<FeeDobj> feeDobjList = getFeeCollectionLists();
            if (feeDobjList != null) {
                for (FeeDobj feedobj : feeDobjList) {
                    if (feedobj.getPurCd() == 99 || feedobj.getPurCd() == 80) {
                        continue;
                    }
                    Long totalAmount = feedobj.getTotalAmount();
                    if (feedobj.getPurCd() != -1) {
                        if (totalAmount == null || totalAmount == 0L) {
                            throw new VahanException("Record cannot be save with zero value for " + ServerUtil.getTaxHead(feedobj.getPurCd()));
                        }
                    }
                }
            }

            feePayDobj.setCollectedFeeList(feeCollectionCloneLists);
            if (isRenderUserChargesAmountPanel()) {
                FeeDobj userCharge = new FeeDobj();
                userCharge.setFeeAmount((long) getTotalUserChrg());
                userCharge.setFineAmount((long) 0l);
                userCharge.setPurCd(99);
                feePayDobj.getCollectedFeeList().add(userCharge);
            }
            //NItin KUmar For Saprate Tax DEtails 
            List<Tax_Pay_Dobj> listTaxDobj = new ArrayList<>();
            DOTaxDetail breakUpDobj = null;

            for (TaxFormPanelBean bean : list_tax_diff) {
                if (bean.getPur_cd() == TableConstants.VM_PARKING_FEE_PUR_CD) {
                    FeeDobj feeDobj_parking = new FeeDobj();
                    feeDobj_parking.setPurCd(bean.getPur_cd());
                    feeDobj_parking.setFromDate(bean.getFinalEditableTaxFrom());
                    feeDobj_parking.setUptoDate(bean.getFinalEditableTaxUpto());
                    feeDobj_parking.setFeeAmount(bean.getTotalPaybaleTax());
                    feeDobj_parking.setFineAmount(bean.getTotalPaybalePenalty());
                    feePayDobj.getCollectedFeeList().add(feeDobj_parking);
                } else {
                    breakUpDobj = new DOTaxDetail();
                    Tax_Pay_Dobj taxDobj = new Tax_Pay_Dobj();
                    taxDobj.setFinalTaxAmount(bean.getFinalTaxAmount());
                    taxDobj.setTotalPaybaleTax(bean.getTotalPaybaleTax());
                    taxDobj.setTotalPaybalePenalty(bean.getTotalPaybalePenalty());
                    taxDobj.setTotalPaybaleSurcharge(bean.getTotalPaybaleSurcharge());
                    taxDobj.setTotalPaybaleRebate(bean.getTotalPaybaleRebate());
                    taxDobj.setTotalPaybaleInterest(bean.getTotalPaybaleInterest());
                    taxDobj.setFinalTaxFrom(DateUtils.parseDate(bean.getFinalEditableTaxFrom()));
                    taxDobj.setFinalTaxUpto(DateUtils.parseDate(bean.getFinalEditableTaxUpto()));
                    taxDobj.setTaxMode(bean.getTaxMode());
                    taxDobj.setPur_cd(bean.getPur_cd());
                    taxDobj.setRegnNo(regn_no);
                    taxDobj.setPaymentMode(getPaymentBean().getPayment_mode());
                    taxDobj.setTotalPaybaleTax1(bean.getTotalTax1());
                    taxDobj.setTotalPaybaleTax2(bean.getTotalTax2());
                    taxDobj.setPreviousAdj(bean.getTotalPayablePrvAdj());
                    //BreakUp Details 
                    breakUpDobj.setTAX_FROM(DateUtils.parseDate(bean.getFinalEditableTaxFrom()));
                    breakUpDobj.setTAX_UPTO(DateUtils.parseDate(bean.getFinalEditableTaxUpto()));
                    breakUpDobj.setPUR_CD(bean.getPur_cd());
                    breakUpDobj.setAMOUNT((double) ((bean.getTotalPaybaleTax())));
                    breakUpDobj.setAMOUNT1((double) (bean.getTotalTax1()));
                    breakUpDobj.setAMOUNT2((double) (bean.getTotalTax2()));
                    breakUpDobj.setPENALTY((double) (bean.getTotalPaybalePenalty()));
                    breakUpDobj.setINTEREST((double) (bean.getTotalPaybaleInterest()));
                    breakUpDobj.setPRV_ADJ((bean.getTotalPayablePrvAdj()));
                    breakUpDobj.setREBATE((double) ((bean.getTotalPaybaleRebate())));
                    breakUpDobj.setSURCHARGE((double) ((bean.getTotalPaybaleSurcharge())));
                    taxDobj.getTaxBreakDetails().add(breakUpDobj);
                    listTaxDobj.add(taxDobj);
                }
            }
            feePayDobj.setListTaxDobj(listTaxDobj);
            if (selectedOption.contains("F")) {
                feePayDobj.setApplNo(sessionVariables.getSelectedWork().getAppl_no());
            } else {
                feePayDobj.setApplNo(selectedOption.matches("[C|O|F]+") ? null : appl_no);
            }
            if (getPaymentType().equals("cash")) {
                String rcptNo = getFeeImpl().saveBalanceFeeDetailsInstrument(feePayDobj, feeDraftDobj, "Balance-" + getBalfeeremark(), getSelectedOption(), blackListedCompoundingAmt);//Details(feePayDobj);
                setGeneratedRcpt(rcptNo);

                if (!Utility.isNullOrBlank(generatedRcpt)) {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful Transaction", " Receipt Number:  " + rcptNo);
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    getFeePanelBean().setReadOnlyFineAmount(true);
                    reset();
                    setTotalAmountPayable(0);
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", generatedRcpt);
                    return "PrintCashReceiptReport";
                }
                getRcpt_bean().reset();
            } else if (getPaymentType().equals("onlinePayment")) {
                if (feePayDobj.getCollectedFeeList() != null && !feePayDobj.getCollectedFeeList().isEmpty()) {
                    for (FeeDobj dobj : feePayDobj.getCollectedFeeList()) {
                        Long totalAmount = dobj.getTotalAmount();
                        checkTotalAmount = checkTotalAmount + totalAmount;
                    }
                }

                List<Tax_Pay_Dobj> taxList = feePayDobj.getListTaxDobj();
                if (taxList != null && !taxList.isEmpty()) {
                    for (Tax_Pay_Dobj taxdobj : taxList) {
                        Long totalAmount = taxdobj.getFinalTaxAmount();
                        checkTotalAmount = checkTotalAmount + totalAmount;
                    }
                }

                String userPwd = getFeeImpl().saveOnlinePaymentData(feePayDobj, checkTotalAmount, getAppl_no(), getMobileNo(), getSessionVariables().getStateCodeSelected(), getSessionVariables().getOffCodeSelected(), getSessionVariables().getEmpCodeLoggedIn(), TableConstants.TAX_MODE_BALANCE, getSelectedOption(), getOwnerDobj(), false, false);
                if (userPwd != null) {
                    String userInfo = "";
                    setRenderOnlineCancelBtn(true);
                    setRenderOnlinePayBtn(false);
                    setBtn_save(false);
                    setBtn_reset(false);
                    if (getSelectedOption() != null && (getSelectedOption().equalsIgnoreCase("R") || getSelectedOption().equalsIgnoreCase("O") || getSelectedOption().equalsIgnoreCase("C"))) {
                        setAppl_no(userPwd.split(",")[0]);
                        userInfo = "Online Payment Credentials User ID : " + getAppl_no() + " & Password :  " + userPwd.split(",")[1];
                    } else {
                        userInfo = "Online Payment Credentials User ID : " + getAppl_no() + " & Password :  " + userPwd;
                    }
                    setOnlineUserCredentialmsg(userInfo);
                    PrimeFaces.current().executeScript("PF('buiVarNoImage').show();");
                    PrimeFaces.current().ajax().update("form_registered_vehicle_fee:onlinePaymentTaxDialog");
                    PrimeFaces.current().executeScript("PF('onlinePaymentTaxvar').show();");
                } else {
                    throw new VahanException("Error in Saving Details !!");
                }
            }
        } catch (VahanException ve) {
            e = ve;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            e = new Exception("Error in Saving Details !!");
        }
        if (e != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
        return "";
    }

    public void cancelOnlinePay() {
        try {
            boolean flag = ServerUtil.cancelOnlinePayment(getAppl_no(), TableConstants.TAX_MODE_BALANCE);
            if (flag) {
                setBtn_save(true);
                setBtn_reset(true);
                setRenderOnlinePayBtn(false);
                setRenderOnlineCancelBtn(false);
                setOnlineUserCredentialmsg("");
                setAppl_no("");
                getList_tax_diff().clear();
                getFeeCollectionLists().clear();
                setTotalAmountPayable(0l);
                setTotalUserChrg((Long) 0l);
                getFeePanelBean().setTotalFeeAmount(0l);
                getFeePanelBean().setTotalFineAmount(0l);
                getFeePanelBean().setTotalAmount(0l);
                setMobileNo(0l);
                setRegn_no("");
                setOwnerName("");
                setSelectedOption("A");
                setDisableApplNo(false);
                setRenderApplNo(true);
                setRenderRegnNo(false);
                setRenderTempRegnNo(false);
                setCarrierRegnPanel(false);
                setOtherStatePanel(false);
                setDisableSelectedOption(false);
                setVehicleDtlsPanel(false);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info!!!", "Online payment cancel successfully !!!"));
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void getOnlineFeeTax(String stateCd, int offCd, String applNo) {
        Long totalFeeAmt = 0l;
        Long totalFineAmt = 0l;
        setTotalAmountPayable(0l);
        try {
            List<TaxFormPanelBean> feeTaxList = new FeeImpl().getOnlineFeeTaxDetails(stateCd, offCd, applNo);
            if (feeTaxList != null && !feeTaxList.isEmpty()) {
                getList_tax_diff().clear();
                getFeeCollectionLists().clear();
                for (TaxFormPanelBean obj : feeTaxList) {
                    if (obj.getFinalTaxFrom() != null && !"".equals(obj.getFinalTaxFrom())) {
                        TaxFormPanelBean selectedOnlineTaxObj = new TaxFormPanelBean();
                        selectedOnlineTaxObj.setPur_cd(obj.getPur_cd());
                        selectedOnlineTaxObj.setFinalTaxFrom(obj.getFinalTaxFrom());
                        selectedOnlineTaxObj.setFinalTaxUpto(obj.getFinalTaxUpto());
                        selectedOnlineTaxObj.setTotalPaybaleTax(obj.getTotalPayableAmount());
                        selectedOnlineTaxObj.setFinalTaxAmount(obj.getFinalTaxAmount());
                        selectedOnlineTaxObj.setTotalPaybalePenalty(obj.getTotalPaybalePenalty());
                        selectedOnlineTaxObj.setTotalPaybaleRebate(obj.getTotalPaybaleRebate());
                        selectedOnlineTaxObj.setTotalTax1(obj.getTotalTax1());
                        selectedOnlineTaxObj.setTotalTax2(obj.getTotalTax2());
                        selectedOnlineTaxObj.setTotalPayablePrvAdj(obj.getTotalPayablePrvAdj());
                        selectedOnlineTaxObj.setTotalPaybaleInterest(obj.getTotalPaybaleInterest());
                        selectedOnlineTaxObj.setTotalPaybaleSurcharge(obj.getTotalPaybaleSurcharge());
                        setTotalAmountPayable(getTotalAmountPayable() + obj.getFinalTaxAmount());
                        getList_tax_diff().add(selectedOnlineTaxObj);
                    } else if ("".equals(obj.getFinalTaxFrom()) && "".equals(obj.getFinalTaxUpto())) {
                        if (TableConstants.VM_TRANSACTION_MAST_USER_CHARGES == obj.getPur_cd()) {
                            setRenderUserChargesAmountPanel(true);
                            setTotalUserChrg((Long) obj.getTotalPayableAmount());
                        } else {
                            FeeDobj selectedOnlineFeeObj = new FeeDobj();
                            selectedOnlineFeeObj.setPurCd(obj.getPur_cd());
                            selectedOnlineFeeObj.setFeeAmount(obj.getTotalPayableAmount());
                            selectedOnlineFeeObj.setFineAmount(obj.getTotalPaybalePenalty());
                            getFeeCollectionLists().add(selectedOnlineFeeObj);
                            totalFeeAmt = totalFeeAmt + obj.getTotalPayableAmount();
                            getFeePanelBean().setTotalFeeAmount(totalFeeAmt);
                            totalFineAmt = totalFineAmt + obj.getTotalPaybalePenalty();
                            getFeePanelBean().setTotalFineAmount(totalFineAmt);
                            getFeePanelBean().setTotalAmount(getFeePanelBean().getTotalFeeAmount() + getFeePanelBean().getTotalFineAmount());
                        }
                    }
                }
                setTotalAmountPayable(getTotalAmountPayable() + getFeePanelBean().getTotalAmount() + getTotalUserChrg());
            }
        } catch (Exception e) {
        }
    }

    public void calculateBalanceAmount(long totalAmount) {
        long totalBal = 0;

        for (PaymentCollectionDobj dobj : getPaymentBean().getPaymentlist()) {
            if (dobj.getAmount() != null) {
                totalBal = totalBal + Long.parseLong(dobj.getAmount());
            }
        }
        if ((totalAmount - totalBal) < 0) {
            getPaymentBean().setBalanceAmount(0);
        } else {
            getPaymentBean().setBalanceAmount((totalAmount) - totalBal);
        }
        getPaymentBean().setAmountInCash(totalBal);
    }

    public void addNewRow(Integer purCd) {
        String mode = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("actionmode");
        if ("add".equalsIgnoreCase(mode)) {
            if (purCd == null || purCd == -1) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Select Fee Head!", "Select Fee Head!");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
            // add row in the fee panel
            if (getFeeCollectionLists().size() == 7) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Maximum number of Fees heads collection reached", "Maximum number of Fees heads collection reached");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            } else {
                FeeDobj fee = new FeeDobj();
                fee.setDisableDropDown(false);
                fee.setReadOnlyFee(false);
                getFeeCollectionLists().add(fee);
                getFeePanelBean().setEnable(false);
            }

        } else if ("minus".equalsIgnoreCase(mode)) {
            // remove current row from table.
            boolean isExist = feeImpl.checkPurCDExist(appl_no, purCd);
            if (isExist) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Fee Details can't be remove!", "Fee Details can't be remove!");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
            FeeDobj selectedFeeObject = new FeeDobj(purCd);
            int lastIndex = getFeeCollectionLists().lastIndexOf(selectedFeeObject);
            if (lastIndex == 0 && getFeeCollectionLists().size() == 1) {
                getFeeCollectionLists().clear();
                FeeDobj fee = new FeeDobj();
                fee.setDisableDropDown(false);
                fee.setReadOnlyFee(false);
                getFeeCollectionLists().add(fee);
                getFeePanelBean().setTotalFeeAmount(0);
                getFeePanelBean().setTotalFineAmount(0);
                getFeePanelBean().setTotalAmount(0);
                calculateTotal();
            } else {
                getFeeCollectionLists().remove(lastIndex);
                calculateTotal();
            }
        }
    }

    public void addNewRowTaxDiff(Integer purCd) {
        String mode = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("actionmode");
        if ("addTaxDiff".equalsIgnoreCase(mode)) {
            if (purCd == null || purCd == -1) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Select Tax Head!", "Select Tax Head!");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
            // add row in the fee panel
            if (getList_tax_diff().size() == 7) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Maximum number of Tax heads collection reached", "Maximum number of Tax heads collection reached");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            } else {
                TaxFormPanelBean dotd = new TaxFormPanelBean();
                getList_tax_diff().add(dotd);
            }

        } else if ("minusTaxDiff".equalsIgnoreCase(mode)) {
            // remove current row from table.
            boolean isExist = feeImpl.checkPurCDExist(appl_no, purCd);
            if (isExist) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Tax Details can't be remove!", "Tax Details can't be remove!");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
            TaxFormPanelBean selectedtaxObject = new TaxFormPanelBean(purCd);
            int lastIndex = getList_tax_diff().lastIndexOf(selectedtaxObject);
            if (lastIndex == 0 && getList_tax_diff().size() == 1) {
                getList_tax_diff().clear();
                TaxFormPanelBean dotd = new TaxFormPanelBean();
                getList_tax_diff().add(dotd);
            } else {
                getList_tax_diff().remove(lastIndex);
            }
            updateTotalPayableAmount();
        }
        // RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("form_registered_vehicle_fee:sub_view_tax_diff:tableTaxDiff");
    }

    public void selectTaxListner(int pur_cd) {
        Date maxDate = null;
        try {
            TaxFormPanelBean selectedTaxObject = new TaxFormPanelBean(pur_cd);
            if (!CommonUtils.isNullOrBlank(regn_no) && pur_cd == 58 && selectedOption.equalsIgnoreCase("R")) {
                TaxInstallCollectionImpl taxInst = new TaxInstallCollectionImpl();
                if (!CommonUtils.isNullOrBlank(taxInst.getCheckInstallmentList(regn_no, sessionVariables.getStateCodeSelected())) && !list_tax_diff.isEmpty()) {
                    TaxFormPanelBean currentSelectedTaxObject = list_tax_diff.get(list_tax_diff.size() - 1);
                    currentSelectedTaxObject.setPur_cd(-1);
                    JSFUtils.setFacesMessage("Registration Number already make Tax Installment.", "Registration Number already make Tax Installment.", JSFUtils.ERROR);
                    return;
                }
            }
            if (selectedOption.equalsIgnoreCase("R")) {
                maxDate = TaxServer_Impl.getTaxDueFromDate(owDobj, pur_cd);
                maxDate = DateUtils.getDateBeforeGivenDays(maxDate, 1);
                selectedTaxObject.setMinFromDate(owDobj.getPurchase_dt());
                if (maxDate.before(owDobj.getPurchase_dt())) {
                    maxDate = null;
                }
                if (maxDate != null && maxDate.before(vow4Date)) {
                    maxDate = vow4Date;
                }
                selectedTaxObject.setMinUptoDate(owDobj.getPurchase_dt());
                selectedTaxObject.setMaxFromDate(maxDate);
                selectedTaxObject.setMaxUptoDate(maxDate);
                selectedTaxObject.setFinalEditableTaxFrom(selectedTaxObject.getMinFromDate());
                selectedTaxObject.setFinalEditableTaxUpto(selectedTaxObject.getMaxUptoDate());

            }
            int lastIndex = getList_tax_diff().lastIndexOf(selectedTaxObject);
            getList_tax_diff().remove(lastIndex);
            if (!getList_tax_diff().contains(selectedTaxObject)) {

                ServerUtil serverUtil = new ServerUtil();
                TaxDobj taxDobj = serverUtil.getBalanceTaxDetails(regn_no, appl_no, pur_cd, sessionVariables.getStateCodeSelected(), selectedOption);
                if (taxDobj != null && taxDobj.getTax_amt() > 0) {
                    selectedTaxObject.setPur_cd(taxDobj.getPur_cd());
                    selectedTaxObject.setFinalEditableTaxFrom(taxDobj.getTax_from());
                    selectedTaxObject.setFinalEditableTaxUpto(taxDobj.getTax_upto());
                    selectedTaxObject.setTotalPaybaleTax(taxDobj.getTax_amt());
                    selectedTaxObject.setFinalTaxAmount(taxDobj.getTax_amt());
                    selectedTaxObject.setDisableTaxAmt(true);
                    selectedTaxObject.setMinFromDate(selectedTaxObject.getFinalEditableTaxFrom());
                    selectedTaxObject.setMaxFromDate(selectedTaxObject.getFinalEditableTaxUpto());

                    selectedTaxObject.setMinUptoDate(selectedTaxObject.getFinalEditableTaxFrom());
                    selectedTaxObject.setMaxUptoDate(selectedTaxObject.getFinalEditableTaxUpto());

                } else {
                    if ("OR".equalsIgnoreCase(sessionVariables.getStateCodeSelected())) {
                        JSFUtils.setFacesMessage("Balance Tax Recovery Amount details not found, please enter first Balance Tax Recovery Amount in Tax Clearance/Balance Option.", "Balance Tax Recovery Amount details not found, please enter first Balance Tax Recovery Amount in Tax Clearance/Balance Option.", JSFUtils.ERROR);
                        selectedTaxObject.setPur_cd(-1);
                        getList_tax_diff().add(selectedTaxObject);
                        return;
                    }
                }
                getList_tax_diff().add(selectedTaxObject);
                updateTotalPayableAmount();
            } else {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Tax Head Already Selected!", "Tax Head Already Selected!");
                FacesContext.getCurrentInstance().addMessage(null, message);
                updateTotalPayableAmount();
                return;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
    }

    public void calculateTotal() {
        long sumOfFee = 0;
        long sumOfFine = 0;
        long sumOfTotal = 0;
        for (FeeDobj sumDobj : getFeeCollectionLists()) {
            sumOfFee = sumOfFee + (sumDobj.getFeeAmount() == null ? 0 : sumDobj.getFeeAmount());
            sumOfFine = sumOfFine + (sumDobj.getFineAmount() == null ? 0 : sumDobj.getFineAmount());
            sumOfTotal = sumOfTotal + (sumDobj.getTotalAmount() == null ? 0 : sumDobj.getTotalAmount());
            getFeePanelBean().setTotalFeeAmount(sumOfFee);
            getFeePanelBean().setTotalFineAmount(sumOfFine);
            getFeePanelBean().setTotalAmount(sumOfTotal);
            updateTotalPayableAmount();
        }
    }

    public void calculateTotalTaxDifference(TaxFormPanelBean bean) {
        long totalAmount = 0L;
        totalAmount = bean.getTotalPaybaleTax() + bean.getTotalTax1() + bean.getTotalTax2() + bean.getTotalPaybalePenalty() + bean.getTotalPayablePrvAdj() + bean.getTotalPaybaleInterest() - bean.getTotalPaybaleRebate() + bean.getTotalPaybaleSurcharge();
        bean.setFinalTaxAmount(totalAmount);
        //RequestContext ca = RequestContext.getCurrentInstance();
        updateTotalPayableAmount();
    }

    public void listnerUpdateUpto(TaxFormPanelBean bean) {
        bean.setMinUptoDate(bean.getFinalEditableTaxFrom());
    }

    public void updateTotalPayableAmount() {
        long totalTaxAountDIff = 0L;
        List<Integer> listPurCd = new ArrayList<>();
        for (TaxFormPanelBean bean : list_tax_diff) {
            totalTaxAountDIff = totalTaxAountDIff + bean.getFinalTaxAmount();
            if (!"PY,HP".contains(sessionVariables.getStateCodeSelected())) {
                if (EpayImpl.getServiceChargeType() != null) {
                    if (bean.getTotalPaybaleTax() > 0) {
                        listPurCd.add(bean.getPur_cd());
                    }
                }
            }
        }

        setTotalAmountPayable(0);
        Long userCharges = 0L;
        if (!"PY,HP".contains(sessionVariables.getStateCodeSelected())) {
            if (EpayImpl.getServiceChargeType() != null) {
                for (FeeDobj fee : getFeeCollectionLists()) {
                    if (fee.getTotalAmount() > 0) {
                        listPurCd.add(fee.getPurCd());
                    }
                }
                userCharges = EpayImpl.getUserChargesFee(null, listPurCd, null);
                setTotalUserChrg(userCharges);
                if (userCharges > 0) {
                    setRenderUserChargesAmountPanel(true);
                }
            } else {
                setRenderUserChargesAmountPanel(false);
            }
        }
        if (getFeePanelBean().getTotalAmount() > 0 || totalTaxAountDIff > 0) {
            setTotalAmountPayable(getTotalAmountPayable() + getFeePanelBean().getTotalAmount() + userCharges + totalTaxAountDIff);
        }
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
     * @return the balDob
     */
    public BalanceCollectionFeeDobj getBalDob() {
        return balDob;
    }

    /**
     * @param balDob the balDob to set
     */
    public void setBalDob(BalanceCollectionFeeDobj balDob) {
        this.balDob = balDob;
    }

    /**
     * @return the confirmPopUp
     */
    public boolean isConfirmPopUp() {
        return confirmPopUp;
    }

    /**
     * @param confirmPopUp the confirmPopUp to set
     */
    public void setConfirmPopUp(boolean confirmPopUp) {
        this.confirmPopUp = confirmPopUp;
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
     * @return the ownerDobj
     */
    public OwnerDetailsDobj getOwnerDobj() {
        return ownerDobj;
    }

    /**
     * @param ownerDobj the ownerDobj to set
     */
    public void setOwnerDobj(OwnerDetailsDobj ownerDobj) {
        this.ownerDobj = ownerDobj;
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
     * @return the renderVehdetails
     */
    public boolean isRenderVehdetails() {
        return renderVehdetails;
    }

    /**
     * @param renderVehdetails the renderVehdetails to set
     */
    public void setRenderVehdetails(boolean renderVehdetails) {
        this.renderVehdetails = renderVehdetails;
    }

    /**
     * @return the purposeCodeList
     */
    public List<SelectItem> getPurposeCodeList() {
        return purposeCodeList;
    }

    /**
     * @param purposeCodeList the purposeCodeList to set
     */
    public void setPurposeCodeList(List<SelectItem> purposeCodeList) {
        this.purposeCodeList = purposeCodeList;
    }

    /**
     * @return the listTransWise
     */
    public List<FeeDobj> getListTransWise() {
        return listTransWise;
    }

    /**
     * @param listTransWise the listTransWise to set
     */
    public void setListTransWise(List<FeeDobj> listTransWise) {
        this.listTransWise = listTransWise;
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
     * @return the userChrg
     */
    public Long getUserChrg() {
        return userChrg;
    }

    /**
     * @param userChrg the userChrg to set
     */
    public void setUserChrg(Long userChrg) {
        this.userChrg = userChrg;
    }

    /**
     * @return the disableApplNo
     */
    public boolean isDisableApplNo() {
        return disableApplNo;
    }

    /**
     * @param disableApplNo the disableApplNo to set
     */
    public void setDisableApplNo(boolean disableApplNo) {
        this.disableApplNo = disableApplNo;
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
     * @return the generatedRcpt
     */
    public String getGeneratedRcpt() {
        return generatedRcpt;
    }

    /**
     * @param generatedRcpt the generatedRcpt to set
     */
    public void setGeneratedRcpt(String generatedRcpt) {
        this.generatedRcpt = generatedRcpt;
    }

    public void reset() {

        setAppl_no("");
        getFeePanelBean().reset();
        getPaymentBean().reset();
        //setBtn_save(false);
        getFeeCollectionLists().clear();
        getFeePanelBean().getFeeCollectionList().clear();
        getFeePanelBean().getPayableFeeCollectionList().clear();
        updateTotalPayableAmount();
        ownerDobj = null;
    }

    /**
     * @return the showInRto
     */
    public boolean isShowInRto() {
        return showInRto;
    }

    /**
     * @param showInRto the showInRto to set
     */
    public void setShowInRto(boolean showInRto) {
        this.showInRto = showInRto;
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
     * @return the renderApplNo
     */
    public boolean isRenderApplNo() {
        return renderApplNo;
    }

    /**
     * @param renderApplNo the renderApplNo to set
     */
    public void setRenderApplNo(boolean renderApplNo) {
        this.renderApplNo = renderApplNo;
    }

    /**
     * @return the selectedOption
     */
    public String getSelectedOption() {
        return selectedOption;
    }

    /**
     * @param selectedOption the selectedOption to set
     */
    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public void selectedOptionListener(AjaxBehaviorEvent ajax) {
        purposeCodeList.clear();
        purposeCodeList.add(new SelectItem("-1", "Select Fee"));
        if (selectedOption.equals("A") || selectedOption.equals("R") || selectedOption.equals("T")) {
            if (selectedOption.equals("A")) {
                renderApplNo = true;
                setRenderRegnNo(false);
                setRenderTempRegnNo(false);
                setRegn_no(null);
            } else if (selectedOption.equals("T")) {
                renderApplNo = false;
                setRenderRegnNo(false);
                setRenderTempRegnNo(true);
                setRegn_no(null);
            } else {
                renderApplNo = false;
                setRenderRegnNo(true);
                setRenderTempRegnNo(false);
                setAppl_no(null);
            }
            setCarrierRegnPanel(false);
            setOwnerName(null);
            setOtherStatePanel(false);
            setVehicleDtlsPanel(true);
            for (int i = 0; i < data.length; i++) {
                if (data[i][6].trim().equalsIgnoreCase("true")) {
                    purposeCodeList.add(new SelectItem(getData()[i][0], getData()[i][1]));
                }
            }

            for (FeeDobj fee : listAll) {
                purposeCodeList.add(new SelectItem(fee.getPurCd(), fee.getFeeHeadDescr()));
            }

            isTaxDiffDetails = true;
        } else if (selectedOption.equalsIgnoreCase("C")) {
            setCarrierRegnPanel(true);
            setRenderApplNo(false);
            setRenderRegnNo(false);
            setOtherStatePanel(false);
            setRegn_no(null);
            setAppl_no(null);
            setVehicleDtlsPanel(false);
            setRenderTempRegnNo(false);
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].trim().equalsIgnoreCase("359") || data[i][0].trim().equalsIgnoreCase("360")
                        || data[i][0].trim().equalsIgnoreCase("361") || data[i][0].trim().equalsIgnoreCase("362")
                        || data[i][0].trim().equalsIgnoreCase("371") || data[i][0].trim().equalsIgnoreCase("372")
                        || data[i][0].trim().equalsIgnoreCase("447") || data[i][0].trim().equalsIgnoreCase("448")
                        || data[i][0].trim().equalsIgnoreCase("449")) {
                    purposeCodeList.add(new SelectItem(getData()[i][0], getData()[i][1]));
                }
            }

            for (FeeDobj fee : listAll) {
                purposeCodeList.add(new SelectItem(fee.getPurCd(), fee.getFeeHeadDescr()));
            }
            isTaxDiffDetails = false;
        } else if (selectedOption.equalsIgnoreCase("O")) {
            setCarrierRegnPanel(false);
            setRenderApplNo(false);
            setRenderRegnNo(false);
            setOtherStatePanel(true);
            setRegn_no(null);
            setAppl_no(null);
            setVehicleDtlsPanel(false);
            setRenderTempRegnNo(false);
            for (int i = 0; i < data.length; i++) {
                if (data[i][6].trim().equalsIgnoreCase("true")) {
                    purposeCodeList.add(new SelectItem(getData()[i][0], getData()[i][1]));
                }
            }

            for (FeeDobj fee : listAll) {
                purposeCodeList.add(new SelectItem(fee.getPurCd(), fee.getFeeHeadDescr()));
            }
            isTaxDiffDetails = true;
        } else if (selectedOption.equalsIgnoreCase("D")) {
            setCarrierRegnPanel(false);
            setRenderApplNo(false);
            setRenderRegnNo(true);
            setOtherStatePanel(false);
            setRegn_no(null);
            setAppl_no(null);
            setVehicleDtlsPanel(false);
            setRenderTempRegnNo(false);
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].trim().equalsIgnoreCase("201")) {
                    purposeCodeList.add(new SelectItem(getData()[i][0], getData()[i][1]));
                }
            }

            for (FeeDobj fee : listAll) {
                purposeCodeList.add(new SelectItem(fee.getPurCd(), fee.getFeeHeadDescr()));
            }
            isTaxDiffDetails = false;
        }
        feePanelBean.setPurposeCodeList(purposeCodeList);

    }

    /**
     * @return the disableSelectedOption
     */
    public boolean isDisableSelectedOption() {
        return disableSelectedOption;
    }

    /**
     * @param disableSelectedOption the disableSelectedOption to set
     */
    public void setDisableSelectedOption(boolean disableSelectedOption) {
        this.disableSelectedOption = disableSelectedOption;
    }

    /**
     * @return the renderFeePanelLabel
     */
    public boolean isRenderFeePanelLabel() {
        return renderFeePanelLabel;
    }

    /**
     * @param renderFeePanelLabel the renderFeePanelLabel to set
     */
    public void setRenderFeePanelLabel(boolean renderFeePanelLabel) {
        this.renderFeePanelLabel = renderFeePanelLabel;
    }

    /**
     * @return the balfeeremark
     */
    public String getBalfeeremark() {
        return balfeeremark;
    }

    /**
     * @param balfeeremark the balfeeremark to set
     */
    public void setBalfeeremark(String balfeeremark) {
        this.balfeeremark = balfeeremark;
    }

    /**
     * @return the ownerName
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * @param ownerName the ownerName to set
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * @return the carrierRegnPanel
     */
    public boolean isCarrierRegnPanel() {
        return carrierRegnPanel;
    }

    /**
     * @param carrierRegnPanel the carrierRegnPanel to set
     */
    public void setCarrierRegnPanel(boolean carrierRegnPanel) {
        this.carrierRegnPanel = carrierRegnPanel;
    }

    /**
     * @return the renderRegnNo
     */
    public boolean isRenderRegnNo() {
        return renderRegnNo;
    }

    /**
     * @param renderRegnNo the renderRegnNo to set
     */
    public void setRenderRegnNo(boolean renderRegnNo) {
        this.renderRegnNo = renderRegnNo;
    }

    /**
     * @return the data
     */
    public String[][] getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(String[][] data) {
        this.data = data;
    }

    /**
     * @return the otherStatePanel
     */
    public boolean isOtherStatePanel() {
        return otherStatePanel;
    }

    /**
     * @param otherStatePanel the otherStatePanel to set
     */
    public void setOtherStatePanel(boolean otherStatePanel) {
        this.otherStatePanel = otherStatePanel;
    }

    /**
     * @return the vehicleDtlsPanel
     */
    public boolean isVehicleDtlsPanel() {
        return vehicleDtlsPanel;
    }

    /**
     * @param vehicleDtlsPanel the vehicleDtlsPanel to set
     */
    public void setVehicleDtlsPanel(boolean vehicleDtlsPanel) {
        this.vehicleDtlsPanel = vehicleDtlsPanel;
    }

    /**
     * @return the autoRunTaxListener
     */
    public boolean isAutoRunTaxListener() {
        return autoRunTaxListener;
    }

    /**
     * @param autoRunTaxListener the autoRunTaxListener to set
     */
    public void setAutoRunTaxListener(boolean autoRunTaxListener) {
        this.autoRunTaxListener = autoRunTaxListener;
    }

    /**
     * @return the listAll
     */
    public List<FeeDobj> getListAll() {
        return listAll;
    }

    /**
     * @param listAll the listAll to set
     */
    public void setListAll(List<FeeDobj> listAll) {
        this.listAll = listAll;
    }

    /**
     * @return the checkFeeTax
     */
    public EpayDobj getCheckFeeTax() {
        return checkFeeTax;
    }

    /**
     * @param checkFeeTax the checkFeeTax to set
     */
    public void setCheckFeeTax(EpayDobj checkFeeTax) {
        this.checkFeeTax = checkFeeTax;
    }

    public List<TaxFormPanelBean> getList_tax_diff() {
        return list_tax_diff;
    }

    public void setList_tax_diff(List<TaxFormPanelBean> list_tax_diff) {
        this.list_tax_diff = list_tax_diff;
    }

    public List getTax_diff_pur_cd() {
        return tax_diff_pur_cd;
    }

    public void setTax_diff_pur_cd(List tax_diff_pur_cd) {
        this.tax_diff_pur_cd = tax_diff_pur_cd;
    }

    public boolean isIsTaxDiffDetails() {
        return isTaxDiffDetails;
    }

    public void setIsTaxDiffDetails(boolean isTaxDiffDetails) {
        this.isTaxDiffDetails = isTaxDiffDetails;
    }

    /**
     * @return the rendertempFeeAmount
     */
    public boolean isRendertempFeeAmount() {
        return rendertempFeeAmount;
    }

    /**
     * @param rendertempFeeAmount the rendertempFeeAmount to set
     */
    public void setRendertempFeeAmount(boolean rendertempFeeAmount) {
        this.rendertempFeeAmount = rendertempFeeAmount;
    }

    public boolean isRenderShowButton() {
        return renderShowButton;
    }

    public void setRenderShowButton(boolean renderShowButton) {
        this.renderShowButton = renderShowButton;
    }

    public boolean isOptionCCarrierRegistration() {
        return optionCCarrierRegistration;
    }

    public void setOptionCCarrierRegistration(boolean optionCCarrierRegistration) {
        this.optionCCarrierRegistration = optionCCarrierRegistration;
    }

    public boolean isCcarrierRegnPanel() {
        return ccarrierRegnPanel;
    }

    public void setCcarrierRegnPanel(boolean ccarrierRegnPanel) {
        this.ccarrierRegnPanel = ccarrierRegnPanel;
    }

    public boolean isCarrierFlowRegnPanel() {
        return carrierFlowRegnPanel;
    }

    public void setCarrierFlowRegnPanel(boolean carrierFlowRegnPanel) {
        this.carrierFlowRegnPanel = carrierFlowRegnPanel;
    }

    public boolean isOptionForMizoram() {
        return optionForMizoram;
    }

    public void setOptionForMizoram(boolean optionForMizoram) {
        this.optionForMizoram = optionForMizoram;
    }

    /**
     * @return the detailCommonCarrierDobj
     */
    public DetailCommonCarrierDobj getDetailCommonCarrierDobj() {
        return detailCommonCarrierDobj;
    }

    /**
     * @param detailCommonCarrierDobj the detailCommonCarrierDobj to set
     */
    public void setDetailCommonCarrierDobj(DetailCommonCarrierDobj detailCommonCarrierDobj) {
        this.detailCommonCarrierDobj = detailCommonCarrierDobj;
    }

    /**
     * @return the ccDobj
     */
    public List<DetailCommonCarrierDobj> getCcDobj() {
        return ccDobj;
    }

    /**
     * @param ccDobj the ccDobj to set
     */
    public void setCcDobj(List<DetailCommonCarrierDobj> ccDobj) {
        this.ccDobj = ccDobj;
    }

    /**
     * @return the feeImpl
     */
    public FeeImpl getFeeImpl() {
        return feeImpl;
    }

    /**
     * @param feeImpl the feeImpl to set
     */
    public void setFeeImpl(FeeImpl feeImpl) {
        this.feeImpl = feeImpl;
    }

    /**
     * @return the allowedPurposeCd
     */
    public List<String> getAllowedPurposeCd() {
        return allowedPurposeCd;
    }

    /**
     * @param allowedPurposeCd the allowedPurposeCd to set
     */
    public void setAllowedPurposeCd(List<String> allowedPurposeCd) {
        this.allowedPurposeCd = allowedPurposeCd;
    }

    /**
     * @return the printRcptNo
     */
    public String getPrintRcptNo() {
        return printRcptNo;
    }

    /**
     * @param printRcptNo the printRcptNo to set
     */
    public void setPrintRcptNo(String printRcptNo) {
        this.printRcptNo = printRcptNo;
    }

    /**
     * @return the sessionVariables
     */
    public SessionVariables getSessionVariables() {
        return sessionVariables;
    }

    /**
     * @param sessionVariables the sessionVariables to set
     */
    public void setSessionVariables(SessionVariables sessionVariables) {
        this.sessionVariables = sessionVariables;
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
     * @return the renderOnlineCancelBtn
     */
    public boolean isRenderOnlineCancelBtn() {
        return renderOnlineCancelBtn;
    }

    /**
     * @param renderOnlineCancelBtn the renderOnlineCancelBtn to set
     */
    public void setRenderOnlineCancelBtn(boolean renderOnlineCancelBtn) {
        this.renderOnlineCancelBtn = renderOnlineCancelBtn;
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
     * @return the paymentMsg
     */
    public String getPaymentMsg() {
        return paymentMsg;
    }

    /**
     * @param paymentMsg the paymentMsg to set
     */
    public void setPaymentMsg(String paymentMsg) {
        this.paymentMsg = paymentMsg;
    }

    /**
     * @return the mobileNo
     */
    public Long getMobileNo() {
        return mobileNo;
    }

    /**
     * @param mobileNo the mobileNo to set
     */
    public void setMobileNo(Long mobileNo) {
        this.mobileNo = mobileNo;
    }

    /**
     * @return the renderAddTaxBtn
     */
    public boolean isRenderAddTaxBtn() {
        return renderAddTaxBtn;
    }

    /**
     * @param renderAddTaxBtn the renderAddTaxBtn to set
     */
    public void setRenderAddTaxBtn(boolean renderAddTaxBtn) {
        this.renderAddTaxBtn = renderAddTaxBtn;
    }

    /**
     * @return the optionRePostalFee
     */
    public boolean isOptionRePostalFee() {
        return optionRePostalFee;
    }

    /**
     * @param optionRePostalFee the optionRePostalFee to set
     */
    public void setOptionRePostalFee(boolean optionRePostalFee) {
        this.optionRePostalFee = optionRePostalFee;
    }

    /**
     * @return the renderTempRegnNo
     */
    public boolean isRenderTempRegnNo() {
        return renderTempRegnNo;
    }

    /**
     * @param renderTempRegnNo the renderTempRegnNo to set
     */
    public void setRenderTempRegnNo(boolean renderTempRegnNo) {
        this.renderTempRegnNo = renderTempRegnNo;
    }

    /**
     * @return the blackListedCompoundingAmt
     */
    public boolean isBlackListedCompoundingAmt() {
        return blackListedCompoundingAmt;
    }

    /**
     * @param blackListedCompoundingAmt the blackListedCompoundingAmt to set
     */
    public void setBlackListedCompoundingAmt(boolean blackListedCompoundingAmt) {
        this.blackListedCompoundingAmt = blackListedCompoundingAmt;
    }

    /**
     * @return the owDobj
     */
    public Owner_dobj getOwDobj() {
        return owDobj;
    }

    /**
     * @param owDobj the owDobj to set
     */
    public void setOwDobj(Owner_dobj owDobj) {
        this.owDobj = owDobj;
    }

    /**
     * @return the taxPaidLabel
     */
    public String getTaxPaidLabel() {
        return taxPaidLabel;
    }

    /**
     * @param taxPaidLabel the taxPaidLabel to set
     */
    public void setTaxPaidLabel(String taxPaidLabel) {
        this.taxPaidLabel = taxPaidLabel;
    }

    /**
     * @return the addTaxPaidLabel
     */
    public String getAddTaxPaidLabel() {
        return addTaxPaidLabel;
    }

    /**
     * @param addTaxPaidLabel the addTaxPaidLabel to set
     */
    public void setAddTaxPaidLabel(String addTaxPaidLabel) {
        this.addTaxPaidLabel = addTaxPaidLabel;
    }

    /**
     * @return the taxClearLabel
     */
    public String getTaxClearLabel() {
        return taxClearLabel;
    }

    /**
     * @param taxClearLabel the taxClearLabel to set
     */
    public void setTaxClearLabel(String taxClearLabel) {
        this.taxClearLabel = taxClearLabel;
    }

    /**
     * @return the addTaxClearLabel
     */
    public String getAddTaxClearLabel() {
        return addTaxClearLabel;
    }

    /**
     * @param addTaxClearLabel the addTaxClearLabel to set
     */
    public void setAddTaxClearLabel(String addTaxClearLabel) {
        this.addTaxClearLabel = addTaxClearLabel;
    }

    /**
     * @return the taxExempLabel
     */
    public String getTaxExempLabel() {
        return taxExempLabel;
    }

    /**
     * @param taxExempLabel the taxExempLabel to set
     */
    public void setTaxExempLabel(String taxExempLabel) {
        this.taxExempLabel = taxExempLabel;
    }

    /**
     * @return the renderTaxPaid
     */
    public boolean isRenderTaxPaid() {
        return renderTaxPaid;
    }

    /**
     * @param renderTaxPaid the renderTaxPaid to set
     */
    public void setRenderTaxPaid(boolean renderTaxPaid) {
        this.renderTaxPaid = renderTaxPaid;
    }

    /**
     * @return the renderAddtaxPaid
     */
    public boolean isRenderAddtaxPaid() {
        return renderAddtaxPaid;
    }

    /**
     * @param renderAddtaxPaid the renderAddtaxPaid to set
     */
    public void setRenderAddtaxPaid(boolean renderAddtaxPaid) {
        this.renderAddtaxPaid = renderAddtaxPaid;
    }

    /**
     * @return the renderTaxClear
     */
    public boolean isRenderTaxClear() {
        return renderTaxClear;
    }

    /**
     * @param renderTaxClear the renderTaxClear to set
     */
    public void setRenderTaxClear(boolean renderTaxClear) {
        this.renderTaxClear = renderTaxClear;
    }

    /**
     * @return the renderAddtaxClear
     */
    public boolean isRenderAddtaxClear() {
        return renderAddtaxClear;
    }

    /**
     * @param renderAddtaxClear the renderAddtaxClear to set
     */
    public void setRenderAddtaxClear(boolean renderAddtaxClear) {
        this.renderAddtaxClear = renderAddtaxClear;
    }

    /**
     * @return the renderTaxExemp
     */
    public boolean isRenderTaxExemp() {
        return renderTaxExemp;
    }

    /**
     * @param renderTaxExemp the renderTaxExemp to set
     */
    public void setRenderTaxExemp(boolean renderTaxExemp) {
        this.renderTaxExemp = renderTaxExemp;
    }

    /**
     * @return the btn_reset
     */
    public boolean isBtn_reset() {
        return btn_reset;
    }

    /**
     * @param btn_reset the btn_reset to set
     */
    public void setBtn_reset(boolean btn_reset) {
        this.btn_reset = btn_reset;
    }

    /**
     * @return the payFeeTaxAndBtnPanel
     */
    public boolean isPayFeeTaxAndBtnPanel() {
        return payFeeTaxAndBtnPanel;
    }

    /**
     * @param payFeeTaxAndBtnPanel the payFeeTaxAndBtnPanel to set
     */
    public void setPayFeeTaxAndBtnPanel(boolean payFeeTaxAndBtnPanel) {
        this.payFeeTaxAndBtnPanel = payFeeTaxAndBtnPanel;
    }

    public Date getVow4Date() {
        return vow4Date;
    }

    public void setVow4Date(Date vow4Date) {
        this.vow4Date = vow4Date;
    }

    public String getVow4_started_message() {
        return vow4_started_message;
    }

    public void setVow4_started_message(String vow4_started_message) {
        this.vow4_started_message = vow4_started_message;
    }
}
