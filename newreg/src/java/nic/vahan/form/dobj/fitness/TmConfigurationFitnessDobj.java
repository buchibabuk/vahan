/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.fitness;

import java.io.Serializable;

/**
 *
 * @author ASHOK
 */
public class TmConfigurationFitnessDobj implements Serializable {

    private String state_cd;
    private boolean logged_in_user_is_fitness_officer;
    private boolean reflective_tape_allowed;
    private boolean skip_fee_for_failed_fitness;
    private boolean skip_user_chg_fitness_centre;
    private boolean fitness_revoke_allowed;
    private int fitness_revoke_allowed_days;
    private int grace_days_for_failed_fitness;
    private boolean check_for_multiple_time_failed_fitness;
    private boolean fit_upto_from_insp_dt_even_appl_before_fit_expiry;
    private String permit_exemption_check_for_veh_age_calculation;
    private boolean multiple_fit_officer;
    private String skip_fitness_check_if_veh_age_expire;
    private boolean NewVehicleFitness;
    private boolean fitness_done_in_suvas_center;
    private boolean speed_governor_reflective_tape_data_from_suvas;
    private boolean document_upload;
    private String allowInspectionForVeh;
    private boolean fcAfterHSRP;
    private String checkSldFitment;
    private boolean uploadFitnessImageAllowed;
    private String check_vltd;

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
     * @return the logged_in_user_is_fitness_officer
     */
    public boolean isLogged_in_user_is_fitness_officer() {
        return logged_in_user_is_fitness_officer;
    }

    /**
     * @param logged_in_user_is_fitness_officer the
     * logged_in_user_is_fitness_officer to set
     */
    public void setLogged_in_user_is_fitness_officer(boolean logged_in_user_is_fitness_officer) {
        this.logged_in_user_is_fitness_officer = logged_in_user_is_fitness_officer;
    }

    /**
     * @return the reflective_tape_allowed
     */
    public boolean isReflective_tape_allowed() {
        return reflective_tape_allowed;
    }

    /**
     * @param reflective_tape_allowed the reflective_tape_allowed to set
     */
    public void setReflective_tape_allowed(boolean reflective_tape_allowed) {
        this.reflective_tape_allowed = reflective_tape_allowed;
    }

    /**
     * @return the skip_fee_for_failed_fitness
     */
    public boolean isSkip_fee_for_failed_fitness() {
        return skip_fee_for_failed_fitness;
    }

    /**
     * @param skip_fee_for_failed_fitness the skip_fee_for_failed_fitness to set
     */
    public void setSkip_fee_for_failed_fitness(boolean skip_fee_for_failed_fitness) {
        this.skip_fee_for_failed_fitness = skip_fee_for_failed_fitness;
    }

    /**
     * @return the skip_user_chg_fitness_centre
     */
    public boolean isSkip_user_chg_fitness_centre() {
        return skip_user_chg_fitness_centre;
    }

    /**
     * @param skip_user_chg_fitness_centre the skip_user_chg_fitness_centre to
     * set
     */
    public void setSkip_user_chg_fitness_centre(boolean skip_user_chg_fitness_centre) {
        this.skip_user_chg_fitness_centre = skip_user_chg_fitness_centre;
    }

    /**
     * @return the fitness_revoke_allowed
     */
    public boolean isFitness_revoke_allowed() {
        return fitness_revoke_allowed;
    }

    /**
     * @param fitness_revoke_allowed the fitness_revoke_allowed to set
     */
    public void setFitness_revoke_allowed(boolean fitness_revoke_allowed) {
        this.fitness_revoke_allowed = fitness_revoke_allowed;
    }

    /**
     * @return the fitness_revoke_allowed_days
     */
    public int getFitness_revoke_allowed_days() {
        return fitness_revoke_allowed_days;
    }

    /**
     * @param fitness_revoke_allowed_days the fitness_revoke_allowed_days to set
     */
    public void setFitness_revoke_allowed_days(int fitness_revoke_allowed_days) {
        this.fitness_revoke_allowed_days = fitness_revoke_allowed_days;
    }

    /**
     * @return the check_for_multiple_time_failed_fitness
     */
    public boolean isCheck_for_multiple_time_failed_fitness() {
        return check_for_multiple_time_failed_fitness;
    }

    /**
     * @param check_for_multiple_time_failed_fitness the
     * check_for_multiple_time_failed_fitness to set
     */
    public void setCheck_for_multiple_time_failed_fitness(boolean check_for_multiple_time_failed_fitness) {
        this.check_for_multiple_time_failed_fitness = check_for_multiple_time_failed_fitness;
    }

    /**
     * @return the grace_days_for_failed_fitness
     */
    public int getGrace_days_for_failed_fitness() {
        return grace_days_for_failed_fitness;
    }

    /**
     * @param grace_days_for_failed_fitness the grace_days_for_failed_fitness to
     * set
     */
    public void setGrace_days_for_failed_fitness(int grace_days_for_failed_fitness) {
        this.grace_days_for_failed_fitness = grace_days_for_failed_fitness;
    }

    /**
     * @return the fit_upto_from_insp_dt_even_appl_before_fit_expiry
     */
    public boolean isFit_upto_from_insp_dt_even_appl_before_fit_expiry() {
        return fit_upto_from_insp_dt_even_appl_before_fit_expiry;
    }

    /**
     * @param fit_upto_from_insp_dt_even_appl_before_fit_expiry the
     * fit_upto_from_insp_dt_even_appl_before_fit_expiry to set
     */
    public void setFit_upto_from_insp_dt_even_appl_before_fit_expiry(boolean fit_upto_from_insp_dt_even_appl_before_fit_expiry) {
        this.fit_upto_from_insp_dt_even_appl_before_fit_expiry = fit_upto_from_insp_dt_even_appl_before_fit_expiry;
    }

    /**
     * @return the permit_exemption_check_for_veh_age_calculation
     */
    public String getPermit_exemption_check_for_veh_age_calculation() {
        return permit_exemption_check_for_veh_age_calculation;
    }

    /**
     * @param permit_exemption_check_for_veh_age_calculation the
     * permit_exemption_check_for_veh_age_calculation to set
     */
    public void setPermit_exemption_check_for_veh_age_calculation(String permit_exemption_check_for_veh_age_calculation) {
        this.permit_exemption_check_for_veh_age_calculation = permit_exemption_check_for_veh_age_calculation;
    }

    /**
     * @return the multiple_fit_officer
     */
    public boolean isMultiple_fit_officer() {
        return multiple_fit_officer;
    }

    /**
     * @param multiple_fit_officer the multiple_fit_officer to set
     */
    public void setMultiple_fit_officer(boolean multiple_fit_officer) {
        this.multiple_fit_officer = multiple_fit_officer;
    }

    /**
     * @return the skip_fitness_check_if_veh_age_expire
     */
    public String getSkip_fitness_check_if_veh_age_expire() {
        return skip_fitness_check_if_veh_age_expire;
    }

    /**
     * @param skip_fitness_check_if_veh_age_expire the
     * skip_fitness_check_if_veh_age_expire to set
     */
    public void setSkip_fitness_check_if_veh_age_expire(String skip_fitness_check_if_veh_age_expire) {
        this.skip_fitness_check_if_veh_age_expire = skip_fitness_check_if_veh_age_expire;
    }

    public boolean isNewVehicleFitness() {
        return NewVehicleFitness;
    }

    public void setNewVehicleFitness(boolean NewVehicleFitness) {
        this.NewVehicleFitness = NewVehicleFitness;
    }

    /**
     * @return the fitness_done_in_suvas_center
     */
    public boolean isFitness_done_in_suvas_center() {
        return fitness_done_in_suvas_center;
    }

    /**
     * @param fitness_done_in_suvas_center the fitness_done_in_suvas_center to
     * set
     */
    public void setFitness_done_in_suvas_center(boolean fitness_done_in_suvas_center) {
        this.fitness_done_in_suvas_center = fitness_done_in_suvas_center;
    }

    /**
     * @return the speed_governor_reflective_tape_data_from_suvas
     */
    public boolean isSpeed_governor_reflective_tape_data_from_suvas() {
        return speed_governor_reflective_tape_data_from_suvas;
    }

    /**
     * @param speed_governor_reflective_tape_data_from_suvas the
     * speed_governor_reflective_tape_data_from_suvas to set
     */
    public void setSpeed_governor_reflective_tape_data_from_suvas(boolean speed_governor_reflective_tape_data_from_suvas) {
        this.speed_governor_reflective_tape_data_from_suvas = speed_governor_reflective_tape_data_from_suvas;
    }

    public boolean isDocument_upload() {
        return document_upload;
    }

    public void setDocument_upload(boolean document_upload) {
        this.document_upload = document_upload;
    }

    /**
     * @return the allowInspectionForVeh
     */
    public String getAllowInspectionForVeh() {
        return allowInspectionForVeh;
    }

    /**
     * @param allowInspectionForVeh the allowInspectionForVeh to set
     */
    public void setAllowInspectionForVeh(String allowInspectionForVeh) {
        this.allowInspectionForVeh = allowInspectionForVeh;
    }

    /**
     * @return the fcAfterHSRP
     */
    public boolean isFcAfterHSRP() {
        return fcAfterHSRP;
    }

    /**
     * @param fcAfterHSRP the fcAfterHSRP to set
     */
    public void setFcAfterHSRP(boolean fcAfterHSRP) {
        this.fcAfterHSRP = fcAfterHSRP;
    }

    /**
     * @return the checkSldFitment
     */
    public String getCheckSldFitment() {
        return checkSldFitment;
    }

    /**
     * @param checkSldFitment the checkSldFitment to set
     */
    public void setCheckSldFitment(String checkSldFitment) {
        this.checkSldFitment = checkSldFitment;
    }

    public boolean isUploadFitnessImageAllowed() {
        return uploadFitnessImageAllowed;
    }

    public void setUploadFitnessImageAllowed(boolean uploadFitnessImageAllowed) {
        this.uploadFitnessImageAllowed = uploadFitnessImageAllowed;
    }

    /**
     * @return the check_vltd
     */
    public String getCheck_vltd() {
        return check_vltd;
    }

    /**
     * @param check_vltd the check_vltd to set
     */
    public void setCheck_vltd(String check_vltd) {
        this.check_vltd = check_vltd;
    }
}
