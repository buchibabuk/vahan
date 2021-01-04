/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.java.util.CommonUtils;
import nic.rto.vahan.common.VahanException;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.admin.ChangesByUser;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.bean.permit.PermitPanelBean;
import nic.vahan.form.dobj.TaxEndorsementDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.tax.TaxDobj;
import nic.vahan.form.impl.TaxEndorsementImpl;
import nic.vahan.form.impl.OwnerImpl;
import static nic.vahan.form.impl.TaxServer_Impl.callKLTaxRateService;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.admin.OwnerAdminImpl;
import nic.vahan.form.impl.tax.TaxImpl;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
//import tax.web.service.VahanTaxParameters;
import nic.vahan.form.dobj.tax.VahanTaxParameters;
import static nic.vahan.CommonUtils.FormulaUtils.fillTaxParametersFromDobj;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;

/**
 *
 * @author nicsi
 */
@ViewScoped
@ManagedBean(name = "endorsmentTaxEntryBean")
public class TaxEndorsementBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger logger = Logger.getLogger(TaxEndorsementBean.class);
    private String regnNo;
    private String applNo = null;
    private String stateCd;
    private String empCd;
    private int offCode;
    private OwnerDetailsDobj ownerDetail;
    private boolean render = false;
    private int actioncd = 0;
    private boolean renderBackBtm = true;
    private boolean renderReset = false;
    private boolean rendertaxPaid = false;
    private TaxEndorsementDobj endorsmentTaxEntryDobj = null;
    private TaxEndorsementDobj endorsmentTaxEntryDobjPrv = null;
    private Date minDateForUpto = DateUtil.parseDate(DateUtil.getCurrentDate());
    private Date maxDate = new Date();
    private Date minregn_dt = new Date();
    private String vahanMessages = null;
    private boolean renderFileMove;
    private List<ComparisonBean> prevChangedDataList = new ArrayList<>();
    private TaxEndorsementImpl endorsmentTaxEntryImpl = new TaxEndorsementImpl();
    private PassengerPermitDetailDobj pmt_dobj = null;
    private PermitPanelBean permitPanelBean = null;
    private Map<String, String> modeHashMap = null;
    private Map<String, String> taxWefMap = null;
    private String wittEffectDate = "";
    private String prvWittEffectDate;
    private List<ComparisonBean> listChanges = new ArrayList<>();
    private List<ChangesByUser> listPreviousChanges = new ArrayList<>();
    private List<TaxEndorsementDobj> listEndosrsmts = new ArrayList<TaxEndorsementDobj>();
    private List<TaxEndorsementDobj> listEndosrsmtsPrv = new ArrayList<TaxEndorsementDobj>();
    private boolean renderSave = false;
    private Date dateCompare;
    private List<TaxEndorsementDobj> historyEndosrsmtsList = new ArrayList<TaxEndorsementDobj>();
    Owner_dobj ownerDobj = null;
    private boolean renderPushBackSeatPanel;
    private PassengerPermitDetailImpl pmImpl = new PassengerPermitDetailImpl();
    private String issueDate = null;
    private boolean endorsmtFirstTime;

    @PostConstruct
    public void init() {
        try {

            endorsmentTaxEntryDobj = new TaxEndorsementDobj();
            OwnerAdminImpl adminImpl = new OwnerAdminImpl();
            modeHashMap = new HashMap<String, String>();

            renderFileMove = false;
            taxWefMap = new HashMap<String, String>();
            stateCd = Util.getUserStateCode();
            offCode = Util.getSelectedSeat().getOff_cd();
            empCd = Util.getEmpCode();
            permitPanelBean = new PermitPanelBean();
            this.applNo = appl_details.getAppl_no();
            if (stateCd == null
                    || offCode == 0
                    || empCd == null) {
                vahanMessages = "Something went wrong, Please try again later...";
                return;
            }
            modeHashMap.put("Automatic", "A");
            modeHashMap.put("Manual", "M");
            modeHashMap.put("Court order", "C");
            if (applNo != null) {

                regnNo = appl_details.getRegn_no();
                listEndosrsmts = endorsmentTaxEntryImpl.getEndorsmentDetails(applNo, stateCd, offCode);
                if (listEndosrsmts.isEmpty()) {
                    vahanMessages = " Vehicle Details not Found !";
                    return;
                }
                listEndosrsmtsPrv.addAll(listEndosrsmts);
                OwnerImpl owner_Impl = new OwnerImpl();
                ownerDetail = owner_Impl.getOwnerDetailsWithOffice(regnNo, stateCd, 0);
                if (ownerDetail != null && ownerDetail.getOff_cd() != Util.getUserOffCode() && ownerDetail.getVh_class() == 73) {
                    //issueDate = endorsmentTaxEntryImpl.checkPermitDtlsInOtherOffice(regnNo, stateCd, Util.getUserOffCode());
                    pmt_dobj = endorsmentTaxEntryImpl.getPermitDtlsWithTmpPmt(regnNo, stateCd);
                    if (pmt_dobj == null) {
                        throw new VahanException("Vehicle Details not Found!");
                    }
                } else if (ownerDetail != null && ownerDetail.getOff_cd() == Util.getUserOffCode()) {
                    pmt_dobj = endorsmentTaxEntryImpl.getPermitDtlsWithTmpPmt(regnNo, stateCd);
                } else {
                    ownerDetail = null;
                }

                if (ownerDetail == null) {
                    vahanMessages = " Vehicle Details not Found !";
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", vahanMessages));
                    return;
                }

                if (ownerDetail.getState_cd().equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(ownerDetail.getVh_class() + ",")) && (ownerDetail.getPush_bk_seat() != 0 || ownerDetail.getOrdinary_seat() != 0)) {
                    setRenderPushBackSeatPanel(true);
                } else {
                    setRenderPushBackSeatPanel(false);
                }

                OwnerImpl ownerImpl = new OwnerImpl();
                ownerDobj = ownerImpl.getOwnerDobj(ownerDetail);
                if (pmt_dobj == null) {
                    pmt_dobj = pmImpl.set_vt_permit_regnNo_to_dobj(ownerDobj.getRegn_no(), "");
                    if (pmt_dobj == null) {
                        pmt_dobj = endorsmentTaxEntryImpl.getPermitDobjFromVhaPermitNew(ownerDobj);
                    }
                }
                if (ownerDobj.getRegn_dt() != null) {

                    taxWefMap.put("Registration Date (" + ownerDetail.getRegnDateDescr() + ")", ownerDetail.getRegnDateDescr());
                }
                if (ownerDobj.getPurchase_dt() != null) {

                    taxWefMap.put("Purchase Date (" + ownerDetail.getPurchaseDateDescr() + ")", ownerDetail.getPurchaseDateDescr());
                }
                if (pmt_dobj != null) {
                    getPermitPanelBean().setPermitDobj(pmt_dobj);
                    getPermitPanelBean().onSelectPermitType(null);
                    if (pmt_dobj.getIssuePmtDate() != null) {
                        taxWefMap.put("Permit Date (" + pmt_dobj.getIssuePmtDateDescr() + ")", pmt_dobj.getIssuePmtDateDescr());
                    } else if (!CommonUtils.isNullOrBlank(issueDate)) {
                        taxWefMap.put("Permit Date (" + issueDate + ")", issueDate);
                    }
                }
                TaxImpl taximpl = new TaxImpl();
                TaxDobj taxDobj = taximpl.getTaxDetails(regnNo, "58", stateCd);
                if (taxDobj != null && taxDobj.getTax_upto() != null) {
                    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
                    Calendar c = Calendar.getInstance();
                    c.setTime(taxDobj.getTax_upto());
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    taxWefMap.put("Previews Tax-Paid-Upto-Date (" + format.format(c.getTime()) + ")", format.format(c.getTime()));
                }
                listPreviousChanges = adminImpl.getModificationOnRegNoByUser(applNo);

                setRenderFileMove(true);
                setRenderBackBtm(false);
                setRender(true);
                setRenderReset(false);
                historyEndosrsmtsList = endorsmentTaxEntryImpl.getVTEndorsmentDetails(regnNo, stateCd, offCode);
                if (historyEndosrsmtsList.isEmpty()) {
                    endorsmtFirstTime = true;
                }
            } else {
                endorsmentTaxEntryDobj = new TaxEndorsementDobj();
                setRenderFileMove(false);
                setRenderBackBtm(false);
                setRender(false);
                setRenderReset(false);
            }
        } catch (VahanException ve) {
            vahanMessages = "Something went wrong, Please try again later...";
        } catch (Exception ex) {
            logger.error(ex.toString() + " [ApplNo-" + appl_details.getAppl_no() + ",RegnNo-" + appl_details.getRegn_no() + ",PurCd-" + appl_details.getPur_cd() + "] " + ex.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
        }
    }

    public void showDetails() {
        try {
            String msg = "";
            issueDate = null;
            stateCd = Util.getUserStateCode();
            offCode = Util.getSelectedSeat().getOff_cd();
            empCd = Util.getEmpCode();
            OwnerImpl ownerImpl = new OwnerImpl();
            OwnerAdminImpl adminImpl = new OwnerAdminImpl();
            if (this.empCd == null || this.empCd.trim().equalsIgnoreCase("") || this.stateCd == null || this.stateCd.trim().equalsIgnoreCase("")) {
                msg = "Session Expired. Please try again.";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }

            if (this.regnNo == null || this.regnNo.trim().equalsIgnoreCase("")) {
                msg = "Please Enter Valid Application Number";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            ownerDetail = ownerImpl.getOwnerDetailsWithOffice(regnNo, stateCd, 0);
            if (ownerDetail != null && ownerDetail.getOff_cd() != Util.getUserOffCode() && ownerDetail.getVh_class() == 73) {
                // issueDate = endorsmentTaxEntryImpl.checkPermitDtlsInOtherOffice(regnNo, stateCd, Util.getUserOffCode());
                pmt_dobj = endorsmentTaxEntryImpl.getPermitDtlsWithTmpPmt(regnNo, stateCd);
                if (pmt_dobj == null) {
                    throw new VahanException("Vehicle Details not Found!");
                }
            } else if (ownerDetail != null && ownerDetail.getOff_cd() == Util.getUserOffCode()) {
                pmt_dobj = endorsmentTaxEntryImpl.getPermitDtlsWithTmpPmt(regnNo, stateCd);
            } else {
                ownerDetail = null;
            }
            msg = " Vehicle Details not Found !";
            if (ownerDetail == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }

            if (ownerDetail.getState_cd().equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(ownerDetail.getVh_class() + ",")) && (ownerDetail.getPush_bk_seat() != 0 || ownerDetail.getOrdinary_seat() != 0)) {
                setRenderPushBackSeatPanel(true);
            } else {
                setRenderPushBackSeatPanel(false);
            }

            historyEndosrsmtsList = endorsmentTaxEntryImpl.getVTEndorsmentDetails(regnNo, stateCd, offCode);

            if (historyEndosrsmtsList.isEmpty()) {
                endorsmtFirstTime = true;
            }
            TmConfigurationDobj config = Util.getTmConfiguration();
            ownerDobj = ownerImpl.getOwnerDobj(ownerDetail);
            boolean endrsmntFlag = false;
            if (ownerDobj != null && config != null) {
                VehicleParameters vehParameters = fillVehicleParametersFromDobj(ownerDobj);
                endrsmntFlag = isCondition(replaceTagValues(config.getTax_exemption(), vehParameters), "Endorsement_tax_showDetails");

            }
            if (!endrsmntFlag) {
                msg = "Tax Endorsement not allowed for this vehicle No [" + regnNo.toUpperCase() + "].";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }

            if (pmt_dobj == null) {
                pmt_dobj = pmImpl.set_vt_permit_regnNo_to_dobj(ownerDobj.getRegn_no(), "");
                if (pmt_dobj == null) {
                    pmt_dobj = endorsmentTaxEntryImpl.getPermitDobjFromVhaPermitNew(ownerDobj);
                }
            }

            if (ownerDobj.getRegn_dt() != null) {
                taxWefMap.put("Registration Date (" + ownerDetail.getRegnDateDescr() + ")", ownerDetail.getRegnDateDescr());
            }
            if (ownerDobj.getPurchase_dt() != null) {
                taxWefMap.put("Purchase Date (" + ownerDetail.getPurchaseDateDescr() + ")", ownerDetail.getPurchaseDateDescr());
            }
            if (pmt_dobj != null) {
                getPermitPanelBean().setPermitDobj(pmt_dobj);
                getPermitPanelBean().onSelectPermitType(null);
                if (pmt_dobj.getIssuePmtDate() != null) {
                    taxWefMap.put("Permit Date (" + pmt_dobj.getIssuePmtDateDescr() + ")", pmt_dobj.getIssuePmtDateDescr());
                } else if (!CommonUtils.isNullOrBlank(issueDate)) {
                    taxWefMap.put("Permit Date (" + issueDate + ")", issueDate);
                }
            }
            TaxImpl taximpl = new TaxImpl();
            TaxDobj taxDobj = taximpl.getTaxDetails(regnNo, "58", stateCd);
            if (taxDobj != null && taxDobj.getTax_upto() != null) {
                SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
                Calendar c = Calendar.getInstance();
                c.setTime(taxDobj.getTax_upto());
                c.add(Calendar.DAY_OF_MONTH, 1);
                taxWefMap.put("Previews Tax-Paid-Upto-Date (" + format.format(c.getTime()) + ")", format.format(c.getTime()));
            }

            setMinregn_dt(DateUtil.getDateToTimesTamp(ownerDetail.getRegn_dt()));
            setRenderSave(true);
            setRender(true);
            setRenderBackBtm(false);
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ve.getMessage()));
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ex.getMessage()));
        }

    }

    public void listnerUpdateUptoForTax() {
        setMinDateForUpto(endorsmentTaxEntryDobj.getEndorsFromDate());
    }

//    public void listnerUpdateManualAutoTaxMode() {
//        String taxMode = endorsmentTaxEntryDobj.getModMnulAuto();
//
//        if (taxMode.equals("M")) {
//            setRenderTaxmode(true);
//
//        } else {
//            setRenderTaxmode(false);
//
//        }
//
//
//    }
    public void saveEndorsTax() {
        try {
            String msg = "";
            if (listEndosrsmts.isEmpty()) {
                throw new VahanException("Please Add the Endorsement Tax Details.");
            }
            String applNo = endorsmentTaxEntryImpl.inwardEndorsmentTax(getListEndosrsmts());
            String facesMessages = "Application No Gererated : [ " + applNo + " ]";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, facesMessages, facesMessages));
            PrimeFaces.current().ajax().update("msgDialog");
            PrimeFaces.current().executeScript("PF('messageDialog').show();");
            setRenderBackBtm(true);
            setRender(false);
            setRenderReset(true);

        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ve.getMessage()));
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ex.getMessage()));
        }

    }

    public void reset() {

        historyEndosrsmtsList.clear();
        //modeHashMap.clear();
        listEndosrsmts.clear();
        endorsmentTaxEntryDobj = null;
        endorsmentTaxEntryDobj = new TaxEndorsementDobj();
        ownerDetail = null;
        applNo = null;
        regnNo = "";
        setRenderBackBtm(true);
        setRender(false);
        setRenderReset(false);
        //isTransport = false;
        issueDate = null;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public String getStateCd() {
        return stateCd;
    }

    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    public String getEmpCd() {
        return empCd;
    }

    public void setEmpCd(String empCd) {
        this.empCd = empCd;
    }

    public int getOffCode() {
        return offCode;
    }

    public void setOffCode(int offCode) {
        this.offCode = offCode;
    }

    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    public boolean isRender() {
        return render;
    }

    public void setRender(boolean render) {
        this.render = render;
    }

    public int getActioncd() {
        return actioncd;
    }

    public void setActioncd(int actioncd) {
        this.actioncd = actioncd;
    }

    public boolean isRenderBackBtm() {
        return renderBackBtm;
    }

    public void setRenderBackBtm(boolean renderBackBtm) {
        this.renderBackBtm = renderBackBtm;
    }

    public boolean isRenderReset() {
        return renderReset;
    }

    public void setRenderReset(boolean renderReset) {
        this.renderReset = renderReset;
    }

    public boolean isRendertaxPaid() {
        return rendertaxPaid;
    }

    public void setRendertaxPaid(boolean rendertaxPaid) {
        this.rendertaxPaid = rendertaxPaid;
    }

    public TaxEndorsementDobj getEndorsmentTaxEntryDobj() {
        return endorsmentTaxEntryDobj;
    }

    public void setEndorsmentTaxEntryDobj(TaxEndorsementDobj endorsmentTaxEntryDobj) {
        this.endorsmentTaxEntryDobj = endorsmentTaxEntryDobj;
    }

    public Date getMinDateForUpto() {
        return minDateForUpto;
    }

    public void setMinDateForUpto(Date minDateForUpto) {
        this.minDateForUpto = minDateForUpto;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public Date getMinregn_dt() {
        return minregn_dt;
    }

    public void setMinregn_dt(Date minregn_dt) {
        this.minregn_dt = minregn_dt;
    }

    @Override
    public String save() {
        try {

            if (listEndosrsmts.isEmpty()) {
                throw new VahanException("Please add the Endorsement Tax Details .");
            }
            if (!compareChanges().isEmpty()) {
                endorsmentTaxEntryImpl.saveModifyEndosmntTax(applNo, listChanges, getListEndosrsmts());
            }
            return "seatwork";
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ve.getMessage()));
        } catch (Exception ex) {
            logger.error("ApplNo-" + appl_details.getAppl_no() + ",Regn-No:" + regnNo + "-" + ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ex.getMessage()));
        }
        return "";
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        try {
            listChanges.clear();
            if (listEndosrsmts.size() == listEndosrsmtsPrv.size() && !listEndosrsmts.isEmpty() && !listEndosrsmtsPrv.isEmpty()) {
                int i = 0;
                for (TaxEndorsementDobj endorsmentDobj : listEndosrsmts) {
                    setEndorsmentTaxEntryDobjPrv(listEndosrsmtsPrv.get(i));
                    Compare("tax_mode", getEndorsmentTaxEntryDobjPrv().getModMnulAuto(), endorsmentDobj.getModMnulAuto(), listChanges);
//                    Compare("wef_date", prvWittEffectDate, this.wittEffectDate, listChanges);
                    if (getEndorsmentTaxEntryDobjPrv().getTaxRate() != endorsmentDobj.getTaxRate()) {
                        Compare("tax_rate", getEndorsmentTaxEntryDobjPrv().getTaxRate(), endorsmentDobj.getTaxRate(), listChanges);
                    }
                    if (getEndorsmentTaxEntryDobjPrv().getNoofQuarter() != endorsmentDobj.getNoofQuarter()) {
                        Compare("no_qurater", getEndorsmentTaxEntryDobjPrv().getNoofQuarter(), endorsmentDobj.getNoofQuarter(), listChanges);
                    }
                    Compare("wef_date", getEndorsmentTaxEntryDobjPrv().getWithEffectDate(), endorsmentDobj.getWithEffectDate(), listChanges);
                    Compare("endsmntax__dt", getEndorsmentTaxEntryDobjPrv().getEndorsFromDate(), endorsmentDobj.getEndorsFromDate(), listChanges);
                    Compare("endsmntax_dt_to ", getEndorsmentTaxEntryDobjPrv().getEndorsUpto(), endorsmentDobj.getEndorsUpto(), listChanges);
                    Compare("remark", getEndorsmentTaxEntryDobjPrv().getRemark(), endorsmentDobj.getRemark(), listChanges);
                    i++;
                }
            } else if (listEndosrsmts.size() > listEndosrsmtsPrv.size() && !listEndosrsmts.isEmpty() && !listEndosrsmtsPrv.isEmpty()) {
                for (TaxEndorsementDobj endorsmentDobj : listEndosrsmts) {
                    if (!listEndosrsmtsPrv.contains(endorsmentDobj)) {
                        Compare("tax_mode_add", null, endorsmentDobj.getModMnulAuto(), listChanges);
                        Compare("tax_rate_add", 0, endorsmentDobj.getTaxRate(), listChanges);
                        Compare("no_qurater_add", 0, endorsmentDobj.getNoofQuarter(), listChanges);
                        Compare("endsmntax__dt_add", null, endorsmentDobj.getEndorsFromDate(), listChanges);
                        Compare("endsmntax_dt_to_add ", null, endorsmentDobj.getEndorsUpto(), listChanges);
                        Compare("remark_add", null, endorsmentDobj.getRemark(), listChanges);
                    }
                }

            } else if (listEndosrsmts.size() < listEndosrsmtsPrv.size() && !listEndosrsmts.isEmpty() && !listEndosrsmtsPrv.isEmpty()) {
                for (TaxEndorsementDobj endorsmentDobj : listEndosrsmtsPrv) {
                    if (!listEndosrsmts.contains(endorsmentDobj)) {
                        Compare("tax_mode_delete", endorsmentDobj.getModMnulAuto(), "", listChanges);
                        Compare("tax_rate_delete", endorsmentDobj.getTaxRate(), 0, listChanges);
                        Compare("no_qurater_delete", endorsmentDobj.getNoofQuarter(), 0, listChanges);
                        Compare("endsmntax__dt_delete", endorsmentDobj.getEndorsFromDate(), getDateCompare(), listChanges);
                        Compare("endsmntax_dt_to_delete ", endorsmentDobj.getEndorsUpto(), getDateCompare(), listChanges);
                        Compare("remark", endorsmentDobj.getRemark(), "", listChanges);
                    }
                }
            }

            return listChanges;
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return null;
    }

    @Override
    public String saveAndMoveFile() {
        try {
            if (listEndosrsmts.isEmpty()) {
                throw new VahanException("Please add the Endorsement Tax Details .");
            }
            if (applNo != null) {
                Status_dobj status = new Status_dobj();
                status.setAppl_dt(getAppl_details().getAppl_dt());
                status.setAppl_no(getAppl_details().getAppl_no());
                status.setPur_cd(getAppl_details().getPur_cd());
                status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
                status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
                status.setStatus(getApp_disapp_dobj().getNew_status());
                status.setAction_cd(getApp_disapp_dobj().getPre_action_code());
                status.setCurrent_role(appl_details.getCurrent_action_cd());
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                        || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT) || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                    endorsmentTaxEntryImpl.saveAndMoveEndosmntTax(status, getListEndosrsmts(), applNo, compareChanges(), getHistoryEndosrsmtsList());
                }

            }
            return "seatwork";
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ve.getMessage()));
        } catch (Exception ex) {
            logger.error("ApplNo-" + appl_details.getAppl_no() + ",Regn-No:" + regnNo + "-" + ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ex.getMessage()));
        }
        return "";
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return listChanges;//To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveEApplication() {
    }

    public boolean isRenderFileMove() {
        return renderFileMove;
    }

    public void setRenderFileMove(boolean renderFileMove) {
        this.renderFileMove = renderFileMove;
    }

    public String getVahanMessages() {
        return vahanMessages;
    }

    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    public PassengerPermitDetailDobj getPmt_dobj() {
        return pmt_dobj;
    }

    public void setPmt_dobj(PassengerPermitDetailDobj pmt_dobj) {
        this.pmt_dobj = pmt_dobj;
    }

    public PermitPanelBean getPermitPanelBean() {
        return permitPanelBean;
    }

    public void setPermitPanelBean(PermitPanelBean permitPanelBean) {
        this.permitPanelBean = permitPanelBean;
    }

    public Map<String, String> getModeHashMap() {
        return modeHashMap;
    }

    public void setModeHashMap(Map<String, String> modeHashMap) {
        this.modeHashMap = modeHashMap;
    }

    public Map<String, String> getTaxWefMap() {
        return taxWefMap;
    }

    public void setTaxWefMap(Map<String, String> taxWefMap) {
        this.taxWefMap = taxWefMap;
    }

    public String getWittEffectDate() {
        return wittEffectDate;
    }

    public void setWittEffectDate(String wittEffectDate) {
        this.wittEffectDate = wittEffectDate;
    }

    public List<ComparisonBean> getListChanges() {
        return listChanges;
    }

    public void setListChanges(List<ComparisonBean> listChanges) {
        this.listChanges = listChanges;
    }

    public TaxEndorsementDobj getEndorsmentTaxEntryDobjPrv() {
        return endorsmentTaxEntryDobjPrv;
    }

    public void setEndorsmentTaxEntryDobjPrv(TaxEndorsementDobj endorsmentTaxEntryDobjPrv) {
        this.endorsmentTaxEntryDobjPrv = endorsmentTaxEntryDobjPrv;
    }

    public String getPrvWittEffectDate() {
        return prvWittEffectDate;
    }

    public void setPrvWittEffectDate(String prvWittEffectDate) {
        this.prvWittEffectDate = prvWittEffectDate;
    }

    public List<ChangesByUser> getListPreviousChanges() {
        return listPreviousChanges;
    }

    public void setListPreviousChanges(List<ChangesByUser> listPreviousChanges) {
        this.listPreviousChanges = listPreviousChanges;
    }

    public String reinit() {
        String withEffDate = getWittEffectDate();
        String facesMessages = "";
        if (getListEndosrsmts().size() >= 3) {
            getListEndosrsmts().remove(getListEndosrsmts().size() - 1);
            facesMessages = "Not Allowed Greater than Two Entry.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, facesMessages, facesMessages));
            return "";
        }
        if (getListEndosrsmts().size() == 1) {
            TaxEndorsementDobj endsDboj = getListEndosrsmts().get(0);
            if (endsDboj.getModMnulAuto().isEmpty()) {
                getListEndosrsmts().remove(getListEndosrsmts().size() - 1);
                facesMessages = "Please Select Tax Mode.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, facesMessages, facesMessages));
                return "";
            }

            if (withEffDate.isEmpty()) {
                getListEndosrsmts().remove(getListEndosrsmts().size() - 1);
                facesMessages = "Please Select Tax With Effect From Date.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, facesMessages, facesMessages));
                return "";

            }
            getListEndosrsmts().get(0).setWithEffectDate(JSFUtils.getStringToDateddMMMyyyy(withEffDate));

            if (endsDboj.getEndorsUpto() == null && endsDboj.getModMnulAuto().equals("M")) {
                getListEndosrsmts().remove(getListEndosrsmts().size() - 1);
                facesMessages = "Please Enter Tax Endorsement To.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, facesMessages, facesMessages));
                return "";

            }

            if (CommonUtils.isNullOrBlank(endsDboj.getRemark())) {
                getListEndosrsmts().remove(getListEndosrsmts().size() - 1);
                facesMessages = "Please Enter Remark.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, facesMessages, facesMessages));
                return "";

            }
            getListEndosrsmts().get(0).setEmpCode(empCd);
            getListEndosrsmts().get(0).setStateCd(stateCd);
            getListEndosrsmts().get(0).setRegnNo(regnNo);
            getListEndosrsmts().get(0).setOffCd(offCode);
            getListEndosrsmts().get(0).setSerial_no(1);

        }
        if (getListEndosrsmts().size() == 2) {
            TaxEndorsementDobj endsDboj = getListEndosrsmts().get(1);
            if (endsDboj.getModMnulAuto().isEmpty()) {
                facesMessages = "Please Select Tax Mode.";
                getListEndosrsmts().remove(getListEndosrsmts().size() - 1);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, facesMessages, facesMessages));
                return "";
            }

            if (withEffDate.isEmpty()) {
                getListEndosrsmts().remove(getListEndosrsmts().size() - 1);
                facesMessages = "Please Select Tax With Effect From Date.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, facesMessages, facesMessages));
                return "";

            }
            getListEndosrsmts().get(1).setWithEffectDate(JSFUtils.getStringToDateddMMMyyyy(withEffDate));

            if (endsDboj.getEndorsUpto() == null && endsDboj.getModMnulAuto().equals("M")) {
                getListEndosrsmts().remove(getListEndosrsmts().size() - 1);
                facesMessages = "Please Enter Tax Endorsement To.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, facesMessages, facesMessages));
                return "";

            }

            if (CommonUtils.isNullOrBlank(endsDboj.getRemark())) {
                getListEndosrsmts().remove(getListEndosrsmts().size() - 1);
                facesMessages = "Please Enter Remark.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, facesMessages, facesMessages));
                return "";

            }
            getListEndosrsmts().get(1).setEmpCode(empCd);
            getListEndosrsmts().get(1).setStateCd(stateCd);
            getListEndosrsmts().get(1).setRegnNo(regnNo);
            getListEndosrsmts().get(1).setOffCd(offCode);
            getListEndosrsmts().get(1).setSerial_no(2);

        }
        setWittEffectDate("");
        endorsmentTaxEntryDobj = new TaxEndorsementDobj();

        return null;
    }

    public void listnerUpdateManualAutoTaxMode() {
        String taxMode = endorsmentTaxEntryDobj.getModMnulAuto();
        try {
            if (taxMode.equals("A")) {
                VahanTaxParameters taxParameters = fillTaxParametersFromDobj(ownerDobj, permitPanelBean.getPermitDobj());
                if (taxParameters.getSTATE_CD().equals("KL")) {
                    int pushbackseat = 0;
                    int ordinaryseat = 0;
                    if (TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(ownerDobj.getVh_class() + ",")) && (ownerDetail.getPush_bk_seat() != 0 || ownerDetail.getOrdinary_seat() != 0)) {
                        pushbackseat = ownerDetail.getPush_bk_seat();
                        ordinaryseat = ownerDetail.getOrdinary_seat();
                    }
                    if (endorsmtFirstTime) {
                        taxParameters.setNEW_VCH("Y");
                    } else {
                        taxParameters.setNEW_VCH("N");
                    }

                    taxParameters.setPUR_CD(58);
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtil.formatDate("dd-MM-yyyy", endorsmentTaxEntryDobj.getEndorsFromDate()));
                    int taxRate = callKLTaxRateService(taxParameters, pushbackseat, ordinaryseat, ownerDobj.getRegn_no(), ownerDobj.getChasi_no());
                    if (taxRate < 0) {
                        throw new VahanException("Error in geting tax rate, tax rate returned is" + taxRate);
                    }
                    endorsmentTaxEntryDobj.setTaxRate(taxRate);
                }
            }
        } catch (VahanException ve) {
            endorsmentTaxEntryDobj = new TaxEndorsementDobj();
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ve.getMessage()));
        } catch (Exception ex) {
            endorsmentTaxEntryDobj = new TaxEndorsementDobj();
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ex.getMessage()));
        }
    }

    public void getDeleteEndorsmntDtls() {
        List list = listEndosrsmts;
        String facesMessages = "Please Select Tax Mode";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, facesMessages, facesMessages));
        return;
    }

    public List<TaxEndorsementDobj> getListEndosrsmts() {
        return listEndosrsmts;
    }

    public void setListEndosrsmts(List<TaxEndorsementDobj> listEndosrsmts) {
        this.listEndosrsmts = listEndosrsmts;
    }

    public boolean isRenderSave() {
        return renderSave;
    }

    public void setRenderSave(boolean renderSave) {
        this.renderSave = renderSave;
    }

    public List<TaxEndorsementDobj> getListEndosrsmtsPrv() {
        return listEndosrsmtsPrv;
    }

    public void setListEndosrsmtsPrv(List<TaxEndorsementDobj> listEndosrsmtsPrv) {
        this.listEndosrsmtsPrv = listEndosrsmtsPrv;
    }

    public Date getDateCompare() {
        return dateCompare;
    }

    public void setDateCompare(Date dateCompare) {
        this.dateCompare = dateCompare;
    }

    public List<TaxEndorsementDobj> getHistoryEndosrsmtsList() {
        return historyEndosrsmtsList;
    }

    public void setHistoryEndosrsmtsList(List<TaxEndorsementDobj> historyEndosrsmtsList) {
        this.historyEndosrsmtsList = historyEndosrsmtsList;
    }

    public boolean isRenderPushBackSeatPanel() {
        return renderPushBackSeatPanel;
    }

    public void setRenderPushBackSeatPanel(boolean renderPushBackSeatPanel) {
        this.renderPushBackSeatPanel = renderPushBackSeatPanel;
    }

    public boolean isEndorsmtFirstTime() {
        return endorsmtFirstTime;
    }

    public void setEndorsmtFirstTime(boolean endorsmtFirstTime) {
        this.endorsmtFirstTime = endorsmtFirstTime;
    }
}
