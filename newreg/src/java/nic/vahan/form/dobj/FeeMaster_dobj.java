/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author Ashok Kumar <send2ashok@hotmail.com>
 */
public class FeeMaster_dobj implements Serializable {

    private int pur_cd;
    private String descr;
    private String short_descr;
    private String catg;
    private String catg_desc;
    private int fees;
    private int service_charge;
    private int imported_fees;

    /**
     * @return the pur_cd
     */
    public int getPur_cd() {
        return pur_cd;
    }

    /**
     * @param pur_cd the pur_cd to set
     */
    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    /**
     * @return the descr
     */
    public String getDescr() {
        return descr;
    }

    /**
     * @param descr the descr to set
     */
    public void setDescr(String descr) {
        this.descr = descr;
    }

    /**
     * @return the short_descr
     */
    public String getShort_descr() {
        return short_descr;
    }

    /**
     * @param short_descr the short_descr to set
     */
    public void setShort_descr(String short_descr) {
        this.short_descr = short_descr;
    }

    /**
     * @return the catg
     */
    public String getCatg() {
        return catg;
    }

    /**
     * @param catg the catg to set
     */
    public void setCatg(String catg) {
        this.catg = catg;
    }

    /**
     * @return the catg_desc
     */
    public String getCatg_desc() {
        return catg_desc;
    }

    /**
     * @param catg_desc the catg_desc to set
     */
    public void setCatg_desc(String catg_desc) {
        this.catg_desc = catg_desc;
    }

    /**
     * @return the fees
     */
    public int getFees() {
        return fees;
    }

    /**
     * @param fees the fees to set
     */
    public void setFees(int fees) {
        this.fees = fees;
    }

    /**
     * @return the service_charge
     */
    public int getService_charge() {
        return service_charge;
    }

    /**
     * @param service_charge the service_charge to set
     */
    public void setService_charge(int service_charge) {
        this.service_charge = service_charge;
    }

    /**
     * @return the imported_fees
     */
    public int getImported_fees() {
        return imported_fees;
    }

    /**
     * @param imported_fees the imported_fees to set
     */
    public void setImported_fees(int imported_fees) {
        this.imported_fees = imported_fees;
    }
}
