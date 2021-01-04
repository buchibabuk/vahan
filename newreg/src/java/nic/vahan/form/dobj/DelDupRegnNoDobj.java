/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author DELL
 */
public class DelDupRegnNoDobj implements Serializable {

    private String regnNo;
    private int totalOffices;
    private String offices;

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public int getTotalOffices() {
        return totalOffices;
    }

    public void setTotalOffices(int totalOffices) {
        this.totalOffices = totalOffices;
    }

    public String getOffices() {
        return offices;
    }

    public void setOffices(String offices) {
        this.offices = offices;
    }
}
