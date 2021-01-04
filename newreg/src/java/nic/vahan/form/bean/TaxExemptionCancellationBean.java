/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.TaxExemptionBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TaxExemptiondobj;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.TaxExemptionImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author acer
 */
@ManagedBean(name = "taxExemptionCancelBean")
@ViewScoped
public class TaxExemptionCancellationBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private TaxExemptiondobj taxExemDobj = new TaxExemptiondobj();
    private static final Logger LOGGER = Logger.getLogger(TaxExemptionBean.class);
    private Owner_dobj ownerDobj;
    private OwnerImpl ownerImpl = null;
    private OwnerDetailsDobj ownerDetail;
    private OwnerIdentificationDobj ownerIdentificationPrev = null;
    private boolean rendern = false;
    private boolean disable = false;
    private List<TaxExemptiondobj> taxExemList = null;
    private TaxExemptiondobj selectedtaxExemdobj = null;
    private String taxExemptDuration = "";
    private String reason = "";
    private List<ComparisonBean> prevChangedDataList;
    private TaxExemptiondobj taxExemDobj_dobj_prv = null;
    private List<ComparisonBean> compBeanList = new ArrayList<>();

    public TaxExemptionCancellationBean() {
        ownerImpl = new OwnerImpl();
        try {
            if (getAppl_details().getOwnerDobj() != null) {
                TaxExemptionImpl impl = new TaxExemptionImpl();
                taxExemDobj = impl.getDeleteTaxExemptDetails(getAppl_details().getRegn_no(), getAppl_details().getAppl_no());
                if (taxExemDobj != null) {
                    taxExemDobj_dobj_prv = (TaxExemptiondobj) taxExemDobj.clone();//for holding current dobj for using in the comparison.
                }
                setRendern(true);
                showDetailAction();
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-TEB-1Could not Processed due to Technical Error in Database.", "Error-TEB1-Could not Processed due to Technical Error in Database."));
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void showDetailAction() {
        FacesMessage message = null;
        TaxExemptionImpl impl = new TaxExemptionImpl();
        if (Utility.isNullOrBlank(taxExemDobj.getRegn_no())) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Enter Vehicle Number!", "Please Try Again!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        Exception e = null;
        try {
            if (taxExemDobj_dobj_prv == null) {
                List<Status_dobj> statusList = ServerUtil.applicationStatus(taxExemDobj.getRegn_no());
                if (!statusList.isEmpty()) {
                    PrimeFaces.current().dialog().showMessageDynamic(
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!",
                            "Vehicle is already Pending for Approval with Generated Application No (" + statusList.get(0).getAppl_no() + ") against Registration No (" + statusList.get(0).getRegn_no() + ")"));
                    return;
                }
            }

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
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle is Blacklisted, can not do Tax Exemption Cancel", ""));
                    return;
                }
                //Check for Vehicle NOC
                if (getOwnerDetail().getStatus().equalsIgnoreCase(TableConstants.VT_NOC_ISSUE_STATUS)) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "NOC is Taken for this Vehicle, Can not do Tax Exemption Cancel", ""));
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
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle Details Not Found!", "Vehicle Details Not Found!");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }
                if (taxExemDobj_dobj_prv == null) {
                    taxExemList = impl.getTaxExemptDetailsVT(taxExemDobj.getRegn_no());
                    System.out.println(taxExemList.size());
                } else {
                    taxExemList = new ArrayList<>();
                    taxExemList.add(taxExemDobj);
                    selectedtaxExemdobj = taxExemDobj;

                }
            }
        } catch (VahanException ve) {
            LOGGER.error(ve);
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

    public void deleteTaxExemptData() {
        String applNo = "";
        FacesMessage message = null;
        Exception exception = null;
        try {
            if (selectedtaxExemdobj == null) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select atleast one row", "Please select atleast one row");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
            taxExemptDuration = TaxExemptionImpl.checkDurationAlreadyTaxPaidDetail(taxExemDobj.getRegn_no(), selectedtaxExemdobj.getExemFromDt(), selectedtaxExemdobj.getExemTo());
            if (taxExemptDuration.length() > 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, taxExemptDuration, taxExemptDuration));
                return;
            }
            Status_dobj status_dobj = new Status_dobj();
            status_dobj.setRegn_no(taxExemDobj.getRegn_no());
            status_dobj.setPur_cd(TableConstants.TAX_EXAMPT_CANCEL_PUR_CD);
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
            selectedtaxExemdobj.setPerpose(taxExemDobj.getPerpose());

            applNo = TaxExemptionImpl.deleteTaxExemptionDetail(status_dobj, selectedtaxExemdobj);
            if (!applNo.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Application Number is Generted Successfully.Application Number is : " + applNo, ""));
                taxExemDobj = new TaxExemptiondobj();
                setRendern(false);
                PrimeFaces.current().ajax().update("formVehDetails");
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
            LOGGER.error(exception);
        }
        if (exception != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-Could Not Save Due to Technical Error in Database", "Error-Could Not Save Due to Technical Error in Database"));
        }
    }

    @Override
    public String save() {
        String return_location = "";

        try {
            if (selectedtaxExemdobj == null) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select the atleast one exemption duration for this transaction !!", "Please select the atleast one exemption duration for this transaction !!");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return return_location;
            }
            List<ComparisonBean> compareChanges = compareChanges();

            if (!compareChanges.isEmpty() || taxExemDobj_dobj_prv == null) { //save only when data is really changed by user
                TaxExemptionImpl.makeChangeTaxExemCancel(taxExemDobj, ComparisonBeanImpl.changedDataContents(compareChanges));
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
        if (selectedtaxExemdobj == null) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select the atleast one exemption duration for this transaction !!", "Please select the atleast one exemption duration for this transaction !!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return return_location;
        }
        try {
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
                taxExemptionImpl.update_CancelTaxExempt_Status(taxExemDobj, null, status, ComparisonBeanImpl.changedDataContents(compareChanges()));

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

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return this.compBeanList;
    }

    @Override
    public List<ComparisonBean> compareChanges() {

        if (taxExemDobj_dobj_prv == null) {
            return compBeanList;
        }
        compBeanList.clear();
        Compare("Purpose", taxExemDobj_dobj_prv.getPerpose(), taxExemDobj.getPerpose(), compBeanList);
        return getCompBeanList();
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

    public void reset() {
        taxExemDobj.setRegn_no("");
        setOwnerDobj(null);
        setTaxExemDobj(null);

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

    public OwnerImpl getOwnerImpl() {
        return ownerImpl;
    }

    public void setOwnerImpl(OwnerImpl ownerImpl) {
        this.ownerImpl = ownerImpl;
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

    public boolean isRendern() {
        return rendern;
    }

    public void setRendern(boolean rendern) {
        this.rendern = rendern;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public List<TaxExemptiondobj> getTaxExemList() {
        return taxExemList;
    }

    public void setTaxExemList(List<TaxExemptiondobj> taxExemList) {
        this.taxExemList = taxExemList;
    }

    /**
     * @return the selectedtaxExemdobj
     */
    public TaxExemptiondobj getSelectedtaxExemdobj() {
        return selectedtaxExemdobj;
    }

    /**
     * @param selectedtaxExemdobj the selectedtaxExemdobj to set
     */
    public void setSelectedtaxExemdobj(TaxExemptiondobj selectedtaxExemdobj) {
        this.selectedtaxExemdobj = selectedtaxExemdobj;
    }

    public String getTaxExemptDuration() {
        return taxExemptDuration;
    }

    public void setTaxExemptDuration(String taxExemptDuration) {
        this.taxExemptDuration = taxExemptDuration;
    }

    public TaxExemptiondobj getTaxExemDobj_dobj_prv() {
        return taxExemDobj_dobj_prv;
    }

    public void setTaxExemDobj_dobj_prv(TaxExemptiondobj taxExemDobj_dobj_prv) {
        this.taxExemDobj_dobj_prv = taxExemDobj_dobj_prv;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
