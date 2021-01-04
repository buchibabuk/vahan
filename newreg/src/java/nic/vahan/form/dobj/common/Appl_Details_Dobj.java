/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.common;

import java.io.Serializable;
import java.util.Date;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;

/**
 *
 * @author nic5912
 */
public class Appl_Details_Dobj implements Serializable {

    private String appl_no;
    private int pur_cd;
    private String pur_desc;
    private Date appl_date;
    private String regn_no;
    private String own_name;
    private String chasi_no;
    private String appl_dt;
    private int current_file_movement_slno;
    private String current_seat_cd;
    private String current_seat_role;
    private String current_public_remark;
    private String current_office_remark;
    private int current_emp_cd;
    private String currentEmpCd;
    private String current_state_cd;
    private String current_changed_data;
    private String current_cntr_id;
    private int current_flow_slno;
    private int current_action_cd;
    private int current_role;
    private String current_status;
    private int prev_action_cd_selected;
    private int current_off_cd;
    private String current_rto_code;
    private OwnerDetailsDobj ownerDetailsDobj;
    private Owner_dobj ownerDobj;
    private VehicleParameters vehicleParameters;
    private StringBuilder exemptionOD = new StringBuilder("");
    private StringBuilder hyptoDtls = new StringBuilder("");
    private boolean renderHypo;
    private boolean documentUploadShow = false;
    private String dmsFileUploadUrl;
    private boolean taxValidityExpired = false;
    private boolean docVerifyForFacelessAppl;
    private String notVerifiedDocdetails;

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
     * @return the pur_desc
     */
    public String getPur_desc() {
        return pur_desc;
    }

    /**
     * @param pur_desc the pur_desc to set
     */
    public void setPur_desc(String pur_desc) {
        this.pur_desc = pur_desc;
    }

    /**
     * @return the appl_date
     */
    public Date getAppl_date() {
        return appl_date;
    }

    /**
     * @param appl_date the appl_date to set
     */
    public void setAppl_date(Date appl_date) {
        this.appl_date = appl_date;
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
     * @return the own_name
     */
    public String getOwn_name() {
        return own_name;
    }

    /**
     * @param own_name the own_name to set
     */
    public void setOwn_name(String own_name) {
        this.own_name = own_name;
    }

    /**
     * @return the chasi_no
     */
    public String getChasi_no() {
        return chasi_no;
    }

    /**
     * @param chasi_no the chasi_no to set
     */
    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
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
     * @return the current_file_movement_slno
     */
    public int getCurrent_file_movement_slno() {
        return current_file_movement_slno;
    }

    /**
     * @param current_file_movement_slno the current_file_movement_slno to set
     */
    public void setCurrent_file_movement_slno(int current_file_movement_slno) {
        this.current_file_movement_slno = current_file_movement_slno;
    }

    /**
     * @return the current_seat_cd
     */
    public String getCurrent_seat_cd() {
        return current_seat_cd;
    }

    /**
     * @param current_seat_cd the current_seat_cd to set
     */
    public void setCurrent_seat_cd(String current_seat_cd) {
        this.current_seat_cd = current_seat_cd;
    }

    /**
     * @return the current_seat_role
     */
    public String getCurrent_seat_role() {
        return current_seat_role;
    }

    /**
     * @param current_seat_role the current_seat_role to set
     */
    public void setCurrent_seat_role(String current_seat_role) {
        this.current_seat_role = current_seat_role;
    }

    /**
     * @return the current_public_remark
     */
    public String getCurrent_public_remark() {
        return current_public_remark;
    }

    /**
     * @param current_public_remark the current_public_remark to set
     */
    public void setCurrent_public_remark(String current_public_remark) {
        this.current_public_remark = current_public_remark;
    }

    /**
     * @return the current_office_remark
     */
    public String getCurrent_office_remark() {
        return current_office_remark;
    }

    /**
     * @param current_office_remark the current_office_remark to set
     */
    public void setCurrent_office_remark(String current_office_remark) {
        this.current_office_remark = current_office_remark;
    }

    /**
     * @return the current_emp_cd
     */
    public int getCurrent_emp_cd() {
        return current_emp_cd;
    }

    /**
     * @param current_emp_cd the current_emp_cd to set
     */
    public void setCurrent_emp_cd(int current_emp_cd) {
        this.current_emp_cd = current_emp_cd;
    }

    /**
     * @return the current_state_cd
     */
    public String getCurrent_state_cd() {
        return current_state_cd;
    }

    /**
     * @param current_state_cd the current_state_cd to set
     */
    public void setCurrent_state_cd(String current_state_cd) {
        this.current_state_cd = current_state_cd;
    }

    /**
     * @return the current_changed_data
     */
    public String getCurrent_changed_data() {
        return current_changed_data;
    }

    /**
     * @param current_changed_data the current_changed_data to set
     */
    public void setCurrent_changed_data(String current_changed_data) {
        this.current_changed_data = current_changed_data;
    }

    /**
     * @return the current_cntr_id
     */
    public String getCurrent_cntr_id() {
        return current_cntr_id;
    }

    /**
     * @param current_cntr_id the current_cntr_id to set
     */
    public void setCurrent_cntr_id(String current_cntr_id) {
        this.current_cntr_id = current_cntr_id;
    }

    /**
     * @return the current_flow_slno
     */
    public int getCurrent_flow_slno() {
        return current_flow_slno;
    }

    /**
     * @param current_flow_slno the current_flow_slno to set
     */
    public void setCurrent_flow_slno(int current_flow_slno) {
        this.current_flow_slno = current_flow_slno;
    }

    /**
     * @return the current_action_cd
     */
    public int getCurrent_action_cd() {
        return current_action_cd;
    }

    /**
     * @param current_action_cd the current_action_cd to set
     */
    public void setCurrent_action_cd(int current_action_cd) {
        this.current_action_cd = current_action_cd;
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
     * @return the current_status
     */
    public String getCurrent_status() {
        return current_status;
    }

    /**
     * @param current_status the current_status to set
     */
    public void setCurrent_status(String current_status) {
        this.current_status = current_status;
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
     * @return the current_off_cd
     */
    public int getCurrent_off_cd() {
        return current_off_cd;
    }

    /**
     * @param current_off_cd the current_off_cd to set
     */
    public void setCurrent_off_cd(int current_off_cd) {
        this.current_off_cd = current_off_cd;
    }

    /**
     * @return the current_rto_code
     */
    public String getCurrent_rto_code() {
        return current_rto_code;
    }

    /**
     * @param current_rto_code the current_rto_code to set
     */
    public void setCurrent_rto_code(String current_rto_code) {
        this.current_rto_code = current_rto_code;
    }

    /**
     * @return the ownerDetailsDobj
     */
    public OwnerDetailsDobj getOwnerDetailsDobj() {
        return ownerDetailsDobj;
    }

    /**
     * @param ownerDetailsDobj the ownerDetailsDobj to set
     */
    public void setOwnerDetailsDobj(OwnerDetailsDobj ownerDetailsDobj) {
        this.ownerDetailsDobj = ownerDetailsDobj;
    }

    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
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
     * @return the exemptionOD
     */
    public StringBuilder getExemptionOD() {
        return exemptionOD;
    }

    /**
     * @param exemptionOD the exemptionOD to set
     */
    public void setExemptionOD(StringBuilder exemptionOD) {
        this.exemptionOD = exemptionOD;
    }

    /**
     * @return the hyptoDtls
     */
    public StringBuilder getHyptoDtls() {
        return hyptoDtls;
    }

    /**
     * @param hyptoDtls the hyptoDtls to set
     */
    public void setHyptoDtls(StringBuilder hyptoDtls) {
        this.hyptoDtls = hyptoDtls;
    }

    /**
     * @return the renderHypo
     */
    public boolean isRenderHypo() {
        return renderHypo;
    }

    /**
     * @param renderHypo the renderHypo to set
     */
    public void setRenderHypo(boolean renderHypo) {
        this.renderHypo = renderHypo;
    }

    /**
     * @return the currentEmpCd
     */
    public String getCurrentEmpCd() {
        return currentEmpCd;
    }

    /**
     * @param currentEmpCd the currentEmpCd to set
     */
    public void setCurrentEmpCd(String currentEmpCd) {
        this.currentEmpCd = currentEmpCd;
    }

    /**
     * @return the documentUploadShow
     */
    public boolean isDocumentUploadShow() {
        return documentUploadShow;
    }

    /**
     * @param documentUploadShow the documentUploadShow to set
     */
    public void setDocumentUploadShow(boolean documentUploadShow) {
        this.documentUploadShow = documentUploadShow;
    }

    /**
     * @return the dmsFileUploadUrl
     */
    public String getDmsFileUploadUrl() {
        return dmsFileUploadUrl;
    }

    /**
     * @param dmsFileUploadUrl the dmsFileUploadUrl to set
     */
    public void setDmsFileUploadUrl(String dmsFileUploadUrl) {
        this.dmsFileUploadUrl = dmsFileUploadUrl;
    }

    /**
     * @return the taxValidityExpired
     */
    public boolean isTaxValidityExpired() {
        return taxValidityExpired;
    }

    /**
     * @param taxValidityExpired the taxValidityExpired to set
     */
    public void setTaxValidityExpired(boolean taxValidityExpired) {
        this.taxValidityExpired = taxValidityExpired;
    }

    /**
     * @return the docVerifyForFacelessAppl
     */
    public boolean isDocVerifyForFacelessAppl() {
        return docVerifyForFacelessAppl;
    }

    /**
     * @param docVerifyForFacelessAppl the docVerifyForFacelessAppl to set
     */
    public void setDocVerifyForFacelessAppl(boolean docVerifyForFacelessAppl) {
        this.docVerifyForFacelessAppl = docVerifyForFacelessAppl;
    }

    /**
     * @return the notVerifiedDocdetails
     */
    public String getNotVerifiedDocdetails() {
        return notVerifiedDocdetails;
    }

    /**
     * @param notVerifiedDocdetails the notVerifiedDocdetails to set
     */
    public void setNotVerifiedDocdetails(String notVerifiedDocdetails) {
        this.notVerifiedDocdetails = notVerifiedDocdetails;
    }
}
