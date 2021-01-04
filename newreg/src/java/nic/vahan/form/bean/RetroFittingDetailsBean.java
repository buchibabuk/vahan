/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import static nic.vahan.server.ServerUtil.Compare;

/**
 *
 * @author tranC103
 */
@ManagedBean
@ViewScoped
public class RetroFittingDetailsBean implements Serializable {

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
    private boolean cngDetails_Visibility_tab = true;
    private RetroFittingDetailsDobj cng_dobj_prv;
    private List compBeanList = new ArrayList<ComparisonBean>();
    private Date minDate=DateUtil.parseDate(DateUtil.getCurrentDate());
    private Date minHydroDate=DateUtil.parseDate(DateUtil.getCurrentDate());
    private boolean disable = false;

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

    public List<ComparisonBean> compareChagnes() throws VahanException {

        RetroFittingDetailsDobj dobj = getCng_dobj_prv();

        if (dobj == null) {
            return getCompBeanList();
        }
        getCompBeanList().clear();

        Compare("KIT No", dobj.getKit_srno(), this.tf_kit_no, (ArrayList) getCompBeanList());
        Compare("KIT Type", dobj.getKit_type(), this.tf_kit_type, (ArrayList) getCompBeanList());
        Compare("Manufacture Name", dobj.getKit_manuf(), this.tf_manu_name, (ArrayList) getCompBeanList());
        Compare("Workshop", dobj.getWorkshop(), this.tf_workshop, (ArrayList) getCompBeanList());
        Compare("License No", dobj.getWorkshop_lic_no(), this.tf_license_no, (ArrayList) getCompBeanList());
        Compare("Cyclinder Sr. No", dobj.getCyl_srno(), this.tf_cyc_sr_no, (ArrayList) getCompBeanList());
        Compare("PUCC Norms", dobj.getKit_pucc_norms(), this.tf_poll_norms, (ArrayList) getCompBeanList());
        Compare("Approval Letter No", dobj.getApproval_no(), this.tf_approv_lettr_no, (ArrayList) getCompBeanList());
        Compare("Installation Date", dobj.getInstall_dt(), this.cal_install_dt, (ArrayList) getCompBeanList());
        Compare("Hydro Date", dobj.getHydro_dt(), getCal_hydro_dt(), (ArrayList) getCompBeanList());
        Compare("Approval Date", dobj.getApproval_dt(), this.cal_approv_dt, (ArrayList) getCompBeanList());

        return getCompBeanList();

    }

    public List<ComparisonBean> addToComapreChangesList(List<ComparisonBean> compBeanListPrev) throws VahanException {

        List<ComparisonBean> list = compareChagnes();

        if (compBeanListPrev == null) {
            compBeanListPrev = new ArrayList<ComparisonBean>();
        }
        if (!list.isEmpty()) {
            compBeanListPrev.addAll(list);
        }

        return compBeanListPrev;
    }

    /**
     * @return the cngDetails_Visibility_tab
     */
    public boolean isCngDetails_Visibility_tab() {
        return cngDetails_Visibility_tab;
    }

    /**
     * @param cngDetails_Visibility_tab the cngDetails_Visibility_tab to set
     */
    public void setCngDetails_Visibility_tab(boolean cngDetails_Visibility_tab) {
        this.cngDetails_Visibility_tab = cngDetails_Visibility_tab;
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

    public Date getCal_approv_dt() {
        return cal_approv_dt;
    }

    public void setCal_approv_dt(Date cal_approv_dt) {
        this.cal_approv_dt = cal_approv_dt;
    }

    public Date getCal_hydro_dt() {
        return cal_hydro_dt;
    }

    public void setCal_hydro_dt(Date cal_hydro_dt) {
        this.cal_hydro_dt = cal_hydro_dt;
    }

    public RetroFittingDetailsDobj getCng_dobj_prv() {
        return cng_dobj_prv;
    }

    public void setCng_dobj_prv(RetroFittingDetailsDobj cng_dobj_prv) {
        this.cng_dobj_prv = cng_dobj_prv;
    }

    /**
     * @return the compBeanList
     */
    public List getCompBeanList() {
        return compBeanList;
    }

    /**
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(List compBeanList) {
        this.compBeanList = compBeanList;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
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

    public Date getMinHydroDate() {
        return minHydroDate;
    }

    public void setMinHydroDate(Date minHydroDate) {
        this.minHydroDate = minHydroDate;
    }
}
