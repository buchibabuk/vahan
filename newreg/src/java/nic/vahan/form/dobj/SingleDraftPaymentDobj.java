/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.List;
import nic.vahan.form.bean.TaxFormPanelBean;

/**
 *
 * @author tranC075
 */
public class SingleDraftPaymentDobj implements Serializable {

    private String regn_no;
    private float amount;
    private String receiptNo;
    private List<TaxFormPanelBean> listTaxFormCart;
    private long inscd = 0;
    private List<SingleDraftPaymentDobj> listSinglePaymentDraftDobj;
    private List<PaymentCollectionDobj> paymentlist;

    public SingleDraftPaymentDobj() {
    }

    public SingleDraftPaymentDobj(String regn_no) {
        this.regn_no = regn_no;
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
     * @return the amount
     */
    public float getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }

    /**
     * @return the listTaxFormCart
     */
    public List<TaxFormPanelBean> getListTaxFormCart() {
        return listTaxFormCart;
    }

    /**
     * @param listTaxFormCart the listTaxFormCart to set
     */
    public void setListTaxFormCart(List<TaxFormPanelBean> listTaxFormCart) {
        this.listTaxFormCart = listTaxFormCart;
    }

    /**
     * @return the receiptNo
     */
    public String getReceiptNo() {
        return receiptNo;
    }

    /**
     * @param receiptNo the receiptNo to set
     */
    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    /**
     * @return the inscd
     */
    public long getInscd() {
        return inscd;
    }

    /**
     * @param inscd the inscd to set
     */
    public void setInscd(long inscd) {
        this.inscd = inscd;
    }

    /**
     * @return the listSinglePaymentDraftDobj
     */
    public List<SingleDraftPaymentDobj> getListSinglePaymentDraftDobj() {
        return listSinglePaymentDraftDobj;
    }

    /**
     * @param listSinglePaymentDraftDobj the listSinglePaymentDraftDobj to set
     */
    public void setListSinglePaymentDraftDobj(List<SingleDraftPaymentDobj> listSinglePaymentDraftDobj) {
        this.listSinglePaymentDraftDobj = listSinglePaymentDraftDobj;
    }

    /**
     * @return the paymentlist
     */
    public List<PaymentCollectionDobj> getPaymentlist() {
        return paymentlist;
    }

    /**
     * @param paymentlist the paymentlist to set
     */
    public void setPaymentlist(List<PaymentCollectionDobj> paymentlist) {
        this.paymentlist = paymentlist;
    }
}
