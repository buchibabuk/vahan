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
public class AdvertisementOnVehicleDobj implements Cloneable, Serializable {
    
    private String stateCode;
    private int offCode;
    private String appl_no;
    private String regn_no;
    private Date from_dt;
    private Date upto_dt;
    private String op_dt;

    /**
     * @return the stateCode
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * @param stateCode the stateCode to set
     */
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    /**
     * @return the offCode
     */
    public int getOffCode() {
        return offCode;
    }

    /**
     * @param offCode the offCode to set
     */
    public void setOffCode(int offCode) {
        this.offCode = offCode;
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
     * @return the from_dt
     */
    public Date getFrom_dt() {
        return from_dt;
    }

    /**
     * @param from_dt the from_dt to set
     */
    public void setFrom_dt(Date from_dt) {
        this.from_dt = from_dt;
    }

    /**
     * @return the upto_dt
     */
    public Date getUpto_dt() {
        return upto_dt;
    }

    /**
     * @param upto_dt the upto_dt to set
     */
    public void setUpto_dt(Date upto_dt) {
        this.upto_dt = upto_dt;
    }

    /**
     * @return the op_dt
     */
    public String getOp_dt() {
        return op_dt;
    }

    /**
     * @param op_dt the op_dt to set
     */
    public void setOp_dt(String op_dt) {
        this.op_dt = op_dt;
    }
}
