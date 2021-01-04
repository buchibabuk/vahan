/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author NIC
 */
public class TmConfigurationSwappingDobj implements Serializable {

    private String state_cd;
    private boolean same_owner_name;
    private boolean same_father_name;
    private boolean same_vehicle_class;
    private boolean multiple_swapping_allowed;
    private int old_vehicle_age;
    private int new_vehicle_age;
    private boolean swapping_allowed_theft_untraced_case;
    private boolean one_regn_must_be_fancy_no;
    private boolean same_vehicle_category;

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
     * @return the same_owner_name
     */
    public boolean isSame_owner_name() {
        return same_owner_name;
    }

    /**
     * @param same_owner_name the same_owner_name to set
     */
    public void setSame_owner_name(boolean same_owner_name) {
        this.same_owner_name = same_owner_name;
    }

    /**
     * @return the same_father_name
     */
    public boolean isSame_father_name() {
        return same_father_name;
    }

    /**
     * @param same_father_name the same_father_name to set
     */
    public void setSame_father_name(boolean same_father_name) {
        this.same_father_name = same_father_name;
    }

    /**
     * @return the same_vehicle_class
     */
    public boolean isSame_vehicle_class() {
        return same_vehicle_class;
    }

    /**
     * @param same_vehicle_class the same_vehicle_class to set
     */
    public void setSame_vehicle_class(boolean same_vehicle_class) {
        this.same_vehicle_class = same_vehicle_class;
    }

    /**
     * @return the old_vehicle_age
     */
    public int getOld_vehicle_age() {
        return old_vehicle_age;
    }

    /**
     * @param old_vehicle_age the old_vehicle_age to set
     */
    public void setOld_vehicle_age(int old_vehicle_age) {
        this.old_vehicle_age = old_vehicle_age;
    }

    /**
     * @return the new_vehicle_age
     */
    public int getNew_vehicle_age() {
        return new_vehicle_age;
    }

    /**
     * @param new_vehicle_age the new_vehicle_age to set
     */
    public void setNew_vehicle_age(int new_vehicle_age) {
        this.new_vehicle_age = new_vehicle_age;
    }

    /**
     * @return the multiple_swapping_allowed
     */
    public boolean isMultiple_swapping_allowed() {
        return multiple_swapping_allowed;
    }

    /**
     * @param multiple_swapping_allowed the multiple_swapping_allowed to set
     */
    public void setMultiple_swapping_allowed(boolean multiple_swapping_allowed) {
        this.multiple_swapping_allowed = multiple_swapping_allowed;
    }

    /**
     * @return the swapping_allowed_theft_untraced_case
     */
    public boolean isSwapping_allowed_theft_untraced_case() {
        return swapping_allowed_theft_untraced_case;
    }

    /**
     * @param swapping_allowed_theft_untraced_case the
     * swapping_allowed_theft_untraced_case to set
     */
    public void setSwapping_allowed_theft_untraced_case(boolean swapping_allowed_theft_untraced_case) {
        this.swapping_allowed_theft_untraced_case = swapping_allowed_theft_untraced_case;
    }

    /**
     * @return the one_regn_must_be_fancy_no
     */
    public boolean isOne_regn_must_be_fancy_no() {
        return one_regn_must_be_fancy_no;
    }

    /**
     * @param one_regn_must_be_fancy_no the one_regn_must_be_fancy_no to set
     */
    public void setOne_regn_must_be_fancy_no(boolean one_regn_must_be_fancy_no) {
        this.one_regn_must_be_fancy_no = one_regn_must_be_fancy_no;
    }

    /**
     * @return the same_vehicle_category
     */
    public boolean isSame_vehicle_category() {
        return same_vehicle_category;
    }

    /**
     * @param same_vehicle_category the same_vehicle_category to set
     */
    public void setSame_vehicle_category(boolean same_vehicle_category) {
        this.same_vehicle_category = same_vehicle_category;
    }
}
