/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

public class InsDobj implements Serializable, Cloneable {

    private String appl_no;
    private String regn_no;
    private int comp_cd;
    private int ins_type;
    private Date ins_from;
    private Date ins_upto;
    private String policy_no;
    private String state_cd;
    private int off_cd;
    private long idv;
    private String insCompName;
    private Date op_dt;
    private boolean insertUpdateInsurnaceFlag;
    private int insPeriodYears = 0;
    private String insTypeDescr;
    private boolean iibData = false;

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
     * @return the ins_type
     */
    public int getIns_type() {
        return ins_type;
    }

    /**
     * @param ins_type the ins_type to set
     */
    public void setIns_type(int ins_type) {
        this.ins_type = ins_type;
    }

    /**
     * @return the policy_no
     */
    public String getPolicy_no() {
        return policy_no;
    }

    /**
     * @param policy_no the policy_no to set
     */
    public void setPolicy_no(String policy_no) {
        this.policy_no = policy_no;
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
     * @return the comp_cd
     */
    public int getComp_cd() {
        return comp_cd;
    }

    /**
     * @param comp_cd the comp_cd to set
     */
    public void setComp_cd(int comp_cd) {
        this.comp_cd = comp_cd;
    }

    /**
     * @return the ins_from
     */
    public Date getIns_from() {
        return ins_from;
    }

    /**
     * @param ins_from the ins_from to set
     */
    public void setIns_from(Date ins_from) {
        this.ins_from = ins_from;
    }

    /**
     * @return the ins_upto
     */
    public Date getIns_upto() {
        return ins_upto;
    }

    /**
     * @param ins_upto the ins_upto to set
     */
    public void setIns_upto(Date ins_upto) {
        this.ins_upto = ins_upto;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the idv
     */
    public long getIdv() {
        return idv;
    }

    /**
     * @param idv the idv to set
     */
    public void setIdv(long idv) {
        this.idv = idv;
    }

    @Override
    public String toString() {
        String insuranceComparison = regn_no + ";"
                + comp_cd + ";"
                + ins_type + ";"
                + ins_from + ";"
                + ins_upto + ";"
                + policy_no;
        return insuranceComparison.toString();
    }

    /**
     * @return the insCompName
     */
    public String getInsCompName() {
        return insCompName;
    }

    /**
     * @param insCompName the insCompName to set
     */
    public void setInsCompName(String insCompName) {
        this.insCompName = insCompName;
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

    public boolean isInsertUpdateInsurnaceFlag() {
        return insertUpdateInsurnaceFlag;
    }

    public void setInsertUpdateInsurnaceFlag(boolean insertUpdateInsurnaceFlag) {
        this.insertUpdateInsurnaceFlag = insertUpdateInsurnaceFlag;
    }

    public int getInsPeriodYears() {
        return insPeriodYears;
    }

    public void setInsPeriodYears(int insPeriodYears) {
        this.insPeriodYears = insPeriodYears;
    }

    /**
     * @return the insTypeDescr
     */
    public String getInsTypeDescr() {
        return insTypeDescr;
    }

    /**
     * @param insTypeDescr the insTypeDescr to set
     */
    public void setInsTypeDescr(String insTypeDescr) {
        this.insTypeDescr = insTypeDescr;
    }

    public boolean isIibData() {
        return iibData;
    }

    public void setIibData(boolean iibData) {
        this.iibData = iibData;
    }

  
}
