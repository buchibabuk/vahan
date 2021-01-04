/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author ASHOK
 */
public class VmValidityMastDobj implements Serializable {

    private String state_cd;
    private int sr_no;
    private String condition_formula;
    private int pur_cd;
    private int new_value;
    private int re_new_value;
    private String mod;

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
     * @return the sr_no
     */
    public int getSr_no() {
        return sr_no;
    }

    /**
     * @param sr_no the sr_no to set
     */
    public void setSr_no(int sr_no) {
        this.sr_no = sr_no;
    }

    /**
     * @return the condition_formula
     */
    public String getCondition_formula() {
        return condition_formula;
    }

    /**
     * @param condition_formula the condition_formula to set
     */
    public void setCondition_formula(String condition_formula) {
        this.condition_formula = condition_formula;
    }

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
     * @return the new_value
     */
    public int getNew_value() {
        return new_value;
    }

    /**
     * @param new_value the new_value to set
     */
    public void setNew_value(int new_value) {
        this.new_value = new_value;
    }

    /**
     * @return the re_new_value
     */
    public int getRe_new_value() {
        return re_new_value;
    }

    /**
     * @param re_new_value the re_new_value to set
     */
    public void setRe_new_value(int re_new_value) {
        this.re_new_value = re_new_value;
    }

    /**
     * @return the mod
     */
    public String getMod() {
        return mod;
    }

    /**
     * @param mod the mod to set
     */
    public void setMod(String mod) {
        this.mod = mod;
    }
}
