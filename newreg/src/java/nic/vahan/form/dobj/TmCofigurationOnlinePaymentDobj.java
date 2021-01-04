/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author Divya Kamboj
 */
public class TmCofigurationOnlinePaymentDobj implements Serializable {

    private String state_cd = "";
    private boolean tax_collection = false;
    private boolean tax_installment = false;

    /**
     * @return the state_cd
     */
    public String getState_cd() {
        return state_cd;
    }

    /**
     * @param state_cd the state_cd to set
     */
    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    /**
     * @return the tax_collection
     */
    public boolean isTax_collection() {
        return tax_collection;
    }

    /**
     * @param tax_collection the tax_collection to set
     */
    public void setTax_collection(boolean tax_collection) {
        this.tax_collection = tax_collection;
    }

    public boolean isTax_installment() {
        return tax_installment;
    }

    public void setTax_installment(boolean tax_installment) {
        this.tax_installment = tax_installment;
    }
}
