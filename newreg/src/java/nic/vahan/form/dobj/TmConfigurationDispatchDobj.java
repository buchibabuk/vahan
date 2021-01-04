/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author afzal
 */
public class TmConfigurationDispatchDobj implements Serializable {

    private String state_cd;
    private boolean is_speed_post_series;
    private boolean is_show_all_pending_records;
    private boolean is_envelop_print;
    private boolean is_sticker_print;
    private boolean is_search_by_regn_no;
    private boolean is_verify_for_hsrp;
    private boolean is_sendSMS_owner;
    private boolean is_barcode_mandatory;
    private boolean is_rcdispatch_userwise;
    private boolean is_rcdispatch_letter;
    private boolean is_edit_on_all_pending_records;
    private boolean is_rcdispatch_byhand;
    private boolean revert_rcdispatch_byhand;
    private String sms_dispatch_return;
    private boolean is_dispatch_address;
    private int by_hand_class_type;

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
     * @return the is_speed_post_series
     */
    public boolean isIs_speed_post_series() {
        return is_speed_post_series;
    }

    /**
     * @param is_speed_post_series the is_speed_post_series to set
     */
    public void setIs_speed_post_series(boolean is_speed_post_series) {
        this.is_speed_post_series = is_speed_post_series;
    }

    /**
     * @return the is_show_all_pending_records
     */
    public boolean isIs_show_all_pending_records() {
        return is_show_all_pending_records;
    }

    /**
     * @param is_show_all_pending_records the is_show_all_pending_records to set
     */
    public void setIs_show_all_pending_records(boolean is_show_all_pending_records) {
        this.is_show_all_pending_records = is_show_all_pending_records;
    }

    /**
     * @return the is_envelop_print
     */
    public boolean isIs_envelop_print() {
        return is_envelop_print;
    }

    /**
     * @param is_envelop_print the is_envelop_print to set
     */
    public void setIs_envelop_print(boolean is_envelop_print) {
        this.is_envelop_print = is_envelop_print;
    }

    /**
     * @return the is_sticker_print
     */
    public boolean isIs_sticker_print() {
        return is_sticker_print;
    }

    /**
     * @param is_sticker_print the is_sticker_print to set
     */
    public void setIs_sticker_print(boolean is_sticker_print) {
        this.is_sticker_print = is_sticker_print;
    }

    /**
     * @return the is_search_by_regn_no
     */
    public boolean isIs_search_by_regn_no() {
        return is_search_by_regn_no;
    }

    /**
     * @param is_search_by_regn_no the is_search_by_regn_no to set
     */
    public void setIs_search_by_regn_no(boolean is_search_by_regn_no) {
        this.is_search_by_regn_no = is_search_by_regn_no;
    }

    /**
     * @return the is_verify_for_hsrp
     */
    public boolean isIs_verify_for_hsrp() {
        return is_verify_for_hsrp;
    }

    /**
     * @param is_verify_for_hsrp the is_verify_for_hsrp to set
     */
    public void setIs_verify_for_hsrp(boolean is_verify_for_hsrp) {
        this.is_verify_for_hsrp = is_verify_for_hsrp;
    }

    /**
     * @return the is_sendSMS_owner
     */
    public boolean isIs_sendSMS_owner() {
        return is_sendSMS_owner;
    }

    /**
     * @param is_sendSMS_owner the is_sendSMS_owner to set
     */
    public void setIs_sendSMS_owner(boolean is_sendSMS_owner) {
        this.is_sendSMS_owner = is_sendSMS_owner;
    }

    /**
     * @return the is_barcode_mandatory
     */
    public boolean isIs_barcode_mandatory() {
        return is_barcode_mandatory;
    }

    /**
     * @param is_barcode_mandatory the is_barcode_mandatory to set
     */
    public void setIs_barcode_mandatory(boolean is_barcode_mandatory) {
        this.is_barcode_mandatory = is_barcode_mandatory;
    }

    /**
     * @return the is_rcdispatch_userwise
     */
    public boolean isIs_rcdispatch_userwise() {
        return is_rcdispatch_userwise;
    }

    /**
     * @param is_rcdispatch_userwise the is_rcdispatch_userwise to set
     */
    public void setIs_rcdispatch_userwise(boolean is_rcdispatch_userwise) {
        this.is_rcdispatch_userwise = is_rcdispatch_userwise;
    }

    /**
     * @return the is_rcdispatch_letter
     */
    public boolean isIs_rcdispatch_letter() {
        return is_rcdispatch_letter;
    }

    /**
     * @param is_rcdispatch_letter the is_rcdispatch_letter to set
     */
    public void setIs_rcdispatch_letter(boolean is_rcdispatch_letter) {
        this.is_rcdispatch_letter = is_rcdispatch_letter;
    }

    /**
     * @return the is_edit_on_all_pending_records
     */
    public boolean isIs_edit_on_all_pending_records() {
        return is_edit_on_all_pending_records;
    }

    /**
     * @param is_edit_on_all_pending_records the is_edit_on_all_pending_records
     * to set
     */
    public void setIs_edit_on_all_pending_records(boolean is_edit_on_all_pending_records) {
        this.is_edit_on_all_pending_records = is_edit_on_all_pending_records;
    }

    /**
     * @return the is_rcdispatch_byhand
     */
    public boolean isIs_rcdispatch_byhand() {
        return is_rcdispatch_byhand;
    }

    /**
     * @param is_rcdispatch_byhand the is_rcdispatch_byhand to set
     */
    public void setIs_rcdispatch_byhand(boolean is_rcdispatch_byhand) {
        this.is_rcdispatch_byhand = is_rcdispatch_byhand;
    }

    /**
     * @return the revert_rcdispatch_byhand
     */
    public boolean isRevert_rcdispatch_byhand() {
        return revert_rcdispatch_byhand;
    }

    /**
     * @param revert_rcdispatch_byhand the revert_rcdispatch_byhand to set
     */
    public void setRevert_rcdispatch_byhand(boolean revert_rcdispatch_byhand) {
        this.revert_rcdispatch_byhand = revert_rcdispatch_byhand;
    }

    /**
     * @return the sms_dispatch_return
     */
    public String getSms_dispatch_return() {
        return sms_dispatch_return;
    }

    /**
     * @param sms_dispatch_return the sms_dispatch_return to set
     */
    public void setSms_dispatch_return(String sms_dispatch_return) {
        this.sms_dispatch_return = sms_dispatch_return;
    }

    /**
     * @return the is_dispatch_address
     */
    public boolean isIs_dispatch_address() {
        return is_dispatch_address;
    }

    /**
     * @param is_dispatch_address the is_dispatch_address to set
     */
    public void setIs_dispatch_address(boolean is_dispatch_address) {
        this.is_dispatch_address = is_dispatch_address;
    }

    /**
     * @return the by_hand_class_type
     */
    public int getBy_hand_class_type() {
        return by_hand_class_type;
    }

    /**
     * @param by_hand_class_type the by_hand_class_type to set
     */
    public void setBy_hand_class_type(int by_hand_class_type) {
        this.by_hand_class_type = by_hand_class_type;
    }
}
