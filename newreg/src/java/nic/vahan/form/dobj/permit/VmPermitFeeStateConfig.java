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
public class VmPermitFeeStateConfig implements Serializable {

    private String state_cd;
    private int pur_cd;
    private int pmt_type;
    private String pur_cd_descr;
    private String pmt_cd_descr;
    private boolean pmt_type_flag;
    private boolean pmt_catg_flag;
    private boolean vh_class_flag;
    private boolean seat_cap_flag;
    private boolean unld_wt_flag;
    private boolean ld_wt_flag;
    private boolean per_region_flag;
    private boolean per_period_flag;

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    public int getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(int pmt_type) {
        this.pmt_type = pmt_type;
    }

    public boolean isPmt_type_flag() {
        return pmt_type_flag;
    }

    public void setPmt_type_flag(boolean pmt_type_flag) {
        this.pmt_type_flag = pmt_type_flag;
    }

    public boolean isPmt_catg_flag() {
        return pmt_catg_flag;
    }

    public void setPmt_catg_flag(boolean pmt_catg_flag) {
        this.pmt_catg_flag = pmt_catg_flag;
    }

    public boolean isVh_class_flag() {
        return vh_class_flag;
    }

    public void setVh_class_flag(boolean vh_class_flag) {
        this.vh_class_flag = vh_class_flag;
    }

    public boolean isSeat_cap_flag() {
        return seat_cap_flag;
    }

    public void setSeat_cap_flag(boolean seat_cap_flag) {
        this.seat_cap_flag = seat_cap_flag;
    }

    public boolean isUnld_wt_flag() {
        return unld_wt_flag;
    }

    public void setUnld_wt_flag(boolean unld_wt_flag) {
        this.unld_wt_flag = unld_wt_flag;
    }

    public boolean isLd_wt_flag() {
        return ld_wt_flag;
    }

    public void setLd_wt_flag(boolean ld_wt_flag) {
        this.ld_wt_flag = ld_wt_flag;
    }

    public boolean isPer_region_flag() {
        return per_region_flag;
    }

    public void setPer_region_flag(boolean per_region_flag) {
        this.per_region_flag = per_region_flag;
    }

    public boolean isPer_period_flag() {
        return per_period_flag;
    }

    public void setPer_period_flag(boolean per_period_flag) {
        this.per_period_flag = per_period_flag;
    }

    public String getPur_cd_descr() {
        return pur_cd_descr;
    }

    public void setPur_cd_descr(String pur_cd_descr) {
        this.pur_cd_descr = pur_cd_descr;
    }

    public String getPmt_cd_descr() {
        return pmt_cd_descr;
    }

    public void setPmt_cd_descr(String pmt_cd_descr) {
        this.pmt_cd_descr = pmt_cd_descr;
    }
}
