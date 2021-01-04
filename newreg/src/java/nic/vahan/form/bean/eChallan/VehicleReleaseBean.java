/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.eChallan;

import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.eChallan.VehicleReleaseDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "vehicleReleaseBean")
@ViewScoped
public class VehicleReleaseBean extends AbstractApplBean implements Serializable {

    private int srNo;
    private String appl_no;
    private String chal_no;
    private String vehicle_no;
    private String cmd_rcpt_no;
    private Date Chal_date;
    private Date release_Date;
    private String release_by;
    private Date cmd_rcpt_dt;
    private int cmd_fee;
    private String rto_name;
    private String district;
    private String policeStation;
    private String rcpt_header;
    private String rcpt_sub_header;
    private boolean renderVehicleReleasePanel;
    VehicleReleaseDobj dobj = new VehicleReleaseDobj();
    PrintDocImpl impl = new PrintDocImpl();

    public VehicleReleaseBean() throws VahanException {
        if (appl_details != null) {
            appl_no = appl_details.getAppl_no();
        }
        getReleaseVehicleDetails(appl_no);
    }

    public void getReleaseVehicleDetails(String appl_no) throws VahanException {
        dobj = impl.getVehicleReleaseDetails(appl_no);
        if (dobj != null) {
            setSrNo(dobj.getSrNo());
            setCmd_fee(dobj.getCmd_fee());
            setCmd_rcpt_no(dobj.getCmd_rcpt_no());
            setCmd_rcpt_dt(dobj.getCmd_rcpt_dt());
            setRelease_Date(dobj.getRelease_Date());
            setRelease_by(dobj.getRelease_by());
            setVehicle_no(dobj.getVehicle_no());
            setDistrict(dobj.getDistrict());
            setRto_name(dobj.getRto_name());
            setPoliceStation(dobj.getPoliceStation());
            setRcpt_header(ServerUtil.getRcptHeading());
            setRcpt_sub_header(ServerUtil.getRcptSubHeading());
            setRenderVehicleReleasePanel(true);
        }


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
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
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
     * @return the rcpt_header
     */
    public String getRcpt_header() {
        return rcpt_header;
    }

    /**
     * @param rcpt_header the rcpt_header to set
     */
    public void setRcpt_header(String rcpt_header) {
        this.rcpt_header = rcpt_header;
    }

    /**
     * @return the rcpt_sub_header
     */
    public String getRcpt_sub_header() {
        return rcpt_sub_header;
    }

    /**
     * @param rcpt_sub_header the rcpt_sub_header to set
     */
    public void setRcpt_sub_header(String rcpt_sub_header) {
        this.rcpt_sub_header = rcpt_sub_header;
    }

    /**
     * @return the renderVehicleReleasePanel
     */
    public boolean isRenderVehicleReleasePanel() {
        return renderVehicleReleasePanel;
    }

    /**
     * @param renderVehicleReleasePanel the renderVehicleReleasePanel to set
     */
    public void setRenderVehicleReleasePanel(boolean renderVehicleReleasePanel) {
        this.renderVehicleReleasePanel = renderVehicleReleasePanel;
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
}
