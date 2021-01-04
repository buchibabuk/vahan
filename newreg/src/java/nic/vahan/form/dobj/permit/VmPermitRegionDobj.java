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
public class VmPermitRegionDobj implements Serializable {

    private String state_code;
    private int off_code;
    private String off_cd_descr;
    private int code;
    private String region;
    private int region_covered;

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public int getOff_code() {
        return off_code;
    }

    public void setOff_code(int off_code) {
        this.off_code = off_code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getRegion_covered() {
        return region_covered;
    }

    public void setRegion_covered(int region_covered) {
        this.region_covered = region_covered;
    }

    public String getOff_cd_descr() {
        return off_cd_descr;
    }

    public void setOff_cd_descr(String off_cd_descr) {
        this.off_cd_descr = off_cd_descr;
    }
}
