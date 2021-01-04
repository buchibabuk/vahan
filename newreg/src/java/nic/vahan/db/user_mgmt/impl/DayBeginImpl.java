/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.user_mgmt.dobj.DayBeginDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.db.user_mgmt.dobj.CloseReopenCashCounterDobj;

/**
 *
 * @author Administrator
 */
public class DayBeginImpl {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(DayBeginImpl.class);

    public static void getWorkDetail(DayBeginDobj dobj) throws VahanException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String sql = "Select day_begin from " + TableList.VM_SMART_CARD_HSRP + " where state_cd=? and off_cd=?";
        try {
            tmgr = new TransactionManager("getWorkDetail");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserSeatOffCode());
            rs = ps.executeQuery();
            DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            while (rs.next()) {
                if (rs.getDate("day_begin") != null && !rs.getDate("day_begin").equals("")) {
                    dobj.setLastWorkDay(format.format(rs.getDate("day_begin")));
                } else {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new java.util.Date());
                    cal.add(Calendar.DATE, -1);
                    java.util.Date yesterdayDate = cal.getTime();
                    dobj.setLastWorkDay(format.format(yesterdayDate));
                }
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
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
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            }
        }
    }

    public static boolean updateDayBegin(DayBeginDobj dobj) throws Exception {
        boolean start = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("updateDayBegin");

            boolean result = updateDayBeginHistory(tmgr, dobj);

            if (result) {
                String sql = "update " + TableList.VM_SMART_CARD_HSRP + " set last_working_day=day_begin, day_begin =current_timestamp where state_cd =? and off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getUserSeatOffCode());
                int i = ps.executeUpdate();
                if (i > 0) {
                    start = true;
                } else {
                    start = false;
                }
                tmgr.commit();
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }
        return start;
    }

    public static boolean updateDayBeginHistory(TransactionManager tmgr, DayBeginDobj dobj) throws Exception {
        boolean start = false;
        PreparedStatement ps = null;
        try {
            String sql = "insert into " + TableList.THM_OFFICE_CONFIGURATION + " select current_timestamp,?,state_cd,off_cd,day_begin,last_working_day, case when current_date > day_begin then DATE_PART('day', current_date-day_begin) else 0 end as no_of_holidays from "
                    + TableList.VM_SMART_CARD_HSRP + " where state_cd =? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(Util.getEmpCode()));
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getUserSeatOffCode());
            int i = ps.executeUpdate();
            if (i > 0) {
                start = true;
            } else {
                start = false;
            }
        } catch (Exception e) {
        }
        return start;
    }

    public static boolean updateIndividualCashCounter(DayBeginDobj dobj) throws Exception {
        boolean isOpen = false;
        boolean isCashCounterClose = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = "";
        tmgr = new TransactionManager("updateIndividualCashCounter");
        try {
            if (!dobj.isCashCounterClose()) {
                sql = "update " + TableList.TM_USER_OPEN_CASH + " set close_cash_dt = current_timestamp,close_cash=? where "
                        + "state_cd=? and user_cd=?";
                isCashCounterClose = true;
                ps = tmgr.prepareStatement(sql);
                ps.setBoolean(1, isCashCounterClose);
                ps.setString(2, Util.getUserStateCode());
                ps.setLong(3, Long.parseLong(Util.getEmpCode()));
            } else {
                sql = "insert into " + TableList.THM_USER_OPEN_CASH + " select * from " + TableList.TM_USER_OPEN_CASH + " where "
                        + "state_cd=? and user_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setLong(2, Long.parseLong(Util.getEmpCode()));
                int i = ps.executeUpdate();

                sql = "delete from " + TableList.TM_USER_OPEN_CASH + " where state_cd=? and user_cd=?";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setLong(2, Long.parseLong(Util.getEmpCode()));
                i = ps.executeUpdate();
                sql = "insert into " + TableList.TM_USER_OPEN_CASH + "(state_cd,off_cd,user_cd,open_cash_dt, \n"
                        + "            close_cash)\n"
                        + "    VALUES (?, ?, ?, current_timestamp,?)";

                isCashCounterClose = false;
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getUserSeatOffCode());
                ps.setLong(3, Long.parseLong(Util.getEmpCode()));
                ps.setBoolean(4, isCashCounterClose);
            }
            int i = ps.executeUpdate();
            if (i > 0) {
                isOpen = true;
                dobj.setCashCounterClose(isCashCounterClose);
                tmgr.commit();
            } else {
                isOpen = false;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }
        return isOpen;
    }

    public static void getCashCounterDetail(DayBeginDobj dobj) throws VahanException, Exception {
        Boolean isUpdate = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        String sql = "select open_cash_dt,close_cash,close_cash_dt from " + TableList.TM_USER_OPEN_CASH + " where state_cd=?"
                + " and off_cd=? and user_cd=?";
        try {
            tmgr = new TransactionManager("getCashCounterDetail");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserSeatOffCode());
            ps.setLong(3, Long.parseLong(Util.getEmpCode()));
            rs = ps.executeQuery();
            if (rs.next()) {
                dobj.setCashCounterClose(rs.getBoolean("close_cash"));
                if (rs.getDate("open_cash_dt") != null && !rs.getDate("open_cash_dt").equals("")) {
                    dobj.setCashCounterOpenDt(format.format(rs.getDate("open_cash_dt")));
                    Date date = rs.getDate("open_cash_dt");
                    String opendate = format.format(date) + " " + rs.getTimestamp("open_cash_dt").toString().split(" ")[1];
                    dobj.setCashCounterOpenDateTime(opendate);
                }
                if (rs.getDate("close_cash_dt") != null && !rs.getDate("close_cash_dt").equals("")) {
                    Date date = rs.getDate(("close_cash_dt"));
                    String closeDt = format.format(date) + " " + rs.getTimestamp("close_cash_dt").toString().split(" ")[1];
                    dobj.setCashCounterCloseDateTime(closeDt);

                }
//                if (!dobj.getCashCounterOpenDt().equals(dobj.getCurrentDt())) {
//                }
                if (rs.getDate("close_cash_dt") == null && !dobj.getCashCounterOpenDt().equals(dobj.getCurrentDt())) {
                    isUpdate = updateIndividualCashCounter(dobj);
                }

            }
        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }

    public static void getDateTimeForOpenCloseCashCounter(List<DayBeginDobj> openList) throws VahanException, Exception {
//        ArrayList<DayBeginDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-YYYY");
        DayBeginDobj dobj = null;
        String closeDate = "";
        String opendate = "";
        ResultSet rs = null;
        String sql = "select * from " + TableList.THM_USER_OPEN_CASH + " where state_cd=? and off_cd=? and user_cd=?"
                + " and open_cash_dt::date =? order by open_cash_dt";
        try {
            tmgr = new TransactionManager("getDateTimeForOpenCloseCashCounter");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserSeatOffCode());
            ps.setLong(3, Long.parseLong(Util.getEmpCode()));
            ps.setDate(4, new java.sql.Date(new Date().getTime()));
            rs = ps.executeQuery();
            while (rs.next()) {
                dobj = new DayBeginDobj();
                if (rs.getDate("open_cash_dt") != null && !rs.getDate("open_cash_dt").equals("")) {
                    Date date = rs.getDate("open_cash_dt");
                    opendate = format.format(date) + " " + rs.getTimestamp("open_cash_dt").toString().split(" ")[1];
                }
                if (rs.getDate("close_cash_dt") != null && !rs.getDate("close_cash_dt").equals("")) {
                    Date date = rs.getDate("close_cash_dt");
                    closeDate = format.format(date) + " " + rs.getTimestamp("close_cash_dt").toString().split(" ")[1];
                }
                dobj.setPreviousCashCounterOpenDt(opendate);
                dobj.setPreviousCashCounterCloseDt(closeDate);
                openList.add(dobj);
            }
        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }

    }

    public static boolean updateCashCounterForAllCashier(DayBeginDobj dobj) throws Exception {
        boolean isOpen = false;
        boolean isAllCashCounterClose = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("updateIndividualCashCounter");
            sql = "update " + TableList.TM_USER_OPEN_CASH + " set close_cash_dt = current_timestamp,close_cash=TRUE where "
                    + "state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserSeatOffCode());
            int i = ps.executeUpdate();
            if (i > 0) {
                isOpen = true;
                dobj.setCloseAllCashCounter(true);
                tmgr.commit();
            } else {
                isOpen = false;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Getting Cash Counter List");
        } finally {
            try {
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
        return isOpen;
    }

    public List<CloseReopenCashCounterDobj> getCounterDetails() throws VahanException {
        List<CloseReopenCashCounterDobj> list = new ArrayList<>();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = "";
        // int i = 0;
        try {
            tmgr = new TransactionManager("getCounterDetails");
            sql = "select a.user_cd,a.user_id,a.user_name,b.* from " + TableList.TM_USER_OPEN_CASH + " b"
                    + " inner join " + TableList.TM_USER_INFO + " a on a.user_cd = b.user_cd and a.state_cd = b.state_cd and a.off_cd = b.off_cd"
                    + " where b.state_cd = ? and b.off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserSeatOffCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                CloseReopenCashCounterDobj dobj = new CloseReopenCashCounterDobj();
                dobj.setUser_cd(rs.getString("user_cd"));
                dobj.setUser_id(rs.getString("user_id"));
                dobj.setUser_name(rs.getString("user_name"));
                dobj.setClose_cash(rs.getBoolean("close_cash"));
//                if (!rs.getBoolean("close_cash")) {
//                    dobj.setOpen_all_counter(false);
//                }
                list.add(dobj);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Getting Cash Counter List");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return list;

    }

    public static boolean updateIndividualCashCounterThroughAdmin(CloseReopenCashCounterDobj dobj) throws Exception {
        boolean isOpen = false;
        boolean isCashCounterClose = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = "";
        tmgr = new TransactionManager("updateIndividualCashCounterThroughAdmin");
        try {
            if (!dobj.isClose_cash()) {
                sql = "update " + TableList.TM_USER_OPEN_CASH + " set close_cash_dt = current_timestamp,close_cash=? where "
                        + "state_cd=? and user_cd=?";
                isCashCounterClose = true;
                ps = tmgr.prepareStatement(sql);
                ps.setBoolean(1, isCashCounterClose);
                ps.setString(2, Util.getUserStateCode());
                ps.setLong(3, Long.parseLong(dobj.getUser_cd()));
            } else {
                sql = "insert into " + TableList.THM_USER_OPEN_CASH + " select * from " + TableList.TM_USER_OPEN_CASH + " where "
                        + "state_cd=? and user_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setLong(2, Long.parseLong(dobj.getUser_cd()));
                int i = ps.executeUpdate();

                sql = "delete from " + TableList.TM_USER_OPEN_CASH + " where state_cd=? and user_cd=?";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setLong(2, Long.parseLong(dobj.getUser_cd()));
                i = ps.executeUpdate();
                sql = "insert into " + TableList.TM_USER_OPEN_CASH + "(state_cd,off_cd,user_cd,open_cash_dt, \n"
                        + "            close_cash)\n"
                        + "    VALUES (?, ?, ?, current_timestamp,?)";

                isCashCounterClose = false;
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getUserSeatOffCode());
                ps.setLong(3, Long.parseLong(dobj.getUser_cd()));
                ps.setBoolean(4, isCashCounterClose);
            }
            int i = ps.executeUpdate();
            if (i > 0) {
                isOpen = true;
                dobj.setClose_cash(isCashCounterClose);
                tmgr.commit();
            } else {
                isOpen = false;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }
        return isOpen;
    }

    public static void getCashCounterDetailForAdmin(DayBeginDobj dobj) throws Exception {
        Boolean isUpdate = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        String sql = "select a.user_cd,a.open_cash_dt,a.close_cash,a.close_cash_dt from " + TableList.TM_USER_OPEN_CASH + " a "
                + " inner join " + TableList.TM_USER_INFO + " b on b.user_cd=a.user_cd and b.state_cd=a.state_cd and b.off_cd=a.off_cd "
                + " where a.state_cd=? and a.off_cd=?";
        try {
            tmgr = new TransactionManager("getCashCounterDetailForAdmin");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getStateCd());
            ps.setInt(2, dobj.getOffCd());
            rs = ps.executeQuery();
            while (rs.next()) {
                dobj.setCashCounterClose(rs.getBoolean("close_cash"));
                if (rs.getDate("open_cash_dt") != null && !rs.getDate("open_cash_dt").equals("")) {
                    dobj.setCashCounterOpenDt(format.format(rs.getDate("open_cash_dt")));
                    Date date = rs.getDate("open_cash_dt");
                    String opendate = format.format(date) + " " + rs.getTimestamp("open_cash_dt").toString().split(" ")[1];
                    dobj.setCashCounterOpenDateTime(opendate);
                }
                if (rs.getDate("close_cash_dt") != null && !rs.getDate("close_cash_dt").equals("")) {
                    Date date = rs.getDate(("close_cash_dt"));
                    String closeDt = format.format(date) + " " + rs.getTimestamp("close_cash_dt").toString().split(" ")[1];
                    dobj.setCashCounterCloseDateTime(closeDt);

                }
//                if (!dobj.getCashCounterOpenDt().equals(dobj.getCurrentDt())) {
//                }
                if (rs.getDate("close_cash_dt") == null && !dobj.getCashCounterOpenDt().equals(dobj.getCurrentDt())) {
                    dobj.setUser_cd(rs.getString("user_cd"));
                    isUpdate = updateIndividualCashCounter(dobj);
                }

            }
        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }

    public static boolean updateAllCashCounterOpenThroughAdmin() throws Exception {
        boolean isOpen = false;
        boolean isCashCounterClose = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = "";
        int i = 0;
        tmgr = new TransactionManager("updateAllCashCounterOpenThroughAdmin");
        try {
//            if (!dobj.isClose_cash()) {
//                sql = "update " + TableList.TM_USER_OPEN_CASH + " set close_cash_dt = current_timestamp,close_cash=? where "
//                        + "state_cd=? and off_cd = ?";
//                isCashCounterClose = true;
//                ps = tmgr.prepareStatement(sql);
//                ps.setBoolean(1, isCashCounterClose);
//                ps.setString(2, Util.getUserStateCode());
//                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
//            } else {
            sql = "insert into " + TableList.THM_USER_OPEN_CASH + " select a.* from " + TableList.TM_USER_OPEN_CASH + " a"
                    + " inner join " + TableList.TM_USER_INFO + " b on b.user_cd=a.user_cd and b.state_cd=a.state_cd and b.off_cd=a.off_cd "
                    + " where a.state_cd=? and a.off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            i = ps.executeUpdate();

            sql = " UPDATE " + TableList.TM_USER_OPEN_CASH
                    + " set open_cash_dt = current_timestamp, close_cash_dt = NULL,"
                    + "  close_cash=FALSE"
                    + " WHERE (state_cd,off_cd,user_cd) in (select state_cd,off_cd,user_cd from " + TableList.TM_USER_INFO
                    + " where state_cd = ? and off_cd =? )";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            i = ps.executeUpdate();

//                sql = "delete from " + TableList.TM_USER_OPEN_CASH + " where state_cd=? and off_cd = ?";
//
//                ps = tmgr.prepareStatement(sql);
//                ps.setString(1, Util.getUserStateCode());
//                ps.setInt(2, Util.getSelectedSeat().getOff_cd());
//                i = ps.executeUpdate();
//                sql = "insert into " + TableList.TM_USER_OPEN_CASH + "(state_cd,off_cd,user_cd,open_cash_dt, \n"
//                        + "            close_cash)\n"
//                        + "    VALUES (?, ?, ?, current_timestamp,?)";
//
//                isCashCounterClose = false;
//                ps = tmgr.prepareStatement(sql);
//                ps.setString(1, Util.getUserStateCode());
//                ps.setInt(2, Util.getUserSeatOffCode());
//                ps.setLong(3, Long.parseLong(dobj.getUser_cd()));
//                ps.setBoolean(4, isCashCounterClose);
//            }

            if (i > 0) {
                isOpen = true;
                //dobj.setClose_cash(isCashCounterClose);
                tmgr.commit();
            } else {
                isOpen = false;
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }
        return isOpen;
    }
}
