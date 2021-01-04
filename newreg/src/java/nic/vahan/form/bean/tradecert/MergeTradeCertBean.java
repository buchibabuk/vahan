/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.tradecert;

import java.util.ArrayList;
import java.util.List;
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
import nic.vahan.form.dobj.tradecert.MergeTradeCertDobj;
import nic.vahan.form.impl.tradecert.MergeTradeCertImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author ManuSri
 */
@ManagedBean(name = "mergeTradeCertBean", eager = true)
@ViewScoped
public class MergeTradeCertBean {

    /**
     * Properties
     */
    private static final Logger LOGGER = Logger.getLogger(MergeTradeCertBean.class);
    private final String ADMIN_DEALER;
    private final String NO_ADMIN_DEALER;
    private final String DEFAULT_SELECT_VALUE = "-SELECT-";
    private final MergeTradeCertImpl impl;
    private final List<MergeTradeCertDobj> mergedTcDobjList;
    private final SessionVariables sessionVariables;
    private String tcRecordsDisplayTableHeading;
    private String mergedRecordDisplayTableHeading;
    private List<SelectItem> dealerList;
    private List<SelectItem> vehCatgList;
    private List<MergeTradeCertDobj> tcDobjsList;
    private MergeTradeCertDobj mergeTradeCertDobj;
    private Boolean disableField;
    private boolean toBeMerged;
    private boolean renderPrintButton;

    /**
     * Default Constructor
     */
    public MergeTradeCertBean() {
        ADMIN_DEALER = "Admin_Dealer";
        NO_ADMIN_DEALER = "No_Admin_Dealer";
        mergeTradeCertDobj = new MergeTradeCertDobj();
        sessionVariables = new SessionVariables();
        mergedTcDobjList = new ArrayList<>();
        impl = new MergeTradeCertImpl();
    }

    /**
     * Post Construct Bean Initializer Method
     */
    @PostConstruct
    public void init() {
        getMergeTradeCertDobj().setStateCd(sessionVariables.getStateCodeSelected());
        getMergeTradeCertDobj().setRtoOfficeCode(sessionVariables.getOffCodeSelected());
        getMergeTradeCertDobj().setRtoOfficeName(ServerUtil.getOfficeName(sessionVariables.getOffCodeSelected(),
                sessionVariables.getStateCodeSelected()));
        getMergeTradeCertDobj().setDealerChoiceCondition(ADMIN_DEALER);
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
        if (getMergeTradeCertDobj() != null) {
            getMergeTradeCertDobj().reset();
        }
        setDealerList(null);
        setVehCatgList(null);
        setTcDobjsList(null);
        getMergedTcDobjList().clear();
        setDisableField(Boolean.FALSE);
        setToBeMerged(Boolean.FALSE);
        setMergeTradeCertDobj(new MergeTradeCertDobj());
        setRenderPrintButton(false);
        init();
    }

    /**
     * Listener called on the event of selection of dealerChoiceCondition
     */
    public void dealerChoiceConditionListener(AjaxBehaviorEvent event) {
        getMergeTradeCertDobj().setDealer("");
        setDealerList(null);
        getMergeTradeCertDobj().setVehCatg("");
        setVehCatgList(null);
        switch (getMergeTradeCertDobj().getDealerChoiceCondition()) {
            case "Admin_Dealer":
                getMergeTradeCertDobj().setDealerChoiceCondition(ADMIN_DEALER);
                break;
            case "No_Admin_Dealer":
                getMergeTradeCertDobj().setDealerChoiceCondition(NO_ADMIN_DEALER);
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
        switch (getMergeTradeCertDobj().getDealerChoiceCondition()) {
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
        getMergeTradeCertDobj().setVehCatg("");
        setVehCatgList(null);
        setRenderPrintButton(false);
        if (!getDEFAULT_SELECT_VALUE().equals(getMergeTradeCertDobj().getDealer())) {
            settingDealerCdIdInDobjSeparately();
            try {
                fillVehCatgList();
            } catch (VahanException ve) {
                JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.ERROR);
            }
        }
    }

    /**
     * Filling the list of vehicle categories according to the selection of
     * dealer from the database
     */
    public void fillVehCatgList() throws VahanException {
        List<SelectItem> listOfVehCatg = impl.fillVehCatgs(getMergeTradeCertDobj().getDealer());
        if (listOfVehCatg != null) {
            this.setVehCatgList(listOfVehCatg);
        }
    }

    /**
     * Proceed action command to fetch the resultant records from the database
     * on the basis of selected dealer and vehicle category
     */
    public void proceed() {
        List<MergeTradeCertDobj> listOfTcDobjs;
        try {
            settingDealerCdIdInDobjSeparately();
            listOfTcDobjs = impl.searchTCsForDealerAndVehCatg(getMergeTradeCertDobj().getDealer(),
                    getMergeTradeCertDobj().getVehCatg(), getMergeTradeCertDobj().getDealerChoiceCondition());

        } catch (VahanException ve) {
            JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.ERROR);
            return;
        }
        if (listOfTcDobjs != null) {
            setTcDobjsList(listOfTcDobjs);
            MergeTradeCertDobj dobjFrom = listOfTcDobjs.get(0);
            getMergeTradeCertDobj().setDealerName(dobjFrom.getDealerName());
            getMergeTradeCertDobj().setVehCatgName(dobjFrom.getVehCatgName());
            StringBuilder heading = new StringBuilder();
            heading = heading.append("List of multiple entries for <b>")
                    .append(getMergeTradeCertDobj().getDealerName())
                    .append("</b> (<b>")
                    .append(getMergeTradeCertDobj().getDealer()).append("</b>) ");
            if ("Admin_Dealer".equals(getMergeTradeCertDobj().getDealerChoiceCondition())) {
                heading = heading.append(" having user-Id as <b>")
                        .append(getMergeTradeCertDobj().getUserId())
                        .append("</b>");
            }
            heading = heading.append(" and for vehicle category <b>")
                    .append(getMergeTradeCertDobj().getVehCatgName())
                    .append("</b>");
            setTcRecordsDisplayTableHeading(heading.toString());
            getMergeTradeCertDobj().setFee(calculateTotalFeeCollected(listOfTcDobjs));
            setDisableField(Boolean.TRUE);
        }
    }

    /**
     * Merge action command to merge the trade certificate records fetched in
     * "proceed" action
     */
    public void merge() {
        try {
            validateData("for_merge");
        } catch (VahanException ve) {
            JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.ERROR);
            return;
        }
        MergeTradeCertDobj dobjRetained = ((MergeTradeCertDobj) getTcDobjsList().get(0)).copy(new MergeTradeCertDobj());
        setMergedRecordDisplayTableHeading("<u>Merging Required :</u> <b>" + getMergeTradeCertDobj().getDealerName() + "</b> (<b>" + getMergeTradeCertDobj().getDealer() + "</b>) "
                + " should have only one trade certificate for vehicle category <b>" + getMergeTradeCertDobj().getVehCatgName() + "</b>");
        getMergedTcDobjList().clear();
        getMergedTcDobjList().add(dobjRetained);
        setToBeMerged(Boolean.TRUE);
    }

    /**
     * Save action command to save the merged trade certificate record in
     * database
     */
    public void update() {
        try {
            Boolean updated = impl.update(getMergedTcDobjList(), getTcDobjsList());
            setMergeTradeCertDobj((MergeTradeCertDobj) getMergedTcDobjList().get(0));
            if (updated) {
                JSFUtils.setFacesMessage("Records updated in database having following details:-", null, JSFUtils.INFO);
                JSFUtils.setFacesMessage("Dealer Name : <b>" + getMergeTradeCertDobj().getDealerName() + "</b>", null, JSFUtils.INFO);
                JSFUtils.setFacesMessage("Dealer Code : <b>" + getMergeTradeCertDobj().getDealer() + "</b>", null, JSFUtils.INFO);
                JSFUtils.setFacesMessage("Vehicle Category Name : <b>" + getMergeTradeCertDobj().getVehCatgName() + "</b>", null, JSFUtils.INFO);
                JSFUtils.setFacesMessage("System Generated Trade Certificate Number : <b>" + getMergeTradeCertDobj().getNewMergeCertNo() + "</b>", null, JSFUtils.INFO);
                JSFUtils.setFacesMessage("Validity : (<b>" + getMergeTradeCertDobj().getValidFromStr() + "</b> - <b>" + getMergeTradeCertDobj().getValidUptoStr() + "</b>)", null, JSFUtils.INFO);
                JSFUtils.setFacesMessage("Number of Vehicles : <b>" + getMergeTradeCertDobj().getNoOfVeh() + "</b>", null, JSFUtils.INFO);
                setRenderPrintButton(true);
            } else {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
        } catch (VahanException ve) {
            setRenderPrintButton(false);
            JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.ERROR);
        }
        getMergeTradeCertDobj().setStateCd(sessionVariables.getStateCodeSelected());
        getMergeTradeCertDobj().setRtoOfficeCode(sessionVariables.getOffCodeSelected());
        getMergeTradeCertDobj().setRtoOfficeName(ServerUtil.getOfficeName(sessionVariables.getOffCodeSelected(),
                sessionVariables.getStateCodeSelected()));
    }

    /**
     * Dispose action command to prepare list for deletion of the trade
     * certificate records fetched in "proceed" action having no fee
     */
    public void dispose() {
        getMergedTcDobjList().clear();
        setMergedRecordDisplayTableHeading("<u>Disposing Required :</u> <b>" + getMergeTradeCertDobj().getDealerName() + "</b> (<b>" + getMergeTradeCertDobj().getDealer() + "</b>) "
                + " should have only those trade certificate which have fee submitted for vehicle category <b>" + getMergeTradeCertDobj().getVehCatgName() + "</b>");
        for (MergeTradeCertDobj dobj : getTcDobjsList()) {
            if (dobj.getFee() == null || (dobj.getFee() != null && dobj.getFee() == 0.0)) {
                MergeTradeCertDobj dobjRetained = dobj.copy(new MergeTradeCertDobj());
                getMergedTcDobjList().add(dobjRetained);
            }
        }
        if (getMergedTcDobjList().isEmpty()) {
            JSFUtils.setFacesMessage("No record found for disposal as 'Fee' has been submitted for all the records.", null, JSFUtils.ERROR);
        }
    }

    /**
     * Delete Trade Certificate when Admin-Id is NOT found in database and fee
     * not found
     */
    public void delete() {
        try {
            Boolean deleted = impl.delete(getMergedTcDobjList());
            setMergeTradeCertDobj((MergeTradeCertDobj) getMergedTcDobjList().get(0));
            if (deleted) {
                JSFUtils.setFacesMessage("Records disposed in database for following details:-", null, JSFUtils.INFO);
                JSFUtils.setFacesMessage("Dealer Name : <b>" + getMergeTradeCertDobj().getDealerName() + "</b>", null, JSFUtils.INFO);
                JSFUtils.setFacesMessage("Dealer Code : <b>" + getMergeTradeCertDobj().getDealer() + "</b>", null, JSFUtils.INFO);
                JSFUtils.setFacesMessage("Vehicle Category Name : <b>" + getMergeTradeCertDobj().getVehCatgName() + "</b>", null, JSFUtils.INFO);
                setRenderPrintButton(true);
            } else {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
        } catch (VahanException ve) {
            setRenderPrintButton(false);
            JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.ERROR);
        }
        getMergeTradeCertDobj().setStateCd(sessionVariables.getStateCodeSelected());
        getMergeTradeCertDobj().setRtoOfficeCode(sessionVariables.getOffCodeSelected());
        getMergeTradeCertDobj().setRtoOfficeName(ServerUtil.getOfficeName(sessionVariables.getOffCodeSelected(),
                sessionVariables.getStateCodeSelected()));
    }

    /**
     * Validating records collected for combination dealer and vehicle category
     * for further actions to be taken
     */
    private void validateData(String fromAction) throws VahanException {
        int countRecordsForWhichFeeTaken = 0;
        for (MergeTradeCertDobj dobj : getTcDobjsList()) {
            /**
             * Check for interrupting action, if 'fee' is found to be submitted
             */
            if (dobj.getFee() != null && dobj.getFee() != 0.0) {
                countRecordsForWhichFeeTaken += 1;
                if ("for_merge".equals(fromAction) && countRecordsForWhichFeeTaken > 1) {
                    throw new VahanException("Record cannot be merged as 'Fee' has been submitted for more than one record. Please cancel records using 'Trade-Cert-Cancel' option.");
                } else if ("for_dispose".equals(fromAction) && countRecordsForWhichFeeTaken > 0) {
                    throw new VahanException("Record cannot be disposed as 'Fee' has been submitted. Please cancel records using 'Trade-Cert-Cancel' option.");
                }
            }
        }
    }

    /**
     * Split DealerCd and UserId and set in dobj separately
     */
    private void settingDealerCdIdInDobjSeparately() {
        String dealerCdStr = getMergeTradeCertDobj().getDealer();
        if (dealerCdStr.contains("#")) {
            getMergeTradeCertDobj().setDealer(dealerCdStr.split("#")[0]);
            getMergeTradeCertDobj().setUserId(dealerCdStr.split("#")[1]);
        } else {
            getMergeTradeCertDobj().setDealer(dealerCdStr);
        }
    }

    /**
     * Calculate total fee collected from all the dobjs in the tcRecordsList
     */
    private Double calculateTotalFeeCollected(List<MergeTradeCertDobj> listOfTcDobjs) {
        Double totalFeeCollected = null;
        for (MergeTradeCertDobj dobj : listOfTcDobjs) {
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
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("mergeOrDeDupl", "MERGE");
        if (isToBeMerged()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("actionTaken", "UPDATE");
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("actionTaken", "DELETE");
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("dealerCd", getMergeTradeCertDobj().getDealer());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("vehCatg", getMergeTradeCertDobj().getVehCatg());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("dealerChoiceCondition", getMergeTradeCertDobj().getDealerChoiceCondition());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("dobj", getMergeTradeCertDobj());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("tcDobjList", getTcDobjsList());
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
     * @return the mergeTradeCertDobj
     */
    public MergeTradeCertDobj getMergeTradeCertDobj() {
        return mergeTradeCertDobj;
    }

    /**
     * @param mergeTradeCertDobj the mergeTradeCertDobj to set
     */
    public void setMergeTradeCertDobj(MergeTradeCertDobj mergeTradeCertDobj) {
        this.mergeTradeCertDobj = mergeTradeCertDobj;
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
     * @return the vehCatgList
     */
    public List<SelectItem> getVehCatgList() {
        return vehCatgList;
    }

    /**
     * @param vehCatgList the vehCatgList to set
     */
    public void setVehCatgList(List<SelectItem> vehCatgList) {
        this.vehCatgList = vehCatgList;
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
    public List<MergeTradeCertDobj> getTcDobjsList() {
        return tcDobjsList;
    }

    /**
     * @param tcDobjsList the tcDobjsList to set
     */
    public void setTcDobjsList(List<MergeTradeCertDobj> tcDobjsList) {
        this.tcDobjsList = tcDobjsList;
    }

    /**
     * @return the mergedTcDobjList
     */
    public List<MergeTradeCertDobj> getMergedTcDobjList() {
        return mergedTcDobjList;
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
     * @return the mergedRecordDisplayTableHeading
     */
    public String getMergedRecordDisplayTableHeading() {
        return mergedRecordDisplayTableHeading;
    }

    /**
     * @param mergedRecordDisplayTableHeading the
     * mergedRecordDisplayTableHeading to set
     */
    public void setMergedRecordDisplayTableHeading(String mergedRecordDisplayTableHeading) {
        this.mergedRecordDisplayTableHeading = mergedRecordDisplayTableHeading;
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
     * @return the toBeMerged
     */
    public boolean isToBeMerged() {
        return toBeMerged;
    }

    /**
     * @param toBeMerged the toBeMerged to set
     */
    public void setToBeMerged(boolean toBeMerged) {
        this.toBeMerged = toBeMerged;
    }
}
