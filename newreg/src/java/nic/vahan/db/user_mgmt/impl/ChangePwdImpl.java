/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.user_mgmt.dobj.ChangePwdDobj;

/**
 *
 * @author tranC103
 */
public class ChangePwdImpl {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ChangePwdImpl.class);

    public String checkOrUpdatePassword(ChangePwdDobj pwdDobj) throws VahanException {
        boolean updateFlag = false;
        String status = "";
        PreparedStatement ps;
        TransactionManager tmgr = null;
        long user_cd;
        try {
            user_cd = pwdDobj.getUser_cd();
            tmgr = new TransactionManager("checkOrUpdatePassword");
            boolean flag = checkOldPassword(pwdDobj, tmgr);
            if (flag) {
                UserMgmtImpl.insertHistory(user_cd, tmgr);
//                UserMgmtImpl.updateSHAPwd(pwdDobj.getShaPwd(), tmgr);
                String sql = "Update " + TableList.TM_USER_INFO + " set user_pwd = ?, op_dt = current_timestamp, forget_password='N' ,newuser_change_password='F' where user_cd = ? and user_pwd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, pwdDobj.getNewPwd());
                ps.setLong(2, pwdDobj.getUser_cd());
                ps.setString(3, pwdDobj.getOldPwd());
                ps.executeUpdate();
                tmgr.commit();
                updateFlag = true;
                status = "Y";
            } else {
                status = "N";
            }
        } catch (SQLException e) {
            throw new VahanException("checkOrUpdatePassword" + e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                throw new VahanException("checkOrUpdatePassword" + e.getMessage());
            }
        }
        return status;
    }
//     public static String insertHistory(ChangePwdDobj pwdDobj, TransactionManager tmgr) throws VahanException {
//
//        try {
//            Date da = new Date();
//            PreparedStatement ps = null;
//            String sql1 = "INSERT INTO THM_USER_INFO  \n"
//                    + "SELECT state_cd, off_cd, user_cd, user_name, desig_cd, user_id,user_pwd, phone_off, mobile_no, email_id, user_catg, status, created_by, created_dt,current_timestamp as moved_on,? as moved_by \n"
//                    + "  FROM " + TableList.TM_USER_INFO + " where user_cd=?";
// 
//            ps = tmgr.prepareStatement(sql1);
//            ps.setString(1, Util.getEmpCode());
//            ps.setLong(2,  pwdDobj.getUser_cd());
//            ps.executeUpdate();
//        } catch (SQLException e) {
//            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//            throw new VahanException("Error in insertHisotry" + e.getMessage());
//        }
//        return "records inserted ";
//    }

    private boolean checkOldPassword(ChangePwdDobj pwdDobj, TransactionManager tmgr) {
        boolean checkFlag = false;
        PreparedStatement ps;
        RowSet rs;
        String sql = "Select user_pwd from " + TableList.TM_USER_INFO + " where user_pwd = ? and user_cd = ?";

        try {
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, pwdDobj.getOldPwd());
            ps.setLong(2, pwdDobj.getUser_cd());
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                checkFlag = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return checkFlag;
    }

    public static boolean checkUsedOldPassword(ChangePwdDobj pwdDobj) {
        boolean status = false;
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs;
        String sql;
        try {
            tmgr = new TransactionManager("checkUsedOldPassword");
            sql = "Select user_pwd from " + TableList.THM_USER_INFO + " where user_pwd = ? and user_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, pwdDobj.getNewPwd());
            ps.setLong(2, pwdDobj.getUser_cd());
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                status = true;
            }
        } catch (Exception ex) {
            LOGGER.error(new VahanException("Error in Select Password") + ex.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return status;
    }
}
