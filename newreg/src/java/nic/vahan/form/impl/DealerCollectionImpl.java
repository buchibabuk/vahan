/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.db.user_mgmt.dobj.DealerMasterDobj;
import nic.vahan.db.user_mgmt.impl.DealerMasterImpl;
import nic.vahan.form.dobj.DealerCollectionDobj;
import org.apache.log4j.Logger;

public class DealerCollectionImpl {

    private static final Logger LOGGER = Logger.getLogger(DealerCollectionImpl.class);

    public List<DealerCollectionDobj> getRtoWiseTotalCollection(Date fromDate, Date uptodate) {

        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        List<DealerCollectionDobj> dobjList = new ArrayList<DealerCollectionDobj>();

        try {
            tmgr = new TransactionManagerReadOnly("getRtoWiseTotalCollection");
            sql = "select * from getdealercollection(?, ?, ?, ?, ?)";

            String user_catg = Util.getUserCategory();
            ps = tmgr.prepareStatement(sql);
            if (user_catg != null && user_catg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER)) {
                ps.setString(1, Util.getUserStateCode());
                ps.setLong(2, Util.getSelectedSeat().getOff_cd());
                ps.setLong(3, Long.parseLong(Util.getEmpCode()));
            } else if (user_catg != null && user_catg.equalsIgnoreCase(TableConstants.USER_CATG_OFF_STAFF)) {
                ps.setString(1, Util.getUserStateCode());
                ps.setLong(2, Util.getSelectedSeat().getOff_cd());
                ps.setLong(3, 0);
            } else if (user_catg != null && user_catg.equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)) {
                ps.setString(1, Util.getUserStateCode());
                ps.setLong(2, 0);
                ps.setLong(3, 0);
            }
            java.sql.Date fromSqlDate = new java.sql.Date(fromDate.getTime());
            java.sql.Date uptoSqlDate = new java.sql.Date(uptodate.getTime());
            ps.setDate(4, fromSqlDate);
            ps.setDate(5, uptoSqlDate);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                DealerCollectionDobj dobj = new DealerCollectionDobj();
                if (rs.getString("dealer_name").equals("<-SUB TOTAL->")) {
                    dobj.setDealerName(rs.getString("dealer_name"));
                    dobj.setSubTotalRegn(rs.getInt("total_regn"));
                    dobj.setSubFees(rs.getInt("fees"));
                    dobj.setSubTax(rs.getInt("mv_tax"));
                    dobj.setSubTotalAmount(rs.getInt("total"));
                    dobjList.add(dobj);
                } else if (rs.getString("dealer_name").equals("<-GRAND TOTAL->")) {
                    dobj.setDealerName(rs.getString("dealer_name"));
                    dobj.setGrandTotalRegn(rs.getInt("total_regn"));
                    dobj.setGrandFees(rs.getInt("fees"));
                    dobj.setGrandTax(rs.getInt("mv_tax"));
                    dobj.setGrandTotalAmount(rs.getInt("total"));
                    dobjList.add(dobj);
                } else if (!rs.getString("dealer_name").equals("<-GRAND TOTAL->") || !rs.getString("dealer_name").equals("<-SUB TOTAL->")) {
                    dobj.setDealerName(rs.getString("dealer_name"));
                    dobj.setOffName(rs.getString("off_name"));
                    dobj.setFees(rs.getInt("fees"));
                    dobj.setTax(rs.getInt("mv_tax"));
                    dobj.setTotalAmount(rs.getInt("total"));
                    dobj.setTotalRegn(rs.getInt("total_regn"));
                    dobjList.add(dobj);
                }
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
        return dobjList;
    }

    public List<DealerCollectionDobj> getTransactionWiseData(Date fromDate, Date uptodate) {

        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        List<DealerCollectionDobj> dobjList = new ArrayList<DealerCollectionDobj>();

        try {
            tmgr = new TransactionManagerReadOnly("inside getTransactionWiseData of DealerCollectionImpl");
            sql = "select * from getdealerregistration(?,?,?,?,?)";

            String user_catg = Util.getUserCategory();
            ps = tmgr.prepareStatement(sql);
            if (user_catg != null && user_catg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER)) {
                ps.setString(1, Util.getUserStateCode());
                ps.setLong(2, Util.getSelectedSeat().getOff_cd());
                ps.setLong(3, Long.parseLong(Util.getEmpCode()));
            } else if (user_catg != null && user_catg.equalsIgnoreCase(TableConstants.USER_CATG_OFF_STAFF)) {
                ps.setString(1, Util.getUserStateCode());
                ps.setLong(2, Util.getSelectedSeat().getOff_cd());
                ps.setLong(3, 0);
            } else if (user_catg != null && user_catg.equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)) {
                ps.setString(1, Util.getUserStateCode());
                ps.setLong(2, 0);
                ps.setLong(3, 0);
            }
            java.sql.Date fromSqlDate = new java.sql.Date(fromDate.getTime());
            java.sql.Date uptoSqlDate = new java.sql.Date(uptodate.getTime());
            ps.setDate(4, fromSqlDate);
            ps.setDate(5, uptoSqlDate);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                DealerCollectionDobj dobj = new DealerCollectionDobj();
                dobj.setOffName(rs.getString("off_name"));
                dobj.setDealerName(rs.getString("dealer_name"));
                dobj.setTinNo(rs.getString("tin_no"));
                dobj.setTransactionNo(rs.getString("transaction_no"));
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setRcptNo(rs.getString("rcpt_no"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setChasiNo(rs.getString("chasi_no"));
                dobj.setSaleAmt(rs.getInt("sale_amt"));
                dobj.setFees(rs.getInt("fees"));
                dobj.setMvTax(rs.getInt("mv_tax"));
                dobj.setTotalAmount(rs.getInt("total"));
                dobj.setRcptDt(rs.getString("rcpt_dt"));
                dobjList.add(dobj);
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
        return dobjList;
    }

    public String updateTinNo(String dealerCd, String tinNo) throws VahanException {
        DealerMasterDobj dobj = new DealerMasterDobj();
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String updatedTinNo = "";
        try {
            tmgr = new TransactionManager("inside updateTinNo of DealerCollectionImpl");
            dobj.setDealerCode(dealerCd);
            DealerMasterImpl.insertHisotry(dobj, tmgr);
            String sql = "UPDATE " + TableList.VM_DEALER_MAST + "\n"
                    + "   SET  tin_no=? \n"
                    + " WHERE dealer_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, tinNo);
            ps.setString(2, dealerCd);

            int temp = ps.executeUpdate();
            if (temp > 0) {
                tmgr.commit();
                updatedTinNo = tinNo;
            } else {
                throw new VahanException("Problem in Tin No Updation");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in Tin No Updation");

        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return updatedTinNo;
    }

    public String getTinNoStatus(String dealerCode) {

        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String tinNoStatus = "";

        try {
            tmgr = new TransactionManagerReadOnly("inside getTinNoStatus of RtoWiseTotalCollectionImpl");
            sql = "select tin_no from " + TableList.VM_DEALER_MAST + " where dealer_cd = ? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dealerCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                tinNoStatus = rs.getString("tin_no");
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
        return tinNoStatus;
    }

    public void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public void setTimeToEndofDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }

    public Calendar getCalendarForNow(Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
