/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.tradecert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nicsi
 */
public class MergeDeDuplPrintDobj implements Serializable {

    private String dealerName;
    private String vehCatgName;
    private String validityPeriod;
    private String tcNumber;
    private int noOfVeh;
    private List vhTradeCertDobjSubList;
    private List vtTCMergeDeDuplDobjSubList;
    private List vtTradeCertDobjSubList;

    public MergeDeDuplPrintDobj() {
        this.dealerName = "";
        this.vehCatgName = "";
        this.validityPeriod = "";
        this.tcNumber = "";
        this.noOfVeh = 0;
        vhTradeCertDobjSubList = new ArrayList();
        vtTCMergeDeDuplDobjSubList = new ArrayList();
        vtTradeCertDobjSubList = new ArrayList();
    }

    /**
     * @return the dealerName
     */
    public String getDealerName() {
        return dealerName;
    }

    /**
     * @param dealerName the dealerName to set
     */
    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    /**
     * @return the vehCatgName
     */
    public String getVehCatgName() {
        return vehCatgName;
    }

    /**
     * @param vehCatgName the vehCatgName to set
     */
    public void setVehCatgName(String vehCatgName) {
        this.vehCatgName = vehCatgName;
    }

    /**
     * @return the validityPeriod
     */
    public String getValidityPeriod() {
        return validityPeriod;
    }

    /**
     * @param validityPeriod the validityPeriod to set
     */
    public void setValidityPeriod(String validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    /**
     * @return the tcNumber
     */
    public String getTcNumber() {
        return tcNumber;
    }

    /**
     * @param tcNumber the tcNumber to set
     */
    public void setTcNumber(String tcNumber) {
        this.tcNumber = tcNumber;
    }

    /**
     * @return the noOfVeh
     */
    public int getNoOfVeh() {
        return noOfVeh;
    }

    /**
     * @param noOfVeh the noOfVeh to set
     */
    public void setNoOfVeh(int noOfVeh) {
        this.noOfVeh = noOfVeh;
    }

    /**
     * @return the vhTradeCertDobjSubList
     */
    public List getVhTradeCertDobjSubList() {
        return vhTradeCertDobjSubList;
    }

    /**
     * @param vhTradeCertDobjSubList the vhTradeCertDobjSubList to set
     */
    public void setVhTradeCertDobjSubList(List vhTradeCertDobjSubList) {
        this.vhTradeCertDobjSubList = vhTradeCertDobjSubList;
    }

    /**
     * @return the vtTCMergeDeDuplDobjSubList
     */
    public List getVtTCMergeDeDuplDobjSubList() {
        return vtTCMergeDeDuplDobjSubList;
    }

    /**
     * @param vtTCMergeDeDuplDobjSubList the vtTCMergeDeDuplDobjSubList to set
     */
    public void setVtTCMergeDeDuplDobjSubList(List vtTCMergeDeDuplDobjSubList) {
        this.vtTCMergeDeDuplDobjSubList = vtTCMergeDeDuplDobjSubList;
    }

    /**
     * @return the vtTradeCertDobjSubList
     */
    public List getVtTradeCertDobjSubList() {
        return vtTradeCertDobjSubList;
    }

    /**
     * @param vtTradeCertDobjSubList the vtTradeCertDobjSubList to set
     */
    public void setVtTradeCertDobjSubList(List vtTradeCertDobjSubList) {
        this.vtTradeCertDobjSubList = vtTradeCertDobjSubList;
    }
}
