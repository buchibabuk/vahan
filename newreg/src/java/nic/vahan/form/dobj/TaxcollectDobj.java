/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author tranC113
 */
public class TaxcollectDobj implements Serializable {

    private int owner_cd;
    private int vh_class;
    private int seat_cap;
    private int stand_cap;
    private int sleeper_cap;
    private int unld_wt;
    private int ld_wt;
    private int fuel;
    //private int norms;
    private float cubic_cap = 0f;
    private float floor_area = 0f;
    private String ac_fitted;
    private String vch_purchase_as;
    private String vch_catg;
    private int sale_amt;
    private float hp = 0f;
    private String state_cd;
    private int off_cd;
    private int other_criteria;
    private String imported_veh;
    private String tax_mode;
    private Date tax_frdt = null;
    private String pmt_type;
    private String pmt_catg;
    private int numberoftrips;
    private double routeLen;
    private int services_type;
    private int domain_cd;
    private Date purchase_dt = null;
    private String RegnType;
    private int vchType;

    /**
     * @return the owner_cd
     */
    public int getOwner_cd() {
        return owner_cd;
    }

    /**
     * @param owner_cd the owner_cd to set
     */
    public void setOwner_cd(int owner_cd) {
        this.owner_cd = owner_cd;
    }

    /**
     * @return the vh_class
     */
    public int getVh_class() {
        return vh_class;
    }

    /**
     * @param vh_class the vh_class to set
     */
    public void setVh_class(int vh_class) {
        this.vh_class = vh_class;
    }

    /**
     * @return the seat_cap
     */
    public int getSeat_cap() {
        return seat_cap;
    }

    /**
     * @param seat_cap the seat_cap to set
     */
    public void setSeat_cap(int seat_cap) {
        this.seat_cap = seat_cap;
    }

    /**
     * @return the stand_cap
     */
    public int getStand_cap() {

        return stand_cap;
    }

    /**
     * @param stand_cap the stand_cap to set
     */
    public void setStand_cap(int stand_cap) {
        this.stand_cap = stand_cap;
    }

    /**
     * @return the sleeper_cap
     */
    public int getSleeper_cap() {
        return sleeper_cap;
    }

    /**
     * @param sleeper_cap the sleeper_cap to set
     */
    public void setSleeper_cap(int sleeper_cap) {
        this.sleeper_cap = sleeper_cap;
    }

    /**
     * @return the unld_wt
     */
    public int getUnld_wt() {
        return unld_wt;
    }

    /**
     * @param unld_wt the unld_wt to set
     */
    public void setUnld_wt(int unld_wt) {
        this.unld_wt = unld_wt;
    }

    /**
     * @return the ld_wt
     */
    public int getLd_wt() {
        return ld_wt;
    }

    /**
     * @param ld_wt the ld_wt to set
     */
    public void setLd_wt(int ld_wt) {
        this.ld_wt = ld_wt;
    }

    /**
     * @return the fuel
     */
    public int getFuel() {
        return fuel;
    }

    /**
     * @param fuel the fuel to set
     */
    public void setFuel(int fuel) {
        this.fuel = fuel;
    }
//
//    /**
//     * @return the norms
//     */
//    public int getNorms() {
//        return norms;
//    }
//
//    /**
//     * @param norms the norms to set
//     */
//    public void setNorms(int norms) {
//        this.norms = norms;
//    }

    /**
     * @return the cubic_cap
     */
    public float getCubic_cap() {
        return cubic_cap;
    }

    /**
     * @param cubic_cap the cubic_cap to set
     */
    public void setCubic_cap(float cubic_cap) {
        this.cubic_cap = cubic_cap;
    }

    /**
     * @return the floor_area
     */
    public float getFloor_area() {
        return floor_area;
    }

    /**
     * @param floor_area the floor_area to set
     */
    public void setFloor_area(float floor_area) {
        this.floor_area = floor_area;
    }

    /**
     * @return the ac_fitted
     */
    public String getAc_fitted() {
        return ac_fitted;
    }

    /**
     * @param ac_fitted the ac_fitted to set
     */
    public void setAc_fitted(String ac_fitted) {
        this.ac_fitted = ac_fitted;
    }

    /**
     * @return the vch_purchase_as
     */
    public String getVch_purchase_as() {
        return vch_purchase_as;
    }

    /**
     * @param vch_purchase_as the vch_purchase_as to set
     */
    public void setVch_purchase_as(String vch_purchase_as) {
        this.vch_purchase_as = vch_purchase_as;
    }

    /**
     * @return the vch_catg
     */
    public String getVch_catg() {
        return vch_catg;
    }

    /**
     * @param vch_catg the vch_catg to set
     */
    public void setVch_catg(String vch_catg) {
        this.vch_catg = vch_catg;
    }

    /**
     * @return the sale_amt
     */
    public int getSale_amt() {
        return sale_amt;
    }

    /**
     * @param sale_amt the sale_amt to set
     */
    public void setSale_amt(int sale_amt) {
        this.sale_amt = sale_amt;
    }

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
     * @return the off_cd
     */
    public int getOff_cd() {
        return off_cd;
    }

    /**
     * @param off_cd the off_cd to set
     */
    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    /**
     * @return the other_criteria
     */
    public int getOther_criteria() {
        return other_criteria;
    }

    /**
     * @param other_criteria the other_criteria to set
     */
    public void setOther_criteria(int other_criteria) {
        this.other_criteria = other_criteria;
    }

    /**
     * @return the imported_veh
     */
    public String getImported_veh() {
        return imported_veh;
    }

    /**
     * @param imported_veh the imported_veh to set
     */
    public void setImported_veh(String imported_veh) {
        this.imported_veh = imported_veh;
    }

    /**
     * @return the tax_mode
     */
    public String getTax_mode() {
        return tax_mode;
    }

    /**
     * @param tax_mode the tax_mode to set
     */
    public void setTax_mode(String tax_mode) {
        this.tax_mode = tax_mode;
    }

    /**
     * @return the tax_frdt
     */
    public Date getTax_frdt() {
        return tax_frdt;
    }

    /**
     * @param tax_frdt the tax_frdt to set
     */
    public void setTax_frdt(Date tax_frdt) {
        this.tax_frdt = tax_frdt;
    }

    /**
     * @return the pmt_type
     */
    public String getPmt_type() {
        return pmt_type;
    }

    /**
     * @param pmt_type the pmt_type to set
     */
    public void setPmt_type(String pmt_type) {
        this.pmt_type = pmt_type;
    }

    /**
     * @return the pmt_catg
     */
    public String getPmt_catg() {
        return pmt_catg;
    }

    /**
     * @param pmt_catg the pmt_catg to set
     */
    public void setPmt_catg(String pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    /**
     * @return the numberoftrips
     */
    public int getNumberoftrips() {
        return numberoftrips;
    }

    /**
     * @param numberoftrips the numberoftrips to set
     */
    public void setNumberoftrips(int numberoftrips) {
        this.numberoftrips = numberoftrips;
    }

    /**
     * @return the hp
     */
    public float getHp() {
        return hp;
    }

    /**
     * @param hp the hp to set
     */
    public void setHp(float hp) {
        this.hp = hp;
    }

    /**
     * @return the routeLen
     */
    public double getRouteLen() {
        return routeLen;
    }

    /**
     * @param routeLen the routeLen to set
     */
    public void setRouteLen(double routeLen) {
        this.routeLen = routeLen;
    }

    /**
     * @return the services_type
     */
    public int getServices_type() {
        return services_type;
    }

    /**
     * @param services_type the services_type to set
     */
    public void setServices_type(int services_type) {
        this.services_type = services_type;
    }

    /**
     * @return the domain_cd
     */
    public int getDomain_cd() {
        return domain_cd;
    }

    /**
     * @param domain_cd the domain_cd to set
     */
    public void setDomain_cd(int domain_cd) {
        this.domain_cd = domain_cd;
    }

    /**
     * @return the purchase_dt
     */
    public Date getPurchase_dt() {
        return purchase_dt;
    }

    /**
     * @param purchase_dt the purchase_dt to set
     */
    public void setPurchase_dt(Date purchase_dt) {
        this.purchase_dt = purchase_dt;
    }

    /**
     * @return the RegnType
     */
    public String getRegnType() {
        return RegnType;
    }

    /**
     * @param RegnType the RegnType to set
     */
    public void setRegnType(String RegnType) {
        this.RegnType = RegnType;
    }

    /**
     * @return the vchType
     */
    public int getVchType() {
        return vchType;
    }

    /**
     * @param vchType the vchType to set
     */
    public void setVchType(int vchType) {
        this.vchType = vchType;
    }
}
