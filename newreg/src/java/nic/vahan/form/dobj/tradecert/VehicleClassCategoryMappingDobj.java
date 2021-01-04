/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.tradecert;

/**
 *
 * @author nicsi
 */
public class VehicleClassCategoryMappingDobj {

    private int srNo;
    private String vehicleClass;
    private String vehicleCategory;
    private String vchCatgCd;

    public VehicleClassCategoryMappingDobj(int srNo, String vehicleClass, String vehicleCategory, String vchCatgCd) {
        this.vehicleClass = vehicleClass;
        this.vehicleCategory = vehicleCategory;
        this.vchCatgCd = vchCatgCd;
    }

    public int getSrNo() {
        return srNo;
    }

    public void setSrNo(int srNo) {
        this.srNo = srNo;
    }

    public String getVehicleClass() {
        return vehicleClass;
    }

    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    public String getVehicleCategory() {
        return vehicleCategory;
    }

    public void setVehicleCategory(String vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }

    public String getVchCatgCd() {
        return vchCatgCd;
    }

    public void setVchCatgCd(String vchCatgCd) {
        this.vchCatgCd = vchCatgCd;
    }
}
