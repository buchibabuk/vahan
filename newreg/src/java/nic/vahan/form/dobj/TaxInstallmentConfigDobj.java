/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author tranC106
 */
public class TaxInstallmentConfigDobj implements Cloneable, Serializable {

    private String state_cd;
    private int off_cd;
    private String regnno;
    private String appl_no;
    private String filerefno;
    private String orderissueby;
    private String orderno;
    private Date orderdate;
    private Date taxfromdt;
    private Date taxuptodt;
    private String serialno = "1";
    private String serialnotable;
    private String taxmode;
    private Long taxamountinstl;
    private Long taxamountinstltable;
    private Date payduedt;
    private String dueDateStr;
    private String paymentmode;
    private String receiptno;
    private Date receiptdt;
    private String deleteflag;
    private Long draftcd;
    private Long totalSuminstallment;

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public int getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    public String getRegnno() {
        return regnno;
    }

    public void setRegnno(String regnno) {
        this.regnno = regnno;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getFilerefno() {
        return filerefno;
    }

    public void setFilerefno(String filerefno) {
        this.filerefno = filerefno;
    }

    public String getOrderissueby() {
        return orderissueby;
    }

    public void setOrderissueby(String orderissueby) {
        this.orderissueby = orderissueby;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public Date getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(Date orderdate) {
        this.orderdate = orderdate;
    }

    public Date getTaxfromdt() {
        return taxfromdt;
    }

    public void setTaxfromdt(Date taxfromdt) {
        this.taxfromdt = taxfromdt;
    }

    public Date getTaxuptodt() {
        return taxuptodt;
    }

    public void setTaxuptodt(Date taxuptodt) {
        this.taxuptodt = taxuptodt;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public String getTaxmode() {
        return taxmode;
    }

    public void setTaxmode(String taxmode) {
        this.taxmode = taxmode;
    }

    public Date getPayduedt() {
        return payduedt;
    }

    public void setPayduedt(Date payduedt) {
        this.payduedt = payduedt;
    }

    public String getPaymentmode() {
        return paymentmode;
    }

    public void setPaymentmode(String paymentmode) {
        this.paymentmode = paymentmode;
    }

    public String getReceiptno() {
        return receiptno;
    }

    public void setReceiptno(String receiptno) {
        this.receiptno = receiptno;
    }

    public Date getReceiptdt() {
        return receiptdt;
    }

    public void setReceiptdt(Date receiptdt) {
        this.receiptdt = receiptdt;
    }

    public String getDeleteflag() {
        return deleteflag;
    }

    public void setDeleteflag(String deleteflag) {
        this.deleteflag = deleteflag;
    }

    public Long getDraftcd() {
        return draftcd;
    }

    public void setDraftcd(Long draftcd) {
        this.draftcd = draftcd;
    }

    public Long getTaxamountinstl() {
        return taxamountinstl;
    }

    public void setTaxamountinstl(Long taxamountinstl) {
        this.taxamountinstl = taxamountinstl;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Long getTotalSuminstallment() {
        return totalSuminstallment;
    }

    public void setTotalSuminstallment(Long totalSuminstallment) {
        this.totalSuminstallment = totalSuminstallment;
    }

    public String getDueDateStr() {
        return dueDateStr;
    }

    public void setDueDateStr(String dueDateStr) {
        this.dueDateStr = dueDateStr;
    }

    public String getSerialnotable() {
        return serialnotable;
    }

    public void setSerialnotable(String serialnotable) {
        this.serialnotable = serialnotable;
    }

    public Long getTaxamountinstltable() {
        return taxamountinstltable;
    }

    public void setTaxamountinstltable(Long taxamountinstltable) {
        this.taxamountinstltable = taxamountinstltable;
    }
}
