/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;
import org.primefaces.component.calendar.Calendar;

/**
 *
 * @author ankur
 */
public class TaxInstallCollectionDobj implements Serializable {

    private String regnNo;
    private String serialno;
    private int tablesrno;
    private String serialnofinal;
    private Long taxAmountInst;
    private int taxAmountInst1;
    private int taxAmountInstfinal;
    private String rcptNo;
    private String paydueDate;
    private String paydueDatefinal;
    private String fileRefNo;
    private String orderIssBy;
    private String orderNo;
    private String orderDate;
    private String regnNoPaid;
    private String serialnoPaid;
    private int taxAmountInstPaid;
    private String paydueDatePaid;
    private boolean select;
    private boolean disablecheckbox;
    private Long penalty=0L;
    private int penaltyfinal;
    private String taxMode;
    private String uptodate;
    private Date payDate;
    private String appl_no;
    private String tax_From;

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public String getFileRefNo() {
        return fileRefNo;
    }

    public void setFileRefNo(String fileRefNo) {
        this.fileRefNo = fileRefNo;
    }

    public String getOrderIssBy() {
        return orderIssBy;
    }

    public void setOrderIssBy(String orderIssBy) {
        this.orderIssBy = orderIssBy;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public String getPaydueDate() {
        return paydueDate;
    }

    public void setPaydueDate(String paydueDate) {
        this.paydueDate = paydueDate;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getRcptNo() {
        return rcptNo;
    }

    public void setRcptNo(String rcptNo) {
        this.rcptNo = rcptNo;
    }

    public String getRegnNoPaid() {
        return regnNoPaid;
    }

    public void setRegnNoPaid(String regnNoPaid) {
        this.regnNoPaid = regnNoPaid;
    }

    public String getSerialnoPaid() {
        return serialnoPaid;
    }

    public void setSerialnoPaid(String serialnoPaid) {
        this.serialnoPaid = serialnoPaid;
    }

    public String getPaydueDatePaid() {
        return paydueDatePaid;
    }

    public void setPaydueDatePaid(String paydueDatePaid) {
        this.paydueDatePaid = paydueDatePaid;
    }

    public int getTaxAmountInstPaid() {
        return taxAmountInstPaid;
    }

    public void setTaxAmountInstPaid(int taxAmountInstPaid) {
        this.taxAmountInstPaid = taxAmountInstPaid;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
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
     * @return the serialnofinal
     */
    public String getSerialnofinal() {
        return serialnofinal;
    }

    /**
     * @param serialnofinal the serialnofinal to set
     */
    public void setSerialnofinal(String serialnofinal) {
        this.serialnofinal = serialnofinal;
    }

    /**
     * @return the taxAmountInstfinal
     */
    public int getTaxAmountInstfinal() {
        return taxAmountInstfinal;
    }

    /**
     * @param taxAmountInstfinal the taxAmountInstfinal to set
     */
    public void setTaxAmountInstfinal(int taxAmountInstfinal) {
        this.taxAmountInstfinal = taxAmountInstfinal;
    }

    /**
     * @return the paydueDatefinal
     */
    public String getPaydueDatefinal() {
        return paydueDatefinal;
    }

    /**
     * @param paydueDatefinal the paydueDatefinal to set
     */
    public void setPaydueDatefinal(String paydueDatefinal) {
        this.paydueDatefinal = paydueDatefinal;
    }

    /**
     * @return the penaltyfinal
     */
    public int getPenaltyfinal() {
        return penaltyfinal;
    }

    /**
     * @param penaltyfinal the penaltyfinal to set
     */
    public void setPenaltyfinal(int penaltyfinal) {
        this.penaltyfinal = penaltyfinal;
    }

    public boolean isDisablecheckbox() {
        return disablecheckbox;
    }

    public void setDisablecheckbox(boolean disablecheckbox) {
        this.disablecheckbox = disablecheckbox;
    }

    public int getTaxAmountInst1() {
        return taxAmountInst1;
    }

    public void setTaxAmountInst1(int taxAmountInst1) {
        this.taxAmountInst1 = taxAmountInst1;
    }

    public int getTablesrno() {
        return tablesrno;
    }

    public void setTablesrno(int tablesrno) {
        this.tablesrno = tablesrno;
    }

    /**
     * @return the penalty
     */
    public Long getPenalty() {
        return penalty;
    }

    /**
     * @param penalty the penalty to set
     */
    public void setPenalty(Long penalty) {
        this.penalty = penalty;
    }

    /**
     * @return the taxAmountInst
     */
    public Long getTaxAmountInst() {
        return taxAmountInst;
    }

    /**
     * @param taxAmountInst the taxAmountInst to set
     */
    public void setTaxAmountInst(Long taxAmountInst) {
        this.taxAmountInst = taxAmountInst;
    }

    /**
     * @return the uptodate
     */
    public String getUptodate() {
        return uptodate;
    }

    /**
     * @param uptodate the uptodate to set
     */
    public void setUptodate(String uptodate) {
        this.uptodate = uptodate;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getTax_From() {
        return tax_From;
    }

    public void setTax_From(String tax_From) {
        this.tax_From = tax_From;
    }
}
