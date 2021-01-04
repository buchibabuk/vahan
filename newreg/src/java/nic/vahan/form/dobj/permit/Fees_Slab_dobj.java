/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;

/**
 *
 * @author MRD
 */
public class Fees_Slab_dobj implements Serializable {

    private int sr_no;
    private String trans_pur_cd = "26";
    private String condition_formula = "";
    private String fee_rate_formula = "";
    private String fine_rate_formula = "";
    private String check_max_amt = "false";
    private String per_region_count = "false";
    private String fine_max_amt = "0";
    private String per_route_count = "false";
    private String period_mode = "D";
    private String trans_pur_descr = "";

    public String getPer_region_count() {
        return per_region_count;
    }

    public void setPer_region_count(String per_region_count) {
        this.per_region_count = per_region_count;
    }

    public String getPer_route_count() {
        return per_route_count;
    }

    public void setPer_route_count(String per_route_count) {
        this.per_route_count = per_route_count;
    }

    public String getCheck_max_amt() {
        return check_max_amt;
    }

    public void setCheck_max_amt(String check_max_amt) {
        this.check_max_amt = check_max_amt;
    }

    public String getFine_max_amt() {
        return fine_max_amt;
    }

    public void setFine_max_amt(String fine_max_amt) {
        this.fine_max_amt = fine_max_amt;
    }

    public int getSr_no() {
        return sr_no;
    }

    public void setSr_no(int sr_no) {
        this.sr_no = sr_no;
    }

    public String getTrans_pur_cd() {
        return trans_pur_cd;
    }

    public void setTrans_pur_cd(String trans_pur_cd) {
        this.trans_pur_cd = trans_pur_cd;
    }

    public String getTrans_pur_descr() {
        return trans_pur_descr;
    }

    public void setTrans_pur_descr(String trans_pur_descr) {
        this.trans_pur_descr = trans_pur_descr;
    }
    public String getCondition_formula() {
        return condition_formula;
    }

    public void setCondition_formula(String condition_formula) {
        this.condition_formula = condition_formula;
    }

    public String getFee_rate_formula() {
        return fee_rate_formula;
    }

    public void setFee_rate_formula(String fee_rate_formula) {
        this.fee_rate_formula = fee_rate_formula;
    }

    public String getFine_rate_formula() {
        return fine_rate_formula;
    }

    public void setFine_rate_formula(String fine_rate_formula) {
        this.fine_rate_formula = fine_rate_formula;
    }

    public String getPeriod_mode() {
        return period_mode;
    }

    public void setPeriod_mode(String period_mode) {
        this.period_mode = period_mode;
    }
}
