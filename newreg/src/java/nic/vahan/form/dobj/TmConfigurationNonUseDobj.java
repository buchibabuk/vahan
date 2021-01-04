/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author Nicsi
 */
public class TmConfigurationNonUseDobj implements Serializable {

    private String state_cd;
    private boolean nonuse_adjust_in_tax_amt;
    private boolean skip_fitness_validation_in_nonuse;
    private boolean nonuse_continue_without_restore;
    private boolean disable_nonuse_fromdate_in_nonuse_continue;
    private boolean docs_surrender;
    private boolean vehicle_inspect_mandatory;
    private boolean declare_withdrawl_date;
    private boolean exemfrom_first_dateofmonth;
    private boolean exemto_last_dateofmonth;
    private boolean exemupto_financial_year;
    private boolean require_advance_tax;
    private boolean vehicle_inspect_for_removalshift;
    private boolean taxclear_for_nonuse_rebate_in_duration;
    private boolean remove_frm_nonuse_in_removalshift;
    private boolean acknnowledge_report_in_restore;
    private String section_act_rule;
    private String approved_authority;

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
     * @return the nonuse_adjust_in_tax_amt
     */
    public boolean isNonuse_adjust_in_tax_amt() {
        return nonuse_adjust_in_tax_amt;
    }

    /**
     * @param nonuse_adjust_in_tax_amt the nonuse_adjust_in_tax_amt to set
     */
    public void setNonuse_adjust_in_tax_amt(boolean nonuse_adjust_in_tax_amt) {
        this.nonuse_adjust_in_tax_amt = nonuse_adjust_in_tax_amt;
    }

    /**
     * @return the skip_fitness_validation_in_nonuse
     */
    public boolean isSkip_fitness_validation_in_nonuse() {
        return skip_fitness_validation_in_nonuse;
    }

    /**
     * @param skip_fitness_validation_in_nonuse the
     * skip_fitness_validation_in_nonuse to set
     */
    public void setSkip_fitness_validation_in_nonuse(boolean skip_fitness_validation_in_nonuse) {
        this.skip_fitness_validation_in_nonuse = skip_fitness_validation_in_nonuse;
    }

    /**
     * @return the nonuse_continue_without_restore
     */
    public boolean isNonuse_continue_without_restore() {
        return nonuse_continue_without_restore;
    }

    /**
     * @param nonuse_continue_without_restore the
     * nonuse_continue_without_restore to set
     */
    public void setNonuse_continue_without_restore(boolean nonuse_continue_without_restore) {
        this.nonuse_continue_without_restore = nonuse_continue_without_restore;
    }

    /**
     * @return the disable_nonuse_fromdate_in_nonuse_continue
     */
    public boolean isDisable_nonuse_fromdate_in_nonuse_continue() {
        return disable_nonuse_fromdate_in_nonuse_continue;
    }

    /**
     * @param disable_nonuse_fromdate_in_nonuse_continue the
     * disable_nonuse_fromdate_in_nonuse_continue to set
     */
    public void setDisable_nonuse_fromdate_in_nonuse_continue(boolean disable_nonuse_fromdate_in_nonuse_continue) {
        this.disable_nonuse_fromdate_in_nonuse_continue = disable_nonuse_fromdate_in_nonuse_continue;
    }

    /**
     * @return the docs_surrender
     */
    public boolean isDocs_surrender() {
        return docs_surrender;
    }

    /**
     * @param docs_surrender the docs_surrender to set
     */
    public void setDocs_surrender(boolean docs_surrender) {
        this.docs_surrender = docs_surrender;
    }

    /**
     * @return the declare_withdrawl_date
     */
    public boolean isDeclare_withdrawl_date() {
        return declare_withdrawl_date;
    }

    /**
     * @param declare_withdrawl_date the declare_withdrawl_date to set
     */
    public void setDeclare_withdrawl_date(boolean declare_withdrawl_date) {
        this.declare_withdrawl_date = declare_withdrawl_date;
    }

    /**
     * @return the vehicle_inspect_mandatory
     */
    public boolean isVehicle_inspect_mandatory() {
        return vehicle_inspect_mandatory;
    }

    /**
     * @param vehicle_inspect_mandatory the vehicle_inspect_mandatory to set
     */
    public void setVehicle_inspect_mandatory(boolean vehicle_inspect_mandatory) {
        this.vehicle_inspect_mandatory = vehicle_inspect_mandatory;
    }

    /**
     * @return the exemfrom_first_dateofmonth
     */
    public boolean isExemfrom_first_dateofmonth() {
        return exemfrom_first_dateofmonth;
    }

    /**
     * @param exemfrom_first_dateofmonth the exemfrom_first_dateofmonth to set
     */
    public void setExemfrom_first_dateofmonth(boolean exemfrom_first_dateofmonth) {
        this.exemfrom_first_dateofmonth = exemfrom_first_dateofmonth;
    }

    /**
     * @return the exemto_last_dateofmonth
     */
    public boolean isExemto_last_dateofmonth() {
        return exemto_last_dateofmonth;
    }

    /**
     * @param exemto_last_dateofmonth the exemto_last_dateofmonth to set
     */
    public void setExemto_last_dateofmonth(boolean exemto_last_dateofmonth) {
        this.exemto_last_dateofmonth = exemto_last_dateofmonth;
    }

    /**
     * @return the exemupto_financial_year
     */
    public boolean isExemupto_financial_year() {
        return exemupto_financial_year;
    }

    /**
     * @param exemupto_financial_year the exemupto_financial_year to set
     */
    public void setExemupto_financial_year(boolean exemupto_financial_year) {
        this.exemupto_financial_year = exemupto_financial_year;
    }

    /**
     * @return the require_advance_tax
     */
    public boolean isRequire_advance_tax() {
        return require_advance_tax;
    }

    /**
     * @param require_advance_tax the require_advance_tax to set
     */
    public void setRequire_advance_tax(boolean require_advance_tax) {
        this.require_advance_tax = require_advance_tax;
    }

    /**
     * @return the vehicle_inspect_for_removalshift
     */
    public boolean isVehicle_inspect_for_removalshift() {
        return vehicle_inspect_for_removalshift;
    }

    /**
     * @param vehicle_inspect_for_removalshift the
     * vehicle_inspect_for_removalshift to set
     */
    public void setVehicle_inspect_for_removalshift(boolean vehicle_inspect_for_removalshift) {
        this.vehicle_inspect_for_removalshift = vehicle_inspect_for_removalshift;
    }

    /**
     * @return the taxclear_for_nonuse_rebate_in_duration
     */
    public boolean isTaxclear_for_nonuse_rebate_in_duration() {
        return taxclear_for_nonuse_rebate_in_duration;
    }

    /**
     * @param taxclear_for_nonuse_rebate_in_duration the
     * taxclear_for_nonuse_rebate_in_duration to set
     */
    public void setTaxclear_for_nonuse_rebate_in_duration(boolean taxclear_for_nonuse_rebate_in_duration) {
        this.taxclear_for_nonuse_rebate_in_duration = taxclear_for_nonuse_rebate_in_duration;
    }

    /**
     * @return the remove_frm_nonuse_in_removalshift
     */
    public boolean isRemove_frm_nonuse_in_removalshift() {
        return remove_frm_nonuse_in_removalshift;
    }

    /**
     * @param remove_frm_nonuse_in_removalshift the
     * remove_frm_nonuse_in_removalshift to set
     */
    public void setRemove_frm_nonuse_in_removalshift(boolean remove_frm_nonuse_in_removalshift) {
        this.remove_frm_nonuse_in_removalshift = remove_frm_nonuse_in_removalshift;
    }

    /**
     * @return the acknnowledge_report_in_restore
     */
    public boolean isAcknnowledge_report_in_restore() {
        return acknnowledge_report_in_restore;
    }

    /**
     * @param acknnowledge_report_in_restore the acknnowledge_report_in_restore
     * to set
     */
    public void setAcknnowledge_report_in_restore(boolean acknnowledge_report_in_restore) {
        this.acknnowledge_report_in_restore = acknnowledge_report_in_restore;
    }

    /**
     * @return the section_act_rule
     */
    public String getSection_act_rule() {
        return section_act_rule;
    }

    /**
     * @param section_act_rule the section_act_rule to set
     */
    public void setSection_act_rule(String section_act_rule) {
        this.section_act_rule = section_act_rule;
    }

    /**
     * @return the approved_authority
     */
    public String getApproved_authority() {
        return approved_authority;
    }

    /**
     * @param approved_authority the approved_authority to set
     */
    public void setApproved_authority(String approved_authority) {
        this.approved_authority = approved_authority;
    }
}
