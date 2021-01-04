/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

public class AltDobj implements Cloneable, Serializable {

    private String appl_no;
    private String regn_no;
    private String chasi_no;
    private String eng_no;
    private int ld_wt;
    private int unld_wt;
    private int vh_class;
    private int no_cyl;
    private float hp;
    private int seat_cap;
    private int stand_cap;
    private int sleeper_cap;
    private float cubic_cap;
    private int wheelbase;
    private float floor_area;
    private int fuel;
    private String body_type;
    private String color;
    private String op_dt;
    private String state_cd;
    private int off_cd;
    private String vch_catg;
    private String ac_fitted;
    private String audio_fitted;
    private String video_fitted;
    private int height;
    private int length;
    private int width;
    private Date fit_date_upto;
    private String linkedRegnNo;
    private boolean oldVahanRecords;
    private boolean pushBkSeatRender = false;
    private int push_bk_seat = 0;
    private int ordinary_seat = 0;

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
     * @return the eng_no
     */
    public String getEng_no() {
        return eng_no;
    }

    /**
     * @param eng_no the eng_no to set
     */
    public void setEng_no(String eng_no) {
        this.eng_no = eng_no;
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
     * @return the vh_class
     */
    public int getVh_class() {
        return vh_class;
    }

    /**
     * @param vh_class the vh_class to set
     */
    public void setVh_class(int vh_class) {
        this.vh_class = vh_class;
    }

    /**
     * @return the no_cyl
     */
    public int getNo_cyl() {
        return no_cyl;
    }

    /**
     * @param no_cyl the no_cyl to set
     */
    public void setNo_cyl(int no_cyl) {
        this.no_cyl = no_cyl;
    }

    /**
     * @return the hp
     */
    public float getHp() {
        return hp;
    }

    /**
     * @param hp the hp to set
     */
    public void setHp(float hp) {
        this.hp = hp;
    }

    /**
     * @return the seat_cap
     */
    public int getSeat_cap() {
        return seat_cap;
    }

    /**
     * @param seat_cap the seat_cap to set
     */
    public void setSeat_cap(int seat_cap) {
        this.seat_cap = seat_cap;
    }

    /**
     * @return the stand_cap
     */
    public int getStand_cap() {
        return stand_cap;
    }

    /**
     * @param stand_cap the stand_cap to set
     */
    public void setStand_cap(int stand_cap) {
        this.stand_cap = stand_cap;
    }

    /**
     * @return the sleeper_cap
     */
    public int getSleeper_cap() {
        return sleeper_cap;
    }

    /**
     * @param sleeper_cap the sleeper_cap to set
     */
    public void setSleeper_cap(int sleeper_cap) {
        this.sleeper_cap = sleeper_cap;
    }

    /**
     * @return the cubic_cap
     */
    public float getCubic_cap() {
        return cubic_cap;
    }

    /**
     * @param cubic_cap the cubic_cap to set
     */
    public void setCubic_cap(float cubic_cap) {
        this.cubic_cap = cubic_cap;
    }

    /**
     * @return the wheelbase
     */
    public int getWheelbase() {
        return wheelbase;
    }

    /**
     * @param wheelbase the wheelbase to set
     */
    public void setWheelbase(int wheelbase) {
        this.wheelbase = wheelbase;
    }

    /**
     * @return the floor_area
     */
    public float getFloor_area() {
        return floor_area;
    }

    /**
     * @param floor_area the floor_area to set
     */
    public void setFloor_area(float floor_area) {
        this.floor_area = floor_area;
    }

    /**
     * @return the fuel
     */
    public int getFuel() {
        return fuel;
    }

    /**
     * @param fuel the fuel to set
     */
    public void setFuel(int fuel) {
        this.fuel = fuel;
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
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return the op_dt
     */
    public String getOp_dt() {
        return op_dt;
    }

    /**
     * @param op_dt the op_dt to set
     */
    public void setOp_dt(String op_dt) {
        this.op_dt = op_dt;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
     * @return the ac_fitted
     */
    public String getAc_fitted() {
        return ac_fitted;
    }

    /**
     * @param ac_fitted the ac_fitted to set
     */
    public void setAc_fitted(String ac_fitted) {
        this.ac_fitted = ac_fitted;
    }

    /**
     * @return the audio_fitted
     */
    public String getAudio_fitted() {
        return audio_fitted;
    }

    /**
     * @param audio_fitted the audio_fitted to set
     */
    public void setAudio_fitted(String audio_fitted) {
        this.audio_fitted = audio_fitted;
    }

    /**
     * @return the video_fitted
     */
    public String getVideo_fitted() {
        return video_fitted;
    }

    /**
     * @param video_fitted the video_fitted to set
     */
    public void setVideo_fitted(String video_fitted) {
        this.video_fitted = video_fitted;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the fit_date_upto
     */
    public Date getFit_date_upto() {
        return fit_date_upto;
    }

    /**
     * @param fit_date_upto the fit_date_upto to set
     */
    public void setFit_date_upto(Date fit_date_upto) {
        this.fit_date_upto = fit_date_upto;
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
     * @return the oldVahanRecords
     */
    public boolean isOldVahanRecords() {
        return oldVahanRecords;
    }

    /**
     * @param oldVahanRecords the oldVahanRecords to set
     */
    public void setOldVahanRecords(boolean oldVahanRecords) {
        this.oldVahanRecords = oldVahanRecords;
    }

    public boolean isPushBkSeatRender() {
        return pushBkSeatRender;
    }

    public void setPushBkSeatRender(boolean pushBkSeatRender) {
        this.pushBkSeatRender = pushBkSeatRender;
    }

    public int getPush_bk_seat() {
        return push_bk_seat;
    }

    public void setPush_bk_seat(int push_bk_seat) {
        this.push_bk_seat = push_bk_seat;
    }

    public int getOrdinary_seat() {
        return ordinary_seat;
    }

    public void setOrdinary_seat(int ordinary_seat) {
        this.ordinary_seat = ordinary_seat;
    }
}
