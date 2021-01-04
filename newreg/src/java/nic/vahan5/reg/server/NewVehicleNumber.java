/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.server;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.sql.RowSet;
import nic.java.util.CommonUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.AuctionDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.InspectionDobj;
import nic.vahan.form.dobj.NumberDetail_dobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.AuctionImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.OwnerChoiceNoImpl;
import nic.vahan.form.impl.Rereg_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import nic.vahan5.reg.commonutils.FormulaUtilities;
import nic.vahan5.reg.form.impl.Utility;

/**
 *
 * @author Kartikey Singh
 */
public class NewVehicleNumber {

    private static Logger LOGGER = Logger.getLogger(NewVehicleNumber.class);

    public static String switchToNextSeriesKl(TransactionManager tmgr, String stateCd, int offCd, int seriesId) throws SQLException, VahanException {
        String newPrefix = null;
        PreparedStatement ps = null;
        String sql = null;

        sql = "update vm_regn_series set running_no = running_no where series_id=? and state_cd=? and off_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setInt(1, seriesId);
        ps.setString(2, Util.getUserStateCode());
        ps.setInt(3, offCd);
        int updatedRows = ps.executeUpdate();

        if (updatedRows == 0) {
            //Insert fresh case as No Valid slab found

            sql = "insert into vm_regn_series SELECT a.series_id,"
                    + " b.criteria_formula,"
                    + " a.prefix_series, a.lower_range_no, a.upper_range_no,a.running_no,?, "
                    + "	   c.regn_gen_type,"
                    + "	   ?,"
                    + "	   current_timestamp, "
                    + "	   a.state_cd, a.off_cd"
                    + "  FROM "
                    + "  vm_regn_series_future a,vm_series b,tm_configuration c"
                    + "  where "
                    + "  a.state_cd=b.state_cd and a.series_id=b.series_id and a.state_cd=c.state_cd and a.state_cd=? and a.off_cd=? and a.series_id=? order by a.entered_on limit 1";

            ps = tmgr.prepareStatement(sql);
            ps.setNull(1, java.sql.Types.VARCHAR);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, stateCd);
            ps.setInt(4, offCd);
            ps.setInt(5, seriesId);
            ps.executeUpdate();

            ps = tmgr.prepareStatement("select prefix_series from vm_regn_series a "
                    + " where  series_id=? and state_cd=? and off_cd=?");
            ps.setInt(1, seriesId);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            RowSet rsNextSeries = tmgr.fetchDetachedRowSet_No_release();
            if (rsNextSeries.next()) {
                newPrefix = rsNextSeries.getString("prefix_series");
            }

            sql = "insert into vhm_regn_series_future select current_timestamp,?, a.* from vm_regn_series_future a"
                    + " where state_cd=? and off_cd=? and series_id=? order by entered_on  limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.setInt(4, seriesId);
            ps.executeUpdate();


            sql = "delete "
                    + " from "
                    + "  vm_regn_series_future a where a.state_cd=? and a.off_cd=? and a.series_id=? "
                    + " and entered_on = (select min(entered_on) from vm_regn_series_future b where a.state_cd=b.state_cd and a.off_cd=b.off_cd and a.series_id=b.series_id)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setInt(3, seriesId);
            ps.executeUpdate();



        } else {
            //insert into history table.
            sql = "insert into vhm_regn_series (select series_id, criteria_formula, prefix_series, lower_range_no, upper_range_no, running_no, next_prefix_series, no_gen_type, entered_by,entered_on,? ,statement_timestamp(),state_cd, off_cd from vm_regn_series where series_id=? and state_cd=? and off_cd=?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setInt(2, seriesId);
            ps.setString(3, Util.getUserStateCode());
            ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            ps.executeUpdate();


            sql = "update  vm_regn_series "
                    + " set "
                    + " prefix_series=b.prefix_series,"
                    + " lower_range_no=b.lower_range_no,"
                    + " upper_range_no=b.upper_range_no,"
                    + " running_no =b.running_no,entered_on=current_timestamp "
                    + " from "
                    + "  (select * from vm_regn_series_future where state_cd=? and off_cd=? and series_id=? order by entered_on limit 1) b"
                    + " where vm_regn_series.state_cd=b.state_cd and vm_regn_series.off_cd=b.off_cd and vm_regn_series.series_id=b.series_id;";
            ps = tmgr.prepareStatement(sql);

            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setInt(3, seriesId);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                throw new VahanException("Registration No Not Availaible in Current/Next Available Series ");
            }

            ps = tmgr.prepareStatement("select prefix_series from vm_regn_series a "
                    + " where  series_id=? and state_cd=? and off_cd=?");
            ps.setInt(1, seriesId);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            RowSet rsNextSeries = tmgr.fetchDetachedRowSet_No_release();
            if (rsNextSeries.next()) {
                newPrefix = rsNextSeries.getString("prefix_series");
            }


            sql = "insert into vhm_regn_series_future select current_timestamp,?, a.* from vm_regn_series_future a"
                    + " where state_cd=? and off_cd=? and series_id=? order by entered_on  limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.setInt(4, seriesId);
            ps.executeUpdate();


            sql = "delete "
                    + "from"
                    + "  vm_regn_series_future a where a.state_cd=? and a.off_cd=? and a.series_id=? "
                    + " and entered_on = (select min(entered_on) from vm_regn_series_future b where a.state_cd=b.state_cd and a.off_cd=b.off_cd and a.series_id=b.series_id)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setInt(3, seriesId);
            ps.executeUpdate();

        }

        if (CommonUtils.isNullOrBlank(newPrefix)) {
            throw new VahanException("Error in Getting Series For Number Generation");
        }


        return newPrefix;
    }

    /**
     * @author Kartikey Singh
     */
    public static String switchToNextSeriesKl(TransactionManager tmgr, String stateCd, int selectedOffCode, int seriesId,
            String empCode) throws SQLException, VahanException {
        String newPrefix = null;
        PreparedStatement ps = null;
        String sql = null;

        sql = "update vm_regn_series set running_no = running_no where series_id=? and state_cd=? and off_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setInt(1, seriesId);
        ps.setString(2, stateCd);
        ps.setInt(3, selectedOffCode);
        int updatedRows = ps.executeUpdate();

        if (updatedRows == 0) {
            //Insert fresh case as No Valid slab found

            sql = "insert into vm_regn_series SELECT a.series_id,"
                    + " b.criteria_formula,"
                    + " a.prefix_series, a.lower_range_no, a.upper_range_no,a.running_no,?, "
                    + "	   c.regn_gen_type,"
                    + "	   ?,"
                    + "	   current_timestamp, "
                    + "	   a.state_cd, a.off_cd"
                    + "  FROM "
                    + "  vm_regn_series_future a,vm_series b,tm_configuration c"
                    + "  where "
                    + "  a.state_cd=b.state_cd and a.series_id=b.series_id and a.state_cd=c.state_cd and a.state_cd=? and a.off_cd=? and a.series_id=? order by a.entered_on limit 1";

            ps = tmgr.prepareStatement(sql);
            ps.setNull(1, java.sql.Types.VARCHAR);
            ps.setString(2, empCode);
            ps.setString(3, stateCd);
            ps.setInt(4, selectedOffCode);
            ps.setInt(5, seriesId);
            ps.executeUpdate();

            ps = tmgr.prepareStatement("select prefix_series from vm_regn_series a "
                    + " where  series_id=? and state_cd=? and off_cd=?");
            ps.setInt(1, seriesId);
            ps.setString(2, stateCd);
            ps.setInt(3, selectedOffCode);
            RowSet rsNextSeries = tmgr.fetchDetachedRowSet_No_release();
            if (rsNextSeries.next()) {
                newPrefix = rsNextSeries.getString("prefix_series");
            }

            sql = "insert into vhm_regn_series_future select current_timestamp,?, a.* from vm_regn_series_future a"
                    + " where state_cd=? and off_cd=? and series_id=? order by entered_on  limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, stateCd);
            ps.setInt(3, selectedOffCode);
            ps.setInt(4, seriesId);
            ps.executeUpdate();

            sql = "delete "
                    + " from "
                    + "  vm_regn_series_future a where a.state_cd=? and a.off_cd=? and a.series_id=? "
                    + " and entered_on = (select min(entered_on) from vm_regn_series_future b where a.state_cd=b.state_cd and a.off_cd=b.off_cd and a.series_id=b.series_id)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, selectedOffCode);
            ps.setInt(3, seriesId);
            ps.executeUpdate();

        } else {
            //insert into history table.
            sql = "insert into vhm_regn_series (select series_id, criteria_formula, prefix_series, lower_range_no, upper_range_no, running_no, next_prefix_series, no_gen_type, entered_by,entered_on,? ,statement_timestamp(),state_cd, off_cd from vm_regn_series where series_id=? and state_cd=? and off_cd=?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setInt(2, seriesId);
            ps.setString(3, stateCd);
            ps.setInt(4, selectedOffCode);
            ps.executeUpdate();

            sql = "update  vm_regn_series "
                    + " set "
                    + " prefix_series=b.prefix_series,"
                    + " lower_range_no=b.lower_range_no,"
                    + " upper_range_no=b.upper_range_no,"
                    + " running_no =b.running_no,entered_on=current_timestamp "
                    + " from "
                    + "  (select * from vm_regn_series_future where state_cd=? and off_cd=? and series_id=? order by entered_on limit 1) b"
                    + " where vm_regn_series.state_cd=b.state_cd and vm_regn_series.off_cd=b.off_cd and vm_regn_series.series_id=b.series_id;";
            ps = tmgr.prepareStatement(sql);

            ps.setString(1, stateCd);
            ps.setInt(2, selectedOffCode);
            ps.setInt(3, seriesId);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                throw new VahanException("Registration No Not Availaible in Current/Next Available Series ");
            }

            ps = tmgr.prepareStatement("select prefix_series from vm_regn_series a "
                    + " where  series_id=? and state_cd=? and off_cd=?");
            ps.setInt(1, seriesId);
            ps.setString(2, stateCd);
            ps.setInt(3, selectedOffCode);
            RowSet rsNextSeries = tmgr.fetchDetachedRowSet_No_release();
            if (rsNextSeries.next()) {
                newPrefix = rsNextSeries.getString("prefix_series");
            }


            sql = "insert into vhm_regn_series_future select current_timestamp,?, a.* from vm_regn_series_future a"
                    + " where state_cd=? and off_cd=? and series_id=? order by entered_on  limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, stateCd);
            ps.setInt(3, selectedOffCode);
            ps.setInt(4, seriesId);
            ps.executeUpdate();


            sql = "delete "
                    + "from"
                    + "  vm_regn_series_future a where a.state_cd=? and a.off_cd=? and a.series_id=? "
                    + " and entered_on = (select min(entered_on) from vm_regn_series_future b where a.state_cd=b.state_cd and a.off_cd=b.off_cd and a.series_id=b.series_id)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, selectedOffCode);
            ps.setInt(3, seriesId);
            ps.executeUpdate();

        }

        if (CommonUtils.isNullOrBlank(newPrefix)) {
            throw new VahanException("Error in Getting Series For Number Generation");
        }

        return newPrefix;
    }

    public static String getNewVehicleNumberFormulaBased(TransactionManager tmgr, String appl_no,
            String regn_no_type, int offCd, String regnNo)
            throws Exception {

        String newRegNo = null;
        String tempAlpha;
        String tempSeri;
        String tempcat;
        int num = 0;
        Exception e = null;
        boolean oldEven = false, oldOdd = false;

        try {
            String sql = null;
            Map map = FormulaUtils.regn_no_records(appl_no, regnNo, tmgr);
            int series_id = Integer.parseInt(map.get("series_id").toString());
            String prefixSeries = map.get("prefix_series").toString();
            PreparedStatement ps = null;
            // to lock the record only
            sql = "update vm_regn_series set running_no = running_no where series_id=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, series_id);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, offCd);
            ps.executeUpdate();
            if (regn_no_type.equals(TableConstants.NO_GEN_TYPE_RAND) || regn_no_type.equals(TableConstants.NO_GEN_TYPE_SEQ)) {
                boolean recordVmRegnAvail = false;
                RowSet rs = null;
                while (true) {

//                    sql = "select  regn_no,status from  " + TableList.VM_REGN_AVAILABLE
//                            + " where  state_cd=? and status in('A','F') "
//                            + " and off_cd=?  and series_id=? order by  1 LIMIT 1";
//
//                    ps = tmgr.prepareStatement(sql);
//                    ps.setString(1, Util.getUserStateCode());
//                    ps.setInt(2, Util.getSelectedSeat().getOff_cd());
//                    ps.setInt(3, series_id);
//                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();
//                    if (rs.next()) {
//                        //Number is generated skip the while loop
//                        newRegNo = rs.getString("regn_no");
//
//                        sql = "Delete from " + TableList.VM_REGN_AVAILABLE + " where  state_cd=? and status in ('A','F') "
//                                + " and off_cd=? and regn_no=? and series_id=? ";
//                        ps = tmgr.prepareStatement(sql);
//                        ps.setString(1, Util.getUserStateCode());
//                        ps.setInt(2, Util.getSelectedSeat().getOff_cd());
//                        ps.setString(3, newRegNo);
//                        ps.setInt(4, series_id);
//                        ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
//                        boolean skipFancy = true;
//                        /**
//                         * For Fancy Numbers are used as Ordinary Numbers in TN
//                         */
//                        if ("TN,KL".contains(Util.getUserStateCode()) && rs.getString("status").equals("F")) {
//                            skipFancy = false;
//                        }
//
//                        if (checkNumberAssignable(newRegNo, skipFancy, tmgr)) {
//                            recordVmRegnAvail = true;
//                            break;
//                        } else {
//                            continue;
//                        }
//                    }


                    ps = tmgr.prepareStatement("select a.*,b.skip_fancy_no from vm_regn_series a,vm_series b"
                            + " where a.series_id=? and a.running_no <= a.upper_range_no "
                            + " and a.state_cd=? and a.off_cd=? and a.state_cd=b.state_cd and a.series_id=b.series_id ");
                    ps.setInt(1, series_id);
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, offCd);
                    rs = tmgr.fetchDetachedRowSet_No_release();

                    if (rs.next()) {

                        tempAlpha = rs.getString("prefix_series").trim();
                        boolean skipFancy = rs.getBoolean("skip_fancy_no");
                        num = rs.getInt("running_no");
                        newRegNo = tempAlpha + CommonUtils.formatNumberPart(num + "");

                        if (checkNumberAssignable(newRegNo, skipFancy, tmgr)) {
                            break;
                        } else {
                            sql = "update vm_regn_series set running_no = running_no+1 where series_id=? and state_cd=? and off_cd=?";
                            ps = tmgr.prepareStatement(sql);
                            ps.setInt(1, series_id);
                            ps.setString(2, Util.getUserStateCode());
                            ps.setInt(3, offCd);
                            ps.executeUpdate();
                            continue;
                        }

                    } else {
                        /// logic to switch to next series if it is available......
                        if ("KL,TN".contains(Util.getUserStateCode())) {
                            switchToNextSeriesKl(tmgr, Util.getUserStateCode(), offCd, series_id);
                        } else {
                            switchToNewSeries(series_id, tmgr);
                        }
                        continue;
                    }

                }
                if (!recordVmRegnAvail) {
                    sql = "update vm_regn_series set running_no = running_no+1 where series_id=? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setInt(1, series_id);
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, offCd);
                    ps.executeUpdate();
                }
            } else if (regn_no_type.equals(TableConstants.NO_GEN_TYPE_MIX)) {
                while (true) {
                    if (Util.getUserStateCode().equalsIgnoreCase("HR")) {
                        String oldRegnNo = new Rereg_Impl().oddEvenOpted(appl_no, tmgr);
                        if (!CommonUtils.isNullOrBlank(oldRegnNo)) {
                            if (JSFUtils.isRegnNoEven(oldRegnNo)) {
                                oldEven = true;
                            } else {
                                oldOdd = true;
                            }
                            if (oldEven) {
                                sql = "select  regn_no from  " + TableList.VM_REGN_AVAILABLE + " where status='A' and "
                                        + "state_cd=? and off_cd=?  and series_id=?  "
                                        + "and (SUBSTR(regn_no, LENGTH(regn_no))::numeric % 2 !=0) order by  RANDOM() LIMIT 1";
                            } else if (oldOdd) {
                                sql = "select  regn_no from  " + TableList.VM_REGN_AVAILABLE + " where status='A' and state_cd=? "
                                        + "and off_cd=?  and series_id=?  "
                                        + "and (SUBSTR(regn_no, LENGTH(regn_no))::numeric % 2 =0) order by  RANDOM() LIMIT 1";
                            }
                        } else {
                            sql = "select  regn_no from  " + TableList.VM_REGN_AVAILABLE
                                    + " where status='A' and state_cd=? "
                                    + " and off_cd=?  and series_id=? order by  RANDOM() LIMIT 1";
                        }
                    } else {
                        sql = "select  regn_no,status from  " + TableList.VM_REGN_AVAILABLE
                                + " where  state_cd=? and status in('A','F') "
                                + " and off_cd=?  and series_id=? order by  RANDOM() LIMIT 1";
                    }
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                    ps.setInt(3, series_id);
                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        //Number is generated skip the while loop
                        newRegNo = rs.getString("regn_no");

                        sql = "Delete from " + TableList.VM_REGN_AVAILABLE + " where  state_cd=? and status in ('A','F') "
                                + " and off_cd=? and regn_no=? and series_id=? ";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, Util.getUserStateCode());
                        ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                        ps.setString(3, newRegNo);
                        ps.setInt(4, series_id);
                        ps.executeUpdate();
                        boolean skipFancy = true;
                        /**
                         * For Fancy Numbers are used as Ordinary Numbers in TN
                         */
                        if ("TN,KL".contains(Util.getUserStateCode()) && rs.getString("status").equals("F")) {
                            skipFancy = false;
                        }

                        if (checkNumberAssignable(newRegNo, skipFancy, tmgr)) {
                            break;
                        } else {
                            continue;
                        }
                    }

                    int MAXBATCH = 0;
                    int NEWRUNNO = 0;

                    sql = "select  regn_gen_random_batch from  " + TableList.TM_CONFIGURATION
                            + " where regn_gen_type=? and state_cd=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, TableConstants.NO_GEN_TYPE_MIX);
                    ps.setString(2, Util.getUserStateCode());

                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        int run_batch = rs.getInt("regn_gen_random_batch");

                        ps = tmgr.prepareStatement("select * from vm_regn_series where series_id=?  "
                                + " and state_cd=? and off_cd=? and running_no <= upper_range_no");
                        ps.setInt(1, series_id);
                        ps.setString(2, Util.getUserStateCode());
                        ps.setInt(3, offCd);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            String prefix_series = rs.getString("prefix_series");
                            int run_no = rs.getInt("running_no");
                            int upper_range_no = rs.getInt("upper_range_no");

                            if (run_no + run_batch == upper_range_no) {
                                MAXBATCH = upper_range_no;
                                //Series Expired
                                NEWRUNNO = upper_range_no + 1;
                            } else if (run_no + run_batch > upper_range_no) {
                                MAXBATCH = upper_range_no;
                                NEWRUNNO = MAXBATCH + 1;
                            } else if (run_no + run_batch < upper_range_no) {
                                MAXBATCH = run_no + run_batch - 1;
                                NEWRUNNO = MAXBATCH + 1;
                            }


                            sql = "INSERT INTO " + TableList.VM_REGN_AVAILABLE
                                    + " SELECT ? as state_cd, ? as off_cd,regn_no,status,"
                                    + " booking_fee as amount,? as entered_by,current_timestamp as entered_on ,? as prefix_series,? as series_id "
                                    + " FROM getregnnoavailable(?,?,?,?,?)";

                            ps = tmgr.prepareStatement(sql);
                            int pos = 1;
                            ps.setString(pos++, Util.getUserStateCode());
                            ps.setInt(pos++, Util.getSelectedSeat().getOff_cd());
                            ps.setString(pos++, Util.getEmpCode());
                            ps.setString(pos++, prefix_series);
                            ps.setInt(pos++, series_id);
                            ps.setString(pos++, Util.getUserStateCode());
                            ps.setInt(pos++, Util.getSelectedSeat().getOff_cd());
                            ps.setString(pos++, prefix_series);
                            ps.setInt(pos++, run_no);
                            ps.setInt(pos++, MAXBATCH);
                            ps.executeUpdate();


                            sql = "update vm_regn_series set running_no = ? where series_id=? and state_cd=? and off_cd=?";
                            ps = tmgr.prepareStatement(sql);
                            ps.setInt(1, NEWRUNNO);
                            ps.setInt(2, series_id);
                            ps.setString(3, Util.getUserStateCode());
                            ps.setInt(4, offCd);
                            ps.executeUpdate();

                        } else {

                            ps = tmgr.prepareStatement("select * from vm_regn_series where series_id=?  "
                                    + " and state_cd=? and off_cd=? ");
                            ps.setInt(1, series_id);
                            ps.setString(2, Util.getUserStateCode());
                            ps.setInt(3, offCd);
                            rs = tmgr.fetchDetachedRowSet_No_release();

                            if (rs.next()) {
                                if ("TN".contains(Util.getUserStateCode())) {
                                    //For TN after series completion,fancy numbers are treated as fancy Numbers
                                    sql = "INSERT INTO " + TableList.VM_REGN_AVAILABLE
                                            + " SELECT ? as state_cd, ? as off_cd,?||fancy_number as regn_no ,?,"
                                            + " 0,? as entered_by,current_timestamp as entered_on ,? as prefix_series,? as series_id "
                                            + " FROM vm_fancy_mast  where state_cd=? and sumOfDigits(fancy_number::int) !=8";

                                    ps = tmgr.prepareStatement(sql);
                                    int pos = 1;
                                    ps.setString(pos++, Util.getUserStateCode());
                                    ps.setInt(pos++, Util.getSelectedSeat().getOff_cd());
                                    ps.setString(pos++, prefixSeries);//Registration Part Prefix series
                                    ps.setString(pos++, "F");
                                    ps.setString(pos++, Util.getEmpCode());
                                    ps.setString(pos++, prefixSeries);
                                    ps.setInt(pos++, series_id);
                                    ps.setString(pos++, Util.getUserStateCode());
                                    ps.executeUpdate();
                                }
                            }
                            //Series Expired prefixSeries
                            if ("KL,TN".contains(Util.getUserStateCode())) {
                                switchToNextSeriesKl(tmgr, Util.getUserStateCode(), offCd, series_id);
                            } else {
                                prefixSeries = switchToNewSeries(series_id, tmgr);
                            }


                        }
                    } else {
                        throw new VahanException("Please Add configuration information for Number Generation ");
                    }

                }
            } else if (regn_no_type.equals(TableConstants.NO_GEN_TYPE_MIX_P)) {
                sql = "select  regn_no from  vm_regn_alloted "
                        + " where appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    //Number is generated skip the while loop
                    newRegNo = rs.getString("regn_no");

                    sql = "Update " + TableList.VM_REGN_AVAILABLE + " set regn_no=?  where status='A' and state_cd=? "
                            + " and off_cd=? and regn_no=? and series_id=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, newRegNo);
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                    ps.setString(4, newRegNo);
                    ps.setInt(5, series_id);
                    int count = ps.executeUpdate();
                    if (count <= 0) {
                        throw new VahanException("Number is no longer available");
                    }
                    sql = "Delete from " + TableList.VM_REGN_AVAILABLE + " where status='A' and state_cd=? "
                            + " and off_cd=? and regn_no=? and series_id=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                    ps.setString(3, newRegNo);
                    ps.setInt(4, series_id);
                    ps.executeUpdate();
                    sql = "Delete from   vm_regn_alloted "
                            + " where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, appl_no);
                    ps.executeUpdate();
                    boolean skipFancy = true;
                    if (!checkNumberAssignable(newRegNo, skipFancy, tmgr)) {
                        throw new VahanException("Selected Vehicle Number is either Fancy or Blocked");
                    }
                }

            }
            sql = "insert into vm_regn_alloted (state_cd, off_cd, regn_no, appl_no) values (?, ?, ?, ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, offCd);
            ps.setString(3, newRegNo);
            ps.setString(4, appl_no);
            ps.executeUpdate();

        } catch (VahanException vx) {
            throw vx;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = ex;
            throw new VahanException("Registration Number Generation Failed ");
        }
        if (e != null) {
            throw new VahanException(e.getMessage());
        }

        if (newRegNo == null || newRegNo.length() < 8) {
            throw new VahanException("Invalid Registration No Generated!!!");
        }

        return newRegNo;

    }

    /**
     * @author Kartikey Singh
     */
    public static String getNewVehicleNumberFormulaBased(TransactionManager tmgr, String appl_no, String regn_no_type, int selectedOffCode, String regnNo,
            String stateCode, String empCode, int actionCode, int userLoginOffCode)
            throws Exception {

        String newRegNo = null;
        String tempAlpha;
        String tempSeri;
        String tempcat;
        int num = 0;
        Exception e = null;
        boolean oldEven = false, oldOdd = false;

        try {
            String sql = null;
            Map map = FormulaUtilities.regn_no_records(appl_no, regnNo, tmgr, stateCode, selectedOffCode, actionCode, userLoginOffCode);
            int series_id = Integer.parseInt(map.get("series_id").toString());
            String prefixSeries = map.get("prefix_series").toString();
            PreparedStatement ps = null;
            // to lock the record only
            sql = "update vm_regn_series set running_no = running_no where series_id=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, series_id);
            ps.setString(2, stateCode);
            ps.setInt(3, selectedOffCode);
            ps.executeUpdate();
            if (regn_no_type.equals(TableConstants.NO_GEN_TYPE_RAND) || regn_no_type.equals(TableConstants.NO_GEN_TYPE_SEQ)) {
                boolean recordVmRegnAvail = false;
                RowSet rs = null;
                while (true) {

                    ps = tmgr.prepareStatement("select a.*,b.skip_fancy_no from vm_regn_series a,vm_series b"
                            + " where a.series_id=? and a.running_no <= a.upper_range_no "
                            + " and a.state_cd=? and a.off_cd=? and a.state_cd=b.state_cd and a.series_id=b.series_id ");
                    ps.setInt(1, series_id);
                    ps.setString(2, stateCode);
                    ps.setInt(3, selectedOffCode);
                    rs = tmgr.fetchDetachedRowSet_No_release();

                    if (rs.next()) {

                        tempAlpha = rs.getString("prefix_series").trim();
                        boolean skipFancy = rs.getBoolean("skip_fancy_no");
                        num = rs.getInt("running_no");
                        newRegNo = tempAlpha + CommonUtils.formatNumberPart(num + "");

                        if (checkNumberAssignable(newRegNo, skipFancy, tmgr)) {
                            break;
                        } else {
                            sql = "update vm_regn_series set running_no = running_no+1 where series_id=? and state_cd=? and off_cd=?";
                            ps = tmgr.prepareStatement(sql);
                            ps.setInt(1, series_id);
                            ps.setString(2, stateCode);
                            ps.setInt(3, selectedOffCode);
                            ps.executeUpdate();
                            continue;
                        }

                    } else {
                        /// logic to switch to next series if it is available......
                        if ("KL,TN".contains(stateCode)) {
                            switchToNextSeriesKl(tmgr, stateCode, selectedOffCode, series_id, empCode);
                        } else {
                            switchToNewSeries(series_id, tmgr, stateCode, selectedOffCode, empCode);
                        }
                        continue;
                    }

                }
                if (!recordVmRegnAvail) {
                    sql = "update vm_regn_series set running_no = running_no+1 where series_id=? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setInt(1, series_id);
                    ps.setString(2, stateCode);
                    ps.setInt(3, selectedOffCode);
                    ps.executeUpdate();
                }
            } else if (regn_no_type.equals(TableConstants.NO_GEN_TYPE_MIX)) {
                while (true) {
                    if (stateCode.equalsIgnoreCase("HR")) {
                        String oldRegnNo = new Rereg_Impl().oddEvenOpted(appl_no, tmgr);
                        if (!CommonUtils.isNullOrBlank(oldRegnNo)) {
                            if (JSFUtils.isRegnNoEven(oldRegnNo)) {
                                oldEven = true;
                            } else {
                                oldOdd = true;
                            }
                            if (oldEven) {
                                sql = "select  regn_no from  " + TableList.VM_REGN_AVAILABLE + " where status='A' and "
                                        + "state_cd=? and off_cd=?  and series_id=?  "
                                        + "and (SUBSTR(regn_no, LENGTH(regn_no))::numeric % 2 !=0) order by  RANDOM() LIMIT 1";
                            } else if (oldOdd) {
                                sql = "select  regn_no from  " + TableList.VM_REGN_AVAILABLE + " where status='A' and state_cd=? "
                                        + "and off_cd=?  and series_id=?  "
                                        + "and (SUBSTR(regn_no, LENGTH(regn_no))::numeric % 2 =0) order by  RANDOM() LIMIT 1";
                            }
                        } else {
                            sql = "select  regn_no from  " + TableList.VM_REGN_AVAILABLE
                                    + " where status='A' and state_cd=? "
                                    + " and off_cd=?  and series_id=? order by  RANDOM() LIMIT 1";
                        }
                    } else {
                        sql = "select  regn_no,status from  " + TableList.VM_REGN_AVAILABLE
                                + " where  state_cd=? and status in('A','F') "
                                + " and off_cd=?  and series_id=? order by  RANDOM() LIMIT 1";
                    }
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, stateCode);
                    ps.setInt(2, selectedOffCode);
                    ps.setInt(3, series_id);
                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        //Number is generated skip the while loop
                        newRegNo = rs.getString("regn_no");

                        sql = "Delete from " + TableList.VM_REGN_AVAILABLE + " where  state_cd=? and status in ('A','F') "
                                + " and off_cd=? and regn_no=? and series_id=? ";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, stateCode);
                        ps.setInt(2, selectedOffCode);
                        ps.setString(3, newRegNo);
                        ps.setInt(4, series_id);
                        ps.executeUpdate();
                        boolean skipFancy = true;
                        /**
                         * For Fancy Numbers are used as Ordinary Numbers in TN
                         */
                        if ("TN,KL".contains(stateCode) && rs.getString("status").equals("F")) {
                            skipFancy = false;
                        }

                        if (checkNumberAssignable(newRegNo, skipFancy, tmgr, stateCode, selectedOffCode)) {
                            break;
                        } else {
                            continue;
                        }
                    }

                    int MAXBATCH = 0;
                    int NEWRUNNO = 0;

                    sql = "select  regn_gen_random_batch from  " + TableList.TM_CONFIGURATION
                            + " where regn_gen_type=? and state_cd=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, TableConstants.NO_GEN_TYPE_MIX);
                    ps.setString(2, stateCode);

                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        int run_batch = rs.getInt("regn_gen_random_batch");

                        ps = tmgr.prepareStatement("select * from vm_regn_series where series_id=?  "
                                + " and state_cd=? and off_cd=? and running_no <= upper_range_no");
                        ps.setInt(1, series_id);
                        ps.setString(2, stateCode);
                        ps.setInt(3, selectedOffCode);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            String prefix_series = rs.getString("prefix_series");
                            int run_no = rs.getInt("running_no");
                            int upper_range_no = rs.getInt("upper_range_no");

                            if (run_no + run_batch == upper_range_no) {
                                MAXBATCH = upper_range_no;
                                //Series Expired
                                NEWRUNNO = upper_range_no + 1;
                            } else if (run_no + run_batch > upper_range_no) {
                                MAXBATCH = upper_range_no;
                                NEWRUNNO = MAXBATCH + 1;
                            } else if (run_no + run_batch < upper_range_no) {
                                MAXBATCH = run_no + run_batch - 1;
                                NEWRUNNO = MAXBATCH + 1;
                            }


                            sql = "INSERT INTO " + TableList.VM_REGN_AVAILABLE
                                    + " SELECT ? as state_cd, ? as off_cd,regn_no,status,"
                                    + " booking_fee as amount,? as entered_by,current_timestamp as entered_on ,? as prefix_series,? as series_id "
                                    + " FROM getregnnoavailable(?,?,?,?,?)";

                            ps = tmgr.prepareStatement(sql);
                            int pos = 1;
                            ps.setString(pos++, stateCode);
                            ps.setInt(pos++, selectedOffCode);
                            ps.setString(pos++, empCode);
                            ps.setString(pos++, prefix_series);
                            ps.setInt(pos++, series_id);
                            ps.setString(pos++, stateCode);
                            ps.setInt(pos++, selectedOffCode);
                            ps.setString(pos++, prefix_series);
                            ps.setInt(pos++, run_no);
                            ps.setInt(pos++, MAXBATCH);
                            ps.executeUpdate();


                            sql = "update vm_regn_series set running_no = ? where series_id=? and state_cd=? and off_cd=?";
                            ps = tmgr.prepareStatement(sql);
                            ps.setInt(1, NEWRUNNO);
                            ps.setInt(2, series_id);
                            ps.setString(3, stateCode);
                            ps.setInt(4, selectedOffCode);
                            ps.executeUpdate();

                        } else {

                            ps = tmgr.prepareStatement("select * from vm_regn_series where series_id=?  "
                                    + " and state_cd=? and off_cd=? ");
                            ps.setInt(1, series_id);
                            ps.setString(2, stateCode);
                            ps.setInt(3, selectedOffCode);
                            rs = tmgr.fetchDetachedRowSet_No_release();

                            if (rs.next()) {
                                if ("TN".contains(stateCode)) {
                                    //For TN after series completion,fancy numbers are treated as fancy Numbers
                                    sql = "INSERT INTO " + TableList.VM_REGN_AVAILABLE
                                            + " SELECT ? as state_cd, ? as off_cd,?||fancy_number as regn_no ,?,"
                                            + " 0,? as entered_by,current_timestamp as entered_on ,? as prefix_series,? as series_id "
                                            + " FROM vm_fancy_mast  where state_cd=? and sumOfDigits(fancy_number::int) !=8";

                                    ps = tmgr.prepareStatement(sql);
                                    int pos = 1;
                                    ps.setString(pos++, stateCode);
                                    ps.setInt(pos++, selectedOffCode);
                                    ps.setString(pos++, prefixSeries);//Registration Part Prefix series
                                    ps.setString(pos++, "F");
                                    ps.setString(pos++, empCode);
                                    ps.setString(pos++, prefixSeries);
                                    ps.setInt(pos++, series_id);
                                    ps.setString(pos++, stateCode);
                                    ps.executeUpdate();
                                }
                            }
                            //Series Expired prefixSeries
                            if ("KL,TN".contains(stateCode)) {
                                switchToNextSeriesKl(tmgr, stateCode, selectedOffCode, series_id, empCode);
                            } else {
                                prefixSeries = switchToNewSeries(series_id, tmgr, stateCode, selectedOffCode, empCode);
                            }
                        }
                    } else {
                        throw new VahanException("Please Add configuration information for Number Generation ");
                    }

                }
            } else if (regn_no_type.equals(TableConstants.NO_GEN_TYPE_MIX_P)) {
                sql = "select  regn_no from  vm_regn_alloted "
                        + " where appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    //Number is generated skip the while loop
                    newRegNo = rs.getString("regn_no");

                    sql = "Update " + TableList.VM_REGN_AVAILABLE + " set regn_no=?  where status='A' and state_cd=? "
                            + " and off_cd=? and regn_no=? and series_id=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, newRegNo);
                    ps.setString(2, stateCode);
                    ps.setInt(3, selectedOffCode);
                    ps.setString(4, newRegNo);
                    ps.setInt(5, series_id);
                    int count = ps.executeUpdate();
                    if (count <= 0) {
                        throw new VahanException("Number is no longer available");
                    }
                    sql = "Delete from " + TableList.VM_REGN_AVAILABLE + " where status='A' and state_cd=? "
                            + " and off_cd=? and regn_no=? and series_id=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, stateCode);
                    ps.setInt(2, selectedOffCode);
                    ps.setString(3, newRegNo);
                    ps.setInt(4, series_id);
                    ps.executeUpdate();
                    sql = "Delete from   vm_regn_alloted "
                            + " where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, appl_no);
                    ps.executeUpdate();
                    boolean skipFancy = true;
                    if (!checkNumberAssignable(newRegNo, skipFancy, tmgr, stateCode, selectedOffCode)) {
                        throw new VahanException("Selected Vehicle Number is either Fancy or Blocked");
                    }
                }
            }
            sql = "insert into vm_regn_alloted (state_cd, off_cd, regn_no, appl_no) values (?, ?, ?, ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setInt(2, selectedOffCode);
            ps.setString(3, newRegNo);
            ps.setString(4, appl_no);
            ps.executeUpdate();

        } catch (VahanException vx) {
            throw vx;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = ex;
            throw new VahanException("Registration Number Generation Failed ");
        }
        if (e != null) {
            throw new VahanException(e.getMessage());
        }

        if (newRegNo == null || newRegNo.length() < 8) {
            throw new VahanException("Invalid Registration No Generated!!!");
        }
        return newRegNo;
    }

    private static String switchToNewSeries(int series_id, TransactionManager tmgr) throws VahanException, SQLException {
        String newPrefix = null;
        PreparedStatement ps = tmgr.prepareStatement("select prefix_series, next_prefix_series from vm_regn_series "
                + " where series_id=? and state_cd=? and off_cd=?");
        ps.setInt(1, series_id);
        ps.setString(2, Util.getUserStateCode());
        ps.setInt(3, Util.getSelectedSeat().getOff_cd());
        RowSet rsNextSeries = tmgr.fetchDetachedRowSet_No_release();
        String sql = null;
        if (rsNextSeries.next()) {
            newPrefix = rsNextSeries.getString("next_prefix_series");
            if (newPrefix == null || newPrefix.isEmpty() || newPrefix.length() < 4) {
                //
                TransactionManager tmgrNew = null;
                try {
                    tmgr.release();
                    tmgrNew = new TransactionManager("switchToNewSeries");
                    sql = "update vm_regn_series set running_no=10000 where series_id=? and state_cd=? and off_cd=? and running_no < 10000";
                    ps = tmgrNew.prepareStatement(sql);
                    ps.setInt(1, series_id);
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                    ps.executeUpdate();
                    //
                    tmgrNew.commit();
                    String errorMessage = "Registration no series [ " + rsNextSeries.getString("prefix_series") + " ] exhausted and next series is not available.";
                    throw new VahanException(errorMessage);
                } catch (VahanException ve) {
                    throw ve;
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException("Error In Switching to Next Series");
                } finally {
                    if (tmgrNew != null) {
                        try {
                            tmgrNew.release();
                        } catch (Exception ex) {
                            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                            throw new VahanException("Error In Switching to Next Series");
                        }
                    }
                }
            }

            newPrefix = newPrefix.toUpperCase();

            //insert into history table.
            sql = "insert into vhm_regn_series (select series_id, criteria_formula, prefix_series, lower_range_no, upper_range_no, running_no, next_prefix_series, no_gen_type, entered_by,entered_on,? ,current_timestamp,state_cd, off_cd from vm_regn_series where series_id=? and state_cd=? and off_cd=?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setInt(2, series_id);
            ps.setString(3, Util.getUserStateCode());
            ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            ps.executeUpdate();

            sql = "update vm_regn_series set running_no=lower_range_no,prefix_series=next_prefix_series,next_prefix_series=null where series_id=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, series_id);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            ps.executeUpdate();
        } else {
            String errorMessage = "Registration no series is exhausted and next series is not available.";
            throw new VahanException(errorMessage);
        }

        return newPrefix;

    }

    /**
     * @author Kartikey Singh
     */
    private static String switchToNewSeries(int series_id, TransactionManager tmgr, String stateCode, int selectedOffCode,
            String empCode) throws VahanException, SQLException {
        String newPrefix = null;
        PreparedStatement ps = tmgr.prepareStatement("select prefix_series, next_prefix_series from vm_regn_series "
                + " where series_id=? and state_cd=? and off_cd=?");
        ps.setInt(1, series_id);
        ps.setString(2, stateCode);
        ps.setInt(3, selectedOffCode);
        RowSet rsNextSeries = tmgr.fetchDetachedRowSet_No_release();
        String sql = null;
        if (rsNextSeries.next()) {
            newPrefix = rsNextSeries.getString("next_prefix_series");
            if (newPrefix == null || newPrefix.isEmpty() || newPrefix.length() < 4) {
                //
                TransactionManager tmgrNew = null;
                try {
                    tmgr.release();
                    tmgrNew = new TransactionManager("switchToNewSeries");
                    sql = "update vm_regn_series set running_no=10000 where series_id=? and state_cd=? and off_cd=? and running_no < 10000";
                    ps = tmgrNew.prepareStatement(sql);
                    ps.setInt(1, series_id);
                    ps.setString(2, stateCode);
                    ps.setInt(3, selectedOffCode);
                    ps.executeUpdate();
                    //
                    tmgrNew.commit();
                    String errorMessage = "Registration no series [ " + rsNextSeries.getString("prefix_series") + " ] exhausted and next series is not available.";
                    throw new VahanException(errorMessage);
                } catch (VahanException ve) {
                    throw ve;
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException("Error In Switching to Next Series");
                } finally {
                    if (tmgrNew != null) {
                        try {
                            tmgrNew.release();
                        } catch (Exception ex) {
                            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                            throw new VahanException("Error In Switching to Next Series");
                        }
                    }
                }
            }

            newPrefix = newPrefix.toUpperCase();

            //insert into history table.
            sql = "insert into vhm_regn_series (select series_id, criteria_formula, prefix_series, lower_range_no, upper_range_no, running_no, next_prefix_series, no_gen_type, entered_by,entered_on,? ,current_timestamp,state_cd, off_cd from vm_regn_series where series_id=? and state_cd=? and off_cd=?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setInt(2, series_id);
            ps.setString(3, stateCode);
            ps.setInt(4, selectedOffCode);
            ps.executeUpdate();

            sql = "update vm_regn_series set running_no=lower_range_no,prefix_series=next_prefix_series,next_prefix_series=null where series_id=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, series_id);
            ps.setString(2, stateCode);
            ps.setInt(3, selectedOffCode);
            ps.executeUpdate();
        } else {
            String errorMessage = "Registration no series is exhausted and next series is not available.";
            throw new VahanException(errorMessage);
        }
        return newPrefix;
    }

    public static void fillForModeMixP(TransactionManager tmgr, int series_id, String stateCd, int offCd) throws VahanException {
        try {


            PreparedStatement ps = null;

            int MAXBATCH = 0;
            int NEWRUNNO = 0;
            int NOOFREG = 0;
            boolean flgFirstInsertion = false;

            String sql = "select  regn_gen_random_batch from  " + TableList.TM_CONFIGURATION
                    + " where regn_gen_type=? and state_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, TableConstants.NO_GEN_TYPE_MIX_P);
            ps.setString(2, Util.getUserStateCode());

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                int run_batch = rs.getInt("regn_gen_random_batch");

                ps = tmgr.prepareStatement("update " + TableList.VM_REGN_SERIES + " set running_no=running_no where series_id=?  "
                        + " and state_cd=? and off_cd=? ");
                ps.setInt(1, series_id);
                ps.setString(2, stateCd);
                ps.setInt(3, offCd);
                ps.executeUpdate();
                //To Check a First Time insertion of records in VM_REGN_AVAILABLE
                sql = "select  count(*) count from  " + TableList.VM_REGN_AVAILABLE
                        + " where  state_cd=? "
                        + " and off_cd=? and series_id=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, offCd);
                ps.setInt(3, series_id);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    if (rs.getInt("count") != 0 && rs.getInt("count") >= run_batch) {
                        return;
                    }
                    NOOFREG = rs.getInt("count");
                    run_batch = run_batch - NOOFREG;
                    flgFirstInsertion = true;
                }
                sql = "select * from " + TableList.VM_REGN_SERIES + " where series_id=?  and state_cd=? and off_cd=? and running_no <= upper_range_no";
                ps = tmgr.prepareStatement(sql);
                ps.setInt(1, series_id);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, offCd);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    String prefix_series = rs.getString("prefix_series");
                    int run_no = rs.getInt("running_no");
                    int upper_range_no = rs.getInt("upper_range_no");
                    if (run_no + run_batch == upper_range_no) {
                        MAXBATCH = upper_range_no;
                        //Series Expired
                        NEWRUNNO = upper_range_no + 1;
                    } else if (run_no + run_batch > upper_range_no) {
                        MAXBATCH = upper_range_no;
                        NEWRUNNO = MAXBATCH + 1;
                    } else if (run_no + run_batch < upper_range_no) {
                        MAXBATCH = run_no + run_batch - 1;
                        NEWRUNNO = MAXBATCH + 1;
                    }
                    sql = "INSERT INTO " + TableList.VM_REGN_AVAILABLE
                            + " SELECT ? as state_cd, ? as off_cd,regn_no,status,"
                            + " booking_fee as amount,? as entered_by,current_timestamp as entered_on ,? as prefix_series,? as series_id "
                            + " FROM getregnnoavailable(?,?,?,?,?)";

                    ps = tmgr.prepareStatement(sql);
                    int pos = 1;
                    ps.setString(pos++, Util.getUserStateCode());
                    ps.setInt(pos++, Util.getSelectedSeat().getOff_cd());
                    ps.setString(pos++, Util.getEmpCode());
                    ps.setString(pos++, prefix_series);
                    ps.setInt(pos++, series_id);
                    ps.setString(pos++, Util.getUserStateCode());
                    ps.setInt(pos++, Util.getSelectedSeat().getOff_cd());
                    ps.setString(pos++, prefix_series);
                    ps.setInt(pos++, run_no);
                    ps.setInt(pos++, MAXBATCH);
                    ps.executeUpdate();
                    sql = "update  " + TableList.VM_REGN_SERIES + "  set running_no = ? where series_id=? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setInt(1, NEWRUNNO);
                    ps.setInt(2, series_id);
                    ps.setString(3, Util.getUserStateCode());
                    ps.setInt(4, offCd);
                    ps.executeUpdate();
                } else {
                    //Series Expired prefixSeries
                    switchToNewSeries(series_id, tmgr);
                }
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error In Generation of Number");
        }
    }

    public String generateAssignNewRegistrationNo(int offCd, int action_cd,
            String appl_no, String regNo, int counter, Owner_dobj owner_dobj, VehicleParameters vehParameters, TransactionManager tmgr) throws VahanException {
        String regn_no = null;
        //Check if Number should be assigned at this action code or not

        String sqlAppl = null;
        Map<String, List<NumberDetail_dobj>> seriesWiseList = null;
        PreparedStatement ps;
        RowSet rs;
        try {
            boolean genNum = false;
            if ((Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                    || (Util.getTmConfiguration() != null && Util.getTmConfiguration().isNum_gen_allowed_dealer() && Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE))
                    && action_cd == TableConstants.TM_NEW_RC_APPROVAL
                    && owner_dobj != null
                    && (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_NEW)
                    || owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_SCRAPPED)
                    || owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_TEMPORARY))
                    && owner_dobj.getRegn_no().trim().equalsIgnoreCase("NEW")) {
                genNum = true;
            }

            if (!"AS,PB,OR".contains(Util.getUserStateCode())) {
                sqlAppl = "Select * from vm_regn_alloted  where appl_no=?";
                ps = tmgr.prepareStatement(sqlAppl);
                ps.setString(1, appl_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    regn_no = rs.getString("regn_no");
                    return regn_no;
                }
            }

            sqlAppl = "Select * from vm_regn_gen_action  where state_cd=?  and action_cd=?";
            ps = tmgr.prepareStatement(sqlAppl);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, action_cd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            boolean blnRsNext = false;
            if (rs.next()) {
                blnRsNext = true;
                if (vehParameters == null) {
                    if (owner_dobj != null) {
                        vehParameters = FormulaUtils.fillVehicleParametersFromDobj(owner_dobj);
                    }
                }
                if (vehParameters != null) {
                    if (!FormulaUtilities.isCondition(FormulaUtilities.replaceTagValues(rs.getString("condition_formula"), vehParameters), "generateAssignNewRegistrationNo")) {
                        return null;
                    }
                }
            }
            if (blnRsNext || genNum) {
                /**
                 * if user has applied for fancy number,his application should
                 * not be approved un till result of auction is out
                 */
                sqlAppl = "Select fn_book_appl_no from fancy.fn_book_appl_no(?)  ";
                ps = tmgr.prepareStatement(sqlAppl);
                ps.setString(1, appl_no);
                RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                if (rs1.next()) {
                    String fancyApplno = rs1.getString("fn_book_appl_no");
                    if (fancyApplno != null && !fancyApplno.isEmpty()) {
                        throw new VahanException("Result Of Fancy Auction is still not out ");
                    }
                }
                seriesWiseList = getNewVehicleNumberToBeAssigned(offCd, appl_no, regNo, tmgr);
                assignNewVehicleNumberRandom(seriesWiseList, offCd, tmgr, appl_no, regNo);

                for (Map.Entry<String, List<NumberDetail_dobj>> entry : seriesWiseList.entrySet()) {
                    List<NumberDetail_dobj> list = entry.getValue();
                    for (int i = 0; i < list.size(); i++) {
                        NumberDetail_dobj numDetail = list.get(i);
                        regn_no = numDetail.getRegn_no_alloted();
                        //logger.info("regn_no Generated : " + numDetail.getRegn_no_alloted() + " Application No:  " + appl_no);
                    }
                }

                if (regn_no == null || regn_no.equals("NEW") || regn_no.equals("")) {
                    throw new VahanException("Vehicle Registration No Generation Failed against Application No " + appl_no);
                } else {
                    String message = "Vehicle No " + regn_no + " generated against Application No " + appl_no;
                    ServerUtil.insertUsersTransactionMessage(message, counter, tmgr);
                }

            }
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(ex.getMessage());
        }

        return regn_no;
    }

    /**
     * @author Kartikey Singh
     */
    public String generateAssignNewRegistrationNo(int selectedOffCode, int actionCode, String appl_no, String regNo, int counter,
            Owner_dobj owner_dobj, VehicleParameters vehParameters, TransactionManager tmgr, int purCode, String stateCode, int userLoginOffCode,
            String userCategory, String empCode) throws VahanException {
        String regn_no = null;
        //Check if Number should be assigned at this action code or not

        String sqlAppl = null;
        Map<String, List<NumberDetail_dobj>> seriesWiseList = null;
        PreparedStatement ps;
        RowSet rs;
        try {
            boolean genNum = false;
            TmConfigurationDobj tmConfigDobj = Utility.getTmConfiguration(null, stateCode);
            if ((purCode == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                    || (tmConfigDobj != null && tmConfigDobj.isNum_gen_allowed_dealer() && purCode == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE))
                    && actionCode == TableConstants.TM_NEW_RC_APPROVAL
                    && owner_dobj != null
                    && (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_NEW)
                    || owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_SCRAPPED)
                    || owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_TEMPORARY))
                    && owner_dobj.getRegn_no().trim().equalsIgnoreCase("NEW")) {
                genNum = true;
            }

            if (!"AS,PB,OR".contains(stateCode)) {
                sqlAppl = "Select * from vm_regn_alloted  where appl_no=?";
                ps = tmgr.prepareStatement(sqlAppl);
                ps.setString(1, appl_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    regn_no = rs.getString("regn_no");
                    return regn_no;
                }
            }

            sqlAppl = "Select * from vm_regn_gen_action  where state_cd=?  and action_cd=?";
            ps = tmgr.prepareStatement(sqlAppl);
            ps.setString(1, stateCode);
            ps.setInt(2, actionCode);
            rs = tmgr.fetchDetachedRowSet_No_release();
            boolean blnRsNext = false;
            if (rs.next()) {
                blnRsNext = true;
                if (vehParameters == null) {
                    if (owner_dobj != null) {
                        vehParameters = FormulaUtilities.fillVehicleParametersFromDobj(owner_dobj, selectedOffCode, userCategory,
                                actionCode, userLoginOffCode, stateCode);
                    }
                }
                if (vehParameters != null) {
                    if (!FormulaUtilities.isCondition(FormulaUtilities.replaceTagValues(rs.getString("condition_formula"), vehParameters), "generateAssignNewRegistrationNo")) {
                        return null;
                    }
                }
            }
            if (blnRsNext || genNum) {
                /**
                 * if user has applied for fancy number,his application should
                 * not be approved un till result of auction is out
                 */
                sqlAppl = "Select fn_book_appl_no from fancy.fn_book_appl_no(?)  ";
                ps = tmgr.prepareStatement(sqlAppl);
                ps.setString(1, appl_no);
                RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                if (rs1.next()) {
                    String fancyApplno = rs1.getString("fn_book_appl_no");
                    if (fancyApplno != null && !fancyApplno.isEmpty()) {
                        throw new VahanException("Result Of Fancy Auction is still not out ");
                    }
                }
                seriesWiseList = getNewVehicleNumberToBeAssigned(selectedOffCode, appl_no, regNo, tmgr, stateCode, actionCode, userLoginOffCode);
                assignNewVehicleNumberRandom(seriesWiseList, selectedOffCode, tmgr, appl_no, regNo, stateCode, empCode, actionCode, userLoginOffCode);

                for (Map.Entry<String, List<NumberDetail_dobj>> entry : seriesWiseList.entrySet()) {
                    List<NumberDetail_dobj> list = entry.getValue();
                    for (int i = 0; i < list.size(); i++) {
                        NumberDetail_dobj numDetail = list.get(i);
                        regn_no = numDetail.getRegn_no_alloted();
                        //logger.info("regn_no Generated : " + numDetail.getRegn_no_alloted() + " Application No:  " + appl_no);
                    }
                }

                if (regn_no == null || regn_no.equals("NEW") || regn_no.equals("")) {
                    throw new VahanException("Vehicle Registration No Generation Failed against Application No " + appl_no);
                } else {
                    String message = "Vehicle No " + regn_no + " generated against Application No " + appl_no;
                    ServerUtility.insertUsersTransactionMessage(message, counter, tmgr, empCode);
                }

            }
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(ex.getMessage());
        }

        return regn_no;
    }

    /**
     *
     * @param offCd
     * @param appl_no
     * @return
     * @throws VahanException For Number Assignment Other than Random Number
     */
    public static Map<String, List<NumberDetail_dobj>> getNewVehicleNumberToBeAssigned(int offCd, String appl_no, String regnNo,
            TransactionManager tmgr)
            throws VahanException {

        ArrayList<NumberDetail_dobj> applArrayList = new ArrayList<>();
        Map<String, List<NumberDetail_dobj>> seriesWiseList = new HashMap<>();

        try {

            NumberDetail_dobj numDetail = new NumberDetail_dobj();
            Map map = FormulaUtils.regn_no_records(appl_no, regnNo, tmgr);
            int series_id = Integer.parseInt(map.get("series_id").toString());
            String prefix_series = map.get("prefix_series").toString();
            numDetail.setAppl_no(appl_no);
            numDetail.setRegn_no_alloted("");
            numDetail.setStatus("");
            numDetail.setRemark("");
            numDetail.setSeries_cd_to_be_allocated(series_id);
            numDetail.setSeries_to_be_allocated(prefix_series);
            numDetail.setOff_cd(offCd);
            numDetail.setState_cd(TableConstants.STATE_CD);

            applArrayList.add(numDetail);

            Collections.sort(applArrayList, new NumberDetail_dobj.OrderBySeriesPrefix());
            for (int i = 0; i < applArrayList.size(); i++) {
                NumberDetail_dobj numberDetail_dobj = applArrayList.get(i);

                if (seriesWiseList.get(numberDetail_dobj.getSeries_to_be_allocated()) == null) {
                    seriesWiseList.put(numberDetail_dobj.getSeries_to_be_allocated(), new ArrayList<NumberDetail_dobj>());
                    seriesWiseList.get(numberDetail_dobj.getSeries_to_be_allocated()).add(numberDetail_dobj);
                } else {
                    seriesWiseList.get(numberDetail_dobj.getSeries_to_be_allocated()).add(numberDetail_dobj);
                }
            }
            // Find Fancy Numbers to be allocated..
            processForFancyNumberList(seriesWiseList, tmgr, appl_no);
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            throw new VahanException(e.getMessage());
        }

        return seriesWiseList;
    }

    /*
     * @author Kartikey Singh
     */
    public static Map<String, List<NumberDetail_dobj>> getNewVehicleNumberToBeAssigned(int selectedOffCode, String appl_no, String regnNo,
            TransactionManager tmgr, String stateCode, int actionCode, int userLoginOffCode) throws VahanException {

        ArrayList<NumberDetail_dobj> applArrayList = new ArrayList<>();
        Map<String, List<NumberDetail_dobj>> seriesWiseList = new HashMap<>();

        try {

            NumberDetail_dobj numDetail = new NumberDetail_dobj();
            Map map = FormulaUtilities.regn_no_records(appl_no, regnNo, tmgr, stateCode, selectedOffCode, actionCode, userLoginOffCode);
            int series_id = Integer.parseInt(map.get("series_id").toString());
            String prefix_series = map.get("prefix_series").toString();
            numDetail.setAppl_no(appl_no);
            numDetail.setRegn_no_alloted("");
            numDetail.setStatus("");
            numDetail.setRemark("");
            numDetail.setSeries_cd_to_be_allocated(series_id);
            numDetail.setSeries_to_be_allocated(prefix_series);
            numDetail.setOff_cd(selectedOffCode);
            numDetail.setState_cd(TableConstants.STATE_CD);

            applArrayList.add(numDetail);

            Collections.sort(applArrayList, new NumberDetail_dobj.OrderBySeriesPrefix());
            for (int i = 0; i < applArrayList.size(); i++) {
                NumberDetail_dobj numberDetail_dobj = applArrayList.get(i);

                if (seriesWiseList.get(numberDetail_dobj.getSeries_to_be_allocated()) == null) {
                    seriesWiseList.put(numberDetail_dobj.getSeries_to_be_allocated(), new ArrayList<NumberDetail_dobj>());
                    seriesWiseList.get(numberDetail_dobj.getSeries_to_be_allocated()).add(numberDetail_dobj);
                } else {
                    seriesWiseList.get(numberDetail_dobj.getSeries_to_be_allocated()).add(numberDetail_dobj);
                }
            }
            // Find Fancy Numbers to be allocated..
            processForFancyNumberList(seriesWiseList, tmgr, appl_no);
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            throw new VahanException(e.getMessage());
        }

        return seriesWiseList;
    }

    public Map<String, List<NumberDetail_dobj>> getNewVehicleNumberToBeAssigned(int offCd, String stateCd) throws Exception {
        TransactionManager tmgr = null;
        ArrayList<NumberDetail_dobj> applArrayList = new ArrayList<>();
        Map<String, List<NumberDetail_dobj>> seriesWiseList = new HashMap<>();
        PreparedStatement ps = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("getNewVehicleNumberToBeAssigned");
            sql = "SELECT appl_no FROM  " + TableList.VA_RANDOM_REGN_NO
                    + "  WHERE allot_dt::date = current_date and state_cd = ? and off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);

            RowSet rsCheckForToday = tmgr.fetchDetachedRowSet_No_release();
            if (!rsCheckForToday.next() || true) {//  (!) and  (|| true) for testing

                sql = "SELECT VA_RANDOM_REGN_NO.appl_no FROM VA_RANDOM_REGN_NO "
                        + " LEFT JOIN VA_FANCY_REGISTER ON VA_RANDOM_REGN_NO.appl_no=regn_appl_no"
                        + " INNER JOIN va_status ON va_status.appl_no = VA_RANDOM_REGN_NO.appl_no  "
                        + " WHERE regn_appl_no is null and regn_no_alloted is null and va_status.action_cd=" + TableConstants.TM_ROLE_RANDOM_MARK_ACTION_CD
                        + " and VA_RANDOM_REGN_NO.state_cd=? and va_status.off_cd=?";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, offCd);

                RowSet rs = tmgr.fetchDetachedRowSet_No_release();

                while (rs.next()) {
                    NumberDetail_dobj numDetail = new NumberDetail_dobj();
                    String appl_no = rs.getString("appl_no");
                    //this will hit va_owner and vm_regn series n number of times////////
                    Map map = FormulaUtils.regn_no_records(appl_no, null, tmgr);
                    int series_id = Integer.parseInt(map.get("series_id").toString());
                    String prefix_series = map.get("prefix_series").toString();
                    ///////////////////////////////////////////////////////////
                    numDetail.setAppl_no(appl_no);
                    numDetail.setRegn_no_alloted("");
                    numDetail.setStatus("");
                    numDetail.setRemark("");
                    numDetail.setSeries_cd_to_be_allocated(series_id);
                    numDetail.setSeries_to_be_allocated(prefix_series);
                    numDetail.setOff_cd(offCd);
                    numDetail.setState_cd(stateCd);
                    applArrayList.add(numDetail);
                }
                // Sort the list of application no based on the SeriesPrefix application to the applications
                Collections.sort(applArrayList, new NumberDetail_dobj.OrderBySeriesPrefix());
            }
            for (int i = 0; i < applArrayList.size(); i++) {
                NumberDetail_dobj numberDetail_dobj = applArrayList.get(i);

                if (seriesWiseList.get(numberDetail_dobj.getSeries_to_be_allocated()) == null) {
                    seriesWiseList.put(numberDetail_dobj.getSeries_to_be_allocated(), new ArrayList<NumberDetail_dobj>());
                    seriesWiseList.get(numberDetail_dobj.getSeries_to_be_allocated()).add(numberDetail_dobj);
                } else {
                    seriesWiseList.get(numberDetail_dobj.getSeries_to_be_allocated()).add(numberDetail_dobj);
                }
            }
            // Find Fancy Numbers to be allocated..
            processForFancyNumberList(seriesWiseList, tmgr, offCd, stateCd);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                throw new VahanException("Error in fetching details");
            }
        }

        return seriesWiseList;
    }

    /**
     * For states other Than Kerala
     *
     * @param seriesWiseList
     * @param offCd
     * @param tmgr
     * @param appl_no
     * @param regnNo
     * @throws VahanException
     */
    public void assignNewVehicleNumberRandom(Map<String, List<NumberDetail_dobj>> seriesWiseList,
            int offCd, TransactionManager tmgr, String appl_no, String regnNo)
            throws VahanException {

        String newRegNo = null;
        String tempAlpha;
        String tempSeri;
        String tempcat;
        RowSet rs = null;
        int num = 0;
        Exception e = null;

        // Timestamp system_date_postgresSQL = ServerUtil.getSystemDateInPostgres();
        try {

///----------- Mapping fancy Number.
/// get the regn_no to be allotted from vt_fancy_register

            processForSystemGeneratedNumberList(seriesWiseList, tmgr, regnNo);

            // find next counter / action for application
            //We can set whole map into the session
            PreparedStatement ps;
            for (Map.Entry<String, List<NumberDetail_dobj>> entry : seriesWiseList.entrySet()) {
                String string = entry.getKey();
                List<NumberDetail_dobj> list = entry.getValue();
                for (int i = 0; i < list.size(); i++) {
                    NumberDetail_dobj numDetail = list.get(i);
                    asignAndUpdateNewVehicleNo(numDetail.getRegn_no_alloted(), numDetail.getAppl_no(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), numDetail.getStatus(), numDetail.getRemark(), tmgr);
                }
            }

        } catch (SQLException sqle) {
            e = sqle;
        } catch (Exception ex) {
            e = ex;
        }
        if (e != null) {
            throw new VahanException(e.getMessage());
        }
    }

    /**
     * @author Kartikey Singh
     * @OldComment: For state other than Kerala
     */
    public void assignNewVehicleNumberRandom(Map<String, List<NumberDetail_dobj>> seriesWiseList,
            int selectedOffCode, TransactionManager tmgr, String appl_no, String regnNo, String stateCode, String empCode, int actionCode,
            int userLoginOffCode) throws VahanException {

        String newRegNo = null;
        String tempAlpha;
        String tempSeri;
        String tempcat;
        RowSet rs = null;
        int num = 0;
        Exception e = null;

        try {

            ///----------- Mapping fancy Number.
            /// get the regn_no to be allotted from vt_fancy_register
            processForSystemGeneratedNumberList(seriesWiseList, tmgr, regnNo, stateCode, empCode, actionCode, userLoginOffCode);

            // find next counter / action for application
            //We can set whole map into the session
            PreparedStatement ps;
            for (Map.Entry<String, List<NumberDetail_dobj>> entry : seriesWiseList.entrySet()) {
                String string = entry.getKey();
                List<NumberDetail_dobj> list = entry.getValue();
                for (int i = 0; i < list.size(); i++) {
                    NumberDetail_dobj numDetail = list.get(i);
                    asignAndUpdateNewVehicleNo(numDetail.getRegn_no_alloted(), numDetail.getAppl_no(), stateCode, selectedOffCode, numDetail.getStatus(), numDetail.getRemark(), tmgr);
                }
            }

        } catch (SQLException sqle) {
            e = sqle;
        } catch (Exception ex) {
            e = ex;
        }
        if (e != null) {
            throw new VahanException(e.getMessage());
        }
    }

    public void assignNewVehicleNumberRandom(Map<String, List<NumberDetail_dobj>> seriesWiseList, int offCd, String regnNo, String state_cd) throws Exception {
        RowSet rs = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        Timestamp system_date_postgresSQL = ServerUtil.getSystemDateInPostgres();
        String sql = null;

        try {

            tmgr = new TransactionManager("assignNewVehicleNumberRandom");
            ///----------- Mapping fancy Number. 
            /// get the regn_no to be allotted from vt_fancy_register

            processForSystemGeneratedNumberList(seriesWiseList, tmgr, regnNo);

            for (Map.Entry<String, List<NumberDetail_dobj>> entry : seriesWiseList.entrySet()) {

                List<NumberDetail_dobj> list = entry.getValue();

                for (int i = 0; i < list.size(); i++) {
                    NumberDetail_dobj numDetail = list.get(i);
                    asignAndUpdateNewVehicleNo(numDetail.getRegn_no_alloted(), numDetail.getAppl_no(), state_cd, offCd, numDetail.getStatus(), numDetail.getRemark(), tmgr);
                }
            }

            HashMap<String, String> updatedStatus = RandonNumberFileMovement(tmgr);

            for (Map.Entry<String, String> entry : updatedStatus.entrySet()) {
                String appl_no = entry.getKey();
                String status = entry.getValue();

                for (Map.Entry<String, List<NumberDetail_dobj>> entry1 : seriesWiseList.entrySet()) {

                    List<NumberDetail_dobj> numberList = entry1.getValue();
                    for (Iterator<NumberDetail_dobj> it = numberList.iterator(); it.hasNext();) {
                        NumberDetail_dobj numberDetail_dobj = it.next();
                        if (numberDetail_dobj.getAppl_no().equals(appl_no)) {
                            numberDetail_dobj.setStatus(status);
                            break;
                        }
                    }
                }
            }

            tmgr.commit();

        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    /**
     * @modifiedBy Kartikey Singh
     *
     * Changed references of Util.getUserStateCode() to state_cd, as both of
     * them referred to the same thing
     *
     * @param regnNo to be assigned
     * @param applNo application Number
     * @param state_cd
     * @param offCd
     * @param tmgr
     * @throws VahanException
     */
    public void asignAndUpdateNewVehicleNo(String regnNo, String applNo, String state_cd, int offCd, String status, String remark, TransactionManager tmgr) throws VahanException {
        String sql = null;
        try {
            PreparedStatement ps = null;
            sql = "UPDATE " + TableList.VA_RANDOM_REGN_NO
                    + " SET regn_no_alloted = ?, allot_dt = current_timestamp, status=?, remark=? "
                    + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, status);
            ps.setString(3, remark);
            ps.setString(4, applNo);
            ps.executeUpdate();

            // for updating va_details for newreg generation
            sql = "UPDATE " + TableList.VA_DETAILS
                    + " SET regn_no=? "
                    + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, applNo);
            ps.executeUpdate();
            //////////////////////////////////////////////

            /* New Generated Registration No is updated in the following 
             tables start */

            sql = " update va_fitness set regn_no=?,op_dt = current_timestamp where appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, applNo);
            ps.executeUpdate();

            /**
             * For Non Transport Vehicles
             */
            sql = " update " + TableList.VA_INSPECTION
                    + " set regn_no=?,op_dt = current_timestamp where appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, applNo);
            ps.executeUpdate();

            AuctionDobj auctionDobj = new AuctionImpl().getVtAuctionDetailsForRegnDtUpdation(applNo);
            
            sql = "select regn_no from " + TableList.VA_OWNER + " where appl_no=? and regn_type in ('"
                    + TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE + "','"
                    + TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE + "') ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
            if (auctionDobj != null || (rs1 != null && rs1.next())) {
                sql = " update  " + TableList.VA_OWNER
                        + " set regn_no=?,op_dt = current_timestamp where appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regnNo);
                ps.setString(2, applNo);
                ps.executeUpdate();

            } else {

                sql = " update  " + TableList.VA_OWNER
                        + " set regn_no=?, regn_dt=current_date,op_dt = current_timestamp where appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regnNo);
                ps.setString(2, applNo);
                ps.executeUpdate();

                // for state Gujrat where regn_date set based on fitness date or receipt_date                     
                if (state_cd.equalsIgnoreCase("GJ")) {
                    Date regnDate = null;
                    FitnessImpl fitnessImpl = new FitnessImpl();
                    FitnessDobj fitnessDobj = fitnessImpl.set_Fitness_appl_db_to_dobj(null, applNo);
                    if (fitnessDobj != null) {
                        regnDate = fitnessDobj.getFit_chk_dt();
                    }

                    if (regnDate == null) {
                        InspectionDobj inspectionDobj = fitnessImpl.getVaInspectionDobj(applNo);
                        if (inspectionDobj != null) {
                            regnDate = inspectionDobj.getInsp_dt();
                        }
                    }

                    if (regnDate != null) {
                        sql = "UPDATE " + TableList.VA_OWNER + " SET regn_dt=?,op_dt = current_timestamp"
                                + " WHERE appl_no=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setDate(1, new java.sql.Date(regnDate.getTime()));
                        ps.setString(2, applNo);
                        ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                    }
                }
            }
            if (state_cd.equals("KL")) {
                sql = "select * from " + TableList.VA_OWNER_OTHER + " where appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
                RowSet rs2 = tmgr.fetchDetachedRowSet_No_release();
                if (rs2.next()) {
                    sql = " update  " + TableList.VA_OWNER_OTHER
                            + " set regn_no=?,op_dt = current_timestamp where appl_no=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regnNo);
                    ps.setString(2, applNo);
                    ps.executeUpdate();
                }
            }//////////////////////
            sql = " update  " + TableList.VA_OWNER_IDENTIFICATION
                    + " set regn_no=?,op_dt = current_timestamp where appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, applNo);
            ps.executeUpdate();

            sql = " update va_hpa set regn_no=?,op_dt = current_timestamp where appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, applNo);
            ps.executeUpdate();

            sql = "update " + TableList.VA_INSURANCE + " set regn_no=?,op_dt = current_timestamp where appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, applNo);
            ps.executeUpdate();

            sql = "update va_trailer set regn_no=?,op_dt = current_timestamp where appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, applNo);
            ps.executeUpdate();

            sql = " update  " + TableList.VA_SIDE_TRAILER
                    + " set regn_no=?,op_dt = current_timestamp where appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, applNo);
            ps.executeUpdate();

            //VT_TAX  
            String taxRcpt = null;
            sql = "select DISTINCT tax.rcpt_no from vp_appl_rcpt_mapping map,vt_tax tax"
                    + " where map.appl_no=? and map.rcpt_no=tax.rcpt_no and map.state_cd=tax.state_cd and map.off_cd=tax.off_cd ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                sql = "UPDATE vt_tax set regn_no= ? WHERE rcpt_no=? and state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regnNo);
                ps.setString(2, rs.getString("rcpt_no"));
                ps.setString(3, state_cd);
                ps.setInt(4, offCd);
                ps.executeUpdate();
                taxRcpt = rs.getString("rcpt_no");
            }

            //Tax Based Upon
            sql = "UPDATE  " + TableList.VT_TAX_BASED_ON
                    + " set regn_no= ? WHERE rcpt_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, taxRcpt);
            ps.setString(3, state_cd);
            ps.setInt(4, offCd);
            ps.executeUpdate();


            ///  VT_FEE
            sql = "select DISTINCT fee.rcpt_no from vp_appl_rcpt_mapping map,vt_fee fee"
                    + " where map.appl_no=? and map.rcpt_no=fee.rcpt_no and map.state_cd=fee.state_cd and map.off_cd=fee.off_cd ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);

            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                //logger.info("Update vt_fee in assignNewVehicleNumberRandom()");
                sql = "UPDATE vt_fee set regn_no= ? WHERE rcpt_no=? and state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regnNo);
                ps.setString(2, rs.getString("rcpt_no"));
                ps.setString(3, state_cd);
                ps.setInt(4, offCd);
                ps.executeUpdate();
            }

            //VT_SCRAP_VEHICLE                    
            sql = "Update " + TableList.VT_SCRAP_VEHICLE + " set new_regn_no=? where regn_appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, applNo);
            ps.executeUpdate();

            //VA_FC_PRINT
            sql = "Update " + TableList.VA_RC_PRINT + " set regn_no=? where appl_no=?  and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, applNo);
            ps.setString(3, state_cd);
            ps.setInt(4, offCd);
            ps.executeUpdate();

            //va_speed_governor
            sql = "Update va_speed_governor set regn_no=? where appl_no=?  and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, applNo);
            ps.setString(3, state_cd);
            ps.setInt(4, offCd);
            ps.executeUpdate();

            //va_reflective_tape
            sql = "Update " + TableList.VA_REFLECTIVE_TAPE
                    + " set regn_no=?,op_dt=current_timestamp where appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, applNo);
            ps.executeUpdate();


            //VA_NEW_REG_LOI
            sql = "Update " + TableList.VA_NEW_REGN_LOI + " set regn_no=? where appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, applNo);
            ps.executeUpdate();
        } catch (VahanException ve) {
            throw ve;

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Assignment of Registration :" + regnNo + " failed for  " + applNo);
        }
    }

    private static void processForFancyNumberList(Map<String, List<NumberDetail_dobj>> seriesWiseList, TransactionManager tmgr, String appl_no) throws SQLException, VahanException {
        String fancy = "Select regn_appl_no, regn_no from  "
                + " vt_fancy_register where regn_appl_no =?";
        PreparedStatement ps = tmgr.prepareStatement(fancy);
        ps.setString(1, appl_no);
        RowSet rsFancy = tmgr.fetchDetachedRowSet_No_release();
        while (rsFancy.next()) {
            String regn_no = rsFancy.getString("regn_no");
            for (Map.Entry<String, List<NumberDetail_dobj>> entry : seriesWiseList.entrySet()) {
                String series_to_allocted = entry.getKey();
                List<NumberDetail_dobj> applArrayList = entry.getValue();
                for (int i = 0; i < applArrayList.size(); i++) {
                    NumberDetail_dobj temp = applArrayList.get(i);
                    if (temp.getAppl_no().equalsIgnoreCase(appl_no)) {
                        temp.setRegn_no_alloted(regn_no);
                        temp.setStatus(TableConstants.RANDOM_NUMBER_STATUS_A);
                        temp.setRemark("Fancy Number Assigned.");
                        break;
                    }
                }
            }
        }


        fancy = "Select regn_appl_no, regn_no from  " + TableList.VT_ADVANCE_REGN_NO
                + "  where regn_appl_no =? ";
        ps = tmgr.prepareStatement(fancy);
        ps.setString(1, appl_no);
        rsFancy = tmgr.fetchDetachedRowSet_No_release();
        while (rsFancy.next()) {
            String regn_no = rsFancy.getString("regn_no");
            for (Map.Entry<String, List<NumberDetail_dobj>> entry : seriesWiseList.entrySet()) {
                String series_to_allocted = entry.getKey();
                List<NumberDetail_dobj> applArrayList = entry.getValue();
                for (int i = 0; i < applArrayList.size(); i++) {
                    NumberDetail_dobj temp = applArrayList.get(i);
                    if (temp.getAppl_no().equalsIgnoreCase(appl_no)) {
                        temp.setRegn_no_alloted(regn_no);
                        temp.setStatus(TableConstants.RANDOM_NUMBER_STATUS_A);
                        temp.setRemark("Fancy Number Assigned.");
                        break;
                    }
                }
            }
        }
        //Added by Afzal for RETEN case
        fancy = "Select regn_appl_no, old_regn_no from  " + TableList.VT_SURRENDER_RETENTION
                + "  where regn_appl_no =? ";
        ps = tmgr.prepareStatement(fancy);
        ps.setString(1, appl_no);
        rsFancy = tmgr.fetchDetachedRowSet_No_release();
        while (rsFancy.next()) {
            String regn_no = rsFancy.getString("old_regn_no");
            for (Map.Entry<String, List<NumberDetail_dobj>> entry : seriesWiseList.entrySet()) {
                String series_to_allocted = entry.getKey();
                List<NumberDetail_dobj> applArrayList = entry.getValue();
                for (int i = 0; i < applArrayList.size(); i++) {
                    NumberDetail_dobj temp = applArrayList.get(i);
                    if (temp.getAppl_no().equalsIgnoreCase(appl_no)) {
                        temp.setRegn_no_alloted(regn_no);
                        temp.setStatus(TableConstants.RANDOM_NUMBER_STATUS_A);
                        temp.setRemark("Reten Number Assigned.");
                        break;
                    }
                }
            }
        }
        Map<String, String> choiceNo = OwnerChoiceNoImpl.getOwnerChoiceRegnNoDetails(appl_no);
        if (choiceNo != null && !choiceNo.isEmpty()) {
            String regn_no = choiceNo.get("regn_no");
            for (Map.Entry<String, List<NumberDetail_dobj>> entry : seriesWiseList.entrySet()) {
                List<NumberDetail_dobj> applArrayList = entry.getValue();
                for (int i = 0; i < applArrayList.size(); i++) {
                    NumberDetail_dobj temp = applArrayList.get(i);
                    if (temp.getAppl_no().equalsIgnoreCase(appl_no)) {
                        temp.setRegn_no_alloted(regn_no);
                        temp.setStatus(TableConstants.RANDOM_NUMBER_STATUS_A);
                        temp.setRemark("Choice Number Assigned.");
                        break;
                    }
                }
            }
        }
    }

    private void processForFancyNumberList(Map<String, List<NumberDetail_dobj>> seriesWiseList, TransactionManager tmgr, int offCd, String stateCd) throws Exception {

        PreparedStatement ps = null;
        String fancy = "SELECT appl_no, regn_no FROM " + TableList.VA_RANDOM_REGN_NO + " a JOIN " + TableList.VT_FANCY_REGISTER
                + " b on appl_no=regn_appl_no and regn_no_alloted is null "
                + " WHERE a.state_cd=? and a.off_cd=?";
        ps = tmgr.prepareStatement(fancy);
        ps.setString(1, stateCd);
        ps.setInt(2, offCd);
        RowSet rsFancy = tmgr.fetchDetachedRowSet_No_release();
        while (rsFancy.next()) {
            String appl_no = rsFancy.getString("appl_no");
            String regn_no = rsFancy.getString("regn_no");
            for (Map.Entry<String, List<NumberDetail_dobj>> entry : seriesWiseList.entrySet()) {
                List<NumberDetail_dobj> applArrayList = entry.getValue();
                for (int i = 0; i < applArrayList.size(); i++) {
                    NumberDetail_dobj temp = applArrayList.get(i);
                    if (temp.getAppl_no().equalsIgnoreCase(appl_no)) {
                        temp.setRegn_no_alloted(regn_no);
                        temp.setStatus(TableConstants.RANDOM_NUMBER_STATUS_A);
                        temp.setRemark("Fancy Number Assigned.");
                        break;
                    }
                }
            }
        }

        fancy = " SELECT appl_no, regn_no FROM " + TableList.VA_RANDOM_REGN_NO + " a JOIN " + TableList.VT_ADVANCE_REGN_NO
                + " b on appl_no=regn_appl_no "
                + " WHERE a.state_cd=? and a.off_cd=?";
        ps = tmgr.prepareStatement(fancy);
        ps.setString(1, stateCd);
        ps.setInt(2, offCd);
        rsFancy = tmgr.fetchDetachedRowSet_No_release();
        while (rsFancy.next()) {
            String appl_no = rsFancy.getString("appl_no");
            String regn_no = rsFancy.getString("regn_no");
            for (Map.Entry<String, List<NumberDetail_dobj>> entry : seriesWiseList.entrySet()) {
                List<NumberDetail_dobj> applArrayList = entry.getValue();
                for (int i = 0; i < applArrayList.size(); i++) {
                    NumberDetail_dobj temp = applArrayList.get(i);
                    if (temp.getAppl_no().equalsIgnoreCase(appl_no)) {
                        temp.setRegn_no_alloted(regn_no);
                        temp.setStatus(TableConstants.RANDOM_NUMBER_STATUS_A);
                        temp.setRemark("Fancy Number Assigned.");
                        break;
                    }
                }
            }
        }
    }

    private void processForSystemGeneratedNumberList(Map<String, List<NumberDetail_dobj>> seriesWiseList, TransactionManager tmgr, String regnNo) throws Exception {

        for (Map.Entry<String, List<NumberDetail_dobj>> entry : seriesWiseList.entrySet()) {
            HashMap<Integer, String> intermediateMapForRandom = new HashMap<>();
            List<NumberDetail_dobj> systemGeneratedApplList = new ArrayList<>();
            String series_to_be_allocated = entry.getKey();

            List<NumberDetail_dobj> applArrayList = entry.getValue();
            String no_gen_type = Util.getTmConfiguration().getRegn_gen_type();
//            String sqlGetNoAllocationType = "SELECT no_gen_type FROM " + TableList.VM_REGN_SERIES
//                    + " WHERE series_id = ? and state_cd=? and off_cd=?";
//            PreparedStatement prstmtGetNoAllocationType = tmgr.prepareStatement(sqlGetNoAllocationType);
//            prstmtGetNoAllocationType.setInt(1, applArrayList.get(0).getSeries_cd_to_be_allocated());
//            prstmtGetNoAllocationType.setString(2, Util.getUserStateCode());
//            prstmtGetNoAllocationType.setInt(3, Util.getSelectedSeat().getOff_cd());
//            RowSet rsGetNoAllocationType = tmgr.fetchDetachedRowSet_No_release();
//            if (rsGetNoAllocationType.next()) {
//                no_gen_type = rsGetNoAllocationType.getString("no_gen_type");
//            } else {
//                throw new VahanException("Error in getting registration no allocation type for series [ " + series_to_be_allocated + " ]");
//            }

            int arrayIndex = 0;
            for (int i = 0; i < applArrayList.size(); i++) {

                /**
                 * Check if number is already assigned to this application via
                 * Fancy Number allotment, then skip this application from the
                 * System Generated Numbers.
                 */
                if (applArrayList.get(i).getRegn_no_alloted() != null && !applArrayList.get(i).getRegn_no_alloted().equals("") && applArrayList.get(i).getStatus() != null && applArrayList.get(i).getStatus().equalsIgnoreCase(TableConstants.RANDOM_NUMBER_STATUS_A)) {
                    continue;
                }


                // Filter Fancy Number / Number that is not allocated by the System.
                systemGeneratedApplList.add(applArrayList.get(i));
                intermediateMapForRandom.put(arrayIndex, getNewVehicleNumberFormulaBased(tmgr, applArrayList.get(i).getAppl_no().toString(), no_gen_type, applArrayList.get(i).getOff_cd(), regnNo));
                arrayIndex++;
            }



            if (no_gen_type.equalsIgnoreCase(TableConstants.NO_GEN_TYPE_RAND)) {
                Random generator = new Random();
                int cnt = 0;

                while (!intermediateMapForRandom.isEmpty()) {

                    Object[] values = intermediateMapForRandom.keySet().toArray();
                    int randomValue = Integer.valueOf(values[generator.nextInt(values.length)].toString());
                    String regn_no = intermediateMapForRandom.get(randomValue).toString();
                    //logger.info(appl_no + "  " + regn_no);
                    intermediateMapForRandom.remove(randomValue);
                    systemGeneratedApplList.get(cnt).setRegn_no_alloted(regn_no);
                    systemGeneratedApplList.get(cnt).setRemark("Random Number Assigned.");
                    systemGeneratedApplList.get(cnt).setStatus(TableConstants.RANDOM_NUMBER_STATUS_A);

                    cnt++;
                }
            } else if (no_gen_type.equalsIgnoreCase("S")) {
                // this block assign number in sequence.
                int cnt = 0;
                while (!intermediateMapForRandom.isEmpty()) {
                    String appl_no = systemGeneratedApplList.get(cnt).getAppl_no().toString();
                    String regn_no = intermediateMapForRandom.get(cnt).toString();
                    //logger.info(appl_no + "  " + regn_no);
                    intermediateMapForRandom.remove(cnt);
                    systemGeneratedApplList.get(cnt).setRegn_no_alloted(regn_no);
                    systemGeneratedApplList.get(cnt).setStatus(TableConstants.RANDOM_NUMBER_STATUS_A);
                    systemGeneratedApplList.get(cnt).setRemark("Sequence Number Assigned.");
                    cnt++;
                }
            } else if (no_gen_type.equalsIgnoreCase(TableConstants.NO_GEN_TYPE_MIX) || no_gen_type.equalsIgnoreCase(TableConstants.NO_GEN_TYPE_MIX_P)) {
                // this block assign number in Mix.
                int cnt = 0;
                while (!intermediateMapForRandom.isEmpty()) {
                    String appl_no = systemGeneratedApplList.get(cnt).getAppl_no().toString();
                    String regn_no = intermediateMapForRandom.get(cnt).toString();
                    //logger.info(appl_no + "  " + regn_no);
                    intermediateMapForRandom.remove(cnt);
                    systemGeneratedApplList.get(cnt).setRegn_no_alloted(regn_no);
                    systemGeneratedApplList.get(cnt).setStatus(TableConstants.RANDOM_NUMBER_STATUS_A);
                    systemGeneratedApplList.get(cnt).setRemark("Mix Number Assigned.");
                    cnt++;
                }
            } else {
                throw new VahanException("registration no allocation type for series [ " + series_to_be_allocated + " ] is not Random (R) / Sequence (S).");
            }
        }
    }

    /**
     * @author Kartikey Singh
     */
    private void processForSystemGeneratedNumberList(Map<String, List<NumberDetail_dobj>> seriesWiseList, TransactionManager tmgr, String regnNo,
            String stateCode, String empCode, int actionCode, int userLoginOffCode) throws Exception {

        for (Map.Entry<String, List<NumberDetail_dobj>> entry : seriesWiseList.entrySet()) {
            HashMap<Integer, String> intermediateMapForRandom = new HashMap<>();
            List<NumberDetail_dobj> systemGeneratedApplList = new ArrayList<>();
            String series_to_be_allocated = entry.getKey();

            List<NumberDetail_dobj> applArrayList = entry.getValue();
            String no_gen_type = Utility.getTmConfiguration(null, stateCode).getRegn_gen_type();

            int arrayIndex = 0;
            for (int i = 0; i < applArrayList.size(); i++) {

                /**
                 * Check if number is already assigned to this application via
                 * Fancy Number allotment, then skip this application from the
                 * System Generated Numbers.
                 */
                if (applArrayList.get(i).getRegn_no_alloted() != null && !applArrayList.get(i).getRegn_no_alloted().equals("") && applArrayList.get(i).getStatus() != null && applArrayList.get(i).getStatus().equalsIgnoreCase(TableConstants.RANDOM_NUMBER_STATUS_A)) {
                    continue;
                }

                // Filter Fancy Number / Number that is not allocated by the System.
                systemGeneratedApplList.add(applArrayList.get(i));
                intermediateMapForRandom.put(arrayIndex, getNewVehicleNumberFormulaBased(tmgr, applArrayList.get(i).getAppl_no().toString(),
                        no_gen_type, applArrayList.get(i).getOff_cd(), regnNo, stateCode, empCode, actionCode, userLoginOffCode));
                arrayIndex++;
            }

            if (no_gen_type.equalsIgnoreCase(TableConstants.NO_GEN_TYPE_RAND)) {
                Random generator = new Random();
                int cnt = 0;

                while (!intermediateMapForRandom.isEmpty()) {

                    Object[] values = intermediateMapForRandom.keySet().toArray();
                    int randomValue = Integer.valueOf(values[generator.nextInt(values.length)].toString());
                    String regn_no = intermediateMapForRandom.get(randomValue).toString();
                    //logger.info(appl_no + "  " + regn_no);
                    intermediateMapForRandom.remove(randomValue);
                    systemGeneratedApplList.get(cnt).setRegn_no_alloted(regn_no);
                    systemGeneratedApplList.get(cnt).setRemark("Random Number Assigned.");
                    systemGeneratedApplList.get(cnt).setStatus(TableConstants.RANDOM_NUMBER_STATUS_A);

                    cnt++;
                }
            } else if (no_gen_type.equalsIgnoreCase("S")) {
                // this block assign number in sequence.
                int cnt = 0;
                while (!intermediateMapForRandom.isEmpty()) {
                    String appl_no = systemGeneratedApplList.get(cnt).getAppl_no().toString();
                    String regn_no = intermediateMapForRandom.get(cnt).toString();
                    //logger.info(appl_no + "  " + regn_no);
                    intermediateMapForRandom.remove(cnt);
                    systemGeneratedApplList.get(cnt).setRegn_no_alloted(regn_no);
                    systemGeneratedApplList.get(cnt).setStatus(TableConstants.RANDOM_NUMBER_STATUS_A);
                    systemGeneratedApplList.get(cnt).setRemark("Sequence Number Assigned.");
                    cnt++;
                }
            } else if (no_gen_type.equalsIgnoreCase(TableConstants.NO_GEN_TYPE_MIX) || no_gen_type.equalsIgnoreCase(TableConstants.NO_GEN_TYPE_MIX_P)) {
                // this block assign number in Mix.
                int cnt = 0;
                while (!intermediateMapForRandom.isEmpty()) {
                    String appl_no = systemGeneratedApplList.get(cnt).getAppl_no().toString();
                    String regn_no = intermediateMapForRandom.get(cnt).toString();
                    //logger.info(appl_no + "  " + regn_no);
                    intermediateMapForRandom.remove(cnt);
                    systemGeneratedApplList.get(cnt).setRegn_no_alloted(regn_no);
                    systemGeneratedApplList.get(cnt).setStatus(TableConstants.RANDOM_NUMBER_STATUS_A);
                    systemGeneratedApplList.get(cnt).setRemark("Mix Number Assigned.");
                    cnt++;
                }
            } else {
                throw new VahanException("registration no allocation type for series [ " + series_to_be_allocated + " ] is not Random (R) / Sequence (S).");
            }
        }
    }

    public static boolean checkNumberAssignable(String regno, boolean skipFancy, TransactionManager tmg) throws SQLException {

        boolean status = true;
        PreparedStatement ps = null;
        RowSet rs = null;
        boolean blFancy = false;

        if (skipFancy) {
            String strFancy = " select  * from vm_fancy_mast where fancy_number =  SUBSTR(?, LENGTH(?) - 3, 4)"
                    + " and state_cd =?";
            ps = tmg.prepareStatement(strFancy);
            ps.setString(1, regno);
            ps.setString(2, regno);
            ps.setString(3, Util.getUserStateCode());
            rs = tmg.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                blFancy = true;
            }
        }
        if (!blFancy) {
            String sqlOwner = " SELECT regn_no||' Found in vt_owner' as regn_no  FROM  " + TableList.VT_OWNER
                    + "  where regn_no=? "
                    + " union "
                    + "SELECT regn_no||' Found in vt_fancy_register' as regn_no  FROM fancy.vt_fancy_register where regn_no=? "
                    + " union "
                    + " SELECT regn_no||' Found in va_fancy_register' as regn_no  FROM va_fancy_register where regn_no=? "
                    + " union "
                    + "SELECT regn_no||' Found in VT_ADVANCE_REGN_NO' as regn_no  FROM  " + TableList.VT_ADVANCE_REGN_NO
                    + " where regn_no=?"
                    + " union "
                    + "SELECT regn_no||' Found in VT_BLOCK_VEH' as regn_no  FROM  " + TableList.VT_BLOCK_VEH
                    + " where regn_no=?"
                    + " union "
                    + " SELECT regn_no ||' Found in fancy.block_regn_no_list' as regn_no  FROM  " + TableList.BLOCK_REGN_LIST
                    + " where regn_no=?"
                    + " union "
                    + " SELECT regn_no ||' Found in VM_REGN_ALLOTED' as regn_no  FROM  " + TableList.VM_REGN_ALLOTED
                    + " where regn_no=?"
                    + " union "
                    + " SELECT regn_no ||' Found in VA_OWNER' as regn_no  FROM  " + TableList.VA_OWNER
                    + " where regn_no=?"
                    + " union "
                    + " SELECT old_regn_no ||' Found in VH_RE_ASSIGN' as regn_no  FROM  " + TableList.VH_RE_ASSIGN
                    + " where old_regn_no=?"
                    + " union "
                    + " SELECT old_regn_no ||' Found in VH_CONVERSION' as regn_no  FROM  " + TableList.VH_CONVERSION
                    + " where old_regn_no=?"
                    + " union "
                    + " SELECT regn_no ||' Found in VA_OWNER_CHOICE_NO' as regn_no  FROM  " + TableList.VA_OWNER_CHOICE_NO
                    + " where regn_no=?"
                    + " union "
                    + " SELECT regn_no ||' Found in VP_ADVANCE_REGN_NO' as regn_no  FROM  " + TableList.VP_ADVANCE_REGN_NO
                    + " where regn_no=?";

            ps = tmg.prepareStatement(sqlOwner);
            int i = 1;
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            rs = tmg.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                blFancy = true;
            }
        }
        if (blFancy) {
            status = false;
            //Update vm_regn_available
            String sqlUpdate = "Update vm_regn_available set status='Z' where regn_no=? and state_cd=? and off_cd=? and status='A'";
            ps = tmg.prepareStatement(sqlUpdate);
            ps.setString(1, regno);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            ps.executeUpdate();
            return status;
        }
        return status;
    }

    /**
     * @author Kartikey Singh
     */
    public static boolean checkNumberAssignable(String regno, boolean skipFancy, TransactionManager tmg, String stateCode, int selectedOffCode) throws SQLException {

        boolean status = true;
        PreparedStatement ps = null;
        RowSet rs = null;
        boolean blFancy = false;

        if (skipFancy) {
            String strFancy = " select  * from vm_fancy_mast where fancy_number =  SUBSTR(?, LENGTH(?) - 3, 4)"
                    + " and state_cd =?";
            ps = tmg.prepareStatement(strFancy);
            ps.setString(1, regno);
            ps.setString(2, regno);
            ps.setString(3, stateCode);
            rs = tmg.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                blFancy = true;
            }
        }
        if (!blFancy) {
            String sqlOwner = " SELECT regn_no||' Found in vt_owner' as regn_no  FROM  " + TableList.VT_OWNER
                    + "  where regn_no=? "
                    + " union "
                    + "SELECT regn_no||' Found in vt_fancy_register' as regn_no  FROM fancy.vt_fancy_register where regn_no=? "
                    + " union "
                    + " SELECT regn_no||' Found in va_fancy_register' as regn_no  FROM va_fancy_register where regn_no=? "
                    + " union "
                    + "SELECT regn_no||' Found in VT_ADVANCE_REGN_NO' as regn_no  FROM  " + TableList.VT_ADVANCE_REGN_NO
                    + " where regn_no=?"
                    + " union "
                    + "SELECT regn_no||' Found in VT_BLOCK_VEH' as regn_no  FROM  " + TableList.VT_BLOCK_VEH
                    + " where regn_no=?"
                    + " union "
                    + " SELECT regn_no ||' Found in fancy.block_regn_no_list' as regn_no  FROM  " + TableList.BLOCK_REGN_LIST
                    + " where regn_no=?"
                    + " union "
                    + " SELECT regn_no ||' Found in VM_REGN_ALLOTED' as regn_no  FROM  " + TableList.VM_REGN_ALLOTED
                    + " where regn_no=?"
                    + " union "
                    + " SELECT regn_no ||' Found in VA_OWNER' as regn_no  FROM  " + TableList.VA_OWNER
                    + " where regn_no=?"
                    + " union "
                    + " SELECT old_regn_no ||' Found in VH_RE_ASSIGN' as regn_no  FROM  " + TableList.VH_RE_ASSIGN
                    + " where old_regn_no=?"
                    + " union "
                    + " SELECT old_regn_no ||' Found in VH_CONVERSION' as regn_no  FROM  " + TableList.VH_CONVERSION
                    + " where old_regn_no=?"
                    + " union "
                    + " SELECT regn_no ||' Found in VA_OWNER_CHOICE_NO' as regn_no  FROM  " + TableList.VA_OWNER_CHOICE_NO
                    + " where regn_no=?"
                    + " union "
                    + " SELECT regn_no ||' Found in VP_ADVANCE_REGN_NO' as regn_no  FROM  " + TableList.VP_ADVANCE_REGN_NO
                    + " where regn_no=?";

            ps = tmg.prepareStatement(sqlOwner);
            int i = 1;
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            ps.setString(i++, regno);
            rs = tmg.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                blFancy = true;
            }
        }
        if (blFancy) {
            status = false;
            //Update vm_regn_available
            String sqlUpdate = "Update vm_regn_available set status='Z' where regn_no=? and state_cd=? and off_cd=? and status='A'";
            ps = tmg.prepareStatement(sqlUpdate);
            ps.setString(1, regno);
            ps.setString(2, stateCode);
            ps.setInt(3, selectedOffCode);
            ps.executeUpdate();
            return status;
        }
        return status;
    }

    public HashMap<String, String> RandonNumberFileMovement(TransactionManager tmgr) throws Exception {

        HashMap<String, String> listProceessed = new HashMap<>();
        String sql = null;
        PreparedStatement ps = null;

        sql = "SELECT a.appl_no FROM VA_RANDOM_REGN_NO  a  INNER JOIN " + TableList.VA_DETAILS
                + " b ON a.appl_no=b.appl_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd"
                + " WHERE a.state_cd=? and a.off_cd=? and a.status = ? and b.entry_status <> ? and b.pur_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getUserStateCode());
        ps.setInt(2, Util.getSelectedSeat().getOff_cd());
        ps.setString(3, TableConstants.RANDOM_NUMBER_STATUS_A);
        ps.setString(4, TableConstants.STATUS_APPROVED);
        ps.setInt(5, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        while (rs.next()) {
            String appl_no = rs.getString("appl_no");
            Status_dobj status = new Status_dobj();
            status.setAppl_no(appl_no);
            status.setPur_cd(TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
            status.setPublic_remark("");
            status.setOffice_remark("");
            status.setStatus(TableConstants.STATUS_COMPLETE);
            status.setAction_cd(Util.getSelectedSeat().getAction_cd());
            status.setCntr_id(Util.getSelectedSeat().getCntr_id());

            status = ServerUtil.webServiceForNextStage(status, tmgr);
            ServerUtil.fileFlow(tmgr, status);

            sql = "UPDATE  VA_RANDOM_REGN_NO "
                    + " SET status=? "
                    + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, TableConstants.RANDOM_NUMBER_STATUS_M);
            ps.setString(2, appl_no);
            ps.executeUpdate();


            listProceessed.put(appl_no, TableConstants.RANDOM_NUMBER_STATUS_M);
        }

        return listProceessed;
    }

    public static String generateTempRegnNo(TransactionManager tmgr, int offCd) throws VahanException {
        String temp_regn = null;
        PreparedStatement ps = null;
        String sql = null;
        String tempAlpha = null;
        int num;
        Exception e = null;
        try {

            sql = "update vm_temp_regn_series set running_no = running_no where  state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, offCd);
            ps.executeUpdate();


            ps = tmgr.prepareStatement("select * from vm_temp_regn_series where "
                    + " running_no < upper_range_no and state_cd=? and off_cd=?");

            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, offCd);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) {
                tempAlpha = rs.getString("prefix_series");
                num = rs.getInt("running_no");

                temp_regn = tempAlpha + CommonUtils.formatNumberPart(num + "");

                sql = "update vm_temp_regn_series set running_no = running_no+1 "
                        + " where  state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, offCd);
                ps.executeUpdate();

            }

        } catch (Exception ee) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            e = ee;
        }

        if (e != null || temp_regn == null) {
            throw new VahanException("Temperary Registration Number Generation Failed");
        }

        return temp_regn;

    }

    public static void main(String[] args) {
        ArrayList<NumberDetail_dobj> list = new ArrayList<>();
        NumberDetail_dobj dobj = new NumberDetail_dobj();
        dobj.setAppl_no("1111");
        dobj.setStatus("A");
        list.add(dobj);


        dobj = new NumberDetail_dobj();
        dobj.setAppl_no("2222");
        dobj.setStatus("A");
        list.add(dobj);

        dobj = new NumberDetail_dobj();
        dobj.setAppl_no("3333");
        dobj.setStatus("A");
        list.add(dobj);

        HashMap<String, String> map = new HashMap<>();
        map.put("1111", "M");
        map.put("2222", "M");
        map.put("3333", "M");

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String appl_no = entry.getKey();
            String status = entry.getValue();
            //logger.info("Appl_no" + appl_no + "  Status" + status);

            for (Iterator<NumberDetail_dobj> it = list.iterator(); it.hasNext();) {
                NumberDetail_dobj numberDetail_dobj = it.next();
                if (numberDetail_dobj.getAppl_no().equals(appl_no)) {
                    numberDetail_dobj.setStatus(status);
                    break;
                }

            }


        }




        for (Iterator<NumberDetail_dobj> it = list.iterator(); it.hasNext();) {
            NumberDetail_dobj numberDetail_dobj = it.next();
            //logger.info("Status   " + numberDetail_dobj.getStatus());
            //logger.info("Appl No   " + numberDetail_dobj.getAppl_no());

        }

    }

    public static String[] getSeriesDetails(String prefix_series) throws SQLException {
        String[] seriesDetails = new String[5];

        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getSeriesPercentageUsed");
            String query = "select prefix_series,upper_range_no,running_no, round(running_no/upper_range_no*100.0,2) as usedper, next_prefix_series from vm_regn_series where prefix_series = ?";
            PreparedStatement pstmt = tmgr.prepareStatement(query);
            pstmt.setString(1, prefix_series);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                seriesDetails[0] = rs.getString("prefix_series");
                seriesDetails[1] = rs.getString("running_no");
                seriesDetails[2] = rs.getString("usedper");
                seriesDetails[3] = rs.getString("upper_range_no");
                seriesDetails[4] = rs.getString("next_prefix_series");


            }
        } catch (Exception e) {
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }


        return seriesDetails;
    }
}
