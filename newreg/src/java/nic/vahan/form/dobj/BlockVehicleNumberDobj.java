/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class BlockVehicleNumberDobj implements Serializable {

    private Integer fromNumber;
    private Integer toNumber;
    private String vehSeries;
    private String blockedRegnNo;
    private String releaseRegnNo;

    public Integer getFromNumber() {
        return fromNumber;
    }

    public void setFromNumber(Integer fromNumber) {
        this.fromNumber = fromNumber;
    }

    public Integer getToNumber() {
        return toNumber;
    }

    public void setToNumber(Integer toNumber) {
        this.toNumber = toNumber;
    }

    public String getVehSeries() {
        return vehSeries;
    }

    public void setVehSeries(String vehSeries) {
        this.vehSeries = vehSeries;
    }

    public String getBlockedRegnNo() {
        return blockedRegnNo;
    }

    public void setBlockedRegnNo(String blockedRegnNo) {
        this.blockedRegnNo = blockedRegnNo;
    }

    public String getReleaseRegnNo() {
        return releaseRegnNo;
    }

    public void setReleaseRegnNo(String releaseRegnNo) {
        this.releaseRegnNo = releaseRegnNo;
    }
}
