/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.configuration;

import java.io.Serializable;

/**
 *
 * @author ASHOK
 */
public class TmConfigurationApplInwardDobj implements Serializable {

    private String state_cd;
    private String pur_code;
    private String ncr_office;
    private boolean veh_age_verification_required;
    private String check_for_TO_without_NOC;
    private String transactions_allowed_for_expired_vehicle;
    private String check_for_anywhere_fitness;
    private String transactionNotAllowWithoutHSRP;
    private boolean check_for_to_with_noc;
    private String purCdInwardNotAllowInOffice;

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
     * @return the pur_code
     */
    public String getPur_code() {
        return pur_code;
    }

    /**
     * @param pur_code the pur_code to set
     */
    public void setPur_code(String pur_code) {
        this.pur_code = pur_code;
    }

    /**
     * @return the ncr_office
     */
    public String getNcr_office() {
        return ncr_office;
    }

    /**
     * @param ncr_office the ncr_office to set
     */
    public void setNcr_office(String ncr_office) {
        this.ncr_office = ncr_office;
    }

    /**
     * @return the veh_age_verification_required
     */
    public boolean isVeh_age_verification_required() {
        return veh_age_verification_required;
    }

    /**
     * @param veh_age_verification_required the veh_age_verification_required to
     * set
     */
    public void setVeh_age_verification_required(boolean veh_age_verification_required) {
        this.veh_age_verification_required = veh_age_verification_required;
    }

    /**
     * @return the check_for_TO_without_NOC
     */
    public String getCheck_for_TO_without_NOC() {
        return check_for_TO_without_NOC;
    }

    /**
     * @param check_for_TO_without_NOC the check_for_TO_without_NOC to set
     */
    public void setCheck_for_TO_without_NOC(String check_for_TO_without_NOC) {
        this.check_for_TO_without_NOC = check_for_TO_without_NOC;
    }

    /**
     * @return the transactions_allowed_for_expired_vehicle
     */
    public String getTransactions_allowed_for_expired_vehicle() {
        return transactions_allowed_for_expired_vehicle;
    }

    /**
     * @param transactions_allowed_for_expired_vehicle the
     * transactions_allowed_for_expired_vehicle to set
     */
    public void setTransactions_allowed_for_expired_vehicle(String transactions_allowed_for_expired_vehicle) {
        this.transactions_allowed_for_expired_vehicle = transactions_allowed_for_expired_vehicle;
    }

    /**
     * @return the check_for_anywhere_fitness
     */
    public String getCheck_for_anywhere_fitness() {
        return check_for_anywhere_fitness;
    }

    /**
     * @param check_for_anywhere_fitness the check_for_anywhere_fitness to set
     */
    public void setCheck_for_anywhere_fitness(String check_for_anywhere_fitness) {
        this.check_for_anywhere_fitness = check_for_anywhere_fitness;
    }

    /**
     * @return the transactionNotAllowWithoutHSRP
     */
    public String getTransactionNotAllowWithoutHSRP() {
        return transactionNotAllowWithoutHSRP;
    }

    /**
     * @param transactionNotAllowWithoutHSRP the transactionNotAllowWithoutHSRP
     * to set
     */
    public void setTransactionNotAllowWithoutHSRP(String transaction_not_allow_without_HSRP) {
        this.transactionNotAllowWithoutHSRP = transaction_not_allow_without_HSRP;
    }

    public boolean isCheck_for_to_with_noc() {
        return check_for_to_with_noc;
    }

    public void setCheck_for_to_with_noc(boolean check_for_to_with_noc) {
        this.check_for_to_with_noc = check_for_to_with_noc;
    }

    /**
     * @return the purCdInwardNotAllowInOffice
     */
    public String getPurCdInwardNotAllowInOffice() {
        return purCdInwardNotAllowInOffice;
    }

    /**
     * @param purCdInwardNotAllowInOffice the purCdInwardNotAllowInOffice to set
     */
    public void setPurCdInwardNotAllowInOffice(String purCdInwardNotAllowInOffice) {
        this.purCdInwardNotAllowInOffice = purCdInwardNotAllowInOffice;
    }

}
