/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;

/**
 *
 * @author sushil
 */
public class DocTableDobj implements Serializable {

    private String docCode;
    private String validity;
    private String issueAuth;
    private String accCategInDocImp;
    private String docNo;
    private String documentDesc;
    private String showdate;
    private String docName;
    private String accusedDescDocImpnd;

    public DocTableDobj() {
    }

    public DocTableDobj(String docCode, String accusedDescDocImpnd, String documentDesc, String validity, String issueAuth, String accCategInDocImp, String docNo, String showdate, String docName) {
        this.docCode = docCode;
        this.validity = validity;
        this.issueAuth = issueAuth;
        this.accCategInDocImp = accCategInDocImp;
        this.docNo = docNo;
        this.documentDesc = documentDesc;
        this.accusedDescDocImpnd = accusedDescDocImpnd;
        this.showdate = showdate;
        this.docName = docName;
    }

    @Override
    public String toString() {
        return "DODoctable{" + "doc_code=" + docCode + ", validity=" + validity + ", issue_auth=" + issueAuth + ", showdate=" + showdate + ",accCategInDocImp=" + accCategInDocImp + ", docNo=" + docNo + ", docName=" + docName + '}';
    }

    /**
     * @return the docCode
     */
    public String getDocCode() {
        return docCode;
    }

    /**
     * @param docCode the docCode to set
     */
    public void setDocCode(String docCode) {
        this.docCode = docCode;
    }

    /**
     * @return the issueAuth
     */
    public String getIssueAuth() {
        return issueAuth;
    }

    /**
     * @param issueAuth the issueAuth to set
     */
    public void setIssueAuth(String issueAuth) {
        this.issueAuth = issueAuth;
    }

    /**
     * @return the validity
     */
    public String getValidity() {
        return validity;
    }

    /**
     * @param validity the validity to set
     */
    public void setValidity(String validity) {
        this.validity = validity;
    }

    /**
     * @return the accCategInDocImp
     */
    public String getAccCategInDocImp() {
        return accCategInDocImp;
    }

    /**
     * @param accCategInDocImp the accCategInDocImp to set
     */
    public void setAccCategInDocImp(String accCategInDocImp) {
        this.accCategInDocImp = accCategInDocImp;
    }

    /**
     * @return the docNo
     */
    public String getDocNo() {
        return docNo;
    }

    /**
     * @param docNo the docNo to set
     */
    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    /**
     * @return the docName
     */
    public String getDocName() {
        return docName;
    }

    /**
     * @param docName the docName to set
     */
    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocumentDesc() {
        return documentDesc;
    }

    public void setDocumentDesc(String documentDesc) {
        this.documentDesc = documentDesc;
    }

    public String getAccusedDescDocImpnd() {
        return accusedDescDocImpnd;
    }

    public void setAccusedDescDocImpnd(String accusedDescDocImpnd) {
        this.accusedDescDocImpnd = accusedDescDocImpnd;
    }

    public String getShowdate() {
        return showdate;
    }

    public void setShowdate(String showdate) {
        this.showdate = showdate;
    }
}
