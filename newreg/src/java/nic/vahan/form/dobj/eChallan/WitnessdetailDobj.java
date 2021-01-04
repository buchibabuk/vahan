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
public class WitnessdetailDobj implements Serializable {

    private String applNo;
    private String witnessName;
    private long witnessContactNo;
    private String witnessAddress;
    private String stateCd;
    private int offCd;
    private String policeStation;

    public WitnessdetailDobj(String witnessName, long witnessContact, String witnessAddress, String policeStation) {
        this.witnessName = witnessName;
        this.witnessContactNo = witnessContact;
        this.witnessAddress = witnessAddress;
        this.policeStation = policeStation;

    }

    public WitnessdetailDobj() {
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public String getWitnessName() {
        return witnessName;
    }

    public void setWitnessName(String witnessName) {
        this.witnessName = witnessName;
    }

    public long getWitnessContactNo() {
        return witnessContactNo;
    }

    public void setWitnessContactNo(long witnessContactNo) {
        this.witnessContactNo = witnessContactNo;
    }

    public String getWitnessAddress() {
        return witnessAddress;
    }

    public void setWitnessAddress(String witnessAddress) {
        this.witnessAddress = witnessAddress;
    }

    public String getStateCd() {
        return stateCd;
    }

    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    public int getOffCd() {
        return offCd;
    }

    public void setOffCd(int offCd) {
        this.offCd = offCd;
    }

    /**
     * @return the policeStation
     */
    public String getPoliceStation() {
        return policeStation;
    }

    /**
     * @param policeStation the policeStation to set
     */
    public void setPoliceStation(String policeStation) {
        this.policeStation = policeStation;
    }
}
