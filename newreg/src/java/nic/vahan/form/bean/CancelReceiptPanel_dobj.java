/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;

/**
 *
 * @author tranC105
 */
public class CancelReceiptPanel_dobj implements Serializable {

    private String descr;
    private String fee;
    private String fine;
    private String status;
    private boolean isChecked;
    private String purCd;

    public CancelReceiptPanel_dobj() {
    }

    public CancelReceiptPanel_dobj(String descr, String fee, String fine, String status, boolean isChecked, String purCd) {
        this.descr = descr;
        this.fee = fee;
        this.fine = fine;
        this.status = status;
        this.isChecked = isChecked;
        this.purCd = purCd;
    }

    @Override
    public String toString() {
        return "CancelReceiptPanel_dobj{" + "descr=" + descr + ", fee=" + fee + ", fine=" + fine + ", status=" + status + ", isChecked=" + isChecked + '}';
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getFine() {
        return fine;
    }

    public void setFine(String fine) {
        this.fine = fine;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getPurCd() {
        return purCd;
    }

    public void setPurCd(String purCd) {
        this.purCd = purCd;
    }
}
