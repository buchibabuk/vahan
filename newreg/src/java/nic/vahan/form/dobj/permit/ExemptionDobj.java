/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author hcl
 */
public class ExemptionDobj implements Cloneable, Serializable {

    private String state_cd;
    private int off_cd;
    private String appl_no;
    private String regn_no;
    private String pmt_no;
    private Date exem_from_date;
    private Date exem_to_date;
    private int exem_amount;
    private int fine_to_be_taken;
    private String order_no;
    private String order_by;
    private Date order_dt;
    private String exem_reason;
    private Date op_dt;
    private int pur_cd;
    private String fitValidUpto;
    private String parkValidUpto;

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

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getPmt_no() {
        return pmt_no;
    }

    public void setPmt_no(String pmt_no) {
        this.pmt_no = pmt_no;
    }

    public Date getExem_from_date() {
        return exem_from_date;
    }

    public void setExem_from_date(Date exem_from_date) {
        this.exem_from_date = exem_from_date;
    }

    public Date getExem_to_date() {
        return exem_to_date;
    }

    public void setExem_to_date(Date exem_to_date) {
        this.exem_to_date = exem_to_date;
    }

    public int getExem_amount() {
        return exem_amount;
    }

    public void setExem_amount(int exem_amount) {
        this.exem_amount = exem_amount;
    }

    public int getFine_to_be_taken() {
        return fine_to_be_taken;
    }

    public void setFine_to_be_taken(int fine_to_be_taken) {
        this.fine_to_be_taken = fine_to_be_taken;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getOrder_by() {
        return order_by;
    }

    public void setOrder_by(String order_by) {
        this.order_by = order_by;
    }

    public Date getOrder_dt() {
        return order_dt;
    }

    public void setOrder_dt(Date order_dt) {
        this.order_dt = order_dt;
    }

    public String getExem_reason() {
        return exem_reason;
    }

    public void setExem_reason(String exem_reason) {
        this.exem_reason = exem_reason;
    }

    public Date getOp_dt() {
        return op_dt;
    }

    public void setOp_dt(Date op_dt) {
        this.op_dt = op_dt;
    }

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    public String getFitValidUpto() {
        return fitValidUpto;
    }

    public void setFitValidUpto(String fitValidUpto) {
        this.fitValidUpto = fitValidUpto;
    }

    public String getParkValidUpto() {
        return parkValidUpto;
    }

    public void setParkValidUpto(String parkValidUpto) {
        this.parkValidUpto = parkValidUpto;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }
}
