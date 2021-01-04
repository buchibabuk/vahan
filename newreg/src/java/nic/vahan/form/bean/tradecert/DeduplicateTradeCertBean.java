/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.tradecert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.tradecert.DeduplicateTradeCertDobj;
import nic.vahan.form.impl.tradecert.DeduplicateTradeCertImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author ManuSri
 */
@ManagedBean(name = "deduplicateTradeCertBean", eager = true)
@ViewScoped
public class DeduplicateTradeCertBean {

    /**
     * Properties
     */
    private static final Logger LOGGER = Logger.getLogger(DeduplicateTradeCertBean.class);
    private final String ADMIN_DEALER;
    private final String NO_ADMIN_DEALER;
    private final String DEFAULT_SELECT_VALUE = "-SELECT-";
    private final DeduplicateTradeCertImpl impl;
    private final List<DeduplicateTradeCertDobj> DeduplicatedTcDobjList;
    private final SessionVariables sessionVariables;
    private String tcRecordsDisplayTableHeading;
    private String deduplicatedRecordDisplayTableHeading;
    private List<SelectItem> dealerList;
    private List<SelectItem> tcNoList;
    private List<DeduplicateTradeCertDobj> tcDobjsList;
    private List<DeduplicateTradeCertDobj> tcDobjsListToBeOperatedOn = new ArrayList();
    private DeduplicateTradeCertDobj DeduplicateTradeCertDobj;
    private Boolean disableField;
    private boolean toBeDeduplicated;
    private boolean renderPrintButton;

    /**
     * Default Constructor
     */
    public DeduplicateTradeCertBean() {
        ADMIN_DEALER = "Admin_Dealer";
        NO_ADMIN_DEALER = "No_Admin_Dealer";
        DeduplicateTradeCertDobj = new DeduplicateTradeCertDobj();
        sessionVariables = new SessionVariables();
        DeduplicatedTcDobjList = new ArrayList<>();
        impl = new DeduplicateTradeCertImpl();
    }

    /**
     * Post Construct Bean Initializer Method
     */
    @PostConstruct
    public void init() {
        getDeduplicateTradeCertDobj().setStateCd(sessionVariables.getStateCodeSelected());
        getDeduplicateTradeCertDobj().setRtoOfficeCode(sessionVariables.getOffCodeSelected());
        getDeduplicateTradeCertDobj().setRtoOfficeName(ServerUtil.getOfficeName(sessionVariables.getOffCodeSelected(),
                sessionVariables.getStateCodeSelected()));
        getDeduplicateTradeCertDobj().setDealerChoiceCondition(ADMIN_DEALER);
        try {
            fillDealerList();
        } catch (VahanException ve) {
            JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.ERROR);
        }
    }

    /**
     * Reset method of bean
     */
    public void reset() {
        if (getDeduplicateTradeCertDobj() != null) {
            getDeduplicateTradeCertDobj().reset();
        }
        setDealerList(null);
        setTcNoList(null);
        setTcDobjsList(null);
        getDeduplicatedTcDobjList().clear();
        setDisableField(Boolean.FALSE);
        setToBeDeduplicated(Boolean.FALSE);
        setDeduplicateTradeCertDobj(new DeduplicateTradeCertDobj());
        setRenderPrintButton(false);
        init();
    }

    /**
     * Listener called on the event of selection of dealerChoiceCondition
     */
    public void dealerChoiceConditionListener(AjaxBehaviorEvent event) {
        getDeduplicateTradeCertDobj().setDealer("");
        setDealerList(null);
        getDeduplicateTradeCertDobj().setCertNo("");
        setTcNoList(null);
        switch (getDeduplicateTradeCertDobj().getDealerChoiceCondition()) {
            case "Admin_Dealer":
                getDeduplicateTradeCertDobj().setDealerChoiceCondition(ADMIN_DEALER);
                break;
            case "No_Admin_Dealer":
                getDeduplicateTradeCertDobj().setDealerChoiceCondition(NO_ADMIN_DEALER);
                break;
        }
        try {
            fillDealerList();
        } catch (VahanException ve) {
            JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.ERROR);
        }
    }

    /**
     * Filling the list of dealers according to the dealerChoiceCondition from
     * the database
     */
    public void fillDealerList() throws VahanException {
        List<SelectItem> listOfDealers = null;
        switch (getDeduplicateTradeCertDobj().getDealerChoiceCondition()) {
            case "Admin_Dealer":
                listOfDealers = impl.fillDealers(ADMIN_DEALER);
                break;
            case "No_Admin_Dealer":
                listOfDealers = impl.fillDealers(NO_ADMIN_DEALER);
                break;
        }
        if (listOfDealers != null) {
            this.setDealerList(listOfDealers);
        }
    }

    /**
     * Listener called on the event of selection of selection of dealer
     */
    public void dealerSelectionListener(AjaxBehaviorEvent event) {
        getDeduplicateTradeCertDobj().setCertNo("");
        setTcNoList(null);
        setRenderPrintButton(false);
        if (!getDEFAULT_SELECT_VALUE().equals(getDeduplicateTradeCertDobj().getDealer())) {
            settingDealerCdIdInDobjSeparately();
            try {
                fillTcNoList();
            } catch (VahanException ve) {
                JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.ERROR);
            }
        }
    }

    /**
     * Filling the list of trade certificate numbers according to the selection
     * of dealer from the database
     */
    public void fillTcNoList() throws VahanException {
        List<SelectItem> listOfTcNo = impl.fillCertNo(getDeduplicateTradeCertDobj().getDealer());
        if (listOfTcNo != null) {
            setTcNoList(listOfTcNo);
        }
    }

    /**
     * Proceed action command to fetch the resultant records from the database
     * on the basis of selected dealer and trade certificate number
     */
    public void proceed() {
        List<DeduplicateTradeCertDobj> listOfTcDobjs;
        try {
            settingDealerCdIdInDobjSeparately();
            listOfTcDobjs = impl.searchTCsForDealerAndTcNo(getDeduplicateTradeCertDobj().getDealer(),
                    getDeduplicateTradeCertDobj().getCertNo(), getDeduplicateTradeCertDobj().getDealerChoiceCondition());
        } catch (VahanException ve) {
            JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.ERROR);
            return;
        }
        if (listOfTcDobjs != null) {
            setTcDobjsList(listOfTcDobjs);
            StringBuilder heading = new StringBuilder();
            heading = heading.append("<u>List of multiple dealer entries for trade certificate number <b>")
                    .append(getDeduplicateTradeCertDobj().getCertNo())
                    .append("</b></u>");
            setTcRecordsDisplayTableHeading(heading.toString());
            getDeduplicateTradeCertDobj().setFee(calculateTotalFeeCollected(listOfTcDobjs));
            setDisableField(Boolean.TRUE);
        }
    }

    /**
     * De-duplicate action command to De-duplicate the trade certificate records
     * fetched in "proceed" action
     */
    public void deduplicate() {
        try {
            validateData("for_deduplicate");
        } catch (VahanException ve) {
            if (ve.getMessage().contains("Exception")) {
                JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.ERROR);
                return;
            } else if (ve.getMessage().contains("Info")) {
                JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.INFO);
            }
        }

        int srNo = 1;
        getDeduplicatedTcDobjList().clear();
        for (DeduplicateTradeCertDobj dobj : getTcDobjsListToBeOperatedOn()) {
            if (getDeduplicateTradeCertDobj().getDealer().equals(dobj.getDealer())) {
                getDeduplicatedTcDobjList().add(dobj.copy(new DeduplicateTradeCertDobj()));
                getDeduplicatedTcDobjList().get(getDeduplicatedTcDobjList().size() - 1).setSrNo(srNo++);
            }
        }
        if (!getDeduplicatedTcDobjList().isEmpty()) {
            getDeduplicateTradeCertDobj().setDealerName(getDeduplicatedTcDobjList().get(0).getDealerName());
            setDeduplicatedRecordDisplayTableHeading("<u>Deduplication Required :</u> <b>" + getDeduplicateTradeCertDobj().getDealerName() + "</b> (<b>" + getDeduplicateTradeCertDobj().getDealer() + "</b>) "
                    + " should have unique trade certificate ");
            setToBeDeduplicated(Boolean.TRUE);
        }
    }

    /**
     * Save action command to save the De-duplicated trade certificate record in
     * database
     */
    public void update() {
        try {
            Map updateOprResultMap = impl.update(getDeduplicatedTcDobjList(), getTcDobjsList());

            if (!updateOprResultMap.isEmpty()) {
                setDeduplicateTradeCertDobj((DeduplicateTradeCertDobj) getDeduplicatedTcDobjList().get(0));
                boolean updated = false;
                List<String> systemGeneratedNewTcNosList = null;
                for (Object key : updateOprResultMap.keySet().toArray()) {
                    if ("updateOprResult".equals(key)) {
                        updated = (Boolean) updateOprResultMap.get(key);
                    } else if ("TcNosList".equals(key)) {
                        systemGeneratedNewTcNosList = (List<String>) updateOprResultMap.get(key);
                        getDeduplicateTradeCertDobj().getSystemGeneratedTcNumberList().clear();
                        getDeduplicateTradeCertDobj().getSystemGeneratedTcNumberList().addAll(systemGeneratedNewTcNosList);
                    }
                }
                if (updated) {
                    JSFUtils.setFacesMessage("Records updated in database having following details:-", null, JSFUtils.INFO);
                    JSFUtils.setFacesMessage("Dealer Name : <b>" + getDeduplicateTradeCertDobj().getDealerName() + "</b>", null, JSFUtils.INFO);
                    JSFUtils.setFacesMessage("Dealer Code : <b>" + getDeduplicateTradeCertDobj().getDealer() + "</b>", null, JSFUtils.INFO);
                    JSFUtils.setFacesMessage("System Generated Trade Certificate Number List:- ", null, JSFUtils.INFO);
                    for (String tcNo : systemGeneratedNewTcNosList) {
                        JSFUtils.setFacesMessage(tcNo, null, JSFUtils.INFO);
                    }
                    setRenderPrintButton(true);
                }
            } else {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
        } catch (VahanException ve) {
            setRenderPrintButton(false);
            JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.ERROR);
        }
        getDeduplicateTradeCertDobj().setStateCd(sessionVariables.getStateCodeSelected());
        getDeduplicateTradeCertDobj().setRtoOfficeCode(sessionVariables.getOffCodeSelected());
        getDeduplicateTradeCertDobj().setRtoOfficeName(ServerUtil.getOfficeName(sessionVariables.getOffCodeSelected(),
                sessionVariables.getStateCodeSelected()));
    }

    /**
     * Dispose action command to prepare list for deletion of the trade
     * certificate records fetched in "proceed" action having no fee
     */
    public void dispose() {
        try {
            validateData("for_dispose");
        } catch (VahanException ve) {
            if (ve.getMessage().contains("Exception")) {
                JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.ERROR);
                return;
            } else if (ve.getMessage().contains("Info")) {
                JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.INFO);
            }
        }

        int srNo = 1;
        getDeduplicatedTcDobjList().clear();
        for (DeduplicateTradeCertDobj dobj : getTcDobjsListToBeOperatedOn()) {
            if (dobj.getFee() == null || (dobj.getFee() != null && dobj.getFee() == 0.0)) {
                getDeduplicatedTcDobjList().add(dobj.copy(new DeduplicateTradeCertDobj()));
                getDeduplicatedTcDobjList().get(getDeduplicatedTcDobjList().size() - 1).setSrNo(srNo++);
            }
        }
        if (getDeduplicatedTcDobjList().isEmpty()) {
            JSFUtils.setFacesMessage("Exception: No record found for disposal as 'Fee' has been submitted for all the records.", null, JSFUtils.ERROR);
        } else {
            getDeduplicateTradeCertDobj().setDealerName(getDeduplicatedTcDobjList().get(0).getDealerName());
            setDeduplicatedRecordDisplayTableHeading("<u>Disposing Required :</u> <b>" + getDeduplicateTradeCertDobj().getDealerName() + "</b> (<b>" + getDeduplicateTradeCertDobj().getDealer() + "</b>) "
                    + " should have unique trade certificate ");
        }
    }

    /**
     * Delete Trade Certificate when Admin-Id is NOT found in database and fee
     * not found
     */
    public void delete() {
        try {
            Boolean deleted = impl.delete(getDeduplicatedTcDobjList());
            if (deleted) {
                JSFUtils.setFacesMessage("Records deleted in database having following details:-", null, JSFUtils.INFO);
                JSFUtils.setFacesMessage("Dealer Name : <b>" + getDeduplicateTradeCertDobj().getDealerName() + "</b>", null, JSFUtils.INFO);
                JSFUtils.setFacesMessage("Dealer Code : <b>" + getDeduplicateTradeCertDobj().getDealer() + "</b>", null, JSFUtils.INFO);
                JSFUtils.setFacesMessage("Trade Certficate Number : <b>" + getDeduplicateTradeCertDobj().getCertNo() + "</b>", null, JSFUtils.INFO);
                setRenderPrintButton(true);
            } else {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
        } catch (VahanException ve) {
            setRenderPrintButton(false);
            JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.ERROR);
        }
        getDeduplicateTradeCertDobj().setStateCd(sessionVariables.getStateCodeSelected());
        getDeduplicateTradeCertDobj().setRtoOfficeCode(sessionVariables.getOffCodeSelected());
        getDeduplicateTradeCertDobj().setRtoOfficeName(ServerUtil.getOfficeName(sessionVariables.getOffCodeSelected(),
                sessionVariables.getStateCodeSelected()));
    }

    /**
     * Validating records collected for combination dealer and trade certificate
     * Number for further actions to be taken
     */
    private void validateData(String fromAction) throws VahanException {
        getTcDobjsListToBeOperatedOn().clear();
        getTcDobjsListToBeOperatedOn().addAll(getTcDobjsList());
        if ("for_deduplicate".equals(fromAction)) {

            Set setOfDealerCdTcNoVehCatg = new HashSet();
            for (DeduplicateTradeCertDobj dobj : getTcDobjsList()) {
                if (setOfDealerCdTcNoVehCatg.contains(dobj.getDealer() + dobj.getCertNo() + dobj.getVehCatg())) {
                    //throw new VahanException("Info: Multiple records found having same dealer, T.C number and vehicle category. Please dispose records using 'Trade-Cert-Merge' option.");
                    for (DeduplicateTradeCertDobj dobjToRemove : getTcDobjsList()) {
                        String key = dobj.getDealer() + dobj.getCertNo() + dobj.getVehCatg();
                        if (key.equals(dobjToRemove.getDealer() + dobjToRemove.getCertNo() + dobjToRemove.getVehCatg())) {
                            if (getTcDobjsListToBeOperatedOn().contains(dobjToRemove)) {
                                getTcDobjsListToBeOperatedOn().remove(dobjToRemove);
                            }
                        }
                    }
                }
                setOfDealerCdTcNoVehCatg.add(dobj.getDealer() + dobj.getCertNo() + dobj.getVehCatg());
            }

            Set vehCatgSet = new HashSet();
            for (DeduplicateTradeCertDobj dobj : getTcDobjsList()) {
                /**
                 * Check for interrupting action, if same vehicle category
                 * coming multiple times
                 */
                if (vehCatgSet.contains(dobj.getVehCatg() + dobj.getDealer())) {
                    throw new VahanException("Exception: Record cannot be deduplicated as same vehicle category found in more than one record. First merge the records using 'Trade-Cert-Merge' option.");
                }
                vehCatgSet.add(dobj.getVehCatg() + dobj.getDealer());
            }
        }

        if ("for_dispose".equals(fromAction)) {

            Set setOfDealerCdTcNoVehCatg = new HashSet();
            for (DeduplicateTradeCertDobj dobj : getTcDobjsList()) {
                if (setOfDealerCdTcNoVehCatg.contains(dobj.getDealer() + dobj.getCertNo() + dobj.getVehCatg())) {
                    //throw new VahanException("Exception: Multiple records found having same dealer, T.C number and vehicle category. Please dispose records using 'Trade-Cert-Merge' option.");
                    for (DeduplicateTradeCertDobj dobjToRemove : getTcDobjsList()) {
                        String key = dobj.getDealer() + dobj.getCertNo() + dobj.getVehCatg();
                        if (key.equals(dobjToRemove.getDealer() + dobjToRemove.getCertNo() + dobjToRemove.getVehCatg())) {
                            if (getTcDobjsListToBeOperatedOn().contains(dobjToRemove)) {
                                getTcDobjsListToBeOperatedOn().remove(dobjToRemove);
                            }
                        }
                    }
                }
                setOfDealerCdTcNoVehCatg.add(dobj.getDealer() + dobj.getCertNo() + dobj.getVehCatg());
            }

            int countRecordsForWhichFeeTaken = 0;
            for (DeduplicateTradeCertDobj dobj : getTcDobjsList()) {
                /**
                 * Check for interrupting action, if 'fee' is found to be
                 * submitted
                 */
                if (dobj.getFee() != null && dobj.getFee() != 0.0) {
                    countRecordsForWhichFeeTaken += 1;
                    if (countRecordsForWhichFeeTaken > 0) {
                        throw new VahanException("Info: Record cannot be disposed for which 'Fee' has been submitted. Please cancel records using 'Trade-Cert-Cancel' option.");
                    }
                }
            }
        }
    }

    /**
     * Split DealerCd and UserId and set in dobj separately
     */
    private void settingDealerCdIdInDobjSeparately() {
        String dealerCdStr = getDeduplicateTradeCertDobj().getDealer();
        if (dealerCdStr.contains("#")) {
            getDeduplicateTradeCertDobj().setDealer(dealerCdStr.split("#")[0]);
            getDeduplicateTradeCertDobj().setUserId(dealerCdStr.split("#")[1]);
        } else {
            getDeduplicateTradeCertDobj().setDealer(dealerCdStr);
        }
    }

    /**
     * Calculate total fee collected from all the dobjs in the tcRecordsList
     */
    private Double calculateTotalFeeCollected(List<DeduplicateTradeCertDobj> listOfTcDobjs) {
        Double totalFeeCollected = null;
        for (DeduplicateTradeCertDobj dobj : listOfTcDobjs) {
            if (totalFeeCollected == null) {
                totalFeeCollected = 0.0;
            }
            if (dobj.getFee() != null) {
                totalFeeCollected += dobj.getFee();
            }
        }
        return totalFeeCollected;
    }

    /**
     * Setting attributes in session for printing of details of records updated
     * in database
     */
    public void confirmRecordsUpdatePrint() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("mergeOrDeDupl", "DEDUPL");
        if (isToBeDeduplicated()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("actionTaken", "UPDATE");
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("actionTaken", "DELETE");
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("dealerCd", getDeduplicateTradeCertDobj().getDealer());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("tcNumber", getDeduplicateTradeCertDobj().getCertNo());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("dealerChoiceCondition", getDeduplicateTradeCertDobj().getDealerChoiceCondition());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("dobj", getDeduplicateTradeCertDobj());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("deduplicatedTcDobjList", getDeduplicatedTcDobjList());
        PrimeFaces.current().executeScript("PF('printRecordsUpdate').show()");
    }

    /**
     * Printing details of records updated in database
     */
    public String printRecordsUpdateDetails() {
        PrimeFaces.current().executeScript("PF('printRecordsUpdate').hide()");
        return "PrintRecordsUpdate";
    }

    /**
     * @return the DeduplicateTradeCertDobj
     */
    public DeduplicateTradeCertDobj getDeduplicateTradeCertDobj() {
        return DeduplicateTradeCertDobj;
    }

    /**
     * @param DeduplicateTradeCertDobj the DeduplicateTradeCertDobj to set
     */
    public void setDeduplicateTradeCertDobj(DeduplicateTradeCertDobj DeduplicateTradeCertDobj) {
        this.DeduplicateTradeCertDobj = DeduplicateTradeCertDobj;
    }

    /**
     * @return the ADMIN_DEALER
     */
    public String getADMIN_DEALER() {
        return ADMIN_DEALER;
    }

    /**
     * @return the STAFF_DEALER
     */
    public String getNO_ADMIN_DEALER() {
        return NO_ADMIN_DEALER;
    }

    /**
     * @return the disableField
     */
    public Boolean getDisableField() {
        return disableField;
    }

    /**
     * @param disableField the disableField to set
     */
    public void setDisableField(Boolean disableField) {
        this.disableField = disableField;
    }

    /**
     * @return the dealerList
     */
    public List<SelectItem> getDealerList() {
        return dealerList;
    }

    /**
     * @param dealerList the dealerList to set
     */
    public void setDealerList(List<SelectItem> dealerList) {
        this.dealerList = dealerList;
    }

    /**
     * @return the DEFAULT_SELECT_VALUE
     */
    public String getDEFAULT_SELECT_VALUE() {
        return DEFAULT_SELECT_VALUE;
    }

    /**
     * @return the tcDobjsList
     */
    public List<DeduplicateTradeCertDobj> getTcDobjsList() {
        return tcDobjsList;
    }

    /**
     * @param tcDobjsList the tcDobjsList to set
     */
    public void setTcDobjsList(List<DeduplicateTradeCertDobj> tcDobjsList) {
        this.tcDobjsList = tcDobjsList;
    }

    /**
     * @return the DeduplicatedTcDobjList
     */
    public List<DeduplicateTradeCertDobj> getDeduplicatedTcDobjList() {
        return DeduplicatedTcDobjList;
    }

    /**
     * @return the tcRecordsDisplayTableHeading
     */
    public String getTcRecordsDisplayTableHeading() {
        return tcRecordsDisplayTableHeading;
    }

    /**
     * @param tcRecordsDisplayTableHeading the tcRecordsDisplayTableHeading to
     * set
     */
    public void setTcRecordsDisplayTableHeading(String tcRecordsDisplayTableHeading) {
        this.tcRecordsDisplayTableHeading = tcRecordsDisplayTableHeading;
    }

    /**
     * @return the deduplicatedRecordDisplayTableHeading
     */
    public String getdeduplicatedRecordDisplayTableHeading() {
        return getDeduplicatedRecordDisplayTableHeading();
    }

    /**
     * @param deduplicatedRecordDisplayTableHeading the
     * deduplicatedRecordDisplayTableHeading to set
     */
    public void setdeduplicatedRecordDisplayTableHeading(String deduplicatedRecordDisplayTableHeading) {
        this.setDeduplicatedRecordDisplayTableHeading(deduplicatedRecordDisplayTableHeading);
    }

    /**
     * @return the tcNoList
     */
    public List<SelectItem> getTcNoList() {
        return tcNoList;
    }

    /**
     * @param tcNoList the tcNoList to set
     */
    public void setTcNoList(List<SelectItem> tcNoList) {
        this.tcNoList = tcNoList;
    }

    /**
     * @return the deduplicatedRecordDisplayTableHeading
     */
    public String getDeduplicatedRecordDisplayTableHeading() {
        return deduplicatedRecordDisplayTableHeading;
    }

    /**
     * @param deduplicatedRecordDisplayTableHeading the
     * deduplicatedRecordDisplayTableHeading to set
     */
    public void setDeduplicatedRecordDisplayTableHeading(String deduplicatedRecordDisplayTableHeading) {
        this.deduplicatedRecordDisplayTableHeading = deduplicatedRecordDisplayTableHeading;
    }

    /**
     * @return the toBeDeduplicated
     */
    public boolean isToBeDeduplicated() {
        return toBeDeduplicated;
    }

    /**
     * @param toBeDeduplicated the toBeDeduplicated to set
     */
    public void setToBeDeduplicated(boolean toBeDeduplicated) {
        this.toBeDeduplicated = toBeDeduplicated;
    }

    /**
     * @return the renderPrintButton
     */
    public boolean isRenderPrintButton() {
        return renderPrintButton;
    }

    /**
     * @param renderPrintButton the renderPrintButton to set
     */
    public void setRenderPrintButton(boolean renderPrintButton) {
        this.renderPrintButton = renderPrintButton;
    }

    /**
     * @return the tcDobjsListToBeOperatedOn
     */
    public List<DeduplicateTradeCertDobj> getTcDobjsListToBeOperatedOn() {
        return tcDobjsListToBeOperatedOn;
    }

    /**
     * @param tcDobjsListToBeOperatedOn the tcDobjsListToBeOperatedOn to set
     */
    public void setTcDobjsListToBeOperatedOn(List<DeduplicateTradeCertDobj> tcDobjsListToBeOperatedOn) {
        this.tcDobjsListToBeOperatedOn = tcDobjsListToBeOperatedOn;
    }
}
