/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.eChallan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.eChallan.ChallanHoldDobj;
import nic.vahan.form.impl.eChallan.ChallanHoldImpl;

/**
 *
 * @author Administrator
 */
@ManagedBean
@ViewScoped
public class ChallanHoldBean implements Serializable {

    ChallanHoldImpl impl = new ChallanHoldImpl();
    private ChallanHoldDobj dobj = null;
    private boolean renderPanel = false;
    private String appl_no;
    private ArrayList statusList = new ArrayList();

    public ChallanHoldBean() throws Exception {

        dobj = new ChallanHoldDobj();
        dobj.setApplNoList(impl.getApplNo());
        statusList.add("ACTIVE");
        statusList.add("HOLD");

    }

    public void challanDetails() throws VahanException, Exception {
        if (!appl_no.equalsIgnoreCase(null) && !appl_no.equals("")) {
            dobj = impl.getChallanDetails(appl_no);
            if (dobj != null) {
                setRenderPanel(true);
                dobj.setApplNoList(impl.getApplNo());
            } else {

                setRenderPanel(false);
                JSFUtils.showMessagesInDialog("Alert", "Data Not Found !!!,Please Try Again", FacesMessage.SEVERITY_ERROR);
                JSFUtils.showMessage("Data Not Found !!!,Please Try Again");
                return;
            }
        } else {

            JSFUtils.showMessagesInDialog("Alert", "Data Not Found !!!,Please Try Again", FacesMessage.SEVERITY_ERROR);
            return;
        }
    }

    public Date getTodayPlusThree() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 3);
        return c.getTime();

    }

    public void holdAndActiveChallan() throws VahanException, Exception {
        String status1 = "";
        if (dobj.getHoldFee() < 0) {
            JSFUtils.showMessage("Please Enter Hold fee");
            return;
        }
        if (dobj.getChallanHoldStatus().equals("-1") && dobj.getChallanHoldStatus() == null) {
            JSFUtils.showMessage("Please Select Hold Status");
            return;
        }
        if (dobj.getHoldFrom().equals("") && dobj.getHoldFrom() == null) {
            JSFUtils.showMessage("Please Enter Hold From");
            return;
        }
        if (dobj.getHoldUpto().equals("") && dobj.getHoldUpto() == null) {
            JSFUtils.showMessage("Please Enter Hold Upto");
            return;
        }
        if (dobj.getHoldReason().equals("") && dobj.getHoldReason() == null) {
            JSFUtils.showMessage("Please Enter Hold Reason");
            return;
        }
        boolean isHold = impl.isChallanHold(appl_no);
        if (isHold) {
            dobj.setApplNoList(impl.getApplNo());
            setRenderPanel(false);
            setAppl_no("");
            JSFUtils.showMessagesInDialog("Alert", "This Challan Is Already Hold !!!", FacesMessage.SEVERITY_ERROR);
            return;
        }
        boolean flag = impl.holdAndActiveChallan(dobj, appl_no);
        if (flag) {
            dobj.setApplNoList(impl.getApplNo());
            setRenderPanel(false);
            setAppl_no("");
            JSFUtils.showMessagesInDialog("Alert", " Challan " + dobj.getChallanHoldStatus() + " Successfully!!!", FacesMessage.SEVERITY_INFO);

        } else {

            JSFUtils.showMessagesInDialog("Alert", "There Is Some Problem,Please Try Again !!!", FacesMessage.SEVERITY_ERROR);
            return;
        }


    }

    /**
     * @return the dobj
     */
    public ChallanHoldDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(ChallanHoldDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the renderPanel
     */
    public boolean isRenderPanel() {
        return renderPanel;
    }

    /**
     * @param renderPanel the renderPanel to set
     */
    public void setRenderPanel(boolean renderPanel) {
        this.renderPanel = renderPanel;
    }

    /**
     * @return the statusList
     */
    public ArrayList getStatusList() {
        return statusList;
    }

    /**
     * @param statusList the statusList to set
     */
    public void setStatusList(ArrayList statusList) {
        this.statusList = statusList;
    }

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }
}
