/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.dealer;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.vahan.form.impl.Util;
import nic.vahan.form.dobj.dealer.DealertReportsDobj;
import nic.vahan.form.impl.dealer.DealerReportsImpl;
import org.apache.log4j.Logger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import nic.vahan.form.impl.PrintDocImpl;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "dealerReportsBean")
@ViewScoped
public class DealerReportsBean implements Serializable {

    private List<DealertReportsDobj> printList;
    private List<DealertReportsDobj> filteredList;
    java.util.Map<String, Object> parameters = new HashMap<String, Object>();
    private static final Logger LOGGER = Logger.getLogger(DealerReportsBean.class);
    private Date fromDate;
    private Date uptoDate;
    private String searchByValue;
    private boolean formPanelVisibility = false;
    private boolean receiptPanelVisibility = false;
    private boolean disclaimerPanelVisibility = false;
    private boolean provRcPanelVisibility = false;
    private boolean provRcButtonVisibility = false;
    private Date currentDate = new Date();
    private String applicationNo;
    private String reportType;
    private boolean dateWisePanelVisibility = false;
    private boolean applicationWisePanelVisibility = false;
    private boolean panelInspCertificateVisibility = false;
    private boolean panelVisibilityAfterApproval = false;
    private boolean renderAffterApprovalInputFields = false;
    private String userCatg;
    private List reportTypeList = new ArrayList();
    private boolean renderForm21Button = false;

    public DealerReportsBean() {
        try {
            fromDate = currentDate;
            uptoDate = currentDate;
            searchByValue = "dateWise";
            userCatg = Util.getUserCategory();
            if (CommonUtils.isNullOrBlank(userCatg)) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
            Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            if (sessionMap != null && sessionMap.get("fromDate") != null && sessionMap.get("uptoDate") != null && sessionMap.get("printList") != null && sessionMap.get("reportType") != null && "dateWise".equals(sessionMap.get("searchByValue"))) {
                fromDate = (Date) sessionMap.get("fromDate");
                uptoDate = (Date) sessionMap.get("uptoDate");
                printList = (List<DealertReportsDobj>) sessionMap.get("printList");
                reportType = (String) sessionMap.get("reportType");
                dateWisePanelVisibility = true;
                TmConfigurationDobj dobj = Util.getTmConfiguration();
                if (dobj != null) {
                    provRcButtonVisibility = dobj.isProv_rc();
                }
                this.renderPanelAsPerReportsType("calledFromCons");
                PrimeFaces.current().ajax().update("DealerReports:dataTable");
                PrimeFaces.current().ajax().update("DealerReports:reportInputs");
                sessionMap.remove("fromDate");
                sessionMap.remove("uptoDate");
                this.fillReportTypeList();
            } else {
                this.updateProvRcStatus();
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void getDetails() {
        try {
            this.renderPanelAsPerReportsType("calledFromGetDetails");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public String printForm2021(DealertReportsDobj dealerReportDobj, String printType) {
        String retURL = "";
        try {
            String applNo = dealerReportDobj.getApplNo();
            int purCd = dealerReportDobj.getPurCd();
            String regnNo = dealerReportDobj.getRegnNo();
            this.setSessionParameters(printType, purCd, regnNo, applNo);
            if (printType.equals("FORM20")) {
                retURL = "/ui/dealer/form_form20Report.xhtml?faces-redirect=true";
            } else if (printType.equals("FORM21")) {
                retURL = "/ui/dealer/form_form21Report.xhtml?faces-redirect=true";
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return retURL;
    }

    public String printDisclaimer() {
        this.setSessionParameters("disclaimer", 0, "", "");
        return PrintDocImpl.printOwnerDiscReport("newRegisteredVehicles", "reportFormat");
    }

    public String printCurrentReceipt(DealertReportsDobj dealerReportDobj) {
        try {
            String rcpt_no = dealerReportDobj.getRcptNo();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", rcpt_no);
            this.setSessionParameters(reportType, dealerReportDobj.getPurCd(), dealerReportDobj.getApplNo(), rcpt_no);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "PrintCashReceiptReport";
    }

    public String printProvisionalRC(DealertReportsDobj dealerReportDobj) {
        try {
            String application_no = dealerReportDobj.getApplNo();
            String regn_no = dealerReportDobj.getRegnNo();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("category", "newRegisteredVehicles");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("reportEntry", "reportFormat");
            this.setSessionParameters("provisionalRC", 0, regn_no, application_no);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "OwnerDisclaimerReport";
    }

    public String showCurrentReceipt() {
        DealerReportsImpl dealer_report_impl = new DealerReportsImpl();
        DealertReportsDobj dobj = dealer_report_impl.getRcptNo(applicationNo);
        String returnVar = "";
        if (dobj.getRcptNo() != null) {
            returnVar = printCurrentReceipt(dobj);
        } else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Either Application No is not valid or you are not authorized to see the application details", "Either Application No is not valid or you are not authorized to see the application details");
            FacesContext.getCurrentInstance().addMessage(null, message);
            returnVar = "";
        }
        return returnVar;
    }

    public String printInspectionCertificate(DealertReportsDobj dealerReportDobj) {
        try {
            String application_no = dealerReportDobj.getApplNo();
            int purCd = dealerReportDobj.getPurCd();
            String regnNo = dealerReportDobj.getRegnNo();
            this.setSessionParameters("inspCert", purCd, regnNo, application_no);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "/ui/dealer/form_inspection_certificate.xhtml?faces-redirect=true";
    }

    public void updateProvRcStatus() throws VahanException, Exception {
        TmConfigurationDobj dobj = Util.getTmConfiguration();
        provRcButtonVisibility = dobj.isProv_rc();
        renderAffterApprovalInputFields = false;
        if (searchByValue != null) {
            if (!searchByValue.equals("-1")) {
                formPanelVisibility = false;
                receiptPanelVisibility = false;
                disclaimerPanelVisibility = false;
                provRcPanelVisibility = false;
                panelInspCertificateVisibility = false;
                panelVisibilityAfterApproval = false;
                if (searchByValue.equals("dateWise")) {
                    dateWisePanelVisibility = true;
                    applicationWisePanelVisibility = false;
                } else if (searchByValue.equals("applicationWise")) {
                    applicationWisePanelVisibility = true;
                    dateWisePanelVisibility = false;
                }
                this.fillReportTypeList();
            } else {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Select Search Type", "Please Select Search Type");
                FacesContext.getCurrentInstance().addMessage(null, message);
                dateWisePanelVisibility = false;
                applicationWisePanelVisibility = false;
                formPanelVisibility = false;
                receiptPanelVisibility = false;
                disclaimerPanelVisibility = false;
                provRcPanelVisibility = false;
                panelInspCertificateVisibility = false;
                panelVisibilityAfterApproval = false;
                fromDate = currentDate;
                uptoDate = currentDate;
            }
        }
    }

    public String showDetailsAfterApprove(String regnNo, String printType, int purCd) {
        String formURL = "";
        this.setSessionParameters(printType, purCd, regnNo, regnNo);
        if (printType.equals("FORM20AfterApproval")) {
            formURL = "/ui/dealer/form_form20Report.xhtml?faces-redirect=true";
        } else if (printType.equals("FORM21AfterApproval")) {
            formURL = "/ui/dealer/form_form21Report.xhtml?faces-redirect=true";
        }
        return formURL;
    }

    public void setSessionParameters(String printType, int purCd, String regnNo, String applNo) {
        Map map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        map.put("applNo", applNo);
        map.put("printType", printType);
        map.put("purCd", purCd);
        map.put("regnNo", regnNo);
        map.put("printList", printList);
        map.put("fromDate", fromDate);
        map.put("uptoDate", uptoDate);
        map.put("reportType", reportType);
        map.put("searchByValue", searchByValue);
    }

    public void fillReportTypeList() {
        reportTypeList.clear();
        if (TableConstants.USER_CATG_DEALER.equals(userCatg)) {
            reportTypeList.add(new SelectItem("form2021", "Form 20 And 21"));
            reportTypeList.add(new SelectItem("receipt", "Current Receipt"));
            reportTypeList.add(new SelectItem("disclaimer", "Disclaimer"));
            reportTypeList.add(new SelectItem("inspCertificate", "Inspection Certificate"));
            reportTypeList.add(new SelectItem("form2021AfterApproval", "Form 20 And 21 After Approval"));
            if (provRcButtonVisibility) {
                reportTypeList.add(new SelectItem("provisionalRC", "Provisional RC"));
            }
        } else {
            renderForm21Button = true;
            reportTypeList.add(new SelectItem("RTOform20", "Print Form 20"));
        }
    }

    public void fromDateChangeListener() {
        if (printList != null && !printList.isEmpty()) {
            printList.clear();
        }
        if (fromDate.compareTo(currentDate) < 0) {
            Date maxDate = ServerUtil.dateRange(fromDate, 0, 0, 30);
            if (maxDate.compareTo(currentDate) < 0) {
                uptoDate = maxDate;
            } else {
                uptoDate = currentDate;
            }
        } else {
            fromDate = currentDate;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "From date out of Current Date", "From date out of current date"));
        }
    }

    public void radioButtonAjaxListener() {
        if (printList != null && !printList.isEmpty()) {
            printList.clear();
        }
        setApplicationNo("");
        if (reportType.equals("form2021AfterApproval")) {
            renderAffterApprovalInputFields = true;
        } else {
            renderAffterApprovalInputFields = false;
        }
    }

    public void renderPanelAsPerReportsType(String callingType) {
        DateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
        DealerReportsImpl dealer_report_impl = new DealerReportsImpl();
        try {
            if (reportType != null && !reportType.equals("")) {

                if (reportType.equals("form2021") || reportType.equals("disclaimer") || reportType.equals("RTOform20") || reportType.equals("inspCertificate") || reportType.equals("form2021AfterApproval")) {
                    receiptPanelVisibility = false;
                    provRcPanelVisibility = false;
                    if (reportType.equals("form2021") || reportType.equals("RTOform20")) {
                        formPanelVisibility = true;
                        disclaimerPanelVisibility = false;
                        panelInspCertificateVisibility = false;
                        panelVisibilityAfterApproval = false;
                    } else if (reportType.equals("disclaimer")) {
                        disclaimerPanelVisibility = true;
                        formPanelVisibility = false;
                        panelInspCertificateVisibility = false;
                        panelVisibilityAfterApproval = false;
                    } else if (reportType.equals("inspCertificate")) {
                        panelInspCertificateVisibility = true;
                        formPanelVisibility = false;
                        disclaimerPanelVisibility = false;
                        panelVisibilityAfterApproval = false;
                    } else if (reportType.equals("form2021AfterApproval")) {
                        formPanelVisibility = false;
                        disclaimerPanelVisibility = false;
                        panelInspCertificateVisibility = false;
                        panelVisibilityAfterApproval = true;
                    }
                    if (callingType.equals("calledFromGetDetails")) {
                        printList = dealer_report_impl.getPrintListForDisclaimer(sdf.parse(sdf.format(fromDate)), sdf.parse(sdf.format(uptoDate)), reportType, Long.parseLong(Util.getEmpCode()), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), applicationNo);
                    }
                } else if (reportType.equals("provisionalRC") || reportType.equals("receipt")) {
                    formPanelVisibility = false;
                    disclaimerPanelVisibility = false;
                    panelInspCertificateVisibility = false;
                    panelVisibilityAfterApproval = false;
                    if (reportType.equals("receipt")) {
                        receiptPanelVisibility = true;
                        provRcPanelVisibility = false;
                    } else if (reportType.equals("provisionalRC")) {
                        provRcPanelVisibility = true;
                        receiptPanelVisibility = false;
                    }
                    if (callingType.equals("calledFromGetDetails")) {
                        printList = dealer_report_impl.getPrintList(sdf.parse(sdf.format(fromDate)), sdf.parse(sdf.format(uptoDate)));
                    }
                }
                if (printList == null || printList.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "No pending application to print.", "No pending application to print."));
                }
            } else {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Select Report Type", "Please Select Report Type");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    /**
     * @return the fromDate
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * @param fromDate the fromDate to set
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * @return the uptoDate
     */
    public Date getUptoDate() {
        return uptoDate;
    }

    /**
     * @param uptoDate the uptoDate to set
     */
    public void setUptoDate(Date uptoDate) {
        this.uptoDate = uptoDate;
    }

    /**
     * @return the searchByValue
     */
    public String getSearchByValue() {
        return searchByValue;
    }

    /**
     * @param searchByValue the searchByValue to set
     */
    public void setSearchByValue(String searchByValue) {
        this.searchByValue = searchByValue;
    }

    /**
     * @return the formPanelVisibility
     */
    public boolean isFormPanelVisibility() {
        return formPanelVisibility;
    }

    /**
     * @param formPanelVisibility the formPanelVisibility to set
     */
    public void setFormPanelVisibility(boolean formPanelVisibility) {
        this.formPanelVisibility = formPanelVisibility;
    }

    /**
     * @return the receiptPanelVisibility
     */
    public boolean isReceiptPanelVisibility() {
        return receiptPanelVisibility;
    }

    /**
     * @param receiptPanelVisibility the receiptPanelVisibility to set
     */
    public void setReceiptPanelVisibility(boolean receiptPanelVisibility) {
        this.receiptPanelVisibility = receiptPanelVisibility;
    }

    /**
     * @return the currentDate
     */
    public Date getCurrentDate() {
        return currentDate;
    }

    /**
     * @param currentDate the currentDate to set
     */
    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    /**
     * @return the disclaimerPanelVisibility
     */
    public boolean isDisclaimerPanelVisibility() {
        return disclaimerPanelVisibility;
    }

    /**
     * @param disclaimerPanelVisibility the disclaimerPanelVisibility to set
     */
    public void setDisclaimerPanelVisibility(boolean disclaimerPanelVisibility) {
        this.disclaimerPanelVisibility = disclaimerPanelVisibility;
    }

    /**
     * @return the printList
     */
    public List<DealertReportsDobj> getPrintList() {
        return printList;
    }

    /**
     * @param printList the printList to set
     */
    public void setPrintList(List<DealertReportsDobj> printList) {
        this.printList = printList;
    }

    /**
     * @return the filteredList
     */
    public List<DealertReportsDobj> getFilteredList() {
        return filteredList;
    }

    /**
     * @param filteredList the filteredList to set
     */
    public void setFilteredList(List<DealertReportsDobj> filteredList) {
        this.filteredList = filteredList;
    }

    /**
     * @return the provRcPanelVisibility
     */
    public boolean isProvRcPanelVisibility() {
        return provRcPanelVisibility;
    }

    /**
     * @param provRcPanelVisibility the provRcPanelVisibility to set
     */
    public void setProvRcPanelVisibility(boolean provRcPanelVisibility) {
        this.provRcPanelVisibility = provRcPanelVisibility;
    }

    /**
     * @return the reportType
     */
    public String getReportType() {
        return reportType;
    }

    /**
     * @param reportType the reportType to set
     */
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    /**
     * @return the applicationNo
     */
    public String getApplicationNo() {
        return applicationNo;
    }

    /**
     * @param applicationNo the applicationNo to set
     */
    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    /**
     * @return the provRcButtonVisibility
     */
    public boolean isProvRcButtonVisibility() {
        return provRcButtonVisibility;
    }

    /**
     * @param provRcButtonVisibility the provRcButtonVisibility to set
     */
    public void setProvRcButtonVisibility(boolean provRcButtonVisibility) {
        this.provRcButtonVisibility = provRcButtonVisibility;
    }

    /**
     * @return the dateWisePanelVisibility
     */
    public boolean isDateWisePanelVisibility() {
        return dateWisePanelVisibility;
    }

    /**
     * @param dateWisePanelVisibility the dateWisePanelVisibility to set
     */
    public void setDateWisePanelVisibility(boolean dateWisePanelVisibility) {
        this.dateWisePanelVisibility = dateWisePanelVisibility;
    }

    /**
     * @return the applicationWisePanelVisibility
     */
    public boolean isApplicationWisePanelVisibility() {
        return applicationWisePanelVisibility;
    }

    /**
     * @param applicationWisePanelVisibility the applicationWisePanelVisibility
     * to set
     */
    public void setApplicationWisePanelVisibility(boolean applicationWisePanelVisibility) {
        this.applicationWisePanelVisibility = applicationWisePanelVisibility;
    }

    /**
     * @return the panelInspCertificateVisibility
     */
    public boolean isPanelInspCertificateVisibility() {
        return panelInspCertificateVisibility;
    }

    /**
     * @param panelInspCertificateVisibility the panelInspCertificateVisibility
     * to set
     */
    public void setPanelInspCertificateVisibility(boolean panelInspCertificateVisibility) {
        this.panelInspCertificateVisibility = panelInspCertificateVisibility;
    }

    /**
     * @return the panelVisibilityAfterApproval
     */
    public boolean isPanelVisibilityAfterApproval() {
        return panelVisibilityAfterApproval;
    }

    /**
     * @param panelVisibilityAfterApproval the panelVisibilityAfterApproval to
     * set
     */
    public void setPanelVisibilityAfterApproval(boolean panelVisibilityAfterApproval) {
        this.panelVisibilityAfterApproval = panelVisibilityAfterApproval;
    }

    /**
     * @return the renderAffterApprovalInputFields
     */
    public boolean isRenderAffterApprovalInputFields() {
        return renderAffterApprovalInputFields;
    }

    /**
     * @param renderAffterApprovalInputFields the
     * renderAffterApprovalInputFields to set
     */
    public void setRenderAffterApprovalInputFields(boolean renderAffterApprovalInputFields) {
        this.renderAffterApprovalInputFields = renderAffterApprovalInputFields;
    }

    public List getReportTypeList() {
        return reportTypeList;
    }

    public void setReportTypeList(List reportTypeList) {
        this.reportTypeList = reportTypeList;
    }

    public boolean isRenderForm21Button() {
        return renderForm21Button;
    }

    public void setRenderForm21Button(boolean renderForm21Button) {
        this.renderForm21Button = renderForm21Button;
    }
}
