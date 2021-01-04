/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.dealer;

import nic.vahan.form.impl.*;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.dealer.PaymentGatewayDobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

public class PaymentGatewayImpl {

    private static final Logger LOGGER = Logger.getLogger(PaymentGatewayImpl.class);

    // used to get the totalAmount detail, registration_no, chassis_no 
    // related to each application number  
    public List<PaymentGatewayDobj> getApplicationNoAndAmountDetail(String paymentId, String purCd) {

        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        List dobjList = new ArrayList();
        List listOfApplNo = new ArrayList();
        try {
            tmgr = new TransactionManager("inside getApplicationNumberList of PaymentGatewayImpl");


            if (!paymentId.equals("New Cart")) {
                sql = " select a.appl_no, a.regn_no, a.chasi_no, a.amount,a.fuel,a.state_cd,b.pur_cd,a.op_dt,a.pur_cd as dlrPurpose, a.appl_dt from (select o.appl_no, o.regn_no, o.chasi_no,sum(vp.amount+vp.surcharge+vp.penalty+vp.interest+vp.tax1+vp.tax2-vp.exempted-vp.rebate) as amount,o.fuel,o.state_cd,vp.op_dt,s.pur_cd, to_char(d.appl_dt, 'YYYY-MM-DD') as appl_dt  from vp_rcpt_cart as vp \n"
                        + " left outer join va_owner as o on o.appl_no = vp.appl_no "
                        + " left outer join va_status as s on s.appl_no = vp.appl_no "
                        + " left outer join va_details as d on d.appl_no = vp.appl_no and d.pur_cd = s.pur_cd"
                        + " where vp.user_cd = ? and vp.transaction_no  = ? and vp.off_cd = ? \n"
                        + " group by 1,2,3,7,8,9 order by 1) a\n"
                        + " left join vp_rcpt_cart b on b.appl_no=a.appl_no and b.pur_cd in (" + TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE + "," + TableConstants.VM_TRANSACTION_MAST_TEMP_REG + "," + TableConstants.VM_TRANSACTION_MAST_FIT_CERT + ") ";
            } else {
                sql = " select a.appl_no, a.regn_no, a.chasi_no, a.amount,a.fuel,a.state_cd,b.pur_cd,a.op_dt,a.pur_cd as dlrPurpose, a.appl_dt from (select o.appl_no, o.regn_no, o.chasi_no,sum(vp.amount+vp.surcharge+vp.penalty+vp.interest+vp.tax1+vp.tax2-vp.exempted-vp.rebate) as amount,o.fuel,o.state_cd,vp.op_dt,s.pur_cd, to_char(d.appl_dt, 'YYYY-MM-DD') as appl_dt  from vp_rcpt_cart as vp \n"
                        + " left outer join va_owner as o on o.appl_no = vp.appl_no "
                        + " left outer join va_status as s on s.appl_no = vp.appl_no "
                        + " left outer join va_details as d on d.appl_no = vp.appl_no and d.pur_cd = s.pur_cd"
                        + " where vp.user_cd = ? and vp.off_cd = ? and vp.transaction_no IS NULL  \n"
                        + " group by 1,2,3,7,8,9 order by 1) a\n"
                        + " left join vp_rcpt_cart b on b.appl_no=a.appl_no and b.pur_cd in (" + TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE + "," + TableConstants.VM_TRANSACTION_MAST_TEMP_REG + "," + TableConstants.VM_TRANSACTION_MAST_FIT_CERT + ") ";
            }

            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(Util.getEmpCode()));
            if (!paymentId.equals("New Cart")) {
                ps.setString(2, paymentId);
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            } else {
                ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                PaymentGatewayDobj dobj = new PaymentGatewayDobj();
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setTtlAmount(rs.getLong("amount"));
                dobj.setStateCd(rs.getString("state_cd"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setChassisNo(rs.getString("chasi_no"));
                dobj.setOpDate(rs.getDate("op_dt"));
                dobj.setFuel(rs.getInt("fuel"));
                dobj.setDlrPurCd(rs.getInt("dlrPurpose"));
                dobj.setAppl_dt(rs.getString("appl_dt"));
                if (!listOfApplNo.contains(rs.getString("appl_no"))) {
                    listOfApplNo.add(rs.getString("appl_no"));
                    dobjList.add(dobj);
                } else {
                    continue;
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
        return dobjList;
    }

    //if user want to revert back to Add To Cart Form then
    //that application number get deleted.
    public String deleteApplicationNumber(String applicationNumber, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        String message = "";
        String sql = "";
        try {
            ServerUtil.revertBackInOnlinePayment(tmgr, applicationNumber, Util.getEmpCode());
            message = "Employee Deleted Successfully!!!";
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }
        return message;
    }

    //used to get tax and fee detail according to application number.
    public List<PaymentGatewayDobj> getDetail(String appln_no) {

        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        List<PaymentGatewayDobj> dobjList = new ArrayList<PaymentGatewayDobj>();

        try {
            tmgr = new TransactionManager("inside getDetail of Payment Gateway Impl");
            sql = " select b.descr as purpose, to_char(a.period_from, 'dd-Mon-yyyy') as period_from, to_char(a.period_upto, 'dd-Mon-yyyy') as period_upto, (amount + surcharge + interest + tax1 + tax2 - exempted - rebate) as amount, penalty as fine \n"
                    + "   from vp_rcpt_cart a \n"
                    + "   left outer join tm_purpose_mast b on b.pur_cd = a.pur_cd\n"
                    + "   where a.appl_no = ? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appln_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                PaymentGatewayDobj dobj = new PaymentGatewayDobj();
                dobj.setPurpose(rs.getString("purpose"));
                dobj.setPeriodFrom(rs.getString("period_from"));
                dobj.setPeriodUpto(rs.getString("period_upto"));
                dobj.setAmount(rs.getLong("amount"));
                dobj.setPenalty(rs.getLong("fine"));
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

    public Object[] updateTransactionNumber(TransactionManager tmgr, String stateCd, String applicationNumberList) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "update vp_rcpt_cart set transaction_no = ? where user_cd = ? and transaction_no IS NULL and appl_no IN (" + applicationNumberList + ")";
        Object[] validate = new Object[2];
        try {
            String paymentInward = ServerUtil.getUniqueOfficeRcptNo(tmgr, stateCd, Util.getSelectedSeat().getOff_cd(), TableConstants.DEALER_PAYMENTID_FLAG, false);
            ps = tmgr.prepareStatement(sql);
            if (paymentInward != null && !paymentInward.equals("")) {
                ps.setString(1, paymentInward);
            } else {
                throw new VahanException("Problem in generating Transaction Number");
            }
            ps.setLong(2, Long.parseLong(Util.getEmpCode()));
            int updatedCount = ps.executeUpdate();
            if (updatedCount > 0) {
                validate[0] = updatedCount;
                validate[1] = paymentInward;
            } else {
                throw new VahanException("Transaction no updation failed!!");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("PaymentId not generated due to technical error.");
        }
        return validate;
    }

    public String getPurCdList(String paymentId) {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String purCdList = "";
        String whereConditionVar = null;
        try {
            tmgr = new TransactionManager("inside getPurCdList of Payment Gateway Impl");

            if (paymentId.equals("New Cart")) {
                whereConditionVar = " where user_cd = ? and transaction_no IS NULL and   pur_cd in (" + TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE + "," + TableConstants.VM_TRANSACTION_MAST_TEMP_REG + "," + TableConstants.VM_TRANSACTION_MAST_FIT_CERT + ") group by 1  order by 1 ";
            } else if (!paymentId.equals("New Cart")) {
                whereConditionVar = " where user_cd = ? and transaction_no = ? and   pur_cd in (" + TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE + "," + TableConstants.VM_TRANSACTION_MAST_TEMP_REG + "," + TableConstants.VM_TRANSACTION_MAST_FIT_CERT + ") group by 1  order by 1 ";
            }

            sql = "select pur_cd from vp_rcpt_cart " + whereConditionVar;
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(Util.getEmpCode()));
            if (!paymentId.equals("New Cart")) {
                ps.setString(2, paymentId);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                purCdList += "'" + rs.getString("pur_cd") + "',";
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
        return purCdList;
    }

    public String getVahanPgiUrl(String Conn_Type) {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String vahan_url = "";

        try {
            tmgr = new TransactionManager("inside getVahanPgiUrl of Payment Gateway Impl");
            sql = "select conn_dblink from tm_dblink_list where conn_type = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Conn_Type);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                vahan_url = rs.getString("conn_dblink");
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
        return vahan_url;
    }

    public void rollbackToPreviousActionCode(Status_dobj status_dobj, String appl_no) throws VahanException {

        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("rollBackToPreviousActionCode");

            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);

            status_dobj.setOffice_remark("");
            status_dobj.setPublic_remark("");
            status_dobj.setCntr_id("");
            status_dobj.setEmp_cd(Long.parseLong(Util.getEmpCode()));
            status_dobj.setOff_cd(Util.getSelectedSeat().getOff_cd());

            ServerUtil.fileFlow(tmgr, status_dobj);

            String sql = "select action_cd from " + TableList.VA_STATUS + " where appl_no = ? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (rs.getInt("action_cd") == status_dobj.getAction_cd()) {
                    String deleteStatus = this.deleteApplicationNumber(appl_no, tmgr);

                    if (deleteStatus != null && !deleteStatus.equals("")) {
                        tmgr.commit();
                    } else {
                        throw new VahanException("RollBack failed!!");
                    }
                }
            } else {
                throw new VahanException("RollBack failed !!");
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
    }

    public List<PaymentGatewayDobj> getFailCartList(Date fromDate, Date uptoDate, String stateCd, int offCd, String empCd) {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        List<PaymentGatewayDobj> dobjList = new ArrayList<PaymentGatewayDobj>();

        try {
            tmgr = new TransactionManager("inside getFailCartList of PaymentGatewayImpl");
            sql = "  select f.moved_on,f.transaction_no , count(distinct f.appl_no) as totalAppl,array_to_string(array(select vph.appl_no from vph_rcpt_cart_fail vph "
                    + " where vph.user_cd = ? and vph.state_cd = ? and vph.off_cd = ? and  vph.moved_on between ? and (?::date + '1 day'::interval) and vph.transaction_no=f.transaction_no group by vph.appl_no),',') as applNoList,"
                    + " f.reason,pgi.return_rcpt_no as bank_ref_no,pgi.transaction_id as pgi_trans_id,to_char(pgi.rcpt_dt,'dd-Mon-yyyy') as bank_verfied_dt,pgi.rcpt_amt as transaction_amt from \n"
                    + " vph_rcpt_cart_fail f left outer join vahanpgi.vp_pgi_details pgi on f.transaction_no = pgi.payment_id where user_cd = ? and f.state_cd = ? and off_cd = ?  and f.moved_on between ? and (?::date + '1 day'::interval) and f.reason !~ 'no web' \n"
                    + " group by moved_on,transaction_no,reason,bank_ref_no,bank_verfied_dt,transaction_amt,transaction_id"
                    + " order by moved_on desc";

            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setLong(i++, Long.parseLong(empCd));
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setDate(i++, new java.sql.Date(fromDate.getTime()));
            ps.setDate(i++, new java.sql.Date(uptoDate.getTime()));
            ps.setLong(i++, Long.parseLong(empCd));
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setDate(i++, new java.sql.Date(fromDate.getTime()));
            ps.setDate(i++, new java.sql.Date(uptoDate.getTime()));
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

    public void checkForReverifyApplicationCountAndAmount(String transactionNo, String stateCd, int offCd, String userId, String applList) throws VahanException {
        TransactionManager tmgr = null;
        boolean status = false;
        int failedApplAmount = 0;
        int vpRcptApplAmount = 0;
        int failedApplCount = 0;
        int vpRcptApplCount = 0;
        try {
            tmgr = new TransactionManager("checkForReverifyApplicationCountAndAmount");
            String sql = "select case when (a.vpRcptApplCount = a.failApplCount) then true else false end as status,a.vpRcptApplCount,a.failApplCount,a.failAmount,a.vpRcptApplAmount \n"
                    + " from ( \n"
                    + " select count(distinct vp.appl_no) as vpRcptApplCount,sum(vp.amount+vp.surcharge+vp.penalty+vp.interest+vp.tax1+vp.tax2-vp.exempted-vp.rebate) as vpRcptApplAmount,(select count(vph.appl_no) from vph_rcpt_cart_fail vph where vph.transaction_no = ? and vph.state_cd = ? and vph.off_cd = ? and vph.user_cd = ? ) AS failApplCount, \n"
                    + "(SELECT sum(vph_brkup.ttlamount+vph_brkup.ttlsurcharge+vph_brkup.ttlpenalty+vph_brkup.ttlinterest+vph_brkup.ttltax1+vph_brkup.ttltax2-vph_brkup.ttlexempted-vph_brkup.ttlrebate) from vph_rcpt_cart_fail_breakup  vph_brkup \n"
                    + " where vph_brkup.user_cd = ? and vph_brkup.state_cd = ? and vph_brkup.off_cd = ? AND vph_brkup.transaction_no = ? group by vph_brkup.transaction_no) as failAmount"
                    + " from vp_rcpt_cart vp \n"
                    + " where vp.transaction_no is null and vp.state_cd = ? and vp.off_cd = ? and vp.user_cd =  ? and vp.appl_no in \n"
                    + " (select f.appl_no from vph_rcpt_cart_fail f where f.transaction_no = ? and f.state_cd = ?  and f.off_cd = ? and f.user_cd = ?)) as a ";

            PreparedStatement ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, transactionNo);
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setLong(i++, Long.parseLong(userId));
            ps.setLong(i++, Long.parseLong(userId));
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setString(i++, transactionNo);
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setLong(i++, Long.parseLong(userId));
            ps.setString(i++, transactionNo);
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setLong(i++, Long.parseLong(userId));
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
                throw new VahanException("Application(s) Mismatch for Payment id " + transactionNo + ". Kindly ADD Application(s) [" + applList + "] to the CART and then WITHOUT initiating the fresh payment, first Re-Verify the failed transaction if amount is already deducted from your account.");
            }

            if (failedApplAmount != vpRcptApplAmount) {
                String amountSql = "select vp.appl_no,fail.appl_no,sum(vp.amount+vp.surcharge+vp.penalty+vp.interest+vp.tax1+vp.tax2-vp.exempted-vp.rebate) as vpRcptApplAmount,\n"
                        + " sum(fail.amount+fail.surcharge+fail.penalty+fail.interest+fail.tax1+fail.tax2-fail.exempted-fail.rebate) as vphFailRcptApplAmount\n"
                        + " from vph_rcpt_cart_fail_detail fail\n"
                        + " inner join vp_rcpt_cart vp on fail.state_cd=vp.state_cd and fail.off_cd=vp.off_cd and fail.appl_no=vp.appl_no and fail.user_cd=vp.user_cd and fail.pur_cd=vp.pur_cd\n"
                        + " where fail.user_cd = ? and fail.state_cd = ? and fail.off_cd = ? and fail.transaction_no = ? group by vp.appl_no,fail.appl_no ";

                PreparedStatement psAmountSql = tmgr.prepareStatement(amountSql);
                int j = 1;
                psAmountSql.setLong(j++, Long.parseLong(userId));
                psAmountSql.setString(j++, stateCd);
                psAmountSql.setInt(j++, offCd);
                psAmountSql.setString(j++, transactionNo);
                RowSet rsAmountSql = tmgr.fetchDetachedRowSet_No_release();

                if (!rsAmountSql.next()) {
                    throw new VahanException(TableConstants.SomthingWentWrong);
                }

                rsAmountSql.last();

                if ((rsAmountSql.getRow() != failedApplCount) || (rsAmountSql.getRow() != vpRcptApplCount)) {
                    throw new VahanException("Application(s) Mismatch for Payment id " + transactionNo + ". Kindly ADD Application(s) [" + applList + "] to the CART and then WITHOUT initiating the fresh payment, first Re-Verify the failed transaction if amount is already deducted from your account.");
                }

                rsAmountSql.beforeFirst();

                int counter = 0;
                String updateSql = "update vp_rcpt_cart set amount=fail.amount,surcharge=fail.surcharge,penalty=fail.penalty,interest=fail.interest,tax1=fail.tax1,tax2=fail.tax2,exempted=fail.exempted,rebate=fail.rebate\n"
                        + "from vph_rcpt_cart_fail_detail fail\n"
                        + "inner join vp_rcpt_cart vp on fail.state_cd=vp.state_cd and fail.off_cd=vp.off_cd and fail.appl_no=vp.appl_no and fail.user_cd=vp.user_cd  and fail.pur_cd=vp.pur_cd\n"
                        + " where fail.user_cd = ? and fail.state_cd = ? and fail.off_cd = ? and fail.appl_no = ? and fail.transaction_no = ? and vp_rcpt_cart.appl_no = fail.appl_no and vp_rcpt_cart.pur_cd=fail.pur_cd";
                PreparedStatement psUpdate = tmgr.prepareStatement(updateSql);
                while (rsAmountSql.next()) {
                    if (rsAmountSql.getInt("vpRcptApplAmount") > rsAmountSql.getInt("vphFailRcptApplAmount")) {
                        counter++;
                        int k = 1;
                        psUpdate.setLong(k++, Long.parseLong(userId));
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
            LOGGER.error("for TransactionNo " + transactionNo + " " + e);
            throw new VahanException("Problem in Reverifcation");
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

    public String getTransactionNumber(String stateCd, String applicationNumberList) throws VahanException {
        String paymentInward = "";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getTransactionNumber()");
            Object[] validate = this.updateTransactionNumber(tmgr, stateCd, applicationNumberList);
            Integer updatedCount = (Integer) validate[0];
            String transNo = (String) validate[1];
            if (updatedCount > 0 && transNo != null && !transNo.equals("")) {
                paymentInward = transNo;
                tmgr.commit();
            } else {
                throw new VahanException("Transaction No updation/generation failed!!!");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("PaymentId not generated due to technical error.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return paymentInward;
    }
}
