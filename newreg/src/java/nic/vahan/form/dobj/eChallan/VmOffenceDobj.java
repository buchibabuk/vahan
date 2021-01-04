/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;

public class VmOffenceDobj implements Cloneable, Serializable {

    private int off_code;
    private String offence_desc;
    private String mva_clause;
    private String cmvr_clause;
    private String smvr_clause;
    private String addl_desc;
    private String punishment1;
    private String punishment2;
    private String isactivefor_tpt;
    private String isactivefor_police;
    private String state_cd;

    public VmOffenceDobj(int off_code, String offence_desc, String mva_clause, String cmvr_clause, String smvr_clause, String addl_desc, String punishment1, String punishment2, String isactivefor_tpt, String isactivefor_police) {

        this.off_code = off_code;
        this.offence_desc = offence_desc;
        this.mva_clause = mva_clause;
        this.cmvr_clause = cmvr_clause;
        this.smvr_clause = smvr_clause;
        this.addl_desc = addl_desc;
        this.punishment1 = punishment1;
        this.punishment2 = punishment2;
        this.isactivefor_tpt = isactivefor_tpt;
        this.isactivefor_police = isactivefor_police;
    }

    public VmOffenceDobj() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public String getOffence_desc() {
        return offence_desc;
    }

    public void setOffence_desc(String offence_desc) {
        this.offence_desc = offence_desc;
    }

    public String getMva_clause() {
        return mva_clause;
    }

    public void setMva_clause(String mva_clause) {
        this.mva_clause = mva_clause;
    }

    public String getCmvr_clause() {
        return cmvr_clause;
    }

    public void setCmvr_clause(String cmvr_clause) {
        this.cmvr_clause = cmvr_clause;
    }

    public String getSmvr_clause() {
        return smvr_clause;
    }

    public void setSmvr_clause(String smvr_clause) {
        this.smvr_clause = smvr_clause;
    }

    public String getAddl_desc() {
        return addl_desc;
    }

    public void setAddl_desc(String addl_desc) {
        this.addl_desc = addl_desc;
    }

    public String getPunishment1() {
        return punishment1;
    }

    public void setPunishment1(String punishment1) {
        this.punishment1 = punishment1;
    }

    public String getPunishment2() {
        return punishment2;
    }

    public void setPunishment2(String punishment2) {
        this.punishment2 = punishment2;
    }

    public String getIsactivefor_tpt() {
        return isactivefor_tpt;
    }

    public void setIsactivefor_tpt(String isactivefor_tpt) {
        this.isactivefor_tpt = isactivefor_tpt;
    }

    public String getIsactivefor_police() {
        return isactivefor_police;
    }

    public void setIsactivefor_police(String isactivefor_police) {
        this.isactivefor_police = isactivefor_police;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public int getOff_code() {
        return off_code;
    }

    public void setOff_code(int off_code) {
        this.off_code = off_code;
    }
}
