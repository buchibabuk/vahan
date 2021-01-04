/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.java.util.CommonUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.RefundAndExcessDobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author GULSHAN
 */
public class BalanceTaxUpdateDeleteImpl {

    private static final Logger LOGGER = Logger.getLogger(BalanceTaxUpdateDeleteImpl.class);

    public List<RefundAndExcessDobj> getBalanceTaxDetailsForUpdate(String regnNo, int offCd, String stateCd) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        List<RefundAndExcessDobj> refList = new ArrayList<>();
        RefundAndExcessDobj dobj = null;
        try {

            String sql = "SELECT * from  vahan4.vt_refund_excess where regn_no=? and state_cd=? and (road_tax_appl_no is null and bal_tax_appl_no is null)";
            tmgr = new TransactionManagerReadOnly("getBalanceTaxDetailsForUpdate");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new RefundAndExcessDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setPur_cd(rs.getInt("pur_cd"));
                dobj.setPurCdDescr(ServerUtil.getTaxHead(rs.getInt("pur_cd")));
                dobj.setTaxFrom(rs.getDate("taxfrom"));
                dobj.setTaxUpto(rs.getDate("taxupto"));
                dobj.setRefundAmt(rs.getInt("refund_amt"));
                dobj.setExcessAmt(rs.getInt("excess_amt"));
                dobj.setOp_dt(rs.getDate("op_dt"));
                dobj.setBalanceAmt(rs.getInt("balance_amt"));
                dobj.setRcptNo(rs.getString("rcpt_no"));//bal_tax_appl_no
                dobj.setRcptDt(rs.getDate("rcpt_dt"));
                dobj.setRoadTaxApplNo(rs.getString("road_tax_appl_no"));
                dobj.setBalTaxApplNo(rs.getString("bal_tax_appl_no"));
                refList.add(dobj);
            }
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
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
        }
        return refList;
    }

    public void updateDelBalaceTaxAmt(List<RefundAndExcessDobj> refunMdfyDobj, List<RefundAndExcessDobj> clonedRefunList, String empCode) throws VahanException {
        TransactionManager tmgr = null;
        try {

            if (clonedRefunList.isEmpty()) {
                throw new VahanException("Balance Tax List is empty!");
            }
            PreparedStatement ps = null;
            tmgr = new TransactionManager("updateBalaceTaxAmt");
            String qry = "";
            RefundAndExcessDobj orderRefunDoj = null;
            if (!clonedRefunList.isEmpty()) {
                orderRefunDoj = clonedRefunList.get(0);
            }
            for (RefundAndExcessDobj refundDobj : clonedRefunList) {
                if (refunMdfyDobj != null && refunMdfyDobj.isEmpty()) {
                    qry = "INSERT INTO vh_refund_excess(\n"
                            + "            state_cd, off_cd, appl_no, regn_no, pur_cd, taxfrom, taxupto, \n"
                            + "            refund_amt, exempt_amt, balance_amt, op_dt, moved_on, moved_by, order_date, order_issue_by, order_no,remark)\n"
                            + "    select state_cd, off_cd, appl_no, regn_no, pur_cd, taxfrom, taxupto, \n"
                            + "            refund_amt, excess_amt, balance_amt, op_dt,current_timestamp,? as moved_by , ?, ?, ?,? from vt_refund_excess  where regn_no=? and off_cd=? and state_cd=? and pur_cd=? and (road_tax_appl_no is null and bal_tax_appl_no is null)";
                    ps = tmgr.prepareStatement(qry);

                    ps.setString(1, empCode);
                    if (orderRefunDoj.getOrderDate() != null) {
                        ps.setDate(2, new java.sql.Date(orderRefunDoj.getOrderDate().getTime()));
                    } else {
                        ps.setDate(2, null);
                    }
                    ps.setString(3, orderRefunDoj.getOrderIssueBy());
                    ps.setString(4, orderRefunDoj.getOrderNo());
                    ps.setString(5, "Delete-" + orderRefunDoj.getRemark());
                    ps.setString(6, refundDobj.getRegn_no());
                    ps.setInt(7, refundDobj.getOff_cd());
                    ps.setString(8, refundDobj.getState_cd());
                    ps.setInt(9, refundDobj.getPur_cd());
                    ps.executeUpdate();

                } else {
                    qry = "INSERT INTO vh_refund_excess(\n"
                            + "            state_cd, off_cd, appl_no, regn_no, pur_cd, taxfrom, taxupto, \n"
                            + "            refund_amt, exempt_amt, balance_amt, op_dt, moved_on, moved_by, order_date, order_issue_by, order_no,remark)\n"
                            + "    select state_cd, off_cd, appl_no, regn_no, pur_cd, taxfrom, taxupto, \n"
                            + "            refund_amt, excess_amt, balance_amt, op_dt,current_timestamp,? as moved_by , order_date, order_issue_by, order_no,remark from vt_refund_excess  where regn_no=? and off_cd=? and state_cd=? and pur_cd=? and (road_tax_appl_no is null and bal_tax_appl_no is null)";
                    ps = tmgr.prepareStatement(qry);
                    ps.setString(1, empCode);
                    ps.setString(2, refundDobj.getRegn_no());
                    ps.setInt(3, refundDobj.getOff_cd());
                    ps.setString(4, refundDobj.getState_cd());
                    ps.setInt(5, refundDobj.getPur_cd());
                    ps.executeUpdate();
                }
                qry = "delete from vahan4.vt_refund_excess where  regn_no=? and off_cd=? and state_cd=? and pur_cd=? and (road_tax_appl_no is null and bal_tax_appl_no is null)";
                ps = tmgr.prepareStatement(qry);
                ps.setString(1, refundDobj.getRegn_no());
                ps.setInt(2, refundDobj.getOff_cd());
                ps.setString(3, refundDobj.getState_cd());
                ps.setInt(4, refundDobj.getPur_cd());
                ps.executeUpdate();
            }
            for (RefundAndExcessDobj refundDobj : refunMdfyDobj) {
                qry = "INSERT INTO vt_refund_excess(\n"
                        + "            state_cd, off_cd, appl_no, regn_no, pur_cd, taxfrom, taxupto, \n"
                        + "            refund_amt, excess_amt, balance_amt, rcpt_no, rcpt_dt, road_tax_appl_no, \n"
                        + "            bal_tax_appl_no, op_dt, order_date, order_issue_by, order_no, \n"
                        + "            remark)\n"
                        + "    VALUES (?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?,?, current_timestamp, ?, ?, ?,?)";
                ps = tmgr.prepareStatement(qry);
                ps.setString(1, refundDobj.getState_cd());
                ps.setInt(2, refundDobj.getOff_cd());
                ps.setString(3, refundDobj.getAppl_no());
                ps.setString(4, refundDobj.getRegn_no());
                ps.setInt(5, refundDobj.getPur_cd());
                ps.setDate(6, new java.sql.Date(refundDobj.getTaxFrom().getTime()));
                ps.setDate(7, new java.sql.Date(refundDobj.getTaxUpto().getTime()));
                ps.setInt(8, refundDobj.getRefundAmt());
                ps.setInt(9, refundDobj.getExcessAmt());
                ps.setInt(10, refundDobj.getBalanceAmt());
                ps.setString(11, refundDobj.getRcptNo());
                if (refundDobj.getRcptDt() != null) {
                    ps.setDate(12, new java.sql.Date(refundDobj.getRcptDt().getTime()));
                } else {
                    ps.setDate(12, null);
                }
                if (CommonUtils.isNullOrBlank(refundDobj.getRoadTaxApplNo())) {
                    ps.setString(13, null);
                }
                if (CommonUtils.isNullOrBlank(refundDobj.getRoadTaxApplNo())) {
                    ps.setString(14, null);
                }

                if (orderRefunDoj.getOrderDate() != null) {
                    ps.setDate(15, new java.sql.Date(orderRefunDoj.getOrderDate().getTime()));
                } else {
                    ps.setDate(15, null);
                }
                ps.setString(16, orderRefunDoj.getOrderIssueBy());
                ps.setString(17, orderRefunDoj.getOrderNo());
                ps.setString(18, orderRefunDoj.getRemark());

                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            }
            tmgr.commit();

        } catch (VahanException ex) {
            throw ex;
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
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
        }
    }
}