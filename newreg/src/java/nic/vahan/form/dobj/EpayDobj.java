/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author tranC095
 */
public class EpayDobj implements Serializable {

    private int purCd;
    private String purCdDescr;
    private long e_TaxFee;
    private long act_TaxFee;
    private long e_FinePenalty;
    private long act_FinePenalty;
    private int e_service_charge;
    private int act_service_charge;
    private int e_pur_total;
    private int act_pur_total;
    private int e_grand_total;
    private int act_grand_total;
    private int e_cess;
    private int act_cess;
    private Date e_tax_fromDt;
    private Date act_tax_fromDt;
    private Date e_tax_toDate;
    private Date act_tax_date;
    private long e_total;
    private long act_total;
    private String appl_no;
    private String rcpt_no;
    private boolean taxExempted;
    private Date fromDate;
    private Date uptoDate;
    private String fromDateDesc;
    private String uptoDateDesc;
    private Date rcptDt;
    private List<EpayDobj> list = new ArrayList();
    private boolean verificationCheckBox = true;
    private Date dueDate;
    private String dueDateString;
    private String taxMode = null;
    private boolean taxInstallment;
    private FeeFineExemptionDobj feeFineExemptionDobj = null;

    /**
     * @return the purCd
     */
    public int getPurCd() {
        return purCd;
    }

    /**
     * @param purCd the purCd to set
     */
    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }

    /**
     * @return the purCdDescr
     */
    public String getPurCdDescr() {
        return purCdDescr;
    }

    /**
     * @param purCdDescr the purCdDescr to set
     */
    public void setPurCdDescr(String purCdDescr) {
        this.purCdDescr = purCdDescr;
    }

    /**
     * @return the e_TaxFee
     */
    public long getE_TaxFee() {
        return e_TaxFee;
    }

    /**
     * @param e_TaxFee the e_TaxFee to set
     */
    public void setE_TaxFee(long e_TaxFee) {
        this.e_TaxFee = e_TaxFee;
    }

    /**
     * @return the act_TaxFee
     */
    public long getAct_TaxFee() {
        return act_TaxFee;
    }

    /**
     * @param act_TaxFee the act_TaxFee to set
     */
    public void setAct_TaxFee(long act_TaxFee) {
        this.act_TaxFee = act_TaxFee;
    }

    /**
     * @return the e_FinePenalty
     */
    public long getE_FinePenalty() {
        return e_FinePenalty;
    }

    /**
     * @param e_FinePenalty the e_FinePenalty to set
     */
    public void setE_FinePenalty(long e_FinePenalty) {
        this.e_FinePenalty = e_FinePenalty;
    }

    /**
     * @return the act_FinePenalty
     */
    public long getAct_FinePenalty() {
        return act_FinePenalty;
    }

    /**
     * @param act_FinePenalty the act_FinePenalty to set
     */
    public void setAct_FinePenalty(long act_FinePenalty) {
        this.act_FinePenalty = act_FinePenalty;
    }

    /**
     * @return the e_service_charge
     */
    public int getE_service_charge() {
        return e_service_charge;
    }

    /**
     * @param e_service_charge the e_service_charge to set
     */
    public void setE_service_charge(int e_service_charge) {
        this.e_service_charge = e_service_charge;
    }

    /**
     * @return the act_service_charge
     */
    public int getAct_service_charge() {
        return act_service_charge;
    }

    /**
     * @param act_service_charge the act_service_charge to set
     */
    public void setAct_service_charge(int act_service_charge) {
        this.act_service_charge = act_service_charge;
    }

    /**
     * @return the e_pur_total
     */
    public int getE_pur_total() {
        return e_pur_total;
    }

    /**
     * @param e_pur_total the e_pur_total to set
     */
    public void setE_pur_total(int e_pur_total) {
        this.e_pur_total = e_pur_total;
    }

    /**
     * @return the act_pur_total
     */
    public int getAct_pur_total() {
        return act_pur_total;
    }

    /**
     * @param act_pur_total the act_pur_total to set
     */
    public void setAct_pur_total(int act_pur_total) {
        this.act_pur_total = act_pur_total;
    }

    /**
     * @return the e_grand_total
     */
    public int getE_grand_total() {
        return e_grand_total;
    }

    /**
     * @param e_grand_total the e_grand_total to set
     */
    public void setE_grand_total(int e_grand_total) {
        this.e_grand_total = e_grand_total;
    }

    /**
     * @return the act_grand_total
     */
    public int getAct_grand_total() {
        return act_grand_total;
    }

    /**
     * @param act_grand_total the act_grand_total to set
     */
    public void setAct_grand_total(int act_grand_total) {
        this.act_grand_total = act_grand_total;
    }

    /**
     * @return the e_cess
     */
    public int getE_cess() {
        return e_cess;
    }

    /**
     * @param e_cess the e_cess to set
     */
    public void setE_cess(int e_cess) {
        this.e_cess = e_cess;
    }

    /**
     * @return the act_cess
     */
    public int getAct_cess() {
        return act_cess;
    }

    /**
     * @param act_cess the act_cess to set
     */
    public void setAct_cess(int act_cess) {
        this.act_cess = act_cess;
    }

    /**
     * @return the list
     */
    public List<EpayDobj> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<EpayDobj> list) {
        this.list = list;
    }

    /**
     * @return the e_tax_fromDt
     */
    public Date getE_tax_fromDt() {
        return e_tax_fromDt;
    }

    /**
     * @param e_tax_fromDt the e_tax_fromDt to set
     */
    public void setE_tax_fromDt(Date e_tax_fromDt) {
        this.e_tax_fromDt = e_tax_fromDt;
    }

    /**
     * @return the act_tax_fromDt
     */
    public Date getAct_tax_fromDt() {
        return act_tax_fromDt;
    }

    /**
     * @param act_tax_fromDt the act_tax_fromDt to set
     */
    public void setAct_tax_fromDt(Date act_tax_fromDt) {
        this.act_tax_fromDt = act_tax_fromDt;
    }

    /**
     * @return the e_tax_toDate
     */
    public Date getE_tax_toDate() {
        return e_tax_toDate;
    }

    /**
     * @param e_tax_toDate the e_tax_toDate to set
     */
    public void setE_tax_toDate(Date e_tax_toDate) {
        this.e_tax_toDate = e_tax_toDate;
    }

    /**
     * @return the act_tax_date
     */
    public Date getAct_tax_date() {
        return act_tax_date;
    }

    /**
     * @param act_tax_date the act_tax_date to set
     */
    public void setAct_tax_date(Date act_tax_date) {
        this.act_tax_date = act_tax_date;
    }

    public long getE_total() {
        return e_total;
    }

    public void setE_total(long e_total) {
        this.e_total = e_total;
    }

    public long getAct_total() {
        return act_total;
    }

    public void setAct_total(long act_total) {
        this.act_total = act_total;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getRcpt_no() {
        return rcpt_no;
    }

    public void setRcpt_no(String rcpt_no) {
        this.rcpt_no = rcpt_no;
    }

    /**
     * @return the taxExempted
     */
    public boolean isTaxExempted() {
        return taxExempted;
    }

    /**
     * @param taxExempted the taxExempted to set
     */
    public void setTaxExempted(boolean taxExempted) {
        this.taxExempted = taxExempted;
    }

    @Override
    public int hashCode() {
        return purCd; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof EpayDobj)) {
            return false;
        }
        EpayDobj ep = (EpayDobj) obj;
        if (ep.getPurCd() == purCd) {
            return true;
        }
        return false;
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
     * @return the fromDateDesc
     */
    public String getFromDateDesc() {
        return fromDateDesc;
    }

    /**
     * @param fromDateDesc the fromDateDesc to set
     */
    public void setFromDateDesc(String fromDateDesc) {
        this.fromDateDesc = fromDateDesc;
    }

    /**
     * @return the uptoDateDesc
     */
    public String getUptoDateDesc() {
        return uptoDateDesc;
    }

    /**
     * @param uptoDateDesc the uptoDateDesc to set
     */
    public void setUptoDateDesc(String uptoDateDesc) {
        this.uptoDateDesc = uptoDateDesc;
    }

    /**
     * @return the rcptDt
     */
    public Date getRcptDt() {
        return rcptDt;
    }

    /**
     * @param rcptDt the rcptDt to set
     */
    public void setRcptDt(Date rcptDt) {
        this.rcptDt = rcptDt;
    }

    /**
     * @return the verificationCheckBox
     */
    public boolean isVerificationCheckBox() {
        return verificationCheckBox;
    }

    /**
     * @param verificationCheckBox the verificationCheckBox to set
     */
    public void setVerificationCheckBox(boolean verificationCheckBox) {
        this.verificationCheckBox = verificationCheckBox;
    }

    /**
     * @return the dueDate
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * @param dueDate the dueDate to set
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * @return the taxMode
     */
    public String getTaxMode() {
        return taxMode;
    }

    /**
     * @param taxMode the taxMode to set
     */
    public void setTaxMode(String taxMode) {
        this.taxMode = taxMode;
    }

    /**
     * @return the dueDateString
     */
    public String getDueDateString() {
        return dueDateString;
    }

    /**
     * @param dueDateString the dueDateString to set
     */
    public void setDueDateString(String dueDateString) {
        this.dueDateString = dueDateString;
    }

    /**
     * @return the taxInstallment
     */
    public boolean isTaxInstallment() {
        return taxInstallment;
    }

    /**
     * @param taxInstallment the taxInstallment to set
     */
    public void setTaxInstallment(boolean taxInstallment) {
        this.taxInstallment = taxInstallment;
    }

    /**
     * @return the feeFineExemptionDobj
     */
    public FeeFineExemptionDobj getFeeFineExemptionDobj() {
        return feeFineExemptionDobj;
    }

    /**
     * @param feeFineExemptionDobj the feeFineExemptionDobj to set
     */
    public void setFeeFineExemptionDobj(FeeFineExemptionDobj feeFineExemptionDobj) {
        this.feeFineExemptionDobj = feeFineExemptionDobj;
    }
}
