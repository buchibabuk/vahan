/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
import nic.java.util.DateUtils;
import nic.java.util.DateUtilsException;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.fillTaxParametersFromDobj;
import nic.vahan.CommonUtils.TaxUtils;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.permit.PermitPanelBean;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.dobj.SingleDraftPaymentDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.impl.EpayImpl;
import nic.vahan.form.impl.FeeDraftimpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.SingleDraftPaymentImpl;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
//import tax.web.service.VahanTaxParameters;
import nic.vahan.form.dobj.tax.VahanTaxParameters;
import static nic.vahan.form.impl.TaxServer_Impl.callTaxService;
import org.primefaces.component.tabview.Tab;

/**
 *
 * @author tranC075
 */
@ManagedBean(name = "singleDraftPayment")
@ViewScoped
public class SingleDraftPaymentBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(SingleDraftPaymentBean.class);
    private PaymentCollectionDobj paydobj;
    private List<PaymentCollectionDobj> paymentlist = new ArrayList<PaymentCollectionDobj>();
    private String header_name;
    private List instrumentList = null;
    private List bank_list = null;
    private String popUpMessage = null;
    private Date max_draft_date = null;
    private Date min_draft_date = null;
    private String regn_no;
    private boolean renderAllPanel = false;
    private List<TaxFormPanelBean> listTaxForm = new ArrayList();
    private OwnerImpl ownerImpl = null;
    private OwnerDetailsDobj ownerDtlsDobj;
    private Owner_dobj ownerDobj = new Owner_dobj();
    private PermitPanelBean permitPanelBean = null;
    private boolean disable = false;
    private boolean renderPermitPanel = false;
    private Long userChrg;
    private boolean renderUserChargesAmountPanel = false;
    private boolean is_taxPaid = false;
    private String taxPaidLabel;
    private boolean is_addtaxPaid = false;
    private String addTaxPaidLabel;
    private boolean is_taxClear = false;
    private String taxClearLabel;
    private boolean is_addtaxClear = false;
    private String addTaxClearLabel;
    private boolean isTaxExemp = false;
    private String taxExempLabel;
    private Long totalUserChrg = 0l;
    private Long totalTaxAmount;
    private List<SingleDraftPaymentDobj> addToCartList = new ArrayList<>();
    private boolean disableAmountDataTable = false;
    private boolean showSave = false;
    private String permitRefreshDtl = null;
    private String permitTaxDtl = null;
    private PaymentCollectionBean paymentCollectionBean = null;
    private ArrayList listOfRcptNo = new ArrayList();
    @ManagedProperty(value = "#{rcpt_bean}")
    private ReceiptMasterBean rcpt_bean;
    private String permitDefaultValue = "";
    private boolean autoRunTaxListener = true;
    private boolean showSingleDraftPage = true;
    private Date oldDate = new Date();
    private List<SingleDraftPaymentDobj> oldReceiptList = new ArrayList<>();
    private boolean showReset = false;
    private List<DOTaxDetail> taxDescriptionList = new ArrayList<>();
    private long commonChargesAmt = 0;
    private boolean renderPushBackSeatPanel = false;

    public SingleDraftPaymentBean() {
        header_name = " Multiple Draft";
        String[][] instrmentType = MasterTableFiller.masterTables.TM_INSTRUMENTS.getData();
        String[][] data = MasterTableFiller.masterTables.TM_BANK.getData();
        try {
            max_draft_date = new Date();
            min_draft_date = DateUtils.addToDate(max_draft_date, 2, -3);
        } catch (DateUtilsException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        instrumentList = new ArrayList();
        instrumentList.add(new SelectItem("-1", "Select"));
        for (int i = 0; i < instrmentType.length; i++) {
            if (instrmentType[i][0].equals("D") || instrmentType[i][0].equals("C")) {
                instrumentList.add(new SelectItem(instrmentType[i][0], instrmentType[i][1]));
            }
        }
        bank_list = new ArrayList();
        bank_list.add(new SelectItem("-1", "Select Bank"));
        for (int i = 0; i < data.length; i++) {
            bank_list.add(new SelectItem(data[i][0], data[i][1]));
        }
    }

    @PostConstruct
    public void postConstruct() {
        ownerImpl = new OwnerImpl();
        //  taxServerImpl = new TaxServer_Impl();
        paymentCollectionBean = new PaymentCollectionBean();
        permitPanelBean = new PermitPanelBean();
        //   permit_Impl = new PermitImpl();
        String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        //   getList_vh_class().clear();
        for (int i = 0; i < data.length; i++) {
            // getList_vh_class().add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_VCH_CATG.getData();
        for (int i = 0; i < data.length; i++) {
            //  getList_vm_catg().add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_MODELS.getData();
        for (int i = 0; i < data.length; i++) {
            // getList_maker_model().add(new SelectItem(data[i][1], data[i][2]));
        }

        if (paymentlist.isEmpty()) {
            getPaymentlist().add(new PaymentCollectionDobj());
        }
        addToCartList = new SingleDraftPaymentImpl().getCartList();
        if (addToCartList.size() > 0) {
            disableAmountDataTable = true;
            paymentlist = new SingleDraftPaymentImpl().setPaymentList();

        }
        if (addToCartList.size() > 1) {
            showSave = true;
        }
    }

    public void validateInstrumentNumber(AjaxBehaviorEvent event) {
        PaymentCollectionDobj selectedDobj = (PaymentCollectionDobj) event.getComponent().getAttributes().get("paymentDobj");
        String error_message = null;
        if (selectedDobj.getBank_name() == null || selectedDobj.getBank_name().equals("-1")) {
            error_message = "Please Select Bank Name";
        }

        String dupInstrument = FeeDraftimpl.chkDuplicateInstrumentNo(String.valueOf(selectedDobj.getNumber()),
                selectedDobj.getBank_name(), selectedDobj.getInstrument(), Util.getUserStateCode());

        if (dupInstrument != null) {
            error_message = dupInstrument;
            selectedDobj.setNumber("");
        }

        if (FeeDraftimpl.chkDuplicateInstrumentNoFromVpInstrumentcart(String.valueOf(selectedDobj.getNumber()),
                selectedDobj.getBank_name(), selectedDobj.getInstrument())) {
            error_message = "Duplicate Instrument Number";
            selectedDobj.setNumber("");
        }
        int commonInstrumentsNo = 0;
        for (PaymentCollectionDobj dobj : getPaymentlist()) {
            if (dobj.getNumber() != null && selectedDobj.getNumber() != null && dobj.getNumber().equals(selectedDobj.getNumber())
                    && dobj.getBank_name() != null && selectedDobj.getNumber() != null && dobj.getBank_name().equals(dobj.getBank_name())) {
                commonInstrumentsNo++;
                if (commonInstrumentsNo > 1) {
                    selectedDobj.setNumber("");
                }
            }
        }

        if (commonInstrumentsNo > 1) {
            error_message = "Duplicate Instrument Number";
        }
        if (error_message != null) {
            setPopUpMessage(error_message);
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, error_message, error_message);
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
    }

    public void addRow(String number, String bankCode) {
        String mode = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("actionmode");
        if ("add".equalsIgnoreCase(mode)) {
            if (number == null || "-1".equals(number) || "".equals(number) || bankCode == null || "-1".equals(bankCode) || "".equals(bankCode)) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Select Bank! And Valid Number", "Please Select Bank! And Valid Number");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
            // add row in the payment panel
            if (getPaymentlist().size() == 4) {
                LOGGER.info(" Maximum payment Selected");
            } else {
                getPaymentlist().add(new PaymentCollectionDobj());
            }

        } else if ("minus".equalsIgnoreCase(mode)) {
            PaymentCollectionDobj selectedNumber = new PaymentCollectionDobj(number, bankCode);
            int index = getPaymentlist().indexOf(selectedNumber);
            if (getPaymentlist().size() == 1) {//can't remove last element from row
                return;
            }
            getPaymentlist().remove(index);
        }
    }

    public void getVehicleDetails() {
        try {
            if (Utility.isNullOrBlank(getRegn_no())) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Enter Vehicle Number!", "Please Enter Vehicle Number!");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
            for (PaymentCollectionDobj dobj : getPaymentlist()) {
                if ((dobj.getBank_name().equals("-1")) || Utility.isNullOrBlank(dobj.getNumber()) || !(dobj.getDated() != null)
                        || Utility.isNullOrBlank(dobj.getAmount()) || Utility.isNullOrBlank(dobj.getBranch())) {
                    FacesMessage message = null;
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Fill Your Draft First", "Please Fill Your Draft First");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }
            }

            for (SingleDraftPaymentDobj list : addToCartList) {
                if (getRegn_no().toUpperCase().equalsIgnoreCase(list.getRegn_no())) {
                    FacesMessage message = null;
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration No already exist in Cart", "Registration No already exist in Cart");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }
            }
            showReset = true;
            disableAmountDataTable = true;

            setRegn_no(getRegn_no().toUpperCase());
            getListTaxForm().clear();
            if (getOwnerImpl() != null) {
                if (TaxServer_Impl.validateTaxPaidForDay(getRegn_no())) {
                    throw new VahanException("You can't pay the Motor Vehicle Tax twice on the same day!!! Please come again on next day or cancel the previous receipt and pay again.");
                }
                setOwnerDtlsDobj(getOwnerImpl().getOwnerDetails(this.getRegn_no().trim(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd()));

                //   setOwDobj(ownerImpl.set_Owner_appl_db_to_dobj(getRegn_no(), null, null, 2));
                if (getOwnerDtlsDobj() == null || !ownerDtlsDobj.getState_cd().equals(Util.getUserStateCode())
                        || getOwnerDtlsDobj().getOff_cd() != Util.getSelectedSeat().getOff_cd()
                        || !(ownerDtlsDobj.getStatus().equals("A") || ownerDtlsDobj.getStatus().equals("Y"))) {
                    throw new VahanException("Either Invalid vehicle Registration No or You are not authorized to collect the MV Tax for this vehicle no.");
                }
                Owner_dobj owDobj = getOwnerImpl().getOwnerDobj(getOwnerDtlsDobj());
                setOwnerDobj(owDobj);
                if (getOwnerDobj() != null) {
                    setRenderAllPanel(true);
                    setDisable(true);
                    PassengerPermitDetailDobj permitDob = TaxServer_Impl.getPermitInfoForSavedTax(getOwnerDobj());
                    VehicleParameters vehicleParameters = FormulaUtils.fillVehicleParametersFromDobj(getOwnerDobj(), permitDob);
                    List<TaxFormPanelBean> listTax = TaxServer_Impl.getListTaxForm(owDobj, vehicleParameters);
                    if (listTax != null) {
                        Iterator<TaxFormPanelBean> itr = listTax.iterator();
                        while (itr.hasNext()) {
                            TaxFormPanelBean ob = itr.next();
                            if (ob.getPur_cd() != 58) {
                                if (!(Util.getUserStateCode().equals("OR") || Util.getUserStateCode().equals("BR"))) {
                                    itr.remove();
                                }
                            }
                        }
                    }
                    setListTaxForm(listTax);
                    int vehClass = getOwnerDobj().getVh_class();

                    if (permitDob != null && permitDob.getOtherCriteria() != null && !permitDob.getOtherCriteria().equalsIgnoreCase("0")) {
                        getOwnerDobj().setOther_criteria(Integer.parseInt(permitDob.getOtherCriteria()));
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
                    //  permitDefaultValue = getPermitPanelBean().getPermitDobj().getPmt_type_code();
                    setUserChrg(EpayImpl.getPurposeCodeFee(getOwnerDobj(), getOwnerDobj().getVh_class(), 99, getOwnerDobj().getVch_catg()));
                    if (getUserChrg() > 0l) {
                        setRenderUserChargesAmountPanel(true);
                    } else {
                        setRenderUserChargesAmountPanel(false);
                    }

                    for (TaxFormPanelBean bean : getListTaxForm()) {
                        Map<String, String> taxPaidAndClearDetail = TaxServer_Impl.getTaxPaidAndClearDetail(getRegn_no(), bean.getPur_cd());
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

                } else {
                    FacesMessage message = null;
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle Details Not Found!", "Vehicle Details Not Found!");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }

            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception ex) {
            getLOGGER().error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void taxModeListener(AjaxBehaviorEvent event) {
        try {
            if (renderPermitPanel && permitPanelBean.getPermitDobj() != null) {
                permitTaxDtl = permitPanelBean.getPermitDobj().toString();
            }
            //  permitDefaultValue = getPermitPanelBean().getPermitDobj().getPmt_type_code();
            TaxFormPanelBean taxSelectedFormBean = (TaxFormPanelBean) event.getComponent().getAttributes().get("taxBeanAttr");
            if (taxSelectedFormBean.getTaxMode().equals("0")) {
                taxSelectedFormBean.resetAll();
                //updateTotalPayableAmount();
                return;
            }

            if (taxSelectedFormBean.getTaxMode().equalsIgnoreCase("O") || taxSelectedFormBean.getTaxMode().equalsIgnoreCase("L")) {
                taxSelectedFormBean.setNoOfUnits(1);
            }

            VahanTaxParameters taxParameters = fillTaxParametersFromDobj(ownerDobj, getPermitPanelBean().getPermitDobj());
            taxParameters.setTAX_MODE(taxSelectedFormBean.getTaxMode());
            taxParameters.setPUR_CD(taxSelectedFormBean.getPur_cd());
            Date taxDueFrom = TaxServer_Impl.getTaxDueFromDate(ownerDobj, taxSelectedFormBean.getPur_cd());
            if (taxDueFrom == null || taxDueFrom == ownerDobj.getPurchase_dt() || taxDueFrom == ownerDobj.getRegn_dt()) {
                if (taxSelectedFormBean.getInitialDueDate() != null
                        && DateUtils.compareDates(taxSelectedFormBean.getInitialDueDate(), taxDueFrom) == 2
                        && DateUtils.compareDates(taxSelectedFormBean.getInitialDueDate(), taxSelectedFormBean.getInitialEffectiveDate()) == 2) {
                    taxDueFrom = taxSelectedFormBean.getInitialDueDate();
                } else if (taxSelectedFormBean.getInitialEffectiveDate() != null
                        && DateUtils.compareDates(taxSelectedFormBean.getInitialEffectiveDate(), taxDueFrom) == 2
                        && DateUtils.compareDates(taxSelectedFormBean.getInitialEffectiveDate(), taxSelectedFormBean.getInitialDueDate()) == 2) {
                    taxDueFrom = taxSelectedFormBean.getInitialEffectiveDate();
                }
            }

            taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(taxDueFrom));
            taxParameters.setNEW_VCH("N");
            taxParameters.setTAX_MODE_NO_ADV(taxSelectedFormBean.getNoOfUnits());
            List<DOTaxDetail> listTaxBreakUp = callTaxService(taxParameters);
            /**
             * For Delhi No Panelty is collected for State Govt Vehicle in case
             * of single draft for multiple vehicles
             */
            if ("DL".equalsIgnoreCase(ownerDobj.getState_cd())
                    && listTaxBreakUp != null && !listTaxBreakUp.isEmpty() && (ownerDobj.getOwner_cd() == 5 || ownerDobj.getOwner_cd() == 6)) {
                for (DOTaxDetail doTax : listTaxBreakUp) {
                    doTax.setPENALTY(0.0);
                }
            }

            taxSelectedFormBean.setTaxDescriptionList(TaxUtils.sortTaxDetails(listTaxBreakUp));
            taxSelectedFormBean.updateTaxBean();
            // updateTotalPayableAmount();

        } catch (VahanException ve) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

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
//        return tempTaxList;
//    }
    public void refreshTaxMode() {

        Exception e = null;

        try {
            setTotalTaxAmount(0l);
            setCommonChargesAmt(0l);
            PassengerPermitDetailDobj permitDob = getPermitPanelBean().getPermitDobj();
            VehicleParameters vehicleParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj, permitDob);
            listTaxForm = TaxServer_Impl.getListTaxForm(getOwnerDobj(), vehicleParameters);

            setUserChrg(EpayImpl.getPurposeCodeFee(ownerDobj, ownerDobj.getVh_class(), 99, ownerDobj.getVch_catg()));
            if (getUserChrg() > 0l) {
                setRenderUserChargesAmountPanel(true);
            } else {
                setRenderUserChargesAmountPanel(false);
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

    public void addToCart() {
        //String newPermitCode = getPermitPanelBean().getPermitDobj().getPmt_type_code();

        if (renderPermitPanel && permitPanelBean.getPermitDobj() != null) {
            permitRefreshDtl = permitPanelBean.getPermitDobj().toString();
            if (permitTaxDtl != null && !permitTaxDtl.equals(permitRefreshDtl)) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Tax is Calculated for Different Permit Details,Please Click on the Refresh-Tax Details Button",
                        "Tax is Calculated for Different Permit Details,Please Click on the  Refresh-Tax Details Button");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;

            }
        }
        boolean valid = true;
        try {
            for (TaxFormPanelBean dobj : listTaxForm) {
                if (valid) {
                    if (!(dobj.getTaxMode()).equals("0")) {
                        valid = false;
                    }
                }
            }

            if (valid) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Select Tax Mode", "Please Select Tax Mode");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
            long totalAmount = 0;
            for (PaymentCollectionDobj pcDobj : paymentlist) {
                totalAmount = totalAmount + (Long.parseLong(pcDobj.getAmount()));
            }
            long totalTax = 0;
            for (TaxFormPanelBean tfpDobj : listTaxForm) {
                totalTax = totalTax + tfpDobj.getFinalTaxAmount();
            }
            float totalCartAmount = 0;
            for (SingleDraftPaymentDobj sdpDobj : addToCartList) {
                totalCartAmount = totalCartAmount + sdpDobj.getAmount();
            }
            if (totalAmount - totalCartAmount - totalTax >= 0) {
                addToCartList = new SingleDraftPaymentImpl().setInstrumentCart(paymentlist, listTaxForm, regn_no, permitPanelBean.getPermitDobj());
                if (addToCartList.size() > 1) {
                    showSave = true;
                }
                regn_no = "";
                renderAllPanel = false;
                showReset = false;
            } else {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Amount Exceeded", "Amount Exceeded");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;

            }
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
        }
    }

    public void confirmSave() {
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update(":masterLayout:confirmMsg");
        PrimeFaces.current().executeScript("PF('save').show()");
    }

    public String saveDetails() {
        //paymentlist = null;
        // paymentlist = new SingleDraftPaymentImpl().setPaymentList();
        Exception e = null;
        regn_no = "";
        String printReceipt = "";
        try {

            //List<SingleDraftPaymentDobj> list = new ArrayList<SingleDraftPaymentDobj>();
            //SingleDraftPaymentImpl taxServer = new SingleDraftPaymentImpl();
            //addToCartList = new SingleDraftPaymentImpl().setWishCartList();
            if (addToCartList == null || addToCartList.size() == 0) {
                throw new VahanException("Empty Cart List");
            }
            long totalInstrumentAmount = 0;
            for (PaymentCollectionDobj pcDobj : paymentlist) {
                totalInstrumentAmount = totalInstrumentAmount + (Long.parseLong(pcDobj.getAmount()));
            }
            float totalCartAmount = 0;
            for (SingleDraftPaymentDobj sdpDobj : addToCartList) {
                totalCartAmount = totalCartAmount + sdpDobj.getAmount();
            }
            long excessAmount = (long) (totalInstrumentAmount - totalCartAmount);
            if (totalCartAmount <= totalInstrumentAmount - 50) {
                throw new VahanException("There is a balance instrument amount Rs. " + (totalCartAmount - totalInstrumentAmount) + ". Please add more vehicles in the cart.");
            }
            paymentlist = new SingleDraftPaymentImpl().setPaymentList();
            printReceipt = new SingleDraftPaymentImpl().saveReceiptDetails(addToCartList, paymentlist, excessAmount);
            getRcpt_bean().reset();
            addToCartList.clear();
//            updateTotalPayableAmount();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = ex;
        }

        if (e != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
            return null;
        }
        return printReceipt;
    }

    public void deleteRow(String regnNo) {
        String mode = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("actionmode");
        if ("minus".equalsIgnoreCase(mode)) {
            SingleDraftPaymentDobj selectedNumber = new SingleDraftPaymentDobj(regnNo);
            getAddToCartList().remove(selectedNumber);
            try {
                new SingleDraftPaymentImpl().deleteCartList(regnNo);
                addToCartList = new SingleDraftPaymentImpl().getCartList();
                if (addToCartList.size() == 0) {
                    disableAmountDataTable = false;
                    paymentlist = new ArrayList<PaymentCollectionDobj>();
                    getPaymentlist().add(new PaymentCollectionDobj());
                    showSave = false;
                }
                if (addToCartList.size() > 1) {
                    showSave = true;
                } else {
                    showSave = false;
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public void showOldReceipts() {
        showSingleDraftPage = false;
        oldReceiptList = new SingleDraftPaymentImpl().getOldReceiptList(getOldDate());
    }

    public void backToSingleDraftPage() {
        showSingleDraftPage = true;
    }

    public String printReceipt(SingleDraftPaymentDobj selectedValue) {
        SingleDraftPaymentDobj paymentAndReceiptList = new SingleDraftPaymentDobj();
        paymentAndReceiptList = new SingleDraftPaymentImpl().getPaymentAndReceiptDetailsAgainstInstrumentCd(selectedValue.getInscd());

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("list", paymentAndReceiptList.getListSinglePaymentDraftDobj());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("paymentlist", paymentAndReceiptList.getPaymentlist());

        return "PrintMultipleDraftReceiptReport";
    }

    public void getBreakUpDetailsByPurpose(TaxFormPanelBean bean) {

        if (taxDescriptionList != null) {
            taxDescriptionList.clear();
        }

        taxDescriptionList = bean.getTaxDescriptionList();
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("new_veh_fee_subview:sub_view_tax_dtls:opBreakupDetails");
        PrimeFaces.current().executeScript("PF('dia_tax_breakup').show()");
    }

    public void oldDateListener(AjaxBehaviorEvent event) {
        oldReceiptList = new SingleDraftPaymentImpl().getOldReceiptList(getOldDate());
    }

    public void isViewIsRendered() {
        //used for calculating tax based on modes automatically
        autoRunTaxListener = false;
        //Please don't remove this as it is required in formtax_details.xhtml
    }

    public void reset() {
        showReset = false;
        renderAllPanel = false;
        regn_no = "";
    }

    /**
     * @return the paydobj
     */
    public PaymentCollectionDobj getPaydobj() {
        return paydobj;
    }

    /**
     * @param paydobj the paydobj to set
     */
    public void setPaydobj(PaymentCollectionDobj paydobj) {
        this.paydobj = paydobj;
    }

    /**
     * @return the paymentlist
     */
    public List<PaymentCollectionDobj> getPaymentlist() {
        return paymentlist;
    }

    /**
     * @param paymentlist the paymentlist to set
     */
    public void setPaymentlist(List<PaymentCollectionDobj> paymentlist) {
        this.paymentlist = paymentlist;
    }

    /**
     * @return the header_name
     */
    public String getHeader_name() {
        return header_name;
    }

    /**
     * @param header_name the header_name to set
     */
    public void setHeader_name(String header_name) {
        this.header_name = header_name;
    }

    /**
     * @return the instrumentList
     */
    public List getInstrumentList() {
        return instrumentList;
    }

    /**
     * @param instrumentList the instrumentList to set
     */
    public void setInstrumentList(List instrumentList) {
        this.instrumentList = instrumentList;
    }

    /**
     * @return the bank_list
     */
    public List getBank_list() {
        return bank_list;
    }

    /**
     * @param bank_list the bank_list to set
     */
    public void setBank_list(List bank_list) {
        this.bank_list = bank_list;
    }

    /**
     * @return the popUpMessage
     */
    public String getPopUpMessage() {
        return popUpMessage;
    }

    /**
     * @param popUpMessage the popUpMessage to set
     */
    public void setPopUpMessage(String popUpMessage) {
        this.popUpMessage = popUpMessage;
    }

    /**
     * @return the max_draft_date
     */
    public Date getMax_draft_date() {
        return max_draft_date;
    }

    /**
     * @param max_draft_date the max_draft_date to set
     */
    public void setMax_draft_date(Date max_draft_date) {
        this.max_draft_date = max_draft_date;
    }

    /**
     * @return the min_draft_date
     */
    public Date getMin_draft_date() {
        return min_draft_date;
    }

    /**
     * @param min_draft_date the min_draft_date to set
     */
    public void setMin_draft_date(Date min_draft_date) {
        this.min_draft_date = min_draft_date;
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
     * @return the ownerImpl
     */
    public OwnerImpl getOwnerImpl() {
        return ownerImpl;
    }

    /**
     * @param ownerImpl the ownerImpl to set
     */
    public void setOwnerImpl(OwnerImpl ownerImpl) {
        this.ownerImpl = ownerImpl;
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
     * @return the disable
     */
    public boolean isDisable() {
        return disable;
    }

    /**
     * @param disable the disable to set
     */
    public void setDisable(boolean disable) {
        this.disable = disable;
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
     * @return the addToCartList
     */
    public List<SingleDraftPaymentDobj> getAddToCartList() {
        return addToCartList;
    }

    /**
     * @param addToCartList the addToCartList to set
     */
    public void setAddToCartList(List<SingleDraftPaymentDobj> addToCartList) {
        this.addToCartList = addToCartList;
    }

    /**
     * @return the LOGGER
     */
    public static Logger getLOGGER() {
        return LOGGER;
    }

    /**
     * @param aLOGGER the LOGGER to set
     */
    public static void setLOGGER(Logger aLOGGER) {
        LOGGER = aLOGGER;
    }

    /**
     * @return the disableAmountDataTable
     */
    public boolean isDisableAmountDataTable() {
        return disableAmountDataTable;
    }

    /**
     * @param disableAmountDataTable the disableAmountDataTable to set
     */
    public void setDisableAmountDataTable(boolean disableAmountDataTable) {
        this.disableAmountDataTable = disableAmountDataTable;
    }

    /**
     * @return the showSave
     */
    public boolean isShowSave() {
        return showSave;
    }

    /**
     * @param showSave the showSave to set
     */
    public void setShowSave(boolean showSave) {
        this.showSave = showSave;
    }

    /**
     * @return the permitRefreshDtl
     */
    public String getPermitRefreshDtl() {
        return permitRefreshDtl;
    }

    /**
     * @param permitRefreshDtl the permitRefreshDtl to set
     */
    public void setPermitRefreshDtl(String permitRefreshDtl) {
        this.permitRefreshDtl = permitRefreshDtl;
    }

    /**
     * @return the paymentCollectionBean
     */
    public PaymentCollectionBean getPaymentCollectionBean() {
        return paymentCollectionBean;
    }

    /**
     * @param paymentCollectionBean the paymentCollectionBean to set
     */
    public void setPaymentCollectionBean(PaymentCollectionBean paymentCollectionBean) {
        this.paymentCollectionBean = paymentCollectionBean;
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
     * @return the listOfRcptNo
     */
    public ArrayList getListOfRcptNo() {
        return listOfRcptNo;
    }

    /**
     * @param listOfRcptNo the listOfRcptNo to set
     */
    public void setListOfRcptNo(ArrayList listOfRcptNo) {
        this.listOfRcptNo = listOfRcptNo;
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
     * @return the showSingleDraftPage
     */
    public boolean isShowSingleDraftPage() {
        return showSingleDraftPage;
    }

    /**
     * @param showSingleDraftPage the showSingleDraftPage to set
     */
    public void setShowSingleDraftPage(boolean showSingleDraftPage) {
        this.showSingleDraftPage = showSingleDraftPage;
    }

    /**
     * @return the oldDate
     */
    public Date getOldDate() {
        return oldDate;
    }

    /**
     * @param oldDate the oldDate to set
     */
    public void setOldDate(Date oldDate) {
        this.oldDate = oldDate;
    }

    /**
     * @return the oldReceiptList
     */
    public List<SingleDraftPaymentDobj> getOldReceiptList() {
        return oldReceiptList;
    }

    /**
     * @param oldReceiptList the oldReceiptList to set
     */
    public void setOldReceiptList(List<SingleDraftPaymentDobj> oldReceiptList) {
        this.oldReceiptList = oldReceiptList;
    }

    /**
     * @return the showReset
     */
    public boolean isShowReset() {
        return showReset;
    }

    /**
     * @param showReset the showReset to set
     */
    public void setShowReset(boolean showReset) {
        this.showReset = showReset;
    }

    public List<DOTaxDetail> getTaxDescriptionList() {
        return taxDescriptionList;
    }

    public void setTaxDescriptionList(List<DOTaxDetail> taxDescriptionList) {
        this.taxDescriptionList = taxDescriptionList;
    }

    /**
     * @return the commonChargesAmt
     */
    public long getCommonChargesAmt() {
        return commonChargesAmt;
    }

    /**
     * @param commonChargesAmt the commonChargesAmt to set
     */
    public void setCommonChargesAmt(long commonChargesAmt) {
        this.commonChargesAmt = commonChargesAmt;
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
}
