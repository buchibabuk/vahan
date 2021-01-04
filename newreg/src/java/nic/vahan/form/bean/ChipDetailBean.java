package nic.vahan.form.bean;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.form.dobj.ChipDetailsDobj;
import nic.vahan.form.impl.ChipDetailsImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author nicsi
 */
@ManagedBean(name = "chipDetailBean")
@ViewScoped
public class ChipDetailBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ChipDetailBean.class);
    private ChipDetailsDobj chipDetail = new ChipDetailsDobj();
    private String ip4;
    private boolean editable;
    private String state_name;
    private String officeName;

    public ChipDetailBean() {
        showDetail();
    }

    public void showDetail() {
        String data[][] = MasterTableFiller.masterTables.TM_STATE.getData();

        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equalsIgnoreCase(Util.getUserStateCode())) {
                state_name = data[i][1];
            }
        }
        data = MasterTableFiller.masterTables.TM_OFFICE.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][13].equalsIgnoreCase(Util.getUserStateCode()) && data[i][0].equalsIgnoreCase(Integer.toString(Util.getUserOffCode()))) {
                officeName = data[i][1];
            }
        }
        try {
            chipDetail = ChipDetailsImpl.getDataFromDataTable();
            if (chipDetail == null) {
                chipDetail = new ChipDetailsDobj();
                chipDetail.setSaveButton(true);
                JSFUtils.showMessage("Record Not Available Please Enter Record & Save");
            }

            chipDetail.setStateName(state_name);
            chipDetail.setState_cd(Util.getUserStateCode());
            chipDetail.setOfficeName(officeName);
            chipDetail.setOff_cd(Util.getUserOffCode());

        } catch (VahanException e) {
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void save() {
        String msg = null;
        try {
            if (valid(chipDetail)) {
                msg = (chipDetail.isSaveButton()) ? ChipDetailsImpl.insertDataintoDataTable(chipDetail) : ChipDetailsImpl.modifydeleteintoDataTable(chipDetail);
            } else {
                msg = "Please Enter Atleast One IP Address OR Chip Number";
            }
        } catch (VahanException ve) {
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        JSFUtils.showMessage(msg);
    }

    public boolean valid(ChipDetailsDobj dobj) {
        boolean valid = false;
        if (!CommonUtils.isNullOrBlank(dobj.getIa1_chip_no())) {
            valid = true;
        } else if (!CommonUtils.isNullOrBlank(dobj.getIa2_chip_no())) {
            valid = true;
        } else if (!CommonUtils.isNullOrBlank(dobj.getIa3_chip_no())) {
            valid = true;
        } else if (!CommonUtils.isNullOrBlank(dobj.getIa4_chip_no())) {
            valid = true;
        } else if (!CommonUtils.isNullOrBlank(dobj.getIa5_chip_no())) {
            valid = true;
        } else if (!CommonUtils.isNullOrBlank(dobj.getIa6_chip_no())) {
            valid = true;
        } else if (!CommonUtils.isNullOrBlank(dobj.getProd_server_ip4())) {
            valid = true;
        } else if (!CommonUtils.isNullOrBlank(dobj.getStage_server_ip4())) {
            valid = true;
        } else if (!CommonUtils.isNullOrBlank(dobj.getTest_server_ip4())) {
            valid = true;
        }

        return valid;

    }

    public String getIp4() {
        return ip4;
    }

    public void setIp4(String ip4) {
        this.ip4 = ip4;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public ChipDetailsDobj getChipDetail() {
        return chipDetail;
    }

    public void setChipDetail(ChipDetailsDobj chipDetail) {
        this.chipDetail = chipDetail;
    }
}
