/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.tradecert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.FileMovementAbstractApplBean;
import nic.vahan.form.dobj.tradecert.CancelTradeCertDobj;
import nic.vahan.form.impl.tradecert.CancelTradeCertImpl;
import nic.vahan.server.CommonUtils;

/**
 *
 * @author acer
 */
@ManagedBean(name = "cancelTradeCertBean", eager = true)
@ViewScoped
public class CancelTradeCertBean extends FileMovementAbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(CancelTradeCertBean.class);
    private CancelTradeCertDobj cancelTradeCertDobj;
    private List<CancelTradeCertDobj> listTCDetailsForDealer;
    private final SessionVariables sessionVariables;
    private ArrayList<ComparisonBean> compBeanList;
    private boolean disableOwnerName;
    private boolean disableClearField;
    private boolean renderFileMovement;
    private Date currentDate;
    private String postSaveMessage;
    private boolean renderRemoveRow;
    private boolean visibleTradeDetailPanel;
    private boolean visibleCancelDetailPanel;
    private final List<CancelTradeCertDobj> listTCDetailsBackUp;
    private List<CancelTradeCertDobj> listTCDetailsToCancel;
    private boolean isOkButtonvisible;
    private boolean isSaveButtonvisible;
    private boolean disableDate;
    private boolean visibleDealerDetailPanel;
    private String headerSection;

    public CancelTradeCertBean() {

        cancelTradeCertDobj = new CancelTradeCertDobj();
        listTCDetailsForDealer = new ArrayList<>();
        sessionVariables = new SessionVariables();
        compBeanList = new ArrayList<>();
        listTCDetailsBackUp = new ArrayList<>();
        listTCDetailsToCancel = new ArrayList<>();
        renderRemoveRow = true;
        currentDate = new Date();
        isOkButtonvisible = true;
        disableDate = true;
    }

    @PostConstruct
    public void init() {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        cancelTradeCertDobj.setStateName((String) sessionMap.get("state_name"));
        CancelTradeCertImpl cancellTradeCertImpl = new CancelTradeCertImpl();
        cancelTradeCertDobj.setStateCd(sessionVariables.getStateCodeSelected());
        cancelTradeCertDobj.setOffCd(sessionVariables.getOffCodeSelected());
        cancelTradeCertDobj.setEmpCd(sessionVariables.getEmpCodeLoggedIn());

        if (appl_details.getCurrent_action_cd() == TableConstants.VM_CANCEL_TRADE_CERT_VERIFY || appl_details.getCurrent_action_cd() == TableConstants.VM_CANCEL_TRADE_CERT_APPROVE) {
            cancelTradeCertDobj.setTradeCertNo(appl_details.getRegn_no());
            cancelTradeCertDobj.setApplNo(appl_details.getAppl_no());
            try {
                cancellTradeCertImpl.getTradeCancelOrderByDetails(cancelTradeCertDobj);
                listTCDetailsToCancel = cancellTradeCertImpl.getTradeCancelDetails(cancelTradeCertDobj);
                if (!listTCDetailsToCancel.isEmpty()) {
                    visibleDealerDetailPanel = true;
                }
                cancellTradeCertImpl.getDealerDetails(cancelTradeCertDobj);
                renderRemoveRow = false;
                isOkButtonvisible = false;
                {
                }
                disableOwnerName = true;
            } catch (VahanException vme) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), null));
                return;
            }
            renderFileMovement = true;
            visibleCancelDetailPanel = true;
            headerSection = (appl_details.getCurrent_action_cd() == TableConstants.VM_CANCEL_TRADE_CERT_VERIFY) ? "VERIFY" : "APPROVE";
        } else {
            headerSection = "ENTRY";
        }

    }

    @Override
    public String save() {
        String returnLocation = "";

        try {
            CancelTradeCertImpl.save(cancelTradeCertDobj, listTCDetailsToCancel);
            if (appl_details.getCurrent_action_cd() == TableConstants.VM_CANCEL_TRADE_CERT_ENTRY) {
                showDialogBox(cancelTradeCertDobj.getApplNo());
            }
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
        return returnLocation;
    }

    public void saveForm() {

        if (this.cancelTradeCertDobj.getReasonForCancellation() == null || "".equals(this.cancelTradeCertDobj.getReasonForCancellation())) {
            JSFUtils.setFacesMessage("! Please enter 'Reason for Cancellation'.", null, JSFUtils.ERROR);
        }
        if (this.cancelTradeCertDobj.getOrderBy().equals("-1")) {
            JSFUtils.setFacesMessage("! Please select 'OrderBy'.", null, JSFUtils.ERROR);
        }
        if (this.cancelTradeCertDobj.getOrderNo() == null || "".equals(this.cancelTradeCertDobj.getOrderNo())) {
            JSFUtils.setFacesMessage("! Please enter 'Order Number'.", null, JSFUtils.ERROR);
        }
        if (this.cancelTradeCertDobj.getOrderDt() == null) {
            JSFUtils.setFacesMessage("! Please enter 'Order Date'.", null, JSFUtils.ERROR);
        }
        if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("trade_cancel_application_subview:confirmationPopup");
            PrimeFaces.current().executeScript("PF('confirmationPopup').show()");
        } else {
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("trade_cancel_application_subview:confirmationSavePopup");
            PrimeFaces.current().executeScript("PF('confSaveDlgTradeCert').show()");
        }

    }

    public void setReset() {
        setClearField();
        visibleTradeDetailPanel = false;
        visibleCancelDetailPanel = false;
        isOkButtonvisible = true;
        isSaveButtonvisible = false;
        visibleDealerDetailPanel = false;
        listTCDetailsToCancel.clear();
        listTCDetailsForDealer.clear();
    }

    public void setClearField() {
        this.cancelTradeCertDobj.setTradeCertNo("");
        this.cancelTradeCertDobj.setReasonForCancellation("");
        this.cancelTradeCertDobj.setOrderBy("");
        this.cancelTradeCertDobj.setOrderNo("");
        this.cancelTradeCertDobj.setOrderDt(null);
    }

    public void showDialogBox(String app_no) {
        if (!CommonUtils.isNullOrBlank(app_no)) {
            setPostSaveMessage("Trade Surrender Application number is : " + app_no);
        } else {
            setPostSaveMessage("Trade Surrender Application number generation fail ");
        }
        PrimeFaces.current().executeScript("PF('app_num_id').show();");
    }

    private void fillTcAndDealerDetails() {
        CancelTradeCertImpl cancellTradeCertImpl = new CancelTradeCertImpl();
        try {
            cancelTradeCertDobj = cancellTradeCertImpl.getDealerDetails(cancelTradeCertDobj);
            listTCDetailsForDealer = cancellTradeCertImpl.getTCDetails(cancelTradeCertDobj);
        } catch (VahanException vme) {
            String[] messageArr = vme.getMessage().split("#");
            for (String messageFragment : messageArr) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, messageFragment, null));
            }
        }
    }

    public void addNewSectionsToApplication() {

        fillTcAndDealerDetails();
        if (!listTCDetailsForDealer.isEmpty()) {
            visibleTradeDetailPanel = true;
            visibleCancelDetailPanel = true;
            isOkButtonvisible = false;
            visibleDealerDetailPanel = true;
        }
    }

    public void removeInListListener(AjaxBehaviorEvent event) {
        if (this.listTCDetailsForDealer.size() > 0) {
            for (Object dobjObj : this.listTCDetailsForDealer) {
                CancelTradeCertDobj Dobj = (CancelTradeCertDobj) dobjObj;
                if (!Dobj.isIsRowRemove()) {
                    this.listTCDetailsBackUp.add(Dobj);
                } else {
                    listTCDetailsToCancel.add(Dobj);
                    Dobj.setIsRowRemove(false);
                }
            }
            this.listTCDetailsForDealer.clear();
            this.listTCDetailsForDealer.addAll(listTCDetailsBackUp);
            if (this.listTCDetailsForDealer.size() <= 0) {
                visibleTradeDetailPanel = false;
            } else {
                visibleTradeDetailPanel = true;
            }
            if (this.listTCDetailsToCancel.size() <= 0) {
                visibleCancelDetailPanel = false;
                isSaveButtonvisible = false;
            } else {
                visibleCancelDetailPanel = true;
                isSaveButtonvisible = true;
            }
            this.listTCDetailsBackUp.clear();
        }
    }

    public void addInListListener(AjaxBehaviorEvent event) {
        if (this.listTCDetailsToCancel.size() > 0) {
            for (Object dobjObj : this.listTCDetailsToCancel) {
                CancelTradeCertDobj Dobj = (CancelTradeCertDobj) dobjObj;
                if (Dobj.isIsRowAdd()) {
                    this.listTCDetailsForDealer.add(Dobj);
                    Dobj.setIsRowAdd(false);
                } else {
                    listTCDetailsBackUp.add(Dobj);
                }
            }
            this.listTCDetailsToCancel.clear();
            this.listTCDetailsToCancel.addAll(listTCDetailsBackUp);
            if (this.listTCDetailsToCancel.size() <= 0) {
                visibleCancelDetailPanel = false;
                isSaveButtonvisible = false;
            } else {
                visibleCancelDetailPanel = true;
                isSaveButtonvisible = true;
            }
            if (this.listTCDetailsForDealer.size() <= 0) {
                visibleTradeDetailPanel = false;
            } else {
                visibleTradeDetailPanel = true;
            }
            this.listTCDetailsBackUp.clear();
        }
    }

    @Override
    public ArrayList<ComparisonBean> compareChanges() {
        return getCompBeanList();
    }

    @Override
    public String saveAndMoveFile() {

        String returnLocation = "seatwork";
        String fileMoveReturn;
        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            status.setAction_cd(appl_details.getCurrent_action_cd());
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                    || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {

                status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                fileMoveReturn = CancelTradeCertImpl.saveAndMoveFile(status, cancelTradeCertDobj, listTCDetailsToCancel);
                if (!CommonUtils.isNullOrBlank(fileMoveReturn)) {
                    returnLocation = "seatwork";
                }
            }
        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), null));
            return "";
        }
        return returnLocation;
    }

    public String cancel() {
        return "";
    }

    public boolean isDisableOwnerName() {
        return disableOwnerName;
    }

    public void setDisableOwnerName(boolean disableOwnerName) {
        this.disableOwnerName = disableOwnerName;
    }

    public boolean isRenderFileMovement() {
        return renderFileMovement;
    }

    public void setRenderFileMovement(boolean renderFileMovement) {
        this.renderFileMovement = renderFileMovement;
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setCompBeanList(ArrayList<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    @Override
    public ArrayList<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public boolean isDisableClearField() {
        return disableClearField;
    }

    public void setDisableClearField(boolean disableClearField) {
        this.disableClearField = disableClearField;
    }

    public String getPostSaveMessage() {
        return postSaveMessage;
    }

    public boolean isRenderRemoveRow() {
        return renderRemoveRow;
    }

    public void setRenderRemoveRow(boolean renderRemoveRow) {
        this.renderRemoveRow = renderRemoveRow;
    }

    public CancelTradeCertDobj getCancelTradeCertDobj() {
        return cancelTradeCertDobj;
    }

    public void setCancelTradeCertDobj(CancelTradeCertDobj cancelTradeCertDobj) {
        this.cancelTradeCertDobj = cancelTradeCertDobj;
    }

    public boolean isVisibleTradeDetailPanel() {
        return visibleTradeDetailPanel;
    }

    public void setVisibleTradeDetailPanel(boolean visibleTradeDetailPanel) {
        this.visibleTradeDetailPanel = visibleTradeDetailPanel;
    }

    public List<CancelTradeCertDobj> getListTCDetailsForDealer() {
        return listTCDetailsForDealer;
    }

    public boolean isIsOkButtonvisible() {
        return isOkButtonvisible;
    }

    public void setIsOkButtonvisible(boolean isOkButtonvisible) {
        this.isOkButtonvisible = isOkButtonvisible;
    }

    public boolean isVisibleCancelDetailPanel() {
        return visibleCancelDetailPanel;
    }

    public void setVisibleCancelDetailPanel(boolean visibleCancelDetailPanel) {
        this.visibleCancelDetailPanel = visibleCancelDetailPanel;
    }

    public boolean isDisableDate() {
        return disableDate;
    }

    public void setDisableDate(boolean disableDate) {
        this.disableDate = disableDate;
    }

    public void setPostSaveMessage(String postSaveMessage) {
        this.postSaveMessage = postSaveMessage;
    }

    public List<CancelTradeCertDobj> getListTCDetailsToCancel() {
        return listTCDetailsToCancel;
    }

    public boolean isIsSaveButtonvisible() {
        return isSaveButtonvisible;
    }

    public void setIsSaveButtonvisible(boolean isSaveButtonvisible) {
        this.isSaveButtonvisible = isSaveButtonvisible;
    }

    public boolean isVisibleDealerDetailPanel() {
        return visibleDealerDetailPanel;
    }

    public void setVisibleDealerDetailPanel(boolean visibleDealerDetailPanel) {
        this.visibleDealerDetailPanel = visibleDealerDetailPanel;
    }

    public String getHeaderSection() {
        return headerSection;
    }

    public void setHeaderSection(String headerSection) {
        this.headerSection = headerSection;
    }
}
