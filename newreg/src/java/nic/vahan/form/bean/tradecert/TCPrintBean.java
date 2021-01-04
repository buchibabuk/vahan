/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.tradecert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.java.util.CommonUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.tradecert.TCPrintDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nicsi
 */
@ManagedBean(name = "TCPrintBean")
@ViewScoped
public class TCPrintBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(IssueTradeCertPrintBean.class);
    private List<TCPrintDobj> printCertDobjForTCNoList = new ArrayList<>();
    private List<TCPrintDobj> printCertDobjForPendingList = new ArrayList<>();
    private List<TCPrintBean> filteredSeat = null;
    private String main_header_label;
    private boolean printTc;
    private String tcRadiobtn;
    private boolean printTcbyTradeCertNo;
    private String tc_no;
    private List<TCPrintDobj> selectedCertDobj = new ArrayList<>();
    private boolean alreadyPrintedTc = false;
    private String dealerName;
    private Date validUpto;
    private Date issueDate;
    private boolean tcForEachVehCatg;
    private boolean displayFuel;
    private final SessionVariables sessionVariables;
    private final boolean forAlreadyPrintedTC;
    private boolean renderVehClass;
    private boolean doNotShowNoOfVehicles;
    private TmConfigurationDobj tmConfigDobj;
    private boolean printTcByApplicationNo;
    private String applNo;

    public TCPrintBean() {
        sessionVariables = new SessionVariables();
        if (sessionVariables.getUserCatgForLoggedInUser().equalsIgnoreCase("A")) {    //for OFFICE ADMIN (printing already printed TC by number)
            forAlreadyPrintedTC = true;
        } else {
            forAlreadyPrintedTC = false;
        }
        try {
            tmConfigDobj = Util.getTmConfiguration();
        } catch (VahanException ex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            return;
        }
        if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {    //// for Gujarat
            displayFuel = true;
        }
        if (tmConfigDobj.getTmTradeCertConfigDobj().isNoOfVehiclesNotToBeShown()) {  ///  For Chhattisgarh
            doNotShowNoOfVehicles = true;
        }
        if (tmConfigDobj.getTmTradeCertConfigDobj().isVehClassToBeRendered()) {  /// for Odisha
            renderVehClass = true;
        }
    }

    @PostConstruct
    public void init() {
        setMain_header_label("Trade Certificate Print Form");
        if (forAlreadyPrintedTC) {
            setPrintTc(false);   //printing mode radio chooser not to be display
            setTcRadiobtn("TCNO");
            setPrintTcbyTradeCertNo(true);
        } else {                                                                      //for OTHER USERS (printing already printed TC by number)
            setTcRadiobtn((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("radioBtnValue"));
            if (getTcRadiobtn() == null) {
                setPrintTc(true);
                setTcRadiobtn("APPLNO");//changed it to Application no initially it was tradeCertificate No. i.e TCNO
                setPrintTcByApplicationNo(true);
                setPrintTcbyTradeCertNo(false);
            } else {
                setPrintTc(true);
                setTcRadiobtn(getTcRadiobtn());
                tcRadioBtnListener();
            }
        }

        try {
            TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
            if (tmConfigurationDobj.isTcNoForEachVehCatg()) {
                tcForEachVehCatg = true;
            }
        } catch (VahanException vahanException) {
            LOGGER.error(vahanException.getMessage() + " " + vahanException.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Exception in setting trade certificate number for each vehicle category"));
        }

    }

    public void tcRadioBtnListener() {
        getPrintCertDobjForTCNoList().clear();
        getPrintCertDobjForPendingList().clear();
        if (getSelectedCertDobj() != null && !getSelectedCertDobj().isEmpty()) {
            getSelectedCertDobj().clear();
        }
        String radioBtnvalue = tcRadiobtn;
        try {
            if (radioBtnvalue.equalsIgnoreCase("PNDNGTC")) {
                setPrintTcbyTradeCertNo(false);
                setAlreadyPrintedTc(false);
                setPrintTcByApplicationNo(false);
                setTc_no("");
                this.setListBeans(PrintDocImpl.getLast15DaysTCPrintDocsDetails(doNotShowNoOfVehicles, displayFuel));
            } else if (radioBtnvalue.equalsIgnoreCase("PRTTC")) {
                setPrintTcbyTradeCertNo(false);
                setAlreadyPrintedTc(true);
                setPrintTcByApplicationNo(false);
                setTc_no("");
                ArrayList<TCPrintDobj> list = PrintDocImpl.getTCTodayPrintedDocsDetails(doNotShowNoOfVehicles, displayFuel);
                if (list.isEmpty()) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "There is no printed certificate as on today."));
                } else {
                    this.setListBeans(list);
                }
            } else if (radioBtnvalue.equalsIgnoreCase("TCNO")) {
                setPrintTcbyTradeCertNo(true);
                setAlreadyPrintedTc(false);
                setPrintTcByApplicationNo(false);
                setDealerName("");
            } else if (radioBtnvalue.equalsIgnoreCase("APPLNO")) {
                setPrintTcByApplicationNo(true);
                setPrintTcbyTradeCertNo(false);
                setAlreadyPrintedTc(false);
                setDealerName("");
                setTc_no("");
            }
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Exception in fetching data from database."));
        }

    }

    public void fetchTCDetailsByNumber() {
        tc_no = tc_no.trim().toUpperCase();
        getPrintCertDobjForTCNoList().clear();
        getPrintCertDobjForPendingList().clear();
        if (getSelectedCertDobj() != null && !getSelectedCertDobj().isEmpty()) {
            getSelectedCertDobj().clear();
        }
        if (getTc_no().trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Trade Certificate Number should not be left blank."));
            return;
        }

        try {
            List<TCPrintDobj> list = PrintDocImpl.getTcDetailsIfTcNoExists(tc_no.trim().toUpperCase(), sessionVariables.getUserCatgForLoggedInUser());

            if (list.isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Trade Certificate Number does not exist or not authorized to print for this number."));
            } else {
                setDealerName(((TCPrintDobj) list.get(0)).getDealerName());
                setValidUpto(((TCPrintDobj) list.get(0)).getValidUpto());
                setIssueDate(((TCPrintDobj) list.get(0)).getIssueDate());
                this.setListBeans(list);
            }

        } catch (Exception e) {
            LOGGER.error(e);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Exception in fetching data from database for entered trade certificate number."));
        }
    }

    public void fetchTCDetailsByApplicationNumber() {
        if (CommonUtils.isNullOrBlank(applNo)) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", "Application Number should not be left blank."));
            return;
        }
        applNo = applNo.trim();
        getPrintCertDobjForTCNoList().clear();
        getPrintCertDobjForPendingList().clear();
        if (getSelectedCertDobj() != null && !getSelectedCertDobj().isEmpty()) {
            getSelectedCertDobj().clear();
        }
        try {
            List<TCPrintDobj> list = PrintDocImpl.getTcDetailsIfApplicationNoExists(applNo, sessionVariables.getUserCatgForLoggedInUser());
            if (list.isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", "Application Number does not exist or not authorized to print for this number."));
            } else {
                setDealerName(((TCPrintDobj) list.get(0)).getDealerName());
                setValidUpto(((TCPrintDobj) list.get(0)).getValidUpto());
                setIssueDate(((TCPrintDobj) list.get(0)).getIssueDate());
                this.setListBeans(list);
            }

        } catch (VahanException e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", e.getMessage()));
        }
    }

    public void setListBeans(List<TCPrintDobj> listDobjs) {
        if (getTcRadiobtn().equals("TCNO")) {
            setPrintCertDobjForTCNoList(listDobjs);
        } else if (getTcRadiobtn().equalsIgnoreCase("APPLNO")) {
            setPrintCertDobjForTCNoList(listDobjs);
        } else {
            setPrintCertDobjForPendingList(listDobjs);
        }
    }

    public void confirmPrintTC(TCPrintDobj printTC) {
        if (getSelectedCertDobj() != null && !getSelectedCertDobj().isEmpty()) {
            getSelectedCertDobj().clear();
        }
        List selectedCertList = new ArrayList();
        selectedCertList.add(printTC);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selectedTcDobjList", selectedCertList);
        PrimeFaces.current().ajax().update("frm_print_TC:printConfirmationPopup");
        PrimeFaces.current().executeScript("PF('printCertificate').show()");
    }

    public void confirmPrintAllTC() {

        if (getSelectedCertDobj() != null && getSelectedCertDobj().size() < 1) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Please select atleast one certificate for printing."));
            return;
        }
        if (getSelectedCertDobj() != null && getSelectedCertDobj().size() > 8) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Increase bulk printing limit as maximum 8 certificates are are allowed."));
            return;
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selectedTcDobjList", getSelectedCertDobj());

        PrimeFaces.current().ajax().update("frm_print_TC:printConfirmationPopup");
        PrimeFaces.current().executeScript("PF('printCertificate').show()");
    }

    public String confirmPrintAllTradeCer() {
        String reportReturnName = "";
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("radioBtnValue", getTcRadiobtn());

        if (getTcRadiobtn().equalsIgnoreCase("PNDNGTC")) {
            for (Object selectedTcPrintDobjListObj : getSelectedCertDobj()) {
                TCPrintDobj dobj = (TCPrintDobj) selectedTcPrintDobjListObj;
                deleteAndSaveHistory(dobj.getApplNo(), dobj.getTcNo());
            }
        }

        if (tmConfigDobj.getTmTradeCertConfigDobj().isMultipleTcHavingUniqueTcNo()) {   //// multiple_tc_having_unique_tc_no work for Delhi,Mizoram,Maharashtra,TamilNadu
            reportReturnName = "TCReportDL";
        } else if (tmConfigDobj.getTmTradeCertConfigDobj().isVehClassToBeRendered()) {   /// render_veh_class work for Odisha
            reportReturnName = "TCReportOR";
        } else {
            reportReturnName = "TCReport";
        }
        return reportReturnName;
    }

    public String confirmPrintTradeCer() {
        String reportReturnName = "";
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("radioBtnValue", getTcRadiobtn());

        List list = (List) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selectedTcDobjList");
        String tcNo = (String) ((TCPrintDobj) list.get(0)).getTcNo();
        String applNo = (String) ((TCPrintDobj) list.get(0)).getApplNo();

        if (!sessionVariables.getUserCatgForLoggedInUser().equalsIgnoreCase("A")) {    //for office admin (printing already printed TC by number)
            deleteAndSaveHistory(applNo, tcNo);
        }

        if (tmConfigDobj.getTmTradeCertConfigDobj().isMultipleTcHavingUniqueTcNo()) {   //// multiple_tc_having_unique_tc_no work for Delhi,Mizoram,Maharashtra,TamilNadu
            reportReturnName = "TCReportDL";
        } else if (tmConfigDobj.getTmTradeCertConfigDobj().isVehClassToBeRendered()) {   /// render_veh_class work for Odisha
            reportReturnName = "TCReportOR";
        } else {
            reportReturnName = "TCReport";
        }
        return reportReturnName;
    }

    private void deleteAndSaveHistory(String applNo, String certNo) {
        try {
            PrintDocImpl.deleteAndSaveHistoryTC(applNo, certNo);
        } catch (VahanException ex) {
            LOGGER.error(ex);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Failure in moving into history table."));
            return;
        }
    }

    public List<TCPrintDobj> getPrintCertDobjForTCNoList() {
        return printCertDobjForTCNoList;
    }

    public void setPrintCertDobjForTCNoList(List<TCPrintDobj> printCertDobjForTCNoList) {
        this.printCertDobjForTCNoList = printCertDobjForTCNoList;
    }

    public List<TCPrintDobj> getPrintCertDobjForPendingList() {
        return printCertDobjForPendingList;
    }

    public void setPrintCertDobjForPendingList(List<TCPrintDobj> printCertDobjForPendingList) {
        this.printCertDobjForPendingList = printCertDobjForPendingList;
    }

    public List<TCPrintBean> getFilteredSeat() {
        return filteredSeat;
    }

    public void setFilteredSeat(List<TCPrintBean> filteredSeat) {
        this.filteredSeat = filteredSeat;
    }

    public String getMain_header_label() {
        return main_header_label;
    }

    public void setMain_header_label(String main_header_label) {
        this.main_header_label = main_header_label;
    }

    public boolean isPrintTc() {
        return printTc;
    }

    public void setPrintTc(boolean printTc) {
        this.printTc = printTc;
    }

    public String getTcRadiobtn() {
        return tcRadiobtn;
    }

    public void setTcRadiobtn(String tcRadiobtn) {
        this.tcRadiobtn = tcRadiobtn;
    }

    public boolean isPrintTcbyTradeCertNo() {
        return printTcbyTradeCertNo;
    }

    public void setPrintTcbyTradeCertNo(boolean printTcbyTradeCertNo) {
        this.printTcbyTradeCertNo = printTcbyTradeCertNo;
    }

    public String getTc_no() {
        return tc_no;
    }

    public void setTc_no(String tc_no) {
        this.tc_no = tc_no;
    }

    public List<TCPrintDobj> getSelectedCertDobj() {
        return selectedCertDobj;
    }

    public void setSelectedCertDobj(List<TCPrintDobj> selectedCertDobj) {
        this.selectedCertDobj = selectedCertDobj;
    }

    public boolean isAlreadyPrintedTc() {
        return alreadyPrintedTc;
    }

    public void setAlreadyPrintedTc(boolean alreadyPrintedTc) {
        this.alreadyPrintedTc = alreadyPrintedTc;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public Date getValidUpto() {
        return validUpto;
    }

    public void setValidUpto(Date validUpto) {
        this.validUpto = validUpto;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public boolean isTcForEachVehCatg() {
        return tcForEachVehCatg;
    }

    public void setTcForEachVehCatg(boolean tcForEachVehCatg) {
        this.tcForEachVehCatg = tcForEachVehCatg;
    }

    public boolean isDisplayFuel() {
        return displayFuel;
    }

    public boolean isForAlreadyPrintedTC() {
        return forAlreadyPrintedTC;
    }

    public boolean isRenderVehClass() {
        return renderVehClass;
    }

    public boolean isDoNotShowNoOfVehicles() {
        return doNotShowNoOfVehicles;
    }

    public void setDoNotShowNoOfVehicles(boolean doNotShowNoOfVehicles) {
        this.doNotShowNoOfVehicles = doNotShowNoOfVehicles;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public boolean isPrintTcByApplicationNo() {
        return printTcByApplicationNo;
    }

    public void setPrintTcByApplicationNo(boolean printTcByApplicationNo) {
        this.printTcByApplicationNo = printTcByApplicationNo;
    }
}
