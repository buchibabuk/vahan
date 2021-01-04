/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author afzal
 */
public class TaxDNRequestDobj implements Serializable {

    private String state_cd;
    private int off_cd;
    private int pur_cd;
    private int VehType;
    private int vch_class_cd;
    private String vch_catg;
    private Date from_dt;
    private Date upto_dt;
    private String pur_cd_desc;
    private String VehType_desc;
    private String vch_class_desc;
    private String vch_catg_desc;
    private String generatedon;
    private String offName;
    private String from_dt_string;
    private String upto_dt_string;
    private String regn_no;
    private String regndate;

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
     * @return the pur_cd
     */
    public int getPur_cd() {
        return pur_cd;
    }

    /**
     * @param pur_cd the pur_cd to set
     */
    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    /**
     * @return the vch_class_cd
     */
    public int getVch_class_cd() {
        return vch_class_cd;
    }

    /**
     * @param vch_class_cd the vch_class_cd to set
     */
    public void setVch_class_cd(int vch_class_cd) {
        this.vch_class_cd = vch_class_cd;
    }

    /**
     * @return the vch_catg
     */
    public String getVch_catg() {
        return vch_catg;
    }

    /**
     * @param vch_catg the vch_catg to set
     */
    public void setVch_catg(String vch_catg) {
        this.vch_catg = vch_catg;
    }

    /**
     * @return the from_dt
     */
    public Date getFrom_dt() {
        return from_dt;
    }

    /**
     * @param from_dt the from_dt to set
     */
    public void setFrom_dt(Date from_dt) {
        this.from_dt = from_dt;
    }

    /**
     * @return the upto_dt
     */
    public Date getUpto_dt() {
        return upto_dt;
    }

    /**
     * @param upto_dt the upto_dt to set
     */
    public void setUpto_dt(Date upto_dt) {
        this.upto_dt = upto_dt;
    }

    /**
     * @return the VehType
     */
    public int getVehType() {
        return VehType;
    }

    /**
     * @param VehType the VehType to set
     */
    public void setVehType(int VehType) {
        this.VehType = VehType;
    }

    /**
     * @return the pur_cd_desc
     */
    public String getPur_cd_desc() {
        return pur_cd_desc;
    }

    /**
     * @param pur_cd_desc the pur_cd_desc to set
     */
    public void setPur_cd_desc(String pur_cd_desc) {
        this.pur_cd_desc = pur_cd_desc;
    }

    /**
     * @return the VehType_desc
     */
    public String getVehType_desc() {
        return VehType_desc;
    }

    /**
     * @param VehType_desc the VehType_desc to set
     */
    public void setVehType_desc(String VehType_desc) {
        this.VehType_desc = VehType_desc;
    }

    /**
     * @return the vch_class_desc
     */
    public String getVch_class_desc() {
        return vch_class_desc;
    }

    /**
     * @param vch_class_desc the vch_class_desc to set
     */
    public void setVch_class_desc(String vch_class_desc) {
        this.vch_class_desc = vch_class_desc;
    }

    /**
     * @return the vch_catg_desc
     */
    public String getVch_catg_desc() {
        return vch_catg_desc;
    }

    /**
     * @param vch_catg_desc the vch_catg_desc to set
     */
    public void setVch_catg_desc(String vch_catg_desc) {
        this.vch_catg_desc = vch_catg_desc;
    }

    /**
     * @return the generatedon
     */
    public String getGeneratedon() {
        return generatedon;
    }

    /**
     * @param generatedon the generatedon to set
     */
    public void setGeneratedon(String generatedon) {
        this.generatedon = generatedon;
    }

    /**
     * @return the offName
     */
    public String getOffName() {
        return offName;
    }

    /**
     * @param offName the offName to set
     */
    public void setOffName(String offName) {
        this.offName = offName;
    }

    /**
     * @return the from_dt_string
     */
    public String getFrom_dt_string() {
        return from_dt_string;
    }

    /**
     * @param from_dt_string the from_dt_string to set
     */
    public void setFrom_dt_string(String from_dt_string) {
        this.from_dt_string = from_dt_string;
    }

    /**
     * @return the upto_dt_string
     */
    public String getUpto_dt_string() {
        return upto_dt_string;
    }

    /**
     * @param upto_dt_string the upto_dt_string to set
     */
    public void setUpto_dt_string(String upto_dt_string) {
        this.upto_dt_string = upto_dt_string;
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
     * @return the regndate
     */
    public String getRegndate() {
        return regndate;
    }

    /**
     * @param regndate the regndate to set
     */
    public void setRegndate(String regndate) {
        this.regndate = regndate;
    }
}
