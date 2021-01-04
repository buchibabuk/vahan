/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;

/**
 *
 * @author nicsi
 */
public class ChallanConfigrationDobj implements Serializable {

    private String state_cd;
    private boolean book_no_enable;
    private boolean is_tax_enable;
    private boolean ismagistrate_exist;
    private boolean is_nccr_no;
    private boolean is_challan_date;
    private boolean is_challan_time;
    private boolean accu_flag;
    private boolean accu_pol_sta;
    private boolean accu_city;
    private boolean accu_pin_cd;
    private boolean impnd_pol_sta;
    private boolean impnd_dist;
    private boolean commingFrom;
    private boolean goingTo;

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public boolean isBook_no_enable() {
        return book_no_enable;
    }

    public void setBook_no_enable(boolean book_no_enable) {
        this.book_no_enable = book_no_enable;
    }

    public boolean isIs_tax_enable() {
        return is_tax_enable;
    }

    public void setIs_tax_enable(boolean is_tax_enable) {
        this.is_tax_enable = is_tax_enable;
    }

    public boolean isIsmagistrate_exist() {
        return ismagistrate_exist;
    }

    public void setIsmagistrate_exist(boolean ismagistrate_exist) {
        this.ismagistrate_exist = ismagistrate_exist;
    }

    public boolean isIs_nccr_no() {
        return is_nccr_no;
    }

    public void setIs_nccr_no(boolean is_nccr_no) {
        this.is_nccr_no = is_nccr_no;
    }

    public boolean isIs_challan_date() {
        return is_challan_date;
    }

    public void setIs_challan_date(boolean is_challan_date) {
        this.is_challan_date = is_challan_date;
    }

    public boolean isIs_challan_time() {
        return is_challan_time;
    }

    public void setIs_challan_time(boolean is_challan_time) {
        this.is_challan_time = is_challan_time;
    }

    public boolean isAccu_flag() {
        return accu_flag;
    }

    public void setAccu_flag(boolean accu_flag) {
        this.accu_flag = accu_flag;
    }

    public boolean isAccu_pol_sta() {
        return accu_pol_sta;
    }

    public void setAccu_pol_sta(boolean accu_pol_sta) {
        this.accu_pol_sta = accu_pol_sta;
    }

    public boolean isAccu_city() {
        return accu_city;
    }

    public void setAccu_city(boolean accu_city) {
        this.accu_city = accu_city;
    }

    public boolean isAccu_pin_cd() {
        return accu_pin_cd;
    }

    public void setAccu_pin_cd(boolean accu_pin_cd) {
        this.accu_pin_cd = accu_pin_cd;
    }

    public boolean isImpnd_pol_sta() {
        return impnd_pol_sta;
    }

    public void setImpnd_pol_sta(boolean impnd_pol_sta) {
        this.impnd_pol_sta = impnd_pol_sta;
    }

    public boolean isImpnd_dist() {
        return impnd_dist;
    }

    public void setImpnd_dist(boolean impnd_dist) {
        this.impnd_dist = impnd_dist;
    }

    public boolean isCommingFrom() {
        return commingFrom;
    }

    public void setCommingFrom(boolean commingFrom) {
        this.commingFrom = commingFrom;
    }

    public boolean isGoingTo() {
        return goingTo;
    }

    public void setGoingTo(boolean goingTo) {
        this.goingTo = goingTo;
    }
}
