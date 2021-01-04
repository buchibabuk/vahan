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
import nic.vahan.form.dobj.ExArmyDobj;
import static nic.vahan.server.ServerUtil.Compare;

/**
 *
 * @author tranC103
 */
@ManagedBean
@ViewScoped
public class ExArmyBean implements Serializable {

    private String tf_Voucher_no;
    private Date tf_VoucherDate;
    private String tf_POP;
    private ExArmyDobj exArmy_dobj_prv;
    private List<ComparisonBean> compBeanList = new ArrayList<>();

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

    public void setDobj_To_Bean(ExArmyDobj obj) {
        if (obj != null) {
            setTf_Voucher_no(obj.getTf_Voucher_no());
            setTf_VoucherDate(obj.getTf_VoucherDate());
            setTf_POP(obj.getTf_POP());
        }
    }

    public List<ComparisonBean> compareChagnes() throws VahanException {

        ExArmyDobj dobj = getExArmy_dobj_prv();

        if (dobj == null) {
            return getCompBeanList();
        }
        getCompBeanList().clear();

        Compare("Voucher No", dobj.getTf_Voucher_no(), this.tf_Voucher_no, (ArrayList) getCompBeanList());
        Compare("Voucher Date", dobj.getTf_VoucherDate(), this.tf_VoucherDate, (ArrayList) getCompBeanList());
        Compare("Place Of Purchase", dobj.getTf_POP(), this.tf_POP, (ArrayList) getCompBeanList());

        return getCompBeanList();

    }

    public void resetExarmyDetails() {
        this.setTf_POP(null);
        this.setTf_Voucher_no(null);
        this.setTf_VoucherDate(null);
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

    public ExArmyDobj getExArmy_dobj_prv() {
        return exArmy_dobj_prv;
    }

    public void setExArmy_dobj_prv(ExArmyDobj exArmy_dobj_prv) {
        this.exArmy_dobj_prv = exArmy_dobj_prv;
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
}
