/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.DocumentType;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.TaxInstallCollectionDobj;
import nic.vahan.form.dobj.Tax_Pay_Dobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;

/**
 *
 * @author ankur
 */
public class TaxInstallCollectionImpl {

    private static final Logger LOGGER = Logger.getLogger(TaxInstallCollectionImpl.class);

    // Show details of UnPaid tax Installments
    public static List<TaxInstallCollectionDobj> getPendingInstallmentList(String regnNo, TaxInstallCollectionDobj installmentDobj, Owner_dobj ownerDobj) throws Exception {
        List<TaxInstallCollectionDobj> listimpl = new ArrayList();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String lastDate = "";
        int i = 1;
        String sql = " SELECT a.regn_no, a.serial_no::numeric ,a.tax_amt_instl,to_char(a.pay_due_date, 'dd-Mon-yyyy') as pay_due_date,a.pay_due_date::date as pay_due_date1,"
                + " to_char(a.pay_due_date - interval '1' day, 'dd-Mon-yyyy') as upto_next,"
                + "b.file_ref_no, b.order_iss_by, b.order_no ,to_char(b.tax_upto,'dd-Mon-yyyy') as lastuptodate, b.tax_mode, to_char(b.order_date, 'dd-Mon-yyyy') as order_date,b.appl_no,to_char(b.tax_from,'dd-Mon-yyyy') as tax_from  "
                + "from vt_tax_installment_brkup a "
                + " left join"
                + " vt_tax_installment b on b.regn_no=a.regn_no where a.regn_no= ? and a.rcpt_no IS NULL and a.state_cd = ? and a.off_cd = ? "
                + "order by serial_no ASC";
        try {
            tmgr = new TransactionManager("getPendingInstallmentList");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getUserOffCode());
            rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                TaxInstallCollectionDobj dobj = new TaxInstallCollectionDobj();
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setTablesrno(i++);
                dobj.setSerialno(rs.getString("serial_no"));
                dobj.setTaxAmountInst(rs.getLong("tax_amt_instl"));
                dobj.setTaxAmountInst1(rs.getInt("tax_amt_instl"));
                dobj.setPaydueDate(rs.getString("pay_due_date"));
                dobj.setPayDate(rs.getDate("pay_due_date1"));
                dobj.setFileRefNo(rs.getString("file_ref_no"));
                dobj.setOrderIssBy(rs.getString("order_iss_by"));
                dobj.setOrderNo(rs.getString("order_no"));
                dobj.setOrderDate(rs.getString("order_date"));
                dobj.setTaxMode(rs.getString("tax_mode"));
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setTax_From(rs.getString("tax_from"));
                lastDate = rs.getString("lastuptodate");
                if (listimpl.isEmpty()) {
                    // dobj.setSelect(false);
                    dobj.setDisablecheckbox(false);
                } else {
                    //dobj.setSelect(true);
                    dobj.setDisablecheckbox(true);
                }
                if (count > 0) {
                    listimpl.get(count - 1).setUptodate(rs.getString("upto_next"));
                }

                count++;
                listimpl.add(dobj);
            }
            if (count > 0) {
                listimpl.get(listimpl.size() - 1).setUptodate(lastDate);
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

    // Paid Installment Details
    public static List<TaxInstallCollectionDobj> getPaidInstallist(String regnNo, TaxInstallCollectionDobj installmentPaidDobj) throws Exception {
        List<TaxInstallCollectionDobj> listimpl = new ArrayList();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = " SELECT a.regn_no, a.serial_no ,a.tax_amt_instl ,a.rcpt_no ,to_char(a.pay_due_date, 'dd-Mon-yyyy') as pay_due_date,"
                + " b.file_ref_no, b.order_iss_by, b.order_no , to_char(b.order_date, 'dd-Mon-yyyy') as order_date "
                + " from vt_tax_installment_brkup a "
                + " left join"
                + " vt_tax_installment b on b.regn_no=a.regn_no where a.regn_no= ? and a.rcpt_no IS NOT NULL and a.state_cd = ? and a.off_cd = ? "
                + "order by serial_no ASC";
        try {
            tmgr = new TransactionManager("getPaidInstallist");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getUserOffCode());
            rs = ps.executeQuery();

            while (rs.next()) {
                TaxInstallCollectionDobj dobj = new TaxInstallCollectionDobj();
                dobj.setRegnNoPaid(rs.getString("regn_no"));
                dobj.setSerialnoPaid(rs.getString("serial_no"));
                dobj.setTaxAmountInstPaid(rs.getInt("tax_amt_instl"));
                dobj.setRcptNo(rs.getString("rcpt_no"));
                dobj.setPaydueDatePaid(rs.getString("pay_due_date"));
                dobj.setFileRefNo(rs.getString("file_ref_no"));
                dobj.setOrderIssBy(rs.getString("order_iss_by"));
                dobj.setOrderNo(rs.getString("order_no"));
                dobj.setOrderDate(rs.getString("order_date"));
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

    public static String saveInstallmentRcpt(Long userChg, String payMode, FeeDraftDobj feeDraftDobj, List<TaxInstallCollectionDobj> listdobj, Owner_dobj ownerDobj, Long totalTaxAmount, Long totalPenalty, PassengerPermitDetailDobj permitDobj) throws VahanException {
        TransactionManager tmgr = null;
        String rcptNo = null;
        Exception e = null;
        long inscd = 0;
        List<Tax_Pay_Dobj> listTaxDobj = new ArrayList<>();
        try {
            tmgr = new TransactionManager("saveTaxInstallmentReceipt");
            rcptNo = Receipt_Master_Impl.generateNewRcptNo(Util.getSelectedSeat().getOff_cd(), tmgr);
            String sql = "Select rcpt_no from " + TableList.VT_FEE + " where rcpt_no=? and state_cd=? and off_cd=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcptNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getUserOffCode());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                throw new VahanException("Receipt No " + rcptNo + " is already assigned to Fee");
            }

            String applNo = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            Tax_Pay_Dobj taxDobj = null;
            String tax_From = null;
            for (TaxInstallCollectionDobj taxInstallcollDobjn : listdobj) {
                if (taxInstallcollDobjn.getSerialno().equals("1")) {
                    tax_From = taxInstallcollDobjn.getTax_From();
                }
                sql = "UPDATE " + TableList.VT_TAX_INSTALLMENT_BRKUP + " SET RCPT_NO = ?"
                        + " WHERE REGN_NO = ? and SERIAL_NO = ? and state_cd = ? and off_cd = ?";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, rcptNo);
                ps.setString(2, taxInstallcollDobjn.getRegnNo());
                ps.setString(3, taxInstallcollDobjn.getSerialno());
                ps.setString(4, Util.getUserStateCode());
                ps.setInt(5, Util.getUserOffCode());
                ps.executeUpdate();
            }

            if (listdobj != null && !listdobj.isEmpty()) {
                TaxInstallCollectionDobj taxInstallcollDobjn = listdobj.get(listdobj.size() - 1);
                if (taxDobj == null) {

                    taxDobj = new Tax_Pay_Dobj();//VT_TAX
                    taxDobj.setApplNo(applNo);
                    taxDobj.setTaxMode("I");
                    taxDobj.setRegnNo(taxInstallcollDobjn.getRegnNo());
                    taxDobj.setRcptNo(rcptNo);
                    taxDobj.setFinalTaxAmount(totalTaxAmount);
                    taxDobj.setTotalPaybalePenalty(totalPenalty);
                    if (tax_From != null && !tax_From.equals("")) {
                        taxDobj.setFinalTaxFrom(tax_From);
                    } else {
                        taxDobj.setFinalTaxFrom(taxInstallcollDobjn.getPaydueDate());
                    }
                    taxDobj.setFinalTaxUpto(taxInstallcollDobjn.getUptodate());
                    taxDobj.setPaymentMode(payMode);
                    taxDobj.setPur_cd(58);
                    taxDobj.setRcptDate(new java.sql.Date(DateUtils.parseDate(taxInstallcollDobjn.getPaydueDate()).getTime()));


                    ////
                    List<DOTaxDetail> listTaxBreakUp = new ArrayList<>();//VT_TAX_BREAKUP
                    DOTaxDetail taxBreakup = new DOTaxDetail();
                    taxBreakup.setTAX_FROM(taxInstallcollDobjn.getPaydueDate());
                    taxBreakup.setTAX_UPTO(taxInstallcollDobjn.getUptodate());
                    taxBreakup.setAMOUNT((double) (taxDobj.getFinalTaxAmount() - taxDobj.getTotalPaybalePenalty()));
                    taxBreakup.setPENALTY((double) taxDobj.getTotalPaybalePenalty());
                    taxBreakup.setPUR_CD(58);
                    taxBreakup.setPRV_ADJ(0L);
                    taxBreakup.setREBATE(0.0);
                    taxBreakup.setSURCHARGE(0.0);
                    taxBreakup.setINTEREST(0.0);
                    taxBreakup.setAMOUNT1(0.0);
                    taxBreakup.setAMOUNT2(0.0);
                    //taxBreakup.se
                    listTaxBreakUp.add(taxBreakup);

                    taxDobj.setTaxBreakDetails(listTaxBreakUp);
                    listTaxDobj.add(taxDobj);
//                rcptNo = Receipt_Master_Impl.generateNewRcptNo(Util.getSelectedSeat().getOff_cd(), tmgr);
                }
            }

            TaxServer_Impl taxServerImpl = new TaxServer_Impl();
            taxServerImpl.savetaxDetails(listTaxDobj, rcptNo, ownerDobj, permitDobj, tmgr);

            if (feeDraftDobj != null) {
                FeeDraftimpl feeDraft_impl = new FeeDraftimpl();
                feeDraftDobj.setAppl_no(applNo);
                feeDraftDobj.setRcpt_no(rcptNo);
                inscd = feeDraft_impl.saveDraftDetails(feeDraftDobj, tmgr);
            }
            FeeImpl feeImpl = new FeeImpl();
            FeeImpl.PaymentGenInfo paymentInfo = feeImpl.getPaymentInfo(listTaxDobj, feeDraftDobj, userChg);
            feeImpl.saveRecptInstMap(inscd, applNo, rcptNo, paymentInfo, ownerDobj, tmgr, null);
            ServerUtil.insertForQRDetails(applNo, null, null, rcptNo, false, DocumentType.RECEIPT_QR, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tmgr);
            ps.executeUpdate();
            tmgr.commit();
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            e = ee;
        } finally {
            try {
                tmgr.release();
            } catch (Exception ee) {
                LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            }
        }

        if (e != null) {
            throw new VahanException("Receipt Number Generation Failed");
        }
        return rcptNo;
    }

    public static String reconfigureInstallment(String regn_no, List<TaxInstallCollectionDobj> listdobj, Owner_dobj ownerDobj, int totalpaid) throws VahanException {
        TransactionManager tmgr = null;
        Exception e = null;
        String flag = "";
        List<Tax_Pay_Dobj> listTaxDobj = new ArrayList<>();
        try {
            PreparedStatement ps = null;
            tmgr = new TransactionManager("saveInstallment");
            String sql = "";
            int serial_no = totalpaid;;
            serial_no++;
            List<TaxInstallCollectionDobj> listpending = getPendingInstallmentList(regn_no, null, ownerDobj);
            for (TaxInstallCollectionDobj taxInstallcollDobjn : listpending) {
                sql = "INSERT INTO " + TableList.VH_TAX_INSTALLMENT_BRKUP + "("
                        + " state_cd, off_cd,regn_no, serial_no, tax_amt_instl, pay_due_date, rcpt_no, user_cd, op_dt, "
                        + " moved_on,moved_by)"
                        + " SELECT state_cd,off_cd,regn_no, serial_no, tax_amt_instl, pay_due_date, rcpt_no, user_cd, op_dt,"
                        + " current_timestamp,?"
                        + " FROM " + TableList.VT_TAX_INSTALLMENT_BRKUP + " WHERE REGN_NO = ? and SERIAL_NO = ? and state_cd = ? and off_cd = ? and rcpt_no IS NULL";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, taxInstallcollDobjn.getRegnNo());
                ps.setString(3, taxInstallcollDobjn.getSerialno());
                ps.setString(4, Util.getUserStateCode());
                ps.setInt(5, Util.getUserOffCode());
                ps.executeUpdate();
            }
            sql = "Delete from " + TableList.VT_TAX_INSTALLMENT_BRKUP + ""
                    + " WHERE REGN_NO = ? and state_cd = ? and off_cd = ? and rcpt_no IS NULL";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getUserOffCode());
            ps.executeUpdate();
            for (TaxInstallCollectionDobj taxInstallcollDobjn : listdobj) {
                sql = "INSERT INTO " + TableList.VT_TAX_INSTALLMENT_BRKUP + "(state_cd, off_cd, regn_no, serial_no, tax_amt_instl, pay_due_date, user_cd, op_dt) VALUES (?, ?, ?, ?, ?,?, ?, current_timestamp)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getUserOffCode());
                ps.setString(3, regn_no);
                ps.setString(4, serial_no + "");
                ps.setLong(5, taxInstallcollDobjn.getTaxAmountInst());
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                Date returndate = formatter.parse(taxInstallcollDobjn.getPaydueDate());
                ps.setDate(6, new java.sql.Date(returndate.getTime()));
                ps.setString(7, Util.getEmpCode());
                ps.executeUpdate();
                serial_no++;
            }
            tmgr.commit();
            flag = "Success";
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            e = ee;
        } finally {
            try {
                tmgr.release();
            } catch (Exception ee) {
                LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            }
        }

        if (e != null) {
            throw new VahanException("Reconfigure Installment Failed");
        }
        return flag;
    }

    public String getCheckInstallmentList(String regnNo, String stateCd) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        String sql = " SELECT a.regn_no "
                + " from vt_tax_installment_brkup a "
                + " left join"
                + " vt_tax_installment b on b.regn_no=a.regn_no where a.regn_no= ? and a.rcpt_no IS NULL and a.state_cd = ? "
                + "order by serial_no ASC limit 1";
        try {
            tmgr = new TransactionManagerReadOnly("getCheckInstallmentList");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("regn_no");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ee) {
                    LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
                }
            }
        }
        return null;
    }

    public static List<TaxInstallCollectionDobj> getOnlineInstallmentList(String applNo, String stateCd) throws VahanException {
        List<TaxInstallCollectionDobj> listimpl = new ArrayList();
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        int i = 1;
        try {
            String sql = " select va.regn_no,va.serial_no,(va.tax_amt_instl-vt.tax_amt_instl) as penalty, vt.tax_amt_instl ,va.pay_due_date,va.appl_no,\n"
                    + " to_char(va.pay_due_date, 'dd-Mon-yyyy') as pay_due_date1, vtins.tax_mode, vtins.tax_from , sum(vp.amount+ vp.penalty) as total\n"
                    + " from vp_rcpt_cart vp\n"
                    + " inner join vahan4.va_tax_installment_brkup va on va.state_cd=vp.state_cd and va.off_cd=vp.off_cd and va.appl_no = vp.appl_no\n"
                    + " left join vahan4.vt_tax_installment_brkup vt on vt.state_cd=va.state_cd and vt.off_cd=va.off_cd and vt.regn_no = va.regn_no and vt.serial_no = va.serial_no\n"
                    + " left join vt_tax_installment vtins on vtins.state_cd=va.state_cd and vtins.regn_no=va.regn_no\n"
                    + " where vp.appl_no=? and vp.state_cd = ? group by 1,2,3,4,5,6,7,8,9 order by serial_no ASC";

            tmgr = new TransactionManagerReadOnly("getOnlineInstallmentList");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, stateCd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                TaxInstallCollectionDobj dobj = new TaxInstallCollectionDobj();
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setTablesrno(i++);
                dobj.setSerialno(rs.getString("serial_no"));
                dobj.setTaxAmountInst(rs.getLong("tax_amt_instl"));
                dobj.setTaxAmountInst1(rs.getInt("total"));
                dobj.setPaydueDate(rs.getString("pay_due_date1"));
                dobj.setPenalty(rs.getLong("penalty"));
                dobj.setPayDate(rs.getDate("pay_due_date"));
                dobj.setTaxMode(rs.getString("tax_mode"));
                dobj.setAppl_no(applNo);
                dobj.setSelect(true);
                dobj.setTax_From(rs.getString("tax_from"));
                dobj.setDisablecheckbox(true);
                listimpl.add(dobj);
            }
        } catch (SQLException ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            throw new VahanException("Problem in getting online insitiated tax details !!!");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ee) {
                LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            }
        }
        return listimpl;
    }
}
