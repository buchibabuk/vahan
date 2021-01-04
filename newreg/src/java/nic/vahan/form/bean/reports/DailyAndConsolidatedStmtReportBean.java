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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.DailyAndConsolidatedStmtReportdobj;
import nic.vahan.form.impl.Home_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.PrinterDocDetails;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nic
 */
@ManagedBean(name = "dailyAndConsolidateStmtRptBean")
@ViewScoped
public class DailyAndConsolidatedStmtReportBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(DailyAndConsolidatedStmtReportBean.class);
    private String user_code;
    private List list_vm_user = new ArrayList();
    private String consolidateStmt;
    private List list_vm_consolidateStmt = new ArrayList();
    private String bank_code;
    private ArrayList list_vm_bankcd = new ArrayList();
    private String inst_type;
    private Date frdt_cal;
    private Date todt_cal;
    private Date today = new Date();
    private String offname;
    private String offcd = "0";
    private String statecd;
    private String usercd;
    private String username;
    private String statename;
    private String instrument;
    private boolean flag = false;
    private String bankcode;
    private String masterLayout = "/masterLayoutPage.xhtml";
    private boolean isButtonClick = true;
    private boolean dailyListDispaly = false;
    private boolean consolidateListDispaly = false;
    private DailyAndConsolidatedStmtReportdobj dobj = null;
    private List<DailyAndConsolidatedStmtReportdobj> returnlist = new ArrayList<DailyAndConsolidatedStmtReportdobj>();
    private List<DailyAndConsolidatedStmtReportdobj> draftlist = new ArrayList<DailyAndConsolidatedStmtReportdobj>();
    private List<DailyAndConsolidatedStmtReportdobj> dailylist = new ArrayList<>();
    private List<DailyAndConsolidatedStmtReportdobj> ConsolidateListonscreen = new ArrayList<>();
    private List<DailyAndConsolidatedStmtReportdobj> onscreenDraftchallangenerate = new ArrayList<>();
    private List<DailyAndConsolidatedStmtReportdobj> onscreenCancelReceipt = new ArrayList<>();
    private String frdateStr = null;
    private String cancel_receipt;
    private Date fromdraftDate;
    private Date todraftDate;
    private String ins_type;
    private String received_dt;
    private String bankName;
    private String branchName;
    private String amount;
    private int grandTotal;
    private String instrumentDate;
    private String header;
    private String instrument_no;
    private boolean flagvalue = false;
    private String AccountStatementType;
    private boolean is_surcharge;
    private boolean is_interest;
    private boolean is_penalty;
    private boolean is_transaction;
    private boolean is_total;
    private boolean is_acchead;
    private boolean is_amount;
    private boolean is_feeAndtax;
    private String consolidatHeaderLabel;
    private String selusercd;
    private String selusername;
    private List instrumentList = new ArrayList();
    ;
    private List<DailyAndConsolidatedStmtReportdobj> ddDtlsList = new ArrayList<>();
    private boolean displayInstDDDetails = false;
    private boolean is_amount1;
    private boolean is_amount2;
    private boolean is_dealerUser;
    private boolean ispaymentShare;
    private int tv_ntv_class_code = 1;
    private List list_tv_ntv_class_code = new ArrayList();
    private boolean renderClassType;
    private Date minToDate = null;
    private Date maxToDate = null;

    public DailyAndConsolidatedStmtReportBean() {
        String userCatg = "";
        masterLayout = "/ui/eapplication/masterLayoutEapplication.xhtml";
        try {
            this.todt_cal = today;
            this.frdt_cal = today;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date minDate = null;
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 0);
            minDate = sdf.parse("" + cal.get(Calendar.YEAR) + "-"
                    + (cal.get(Calendar.MONTH) + 0) + "-" + (cal.get(Calendar.DAY_OF_MONTH) + 1));
            this.minToDate = minDate;
            this.maxToDate = today;
            HttpSession session = Util.getSession();
            this.usercd = session.getAttribute("emp_cd").toString();
            if (Util.getSelectedSeat() != null) {
                this.offcd = String.valueOf(Util.getSelectedSeat().getOff_cd());
            } else {
                this.offcd = Util.getUserLoginOffCode().toString();
            }
            this.offname = getOfficeName();
            this.statecd = session.getAttribute("state_cd").toString();
            this.statename = session.getAttribute("state_name").toString();
            this.username = session.getAttribute("emp_name").toString();
            userCatg = session.getAttribute("user_catg").toString();
            list_vm_user.add(new SelectItem(session.getAttribute("emp_cd").toString(), session.getAttribute("emp_name").toString()));
            if (!userCatg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER) && !userCatg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN)) {
                list_vm_user.add(new SelectItem(TableConstants.ONLINE_PAYMENT, "Online Payment at Office"));
                list_vm_user.add(new SelectItem("0".toString(), "ALL CASHIER(ONLY VAHAN4 TRANSACTIONS)"));
                setIs_dealerUser(false);
            } else {
                setIs_dealerUser(true);
            }
            String[][] data = MasterTableFiller.masterTables.TM_BANK.getData();
            for (int i = 0; i < data.length; i++) {
                list_vm_bankcd.add(new SelectItem(data[i][0], data[i][1]));
            }
            list_vm_consolidateStmt.add(new SelectItem("D", "Daily Cash Account Statement"));
            list_vm_consolidateStmt.add(new SelectItem("T", "Transaction Wise Consolidated Statement"));
            list_vm_consolidateStmt.add(new SelectItem("H", "Account Head Wise Consolidated Statement"));
            setSelusercd(usercd);
            setSelusername(username);
            user_code = usercd;
            String[][] instrmentType = MasterTableFiller.masterTables.TM_INSTRUMENTS.getData();
            instrumentList.add(new SelectItem("-1", "Select"));
            for (int i = 0; i < instrmentType.length; i++) {
                if ((instrmentType[i][2].split(",")).length > 1) {
                    String[] statecdArray = instrmentType[i][2].split(",");
                    for (String statecd : statecdArray) {
                        if (statecd.equals(Util.getUserStateCode())) {
                            instrumentList.add(new SelectItem(instrmentType[i][0], instrmentType[i][1]));
                        }
                    }
                } else {
                    if (instrmentType[i][2].equals(Util.getUserStateCode()) || instrmentType[i][2].equals("ALL")) {
                        instrumentList.add(new SelectItem(instrmentType[i][0], instrmentType[i][1]));
                    }
                }
            }
            list_tv_ntv_class_code.add(new SelectItem(1, "Non-Transport/Transport Wise"));
            list_tv_ntv_class_code.add(new SelectItem(2, "Vehicle Class Wise"));
            list_tv_ntv_class_code.add(new SelectItem(3, "Payment Mode Wise"));

            PrinterDocDetails.insertIntoVmAccStmtSignIfNotExist(statecd, Integer.parseInt(offcd));
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
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
            if (!todate.equals(frdate)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date maxDate = null;
                Calendar maxcal = Calendar.getInstance();
                maxcal.setTime(frdt_cal);
                maxcal.add(Calendar.MONTH, +1);
                maxDate = sdf.parse("" + maxcal.get(Calendar.YEAR) + "-"
                        + (maxcal.get(Calendar.MONTH) + 1) + "-" + (maxcal.get(Calendar.DAY_OF_MONTH) - 1));
                this.setMinToDate(frdt_cal);
                if (maxDate.after(today)) {
                    this.setMaxToDate(today);
                    this.todt_cal = today;
                } else {
                    this.setMaxToDate(maxDate);
                    this.todt_cal = maxDate;
                }
                frdate = tosdf.parse(to_dte_formatter.format(((java.util.Date) frdt_cal).getTime()));
                todate = tosdf.parse(to_dte_formatter.format(((java.util.Date) todt_cal).getTime()));
            }
            if (frdate.after(todate)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "from date must be less then upto date!"));
                return;
            }
            if (frdate.compareTo(todate) == 0 && !user_code.equalsIgnoreCase("0")) {
                list_vm_consolidateStmt.clear();
                list_vm_consolidateStmt.add(new SelectItem("D", "Daily Cash Account Statement"));
                list_vm_consolidateStmt.add(new SelectItem("T", "Transaction Wise Consolidated Statement"));
                list_vm_consolidateStmt.add(new SelectItem("H", "Account Head Wise Consolidated Statement"));
            } else {
                list_vm_consolidateStmt.clear();
                list_vm_consolidateStmt.add(new SelectItem("T", "Transaction Wise Consolidated Statement"));
                list_vm_consolidateStmt.add(new SelectItem("H", "Account Head Wise Consolidated Statement"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void onUptoDateSelect() {
        try {
            DateFormat to_dte_formatter = new SimpleDateFormat("dd-MMM-yyyy");
            SimpleDateFormat tosdf = new SimpleDateFormat("dd-MMM-yyyy");
            if (frdt_cal == null || todt_cal == null) {
                return;
            }
            java.util.Date frdate = tosdf.parse(to_dte_formatter.format(((java.util.Date) frdt_cal).getTime()));
            java.util.Date todate = tosdf.parse(to_dte_formatter.format(((java.util.Date) todt_cal).getTime()));

            if (frdate.after(todate)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "from date must be less then upto date!"));
                return;
            }
            if (frdate.compareTo(todate) == 0 && !user_code.equalsIgnoreCase("0")) {
                list_vm_consolidateStmt.clear();
                list_vm_consolidateStmt.add(new SelectItem("D", "Daily Cash Account Statement"));
                list_vm_consolidateStmt.add(new SelectItem("T", "Transaction Wise Consolidated Statement"));
                list_vm_consolidateStmt.add(new SelectItem("H", "Account Head Wise Consolidated Statement"));
            } else {
                list_vm_consolidateStmt.clear();
                list_vm_consolidateStmt.add(new SelectItem("T", "Transaction Wise Consolidated Statement"));
                list_vm_consolidateStmt.add(new SelectItem("H", "Account Head Wise Consolidated Statement"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void onselectCashier() {
        try {
            DateFormat to_dte_formatter = new SimpleDateFormat("dd-MMM-yyyy");
            SimpleDateFormat tosdf = new SimpleDateFormat("dd-MMM-yyyy");

            java.util.Date frdate = tosdf.parse(to_dte_formatter.format(((java.util.Date) frdt_cal).getTime()));
            java.util.Date todate = tosdf.parse(to_dte_formatter.format(((java.util.Date) todt_cal).getTime()));
            if (user_code.equalsIgnoreCase("0")) {
                setSelusercd(user_code);
                setSelusername("ALL CASHIER(ONLY VAHAN4 TRANSACTIONS)");
            } else if (user_code.equalsIgnoreCase(TableConstants.ONLINE_PAYMENT)) {
                setSelusercd(user_code);
                setSelusername("Online Payment at Office");
            } else {
                setSelusercd(user_code);
                setSelusername(username);
            }
            if (!user_code.equalsIgnoreCase("0") && frdate.compareTo(todate) == 0) {
                list_vm_consolidateStmt.clear();
                list_vm_consolidateStmt.add(new SelectItem("D", "Daily Cash Account Statement"));
                list_vm_consolidateStmt.add(new SelectItem("T", "Transaction Wise Consolidated Statement"));
                list_vm_consolidateStmt.add(new SelectItem("H", "Account Head Wise Consolidated Statement"));
            } else {
                list_vm_consolidateStmt.clear();
                list_vm_consolidateStmt.add(new SelectItem("T", "Transaction Wise Consolidated Statement"));
                list_vm_consolidateStmt.add(new SelectItem("H", "Account Head Wise Consolidated Statement"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    private String getOfficeName() {
        String officeName = "";
        Map<String, Object> allotedOffCodeList = Home_Impl.getAllotedOfficeCodeDescr(false);
        for (Map.Entry<String, Object> offcdEntry : allotedOffCodeList.entrySet()) {
            officeName = offcdEntry.getKey();
            break;
        }
        return officeName;
    }

    public void displayDailyAccStmt() {
        try {
            if (!getOnscreenDraftchallangenerate().isEmpty()) {
                getOnscreenDraftchallangenerate().clear();
            }
            if (frdt_cal == null || frdt_cal.equals("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "from date could not be blank!"));
                return;
            }
            if (todt_cal == null || todt_cal.equals("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "upto date could not be blank!"));
                return;
            }

            DateFormat frm_dte_formatter = new SimpleDateFormat("dd-MMM-yyyy");
            setFrdateStr(frm_dte_formatter.format(((java.util.Date) frdt_cal).getTime()));
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
            java.util.Date frdate = sdf1.parse(getFrdateStr());
            java.sql.Date frm_dte = new java.sql.Date(frdate.getTime());
            DateFormat to_dte_formatter = new SimpleDateFormat("dd-MMM-yyyy");

            SimpleDateFormat tosdf = new SimpleDateFormat("dd-MMM-yyyy");
            java.util.Date todate = tosdf.parse(to_dte_formatter.format(((java.util.Date) todt_cal).getTime()));
            java.sql.Date to_dte = new java.sql.Date(todate.getTime());

            if (frm_dte.after(to_dte)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "from date must be less then upto date!"));
                return;
            }
            if (consolidateStmt.equalsIgnoreCase("D")) {
                setConsolidatHeaderLabel("Daily Cash Account Statement( " + getSelusername() + " )");
                setDailyListDispaly(true);
                setConsolidateListDispaly(false);
                setIs_transaction(true);
                setIs_total(true);
                setIs_penalty(true);
                setIs_feeAndtax(true);
                setIs_acchead(false);
                setIs_amount(false);
                ArrayList<DailyAndConsolidatedStmtReportdobj> list = PrinterDocDetails.getDailyCashReport(user_code, frm_dte, to_dte, statecd, offcd);
                if (list.isEmpty()) {
                    setIs_surcharge(false);
                    setIs_interest(false);
                    setIs_amount1(false);
                    setIs_amount2(false);
                    setIspaymentShare(false);
                } else {
                    setDisplayInstDDDetails(true);
                    dobj = list.get(list.size() - 1);
                    if (dobj.isIs_surcharge()) {
                        setIs_surcharge(true);
                    }
                    if (dobj.isIs_interest()) {
                        setIs_interest(true);
                    }
                    if (dobj.isIs_amount1()) {
                        setIs_amount1(true);
                    }
                    if (dobj.isIs_amount2()) {
                        setIs_amount2(true);
                    }
                    if (dobj.isIspaymentShare()) {
                        setIspaymentShare(true);
                    }
                }
                setDailylist(list);
                setOnscreenCancelReceipt(PrinterDocDetails.getCancelReport(user_code, frm_dte, to_dte, statecd, offcd));
                if (getDdDtlsList() != null) {
                    getDdDtlsList().clear();
                }
                if (user_code.equals("9999999999")) {
                    setDisplayInstDDDetails(false);
                } else {
                    setDisplayInstDDDetails(true);
                    setDdDtlsList(PrinterDocDetails.getDDDetails(statecd, offcd, user_code, frm_dte, to_dte));
                }

                if (dobj != null && !getDdDtlsList().isEmpty()) {
                    dobj.setGrandTotal(Integer.parseInt(getDdDtlsList().get(getDdDtlsList().size() - 1).getTotal()));
                }
            } else if (consolidateStmt.equalsIgnoreCase("H")) {
                setConsolidatHeaderLabel("Account Head Wise Consolidated Statement ( " + getSelusername() + " )");
                setDailyListDispaly(false);
                setConsolidateListDispaly(true);
                setIs_surcharge(false);
                setIs_interest(false);
                setIs_transaction(false);
                setIs_total(false);
                setIs_penalty(false);
                setIs_feeAndtax(false);
                setIs_acchead(true);
                setIs_amount(true);
                setIs_amount1(false);
                setIs_amount2(false);
                setDisplayInstDDDetails(false);
                ArrayList<DailyAndConsolidatedStmtReportdobj> list = PrinterDocDetails.getHeadWiseConsolidatedStmReport(user_code, offcd, frm_dte, to_dte, statecd, tv_ntv_class_code);
                setConsolidateListonscreen(list);
                if (!list.isEmpty()) {
                    setDisplayInstDDDetails(true);
                    dobj = list.get(list.size() - 1);
                    if (dobj.isRenderClassType()) {
                        setRenderClassType(true);
                    } else {
                        setRenderClassType(false);
                    }
                }
                if (getDdDtlsList() != null) {
                    getDdDtlsList().clear();
                }
                if (user_code.equals("9999999999")) {
                    setDisplayInstDDDetails(false);
                } else {
                    setDisplayInstDDDetails(true);
                    setDdDtlsList(PrinterDocDetails.getDDDetails(statecd, offcd, user_code, frm_dte, to_dte));
                }

                if (dobj != null && !getDdDtlsList().isEmpty()) {
                    dobj.setGrandTotal(Integer.parseInt(getDdDtlsList().get(getDdDtlsList().size() - 1).getTotal()));
                }
            } else {
                setConsolidatHeaderLabel("Transaction Wise Consolidated Statement ( " + getSelusername() + " )");
                setDailyListDispaly(false);
                setConsolidateListDispaly(true);
                setIs_transaction(true);
                setIs_total(true);
                setIs_penalty(true);
                setIs_feeAndtax(true);
                setIs_acchead(false);
                setIs_amount(false);
                setDisplayInstDDDetails(false);
                ArrayList<DailyAndConsolidatedStmtReportdobj> list = PrinterDocDetails.getConsolidatedStmReport(user_code, offcd, frm_dte, to_dte, statecd, tv_ntv_class_code);
                if (list.isEmpty()) {
                    setIs_surcharge(false);
                    setIs_interest(false);
                    setIs_amount1(false);
                    setIs_amount2(false);
                    setIspaymentShare(false);
                } else {
                    setDisplayInstDDDetails(true);
                    dobj = list.get(list.size() - 1);
                    if (dobj.isIs_surcharge()) {
                        setIs_surcharge(true);
                    }
                    if (dobj.isIs_interest()) {
                        setIs_interest(true);
                    }
                    if (dobj.isIs_amount1()) {
                        setIs_amount1(true);
                    }
                    if (dobj.isIs_amount2()) {
                        setIs_amount2(true);
                    }
                    if (dobj.isIspaymentShare()) {
                        setIspaymentShare(true);
                    }
                    if (dobj.isRenderClassType()) {
                        setRenderClassType(true);
                    } else {
                        setRenderClassType(false);
                    }
                }
                setConsolidateListonscreen(list);
                if (getDdDtlsList() != null) {
                    getDdDtlsList().clear();
                }
                if (user_code.equals("9999999999")) {
                    setDisplayInstDDDetails(false);
                } else {
                    setDisplayInstDDDetails(true);
                    setDdDtlsList(PrinterDocDetails.getDDDetails(statecd, offcd, user_code, frm_dte, to_dte));
                }
                if (dobj != null && !getDdDtlsList().isEmpty()) {
                    dobj.setGrandTotal(Integer.parseInt(getDdDtlsList().get(getDdDtlsList().size() - 1).getTotal()));;
                }
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }


    }

    public void displayDailyDraftStmReport() {
        try {
            if (!getOnscreenDraftchallangenerate().isEmpty()) {
                getOnscreenDraftchallangenerate().clear();
            }
            setDisplayInstDDDetails(false);
            if (frdt_cal == null || frdt_cal.equals("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "from date could not be blank!"));
                return;
            }
            if (todt_cal == null || todt_cal.equals("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "upto date could not be blank!"));
                return;
            }
            if (this.getInst_type() == null || this.getInst_type().equals("-1")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please select Instrument Type!"));
                return;
            }
            if (user_code.equals(TableConstants.ONLINE_PAYMENT)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Selected User Name in not Applicable to Dispaly/Generate the Draft / Cheque Collected Statement!"));
                return;
            }
            setDailyListDispaly(false);
            setConsolidateListDispaly(false);
            this.instrument = this.getInst_type();
            this.bankcode = this.getBank_code();
            DateFormat readFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
            Date datefrom = (Date) readFormat.parse(frdt_cal.toString());
            Date dateupto = (Date) readFormat.parse(todt_cal.toString());
            java.sql.Date frm_dte = new java.sql.Date(datefrom.getTime());
            java.sql.Date to_dte = new java.sql.Date(dateupto.getTime());
            if (frm_dte.after(to_dte)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "from date must be less then upto date!"));
                return;
            }
            setOnscreenDraftchallangenerate(PrinterDocDetails.getDailyDraftReport(frm_dte, to_dte, user_code, getSelusername(), offcd, offname, statecd, statename, instrument, bankcode));
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }


    }

    public String printDailyAccStmt() throws ParseException {
        try {
            if (frdt_cal == null || frdt_cal.equals("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "from date could not be blank!"));
                return "";
            }
            if (todt_cal == null || todt_cal.equals("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "upto date could not be blank!"));
                return "";
            }
            DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            String user_catg = "";
            user_catg = (String) Util.getSession().getAttribute("user_catg");
            if (user_catg.equals(TableConstants.USER_CATG_OFFICE_ADMIN)
                    || user_catg.equals(TableConstants.USER_CATG_OFF_STAFF)) {
                List list = Home_Impl.getCashCounterDetails();
                if (format.format(((java.util.Date) frdt_cal).getTime()).equals(Home_Impl.getDBCurrentDate())) {
                    if (list.size() < 0 || ((boolean) list.get(1)) == false) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Cash counter is opened!"));
                        return "";
                    }
                }
            }
            flag = true;
            DateFormat frm_dte_formatter = new SimpleDateFormat("dd-MMM-yyyy");
            setFrdateStr(frm_dte_formatter.format(((java.util.Date) frdt_cal).getTime()));
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
            java.util.Date frdate = sdf1.parse(getFrdateStr());
            java.sql.Date frm_dte = new java.sql.Date(frdate.getTime());
            DateFormat to_dte_formatter = new SimpleDateFormat("dd-MMM-yyyy");
            SimpleDateFormat tosdf = new SimpleDateFormat("dd-MMM-yyyy");
            java.util.Date todate = tosdf.parse(to_dte_formatter.format(((java.util.Date) todt_cal).getTime()));
            java.sql.Date to_dte = new java.sql.Date(todate.getTime());
            if (frm_dte.after(to_dte)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "from date must be less then upto date!"));
                return "";
            }
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("flag", flag);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("frDate", frm_dte);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("ToDate", to_dte);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("flag", flag);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("AccStmtType", consolidateStmt);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selusercd", getSelusercd());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selusername", getSelusername());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("tv_ntv_class_code", getTv_ntv_class_code());
            if (Util.getSelectedSeat() == null) {
                throw new VahanException("Printing failed, Please try again.");
            }


        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return "";
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return "";
        }
        if (consolidateStmt.equalsIgnoreCase("D")) {
            return "DailyCashReport";
        } else {
            return "ConsolidateReport";
        }

    }

    public String printDailyDraftStmReport() {
        try {
            if (frdt_cal == null || frdt_cal.equals("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "from date could not be blank!"));
                return "";
            }
            if (todt_cal == null || todt_cal.equals("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "upto date could not be blank!"));
                return "";
            }
            if (this.getInst_type() == null || this.getInst_type().equals("-1")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please select Instrument Type!"));
                return "";
            }
            DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            String user_catg = "";
            user_catg = (String) Util.getSession().getAttribute("user_catg");
            if (user_catg.equals(TableConstants.USER_CATG_OFFICE_ADMIN)
                    || user_catg.equals(TableConstants.USER_CATG_OFF_STAFF)) {
                List list = Home_Impl.getCashCounterDetails();
                if (format.format(((java.util.Date) frdt_cal).getTime()).equals(Home_Impl.getDBCurrentDate())) {
                    if (list.size() < 0 || ((boolean) list.get(1)) == false) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Cash counter is opened!"));
                        return "";
                    }
                }
            }

            flag = false;
            this.instrument = this.getInst_type();
            this.bankcode = this.getBank_code();

            DateFormat readFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
            Date datefrom = (Date) readFormat.parse(frdt_cal.toString());
            Date dateupto = (Date) readFormat.parse(todt_cal.toString());
            java.sql.Date frm_dte = new java.sql.Date(datefrom.getTime());
            java.sql.Date to_dte = new java.sql.Date(dateupto.getTime());
            if (frm_dte.after(to_dte)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "from date must be less then upto date!"));
                return "";
            }
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("frDate", frm_dte);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("ToDate", to_dte);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("instrument", instrument);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("bankCode", bankcode);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("flag", flag);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selusercd", getSelusercd());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selusername", getSelusername());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("tv_ntv_class_code", getTv_ntv_class_code());
            

        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return "";
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return "";
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("draftlist", draftlist);
        return "DraftReport";
    }

    public void bankStateChangeListener() {
    }

    public List getList_vm_user() {
        return list_vm_user;
    }

    /**
     * @param list_vm_user the list_vm_user to set
     */
    public void setList_vm_user(List list_vm_user) {
        this.list_vm_user = list_vm_user;
    }

    /**
     * @return the consolidateStmt
     */
    public String getConsolidateStmt() {
        return consolidateStmt;
    }

    /**
     * @param consolidateStmt the consolidateStmt to set
     */
    public void setConsolidateStmt(String consolidateStmt) {
        this.consolidateStmt = consolidateStmt;
    }

    /**
     * @return the list_vm_consolidateStmt
     */
    public List getList_vm_consolidateStmt() {
        return list_vm_consolidateStmt;
    }

    /**
     * @param list_vm_consolidateStmt the list_vm_consolidateStmt to set
     */
    public void setList_vm_consolidateStmt(List list_vm_consolidateStmt) {
        this.list_vm_consolidateStmt = list_vm_consolidateStmt;
    }

    /**
     * @return the bank_code
     */
    public String getBank_code() {
        return bank_code;
    }

    /**
     * @param bank_code the bank_code to set
     */
    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    /**
     * @return the list_vm_bankcd
     */
    public ArrayList getList_vm_bankcd() {
        return list_vm_bankcd;
    }

    /**
     * @param list_vm_bankcd the list_vm_bankcd to set
     */
    public void setList_vm_bankcd(ArrayList list_vm_bankcd) {
        this.list_vm_bankcd = list_vm_bankcd;
    }

    /**
     * @return the inst_type
     */
    public String getInst_type() {
        return inst_type;
    }

    /**
     * @param inst_type the inst_type to set
     */
    public void setInst_type(String inst_type) {
        this.inst_type = inst_type;
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
     * @return the offname
     */
    public String getOffname() {
        return offname;
    }

    /**
     * @param offname the offname to set
     */
    public void setOffname(String offname) {
        this.offname = offname;
    }

    /**
     * @return the statecd
     */
    public String getStatecd() {
        return statecd;
    }

    /**
     * @param statecd the statecd to set
     */
    public void setStatecd(String statecd) {
        this.statecd = statecd;
    }

    /**
     * @return the usercd
     */
    public String getUsercd() {
        return usercd;
    }

    /**
     * @param usercd the usercd to set
     */
    public void setUsercd(String usercd) {
        this.usercd = usercd;
    }

    /**
     * @return the masterLayout
     */
    public String getMasterLayout() {
        return masterLayout;
    }

    /**
     * @param masterLayout the masterLayout to set
     */
    public void setMasterLayout(String masterLayout) {
        this.masterLayout = masterLayout;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the statename
     */
    public String getStatename() {
        return statename;
    }

    /**
     * @param statename the statename to set
     */
    public void setStatename(String statename) {
        this.statename = statename;
    }

    /**
     * @return the offcd
     */
    public String getOffcd() {
        return offcd;
    }

    /**
     * @param offcd the offcd to set
     */
    public void setOffcd(String offcd) {
        this.offcd = offcd;
    }

    /**
     * @return the isButtonClick
     */
    public boolean isIsButtonClick() {
        return isButtonClick;
    }

    /**
     * @param isButtonClick the isButtonClick to set
     */
    public void setIsButtonClick(boolean isButtonClick) {
        this.isButtonClick = isButtonClick;
    }

    /**
     * @return the returnlist
     */
    public List<DailyAndConsolidatedStmtReportdobj> getReturnlist() {
        return returnlist;
    }

    /**
     * @param returnlist the returnlist to set
     */
    public void setReturnlist(List<DailyAndConsolidatedStmtReportdobj> returnlist) {
        this.returnlist = returnlist;
    }

    /**
     * @return the dobj
     */
    public DailyAndConsolidatedStmtReportdobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(DailyAndConsolidatedStmtReportdobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the draftlist
     */
    public List<DailyAndConsolidatedStmtReportdobj> getDraftlist() {
        return draftlist;
    }

    /**
     * @param draftlist the draftlist to set
     */
    public void setDraftlist(List<DailyAndConsolidatedStmtReportdobj> draftlist) {
        this.draftlist = draftlist;
    }

    /**
     * @return the flag
     */
    public boolean isFlag() {
        return flag;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    /**
     * @return the frdateStr
     */
    public String getFrdateStr() {
        return frdateStr;
    }

    /**
     * @param frdateStr the frdateStr to set
     */
    public void setFrdateStr(String frdateStr) {
        this.frdateStr = frdateStr;
    }

    /**
     * @return the cancel_receipt
     */
    public String getCancel_receipt() {
        return cancel_receipt;
    }

    /**
     * @param cancel_receipt the cancel_receipt to set
     */
    public void setCancel_receipt(String cancel_receipt) {
        this.cancel_receipt = cancel_receipt;
    }

    /**
     * @return the fromdraftDate
     */
    public Date getFromdraftDate() {
        return fromdraftDate;
    }

    /**
     * @param fromdraftDate the fromdraftDate to set
     */
    public void setFromdraftDate(Date fromdraftDate) {
        this.fromdraftDate = fromdraftDate;
    }

    /**
     * @return the todraftDate
     */
    public Date getTodraftDate() {
        return todraftDate;
    }

    /**
     * @param todraftDate the todraftDate to set
     */
    public void setTodraftDate(Date todraftDate) {
        this.todraftDate = todraftDate;
    }

    /**
     * @return the ins_type
     */
    public String getIns_type() {
        return ins_type;
    }

    /**
     * @param ins_type the ins_type to set
     */
    public void setIns_type(String ins_type) {
        this.ins_type = ins_type;
    }

    /**
     * @return the received_dt
     */
    public String getReceived_dt() {
        return received_dt;
    }

    /**
     * @param received_dt the received_dt to set
     */
    public void setReceived_dt(String received_dt) {
        this.received_dt = received_dt;
    }

    /**
     * @return the bankName
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * @param bankName the bankName to set
     */
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    /**
     * @return the branchName
     */
    public String getBranchName() {
        return branchName;
    }

    /**
     * @param branchName the branchName to set
     */
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    /**
     * @return the amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * @return the grandTotal
     */
    public int getGrandTotal() {
        return grandTotal;
    }

    /**
     * @param grandTotal the grandTotal to set
     */
    public void setGrandTotal(int grandTotal) {
        this.grandTotal = grandTotal;
    }

    /**
     * @return the instrumentDate
     */
    public String getInstrumentDate() {
        return instrumentDate;
    }

    /**
     * @param instrumentDate the instrumentDate to set
     */
    public void setInstrumentDate(String instrumentDate) {
        this.instrumentDate = instrumentDate;
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the instrument_no
     */
    public String getInstrument_no() {
        return instrument_no;
    }

    /**
     * @param instrument_no the instrument_no to set
     */
    public void setInstrument_no(String instrument_no) {
        this.instrument_no = instrument_no;
    }

    /**
     * @return the flagvalue
     */
    public boolean isFlagvalue() {
        return flagvalue;
    }

    /**
     * @param flagvalue the flagvalue to set
     */
    public void setFlagvalue(boolean flagvalue) {
        this.flagvalue = flagvalue;
    }

    /**
     * @return the AccountStatementType
     */
    public String getAccountStatementType() {
        return AccountStatementType;
    }

    /**
     * @param AccountStatementType the AccountStatementType to set
     */
    public void setAccountStatementType(String AccountStatementType) {
        this.AccountStatementType = AccountStatementType;
    }

    /**
     * @return the ConsolidateListonscreen
     */
    public List<DailyAndConsolidatedStmtReportdobj> getConsolidateListonscreen() {
        return ConsolidateListonscreen;
    }

    /**
     * @param ConsolidateListonscreen the ConsolidateListonscreen to set
     */
    public void setConsolidateListonscreen(List<DailyAndConsolidatedStmtReportdobj> ConsolidateListonscreen) {
        this.ConsolidateListonscreen = ConsolidateListonscreen;
    }

    /**
     * @return the onscreenDraftchallangenerate
     */
    public List<DailyAndConsolidatedStmtReportdobj> getOnscreenDraftchallangenerate() {
        return onscreenDraftchallangenerate;
    }

    /**
     * @param onscreenDraftchallangenerate the onscreenDraftchallangenerate to
     * set
     */
    public void setOnscreenDraftchallangenerate(List<DailyAndConsolidatedStmtReportdobj> onscreenDraftchallangenerate) {
        this.onscreenDraftchallangenerate = onscreenDraftchallangenerate;
    }

    /**
     * @return the onscreenCancelReceipt
     */
    public List<DailyAndConsolidatedStmtReportdobj> getOnscreenCancelReceipt() {
        return onscreenCancelReceipt;
    }

    /**
     * @param onscreenCancelReceipt the onscreenCancelReceipt to set
     */
    public void setOnscreenCancelReceipt(List<DailyAndConsolidatedStmtReportdobj> onscreenCancelReceipt) {
        this.onscreenCancelReceipt = onscreenCancelReceipt;
    }

    /**
     * @return the dailyListDispaly
     */
    public boolean isDailyListDispaly() {
        return dailyListDispaly;
    }

    /**
     * @param dailyListDispaly the dailyListDispaly to set
     */
    public void setDailyListDispaly(boolean dailyListDispaly) {
        this.dailyListDispaly = dailyListDispaly;
    }

    /**
     * @return the consolidateListDispaly
     */
    public boolean isConsolidateListDispaly() {
        return consolidateListDispaly;
    }

    /**
     * @param consolidateListDispaly the consolidateListDispaly to set
     */
    public void setConsolidateListDispaly(boolean consolidateListDispaly) {
        this.consolidateListDispaly = consolidateListDispaly;
    }

    /**
     * @return the dailylist
     */
    public List<DailyAndConsolidatedStmtReportdobj> getDailylist() {
        return dailylist;
    }

    /**
     * @param dailylist the dailylist to set
     */
    public void setDailylist(List<DailyAndConsolidatedStmtReportdobj> dailylist) {
        this.dailylist = dailylist;
    }

    /**
     * @return the is_surcharge
     */
    public boolean isIs_surcharge() {
        return is_surcharge;
    }

    /**
     * @param is_surcharge the is_surcharge to set
     */
    public void setIs_surcharge(boolean is_surcharge) {
        this.is_surcharge = is_surcharge;
    }

    /**
     * @return the is_interest
     */
    public boolean isIs_interest() {
        return is_interest;
    }

    /**
     * @param is_interest the is_interest to set
     */
    public void setIs_interest(boolean is_interest) {
        this.is_interest = is_interest;
    }

    /**
     * @return the is_penalty
     */
    public boolean isIs_penalty() {
        return is_penalty;
    }

    /**
     * @param is_penalty the is_penalty to set
     */
    public void setIs_penalty(boolean is_penalty) {
        this.is_penalty = is_penalty;
    }

    /**
     * @return the is_transaction
     */
    public boolean isIs_transaction() {
        return is_transaction;
    }

    /**
     * @param is_transaction the is_transaction to set
     */
    public void setIs_transaction(boolean is_transaction) {
        this.is_transaction = is_transaction;
    }

    /**
     * @return the is_total
     */
    public boolean isIs_total() {
        return is_total;
    }

    /**
     * @param is_total the is_total to set
     */
    public void setIs_total(boolean is_total) {
        this.is_total = is_total;
    }

    /**
     * @return the is_acchead
     */
    public boolean isIs_acchead() {
        return is_acchead;
    }

    /**
     * @param is_acchead the is_acchead to set
     */
    public void setIs_acchead(boolean is_acchead) {
        this.is_acchead = is_acchead;
    }

    /**
     * @return the is_amount
     */
    public boolean isIs_amount() {
        return is_amount;
    }

    /**
     * @param is_amount the is_amount to set
     */
    public void setIs_amount(boolean is_amount) {
        this.is_amount = is_amount;
    }

    /**
     * @return the is_feeAndtax
     */
    public boolean isIs_feeAndtax() {
        return is_feeAndtax;
    }

    /**
     * @param is_feeAndtax the is_feeAndtax to set
     */
    public void setIs_feeAndtax(boolean is_feeAndtax) {
        this.is_feeAndtax = is_feeAndtax;
    }

    /**
     * @return the consolidatHeaderLabel
     */
    public String getConsolidatHeaderLabel() {
        return consolidatHeaderLabel;
    }

    /**
     * @param consolidatHeaderLabel the consolidatHeaderLabel to set
     */
    public void setConsolidatHeaderLabel(String consolidatHeaderLabel) {
        this.consolidatHeaderLabel = consolidatHeaderLabel;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    /**
     * @return the selusercd
     */
    public String getSelusercd() {
        return selusercd;
    }

    /**
     * @param selusercd the selusercd to set
     */
    public void setSelusercd(String selusercd) {
        this.selusercd = selusercd;

    }

    /**
     * @return the selusername
     */
    public String getSelusername() {
        return selusername;
    }

    /**
     * @param selusername the selusername to set
     */
    public void setSelusername(String selusername) {
        this.selusername = selusername;
    }

    /**
     * @return the instrumentList
     */
    public List getInstrumentList() {
        return instrumentList;
    }

    /**
     * @param instrumentList the instrumentList to set
     */
    public void setInstrumentList(List instrumentList) {
        this.instrumentList = instrumentList;
    }

    /**
     * @return the ddDtlsList
     */
    public List<DailyAndConsolidatedStmtReportdobj> getDdDtlsList() {
        return ddDtlsList;
    }

    /**
     * @param ddDtlsList the ddDtlsList to set
     */
    public void setDdDtlsList(List<DailyAndConsolidatedStmtReportdobj> ddDtlsList) {
        this.ddDtlsList = ddDtlsList;
    }

    /**
     * @return the displayInstDDDetails
     */
    public boolean isDisplayInstDDDetails() {
        return displayInstDDDetails;
    }

    /**
     * @param displayInstDDDetails the displayInstDDDetails to set
     */
    public void setDisplayInstDDDetails(boolean displayInstDDDetails) {
        this.displayInstDDDetails = displayInstDDDetails;
    }

    /**
     * @return the is_amount1
     */
    public boolean isIs_amount1() {
        return is_amount1;
    }

    /**
     * @param is_amount1 the is_amount1 to set
     */
    public void setIs_amount1(boolean is_amount1) {
        this.is_amount1 = is_amount1;
    }

    /**
     * @return the is_amount2
     */
    public boolean isIs_amount2() {
        return is_amount2;
    }

    /**
     * @param is_amount2 the is_amount2 to set
     */
    public void setIs_amount2(boolean is_amount2) {
        this.is_amount2 = is_amount2;
    }

    /**
     * @return the is_dealerUser
     */
    public boolean isIs_dealerUser() {
        return is_dealerUser;
    }

    /**
     * @param is_dealerUser the is_dealerUser to set
     */
    public void setIs_dealerUser(boolean is_dealerUser) {
        this.is_dealerUser = is_dealerUser;
    }

    /**
     * @return the ispaymentShare
     */
    public boolean isIspaymentShare() {
        return ispaymentShare;
    }

    /**
     * @param ispaymentShare the ispaymentShare to set
     */
    public void setIspaymentShare(boolean ispaymentShare) {
        this.ispaymentShare = ispaymentShare;
    }

    /**
     * @return the tv_ntv_class_code
     */
    public int getTv_ntv_class_code() {
        return tv_ntv_class_code;
    }

    /**
     * @param tv_ntv_class_code the tv_ntv_class_code to set
     */
    public void setTv_ntv_class_code(int tv_ntv_class_code) {
        this.tv_ntv_class_code = tv_ntv_class_code;
    }

    /**
     * @return the list_tv_ntv_class_code
     */
    public List getList_tv_ntv_class_code() {
        return list_tv_ntv_class_code;
    }

    /**
     * @param list_tv_ntv_class_code the list_tv_ntv_class_code to set
     */
    public void setList_tv_ntv_class_code(List list_tv_ntv_class_code) {
        this.list_tv_ntv_class_code = list_tv_ntv_class_code;
    }

    /**
     * @return the renderClassType
     */
    public boolean isRenderClassType() {
        return renderClassType;
    }

    /**
     * @param renderClassType the renderClassType to set
     */
    public void setRenderClassType(boolean renderClassType) {
        this.renderClassType = renderClassType;
    }

    /**
     * @return the minToDate
     */
    public Date getMinToDate() {
        return minToDate;
    }

    /**
     * @param minToDate the minToDate to set
     */
    public void setMinToDate(Date minToDate) {
        this.minToDate = minToDate;
    }

    /**
     * @return the maxToDate
     */
    public Date getMaxToDate() {
        return maxToDate;
    }

    /**
     * @param maxToDate the maxToDate to set
     */
    public void setMaxToDate(Date maxToDate) {
        this.maxToDate = maxToDate;
    }
}
