/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.user_mgmt.dobj.UserAuthorityDobj;
import nic.vahan.form.dobj.UserInfo;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import java.util.Map;
import java.util.LinkedHashMap;
import nic.rto.vahan.common.ApplicationConfiguration;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.common.DOAuditTrail;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.mail.MailSender;

public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class);
    /// pending work in this file...
    //      1. Use Salted MD5 for Login Auhtenticaltion....
    //   for this browser must send Salted MD5 Password and then here you need to Salt your DB password with the Salting Number used by the
    //   browser and then match.

    public static List<UserInfo> login(String user_id) {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        List<UserInfo> loginStatus = new ArrayList<>();

        String sql = null;
        if (TableConstants.isNextStageWebService) {
            sql = " select emp_cd,user_pwd,emp_name,desig_name,state,off_cd, descr as state_name "
                    + "   from tm_user left outer join tm_state on " + TableList.TM_STATE + " = state where emp_cd= ?";

        } else {
            sql = "select a.user_cd, a.user_id, a.user_pwd, a.user_name, a.state_cd, "
                    + "   a.off_cd, b.descr as state_name, c.desig_name, "
                    + "   a.phone_off, a.mobile_no, a.email_id, a.user_catg, a.status, a.newuser_change_password "
                    + " from " + TableList.TM_USER_INFO + " a "
                    + " left outer join " + TableList.TM_STATE + " b on b.state_code = a.state_cd "
                    + " left outer join " + TableList.TM_DESIGNATION + " c on c.desig_cd = a.desig_cd "
                    + " where user_id = ?";
        }
        try {
            tmgr = new TransactionManager("Login");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, user_id);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                UserInfo lb = new UserInfo();
                lb.setEmp_cd(rs.getLong("user_cd"));
                lb.setUser_id(rs.getString("user_id"));
                lb.setEmp_name(rs.getString("user_name"));
                lb.setDesig_name(rs.getString("desig_name"));
                lb.setState_cd(rs.getString("state_cd"));
                lb.setOff_cd(rs.getInt("off_cd"));
                lb.setState_name(rs.getString("state_name"));
                lb.setUser_catg(rs.getString("user_catg"));
                lb.setStatus(rs.getString("status"));
                lb.setNewuser_change_password(rs.getString("newuser_change_password"));
                loginStatus.add(lb);
            }
            tmgr = null;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return loginStatus;
    }

    public static UserAuthorityDobj userAuthority(String user_id) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        UserAuthorityDobj authorityDobj = null;
        String sql = "SELECT lower_range_no, upper_range_no, class_type, vch_class, \n"
                + "       pmt_type, pmt_catg, dealer_cd FROM " + TableList.TM_USER_PERMISSIONS + " per\n"
                + "left join " + TableList.TM_USER_INFO + " info on info.USER_CD = per.user_cd\n"
                + "WHERE info.USER_ID = ?";
        try {
            tmgr = new TransactionManager("getuserPermission");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, user_id);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                authorityDobj = new UserAuthorityDobj();
                authorityDobj.setLowerBound(rs.getInt("lower_range_no"));
                authorityDobj.setUpperBound(rs.getInt("upper_range_no"));
                authorityDobj.setVehType(rs.getInt("class_type"));
                authorityDobj.setPermitType(ServerUtil.makeList(rs.getString("pmt_type")));
                authorityDobj.setSelectedPermitCatg(ServerUtil.makeList(rs.getString("pmt_catg")));
                authorityDobj.setSelectedVehClass(ServerUtil.makeList(rs.getString("vch_class")));
                authorityDobj.setDealerCode(rs.getString("dealer_cd"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return authorityDobj;
    }

    public static boolean validatePassword(String user_id, String password, String hiddenRandomNo) {
        boolean valid = false;
        PreparedStatement ps;
        RowSet rs;
        String sql;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("validatePassword");
            sql = "select user_pwd,user_cd from " + TableList.TM_USER_INFO + " where user_id= ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, user_id);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                String pwd = rs.getString("user_pwd");
                Long user_cd = rs.getLong("user_cd");
                pwd = ServerUtil.sha256hex(pwd + hiddenRandomNo);
                if (password.equals(pwd)) {
                    valid = true;
                }
            }
        } catch (Exception ex) {
            LOGGER.error(new VahanException("Error in validatePassword") + ex.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return valid;
    }

    public static Map<String, String> getRunningRtoRecords(String sqlQuery, boolean isMultipleRowData, boolean stateWise) {
        TransactionManager tmgr = null;
        String whereiam = "DashboardImpl.getRunningRtoRecords";
        Map hm = new LinkedHashMap();
        PreparedStatement psmt = null;
        RowSet rs = null;
        String sql = null;
        int totalV4States = 0;
        int totalV4 = 0;
        try {
            tmgr = new TransactionManager(whereiam);
            sql = "select state_cd as x_axis,case when state_cd in ('MP','TS','AP') then 0 else sum(started)  end as y_axis from (select o.state_cd, count(*) as started,0 as notstarted from tm_office o inner join tm_state s on s.state_code=o.state_cd where o.vow4 is not null group by o.state_cd union select o.state_cd, 0 as started,count(*) as notstarted from tm_office o inner join tm_state s on s.state_code=o.state_cd where o.vow4 is null group by o.state_cd) as a group by state_cd order by state_cd";
            psmt = tmgr.prepareStatement(sql);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                hm.put(rs.getString("x_axis"), rs.getString("y_axis"));
                totalV4 = totalV4 + rs.getInt("y_axis");
                if (rs.getInt("y_axis") > 0) {
                    totalV4States = totalV4States + 1;
                }
            }
            ApplicationConfiguration.totalRunningV4_offices = "(" + totalV4States + " states & " + totalV4 + " offices)";
            ApplicationConfiguration.totalVahan4_offices = totalV4;
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return hm;
    }

    public static Map<String, String> getTotalRtoRecords(String sqlQuery, boolean isMultipleRowData, boolean stateWise) {
        TransactionManager tmgr = null;
        String whereiam = "DashboardImpl.getTotalRtoRecords";
        Map hm = new LinkedHashMap();
        PreparedStatement psmt = null;
        RowSet rs = null;
        String sql = null;
        int totalVahan_offices = 0;
        try {
            tmgr = new TransactionManager(whereiam);
            sql = "select o.state_cd as x_axis,count(*) as y_axis from tm_office o inner join tm_state s on s.state_code=o.state_cd  group by o.state_cd order by o.state_cd asc";
            psmt = tmgr.prepareStatement(sql);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                hm.put(rs.getString("x_axis"), rs.getString("y_axis"));
                totalVahan_offices = totalVahan_offices + rs.getInt("y_axis");
            }
            ApplicationConfiguration.totalVahan_offices = totalVahan_offices;
            ApplicationConfiguration.totalOlderVahan_offices = ApplicationConfiguration.totalVahan_offices - (ApplicationConfiguration.totalVahan4_offices + ApplicationConfiguration.getTotalnonVahan_offices());
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return hm;
    }

    public static Map<String, String> getNonRtoRecords(String sqlQuery, boolean isMultipleRowData, boolean stateWise) {
        TransactionManager tmgr = null;
        String whereiam = "DashboardImpl.getNonRtoRecords";
        Map hm = new LinkedHashMap();
        PreparedStatement psmt = null;
        RowSet rs = null;
        String sql = null;
        int totalNonVahanStates = 0;
        int totalnonVahanOffices = 0;
        try {
            tmgr = new TransactionManager(whereiam);
            sql = "select o.state_cd as x_axis,case when state_cd in ('MP','TS','AP') then count(*) else 0 end as y_axis from tm_office o inner join tm_state s on s.state_code=o.state_cd  group by o.state_cd order by o.state_cd asc";
            psmt = tmgr.prepareStatement(sql);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                hm.put(rs.getString("x_axis"), rs.getString("y_axis"));
                totalnonVahanOffices = totalnonVahanOffices + rs.getInt("y_axis");
                if (rs.getInt("y_axis") > 0) {
                    totalNonVahanStates = totalNonVahanStates + 1;
                }
            }
            ApplicationConfiguration.setTotalnonVahan_states(totalNonVahanStates);
            ApplicationConfiguration.setTotalnonVahan_offices(totalnonVahanOffices);
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return hm;
    }

    public static boolean getBlockUser(String user_id) {
        boolean valid = false;
        PreparedStatement ps;
        RowSet rs;
        String sql;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getBlockUser");
            sql = "SELECT true as valid from " + TableList.TM_USER_STATUS + " where user_cd=(select user_cd from " + TableList.TM_USER_INFO + " where user_id = ?)::text";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, user_id);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                valid = rs.getBoolean("valid");
            }

        } catch (Exception ex) {
            LOGGER.error(new VahanException("Error in Fetch UserID") + ex.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return valid;
    }

    public static String generateLoginType(String userId, boolean isReGenerate) throws VahanException {
        boolean loginType = false;
        String userData = "";
        String mobile = "";
        String otp = "";
        String email_id = "";
        PreparedStatement ps;
        RowSet rs;
        String sql;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("generateLoginType");
            sql = "select vs.login_with_otp,tu.email_id, tu.mobile_no::text, tu.user_cd,totp.op_dt,tu.user_catg,"
                    + " strpos(user_catg_mandate_otp,','||tu.user_catg||',') = 0 as valid_user_catg,"
                    + " (current_time between (? :: time) and (? :: time)) as  valid_current_morning, ((totp.op_dt :: time) between (? :: time) and (? :: time)) as valid_morning_op_dt,"
                    + " ((EXTRACT (EPOCH FROM (current_timestamp::timestamp) - totp.op_dt)/3600) <= cast((extract (epoch from  ?::time -?::time)/3600) as int)) as valid_hours, totp.otp::text"
                    + " from " + TableList.TM_CONFIGURATION + "  vs"
                    + " inner join " + TableList.TM_USER_INFO + " tu on tu.state_cd = vs.state_cd"
                    + " left outer join " + TableList.TM_USER_OTP + " totp on totp.user_cd = tu.user_cd"
                    + " where tu.user_id=? order by totp.op_dt desc limit 1";
            ps = tmgr.prepareStatement(sql);
            String[] time = TableConstants.MORNING_TIMING_OTP.split("-");
            ps.setString(1, time[0]);
            ps.setString(2, time[1]);
            ps.setString(3, time[0]);
            ps.setString(4, time[1]);
            ps.setString(6, time[0]);
            ps.setString(5, time[1]);
            ps.setString(7, userId);
            rs = tmgr.fetchDetachedRowSet_No_release();
            String user_cd = null;
            String old_otp = "";
            if (rs.next()) {
                loginType = rs.getBoolean("login_with_otp") || !rs.getBoolean("valid_user_catg");
                mobile = rs.getString("mobile_no");
                user_cd = rs.getString("user_cd");
                old_otp = rs.getString("otp");
                email_id = rs.getString("email_id");
                boolean isverified = UserDAO.getVerificationDetails(user_cd);
                userData = loginType + "`" + mobile + "`" + old_otp + "`" + user_cd + "`" + isverified + "`" + rs.getString("user_catg");
                boolean valid_otp_condition = false;
                // If user is OTP based and password is correct and Mobile number is verified.
                if (loginType && (!isReGenerate || isReGenerate) && isverified) {
                    valid_otp_condition = !CommonUtils.isNullOrBlank(old_otp)
                            && ((rs.getBoolean("valid_current_morning")
                            && rs.getBoolean("valid_morning_op_dt")) || (!rs.getBoolean("valid_current_morning")
                            && !rs.getBoolean("valid_morning_op_dt")))
                            && rs.getBoolean("valid_hours");
                    otp = valid_otp_condition ? old_otp : new ServerUtil().genOTP(mobile);
                    userData = loginType + "`" + mobile + "`" + otp + "`" + user_cd + "`" + isverified + "`" + rs.getString("user_catg");
                }
                if (isReGenerate) {
                    otp = new ServerUtil().genOTP(mobile);
                    userData = loginType + "`" + mobile + "`" + otp + "`" + user_cd + "`" + isverified + "`" + rs.getString("user_catg");
                    //String otp_msg = "OTP for VAHAN4 user is " + otp + " and valid upto 12:00 hours. Do not share it with anyone.";
                    // ServerUtil.sendOTP(mobile, otp_msg);
//                    if (email_id != null && !email_id.equals("")) {
//                        MailSender sendMail = new MailSender(email_id, otp_msg, "OTP for VAHAN4 Login");
//                        sendMail.start();
//                    }

                }
                if (!CommonUtils.isNullOrBlank(otp) && !CommonUtils.isNullOrBlank(user_cd) && !valid_otp_condition && !isReGenerate || isReGenerate) {
                    sql = "UPDATE " + TableList.TM_USER_OTP
                            + "     SET otp=?, op_dt=current_timestamp"
                            + " WHERE user_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    int i = 1;
                    ps.setString(i++, otp);
                    ps.setInt(i++, Integer.valueOf(user_cd));
                    int psexecute = ps.executeUpdate();
                    if (!(psexecute > 0)) {
                        sql = "INSERT INTO " + TableList.TM_USER_OTP
                                + " (user_cd, otp, op_dt)"
                                + " VALUES (?, ?, current_timestamp);";
                        ps = tmgr.prepareStatement(sql);
                        i = 1;
                        ps.setInt(i++, Integer.valueOf(user_cd));
                        ps.setString(i++, otp);
                        psexecute = ps.executeUpdate();
                    }
                    if (psexecute > 0) {
                        String otp_msg = "Namaskar! " + otp + " is your OTP for VAHAN4 Login. Please keep it safe for next 12:00 hours. Do not share it with anyone.";
                        ServerUtil.sendOTP(mobile, otp_msg, Util.getUserStateCode());
                        if (email_id != null && !email_id.equals("")) {
                            MailSender sendMail = new MailSender(email_id, otp_msg, "OTP for VAHAN4 Login");
                            sendMail.start();
                        }
                    }
                }
            }
            tmgr.commit();
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new VahanException("Unable to Genrate OTP.");
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

    public static boolean getVerificationDetails(String user_cd) throws VahanException {
        boolean valid = false;
        PreparedStatement ps;
        RowSet rs;
        String sql;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getVerificationDetails");
            sql = "SELECT  case when count(1) = 0 then false else true end as isverified"
                    + "  from " + TableList.TM_USER_DETAILS_VERIFICATION + "  where user_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, Integer.valueOf(user_cd));
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                valid = rs.getBoolean("isverified");
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Unable to get user details.");
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
        return valid;
    }

    public static boolean getOfficetime(String state_cd, int off_cd, long user_cd) throws VahanException {
        boolean valid = true;
        PreparedStatement ps;
        RowSet rs;
        String sql;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getOfficetime");
            sql = "select (a.opentime <= (current_timestamp :: timestamp)) and (a.closeTime > (current_timestamp :: timestamp)) as valid \n"
                    + " from ("
                    + " select ((current_date :: date)||(' '||COALESCE(tcut.open_time,tcl.open_time))) :: timestamp as openTime, "
                    + " case when ((tcut.entered_on :: date < current_date) or (tcut.entered_on is null)) then ((current_date :: date)||(' '||tcl.close_time)) :: timestamp "
                    + " else ((current_date :: date)||(' '||tcut.close_time)) :: timestamp end closeTime "
                    + " from " + TableList.TM_CONFIGURATION_LOGIN + " tcl "
                    + " left outer join " + TableList.TM_CONFIGURATION_USER_TIMING + " tcut on tcut.state_cd = tcl.state_cd and tcut.off_cd = tcl.off_cd and tcut.user_cd = ? "
                    + " where tcl.state_cd = ? and tcl.off_cd = ? "
                    + " ) a";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, user_cd);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                valid = rs.getBoolean("valid");
                sql = "INSERT INTO " + TableList.THM_CONFIGURATION_USER_TIMING
                        + " ( " + " SELECT state_cd, off_cd, user_cd, user_name, open_time, close_time, entered_by, entered_on, current_timestamp,? "
                        + " FROM " + TableList.TM_CONFIGURATION_USER_TIMING + " where state_cd = ? and off_cd = ? and user_cd= ? and entered_on::date < current_date)";
                ps = tmgr.prepareStatement(sql);
                ps.setLong(1, user_cd);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                ps.setInt(4, (int) user_cd);

                if (ps.executeUpdate() > 0) {
                    String Sql = " delete from " + TableList.TM_CONFIGURATION_USER_TIMING + " where state_cd = ? and off_cd = ? and user_cd = ? ";
                    ps = tmgr.prepareStatement(Sql);
                    ps.setString(1, state_cd);
                    ps.setInt(2, off_cd);
                    ps.setLong(3, user_cd);
                    ps.executeUpdate();
                }
                tmgr.commit();
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Fetch Data");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        getholidays(state_cd, off_cd);
        return valid;
    }

    public static void getholidays(String state_cd, int off_cd) throws VahanException {
        PreparedStatement ps;
        RowSet rs;
        String sql;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getholiday");
            sql = "select holiday_reason from " + TableList.VM_HOLIDAY_MASTER + " where state_cd=? and off_cd=? and holiday_date::date = current_date";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                throw new VahanException("Today is holiday due to " + rs.getString("holiday_reason"));
            }
        } catch (VahanException ex) {
            throw new VahanException(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Fetch Data");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
    }

    // FOR IP ADDRESS Status 
    public static boolean getIpAddress(String state_cd) throws VahanException {
        boolean valid = false;
        PreparedStatement ps;
        RowSet rs;
        String sql;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getIpAddress");
            sql = "SELECT login_via_ip_address from " + TableList.TM_CONFIGURATION + " where state_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                valid = rs.getBoolean("login_via_ip_address");
            }
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
        return valid;
    }

    //For Session_id And IP Address 
    public static UserInfo getSessionIdAndIPDetails(String user_id) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        UserInfo dobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("getSessionIdAndIPDetails");
            sql = " select * from vahan4.tm_user_login_info where user_id=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, user_id);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new UserInfo();
                dobj.setIp_address(rs.getString("ipaddress"));
                dobj.setSession_id(rs.getString("session_id"));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in getting User Details");
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

    public void loginUserInfo(DOAuditTrail auditTrail, String session_id, String casTicket) throws VahanException, SQLException {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("loginUserInfo");
            sql = "select * from vahan4.tm_user_login_info where user_id= ?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, auditTrail.getmUserNameId());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                sql = "update vahan4.tm_user_login_info set ipaddress=?,session_id = ?,access_on = current_timestamp,cas_ticket = ? where user_id = ?  ";
                pstmt = tmgr.prepareStatement(sql);
                pstmt.setString(1, auditTrail.getmIpAddress());
                pstmt.setString(2, session_id);
                pstmt.setString(3, casTicket);
                pstmt.setString(4, auditTrail.getmUserNameId());
                ServerUtil.validateQueryResult(tmgr, pstmt.executeUpdate(), pstmt);

            } else {
                sql = "INSERT INTO vahan4.tm_user_login_info( user_id, ipaddress, session_id, access_on, cas_ticket) VALUES (?, ?, ?, ?, current_timestamp) ";
                pstmt = tmgr.prepareStatement(sql);
                pstmt.setString(1, auditTrail.getmUserNameId());
                pstmt.setString(2, auditTrail.getmIpAddress());
                pstmt.setString(3, session_id);
                pstmt.setString(4, casTicket);
                ServerUtil.validateQueryResult(tmgr, pstmt.executeUpdate(), pstmt);
            }
            tmgr.commit();
        } catch (VahanException vex) {
            throw vex;
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
    }

    /*
        Validate CAS ticket in the session with the CAS ticket stored in the DB
        @author Kartikey Singh
    */
    public static boolean validateCASTicketForLoggedInUser(String user_id, String casTicket) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        boolean validCasTicket = false;
        try {
            tmgr = new TransactionManagerReadOnly("validateCASTicketForLoggedInUser");
            sql = " select cas_ticket from vahan4.tm_user_login_info where user_id=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, user_id);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (casTicket != null && casTicket.equals(rs.getString("cas_ticket"))) {
                    validCasTicket = true;
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in validating CAS ticket for User");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return validCasTicket;
    }

    /*
        Update CAS ticket in the DB whenever the session is invalidated
        @author Kartikey Singh
    */
    public static void removeCASTicketForLoggedInUser(String user_id, String casTicket) throws VahanException {
        String sql = null;
        PreparedStatement pstmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("updateCASTicketForLoggedInUser");
            sql = "select * from vahan4.tm_user_login_info where user_id= ? and cas_ticket = ?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, user_id);
            pstmt.setString(2, casTicket);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                sql = "update vahan4.tm_user_login_info set cas_ticket = '' where user_id = ? and cas_ticket = ?";
                pstmt = tmgr.prepareStatement(sql);
                pstmt.setString(1, user_id);                
                pstmt.setString(2, casTicket);
                ServerUtil.validateQueryResult(tmgr, pstmt.executeUpdate(), pstmt);
                tmgr.commit();
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in removing CAS ticket for users");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
    }

    public static boolean getOTPBasedLoginDetails(String user_id) throws VahanException {
        boolean valid = false;
        PreparedStatement ps;
        RowSet rs;
        String sql;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getOTPBasedLoginDetails");
            sql = "  select user_cd,user_catg,login_with_otp,user_catg_mandate_otp from tm_user_info i "
                    + " inner join tm_configuration t on t.state_cd=i.state_cd   "
                    + " where user_id=? and  (i.user_catg = ANY (string_to_array(t.user_catg_mandate_otp,',')) or login_with_otp=true) ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, user_id);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                valid = true;
            } else {
                valid = false;
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Unable to get OTP Based LOgin  details.");
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
        return valid;
    }

    // For Ristrict User Catrgory For Login
    public static void getRestrictUserCatg(String state_cd, String user_catg) throws VahanException {
        PreparedStatement ps;
        RowSet rs;
        String sql;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getRestrictUserCatg");
            sql = "SELECT restrict_login_user_catg from " + TableList.TM_CONFIGURATION_USERMGMT + " where state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                String restrictUserCatg = rs.getString("restrict_login_user_catg");
                if (restrictUserCatg.contains(user_catg + ",")) {
                    throw new VahanException("As per the instruction you are not allowed to Login, Please Contact to State Administrator. Sorry for the inconvenience.....");
                }
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
    }

    // For compare single user close timing is valid or Not
    public static boolean compareUserCloseTiming(Integer user_cd) throws VahanException {
        boolean valid = false;
        PreparedStatement ps;
        RowSet rs;
        String sql;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("compareUserCloseTiming");
            sql = " select (((current_date :: date)||(' '||tcut.close_time)) > to_char(current_timestamp,'yyyy-mm-dd hh24:mm:ss')) as close_time "
                    + " from " + TableList.TM_CONFIGURATION_USER_TIMING + " tcut where user_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, user_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                valid = rs.getBoolean("close_time");
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Unable to compare user timing.");
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
        return valid;
    }
}
