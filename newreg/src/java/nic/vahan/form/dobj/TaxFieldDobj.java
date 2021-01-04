/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class TaxFieldDobj implements Serializable {

    private String tax1Label = "Addl Tax1";
    private String tax2Label = "Addl Tax2";

    /**
     * @return the tax1Label
     */
    public String getTax1Label() {
        return tax1Label;
    }

    /**
     * @param tax1Label the tax1Label to set
     */
    public void setTax1Label(String tax1Label) {
        this.tax1Label = tax1Label;
    }

    /**
     * @return the tax2Label
     */
    public String getTax2Label() {
        return tax2Label;
    }

    /**
     * @param tax2Label the tax2Label to set
     */
    public void setTax2Label(String tax2Label) {
        this.tax2Label = tax2Label;
    }
}
