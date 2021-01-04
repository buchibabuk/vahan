/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.RenewalDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.RenewalImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author acer
 */
@ManagedBean(name = "renewalBean")
@ViewScoped
public class RenewalBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(RenewalBean.class);
    private String masterLayout = "/masterLayoutPage_new.xhtml";
    private RenewalDobj renewalDobj = null;
    private RenewalDobj renewalDobjPrev;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    private Date today = new Date();
    private Date oldFitDate = new Date();
    RenewalImpl impl = new RenewalImpl();
    Owner_dobj ownerDobj = null;
    FitnessImpl fit_impl = new FitnessImpl();
    int newFit = 0;
    private String vahanMessages = null;
    private Date minDate = new Date();
    private boolean disableInspectionDetails;

    public RenewalBean() {
        renewalDobj = new RenewalDobj();
        ownerDobj = new Owner_dobj();
    }

    @PostConstruct
    public void init() {
        try {
            if (getAppl_details() != null) {
                if (appl_details.getOwnerDetailsDobj() == null) {
                    vahanMessages = "Owner Details not Found in the Database, Please Contact to the System Administrator";
                    return;
                }
                if (appl_details.getCurrent_action_cd() == TableConstants.RENEWAL_OF_REGISTRATION_ENTRY && impl.getFlowSrNo(Util.getUserStateCode(), appl_details.getPur_cd(), appl_details.getCurrent_action_cd()) == 1) {
                    setDisableInspectionDetails(true);
                    renewalDobj.setInspectedBy(renewalDobj.getInspectedBy() == null ? TableConstants.EMPTY_STRING : renewalDobj.getInspectedBy());
                    renewalDobj.setInspectedDt(renewalDobj.getInspectedDt() == null ? null : new java.sql.Date(renewalDobj.getInspectedDt().getTime()));
                }
                minDate = DateUtils.parseDate(getAppl_details().getAppl_dt());
                ownerDobj = appl_details.getOwnerDobj();
                if (ownerDobj != null) {
                    newFit = FitnessImpl.getReNewFitnessUpto(ownerDobj, TableConstants.VM_TRANSACTION_MAST_REN_REG);
                }
                //String[] currentData = impl.present_technicalDetail(getAppl_details().getRegn_no());
                if (appl_details.getOwnerDetailsDobj() != null) {
                    String prevDetails = "Fitment Date Upto [" + appl_details.getOwnerDetailsDobj().getFit_upto() + "],Vehicle Class [" + appl_details.getOwnerDetailsDobj().getVh_class_desc() + "], Vehicle Category [" + appl_details.getOwnerDetailsDobj().getVch_catg() + "]";
                    prevDetails = prevDetails.replace(",", "&nbsp; <font color=\"red\"> | </font> &nbsp;");
                    currentdata.put("Vehicle Current Technical Detail", prevDetails);
                }
                renewalDobj = impl.setRenewalApplDbToDobj(getAppl_details().getAppl_no());
                if (renewalDobj != null) {
                    renewalDobjPrev = (RenewalDobj) renewalDobj.clone();
                } else {
                    renewalDobj = new RenewalDobj();
                    renewalDobj.setApplNo(getAppl_details().getAppl_no());
                    renewalDobj.setStateCd(Util.getUserStateCode());
                    renewalDobj.setOffCd(Util.getSelectedSeat().getOff_cd());
                    renewalDobj.setRegnNo(getAppl_details().getRegn_no());
                    renewalDobj.setOldFitDt(JSFUtils.getStringToDateyyyyMMdd(appl_details.getOwnerDetailsDobj().getFit_upto()));
                    oldFitDate = renewalDobj.getOldFitDt();
                    oldFitDate = ServerUtil.dateRange(oldFitDate, 0, 0, 1);
                    TmConfigurationDobj tmConfig = Util.getTmConfiguration();
                    if (tmConfig == null) {
                        tmConfig = ServerUtil.getTmConfigurationParameters(Util.getUserStateCode());
                    }
                    if (tmConfig != null && !CommonUtils.isNullOrBlank(tmConfig.getRen_regn_from_date()) && tmConfig.getRen_regn_from_date().equalsIgnoreCase(TableConstants.REN_FROM_FIT_UPTO)) {
                        renewalDobj.setNewFitDt(ServerUtil.dateRange(ServerUtil.dateRange(oldFitDate, newFit, 0, 0), 0, 0, -1));
                    } else if (tmConfig != null && !CommonUtils.isNullOrBlank(tmConfig.getRen_regn_from_date()) && tmConfig.getRen_regn_from_date().equalsIgnoreCase(TableConstants.REN_FROM_CURRNT_DT)) {
                        renewalDobj.setNewFitDt(ServerUtil.dateRange(ServerUtil.dateRange(today, newFit, 0, 0), 0, 0, -1));
                    } else if (tmConfig != null && CommonUtils.isNullOrBlank(tmConfig.getRen_regn_from_date())) {
                        int compareDate = DateUtils.compareDates(JSFUtils.getStringToDateyyyyMMdd(appl_details.getOwnerDetailsDobj().getRegn_upto()), new Date());
                        if (compareDate == 0 || compareDate == 2) {
                            renewalDobj.setNewFitDt(ServerUtil.dateRange(ServerUtil.dateRange(oldFitDate, newFit, 0, 0), 0, 0, -1));
                        } else if (compareDate == 1) {
                            renewalDobj.setNewFitDt(ServerUtil.dateRange(ServerUtil.dateRange(today, newFit, 0, 0), 0, 0, -1));
                        }
                    }
                }
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
            }
        } catch (VahanException vme) {
            vahanMessages = vme.getMessage();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
        }
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        if (renewalDobjPrev == null) {
            return getCompBeanList();
        }
        getCompBeanList().clear();
        Compare("Inspected Date", renewalDobjPrev.getInspectedDt(), renewalDobj.getInspectedDt(), (ArrayList) getCompBeanList());
        Compare("Inspected By", renewalDobjPrev.getInspectedBy(), renewalDobj.getInspectedBy(), (ArrayList) getCompBeanList());
        Compare("New Fitness Upto", renewalDobjPrev.getNewFitDt(), renewalDobj.getNewFitDt(), (ArrayList) getCompBeanList());
        return getCompBeanList();
    }

    @Override
    public String save() {
        String return_location = "";
        List<ComparisonBean> compareChanges = compareChanges();

        try {
            if (!compareChanges.isEmpty() || renewalDobjPrev == null) { //save only when data is really changed by user
                impl.makeChangeRenewal(renewalDobj, ComparisonBeanImpl.changedDataContents(compareChanges));
                return_location = "seatwork";
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return return_location;
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            if (ownerDobj != null) {
                status.setVehicleParameters(FormulaUtils.fillVehicleParametersFromDobj(ownerDobj));
            }

            status.setCurrent_role(appl_details.getCurrent_role());

            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                    || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)
                    || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                status.setVehicleParameters(appl_details.getVehicleParameters());
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) && this.appl_details.isDocVerifyForFacelessAppl()) {
                    String[] notVerifiedDocDetails = ServerUtil.verifyDocUploaded(appl_details.getAppl_no(), getAppl_details().getCurrent_action_cd());
                    if (notVerifiedDocDetails != null) {
                        appl_details.setNotVerifiedDocdetails(notVerifiedDocDetails[1]);
                        throw new VahanException(notVerifiedDocDetails[0]);
                    }
                }
                impl.updateRenewalStatus(renewalDobj, status, ComparisonBeanImpl.changedDataContents(compareChanges()), appl_details, ownerDobj);
                if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    ServerUtil.sendSmsInFacelessService(appl_details.getAppl_no(), Util.getUserStateCode(), Util.getUserSeatOffCode(), appl_details.getRegn_no(), appl_details.getNotVerifiedDocdetails());
                    return_location = disapprovalPrint();
                } else {
                    return_location = "seatwork";
                }
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    public void fitnessValidUptoDateChangeListener(SelectEvent event) {
        try {
            if ("HP,PY".contains(appl_details.getCurrent_state_cd())) {
                renewalDobj.setNewFitDt(ServerUtil.dateRange(ServerUtil.dateRange(renewalDobj.getInspectedDt(), newFit, 0, 0), 0, 0, -1));
            }
            if ("KL".contains(appl_details.getCurrent_state_cd())) {
                int compareDate = DateUtils.compareDates(JSFUtils.getStringToDateyyyyMMdd(appl_details.getOwnerDetailsDobj().getRegn_upto()), new Date());
                if (compareDate == 0 || compareDate == 2) {
                    renewalDobj.setNewFitDt(ServerUtil.dateRange(ServerUtil.dateRange(oldFitDate, newFit, 0, 0), 0, 0, -1));
                } else if (compareDate == 1) {
                    renewalDobj.setNewFitDt(ServerUtil.dateRange(ServerUtil.dateRange(renewalDobj.getInspectedDt(), newFit, 0, 0), 0, 0, -1));
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    /**
     * @return the renewalDobj
     */
    public RenewalDobj getRenewalDobj() {
        return renewalDobj;
    }

    /**
     * @param renewalDobj the renewalDobj to set
     */
    public void setRenewalDobj(RenewalDobj renewalDobj) {
        this.renewalDobj = renewalDobj;
    }

    /**
     * @return the renewalDobjPrev
     */
    public RenewalDobj getRenewalDobjPrev() {
        return renewalDobjPrev;
    }

    /**
     * @param renewalDobjPrev the renewalDobjPrev to set
     */
    public void setRenewalDobjPrev(RenewalDobj renewalDobjPrev) {
        this.renewalDobjPrev = renewalDobjPrev;
    }

    /**
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    /**
     * @return the masterLayout
     */
    public String getMasterLayout() {
        return masterLayout;
    }

    /**
     * @param masterLayout the masterLayout to set
     */
    public void setMasterLayout(String masterLayout) {
        this.masterLayout = masterLayout;
    }

    public Date getToday() {
        return today;
    }

    public void setToday(Date today) {
        this.today = today;
    }

    /**
     * @return the vahanMessages
     */
    public String getVahanMessages() {
        return vahanMessages;
    }

    /**
     * @param vahanMessages the vahanMessages to set
     */
    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
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

    public boolean isDisableInspectionDetails() {
        return disableInspectionDetails;
    }

    public void setDisableInspectionDetails(boolean disableInspectionDetails) {
        this.disableInspectionDetails = disableInspectionDetails;
    }
}
