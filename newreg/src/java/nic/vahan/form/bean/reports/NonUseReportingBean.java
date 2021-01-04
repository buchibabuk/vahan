/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.NonUseDobj;
import nic.vahan.form.dobj.TmConfigurationNonUseDobj;
import nic.vahan.form.impl.NonUseReportingImpl;
import nic.vahan.form.impl.Util;

/**
 *
 * @author acer
 */
@ManagedBean(name = "nonUseReport")
@ViewScoped
public class NonUseReportingBean implements Serializable {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(NonUseReportingBean.class);
    private String applNo;
    private String regnNo;
    private String action_cd;
    private NonUseDobj dobj;
    private boolean nonUseReport;
    private boolean nonUseShiftingRepair;
    private boolean nonUseViolation;
    private boolean entryPart;
    private SessionVariables sessionVariables = null;
    private String radiobuttonValue;
    TmConfigurationNonUseDobj configDobj = null;

    public NonUseReportingBean() {
        try {
            HttpSession ses = Util.getSession();
            configDobj = Util.getTmConfigurationNonUse();
            reset();
            if (ses != null) {
                sessionVariables = new SessionVariables();
                if (sessionVariables == null || nic.vahan.server.CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                        || sessionVariables.getOffCodeSelected() == 0) {
                    return;
                }
                regnNo = (String) ses.getAttribute("regnNoNUB");
                dobj = NonUseReportingImpl.getDetails(regnNo, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), configDobj,radiobuttonValue);
                if (dobj == null) {
                    dobj = new NonUseDobj();
                    nonUseReport = false;
                    nonUseShiftingRepair = false;
                    JSFUtils.showMessagesInDialog("Alert!", "No Record Found for regnNo", FacesMessage.SEVERITY_WARN);
                    return;
                }
            }

        } catch (VahanException ex) {
            reset();
            JSFUtils.showMessagesInDialog("Alert", ex.getMessage(), FacesMessage.SEVERITY_INFO);
        } catch (Exception ex) {
            reset();
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert", "Their may be some problem occurred while loading for print", FacesMessage.SEVERITY_INFO);
        }
    }

    public String printDetails() {
        HttpSession ses = Util.getSession();
        ses.setAttribute("regnNoNUB", regnNo);
        if ("I".equalsIgnoreCase(radiobuttonValue)) {
            return "/ui/reports/nonuseIntimationReport.xhtml?faces-redirect=true";
        } else if ("S".equalsIgnoreCase(radiobuttonValue)) {
            return "/ui/reports/nonUseShiftingAndRepairReport.xhtml?faces-redirect=true";
        } else if ("R".equalsIgnoreCase(radiobuttonValue) && configDobj != null && configDobj.isAcknnowledge_report_in_restore()) {
            return "/ui/reports/nonuseRestoreWithdrawl.xhtml?faces-redirect=true";
        } else {
            JSFUtils.showMessagesInDialog("Alert", "This Report not Available for your State.", FacesMessage.SEVERITY_INFO);
        }
        return "";
    }

    private void reset() {
        applNo = null;
        regnNo = null;
        setDobj(null);
        nonUseReport = false;
        nonUseShiftingRepair = false;
        nonUseViolation = false;
        entryPart = true;
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
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the dobj
     */
    public NonUseDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(NonUseDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the nonUseReport
     */
    public boolean isNonUseReport() {
        return nonUseReport;
    }

    /**
     * @param nonUseReport the nonUseReport to set
     */
    public void setNonUseReport(boolean nonUseReport) {
        this.nonUseReport = nonUseReport;
    }

    /**
     * @return the nonUseShiftingRepair
     */
    public boolean isNonUseShiftingRepair() {
        return nonUseShiftingRepair;
    }

    /**
     * @param nonUseShiftingRepair the nonUseShiftingRepair to set
     */
    public void setNonUseShiftingRepair(boolean nonUseShiftingRepair) {
        this.nonUseShiftingRepair = nonUseShiftingRepair;
    }

    /**
     * @return the nonUseViolation
     */
    public boolean isNonUseViolation() {
        return nonUseViolation;
    }

    /**
     * @param nonUseViolation the nonUseViolation to set
     */
    public void setNonUseViolation(boolean nonUseViolation) {
        this.nonUseViolation = nonUseViolation;
    }

    /**
     * @return the entryPart
     */
    public boolean isEntryPart() {
        return entryPart;
    }

    /**
     * @param entryPart the entryPart to set
     */
    public void setEntryPart(boolean entryPart) {
        this.entryPart = entryPart;
    }

    public String getRadiobuttonValue() {
        return radiobuttonValue;
    }

    public void setRadiobuttonValue(String radiobuttonValue) {
        this.radiobuttonValue = radiobuttonValue;
    }
}
