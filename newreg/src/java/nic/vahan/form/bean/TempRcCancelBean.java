/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TempRcCancelDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.TempRcCancelImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import static nic.vahan.server.ServerUtil.Compare;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "tempRcCancelBean")
@ViewScoped
public class TempRcCancelBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(TempRcCancelBean.class);
    private TempRcCancelDobj dobj = new TempRcCancelDobj();
    TempRcCancelImpl impl = new TempRcCancelImpl();
    private boolean ownerPanel;
    private Date minDate = new Date();
    private String tempRegnNo;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private TempRcCancelDobj previousDobj;
    private List<ComparisonBean> prevChangedDataList;
    private boolean showApproveDisApprovePanelForSave;
    private boolean showPanelForSaveOnly;
    private boolean showOnlyRegnNo;
    private boolean showRegnNoAndApplNo;

    public TempRcCancelBean() throws CloneNotSupportedException {
        TempRcCancelDobj dobj1 = null;
        String regn_no = appl_details.getRegn_no();
        String actionCode = String.valueOf(appl_details.getCurrent_action_cd());
        if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_TEMP_RC_CANCEL_VERIFY) {
            dobj = impl.getOwnerDetailsFoVerification(regn_no);
            previousDobj = (TempRcCancelDobj) dobj.clone();
            setTempRegnNo(dobj.getTempRegnNo());
            setOwnerPanel(true);
            setShowPanelForSaveOnly(false);
            setShowApproveDisApprovePanelForSave(true);
            setShowRegnNoAndApplNo(true);
            setShowOnlyRegnNo(false);
        } else if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_TEMP_RC_CANCEL_APPROVE) {
            dobj = impl.getOwnerDetailsFoVerification(regn_no);
            setOwnerPanel(true);
            previousDobj = (TempRcCancelDobj) dobj.clone();
            setTempRegnNo(dobj.getTempRegnNo());
            setShowPanelForSaveOnly(false);
            setShowApproveDisApprovePanelForSave(true);
            setShowRegnNoAndApplNo(true);
            setShowOnlyRegnNo(false);
        } else {
            setShowPanelForSaveOnly(false);
            setShowApproveDisApprovePanelForSave(false);
            setShowRegnNoAndApplNo(false);
            setShowOnlyRegnNo(true);
        }

        try {
            prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(appl_details.getAppl_no(), TableConstants.VM_MAST_TEMP_RC_CANCEL);
        } catch (Exception e) {
        }
    }

    public void fetchOwnerDeatils() {
        boolean checkForExistance = true;
        try {
            checkForExistance = impl.chekIfAlreadyExist(tempRegnNo);
            if (!checkForExistance) {
                JSFUtils.showMessagesInDialog("Alert", "Application Already Inwarded For This Vehicle NO !!!", FacesMessage.SEVERITY_ERROR);
                return;
            }
            dobj = impl.getOwnerDetails(tempRegnNo);
            if (dobj != null) {
                setOwnerPanel(true);
                setShowPanelForSaveOnly(true);
            } else {
                JSFUtils.showMessagesInDialog("Alert", "Owner Details Not Found !!!", FacesMessage.SEVERITY_ERROR);
                setTempRegnNo("");
                return;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);

        }

    }

    public void cancelTempRC() {
        if (dobj.getReasonToCancel() == null && dobj.getReasonToCancel().equals("")) {
            JSFUtils.showMessage("Please Enter Reason Value");
            return;
        }
        if (dobj.getCancelDate() == null && dobj.getCancelDate().equals("")) {
            JSFUtils.showMessage("Please Enter Cancel Date");
            return;
        }
        try {
            Status_dobj status_dobj = new Status_dobj();
            status_dobj.setRegn_no(tempRegnNo);
            status_dobj.setPur_cd(TableConstants.VM_MAST_TEMP_RC_CANCEL);
            status_dobj.setAction_cd(TableConstants.VM_ROLE_TEMP_RC_CANCEL_ENTRY);
            status_dobj.setFlow_slno(1);
            status_dobj.setFile_movement_slno(1);
            status_dobj.setState_cd(Util.getUserStateCode());
            status_dobj.setOff_cd(Util.getSelectedSeat().getOff_cd());
            status_dobj.setEmp_cd(0);
            boolean val = impl.cancelTempRc(tempRegnNo, dobj.getReasonToCancel(), status_dobj);
            if (val) {
                JSFUtils.showMessagesInDialog("Temporary RC Cancel Application Inwarded", "Your Application No is-::" + status_dobj.getAppl_no(), FacesMessage.SEVERITY_INFO);
                setOwnerPanel(false);
                setShowPanelForSaveOnly(false);
                dobj.setTempRegnNo("");
                reset();
            } else {
                JSFUtils.showMessagesInDialog("Alert", "There Is Some Problom To Cancel Temporary RC.....", FacesMessage.SEVERITY_ERROR);
                return;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    public void reset() {
        dobj.setReasonToCancel("");
        dobj.setCancelDate(null);
        setOwnerPanel(false);
        setShowPanelForSaveOnly(false);
        setTempRegnNo("");
    }

    /**
     * @return the dobj
     */
    public TempRcCancelDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(TempRcCancelDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the ownerPanel
     */
    public boolean isOwnerPanel() {
        return ownerPanel;
    }

    /**
     * @param ownerPanel the ownerPanel to set
     */
    public void setOwnerPanel(boolean ownerPanel) {
        this.ownerPanel = ownerPanel;
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
     * @return the tempRegnNo
     */
    public String getTempRegnNo() {
        return tempRegnNo;
    }

    /**
     * @param tempRegnNo the tempRegnNo to set
     */
    public void setTempRegnNo(String tempRegnNo) {
        this.tempRegnNo = tempRegnNo;
    }

    @Override
    public String save() {
        String actionCode = String.valueOf(appl_details.getCurrent_action_cd());
        String returnLocation = "";
        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setRegn_no(appl_details.getRegn_no());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            impl.saveUpdatedDetails(actionCode, status, ComparisonBeanImpl.changedDataContents(compareChanges()), dobj, previousDobj);
            returnLocation = "seatwork";


        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return returnLocation;
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        if (previousDobj == null) {
            return compBeanList;
        }
        compBeanList.clear();
        Compare("Reason", previousDobj.getReasonToCancel(), dobj.getReasonToCancel(), compBeanList);
        return compBeanList;
    }

    @Override
    public String saveAndMoveFile() {
        String actionCode = String.valueOf(appl_details.getCurrent_action_cd());
        String returnLocation = "";
        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setRegn_no(appl_details.getRegn_no());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)) {

                if ((actionCode == null ? (String.valueOf(TableConstants.VM_ROLE_TEMP_RC_CANCEL_VERIFY)) == null : actionCode.equals(String.valueOf(TableConstants.VM_ROLE_TEMP_RC_CANCEL_VERIFY))) || (actionCode == null ? (String.valueOf(TableConstants.VM_ROLE_TEMP_RC_CANCEL_APPROVE)) == null : actionCode.equals(String.valueOf(TableConstants.VM_ROLE_TEMP_RC_CANCEL_APPROVE)))) {
                    impl.movetoapprove(actionCode, status, ComparisonBeanImpl.changedDataContents(compareChanges()), dobj, previousDobj);
                    returnLocation = "seatwork";
                }
            }
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                status.setPrev_action_cd_selected(app_disapp_dobj.getPre_action_code());
                impl.reback(actionCode, status, ComparisonBeanImpl.changedDataContents(compareChanges()), dobj, previousDobj);
                returnLocation = "seatwork";
            }
        } catch (VahanException e) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", e.toString());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", "Problem in saving the details, Please contact Administrator!");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return returnLocation;
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

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    /**
     * @return the showApproveDisApprovePanelForSave
     */
    public boolean isShowApproveDisApprovePanelForSave() {
        return showApproveDisApprovePanelForSave;
    }

    /**
     * @param showApproveDisApprovePanelForSave the
     * showApproveDisApprovePanelForSave to set
     */
    public void setShowApproveDisApprovePanelForSave(boolean showApproveDisApprovePanelForSave) {
        this.showApproveDisApprovePanelForSave = showApproveDisApprovePanelForSave;
    }

    /**
     * @return the showPanelForSaveOnly
     */
    public boolean isShowPanelForSaveOnly() {
        return showPanelForSaveOnly;
    }

    /**
     * @param showPanelForSaveOnly the showPanelForSaveOnly to set
     */
    public void setShowPanelForSaveOnly(boolean showPanelForSaveOnly) {
        this.showPanelForSaveOnly = showPanelForSaveOnly;
    }

    /**
     * @return the showOnlyRegnNo
     */
    public boolean isShowOnlyRegnNo() {
        return showOnlyRegnNo;
    }

    /**
     * @param showOnlyRegnNo the showOnlyRegnNo to set
     */
    public void setShowOnlyRegnNo(boolean showOnlyRegnNo) {
        this.showOnlyRegnNo = showOnlyRegnNo;
    }

    /**
     * @return the showRegnNoAndApplNo
     */
    public boolean isShowRegnNoAndApplNo() {
        return showRegnNoAndApplNo;
    }

    /**
     * @param showRegnNoAndApplNo the showRegnNoAndApplNo to set
     */
    public void setShowRegnNoAndApplNo(boolean showRegnNoAndApplNo) {
        this.showRegnNoAndApplNo = showRegnNoAndApplNo;
    }
}
