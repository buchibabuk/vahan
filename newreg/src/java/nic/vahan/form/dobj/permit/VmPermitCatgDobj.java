/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;

/**
 *
 * @author tranC106
 */
public class VmPermitCatgDobj implements Serializable {

    private String state_code;
    private String ccode;
    private String description;
    private int permit_code;
    private String pmt_type_descr;
    private boolean flag;

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPermit_code() {
        return permit_code;
    }

    public void setPermit_code(int permit_code) {
        this.permit_code = permit_code;
    }

    public String getCcode() {
        return ccode;
    }

    public void setCcode(String ccode) {
        this.ccode = ccode;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getPmt_type_descr() {
        return pmt_type_descr;
    }

    public void setPmt_type_descr(String pmt_type_descr) {
        this.pmt_type_descr = pmt_type_descr;
    }
}
