/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;

/**
 *
 * @author Afzal
 */
public class VmRoadSafetySloganPrintDobj implements Serializable {

    private String state_cd;
    private int sr_no;
    private String english_lang;
    private String state_lang;

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
     * @return the sr_no
     */
    public int getSr_no() {
        return sr_no;
    }

    /**
     * @param sr_no the sr_no to set
     */
    public void setSr_no(int sr_no) {
        this.sr_no = sr_no;
    }

    /**
     * @return the english_lang
     */
    public String getEnglish_lang() {
        return english_lang;
    }

    /**
     * @param english_lang the english_lang to set
     */
    public void setEnglish_lang(String english_lang) {
        this.english_lang = english_lang;
    }

    /**
     * @return the state_lang
     */
    public String getState_lang() {
        return state_lang;
    }

    /**
     * @param state_lang the state_lang to set
     */
    public void setState_lang(String state_lang) {
        this.state_lang = state_lang;
    }
}
