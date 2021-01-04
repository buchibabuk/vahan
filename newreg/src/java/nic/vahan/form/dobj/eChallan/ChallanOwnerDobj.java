/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;

/**
 *
 * @author tranC094
 */
public class ChallanOwnerDobj implements Serializable {

    private String bookNo;
    private String challanNo;
    private String vehicleNo;
    private String vhClassCd;
    private String rtoFrom;
    private String stateFrom;
    private String ownerName;
    private String chasiNo;

    public void reset() {
        setBookNo("");
        setVehicleNo("");
        setVhClassCd("");
        setChallanNo("");
        setChallanNo("");
        setStateFrom("");
        setOwnerName("");
        setChasiNo("");
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public String getChallanNo() {
        return challanNo;
    }

    public void setChallanNo(String challanNo) {
        this.challanNo = challanNo;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getVhClassCd() {
        return vhClassCd;
    }

    public void setVhClassCd(String vhClassCd) {
        this.vhClassCd = vhClassCd;
    }

    public String getRtoFrom() {
        return rtoFrom;
    }

    public void setRtoFrom(String rtoFrom) {
        this.rtoFrom = rtoFrom;
    }

    public String getStateFrom() {
        return stateFrom;
    }

    public void setStateFrom(String stateFrom) {
        this.stateFrom = stateFrom;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getChasiNo() {
        return chasiNo;
    }

    public void setChasiNo(String chasiNo) {
        this.chasiNo = chasiNo;
    }
}
