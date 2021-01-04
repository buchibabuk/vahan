/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.services.PosDobj;

/**
 *
 * @author tranC105
 */
public class PaymentCollectionDobj implements Serializable {

    private String number;
    private Date dated;
    private String amount;
    private String bank_name;
    private String branch;
    private String instrument;
    private List bank_list = new ArrayList();
    private String validationMessage = "";
    private boolean disableBankList = false;
    private boolean disableBranch = false;
    private boolean showVerifyBtn = false;
    private Date max_draft_date = null;
    private Date min_draft_date = null;
    private boolean disableInstrument = false;
    private boolean disableNumber = false;
    private boolean disableDate = false;
    private boolean disableAmt = false;
    private String eGrassStatus = "";
    private boolean showEgrassStatus = false;
    private boolean eGrassVerified = false;
    private boolean showMinusBtn = true;
    private boolean rowflag;
    private String instrumentDesc;
    private String bankNameDesc;
    private String dateInFormat;
    private boolean renderDraftInstmnt = true;
    private boolean renderChallanInstmnt = false;
    private boolean renderPOSInstmnt = false;
    private PosDobj posDobj;
    private String paymentVerificationMessage = "";

    @Override
    public String toString() {
        return "PaymentCollection_dobj{" + "number=" + number + ", dated=" + dated + ", amount=" + amount + ", bank_name=" + bank_name + ", branch=" + branch + ", validationMessage=" + validationMessage + '}';
    }

    public boolean validateDobj() {

        if (Utility.isNullOrBlank(number)) {
            setValidationMessage("Invalid Number Entered. Please Check");
            return false;
        }

        if (dated == null) {
            setValidationMessage("Please Enter Draft Date.");
            return false;
        } else if (dated.compareTo(new Date()) > 0) {
            setValidationMessage("Instrument date cannot Be greater than current date");
            return false;
        }
        if (Utility.isZeroOrNot(amount)) {
            setValidationMessage("Amount can not be less than zero");
            return false;
        }
        if (!Utility.isNullOrBlank(amount)) {
            try {
                Integer.parseInt(amount);
            } catch (NumberFormatException nfe) {
                setValidationMessage("Please Enter Proper Amount.");
                return false;
            }
        } else {
            setValidationMessage("Amount can not be left blank");
            return false;
        }

        if (Utility.isNullOrBlank(bank_name) || ("-1".equalsIgnoreCase(bank_name))) {
            setValidationMessage("Select Valid Bank");
            return false;
        }

        if (Utility.isNullOrBlank(branch)) {
            setValidationMessage("Enter Branch Name");
            return false;
        }


        return true;
    }

    public PaymentCollectionDobj() {
    }

    public PaymentCollectionDobj(String instrumentNo, String bank_name) {
        this.bank_name = bank_name;
        this.number = instrumentNo;
    }

    @Override
    public int hashCode() {
        String hash = this.number + ":" + this.bank_name;
        return hash.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof PaymentCollectionDobj)) {
            return false;
        }
        PaymentCollectionDobj ob = (PaymentCollectionDobj) obj;
        if (ob.bank_name != null && ob.number != null && !ob.number.concat(ob.bank_name).equals(this.number.concat(this.bank_name))) {
            return false;
        }
        return true;
    }

    public PaymentCollectionDobj(String number, Date dated, String amount, String bank_name, String branch, String instrument) {
        this.number = number;
        this.dated = dated;
        this.amount = amount;
        this.bank_name = bank_name;
        this.branch = branch;
        this.instrument = instrument;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDated() {
        return dated;
    }

    public void setDated(Date dated) {
        this.dated = dated;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    /**
     * @return the bank_list
     */
    public List getBank_list() {
        return bank_list;
    }

    /**
     * @param bank_list the bank_list to set
     */
    public void setBank_list(List bank_list) {
        this.bank_list = bank_list;
    }

    /**
     * @return the disableBankList
     */
    public boolean isDisableBankList() {
        return disableBankList;
    }

    /**
     * @param disableBankList the disableBankList to set
     */
    public void setDisableBankList(boolean disableBankList) {
        this.disableBankList = disableBankList;
    }

    /**
     * @return the disableBranch
     */
    public boolean isDisableBranch() {
        return disableBranch;
    }

    /**
     * @param disableBranch the disableBranch to set
     */
    public void setDisableBranch(boolean disableBranch) {
        this.disableBranch = disableBranch;
    }

    /**
     * @return the showVerifyBtn
     */
    public boolean isShowVerifyBtn() {
        return showVerifyBtn;
    }

    /**
     * @param showVerifyBtn the showVerifyBtn to set
     */
    public void setShowVerifyBtn(boolean showVerifyBtn) {
        this.showVerifyBtn = showVerifyBtn;
    }

    /**
     * @return the max_draft_date
     */
    public Date getMax_draft_date() {
        return max_draft_date;
    }

    /**
     * @param max_draft_date the max_draft_date to set
     */
    public void setMax_draft_date(Date max_draft_date) {
        this.max_draft_date = max_draft_date;
    }

    /**
     * @return the min_draft_date
     */
    public Date getMin_draft_date() {
        return min_draft_date;
    }

    /**
     * @param min_draft_date the min_draft_date to set
     */
    public void setMin_draft_date(Date min_draft_date) {
        this.min_draft_date = min_draft_date;
    }

    /**
     * @return the disableInstrument
     */
    public boolean isDisableInstrument() {
        return disableInstrument;
    }

    /**
     * @param disableInstrument the disableInstrument to set
     */
    public void setDisableInstrument(boolean disableInstrument) {
        this.disableInstrument = disableInstrument;
    }

    /**
     * @return the disableNumber
     */
    public boolean isDisableNumber() {
        return disableNumber;
    }

    /**
     * @param disableNumber the disableNumber to set
     */
    public void setDisableNumber(boolean disableNumber) {
        this.disableNumber = disableNumber;
    }

    /**
     * @return the disableDate
     */
    public boolean isDisableDate() {
        return disableDate;
    }

    /**
     * @param disableDate the disableDate to set
     */
    public void setDisableDate(boolean disableDate) {
        this.disableDate = disableDate;
    }

    /**
     * @return the disableAmt
     */
    public boolean isDisableAmt() {
        return disableAmt;
    }

    /**
     * @param disableAmt the disableAmt to set
     */
    public void setDisableAmt(boolean disableAmt) {
        this.disableAmt = disableAmt;
    }

    /**
     * @return the eGrassStatus
     */
    public String geteGrassStatus() {
        return eGrassStatus;
    }

    /**
     * @param eGrassStatus the eGrassStatus to set
     */
    public void seteGrassStatus(String eGrassStatus) {
        this.eGrassStatus = eGrassStatus;
    }

    /**
     * @return the showEgrassStatus
     */
    public boolean isShowEgrassStatus() {
        return showEgrassStatus;
    }

    /**
     * @param showEgrassStatus the showEgrassStatus to set
     */
    public void setShowEgrassStatus(boolean showEgrassStatus) {
        this.showEgrassStatus = showEgrassStatus;
    }

    /**
     * @return the eGrassVerified
     */
    public boolean iseGrassVerified() {
        return eGrassVerified;
    }

    /**
     * @param eGrassVerified the eGrassVerified to set
     */
    public void seteGrassVerified(boolean eGrassVerified) {
        this.eGrassVerified = eGrassVerified;
    }

    /**
     * @return the showMinusBtn
     */
    public boolean isShowMinusBtn() {
        return showMinusBtn;
    }

    /**
     * @param showMinusBtn the showMinusBtn to set
     */
    public void setShowMinusBtn(boolean showMinusBtn) {
        this.showMinusBtn = showMinusBtn;
    }

    /**
     * @return the rowflag
     */
    public boolean isRowflag() {
        return rowflag;
    }

    /**
     * @param rowflag the rowflag to set
     */
    public void setRowflag(boolean rowflag) {
        this.rowflag = rowflag;
    }

    /**
     * @return the instrumentDesc
     */
    public String getInstrumentDesc() {
        return instrumentDesc;
    }

    /**
     * @param instrumentDesc the instrumentDesc to set
     */
    public void setInstrumentDesc(String instrumentDesc) {
        this.instrumentDesc = instrumentDesc;
    }

    /**
     * @return the bankNameDesc
     */
    public String getBankNameDesc() {
        return bankNameDesc;
    }

    /**
     * @param bankNameDesc the bankNameDesc to set
     */
    public void setBankNameDesc(String bankNameDesc) {
        this.bankNameDesc = bankNameDesc;
    }

    /**
     * @return the dateInFormat
     */
    public String getDateInFormat() {
        return dateInFormat;
    }

    /**
     * @param dateInFormat the dateInFormat to set
     */
    public void setDateInFormat(String dateInFormat) {
        this.dateInFormat = dateInFormat;
    }

    /**
     * @return the renderDraftInstmnt
     */
    public boolean isRenderDraftInstmnt() {
        return renderDraftInstmnt;
    }

    /**
     * @param renderDraftInstmnt the renderDraftInstmnt to set
     */
    public void setRenderDraftInstmnt(boolean renderDraftInstmnt) {
        this.renderDraftInstmnt = renderDraftInstmnt;
    }

    /**
     * @return the renderChallanInstmnt
     */
    public boolean isRenderChallanInstmnt() {
        return renderChallanInstmnt;
    }

    /**
     * @param renderChallanInstmnt the renderChallanInstmnt to set
     */
    public void setRenderChallanInstmnt(boolean renderChallanInstmnt) {
        this.renderChallanInstmnt = renderChallanInstmnt;
    }

    /**
     * @return the renderPOSInstmnt
     */
    public boolean isRenderPOSInstmnt() {
        return renderPOSInstmnt;
    }

    /**
     * @param renderPOSInstmnt the renderPOSInstmnt to set
     */
    public void setRenderPOSInstmnt(boolean renderPOSInstmnt) {
        this.renderPOSInstmnt = renderPOSInstmnt;
    }

    /**
     * @return the posDobj
     */
    public PosDobj getPosDobj() {
        return posDobj;
    }

    /**
     * @param posDobj the posDobj to set
     */
    public void setPosDobj(PosDobj posDobj) {
        this.posDobj = posDobj;
    }

    /**
     * @return the paymentVerificationMessage
     */
    public String getPaymentVerificationMessage() {
        return paymentVerificationMessage;
    }

    /**
     * @param paymentVerificationMessage the paymentVerificationMessage to set
     */
    public void setPaymentVerificationMessage(String paymentVerificationMessage) {
        this.paymentVerificationMessage = paymentVerificationMessage;
    }

}
