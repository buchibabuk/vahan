/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;

/**
 *
 * @author nic
 */
public class NOCReportDobj implements Serializable {

    private String regn_no;
    private String eng_no;
    private String tax_valid_upto;
    private String noc_ref_no;
    private String state_to;
    private String regn_dt;
    private String chassi_no;
    private String noc_issue_dt;
    private String dispatch_no;
    private String rto_to;
    private String StateName;
    private String offName;
    private String header;
    private String subHeader;
    private String printDate;
    private String fncr_full_add;
    private boolean ishypothecated = false;
    private String noc_no;
    private String noc_cc_lable;
    private String noc_cc_fieldlable;
    //add
    private String descr;
    private String new_own_name;
    private boolean render_Descr;
    private boolean render_newName;
    private byte[] userSign;
    private boolean isUserSignExist;
    private String image_background;
    private String image_logo;
    private boolean show_image_background;
    private boolean show_image_logo;
    private String appl_no;
    private boolean print_no_dues_cert;

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
     * @return the eng_no
     */
    public String getEng_no() {
        return eng_no;
    }

    /**
     * @param eng_no the eng_no to set
     */
    public void setEng_no(String eng_no) {
        this.eng_no = eng_no;
    }

    /**
     * @return the tax_valid_upto
     */
    public String getTax_valid_upto() {
        return tax_valid_upto;
    }

    /**
     * @param tax_valid_upto the tax_valid_upto to set
     */
    public void setTax_valid_upto(String tax_valid_upto) {
        this.tax_valid_upto = tax_valid_upto;
    }

    /**
     * @return the noc_ref_no
     */
    public String getNoc_ref_no() {
        return noc_ref_no;
    }

    /**
     * @param noc_ref_no the noc_ref_no to set
     */
    public void setNoc_ref_no(String noc_ref_no) {
        this.noc_ref_no = noc_ref_no;
    }

    /**
     * @return the state_to
     */
    public String getState_to() {
        return state_to;
    }

    /**
     * @param state_to the state_to to set
     */
    public void setState_to(String state_to) {
        this.state_to = state_to;
    }

    /**
     * @return the regn_dt
     */
    public String getRegn_dt() {
        return regn_dt;
    }

    /**
     * @param regn_dt the regn_dt to set
     */
    public void setRegn_dt(String regn_dt) {
        this.regn_dt = regn_dt;
    }

    /**
     * @return the chassi_no
     */
    public String getChassi_no() {
        return chassi_no;
    }

    /**
     * @param chassi_no the chassi_no to set
     */
    public void setChassi_no(String chassi_no) {
        this.chassi_no = chassi_no;
    }

    /**
     * @return the noc_issue_dt
     */
    public String getNoc_issue_dt() {
        return noc_issue_dt;
    }

    /**
     * @param noc_issue_dt the noc_issue_dt to set
     */
    public void setNoc_issue_dt(String noc_issue_dt) {
        this.noc_issue_dt = noc_issue_dt;
    }

    /**
     * @return the dispatch_no
     */
    public String getDispatch_no() {
        return dispatch_no;
    }

    /**
     * @param dispatch_no the dispatch_no to set
     */
    public void setDispatch_no(String dispatch_no) {
        this.dispatch_no = dispatch_no;
    }

    /**
     * @return the rto_to
     */
    public String getRto_to() {
        return rto_to;
    }

    /**
     * @param rto_to the rto_to to set
     */
    public void setRto_to(String rto_to) {
        this.rto_to = rto_to;
    }

    /**
     * @return the StateName
     */
    public String getStateName() {
        return StateName;
    }

    /**
     * @param StateName the StateName to set
     */
    public void setStateName(String StateName) {
        this.StateName = StateName;
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
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the printDate
     */
    public String getPrintDate() {
        return printDate;
    }

    /**
     * @param printDate the printDate to set
     */
    public void setPrintDate(String printDate) {
        this.printDate = printDate;
    }

    /**
     * @return the subHeader
     */
    public String getSubHeader() {
        return subHeader;
    }

    /**
     * @param subHeader the subHeader to set
     */
    public void setSubHeader(String subHeader) {
        this.subHeader = subHeader;
    }

    /**
     * @return the fncr_full_add
     */
    public String getFncr_full_add() {
        return fncr_full_add;
    }

    /**
     * @param fncr_full_add the fncr_full_add to set
     */
    public void setFncr_full_add(String fncr_full_add) {
        this.fncr_full_add = fncr_full_add;
    }

    /**
     * @return the ishypothecated
     */
    public boolean isIshypothecated() {
        return ishypothecated;
    }

    /**
     * @param ishypothecated the ishypothecated to set
     */
    public void setIshypothecated(boolean ishypothecated) {
        this.ishypothecated = ishypothecated;
    }

    /**
     * @return the noc_no
     */
    public String getNoc_no() {
        return noc_no;
    }

    /**
     * @param noc_no the noc_no to set
     */
    public void setNoc_no(String noc_no) {
        this.noc_no = noc_no;
    }

    /**
     * @return the noc_cc_lable
     */
    public String getNoc_cc_lable() {
        return noc_cc_lable;
    }

    /**
     * @param noc_cc_lable the noc_cc_lable to set
     */
    public void setNoc_cc_lable(String noc_cc_lable) {
        this.noc_cc_lable = noc_cc_lable;
    }

    /**
     * @return the noc_cc_fieldlable
     */
    public String getNoc_cc_fieldlable() {
        return noc_cc_fieldlable;
    }

    /**
     * @param noc_cc_fieldlable the noc_cc_fieldlable to set
     */
    public void setNoc_cc_fieldlable(String noc_cc_fieldlable) {
        this.noc_cc_fieldlable = noc_cc_fieldlable;
    }

    public String getNew_own_name() {
        return new_own_name;
    }

    public void setNew_own_name(String new_own_name) {
        this.new_own_name = new_own_name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public boolean isRender_Descr() {
        return render_Descr;
    }

    public void setRender_Descr(boolean render_Descr) {
        this.render_Descr = render_Descr;
    }

    public boolean isRender_newName() {
        return render_newName;
    }

    public void setRender_newName(boolean render_newName) {
        this.render_newName = render_newName;
    }

    /**
     * @return the userSign
     */
    public byte[] getUserSign() {
        return userSign;
    }

    /**
     * @param userSign the userSign to set
     */
    public void setUserSign(byte[] userSign) {
        this.userSign = userSign;
    }

    /**
     * @return the isUserSignExist
     */
    public boolean isIsUserSignExist() {
        return isUserSignExist;
    }

    /**
     * @param isUserSignExist the isUserSignExist to set
     */
    public void setIsUserSignExist(boolean isUserSignExist) {
        this.isUserSignExist = isUserSignExist;
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
     * @return the print_no_dues_cert
     */
    public boolean isPrint_no_dues_cert() {
        return print_no_dues_cert;
    }

    /**
     * @param print_no_dues_cert the print_no_dues_cert to set
     */
    public void setPrint_no_dues_cert(boolean print_no_dues_cert) {
        this.print_no_dues_cert = print_no_dues_cert;
    }
}
