/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.smartcard;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author NIC
 */
public class DeleteSmartcardFlatFileDobj implements Cloneable, Serializable {

    private String regn_no;
    private String genrated_on;
    private String reason;
    private String appl_no;
    private String rcpt_no;
    private Date op_dt;

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getGenrated_on() {
        return genrated_on;
    }

    public void setGenrated_on(String genrated_on) {
        this.genrated_on = genrated_on;
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
     * @return the rcpt_no
     */
    public String getRcpt_no() {
        return rcpt_no;
    }

    /**
     * @param rcpt_no the rcpt_no to set
     */
    public void setRcpt_no(String rcpt_no) {
        this.rcpt_no = rcpt_no;
    }

    /**
     * @return the op_dt
     */
    public Date getOp_dt() {
        return op_dt;
    }

    /**
     * @param op_dt the op_dt to set
     */
    public void setOp_dt(Date op_dt) {
        this.op_dt = op_dt;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
