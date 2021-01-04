/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.tradecert;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.tradecert.DeduplicateTradeCertDobj;
import nic.vahan.form.dobj.tradecert.IssueTradeCertDobj;
import nic.vahan.form.dobj.tradecert.MergeTradeCertDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
public class MergeDeDuplPrintImpl {

    private static final Logger LOGGER = Logger.getLogger(MergeDeDuplPrintImpl.class);

    public static List<IssueTradeCertDobj> fetchVhTradeCertRecords(Object dobjFromDobjsList, String mergeOrDeDupl, String action) throws VahanException {
        List<IssueTradeCertDobj> vhTradeCertRecordsList = null;
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        RowSet rs = null;
        Exception e = null;
        String dealer =((MergeTradeCertDobj) dobjFromDobjsList).getDealer();
        String vehCatg = ((MergeTradeCertDobj) dobjFromDobjsList).getVehCatg();
        String tcNo = null;
        String applNo = ((MergeTradeCertDobj) dobjFromDobjsList).getApplNo();
        Integer applSrNo = null;
        SessionVariables sessionVariables = new SessionVariables();
        if (dobjFromDobjsList instanceof DeduplicateTradeCertDobj) {
            tcNo = ((DeduplicateTradeCertDobj) dobjFromDobjsList).getCertNo();
            applSrNo = ((DeduplicateTradeCertDobj) dobjFromDobjsList).getSrNo();
        }
        String sql = " SELECT vh_trade_cert.appl_no, vh_trade_cert.sr_no, vm_vch_catg.catg, vm_vch_catg.catg_desc,  "
                + " vh_trade_cert.cert_no, vh_trade_cert.valid_upto, vh_trade_cert.remark "
                + " FROM " + TableList.VH_TRADE_CERTIFICATE + " vh_trade_cert "
                + " LEFT OUTER JOIN " + TableList.VM_VCH_CATG + " vm_vch_catg  ON vm_vch_catg.catg = vh_trade_cert.vch_catg "
                + " WHERE vh_trade_cert.state_cd = ?  AND vh_trade_cert.off_cd = ?  "
                + "   AND vh_trade_cert.dealer_cd = ? AND vh_trade_cert.vch_catg = ? "
                + "   AND vh_trade_cert.appl_no = ? ";
        if ("MERGE".equals(mergeOrDeDupl)) {
            if ("DELETE".equals(action)) {
                sql += "   AND vh_trade_cert.remark like '%MERGE:DELETE' ";
            } else {
                sql += "   AND vh_trade_cert.remark like '%MERGE:MERGE' ";
            }
        } else {
            sql += "   AND vh_trade_cert.cert_no = ?";
            if ("DELETE".equals(action)) {
                sql += "   AND vh_trade_cert.remark like '%DEDUPLICATE:DELETE' ";
            } else {
                sql += "   AND vh_trade_cert.remark like '%DEDUPLICATE:DEDUPL' ";
            }
        }
        int srNo = 1;
        try {
            tmgr = new TransactionManager("fetchVhTradeCertRecords");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, sessionVariables.getStateCodeSelected());
            pstmt.setInt(2, sessionVariables.getOffCodeSelected());
            pstmt.setString(3, dealer);
            pstmt.setString(4, vehCatg);
            pstmt.setString(5, applNo);
            if ("DEDUPL".equals(mergeOrDeDupl)) {
                pstmt.setString(6, tcNo);
            }
            LOGGER.info("fetchVhTradeCertRecords sql :: " + pstmt.toString());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (vhTradeCertRecordsList == null) {
                    vhTradeCertRecordsList = new ArrayList<>();
                }
                IssueTradeCertDobj dobj = new IssueTradeCertDobj();
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setSrNo(rs.getString("sr_no"));
                dobj.setVehCatgFor(rs.getString("catg"));
                dobj.setVehCatgName(rs.getString("catg_desc"));
                dobj.setTradeCertNo(rs.getString("cert_no"));
                dobj.setValidUpto(rs.getDate("valid_upto"));
                dobj.setRemark(rs.getString("remark"));
                vhTradeCertRecordsList.add(dobj);
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
            throw new VahanException("Error occured while fetching records from vh_trade_certificate.");
        }
        return vhTradeCertRecordsList;
    }

    public static List<IssueTradeCertDobj> fetchVtTradeCertRecords(Object dobjFromDobjsList, String mergeOrDeDupl, String action) throws VahanException {
        List<IssueTradeCertDobj> vtTradeCertRecordsList = null;
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        RowSet rs = null;
        Exception e = null;
        String dealer = null;
        String vehCatg = null;
        String tcNo = null;
        String applNo = null;
        Integer applSrNo = null;
        SessionVariables sessionVariables = new SessionVariables();
        if (dobjFromDobjsList instanceof MergeTradeCertDobj) {
            dealer = ((MergeTradeCertDobj) dobjFromDobjsList).getDealer();
        } else {
            dealer = ((DeduplicateTradeCertDobj) dobjFromDobjsList).getDealer();
            vehCatg = ((DeduplicateTradeCertDobj) dobjFromDobjsList).getVehCatg();
            tcNo = ((DeduplicateTradeCertDobj) dobjFromDobjsList).getCertNo();
            applNo = ((DeduplicateTradeCertDobj) dobjFromDobjsList).getApplNo();
            applSrNo = ((DeduplicateTradeCertDobj) dobjFromDobjsList).getSrNo();
        }
        String sql = " SELECT vt_trade_cert.appl_no, vt_trade_cert.sr_no, vm_vch_catg.catg, vm_vch_catg.catg_desc,  "
                + " vt_trade_cert.cert_no, vt_trade_cert.valid_upto, vt_trade_cert.no_of_vch "
                + " FROM " + TableList.VT_TRADE_CERTIFICATE + " vt_trade_cert "
                + " LEFT OUTER JOIN " + TableList.VM_VCH_CATG + " vm_vch_catg  ON vm_vch_catg.catg = vt_trade_cert.vch_catg "
                + " WHERE vt_trade_cert.state_cd = ?  AND vt_trade_cert.off_cd = ?  "
                + "   AND vt_trade_cert.dealer_cd = ? ";
        int srNo = 1;
        try {
            tmgr = new TransactionManager("fetchVtTradeCertRecords");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, sessionVariables.getStateCodeSelected());
            pstmt.setInt(2, sessionVariables.getOffCodeSelected());
            pstmt.setString(3, dealer);
            LOGGER.info("fetchVtTradeCertRecords sql :: " + pstmt.toString());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (vtTradeCertRecordsList == null) {
                    vtTradeCertRecordsList = new ArrayList<>();
                }
                IssueTradeCertDobj dobj = new IssueTradeCertDobj();
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setSrNo(rs.getString("sr_no"));
                dobj.setVehCatgFor(rs.getString("catg"));
                dobj.setVehCatgName(rs.getString("catg_desc"));
                dobj.setTradeCertNo(rs.getString("cert_no"));
                dobj.setValidUpto(rs.getDate("valid_upto"));
                dobj.setNoOfAllowedVehicles(rs.getString("no_of_vch"));
                vtTradeCertRecordsList.add(dobj);
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
            throw new VahanException("Error occured while fetching records from vt_trade_certificate.");
        }
        return vtTradeCertRecordsList;
    }
}
