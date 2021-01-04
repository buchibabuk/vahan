/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tranC106
 */
public class VmPermitStateMap implements Serializable {

    private String state_code;
    private int purpose_code;
    private int permit_code;
    private String pur_cd_descr;
    private String pmt_type_descr;
    private List selectDocList = new ArrayList();
    private String documentid;
    private String[] documentList;

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public int getPurpose_code() {
        return purpose_code;
    }

    public void setPurpose_code(int purpose_code) {
        this.purpose_code = purpose_code;
    }

    public int getPermit_code() {
        return permit_code;
    }

    public void setPermit_code(int permit_code) {
        this.permit_code = permit_code;
    }

    public String getDocumentid() {
        return documentid;
    }

    public void setDocumentid(String documentid) {
        this.documentid = documentid;
    }

    public String getPur_cd_descr() {
        return pur_cd_descr;
    }

    public void setPur_cd_descr(String pur_cd_descr) {
        this.pur_cd_descr = pur_cd_descr;
    }

    public String getPmt_type_descr() {
        return pmt_type_descr;
    }

    public void setPmt_type_descr(String pmt_type_descr) {
        this.pmt_type_descr = pmt_type_descr;
    }

    public String[] getDocumentList() {
        return documentList;
    }

    public void setDocumentList(String[] documentList) {
        this.documentList = documentList;
    }

    public List getSelectDocList() {
        return selectDocList;
    }

    public void setSelectDocList(List selectDocList) {
        this.selectDocList = selectDocList;
    }
}
