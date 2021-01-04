/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

/**
 *
 * @author NIC
 */
public class TechDataDobj implements Cloneable, Serializable {

    private String regn_no;
    private String request_dt;
    private int maker;
    private String maker_model;
    private int fuel;
    private int seat_cap;
    private int stand_cap;
    private int unld_wt;
    private int ld_wt;
    private float cubic_cap;
    private float hp;
    private int no_cyl;
    private String ac_fitted = "";
    private String audio_fitted = "";
    private String video_fitted = "";
    private int sale_amt;
    private String reason;
    private String state_cd;
    private int off_cd;
    private String appl_no;
    private Long mobile_no = 0L;
    private boolean pmtPanelRendered;
    private List<SelectItem> permitTypeList = new ArrayList<>();
    private List<SelectItem> permitCatgList = new ArrayList<>();
    private int permitType;
    private String permitTypeDesc;
    private int permitCatg;
    private String transportCatg;
    private String permitCatgDesc;
    private int norms;

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getRequest_dt() {
        return request_dt;
    }

    public void setRequest_dt(String request_dt) {
        this.request_dt = request_dt;
    }

    public int getMaker() {
        return maker;
    }

    public void setMaker(int maker) {
        this.maker = maker;
    }

    public String getMaker_model() {
        return maker_model;
    }

    public void setMaker_model(String maker_model) {
        this.maker_model = maker_model;
    }

    public int getFuel() {
        return fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public int getSeat_cap() {
        return seat_cap;
    }

    public void setSeat_cap(int seat_cap) {
        this.seat_cap = seat_cap;
    }

    public int getStand_cap() {
        return stand_cap;
    }

    public void setStand_cap(int stand_cap) {
        this.stand_cap = stand_cap;
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

    public float getCubic_cap() {
        return cubic_cap;
    }

    public void setCubic_cap(float cubic_cap) {
        this.cubic_cap = cubic_cap;
    }

    public float getHp() {
        return hp;
    }

    public void setHp(float hp) {
        this.hp = hp;
    }

    public int getNo_cyl() {
        return no_cyl;
    }

    public void setNo_cyl(int no_cyl) {
        this.no_cyl = no_cyl;
    }

    public String getAc_fitted() {
        return ac_fitted;
    }

    public void setAc_fitted(String ac_fitted) {
        this.ac_fitted = ac_fitted;
    }

    public String getAudio_fitted() {
        return audio_fitted;
    }

    public void setAudio_fitted(String audio_fitted) {
        this.audio_fitted = audio_fitted;
    }

    public String getVideo_fitted() {
        return video_fitted;
    }

    public void setVideo_fitted(String video_fitted) {
        this.video_fitted = video_fitted;
    }

    public int getSale_amt() {
        return sale_amt;
    }

    public void setSale_amt(int sale_amt) {
        this.sale_amt = sale_amt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public int getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public Long getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(Long mobile_no) {
        this.mobile_no = mobile_no;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the pmtPanelRendered
     */
    public boolean isPmtPanelRendered() {
        return pmtPanelRendered;
    }

    /**
     * @param pmtPanelRendered the pmtPanelRendered to set
     */
    public void setPmtPanelRendered(boolean pmtPanelRendered) {
        this.pmtPanelRendered = pmtPanelRendered;
    }

    /**
     * @return the permitType
     */
    public int getPermitType() {
        return permitType;
    }

    /**
     * @param permitType the permitType to set
     */
    public void setPermitType(int permitType) {
        this.permitType = permitType;
    }

    /**
     * @return the permitCatg
     */
    public int getPermitCatg() {
        return permitCatg;
    }

    /**
     * @param permitCatg the permitCatg to set
     */
    public void setPermitCatg(int permitCatg) {
        this.permitCatg = permitCatg;
    }

    /**
     * @return the permitTypeList
     */
    public List<SelectItem> getPermitTypeList() {
        return permitTypeList;
    }

    /**
     * @param permitTypeList the permitTypeList to set
     */
    public void setPermitTypeList(List<SelectItem> permitTypeList) {
        this.permitTypeList = permitTypeList;
    }

    /**
     * @return the permitCatgList
     */
    public List<SelectItem> getPermitCatgList() {
        return permitCatgList;
    }

    /**
     * @param permitCatgList the permitCatgList to set
     */
    public void setPermitCatgList(List<SelectItem> permitCatgList) {
        this.permitCatgList = permitCatgList;
    }

    /**
     * @return the transportCatg
     */
    public String getTransportCatg() {
        return transportCatg;
    }

    /**
     * @param transportCatg the transportCatg to set
     */
    public void setTransportCatg(String transportCatg) {
        this.transportCatg = transportCatg;
    }

    /**
     * @return the permitTypeDesc
     */
    public String getPermitTypeDesc() {
        return permitTypeDesc;
    }

    /**
     * @param permitTypeDesc the permitTypeDesc to set
     */
    public void setPermitTypeDesc(String permitTypeDesc) {
        this.permitTypeDesc = permitTypeDesc;
    }

    /**
     * @return the permitCatgDesc
     */
    public String getPermitCatgDesc() {
        return permitCatgDesc;
    }

    /**
     * @param permitCatgDesc the permitCatgDesc to set
     */
    public void setPermitCatgDesc(String permitCatgDesc) {
        this.permitCatgDesc = permitCatgDesc;
    }

    public int getNorms() {
        return norms;
    }

    public void setNorms(int norms) {
        this.norms = norms;
    }
}
