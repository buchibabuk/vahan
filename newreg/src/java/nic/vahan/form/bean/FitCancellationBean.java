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
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.FitnessCancellationDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.FitnessCancellationImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.Util;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;

/**
 *
 * @author ASHOK
 */
@ViewScoped
@ManagedBean(name = "fitCancellationBean")
public class FitCancellationBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(FitCancellationBean.class);
    private String message = null;
    private OwnerDetailsDobj ownerDetail;
    private FitnessDobj fitnessDobj = null;
    private List<ComparisonBean> prevChangedDataList;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private FitnessCancellationDobj cancellationDobjPrev = null;
    private FitnessCancellationDobj cancellationDobj = new FitnessCancellationDobj();
    private String vahanMessages = null;
    private String lableValue = null;

    public FitCancellationBean() {

        try {
            if (getAppl_details() == null
                    || getAppl_details().getCurrent_state_cd() == null
                    || getAppl_details().getCurrent_off_cd() == 0) {
                vahanMessages = "Something went wrong, Please try again...";
                return;
            }

            if (appl_details != null && appl_details.getOwnerDetailsDobj() != null) {
                ownerDetail = appl_details.getOwnerDetailsDobj();
            }

            if (ownerDetail != null) {

                //for Owner Identification Fields disallow typing
                if (ownerDetail.getOwnerIdentity() != null) {
                    ownerDetail.getOwnerIdentity().setFlag(true);
                    ownerDetail.getOwnerIdentity().setMobileNoEditable(true);
                }

                FitnessImpl fitImpl = new FitnessImpl();
                if (appl_details.getPur_cd() == TableConstants.TM_PURPOSE_FITNESS_CANCELLATION) {
                    lableValue = "Reason for Fitness Cancellation";
                    fitnessDobj = fitImpl.set_Fitness_appl_db_to_dobj(ownerDetail.getRegn_no(), null);
                } else {
                    lableValue = "Reason for Fitness Revocation";
                    fitnessDobj = fitImpl.set_FitnessHist_appl_db_to_dobj(appl_details.getRegn_no());
                }
                if (fitnessDobj == null) {
                    vahanMessages = "Fitness Detail is not Found, Please Contact to the System Administrator.";
                    return;
                }

                if (fitnessDobj.getFit_valid_to() != null && fitnessDobj.getFit_valid_to().compareTo(DateUtils.parseDate(DateUtils.getCurrentDate())) < 0) {
                    vahanMessages = "Fitness of Vehicle is Expired, Please Contact to the System Administrator.";
                    return;
                }

                if (fitnessDobj.getFit_valid_to() == null && fitnessDobj.getFit_nid() == null) {
                    vahanMessages = "Fitness Valid Upto and Next Inspection Date(NID) is not Found in the Database, Please Contact to the System Administrator.";
                    return;
                }


                FitnessCancellationImpl cancellationImpl = new FitnessCancellationImpl();
                if (appl_details.getPur_cd() == TableConstants.TM_PURPOSE_FITNESS_CANCELLATION) {
                    cancellationDobj = cancellationImpl.getFitnessCancellationDobj(appl_details.getAppl_no());
                } else {
                    cancellationDobj = cancellationImpl.getFitnessRevokeDetails(appl_details.getAppl_no());
                }
                if (cancellationDobj != null) {
                    cancellationDobjPrev = (FitnessCancellationDobj) cancellationDobj.clone();
                    prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                } else if (cancellationDobj == null) {
                    cancellationDobj = new FitnessCancellationDobj();
                    cancellationDobj.setAppl_no(appl_details.getAppl_no());
                    cancellationDobj.setRegn_no(appl_details.getRegn_no());
                    cancellationDobj.setState_cd(appl_details.getCurrent_state_cd());
                    cancellationDobj.setOff_cd(appl_details.getCurrent_off_cd());
                }

            }

        } catch (VahanException ve) {
            vahanMessages = "Something Went Wrong Due to " + ve.getMessage();
        } catch (Exception e) {
            vahanMessages = "Something Went Wrong, Please Contact to System Administrator.";
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    @Override
    public List<ComparisonBean> compareChanges() {

        if (this.cancellationDobjPrev == null) {
            return compBeanList;
        }
        compBeanList.clear(); // for clearing the list in case of clicking compare changes button again and again.

        Compare("Reason", cancellationDobjPrev.getReason(), cancellationDobj.getReason(), compBeanList);

        return compBeanList;
    }

    @Override
    public String save() {
        List<ComparisonBean> compareChanges = compareChanges();

        try {
            if (!compareChanges.isEmpty() || cancellationDobjPrev == null) { //save only when data is really changed by user
                FitnessCancellationImpl fitnessCancellationImpl = new FitnessCancellationImpl();
                if (appl_details.getPur_cd() == TableConstants.TM_PURPOSE_FITNESS_CANCELLATION) {
                    fitnessCancellationImpl.makeChangeFitCancel(cancellationDobj, ComparisonBeanImpl.changedDataContents(compareChanges));
                } else {
                    fitnessCancellationImpl.makeChangeFitRevoke(cancellationDobj, ComparisonBeanImpl.changedDataContents(compareChanges));
                }
            }
        } catch (VahanException ve) {
            message = ve.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
            return "";
        } catch (Exception ex) {
            message = "Error-Could Not Save Due to Technical Error in Database";
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
            return "";
        }
        return "seatwork";
    }

    @Override
    public String saveAndMoveFile() {

        String returLocation = "";

        Status_dobj status = new Status_dobj();
        status.setAppl_dt(appl_details.getAppl_dt());
        status.setAppl_no(appl_details.getAppl_no());
        status.setPur_cd(appl_details.getPur_cd());
        status.setCurrent_role(appl_details.getCurrent_action_cd());
        status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
        status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
        status.setStatus(getApp_disapp_dobj().getNew_status());
        status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());

        try {
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                    || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)
                    || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                FitnessCancellationImpl fitnessCancellationImpl = new FitnessCancellationImpl();
                if (appl_details.getPur_cd() == TableConstants.TM_PURPOSE_FITNESS_CANCELLATION) {
                    fitnessCancellationImpl.fitnessCancellation(cancellationDobj, cancellationDobjPrev, status, ComparisonBeanImpl.changedDataContents(compareChanges()), Util.getEmpCode());
                } else {
                    fitnessCancellationImpl.fitnessRevocation(cancellationDobj, cancellationDobjPrev, status, ComparisonBeanImpl.changedDataContents(compareChanges()), Util.getEmpCode(), fitnessDobj);
                }
            }

            if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                returLocation = disapprovalPrint();
            } else {
                returLocation = "seatwork";
            }

        } catch (VahanException ve) {
            message = "Exception Occured - Could not Save and Move File Due to " + ve.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
            return "";
        } catch (Exception ex) {
            message = "Error-Could Not Save and Move File Due to Technical Error in Database";
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
            return "";
        }
        return returLocation;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the ownerDetail
     */
    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    /**
     * @param ownerDetail the ownerDetail to set
     */
    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    /**
     * @return the fitnessDobj
     */
    public FitnessDobj getFitnessDobj() {
        return fitnessDobj;
    }

    /**
     * @param fitnessDobj the fitnessDobj to set
     */
    public void setFitnessDobj(FitnessDobj fitnessDobj) {
        this.fitnessDobj = fitnessDobj;
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the prevChangedDataList
     */
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    /**
     * @param prevChangedDataList the prevChangedDataList to set
     */
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    /**
     * @return the compBeanList
     */
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    /**
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    /**
     * @return the cancellationDobjPrev
     */
    public FitnessCancellationDobj getCancellationDobjPrev() {
        return cancellationDobjPrev;
    }

    /**
     * @param cancellationDobjPrev the cancellationDobjPrev to set
     */
    public void setCancellationDobjPrev(FitnessCancellationDobj cancellationDobjPrev) {
        this.cancellationDobjPrev = cancellationDobjPrev;
    }

    /**
     * @return the cancellationDobj
     */
    public FitnessCancellationDobj getCancellationDobj() {
        return cancellationDobj;
    }

    /**
     * @param cancellationDobj the cancellationDobj to set
     */
    public void setCancellationDobj(FitnessCancellationDobj cancellationDobj) {
        this.cancellationDobj = cancellationDobj;
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
     * @return the lableValue
     */
    public String getLableValue() {
        return lableValue;
    }

    /**
     * @param lableValue the lableValue to set
     */
    public void setLableValue(String lableValue) {
        this.lableValue = lableValue;
    }
}
