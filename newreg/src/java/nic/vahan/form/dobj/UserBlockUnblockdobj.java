/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author acer
 */
public class UserBlockUnblockdobj implements Serializable {

    private String state_cd;
    private int off_cd;
    private String reason;
    private boolean blockUnBlockStatus;
    private String user_name;
    private String blockby;
    private int user_cd;

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isBlockUnBlockStatus() {
        return blockUnBlockStatus;
    }

    public void setBlockUnBlockStatus(boolean blockUnBlockStatus) {
        this.blockUnBlockStatus = blockUnBlockStatus;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getBlockby() {
        return blockby;
    }

    public void setBlockby(String blockby) {
        this.blockby = blockby;
    }

    public int getUser_cd() {
        return user_cd;
    }

    public void setUser_cd(int user_cd) {
        this.user_cd = user_cd;
    }
}
