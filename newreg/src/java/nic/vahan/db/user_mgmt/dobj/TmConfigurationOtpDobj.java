/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.dobj;

import java.io.Serializable;

/**
 *
 * @author acer
 */
public class TmConfigurationOtpDobj implements Serializable {

    private String stateCd;
    private boolean rcpt_cancel_with_otp;
    private boolean appl_dispose_with_otp;
    private boolean owner_mobile_verify_with_otp;
    private boolean change_veh_office_with_otp;
    private boolean delete_smartcard_flatfile_withOtp;
    private boolean add_modify_office_with_otp;
    private boolean modifyLTTorOTTwithotp;
    private boolean restoreDisposeApplicationOtp;
    private boolean mobile_no_count_with_otp;
    private boolean deleteModifyRefundWithOtp;

    /**
     * @return the appl_dispose_with_otp
     */
    public boolean isAppl_dispose_with_otp() {
        return appl_dispose_with_otp;
    }

    /**
     * @param appl_dispose_with_otp the appl_dispose_with_otp to set
     */
    public void setAppl_dispose_with_otp(boolean appl_dispose_with_otp) {
        this.appl_dispose_with_otp = appl_dispose_with_otp;
    }

    /**
     * @return the stateCd
     */
    public String getStateCd() {
        return stateCd;
    }

    /**
     * @param stateCd the stateCd to set
     */
    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    /**
     * @return the rcpt_cancel_with_otp
     */
    public boolean isRcpt_cancel_with_otp() {
        return rcpt_cancel_with_otp;
    }

    /**
     * @param rcpt_cancel_with_otp the rcpt_cancel_with_otp to set
     */
    public void setRcpt_cancel_with_otp(boolean rcpt_cancel_with_otp) {
        this.rcpt_cancel_with_otp = rcpt_cancel_with_otp;
    }

    /**
     * @return the owner_mobile_verify_with_otp
     */
    public boolean isOwner_mobile_verify_with_otp() {
        return owner_mobile_verify_with_otp;
    }

    /**
     * @param owner_mobile_verify_with_otp the owner_mobile_verify_with_otp to
     * set
     */
    public void setOwner_mobile_verify_with_otp(boolean owner_mobile_verify_with_otp) {
        this.owner_mobile_verify_with_otp = owner_mobile_verify_with_otp;
    }

    /**
     * @return the change_veh_office_with_otp
     */
    public boolean isChange_veh_office_with_otp() {
        return change_veh_office_with_otp;
    }

    /**
     * @param change_veh_office_with_otp the change_veh_office_with_otp to set
     */
    public void setChange_veh_office_with_otp(boolean change_veh_office_with_otp) {
        this.change_veh_office_with_otp = change_veh_office_with_otp;
    }

    /**
     * @return the delete_smartcard_flatfile_withOtp
     */
    public boolean isDelete_smartcard_flatfile_withOtp() {
        return delete_smartcard_flatfile_withOtp;
    }

    /**
     * @param delete_smartcard_flatfile_withOtp the
     * delete_smartcard_flatfile_withOtp to set
     */
    public void setDelete_smartcard_flatfile_withOtp(boolean delete_smartcard_flatfile_withOtp) {
        this.delete_smartcard_flatfile_withOtp = delete_smartcard_flatfile_withOtp;
    }

    /**
     * @return the add_modify_office_with_otp
     */
    public boolean isAdd_modify_office_with_otp() {
        return add_modify_office_with_otp;
    }

    /**
     * @param add_modify_office_with_otp the add_modify_office_with_otp to set
     */
    public void setAdd_modify_office_with_otp(boolean add_modify_office_with_otp) {
        this.add_modify_office_with_otp = add_modify_office_with_otp;
    }

    public boolean isModifyLTTorOTTwithotp() {
        return modifyLTTorOTTwithotp;
    }

    public void setModifyLTTorOTTwithotp(boolean modifyLTTorOTTwithotp) {
        this.modifyLTTorOTTwithotp = modifyLTTorOTTwithotp;
    }

    public boolean isRestoreDisposeApplicationOtp() {
        return restoreDisposeApplicationOtp;
    }

    public void setRestoreDisposeApplicationOtp(boolean restoreDisposeApplicationOtp) {
        this.restoreDisposeApplicationOtp = restoreDisposeApplicationOtp;
    }

    public boolean isMobile_no_count_with_otp() {
        return mobile_no_count_with_otp;
    }

    public void setMobile_no_count_with_otp(boolean mobile_no_count_with_otp) {
        this.mobile_no_count_with_otp = mobile_no_count_with_otp;
    }

    public boolean isDeleteModifyRefundWithOtp() {
        return deleteModifyRefundWithOtp;
    }

    public void setDeleteModifyRefundWithOtp(boolean deleteModifyRefundWithOtp) {
        this.deleteModifyRefundWithOtp = deleteModifyRefundWithOtp;
    }
    
}
