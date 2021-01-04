/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.form.bean.HpaBean;
import nic.vahan.form.dobj.HpaDobj;

/**
 *
 * @author Kartikey Singh
 */
public class HpaBeanModel {

    private List list_state;
    private List list_district;
    private List list_hp_type;
    private List<ComparisonBeanModel> compBeanList = new ArrayList<>();
    private HpaDobj hpaDobj;
    private HpaDobj hpa_dobj_prv;
    private boolean disable;
    private Date maxDate;
    private Date minDate;

    public HpaBeanModel() {
    }

    public HpaBeanModel(List list_state, List list_district, List list_hp_type, HpaDobj hpaDobj, HpaDobj hpa_dobj_prv, boolean disable, Date maxDate, Date minDate) {
//        this.list_state = list_state;
//        this.list_district = list_district;
//        this.list_hp_type = list_hp_type;
        this.hpaDobj = hpaDobj;
        this.hpa_dobj_prv = hpa_dobj_prv;
        this.disable = disable;
        this.maxDate = maxDate;
        this.minDate = minDate;
    }

    public HpaBeanModel(HpaBean hpaBean) {
//        this.list_state = hpaBean.getList_state();
//        this.list_district = hpaBean.getList_district();
//        this.list_hp_type = hpaBean.getList_hp_type();
        this.hpaDobj = hpaBean.getHpaDobj();
        this.hpa_dobj_prv = hpaBean.getHpa_dobj_prv();
        this.disable = hpaBean.isDisable();
        this.maxDate = hpaBean.getMaxDate();
        this.minDate = hpaBean.getMinDate();
    }

    public void populateBeanFromModel(HpaBean hpaBean) {
        hpaBean.setHpaDobj(this.hpaDobj);
        hpaBean.setHpa_dobj_prv(this.hpa_dobj_prv);
        hpaBean.setDisable(this.disable);
        hpaBean.setMaxDate(this.maxDate);
        hpaBean.setMinDate(this.minDate);
    }

    public void StateFncrListener() {
        String state_cd = hpaDobj.getFncr_state();
        list_district.clear();
        list_district = MasterTableFiller.getDistrictList(state_cd);
    }

    public List getList_state() {
        return list_state;
    }

    public void setList_state(List list_state) {
        this.list_state = list_state;
    }

    public List getList_district() {
        return list_district;
    }

    public void setList_district(List list_district) {
        this.list_district = list_district;
    }

    public List getList_hp_type() {
        return list_hp_type;
    }

    public void setList_hp_type(List list_hp_type) {
        this.list_hp_type = list_hp_type;
    }

    public List<ComparisonBeanModel> getCompBeanList() {
        return compBeanList;
    }

    public void setCompBeanList(List<ComparisonBeanModel> compBeanList) {
        this.compBeanList = compBeanList;
    }

    public HpaDobj getHpaDobj() {
        return hpaDobj;
    }

    public void setHpaDobj(HpaDobj hpaDobj) {
        this.hpaDobj = hpaDobj;
    }

    public HpaDobj getHpa_dobj_prv() {
        return hpa_dobj_prv;
    }

    public void setHpa_dobj_prv(HpaDobj hpa_dobj_prv) {
        this.hpa_dobj_prv = hpa_dobj_prv;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }
}
