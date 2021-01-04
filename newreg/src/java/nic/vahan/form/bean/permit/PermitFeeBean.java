/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.eapplication.OnlinePaymentImpl;
import nic.vahan.form.bean.FormFeePanelBean;
import nic.vahan.form.bean.PaymentCollectionBean;
import nic.vahan.form.bean.ReceiptMasterBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.dobj.permit.PermitFeeDobj;
import nic.vahan.form.dobj.permit.CommonPermitFeeDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitFeeRequiredDataDobj;
import nic.vahan.form.dobj.permit.PermitShowFeeDetailDobj;
import nic.vahan.form.impl.FeeDraftimpl;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.permit.PermitFeeImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PermitLOIImpl;
import nic.vahan.form.impl.permit.PermitShowFeeDetailImpl;
import nic.vahan.form.impl.permit.PrintPermitDocInXhtmlImpl;
import nic.vahan.form.impl.permit.SurrenderPermitImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author tranC092
 */
@ManagedBean(name = "permit_fee")
@ViewScoped
public class PermitFeeBean extends AbstractApplBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PermitFeeBean.class);
    @ManagedProperty(value = "#{pmt_owner_dtls}")
    private PermitOwnerDetailBean ownerBean;
    @ManagedProperty(value = "#{pmtDtlsBean}")
    private PermitCheckDetailsBean pmtCheckDtsl;
    PermitOwnerDetailDobj owner_dobj = null;
    PermitOwnerDetailImpl ownerImpl = null;
    private long totalAmountPayable = 0;
    private List period_mode = new ArrayList();
    private String period = "";
    private String per_mode_sel = "";
    private String state_cd = "";
    private String regn_no = "";
    private String regn_no2 = "";
    private String regn_dt = "";
    private String owner_name = "";
    private String f_name = "";
    private String regn_type = "";
    private String vh_class = "";
    private String chasi_no = "";
    private String seat_cap = "";
    private String stand_cap = "";
    private String unld_wt = "";
    private String ld_wt = "";
    private String fuel = "";
    private String op_dt = "";
    private String color = "";
    private String norms = "";
    private String lblpermit_no = "";
    private String lblregn_no = "";
    private String permit_no = "";
    private String valid_in_months = "";
    private String amount1 = "";
    private String fine1 = "";
    private String total1 = "";
    private String amount2 = "";
    private String fine2 = "";
    private String total2 = "";
    private String pmt_no = "";
    private Date pmt_valid_from;
    private String grand_total = "";
    private String permit_type = "";
    private List pmt_type_list = new ArrayList();
    private String permit_domain = "";
    private List<SelectItem> pmt_domain_list = new ArrayList();
    private String permit_catg = "";
    private List<SelectItem> pmt_catg_list = new ArrayList();
    private String service_type = "";
    private List<SelectItem> pmt_service_type_list = new ArrayList();
    private String fee_desc1 = "";
    private List pmt_fee_desc1_list = new ArrayList();
    private String fee_desc2 = "";
    private List pmt_fee_desc2_list = new ArrayList();
    private boolean takeFeeDisable = false;
    private boolean renderPermit_dtls = false;
    private boolean vehiclePanelrender = false;
    private String radioValue;
    private String new_regn_no = "";
    private String new_f_name = "";
    private String new_o_name = "";
    private String new_vh_class_menu = "";
    private String new_regn_dt = "";
    private List new_vh_class = new ArrayList();
    private PaymentCollectionBean paymentBean = new PaymentCollectionBean();
    FeeDraftimpl feeDraftImpl = null;
    private String rept_no_msg;
    private String appl_no = "";
    private FormFeePanelBean feePanelBean = new FormFeePanelBean();
    @ManagedProperty(value = "#{rcpt_bean}")
    private ReceiptMasterBean rcpt_bean;
    @ManagedProperty(value = "#{showFee}")
    private PermitShowFeeDetailBean showFeeBean;
    private String offLetterNo = "";
    private String db_offerLetterNo = "";
    private boolean route_status;
    private boolean revertBackRender = false;
    private boolean pmtFeeDialogeVisible = false;
    private boolean pmtFeeOwnerDtls = false;
    private boolean printOfferLetter = false;
    private boolean showOffLtOption = false;
    private String offerInfoMsg = "";
    Map<String, String> confige = null;
    private String pmtSubType = "";
    private boolean pmtSubTypeRender = false;
    private List<PassengerPermitDetailDobj> routedata = new ArrayList<>();
    private String region_covered;
    private String app_no_msg;
    private boolean onlinePayment = false;
    private boolean onlinePaymentRender = false;
    private String userInfo = null;
    private boolean savePayment = false;
    private boolean cancelOnlinePayment = false;
    private String onlinePaymentMsg = null;
    private boolean pmtFeePaidAtTymOfRegnVisible = false;
    private String pmtFeePaidAtTymOfRegnLable = null;
    private SessionVariables sessionVariables = null;
    private String vahanMessages = null;
    private boolean otherthanCashMode;
    private PermitFeeRequiredDataDobj requiredDataDobj = new PermitFeeRequiredDataDobj();
    PermitShowFeeDetailImpl showFeeImpl = null;
    int feeCollectionSize = 0;
    private boolean cashModeDisabled = false;
    private boolean otherRouteAllow;

    public PermitFeeBean() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
            vahanMessages = "Session time Out";
            return;
        }
        confige = CommonPermitPrintImpl.getVmPermitStateConfiguration(sessionVariables.getStateCodeSelected());
        route_status = Boolean.parseBoolean(confige.get("temp_route_area").toString());
        setCashModeDisabled(Boolean.parseBoolean(confige.get("cash_mode_disabled").toString()));
        otherRouteAllow = Boolean.parseBoolean(confige.get("other_office_route_allow"));
        pmt_type_list = new ArrayList();
        feeDraftImpl = new FeeDraftimpl();
        String[][] data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
        pmt_type_list.add(new SelectItem("", ""));
        for (int i = 0; i < data.length; i++) {
            pmt_type_list.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
        for (int i = 0; i < data.length; i++) {
            period_mode.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        new_vh_class.add(new SelectItem("-1", "Select"));
        for (int i = 0; i < data.length; i++) {
            new_vh_class.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.vm_service_type.getData();
        pmt_service_type_list.add(new SelectItem("-1", "Select"));
        for (int i = 0; i < data.length; i++) {
            pmt_service_type_list.add(new SelectItem(data[i][0].trim(), data[i][1]));
        }
        setService_type((String) pmt_service_type_list.get(0).getValue());
    }

    @PostConstruct
    public void init() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected()) || CommonUtils.isNullOrBlank(sessionVariables.getEmpCodeLoggedIn())
                || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
            vahanMessages = "Session time Out";
            return;
        }
        savePayment = true;
        PermitLOIImpl loiImpl = new PermitLOIImpl();
        setNew_vh_class_menu("-1");
        setNew_o_name("");
        pmtFeeOwnerDtls = true;
        owner_dobj = new PermitOwnerDetailDobj();
        owner_dobj.setDisable(true);
        setAppl_no(appl_details.getAppl_no());
        PermitFeeImpl pmt_fee_impl = new PermitFeeImpl();
        db_offerLetterNo = pmt_fee_impl.getOffereLetterNo(appl_details.getAppl_no());
        PrintPermitDocInXhtmlImpl pmtDocImpl = new PrintPermitDocInXhtmlImpl();
        if (route_status && (appl_details.getPur_cd() == TableConstants.VM_PMT_TEMP_PUR_CD
                || appl_details.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD || appl_details.getPur_cd() == TableConstants.VM_PMT_RENEW_TEMP_PUR_CD)) {
            setRoutedata(pmtDocImpl.getRouteData(appl_details.getAppl_no(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), TableList.VA_TEMP_PERMIT_ROUTE, false));
            setRegion_covered(pmtDocImpl.getRegionDetail(appl_details.getAppl_no(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), TableList.VA_TEMP_PERMIT_ROUTE));
        } else {
            setRoutedata(pmtDocImpl.getRouteData(appl_details.getAppl_no(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), TableList.va_permit_route, otherRouteAllow));
            setRegion_covered(pmtDocImpl.getRegionDetail(appl_details.getAppl_no(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), TableList.VA_PERMIT));
        }
        if (("").equalsIgnoreCase(db_offerLetterNo) && !(loiImpl.recodeExistInVaPmtOfferApp(appl_details.getAppl_no()))) {
            get_details();
            if (("new").equalsIgnoreCase(regn_no) && !("TN,PY,UP").contains(sessionVariables.getStateCodeSelected())) {
                takeFeeDisable = true;
                vehiclePanelrender = true;
            } else {
                takeFeeDisable = false;
                vehiclePanelrender = false;
            }
            pmtFeeDialogeVisible = false;
        } else {
            pmtFeeDialogeVisible = true;
            if (("").equalsIgnoreCase(db_offerLetterNo) || loiImpl.recodeExistInVaPmtOfferApp(appl_details.getAppl_no())) {
                offerInfoMsg = "<font color=\"red\"> Your Application No. " + appl_details.getAppl_no() + " is pending for Approval </font>";
            } else {
                offerInfoMsg = "<font color=\"green\"> Your Application  No. " + appl_details.getAppl_no() + " is successfully approve by Authorised Offer </font>";
            }
        }
        if (appl_details.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                || appl_details.getPur_cd() == TableConstants.VM_PMT_TEMP_PUR_CD
                || appl_details.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD
                || appl_details.getPur_cd() == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
            revertBackRender = true;
            if (!("NEW").equalsIgnoreCase(appl_details.getRegn_no())
                    && (appl_details.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD)) {
                String msg = CommonPermitPrintImpl.getPmtFeePaidAtTymOfRegn(appl_details.getCurrent_state_cd(), appl_details.getRegn_no(), appl_details.getAppl_no());
                if (!CommonUtils.isNullOrBlank(msg) && !("KL,RJ").contains(sessionVariables.getStateCodeSelected())) {
                    pmtFeePaidAtTymOfRegnVisible = true;
                    pmtFeePaidAtTymOfRegnLable = msg;
                    PrimeFaces.current().ajax().update("pmtFeePaid");
                    //       PrimeFaces.current().ajax().update("pmtFeePaidAtRegn");
                }
            }
        }
        if (regn_no.equalsIgnoreCase("NEW") && appl_details.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD && !("TN,PY,UP").contains(sessionVariables.getStateCodeSelected())) {
            showOffLtOption = true;
        }

        try {
            TmConfigurationDobj tmConf = Util.getTmConfiguration();
            if (tmConf != null && tmConf.isOnlinePayment()) {
                boolean onlineData = new FeeImpl().getOnlinePayData(appl_details.getAppl_no());
                if (onlineData) {
                    Object[] obj = new FeeImpl().getUserIDAndPassword(appl_details.getAppl_no());
                    if (obj != null && obj.length > 0) {
                        onlinePaymentMsg = "Online Payment Credentials User ID is : " + appl_no + " & Password : " + obj[0];
                        savePayment = false;
                        revertBackRender = false;
                        cancelOnlinePayment = true;
                    } else {
                        onlinePaymentRender = true;
                    }
                } else {
                    onlinePaymentRender = true;
                }
            }
        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        PrimeFaces.current().ajax().update("workbench_tabview:payAndFeePanel");

    }

    public void checkOffNumber() {
        String offerLetterDocId = "3";
        try {
            boolean offerLetterPrint = CommonPermitPrintImpl.checkPermitPrintPending(appl_details.getAppl_no(), offerLetterDocId);
            if (offerLetterPrint) {
                JSFUtils.showMessagesInDialog("Message", "Please print the offer letter first.", FacesMessage.SEVERITY_ERROR);
            } else if (CommonUtils.isNullOrBlank(offLetterNo)) {
                JSFUtils.showMessagesInDialog("Error", "Please Fill the LOI/Offer no.", FacesMessage.SEVERITY_ERROR);
            } else if (db_offerLetterNo.equalsIgnoreCase(offLetterNo)) {
                PrimeFaces.current().executeScript("PF('offNo').hide();");
                get_details();
                if (("new").equalsIgnoreCase(regn_no) && !("TN,PY").contains(sessionVariables.getStateCodeSelected())) {
                    takeFeeDisable = true;
                    vehiclePanelrender = true;
                } else {
                    takeFeeDisable = false;
                    vehiclePanelrender = false;
                }
            } else {
                JSFUtils.showMessagesInDialog("Error", "Please enter the correct LOI/Offer no.", FacesMessage.SEVERITY_ERROR);
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Message", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Message", TableConstants.SomthingWentWrong, FacesMessage.SEVERITY_ERROR);
        }
    }

    public String skipFee() {
        PermitFeeImpl feeImpl = new PermitFeeImpl();
        String values = null;
        try {
            PermitFeeDobj dobj = new PermitFeeDobj();
            dobj.setRegn_no(appl_details.getRegn_no().toUpperCase());
            dobj.setAppl_no(getAppl_no().toString());
            dobj.setPermit_type(getPermit_type().toString());
            if (!CommonUtils.isNullOrBlank(getPermit_domain())) {
                dobj.setPermit_domain(Integer.parseInt(getPermit_domain()));
            }
            if (!CommonUtils.isNullOrBlank(getPermit_catg())) {
                dobj.setPermit_catg(Integer.parseInt(getPermit_catg()));
            }
            dobj.setPur_cd(String.valueOf(appl_details.getPur_cd()));
            dobj.setFlag("1");
            dobj.setState_cd(sessionVariables.getStateCodeSelected());
            if (feeImpl.checkPmtissuedAfterFeePaidAtNewRegn(appl_details.getRegn_no().toUpperCase())) {
                showApplPendingDialogBox("Fee can not be skipped. Permit is already issued after new registration.");
                return null;
                //  throw new VahanException("Fee can not be skipped. Permit is already issued after new registration.");
            }
            feeImpl.feeSkip(dobj, showFeeBean.getTotalAmt());
            values = "home";
        } catch (VahanException e) {
            values = e.getMessage();
            showApplPendingDialogBox(values);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "- regn_no : " + appl_details.getRegn_no() + " appl_no : " + getAppl_no() + " pmt_domain : " + getPermit_domain() + " pmt_catg : " + getPermit_catg() + e.getStackTrace()[0]);
            showApplPendingDialogBox(TableConstants.SomthingWentWrong);
        }
        return values;
    }

    public void check_regn_no() {
        PermitFeeImpl pmt_fee_impl = new PermitFeeImpl();
        PermitFeeDobj pmt_fee_dobj = null;
        try {
            String pendingApplication = ServerUtil.applicationStatusForPermit(getNew_regn_no().toUpperCase(), sessionVariables.getStateCodeSelected());
            String value = null;
            try {
                value = CommonPermitPrintImpl.getRegnNoinVtPermit(getNew_regn_no().toUpperCase(), Util.getUserStateCode());
            } catch (VahanException e) {
                value = "";
            }
            if (CommonUtils.isNullOrBlank(value) && CommonUtils.isNullOrBlank(pendingApplication)) {
                pmt_fee_dobj = pmt_fee_impl.get_regnNo_details(getNew_regn_no().toUpperCase(), appl_details.getCurrent_action_cd());
                if (pmt_fee_dobj != null) {
                    if (((ownerBean.getVh_class().equalsIgnoreCase(pmt_fee_dobj.getNew_vh_class())))
                            && (ownerBean.getOwner_name().trim().equalsIgnoreCase(pmt_fee_dobj.getNew_o_name().trim()))) {
                        setNew_f_name((pmt_fee_dobj.getNew_f_name()).toUpperCase());
                        setNew_o_name((pmt_fee_dobj.getNew_o_name()).toUpperCase());
                        setNew_vh_class_menu(pmt_fee_dobj.getNew_vh_class());
                        setNew_regn_dt(pmt_fee_dobj.getRegn_dt());
                        if (regn_no.equalsIgnoreCase("NEW") && appl_details.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD && sessionVariables.getStateCodeSelected().equalsIgnoreCase("WB")) {
                            showFeeImpl = new PermitShowFeeDetailImpl();
                            requiredDataDobj = showFeeImpl.getVaPermitDetailsAndVtOwnerDetail(appl_details.getAppl_no(), appl_details.getPur_cd(), getNew_regn_no().toUpperCase());
                            if (requiredDataDobj == null) {
                                JSFUtils.showMessagesInDialog("Information", "Owner and Permit Details not found.", FacesMessage.SEVERITY_INFO);
                                return;
                            }
                            showFeeBean.getFeeShowPanal().clear();
                            showFeeBean.setFeeShowPanal(showFeeBean.showFeeDetails(requiredDataDobj, showFeeBean.getFeeShowPanal()));
                            showFeeBean.setPermitFee(0);
                            showFeeBean.setPermitFine(0);
                            showFeeBean.setTotalAmt(0);
                            for (int i = 0; i < showFeeBean.getFeeShowPanal().size(); i++) {
                                showFeeBean.setPermitFee(showFeeBean.getPermitFee() + Integer.valueOf(showFeeBean.getFeeShowPanal().get(i).getPermitAmt()));
                                showFeeBean.setPermitFine(showFeeBean.getPermitFine() + Integer.valueOf(showFeeBean.getFeeShowPanal().get(i).getPenalty()));
                                feeCollectionSize++;
                            }
                            showFeeBean.updateTotalPayableAmount(requiredDataDobj.getVh_class(), feeCollectionSize, appl_details.getRegn_no(), appl_details.getPur_cd(), requiredDataDobj.getPmt_type(), false);
                        }
                        pmtCheckDtsl.getAlldetails(getNew_regn_no().toUpperCase(), null, sessionVariables.getStateCodeSelected(), 0);
                        if (Boolean.parseBoolean(confige.get("exempt_echallan"))) {
                            pmtCheckDtsl.getDtlsDobj().setExemptedChallan(true);
                        }
                        String msg = CommonPermitPrintImpl.checkPmtValidation(pmtCheckDtsl.getDtlsDobj());
                        if (CommonUtils.isNullOrBlank(msg)) {
                            takeFeeDisable = false;
                        } else {
                            takeFeeDisable = true;
                            JSFUtils.showMessagesInDialog("Information", msg, FacesMessage.SEVERITY_INFO);

                        }
                    } else {
                        takeFeeDisable = true;
                        JSFUtils.showMessagesInDialog("Information", "Owner Name/Vehicle Class dose not match.", FacesMessage.SEVERITY_INFO);
                    }
                } else {
                    setNew_f_name("");
                    setNew_o_name("");
                    setNew_regn_dt("");
                    setNew_vh_class_menu("-1");
                    takeFeeDisable = true;
                    JSFUtils.showMessagesInDialog("In-Valid Vehicle", "This Registration No is not valid ", FacesMessage.SEVERITY_ERROR);
                }
            } else if (!CommonUtils.isNullOrBlank(value)) {
                takeFeeDisable = true;
                JSFUtils.showMessagesInDialog("Infoemation", "Permit is allready granted.", FacesMessage.SEVERITY_INFO);
            } else if (!CommonUtils.isNullOrBlank(pendingApplication)) {
                takeFeeDisable = true;
                showApplPendingDialogBox(pendingApplication);
            }
        } catch (Exception e) {
            takeFeeDisable = true;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void showApplPendingDialogBox(String pendingApplication) {
        setApp_no_msg(pendingApplication);
        PrimeFaces.current().ajax().update("app_num_id");
        PrimeFaces.current().executeScript("PF('appNum').show();");
    }

    public void get_details() {
        try {
            PermitFeeDobj permit_dobj = new PermitFeeDobj();
            ownerImpl = new PermitOwnerDetailImpl();
            owner_dobj = new PermitOwnerDetailDobj();
            PermitFeeImpl pmt_impl = new PermitFeeImpl();
            if (appl_details.getPur_cd() == TableConstants.VM_PMT_DUPLICATE_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_SURRENDER_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_TRANSFER_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_CA_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_CANCELATION_PUR_CD) {
                ownerBean.setValueinDOBJ(ownerImpl.setVtOwnerDtlsOnlyDisplay(appl_details.getRegn_no().toUpperCase(), sessionVariables.getStateCodeSelected()));
                permit_dobj = pmt_impl.getDetaliINVtPermit(appl_details.getRegn_no().toUpperCase(), appl_details.getAppl_no(), sessionVariables.getStateCodeSelected());
                if (permit_dobj != null) {
                    setPmt_no(permit_dobj.getPermit_no());
                    setPmt_valid_from(permit_dobj.getPermit_valid_upto());
                }
                renderPermit_dtls = false;
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD
                    || (appl_details.getPur_cd() == TableConstants.VM_PMT_RESTORE_PUR_CD
                    && ("KA").contains(sessionVariables.getStateCodeSelected()))) {
                if (appl_details.getPur_cd() == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD) {
                    SurrenderPermitImpl surrImpl = new SurrenderPermitImpl();
                    ownerBean.setValueinDOBJ(ownerImpl.set_Owner_appl_db_to_dobj(surrImpl.getReAssigmentOldToNew(appl_details.getRegn_no().toUpperCase()), null));
                } else {
                    ownerBean.setValueinDOBJ(ownerImpl.setVtOwnerDtlsOnlyDisplay(appl_details.getRegn_no().toUpperCase(), sessionVariables.getStateCodeSelected()));
                }
                String registrationNo = appl_details.getRegn_no().toUpperCase();
                if (("UK").contains(sessionVariables.getStateCodeSelected())
                        && appl_details.getPur_cd() == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD) {
                    registrationNo = pmt_impl.getRegnNoForReplacement(appl_details.getRegn_no());
                }
                permit_dobj = pmt_impl.getDetaliINVhPermit(registrationNo);
                permit_dobj.setRegn_no(appl_details.getRegn_no().toUpperCase());
                setPmt_no(permit_dobj.getPermit_no());
                setPmt_valid_from(permit_dobj.getPermit_valid_upto());
                renderPermit_dtls = false;
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD) {
                ownerBean.setValueinDOBJ(ownerImpl.set_Owner_appl_db_to_dobj(appl_details.getRegn_no().toUpperCase(), null));
                permit_dobj = pmt_impl.getDetaliINVhPermit(appl_details.getRegn_no().toUpperCase());
                permit_dobj.setRegn_no(appl_details.getRegn_no().toUpperCase());
                setPmt_no(permit_dobj.getPermit_no());
                setPmt_valid_from(permit_dobj.getPermit_valid_upto());
                renderPermit_dtls = false;
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_TEMP_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_RENEW_TEMP_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                renderPermit_dtls = true;
                ownerBean.setValueinDOBJ(ownerImpl.set_Owner_appl_db_to_dobj(appl_details.getRegn_no().toUpperCase(), null));
                permit_dobj = pmt_impl.getDetailsOfTempPermit(appl_details.getAppl_no().toUpperCase());
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD) {
                renderPermit_dtls = false;
                ownerBean.setValueinDOBJ(ownerImpl.set_Owner_appl_db_to_dobj(appl_details.getRegn_no().toUpperCase(), null));
                permit_dobj = pmt_impl.getDetaliINVtPermit(appl_details.getRegn_no().toUpperCase(), appl_details.getAppl_no(), sessionVariables.getStateCodeSelected());
                setPmt_no(permit_dobj.getPermit_no());
                setPmt_valid_from(permit_dobj.getPermit_valid_upto());
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    && appl_details.getRegn_no().equalsIgnoreCase("NEW")) {
                renderPermit_dtls = true;
                ownerBean.setValueinDOBJ(ownerImpl.set_VA_Owner_permit_to_dobj(appl_details.getAppl_no(), appl_details.getRegn_no()));
                permit_dobj = pmt_impl.getRegnNO(getAppl_no());
                pmtSubType = CommonPermitPrintImpl.getPmtSubType(getAppl_no());
                pmtSubTypeRender = true;
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_RENEW_COUNTER_SIGNATURE_PUR_CD) {
                renderPermit_dtls = true;
                ownerBean.setValueinDOBJ(ownerImpl.ownerDeatilsCounterSig(appl_details.getAppl_no()));
                permit_dobj = pmt_impl.getDetailsCounterSignature(appl_details.getAppl_no());
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_DUP_COUNTER_SIGNATURE_PUR_CD) {
                renderPermit_dtls = true;
                ownerBean.setValueinDOBJ(ownerImpl.ownerDeatilsCounterSigFromVt(appl_details.getRegn_no().toUpperCase()));
                permit_dobj = pmt_impl.getDetailsCounterSignatureFromVt(appl_details.getRegn_no().toUpperCase());
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_LEASE_PUR_CD) {
                renderPermit_dtls = true;
                ownerBean.setValueinDOBJ(ownerImpl.leaseOwnerDeatilsPermit(appl_details.getAppl_no()));
                permit_dobj = pmt_impl.getDetailsLeasePermit(appl_details.getAppl_no());
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_COUNTER_SIGNATURE_AUTH_PUR_CD) {
                renderPermit_dtls = true;
                ownerBean.setValueinDOBJ(ownerImpl.set_Owner_appl_db_to_dobj(appl_details.getRegn_no().toUpperCase(), null));
                permit_dobj = pmt_impl.getDetailsCounterSignatureAuth(appl_details.getAppl_no());
            } else {
                renderPermit_dtls = true;
                ownerBean.setValueinDOBJ(ownerImpl.set_VA_Owner_permit_to_dobj(appl_details.getAppl_no(), appl_details.getRegn_no()));
                permit_dobj = pmt_impl.getRegnNO(getAppl_no());
                pmtSubType = CommonPermitPrintImpl.getPmtSubType(getAppl_no());
                pmtSubTypeRender = true;
            }
            setPermit_type(permit_dobj.getPermit_type().trim());
            setPermit_domain(String.valueOf(permit_dobj.getPermit_domain()));
            String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
            pmt_catg_list.clear();
            for (int j = 0; j < data.length; j++) {
                if (data[j][0].equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                        && Integer.parseInt(data[j][3]) == Integer.parseInt(getPermit_type())) {
                    pmt_catg_list.add(new SelectItem(data[j][1], data[j][2]));
                }
            }
            setPermit_catg(String.valueOf(permit_dobj.getPermit_catg()));
            setService_type(String.valueOf(permit_dobj.getService_type()));
            setFee_desc1(permit_dobj.getPur_cd());
            setRegn_no(permit_dobj.getRegn_no());
            setPer_mode_sel(permit_dobj.getPeriod_mode());
            setPeriod(permit_dobj.getPeriod());
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void pemitCatgList() {
        pmt_catg_list.clear();
        try {
            String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
            for (int j = 0; j < data.length; j++) {
                if (data[j][0].equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                        && Integer.parseInt(data[j][3]) == Integer.parseInt(getPermit_type())) {
                    pmt_catg_list.add(new SelectItem(data[j][1], data[j][2]));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void validateForm() {
        boolean allGood = false;
        if (!getPaymentBean().getPayment_mode().equals("C")) {
            otherthanCashMode = true;
            for (PaymentCollectionDobj payDobj : getPaymentBean().getPaymentlist()) {
                if (!(allGood = payDobj.validateDobj())) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, payDobj.getValidationMessage(), payDobj.getValidationMessage());
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    allGood = true;
                    break;
                } else {
                    allGood = false;
                }
            }
        } else {
            otherthanCashMode = false;
        }

        if (getPaymentBean().getPayment_mode().equals("C") && isCashModeDisabled()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Payment in Cash mode is not accepted.", "Payment in Cash mode is not accepted.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            allGood = true;
        }

        if (!calculateBalanceAmount(Long.valueOf(showFeeBean.getTotalAmt()))) {
            return;
        }

        if (!allGood) {
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("popup");
            PrimeFaces.current().executeScript("PF('confDlgFee').show()");
        }
    }

    public void validateFormOnlinePayment() {
        boolean allGood = false;
        if (!getPaymentBean().getPayment_mode().equals("C")) {
            for (PaymentCollectionDobj payDobj : getPaymentBean().getPaymentlist()) {
                if (!(allGood = payDobj.validateDobj())) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, payDobj.getValidationMessage(), payDobj.getValidationMessage());
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    allGood = true;
                    break;
                } else {
                    allGood = false;
                }
            }
        }
        calculateBalanceAmount(Long.valueOf(showFeeBean.getTotalAmt()));
        if (!allGood) {
            if (getPaymentBean().getPayment_mode().equals("C")) {
                onlinePayment = true;
            }
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("popup");
            PrimeFaces.current().executeScript("PF('confDlgFee').show()");
        }
    }

    public boolean calculateBalanceAmount(long totalPayableAmount) {
        long totalInstrumentAmt = 0;
        boolean allGood = true;
        for (PaymentCollectionDobj dobj : getPaymentBean().getPaymentlist()) {
            if (dobj.getAmount() != null) {
                totalInstrumentAmt = totalInstrumentAmt + Long.parseLong(dobj.getAmount());
            }
        }
        if ((totalPayableAmount - totalInstrumentAmt) < 0) {
            getPaymentBean().setBalanceAmount(0);
        } else {
            getPaymentBean().setBalanceAmount((totalPayableAmount) - totalInstrumentAmt);
        }
        getPaymentBean().setAmountInCash(totalInstrumentAmt);

        if (getPaymentBean() != null && !getPaymentBean().getPayment_mode().equals("C")) {
            if (totalInstrumentAmt > totalPayableAmount) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Excess Total Instrument Amount (Rs." + (totalInstrumentAmt - totalPayableAmount) + "/- must be adjusted in any transaction head amount."));
                return false;
            } else if ("DL,SK".contains(sessionVariables.getStateCodeSelected())) {
                if (totalPayableAmount != totalInstrumentAmt) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Paying total Instrument Amount Must be equal to Total Payable Amount"));
                    return false;
                }
            }
        } else {
            if ("SK".contains(sessionVariables.getStateCodeSelected())) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please select Mixed Mode for payment. Because Cash payment mode in not allowed in Sikkim"));
                allGood = true;
                return false;
            }
        }
        return allGood;
    }

    public String saveFeeDetails() {
        int paymentProcessAmount = 0;
        int total_amount = 0;
        FeeDraftDobj feeDraftDobj = null;
        String generatedRcpt = "", userPwd = "";
        PermitFeeImpl pmtfeeimpl = new PermitFeeImpl();
        PermitFeeDobj dobj = new PermitFeeDobj();

        dobj.setRegn_no(appl_details.getRegn_no().toUpperCase());
        if (("new").equalsIgnoreCase(appl_details.getRegn_no().toUpperCase())) {
            dobj.setNew_regn_no(getNew_regn_no().toUpperCase());
        }
        dobj.setAppl_no(getAppl_no().toString());
        dobj.setPermit_type(getPermit_type().toString());
        if (!CommonUtils.isNullOrBlank(getPermit_domain())) {
            dobj.setPermit_domain(Integer.parseInt(getPermit_domain()));
        } else {
            dobj.setPermit_domain(0);
        }
        if (!CommonUtils.isNullOrBlank(getPermit_catg())) {
            dobj.setPermit_catg(Integer.parseInt(getPermit_catg()));
        } else {
            dobj.setPermit_catg(-1);
        }
        dobj.setPur_cd(String.valueOf(appl_details.getPur_cd()));
        dobj.setFlag("1");
        CommonPermitFeeDobj pmt_fee_dobj = new CommonPermitFeeDobj();
        pmt_fee_dobj.setRegn_no(getRegn_no());
        if (("new").equalsIgnoreCase(appl_details.getRegn_no().toUpperCase())) {
            pmt_fee_dobj.setRegn_no(getNew_regn_no().toUpperCase());
        }
        pmt_fee_dobj.setPayment_mode("C");
        if (showFeeBean.isRenderUserChargesAmountPanel()) {
            PermitShowFeeDetailDobj userCharge = new PermitShowFeeDetailDobj();
            userCharge.setPermitAmt(String.valueOf(showFeeBean.getExtraChrg()));
            userCharge.setPenalty(String.valueOf(0));
            userCharge.setPurCd("99");
            showFeeBean.getFeeShowPanal().add(userCharge);
        }
        pmt_fee_dobj.setListPmtFeeDetails(showFeeBean.getFeeShowPanal());
        pmt_fee_dobj.setFlag("");
        pmt_fee_dobj.setCollected_by(sessionVariables.getEmpCodeLoggedIn());
        pmt_fee_dobj.setState_cd(sessionVariables.getStateCodeSelected());
        pmt_fee_dobj.setOff_cd(String.valueOf(sessionVariables.getOffCodeSelected()));
        pmt_fee_dobj.setAppl_no(getAppl_no());
        if (("new").equalsIgnoreCase(appl_details.getRegn_no().toUpperCase()) && !CommonUtils.isNullOrBlank(getNew_regn_no())) {
            String[] newVehicalDetail = pmtfeeimpl.getNewOwnerDetails(getNew_regn_no().toUpperCase(), sessionVariables.getStateCodeSelected());
            if (CommonUtils.isNullOrBlank(newVehicalDetail[0])) {
                pmt_fee_dobj.setOwner_name(ownerBean.getOwner_name());
            } else {
                pmt_fee_dobj.setOwner_name(newVehicalDetail[0]);
            }
            if (CommonUtils.isNullOrBlank(newVehicalDetail[1])) {
                pmt_fee_dobj.setChasi_no(ownerBean.getChasi_no());
            } else {
                pmt_fee_dobj.setChasi_no(newVehicalDetail[1]);
            }
            if (CommonUtils.isNullOrBlank(newVehicalDetail[2])) {
                pmt_fee_dobj.setVh_class(ownerBean.getVh_class());
            } else {
                pmt_fee_dobj.setVh_class(newVehicalDetail[2]);
            }
        } else {
            pmt_fee_dobj.setOwner_name(ownerBean.getOwner_name());
            pmt_fee_dobj.setChasi_no(ownerBean.getChasi_no());
            pmt_fee_dobj.setVh_class(ownerBean.getVh_class());
        }
        try {
            if (showFeeBean.getPmtValidFrom() == null) {
                throw new VahanException("Permit valid From date is empty");
            }
            if (showFeeBean.getPmtValidUpto() == null) {
                throw new VahanException("Permit from From date is empty");
            }
            if (showFeeBean.getPmtValidFrom().compareTo(showFeeBean.getPmtValidUpto()) > 0) {
                throw new VahanException("Valid From date is greater than Valid upto date. \n Valid from : " + CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(showFeeBean.getPmtValidFrom()) + " and Valid from : " + CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(showFeeBean.getPmtValidUpto()));
            }
            if (getPaymentBean().getPayment_mode().equals("-1")) {
                JSFUtils.setFacesMessage("Please Select Fee", "Please Select Fee", JSFUtils.ERROR);
                return "";
            }
            if (showFeeBean.getFeeShowPanal() != null && showFeeBean.getFeeShowPanal().size() > 0) {
                for (PermitShowFeeDetailDobj Feedobj : showFeeBean.getFeeShowPanal()) {
                    if (!CommonUtils.isNullOrBlank(Feedobj.getPermitAmt())) {
                        total_amount += Integer.parseInt(Feedobj.getPermitAmt());
                    }
                    if (!Feedobj.getPurCd().equalsIgnoreCase(String.valueOf(TableConstants.FEE_FINE_EXEMTION)) && !Feedobj.getPurCd().equalsIgnoreCase(String.valueOf(TableConstants.VM_MAST_MANUAL_RECEIPT)) && !CommonUtils.isNullOrBlank(Feedobj.getPermitAmt()) && Integer.parseInt(Feedobj.getPermitAmt()) < 0) {
                        throw new VahanException("Permit Fee can not be paid due to negative Amount");
                    }
                }
            }
            if (showFeeBean.getFeeShowPanal() != null && showFeeBean.getFeeShowPanal().size() > 0) {
                if (total_amount < 0) {
                    throw new VahanException("Permit Fee can not be paid due to negative Amount");
                }
            }

            if (!Utility.isNullOrBlank(getPaymentBean().getPayment_mode()) && (!getPaymentBean().getPayment_mode().equals("-1"))
                    && (!getPaymentBean().getPayment_mode().equals("C"))) {
                for (PaymentCollectionDobj payPanelDobj : getPaymentBean().getPaymentlist()) {
                    paymentProcessAmount = paymentProcessAmount + (!CommonUtils.isNullOrBlank(payPanelDobj.getAmount()) ? Integer.parseInt(payPanelDobj.getAmount()) : 0);
                }
                if (!(Utility.isNullOrBlank(getPaymentBean().getPayment_mode())
                        || "-1".equalsIgnoreCase(getPaymentBean().getPayment_mode()))) {
                    feeDraftDobj = new FeeDraftDobj();
                    feeDraftDobj.setAppl_no(this.getAppl_no());
                    feeDraftDobj.setFlag("A");
                    feeDraftDobj.setCollected_by(sessionVariables.getEmpCodeLoggedIn());
                    feeDraftDobj.setState_cd(sessionVariables.getStateCodeSelected());
                    feeDraftDobj.setOff_cd(String.valueOf(sessionVariables.getOffCodeSelected()));
                    feeDraftDobj.setDraftPaymentList(getPaymentBean().getPaymentlist());
                    pmt_fee_dobj.setPayment_mode(getPaymentBean().getPayment_mode());
                    generatedRcpt = pmtfeeimpl.saveFeeDetails(dobj, pmt_fee_dobj, feeDraftDobj, printOfferLetter);
                } else {
                    JSFUtils.setFacesMessage("Please select payment mode", "Please select payment mode", JSFUtils.WARN);
                    return "";
                }
            } else if (getPaymentBean().getPayment_mode().equals("C") && onlinePayment) {
                if (!CommonUtils.isNullOrBlank(pmt_fee_dobj.getRegn_no())) {
                    List<OwnerDetailsDobj> ownerlist = new OwnerImpl().getOwnerDetailsList(pmt_fee_dobj.getRegn_no(), null);
                    if (ownerlist != null && !ownerlist.isEmpty() && ownerlist.size() > 1) {
                        throw new VahanException("Duplicate vehicle details are available against the Registration no " + ownerlist.get(0).getRegn_no() + ". Please do de-duplicate first then initiate for online payment.");
                    }
                }
                userPwd = pmtfeeimpl.onlinePayment(dobj, pmt_fee_dobj, feeDraftDobj, printOfferLetter, ownerBean.getMobile_no());
                if (userPwd != null) {
                    userInfo = "Online Payment Credentials User ID : " + appl_no + " & Password :  " + userPwd;
                } else {
                    throw new VahanException("Error in Saving Details !!");
                }
            } else if (getPaymentBean().getPayment_mode().equals("C")) {
                generatedRcpt = pmtfeeimpl.saveFeeDetails(dobj, pmt_fee_dobj, feeDraftDobj, printOfferLetter);
            }
            if (!Utility.isNullOrBlank(generatedRcpt)) {
                rept_no_msg = "Receipt Number:" + generatedRcpt + " generated for " + getAppl_no() + " Application Number.";
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", generatedRcpt);
                return "PrintCashReceiptReport";
            } else if (!CommonUtils.isNullOrBlank(userInfo)) {
                onlinePaymentRender = false;
                savePayment = false;
                revertBackRender = false;
                cancelOnlinePayment = true;
                onlinePaymentMsg = userInfo;
            } else {
                rept_no_msg = "Fee not submitted";
            }
        } catch (VahanException e) {
            JSFUtils.setFacesMessage(e.getMessage(), "Information", JSFUtils.ERROR);
        } catch (Exception e) {
            rept_no_msg = "Fee not submitted";
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "";
//       PrimeFaces.current().executeScript("PF('dlg').show();");
//        return "seatwork";
    }

    public void checkSaveValidation() {
        if (("NEW").equalsIgnoreCase(getRegn_no())) {
            if (!CommonUtils.isNullOrBlank(getNew_regn_no())) {
                if ((ownerBean.getVh_class().equalsIgnoreCase(getNew_vh_class_menu()))
                        || (ownerBean.getOwner_name().equalsIgnoreCase(getNew_o_name()))) {
                } else {
                    JSFUtils.showMessagesInDialog("Infoemation", "Owner Name/Vehicle Class dose not match.", FacesMessage.SEVERITY_INFO);
                }
            } else {
                JSFUtils.showMessagesInDialog("Empry Registration No ", "Please provide the vehicle no", FacesMessage.SEVERITY_ERROR);
            }
        }
    }

    public String reverBackForRectification() {
        Status_dobj status_dobj = new Status_dobj();
        int prevAcCode = 0;
        Exception ex = null;
        String return_path = "";
        try {
            prevAcCode = ServerUtil.getPreviousActionCode(appl_details.getCurrent_action_cd(), appl_details.getPur_cd(), appl_details.getAppl_no(), null);
            status_dobj.setPrev_action_cd_selected(prevAcCode);
            status_dobj.setAppl_no(appl_details.getAppl_no());
            status_dobj.setPur_cd(appl_details.getPur_cd());
            status_dobj.setStatus(TableConstants.STATUS_REVERT);
            status_dobj.setSeat_cd(TableConstants.STATUS_REVERT);
            FeeImpl.revertBackForRectification(status_dobj);
        } catch (Exception e) {
            ex = e;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        if (ex != null) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", "Can't RollBack For " + appl_no + ",Please contact Administrator!");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        if (ex == null) {
            return_path = "home";
        }
        return return_path;
    }

    public String cancelPayment() {
        boolean flag = false;
        String password = "";
        long user_cd = 0;
        try {
            Object[] obj = new FeeImpl().getUserIDAndPassword(appl_details.getAppl_no());
            if (obj != null && obj.length > 0) {
                password = (String) obj[0];
                user_cd = (long) obj[1];
                if (CommonUtils.isNullOrBlank(new OnlinePaymentImpl().getTransactionNumber(appl_details.getAppl_no()))) {
                    flag = new OnlinePaymentImpl().getPaymentRevertBack(user_cd + "", appl_details.getAppl_no(), null);
                    if (flag) {
                        savePayment = true;
                        revertBackRender = true;
                        onlinePayment = true;
                        cancelOnlinePayment = false;
                        new PermitFeeImpl().cancelFineExemPayment(user_cd + "", appl_details.getAppl_no());
                    }
                } else {
                    throw new VahanException("Payment has been initiated, you can not Cancel Online Payment");
                }
            }
        } catch (VahanException e) {
            userInfo = e.getMessage();
            return userInfo;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            return TableConstants.SomthingWentWrong;
        }
        return "home";
    }

    /**
     * @return the lblpermit_no
     */
    public String getLblpermit_no() {
        return lblpermit_no;
    }

    /**
     * @param lblpermit_no the lblpermit_no to set
     */
    public void setLblpermit_no(String lblpermit_no) {
        this.lblpermit_no = lblpermit_no;
    }

    /**
     * @return the lblregn_no
     */
    public String getLblregn_no() {
        return lblregn_no;
    }

    /**
     * @param lblregn_no the lblregn_no to set
     */
    public void setLblregn_no(String lblregn_no) {
        this.lblregn_no = lblregn_no;
    }

    /**
     * @return the permit_no
     */
    public String getPermit_no() {
        return permit_no;
    }

    /**
     * @param permit_no the permit_no to set
     */
    public void setPermit_no(String permit_no) {
        this.permit_no = permit_no;
    }

    /**
     * @return the state_cd
     */
    public String getState_cd() {
        return state_cd;
    }

    /**
     * @param state_cd the state_cd to set
     */
    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
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
     * @return the regn_no2
     */
    public String getRegn_no2() {
        return regn_no2;
    }

    /**
     * @param regn_no2 the regn_no2 to set
     */
    public void setRegn_no2(String regn_no2) {
        this.regn_no2 = regn_no2;
    }

    /**
     * @return the regn_dt
     */
    public String getRegn_dt() {
        return regn_dt;
    }

    /**
     * @param regn_dt the regn_dt to set
     */
    public void setRegn_dt(String regn_dt) {
        this.regn_dt = regn_dt;
    }

    /**
     * @return the owner_name
     */
    public String getOwner_name() {
        return owner_name;
    }

    /**
     * @param owner_name the owner_name to set
     */
    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    /**
     * @return the f_name
     */
    public String getF_name() {
        return f_name;
    }

    /**
     * @param f_name the f_name to set
     */
    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    /**
     * @return the regn_type
     */
    public String getRegn_type() {
        return regn_type;
    }

    /**
     * @param regn_type the regn_type to set
     */
    public void setRegn_type(String regn_type) {
        this.regn_type = regn_type;
    }

    /**
     * @return the vh_class
     */
    public String getVh_class() {
        return vh_class;
    }

    /**
     * @param vh_class the vh_class to set
     */
    public void setVh_class(String vh_class) {
        this.vh_class = vh_class;
    }

    /**
     * @return the chasi_no
     */
    public String getChasi_no() {
        return chasi_no;
    }

    /**
     * @param chasi_no the chasi_no to set
     */
    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
    }

    /**
     * @return the seat_cap
     */
    public String getSeat_cap() {
        return seat_cap;
    }

    /**
     * @param seat_cap the seat_cap to set
     */
    public void setSeat_cap(String seat_cap) {
        this.seat_cap = seat_cap;
    }

    /**
     * @return the stand_cap
     */
    public String getStand_cap() {
        return stand_cap;
    }

    /**
     * @param stand_cap the stand_cap to set
     */
    public void setStand_cap(String stand_cap) {
        this.stand_cap = stand_cap;
    }

    /**
     * @return the unld_wt
     */
    public String getUnld_wt() {
        return unld_wt;
    }

    /**
     * @param unld_wt the unld_wt to set
     */
    public void setUnld_wt(String unld_wt) {
        this.unld_wt = unld_wt;
    }

    /**
     * @return the ld_wt
     */
    public String getLd_wt() {
        return ld_wt;
    }

    /**
     * @param ld_wt the ld_wt to set
     */
    public void setLd_wt(String ld_wt) {
        this.ld_wt = ld_wt;
    }

    /**
     * @return the fuel
     */
    public String getFuel() {
        return fuel;
    }

    /**
     * @param fuel the fuel to set
     */
    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    /**
     * @return the op_dt
     */
    public String getOp_dt() {
        return op_dt;
    }

    /**
     * @param op_dt the op_dt to set
     */
    public void setOp_dt(String op_dt) {
        this.op_dt = op_dt;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return the norms
     */
    public String getNorms() {
        return norms;
    }

    /**
     * @param norms the norms to set
     */
    public void setNorms(String norms) {
        this.norms = norms;
    }

    /**
     * @return the pmt_type_list
     */
    public List getPmt_type_list() {
        return pmt_type_list;
    }

    /**
     * @param pmt_type_list the pmt_type_list to set
     */
    public void setPmt_type_list(List pmt_type_list) {
        this.pmt_type_list = pmt_type_list;
    }

    /**
     * @return the permit_type
     */
    public String getPermit_type() {
        return permit_type;
    }

    /**
     * @param permit_type the permit_type to set
     */
    public void setPermit_type(String permit_type) {
        this.permit_type = permit_type;
    }

    /**
     * @return the permit_domain
     */
    public String getPermit_domain() {
        return permit_domain;
    }

    /**
     * @param permit_domain the permit_domain to set
     */
    public void setPermit_domain(String permit_domain) {
        this.permit_domain = permit_domain;
    }

    /**
     * @return the pmt_domain_list
     */
    public List getPmt_domain_list() {
        return pmt_domain_list;
    }

    /**
     * @param pmt_domain_list the pmt_domain_list to set
     */
    public void setPmt_domain_list(List pmt_domain_list) {
        this.pmt_domain_list = pmt_domain_list;
    }

    /**
     * @return the permit_catg
     */
    public String getPermit_catg() {
        return permit_catg;
    }

    /**
     * @param permit_catg the permit_catg to set
     */
    public void setPermit_catg(String permit_catg) {
        this.permit_catg = permit_catg;
    }

    /**
     * @return the pmt_catg_list
     */
    public List getPmt_catg_list() {
        return pmt_catg_list;
    }

    /**
     * @param pmt_catg_list the pmt_catg_list to set
     */
    public void setPmt_catg_list(List pmt_catg_list) {
        this.pmt_catg_list = pmt_catg_list;
    }

    /**
     * @return the service_type
     */
    public String getService_type() {
        return service_type;
    }

    /**
     * @param service_type the service_type to set
     */
    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    /**
     * @return the pmt_service_type_list
     */
    public List getPmt_service_type_list() {
        return pmt_service_type_list;
    }

    /**
     * @param pmt_service_type_list the pmt_service_type_list to set
     */
    public void setPmt_service_type_list(List pmt_service_type_list) {
        this.pmt_service_type_list = pmt_service_type_list;
    }

    /**
     * @return the fee_desc1
     */
    public String getFee_desc1() {
        return fee_desc1;
    }

    /**
     * @param fee_desc1 the fee_desc1 to set
     */
    public void setFee_desc1(String fee_desc1) {
        this.fee_desc1 = fee_desc1;
    }

    /**
     * @return the pmt_fee_desc1_list
     */
    public List getPmt_fee_desc1_list() {
        return pmt_fee_desc1_list;
    }

    /**
     * @param pmt_fee_desc1_list the pmt_fee_desc1_list to set
     */
    public void setPmt_fee_desc1_list(List pmt_fee_desc1_list) {
        this.pmt_fee_desc1_list = pmt_fee_desc1_list;
    }

    /**
     * @return the fee_desc2
     */
    public String getFee_desc2() {
        return fee_desc2;
    }

    /**
     * @param fee_desc2 the fee_desc2 to set
     */
    public void setFee_desc2(String fee_desc2) {
        this.fee_desc2 = fee_desc2;
    }

    /**
     * @return the pmt_fee_desc2_list
     */
    public List getPmt_fee_desc2_list() {
        return pmt_fee_desc2_list;
    }

    /**
     * @param pmt_fee_desc2_list the pmt_fee_desc2_list to set
     */
    public void setPmt_fee_desc2_list(List pmt_fee_desc2_list) {
        this.pmt_fee_desc2_list = pmt_fee_desc2_list;
    }

    public boolean isTakeFeeDisable() {
        return takeFeeDisable;
    }

    public void setTakeFeeDisable(boolean takeFeeDisable) {
        this.takeFeeDisable = takeFeeDisable;
    }

    /**
     * @return the radioValue
     */
    public String getRadioValue() {
        return radioValue;
    }

    /**
     * @param radioValue the radioValue to set
     */
    public void setRadioValue(String radioValue) {
        this.radioValue = radioValue;
    }

    public boolean isRenderPermit_dtls() {
        return renderPermit_dtls;
    }

    public void setRenderPermit_dtls(boolean renderPermit_dtls) {
        this.renderPermit_dtls = renderPermit_dtls;
    }

    public boolean isVehiclePanelrender() {
        return vehiclePanelrender;
    }

    public void setVehiclePanelrender(boolean vehiclePanelrender) {
        this.vehiclePanelrender = vehiclePanelrender;
    }

    /**
     * @return the valid_in_months
     */
    public String getValid_in_months() {
        return valid_in_months;
    }

    /**
     * @param valid_in_months the valid_in_months to set
     */
    public void setValid_in_months(String valid_in_months) {
        this.valid_in_months = valid_in_months;
    }

    /**
     * @return the amount1
     */
    public String getAmount1() {
        return amount1;
    }

    /**
     * @param amount1 the amount1 to set
     */
    public void setAmount1(String amount1) {
        this.amount1 = amount1;
    }

    /**
     * @return the fine1
     */
    public String getFine1() {
        return fine1;
    }

    /**
     * @param fine1 the fine1 to set
     */
    public void setFine1(String fine1) {
        this.fine1 = fine1;
    }

    /**
     * @return the total1
     */
    public String getTotal1() {
        return total1;
    }

    /**
     * @param total1 the total1 to set
     */
    public void setTotal1(String total1) {
        this.total1 = total1;
    }

    /**
     * @return the amount2
     */
    public String getAmount2() {
        return amount2;
    }

    /**
     * @param amount2 the amount2 to set
     */
    public void setAmount2(String amount2) {
        this.amount2 = amount2;
    }

    /**
     * @return the fine2
     */
    public String getFine2() {
        return fine2;
    }

    /**
     * @param fine2 the fine2 to set
     */
    public void setFine2(String fine2) {
        this.fine2 = fine2;
    }

    /**
     * @return the total2
     */
    public String getTotal2() {
        return total2;
    }

    /**
     * @param total2 the total2 to set
     */
    public void setTotal2(String total2) {
        this.total2 = total2;
    }

    /**
     * @return the grand_total
     */
    public String getGrand_total() {
        return grand_total;
    }

    /**
     * @param grand_total the grand_total to set
     */
    public void setGrand_total(String grand_total) {
        this.grand_total = grand_total;
    }

    public PaymentCollectionBean getPaymentBean() {
        return paymentBean;
    }

    public void setPaymentBean(PaymentCollectionBean paymentBean) {
        this.paymentBean = paymentBean;
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
    /**
     * @return the rept_no_msg
     */
    public String getRept_no_msg() {
        return rept_no_msg;
    }

    /**
     * @param rept_no_msg the rept_no_msg to set
     */
    public void setRept_no_msg(String rept_no_msg) {
        this.rept_no_msg = rept_no_msg;
    }

    public PermitOwnerDetailBean getOwnerBean() {
        return ownerBean;
    }

    public void setOwnerBean(PermitOwnerDetailBean ownerBean) {
        this.ownerBean = ownerBean;
    }

    public PermitOwnerDetailDobj getOwner_dobj() {
        return owner_dobj;
    }

    public void setOwner_dobj(PermitOwnerDetailDobj owner_dobj) {
        this.owner_dobj = owner_dobj;
    }

    public PermitOwnerDetailImpl getOwnerImpl() {
        return ownerImpl;
    }

    public void setOwnerImpl(PermitOwnerDetailImpl ownerImpl) {
        this.ownerImpl = ownerImpl;
    }

    public String getNew_regn_no() {
        return new_regn_no;
    }

    public void setNew_regn_no(String new_regn_no) {
        this.new_regn_no = new_regn_no;
    }

    public String getNew_f_name() {
        return new_f_name;
    }

    public void setNew_f_name(String new_f_name) {
        this.new_f_name = new_f_name;
    }

    public String getNew_o_name() {
        return new_o_name;
    }

    public void setNew_o_name(String new_o_name) {
        this.new_o_name = new_o_name;
    }

    public List getNew_vh_class() {
        return new_vh_class;
    }

    public void setNew_vh_class(List new_vh_class) {
        this.new_vh_class = new_vh_class;
    }

    public String getNew_vh_class_menu() {
        return new_vh_class_menu;
    }

    public void setNew_vh_class_menu(String new_vh_class_menu) {
        this.new_vh_class_menu = new_vh_class_menu;
    }

    public List getPeriod_mode() {
        return period_mode;
    }

    public void setPeriod_mode(List period_mode) {
        this.period_mode = period_mode;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getPer_mode_sel() {
        return per_mode_sel;
    }

    public void setPer_mode_sel(String per_mode_sel) {
        this.per_mode_sel = per_mode_sel;
    }

    public FormFeePanelBean getFeePanelBean() {
        return feePanelBean;
    }

    public void setFeePanelBean(FormFeePanelBean feePanelBean) {
        this.feePanelBean = feePanelBean;
    }

    public ReceiptMasterBean getRcpt_bean() {
        return rcpt_bean;
    }

    public void setRcpt_bean(ReceiptMasterBean rcpt_bean) {
        this.rcpt_bean = rcpt_bean;
    }

    public String getPmt_no() {
        return pmt_no;
    }

    public void setPmt_no(String pmt_no) {
        this.pmt_no = pmt_no;
    }

//    public org.primefaces.component.calendar.Calendar getPmt_valid_from() {
//        return pmt_valid_from;
//    }
//
//    public void setPmt_valid_from(org.primefaces.component.calendar.Calendar pmt_valid_from) {
//        this.pmt_valid_from = pmt_valid_from;
//    }
    public String getOffLetterNo() {
        return offLetterNo;
    }

    public void setOffLetterNo(String offLetterNo) {
        this.offLetterNo = offLetterNo;
    }

    public long getTotalAmountPayable() {
        return totalAmountPayable;
    }

    public void setTotalAmountPayable(long totalAmountPayable) {
        this.totalAmountPayable = totalAmountPayable;
    }

    public PermitShowFeeDetailBean getShowFeeBean() {
        return showFeeBean;
    }

    public void setShowFeeBean(PermitShowFeeDetailBean showFeeBean) {
        this.showFeeBean = showFeeBean;
    }

    public boolean isRevertBackRender() {
        return revertBackRender;
    }

    public void setRevertBackRender(boolean revertBackRender) {
        this.revertBackRender = revertBackRender;
    }

    public boolean isPmtFeeDialogeVisible() {
        return pmtFeeDialogeVisible;
    }

    public void setPmtFeeDialogeVisible(boolean pmtFeeDialogeVisible) {
        this.pmtFeeDialogeVisible = pmtFeeDialogeVisible;
    }

    public boolean isPmtFeeOwnerDtls() {
        return pmtFeeOwnerDtls;
    }

    public void setPmtFeeOwnerDtls(boolean pmtFeeOwnerDtls) {
        this.pmtFeeOwnerDtls = pmtFeeOwnerDtls;
    }

    public Date getPmt_valid_from() {
        return pmt_valid_from;
    }

    public void setPmt_valid_from(Date pmt_valid_from) {
        this.pmt_valid_from = pmt_valid_from;
    }

    public boolean isPrintOfferLetter() {
        return printOfferLetter;
    }

    public void setPrintOfferLetter(boolean printOfferLetter) {
        this.printOfferLetter = printOfferLetter;
    }

    public boolean isShowOffLtOption() {
        return showOffLtOption;
    }

    public void setShowOffLtOption(boolean showOffLtOption) {
        this.showOffLtOption = showOffLtOption;
    }

    public String getOfferInfoMsg() {
        return offerInfoMsg;
    }

    public void setOfferInfoMsg(String offerInfoMsg) {
        this.offerInfoMsg = offerInfoMsg;
    }

    public String getPmtSubType() {
        return pmtSubType;
    }

    public void setPmtSubType(String pmtSubType) {
        this.pmtSubType = pmtSubType;
    }

    public boolean isPmtSubTypeRender() {
        return pmtSubTypeRender;
    }

    public void setPmtSubTypeRender(boolean pmtSubTypeRender) {
        this.pmtSubTypeRender = pmtSubTypeRender;
    }

    public String getNew_regn_dt() {
        return new_regn_dt;
    }

    public void setNew_regn_dt(String new_regn_dt) {
        this.new_regn_dt = new_regn_dt;
    }

    public List<PassengerPermitDetailDobj> getRoutedata() {
        return routedata;
    }

    public void setRoutedata(List<PassengerPermitDetailDobj> routedata) {
        this.routedata = routedata;
    }

    public String getRegion_covered() {
        return region_covered;
    }

    public void setRegion_covered(String region_covered) {
        this.region_covered = region_covered;
    }

    public String getApp_no_msg() {
        return app_no_msg;
    }

    public void setApp_no_msg(String app_no_msg) {
        this.app_no_msg = app_no_msg;
    }

    public boolean isOnlinePayment() {
        return onlinePayment;
    }

    public void setOnlinePayment(boolean onlinePayment) {
        this.onlinePayment = onlinePayment;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public boolean isSavePayment() {
        return savePayment;
    }

    public void setSavePayment(boolean savePayment) {
        this.savePayment = savePayment;
    }

    public boolean isCancelOnlinePayment() {
        return cancelOnlinePayment;
    }

    public void setCancelOnlinePayment(boolean cancelOnlinePayment) {
        this.cancelOnlinePayment = cancelOnlinePayment;
    }

    public String getOnlinePaymentMsg() {
        return onlinePaymentMsg;
    }

    public void setOnlinePaymentMsg(String onlinePaymentMsg) {
        this.onlinePaymentMsg = onlinePaymentMsg;
    }

    public boolean isOnlinePaymentRender() {
        return onlinePaymentRender;
    }

    public void setOnlinePaymentRender(boolean onlinePaymentRender) {
        this.onlinePaymentRender = onlinePaymentRender;
    }

    public PermitCheckDetailsBean getPmtCheckDtsl() {
        return pmtCheckDtsl;
    }

    public void setPmtCheckDtsl(PermitCheckDetailsBean pmtCheckDtsl) {
        this.pmtCheckDtsl = pmtCheckDtsl;
    }

    public boolean isPmtFeePaidAtTymOfRegnVisible() {
        return pmtFeePaidAtTymOfRegnVisible;
    }

    public void setPmtFeePaidAtTymOfRegnVisible(boolean pmtFeePaidAtTymOfRegnVisible) {
        this.pmtFeePaidAtTymOfRegnVisible = pmtFeePaidAtTymOfRegnVisible;
    }

    public String getPmtFeePaidAtTymOfRegnLable() {
        return pmtFeePaidAtTymOfRegnLable;
    }

    public void setPmtFeePaidAtTymOfRegnLable(String pmtFeePaidAtTymOfRegnLable) {
        this.pmtFeePaidAtTymOfRegnLable = pmtFeePaidAtTymOfRegnLable;
    }

    public String getVahanMessages() {
        return vahanMessages;
    }

    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    public boolean isOtherthanCashMode() {
        return otherthanCashMode;
    }

    public void setOtherthanCashMode(boolean otherthanCashMode) {
        this.otherthanCashMode = otherthanCashMode;
    }

    public boolean isCashModeDisabled() {
        return cashModeDisabled;
    }

    public void setCashModeDisabled(boolean cashModeDisabled) {
        this.cashModeDisabled = cashModeDisabled;
    }
}
