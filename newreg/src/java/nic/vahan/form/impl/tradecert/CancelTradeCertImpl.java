/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.tradecert;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.tradecert.CancelTradeCertDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

public class CancelTradeCertImpl {

    private static final Logger LOGGER = Logger.getLogger(CancelTradeCertImpl.class);

    public static void save(CancelTradeCertDobj cancelTrDobj, List listTCDetailsForDealer) throws VahanException {
        TransactionManager tmgr = null;
        String applNo;
        try {
            tmgr = new TransactionManager("save");
            applNo = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            cancelTrDobj.setApplNo(applNo);
            insertIntoVaCancelTrade(tmgr, cancelTrDobj, listTCDetailsForDealer);
            Status_dobj status = new Status_dobj();
            status.setAppl_no(applNo);
            status.setPur_cd(TableConstants.VM_CANCEL_TRADE_CERT);
            status.setRegn_no(cancelTrDobj.getTradeCertNo());
            status.setFlow_slno(1);
            status.setFile_movement_slno(1);
            status.setAction_cd(TableConstants.VM_CANCEL_TRADE_CERT_ENTRY);
            status.setEmp_cd(Long.parseLong(Util.getEmpCode()));
            status.setOffice_remark("");
            status.setPublic_remark("");
            status.setStatus("N");
            status.setState_cd(Util.getUserStateCode());
            status.setOff_cd(Util.getSelectedSeat().getOff_cd());
            ServerUtil.fileFlowForNewApplication(tmgr, status);
            status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
            status.setStatus(TableConstants.STATUS_COMPLETE);

            ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, applNo,
                    TableConstants.VM_CANCEL_TRADE_CERT_VERIFY, TableConstants.VM_CANCEL_TRADE_CERT, null, tmgr);
            ServerUtil.fileFlow(tmgr, status);

            tmgr.commit();
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

    public static String saveAndMoveFile(Status_dobj status_dobj, CancelTradeCertDobj Cdobj, List listTCDetailsToCancel) throws VahanException {
        TransactionManager tmgr = null;
        String regnNo = "";
        PreparedStatement psmt;
        RowSet rs = null;
        CancelTradeCertDobj cancelTradeCertDobj;
        try {
            tmgr = new TransactionManager("saveAndMoveFile");
            if (status_dobj.getAction_cd() == TableConstants.VM_CANCEL_TRADE_CERT_APPROVE
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)) {
                regnNo = Cdobj.getTradeCertNo();
                if ((regnNo != null && !regnNo.equals(""))) {
                    String sql = "INSERT INTO " + TableList.VT_SURRENDER_TRADE_CERTIFICATE + " "
                            + " SELECT state_cd, off_cd, appl_no, dealer_cd, vch_catg, no_of_vch, cert_no, valid_from, valid_upto, current_timestamp, status, \n"
                            + " reason_for_cancel, order_by, order_no, order_dt, sr_no, tc_appl_no, tc_sr_no "
                            + "  from  " + TableList.VA_SURRENDER_TRADE_CERTIFICATE + " where appl_no=? and state_cd=? and off_cd=?";
                    psmt = tmgr.prepareStatement(sql);
                    psmt.setString(1, Cdobj.getApplNo());
                    psmt.setString(2, Cdobj.getStateCd());
                    psmt.setInt(3, Cdobj.getOffCd());
                    psmt.executeUpdate();

                    String sqlVhaSrTrade = "INSERT INTO " + TableList.VHA_SURRENDER_TRADE_CERTIFICATE + " ( state_cd, off_cd, appl_no, "
                            + "dealer_cd, vch_catg, no_of_vch, cert_no, valid_from, valid_upto, op_dt, status, \n"
                            + "reason_for_cancel, order_by, order_no, order_dt, sr_no, tc_appl_no, tc_sr_no, moved_on, moved_by)\n"
                            + " SELECT state_cd, off_cd, appl_no, dealer_cd, vch_catg, no_of_vch, cert_no, valid_from, valid_upto, "
                            + "current_timestamp, status, reason_for_cancel, order_by, order_no, order_dt, sr_no, tc_appl_no,"
                            + " tc_sr_no , CURRENT_TIMESTAMP,'" + Cdobj.getEmpCd() + "'"
                            + "  from  " + TableList.VA_SURRENDER_TRADE_CERTIFICATE + " where appl_no=? and state_cd=? and off_cd=?";
                    psmt = tmgr.prepareStatement(sqlVhaSrTrade);
                    psmt.setString(1, Cdobj.getApplNo());
                    psmt.setString(2, Cdobj.getStateCd());
                    psmt.setInt(3, Cdobj.getOffCd());
                    psmt.executeUpdate();

                    String sqlVhTcSave = "INSERT INTO " + TableList.VH_TRADE_CERTIFICATE
                            + " (state_cd, off_cd, appl_no, dealer_cd, sr_no, vch_catg, no_of_vch, cert_no,"
                            + " valid_from, valid_upto, issue_dt, no_of_vch_used, moved_on, moved_by, fuel_cd,"
                            + " no_vch_print, applicant_type, remark, backlog_size, selected_duplicate_certificate)"
                            + "(SELECT state_cd, off_cd, appl_no, dealer_cd, sr_no, vch_catg, no_of_vch, cert_no,"
                            + " valid_from, valid_upto, issue_dt, no_of_vch_used, CURRENT_TIMESTAMP, '" + Cdobj.getEmpCd()
                            + "', fuel_cd, no_vch_print, applicant_type, CONCAT  (remark,'[CANCELLED]') AS remark, backlog_size, selected_duplicate_certificate FROM "
                            + TableList.VT_TRADE_CERTIFICATE + " where appl_no=? and state_cd=? and off_cd=? and sr_no = ? and vch_catg = ?)";

                    psmt = tmgr.prepareStatement(sqlVhTcSave);
                    for (Object keyObj : listTCDetailsToCancel) {
                        cancelTradeCertDobj = (CancelTradeCertDobj) keyObj;
                        psmt.setString(1, cancelTradeCertDobj.getTcApplNo());
                        psmt.setString(2, Cdobj.getStateCd());
                        psmt.setInt(3, Cdobj.getOffCd());
                        psmt.setInt(4, cancelTradeCertDobj.getTcSrNo());
                        psmt.setString(5, cancelTradeCertDobj.getVehCatgFor());
                        psmt.executeUpdate();
                    }

                    sql = "DELETE FROM " + TableList.VT_TRADE_CERTIFICATE + " where appl_no = ? and state_cd=? and off_cd=? and sr_no = ? and vch_catg = ?";
                    psmt = tmgr.prepareStatement(sql);
                    for (Object keyObj : listTCDetailsToCancel) {
                        cancelTradeCertDobj = (CancelTradeCertDobj) keyObj;
                        psmt.setString(1, cancelTradeCertDobj.getTcApplNo());
                        psmt.setString(2, Cdobj.getStateCd());
                        psmt.setInt(3, Cdobj.getOffCd());
                        psmt.setInt(4, cancelTradeCertDobj.getTcSrNo());
                        psmt.setString(5, cancelTradeCertDobj.getVehCatgFor());
                        psmt.executeUpdate();
                    }

                    sql = "DELETE FROM " + TableList.VA_SURRENDER_TRADE_CERTIFICATE + " where appl_no = ? and state_cd=? and off_cd=?";
                    psmt = tmgr.prepareStatement(sql);
                    psmt.setString(1, Cdobj.getApplNo());
                    psmt.setString(2, Cdobj.getStateCd());
                    psmt.setInt(3, Cdobj.getOffCd());
                    psmt.executeUpdate();

                    status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                    ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                }
            }
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return regnNo;
    }

    public List getTradeCancelDetails(CancelTradeCertDobj dobj) throws VahanException {

        List listForTCDetail = new ArrayList<>();
        PreparedStatement psmt;
        RowSet rs;
        TransactionManager tmgr = null;
        CancelTradeCertDobj cancelTradeCertDobj;

        String sql = " Select * from " + TableList.VA_SURRENDER_TRADE_CERTIFICATE
                + " where appl_no = ? and state_cd = ? and off_cd = ?";
        try {
            tmgr = new TransactionManager("getTradeCancelDetails");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, dobj.getApplNo());
            psmt.setString(2, dobj.getStateCd());
            psmt.setInt(3, dobj.getOffCd());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                cancelTradeCertDobj = new CancelTradeCertDobj();
                cancelTradeCertDobj.setVehCatgName(ApplicationTradeCertImpl.getVehCatgDesc(rs.getString("vch_catg")));
                cancelTradeCertDobj.setVehCatgFor(rs.getString("vch_catg"));
                cancelTradeCertDobj.setNoOfAllowedVehicles(rs.getString("no_of_vch"));
                cancelTradeCertDobj.setTcSrNo(rs.getInt("tc_sr_no"));
                cancelTradeCertDobj.setValidUpto(rs.getDate("valid_upto"));
                cancelTradeCertDobj.setValidFrom(rs.getDate("valid_from"));
                cancelTradeCertDobj.setTcApplNo(rs.getString("tc_appl_no"));
                listForTCDetail.add(cancelTradeCertDobj);
            }
            if (listForTCDetail.isEmpty()) {
                throw new VahanException("Trade Certificate Details not Found.");
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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
        return listForTCDetail;
    }

    public CancelTradeCertDobj getDealerDetails(CancelTradeCertDobj cancelTradeCertDobj) throws VahanException {
        PreparedStatement psmt;
        RowSet rs;
        TransactionManager tmgr = null;

        String sql = " Select distinct(vm_dealer.dealer_cd),vm_dealer.dealer_name,vt_tc.cert_no, "
                + " COALESCE(vm_dealer.d_add1, ''::character varying)  || ',' || COALESCE(vm_dealer.d_add2, ''::character varying) as dealerAddress "
                + " from " + TableList.VM_DEALER_MAST + " vm_dealer "
                + " LEFT OUTER JOIN " + TableList.VT_TRADE_CERTIFICATE + " vt_tc ON vt_tc.dealer_cd = vm_dealer.dealer_cd "
                + " where vt_tc.cert_no = ? and vm_dealer.state_cd = ? and vm_dealer.off_cd = ?";
        try {
            tmgr = new TransactionManager("getDealerDetails");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, cancelTradeCertDobj.getTradeCertNo());
            psmt.setString(2, cancelTradeCertDobj.getStateCd());
            psmt.setInt(3, cancelTradeCertDobj.getOffCd());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                cancelTradeCertDobj.setDealerName(rs.getString("dealer_name"));
                cancelTradeCertDobj.setDealerAddress(rs.getString("dealerAddress"));
                cancelTradeCertDobj.setTradeCertNo(rs.getString("cert_no"));
                cancelTradeCertDobj.setDealerCode(rs.getString("dealer_cd"));
            } else {
                throw new VahanException("Trade Certificate Number not Found.");
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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
        return cancelTradeCertDobj;
    }

    public static void insertIntoVaCancelTrade(TransactionManager tmgr, CancelTradeCertDobj Cdobj, Collection listTCDetailsForDealer) throws SQLException, Exception {
        PreparedStatement pstmt;
        String sql;
        int srNo = 1;
        CancelTradeCertDobj cancelTradeCertDobj;
        sql = "INSERT INTO " + TableList.VA_SURRENDER_TRADE_CERTIFICATE + " "
                + "(state_cd, off_cd, appl_no, dealer_cd, vch_catg, no_of_vch, cert_no, valid_from, valid_upto, status, reason_for_cancel, order_by, order_no, order_dt, op_dt, sr_no,tc_appl_no,tc_sr_no) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp, ?, ?, ?)";
        pstmt = tmgr.prepareStatement(sql);
        for (Object keyObj : listTCDetailsForDealer) {
            cancelTradeCertDobj = (CancelTradeCertDobj) keyObj;
            int i = 1;
            pstmt.setString(i++, Cdobj.getStateCd());
            pstmt.setInt(i++, Cdobj.getOffCd());
            pstmt.setString(i++, Cdobj.getApplNo());
            pstmt.setString(i++, Cdobj.getDealerCode());
            pstmt.setString(i++, cancelTradeCertDobj.getVehCatgFor());
            pstmt.setInt(i++, Integer.valueOf(cancelTradeCertDobj.getNoOfAllowedVehicles()));
            pstmt.setString(i++, Cdobj.getTradeCertNo());
            pstmt.setDate(i++, new java.sql.Date(cancelTradeCertDobj.getValidFrom().getTime()));
            pstmt.setDate(i++, new java.sql.Date(cancelTradeCertDobj.getValidUpto().getTime()));
            pstmt.setString(i++, "CANC");
            pstmt.setString(i++, Cdobj.getReasonForCancellation());
            pstmt.setString(i++, Cdobj.getOrderBy());
            pstmt.setString(i++, Cdobj.getOrderNo());
            pstmt.setDate(i++, new java.sql.Date(Cdobj.getOrderDt().getTime()));
            pstmt.setInt(i++, srNo++);
            pstmt.setString(i++, cancelTradeCertDobj.getTcApplNo());
            pstmt.setInt(i++, cancelTradeCertDobj.getTcSrNo());
            pstmt.addBatch();
        }
        if (pstmt != null && (listTCDetailsForDealer != null && !listTCDetailsForDealer.isEmpty())) {
            pstmt.executeBatch();
        }
    }

    public CancelTradeCertDobj getTradeCancelOrderByDetails(CancelTradeCertDobj dobj) throws VahanException {

        PreparedStatement psmt;
        RowSet rs;
        TransactionManager tmgr = null;

        String sql = " Select * from " + TableList.VA_SURRENDER_TRADE_CERTIFICATE
                + " where appl_no = ? and state_cd = ? and off_cd = ?";
        try {
            tmgr = new TransactionManager("getTradeCancelOrderByDetails");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, dobj.getApplNo());
            psmt.setString(2, dobj.getStateCd());
            psmt.setInt(3, dobj.getOffCd());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj.setOrderBy(rs.getString("order_by"));
                dobj.setOrderDt(rs.getDate("order_dt"));
                dobj.setOrderNo(rs.getString("order_no"));
                dobj.setReasonForCancellation(rs.getString("reason_for_cancel"));
            } else {
                throw new VahanException("Data not Found.");
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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
        return dobj;
    }

    public List getTCDetails(CancelTradeCertDobj dobj) throws VahanException {
        List listForTradeDetail = new ArrayList<>();
        PreparedStatement psmt;
        RowSet rs;
        TransactionManager tmgr = null;
        CancelTradeCertDobj cancelTradeCertDobj;
        try {
            tmgr = new TransactionManager("getTCDetails");

            //To check if any online transaction is pending in pamayent mode for dealer                    
            String sqlVtTemp = "Select transaction_no,application_status from " + TableList.VT_TEMP_APPL_TRANSACTION + " where regn_no= ? and state_cd = ? and off_cd = ?";
            psmt = tmgr.prepareStatement(sqlVtTemp);
            psmt.setString(1, dobj.getDealerCode());
            psmt.setString(2, dobj.getStateCd());
            psmt.setInt(3, dobj.getOffCd());
            rs = tmgr.fetchDetachedRowSet_No_release();

            StringBuilder message = new StringBuilder();
            message.append("! Some Transaction(s) are pending against Applicant Name: ").append(dobj.getDealerName());
            int noOfPendingTrans = 0;
            while (rs.next()) {
                if (!rs.getString("application_status").isEmpty() && TableConstants.TRADE_CERT_PENDING_FOR_PAYMENT_VAL.equalsIgnoreCase(rs.getString("application_status"))) {
                    if (noOfPendingTrans == 0) {
                        message.append(TableConstants.TRADE_CERT_STRING_SPLIT_CONSTANT);
                        message.append("> Check Pending Payment in VahanTC portal for applications :  ");
                    } else {
                        message.append(", ");
                    }
                    message.append(rs.getString("transaction_no"));
                    noOfPendingTrans++;
                }
            }

            //To check if any transaction is pending in verify/approved mode
            String sqlVaTrade = "Select appl_no from " + TableList.VA_TRADE_CERTIFICATE + " where dealer_cd= ?  and state_cd = ? and off_cd = ?";
            psmt = tmgr.prepareStatement(sqlVaTrade);
            psmt.setString(1, dobj.getDealerCode());
            psmt.setString(2, dobj.getStateCd());
            psmt.setInt(3, dobj.getOffCd());
            rs = tmgr.fetchDetachedRowSet_No_release();
            int noOfApplInVaTrade = 0;
            while (rs.next()) {
                if (noOfApplInVaTrade == 0) {
                    message.append(TableConstants.TRADE_CERT_STRING_SPLIT_CONSTANT);
                    message.append("> Pending applications in Vahan4 portal:  ");
                } else {
                    message.append(", ");
                }
                message.append(rs.getString("appl_no"));
                noOfApplInVaTrade++;
            }
            if (noOfPendingTrans > 0 || noOfApplInVaTrade > 0) {
                message.append(TableConstants.TRADE_CERT_STRING_SPLIT_CONSTANT);
                message.append(" First process above transactions, then progress on cancellation of Trade Certificate No.").append(dobj.getTradeCertNo());
                throw new VahanException(message.toString());
            }

            String sql = " Select vm_dealer.dealer_cd,vt_tc.vch_catg,vt_tc.no_of_vch,vt_tc.valid_upto,vt_tc.valid_from,vt_tc.appl_no as tc_appl_no,vt_tc.sr_no as tc_sr_no "
                    + " from " + TableList.VM_DEALER_MAST + " vm_dealer "
                    + " LEFT OUTER JOIN " + TableList.VT_TRADE_CERTIFICATE + " vt_tc ON vt_tc.dealer_cd = vm_dealer.dealer_cd "
                    + " where vt_tc.appl_no NOT IN (Select tc_appl_no from " + TableList.VA_SURRENDER_TRADE_CERTIFICATE + " where cert_no = ?) and "
                    + "vt_tc.cert_no = ? and vm_dealer.state_cd = ? and vm_dealer.off_cd = ? order by vt_tc asc";

            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, dobj.getTradeCertNo());
            psmt.setString(2, dobj.getTradeCertNo());
            psmt.setString(3, dobj.getStateCd());
            psmt.setInt(4, dobj.getOffCd());
            rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                cancelTradeCertDobj = new CancelTradeCertDobj();
                cancelTradeCertDobj.setVehCatgName(ApplicationTradeCertImpl.getVehCatgDesc(rs.getString("vch_catg")));
                cancelTradeCertDobj.setVehCatgFor(rs.getString("vch_catg"));
                cancelTradeCertDobj.setNoOfAllowedVehicles(rs.getString("no_of_vch"));
                cancelTradeCertDobj.setValidUpto(rs.getDate("valid_upto"));
                cancelTradeCertDobj.setValidFrom(rs.getDate("valid_from"));
                cancelTradeCertDobj.setTcApplNo(rs.getString("tc_appl_no"));
                cancelTradeCertDobj.setTcSrNo(rs.getInt("tc_sr_no"));
                listForTradeDetail.add(cancelTradeCertDobj);
            }
            if (listForTradeDetail.isEmpty()) {
                throw new VahanException("Already applied cancellation of the provided trade certificate.");
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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
        return listForTradeDetail;
    }

    /**
     * For Dispose of Application and used in Dispose~of~Application form
     *
     * @param tmgr
     * @param appl_no
     * @throws SQLException
     */
    public static void insertTCCancelApplicationToHistory(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps;
        String sql;

        sql = "INSERT INTO " + TableList.VHA_SURRENDER_TRADE_CERTIFICATE
                + " SELECT state_cd, off_cd, appl_no, dealer_cd, vch_catg, no_of_vch, cert_no, valid_from, valid_upto, \n"
                + "  current_timestamp, status, reason_for_cancel, order_by, order_no, order_dt, sr_no, tc_appl_no, \n"
                + "  tc_sr_no , current_timestamp as moved_on, ? moved_by \n"
                + "  from  " + TableList.VA_SURRENDER_TRADE_CERTIFICATE + " where appl_no=?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    } // end of insertIntoToHistory
}
