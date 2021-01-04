/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.reports.DownloadDispatchDobj;
import nic.vahan.form.impl.ReturnRCDispatchByHandImpl;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author afzal
 */
@ManagedBean(name = "rcReturnDispatchByHandBean")
@ViewScoped
public class ReturnRCDispatchByHandBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ReturnRCDispatchByHandBean.class);
    private String radioBtnValue = "";
    private String state_cd;
    private int off_cd;
    private String searchLabel = "";
    private String enteredNo;
    //private ArrayList returnReasonList;
    private int maxlenght;
    private String remark;
    private Date today = new Date();
    private Date frdt_cal;
    private Date todt_cal;
    private String rptSearchLabel = "";
    private String rptEnteredNo;
    private int rptMaxlenght;
    private String rptRadioBtnValue = "";
    private boolean retDtWiseReport;
    private boolean applOrRegnWiseReport;
    private DownloadDispatchDobj dobj;
    private SessionVariables sessionVariables;

    public ReturnRCDispatchByHandBean() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
            return;
        }
        setState_cd(sessionVariables.getStateCodeSelected());
        setOff_cd(sessionVariables.getOffCodeSelected());
        String userCatg = "";
        setSearchLabel("Application No");
        setRadioBtnValue("A");
        setMaxlenght(16);
        setRptRadioBtnValue("APPLNO");
        setRptMaxlenght(16);
        setRptSearchLabel("Application No");
        setRetDtWiseReport(false);
        setApplOrRegnWiseReport(true);
        this.frdt_cal = today;
        this.todt_cal = today;
    }

    public void rcRadioBtnListener() {
        this.enteredNo = "";
        if (dobj != null && !dobj.getListFileExport().isEmpty()) {
            dobj.getListFileExport().clear();
        }
        String rdBtnvalue = radioBtnValue;
        if (rdBtnvalue.equalsIgnoreCase("A")) {
            setMaxlenght(16);
            setSearchLabel("Application No");
        } else if (rdBtnvalue.equalsIgnoreCase("R")) {
            setMaxlenght(10);
            setSearchLabel("Registrationn No");
        }
    }

    public void rcHandedOverRadioBtnListener() {
        this.rptEnteredNo = "";
        String rdBtnvalue = getRptRadioBtnValue();
        if (rdBtnvalue.equalsIgnoreCase("APPLNO")) {
            setRptMaxlenght(16);
            setRptSearchLabel("Application No");
            setRetDtWiseReport(false);
            setApplOrRegnWiseReport(true);
        } else if (rdBtnvalue.equalsIgnoreCase("REGNNO")) {
            setRptMaxlenght(10);
            setRptSearchLabel("Registration No");
            setRetDtWiseReport(false);
            setApplOrRegnWiseReport(true);
        } else if (rdBtnvalue.equalsIgnoreCase("HANDEDOVERDT")) {
            setRptMaxlenght(0);
            setRptSearchLabel("");
            setRetDtWiseReport(true);
            setApplOrRegnWiseReport(false);
        }
    }

    public void getDetails() {
        ReturnRCDispatchByHandImpl impl = new ReturnRCDispatchByHandImpl();
        try {
            dobj = impl.getRCDispatchDetails(getState_cd(), getOff_cd(), radioBtnValue, enteredNo.trim().toUpperCase());
            if (dobj == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Details not found for " + searchLabel + " " + enteredNo.toUpperCase() + " !"));
                return;
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    public void saveReturnEntry() {
        ReturnRCDispatchByHandImpl impl = new ReturnRCDispatchByHandImpl();
        if (enteredNo == null || enteredNo.isEmpty()) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Application / Registration No should not be Blank !!"));
            return;
        }
        if (remark == null || remark.isEmpty()) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Remark Should Not Blank!!"));
            return;
        }
        try {

            boolean status = impl.saveRCDispatchByHand(state_cd, off_cd, enteredNo.toUpperCase(), radioBtnValue, remark.toUpperCase());
            enteredNo = "";
            if (dobj != null && !dobj.getListFileExport().isEmpty()) {
                dobj.getListFileExport().clear();
            }
            if (status) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "RC Dispatch By Hand " + remark + " Save Successfully !!"));
            } else if (!status) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Either Record not found or Already RC Dispatch By Hand !!"));
            }
            remark = null;
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }

    }

    public String printRCDispatchHandedOverEntry() {
        if (getRptRadioBtnValue().equalsIgnoreCase("APPLNO") || getRptRadioBtnValue().equalsIgnoreCase("REGNNO")) {
            if (rptEnteredNo == null || rptEnteredNo.isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Application / Registration No should not be Blank !!"));
                return "";
            }
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("enterNo", getRptEnteredNo().toUpperCase());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("radioBtnValue", getRptRadioBtnValue());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rptSearchLabel", getRptSearchLabel());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("from_date", getFrdt_cal());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("to_date", getTodt_cal());
        return "printReturnRCDispatchByHandReport";
    }

    /**
     * @return the radioBtnValue
     */
    public String getRadioBtnValue() {
        return radioBtnValue;
    }

    /**
     * @param radioBtnValue the radioBtnValue to set
     */
    public void setRadioBtnValue(String radioBtnValue) {
        this.radioBtnValue = radioBtnValue;
    }

    /**
     * @return the state_cd
     */
    public String getState_cd() {
        return state_cd;
    }

    /**
     * @param state_cd the state_cd to set
     */
    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    /**
     * @return the off_cd
     */
    public int getOff_cd() {
        return off_cd;
    }

    /**
     * @param off_cd the off_cd to set
     */
    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    /**
     * @return the searchLabel
     */
    public String getSearchLabel() {
        return searchLabel;
    }

    /**
     * @param searchLabel the searchLabel to set
     */
    public void setSearchLabel(String searchLabel) {
        this.searchLabel = searchLabel;
    }

    /**
     * @return the enteredNo
     */
    public String getEnteredNo() {
        return enteredNo;
    }

    /**
     * @param enteredNo the enteredNo to set
     */
    public void setEnteredNo(String enteredNo) {
        this.enteredNo = enteredNo;
    }

    /**
     * @return the maxlenght
     */
    public int getMaxlenght() {
        return maxlenght;
    }

    /**
     * @param maxlenght the maxlenght to set
     */
    public void setMaxlenght(int maxlenght) {
        this.maxlenght = maxlenght;
    }

    /**
     * @return the today
     */
    public Date getToday() {
        return today;
    }

    /**
     * @param today the today to set
     */
    public void setToday(Date today) {
        this.today = today;
    }

    /**
     * @return the frdt_cal
     */
    public Date getFrdt_cal() {
        return frdt_cal;
    }

    /**
     * @param frdt_cal the frdt_cal to set
     */
    public void setFrdt_cal(Date frdt_cal) {
        this.frdt_cal = frdt_cal;
    }

    /**
     * @return the todt_cal
     */
    public Date getTodt_cal() {
        return todt_cal;
    }

    /**
     * @param todt_cal the todt_cal to set
     */
    public void setTodt_cal(Date todt_cal) {
        this.todt_cal = todt_cal;
    }

    /**
     * @return the rptSearchLabel
     */
    public String getRptSearchLabel() {
        return rptSearchLabel;
    }

    /**
     * @param rptSearchLabel the rptSearchLabel to set
     */
    public void setRptSearchLabel(String rptSearchLabel) {
        this.rptSearchLabel = rptSearchLabel;
    }

    /**
     * @return the rptEnteredNo
     */
    public String getRptEnteredNo() {
        return rptEnteredNo;
    }

    /**
     * @param rptEnteredNo the rptEnteredNo to set
     */
    public void setRptEnteredNo(String rptEnteredNo) {
        this.rptEnteredNo = rptEnteredNo;
    }

    /**
     * @return the rptMaxlenght
     */
    public int getRptMaxlenght() {
        return rptMaxlenght;
    }

    /**
     * @param rptMaxlenght the rptMaxlenght to set
     */
    public void setRptMaxlenght(int rptMaxlenght) {
        this.rptMaxlenght = rptMaxlenght;
    }

    /**
     * @return the rptRadioBtnValue
     */
    public String getRptRadioBtnValue() {
        return rptRadioBtnValue;
    }

    /**
     * @param rptRadioBtnValue the rptRadioBtnValue to set
     */
    public void setRptRadioBtnValue(String rptRadioBtnValue) {
        this.rptRadioBtnValue = rptRadioBtnValue;
    }

    /**
     * @return the retDtWiseReport
     */
    public boolean isRetDtWiseReport() {
        return retDtWiseReport;
    }

    /**
     * @param retDtWiseReport the retDtWiseReport to set
     */
    public void setRetDtWiseReport(boolean retDtWiseReport) {
        this.retDtWiseReport = retDtWiseReport;
    }

    /**
     * @return the applOrRegnWiseReport
     */
    public boolean isApplOrRegnWiseReport() {
        return applOrRegnWiseReport;
    }

    /**
     * @param applOrRegnWiseReport the applOrRegnWiseReport to set
     */
    public void setApplOrRegnWiseReport(boolean applOrRegnWiseReport) {
        this.applOrRegnWiseReport = applOrRegnWiseReport;
    }

    /**
     * @return the dobj
     */
    public DownloadDispatchDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(DownloadDispatchDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the sessionVariables
     */
    public SessionVariables getSessionVariables() {
        return sessionVariables;
    }

    /**
     * @param sessionVariables the sessionVariables to set
     */
    public void setSessionVariables(SessionVariables sessionVariables) {
        this.sessionVariables = sessionVariables;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
