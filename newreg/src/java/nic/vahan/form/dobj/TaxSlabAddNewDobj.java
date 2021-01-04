/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author Ashok Kumar <send2ashok@hotmail.com>
 */
public class TaxSlabAddNewDobj implements Serializable {

    private String state_cd;
    private int slab_code;
    private int add_code;
    private String condition_formula;
    private String type_flag;
    private String rate_formula;
    private String emp_cd;
    private String op_dt;
    private boolean newAddSlab;
    private String ifCondFormula;

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
     * @return the slab_code
     */
    public int getSlab_code() {
        return slab_code;
    }

    /**
     * @param slab_code the slab_code to set
     */
    public void setSlab_code(int slab_code) {
        this.slab_code = slab_code;
    }

    /**
     * @return the add_code
     */
    public int getAdd_code() {
        return add_code;
    }

    /**
     * @param add_code the add_code to set
     */
    public void setAdd_code(int add_code) {
        this.add_code = add_code;
    }

    /**
     * @return the condition_formula
     */
    public String getCondition_formula() {
        return condition_formula;
    }

    /**
     * @param condition_formula the condition_formula to set
     */
    public void setCondition_formula(String condition_formula) {
        this.condition_formula = condition_formula;
    }

    /**
     * @return the type_flag
     */
    public String getType_flag() {
        return type_flag;
    }

    /**
     * @param type_flag the type_flag to set
     */
    public void setType_flag(String type_flag) {
        this.type_flag = type_flag;
    }

    /**
     * @return the rate_formula
     */
    public String getRate_formula() {
        return rate_formula;
    }

    /**
     * @param rate_formula the rate_formula to set
     */
    public void setRate_formula(String rate_formula) {
        this.rate_formula = rate_formula;
    }

    /**
     * @return the emp_cd
     */
    public String getEmp_cd() {
        return emp_cd;
    }

    /**
     * @param emp_cd the emp_cd to set
     */
    public void setEmp_cd(String emp_cd) {
        this.emp_cd = emp_cd;
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
     * @return the newAddSlab
     */
    public boolean isNewAddSlab() {
        return newAddSlab;
    }

    /**
     * @param newAddSlab the newAddSlab to set
     */
    public void setNewAddSlab(boolean newAddSlab) {
        this.newAddSlab = newAddSlab;
    }

    /**
     * @return the ifCondFormula
     */
    public String getIfCondFormula() {
        return ifCondFormula;
    }

    /**
     * @param ifCondFormula the ifCondFormula to set
     */
    public void setIfCondFormula(String ifCondFormula) {
        this.ifCondFormula = ifCondFormula;
    }
}
