/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;

/**
 *
 * @author nic
 */
public class VmCourtDobj implements Cloneable, Serializable {

    private String courtname;
    private int courtcode;

    public VmCourtDobj(int courtcode, String courtname) {

        this.courtname = courtname;
        this.courtcode = courtcode;
    }

    public VmCourtDobj() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public String getCourtname() {
        return courtname;
    }

    public void setCourtname(String courtname) {
        this.courtname = courtname;
    }

    public int getCourtcode() {
        return courtcode;
    }

    public void setCourtcode(int courtcode) {
        this.courtcode = courtcode;
    }
}
