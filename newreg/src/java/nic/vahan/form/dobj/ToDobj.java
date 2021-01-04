/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

public class ToDobj implements Cloneable, Serializable {

    private String appl_no;
    private String regn_no;
    private int pur_cd;
    private int owner_sr;
    private String owner_name;
    private int owner_cd;
    private int owner_ctg;
    private String f_name;
    private String c_add1;
    private String c_add2;
    private String c_add3;
    private String c_state;
    private String c_state_name;
    private int c_district;
    private String c_district_name;
    private int c_pincode;
    private String p_add1;
    private String p_add2;
    private String p_add3;
    private String p_state;
    private String p_state_name;
    private int p_district;
    private String p_district_name;
    private int p_pincode;
    private Date owner_from;
    private Date transfer_dt;
    private Date sale_dt;
    private int sale_amt;
    private String reason;
    private String garage_add;
    private String op_dt;
    private String stateCode;
    private int offCode;
    private boolean assignRetainNo;
    private String assignRetainRegnNo;
    ///fancy///////
    private boolean assignFancyNumber;
    private String assignFancyRegnNumber;
    private String numberRetention = "NO";

    public ToDobj() {
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
     * @return the owner_sr
     */
    public int getOwner_sr() {
        return owner_sr;
    }

    /**
     * @param owner_sr the owner_sr to set
     */
    public void setOwner_sr(int owner_sr) {
        this.owner_sr = owner_sr;
    }

    /**
     * @return the owner_name
     */
    public String getOwner_name() {
        return owner_name;
    }

    /**
     * @param owner_name the owner_name to set
     */
    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    /**
     * @return the owner_cd
     */
    public int getOwner_cd() {
        return owner_cd;
    }

    /**
     * @param owner_cd the owner_cd to set
     */
    public void setOwner_cd(int owner_cd) {
        this.owner_cd = owner_cd;
    }

    /**
     * @return the owner_ctg
     */
    public int getOwner_ctg() {
        return owner_ctg;
    }

    /**
     * @param owner_ctg the owner_ctg to set
     */
    public void setOwner_ctg(int owner_ctg) {
        this.owner_ctg = owner_ctg;
    }

    /**
     * @return the f_name
     */
    public String getF_name() {
        return f_name;
    }

    /**
     * @param f_name the f_name to set
     */
    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    /**
     * @return the c_add1
     */
    public String getC_add1() {
        return c_add1;
    }

    /**
     * @param c_add1 the c_add1 to set
     */
    public void setC_add1(String c_add1) {
        this.c_add1 = c_add1;
    }

    /**
     * @return the c_add2
     */
    public String getC_add2() {
        return c_add2;
    }

    /**
     * @param c_add2 the c_add2 to set
     */
    public void setC_add2(String c_add2) {
        this.c_add2 = c_add2;
    }

    /**
     * @return the c_district
     */
    public int getC_district() {
        return c_district;
    }

    /**
     * @param c_district the c_district to set
     */
    public void setC_district(int c_district) {
        this.c_district = c_district;
    }

    /**
     * @return the c_pincode
     */
    public int getC_pincode() {
        return c_pincode;
    }

    /**
     * @param c_pincode the c_pincode to set
     */
    public void setC_pincode(int c_pincode) {
        this.c_pincode = c_pincode;
    }

    /**
     * @return the p_add1
     */
    public String getP_add1() {
        return p_add1;
    }

    /**
     * @param p_add1 the p_add1 to set
     */
    public void setP_add1(String p_add1) {
        this.p_add1 = p_add1;
    }

    /**
     * @return the p_add2
     */
    public String getP_add2() {
        return p_add2;
    }

    /**
     * @param p_add2 the p_add2 to set
     */
    public void setP_add2(String p_add2) {
        this.p_add2 = p_add2;
    }

    /**
     * @return the p_district
     */
    public int getP_district() {
        return p_district;
    }

    /**
     * @param p_district the p_district to set
     */
    public void setP_district(int p_district) {
        this.p_district = p_district;
    }

    /**
     * @return the p_pincode
     */
    public int getP_pincode() {
        return p_pincode;
    }

    /**
     * @param p_pincode the p_pincode to set
     */
    public void setP_pincode(int p_pincode) {
        this.p_pincode = p_pincode;
    }

    /**
     * @return the owner_from
     */
    public Date getOwner_from() {
        return owner_from;
    }

    /**
     * @param owner_from the owner_from to set
     */
    public void setOwner_from(Date owner_from) {
        this.owner_from = owner_from;
    }

    /**
     * @return the sale_amt
     */
    public int getSale_amt() {
        return sale_amt;
    }

    /**
     * @param sale_amt the sale_amt to set
     */
    public void setSale_amt(int sale_amt) {
        this.sale_amt = sale_amt;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the garage_add
     */
    public String getGarage_add() {
        return garage_add;
    }

    /**
     * @param garage_add the garage_add to set
     */
    public void setGarage_add(String garage_add) {
        this.garage_add = garage_add;
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

    /**
     * @return the sale_dt
     */
    public Date getSale_dt() {
        return sale_dt;
    }

    /**
     * @param sale_dt the sale_dt to set
     */
    public void setSale_dt(Date sale_dt) {
        this.sale_dt = sale_dt;
    }

    /**
     * @return the transfer_dt
     */
    public Date getTransfer_dt() {
        return transfer_dt;
    }

    /**
     * @param transfer_dt the transfer_dt to set
     */
    public void setTransfer_dt(Date transfer_dt) {
        this.transfer_dt = transfer_dt;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

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
     * @return the c_add3
     */
    public String getC_add3() {
        return c_add3;
    }

    /**
     * @param c_add3 the c_add3 to set
     */
    public void setC_add3(String c_add3) {
        this.c_add3 = c_add3;
    }

    /**
     * @return the c_state
     */
    public String getC_state() {
        return c_state;
    }

    /**
     * @param c_state the c_state to set
     */
    public void setC_state(String c_state) {
        this.c_state = c_state;
    }

    /**
     * @return the c_state_name
     */
    public String getC_state_name() {
        return c_state_name;
    }

    /**
     * @param c_state_name the c_state_name to set
     */
    public void setC_state_name(String c_state_name) {
        this.c_state_name = c_state_name;
    }

    /**
     * @return the c_district_name
     */
    public String getC_district_name() {
        return c_district_name;
    }

    /**
     * @param c_district_name the c_district_name to set
     */
    public void setC_district_name(String c_district_name) {
        this.c_district_name = c_district_name;
    }

    /**
     * @return the p_add3
     */
    public String getP_add3() {
        return p_add3;
    }

    /**
     * @param p_add3 the p_add3 to set
     */
    public void setP_add3(String p_add3) {
        this.p_add3 = p_add3;
    }

    /**
     * @return the p_state
     */
    public String getP_state() {
        return p_state;
    }

    /**
     * @param p_state the p_state to set
     */
    public void setP_state(String p_state) {
        this.p_state = p_state;
    }

    /**
     * @return the p_state_name
     */
    public String getP_state_name() {
        return p_state_name;
    }

    /**
     * @param p_state_name the p_state_name to set
     */
    public void setP_state_name(String p_state_name) {
        this.p_state_name = p_state_name;
    }

    /**
     * @return the p_district_name
     */
    public String getP_district_name() {
        return p_district_name;
    }

    /**
     * @param p_district_name the p_district_name to set
     */
    public void setP_district_name(String p_district_name) {
        this.p_district_name = p_district_name;
    }

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
     * @return the assignRetainNo
     */
    public boolean isAssignRetainNo() {
        return assignRetainNo;
    }

    /**
     * @param assignRetainNo the assignRetainNo to set
     */
    public void setAssignRetainNo(boolean assignRetainNo) {
        this.assignRetainNo = assignRetainNo;
    }

    /**
     * @return the assignRetainRegnNo
     */
    public String getAssignRetainRegnNo() {
        return assignRetainRegnNo;
    }

    /**
     * @param assignRetainRegnNo the assignRetainRegnNo to set
     */
    public void setAssignRetainRegnNo(String assignRetainRegnNo) {
        this.assignRetainRegnNo = assignRetainRegnNo;
    }

    public boolean isAssignFancyNumber() {
        return assignFancyNumber;
    }

    public void setAssignFancyNumber(boolean assignFancyNumber) {
        this.assignFancyNumber = assignFancyNumber;
    }

    public String getAssignFancyRegnNumber() {
        return assignFancyRegnNumber;
    }

    public void setAssignFancyRegnNumber(String assignFancyRegnNumber) {
        this.assignFancyRegnNumber = assignFancyRegnNumber;
    }

    /**
     * @return the numberRetention
     */
    public String getNumberRetention() {
        return numberRetention;
    }

    /**
     * @param numberRetention the numberRetention to set
     */
    public void setNumberRetention(String numberRetention) {
        this.numberRetention = numberRetention;
    }
}
