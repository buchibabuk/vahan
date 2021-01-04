/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;

/**
 *
 * @author afzal
 */
public class ScrapVehicleDobj implements Serializable {

    private String old_regn_no;
    private String old_chasi_no;
    private String scrap_cert_no;
    private String loi_no;
    private int scrap_reason;
    private String state_cd;
    private int off_cd;
    private String new_regn_no;

    /**
     * @return the old_regn_no
     */
    public String getOld_regn_no() {
        return old_regn_no;
    }

    /**
     * @param old_regn_no the old_regn_no to set
     */
    public void setOld_regn_no(String old_regn_no) {
        this.old_regn_no = old_regn_no;
    }

    /**
     * @return the old_chasi_no
     */
    public String getOld_chasi_no() {
        return old_chasi_no;
    }

    /**
     * @param old_chasi_no the old_chasi_no to set
     */
    public void setOld_chasi_no(String old_chasi_no) {
        this.old_chasi_no = old_chasi_no;
    }

    /**
     * @return the scrap_cert_no
     */
    public String getScrap_cert_no() {
        return scrap_cert_no;
    }

    /**
     * @param scrap_cert_no the scrap_cert_no to set
     */
    public void setScrap_cert_no(String scrap_cert_no) {
        this.scrap_cert_no = scrap_cert_no;
    }

    /**
     * @return the loi_no
     */
    public String getLoi_no() {
        return loi_no;
    }

    /**
     * @param loi_no the loi_no to set
     */
    public void setLoi_no(String loi_no) {
        this.loi_no = loi_no;
    }

    /**
     * @return the scrap_reason
     */
    public int getScrap_reason() {
        return scrap_reason;
    }

    /**
     * @param scrap_reason the scrap_reason to set
     */
    public void setScrap_reason(int scrap_reason) {
        this.scrap_reason = scrap_reason;
    }

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
     * @return the off_cd
     */
    public int getOff_cd() {
        return off_cd;
    }

    /**
     * @param off_cd the off_cd to set
     */
    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    /**
     * @return the new_regn_no
     */
    public String getNew_regn_no() {
        return new_regn_no;
    }

    /**
     * @param new_regn_no the new_regn_no to set
     */
    public void setNew_regn_no(String new_regn_no) {
        this.new_regn_no = new_regn_no;
    }
}
