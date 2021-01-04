/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.eChallan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpSession;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.eChallan.ChallanReportDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "echallanReport")
@ViewScoped
public class ChallanReportBean extends AbstractApplBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(ChallanReportBean.class);
    private ChallanReportDobj dobj = null;
    private String appl_no;
    private String actionCode;
    private String reportHeading;
    private boolean renderQr = false;
    private String numToWord;
    private List<ChallanReportDobj> accusedOffenceList = new ArrayList<>();

    public ChallanReportBean() {

        getDetails();
    }

    public void getDetails() {

        try {
            Utility util = new Utility();
            //String duplicate_appl_no = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("duplicate_appl_no").toString();
            HttpSession ses = Util.getSession();
            actionCode = String.valueOf(appl_details.getCurrent_action_cd());
            if (actionCode.equals(String.valueOf(TableConstants.VM_ROLE_ENFORCEMENT_ENTRY))) {
                appl_no = (String) ses.getAttribute("eChallan_appl_no");
                setReportHeading("Vehicle Checking Report");
            }
            if (actionCode.equals(String.valueOf(TableConstants.VM_ROLE_ENFORCE_DUP_AND_CHALLAN_CANCEL))) {
                appl_no = (String) ses.getAttribute("duplcate_appl_no");
                setReportHeading("Duplicate Vehicle Checking Report");

            }


            dobj = new ChallanReportDobj();

            dobj = PrintDocImpl.printChallanReport(appl_no);
            setAccusedOffenceList(PrintDocImpl.getOffenceAndAccusedDetails(appl_no));

            if (dobj != null) {
                if (dobj.getVcr_no() != null) {
                    setRenderQr(true);
                }
                int a = Integer.parseInt(dobj.getTotalFee() + "");
                dobj.setRcpt_heading(ServerUtil.getRcptHeading());
                dobj.setRcpt_sub_heading(ServerUtil.getRcptSubHeading());
                setNumToWord(util.ConvertNumberToWords(a));
                int b = Integer.parseInt(dobj.getAdCompFee() + "");
                dobj.setRcpt_heading(ServerUtil.getRcptHeading());
                dobj.setRcpt_sub_heading(ServerUtil.getRcptSubHeading());
                setNumToWord(util.ConvertNumberToWords(b));
            } else {
                JSFUtils.showMessagesInDialog("Alert", "Duplicate Challan Details Not Found", FacesMessage.SEVERITY_ERROR);
                return;
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public String redirect() {
        String backButton = "";
        if (actionCode.equals(String.valueOf(TableConstants.VM_ROLE_ENFORCEMENT_ENTRY))) {
            backButton = "/ui/eChallan/ChallanEntryForm.xhtml?faces-redirect=true";
        }
        if (actionCode.equals(String.valueOf(TableConstants.VM_ROLE_ENFORCE_DUP_AND_CHALLAN_CANCEL))) {
            backButton = "/ui/eChallan/formDuplicateandCancellation.xhtml?faces-redirect=true";

        }
        return backButton;
    }

    /**
     * @return the dobj
     */
    public ChallanReportDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(ChallanReportDobj dobj) {
        this.dobj = dobj;
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

    /**
     * @return the actionCode
     */
    public String getActionCode() {
        return actionCode;
    }

    /**
     * @param actionCode the actionCode to set
     */
    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    /**
     * @return the reportHeading
     */
    public String getReportHeading() {
        return reportHeading;
    }

    /**
     * @param reportHeading the reportHeading to set
     */
    public void setReportHeading(String reportHeading) {
        this.reportHeading = reportHeading;
    }

    /**
     * @return the renderQr
     */
    public boolean isRenderQr() {
        return renderQr;
    }

    /**
     * @param renderQr the renderQr to set
     */
    public void setRenderQr(boolean renderQr) {
        this.renderQr = renderQr;
    }

    /**
     * @return the accusedOffenceList
     */
    public List<ChallanReportDobj> getAccusedOffenceList() {
        return accusedOffenceList;
    }

    /**
     * @param accusedOffenceList the accusedOffenceList to set
     */
    public void setAccusedOffenceList(List<ChallanReportDobj> accusedOffenceList) {
        this.accusedOffenceList = accusedOffenceList;
    }

    public String getNumToWord() {
        return numToWord;
    }

    public void setNumToWord(String numToWord) {
        this.numToWord = numToWord;
    }
}
