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
public class ExArmyDobj implements Serializable, Cloneable {

    private String tf_Voucher_no = "";
    private Date tf_VoucherDate;
    private String tf_POP = "";
    private boolean exArmyInsertUpdateFlag;

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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean isExArmyInsertUpdateFlag() {
        return exArmyInsertUpdateFlag;
    }

    public void setExArmyInsertUpdateFlag(boolean exArmyInsertUpdateFlag) {
        this.exArmyInsertUpdateFlag = exArmyInsertUpdateFlag;
    }
    
}
