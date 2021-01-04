/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services;

/**
 *
 * @author nicsi
 */
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class VTDocumentModel implements Serializable {

    private static final long serialVersionUID = 4185615046051058821L;
    private String appl_no;
    private String regn_no;
    private String doc_catg_id;
    private BigDecimal doc_id;
    private String doc_unique_no;
    private String doc_uploaded_dt;
    private String doc_url;
    private String state_cd;
    private BigDecimal off_cd;
    private boolean isuploaded;
    private boolean doc_verified;
    private boolean doc_approved;
    private boolean doc_recieved;
    private boolean temp_doc_approved;
    private String temp_doc_approve_dt;
    private String doc_verified_dt;
    private String doc_approve_dt;
    private String doc_recieve_dt;
    private String doc_desc;

//	private Date  temp_doc_approve_dt;
//	private Date doc_verified_dt;
//	 
//	private Date doc_approve_dt;
//	 
//	private Date doc_recieve_dt;
    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getDoc_catg_id() {
        return doc_catg_id;
    }

    public void setDoc_catg_id(String doc_catg_id) {
        this.doc_catg_id = doc_catg_id;
    }

    public BigDecimal getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(BigDecimal doc_id) {
        this.doc_id = doc_id;
    }

    public String getDoc_unique_no() {
        return doc_unique_no;
    }

    public void setDoc_unique_no(String doc_unique_no) {
        this.doc_unique_no = doc_unique_no;
    }

    public String getDoc_uploaded_dt() {
        return doc_uploaded_dt;
    }

    public void setDoc_uploaded_dt(String doc_uploaded_dt) {
        this.doc_uploaded_dt = doc_uploaded_dt;
    }

    public String getDoc_url() {
        return doc_url;
    }

    public void setDoc_url(String doc_url) {
        this.doc_url = doc_url;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public BigDecimal getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(BigDecimal off_cd) {
        this.off_cd = off_cd;
    }

    public boolean isIsuploaded() {
        return isuploaded;
    }

    public void setIsuploaded(boolean isuploaded) {
        this.isuploaded = isuploaded;
    }

    public boolean isDoc_verified() {
        return doc_verified;
    }

    public void setDoc_verified(boolean doc_verified) {
        this.doc_verified = doc_verified;
    }

    public boolean isDoc_approved() {
        return doc_approved;
    }

    public void setDoc_approved(boolean doc_approved) {
        this.doc_approved = doc_approved;
    }

    public boolean isDoc_recieved() {
        return doc_recieved;
    }

    public void setDoc_recieved(boolean doc_recieved) {
        this.doc_recieved = doc_recieved;
    }

    public String getDoc_verified_dt() {
        return doc_verified_dt;
    }

    public void setDoc_verified_dt(String doc_verified_dt) {
        this.doc_verified_dt = doc_verified_dt;
    }

    public String getDoc_approve_dt() {
        return doc_approve_dt;
    }

    public void setDoc_approve_dt(String doc_approve_dt) {
        this.doc_approve_dt = doc_approve_dt;
    }

    public String getDoc_recieve_dt() {
        return doc_recieve_dt;
    }

    public void setDoc_recieve_dt(String doc_recieve_dt) {
        this.doc_recieve_dt = doc_recieve_dt;
    }

    public boolean isTemp_doc_approved() {
        return temp_doc_approved;
    }

    public void setTemp_doc_approved(boolean temp_doc_approved) {
        this.temp_doc_approved = temp_doc_approved;
    }

    public String getTemp_doc_approve_dt() {
        return temp_doc_approve_dt;
    }

    public void setTemp_doc_approve_dt(String temp_doc_approve_dt) {
        this.temp_doc_approve_dt = temp_doc_approve_dt;
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
}
