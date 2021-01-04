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
public class SubmmitedFilePdfDO implements Serializable {

    private String submmitDate;
    private String submmitBy;
    private int srNo;
    private int pendingDays;
    private String regnDate;
    private String fileNo;
    private String appl_no;
    private String status;
    private List<SubmmitedFilePdfDO> filelist = new ArrayList();

    public String getSubmmitDate() {
        return submmitDate;
    }

    public void setSubmmitDate(String submmitDate) {
        this.submmitDate = submmitDate;
    }

    public String getSubmmitBy() {
        return submmitBy;
    }

    public void setSubmmitBy(String submmitBy) {
        this.submmitBy = submmitBy;
    }

    public List<SubmmitedFilePdfDO> getFilelist() {
        return filelist;
    }

    public void setFilelist(List<SubmmitedFilePdfDO> filelist) {
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
}
