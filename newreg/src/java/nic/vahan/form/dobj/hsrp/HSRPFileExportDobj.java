/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.hsrp;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author tranC075
 */
public class HSRPFileExportDobj implements Serializable {

    private String regnNo = "";
    private String chassisNo = "";
    private String engNo = "";
    private String hrspFlag = "";
    private String vehicleClass = "";
    private String vehicleCatg = "";
    private String maker = "";
    private String model = "";
    private String ownerName = "";
    private String dealer = "";
    private String reciptNo = "";
    private String star = "";
    private String seperator = "";
    private int srNo = 0;
    private String cur_date;
    private List<HSRPFileExportDobj> listFileExport;
    private String HsrpFileName = "";
    private String purCd = "";
    private String fuelDescr = "";
    private String offCd;
    private String stateCd;
    private String regnDt;

    public List<HSRPFileExportDobj> getListFileExport() {
        return listFileExport;
    }

    public void setListFileExport(List<HSRPFileExportDobj> listFileExport) {
        this.listFileExport = listFileExport;
    }

    /**
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the chassisNo
     */
    public String getChassisNo() {
        return chassisNo;
    }

    /**
     * @param chassisNo the chassisNo to set
     */
    public void setChassisNo(String chassisNo) {
        this.chassisNo = chassisNo;
    }

    /**
     * @return the engNo
     */
    public String getEngNo() {
        return engNo;
    }

    /**
     * @param engNo the engNo to set
     */
    public void setEngNo(String engNo) {
        this.engNo = engNo;
    }

    /**
     * @return the hrspFlag
     */
    public String getHrspFlag() {
        return hrspFlag;
    }

    /**
     * @param hrspFlag the hrspFlag to set
     */
    public void setHrspFlag(String hrspFlag) {
        this.hrspFlag = hrspFlag;
    }

    /**
     * @return the vehicleClass
     */
    public String getVehicleClass() {
        return vehicleClass;
    }

    /**
     * @param vehicleClass the vehicleClass to set
     */
    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    /**
     * @return the maker
     */
    public String getMaker() {
        return maker;
    }

    /**
     * @param maker the maker to set
     */
    public void setMaker(String maker) {
        this.maker = maker;
    }

    /**
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * @return the ownerName
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * @param ownerName the ownerName to set
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * @return the dealer
     */
    public String getDealer() {
        return dealer;
    }

    /**
     * @param dealer the dealer to set
     */
    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    /**
     * @return the reciptNo
     */
    public String getReciptNo() {
        return reciptNo;
    }

    /**
     * @param reciptNo the reciptNo to set
     */
    public void setReciptNo(String reciptNo) {
        this.reciptNo = reciptNo;
    }

    /**
     * @return the star
     */
    public String getStar() {
        return star;
    }

    /**
     * @param star the star to set
     */
    public void setStar(String star) {
        this.star = star;
    }

    /**
     * @return the seperator
     */
    public String getSeperator() {
        return seperator;
    }

    /**
     * @param seperator the seperator to set
     */
    public void setSeperator(String seperator) {
        this.seperator = seperator;
    }

    /**
     * @return the srNo
     */
    public int getSrNo() {
        return srNo;
    }

    /**
     * @param srNo the srNo to set
     */
    public void setSrNo(int srNo) {
        this.srNo = srNo;
    }

    /**
     * @return the cur_date
     */
    public String getCur_date() {
        return cur_date;
    }

    /**
     * @param cur_date the cur_date to set
     */
    public void setCur_date(String cur_date) {
        this.cur_date = cur_date;
    }

    /**
     * @return the HsrpFileName
     */
    public String getHsrpFileName() {
        return HsrpFileName;
    }

    /**
     * @param HsrpFileName the HsrpFileName to set
     */
    public void setHsrpFileName(String HsrpFileName) {
        this.HsrpFileName = HsrpFileName;
    }

    /**
     * @return the vehicleCatg
     */
    public String getVehicleCatg() {
        return vehicleCatg;
    }

    /**
     * @param vehicleCatg the vehicleCatg to set
     */
    public void setVehicleCatg(String vehicleCatg) {
        this.vehicleCatg = vehicleCatg;
    }

    /**
     * @return the purCd
     */
    public String getPurCd() {
        return purCd;
    }

    /**
     * @param purCd the purCd to set
     */
    public void setPurCd(String purCd) {
        this.purCd = purCd;
    }

    /**
     * @return the fuelDescr
     */
    public String getFuelDescr() {
        return fuelDescr;
    }

    /**
     * @param fuelDescr the fuelDescr to set
     */
    public void setFuelDescr(String fuelDescr) {
        this.fuelDescr = fuelDescr;
    }

    
    /**
     * @return the stateCd
     */
    public String getStateCd() {
        return stateCd;
    }

    /**
     * @param stateCd the stateCd to set
     */
    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    /**
     * @return the offCd
     */
    public String getOffCd() {
        return offCd;
    }

    /**
     * @param offCd the offCd to set
     */
    public void setOffCd(String offCd) {
        this.offCd = offCd;
    }

    /**
     * @return the regnDt
     */
    public String getRegnDt() {
        return regnDt;
    }

    /**
     * @param regnDt the regnDt to set
     */
    public void setRegnDt(String regnDt) {
        this.regnDt = regnDt;
    }


}
