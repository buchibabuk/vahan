/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tranC103
 */
public class VehicleClass {

    private List transportVehilceClass = new ArrayList();
    private List nonTransportVehicleClass = new ArrayList();
    private List vehicleCategory = new ArrayList();

    /**
     * @return the transportVehilceClass
     */
    public List getTransportVehilceClass() {
        return transportVehilceClass;
    }

    /**
     * @param transportVehilceClass the transportVehilceClass to set
     */
    public void setTransportVehilceClass(List transportVehilceClass) {
        this.transportVehilceClass = transportVehilceClass;
    }

    /**
     * @return the nonTransportVehicleClass
     */
    public List getNonTransportVehicleClass() {
        return nonTransportVehicleClass;
    }

    /**
     * @param nonTransportVehicleClass the nonTransportVehicleClass to set
     */
    public void setNonTransportVehicleClass(List nonTransportVehicleClass) {
        this.nonTransportVehicleClass = nonTransportVehicleClass;
    }

    /**
     * @return the vehicleCategory
     */
    public List getVehicleCategory() {
        return vehicleCategory;
    }

    /**
     * @param vehicleCategory the vehicleCategory to set
     */
    public void setVehicleCategory(List vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }
}
