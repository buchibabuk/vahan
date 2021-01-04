/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import nic.vahan.form.dobj.TaxFieldDobj;
import nic.vahan.form.impl.TaxServer_Impl;

/**
 *
 * @author Administrator
 */
@SessionScoped
@ManagedBean(name = "taxFieldLabel")
public class TaxFieldLabelBean implements Serializable {

    private TaxFieldDobj taxFieldDobj = null;

    public TaxFieldLabelBean() {
        taxFieldDobj = TaxServer_Impl.getTaxField();
    }

    /**
     * @return the taxFieldDobj
     */
    public TaxFieldDobj getTaxFieldDobj() {
        return taxFieldDobj;
    }

    /**
     * @param taxFieldDobj the taxFieldDobj to set
     */
    public void setTaxFieldDobj(TaxFieldDobj taxFieldDobj) {
        this.taxFieldDobj = taxFieldDobj;
    }
}
