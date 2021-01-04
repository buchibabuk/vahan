/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.clients.dobj;

import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;

/**
 *
 * @author acer
 */
public class FeeTaxInputDobj {

    private Owner_dobj ownerDobj;
    private PassengerPermitDetailDobj passengerPermitDetailDobj;
    private java.util.Date date = new java.util.Date();
    private int purcode;

    /**
     * @return the ownerDobj
     */
    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    /**
     * @param ownerDobj the ownerDobj to set
     */
    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    /**
     * @return the passengerPermitDetailDobj
     */
    public PassengerPermitDetailDobj getPassengerPermitDetailDobj() {
        return passengerPermitDetailDobj;
    }

    /**
     * @param passengerPermitDetailDobj the passengerPermitDetailDobj to set
     */
    public void setPassengerPermitDetailDobj(PassengerPermitDetailDobj passengerPermitDetailDobj) {
        this.passengerPermitDetailDobj = passengerPermitDetailDobj;
    }

    /**
     * @return the date
     */
    public java.util.Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(java.util.Date date) {
        this.date = date;
    }

    /**
     * @return the purcode
     */
    public int getPurcode() {
        return purcode;
    }

    /**
     * @param purcode the purcode to set
     */
    public void setPurcode(int purcode) {
        this.purcode = purcode;
    }
}
