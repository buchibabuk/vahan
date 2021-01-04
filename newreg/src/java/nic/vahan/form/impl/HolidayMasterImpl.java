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
import nic.vahan.form.dobj.HolidayMasterDobj;
import nic.vahan.server.ServerUtil;

public class HolidayMasterImpl {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(HolidayMasterImpl.class);

    public static boolean saveHoliday_DateData(String state_cd, int offCd, String emp_cd, String reason, String holiday_date) throws VahanException {
        boolean flag = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = "";
        String whereiam = "saveHoliday_DateData";
        try {
            tmgr = new TransactionManager(whereiam);
            if (getHoliday_DateValue(state_cd, offCd, holiday_date)) {
                sql = "UPDATE " + TableList.VM_HOLIDAY_MASTER + "\n"
                        + "  SET holiday_reason=?, user_cd=? , op_date=current_timestamp  where holiday_date=? and state_cd=? and  off_cd=?";
            } else {
                sql = "INSERT INTO " + TableList.VM_HOLIDAY_MASTER + "(\n"
                        + "  holiday_reason, user_cd , op_date, holiday_date, state_cd, off_cd) \n"
                        + "  VALUES (?, ?,current_timestamp, ?, ? , ?)";
            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, reason);
            ps.setInt(2, Integer.parseInt(emp_cd));
            ps.setTimestamp(3, ServerUtil.getDateToTimesTamp(holiday_date));
            ps.setString(4, state_cd);
            ps.setInt(5, offCd);
            ps.executeUpdate();
            tmgr.commit();
            flag = true;
        } catch (VahanException vex) {
            flag = false;
            throw vex;
        } catch (Exception e) {
            flag = false;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Unable to process your request.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return flag;
    }

    public static boolean getHoliday_DateValue(String state_cd, int off_cd, String holiday_dt) throws VahanException {
        boolean flag = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = "";
        String whereiam = "getHoliday_DateValue";
        try {
            tmgr = new TransactionManager(whereiam);
            sql = " select * from " + TableList.VM_HOLIDAY_MASTER + " where holiday_date=? and state_cd=? and off_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setTimestamp(1, ServerUtil.getDateToTimesTamp(holiday_dt));
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                flag = true;
            }

        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                    throw new VahanException(TableConstants.SomthingWentWrong);
                }
            }
        }
        return flag;
    }

    //gettingListFromvm_holiday_master
    public static List<HolidayMasterDobj> gettingListFromvm_holiday_master(String state_cd, int off_cd) throws VahanException {
        ArrayList<HolidayMasterDobj> dobjLis = new ArrayList<>();
        HolidayMasterDobj dobj = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = "";
        String whereiam = "gettingListFromvm_holiday_master";
        try {
            tmgr = new TransactionManager(whereiam);
            sql = " select * from " + TableList.VM_HOLIDAY_MASTER + " where state_cd=? and off_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                dobj = new HolidayMasterDobj();
                dobj.setHoliday_date(rs.getString("holiday_date"));
                dobj.setHoliday_reason(rs.getString("holiday_reason"));
                dobj.setOp_date(rs.getString("op_date"));
                dobjLis.add(dobj);
            }

        } catch (Exception e) {
            dobjLis = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Unable to get holiday records.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException(TableConstants.SomthingWentWrong);
                }
            }
        }
        return dobjLis;
    }

    //deleteFromvm_holiday_master
    public static boolean deleteFromvm_holiday_master(String state_cd, int off_cd, HolidayMasterDobj dobj) throws VahanException {
        boolean flag = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = "";
        String whereiam = "deleteFromvm_holiday_master";
        try {
            tmgr = new TransactionManager(whereiam);
            sql = " delete from " + TableList.VM_HOLIDAY_MASTER + " where holiday_date=? and state_cd=? and off_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setTimestamp(1, ServerUtil.getDateToTimesTamp(dobj.getHoliday_date()));
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            int i = ps.executeUpdate();
            if (i > 0) {
                tmgr.commit();
                flag = true;
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Unable to delete holiday record.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return flag;
    }
}
