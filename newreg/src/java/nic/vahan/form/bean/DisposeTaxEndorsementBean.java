/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.admin.ChangesByUser;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.bean.permit.PermitPanelBean;
import nic.vahan.form.dobj.TaxEndorsementDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.tax.TaxDobj;
import nic.vahan.form.impl.DisposeTaxEndorsementImpl;
import nic.vahan.form.impl.TaxEndorsementImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.admin.OwnerAdminImpl;
import nic.vahan.form.impl.tax.TaxImpl;
import org.apache.log4j.Logger;
//import org.primefaces.context.RequestContext;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nicsi
 */
@ViewScoped
@ManagedBean(name = "disposeEndrsmntTaxBean")
public class DisposeTaxEndorsementBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

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
//    private Date minregn_dt = new Date();
    private String vahanMessages = null;
    private boolean renderFileMove;
    private List<ComparisonBean> prevChangedDataList = new ArrayList<>();
    private TaxEndorsementImpl endorsmentTaxEntryImpl = new TaxEndorsementImpl();
    private DisposeTaxEndorsementImpl disposeEndorsmentImpl = new DisposeTaxEndorsementImpl();
    private PassengerPermitDetailDobj pmt_dobj = null;
    private PermitPanelBean permitPanelBean = null;
    private Map<String, String> modeHashMap = null;
    private Map<String, String> taxWefMap = null;
    private String wittEffectDate = "";
    private String prvWittEffectDate;
    private List<ComparisonBean> listChanges = new ArrayList<>();
    private List<ChangesByUser> listPreviousChanges = new ArrayList<>();
    private boolean renderSave = false;
    private boolean isTransport;
    private List<TaxEndorsementDobj> historyEndosrsmtsList = new ArrayList<TaxEndorsementDobj>();

    @PostConstruct
    public void init() {
        try {
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
                if (CommonUtils.isNullOrBlank(regnNo)) {
                    vahanMessages = " Vehicle Details not Found !";
                    return;
                }
                historyEndosrsmtsList = endorsmentTaxEntryImpl.getVTEndorsmentDetails(regnNo, stateCd, offCode);
                if (historyEndosrsmtsList.isEmpty()) {
                    vahanMessages = " Vehicle Details not Found !";
                    return;
                }
                //listEndosrsmtsPrv.addAll(listEndosrsmts);
                OwnerImpl owner_Impl = new OwnerImpl();
                ownerDetail = owner_Impl.getOwnerDetailsWithOffice(regnNo, stateCd, offCode);
                if (ownerDetail == null) {
                    vahanMessages = " Vehicle Details not Found !";
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", vahanMessages));
                    return;
                }
                OwnerImpl ownerImpl = new OwnerImpl();
                Owner_dobj ownerDobj = ownerImpl.getOwnerDobj(ownerDetail);
                pmt_dobj = TaxServer_Impl.getPermitInfoForSavedTax(ownerDobj);
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
                //listPreviousChanges = adminImpl.getModificationOnRegNoByUser(applNo);

                setRenderFileMove(true);
                setRenderBackBtm(false);
                setRender(true);
                setRenderReset(false);

            } else {
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
            String isPending = adminImpl.checkPending(regnNo, Util.getUserStateCode());
            if (isPending != null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, isPending, isPending));
                return;
            }
            ownerDetail = ownerImpl.getOwnerDetailsWithOffice(regnNo, stateCd, offCode);
            msg = " Vehicle Details not Found !";
            if (ownerDetail == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            historyEndosrsmtsList = endorsmentTaxEntryImpl.getVTEndorsmentDetails(regnNo, stateCd, offCode);
            if (historyEndosrsmtsList.isEmpty()) {
                msg = " Endorsement Tax Details not Found !";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }

            // DateUtil.getDateToTimesTamp(otp)
            Owner_dobj ownerDobj = ownerImpl.getOwnerDobj(ownerDetail);
            pmt_dobj = TaxServer_Impl.getPermitInfoForSavedTax(ownerDobj);
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
//
//            setMinregn_dt(DateUtil.getDateToTimesTamp(ownerDetail.getRegn_dt()));
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

//    public void listnerUpdateUptoForTax() {
//        setMinDateForUpto(endorsmentTaxEntryDobj.getEndorsFromDate());
//    }
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
    public void disposeEndorsTax() {
        try {
            String msg = "";
            if (historyEndosrsmtsList.isEmpty()) {
                throw new VahanException("Endorsement Tax Details not Found.");
            }
            String applNo = disposeEndorsmentImpl.inwardDisposeEndorsmentTax(getHistoryEndosrsmtsList());
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
        ownerDetail = null;
        applNo = null;
        regnNo = "";
        setRenderBackBtm(true);
        setRender(false);
        setRenderReset(false);
        isTransport = false;
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

    @Override
    public String save() {
        try {
            String msg = "";
            if (empCd == null || empCd.trim().equalsIgnoreCase("")) {
                msg = "Session Expired. Please try again.";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));

            }
            return "seatwork";
        } catch (Exception ex) {
            logger.error("ApplNo-" + appl_details.getAppl_no() + ",Regn-No:" + regnNo + "-" + ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ex.getMessage()));
        }
        return "";
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        return listChanges;
    }

    @Override
    public String saveAndMoveFile() {
        try {
            String msg = "";
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
                    disposeEndorsmentImpl.saveAndMoveDisposeEndosmntTax(status, getHistoryEndosrsmtsList(), applNo, empCd);
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

    public boolean isRenderSave() {
        return renderSave;
    }

    public void setRenderSave(boolean renderSave) {
        this.renderSave = renderSave;
    }

    public boolean isIsTransport() {
        return isTransport;
    }

    public void setIsTransport(boolean isTransport) {
        this.isTransport = isTransport;
    }

    public List<TaxEndorsementDobj> getHistoryEndosrsmtsList() {
        return historyEndosrsmtsList;
    }

    public void setHistoryEndosrsmtsList(List<TaxEndorsementDobj> historyEndosrsmtsList) {
        this.historyEndosrsmtsList = historyEndosrsmtsList;
    }
}
