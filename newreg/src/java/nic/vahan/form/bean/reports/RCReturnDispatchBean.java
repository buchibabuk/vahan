/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.TmConfigurationDispatchDobj;
import nic.vahan.form.dobj.reports.DownloadDispatchDobj;
import nic.vahan.form.impl.RCReturnDispatchImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nicsi
 */
@ManagedBean(name = "rcReturnDispatchBean")
@ViewScoped
public class RCReturnDispatchBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(RCReturnDispatchBean.class);
    private String radioBtnValue = "";
    private String state_cd;
    private int off_cd;
    private String searchLabel = "";
    private String enteredNo;
    private ArrayList returnReasonList;
    private int maxlenght;
    private int reason;
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
    private TmConfigurationDispatchDobj tmConfDispatchDobj = null;

    public RCReturnDispatchBean() {
        try {
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
            returnReasonList = new ArrayList();
            String[][] data = MasterTableFiller.masterTables.VM_RCD_RETURN_REASON.getData();
            returnReasonList.add(new SelectItem("-1", "Select"));
            for (int i = 0; i < data.length; i++) {
                returnReasonList.add(new SelectItem(data[i][0], data[i][1]));
            }
            this.frdt_cal = today;
            this.todt_cal = today;
            tmConfDispatchDobj = Util.getTmConfigurationDispatch();
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
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

    public void getDetails() {
        RCReturnDispatchImpl impl = new RCReturnDispatchImpl();
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

    public void rcRPTRadioBtnListener() {
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
        } else if (rdBtnvalue.equalsIgnoreCase("RETURNDT")) {
            setRptMaxlenght(0);
            setRptSearchLabel("");
            setRetDtWiseReport(true);
            setApplOrRegnWiseReport(false);
        }
    }

    public void onDateSelect() {
        try {
            DateFormat to_dte_formatter = new SimpleDateFormat("dd-MMM-yyyy");
            SimpleDateFormat tosdf = new SimpleDateFormat("dd-MMM-yyyy");
            if (frdt_cal == null || todt_cal == null) {
                return;
            }
            java.util.Date frdate = tosdf.parse(to_dte_formatter.format(((java.util.Date) frdt_cal).getTime()));
            java.util.Date todate = tosdf.parse(to_dte_formatter.format(((java.util.Date) todt_cal).getTime()));
            try {
                if (frdate.after(todate)) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "from date must be less then OR equal to upto date!"));
                    return;
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(DailyAndConsolidatedStmtReportBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveReturnEntry() {
        RCReturnDispatchImpl impl = new RCReturnDispatchImpl();
        if (Util.getEmpCode() == null || dobj == null || tmConfDispatchDobj == null) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Error in Saving RC Return Dispatch List details,Please try again  !!"));
            return;
        }
        if (enteredNo == null || "".contains(enteredNo.trim())) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Application / Registration No should not be Blank !!"));
            return;
        }
        if (reason == -1) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please select Reason !!"));
            return;
        }
        try {
            boolean status = impl.save(dobj, reason, tmConfDispatchDobj);
            enteredNo = "";
            reason = -1;
            if (dobj != null && !dobj.getListFileExport().isEmpty()) {
                dobj.getListFileExport().clear();
            }
            if (status) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "RC Dispatch Return Save Successfully !!"));
                return;
            } else if (!status) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Either Record not found or Already RC Dispatch Returned !!"));
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

    public String printReturnEntry() {
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
        return "printReturnDispatchInfo";
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
     * @return the returnReasonList
     */
    public ArrayList getReturnReasonList() {
        return returnReasonList;
    }

    /**
     * @param returnReasonList the returnReasonList to set
     */
    public void setReturnReasonList(ArrayList returnReasonList) {
        this.returnReasonList = returnReasonList;
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
     * @return the reason
     */
    public int getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(int reason) {
        this.reason = reason;
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
}
