/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;

/**
 *
 * @author Mohd Afzal
 */
public class TaxExemCertDobj implements Serializable {

    private String appl_no;
    private String regn_no;
    private String off_name;
    private String report_header;
    private String report_subheader;
    private String from_date;
    private String upto_date;
    private String ownershiptype;
    private String owner_name;
    private String f_name;
    private String regn_date;
    private String chasi_no;
    private String engine_no;
    private String permanent_address;
    private String temporary_address;
    private String purchase_date;
    private String state_cd;
    private int off_cd;
    private String perm_no;
    private String perm_date;
    private String remark;
    private String printedDate;
    private String op_date;
    private String image_background;
    private String image_logo;
    private boolean show_image_background;
    private boolean show_image_logo;
    private boolean showRoadSafetySlogan;
    private VmRoadSafetySloganPrintDobj roadSafetySloganDobj = null;

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
     * @return the report_header
     */
    public String getReport_header() {
        return report_header;
    }

    /**
     * @param report_header the report_header to set
     */
    public void setReport_header(String report_header) {
        this.report_header = report_header;
    }

    /**
     * @return the report_subheader
     */
    public String getReport_subheader() {
        return report_subheader;
    }

    /**
     * @param report_subheader the report_subheader to set
     */
    public void setReport_subheader(String report_subheader) {
        this.report_subheader = report_subheader;
    }

    /**
     * @return the from_date
     */
    public String getFrom_date() {
        return from_date;
    }

    /**
     * @param from_date the from_date to set
     */
    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    /**
     * @return the upto_date
     */
    public String getUpto_date() {
        return upto_date;
    }

    /**
     * @param upto_date the upto_date to set
     */
    public void setUpto_date(String upto_date) {
        this.upto_date = upto_date;
    }

    /**
     * @return the ownershiptype
     */
    public String getOwnershiptype() {
        return ownershiptype;
    }

    /**
     * @param ownershiptype the ownershiptype to set
     */
    public void setOwnershiptype(String ownershiptype) {
        this.ownershiptype = ownershiptype;
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
     * @return the f_name
     */
    public String getF_name() {
        return f_name;
    }

    /**
     * @param f_name the f_name to set
     */
    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    /**
     * @return the regn_date
     */
    public String getRegn_date() {
        return regn_date;
    }

    /**
     * @param regn_date the regn_date to set
     */
    public void setRegn_date(String regn_date) {
        this.regn_date = regn_date;
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
     * @return the engine_no
     */
    public String getEngine_no() {
        return engine_no;
    }

    /**
     * @param engine_no the engine_no to set
     */
    public void setEngine_no(String engine_no) {
        this.engine_no = engine_no;
    }

    /**
     * @return the permanent_address
     */
    public String getPermanent_address() {
        return permanent_address;
    }

    /**
     * @param permanent_address the permanent_address to set
     */
    public void setPermanent_address(String permanent_address) {
        this.permanent_address = permanent_address;
    }

    /**
     * @return the temporary_address
     */
    public String getTemporary_address() {
        return temporary_address;
    }

    /**
     * @param temporary_address the temporary_address to set
     */
    public void setTemporary_address(String temporary_address) {
        this.temporary_address = temporary_address;
    }

    /**
     * @return the purchase_date
     */
    public String getPurchase_date() {
        return purchase_date;
    }

    /**
     * @param purchase_date the purchase_date to set
     */
    public void setPurchase_date(String purchase_date) {
        this.purchase_date = purchase_date;
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
     * @return the perm_no
     */
    public String getPerm_no() {
        return perm_no;
    }

    /**
     * @param perm_no the perm_no to set
     */
    public void setPerm_no(String perm_no) {
        this.perm_no = perm_no;
    }

    /**
     * @return the perm_date
     */
    public String getPerm_date() {
        return perm_date;
    }

    /**
     * @param perm_date the perm_date to set
     */
    public void setPerm_date(String perm_date) {
        this.perm_date = perm_date;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the printedDate
     */
    public String getPrintedDate() {
        return printedDate;
    }

    /**
     * @param printedDate the printedDate to set
     */
    public void setPrintedDate(String printedDate) {
        this.printedDate = printedDate;
    }

    /**
     * @return the op_date
     */
    public String getOp_date() {
        return op_date;
    }

    /**
     * @param op_date the op_date to set
     */
    public void setOp_date(String op_date) {
        this.op_date = op_date;
    }

    /**
     * @return the image_background
     */
    public String getImage_background() {
        return image_background;
    }

    /**
     * @param image_background the image_background to set
     */
    public void setImage_background(String image_background) {
        this.image_background = image_background;
    }

    /**
     * @return the image_logo
     */
    public String getImage_logo() {
        return image_logo;
    }

    /**
     * @param image_logo the image_logo to set
     */
    public void setImage_logo(String image_logo) {
        this.image_logo = image_logo;
    }

    /**
     * @return the show_image_background
     */
    public boolean isShow_image_background() {
        return show_image_background;
    }

    /**
     * @param show_image_background the show_image_background to set
     */
    public void setShow_image_background(boolean show_image_background) {
        this.show_image_background = show_image_background;
    }

    /**
     * @return the show_image_logo
     */
    public boolean isShow_image_logo() {
        return show_image_logo;
    }

    /**
     * @param show_image_logo the show_image_logo to set
     */
    public void setShow_image_logo(boolean show_image_logo) {
        this.show_image_logo = show_image_logo;
    }

    /**
     * @return the showRoadSafetySlogan
     */
    public boolean isShowRoadSafetySlogan() {
        return showRoadSafetySlogan;
    }

    /**
     * @param showRoadSafetySlogan the showRoadSafetySlogan to set
     */
    public void setShowRoadSafetySlogan(boolean showRoadSafetySlogan) {
        this.showRoadSafetySlogan = showRoadSafetySlogan;
    }

    /**
     * @return the roadSafetySloganDobj
     */
    public VmRoadSafetySloganPrintDobj getRoadSafetySloganDobj() {
        return roadSafetySloganDobj;
    }

    /**
     * @param roadSafetySloganDobj the roadSafetySloganDobj to set
     */
    public void setRoadSafetySloganDobj(VmRoadSafetySloganPrintDobj roadSafetySloganDobj) {
        this.roadSafetySloganDobj = roadSafetySloganDobj;
    }
}
