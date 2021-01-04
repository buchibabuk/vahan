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
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.user_mgmt.dobj.UpdateProfileDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC103
 */
public class UpdateProfileImpl {

    private static final Logger LOGGER = Logger.getLogger(UpdateProfileImpl.class);

    public UpdateProfileDobj getUserData(UpdateProfileDobj updateDobj) {
        long userCode = updateDobj.getUserCode();
        UpdateProfileDobj dobj = null;
        PreparedStatement ps;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql;
        try {
            tmgr = new TransactionManager("getUserData");
            sql = "Select tui.user_id,tui.user_name,tui.phone_off,tui.mobile_no,"
                    + " tui.email_id,tui.state_cd,tui.off_cd,tuv.mobile_verify,tuv.email_verify,tui.user_catg from "
                    + TableList.TM_USER_INFO + " tui "
                    + " left outer join " + TableList.TM_USER_DETAILS_VERIFICATION + " tuv on "
                    + " tuv.state_cd = tui.state_cd and tuv.off_cd = tui.off_cd "
                    + " and tui.user_cd = tuv.user_cd where tui.user_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, userCode);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UpdateProfileDobj();
                dobj.setUserCode(userCode);
                dobj.setUser_id(rs.getString("user_id"));
                dobj.setUserName(rs.getString("user_name"));
                dobj.setOffNo(rs.getString("phone_off"));
                dobj.setMobileNo(rs.getLong("mobile_no"));
                dobj.setEmailID(rs.getString("email_id"));
                dobj.setStateCode(rs.getString("state_cd"));
                dobj.setOffCode(rs.getInt("off_cd"));
                dobj.setMobile_verify(rs.getBoolean("mobile_verify"));
                dobj.setEmail_verify(rs.getBoolean("email_verify"));
                dobj.setUser_catg(rs.getString("user_catg"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return dobj;
    }

//   public static String insertUserHistory(UpdateProfileDobj dobj, TransactionManager tmgr) throws VahanException {
//
//        try {
//            Date da = new Date();
//            PreparedStatement ps = null;
//            String sql1 = "INSERT INTO " + TableList.THM_USER_INFO + "  \n"
//                    + "SELECT state_cd, off_cd, user_cd, user_name, desig_cd, user_id,user_pwd, phone_off, mobile_no, email_id, user_catg, status, created_by, created_dt,aadhaar,close_cash_dt,close_cash,current_timestamp as moved_on,? as moved_by \n"
//                    + "  FROM " + TableList.TM_USER_INFO + " where user_cd=?";
//
//            ps = tmgr.prepareStatement(sql1);
//            ps.setString(1, Util.getEmpCode());
//            ps.setLong(2, dobj.getUserCode());
//            ps.executeUpdate();
//        } catch (SQLException e) {
//            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//            throw new VahanException("Error in insertUserHisotry" + e.getMessage());
//        }
//        return "Records inserted ";
//    }
    public boolean updateUserData(UpdateProfileDobj updateDobj, String changeData, String verifyType, String dealerCode) throws VahanException {
        boolean flag = false;
        PreparedStatement ps;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql;
        try {
            tmgr = new TransactionManager("updateUserData");
            if (updateDobj.getUser_catg().equals(TableConstants.USER_CATG_DEALER_ADMIN)) {
                updateDealerAdminData(updateDobj, dealerCode, tmgr);
            }
            UserMgmtImpl.insertHistory(updateDobj.getUserCode(), tmgr);
            updateUserVerification(tmgr, updateDobj, verifyType);
            sql = "Update " + TableList.TM_USER_INFO + " set user_name=?,phone_off=?,mobile_no=?,email_id=?,op_dt = current_timestamp where user_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, updateDobj.getUserName());
            ps.setString(2, updateDobj.getOffNo());
            ps.setLong(3, updateDobj.getMobileNo());
            ps.setString(4, updateDobj.getEmailID());
            ps.setLong(5, updateDobj.getUserCode());

            int temp = ps.executeUpdate();
            if (temp > 0) {
                flag = true;
                tmgr.commit();
            }

        } catch (VahanException vex) {
            throw vex;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException(TableConstants.SomthingWentWrong);
                }
            }
        }
        return flag;
    }

    private void updateUserVerification(TransactionManager tmgr, UpdateProfileDobj updateDobj, String verifyType) throws SQLException {
        PreparedStatement ps;
        RowSet rs;
        String query = "Select user_cd from " + TableList.TM_USER_DETAILS_VERIFICATION + "  where user_cd = ?";
        ps = tmgr.prepareStatement(query);
        ps.setLong(1, updateDobj.getUserCode());
        rs = tmgr.fetchDetachedRowSet_No_release();
        String verifyStr = "mobile_verify";
        if ("email".equals(verifyType)) {
            verifyStr = "email_verify";
        }
        if (rs.next()) {
            query = "UPDATE " + TableList.TM_USER_DETAILS_VERIFICATION + "  SET " + verifyStr + " = ?,op_dt=current_timestamp WHERE user_cd = ?";
            ps = tmgr.prepareStatement(query);
            ps.setBoolean(1, true);
            ps.setLong(2, updateDobj.getUserCode());
            ps.executeUpdate();
        } else {
            query = "INSERT INTO " + TableList.TM_USER_DETAILS_VERIFICATION + " (state_cd, off_cd, user_cd, " + verifyStr + ", op_dt) VALUES (?, ?, ?, ?, current_timestamp)";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, updateDobj.getStateCode());
            ps.setInt(2, updateDobj.getOffCode());
            ps.setLong(3, updateDobj.getUserCode());
            ps.setBoolean(4, true);
            ps.executeUpdate();
        }

    }

    public boolean getMobileNoAndEmailId(String verifyType, long mobileNo, String emailId, String userId) throws VahanException {
        boolean flag = false;
        PreparedStatement ps;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql;
        String type = "mobile_no";
        try {
            tmgr = new TransactionManager("getMobileNoAndEmailId");
            if ("email".equals(verifyType)) {
                type = "email_id";
            }
            sql = " select case when (b::character varying ~ 'TRUE,') then true else false end as verify FROM ( "
                    + " SELECT array_to_string( ARRAY(select case when (case when 'mobileno' = 'mobileno' then v.mobile_verify else v.email_verify end ) "
                    + " then 'TRUE' else 'FALSE' end as isverify from  " + TableList.TM_USER_INFO + "  tu inner join " + TableList.TM_USER_DETAILS_VERIFICATION + " v "
                    + " on tu.user_cd = v.user_cd where " + type + " = ? and user_id != ? ), ', ') as a LIMIT 1) b ";

            ps = tmgr.prepareStatement(sql);
            if ("email".equals(verifyType)) {
                ps.setString(1, emailId);
            } else {
                ps.setLong(1, mobileNo);
            }
            ps.setString(2, userId);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = rs.getBoolean("verify");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException(TableConstants.SomthingWentWrong);
                }
            }
        }
        return flag;
    }
    // for check fitness officer

    public static Boolean IsFitOfficerOrNot(Long user_cd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String query = "";
        boolean status = false;
        RowSet rs;
        try {
            tmgr = new TransactionManager("updateUserPassword");
            query = "Select user_cd from " + TableList.VM_FIT_OFFICER + " where user_cd = ? ";
            ps = tmgr.prepareStatement(query);
            ps.setLong(1, user_cd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                status = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                throw new VahanException(e.getMessage());
            }
        }
        return status;
    }

    public static String updateDealerAdminData(UpdateProfileDobj updateDobj, String dealerCode, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps;
        RowSet rs;
        String sql;
        try {
            insertDealerUserHistory(updateDobj, dealerCode, tmgr);
            sql = " UPDATE " + TableList.VM_DEALER_MAST + "\n"
                    + "   SET email_id=?, contact_no=? \n"
                    + " WHERE dealer_cd=? and state_cd=? and off_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, updateDobj.getEmailID());
            ps.setLong(2, updateDobj.getMobileNo());
            ps.setString(3, dealerCode);
            ps.setString(4, updateDobj.getStateCode());
            ps.setInt(5, updateDobj.getOffCode());
            ps.executeUpdate();

        } catch (VahanException vex) {
            throw vex;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return "Records Updated ";
    }

    //For inserting updated user history 
    public static String insertDealerUserHistory(UpdateProfileDobj updateDobj, String dealerCode, TransactionManager tmgr) throws VahanException {
        try {
            PreparedStatement ps = null;
            String sql1 = "INSERT INTO " + TableList.VHM_DEALER_MAST + " \n"
                    + "SELECT dealer_cd, state_cd, off_cd, dealer_name, dealer_regn_no, d_add1, d_add2, d_district, d_pincode, d_state, valid_upto, entered_by, entered_on, tin_no,current_timestamp as moved_on,? as moved_by,regn_mark_gen_by_dealer \n"
                    + "  FROM " + TableList.VM_DEALER_MAST + " where dealer_cd=?";

            ps = tmgr.prepareStatement(sql1);
            ps.setLong(1, updateDobj.getUserCode());
            ps.setString(2, dealerCode);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new VahanException("Error in insertHisotry" + e.getMessage());
        }
        return "Records inserted ";
    }
}
