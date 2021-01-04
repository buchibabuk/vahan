/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.util.Date;

/**
 *
 * @author Administrator
 */
public class RefertoCourtDobj {

    private String challanNo;
    private String applicationno;
    private String vehicleNo;
    private Date challanDate;
    private Date opdate;
    private Date hearingDate;
    private String ownername;
    private String courtNAme;
    private boolean SelectRow;

    public RefertoCourtDobj() {
    }

    public RefertoCourtDobj(String applicationno, String challanNo, String vehicleNo, Date challanDate, String ownername) {
        this.applicationno = applicationno;
        this.challanNo = challanNo;
        this.vehicleNo = vehicleNo;
        this.challanDate = challanDate;
        this.ownername = ownername;
    }

    public String getChallanNo() {
        return challanNo;
    }

    public void setChallanNo(String challanNo) {
        this.challanNo = challanNo;
    }

    public String getApplicationno() {
        return applicationno;
    }

    public void setApplicationno(String applicationno) {
        this.applicationno = applicationno;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public Date getChallanDate() {
        return challanDate;
    }

    public void setChallanDate(Date challanDate) {
        this.challanDate = challanDate;
    }

    public Date getHearingDate() {
        return hearingDate;
    }

    public void setHearingDate(Date hearingDate) {
        this.hearingDate = hearingDate;
    }

    public String getCourtNAme() {
        return courtNAme;
    }

    public void setCourtNAme(String courtNAme) {
        this.courtNAme = courtNAme;
    }

    public boolean isSelectRow() {
        return SelectRow;
    }

    public void setSelectRow(boolean SelectRow) {
        this.SelectRow = SelectRow;
    }

    public String getOwnername() {
        return ownername;
    }

    public void setOwnername(String ownername) {
        this.ownername = ownername;
    }

    public Date getOpdate() {
        return opdate;
    }

    public void setOpdate(Date opdate) {
        this.opdate = opdate;
    }
}
