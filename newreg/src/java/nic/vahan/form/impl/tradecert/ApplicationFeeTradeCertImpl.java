/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.tradecert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.db.DocumentType;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.tradecert.ApplicationFeeTradeCertDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.FeeDraftimpl;
import nic.vahan.form.impl.Receipt_Master_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
//import tax.web.service.VahanTaxParameters;
import nic.vahan.form.dobj.tax.VahanTaxParameters;

/**
 *
 * @author tranC081
 */
public class ApplicationFeeTradeCertImpl {

    private static final Logger LOGGER = Logger.getLogger(ApplicationFeeTradeCertImpl.class);

    public static String getFuelTaxDetail(int purCd, String stateCd, VahanTaxParameters vehParameters) throws VahanException {
        String Tax = "";
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        String sql = " select rate_formula,condition_formula from vm_tax_slab_new tsn\n"
                + " inner join vm_tax_slab_add_new tsan on tsn.slab_code  = tsan.slab_code  and tsn.state_cd = tsan.state_cd \n"
                + " where tsn.pur_cd = ? and tsn.state_cd= ?";
        try {
            tmgr = new TransactionManager("getFuelTaxDetail");
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, purCd);
            psmt.setString(2, stateCd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (FormulaUtils.isCondition(FormulaUtils.replaceTagValues(rs.getString("condition_formula"), vehParameters), "getFuelTaxDetail")) {
                    Tax = rs.getString("rate_formula");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return Tax;
    }

    public Map getApplicationMap(String purCd) throws VahanException {
        Map applicationMap = new HashMap();
        PreparedStatement psmt = null;
        ResultSet rs = null;
        ResultSet rsApplNo = null;
        TransactionManager tmgr = null;
        String sql = " Select appl_no from va_trade_certificate";
        try {
            tmgr = new TransactionManager("getApplicationMap");
            psmt = tmgr.prepareStatement(sql);
            rsApplNo = psmt.executeQuery();
            while (rsApplNo.next()) {
                String applNo = rsApplNo.getString("appl_no");
                sql = " Select regn_no from vt_fee where regn_no = '" + applNo + "' and pur_cd = " + Integer.valueOf(purCd);
                psmt = tmgr.prepareStatement(sql);
                rs = psmt.executeQuery();
                if (!rs.next()) {
                    applicationMap.put(rsApplNo.getString("appl_no"), rsApplNo.getString("appl_no"));
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return applicationMap;
    }

    public Map getDealerMap(String stateCd, String offCd) throws VahanException {
        Map dealerMap = new HashMap();
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String sql = " Select dealer_cd,dealer_name  from " + TableList.VM_DEALER_MAST + " where state_cd = ? and off_cd = ? ";
        try {
            tmgr = new TransactionManager("getDealerMap");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, stateCd);
            psmt.setInt(2, Integer.valueOf(offCd));
            rs = psmt.executeQuery();
            while (rs.next()) {
                dealerMap.put(rs.getInt("dealer_cd"), rs.getString("dealer_name"));
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }

            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return dealerMap;
    }

    public Map getVehCatgMap(String vehClass, String dealerCd, boolean filtered) throws VahanException {
        Map vehCatgMap = new HashMap();
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String sql = " Select vch_catg from vm_vhclass_catg_map where vh_class = " + Integer.valueOf(vehClass);
        try {
            tmgr = new TransactionManager("getVehCatgMap");
            psmt = tmgr.prepareStatement(sql);
            rs = psmt.executeQuery();
            String whereClauseForVmVchCatg = "'";
            while (rs.next()) {
                whereClauseForVmVchCatg += rs.getString("vch_catg").trim() + "','";
            }
            whereClauseForVmVchCatg = whereClauseForVmVchCatg.substring(0, whereClauseForVmVchCatg.lastIndexOf(","));
            sql = " Select catg,catg_desc from vm_vch_catg where catg in (" + whereClauseForVmVchCatg + ")";
            psmt = tmgr.prepareStatement(sql);
            rs = psmt.executeQuery();
            while (rs.next()) {
                vehCatgMap.put(rs.getString("catg"), rs.getString("catg_desc"));
                if (filtered) {
                    sql = " Select vch_catg from va_trade_certificate where dealer_cd = " + dealerCd + " and vch_catg = '" + rs.getString("catg") + "'";
                    psmt = tmgr.prepareStatement(sql);
                    rs = psmt.executeQuery();
                    while (rs.next()) {
                        vehCatgMap.remove(rs.getString("vch_catg"));
                    }
                }
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }

            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return vehCatgMap;
    }

    public Map getVehClassMap(String vehType) throws VahanException {
        Map vehClassMap = new HashMap();
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String sql = " Select vh_class,descr from vm_vh_class where class_type = " + Integer.valueOf(vehType);
        try {
            tmgr = new TransactionManager("getVehClassMap");
            psmt = tmgr.prepareStatement(sql);
            rs = psmt.executeQuery();
            while (rs.next()) {
                vehClassMap.put(rs.getInt("vh_class"), rs.getString("descr"));
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return vehClassMap;
    }
    //Note:No need to pass tmgr here,it is preferable to avoid batch and even if you are using please try executing relevant block of batch 

    public String insertIntoVtFee(PreparedStatement pstmt, ApplicationFeeTradeCertDobj applicationFeeTradeCertDobj, TransactionManager tmgr) throws VahanException {
        String exceptionSegment = ": some reason ";
        String result = "SUCCESS";
        try {
            pstmt.setString(1, applicationFeeTradeCertDobj.getApplNo());
            pstmt.setString(2, applicationFeeTradeCertDobj.getPaymentMode());
            pstmt.setDouble(3, Double.valueOf(applicationFeeTradeCertDobj.getFeeCollected()));
            pstmt.setDouble(4, Double.valueOf(applicationFeeTradeCertDobj.getFineCollected()));
            pstmt.setString(5, applicationFeeTradeCertDobj.getReceiptNumber());
            pstmt.setInt(6, Integer.valueOf(applicationFeeTradeCertDobj.getPurCd()));
            pstmt.setString(7, applicationFeeTradeCertDobj.getFlag());
            pstmt.setString(8, String.valueOf(applicationFeeTradeCertDobj.getEmpCd()));
            pstmt.setString(9, applicationFeeTradeCertDobj.getStateCd());
            pstmt.setInt(10, applicationFeeTradeCertDobj.getOffCd());
            pstmt.addBatch();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            result = "FAILURE " + exceptionSegment;
            throw new VahanException("Unable to Save Data as " + exceptionSegment);
        }
        return result;
    }

    public String insertIntoVpApplRcptMapping(PreparedStatement pstmt, long inctcode, ApplicationFeeTradeCertDobj applicationFeeTradeCertDobj, TransactionManager tmgr) throws VahanException {

        String exceptionSegment = ": some reason ";
        String result = "SUCCESS";
        int ownerNameSizeInDb = 35;
        try {
            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();
            String dealerName = "";
            if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {    //using_online_schema_tc work for Odisha
                dealerName = getDealerNameOR(applicationFeeTradeCertDobj.getDealerFor(), applicationFeeTradeCertDobj.getStateCd(), applicationFeeTradeCertDobj.getOffCd());
            } else {
                dealerName = getDealerName(applicationFeeTradeCertDobj.getDealerFor(), applicationFeeTradeCertDobj.getStateCd(), applicationFeeTradeCertDobj.getOffCd());
            }
            if (dealerName.length() > ownerNameSizeInDb) {
                dealerName = dealerName.substring(0, ownerNameSizeInDb - 1);
            }
            pstmt.setString(1, applicationFeeTradeCertDobj.getStateCd());
            pstmt.setInt(2, applicationFeeTradeCertDobj.getOffCd());
            pstmt.setString(3, applicationFeeTradeCertDobj.getApplNo());
            pstmt.setString(4, applicationFeeTradeCertDobj.getReceiptNumber());
            pstmt.setString(5, dealerName);
            pstmt.setInt(6, 0); /// VH_CLASS NOT APPLICABLE FOR TRADE CERTIFICATE 
            if (applicationFeeTradeCertDobj.getFeeDraftDobj() != null) {
                pstmt.setDouble(7, inctcode); //////Instrument CD
                //pstmt.setDouble(8, applicationFeeTradeCertDobj.getFeeDraftDobj().getExcess_amount());
                //pstmt.setDouble(9, applicationFeeTradeCertDobj.getCashAmtForApplRcptMapping());
                pstmt.setDouble(8, 0); //applicationFeeTradeCertDobj.getPaymentGenInfo().getExcessAmt();
                pstmt.setDouble(9, applicationFeeTradeCertDobj.getPaymentGenInfo().getCashAmt());
            } else {
                pstmt.setDouble(7, 0); //////Instrument CD  = 0
                pstmt.setDouble(8, 0); /// Excess amount = 0
                pstmt.setDouble(9, Long.valueOf(applicationFeeTradeCertDobj.getFeeCollected()) + Long.valueOf(applicationFeeTradeCertDobj.getTaxCollected()));
            }

            pstmt.setString(10, applicationFeeTradeCertDobj.getManualFeeReceiptRemark());
            pstmt.addBatch();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            result = "FAILURE " + exceptionSegment;
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return result;
    }

    public String insertServiceChargeIntoVtFee(PreparedStatement pstmt, ApplicationFeeTradeCertDobj applicationFeeTradeCertDobj, TransactionManager tmgr) throws VahanException {
        String exceptionSegment = ": some reason ";
        String result = "SUCCESS";
        try {
            pstmt.setString(1, applicationFeeTradeCertDobj.getApplNo());
            pstmt.setString(2, applicationFeeTradeCertDobj.getPaymentMode());
            pstmt.setDouble(3, applicationFeeTradeCertDobj.getServiceCharge());
            pstmt.setDouble(4, 0);
            pstmt.setString(5, applicationFeeTradeCertDobj.getReceiptNumber());
            pstmt.setInt(6, TableConstants.VM_TRANSACTION_MAST_USER_CHARGES); //PURPOSE_CODE of service charge (99)
            pstmt.setString(7, applicationFeeTradeCertDobj.getFlag());
            pstmt.setString(8, String.valueOf(applicationFeeTradeCertDobj.getEmpCd()));
            pstmt.setString(9, applicationFeeTradeCertDobj.getStateCd());
            pstmt.setInt(10, applicationFeeTradeCertDobj.getOffCd());
            pstmt.addBatch();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            result = "FAILURE " + exceptionSegment;
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return result;
    }

    public String insertTransactionFeeIntoVtFee(PreparedStatement pstmt, ApplicationFeeTradeCertDobj applicationFeeTradeCertDobj, TransactionManager tmgr) throws VahanException {
        String exceptionSegment = ": some reason ";
        String result = "SUCCESS";
        try {
            pstmt.setString(1, applicationFeeTradeCertDobj.getApplNo());
            pstmt.setString(2, applicationFeeTradeCertDobj.getPaymentMode());
            pstmt.setDouble(3, applicationFeeTradeCertDobj.getTransactionFee());
            pstmt.setDouble(4, 0);
            pstmt.setString(5, applicationFeeTradeCertDobj.getReceiptNumber());
            pstmt.setInt(6, TableConstants.VM_TRANSACTION_MAST_ALL); //PURPOSE_CODE of transaction fees (100)
            pstmt.setString(7, applicationFeeTradeCertDobj.getFlag());
            pstmt.setString(8, String.valueOf(applicationFeeTradeCertDobj.getEmpCd()));
            pstmt.setString(9, applicationFeeTradeCertDobj.getStateCd());
            pstmt.setInt(10, applicationFeeTradeCertDobj.getOffCd());
            pstmt.addBatch();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            result = "FAILURE " + exceptionSegment;
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return result;
    }

    public String insertManualFeeReceiptAmountIntoVtFee(PreparedStatement pstmt, ApplicationFeeTradeCertDobj applicationFeeTradeCertDobj, TransactionManager tmgr) throws VahanException {
        String exceptionSegment = ": some reason ";
        String result = "SUCCESS";
        try {
            pstmt.setString(1, applicationFeeTradeCertDobj.getApplNo());
            pstmt.setString(2, "C"); // cash
            pstmt.setDouble(3, -applicationFeeTradeCertDobj.getManualFeeReceiptAmount());
            pstmt.setDouble(4, 0);
            pstmt.setString(5, applicationFeeTradeCertDobj.getReceiptNumber());
            //pstmt.setDate(6, new java.sql.Date(DateUtils.parseDate(applicationFeeTradeCertDobj.getManualFeeReceiptDate()).getTime()));
            pstmt.setInt(6, TableConstants.VM_MAST_MANUAL_RECEIPT); //PURPOSE_CODE of manual fee receipt amount
            pstmt.setString(7, applicationFeeTradeCertDobj.getFlag());
            pstmt.setString(8, String.valueOf(applicationFeeTradeCertDobj.getEmpCd()));
            pstmt.setString(9, applicationFeeTradeCertDobj.getStateCd());
            pstmt.setInt(10, applicationFeeTradeCertDobj.getOffCd());
            pstmt.addBatch();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            result = "FAILURE " + exceptionSegment;
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return result;
    }

    public String insertMiscFeeIntoVtFee(PreparedStatement pstmt, ApplicationFeeTradeCertDobj applicationFeeTradeCertDobj, TransactionManager tmgr) throws VahanException {
        String exceptionSegment = ": some reason ";
        String result = "SUCCESS";
        try {
            pstmt.setString(1, applicationFeeTradeCertDobj.getApplNo());
            pstmt.setString(2, applicationFeeTradeCertDobj.getPaymentMode());
            pstmt.setDouble(3, applicationFeeTradeCertDobj.getMiscFee());
            pstmt.setDouble(4, 0);
            pstmt.setString(5, applicationFeeTradeCertDobj.getReceiptNumber());
            pstmt.setInt(6, TableConstants.VM_TRANSACTION_MAST_MISC); //PURPOSE_CODE of Misc Fee (48)
            pstmt.setString(7, applicationFeeTradeCertDobj.getFlag());
            pstmt.setString(8, String.valueOf(applicationFeeTradeCertDobj.getEmpCd()));
            pstmt.setString(9, applicationFeeTradeCertDobj.getStateCd());
            pstmt.setInt(10, applicationFeeTradeCertDobj.getOffCd());
            pstmt.addBatch();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            result = "FAILURE " + exceptionSegment;
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return result;
    }

    public void insert_into_vt_tax(ApplicationFeeTradeCertDobj applicationFeeTradeCertDobj, TransactionManager tmgr) throws SQLException, VahanException {
        PreparedStatement pstmt;
        String Query;
        Query = "INSERT INTO vt_tax(\n"
                + "            regn_no, tax_mode, payment_mode, tax_amt, tax_fine, rcpt_no, \n"
                + "            rcpt_dt, tax_from, tax_upto, pur_cd, flag, collected_by, state_cd, \n"
                + "            off_cd)\n"
                + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                + "                 CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, \n"
                + "            ?)";

        // " CURRENT_TIMESTAMP, now(), date('now') + interval '1 year', ?, ?, ?, ?, \n"
        pstmt = tmgr.prepareStatement(Query);
        int i = 1;
        pstmt.setString(i++, applicationFeeTradeCertDobj.getApplNo());
        pstmt.setString(i++, "Y");
        pstmt.setString(i++, applicationFeeTradeCertDobj.getPaymentMode());
        double taxcollected = Double.valueOf(applicationFeeTradeCertDobj.getTaxCollected()) + applicationFeeTradeCertDobj.getSurcharge();
        pstmt.setDouble(i++, taxcollected);
        pstmt.setDouble(i++, 0);
        pstmt.setString(i++, applicationFeeTradeCertDobj.getReceiptNumber());
        //Modified in case of renew only for "RJ"
        Date validFrom = new Date();
        Calendar c = Calendar.getInstance();
        TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();

        if (tmConfigDobj.getTmTradeCertConfigDobj().isValidFromNextDayOfPrevValidUpto() /// for Rajasthan
                && applicationFeeTradeCertDobj.getPurCd().equals(String.valueOf(TableConstants.VM_TRANSACTION_TRADE_CERT_NEW))
                && !CommonUtils.isNullOrBlank(applicationFeeTradeCertDobj.getTradeCertNo())) {
            c.setTime(applicationFeeTradeCertDobj.getValidUpto());
            c.add(Calendar.DATE, 1); // Adding 1 Day
            validFrom = c.getTime();
            pstmt.setDate(i++, new java.sql.Date(validFrom.getTime()));
        } else {
            pstmt.setDate(i++, new java.sql.Date(validFrom.getTime()));
        }
        c.setTime(validFrom);
        c.add(Calendar.YEAR, 1); // Adding 1 Year
        c.add(Calendar.DATE, -1);
        Date validUpto = c.getTime();
        pstmt.setDate(i++, new java.sql.Date(validUpto.getTime()));
        //end here

        pstmt.setInt(i++, TableConstants.TRADE_CERTIFICATE_TAX);
        pstmt.setString(i++, null);
        pstmt.setString(i++, String.valueOf(applicationFeeTradeCertDobj.getEmpCd()));
        pstmt.setString(i++, applicationFeeTradeCertDobj.getStateCd());
        pstmt.setInt(i++, applicationFeeTradeCertDobj.getOffCd());
        pstmt.executeUpdate();
    }

    /////////////////////////////////
    public void insert_into_vt_tax_breakup(ApplicationFeeTradeCertDobj applicationFeeTradeCertDobj, TransactionManager tmgr) throws SQLException, VahanException {

        PreparedStatement pstmt;
        String Query;
        Query = "INSERT INTO " + TableList.VT_TAX_BREAKUP + " "
                + "      (state_cd, off_cd,rcpt_no,sr_no,tax_from,tax_upto,pur_cd, \n"
                + "      prv_adjustment,tax,exempted,rebate,surcharge,penalty, \n"
                + "      interest,tax1,tax2)\n"
                + "  VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                + "  ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        pstmt = tmgr.prepareStatement(Query);
        int i = 1;
        pstmt.setString(i++, applicationFeeTradeCertDobj.getStateCd());
        pstmt.setInt(i++, applicationFeeTradeCertDobj.getOffCd());
        pstmt.setString(i++, applicationFeeTradeCertDobj.getReceiptNumber());
        pstmt.setDouble(i++, 1);

        //Modification in case of renew only for "RJ"
        Date validFrom = new Date();
        Calendar c = Calendar.getInstance();
        TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();

        if (tmConfigDobj.getTmTradeCertConfigDobj().isValidFromNextDayOfPrevValidUpto() /// for Rajasthan
                && applicationFeeTradeCertDobj.getPurCd().equals(String.valueOf(TableConstants.VM_TRANSACTION_TRADE_CERT_NEW))
                && !CommonUtils.isNullOrBlank(applicationFeeTradeCertDobj.getTradeCertNo())) {
            c.setTime(applicationFeeTradeCertDobj.getValidUpto());
            c.add(Calendar.DATE, 1); // Adding 1 Day
            validFrom = c.getTime();
            pstmt.setDate(i++, new java.sql.Date(validFrom.getTime()));
        } else {
            pstmt.setDate(i++, new java.sql.Date(validFrom.getTime()));
        }
        c.setTime(validFrom);
        c.add(Calendar.YEAR, 1); // Adding 1 Year
        c.add(Calendar.DATE, -1);
        Date validUpto = c.getTime();
        pstmt.setDate(i++, new java.sql.Date(validUpto.getTime()));
        //Modification ends here

        pstmt.setInt(i++, TableConstants.TRADE_CERTIFICATE_TAX);
        pstmt.setDouble(i++, 0);
        pstmt.setDouble(i++, Double.valueOf(applicationFeeTradeCertDobj.getTaxCollected()));
        pstmt.setDouble(i++, 0);
        pstmt.setDouble(i++, 0);
        pstmt.setDouble(i++, Double.valueOf(applicationFeeTradeCertDobj.getSurcharge()));  /// surcharge
        pstmt.setDouble(i++, 0);
        pstmt.setDouble(i++, 0);
        pstmt.setDouble(i++, 0);
        pstmt.setDouble(i++, 0);
        pstmt.executeUpdate();

    }

    //////////////////////////////
    public String save(ApplicationFeeTradeCertDobj applicationFeeTradeCertDobj, String rcptNo) throws VahanException {
        TransactionManager tmgr = null;
        String result = ": Receipt No:";
        PreparedStatement pstmt = null;
        PreparedStatement pstmtVpAppRcptMappng = null;
        try {
            long inscd = 0;
            tmgr = new TransactionManager("ApplicationFeeTradeCert_Impl.save()");
            if (rcptNo == null || rcptNo.equals("")) {
                rcptNo = Receipt_Master_Impl.generateNewRcptNo(Util.getUserOffCode(), tmgr);
                applicationFeeTradeCertDobj.setReceiptNumber(rcptNo);
            }
            if (applicationFeeTradeCertDobj.getFeeDraftDobj() != null) {
                applicationFeeTradeCertDobj.getFeeDraftDobj().setRcpt_no(rcptNo);
                applicationFeeTradeCertDobj.getFeeDraftDobj().setAppl_no(applicationFeeTradeCertDobj.getApplNo());
                FeeDraftimpl draftImpl = new FeeDraftimpl();
                inscd = draftImpl.saveDraftDetails(applicationFeeTradeCertDobj.getFeeDraftDobj(), tmgr);
            }
            String sql = "INSERT INTO vt_fee(regn_no,payment_mode,fees,fine,rcpt_no,rcpt_dt,pur_cd,flag,collected_by,state_cd,off_cd) VALUES (?, ?, ?, ?, ?,CURRENT_TIMESTAMP, ?, ?, ?, ?, ?)";
            pstmt = tmgr.prepareStatement(sql);

            int noOfQueryToBeInserted = 0;
            insertIntoVtFee(pstmt, applicationFeeTradeCertDobj, tmgr);
            noOfQueryToBeInserted++;

            if (Double.valueOf(applicationFeeTradeCertDobj.getTaxCollected()) > 0) {
                insert_into_vt_tax(applicationFeeTradeCertDobj, tmgr);
                insert_into_vt_tax_breakup(applicationFeeTradeCertDobj, tmgr);

            }

            if (applicationFeeTradeCertDobj.isServiceChargeToBeRendered()) {
                insertServiceChargeIntoVtFee(pstmt, applicationFeeTradeCertDobj, tmgr);
                noOfQueryToBeInserted++;
            }

            if (applicationFeeTradeCertDobj.getTransactionFee() != null && applicationFeeTradeCertDobj.getTransactionFee() != 0.0) {
                insertTransactionFeeIntoVtFee(pstmt, applicationFeeTradeCertDobj, tmgr);
                noOfQueryToBeInserted++;
            }

            if (applicationFeeTradeCertDobj.getMiscFee() != null && applicationFeeTradeCertDobj.getMiscFee() != 0.0) {
                insertMiscFeeIntoVtFee(pstmt, applicationFeeTradeCertDobj, tmgr);
                noOfQueryToBeInserted++;
            }

            if (applicationFeeTradeCertDobj.getManualFeeReceiptAmount() != 0) {
                insertManualFeeReceiptAmountIntoVtFee(pstmt, applicationFeeTradeCertDobj, tmgr);
                noOfQueryToBeInserted++;
            }

            int[] i = pstmt.executeBatch();
            if (i.length == noOfQueryToBeInserted) {
                result = "SUCCESS " + result;
                result = result + applicationFeeTradeCertDobj.getReceiptNumber();
            }

            {   /////////////// vp_appl_rcpt_mapping Insertion for printing fee reciept
                sql = "INSERT INTO " + TableList.VP_APPL_RCPT_MAPPING + " "
                        + " (state_cd,off_cd,appl_no,rcpt_no,owner_name,chasi_no,vh_class,instrument_cd,excess_amt,cash_amt,remarks)"
                        + " VALUES (?, ?, ?, ?, ?,'CHASI_NO_NOT_APPLICABLE_FOR_TC', ?, ?, ?, ?, ?)";
                pstmtVpAppRcptMappng = tmgr.prepareStatement(sql);
                noOfQueryToBeInserted = 1;
                String saveVpApplRcptMapping = insertIntoVpApplRcptMapping(pstmtVpAppRcptMappng, inscd, applicationFeeTradeCertDobj, tmgr);
                if (saveVpApplRcptMapping.contains("SUCCESS")) {
                    i = pstmtVpAppRcptMappng.executeBatch();
                    if (i.length == noOfQueryToBeInserted) {
                        result = "SUCCESS " + result;
                    }
                }
            }
            ServerUtil.insertForQRDetails(applicationFeeTradeCertDobj.getApplNo(), null, null, applicationFeeTradeCertDobj.getReceiptNumber(), false, DocumentType.RECEIPT_QR, applicationFeeTradeCertDobj.getStateCd(), applicationFeeTradeCertDobj.getOffCd(), tmgr);
            if (result.contains("SUCCESS")) {
                Status_dobj status = new Status_dobj();
                status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
                status.setStatus(TableConstants.STATUS_COMPLETE);
                status.setAppl_no(applicationFeeTradeCertDobj.getApplNo());
                status.setOffice_remark("Fee Submitted");
                status.setPublic_remark("Fee Submitted");
                status.setState_cd(Util.getUserStateCode());
                status.setPur_cd(Integer.parseInt(applicationFeeTradeCertDobj.getPurCd()));
                status.setEmp_cd(applicationFeeTradeCertDobj.getEmpCd());

                ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, applicationFeeTradeCertDobj.getApplNo(),
                        TableConstants.TM_ROLE_TRADE_CERT_FEE, Integer.parseInt(applicationFeeTradeCertDobj.getPurCd()), null, tmgr);

                ServerUtil.fileFlow(tmgr, status);
                tmgr.commit();

            }

        } catch (java.sql.BatchUpdateException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return result;
    }

    public void getAllSrNoForSelectedApplication(String applNo, Map applicationSectionsList) throws VahanException {
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        String sql = "";

        try {
            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();
            if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {   /// for Odisha
                sql = "Select * from " + TableList.VA_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " where appl_no = ?";
            } else {
                sql = "Select * from va_trade_certificate where appl_no = ?";
            }
            tmgr = new TransactionManager("getAllSrNoForSelectedApplication");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            rs = tmgr.fetchDetachedRowSet();
            ApplicationFeeTradeCertDobj applicationFeeTradeCertDobj = null;
            while (rs.next()) {
                applicationFeeTradeCertDobj = new ApplicationFeeTradeCertDobj();
                applicationFeeTradeCertDobj.setSrNo(String.valueOf(rs.getInt("sr_no")));
                if (tmConfigDobj.getTmTradeCertConfigDobj().isVehClassToBeRendered()) {  /// for Odisha
                    applicationFeeTradeCertDobj.setVehCatgName(getVehClassDesc(rs.getString("vch_catg")));
                } else {
                    applicationFeeTradeCertDobj.setVehCatgName(ApplicationTradeCertImpl.getVehCatgDesc(rs.getString("vch_catg")));
                }
                applicationFeeTradeCertDobj.setVehCatgFor(rs.getString("vch_catg"));
                applicationFeeTradeCertDobj.setNoOfAllowedVehicles(String.valueOf(rs.getInt("no_of_vch")));
                if (tmConfigDobj.getTmTradeCertConfigDobj().isIllegitimateTradeCertApplicable()) { /// for odisha
                    applicationFeeTradeCertDobj.setExtraVehiclesSoldLastYr(String.valueOf(rs.getInt("extra_vch_sold_last_yr")));
                }
                applicationFeeTradeCertDobj.setApplNo(String.valueOf(rs.getString("appl_no")));
                if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {   /// for Odisha
                    applicationFeeTradeCertDobj.setDealerName(getDealerNameOR(rs.getString("dealer_cd"), rs.getString("state_cd"), rs.getInt("off_cd")));
                    applicationFeeTradeCertDobj.setApplicantCategory(rs.getString("applicant_type"));
                } else {
                    applicationFeeTradeCertDobj.setDealerName(getDealerName(rs.getString("dealer_cd"), rs.getString("state_cd"), rs.getInt("off_cd")));
                }
                applicationFeeTradeCertDobj.setDealerFor(rs.getString("dealer_cd"));
                applicationFeeTradeCertDobj.setFuelTypeFor(rs.getString("fuel_cd"));
                applicationFeeTradeCertDobj.setFuelTypeName(getFuelTypeDesc(rs.getInt("fuel_cd")));
                if (!CommonUtils.isNullOrBlank(rs.getString("status"))) {
                    applicationFeeTradeCertDobj.setStatus(rs.getString("status"));
                }

                applicationFeeTradeCertDobj.setStockTransferApplicable(rs.getBoolean("stock_transfer_req"));

                applicationSectionsList.put(applicationFeeTradeCertDobj.getSrNo(), applicationFeeTradeCertDobj);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (psmt != null) {
                    psmt.close();
                }
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    private String getFuelTypeDesc(int fuelCd) throws VahanException {
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        String sql = " Select descr from vm_fuel where code  = ?";
        try {
            tmgr = new TransactionManager("getFuelTypeDesc");
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, fuelCd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                return rs.getString("descr");
            } else {
                return "";
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (psmt != null) {
                    psmt.close();
                }
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    private String getVehClassDesc(String vehClassCd) throws VahanException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String sql = " Select vch_class_descr from " + TableList.VM_VCH_CLASS_ONLINE_SCHEMA_TC + " where vch_class_cd in ('" + vehClassCd + "')";
        try {
            tmgr = new TransactionManager("getVehClassDesc");
            psmt = tmgr.prepareStatement(sql);
            rs = psmt.executeQuery();
            if (rs.next()) {
                return rs.getString("vch_class_descr");
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return vehClassCd;
    }

    public String getDealerName(String dealerCd, String stateCd, int offCd) throws VahanException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String dealerName = "";
        String sql = " Select dealer_name,d_add1,d_add2,d_district,d_state,d_pincode  from " + TableList.VM_DEALER_MAST + " where dealer_cd in ('" + dealerCd + "') and state_cd = ? and off_cd = ? ";
        try {
            tmgr = new TransactionManager("getDealerName");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, stateCd);
            psmt.setInt(2, offCd);
            rs = psmt.executeQuery();
            if (rs.next()) {
                dealerName = rs.getString("dealer_name");
                if (rs.getString("d_add1") != null && !rs.getString("d_add1").equals("")) {
                    dealerName += "," + rs.getString("d_add1");
                }
                if (rs.getString("d_add2") != null && !rs.getString("d_add2").equals("")) {
                    dealerName += "," + rs.getString("d_add2");
                }
                if (rs.getString("d_district") != null && !rs.getString("d_district").equals("")) {
                    dealerName += "," + rs.getString("d_district");
                }
                if (rs.getString("d_state") != null && !rs.getString("d_state").equals("")) {
                    dealerName += "," + rs.getString("d_state");
                }
                if (rs.getString("d_pincode") != null && !rs.getString("d_pincode").equals("")) {
                    dealerName += "," + rs.getString("d_pincode");
                }
                return dealerName;
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return dealerCd;
    }

    public String getDealerNameOR(String dealerCd, String stateCd, int offCd) throws VahanException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String dealerName = "";
        boolean recordFound = false;
        String sql = " Select applicant_name  from " + TableList.VM_APPLICANT_MAST_APPL_ONLINE_SCHEMA_TC + " where applicant_cd in ('" + dealerCd + "') and applicant_state_cd = ? and applicant_off_cd = ? ";
        try {
            tmgr = new TransactionManager("getDealerNameOR");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, stateCd);
            psmt.setInt(2, offCd);
            rs = psmt.executeQuery();
            if (rs.next()) {
                recordFound = true;
                dealerName = rs.getString("applicant_name");

                return dealerName;
            }
            if (!recordFound) {
                sql = " Select dealer_name  from " + TableList.VM_DEALER_MAST + " where dealer_cd in ('" + dealerCd + "') and state_cd = ? and off_cd = ? ";
                psmt = tmgr.prepareStatement(sql);
                psmt.setString(1, stateCd);
                psmt.setInt(2, offCd);
                rs = psmt.executeQuery();
                if (rs.next()) {
                    recordFound = true;
                    dealerName = rs.getString("dealer_name");
                    return dealerName;
                }
            }
            if (!recordFound) {
                sql = " Select financer_name  from " + TableList.VM_FINANCIER_TC + " where financer_cd in ('" + dealerCd + "') and state_cd = ? and off_cd = ? ";
                psmt = tmgr.prepareStatement(sql);
                psmt.setString(1, stateCd);
                psmt.setInt(2, offCd);
                rs = psmt.executeQuery();
                if (rs.next()) {
                    recordFound = true;
                    dealerName = rs.getString("financer_name");
                    return dealerName;
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return dealerCd;
    }

    public Map getActionDescriptions(int[] actionCds) throws VahanException {
        Map map = new HashMap();
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String sql = " Select a_code,a_desc from vm_action where a_code in ( ";
        for (int cds : actionCds) {
            sql += cds + ",";
        }
        sql = sql.substring(0, sql.lastIndexOf(","));
        sql += " ) ";
        try {
            tmgr = new TransactionManager("getActionDescriptions");
            psmt = tmgr.prepareStatement(sql);
            rs = psmt.executeQuery();
            while (rs.next()) {
                map.put("" + rs.getInt("a_code"), rs.getString("a_desc"));
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return map;
    }

    public int[] getFeeDetails(String stateCode, String purCd, String vehCatgSelected) throws VahanException {
        int[] feeDetailsArr = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String sql = " Select fees from vm_feemast_catg where pur_cd = " + Integer.valueOf(purCd) + " and state_cd = '" + stateCode.toUpperCase() + "' and vch_catg = '" + vehCatgSelected + "'";
        try {
            tmgr = new TransactionManager("getFeeDetails");
            psmt = tmgr.prepareStatement(sql);
            rs = psmt.executeQuery();
            if (rs.next()) {
                feeDetailsArr = new int[]{rs.getInt("fees")};
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return feeDetailsArr;
    }

    public Map<String, Integer> getFeeDetail(String stateCode, String purCd, String vehCatgSelected) throws VahanException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        String fee = "";
        TransactionManager tmgr = null;
        Map<String, Integer> mp = new HashMap();
        //String sql = " Select array_to_string(array_agg(fees), ',') as fees from vm_feemast_catg where pur_cd IN (" + Integer.valueOf(purCd) + "," + TableConstants.TRADE_CERTIFICATE_TAX + ") and state_cd = '" + stateCode.toUpperCase() + "' and vch_catg = '" + vehCatgSelected + "'";
        String sql = " Select * from vm_feemast_catg where pur_cd IN (" + Integer.valueOf(purCd) + "," + TableConstants.TRADE_CERTIFICATE_TAX + "," + TableConstants.TRADE_CERTIFICATE_SURCHARGE + ") and state_cd = '" + stateCode.toUpperCase() + "' and vch_catg = '" + vehCatgSelected + "'";
        try {
            tmgr = new TransactionManager("getFeeDetails");
            psmt = tmgr.prepareStatement(sql);
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                Integer fees = rs.getInt("fees");
                Integer pur_cd = rs.getInt("pur_cd");
                if (pur_cd == TableConstants.TRADE_CERTIFICATE_TAX) {
                    mp.put("TRADE_CERT_TAX", fees);
                } else if (pur_cd == TableConstants.TRADE_CERTIFICATE_SURCHARGE) {
                    mp.put("TRADE_CERT_SURCHARGE", fees);
                } else {
                    mp.put("TRADE_CERT_FEE", fees);
                }

            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return mp;
    }

    public static Double getTransactionFeeDetail() throws VahanException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String sql = " Select * from vm_feemast where pur_cd IN (" + TableConstants.VM_TRANSACTION_MAST_ALL + ") and state_cd = ?";
        try {
            tmgr = new TransactionManager("getTransactionFeeDetail");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                return Double.valueOf(rs.getInt("fees"));
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return 0.0;
    }

    public Map<String, Integer> getFeeDetailOR(String stateCode, String purCd, String vehCatgSelected) throws VahanException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        String fee = "";
        TransactionManager tmgr = null;
        Map<String, Integer> mp = new HashMap();
        String sql = " Select * from vm_feemast where pur_cd IN (?," + TableConstants.TRADE_CERTIFICATE_TAX + "," + TableConstants.TRADE_CERTIFICATE_SURCHARGE + ") and state_cd = ? and vh_class = ?";
        try {
            tmgr = new TransactionManager("getFeeDetailsOR");
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, Integer.valueOf(purCd));
            psmt.setString(2, stateCode.toUpperCase());
            psmt.setInt(3, Integer.valueOf(vehCatgSelected));
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                Integer fees = rs.getInt("fees");
                Integer pur_cd = rs.getInt("pur_cd");
                if (pur_cd == TableConstants.TRADE_CERTIFICATE_TAX) {
                    mp.put("TRADE_CERT_TAX", fees);
                } else if (pur_cd == TableConstants.TRADE_CERTIFICATE_SURCHARGE) {
                    mp.put("TRADE_CERT_SURCHARGE", fees);
                } else {
                    mp.put("TRADE_CERT_FEE", fees);
                }

            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return mp;
    }

    public Map getBankMap() throws VahanException {
        Map bankMap = new HashMap();
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String sql = " Select bank_code,descr  from tm_bank";
        try {
            tmgr = new TransactionManager("getBankMap");
            psmt = tmgr.prepareStatement(sql);

            rs = psmt.executeQuery();
            while (rs.next()) {
                bankMap.put(rs.getString("bank_code"), rs.getString("descr"));
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return bankMap;
    }

    public Map getBankBranchesMap(String bankCode) throws VahanException {
        Map bankMap = new HashMap();
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String sql = " Select branch_code,branch_name from tm_bank_branch where bank_code = '" + bankCode + "'";
        try {
            tmgr = new TransactionManager("getBankBranchesMap");
            psmt = tmgr.prepareStatement(sql);

            rs = psmt.executeQuery();
            while (rs.next()) {
                bankMap.put(rs.getString("branch_code"), rs.getString("branch_name"));
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return bankMap;
    }

    public String generateReceiptNo() throws VahanException {

        TransactionManager tmgr = null;
        String rcptNo = null;
        try {
            tmgr = new TransactionManager("generateReceiptNo");
            rcptNo = Receipt_Master_Impl.generateNewRcptNo(Util.getUserOffCode(), tmgr);

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return rcptNo;
    }

    public static Object[] getManualFeeReceiptDtls(String transApplNo) throws VahanException {
        Object[] manualFeeReceiptDtlsArr = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String sql = " select * from " + TableList.VT_MANUAL_RECEIPT + " where transaction_appl_no = ? and state_cd = ? and off_cd = ? ";
        try {
            tmgr = new TransactionManager("getManualFeeReceiptDtls");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, transApplNo);
            psmt.setString(2, Util.getUserStateCode());
            psmt.setInt(3, Util.getUserOffCode());
            rs = psmt.executeQuery();
            if (rs.next()) {
                if (manualFeeReceiptDtlsArr == null) {
                    manualFeeReceiptDtlsArr = new Object[3];
                }
                manualFeeReceiptDtlsArr[0] = rs.getInt("amount");
                manualFeeReceiptDtlsArr[1] = rs.getString("rcpt_no");
                manualFeeReceiptDtlsArr[2] = rs.getDate("rcpt_dt");
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (rs != null) {
                    rs.close();
                }
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return manualFeeReceiptDtlsArr;
    }
}
