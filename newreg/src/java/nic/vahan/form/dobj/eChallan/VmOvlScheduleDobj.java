/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

public class VmOvlScheduleDobj implements Cloneable {

    private int srno;
    private int ld_wt_lower;
    private int ld_wt_upper;
    private String ld_wt_unit;
    private int flat_cf_amt;
    private int unit_cf_amt;
    private int wt_unit_val;
    private String sate_cd;

    public VmOvlScheduleDobj(int srno, int ld_wt_lower, int ld_wt_upper, String ld_wt_unit, int flat_cf_amt, int unit_cf_amt, int wt_unit_val) {
        this.srno = srno;
        this.ld_wt_lower = ld_wt_lower;
        this.ld_wt_upper = ld_wt_upper;
        this.ld_wt_unit = ld_wt_unit;
        this.flat_cf_amt = flat_cf_amt;
        this.unit_cf_amt = unit_cf_amt;
        this.wt_unit_val = wt_unit_val;
    }

    public VmOvlScheduleDobj() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public int getSrno() {
        return srno;
    }

    public void setSrno(int srno) {
        this.srno = srno;
    }

    public int getLd_wt_lower() {
        return ld_wt_lower;
    }

    public void setLd_wt_lower(int ld_wt_lower) {
        this.ld_wt_lower = ld_wt_lower;
    }

    public int getLd_wt_upper() {
        return ld_wt_upper;
    }

    public void setLd_wt_upper(int ld_wt_upper) {
        this.ld_wt_upper = ld_wt_upper;
    }

    public String getLd_wt_unit() {
        return ld_wt_unit;
    }

    public void setLd_wt_unit(String ld_wt_unit) {
        this.ld_wt_unit = ld_wt_unit;
    }

    public int getFlat_cf_amt() {
        return flat_cf_amt;
    }

    public void setFlat_cf_amt(int flat_cf_amt) {
        this.flat_cf_amt = flat_cf_amt;
    }

    public int getUnit_cf_amt() {
        return unit_cf_amt;
    }

    public void setUnit_cf_amt(int unit_cf_amt) {
        this.unit_cf_amt = unit_cf_amt;
    }

    public int getWt_unit_val() {
        return wt_unit_val;
    }

    public void setWt_unit_val(int wt_unit_val) {
        this.wt_unit_val = wt_unit_val;
    }

    public String getSate_cd() {
        return sate_cd;
    }

    public void setSate_cd(String sate_cd) {
        this.sate_cd = sate_cd;
    }
}
