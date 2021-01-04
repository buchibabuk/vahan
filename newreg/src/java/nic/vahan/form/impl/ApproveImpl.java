/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import org.primefaces.component.inputtextarea.InputTextarea;
import org.primefaces.component.panel.Panel;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.component.selectoneradio.SelectOneRadio;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "approveImpl")
@ViewScoped
public class ApproveImpl implements Serializable { // need to make this class ApproveBean and particular ApproveImpl

    private InputTextarea public_remark = new InputTextarea();
    private InputTextarea office_remark = new InputTextarea();
    private SelectOneRadio newStatus = new SelectOneRadio();
    private Panel panelMoveCancel = new Panel();
    private Panel panelAppDisapp = new Panel();
    private SelectOneRadio buttonMoveCancelRadio = new SelectOneRadio();
    private SelectOneMenu prevAction = new SelectOneMenu();
    private UISelectItem revertBack = new UISelectItem();
    private boolean render;
    private List reasonList;
    private List selectedReasonList;

    public ApproveImpl() {
        reasonList = new ArrayList();
        selectedReasonList = new ArrayList();

        String[][] data = MasterTableFiller.masterTables.VM_REASON.getData();
        for (int i = 0; i < data.length; i++) {
            reasonList.add(new SelectItem(data[i][0], data[i][1]));
        }
    }

    @PostConstruct
    public void init() {
        prevAction.setRendered(false);
        newStatus.setValue(TableConstants.STATUS_COMPLETE); // old value was "Y"
    }

    public void statusValueChangeListener(AjaxBehaviorEvent event) {
        UIComponent comp = event.getComponent();
        UISelectItem rBack = (UISelectItem) comp.findComponent("revBack");
    }

    public void reverBackListener(AjaxBehaviorEvent e) {
        if (getNewStatus().getValue().toString().trim().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING)) {
            PrimeFaces.current().executeScript("PF('reasonDlg').show();");
        } else {
            PrimeFaces.current().executeScript("PF('reasonDlg').hide();");
        }
        if (getNewStatus().getValue().toString().trim().equalsIgnoreCase(TableConstants.STATUS_REVERT)) {
            prevAction.setRendered(true);
        } else {
            prevAction.setRendered(false);
        }
    }

    public void saveWithReason() {
        String selectedReasons = "";
        if (getNewStatus().getValue().toString().trim().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING)) {
            for (Object resn : selectedReasonList) {
                selectedReasons = selectedReasons + String.valueOf(resn) + ",";
            }
            if (selectedReasons.endsWith(",")) {
                selectedReasons = selectedReasons.substring(0, selectedReasons.length() - 1);
            }
            public_remark.setValue(selectedReasons);
        }
    }

    /**
     * @return the public_remark
     */
    public InputTextarea getPublic_remark() {
        return public_remark;
    }

    /**
     * @param public_remark the public_remark to set
     */
    public void setPublic_remark(InputTextarea public_remark) {
        this.public_remark = public_remark;
    }

    /**
     * @return the office_remark
     */
    public InputTextarea getOffice_remark() {
        return office_remark;
    }

    /**
     * @param office_remark the office_remark to set
     */
    public void setOffice_remark(InputTextarea office_remark) {
        this.office_remark = office_remark;
    }

    /**
     * @return the newStatus
     */
    public SelectOneRadio getNewStatus() {
        return newStatus;
    }

    /**
     * @param newStatus the newStatus to set
     */
    public void setNewStatus(SelectOneRadio newStatus) {
        this.newStatus = newStatus;
    }

    /**
     * @return the panelMoveCancel
     */
    public Panel getPanelMoveCancel() {
        return panelMoveCancel;
    }

    /**
     * @param panelMoveCancel the panelMoveCancel to set
     */
    public void setPanelMoveCancel(Panel panelMoveCancel) {
        this.panelMoveCancel = panelMoveCancel;
    }

    /**
     * @return the panelAppDisapp
     */
    public Panel getPanelAppDisapp() {
        return panelAppDisapp;
    }

    /**
     * @param panelAppDisapp the panelAppDisapp to set
     */
    public void setPanelAppDisapp(Panel panelAppDisapp) {
        this.panelAppDisapp = panelAppDisapp;
    }

    /**
     * @return the buttonMoveCancelRadio
     */
    public SelectOneRadio getButtonMoveCancelRadio() {
        return buttonMoveCancelRadio;
    }

    /**
     * @param buttonMoveCancelRadio the buttonMoveCancelRadio to set
     */
    public void setButtonMoveCancelRadio(SelectOneRadio buttonMoveCancelRadio) {
        this.buttonMoveCancelRadio = buttonMoveCancelRadio;
    }

    /**
     * @return the render
     */
    public boolean isRender() {
        return render;
    }

    /**
     * @param render the render to set
     */
    public void setRender(boolean render) {
        this.render = render;
    }

    /**
     * @return the prevAction
     */
    public SelectOneMenu getPrevAction() {
        return prevAction;
    }

    /**
     * @param prevAction the prevAction to set
     */
    public void setPrevAction(SelectOneMenu prevAction) {
        this.prevAction = prevAction;
    }

    /**
     * @return the revertBack
     */
    public UISelectItem getRevertBack() {
        return revertBack;
    }

    /**
     * @param revertBack the revertBack to set
     */
    public void setRevertBack(UISelectItem revertBack) {
        this.revertBack = revertBack;
    }

    public List getReasonList() {
        return reasonList;
    }

    public void setReasonList(List reasonList) {
        this.reasonList = reasonList;
    }

    public List getSelectedReasonList() {
        return selectedReasonList;
    }

    public void setSelectedReasonList(List selectedReasonList) {
        this.selectedReasonList = selectedReasonList;
    }
}
