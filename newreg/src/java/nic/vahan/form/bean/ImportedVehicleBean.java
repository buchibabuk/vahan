/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.form.dobj.ImportedVehicleDobj;
import nic.vahan.server.CommonUtils;
import static nic.vahan.server.ServerUtil.Compare;

/**
 *
 * @author tranC103
 */
@ManagedBean
@ViewScoped
public class ImportedVehicleBean implements Serializable {

    private int cm_country_imp;
    private List cm_country_imp_list = new ArrayList();
    private String tf_dealer_imp = "";
    private String tf_place_imp = "";
    private String tf_foreign_imp = "";
    private Integer tf_YOM_imp;
    private ImportedVehicleDobj imp_dobj_prv;
    private List<ComparisonBean> compBeanList = new ArrayList<>();

    public ImportedVehicleBean() {
        String[][] data = MasterTableFiller.masterTables.TM_COUNTRY.getData();
        for (int i = 0; i < data.length; i++) {
            cm_country_imp_list.add(new SelectItem(data[i][0], data[i][1]));
        }
    }

    public ImportedVehicleDobj setBean_to_Dobj() {
        ImportedVehicleDobj obj = null;
        if (cm_country_imp != 0 && tf_YOM_imp != 0 && tf_YOM_imp != null
                && !CommonUtils.isNullOrBlank(tf_dealer_imp)
                && !CommonUtils.isNullOrBlank(tf_place_imp) && !CommonUtils.isNullOrBlank(tf_foreign_imp)) {
            obj = new ImportedVehicleDobj();
            obj.setCm_country_imp(cm_country_imp);
            obj.setTf_dealer_imp(tf_dealer_imp.trim());
            obj.setTf_YOM_imp(tf_YOM_imp);
            obj.setTf_foreign_imp(tf_foreign_imp.trim());
            obj.setTf_place_imp(tf_place_imp.trim());
        }
        return obj;
    }

    public void setDobj_to_Bean(ImportedVehicleDobj obj) {
        if (obj != null) {
            setCm_country_imp(obj.getCm_country_imp());
            setTf_YOM_imp(obj.getTf_YOM_imp());
            setTf_dealer_imp(obj.getTf_dealer_imp());
            setTf_foreign_imp(obj.getTf_foreign_imp());
            setTf_place_imp(obj.getTf_place_imp());
        }
    }

    public List<ComparisonBean> compareChagnes() throws VahanException {

        ImportedVehicleDobj dobj = getImp_dobj_prv();

        if (dobj == null) {
            return getCompBeanList();
        }
        getCompBeanList().clear();

        Compare("Country Name", dobj.getCm_country_imp(), this.cm_country_imp, (ArrayList) getCompBeanList());
        Compare("Dealer", dobj.getTf_dealer_imp(), this.tf_dealer_imp, (ArrayList) getCompBeanList());
        Compare("Foreign Reg No", dobj.getTf_foreign_imp(), this.tf_foreign_imp, (ArrayList) getCompBeanList());
        Compare("Place", dobj.getTf_place_imp(), this.tf_place_imp, (ArrayList) getCompBeanList());
        Compare("Manufacture Year", dobj.getTf_YOM_imp(), this.tf_YOM_imp, (ArrayList) getCompBeanList());

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

    public int getCm_country_imp() {
        return cm_country_imp;
    }

    public void setCm_country_imp(int cm_country_imp) {
        this.cm_country_imp = cm_country_imp;
    }

    public List getCm_country_imp_list() {
        return cm_country_imp_list;
    }

    public void setCm_country_imp_list(List cm_country_imp_list) {
        this.cm_country_imp_list = cm_country_imp_list;
    }

    public String getTf_dealer_imp() {
        return tf_dealer_imp;
    }

    public void setTf_dealer_imp(String tf_dealer_imp) {
        this.tf_dealer_imp = tf_dealer_imp;
    }

    public String getTf_place_imp() {
        return tf_place_imp;
    }

    public void setTf_place_imp(String tf_place_imp) {
        this.tf_place_imp = tf_place_imp;
    }

    public String getTf_foreign_imp() {
        return tf_foreign_imp;
    }

    public void setTf_foreign_imp(String tf_foreign_imp) {
        this.tf_foreign_imp = tf_foreign_imp;
    }

    public Integer getTf_YOM_imp() {
        return tf_YOM_imp;
    }

    public void setTf_YOM_imp(Integer tf_YOM_imp) {
        this.tf_YOM_imp = tf_YOM_imp;
    }

    public ImportedVehicleDobj getImp_dobj_prv() {
        return imp_dobj_prv;
    }

    public void setImp_dobj_prv(ImportedVehicleDobj imp_dobj_prv) {
        this.imp_dobj_prv = imp_dobj_prv;
    }

    /**
     * @return the compBeanList
     */
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    /**
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    public void validateImportedYear(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        Integer yom = (Integer) value;
        String msg = " Year of Manufacture Can't be greater than current year";
        Date dt = new Date();
        Calendar cl = Calendar.getInstance();
        cl.setTime(dt);
        int year = cl.get(Calendar.YEAR);
        if (yom != null) {
            if (year < yom) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
            }
        }

    }
}
