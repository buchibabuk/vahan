/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.eapplication;

import java.sql.PreparedStatement;
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
import nic.vahan.form.dobj.dealer.PaymentGatewayDobj;
import nic.vahan.form.impl.ManualReceiptEntryImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.fancy.AdvanceRegnFeeImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

public class OnlinePaymentImpl {

    private static Logger LOGGER = Logger.getLogger(OnlinePaymentImpl.class);

    //Revert back 
    public boolean getPaymentRevertBack(String user_cd, String appl_no, String payType) throws VahanException {
        boolean flag = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        Exception ex = null;
        try {
            tmgr = new TransactionManager("inside revertBack method in OnlinePayment");

            ServerUtil.revertBackInOnlinePayment(tmgr, appl_no, user_cd);

            String sql = "INSERT INTO " + TableList.VP_ONLINE_PAY_USER_INFO_CANCEL + "("
                    + " cancel_dt, cancel_by,state_cd, off_cd, user_cd, user_pwd, mobile_no, created_by, created_dt, "
                    + " appl_no)"
                    + " SELECT current_timestamp,?,state_cd, off_cd, user_cd, user_pwd, mobile_no, created_by, created_dt,"
                    + " appl_no "
                    + " FROM " + TableList.VP_ONLINE_PAY_USER_INFO + " Where user_cd = ? and appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, user_cd);
            ps.setLong(2, Long.parseLong(user_cd));
            ps.setString(3, appl_no);
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

            sql = "delete from " + TableList.VP_ONLINE_PAY_USER_INFO + " where user_cd=? and appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(user_cd));
            ps.setString(2, appl_no);
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

            ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VP_RCPT_CART_TEMP);

            ManualReceiptEntryDobj manualRcptDobj = ManualReceiptEntryImpl.getApprovedManualReceiptDetails(appl_no);
            if (manualRcptDobj != null) {
                ManualReceiptEntryImpl.updateVTManualReceiptEntryStatus(tmgr, appl_no, false);
            }

            if (!CommonUtils.isNullOrBlank(payType) && (TableConstants.TAX_MODE_BALANCE.equals(payType) || TableConstants.ONLINE_TAX.equals(payType) || TableConstants.ONLINE_TAX.equals(payType) || TableConstants.ONLINE_FANCY.equals(payType))) {
                String balanceInsertSql = "INSERT INTO " + TableList.VP_ONLINE_PAY_BALANCE_FEE_CANCEL + " SELECT current_timestamp, ?, state_cd, off_cd, user_cd, appl_no, "
                        + "regn_no, owner_name, selected_option, chasi_no, op_dt,form_type,vh_class FROM " + TableList.VP_ONLINE_PAY_BALANCE_FEE_DETAILS + "  where user_cd = ?  ";

                PreparedStatement psBalance = tmgr.prepareStatement(balanceInsertSql);
                psBalance.setString(1, user_cd);
                psBalance.setLong(2, Long.parseLong(user_cd));
                ServerUtil.validateQueryResult(tmgr, psBalance.executeUpdate(), psBalance);

                String BalanceDeleteSql = "delete from " + TableList.VP_ONLINE_PAY_BALANCE_FEE_DETAILS + " where user_cd = ? ";
                PreparedStatement psBalanceDelete = tmgr.prepareStatement(BalanceDeleteSql);
                psBalanceDelete.setLong(1, Long.parseLong(user_cd));
                ServerUtil.validateQueryResult(tmgr, psBalanceDelete.executeUpdate(), psBalanceDelete);

                ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_TAX_INSTALLMENT_BRKUP);
                if (TableConstants.ONLINE_FANCY.equals(payType)) {
                    String advanceDeleteSql = "delete from  " + TableList.VP_ADVANCE_REGN_NO + " where pay_appl_no = ? ";
                    PreparedStatement psAdvanceDelete = tmgr.prepareStatement(advanceDeleteSql);
                    psAdvanceDelete.setString(1, appl_no);
                    ServerUtil.validateQueryResult(tmgr, psAdvanceDelete.executeUpdate(), psAdvanceDelete);
                }
            }
            tmgr.commit();
            flag = true;
        } catch (VahanException ve) {
            throw ve;
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

        return flag;

    }

    // used to get createdBy and  validate userId and password.
    public static List validateUserIdAndPassword(String appl_no, String password, String hiddenRandomNo) throws VahanException {
        String sql = null;
        String createdBy = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        List list = new ArrayList();
        try {
            tmgr = new TransactionManager("inside validateUserIdAndPassword of OnlinePaymentImpl");
            //sql = "select * from " + TableList.VP_ONLINE_PAY_USER_INFO + " where user_cd = ? ";
            sql = "select distinct cart.appl_no,info.user_pwd,info.created_dt,info.created_by,info.user_cd from " + TableList.VP_RCPT_CART + " as cart," + TableList.VP_ONLINE_PAY_USER_INFO + " as info where info.user_cd=cart.user_cd and cart.appl_no =?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                String pwd = rs.getString("user_pwd");
                pwd = ServerUtil.sha256hex(ServerUtil.sha256hex(pwd) + hiddenRandomNo);
                String id = rs.getString("user_cd");
                String applNo = rs.getString("appl_no");
                if (password.equals(pwd) && appl_no.equals(applNo)) {
                    createdBy = String.valueOf(rs.getLong("created_by"));
                    list.add(id);
                    list.add(createdBy);
                    list.add(rs.getString("created_dt"));
                } else {
                    throw new VahanException("Invalid Login Credentials. Please try again !!!");
                }
            }
        } catch (VahanException e) {
            throw e;
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
        return list;
    }

    public String getTransactionNumber(String applNo) throws VahanException {
        TransactionManager tmgr = null;
        String transNo = null;
        PreparedStatement ps;
        RowSet rs;
        try {
            String sql = "select distinct vpr.transaction_no from " + TableList.VP_RCPT_CART + " as vpr, " + TableList.VP_ONLINE_PAY_USER_INFO + " as vpin where vpr.user_cd=vpin.user_cd and vpr.state_cd=vpin.state_cd and vpr.appl_no =?";
            tmgr = new TransactionManager("getTransactionNumber");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                transNo = rs.getString("transaction_no");
            }
        } catch (SQLException e) {
            throw new VahanException(e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return transNo;
    }

    // used to get Detail of particular applNo.
    public List<OnlinePaymentDobj> getDetail(String userCd, String applNo) throws VahanException, Exception {

        String sql = null;
        PreparedStatement ps = null;
        RowSet rs, rs1;
        TransactionManager tmgr = null;
        List<OnlinePaymentDobj> dobjList = new ArrayList<OnlinePaymentDobj>();

        try {
            tmgr = new TransactionManager("inside getDetail of online cash Impl");
            sql = "select * from " + TableList.VP_ONLINE_PAY_BALANCE_FEE_DETAILS + " WHERE USER_CD = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(userCd));
            rs1 = tmgr.fetchDetachedRowSet_No_release();
            if (!rs1.next()) {
                sql = "SELECT c.regn_no,b.pur_cd FROM " + TableList.VP_ONLINE_PAY_USER_INFO + " a\n"
                        + "INNER JOIN " + TableList.VA_STATUS + " b on a.appl_no = b.appl_no  \n"
                        + "INNER JOIN " + TableList.VA_DETAILS + " c on a.appl_no = c.appl_no AND c.pur_cd = b.pur_cd\n"
                        + "where a.user_cd = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setLong(1, Long.parseLong(userCd));
                rs1 = tmgr.fetchDetachedRowSet_No_release();
                if (rs1.next()) {
                    if ((rs1.getInt("pur_cd") == TableConstants.VM_TRANSACTION_TRADE_CERT_NEW)
                            || (rs1.getInt("pur_cd") == TableConstants.VM_TRANSACTION_TRADE_CERT_DUP)
                            || (rs1.getInt("pur_cd") == TableConstants.TRADE_CERTIFICATE_TAX)) {
                        sql = "select b.descr as purpose, to_char(a.period_from, 'dd-Mon-yyyy') as period_from, to_char(a.period_upto, 'dd-Mon-yyyy') as period_upto,"
                                + " (a.amount + a.surcharge + a.interest + a.tax1 + a.tax2 - a.exempted - a.rebate) as amount,a.transaction_no, a.penalty as fine,s.state_cd,s.off_cd,a.appl_no,s.action_cd, 'TradeRegn' as regn_no \n"
                                + " from " + TableList.VP_RCPT_CART + " a\n"
                                + " left outer join " + TableList.TM_PURPOSE_MAST + " b on b.pur_cd = a.pur_cd\n"
                                + " left outer join " + TableList.VA_STATUS + " s on a.appl_no = s.appl_no and s.pur_cd=a.pur_cd\n"
                                + " where a.user_cd = ? "
                                + " order by s.action_cd";
                        ps = tmgr.prepareStatement(sql);
                        ps.setLong(1, Long.parseLong(userCd));
                    } else if (rs1.getInt("pur_cd") != TableConstants.VM_PMT_RENEW_COUNTER_SIGNATURE_PUR_CD && rs1.getInt("pur_cd") != TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD && rs1.getInt("pur_cd") != TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                            && rs1.getInt("pur_cd") != TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && !(rs1.getString("regn_no").equalsIgnoreCase("NEW") && rs1.getInt("pur_cd") == TableConstants.VM_PMT_FRESH_PUR_CD)) {
                        sql = " select b.descr as purpose, to_char(a.period_from, 'dd-Mon-yyyy') as period_from, to_char(a.period_upto, 'dd-Mon-yyyy') as period_upto, "
                                + " (a.amount + a.surcharge + a.interest + a.tax1 + a.tax2 + a.prv_adjustment - a.exempted - a.rebate) as amount,a.transaction_no, a.penalty as fine,a.state_cd,a.off_cd,a.appl_no,s.action_cd,COALESCE(o.regn_no, 'NEW') AS regn_no\n"
                                + " from " + TableList.VP_RCPT_CART + " a \n"
                                + " inner join (SELECT st.* FROM " + TableList.VA_STATUS + " st"
                                + " inner join " + TableList.TM_ACTION + " a on st.action_cd = a.action_cd  WHERE appl_no = ? and a.role_cd=" + TableConstants.USER_ROLE_CASHIER + " LIMIT 1) s on a.appl_no = s.appl_no  \n"
                                + " left outer join " + TableList.TM_PURPOSE_MAST + " b on b.pur_cd = a.pur_cd\n"
                                + " left outer join " + TableList.VT_OWNER + " o on (o.state_cd,o.off_cd,o.regn_no) in (select state_cd,off_cd,regn_no from vt_owner where regn_no = ? order by op_dt desc limit 1) \n"
                                + " where a.user_cd = ?  order by s.action_cd ";

                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, applNo);
                        ps.setString(2, rs1.getString("regn_no").toUpperCase());
                        ps.setLong(3, Long.parseLong(userCd));
                    } else if (rs1.getInt("pur_cd") == TableConstants.VM_PMT_FRESH_PUR_CD && rs1.getString("regn_no").equalsIgnoreCase("NEW")) {
                        sql = " select b.descr as purpose, to_char(a.period_from, 'dd-Mon-yyyy') as period_from, to_char(a.period_upto, 'dd-Mon-yyyy') as period_upto, "
                                + " (a.amount + a.surcharge + a.interest + a.tax1 + a.tax2 + a.prv_adjustment - a.exempted - a.rebate) as amount,a.transaction_no, a.penalty as fine,o.state_cd,a.off_cd,a.appl_no,s.action_cd,COALESCE(o.regn_no, 'NEW') AS regn_no\n"
                                + " from " + TableList.VP_RCPT_CART + " a \n"
                                + " inner join (SELECT * FROM " + TableList.VA_STATUS + " WHERE appl_no = ? LIMIT 1) s on a.appl_no = s.appl_no  \n"
                                + " left outer join " + TableList.TM_PURPOSE_MAST + " b on b.pur_cd = a.pur_cd\n"
                                + " left outer join " + TableList.VA_PERMIT_OWNER + " o on (o.state_cd,o.off_cd,o.appl_no,o.regn_no) in (select state_cd,off_cd,appl_no,regn_no from permit.va_permit_owner where regn_no = ? and appl_no=? ) \n"
                                + " where a.user_cd = ?  order by s.action_cd";

                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, applNo);
                        ps.setString(2, rs1.getString("regn_no").toUpperCase());
                        ps.setString(3, applNo);
                        ps.setLong(4, Long.parseLong(userCd));
                    } else if (rs1.getInt("pur_cd") == TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD || rs1.getInt("pur_cd") == TableConstants.VM_PMT_DUP_COUNTER_SIGNATURE_PUR_CD) {
                        sql = " select b.descr as purpose, to_char(a.period_from, 'dd-Mon-yyyy') as period_from, to_char(a.period_upto, 'dd-Mon-yyyy') as period_upto, "
                                + " (a.amount + a.surcharge + a.interest + a.tax1 + a.tax2 + a.prv_adjustment - a.exempted - a.rebate) as amount,a.transaction_no, a.penalty as fine,o.state_cd,a.off_cd,a.appl_no,s.action_cd,COALESCE(o.regn_no, 'NEW') AS regn_no\n"
                                + " from " + TableList.VP_RCPT_CART + " a \n"
                                + " inner join (SELECT * FROM " + TableList.VA_STATUS + " WHERE appl_no = ? LIMIT 1) s on a.appl_no = s.appl_no  \n"
                                + " left outer join " + TableList.TM_PURPOSE_MAST + " b on b.pur_cd = a.pur_cd\n"
                                + " left outer join " + TableList.VA_PERMIT_COUNTERSIGNATURE + " o on (o.state_cd,o.off_cd,o.appl_no,o.regn_no) in (select state_cd,off_cd,appl_no,regn_no from permit.VA_PERMIT_COUNTERSIGNATURE where regn_no = ? and appl_no=? ) \n"
                                + " where a.user_cd = ?  order by s.action_cd";

                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, applNo);
                        ps.setString(2, rs1.getString("regn_no").toUpperCase());
                        ps.setString(3, applNo);
                        ps.setLong(4, Long.parseLong(userCd));
                    } else {
                        sql = " select b.descr as purpose, to_char(a.period_from, 'dd-Mon-yyyy') as period_from, to_char(a.period_upto, 'dd-Mon-yyyy') as period_upto, (a.amount + a.surcharge + a.interest + a.tax1 + a.tax2 + a.prv_adjustment - a.exempted - a.rebate) as amount"
                                + " ,a.transaction_no, a.penalty as fine,o.state_cd,o.off_cd,a.appl_no,s.action_cd,COALESCE(o.regn_no, 'NEW') AS regn_no \n"
                                + " from " + TableList.VP_RCPT_CART + " a \n"
                                + " left outer join " + TableList.TM_PURPOSE_MAST + "  b on b.pur_cd = a.pur_cd \n"
                                + " left outer join " + TableList.VA_OWNER + "  o on a.appl_no = o.appl_no \n"
                                + " left outer join " + TableList.VA_STATUS + " s on a.appl_no = s.appl_no \n"
                                + " where a.user_cd = ? ";

                        ps = tmgr.prepareStatement(sql);
                        ps.setLong(1, Long.parseLong(userCd));
                    }
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    while (rs.next()) {
                        OnlinePaymentDobj dobj = new OnlinePaymentDobj();
                        dobj.setPurpose(rs.getString("purpose"));
                        dobj.setPeriodFrom(rs.getString("period_from"));
                        dobj.setPeriodUpto(rs.getString("period_upto"));
                        dobj.setAmount(rs.getLong("amount"));
                        dobj.setPenalty(rs.getLong("fine"));
                        dobj.setStateCd(rs.getString("state_cd"));
                        dobj.setOffCode(rs.getInt("off_cd"));
                        dobj.setApplNo(rs.getString("appl_no"));
                        dobj.setActionCode(rs.getInt("action_cd"));
                        dobj.setTransactionNo(rs.getString("transaction_no"));
                        dobj.setRegnNo(rs.getString("regn_no"));
                        dobj.setPurCd(rs1.getInt("pur_cd"));
                        dobjList.add(dobj);
                    }
                }
            } else {
                sql = "  select b.descr as purpose, to_char(a.period_from, 'dd-Mon-yyyy') as period_from, to_char(a.period_upto, 'dd-Mon-yyyy') as period_upto, (amount + surcharge + interest + tax1 + tax2 + prv_adjustment - exempted - rebate) as amount, penalty as fine,a.state_cd,a.off_cd,a.appl_no,a.transaction_no,vb.REGN_NO,vb.form_type, selected_option "
                        + " from " + TableList.VP_RCPT_CART + " a inner join " + TableList.VP_ONLINE_PAY_BALANCE_FEE_DETAILS + " vb on a.state_cd= vb.state_cd and a.off_cd=vb.off_cd and a.appl_no = vb.appl_no and a.user_cd=vb.user_cd "
                        + " left outer join tm_purpose_mast b on b.pur_cd = a.pur_cd where a.appl_no = ? and a.user_cd = ?";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
                ps.setLong(2, Long.parseLong(userCd));
                RowSet rs2 = tmgr.fetchDetachedRowSet_No_release();
                while (rs2.next()) {
                    OnlinePaymentDobj dobj = new OnlinePaymentDobj();
                    dobj.setPurpose(rs2.getString("purpose"));
                    dobj.setPeriodFrom(rs2.getString("period_from"));
                    dobj.setPeriodUpto(rs2.getString("period_upto"));
                    dobj.setAmount(rs2.getLong("amount"));
                    dobj.setPenalty(rs2.getLong("fine"));
                    dobj.setStateCd(rs2.getString("state_cd"));
                    dobj.setOffCode(rs2.getInt("off_cd"));
                    dobj.setApplNo(applNo);
                    if (rs2.getString("selected_option") != null && rs2.getString("selected_option").equals(TableConstants.ONLINE_FANCY)) {
                        dobj.setActionCode(TableConstants.TM_ROLE_FANCY_ADVANCE_REGN_FEE);
                    } else if (rs2.getString("selected_option") != null && rs2.getString("selected_option").equals(TableConstants.ONLINE_AUDIT)) {
                        dobj.setActionCode(TableConstants.TM_ROLE_AUDIT);
                    } else {
                        dobj.setActionCode(TableConstants.TM_ROLE_BALANCE_FEE_TAX_COLLECTION);
                    }
                    dobj.setTransactionNo(rs2.getString("transaction_no"));
                    dobj.setRegnNo(rs2.getString("regn_no"));
                    dobj.setPaymentType(rs2.getString("form_type"));
                    dobj.setBalanceFeePaySelectedMode(rs2.getString("selected_option"));
                    dobjList.add(dobj);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting Details. Please contact Administrator!!!");
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

    // used to update transaction Number
    public String updateTransactionNumber(String stateCd, String applicationNumber, int offCd, Long userId) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "update vp_rcpt_cart set transaction_no = ? where user_cd = ? and transaction_no IS NULL and appl_no = ? ";
        String tempSql = "update " + TableList.VP_RCPT_CART_TEMP + " set transaction_no = ? where user_cd = ? and transaction_no IS NULL and appl_no = ? ";
        String message = "";
        TransactionManager tmgr = null;
        String paymentInward = "";
        try {
            tmgr = new TransactionManager("makePayment()");
            paymentInward = ServerUtil.getUniqueOfficeRcptNo(tmgr, stateCd, offCd, TableConstants.DEALER_PAYMENTID_FLAG, false);
            PreparedStatement psTemp = tmgr.prepareStatement(tempSql);
            psTemp.setString(1, paymentInward);
            psTemp.setLong(2, userId);
            psTemp.setString(3, applicationNumber);
            psTemp.executeUpdate();

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, paymentInward);
            ps.setLong(2, userId);
            ps.setString(3, applicationNumber);
            int updatedCount = ps.executeUpdate();
            if (updatedCount > 0) {
                tmgr.commit();
                message = "Tansaction no Updated Successfully!!!";
            } else {
                throw new VahanException("Tansaction no updation failed!!");
            }

        } catch (VahanException ve) {
            paymentInward = "";
            throw ve;
        } catch (Exception e) {
            paymentInward = "";
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                paymentInward = "";
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return paymentInward;
    }

    public void vatovhNPAuthData(String applNo, TransactionManager tmgr) throws VahanException, Exception {
        String sql = null;
        PreparedStatement ps = null;
        RowSet rs;
        try {
            sql = "select * from permit.va_np_detail WHERE state_cd =? and appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, applNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                sql = "Insert into permit.vha_np_detail (select current_date,?,* from permit.va_np_detail WHERE state_cd =? and appl_no=?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, Util.getUserStateCode());
                ps.setString(3, applNo);
                ps.executeUpdate();
                ps = null;
                sql = "Delete from permit.va_np_detail WHERE state_cd =? and appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setString(2, applNo);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting va_np_detail. Please contact Administrator!!!");
        }

    }

    public static List<PaymentGatewayDobj> getOnlinePaymentFailedList(String stateCd, int offCd, String applNo) {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        List<PaymentGatewayDobj> dobjList = new ArrayList<>();
        try {
            tmgr = new TransactionManagerReadOnly("getFailOnlinePaymentList");
            sql = "  select f.moved_on,f.transaction_no , count(distinct f.appl_no) as totalAppl,array_to_string(array(select vph.appl_no from vph_rcpt_cart_fail vph \n"
                    + " where vph.appl_no = ? and vph.state_cd = ? and vph.off_cd = ? and vph.transaction_no=f.transaction_no group by vph.appl_no),',') as applNoList,\n"
                    + " f.reason,pgi.return_rcpt_no as bank_ref_no,pgi.transaction_id as pgi_trans_id,to_char(pgi.rcpt_dt,'dd-Mon-yyyy') as bank_verfied_dt,pgi.rcpt_amt as transaction_amt from \n"
                    + " vph_rcpt_cart_fail f \n"
                    + " inner join vph_online_pay_user_info_fail d on d.appl_no=f.appl_no and d.state_cd = f.state_cd and d.off_cd=f.off_cd\n"
                    + " left outer join vahanpgi.vp_pgi_details pgi on f.transaction_no = pgi.payment_id where f.appl_no = ? and f.state_cd = ? and f.off_cd = ? and f.reason !~ 'no web'\n"
                    + " group by f.moved_on,f.transaction_no,f.reason,bank_ref_no,bank_verfied_dt,transaction_amt,transaction_id\n"
                    + " order by moved_on desc;";

            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, applNo);
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setString(i++, applNo);
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                PaymentGatewayDobj dobj = new PaymentGatewayDobj();
                dobj.setPaymentId(rs.getString("transaction_no"));
                dobj.setTotalNoOfApplicationsIncart(rs.getString("totalAppl"));
                dobj.setApplicationNumberList(rs.getString("applNoList"));
                dobj.setReason(rs.getString("reason"));
                dobj.setTtlAmount(rs.getLong("transaction_amt"));
                dobj.setBankReferenceNo(rs.getString("bank_ref_no"));
                dobj.setBankVerifiedDate(rs.getString("bank_verfied_dt"));
                dobj.setPgiTransId(rs.getString("pgi_trans_id"));
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

    public void checkReVerifyApplicationCountAndAmount(String transactionNo, String stateCd, int offCd, String applNo) throws VahanException {
        TransactionManager tmgr = null;
        boolean status = false;
        int failedApplAmount = 0;
        int vpRcptApplAmount = 0;
        int failedApplCount = 0;
        int vpRcptApplCount = 0;
        try {
            tmgr = new TransactionManager("checkReVerifyApplicationCountAndAmount");

            String sql = "select case when (a.vpRcptApplCount = a.failApplCount) then true else false end as status,a.vpRcptApplCount,a.failApplCount,a.failAmount,a.vpRcptApplAmount \n"
                    + " from (select count(distinct vp.appl_no) as vpRcptApplCount,sum(vp.amount+vp.surcharge+vp.penalty+vp.interest+vp.tax1+vp.tax2-vp.exempted-vp.rebate) as vpRcptApplAmount,(select count(vph.appl_no) from vph_rcpt_cart_fail vph where vph.transaction_no = ? and vph.state_cd = ? and vph.off_cd = ?) AS failApplCount, \n"
                    + "(SELECT sum(vph_brkup.ttlamount+vph_brkup.ttlsurcharge+vph_brkup.ttlpenalty+vph_brkup.ttlinterest+vph_brkup.ttltax1+vph_brkup.ttltax2-vph_brkup.ttlexempted-vph_brkup.ttlrebate) from vph_rcpt_cart_fail_breakup  vph_brkup \n"
                    + " where vph_brkup.state_cd = ? and vph_brkup.off_cd = ? AND vph_brkup.transaction_no = ? group by vph_brkup.transaction_no) as failAmount"
                    + " from vp_rcpt_cart vp \n"
                    + " where vp.transaction_no is null and vp.state_cd = ? and vp.off_cd = ? and vp.appl_no in \n"
                    + " (select f.appl_no from vph_rcpt_cart_fail f where f.transaction_no = ? and f.state_cd = ?  and f.off_cd = ?)) as a ";

            PreparedStatement ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, transactionNo);
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setString(i++, transactionNo);
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setString(i++, transactionNo);
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                status = rs.getBoolean("status");
                failedApplAmount = rs.getInt("failAmount");
                vpRcptApplAmount = rs.getInt("vpRcptApplAmount");
                failedApplCount = rs.getInt("failApplCount");
                vpRcptApplCount = rs.getInt("vpRcptApplCount");
            } else {
                throw new VahanException("Problem in getting status of fail transaction");
            }

            if (!status) {
                throw new VahanException("Application(s) Mismatch for Payment id " + transactionNo + ". Kindly re-generate the user Credentials for Application(s) [" + applNo + "] then, Re-Verify the failed transaction if amount is already deducted from your account.");
            }

            if (failedApplAmount != vpRcptApplAmount) {

                String applSQLCount = "select a.rcpt_appl_count,b.rcpt_failed_appl_count from \n"
                        + " (select vp.appl_no,count(1) rcpt_appl_count from vp_rcpt_cart vp where vp.appl_no=? group by 1 ) a,\n"
                        + " (select appl_no,count(1) rcpt_failed_appl_count from vph_rcpt_cart_fail_detail fail where appl_no=? and fail.transaction_no = ? group by 1) b";
                PreparedStatement psApplSQLCount = tmgr.prepareStatement(applSQLCount);
                psApplSQLCount.setString(1, applNo);
                psApplSQLCount.setString(2, applNo);
                psApplSQLCount.setString(3, transactionNo);
                RowSet rsApplSQLCount = tmgr.fetchDetachedRowSet_No_release();
                if (rsApplSQLCount.next()) {
                    if (rsApplSQLCount.getInt("rcpt_appl_count") != rsApplSQLCount.getInt("rcpt_failed_appl_count")) {
                        throw new VahanException("Fee details Mismatch for Payment id " + transactionNo + ". Kindly ADD All fees for Application(s) [" + applNo + "] which was failed.");
                    }
                } else {
                    throw new VahanException("Failed transaction Details not found. ");
                }

                String amountSql = "select vp.appl_no,fail.appl_no,sum(vp.amount+vp.surcharge+vp.penalty+vp.interest+vp.tax1+vp.tax2-vp.exempted-vp.rebate) as vpRcptApplAmount,\n"
                        + " sum(fail.amount+fail.surcharge+fail.penalty+fail.interest+fail.tax1+fail.tax2-fail.exempted-fail.rebate) as vphFailRcptApplAmount\n"
                        + " from vph_rcpt_cart_fail_detail fail\n"
                        + " inner join vp_rcpt_cart vp on fail.state_cd=vp.state_cd and fail.off_cd=vp.off_cd and fail.appl_no=vp.appl_no and fail.pur_cd=vp.pur_cd\n"
                        + " where fail.state_cd = ? and fail.off_cd = ? and fail.transaction_no = ? group by vp.appl_no,fail.appl_no ";

                PreparedStatement psAmountSql = tmgr.prepareStatement(amountSql);
                int j = 1;
                psAmountSql.setString(j++, stateCd);
                psAmountSql.setInt(j++, offCd);
                psAmountSql.setString(j++, transactionNo);
                RowSet rsAmountSql = tmgr.fetchDetachedRowSet_No_release();

                if (!rsAmountSql.next()) {
                    throw new VahanException("Failed applications details not found for payment process.");
                }

                rsAmountSql.last();

                if ((rsAmountSql.getRow() != failedApplCount) || (rsAmountSql.getRow() != vpRcptApplCount)) {
                    throw new VahanException("Application(s) Mismatch for Payment id " + transactionNo + ". Kindly ADD Application(s) [" + applNo + "] to the CART and then WITHOUT initiating the fresh payment, first Re-Verify the failed transaction if amount is already deducted from your account.");
                }

                rsAmountSql.beforeFirst();

                int counter = 0;
                String updateSql = "update vp_rcpt_cart set amount=fail.amount,surcharge=fail.surcharge,penalty=fail.penalty,interest=fail.interest,tax1=fail.tax1,tax2=fail.tax2,exempted=fail.exempted,rebate=fail.rebate\n"
                        + "from vph_rcpt_cart_fail_detail fail\n"
                        + "inner join vp_rcpt_cart vp on fail.state_cd=vp.state_cd and fail.off_cd=vp.off_cd and fail.appl_no=vp.appl_no and  fail.pur_cd=vp.pur_cd\n"
                        + " where fail.state_cd = ? and fail.off_cd = ? and fail.appl_no = ? and fail.transaction_no = ? and vp_rcpt_cart.appl_no = fail.appl_no and vp_rcpt_cart.pur_cd=fail.pur_cd";
                PreparedStatement psUpdate = tmgr.prepareStatement(updateSql);
                while (rsAmountSql.next()) {
                    if (rsAmountSql.getInt("vpRcptApplAmount") > rsAmountSql.getInt("vphFailRcptApplAmount")) {
                        counter++;
                        int k = 1;
                        psUpdate.setString(k++, stateCd);
                        psUpdate.setInt(k++, offCd);
                        psUpdate.setString(k++, rsAmountSql.getString("appl_no"));
                        psUpdate.setString(k++, transactionNo);
                        psUpdate.addBatch();
                    }
                }
                int[] updatedCount = psUpdate.executeBatch();
                if (counter != 0 && updatedCount.length == counter) {
                    tmgr.commit();
                }
            }
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + transactionNo + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem occurred during Re-verifcation of transaction.");
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

    public String[] getOwnerDetailsForFancyNoDetails(String applNo) {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String[] arr = new String[3];
        try {
            tmgr = new TransactionManagerReadOnly("getOwnerDetailsForFancyNoDetails");
            sql = " select vp.owner_name,vp.f_name ,vh.descr\n"
                    + " from " + TableList.VP_ADVANCE_REGN_NO + " vp inner join " + TableList.VP_ONLINE_PAY_BALANCE_FEE_DETAILS + " b on vp.pay_appl_no = b.appl_no\n"
                    + " left outer join " + TableList.VM_VH_CLASS + " vh on b.vh_class = vh.vh_class\n"
                    + " where pay_appl_no = ? ";

            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, applNo);

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                arr[0] = rs.getString("owner_name");
                arr[1] = rs.getString("f_name");
                arr[2] = rs.getString("descr");
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
        return arr;
    }

    public void validateAdvanceRegistrationNumber(String regnNo, String stateCd, int offCd) throws VahanException {
        TransactionManager tmgr = null;
        try {
            String validateMess = "";
            String sql = "select * from vt_advance_regn_no where regn_no=?";
            tmgr = new TransactionManager("validateAdvanceRegistrationNumber");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                throw new VahanException("Vehicle No " + regnNo + " already booked!!! Please try with different no.");
            }

            validateMess = new AdvanceRegnFeeImpl().verifyInVtOwner(regnNo, stateCd, offCd);
            if (CommonUtils.isNullOrBlank(validateMess)) {
                validateMess = new AdvanceRegnFeeImpl().verifyRegnNoWithoutSurrender(regnNo, stateCd, offCd);
                if (!CommonUtils.isNullOrBlank(validateMess)) {
                    throw new VahanException(validateMess);
                }
            } else {
                throw new VahanException(validateMess);
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
    }
}
