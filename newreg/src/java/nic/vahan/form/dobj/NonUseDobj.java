package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author nicsi
 */
public class NonUseDobj implements Cloneable, Serializable {

    private String appl_no;
    private String regn_no;
    private String non_use_purpose;
    private Date exemp_from;
    private Date exemp_to;
    private String authorised_by;
    private String permission_no;
    private Date permission_dt;
    private String location_of_garage;
    private Date vehicle_use_frm;
    private int amount;
    private String inspectedBy;
    private String inspectionReportNo;
    private Date inspectionDate;
    private String speecifiedLocation;
    private String insFlag;
    private String doc_flag;
    private String doc_Details;
    private long adjustmentAmount;
    private long nonUsePenalty;
    private String certifiedBy;
    private Date certiPermissionDt;
    private Date certificationDt;
    private String towedVehicleNo;
    private String regisAuthority;
    private String newGarageLocation;
    private String valueOfRadioEvent;
    private int pur_cd;
    private String owner_name;
    private String vh_class_desc;
    private int vehType;
    private String vehTypeDescr;
    private String vch_catg;
    private String period_from;
    private String period_to;
    private String owner_state;
    private String off_name;
    private String current_year;
    private String current_date;
    private String remarks;
    private String garage_add1;
    private String garage_add2;
    private String garage_add3;
    private int garage_district;
    private String garage_district_descr;
    private String garage_state;
    private int garage_pincode;
    private String rcpt_heading;
    private String rcpt_sub_heading;
    private String section_act_rule;
    private String nonUseDeclareDtls;
    private Date exemFrmDate;
    private Date exemUptoDate;
    private String vehicle_used_from_dtls;
    private String approved_authority;

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getNon_use_purpose() {
        return non_use_purpose;
    }

    public void setNon_use_purpose(String non_use_purpose) {
        this.non_use_purpose = non_use_purpose;
    }

    public Date getExemp_from() {
        return exemp_from;
    }

    public void setExemp_from(Date exemp_from) {
        this.exemp_from = exemp_from;
    }

    public Date getExemp_to() {
        return exemp_to;
    }

    public void setExemp_to(Date exemp_to) {
        this.exemp_to = exemp_to;
    }

    public String getSpeecifiedLocation() {
        return speecifiedLocation;
    }

    public void setSpeecifiedLocation(String speecifiedLocation) {
        this.speecifiedLocation = speecifiedLocation;
    }

    public String getAuthorised_by() {
        return authorised_by;
    }

    public void setAuthorised_by(String authorised_by) {
        this.authorised_by = authorised_by;
    }

    public String getLocation_of_garage() {
        return location_of_garage;
    }

    public void setLocation_of_garage(String location_of_garage) {
        this.location_of_garage = location_of_garage;
    }

    public String getPermission_no() {
        return permission_no;
    }

    public void setPermission_no(String permission_no) {
        this.permission_no = permission_no;
    }

    public Date getPermission_dt() {
        return permission_dt;
    }

    public void setPermission_dt(Date permission_dt) {
        this.permission_dt = permission_dt;
    }

    public Date getVehicle_use_frm() {
        return vehicle_use_frm;
    }

    public void setVehicle_use_frm(Date vehicle_use_frm) {
        this.vehicle_use_frm = vehicle_use_frm;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getInspectedBy() {
        return inspectedBy;
    }

    public void setInspectedBy(String inspectedBy) {
        this.inspectedBy = inspectedBy;
    }

    public String getInspectionReportNo() {
        return inspectionReportNo;
    }

    public void setInspectionReportNo(String inspectionReportNo) {
        this.inspectionReportNo = inspectionReportNo;
    }

    public Date getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(Date inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getInsFlag() {
        return insFlag;
    }

    public void setInsFlag(String insFlag) {
        this.insFlag = insFlag;
    }

    public String getDoc_flag() {
        return doc_flag;
    }

    public void setDoc_flag(String doc_flag) {
        this.doc_flag = doc_flag;
    }

    public String getDoc_Details() {
        return doc_Details;
    }

    public void setDoc_Details(String doc_Details) {
        this.doc_Details = doc_Details;
    }

    public long getAdjustmentAmount() {
        return adjustmentAmount;
    }

    public void setAdjustmentAmount(long adjustmentAmount) {
        this.adjustmentAmount = adjustmentAmount;
    }

    public String getCertifiedBy() {
        return certifiedBy;
    }

    public void setCertifiedBy(String certifiedBy) {
        this.certifiedBy = certifiedBy;
    }

    public Date getCertiPermissionDt() {
        return certiPermissionDt;
    }

    public void setCertiPermissionDt(Date certiPermissionDt) {
        this.certiPermissionDt = certiPermissionDt;
    }

    public Date getCertificationDt() {
        return certificationDt;
    }

    public void setCertificationDt(Date certificationDt) {
        this.certificationDt = certificationDt;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getTowedVehicleNo() {
        return towedVehicleNo;
    }

    public void setTowedVehicleNo(String towedVehicleNo) {
        this.towedVehicleNo = towedVehicleNo;
    }

    public String getRegisAuthority() {
        return regisAuthority;
    }

    public void setRegisAuthority(String regisAuthority) {
        this.regisAuthority = regisAuthority;
    }

    public String getNewGarageLocation() {
        return newGarageLocation;
    }

    public void setNewGarageLocation(String newGarageLocation) {
        this.newGarageLocation = newGarageLocation;
    }

    public String getValueOfRadioEvent() {
        return valueOfRadioEvent;
    }

    public void setValueOfRadioEvent(String valueOfRadioEvent) {
        this.valueOfRadioEvent = valueOfRadioEvent;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public long getNonUsePenalty() {
        return nonUsePenalty;
    }

    public void setNonUsePenalty(long nonUsePenalty) {
        this.nonUsePenalty = nonUsePenalty;
    }

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getGarage_add1() {
        return garage_add1;
    }

    public void setGarage_add1(String garage_add1) {
        this.garage_add1 = garage_add1;
    }

    public String getGarage_add2() {
        return garage_add2;
    }

    public void setGarage_add2(String garage_add2) {
        this.garage_add2 = garage_add2;
    }

    public String getGarage_add3() {
        return garage_add3;
    }

    public void setGarage_add3(String garage_add3) {
        this.garage_add3 = garage_add3;
    }

    public int getGarage_district() {
        return garage_district;
    }

    public void setGarage_district(int garage_district) {
        this.garage_district = garage_district;
    }

    public String getGarage_state() {
        return garage_state;
    }

    public void setGarage_state(String garage_state) {
        this.garage_state = garage_state;
    }

    public int getGarage_pincode() {
        return garage_pincode;
    }

    public void setGarage_pincode(int garage_pincode) {
        this.garage_pincode = garage_pincode;
    }

    /**
     * @return the owner_name
     */
    public String getOwner_name() {
        return owner_name;
    }

    /**
     * @param owner_name the owner_name to set
     */
    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    /**
     * @return the vh_class_desc
     */
    public String getVh_class_desc() {
        return vh_class_desc;
    }

    /**
     * @param vh_class_desc the vh_class_desc to set
     */
    public void setVh_class_desc(String vh_class_desc) {
        this.vh_class_desc = vh_class_desc;
    }

    /**
     * @return the vehType
     */
    public int getVehType() {
        return vehType;
    }

    /**
     * @param vehType the vehType to set
     */
    public void setVehType(int vehType) {
        this.vehType = vehType;
    }

    /**
     * @return the vehTypeDescr
     */
    public String getVehTypeDescr() {
        return vehTypeDescr;
    }

    /**
     * @param vehTypeDescr the vehTypeDescr to set
     */
    public void setVehTypeDescr(String vehTypeDescr) {
        this.vehTypeDescr = vehTypeDescr;
    }

    /**
     * @return the vch_catg
     */
    public String getVch_catg() {
        return vch_catg;
    }

    /**
     * @param vch_catg the vch_catg to set
     */
    public void setVch_catg(String vch_catg) {
        this.vch_catg = vch_catg;
    }

    /**
     * @return the period_from
     */
    public String getPeriod_from() {
        return period_from;
    }

    /**
     * @param period_from the period_from to set
     */
    public void setPeriod_from(String period_from) {
        this.period_from = period_from;
    }

    /**
     * @return the period_to
     */
    public String getPeriod_to() {
        return period_to;
    }

    /**
     * @param period_to the period_to to set
     */
    public void setPeriod_to(String period_to) {
        this.period_to = period_to;
    }

    /**
     * @return the owner_state
     */
    public String getOwner_state() {
        return owner_state;
    }

    /**
     * @param owner_state the owner_state to set
     */
    public void setOwner_state(String owner_state) {
        this.owner_state = owner_state;
    }

    /**
     * @return the off_name
     */
    public String getOff_name() {
        return off_name;
    }

    /**
     * @param off_name the off_name to set
     */
    public void setOff_name(String off_name) {
        this.off_name = off_name;
    }

    /**
     * @return the current_year
     */
    public String getCurrent_year() {
        return current_year;
    }

    /**
     * @param current_year the current_year to set
     */
    public void setCurrent_year(String current_year) {
        this.current_year = current_year;
    }

    /**
     * @return the current_date
     */
    public String getCurrent_date() {
        return current_date;
    }

    /**
     * @param current_date the current_date to set
     */
    public void setCurrent_date(String current_date) {
        this.current_date = current_date;
    }

    public String getRcpt_heading() {
        return rcpt_heading;
    }

    public void setRcpt_heading(String rcpt_heading) {
        this.rcpt_heading = rcpt_heading;
    }

    public String getRcpt_sub_heading() {
        return rcpt_sub_heading;
    }

    public void setRcpt_sub_heading(String rcpt_sub_heading) {
        this.rcpt_sub_heading = rcpt_sub_heading;
    }

    public String getGarage_district_descr() {
        return garage_district_descr;
    }

    public void setGarage_district_descr(String garage_district_descr) {
        this.garage_district_descr = garage_district_descr;
    }

    /**
     * @return the section_act_rule
     */
    public String getSection_act_rule() {
        return section_act_rule;
    }

    /**
     * @param section_act_rule the section_act_rule to set
     */
    public void setSection_act_rule(String section_act_rule) {
        this.section_act_rule = section_act_rule;
    }

    /**
     * @return the nonUseDeclareDtls
     */
    public String getNonUseDeclareDtls() {
        return nonUseDeclareDtls;
    }

    /**
     * @param nonUseDeclareDtls the nonUseDeclareDtls to set
     */
    public void setNonUseDeclareDtls(String nonUseDeclareDtls) {
        this.nonUseDeclareDtls = nonUseDeclareDtls;
    }

    /**
     * @return the exemFrmDate
     */
    public Date getExemFrmDate() {
        return exemFrmDate;
    }

    /**
     * @param exemFrmDate the exemFrmDate to set
     */
    public void setExemFrmDate(Date exemFrmDate) {
        this.exemFrmDate = exemFrmDate;
    }

    /**
     * @return the exemUptoDate
     */
    public Date getExemUptoDate() {
        return exemUptoDate;
    }

    /**
     * @param exemUptoDate the exemUptoDate to set
     */
    public void setExemUptoDate(Date exemUptoDate) {
        this.exemUptoDate = exemUptoDate;
    }

    /**
     * @return the vehicle_used_from_dtls
     */
    public String getVehicle_used_from_dtls() {
        return vehicle_used_from_dtls;
    }

    /**
     * @param vehicle_used_from_dtls the vehicle_used_from_dtls to set
     */
    public void setVehicle_used_from_dtls(String vehicle_used_from_dtls) {
        this.vehicle_used_from_dtls = vehicle_used_from_dtls;
    }

    /**
     * @return the approved_authority
     */
    public String getApproved_authority() {
        return approved_authority;
    }

    /**
     * @param approved_authority the approved_authority to set
     */
    public void setApproved_authority(String approved_authority) {
        this.approved_authority = approved_authority;
    }
}
