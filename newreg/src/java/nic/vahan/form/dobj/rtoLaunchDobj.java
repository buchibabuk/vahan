/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author nicsi
 */
public class rtoLaunchDobj implements Serializable {

    private int off_cd;
    private String off_name;
    private String off_add1;
    private String off_add2;
    private String pin_cd;
    private long village_cd;
    private int taluk_cd;
    private long mobile_no;
    private String landline;
    private String email_id;
    private int off_under_cd;
    private int off_type_cd;
    private String state_cd;
    private Date vow4_launch_date;
    private String hsrp;
    private String smartCard;
    private boolean isHsrp;
    private boolean isSmartCard;
    private String paperRC;
    private String plasticCard;
    private String rcType;
    private String rcOption;
    private boolean isOldHsrp;
    private String old_veh_hsrp;
    private String plasticCardpvc;

    public int getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    public String getOff_name() {
        return off_name;
    }

    public void setOff_name(String off_name) {
        this.off_name = off_name;
    }

    public String getOff_add1() {
        return off_add1;
    }

    public void setOff_add1(String off_add1) {
        this.off_add1 = off_add1;
    }

    public String getOff_add2() {
        return off_add2;
    }

    public void setOff_add2(String off_add2) {
        this.off_add2 = off_add2;
    }

    public String getPin_cd() {
        return pin_cd;
    }

    public void setPin_cd(String pin_cd) {
        this.pin_cd = pin_cd;
    }

    public long getVillage_cd() {
        return village_cd;
    }

    public void setVillage_cd(long village_cd) {
        this.village_cd = village_cd;
    }

    public int getTaluk_cd() {
        return taluk_cd;
    }

    public void setTaluk_cd(int taluk_cd) {
        this.taluk_cd = taluk_cd;
    }

    public long getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(long mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public int getOff_under_cd() {
        return off_under_cd;
    }

    public void setOff_under_cd(int off_under_cd) {
        this.off_under_cd = off_under_cd;
    }

    public int getOff_type_cd() {
        return off_type_cd;
    }

    public void setOff_type_cd(int off_type_cd) {
        this.off_type_cd = off_type_cd;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public Date getVow4_launch_date() {
        return vow4_launch_date;
    }

    public void setVow4_launch_date(Date vow4_launch_date) {
        this.vow4_launch_date = vow4_launch_date;
    }

    public String getHsrp() {
        return hsrp;
    }

    public void setHsrp(String hsrp) {
        this.hsrp = hsrp;
    }

    public String getSmartCard() {
        return smartCard;
    }

    public void setSmartCard(String smartCard) {
        this.smartCard = smartCard;
    }

    public boolean isIsHsrp() {
        return isHsrp;
    }

    public void setIsHsrp(boolean isHsrp) {
        this.isHsrp = isHsrp;
    }

    public boolean isIsSmartCard() {
        return isSmartCard;
    }

    public void setIsSmartCard(boolean isSmartCard) {
        this.isSmartCard = isSmartCard;
    }

    public String getPaperRC() {
        return paperRC;
    }

    public void setPaperRC(String paperRC) {
        this.paperRC = paperRC;
    }

    public String getPlasticCard() {
        return plasticCard;
    }

    public void setPlasticCard(String plasticCard) {
        this.plasticCard = plasticCard;
    }

    public String getRcOption() {
        return rcOption;
    }

    public void setRcOption(String rcOption) {
        this.rcOption = rcOption;
    }

    public String getRcType() {
        return rcType;
    }

    public void setRcType(String rcType) {
        this.rcType = rcType;
    }

    public boolean isIsOldHsrp() {
        return isOldHsrp;
    }

    public void setIsOldHsrp(boolean isOldHsrp) {
        this.isOldHsrp = isOldHsrp;
    }

    public String getOld_veh_hsrp() {
        return old_veh_hsrp;
    }

    public void setOld_veh_hsrp(String old_veh_hsrp) {
        this.old_veh_hsrp = old_veh_hsrp;
    }

    /**
     * @return the plasticCardpvc
     */
    public String getPlasticCardpvc() {
        return plasticCardpvc;
    }

    /**
     * @param plasticCardpvc the plasticCardpvc to set
     */
    public void setPlasticCardpvc(String plasticCardpvc) {
        this.plasticCardpvc = plasticCardpvc;
    }
}
