/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Receipt_Master_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author nic5912
 */
public class Receipt_Master_Impl {

    private static final Logger LOGGER = Logger.getLogger(Receipt_Master_Impl.class);

    public static Receipt_Master_dobj getReceipt_Master_dobj() {

        Receipt_Master_dobj dobj = null;
        TransactionManager tmgr = null;
        long userCode = 0;
        boolean isRcptNoFound = false;
        try {
            String stateCd = Util.getUserStateCode();
            TmConfigurationDobj tmConf = Util.getTmConfiguration();
            dobj = new Receipt_Master_dobj();
            dobj.setBook_rcpt_no("Not Assigned");
            dobj.setBook_no("");
            dobj.setCurrent_rcpt_no(0);
            dobj.setRcpt_start(0);
            dobj.setRcpt_end(0);
            //
            if (true) {
                return null;
            }

            tmgr = new TransactionManager("getReceipt_Master_dobj");
            int offCd = Util.getSelectedSeat().getOff_cd();
            if (tmConf.isAuto_cash_rcpt_gen()) {
                String rcpt = ServerUtil.getUniqueOfficeRcptNo(tmgr, stateCd, offCd, TableConstants.CASH_RCPT_FLAG, true);
                dobj.setBook_rcpt_no(rcpt);
                return dobj;
            }

            userCode = Long.parseLong(Util.getEmpCode());
            int off_cd = Integer.parseInt(Util.getSession().getAttribute("selected_off_cd").toString());
            String sql = "Select * from tm_rcpt_no where user_cd=? and off_cd=? and state_cd=? and expired='N' ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setLong(1, userCode);
            ps.setInt(2, off_cd);
            ps.setString(3, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (!rs.next()) {
                userCode = 0; //Same Receipt No for All the Cashiers in one Office
                ps = tmgr.prepareStatement(sql);
                ps.setLong(1, userCode);
                ps.setInt(2, off_cd);
                ps.setString(3, stateCd);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    isRcptNoFound = true;
                }
            } else {
                isRcptNoFound = true;
            }
            if (isRcptNoFound) {
                dobj.setBook_rcpt_no(rs.getString("rcpt_prefix") + " " + rs.getInt("rcpt_current_no"));
                dobj.setBook_no(rs.getString("rcpt_prefix"));
                dobj.setCurrent_rcpt_no(rs.getInt("rcpt_current_no"));
                dobj.setRcpt_start(rs.getInt("rcpt_start"));
                dobj.setRcpt_end(rs.getInt("rcpt_end"));
            }
        } catch (VahanException ve) {
            //throw ve;
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
        return dobj;

    }

    public static String generateNewRcptNo(int off_cd, TransactionManager tmgr) throws VahanException {
        String rcptno = null;

        String sql = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String userStateCode = Util.getUserStateCode();
        long userCode = Long.parseLong(Util.getEmpCode());
        boolean isRcptNoFound = false;
        try {
            TmConfigurationDobj tmConf = Util.getTmConfiguration();
            if (tmConf.isAuto_cash_rcpt_gen()) {
                String rcpt = ServerUtil.getUniqueOfficeRcptNo(tmgr, userStateCode, off_cd, TableConstants.CASH_RCPT_FLAG, false);
                return rcpt;
            }

            sql = "select * from tm_rcpt_no where state_cd = ? and off_cd = ? and user_cd = ? and expired='N'  ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, userStateCode);
            ps.setInt(2, off_cd);
            ps.setLong(3, userCode);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (!rs.next()) {
                userCode = 0; //Same Receipt No for All the Cashiers in one Office
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, userStateCode);
                ps.setInt(2, off_cd);
                ps.setLong(3, userCode);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    isRcptNoFound = true;
                }
            } else {
                isRcptNoFound = true;
            }
            if (isRcptNoFound) {
                sql = "Update tm_rcpt_no set rcpt_current_no=rcpt_current_no where state_cd = ? and off_cd = ? and user_cd = ? and expired='N' ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, userStateCode);
                ps.setInt(2, off_cd);
                ps.setLong(3, userCode);
                ps.executeUpdate();

                sql = "select rcpt_current_no,rcpt_prefix from tm_rcpt_no where state_cd = ? and off_cd = ? and user_cd = ? "
                        + " and expired = 'N' and rcpt_current_no between rcpt_start and rcpt_end ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, userStateCode);
                ps.setInt(2, off_cd);
                ps.setLong(3, userCode);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    rcptno = rs.getString("rcpt_prefix") + rs.getInt("rcpt_current_no");

                    sql = "Update tm_rcpt_no set rcpt_current_no=rcpt_current_no+1 where state_cd = ? and off_cd = ? and user_cd=? and expired='N' ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, userStateCode);
                    ps.setInt(2, off_cd);
                    ps.setLong(3, userCode);
                    ps.executeUpdate();

                    sql = "Update tm_rcpt_no set expired = 'Y' where rcpt_current_no > rcpt_end and state_cd = ? and off_cd = ? and user_cd=? and expired='N' ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, userStateCode);
                    ps.setInt(2, off_cd);
                    ps.setLong(3, userCode);
                    ps.executeUpdate();
                } else {
                    isRcptNoFound = false;
                }
            }
            if (!isRcptNoFound) {
                throw new VahanException("Either Cash Receipt Series not given or issued Receipt is exuasted.");
            } else {
                if (isDuplicateRcptNo(Util.getUserStateCode(), Util.getUserSeatOffCode(), rcptno, tmgr)) {
                    throw new VahanException("Cash Receipt No already available, Please reset the Cash Receipt No Master properly...");
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Either Cash Receipt Series not given or issued Receipt is exuasted.");
        }
        return rcptno;
    }

    public static boolean isDuplicateRcptNo(String userStateCode, int userOffCd, String rcptNo, TransactionManager tmgr) throws VahanException {
        boolean isRcptNoFound = false;
        PreparedStatement ps = null;
        RowSet rs = null;
        String sql = null;
        try {
            sql = "select * from " + TableList.VT_FEE + " where state_cd = ? and off_cd = ? and rcpt_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, userStateCode);
            ps.setInt(2, userOffCd);
            ps.setString(3, rcptNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                isRcptNoFound = true;
            } else {
                sql = "select * from " + TableList.VT_TAX + " where state_cd = ? and off_cd = ? and rcpt_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, userStateCode);
                ps.setInt(2, userOffCd);
                ps.setString(3, rcptNo);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    isRcptNoFound = true;
                } else {
                    isRcptNoFound = false;
                }
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Exception in isDuplicateRcptNo method to find duplicate Receipt no.");
        }
        return isRcptNoFound;
    }

    synchronized public static void saveNewRcptNo(int newRcptNo) throws VahanException {
        String sql = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        int off_cd = Integer.parseInt(Util.getSession().getAttribute("selected_off_cd").toString());
        try {
            tmgr = new TransactionManager("saveNewRcptNo");

            sql = "Update tm_rcpt_no set rcpt_current_no=rcpt_current_no where user_cd=? and off_cd=? and state_cd=? and expired='N'  ";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(Util.getEmpCode()));
            ps.setInt(2, off_cd);
            ps.setString(3, Util.getUserStateCode());
            ps.executeUpdate();

            sql = "select rcpt_current_no from tm_rcpt_no where user_cd=? and off_cd=?  and state_cd=? and expired='N' and rcpt_current_no between rcpt_start and rcpt_end ";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(Util.getEmpCode()));
            ps.setInt(2, off_cd);
            ps.setString(3, Util.getUserStateCode());

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                // rcptno = rs.getInt("rcpt_current_no");

                sql = "Update tm_rcpt_no set rcpt_current_no=? where user_cd=? and off_cd=? and state_cd=? and expired='N' ";
                ps = tmgr.prepareStatement(sql);
                ps.setInt(1, newRcptNo);
                ps.setLong(2, Long.parseLong(Util.getEmpCode()));
                ps.setInt(3, off_cd);
                ps.setString(4, Util.getUserStateCode());
                ps.executeUpdate();

                sql = "Update tm_rcpt_no set expired = 'Y' where rcpt_current_no > rcpt_end and user_cd=? and off_cd=? and state_cd=? and expired='N' ";
                ps = tmgr.prepareStatement(sql);
                ps.setLong(1, Long.parseLong(Util.getEmpCode()));
                ps.setInt(2, off_cd);
                ps.setString(3, Util.getUserStateCode());
                ps.executeUpdate();
            }

            tmgr.commit();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
