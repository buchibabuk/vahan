/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.util.Date;

/**
 *
 * @author nicsi
 */
public class RCCancelCertificateDobj {

    private String office;
    private String state;
    private Date currentDate;
    private String vehicleNo;
    private String ChassisNo;
    private String engineNO;
    private String ownerName;
    private String ownerAddress;
    private String fileRefrenceNo;

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getChassisNo() {
        return ChassisNo;
    }

    public void setChassisNo(String ChassisNo) {
        this.ChassisNo = ChassisNo;
    }

    public String getEngineNO() {
        return engineNO;
    }

    public void setEngineNO(String engineNO) {
        this.engineNO = engineNO;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public String getFileRefrenceNo() {
        return fileRefrenceNo;
    }

    public void setFileRefrenceNo(String fileRefrenceNo) {
        this.fileRefrenceNo = fileRefrenceNo;
    }
}
