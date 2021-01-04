/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.UserBlockUnblockdobj;
import org.apache.log4j.Logger;

/**
 *
 * @author acer
 */
public class UserBlockUnblockImpl {

    private static final Logger LOGGER = Logger.getLogger(UserBlockUnblockImpl.class);

    public static List<UserBlockUnblockdobj> getUserUnBlockedList(String stateCd, int offCd, String user_catg, String dealer_cd) throws VahanException {
        List listUser = new ArrayList();
        TransactionManagerReadOnly tmgr = null;
        UserBlockUnblockdobj dobj = null;
        String sql;
        try {
            tmgr = new TransactionManagerReadOnly("getUserUnBlockList");
            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN)) {
                sql = " 	select p.user_cd, user_name from " + TableList.TM_USER_INFO + " p "
                        + " inner join " + TableList.TM_USER_PERMISSIONS + " o on p.state_cd=o.state_cd and p.user_cd= o.user_cd "
                        + " where p.state_cd=? and p.off_cd=? and o.dealer_cd = ? and user_catg = 'B' "
                        + " and   p.user_cd not in (select s.user_cd :: numeric from " + TableList.TM_USER_STATUS + " s "
                        + " inner join " + TableList.TM_USER_PERMISSIONS + " permissions on s.state_cd=permissions.state_cd and s.user_cd::numeric= permissions.user_cd and dealer_cd = ? "
                        + " where s.state_cd=? and s.off_cd=?) ";
                PreparedStatement ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, offCd);
                ps.setString(3, dealer_cd);
                ps.setString(4, dealer_cd);
                ps.setString(5, stateCd);
                ps.setInt(6, offCd);
            } else {
                sql = " select user_cd, user_name from " + TableList.TM_USER_INFO + " where state_cd=? and off_cd=? and user_catg NOT IN ('A','S','D','B','P','F','C') "
                        + " AND user_cd not in (select user_cd :: numeric from " + TableList.TM_USER_STATUS + " where state_cd=? and off_cd=? )";

                PreparedStatement ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, offCd);
                ps.setString(3, stateCd);
                ps.setInt(4, offCd);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UserBlockUnblockdobj();
                dobj.setUser_cd(rs.getInt("user_cd"));
                dobj.setUser_name(rs.getString("user_name"));
                listUser.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return listUser;
    }

    public static List<UserBlockUnblockdobj> getUserBlockedList(String stateCd, int offCd, String user_catg, String dealer_cd) throws VahanException {
        List userBlocklist = new ArrayList();
        String sql;
        TransactionManagerReadOnly tmgr = null;
        UserBlockUnblockdobj dobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("getUserBlockList");
            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN)) {
                sql = " select p.user_cd, user_name,reason from " + TableList.TM_USER_INFO + " p "
                        + " inner join " + TableList.TM_USER_PERMISSIONS + " o on p.state_cd=o.state_cd and p.user_cd= o.user_cd "
                        + " inner join " + TableList.TM_USER_STATUS + " s on "
                        + " p.state_cd=s.state_cd and o.user_cd :: TEXT= s.user_cd  where p.state_cd=? and p.off_cd=? and o.dealer_cd = ?and user_catg = 'B' ";
                PreparedStatement ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, offCd);
                ps.setString(3, dealer_cd);
            } else {
                sql = "select user_st.*, info.user_name from " + TableList.TM_USER_STATUS + " user_st\n"
                        + " inner join " + TableList.TM_USER_INFO + " info on info.user_cd=user_st.user_cd :: numeric and info.state_cd=user_st.state_cd and info.off_cd=user_st.off_cd\n"
                        + " where user_st.state_cd=? and user_st.off_cd=?  ";

                PreparedStatement ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, offCd);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UserBlockUnblockdobj();
                dobj.setUser_cd(rs.getInt("user_cd"));
                dobj.setUser_name(rs.getString("user_name"));
                dobj.setReason(rs.getString("reason"));
                dobj.setOff_cd(offCd);
                userBlocklist.add(dobj);
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return userBlocklist;
    }

    public int blockUser(List<UserBlockUnblockdobj> userList) throws VahanException, Exception {
        PreparedStatement psInsert = null;
        TransactionManager tmgr = null;
        int count = 0;
        try {
            tmgr = new TransactionManager("insert Block User Details");
            for (UserBlockUnblockdobj user : userList) {
                if (user.isBlockUnBlockStatus()) {
                    String insertSql = "INSERT INTO " + TableList.TM_USER_STATUS
                            + "(state_cd, off_cd, user_cd, reason, status, blocked_by, blocked_on)\n"
                            + "VALUES (?, ?, ?, ?, ?, ?, current_timestamp)";

                    psInsert = tmgr.prepareStatement(insertSql);
                    int i = 1;
                    psInsert.setString(i++, Util.getUserStateCode());
                    psInsert.setInt(i++, Util.getUserOffCode());
                    psInsert.setInt(i++, user.getUser_cd());
                    if (user.getReason() != null && !user.getReason().isEmpty()) {
                        psInsert.setString(i++, user.getReason());
                    } else {
                        throw new VahanException("Reason Can't be empty for the selected user");
                    }
                    psInsert.setString(i++, "B");
                    psInsert.setString(i++, Util.getEmpCode());
                    psInsert.executeUpdate();
                    count++;
                }
            }
            if (count > 0) {
                tmgr.commit();
            }
        } catch (VahanException vme) {
            throw vme;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return count;
    }

    public int unBlockUser(List<UserBlockUnblockdobj> userList) throws Exception {
        PreparedStatement psInsertHistory = null;
        PreparedStatement psDelete = null;
        TransactionManager tmgr = null;
        int count = 0;
        try {
            tmgr = new TransactionManager("insert Block User Details");
            for (UserBlockUnblockdobj user : userList) {
                if (user.isBlockUnBlockStatus()) {
                    String insertHisSql = "INSERT INTO " + TableList.THM_USER_STATUS + " SELECT current_timestamp as moved_on,? as moved_by,state_cd, off_cd, user_cd, reason, status, blocked_by, blocked_on "
                            + " FROM " + TableList.TM_USER_STATUS + " where state_cd = ? and off_cd = ? and user_cd::NUMERIC = ? ";

                    psInsertHistory = tmgr.prepareStatement(insertHisSql);
                    psInsertHistory.setString(1, Util.getEmpCode());
                    psInsertHistory.setString(2, Util.getUserStateCode());
                    psInsertHistory.setInt(3, user.getOff_cd());
                    psInsertHistory.setInt(4, user.getUser_cd());
                    psInsertHistory.executeUpdate();

                    String deleteSql = " delete from " + TableList.TM_USER_STATUS + " where state_cd = ? and off_cd = ? and user_cd::NUMERIC = ? ";
                    psDelete = tmgr.prepareStatement(deleteSql);
                    psDelete.setString(1, Util.getUserStateCode());
                    psDelete.setInt(2, user.getOff_cd());
                    psDelete.setInt(3, user.getUser_cd());
                    psDelete.executeUpdate();
                    count++;
                }
            }
            if (count > 0) {
                tmgr.commit();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw e;
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return count;
    }
}
