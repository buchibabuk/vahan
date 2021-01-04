/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.DeleteTaxInstallmentDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author DELL
 */
public class DeleteTaxInstallmentImpl {

    private static final Logger logger = Logger.getLogger(DeleteTaxInstallmentImpl.class);

    public int getPendingTaxIntlmnt(String regnNo) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        String sql = " SELECT count(*) as totalcreateInstlmnt,count(rcpt_no) as totalpaytaxinstlmnt "
                + " from vt_tax_installment_brkup a "
                + " left join"
                + " vt_tax_installment b on b.regn_no=a.regn_no where a.regn_no= ? and a.state_cd = ? and a.off_cd = ? ";
        try {
            tmgr = new TransactionManagerReadOnly("getPaidInstallist");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getUserOffCode());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int totalcreateInstlmnt = rs.getInt("totalcreateInstlmnt");
                int totalpaytaxinstlmnt = rs.getInt("totalpaytaxinstlmnt");
                if (totalpaytaxinstlmnt != 0 && totalcreateInstlmnt > totalpaytaxinstlmnt) {
                    throw new VahanException("Tax Installment is Pending.you can not delete Tax Installment.");
                } else if (totalpaytaxinstlmnt != 0 && totalpaytaxinstlmnt != 0 && totalpaytaxinstlmnt == totalpaytaxinstlmnt) {
                    throw new VahanException("Tax Installment has been paid. you can not delete Tax Installment.");
                }
            }
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return 0;
    }

    public String getRegnApplNoFromTaxInstallmt(String regnNo, String stateCd, int offcd) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        String sql = " select * from va_details va inner join vt_tax_installment vt on va.appl_no=vt.appl_regn_no and va.state_cd=vt.state_cd where va.regn_no=? \n"
                + "and va.state_cd =? and va.off_cd=?";
        try {
            tmgr = new TransactionManagerReadOnly("getRegnApplNoFromTaxInstallmt");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offcd);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("appl_regn_no");
            }
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return null;
    }

    public String insertOrDeleteTaxInstmnt(DeleteTaxInstallmentDobj deleteTaxInstallmentDobj) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("insertOrDeleteTaxInstmnt");
            String sql = "INSERT INTO vt_tax_installment_cancel(\n"
                    + "            state_cd, off_cd, appl_no, regn_no, remark, requested_by, requested_on, \n"
                    + "            deleted_by, deleted_on)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                    + "            ?, current_timestamp)";
            PreparedStatement pmt = tmgr.prepareStatement(sql);
            pmt.setString(1, deleteTaxInstallmentDobj.getStateCd());
            pmt.setInt(2, deleteTaxInstallmentDobj.getOffcd());
            pmt.setString(3, deleteTaxInstallmentDobj.getApplNo());
            pmt.setString(4, deleteTaxInstallmentDobj.getRegnNo());
            pmt.setString(5, deleteTaxInstallmentDobj.getRemark());
            pmt.setString(6, deleteTaxInstallmentDobj.getRequested_by());
            pmt.setDate(7, new java.sql.Date(deleteTaxInstallmentDobj.getRequestedOn().getTime()));
            pmt.setString(8, deleteTaxInstallmentDobj.getEmpCode());
            pmt.executeUpdate();
            deleteTaxInstallmentAfterGenerated(deleteTaxInstallmentDobj, tmgr);
            tmgr.commit();
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.equals(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return "";
    }

    public void deleteTaxInstallmentAfterGenerated(DeleteTaxInstallmentDobj deleteTaxInstallmentDobj, TransactionManager tmgr) throws VahanException {

        try {
            String sql = "INSERT INTO " + TableList.VH_TAX_INSTALLMENT_BRKUP + "("
                    + " state_cd, off_cd,regn_no, serial_no, tax_amt_instl, pay_due_date, rcpt_no, user_cd, op_dt, "
                    + " moved_on,moved_by)"
                    + " SELECT state_cd,off_cd,regn_no, serial_no, tax_amt_instl, pay_due_date, rcpt_no, user_cd, op_dt,"
                    + " current_timestamp,?"
                    + " FROM " + TableList.VT_TAX_INSTALLMENT_BRKUP + " WHERE REGN_NO = ? and state_cd = ? and off_cd = ?";
            PreparedStatement prstmt = tmgr.prepareStatement(sql);
            prstmt.setString(1, deleteTaxInstallmentDobj.getEmpCode());
            prstmt.setString(2, deleteTaxInstallmentDobj.getRegnNo());
            prstmt.setString(3, deleteTaxInstallmentDobj.getStateCd());
            prstmt.setInt(4, deleteTaxInstallmentDobj.getOffcd());
            prstmt.executeUpdate();

            sql = "delete from " + TableList.VT_TAX_INSTALLMENT_BRKUP + " WHERE REGN_NO = ? and state_cd = ? and off_cd = ?";
            prstmt = tmgr.prepareStatement(sql);
            prstmt.setString(1, deleteTaxInstallmentDobj.getRegnNo());
            prstmt.setString(2, deleteTaxInstallmentDobj.getStateCd());
            prstmt.setInt(3, deleteTaxInstallmentDobj.getOffcd());
            prstmt.executeUpdate();

            sql = "INSERT INTO " + TableList.VH_TAX_INSTALLMENT
                    + " SELECT state_cd, off_cd, ? as regn_no, file_ref_no, order_iss_by, order_no, "
                    + " order_date, tax_from, tax_upto, tax_mode,"
                    + "  user_cd, op_dt, current_timestamp as moved_on, ? as moved_by "
                    + "  FROM " + TableList.VT_TAX_INSTALLMENT
                    + " WHERE appl_regn_no=? and state_cd = ? and off_cd = ?";
            prstmt = tmgr.prepareStatement(sql);
            prstmt.setString(1, deleteTaxInstallmentDobj.getRegnNo());
            prstmt.setString(2, deleteTaxInstallmentDobj.getEmpCode());
            prstmt.setString(3, deleteTaxInstallmentDobj.getApplNo());
            prstmt.setString(4, deleteTaxInstallmentDobj.getStateCd());
            prstmt.setInt(5, deleteTaxInstallmentDobj.getOffcd());
            prstmt.executeUpdate();
            sql = "delete from " + TableList.VT_TAX_INSTALLMENT + " WHERE appl_regn_no = ? and state_cd = ? and off_cd = ?";
            prstmt = tmgr.prepareStatement(sql);
            prstmt.setString(1, deleteTaxInstallmentDobj.getApplNo());
            prstmt.setString(2, deleteTaxInstallmentDobj.getStateCd());
            prstmt.setInt(3, deleteTaxInstallmentDobj.getOffcd());
            prstmt.executeUpdate();
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }
}
