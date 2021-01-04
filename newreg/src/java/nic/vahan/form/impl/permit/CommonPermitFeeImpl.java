/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.dobj.permit.CommonPermitFeeDobj;
import nic.vahan.form.dobj.permit.PermitShowFeeDetailDobj;
import nic.vahan.form.impl.Receipt_Master_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;

/**
 *
 * @author Administrator
 */
public class CommonPermitFeeImpl {

    static class PaymentGenInfo {

        private long cashAmt = 0;
        private long draftAmt = 0;
        private long totalAmt = 0;
        private long excessAmt = 0;

        public long getCashAmt() {
            return cashAmt;
        }

        public void setCashAmt(long cashAmt) {
            this.cashAmt = cashAmt;
        }

        public long getDraftAmt() {
            return draftAmt;
        }

        public void setDraftAmt(long draftAmt) {
            this.draftAmt = draftAmt;
        }

        public long getTotalAmt() {
            return totalAmt;
        }

        public void setTotalAmt(long totalAmt) {
            this.totalAmt = totalAmt;
        }

        public long getExcessAmt() {
            return excessAmt;
        }

        public void setExcessAmt(long excessAmt) {
            this.excessAmt = excessAmt;
        }
    }

    public String createRecpt_no(TransactionManager tmgr) throws VahanException {
        String rcpt_no = Receipt_Master_Impl.generateNewRcptNo(Util.getUserOffCode(), tmgr);
        return rcpt_no;
    }

    public void insert_into_vt_fee(CommonPermitFeeDobj dobj, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps;
        String Query;
        for (PermitShowFeeDetailDobj feeShowPanal : dobj.getListPmtFeeDetails()) {
            Query = "INSERT INTO " + TableList.VT_FEE + "(\n"
                    + "            regn_no, payment_mode, fees, fine, rcpt_no, rcpt_dt, pur_cd, \n"
                    + "            flag, collected_by, state_cd, off_cd)\n"
                    + "    VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, \n"
                    + "            ?, ?, ?, ?)";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, dobj.getRegn_no());
            ps.setString(i++, dobj.getPayment_mode());
            ps.setInt(i++, Integer.parseInt(feeShowPanal.getPermitAmt()));
            ps.setInt(i++, Integer.parseInt(feeShowPanal.getPenalty()));
            ps.setString(i++, dobj.getRcpt_no());
            ps.setInt(i++, Integer.parseInt(feeShowPanal.getPurCd()));
            ps.setString(i++, dobj.getFlag());
            ps.setString(i++, dobj.getCollected_by());
            ps.setString(i++, dobj.getState_cd());
            ps.setInt(i++, Integer.parseInt(dobj.getOff_cd()));
            ps.executeUpdate();
        }
    }

    public PaymentGenInfo getPaymentInfo(CommonPermitFeeDobj feeDobj, FeeDraftDobj feeDraftDobj) {
        PaymentGenInfo payInfo = new PaymentGenInfo();
        long amtTotal = 0;
        long amtDraft = 0;
        long amtCash = 0;
        long amtExcess = 0;
        for (PermitShowFeeDetailDobj dobj : feeDobj.getListPmtFeeDetails()) {
            amtTotal = amtTotal + (Long.valueOf(dobj.getPermitAmt()) + Long.valueOf(dobj.getPenalty()));
        }
        if (feeDraftDobj != null) {
            for (PaymentCollectionDobj draftPayment : feeDraftDobj.getDraftPaymentList()) {
                amtDraft = amtDraft + Long.parseLong(draftPayment.getAmount());
            }
        }

        if (amtDraft > amtTotal) {
            amtExcess = amtDraft - amtTotal;
            amtCash = 0;
        } else {
            amtExcess = 0;
            amtCash = amtTotal - amtDraft;
        }

        payInfo.setCashAmt(amtCash);
        payInfo.setDraftAmt(amtDraft);
        payInfo.setExcessAmt(amtExcess);
        payInfo.setTotalAmt(amtTotal);

        return payInfo;
    }

    public void saveRecptInstMap(Long instNo, String applno, String rcptno, PaymentGenInfo payInfo, CommonPermitFeeDobj dobj, TransactionManager tmgr) throws SQLException {
        String sql = "INSERT INTO " + TableList.VP_APPL_RCPT_MAPPING
                + "( state_cd, off_cd, appl_no, rcpt_no, owner_name, chasi_no, vh_class, \n"
                + " instrument_cd, excess_amt, cash_amt,remarks)"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?)";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
        ps.setString(i++, applno);
        ps.setString(i++, rcptno);
        ps.setString(i++, dobj.getOwner_name());
        if (CommonUtils.isNullOrBlank(dobj.getChasi_no())) {
            ps.setString(i++, "NA");
        } else {
            ps.setString(i++, dobj.getChasi_no());
        }
        ps.setInt(i++, Integer.valueOf(dobj.getVh_class()));
        ps.setLong(i++, instNo);
        ps.setLong(i++, payInfo.getExcessAmt());
        ps.setLong(i++, payInfo.getCashAmt());
        ps.setString(i++, null);
        ps.executeUpdate();
    }
}
