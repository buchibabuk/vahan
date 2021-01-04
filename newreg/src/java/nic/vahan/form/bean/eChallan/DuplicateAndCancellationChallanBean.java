/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.eChallan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpSession;
import nic.vahan.form.dobj.eChallan.ChallanReportDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.eChallan.DuplicateAndCancellationChallanImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "duplicateReport")
@ViewScoped
public class DuplicateAndCancellationChallanBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(DuplicateAndCancellationChallanBean.class);
    private String applNo;
    private String reason;
    private ChallanReportDobj dobj;
    DuplicateAndCancellationChallanImpl impl;
    private List applNoList = new ArrayList();
    private List applNoForDuplicateChallanList = new ArrayList();
    private ChallanReportDobj reportDobj = new ChallanReportDobj();

    public DuplicateAndCancellationChallanBean() {
        Init();
    }

    public void Init() {
        try {
            impl = new DuplicateAndCancellationChallanImpl();
            setApplNoList(impl.getApplNo());
            setApplNoForDuplicateChallanList(impl.getApplNoToPrintDuplicateChalllan());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }
//function to print duplicate challan

    public String printDuplicateChallan() {
        HttpSession ss = Util.getSession();
        ss.setAttribute("duplcate_appl_no", applNo);
        reset();
        return "/ui/reports/formVehicleChallanReport.xhtml?faces-redirect=true";
    }
//function to cancel challan

    public String cancelChallan() {
        HttpSession ss = Util.getSession();
        ss.setAttribute("duplcate_appl_no", applNo);
        ss.setAttribute("reason", reason);
        reset();
        return "/ui/reports/formChallanCancelReport.xhtml?faces-redirect=true";

    }
//reset the field

    public void reset() {
        setApplNo("");
        setReason("");
    }

    /**
     * @return the applNo
     */
    public String getApplNo() {
        return applNo;
    }

    /**
     * @param applNo the applNo to set
     */
    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
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
     * @return the applNoList
     */
    public List getApplNoList() {
        return applNoList;
    }

    /**
     * @param applNoList the applNoList to set
     */
    public void setApplNoList(List applNoList) {
        this.applNoList = applNoList;
    }

    /**
     * @return the applNoForDuplicateChallanList
     */
    public List getApplNoForDuplicateChallanList() {
        return applNoForDuplicateChallanList;
    }

    /**
     * @param applNoForDuplicateChallanList the applNoForDuplicateChallanList to
     * set
     */
    public void setApplNoForDuplicateChallanList(List applNoForDuplicateChallanList) {
        this.applNoForDuplicateChallanList = applNoForDuplicateChallanList;
    }

    /**
     * @return the reportDobj
     */
    public ChallanReportDobj getReportDobj() {
        return reportDobj;
    }

    /**
     * @param reportDobj the reportDobj to set
     */
    public void setReportDobj(ChallanReportDobj reportDobj) {
        this.reportDobj = reportDobj;
    }
}
