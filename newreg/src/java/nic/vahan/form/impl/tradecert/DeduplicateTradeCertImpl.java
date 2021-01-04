/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.tradecert;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.tradecert.DeduplicateTradeCertDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author ManuSri
 */
public class DeduplicateTradeCertImpl {

    private static final Logger LOGGER = Logger.getLogger(DeduplicateTradeCertImpl.class);

    public List<SelectItem> fillDealers(String dealerChoiceCondition) throws VahanException {
        List<SelectItem> dealerList = null;
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        RowSet rs = null;
        Exception e = null;
        SessionVariables sessionVariables = new SessionVariables();

        String sql = "";
        if ("Admin_Dealer".equals(dealerChoiceCondition)) {

            sql = " SELECT DISTINCT vm_dealer.dealer_cd,vm_dealer.dealer_name,tm_userinfo.user_id,\n"
                    + " COALESCE(vm_dealer.d_add1, ''::character varying)  || ',' || COALESCE(vm_dealer.d_add2, ''::character varying) as dealerAddress  \n"
                    + " FROM " + TableList.VM_DEALER_MAST + " vm_dealer \n"
                    + " LEFT OUTER JOIN " + TableList.TM_USER_PERMISSIONS + " tm_userperm ON tm_userperm.dealer_cd = vm_dealer.dealer_cd \n"
                    + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " tm_userinfo ON tm_userinfo.user_cd = tm_userperm.user_cd \n"
                    + " INNER JOIN (SELECT count(*),cert_no,dealer_cd \n"
                    + " FROM " + TableList.VT_TRADE_CERTIFICATE + "\n"
                    + " WHERE state_cd = ?  AND off_cd = ?  \n"
                    + "	GROUP BY cert_no,dealer_cd HAVING count(*)>1) vt_trade_cert ON vt_trade_cert.dealer_cd = vm_dealer.dealer_cd\n"
                    + " WHERE vm_dealer.state_cd = ?  AND vm_dealer.off_cd = ? AND tm_userinfo.user_catg = 'D'  order by dealer_name ";

        } else if ("No_Admin_Dealer".equals(dealerChoiceCondition)) {

            sql = "SELECT distinct vm_dealer.dealer_cd, vm_dealer.dealer_name,  \n"
                    + "       COALESCE(vm_dealer.d_add1, ''::character varying)  || ',' || COALESCE(vm_dealer.d_add2, ''::character varying) as dealerAddress\n"
                    + " FROM " + TableList.VM_DEALER_MAST + " vm_dealer \n"
                    + " INNER JOIN (select count(*),cert_no,dealer_cd \n"
                    + " FROM " + TableList.VT_TRADE_CERTIFICATE + "\n"
                    + " WHERE state_cd=? AND OFF_cd=? \n"
                    + " GROUP BY cert_no,dealer_cd \n"
                    + " HAVING count(*)>1) vt_trade_cert ON vt_trade_cert.dealer_cd = vm_dealer.dealer_cd  \n"
                    + " WHERE vm_dealer.DEALER_CD \n"
                    + "   NOT IN (SELECT DISTINCT vm_dealer.dealer_cd \n"
                    + "           FROM " + TableList.VM_DEALER_MAST + " vm_dealer \n"
                    + "           LEFT OUTER JOIN " + TableList.TM_USER_PERMISSIONS + " tm_userperm ON tm_userperm.dealer_cd = vm_dealer.dealer_cd \n"
                    + "           LEFT OUTER JOIN " + TableList.TM_USER_INFO + " tm_userinfo ON tm_userinfo.user_cd = tm_userperm.user_cd \n"
                    + "           INNER JOIN (SELECT count(*),cert_no,dealer_cd \n"
                    + "                       FROM " + TableList.VT_TRADE_CERTIFICATE + "\n"
                    + "                       WHERE state_cd = ?  AND off_cd = ?  \n"
                    + "	                  GROUP BY cert_no,dealer_cd \n"
                    + "                       HAVING count(*)>1) vt_trade_cert ON vt_trade_cert.dealer_cd = vm_dealer.dealer_cd\n"
                    + "                       WHERE vm_dealer.state_cd = ?  AND vm_dealer.off_cd = ? AND tm_userinfo.user_catg = 'D' )\n"
                    + "   AND vm_dealer.state_cd = ?  AND vm_dealer.off_cd = ? \n"
                    + " ORDER BY vm_dealer.dealer_name ";
        }

        try {
            tmgr = new TransactionManager("fillDealers");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, sessionVariables.getStateCodeSelected());
            pstmt.setInt(2, sessionVariables.getOffCodeSelected());
            pstmt.setString(3, sessionVariables.getStateCodeSelected());
            pstmt.setInt(4, sessionVariables.getOffCodeSelected());
            if ("No_Admin_Dealer".equals(dealerChoiceCondition)) {
                pstmt.setString(5, sessionVariables.getStateCodeSelected());
                pstmt.setInt(6, sessionVariables.getOffCodeSelected());
                pstmt.setString(7, sessionVariables.getStateCodeSelected());
                pstmt.setInt(8, sessionVariables.getOffCodeSelected());
            }
            LOGGER.info("fillDealers sql :: " + pstmt.toString());
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if (dealerList == null) {
                    dealerList = new ArrayList<>();
                }
                if ("Admin_Dealer".equals(dealerChoiceCondition)) {
                    if (!CommonUtils.isNullOrBlank(rs.getString("user_id"))) {
                        dealerList.add(new SelectItem(rs.getString("dealer_cd") + "#" + rs.getString("user_id"), rs.getString("dealer_name") + " [ ID : " + rs.getString("user_id") + " ]"));
                    } else if (!CommonUtils.isNullOrBlank(rs.getString("dealerAddress"))) {
                        dealerList.add(new SelectItem(rs.getString("dealer_cd"), rs.getString("dealer_name") + " [ ADDRESS : " + rs.getString("dealerAddress") + " ]"));
                    } else {
                        dealerList.add(new SelectItem(rs.getString("dealer_cd"), rs.getString("dealer_name") + " ]"));
                    }
                } else if ("No_Admin_Dealer".equals(dealerChoiceCondition)) {
                    if (!CommonUtils.isNullOrBlank(rs.getString("dealerAddress"))) {
                        dealerList.add(new SelectItem(rs.getString("dealer_cd"), rs.getString("dealer_name") + " [ ADDRESS : " + rs.getString("dealerAddress") + " ]"));
                    } else {
                        dealerList.add(new SelectItem(rs.getString("dealer_cd"), rs.getString("dealer_name")));
                    }
                }
            }
        } catch (SQLException se) {
            e = se;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0] + " " + e.getMessage());
        } catch (Exception ex) {
            e = ex;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0] + " " + ex.getMessage());
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        if (e
                != null) {
            throw new VahanException("Error occured while fetching list of dealers from database.");
        }
        return dealerList;
    }

    public List<SelectItem> fillCertNo(String dealerCd) throws VahanException {
        List<SelectItem> tcNoList = null;
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        RowSet rs = null;
        Exception e = null;
        SessionVariables sessionVariables = new SessionVariables();
        String sql = " SELECT DISTINCT(cert_no),count(*) "
                + " FROM " + TableList.VT_TRADE_CERTIFICATE
                + " WHERE state_cd = ?  AND off_cd = ? AND dealer_cd = ? "
                + " GROUP BY cert_no "
                + " HAVING count(*) > 1 ";

        try {
            tmgr = new TransactionManager("fillCertNo");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, sessionVariables.getStateCodeSelected());
            pstmt.setInt(2, sessionVariables.getOffCodeSelected());
            pstmt.setString(3, dealerCd);
            LOGGER.info("fillCertNo sql :: " + pstmt.toString());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (tcNoList == null) {
                    tcNoList = new ArrayList<>();
                }
                tcNoList.add(new SelectItem(rs.getString("cert_no"), rs.getString("cert_no")));
            }
        } catch (SQLException se) {
            e = se;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0] + " " + e.getMessage());
        } catch (Exception ex) {
            e = ex;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0] + " " + ex.getMessage());
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        if (e != null) {
            throw new VahanException("Error occured while fetching list of trade certificate numbers from database.");
        }
        return tcNoList;
    }

    public List<DeduplicateTradeCertDobj> searchTCsForDealerAndTcNo(String dealer, String tcNo, String dealerChoiceCondition) throws VahanException {
        List<DeduplicateTradeCertDobj> tcDobjsList = null;
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        RowSet rs = null;
        Exception e = null;
        SessionVariables sessionVariables = new SessionVariables();
        String sql = " SELECT vt_fee.fees,vt_fee.rcpt_no,vt_fee.rcpt_dt,vt_trade_cert.dealer_cd, vm_dealer.dealer_name,vt_trade_cert.vch_catg, vm_vch_catg.catg_desc, vt_trade_cert.appl_no, "
                + " vt_trade_cert.cert_no, vt_trade_cert.issue_dt,vt_trade_cert.no_of_vch, "
                + " vt_trade_cert.valid_upto, vt_trade_cert.valid_from "
                + " FROM " + TableList.VT_TRADE_CERTIFICATE + " vt_trade_cert "
                + " LEFT OUTER JOIN " + TableList.VM_DEALER_MAST + " vm_dealer ON vm_dealer.dealer_cd = vt_trade_cert.dealer_cd "
                + " LEFT OUTER JOIN " + TableList.VM_VCH_CATG + " vm_vch_catg  ON vm_vch_catg.catg = vt_trade_cert.vch_catg "
                + " LEFT JOIN " + TableList.VP_APPL_RCPT_MAPPING + " vp_appl_rcpt_map ON vp_appl_rcpt_map.appl_no = vt_trade_cert.appl_no "
                + " LEFT JOIN " + TableList.VT_FEE + " vt_fee ON vt_fee.rcpt_no = vp_appl_rcpt_map.rcpt_no AND vt_fee.state_cd = ?  AND vt_fee.off_cd = ? AND vt_fee.pur_cd = 21"
                + " WHERE vt_trade_cert.state_cd = ?    AND vt_trade_cert.off_cd = ?  "
                + "       AND vt_trade_cert.cert_no = ? AND vt_trade_cert.dealer_cd ";

        if ("No_Admin_Dealer".equals(dealerChoiceCondition)) {
            sql += " NOT ";
        }

        sql += "  IN (SELECT DISTINCT vm_dealer.dealer_cd "
                + " FROM " + TableList.VM_DEALER_MAST + " vm_dealer"
                + " LEFT OUTER JOIN tm_user_permissions tm_userperm ON tm_userperm.dealer_cd = vm_dealer.dealer_cd"
                + " LEFT OUTER JOIN tm_user_info tm_userinfo ON tm_userinfo.user_cd = tm_userperm.user_cd"
                + " WHERE vm_dealer.state_cd = ? AND vm_dealer.off_cd = ? AND tm_userinfo.user_catg = 'D')"
                + " ORDER BY vt_fee.fees, vt_trade_cert.valid_upto DESC, vt_trade_cert.no_of_vch DESC";

        int srNo = 1;
        try {
            tmgr = new TransactionManager("searchTCsForDealerAndTcNo");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, sessionVariables.getStateCodeSelected());
            pstmt.setInt(2, sessionVariables.getOffCodeSelected());
            pstmt.setString(3, sessionVariables.getStateCodeSelected());
            pstmt.setInt(4, sessionVariables.getOffCodeSelected());
            pstmt.setString(5, tcNo);
            pstmt.setString(6, sessionVariables.getStateCodeSelected());
            pstmt.setInt(7, sessionVariables.getOffCodeSelected());
            LOGGER.info("searchTCsForDealerAndTcNo sql :: " + pstmt.toString());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (tcDobjsList == null) {
                    tcDobjsList = new ArrayList<>();
                }
                DeduplicateTradeCertDobj dobj = new DeduplicateTradeCertDobj();
                dobj.setSrNo(srNo++);
                dobj.setDealerChoiceCondition(dealerChoiceCondition);
                dobj.setDealer(rs.getString("dealer_cd"));
                dobj.setVehCatg(rs.getString("vch_catg"));
                dobj.setDealerName(rs.getString("dealer_name"));
                dobj.setVehCatgName(rs.getString("catg_desc"));
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setCertNo(rs.getString("cert_no"));
                dobj.setIssueDt(rs.getDate("issue_dt"));
                if (dobj.getIssueDt() != null) {
                    dobj.setIssueDtStr(JSFUtils.getDateInDD_MMM_YYYY(DateUtils.getDateInDDMMYYYY(dobj.getIssueDt())));
                }
                dobj.setNoOfVeh(rs.getInt("no_of_vch"));
                dobj.setValidUpto(rs.getDate("valid_upto"));
                if (dobj.getValidUpto() != null) {
                    dobj.setValidUptoStr(JSFUtils.getDateInDD_MMM_YYYY(DateUtils.getDateInDDMMYYYY(dobj.getValidUpto())));
                }
                dobj.setValidFrom(rs.getDate("valid_from"));
                if (dobj.getValidFrom() != null) {
                    dobj.setValidFromStr(JSFUtils.getDateInDD_MMM_YYYY(DateUtils.getDateInDDMMYYYY(dobj.getValidFrom())));
                }
                if (rs.getDouble("fees") != java.sql.Types.NULL) {
                    dobj.setFee(rs.getDouble("fees"));
                    dobj.setReceiptNo(rs.getString("rcpt_no"));
                    dobj.setReceiptDt(rs.getDate("rcpt_dt"));
                    dobj.setReceiptDtStr(JSFUtils.getDateInDD_MMM_YYYY(DateUtils.getDateInDDMMYYYY(dobj.getReceiptDt())));
                }
                tcDobjsList.add(dobj);
            }
        } catch (SQLException se) {
            e = se;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0] + " " + e.getMessage());
        } catch (Exception ex) {
            e = ex;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0] + " " + ex.getMessage());
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        if (e != null) {
            throw new VahanException("Error occured while fetching list of trade certificate records from database.");
        }
        return tcDobjsList;
    }

    public String generateCertNo(TransactionManager tmgr, DeduplicateTradeCertDobj dobj) throws VahanException {
        String certNo = null;
        Exception e = null;
        SessionVariables sessionVariables = new SessionVariables();
        try {
            certNo = ApplicationTradeCertImpl.getUniqueTcNo(tmgr, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER);
            if (!CommonUtils.isNullOrBlank(certNo)) {
                dobj.setNewCertNo(certNo);
                boolean recordSaved = insertOldTcMappingDetailsInNewTc(tmgr, dobj);
                if (recordSaved) {
                    return certNo;
                }
            }
        } catch (SQLException se) {
            e = se;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0] + " " + e.getMessage());
        } catch (Exception ex) {
            e = ex;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0] + " " + ex.getMessage());
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

        if (e != null) {
            throw new VahanException("Error occured while generating new trade certificate number from database.");
        }
        return certNo;
    }

    public Map update(List<DeduplicateTradeCertDobj> deduplicatedTcDobjList, List<DeduplicateTradeCertDobj> tcDobjList) throws VahanException {
        Map updateOprResultMap = new HashMap();
        TransactionManager tmgr = null;
        Exception e = null;
        try {
            tmgr = new TransactionManager("update");
            String mergeApplNo = deduplicatedTcDobjList.get(0).getApplNo();
            /**
             * Update new system generated trade certificate number in
             * vt_trade_certificate
             */
            List systemGeneratedNewtTcNosList = new ArrayList();
            int updateCounts = 0;
            for (DeduplicateTradeCertDobj dobj : deduplicatedTcDobjList) {
                String certNo = generateCertNo(tmgr, dobj);
                if (certNo != null) {
                    dobj.setNewCertNo(certNo);
                    dobj.setNewApplNo(mergeApplNo);
                    systemGeneratedNewtTcNosList.add("Vehicle Category: <b>" + dobj.getVehCatgName() + "</b> have Trade Certificate Number: <b>" + certNo + "</b>");
                } else {
                    throw new VahanException("Problem in generating new trade certificate number.");
                }
                boolean recordsMovedToHistory = moveRecordsInHistoryForTcNo(tmgr, dobj, "DEDUPL");
                boolean historyRecordsSavedInMergeTable = saveHistoryRecordsInMergedTableForNewApplNo(tmgr, dobj, "DEDUPL");
                int recordUpdated = updateTCRecordForTcNo(tmgr, dobj);
                if (historyRecordsSavedInMergeTable && recordsMovedToHistory) {
                    updateCounts += recordUpdated;
                }

            }
            if (updateCounts == deduplicatedTcDobjList.size()) {
                updateOprResultMap.put("updateOprResult", true);
                updateOprResultMap.put("TcNosList", systemGeneratedNewtTcNosList);
                tmgr.commit();
            } else {
                updateOprResultMap.put("updateOprResult", false);
                updateOprResultMap.put("TcNosList", null);
            }
        } catch (SQLException se) {
            e = se;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0] + " " + e.getMessage());
        } catch (Exception ex) {
            e = ex;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0] + " " + ex.getMessage());
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
        if (e != null) {
            throw new VahanException("Error occured while updating database.");
        }
        return updateOprResultMap;
    }

    private Boolean moveRecordsInHistoryForTcNo(TransactionManager tmgr, DeduplicateTradeCertDobj dobj, String fromAction) throws SQLException {
        SessionVariables sessionVariables = new SessionVariables();
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO " + TableList.VH_TRADE_CERTIFICATE
                    + " SELECT state_cd, off_cd, appl_no, dealer_cd, sr_no, vch_catg, no_of_vch, cert_no,\n"
                    + " valid_from, valid_upto, issue_dt, no_of_vch_used, current_timestamp as moved_on, ? moved_by, \n"
                    + " applicant_type,fuel_cd,no_vch_print,?,backlog_size,selected_duplicate_certificate"
                    + " FROM " + TableList.VT_TRADE_CERTIFICATE
                    + " WHERE state_cd  = ? AND off_cd   = ? "
                    + " AND   cert_no = ?   AND dealer_cd = ?"
                    + " AND   appl_no = ?   AND vch_catg = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, "Admin_Dealer".equals(dobj.getDealerChoiceCondition())
                    ? "DEDUPL".equals(fromAction) ? "A_ID_Y_DEDUPLICATE:DEDUPL" : "A_ID_Y_DEDUPLICATE:DELETE"
                    : "DEDUPL".equals(fromAction) ? "A_ID_N_DEDUPLICATE:DEDUPL" : "A_ID_N_DEDUPLICATE:DELETE");
            ps.setString(3, sessionVariables.getStateCodeSelected());
            ps.setInt(4, sessionVariables.getOffCodeSelected());
            ps.setString(5, dobj.getCertNo());
            ps.setString(6, dobj.getDealer());
            ps.setString(7, dobj.getApplNo());
            ps.setString(8, dobj.getVehCatg());
            LOGGER.info("moveRecordsInHistoryForTcNo sql :: " + ps.toString());
            int recordsMoved = ps.executeUpdate();
            return recordsMoved > 0 ? true : false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

    }

    private int updateTCRecordForTcNo(TransactionManager tmgr, DeduplicateTradeCertDobj dobj) throws SQLException, VahanException {
        SessionVariables sessionVariables = new SessionVariables();
        PreparedStatement ps = null;
        try {
            String sql = "UPDATE " + TableList.VT_TRADE_CERTIFICATE
                    + " SET   cert_no = ? \n"
                    + " WHERE state_cd  = ?  AND off_cd  = ? "
                    + " AND   cert_no = ?    AND dealer_cd = ? "
                    + " AND   appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getNewCertNo());
            ps.setString(2, sessionVariables.getStateCodeSelected());
            ps.setInt(3, sessionVariables.getOffCodeSelected());
            ps.setString(4, dobj.getCertNo());
            ps.setString(5, dobj.getDealer());
            ps.setString(6, dobj.getApplNo());
            LOGGER.info("updateTCRecordForTcNo sql :: " + ps.toString());
            return ps.executeUpdate();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    private boolean saveHistoryRecordsInMergedTableForNewApplNo(TransactionManager tmgr, DeduplicateTradeCertDobj dobj, String fromAction) throws SQLException {
        SessionVariables sessionVariables = new SessionVariables();
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO " + TableList.VT_TRADE_CERTIFICATE_MERGE
                    + " SELECT ? as merge_appl_no, ? as status, \n"
                    + " state_cd, off_cd, appl_no, dealer_cd, sr_no, vch_catg, no_of_vch, ? new_cert_no, cert_no, \n"
                    + " valid_from, valid_upto, issue_dt, no_of_vch_used, current_timestamp as op_dt, ? deal_cd, \n"
                    + " applicant_type, fuel_cd, no_vch_print, remark, backlog_size, selected_duplicate_certificate \n"
                    + " FROM " + TableList.VT_TRADE_CERTIFICATE
                    + " WHERE state_cd  = ? AND off_cd   = ? "
                    + " AND   cert_no = ?   AND dealer_cd = ? "
                    + " AND   appl_no = ?   AND vch_catg = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getNewApplNo());
            ps.setString(2, "Admin_Dealer".equals(dobj.getDealerChoiceCondition())
                    ? "A_ID_Y_DEDUPLICATE:" + fromAction
                    : "A_ID_N_DEDUPLICATE:" + fromAction);
            ps.setString(3, dobj.getNewCertNo());
            ps.setString(4, Util.getEmpCode());
            ps.setString(5, sessionVariables.getStateCodeSelected());
            ps.setInt(6, sessionVariables.getOffCodeSelected());
            ps.setString(7, dobj.getCertNo());
            ps.setString(8, dobj.getDealer());
            ps.setString(9, dobj.getApplNo());
            ps.setString(10, dobj.getVehCatg());
            LOGGER.info("INSERT INTO VT_TRADE_CERTIFICATE_MERGE sql :: " + ps.toString());
            int recordsSaved = ps.executeUpdate();
            return recordsSaved > 0 ? true : false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public boolean insertOldTcMappingDetailsInNewTc(TransactionManager tmgr, DeduplicateTradeCertDobj dobj) throws SQLException {
        SessionVariables sessionVariables = new SessionVariables();
        PreparedStatement ps = null;
        String sql = "INSERT INTO " + TableList.VH_REASSIGN_TC
                + " (state_cd, off_cd, dealer_cd, vch_catg, old_tc_no, new_assigned_tc_no, assigned_on, applicant_type)"
                + " VALUES(?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";
        try {

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, sessionVariables.getStateCodeSelected());
            ps.setInt(2, sessionVariables.getOffCodeSelected());
            ps.setString(3, dobj.getDealer());
            ps.setString(4, dobj.getVehCatg());
            ps.setString(5, dobj.getCertNo());
            ps.setString(6, dobj.getNewCertNo());
            ps.setString(7, "D");
            LOGGER.info("insertOldTcMappingDetailsInNewTc sql :: " + ps.toString());
            int recordSaved = ps.executeUpdate();
            return recordSaved > 0 ? true : false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public Boolean delete(List<DeduplicateTradeCertDobj> deduplicatedTcDobjList) throws VahanException {
        boolean deleteOperationComplete = false;
        TransactionManager tmgr = null;
        Exception e = null;

        try {
            tmgr = new TransactionManager("delete");
            int recordUpdateCount = 0;
            String mergeApplNo = deduplicatedTcDobjList.get(0).getApplNo();
            String certNo = "NOT_GENERATED";
            for (DeduplicateTradeCertDobj dobj : deduplicatedTcDobjList) {
                dobj.setNewApplNo(mergeApplNo);
                dobj.setNewCertNo(certNo);
                boolean recordsMovedToHistory = moveRecordsInHistoryForTcNo(tmgr, dobj, "DISPOSE");
                boolean historyRecordsSavedInMergeTable = saveHistoryRecordsInMergedTableForNewApplNo(tmgr, dobj, "DELETE");
                boolean recordsDeleted = deleteTCRecordsForDealerAndCertNo(tmgr, dobj);
                if (recordsMovedToHistory
                        && historyRecordsSavedInMergeTable
                        && recordsDeleted) {
                    recordUpdateCount++;
                }
            }
            if (recordUpdateCount == deduplicatedTcDobjList.size()) {
                deleteOperationComplete = true;
                tmgr.commit();
            }

        } catch (SQLException se) {
            e = se;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0] + " " + e.getMessage());
        } catch (Exception ex) {
            e = ex;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0] + " " + ex.getMessage());
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
        if (e
                != null) {
            throw new VahanException("Error occured while disposing trade certificate records in database.");
        }
        return deleteOperationComplete;
    }

    private boolean deleteTCRecordsForDealerAndCertNo(TransactionManager tmgr, DeduplicateTradeCertDobj dobj) throws SQLException {
        SessionVariables sessionVariables = new SessionVariables();
        PreparedStatement ps = null;
        try {
            String sql = "DELETE FROM " + TableList.VT_TRADE_CERTIFICATE
                    + " WHERE state_cd  = ?  AND off_cd  = ? "
                    + " AND   dealer_cd = ?  AND cert_no = ? "
                    + " AND   appl_no = ?   AND vch_catg = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, sessionVariables.getStateCodeSelected());
            ps.setInt(2, sessionVariables.getOffCodeSelected());
            ps.setString(3, dobj.getDealer());
            ps.setString(4, dobj.getCertNo());
            ps.setString(5, dobj.getApplNo());
            ps.setString(6, dobj.getVehCatg());
            LOGGER.info("deleteTCRecordsForDealerAndCertNo sql :: " + ps.toString());
            int recordsDeleted = ps.executeUpdate();
            return recordsDeleted > 0 ? true : false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }
}
