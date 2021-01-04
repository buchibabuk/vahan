/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.eapplication;

import java.io.Serializable;

public class EApplicationDobj implements Serializable {

    private int pur_cd;
    private String regn_no;
    private String chasis_no;

    /**
     * @return the pur_cd
     */
    public int getPur_cd() {
        return pur_cd;
    }

    /**
     * @param pur_cd the pur_cd to set
     */
    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
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
     * @return the chasis_no
     */
    public String getChasis_no() {
        return chasis_no;
    }

    /**
     * @param chasis_no the chasis_no to set
     */
    public void setChasis_no(String chasis_no) {
        this.chasis_no = chasis_no;
    }
}
