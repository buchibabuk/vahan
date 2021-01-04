/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.user_mgmt.dobj.ReceiptMasterDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;

/**
 *
 * @author tranC103
 */
public class ReceiptMasterImpl {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ReceiptMasterImpl.class);

    public boolean saveNewBook(ReceiptMasterDobj receiptDobj) throws VahanException {
        boolean insertFlag = false;
        PreparedStatement ps;
        TransactionManager tmgr = null;
        String sql;
        int i = 1;
        String expiredFlag = "N";
        try {
            tmgr = new TransactionManager("saveNewBook");

            sql = "INSERT INTO " + TableList.TM_RCPT_NO + "(\n"
                    + "            state_cd, off_cd, rcpt_prefix, rcpt_start, rcpt_end, rcpt_current_no, \n"
                    + "            user_cd, expired, issue_dt, issue_emp_cd)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, current_timestamp, ?);";
            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, receiptDobj.getStateCode());
            ps.setInt(i++, receiptDobj.getOffCode());
            ps.setString(i++, receiptDobj.getRcptPrefix());
            ps.setLong(i++, receiptDobj.getRcptStart());
            ps.setLong(i++, receiptDobj.getRcptEnd());
            ps.setLong(i++, receiptDobj.getRcptCurrent());
            ps.setLong(i++, receiptDobj.getRcptUserCode());
            ps.setString(i++, expiredFlag);
            ps.setString(i++, Util.getEmpCode());

            int temp = ps.executeUpdate();
            if (temp > 0) {
                insertFlag = true;
                tmgr.commit();
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in saveNewBook");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in saveNewBook");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException("Error in saveNewBook");
                }
            }
        }
        return insertFlag;
    }

    public boolean verifyNewBook(ReceiptMasterDobj receiptDobj) throws VahanException {
        boolean verifyFlag = true;
        PreparedStatement ps;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql;
        try {
            tmgr = new TransactionManager("verifyNewBook");
            sql = "Select user_cd,expired,rcpt_end from " + TableList.TM_RCPT_NO + " where user_cd=? and state_cd = ? and off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, receiptDobj.getRcptUserCode());
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (rs.getString("expired").equalsIgnoreCase("N")) {
                    verifyFlag = false;
                }
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in verifyNewBook" + e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException("Error in verifyNewBook" + e.getMessage());
                }
            }
        }
        return verifyFlag;
    }

    public void getExpiredRows(ReceiptMasterDobj receiptDobj, List<ReceiptMasterDobj> expiredList) throws VahanException {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs = null;
        String sql;
        String stateCode = receiptDobj.getStateCode();
        int offCode = receiptDobj.getOffCode();
        String expired = "Y";
        ReceiptMasterDobj dobj;
        try {
            tmgr = new TransactionManager("getExpiredRowset");
            sql = "select * from ( Select a.*,b.user_name from " + TableList.THM_RCPT_NO + " a inner join " + TableList.TM_USER_INFO + " b on a.user_cd = b.user_cd  where a.state_cd = ? and a.off_cd = ? and a.expired = ?\n"
                    + " union all\n"
                    + "Select *,'FOR ALL' as user_name from " + TableList.THM_RCPT_NO + " where state_cd = ? and off_cd = ? and user_cd = 0 and expired = ?) a";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setInt(2, offCode);
            ps.setString(3, expired);
            ps.setString(4, stateCode);
            ps.setInt(5, offCode);
            ps.setString(6, expired);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new ReceiptMasterDobj();
                dobj.setRcptPrefix(rs.getString("rcpt_prefix"));
                dobj.setRcptStart(rs.getLong("rcpt_start"));
                dobj.setRcptEnd(rs.getLong("rcpt_end"));
                dobj.setRcptCurrent(rs.getLong("rcpt_current_no"));
                dobj.setRcptUserCode(rs.getLong("user_cd"));
                dobj.setExpiredFlag(rs.getString("expired"));
                dobj.setUserName(rs.getString("user_name"));
                expiredList.add(dobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in getExpiredRowset" + e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException("Error in getExpiredRowset" + e.getMessage());
                }
            }

        }
    }

    public void getIssuedRows(ReceiptMasterDobj receiptDobj, List<ReceiptMasterDobj> issuedList) throws VahanException {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs = null;
        String sql;
        String stateCode = receiptDobj.getStateCode();
        int offCode = receiptDobj.getOffCode();
        String expired = "N";
        ReceiptMasterDobj dobj;
        try {
            tmgr = new TransactionManager("getIssuedRows");
            sql = "select * from ( Select a.*,b.user_name from " + TableList.TM_RCPT_NO + " a inner join " + TableList.TM_USER_INFO + " b on a.user_cd = b.user_cd  where a.state_cd = ? and a.off_cd = ? and a.expired = ?\n"
                    + " union all\n"
                    + "Select *,'FOR ALL' as user_name from " + TableList.TM_RCPT_NO + " where state_cd = ? and off_cd = ? and user_cd = 0 and expired = ?) a";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setInt(2, offCode);
            ps.setString(3, expired);
            ps.setString(4, stateCode);
            ps.setInt(5, offCode);
            ps.setString(6, expired);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new ReceiptMasterDobj();
                dobj.setRcptPrefix(rs.getString("rcpt_prefix"));
                dobj.setRcptStart(rs.getLong("rcpt_start"));
                dobj.setRcptEnd(rs.getLong("rcpt_end"));
                dobj.setRcptCurrent(rs.getLong("rcpt_current_no"));
                dobj.setRcptUserCode(rs.getLong("user_cd"));
                dobj.setExpiredFlag(rs.getString("expired"));
                dobj.setUserName(rs.getString("user_name"));
                issuedList.add(dobj);
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in getIssuedRows" + e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException("Error in getIssuedRows" + e.getMessage());
                }
            }

        }
    }

    public void updateEditableRow(ReceiptMasterDobj dobj) throws VahanException {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        String sql;
        try {
            tmgr = new TransactionManager("updateEditableRow");

            sql = "INSERT INTO " + TableList.THM_RCPT_NO + "(\n"
                    + "            state_cd, off_cd, rcpt_prefix, rcpt_start, rcpt_end, rcpt_current_no, \n"
                    + "            user_cd, expired, issue_dt, issue_emp_cd, moved_on, moved_by)\n"
                    + "    SELECT state_cd, off_cd, rcpt_prefix, rcpt_start, rcpt_end, rcpt_current_no, \n"
                    + "       user_cd, ? as expired, issue_dt, issue_emp_cd, current_timestamp as moved_on, ? as moved_by\n"
                    + "  FROM " + TableList.TM_RCPT_NO + " where user_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getExpiredFlag());
            ps.setString(2, Util.getEmpCode());
            ps.setLong(3, dobj.getRcptUserCode());
            ps.executeUpdate();

            sql = "Delete from " + TableList.TM_RCPT_NO + " where user_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, dobj.getRcptUserCode());
            ps.executeUpdate();

            tmgr.commit();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in updateEditableRow" + e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException("Error in updateEditableRow" + e.getMessage());
                }
            }
        }

    }

    public boolean uniqueBookNumber(ReceiptMasterDobj receiptDobj) throws VahanException {
        PreparedStatement ps;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql;
        boolean flag = true;
        try {
            tmgr = new TransactionManager("uniqueBookNumber");
            sql = "Select rcpt_prefix,rcpt_end from " + TableList.TM_RCPT_NO + " where state_cd = ? and off_cd = ? and rcpt_prefix = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, receiptDobj.getStateCode());
            ps.setInt(2, receiptDobj.getOffCode());
            ps.setString(3, receiptDobj.getRcptPrefix());

            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (rs.getInt("rcpt_end") == TableConstants.RCPT_UPPER_BOUND) {
                    flag = false;
                }
            } else {
                sql = "Select rcpt_prefix,rcpt_end from " + TableList.THM_RCPT_NO + " where state_cd = ? and off_cd = ? and rcpt_prefix = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, receiptDobj.getStateCode());
                ps.setInt(2, receiptDobj.getOffCode());
                ps.setString(3, receiptDobj.getRcptPrefix());
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    if (rs.getInt("rcpt_end") == TableConstants.RCPT_UPPER_BOUND) {
                        flag = false;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in uniqueBookNumber" + e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException("Error in uniqueBookNumber" + e.getMessage());
                }
            }
        }
        return flag;
    }

    public List getUserData(String stateCode, int offCode, List user) throws VahanException {
        PreparedStatement ps;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("getUserData");
            sql = "Select user_cd,user_name,user_id from " + TableList.TM_USER_INFO + " where state_cd = ? and off_cd =?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setInt(2, offCode);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                user.add(new SelectItem(rs.getInt("user_cd"), rs.getString("user_name") + "(" + rs.getString("user_id") + ")"));
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in getUserData" + ex.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                    throw new VahanException("Error in getUserData" + ex.getMessage());
                }
            }
        }
        return user;
    }

    public boolean verifyLowerBound(ReceiptMasterDobj receiptDobj) throws VahanException {
        boolean lbFlag = true;
        PreparedStatement ps;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql;
        try {
            tmgr = new TransactionManager("verifyLowerBound");
            sql = "Select max(rcpt_end) as rcpt_end from " + TableList.TM_RCPT_NO + " where state_cd = ? and off_cd = ? and rcpt_prefix = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, receiptDobj.getStateCode());
            ps.setInt(2, receiptDobj.getOffCode());
            ps.setString(3, receiptDobj.getRcptPrefix());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (!CommonUtils.isNullOrBlank(rs.getString("rcpt_end"))) {
                    if (receiptDobj.getRcptStart() <= rs.getInt("rcpt_end")) {
                        lbFlag = false;
                    } else {
                        lbFlag = true;
                    }
                } else {
                    sql = "Select max(rcpt_end) as rcpt_end from " + TableList.THM_RCPT_NO + " where state_cd = ? and off_cd = ? and rcpt_prefix = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, receiptDobj.getStateCode());
                    ps.setInt(2, receiptDobj.getOffCode());
                    ps.setString(3, receiptDobj.getRcptPrefix());
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        if (!CommonUtils.isNullOrBlank(rs.getString("rcpt_end"))) {
                            if (receiptDobj.getRcptStart() <= rs.getInt("rcpt_end")) {
                                lbFlag = false;
                            } else {
                                lbFlag = true;
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Invalid Lower Bound");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Invalid Lower Bound");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                    throw new VahanException("Error in verifyLowerBound" + ex.getMessage());
                }
            }
        }
        return lbFlag;
    }

    public boolean verifySeries(ReceiptMasterDobj receiptDobj) throws VahanException {
        PreparedStatement ps;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql = "";
        String seriesStart = receiptDobj.getRcptPrefix() + receiptDobj.getRcptStart();
        String seriesEnd = receiptDobj.getRcptPrefix() + receiptDobj.getRcptEnd();
        boolean verifySeriesFlag = false;
        try {
            tmgr = new TransactionManager("getUserData");
            sql = "select count(*) as total from " + TableList.VT_FEE + " where state_cd = ? and off_cd = ? and rcpt_no between ? and ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, receiptDobj.getStateCode());
            ps.setInt(2, receiptDobj.getOffCode());
            ps.setString(3, seriesStart);
            ps.setString(4, seriesEnd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (rs.getInt("total") <= 0) {
                    verifySeriesFlag = true;
                    sql = "select count(*) as total from " + TableList.VT_TAX + " where state_cd = ? and off_cd = ? and rcpt_no between ? and ? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, receiptDobj.getStateCode());
                    ps.setInt(2, receiptDobj.getOffCode());
                    ps.setString(3, seriesStart);
                    ps.setString(4, seriesEnd);
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        if (rs.getInt("total") <= 0) {
                            verifySeriesFlag = true;
                        } else {
                            verifySeriesFlag = false;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in verifySeries:" + ex.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                    throw new VahanException("Error in verifySeries:" + ex.getMessage());
                }
            }
        }
        return verifySeriesFlag;
    }
}
