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
public class RejectedFilePdfDO implements Serializable {

    private String rejectedDate;
    private String rejectedBy;
    private int srNo;
    private int pendingDays;
    private String regnDate;
    private String fileNo;
    private String appl_no;
    private String status;
    private String statusDesc;
    private List<RejectedFilePdfDO> filelist = new ArrayList();

    public List<RejectedFilePdfDO> getFilelist() {
        return filelist;
    }

    public void setFilelist(List<RejectedFilePdfDO> filelist) {
        this.filelist = filelist;
    }

    public int getSrNo() {
        return srNo;
    }

    public void setSrNo(int srNo) {
        this.srNo = srNo;
    }

    public int getPendingDays() {
        return pendingDays;
    }

    public void setPendingDays(int pendingDays) {
        this.pendingDays = pendingDays;
    }

    public String getRegnDate() {
        return regnDate;
    }

    public void setRegnDate(String regnDate) {
        this.regnDate = regnDate;
    }

    public String getFileNo() {
        return fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getRejectedDate() {
        return rejectedDate;
    }

    public void setRejectedDate(String rejectedDate) {
        this.rejectedDate = rejectedDate;
    }

    public String getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(String rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }
}
