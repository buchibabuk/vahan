/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.fileMonitoring;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Deepak
 */
public class printRejectFilePdfDobj implements Serializable {

    private String reject_by;
    private String reject_date;
    private List<printRejectFilePdfDobj> docsList = new ArrayList();
    private String serialNo = "";
    private String file_no;
    private String serviceName;
    private String reason;

    public String getReject_by() {
        return reject_by;
    }

    public void setReject_by(String reject_by) {
        this.reject_by = reject_by;
    }

    public String getReject_date() {
        return reject_date;
    }

    public void setReject_date(String reject_date) {
        this.reject_date = reject_date;
    }

    public List<printRejectFilePdfDobj> getDocsList() {
        return docsList;
    }

    public void setDocsList(List<printRejectFilePdfDobj> docsList) {
        this.docsList = docsList;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getFile_no() {
        return file_no;
    }

    public void setFile_no(String file_no) {
        this.file_no = file_no;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
