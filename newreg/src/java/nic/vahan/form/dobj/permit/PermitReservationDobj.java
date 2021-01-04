/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;

/**
 *
 * @author hcl
 */
public class PermitReservationDobj implements Serializable {

    private String state_cd;
    private int pmt_type;
    private int pmt_catg;
    private int owner_ctg;
    private int running_no;
    private int max_no;
    private int fuel;
    private String pmt_type_descr;
    private String pmt_catg_descr;
    private String owner_ctg_descr;
    private String fuel_descr;

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public int getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(int pmt_type) {
        this.pmt_type = pmt_type;
    }

    public int getPmt_catg() {
        return pmt_catg;
    }

    public void setPmt_catg(int pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    public int getOwner_ctg() {
        return owner_ctg;
    }

    public void setOwner_ctg(int owner_ctg) {
        this.owner_ctg = owner_ctg;
    }

    public int getRunning_no() {
        return running_no;
    }

    public void setRunning_no(int running_no) {
        this.running_no = running_no;
    }

    public int getMax_no() {
        return max_no;
    }

    public void setMax_no(int max_no) {
        this.max_no = max_no;
    }

    public String getPmt_type_descr() {
        return pmt_type_descr;
    }

    public void setPmt_type_descr(String pmt_type_descr) {
        this.pmt_type_descr = pmt_type_descr;
    }

    public String getPmt_catg_descr() {
        return pmt_catg_descr;
    }

    public void setPmt_catg_descr(String pmt_catg_descr) {
        this.pmt_catg_descr = pmt_catg_descr;
    }

    public String getOwner_ctg_descr() {
        return owner_ctg_descr;
    }

    public void setOwner_ctg_descr(String owner_ctg_descr) {
        this.owner_ctg_descr = owner_ctg_descr;
    }

    public int getFuel() {
        return fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public String getFuel_descr() {
        return fuel_descr;
    }

    public void setFuel_descr(String fuel_descr) {
        this.fuel_descr = fuel_descr;
    }
}
