/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;

public class VmDaDobj implements Cloneable, Serializable {

    private int code;
    private String description;
    private String offence_code;
    private String da_schedule;
    private String da_sub_schedule;

    public VmDaDobj(int code, String description, String offence_code, String da_schedule, String da_sub_schedule) {

        this.code = code;
        this.description = description;
        this.offence_code = offence_code;
        this.da_schedule = da_schedule;
        this.da_sub_schedule = da_sub_schedule;

    }

    public VmDaDobj() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOffence_code() {
        return offence_code;
    }

    public void setOffence_code(String offence_code) {
        this.offence_code = offence_code;
    }

    public String getDa_schedule() {
        return da_schedule;
    }

    public void setDa_schedule(String da_schedule) {
        this.da_schedule = da_schedule;
    }

    public String getDa_sub_schedule() {
        return da_sub_schedule;
    }

    public void setDa_sub_schedule(String da_sub_schedule) {
        this.da_sub_schedule = da_sub_schedule;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
