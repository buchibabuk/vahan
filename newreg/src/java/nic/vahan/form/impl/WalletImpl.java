/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.WalletDobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

public class WalletImpl implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(WalletImpl.class);

    public List<WalletDobj> getWalletDataPerTransactionNo(TransactionManager tmgr, String transactionNo, String dealerCd, String userId, String stateCd, int offCd) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        List<WalletDobj> dobjList = new ArrayList<WalletDobj>();
        try {
            sql = "select f.state_cd, f.off_cd , fbp.pur_cd , fbp.ttlamount,fbp.ttlsurcharge,fbp.ttlpenalty,fbp.ttlinterest,fbp.ttlexempted,fbp.ttlrebate,fbp.dealer_cd,fbp.ttltax1,fbp.ttltax2\n"
                    + " from vph_rcpt_cart_fail  f\n"
                    + " inner join vph_rcpt_cart_fail_breakup fbp on  f.transaction_no = fbp.transaction_no and f.state_cd = fbp.state_cd and f.off_cd = fbp.off_cd \n"
                    + " where f.user_cd = ? and f.state_cd = ? and f.off_cd = ? and f.transaction_no = ? and fbp.dealer_cd = ? \n"
                    + " group by f.state_cd,f.off_cd,fbp.ttlamount,fbp.ttlsurcharge,fbp.ttlpenalty,fbp.ttlinterest,fbp.ttlexempted,fbp.ttlrebate,fbp.pur_cd,fbp.dealer_cd,fbp.ttltax1,fbp.ttltax2";

            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(userId));
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.setString(4, transactionNo);
            ps.setString(5, dealerCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                WalletDobj dobj = new WalletDobj();
                dobj.setWalletPurCode(rs.getInt("pur_cd"));
                dobj.setWalletAmount(rs.getLong("ttlamount"));
                dobj.setWalletSurcharge(rs.getLong("ttlsurcharge"));
                dobj.setWalletPenalty(rs.getLong("ttlpenalty"));
                dobj.setWalletInterest(rs.getLong("ttlinterest"));
                dobj.setWalletExempted(rs.getLong("ttlexempted"));
                dobj.setWalletRebate(rs.getLong("ttlrebate"));
                dobj.setDealerCode(rs.getString("dealer_cd"));
                dobj.setStateCd(rs.getString("state_cd"));
                dobj.setOffCd(rs.getInt("off_cd"));
                dobj.setWalletTax1(rs.getLong("ttltax1"));
                dobj.setWalletTax2(rs.getLong("ttltax2"));
                dobjList.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transactionNo + " " + e);
            throw new VahanException("Problem in getting Wallet Information Per Transaction Number");
        }
        return dobjList;
    }

    public List<WalletDobj> getWalletData(TransactionManager tmgr, String dealerCd, String stateCd, int offCd) throws VahanException, Exception {
        List<WalletDobj> walletList = new ArrayList<WalletDobj>();
        try {
            String sql = "select a.state_cd, a.off_cd, a.pur_cd, a.wallet_amount ,a.wallet_surcharge, a.wallet_penalty, a.wallet_interest, a.wallet_exempted, a.wallet_rebate,a.dealer_cd,b.descr,a.wallet_tax1,a.wallet_tax2 from  vp_rcpt_wallet a left outer join tm_purpose_mast b on b.pur_cd = a.pur_cd\n"
                    + " where dealer_cd = ? and state_cd = ? and off_cd = ? ";

            PreparedStatement psSelect = tmgr.prepareStatement(sql);
            psSelect.setString(1, dealerCd);
            psSelect.setString(2, stateCd);
            psSelect.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                WalletDobj dobj = new WalletDobj();
                dobj.setWalletPurCode(rs.getInt("pur_cd"));
                dobj.setWalletAmount(rs.getLong("wallet_amount"));
                dobj.setWalletSurcharge(rs.getLong("wallet_surcharge"));
                dobj.setWalletPenalty(rs.getLong("wallet_penalty"));
                dobj.setWalletInterest(rs.getLong("wallet_interest"));
                dobj.setWalletExempted(rs.getLong("wallet_exempted"));
                dobj.setWalletRebate(rs.getLong("wallet_rebate"));
                dobj.setDealerCode(rs.getString("dealer_cd"));
                dobj.setStateCd(rs.getString("state_cd"));
                dobj.setOffCd(rs.getInt("off_cd"));
                dobj.setPurposeDescr(rs.getString("descr"));
                dobj.setWalletTax1(rs.getLong("wallet_tax1"));
                dobj.setWalletTax2(rs.getLong("wallet_tax2"));
                walletList.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error("for DealerCd " + dealerCd + " " + e);
            throw new VahanException("Problem in getting Wallet Information");
        }
        return walletList;
    }

    public void moveDataFromVphFailToVHFail(TransactionManager tmgr, String reVerifyActionType, String transactionNo, String userId, String stateCd, int offCd) throws VahanException, Exception {
        String sql = null;
        try {
            sql = "INSERT INTO vh_rcpt_cart_fail  SELECT current_timestamp,?, moved_on, user_cd, state_cd, off_cd, appl_no, transaction_no, reason,?,op_dt \n"
                    + "  FROM vph_rcpt_cart_fail  where user_cd = ? and transaction_no = ? and state_cd = ? and off_cd = ? ";

            PreparedStatement psVphFailToVhFail = tmgr.prepareStatement(sql);
            psVphFailToVhFail.setString(1, userId);
            psVphFailToVhFail.setString(2, reVerifyActionType);
            psVphFailToVhFail.setLong(3, Long.parseLong(userId));
            psVphFailToVhFail.setString(4, transactionNo);
            psVphFailToVhFail.setString(5, stateCd);
            psVphFailToVhFail.setInt(6, offCd);
            ServerUtil.validateQueryResult(tmgr,psVphFailToVhFail.executeUpdate(), psVphFailToVhFail);

            sql = "INSERT INTO vh_rcpt_cart_fail_detail  SELECT current_timestamp,?,moved_on, moved_by,state_cd, off_cd,user_cd, appl_no, pur_cd ,period_mode, period_from, period_upto, amount, exempted, \n"
                    + " rebate, surcharge, penalty, interest, transaction_no,pmt_type , \n"
                    + " pmt_catg , service_type, route_class, route_length , no_of_trips , domain_cd , distance_run_in_quarter,op_dt,no_adv_units,tax1,tax2 \n"
                    + " FROM vph_rcpt_cart_fail_detail where user_cd = ? and transaction_no = ? and state_cd = ? and off_cd = ? ";

            PreparedStatement psVphFailToVhFailDetail = tmgr.prepareStatement(sql);
            psVphFailToVhFailDetail.setString(1, userId);
            psVphFailToVhFailDetail.setLong(2, Long.parseLong(userId));
            psVphFailToVhFailDetail.setString(3, transactionNo);
            psVphFailToVhFailDetail.setString(4, stateCd);
            psVphFailToVhFailDetail.setInt(5, offCd);
            ServerUtil.validateQueryResult(tmgr, psVphFailToVhFailDetail.executeUpdate(), psVphFailToVhFailDetail);

            sql = "INSERT INTO vh_rcpt_cart_fail_breakup SELECT current_timestamp,?, moved_on, user_cd, state_cd, off_cd, transaction_no, pur_cd, \n"
                    + " ttlamount, ttlexempted, ttlrebate, ttlsurcharge, ttlpenalty, ttlinterest,ttltax1,ttltax2 \n"
                    + "  FROM vph_rcpt_cart_fail_breakup  where user_cd = ? and transaction_no = ? and state_cd = ? and off_cd = ? ";

            PreparedStatement psVphFailBreakUptoVhFailBreakUp = tmgr.prepareStatement(sql);
            psVphFailBreakUptoVhFailBreakUp.setString(1, userId);
            psVphFailBreakUptoVhFailBreakUp.setLong(2, Long.parseLong(userId));
            psVphFailBreakUptoVhFailBreakUp.setString(3, transactionNo);
            psVphFailBreakUptoVhFailBreakUp.setString(4, stateCd);
            psVphFailBreakUptoVhFailBreakUp.setInt(5, offCd);
            ServerUtil.validateQueryResult(tmgr,psVphFailBreakUptoVhFailBreakUp.executeUpdate(), psVphFailBreakUptoVhFailBreakUp);

            sql = " delete from vph_rcpt_cart_fail where user_cd = ? and transaction_no = ? and state_cd = ? and off_cd = ? ";
            PreparedStatement psDeleteVphFailToVhFail = tmgr.prepareStatement(sql);
            psDeleteVphFailToVhFail.setLong(1, Long.parseLong(userId));
            psDeleteVphFailToVhFail.setString(2, transactionNo);
            psDeleteVphFailToVhFail.setString(3, stateCd);
            psDeleteVphFailToVhFail.setInt(4, offCd);
            ServerUtil.validateQueryResult(tmgr,psDeleteVphFailToVhFail.executeUpdate(), psDeleteVphFailToVhFail);

            sql = " delete from vph_rcpt_cart_fail_detail where user_cd = ? and transaction_no = ? and state_cd = ? and off_cd = ? ";
            PreparedStatement psDeleteVphFailToVhFailDetail = tmgr.prepareStatement(sql);
            psDeleteVphFailToVhFailDetail.setLong(1, Long.parseLong(userId));
            psDeleteVphFailToVhFailDetail.setString(2, transactionNo);
            psDeleteVphFailToVhFailDetail.setString(3, stateCd);
            psDeleteVphFailToVhFailDetail.setInt(4, offCd);
            ServerUtil.validateQueryResult(tmgr, psDeleteVphFailToVhFailDetail.executeUpdate(), psDeleteVphFailToVhFailDetail);

            sql = " delete from vph_rcpt_cart_fail_breakup where user_cd = ? and transaction_no = ? and state_cd = ? and off_cd = ? ";
            PreparedStatement psDeleteVphFailBreakUptoVhFailBreakUp = tmgr.prepareStatement(sql);
            psDeleteVphFailBreakUptoVhFailBreakUp.setLong(1, Long.parseLong(userId));
            psDeleteVphFailBreakUptoVhFailBreakUp.setString(2, transactionNo);
            psDeleteVphFailBreakUptoVhFailBreakUp.setString(3, stateCd);
            psDeleteVphFailBreakUptoVhFailBreakUp.setInt(4, offCd);
            ServerUtil.validateQueryResult(tmgr,psDeleteVphFailBreakUptoVhFailBreakUp.executeUpdate(), psDeleteVphFailBreakUptoVhFailBreakUp);
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transactionNo + " " + e);
            throw new VahanException("Problem in moving data from vphFail To vhFail");
        }

    }
//    public List<WalletDobj> getUsedWalletAmount(TransactionManager tmgr, List<WalletDobj> feeAndTaxList, List<WalletDobj> walletList) throws SQLException, Exception {
//        List<WalletDobj> usedWalletAmountList = new ArrayList<WalletDobj>();
//        for (WalletDobj feeAndTaxPurCd : feeAndTaxList) {
//            if (walletList.contains(feeAndTaxPurCd)) {
//                WalletDobj usedWalletAmountDobj = new WalletDobj();
//                int index = walletList.indexOf(feeAndTaxPurCd);
//                WalletDobj walletDobj = walletList.get(index);
//
//                usedWalletAmountDobj.setWalletPurCode(feeAndTaxPurCd.getWalletPurCode());
//                if (feeAndTaxPurCd.getWalletAmount() >= walletDobj.getWalletAmount()) {
//                    usedWalletAmountDobj.setWalletUsedAmount(walletDobj.getWalletAmount());
//                } else {
//                    usedWalletAmountDobj.setWalletUsedAmount(feeAndTaxPurCd.getWalletAmount());
//                }
//
//                if (feeAndTaxPurCd.getWalletPenalty() >= walletDobj.getWalletPenalty()) {
//                    usedWalletAmountDobj.setWalletUsedPenalty(walletDobj.getWalletPenalty());
//                } else {
//                    usedWalletAmountDobj.setWalletUsedPenalty(feeAndTaxPurCd.getWalletPenalty());
//                }
//
//                if (feeAndTaxPurCd.getWalletExempted() >= walletDobj.getWalletExempted()) {
//                    usedWalletAmountDobj.setWalletUsedExempted(walletDobj.getWalletExempted());
//                } else {
//                    usedWalletAmountDobj.setWalletUsedExempted(feeAndTaxPurCd.getWalletExempted());
//                }
//
//                if (feeAndTaxPurCd.getWalletSurcharge() >= walletDobj.getWalletSurcharge()) {
//                    usedWalletAmountDobj.setWalletUsedSurcharge(walletDobj.getWalletSurcharge());
//                } else {
//                    usedWalletAmountDobj.setWalletUsedSurcharge(feeAndTaxPurCd.getWalletSurcharge());
//                }
//
//                if (feeAndTaxPurCd.getWalletRebate() >= walletDobj.getWalletRebate()) {
//                    usedWalletAmountDobj.setWalletUsedRebate(walletDobj.getWalletRebate());
//                } else {
//                    usedWalletAmountDobj.setWalletUsedRebate(feeAndTaxPurCd.getWalletRebate());
//                }
//
//                if (feeAndTaxPurCd.getWalletInterest() >= walletDobj.getWalletInterest()) {
//                    usedWalletAmountDobj.setWalletUsedInterest(walletDobj.getWalletInterest());
//                } else {
//                    usedWalletAmountDobj.setWalletUsedInterest(feeAndTaxPurCd.getWalletInterest());
//                }
//
//
//                usedWalletAmountDobj.setWalletAmount(Math.abs(usedWalletAmountDobj.getWalletUsedAmount() - walletDobj.getWalletAmount()));
//                usedWalletAmountDobj.setWalletPenalty(Math.abs(usedWalletAmountDobj.getWalletUsedPenalty() - walletDobj.getWalletPenalty()));
//                usedWalletAmountDobj.setWalletRebate(Math.abs(usedWalletAmountDobj.getWalletUsedRebate() - walletDobj.getWalletRebate()));
//                usedWalletAmountDobj.setWalletSurcharge(Math.abs(usedWalletAmountDobj.getWalletUsedSurcharge() - walletDobj.getWalletSurcharge()));
//                usedWalletAmountDobj.setWalletExempted(Math.abs(usedWalletAmountDobj.getWalletUsedExempted() - walletDobj.getWalletExempted()));
//                usedWalletAmountDobj.setWalletInterest(Math.abs(usedWalletAmountDobj.getWalletUsedInterest() - walletDobj.getWalletInterest()));
//                usedWalletAmountList.add(usedWalletAmountDobj);
//            }
//        }
//        return usedWalletAmountList;
//    }
}
