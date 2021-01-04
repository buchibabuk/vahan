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
public class printFilePdfDobj implements Serializable {

    private int srNo;
    private String dealer_name;
    private String rto_name;
    private int off_cd;
    private String approve_by;
    private String approve_date;
    private List<printFilePdfDobj> filesList = new ArrayList();
    private String serialNo = "";
    private String file_no;
    private String status;
    private String status_desc;
    private String op_dt;

    public String getDealer_name() {
        return dealer_name;
    }

    public void setDealer_name(String dealer_name) {
        this.dealer_name = dealer_name;
    }

    public String getApprove_by() {
        return approve_by;
    }

    public void setApprove_by(String approve_by) {
        this.approve_by = approve_by;
    }

    public List<printFilePdfDobj> getFilesList() {
        return filesList;
    }

    public void setFilesList(List<printFilePdfDobj> filesList) {
        this.filesList = filesList;
    }

    public int getSrNo() {
        return srNo;
    }

    public void setSrNo(int srNo) {
        this.srNo = srNo;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_desc() {
        return status_desc;
    }

    public void setStatus_desc(String status_desc) {
        this.status_desc = status_desc;
    }

    public String getOp_dt() {
        return op_dt;
    }

    public void setOp_dt(String op_dt) {
        this.op_dt = op_dt;
    }

    public String getApprove_date() {
        return approve_date;
    }

    public void setApprove_date(String approve_date) {
        this.approve_date = approve_date;
    }

    public int getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    public String getRto_name() {
        return rto_name;
    }

    public void setRto_name(String rto_name) {
        this.rto_name = rto_name;
    }
}
