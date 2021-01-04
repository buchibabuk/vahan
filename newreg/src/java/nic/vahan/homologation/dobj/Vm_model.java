/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.homologation.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nprpE070
 */
public class Vm_model implements Serializable {

    private List<String> actionSource = new ArrayList<String>();
    private List<String> actionTarget = new ArrayList<String>();
    private String maker_wmi_code;
    private String model_cd_prv;
    private String model_cd;
    private String model_name;
    private String model_name_on_rc;
    private int taa_cd;
    private String tac_no;
    private String vch_make_as;
    private String vch_catg;
    private String vch_class;
    private int seat_cap;
    private float cubic_cap;
    private float engine_power;
    private float engine_power_bifuel;
    private int unld_wt;
    private int gvw;
    private Integer gcw;
    private int fuelCode;
    private String fuelDescr;
    private String body_type;
    private int no_of_cyl;
    private int wheelbase;
    private int norms;
    private String norms_descr;
    private String f_axle_descp;
    private String r_axle_descp;
    private String o1_axle_descp;
    private String o2_axle_descp;
    private String o3_axle_descp;
    private String o4_axle_descp;
    private String o5_axle_descp;
    private String t_axle_descp;
    private Integer f_axle_weight = null;
    private Integer r_axle_weight = null;
    private Integer o1_axle_weight = null;
    private Integer o2_axle_weight = null;
    private Integer o3_axle_weight = null;
    private Integer o4_axle_weight = null;
    private Integer o5_axle_weight = null;
    private Integer t_axle_weight = null;
    private Integer length = null;
    private Integer width = null;
    private Integer height = null;
    private Integer f_overhang = null;
    private Integer r_overhang = null;
    private String body_builder_acc_no = "";
    private String status;
    private String entered_by;
    private String entered_on;
    private String approved_by;
    private String approved_on;
    private String agency_name;
    //----
    private String approvalCertNo = "";
    private boolean isCreate = false;
    private int serialNo = 0;
    private int maker_code = 0;
    private String maker_descr;
    private String reason;
    private boolean showApproval;
    private String state_cd;
    private String targetstr;

    /**
     * @return the maker_wmi_code
     */
    public String getMaker_wmi_code() {
        return maker_wmi_code;
    }

    /**
     * @param maker_wmi_code the maker_wmi_code to set
     */
    public void setMaker_wmi_code(String maker_wmi_code) {
        this.maker_wmi_code = maker_wmi_code;
    }

    /**
     * @return the model_cd
     */
    public String getModel_cd() {
        return model_cd;
    }

    /**
     * @param model_cd the model_cd to set
     */
    public void setModel_cd(String model_cd) {
        this.model_cd = model_cd;
    }

    /**
     * @return the model_name
     */
    public String getModel_name() {
        return model_name;
    }

    /**
     * @param model_name the model_name to set
     */
    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    /**
     * @return the taa_cd
     */
    public int getTaa_cd() {
        return taa_cd;
    }

    /**
     * @param taa_cd the taa_cd to set
     */
    public void setTaa_cd(int taa_cd) {
        this.taa_cd = taa_cd;
    }

    /**
     * @return the tac_no
     */
    public String getTac_no() {
        return tac_no;
    }

    /**
     * @param tac_no the tac_no to set
     */
    public void setTac_no(String tac_no) {
        this.tac_no = tac_no;
    }

    /**
     * @return the vch_make_as
     */
    public String getVch_make_as() {
        return vch_make_as;
    }

    /**
     * @param vch_make_as the vch_make_as to set
     */
    public void setVch_make_as(String vch_make_as) {
        this.vch_make_as = vch_make_as;
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
     * @return the seat_cap
     */
    public int getSeat_cap() {
        return seat_cap;
    }

    /**
     * @param seat_cap the seat_cap to set
     */
    public void setSeat_cap(int seat_cap) {
        this.seat_cap = seat_cap;
    }

    /**
     * @return the cubic_cap
     */
    public float getCubic_cap() {
        return cubic_cap;
    }

    /**
     * @param cubic_cap the cubic_cap to set
     */
    public void setCubic_cap(float cubic_cap) {
        this.cubic_cap = cubic_cap;
    }

    /**
     * @return the engine_power
     */
    public float getEngine_power() {
        return engine_power;
    }

    /**
     * @param engine_power the engine_power to set
     */
    public void setEngine_power(float engine_power) {
        this.engine_power = engine_power;
    }

    /**
     * @return the unld_wt
     */
    public int getUnld_weight() {
        return unld_wt;
    }

    /**
     * @param unld_wt the unld_wt to set
     */
    public void setUnld_weight(int unld_wt) {
        this.unld_wt = unld_wt;
    }

    /**
     * @return the gvw
     */
    public int getGvw() {
        return gvw;
    }

    /**
     * @param gvw the gvw to set
     */
    public void setGvw(int gvw) {
        this.gvw = gvw;
    }

    /**
     * @return the gcw
     */
//    public int getGcw() {
//        return gcw;
//    }
//
//    /**
//     * @param gcw the gcw to set
//     */
//    public void setGcw(int gcw) {
//        this.gcw = gcw;
//    }
    /**
     * @return the body_type
     */
    public String getBody_type() {
        return body_type;
    }

    /**
     * @param body_type the body_type to set
     */
    public void setBody_type(String body_type) {
        this.body_type = body_type;
    }

    /**
     * @return the no_of_cyl
     */
    public int getNo_of_cyl() {
        return no_of_cyl;
    }

    /**
     * @param no_of_cyl the no_of_cyl to set
     */
    public void setNo_of_cyl(int no_of_cyl) {
        this.no_of_cyl = no_of_cyl;
    }

    /**
     * @return the wheelbase
     */
    public int getWheelbase() {
        return wheelbase;
    }

    /**
     * @param wheelbase the wheelbase to set
     */
    public void setWheelbase(int wheelbase) {
        this.wheelbase = wheelbase;
    }

    /**
     * @return the f_axle_descp
     */
    public String getF_axle_descp() {
        return f_axle_descp;
    }

    /**
     * @param f_axle_descp the f_axle_descp to set
     */
    public void setF_axle_descp(String f_axle_descp) {
        this.f_axle_descp = f_axle_descp;
    }

    /**
     * @return the r_axle_descp
     */
    public String getR_axle_descp() {
        return r_axle_descp;
    }

    /**
     * @param r_axle_descp the r_axle_descp to set
     */
    public void setR_axle_descp(String r_axle_descp) {
        this.r_axle_descp = r_axle_descp;
    }

    /**
     * @return the t_axle_descp
     */
    public String getT_axle_descp() {
        return t_axle_descp;
    }

    /**
     * @param t_axle_descp the t_axle_descp to set
     */
    public void setT_axle_descp(String t_axle_descp) {
        this.t_axle_descp = t_axle_descp;
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
     * @return the entered_by
     */
    public String getEntered_by() {
        return entered_by;
    }

    /**
     * @param entered_by the entered_by to set
     */
    public void setEntered_by(String entered_by) {
        this.entered_by = entered_by;
    }

    /**
     * @return the entered_on
     */
    public String getEntered_on() {
        return entered_on;
    }

    /**
     * @param entered_on the entered_on to set
     */
    public void setEntered_on(String entered_on) {
        this.entered_on = entered_on;
    }

    /**
     * @return the approved_by
     */
    public String getApproved_by() {
        return approved_by;
    }

    /**
     * @param approved_by the approved_by to set
     */
    public void setApproved_by(String approved_by) {
        this.approved_by = approved_by;
    }

    /**
     * @return the approved_on
     */
    public String getApproved_on() {
        return approved_on;
    }

    /**
     * @param approved_on the approved_on to set
     */
    public void setApproved_on(String approved_on) {
        this.approved_on = approved_on;
    }

    /**
     * @return the approvalCertNo
     */
    public String getApprovalCertNo() {
        return approvalCertNo;
    }

    /**
     * @param approvalCertNo the approvalCertNo to set
     */
    public void setApprovalCertNo(String approvalCertNo) {
        this.approvalCertNo = approvalCertNo;
    }

    /**
     * @return the o1_axle_descp
     */
    public String getO1_axle_descp() {
        return o1_axle_descp;
    }

    /**
     * @param o1_axle_descp the o1_axle_descp to set
     */
    public void setO1_axle_descp(String o1_axle_descp) {
        this.o1_axle_descp = o1_axle_descp;
    }

    /**
     * @return the o2_axle_descp
     */
    public String getO2_axle_descp() {
        return o2_axle_descp;
    }

    /**
     * @param o2_axle_descp the o2_axle_descp to set
     */
    public void setO2_axle_descp(String o2_axle_descp) {
        this.o2_axle_descp = o2_axle_descp;
    }

    /**
     * @return the o3_axle_descp
     */
    public String getO3_axle_descp() {
        return o3_axle_descp;
    }

    /**
     * @param o3_axle_descp the o3_axle_descp to set
     */
    public void setO3_axle_descp(String o3_axle_descp) {
        this.o3_axle_descp = o3_axle_descp;
    }

    /**
     * @return the o4_axle_descp
     */
    public String getO4_axle_descp() {
        return o4_axle_descp;
    }

    /**
     * @param o4_axle_descp the o4_axle_descp to set
     */
    public void setO4_axle_descp(String o4_axle_descp) {
        this.o4_axle_descp = o4_axle_descp;
    }

    /**
     * @return the o5_axle_descp
     */
    public String getO5_axle_descp() {
        return o5_axle_descp;
    }

    /**
     * @param o5_axle_descp the o5_axle_descp to set
     */
    public void setO5_axle_descp(String o5_axle_descp) {
        this.o5_axle_descp = o5_axle_descp;
    }

    /**
     * @return the body_builder_acc_no
     */
    public String getBody_builder_acc_no() {
        return body_builder_acc_no;
    }

    /**
     * @param body_builder_acc_no the body_builder_acc_no to set
     */
    public void setBody_builder_acc_no(String body_builder_acc_no) {
        this.body_builder_acc_no = body_builder_acc_no;
    }

    /**
     * @return the vch_class
     */
    public String getVch_class() {
        return vch_class;
    }

    /**
     * @param vch_class the vch_class to set
     */
    public void setVch_class(String vch_class) {
        this.vch_class = vch_class;
    }

    /**
     * @return the isCreate
     */
    public boolean isIsCreate() {
        return isCreate;
    }

    /**
     * @param isCreate the isCreate to set
     */
    public void setIsCreate(boolean isCreate) {
        this.isCreate = isCreate;
    }

    /**
     * @return the serialNo
     */
    public int getSerialNo() {
        return serialNo;
    }

    /**
     * @param serialNo the serialNo to set
     */
    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * @return the agency_name
     */
    public String getAgency_name() {
        return agency_name;
    }

    /**
     * @param agency_name the agency_name to set
     */
    public void setAgency_name(String agency_name) {
        this.agency_name = agency_name;
    }

    /**
     * @return the maker_code
     */
    public int getMaker_code() {
        return maker_code;
    }

    /**
     * @param maker_code the maker_code to set
     */
    public void setMaker_code(int maker_code) {
        this.maker_code = maker_code;
    }

    /**
     * @return the model_cd_prv
     */
    public String getModel_cd_prv() {
        return model_cd_prv;
    }

    /**
     * @param model_cd_prv the model_cd_prv to set
     */
    public void setModel_cd_prv(String model_cd_prv) {
        this.model_cd_prv = model_cd_prv;
    }

    /**
     * @return the gcw
     */
    public Integer getGcw() {
        return gcw;
    }

    /**
     * @param gcw the gcw to set
     */
    public void setGcw(Integer gcw) {
        this.gcw = gcw;
    }

    /**
     * @return the f_axle_weight
     */
    public Integer getF_axle_weight() {
        return f_axle_weight;
    }

    /**
     * @param f_axle_weight the f_axle_weight to set
     */
    public void setF_axle_weight(Integer f_axle_weight) {
        this.f_axle_weight = f_axle_weight;
    }

    /**
     * @return the r_axle_weight
     */
    public Integer getR_axle_weight() {
        return r_axle_weight;
    }

    /**
     * @param r_axle_weight the r_axle_weight to set
     */
    public void setR_axle_weight(Integer r_axle_weight) {
        this.r_axle_weight = r_axle_weight;
    }

    /**
     * @return the o1_axle_weight
     */
    public Integer getO1_axle_weight() {
        return o1_axle_weight;
    }

    /**
     * @param o1_axle_weight the o1_axle_weight to set
     */
    public void setO1_axle_weight(Integer o1_axle_weight) {
        this.o1_axle_weight = o1_axle_weight;
    }

    /**
     * @return the o2_axle_weight
     */
    public Integer getO2_axle_weight() {
        return o2_axle_weight;
    }

    /**
     * @param o2_axle_weight the o2_axle_weight to set
     */
    public void setO2_axle_weight(Integer o2_axle_weight) {
        this.o2_axle_weight = o2_axle_weight;
    }

    /**
     * @return the o3_axle_weight
     */
    public Integer getO3_axle_weight() {
        return o3_axle_weight;
    }

    /**
     * @param o3_axle_weight the o3_axle_weight to set
     */
    public void setO3_axle_weight(Integer o3_axle_weight) {
        this.o3_axle_weight = o3_axle_weight;
    }

    /**
     * @return the o4_axle_weight
     */
    public Integer getO4_axle_weight() {
        return o4_axle_weight;
    }

    /**
     * @param o4_axle_weight the o4_axle_weight to set
     */
    public void setO4_axle_weight(Integer o4_axle_weight) {
        this.o4_axle_weight = o4_axle_weight;
    }

    /**
     * @return the o5_axle_weight
     */
    public Integer getO5_axle_weight() {
        return o5_axle_weight;
    }

    /**
     * @param o5_axle_weight the o5_axle_weight to set
     */
    public void setO5_axle_weight(Integer o5_axle_weight) {
        this.o5_axle_weight = o5_axle_weight;
    }

    /**
     * @return the t_axle_weight
     */
    public Integer getT_axle_weight() {
        return t_axle_weight;
    }

    /**
     * @param t_axle_weight the t_axle_weight to set
     */
    public void setT_axle_weight(Integer t_axle_weight) {
        this.t_axle_weight = t_axle_weight;
    }

    /**
     * @return the length
     */
    public Integer getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(Integer length) {
        this.length = length;
    }

    /**
     * @return the width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * @return the f_overhang
     */
    public Integer getF_overhang() {
        return f_overhang;
    }

    /**
     * @param f_overhang the f_overhang to set
     */
    public void setF_overhang(Integer f_overhang) {
        this.f_overhang = f_overhang;
    }

    /**
     * @return the r_overhang
     */
    public Integer getR_overhang() {
        return r_overhang;
    }

    /**
     * @param r_overhang the r_overhang to set
     */
    public void setR_overhang(Integer r_overhang) {
        this.r_overhang = r_overhang;
    }

    /**
     * @return the maker_descr
     */
    public String getMaker_descr() {
        return maker_descr;
    }

    /**
     * @param maker_descr the maker_descr to set
     */
    public void setMaker_descr(String maker_descr) {
        this.maker_descr = maker_descr;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the fuelCode
     */
    public int getFuelCode() {
        return fuelCode;
    }

    /**
     * @param fuelCode the fuelCode to set
     */
    public void setFuelCode(int fuelCode) {
        this.fuelCode = fuelCode;
    }

    /**
     * @return the fuelDescr
     */
    public String getFuelDescr() {
        return fuelDescr;
    }

    /**
     * @param fuelDescr the fuelDescr to set
     */
    public void setFuelDescr(String fuelDescr) {
        this.fuelDescr = fuelDescr;
    }

    /**
     * @return the showApproval
     */
    public boolean isShowApproval() {
        return showApproval;
    }

    /**
     * @param showApproval the showApproval to set
     */
    public void setShowApproval(boolean showApproval) {
        this.showApproval = showApproval;
    }

    /**
     * @return the engine_power_bifuel
     */
    public float getEngine_power_bifuel() {
        return engine_power_bifuel;
    }

    /**
     * @param engine_power_bifuel the engine_power_bifuel to set
     */
    public void setEngine_power_bifuel(float engine_power_bifuel) {
        this.engine_power_bifuel = engine_power_bifuel;
    }

    /**
     * @return the norms
     */
    public int getNorms() {
        return norms;
    }

    /**
     * @param norms the norms to set
     */
    public void setNorms(int norms) {
        this.norms = norms;
    }

    /**
     * @return the norms_descr
     */
    public String getNorms_descr() {
        return norms_descr;
    }

    /**
     * @param norms_descr the norms_descr to set
     */
    public void setNorms_descr(String norms_descr) {
        this.norms_descr = norms_descr;
    }

    /**
     * @return the model_name_on_rc
     */
    public String getModel_name_on_rc() {
        return model_name_on_rc;
    }

    /**
     * @param model_name_on_rc the model_name_on_rc to set
     */
    public void setModel_name_on_rc(String model_name_on_rc) {
        this.model_name_on_rc = model_name_on_rc;
    }

    /**
     * @return the actionSource
     */
    public List<String> getActionSource() {
        return actionSource;
    }

    /**
     * @param actionSource the actionSource to set
     */
    public void setActionSource(List<String> actionSource) {
        this.actionSource = actionSource;
    }

    /**
     * @return the actionTarget
     */
    public List<String> getActionTarget() {
        return actionTarget;
    }

    /**
     * @param actionTarget the actionTarget to set
     */
    public void setActionTarget(List<String> actionTarget) {
        this.actionTarget = actionTarget;
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
     * @return the targetstr
     */
    public String getTargetstr() {
        return targetstr;
    }

    /**
     * @param targetstr the targetstr to set
     */
    public void setTargetstr(String targetstr) {
        this.targetstr = targetstr;
    }
}
