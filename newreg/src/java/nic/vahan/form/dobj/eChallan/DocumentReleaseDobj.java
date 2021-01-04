/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class DocumentReleaseDobj implements Serializable {

    private int srNo;
    private String chal_no;
    private String vehicle_no;
    private String release_Date;
    private String release_by;
    private String doc_desc;
    private String validity;
    private String doc_no;
    private String iss_auth;
    private String accused_catg;
    private String rto_name;
    private String other_doc_name;

    /**
     * @return the srNo
     */
    public int getSrNo() {
        return srNo;
    }

    /**
     * @param srNo the srNo to set
     */
    public void setSrNo(int srNo) {
        this.srNo = srNo;
    }

    /**
     * @return the chal_no
     */
    public String getChal_no() {
        return chal_no;
    }

    /**
     * @param chal_no the chal_no to set
     */
    public void setChal_no(String chal_no) {
        this.chal_no = chal_no;
    }

    /**
     * @return the vehicle_no
     */
    public String getVehicle_no() {
        return vehicle_no;
    }

    /**
     * @param vehicle_no the vehicle_no to set
     */
    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }

    /**
     * @return the release_Date
     */
    public String getRelease_Date() {
        return release_Date;
    }

    /**
     * @param release_Date the release_Date to set
     */
    public void setRelease_Date(String release_Date) {
        this.release_Date = release_Date;
    }

    /**
     * @return the release_by
     */
    public String getRelease_by() {
        return release_by;
    }

    /**
     * @param release_by the release_by to set
     */
    public void setRelease_by(String release_by) {
        this.release_by = release_by;
    }

    /**
     * @return the doc_desc
     */
    public String getDoc_desc() {
        return doc_desc;
    }

    /**
     * @param doc_desc the doc_desc to set
     */
    public void setDoc_desc(String doc_desc) {
        this.doc_desc = doc_desc;
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
     * @return the doc_no
     */
    public String getDoc_no() {
        return doc_no;
    }

    /**
     * @param doc_no the doc_no to set
     */
    public void setDoc_no(String doc_no) {
        this.doc_no = doc_no;
    }

    /**
     * @return the iss_auth
     */
    public String getIss_auth() {
        return iss_auth;
    }

    /**
     * @param iss_auth the iss_auth to set
     */
    public void setIss_auth(String iss_auth) {
        this.iss_auth = iss_auth;
    }

    /**
     * @return the accused_catg
     */
    public String getAccused_catg() {
        return accused_catg;
    }

    /**
     * @param accused_catg the accused_catg to set
     */
    public void setAccused_catg(String accused_catg) {
        this.accused_catg = accused_catg;
    }

    /**
     * @return the rto_name
     */
    public String getRto_name() {
        return rto_name;
    }

    /**
     * @param rto_name the rto_name to set
     */
    public void setRto_name(String rto_name) {
        this.rto_name = rto_name;
    }

    /**
     * @return the other_doc_name
     */
    public String getOther_doc_name() {
        return other_doc_name;
    }

    /**
     * @param other_doc_name the other_doc_name to set
     */
    public void setOther_doc_name(String other_doc_name) {
        this.other_doc_name = other_doc_name;
    }
}
