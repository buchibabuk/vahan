/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.dobj.permit.PermitApplicatonFeeDobj;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.permit.PermitApplicatonFeeImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableList;
import nic.vahan.form.bean.FormFeePanelBean;
import nic.vahan.form.bean.PaymentCollectionBean;
import nic.vahan.form.bean.ReceiptMasterBean;
import nic.vahan.form.dobj.permit.CommonPermitFeeDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitFeeDobj;
import nic.vahan.form.dobj.permit.PermitShowFeeDetailDobj;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PrintPermitDocInXhtmlImpl;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "pmt_appl_fee")
@ViewScoped
public class PermitApplicatonFeeBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PermitApplicatonFeeBean.class);
    private FormFeePanelBean feePanelBean = new FormFeePanelBean();
    FeeImpl feeImpl = new FeeImpl();
    @ManagedProperty(value = "#{rcpt_bean}")
    private ReceiptMasterBean rcpt_bean;
    private List<PaymentCollectionDobj> payment_list = new ArrayList<>();
    private PaymentCollectionBean paymentBean = new PaymentCollectionBean();
    private String appl_no = "";
    private String regn_no = "";
    private String owner_name = "";
    private String f_name = "";
    private String seat_cap = "";
    private String chasi_no = "";
    private String vh_class = "";
    private String pmt_catg = "";
    private String pmt_type = "";
    private List vh_class_array = new ArrayList();
    private List pmt_catg_array = new ArrayList();
    private List pmt_type_array = new ArrayList();
    PermitApplicatonFeeImpl pmt_fee_impl = null;
    PermitApplicatonFeeDobj pmt_fee_dobj = null;
    private String app_no_msg;
    @ManagedProperty(value = "#{showFee}")
    private PermitShowFeeDetailBean showFeeBean;
    private long totalAmountPayable = 0;
    private String rept_no_msg;
    private String pmtSubType = "";
    private List<PassengerPermitDetailDobj> routedata = new ArrayList<>();
    private String region_covered;
    Map<String, String> config = null;
    private boolean cashModeDisabled = false;
    private Map<String, String> stateConfigMap = null;
    private boolean otherRouteAllow;

    public PermitApplicatonFeeBean() {

        config = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
        setCashModeDisabled(Boolean.parseBoolean(config.get("cash_mode_disabled").toString()));

        String[][] data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
        for (int i = 0; i < data.length; i++) {
            pmt_type_array.add(new SelectItem(data[i][0], data[i][1]));
        }

        data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        for (int i = 0; i < data.length; i++) {
            vh_class_array.add(new SelectItem(data[i][0], data[i][1]));
        }

        feePanelBean.getPurposeCodeList().clear();
        feePanelBean.getPurposeCodeList().add(new SelectItem("-1", "Select Fee"));
        data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
        for (int i = 0; i < data.length; i++) {
            if (Integer.parseInt(data[i][0]) == TableConstants.VM_PMT_APPLICATION_PUR_CD
                    || Integer.parseInt(data[i][0]) == TableConstants.VM_TRANSACTION_MAST_ALL) {
                feePanelBean.getPurposeCodeList().add(new SelectItem(data[i][0], data[i][1]));
            }
        }
        stateConfigMap = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
        otherRouteAllow = Boolean.parseBoolean(stateConfigMap.get("other_office_route_allow"));
        get_VehicleDetails();
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

    public void get_VehicleDetails() {
        Map map = (Map) Util.getSession().getAttribute("seat_map");
        PrintPermitDocInXhtmlImpl pmtDocImpl = new PrintPermitDocInXhtmlImpl();
        setRoutedata(pmtDocImpl.getRouteData(map.get("appl_no").toString(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), TableList.va_permit_route,otherRouteAllow));
        setRegion_covered(pmtDocImpl.getRegionDetail(map.get("appl_no").toString(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), TableList.VA_PERMIT));
        pmt_fee_impl = new PermitApplicatonFeeImpl();
        pmt_fee_dobj = new PermitApplicatonFeeDobj();
        pmtSubType = CommonPermitPrintImpl.getPmtSubType(map.get("appl_no").toString());
        pmt_fee_dobj = pmt_fee_impl.getVehicleDetails(map.get("appl_no").toString(), map.get("regn_no").toString());
        setDobjToValue(pmt_fee_dobj);
    }

    public void setDobjToValue(PermitApplicatonFeeDobj dobj) {
        pmt_catg_array.clear();
        if (dobj != null) {
            this.setAppl_no(dobj.getAppl_no());
            this.setF_name(dobj.getF_name());
            this.setOwner_name(dobj.getOwner_name());
            this.setPmt_type(dobj.getPmt_type());
            String permit_type = dobj.getPmt_type();
            String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
            for (int j = 0; j < data.length; j++) {
                if (data[j][0].equalsIgnoreCase(Util.getUserStateCode())
                        && Integer.parseInt(data[j][3]) == Integer.parseInt(permit_type)) {
                    pmt_catg_array.add(new SelectItem(data[j][1], data[j][2]));
                }
            }
            this.setPmt_catg(dobj.getPmt_catg());
            this.setRegn_no(dobj.getRegn_no());
            this.setVh_class(dobj.getVh_class());
            this.setSeat_cap(dobj.getSeat_cap());
            this.setChasi_no(dobj.getChasi_no());
            if (CommonUtils.isNullOrBlank(getRegion_covered())) {
                setRegion_covered(new PrintPermitDocInXhtmlImpl().getRegionDetailStateVise(dobj.getAppl_no(), dobj.getPmtDobj().getRegion_covered()));
            }
        }
    }

    public String saveFeeDetails() {
        int paymentProcessAmount = 0;
        FeeDraftDobj feeDraftDobj = null;
        String generatedRcpt = "";
        PermitFeeDobj dobj = new PermitFeeDobj();
        dobj.setRegn_no(getRegn_no());
        dobj.setAppl_no(getAppl_no().toString());
        dobj.setFlag("1");
        CommonPermitFeeDobj pmt_fee_dobj = new CommonPermitFeeDobj();
        pmt_fee_dobj.setRegn_no(getRegn_no());
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
        pmt_fee_dobj.setCollected_by(Util.getEmpCode());
        pmt_fee_dobj.setState_cd(Util.getUserStateCode());
        pmt_fee_dobj.setOff_cd(String.valueOf(Util.getUserOffCode()));
        pmt_fee_dobj.setAppl_no(getAppl_no());
        pmt_fee_dobj.setOwner_name(getOwner_name());
        pmt_fee_dobj.setChasi_no(getChasi_no());
        pmt_fee_dobj.setVh_class(getVh_class());
        pmt_fee_dobj.setPur_cd(Util.getSelectedSeat().getPur_cd());
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
            if ((getPaymentBean().getPayment_mode().equals("-1"))) {
                JSFUtils.setFacesMessage("Please Select Fee", "Please Select Fee", JSFUtils.ERROR);
                return "";
            }
            if (!Utility.isNullOrBlank(getPaymentBean().getPayment_mode()) && (!getPaymentBean().getPayment_mode().equals("-1"))
                    && (!getPaymentBean().getPayment_mode().equals("C"))) {
                for (PaymentCollectionDobj payPanelDobj : getPaymentBean().getPaymentlist()) {
                    paymentProcessAmount = paymentProcessAmount + Integer.parseInt(payPanelDobj.getAmount());
                }
                if (!(Utility.isNullOrBlank(getPaymentBean().getPayment_mode())
                        || "-1".equalsIgnoreCase(getPaymentBean().getPayment_mode()))) {
                    feeDraftDobj = new FeeDraftDobj();
                    feeDraftDobj.setAppl_no(this.getAppl_no());
                    feeDraftDobj.setFlag("A");
                    feeDraftDobj.setCollected_by(Util.getEmpCode());
                    feeDraftDobj.setState_cd(Util.getUserStateCode());
                    feeDraftDobj.setOff_cd(String.valueOf(Util.getUserOffCode()));
                    feeDraftDobj.setDraftPaymentList(getPaymentBean().getPaymentlist());
                    generatedRcpt = pmt_fee_impl.saveFeeDetails(pmt_fee_dobj, feeDraftDobj);
                } else {
                    JSFUtils.setFacesMessage("Please select payment mode", "Please select payment mode", JSFUtils.WARN);
                    return "";
                }
            } else if ((getPaymentBean().getPayment_mode().equals("C"))) {
                generatedRcpt = pmt_fee_impl.saveFeeDetails(pmt_fee_dobj, feeDraftDobj);
            }
            if (!Utility.isNullOrBlank(generatedRcpt)) {
                String msg = "Receipt Number:" + generatedRcpt + " generated for " + this.getAppl_no() + " Application Number.";
                printDialogBox(msg);
            } else {
                String msg = "Fee not submitted";
                printDialogBox(msg);
            }
            if (!Utility.isNullOrBlank(generatedRcpt)) {
                rept_no_msg = "Receipt Number:" + generatedRcpt + " generated for " + getAppl_no() + " Application Number.";
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", generatedRcpt);
                return "PrintCashReceiptReport";
            } else {
                rept_no_msg = "Fee not submitted";
            }
        } catch (VahanException e) {
            JSFUtils.setFacesMessage(e.getMessage(), "Information", JSFUtils.ERROR);
        } catch (Exception ex) {
            JSFUtils.setFacesMessage("There is some problem", "There is some problem", JSFUtils.ERROR);
            LOGGER.error(rept_no_msg + ex);
        }
        return "";
    }

    public void printDialogBox(String app_no) {
        if (("").equalsIgnoreCase(app_no)) {
            setApp_no_msg("Fee not Submited");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        } else {
            setApp_no_msg(app_no);
            PrimeFaces.current().executeScript("PF('appNum').show();");
        }
    }

    public void validateForm() {
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

        if (getPaymentBean().getPayment_mode().equals("C") && isCashModeDisabled()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Payment in Cash mode is not accepted.", "Payment in Cash mode is not accepted.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            allGood = true;
        }

        calculateBalanceAmount(Long.valueOf(getShowFeeBean().getTotalAmt()));
        if (!allGood) {
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("popup");
            PrimeFaces.current().executeScript("PF('confDlgFee').show()");
        }
    }

    public List getVh_class_array() {
        return vh_class_array;
    }

    public void setVh_class_array(List vh_class_array) {
        this.vh_class_array = vh_class_array;
    }

    public List getPmt_type_array() {
        return pmt_type_array;
    }

    public void setPmt_type_array(List pmt_type_array) {
        this.pmt_type_array = pmt_type_array;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getVh_class() {
        return vh_class;
    }

    public void setVh_class(String vh_class) {
        this.vh_class = vh_class;
    }

    public String getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(String pmt_type) {
        this.pmt_type = pmt_type;
    }

    public PermitApplicatonFeeImpl getPmt_fee_impl() {
        return pmt_fee_impl;
    }

    public void setPmt_fee_impl(PermitApplicatonFeeImpl pmt_fee_impl) {
        this.pmt_fee_impl = pmt_fee_impl;
    }

    public PermitApplicatonFeeDobj getPmt_fee_dobj() {
        return pmt_fee_dobj;
    }

    public void setPmt_fee_dobj(PermitApplicatonFeeDobj pmt_fee_dobj) {
        this.pmt_fee_dobj = pmt_fee_dobj;
    }

    public FormFeePanelBean getFeePanelBean() {
        return feePanelBean;
    }

    public void setFeePanelBean(FormFeePanelBean feePanelBean) {
        this.feePanelBean = feePanelBean;
    }

    public FeeImpl getFeeImpl() {
        return feeImpl;
    }

    public void setFeeImpl(FeeImpl feeImpl) {
        this.feeImpl = feeImpl;
    }

    public ReceiptMasterBean getRcpt_bean() {
        return rcpt_bean;
    }

    public void setRcpt_bean(ReceiptMasterBean rcpt_bean) {
        this.rcpt_bean = rcpt_bean;
    }

    public List<PaymentCollectionDobj> getPayment_list() {
        return payment_list;
    }

    public void setPayment_list(List<PaymentCollectionDobj> payment_list) {
        this.payment_list = payment_list;
    }

    public PaymentCollectionBean getPaymentBean() {
        return paymentBean;
    }

    public void setPaymentBean(PaymentCollectionBean paymentBean) {
        this.paymentBean = paymentBean;
    }

    public String getApp_no_msg() {
        return app_no_msg;
    }

    public void setApp_no_msg(String app_no_msg) {
        this.app_no_msg = app_no_msg;
    }

    public String getPmt_catg() {
        return pmt_catg;
    }

    public void setPmt_catg(String pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    public List getPmt_catg_array() {
        return pmt_catg_array;
    }

    public void setPmt_catg_array(List pmt_catg_array) {
        this.pmt_catg_array = pmt_catg_array;
    }

    public String getSeat_cap() {
        return seat_cap;
    }

    public void setSeat_cap(String seat_cap) {
        this.seat_cap = seat_cap;
    }

    public long getTotalAmountPayable() {
        return totalAmountPayable;
    }

    public void setTotalAmountPayable(long totalAmountPayable) {
        this.totalAmountPayable = totalAmountPayable;
    }

    public String getChasi_no() {
        return chasi_no;
    }

    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
    }

    public PermitShowFeeDetailBean getShowFeeBean() {
        return showFeeBean;
    }

    public void setShowFeeBean(PermitShowFeeDetailBean showFeeBean) {
        this.showFeeBean = showFeeBean;
    }

    public String getRept_no_msg() {
        return rept_no_msg;
    }

    public void setRept_no_msg(String rept_no_msg) {
        this.rept_no_msg = rept_no_msg;
    }

    public String getPmtSubType() {
        return pmtSubType;
    }

    public void setPmtSubType(String pmtSubType) {
        this.pmtSubType = pmtSubType;
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

    public boolean isCashModeDisabled() {
        return cashModeDisabled;
    }

    public void setCashModeDisabled(boolean cashModeDisabled) {
        this.cashModeDisabled = cashModeDisabled;
    }

    public boolean isOtherRouteAllow() {
        return otherRouteAllow;
    }

    public void setOtherRouteAllow(boolean otherRouteAllow) {
        this.otherRouteAllow = otherRouteAllow;
    }
}
