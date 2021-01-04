/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.tradecert;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.tradecert.MergeTradeCertDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
public class MergeTradeCertImpl {

    private static final Logger LOGGER = Logger.getLogger(MergeTradeCertImpl.class);

    public List<SelectItem> fillDealers(String dealerChoiceCondition) throws VahanException {
        List<SelectItem> dealerList = null;
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        RowSet rs = null;
        Exception e = null;
        SessionVariables sessionVariables = new SessionVariables();

        String sql = "";
        if ("Admin_Dealer".equals(dealerChoiceCondition)) {

            sql = " SELECT DISTINCT vm_dealer.dealer_cd,vm_dealer.dealer_name,tm_userinfo.user_id, "
                    + " COALESCE(vm_dealer.d_add1, ''::character varying)  || ',' || COALESCE(vm_dealer.d_add2, ''::character varying) as dealerAddress "
                    + " FROM " + TableList.VM_DEALER_MAST + " vm_dealer "
                    + " LEFT OUTER JOIN " + TableList.TM_USER_PERMISSIONS + " tm_userperm ON tm_userperm.dealer_cd = vm_dealer.dealer_cd "
                    + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " tm_userinfo ON tm_userinfo.user_cd = tm_userperm.user_cd "
                    + " INNER JOIN (SELECT count(*),dealer_cd,vch_catg "
                    + "	            FROM " + TableList.VT_TRADE_CERTIFICATE
                    + "             WHERE state_cd = ?  AND off_cd = ? "
                    + "             GROUP BY dealer_cd,vch_catg HAVING count(*)>1) vt_trade_cert ON vt_trade_cert.dealer_cd = vm_dealer.dealer_cd "
                    + " WHERE vm_dealer.state_cd = ?  AND vm_dealer.off_cd = ? AND tm_userinfo.user_catg = 'D' ";

        } else if ("No_Admin_Dealer".equals(dealerChoiceCondition)) {

            sql = "SELECT DISTINCT vm_dealer.dealer_cd,vm_dealer.dealer_name,  "
                    + " COALESCE(vm_dealer.d_add1, ''::character varying)  || ',' || COALESCE(vm_dealer.d_add2, ''::character varying) as dealerAddress "
                    + " FROM " + TableList.VM_DEALER_MAST + " vm_dealer "
                    + " INNER JOIN (SELECT count(*),dealer_cd,vch_catg "
                    + "	            FROM " + TableList.VT_TRADE_CERTIFICATE
                    + "             WHERE state_cd = ?  AND off_cd = ? "
                    + "             GROUP BY dealer_cd,vch_catg HAVING count(*)>1) vt_trade_cert ON vt_trade_cert.dealer_cd = vm_dealer.dealer_cd "
                    + " WHERE vm_dealer.DEALER_CD NOT IN (SELECT DISTINCT vm_dealer.dealer_cd "
                    + "                                   FROM " + TableList.VM_DEALER_MAST + " vm_dealer "
                    + "                                   LEFT OUTER JOIN " + TableList.TM_USER_PERMISSIONS + " tm_userperm ON tm_userperm.dealer_cd = vm_dealer.dealer_cd "
                    + "                                   LEFT OUTER JOIN " + TableList.TM_USER_INFO + " tm_userinfo ON tm_userinfo.user_cd = tm_userperm.user_cd "
                    + "                                   LEFT OUTER JOIN " + TableList.VT_TRADE_CERTIFICATE + " vt_trade_cert ON vt_trade_cert.dealer_cd = vm_dealer.dealer_cd AND vt_trade_cert.state_cd = ? AND vt_trade_cert.off_cd = ?"
                    + "                                   WHERE vm_dealer.state_cd = ? AND vm_dealer.off_cd = ? AND tm_userinfo.user_catg = 'D') "
                    + "       AND vm_dealer.state_cd = ? AND vm_dealer.off_cd = ? ";
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
                        dealerList.add(new SelectItem(rs.getString("dealer_cd"), rs.getString("dealer_name")));
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

    public List<SelectItem> fillVehCatgs(String dealerCd) throws VahanException {
        List<SelectItem> vehCatgList = null;
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        RowSet rs = null;
        Exception e = null;
        SessionVariables sessionVariables = new SessionVariables();
        String sql = " SELECT DISTINCT vm_vch_catg.catg,vm_vch_catg.catg_desc "
                + " FROM " + TableList.VM_VCH_CATG + " vm_vch_catg "
                + " INNER JOIN (SELECT count(*),state_cd,off_cd,vch_catg,dealer_cd "
                + "             FROM " + TableList.VT_TRADE_CERTIFICATE
                + "             WHERE state_cd = ?  AND off_cd = ? "
                + "             GROUP BY vch_catg,dealer_cd,state_cd,off_cd "
                + "             HAVING count(*)>1) vt_trade_cert "
                + " ON vt_trade_cert.vch_catg = vm_vch_catg.catg AND vt_trade_cert.state_cd = ?  AND vt_trade_cert.off_cd = ?  AND vt_trade_cert.dealer_cd = ?";

        try {
            tmgr = new TransactionManager("fillVehCatgs");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, sessionVariables.getStateCodeSelected());
            pstmt.setInt(2, sessionVariables.getOffCodeSelected());
            pstmt.setString(3, sessionVariables.getStateCodeSelected());
            pstmt.setInt(4, sessionVariables.getOffCodeSelected());
            pstmt.setString(5, dealerCd);
            LOGGER.info("fillVehCatgs sql :: " + pstmt.toString());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (vehCatgList == null) {
                    vehCatgList = new ArrayList<>();
                }
                vehCatgList.add(new SelectItem(rs.getString("catg"), rs.getString("catg_desc")));
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
            throw new VahanException("Error occured while fetching list of vehicle categories from database.");
        }
        return vehCatgList;
    }

    public List<MergeTradeCertDobj> searchTCsForDealerAndVehCatg(String dealer, String vehCatg, String dealerChoiceCondition) throws VahanException {
        List<MergeTradeCertDobj> tcDobjsList = null;
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
                + " WHERE vt_trade_cert.state_cd = ?  AND vt_trade_cert.off_cd = ?  "
                + "   AND vt_trade_cert.dealer_cd = ? AND vt_trade_cert.vch_catg = ? "
                + " ORDER BY vt_fee.fees, vt_trade_cert.valid_upto DESC, vt_trade_cert.no_of_vch DESC";

        int srNo = 1;
        try {
            tmgr = new TransactionManager("searchTCsForDealerAndVehCatg");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, sessionVariables.getStateCodeSelected());
            pstmt.setInt(2, sessionVariables.getOffCodeSelected());
            pstmt.setString(3, sessionVariables.getStateCodeSelected());
            pstmt.setInt(4, sessionVariables.getOffCodeSelected());
            pstmt.setString(5, dealer);
            pstmt.setString(6, vehCatg);
            LOGGER.info("searchTCsForDealerAndVehCatg sql :: " + pstmt.toString());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (tcDobjsList == null) {
                    tcDobjsList = new ArrayList<>();
                }
                MergeTradeCertDobj dobj = new MergeTradeCertDobj();
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

    public String generateCertNo(TransactionManager tmgr, MergeTradeCertDobj dobj) throws VahanException {
        String certNo = null;
        Exception e = null;
        SessionVariables sessionVariables = new SessionVariables();
        try {
            certNo = ApplicationTradeCertImpl.getUniqueTcNo(tmgr, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER);
            dobj.setNewMergeCertNo(certNo);
            boolean recordSaved = insertOldTcMappingDetailsInNewTc(tmgr, dobj);
            if (recordSaved) {
                return certNo;
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

    public Boolean update(List<MergeTradeCertDobj> mergedTcDobjList, List<MergeTradeCertDobj> tcDobjList) throws VahanException {
        boolean updateOperationComplete = false;
        TransactionManager tmgr = null;
        Exception e = null;

        try {
            tmgr = new TransactionManager("update");
            MergeTradeCertDobj dobj = mergedTcDobjList.get(0);
            dobj.setNewMergeApplNo(dobj.getApplNo());
            dobj.setNewMergeCertNo(generateCertNo(tmgr, dobj));
            boolean recordsMovedToHistory = moveRecordsInHistoryForDealerAndVehCatg(tmgr, dobj);
            boolean historyRecordsSavedInMergeTable = saveHistoryRecordsInMergedTableForNewMergedApplNo(tmgr, dobj);
            boolean mergedRecordUpdated = updateTCRecordForApplNo(tmgr, dobj);
            boolean nonEssentialRecordsDeleted = deleteTCRecordsForNonUpdatedApplNo(tmgr, tcDobjList, dobj.getApplNo());
            if (recordsMovedToHistory
                    && historyRecordsSavedInMergeTable
                    && mergedRecordUpdated
                    && nonEssentialRecordsDeleted) {
                updateOperationComplete = true;
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
        if (e != null) {
            throw new VahanException("Error occured while updating database.");
        }
        return updateOperationComplete;
    }

    private Boolean moveRecordsInHistoryForDealerAndVehCatg(TransactionManager tmgr, MergeTradeCertDobj dobj) throws SQLException {
        SessionVariables sessionVariables = new SessionVariables();
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO " + TableList.VH_TRADE_CERTIFICATE
                    + " SELECT state_cd, off_cd, appl_no, dealer_cd, sr_no, vch_catg, no_of_vch, cert_no,\n"
                    + " valid_from, valid_upto, issue_dt, no_of_vch_used, current_timestamp as moved_on, ? moved_by, \n"
                    + " applicant_type,fuel_cd,no_vch_print,?,backlog_size,selected_duplicate_certificate"
                    + " FROM " + TableList.VT_TRADE_CERTIFICATE
                    + " WHERE state_cd  = ? AND off_cd   = ? "
                    + " AND   dealer_cd = ? AND vch_catg = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, "Admin_Dealer".equals(dobj.getDealerChoiceCondition()) ? "A_ID_Y_MERGE:MERGE" : "A_ID_N_MERGE:MERGE");
            ps.setString(3, sessionVariables.getStateCodeSelected());
            ps.setInt(4, sessionVariables.getOffCodeSelected());
            ps.setString(5, dobj.getDealer());
            ps.setString(6, dobj.getVehCatg());
            LOGGER.info("moveRecordsInHistoryForDealerAndVehCatg sql :: " + ps.toString());
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

    private boolean updateTCRecordForApplNo(TransactionManager tmgr, MergeTradeCertDobj dobj) throws SQLException, VahanException {
        SessionVariables sessionVariables = new SessionVariables();
        PreparedStatement ps = null;
        try {
            String sql = "UPDATE " + TableList.VT_TRADE_CERTIFICATE
                    + " SET   no_of_vch = ?, appl_no = ?, cert_no = ? \n"
                    + " WHERE state_cd  = ?  AND off_cd  = ? "
                    + " AND   dealer_cd = ? AND vch_catg = ? "
                    + " AND   appl_no = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, dobj.getNoOfVeh());
            ps.setString(2, dobj.getNewMergeApplNo());
            ps.setString(3, dobj.getNewMergeCertNo());
            ps.setString(4, sessionVariables.getStateCodeSelected());
            ps.setInt(5, sessionVariables.getOffCodeSelected());
            ps.setString(6, dobj.getDealer());
            ps.setString(7, dobj.getVehCatg());
            ps.setString(8, dobj.getApplNo());
            LOGGER.info("updateTCRecordForApplNo sql :: " + ps.toString());
            int recordUpdated = ps.executeUpdate();
            return recordUpdated > 0 ? true : false;
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

    private boolean deleteTCRecordsForNonUpdatedApplNo(TransactionManager tmgr, List<MergeTradeCertDobj> tcDobjList, String updatedApplNo) throws SQLException {
        SessionVariables sessionVariables = new SessionVariables();
        PreparedStatement ps = null;
        String dealer = null;
        String vchCatg = null;
        StringBuilder stringOfApplNosToBeDeleted = new StringBuilder();
        try {
            List<MergeTradeCertDobj> tempList = new ArrayList();
            tempList.addAll(tcDobjList);
            for (MergeTradeCertDobj dobj : tempList) {
                if (updatedApplNo.equals(dobj.getApplNo())) {
                    tcDobjList.remove(dobj);
                    dealer = dobj.getDealer();
                    vchCatg = dobj.getVehCatg();
                    continue;
                }
                if (stringOfApplNosToBeDeleted.length() > 0) {
                    stringOfApplNosToBeDeleted.append(",");
                }
                stringOfApplNosToBeDeleted.append("'").append(dobj.getApplNo()).append("'");
            }
            String sql = "DELETE FROM " + TableList.VT_TRADE_CERTIFICATE
                    + " WHERE state_cd  = ?  AND off_cd  = ? "
                    + " AND   dealer_cd = ? AND vch_catg = ? "
                    + " AND   appl_no in (" + stringOfApplNosToBeDeleted + ") ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, sessionVariables.getStateCodeSelected());
            ps.setInt(2, sessionVariables.getOffCodeSelected());
            ps.setString(3, dealer);
            ps.setString(4, vchCatg);
            LOGGER.info("deleteTCRecordsForNonUpdatedApplNo sql :: " + ps.toString());
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

    private boolean saveHistoryRecordsInMergedTableForNewMergedApplNo(TransactionManager tmgr, MergeTradeCertDobj dobj) throws SQLException {
        SessionVariables sessionVariables = new SessionVariables();
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO " + TableList.VT_TRADE_CERTIFICATE_MERGE
                    + " SELECT ? as merge_appl_no, ? as status, \n"
                    + " state_cd, off_cd, appl_no, dealer_cd, sr_no, vch_catg, no_of_vch,? new_cert_no, cert_no,\n"
                    + " valid_from, valid_upto, issue_dt, no_of_vch_used, current_timestamp as moved_on, ? moved_by, \n"
                    + " applicant_type, fuel_cd, no_vch_print, remark, backlog_size, selected_duplicate_certificate \n"
                    + " FROM " + TableList.VT_TRADE_CERTIFICATE
                    + " WHERE state_cd  = ? AND off_cd   = ? "
                    + " AND   dealer_cd = ? AND vch_catg = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getNewMergeApplNo());
            ps.setString(2, "Admin_Dealer".equals(dobj.getDealerChoiceCondition()) ? "A_ID_Y_MERGE:DISPOSE" : "A_ID_N_MERGE:DISPOSE");
            ps.setString(3, "NOT_GENERATED");
            ps.setString(4, Util.getEmpCode());
            ps.setString(5, sessionVariables.getStateCodeSelected());
            ps.setInt(6, sessionVariables.getOffCodeSelected());
            ps.setString(7, dobj.getDealer());
            ps.setString(8, dobj.getVehCatg());
            LOGGER.info("INSERT INTO VT_TRADE_CERTIFICATE_MERGE sql :: " + ps.toString());
            int recordsSaved = ps.executeUpdate();
            if (recordsSaved > 0) {
                sql = "UPDATE " + TableList.VT_TRADE_CERTIFICATE_MERGE
                        + " SET status = ?, new_cert_no = ? "
                        + " WHERE state_cd  = ? AND off_cd   = ? "
                        + " AND   dealer_cd = ? AND vch_catg = ? "
                        + " AND   appl_no = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, "Admin_Dealer".equals(dobj.getDealerChoiceCondition()) ? "A_ID_Y_MERGE:NEW_CREATE" : "A_ID_N_MERGE:NEW_CREATE");
                ps.setString(2, dobj.getNewMergeCertNo());
                ps.setString(3, sessionVariables.getStateCodeSelected());
                ps.setInt(4, sessionVariables.getOffCodeSelected());
                ps.setString(5, dobj.getDealer());
                ps.setString(6, dobj.getVehCatg());
                ps.setString(7, dobj.getApplNo());
                LOGGER.info("UPDATE VT_TRADE_CERTIFICATE_MERGE sql :: " + ps.toString());
                int recordUpdated = ps.executeUpdate();
                return recordUpdated > 0 ? true : false;
            } else {
                return false;
            }
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

    public Boolean delete(List<MergeTradeCertDobj> tcDobjsList) throws VahanException {
        TransactionManager tmgr = null;
        Exception e = null;
        Set uniqueApplNoSet = new HashSet();
        try {
            tmgr = new TransactionManager("delete");
            for (MergeTradeCertDobj dobj : tcDobjsList) {
                if (uniqueApplNoSet.contains(dobj.getApplNo())) {
                    continue;
                }
                moveRecordsInHistoryForDealerAndVehCatgAndApplNo(tmgr, dobj);
                tcDobjsList.get(0).setNewMergeCertNo("NOT_GENERATED");
                saveHistoryRecordsInMergedTableForDisposedRecords(tmgr, dobj);
                deleteTCRecordsForDealerAndVehCatgAndApplNo(tmgr, dobj);
                uniqueApplNoSet.add(dobj.getApplNo());
            }
            tmgr.commit();
            return true;
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
            throw new VahanException("Error occured while disposing trade certificate records in database.");
        }
        return false;
    }

    private Boolean moveRecordsInHistoryForDealerAndVehCatgAndApplNo(TransactionManager tmgr, MergeTradeCertDobj dobj) throws SQLException {
        SessionVariables sessionVariables = new SessionVariables();
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO " + TableList.VH_TRADE_CERTIFICATE
                    + " SELECT state_cd, off_cd, appl_no, dealer_cd, sr_no, vch_catg, no_of_vch, cert_no,\n"
                    + " valid_from, valid_upto, issue_dt, no_of_vch_used, current_timestamp as moved_on, ? moved_by, \n"
                    + " applicant_type,fuel_cd,no_vch_print,?,backlog_size,selected_duplicate_certificate"
                    + " FROM " + TableList.VT_TRADE_CERTIFICATE
                    + " WHERE state_cd  = ? AND off_cd   = ? "
                    + " AND   dealer_cd = ? AND vch_catg = ? "
                    + " AND   appl_no   = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, "Admin_Dealer".equals(dobj.getDealerChoiceCondition()) ? "A_ID_Y_MERGE:DELETE" : "A_ID_N_MERGE:DELETE");
            ps.setString(3, sessionVariables.getStateCodeSelected());
            ps.setInt(4, sessionVariables.getOffCodeSelected());
            ps.setString(5, dobj.getDealer());
            ps.setString(6, dobj.getVehCatg());
            ps.setString(7, dobj.getApplNo());
            LOGGER.info("moveRecordsInHistoryForDealerAndVehCatgAndApplNo sql :: " + ps.toString());
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

    private boolean deleteTCRecordsForDealerAndVehCatgAndApplNo(TransactionManager tmgr, MergeTradeCertDobj dobj) throws SQLException {
        SessionVariables sessionVariables = new SessionVariables();
        PreparedStatement ps = null;
        try {
            String sql = "DELETE FROM " + TableList.VT_TRADE_CERTIFICATE
                    + " WHERE state_cd  = ?  AND off_cd  = ? "
                    + " AND   dealer_cd = ?  AND vch_catg = ? "
                    + " AND   appl_no = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, sessionVariables.getStateCodeSelected());
            ps.setInt(2, sessionVariables.getOffCodeSelected());
            ps.setString(3, dobj.getDealer());
            ps.setString(4, dobj.getVehCatg());
            ps.setString(5, dobj.getApplNo());
            LOGGER.info("deleteTCRecordsForDealerAndVehCatgAndApplNo sql :: " + ps.toString());
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

    private boolean saveHistoryRecordsInMergedTableForDisposedRecords(TransactionManager tmgr, MergeTradeCertDobj dobj) throws SQLException {
        SessionVariables sessionVariables = new SessionVariables();
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO " + TableList.VT_TRADE_CERTIFICATE_MERGE
                    + " SELECT ? as merge_appl_no, ? as status, \n"
                    + " state_cd, off_cd, appl_no, dealer_cd, sr_no, vch_catg, no_of_vch,? new_cert_no, cert_no,\n"
                    + " valid_from, valid_upto, issue_dt, no_of_vch_used, current_timestamp as op_dt, ? deal_cd, \n"
                    + " applicant_type, fuel_cd, no_vch_print, remark, backlog_size, selected_duplicate_certificate \n"
                    + " FROM " + TableList.VT_TRADE_CERTIFICATE
                    + " WHERE state_cd  = ? AND off_cd   = ? "
                    + " AND   dealer_cd = ? AND vch_catg = ? "
                    + " AND   appl_no   = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getApplNo());
            ps.setString(2, "Admin_Dealer".equals(dobj.getDealerChoiceCondition()) ? "A_ID_Y_MERGE:DELETE" : "A_ID_N_MERGE:DELETE");
            ps.setString(3, dobj.getNewMergeCertNo());
            ps.setString(4, Util.getEmpCode());
            ps.setString(5, sessionVariables.getStateCodeSelected());
            ps.setInt(6, sessionVariables.getOffCodeSelected());
            ps.setString(7, dobj.getDealer());
            ps.setString(8, dobj.getVehCatg());
            ps.setString(9, dobj.getApplNo());
            LOGGER.info("INSERT INTO VT_TRADE_CERTIFICATE_MERGE FOR DISPOSED RECORDS sql :: " + ps.toString());
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

    public boolean insertOldTcMappingDetailsInNewTc(TransactionManager tmgr, MergeTradeCertDobj dobj) throws SQLException {
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
            ps.setString(6, dobj.getNewMergeCertNo());
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
}
