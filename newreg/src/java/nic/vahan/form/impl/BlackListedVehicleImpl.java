/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.UploadPollutionDataDobj;
import nic.vahan.form.dobj.common.CodeDescrDobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author Afzal
 */
public class BlackListedVehicleImpl {

    private static final Logger LOGGER = Logger.getLogger(BlackListedVehicleImpl.class);

    public Vector getCurrentStatus(String numberEntered, String search_by_regn_no) throws VahanException {
        ArrayList<BlackListedVehicleDobj> blacklist = new ArrayList<BlackListedVehicleDobj>();
        ArrayList<BlackListedVehicleDobj> releasedlist = new ArrayList<BlackListedVehicleDobj>();
        TransactionManager tmgr = null;
        String sql;
        String sqlreleased = null;
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        DateFormat showDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Vector vecDataVector = new Vector();
        String firNo = "";
        String firDt = "";
        BlackListedVehicleDobj blacklistdobj = null;
        try {

            if (search_by_regn_no.equalsIgnoreCase("R")) {
                sql = "select a.*, to_char(a.complain_dt,'dd-Mon-yyyy HH24:mi:ss') as complain_dt_char, b.descr, c.off_name,COALESCE(d.user_name,a.entered_by) AS blocked_by,compounding_amt"
                        + "  from " + TableList.VT_BLACKLIST + " a"
                        + "  left outer join " + TableList.VM_BLACKLIST + " b on b.code = a.complain_type"
                        + "  left outer join " + TableList.TM_OFFICE + " c on c.state_cd = a.state_cd and c.off_cd = a.off_cd"
                        + "  left outer join " + TableList.TM_USER_INFO + " d on d.user_cd = regexp_replace(COALESCE(trim(a.entered_by), '0'), '[^0-9]', '0', 'g')::numeric \n"
                        + "    where a.regn_no=?";
                sqlreleased = "select a.*, to_char(a.complain_dt,'dd-Mon-yyyy HH24:mi:ss') as complain_dt_char,b.descr,COALESCE(c.user_name, a.entered_by) AS blocked_by,COALESCE(d.user_name,a.action_taken_by) as release_by,compounding_amt \n"
                        + " from " + TableList.VH_BLACKLIST + " a \n"
                        + " left outer join " + TableList.VM_BLACKLIST + " b on b.code=a.complain_type \n"
                        + " left outer join " + TableList.TM_USER_INFO + " c on c.user_cd = regexp_replace(COALESCE(trim(a.entered_by), '0'), '[^0-9]', '0', 'g')::numeric \n"
                        + " left outer join " + TableList.TM_USER_INFO + " d on d.user_cd = regexp_replace(COALESCE(trim(a.action_taken_by), '0'), '[^0-9]', '0', 'g')::numeric \n"
                        + " where a.regn_no=? ";
            } else {
                sql = "  select a.*, to_char(a.complain_dt,'dd-Mon-yyyy HH24:mi:ss') as complain_dt_char ,b.descr, c.off_name,COALESCE(d.user_name,a.entered_by) AS blocked_by"
                        + "  from " + TableList.VT_BLACKLIST_CHASSIS + "  a "
                        + "  left outer join " + TableList.VM_BLACKLIST + " b  on b.code=a.complain_type"
                        + "  left outer join " + TableList.TM_OFFICE + " c on c.state_cd = a.state_cd and c.off_cd = a.off_cd"
                        + "  left outer join " + TableList.TM_USER_INFO + " d on d.user_cd = regexp_replace(COALESCE(trim(a.entered_by), '0'), '[^0-9]', '0', 'g')::numeric \n"
                        + "  where a.chasi_no=?";
                sqlreleased = "select a.*, to_char(a.complain_dt,'dd-Mon-yyyy HH24:mi:ss') as complain_dt_char,b.descr,COALESCE(c.user_name,a.entered_by) AS blocked_by,COALESCE(d.user_name,a.action_taken_by) as release_by \n"
                        + " from " + TableList.VH_BLACKLIST_CHASSIS + " a \n"
                        + " left outer join " + TableList.VM_BLACKLIST + " b on b.code=a.complain_type \n"
                        + " left outer join " + TableList.TM_USER_INFO + " c on c.user_cd = regexp_replace(COALESCE(trim(a.entered_by), '0'), '[^0-9]', '0', 'g')::numeric \n"
                        + " left outer join " + TableList.TM_USER_INFO + " d on d.user_cd = regexp_replace(COALESCE(trim(a.action_taken_by), '0'), '[^0-9]', '0', 'g')::numeric \n"
                        + " where a.chasi_no=? ";
            }
            tmgr = new TransactionManager("getCurrentStatus-1");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, numberEntered);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                blacklistdobj = new BlackListedVehicleDobj();
                if (search_by_regn_no.equalsIgnoreCase("R")) {
                    blacklistdobj.setRegin_no(rs.getString("regn_no"));
                } else {
                    blacklistdobj.setChasi_no(rs.getString("chasi_no"));
                }
                blacklistdobj.setOfficeName(rs.getString("off_name"));
                if (rs.getString("fir_no") != null) {
                    firNo = rs.getString("fir_no");
                } else {
                    firNo = "";
                }
                if (rs.getDate("fir_dt") != null) {
                    firDt = showDateFormat.format(rs.getDate("fir_dt"));
                } else {
                    firDt = "";
                }
                blacklistdobj.setComplain_type(rs.getInt("complain_type"));
                if (rs.getInt("complain_type") == 1) {
                    blacklistdobj.setComplain("FIR No: " + firNo + " Dated: " + firDt + " @Police Station: " + rs.getString("complain"));
                    blacklistdobj.setFirNo(rs.getString("fir_no"));
                    blacklistdobj.setFirDate(rs.getDate("fir_dt"));
                    blacklistdobj.setPoliceStation(rs.getString("complain"));
                } else if (rs.getInt("complain_type") == 62) {
                    blacklistdobj.setComplain("FILE No: " + firNo);
                } else {
                    blacklistdobj.setComplain(rs.getString("complain"));
                }
                blacklistdobj.setComplain_dt(rs.getDate("complain_dt"));
                blacklistdobj.setComplaindt(rs.getString("complain_dt_char"));
                blacklistdobj.setEntered_by(rs.getString("entered_by"));
                blacklistdobj.setOff_cd(rs.getInt("off_cd"));
                blacklistdobj.setState_cd(rs.getString("state_cd"));
                blacklistdobj.setComplainDesc(rs.getString("descr"));
                blacklistdobj.setBlocked_by(rs.getString("blocked_by"));
                if (search_by_regn_no.equalsIgnoreCase("R")) {
                    blacklistdobj.setCompounding_amt(rs.getLong("compounding_amt"));
                }
                blacklist.add(blacklistdobj);
            }
            tmgr = new TransactionManager("getCurrentStatus-2");
            PreparedStatement psrelease = tmgr.prepareStatement(sqlreleased);
            psrelease.setString(1, numberEntered);
            RowSet rsReleased = tmgr.fetchDetachedRowSet();
            BlackListedVehicleDobj releasedlistdobj = null;
            while (rsReleased.next()) {
                releasedlistdobj = new BlackListedVehicleDobj();
                if (search_by_regn_no.equalsIgnoreCase("R")) {
                    releasedlistdobj.setRegin_no(rsReleased.getString("regn_no"));
                } else {
                    releasedlistdobj.setChasi_no(rsReleased.getString("chasi_no"));
                }
                if (rsReleased.getString("fir_no") != null) {
                    firNo = rsReleased.getString("fir_no");
                } else {
                    firNo = "";
                }
                if (rsReleased.getDate("fir_dt") != null) {
                    firDt = showDateFormat.format(rsReleased.getDate("fir_dt"));
                } else {
                    firDt = "";
                }
                releasedlistdobj.setComplain_type(rsReleased.getInt("complain_type"));
                if (rsReleased.getInt("complain_type") == 1) {
                    releasedlistdobj.setComplain("FIR No: " + firNo + " Dated: " + firDt + " @Police Station: " + rsReleased.getString("complain"));
                } else if (rsReleased.getInt("complain_type") == 62) {
                    releasedlistdobj.setComplain("FILE No: " + firNo);
                } else {
                    releasedlistdobj.setComplain(rsReleased.getString("complain"));
                }
                releasedlistdobj.setComplain_dt(rsReleased.getDate("complain_dt"));
                releasedlistdobj.setComplaindt(Utility.convertdateFormatString(rsReleased.getString("complain_dt")));
                releasedlistdobj.setComplainDesc(rsReleased.getString("descr"));
                releasedlistdobj.setActiontaken(rsReleased.getString("action_taken"));
                releasedlistdobj.setActiondt(Utility.convertdateFormatString(rsReleased.getString("action_dt")));
                releasedlistdobj.setBlocked_by(rsReleased.getString("blocked_by"));
                releasedlistdobj.setReleased_by(rsReleased.getString("release_by"));
                if (search_by_regn_no.equalsIgnoreCase("R")) {
                    releasedlistdobj.setCompounding_amt(rsReleased.getLong("compounding_amt"));
                }
                releasedlist.add(releasedlistdobj);
            }
            vecDataVector.add(0, blacklist);
            vecDataVector.add(1, releasedlist);

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return vecDataVector;
    }

    public BlackListedVehicleDobj unblockActionTaken(String selectedNo, String selectedfdate, Boolean selected_regn_no) throws ParseException, VahanException {
        BlackListedVehicleDobj blacklistedVehicle_dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String sql;
        boolean rsNotExecuted = true;
        try {
            tmgr = new TransactionManager("unblockActionTaken");
            if (selected_regn_no) {
                sql = "select a.*, b.off_cd as regn_off_cd "
                        + " from " + TableList.VT_BLACKLIST + " a "
                        + " left outer join " + TableList.VT_OWNER + " b on b.state_cd = a.state_cd and b.regn_no = a.regn_no "
                        + " where a.regn_no = ? and to_char(complain_dt,'dd-Mon-yyyy HH24:mi:ss')=? and "
                        + " a.state_cd = ? and (a.off_cd = ? or b.off_cd = ?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, selectedNo);
                ps.setString(2, selectedfdate);
                ps.setString(3, Util.getUserStateCode());
                ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                ps.setInt(5, Util.getSelectedSeat().getOff_cd());
            } else {
                sql = "select * from " + TableList.VT_BLACKLIST_CHASSIS + " where chasi_no=? and to_char(complain_dt,'dd-Mon-yyyy HH24:mi:ss') = ? and state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, selectedNo);
                ps.setString(2, selectedfdate);
                ps.setString(3, Util.getUserStateCode());
                ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            }
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                rsNotExecuted = false;
                blacklistedVehicle_dobj = new BlackListedVehicleDobj();
                if (selected_regn_no) {
                    blacklistedVehicle_dobj.setRegin_no(rs.getString("regn_no"));
                } else {
                    blacklistedVehicle_dobj.setChasi_no(rs.getString("chasi_no"));
                }
                blacklistedVehicle_dobj.setComplain_type(rs.getInt("complain_type"));
                if (rs.getInt("complain_type") == 1) {
                    blacklistedVehicle_dobj.setFirNo(rs.getString("fir_no"));
                    blacklistedVehicle_dobj.setFirDate(rs.getDate("fir_dt"));
                    blacklistedVehicle_dobj.setPoliceStation(rs.getString("complain"));
                } else if (rs.getInt("complain_type") == 62) {
                    blacklistedVehicle_dobj.setFirNo(rs.getString("fir_no"));
                    if (selected_regn_no) {
                        blacklistedVehicle_dobj.setCompounding_amt(rs.getLong("compounding_amt"));
                    }
                } else {
                    blacklistedVehicle_dobj.setComplain(rs.getString("complain"));
                }

                blacklistedVehicle_dobj.setComplain_dt(rs.getDate("complain_dt"));
                blacklistedVehicle_dobj.setEntered_by(rs.getString("entered_by"));
                blacklistedVehicle_dobj.setOff_cd(rs.getInt("off_cd"));
                blacklistedVehicle_dobj.setState_cd(rs.getString("state_cd"));
            }
            if (rsNotExecuted) {
                throw new VahanException("You are not authorized to Release this vehicle because it is blocked by other Office!!!");
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
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return blacklistedVehicle_dobj;

    }

    public boolean insertIntoBlacklistedVehicle(BlackListedVehicleDobj dobj, boolean searchByregnN0) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String sql;
        boolean insertsuccess = false;
        if (dobj.getComplain_dt().after(ServerUtil.getSystemDateInPostgres())) {
            throw new VahanException("Complain Date can't be greater than current date.");
        }
        try {
            tmgr = new TransactionManager("insertIntoBlacklistedVehicle");
            if (searchByregnN0) {
                sql = "INSERT INTO " + TableList.VT_BLACKLIST + "(state_cd,off_cd,regn_no,complain_type,fir_no,fir_dt,complain,complain_dt,entered_by,compounding_amt) "
                        + " VALUES (?, ?, ?,?,?,?,?,?,?,?)";
            } else {
                sql = "INSERT INTO " + TableList.VT_BLACKLIST_CHASSIS + "(state_cd,off_cd,chasi_no,complain_type,fir_no,fir_dt,complain,complain_dt,entered_by) "
                        + " VALUES (?, ?, ?,?,?,?,?,?,?)";
            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            if (searchByregnN0) {
                ps.setString(3, dobj.getRegin_no());
            } else {
                ps.setString(3, dobj.getChasi_no());
            }
            ps.setInt(4, dobj.getComplain_type());
            ps.setString(5, dobj.getFirNo());
            if (dobj.getFirDate() != null) {
                ps.setDate(6, new java.sql.Date(dobj.getFirDate().getTime()));
            } else {
                ps.setDate(6, null);
            }
            if (dobj.getComplain_type() == 1) {

                ps.setString(7, dobj.getPoliceStation());
            } else if (dobj.getComplain() != null) {
                ps.setString(7, dobj.getComplain());
            } else {
                ps.setString(7, "");
            }
            ps.setTimestamp(8, new java.sql.Timestamp(dobj.getComplain_dt().getTime()));
            ps.setString(9, Util.getEmpCode());
            if (searchByregnN0) {
                ps.setLong(10, dobj.getCompounding_amt());
            }
            int i = ps.executeUpdate();
            if (i == 1) {
                insertsuccess = true;
            }
            if (insertsuccess = true) {
                tmgr.commit();
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
        return insertsuccess;
    }

    public List<String> getDetailsChassisNo(String chassisNo) throws VahanException {
        boolean isExist;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String sql;
        RowSet rs;
        boolean regn_no_blcklisted;
        List<String> blacklistedvehiclelist = new ArrayList<String>();
        SimpleDateFormat farmater = new SimpleDateFormat("yyyy-MM-dd");
        try {
            tmgr = new TransactionManager("getDetailsChassisNo");
            sql = "select chasi_no,regn_no, regn_dt from  " + TableList.VT_OWNER
                    + " where chasi_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, chassisNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isExist = true;
                regn_no_blcklisted = checkRegnNoForBlackList(rs.getString("regn_no"));
                blacklistedvehiclelist.add(isExist + "");
                blacklistedvehiclelist.add(regn_no_blcklisted + "");
                blacklistedvehiclelist.add(rs.getString("regn_no"));
                String regnDt = farmater.format(rs.getDate("regn_dt"));
                blacklistedvehiclelist.add(regnDt);
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
        return blacklistedvehiclelist;
    }

    public List<String> getAlreadyRegnNoBlacklisted(String RegnNo) throws VahanException {
        boolean isExist;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String sql;
        RowSet rs;
        boolean chassis_no_blcklisted;
        List<String> chassisAlreadyBlacklisted = new ArrayList<String>();
        try {
            tmgr = new TransactionManager("getAlreadyRegnNoBlacklisted");
            sql = "select a.regn_no,b.off_name,COALESCE(c.user_name,a.entered_by) as user_name from " + TableList.VT_BLACKLIST + " a "
                    + " left outer join " + TableList.TM_OFFICE + " b on b.state_cd = a.state_cd and b.off_cd = a.off_cd  "
                    + " left outer join " + TableList.TM_USER_INFO + " c on c.user_cd = regexp_replace(COALESCE(trim(a.entered_by), '0'), '[^0-9]', '0', 'g')::numeric "
                    + " where a.regn_no=? order by a.complain_dt desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, RegnNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isExist = true;
                chassis_no_blcklisted = checkRegnNoForBlackList(rs.getString("regn_no"));
                chassisAlreadyBlacklisted.add(isExist + "");
                chassisAlreadyBlacklisted.add(chassis_no_blcklisted + "");
                chassisAlreadyBlacklisted.add(rs.getString("regn_no"));
                chassisAlreadyBlacklisted.add(rs.getString("off_name"));
                chassisAlreadyBlacklisted.add(rs.getString("user_name"));
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
        return chassisAlreadyBlacklisted;
    }

    public List<String> getAlreadyChassisNoBlacklisted(String chassisNo) throws VahanException {
        boolean isExist;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String sql;
        RowSet rs;
        boolean chassis_no_blcklisted;
        List<String> chassisAlreadyBlacklisted = new ArrayList<String>();
        try {
            tmgr = new TransactionManager("getAlreadyChassisNoBlacklisted");
            sql = "select a.chasi_no,b.off_name,COALESCE(c.user_name,a.entered_by) as user_name from " + TableList.VT_BLACKLIST_CHASSIS + " a "
                    + " left outer join " + TableList.TM_OFFICE + " b on b.state_cd = a.state_cd and b.off_cd = a.off_cd  "
                    + " left outer join " + TableList.TM_USER_INFO + " c on c.user_cd = regexp_replace(COALESCE(trim(a.entered_by), '0'), '[^0-9]', '0', 'g')::numeric "
                    + " where a.chasi_no=? order by a.complain_dt desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, chassisNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isExist = true;
                chassis_no_blcklisted = checkRegnNoForBlackList(rs.getString("chasi_no"));
                chassisAlreadyBlacklisted.add(isExist + "");
                chassisAlreadyBlacklisted.add(chassis_no_blcklisted + "");
                chassisAlreadyBlacklisted.add(rs.getString("chasi_no"));
                chassisAlreadyBlacklisted.add(rs.getString("off_name"));
                chassisAlreadyBlacklisted.add(rs.getString("user_name"));
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
        return chassisAlreadyBlacklisted;
    }

    public List<String> getDetailsRegnNo(String regnNo) throws VahanException {
        boolean isExist;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String sql;

        List<String> blacklistedvehiclelist = new ArrayList<String>();

        String BlackListedDetails = null;
        RowSet rs;
        SimpleDateFormat farmater = new SimpleDateFormat("yyyy-MM-dd");
        try {
            tmgr = new TransactionManager("getDetailsRegnNo");
            sql = "select regn_no,chasi_no,regn_dt from  " + TableList.VT_OWNER
                    + " where regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isExist = true;
                BlackListedDetails = checkChassisNoForBlackList(rs.getString("chasi_no"));
                blacklistedvehiclelist.add(isExist + "");
                if (BlackListedDetails != null) {
                    blacklistedvehiclelist.add(true + "");
                } else {
                    blacklistedvehiclelist.add(false + "");
                }
                blacklistedvehiclelist.add(rs.getString("chasi_no"));
                String regnDt = farmater.format(rs.getDate("regn_dt"));
                blacklistedvehiclelist.add(regnDt);
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
        return blacklistedvehiclelist;
    }

    public String checkChassisNoForBlackList(String ChassisNo) throws VahanException {
        String BlackListedDetails = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String sqlchassis;
        try {

            tmgr = new TransactionManager("checkChassisNoForBlackList");
            sqlchassis = " select a.chasi_no,b.descr,c.off_name,COALESCE(d.user_name,a.entered_by) AS blocked_by,a.state_cd \n"
                    + " from  " + TableList.VT_BLACKLIST_CHASSIS + " a\n"
                    + " left outer join " + TableList.VM_BLACKLIST + " b  on b.code=a.complain_type \n"
                    + " left outer join " + TableList.TM_OFFICE + "  c on c.state_cd = a.state_cd and c.off_cd = a.off_cd \n"
                    + " left outer join " + TableList.TM_USER_INFO + " d on d.user_cd = regexp_replace(COALESCE(trim(a.entered_by), '0'), '[^0-9]', '0', 'g')::numeric \n"
                    + " where a.chasi_no=?";
            ps = tmgr.prepareStatement(sqlchassis);
            ps.setString(1, ChassisNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                BlackListedDetails = "Vehicle with Chassis No \"" + ChassisNo.toUpperCase() + "\" is Blacklisted on Registering Authority      \"" + rs.getString("off_name").toUpperCase() + ", " + rs.getString("state_cd").toUpperCase() + "\"";
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
        return BlackListedDetails;
    }

    public boolean checkRegnNoForBlackList(String RegnNo) throws VahanException {
        boolean isBlackListed = false;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String sqlchassis;
        try {

            tmgr = new TransactionManager("checkRegnNoForBlackList");
            sqlchassis = "select regn_no from  " + TableList.VT_BLACKLIST
                    + " where regn_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sqlchassis);
            ps.setString(1, RegnNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isBlackListed = true;
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
        return isBlackListed;
    }

    public void insertIntoHistBlacklistedVehicle(TransactionManager tmgr, BlackListedVehicleDobj unblockdobj, boolean addByRegnno) throws VahanException {
        PreparedStatement ps;
        String sql;
        try {
            if (addByRegnno) {
                sql = " INSERT INTO " + TableList.VH_BLACKLIST + "(regn_no,complain,complain_dt,complain_type,state_cd,off_cd,entered_by,action_taken,action_dt,action_taken_by,fir_no,fir_dt,compounding_amt) "
                        + " VALUES (?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,?,?,?,?)";
            } else {
                sql = " INSERT INTO " + TableList.VH_BLACKLIST_CHASSIS + "(chasi_no,complain,complain_dt,complain_type,state_cd,off_cd,entered_by,action_taken,action_dt,action_taken_by,fir_no,fir_dt) "
                        + " VALUES (?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,?,?,?)";
            }
            ps = tmgr.prepareStatement(sql);
            if (unblockdobj.getRegin_no() != null) {
                ps.setString(1, unblockdobj.getRegin_no());
            } else {
                ps.setString(1, unblockdobj.getChasi_no());
            }
            if (unblockdobj.getComplain_type() != 1 && unblockdobj.getComplain_type() != 62) {
                ps.setString(2, unblockdobj.getComplain());
            } else if (unblockdobj.getComplain_type() == 62) {
                ps.setString(2, unblockdobj.getFirNo());
            } else {
                ps.setString(2, unblockdobj.getPoliceStation());
            }

            if (unblockdobj.getComplain_type() == 0 || unblockdobj.getComplain_type() == -1) {
                throw new VahanException("Vehicle could not release with Invalid complain Type !!!");
            }

            if (unblockdobj.getComplain_dt() == null || unblockdobj.getComplain_dt().equals("")) {
                throw new VahanException("Vehicle could not release with blank complain Date!!!");
            } else {
                ps.setTimestamp(3, new java.sql.Timestamp(unblockdobj.getComplain_dt().getTime()));
            }

            ps.setInt(4, unblockdobj.getComplain_type());
            ps.setString(5, Util.getUserStateCode());
            ps.setInt(6, Util.getSelectedSeat().getOff_cd());
            ps.setString(7, unblockdobj.getEntered_by());
            ps.setString(8, unblockdobj.getActiontaken());
            ps.setString(9, Util.getEmpCode());
            ps.setString(10, unblockdobj.getFirNo());
            if (unblockdobj.getFirDate() != null) {
                ps.setDate(11, new java.sql.Date(unblockdobj.getFirDate().getTime()));
            } else {
                ps.setDate(11, null);
            }
            if (addByRegnno) {
                ps.setLong(12, unblockdobj.getCompounding_amt());
            }
            ps.executeUpdate();
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void deleteCurrentRecord(TransactionManager tmgr, String regnNoORchasiNo, String complainDate, boolean addByRegnNo) throws VahanException {
        PreparedStatement ps;
        String sql;
        try {
            if (addByRegnNo) {
                sql = "delete from " + TableList.VT_BLACKLIST + " where regn_no=? and  to_char(complain_dt,'dd-Mon-yyyy HH24:mi:ss')=? and state_cd=?";
            } else {
                sql = "delete from " + TableList.VT_BLACKLIST_CHASSIS + " where chasi_no=? and  to_char(complain_dt,'dd-Mon-yyyy HH24:mi:ss')=? and state_cd=? ";
            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNoORchasiNo);
            ps.setString(2, complainDate);
            ps.setString(3, Util.getUserStateCode());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public boolean unblockcurrentrow(BlackListedVehicleDobj unblockdobj, boolean addByRegnNo) throws VahanException {
        TransactionManager tmgr = null;
        boolean istrueOrfalse;
        try {
            istrueOrfalse = true;
            tmgr = new TransactionManager("unblockcurrentrow");
            if (addByRegnNo) {
                deleteCurrentRecord(tmgr, unblockdobj.getRegin_no(), unblockdobj.getDeletecomplaindt(), addByRegnNo);
                insertIntoHistBlacklistedVehicle(tmgr, unblockdobj, addByRegnNo);
            } else {
                deleteCurrentRecord(tmgr, unblockdobj.getChasi_no(), unblockdobj.getDeletecomplaindt(), addByRegnNo);
                insertIntoHistBlacklistedVehicle(tmgr, unblockdobj, addByRegnNo);
            }
            tmgr.commit();
        } catch (VahanException ve) {
            istrueOrfalse = false;
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            istrueOrfalse = false;
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                istrueOrfalse = false;
            }
        }
        return istrueOrfalse;
    }

    public CodeDescrDobj checkForBlacklistedVehicle(String regnNo, String chasisNo) throws VahanException {

        CodeDescrDobj status = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String sql = null;
        try {
            tmgr = new TransactionManager("checkForBlacklistedVehicle-1");
            sql = "SELECT regn_no,code,descr FROM " + TableList.VT_BLACKLIST + " a," + TableList.VM_BLACKLIST + " b "
                    + " WHERE a.complain_type = b.code and a.regn_no=? and a.state_cd=? and a.off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                status = new CodeDescrDobj();
                status.setCode(rs.getInt("code"));
                status.setDescr(rs.getString("descr"));
            }

            sql = "SELECT chasi_no,code,descr FROM " + TableList.VT_BLACKLIST_CHASSIS + " a," + TableList.VM_BLACKLIST + " b"
                    + " WHERE a.complain_type = b.code and a.chasi_no=? and a.state_cd=? and a.off_cd=?";
            tmgr = new TransactionManager("checkForBlacklistedVehicle-2");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, chasisNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                status = new CodeDescrDobj();
                status.setCode(rs.getInt("code"));
                status.setDescr(rs.getString("descr"));
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }

        return status;
    }

    public BlackListedVehicleDobj getBlacklistedVehicleDetails(String regnNo, String chasisNo) throws VahanException {

        BlackListedVehicleDobj blackListedVehicleDobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String sql = null;
        try {
            tmgr = new TransactionManager("getBlacklistedVehicleDetails-1");
            sql = "SELECT a.*,b.descr as blackListDescr,c.descr as state_name,d.off_name "
                    + " FROM " + TableList.VT_BLACKLIST + " a "
                    + " LEFT JOIN " + TableList.VM_BLACKLIST + " b on a.complain_type = b.code "
                    + " LEFT JOIN " + TableList.TM_STATE + " c ON c.state_code = a.state_cd "
                    + " LEFT JOIN " + TableList.TM_OFFICE + " d ON d.off_cd = a.off_cd AND d.state_cd = a.state_cd "
                    + " WHERE a.regn_no=? order by a.complain_dt desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                blackListedVehicleDobj = new BlackListedVehicleDobj();
                blackListedVehicleDobj.setState_cd(rs.getString("state_cd"));
                blackListedVehicleDobj.setStateName(rs.getString("state_name"));
                blackListedVehicleDobj.setOff_cd(rs.getInt("off_cd"));
                blackListedVehicleDobj.setOfficeName(rs.getString("off_name"));
                blackListedVehicleDobj.setComplain_type(rs.getInt("complain_type"));
                blackListedVehicleDobj.setComplainDesc(rs.getString("blackListDescr"));
                blackListedVehicleDobj.setFirDate(rs.getDate("fir_dt"));
                blackListedVehicleDobj.setComplain_dt(rs.getDate("complain_dt"));
                blackListedVehicleDobj.setCompounding_amt(rs.getLong("compounding_amt"));
                blackListedVehicleDobj.setRegn_no(rs.getString("regn_no"));
                blackListedVehicleDobj.setComplaindt(DateUtil.parseDateToString(rs.getDate("complain_dt")));
            } else {
                tmgr = new TransactionManager("getBlacklistedVehicleDetails-2");
                sql = "SELECT a.*,b.descr as blackListDescr,c.descr as state_name,d.off_name "
                        + " FROM " + TableList.VT_BLACKLIST_CHASSIS + " a "
                        + " LEFT JOIN " + TableList.VM_BLACKLIST + " b on a.complain_type = b.code "
                        + " LEFT JOIN " + TableList.TM_STATE + "  c ON c.state_code = a.state_cd "
                        + " LEFT JOIN " + TableList.TM_OFFICE + " d ON d.off_cd = a.off_cd AND d.state_cd = a.state_cd "
                        + " WHERE a.chasi_no=? order by a.complain_dt desc limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, chasisNo);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    blackListedVehicleDobj = new BlackListedVehicleDobj();
                    blackListedVehicleDobj.setState_cd(rs.getString("state_cd"));
                    blackListedVehicleDobj.setStateName(rs.getString("state_name"));
                    blackListedVehicleDobj.setOff_cd(rs.getInt("off_cd"));
                    blackListedVehicleDobj.setOfficeName(rs.getString("off_name"));
                    blackListedVehicleDobj.setComplain_type(rs.getInt("complain_type"));
                    blackListedVehicleDobj.setComplainDesc(rs.getString("blackListDescr"));
                    blackListedVehicleDobj.setFirDate(rs.getDate("fir_dt"));
                    blackListedVehicleDobj.setComplain_dt(rs.getDate("complain_dt"));
                    blackListedVehicleDobj.setComplaindt(DateUtil.parseDateToString(rs.getDate("complain_dt")));
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }

        return blackListedVehicleDobj;
    }

    public boolean isDupComplainDate(String complain_dt, String radioBtnvalue, String regnOrChasiNo) throws VahanException {
        boolean isDup = false;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String sqlchassis = null;
        try {
            tmgr = new TransactionManager("isDupComplaineDate");
            if (radioBtnvalue.equals("R")) {
                sqlchassis = "select * from " + TableList.VT_BLACKLIST + " where regn_no=? and to_char(complain_dt,'dd-Mon-yyyy HH24:mi:ss')=?";
            } else if (radioBtnvalue.equals("C")) {
                sqlchassis = "select * from " + TableList.VT_BLACKLIST_CHASSIS + " where chasi_no=? and to_char(complain_dt,'dd-Mon-yyyy HH24:mi:ss') = ?";
            }
            ps = tmgr.prepareStatement(sqlchassis);
            ps.setString(1, regnOrChasiNo);
            ps.setString(2, complain_dt);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isDup = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return isDup;
    }

    public Map<Integer, Object> SavePollutionData(List<UploadPollutionDataDobj> uploadPollutionDataDobjList)
            throws Exception, SQLException {
        Map<Integer, Object> returnMap = new HashMap<Integer, Object>();
        int totalrecord = 0;
        List<UploadPollutionDataDobj> failUploadPollutionDataDobjList = new ArrayList<>();
        List<UploadPollutionDataDobj> successUploadPollutionDataDobjList = new ArrayList<>();
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        Iterator itr = uploadPollutionDataDobjList.iterator();
        boolean status = true;
        try {
            tmgr = new TransactionManager("SavePollutionData");
            String sql = "INSERT INTO  " + TableList.VT_BLACKLIST + " ("
                    + "    state_cd, off_cd, regn_no, complain_type, fir_no, fir_dt, complain, entered_by, complain_dt)"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";
            psmt = tmgr.prepareStatement(sql);
            while (itr.hasNext()) {
                UploadPollutionDataDobj uploadPollutionDataDobj = (UploadPollutionDataDobj) itr.next();
                try {

                    status = chechBacklist(uploadPollutionDataDobj.getRegn_no(), uploadPollutionDataDobj.getFir_dt());
                    if (status == false) {

                        psmt.setString(1, uploadPollutionDataDobj.getState_cd());
                        psmt.setInt(2, uploadPollutionDataDobj.getOff_cd());
                        psmt.setString(3, uploadPollutionDataDobj.getRegn_no());
                        psmt.setInt(4, uploadPollutionDataDobj.getComplain_type());
                        psmt.setString(5, uploadPollutionDataDobj.getFir_no());
                        psmt.setDate(6, new java.sql.Date(uploadPollutionDataDobj.getFir_dt().getTime()));
                        psmt.setString(7, uploadPollutionDataDobj.getComplain());
                        psmt.setString(8, Util.getEmpCode());
                        psmt.setTimestamp(9, new java.sql.Timestamp(uploadPollutionDataDobj.getFir_dt().getTime()));
                        psmt.addBatch();
                        successUploadPollutionDataDobjList.add(uploadPollutionDataDobj);
                        totalrecord = totalrecord + 1;
                    } else {
                        failUploadPollutionDataDobjList.add(uploadPollutionDataDobj);
                    }
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException(TableConstants.SomthingWentWrong);
                }
            }
            psmt.executeBatch();
            tmgr.commit();
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        returnMap.put(1, totalrecord);
        returnMap.put(2, failUploadPollutionDataDobjList);
        returnMap.put(3, successUploadPollutionDataDobjList);
        return returnMap;

    }

    public boolean chechBacklist(String regn_no, Date firDate) throws VahanException {
        boolean flag = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("chechBacklist");
            ps = tmgr.prepareStatement("SELECT regn_no"
                    + " FROM " + TableList.VT_BLACKLIST + " where regn_no=? and fir_dt=?");
            ps.setString(1, regn_no);
            ps.setDate(2, new java.sql.Date(firDate.getTime()));
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = true;
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
        return flag;
    }

    public void insertIntoVhBlacklistFromVtBlacklist(TransactionManager tmgr, String regn_no, String state_cd, int off_cd, String rcpt_no) throws VahanException {
        PreparedStatement ps;
        String sql;
        try {
            if ("TN".contains(state_cd)) {
                sql = "insert into " + TableList.VH_BLACKLIST + " select state_cd,off_cd,"
                        + " regn_no,complain_type, fir_no, fir_dt, complain,complain_dt,entered_by, "
                        + " 'Amount Paid Against Receipt Number " + rcpt_no + "' ,current_timestamp,?,compounding_amt "
                        + " FROM " + TableList.VT_BLACKLIST
                        + " where regn_no=? and state_cd = ? and off_cd = ? and complain_type = ? order by complain_dt desc limit 1";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, regn_no);
                ps.setString(3, state_cd);
                ps.setInt(4, off_cd);
                ps.setInt(5, TableConstants.BLCompoundingAmtCode);
                ps.executeUpdate();

                sql = "delete from " + TableList.VT_BLACKLIST
                        + " where (state_cd,off_cd,regn_no,complain_dt) in ( select state_cd,off_cd,regn_no,complain_dt FROM " + TableList.VT_BLACKLIST
                        + " WHERE regn_no=? and state_cd = ? and off_cd = ? and complain_type = ? order by complain_dt desc limit 1)";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                ps.setInt(4, TableConstants.BLCompoundingAmtCode);
                ps.executeUpdate();
            } else {
                sql = "insert into " + TableList.VH_BLACKLIST + " select state_cd,off_cd,"
                        + " regn_no,complain_type, fir_no, fir_dt, complain,complain_dt,entered_by, "
                        + " 'Amount Paid Against Receipt Number " + rcpt_no + "' ,current_timestamp,?,compounding_amt "
                        + " FROM " + TableList.VT_BLACKLIST
                        + " where regn_no=? and state_cd = ? and complain_type = ? order by complain_dt desc limit 1";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, regn_no);
                ps.setString(3, state_cd);
                ps.setInt(4, TableConstants.BLCompoundingAmtCode);
                ps.executeUpdate();

                sql = "delete from " + TableList.VT_BLACKLIST
                        + " where (state_cd,regn_no,complain_dt) in ( select state_cd,regn_no,complain_dt FROM " + TableList.VT_BLACKLIST
                        + " WHERE regn_no=? and state_cd = ? and complain_type = ? order by complain_dt desc limit 1)";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no);
                ps.setString(2, state_cd);
                ps.setInt(3, TableConstants.BLCompoundingAmtCode);
                ps.executeUpdate();
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public boolean saveUntracedVehicleDetail(BlackListedVehicleDobj dobj) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String sql;
        boolean insertsuccess = false;
//        if (dobj.getComplain_dt().after(ServerUtil.getSystemDateInPostgres())) {
//            throw new VahanException("Complain Date can't be greater than current date.");
//        }
        try {
            tmgr = new TransactionManager("insertIntoBlacklistedVehicle");

            sql = "INSERT INTO " + TableList.VT_UNTRACED_VEHICLE + "(state_cd,off_cd,regn_no,chasi_no,vh_class,colour,dist_cd,fir_no,police_station,under_section,order_no,order_dt,entered_on,entered_by) "
                    + " VALUES (?, ?, ?,?,?,?,?,?,?,?,?,?,current_timestamp,?)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, dobj.getRegin_no());
            ps.setString(4, dobj.getChasi_no());
            ps.setInt(5, dobj.getVh_class());
            ps.setString(6, dobj.getColour());
            ps.setInt(7, dobj.getDistrict());
            ps.setString(8, dobj.getFirNo());
            ps.setString(9, dobj.getPoliceStation());
            ps.setString(10, dobj.getUnderSection());
            ps.setString(11, dobj.getCourt_order_no());
            ps.setTimestamp(12, new java.sql.Timestamp(dobj.getOrder_dt().getTime()));
            ps.setInt(13, Integer.parseInt(Util.getEmpCode()));
            int i = ps.executeUpdate();
            if (i == 1) {
                insertsuccess = true;
            }
            if (insertsuccess) {
                tmgr.commit();
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
        return insertsuccess;
    }

    public Date getFirDetail(String sql) throws VahanException {
        Date fir_date = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs;
        try {
            tmgr = new TransactionManager("getDetailsRegnNo");
            ps = tmgr.prepareStatement(sql);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                fir_date = rs.getDate("fir_dt");
            }
            if (fir_date == null) {
                throw new VahanException("Fir date not found for the untraced vehicle !!");
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
        return fir_date;
    }

    public List<BlackListedVehicleDobj> getUntracedRCDetail(String regn_no, String untracedRegnChasiNoRadiobtn) throws VahanException {
        List<BlackListedVehicleDobj> dobjList = new ArrayList<BlackListedVehicleDobj>();
        BlackListedVehicleDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String sqlchassis;
        String whereClauseforUntracedReport = "";
        try {
            if (untracedRegnChasiNoRadiobtn == null || untracedRegnChasiNoRadiobtn.equalsIgnoreCase("REGNNO")) {
                whereClauseforUntracedReport = "a.regn_no='" + regn_no.toUpperCase() + "'";
            } else if (untracedRegnChasiNoRadiobtn.equalsIgnoreCase("CHASINO")) {
                whereClauseforUntracedReport = "a.chasi_no='" + regn_no.toUpperCase() + "'";
            }
            tmgr = new TransactionManager("checkRegnNoForBlackList");
            sqlchassis = "select a.regn_no,a.chasi_no,a.colour,a.fir_no,a.police_station,a.under_section,a.order_no,to_char(a.order_dt,'dd-Mon-yyyy') as order_dt,d.descr as vh_class_descr,to_char(a.entered_on,'dd-Mon-yyyy HH24:mi:ss') as entered_on,"
                    + " to_char(current_timestamp,'dd-Mon-yyyy HH24:mi:ss') as printed_on,b.user_name as entered_by,COALESCE(c.descr,'') as district_name,e.rcpt_heading,e.rcpt_subheading,"
                    + " f.off_name "
                    + " from  " + TableList.VT_UNTRACED_VEHICLE + " a "
                    + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " b on b.user_cd=regexp_replace(COALESCE(trim(a.entered_by::text), '0'), '[^0-9]', '0', 'g')::numeric \n"
                    + " LEFT OUTER JOIN " + TableList.TM_DISTRICT + " c on a.dist_cd=c.dist_cd \n"
                    + " LEFT OUTER JOIN " + TableList.VM_VH_CLASS + " d on d.vh_class = a.vh_class \n"
                    + " LEFT OUTER JOIN " + TableList.TM_CONFIGURATION + " e on e.state_cd=a.state_cd \n"
                    + " LEFT JOIN " + TableList.TM_OFFICE + " f ON f.off_cd = a.off_cd AND f.state_cd = a.state_cd \n"
                    + " where " + whereClauseforUntracedReport + " limit 1";
            ps = tmgr.prepareStatement(sqlchassis);
            // ps.setString(1, regn_no);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new BlackListedVehicleDobj();
                dobj.setUntracedReportOfBlacklistedForSwappingUse(true);
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setVh_class_descr(rs.getString("vh_class_descr"));
                dobj.setColour(rs.getString("colour"));
                dobj.setFirNo(rs.getString("fir_no"));
                dobj.setPoliceStation(rs.getString("police_station"));
                dobj.setUnderSection(rs.getString("under_section"));
                dobj.setDisplay_order_dt(rs.getString("order_dt"));
                dobj.setEntered_by(rs.getString("entered_by"));
                dobj.setPrinted_on(rs.getString("printed_on"));
                dobj.setEntered_on(rs.getString("entered_on"));
                dobj.setDistrict_name(rs.getString("district_name"));
                dobj.setRcpt_heading(rs.getString("rcpt_heading"));
                dobj.setRcpt_subheading(rs.getString("rcpt_subheading"));
                dobj.setOfficeName(rs.getString("off_name"));
                dobj.setCourt_order_no(rs.getString("order_no"));
                dobjList.add(dobj);
            }
        } catch (SQLException ex) {
            throw new VahanException("Error in execution query in getUntracedRCDetail()");
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
        return dobjList;
    }

    public List<BlackListedVehicleDobj> getCompoundingAmountDetails(String regnNo) throws VahanException {
        List<BlackListedVehicleDobj> dobjList = new ArrayList<BlackListedVehicleDobj>();
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String sql = null;
        try {
            tmgr = new TransactionManager("getCompoundingAmountDetails");
            sql = "select a.fir_no,a.complain_dt,a.compounding_amt,b.descr as  complainDesc from " + TableList.VT_BLACKLIST
                    + " a inner join " + TableList.VM_BLACKLIST + " b on b.code = a.complain_type "
                    + " WHERE regn_no = ? and state_cd = ? and off_cd = ? and complain_type = ? order by a.complain_dt desc ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            ps.setInt(4, TableConstants.BLCompoundingAmtCode);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                BlackListedVehicleDobj dobj = new BlackListedVehicleDobj();
                dobj.setComplainDesc(rs.getString("complainDesc"));
                dobj.setFirNo(rs.getString("fir_no"));
                dobj.setComplain_dt(rs.getDate("complain_dt"));
                dobj.setCompounding_amt(rs.getLong("compounding_amt"));
                dobjList.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }

        return dobjList;
    }

    public void updateCompoundingAmount(String regn_no, Long amount, Date complain_dt) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String sql;
        try {
            tmgr = new TransactionManager("updateCompoundingAmount");
            sql = "insert into " + TableList.VH_BLACKLIST + " select state_cd,off_cd,"
                    + " regn_no,complain_type, fir_no, fir_dt, complain,complain_dt,entered_by, "
                    + " 'Amount Updated By " + Util.getUserName() + "' ,current_timestamp,?,compounding_amt "
                    + " FROM " + TableList.VT_BLACKLIST
                    + " where regn_no=? and state_cd = ? and off_cd = ? and complain_dt = ?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, regn_no);
            ps.setString(3, Util.getUserStateCode());
            ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            ps.setTimestamp(5, new Timestamp(complain_dt.getTime()));
            ps.executeUpdate();

            sql = "update " + TableList.VT_BLACKLIST
                    + " set compounding_amt = ? "
                    + " where regn_no=? and state_cd = ? and off_cd = ? and complain_dt = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, amount);
            ps.setString(2, regn_no);
            ps.setString(3, Util.getUserStateCode());
            ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            ps.setTimestamp(5, new Timestamp(complain_dt.getTime()));
            ps.executeUpdate();

            tmgr.commit();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
    }
}
