/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.user_mgmt.dobj.ChangePwdDobj;
import static nic.vahan.form.impl.ForgetPasswordImpl.insertHistory;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author acer
 */
public class ForgetPasswordImpl {

    private static Logger LOGGER = Logger.getLogger(ForgetPasswordImpl.class);

    public static String getUserCd(String user_id) throws VahanException {
        String query;
        String user_cd = null;
        String state_cd = null;
        String off_cd = null;
        String userData = "";
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        try {
            tmgr = new TransactionManager("ForgetPasswordImpl:getUserCd");
            query = "SELECT user_cd, state_cd, off_cd from " + TableList.TM_USER_INFO + " where user_id = ? and (user_catg=?  OR user_catg=? OR user_catg=?)  limit 1";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, user_id);
            ps.setString(2, TableConstants.USER_CATG_STATE_ADMIN);
            ps.setString(3, TableConstants.USER_CATG_OFFICE_ADMIN);
            ps.setString(4, TableConstants.USER_CATG_DEALER_ADMIN);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                user_cd = rs.getString("user_cd");
                state_cd = rs.getString("state_cd");
                off_cd = rs.getString("off_cd");
                userData = user_cd + "`" + state_cd + "`" + off_cd;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return userData;
    }

    public static Boolean updateUserPassword(long user_cd, String password, String state_cd, int off_cd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String query = "";
        boolean status = false;
        try {
            tmgr = new TransactionManager("updateUserPassword");
            if (insertHistory(tmgr, user_cd)) {
                query = "update " + TableList.TM_USER_INFO + " set user_pwd=?, forget_password = 'Y' where user_cd=? and state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, password);
                ps.setLong(2, user_cd);
                ps.setString(3, state_cd);
                ps.setInt(4, off_cd);
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                deleteToken(tmgr, user_cd, state_cd, off_cd);
                tmgr.commit();
                status = true;
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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

    public static boolean insertHistory(TransactionManager tmgr, long user_cd) throws VahanException {
        boolean status = false;
        PreparedStatement ps = null;
        try {
            String sql1 = "INSERT INTO " + TableList.THM_USER_INFO + "  \n"
                    + "SELECT state_cd, off_cd, user_cd, user_name, desig_cd, user_id,user_pwd, phone_off, mobile_no, email_id, user_catg, status, created_by, created_dt,aadhaar,current_timestamp as moved_on,? as moved_by,op_dt \n"
                    + "  FROM " + TableList.TM_USER_INFO + " where user_cd=? ";
            ps = tmgr.prepareStatement(sql1);
            ps.setLong(1, user_cd);
            ps.setLong(2, user_cd);
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            status = true;
        } catch (SQLException e) {
            throw new VahanException("Error in insertHisotry" + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                throw new VahanException(e.getMessage());
            }
        }
        return status;
    }

    public static boolean insertToken(String stateCd, int off_cd, String user_cd, String uuid) throws VahanException {
        TransactionManager tmgr = null;
        boolean tokenstatus = false;
        PreparedStatement ps = null;
        try {
            String sql1 = " INSERT INTO " + TableList.TM_FORGETPASSWORD_TOKEN + " (state_cd,off_cd,user_cd,token_no,op_dt) "
                    + " values(?,?,?,?,current_timestamp) ";
            tmgr = new TransactionManager("insertToken");
            ps = tmgr.prepareStatement(sql1);
            ps.setString(1, stateCd);
            ps.setInt(2, off_cd);
            ps.setInt(3, Integer.parseInt(user_cd));
            ps.setString(4, uuid);
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            tmgr.commit();
            tokenstatus = true;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in insert Record");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
        }
        return tokenstatus;
    }

    public ChangePwdDobj getTokenDetail(String tokenId) throws Exception {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        RowSet rs = null;
        ChangePwdDobj dobj = null;
        long diffMin;
        try {
            tmgr = new TransactionManager("getTokenDetail");

            sql = "select i.state_cd,i.off_cd,i.user_cd,i.op_dt,o.user_id,now()::timestamp as currentdate from " + TableList.TM_FORGETPASSWORD_TOKEN + " i\n"
                    + " inner join tm_user_info o on i.state_cd=o.state_cd and i.off_cd=o.off_cd and i.user_cd=o.user_cd where i.token_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, tokenId);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (DateUtil.compareDates(rs.getDate("currentdate"), rs.getDate("op_dt")) != 0 || (rs.getDate("currentdate").getTime() - rs.getDate("op_dt").getTime()) / (60 * 1000) > 30) {
                    deleteToken(tmgr, rs.getLong("user_cd"), rs.getString("state_cd"), rs.getInt("off_cd"));
                    tmgr.commit();
                    throw new VahanException("Link is Expired");
                }
                dobj = new ChangePwdDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setUser_cd(rs.getLong("user_cd"));
                dobj.setUser_id(rs.getString("user_id"));
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj;
    }

    public static boolean deleteToken(TransactionManager tmgr, long user_cd, String state_cd, int off_cd) throws VahanException {
        boolean status = false;
        PreparedStatement ps = null;
        try {
            String query = "delete from " + TableList.TM_FORGETPASSWORD_TOKEN + " where user_cd=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(query);
            ps.setLong(1, user_cd);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            status = true;
        } catch (SQLException e) {
            throw new VahanException("Error in insertHisotry" + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                throw new VahanException(e.getMessage());
            }
        }
        return status;
    }

    public static String getOldPassword(long user_cd) throws VahanException {
        String user_oldpwd = "";
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs;

        try {
            tmgr = new TransactionManager("getOldPassword");
            String sql = "Select user_pwd from " + TableList.TM_USER_INFO + " where user_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, user_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                user_oldpwd = rs.getString("user_pwd");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return user_oldpwd;
    }
}
