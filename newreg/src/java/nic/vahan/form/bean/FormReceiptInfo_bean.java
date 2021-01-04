/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author tranC105
 */
@ManagedBean(name="receiptInfo")
@ViewScoped
public class FormReceiptInfo_bean implements Serializable{
    
    private List<CancelReceiptPanel_dobj> receiptInfoList = new ArrayList<>();
    private String totalFee;
    private String totalFine;
    private String grandTotal;
    private boolean enableSelect = true;

    public FormReceiptInfo_bean() {
       
    }
    @PostConstruct
    public void postConstruct(){
       // receiptInfoList = new ArrayList<>();
         getReceiptInfoList().add(new CancelReceiptPanel_dobj());
    }
    public void calulateTotal(){
        setTotalFee("1000");
    }

    public List<CancelReceiptPanel_dobj> getReceiptInfoList() {
        return receiptInfoList;
    }

    public void setReceiptInfoList(List<CancelReceiptPanel_dobj> receiptInfoList) {
        this.receiptInfoList = receiptInfoList;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getTotalFine() {
        return totalFine;
    }

    public void setTotalFine(String totalFine) {
        this.totalFine = totalFine;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public boolean isEnableSelect() {
        return enableSelect;
    }

    public void setEnableSelect(boolean enableSelect) {
        this.enableSelect = enableSelect;
    }
    
    
    
    
    
}
