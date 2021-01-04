/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author afzal
 */
public class VmSmartCardHsrpDobj implements Serializable {

    private String state_cd;
    private boolean smart_card;
    private boolean hsrp;
    private boolean cash_counter_closed;
    private boolean old_veh_hsrp;
    private String paper_rc;
    private Date day_begin;
    private Date last_working_day;
    private String new_regn_not_allowed;
    private String new_regn_not_allowed_msg;
    private String automaticFitness;

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
     * @return the smart_card
     */
    public boolean isSmart_card() {
        return smart_card;
    }

    /**
     * @param smart_card the smart_card to set
     */
    public void setSmart_card(boolean smart_card) {
        this.smart_card = smart_card;
    }

    /**
     * @return the hsrp
     */
    public boolean isHsrp() {
        return hsrp;
    }

    /**
     * @param hsrp the hsrp to set
     */
    public void setHsrp(boolean hsrp) {
        this.hsrp = hsrp;
    }

    /**
     * @return the cash_counter_closed
     */
    public boolean isCash_counter_closed() {
        return cash_counter_closed;
    }

    /**
     * @param cash_counter_closed the cash_counter_closed to set
     */
    public void setCash_counter_closed(boolean cash_counter_closed) {
        this.cash_counter_closed = cash_counter_closed;
    }

    /**
     * @return the old_veh_hsrp
     */
    public boolean isOld_veh_hsrp() {
        return old_veh_hsrp;
    }

    /**
     * @param old_veh_hsrp the old_veh_hsrp to set
     */
    public void setOld_veh_hsrp(boolean old_veh_hsrp) {
        this.old_veh_hsrp = old_veh_hsrp;
    }

    /**
     * @return the paper_rc
     */
    public String getPaper_rc() {
        return paper_rc;
    }

    /**
     * @param paper_rc the paper_rc to set
     */
    public void setPaper_rc(String paper_rc) {
        this.paper_rc = paper_rc;
    }

    /**
     * @return the day_begin
     */
    public Date getDay_begin() {
        return day_begin;
    }

    /**
     * @param day_begin the day_begin to set
     */
    public void setDay_begin(Date day_begin) {
        this.day_begin = day_begin;
    }

    /**
     * @return the last_working_day
     */
    public Date getLast_working_day() {
        return last_working_day;
    }

    /**
     * @param last_working_day the last_working_day to set
     */
    public void setLast_working_day(Date last_working_day) {
        this.last_working_day = last_working_day;
    }

    /**
     * @return the new_regn_not_allowed
     */
    public String getNew_regn_not_allowed() {
        return new_regn_not_allowed;
    }

    /**
     * @param new_regn_not_allowed the new_regn_not_allowed to set
     */
    public void setNew_regn_not_allowed(String new_regn_not_allowed) {
        this.new_regn_not_allowed = new_regn_not_allowed;
    }

    /**
     * @return the new_regn_not_allowed_msg
     */
    public String getNew_regn_not_allowed_msg() {
        return new_regn_not_allowed_msg;
    }

    /**
     * @param new_regn_not_allowed_msg the new_regn_not_allowed_msg to set
     */
    public void setNew_regn_not_allowed_msg(String new_regn_not_allowed_msg) {
        this.new_regn_not_allowed_msg = new_regn_not_allowed_msg;
    }

    /**
     * @return the automaticFitness
     */
    public String getAutomaticFitness() {
        return automaticFitness;
    }

    /**
     * @param automaticFitness the automaticFitness to set
     */
    public void setAutomaticFitness(String automaticFitness) {
        this.automaticFitness = automaticFitness;
    }
}
