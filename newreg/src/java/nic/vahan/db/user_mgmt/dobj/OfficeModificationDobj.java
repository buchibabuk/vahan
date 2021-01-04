/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.dobj;

import java.io.Serializable;
import java.util.Date;
import oracle.sql.DATE;

/**
 *
 * @author acer
 */
public class OfficeModificationDobj implements Serializable, Cloneable {

    private String state_cd;
    private int off_cd;
    private String off_name;
    private String off_add1;
    private String off_add2;
    private String pinCode;
    private int village_Cd;
    private int taluk_Cd;
    private int district_Cd;
    private String mobileNo;
    private String landLine_No;
    private String email_Id;
    private int off_under_cd;
    private int off_type_cd;
    private Date vow4_dt;
    private boolean smartcardFlag = false;
    private boolean hsrpFlag = false;
    private String paperRc ;
    private boolean oldHsrp = false;
    private String rcOption;
    private String statecdWithOffcd;

    public OfficeModificationDobj() {
        this.state_cd = null;
        this.off_cd = 0;
        this.off_name = null;
    }

    public OfficeModificationDobj(String state_cd, int off_cd, String off_name) {
        this.state_cd = state_cd;
        this.off_cd = off_cd;
        this.off_name = off_name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
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
     * @return the off_name
     */
    public String getOff_name() {
        return off_name;
    }

    /**
     * @param off_name the off_name to set
     */
    public void setOff_name(String off_name) {
        this.off_name = off_name;
    }

    /**
     * @return the pinCode
     */
    public String getPinCode() {
        return pinCode;
    }

    /**
     * @param pinCode the pinCode to set
     */
    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    /**
     * @return the village_Cd
     */
    public int getVillage_Cd() {
        return village_Cd;
    }

    /**
     * @param village_Cd the village_Cd to set
     */
    public void setVillage_Cd(int village_Cd) {
        this.village_Cd = village_Cd;
    }

    /**
     * @return the taluk_Cd
     */
    public int getTaluk_Cd() {
        return taluk_Cd;
    }

    /**
     * @param taluk_Cd the taluk_Cd to set
     */
    public void setTaluk_Cd(int taluk_Cd) {
        this.taluk_Cd = taluk_Cd;
    }

    /**
     * @return the district_Cd
     */
    public int getDistrict_Cd() {
        return district_Cd;
    }

    /**
     * @param district_Cd the district_Cd to set
     */
    public void setDistrict_Cd(int district_Cd) {
        this.district_Cd = district_Cd;
    }

    /**
     * @return the mobileNo
     */
    public String getMobileNo() {
        return mobileNo;
    }

    /**
     * @param mobileNo the mobileNo to set
     */
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    /**
     * @return the landLine_No
     */
    public String getLandLine_No() {
        return landLine_No;
    }

    /**
     * @param landLine_No the landLine_No to set
     */
    public void setLandLine_No(String landLine_No) {
        this.landLine_No = landLine_No;
    }

    /**
     * @return the email_Id
     */
    public String getEmail_Id() {
        return email_Id;
    }

    /**
     * @param email_Id the email_Id to set
     */
    public void setEmail_Id(String email_Id) {
        this.email_Id = email_Id;
    }

    /**
     * @return the off_under_cd
     */
    public int getOff_under_cd() {
        return off_under_cd;
    }

    /**
     * @param off_under_cd the off_under_cd to set
     */
    public void setOff_under_cd(int off_under_cd) {
        this.off_under_cd = off_under_cd;
    }

    /**
     * @return the off_type_cd
     */
    public int getOff_type_cd() {
        return off_type_cd;
    }

    /**
     * @param off_type_cd the off_type_cd to set
     */
    public void setOff_type_cd(int off_type_cd) {
        this.off_type_cd = off_type_cd;
    }

    /**
     * @return the off_add1
     */
    public String getOff_add1() {
        return off_add1;
    }

    /**
     * @param off_add1 the off_add1 to set
     */
    public void setOff_add1(String off_add1) {
        this.off_add1 = off_add1;
    }

    /**
     * @return the off_add2
     */
    public String getOff_add2() {
        return off_add2;
    }

    /**
     * @param off_add2 the off_add2 to set
     */
    public void setOff_add2(String off_add2) {
        this.off_add2 = off_add2;
    }

    /**
     * @return the vow4_dt
     */
    public Date getVow4_dt() {
        return vow4_dt;
    }

    /**
     * @param vow4_dt the vow4_dt to set
     */
    public void setVow4_dt(Date vow4_dt) {
        this.vow4_dt = vow4_dt;
    }

    /**
     * @return the smartcardFlag
     */
    public boolean isSmartcardFlag() {
        return smartcardFlag;
    }

    /**
     * @param smartcardFlag the smartcardFlag to set
     */
    public void setSmartcardFlag(boolean smartcardFlag) {
        this.smartcardFlag = smartcardFlag;
    }

    /**
     * @return the hsrpFlag
     */
    public boolean isHsrpFlag() {
        return hsrpFlag;
    }

    /**
     * @param hsrpFlag the hsrpFlag to set
     */
    public void setHsrpFlag(boolean hsrpFlag) {
        this.hsrpFlag = hsrpFlag;
    }

    /**
     * @return the oldHsrp
     */
    public boolean isOldHsrp() {
        return oldHsrp;
    }

    /**
     * @param oldHsrp the oldHsrp to set
     */
    public void setOldHsrp(boolean oldHsrp) {
        this.oldHsrp = oldHsrp;
    }

    /**
     * @return the rcOption
     */
    public String getRcOption() {
        return rcOption;
    }

    /**
     * @param rcOption the rcOption to set
     */
    public void setRcOption(String rcOption) {
        this.rcOption = rcOption;
    }

    /**
     * @return the paperRc
     */
    public String getPaperRc() {
        return paperRc;
    }

    /**
     * @param paperRc the paperRc to set
     */
    public void setPaperRc(String paperRc) {
        this.paperRc = paperRc;
    }

    /**
     * @return the statecdWithOffcd
     */
    public String getStatecdWithOffcd() {
        return statecdWithOffcd;
    }

    /**
     * @param statecdWithOffcd the statecdWithOffcd to set
     */
    public void setStatecdWithOffcd(String statecdWithOffcd) {
        this.statecdWithOffcd = statecdWithOffcd;
    }
}
