/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.ApproveDisapprove_dobj;
import nic.vahan.form.dobj.common.Appl_Details_Dobj;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nicsi
 */
public class FileMovementAbstractApplBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(AbstractApplBean.class);
    protected ApproveDisapprove_dobj app_disapp_dobj = null;
    protected Appl_Details_Dobj appl_details = null;
    protected Map<String, String> currentdata = new HashMap();
    protected boolean render = false;
    protected boolean disappPrint = false;
    private boolean disableSaveButton;
    private final SessionVariables sessionVariables = new SessionVariables();

    public FileMovementAbstractApplBean() {

        try {
            SeatAllotedDetails selectedSeat = (SeatAllotedDetails) Util.getSession().getAttribute("SelectedSeat");
            app_disapp_dobj = new ApproveDisapprove_dobj();
            String[][] data = MasterTableFiller.masterTables.VM_REASON.getData();
            for (int i = 0; i < data.length; i++) {
                app_disapp_dobj.getReasonList().add(new SelectItem(data[i][0], data[i][1]));
            }
            if (selectedSeat != null) {

                appl_details = new Appl_Details_Dobj();
                appl_details.setAppl_no(selectedSeat.getAppl_no());
                appl_details.setPur_cd(selectedSeat.getPur_cd());
                appl_details.setCurrent_action_cd(selectedSeat.getAction_cd());
                appl_details.setCurrent_role(selectedSeat.getAction_cd() % 1000);// for extracting role_code from action_code
                appl_details.setAppl_dt(selectedSeat.getAppl_dt() != null ? selectedSeat.getAppl_dt() : "");
                appl_details.setCurrent_office_remark(selectedSeat.getOffice_remark());
                appl_details.setCurrent_public_remark(selectedSeat.getRemark_for_public());
                appl_details.setCurrent_status(selectedSeat.getStatus());
                appl_details.setCurrent_cntr_id(selectedSeat.getCntr_id());
                appl_details.setPur_desc(selectedSeat.getPurpose_descr());
                appl_details.setRegn_no(selectedSeat.getRegn_no());
                appl_details.setCurrent_state_cd(Util.getUserStateCode());
                appl_details.setCurrent_off_cd(Util.getSelectedSeat().getOff_cd());

                app_disapp_dobj.setNew_status(TableConstants.STATUS_COMPLETE);
                app_disapp_dobj.setPrevRoleLabelValue(ServerUtil.getPrevRole(appl_details.getCurrent_action_cd(), appl_details.getPur_cd(), appl_details.getCurrent_state_cd()));

                if (app_disapp_dobj.getPrevRoleLabelValue().isEmpty()) {
                    app_disapp_dobj.setRenderRevert(false);
                } else {
                    app_disapp_dobj.setRenderRevert(true);
                }

                if (appl_details != null && appl_details.getCurrent_status() != null) {
                    if (appl_details.getCurrent_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                        setDisappPrint(true);
                        app_disapp_dobj.setAssignedReasons(ServerUtil.getReasonsForHolding(appl_details.getAppl_no()));
                    } else {
                        setDisappPrint(false);
                    }
                }
            }
            if (sessionVariables.getStateCodeSelected().equals("OR")) {
                app_disapp_dobj.setRenderRevert(false);
                app_disapp_dobj.setRenderNotRecommend(true);
            }
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public String getKeyData(String str) {
        return currentdata.get(str);
    }

    public List getCurrentInfoLabel() {
        ArrayList<String> arr = new ArrayList<>();
        java.util.Set s = currentdata.keySet();
        Iterator itr = s.iterator();
        for (Iterator it = s.iterator(); it.hasNext();) {
            Object object = it.next();
            arr.add(String.valueOf(object));
        }

        return arr;
    }

    public void reverBackListener(AjaxBehaviorEvent e) {  //OK
        if (app_disapp_dobj.getNew_status().trim().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING)) {
            PrimeFaces.current().executeScript("PF('reasonDlg').show();");
        } else {
            PrimeFaces.current().executeScript("PF('reasonDlg').hide();");
        }
        if (app_disapp_dobj.getNew_status().equalsIgnoreCase(TableConstants.STATUS_REVERT)) {
            app_disapp_dobj.setRenderRevert(true);
            app_disapp_dobj.setRenderRevertList(true);
        } else {
            app_disapp_dobj.setRenderRevertList(false);
        }

    }

    public void saveWithReason() { ///OK
        String selectedReasons = "";
        if (app_disapp_dobj.getNew_status().trim().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING)) {
            for (Object resn : app_disapp_dobj.getSelectedReasonList()) {
                selectedReasons = selectedReasons + String.valueOf(resn) + ",";
            }
            if (selectedReasons.endsWith(",")) {
                selectedReasons = selectedReasons.substring(0, selectedReasons.length() - 1);
            }
            app_disapp_dobj.setPublic_remark(selectedReasons);
            PrimeFaces.current().executeScript("PF('reasonDlg').hide();");
        }
    }

    public String disapprovalPrint() {   //OK
        if (appl_details != null) {
            HttpSession ses = Util.getSession();
            ses.setAttribute("applNo", appl_details.getAppl_no());
            ses.setAttribute("regnNo", appl_details.getRegn_no());
            ses.setAttribute("purCode", appl_details.getPur_cd());
            return "disAppNotice";
        }
        return "";
    }

    /**
     * @return the app_disapp_dobj
     */
    public ApproveDisapprove_dobj getApp_disapp_dobj() {
        return app_disapp_dobj;
    }

    /**
     * @param app_disapp_dobj the app_disapp_dobj to set
     */
    public void setApp_disapp_dobj(ApproveDisapprove_dobj app_disapp_dobj) {
        this.app_disapp_dobj = app_disapp_dobj;
    }

    /**
     * @return the appl_details
     */
    public Appl_Details_Dobj getAppl_details() {
        return appl_details;
    }

    /**
     * @param appl_details the appl_details to set
     */
    public void setAppl_details(Appl_Details_Dobj appl_details) {
        this.appl_details = appl_details;
    }

    /**
     * @return the currentdata
     */
    public Map<String, String> getCurrentdata() {
        return currentdata;
    }

    /**
     * @param currentdata the currentdata to set
     */
    public void setCurrentdata(Map<String, String> currentdata) {
        this.currentdata = currentdata;
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

    public boolean isDisappPrint() {
        return disappPrint;
    }

    public void setDisappPrint(boolean disappPrint) {
        this.disappPrint = disappPrint;
    }

    public boolean isDisableSaveButton() {
        return disableSaveButton;
    }

    public void setDisableSaveButton(boolean disableSaveButton) {
        this.disableSaveButton = disableSaveButton;
    }
}
