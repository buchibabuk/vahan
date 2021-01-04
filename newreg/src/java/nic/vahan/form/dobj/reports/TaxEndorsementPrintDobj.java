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
public class TaxEndorsementPrintDobj implements Serializable {

    private String regn_no;
    private String appl_no;
    private String vch_class_desc;
    private String tax_rate;
    private String noofqtr;
    private String with_effect_from;
    private String endorsement_date;
    private String header;
    private String subHeader;
    private String printDate;
    private String off_name;
    private String image_background;
    private String image_logo;
    private boolean show_image_background;
    private boolean show_image_logo;
    private String qrtext;
    private boolean showRoadSafetySlogan;
    private VmRoadSafetySloganPrintDobj roadSafetySloganDobj = null;

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
     * @return the vch_class_desc
     */
    public String getVch_class_desc() {
        return vch_class_desc;
    }

    /**
     * @param vch_class_desc the vch_class_desc to set
     */
    public void setVch_class_desc(String vch_class_desc) {
        this.vch_class_desc = vch_class_desc;
    }

    /**
     * @return the tax_rate
     */
    public String getTax_rate() {
        return tax_rate;
    }

    /**
     * @param tax_rate the tax_rate to set
     */
    public void setTax_rate(String tax_rate) {
        this.tax_rate = tax_rate;
    }

    /**
     * @return the noofqtr
     */
    public String getNoofqtr() {
        return noofqtr;
    }

    /**
     * @param noofqtr the noofqtr to set
     */
    public void setNoofqtr(String noofqtr) {
        this.noofqtr = noofqtr;
    }

    /**
     * @return the with_effect_from
     */
    public String getWith_effect_from() {
        return with_effect_from;
    }

    /**
     * @param with_effect_from the with_effect_from to set
     */
    public void setWith_effect_from(String with_effect_from) {
        this.with_effect_from = with_effect_from;
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
     * @return the qrtext
     */
    public String getQrtext() {
        return qrtext;
    }

    /**
     * @param qrtext the qrtext to set
     */
    public void setQrtext(String qrtext) {
        this.qrtext = qrtext;
    }

    /**
     * @return the endorsement_date
     */
    public String getEndorsement_date() {
        return endorsement_date;
    }

    /**
     * @param endorsement_date the endorsement_date to set
     */
    public void setEndorsement_date(String endorsement_date) {
        this.endorsement_date = endorsement_date;
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
