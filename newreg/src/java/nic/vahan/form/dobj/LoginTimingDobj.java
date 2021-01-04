/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author acer
 */
public class LoginTimingDobj implements Serializable {

    private String state_cd;
    private Integer off_cd;
    private Date open_timing = null;
    private String openTimingStr = "";
    private String closeTimingStr = "";
    private Date close_timing = null;
    private String stateName;
    private String officeName;
    private boolean saveButton;
    private String login_type;
    private String user_name;
    private String user_id;
    private int user_cd;
    private String emp_cd;
    private String offCd;

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
    public Integer getOff_cd() {
        return off_cd;
    }

    /**
     * @param off_cd the off_cd to set
     */
    public void setOff_cd(Integer off_cd) {
        this.off_cd = off_cd;
    }

    /**
     * @return the open_timing
     */
    public Date getOpen_timing() {
        return open_timing;
    }

    /**
     * @param open_timing the open_timing to set
     */
    public void setOpen_timing(Date open_timing) {
        this.open_timing = open_timing;
    }

    /**
     * @return the close_timing
     */
    public Date getClose_timing() {
        return close_timing;
    }

    /**
     * @param close_timing the close_timing to set
     */
    public void setClose_timing(Date close_timing) {
        this.close_timing = close_timing;
    }

    /**
     * @return the stateName
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * @param stateName the stateName to set
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    /**
     * @return the officeName
     */
    public String getOfficeName() {
        return officeName;
    }

    /**
     * @param officeName the officeName to set
     */
    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    /**
     * @return the saveButton
     */
    public boolean isSaveButton() {
        return saveButton;
    }

    /**
     * @param saveButton the saveButton to set
     */
    public void setSaveButton(boolean saveButton) {
        this.saveButton = saveButton;
    }

    /**
     * @return the login_type
     */
    public String getLogin_type() {
        return login_type;
    }

    /**
     * @param login_type the login_type to set
     */
    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }

    /**
     * @return the user_name
     */
    public String getUser_name() {
        return user_name;
    }

    /**
     * @param user_name the user_name to set
     */
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    /**
     * @return the user_id
     */
    public String getUser_id() {
        return user_id;
    }

    /**
     * @param user_id the user_id to set
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOpenTimingStr() {
        return openTimingStr;
    }

    public void setOpenTimingStr(String openTimingStr) {
        this.openTimingStr = openTimingStr;
    }

    public String getCloseTimingStr() {
        return closeTimingStr;
    }

    public void setCloseTimingStr(String closeTimingStr) {
        this.closeTimingStr = closeTimingStr;
    }

    /**
     * @return the user_cd
     */
    public int getUser_cd() {
        return user_cd;
    }

    /**
     * @param user_cd the user_cd to set
     */
    public void setUser_cd(int user_cd) {
        this.user_cd = user_cd;
    }

    /**
     * @return the emp_cd
     */
    public String getEmp_cd() {
        return emp_cd;
    }

    /**
     * @param emp_cd the emp_cd to set
     */
    public void setEmp_cd(String emp_cd) {
        this.emp_cd = emp_cd;
    }

    /**
     * @return the offCd
     */
    public String getOffCd() {
        return offCd;
    }

    /**
     * @param offCd the offCd to set
     */
    public void setOffCd(String offCd) {
        this.offCd = offCd;
    }
}
