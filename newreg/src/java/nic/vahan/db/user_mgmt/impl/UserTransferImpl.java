/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.db.user_mgmt.dobj.UserTransferDobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC103
 */
public class UserTransferImpl {

    private static final Logger LOGGER = Logger.getLogger(UserTransferImpl.class);

    public static List fillDataTable(UserTransferDobj dobj) throws VahanException {
        List<UserTransferDobj> userList = new ArrayList<>();
        TransactionManager tmgr = null;
        String[] officeArray = null;
        UserTransferDobj userDobj = null;
        try {
            tmgr = new TransactionManager("fillDataTable");
            PreparedStatement ps;
            RowSet rs;
            String sql = "select assigned_office from " + TableList.TM_USER_PERMISSIONS + "  where user_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, dobj.getUserCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                String office = rs.getString("assigned_office");
                officeArray = office.split(",");
            }
            for (int i = 0; i < officeArray.length; i++) {
                int offc = Integer.parseInt(officeArray[i]);
                sql = " Select b.user_cd,b.user_name,b.user_id,desig_name from " + TableList.TM_USER_INFO + " b \n"
                        + "left outer join " + TableList.TM_DESIGNATION + " d on b.desig_cd = d.desig_cd "
                        + "where b.user_cd IN (Select user_cd from " + TableList.TM_USER_PERMISSIONS + " a "
                        + "where state_cd = ? and a.assigned_office!='ANY' and ? = ANY(string_to_array(a.assigned_office,',')::numeric[]) order by user_cd)\n"
                        + "	and b.user_catg IN ('L','Y','X','D','Z') order by 2";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getStateCode());
                ps.setInt(2, offc);
                rs = tmgr.fetchDetachedRowSet();
                while (rs.next()) {
                    userDobj = new UserTransferDobj();
                    userDobj.setUserCode(rs.getLong("user_cd"));
                    userDobj.setUserID(rs.getString("user_id"));
                    userDobj.setUserName(rs.getString("user_name"));
                    userDobj.setDesignation(rs.getString("desig_name"));
                    userList.add(userDobj);
                }
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
                    throw new VahanException(e.getMessage());
                }
            }
        }
        return userList;
    }

    public static List fillTransferDataTable(UserTransferDobj dobj) throws VahanException {
        List<UserTransferDobj> userTransferList = new ArrayList<>();
        TransactionManager tmgr = null;
        UserTransferDobj userDobj = null;
        try {
            tmgr = new TransactionManager("fillDataTable");
            PreparedStatement ps;
            RowSet rs;
            String sql = "select distinct a.state_cd,c.off_cd,user_id,user_name,a.user_cd,a.assigned_office as old_office, b.assigned_office as new_office \n"
                    + "from thm_user_permissions a\n"
                    + "left outer join tm_user_permissions b on b.user_cd=a.user_cd and b.state_cd=a.state_cd \n"
                    + "left outer join tm_user_info c on c.user_cd=a.user_cd and c.state_cd=a.state_cd \n"
                    + "where a.state_cd=? and a.assigned_office=? \n"
                    + "and a.assigned_office<> b.assigned_office \n"
                    + "and (a.user_cd,a.assigned_office) in (select user_cd,assigned_office from thm_user_permissions a1 where a1.user_cd=a.user_cd and a1.state_cd=a.state_cd order by op_dt desc limit 1)\n"
                    + "order by 1,2,3,4";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getStateCode());
            ps.setString(2, String.valueOf(dobj.getOffCode()));
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                userDobj = new UserTransferDobj();
                userDobj.setUserID(rs.getString("user_id"));
                userDobj.setUserName(rs.getString("user_name"));
                userDobj.setOffCode(rs.getInt("new_office"));
                userDobj.setOldOffCode(rs.getInt("old_office"));
                userDobj.setStateCode(rs.getString("state_cd"));
                userTransferList.add(userDobj);
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
                    throw new VahanException(e.getMessage());
                }
            }
        }
        return userTransferList;
    }

    public UserTransferDobj getUserDetails(long userCode) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        UserTransferDobj userInfoObj = null;
        try {
            tmgr = new TransactionManager("getUserDetails");
            String query = "SELECT a.*,desig_name,assigned_office,descr from " + TableList.TM_USER_INFO + " a inner "
                    + "join " + TableList.TM_USER_PERMISSIONS + " b on a.user_cd = b.user_cd \n"
                    + "inner join " + TableList.TM_DESIGNATION + " c on a.desig_cd = c.desig_cd "
                    + "inner join " + TableList.TM_USER_CATG + " d on d.code = a.user_catg  where a.user_cd = ?";
            ps = tmgr.prepareStatement(query);
            ps.setLong(1, userCode);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                userInfoObj = new UserTransferDobj();
                userInfoObj.setUserName(rs.getString("user_name"));
                userInfoObj.setUserID(rs.getString("user_id"));
                userInfoObj.setEmailID(rs.getString("email_id"));
                userInfoObj.setAdhaarNo(rs.getString("aadhaar"));
                userInfoObj.setMobileNo(rs.getLong("mobile_no"));
                userInfoObj.setDesignation(rs.getString("desig_name"));
                userInfoObj.setUserCatg(rs.getString("descr"));
                userInfoObj.setOfficePh(rs.getString("phone_off"));
                userInfoObj.setOffCode(Integer.parseInt(rs.getString("assigned_office")));
                userInfoObj.setOldOffCode(Integer.parseInt(rs.getString("assigned_office")));
                userInfoObj.setUserCode(userCode);
                userInfoObj.setStateCode(rs.getString("state_cd"));
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
                    LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
                }
            }
        }
        return userInfoObj;
    }

    public boolean transferUser(UserTransferDobj dobj) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        boolean userTransferFlag = false;
        String query;
        try {
            tmgr = new TransactionManager("transferUser");
            //varifyPendingAppls(tmgr, dobj);
            actionRemoval(dobj, tmgr);
            query = "INSERT INTO " + TableList.THM_USER_INFO + "  \n"
                    + "SELECT state_cd, off_cd, user_cd, user_name, desig_cd, user_id,user_pwd, phone_off, mobile_no, email_id, user_catg, status, created_by, created_dt,aadhaar,current_timestamp as moved_on,? as moved_by, op_dt \n"
                    + "  FROM " + TableList.TM_USER_INFO + " where user_cd=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getEmpCode());
            ps.setLong(2, dobj.getUserCode());
            ps.executeUpdate();

            query = "Update " + TableList.TM_USER_INFO + " Set off_cd = ?, op_dt = current_timestamp Where user_cd = ?";
            ps = tmgr.prepareStatement(query);
            ps.setInt(1, dobj.getOffCode());
            ps.setLong(2, dobj.getUserCode());
            ps.executeUpdate();

            query = "INSERT INTO " + TableList.THM_USER_PERMISSIONS + "  "
                    + "Select current_timestamp as moved_on,? as moved_by,state_cd,user_cd, lower_range_no, upper_range_no, class_type, vch_class, "
                    + " pmt_type, pmt_catg, dealer_cd, maker, assigned_office ,all_Office_Auth,op_dt "
                    + "  FROM " + TableList.TM_USER_PERMISSIONS + " where user_cd=?";

            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getEmpCode());
            ps.setLong(2, dobj.getUserCode());
            ps.executeUpdate();

            query = "Update " + TableList.TM_USER_PERMISSIONS + " SET assigned_office = ?,op_dt = current_timestamp where user_cd = ?";
            ps = tmgr.prepareStatement(query);
            ps.setInt(1, dobj.getOffCode());
            ps.setLong(2, dobj.getUserCode());
            if (ps.executeUpdate() > 0) {
                userTransferFlag = true;
            }

            updateVmFitOfficer(tmgr, dobj);
            updateVmEnforcementOfficer(tmgr, dobj);
            updateUserCashCounter(tmgr, dobj);

            tmgr.commit();
        } catch (VahanException vme) {
            throw vme;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
        }
        return userTransferFlag;
    }

    private void actionRemoval(UserTransferDobj dobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps;
        try {
            removedActionHistory(dobj, tmgr);
            String query = "Delete From " + TableList.TM_OFF_EMP_ACTION + " where state_cd = ? and off_cd = ? and user_cd = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, dobj.getStateCode());
            ps.setInt(2, dobj.getOldOffCode());
            ps.setLong(3, dobj.getUserCode());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

    }

    private void removedActionHistory(UserTransferDobj dobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps;
        try {
            String query = "INSERT INTO " + TableList.THM_OFF_EMP_ACTION + "(\n"
                    + "            state_cd, off_cd, user_cd, action_cd, entered_by, entered_on, \n"
                    + "            moved_on, moved_by)\n"
                    + "    SELECT state_cd, off_cd, user_cd, action_cd, entered_by, entered_on,current_timestamp as moved_on,? as moved_by "
                    + " FROM " + TableList.TM_OFF_EMP_ACTION + " WHERE state_cd = ? and off_cd = ? and user_cd = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, dobj.getStateCode());
            ps.setInt(3, dobj.getOldOffCode());
            ps.setLong(4, dobj.getUserCode());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    private void updateVmFitOfficer(TransactionManager tmgr, UserTransferDobj dobj) throws SQLException {
        PreparedStatement ps;
        RowSet rs;
        String sql = "Select * from " + TableList.VM_FIT_OFFICER + " where user_cd = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setLong(1, dobj.getUserCode());
        rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            sql = "Update " + TableList.VM_FIT_OFFICER + " Set off_cd = ? Where user_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, dobj.getOffCode());
            ps.setLong(2, dobj.getUserCode());
            ps.executeUpdate();
        }
    }

    private void updateVmEnforcementOfficer(TransactionManager tmgr, UserTransferDobj dobj) throws SQLException {
        PreparedStatement ps;
        RowSet rs;
        String sql = "Select * from " + TableList.VM_ENFORCEMENT_OFFICER + " where user_cd = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setLong(1, dobj.getUserCode());
        rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            sql = "Update " + TableList.VM_ENFORCEMENT_OFFICER + " Set off_cd = ? Where user_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, dobj.getOffCode());
            ps.setLong(2, dobj.getUserCode());
            ps.executeUpdate();
        }
    }

    private void updateUserCashCounter(TransactionManager tmgr, UserTransferDobj dobj) throws SQLException {
        PreparedStatement ps;
        RowSet rs;
        String sql = "Select * from " + TableList.TM_USER_OPEN_CASH + " where user_cd = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setLong(1, dobj.getUserCode());
        rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            sql = "Update " + TableList.TM_USER_OPEN_CASH + " Set off_cd = ? where user_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, dobj.getOffCode());
            ps.setLong(2, dobj.getUserCode());
            ps.executeUpdate();
        }
    }

    // For check user is block or not
    public boolean checkBlockUser(UserTransferDobj dobj) throws VahanException {
        boolean flag = false;
        PreparedStatement ps;
        RowSet rs;
        TransactionManagerReadOnly tmgr = null;
        String sql;
        try {
            tmgr = new TransactionManagerReadOnly("checkBlockUser");
            sql = "select user_cd from " + TableList.TM_USER_STATUS + " where user_cd=? and status = 'B'";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Long.toString(dobj.getUserCode()));
            rs = tmgr.fetchDetachedRowSet();
            flag = rs.next();
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
}
