/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nic.vahan.form.bean.ExArmyBean;
import nic.vahan.form.dobj.ExArmyDobj;

/**
 *
 * @author Kartikey Singh
 */
public class ExArmyBeanModel {
    private String tf_Voucher_no;
    private Date tf_VoucherDate;
    private String tf_POP;
    private ExArmyDobj exArmy_dobj_prv;
    private List<ComparisonBeanModel> compBeanList = new ArrayList<>();

    public ExArmyBeanModel() {
    }

    public ExArmyBeanModel(ExArmyBean exArmyBean) {
        this.tf_Voucher_no = exArmyBean.getTf_Voucher_no();
        this.tf_VoucherDate = exArmyBean.getTf_VoucherDate();
        this.tf_POP = exArmyBean.getTf_POP();
        this.exArmy_dobj_prv = exArmyBean.getExArmy_dobj_prv();
    }
    
    public ExArmyDobj setExArmyBean_To_Dobj() {
        ExArmyDobj obj = null;
        if (tf_Voucher_no != null && tf_VoucherDate != null && tf_POP != null) {
            obj = new ExArmyDobj();
            obj.setTf_Voucher_no(getTf_Voucher_no());
            obj.setTf_VoucherDate(getTf_VoucherDate());
            obj.setTf_POP(getTf_POP());
        }

        return obj;
    }

    public String getTf_Voucher_no() {
        return tf_Voucher_no;
    }

    public void setTf_Voucher_no(String tf_Voucher_no) {
        this.tf_Voucher_no = tf_Voucher_no;
    }

    public Date getTf_VoucherDate() {
        return tf_VoucherDate;
    }

    public void setTf_VoucherDate(Date tf_VoucherDate) {
        this.tf_VoucherDate = tf_VoucherDate;
    }

    public String getTf_POP() {
        return tf_POP;
    }

    public void setTf_POP(String tf_POP) {
        this.tf_POP = tf_POP;
    }

    public ExArmyDobj getExArmy_dobj_prv() {
        return exArmy_dobj_prv;
    }

    public void setExArmy_dobj_prv(ExArmyDobj exArmy_dobj_prv) {
        this.exArmy_dobj_prv = exArmy_dobj_prv;
    }

    public List<ComparisonBeanModel> getCompBeanList() {
        return compBeanList;
    }

    public void setCompBeanList(List<ComparisonBeanModel> compBeanList) {
        this.compBeanList = compBeanList;
    }

    @Override
    public String toString() {
        return "ExArmyBeanModel{" + "tf_Voucher_no=" + tf_Voucher_no + ", tf_VoucherDate=" + tf_VoucherDate + ", tf_POP=" + tf_POP + ", exArmy_dobj_prv=" + exArmy_dobj_prv + ", compBeanList=" + compBeanList + '}';
    }
    
}
