/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author Ashok Kumar <send2ashok@hotmail.com>
 */
public class Trailer_dobj implements Serializable, Cloneable {

    private String appl_no;
    private String regn_no;
    private String chasi_no;
    private String body_type;
    private int ld_wt;
    private int unld_wt;
    private String f_axle_descp;
    private String r_axle_descp;
    private String o_axle_descp;
    private String t_axle_descp;
    private int f_axle_weight;
    private int r_axle_weight;
    private int o_axle_weight;
    private int t_axle_weight;
    private String state_cd;
    private int off_cd;
    private int srNo;
    private boolean modifiable;
    private String dup_chassis;
    private String linkedRegnNo;
    private boolean disable;
    private boolean insertUpdateTrailerFlag;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    /**
     * @return the regn_no
     */
    public String getRegn_no() {
        return regn_no;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    /**
     * @return the chasi_no
     */
    public String getChasi_no() {
        return chasi_no;
    }

    /**
     * @param chasi_no the chasi_no to set
     */
    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
    }

    /**
     * @return the body_type
     */
    public String getBody_type() {
        return body_type;
    }

    /**
     * @param body_type the body_type to set
     */
    public void setBody_type(String body_type) {
        this.body_type = body_type;
    }

    /**
     * @return the ld_wt
     */
    public int getLd_wt() {
        return ld_wt;
    }

    /**
     * @param ld_wt the ld_wt to set
     */
    public void setLd_wt(int ld_wt) {
        this.ld_wt = ld_wt;
    }

    /**
     * @return the unld_wt
     */
    public int getUnld_wt() {
        return unld_wt;
    }

    /**
     * @param unld_wt the unld_wt to set
     */
    public void setUnld_wt(int unld_wt) {
        this.unld_wt = unld_wt;
    }

    /**
     * @return the f_axle_descp
     */
    public String getF_axle_descp() {
        return f_axle_descp;
    }

    /**
     * @param f_axle_descp the f_axle_descp to set
     */
    public void setF_axle_descp(String f_axle_descp) {
        this.f_axle_descp = f_axle_descp;
    }

    /**
     * @return the r_axle_descp
     */
    public String getR_axle_descp() {
        return r_axle_descp;
    }

    /**
     * @param r_axle_descp the r_axle_descp to set
     */
    public void setR_axle_descp(String r_axle_descp) {
        this.r_axle_descp = r_axle_descp;
    }

    /**
     * @return the o_axle_descp
     */
    public String getO_axle_descp() {
        return o_axle_descp;
    }

    /**
     * @param o_axle_descp the o_axle_descp to set
     */
    public void setO_axle_descp(String o_axle_descp) {
        this.o_axle_descp = o_axle_descp;
    }

    /**
     * @return the t_axle_descp
     */
    public String getT_axle_descp() {
        return t_axle_descp;
    }

    /**
     * @param t_axle_descp the t_axle_descp to set
     */
    public void setT_axle_descp(String t_axle_descp) {
        this.t_axle_descp = t_axle_descp;
    }

    /**
     * @return the f_axle_weight
     */
    public int getF_axle_weight() {
        return f_axle_weight;
    }

    /**
     * @param f_axle_weight the f_axle_weight to set
     */
    public void setF_axle_weight(int f_axle_weight) {
        this.f_axle_weight = f_axle_weight;
    }

    /**
     * @return the r_axle_weight
     */
    public int getR_axle_weight() {
        return r_axle_weight;
    }

    /**
     * @param r_axle_weight the r_axle_weight to set
     */
    public void setR_axle_weight(int r_axle_weight) {
        this.r_axle_weight = r_axle_weight;
    }

    /**
     * @return the o_axle_weight
     */
    public int getO_axle_weight() {
        return o_axle_weight;
    }

    /**
     * @param o_axle_weight the o_axle_weight to set
     */
    public void setO_axle_weight(int o_axle_weight) {
        this.o_axle_weight = o_axle_weight;
    }

    /**
     * @return the t_axle_weight
     */
    public int getT_axle_weight() {
        return t_axle_weight;
    }

    /**
     * @param t_axle_weight the t_axle_weight to set
     */
    public void setT_axle_weight(int t_axle_weight) {
        this.t_axle_weight = t_axle_weight;
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
     * @return the srNo
     */
    public int getSrNo() {
        return srNo;
    }

    /**
     * @param srNo the srNo to set
     */
    public void setSrNo(int srNo) {
        this.srNo = srNo;
    }

    /**
     * @return the modifiable
     */
    public boolean isModifiable() {
        return modifiable;
    }

    /**
     * @param modifiable the modifiable to set
     */
    public void setModifiable(boolean modifiable) {
        this.modifiable = modifiable;
    }

    /**
     * @return the dup_chassis
     */
    public String getDup_chassis() {
        return dup_chassis;
    }

    /**
     * @param dup_chassis the dup_chassis to set
     */
    public void setDup_chassis(String dup_chassis) {
        this.dup_chassis = dup_chassis;
    }

    /**
     * @return the linkedRegnNo
     */
    public String getLinkedRegnNo() {
        return linkedRegnNo;
    }

    /**
     * @param linkedRegnNo the linkedRegnNo to set
     */
    public void setLinkedRegnNo(String linkedRegnNo) {
        this.linkedRegnNo = linkedRegnNo;
    }

    /**
     * @return the disable
     */
    public boolean isDisable() {
        return disable;
    }

    /**
     * @param disable the disable to set
     */
    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public boolean isInsertUpdateTrailerFlag() {
        return insertUpdateTrailerFlag;
    }

    public void setInsertUpdateTrailerFlag(boolean insertUpdateTrailerFlag) {
        this.insertUpdateTrailerFlag = insertUpdateTrailerFlag;
    }
    
}
