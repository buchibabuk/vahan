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
public class TmConfigurationPrintDobj implements Serializable {

    private String state_cd;
    private String image_background;
    private String image_logo;
    private String temporary_rc_remarks;
    private boolean print_tax_token;
    private boolean print_no_dues_cert;
    private boolean road_safety_slogan;
    private String cash_rcpt_note;
    private int defaulterNoticeGracePeriod = 0;
    private int advance_receipt_validity_days;

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
     * @return the temporary_rc_remarks
     */
    public String getTemporary_rc_remarks() {
        return temporary_rc_remarks;
    }

    /**
     * @param temporary_rc_remarks the temporary_rc_remarks to set
     */
    public void setTemporary_rc_remarks(String temporary_rc_remarks) {
        this.temporary_rc_remarks = temporary_rc_remarks;
    }

    /**
     * @return the print_tax_token
     */
    public boolean isPrint_tax_token() {
        return print_tax_token;
    }

    /**
     * @param print_tax_token the print_tax_token to set
     */
    public void setPrint_tax_token(boolean print_tax_token) {
        this.print_tax_token = print_tax_token;
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

    /**
     * @return the road_safety_slogan
     */
    public boolean isRoad_safety_slogan() {
        return road_safety_slogan;
    }

    /**
     * @param road_safety_slogan the road_safety_slogan to set
     */
    public void setRoad_safety_slogan(boolean road_safety_slogan) {
        this.road_safety_slogan = road_safety_slogan;
    }

    /**
     * @return the cash_rcpt_note
     */
    public String getCash_rcpt_note() {
        return cash_rcpt_note;
    }

    /**
     * @param cash_rcpt_note the cash_rcpt_note to set
     */
    public void setCash_rcpt_note(String cash_rcpt_note) {
        this.cash_rcpt_note = cash_rcpt_note;
    }

    /**
     * @return the defaulterNoticeGracePeriod
     */
    public int getDefaulterNoticeGracePeriod() {
        return defaulterNoticeGracePeriod;
    }

    /**
     * @param defaulterNoticeGracePeriod the defaulterNoticeGracePeriod to set
     */
    public void setDefaulterNoticeGracePeriod(int defaulterNoticeGracePeriod) {
        this.defaulterNoticeGracePeriod = defaulterNoticeGracePeriod;
    }

    /**
     * @return the advance_receipt_validity_days
     */
    public int getAdvance_receipt_validity_days() {
        return advance_receipt_validity_days;
    }

    /**
     * @param advance_receipt_validity_days the advance_receipt_validity_days to
     * set
     */
    public void setAdvance_receipt_validity_days(int advance_receipt_validity_days) {
        this.advance_receipt_validity_days = advance_receipt_validity_days;
    }
}
