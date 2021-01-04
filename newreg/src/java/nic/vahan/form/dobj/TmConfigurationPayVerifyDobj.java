/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

public class TmConfigurationPayVerifyDobj implements Serializable {

    private String stateCd;
    private boolean eGrassVerify;
    private boolean bankReceiptVerify;

    /**
     * @return the eGrassVerify
     */
    public boolean iseGrassVerify() {
        return eGrassVerify;
    }

    /**
     * @param eGrassVerify the eGrassVerify to set
     */
    public void seteGrassVerify(boolean eGrassVerify) {
        this.eGrassVerify = eGrassVerify;
    }

    /**
     * @return the bankReceiptVerify
     */
    public boolean isBankReceiptVerify() {
        return bankReceiptVerify;
    }

    /**
     * @param bankReceiptVerify the bankReceiptVerify to set
     */
    public void setBankReceiptVerify(boolean bankReceiptVerify) {
        this.bankReceiptVerify = bankReceiptVerify;
    }

    /**
     * @return the stateCd
     */
    public String getStateCd() {
        return stateCd;
    }

    /**
     * @param stateCd the stateCd to set
     */
    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }
}
