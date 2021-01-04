/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;
import java.util.Date;
import oracle.sql.DATE;

/**
 *
 * @author tranC111
 */
public class VehicleImpoundDobj implements Serializable {

    private String impndPlace;
    private Date impndDate;
    private String officerToContact;
    private String sezNO;
    private String accused;
    private String impdPoliceStationName;
    private String impdDistrictName;

    public String getImpndPlace() {
        return impndPlace;
    }

    public void setImpndPlace(String impndPlace) {
        this.impndPlace = impndPlace;
    }

    public Date getImpndDate() {
        return impndDate;
    }

    public void setImpndDate(Date impndDate) {
        this.impndDate = impndDate;
    }

    public String getOfficerToContact() {
        return officerToContact;
    }

    public void setOfficerToContact(String officerToContact) {
        this.officerToContact = officerToContact;
    }

    public String getSezNO() {
        return sezNO;
    }

    public void setSezNO(String sezNO) {
        this.sezNO = sezNO;
    }

    public String getAccused() {
        return accused;
    }

    public void setAccused(String accused) {
        this.accused = accused;
    }

    /**
     * @return the impdPoliceStationName
     */
    public String getImpdPoliceStationName() {
        return impdPoliceStationName;
    }

    /**
     * @param impdPoliceStationName the impdPoliceStationName to set
     */
    public void setImpdPoliceStationName(String impdPoliceStationName) {
        this.impdPoliceStationName = impdPoliceStationName;
    }

    /**
     * @return the impdDistrictName
     */
    public String getImpdDistrictName() {
        return impdDistrictName;
    }

    /**
     * @param impdDistrictName the impdDistrictName to set
     */
    public void setImpdDistrictName(String impdDistrictName) {
        this.impdDistrictName = impdDistrictName;
    }
}
