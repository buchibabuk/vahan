/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ashok Kumar <send2ashok@hotmail.com>
 */
public class TaxSlabDobj implements Serializable {

    private String state_cd;
    private int slab_code;
    private String descr;
    private int pur_cd;
    private String tax_mode;
    private int class_type;
    private String tax_on_vch;
    private Date date_from;
    private Date date_to;
    private String emp_cd;
    private String op_dt;
    private boolean newSlab;
    private List<TaxSlabAddNewDobj> addl_tax_slab_list = new ArrayList<TaxSlabAddNewDobj>();
   
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
     * @return the descr
     */
    public String getDescr() {
        return descr;
    }

    /**
     * @param descr the descr to set
     */
    public void setDescr(String descr) {
        this.descr = descr;
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
     * @return the tax_mode
     */
    public String getTax_mode() {
        return tax_mode;
    }

    /**
     * @param tax_mode the tax_mode to set
     */
    public void setTax_mode(String tax_mode) {
        this.tax_mode = tax_mode;
    }

    /**
     * @return the class_type
     */
    public int getClass_type() {
        return class_type;
    }

    /**
     * @param class_type the class_type to set
     */
    public void setClass_type(int class_type) {
        this.class_type = class_type;
    }

    /**
     * @return the tax_on_vch
     */
    public String getTax_on_vch() {
        return tax_on_vch;
    }

    /**
     * @param tax_on_vch the tax_on_vch to set
     */
    public void setTax_on_vch(String tax_on_vch) {
        this.tax_on_vch = tax_on_vch;
    }

    /**
     * @return the date_from
     */
    public Date getDate_from() {
        return date_from;
    }

    /**
     * @param date_from the date_from to set
     */
    public void setDate_from(Date date_from) {
        this.date_from = date_from;
    }

    /**
     * @return the date_to
     */
    public Date getDate_to() {
        return date_to;
    }

    /**
     * @param date_to the date_to to set
     */
    public void setDate_to(Date date_to) {
        this.date_to = date_to;
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
     * @return the newSlab
     */
    public boolean isNewSlab() {
        return newSlab;
    }

    /**
     * @param newSlab the newSlab to set
     */
    public void setNewSlab(boolean newSlab) {
        this.newSlab = newSlab;
    }

    /**
     * @return the addl_tax_slab_list
     */
    public List<TaxSlabAddNewDobj> getAddl_tax_slab_list() {
        return addl_tax_slab_list;
    }

    /**
     * @param addl_tax_slab_list the addl_tax_slab_list to set
     */
    public void setAddl_tax_slab_list(List<TaxSlabAddNewDobj> addl_tax_slab_list) {
        this.addl_tax_slab_list = addl_tax_slab_list;
    }

    }
