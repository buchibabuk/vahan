/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nic
 */
public class OwnerDisclaimerDobj implements Serializable {

    private String appl_no;
    private String appl_dt;
    private String regno;
    private int purCd;
    private String purCdDescr;
    private List<OwnerDisclaimerDobj> list = new ArrayList();
    private int action_cd;
    private String state_cd;
    private int off_cd;

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
     * @return the regno
     */
    public String getRegno() {
        return regno;
    }

    /**
     * @param regno the regno to set
     */
    public void setRegno(String regno) {
        this.regno = regno;
    }

    /**
     * @return the purCd
     */
    public int getPurCd() {
        return purCd;
    }

    /**
     * @param purCd the purCd to set
     */
    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }

    /**
     * @return the purCdDescr
     */
    public String getPurCdDescr() {
        return purCdDescr;
    }

    /**
     * @param purCdDescr the purCdDescr to set
     */
    public void setPurCdDescr(String purCdDescr) {
        this.purCdDescr = purCdDescr;
    }

    /**
     * @return the list
     */
    public List<OwnerDisclaimerDobj> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<OwnerDisclaimerDobj> list) {
        this.list = list;
    }

    /**
     * @return the action_cd
     */
    public int getAction_cd() {
        return action_cd;
    }

    /**
     * @param action_cd the action_cd to set
     */
    public void setAction_cd(int action_cd) {
        this.action_cd = action_cd;
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
     * @return the appl_dt
     */
    public String getAppl_dt() {
        return appl_dt;
    }

    /**
     * @param appl_dt the appl_dt to set
     */
    public void setAppl_dt(String appl_dt) {
        this.appl_dt = appl_dt;
    }
}
