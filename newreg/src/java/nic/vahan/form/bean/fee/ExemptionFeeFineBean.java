/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.fee;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.TaxExemptiondobj;
import nic.vahan.form.impl.ExemptionFeeFineImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author tranC103
 */
@ManagedBean(name = "exemBean")
@ViewScoped
public class ExemptionFeeFineBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ExemptionFeeFineBean.class);
    private String applNo;
    private boolean renderAllPanel;
    private OwnerDetailsDobj ownerDtlsDobj;
    private OwnerImpl ownerImpl = null;
    private List<TaxExemptiondobj> taxExemList = new ArrayList();
    private Date exemOrderDt;
    private String exemOrderNo;
    private String exemReason;
    private Date currentDate;
    ExemptionFeeFineImpl exemImpl = null;
    private String selectedOption;
    private boolean disableSelectedOption;
    private boolean renderApplPnl;
    private boolean renderRegnPnl;
    private String regnNo;
    private boolean renderAddBtn;
    private boolean renderModifyBtn;
    private boolean renderPushBackSeatPanel = false;
    List<TaxExemptiondobj> temp = new ArrayList<>();

    @PostConstruct
    public void postConstruct() {
        ownerImpl = new OwnerImpl();
        currentDate = new Date();
        exemImpl = new ExemptionFeeFineImpl();

        if (taxExemList.isEmpty()) {
            TaxExemptiondobj dobj1 = new TaxExemptiondobj();
            TaxExemptiondobj dobj2 = new TaxExemptiondobj();
            TaxExemptiondobj dobj3 = new TaxExemptiondobj();
            exemOrderNo = "";
            exemOrderDt = null;
            exemReason = "";
            //Fee Fine Exemtion        
            dobj1.setPur_cd(TableConstants.FEE_FINE_EXEMTION);
            dobj1.setExemHead(TableConstants.FEE_FINE_EXEMTION_HEAD);

            dobj1.setAppl_no(applNo);
            getTaxExemList().add(dobj1);

            //Tax Penalty Exemtion
            dobj2.setPur_cd(TableConstants.TAX_PENALTY_EXEMTION);
            dobj2.setExemHead(TableConstants.TAX_PENALTY_EXEMTION_HEAD);

            dobj2.setAppl_no(applNo);
            getTaxExemList().add(dobj2);

            dobj3.setPur_cd(TableConstants.TAX_INTEREST_EXEMTION);
            dobj3.setExemHead(TableConstants.TAX_INTEREST_EXEMTION_HEAD);

            dobj3.setAppl_no(applNo);
            getTaxExemList().add(dobj3);


            setRenderModifyBtn(false);
            setRenderAddBtn(true);
        }
    }

    public void selectedOptionListener(AjaxBehaviorEvent ajax) {
        if (selectedOption.equalsIgnoreCase("A")) {
            setRenderApplPnl(true);
            setRenderRegnPnl(false);
            setRenderAllPanel(false);
        } else if (selectedOption.equalsIgnoreCase("R")) {
            setRenderApplPnl(false);
            setRenderRegnPnl(true);
            setRenderAllPanel(false);
        }
    }

    public void getOwnerDetails() {
        try {
            String stateCode = Util.getUserStateCode();
            int offCode = Util.getSelectedSeat().getOff_cd();
            if (selectedOption.equalsIgnoreCase("A")) {
                if (CommonUtils.isNullOrBlank(applNo)) {
                    JSFUtils.setFacesMessage("Application Number is not valid", null, JSFUtils.WARN);
                    return;
                }
                if (!CommonUtils.isNullOrBlank(applNo) && !CommonUtils.isNullOrBlank(ServerUtil.getLatestRcptNo(applNo, stateCode, offCode))) {
                    JSFUtils.setFacesMessage("Fee Already Paid with this Application Number", null, JSFUtils.WARN);
                    return;
                }
                setOwnerDtlsDobj(ownerImpl.getOwnerDetailsFromAppl(this.applNo.trim(), stateCode, offCode));
            } else if (selectedOption.equalsIgnoreCase("R")) {
                if (CommonUtils.isNullOrBlank(regnNo)) {
                    JSFUtils.setFacesMessage("Registration Number is not valid", null, JSFUtils.WARN);
                    return;
                }
                setOwnerDtlsDobj(ownerImpl.getOwnerDetails(this.regnNo.trim(), stateCode, offCode));
            }
            if (ownerDtlsDobj != null) {
                if (CommonUtils.isNullOrBlank(applNo)) {
                    applNo = regnNo;
                }
                temp = exemImpl.getExemptionDetails(applNo);
                if (temp == null || temp.isEmpty()) {
                    getTaxExemList();
                } else {
                    ListIterator<TaxExemptiondobj> itrTaxExemList = taxExemList.listIterator();
                    while (itrTaxExemList.hasNext()) {
                        TaxExemptiondobj obj = itrTaxExemList.next();
                        for (TaxExemptiondobj tempObj : temp) {   // iterate fill value from database
                            if (obj.getPur_cd() == tempObj.getPur_cd()) {   // compare pur_cd, fetching from DB and constructor
                                itrTaxExemList.remove();
                                itrTaxExemList.add(tempObj);
                            }
                        }
                    }
                    for (TaxExemptiondobj obj : taxExemList) {
                        if (obj.getPermissionNo() != null) {     // Tax Exmption find from DB
                            exemOrderDt = new java.util.Date(obj.getPermissionDt().getTime());
                            exemOrderNo = obj.getPermissionNo();
                            exemReason = obj.getPerpose();
                        }
                    }
                    Collections.sort(taxExemList);
                    setRenderModifyBtn(true);
                    setRenderAddBtn(false);
                }
                setRenderAllPanel(true);
                setDisableSelectedOption(true);
            } else {
                JSFUtils.setFacesMessage("Owner Details Not Found", null, JSFUtils.ERROR);
            }
        } catch (VahanException e) {
            JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.ERROR);
        }
    }

    public String saveExemtionDetails() {
        boolean status = false;
        long totalExemptedAmt = 0l;
        try {
            for (TaxExemptiondobj exemdobj : getTaxExemList()) {
                exemdobj.setPermissionDt(exemOrderDt);
                exemdobj.setPermissionNo(exemOrderNo);
                exemdobj.setPerpose(exemReason);
                totalExemptedAmt = totalExemptedAmt + exemdobj.getExemAmount();
            }
            if (totalExemptedAmt == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Total Exemption can not be Zero", "Total Exemption can not be Zero"));
            } else {
                status = exemImpl.saveExemptionOrderDetails(applNo, taxExemList, Util.getUserStateCode(), Util.getUserOffCode());
                if (status) {
                    return "home";
                }
            }
        } catch (VahanException e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", e.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save Due to Technical Error in Database", "Error-File Could Not Save Due to Technical Error in Database"));
        }

        return "";
    }

    public String updateExemtionDetails() {
        boolean status = false;
        try {
            for (TaxExemptiondobj exemdobj : getTaxExemList()) {
                exemdobj.setPermissionDt(exemOrderDt);
                exemdobj.setPermissionNo(exemOrderNo);
                exemdobj.setPerpose(exemReason);
            }
            status = exemImpl.updateExemptionOrderDetails(applNo, taxExemList, Util.getUserStateCode(), Util.getUserOffCode(), Util.getEmpCode());
            if (status) {
                return "home";
            }
        } catch (VahanException e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", e.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Update Due to Technical Error in Database", "Error-File Could Not Save and Update Due to Technical Error in Database"));
        }
        return "";
    }

    public void exemptedPaneltyBlur(TaxExemptiondobj exemdobj) {
        if (exemdobj.getExemAmount() < 0) {
            exemdobj.setExemAmount(0l);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", "Exempted Amount is not valid.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    /**
     * @return the applNo
     */
    public String getApplNo() {
        return applNo;
    }

    /**
     * @param applNo the applNo to set
     */
    public void setApplNo(String applNo) {
        this.applNo = applNo;
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
     * @return the taxExemList
     */
    public List<TaxExemptiondobj> getTaxExemList() {
        return taxExemList;
    }

    /**
     * @param taxExemList the taxExemList to set
     */
    public void setTaxExemList(List<TaxExemptiondobj> taxExemList) {
        this.taxExemList = taxExemList;
    }

    /**
     * @return the exemOrderDt
     */
    public Date getExemOrderDt() {
        return exemOrderDt;
    }

    /**
     * @param exemOrderDt the exemOrderDt to set
     */
    public void setExemOrderDt(Date exemOrderDt) {
        this.exemOrderDt = exemOrderDt;
    }

    /**
     * @return the exemOrderNo
     */
    public String getExemOrderNo() {
        return exemOrderNo;
    }

    /**
     * @param exemOrderNo the exemOrderNo to set
     */
    public void setExemOrderNo(String exemOrderNo) {
        this.exemOrderNo = exemOrderNo;
    }

    /**
     * @return the exemReason
     */
    public String getExemReason() {
        return exemReason;
    }

    /**
     * @param exemReason the exemReason to set
     */
    public void setExemReason(String exemReason) {
        this.exemReason = exemReason;
    }

    /**
     * @return the currentDate
     */
    public Date getCurrentDate() {
        return currentDate;
    }

    /**
     * @param currentDate the currentDate to set
     */
    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
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
     * @return the renderApplPnl
     */
    public boolean isRenderApplPnl() {
        return renderApplPnl;
    }

    /**
     * @param renderApplPnl the renderApplPnl to set
     */
    public void setRenderApplPnl(boolean renderApplPnl) {
        this.renderApplPnl = renderApplPnl;
    }

    /**
     * @return the renderRegnPnl
     */
    public boolean isRenderRegnPnl() {
        return renderRegnPnl;
    }

    /**
     * @param renderRegnPnl the renderRegnPnl to set
     */
    public void setRenderRegnPnl(boolean renderRegnPnl) {
        this.renderRegnPnl = renderRegnPnl;
    }

    /**
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the renderAddBtn
     */
    public boolean isRenderAddBtn() {
        return renderAddBtn;
    }

    /**
     * @param renderAddBtn the renderAddBtn to set
     */
    public void setRenderAddBtn(boolean renderAddBtn) {
        this.renderAddBtn = renderAddBtn;
    }

    /**
     * @return the renderModifyBtn
     */
    public boolean isRenderModifyBtn() {
        return renderModifyBtn;
    }

    /**
     * @param renderModifyBtn the renderModifyBtn to set
     */
    public void setRenderModifyBtn(boolean renderModifyBtn) {
        this.renderModifyBtn = renderModifyBtn;
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
