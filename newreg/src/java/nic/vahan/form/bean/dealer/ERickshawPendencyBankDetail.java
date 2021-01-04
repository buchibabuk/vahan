/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.dealer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.dealer.PendencyBankDobj;
import nic.vahan.form.impl.dealer.PendencyBankDetailImpl;
import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author pramod
 */
@ManagedBean(name = "pendencyBankDetailBean")
@ViewScoped
public class ERickshawPendencyBankDetail implements Serializable {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(PendencyBankDetailImpl.class);
    private String applNo;
    private List bankNameList = new ArrayList();
    private List<PendencyBankDobj> bankDobjList = new ArrayList<>();
    private List<PendencyBankDobj> filteredBankDobjList;
    private SessionVariables sessionVariables = null;
    private List statusList = new ArrayList();
    private String docsType = "recommended";
    private String headerText = "PENDING E-RICKSHAW SUBSIDY CASES";
    private boolean disableBankDetails = false;
    private boolean renderEditDownloadFileButton = false;//render edit or radio button to see the subsidy granted records
    private String statusHeader = "Status";
    private boolean renderRadioButton = false;
    private String recordStatus;

    @PostConstruct
    public void init() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getOffCodeSelected() == 0
                    || sessionVariables.getEmpCodeLoggedIn() == null
                    || sessionVariables.getActionCodeSelected() == 0) {
                throw new VahanException("Something went wrong, Please try again...");

            }
            switch (sessionVariables.getActionCodeSelected()) {
                case TableConstants.VM_SUBSIDY_VERIFY:
                    recordStatus = TableConstants.SUBSIDY_PENDING_STATUS;
                    statusList.clear();
                    renderEditDownloadFileButton = true;
                    statusList.add(new SelectItem("V", "Verified"));
                    break;
                case TableConstants.VM_SUBSIDY_RECOMMENDED:
                    recordStatus = TableConstants.SUBSIDY_VERIFIED_STATUS;
                    statusList.clear();
                    renderEditDownloadFileButton = true;
                    statusList.add(new SelectItem("P", "Return"));
                    statusList.add(new SelectItem("R", "Recommend"));
                    headerText = "PENDING CASES OF E-RICKSHAW";
                    break;
                case TableConstants.VM_SUBSIDY_RECORD_PRINT_GRANT:
                    recordStatus = TableConstants.SUBSIDY_RECOMMENDED_STATUS;
                    disableBankDetails = true;
                    statusHeader = "Recommended";
                    renderRadioButton = true;
                    renderEditDownloadFileButton = false;
                    headerText = "PENDING CASES OF E-RICKSHAW";
                    break;
            }
            bankDobjList = new PendencyBankDetailImpl().getPendencySubsidyBankDtls(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), recordStatus);
            if (bankDobjList != null && !bankDobjList.isEmpty()) {
                String[][] bankData = MasterTableFiller.masterTables.TM_BANK.getData();
                for (int i = 0; i < bankData.length; i++) {
                    bankNameList.add(new SelectItem(bankData[i][0], bankData[i][1]));
                }
            }
        } catch (VahanException ve) {
            JSFUtils.showMessagesInDialog("Alert !!", ve.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "  " + e.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void recordSaveAfterEdit(RowEditEvent event) {
        try {
            PendencyBankDobj pendencyObj = (PendencyBankDobj) event.getObject();
            boolean flag = new PendencyBankDetailImpl().updateBankSubsidyEditedRecords(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), pendencyObj, sessionVariables.getEmpCodeLoggedIn());
            if (flag) {
                bankDobjList.remove(pendencyObj);
                PrimeFaces.current().ajax().update("onSuccess_panel");
                PrimeFaces.current().executeScript("PF('onsuccessful_data_save').show();");
            } else {
                throw new VahanException("Error in updating record.");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + "  " + e.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void updateStatusBeforeDownloadRecords(Object document) {
        try {
            new PendencyBankDetailImpl().updateStatusOfPrintedRecords(bankDobjList, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), sessionVariables.getEmpCodeLoggedIn());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
    }

    public void changeRadioButtonEvent() { // CALL ON RADIO CHANGE EVENT.
        try {
            switch (docsType) {
                case "recommended":
                    // Records is ready to send to bank after MLO recommended.
                    disableBankDetails = true;
                    renderEditDownloadFileButton = false;
                    renderRadioButton = true;
                    recordStatus = TableConstants.SUBSIDY_RECOMMENDED_STATUS;
                    break;
                case "grant":
                    // Records send to bank after MLO recommended
                    recordStatus = TableConstants.SUBSIDY_RECORDS_PRINT_STATUS;
                    disableBankDetails = true;
                    renderEditDownloadFileButton = true;
                    renderRadioButton = true;
                    statusList.clear();
                    statusList.add(new SelectItem("P", "Return"));
                    statusList.add(new SelectItem("S", "Subsidy Granted"));
                    break;
            }
            bankDobjList = new PendencyBankDetailImpl().getPendencySubsidyBankDtls(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), recordStatus);
            headerText = "PENDENCY VERIFIED/GRANT REPORTS";
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }

    } // END RADIO CHANGE EVENT

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
     * @return the bankDobjList
     */
    public List<PendencyBankDobj> getBankDobjList() {
        return bankDobjList;
    }

    /**
     * @param bankDobjList the bankDobjList to set
     */
    public void setBankDobjList(List<PendencyBankDobj> bankDobjList) {
        this.bankDobjList = bankDobjList;
    }

    /**
     * @return the bankNameList
     */
    public List getBankNameList() {
        return bankNameList;
    }

    /**
     * @param bankNameList the bankNameList to set
     */
    public void setBankNameList(List bankNameList) {
        this.bankNameList = bankNameList;
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
     * @return the statusList
     */
    public List getStatusList() {
        return statusList;
    }

    /**
     * @param statusList the statusList to set
     */
    public void setStatusList(List statusList) {
        this.statusList = statusList;
    }

    /**
     * @return the docsType
     */
    public String getDocsType() {
        return docsType;
    }

    /**
     * @param docsType the docsType to set
     */
    public void setDocsType(String docsType) {
        this.docsType = docsType;
    }

    /**
     * @return the headerText
     */
    public String getHeaderText() {
        return headerText;
    }

    /**
     * @param headerText the headerText to set
     */
    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    /**
     * @return the disableBankDetails
     */
    public boolean isDisableBankDetails() {
        return disableBankDetails;
    }

    /**
     * @param disableBankDetails the disableBankDetails to set
     */
    public void setDisableBankDetails(boolean disableBankDetails) {
        this.disableBankDetails = disableBankDetails;
    }

    /**
     * @return the renderEditDownloadFileButton
     */
    public boolean isRenderEditDownloadFileButton() {
        return renderEditDownloadFileButton;
    }

    /**
     * @param renderEditDownloadFileButton the renderEditDownloadFileButton to
     * set
     */
    public void setRenderEditDownloadFileButton(boolean renderEditDownloadFileButton) {
        this.renderEditDownloadFileButton = renderEditDownloadFileButton;
    }

    /**
     * @return the statusHeader
     */
    public String getStatusHeader() {
        return statusHeader;
    }

    /**
     * @param statusHeader the statusHeader to set
     */
    public void setStatusHeader(String statusHeader) {
        this.statusHeader = statusHeader;
    }

    /**
     * @return the renderRadioButton
     */
    public boolean isRenderRadioButton() {
        return renderRadioButton;
    }

    /**
     * @param renderRadioButton the renderRadioButton to set
     */
    public void setRenderRadioButton(boolean renderRadioButton) {
        this.renderRadioButton = renderRadioButton;
    }

    /**
     * @return the filteredBankDobjList
     */
    public List<PendencyBankDobj> getFilteredBankDobjList() {
        return filteredBankDobjList;
    }

    /**
     * @param filteredBankDobjList the filteredBankDobjList to set
     */
    public void setFilteredBankDobjList(List<PendencyBankDobj> filteredBankDobjList) {
        this.filteredBankDobjList = filteredBankDobjList;
    }
}
