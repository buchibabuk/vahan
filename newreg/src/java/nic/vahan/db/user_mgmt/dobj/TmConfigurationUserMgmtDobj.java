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
public class TmConfigurationUserMgmtDobj implements Serializable {

    private String stateCd;
    private String restrict_login_user_catg;
    private boolean ip_config_by_state_admin;
//    private boolean rcpt_cancel_with_otp;
//    private boolean appl_dispose_with_otp;
//    private boolean owner_mobile_verify_with_otp ;
//    private boolean change_veh_office_with_otp ;
//    private boolean delete_smartcard_flatfile_withOtp;

    /**
     * @return the appl_dispose_with_otp
     */
//    public boolean isAppl_dispose_with_otp() {
//        return appl_dispose_with_otp;
//    }
//
//    /**
//     * @param appl_dispose_with_otp the appl_dispose_with_otp to set
//     */
//    public void setAppl_dispose_with_otp(boolean appl_dispose_with_otp) {
//        this.appl_dispose_with_otp = appl_dispose_with_otp;
//    }
//
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
//
//    /**
//     * @return the rcpt_cancel_with_otp
//     */
//    public boolean isRcpt_cancel_with_otp() {
//        return rcpt_cancel_with_otp;
//    }
//
//    /**
//     * @param rcpt_cancel_with_otp the rcpt_cancel_with_otp to set
//     */
//    public void setRcpt_cancel_with_otp(boolean rcpt_cancel_with_otp) {
//        this.rcpt_cancel_with_otp = rcpt_cancel_with_otp;
//    }
//
//    /**
//     * @return the owner_mobile_verify_with_otp
//     */
//    public boolean isOwner_mobile_verify_with_otp() {
//        return owner_mobile_verify_with_otp;
//    }
//
//    /**
//     * @param owner_mobile_verify_with_otp the owner_mobile_verify_with_otp to set
//     */
//    public void setOwner_mobile_verify_with_otp(boolean owner_mobile_verify_with_otp) {
//        this.owner_mobile_verify_with_otp = owner_mobile_verify_with_otp;
//    }
//
//    /**
//     * @return the change_veh_office_with_otp
//     */
//    public boolean isChange_veh_office_with_otp() {
//        return change_veh_office_with_otp;
//    }
//
//    /**
//     * @param change_veh_office_with_otp the change_veh_office_with_otp to set
//     */
//    public void setChange_veh_office_with_otp(boolean change_veh_office_with_otp) {
//        this.change_veh_office_with_otp = change_veh_office_with_otp;
//    }
//
//    /**
//     * @return the delete_smartcard_flatfile_withOtp
//     */
//    public boolean isDelete_smartcard_flatfile_withOtp() {
//        return delete_smartcard_flatfile_withOtp;
//    }
//
//    /**
//     * @param delete_smartcard_flatfile_withOtp the delete_smartcard_flatfile_withOtp to set
//     */
//    public void setDelete_smartcard_flatfile_withOtp(boolean delete_smartcard_flatfile_withOtp) {
//        this.delete_smartcard_flatfile_withOtp = delete_smartcard_flatfile_withOtp;
//    }

    /**
     * @return the restrict_login_user_catg
     */
    public String getRestrict_login_user_catg() {
        return restrict_login_user_catg;
    }

    /**
     * @param restrict_login_user_catg the restrict_login_user_catg to set
     */
    public void setRestrict_login_user_catg(String restrict_login_user_catg) {
        this.restrict_login_user_catg = restrict_login_user_catg;
    }

    /**
     * @return the ip_config_by_state_admin
     */
    public boolean isIp_config_by_state_admin() {
        return ip_config_by_state_admin;
    }

    /**
     * @param ip_config_by_state_admin the ip_config_by_state_admin to set
     */
    public void setIp_config_by_state_admin(boolean ip_config_by_state_admin) {
        this.ip_config_by_state_admin = ip_config_by_state_admin;
    }

}
