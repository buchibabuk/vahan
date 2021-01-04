/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.java.util.CommonUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.Regn_series_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.VMRegnAvailableDobj;
import nic.vahan.form.impl.RegnSeriesImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author AMBRISH
 */
@ManagedBean(name = "RegnSeries")
@ViewScoped
public class RegnSeriesBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(RegnSeriesBean.class);
    private Regn_series_dobj regnSeries_entry = new Regn_series_dobj();
    private Regn_series_dobj dobjremove = new Regn_series_dobj();
    private Regn_series_dobj regnSeries_selected = new Regn_series_dobj();
    private List<Regn_series_dobj> listRegnSeries = new ArrayList<>();
    private List<Regn_series_dobj> listApproveSeries = new ArrayList<>();
    private CommandButton save = new CommandButton();
    private CommandButton edit = new CommandButton();
    private CommandButton delete = new CommandButton();
    private CommandButton approve = new CommandButton();
    private CommandButton approveUpdate = new CommandButton();
    RegnSeriesImpl regnSeriesImpl = new RegnSeriesImpl();
    private List<VMRegnAvailableDobj> vmAvailableRegistration = new ArrayList<>();
    private String prefixPart1;
    private String prefixPart2;
    private String nextPrefixPart1;
    private String nextPrefixPart2;
    private boolean constPreFix;
    private boolean constPreFix2;
    private boolean constNextPreFix;
    private boolean constNextPreFix2;
    private boolean constUpperRange;
    private boolean constRunnNum;
    private SessionVariables sessionVariables = null;
    private boolean prefixPart2Req = true;
    String msg;
    private String noGenType;
    private boolean editDisable;
    private boolean viewAvailRegn;

    public RegnSeriesBean() {
        prefixPart1 = Util.getUserStateCode();
        constPreFix = true;
        constNextPreFix = true;
        viewAvailRegn = false;
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getOffCodeSelected() == 0
                    || sessionVariables.getEmpCodeLoggedIn() == null
                    || sessionVariables.getUserCatgForLoggedInUser() == null) {
                JSFUtils.showMessage("Something went wrong, Please try again...");
                return;
            }
            listRegnSeries = regnSeriesImpl.getAllRegnSeries();
            listApproveSeries = regnSeriesImpl.getRecordfromVa();

            TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
            if (tmConfigurationDobj != null) {
                noGenType = tmConfigurationDobj.getRegn_gen_type();
                regnSeries_entry.setNo_gen_type(noGenType);
            }
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
        }

        save.setRendered(false);
        edit.setRendered(false);
        delete.setRendered(false);
        approve.setRendered(false);
        approveUpdate.setRendered(false);

        if ("M,R".contains(noGenType)) {//for view avail regn No
            viewAvailRegn = true;
        }
    }

    public List<Regn_series_dobj> getAllRegnSeries() {
        return listRegnSeries;
    }

    public void addRegnSeries() throws Exception {
        try {
            String mergePrefix = prefixPart1 + prefixPart2;
            String mergeNextPrefix = nextPrefixPart1 + nextPrefixPart2;

            if (mergePrefix == null || mergePrefix.equals("") || mergePrefix.length() < 5) {
                if (prefixPart2Req) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Prefix Length (Part-1 and Part-2) Must not be Less than 5 Character"));
                    return;
                }
            }
            if (prefixPart2.contains("I") || prefixPart2.contains("O")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "'I' and 'O' Character are not allowed in Prefix Series"));
                return;
            }
            if (nextPrefixPart2.contains("I") || nextPrefixPart2.contains("O")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "'I' and 'O' Character  are not allowed in Next Prefix Series"));
                return;
            }
            if (mergeNextPrefix != null && mergeNextPrefix.length() >= 5) {
                regnSeries_entry.setNext_prefix_series(mergeNextPrefix);
            } else if (mergeNextPrefix != null && mergeNextPrefix.length() > 0 && mergeNextPrefix.length() < 5) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Next Prefix Length (Part-1 and Part-2) Must not be Less than 5 Character"));
                return;
            }

            regnSeries_entry.setPrefix_series(mergePrefix);

            if (regnSeries_entry.getLower_range_no() > regnSeries_entry.getUpper_range_no()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Upper Range Must be Greater than Lower Range!!!", "Upper Range Must be Greater than Lower Range!!!"));
                return;
            }

            if (regnSeries_entry.getRunning_no() > regnSeries_entry.getUpper_range_no()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Running No Can not be greater than Upper Range!!!", "Running No Can not be greater than Upper Range!!!"));
                return;
            }

            if (mergePrefix.equalsIgnoreCase(mergeNextPrefix)) {
                msg = "Prefix Series and Next Prefix Series Can not be Same";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, msg, msg));
                return;
            }

            getRegnSeries_entry().setOff_cd(Util.getSelectedSeat().getOff_cd());
            getRegnSeries_entry().setState_cd(Util.getUserStateCode());
            getRegnSeries_entry().setEntered_by(Util.getEmpCode());
            if (validateForm()) {
                if (regnSeries_entry.isIsApprovebutton()) {
                    regnSeriesImpl.addRegnSeries(getRegnSeries_entry());
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Registration Series Sucessfully Saved."));
                } else {
                    getRegnSeries_entry().setAction_type("N");
                    regnSeriesImpl.saveRegnSeriesintoVa(getRegnSeries_entry());
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Record Saved Initailly, Please Approve Now to Save Registration Series."));
                }
                listRegnSeries = regnSeriesImpl.getAllRegnSeries();
                listApproveSeries = regnSeriesImpl.getRecordfromVa();
                reset();
            }
            PrimeFaces.current().executeScript("PF('approveDialog').hide()");
            PrimeFaces.current().executeScript("PF('entryDialog').hide()");

        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
        }
    }

    public boolean validateForm() throws VahanException {
        boolean flag = true;

        if (regnSeries_entry.getPrefix_series().length() > 6) {
            flag = false;
            throw new VahanException("Please Check Series Prefix. Length Should be 6 Character.");
        } else {
            String str = regnSeries_entry.getPrefix_series().substring(0, 2);
            String validSeries = Util.getUserStateCode();
            if (!str.equalsIgnoreCase(validSeries)) {
                if (!("OR".contains(Util.getUserStateCode()) && "OD".equals(prefixPart1.substring(0, 2)))) {
                    flag = false;
                    throw new VahanException("Please Check Series Prefix. This is not belong to the state you are Logged in. Series should start with [ " + validSeries + " ]");
                }
            }
        }

        if (regnSeries_entry.getNext_prefix_series().length() > 0) {
            String str = regnSeries_entry.getNext_prefix_series().substring(0, 2);
            String validSeries = Util.getUserStateCode();
            if (!str.equalsIgnoreCase(validSeries)) {
                if (!("OR".contains(Util.getUserStateCode()) && "OD".equals(prefixPart1.substring(0, 2)))) {
                    flag = false;
                    throw new VahanException("Please Check Series  Next Prefix. This is not belong to the state you are Logged in. Series should start with [ " + validSeries + " ]");
                }
            }

        }
        if (regnSeries_entry.getLower_range_no() < 0 || regnSeries_entry.getLower_range_no() > 9999) {
            flag = false;
            throw new VahanException("Please Check Lower Range");
        }

        if (regnSeries_entry.getUpper_range_no() < 0 || regnSeries_entry.getUpper_range_no() > 9999) {
            flag = false;
            throw new VahanException("Please Check Upper Range");
        }

        if (regnSeries_entry.getRunning_no() < 0 || regnSeries_entry.getRunning_no() > 9999) {
            flag = false;
            throw new VahanException("Please Check Running Number");
        }

        return flag;

    }

    public void updateRegnSeries() throws Exception {
        try {

            String mergePrefix = prefixPart1 + prefixPart2;
            String mergeNextPrefix = nextPrefixPart1 + nextPrefixPart2;

            if (mergePrefix == null || mergePrefix.equals("") || mergePrefix.length() < 5) {
                if (!prefixPart2Req) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Prefix Length (Part-1 and Part-2) Must not be Less than 5 Character"));
                    return;
                }
            }
            if (nextPrefixPart2.contains("I") || nextPrefixPart2.contains("O")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "'I' and 'O' Character  are not allowed in Next Prefix Series"));
                return;
            }

            if (mergeNextPrefix != null && mergeNextPrefix.length() >= 5) {
                regnSeries_entry.setNext_prefix_series(mergeNextPrefix);
            } else if (mergeNextPrefix != null && mergeNextPrefix.length() > 0 && mergeNextPrefix.length() < 5) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Next Prefix Length (Part-1 and Part-2) Must not be Less than 5 Character"));
                return;
            }

            if (regnSeries_selected.getNext_prefix_series() != null
                    && !regnSeries_selected.getNext_prefix_series().isEmpty()) {
                if (regnSeries_selected.getPrefix_series().equalsIgnoreCase(mergePrefix) && regnSeries_selected.getNext_prefix_series() != null) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Prefix Series (Part-1 and Part-2) Must not be Same as Old Prefix Series"));
                    return;
                }
            }

            if (regnSeries_selected != null && ((regnSeries_selected.getRunning_no() >= regnSeries_selected.getUpper_range_no()) && mergePrefix.equalsIgnoreCase(regnSeries_selected.getPrefix_series()))) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Prefix Series [" + regnSeries_selected.getPrefix_series() + "] is already used, Please provide new Prefix Series."));
                return;
            }

            if (regnSeries_selected.getNext_prefix_series() != null
                    && !regnSeries_selected.getNext_prefix_series().isEmpty()
                    && mergeNextPrefix != null
                    && !mergeNextPrefix.isEmpty()
                    && regnSeries_selected.getNext_prefix_series().equalsIgnoreCase(mergeNextPrefix)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Next Prefix Series (Part-1 and Part-2) Must not be Same as Old Next Prefix Series"));
                return;
            }

            String prefixSeriesValidation = regnSeriesImpl.validatePrefixSeries(mergePrefix, mergeNextPrefix, regnSeries_entry.getState_cd(), regnSeries_entry.getOff_cd(), regnSeries_entry.getSeries_id());
            if (prefixSeriesValidation != null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", prefixSeriesValidation + " is Already Used. Choose Different Series"));
                return;
            }
            if (!CommonUtils.isNullOrBlank(mergeNextPrefix)) {
                String nextprefixSeriesValidation = regnSeriesImpl.validateNextPrefixSeries(mergePrefix, mergeNextPrefix, regnSeries_entry.getState_cd(), regnSeries_entry.getOff_cd(), regnSeries_entry.getSeries_id());
                if (nextprefixSeriesValidation != null) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", nextprefixSeriesValidation + " is Already Used. Choose Different Series"));
                    return;
                }
            }

            regnSeries_entry.setPrefix_series(mergePrefix);

            if (regnSeries_entry.getLower_range_no() > regnSeries_entry.getUpper_range_no()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Upper Range Must be Greater than Lower Range!!!", "Upper Range Must be Greater than Lower Range!!!"));
                return;
            }

            if (regnSeries_entry.getUpper_range_no() < regnSeries_entry.getRunning_no()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Upper Range Must be Greater than Running Number!!!", "Upper Range Must be Greater than Running Number!!!"));
                return;
            }

            if (mergePrefix.equalsIgnoreCase(mergeNextPrefix)) {
                msg = "Prefix Series and Next Prefix Series Can not be Same";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, msg, msg));
                return;
            }

            getRegnSeries_entry().setOff_cd(Util.getSelectedSeat().getOff_cd());
            getRegnSeries_entry().setState_cd(Util.getUserStateCode());
            getRegnSeries_entry().setEntered_by(Util.getEmpCode());
            if (validateForm()) {
                getRegnSeries_entry().setAction_type("O");
                regnSeriesImpl.saveRegnSeriesintoVa(getRegnSeries_entry());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Record Save Initailly, Please Approve Now for Update Registration Series."));
                listRegnSeries = regnSeriesImpl.getAllRegnSeries();
                listApproveSeries = regnSeriesImpl.getRecordfromVa();
            }
            edit.setRendered(false);
            delete.setRendered(false);
            PrimeFaces.current().executeScript("PF('approveDialog').hide()");
            PrimeFaces.current().executeScript("PF('entryDialog').hide()");
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
        }

    }

    public void reset() {

        regnSeries_entry = new Regn_series_dobj();
        regnSeries_entry.setCriteria_formula(null);
        regnSeries_entry.setEntered_by(null);
        regnSeries_entry.setEntered_on(null);
        regnSeries_entry.setLower_range_no(1);
        regnSeries_entry.setUpper_range_no(9999);
        regnSeries_entry.setNext_prefix_series(null);
        regnSeries_entry.setNo_gen_type(null);
        regnSeries_entry.setRunning_no(1);

        prefixPart1 = Util.getUserStateCode() + Util.getSelectedSeat().getOff_cd();
        this.prefixPart2 = "";
        this.nextPrefixPart1 = "";
        this.nextPrefixPart2 = "";

        save.setRendered(false);
        edit.setRendered(false);
        delete.setRendered(false);
        prefixPart2Req = true;
        approve.setRendered(false);
        approveUpdate.setRendered(false);

    }

    public void viewAvailRegnNo(Regn_series_dobj dobj) throws VahanException {
        String facesMessages = null;
        try {
            List<VMRegnAvailableDobj> list = regnSeriesImpl.getAvailRegnNo(dobj);
            if (!list.isEmpty()) {
                vmAvailableRegistration = list;
            }
            PrimeFaces.current().ajax().update("RegnMasterListForm:dialogAvailRegNo");
            PrimeFaces.current().executeScript("PF('varAvailableRegNo').show()");

        } catch (VahanException vex) {
            facesMessages = vex.getMessage();
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", facesMessages));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            facesMessages = TableConstants.SomthingWentWrong;
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", facesMessages));
        }
    }

    public void editRegnSeries(Regn_series_dobj dobj) {
        String facesMessages = null;
        boolean insertFlag = false;
        int runningNum = 0;
        constPreFix2 = false;
        constRunnNum = false;
        constUpperRange = false;
        reset();

        if ("KL,TN".contains(Util.getUserStateCode())) {
            editDisable = true;
            facesMessages = "Editing is not allowed for " + ServerUtil.getStateNameByStateCode(Util.getUserStateCode());
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", facesMessages));
            return;
        } else {
            editDisable = false;
        }

        try {
            if (dobj != null) {
                dobjremove = dobj;
                regnSeries_selected = (Regn_series_dobj) dobj.clone();
                if (!regnSeries_selected.isIsApprovebutton() && listApproveSeries != null) {
                    for (int i = 0; i < listApproveSeries.size(); i++) {
                        if (regnSeries_selected.getCriteria_formula().equals(listApproveSeries.get(i).getCriteria_formula())) {
                            throw new VahanException("Series is pending at Approval Stage. Kindly Approve.");
                        }
                    }
                }
                runningNum = dobj.getRunning_no();
                if (dobj.getRunning_no() == 0) {
                    regnSeries_entry.setRunning_no(1);
                    dobj.setRunning_no(1);
                }
                if (dobj.getLower_range_no() == 0) {
                    regnSeries_entry.setLower_range_no(1);
                    dobj.setLower_range_no(1);
                }
                if (dobj.getUpper_range_no() == 0) {
                    regnSeries_entry.setUpper_range_no(9999);
                    dobj.setUpper_range_no(9999);
                }
                if (!CommonUtils.isNullOrBlank(dobj.getPrefix_series())) {
                    prefixPart1 = dobj.getPrefix_series().substring(0, 4);
                }
            }

            if ((dobj.getEntered_by() == null || dobj.getEntered_by().isEmpty())
                    && (dobj.getEntered_on() == null || dobj.getEntered_on().isEmpty())) {
                insertFlag = true;
                edit.setRendered(false);
                save.setRendered(true);
                constPreFix = false;//for enable to insert part-1 of prefix series
                constNextPreFix = false;//for enable to insert part-1 of Next prefix series
            }

            regnSeries_entry = dobj;
            dobj.setState_cd(Util.getUserStateCode());
            dobj.setOff_cd(Util.getSelectedSeat().getOff_cd());
            regnSeries_entry.setNo_gen_type(noGenType);//for fixing the No. Generation Type

            if (!insertFlag) {
                if (dobj.getPrefix_series().length() >= 5) {
                    this.prefixPart1 = dobj.getPrefix_series().substring(0, 4);
                    this.prefixPart2 = dobj.getPrefix_series().substring(4, dobj.getPrefix_series().length());
                    constPreFix = true;//for disable Prefix Part-1 when Updating the Series
                } else if (dobj.getPrefix_series() != null && dobj.getPrefix_series().length() > 0 && dobj.getPrefix_series().length() < 5) {
                    if (!prefixPart2Req) {
                        facesMessages = "Prefix Length (Part-1 and Part-2) Must not be Less than 5 Character";
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", facesMessages));
                        edit.setRendered(false);
                        return;
                    }
                }

                if (dobj.getNext_prefix_series().length() >= 5) {
                    this.nextPrefixPart1 = dobj.getNext_prefix_series().substring(0, 4);
                    this.nextPrefixPart2 = dobj.getNext_prefix_series().substring(4, dobj.getNext_prefix_series().length());
                    constNextPreFix = true;//for disable Next Seriess Prefix Part-1 when Updating the Series
                } else if (dobj.getNext_prefix_series() != null && dobj.getNext_prefix_series().length() > 0 && dobj.getNext_prefix_series().length() < 5) {
                    facesMessages = "Next Prefix Series Length (Part-1 and Part-2) Must not be Less than 5 Character";
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", facesMessages));
                    edit.setRendered(false);
                    return;
                } else if (dobj.getNext_prefix_series().isEmpty()) {
                    constNextPreFix = false;//for enable to insert part-1 of Next prefix series
                }

                save.setRendered(false);

                if (dobj.getRunning_no() <= dobj.getUpper_range_no()) { //<= condition
                    boolean isRunningNoFancy = false;
                    if ((dobj.getRunning_no() == dobj.getUpper_range_no())) {
                        isRunningNoFancy = regnSeriesImpl.isRunningNoIsFancyNo(String.valueOf(dobj.getRunning_no()), dobj.getState_cd());
                        if (isRunningNoFancy) {
                            dobj.setRunning_no(dobj.getRunning_no() + 1);
                        }
                        if (!isRunningNoFancy && dobj.getUpper_range_no() <= 9999) {
                            prefixPart2Req = false;

                        }
                    }
                }

                if (dobj.getRunning_no() > dobj.getUpper_range_no()) {
                    List<VMRegnAvailableDobj> list = regnSeriesImpl.getAvailRegnNo(dobj);
                    if (!list.isEmpty()) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getStatus().equalsIgnoreCase("A")) {
                                vmAvailableRegistration = list;
                                PrimeFaces.current().executeScript("PF('varAvailableRegNo').show()");
                                edit.setRendered(false);
                                break;
                            }
                        }
                    }
                }

                edit.setRendered(true);
            }
            if (CommonUtils.isNullOrBlank(dobj.getNext_prefix_series()) && !CommonUtils.isNullOrBlank(dobj.getPrefix_series()) && dobj.getUpper_range_no() > dobj.getRunning_no() && dobj.getLower_range_no() != 0) {
                constNextPreFix = false;
                edit.setRendered(true);
                if (!CommonUtils.isNullOrBlank(dobj.getPrefix_series())) {
                    constPreFix = true;
                    constPreFix2 = false;
                    constRunnNum = true;
                    constUpperRange = true;
                }
            }
            if ((CommonUtils.isNullOrBlank(regnSeries_selected.getPrefix_series())) && (CommonUtils.isNullOrBlank(regnSeries_selected.getPrefix_series()))) {
                prefixPart2Req = false;
            }
            if (runningNum > 0) {
                delete.setRendered(true);
            }
            if (regnSeries_selected.isIsApprovebutton()) {
                if ("N".equals(dobj.getAction_type())) {
                    approve.setRendered(true);
                    edit.setRendered(false);
                    save.setRendered(false);
                    delete.setRendered(false);
                    prefixPart2Req = false;
                } else {
                    delete.setRendered(false);
                    edit.setRendered(false);
                    approveUpdate.setRendered(true);
                    if (prefixPart2 == null || prefixPart2.equalsIgnoreCase("")) {
                        constPreFix2 = true;
                    }
                }
                PrimeFaces.current().ajax().update("RegnMasterListForm:approve_output_panel_id");
                PrimeFaces.current().executeScript("PF('approveDialog').show()");
            }
            if (!regnSeries_selected.isIsApprovebutton()) {
                PrimeFaces.current().ajax().update("RegnMasterListForm:ent_series_dlg_panel_id");
                PrimeFaces.current().executeScript("PF('entryDialog').show()");
            }
        } catch (VahanException vex) {
            facesMessages = vex.getMessage();
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", facesMessages));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            facesMessages = TableConstants.SomthingWentWrong;
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", facesMessages));
        }
    }

    public void updateRegnNextSeries(RowEditEvent event) {
        try {
            Regn_series_dobj dobj = ((Regn_series_dobj) event.getObject());
            dobj.setOff_cd(Util.getSelectedSeat().getOff_cd());
            dobj.setState_cd(Util.getUserStateCode());
            dobj.setEntered_by(Util.getEmpCode());
            regnSeriesImpl.updateNextRegnSeries(dobj);
            reset();
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
        }
    }

    public void deleteRegnSeries() {
        try {

            regnSeriesImpl.deleteRegnSeries(dobjremove);
            listRegnSeries.remove(dobjremove);
            listRegnSeries = regnSeriesImpl.getAllRegnSeries();
            reset();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Registration Series Sucessfully Deleted."));
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
        }
    }

    public void prefixSeriesCheckListener() {

        if (this.prefixPart1.length() < 2) {
            return;
        }

        if (!this.prefixPart1.substring(0, 2).equalsIgnoreCase(Util.getUserStateCode())) {
            if (!("OR".contains(Util.getUserStateCode()) && "OD".equals(prefixPart1.substring(0, 2)))) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Prefeix Series Part-1 Must Start with State Code!!!", "Prefeix Series Part-1 Must Start with State Code!!!"));
                this.prefixPart1 = Util.getUserStateCode();
                return;
            }
        }

        boolean isNumeric = true;
        String ch = String.valueOf(this.prefixPart1.charAt(2));
        for (int i = 0; i <= 9; i++) {
            if (ch.equalsIgnoreCase(String.valueOf(i))) {
                isNumeric = false;
            }
        }

        if (isNumeric) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "3rd Character of Prefix Series Part-1 Must be Numeric!!!", "3rd Character of Prefix Series Part-1 Must be Numeric!!!"));
            return;
        }

    }

    // Listner for next prefix series
    public void nextprefixSeriesCheckListener() {

        if (this.nextPrefixPart1.length() < 2) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Next Prefeix Series Part-1 Length is Less than Minimum Length of 4 Character!!!", "Next Prefeix Series Part-1 Length is Less than Minimum Length of 4 Character!!!"));
            return;
        }

        if (this.nextPrefixPart1.length() < 4) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Next Prefeix Series Part-1 Length is Less than Minimum Length of 4 Character!!!", "Next Prefeix Series Part-1 Length is Less than Minimum Length of 4 Character!!!"));
            return;
        }

        if (!this.nextPrefixPart1.substring(0, 2).equalsIgnoreCase(Util.getUserStateCode())) {
            if (!("OR".contains(Util.getUserStateCode()) && "OD".equals(prefixPart1.substring(0, 2)))) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Next Prefeix Series Part-1 Must Start with State Code!!!", "Next Prefeix Series Part-1 Must Start with State Code!!!"));
                this.nextPrefixPart1 = Util.getUserStateCode();
                return;
            }
        }

        if (this.nextPrefixPart2.length() < 1) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Next Prefeix Series Part-2 Length is Less than Minimum Length of 1 Character!!!", "Next Prefeix Series Part-2 Length is Less than Minimum Length of 1 Character!!!"));
            return;
        }

        boolean isNumeric = true;
        String ch = String.valueOf(this.nextPrefixPart1.charAt(2));
        for (int i = 0; i <= 9; i++) {
            if (ch.equalsIgnoreCase(String.valueOf(i))) {
                isNumeric = false;
            }
        }

        if (isNumeric) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "3rd Character of Next Prefix Series Part-1 Must be Numeric!!!", "3rd Character of Next Prefix Series Part-1 Must be Numeric!!!"));
            return;
        }

    }

    public void runningNoUpdateListener() {
        if (regnSeries_entry.getLower_range_no() != 0) {
            regnSeries_entry.setRunning_no(regnSeries_entry.getLower_range_no());
        }
    }

    public void approveNewExistingSeries() {
        String prefixPart2_selected = regnSeries_selected.getPrefix_series();
        String nextPrefixPart2_selected = regnSeries_selected.getNext_prefix_series();
        try {
            String mergePrefix = prefixPart1 + prefixPart2;
            String mergeNextPrefix = nextPrefixPart1 + nextPrefixPart2;
            if (validateForm()) {
                if (regnSeries_selected.isIsApprovebutton()) {
                    if (regnSeries_selected.getRunning_no() >= 1) {
                        if (mergePrefix.equalsIgnoreCase(prefixPart2_selected) && mergeNextPrefix.equalsIgnoreCase(nextPrefixPart2_selected)) {
                            regnSeriesImpl.updateRegnSeries(getRegnSeries_entry());
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Registration Series Sucessfully Updated."));
                        } else {
                            editRegnSeries(regnSeries_entry);
                            updateRegnSeries();
                        }
                    } else if (mergeNextPrefix != null && mergeNextPrefix.length() >= 5) {
                        regnSeriesImpl.updateSeries(getRegnSeries_entry());
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Registration Series Sucessfully Updated."));
                    }
                }
                listRegnSeries = regnSeriesImpl.getAllRegnSeries();
                listApproveSeries = regnSeriesImpl.getRecordfromVa();
                reset();
            }
            PrimeFaces.current().executeScript("PF('approveDialog').hide()");
        } catch (Exception e) {
        }
    }

    public void deleteVaRegnSeries() {
        try {
            regnSeriesImpl.deleteRecordFromVa(dobjremove);
            listApproveSeries = regnSeriesImpl.getRecordfromVa();
            reset();
            PrimeFaces.current().executeScript("PF('approveDialog').hide()");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Wrongly Entered Series Sucessfully Deleted."));
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
        }
    }

    /**
     * @return the regnSeries_entry
     */
    public Regn_series_dobj getRegnSeries_entry() {
        return regnSeries_entry;
    }

    /**
     * @param regnSeries_entry the regnSeries_entry to set
     */
    public void setRegnSeries_entry(Regn_series_dobj regnSeries_entry) {
        this.regnSeries_entry = regnSeries_entry;
    }

    /**
     * @return the regnSeries_selected
     */
    public Regn_series_dobj getRegnSeries_selected() {
        return regnSeries_selected;
    }

    /**
     * @param regnSeries_selected the regnSeries_selected to set
     */
    public void setRegnSeries_selected(Regn_series_dobj regnSeries_selected) {
        this.regnSeries_selected = regnSeries_selected;
    }

    /**
     * @return the listRegnSeries
     */
    public List<Regn_series_dobj> getListRegnSeries() {
        return listRegnSeries;
    }

    /**
     * @param listRegnSeries the listRegnSeries to set
     */
    public void setListRegnSeries(List<Regn_series_dobj> listRegnSeries) {
        this.listRegnSeries = listRegnSeries;
    }

    /**
     * @return the saveEdit
     */
    public CommandButton getSave() {
        return save;
    }

    /**
     * @param saveEdit the saveEdit to set
     */
    public void setSave(CommandButton saveEdit) {
        this.save = saveEdit;
    }

    /**
     * @return the edit
     */
    public CommandButton getEdit() {
        return edit;
    }

    /**
     * @param edit the edit to set
     */
    public void setEdit(CommandButton edit) {
        this.edit = edit;
    }

    /**
     * @return the vmAvailableRegistration
     */
    public List<VMRegnAvailableDobj> getVmAvailableRegistration() {
        return vmAvailableRegistration;
    }

    /**
     * @param vmAvailableRegistration the vmAvailableRegistration to set
     */
    public void setVmAvailableRegistration(List<VMRegnAvailableDobj> vmAvailableRegistration) {
        this.vmAvailableRegistration = vmAvailableRegistration;
    }

    /**
     * @return the prefixPart1
     */
    public String getPrefixPart1() {
        return prefixPart1;
    }

    /**
     * @param prefixPart1 the prefixPart1 to set
     */
    public void setPrefixPart1(String prefixPart1) {
        this.prefixPart1 = prefixPart1;
    }

    /**
     * @return the prefixPart2
     */
    public String getPrefixPart2() {
        return prefixPart2;
    }

    /**
     * @param prefixPart2 the prefixPart2 to set
     */
    public void setPrefixPart2(String prefixPart2) {
        this.prefixPart2 = prefixPart2;
    }

    /**
     * @return the constPreFix
     */
    public boolean isConstPreFix() {
        return constPreFix;
    }

    /**
     * @param constPreFix the constPreFix to set
     */
    public void setConstPreFix(boolean constPreFix) {
        this.constPreFix = constPreFix;
    }

    /**
     * @return the nextPrefixPart1
     */
    public String getNextPrefixPart1() {
        return nextPrefixPart1;
    }

    /**
     * @param nextPrefixPart1 the nextPrefixPart1 to set
     */
    public void setNextPrefixPart1(String nextPrefixPart1) {
        this.nextPrefixPart1 = nextPrefixPart1;
    }

    /**
     * @return the nextPrefixPart2
     */
    public String getNextPrefixPart2() {
        return nextPrefixPart2;
    }

    /**
     * @param nextPrefixPart2 the nextPrefixPart2 to set
     */
    public void setNextPrefixPart2(String nextPrefixPart2) {
        this.nextPrefixPart2 = nextPrefixPart2;
    }
//    public static void main(String[] args) {
//        System.out.println("Test :" + "12345".substring(4, 5));
//    }

    /**
     * @return the constNextPreFix
     */
    public boolean isConstNextPreFix() {
        return constNextPreFix;
    }

    /**
     * @param constNextPreFix the constNextPreFix to set
     */
    public void setConstNextPreFix(boolean constNextPreFix) {
        this.constNextPreFix = constNextPreFix;
    }

    /**
     * @return the noGenType
     */
    public String getNoGenType() {
        return noGenType;
    }

    /**
     * @param noGenType the noGenType to set
     */
    public void setNoGenType(String noGenType) {
        this.noGenType = noGenType;
    }

    public boolean isConstPreFix2() {
        return constPreFix2;
    }

    public void setConstPreFix2(boolean constPreFix2) {
        this.constPreFix2 = constPreFix2;
    }

    public boolean isConstUpperRange() {
        return constUpperRange;
    }

    public void setConstUpperRange(boolean constUpperRange) {
        this.constUpperRange = constUpperRange;
    }

    public boolean isConstRunnNum() {
        return constRunnNum;
    }

    public void setConstRunnNum(boolean constRunnNum) {
        this.constRunnNum = constRunnNum;
    }

    public boolean isConstNextPreFix2() {
        return constNextPreFix2;
    }

    public void setConstNextPreFix2(boolean constNextPreFix2) {
        this.constNextPreFix2 = constNextPreFix2;
    }

    public RegnSeriesImpl getRegnSeriesImpl() {
        return regnSeriesImpl;
    }

    public void setRegnSeriesImpl(RegnSeriesImpl regnSeriesImpl) {
        this.regnSeriesImpl = regnSeriesImpl;
    }

    public boolean isPrefixPart2Req() {
        return prefixPart2Req;
    }

    public void setPrefixPart2Req(boolean prefixPart2Req) {
        this.prefixPart2Req = prefixPart2Req;
    }

    public CommandButton getDelete() {
        return delete;
    }

    public void setDelete(CommandButton delete) {
        this.delete = delete;
    }

    public boolean isEditDisable() {
        return editDisable;
    }

    public void setEditDisable(boolean editDisable) {
        this.editDisable = editDisable;
    }

    public boolean isViewAvailRegn() {
        return viewAvailRegn;
    }

    public void setViewAvailRegn(boolean viewAvailRegn) {
        this.viewAvailRegn = viewAvailRegn;
    }

    /**
     * @return the approve
     */
    public CommandButton getApprove() {
        return approve;
    }

    /**
     * @param approve the approve to set
     */
    public void setApprove(CommandButton approve) {
        this.approve = approve;
    }

    /**
     * @return the listApproveSeries
     */
    public List<Regn_series_dobj> getListApproveSeries() {
        return listApproveSeries;
    }

    /**
     * @param listApproveSeries the listApproveSeries to set
     */
    public void setListApproveSeries(List<Regn_series_dobj> listApproveSeries) {
        this.listApproveSeries = listApproveSeries;
    }

    /**
     * @return the approveUpdate
     */
    public CommandButton getApproveUpdate() {
        return approveUpdate;
    }

    /**
     * @param approveUpdate the approveUpdate to set
     */
    public void setApproveUpdate(CommandButton approveUpdate) {
        this.approveUpdate = approveUpdate;
    }
}
