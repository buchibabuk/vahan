/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import nic.java.util.CommonUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.DailyAndConsolidatedStmtReportdobj;
import nic.vahan.form.impl.Home_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.PrinterDocDetails;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nprpE081
 */
@ManagedBean(name = "dailyreport")
@ViewScoped
public final class DailycashReportbean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(DailycashReportbean.class);
    private DailyAndConsolidatedStmtReportdobj dobj = null;
    private List<DailyAndConsolidatedStmtReportdobj> returnlist = new ArrayList<DailyAndConsolidatedStmtReportdobj>();
    private List<DailyAndConsolidatedStmtReportdobj> cancelreceiptlist = new ArrayList<DailyAndConsolidatedStmtReportdobj>();
    private List<DailyAndConsolidatedStmtReportdobj> draftlist = new ArrayList<DailyAndConsolidatedStmtReportdobj>();
    private List<DailyAndConsolidatedStmtReportdobj> consolidateList = new ArrayList<DailyAndConsolidatedStmtReportdobj>();
    private ArrayList list_vm_instruement_type = new ArrayList();
    private String name;
    private String application_No;
    private String regn_no;
    private String vehicle_Class;
    private String transaction;
    private String receipt_No;
    private String feeCM_Penalty_Tax;
    private String total;
    private int off_cd;
    private Date frDt;
    private Date toDt;
    private String username;
    private String statename;
    private Date fromDate;
    private Date toDate;
    private String offname;
    private String offcd;
    private String statecd;
    private String usercd;
    private Calendar frdt_cal = new Calendar();
    private Calendar todt_cal = new Calendar();
    private String[] cancel_receipt_list;
    private String cancel_receipt;
    private Date fromdraftDate;
    private Date todraftDate;
    private String ins_type;
    private String received_dt;
    private String bankName;
    private String branchName;
    private String amount;
    private int grandTotal;
    private String instrument;
    private String bankcode;
    private String instrumentDate;
    private String header;
    private String instrument_no;
    boolean flagvalue = false;
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
    private List<DailyAndConsolidatedStmtReportdobj> ddDtlsList = new ArrayList<>();
    private boolean is_amount1;
    private boolean is_amount2;
    private String grandTotalInWords;
    private String cashier;
    private String accountant;
    private String superintendent;
    private String patorto;
    private String rto;
    private boolean ispaymentShare;
    private int tv_ntv_class_code;
    private boolean displayInstDDDetails;
    private boolean renderClassType;

    public DailycashReportbean() {
        HttpSession session = Util.getSession();
        this.usercd = session.getAttribute("emp_cd").toString();
        this.statecd = session.getAttribute("state_cd").toString();
        this.statename = session.getAttribute("state_name").toString();
        this.username = session.getAttribute("emp_name").toString();
        if (Util.getSelectedSeat() != null) {
            this.offcd = Util.getSelectedSeat().getOff_cd() + "";
            this.offname = ServerUtil.getOfficeName(Util.getSelectedSeat().getOff_cd(), statecd);
        }
    }

    @PostConstruct
    public void init() {
        Exception exception = null;
        try {
            if (CommonUtils.isNullOrBlank(offcd)) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
            Utility utility = new Utility();
            AccountStatementType = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("AccStmtType");
            fromDate = (Date) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("frDate");
            toDate = (Date) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("ToDate");
            this.instrument = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("instrument");
            this.flagvalue = (boolean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("flag");
            this.bankcode = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("bankCode");
            setSelusercd((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selusercd"));
            setSelusername((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selusername"));
            setTv_ntv_class_code((int) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("tv_ntv_class_code"));


            if (this.flagvalue == true) {
                printDailyAccStmt();
                if (getSelusercd().equals("9999999999")) {
                    setDisplayInstDDDetails(false);
                } else {
                    setDisplayInstDDDetails(true);
                    getCancelReceiptList();
                    setDdDtlsList(PrinterDocDetails.getDDDetails(statecd, offcd, getSelusercd(), fromDate, toDate));
                }

                if (!getDdDtlsList().isEmpty()) {
                    getDobj().setGrandTotal(Integer.parseInt(getDdDtlsList().get(getDdDtlsList().size() - 1).getTotal()));
                    getDobj().setGrandTotalInwords(utility.ConvertNumberToWords(dobj.getGrandTotal()));
                } else {
                    if (!getConsolidateList().isEmpty()) {
                        getDobj().setGrandTotal(Integer.parseInt(getConsolidateList().get(getConsolidateList().size() - 1).getTotal()));
                        getDobj().setGrandTotalInwords(utility.ConvertNumberToWords(Integer.parseInt(getConsolidateList().get(getConsolidateList().size() - 1).getTotal())));
                    } else if (!getReturnlist().isEmpty()) {
                        getDobj().setGrandTotal(Integer.parseInt(getReturnlist().get(getReturnlist().size() - 1).getTotal()));
                        getDobj().setGrandTotalInwords(utility.ConvertNumberToWords(Integer.parseInt(getReturnlist().get(getReturnlist().size() - 1).getTotal())));
                    }
                }
            } else {
                printDailyDraftStmReport();
            }
        } catch (VahanException ve) {
            exception = ve;
        } catch (ParseException e) {
            exception = e;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        if (exception != null) {
            PrimeFaces.current().ajax().update("error_dialog_panel");
            PrimeFaces.current().executeScript("PF('error_dialog_ver').show();");
        }

    }

    public void getCancelReceiptList() throws VahanException {

        setCancelreceiptlist(PrinterDocDetails.getCancelReport(getSelusercd(), getFrDt1(), getToDt1(), statecd, offcd));

    }

    public void printDailyAccStmt() throws ParseException {
        try {
            dobj = new DailyAndConsolidatedStmtReportdobj();
            if (AccountStatementType.equalsIgnoreCase("D")) {
                setIs_transaction(true);
                setIs_total(true);
                setIs_penalty(true);
                setIs_feeAndtax(true);
                setIs_acchead(false);
                setIs_amount(false);
                this.instrument = this.ins_type;
                ArrayList<DailyAndConsolidatedStmtReportdobj> list = PrinterDocDetails.getDailyCashReport(getSelusercd(), getFrDt1(), getToDt1(), statecd, offcd);
                if (list.isEmpty()) {
                    setIs_surcharge(false);
                    setIs_interest(false);
                    setIs_amount1(false);
                    setIs_amount2(false);
                    setIspaymentShare(false);
                } else {
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
                    setCashier(dobj.getCashier());
                    setAccountant(dobj.getAccountant());
                    setSuperintendent(dobj.getSuperintendent());
                    setPatorto(dobj.getPatorto());
                    setRto(dobj.getRto());
                    setTotal(dobj.getTotal());
                }

                setReturnlist(list);
            } else if (AccountStatementType.equalsIgnoreCase("H")) {
                setConsolidatHeaderLabel("Account Head Wise Consolidated Statement");
                setIs_surcharge(false);
                setIs_interest(false);
                setIs_amount1(false);
                setIs_amount2(false);
                setIs_transaction(false);
                setIs_total(false);
                setIs_penalty(false);
                setIs_feeAndtax(false);
                setIs_acchead(true);
                setIs_amount(true);
                int grandTotal = 0;
                ArrayList<DailyAndConsolidatedStmtReportdobj> list = PrinterDocDetails.getHeadWiseConsolidatedStmReport(getSelusercd(), offcd, getFrDt1(), getToDt1(), statecd, tv_ntv_class_code);
                if (!list.isEmpty()) {
                    dobj = list.get(list.size() - 1);
                    if (dobj.isRenderClassType()) {
                        setRenderClassType(true);
                    } else {
                        setRenderClassType(false);
                    }
                    setCashier(dobj.getCashier());
                    setAccountant(dobj.getAccountant());
                    setSuperintendent(dobj.getSuperintendent());
                    setPatorto(dobj.getPatorto());
                    setRto(dobj.getRto());
                    grandTotal = Integer.valueOf(dobj.getAmount());
                }
                setConsolidateList(list);


            } else {
                setConsolidatHeaderLabel("Transaction Wise Consolidated Statement");
                setIs_transaction(true);
                setIs_total(true);
                setIs_penalty(true);
                setIs_feeAndtax(true);
                setIs_acchead(false);
                setIs_amount(false);
                ArrayList<DailyAndConsolidatedStmtReportdobj> list = PrinterDocDetails.getConsolidatedStmReport(getSelusercd(), offcd, getFrDt1(), getToDt1(), statecd, tv_ntv_class_code);
                if (list.isEmpty()) {
                    setIs_surcharge(false);
                    setIs_interest(false);
                    setIs_amount1(false);
                    setIs_amount2(false);
                    setIspaymentShare(false);
                } else {
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
                    if (dobj.isRenderClassType()) {
                        setRenderClassType(true);
                    } else {
                        setRenderClassType(false);
                    }
                    setCashier(dobj.getCashier());
                    setAccountant(dobj.getAccountant());
                    setSuperintendent(dobj.getSuperintendent());
                    setPatorto(dobj.getPatorto());
                    setRto(dobj.getRto());
                    if (dobj.isIspaymentShare()) {
                        setIspaymentShare(true);
                    }
                }
                setConsolidateList(list);

            }
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void printDailyDraftStmReport() {
        try {
            try {
                Utility utility = new Utility();
                ArrayList<DailyAndConsolidatedStmtReportdobj> list = PrinterDocDetails.getDailyDraftReport(getFrDt1(), getToDt1(), getSelusercd(), getSelusername(), offcd, offname, statecd, statename, instrument, bankcode);
                if (!list.isEmpty()) {
                    dobj = list.get(list.size() - 1);
                    setCashier(dobj.getCashier());
                    setAccountant(dobj.getAccountant());
                    setSuperintendent(dobj.getSuperintendent());
                    setPatorto(dobj.getPatorto());
                    setRto(dobj.getRto());
                    getDobj().setGrandTotalInwords(utility.ConvertNumberToWords(dobj.getGrandTotal()));
                }
                setDraftlist(list);
            } catch (NumberFormatException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);

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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the application_No
     */
    public String getApplication_No() {
        return application_No;
    }

    /**
     * @param application_No the application_No to set
     */
    public void setApplication_No(String application_No) {
        this.application_No = application_No;
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
     * @return the vehicle_Class
     */
    public String getVehicle_Class() {
        return vehicle_Class;
    }

    /**
     * @param vehicle_Class the vehicle_Class to set
     */
    public void setVehicle_Class(String vehicle_Class) {
        this.vehicle_Class = vehicle_Class;
    }

    /**
     * @return the transaction
     */
    public String getTransaction() {
        return transaction;
    }

    /**
     * @param transaction the transaction to set
     */
    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    /**
     * @return the receipt_No
     */
    public String getReceipt_No() {
        return receipt_No;
    }

    /**
     * @param receipt_No the receipt_No to set
     */
    public void setReceipt_No(String receipt_No) {
        this.receipt_No = receipt_No;
    }

    /**
     * @return the feeCM_Penalty_Tax
     */
    public String getFeeCM_Penalty_Tax() {
        return feeCM_Penalty_Tax;
    }

    /**
     * @param feeCM_Penalty_Tax the feeCM_Penalty_Tax to set
     */
    public void setFeeCM_Penalty_Tax(String feeCM_Penalty_Tax) {
        this.feeCM_Penalty_Tax = feeCM_Penalty_Tax;
    }

    /**
     * @return the total
     */
    public String getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(String total) {
        this.total = total;
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
     * @return the frDt
     */
    public Date getFrDt() {
        return frDt;
    }

    /**
     * @param frDt the frDt to set
     */
    public void setFrDt(Date frDt) {
        this.frDt = frDt;
    }

    /**
     * @return the toDt
     */
    public Date getToDt() {
        return toDt;
    }

    /**
     * @param toDt the toDt to set
     */
    public void setToDt(Date toDt) {
        this.toDt = toDt;
    }

    /**
     * @return the frdt_cal
     */
    public Calendar getFrdt_cal() {
        return frdt_cal;
    }

    /**
     * @param frdt_cal the frdt_cal to set
     */
    public void setFrdt_cal(Calendar frdt_cal) {
        this.frdt_cal = frdt_cal;
    }

    /**
     * @return the todt_cal
     */
    public Calendar getTodt_cal() {
        return todt_cal;
    }

    /**
     * @param todt_cal the todt_cal to set
     */
    public void setTodt_cal(Calendar todt_cal) {
        this.todt_cal = todt_cal;
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
     * @return the frDt1
     */
    public Date getFrDt1() {
        return fromDate;
    }

    /**
     * @param frDt1 the frDt1 to set
     */
    public void setFrDt1(Date frDt1) {
        this.fromDate = frDt1;
    }

    /**
     * @return the toDt1
     */
    public Date getToDt1() {
        return toDate;
    }

    /**
     * @param toDt1 the toDt1 to set
     */
    public void setToDt1(Date toDt1) {
        this.toDate = toDt1;
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
     * @return the cancelreceiptlist
     */
    public List<DailyAndConsolidatedStmtReportdobj> getCancelreceiptlist() {
        return cancelreceiptlist;
    }

    /**
     * @param cancelreceiptlist the cancelreceiptlist to set
     */
    public void setCancelreceiptlist(List<DailyAndConsolidatedStmtReportdobj> cancelreceiptlist) {
        this.cancelreceiptlist = cancelreceiptlist;
    }

    /**
     * @return the cancel_receipt_list
     */
    public String[] getCancel_receipt_list() {
        return cancel_receipt_list;
    }

    /**
     * @param cancel_receipt_list the cancel_receipt_list to set
     */
    public void setCancel_receipt_list(String[] cancel_receipt_list) {
        this.cancel_receipt_list = cancel_receipt_list;
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
     * @return the fromCashDate
     */
    /**
     * @return the toCashDate
     */
    /**
     * @return the fromDraftDate
     */
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
     * @return the instrument
     */
    public String getInstrument() {
        return instrument;
    }

    /**
     * @param instrument the instrument to set
     */
    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    /**
     * @return the bankcode
     */
    public String getBankcode() {
        return bankcode;
    }

    /**
     * @param bankcode the bankcode to set
     */
    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
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
     * @return the list_vm_instruement_type
     */
    public ArrayList getList_vm_instruement_type() {
        return list_vm_instruement_type;
    }

    /**
     * @param list_vm_instruement_type the list_vm_instruement_type to set
     */
    public void setList_vm_instruement_type(ArrayList list_vm_instruement_type) {
        this.list_vm_instruement_type = list_vm_instruement_type;
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
     * @return the consolidateList
     */
    public List<DailyAndConsolidatedStmtReportdobj> getConsolidateList() {
        return consolidateList;
    }

    /**
     * @param consolidateList the consolidateList to set
     */
    public void setConsolidateList(List<DailyAndConsolidatedStmtReportdobj> consolidateList) {
        this.consolidateList = consolidateList;
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
     * @return the grandTotalInWords
     */
    public String getGrandTotalInWords() {
        return grandTotalInWords;
    }

    /**
     * @param grandTotalInWords the grandTotalInWords to set
     */
    public void setGrandTotalInWords(String grandTotalInWords) {
        this.grandTotalInWords = grandTotalInWords;
    }

    /**
     * @return the cashier
     */
    public String getCashier() {
        return cashier;
    }

    /**
     * @param cashier the cashier to set
     */
    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    /**
     * @return the accountant
     */
    public String getAccountant() {
        return accountant;
    }

    /**
     * @param accountant the accountant to set
     */
    public void setAccountant(String accountant) {
        this.accountant = accountant;
    }

    /**
     * @return the superintendent
     */
    public String getSuperintendent() {
        return superintendent;
    }

    /**
     * @param superintendent the superintendent to set
     */
    public void setSuperintendent(String superintendent) {
        this.superintendent = superintendent;
    }

    /**
     * @return the patorto
     */
    public String getPatorto() {
        return patorto;
    }

    /**
     * @param patorto the patorto to set
     */
    public void setPatorto(String patorto) {
        this.patorto = patorto;
    }

    /**
     * @return the rto
     */
    public String getRto() {
        return rto;
    }

    /**
     * @param rto the rto to set
     */
    public void setRto(String rto) {
        this.rto = rto;
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
}
