/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.server.ServerUtil;
import nic.vahan.services.POSImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC105
 */
public class FeeDraftimpl {

    private static final Logger LOGGER = Logger.getLogger(FeeDraftimpl.class);

    public boolean saveDraftDetails(FeeDraftDobj draftDobj) {
        PreparedStatement pstmtDraft = null;
        PreparedStatement pstmtInsRcptMast = null;
        String whereiam = "FeeDraft_impl : saveDraftDetails()";
        long instrumentCd = 1;
        int instrumentSlNo = 1;
        boolean flag = false;
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager(whereiam);
            String instrumentRcptMapSql = "INSERT INTO vt_instrument_rcpt_mapping(state_cd, off_cd, rcpt_no, instrument_cd) VALUES (?, ?, ?, ?)";
            String save_draft_sql = "INSERT INTO vt_instruments("
                    + "             instrument_cd, sr_no, instrument_type, instrument_no, "
                    + "            instrument_dt, instrument_amt, bank_code, branch_name, received_dt, collected_by,state_cd, off_cd) "
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            instrumentCd = ServerUtil.getUniqueInstrumentNo(tmgr, Util.getUserStateCode());
            pstmtDraft = tmgr.prepareStatement(save_draft_sql);
            pstmtInsRcptMast = tmgr.prepareStatement(instrumentRcptMapSql);
            for (PaymentCollectionDobj draftPayment : draftDobj.getDraftPaymentList()) {
                pstmtDraft.setLong(1, instrumentCd);//draft cd
                pstmtDraft.setInt(2, instrumentSlNo);//sr_no
                pstmtDraft.setString(3, draftPayment.getInstrument());
                pstmtDraft.setString(4, draftPayment.getNumber());//draft_no
                pstmtDraft.setTimestamp(5, new Timestamp(new java.util.Date().getTime()));//dated to convert into timestamp
                pstmtDraft.setInt(6, Integer.parseInt(draftPayment.getAmount()));
                pstmtDraft.setString(7, draftPayment.getBank_name());//bank_code
                pstmtDraft.setString(8, draftPayment.getBranch().trim().toUpperCase());//branch_name
                pstmtDraft.setTimestamp(9, new Timestamp(new java.util.Date().getTime()));//receieved date
                pstmtDraft.setString(10, Util.getEmpCode());//collectedr by                 
                pstmtDraft.setString(11, draftDobj.getState_cd());//statecd
                pstmtDraft.setInt(12, Util.getUserOffCode());//off_cd                 
                pstmtDraft.addBatch();
                instrumentSlNo++;
            }

            //================insert in vt_instrument_rcpt_mapping===============
            pstmtInsRcptMast.setString(1, draftDobj.getState_cd());
            pstmtInsRcptMast.setInt(2, Util.getUserOffCode());
            pstmtInsRcptMast.setString(3, draftDobj.getRcpt_no());
            pstmtInsRcptMast.setLong(4, instrumentCd);
            if (pstmtDraft != null) {
                pstmtDraft.executeBatch();
                flag = true;
            }
            pstmtInsRcptMast.executeUpdate();
            tmgr.commit();

        } catch (SQLException sql) {
            flag = false;
            LOGGER.error(sql);

        } catch (Exception e) {
            flag = false;
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

        return flag;
    }

    public long saveDraftDetails(FeeDraftDobj draftDobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement pstmtDraft = null;
        PreparedStatement pstmtGetDraftCd = null;
        PreparedStatement pstmtInsRcptMast = null;
        String whereiam = "FeeDraft_impl : saveDraftDetails()";
        int draftCd = 0;
        long instrumentCd = 1;
        int instrumentSlNo = 1;
        boolean flag = false;
        instrumentCd = ServerUtil.getUniqueInstrumentNo(tmgr, Util.getUserStateCode());
        String save_draft_sql = "INSERT INTO vt_instruments("
                + "  instrument_cd, sr_no, instrument_type, instrument_no, "
                + "  instrument_dt, instrument_amt, bank_code, branch_name, received_dt, collected_by,state_cd, off_cd) "
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, current_timestamp , ?, ?, ?)";
        try {

            pstmtDraft = tmgr.prepareStatement(save_draft_sql);

            for (PaymentCollectionDobj draftPayment : draftDobj.getDraftPaymentList()) {
                String dupInstrument = FeeDraftimpl.chkDuplicateInstrumentNo(String.valueOf(draftPayment.getNumber()),
                        draftPayment.getBank_name(), draftPayment.getInstrument(), Util.getUserStateCode());
                if (dupInstrument != null) {
                    throw new VahanException(dupInstrument);
                }

                pstmtDraft.setLong(1, instrumentCd);//draft cd
                pstmtDraft.setInt(2, instrumentSlNo);//sr_no
                pstmtDraft.setString(3, draftPayment.getInstrument());
                pstmtDraft.setString(4, draftPayment.getNumber());//draft_no
                pstmtDraft.setDate(5, new java.sql.Date(draftPayment.getDated().getTime()));//dated to convert into timestamp
                pstmtDraft.setInt(6, Integer.parseInt(draftPayment.getAmount()));
                pstmtDraft.setString(7, draftPayment.getBank_name());//bank_code
                pstmtDraft.setString(8, draftPayment.getBranch().trim().toUpperCase());//branch_name
                //pstmtDraft.setTimestamp(9, new Timestamp(new java.util.Date().getTime()));//receieved date
                pstmtDraft.setString(9, Util.getEmpCode());//collectedr by                 
                pstmtDraft.setString(10, draftDobj.getState_cd());//statecd
                pstmtDraft.setInt(11, Util.getUserOffCode());//off_cd                 
                pstmtDraft.addBatch();
                instrumentSlNo++;

                if (draftPayment.getPosDobj() != null && draftPayment.getInstrument().equalsIgnoreCase(TableConstants.MPOS_INSTRUMENT_CODE)) {
                    POSImpl pOSImpl = new POSImpl();
                    if (draftDobj.getAppl_no() != null && draftDobj.getAppl_no().trim().length() > 0) {
                        draftPayment.getPosDobj().setApplNo(draftDobj.getAppl_no());
                    } else if (draftDobj.getRcpt_no() != null && draftDobj.getRcpt_no().trim().length() > 0) {
                        draftPayment.getPosDobj().setApplNo(draftDobj.getRcpt_no());
                    }
                    pOSImpl.insertIntoVtPOSResponse(tmgr, draftPayment.getPosDobj());
                }
            }
            if (pstmtDraft != null) {
                pstmtDraft.executeBatch();
                flag = true;
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Insertion of Instrument details failed");
        }
        return instrumentCd;
    }

    static public String chkDuplicateInstrumentNo(String instrument_no, String bank_code, String instrument_type, String stateCd) {
        String dupMessage = null;
        String sqlDup = "select b.appl_no "
                + " from vt_instruments a "
                + " left outer join vp_appl_rcpt_mapping b on b.instrument_cd=a.instrument_cd "
                + " where a.instrument_no=? and bank_code=? and instrument_type=?";

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("chkDuplicateInstrumentNo");
            ps = tmgr.prepareStatement(sqlDup);
            ps.setString(1, instrument_no);
            ps.setString(2, bank_code);
            ps.setString(3, instrument_type);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dupMessage = "Instrument No is already used against Application Number: " + rs.getString("appl_no") + " for the same bank.";
            } else {
                if (bank_code != null && bank_code.equals(TableConstants.EGRASS_BANK_CODE)) {
                    String sql = "Select * from " + TableList.VP_PGI_DETAILS
                            + " where  treasury_ref_no = ? and left(transaction_id,2)=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, instrument_no);
                    ps.setString(2, stateCd);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        dupMessage = "Instrument No is already used against Transaction Id: " + rs.getString("transaction_id");
                    }
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


        return dupMessage;
    }

    static public boolean chkDuplicateInstrumentNoFromVpInstrumentcart(String instrument_no, String bank_code, String instrument_type) {
        boolean dupInstrument = false;
        String sqlDup = "Select * from vp_instrument_cart  where instrument_no=? and bank_code=? and instrument_type=?";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("chkDuplicateInstrumentNoFromVpInstrumentcart");
            PreparedStatement ps = tmgr.prepareStatement(sqlDup);
            ps.setString(1, instrument_no);
            ps.setString(2, bank_code);
            ps.setString(3, instrument_type);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dupInstrument = true;
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


        return dupInstrument;
    }
}
