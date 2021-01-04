/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.clients.dobj;

import java.util.List;

public class ResponseDocument {

    private String applno;
    private String state;
    private String purposeCode;
    private String purposeName;
    private List<UploadedList> uploadedList;
    private List<NonUploadedList> nonUploadedList;
    private List<MandatoryList> mandatoryList;

    public ResponseDocument() {
        super();

    }

    public ResponseDocument(String applno, String state, String purposeCode, String purposeName,
            List<UploadedList> uploadedList, List<NonUploadedList> nonUploadedList,
            List<MandatoryList> mandatoryList) {
        super();
        this.applno = applno;
        this.state = state;
        this.purposeCode = purposeCode;
        this.purposeName = purposeName;
        this.uploadedList = uploadedList;
        this.nonUploadedList = nonUploadedList;
        this.mandatoryList = mandatoryList;
    }

    public String getApplno() {
        return applno;
    }

    public String getState() {
        return state;
    }

    public String getPurposeCode() {
        return purposeCode;
    }

    public String getPurposeName() {
        return purposeName;
    }

    public List<UploadedList> getUploadedList() {
        return uploadedList;
    }

    public List<NonUploadedList> getNonUploadedList() {
        return nonUploadedList;
    }

    public List<MandatoryList> getMandatoryList() {
        return mandatoryList;
    }

    public void setApplno(String applno) {
        this.applno = applno;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    public void setPurposeName(String purposeName) {
        this.purposeName = purposeName;
    }

    public void setUploadedList(List<UploadedList> uploadedList) {
        this.uploadedList = uploadedList;
    }

    public void setNonUploadedList(List<NonUploadedList> nonUploadedList) {
        this.nonUploadedList = nonUploadedList;
    }

    public void setMandatoryList(List<MandatoryList> mandatoryList) {
        this.mandatoryList = mandatoryList;
    }
}
