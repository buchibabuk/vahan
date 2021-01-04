/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author acer
 */
public class DocumentUploadDobj implements Serializable {

    private String applNo;
    private boolean docUpload = false;
    private boolean docVerified = false;
    private boolean docApproved = false;
    private boolean docTempApproved = false;
    private String docDescr;
    private String docInfo;

    /**
     * @return the applNo
     */
    public String getApplNo() {
        return applNo;
    }

    /**
	 * @param applNo
	 *            the applNo to set
	 */
    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    /**
     * @return the docUpload
     */
    public boolean isDocUpload() {
        return docUpload;
    }

    /**
	 * @param docUpload
	 *            the docUpload to set
	 */
    public void setDocUpload(boolean docUpload) {
        this.docUpload = docUpload;
    }

    /**
     * @return the docVerified
     */
    public boolean isDocVerified() {
        return docVerified;
    }

    /**
	 * @param docVerified
	 *            the docVerified to set
	 */
    public void setDocVerified(boolean docVerified) {
        this.docVerified = docVerified;
    }

    /**
     * @return the docApproved
     */
    public boolean isDocApproved() {
        return docApproved;
    }

    /**
	 * @param docApproved
	 *            the docApproved to set
	 */
    public void setDocApproved(boolean docApproved) {
        this.docApproved = docApproved;
    }

    /**
     * @return the docDescr
     */
    public String getDocDescr() {
        return docDescr;
    }

    /**
	 * @param docDescr
	 *            the docDescr to set
	 */
    public void setDocDescr(String docDescr) {
        this.docDescr = docDescr;
    }

    /**
     * @return the docInfo
     */
    public String getDocInfo() {
        return docInfo;
    }

    /**
	 * @param docInfo
	 *            the docInfo to set
	 */
    public void setDocInfo(String docInfo) {
        this.docInfo = docInfo;
    }

    /**
     * @return the docTempApproved
     */
    public boolean isDocTempApproved() {
        return docTempApproved;
    }

    /**
     * @param docTempApproved the docTempApproved to set
     */
    public void setDocTempApproved(boolean docTempApproved) {
        this.docTempApproved = docTempApproved;
    }
}
