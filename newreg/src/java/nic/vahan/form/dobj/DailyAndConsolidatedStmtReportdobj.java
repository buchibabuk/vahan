/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author nprpE081
 */
public class DailyAndConsolidatedStmtReportdobj implements Serializable {

    private String name;
    private String application_No;
    private String regn_no;
    private String vehicle_Class;
    private String transaction;
    private String receipt_No;
    private String feeCM_Penalty_Tax;
    private String total = null;
    private String[] cancel_receipt_list;
    private String cancel_receipt;
    private String fromCashDate;
    private String toCashDate;
    private String instrument;
    private String bankcode;
    private String fromDraftDate;
    private String toDraftDate;
    private String received_dt;
    private String bankName;
    private String branchName;
    private String amount;
    private int grandTotal;
    private String grandTotalInwords;
    private String instrumentDate;
    private String instrument_no;
    private String surcharge;
    private String interest;
    private boolean is_surcharge;
    private boolean is_interest;
    private boolean is_penalty;
    private String AccHead;
    private int srindex;
    private List<DailyAndConsolidatedStmtReportdobj> dddetailsList;
    private String instrumentDeac;
    private String instrumentAmt;
    private String reportHeader;
    private String amount1;
    private String amount2;
    private boolean is_amount1;
    private boolean is_amount2;
    private String cashier;
    private String accountant;
    private String superintendent;
    private String patorto;
    private String rto;
    private Float venderShare;
    private Float departmentShare;
    private boolean ispaymentShare = false;
    private String amountVendor;
    private String class_type;
    private boolean renderClassType = true;

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
    public String getFromCashDate() {
        return fromCashDate;
    }

    /**
     * @param fromCashDate the fromCashDate to set
     */
    public void setFromCashDate(String fromCashDate) {
        this.fromCashDate = fromCashDate;
    }

    /**
     * @return the toCashDate
     */
    public String getToCashDate() {
        return toCashDate;
    }

    /**
     * @param toCashDate the toCashDate to set
     */
    public void setToCashDate(String toCashDate) {
        this.toCashDate = toCashDate;
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
     * @return the fromDraftDate
     */
    public String getFromDraftDate() {
        return fromDraftDate;
    }

    /**
     * @param fromDraftDate the fromDraftDate to set
     */
    public void setFromDraftDate(String fromDraftDate) {
        this.fromDraftDate = fromDraftDate;
    }

    /**
     * @return the toDraftDate
     */
    public String getToDraftDate() {
        return toDraftDate;
    }

    /**
     * @param toDraftDate the toDraftDate to set
     */
    public void setToDraftDate(String toDraftDate) {
        this.toDraftDate = toDraftDate;
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
     * @return the grandTotalInwords
     */
    public String getGrandTotalInwords() {
        return grandTotalInwords;
    }

    /**
     * @param grandTotalInwords the grandTotalInwords to set
     */
    public void setGrandTotalInwords(String grandTotalInwords) {
        this.grandTotalInwords = grandTotalInwords;
    }

    /**
     * @return the surcharge
     */
    public String getSurcharge() {
        return surcharge;
    }

    /**
     * @param surcharge the surcharge to set
     */
    public void setSurcharge(String surcharge) {
        this.surcharge = surcharge;
    }

    /**
     * @return the interest
     */
    public String getInterest() {
        return interest;
    }

    /**
     * @param interest the interest to set
     */
    public void setInterest(String interest) {
        this.interest = interest;
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
     * @return the AccHead
     */
    public String getAccHead() {
        return AccHead;
    }

    /**
     * @param AccHead the AccHead to set
     */
    public void setAccHead(String AccHead) {
        this.AccHead = AccHead;
    }

    public int getSrindex() {
        return srindex;
    }

    public void setSrindex(int srindex) {
        this.srindex = srindex;
    }

    /**
     * @return the instrumentDeac
     */
    public String getInstrumentDeac() {
        return instrumentDeac;
    }

    /**
     * @param instrumentDeac the instrumentDeac to set
     */
    public void setInstrumentDeac(String instrumentDeac) {
        this.instrumentDeac = instrumentDeac;
    }

    /**
     * @return the dddetailsList
     */
    public List<DailyAndConsolidatedStmtReportdobj> getDddetailsList() {
        return dddetailsList;
    }

    /**
     * @param dddetailsList the dddetailsList to set
     */
    public void setDddetailsList(List<DailyAndConsolidatedStmtReportdobj> dddetailsList) {
        this.dddetailsList = dddetailsList;
    }

    /**
     * @return the instrumentAmt
     */
    public String getInstrumentAmt() {
        return instrumentAmt;
    }

    /**
     * @param instrumentAmt the instrumentAmt to set
     */
    public void setInstrumentAmt(String instrumentAmt) {
        this.instrumentAmt = instrumentAmt;
    }

    /**
     * @return the reportHeader
     */
    public String getReportHeader() {
        return reportHeader;
    }

    /**
     * @param reportHeader the reportHeader to set
     */
    public void setReportHeader(String reportHeader) {
        this.reportHeader = reportHeader;
    }

    /**
     * @return the amount1
     */
    public String getAmount1() {
        return amount1;
    }

    /**
     * @param amount1 the amount1 to set
     */
    public void setAmount1(String amount1) {
        this.amount1 = amount1;
    }

    /**
     * @return the amount2
     */
    public String getAmount2() {
        return amount2;
    }

    /**
     * @param amount2 the amount2 to set
     */
    public void setAmount2(String amount2) {
        this.amount2 = amount2;
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
     * @return the departmentShare
     */
    public Float getDepartmentShare() {
        return departmentShare;
    }

    /**
     * @param departmentShare the departmentShare to set
     */
    public void setDepartmentShare(Float departmentShare) {
        this.departmentShare = departmentShare;
    }

    /**
     * @return the venderShare
     */
    public Float getVenderShare() {
        return venderShare;
    }

    /**
     * @param venderShare the venderShare to set
     */
    public void setVenderShare(Float venderShare) {
        this.venderShare = venderShare;
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
     * @return the amountVendor
     */
    public String getAmountVendor() {
        return amountVendor;
    }

    /**
     * @param amountVendor the amountVendor to set
     */
    public void setAmountVendor(String amountVendor) {
        this.amountVendor = amountVendor;
    }

    /**
     * @return the class_type
     */
    public String getClass_type() {
        return class_type;
    }

    /**
     * @param class_type the class_type to set
     */
    public void setClass_type(String class_type) {
        this.class_type = class_type;
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
