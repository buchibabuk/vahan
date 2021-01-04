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
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.form.dobj.HpaDobj;
import static nic.vahan.server.ServerUtil.Compare;

/**
 *
 * @author AMBRISH
 */
@ManagedBean(name = "hpa_bean")
@ViewScoped
public class HpaBean implements Serializable {

    private List list_state;
    private List list_district;
    private List list_hp_type;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private HpaDobj hpaDobj = new HpaDobj();
    private HpaDobj hpa_dobj_prv;
    private boolean disable;
    private Date maxDate = DateUtil.parseDate(DateUtil.getCurrentDate());
    private Date minDate = DateUtil.parseDate(DateUtil.getCurrentDate());

    public HpaBean() {

        list_district = new ArrayList();
        list_hp_type = new ArrayList();
        list_state = new ArrayList();

        list_state = MasterTableFiller.getStateList();

        String[][] data = MasterTableFiller.masterTables.VM_HP_TYPE.getData();
        for (int i = 0; i < data.length; i++) {
            list_hp_type.add(new SelectItem(data[i][0], data[i][1]));
        }
        minDate = DateUtil.parseDate(DateUtil.getCurrentDate());
    }

    public void StateFncrListener() {
        String state_cd = hpaDobj.getFncr_state();
        list_district.clear();
        list_district = MasterTableFiller.getDistrictList(state_cd);
    }

    public List<ComparisonBean> addToComapreChangesList(List<ComparisonBean> compBeanListPrev) throws VahanException {

        List<ComparisonBean> list = compareChagnes();

        if (compBeanListPrev == null) {
            compBeanListPrev = new ArrayList<ComparisonBean>();
        }
        if (list.size() > 0) {
            compBeanListPrev.addAll(list);
        }

        return compBeanListPrev;
    }

    public List<ComparisonBean> compareChagnes() throws VahanException {

        if (hpa_dobj_prv == null) {
            return compBeanList;
        }
        compBeanList.clear();

        Compare("Hypothecation_Type", hpa_dobj_prv.getHp_type(), hpaDobj.getHp_type(), compBeanList);
        Compare("fncr_Name ", hpa_dobj_prv.getFncr_name(), hpaDobj.getFncr_name(), compBeanList);
        Compare("fncr_Add1", hpa_dobj_prv.getFncr_add1(), hpaDobj.getFncr_add1(), compBeanList);
        Compare("fncr_Add2", hpa_dobj_prv.getFncr_add2(), hpaDobj.getFncr_add2(), compBeanList);
        Compare("fncr_Add3", hpa_dobj_prv.getFncr_add3(), hpaDobj.getFncr_add3(), compBeanList);
        Compare("fncr_State", hpa_dobj_prv.getFncr_district(), hpaDobj.getFncr_district(), compBeanList);
        Compare("fncr_Pincode", hpa_dobj_prv.getFncr_pincode(), hpaDobj.getFncr_pincode(), compBeanList);
        Compare("finace_from_dt", hpa_dobj_prv.getFrom_dt(), hpaDobj.getFrom_dt(), compBeanList);

        return compBeanList;
    }

    public void componentEditable(boolean flag) {
        flag = !flag;
        setDisable(flag);
    }

    /**
     * @return the list_district
     */
    public List getList_district() {
        return list_district;
    }

    /**
     * @param list_district the list_district to set
     */
    public void setList_district(List list_district) {
        this.list_district = list_district;
    }

    /**
     * @return the list_hp_type
     */
    public List getList_hp_type() {
        return list_hp_type;
    }

    /**
     * @param list_hp_type the list_hp_type to set
     */
    public void setList_hp_type(List list_hp_type) {
        this.list_hp_type = list_hp_type;
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

    /**
     * @return the hpa_dobj_prv
     */
    public HpaDobj getHpa_dobj_prv() {
        return hpa_dobj_prv;
    }

    /**
     * @param hpa_dobj_prv the hpa_dobj_prv to set
     */
    public void setHpa_dobj_prv(HpaDobj hpa_dobj_prv) {
        this.hpa_dobj_prv = hpa_dobj_prv;
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

    /**
     * @return the list_state
     */
    public List getList_state() {
        return list_state;
    }

    /**
     * @param list_state the list_state to set
     */
    public void setList_state(List list_state) {
        this.list_state = list_state;
    }

    /**
     * @return the hpaDobj
     */
    public HpaDobj getHpaDobj() {
        return hpaDobj;
    }

    /**
     * @param hpaDobj the hpaDobj to set
     */
    public void setHpaDobj(HpaDobj hpaDobj) {
        this.hpaDobj = hpaDobj;
    }

    /**
     * @return the maxDate
     */
    public Date getMaxDate() {
        return maxDate;
    }

    /**
     * @param maxDate the maxDate to set
     */
    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    /**
     * @return the minDate
     */
    public Date getMinDate() {
        return minDate;
    }

    /**
     * @param minDate the minDate to set
     */
    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }
}
