/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.audit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.DocumentType;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.AuditRecoveryDobj;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.impl.FeeDraftimpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Receipt_Master_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author Kunal Maiti
 */
public class EntryAuditRecoveryImpl {

    private static final Logger LOGGER = Logger.getLogger(EntryAuditRecoveryImpl.class);

    //    Showing initial details from vt_owner
    public OwnerDetailsDobj getOwnerDetails(String regn_no) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        OwnerDetailsDobj dobj = null;
        VahanException vahanexecption = null;
        try {
            tmgr = new TransactionManager("ALT_Impl");
            ps = tmgr.prepareStatement("SELECT * FROM " + TableList.VIEW_VV_OWNER + " where regn_no=?");

            ps.setString(1, regn_no);
//            ps.setString(2, stateCode);
//            ps.setInt(3, officeCode);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                dobj = new OwnerDetailsDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setState_name(rs.getString("state_name"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setOff_name(rs.getString("off_name"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setRegn_dt(rs.getString("regn_dt"));
                dobj.setRegnDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_dt())));
                dobj.setPurchase_dt(rs.getString("purchase_dt"));
                dobj.setPurchaseDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getPurchase_dt())));
                dobj.setOwner_sr(rs.getInt("owner_sr"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_add3(rs.getString("c_add3"));
                dobj.setC_district(rs.getInt("c_district"));
                dobj.setC_district_name(rs.getString("c_district_name"));
                dobj.setC_state(rs.getString("c_state"));
                dobj.setC_state_name(rs.getString("c_state_name"));
                dobj.setC_pincode(rs.getInt("c_pincode"));
                dobj.setP_add1(rs.getString("p_add1"));
                dobj.setP_add2(rs.getString("p_add2"));
                dobj.setP_add3(rs.getString("p_add3"));
                dobj.setP_district(rs.getInt("p_district"));
                dobj.setP_district_name(rs.getString("p_district_name"));
                dobj.setP_state(rs.getString("p_state"));
                dobj.setP_state_name(rs.getString("p_state_name"));
                dobj.setP_pincode(rs.getInt("p_pincode"));
                dobj.setOwner_cd(rs.getInt("owner_cd"));

                dobj.setVh_class(rs.getInt("vh_class"));
                dobj.setVh_class_desc(rs.getString("vh_class_desc"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setEng_no(rs.getString("eng_no"));

                tmgr = new TransactionManager("Owner_Id");
                String sql = "SELECT * FROM " + TableList.VT_OWNER_IDENTIFICATION
                        + " where regn_no =? ";
                PreparedStatement psOwner = tmgr.prepareStatement(sql);
                psOwner.setString(1, regn_no);

                rs = tmgr.fetchDetachedRowSet();
                OwnerIdentificationDobj own_identity = new OwnerImpl().fillOwnerIdentityDobj(rs);
                if (own_identity != null) {
                    dobj.setOwnerIdentity(own_identity);
                }

            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanexecption = new VahanException("Database Error in fetching details for [ " + regn_no + "]");
            throw vahanexecption;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanexecption = new VahanException("Error in fetching details for [ " + regn_no + "]");
            throw vahanexecption;
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

    public static String saveFeePaymentDtls(AuditRecoveryDobj dobj, int payAmt, int pur_cd, String pay_mode, FeeDraftDobj draftDobj) {
        String returnval = "false";
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String rcptNo = null;
        String empCode = Util.getEmpCode();
        Date currentdate = new Date();
        long instrumentCd = 0;
        FeeDraftimpl feeDraftimpl = new FeeDraftimpl();
        try {
            tmgr = new TransactionManager("saveFeePaymentDtls");
            rcptNo = Receipt_Master_Impl.generateNewRcptNo(dobj.getOff_cd(), tmgr);

            updateAuditRecoveryDtls(tmgr, empCode, dobj, payAmt, rcptNo);

            String sql_Account = "INSERT INTO " + TableList.VT_FEE + " (regn_no, payment_mode, fees, fine, rcpt_no, rcpt_dt, pur_cd, collected_by, state_cd, off_cd) VALUES (?, ?, ?,  ?, ? , current_timestamp, ?, ?, ?,?)";

            ps = tmgr.prepareStatement(sql_Account);
            int i = 1;
            ps.setString(i++, dobj.getRegn_no());
            ps.setString(i++, pay_mode);
            ps.setInt(i++, dobj.getAmount());
            ps.setInt(i++, 0);
            ps.setString(i++, rcptNo);
            ps.setInt(i++, pur_cd);
            ps.setString(i++, empCode);
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getUserOffCode());

            ps.executeUpdate();
            if (draftDobj != null) {
                instrumentCd = feeDraftimpl.saveDraftDetails(draftDobj, tmgr);
                payAmt = 0;
            }
            EntryAuditRecoveryImpl entryAuditRecoveryImpl = new EntryAuditRecoveryImpl();
            OwnerDetailsDobj owndobj = entryAuditRecoveryImpl.getOwnerDetails(dobj.getRegn_no());
            entryAuditRecoveryImpl.saveRecptInstMap(instrumentCd, dobj.getApplNo(), rcptNo, 0, payAmt, owndobj, tmgr, "");
            ServerUtil.insertForQRDetails(dobj.getApplNo(), null, null, rcptNo, false, DocumentType.RECEIPT_QR, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tmgr);
            tmgr.commit();
            returnval = rcptNo;
        } catch (SQLException e) {
            returnval = "false";
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (VahanException ex) {
            java.util.logging.Logger.getLogger(EntryAuditRecoveryImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return returnval;
    }

    public static void updateAuditRecoveryDtls(TransactionManager tmgr, String empCode, AuditRecoveryDobj dobj, long payAmt, String rcptNo) throws SQLException {
        String sql = "UPDATE " + TableList.VA_AUDIT_RECOVERY + " SET PAID_DEAL_CD = ?, pay_dt = ?, PAY_AMT = ?, RCPT_NO = ?, PAID_BY = ?, PAID_OP_DATE = ? "
                + "  WHERE REGN_NO =? AND para_no=? and para_year=? and audit_ty=?";

        PreparedStatement ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, empCode);
        ps.setDate(i++, new java.sql.Date(dobj.getPay_dt().getTime()));
        ps.setLong(i++, payAmt);
        ps.setString(i++, rcptNo);
        ps.setString(i++, empCode);
        ps.setDate(i++, new java.sql.Date(new Date().getTime()));
        ps.setString(i++, dobj.getRegn_no());
        ps.setString(i++, dobj.getPara_no());
        ps.setString(i++, dobj.getPara_year());
        ps.setString(i++, dobj.getAudit_ty());

        ps.executeUpdate();

    }

    public void saveRecptInstMap(Long instNo, String applno, String rcptno, int excessAmt, int cashAmt, OwnerDetailsDobj owndobj, TransactionManager tmgr, String remarks) throws SQLException {
        String sql = "INSERT INTO " + TableList.VP_APPL_RCPT_MAPPING
                + "( state_cd, off_cd, appl_no, rcpt_no, owner_name, chasi_no, vh_class, \n"
                + " instrument_cd, excess_amt, cash_amt,remarks)"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?,?, ?, ?,?)";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
        ps.setString(i++, applno);
        ps.setString(i++, rcptno);
        ps.setString(i++, owndobj.getOwner_name());
        ps.setString(i++, owndobj.getChasi_no() == null ? "" : owndobj.getChasi_no());
        ps.setInt(i++, owndobj.getVh_class());
        if (instNo == null) {
            ps.setNull(i++, java.sql.Types.INTEGER);
        } else {
            ps.setLong(i++, instNo);
        }

        ps.setLong(i++, excessAmt);
        ps.setLong(i++, cashAmt);
        if (remarks != null && !remarks.equals("")) {
            ps.setString(i++, remarks.toUpperCase());
        } else {
            ps.setString(i++, null);
        }
        ps.executeUpdate();

    }

    public static String saveAuditRecoveryDtls(AuditRecoveryDobj dobj) {
        String returnval = "false";
        TransactionManager tmgr = null;
        PreparedStatement ps = null;

        try {
            tmgr = new TransactionManager("saveActionRecord");
            String appleNo = ServerUtil.getUniqueApplNo(tmgr, dobj.getState_cd());
            String sql = "INSERT INTO " + TableList.VA_AUDIT_RECOVERY + "(regn_no, amount, para_no, para_year, objection, from_dt, \n"
                    + "            to_dt, deal_cd, op_date,audit_ty,state_cd, off_cd,appl_no)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?, ?,?);";

            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, dobj.getRegn_no());
            ps.setInt(i++, dobj.getAmount());
            ps.setString(i++, dobj.getPara_no());
            ps.setString(i++, dobj.getPara_year());
            ps.setString(i++, dobj.getObjection());
            ps.setDate(i++, new java.sql.Date(dobj.getFrom_dt().getTime()));
            ps.setDate(i++, new java.sql.Date(dobj.getTo_dt().getTime()));
            ps.setString(i++, dobj.getDeal_cd());
            ps.setDate(i++, new java.sql.Date(dobj.getOp_date().getTime()));
            ps.setString(i++, dobj.getAudit_ty());;
            ps.setString(i++, dobj.getState_cd());
            ps.setInt(i++, dobj.getOff_cd());
            ps.setString(i++, appleNo);

            ps.executeUpdate();

            tmgr.commit();
            returnval = "true";
        } catch (SQLException e) {
            returnval = "false";
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (VahanException ex) {
            java.util.logging.Logger.getLogger(EntryAuditRecoveryImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return returnval;
    }

    public static int saveReconciliationDtls(AuditRecoveryDobj dobj, AuditRecoveryDobj auditRecoveryDobj) {
        int val = 0;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("saveActionRecord");
            String sql = "INSERT INTO " + TableList.VH_AUDIT_RECOVERY_RECONCIL + "(SL_NO, REGN_NO, AMOUNT, PARA_NO, PARA_YEAR, OBJECTION, FROM_DT, TO_DT, DEAL_CD, OP_DATE, PAID_DEAL_CD, paid_op_date, RCPT_NO, AUDIT_TY, PAID_BY, PAY_AMT, PAY_DT, RECONCIL_FLAG, RECONCIL_REASON, STATE_CD, off_cd,appl_no)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?,?,?,?,?,?, \n"
                    + "            ?, ?, ?, ?, ?, ?,?,?,?,?,? );";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setInt(i++, dobj.getSl_no());
            ps.setString(i++, dobj.getRegn_no());
            ps.setInt(i++, dobj.getAmount());
            ps.setString(i++, dobj.getPara_no());
            ps.setString(i++, dobj.getPara_year());
            ps.setString(i++, dobj.getObjection());
            if (dobj.getFrom_dt() != null) {
                ps.setDate(i++, new java.sql.Date(dobj.getFrom_dt().getTime()));
            } else {
                ps.setDate(i++, null);
            }
            if (dobj.getTo_dt() != null) {
                ps.setDate(i++, new java.sql.Date(dobj.getTo_dt().getTime()));
            } else {
                ps.setDate(i++, null);
            }
            ps.setString(i++, dobj.getDeal_cd());
            if (auditRecoveryDobj.getOp_date() != null) {
                ps.setDate(i++, new java.sql.Date(auditRecoveryDobj.getOp_date().getTime()));
            } else {
                ps.setDate(i++, null);
            }
            ps.setString(i++, dobj.getPaid_deal_cd());
            if (dobj.getPaid_op_date() != null) {
                ps.setDate(i++, new java.sql.Date(dobj.getPaid_op_date().getTime()));
            } else {
                ps.setDate(i++, null);
            }
            ps.setString(i++, dobj.getRcpt_no());
            ps.setString(i++, dobj.getAudit_ty());
            ps.setString(i++, dobj.getPaid_by());
            ps.setInt(i++, dobj.getPay_amt());
            if (dobj.getPay_dt() != null) {
                ps.setDate(i++, new java.sql.Date(dobj.getPay_dt().getTime()));
            } else {
                ps.setDate(i++, null);
            }
            ps.setString(i++, "R");
            ps.setString(i++, auditRecoveryDobj.getObjection());
            ps.setString(i++, dobj.getState_cd());
            ps.setInt(i++, dobj.getOff_cd());
            ps.setString(i++, dobj.getApplNo());

            ps.executeUpdate();

            String updateSql = "Update " + TableList.VA_AUDIT_RECOVERY + " set amount = ?, from_dt = ?, to_dt=?, OP_DATE=?, OBJECTION=?, RECONCIL_FLAG='R', DEAL_CD=?   where regn_no = ? AND para_no = ? AND audit_ty=? and para_year=? ";
            ps = tmgr.prepareStatement(updateSql);
            i = 1;
            ps.setInt(i++, auditRecoveryDobj.getAmount());
            ps.setDate(i++, new java.sql.Date(auditRecoveryDobj.getFrom_dt().getTime()));
            ps.setDate(i++, new java.sql.Date(auditRecoveryDobj.getTo_dt().getTime()));
            ps.setDate(i++, new java.sql.Date(auditRecoveryDobj.getOp_date().getTime()));
            ps.setString(i++, auditRecoveryDobj.getObjection());
            ps.setString(i++, auditRecoveryDobj.getDeal_cd());
            ps.setString(i++, dobj.getRegn_no());
            ps.setString(i++, dobj.getPara_no());
            ps.setString(i++, dobj.getAudit_ty());
            ps.setString(i++, dobj.getPara_year());

            val = ps.executeUpdate();

            tmgr.commit();
        } catch (SQLException e) {
            val = 0;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return val;
    }

    public static int dropReconciliationDtls(AuditRecoveryDobj dobj, AuditRecoveryDobj dobj1) {
        int val = 0;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("saveActionRecord");
            String sql = "INSERT INTO " + TableList.VH_AUDIT_RECOVERY_RECONCIL + "(SL_NO, REGN_NO, AMOUNT, PARA_NO, PARA_YEAR, OBJECTION, FROM_DT, TO_DT, DEAL_CD, OP_DATE, PAID_DEAL_CD, paid_op_date, RCPT_NO, AUDIT_TY, PAID_BY, PAY_AMT, PAY_DT, RECONCIL_FLAG, RECONCIL_REASON, STATE_CD, off_cd,appl_no) "
                    + "SELECT SL_NO, REGN_NO, AMOUNT, PARA_NO, PARA_YEAR, OBJECTION, FROM_DT, TO_DT, DEAL_CD, OP_DATE, PAID_DEAL_CD, PAID_OP_DATE, RCPT_NO, AUDIT_TY, PAID_BY, PAY_AMT, PAY_DT, 'D', ?, STATE_CD, off_cd, appl_no FROM " + TableList.VA_AUDIT_RECOVERY + " WHERE AUDIT_TY = ? AND PARA_NO=? AND PARA_YEAR=? AND REGN_NO=?";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, dobj1.getObjection());
            ps.setString(i++, dobj.getAudit_ty());
            ps.setString(i++, dobj.getPara_no());
            ps.setString(i++, dobj.getPara_year());
            ps.setString(i++, dobj.getRegn_no());

            ps.executeUpdate();

            String updateSql = "delete from " + TableList.VA_AUDIT_RECOVERY + " where regn_no = ? AND para_no = ? AND audit_ty=? and para_year=? ";
            ps = tmgr.prepareStatement(updateSql);

            ps.setString(1, dobj.getRegn_no());
            ps.setString(2, dobj.getPara_no());
            ps.setString(3, dobj.getAudit_ty());
            ps.setString(4, dobj.getPara_year());
            ps.executeUpdate();

            tmgr.commit();
            val = 1;
        } catch (SQLException e) {
            val = 0;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return val;
    }

    public static int stayReconciliationDtls(AuditRecoveryDobj dobj, AuditRecoveryDobj dobj1) {
        int val = 0;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("saveActionRecord");
            String sql = "INSERT INTO " + TableList.VH_AUDIT_RECOVERY_RECONCIL + "(SL_NO, REGN_NO, AMOUNT, PARA_NO, PARA_YEAR, OBJECTION, FROM_DT, TO_DT, DEAL_CD, OP_DATE, PAID_DEAL_CD, paid_op_date, RCPT_NO, AUDIT_TY, PAID_BY, PAY_AMT, PAY_DT, RECONCIL_FLAG, RECONCIL_REASON, STATE_CD, off_cd,appl_no) "
                    + "SELECT SL_NO, REGN_NO, AMOUNT, PARA_NO, PARA_YEAR, OBJECTION, FROM_DT, TO_DT, DEAL_CD, OP_DATE, PAID_DEAL_CD, PAID_OP_DATE, RCPT_NO, AUDIT_TY, PAID_BY, PAY_AMT, PAY_DT, 'S', ?, STATE_CD, off_cd,appl_no FROM " + TableList.VA_AUDIT_RECOVERY + " WHERE AUDIT_TY = ? AND PARA_NO=? AND PARA_YEAR=? AND REGN_NO=?";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, dobj1.getObjection());
            ps.setString(i++, dobj.getAudit_ty());
            ps.setString(i++, dobj.getPara_no());
            ps.setString(i++, dobj.getPara_year());
            ps.setString(i++, dobj.getRegn_no());

            ps.executeUpdate();

            String updateSql = "Update " + TableList.VA_AUDIT_RECOVERY + " set OP_DATE=?, RECONCIL_FLAG='S', DEAL_CD=?  where regn_no = ? AND para_no = ? AND audit_ty=? and para_year=? ";
            ps = tmgr.prepareStatement(updateSql);
//            ps.setInt(1, dobj1.getAmount());
//            ps.setDate(2,null);
//            ps.setDate(3,null);
            ps.setDate(1, new java.sql.Date(dobj1.getOp_date().getTime()));
            ps.setString(2, dobj1.getDeal_cd());
            ps.setString(3, dobj.getRegn_no());
            ps.setString(4, dobj.getPara_no());
            ps.setString(5, dobj.getAudit_ty());
            ps.setString(6, dobj.getPara_year());
            val = ps.executeUpdate();
            tmgr.commit();
        } catch (SQLException e) {
            val = 0;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return val;
    }

    public AuditRecoveryDobj getvtAuditRecovery(String auditType, String paraNo, String year, String regn_no) {
        AuditRecoveryDobj auditRecoveryDobj = new AuditRecoveryDobj();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;

        VahanException vahanexecption = null;
        try {
            tmgr = new TransactionManager("ALT_Impl");
            ps = tmgr.prepareStatement("SELECT * FROM " + TableList.VA_AUDIT_RECOVERY + " where audit_ty=? and para_no = ? and para_year = ? and regn_no = ?");

            ps.setString(1, auditType);
            ps.setString(2, paraNo);
            ps.setString(3, year);
            ps.setString(4, regn_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {

                auditRecoveryDobj.setRegn_no(rs.getString("regn_no"));
                auditRecoveryDobj.setAmount(rs.getInt("amount"));
                auditRecoveryDobj.setPara_no(rs.getString("para_no"));
                auditRecoveryDobj.setPara_year(rs.getString("para_year"));
                auditRecoveryDobj.setObjection(rs.getString("objection"));
                auditRecoveryDobj.setFrom_dt(rs.getDate("from_dt"));
                auditRecoveryDobj.setTo_dt(rs.getDate("to_dt"));
                auditRecoveryDobj.setDeal_cd(rs.getString("deal_cd"));
                auditRecoveryDobj.setOp_date(rs.getDate("op_date"));
                auditRecoveryDobj.setPaid_deal_cd(rs.getString("paid_deal_cd"));
                auditRecoveryDobj.setPaid_op_date(rs.getDate("paid_op_date"));
                auditRecoveryDobj.setRcpt_no(rs.getString("rcpt_no"));
                auditRecoveryDobj.setAudit_ty(rs.getString("audit_ty"));
                auditRecoveryDobj.setPaid_by(rs.getString("paid_by"));
                auditRecoveryDobj.setPay_amt(rs.getInt("pay_amt"));
                auditRecoveryDobj.setPay_dt(rs.getDate("pay_dt"));
                auditRecoveryDobj.setReconcil_flag(rs.getString("reconcil_flag"));
                auditRecoveryDobj.setState_cd(rs.getString("state_cd"));
                auditRecoveryDobj.setOff_cd(rs.getInt("off_cd"));
                auditRecoveryDobj.setApplNo(rs.getString("appl_no"));
                auditRecoveryDobj.setSl_no(rs.getInt("sl_no"));

            }

        } catch (SQLException e) {
            auditRecoveryDobj = new AuditRecoveryDobj();
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
        return auditRecoveryDobj;
    }

    public List<AuditRecoveryDobj> getvtAuditRecovery(String regn_no) {
        List<AuditRecoveryDobj> auditRecoveryList = new ArrayList<>();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;

        VahanException vahanexecption = null;
        try {
            tmgr = new TransactionManager("ALT_Impl");
            ps = tmgr.prepareStatement("SELECT * FROM " + TableList.VA_AUDIT_RECOVERY + " where  regn_no = ? order by sl_no");

            ps.setString(1, regn_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                if (!"".equals(rs.getString("rcpt_no"))) {
                } else {
                    AuditRecoveryDobj auditRecoveryDobj = new AuditRecoveryDobj();
                    auditRecoveryDobj.setSl_no(rs.getInt("sl_no"));
                    auditRecoveryDobj.setRegn_no(rs.getString("regn_no"));
                    auditRecoveryDobj.setAmount(rs.getInt("amount"));
                    auditRecoveryDobj.setPara_no(rs.getString("para_no"));
                    auditRecoveryDobj.setPara_year(rs.getString("para_year"));
                    auditRecoveryDobj.setObjection(rs.getString("objection"));
                    auditRecoveryDobj.setFrom_dt(rs.getDate("from_dt"));
                    auditRecoveryDobj.setTo_dt(rs.getDate("to_dt"));
                    auditRecoveryDobj.setDeal_cd(rs.getString("deal_cd"));
                    auditRecoveryDobj.setOp_date(rs.getDate("op_date"));
                    auditRecoveryDobj.setPaid_deal_cd(rs.getString("paid_deal_cd"));
                    auditRecoveryDobj.setPaid_op_date(rs.getDate("paid_op_date"));
                    auditRecoveryDobj.setRcpt_no(rs.getString("rcpt_no"));
                    auditRecoveryDobj.setAudit_ty(rs.getString("audit_ty"));
                    if (rs.getString("audit_ty").equalsIgnoreCase("1")) {
                        auditRecoveryDobj.setAudit_tyDesc("AG Audit");
                    } else if (rs.getString("audit_ty").equalsIgnoreCase("2")) {
                        auditRecoveryDobj.setAudit_tyDesc("Internal Audit");
                    } else {
                        auditRecoveryDobj.setAudit_tyDesc(rs.getString("audit_ty"));
                    }
                    auditRecoveryDobj.setPaid_by(rs.getString("paid_by"));
                    auditRecoveryDobj.setPay_amt(rs.getInt("pay_amt"));
                    auditRecoveryDobj.setPay_dt(rs.getDate("pay_dt"));
                    auditRecoveryDobj.setReconcil_flag(rs.getString("reconcil_flag"));
                    auditRecoveryDobj.setState_cd(rs.getString("state_cd"));
                    auditRecoveryDobj.setOff_cd(rs.getInt("off_cd"));
                    auditRecoveryDobj.setApplNo(rs.getString("appl_no"));

                    auditRecoveryList.add(auditRecoveryDobj);
                }
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
        return auditRecoveryList;
    }

    //insertFromVA_TOVT_AUDIT_RECOVERY
    public static boolean insertFromVA_TOVT_AUDIT_RECOVERY(AuditRecoveryDobj dobj) {
        TransactionManager tmgr = null;
        boolean flag = false;
        try {
            tmgr = new TransactionManager("insertFromVA_TOVT_AUDIT_RECOVERY");
            int j = insertFromVA_TOVT_AUDIT_RECOVERY(tmgr, dobj);
            if (j > 0) {
                tmgr.commit();
                flag = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public static int insertFromVA_TOVT_AUDIT_RECOVERY(TransactionManager tmgr, AuditRecoveryDobj dobj) {
        boolean flag = false;
        PreparedStatement ps = null;
        String empCode = Util.getEmpCode();
        Date currentdate = new Date();
        int j = 0;
        try {
            String sql = "INSERT INTO " + TableList.VT_AUDIT_RECOVERY + "(\n"
                    + "            sl_no, regn_no, amount, para_no, para_year, objection, from_dt, \n"
                    + "            to_dt, deal_cd, op_date, paid_deal_cd, paid_op_date, rcpt_no, \n"
                    + "            audit_ty, paid_by, pay_amt, pay_dt, reconcil_flag, state_cd, \n"
                    + "            off_cd, appl_no)"
                    + "SELECT sl_no, regn_no, amount, para_no, para_year, objection, from_dt, \n"
                    + "       to_dt, deal_cd, op_date, paid_deal_cd, paid_op_date, rcpt_no, \n"
                    + "       audit_ty, paid_by, pay_amt, pay_dt, reconcil_flag, state_cd, \n"
                    + "       off_cd, appl_no\n"
                    + "  FROM " + TableList.VA_AUDIT_RECOVERY + " WHERE REGN_NO =? AND para_no=? and para_year=? and audit_ty=?";

            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, dobj.getRegn_no());
            ps.setString(i++, dobj.getPara_no());
            ps.setString(i++, dobj.getPara_year());
            ps.setString(i++, dobj.getAudit_ty());
            int k = ps.executeUpdate();
            if (k > 0) {
                String dltSql = "delete from " + TableList.VA_AUDIT_RECOVERY + " where regn_no = ? AND para_no = ? AND audit_ty=? and para_year=? ";
                ps = tmgr.prepareStatement(dltSql);
                ps.setString(1, dobj.getRegn_no());
                ps.setString(2, dobj.getPara_no());
                ps.setString(3, dobj.getAudit_ty());
                ps.setString(4, dobj.getPara_year());
                j = ps.executeUpdate();
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return j;
    }
}
