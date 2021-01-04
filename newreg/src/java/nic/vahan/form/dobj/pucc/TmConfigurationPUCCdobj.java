/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.pucc;

import java.io.Serializable;

/**
 *
 * @author ASHOK
 */
public class TmConfigurationPUCCdobj implements Serializable {

    private String state_cd;
    private String expired_pucc_check;
    private boolean pucc_service;

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
     * @return the expired_pucc_check
     */
    public String getExpired_pucc_check() {
        return expired_pucc_check;
    }

    /**
     * @param expired_pucc_check the expired_pucc_check to set
     */
    public void setExpired_pucc_check(String expired_pucc_check) {
        this.expired_pucc_check = expired_pucc_check;
    }

    public boolean isPucc_service() {
        return pucc_service;
    }

    public void setPucc_service(boolean pucc_service) {
        this.pucc_service = pucc_service;
    }
}
