/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;

/**
 *
 * @author R Gautam
 */
public class PermitFeeRequiredDataDobj implements Serializable {

    private String state_cd = "";
    private int pur_cd = 0;
    private int pmt_catg = 0;
    private int pmt_type = 0;
    private String period_mode = "";
    private int period = 0;
    private String region_covered = "";
    private int vh_class = 0;
    private int seat_cap = 0;
    private int unld_wt = 0;
    private int ld_wt = 0;
    private int stand_cap = 0;
    private int sleeper_cap = 0;
    private int own_catg = 0;
    private String regn_no = "";
    private String appl_no = "";
    private String validFromTemp = "";
    private String validUptoTemp = "";
    private String excemptedFlag = "";
    private int trans_pur_cd = 0;
    private String dupPmtReason = "";
    private int service_type = 0;
    private int route_length = 0;
    private int fuel_type = 0;
    private int other_criteria = 0;
    private int owner_cd = 0;
    private String validFromPrvDate = "";
    private String validUpToPrvDate = "";
    private String validAuthUpToDate = "";
    private boolean renderRecursiveAuthfee = false;
    private boolean regn_state;

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

    public int getPmt_catg() {
        return pmt_catg;
    }

    public void setPmt_catg(int pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    public int getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(int pmt_type) {
        this.pmt_type = pmt_type;
    }

    public String getPeriod_mode() {
        return period_mode;
    }

    public void setPeriod_mode(String period_mode) {
        this.period_mode = period_mode;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getRegion_covered() {
        return region_covered;
    }

    public void setRegion_covered(String region_covered) {
        this.region_covered = region_covered;
    }

    public int getVh_class() {
        return vh_class;
    }

    public void setVh_class(int vh_class) {
        this.vh_class = vh_class;
    }

    public int getSeat_cap() {
        return seat_cap;
    }

    public void setSeat_cap(int seat_cap) {
        this.seat_cap = seat_cap;
    }

    public int getUnld_wt() {
        return unld_wt;
    }

    public void setUnld_wt(int unld_wt) {
        this.unld_wt = unld_wt;
    }

    public int getLd_wt() {
        return ld_wt;
    }

    public void setLd_wt(int ld_wt) {
        this.ld_wt = ld_wt;
    }

    public int getStand_cap() {
        return stand_cap;
    }

    public void setStand_cap(int stand_cap) {
        this.stand_cap = stand_cap;
    }

    public int getSleeper_cap() {
        return sleeper_cap;
    }

    public void setSleeper_cap(int sleeper_cap) {
        this.sleeper_cap = sleeper_cap;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getValidFromTemp() {
        return validFromTemp;
    }

    public void setValidFromTemp(String validFromTemp) {
        this.validFromTemp = validFromTemp;
    }

    public String getValidUptoTemp() {
        return validUptoTemp;
    }

    public void setValidUptoTemp(String validUptoTemp) {
        this.validUptoTemp = validUptoTemp;
    }

    public int getOwn_catg() {
        return own_catg;
    }

    public void setOwn_catg(int own_catg) {
        this.own_catg = own_catg;
    }

    public String getExcemptedFlag() {
        return excemptedFlag;
    }

    public void setExcemptedFlag(String excemptedFlag) {
        this.excemptedFlag = excemptedFlag;
    }

    /**
     * @return the trans_pur_cd
     */
    public int getTrans_pur_cd() {
        return trans_pur_cd;
    }

    /**
     * @param trans_pur_cd the trans_pur_cd to set
     */
    public void setTrans_pur_cd(int trans_pur_cd) {
        this.trans_pur_cd = trans_pur_cd;
    }

    public String getDupPmtReason() {
        return dupPmtReason;
    }

    public void setDupPmtReason(String dupPmtReason) {
        this.dupPmtReason = dupPmtReason;
    }

    public int getService_type() {
        return service_type;
    }

    public void setService_type(int service_type) {
        this.service_type = service_type;
    }

    /**
     * @return the route_length
     */
    public int getRoute_length() {
        return route_length;
    }

    /**
     * @param route_length the route_length to set
     */
    public void setRoute_length(int route_length) {
        this.route_length = route_length;
    }

    public int getFuel_type() {
        return fuel_type;
    }

    public void setFuel_type(int fuel_type) {
        this.fuel_type = fuel_type;
    }

    public int getOther_criteria() {
        return other_criteria;
    }

    public void setOther_criteria(int other_criteria) {
        this.other_criteria = other_criteria;
    }

    public int getOwner_cd() {
        return owner_cd;
    }

    public void setOwner_cd(int owner_cd) {
        this.owner_cd = owner_cd;
    }

    public String getValidFromPrvDate() {
        return validFromPrvDate;
    }

    public void setValidFromPrvDate(String validFromPrvDate) {
        this.validFromPrvDate = validFromPrvDate;
    }

    public boolean isRenderRecursiveAuthfee() {
        return renderRecursiveAuthfee;
    }

    public void setRenderRecursiveAuthfee(boolean renderRecursiveAuthfee) {
        this.renderRecursiveAuthfee = renderRecursiveAuthfee;
    }

    public String getValidUpToPrvDate() {
        return validUpToPrvDate;
    }

    public void setValidUpToPrvDate(String validUpToPrvDate) {
        this.validUpToPrvDate = validUpToPrvDate;
    }

    public String getValidAuthUpToDate() {
        return validAuthUpToDate;
    }

    public void setValidAuthUpToDate(String validAuthUpToDate) {
        this.validAuthUpToDate = validAuthUpToDate;
    }

    public boolean isRegn_state() {
        return regn_state;
    }

    public void setRegn_state(boolean regn_state) {
        this.regn_state = regn_state;
    }
}
