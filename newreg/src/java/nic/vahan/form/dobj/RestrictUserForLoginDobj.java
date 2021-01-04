/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DELL
 */
public class RestrictUserForLoginDobj {

    private String stateName;
    private String officeName;
    private boolean saveButton;
    private String state_cd;
    private Integer off_cd;
    private List selectedUserCatg = new ArrayList();

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
     * @return the selectedUserCatg
     */
    public List getSelectedUserCatg() {
        return selectedUserCatg;
    }

    /**
     * @param selectedUserCatg the selectedUserCatg to set
     */
    public void setSelectedUserCatg(List selectedUserCatg) {
        this.selectedUserCatg = selectedUserCatg;
    }
}
