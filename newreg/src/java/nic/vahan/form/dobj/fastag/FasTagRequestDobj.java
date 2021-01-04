/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.fastag;

import java.io.Serializable;

/**
 *
 * @author DELL
 */
public class FasTagRequestDobj implements Serializable {

    private String vin;
    private String engineno;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getEngineno() {
        return engineno;
    }

    public void setEngineno(String engineno) {
        this.engineno = engineno;
    }
}
