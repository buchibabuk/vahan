/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author acer
 */
public class QrCodeDobj implements Serializable {

    private String stateCD;
    private int offCD;
    private String applNO;
    private String regnNO;
    private String qrhash;
    private String formName;
    private String receiptNo;
    private String chasiNo;

    /**
     * @return the stateCD
     */
    public String getStateCD() {
        return stateCD;
    }

    /**
     * @param stateCD the stateCD to set
     */
    public void setStateCD(String stateCD) {
        this.stateCD = stateCD;
    }

    /**
     * @return the offCD
     */
    public int getOffCD() {
        return offCD;
    }

    /**
     * @param offCD the offCD to set
     */
    public void setOffCD(int offCD) {
        this.offCD = offCD;
    }

    /**
     * @return the applNO
     */
    public String getApplNO() {
        return applNO;
    }

    /**
     * @param applNO the applNO to set
     */
    public void setApplNO(String applNO) {
        this.applNO = applNO;
    }

    /**
     * @return the regnNO
     */
    public String getRegnNO() {
        return regnNO;
    }

    /**
     * @param regnNO the regnNO to set
     */
    public void setRegnNO(String regnNO) {
        this.regnNO = regnNO;
    }

    /**
     * @return the qrhash
     */
    public String getQrhash() {
        return qrhash;
    }

    /**
     * @param qrhash the qrhash to set
     */
    public void setQrhash(String qrhash) {
        this.qrhash = qrhash;
    }

    /**
     * @return the formName
     */
    public String getFormName() {
        return formName;
    }

    /**
     * @param formName the formName to set
     */
    public void setFormName(String formName) {
        this.formName = formName;
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
     * @return the chasiNo
     */
    public String getChasiNo() {
        return chasiNo;
    }

    /**
     * @param chasiNo the chasiNo to set
     */
    public void setChasiNo(String chasiNo) {
        this.chasiNo = chasiNo;
    }
}
