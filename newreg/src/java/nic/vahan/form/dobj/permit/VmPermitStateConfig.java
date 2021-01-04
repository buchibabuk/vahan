/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tranC106
 */
public class VmPermitStateConfig implements Serializable {

    private String state_cd;
    private int temp_days_valid_upto;
    private int temp_weeks_valid_upto;
    private int spec_days_valid_upto;
    private int spec_weeks_valid_upto;
    private int sc_renew_before_days;
    private int cc_renew_before_days;
    private int ai_renew_before_days;
    private int psv_renew_before_days;
    private int gp_renew_before_days;
    private int np_renew_before_days;
    private String sc_rule_heading;
    private String sc_formno_heading;
    private String cc_rule_heading;
    private String cc_formno_heading;
    private String aitp_rule_heading;
    private String aitp_formno_heading;
    private String psvp_rule_heading;
    private String psvp_formno_heading;
    private String gp_rule_heading;
    private String gp_formno_heading;
    private String np_rule_heading;
    private String np_formno_heading;
    private String temp_rule_heading;
    private String temp_formno_heading;
    private String spec_rule_heading;
    private String spec_formno_heading;
    private String auth_aitp_rule_heading;
    private String auth_aitp_formno_heading;
    private String auth_np_rule_heading;
    private String auth_np_formno_heading;
    private String note_in_footer;
    private int temp_days_valid_from;
    private int temp_weeks_valid_from;
    private int spec_days_valid_from;
    private int spec_weeks_valid_from;
    private int sc_renew_after_days;
    private int cc_renew_after_days;
    private int ai_renew_after_days;
    private int psv_renew_after_days;
    private int gp_renew_after_days;
    private int np_renew_after_days;
    private String renewal_of_permit_valid_from_flag;
    private boolean temp_route_area;
    private boolean genrate_ol_appl;
    private boolean permanent_permit_valid;
    private String allowed_surr_pur_cd;
    private boolean temp_pmt_type;
    private boolean renew_temp_pmt;
    private boolean spl_pmt_route;
    private String sur_pur_cd;
    private String[] surPurList;
    private List salectedPurList = new ArrayList();

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public int getTemp_days_valid_upto() {
        return temp_days_valid_upto;
    }

    public void setTemp_days_valid_upto(int temp_days_valid_upto) {
        this.temp_days_valid_upto = temp_days_valid_upto;
    }

    public int getTemp_weeks_valid_upto() {
        return temp_weeks_valid_upto;
    }

    public void setTemp_weeks_valid_upto(int temp_weeks_valid_upto) {
        this.temp_weeks_valid_upto = temp_weeks_valid_upto;
    }

    public int getSpec_days_valid_upto() {
        return spec_days_valid_upto;
    }

    public void setSpec_days_valid_upto(int spec_days_valid_upto) {
        this.spec_days_valid_upto = spec_days_valid_upto;
    }

    public int getSpec_weeks_valid_upto() {
        return spec_weeks_valid_upto;
    }

    public void setSpec_weeks_valid_upto(int spec_weeks_valid_upto) {
        this.spec_weeks_valid_upto = spec_weeks_valid_upto;
    }

    public int getSc_renew_before_days() {
        return sc_renew_before_days;
    }

    public void setSc_renew_before_days(int sc_renew_before_days) {
        this.sc_renew_before_days = sc_renew_before_days;
    }

    public int getCc_renew_before_days() {
        return cc_renew_before_days;
    }

    public void setCc_renew_before_days(int cc_renew_before_days) {
        this.cc_renew_before_days = cc_renew_before_days;
    }

    public int getAi_renew_before_days() {
        return ai_renew_before_days;
    }

    public void setAi_renew_before_days(int ai_renew_before_days) {
        this.ai_renew_before_days = ai_renew_before_days;
    }

    public int getPsv_renew_before_days() {
        return psv_renew_before_days;
    }

    public void setPsv_renew_before_days(int psv_renew_before_days) {
        this.psv_renew_before_days = psv_renew_before_days;
    }

    public int getGp_renew_before_days() {
        return gp_renew_before_days;
    }

    public void setGp_renew_before_days(int gp_renew_before_days) {
        this.gp_renew_before_days = gp_renew_before_days;
    }

    public int getNp_renew_before_days() {
        return np_renew_before_days;
    }

    public void setNp_renew_before_days(int np_renew_before_days) {
        this.np_renew_before_days = np_renew_before_days;
    }

    public String getSc_rule_heading() {
        return sc_rule_heading;
    }

    public void setSc_rule_heading(String sc_rule_heading) {
        this.sc_rule_heading = sc_rule_heading;
    }

    public String getSc_formno_heading() {
        return sc_formno_heading;
    }

    public void setSc_formno_heading(String sc_formno_heading) {
        this.sc_formno_heading = sc_formno_heading;
    }

    public String getCc_rule_heading() {
        return cc_rule_heading;
    }

    public void setCc_rule_heading(String cc_rule_heading) {
        this.cc_rule_heading = cc_rule_heading;
    }

    public String getCc_formno_heading() {
        return cc_formno_heading;
    }

    public void setCc_formno_heading(String cc_formno_heading) {
        this.cc_formno_heading = cc_formno_heading;
    }

    public String getAitp_rule_heading() {
        return aitp_rule_heading;
    }

    public void setAitp_rule_heading(String aitp_rule_heading) {
        this.aitp_rule_heading = aitp_rule_heading;
    }

    public String getAitp_formno_heading() {
        return aitp_formno_heading;
    }

    public void setAitp_formno_heading(String aitp_formno_heading) {
        this.aitp_formno_heading = aitp_formno_heading;
    }

    public String getPsvp_rule_heading() {
        return psvp_rule_heading;
    }

    public void setPsvp_rule_heading(String psvp_rule_heading) {
        this.psvp_rule_heading = psvp_rule_heading;
    }

    public String getPsvp_formno_heading() {
        return psvp_formno_heading;
    }

    public void setPsvp_formno_heading(String psvp_formno_heading) {
        this.psvp_formno_heading = psvp_formno_heading;
    }

    public String getGp_rule_heading() {
        return gp_rule_heading;
    }

    public void setGp_rule_heading(String gp_rule_heading) {
        this.gp_rule_heading = gp_rule_heading;
    }

    public String getGp_formno_heading() {
        return gp_formno_heading;
    }

    public void setGp_formno_heading(String gp_formno_heading) {
        this.gp_formno_heading = gp_formno_heading;
    }

    public String getNp_rule_heading() {
        return np_rule_heading;
    }

    public void setNp_rule_heading(String np_rule_heading) {
        this.np_rule_heading = np_rule_heading;
    }

    public String getNp_formno_heading() {
        return np_formno_heading;
    }

    public void setNp_formno_heading(String np_formno_heading) {
        this.np_formno_heading = np_formno_heading;
    }

    public String getTemp_rule_heading() {
        return temp_rule_heading;
    }

    public void setTemp_rule_heading(String temp_rule_heading) {
        this.temp_rule_heading = temp_rule_heading;
    }

    public String getTemp_formno_heading() {
        return temp_formno_heading;
    }

    public void setTemp_formno_heading(String temp_formno_heading) {
        this.temp_formno_heading = temp_formno_heading;
    }

    public String getSpec_rule_heading() {
        return spec_rule_heading;
    }

    public void setSpec_rule_heading(String spec_rule_heading) {
        this.spec_rule_heading = spec_rule_heading;
    }

    public String getSpec_formno_heading() {
        return spec_formno_heading;
    }

    public void setSpec_formno_heading(String spec_formno_heading) {
        this.spec_formno_heading = spec_formno_heading;
    }

    public String getAuth_aitp_rule_heading() {
        return auth_aitp_rule_heading;
    }

    public void setAuth_aitp_rule_heading(String auth_aitp_rule_heading) {
        this.auth_aitp_rule_heading = auth_aitp_rule_heading;
    }

    public String getAuth_aitp_formno_heading() {
        return auth_aitp_formno_heading;
    }

    public void setAuth_aitp_formno_heading(String auth_aitp_formno_heading) {
        this.auth_aitp_formno_heading = auth_aitp_formno_heading;
    }

    public String getAuth_np_rule_heading() {
        return auth_np_rule_heading;
    }

    public void setAuth_np_rule_heading(String auth_np_rule_heading) {
        this.auth_np_rule_heading = auth_np_rule_heading;
    }

    public String getAuth_np_formno_heading() {
        return auth_np_formno_heading;
    }

    public void setAuth_np_formno_heading(String auth_np_formno_heading) {
        this.auth_np_formno_heading = auth_np_formno_heading;
    }

    public String getNote_in_footer() {
        return note_in_footer;
    }

    public void setNote_in_footer(String note_in_footer) {
        this.note_in_footer = note_in_footer;
    }

    public int getTemp_days_valid_from() {
        return temp_days_valid_from;
    }

    public void setTemp_days_valid_from(int temp_days_valid_from) {
        this.temp_days_valid_from = temp_days_valid_from;
    }

    public int getTemp_weeks_valid_from() {
        return temp_weeks_valid_from;
    }

    public void setTemp_weeks_valid_from(int temp_weeks_valid_from) {
        this.temp_weeks_valid_from = temp_weeks_valid_from;
    }

    public int getSpec_days_valid_from() {
        return spec_days_valid_from;
    }

    public void setSpec_days_valid_from(int spec_days_valid_from) {
        this.spec_days_valid_from = spec_days_valid_from;
    }

    public int getSpec_weeks_valid_from() {
        return spec_weeks_valid_from;
    }

    public void setSpec_weeks_valid_from(int spec_weeks_valid_from) {
        this.spec_weeks_valid_from = spec_weeks_valid_from;
    }

    public int getSc_renew_after_days() {
        return sc_renew_after_days;
    }

    public void setSc_renew_after_days(int sc_renew_after_days) {
        this.sc_renew_after_days = sc_renew_after_days;
    }

    public int getCc_renew_after_days() {
        return cc_renew_after_days;
    }

    public void setCc_renew_after_days(int cc_renew_after_days) {
        this.cc_renew_after_days = cc_renew_after_days;
    }

    public int getAi_renew_after_days() {
        return ai_renew_after_days;
    }

    public void setAi_renew_after_days(int ai_renew_after_days) {
        this.ai_renew_after_days = ai_renew_after_days;
    }

    public int getPsv_renew_after_days() {
        return psv_renew_after_days;
    }

    public void setPsv_renew_after_days(int psv_renew_after_days) {
        this.psv_renew_after_days = psv_renew_after_days;
    }

    public int getGp_renew_after_days() {
        return gp_renew_after_days;
    }

    public void setGp_renew_after_days(int gp_renew_after_days) {
        this.gp_renew_after_days = gp_renew_after_days;
    }

    public int getNp_renew_after_days() {
        return np_renew_after_days;
    }

    public void setNp_renew_after_days(int np_renew_after_days) {
        this.np_renew_after_days = np_renew_after_days;
    }

    public String getRenewal_of_permit_valid_from_flag() {
        return renewal_of_permit_valid_from_flag;
    }

    public void setRenewal_of_permit_valid_from_flag(String renewal_of_permit_valid_from_flag) {
        this.renewal_of_permit_valid_from_flag = renewal_of_permit_valid_from_flag;
    }

    public boolean isTemp_route_area() {
        return temp_route_area;
    }

    public void setTemp_route_area(boolean temp_route_area) {
        this.temp_route_area = temp_route_area;
    }

    public boolean isGenrate_ol_appl() {
        return genrate_ol_appl;
    }

    public void setGenrate_ol_appl(boolean genrate_ol_appl) {
        this.genrate_ol_appl = genrate_ol_appl;
    }

    public boolean isPermanent_permit_valid() {
        return permanent_permit_valid;
    }

    public void setPermanent_permit_valid(boolean permanent_permit_valid) {
        this.permanent_permit_valid = permanent_permit_valid;
    }

    public String getAllowed_surr_pur_cd() {
        return allowed_surr_pur_cd;
    }

    public void setAllowed_surr_pur_cd(String allowed_surr_pur_cd) {
        this.allowed_surr_pur_cd = allowed_surr_pur_cd;
    }

    public boolean isTemp_pmt_type() {
        return temp_pmt_type;
    }

    public void setTemp_pmt_type(boolean temp_pmt_type) {
        this.temp_pmt_type = temp_pmt_type;
    }

    public boolean isRenew_temp_pmt() {
        return renew_temp_pmt;
    }

    public void setRenew_temp_pmt(boolean renew_temp_pmt) {
        this.renew_temp_pmt = renew_temp_pmt;
    }

    public String getSur_pur_cd() {
        return sur_pur_cd;
    }

    public void setSur_pur_cd(String sur_pur_cd) {
        this.sur_pur_cd = sur_pur_cd;
    }

    public String[] getSurPurList() {
        return surPurList;
    }

    public void setSurPurList(String[] surPurList) {
        this.surPurList = surPurList;
    }

    public List getSalectedPurList() {
        return salectedPurList;
    }

    public void setSalectedPurList(List salectedPurList) {
        this.salectedPurList = salectedPurList;
    }

    public boolean isSpl_pmt_route() {
        return spl_pmt_route;
    }

    public void setSpl_pmt_route(boolean spl_pmt_route) {
        this.spl_pmt_route = spl_pmt_route;
    }
}
