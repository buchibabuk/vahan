/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class VehicleReleaseDobj implements Serializable {

    private int srNo;
    private String chal_no;
    private String vehicle_no;
    private String cmd_rcpt_no;
    private Date Chal_date;
    private Date release_Date;
    private String release_by;
    private Date cmd_rcpt_dt;
    private int cmd_fee;
    private String rto_name;
    private String policeStation;
    private String district;

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
     * @return the chal_no
     */
    public String getChal_no() {
        return chal_no;
    }

    /**
     * @param chal_no the chal_no to set
     */
    public void setChal_no(String chal_no) {
        this.chal_no = chal_no;
    }

    /**
     * @return the vehicle_no
     */
    public String getVehicle_no() {
        return vehicle_no;
    }

    /**
     * @param vehicle_no the vehicle_no to set
     */
    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }

    /**
     * @return the Chal_date
     */
    public Date getChal_date() {
        return Chal_date;
    }

    /**
     * @param Chal_date the Chal_date to set
     */
    public void setChal_date(Date Chal_date) {
        this.Chal_date = Chal_date;
    }

    /**
     * @return the release_Date
     */
    public Date getRelease_Date() {
        return release_Date;
    }

    /**
     * @param release_Date the release_Date to set
     */
    public void setRelease_Date(Date release_Date) {
        this.release_Date = release_Date;
    }

    /**
     * @return the release_by
     */
    public String getRelease_by() {
        return release_by;
    }

    /**
     * @param release_by the release_by to set
     */
    public void setRelease_by(String release_by) {
        this.release_by = release_by;
    }

    /**
     * @return the cmd_fee
     */
    public int getCmd_fee() {
        return cmd_fee;
    }

    /**
     * @param cmd_fee the cmd_fee to set
     */
    public void setCmd_fee(int cmd_fee) {
        this.cmd_fee = cmd_fee;
    }

    /**
     * @return the district
     */
    public String getDistrict() {
        return district;
    }

    /**
     * @param district the district to set
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * @return the rto_name
     */
    public String getRto_name() {
        return rto_name;
    }

    /**
     * @param rto_name the rto_name to set
     */
    public void setRto_name(String rto_name) {
        this.rto_name = rto_name;
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

    /**
     * @return the cmd_rcpt_dt
     */
    public Date getCmd_rcpt_dt() {
        return cmd_rcpt_dt;
    }

    /**
     * @param cmd_rcpt_dt the cmd_rcpt_dt to set
     */
    public void setCmd_rcpt_dt(Date cmd_rcpt_dt) {
        this.cmd_rcpt_dt = cmd_rcpt_dt;
    }

    /**
     * @return the cmd_rcpt_no
     */
    public String getCmd_rcpt_no() {
        return cmd_rcpt_no;
    }

    /**
     * @param cmd_rcpt_no the cmd_rcpt_no to set
     */
    public void setCmd_rcpt_no(String cmd_rcpt_no) {
        this.cmd_rcpt_no = cmd_rcpt_no;
    }
    /**
     * @return the tax_rcpt_no
     */
}
