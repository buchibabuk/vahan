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
public class REF_DOC_dobj implements Serializable {

    private String appl_no;
    private String scan_doc_no;
    private int ref_doc_source_cd;
    private String ref_doc_source_descr = "";
    private byte[] scan_doc;
    private byte[] dig_signature;
    private int scan_doc_purpose_cd;

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

    /**
     * @return the ref_doc_source_cd
     */
    public int getRef_doc_source_cd() {
        return ref_doc_source_cd;
    }

    /**
     * @param ref_doc_source_cd the ref_doc_source_cd to set
     */
    public void setRef_doc_source_cd(int ref_doc_source_cd) {
        this.ref_doc_source_cd = ref_doc_source_cd;
    }

    /**
     * @return the scan_doc
     */
    public byte[] getScan_doc() {
        return scan_doc;
    }

    /**
     * @param scan_doc the scan_doc to set
     */
    public void setScan_doc(byte[] scan_doc) {
        this.scan_doc = scan_doc;
    }

    /**
     * @return the dig_signature
     */
    public byte[] getDig_signature() {
        return dig_signature;
    }

    /**
     * @param dig_signature the dig_signature to set
     */
    public void setDig_signature(byte[] dig_signature) {
        this.dig_signature = dig_signature;
    }

    /**
     * @return the ref_doc_source_descr
     */
    public String getRef_doc_source_descr() {
        return ref_doc_source_descr;
    }

    /**
     * @param ref_doc_source_descr the ref_doc_source_descr to set
     */
    public void setRef_doc_source_descr(String ref_doc_source_descr) {
        this.ref_doc_source_descr = ref_doc_source_descr;
    }

    public String toString() {
        return "appl_no " + appl_no + "  scan_doc_no    " + scan_doc_no + "  ref_doc_source_cd  " + ref_doc_source_cd + "  ref_doc_source_descr    " + ref_doc_source_descr;
    }

    /**
     * @return the scan_doc_purpose_cd
     */
    public int getScan_doc_purpose_cd() {
        return scan_doc_purpose_cd;
    }

    /**
     * @param scan_doc_purpose_cd the scan_doc_purpose_cd to set
     */
    public void setScan_doc_purpose_cd(int scan_doc_purpose_cd) {
        this.scan_doc_purpose_cd = scan_doc_purpose_cd;
    }
}
