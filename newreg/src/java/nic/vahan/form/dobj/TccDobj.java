/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author acer
 */
public class TccDobj implements Serializable {

    private String amount = "";
    private String tcrNo = "";
    private String applNo = "";
    private String printDt = "";

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
     * @return the tcrNo
     */
    public String getTcrNo() {
        return tcrNo;
    }

    /**
     * @param tcrNo the tcrNo to set
     */
    public void setTcrNo(String tcrNo) {
        this.tcrNo = tcrNo;
    }

    /**
     * @return the applNo
     */
    public String getApplNo() {
        return applNo;
    }

    /**
     * @param applNo the applNo to set
     */
    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    /**
     * @return the printDt
     */
    public String getPrintDt() {
        return printDt;
    }

    /**
     * @param printDt the printDt to set
     */
    public void setPrintDt(String printDt) {
        this.printDt = printDt;
    }
}
