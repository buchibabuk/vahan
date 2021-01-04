/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nic.vahan.form.bean.RetroFittingDetailsBean;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;

/**
 *
 * @author Kartikey Singh
 */
public class RetroFittingDetailsBeanModel {

    private String tf_kit_no;
    private String tf_kit_type;
    private String tf_manu_name;
    private String tf_workshop;
    private String tf_license_no;
    private String tf_cyc_sr_no;
    private String tf_poll_norms;
    private String tf_approv_lettr_no;
    private Date cal_install_dt;
    private Date cal_hydro_dt;
    private Date cal_approv_dt;
    private boolean cngDetails_Visibility_tab;
    private RetroFittingDetailsDobj cng_dobj_prv;
    private List compBeanList = new ArrayList<ComparisonBeanModel>();
    private Date minDate;
    private Date minHydroDate;
    private boolean disable;

    public RetroFittingDetailsBeanModel() {
    }

    public RetroFittingDetailsBeanModel(RetroFittingDetailsBean cngDetailsBean) {
        this.tf_kit_no = cngDetailsBean.getTf_kit_no();
        this.tf_kit_type = cngDetailsBean.getTf_kit_type();
        this.tf_manu_name = cngDetailsBean.getTf_manu_name();
        this.tf_workshop = cngDetailsBean.getTf_workshop();
        this.tf_license_no = cngDetailsBean.getTf_license_no();
        this.tf_cyc_sr_no = cngDetailsBean.getTf_cyc_sr_no();
        this.tf_poll_norms = cngDetailsBean.getTf_poll_norms();
        this.tf_approv_lettr_no = cngDetailsBean.getTf_approv_lettr_no();
        this.cal_install_dt = cngDetailsBean.getCal_install_dt();
        this.cal_hydro_dt = cngDetailsBean.getCal_hydro_dt();
        this.cal_approv_dt = cngDetailsBean.getCal_approv_dt();
        this.cngDetails_Visibility_tab = cngDetailsBean.isCngDetails_Visibility_tab();
        this.cng_dobj_prv = cngDetailsBean.getCng_dobj_prv();
        this.minDate = cngDetailsBean.getMinDate();
        this.minHydroDate = cngDetailsBean.getMinHydroDate();
        this.disable = cngDetailsBean.isDisable();
    }

    public void populateBeanFromModel(RetroFittingDetailsBean cngDetailsBean) {
        cngDetailsBean.setTf_kit_no(this.tf_kit_no);
        cngDetailsBean.setTf_kit_type(this.tf_kit_type);
        cngDetailsBean.setTf_manu_name(this.tf_manu_name);
        cngDetailsBean.setTf_workshop(this.tf_workshop);
        cngDetailsBean.setTf_license_no(this.tf_license_no);
        cngDetailsBean.setTf_cyc_sr_no(this.tf_cyc_sr_no);
        cngDetailsBean.setTf_poll_norms(this.tf_poll_norms);
        cngDetailsBean.setTf_approv_lettr_no(this.tf_approv_lettr_no);
        cngDetailsBean.setCal_install_dt(this.cal_install_dt);
        cngDetailsBean.setCal_hydro_dt(this.cal_hydro_dt);
        cngDetailsBean.setCal_approv_dt(this.cal_approv_dt);
        cngDetailsBean.setCngDetails_Visibility_tab(this.cngDetails_Visibility_tab);
        cngDetailsBean.setCng_dobj_prv(this.cng_dobj_prv);
        cngDetailsBean.setMinDate(this.minDate);
        cngDetailsBean.setMinHydroDate(this.minHydroDate);
        cngDetailsBean.setDisable(this.disable);
    }

    public RetroFittingDetailsDobj setBean_To_Dobj() {
        RetroFittingDetailsDobj obj = null;
        if (tf_kit_no != null && tf_kit_type != null && tf_workshop != null && tf_license_no != null && tf_manu_name != null
                && tf_cyc_sr_no != null && tf_poll_norms != null && tf_approv_lettr_no != null && cal_approv_dt != null) {
            obj = new RetroFittingDetailsDobj();
            obj.setKit_srno(tf_kit_no);
            obj.setKit_type(tf_kit_type);
            obj.setKit_manuf(tf_manu_name);
            obj.setWorkshop(tf_workshop);
            obj.setWorkshop_lic_no(tf_license_no);
            obj.setCyl_srno(tf_cyc_sr_no);
            obj.setKit_pucc_norms(tf_poll_norms);
            obj.setApproval_no(tf_approv_lettr_no);
            obj.setInstall_dt(cal_install_dt);
            obj.setHydro_dt(cal_hydro_dt);
            obj.setApproval_dt(cal_approv_dt);
            obj.setDisable(disable);
        }
        return obj;
    }

    public String getTf_kit_no() {
        return tf_kit_no;
    }

    public void setTf_kit_no(String tf_kit_no) {
        this.tf_kit_no = tf_kit_no;
    }

    public String getTf_kit_type() {
        return tf_kit_type;
    }

    public void setTf_kit_type(String tf_kit_type) {
        this.tf_kit_type = tf_kit_type;
    }

    public String getTf_manu_name() {
        return tf_manu_name;
    }

    public void setTf_manu_name(String tf_manu_name) {
        this.tf_manu_name = tf_manu_name;
    }

    public String getTf_workshop() {
        return tf_workshop;
    }

    public void setTf_workshop(String tf_workshop) {
        this.tf_workshop = tf_workshop;
    }

    public String getTf_license_no() {
        return tf_license_no;
    }

    public void setTf_license_no(String tf_license_no) {
        this.tf_license_no = tf_license_no;
    }

    public String getTf_cyc_sr_no() {
        return tf_cyc_sr_no;
    }

    public void setTf_cyc_sr_no(String tf_cyc_sr_no) {
        this.tf_cyc_sr_no = tf_cyc_sr_no;
    }

    public String getTf_poll_norms() {
        return tf_poll_norms;
    }

    public void setTf_poll_norms(String tf_poll_norms) {
        this.tf_poll_norms = tf_poll_norms;
    }

    public String getTf_approv_lettr_no() {
        return tf_approv_lettr_no;
    }

    public void setTf_approv_lettr_no(String tf_approv_lettr_no) {
        this.tf_approv_lettr_no = tf_approv_lettr_no;
    }

    public Date getCal_install_dt() {
        return cal_install_dt;
    }

    public void setCal_install_dt(Date cal_install_dt) {
        this.cal_install_dt = cal_install_dt;
    }

    public Date getCal_hydro_dt() {
        return cal_hydro_dt;
    }

    public void setCal_hydro_dt(Date cal_hydro_dt) {
        this.cal_hydro_dt = cal_hydro_dt;
    }

    public Date getCal_approv_dt() {
        return cal_approv_dt;
    }

    public void setCal_approv_dt(Date cal_approv_dt) {
        this.cal_approv_dt = cal_approv_dt;
    }

    public boolean isCngDetails_Visibility_tab() {
        return cngDetails_Visibility_tab;
    }

    public void setCngDetails_Visibility_tab(boolean cngDetails_Visibility_tab) {
        this.cngDetails_Visibility_tab = cngDetails_Visibility_tab;
    }

    public RetroFittingDetailsDobj getCng_dobj_prv() {
        return cng_dobj_prv;
    }

    public void setCng_dobj_prv(RetroFittingDetailsDobj cng_dobj_prv) {
        this.cng_dobj_prv = cng_dobj_prv;
    }

    public List getCompBeanList() {
        return compBeanList;
    }

    public void setCompBeanList(List compBeanList) {
        this.compBeanList = compBeanList;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    /*
     * Created by Sai
     */
    public void setDobj_To_Bean(RetroFittingDetailsDobj obj) {
        if (obj != null) {
            tf_kit_no = obj.getKit_srno();
            tf_kit_type = obj.getKit_type();
            tf_manu_name = obj.getKit_manuf();
            tf_workshop = obj.getWorkshop();
            tf_license_no = obj.getWorkshop_lic_no();
            tf_cyc_sr_no = obj.getCyl_srno();
            tf_poll_norms = obj.getKit_pucc_norms();
            tf_approv_lettr_no = obj.getApproval_no();
            cal_install_dt = obj.getInstall_dt();
            cal_hydro_dt = obj.getHydro_dt();
            cal_approv_dt = obj.getApproval_dt();
            disable = obj.isDisable();
        } else {
            return;
        }
    }

    public Date getMinHydroDate() {
        return minHydroDate;
    }

    public void setMinHydroDate(Date minHydroDate) {
        this.minHydroDate = minHydroDate;
    }

    @Override
    public String toString() {
        return "RetroFittingDetailsBeanModel{" + "tf_kit_no=" + tf_kit_no + ", tf_kit_type=" + tf_kit_type + ", tf_manu_name=" + tf_manu_name + ", tf_workshop=" + tf_workshop + ", tf_license_no=" + tf_license_no + ", tf_cyc_sr_no=" + tf_cyc_sr_no + ", tf_poll_norms=" + tf_poll_norms + ", tf_approv_lettr_no=" + tf_approv_lettr_no + ", cal_install_dt=" + cal_install_dt + ", cal_hydro_dt=" + cal_hydro_dt + ", cal_approv_dt=" + cal_approv_dt + ", cngDetails_Visibility_tab=" + cngDetails_Visibility_tab + ", cng_dobj_prv=" + cng_dobj_prv + ", compBeanList=" + compBeanList + ", minDate=" + minDate + ", disable=" + disable + '}';
    }

}
