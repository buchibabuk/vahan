/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author nic
 */
public class HsrpPrintDobj implements Serializable {

    private String appl_no;
    private String appl_dt;
    private String regno;
    private boolean isApproved;

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

    /**
     * @return the isApproved
     */
    public boolean isIsApproved() {
        return isApproved;
    }

    /**
     * @param isApproved the isApproved to set
     */
    public void setIsApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }
}
