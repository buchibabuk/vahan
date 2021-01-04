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
public class VmPermitFeeMasterDobj implements Serializable {

    private String state_code;
    private int purpose_code;
    private String pur_cd_descr;
    private String pmt_type_descr;
    private int permit_type;
    private int permit_catg;
    private String pmt_catg_descr;
    private String l_vh_class_descr;
    private String u_vh_class_descr;
    private int l_vh_class;
    private int u_vh_class;
    private int l_seat_cap;
    private int u_seat_cap;
    private int l_unld_wt;
    private int u_unld_wt;
    private int l_ld_wt;
    private int u_ld_wt;
    private String per_region;
    private String per_period;
    private int fee;
    private int fine;

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public int getPurpose_code() {
        return purpose_code;
    }

    public void setPurpose_code(int purpose_code) {
        this.purpose_code = purpose_code;
    }

    public int getPermit_type() {
        return permit_type;
    }

    public void setPermit_type(int permit_type) {
        this.permit_type = permit_type;
    }

    public int getPermit_catg() {
        return permit_catg;
    }

    public void setPermit_catg(int permit_catg) {
        this.permit_catg = permit_catg;
    }

    public int getL_vh_class() {
        return l_vh_class;
    }

    public void setL_vh_class(int l_vh_class) {
        this.l_vh_class = l_vh_class;
    }

    public int getU_vh_class() {
        return u_vh_class;
    }

    public void setU_vh_class(int u_vh_class) {
        this.u_vh_class = u_vh_class;
    }

    public int getL_seat_cap() {
        return l_seat_cap;
    }

    public void setL_seat_cap(int l_seat_cap) {
        this.l_seat_cap = l_seat_cap;
    }

    public int getU_seat_cap() {
        return u_seat_cap;
    }

    public void setU_seat_cap(int u_seat_cap) {
        this.u_seat_cap = u_seat_cap;
    }

    public int getL_unld_wt() {
        return l_unld_wt;
    }

    public void setL_unld_wt(int l_unld_wt) {
        this.l_unld_wt = l_unld_wt;
    }

    public int getU_unld_wt() {
        return u_unld_wt;
    }

    public void setU_unld_wt(int u_unld_wt) {
        this.u_unld_wt = u_unld_wt;
    }

    public int getL_ld_wt() {
        return l_ld_wt;
    }

    public void setL_ld_wt(int l_ld_wt) {
        this.l_ld_wt = l_ld_wt;
    }

    public int getU_ld_wt() {
        return u_ld_wt;
    }

    public void setU_ld_wt(int u_ld_wt) {
        this.u_ld_wt = u_ld_wt;
    }

    public String getPer_region() {
        return per_region;
    }

    public void setPer_region(String per_region) {
        this.per_region = per_region;
    }

    public String getPer_period() {
        return per_period;
    }

    public void setPer_period(String per_period) {
        this.per_period = per_period;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public int getFine() {
        return fine;
    }

    public void setFine(int fine) {
        this.fine = fine;
    }

    public String getPur_cd_descr() {
        return pur_cd_descr;
    }

    public void setPur_cd_descr(String pur_cd_descr) {
        this.pur_cd_descr = pur_cd_descr;
    }

    public String getPmt_type_descr() {
        return pmt_type_descr;
    }

    public void setPmt_type_descr(String pmt_type_descr) {
        this.pmt_type_descr = pmt_type_descr;
    }

    public String getPmt_catg_descr() {
        return pmt_catg_descr;
    }

    public void setPmt_catg_descr(String pmt_catg_descr) {
        this.pmt_catg_descr = pmt_catg_descr;
    }

    public String getL_vh_class_descr() {
        return l_vh_class_descr;
    }

    public void setL_vh_class_descr(String l_vh_class_descr) {
        this.l_vh_class_descr = l_vh_class_descr;
    }

    public String getU_vh_class_descr() {
        return u_vh_class_descr;
    }

    public void setU_vh_class_descr(String u_vh_class_descr) {
        this.u_vh_class_descr = u_vh_class_descr;
    }
}
