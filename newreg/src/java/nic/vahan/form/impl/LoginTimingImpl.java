/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.LoginTimingDobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author acer
 */
public class LoginTimingImpl {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(LoginTimingImpl.class);
    private static SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");

    public static LoginTimingDobj getDataFromDataTable(int off_cd, String state_cd) throws VahanException {

        PreparedStatement ps = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;

        String sql = "Select state_cd, off_cd, (current_date::text||' '||open_time)::timestamp as open_time,"
                + " (current_date::text||' '||close_time)::timestamp as close_time"
                + " from " + TableList.TM_CONFIGURATION_LOGIN + " where state_cd=? and off_cd=?";
        LoginTimingDobj logintimeDobj = null;
        try {
            tmgr = new TransactionManager("getDataFromDataTable");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                logintimeDobj = new LoginTimingDobj();
                logintimeDobj.setState_cd(state_cd);
                logintimeDobj.setOff_cd(off_cd);
                logintimeDobj.setOpen_timing(rs.getTimestamp("open_time"));
                logintimeDobj.setClose_timing(rs.getTimestamp("close_time"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return logintimeDobj;
    }

    public static String insertDataintoDataTable(LoginTimingDobj dobj) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String msg = null;
        String sql = "INSERT INTO  " + TableList.TM_CONFIGURATION_LOGIN + " ("
                + "            state_cd, off_cd, open_time, close_time, entered_by, "
                + "            entered_on)"
                + "    VALUES (?, ?, ?, ?, ?,"
                + "            current_timestamp)";
        try {
            tmgr = new TransactionManager("insertDataintoDataTable");
            ps = tmgr.prepareStatement(sql);

            int i = 1;
            ps.setString(i++, dobj.getState_cd());
            ps.setInt(i++, dobj.getOff_cd());
            String time = localDateFormat.format(dobj.getOpen_timing());
            ps.setString(i++, time);
            time = localDateFormat.format(dobj.getClose_timing());
            ps.setString(i++, time);
            ps.setString(i++, Util.getEmpCode());
            ps.executeUpdate();
            tmgr.commit();
            msg = "Office Timing SuccessFully Added";
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            msg = "Data Not Saved.";
            throw new VahanException(msg);
        } finally {
            try {
                ps.close();
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                msg = "Data Not Saved.";
            }
        }
        return msg;
    }

    public static String modifydeleteintoDataTable(LoginTimingDobj dobj, String emp_cd, String state_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String msg = null;
        String sql = "INSERT INTO  " + TableList.THM_CONFIGURATION_LOGIN + " ("
                + " state_cd, off_cd, open_time, close_time, changed_by, changed_on)"
                + " select state_cd, off_cd, open_time, close_time,?,current_timestamp from "
                + TableList.TM_CONFIGURATION_LOGIN + " where state_cd = ? and off_cd = ?";
        try {
            tmgr = new TransactionManager("insertDataintoDataTable");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, emp_cd);
            ps.setString(2, state_cd);
            ps.setInt(3, dobj.getOff_cd());
            int l = ps.executeUpdate();
            if (l > 0) {
                ps = null;
                sql = "UPDATE " + TableList.TM_CONFIGURATION_LOGIN + " "
                        + "   SET open_time=?, close_time=?, "
                        + "       entered_by=?, entered_on= current_timestamp "
                        + " WHERE state_cd=? and off_cd=?";
                int i = 1;
                ps = tmgr.prepareStatement(sql);
                ps.setString(i++, localDateFormat.format(dobj.getOpen_timing()));
                ps.setString(i++, localDateFormat.format(dobj.getClose_timing()));
                ps.setString(i++, emp_cd);
                ps.setString(i++, dobj.getState_cd());
                ps.setInt(i++, dobj.getOff_cd());
                ps.executeUpdate();
            }
            tmgr.commit();
            msg = "Office Timing SuccessFully Updated";
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            msg = "Office Timing Not Updated";
            throw new VahanException(msg);
        } finally {
            try {
                ps.close();
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                msg = "Data Not Updated";
            }
        }
        return msg;
    }

    public static List<LoginTimingDobj> getDataAndTimefromdataTable(String stateName, String officeName, int off_cd, String state_cd) throws VahanException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        ArrayList<LoginTimingDobj> list = new ArrayList<>();
        String sql = "SELECT a.state_cd,a.off_cd,a.user_cd,a.user_name,COALESCE(max(a.opentime2),max(a.opentime1)) as opentime,COALESCE(max(a.closetime2),max(a.closetime1)) as closetime FROM"
                + " ("
                + " select state_cd , off_cd, user_cd, user_name,  null as opentime1, open_time as opentime2, null as closetime1,"
                + " case when ((tut.entered_on is NOT null) AND (tut.entered_on :: date = current_date)) THEN close_time else null end as closetime2 from " + TableList.TM_CONFIGURATION_USER_TIMING + " tut"
                + " where tut.state_cd=? and tut.off_cd=?"
                + " union"
                + " select tui.state_cd, tui.off_cd , tui.user_cd, tui.user_name, tcl.open_time as opentime1, null as opentime2, tcl.close_time as closetime1, null as closetime2 from tm_user_info  tui"
                + " left outer JOIN " + TableList.TM_CONFIGURATION_LOGIN + " tcl on tcl.state_cd = tui.state_cd and tui.off_cd =tcl.off_cd"
                + " where tui.state_cd=? and tui.off_cd=? and tui.user_catg NOT IN ('S','A','C','D','X','Y','B')"
                + " )"
                + " A GROUP BY 1,2,3,4";
        LoginTimingDobj logintimeDobj = null;
        try {
            tmgr = new TransactionManager("getDataAndTimefromdataTable");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                logintimeDobj = new LoginTimingDobj();
                logintimeDobj.setStateName(stateName);
                logintimeDobj.setOfficeName(officeName);
                logintimeDobj.setState_cd(rs.getString("state_cd"));
                logintimeDobj.setOff_cd(off_cd);
                logintimeDobj.setUser_name(rs.getString("user_name"));
                if (!CommonUtils.isNullOrBlank(rs.getString("opentime"))) {
                    logintimeDobj.setOpen_timing(localDateFormat.parse(rs.getString("opentime")));
                } else {
                    logintimeDobj.setOpen_timing(null);
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("closetime"))) {
                    logintimeDobj.setClose_timing(localDateFormat.parse(rs.getString("closetime")));
                } else {
                    logintimeDobj.setClose_timing(null);
                }
                logintimeDobj.setUser_cd(rs.getInt("user_cd"));
                list.add(logintimeDobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting user timing details.");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return list;
    }

    public static String modifyUserTimingintoDataTable(LoginTimingDobj dobj, String state_cd, String emp_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String msg = null;

        String sql = "UPDATE " + TableList.TM_CONFIGURATION_USER_TIMING + " "
                + "   SET open_time=?, close_time=?, "
                + "       entered_by=?, entered_on= current_timestamp "
                + " WHERE state_cd=? and off_cd=? and user_cd=?";

        int j = 1;
        try {
            tmgr = new TransactionManager("insertDataintoDataTable");
            ps = tmgr.prepareStatement(sql);
            ps.setString(j++, localDateFormat.format(dobj.getOpen_timing()));
            ps.setString(j++, localDateFormat.format(dobj.getClose_timing()));
            ps.setString(j++, emp_cd);
            ps.setString(j++, state_cd);
            ps.setInt(j++, dobj.getOff_cd());
            ps.setInt(j++, dobj.getUser_cd());
            int k = ps.executeUpdate();
            if (!(k > 0)) {
                j = 1;
                sql = "INSERT INTO " + TableList.TM_CONFIGURATION_USER_TIMING + "( "
                        + " state_cd, off_cd, user_cd, user_name, open_time, close_time, "
                        + "  entered_by, entered_on ) "
                        + " VALUES( ?,  ?,  ?,  ?,  ?,  ?, ?,  current_timestamp) ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(j++, state_cd);
                ps.setInt(j++, dobj.getOff_cd());
                ps.setInt(j++, dobj.getUser_cd());
                ps.setString(j++, dobj.getUser_name());
                String time = localDateFormat.format(dobj.getOpen_timing());
                ps.setString(j++, time);
                time = localDateFormat.format(dobj.getClose_timing());
                ps.setString(j++, time);
                ps.setString(j++, emp_cd);
                k = ps.executeUpdate();
            }
            if (k > 0) {
                msg = "User Timing Sucessfully Updated";
                tmgr.commit();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Data Has not been Updated");
        } finally {
            try {
                ps.close();
                tmgr.release();
            } catch (Exception e) {
                msg = "Data Not Updated";

            }
        }
        return msg;
    }

    public static List<LoginTimingDobj> getAllOfficesTiming(String stateName, String officeName, String state_cd) throws VahanException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        ArrayList<LoginTimingDobj> list = new ArrayList<>();
        String sql = "SELECT off_name, c.state_cd,c.off_cd,open_time,close_time "
                + " from tm_configuration_login c "
                + " inner join tm_office o on c.state_cd= o.state_cd and o.off_cd = c.off_cd "
                + " where c.state_cd=? ";
        LoginTimingDobj logintimeDobj = null;
        try {
            tmgr = new TransactionManager("getAllOfficesTiming");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                logintimeDobj = new LoginTimingDobj();
                logintimeDobj.setStateName(stateName.toUpperCase());
                logintimeDobj.setOfficeName(rs.getString("off_name"));
                logintimeDobj.setState_cd(rs.getString("state_cd"));
                logintimeDobj.setOff_cd(rs.getInt("off_cd"));
                if (!CommonUtils.isNullOrBlank(rs.getString("open_time"))) {
                    logintimeDobj.setOpen_timing(localDateFormat.parse(rs.getString("open_time")));
                } else {
                    logintimeDobj.setOpen_timing(null);
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("close_time"))) {
                    logintimeDobj.setClose_timing(localDateFormat.parse(rs.getString("close_time")));
                } else {
                    logintimeDobj.setClose_timing(null);
                }
                list.add(logintimeDobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting user timing details.");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return list;
    }

    public static String deleteOfficeTiming(LoginTimingDobj dobj, String state_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String msg = null;

        try {
            tmgr = new TransactionManager("deleteOfficeTiming");
            String sql = " INSERT INTO " + TableList.THM_CONFIGURATION_LOGIN + "  "
                    + " SELECT  state_cd,off_cd,open_time,close_time,? as changed_by ,current_timestamp FROM " + TableList.TM_CONFIGURATION_LOGIN + " where off_cd=? and state_cd=? ";
            int n = 1;

            ps = tmgr.prepareStatement(sql);
            ps.setString(n++, Util.getEmpCode());
            ps.setInt(n++, dobj.getOff_cd());
            ps.setString(n++, dobj.getState_cd());
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

            sql = "DELETE FROM " + TableList.TM_CONFIGURATION_LOGIN + " WHERE off_cd=? and state_cd=? ";
            int k = 1;

            ps = tmgr.prepareStatement(sql);
            ps.setInt(k++, dobj.getOff_cd());
            ps.setString(k++, state_cd);
            int i = ps.executeUpdate();
            if (i > 0) {
                msg = "Office Timing Sucessfully Deleted";
                tmgr.commit();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Data Has not been Deleted");
        } finally {
            try {
                ps.close();
                tmgr.release();
            } catch (Exception e) {
                msg = "Office Timing Not Deleted";
            }
        }
        return msg;
    }
}
