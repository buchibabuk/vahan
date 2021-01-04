/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import nic.vahan.form.bean.permit.PermitPanelBean;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;
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
import static nic.vahan.CommonUtils.FormulaUtils.fillTaxParametersFromDobj;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.TaxUtils;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.eapplication.OnlinePaymentImpl;
import nic.vahan.form.dobj.AuditRecoveryDobj;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.FeeDobj;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.Fee_Pay_Dobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.NocDobj;
import nic.vahan.form.dobj.NonUseDobj;
import nic.vahan.form.dobj.OnlinePayDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TaxExemptiondobj;
import nic.vahan.form.dobj.TaxInstallCollectionDobj;
import nic.vahan.form.dobj.Tax_Pay_Dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.common.UserIDAndPasswordForOnlinePaymentDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.reports.TaxDefaulterListDobj;
import nic.vahan.form.dobj.tax.TaxDobj;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import nic.vahan.form.impl.EpayImpl;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.NocImpl;
import nic.vahan.form.impl.NonUseImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.permit.PermitImpl;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.tax.TaxImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
//import tax.web.service.VahanTaxParameters;
import nic.vahan.form.dobj.tax.VahanTaxParameters;
import static nic.vahan.form.impl.TaxServer_Impl.callTaxService;
import static nic.vahan.form.impl.TaxServer_Impl.callKLTaxService;
import nic.vahan.form.impl.admin.DelDupRegnNoImpl;
import nic.vahan.server.CommonUtils;

/**
 *
 * @author tranC094
 */
@ManagedBean(name = "roadTaxCollectionBean")
@ViewScoped
public class RoadTaxCollectionBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(RoadTaxCollectionBean.class);
    private String regn_no;
    private Owner_dobj ownerDobj = new Owner_dobj();
    private OwnerImpl ownerImpl = null;
    private TaxFormPanelBean taxFormBean = null;
    private PaymentCollectionBean paymentCollectionBean = null;
    private PermitPanelBean permitPanelBean = null;
    PermitImpl permit_Impl = null;
    @ManagedProperty(value = "#{rcpt_bean}")
    private ReceiptMasterBean rcpt_bean;
    private boolean disable = false;
    private ArrayList list_vh_class = new ArrayList();
    private ArrayList list_vm_catg = new ArrayList();
    private ArrayList list_maker_model = new ArrayList();
    private List<TaxFormPanelBean> listTaxForm = new ArrayList();
    private long totalTaxAmount;
    private long totalBalanceAmount;
    private long excessAmount;
    private long totalDraftAmount;
    private boolean renderPermitPanel = false;
    private String generatedRcptNo = null;
    String printRcptNo = null;
    private boolean btn_save = true;
    private long userChrg;
    private boolean renderUserChargesAmountPanel = false;
    private List<FeeDobj> listTransWise = null;
    private long totalUserChrg = 0l;
    private OwnerDetailsDobj ownerDtlsDobj;
    private boolean renderAllPanel = false;
    private boolean is_taxPaid = false;
    private boolean is_addtaxPaid = false;
    private boolean is_taxClear = false;
    private boolean is_addtaxClear = false;
    private String taxPaidLabel;
    private String addTaxPaidLabel;
    private String taxClearLabel;
    private String addTaxClearLabel;
    private boolean isTaxExemp = false;
    private String taxExempLabel;
    private boolean autoRunTaxListener = true;
    private boolean renderModelCost = false;
    @ManagedProperty(value = "#{commonCharges}")
    private CommonChargesForAllBean commonCharges;
    private boolean showCommonChargesForAll;
    private long commonChargesAmt = 0;
    private List<DOTaxDetail> taxDescriptionList = new ArrayList<>();
    private boolean taxAssesment;
    private String headerDescription = "Collection of MV Tax";
    private String registrationDetails;
    private SeatAllotedDetails selectedSeat;
    NonUseDobj nonUseDobj = null;
    private List<OwnerDetailsDobj> dupRegnList = new ArrayList<>();
    private SessionVariables sessionVariables = null;
    private boolean renderPushBackSeatPanel = false;
    private boolean renderExcessDraft = false;
    private String excessDraftMessage = null;
    private int currentOffCd = 0;
    private List<Status_dobj> statusList = new ArrayList<>();
    private String paymentTypeBtn = "";
    private boolean renderCancelPayment = false;
    private boolean renderUserAndPasswored = false;
    private boolean renderOnlinePayBtn = false;
    private String appl_no;
    private String onlineUserCredentialmsg = "";
    private boolean taxDefList;
    private OwnerDetailsDobj moveToHistoryOwnerDtls = null;
    private String currentOfficeName;
    private boolean renderMoveToHistoryButton = false;

    public RoadTaxCollectionBean() {
        taxAssesment = false;
        setTaxDefList(false);
    }

    @PostConstruct
    public void postConstruct() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || sessionVariables.getStateCodeSelected() == null) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Something went wrong, Please try again...", "Something went wrong, Please try again...");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        if (Util.getSelectedSeat() != null) {
            currentOffCd = Util.getSelectedSeat().getOff_cd();
        }

        if (currentOffCd == 0) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Something Went Wrong. Please try again after some time..."));
            return;
        }
        selectedSeat = Util.getSelectedSeat();
        ownerImpl = new OwnerImpl();
        paymentCollectionBean = new PaymentCollectionBean();
        permitPanelBean = new PermitPanelBean();
        permit_Impl = new PermitImpl();
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

        if (Util.getSelectedSeat() != null && Util.getSelectedSeat().getAction_cd() == TableConstants.TAX_ASSESMENT) {
            taxAssesment = true;
            headerDescription = "Tax Assessment";
        } else {
            headerDescription = "Collection of MV Tax";
            taxAssesment = false;
        }
    }

    public String regnNo_focusLost() {
        moveToHistoryOwnerDtls = null;
        if (Utility.isNullOrBlank(regn_no)) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Enter Vehicle Number!", "Please Enter Vehicle Number!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return null;
        }

        regn_no = regn_no.toUpperCase();
        listTaxForm.clear();
        //Local Variable to Hold value temporarilry 
        //So that all modifications can be applied to it before assigning to instance variable
        OwnerDetailsDobj ownDtls = null;
        try {

            if (ownerImpl != null) {

                /*if (TaxServer_Impl.validateTaxPaidForDay(regn_no)) {
                 throw new VahanMessageException("You can't pay the Motor Vehicle Tax twice on the same day!!! Please come again on next day or cancel the previous receipt and pay again.");
                 }*/
                String taxPaidInfo = TaxServer_Impl.taxPaidInfo(getRegn_no());
                if (taxPaidInfo != null) {
                    if (taxPaidInfo.contains("Error:")) {
                        taxPaidInfo = taxPaidInfo.replaceAll("Error:", "");
                        // throw new VahanMessageException(taxPaidInfo);
                    }

                    FacesMessage message = null;
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, taxPaidInfo, taxPaidInfo);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    // throw new VahanMessageException("You can't pay the Motor Vehicle Tax twice on the same day!!! Please come again on next day or cancel the previous receipt and pay again.");
                }

                List<Integer> listPendingPurcd = TaxServer_Impl.getPendingOnlinePurCodesForRechecks(this.regn_no.trim(), Util.getUserStateCode());
                if (!listPendingPurcd.isEmpty() && listPendingPurcd.contains(58)) {
                    throw new VahanException("Online Paid Tax is Pending for Recheck");
                }

                List<OwnerDetailsDobj> listOwnerDetails = ownerImpl.getOwnerDetailsList(this.regn_no.trim(), Util.getUserStateCode());

                if (listOwnerDetails == null || listOwnerDetails.isEmpty()) {
                    throw new VahanException("Either Invalid vehicle Registration No or You are not authorized to collect the MV Tax for this vehicle no.");
                } else if (listOwnerDetails.size() > 0) {
                    if (listOwnerDetails.get(0).getState_cd().equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(listOwnerDetails.get(0).getVh_class() + ",")) && (listOwnerDetails.get(0).getPush_bk_seat() != 0 || listOwnerDetails.get(0).getOrdinary_seat() != 0)) {
                        setRenderPushBackSeatPanel(true);
                    } else {
                        setRenderPushBackSeatPanel(false);
                    }
                    /**
                     * Only One Registration Number is available within a state
                     */
                    if (listOwnerDetails.size() == 1) {
                        ownDtls = listOwnerDetails.get(0);
                        /**
                         * Only Approved/Active Vehicles are allowed to collect
                         * TAX
                         */
                        if (ownDtls.getStatus().equals("N")) {
                            NocImpl nocImpl = new NocImpl();
                            NocDobj nocDobj = nocImpl.set_NOC_appl_db_to_dobj(null, regn_no);
                            if (nocDobj == null || (!sessionVariables.getStateCodeSelected().equals(nocDobj.getState_to()) || !ownDtls.getState_cd().equals(nocDobj.getState_to()))) {
                                throw new VahanException("Either Invalid vehicle Registration No or You are not authorized to collect the MV Tax for this vehicle no.");
                            }

                        }

                        if (ownDtls.getStatus().equals(TableConstants.VT_RC_CANCEL_STATUS)) {
                            throw new VahanException("Road Tax Can not be Collected due to RC is Cancelled");
                        }
                        if (ownDtls.getStatus().equals(TableConstants.VT_SCRAP_VEHICLE_STATUS)) {
                            throw new VahanException("Road Tax Can not be Collected due to Vehicle Scrapped");
                        }
                        if (ownDtls.getOff_cd() != Util.getSelectedSeat().getOff_cd()) {
                            Date vahan4startDate = ServerUtil.getVahan4StartDate(ownDtls.getState_cd(), ownDtls.getOff_cd());
                            if (Util.getTmConfiguration().isMv_tax_at_any_office()) {
                                if (vahan4startDate != null) {
                                    registrationDetails = "Vehicle is Registered at Office : " + ServerUtil.getOfficeName(ownDtls.getOff_cd(), ownDtls.getState_cd());
                                    ownerDtlsDobj = ownDtls;
                                } else {
                                    registrationDetails = "Vehicle is Registered at Office : " + ServerUtil.getOfficeName(ownDtls.getOff_cd(), ownDtls.getState_cd());
                                    throw new VahanException("Vahan4 has has not yet started at Office : " + ServerUtil.getOfficeName(ownDtls.getOff_cd(), ownDtls.getState_cd()));
                                }
                            } else {
                                throw new VahanException("You are not authorized to collect the MV Tax for this vehicle no.");
                            }
                        } else {
                            registrationDetails = "Vehicle is Registered at Office : " + ServerUtil.getOfficeName(ownDtls.getOff_cd(), ownDtls.getState_cd());
                            ownerDtlsDobj = ownDtls;
                        }

                    } else if (listOwnerDetails.size() >= 2) {

                        int ownerDetailsCounts = 0;
                        int ownerDetailsCountsNoc = 0;
                        boolean isSameOffice = false;
                        for (int i = 0; i < listOwnerDetails.size(); i++) {

                            if (listOwnerDetails.get(i).getOff_cd() == sessionVariables.getOffCodeSelected()) {
                                isSameOffice = true;
                            }
                            if ("N".equalsIgnoreCase(listOwnerDetails.get(i).getStatus())) {
                                ownerDetailsCountsNoc++;
                            }
                            if (!"N".equalsIgnoreCase(listOwnerDetails.get(i).getStatus())) {
                                ownerDetailsCounts++;
                                ownDtls = listOwnerDetails.get(i);
                            }
                        }
                        if (!isSameOffice) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number not found in Selected Office"));
                            return null;
                        }
                        dupRegnList = listOwnerDetails;
                        if ("WB".contains(Util.getUserStateCode())) {
                            setRenderMoveToHistoryButton(true);
                        }
                        PrimeFaces.current().executeScript("PF('varDupRegNo').show()");
                        return null;
                    }
                }

                if ("OR".equalsIgnoreCase(ownerDtlsDobj.getState_cd())) {
                    boolean isNonUse = NonUseImpl.nonUseDetailsExist(regn_no, ownerDtlsDobj.getState_cd());
                    if (isNonUse) {
                        FacesMessage message = null;
                        message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle is in NonUse . Please Restore/ Remove Vehicle From NonUse!", "Vehicle is in NonUse . Please Restore/ Remove Vehicle From NonUse!!");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        return null;
                    }

                }
                Util.getSelectedSeat().setRegn_no(regn_no);
                Util.getSession().setAttribute("SelectedSeat", getSelectedSeat());
                InsDobj insDobj = InsImpl.set_ins_dtls_db_to_dobj(regn_no, null, ownerDtlsDobj.getState_cd(), ownerDtlsDobj.getOff_cd());
                InsImpl insImpl = new InsImpl();
                if ((insDobj == null || !insImpl.validateInsurance(insDobj)) && !taxAssesment) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle Insurance has Expired", "Vehicle Insurance has Expired");
                    FacesContext.getCurrentInstance().addMessage(null, message);
//                    if (ownerDtlsDobj.getState_cd().equalsIgnoreCase("WB")) {
//                        ownerDtlsDobj = null;
//                        getSelectedSeat().setIsInsuranceCheck(true);
//                        return "/ui/registration/formUpdateInsurancePollutionDetails.xhtml?faces-redirect=true";
//                    }
                }

                if ((ownerDtlsDobj.getOwnerIdentity().getMobile_no() == 0 || ownerDtlsDobj.getOwnerIdentity().getMobile_no().toString().length() < 10) && !taxAssesment) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Owner's Mobile no is Not Updated : ", "Owner's Mobile no is Not Updated : ");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    getSelectedSeat().setIsInsuranceCheck(true);
//                    if (ownerDtlsDobj.getState_cd().equalsIgnoreCase("WB")) {
//                        ownerDtlsDobj = null;
//                        return "/ui/registration/formUpdateInsurancePollutionDetails.xhtml?faces-redirect=true";
//                    }
                }

                BlackListedVehicleImpl blkImpl = new BlackListedVehicleImpl();
                BlackListedVehicleDobj blkDobj = blkImpl.getBlacklistedVehicleDetails(regn_no, ownerDtlsDobj.getChasi_no());
                if (blkDobj != null) {
                    if (blkDobj.getComplain_type() == TableConstants.BLCompoundingAmtCode) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Vehicle is blacklisted for compounding amount please first pay amount through Balance Fee option!!!"));
                        return null;
                    }
                }
                if (!taxAssesment) {
                    boolean isblacklistedvehicle;
                    TmConfigurationDobj tmConfig = Util.getTmConfiguration();
                    isblacklistedvehicle = ServerUtil.isPurposeCodeOnBlacklistedVehicle(ownerDtlsDobj.getRegn_no(), Util.getSelectedSeat().getAction_cd(), Util.getUserStateCode(), tmConfig);
                    if (blkDobj != null) {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle is BlackListed: " + blkDobj.getComplainDesc(), "Vehicle is BlackListed: " + blkDobj.getComplainDesc());
                        FacesContext.getCurrentInstance().addMessage(null, message);
                    }
                    if (isblacklistedvehicle) {
                        ownerDtlsDobj = null;
                        return null;
                    }
                }
                Owner_dobj owDobj = ownerImpl.getOwnerDobj(ownerDtlsDobj);
                setOwnerDobj(owDobj);
                if (getOwnerDobj() != null) {
                    renderAllPanel = true;
                    setDisable(true);
                    PassengerPermitDetailDobj permitDob = TaxServer_Impl.getPermitInfoForSavedTax(ownerDobj);
//                    if (permitDob != null && permitDob.getValid_upto() != null
//                            && DateUtils.isBefore(DateUtils.parseDate(permitDob.getValid_upto()), DateUtils.getCurrentDate())) {
//                        throw new VahanMessageException("Permit of Vehcile has expired");
//                    }
                    VehicleParameters vehicleParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj, permitDob);
                    listTaxForm = TaxServer_Impl.getListTaxForm(owDobj, vehicleParameters);
                    int vehClass = ownerDobj.getVh_class();
                    if (permitDob != null && permitDob.getOtherCriteria() != null && !permitDob.getOtherCriteria().isEmpty() && !permitDob.getOtherCriteria().equalsIgnoreCase("0")) {
                        ownerDobj.setOther_criteria(Integer.parseInt(permitDob.getOtherCriteria()));
                    }
                    if (ServerUtil.isTaxOnPermit(vehClass, Util.getUserStateCode())) {
                        if (permitDob != null) {
                            getPermitPanelBean().setPermitDobj(permitDob);
                            getPermitPanelBean().onSelectPermitType(null);
                        }
                        setRenderPermitPanel(true);
                    } else {
                        getPermitPanelBean().setPermitDobj(null);
                        setRenderPermitPanel(false);
                    }
                    setUserChrg(EpayImpl.getPurposeCodeFee(ownerDobj, ownerDobj.getVh_class(), 99, ownerDobj.getVch_catg()));
                    if (getUserChrg() > 0l) {
                        setRenderUserChargesAmountPanel(true);
                    } else {
                        setRenderUserChargesAmountPanel(false);
                    }
                    for (TaxFormPanelBean bean : listTaxForm) {
                        Map<String, String> taxPaidAndClearDetail = TaxServer_Impl.getTaxPaidAndClearDetail(regn_no, bean.getPur_cd());
                        for (Map.Entry<String, String> entry : taxPaidAndClearDetail.entrySet()) {
                            if (entry.getKey().equals("taxpaid")) {
                                if (entry.getValue() != null && !entry.getValue().equals("")) {
                                    setIs_taxPaid(true);
                                    setTaxPaidLabel(entry.getValue());
                                } else {
                                    setIs_taxPaid(false);
                                }
                            }
                            if (entry.getKey().equals("addtaxpaid")) {
                                if (entry.getValue() != null && !entry.getValue().equals("")) {
                                    setIs_addtaxPaid(true);
                                    setAddTaxPaidLabel(entry.getValue());
                                } else {
                                    setIs_addtaxPaid(false);
                                }
                            }
                            if (entry.getKey().equals("taxclear")) {
                                if (entry.getValue() != null && !entry.getValue().equals("")) {
                                    setIs_taxClear(true);
                                    setTaxClearLabel(entry.getValue());
                                } else {
                                    setIs_taxClear(false);
                                }
                            }
                            if (entry.getKey().equals("addtaxclear")) {
                                if (entry.getValue() != null && !entry.getValue().equals("")) {
                                    setIs_addtaxClear(true);
                                    setAddTaxClearLabel(entry.getValue());
                                } else {
                                    setIs_addtaxClear(false);
                                }
                            }

                            if (entry.getKey().equals("taxExemp")) {
                                if (entry.getValue() != null && !entry.getValue().equals("")) {
                                    setIsTaxExemp(true);
                                    setTaxExempLabel(entry.getValue());
                                } else {
                                    setIsTaxExemp(false);
                                }
                            }

                        }
                    }

                    if ("RJ".equals(ownerDobj.getState_cd()) && ownerDobj.getVehType() == TableConstants.VM_VEHTYPE_TRANSPORT) {
                        renderModelCost = true;
                    }

                    // for Transaction Charges
                    commonCharges.getCommaonCharges(owDobj);
                    if (commonCharges.getCommon_charge_list() != null) {
                        showCommonChargesForAll = true;
                        for (EpayDobj dobj : commonCharges.getCommon_charge_list()) {
                            commonChargesAmt = commonChargesAmt + dobj.getE_TaxFee();
                        }
                        if (getTotalTaxAmount() == 0) {
                            setTotalTaxAmount(commonChargesAmt);
                        } else {
                            setTotalTaxAmount(getTotalTaxAmount() + commonChargesAmt);
                        }

                    } else {
                        showCommonChargesForAll = false;
                    }
                    // for Transaction Charges
                } else {
                    FacesMessage message = null;
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle Details Not Found!", "Vehicle Details Not Found!");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return null;
                }

            }
            TmConfigurationDobj tmConf = Util.getTmConfiguration();
            if (tmConf != null && tmConf.getTmConfigOnlineDobj() != null) {
                if (tmConf.getTmConfigOnlineDobj().isTax_collection() && !isTaxAssesment()) {
                    setRenderOnlinePayBtn(true);
                    UserIDAndPasswordForOnlinePaymentDobj obj = new FeeImpl().getUserIDAndPasswordForOnlinePayment(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), appl_no, ownerDtlsDobj.getOwner_name(), ownerDtlsDobj.getOwnerIdentity().getMobile_no(), regn_no, "R");
                    if (obj != null) {
                        appl_no = obj.getAppl_no();
                        String userInfo = "Online Payment Credentials User ID is : " + appl_no + " & Password : " + obj.getUser_pwd();
                        setRenderOnlinePayBtn(false);
                        setBtn_save(false);
                        setRenderUserAndPasswored(true);
                        setOnlineUserCredentialmsg(userInfo);
                        OnlinePayDobj onlinepaydobj = new FeeImpl().getOnlineFeeTaxDetail(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), appl_no, ownerDobj);
                        if (onlinepaydobj != null) {
                            listTaxForm.clear();
                            listTaxForm.addAll(onlinepaydobj.getTaxFormPanelBeanList());
                        }
                        if (onlinepaydobj != null) {
                            setTotalTaxAmount(0L);
                            long finalTaxAmount = 0;
                            List<Integer> listPurCd = new ArrayList<>();
                            for (TaxFormPanelBean bean : listTaxForm) {
                                finalTaxAmount = finalTaxAmount + bean.getFinalTaxAmount();
                                if (bean.getFinalTaxAmount() > 0) {
                                    listPurCd.add(bean.getPur_cd());
                                }
                            }
                            // updateTotalPayableAmount();
                            for (EpayDobj dobj : onlinepaydobj.getCommon_fee_chargeList()) {
                                if (dobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_USER_CHARGES) {
                                    setTotalUserChrg(dobj.getE_total());
                                } else if (dobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_ALL) {
                                    setCommonChargesAmt(dobj.getE_total());
                                }
                            }
                            setTotalTaxAmount(getTotalTaxAmount() + finalTaxAmount + getTotalUserChrg() + getCommonChargesAmt());
                        } else {
                            updateTotalPayableAmount();
                        }
                        if (CommonUtils.isNullOrBlank(new OnlinePaymentImpl().getTransactionNumber(appl_no))) {
                            setRenderCancelPayment(true);
                        } else {
                            setRenderCancelPayment(false);
                        }
                    }
                }
            }
        } catch (VahanException ve) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            reset();
            return null;
        } catch (IndexOutOfBoundsException ex) {
            LOGGER.error("RegnNo: " + regn_no + ":- Check StackTrace in Catalina.out..");
            System.out.println("RegnNo: " + regn_no);
            ex.printStackTrace();
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
            reset();
            return null;
        } catch (Exception ex) {
            LOGGER.error("RegnNo: " + regn_no + ":-" + ex.toString() + " " + ex.getStackTrace()[0]);
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
            reset();
            return null;
        }

        return null;
    }

    public String printTaxDefaulterList() {
        try {
            Utility utility = new Utility();
            TaxDefaulterListDobj dobjTaxList = null;
            List<TaxDefaulterListDobj> selectedTaxlist = null;
            List<TaxDefaulterListDobj> returnTDNlist = null;
            if (!listTaxForm.isEmpty() && ownerDobj != null) {
                List<TaxDefaulterListDobj> selectedTDNlist = new ArrayList<TaxDefaulterListDobj>();
                TaxDefaulterListDobj dobjList = new TaxDefaulterListDobj();
                returnTDNlist = PrintDocImpl.getTaxDefaulterNoticeFromTaxAssessment(regn_no, sessionVariables.getStateCodeSelected(), currentOffCd, ownerDobj.getVh_class());
                if (!returnTDNlist.isEmpty()) {
                    selectedTaxlist = new ArrayList<TaxDefaulterListDobj>();
                    dobjTaxList = new TaxDefaulterListDobj();
                    dobjTaxList.setTaxTenure("Road Tax(" + listTaxForm.get(0).getFinalTaxFrom() + " to " + listTaxForm.get(0).getFinalTaxUpto() + ")");
                    dobjTaxList.setTaxamt(String.valueOf(listTaxForm.get(0).getTotalPaybaleTax()));
                    selectedTaxlist.add(dobjTaxList);
                }
                returnTDNlist.get(0).setListTaxAmt(selectedTaxlist);
                returnTDNlist.get(0).setTaxamt(String.valueOf(listTaxForm.get(0).getTotalPaybaleTax()));
                returnTDNlist.get(0).setTaxAmtInWords(utility.ConvertNumberToWords(Integer.parseInt(String.valueOf(listTaxForm.get(0).getTotalPaybaleTax()))));
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("printDefaulterList", returnTDNlist);
                return "taxDefaulterNotice";
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "You can not print the Tax Defaulter Notice for Registration no " + getRegn_no().toUpperCase().trim() + " , as Either Tax amount is zero or Tax not clear !!"));
                return "";
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return "";
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return "";
        }
    }

    public List<SelectItem> getListTaxModes(Owner_dobj ownerDobj, int pur_cd) throws VahanException {
        String[] taxModes = TaxServer_Impl.getAvailalableTaxModes(ownerDobj, pur_cd, null);
        List<SelectItem> listTaxModes = new ArrayList();
        String[][] dataTaxModes = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
        if (taxModes != null) {
            listTaxModes.add(new SelectItem("0", "--Select--"));
            for (int i = 0; i < taxModes.length; i++) {
                for (int ii = 0; ii < dataTaxModes.length; ii++) {
                    if (dataTaxModes[ii][0].trim().equals(taxModes[i].trim())) {
                        listTaxModes.add(new SelectItem(dataTaxModes[ii][0], dataTaxModes[ii][1]));
                        break;
                    }
                }
            }
        }

        return listTaxModes;
    }

    private void setTotal() {
        long sumOfTotalPayableInterest = 0;
        long sumOfTotalPayablePenalty = 0;
        long sumOfTotalPayableRebate = 0;
        long sumOfTotalPayableSurcharge = 0;
        long sumOfTotalPayableTax = 0;
        for (DOTaxDetail dobj : getTaxFormBean().getTaxDescriptionList()) {
            sumOfTotalPayableInterest = (long) (sumOfTotalPayableInterest + dobj.getINTEREST());
            sumOfTotalPayablePenalty = (long) (sumOfTotalPayablePenalty + dobj.getPENALTY());
            sumOfTotalPayableRebate = (long) (sumOfTotalPayableRebate + dobj.getREBATE());
            sumOfTotalPayableSurcharge = (long) (sumOfTotalPayableSurcharge + dobj.getSURCHARGE());
            sumOfTotalPayableTax = (long) (sumOfTotalPayableTax + dobj.getAMOUNT());
            getTaxFormBean().setTotalPaybaleInterest(sumOfTotalPayableInterest);
            getTaxFormBean().setTotalPaybalePenalty(sumOfTotalPayablePenalty);
            getTaxFormBean().setTotalPaybaleRebate(sumOfTotalPayableRebate);
            getTaxFormBean().setTotalPaybaleSurcharge(sumOfTotalPayableSurcharge);
            getTaxFormBean().setTotalPaybaleTax(sumOfTotalPayableTax);
        }
        getTaxFormBean().setTotalPayableAmount(
                getTaxFormBean().getTotalPaybaleInterest() + getTaxFormBean().getTotalPaybalePenalty()
                - getTaxFormBean().getTotalPaybaleRebate() + getTaxFormBean().getTotalPaybaleSurcharge()
                + getTaxFormBean().getTotalPaybaleTax());

    }

    public void reset() {
        setRegn_no("");
        setOwnerDobj(null);
        getTaxFormBean().reset();
        getPaymentCollectionBean().reset();
        setBtn_save(false);
        setRenderUserAndPasswored(false);
        setOnlineUserCredentialmsg("");

    }

    public void validateForm(String param) {
        try {
            if (taxAssesment) {
                return;
            }
            this.setPaymentTypeBtn(param);
            boolean allGood = false;
            setRenderExcessDraft(false);
            setExcessDraftMessage(null);

            // Checking for Payment Mode 
            if (Utility.isNullOrBlank(getPaymentCollectionBean().getPayment_mode())
                    || "-1".equalsIgnoreCase(getPaymentCollectionBean().getPayment_mode())) {

                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please select payment mode", "Please select payment mode");
                FacesContext.getCurrentInstance().addMessage(null, message);
                allGood = true;
                return;
            }

            try {
                ServerUtil.validateNoCashPayment(paymentTypeBtn, getPaymentCollectionBean().getPayment_mode());
            } catch (VahanException ve) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, ve.getMessage(), ve.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
            if (getPaymentTypeBtn().equals("OnlinePayment") && !getPaymentCollectionBean().getPayment_mode().equals("C")) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please select cash payment mode for online payment.", "Please select cash payment mode for online payment.");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
            boolean flg = false;
            for (TaxFormPanelBean bean : listTaxForm) {
                if (!bean.getTaxMode().equals("0")) {
                    flg = true;
                }
            }

            if (!flg) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please select Tax Mode", "Please select Tax Mode");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }

            setTotalDraftAmount(0l);
            // Cash or some thing else
            // hardcode as cash = 4
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
                if (!allGood) {
                    if (getTotalDraftAmount() > totalTaxAmount) {
                        String strMes = "Excess Total Instrument Amount (Rs." + (getTotalDraftAmount() - totalTaxAmount) + "/- will be saved as Miscellenous Fee Head.";
                        setRenderExcessDraft(true);
                        setExcessDraftMessage(strMes);
                        allGood = false;
                    } else if ("DL,SK".contains(Util.getUserStateCode())) {
                        if (totalTaxAmount != getTotalDraftAmount()) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Paying total instrument Amount Must be equal to Total Payable Amount"));
                            return;
                        }
                    }
                }
            } else {
                if ("SK".contains(Util.getUserStateCode())) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please select Mixed Mode for payment. Because Cash payment mode in not allowed in Sikkim"));
                    allGood = true;
                    return;
                }
            }
            if (commonCharges != null) {
                for (TaxExemptiondobj exemdobj : commonCharges.taxExemList) {
                    if (exemdobj.getPur_cd() == TableConstants.FEE_FINE_EXEMTION) {
                        if (exemdobj.getExemAmount() > 0) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Invalid Exemption Order Details. Please make sure amount is valid."));
                            return;
                        }
                    } else if (exemdobj.getPur_cd() == TableConstants.TAX_PENALTY_EXEMTION) {
                        long totalPenaltyAmnt = 0l;
                        for (TaxFormPanelBean bean : listTaxForm) {
                            totalPenaltyAmnt = totalPenaltyAmnt + bean.getTotalPaybalePenalty();
                        }
                        if (totalPenaltyAmnt < exemdobj.getExemAmount()) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Invalid Exemption Order Details. Please make sure amount is valid."));
                            return;
                        }
                    } else if (exemdobj.getPur_cd() == TableConstants.TAX_INTEREST_EXEMTION) {
                        long totalInterestAmnt = 0l;
                        for (TaxFormPanelBean bean : listTaxForm) {
                            totalInterestAmnt = totalInterestAmnt + bean.getTotalPaybaleInterest();
                        }
                        if (totalInterestAmnt < exemdobj.getExemAmount()) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Invalid Exemption Order Details. Please make sure amount is valid."));
                            return;
                        }
                    }
                }
            }

            //Only For Orissa---Check  for Audit---------
            if (!CommonUtils.isNullOrBlank(Util.getUserStateCode()) && "OR".equals(Util.getUserStateCode())) {
                AuditRecoveryDobj auditRecoveryDobj = ServerUtil.getAuditRecordFromVA_AUDIT(regn_no, Util.getUserStateCode(), Util.getUserOffCode());
                if (auditRecoveryDobj != null) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Audit is Pending for  Registration No", "Audit is Pending for  Registration No");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }
            }
            //Only for orissa----Check for Tax installment
            if (!CommonUtils.isNullOrBlank(Util.getUserStateCode()) && "OR".equals(Util.getUserStateCode())) {
                TaxInstallCollectionDobj TaxinstallDobj = ServerUtil.getTaxInstallCheck(regn_no, Util.getUserStateCode(), Util.getUserOffCode());
                if (TaxinstallDobj != null) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "TaxInstallment is Pending for  Registration No", "TaxInstallment is Pending for  Registration No");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }
            }

            ////Add for WB 
            for (TaxFormPanelBean bean : listTaxForm) {
                if ("WB".equals(sessionVariables.getStateCodeSelected()) && (bean.getPur_cd() == 58)) {
                    String prevTax = new TaxImpl().getPrevious_TaxMode(regn_no, String.valueOf(bean.getPur_cd()), Util.getUserStateCode());
                    if (prevTax != null && !prevTax.isEmpty()) {
                        if (!prevTax.equalsIgnoreCase(bean.getTaxMode()) && !ServerUtil.isTransport(ownerDobj.getVh_class(), ownerDobj) && !bean.getTaxMode().equalsIgnoreCase("E")) {
                            List list_select = bean.getListTaxModes();
                            String dcrs = "";
                            for (int i = 0; i < list_select.size(); i++) {
                                SelectItem ites = (SelectItem) list_select.get(i);
                                if (ites.getValue().equals(bean.getTaxMode())) {
                                    dcrs = ites.getLabel();
                                    break;
                                }
                            }
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "You can not select " + dcrs + "  Tax mode", "You can not select " + dcrs + " Tax mode");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            return;
                        }
                    }
                }
            }

            for (PaymentCollectionDobj dobj : getPaymentCollectionBean().getPaymentlist()) {
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
            //end
            if (!allGood) {
                if (getTotalDraftAmount() <= getTotalTaxAmount()) {
                    setTotalBalanceAmount(getTotalTaxAmount() - getTotalDraftAmount());
                } else {
                    setTotalBalanceAmount(0l);
                    setExcessAmount(getTotalDraftAmount() - getTotalTaxAmount());
                }
                // RequestContext rc = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("new_veh_fee_subview:confirmationPopup");
                PrimeFaces.current().executeScript("PF('confDlgTax').show()");
            }
        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
    }

    public String saveTaxDetails() {

        Exception e = null;
        long checkTotalAmount = 0L;
        try {
            if (taxAssesment) {
                return "";
            }
            /*if (TaxServer_Impl.validateTaxPaidForDay(regn_no)) {
             throw new VahanMessageException("You can't pay the Motor Vehicle Tax twice on the same day!!! Please come again on next day or cancel the previous receipt and pay again.");
             }*/

            TaxServer_Impl taxServer = new TaxServer_Impl();
            List<Tax_Pay_Dobj> listTaxDobj = new ArrayList<>();

            for (TaxFormPanelBean bean : listTaxForm) {
                if (!bean.getTaxMode().equals("0")) {
                    String taxCalc = permitPanelBean.getPermitDobj() != null ? permitPanelBean.getPermitDobj().toString() : "";
                    taxCalc = taxCalc + " : " + ownerDobj.getSale_amt() + " : " + ownerDobj.getOther_criteria();
                    if (bean.getTaxCalcBasedOnParms() != null && !bean.getTaxCalcBasedOnParms().equals(taxCalc)) {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Tax is Calculated for Different Permit Details,Please Press Refresh-Tax Details",
                                "Tax is Calculated for Different Permit Details,Please Press Refresh-Tax Details");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        return null;

                    }

                    Tax_Pay_Dobj taxDobj = new Tax_Pay_Dobj();
                    taxDobj.setFinalTaxAmount(bean.getFinalTaxAmount());
                    taxDobj.setTotalPaybaleTax(bean.getTotalPaybaleTax());
                    taxDobj.setTotalPaybalePenalty(bean.getTotalPaybalePenalty());
                    taxDobj.setTotalPaybaleSurcharge(bean.getTotalPaybaleSurcharge());
                    taxDobj.setTotalPaybaleRebate(bean.getTotalPaybaleRebate());
                    taxDobj.setTotalPaybaleInterest(bean.getTotalPaybaleInterest());
                    taxDobj.setFinalTaxFrom(bean.getFinalTaxFrom());
                    taxDobj.setFinalTaxUpto(bean.getFinalTaxUpto());
                    taxDobj.setTaxMode(bean.getTaxMode());
                    taxDobj.setPur_cd(bean.getPur_cd());
                    taxDobj.setRegnNo(regn_no);
                    taxDobj.setPaymentMode(getPaymentCollectionBean().getPayment_mode());
                    taxDobj.setTaxBreakDetails(bean.getTaxDescriptionList());
                    taxDobj.setNoOfAdvUnits(bean.getNoOfUnits());
                    taxDobj.setTotalPaybaleTax1(bean.getTotalTax1());
                    taxDobj.setTotalPaybaleTax2(bean.getTotalTax2());
                    taxDobj.setTaxCeaseDate(DateUtils.parseDate(bean.getTaxCeaseDate()));
                    listTaxDobj.add(taxDobj);

                }

            }

//            if (renderPermitPanel && permitPanelBean.getPermitDobj() != null) {
//                permitRefreshDtl = permitPanelBean.getPermitDobj().toString() + ownerDobj.getSale_amt();
//                if (permitTaxDtl != null && !permitTaxDtl.equals(permitRefreshDtl)) {
//                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Tax is Calculated for Different Permit Details,Please calculate tax again",
//                            "Tax is Calculated for Different Permit Details,Please calculate tax again");
//                    FacesContext.getCurrentInstance().addMessage(null, message);
//                    return null;
//
//                }
//            }
            FeeDraftDobj feeDraftDobj = null;

            if (!Utility.isNullOrBlank(getPaymentCollectionBean().getPayment_mode()) && (!getPaymentCollectionBean().getPayment_mode().equals("-1"))
                    && (!getPaymentCollectionBean().getPayment_mode().equals("C"))) {

                feeDraftDobj = new FeeDraftDobj();
                feeDraftDobj.setCollected_by(Util.getEmpCode());
                feeDraftDobj.setState_cd(Util.getUserStateCode());
                feeDraftDobj.setOff_cd(String.valueOf(Util.getUserOffCode()));
                feeDraftDobj.setDraftPaymentList(getPaymentCollectionBean().getPaymentlist());

            }

            if (!CommonUtils.isNullOrBlank(paymentTypeBtn) && "RtoPayment".equals(getPaymentTypeBtn())) {
                Long userCharges = getTotalUserChrg();
                String payMode = getPaymentCollectionBean().getPayment_mode();
                List<EpayDobj> listCommonCharges = commonCharges.getCommon_charge_list();

                if (getTotalDraftAmount() > totalTaxAmount) {
                    EpayDobj ep = new EpayDobj();
                    ep.setPurCd(48);
                    ep.setE_TaxFee(getTotalDraftAmount() - totalTaxAmount);
                    if (listCommonCharges != null) {
                        listCommonCharges.add(ep);
                    } else {
                        listCommonCharges = new ArrayList<>();
                        listCommonCharges.add(ep);
                    }
                }

                generatedRcptNo = taxServer.saveTaxTransactionDetails(userCharges, payMode, listTaxDobj, feeDraftDobj, ownerDobj, getPermitPanelBean().getPermitDobj(), listCommonCharges, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), nonUseDobj);

                if (!Utility.isNullOrBlank(generatedRcptNo)) {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful Transaction", " Receipt Number Generated : " + generatedRcptNo);
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    reset();
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", generatedRcptNo);
                    return "PrintCashReceiptReport";

                }
                rcpt_bean.reset();
                listTaxForm.clear();
                updateTotalPayableAmount();
            } else if (!CommonUtils.isNullOrBlank(paymentTypeBtn) && "OnlinePayment".equals(getPaymentTypeBtn())) {
                List<FeeDobj> feedobjList = new ArrayList<FeeDobj>();
                Fee_Pay_Dobj feePayDobj = new Fee_Pay_Dobj();
                feePayDobj.setListTaxDobj(listTaxDobj);
                feePayDobj.setPermitDobj(getPermitPanelBean().getPermitDobj());
                Long userCharges = getTotalUserChrg();
                String payMode = getPaymentCollectionBean().getPayment_mode();
                List<EpayDobj> listCommonCharges = commonCharges.getCommon_charge_list();

                if (getTotalDraftAmount() > totalTaxAmount) {
                    EpayDobj ep = new EpayDobj();
                    ep.setPurCd(48);
                    ep.setE_TaxFee(getTotalDraftAmount() - totalTaxAmount);
                    if (listCommonCharges != null) {
                        listCommonCharges.add(ep);
                    } else {
                        listCommonCharges = new ArrayList<>();
                        listCommonCharges.add(ep);
                    }
                }
                if (listCommonCharges != null && !listCommonCharges.isEmpty()) {
                    for (EpayDobj ePayDobj : listCommonCharges) {
                        FeeDobj feeDobj = new FeeDobj();
                        feeDobj.setPurCd(ePayDobj.getPurCd());
                        feeDobj.setFeeAmount(ePayDobj.getE_TaxFee());
                        feeDobj.setFineAmount(0L);
                        feedobjList.add(feeDobj);
                        checkTotalAmount = checkTotalAmount + feeDobj.getFeeAmount();
                    }
                }
                if (userCharges > 0L) {
                    FeeDobj feeDobj = new FeeDobj();
                    feeDobj.setPurCd(TableConstants.VM_TRANSACTION_MAST_USER_CHARGES);
                    feeDobj.setFeeAmount(userCharges);
                    feeDobj.setFineAmount(0L);
                    feedobjList.add(feeDobj);
                }

                feePayDobj.setCollectedFeeList(feedobjList);
                this.saveOnlinePaymentDetails(feePayDobj, checkTotalAmount);
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

    public void saveOnlinePaymentDetails(Fee_Pay_Dobj feePayDobj, long checkTotalAmount) throws VahanException {

        try {
            List<Tax_Pay_Dobj> taxList = feePayDobj.getListTaxDobj();
            if (taxList != null && !taxList.isEmpty()) {
                for (Tax_Pay_Dobj taxdobj : taxList) {
                    Long totalAmount = taxdobj.getFinalTaxAmount();
                    checkTotalAmount = checkTotalAmount + totalAmount;
                }
            }
            checkTotalAmount = checkTotalAmount + totalUserChrg;
            String userPwd = new FeeImpl().saveOnlinePaymentData(feePayDobj, checkTotalAmount, appl_no, ownerDobj.getOwner_identity().getMobile_no(), ownerDobj.getState_cd(), ownerDobj.getOff_cd(), Util.getEmpCode(), TableConstants.ONLINE_TAX, "R", ownerDtlsDobj, false, false);
            if (userPwd != null) {
                appl_no = userPwd.split(",")[0];
                String userInfo = "Online Payment Credentials User ID & Password :  " + userPwd;
                setOnlineUserCredentialmsg(userInfo);
                setRenderUserAndPasswored(true);
                setBtn_save(false);
                setRenderOnlinePayBtn(false);
                setRenderCancelPayment(true);
            } else {
                throw new VahanException("Error in Saving Details !!");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

    }

    public String cancelOnlinePayment() {
        OnlinePaymentImpl onlinePayImpl = new OnlinePaymentImpl();
        boolean flag = false;
        String password = "";
        long user_cd = 0;
        try {
            Object[] obj = new FeeImpl().getUserIDAndPassword(appl_no);
            if (obj != null && obj.length > 0) {
                password = (String) obj[0];
                user_cd = (long) obj[1];
                if (CommonUtils.isNullOrBlank(onlinePayImpl.getTransactionNumber(appl_no))) {
                    flag = onlinePayImpl.getPaymentRevertBack(user_cd + "", appl_no, TableConstants.ONLINE_TAX);
                    if (flag) {
                        setBtn_save(true);
                        setRenderOnlinePayBtn(true);
                        setRenderCancelPayment(false);
                        setRenderUserAndPasswored(false);
                    }
                }
            }
        } catch (VahanException e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "home";
    }

    public void taxModeListener(AjaxBehaviorEvent event) {

        //#########################--IMPORTANT--######################################
        //if any changes in this method then same changes also must be in beforeTaxModeListener()
        //#########################--IMPORTANT--#######################################
        TaxFormPanelBean taxSelectedFormBean = null;
        try {
            taxSelectedFormBean = (TaxFormPanelBean) event.getComponent().getAttributes().get("taxBeanAttr");
            if (ownerDobj == null) {
                taxSelectedFormBean.reset();
                taxSelectedFormBean.resetAll();
                updateTotalPayableAmount();
                return;
            }
            if (taxSelectedFormBean.getTaxMode().equals("0")) {
                taxSelectedFormBean.resetAll();
                updateTotalPayableAmount();
                return;
            }

            //if (renderPermitPanel && permitPanelBean.getPermitDobj() != null) {
            String taxCalc = permitPanelBean.getPermitDobj() != null ? permitPanelBean.getPermitDobj().toString() : "";
            taxCalc = taxCalc + " : " + ownerDobj.getSale_amt() + " : " + ownerDobj.getOther_criteria();
            taxSelectedFormBean.setTaxCalcBasedOnParms(taxCalc);
            //}

            if (taxSelectedFormBean.getTaxMode().equalsIgnoreCase("O") || taxSelectedFormBean.getTaxMode().equalsIgnoreCase("L")) {
                taxSelectedFormBean.setNoOfUnits(1);
            }

            VahanTaxParameters taxParameters = fillTaxParametersFromDobj(ownerDobj, getPermitPanelBean().getPermitDobj());
            taxParameters.setTAX_CEASE_DATE(DateUtils.parseDate(taxSelectedFormBean.getTaxCeaseDate()));
            if (ownerDobj.getState_cd().equals("TN") && taxSelectedFormBean.getTaxCeaseDate() != null && taxSelectedFormBean.getNoOfUnits() > 1) {
                taxSelectedFormBean.setNoOfUnits(taxSelectedFormBean.getNoOfUnits() - 1);
                taxParameters.setTAX_CEASE_DATE(null);
            }

            if (ownerDobj.getState_cd().equals("ML")) {
                int ldWt = ownerDobj.getLd_wt();
                String passengerVehicleClass = "56,57,70,71,73,75,86,";
                String goodsVehicleClass = "58,59,";
                int GOODS_TAX_LD_WT = 3000;
                if (taxSelectedFormBean.getPur_cd() == TableConstants.PASSENGER_TAX || taxSelectedFormBean.getPur_cd() == TableConstants.GOODS_TAX) {
                    Date taxClearDateUpto = TaxServer_Impl.getPGTaxDetails(regn_no, taxSelectedFormBean.getPur_cd());
                    String vahanerrormessage = "Either Invalid vehicle Registration No or You are not authorized to collect the PG Tax for this vehicle no.";
                    if (taxClearDateUpto == null) {
                        if (passengerVehicleClass.contains("," + String.valueOf(ownerDobj.getVh_class() + ","))) {
                            throw new VahanException(vahanerrormessage);
                        } else if ((goodsVehicleClass.contains("," + String.valueOf(ownerDobj.getVh_class() + ",")) && ldWt > GOODS_TAX_LD_WT)) {
                            throw new VahanException(vahanerrormessage);
                        }
                    }
                }
            }

            if (Util.getTmConfiguration().isAuto_tax_no_units()) {
                for (TaxFormPanelBean taxForm : listTaxForm) {
                    if (taxForm.getTaxMode().equals(taxSelectedFormBean.getTaxMode())) {
                        taxForm.setNoOfUnits(taxSelectedFormBean.getNoOfUnits());
                        updateSelectedTaxFormBean(taxForm, taxParameters);
                    }
                }
            } else {
                updateSelectedTaxFormBean(taxSelectedFormBean, taxParameters);
            }
            if (taxSelectedFormBean.getTaxDescriptionList() != null && !taxSelectedFormBean.getTaxDescriptionList().isEmpty() && taxAssesment) {
                Date taxPaidUpto = null;
                Map<String, String> taxPaid = new HashMap<>();
                taxPaid = ServerUtil.taxPaidInfo(false, regn_no, 58);

                if (!taxPaid.isEmpty()) {
                    for (Map.Entry<String, String> entry : taxPaid.entrySet()) {
                        if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("tax_mode") && entry.getValue() != null) {
                            if (!entry.getValue().isEmpty() && "L,O".contains(entry.getValue())) {
                                setTaxDefList(false);
                                break;
                            }
                        } else if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("tax_upto") && entry.getValue() != null) {
                            taxPaidUpto = DateUtils.parseDate(entry.getValue());
                            if (new Date().after(taxPaidUpto)) {
                                setTaxDefList(true);
                                break;
                            } else {
                                setTaxDefList(false);
                            }
                        }
                    }
                    //RequestContext ca = RequestContext.getCurrentInstance();
                    PrimeFaces.current().ajax().update("new_veh_fee_subview:taxDefListID");
                }
            }

        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            if (taxSelectedFormBean != null) {
                taxSelectedFormBean.reset();
                taxSelectedFormBean.resetAll();
                updateTotalPayableAmount();
            }
            return;
        } catch (Exception e) {
            if (taxSelectedFormBean != null) {
                taxSelectedFormBean.reset();
                taxSelectedFormBean.resetAll();
                updateTotalPayableAmount();
            }
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    public void updateSelectedTaxFormBean(TaxFormPanelBean taxSelectedFormBean, VahanTaxParameters taxParameters) throws VahanException {
        try {
            if (taxSelectedFormBean.getTaxMode().equals("0")) {
                taxSelectedFormBean.resetAll();
                updateTotalPayableAmount();
                return;
            }

            if (taxSelectedFormBean.getTaxMode().equalsIgnoreCase("O") || taxSelectedFormBean.getTaxMode().equalsIgnoreCase("L")) {
                taxSelectedFormBean.setNoOfUnits(1);
            }

            taxParameters.setTAX_MODE(taxSelectedFormBean.getTaxMode());
            taxParameters.setPUR_CD(taxSelectedFormBean.getPur_cd());
            if (taxSelectedFormBean.getPur_cd() == 58) {
                String taxPaidInfo = TaxServer_Impl.taxPaidInfo(getRegn_no());
                if (taxPaidInfo != null) {
                    if (taxPaidInfo.contains("Error:") && "L,O,S".contains(taxSelectedFormBean.getTaxMode())) {
                        taxPaidInfo = taxPaidInfo.replaceAll("Error:", "");
                        throw new VahanException(taxPaidInfo);
                    }

                }
            }

            TaxDobj taxDobj = new ServerUtil().getBalanceTaxDetails(regn_no, appl_no, taxSelectedFormBean.getPur_cd(), ownerDobj.getState_cd(), null);
            if (taxDobj != null && taxDobj.getTax_amt() > 0 && !"ML".equals(ownerDobj.getState_cd())) {
                throw new VahanException("As per record the Vehicle has Tax Recovery Pending, please first pay Tax Recovery Amount of Rs " + taxDobj.getTax_amt()
                        + " using Balance Fee/Tax Collection option.");
            }

            Date taxDueFrom = TaxServer_Impl.getTaxDueFromDate(ownerDobj, taxSelectedFormBean.getPur_cd());
            if (taxDueFrom == null || taxDueFrom == ownerDobj.getPurchase_dt() || taxDueFrom == ownerDobj.getRegn_dt()) {
                if (!Util.getUserStateCode().equalsIgnoreCase("BR")) {
                    if (taxSelectedFormBean.getInitialDueDate() != null
                            && DateUtils.compareDates(taxSelectedFormBean.getInitialDueDate(), taxDueFrom) == 2
                            && (DateUtils.compareDates(taxSelectedFormBean.getInitialDueDate(), taxSelectedFormBean.getInitialEffectiveDate()) == 2)
                            || DateUtils.compareDates(taxSelectedFormBean.getInitialDueDate(), taxSelectedFormBean.getInitialEffectiveDate()) == 0) {
                        taxDueFrom = taxSelectedFormBean.getInitialDueDate();
                    } else if (taxSelectedFormBean.getInitialEffectiveDate() != null
                            && DateUtils.compareDates(taxSelectedFormBean.getInitialEffectiveDate(), taxDueFrom) == 2
                            && (DateUtils.compareDates(taxSelectedFormBean.getInitialEffectiveDate(), taxSelectedFormBean.getInitialDueDate()) == 2)
                            || DateUtils.compareDates(taxSelectedFormBean.getInitialEffectiveDate(), taxSelectedFormBean.getInitialDueDate()) == 0) {
                        taxDueFrom = taxSelectedFormBean.getInitialEffectiveDate();
                    }
                } else {
                    if (taxSelectedFormBean.getInitialDueDate() != null
                            && DateUtils.compareDates(taxSelectedFormBean.getInitialDueDate(), taxDueFrom) == 2
                            && (DateUtils.compareDates(taxSelectedFormBean.getInitialDueDate(), taxSelectedFormBean.getInitialEffectiveDate()) == 2
                            || DateUtils.compareDates(taxSelectedFormBean.getInitialDueDate(), taxSelectedFormBean.getInitialEffectiveDate()) == 0)) {
                        taxDueFrom = taxSelectedFormBean.getInitialDueDate();
                    } else if (taxSelectedFormBean.getInitialEffectiveDate() != null
                            && DateUtils.compareDates(taxSelectedFormBean.getInitialEffectiveDate(), taxDueFrom) == 2
                            && (DateUtils.compareDates(taxSelectedFormBean.getInitialEffectiveDate(), taxSelectedFormBean.getInitialDueDate()) == 2
                            || DateUtils.compareDates(taxSelectedFormBean.getInitialEffectiveDate(), taxSelectedFormBean.getInitialDueDate()) == 0)) {
                        taxDueFrom = taxSelectedFormBean.getInitialEffectiveDate();
                    }
                }
            }

            taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(taxDueFrom));
            taxParameters.setNEW_VCH("N");
            taxParameters.setTAX_MODE_NO_ADV(taxSelectedFormBean.getNoOfUnits());
            if (taxSelectedFormBean.getTaxCeaseDate() != null) {
                taxParameters.setTAX_CEASE_DATE(DateUtils.parseDate(taxSelectedFormBean.getTaxCeaseDate()));
            }
            TaxDobj prevTaxPaid = null;
            if ("HP".contains(taxParameters.getSTATE_CD()) && (taxParameters.getPUR_CD() == 58)) {
                prevTaxPaid = new TaxImpl().getTaxDetails(regn_no, String.valueOf(taxParameters.getPUR_CD()), taxParameters.getSTATE_CD());
                if (prevTaxPaid != null && "Y,Q".contains(prevTaxPaid.getTax_mode())
                        && DateUtils.getDate1MinusDate2_Months(prevTaxPaid.getTax_from(), prevTaxPaid.getTax_upto()) > 11) {
                    taxParameters.setOTHER_CRITERIA(99);
                }
            }
            if ("WB".contains(taxParameters.getSTATE_CD()) && (taxParameters.getPUR_CD() == 59)) {
                if (!regn_no.substring(0, 2).equalsIgnoreCase("WB") && ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)) {
                    taxParameters.setOTHER_CRITERIA(99);
                }
            }
            List<DOTaxDetail> listTaxBreakUp = null;
            if (taxParameters.getSTATE_CD().equals("KL")) {
                int pushbackseat = 0;
                int ordinaryseat = 0;
                if (TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(ownerDobj.getVh_class() + ",")) && (ownerDtlsDobj.getPush_bk_seat() != 0 || ownerDtlsDobj.getOrdinary_seat() != 0)) {
                    pushbackseat = ownerDtlsDobj.getPush_bk_seat();
                    ordinaryseat = ownerDtlsDobj.getOrdinary_seat();
                }

                Date currentDate = new Date();
                SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy", Locale.ENGLISH);
                Date datedue = format.parse(taxParameters.getTAX_DUE_FROM_DATE());
                datedue = DateUtils.addToDate(datedue, 1, -30);
                String taxPayableDate = DateUtils.parseDate(datedue);
                if (currentDate.before(datedue)) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Alert", "Please pay Tax after " + taxPayableDate));
                    return;
                } else {
                    listTaxBreakUp = callKLTaxService(taxParameters, pushbackseat, ordinaryseat, ownerDobj.getRegn_no(), ownerDobj.getChasi_no());
                }
            } else {
                listTaxBreakUp = callTaxService(taxParameters);
            }
            //List<DOTaxDetail> listTaxBreakUp = callTaxService(taxParameters);
            if (listTaxBreakUp != null && !listTaxBreakUp.isEmpty()) {
                listTaxBreakUp = TaxUtils.sortTaxDetails(listTaxBreakUp);
                if (taxSelectedFormBean.getTaxMode().equals("O") || taxSelectedFormBean.getTaxMode().equals("L")
                        || taxSelectedFormBean.getTaxMode().equals("S")) {
                    listTaxBreakUp.get(listTaxBreakUp.size() - 1).setTAX_UPTO(null);
                }
                /*
                 *For vehicles which were paying recurring tax earlier but need to pay one time now in Manipur.
                 */
                if ("MN".equals(taxParameters.getSTATE_CD())) {
                    Date regnDate = ownerDobj.getRegn_dt();
                    String remainingDuration = null;
                    int remainingMonth = 0;
                    int LIFE_TIME_TAX_IN_MONTH = 180;
                    if ("Y15".equals(taxSelectedFormBean.getTaxMode())) {
                        prevTaxPaid = new TaxImpl().getTaxDetails(regn_no, String.valueOf(taxParameters.getPUR_CD()), taxParameters.getSTATE_CD());
                        if (prevTaxPaid != null && prevTaxPaid.getTax_upto() != null) {
                            remainingMonth = DateUtils.getDate1MinusDate2_Months(regnDate, prevTaxPaid.getTax_upto());
                            if (LIFE_TIME_TAX_IN_MONTH > remainingMonth) {
                                remainingMonth = LIFE_TIME_TAX_IN_MONTH - remainingMonth;
                                remainingDuration = DateUtils.parseDate(DateUtils.addToDate(DateUtils.parseDate(taxDueFrom),
                                        DateUtils.MONTH, remainingMonth));
                                remainingDuration = JSFUtils.getDateInDD_MMM_YYYY(
                                        DateUtils.parseDate(DateUtils.addToDate(remainingDuration, DateUtils.DAY, -1)));
                                listTaxBreakUp.get(listTaxBreakUp.size() - 1).setTAX_UPTO(remainingDuration);
                            }
                        }
                    }
                }
                DOTaxDetail bean = listTaxBreakUp.get(0);
                if (taxParameters.getSTATE_CD().equalsIgnoreCase("ML")) {
                    if (taxDobj != null && taxDobj.getTax_amt() > 0) {
                        bean.setPRV_ADJ(taxDobj.getTax_amt());
                    }
                }
                if (TableConstants.TM_ROAD_TAX == taxSelectedFormBean.getPur_cd()) {
                    NonUseImpl nonUseImpl = new NonUseImpl();
                    boolean mFormStatus = false;
                    if ("CG".equals(taxParameters.getSTATE_CD())) {
                        mFormStatus = nonUseImpl.checkMFormStatus(regn_no);
                    }
                    if (!mFormStatus) {
                        nonUseDobj = nonUseImpl.geTotalNonUseAmount(ownerDobj);
                        if (nonUseDobj != null) {
                            double totalPaybalRebate = 0.0;
                            double totalPaybalPenalty = 0.0;
                            totalPaybalRebate = totalPaybalRebate + bean.getREBATE() + nonUseDobj.getAdjustmentAmount();
                            totalPaybalPenalty = totalPaybalPenalty + bean.getPENALTY() + nonUseDobj.getNonUsePenalty();
                            bean.setREBATE(totalPaybalRebate);
                            bean.setPENALTY(totalPaybalPenalty);
                        }
                    }
                }
                taxSelectedFormBean.setTaxDescriptionList(listTaxBreakUp);
            }

            taxSelectedFormBean.updateTaxBean();
            updateTotalPayableAmount();
            PrimeFaces.current().ajax().update(":masterLayout:new_veh_fee_subview:totalPaybaleAmountPanel");
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void isViewIsRendered() {
        try {
            if (listTaxForm != null && !listTaxForm.isEmpty() && Util.getTmConfiguration().isAuto_tax_mode_filler()) {
                TaxImpl taxImpl = new TaxImpl();
                VahanTaxParameters taxParameters = fillTaxParametersFromDobj(ownerDobj, permitPanelBean.getPermitDobj());
                for (int i = 0; i < listTaxForm.size(); i++) {
                    TaxDobj taxDobj = taxImpl.getTaxDetails(regn_no, String.valueOf(listTaxForm.get(i).getPur_cd()), ownerDobj.getState_cd());
                    if (taxDobj != null && taxDobj.getPur_cd() == listTaxForm.get(i).getPur_cd()) {
                        TaxFormPanelBean taxSelectedFormBean = listTaxForm.get(i);
                        String taxCalc = permitPanelBean.getPermitDobj() != null ? permitPanelBean.getPermitDobj().toString() : "";
                        taxCalc = taxCalc + " : " + ownerDobj.getSale_amt() + " : " + ownerDobj.getOther_criteria();
                        taxSelectedFormBean.setTaxCalcBasedOnParms(taxCalc);
                        taxSelectedFormBean.setTaxMode(taxDobj.getTax_mode());
                        try {
                            updateSelectedTaxFormBean(taxSelectedFormBean, taxParameters);
                        } catch (VahanException vex) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
                        }
                    } else if (taxDobj == null) {
                        TaxFormPanelBean taxSelectedFormBean = listTaxForm.get(i);
                        if (taxSelectedFormBean.getListTaxModes().size() <= 2) {
                            taxSelectedFormBean = listTaxForm.get(i);

                            String taxCalc = permitPanelBean.getPermitDobj() != null ? permitPanelBean.getPermitDobj().toString() : "";
                            taxCalc = taxCalc + " : " + ownerDobj.getSale_amt() + " : " + ownerDobj.getOther_criteria();
                            taxSelectedFormBean.setTaxCalcBasedOnParms(taxCalc);
                            SelectItem selectItem = (SelectItem) taxSelectedFormBean.getListTaxModes().get(1);
                            taxSelectedFormBean.setTaxMode(selectItem.getValue().toString());
                            try {
                                updateSelectedTaxFormBean(taxSelectedFormBean, taxParameters);
                            } catch (VahanException vex) {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
                            }
                        }
                    }
                }
            }
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        autoRunTaxListener = false;
    }

    public void updateTotalPayableAmount() {
        setTotalTaxAmount(0L);
        long finalTaxAmount = 0;
        List<Integer> listPurCd = new ArrayList<>();
        for (TaxFormPanelBean bean : listTaxForm) {
            finalTaxAmount = finalTaxAmount + bean.getFinalTaxAmount();
            if (bean.getFinalTaxAmount() > 0 || bean.getTotalPaybaleTax() > 0) {
                listPurCd.add(bean.getPur_cd());
            }
        }
        if (EpayImpl.getServiceChargeType() != null) {
            Long userCharges = EpayImpl.getUserChargesFee(ownerDobj, listPurCd, null);
            setTotalUserChrg(userCharges);
            setRenderUserChargesAmountPanel(true);
        } else {
            setRenderUserChargesAmountPanel(false);
        }

        setTotalTaxAmount(getTotalTaxAmount() + finalTaxAmount + getTotalUserChrg() + getCommonChargesAmt());

    }

//    public List<DOTaxDetail> callTaxService(VahanTaxParameters taxParameters) throws VahanException {
//        List<DOTaxDetail> tempTaxList = null;
//
//        VahanTaxClient taxClient = null;
//        try {
//            taxClient = new VahanTaxClient();
//            String taxServiceResponse = taxClient.getTaxDetails(taxParameters);
//            tempTaxList = taxClient.parseTaxResponse(taxServiceResponse, taxParameters.getPURCD(), taxParameters.getTAXMODE());
//
//        } catch (javax.xml.ws.WebServiceException e) {
//            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//        }
//
//
//        return tempTaxList;
//    }
    /**
     * @return the ownerDobj
     */
    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    /**
     * @param ownerDobj the ownerDobj to set
     */
    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
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

    public TaxFormPanelBean getTaxFormBean() {
        if (taxFormBean == null) {
            taxFormBean = new TaxFormPanelBean();
        }
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

    public PermitPanelBean getPermitPanelBean() {
        return permitPanelBean;
    }

    public void setPermitPanelBean(PermitPanelBean permitPanelBean) {
        this.permitPanelBean = permitPanelBean;
    }

    public ReceiptMasterBean getRcpt_bean() {
        return rcpt_bean;
    }

    public void setRcpt_bean(ReceiptMasterBean rcpt_bean) {
        this.rcpt_bean = rcpt_bean;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
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
     * @return the list_vh_class
     */
    public ArrayList getList_vh_class() {
        return list_vh_class;
    }

    /**
     * @param list_vh_class the list_vh_class to set
     */
    public void setList_vh_class(ArrayList list_vh_class) {
        this.list_vh_class = list_vh_class;
    }

    /**
     * @return the list_vm_catg
     */
    public ArrayList getList_vm_catg() {
        return list_vm_catg;
    }

    /**
     * @param list_vm_catg the list_vm_catg to set
     */
    public void setList_vm_catg(ArrayList list_vm_catg) {
        this.list_vm_catg = list_vm_catg;
    }

    /**
     * @return the list_maker_model
     */
    public ArrayList getList_maker_model() {
        return list_maker_model;
    }

    /**
     * @param list_maker_model the list_maker_model to set
     */
    public void setList_maker_model(ArrayList list_maker_model) {
        this.list_maker_model = list_maker_model;
    }

    /**
     * @return the renderPermitPanel
     */
    public boolean isRenderPermitPanel() {
        return renderPermitPanel;
    }

    /**
     * @param renderPermitPanel the renderPermitPanel to set
     */
    public void setRenderPermitPanel(boolean renderPermitPanel) {
        this.renderPermitPanel = renderPermitPanel;
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
     * @return the totalUserChrg
     */
    public Long getTotalUserChrg() {
        return totalUserChrg;
    }

    /**
     * @param totalUserChrg the totalUserChrg to set
     */
    public void setTotalUserChrg(Long totalUserChrg) {
        this.setTotalUserChrg((long) totalUserChrg);
    }

    /**
     * @return the renderAllPanel
     */
    public boolean isRenderAllPanel() {
        return renderAllPanel;
    }

    /**
     * @param renderAllPanel the renderAllPanel to set
     */
    public void setRenderAllPanel(boolean renderAllPanel) {
        this.renderAllPanel = renderAllPanel;
    }

    /**
     * @return the ownerDtlsDobj
     */
    public OwnerDetailsDobj getOwnerDtlsDobj() {
        return ownerDtlsDobj;
    }

    /**
     * @param ownerDtlsDobj the ownerDtlsDobj to set
     */
    public void setOwnerDtlsDobj(OwnerDetailsDobj ownerDtlsDobj) {
        this.ownerDtlsDobj = ownerDtlsDobj;
    }

    /**
     * @return the is_taxPaid
     */
    public boolean isIs_taxPaid() {
        return is_taxPaid;
    }

    /**
     * @param is_taxPaid the is_taxPaid to set
     */
    public void setIs_taxPaid(boolean is_taxPaid) {
        this.is_taxPaid = is_taxPaid;
    }

    /**
     * @return the is_taxClear
     */
    public boolean isIs_taxClear() {
        return is_taxClear;
    }

    /**
     * @param is_taxClear the is_taxClear to set
     */
    public void setIs_taxClear(boolean is_taxClear) {
        this.is_taxClear = is_taxClear;
    }

    /**
     * @return the is_addtaxPaid
     */
    public boolean isIs_addtaxPaid() {
        return is_addtaxPaid;
    }

    /**
     * @param is_addtaxPaid the is_addtaxPaid to set
     */
    public void setIs_addtaxPaid(boolean is_addtaxPaid) {
        this.is_addtaxPaid = is_addtaxPaid;
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
     * @return the is_addtaxClear
     */
    public boolean isIs_addtaxClear() {
        return is_addtaxClear;
    }

    /**
     * @param is_addtaxClear the is_addtaxClear to set
     */
    public void setIs_addtaxClear(boolean is_addtaxClear) {
        this.is_addtaxClear = is_addtaxClear;
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
     * @return the isTaxExemp
     */
    public boolean isIsTaxExemp() {
        return isTaxExemp;
    }

    /**
     * @param isTaxExemp the isTaxExemp to set
     */
    public void setIsTaxExemp(boolean isTaxExemp) {
        this.isTaxExemp = isTaxExemp;
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
     * @return the renderModelCost
     */
    public boolean isRenderModelCost() {
        return renderModelCost;
    }

    /**
     * @param renderModelCost the renderModelCost to set
     */
    public void setRenderModelCost(boolean renderModelCost) {
        this.renderModelCost = renderModelCost;
    }

    public CommonChargesForAllBean getCommonCharges() {
        return commonCharges;
    }

    public void setCommonCharges(CommonChargesForAllBean commonCharges) {
        this.commonCharges = commonCharges;
    }

    public boolean isShowCommonChargesForAll() {
        return showCommonChargesForAll;
    }

    public void setShowCommonChargesForAll(boolean showCommonChargesForAll) {
        this.showCommonChargesForAll = showCommonChargesForAll;
    }

    public long getCommonChargesAmt() {
        return commonChargesAmt;
    }

    public void setCommonChargesAmt(long commonChargesAmt) {
        this.commonChargesAmt = commonChargesAmt;
    }

    public List<DOTaxDetail> getTaxDescriptionList() {
        return taxDescriptionList;
    }

    public void setTaxDescriptionList(List<DOTaxDetail> taxDescriptionList) {
        this.taxDescriptionList = taxDescriptionList;
    }

    public void refreshTaxMode() {

        Exception e = null;

        try {
            setTotalTaxAmount(0l);
            commonChargesAmt = 0l;
            PassengerPermitDetailDobj permitDob = getPermitPanelBean().getPermitDobj();
            VehicleParameters vehicleParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj, permitDob);
            listTaxForm = TaxServer_Impl.getListTaxForm(getOwnerDobj(), vehicleParameters);

            setUserChrg(EpayImpl.getPurposeCodeFee(ownerDobj, ownerDobj.getVh_class(), 99, ownerDobj.getVch_catg()));
            if (getUserChrg() > 0l) {
                setRenderUserChargesAmountPanel(true);
            } else {
                setRenderUserChargesAmountPanel(false);
            }

            for (TaxFormPanelBean bean : listTaxForm) {
                Map<String, String> taxPaidAndClearDetail = TaxServer_Impl.getTaxPaidAndClearDetail(regn_no, bean.getPur_cd());
                for (Map.Entry<String, String> entry : taxPaidAndClearDetail.entrySet()) {
                    if (entry.getKey().equals("taxpaid")) {
                        if (entry.getValue() != null && !entry.getValue().equals("")) {
                            setIs_taxPaid(true);
                            setTaxPaidLabel(entry.getValue());
                        } else {
                            setIs_taxPaid(false);
                        }
                    }
                    if (entry.getKey().equals("addtaxpaid")) {
                        if (entry.getValue() != null && !entry.getValue().equals("")) {
                            setIs_addtaxPaid(true);
                            setAddTaxPaidLabel(entry.getValue());
                        } else {
                            setIs_addtaxPaid(false);
                        }
                    }
                    if (entry.getKey().equals("taxclear")) {
                        if (entry.getValue() != null && !entry.getValue().equals("")) {
                            setIs_taxClear(true);
                            setTaxClearLabel(entry.getValue());
                        } else {
                            setIs_taxClear(false);
                        }
                    }
                    if (entry.getKey().equals("addtaxclear")) {
                        if (entry.getValue() != null && !entry.getValue().equals("")) {
                            setIs_addtaxClear(true);
                            setAddTaxClearLabel(entry.getValue());
                        } else {
                            setIs_addtaxClear(false);
                        }
                    }

                    if (entry.getKey().equals("taxExemp")) {
                        if (entry.getValue() != null && !entry.getValue().equals("")) {
                            setIsTaxExemp(true);
                            setTaxExempLabel(entry.getValue());
                        } else {
                            setIsTaxExemp(false);
                        }
                    }

                }
            }
            // for Transaction Charges
            commonCharges.getCommaonCharges(getOwnerDobj());
            if (commonCharges.getCommon_charge_list() != null) {
                showCommonChargesForAll = true;
                for (EpayDobj dobj : commonCharges.getCommon_charge_list()) {
                    commonChargesAmt = commonChargesAmt + dobj.getE_TaxFee();
                }
                if (getTotalTaxAmount() == 0) {
                    setTotalTaxAmount(commonChargesAmt);
                } else {
                    setTotalTaxAmount(getTotalTaxAmount() + commonChargesAmt);
                }

            } else {
                showCommonChargesForAll = false;
            }

        } catch (VahanException ex) {
            e = ex;
        } catch (Exception ex) {
            e = ex;
        }

        if (e != null) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(),
                    e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }

    }

    public void getBreakUpDetailsByPurpose(TaxFormPanelBean bean) {
        taxDescriptionList = bean.getTaxDescriptionList();
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("new_veh_fee_subview:sub_view_tax_dtls:opBreakupDetails");
        PrimeFaces.current().executeScript("PF('dia_tax_breakup').show()");
    }

    public void delDuplicateVehDetails(OwnerDetailsDobj ownerDtls) {
        try {
            //For restricting the user to generate application no again and again.start           
            setStatusList(ServerUtil.applicationStatus(this.regn_no.trim()));
            if (statusList != null && !statusList.isEmpty()) {
                PrimeFaces.current().executeScript("PF('varInwardedApplNo').show()");
                return;
            }
            if ("TN".equals(sessionVariables.getStateCodeSelected()) && moveToHistoryOwnerDtls == null && ownerDtls != null && ownerDtls.getHpaDobj() != null) {
                List<HpaDobj> hypth = new HpaImpl().getHypothecationList(ownerDtls.getRegn_no(), ownerDtls.getState_cd(), currentOffCd);
                if (hypth == null || hypth.isEmpty()) {
                    moveToHistoryOwnerDtls = ownerDtls;
                    // RequestContext rc = RequestContext.getCurrentInstance();
                    currentOfficeName = ServerUtil.getOfficeName(currentOffCd, sessionVariables.getStateCodeSelected());
                    PrimeFaces.current().ajax().update("confirmDialog");
                    PrimeFaces.current().executeScript("PF('confirmDialogVar').show()");
                    return;
                }
            }
            boolean isDelete = new DelDupRegnNoImpl().deleteDuplicateVehicle(ownerDtls);
            if (isDelete) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Registration Number Deleted Successfully"));
                PrimeFaces.current().executeScript("PF('varDupRegNo').hide()");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void moveHistoryDuplicateVehDetails(String moveHPAType) {
        try {
            if (getMoveToHistoryOwnerDtls() != null) {
                getMoveToHistoryOwnerDtls().setMoveHPADetails(moveHPAType);
                boolean isDelete = new DelDupRegnNoImpl().deleteDuplicateVehicle(getMoveToHistoryOwnerDtls());
                if (isDelete) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Registration Number Deleted Successfully"));
                    PrimeFaces.current().executeScript("PF('varDupRegNo').hide()");
                    PrimeFaces.current().executeScript("PF('confirmDialogVar').hide()");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    /**
     * @return the taxAssesment
     */
    public boolean isTaxAssesment() {
        return taxAssesment;
    }

    /**
     * @param taxAssesment the taxAssesment to set
     */
    public void setTaxAssesment(boolean taxAssesment) {
        this.taxAssesment = taxAssesment;
    }

    /**
     * @return the headerDescription
     */
    public String getHeaderDescription() {
        return headerDescription;
    }

    /**
     * @param headerDescription the headerDescription to set
     */
    public void setHeaderDescription(String headerDescription) {
        this.headerDescription = headerDescription;
    }

    /**
     * @return the totalTaxAmount
     */
    public long getTotalTaxAmount() {
        return totalTaxAmount;
    }

    /**
     * @param totalTaxAmount the totalTaxAmount to set
     */
    public void setTotalTaxAmount(long totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    /**
     * @return the totalBalanceAmount
     */
    public long getTotalBalanceAmount() {
        return totalBalanceAmount;
    }

    /**
     * @param totalBalanceAmount the totalBalanceAmount to set
     */
    public void setTotalBalanceAmount(long totalBalanceAmount) {
        this.totalBalanceAmount = totalBalanceAmount;
    }

    /**
     * @return the excessAmount
     */
    public long getExcessAmount() {
        return excessAmount;
    }

    /**
     * @param excessAmount the excessAmount to set
     */
    public void setExcessAmount(long excessAmount) {
        this.excessAmount = excessAmount;
    }

    /**
     * @return the totalDraftAmount
     */
    public long getTotalDraftAmount() {
        return totalDraftAmount;
    }

    /**
     * @param totalDraftAmount the totalDraftAmount to set
     */
    public void setTotalDraftAmount(long totalDraftAmount) {
        this.totalDraftAmount = totalDraftAmount;
    }

    /**
     * @return the userChrg
     */
    public long getUserChrg() {
        return userChrg;
    }

    /**
     * @param userChrg the userChrg to set
     */
    public void setUserChrg(long userChrg) {
        this.userChrg = userChrg;
    }

    /**
     * @param totalUserChrg the totalUserChrg to set
     */
    public void setTotalUserChrg(long totalUserChrg) {
        this.totalUserChrg = totalUserChrg;
    }

    /**
     * @return the registrationDetails
     */
    public String getRegistrationDetails() {
        return registrationDetails;
    }

    /**
     * @param registrationDetails the registrationDetails to set
     */
    public void setRegistrationDetails(String registrationDetails) {
        this.registrationDetails = registrationDetails;
    }

    public SeatAllotedDetails getSelectedSeat() {
        return selectedSeat;
    }

    public void setSelectedSeat(SeatAllotedDetails selectedSeat) {
        this.selectedSeat = selectedSeat;
    }

    /**
     * @return the dupRegnList
     */
    public List<OwnerDetailsDobj> getDupRegnList() {
        return dupRegnList;
    }

    /**
     * @param dupRegnList the dupRegnList to set
     */
    public void setDupRegnList(List<OwnerDetailsDobj> dupRegnList) {
        this.dupRegnList = dupRegnList;
    }

    public boolean isRenderPushBackSeatPanel() {
        return renderPushBackSeatPanel;
    }

    public void setRenderPushBackSeatPanel(boolean renderPushBackSeatPanel) {
        this.renderPushBackSeatPanel = renderPushBackSeatPanel;
    }

    /**
     * @return the renderExcessDraft
     */
    public boolean isRenderExcessDraft() {
        return renderExcessDraft;
    }

    /**
     * @param renderExcessDraft the renderExcessDraft to set
     */
    public void setRenderExcessDraft(boolean renderExcessDraft) {
        this.renderExcessDraft = renderExcessDraft;
    }

    /**
     * @return the excessDraftMessage
     */
    public String getExcessDraftMessage() {
        return excessDraftMessage;
    }

    /**
     * @param excessDraftMessage the excessDraftMessage to set
     */
    public void setExcessDraftMessage(String excessDraftMessage) {
        this.excessDraftMessage = excessDraftMessage;
    }

    /**
     * @return the currentOffCd
     */
    public int getCurrentOffCd() {
        return currentOffCd;
    }

    /**
     * @param currentOffCd the currentOffCd to set
     */
    public void setCurrentOffCd(int currentOffCd) {
        this.currentOffCd = currentOffCd;
    }

    /**
     * @return the statusList
     */
    public List<Status_dobj> getStatusList() {
        return statusList;
    }

    /**
     * @param statusList the statusList to set
     */
    public void setStatusList(List<Status_dobj> statusList) {
        this.statusList = statusList;
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

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

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
     * @return the taxDefList
     */
    public boolean isTaxDefList() {
        return taxDefList;
    }

    /**
     * @param taxDefList the taxDefList to set
     */
    public void setTaxDefList(boolean taxDefList) {
        this.taxDefList = taxDefList;
    }

    /**
     * @return the moveToHistoryOwnerDtls
     */
    public OwnerDetailsDobj getMoveToHistoryOwnerDtls() {
        return moveToHistoryOwnerDtls;
    }

    /**
     * @param moveToHistoryOwnerDtls the moveToHistoryOwnerDtls to set
     */
    public void setMoveToHistoryOwnerDtls(OwnerDetailsDobj moveToHistoryOwnerDtls) {
        this.moveToHistoryOwnerDtls = moveToHistoryOwnerDtls;
    }

    /**
     * @return the currentOfficeName
     */
    public String getCurrentOfficeName() {
        return currentOfficeName;
    }

    /**
     * @param currentOfficeName the currentOfficeName to set
     */
    public void setCurrentOfficeName(String currentOfficeName) {
        this.currentOfficeName = currentOfficeName;
    }

    /**
     * @return the renderMoveToHistoryButton
     */
    public boolean isRenderMoveToHistoryButton() {
        return renderMoveToHistoryButton;
    }

    /**
     * @param renderMoveToHistoryButton the renderMoveToHistoryButton to set
     */
    public void setRenderMoveToHistoryButton(boolean renderMoveToHistoryButton) {
        this.renderMoveToHistoryButton = renderMoveToHistoryButton;
    }
}
