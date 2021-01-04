/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;

/**
 *
 * @author tranC105
 */
public class DocImpoundDobj implements Serializable {

    private String document;
    private String docNo;
    private String issueAuth;
    private String validUpto;
    private String docDesc;
    private String accusedCatg;
    private String accusedDescR;
    private String otherDocName;

    public DocImpoundDobj(String accusedCatg, String accusedDescR, String document, String docNo, String issueAuth, String validUpto, String otherDocName) {

        this.document = document;
        this.docNo = docNo;
        this.issueAuth = issueAuth;
        this.validUpto = validUpto;
        this.accusedCatg = accusedCatg;
        this.accusedDescR = accusedDescR;
        this.otherDocName = otherDocName;
    }

    public DocImpoundDobj() {
    }

    /**
     * @return the document
     */
    public String getDocument() {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(String document) {
        this.document = document;
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
     * @return the validUpto
     */
    public String getValidUpto() {
        return validUpto;
    }

    /**
     * @param validUpto the validUpto to set
     */
    public void setValidUpto(String validUpto) {
        this.validUpto = validUpto;
    }

    public String getDocDesc() {
        return docDesc;
    }

    public void setDocDesc(String docDesc) {
        this.docDesc = docDesc;
    }

    public String getAccusedCatg() {
        return accusedCatg;
    }

    public void setAccusedCatg(String accusedCatg) {
        this.accusedCatg = accusedCatg;
    }

    public String getAccusedDescR() {
        return accusedDescR;
    }

    public void setAccusedDescR(String accusedDescR) {
        this.accusedDescR = accusedDescR;
    }

    /**
     * @return the otherDocName
     */
    public String getOtherDocName() {
        return otherDocName;
    }

    /**
     * @param otherDocName the otherDocName to set
     */
    public void setOtherDocName(String otherDocName) {
        this.otherDocName = otherDocName;
    }
}
