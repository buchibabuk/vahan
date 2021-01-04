/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.RefundAndExcessDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TaxClearDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import nic.vahan5.reg.server.ServerUtility;
import org.apache.log4j.Logger;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;

/**
 * Created this class as function updateAppl_noInVt_Refund_Excess() of the
 * original class couldn't be called from outside the package, perhaps it was
 * kept that way to prevent a security breach.
 *
 * @author Kartikey Singh
 */
public class TaxClearImplementation {

    private static final Logger LOGGER = Logger.getLogger(TaxClearImplementation.class);

    public List<TaxClearDobj> getTaxClearDetails(String regn_no, String appl_no) {
        List<TaxClearDobj> listTaxClearDobj = new ArrayList<>();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        TaxClearDobj dobj = null;
        try {
            tmgr = new TransactionManager("TaxClearImpl");
            ps = tmgr.prepareStatement("SELECT * FROM " + TableList.VA_TAX_CLEAR + " WHERE regn_no=? and appl_no=?");
            ps.setString(1, regn_no);
            ps.setString(2, appl_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) // found
            {
                dobj = new TaxClearDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegnno(rs.getString("regn_no"));
                dobj.setPur_cd(rs.getInt("pur_cd"));
                dobj.setClear_fr(rs.getDate("clear_fr"));
                dobj.setTaxclearuptodt(rs.getDate("clear_to"));
                dobj.setOrderno(rs.getString("tcr_no"));
                dobj.setClearby(rs.getString("iss_auth"));
                dobj.setOrderdt(rs.getDate("iss_dt"));
                dobj.setRemarks(rs.getString("remark"));
                dobj.setRecieptDate(rs.getDate("rcpt_dt"));
                dobj.setRecieptNo(rs.getString("rcpt_no"));
                dobj.setClear_fr(rs.getDate("clear_fr"));
                listTaxClearDobj.add(dobj);
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return listTaxClearDobj;

    }

    public String InsertIntoVATaxClearInward(Status_dobj status, TaxClearDobj taxClearDobj, List selectedPur_cd, List<RefundAndExcessDobj> refund_list, boolean renderClearPanel, boolean renderTaxPanel) throws VahanException, Exception {
        String appl_no = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        int action_cd = 0;
        int actionCodeArray[] = null;
        try {
            tmgr = new TransactionManager("InsertIntoVATaxClear");
            appl_no = ServerUtil.getUniqueApplNo(tmgr, status.getState_cd());//generate a new application no.
            if (renderClearPanel) {
                sql = "INSERT INTO " + TableList.VA_TAX_CLEAR + "(state_cd, off_cd, regn_no,appl_no, pur_cd,"
                        + "  clear_to, tcr_no, iss_auth, iss_dt, remark, user_cd, op_dt,clear_fr,rcpt_no,rcpt_dt)"
                        + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  ?, current_timestamp,?,?,?)";


                for (Object pur_cd : selectedPur_cd) {
                    int i = 1;
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(i++, status.getState_cd());
                    ps.setInt(i++, status.getOff_cd());
                    ps.setString(i++, taxClearDobj.getRegnno());
                    ps.setString(i++, appl_no);
                    ps.setInt(i++, Integer.valueOf(pur_cd.toString()));
                    ps.setDate(i++, new java.sql.Date(taxClearDobj.getTaxclearuptodt().getTime()));
                    ps.setString(i++, taxClearDobj.getOrderno());
                    ps.setString(i++, taxClearDobj.getClearby());
                    ps.setDate(i++, new java.sql.Date(taxClearDobj.getOrderdt().getTime()));
                    ps.setString(i++, taxClearDobj.getRemarks());
                    ps.setString(i++, Util.getEmpCode());
                    ps.setDate(i++, new java.sql.Date(taxClearDobj.getClear_fr().getTime()));
                    ps.setString(i++, taxClearDobj.getRecieptNo());
                    if (taxClearDobj.getRecieptDate() != null) {
                        ps.setDate(i++, new java.sql.Date(taxClearDobj.getRecieptDate().getTime()));
                    } else {
                        ps.setDate(i++, null);
                    }

                    ps.executeUpdate();
                }
            }
            if (renderTaxPanel) {
                insertVaRefundAndExcessDetails(status.getState_cd(), status.getOff_cd(), appl_no, taxClearDobj.getRegnno(), refund_list, tmgr);
            }
            actionCodeArray = ServerUtil.getInitialAction(tmgr, status.getState_cd(), status.getPur_cd(), null);
            if (actionCodeArray == null) {
                throw new VahanException("Initial Action Code is Not Available!");
            }
            action_cd = actionCodeArray[0];
            status.setAppl_no(appl_no);
            status.setAction_cd(action_cd);//Initial Action_cd
            status.setAppl_date(ServerUtil.getSystemDateInPostgres());
            ServerUtil.insertIntoVaStatus(tmgr, status);
            ServerUtil.insertIntoVaDetails(tmgr, status);

            status.setStatus(TableConstants.STATUS_COMPLETE);
            status = ServerUtil.webServiceForNextStage(status, tmgr);
            ServerUtil.fileFlow(tmgr, status); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();//Commiting data here....


        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error In Application Number Generation");
        } catch (VahanException ex) {
            throw ex;
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }
        return appl_no;
    }

    public static void insertVaRefundAndExcessDetails(String state_cd, int off_cd, String appl_no, String regn_no, List<RefundAndExcessDobj> refund_list, TransactionManager tmgr) throws VahanException {
        String sql;
        PreparedStatement ps = null;
        try {
            sql = "INSERT INTO " + TableList.VA_REFUND_EXCESS + "(\n"
                    + "            state_cd, off_cd, appl_no, regn_no, pur_cd,taxfrom, taxupto, refund_amt, excess_amt,op_dt,balance_amt)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp,?);";
            for (RefundAndExcessDobj dobj : refund_list) {
                if (dobj.getPur_cd() != -1) {
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, state_cd);
                    ps.setInt(2, off_cd);
                    ps.setString(3, appl_no);
                    ps.setString(4, regn_no);
                    ps.setInt(5, dobj.getPur_cd());
                    ps.setDate(6, new java.sql.Date(dobj.getTaxFrom().getTime()));
                    ps.setDate(7, new java.sql.Date(dobj.getTaxUpto().getTime()));
                    ps.setInt(8, dobj.getRefundAmt());
                    ps.setInt(9, dobj.getExcessAmt());
                    ps.setInt(10, dobj.getBalanceAmt());
                    ps.execute();
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error In Saving Refund And Excess Details");
        }
    }

    public static void updateVaRefundAndExcessDetails(TaxClearDobj taxClearDobj, List<RefundAndExcessDobj> refund_list, TransactionManager tmgr) throws VahanException {
        String sql;
        PreparedStatement ps = null;
        try {
            sql = "UPDATE " + TableList.VA_REFUND_EXCESS + " \n"
                    + "   SET taxfrom =?, taxupto=?, refund_amt=?, \n"
                    + "       excess_amt=?,balance_amt=?, op_dt=current_timestamp\n"
                    + " WHERE state_cd=? and off_cd=? and appl_no=? and regn_no=? and pur_cd=?";
            for (RefundAndExcessDobj dobj : refund_list) {
                ps = tmgr.prepareStatement(sql);
                ps.setDate(1, new java.sql.Date(dobj.getTaxFrom().getTime()));
                ps.setDate(2, new java.sql.Date(dobj.getTaxUpto().getTime()));
                ps.setInt(3, dobj.getRefundAmt());
                ps.setInt(4, dobj.getExcessAmt());
                ps.setInt(5, dobj.getBalanceAmt());
                ps.setString(6, Util.getUserStateCode());
                ps.setInt(7, Util.getSelectedSeat().getOff_cd());
                ps.setString(8, taxClearDobj.getAppl_no());
                ps.setString(9, taxClearDobj.getRegnno());
                ps.setInt(10, dobj.getPur_cd());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error In Updating Refund And Excess Details");
        }
    }

    public static void insertRefundAndExcessDetailsHistory(TaxClearDobj taxClearDobj, List<RefundAndExcessDobj> refund_list, TransactionManager tmgr) throws VahanException {
        String sql;
        PreparedStatement ps = null;
        try {
            sql = "INSERT INTO " + TableList.VHA_REFUND_EXCESS + " (\n"
                    + "            moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, pur_cd,taxfrom, taxupto, \n"
                    + "            refund_amt, excess_amt, op_dt,balance_amt)\n"
                    + "   SELECT current_timestamp as moved_on, ? as moved_by ,state_cd, off_cd, appl_no, regn_no, pur_cd,taxfrom, taxupto, refund_amt, excess_amt, \n"
                    + "       op_dt,balance_amt\n"
                    + "  FROM " + TableList.VA_REFUND_EXCESS + " where state_cd=? and off_cd=? and appl_no=? and regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            ps.setString(4, taxClearDobj.getAppl_no());
            ps.setString(5, taxClearDobj.getRegnno());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error In Moving Data in History Table");
        }
    }

    public static void insertVtRefundAndExcessDetails(TaxClearDobj taxClearDobj, List<RefundAndExcessDobj> refund_list, TransactionManager tmgr, Status_dobj status_dobj) throws VahanException {
        String sql;
        PreparedStatement ps = null;
        try {
            sql = "INSERT INTO " + TableList.VT_REFUND_EXCESS + " (\n"
                    + "            state_cd, off_cd, appl_no, regn_no, pur_cd,taxfrom, taxupto, refund_amt, excess_amt, \n"
                    + "            op_dt,balance_amt)\n"
                    + "            SELECT state_cd, off_cd, appl_no, regn_no, pur_cd,taxfrom, taxupto, refund_amt, excess_amt, \n"
                    + "       current_timestamp,balance_amt\n"
                    + "  FROM " + TableList.VA_REFUND_EXCESS + " where state_cd=? and off_cd=? and appl_no=? and regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, taxClearDobj.getAppl_no());
            ps.setString(4, taxClearDobj.getRegnno());
            ps.executeUpdate();
            sql = "DELETE FROM " + TableList.VA_REFUND_EXCESS + " \n"
                    + " where state_cd=? and off_cd=? and appl_no=? and regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, taxClearDobj.getAppl_no());
            ps.setString(4, taxClearDobj.getRegnno());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error In approval Refund And Excess Details");
        }
    }

    // Get details of Paid tax from VT_TAX 
    public static List<TaxClearDobj> getTaxDetaillist(TaxClearDobj clearDobj, Map<String, Integer> purCodeList) throws Exception {
        List<TaxClearDobj> listimpl = new ArrayList();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select rcpt_no ,tax_from, to_char(tax_from, 'dd-Mon-yyyy') as tax_from_descr , "
                + "to_char(tax_upto, 'dd-Mon-yyyy') as tax_upto,a.pur_cd, b.descr ,to_char(rcpt_dt,'dd-Mon-yyyy') as rcpt_dt,"
                + "tax_amt , tax_fine "
                + " from " + TableList.VT_TAX + " a"
                + " left join " + TableList.TM_PURPOSE_MAST + " b on b.pur_cd=a.pur_cd"
                + " where a.regn_no=? and a.pur_cd=? and a.state_cd = ?"
                + " order by rcpt_dt desc";
        try {
            tmgr = new TransactionManager("getTaxDetaillist");

            for (Map.Entry<String, Integer> entry : purCodeList.entrySet()) {


                ps = tmgr.prepareStatement(sql);
                ps.setString(1, clearDobj.getRegnno());
                ps.setInt(2, entry.getValue());
                ps.setString(3, Util.getUserStateCode());
                rs = ps.executeQuery();

                while (rs.next()) {
                    TaxClearDobj dobj = new TaxClearDobj();
                    dobj.setRcpt_noold(rs.getString("rcpt_no"));
                    dobj.setClear_fr(rs.getDate("tax_from"));
                    dobj.setTax_fromold(rs.getString("tax_from_descr"));
                    dobj.setTax_uptoold(rs.getString("tax_upto"));
                    dobj.setPur_cd(rs.getInt("pur_cd"));
                    dobj.setDescrold(rs.getString("descr"));
                    dobj.setRcpt_dtold(rs.getString("rcpt_dt"));
                    dobj.setTax_amtold(rs.getString("tax_amt"));
                    dobj.setTax_fineold(rs.getString("tax_fine"));
                    listimpl.add(dobj);
                }
            }
        } catch (SQLException e) {
            throw new VahanException(e.getMessage());

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }

        return listimpl;
    }

    /**
     * Get details of Paid tax from VT_TAX Order By Tax Upto Date
     *
     * @param clearDobj
     * @param state_cd
     * @param off_cd
     * @return
     * @throws Exception
     */
    public static List<TaxClearDobj> getTaxDetaillist(String regnNo, String state_cd) throws Exception {

        List<TaxClearDobj> listimpl = new ArrayList();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select rcpt_no ,tax_from, to_char(tax_from, 'dd-Mon-yyyy') as tax_from_descr , to_char(tax_upto, 'dd-Mon-yyyy') as tax_upto,a.pur_cd, b.descr ,to_char(rcpt_dt,'dd-Mon-yyyy') as rcpt_dt, tax_amt , tax_fine, COALESCE(c.off_name, '') as off_name \n"
                + " from " + TableList.VT_TAX + " a \n"
                + " left join  " + TableList.TM_PURPOSE_MAST + " b on b.pur_cd=a.pur_cd \n"
                + " left join  " + TableList.TM_OFFICE + " c on c.state_cd=a.state_cd and c.off_cd=a.off_cd \n"
                + " where a.regn_no=? and a.state_cd = ? "
                + " order by a.rcpt_dt desc ";
        try {
            tmgr = new TransactionManager("getTaxDetaillist");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, state_cd);
            rs = ps.executeQuery();
            while (rs.next()) {
                TaxClearDobj dobj = new TaxClearDobj();
                dobj.setRcpt_noold(rs.getString("rcpt_no"));
                dobj.setClear_fr(rs.getDate("tax_from"));
                dobj.setTax_fromold(rs.getString("tax_from_descr"));
                dobj.setTax_uptoold(rs.getString("tax_upto"));
                dobj.setPur_cd(rs.getInt("pur_cd"));
                dobj.setDescrold(rs.getString("descr"));
                dobj.setRcpt_dtold(rs.getString("rcpt_dt"));
                dobj.setTax_amtold(rs.getString("tax_amt"));
                dobj.setTax_fineold(rs.getString("tax_fine"));
                dobj.setOff_name(rs.getString("off_name"));
                listimpl.add(dobj);
            }
        } catch (SQLException e) {
            throw new VahanException(e.getMessage());

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }

        return listimpl;
    }
    // the end

    // Show details of Paid tax from VH_TAX_CLEAR 
    public static List<TaxClearDobj> getHistoryTaxDetaillist(TaxClearDobj clearDobj, Map<String, Integer> purCodeList) throws Exception {
        List<TaxClearDobj> listimpl = new ArrayList();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = " select a.tcr_no,a.clear_fr, a.clear_to, a.pur_cd, a.descr, to_char(a.op_dt,'dd-Mon-yyyy') as op_date, a.mvTaxfrom, a.orRcpt_dt, a.orRcpt_no from"
                + "((select tcr_no ,to_char(clear_fr, 'dd-Mon-yyyy') as clear_fr , to_char(clear_to, 'dd-Mon-yyyy') as clear_to,a.pur_cd, b.descr , op_dt, a.clear_fr as mvTaxfrom, a.rcpt_dt as orRcpt_dt, a.rcpt_no as orRcpt_no"
                + " from " + TableList.VT_TAX_CLEAR + " a"
                + " left join " + TableList.TM_PURPOSE_MAST + " b on b.pur_cd=a.pur_cd"
                + " where regn_no=? and a.pur_cd=? and a.state_cd = ?) "
                + " UNION ALL "
                + " (select tcr_no ,to_char(clear_fr, 'dd-Mon-yyyy') as clear_fr , to_char(clear_to, 'dd-Mon-yyyy') as clear_to,a.pur_cd, b.descr , op_dt, a.clear_fr as mvTaxfrom, a.rcpt_dt as orRcpt_dt, a.rcpt_no as orRcpt_no"
                + " from " + TableList.VH_TAX_CLEAR + " a"
                + " left join " + TableList.TM_PURPOSE_MAST + " b on b.pur_cd=a.pur_cd"
                + " where regn_no=? and a.pur_cd=? and a.state_cd = ?) "
                + " ) a order by op_dt DESC";
        try {
            tmgr = new TransactionManager("getHistoryTaxDetaillist");
            for (Map.Entry<String, Integer> entry : purCodeList.entrySet()) {
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, clearDobj.getRegnno());
                ps.setInt(2, entry.getValue());
                ps.setString(3, Util.getUserStateCode());
                ps.setString(4, clearDobj.getRegnno());
                ps.setInt(5, entry.getValue());
                ps.setString(6, Util.getUserStateCode());
                rs = ps.executeQuery();

                while (rs.next()) {
                    TaxClearDobj dobj = new TaxClearDobj();
                    dobj.setRcpt_noHist(rs.getString("tcr_no"));
//                dobj.setTax_fromold((java.util.Date)rs.getDate("tax_from"));
                    dobj.setTax_fromHist(rs.getString("clear_fr"));
                    dobj.setTax_uptoHist(rs.getString("clear_to"));
                    dobj.setPur_cd(rs.getInt("pur_cd"));
                    dobj.setDescrHist(rs.getString("descr"));
                    dobj.setOp_dtHist(rs.getString("op_date"));
                    dobj.setClear_fr(rs.getDate("mvTaxfrom"));
                    dobj.setRecieptDate(rs.getDate("orRcpt_dt"));
                    dobj.setRecieptNo(rs.getString("orRcpt_no"));
                    listimpl.add(dobj);
                }
            }
        } catch (SQLException e) {
            throw new VahanException(e.getMessage());
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }

        return listimpl;
    }

    // Show details of tax Exem from VT_TAX_EXEM AND Vh_tax_exem
    // Akshay Jain 01112020
    public static List<TaxClearDobj> getExemptionTaxDetaillist(TaxClearDobj clearDobj, Map<String, Integer> purCodeList) throws Exception {
        List<TaxClearDobj> listimpl = new ArrayList();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = " select a.perm_no,a.exem_fr, a.exem_to, to_char(a.op_dt,'dd-Mon-yyyy') as op_date, a.mvTaxfrom, a.orRcpt_dt, a.orRcpt_no from"
                + "((select perm_no,to_char(exem_fr, 'dd-Mon-yyyy') as exem_fr , to_char(exem_to, 'dd-Mon-yyyy') as exem_to, op_dt, a.exem_fr as mvTaxfrom, a.perm_dt as orRcpt_dt, a.perm_no as orRcpt_no"
                + " from " + TableList.VT_TAX_EXEM + " a"
                + " where regn_no=? and a.state_cd = ?) "
                + " UNION ALL "
                + "((select perm_no,to_char(exem_fr, 'dd-Mon-yyyy') as exem_fr , to_char(exem_to, 'dd-Mon-yyyy') as exem_to, op_dt, a.exem_fr as mvTaxfrom, a.perm_dt as orRcpt_dt, a.perm_no as orRcpt_no"
                + " from " + TableList.VH_TAX_EXEM + " a"
                + " where regn_no=? and a.state_cd = ?) "
                + " )) a order by op_dt DESC";
        try {
            tmgr = new TransactionManager("getExemptionTaxDetaillist");
            for (Map.Entry<String, Integer> entry : purCodeList.entrySet()) {
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, clearDobj.getRegnno());
                //ps.setInt(2, entry.getValue());
                ps.setString(2, Util.getUserStateCode());
                ps.setString(3, clearDobj.getRegnno());
                //ps.setInt(5, entry.getValue());
                ps.setString(4, Util.getUserStateCode());
                rs = ps.executeQuery();

                while (rs.next()) {
                    TaxClearDobj dobj = new TaxClearDobj();
                    dobj.setRcpt_noHist(rs.getString("perm_no"));
//                dobj.setTax_fromold((java.util.Date)rs.getDate("tax_from"));
                    dobj.setTax_fromHist(rs.getString("exem_fr"));
                    dobj.setTax_uptoHist(rs.getString("exem_to"));
                    // dobj.setPur_cd(TableConstants.TAX_EXAMPT_PUR_CD);
//                dobj.setPur_cd(rs.getInt("pur_cd"));
                    dobj.setDescrHist("MV Tax");
                    dobj.setOp_dtHist(rs.getString("op_date"));
                    dobj.setClear_fr(rs.getDate("mvTaxfrom"));
                    dobj.setRecieptDate(rs.getDate("orRcpt_dt"));
                    dobj.setRecieptNo(rs.getString("orRcpt_no"));
                    listimpl.add(dobj);
                }
            }
        } catch (SQLException e) {
            throw new VahanException(e.getMessage());
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }

        return listimpl;
    }

    // Print Difference of Tax Details
    public static ArrayList<TaxClearDobj> getDiffOfTaxDetailsPrintDobj(TaxClearDobj clearDobj) throws VahanException, ParseException {
        TransactionManager tmgr = null;
        TaxClearDobj diff_taxdobj = null;
        ArrayList<TaxClearDobj> dataListDiff_TAX = new ArrayList<TaxClearDobj>();
        try {
            String sql = " SELECT a.regn_no as Regn_no, a.fees, a.fine, a.rcpt_no, to_char(a.rcpt_dt, 'dd-Mon-yyyy') as rcpt_dt,a.pur_cd as pur_cd,"
                    + " b.descr as pur_cd_descr "
                    + " from " + TableList.VT_FEE + " a left join " + TableList.TM_PURPOSE_MAST + " b on a.pur_cd = b.pur_cd "
                    + " where a.regn_no=? and a.pur_cd = 86 and a.state_cd = ? order by a.rcpt_dt DESC limit 5";
            tmgr = new TransactionManager("getTCCPrintDobj");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, clearDobj.getRegnno());
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                diff_taxdobj = new TaxClearDobj();
                diff_taxdobj.setFees_Diff_tax(rs.getString("fees"));
                diff_taxdobj.setFine_Diff_tax(rs.getString("fine"));
                diff_taxdobj.setRcpt_no_Diff_tax(rs.getString("rcpt_no"));
                diff_taxdobj.setRcpt_dt_Diff_tax(rs.getString("rcpt_dt"));
                dataListDiff_TAX.add(diff_taxdobj);
            }
            sql = " SELECT a.regn_no as Regn_no, a.tax_amt, a.tax_fine, a.rcpt_no, to_char(a.rcpt_dt, 'dd-Mon-yyyy') as rcpt_dt,a.pur_cd as pur_cd,"
                    + " b.descr as pur_cd_descr "
                    + " from " + TableList.VT_TAX + " a left join " + TableList.TM_PURPOSE_MAST + " b on a.pur_cd = b.pur_cd "
                    + " where a.regn_no=? and a.tax_mode='B' and a.state_cd = ? order by a.rcpt_dt DESC limit 5";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, clearDobj.getRegnno());
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                diff_taxdobj = new TaxClearDobj();
                diff_taxdobj.setFees_Diff_tax(rs.getString("tax_amt"));
                diff_taxdobj.setFine_Diff_tax(rs.getString("tax_fine"));
                diff_taxdobj.setRcpt_no_Diff_tax(rs.getString("rcpt_no"));
                diff_taxdobj.setRcpt_dt_Diff_tax(rs.getString("rcpt_dt"));
                dataListDiff_TAX.add(diff_taxdobj);
            }



        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dataListDiff_TAX;
    }

    public Map<String, Integer> getAllowedPurCodeDescr(Owner_dobj dobj, PassengerPermitDetailDobj permitDob) throws VahanException, ParseException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        VehicleParameters parameters = null;
        Map<String, Integer> allowedLabelValue = new LinkedHashMap<>();
        try {
            parameters = FormulaUtils.fillVehicleParametersFromDobj(dobj, permitDob);
            if (parameters.getREGN_DATE() != null) {
                parameters.setREGN_DATE(JSFUtils.getDateInDD_MMM_YYYY(parameters.getREGN_DATE()));
            }
            tmgr = new TransactionManager("getAllowedPurCodeDescr");

            sql = " SELECT a.pur_cd,a.descr,b.condition_formula FROM " + TableList.TM_PURPOSE_MAST + " a," + TableList.VM_ALLOWED_MODS + " b "
                    + " WHERE  a.pur_cd = b.pur_cd and b.state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getState_cd());

            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next())//found
            {
                if ("RJ,AN,UP".equals(parameters.getSTATE_CD())) {
                    allowedLabelValue.put(rs.getString("descr"), rs.getInt("pur_cd")); //label,value
                } else {
                    if (isCondition(replaceTagValues(rs.getString("condition_formula"), parameters), "getAllowedPurCodeDescr")) {
                        allowedLabelValue.put(rs.getString("descr"), rs.getInt("pur_cd")); //label,value
                    }
                }
            }

        } catch (SQLException e) {
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

        return allowedLabelValue;
    }

    public static void insertUpdateTaxClear(TransactionManager tmgr, TaxClearDobj taxClearDobj, List selectedPurCd, List<RefundAndExcessDobj> ref_list, boolean renderClearPanel, boolean renderTaxPanel) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        if (renderClearPanel) {


            sql = "SELECT regn_no FROM " + TableList.VA_TAX_CLEAR + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, taxClearDobj.getAppl_no());
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoTaxClearHistory(tmgr, taxClearDobj.getAppl_no());
                updateTaxClear(tmgr, taxClearDobj);
            } else {
                insertIntoTaxClear(tmgr, taxClearDobj, selectedPurCd);
            }
        }
        if (renderTaxPanel) {
            sql = "SELECT * FROM " + TableList.VA_REFUND_EXCESS + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, taxClearDobj.getAppl_no());
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertRefundAndExcessDetailsHistory(taxClearDobj, ref_list, tmgr);
                updateVaRefundAndExcessDetails(taxClearDobj, ref_list, tmgr);
            } else {
                insertVaRefundAndExcessDetails(Util.getUserStateCode(), taxClearDobj.getOff_cd(), taxClearDobj.getRegnno(), taxClearDobj.getAppl_no(), ref_list, tmgr);
            }

        }


    }

    public static void updateTaxClear(TransactionManager tmgr, TaxClearDobj taxClearDobj) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        //updation of va_tax_clear
        sql = " update " + TableList.VA_TAX_CLEAR
                + " set clear_to=?,"
                + " tcr_no=?,"
                + " iss_auth=?,"
                + " iss_dt=?,"
                + " remark=?,"
                + " user_cd=?,"
                + " op_dt=current_timestamp"
                + ", clear_fr=? , rcpt_dt=?, rcpt_no=?"
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setDate(i++, new java.sql.Date(taxClearDobj.getTaxclearuptodt().getTime()));
        ps.setString(i++, taxClearDobj.getOrderno());
        ps.setString(i++, taxClearDobj.getClearby());

        ps.setDate(i++, new java.sql.Date(taxClearDobj.getOrderdt().getTime()));
        ps.setString(i++, taxClearDobj.getRemarks());
        ps.setString(i++, Util.getEmpCode());
        ps.setDate(i++, new java.sql.Date(taxClearDobj.getClear_fr().getTime()));
        if (taxClearDobj.getRecieptDate() != null) {
            ps.setDate(i++, new java.sql.Date(taxClearDobj.getRecieptDate().getTime()));
        } else {
            ps.setDate(i++, null);
        }
        ps.setString(i++, taxClearDobj.getRecieptNo());
        ps.setString(i++, taxClearDobj.getAppl_no());
        ps.executeUpdate();
    } // end of update VA_TAX_CLEAR

    public static void insertIntoTaxClear(TransactionManager tmgr, TaxClearDobj taxClearDobj, List selectedPurCd) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VA_TAX_CLEAR + "(state_cd, off_cd, regn_no,appl_no, pur_cd,"
                + "  clear_to, tcr_no, iss_auth, iss_dt, remark, user_cd, op_dt, clear_fr, rcpt_no, rcpt_dt)"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp,?,?,?)";
        for (Object pur_cd : selectedPurCd) {
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, taxClearDobj.getState_cd());
            ps.setInt(i++, taxClearDobj.getOff_cd());
            ps.setString(i++, taxClearDobj.getRegnno());
            ps.setString(i++, taxClearDobj.getAppl_no());
            ps.setInt(i++, Integer.valueOf(pur_cd.toString()));
            ps.setDate(i++, new java.sql.Date(taxClearDobj.getTaxclearuptodt().getTime()));
            ps.setString(i++, taxClearDobj.getOrderno());
            ps.setString(i++, taxClearDobj.getClearby());
            ps.setDate(i++, new java.sql.Date(taxClearDobj.getOrderdt().getTime()));
            ps.setString(i++, taxClearDobj.getRemarks());
            ps.setString(i++, Util.getEmpCode());

            ps.setDate(i++, new java.sql.Date(taxClearDobj.getClear_fr().getTime()));
            ps.setString(i++, taxClearDobj.getRecieptNo());
            if (taxClearDobj.getRecieptDate() != null) {
                ps.setDate(i++, new java.sql.Date(taxClearDobj.getRecieptDate().getTime()));
            } else {
                ps.setDate(i++, null);
            }
            ps.executeUpdate();
        }
    }

    public static void insertIntoTaxClearHistory(TransactionManager tmgr, String appl_no) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        //inserting data into vha_ca from va_ca
        sql = "INSERT INTO " + TableList.VHA_TAX_CLEAR + "(\n"
                + "state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                + "tcr_no, iss_auth, iss_dt, remark, user_cd, op_dt, moved_on, moved_by, \n"
                + "rcpt_no, rcpt_dt)   \n"
                + "SELECT state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                + "tcr_no, iss_auth, iss_dt, remark, user_cd, op_dt, current_timestamp as moved_on, ? as moved_by, rcpt_no, rcpt_dt\n"
                + "FROM " + TableList.VA_TAX_CLEAR
                + " WHERE  appl_no=? and state_cd=? and off_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.setString(3, Util.getUserStateCode());
        ps.setInt(4, Util.getUserSeatOffCode());
        ps.executeUpdate();
    } // end of insertIntoCAHistory

    public static void makeChangeTaxClear(TaxClearDobj taxClearDobj, String changedata, List selectedPurCd, List<RefundAndExcessDobj> ref_list, boolean renderClearPanel, boolean renderTaxPanel) throws SQLException, Exception {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangeTaxClear");
            insertUpdateTaxClear(tmgr, taxClearDobj, selectedPurCd, ref_list, renderClearPanel, renderTaxPanel);
            ComparisonBeanImpl.updateChangedData(taxClearDobj.getAppl_no(), changedata, tmgr);
            tmgr.commit();
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }

    public void update_TAXCLEAR_Status(TaxClearDobj taxClearDobj, TaxClearDobj taxclear_dobj_prv, Status_dobj status_dobj, String changedData, List selectedPurCdList, List<RefundAndExcessDobj> ref_list, boolean renderClearPanel, boolean renderTaxPanel, int vh_class) throws VahanException, Exception {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;

        try {

            tmgr = new TransactionManager("update_TAXCLEAR_Status");
            //=================WEB SERVICES FOR NEXTSTAGE START=========//
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_TAX_CLEAR_ENTRY
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_TAX_CLEAR_VERIFICATION
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_TAX_CLEAR_APPROVAL
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_TAX_CLEAR_APPROVAL_AS) {

                if ((changedData != null && !changedData.equals("")) || taxclear_dobj_prv == null) {
                    insertUpdateTaxClear(tmgr, taxClearDobj, selectedPurCdList, ref_list, renderClearPanel, renderTaxPanel);//when there is change by user or Entry Scrutiny
                }

            }

            if ((status_dobj.getCurrent_role() == TableConstants.TM_ROLE_TAX_CLEAR_APPROVAL
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_TAX_CLEAR_APPROVAL_AS)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                if (renderClearPanel) {


                    for (Object pur_cd : selectedPurCdList) {
                        sql = "INSERT INTO " + TableList.VH_TAX_CLEAR + "(\n"
                                + "            state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                                + "            tcr_no, iss_auth, iss_dt, remark, user_cd, op_dt, moved_on, moved_by, \n"
                                + "            rcpt_no, rcpt_dt)\n"
                                + "SELECT state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                                + "       tcr_no, iss_auth, iss_dt, remark, user_cd, op_dt, current_timestamp, ?, \n"
                                + "       rcpt_no, rcpt_dt\n"
                                + "  FROM " + TableList.VT_TAX_CLEAR
                                + " WHERE regn_no=? and pur_cd=? and state_cd=?";



                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, Util.getEmpCode());
                        ps.setString(2, taxClearDobj.getRegnno());
                        ps.setInt(3, Integer.valueOf(pur_cd.toString()));
                        ps.setString(4, Util.getUserStateCode());
                        ps.executeUpdate();

                        sql = "DELETE FROM " + TableList.VT_TAX_CLEAR + " where regn_no = ? and pur_cd=? and state_cd=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, taxClearDobj.getRegnno());
                        ps.setInt(2, Integer.valueOf(pur_cd.toString()));
                        ps.setString(3, Util.getUserStateCode());
                        ps.executeUpdate();
                    }

                    sql = "INSERT INTO " + TableList.VT_TAX_CLEAR + "(\n"
                            + "            state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                            + "            tcr_no, iss_auth, iss_dt, remark, user_cd, op_dt, rcpt_no, rcpt_dt)\n"
                            + " SELECT state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                            + "       tcr_no, iss_auth, iss_dt, remark, ?, current_timestamp, rcpt_no, rcpt_dt\n"
                            + "  FROM " + TableList.VA_TAX_CLEAR
                            + " WHERE appl_no=? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, taxClearDobj.getAppl_no());
                    ps.setString(3, Util.getUserStateCode());
                    ps.setInt(4, Util.getUserSeatOffCode());
                    ps.executeUpdate();
                    //for odisha trailer
                    if (!taxClearDobj.getRegnno().equalsIgnoreCase("NEW") && "OR".contains(Util.getUserStateCode()) && TableConstants.TRACTOR_VEH_CLASS.contains("," + vh_class + ",")) {
                        String sqlOwnerDetailTrailor = "select distinct regn_no from " + TableList.VT_OWNER
                                + " where (regn_no,state_cd,off_cd) in (select regn_no,state_cd,off_cd from " + TableList.VT_SIDE_TRAILER + " where link_regn_no =? and state_cd=? and off_cd=?)";
                        ps = tmgr.prepareStatement(sqlOwnerDetailTrailor);
                        ps.setString(1, taxClearDobj.getRegnno());
                        ps.setString(2, Util.getUserStateCode());
                        ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                        RowSet rst = tmgr.fetchDetachedRowSet_No_release();
                        while (rst.next()) {
                            String trailor_regn_no = rst.getString("regn_no");
                            String vtTaxClear = "INSERT INTO " + TableList.VT_TAX_CLEAR + "(\n"
                                    + "            state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                                    + "            tcr_no,  iss_dt, remark, user_cd, op_dt, rcpt_no, rcpt_dt)\n"
                                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                                    + "            ?, CURRENT_TIMESTAMP,?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP);";

                            ps = tmgr.prepareStatement(vtTaxClear);
                            int k = 1;
                            ps.setString(k++, Util.getUserStateCode());
                            ps.setInt(k++, Util.getSelectedSeat().getOff_cd());
                            ps.setString(k++, taxClearDobj.getAppl_no());
                            ps.setString(k++, trailor_regn_no);
                            ps.setInt(k++, taxClearDobj.getPur_cd());//   pur_cd
                            ps.setDate(k++, new java.sql.Date(taxClearDobj.getClear_fr().getTime()));//tax_from
                            if (taxClearDobj.getTaxclearuptodt() != null) {
                                ps.setDate(k++, new java.sql.Date(taxClearDobj.getTaxclearuptodt().getTime()));//tax_upto
                            } else {
                                ps.setNull(k++, java.sql.Types.DATE);//tax_upto
                            }
                            ps.setString(k++, taxClearDobj.getRegnno());//tcrNo
                            ps.setString(k++, "COMBINED TRAILOR ENTRY");//remark
                            ps.setString(k++, Util.getEmpCode());
                            if (!CommonUtils.isNullOrBlank(taxClearDobj.getRecieptNo())) {
                                ps.setString(k++, taxClearDobj.getRecieptNo());
                            } else {
                                ps.setString(k++, taxClearDobj.getRegnno());
                            }
                            ps.executeUpdate();
                        }
                    }
                    ////end 
                    /**
                     * Call For Tax Defaulter
                     */
                    sql = "SELECT state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, "
                            + "       tcr_no, iss_auth, iss_dt, remark,  current_timestamp, rcpt_no, rcpt_dt "
                            + "  FROM " + TableList.VA_TAX_CLEAR
                            + " WHERE appl_no=? and state_cd=? and off_cd=?";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, taxClearDobj.getAppl_no());
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, Util.getUserSeatOffCode());
                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                    while (rs.next()) {
                        String regno = rs.getString("regn_no");
                        Date taxUpto = rs.getDate("clear_to");
                        int purCd = rs.getInt("pur_cd");
                        TaxServer_Impl.insertUpdateTaxDefaulter(regno, taxUpto, purCd, tmgr);

                    }


                    sql = "INSERT INTO " + TableList.VHA_TAX_CLEAR + "(\n"
                            + "            state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                            + "            tcr_no, iss_auth, iss_dt, remark, user_cd, op_dt, moved_on, moved_by, \n"
                            + "            rcpt_no, rcpt_dt)   \n"
                            + "SELECT state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                            + "       tcr_no, iss_auth, iss_dt, remark, user_cd, op_dt, current_timestamp+ interval '1 second' as moved_on, ? as moved_by, rcpt_no, rcpt_dt\n"
                            + "  FROM " + TableList.VA_TAX_CLEAR
                            + " WHERE  appl_no=? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, taxClearDobj.getAppl_no());
                    ps.setString(3, Util.getUserStateCode());
                    ps.setInt(4, Util.getUserSeatOffCode());
                    ps.executeUpdate();

                    sql = "DELETE FROM " + TableList.VA_TAX_CLEAR + " where appl_no = ? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, taxClearDobj.getAppl_no());
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, Util.getUserSeatOffCode());
                    ps.executeUpdate();


                }
                if (renderTaxPanel) {
                    if (ref_list != null && !ref_list.isEmpty()) {
                        insertVtRefundAndExcessDetails(taxClearDobj, ref_list, tmgr, status_dobj);
                    }
                }

                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                //for updating the status of application when it is approved end
            }

            ServerUtility.insertIntoVhaChangedData(tmgr, taxClearDobj.getAppl_no(), changedData);//for saving the data into table those are changed by the user

            ServerUtil.fileFlow(tmgr, status_dobj); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();//Commiting data here....

        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }

    }

    public Map<String, String> taxModeInfo(String regNo) throws VahanException, Exception {
        Map<String, String> taxPaid = new HashMap<>();
        try {
            taxPaid = ServerUtil.taxPaidInfo(false, regNo, 58);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return taxPaid;
    }

    public List<RefundAndExcessDobj> getRefundAndExcessDetails(String appl_no, String regn_no, String current_state_cd, int current_off_cd, int action) {
        List<RefundAndExcessDobj> ref_list = new ArrayList<>();
        RefundAndExcessDobj dobj = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String appString = "";
        String sqlString = "";
        int i = 1;
        if (appl_no != null) {
            appString = " and appl_no=? ";
        }

        if (action == TableConstants.TAX_CLEAR_PUR_CD) {
            sqlString = TableList.VA_REFUND_EXCESS;
        } else if (action == TableConstants.TM_ROLE_TAX_COLLECTION) {
            sqlString = TableList.VT_REFUND_EXCESS;
            appString = appString + "road_tax_appl_no is null";
        }
        try {
            String sql = "SELECT state_cd, off_cd, appl_no, regn_no, pur_cd, taxfrom, taxupto, refund_amt, excess_amt, op_dt,balance_amt"
                    + " FROM " + sqlString + " where regn_no=? " + appString + "  and state_cd=? and off_cd=?";
            tmgr = new TransactionManager("getRefundAndExcessDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, regn_no);
            if (appl_no != null) {
                ps.setString(i++, appl_no);
            }
            ps.setString(i++, current_state_cd);
            ps.setInt(i++, current_off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new RefundAndExcessDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setPur_cd(rs.getInt("pur_cd"));
                dobj.setTaxFrom(rs.getDate("taxfrom"));
                dobj.setTaxUpto(rs.getDate("taxupto"));
                dobj.setRefundAmt(rs.getInt("refund_amt"));
                dobj.setExcessAmt(rs.getInt("excess_amt"));
                dobj.setOp_dt(rs.getDate("op_dt"));
                dobj.setBalanceAmt(rs.getInt("balance_amt"));
                ref_list.add(dobj);
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
        return ref_list;
    }

    void updateAppl_noInVt_Refund_Excess(String applNo, int pur_cd, String regnNo, int action_code, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        String appString = "";
        String whereCondition = "";
        try {
            if (action_code == TableConstants.TM_ROLE_TAX_COLLECTION) {
                appString = "road_tax_appl_no=?";
                whereCondition = "road_tax_appl_no";
            } else if (action_code == TableConstants.TM_ROLE_BALANCE_FEE_TAX_COLLECTION) {
                appString = "bal_tax_appl_no=?";
                whereCondition = "bal_tax_appl_no";

            }
            String sql = " UPDATE " + TableList.VT_REFUND_EXCESS + " SET " + appString + " Where regn_no=?  and pur_cd=? and state_cd=? and " + whereCondition + " is null ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, regnNo);
            ps.setInt(3, pur_cd);
            ps.setString(4, Util.getUserStateCode());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error : Unable to Update Details");
        }
    }

    /*
     * @author Kartikey Singh
     * @ipdate-16Jul2020 With the latest merge, offCode is no longer required in the function.
     */
    void updateAppl_noInVt_Refund_Excess(String applNo, int pur_cd, String regnNo, int action_code, TransactionManager tmgr,
            String stateCode) throws VahanException {
        PreparedStatement ps = null;
        String appString = "";
        String whereCondition = "";
        try {
            if (action_code == TableConstants.TM_ROLE_TAX_COLLECTION) {
                appString = "road_tax_appl_no=?";
                whereCondition = "road_tax_appl_no";
            } else if (action_code == TableConstants.TM_ROLE_BALANCE_FEE_TAX_COLLECTION) {
                appString = "bal_tax_appl_no=?";
                whereCondition = "bal_tax_appl_no";

            }
            String sql = " UPDATE " + TableList.VT_REFUND_EXCESS + " SET " + appString + " Where regn_no=?  and pur_cd=? and state_cd=? and " + whereCondition + " is null ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, regnNo);
            ps.setInt(3, pur_cd);
            ps.setString(4, stateCode);
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error : Unable to Update Details");
        }
    }

    public boolean getDetailsOfBalanceTax(int pur_cd, String regn_no) throws VahanException {
        boolean isExist = false;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = "";
        try {
            sql = "SELECT * FROM vahan4.vt_refund_excess where regn_no=? and pur_cd=? and (road_tax_appl_no is null and bal_tax_appl_no is null)";
            tmgr = new TransactionManager("getDetailsOfBalanceTax");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setInt(2, pur_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isExist = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error : Unable Get  Details Of Balance Tax");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return isExist;
    }
}
