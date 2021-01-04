/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.ManualReceiptEntryDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author Kartikey Singh
 */
public class ManualReceiptEntryImplementation {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ManualReceiptEntryImplementation.class);

    public String insertDataintoVa(ManualReceiptEntryDobj dobj, String changedDataContents, Status_dobj statusDobj) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String msg = null;
        String applno = "";

        try {
            tmgr = new TransactionManager("insertDataintoVa");
            String sql = "select * from " + TableList.VA_MANUAL_RECEIPT + " where appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, statusDobj.getAppl_no());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (!changedDataContents.isEmpty()) {
                    insertIntoVhaManualReceiptEntry(tmgr, dobj.getApplno());
                    updateVaManualReceiptEntry(tmgr, dobj, dobj.getApplno());
                    ComparisonBeanImpl.updateChangedData(dobj.getApplno(), changedDataContents, tmgr);
                }
            } else {
                applno = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
                insertIntoVaManualReceiptEntry(tmgr, dobj, applno);
                Status_dobj status = new Status_dobj();
                int initialFlow[] = ServerUtil.getInitialAction(tmgr, Util.getUserStateCode(), TableConstants.VM_MAST_MANUAL_RECEIPT, null);
                status.setState_cd(Util.getUserStateCode());
                status.setOff_cd(Util.getUserOffCode());
                status.setAppl_no(applno);
                status.setPur_cd(TableConstants.VM_MAST_MANUAL_RECEIPT);
                status.setFlow_slno(initialFlow[1]);
                status.setFile_movement_slno(initialFlow[1]);
                status.setAction_cd(initialFlow[0]);
                status.setEmp_cd(Long.parseLong(Util.getEmpCode()));
                status.setRegn_no(getRegNo(dobj.getTrans_appl_no(), tmgr));
                ServerUtil.fileFlowForNewApplication(tmgr, status);
            }

            tmgr.commit();


        } catch (VahanException e) {
            applno = null;
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            msg = "Data Not Saved.";
            applno = null;
            throw new VahanException(msg);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                msg = "Data Not Saved.";
            }
        }
        return applno;
    }

    public static String getRegNo(String appl_no, TransactionManager tmgr) {
        PreparedStatement ps = null;
        String sql = null;
        String regnNo = null;
        try {
            sql = "SELECT distinct regn_no from " + TableList.VA_DETAILS
                    + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) // found
            {
                regnNo = rs.getString("regn_no");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return regnNo;
    }

    public String updateInsertManualRecieptEntry(ManualReceiptEntryDobj manualrcptdobj, String changedDataContents, Status_dobj statusDobj) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String applno = null;
        String msg = null;
        try {
            tmgr = new TransactionManager("updateStatusManualReciept");
            String sql = "select * from " + TableList.VA_MANUAL_RECEIPT + " where transaction_appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, manualrcptdobj.getTrans_appl_no());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (!changedDataContents.isEmpty()) {
                    insertIntoVhaManualReceiptEntry(tmgr, manualrcptdobj.getApplno());
                    updateVaManualReceiptEntry(tmgr, manualrcptdobj, statusDobj.getAppl_no());
                    ComparisonBeanImpl.updateChangedData(manualrcptdobj.getApplno(), changedDataContents, tmgr);
                }
            } else {
                applno = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
                insertIntoVaManualReceiptEntry(tmgr, manualrcptdobj, applno);
                int initialFlow[] = ServerUtil.getInitialAction(tmgr, Util.getUserStateCode(), TableConstants.VM_MAST_MANUAL_RECEIPT, null);
                statusDobj.setState_cd(Util.getUserStateCode());
                statusDobj.setOff_cd(Util.getUserOffCode());
                statusDobj.setAppl_no(applno);
                statusDobj.setPur_cd(TableConstants.VM_MAST_MANUAL_RECEIPT);
                statusDobj.setFlow_slno(initialFlow[1]);
                statusDobj.setFile_movement_slno(initialFlow[1]);
                statusDobj.setAction_cd(initialFlow[0]);
                statusDobj.setEmp_cd(Long.parseLong(Util.getEmpCode()));
                statusDobj.setRegn_no(getRegNo(manualrcptdobj.getTrans_appl_no(), tmgr));
                ServerUtil.fileFlowForNewApplication(tmgr, statusDobj);
            }
            if (TableConstants.VM_ROLE_MANUAL_RECEIPT_APPROVAL == statusDobj.getAction_cd()) {
                insertIntoVhManualReceiptEntry(tmgr, manualrcptdobj);
                deleteVTManualReceiptEntry(tmgr, manualrcptdobj);
                insertIntoVtManualReceiptEntry(tmgr, manualrcptdobj.getApplno());
                deleteVaManualReceiptEntry(tmgr, manualrcptdobj);
                statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, statusDobj);
            }
            ServerUtil.webServiceForNextStage(statusDobj, tmgr);
            ServerUtil.fileFlow(tmgr, statusDobj);
            tmgr.commit();

        } catch (VahanException e) {
            applno = null;
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            msg = "Data Not Saved.";
            applno = null;
            throw new VahanException(msg);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                msg = "Data Not Saved.";
            }
        }
        return applno;
    }

    public ManualReceiptEntryDobj getApplicationDetails(String appl_no) throws VahanException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String sql = "SELECT state_cd,off_cd,appl_no,transaction_appl_no,rcpt_no,rcpt_dt,amount from " + TableList.VA_MANUAL_RECEIPT + " where appl_no=? ";
        ManualReceiptEntryDobj manualrcptdobj = null;
        try {
            tmgr = new TransactionManager("getApplicationDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                manualrcptdobj = new ManualReceiptEntryDobj();
                manualrcptdobj.setState_cd(rs.getString("state_cd"));
                manualrcptdobj.setOff_cd(rs.getInt("off_cd"));
                manualrcptdobj.setApplno(rs.getString("appl_no"));
                manualrcptdobj.setTrans_appl_no(rs.getString("transaction_appl_no"));
                manualrcptdobj.setRcptNo(rs.getString("rcpt_no"));
                manualrcptdobj.setReceipt_dt(rs.getDate("rcpt_dt"));
                manualrcptdobj.setAmount(rs.getLong("amount"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return manualrcptdobj;
    }

    public static void insertIntoVhaManualReceiptEntry(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VHA_MANUAL_RECEIPT
                + " SELECT  current_timestamp as moved_on, ? as moved_by, state_cd, off_cd,appl_no,transaction_appl_no,rcpt_no,rcpt_dt,amount,rcpt_used, \n"
                + "        entered_by,entered_on \n"
                + "  FROM " + TableList.VA_MANUAL_RECEIPT + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public static void insertIntoVaManualReceiptEntry(TransactionManager tmgr, ManualReceiptEntryDobj dobj, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VA_MANUAL_RECEIPT + " ("
                + "            state_cd, off_cd, appl_no, transaction_appl_no, rcpt_no, rcpt_dt, amount, rcpt_used, "
                + "            entered_by, entered_on) "
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, "
                + "            ?, current_timestamp) ";
        ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getUserOffCode());
        ps.setString(i++, appl_no);
        ps.setString(i++, dobj.getTrans_appl_no());
        ps.setString(i++, dobj.getRcptNo());
        ps.setDate(i++, new java.sql.Date(dobj.getReceipt_dt().getTime()));
        ps.setLong(i++, dobj.getAmount());
        ps.setBoolean(i++, false);
        ps.setString(i++, Util.getEmpCode());
        ps.executeUpdate();
    }

    private static void updateVaManualReceiptEntry(TransactionManager tmgr, ManualReceiptEntryDobj dobj, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "UPDATE " + TableList.VA_MANUAL_RECEIPT + " \n"
                + "   SET state_cd = ?,off_cd = ?,appl_no=?, transaction_appl_no=?, rcpt_no=?, rcpt_dt=?, amount=?, rcpt_used=false, \n"
                + " entered_by=?,entered_on = current_timestamp\n"
                + " WHERE appl_no=?";
        ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
        ps.setString(i++, appl_no);
        ps.setString(i++, dobj.getTrans_appl_no());
        ps.setString(i++, dobj.getRcptNo());
        ps.setDate(i++, new java.sql.Date(dobj.getReceipt_dt().getTime()));
        ps.setLong(i++, dobj.getAmount());
        ps.setString(i++, Util.getEmpCode());
        ps.setString(i++, appl_no);
        ps.executeUpdate();
    }

    public static String getRcptNumberDetails(String rcpt_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        TransactionManager tmgr = null;
        String rcptNumber = null;
        try {
            sql = " select rcpt_no from vt_fee where rcpt_no=? and state_cd= ? and off_cd= ? and pur_cd=603 "
                    + " union "
                    + " select rcpt_no from vt_tax where rcpt_no= ? and state_cd= ? and off_cd= ? and pur_cd=603 "
                    + " union "
                    + " select rcpt_no from va_manual_receipt where rcpt_no= ? and state_cd= ? and off_cd= ? ";
//                    + " union "
//                    + " select manual_rcpt_no as rcpt_no from vt_manual_receipt where manual_rcpt_no= ? and state_cd= ? and off_cd= ? and pur_cd=603 ";
            tmgr = new TransactionManager("getRcptNumberDetails");
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, rcpt_no);
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, rcpt_no);
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, rcpt_no);
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) // found
            {
                rcptNumber = rs.getString("rcpt_no");
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
        return rcptNumber;
    }

    public static void insertIntoVtManualReceiptEntry(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "INSERT INTO " + TableList.VT_MANUAL_RECEIPT
                + " SELECT  state_cd, off_cd,appl_no,transaction_appl_no,rcpt_no,rcpt_dt,amount,rcpt_used, \n"
                + "        entered_by,entered_on \n"
                + "  FROM " + TableList.VA_MANUAL_RECEIPT + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, appl_no);
        ps.executeUpdate();
    }

    public static void deleteVaManualReceiptEntry(TransactionManager tmgr, ManualReceiptEntryDobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "Delete from " + TableList.VA_MANUAL_RECEIPT + " where transaction_appl_no=? and state_cd=? and off_cd=? ";
        ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, dobj.getTrans_appl_no());
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
        ps.executeUpdate();
    }

    public static List<ManualReceiptEntryDobj> getManualReceiptEntryDetails(String applNo) throws VahanException {
        String query;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        List<ManualReceiptEntryDobj> manualDtlslist = new ArrayList<>();
        try {
            tmgr = new TransactionManager("getManualReceiptEntryDetails");
            query = " SELECT a.state_cd,a.off_cd,a.transaction_appl_no,b.descr,sum(amount) as amount from " + TableList.VT_MANUAL_RECEIPT + "  \n"
                    + " a inner join TM_PURPOSE_MAST b on " + TableConstants.VM_MAST_MANUAL_RECEIPT + " = b.pur_cd \n"
                    + " where transaction_appl_no =? and state_cd= ? group by 1,2,3,4 ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, applNo);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                ManualReceiptEntryDobj obj = new ManualReceiptEntryDobj();

                obj.setState_cd(rs.getString("state_cd"));
                obj.setOff_cd(rs.getInt("off_cd"));
                obj.setTrans_appl_no(applNo);
                obj.setAmount(rs.getLong("amount"));
                obj.setPur_cd((TableConstants.VM_MAST_MANUAL_RECEIPT));
                manualDtlslist.add(obj);
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
        return manualDtlslist;
    }
    
    /**
     * @author Kartikey Singh
     */
    public static List<ManualReceiptEntryDobj> getManualReceiptEntryDetails(String applNo, String stateCode) throws VahanException {
        String query;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        List<ManualReceiptEntryDobj> manualDtlslist = new ArrayList<>();
        try {
            tmgr = new TransactionManager("getManualReceiptEntryDetails");
            query = " SELECT a.state_cd,a.off_cd,a.transaction_appl_no,b.descr,sum(amount) as amount from " + TableList.VT_MANUAL_RECEIPT + "  \n"
                    + " a inner join TM_PURPOSE_MAST b on " + TableConstants.VM_MAST_MANUAL_RECEIPT + " = b.pur_cd \n"
                    + " where transaction_appl_no =? and state_cd= ? group by 1,2,3,4 ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, applNo);
            ps.setString(2, stateCode);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                ManualReceiptEntryDobj obj = new ManualReceiptEntryDobj();

                obj.setState_cd(rs.getString("state_cd"));
                obj.setOff_cd(rs.getInt("off_cd"));
                obj.setTrans_appl_no(applNo);
                obj.setAmount(rs.getLong("amount"));
                obj.setPur_cd((TableConstants.VM_MAST_MANUAL_RECEIPT));
                manualDtlslist.add(obj);
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
        return manualDtlslist;
    }

    public static void updateVTManualReceiptEntryStatus(TransactionManager tmgr, String appl_no, boolean isRcptUsed) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        try {
            sql = "UPDATE  " + TableList.VT_MANUAL_RECEIPT + " SET rcpt_used=? WHERE state_cd= ? and off_cd= ? and transaction_appl_no=?";
            int l = 1;
            ps = tmgr.prepareStatement(sql);
            ps.setBoolean(l++, isRcptUsed);
            ps.setString(l++, Util.getUserStateCode());
            ps.setInt(l++, Util.getUserOffCode());
            ps.setString(l++, appl_no);
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");

        }
    }

    /*
     * @author Kartikey Singh
     */
    public static void updateVTManualReceiptEntryStatus(TransactionManager tmgr, String appl_no, boolean isRcptUsed, String stateCode,
            int selectedOffCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        try {
            sql = "UPDATE  " + TableList.VT_MANUAL_RECEIPT + " SET rcpt_used=? WHERE state_cd= ? and off_cd= ? and transaction_appl_no=?";
            int l = 1;
            ps = tmgr.prepareStatement(sql);
            ps.setBoolean(l++, isRcptUsed);
            ps.setString(l++, stateCode);
            ps.setInt(l++, selectedOffCode);
            ps.setString(l++, appl_no);
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");

        }
    }

    public static String getBalanceFeeTaxAmount(String rcpt_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        TransactionManager tmgr = null;
        String amount = null;
        String rcpt_dt = null;
        String rcptData = "";
        try {
            sql = " select rcpt_dt::date,sum(a.fees+a.fine) from (select * from get_rcpt_details(?,?,?)) a," + TableList.VP_APPL_RCPT_MAPPING + " b "
                    + " where"
                    + " a.rcpt_no=b.rcpt_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd and upper(substring(remarks,1,7))='BALANCE' and "
                    + " a.pur_cd NOT IN (100,99) group by 1 ";
            tmgr = new TransactionManager("getBalanceFeeTaxAmount");
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, rcpt_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) // found
            {
                amount = rs.getString("sum");
                rcpt_dt = rs.getString("rcpt_dt");
                rcptData = amount + "`" + rcpt_dt;
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
        return rcptData;
    }

    public static boolean isManualReceiptRecordInVa(String appl_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        TransactionManager tmgr = null;
        Boolean manualreceipt = false;
        try {
            sql = " select * from " + TableList.VA_MANUAL_RECEIPT + " where transaction_appl_no=? and state_cd=? and off_cd=? ";
            tmgr = new TransactionManager("isManualReceiptRecordInVa");
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, appl_no);
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) // found
            {
                manualreceipt = true;
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
        return manualreceipt;
    }

    public static void insertIntoVhManualReceiptEntry(TransactionManager tmgr, ManualReceiptEntryDobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VH_MANUAL_RECEIPT
                + " SELECT  current_timestamp as moved_on, ? as moved_by, state_cd, off_cd,appl_no,transaction_appl_no,rcpt_no,rcpt_dt,amount,rcpt_used, \n"
                + "        entered_by,entered_on \n"
                + "  FROM " + TableList.VT_MANUAL_RECEIPT + " where rcpt_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, dobj.getRcptNo());
        ps.executeUpdate();
    }

    public static void deleteVTManualReceiptEntry(TransactionManager tmgr, ManualReceiptEntryDobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "Delete from " + TableList.VT_MANUAL_RECEIPT + " where rcpt_no=? and state_cd=? and off_cd=? ";
        ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, dobj.getRcptNo());
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
        ps.executeUpdate();
    }

    public static ManualReceiptEntryDobj getApprovedManualReceiptDetails(String appl_no) throws VahanException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String sql = " select state_cd,off_cd,transaction_appl_no, CASE WHEN(LENGTH(String_agg(rcpt_no|| ':' ||to_char(rcpt_dt,'dd-MM-yyyy'), ',')) < 30) THEN String_agg(rcpt_no|| ':' ||to_char(rcpt_dt,'dd-MM-yyyy'), ',') \n"
                + " ELSE (SPLIT_PART(String_agg(rcpt_no|| ':' ||to_char(rcpt_dt,'dd-MM-yyyy'), ','), ',',1)|| ' etc.') END as rcpt_no, sum(amount) as amount from " + TableList.VT_MANUAL_RECEIPT + "  where transaction_appl_no=? group by 1,2,3 ";

        ManualReceiptEntryDobj manualrcptdobj = null;
        try {
            tmgr = new TransactionManager("getApprovedManualReceiptDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                manualrcptdobj = new ManualReceiptEntryDobj();
                manualrcptdobj.setState_cd(rs.getString("state_cd"));
                manualrcptdobj.setOff_cd(rs.getInt("off_cd"));
                manualrcptdobj.setTrans_appl_no(rs.getString("transaction_appl_no"));
                manualrcptdobj.setRcptNo(rs.getString("rcpt_no"));
                manualrcptdobj.setAmount(rs.getLong("amount"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return manualrcptdobj;
    }

    public static boolean isFeePaidForVehicle(String appl_no) {
        boolean feePaid = false;
        String link_appl_no = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManagerReadOnly("inside isFeePaidForVehicle");
            String sql = "select rcpt_no from " + TableList.VP_APPL_RCPT_MAPPING + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                feePaid = true;
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return feePaid;
    }

    public static ManualReceiptEntryDobj getManualFeeAmount(String rcpt_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        TransactionManager tmgr = null;
        sql = " select amount,rcpt_dt from " + TableList.VT_MANUAL_RECEIPT + " where rcpt_no=? and state_cd=?";
        ManualReceiptEntryDobj manualrcptdobj = null;
        try {
            tmgr = new TransactionManager("getManualFeeAmount");
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, rcpt_no);
            ps.setString(i++, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                manualrcptdobj = new ManualReceiptEntryDobj();
                manualrcptdobj.setRcptNo(rcpt_no);
                manualrcptdobj.setAmount(rs.getLong("amount"));
                manualrcptdobj.setReceipt_dt(rs.getDate("rcpt_dt"));
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
        return manualrcptdobj;
    }

    public boolean updateManulaRcptAmount(ManualReceiptEntryDobj manualDobj) throws VahanException {
        boolean flag = false;
        PreparedStatement ps;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql;
        try {
            tmgr = new TransactionManager("updateManulaRcptAmount");
            sql = "select * from " + TableList.VT_MANUAL_RECEIPT + " where rcpt_no=? and transaction_appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, manualDobj.getRcptNo());
            ps.setString(2, manualDobj.getTrans_appl_no());
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {

                sql = "INSERT INTO " + TableList.VH_MANUAL_RECEIPT
                        + " SELECT  current_timestamp as moved_on, ? as moved_by, state_cd, off_cd,appl_no,transaction_appl_no,rcpt_no,rcpt_dt,amount,rcpt_used, \n"
                        + "        entered_by,entered_on \n"
                        + "  FROM " + TableList.VT_MANUAL_RECEIPT + " where rcpt_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, manualDobj.getRcptNo());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                sql = "UPDATE " + TableList.VT_MANUAL_RECEIPT + " set amount=?, entered_by=?, entered_on= current_timestamp where rcpt_no=? and transaction_appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setLong(1, manualDobj.getAmount());
                ps.setString(2, Util.getEmpCode());
                ps.setString(3, manualDobj.getRcptNo());
                ps.setString(4, manualDobj.getTrans_appl_no());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                tmgr.commit();
                flag = true;
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException(TableConstants.SomthingWentWrong);
                }
            }
        }
        return flag;
    }

    public static List fillMaualRecordDataTable(ManualReceiptEntryDobj dobj) throws VahanException {
        List<ManualReceiptEntryDobj> manualRcptRecordList = new ArrayList<>();
        TransactionManager tmgr = null;
        ManualReceiptEntryDobj manualRcptDobj = null;
        try {
            tmgr = new TransactionManager("fillDataTable");
            PreparedStatement ps;
            RowSet rs;
            String sql = " select transaction_appl_no,appl_no,rcpt_no,rcpt_dt,amount from vt_manual_receipt where transaction_appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getTrans_appl_no());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                manualRcptDobj = new ManualReceiptEntryDobj();
                manualRcptDobj.setTrans_appl_no(rs.getString("transaction_appl_no"));
                manualRcptDobj.setApplno(rs.getString("appl_no"));
                manualRcptDobj.setRcptNo(rs.getString("rcpt_no"));
                manualRcptDobj.setRcpt_dt(rs.getString("rcpt_dt"));
                manualRcptDobj.setAmount(rs.getLong("amount"));
                manualRcptRecordList.add(manualRcptDobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException(e.getMessage());
                }
            }
        }
        return manualRcptRecordList;
    }
}
