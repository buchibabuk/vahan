/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.configuration;

import java.io.Serializable;

/**
 *
 * @author Dhananjay
 */
public class TmConfigApplAppointmentInward implements Serializable {

    private String state_cd;
    private String restrictedPurCdInwardOnline;
    private boolean appointmentOnDate;

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
     * @return the restrictedPurCdInwardOnline
     */
    public String getRestrictedPurCdInwardOnline() {
        return restrictedPurCdInwardOnline;
    }

    /**
     * @param restrictedPurCdInwardOnline the restrictedPurCdInwardOnline to set
     */
    public void setRestrictedPurCdInwardOnline(String restrictedPurCdInwardOnline) {
        this.restrictedPurCdInwardOnline = restrictedPurCdInwardOnline;
    }

    /**
     * @return the appointmentOnDate
     */
    public boolean isAppointmentOnDate() {
        return appointmentOnDate;
    }

    /**
     * @param appointmentOnDate the appointmentOnDate to set
     */
    public void setAppointmentOnDate(boolean appointmentOnDate) {
        this.appointmentOnDate = appointmentOnDate;
    }

}
