/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.dobj;

import java.io.Serializable;

/**
 *
 * @author tranC103
 */
public class ChangePwdDobj implements Serializable {

    private String oldPwd;
    private String newPwd;
    private String cnfrmNewPwd;
    private long user_cd;
    private String state_cd;
    private Integer off_cd;
    private String user_id;
    private String shaPwd;

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }

    public String getCnfrmNewPwd() {
        return cnfrmNewPwd;
    }

    public void setCnfrmNewPwd(String cnfrmNewPwd) {
        this.cnfrmNewPwd = cnfrmNewPwd;
    }

    public long getUser_cd() {
        return user_cd;
    }

    public void setUser_cd(long user_cd) {
        this.user_cd = user_cd;
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

    /**
     * @return the shaPwd
     */
    public String getShaPwd() {
        return shaPwd;
    }

    /**
     * @param shaPwd the shaPwd to set
     */
    public void setShaPwd(String shaPwd) {
        this.shaPwd = shaPwd;
    }
}
