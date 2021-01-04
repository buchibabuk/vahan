/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.server;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.tradecert.ApplicationFeeTradeCertDobj;
import nic.vahan.form.dobj.DailyAndConsolidatedStmtReportdobj;
import nic.vahan.form.dobj.PrintRcptParticularDtlsDobj;
import nic.vahan.form.dobj.PrintReceiptDtlsDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.tradecert.IssueTradeCertDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.tradecert.ApplicationTradeCertImpl;
import nic.vahan.form.impl.tradecert.PublicApplicationTradeCertImpl;
import org.apache.log4j.Logger;

/**
 * Certificates or Receipts are printed in one pages only so we can send a
 * details in Map and these will be printed using Jasper Report Templates
 *
 */
public class PrinterDocDetails {

    private static Logger LOGGER = Logger.getLogger(PrinterDocDetails.class);
    static SessionVariables sessionVariables = new SessionVariables();

    public static Map getTradeCertificateData(String stateCd, int offCd, String applNo, String tradeCertNo, boolean isRenew, int purCd) throws VahanException {
        Map mapData = null;
        TransactionManagerReadOnly tmgr = null;
        try {

            String sql = "select tmstate.descr as statename,tmoffice.off_name as officename, vt_tradecert.appl_no,to_char(vt_tradecert.valid_from, 'dd-Mon-yyyy') || ' TO ' || to_char(vt_tradecert.valid_upto, 'dd-Mon-yyyy') as validity_period, vt_tradecert.cert_no,       vd.dealer_name as dealerName,to_char(vt_tradecert.issue_dt, 'dd-Mon-yyyy') as issueDate,       COALESCE(vd.d_add1, ''::character varying)  || ',' || COALESCE(vd.d_add2, ''::character varying) || ',' || COALESCE(vd_state.descr , ''::character varying) as dealerAddress,vt_tradecert.sr_no "
                    + " from vt_trade_certificate vt_tradecert "
                    + " LEFT JOIN vm_dealer_mast vd ON vd.dealer_cd::text = vt_tradecert.dealer_cd::text "
                    + " LEFT JOIN TM_STATE tmstate ON tmstate.state_code = vt_tradecert.state_cd "
                    + " LEFT JOIN TM_STATE vd_state ON vd_state.state_code = vd.d_state "
                    + " LEFT JOIN tm_office tmoffice ON tmoffice.off_cd = vt_tradecert.off_cd AND tmoffice.state_cd = vt_tradecert.state_cd::bpchar ";

            if (purCd == TableConstants.VM_TRANSACTION_TRADE_CERT_DUP || purCd == 0) {   ///// DUPLICATE OR Called from TRADE_CERT_PRINT form
                sql += " WHERE  vt_tradecert.cert_no=? ";
                applNo = tradeCertNo;
            } else {
                sql += " WHERE  vt_tradecert.appl_no=? ";
            }

            tmgr = new TransactionManagerReadOnly("getTradeCertificateData");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                mapData = new HashMap();
                mapData.put("stateName", rs.getString("statename"));
                mapData.put("officeName", rs.getString("officename") + "," + rs.getString("statename"));
                mapData.put("applNo", rs.getString("appl_no"));
                String regnNo = "";
                if (isRenew) {
                    regnNo = applNo;
                } else {
                    regnNo = tradeCertNo;
                }

                String receptNo = "";
                if (purCd != 0) {
                    receptNo = getReceiptNo(tmgr, regnNo, purCd);
                    if (receptNo == null || (receptNo != null && receptNo.equals(""))) {
                        receptNo = getReceiptNo(tmgr, tradeCertNo, purCd);
                    }
                } else {  ////// Called from TRADE_CERT_PRINT form
                    purCd = 21;
                    while (purCd < 23) {
                        receptNo = getReceiptNo(tmgr, regnNo, purCd);
                        if (receptNo == null || (receptNo != null && receptNo.equals(""))) {
                            receptNo = getReceiptNo(tmgr, tradeCertNo, purCd);
                        }
                        if (receptNo == null || (receptNo != null && receptNo.equals(""))) {
                            purCd++;
                        } else {
                            break;
                        }
                    }
                }
                if (receptNo != null) {
                    mapData.put("fees", getFeesForTradeCert(tmgr, stateCd, offCd, receptNo, purCd));
                }
                mapData.put("validityPeriod", rs.getString("validity_period"));
                mapData.put("certNo", rs.getString("cert_no"));
                mapData.put("dealerName", rs.getString("dealerName"));
                mapData.put("dealerAddress", rs.getString("dealerAddress"));
                mapData.put("issueDateAsString", rs.getString("issueDate"));
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
        return mapData;
    }

    public static Map getTradeCertificateApplPrinting(String stateCd, int offCd, String applNo, int purCd) throws VahanException {
        Map mapData = null;
        TransactionManagerReadOnly tmgr = null;
        try {

            String sql = " select tmstate.descr as statename,tmoffice.off_name as officename, va_tradecert.appl_no, "
                    + "      vd.dealer_name as dealerName,vd.dealer_cd as dealerCode, "
                    + "      COALESCE(vd.d_add1, ''::character varying)  || ',' || COALESCE(vd.d_add2, ''::character varying) || ',' || COALESCE(tmstate.descr, ''::character varying) as dealerAddress, "
                    + "      va_tradecert.sr_no  "
                    + " from va_trade_certificate va_tradecert "
                    + "      LEFT JOIN vm_dealer_mast vd ON vd.dealer_cd::text =va_tradecert.dealer_cd::text  "
                    + "      LEFT JOIN TM_STATE tmstate ON tmstate.state_code = va_tradecert.state_cd  "
                    + "      LEFT JOIN tm_office tmoffice ON tmoffice.off_cd = va_tradecert.off_cd AND tmoffice.state_cd = va_tradecert.state_cd::bpchar  "
                    + " WHERE  va_tradecert.appl_no=? ";
            tmgr = new TransactionManagerReadOnly("getTradeCertificateApplPrinting");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                mapData = new HashMap();
                mapData.put("stateName", rs.getString("statename"));
                mapData.put("officeName", rs.getString("officename"));
                mapData.put("applNo", rs.getString("appl_no"));
                String regnNo = applNo;
                String receptNo = getReceiptNo(tmgr, regnNo, purCd);
                if (receptNo != null) {
                    mapData.put("fees", getFeesForTradeCert(tmgr, stateCd, offCd, receptNo, purCd));
                }
                mapData.put("dealerCode", rs.getString("dealerCode"));
                mapData.put("dealerName", rs.getString("dealerName"));
                mapData.put("dealerAddress", rs.getString("dealerAddress"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Exception in fetching trade certificate application printing data.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return mapData;
    }

    public static Map getTradeCertificateApplPrinting(String stateCd, int offCd, String applNo, int purCd, String applicantType) throws VahanException {
        Map mapData = null;
        TransactionManagerReadOnly tmgr = null;
        StringBuilder fullAddress = new StringBuilder();
        String vaTradeCertificateTable = "";
        try {
            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();
            if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {   /// using_online_schema_tc work
                vaTradeCertificateTable = TableList.VA_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC;
            } else {
                vaTradeCertificateTable = TableList.VA_TRADE_CERTIFICATE;
            }

            String sql = " select tmstate.state_code as state_cd,tmstate.descr as statename,tmoffice.off_name as officename, va_tradecert.appl_no, "
                    + "  va_tradecert.dealer_cd as dealerCode,va_tradecert.applicant_type as applicantType, va_tradecert.no_of_vch, ";

            if (CommonUtils.isNullOrBlank(applicantType)
                    || (!CommonUtils.isNullOrBlank(applicantType) && (applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)))) {
                sql += " vd.dealer_name as dealerName, vd.d_add1, vd.d_add2, vd.d_district, vd.d_pincode, tmstate.descr, ";
            } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_MANUFACTURER)) {
                sql += " vd.maker_name as dealerName, vd.m_add1, vd.m_add2, vd.district, vd.pincode, tmstate.descr,";
            } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER)) {
                sql += " vd.financer_name as dealerName, vd.branch_add1, vd.branch_add2, vd.branch_district, vd.branch_pincode, tmstate.descr,";
            } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_RETRO_FITTER)) {
                sql += " vd.retrofitter_name as dealerName, vd.rf_add1, vd.rf_add2, vd.district, vd.pincode, tmstate.descr,";
            }

            sql += "  va_tradecert.sr_no  "
                    + " from " + vaTradeCertificateTable + " va_tradecert ";

            if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_MANUFACTURER)) {
                sql += "      LEFT JOIN " + TableList.VM_MAKER_TC + " vd ON vd.maker_cd::text = va_tradecert.dealer_cd  ";
            } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER)) {
                sql += "      LEFT JOIN " + TableList.VM_FINANCIER_TC + " vd ON vd.financer_cd = va_tradecert.dealer_cd  ";
            } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_RETRO_FITTER)) {
                sql += "      LEFT JOIN " + TableList.VM_RETROFITTER_TC + " vd ON vd.retrofitter_cd = va_tradecert.dealer_cd::int  ";
            } else {
                sql += "      LEFT JOIN " + TableList.VM_DEALER_MAST + " vd ON vd.dealer_cd = va_tradecert.dealer_cd  ";
            }

            sql += "      LEFT JOIN TM_STATE tmstate ON tmstate.state_code = va_tradecert.state_cd  "
                    + "      LEFT JOIN tm_office tmoffice ON tmoffice.off_cd = va_tradecert.off_cd AND tmoffice.state_cd = va_tradecert.state_cd::bpchar  "
                    + " WHERE  va_tradecert.appl_no=? ";
            tmgr = new TransactionManagerReadOnly("getTradeCertificateApplPrinting");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            int noOfVeh = 0;
            while (rs.next()) {
                if (mapData == null) {
                    mapData = new HashMap();
                    mapData.put("stateName", rs.getString("statename"));
                    mapData.put("officeName", rs.getString("officename"));
                    mapData.put("applNo", rs.getString("appl_no"));
                    String regnNo = applNo;
                    int[] purCdArr = {purCd,
                        TableConstants.TRADE_CERTIFICATE_TAX,
                        TableConstants.TRADE_CERTIFICATE_SURCHARGE,
                        TableConstants.VM_TRANSACTION_MAST_USER_CHARGES,
                        TableConstants.VM_TRANSACTION_MAST_ALL,
                        TableConstants.VM_TRANSACTION_MAST_MISC};
                    String receptNo = getReceiptNo(tmgr, regnNo, purCd);
                    if (CommonUtils.isNullOrBlank(receptNo)) {
                        receptNo = getReceiptNoForOnlineTradeCertAppl(tmgr, regnNo, purCd);
                    }
                    Long feeAmount = 0L;
                    if (receptNo != null) {
                        for (int purCode : purCdArr) {
                            String amount = getFeesForTradeCert(tmgr, stateCd, offCd, receptNo, purCode);
                            if (!CommonUtils.isNullOrBlank(amount)) {
                                feeAmount += Long.valueOf(amount);
                            }
                        }
                    }
                    mapData.put("fees", String.valueOf(feeAmount));
                    mapData.put("dealerCode", rs.getString("dealerCode"));
                    mapData.put("dealerName", rs.getString("dealerName"));
                    fullAddress.append("");
                    if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_MANUFACTURER)) {
                        if (rs.getString("m_add1") != null && !rs.getString("m_add1").equals("")) {
                            fullAddress.append(",").append(rs.getString("m_add1"));
                        }
                        if (rs.getString("m_add2") != null && !rs.getString("m_add2").equals("") && !rs.getString("m_add2").equals(rs.getString("m_add1"))) {
                            fullAddress.append(", ").append(rs.getString("m_add2"));
                        }
                        if (rs.getInt("district") != 0) {
                            fullAddress.append(", ").append(PublicApplicationTradeCertImpl.getDistrictNameFromCode(rs.getString("state_cd"), String.valueOf(rs.getInt("district"))).toUpperCase());
                        }
                        if (rs.getInt("pincode") != 0) {
                            fullAddress.append(", ").append(rs.getInt("pincode"));
                        }
                    } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER)) {
                        if (rs.getString("branch_add1") != null && !rs.getString("branch_add1").equals("")) {
                            fullAddress.append(",").append(rs.getString("branch_add1"));
                        }
                        if (rs.getString("branch_add2") != null && !rs.getString("branch_add2").equals("") && !rs.getString("branch_add2").equals(rs.getString("branch_add1"))) {
                            fullAddress.append(", ").append(rs.getString("branch_add2"));
                        }
                        if (rs.getInt("branch_district") != 0) {
                            fullAddress.append(", ").append(PublicApplicationTradeCertImpl.getDistrictNameFromCode(rs.getString("state_cd"), String.valueOf(rs.getInt("branch_district"))).toUpperCase());
                        }
                        if (rs.getInt("branch_pincode") != 0) {
                            fullAddress.append(", ").append(rs.getInt("branch_pincode"));
                        }
                    } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_RETRO_FITTER)) {
                        if (rs.getString("rf_add1") != null && !rs.getString("rf_add1").equals("")) {
                            fullAddress.append(",").append(rs.getString("rf_add1"));
                        }
                        if (rs.getString("rf_add2") != null && !rs.getString("rf_add2").equals("") && !rs.getString("rf_add2").equals(rs.getString("rf_add1"))) {
                            fullAddress.append(", ").append(rs.getString("rf_add2"));
                        }
                        if (rs.getInt("district") != 0) {
                            fullAddress.append(", ").append(PublicApplicationTradeCertImpl.getDistrictNameFromCode(rs.getString("state_cd"), String.valueOf(rs.getInt("district"))).toUpperCase());
                        }
                        if (rs.getInt("pincode") != 0) {
                            fullAddress.append(", ").append(rs.getInt("pincode"));
                        }
                    } else {
                        if (rs.getString("d_add1") != null && !rs.getString("d_add1").equals("")) {
                            fullAddress.append(",").append(rs.getString("d_add1"));
                        }
                        if (rs.getString("d_add2") != null && !rs.getString("d_add2").equals("") && !rs.getString("d_add2").equals(rs.getString("d_add1"))) {
                            fullAddress.append(", ").append(rs.getString("d_add2"));
                        }
                        if (rs.getInt("d_district") != 0) {
                            fullAddress.append(", ").append(PublicApplicationTradeCertImpl.getDistrictNameFromCode(rs.getString("state_cd"), String.valueOf(rs.getInt("d_district"))).toUpperCase());
                        }
                        if (rs.getInt("d_pincode") != 0) {
                            fullAddress.append(", ").append(rs.getInt("d_pincode"));
                        }
                    }

                    if (rs.getString("descr") != null && !rs.getString("descr").equals("")) {
                        fullAddress.append(", ").append(rs.getString("descr").toUpperCase());
                    }

                    if (fullAddress.toString().startsWith(",")) {
                        mapData.put("dealerAddress", fullAddress.substring(1).trim());
                    } else {
                        mapData.put("dealerAddress", fullAddress.toString().trim());
                    }
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("no_of_vch"))) {
                    noOfVeh += Integer.valueOf(rs.getString("no_of_vch"));
                }
                mapData.put("NoOfVehicles", noOfVeh);
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Exception in fetching trade certificate application printing data.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return mapData;
    }

    public static Map getTradeCertificatePrintingFromVt(String stateCd, int offCd, int purCd, String certNo, boolean doNotShowNoOfVehicles, boolean displayFuel) throws VahanException {
        Map mapData = null;
        TransactionManagerReadOnly tmgr = null;
        String vchCatgNames = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        String validityPeriod = "";

        try {

            tmgr = new TransactionManagerReadOnly("getTradeCertificateApplPrintingFromVt");
            PreparedStatement ps = null;
            String sql = " select tmstate.descr as statename,tmoffice.off_name as officename, vt_tradecert.appl_no, vt_tradecert.fuel_cd, "
                    + "      vd.dealer_name as dealerName,vd.dealer_cd as dealerCode, "
                    + "      COALESCE(vd.d_add1, ''::character varying)  || ',' || COALESCE(vd.d_add2, ''::character varying) || ',' || COALESCE(tmstate.descr, ''::character varying) as dealerAddress, "
                    + "      vt_tradecert.cert_no,vt_tradecert.vch_catg,vt_tradecert.valid_from,vt_tradecert.valid_upto,vt_tradecert.issue_dt,vt_tradecert.no_of_vch,vt_tradecert.no_of_vch_used  "
                    + " from vt_trade_certificate vt_tradecert "
                    + "      LEFT JOIN vm_dealer_mast vd ON vd.dealer_cd::text =vt_tradecert.dealer_cd::text  "
                    + "      LEFT JOIN TM_STATE tmstate ON tmstate.state_code = vt_tradecert.state_cd  "
                    + "      LEFT JOIN tm_office tmoffice ON tmoffice.off_cd = vt_tradecert.off_cd AND tmoffice.state_cd = vt_tradecert.state_cd::bpchar  ";

            /////////// FOR NEW AND RENEW 
            sql += " WHERE  vt_tradecert.cert_no=? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, certNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            String receptNo = null;

            boolean runFirstTime = true;

            while (rs.next()) {
                if (runFirstTime) {
                    mapData = new HashMap();
                    mapData.put("stateName", rs.getString("statename"));
                    mapData.put("officeName", rs.getString("officename"));
                    mapData.put("certNo", rs.getString("cert_no"));
                    mapData.put("applNo", rs.getString("appl_no"));
                    String regnNo = rs.getString("appl_no");

//                    if (purCd == 0) {
//                        purCd = TableConstants.VM_TRANSACTION_TRADE_CERT_NEW;
//                        receptNo = getReceiptNo(tmgr, regnNo, purCd);
//                        if (receptNo == null) {
//                            receptNo = getReceiptNoForTCRenew(tmgr, certNo, purCd);
//                        }
//                        if (receptNo == null) {
//                            purCd = TableConstants.VM_TRANSACTION_TRADE_CERT_DUP;
//                            receptNo = getReceiptNo(tmgr, regnNo, purCd);
//                            if (receptNo == null) {
//                                receptNo = getReceiptNo(tmgr, certNo, purCd);
//                            }
//
//                        }
//                    } else {
//                        receptNo = getReceiptNo(tmgr, regnNo, purCd);
//                        if (receptNo == null) {
//                            receptNo = getReceiptNo(tmgr, certNo, purCd);
//                        }
//                    }
//
//                    if (receptNo != null) {
//                        mapData.put("fees", getFeesForTradeCert(tmgr, stateCd, offCd, receptNo, purCd));
//                    } else {
//                        mapData.put("fees", "");
//                    }

                    int[] purCdArr = {purCd,
                        TableConstants.TRADE_CERTIFICATE_TAX,
                        TableConstants.TRADE_CERTIFICATE_SURCHARGE,
                        TableConstants.VM_TRANSACTION_MAST_USER_CHARGES,
                        TableConstants.VM_TRANSACTION_MAST_ALL,
                        TableConstants.VM_TRANSACTION_MAST_MISC};
                    receptNo = getReceiptNo(tmgr, regnNo, purCd);
                    if (CommonUtils.isNullOrBlank(receptNo)) {
                        receptNo = getReceiptNoForOnlineTradeCertAppl(tmgr, regnNo, purCd);
                    }
                    Long feeAmount = 0L;
                    if (receptNo != null) {
                        for (int purCode : purCdArr) {
                            String amount = getFeesForTradeCert(tmgr, stateCd, offCd, receptNo, purCode);
                            if (!CommonUtils.isNullOrBlank(amount)) {
                                feeAmount += Long.valueOf(amount);
                            }
                        }
                    }
                    mapData.put("fees", String.valueOf(feeAmount));
                    mapData.put("dealerCode", rs.getString("dealerCode"));
                    mapData.put("dealerName", rs.getString("dealerName"));
                    mapData.put("dealerAddress", rs.getString("dealerAddress"));
                    mapData.put("issueDate", sdf.format(rs.getDate("issue_dt")));
                    mapData.put("validUpto", sdf.format(rs.getDate("valid_upto")));
                    validityPeriod = " ( " + sdf.format(rs.getDate("valid_from")) + " - " + sdf.format(rs.getDate("valid_upto")) + " ) ";
                    mapData.put("validityPeriod", validityPeriod);
                }

                if (doNotShowNoOfVehicles) {
                    if (displayFuel) {
                        vchCatgNames += "," + rs.getString("vch_catg") + " [ {fuel:" + ApplicationTradeCertImpl.getFuelTypeDesc(rs.getInt("fuel_cd")) + "} ]";
                    } else {
                        vchCatgNames += "," + rs.getString("vch_catg");
                    }
                } else {
                    if (displayFuel) {
                        vchCatgNames += "," + rs.getString("vch_catg") + " [ " + rs.getInt("no_of_vch") + ":" + rs.getInt("no_of_vch_used") + "{fuel:" + ApplicationTradeCertImpl.getFuelTypeDesc(rs.getInt("fuel_cd")) + "} ]";
                    } else {
                        vchCatgNames += "," + rs.getString("vch_catg") + " [ " + rs.getInt("no_of_vch") + ":" + rs.getInt("no_of_vch_used") + " ]";
                    }
                }
                if (runFirstTime) {
                    runFirstTime = false;
                }
            }
            mapData.put("vchCatgNames", vchCatgNames.substring(1));


        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Exception in fetching and mapping data for trade certificate printing from vt_trade_certificate.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return mapData;
    }

    public static Map getTradeCertificatePrintingFromVt(String stateCd, int offCd, String certNo, boolean doNotShowNoOfVehicles, boolean displayFuel, String applicantType) throws VahanException {
        Map mapData = null;
        TransactionManagerReadOnly tmgr = null;
        StringBuilder fullAddress = new StringBuilder();
        StringBuilder fullAddressShowroom = new StringBuilder();
        String vchCatgNames = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        String validityPeriod = "";
        String vtTradeCertificateTable = "";
        Long feeAmount = 0L;

        try {
            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();
            if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {   //// using_online_schema_tc work
                vtTradeCertificateTable = TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC;
            } else {
                vtTradeCertificateTable = TableList.VT_TRADE_CERTIFICATE;
            }
            tmgr = new TransactionManagerReadOnly("getTradeCertificateApplPrintingFromVt");
            PreparedStatement ps = null;

            String sql = " select tmstate.state_code as state_cd,tmstate.descr as statename,tmoffice.off_name as officename, vt_tradecert.appl_no, vt_tradecert.fuel_cd, "
                    + "  vt_tradecert.dealer_cd as dealerCode,vt_tradecert.applicant_type as applicantType, ";

            if (CommonUtils.isNullOrBlank(applicantType)
                    || (!CommonUtils.isNullOrBlank(applicantType) && (applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)))) {
                sql += " vd.dealer_name as dealerName, vd.d_add1, vd.d_add2, vd.d_district, vd.d_pincode, tmstate.descr, ";
                if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {    //// using_online_schema_tc work
                    sql += " vd_oth_dtl.showroom_name, vd_oth_dtl.showroom_add1, vd_oth_dtl.showroom_add2, vd_oth_dtl.showroom_district, vd_oth_dtl.showroom_pincode, ";
                }
            } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_MANUFACTURER)) {
                sql += " vd.maker_name as dealerName, vd.m_add1, vd.m_add2, vd.district, vd.pincode, tmstate.descr,";
            } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER)) {
                sql += " vd.financer_name as dealerName, vd.branch_add1, vd.branch_add2, vd.branch_district, vd.branch_pincode, tmstate.descr,";
            } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_RETRO_FITTER)) {
                sql += " vd.retrofitter_name as dealerName, vd.rf_add1, vd.rf_add2, vd.district, vd.pincode, tmstate.descr,";
            }

            sql += "  vt_tradecert.cert_no,status.pur_cd,vt_tradecert.vch_catg,vt_tradecert.valid_from,vt_tradecert.valid_upto,vt_tradecert.issue_dt,vt_tradecert.no_of_vch,vt_tradecert.no_of_vch_used  "
                    + " from " + vtTradeCertificateTable + " vt_tradecert ";

            if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_MANUFACTURER)) {
                sql += "      LEFT JOIN " + TableList.VM_MAKER_TC + " vd ON vd.maker_cd::text = vt_tradecert.dealer_cd  ";
            } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER)) {
                sql += "      LEFT JOIN " + TableList.VM_FINANCIER_TC + " vd ON vd.financer_cd = vt_tradecert.dealer_cd  ";
            } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_RETRO_FITTER)) {
                sql += "      LEFT JOIN " + TableList.VM_RETROFITTER_TC + " vd ON vd.retrofitter_cd = vt_tradecert.dealer_cd::int  ";
            } else {
                sql += "      LEFT JOIN " + TableList.VM_DEALER_MAST + " vd ON vd.dealer_cd = vt_tradecert.dealer_cd  ";
                if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {   //// using_online_schema_tc work
                    sql += "      LEFT JOIN " + TableList.VM_DEALER_OTHER_DETAILS + " vd_oth_dtl ON vd_oth_dtl.dealer_cd = vt_tradecert.dealer_cd  ";
                }
            }

            sql += "      LEFT JOIN TM_STATE tmstate ON tmstate.state_code = vt_tradecert.state_cd  "
                    + "   LEFT JOIN tm_office tmoffice ON tmoffice.off_cd = vt_tradecert.off_cd AND tmoffice.state_cd = vt_tradecert.state_cd::bpchar  "
                    + "   LEFT JOIN va_status status ON status.appl_no = vt_tradecert.appl_no  ";

            /////////// FOR NEW AND RENEW 
            sql += " WHERE  vt_tradecert.cert_no=? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, certNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            boolean runFirstTime = true;
            int noOfVeh = 0;
            String vchCatg = "";
            while (rs.next()) {
                if (runFirstTime) {
                    mapData = new HashMap();
                    mapData.put("stateName", rs.getString("statename"));
                    mapData.put("stateCd", rs.getString("state_cd"));
                    mapData.put("officeName", rs.getString("officename"));
                    mapData.put("certNo", rs.getString("cert_no"));
                    mapData.put("applNo", rs.getString("appl_no"));
                    String regnNo = rs.getString("appl_no");
                    int purCd = rs.getInt("pur_cd");
                    mapData.put("purCd", rs.getInt("pur_cd"));
                    int[] purCdArr = {purCd, TableConstants.TRADE_CERTIFICATE_TAX, TableConstants.TRADE_CERTIFICATE_SURCHARGE};
                    String receptNo = getReceiptNo(tmgr, regnNo, purCd);
                    if (CommonUtils.isNullOrBlank(receptNo)) {
                        receptNo = getReceiptNoForOnlineTradeCertAppl(tmgr, regnNo, purCd);
                    }
                    String amount = "";
                    if (receptNo != null) {
                        for (int purCode : purCdArr) {
                            amount = getFeesForTradeCert(tmgr, stateCd, offCd, receptNo, purCode);
                            if (!CommonUtils.isNullOrBlank(amount)) {
                                feeAmount += Long.valueOf(amount);
                            }
                        }
                    }

                    if (feeAmount != 0L) {
                        mapData.put("fees", String.valueOf(feeAmount));
                    } else {
                        mapData.put("fees", "");
                    }

                    if ("OR".equals(stateCd)) {
                        mapData.put("vchClass", ApplicationTradeCertImpl.getVehClassDesc(rs.getString("vch_catg")).toUpperCase());
                    }
                    mapData.put("dealerCode", rs.getString("dealerCode"));
                    mapData.put("dealerName", rs.getString("dealerName"));
                    if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc() //// using_online_schema_tc work  for Odisha
                            && (!CommonUtils.isNullOrBlank(applicantType) && (applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)))) {
                        mapData.put("showroomName", rs.getString("showroom_name"));
                    }
                    fullAddress.append("");
                    fullAddressShowroom.append("");
                    if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_MANUFACTURER)) {
                        if (rs.getString("m_add1") != null && !rs.getString("m_add1").equals("")) {
                            fullAddress.append(",").append(rs.getString("m_add1"));
                        }
                        if (rs.getString("m_add2") != null && !rs.getString("m_add2").equals("") && !rs.getString("m_add2").equals(rs.getString("m_add1"))) {
                            fullAddress.append(", ").append(rs.getString("m_add2"));
                        }
                        if (rs.getInt("district") != 0) {
                            fullAddress.append(", ").append(PublicApplicationTradeCertImpl.getDistrictNameFromCode(rs.getString("state_cd"), String.valueOf(rs.getInt("district"))).toUpperCase());
                        }
                        if (rs.getInt("pincode") != 0) {
                            fullAddress.append("- ").append(rs.getInt("pincode"));
                        }
                    } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER)) {
                        if (rs.getString("branch_add1") != null && !rs.getString("branch_add1").equals("")) {
                            fullAddress.append(",").append(rs.getString("branch_add1"));
                        }
                        if (rs.getString("branch_add2") != null && !rs.getString("branch_add2").equals("") && !rs.getString("branch_add2").equals(rs.getString("branch_add1"))) {
                            fullAddress.append(", ").append(rs.getString("branch_add2"));
                        }
                        if (rs.getInt("branch_district") != 0) {
                            fullAddress.append(", ").append(PublicApplicationTradeCertImpl.getDistrictNameFromCode(rs.getString("state_cd"), String.valueOf(rs.getInt("branch_district"))).toUpperCase());
                        }
                        if (rs.getInt("branch_pincode") != 0) {
                            fullAddress.append("- ").append(rs.getInt("branch_pincode"));
                        }
                    } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_RETRO_FITTER)) {
                        if (rs.getString("rf_add1") != null && !rs.getString("rf_add1").equals("")) {
                            fullAddress.append(",").append(rs.getString("rf_add1"));
                        }
                        if (rs.getString("rf_add2") != null && !rs.getString("rf_add2").equals("") && !rs.getString("rf_add2").equals(rs.getString("rf_add1"))) {
                            fullAddress.append(", ").append(rs.getString("rf_add2"));
                        }
                        if (rs.getInt("district") != 0) {
                            fullAddress.append(", ").append(PublicApplicationTradeCertImpl.getDistrictNameFromCode(rs.getString("state_cd"), String.valueOf(rs.getInt("district"))).toUpperCase());
                        }
                        if (rs.getInt("pincode") != 0) {
                            fullAddress.append("- ").append(rs.getInt("pincode"));
                        }
                    } else {
                        if (rs.getString("d_add1") != null && !rs.getString("d_add1").equals("")) {
                            fullAddress.append(",").append(rs.getString("d_add1"));
                        }
                        if (rs.getString("d_add2") != null && !rs.getString("d_add2").equals("") && !rs.getString("d_add2").equals(rs.getString("d_add1"))) {
                            fullAddress.append(", ").append(rs.getString("d_add2"));
                        }
                        if (rs.getInt("d_district") != 0) {
                            fullAddress.append(", ").append(PublicApplicationTradeCertImpl.getDistrictNameFromCode(rs.getString("state_cd"), String.valueOf(rs.getInt("d_district"))).toUpperCase());
                        }
                        if (rs.getInt("d_pincode") != 0) {
                            fullAddress.append("- ").append(rs.getInt("d_pincode"));
                        }

                        if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc() //// using_online_schema_tc work
                                && (!CommonUtils.isNullOrBlank(applicantType) && (applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)))) {
                            if (rs.getString("showroom_add1") != null && !rs.getString("showroom_add1").equals("")) {
                                fullAddressShowroom.append(",").append(rs.getString("showroom_add1"));
                            }
                            if (rs.getString("showroom_add2") != null && !rs.getString("showroom_add2").equals("") && !rs.getString("showroom_add2").equals(rs.getString("showroom_add1"))) {
                                fullAddressShowroom.append(", ").append(rs.getString("showroom_add2"));
                            }
                            if (rs.getInt("showroom_district") != 0) {
                                fullAddressShowroom.append(", ").append(PublicApplicationTradeCertImpl.getDistrictNameFromCode(rs.getString("state_cd"), String.valueOf(rs.getInt("showroom_district"))).toUpperCase());
                            }
                            if (rs.getInt("showroom_pincode") != 0) {
                                fullAddressShowroom.append("- ").append(rs.getInt("showroom_pincode"));
                            }

                            if (rs.getString("descr") != null && !rs.getString("descr").equals("")) {
                                fullAddressShowroom.append(", ").append(rs.getString("descr").toUpperCase());
                            }

                            if (fullAddressShowroom.toString().startsWith(",")) {
                                mapData.put("showroomAddress", fullAddressShowroom.substring(1).trim());
                            } else {
                                mapData.put("showroomAddress", fullAddressShowroom.toString().trim());
                            }
                        }
                    }

                    if (rs.getString("descr") != null && !rs.getString("descr").equals("")) {
                        fullAddress.append(", ").append(rs.getString("descr").toUpperCase());
                    }

                    if (fullAddress.toString().startsWith(",")) {
                        mapData.put("dealerAddress", fullAddress.substring(1).trim());
                    } else {
                        mapData.put("dealerAddress", fullAddress.toString().trim());
                    }

                    mapData.put("issueDate", sdf.format(rs.getDate("issue_dt")));
                    mapData.put("validUpto", sdf.format(rs.getDate("valid_upto")));
                    validityPeriod = " ( " + sdf.format(rs.getDate("valid_from")) + " - " + sdf.format(rs.getDate("valid_upto")) + " ) ";
                    mapData.put("validityPeriod", validityPeriod);
                }

                if (doNotShowNoOfVehicles) {
                    if (displayFuel) {
                        vchCatgNames += "," + rs.getString("vch_catg") + " [ {fuel:" + ApplicationTradeCertImpl.getFuelTypeDesc(rs.getInt("fuel_cd")) + "} ]";
                    } else {
                        vchCatgNames += "," + rs.getString("vch_catg");
                    }
                } else {
                    noOfVeh = rs.getInt("no_of_vch");
                    vchCatg = rs.getString("vch_catg");
                    if (displayFuel) {
                        vchCatgNames += "," + vchCatg + " [ " + noOfVeh + ":" + rs.getInt("no_of_vch_used") + "{fuel:" + ApplicationTradeCertImpl.getFuelTypeDesc(rs.getInt("fuel_cd")) + "} ]";
                    } else {
                        vchCatgNames += "," + vchCatg + " [ " + noOfVeh + ":" + rs.getInt("no_of_vch_used") + " ]";
                    }
                }
                if (runFirstTime) {
                    runFirstTime = false;
                }
            }
            mapData.put("vchCatgNames", vchCatgNames.substring(1));

            if (tmConfigDobj.getTmTradeCertConfigDobj().isSubListDataDetailsToBeShownInForm17()) {  ///// for Maharashtra
                List dobjSubList = new ArrayList();
                IssueTradeCertDobj issueTradeCertDobj = null;
                for (int i = 0; i < noOfVeh; i++) {
                    issueTradeCertDobj = new IssueTradeCertDobj();
                    issueTradeCertDobj.setStateName((String) mapData.get("stateName"));
                    issueTradeCertDobj.setStateCd((String) mapData.get("stateCd"));
                    issueTradeCertDobj.setOfficeName((String) mapData.get("officeName"));
                    issueTradeCertDobj.setTradeCertNo((String) mapData.get("certNo"));
                    if (!CommonUtils.isNullOrBlank((String) mapData.get("fees"))) {
                        Double feesPerTC = Double.valueOf((String) mapData.get("fees")) / noOfVeh;
                        issueTradeCertDobj.setFee(String.valueOf(feesPerTC));
                    }
                    issueTradeCertDobj.setDealerFor((String) mapData.get("dealerCode"));
                    issueTradeCertDobj.setDealerName((String) mapData.get("dealerName"));
                    issueTradeCertDobj.setDealerAddress((String) mapData.get("dealerAddress"));
                    issueTradeCertDobj.setIssueDtAsString((String) mapData.get("issueDate"));
                    issueTradeCertDobj.setValidUptoAsString((String) mapData.get("validUpto"));
                    issueTradeCertDobj.setVehCatgName(ApplicationTradeCertImpl.getVehCatgDesc(vchCatg));
                    issueTradeCertDobj.setSrNo((i + 1) + "");
                    issueTradeCertDobj.setNoOfAllowedVehicles(noOfVeh + "");


                    String text = " Trade Certificate No:" + issueTradeCertDobj.getTradeCertNo() + ", Dealer Name:" + issueTradeCertDobj.getDealerName() + ", Dealer Address:" + issueTradeCertDobj.getDealerAddress()
                            + ", Fee Amount:" + issueTradeCertDobj.getFee() + ", Vehicle Category Name:" + issueTradeCertDobj.getVehCatgName() + ", Valid Upto Date: " + issueTradeCertDobj.getValidUptoAsString()
                            + ", State Name:" + issueTradeCertDobj.getStateName() + ", Office Name:" + issueTradeCertDobj.getOfficeName();


                    text += ",Number Of Trade Certificates:" + noOfVeh;

                    issueTradeCertDobj.setText(text);

                    dobjSubList.add(issueTradeCertDobj);
                }
                mapData.put("dobjSubList", dobjSubList);
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Exception in fetching and mapping data for trade certificate printing from vt_trade_certificate.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return mapData;
    }

    public static Map getTradeCertificatePrintingFromVtForDL(String stateCd, int offCd, String certNo, Object... paramVarArgs) throws VahanException {
        Map mapData = null;
        TransactionManagerReadOnly tmgr = null;
        String vchCatgName = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        String validityPeriod = "";
        List<String> selectedDuplicateCertificates = null;
        List dobjSubList = null;
        String vchCatgCode = null;
        if (paramVarArgs.length > 0) {
            if (paramVarArgs[0] instanceof List) {
                dobjSubList = (List) paramVarArgs[0];
            }
            if (paramVarArgs.length > 1 && paramVarArgs[1] instanceof String) {
                vchCatgCode = (String) paramVarArgs[1];
            }
        }
        try {

            tmgr = new TransactionManagerReadOnly("getTradeCertificateApplPrintingFromVt");
            PreparedStatement ps = null;
            String sql = " select tmstate.state_code as state_cd,tmstate.descr as statename,tmoffice.off_name as officename, vt_tradecert.appl_no, "
                    + "      vd.dealer_name as dealerName,vd.dealer_cd as dealerCode, "
                    + "      COALESCE(vd.d_add1, ''::character varying)  || ',' || COALESCE(vd.d_add2, ''::character varying) || ',' || COALESCE(tmstate.descr, ''::character varying) as dealerAddress, "
                    + "      vt_tradecert.cert_no,vt_tradecert.vch_catg,vt_tradecert.valid_from,vt_tradecert.valid_upto,vt_tradecert.issue_dt,vt_tradecert.no_of_vch,vt_tradecert.no_vch_print,vt_tradecert.no_of_vch_used,vt_tradecert.selected_duplicate_certificate  "
                    + " from vt_trade_certificate vt_tradecert "
                    + "      LEFT JOIN vm_dealer_mast vd ON vd.dealer_cd::text =vt_tradecert.dealer_cd::text  "
                    + "      LEFT JOIN TM_STATE tmstate ON tmstate.state_code = vt_tradecert.state_cd  "
                    + "      LEFT JOIN tm_office tmoffice ON tmoffice.off_cd = vt_tradecert.off_cd AND tmoffice.state_cd = vt_tradecert.state_cd::bpchar  ";

            /////////// FOR NEW AND RENEW 
            sql += " WHERE  vt_tradecert.cert_no=? ";

            // FOR DUPLICATE
            if (vchCatgCode != null) {
                sql += " and vch_catg=?";
            }

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, certNo);
            if (vchCatgCode != null) {
                ps.setString(2, vchCatgCode);
            }
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            String receptNo = null;
            int noOfTcRequired = 0;
            int noOfTcPrintInitially = 0;


            while (rs.next()) {

                mapData = new HashMap();
                mapData.put("stateName", rs.getString("statename"));
                mapData.put("stateCd", rs.getString("state_cd"));
                mapData.put("officeName", rs.getString("officename"));
                mapData.put("certNo", rs.getString("cert_no"));
                mapData.put("applNo", rs.getString("appl_no"));
                String regnNo = rs.getString("appl_no");

                int purCd = TableConstants.VM_TRANSACTION_TRADE_CERT_ONLINE_APPLICATION;
                receptNo = getReceiptNo(tmgr, regnNo, purCd);
                if (receptNo == null) {
                    receptNo = getReceiptNo(tmgr, certNo, purCd);
                }
                if (receptNo == null) {
                    purCd = TableConstants.VM_TRANSACTION_TRADE_CERT_NEW;
                    receptNo = getReceiptNo(tmgr, regnNo, purCd);
                    if (receptNo == null) {
                        receptNo = getReceiptNoForTCRenew(tmgr, certNo, purCd);
                    }
                    if (receptNo == null) {
                        purCd = TableConstants.VM_TRANSACTION_TRADE_CERT_DUP;
                        receptNo = getReceiptNo(tmgr, regnNo, purCd);
                        if (receptNo == null) {
                            receptNo = getReceiptNo(tmgr, certNo, purCd);
                        }

                    }
                }

                if (receptNo != null) {
                    mapData.put("fees", getFeesForTradeCert(tmgr, stateCd, offCd, receptNo, purCd));
                    mapData.put("purCd", purCd);
                    mapData.put("receiptNumber", receptNo);

                } else {
                    mapData.put("fees", "");
                    mapData.put("receiptNumber", "");
                }
                mapData.put("dealerCode", rs.getString("dealerCode"));
                mapData.put("dealerName", rs.getString("dealerName"));
                mapData.put("dealerAddress", rs.getString("dealerAddress"));
                mapData.put("issueDate", sdf.format(rs.getDate("issue_dt")));
                validityPeriod = " ( " + sdf.format(rs.getDate("valid_from")) + " - " + sdf.format(rs.getDate("valid_upto")) + " ) ";
                mapData.put("validityPeriod", validityPeriod);
                mapData.put("validUpto", sdf.format(rs.getDate("valid_upto")));

                //in case of duplicate get the selected duplicate serial no.            
                if (!CommonUtils.isNullOrBlank(rs.getString("selected_duplicate_certificate"))) {
                    selectedDuplicateCertificates = Utility.convertStringToList(rs.getString("selected_duplicate_certificate"));
                    noOfTcRequired = selectedDuplicateCertificates.size();
                } else {
                    noOfTcRequired = rs.getInt("no_of_vch");
                }
                mapData.put("selectedDuplicateCertificate", selectedDuplicateCertificates);
                //in case of duplicate get the selected duplicate serial no.

                vchCatgName = ApplicationTradeCertImpl.getVehCatgDesc(rs.getString("vch_catg"));

                noOfTcPrintInitially = rs.getInt("no_vch_print");
                if (noOfTcPrintInitially >= noOfTcRequired) {
                    noOfTcPrintInitially = 0;
                }
                IssueTradeCertDobj issueTradeCertDobj = null;

                for (int i = noOfTcPrintInitially; i < noOfTcRequired; i++) {
                    issueTradeCertDobj = new IssueTradeCertDobj();
                    issueTradeCertDobj.setStateName((String) mapData.get("stateName"));
                    issueTradeCertDobj.setStateCd((String) mapData.get("stateCd"));
                    issueTradeCertDobj.setSerialNoWithTC(Util.getTmConfiguration().getTmTradeCertConfigDobj().isSerialNoWithTc());
                    issueTradeCertDobj.setOfficeName((String) mapData.get("officeName"));
                    issueTradeCertDobj.setTradeCertNo((String) mapData.get("certNo"));
                    issueTradeCertDobj.setFee((String) mapData.get("fees"));
                    issueTradeCertDobj.setReceiptNumber((String) mapData.get("receiptNumber"));
                    issueTradeCertDobj.setDealerFor((String) mapData.get("dealerCode"));
                    issueTradeCertDobj.setDealerName((String) mapData.get("dealerName"));
                    issueTradeCertDobj.setDealerAddress((String) mapData.get("dealerAddress"));
                    issueTradeCertDobj.setIssueDtAsString((String) mapData.get("issueDate"));
                    issueTradeCertDobj.setValidUptoAsString((String) mapData.get("validUpto"));
                    issueTradeCertDobj.setVehCatgName(vchCatgName);
                    if (selectedDuplicateCertificates != null && selectedDuplicateCertificates.size() > 0) {
                        issueTradeCertDobj.setSrNo(selectedDuplicateCertificates.get(i));
                    } else {
                        issueTradeCertDobj.setSrNo((i + 1) + "");
                    }
                    issueTradeCertDobj.setNoOfAllowedVehicles(noOfTcRequired + "");


                    String text = " Trade Certificate. No." + issueTradeCertDobj.getTradeCertNo() + " Dealer Name." + issueTradeCertDobj.getDealerName() + " Dealer Address." + issueTradeCertDobj.getDealerAddress()
                            + " Fee Amount." + issueTradeCertDobj.getFee() + " Vehicle Category. Name." + issueTradeCertDobj.getVehCatgName() + " Valid Upto Date. " + issueTradeCertDobj.getValidUptoAsString()
                            + " State Name." + issueTradeCertDobj.getStateName() + " Office Name." + issueTradeCertDobj.getOfficeName();


                    text += "Number Of Trade Certificates." + noOfTcRequired;

                    issueTradeCertDobj.setText(text);

                    if (dobjSubList != null) {
                        dobjSubList.add(issueTradeCertDobj);
                    }
                }
            }

            mapData.put("dobjSubList", dobjSubList);

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Exception in fetching and mapping data for trade certificate printing from vt_trade_certificate.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return mapData;
    }

    private static String getReceiptNo(TransactionManagerReadOnly tmgr, String regnNo, int purCd) {
        String receiptNo = null;
        PreparedStatement ps = null;
        try {
            String sql = "select rcpt_no from vt_fee where regn_no=? and pur_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setInt(2, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                receiptNo = rs.getString("rcpt_no");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return receiptNo;
    }

    private static String getReceiptNoForOnlineTradeCertAppl(TransactionManagerReadOnly tmgr, String regnNo, int purCd) {
        String receiptNo = null;
        PreparedStatement ps = null;
        try {
            String sql = "select rcpt_no from vph_rcpt_cart where appl_no=? and pur_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setInt(2, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                receiptNo = rs.getString("rcpt_no");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return receiptNo;
    }

    private static String getReceiptNoForTCRenew(TransactionManagerReadOnly tmgr, String regnNo, int purCd) {
        String receiptNo = null;
        PreparedStatement ps = null;
        try {
            String sql = "select rcpt_no from vt_fee where regn_no=? and pur_cd=? order by rcpt_dt desc";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setInt(2, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                receiptNo = rs.getString("rcpt_no");
                break;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return receiptNo;
    }

    private static String getFeesForTradeCert(TransactionManagerReadOnly tmgr, String stateCd, int officeCd, String receiptNo, int purCd) {
        String fees = null;
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        try {
            sql = "select * from get_rcpt_details(?,?,?) where pur_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, officeCd);
            ps.setString(3, receiptNo);
            ps.setInt(4, purCd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                fees = (rs.getLong("fees") + rs.getLong("fine")) + "";
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return fees;
    }

    public static ArrayList<DailyAndConsolidatedStmtReportdobj> getConsolidatedStmReport(String usercd, String off_cd, Date frDt, Date toDt, String statecd, int tv_ntv_class_code) throws VahanException {
        ArrayList<DailyAndConsolidatedStmtReportdobj> list = new ArrayList<>();
        TransactionManagerReadOnly tmgr = null;
        int sum = 0;
        boolean surcharge = false;
        boolean interest = false;
        boolean penalty = false;
        boolean amount1 = false;
        boolean amount2 = false;
        Exception e = null;
        boolean ispaymentShare = false;
        DailyAndConsolidatedStmtReportdobj dobjPymtShare = null;
        int i = 0;
        String whereClauseFunctionName;
        try {
            if (tv_ntv_class_code == 3) {
                whereClauseFunctionName = "summaryacctstmt( ?,  ?,  ?,  ?,  ?)";
            } else {
                whereClauseFunctionName = "summaryacctstmt_class_wise( ?,  ?,  ?,  ?,  ?,  ?)";
            }
            String sql = "SELECT a.*,b.cashier,b.accountant,b.superintendent,b.patorto,b.rto from " + whereClauseFunctionName + " a"
                    + " left join " + TableList.VM_ACCOUNT_STATEMENT_SIGNATURE + " b on  b.state_cd=?  and b.off_cd= ? ";
            tmgr = new TransactionManagerReadOnly("getConsolidatedStmReport");

            PreparedStatement ps = tmgr.prepareStatement(sql);
            int j = 1;
            ps.setString(j++, statecd);
            ps.setInt(j++, Integer.parseInt(off_cd));
            ps.setLong(j++, Long.parseLong(usercd));
            ps.setDate(j++, (java.sql.Date) frDt);
            ps.setDate(j++, (java.sql.Date) toDt);
            if (tv_ntv_class_code != 3) {
                ps.setInt(j++, tv_ntv_class_code);
            }
            ps.setString(j++, statecd);
            ps.setInt(j++, Integer.parseInt(off_cd));
            RowSet rs = tmgr.fetchDetachedRowSet();
            DailyAndConsolidatedStmtReportdobj dobj = null;
            while (rs.next()) {
                dobj = new DailyAndConsolidatedStmtReportdobj();
                if (rs.getString("purpose") != null && !rs.getString("purpose").equals("")) {
                    dobj.setTransaction(rs.getString("purpose"));
                    i++;
                    dobj.setSrindex(i);
                }
                if (tv_ntv_class_code != 3 && rs.getString("class_type") != null && !rs.getString("class_type").equals("")) {
                    dobj.setClass_type(rs.getString("class_type"));
                    dobj.setRenderClassType(true);
                } else {
                    dobj.setRenderClassType(false);
                }
                if (rs.getString("penalty") != null && !rs.getString("penalty").equals("")) {
                    if (Integer.parseInt(rs.getString("penalty").replace(".00", "")) > 0) {
                        penalty = true;
                    }
                    dobj.setFeeCM_Penalty_Tax(rs.getString("penalty").replace(".00", ""));
                }
                if (rs.getString("total") != null && !rs.getString("total").equals("")) {
                    dobj.setTotal(rs.getString("total").replace(".00", ""));
                    sum = sum + Integer.valueOf(rs.getString("total").replace(".00", ""));
                }
                if (rs.getString("amount") != null && !rs.getString("amount").isEmpty() && rs.getInt("pur_cd") == 80) {
                    dobjPymtShare = isPaymentShare(statecd);
                    if (dobjPymtShare != null && dobjPymtShare.getVenderShare() != null && !dobjPymtShare.getVenderShare().equals("") && dobjPymtShare.getDepartmentShare() != null && !dobjPymtShare.getDepartmentShare().equals("")) {
                        DecimalFormat decimalFormat = new DecimalFormat("#.##");
                        dobj.setAmount(decimalFormat.format((rs.getFloat("amount") / (dobjPymtShare.getVenderShare() + dobjPymtShare.getDepartmentShare())) * dobjPymtShare.getDepartmentShare()));
                        dobj.setAmountVendor(decimalFormat.format((rs.getFloat("amount") / (dobjPymtShare.getVenderShare() + dobjPymtShare.getDepartmentShare())) * dobjPymtShare.getVenderShare()));
                        ispaymentShare = true;
                    } else {
                        if (rs.getString("amount") != null && !rs.getString("amount").equals("")) {
                            dobj.setAmount(rs.getString("amount").replace(".00", ""));
                        }
                    }
                } else {
                    if (rs.getString("amount") != null && !rs.getString("amount").equals("")) {
                        dobj.setAmount(rs.getString("amount").replace(".00", ""));
                    }
                }
                dobj.setGrandTotal(sum);

                if (rs.getString("interest") != null && !rs.getString("interest").equals("")) {
                    if (Integer.parseInt(rs.getString("interest").replace(".00", "")) > 0) {
                        interest = true;
                    }
                    dobj.setInterest(rs.getString("interest").replace(".00", ""));
                }
                if (rs.getString("surcharge") != null && !rs.getString("surcharge").equals("")) {
                    if (Integer.parseInt(rs.getString("surcharge").replace(".00", "")) > 0) {
                        surcharge = true;
                    }
                    dobj.setSurcharge(rs.getString("surcharge").replace(".00", ""));
                }
                if (rs.getString("amount1") != null && !rs.getString("amount1").equals("")) {
                    if (Integer.parseInt(rs.getString("amount1").replace(".00", "")) > 0) {
                        amount1 = true;
                    }
                    dobj.setAmount1(rs.getString("amount1").replace(".00", ""));
                }
                if (rs.getString("amount2") != null && !rs.getString("amount2").equals("")) {
                    if (Integer.parseInt(rs.getString("amount2").replace(".00", "")) > 0) {
                        amount2 = true;
                    }
                    dobj.setAmount2(rs.getString("amount2").replace(".00", ""));
                }
                DateFormat frm_dte_formatter = new SimpleDateFormat("dd-MMM-yyyy");
                String frdateStr = frm_dte_formatter.format(((java.util.Date) frDt));
                String todateStr = frm_dte_formatter.format(((java.util.Date) toDt));
                dobj.setFromCashDate(frdateStr);
                dobj.setToCashDate(todateStr);
                dobj.setIs_interest(interest);
                dobj.setIs_penalty(penalty);
                dobj.setIs_surcharge(surcharge);
                dobj.setIs_amount1(amount1);
                dobj.setIs_amount2(amount2);
                dobj.setCashier(rs.getString("cashier"));
                dobj.setAccountant(rs.getString("accountant"));
                dobj.setSuperintendent(rs.getString("superintendent"));
                dobj.setPatorto(rs.getString("patorto"));
                dobj.setRto(rs.getString("rto"));
                dobj.setIspaymentShare(ispaymentShare);
                list.add(dobj);
            }
        } catch (SQLException se) {
            e = se;
        } catch (Exception ex) {
            e = ex;
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
        if (e != null) {
            throw new VahanException("Error in method getting Consolidated Statement Details");
        }
        return list;

    }

    public static ArrayList<DailyAndConsolidatedStmtReportdobj> getHeadWiseConsolidatedStmReport(String usercd, String off_cd, Date frDt, Date toDt, String statecd, int tv_ntv_class_code) throws VahanException {
        ArrayList<DailyAndConsolidatedStmtReportdobj> list = new ArrayList<>();
        TransactionManagerReadOnly tmgr = null;
        Exception e = null;
        int i = 0;
        String whereClauseFunctionName;
        try {
            int j = 1;
            if (tv_ntv_class_code == 3) {
                whereClauseFunctionName = "summaryacctstmtheadwise( ?,  ?,  ?,  ?,  ?)";
            } else {
                whereClauseFunctionName = "summaryacctstmtheadwise_class_wise( ?,  ?,  ?,  ?,  ?,  ?)";
            }
            String sql = "SELECT * from " + whereClauseFunctionName + " "
                    + " left join " + TableList.VM_ACCOUNT_STATEMENT_SIGNATURE + " b on b.state_cd=?  and b.off_cd= ? ";
            tmgr = new TransactionManagerReadOnly("getConsolidatedStmReport");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(j++, statecd);
            ps.setInt(j++, Integer.parseInt(off_cd));
            ps.setLong(j++, Long.parseLong(usercd));
            ps.setDate(j++, (java.sql.Date) frDt);
            ps.setDate(j++, (java.sql.Date) toDt);
            if (tv_ntv_class_code != 3) {
                ps.setInt(j++, tv_ntv_class_code);
            }
            ps.setString(j++, statecd);
            ps.setInt(j++, Integer.parseInt(off_cd));
            RowSet rs = tmgr.fetchDetachedRowSet();
            DailyAndConsolidatedStmtReportdobj dobj = null;
            while (rs.next()) {
                dobj = new DailyAndConsolidatedStmtReportdobj();
                if (rs.getString("acc_head") != null && !rs.getString("acc_head").equals("")) {
                    dobj.setAccHead(rs.getString("acc_head"));
                    i++;
                    dobj.setSrindex(i);
                }
                if (tv_ntv_class_code != 3 && rs.getString("class_type") != null && !rs.getString("class_type").equals("")) {
                    dobj.setClass_type(rs.getString("class_type"));
                    dobj.setRenderClassType(true);
                } else {
                    dobj.setRenderClassType(false);
                }
                if (rs.getString("amount") != null && !rs.getString("amount").equals("")) {
                    dobj.setAmount(rs.getString("amount").replace(".00", ""));
                    dobj.setTotal(rs.getString("amount").replace(".00", ""));
                }
                DateFormat frm_dte_formatter = new SimpleDateFormat("dd-MMM-yyyy");
                String frdateStr = frm_dte_formatter.format(((java.util.Date) frDt));
                String todateStr = frm_dte_formatter.format(((java.util.Date) toDt));
                dobj.setFromCashDate(frdateStr);
                dobj.setToCashDate(todateStr);
                dobj.setCashier(rs.getString("cashier"));
                dobj.setAccountant(rs.getString("accountant"));
                dobj.setSuperintendent(rs.getString("superintendent"));
                dobj.setPatorto(rs.getString("patorto"));
                dobj.setRto(rs.getString("rto"));
                list.add(dobj);
            }
        } catch (SQLException ee) {
            e = ee;
        } catch (Exception ex) {
            e = ex;
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
        if (e != null) {
            throw new VahanException("Error in getting HeadWiseConsolidated Statement Details");
        }
        return list;

    }

    public static ArrayList<DailyAndConsolidatedStmtReportdobj> getDailyCashReport(String usercd, Date frDt, Date toDt, String statecd, String off_cd) throws VahanException {
        ArrayList<DailyAndConsolidatedStmtReportdobj> list = new ArrayList<>();
        TransactionManagerReadOnly tmgr = null;
        Exception e = null;
        int i = 0;
        long sum = 0;
        try {
            String sql = "select rcpt_dt,regn_no,owner_name,rcpt_no,purpose,off_name, payment_mode, amount,surcharge,interest, penalty,amount1,amount2,total,c.tax1,c.tax2,d.cashier,d.accountant,d.superintendent,d.patorto,d.rto"
                    + " from dailyacctstmt(?,?,?,?,?) a "
                    + " left join " + TableList.TM_OFFICE + " b on  b.state_cd=?  and b.off_cd= ? "
                    + " left join " + TableList.VM_TAX_FIELD_LABEL + " c on  c.state_cd=?"
                    + " left join " + TableList.VM_ACCOUNT_STATEMENT_SIGNATURE + " d on  d.state_cd=?  and d.off_cd=? ";
            tmgr = new TransactionManagerReadOnly("getDailyCashReport");

            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, statecd);
            ps.setInt(2, Integer.parseInt(off_cd));
            ps.setLong(3, Long.parseLong(usercd));
            ps.setDate(4, (java.sql.Date) frDt);
            ps.setDate(5, (java.sql.Date) toDt);
            ps.setString(6, statecd);
            ps.setInt(7, Integer.parseInt(off_cd));
            ps.setString(8, statecd);
            ps.setString(9, statecd);
            ps.setInt(10, Integer.parseInt(off_cd));

            RowSet rs = tmgr.fetchDetachedRowSet();
            DailyAndConsolidatedStmtReportdobj dobj = null;
            boolean dailyCashRcptSurcharge = false;
            boolean dailyCashRcptInterest = false;
            boolean dailyCashAmt1 = false;
            boolean dailyCashAmt2 = false;
            String dailyCashAmt1Label = null;
            String dailyCashAmt2Label = null;
            boolean ispaymentShare = false;
            DailyAndConsolidatedStmtReportdobj dobjPymtShare = null;
            while (rs.next()) {
                dobj = new DailyAndConsolidatedStmtReportdobj();
                if (rs.getString("rcpt_dt") != null && !rs.getString("rcpt_dt").equals("")) {
                    dobj.setReceived_dt(rs.getString("rcpt_dt"));
                    i++;
                    dobj.setSrindex(i);
                }
                if (rs.getString("rcpt_no") != null && !rs.getString("rcpt_no").equals("")) {
                    dobj.setReceipt_No(rs.getString("rcpt_no"));
                }
                if (rs.getString("owner_name") != null && !rs.getString("owner_name").equals("")) {
                    dobj.setName(rs.getString("owner_name"));
                }
                if (rs.getString("regn_no") != null && !rs.getString("regn_no").equals("")) {
                    dobj.setRegn_no(rs.getString("regn_no"));
                }
                if (rs.getString("penalty") != null && !rs.getString("penalty").equals("")) {
                    dobj.setFeeCM_Penalty_Tax(rs.getString("penalty").replace(".00", ""));
                }
                if (rs.getString("total") != null && !rs.getString("total").equals("")) {
                    dobj.setTotal(rs.getString("total").replace(".00", ""));
                }
                if (rs.getString("purpose") != null && !rs.getString("purpose").equals("")) {
                    dobj.setTransaction(rs.getString("purpose"));
                }
                if (rs.getString("amount") != null && !rs.getString("amount").isEmpty() && rs.getString("purpose") != null && !rs.getString("purpose").isEmpty() && rs.getString("purpose").contains("Smart Card Fee")) {
                    dobjPymtShare = isPaymentShare(statecd);
                    if (dobjPymtShare != null && dobjPymtShare.getVenderShare() != null && !dobjPymtShare.getVenderShare().equals("") && dobjPymtShare.getDepartmentShare() != null && !dobjPymtShare.getDepartmentShare().equals("")) {
                        DecimalFormat decimalFormat = new DecimalFormat("#.##");
                        dobj.setAmount(decimalFormat.format((rs.getFloat("amount") / (dobjPymtShare.getVenderShare() + dobjPymtShare.getDepartmentShare())) * dobjPymtShare.getDepartmentShare()));
                        dobj.setAmountVendor(decimalFormat.format((rs.getFloat("amount") / (dobjPymtShare.getVenderShare() + dobjPymtShare.getDepartmentShare())) * dobjPymtShare.getVenderShare()));
                        ispaymentShare = true;
                    } else {
                        if (rs.getString("amount") != null && !rs.getString("amount").equals("")) {
                            dobj.setAmount(rs.getString("amount").replace(".00", ""));
                        }
                    }
                } else {
                    if (rs.getString("amount") != null && !rs.getString("amount").equals("")) {
                        dobj.setAmount(rs.getString("amount").replace(".00", ""));
                    }
                }
                if (rs.getString("surcharge") != null && !rs.getString("surcharge").equals("")) {
                    dobj.setSurcharge(rs.getString("surcharge"));
                    if (!dailyCashRcptSurcharge && Integer.parseInt(rs.getString("surcharge").replace(".00", "")) > 0) {
                        dailyCashRcptSurcharge = true;
                    }
                }
                if (rs.getString("interest") != null && !rs.getString("interest").equals("")) {
                    dobj.setInterest(rs.getString("interest").replace(".00", ""));
                    if (!dailyCashRcptInterest && Integer.parseInt(rs.getString("interest").replace(".00", "")) > 0) {
                        dailyCashRcptInterest = true;
                    }
                }
                if (rs.getString("amount1") != null && !rs.getString("amount1").equals("")) {
                    dobj.setAmount1(rs.getString("amount1").replace(".00", ""));
                    if (!dailyCashAmt1 && Integer.parseInt(rs.getString("amount1").replace(".00", "")) > 0) {
                        dailyCashAmt1 = true;
                        dailyCashAmt1Label = rs.getString("tax1");
                    }
                }
                if (rs.getString("amount2") != null && !rs.getString("amount2").equals("")) {
                    dobj.setAmount2(rs.getString("amount2").replace(".00", ""));
                    if (!dailyCashAmt2 && Integer.parseInt(rs.getString("amount2").replace(".00", "")) > 0) {
                        dailyCashAmt2 = true;
                        dailyCashAmt2Label = rs.getString("tax2");
                    }
                }
                dobj.setCashier(rs.getString("cashier"));
                dobj.setAccountant(rs.getString("accountant"));
                dobj.setSuperintendent(rs.getString("superintendent"));
                dobj.setPatorto(rs.getString("patorto"));
                dobj.setRto(rs.getString("rto"));
                DateFormat frm_dte_formatter = new SimpleDateFormat("dd-MMM-yyyy");
                String frdateStr = frm_dte_formatter.format(((java.util.Date) frDt));
                String todateStr = frm_dte_formatter.format(((java.util.Date) toDt));
                dobj.setFromCashDate(frdateStr);
                dobj.setToCashDate(todateStr);
                dobj.setIs_surcharge(dailyCashRcptSurcharge);
                dobj.setIs_interest(dailyCashRcptInterest);
                dobj.setIs_amount1(dailyCashAmt1);
                dobj.setIs_amount2(dailyCashAmt2);
                dobj.setIspaymentShare(ispaymentShare);
                list.add(dobj);
            }


        } catch (SQLException ee) {
            e = ee;
        } catch (Exception ex) {
            e = ex;
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
        if (e != null) {
            throw new VahanException("Error in getHeadWiseConsolidatedStmReport  to select records");
        }
        return list;

    }

    public static ArrayList<DailyAndConsolidatedStmtReportdobj> getCancelReport(String usercd, Date frDt, Date toDt, String statecd, String off_cd) throws VahanException {
        ArrayList<DailyAndConsolidatedStmtReportdobj> list = new ArrayList<>();
        TransactionManagerReadOnly tmgr = null;
        Exception e = null;
        try {
            String sql = " select  array_to_string " + " ( "
                    + " array( "
                    + " SELECT distinct vt_fee_cancel.rcpt_no FROM " + TableList.VT_FEE_CANCEL + " where date(vt_fee_cancel.rcpt_dt) between ? and ? "
                    + " and vt_fee_cancel.collected_by = ?::text and vt_fee_cancel.state_cd=? and vt_fee_cancel.off_cd=? "
                    + " union "
                    + "	SELECT distinct vt_tax_cancel.rcpt_no FROM vt_tax_cancel where date(vt_tax_cancel.rcpt_dt) between ? and ? "
                    + "   and vt_tax_cancel.collected_by = ?::text  and vt_tax_cancel.state_cd=? and vt_tax_cancel.off_cd =? ),',') "
                    + "  as rcpt_cancel_list ";
            tmgr = new TransactionManagerReadOnly("getCancelReport");

            PreparedStatement ps = tmgr.prepareStatement(sql);

            ps.setDate(1, (java.sql.Date) frDt);
            ps.setDate(2, (java.sql.Date) toDt);
            ps.setLong(3, Long.parseLong(usercd));
            ps.setString(4, statecd);
            ps.setInt(5, Integer.parseInt(off_cd));
            ps.setDate(6, (java.sql.Date) frDt);
            ps.setDate(7, (java.sql.Date) toDt);
            ps.setLong(8, Long.parseLong(usercd));
            ps.setString(9, statecd);
            ps.setInt(10, Integer.parseInt(off_cd));
            RowSet rs = tmgr.fetchDetachedRowSet();
            DailyAndConsolidatedStmtReportdobj dobj = null;
            while (rs.next()) {
                dobj = new DailyAndConsolidatedStmtReportdobj();
                if (rs.getString("rcpt_cancel_list") != null && !rs.getString("rcpt_cancel_list").equals("")) {
                    dobj.setCancel_receipt(rs.getString("rcpt_cancel_list"));
                }
                list.add(dobj);
            }
        } catch (SQLException ee) {
            e = ee;
        } catch (Exception ex) {
            e = ex;
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
        if (e != null) {
            throw new VahanException("Error in getting Cancel Receipt Details");
        }
        return list;

    }

    public static ArrayList<DailyAndConsolidatedStmtReportdobj> getDailyDraftReport(Date frm_dt, Date to_dt, String usercd, String username, String offcd, String offname, String statecd, String statename, String instrument, String bankcode) throws VahanException {

        ArrayList<DailyAndConsolidatedStmtReportdobj> list = new ArrayList<>();
        TransactionManagerReadOnly tmgr = null;
        String whereClauseUserCd = "";
        Exception e = null;
        if (usercd != null && !usercd.equals("") && !usercd.equals("0")) {
            whereClauseUserCd = " AND a.collected_by = '" + usercd + "' ";
        }
        if (offcd != null && !offcd.equals("") && !offcd.equals("0")) {
            whereClauseUserCd += " AND a.off_cd=" + offcd + " ";
        }
        int sum = 0;
        try {

            String sql = "SELECT a.instrument_cd, a.instrument_type,  a.instrument_no, a.instrument_dt, a.instrument_amt, a.bank_code, a.bank_name, a.branch_name, a.bank_response_dt, a.collected_by,a.descr,a.cashier,a.accountant,a.superintendent,a.patorto,a.rto, string_agg(a.appl_no, '<br/>') as appl_no, string_agg(a.rcpt_no, '<br/>') as rcpt_no\n"
                    + " FROM (\n"
                    + "  SELECT a.instrument_cd, a.instrument_type,  a.instrument_no, a.instrument_dt, a.instrument_amt,b.appl_no,b.rcpt_no,   a.bank_code, c.descr as bank_name, a.branch_name, a.bank_response_dt, a.collected_by,d.descr,e.cashier,e.accountant,e.superintendent,e.patorto,e.rto  \n"
                    + "  FROM VT_INSTRUMENTS a  \n"
                    + "  INNER JOIN vp_appl_rcpt_mapping b ON b.instrument_cd = a.instrument_cd and b.state_cd=a.state_cd and b.off_cd=a.off_cd   \n"
                    + "  LEFT OUTER JOIN TM_BANK c  ON c.bank_code = a.bank_code  LEFT OUTER JOIN TM_INSTRUMENTS d  ON d.code = a.instrument_type  \n"
                    + "  LEFT OUTER JOIN vm_account_satatement_signature e  ON e.state_cd = a.state_cd and e.off_cd=a.off_cd  \n"
                    + " WHERE 	a.state_cd = ? " + whereClauseUserCd + " AND   a.instrument_type = ? "
                    + " AND 	a.received_dt between ?::date and (?::date + '1 day'::interval) "
                    + " AND	CASE when UPPER(?) = 'ALL' THEN true ELSE a.bank_code = UPPER(?) end  "
                    + "  ORDER BY  7 \n"
                    + " ) a\n"
                    + " GROUP BY 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16\n"
                    + " ORDER BY 1";
            tmgr = new TransactionManagerReadOnly("getDailyDraftReport");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, statecd);
            ps.setString(2, instrument);
            ps.setDate(3, (java.sql.Date) frm_dt);
            ps.setDate(4, (java.sql.Date) to_dt);
            ps.setString(5, bankcode);
            ps.setString(6, bankcode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            DailyAndConsolidatedStmtReportdobj dobj = null;
            while (rs.next()) {
                dobj = new DailyAndConsolidatedStmtReportdobj();
                if (rs.getString("instrument_amt") != null && !rs.getString("instrument_amt").equals("")) {
                    dobj.setInstrument(rs.getString("instrument_amt"));
                }
                if (rs.getString("bank_name") != null && !rs.getString("bank_name").equals("")) {
                    dobj.setBankName(rs.getString("bank_name"));
                }
                if (rs.getString("branch_name") != null && !rs.getString("branch_name").equals("")) {
                    dobj.setBranchName(rs.getString("branch_name"));
                }
                if (rs.getString("instrument_no") != null && !rs.getString("instrument_no").equals("")) {
                    dobj.setInstrument_no(rs.getString("instrument_no"));
                }

                if (rs.getDate("instrument_dt") != null && !rs.getString("instrument_dt").equals("")) {
                    DateFormat frm_dte_formatter = new SimpleDateFormat("dd-MMM-yyyy");
                    String instrumentDate = frm_dte_formatter.format(((java.util.Date) rs.getDate("instrument_dt")));
                    // ins_date_format.format(((java.util.Date) rs.getDate("instrument_dt")));
                    dobj.setInstrumentDate(instrumentDate);
                }
                if (rs.getString("bank_code") != null && !rs.getString("bank_code").equals("")) {
                    dobj.setBankcode(rs.getString("bank_code"));
                    DateFormat frm_dte_formatter = new SimpleDateFormat("dd-MMM-yyyy");
                    String frdateStr = frm_dte_formatter.format(((java.util.Date) frm_dt));
                    String todateStr = frm_dte_formatter.format(((java.util.Date) to_dt));
                    dobj.setFromDraftDate(frdateStr);
                    dobj.setToDraftDate(todateStr);
                }
                if (rs.getString("appl_no") != null && !rs.getString("appl_no").equals("")) {
                    dobj.setApplication_No(rs.getString("appl_no"));
                }
                if (rs.getString("rcpt_no") != null && !rs.getString("rcpt_no").equals("")) {
                    dobj.setReceipt_No(rs.getString("rcpt_no"));
                }
                dobj.setReportHeader(rs.getString("descr") + " Report");
                int total = Integer.parseInt(dobj.getInstrument());
                sum = sum + total;
                dobj.setCashier(rs.getString("cashier"));
                dobj.setAccountant(rs.getString("accountant"));
                dobj.setSuperintendent(rs.getString("superintendent"));
                dobj.setPatorto(rs.getString("patorto"));
                dobj.setRto(rs.getString("rto"));
                dobj.setGrandTotal(sum);
                // dobj.getInstrument();
                list.add(dobj);
            }

            //list.add(dobj);         
        } catch (SQLException ee) {
            e = ee;
        } catch (Exception ex) {
            e = ex;
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
        if (e != null) {
            throw new VahanException("Error in getting Draft Details");
        }
        return list;

    }

    public static PrintReceiptDtlsDobj getMasterReceiptdetails(String rcpt_no) {
        PrintReceiptDtlsDobj masterDobj = null;
        TransactionManagerReadOnly tmgr = null;
        String sqlOwner = null;
        try {

            sqlOwner = "select a.* , b.descr as vh_class_descr "
                    + "  from get_rcpt_details(?,?,?) a "
                    + "  left outer join vm_vh_class b on b.vh_class = a.vh_class";

            tmgr = new TransactionManagerReadOnly("getMasterReceiptdetails");
            PreparedStatement ps = tmgr.prepareStatement(sqlOwner);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserOffCode());
            ps.setString(3, rcpt_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                masterDobj = new PrintReceiptDtlsDobj();
                masterDobj.setStateName(ServerUtil.getRcptHeading());
                masterDobj.setVhClass(rs.getString("vh_class_descr"));
                masterDobj.setRtoOff(rs.getString("office"));
                masterDobj.setReceiptNo(rs.getString("rcpt_no"));
                masterDobj.setReceiptDate(rs.getString("rcpt_dt"));
                masterDobj.setRegnNo(rs.getString("regn_no"));
                masterDobj.setOwnerName(rs.getString("owner_name"));
                masterDobj.setChasino(rs.getString("chasi_no"));
                masterDobj.setRcptNo(rcpt_no);
                masterDobj.setApplNo(rs.getString("appl_no"));
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
        return masterDobj;
    }

    public static List<ApplicationFeeTradeCertDobj> getMasterReceiptDetailsForTradeCertificate(String stateCd, int offCd, String RcptNo) {
        ApplicationFeeTradeCertDobj masterDobj = null;
        TransactionManagerReadOnly tmgr = null;
        List<ApplicationFeeTradeCertDobj> list = new ArrayList<>();
        try {
            String sqlOwner = "select rcptDtls.fees as fees,rcptDtls.fine as fine,rcptDtls.rcpt_dt as receiptdate,rcptDtls.rcpt_no as receiptno,rcptDtls.purpose as purpose,tmstate.descr as statename,rcptDtls.office as rtooffice,"
                    + " vatradecer.appl_no as applno,vatradecer.sr_no as srno,vatradecer.no_of_vch as noOfVeh,vmvchcatg.catg_desc as vhcatg,vmdealer.dealer_name as dealer"
                    + " FROM " + TableList.VA_TRADE_CERTIFICATE + " as vatradecer "
                    + " JOIN " + TableList.TM_STATE + " tmstate ON tmstate.state_code = vatradecer.state_cd"
                    + " JOIN get_rcpt_details(?,?,?) rcptDtls on rcptDtls.regn_no = vatradecer.appl_no"
                    + " JOIN " + TableList.VM_DEALER_MAST + " vmdealer ON vmdealer.dealer_cd = vatradecer.dealer_cd "
                    + " JOIN " + TableList.VM_VCH_CATG + " vmvchcatg ON vmvchcatg.catg = vatradecer.vch_catg "
                    + " WHERE vatradecer.state_cd=?";
            tmgr = new TransactionManagerReadOnly("getMasterReceiptDetailsForTradeCertificate");
            PreparedStatement ps = tmgr.prepareStatement(sqlOwner);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, RcptNo);
            ps.setString(4, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                masterDobj = new ApplicationFeeTradeCertDobj();
                masterDobj.setStateName(rs.getString("statename"));
                masterDobj.setOfficeName(rs.getString("rtooffice"));
                masterDobj.setReceiptNumber(rs.getString("receiptno"));
                masterDobj.setReceiptDateAsString(rs.getString("receiptdate"));
                masterDobj.setApplNo(rs.getString("applno"));
                masterDobj.setVehCatgName(rs.getString("vhcatg"));
                masterDobj.setDealerName(rs.getString("dealer"));
                masterDobj.setNoOfAllowedVehicles(rs.getInt("noOfVeh") + "");
                masterDobj.setFeeCollected(String.valueOf(rs.getInt("fees")));
                masterDobj.setFineCollected(String.valueOf(rs.getInt("fine")));
                masterDobj.setAmount((rs.getInt("fees") + rs.getInt("fine")) + "");
                masterDobj.setPurpose(rs.getString("purpose"));

                list.add(masterDobj);
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
        return list;
    }

    public static List<PrintRcptParticularDtlsDobj> getParticularReceiptdetails(String rcpt_no) {
        List<PrintRcptParticularDtlsDobj> list = new ArrayList<PrintRcptParticularDtlsDobj>();
        TransactionManagerReadOnly tmgr = null;
        PrintRcptParticularDtlsDobj particularDOBJ = null;
        try {
            tmgr = new TransactionManagerReadOnly("getParticularReceiptdetails");
            String sqlOwnerList = "select * from get_rcpt_details(?,?,?)";
            PreparedStatement psList = tmgr.prepareStatement(sqlOwnerList);
            psList.setString(1, Util.getUserStateCode());
            psList.setInt(2, Util.getUserOffCode());
            psList.setString(3, rcpt_no);
            RowSet rsList = tmgr.fetchDetachedRowSet();
            while (rsList.next()) {
                particularDOBJ = new PrintRcptParticularDtlsDobj();
                particularDOBJ.setDateFrom(rsList.getString("period_from"));
                particularDOBJ.setDateUpto(rsList.getString("period_upto"));
                particularDOBJ.setAmount(rsList.getInt("fees") + "");
                particularDOBJ.setPenalty(rsList.getInt("fine") + "");
                particularDOBJ.setTotal(rsList.getInt("fees") + rsList.getInt("fine"));
                particularDOBJ.setPurpose(rsList.getString("purpose"));
                particularDOBJ.setEmpName(rsList.getString("emp_name"));
                list.add(particularDOBJ);
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
        return list;
    }

    public static ArrayList<DailyAndConsolidatedStmtReportdobj> getDDDetails(String statecd, String off_cd, String usercd, Date frDt, Date toDt) throws VahanException {
        ArrayList<DailyAndConsolidatedStmtReportdobj> list = new ArrayList<>();
        TransactionManagerReadOnly tmgr = null;
        Exception e = null;
        try {
            String sql = " select instrument,instrument_total  from summaryinstrumentstmt(?,?,?,?,?)";
            tmgr = new TransactionManagerReadOnly("getCancelReport");

            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, statecd);
            ps.setInt(2, Integer.parseInt(off_cd));
            ps.setLong(3, Long.parseLong(usercd));
            ps.setDate(4, (java.sql.Date) frDt);
            ps.setDate(5, (java.sql.Date) toDt);
            RowSet rs = tmgr.fetchDetachedRowSet();
            DailyAndConsolidatedStmtReportdobj dobj = null;
            Long totalAmt = 0l;
            while (rs.next()) {
                dobj = new DailyAndConsolidatedStmtReportdobj();
                if (Integer.parseInt(rs.getString("instrument_total")) > 0) {
                    dobj.setInstrumentDeac("By " + rs.getString("instrument"));
                    dobj.setInstrumentAmt(rs.getString("instrument_total") + "/-");
                    if (!rs.getString("instrument").equalsIgnoreCase("ManualPaidReceipt")) {
                        totalAmt += (rs.getInt("instrument_total"));
                    }
                }
                dobj.setTotal(String.valueOf(totalAmt));
                list.add(dobj);
            }
        } catch (SQLException ee) {
            e = ee;
        } catch (Exception ex) {
            e = ex;
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
        if (e != null) {
            throw new VahanException("Error in getting Draft Details");
        }
        return list;

    }

    public static void insertIntoVmAccStmtSignIfNotExist(String state_cd, int off_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;

        try {
            tmgr = new TransactionManager("insertIntoVmAccStmtSignIfNotExist");
            sql = "SELECT * FROM " + TableList.VM_ACCOUNT_STATEMENT_SIGNATURE
                    + " WHERE state_cd = ?  and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (!rs.next()) {
                sql = "insert into " + TableList.VM_ACCOUNT_STATEMENT_SIGNATURE + " (select state_cd,?,cashier,accountant,superintendent,patorto,rto from " + TableList.VM_ACCOUNT_STATEMENT_SIGNATURE + " where state_cd=? limit 1)";
                ps = tmgr.prepareStatement(sql);
                ps.setInt(1, off_cd);
                ps.setString(2, state_cd);
                ps.executeUpdate();
                tmgr.commit();
            }
        } catch (SQLException e) {
            throw new VahanException("Problem in getting Details from VM_ACCOUNT_STATEMENT_SIGNATURE!!");
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

    public static DailyAndConsolidatedStmtReportdobj isPaymentShare(String state_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        DailyAndConsolidatedStmtReportdobj dobj = new DailyAndConsolidatedStmtReportdobj();

        try {
            tmgr = new TransactionManagerReadOnly("isPaymentShare");
            sql = "SELECT * FROM tm_payment_share WHERE state_cd = ?  and pur_cd=80 and valid_upto >= current_date";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {

                if (rs.getFloat("depart_share") != 0l && rs.getFloat("depart_share") > 0l) {
                    dobj.setDepartmentShare(rs.getFloat("depart_share"));
                }
                if (rs.getFloat("vendor_share") != 0l && rs.getFloat("vendor_share") > 0l) {
                    dobj.setVenderShare(rs.getFloat("vendor_share"));
                }

            }
        } catch (SQLException e) {
            throw new VahanException("Problem in getting Details from tm_payment_share!!");
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
        return dobj;
    }
}
