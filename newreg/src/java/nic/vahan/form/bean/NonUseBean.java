/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import static java.util.Calendar.*;
import static java.util.Calendar.DAY_OF_MONTH;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.bean.permit.PermitPanelBean;
import nic.vahan.form.dobj.NonUseDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationNonUseDobj;
import nic.vahan.form.dobj.reports.CashRecieptSubListDobj;
import nic.vahan.form.dobj.tax.TaxDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.NonUseImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.tax.TaxImpl;
import nic.vahan.server.CommonUtils;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
@ManagedBean(name = "nonUse")
@ViewScoped
public class NonUseBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(NonUseBean.class);
    private NonUseDobj dobj = new NonUseDobj();
    private NonUseDobj old_dobj = new NonUseDobj();
    private List purposeList = new ArrayList<>();
    private Date maxDate = new Date();
    private Date minDate = DateUtil.parseDate(DateUtil.getCurrentDate());
    private Date allowDate = new Date();
    private Date inspMinDate = DateUtil.parseDate(DateUtil.getCurrentDate());
    private Date withdrawlMaxDate = new Date();
    private Date inspDate = new Date();
    private boolean inspectPanel = false;
    private boolean withdrawlInfoPanel = false;
    private boolean certificationPanel = false;
    private OwnerDetailsDobj ownerDetail;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    private List inspector_List = new ArrayList();
    private List registringAuthority = new ArrayList();
    private PermitPanelBean permitPanelBean = null;
    private List<TaxFormPanelBean> listTaxForm = new ArrayList();
    NonUseImpl impl = new NonUseImpl();
    private String appl_no;
    private boolean showRestorePanel;
    private String pageHeader;
    private boolean renderRemovalOrRestore;
    private String regn_no;
    private String actionCode;
    boolean disableNonuseAdjustmentAmount;
    boolean disableNonuUseInfPanel = false;
    boolean renderInspDetailsCb;
    boolean inspDetailsCb;
    boolean disableNonuUseUpto;
    boolean disableNonuUsefrom;
    boolean showRemarksPanel;
    boolean disableGarageAddress;
    private List list_c_state;
    private List list_c_district;
    boolean disableRadioButton;
    private List<NonUseDobj> previousNonUseList = null;
    private SessionVariables sessionVariables = null;
    TmConfigurationNonUseDobj configDobj = null;
    NonUseDobj previousNonUseDobj = null;
    private Date occurrenceDate = new Date();

    public NonUseBean() {

        try {
            sessionVariables = new SessionVariables();
            configDobj = Util.getTmConfigurationNonUse();

            if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                    || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
                return;
            }

            //Allow Non_Use "from date" Date of occurrence of incidence
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -7);
            occurrenceDate = cal.getTime();

            //Allow Non_Use "from date" select from starting of LOCKDOWN Period - 2020-03-22
            cal.set(Calendar.YEAR, 2020);
            cal.set(Calendar.MONTH, 2);
            cal.set(Calendar.DAY_OF_MONTH, 22);
            allowDate = cal.getTime();

            minDate = allowDate;

            Date nextMonthDateFromUptoDate = null;
            list_c_district = new ArrayList();
            list_c_state = new ArrayList();
            list_c_state = MasterTableFiller.getStateList();
            list_c_district = MasterTableFiller.getDistrictList(sessionVariables.getStateCodeSelected());
            purposeList.add("Not Intending to Use or Keep for Use of Motor Vehicle");
            if ("MH".equalsIgnoreCase(sessionVariables.getStateCodeSelected())) {
                purposeList.add("Motor Vehicle is rendered incapable of being used or Kept for Use (In case of occurrence of Accident)");
            } else {
                purposeList.add("Motor Vehicle is rendered incapable of being used or Kept for Use (Seven Days from the Date of occurrence of incidence)");
            }
            purposeList.add("Motor Vehicle not used in any public place");
            purposeList.add("Deemed Non-Use");

            int pur_cd = appl_details.getPur_cd();
            actionCode = String.valueOf(appl_details.getCurrent_action_cd());
            if ("KL".equalsIgnoreCase(sessionVariables.getStateCodeSelected())) {
                allowDate = JSFUtils.getStringToDateddMMMyyyy(appl_details.getAppl_dt());
            }
            if ("KL".equalsIgnoreCase(sessionVariables.getStateCodeSelected())) {
                minDate = JSFUtils.getStringToDateddMMMyyyy(appl_details.getAppl_dt());
            }
            regn_no = appl_details.getRegn_no();
            appl_no = appl_details.getAppl_no();
            if (pur_cd == TableConstants.VM_MAST_NON_USE) {

                previousNonUseDobj = impl.getPreviousDetails(regn_no);
                if (previousNonUseDobj != null) {
                    previousNonUseList = new ArrayList<>();
                    previousNonUseList.add(previousNonUseDobj);
                    Calendar upto_Date = Calendar.getInstance();
                    upto_Date.setTime(previousNonUseDobj.getExemp_to());
                    Calendar next_monyh = getInstance();
                    next_monyh.clear();
                    next_monyh.set(YEAR, upto_Date.get(YEAR));
                    next_monyh.set(MONTH, upto_Date.get(MONTH) + 1);
                    next_monyh.set(DAY_OF_MONTH, 1);
                    nextMonthDateFromUptoDate = next_monyh.getTime();
                }
                pageHeader = "Non Use Intimation Details";
                OwnerImpl owner_Impl = new OwnerImpl();
                List<OwnerDetailsDobj> ownerDetailsDobjList = owner_Impl.getOwnerDetailsList(regn_no.toUpperCase().trim(), null);
                ownerDetailsDobjList = owner_Impl.getStateWiseOwnerList(ownerDetailsDobjList, sessionVariables.getStateCodeSelected());
                ownerDetail = ownerDetailsDobjList.get(0);
                setInspector_List(impl.getInspectionOfficer());
                dobj = impl.getNonUseDetails(appl_no, regn_no, actionCode, pur_cd);
                if (dobj == null) {
                    dobj = new NonUseDobj();
                    if (nextMonthDateFromUptoDate != null) {
                        dobj.setExemp_from(nextMonthDateFromUptoDate);
                        if (configDobj != null && configDobj.isDisable_nonuse_fromdate_in_nonuse_continue()) {
                            setDisableNonuUsefrom(true);
                        }
                    }
                } else {
                    old_dobj = (NonUseDobj) dobj.clone();
                    if (dobj.getGarage_state() != null && !"".equalsIgnoreCase(dobj.getGarage_state())) {
                        vehC_StateListener();
                    }
                }
                inspMinDate = dobj.getExemp_from();
                if (dobj.getExemp_to() != null) {
                    if (dobj.getExemp_to().after(new Date())) {
                        inspDate = new Date();
                    } else {
                        inspDate = dobj.getExemp_to();
                    }
                } else {
                    inspDate = new Date();
                }
                setRenderRemovalOrRestore(false);
                if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_NON_USE_INTIMATION_VERIFY
                        || Integer.parseInt(actionCode) == TableConstants.VM_ROLE_NON_USE_INTIMATION_APPROVE) {
                    if (configDobj != null && configDobj.isVehicle_inspect_mandatory()) {
                        setInspectPanel(true);
                        setRenderInspDetailsCb(false);
                    } else {
                        setRenderInspDetailsCb(true);
                        setInspectPanel(false);
                    }

                    if (configDobj != null && configDobj.isDocs_surrender()) {
                        if (TableConstants.VT_RC_SURRENDER_STATUS.equalsIgnoreCase(ownerDetail.getStatus())) {
                            dobj.setDoc_flag("Y");
                            dobj.setDoc_Details("Documents : Document Is Surrender");
                        } else {
                            dobj.setDoc_flag("N");
                            dobj.setDoc_Details("Documents : Document Is Not Surrender");
                        }
                    }

                    if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_NON_USE_INTIMATION_APPROVE) {
                        setRenderInspDetailsCb(false);
                        disableNonuUseInfPanel = true;
                        setDisableNonuUseUpto(true);
                        setDisableNonuUsefrom(true);
                        long tax_amt = 0L;
                        if (configDobj != null && configDobj.isNonuse_adjust_in_tax_amt()) {
                            setCertificationPanel(true);
                        }
                        TaxImpl taxImpl = new TaxImpl();
                        int nonUseNoOfMonth = DateUtils.getDate1MinusDate2_Months(dobj.getExemp_from(), dobj.getExemp_to());
                        OwnerImpl ownerImpl = new OwnerImpl();
                        Owner_dobj ownerDobj = ownerImpl.getOwnerDobj(ownerDetail);
                        TaxDobj taxDobj = taxImpl.getTaxDetails(ownerDobj.getRegn_no().trim(), String.valueOf(TableConstants.TM_ROAD_TAX), ownerDobj.getState_cd());
                        CashRecieptSubListDobj cashRecieptSubListDobj = impl.gettaxBreakupDetailsForNonUse(taxDobj);
                        if (cashRecieptSubListDobj != null) {
                            tax_amt = Long.parseLong(cashRecieptSubListDobj.getAmount());
                        } else if (taxDobj != null) {
                            tax_amt = taxDobj.getTax_amt();
                        } else {
                            throw new VahanException("Tax Details Not Found !!!");
                        }
                        String isGoodsVehicle = impl.getTransportCategory(ownerDobj.getVh_class());
                        int taxMonths = DateUtils.getDate1MinusDate2_Months(dobj.getExemp_from(), dobj.getExemp_to());
                        if (taxMonths <= 12) {
                            disableNonuseAdjustmentAmount = true;
                            NonUseDobj tempNonUseDobj = impl.getAdjustmentAmount(taxDobj, isGoodsVehicle, nonUseNoOfMonth, dobj.getInsFlag(), tax_amt);
                            if (tempNonUseDobj != null) {
                                if ((long) tempNonUseDobj.getAdjustmentAmount() != 0) {
                                    dobj.setAdjustmentAmount(tempNonUseDobj.getAdjustmentAmount());
                                    dobj.setNonUsePenalty(tempNonUseDobj.getNonUsePenalty());
                                } else {
                                    disableNonuseAdjustmentAmount = false;
                                }
                            } else {
                                JSFUtils.setFacesMessage("Error in Calculating Nonuse Tax Amount/Penalty", "Error in Calculating Nonuse Tax Amount/Penalty", JSFUtils.ERROR);
                                return;
                            }

                        } else {
                            disableNonuseAdjustmentAmount = false;
                        }
                    }
                    if (!"".equalsIgnoreCase(dobj.getInspectedBy())) {
                        inspDetailsCb = true;
                        inspectPanel = true;
                    }
                }

            } else if (pur_cd == TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE) {

                disableNonuUseInfPanel = true;
                setDisableNonuUseUpto(true);
                setDisableNonuUsefrom(true);
                pageHeader = "Non Use Restore / Remove";
                setRenderRemovalOrRestore(true);
                setCertificationPanel(false);
                setInspectPanel(false);
                String[][] data = MasterTableFiller.masterTables.TM_OFFICE.getData();
                registringAuthority.add(new SelectItem("-1", "---Select---"));
                for (int i = 0; i < data.length; i++) {
                    if (sessionVariables.getStateCodeSelected().equalsIgnoreCase(data[i][13])) {
                        registringAuthority.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
                validateApplication();
                if (configDobj != null && configDobj.isDeclare_withdrawl_date()) {
                    setWithdrawlInfoPanel(true);
                }
            }
            prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(appl_no, TableConstants.VM_MAST_NON_USE);
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void vehC_StateListener() {

        String sel_c_state = dobj.getGarage_state();
        String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
        list_c_district.clear();
        for (int i = 0; i < data.length; i++) {
            if (data[i][2].equals(sel_c_state)) {
                list_c_district.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
    }

    public boolean isLastDayofMonth() throws ParseException {
        boolean flag = true;
        try {
            boolean isLeapYear = ((getYear(dobj.getVehicle_use_frm()) % 4 == 0) && (getYear(dobj.getVehicle_use_frm()) % 100 != 0) || (getYear(dobj.getVehicle_use_frm()) % 400 == 0));
            if (getMonth(dobj.getVehicle_use_frm()) == 2) {
                if (getDay(dobj.getVehicle_use_frm()) != 28 && isLeapYear == false) {
                    flag = false;
                }
                if (getDay(dobj.getVehicle_use_frm()) != 29 && isLeapYear == true) {
                    flag = false;
                }
            }
            if ((getMonth(dobj.getVehicle_use_frm()) == 1 || getMonth(dobj.getVehicle_use_frm()) == 3 || getMonth(dobj.getVehicle_use_frm()) == 5 || getMonth(dobj.getVehicle_use_frm()) == 7 || getMonth(dobj.getVehicle_use_frm()) == 8 || getMonth(dobj.getVehicle_use_frm()) == 10 || getMonth(dobj.getVehicle_use_frm()) == 12) && getDay(dobj.getVehicle_use_frm()) != 31) {
                flag = false;
            }
            if ((getMonth(dobj.getVehicle_use_frm()) == 4 || getMonth(dobj.getVehicle_use_frm()) == 6 || getMonth(dobj.getVehicle_use_frm()) == 9 || getMonth(dobj.getVehicle_use_frm()) == 11) && getDay(dobj.getVehicle_use_frm()) != 30) {
                flag = false;
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return flag;
    }

    public boolean validdate() throws ParseException {

        boolean flag = true;
        try {
            if (appl_details.getPur_cd() == TableConstants.VM_MAST_NON_USE) {
                String diff_date;
                if (getMonth(dobj.getExemp_from()) > 3) {
                    diff_date = "31-" + "Mar-" + getYear(dobj.getExemp_from()) + 1;
                } else {
                    diff_date = "31-" + "Mar-" + getYear(dobj.getExemp_from());
                }

                if (appl_details.getCurrent_action_cd() == TableConstants.VM_ROLE_NON_USE_INTIMATION_ENTRY) {

                    if (dobj.getNon_use_purpose().equals("0")) {
                        JSFUtils.showMessage("Select Non Use Purpose.");
                        flag = false;
                    }
                    if (!dobj.getNon_use_purpose().equals("Deemed Non-Use")) {

                        //non-use data entry allow upto date 5th in next month if application of non-use inwarded in previous month.
                        if (dobj.getExemp_from().before(new Date())) {
                            if ((new SimpleDateFormat("dd-MMM-yyyy").parse(appl_details.getAppl_dt())).before(dobj.getExemp_from()) && (getMonth(new SimpleDateFormat("dd-MMM-yyyy").parse(appl_details.getAppl_dt())) != getMonth(dobj.getExemp_from()))) {
                                flag = true;
                            } else if (dobj.getNon_use_purpose().equals("Motor Vehicle is rendered incapable of being used or Kept for Use (Seven Days from the Date of occurrence of incidence)")
                                    && dobj.getExemp_from().before(occurrenceDate)) {
                                JSFUtils.showMessage("Exemption date should not be less than occurrence of incidence date.");
                                flag = false;
                            } else {
                                JSFUtils.showMessage("Exemption date should not be less than today date.");
                                flag = false;
                            }
                        }
                    }
                }

                if (getDay(dobj.getExemp_from()) > 1 && configDobj != null && configDobj.isExemfrom_first_dateofmonth()) {
                    JSFUtils.showMessage("Exemption is allowed only from the first day of month.");
                    flag = false;
                }
                if (dobj.getExemp_from().after(dobj.getExemp_to())) {
                    JSFUtils.showMessage("Exemption To date should be greater than exemption from date.");
                    flag = false;
                }
                boolean isLeapYear = ((getYear(dobj.getExemp_to()) % 4 == 0) && (getYear(dobj.getExemp_to()) % 100 != 0) || (getYear(dobj.getExemp_to()) % 400 == 0));
                if (getMonth(dobj.getExemp_to()) == 2) {
                    if (getDay(dobj.getExemp_to()) != 28 && isLeapYear == false && configDobj != null && configDobj.isExemto_last_dateofmonth()) {
                        JSFUtils.showMessage("Exemption is allowed only upto end of month.");
                        flag = false;
                    }
                    if (getDay(dobj.getExemp_to()) != 29 && isLeapYear == true && configDobj != null && configDobj.isExemto_last_dateofmonth()) {
                        JSFUtils.showMessage("Exemption is allowed only upto end of month.");
                        flag = false;
                    }
                }
                if ((getMonth(dobj.getExemp_to()) == 1 || getMonth(dobj.getExemp_to()) == 3 || getMonth(dobj.getExemp_to()) == 5 || getMonth(dobj.getExemp_to()) == 7 || getMonth(dobj.getExemp_to()) == 8 || getMonth(dobj.getExemp_to()) == 10 || getMonth(dobj.getExemp_to()) == 12) && getDay(dobj.getExemp_to()) != 31 && configDobj != null && configDobj.isExemto_last_dateofmonth()) {
                    JSFUtils.showMessage("Exemption is allowed only upto end of month.");
                    flag = false;
                }
                if ((getMonth(dobj.getExemp_to()) == 4 || getMonth(dobj.getExemp_to()) == 6 || getMonth(dobj.getExemp_to()) == 9 || getMonth(dobj.getExemp_to()) == 11) && getDay(dobj.getExemp_to()) != 30 && configDobj != null && configDobj.isExemto_last_dateofmonth()) {
                    JSFUtils.showMessage("Exemption is allowed only upto end of month.");
                    flag = false;
                }
                DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                Date dd = dateFormat.parse(diff_date);
                if (dobj.getExemp_to().after(dd) && configDobj != null && configDobj.isExemupto_financial_year()) {
                    JSFUtils.showMessage("Exemption is allowed upto end of financial year.");
                    flag = false;
                }

                if (previousNonUseDobj == null && configDobj != null && !configDobj.isNonuse_adjust_in_tax_amt()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dobj.getExemp_from());
                    calendar.add(Calendar.MONTH, 0);
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    calendar.add(Calendar.DATE, -1);
                    Date lastDayOfExemptFrom = calendar.getTime();
                    boolean isTaxPaidOrClearNonUesFrmDt = impl.isTaxPaidOrClearedNonUse(regn_no.toUpperCase().trim(), lastDayOfExemptFrom);
                    if (!isTaxPaidOrClearNonUesFrmDt) {
                        JSFUtils.showMessage("Tax is not upto date till the Exemption From Date");
                        flag = false;
                    }
                }

                Date taxUpto = impl.getTaxUpto(regn_no.toUpperCase().trim());

                String currentStateCd = Util.getUserStateCode();
                Owner_dobj ownerDobj;
                OwnerImpl ownerImpl = new OwnerImpl();
                NonUseImpl nonUseImpl = new NonUseImpl();
                String regnNo = appl_details.getRegn_no();

                int currentOffCd = 0;
                if (Util.getSelectedSeat() != null) {
                    currentOffCd = Util.getSelectedSeat().getOff_cd();
                }
                ownerDobj = ownerImpl.getVtOwnerDetailsForRegnNoStateCdOffCd(regnNo, currentStateCd, currentOffCd);
                if ("CG".equals(currentStateCd) && (ownerDobj != null) && !nonUseImpl.checkMFormStatus(regn_no) && previousNonUseDobj == null) {
                    if ((ownerDobj.getVh_class() != TableConstants.GOODS_CARRIER_VCH_CLASS) && (dobj.getExemp_to().after(taxUpto))) {
                        dobj.setExemp_to(null);
                        JSFUtils.showMessage(" Please Pay Tax For 1 Month");
                    } else if ((ownerDobj.getVh_class() == TableConstants.GOODS_CARRIER_VCH_CLASS) && (dobj.getExemp_to().after(taxUpto))) {
                        JSFUtils.showMessage(" Please Pay Your Quarterly Tax First ");
                    }
                }

                if (configDobj != null && configDobj.isRequire_advance_tax()) {
                    if (taxUpto != null) {
                        if (dobj.getExemp_to().after(taxUpto)) {
                            JSFUtils.showMessage("Tax Paid upto -" + taxUpto + " Please Pay Advance Tax");
                            flag = false;
                        }
                    } else {
                        JSFUtils.showMessage("Error : Can't Find Tax Details.");
                        flag = false;
                    }
                }

                int nonUseNoOfMonth = DateUtils.getDate1MinusDate2_Months(dobj.getExemp_from(), dobj.getExemp_to());
                if (nonUseNoOfMonth > 6 && "OR".equalsIgnoreCase(sessionVariables.getStateCodeSelected())) {
                    JSFUtils.showMessage("Vehicle NonUse Period  Maximum 6 Months ");
                    flag = false;
                }

                if ("AS".equalsIgnoreCase(sessionVariables.getStateCodeSelected())) {
                    if (nonUseNoOfMonth > 12) {
                        JSFUtils.showMessage("Vehicle NonUse Period  Maximum 1 Year ");
                        flag = false;
                    }
                    if (nonUseNoOfMonth < 3) {
                        JSFUtils.showMessage("Vehicle NonUse Period  Minimum 1 Quarter ");
                        flag = false;
                    }
                }
                if (inspectPanel) {
                    if (dobj.getInspectionDate().before(dobj.getExemp_from())) {
                        JSFUtils.showMessage("Inspection date should not be less then Exemption From date.");
                        flag = false;
                    }
                    if (dobj.getInspectedBy().equals("0")) {
                        JSFUtils.showMessage("Select Inspection Officer Name.");
                        flag = false;
                    }
                }

                if (actionCode.equalsIgnoreCase(String.valueOf(TableConstants.VM_ROLE_NON_USE_INTIMATION_APPROVE))
                        && configDobj != null && configDobj.isNonuse_adjust_in_tax_amt()) {
                    if (dobj.getAdjustmentAmount() == 0 && "Y".equalsIgnoreCase(dobj.getInsFlag())) {
                        JSFUtils.showMessage("Non Use Adjustment Amount Can't be Zero");
                        flag = false;
                    }
                    if (dobj.getNonUsePenalty() == 0 && "N".equalsIgnoreCase(dobj.getInsFlag())) {
                        JSFUtils.showMessage("Non Use Penalty Can't be Zero");
                        flag = false;
                    }
                }
            } else {
                if (configDobj != null && configDobj.isVehicle_inspect_for_removalshift() && "N".equalsIgnoreCase(dobj.getInsFlag())) {
                    JSFUtils.showMessage("In NonUse Restore/Remove Vehicle Should be Found at Specified Location ");
                    flag = false;
                }
            }

        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return flag;

    }

    public int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    public int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        return year;
    }

    public int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONDAY) + 1;
        return month;
    }

    public void reset() {
        dobj.setAuthorised_by("");
        dobj.setExemp_from(null);
        dobj.setLocation_of_garage("");
        dobj.setVehicle_use_frm(null);
        dobj.setNon_use_purpose("");
        dobj.setPermission_dt(null);
        dobj.setPermission_no("");
        dobj.setRegn_no("");
        dobj.setExemp_to(null);

    }

    public void listnerInspectDetails() {
        inspectPanel = false;
        if (inspDetailsCb) {
            inspectPanel = true;
        }
    }

    public void selectListener() {

        if (dobj.getValueOfRadioEvent().equalsIgnoreCase("R")) {
            setShowRestorePanel(false);
            setInspectPanel(false);
            setDisableNonuUseUpto(true);
            setDisableNonuUsefrom(true);
            setShowRemarksPanel(false);
            setDisableGarageAddress(true);

        } else if (dobj.getValueOfRadioEvent().equalsIgnoreCase("C")) {
            setDisableNonuUseUpto(true);
            setDisableNonuUsefrom(true);
            setShowRemarksPanel(false);
            setDisableGarageAddress(false);

            if (configDobj != null && configDobj.isVehicle_inspect_for_removalshift()) {
                if (appl_details.getCurrent_action_cd() == TableConstants.VM_ROLE_NON_USE_RESTORE_REMOVE_ENTRY) {
                    setInspectPanel(false);
                } else {
                    setInspectPanel(true);
                }
                setShowRestorePanel(true);

            } else {
                setInspectPanel(false);
                setShowRestorePanel(true);
            }

        } else if (dobj.getValueOfRadioEvent().equalsIgnoreCase("NC")) {
            setShowRestorePanel(false);
            setInspectPanel(false);
            setDisableNonuUseUpto(false);
            setDisableNonuUsefrom(false);
            setShowRemarksPanel(true);
            setDisableGarageAddress(true);
        }
    }

    public void validateApplication() {
        try {
            dobj = impl.getNonUseDetails(appl_no, regn_no, actionCode, appl_details.getPur_cd());
            if (dobj != null) {
                if (configDobj != null && configDobj.isDeclare_withdrawl_date()) {
                    dobj.setValueOfRadioEvent("R");
                    if (dobj.getVehicle_use_frm() == null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(dobj.getExemp_to());
                        calendar.add(Calendar.DATE, 1);
                        Date nextDay = calendar.getTime();
                        dobj.setVehicle_use_frm(nextDay);
                        withdrawlMaxDate = nextDay;
                    }

                    if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_NON_USE_RESTORE_REMOVE_VERIFICATION
                            || Integer.parseInt(actionCode) == TableConstants.VM_ROLE_NON_USE_RESTORE_REMOVE_APPROVAL) {

                        if (getDay(dobj.getExemp_from()) > 1) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(dobj.getExemp_from());
                            calendar.add(Calendar.MONTH, 1);
                            calendar.set(Calendar.DAY_OF_MONTH, 1);
                            calendar.add(Calendar.DATE, 0);
                            Date firsttDayOfNextMonthExempFrm = calendar.getTime();
                            dobj.setExemFrmDate(firsttDayOfNextMonthExempFrm);
                        } else {
                            dobj.setExemFrmDate(dobj.getExemp_from());
                        }

                        if (dobj.getExemp_to().after(dobj.getVehicle_use_frm()) || !isLastDayofMonth()) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(dobj.getVehicle_use_frm());
                            calendar.add(Calendar.MONTH, 0);
                            calendar.set(Calendar.DAY_OF_MONTH, 1);
                            calendar.add(Calendar.DATE, -1);
                            Date lastDayOfPreviousMonthExempTo = calendar.getTime();
                            dobj.setExemUptoDate(lastDayOfPreviousMonthExempTo);
                        } else {
                            dobj.setExemUptoDate(dobj.getExemp_to());
                        }
                        String pattern = "dd-MMM-yyyy";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                        if (dobj.getExemUptoDate().after(dobj.getExemFrmDate())) {
                            dobj.setNonUseDeclareDtls("Tax Exemption will be applicable for the duration From: " + simpleDateFormat.format(dobj.getExemFrmDate()) + "   To: " + simpleDateFormat.format(dobj.getExemUptoDate()) + " only.");
                        } else {
                            dobj.setNonUseDeclareDtls("No Tax Exemption will be applicable.");
                        }
                    }
                }
                if (!"".equalsIgnoreCase(dobj.getValueOfRadioEvent()) && dobj.getValueOfRadioEvent() != null) {
                    disableRadioButton = true;
                } else {
                    disableRadioButton = false;
                }
                if (dobj.getGarage_state() != null && !"".equalsIgnoreCase(dobj.getGarage_state())) {
                    vehC_StateListener();
                }
                OwnerImpl owner_Impl = new OwnerImpl();
                List<OwnerDetailsDobj> ownerDetailsDobjList = owner_Impl.getOwnerDetailsList(dobj.getRegn_no().toUpperCase().trim(), null);
                ownerDetailsDobjList = owner_Impl.getStateWiseOwnerList(ownerDetailsDobjList, sessionVariables.getStateCodeSelected());
                ownerDetail = ownerDetailsDobjList.get(0);
                setInspector_List(impl.getInspectionOfficer());
                inspMinDate = dobj.getExemp_from();
                if (dobj.getExemp_to() != null) {
                    if (dobj.getExemp_to().after(new Date())) {
                        inspDate = new Date();
                    } else {
                        inspDate = dobj.getExemp_to();
                    }
                } else {
                    inspDate = new Date();
                }
                setInspectPanel(false);
                setCertificationPanel(false);
                if ("C".equalsIgnoreCase(dobj.getValueOfRadioEvent())) {
                    if (configDobj != null && configDobj.isVehicle_inspect_for_removalshift()) {
                        if (appl_details.getCurrent_action_cd() == TableConstants.VM_ROLE_NON_USE_RESTORE_REMOVE_ENTRY) {
                            setInspectPanel(false);
                        } else {
                            setInspectPanel(true);
                        }
                        setShowRestorePanel(true);
                    } else {
                        setInspectPanel(false);
                        setShowRestorePanel(true);
                    }
                    setDisableGarageAddress(false);
                } else if ("NC".equalsIgnoreCase(dobj.getValueOfRadioEvent())) {
                    //dobj.setValueOfRadioEvent("NC");
                    setShowRestorePanel(false);
                    setInspectPanel(false);
                    setDisableNonuUseUpto(false);
                    setDisableNonuUsefrom(false);
                    setShowRemarksPanel(true);
                    setDisableGarageAddress(true);
                } else if ("R".equalsIgnoreCase(dobj.getValueOfRadioEvent())) {
                    setShowRestorePanel(false);
                    if (configDobj != null && configDobj.isVehicle_inspect_mandatory()) {
                        if (appl_details.getCurrent_action_cd() == TableConstants.VM_ROLE_NON_USE_RESTORE_REMOVE_ENTRY) {
                            setInspectPanel(false);
                        } else if (appl_details.getCurrent_action_cd() == TableConstants.VM_ROLE_NON_USE_RESTORE_REMOVE_VERIFICATION) {
                            setInspectPanel(true);
                            dobj.setInsFlag(null);
                            dobj.setInspectedBy(null);
                            dobj.setInspectionDate(null);
                            dobj.setInspectionReportNo(null);
                        } else {
                            setInspectPanel(true);
                        }
                    }
                    if (appl_details.getCurrent_action_cd() == TableConstants.VM_ROLE_NON_USE_RESTORE_REMOVE_VERIFICATION) {
                        setRenderInspDetailsCb(true);
                        dobj.setInsFlag(null);
                        dobj.setInspectedBy(null);
                        dobj.setInspectionDate(null);
                        dobj.setInspectionReportNo(null);
                    }
                    setDisableGarageAddress(true);
                }
            } else {
                JSFUtils.setFacesMessage("Application Details Not Found", "Application Details Not Found", JSFUtils.ERROR);
                return;
            }

        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void checkNonUseMonth() {
        try {
            String currentStateCd = Util.getUserStateCode();
            String regnNo = appl_details.getRegn_no();
            Owner_dobj ownerDobj;
            int currentOffCd = 0;
            if (Util.getSelectedSeat() != null) {
                currentOffCd = Util.getSelectedSeat().getOff_cd();
            }

            OwnerImpl ownerImpl = new OwnerImpl();
            ownerDobj = ownerImpl.getVtOwnerDetailsForRegnNoStateCdOffCd(regnNo, currentStateCd, currentOffCd);

            if ("CG".equals(currentStateCd) && (ownerDobj != null)) {
                if (ownerDobj.getVh_class() != TableConstants.GOODS_CARRIER_VCH_CLASS && DateUtils.getDate1MinusDate2_Months(dobj.getExemp_from(), dobj.getExemp_to()) > 1) {
                    dobj.setExemp_to(null);
                    JSFUtils.showMessage("Vehicle NonUse Period  Maximum 1 Months ");
                } else if (ownerDobj.getVh_class() == TableConstants.GOODS_CARRIER_VCH_CLASS && DateUtils.getDate1MinusDate2_Months(dobj.getExemp_from(), dobj.getExemp_to()) > 3) {
                    JSFUtils.showMessage("Vehicle NonUse Period  Maximum 3 Months ");
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public NonUseDobj getDobj() {
        return dobj;
    }

    public void setDobj(NonUseDobj dobj) {
        this.dobj = dobj;
    }

    public List getPurposeList() {
        return purposeList;
    }

    public void setPurposeList(List purposeList) {
        this.purposeList = purposeList;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    @Override
    public String save() {

        String returnLocation = "";
        try {

            boolean valid = validdate();

            Status_dobj statusDobj = new Status_dobj();
            statusDobj.setAction_cd(appl_details.getCurrent_action_cd());
            statusDobj.setAppl_dt(appl_details.getAppl_dt());
            statusDobj.setAppl_no(appl_details.getAppl_no());
            statusDobj.setRegn_no(appl_details.getRegn_no());
            statusDobj.setPur_cd(appl_details.getPur_cd());
            statusDobj.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            statusDobj.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            statusDobj.setStatus(getApp_disapp_dobj().getNew_status());
            statusDobj.setCurrent_role(appl_details.getCurrent_role());
            dobj.setRegn_no(appl_details.getRegn_no());
            dobj.setPur_cd(appl_details.getPur_cd());
            if (statusDobj.getPur_cd() == TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE) {
                Date today = new Date();
                int result = DateUtils.compareDates(dobj.getExemp_from(), today);
                if (result == 2) {
                    dobj.setAdjustmentAmount(0);
                    dobj.setNonUsePenalty(0);
                } else if (result == 0) {
                    dobj.setAdjustmentAmount(0);
                    dobj.setNonUsePenalty(0);
                } else if (result == 1) {
                    today = new java.sql.Date(today.getTime());
                    if (!dobj.getValueOfRadioEvent().equalsIgnoreCase("C")) {
                        if (dobj.getExemp_to().after(new Date())) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(today);
                            calendar.add(Calendar.MONTH, 0);
                            calendar.set(Calendar.DAY_OF_MONTH, 1);
                            calendar.add(Calendar.DATE, -1);
                            Date lastDayOfPreviousMonthExempTo = calendar.getTime();
                            if (lastDayOfPreviousMonthExempTo.before(dobj.getExemp_from())) {
                                dobj.setExemp_to(new Date());
                            } else {
                                dobj.setExemp_to(lastDayOfPreviousMonthExempTo);
                            }
                        }
                    }
                }
                if (dobj.getValueOfRadioEvent().equalsIgnoreCase("R") || dobj.getValueOfRadioEvent().equalsIgnoreCase("NC")) {
                    if (configDobj != null && configDobj.isNonuse_adjust_in_tax_amt()) {
                        updateNonUseAmount();
                    }
                }
            }
            if (valid) {
                impl.saveNonUse(dobj, statusDobj, ComparisonBeanImpl.changedDataContents(compareChanges()), configDobj);
                returnLocation = "seatwork";
            }

        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
        return returnLocation;
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        compBeanList.clear();
        Compare("Non Use Purpose", dobj.getNon_use_purpose(), old_dobj.getNon_use_purpose(), compBeanList);
        Compare("Non Use From Date", DateUtils.parseDate(dobj.getExemp_from()), DateUtils.parseDate(old_dobj.getExemp_from()), compBeanList);
        Compare("Non Use To Date", DateUtils.parseDate(dobj.getExemp_to()), DateUtils.parseDate(old_dobj.getExemp_to()), compBeanList);
        Compare("Authorised By", dobj.getAuthorised_by(), old_dobj.getAuthorised_by(), compBeanList);
        Compare("Permission Date", DateUtils.parseDate(dobj.getPermission_dt()), DateUtils.parseDate(old_dobj.getPermission_dt()), compBeanList);
        Compare("Permission No", dobj.getPermission_no(), old_dobj.getPermission_no(), compBeanList);
        Compare("Non Use Date", DateUtils.parseDate(dobj.getVehicle_use_frm()), DateUtils.parseDate(old_dobj.getVehicle_use_frm()), compBeanList);
        Compare("Location Of Garage", dobj.getAuthorised_by(), old_dobj.getAuthorised_by(), compBeanList);
        Compare("Inspected By", dobj.getInspectedBy(), old_dobj.getInspectedBy(), compBeanList);
        Compare("Inspection Date", DateUtils.parseDate(dobj.getInspectionDate()), DateUtils.parseDate(old_dobj.getInspectionDate()), compBeanList);
        Compare("Inspection No", dobj.getInspectionReportNo(), old_dobj.getInspectionReportNo(), compBeanList);
        Compare("Vehicle Found", dobj.getInsFlag(), old_dobj.getInsFlag(), compBeanList);
        Compare("Certificate Permission Date", DateUtils.parseDate(dobj.getCertiPermissionDt()), DateUtils.parseDate(old_dobj.getCertiPermissionDt()), compBeanList);
        Compare("Certificate Date", DateUtils.parseDate(dobj.getCertificationDt()), DateUtils.parseDate(old_dobj.getCertificationDt()), compBeanList);
        Compare("Certificated By", dobj.getCertifiedBy(), old_dobj.getCertifiedBy(), compBeanList);
        Compare("Adjustment Amount", dobj.getAdjustmentAmount(), old_dobj.getAdjustmentAmount(), compBeanList);
        Compare("Garage Address 1", dobj.getGarage_add1(), old_dobj.getGarage_add1(), compBeanList);
        Compare("Garage Address 2", dobj.getGarage_add2(), old_dobj.getGarage_add2(), compBeanList);
        Compare("Garage Address 3", dobj.getGarage_add3(), old_dobj.getGarage_add3(), compBeanList);
        Compare("Garage State", dobj.getGarage_state(), old_dobj.getGarage_state(), compBeanList);
        Compare("Garage District", dobj.getGarage_district(), old_dobj.getGarage_district(), compBeanList);
        Compare("Garage PinCode", dobj.getGarage_pincode(), old_dobj.getGarage_pincode(), compBeanList);

        return compBeanList;
    }

    @Override
    public String saveAndMoveFile() {
        String returnLocation = "";
        try {
            boolean valid = validdate();
            Status_dobj status = new Status_dobj();
            status.setAction_cd(appl_details.getCurrent_action_cd());
            status.setRegn_no(appl_details.getRegn_no());
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            if (status.getPur_cd() == TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE) {
                Date today = new Date();
                int result = DateUtils.compareDates(dobj.getExemp_from(), today);
                if (result == 2) {
                    dobj.setAdjustmentAmount(0);
                    dobj.setNonUsePenalty(0);
                } else if (result == 0) {
                    dobj.setAdjustmentAmount(0);
                    dobj.setNonUsePenalty(0);
                } else if (result == 1) {
                    today = new java.sql.Date(today.getTime());
                    if (!dobj.getValueOfRadioEvent().equalsIgnoreCase("C")) {
                        if (dobj.getExemp_to().after(new Date())) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(today);
                            calendar.add(Calendar.MONTH, 0);
                            calendar.set(Calendar.DAY_OF_MONTH, 1);
                            calendar.add(Calendar.DATE, -1);
                            Date lastDayOfPreviousMonthExempTo = calendar.getTime();
                            if (lastDayOfPreviousMonthExempTo.before(dobj.getExemp_from())) {
                                dobj.setExemp_to(new Date());
                            } else {
                                dobj.setExemp_to(lastDayOfPreviousMonthExempTo);
                            }
                        }
                    }
                }
                if (dobj.getValueOfRadioEvent().equalsIgnoreCase("R") || dobj.getValueOfRadioEvent().equalsIgnoreCase("NC")) {
                    if (configDobj != null && configDobj.isNonuse_adjust_in_tax_amt()) {
                        updateNonUseAmount();
                    } else {
                        if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_NON_USE_RESTORE_REMOVE_APPROVAL) {
                            if (dobj.getExemFrmDate() != null) {
                                dobj.setExemp_from(dobj.getExemFrmDate());
                            }
                            if (dobj.getExemUptoDate() != null) {
                                dobj.setExemp_to(dobj.getExemUptoDate());
                            }
                        }
                    }
                }
            }

            if (valid) {
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                        || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    impl.isSaveAndMove(dobj, status, ComparisonBeanImpl.changedDataContents(compareChanges()), configDobj);
                    if (actionCode.equals(TableConstants.VM_ROLE_NON_USE_INTIMATION_APPROVE + "") || actionCode.equals(TableConstants.VM_ROLE_NON_USE_RESTORE_REMOVE_APPROVAL + "")) {
                        returnLocation = frcPrint();
                    } else {
                        returnLocation = "seatwork";
                    }
                }
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                    status.setPrev_action_cd_selected(app_disapp_dobj.getPre_action_code());
                    impl.reback(dobj, status, ComparisonBeanImpl.changedDataContents(compareChanges()), configDobj);
                    returnLocation = "seatwork";
                }
            }
        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
        return returnLocation;
    }

    private String frcPrint() {
        if (appl_details != null) {
            HttpSession ses = Util.getSession();
            ses.setAttribute("applNoNUB", appl_details.getAppl_no());
            ses.setAttribute("regnNoNUB", appl_details.getRegn_no());
            ses.setAttribute("actionCdNUB", appl_details.getPur_cd() + "");
            if (actionCode.equals(TableConstants.VM_ROLE_NON_USE_INTIMATION_APPROVE + "")) {
                return "/ui/reports/nonuseIntimationReport.xhtml?faces-redirect=true";
            } else if (actionCode.equals(TableConstants.VM_ROLE_NON_USE_RESTORE_REMOVE_APPROVAL + "")) {
                if (dobj.getValueOfRadioEvent().equalsIgnoreCase("C")) {
                    return "/ui/reports/nonUseShiftingAndRepairReport.xhtml?faces-redirect=true";
                } else if (dobj.getValueOfRadioEvent().equalsIgnoreCase("R") && configDobj != null && configDobj.isAcknnowledge_report_in_restore()) {
                    return "/ui/reports/nonuseRestoreWithdrawl.xhtml?faces-redirect=true";
                }

            } else {
                return "seatwork";
            }
        }
        return "seatwork";
    }

    public void updateNonUseAmount() {
        try {
            boolean valid = validdate();
            if (valid) {
                long tax_amt = 0L;
                OwnerImpl ownerImpl = new OwnerImpl();
                TaxImpl taxImpl = new TaxImpl();
                Owner_dobj ownerDobj = ownerImpl.getOwnerDobj(ownerDetail);
                TaxDobj taxDobj = taxImpl.getTaxDetails(ownerDobj.getRegn_no().trim(), String.valueOf(TableConstants.TM_ROAD_TAX), ownerDobj.getState_cd());
                CashRecieptSubListDobj cashRecieptSubListDobj = impl.gettaxBreakupDetailsForNonUse(taxDobj);
                if (cashRecieptSubListDobj != null) {
                    tax_amt = Long.parseLong(cashRecieptSubListDobj.getAmount());
                } else {
                    tax_amt = taxDobj.getTax_amt();
                }
                int nonUseNoOfMonth = DateUtils.getDate1MinusDate2_Months(dobj.getExemp_from(), dobj.getExemp_to());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dobj.getExemp_to());
                calendar.add(Calendar.MONTH, 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.DATE, -1);
                Date lastDayOfExempTo = calendar.getTime();
                int result = DateUtils.compareDates(lastDayOfExempTo, dobj.getExemp_to());
                if (result == 2) {
                    nonUseNoOfMonth = nonUseNoOfMonth - 1;
                }
                String isGoodsVehicle = impl.getTransportCategory(ownerDobj.getVh_class());
                NonUseDobj tempNonUseDobj = impl.getAdjustmentAmount(taxDobj, isGoodsVehicle, nonUseNoOfMonth, dobj.getInsFlag(), tax_amt);
                if (tempNonUseDobj != null) {
                    dobj.setAdjustmentAmount(tempNonUseDobj.getAdjustmentAmount());
                    dobj.setNonUsePenalty(tempNonUseDobj.getNonUsePenalty());
                } else {
                    JSFUtils.setFacesMessage("Error in Calculating Nonuse Tax Amount/Penalty", "Error in Calculating Nonuse Tax Amount/Penalty", JSFUtils.ERROR);
                    return;
                }
            }
        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }

    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    public boolean isInspectPanel() {
        return inspectPanel;
    }

    public void setInspectPanel(boolean inspectPanel) {
        this.inspectPanel = inspectPanel;
    }

    public List getInspector_List() {
        return inspector_List;
    }

    public void setInspector_List(List inspector_List) {
        this.inspector_List = inspector_List;
    }

    public boolean isCertificationPanel() {
        return certificationPanel;
    }

    public void setCertificationPanel(boolean certificationPanel) {
        this.certificationPanel = certificationPanel;
    }

    public NonUseDobj getOld_dobj() {
        return old_dobj;
    }

    public void setOld_dobj(NonUseDobj old_dobj) {
        this.old_dobj = old_dobj;
    }

    public PermitPanelBean getPermitPanelBean() {
        return permitPanelBean;
    }

    public void setPermitPanelBean(PermitPanelBean permitPanelBean) {
        this.permitPanelBean = permitPanelBean;
    }

    public List<TaxFormPanelBean> getListTaxForm() {
        return listTaxForm;
    }

    public void setListTaxForm(List<TaxFormPanelBean> listTaxForm) {
        this.listTaxForm = listTaxForm;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getPageHeader() {
        return pageHeader;
    }

    public void setPageHeader(String pageHeader) {
        this.pageHeader = pageHeader;
    }

    public List getRegistringAuthority() {
        return registringAuthority;
    }

    public void setRegistringAuthority(List registringAuthority) {
        this.registringAuthority = registringAuthority;
    }

    public boolean isShowRestorePanel() {
        return showRestorePanel;
    }

    public void setShowRestorePanel(boolean showRestorePanel) {
        this.showRestorePanel = showRestorePanel;
    }

    public boolean isRenderRemovalOrRestore() {
        return renderRemovalOrRestore;
    }

    public void setRenderRemovalOrRestore(boolean renderRemovalOrRestore) {
        this.renderRemovalOrRestore = renderRemovalOrRestore;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public boolean isDisableNonuseAdjustmentAmount() {
        return disableNonuseAdjustmentAmount;
    }

    public void setDisableNonuseAdjustmentAmount(boolean disableNonuseAdjustmentAmount) {
        this.disableNonuseAdjustmentAmount = disableNonuseAdjustmentAmount;
    }

    public boolean isDisableNonuUseInfPanel() {
        return disableNonuUseInfPanel;
    }

    public void setDisableNonuUseInfPanel(boolean disableNonuUseInfPanel) {
        this.disableNonuUseInfPanel = disableNonuUseInfPanel;
    }

    public boolean isRenderInspDetailsCb() {
        return renderInspDetailsCb;
    }

    public void setRenderInspDetailsCb(boolean renderInspDetailsCb) {
        this.renderInspDetailsCb = renderInspDetailsCb;
    }

    public boolean isInspDetailsCb() {
        return inspDetailsCb;
    }

    public void setInspDetailsCb(boolean inspDetailsCb) {
        this.inspDetailsCb = inspDetailsCb;
    }

    public boolean isDisableNonuUseUpto() {
        return disableNonuUseUpto;
    }

    public void setDisableNonuUseUpto(boolean disableNonuUseUpto) {
        this.disableNonuUseUpto = disableNonuUseUpto;
    }

    public boolean isShowRemarksPanel() {
        return showRemarksPanel;
    }

    public void setShowRemarksPanel(boolean showRemarksPanel) {
        this.showRemarksPanel = showRemarksPanel;
    }

    public List getList_c_state() {
        return list_c_state;
    }

    public void setList_c_state(List list_c_state) {
        this.list_c_state = list_c_state;
    }

    public List getList_c_district() {
        return list_c_district;
    }

    public void setList_c_district(List list_c_district) {
        this.list_c_district = list_c_district;
    }

    public boolean isDisableGarageAddress() {
        return disableGarageAddress;
    }

    public void setDisableGarageAddress(boolean disableGarageAddress) {
        this.disableGarageAddress = disableGarageAddress;
    }

    public boolean isDisableRadioButton() {
        return disableRadioButton;
    }

    public void setDisableRadioButton(boolean disableRadioButton) {
        this.disableRadioButton = disableRadioButton;
    }

    public List<NonUseDobj> getPreviousNonUseList() {
        return previousNonUseList;
    }

    public void setPreviousNonUseList(List<NonUseDobj> previousNonUseList) {
        this.previousNonUseList = previousNonUseList;
    }

    public boolean isDisableNonuUsefrom() {
        return disableNonuUsefrom;
    }

    public void setDisableNonuUsefrom(boolean disableNonuUsefrom) {
        this.disableNonuUsefrom = disableNonuUsefrom;
    }

    /**
     * @return the allowDate
     */
    public Date getAllowDate() {
        return allowDate;
    }

    /**
     * @param currentMonth the currentMonth to set
     */
    public void setAllowDate(Date allowDate) {
        this.allowDate = allowDate;
    }

    /**
     * @return the inspMinDate
     */
    public Date getInspMinDate() {
        return inspMinDate;
    }

    /**
     * @param inspMinDate the inspMinDate to set
     */
    public void setInspMinDate(Date inspMinDate) {
        this.inspMinDate = inspMinDate;
    }

    /**
     * @return the withdrawlInfoPanel
     */
    public boolean isWithdrawlInfoPanel() {
        return withdrawlInfoPanel;
    }

    /**
     * @param withdrawlInfoPanel the withdrawlInfoPanel to set
     */
    public void setWithdrawlInfoPanel(boolean withdrawlInfoPanel) {
        this.withdrawlInfoPanel = withdrawlInfoPanel;
    }

    /**
     * @return the withdrawlMaxDate
     */
    public Date getWithdrawlMaxDate() {
        return withdrawlMaxDate;
    }

    /**
     * @param withdrawlMaxDate the withdrawlMaxDate to set
     */
    public void setWithdrawlMaxDate(Date withdrawlMaxDate) {
        this.withdrawlMaxDate = withdrawlMaxDate;
    }

    /**
     * @return the inspDate
     */
    public Date getInspDate() {
        return inspDate;
    }

    /**
     * @param inspDate the inspDate to set
     */
    public void setInspDate(Date inspDate) {
        this.inspDate = inspDate;
    }
}
