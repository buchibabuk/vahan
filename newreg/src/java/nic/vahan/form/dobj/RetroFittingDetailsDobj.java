/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author tranC103
 */
public class RetroFittingDetailsDobj implements Serializable, Cloneable {

    private String kit_srno;
    private String kit_type;
    private String kit_manuf;
    private String kit_pucc_norms;
    private String workshop;
    private String workshop_lic_no;
    private String cyl_srno;
    private String approval_no;
    private Date install_dt;
    private Date hydro_dt;
    private Date approval_dt;
    private boolean disable;
    private boolean retrofitInsertUpdateFlag;

    public String getKit_srno() {
        return kit_srno;
    }

    public void setKit_srno(String kit_srno) {
        this.kit_srno = kit_srno;
    }

    public String getKit_type() {
        return kit_type;
    }

    public void setKit_type(String kit_type) {
        this.kit_type = kit_type;
    }

    public String getKit_manuf() {
        return kit_manuf;
    }

    public void setKit_manuf(String kit_manuf) {
        this.kit_manuf = kit_manuf;
    }

    public String getKit_pucc_norms() {
        return kit_pucc_norms;
    }

    public void setKit_pucc_norms(String kit_pucc_norms) {
        this.kit_pucc_norms = kit_pucc_norms;
    }

    public String getWorkshop() {
        return workshop;
    }

    public void setWorkshop(String workshop) {
        this.workshop = workshop;
    }

    public String getWorkshop_lic_no() {
        return workshop_lic_no;
    }

    public void setWorkshop_lic_no(String workshop_lic_no) {
        this.workshop_lic_no = workshop_lic_no;
    }

    public String getCyl_srno() {
        return cyl_srno;
    }

    public void setCyl_srno(String cyl_srno) {
        this.cyl_srno = cyl_srno;
    }

    public String getApproval_no() {
        return approval_no;
    }

    public void setApproval_no(String approval_no) {
        this.approval_no = approval_no;
    }

    public Date getInstall_dt() {
        return install_dt;
    }

    public void setInstall_dt(Date install_dt) {
        this.install_dt = install_dt;
    }

    public Date getApproval_dt() {
        return approval_dt;
    }

    public void setApproval_dt(Date approval_dt) {
        this.approval_dt = approval_dt;
    }

    public Date getHydro_dt() {
        return hydro_dt;
    }

    public void setHydro_dt(Date hydro_dt) {
        this.hydro_dt = hydro_dt;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the disable
     */
    public boolean isDisable() {
        return disable;
    }

    /**
     * @param disable the disable to set
     */
    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public boolean isRetrofitInsertUpdateFlag() {
        return retrofitInsertUpdateFlag;
    }

    public void setRetrofitInsertUpdateFlag(boolean retrofitInsertUpdateFlag) {
        this.retrofitInsertUpdateFlag = retrofitInsertUpdateFlag;
    }
}
