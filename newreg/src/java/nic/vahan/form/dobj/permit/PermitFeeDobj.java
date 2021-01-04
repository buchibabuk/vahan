/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.sql.Date;

public class PermitFeeDobj implements Serializable {

    private String state_cd;
    private String regn_no;
    private String new_regn_no;
    private String regn_dt;
    private String owner_name;
    private String f_name;
    private String regn_type;
    private String vh_class;
    private String chasi_no;
    private int seat_cap;
    private int stand_cap;
    private int sleeper_cap;
    private int unld_wt;
    private int ld_wt;
    private String fuel;
    private Date op_dt;
    private String color;
    private String norms;
    private String permit_type;
    private int valid_in_months;
    private int permit_domain;
    private int permit_catg;
    private int service_type;
    private String fee_desc2;
    private int amount1;
    private int fine1;
    private int total1;
    private int amount2;
    private int fine2;
    private int total2;
    private int rto_cd;
    private Date app_dt;
    private String permit_no;
    private String appl_no;
    private String flag;
    private String new_o_name;
    private String new_f_name;
    private String new_vh_class;
    private Date permit_valid_upto;
    private String period_mode;
    private String period;
    private String pur_cd;
    private String recpt_no;
    private String offer_no;
    private String paymentStaus;
    private String npTransactionDate;
    private String npIssueDate;
    private String bankRefNo;
    private String transactionNo;
    private String old_regn_no;
    private String trans_status;

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
     * @return the regn_dt
     */
    public String getRegn_dt() {
        return regn_dt;
    }

    /**
     * @param regn_dt the regn_dt to set
     */
    public void setRegn_dt(String regn_dt) {
        this.regn_dt = regn_dt;
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
     * @return the regn_type
     */
    public String getRegn_type() {
        return regn_type;
    }

    /**
     * @param regn_type the regn_type to set
     */
    public void setRegn_type(String regn_type) {
        this.regn_type = regn_type;
    }

    /**
     * @return the vh_class
     */
    public String getVh_class() {
        return vh_class;
    }

    /**
     * @param vh_class the vh_class to set
     */
    public void setVh_class(String vh_class) {
        this.vh_class = vh_class;
    }

    /**
     * @return the chasi_no
     */
    public String getChasi_no() {
        return chasi_no;
    }

    /**
     * @param chasi_no the chasi_no to set
     */
    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
    }

    /**
     * @return the seat_cap
     */
    public int getSeat_cap() {
        return seat_cap;
    }

    /**
     * @param seat_cap the seat_cap to set
     */
    public void setSeat_cap(int seat_cap) {
        this.seat_cap = seat_cap;
    }

    /**
     * @return the stand_cap
     */
    public int getStand_cap() {
        return stand_cap;
    }

    /**
     * @param stand_cap the stand_cap to set
     */
    public void setStand_cap(int stand_cap) {
        this.stand_cap = stand_cap;
    }

    /**
     * @return the unld_wt
     */
    public int getUnld_wt() {
        return unld_wt;
    }

    /**
     * @param unld_wt the unld_wt to set
     */
    public void setUnld_wt(int unld_wt) {
        this.unld_wt = unld_wt;
    }

    /**
     * @return the ld_wt
     */
    public int getLd_wt() {
        return ld_wt;
    }

    /**
     * @param ld_wt the ld_wt to set
     */
    public void setLd_wt(int ld_wt) {
        this.ld_wt = ld_wt;
    }

    /**
     * @return the fuel
     */
    public String getFuel() {
        return fuel;
    }

    /**
     * @param fuel the fuel to set
     */
    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return the norms
     */
    public String getNorms() {
        return norms;
    }

    /**
     * @param norms the norms to set
     */
    public void setNorms(String norms) {
        this.norms = norms;
    }

    /**
     * @return the permit_type
     */
    public String getPermit_type() {
        return permit_type;
    }

    /**
     * @param permit_type the permit_type to set
     */
    public void setPermit_type(String permit_type) {
        this.permit_type = permit_type;
    }

    /**
     * @return the valid_in_months
     */
    public int getValid_in_months() {
        return valid_in_months;
    }

    /**
     * @param valid_in_months the valid_in_months to set
     */
    public void setValid_in_months(int valid_in_months) {
        this.valid_in_months = valid_in_months;
    }

    /**
     * @return the permit_domain
     */
    public int getPermit_domain() {
        return permit_domain;
    }

    /**
     * @param permit_domain the permit_domain to set
     */
    public void setPermit_domain(int permit_domain) {
        this.permit_domain = permit_domain;
    }

    /**
     * @return the permit_catg
     */
    public int getPermit_catg() {
        return permit_catg;
    }

    /**
     * @param permit_catg the permit_catg to set
     */
    public void setPermit_catg(int permit_catg) {
        this.permit_catg = permit_catg;
    }

    /**
     * @return the service_type
     */
    public int getService_type() {
        return service_type;
    }

    /**
     * @param service_type the service_type to set
     */
    public void setService_type(int service_type) {
        this.service_type = service_type;
    }

    /**
     * @return the fee_desc2
     */
    public String getFee_desc2() {
        return fee_desc2;
    }

    /**
     * @param fee_desc2 the fee_desc2 to set
     */
    public void setFee_desc2(String fee_desc2) {
        this.fee_desc2 = fee_desc2;
    }

    /**
     * @return the amount1
     */
    public int getAmount1() {
        return amount1;
    }

    /**
     * @param amount1 the amount1 to set
     */
    public void setAmount1(int amount1) {
        this.amount1 = amount1;
    }

    /**
     * @return the fine1
     */
    public int getFine1() {
        return fine1;
    }

    /**
     * @param fine1 the fine1 to set
     */
    public void setFine1(int fine1) {
        this.fine1 = fine1;
    }

    /**
     * @return the total1
     */
    public int getTotal1() {
        return total1;
    }

    /**
     * @param total1 the total1 to set
     */
    public void setTotal1(int total1) {
        this.total1 = total1;
    }

    /**
     * @return the amount2
     */
    public int getAmount2() {
        return amount2;
    }

    /**
     * @param amount2 the amount2 to set
     */
    public void setAmount2(int amount2) {
        this.amount2 = amount2;
    }

    /**
     * @return the fine2
     */
    public int getFine2() {
        return fine2;
    }

    /**
     * @param fine2 the fine2 to set
     */
    public void setFine2(int fine2) {
        this.fine2 = fine2;
    }

    /**
     * @return the total2
     */
    public int getTotal2() {
        return total2;
    }

    /**
     * @param total2 the total2 to set
     */
    public void setTotal2(int total2) {
        this.total2 = total2;
    }

    /**
     * @return the rto_cd
     */
    public int getRto_cd() {
        return rto_cd;
    }

    /**
     * @param rto_cd the rto_cd to set
     */
    public void setRto_cd(int rto_cd) {
        this.rto_cd = rto_cd;
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

    /**
     * @return the app_dt
     */
    public Date getApp_dt() {
        return app_dt;
    }

    /**
     * @param app_dt the app_dt to set
     */
    public void setApp_dt(Date app_dt) {
        this.app_dt = app_dt;
    }

    /**
     * @return the permit_no
     */
    public String getPermit_no() {
        return permit_no;
    }

    /**
     * @param permit_no the permit_no to set
     */
    public void setPermit_no(String permit_no) {
        this.permit_no = permit_no;
    }

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getNew_o_name() {
        return new_o_name;
    }

    public void setNew_o_name(String new_o_name) {
        this.new_o_name = new_o_name;
    }

    public String getNew_f_name() {
        return new_f_name;
    }

    public void setNew_f_name(String new_f_name) {
        this.new_f_name = new_f_name;
    }

    public String getNew_vh_class() {
        return new_vh_class;
    }

    public void setNew_vh_class(String new_vh_class) {
        this.new_vh_class = new_vh_class;
    }

    public String getPeriod_mode() {
        return period_mode;
    }

    public void setPeriod_mode(String period_mode) {
        this.period_mode = period_mode;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getNew_regn_no() {
        return new_regn_no;
    }

    public void setNew_regn_no(String new_regn_no) {
        this.new_regn_no = new_regn_no;
    }

    public Date getPermit_valid_upto() {
        return permit_valid_upto;
    }

    public void setPermit_valid_upto(Date permit_valid_upto) {
        this.permit_valid_upto = permit_valid_upto;
    }

    public String getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(String pur_cd) {
        this.pur_cd = pur_cd;
    }

    public String getRecpt_no() {
        return recpt_no;
    }

    public void setRecpt_no(String recpt_no) {
        this.recpt_no = recpt_no;
    }

    public String getOffer_no() {
        return offer_no;
    }

    public void setOffer_no(String offer_no) {
        this.offer_no = offer_no;
    }

    public String getPaymentStaus() {
        return paymentStaus;
    }

    public void setPaymentStaus(String paymentStaus) {
        this.paymentStaus = paymentStaus;
    }

    public String getNpTransactionDate() {
        return npTransactionDate;
    }

    public void setNpTransactionDate(String npTransactionDate) {
        this.npTransactionDate = npTransactionDate;
    }

    public String getNpIssueDate() {
        return npIssueDate;
    }

    public void setNpIssueDate(String npIssueDate) {
        this.npIssueDate = npIssueDate;
    }

    public String getBankRefNo() {
        return bankRefNo;
    }

    public void setBankRefNo(String bankRefNo) {
        this.bankRefNo = bankRefNo;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getOld_regn_no() {
        return old_regn_no;
    }

    public void setOld_regn_no(String old_regn_no) {
        this.old_regn_no = old_regn_no;
    }

    public int getSleeper_cap() {
        return sleeper_cap;
    }

    public void setSleeper_cap(int sleeper_cap) {
        this.sleeper_cap = sleeper_cap;
    }

    public String getTrans_status() {
        return trans_status;
    }

    public void setTrans_status(String trans_status) {
        this.trans_status = trans_status;
    }
}
