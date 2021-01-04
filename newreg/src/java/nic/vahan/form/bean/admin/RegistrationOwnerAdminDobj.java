/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.admin;

import java.io.Serializable;
import java.util.List;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.smartcard.SmartCardDobj;

/**
 *
 * @author nicsi
 */
public class RegistrationOwnerAdminDobj implements Serializable {

    private Owner_dobj ownerDobj;
    private List<ComparisonBean> listChanges;
    private int regVehType;
    private String remarks;
    private boolean isHypth = false;
    private SmartCardDobj smartCardDobj = null;
    private String admin_Remarks;
    private String changedata_appl_no = "";
    private Status_dobj status_dobj;
    private boolean ownerDetailsFlag;
    private String prevTempApplno;

    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    public List<ComparisonBean> getListChanges() {
        return listChanges;
    }

    public void setListChanges(List<ComparisonBean> listChanges) {
        this.listChanges = listChanges;
    }

    public int getRegVehType() {
        return regVehType;
    }

    public void setRegVehType(int regVehType) {
        this.regVehType = regVehType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isIsHypth() {
        return isHypth;
    }

    public void setIsHypth(boolean isHypth) {
        this.isHypth = isHypth;
    }

    public SmartCardDobj getSmartCardDobj() {
        return smartCardDobj;
    }

    public void setSmartCardDobj(SmartCardDobj smartCardDobj) {
        this.smartCardDobj = smartCardDobj;
    }

    public String getAdmin_Remarks() {
        return admin_Remarks;
    }

    public void setAdmin_Remarks(String admin_Remarks) {
        this.admin_Remarks = admin_Remarks;
    }

    public String getChangedata_appl_no() {
        return changedata_appl_no;
    }

    public void setChangedata_appl_no(String changedata_appl_no) {
        this.changedata_appl_no = changedata_appl_no;
    }

    public Status_dobj getStatus_dobj() {
        return status_dobj;
    }

    public void setStatus_dobj(Status_dobj status_dobj) {
        this.status_dobj = status_dobj;
    }

    public boolean isOwnerDetailsFlag() {
        return ownerDetailsFlag;
    }

    public void setOwnerDetailsFlag(boolean ownerDetailsFlag) {
        this.ownerDetailsFlag = ownerDetailsFlag;
    }

    public String getPrevTempApplno() {
        return prevTempApplno;
    }

    public void setPrevTempApplno(String prevTempApplno) {
        this.prevTempApplno = prevTempApplno;
    }
}
