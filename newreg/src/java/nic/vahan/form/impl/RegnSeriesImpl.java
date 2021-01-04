/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.java.util.CommonUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Regn_series_dobj;
import nic.vahan.form.dobj.VMRegnAvailableDobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

public class RegnSeriesImpl {

    private static final Logger LOGGER = Logger.getLogger(RegnSeriesImpl.class);
    private int flagSeriesDeleted = 1;

    public void addRegnSeries(Regn_series_dobj dobj) throws VahanException {
        VahanException e = null;

        TransactionManager tmgr = null;
        try {
//          select case when max(series_id) >0 then max(series_id)+1 else 1 end from vm_regn_series where off_cd=? and state_cd=?)
            String sqlInsertQuery = "INSERT INTO " + TableList.VM_REGN_SERIES
                    + "             (series_id, criteria_formula, prefix_series, lower_range_no, upper_range_no, "
                    + "              running_no, next_prefix_series, no_gen_type, entered_by, entered_on, "
                    + "              state_cd, off_cd) "
                    + "              VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            tmgr = new TransactionManager("Regn_series_Impl:addRegnSeries");
            PreparedStatement pstmt = tmgr.prepareStatement(sqlInsertQuery);

            int i = 1;
            pstmt.setInt(i++, dobj.getSeries_id());
            pstmt.setString(i++, dobj.getCriteria_formula());
            pstmt.setString(i++, dobj.getPrefix_series());
            pstmt.setInt(i++, dobj.getLower_range_no());
            pstmt.setInt(i++, dobj.getUpper_range_no());
            pstmt.setInt(i++, dobj.getRunning_no());
            pstmt.setString(i++, dobj.getNext_prefix_series());
            pstmt.setString(i++, dobj.getNo_gen_type());
            pstmt.setString(i++, Util.getEmpCode());
            pstmt.setTimestamp(i++, ServerUtil.getSystemDateInPostgres());
            pstmt.setString(i++, dobj.getState_cd());
            pstmt.setInt(i++, dobj.getOff_cd());
            ServerUtil.validateQueryResult(tmgr, pstmt.executeUpdate(), pstmt);

            //for insert record va_regn_series to vhm_regn_series(after approval)
            insertIntoHistory(tmgr, dobj);
            //for delete record from va_regn_series (after approval)

            String sql = "DELETE FROM " + TableList.VA_REGN_SERIES + " where state_cd=? and off_cd=? and series_id=?";
            pstmt = tmgr.prepareStatement(sql);
            int j = 1;
            pstmt.setString(j++, dobj.getState_cd());
            pstmt.setInt(j++, dobj.getOff_cd());
            pstmt.setInt(j++, dobj.getSeries_id());
            ServerUtil.validateQueryResult(tmgr, pstmt.executeUpdate(), pstmt);
            tmgr.commit();

        } catch (VahanException vx) {
            throw vx;
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = new VahanException("Problem in Series Saving,Please Contact to Administrator");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                e = new VahanException("Problem in Series Saving,Please Contact to Administrator");
            }
        }
        if (e != null) {
            throw e;
        }

    }

    public void insertIntoHistory(TransactionManager tmgr, Regn_series_dobj dobj) throws VahanException {
        PreparedStatement pstmt = null;
        try {
            String insertVHMQuery = "INSERT into " + TableList.VHM_REGN_SERIES + " (select series_id, criteria_formula, prefix_series, lower_range_no, upper_range_no, running_no, next_prefix_series, no_gen_type, entered_by,entered_on,? ,current_timestamp,state_cd, off_cd from  " + TableList.VA_REGN_SERIES + " where series_id=? and state_cd=? and off_cd=?)";
            pstmt = tmgr.prepareStatement(insertVHMQuery);
            pstmt.setString(1, Util.getEmpCode());
            pstmt.setInt(2, dobj.getSeries_id());
            pstmt.setString(3, dobj.getState_cd());
            pstmt.setInt(4, dobj.getOff_cd());
            ServerUtil.validateQueryResult(tmgr, pstmt.executeUpdate(), pstmt);
        } catch (VahanException vx) {
            throw vx;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }


    }

    public void updateRegnSeries(Regn_series_dobj dobj) throws VahanException {

        VahanException e = null;
        TransactionManager tmgr = null;
        String sql = "";
        int pos = 1;
        try {
            sql = "insert into " + TableList.VHM_REGN_SERIES
                    + " ( SELECT series_id, criteria_formula, prefix_series, "
                    + "lower_range_no, upper_range_no, running_no, "
                    + "next_prefix_series, no_gen_type, entered_by,entered_on, ?,"
                    + "current_timestamp,state_cd, off_cd from " + TableList.VM_REGN_SERIES
                    + " WHERE series_id=? and state_cd=? and off_cd=? )";

            tmgr = new TransactionManager("Regn_series_Impl:addRegnSeries");

            /// insert into history table.
            PreparedStatement pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(pos++, dobj.getEntered_by());
            pstmt.setInt(pos++, dobj.getSeries_id());
            pstmt.setString(pos++, dobj.getState_cd());
            pstmt.setInt(pos++, dobj.getOff_cd());
            pstmt.executeUpdate();


            sql = "UPDATE " + TableList.VM_REGN_SERIES
                    + " SET  prefix_series=?, lower_range_no=?, "
                    + " upper_range_no=?, running_no=?, next_prefix_series=?, "
                    + " entered_by=?, entered_on=? "
                    + " WHERE series_id=? and state_cd=? and off_cd=?";
            pstmt = tmgr.prepareStatement(sql);

            pos = 1;

            pstmt.setString(pos++, dobj.getPrefix_series());
            pstmt.setInt(pos++, dobj.getLower_range_no());
            pstmt.setInt(pos++, dobj.getUpper_range_no());
            pstmt.setInt(pos++, dobj.getRunning_no());

            if (dobj.getNext_prefix_series() == null || dobj.getNext_prefix_series().trim().equalsIgnoreCase("")) {
                pstmt.setNull(pos++, java.sql.Types.VARCHAR);
            } else {
                pstmt.setString(pos++, dobj.getNext_prefix_series());
            }
            pstmt.setString(pos++, dobj.getEntered_by());
            pstmt.setTimestamp(pos++, ServerUtil.getSystemDateInPostgres());
            pstmt.setInt(pos++, dobj.getSeries_id());
            pstmt.setString(pos++, dobj.getState_cd());
            pstmt.setInt(pos++, dobj.getOff_cd());
            pstmt.executeUpdate();

            /*
             sql = "INSERT INTO " + TableList.VM_REGN_AVAILABLE
             + " SELECT ? as state_cd, ? as off_cd,regn_no,status,"
             + " booking_fee as amount,? as entered_by,current_timestamp as entered_on,? as prefix_series "
             + " FROM getregnnoavailable(?,?,?,?,?)";

             pstmt = tmgr.prepareStatement(sql);
             pos = 1;
             pstmt.setString(pos++, dobj.getState_cd());
             pstmt.setInt(pos++, dobj.getOff_cd());
             pstmt.setString(pos++, dobj.getEntered_by());
             pstmt.setString(pos++, dobj.getPrefix_series());
             pstmt.setString(pos++, dobj.getState_cd());
             pstmt.setInt(pos++, dobj.getOff_cd());
             pstmt.setString(pos++, dobj.getPrefix_series());
             pstmt.setInt(pos++, dobj.getLower_range_no());
             pstmt.setInt(pos++, dobj.getUpper_range_no());
             pstmt.executeUpdate();
             */

            sql = "DELETE FROM " + TableList.VA_REGN_SERIES + " where state_cd=? and off_cd=? and series_id=?";
            pstmt = tmgr.prepareStatement(sql);
            int j = 1;
            pstmt.setString(j++, dobj.getState_cd());
            pstmt.setInt(j++, dobj.getOff_cd());
            pstmt.setInt(j++, dobj.getSeries_id());
            ServerUtil.validateQueryResult(tmgr, pstmt.executeUpdate(), pstmt);
            tmgr.commit();

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = new VahanException("Problem in Series Saving,Please Contact to Administrator");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                e = new VahanException("Problem in Series Saving,Please Contact to Administrator");
            }
        }
        if (e != null) {
            throw e;
        }
    }

    public void updateSeries(Regn_series_dobj dobj) throws VahanException {

        VahanException e = null;
        TransactionManager tmgr = null;
        String sql = "";
        int pos = 1;
        try {
            sql = "insert into " + TableList.VHM_REGN_SERIES
                    + " ( SELECT series_id, criteria_formula, prefix_series, "
                    + " lower_range_no, upper_range_no, running_no, "
                    + " next_prefix_series, no_gen_type, entered_by,entered_on, ?,"
                    + " current_timestamp,state_cd, off_cd from " + TableList.VM_REGN_SERIES
                    + " WHERE series_id=? and state_cd=? and off_cd=? )";

            tmgr = new TransactionManager("Regn_series_Impl:updateRegnNxtPrifixSeries");
            PreparedStatement pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(pos++, dobj.getEntered_by());
            pstmt.setInt(pos++, dobj.getSeries_id());
            pstmt.setString(pos++, dobj.getState_cd());
            pstmt.setInt(pos++, dobj.getOff_cd());
            pstmt.executeUpdate();

            sql = "UPDATE " + TableList.VM_REGN_SERIES
                    + " SET  "
                    + " next_prefix_series=?, "
                    + " entered_by=?, entered_on=? "
                    + " WHERE series_id=? and state_cd=? and off_cd=?";
            pstmt = tmgr.prepareStatement(sql);

            pos = 1;
            pstmt.setString(pos++, dobj.getNext_prefix_series());
            pstmt.setString(pos++, dobj.getEntered_by());
            pstmt.setTimestamp(pos++, ServerUtil.getSystemDateInPostgres());
            pstmt.setInt(pos++, dobj.getSeries_id());
            pstmt.setString(pos++, dobj.getState_cd());
            pstmt.setInt(pos++, dobj.getOff_cd());
            pstmt.executeUpdate();

            sql = "DELETE FROM " + TableList.VA_REGN_SERIES + " where state_cd=? and off_cd=? and series_id=?";
            pstmt = tmgr.prepareStatement(sql);
            int j = 1;
            pstmt.setString(j++, dobj.getState_cd());
            pstmt.setInt(j++, dobj.getOff_cd());
            pstmt.setInt(j++, dobj.getSeries_id());
            ServerUtil.validateQueryResult(tmgr, pstmt.executeUpdate(), pstmt);

            tmgr.commit();

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = new VahanException("Problem in Updating data");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                e = new VahanException("Problem in Updatting data");
            }
        }
        if (e != null) {
            throw e;
        }
    }

    public void deleteRegnSeries(Regn_series_dobj dobj) throws VahanException {

        String insertVHMQuery = "insert into vhm_regn_series (select series_id, criteria_formula, prefix_series, lower_range_no, upper_range_no, running_no, next_prefix_series, no_gen_type, entered_by,entered_on,? ,current_timestamp,state_cd, off_cd," + flagSeriesDeleted + "  from vm_regn_series where series_id=? and state_cd=? and off_cd=?)";
        String deleteQuery = " delete from vm_regn_series where series_id=? and state_cd=? and off_cd=?";
        String deleteVMRegnAvlQuery = " delete from " + TableList.VM_REGN_AVAILABLE + " where prefix_series=? and series_id=? and state_cd=? and off_cd=?";
        VahanException e = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("Regn_series_Impl:deleteRegnSeries");
            /// insert into history table.
            PreparedStatement pstmt = tmgr.prepareStatement(insertVHMQuery);
            pstmt.setString(1, Util.getEmpCode());
            pstmt.setInt(2, dobj.getSeries_id());
            pstmt.setString(3, dobj.getState_cd());
            pstmt.setInt(4, dobj.getOff_cd());
            pstmt.executeUpdate();
            /// delete query for vm_regn_series
            pstmt = tmgr.prepareStatement(deleteQuery);
            pstmt.setInt(1, dobj.getSeries_id());
            pstmt.setString(2, dobj.getState_cd());
            pstmt.setInt(3, dobj.getOff_cd());
            pstmt.executeUpdate();
            /// delete query for VM_REGN_AVAILABLE
            pstmt = tmgr.prepareStatement(deleteVMRegnAvlQuery);
            pstmt.setString(1, dobj.getPrefix_series());
            pstmt.setInt(2, dobj.getSeries_id());
            pstmt.setString(3, dobj.getState_cd());
            pstmt.setInt(4, dobj.getOff_cd());
            pstmt.executeUpdate();
            /// comit the transaction...
            tmgr.commit();
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = new VahanException("Problem in deleting data");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                e = new VahanException("Problem in deleting data");
            }
        }
        if (e != null) {
            throw e;
        }


    }

    public List<Regn_series_dobj> getAllRegnSeries() throws VahanException {
        String runningSeries = "";
        List<Regn_series_dobj> list = new ArrayList<>();
        String getListQuery = "select a.descr,a.series_id, a.criteria_formula, b.prefix_series,"
                + "  b.lower_range_no, b.upper_range_no, b.running_no, b.next_prefix_series,"
                + "  b.no_gen_type, b.entered_by, to_char(b.entered_on,'dd-MON-yyyy HH:MI:SS') as entered_on,"
                + "  a.state_cd,b.off_cd"
                + "  FROM  VM_SERIES a "
                + "  LEFT JOIN VM_REGN_SERIES b on a.series_id=b.series_id and a.state_cd=b.state_cd and b.off_cd=?"
                + "  WHERE a.state_cd=?"
                + "  order by series_id";
        VahanException e = null;
        TransactionManager tmgr = null;
        try {


            tmgr = new TransactionManager("Regn_series_Impl:addRegnSeries");
            PreparedStatement pstmt = tmgr.prepareStatement(getListQuery);

            pstmt.setInt(1, Util.getUserOffCode());
            pstmt.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                Regn_series_dobj dobj = new Regn_series_dobj();
                dobj.setSeries_id(rs.getInt("series_id"));
                dobj.setCriteria_formula(rs.getString("criteria_formula"));
                dobj.setCriteria_formuladesc(FormulaUtils.makeRegistrationSeriesDesc(rs.getString("criteria_formula")));
                dobj.setPrefix_series(rs.getString("prefix_series"));
                dobj.setLower_range_no(rs.getInt("lower_range_no"));
                dobj.setUpper_range_no(rs.getInt("upper_range_no"));
                dobj.setRunning_no(rs.getInt("running_no"));
                dobj.setNext_prefix_series(rs.getString("next_prefix_series"));
                dobj.setNo_gen_type(rs.getString("no_gen_type"));
                dobj.setEntered_by(rs.getString("entered_by"));
                dobj.setEntered_on(rs.getString("entered_on"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setCriteria_formula_descr(rs.getString("descr"));
                switch (rs.getString("no_gen_type")) {
                    case "P":
                        dobj.setNo_gen_type_descr("Number Selection From Batch");
                        break;
                    case "S":
                        dobj.setNo_gen_type_descr("Sequence");
                        break;
                    case "R":
                        dobj.setNo_gen_type_descr("Random");
                        break;
                    case "M":
                        dobj.setNo_gen_type_descr("Random Sequence");
                        break;
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("prefix_series"))) {
                    switch (String.valueOf(rs.getInt("running_no")).length()) {
                        case 1:
                            dobj.setRegn_series_format(rs.getString("prefix_series") + "-000" + String.valueOf(rs.getInt("running_no")));
                            break;
                        case 2:
                            dobj.setRegn_series_format(rs.getString("prefix_series") + "-00" + String.valueOf(rs.getInt("running_no")));
                            break;
                        case 3:
                            dobj.setRegn_series_format(rs.getString("prefix_series") + "-0" + String.valueOf(rs.getInt("running_no")));
                            break;
                        default:
                            dobj.setRegn_series_format(rs.getString("prefix_series") + "-" + String.valueOf(rs.getInt("running_no")));
                            break;

                    }
                }

                list.add(dobj);
            }


        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = new VahanException("Problem in savind data");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                e = new VahanException("Problem in savind data");
            }
        }
        if (e != null) {
            throw e;
        }

        return list;
    }

    public void updateNextRegnSeries(Regn_series_dobj dobj) throws VahanException {
        VahanException e = null;
        TransactionManager tmgr = null;
        try {
            String insertVHMQuery = "insert into vhm_regn_series (select series_id, criteria_formula, prefix_series, lower_range_no, upper_range_no, running_no, next_prefix_series, no_gen_type, entered_by,entered_on,? ,current_timestamp,state_cd, off_cd from vm_regn_series where series_id=? and state_cd=? and off_cd=?)";
            String sqlUpdateQuery = "UPDATE vm_regn_series "
                    + " SET next_prefix_series=? "
                    + " entered_by=?, entered_on=? "
                    + " where series_id=? and state_cd=? and off_cd=?";
            tmgr = new TransactionManager("Regn_series_Impl:addRegnSeries");

            /// insert into history table.
            PreparedStatement pstmt = tmgr.prepareStatement(insertVHMQuery);
            pstmt.setString(1, Util.getEmpCode());
            pstmt.setInt(2, dobj.getSeries_id());
            pstmt.setString(3, dobj.getState_cd());
            pstmt.setInt(4, Util.getUserOffCode());
            pstmt.executeUpdate();

            pstmt = tmgr.prepareStatement(sqlUpdateQuery);


            pstmt.setString(1, dobj.getNext_prefix_series());
            pstmt.setString(2, Util.getEmpCode());
            pstmt.setTimestamp(3, ServerUtil.getSystemDateInPostgres());
            pstmt.setInt(4, dobj.getSeries_id());
            pstmt.setString(5, dobj.getState_cd());
            pstmt.setInt(6, dobj.getOff_cd());
            pstmt.executeUpdate();
            tmgr.commit();


        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = new VahanException("Problem in Updating data");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                e = new VahanException("Problem in Updatting data");
            }
        }
        if (e != null) {
            throw e;
        }
    }

    public List<VMRegnAvailableDobj> getAvailRegnNo(Regn_series_dobj dobj) throws VahanException {

        List<VMRegnAvailableDobj> list = new ArrayList<>();
        String getListQuery = "SELECT * FROM " + TableList.VM_REGN_AVAILABLE + " WHERE series_id=? and state_cd=? and off_cd=?";
        int pos = 1;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("Regn_series_Impl:getAvailRegnNo");
            PreparedStatement pstmt = tmgr.prepareStatement(getListQuery);

            pstmt.setInt(pos++, dobj.getSeries_id());
            pstmt.setString(pos++, dobj.getState_cd());
            pstmt.setInt(pos++, dobj.getOff_cd());

            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                VMRegnAvailableDobj availRegn = new VMRegnAvailableDobj();
                availRegn.setRegn_no(rs.getString("regn_no"));
                availRegn.setStatus(rs.getString("status"));
                list.add(availRegn);
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Check of Avilable Series, Please Contact to the System Administrator");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Check of Avilable Series, Please Contact to the System Administrator");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

        return list;
    }

    public String validatePrefixSeries(String prefix, String nextPrefix, String stateCd, int offCd, int seriesId) {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        String existedPrefix = null;
        RowSet rs = null;
        try {

            prefix = prefix.toUpperCase();
            nextPrefix = nextPrefix.toUpperCase();

            sql = "SELECT prefix_series FROM " + TableList.VHM_REGN_SERIES
                    + " WHERE prefix_series in (?,?) and state_cd=? and off_cd=? and series_id=? and  flag not in (" + flagSeriesDeleted + ") limit 1";

            tmgr = new TransactionManager("validatePrefixSeries");

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, prefix);
            ps.setString(2, nextPrefix);
            ps.setString(3, stateCd);
            ps.setInt(4, offCd);
            ps.setInt(5, seriesId);
            rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist
                existedPrefix = "Prefix Series [ " + rs.getString("prefix_series") + " ]";
            } else {
                sql = "SELECT next_prefix_series FROM " + TableList.VHM_REGN_SERIES
                        + " WHERE next_prefix_series in (?,?) and state_cd=? and off_cd=? and series_id=? and  flag not in (" + flagSeriesDeleted + ")  and next_prefix_series is not null and trim(next_prefix_series) <> '' limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, prefix.trim());
                ps.setString(2, nextPrefix.trim());
                ps.setString(3, stateCd);
                ps.setInt(4, offCd);
                ps.setInt(5, seriesId);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    String vhmNxtPre = rs.getString("next_prefix_series");
                    sql = "SELECT prefix_series FROM " + TableList.VM_REGN_SERIES
                            + " WHERE prefix_series in (?) and state_cd=? and off_cd=? and series_id=?  ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, vhmNxtPre.trim());
                    ps.setString(2, stateCd);
                    ps.setInt(3, offCd);
                    ps.setInt(4, seriesId);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (!rs.next()) {
                        existedPrefix = " Prefix Series [ " + rs.getString("prefix_series") + " ]";
                    }
                }
            }
        } catch (SQLException e) {
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
        return existedPrefix;
    }

    public String validateNextPrefixSeries(String prefix, String nextPrefix, String stateCd, int offCd, int seriesId) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        String existedNextPrefix = null;
        RowSet rs = null;
        int flagflagSeriesDeleted = 1;
        try {
            prefix = prefix.toUpperCase();
            nextPrefix = nextPrefix.toUpperCase();
            sql = "SELECT next_prefix_series FROM " + TableList.VM_REGN_SERIES
                    + " WHERE next_prefix_series in (?) and state_cd=? and off_cd=?  limit 1";

            tmgr = new TransactionManager("validateNextPrefixSeries");

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, nextPrefix);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);

            rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist
                existedNextPrefix = "Next Prefix Series [ " + rs.getString("next_prefix_series") + " ]";
            } else {
                sql = "SELECT next_prefix_series FROM " + TableList.VHM_REGN_SERIES
                        + " WHERE next_prefix_series in (?) and state_cd=? and off_cd=?  and  flag not in (" + flagflagSeriesDeleted + ")  and next_prefix_series is not null and trim(next_prefix_series) <> '' limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, nextPrefix.trim());
                ps.setString(2, stateCd);
                ps.setInt(3, offCd);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    existedNextPrefix = " Next Prefix Series [ " + rs.getString("next_prefix_series") + " ]";
                } else {
                    sql = "SELECT prefix_series FROM " + TableList.VM_REGN_SERIES
                            + " WHERE prefix_series in (?) and state_cd=? and off_cd=?  limit 1";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, nextPrefix.trim());
                    ps.setString(2, stateCd);
                    ps.setInt(3, offCd);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        existedNextPrefix = " Next Prefix Series [ " + rs.getString("prefix_series") + " ]";
                    } else {
                        sql = "SELECT prefix_series FROM " + TableList.VHM_REGN_SERIES
                                + " WHERE prefix_series in (?) and state_cd=? and off_cd=?  and  flag not in (" + flagflagSeriesDeleted + ")  and next_prefix_series is not null and trim(next_prefix_series) <> '' limit 1";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, nextPrefix.trim());
                        ps.setString(2, stateCd);
                        ps.setInt(3, offCd);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            existedNextPrefix = " Next Prefix Series [ " + rs.getString("prefix_series") + " ]";
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong during check of whether the next prefix is existed or not");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return existedNextPrefix;
    }

    public boolean isRunningNoIsFancyNo(String runningNo, String stateCd) throws VahanException {

        boolean isFancy = false;
        String sql = "SELECT fancy_number from " + TableList.VM_FANCY_MAST + " where state_cd =? and fancy_number=?";
        int pos = 1;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("Regn_series_Impl:isRunningNoIsFancyNo");
            PreparedStatement pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(pos++, stateCd);
            pstmt.setString(pos++, CommonUtils.formatNumberPart(runningNo));
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isFancy = true;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Check of Running No is Fancy No, Please Contact to the System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return isFancy;
    }

    public void saveRegnSeriesintoVa(Regn_series_dobj dobj) throws VahanException, Exception {
        VahanException e = null;
        TransactionManager tmgr = null;
        String sql = null;
        PreparedStatement pstmt = null;
        RowSet rs = null;
        int i = 1;

        try {
            tmgr = new TransactionManager("Regn_series_Impl:saveRegnSeriesintoVa");
            sql = "select * from " + TableList.VA_REGN_SERIES + " where state_cd= ? and off_cd=? and series_id=?";
            pstmt = tmgr.prepareStatement(sql);

            pstmt.setString(i++, dobj.getState_cd());
            pstmt.setInt(i++, dobj.getOff_cd());
            pstmt.setInt(i++, dobj.getSeries_id());
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                insertIntoHistory(tmgr, dobj);
                sql = "UPDATE " + TableList.VA_REGN_SERIES
                        + " SET  prefix_series=?, lower_range_no=?, "
                        + " upper_range_no=?, running_no=?, next_prefix_series=?, "
                        + " entered_by=?, entered_on=? "
                        + " WHERE series_id=? and state_cd=? and off_cd=?";
                pstmt = tmgr.prepareStatement(sql);
                i = 1;

                pstmt.setString(i++, dobj.getPrefix_series());
                pstmt.setInt(i++, dobj.getLower_range_no());
                pstmt.setInt(i++, dobj.getUpper_range_no());
                pstmt.setInt(i++, dobj.getRunning_no());
                if (dobj.getNext_prefix_series() == null || dobj.getNext_prefix_series().trim().equalsIgnoreCase("")) {
                    pstmt.setNull(i++, java.sql.Types.VARCHAR);
                } else {
                    pstmt.setString(i++, dobj.getNext_prefix_series());
                }
                pstmt.setString(i++, dobj.getEntered_by());
                pstmt.setTimestamp(i++, ServerUtil.getSystemDateInPostgres());
                pstmt.setInt(i++, dobj.getSeries_id());
                pstmt.setString(i++, dobj.getState_cd());
                pstmt.setInt(i++, dobj.getOff_cd());
                pstmt.executeUpdate();
            } else {
                sql = "INSERT INTO " + TableList.VA_REGN_SERIES
                        + "             (series_id, criteria_formula, prefix_series, lower_range_no, upper_range_no, "
                        + "              running_no, next_prefix_series, no_gen_type, entered_by, entered_on, "
                        + "              state_cd, off_cd,action_type) "
                        + "              VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                pstmt = tmgr.prepareStatement(sql);

                i = 1;
                pstmt.setInt(i++, dobj.getSeries_id());
                pstmt.setString(i++, dobj.getCriteria_formula());
                pstmt.setString(i++, dobj.getPrefix_series());
                pstmt.setInt(i++, dobj.getLower_range_no());
                pstmt.setInt(i++, dobj.getUpper_range_no());
                pstmt.setInt(i++, dobj.getRunning_no());
                pstmt.setString(i++, dobj.getNext_prefix_series());
                pstmt.setString(i++, dobj.getNo_gen_type());
                pstmt.setString(i++, Util.getEmpCode());
                pstmt.setTimestamp(i++, ServerUtil.getSystemDateInPostgres());
                pstmt.setString(i++, dobj.getState_cd());
                pstmt.setInt(i++, dobj.getOff_cd());
                pstmt.setString(i++, dobj.getAction_type());
                ServerUtil.validateQueryResult(tmgr, pstmt.executeUpdate(), pstmt);
            }
            tmgr.commit();
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = new VahanException("Problem in Series Saving,Please Contact to Administrator");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                e = new VahanException("Problem in Series Saving,Please Contact to Administrator");
            }
        }
        if (e != null) {
            throw e;
        }

    }

    public List<Regn_series_dobj> getRecordfromVa() throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        RowSet rs = null;
        Regn_series_dobj dobj = null;
        List<Regn_series_dobj> list = new ArrayList<>();
        String getlistrecord = " select series.descr as criteria_desc,  vm_regnsrs.series_id ,vm_regnsrs.criteria_formula ,prefix_series ,"
                + " lower_range_no ,upper_range_no ,running_no ,next_prefix_series , "
                + " no_gen_type ,entered_by ,entered_on ,vm_regnsrs.state_cd ,vm_regnsrs.off_cd,action_type "
                + "  FROM " + TableList.VA_REGN_SERIES + " vm_regnsrs"
                + " inner join " + TableList.VM_SERIES + " series on trim(series.criteria_formula)=trim(vm_regnsrs.criteria_formula) and "
                + " series.state_cd=vm_regnsrs.state_cd and series.series_id=vm_regnsrs.series_id "
                + " where vm_regnsrs.state_cd =? and vm_regnsrs.off_cd=? ";

        try {
            tmgr = new TransactionManager("getRecordfromVa");
            ps = tmgr.prepareStatement(getlistrecord);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserOffCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new Regn_series_dobj();
                dobj.setSeries_id(rs.getInt("series_id"));
                dobj.setCriteria_formula_descr(rs.getString("criteria_desc"));
                dobj.setCriteria_formula(rs.getString("criteria_formula"));
                dobj.setPrefix_series(rs.getString("prefix_series"));
                dobj.setLower_range_no(rs.getInt("lower_range_no"));
                dobj.setUpper_range_no(rs.getInt("upper_range_no"));
                dobj.setRunning_no(rs.getInt("running_no"));
                dobj.setNext_prefix_series(rs.getString("next_prefix_series"));
                dobj.setNo_gen_type(rs.getString("no_gen_type"));
                dobj.setEntered_by(rs.getString("entered_by"));
                dobj.setEntered_on(rs.getString("entered_on"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setAction_type(rs.getString("action_type"));
                dobj.setIsApprovebutton(true);
                switch (String.valueOf(rs.getInt("running_no")).length()) {
                    case 1:
                        dobj.setRegn_series_format(rs.getString("prefix_series") + "-000" + String.valueOf(rs.getInt("running_no")));
                        break;
                    case 2:
                        dobj.setRegn_series_format(rs.getString("prefix_series") + "-00" + String.valueOf(rs.getInt("running_no")));
                        break;
                    case 3:
                        dobj.setRegn_series_format(rs.getString("prefix_series") + "-0" + String.valueOf(rs.getInt("running_no")));
                        break;
                    default:
                        dobj.setRegn_series_format("");
                        break;
                }
                list.add(dobj);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem in getting data");
        }
        return list;
    }

    public void deleteRecordFromVa(Regn_series_dobj dobj) throws VahanException {
        String deleteQuery = " delete from " + TableList.VA_REGN_SERIES + " where series_id=? and state_cd=? and off_cd=?";
        VahanException e = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("Regn_series_Impl:deleteRecordFromVa");
            PreparedStatement pstmt = tmgr.prepareStatement(deleteQuery);
            pstmt.setInt(1, dobj.getSeries_id());
            pstmt.setString(2, dobj.getState_cd());
            pstmt.setInt(3, dobj.getOff_cd());
            pstmt.executeUpdate();
            tmgr.commit();

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = new VahanException("Problem in deleting data");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                e = new VahanException("Problem in deleting data");
            }
        }
        if (e != null) {
            throw e;
        }
    }
}
