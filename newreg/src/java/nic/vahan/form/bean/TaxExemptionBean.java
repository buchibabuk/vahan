/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import nic.vahan.form.dobj.TaxExemptiondobj;
import nic.vahan.form.impl.TaxExemptionImpl;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import org.apache.log4j.Logger;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import static nic.vahan.server.ServerUtil.Compare;
import nic.vahan.form.impl.Util;
import org.primefaces.PrimeFaces;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.server.ServerUtil;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import javax.faces.event.AjaxBehaviorEvent;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.TaxClearDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.impl.TaxClearImpl;
import nic.vahan.form.impl.TaxServer_Impl;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "taxExemptionBean")
@ViewScoped
public class TaxExemptionBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private TaxExemptiondobj taxExemDobj = new TaxExemptiondobj();
    private static final Logger LOGGER = Logger.getLogger(TaxExemptionBean.class);
    private Owner_dobj ownerDobj;
    private OwnerImpl ownerImpl = null;
    private boolean disable = false;
    private ArrayList list_vh_class = new ArrayList();
    private ArrayList list_vm_catg = new ArrayList();
    private ArrayList list_maker_model = new ArrayList();
    private Date maxDate = new Date();
    private boolean rendern = false;
    private TaxExemptiondobj taxExemDobj_dobj_prv = null;
    private List<ComparisonBean> prevChangedDataList;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private OwnerDetailsDobj ownerDetail;
    private OwnerIdentificationDobj ownerIdentificationPrev = null;
    private String taxExemptDuration = "";
    private Date minDate = new Date();
    List pur_cd = new ArrayList();
    private TaxClearDobj taxClearDobj = new TaxClearDobj();
    private List<TaxClearDobj> taxPaidList = new ArrayList();
    private List<TaxClearDobj> taxClearList = new ArrayList();
    private List<TaxClearDobj> taxExemList = new ArrayList();
    private Map<String, Integer> purCodeList = null;

    public TaxExemptionBean() {
        ownerImpl = new OwnerImpl();
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
        try {
            if (getAppl_details() != null && getAppl_details().getOwnerDobj() != null) {
                TaxExemptionImpl impl = new TaxExemptionImpl();
                taxExemDobj = impl.getTaxExemptDetails(getAppl_details().getRegn_no(), getAppl_details().getAppl_no());
                if (taxExemDobj != null) {
                    taxExemDobj_dobj_prv = (TaxExemptiondobj) taxExemDobj.clone();//for holding current dobj for using in the comparison.
                }
                setRendern(true);
                showDetailAction();
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());

            }
            if (taxExemDobj != null && taxExemDobj.getExemFromDt() != null) {
                setMinDate(ServerUtil.dateRange(taxExemDobj.getExemFromDt(), 0, 0, 1));
                //RequestContext re = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("formTaxExemption:panelTaxExempDtls");
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-TEB-1Could not Processed due to Technical Error in Database.", "Error-TEB1-Could not Processed due to Technical Error in Database."));
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void taxExemFromDateListener(AjaxBehaviorEvent actionEvent) {
        setMinDate(ServerUtil.dateRange(taxExemDobj.getExemFromDt(), 0, 0, 1));
        //RequestContext re = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("formTaxExemption:panelTaxExempDtls");
        return;
    }

    public void saveTaxExemptData() {
        String applNo = "";
        FacesMessage message = null;
        Exception exception = null;
        try {
            if (taxExemDobj.getExemFromDt().after(taxExemDobj.getExemTo())) {

                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exempt upto date should be greater than exempt from date!!!", "Exempt upto date should be greater than exempt from date!!!");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
            taxExemptDuration = TaxExemptionImpl.checkDurationAlreadyExemptionDetail(taxExemDobj.getRegn_no(), taxExemDobj.getExemFromDt(), taxExemDobj.getExemTo());
            if (taxExemptDuration.length() > 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, taxExemptDuration, taxExemptDuration));
                return;
            }
            Status_dobj status_dobj = new Status_dobj();
            status_dobj.setRegn_no(taxExemDobj.getRegn_no());
            status_dobj.setPur_cd(TableConstants.TAX_EXAMPT_PUR_CD);
            status_dobj.setFlow_slno(1);//initial flow serial no.
            status_dobj.setFile_movement_slno(1);//initial file movement serial no.
            status_dobj.setState_cd(Util.getUserStateCode());
            status_dobj.setOff_cd(Util.getUserSeatOffCode());
            status_dobj.setEmp_cd(Long.parseLong(Util.getEmpCode()));
            status_dobj.setSeat_cd("");
            status_dobj.setCntr_id("");
            status_dobj.setStatus("N");
            status_dobj.setOffice_remark("");
            status_dobj.setPublic_remark("");
            status_dobj.setFile_movement_type("F");
            status_dobj.setUser_id(Util.getUserId());
            status_dobj.setAppl_date(ServerUtil.getSystemDateInPostgres());
            status_dobj.setUser_type("");
            status_dobj.setEntry_ip("");
            status_dobj.setEntry_status("");//for New File
            status_dobj.setConfirm_ip("");
            status_dobj.setConfirm_status("");
            status_dobj.setConfirm_date(new java.util.Date());

            applNo = TaxExemptionImpl.updateTaxExemptionDetail(status_dobj, taxExemDobj);
            if (!applNo.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Application Number is Generted Successfully.Application Number is : " + applNo, ""));
                taxExemDobj = new TaxExemptiondobj();
                setRendern(false);
                PrimeFaces.current().ajax().update("outerPanel");
                return;
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-Could Not Save Due to Technical Error in Database!!!", "Error-Could Not Save Due to Technical Error in Database!!!");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
        } catch (VahanException e) {
            exception = e;
        } catch (Exception e) {
            exception = e;
        }
        if (exception != null) {
            LOGGER.error(exception);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-Could Not Save Due to Technical Error in Database", "Error-Could Not Save Due to Technical Error in Database"));
        }
    }

    public void showDetailAction() {
        FacesMessage message = null;
        if (Utility.isNullOrBlank(taxExemDobj.getRegn_no())) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Enter Vehicle Number!", "Please Try Again!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        Exception e = null;
        try {
            if (ownerImpl != null) {
                setOwnerDetail(ownerImpl.getOwnerDetails(taxExemDobj.getRegn_no(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd()));
//                for Owner Identification Fields disallow typing
                if (getOwnerDetail() == null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration Number Not Found", ""));
                    return;
                }
                //Check for blacklisted
                BlackListedVehicleImpl obj = new BlackListedVehicleImpl();
                BlackListedVehicleDobj blacklistedStatus = obj.getBlacklistedVehicleDetails(taxExemDobj.getRegn_no(), getOwnerDetail().getChasi_no());
                if (blacklistedStatus != null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle is Blacklisted, can not do Tax Exemption", ""));
                    return;
                }
                //Check for Vehicle NOC
                if (getOwnerDetail().getStatus().equalsIgnoreCase(TableConstants.VT_NOC_ISSUE_STATUS)) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "NOC is Taken for this Vehicle, Can not do Tax Exemption", ""));
                    return;
                }
                if (getOwnerDetail().getOwnerIdentity() != null) {
                    //for Owner Identification Fields disallow typing
                    getOwnerDetail().getOwnerIdentity().setFlag(true);
                    getOwnerDetail().getOwnerIdentity().setMobileNoEditable(true);
                    if (getOwnerDetail().getOwnerIdentity().getMobile_no() == null) {
                        getOwnerDetail().getOwnerIdentity().setMobile_no(0l);
                    }
                    setOwnerIdentificationPrev((OwnerIdentificationDobj) getOwnerDetail().getOwnerIdentity().clone());
                }
                Owner_dobj owDobj = ownerImpl.getOwnerDobj(ownerDetail);
                setOwnerDobj(owDobj);
                if (getOwnerDobj() != null) {
                    setDisable(true);
                    setRendern(true);
                    //New changes to display tax details
                    TaxClearImpl tcImpl = new TaxClearImpl();
                    if (getPurCodeList() != null) {
                        getPurCodeList().clear();
                    }
                    PassengerPermitDetailDobj permitDob = TaxServer_Impl.getPermitInfoForSavedTax(owDobj);
                    purCodeList = tcImpl.getAllowedPurCodeDescr(owDobj, permitDob);
                    for (Map.Entry<String, Integer> entry : getPurCodeList().entrySet()) {
                        SelectItem item = new SelectItem(entry.getValue(), entry.getKey());
                        pur_cd.add(item);
                    }
                    taxClearDobj.setRegnno(owDobj.getRegn_no());
                    setTaxPaidList(TaxClearImpl.getTaxDetaillist(getTaxClearDobj(), getPurCodeList()));
                    setTaxClearList(TaxClearImpl.getHistoryTaxDetaillist(getTaxClearDobj(), getPurCodeList()));
                    //Akshay Jain 
                    setTaxExemList(TaxClearImpl.getExemptionTaxDetaillist(getTaxClearDobj(), getPurCodeList()));
                    //New changes to display tax details
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle Details Not Found!", "Vehicle Details Not Found!");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }
            }
        } catch (VahanException ve) {
            e = ve;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = ex;
        }
        if (e != null) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            reset();
            return;
        }
    }

    @Override
    public String save() {
        String return_location = "";

        try {
            if (taxExemDobj.getExemFromDt().after(taxExemDobj.getExemTo())) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exempt upto date should be greater than exempt from date!!!", "Exempt upto date should be greater than exempt from date!!!");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return return_location;
            }
            taxExemptDuration = TaxExemptionImpl.checkDurationAlreadyExemptionDetail(taxExemDobj.getRegn_no(), taxExemDobj.getExemFromDt(), taxExemDobj.getExemTo());
            if (taxExemptDuration.length() > 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, taxExemptDuration, taxExemptDuration));
                return return_location;
            }
            List<ComparisonBean> compareChanges = compareChanges();

            if (!compareChanges.isEmpty() || taxExemDobj_dobj_prv == null) { //save only when data is really changed by user
                TaxExemptionImpl.makeChangeTaxExem(taxExemDobj, ComparisonBeanImpl.changedDataContents(compareChanges));
            }

            return_location = "seatwork";

        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        try {
            if (taxExemDobj.getExemFromDt().after(taxExemDobj.getExemTo())) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exempt upto date should be greater than exempt from date!!!", "Exempt upto date should be greater than exempt from date!!!");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return return_location;
            }
            taxExemptDuration = TaxExemptionImpl.checkDurationAlreadyExemptionDetail(taxExemDobj.getRegn_no(), taxExemDobj.getExemFromDt(), taxExemDobj.getExemTo());
            if (taxExemptDuration.length() > 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, taxExemptDuration, taxExemptDuration));
                return return_location;
            }
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(getAppl_details().getAppl_dt());
            status.setAppl_no(getAppl_details().getAppl_no());
            status.setPur_cd(getAppl_details().getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());

            status.setCurrent_role(appl_details.getCurrent_action_cd());

            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                    || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)
                    || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                TaxExemptionImpl taxExemptionImpl = new TaxExemptionImpl();
                taxExemptionImpl.update_TaxExempt_Status(taxExemDobj, taxExemDobj_dobj_prv, status, ComparisonBeanImpl.changedDataContents(compareChanges()));

                if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    return_location = disapprovalPrint();
                } else {
                    return_location = "seatwork";
                }
            }
        } catch (VahanException vmet) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vmet.getMessage(), vmet.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    public void reset() {
        taxExemDobj.setRegn_no("");
        setOwnerDobj(null);
        setTaxExemDobj(null);

    }

    @Override
    public List<ComparisonBean> compareChanges() {

        if (taxExemDobj_dobj_prv == null) {
            return compBeanList;
        }
        compBeanList.clear();
        Compare("Tax Exampt From", taxExemDobj_dobj_prv.getExemFromDt(), taxExemDobj.getExemFromDt(), compBeanList);
        Compare("Tax Exampt Upto", taxExemDobj_dobj_prv.getExemTo(), taxExemDobj.getExemTo(), compBeanList);
        Compare("Auth By", taxExemDobj_dobj_prv.getAuthBy(), taxExemDobj.getAuthBy(), compBeanList);
        Compare("Permission Date", taxExemDobj_dobj_prv.getPermissionDt(), taxExemDobj.getPermissionDt(), compBeanList);
        Compare("Permission No", taxExemDobj_dobj_prv.getPermissionNo(), taxExemDobj.getPermissionNo(), compBeanList);
        Compare("Perpose", taxExemDobj_dobj_prv.getPerpose(), taxExemDobj.getPerpose(), compBeanList);
        return getCompBeanList();
    }

    public TaxExemptiondobj getTaxExemDobj() {
        return taxExemDobj;
    }

    public void setTaxExemDobj(TaxExemptiondobj taxExemDobj) {
        this.taxExemDobj = taxExemDobj;
    }

    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public OwnerImpl getOwnerImpl() {
        return ownerImpl;
    }

    public void setOwnerImpl(OwnerImpl ownerImpl) {
        this.ownerImpl = ownerImpl;
    }

    public ArrayList getList_vh_class() {
        return list_vh_class;
    }

    public void setList_vh_class(ArrayList list_vh_class) {
        this.list_vh_class = list_vh_class;
    }

    public ArrayList getList_vm_catg() {
        return list_vm_catg;
    }

    public void setList_vm_catg(ArrayList list_vm_catg) {
        this.list_vm_catg = list_vm_catg;
    }

    public ArrayList getList_maker_model() {
        return list_maker_model;
    }

    public void setList_maker_model(ArrayList list_maker_model) {
        this.list_maker_model = list_maker_model;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    /**
     * @return the rendern
     */
    public boolean isRendern() {
        return rendern;
    }

    /**
     * @param rendern the rendern to set
     */
    public void setRendern(boolean rendern) {
        this.rendern = rendern;
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return this.compBeanList;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return this.prevChangedDataList;
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
    }

    @Override
    public void saveEApplication() {
    }

    /**
     * @return the taxExemDobj_dobj_prv
     */
    public TaxExemptiondobj getTaxExemDobj_dobj_prv() {
        return taxExemDobj_dobj_prv;
    }

    /**
     * @param taxExemDobj_dobj_prv the taxExemDobj_dobj_prv to set
     */
    public void setTaxExemDobj_dobj_prv(TaxExemptiondobj taxExemDobj_dobj_prv) {
        this.taxExemDobj_dobj_prv = taxExemDobj_dobj_prv;
    }

    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    public OwnerIdentificationDobj getOwnerIdentificationPrev() {
        return ownerIdentificationPrev;
    }

    public void setOwnerIdentificationPrev(OwnerIdentificationDobj ownerIdentificationPrev) {
        this.ownerIdentificationPrev = ownerIdentificationPrev;
    }

    /**
     * @return the minDate
     */
    public Date getMinDate() {
        return minDate;
    }

    /**
     * @param minDate the minDate to set
     */
    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    /**
     * @return the taxClearDobj
     */
    public TaxClearDobj getTaxClearDobj() {
        return taxClearDobj;
    }

    /**
     * @param taxClearDobj the taxClearDobj to set
     */
    public void setTaxClearDobj(TaxClearDobj taxClearDobj) {
        this.taxClearDobj = taxClearDobj;
    }

    /**
     * @return the taxPaidList
     */
    public List<TaxClearDobj> getTaxPaidList() {
        return taxPaidList;
    }

    /**
     * @param taxPaidList the taxPaidList to set
     */
    public void setTaxPaidList(List<TaxClearDobj> taxPaidList) {
        this.taxPaidList = taxPaidList;
    }

    /**
     * @return the taxClearList
     */
    public List<TaxClearDobj> getTaxClearList() {
        return taxClearList;
    }

    /**
     * @param taxClearList the taxClearList to set
     */
    public void setTaxClearList(List<TaxClearDobj> taxClearList) {
        this.taxClearList = taxClearList;
    }

    /**
     * @return the taxExemList
     */
    public List<TaxClearDobj> getTaxExemList() {
        return taxExemList;
    }

    /**
     * @param taxExemList the taxExemList to set
     */
    public void setTaxExemList(List<TaxClearDobj> taxExemList) {
        this.taxExemList = taxExemList;
    }

    /**
     * @return the purCodeList
     */
    public Map<String, Integer> getPurCodeList() {
        return purCodeList;
    }

    /**
     * @param purCodeList the purCodeList to set
     */
    public void setPurCodeList(Map<String, Integer> purCodeList) {
        this.purCodeList = purCodeList;
    }
}
