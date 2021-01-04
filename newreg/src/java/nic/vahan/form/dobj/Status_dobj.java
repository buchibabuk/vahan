/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import nic.vahan.CommonUtils.VehicleParameters;

public class Status_dobj implements Serializable {

    private String appl_no;
    private String appl_dt;
    Timestamp appl_date;
    private String regn_no;
    private int pur_cd;
    private int file_movement_slno;
    private String seat_cd;
    private String seat_role;
    private String status;
    private String public_remark;
    private String office_remark;
    private long emp_cd;
    private String state_cd;
    private int off_cd;
    private String changed_data;
    private String rto_code;
    private String file_movement_type;
    private String cntr_id;
    private int flow_slno;
    private int action_cd;
    //For Administrator who wants to skip fee 
    private int prev_action_cd;
    private int prev_action_cd_selected;
    private int current_role;
    private String user_id;
    private String user_type;
    private String entry_ip;
    private String entry_status;
    private String confirm_ip;
    private String confirm_status;
    private Date confirm_date;
    private String purCdDescr;
    private VehicleParameters vehicleParameters;
    private Date moved_on;
    private Date op_dt;
    private String stateName;
    private String offName;
    private List<EpayDobj> listFeeTaxDifference = null;

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
     * @return the appl_dt
     */
    public String getAppl_dt() {
        return appl_dt;
    }

    /**
     * @param appl_dt the appl_dt to set
     */
    public void setAppl_dt(String appl_dt) {
        this.appl_dt = appl_dt;
    }

    /**
     * @return the regn_no
     */
    public String getRegn_no() {
        return regn_no;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    /**
     * @return the pur_cd
     */
    public int getPur_cd() {
        return pur_cd;
    }

    /**
     * @param pur_cd the pur_cd to set
     */
    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    /**
     * @return the file_movement_slno
     */
    public int getFile_movement_slno() {
        return file_movement_slno;
    }

    /**
     * @param file_movement_slno the file_movement_slno to set
     */
    public void setFile_movement_slno(int file_movement_slno) {
        this.file_movement_slno = file_movement_slno;
    }

    /**
     * @return the seat_cd
     */
    public String getSeat_cd() {
        return seat_cd;
    }

    /**
     * @param seat_cd the seat_cd to set
     */
    public void setSeat_cd(String seat_cd) {
        this.seat_cd = seat_cd;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the public_remark
     */
    public String getPublic_remark() {
        return public_remark;
    }

    /**
     * @param public_remark the public_remark to set
     */
    public void setPublic_remark(String public_remark) {
        this.public_remark = public_remark;
    }

    /**
     * @return the office_remark
     */
    public String getOffice_remark() {
        return office_remark;
    }

    /**
     * @param office_remark the office_remark to set
     */
    public void setOffice_remark(String office_remark) {
        this.office_remark = office_remark;
    }

    /**
     * @return the emp_cd
     */
    public long getEmp_cd() {
        return emp_cd;
    }

    /**
     * @param emp_cd the emp_cd to set
     */
    public void setEmp_cd(long emp_cd) {
        this.emp_cd = emp_cd;
    }

    /**
     * @return the state_cd
     */
    public String getState_cd() {
        return state_cd;
    }

    /**
     * @param state_cd the state_cd to set
     */
    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    /**
     * @return the off_cd
     */
    public int getOff_cd() {
        return off_cd;
    }

    /**
     * @param off_cd the off_cd to set
     */
    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    /**
     * @return the seat_role
     */
    public String getSeat_role() {
        return seat_role;
    }

    /**
     * @param seat_role the seat_role to set
     */
    public void setSeat_role(String seat_role) {
        this.seat_role = seat_role;
    }

    /**
     * @return the changed_data
     */
    public String getChanged_data() {
        return changed_data;
    }

    /**
     * @param changed_data the changed_data to set
     */
    public void setChanged_data(String changed_data) {
        this.changed_data = changed_data;
    }

    /**
     * @return the rto_code
     */
    public String getRto_code() {
        return rto_code;
    }

    /**
     * @param rto_code the rto_code to set
     */
    public void setRto_code(String rto_code) {
        this.rto_code = rto_code;
    }

    /**
     * @return the file_movement_type
     */
    public String getFile_movement_type() {
        return file_movement_type;
    }

    /**
     * @param file_movement_type the file_movement_type to set
     */
    public void setFile_movement_type(String file_movement_type) {
        this.file_movement_type = file_movement_type;
    }

    /**
     * @return the cntr_id
     */
    public String getCntr_id() {
        return cntr_id;
    }

    /**
     * @param cntr_id the cntr_id to set
     */
    public void setCntr_id(String cntr_id) {
        this.cntr_id = cntr_id;
    }

    /**
     * @return the flow_slno
     */
    public int getFlow_slno() {
        return flow_slno;
    }

    /**
     * @param flow_slno the flow_slno to set
     */
    public void setFlow_slno(int flow_slno) {
        this.flow_slno = flow_slno;
    }

    /**
     * @return the action_cd
     */
    public int getAction_cd() {
        return action_cd;
    }

    /**
     * @param action_cd the action_cd to set
     */
    public void setAction_cd(int action_cd) {
        this.action_cd = action_cd;
    }

    public String toString() {
        String str = "action_cd" + action_cd + "  :  flow_slno" + flow_slno + "  :  " + "emp_cd" + emp_cd + "  :  " + appl_no + " : " + appl_dt + " : " + regn_no + " : " + pur_cd + " : " + file_movement_slno + " : " + seat_cd + " : " + seat_role + " : " + status + " : " + public_remark;
        return str;

    }

    /**
     * @return the prev_action_cd_selected
     */
    public int getPrev_action_cd_selected() {
        return prev_action_cd_selected;
    }

    /**
     * @param prev_action_cd_selected the prev_action_cd_selected to set
     */
    public void setPrev_action_cd_selected(int prev_action_cd_selected) {
        this.prev_action_cd_selected = prev_action_cd_selected;
    }

    /**
     * @return the current_role
     */
    public int getCurrent_role() {
        return current_role;
    }

    /**
     * @param current_role the current_role to set
     */
    public void setCurrent_role(int current_role) {
        this.current_role = current_role;
    }

    /**
     * @return the user_id
     */
    public String getUser_id() {
        return user_id;
    }

    /**
     * @param user_id the user_id to set
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /**
     * @return the user_type
     */
    public String getUser_type() {
        return user_type;
    }

    /**
     * @param user_type the user_type to set
     */
    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    /**
     * @return the entry_ip
     */
    public String getEntry_ip() {
        return entry_ip;
    }

    /**
     * @param entry_ip the entry_ip to set
     */
    public void setEntry_ip(String entry_ip) {
        this.entry_ip = entry_ip;
    }

    /**
     * @return the entry_status
     */
    public String getEntry_status() {
        return entry_status;
    }

    /**
     * @param entry_status the entry_status to set
     */
    public void setEntry_status(String entry_status) {
        this.entry_status = entry_status;
    }

    /**
     * @return the confirm_ip
     */
    public String getConfirm_ip() {
        return confirm_ip;
    }

    /**
     * @param confirm_ip the confirm_ip to set
     */
    public void setConfirm_ip(String confirm_ip) {
        this.confirm_ip = confirm_ip;
    }

    /**
     * @return the confirm_status
     */
    public String getConfirm_status() {
        return confirm_status;
    }

    /**
     * @param confirm_status the confirm_status to set
     */
    public void setConfirm_status(String confirm_status) {
        this.confirm_status = confirm_status;
    }

    /**
     * @return the appl_date
     */
    public Timestamp getAppl_date() {
        return appl_date;
    }

    /**
     * @param appl_date the appl_date to set
     */
    public void setAppl_date(Timestamp appl_date) {
        this.appl_date = appl_date;
    }

    /**
     * @return the confirm_date
     */
    public Date getConfirm_date() {
        return confirm_date;
    }

    /**
     * @param confirm_date the confirm_date to set
     */
    public void setConfirm_date(Date confirm_date) {
        this.confirm_date = confirm_date;
    }

    /**
     * @return the vehicleParameters
     */
    public VehicleParameters getVehicleParameters() {
        return vehicleParameters;
    }

    /**
     * @param vehicleParameters the vehicleParameters to set
     */
    public void setVehicleParameters(VehicleParameters vehicleParameters) {
        this.vehicleParameters = vehicleParameters;
    }

    /**
     * @return the prev_action_cd
     */
    public int getPrev_action_cd() {
        return prev_action_cd;
    }

    /**
     * @param prev_action_cd the prev_action_cd to set
     */
    public void setPrev_action_cd(int prev_action_cd) {
        this.prev_action_cd = prev_action_cd;
    }

    /**
     * @return the purCdDescr
     */
    public String getPurCdDescr() {
        return purCdDescr;
    }

    /**
     * @param purCdDescr the purCdDescr to set
     */
    public void setPurCdDescr(String purCdDescr) {
        this.purCdDescr = purCdDescr;
    }

    /**
     * @return the moved_on
     */
    public Date getMoved_on() {
        return moved_on;
    }

    /**
     * @param moved_on the moved_on to set
     */
    public void setMoved_on(Date moved_on) {
        this.moved_on = moved_on;
    }

    /**
     * @return the op_dt
     */
    public Date getOp_dt() {
        return op_dt;
    }

    /**
     * @param op_dt the op_dt to set
     */
    public void setOp_dt(Date op_dt) {
        this.op_dt = op_dt;
    }

    /**
     * @return the stateName
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * @param stateName the stateName to set
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    /**
     * @return the offName
     */
    public String getOffName() {
        return offName;
    }

    /**
     * @param offName the offName to set
     */
    public void setOffName(String offName) {
        this.offName = offName;
    }

    /**
     * @return the listFeeTaxDifference
     */
    public List<EpayDobj> getListFeeTaxDifference() {
        return listFeeTaxDifference;
    }

    /**
     * @param listFeeTaxDifference the listFeeTaxDifference to set
     */
    public void setListFeeTaxDifference(List<EpayDobj> listFeeTaxDifference) {
        this.listFeeTaxDifference = listFeeTaxDifference;
    }
}
