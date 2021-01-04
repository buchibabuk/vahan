/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.util.Date;
import oracle.sql.DATE;

/**
 *
 * @author DELL
 */
public class TmConfigurationServiceExemption {

    private String state_cd;
    private String pur_cd;
    private Date from_dt;
    private Date upto_dt;
    private boolean tax_exemption;
    private boolean pucc_exemption;
    private boolean insurace_exemption;

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public String getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(String pur_cd) {
        this.pur_cd = pur_cd;
    }

    public Date getFrom_dt() {
        return from_dt;
    }

    public void setFrom_dt(Date from_dt) {
        this.from_dt = from_dt;
    }

    public Date getUpto_dt() {
        return upto_dt;
    }

    public void setUpto_dt(Date upto_dt) {
        this.upto_dt = upto_dt;
    }

    public boolean isTax_exemption() {
        return tax_exemption;
    }

    public void setTax_exemption(boolean tax_exemption) {
        this.tax_exemption = tax_exemption;
    }

    public boolean isPucc_exemption() {
        return pucc_exemption;
    }

    public void setPucc_exemption(boolean pucc_exemption) {
        this.pucc_exemption = pucc_exemption;
    }

    public boolean isInsurace_exemption() {
        return insurace_exemption;
    }

    public void setInsurace_exemption(boolean insurace_exemption) {
        this.insurace_exemption = insurace_exemption;
    }
    

}
