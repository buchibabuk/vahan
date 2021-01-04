/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.RCCancelCertificateDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
@ManagedBean(name = "rCCancelCertificateBean")
@ViewScoped
public class RCCancelCertificateBean extends AbstractApplBean {

    private static Logger LOGGER = Logger.getLogger(RCCancelCertificateBean.class);
    private String office;
    private String state;
    private String currentDate;
    private String vehicleNo;
    private String ChassisNo;
    private String engineNO;
    private String ownerName;
    private String ownerAddress;
    private String recieptHeader;
    private String fileRefrenceNo;
    RCCancelCertificateDobj dobj = new RCCancelCertificateDobj();
    PrintDocImpl impl = new PrintDocImpl();

    public RCCancelCertificateBean() {
        if (appl_details != null) {
            vehicleNo = appl_details.getRegn_no();
        }
        getDetails(vehicleNo);
    }

    public void getDetails(String regnNo) {
        try {
            dobj = impl.getVehicleDetails(regnNo);
            setChassisNo(dobj.getChassisNo());
            setEngineNO(dobj.getEngineNO());
            setOwnerName(dobj.getOwnerName());
            setOwnerAddress(dobj.getOwnerAddress());
            setVehicleNo(vehicleNo);
            setOffice(dobj.getOffice());
            setState(dobj.getState());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
            String aa = sdf.format(new Date());

            setCurrentDate(new SimpleDateFormat("dd-MMM-yy").format(new Date()));
            setRecieptHeader(ServerUtil.getRcptHeading());
            setFileRefrenceNo(dobj.getFileRefrenceNo());



        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getChassisNo() {
        return ChassisNo;
    }

    public void setChassisNo(String ChassisNo) {
        this.ChassisNo = ChassisNo;
    }

    public String getEngineNO() {
        return engineNO;
    }

    public void setEngineNO(String engineNO) {
        this.engineNO = engineNO;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public String getRecieptHeader() {
        return recieptHeader;
    }

    public void setRecieptHeader(String recieptHeader) {
        this.recieptHeader = recieptHeader;
    }

    public String getFileRefrenceNo() {
        return fileRefrenceNo;
    }

    public void setFileRefrenceNo(String fileRefrenceNo) {
        this.fileRefrenceNo = fileRefrenceNo;
    }
}
