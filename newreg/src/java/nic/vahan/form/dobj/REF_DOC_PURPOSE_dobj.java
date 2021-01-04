/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author AMBRISH
 */
public class REF_DOC_PURPOSE_dobj implements Serializable {

    private int ref_doc_purpose_cd;
    private String ref_doc_purpose_descr;
    private String appl_no;
    private String scan_doc_no;
    private boolean verified;
    private String verified_on;
    private String remarks;

    /**
     * @return the ref_doc_purpose_cd
     */
    public int getRef_doc_purpose_cd() {
        return ref_doc_purpose_cd;
    }

    /**
     * @param ref_doc_purpose_cd the ref_doc_purpose_cd to set
     */
    public void setRef_doc_purpose_cd(int ref_doc_purpose_cd) {
        this.ref_doc_purpose_cd = ref_doc_purpose_cd;
    }

    /**
     * @return the ref_doc_purpose_descr
     */
    public String getRef_doc_purpose_descr() {
        return ref_doc_purpose_descr;
    }

    /**
     * @param ref_doc_purpose_descr the ref_doc_purpose_descr to set
     */
    public void setRef_doc_purpose_descr(String ref_doc_purpose_descr) {
        this.ref_doc_purpose_descr = ref_doc_purpose_descr;
    }

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    /**
     * @return the scan_doc_no
     */
    public String getScan_doc_no() {
        return scan_doc_no;
    }

    /**
     * @param scan_doc_no the scan_doc_no to set
     */
    public void setScan_doc_no(String scan_doc_no) {
        this.scan_doc_no = scan_doc_no;
    }

    public String toString() {
        return "ref_doc_purpose_cd  " + ref_doc_purpose_cd + " ref_doc_purpose_descr  " + ref_doc_purpose_descr + " appl_no " + appl_no + "  Scan_doc_no " + scan_doc_no;
    }

    /**
     * @return the verified
     */
    public boolean isVerified() {
        return verified;
    }

    /**
     * @param verified the verified to set
     */
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    /**
     * @return the verified_on
     */
    public String getVerified_on() {
        return verified_on;
    }

    /**
     * @param verified_on the verified_on to set
     */
    public void setVerified_on(String verified_on) {
        this.verified_on = verified_on;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
