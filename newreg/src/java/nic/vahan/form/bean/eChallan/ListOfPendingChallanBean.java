/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.eChallan;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.eChallan.ChallanReportDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "listOfPendingChallanBean")
@SessionScoped
public class ListOfPendingChallanBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(ListOfPendingChallanBean.class);
    private List<ChallanReportDobj> pendingChallan = new ArrayList<>();
    private List<ChallanReportDobj> challanHistoyList = new ArrayList<>();
    private List<ChallanReportDobj> challanOffenceList = new ArrayList<>();
    private ChallanReportDobj dobj = new ChallanReportDobj();
    private String regn_no;
    private int totalecords = 0;
    private String currentDate;

    public ListOfPendingChallanBean() {
    }

    public String printChallanPendingReport() {
        String returnUrl = "";
        try {
            setPendingChallan(PrintDocImpl.getPendingChallanList(regn_no));
            if (!pendingChallan.isEmpty()) {
                for (ChallanReportDobj challanReportDobj : pendingChallan) {
                    dobj.setChal_officer(challanReportDobj.getChal_officer());
                    dobj.setRto_name(challanReportDobj.getRto_name());
                    dobj.setVehicle_no(challanReportDobj.getVehicle_no());
                    dobj.setVcr_no(challanReportDobj.getVcr_no());
                }
                dobj.setRcpt_heading(ServerUtil.getRcptHeading());
                dobj.setRcpt_sub_heading(ServerUtil.getRcptSubHeading());
                dobj.setVehicle_no(regn_no.toUpperCase());
                returnUrl = "/ui/reports/formChallanClearanceReport.xhtml?faces-redirect=true";
                setRegn_no("");
            } else {
                setRegn_no("");
                JSFUtils.showMessagesInDialog("Alert", "Data Not Found.!!!", FacesMessage.SEVERITY_ERROR);
                returnUrl = "";
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return returnUrl;
    }

    public String printChallanHistoryReport() {
        String returnUrl = "";
        try {
            setChallanHistoyList(PrintDocImpl.getChallanHistoryList(regn_no));
            if (!challanHistoyList.isEmpty()) {
                dobj.setRcpt_heading(ServerUtil.getRcptHeading());
                dobj.setRcpt_sub_heading(ServerUtil.getRcptSubHeading());
                int val = 0;
                for (ChallanReportDobj challanReportDobj : challanHistoyList) {
                    val++;
                    dobj.setRegn_dt(challanReportDobj.getRegn_dt());
                    dobj.setOwner_address(challanReportDobj.getOwner_address());
                    dobj.setOwner_name(challanReportDobj.getOwner_name());
                    dobj.setRto_name(challanReportDobj.getRto_name());
                }
                setTotalecords(val);
                dobj.setVehicle_no(regn_no.toUpperCase());
                returnUrl = "/ui/reports/formChallanHistoryReport.xhtml?faces-redirect=true";
                setRegn_no("");
            } else {
                setRegn_no("");
                JSFUtils.showMessagesInDialog("Alert", "Data Not Found.!!!", FacesMessage.SEVERITY_ERROR);
                returnUrl = "";
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return returnUrl;
    }

    public String printChallaHLCReport() {
        String returnUrl = "";
        try {
            setChallanOffenceList(PrintDocImpl.getChallanHLCReportDetails(regn_no));
            if (!challanOffenceList.isEmpty()) {
                dobj.setRcpt_heading(ServerUtil.getRcptHeading());
                dobj.setRcpt_sub_heading(ServerUtil.getRcptSubHeading());
                for (ChallanReportDobj challanDobj : challanOffenceList) {
                    dobj.setOwner_name(challanDobj.getOwner_name());
                    dobj.setOwner_address(challanDobj.getOwner_address());
                    dobj.setChal_officer(challanDobj.getChal_officer());
                    dobj.setChal_date(challanDobj.getChal_date());
                    dobj.setChal_place(challanDobj.getChal_place());
                    dobj.setChal_time(challanDobj.getChal_time());
                    dobj.setWitness_address(challanDobj.getWitness_address());
                    dobj.setWitness_name(challanDobj.getWitness_name());
                    dobj.setVehicle_no(challanDobj.getVehicle_no());
                    dobj.setVh_class(challanDobj.getVh_class());
                    dobj.setVcr_no(challanDobj.getVcr_no());
                    dobj.setRto_name(challanDobj.getRto_name());
                }
                returnUrl = "/ui/reports/formChallanHLCReport.xhtml?faces-redirect=true";
                setRegn_no("");
            } else {
                setRegn_no("");
                JSFUtils.showMessagesInDialog("Alert", "Data Not Found.!!! ", FacesMessage.SEVERITY_ERROR);
                returnUrl = "";
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return returnUrl;
    }

    /**
     * @return the pendingChallan
     */
    public List<ChallanReportDobj> getPendingChallan() {
        return pendingChallan;
    }

    /**
     * @param pendingChallan the pendingChallan to set
     */
    public void setPendingChallan(List<ChallanReportDobj> pendingChallan) {
        this.pendingChallan = pendingChallan;
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
     * @return the regn_no
     */
    public String getRegn_no() {
        return regn_no;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    /**
     * @return the challanHistoyList
     */
    public List<ChallanReportDobj> getChallanHistoyList() {
        return challanHistoyList;
    }

    /**
     * @param challanHistoyList the challanHistoyList to set
     */
    public void setChallanHistoyList(List<ChallanReportDobj> challanHistoyList) {
        this.challanHistoyList = challanHistoyList;
    }

    /**
     * @return the totalecords
     */
    public int getTotalecords() {
        return totalecords;
    }

    /**
     * @param totalecords the totalecords to set
     */
    public void setTotalecords(int totalecords) {
        this.totalecords = totalecords;
    }

    /**
     * @return the challanOffenceList
     */
    public List<ChallanReportDobj> getChallanOffenceList() {
        return challanOffenceList;
    }

    /**
     * @param challanOffenceList the challanOffenceList to set
     */
    public void setChallanOffenceList(List<ChallanReportDobj> challanOffenceList) {
        this.challanOffenceList = challanOffenceList;
    }

    /**
     * @return the currentDate
     */
    public String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        currentDate = sdf.format(date);
        return currentDate;
    }

    /**
     * @param currentDate the currentDate to set
     */
    public void setCurrentDate(String currentDate) {

        this.currentDate = currentDate;
    }
}
