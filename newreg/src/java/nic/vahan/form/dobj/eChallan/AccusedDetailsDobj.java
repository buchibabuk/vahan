/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;

/**
 *
 * @author nicsi
 */
public class AccusedDetailsDobj implements Serializable, Cloneable {

    private String accCatergory;
    private String accName;
    private String accAddress;
    private String accDLBladgeNo;
    private String accDesc;
    private String accPoliceStation;
    private String accCity;
    private String accFlag;
    private int accPincode;

    public AccusedDetailsDobj(String accCatergory, String accDesc, String accName, String accAddress, String accDLBladgeNo, String accPoliceStation, String accCity, String accFlag, int accPincode) {
        this.accCatergory = accCatergory;
        this.accName = accName;
        this.accAddress = accAddress;
        this.accDLBladgeNo = accDLBladgeNo;
        this.accDesc = accDesc;
        this.accPoliceStation = accPoliceStation;
        this.accCity = accCity;
        this.accFlag = accFlag;
        this.accPincode = accPincode;

    }

    public AccusedDetailsDobj() {
    }

    /**
     * @return the accCatergory
     */
    public String getAccCatergory() {
        return accCatergory;
    }

    /**
     * @param accCatergory the accCatergory to set
     */
    public void setAccCatergory(String accCatergory) {
        this.accCatergory = accCatergory;
    }

    /**
     * @return the accName
     */
    public String getAccName() {
        return accName;
    }

    /**
     * @param accName the accName to set
     */
    public void setAccName(String accName) {
        this.accName = accName;
    }

    /**
     * @return the accAddress
     */
    public String getAccAddress() {
        return accAddress;
    }

    /**
     * @param accAddress the accAddress to set
     */
    public void setAccAddress(String accAddress) {
        this.accAddress = accAddress;
    }

    /**
     * @return the accDLBladgeNo
     */
    public String getAccDLBladgeNo() {
        return accDLBladgeNo;
    }

    /**
     * @param accDLBladgeNo the accDLBladgeNo to set
     */
    public void setAccDLBladgeNo(String accDLBladgeNo) {
        this.accDLBladgeNo = accDLBladgeNo;
    }

    public String getAccDesc() {
        return accDesc;
    }

    public void setAccDesc(String accDesc) {
        this.accDesc = accDesc;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the accPoliceStation
     */
    public String getAccPoliceStation() {
        return accPoliceStation;
    }

    /**
     * @param accPoliceStation the accPoliceStation to set
     */
    public void setAccPoliceStation(String accPoliceStation) {
        this.accPoliceStation = accPoliceStation;
    }

    /**
     * @return the accCity
     */
    public String getAccCity() {
        return accCity;
    }

    /**
     * @param accCity the accCity to set
     */
    public void setAccCity(String accCity) {
        this.accCity = accCity;
    }

    /**
     * @return the accFlag
     */
    public String getAccFlag() {
        return accFlag;
    }

    /**
     * @param accFlag the accFlag to set
     */
    public void setAccFlag(String accFlag) {
        this.accFlag = accFlag;
    }

    /**
     * @return the accPincode
     */
    public int getAccPincode() {
        return accPincode;
    }

    /**
     * @param accPincode the accPincode to set
     */
    public void setAccPincode(int accPincode) {
        this.accPincode = accPincode;
    }
}
