/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Ashok Kumar <send2ashok@hotmail.com>
 */
public class TempRegDobj implements Serializable, Cloneable {

    private String appl_no;
    private String regn_no;
    private int tmp_off_cd;
    private String regn_auth;
    private String tmp_state_cd;
    private String tmp_regn_no;
    private Date tmp_regn_dt;
    private Date tmp_valid_upto;
    private String dealer_cd;
    private boolean tempRegnInsertFlag;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

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
     * @return the tmp_off_cd
     */
    public int getTmp_off_cd() {
        return tmp_off_cd;
    }

    /**
     * @param tmp_off_cd the tmp_off_cd to set
     */
    public void setTmp_off_cd(int tmp_off_cd) {
        this.tmp_off_cd = tmp_off_cd;
    }

    /**
     * @return the regn_auth
     */
    public String getRegn_auth() {
        return regn_auth;
    }

    /**
     * @param regn_auth the regn_auth to set
     */
    public void setRegn_auth(String regn_auth) {
        this.regn_auth = regn_auth;
    }

    /**
     * @return the tmp_state_cd
     */
    public String getTmp_state_cd() {
        return tmp_state_cd;
    }

    /**
     * @param tmp_state_cd the tmp_state_cd to set
     */
    public void setTmp_state_cd(String tmp_state_cd) {
        this.tmp_state_cd = tmp_state_cd;
    }

    /**
     * @return the tmp_regn_no
     */
    public String getTmp_regn_no() {
        return tmp_regn_no;
    }

    /**
     * @param tmp_regn_no the tmp_regn_no to set
     */
    public void setTmp_regn_no(String tmp_regn_no) {
        this.tmp_regn_no = tmp_regn_no;
    }

    /**
     * @return the tmp_regn_dt
     */
    public Date getTmp_regn_dt() {
        return tmp_regn_dt;
    }

    /**
     * @param tmp_regn_dt the tmp_regn_dt to set
     */
    public void setTmp_regn_dt(Date tmp_regn_dt) {
        this.tmp_regn_dt = tmp_regn_dt;
    }

    /**
     * @return the dealer_cd
     */
    public String getDealer_cd() {
        return dealer_cd;
    }

    /**
     * @param dealer_cd the dealer_cd to set
     */
    public void setDealer_cd(String dealer_cd) {
        this.dealer_cd = dealer_cd;
    }

    /**
     * @return the tmp_valid_upto
     */
    public Date getTmp_valid_upto() {
        return tmp_valid_upto;
    }

    /**
     * @param tmp_valid_upto the tmp_valid_upto to set
     */
    public void setTmp_valid_upto(Date tmp_valid_upto) {
        this.tmp_valid_upto = tmp_valid_upto;
    }

    public boolean isTempRegnInsertFlag() {
        return tempRegnInsertFlag;
    }

    public void setTempRegnInsertFlag(boolean tempRegnInsertFlag) {
        this.tempRegnInsertFlag = tempRegnInsertFlag;
    }

    
    
    
}
