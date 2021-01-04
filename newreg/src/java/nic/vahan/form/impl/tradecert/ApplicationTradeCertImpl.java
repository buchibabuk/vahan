/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.tradecert;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.common.jsf.utils.validators.POSValidator;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.user_mgmt.dobj.DealerMasterDobj;
import nic.vahan.form.bean.tradecert.VerifyApproveTradeCertBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.tradecert.ApplicationTradeCertDobj;
import nic.vahan.form.dobj.tradecert.IssueTradeCertDobj;
import nic.vahan.form.dobj.tradecert.TradeCertificateDobj;
import nic.vahan.form.dobj.tradecert.VehicleClassCategoryMappingDobj;
import nic.vahan.form.dobj.tradecert.VerifyApproveTradeCertDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import nic.vahan.services.DmsDocCheckUtils;
import nic.vahan.services.VTDocumentModel;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC081
 */
public class ApplicationTradeCertImpl {

    private static final Logger LOGGER = Logger.getLogger(ApplicationTradeCertImpl.class);

    public static void disposeApplicationForOdisha(Status_dobj statusDobj, String applicantCd) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("ApplicationTradeCertImpl.disposeApplicationForOdisha()");
            deleteApplcantMastApplDetails(tmgr, applicantCd);
            insertTCApplicationToHistory(tmgr, statusDobj.getAppl_no());
            ServerUtil.deleteFromTable(tmgr, null, statusDobj.getAppl_no(), TableList.VA_TRADE_CERTIFICATE);

            String sql = " INSERT INTO " + TableList.VHA_STATUS
                    + "       SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno,"
                    + "       action_cd, seat_cd, cntr_id, ? as status, ? as office_remark, public_remark,"
                    + "       file_movement_type, ? as emp_cd, op_dt, current_timestamp as moved_on,? "
                    + "       FROM " + TableList.VA_STATUS
                    + " WHERE appl_no=? and pur_cd=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, TableConstants.DISPOSE);
            ps.setString(i++, statusDobj.getOffice_remark());
            ps.setLong(i++, statusDobj.getEmp_cd());
            ps.setString(i++, Util.getClientIpAdress());
            ps.setString(i++, statusDobj.getAppl_no());
            ps.setInt(i++, statusDobj.getPur_cd());
            ps.executeUpdate();

            sql = "DELETE FROM " + TableList.VA_STATUS + " WHERE appl_no=? and pur_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, statusDobj.getAppl_no());
            ps.setInt(2, statusDobj.getPur_cd());
            ps.executeUpdate();
            tmgr.commit();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                tmgr.release();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }

        }
    }

    public Map getApplicationMap() throws VahanException {
        Map applicationMap = new HashMap();
        PreparedStatement psmt = null;
        RowSet rsApplNo = null;
        TransactionManager tmgr = null;

        String sql = " Select appl_no,valid_upto from vt_trade_certificate";
        try {
            tmgr = new TransactionManager("getApplicationMap");
            psmt = tmgr.prepareStatement(sql);

            rsApplNo = tmgr.fetchDetachedRowSet();
            while (rsApplNo.next()) {
                applicationMap.put(rsApplNo.getString("appl_no"), rsApplNo.getString("appl_no"));

            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                tmgr.release();
                psmt.close();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return applicationMap;
    }

    public Map getDealerMap(String stateCd, int offCd, boolean linkWithVTTradeCert) throws VahanException {
        Map dealerMap = new HashMap();
        PreparedStatement psmt = null;
        RowSet rs;
        TransactionManager tmgr = null;
        TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
        String sql = " Select vm_dealer.dealer_cd,vm_dealer.dealer_name,tm_userinfo.user_id, "
                + " COALESCE(vm_dealer.d_add1, ''::character varying)  || ',' || COALESCE(vm_dealer.d_add2, ''::character varying) as dealerAddress "
                + " from " + TableList.VM_DEALER_MAST + " vm_dealer "
                + " LEFT OUTER JOIN " + TableList.TM_USER_PERMISSIONS + " tm_userperm ON tm_userperm.dealer_cd = vm_dealer.dealer_cd "
                + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " tm_userinfo ON tm_userinfo.user_cd = tm_userperm.user_cd ";
        if (linkWithVTTradeCert) {
            sql += " LEFT OUTER JOIN " + TableList.VT_TRADE_CERTIFICATE + " vt_trade_cert ON vt_trade_cert.dealer_cd = vm_dealer.dealer_cd ";
        }
        sql += " where vm_dealer.state_cd = ?  and vm_dealer.off_cd = ? ";
        if (tmConfigurationDobj.getTmTradeCertConfigDobj().isShowOnlyAdminDealers()) {
            sql += "and tm_userinfo.user_catg = 'D' ";
        }

        try {
            tmgr = new TransactionManager("getDealerMap");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, stateCd);
            psmt.setInt(2, offCd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {

                if (!CommonUtils.isNullOrBlank(rs.getString("user_id"))) {
                    dealerMap.put(rs.getString("dealer_cd"), rs.getString("dealer_name") + " [ ID : " + rs.getString("user_id") + " ]");
                } else if (!CommonUtils.isNullOrBlank(rs.getString("dealerAddress"))) {
                    dealerMap.put(rs.getString("dealer_cd"), rs.getString("dealer_name") + " [ ADDRESS : " + rs.getString("dealerAddress") + " ]");
                } else {
                    dealerMap.put(rs.getString("dealer_cd"), rs.getString("dealer_name"));
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
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return dealerMap;
    }

    public static String fetchApplicantEmailId(String dealerCd, String stateCd, int offCd, String applicantType) throws VahanException {

        String emailId = null;
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;

        String sql = " Select email_id from ";
        if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_MANUFACTURER)) {
            sql += TableList.VM_MAKER_TC + " where maker_cd = ? and state_cd = ?";
        } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER)) {
            sql += TableList.VM_FINANCIER_TC + " where financer_cd = ? and state_cd = ?";
        } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_RETRO_FITTER)) {
            sql += TableList.VM_RETROFITTER_TC + " where retrofitter_cd = ? and state_cd = ?";
        } else {
            sql += TableList.VM_DEALER_MAST + " where dealer_cd = ? and state_cd = ? and off_cd = ? ";
        }

        try {
            tmgr = new TransactionManager("fetchApplicantEmailId");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, String.valueOf(dealerCd));
            psmt.setString(2, stateCd);
            if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)) {
                psmt.setInt(3, offCd);
            }
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (!CommonUtils.isNullOrBlank(rs.getString("email_id"))) {
                    emailId = rs.getString("email_id");
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
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return emailId;

    }

    public static Long fetchApplicantContactNo(String dealerCd, String stateCd, int offCd, String applicantType) throws VahanException {

        long contactNo = 0L;
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;

        String sql = " Select contact_no from ";
        if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_MANUFACTURER)) {
            sql += TableList.VM_MAKER_TC + " where maker_cd = ? and state_cd = ?";
        } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER)) {
            sql += TableList.VM_FINANCIER_TC + " where financer_cd = ? and state_cd = ?";
        } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_RETRO_FITTER)) {
            sql += TableList.VM_RETROFITTER_TC + " where retrofitter_cd = ? and state_cd = ?";
        } else {
            sql += TableList.VM_DEALER_MAST + " where dealer_cd = ? and state_cd = ? and off_cd = ? ";
        }

        try {
            tmgr = new TransactionManager("fetchApplicantContactNo");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, String.valueOf(dealerCd));
            psmt.setString(2, stateCd);
            if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)) {
                psmt.setInt(3, offCd);
            }
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (!CommonUtils.isNullOrBlank(rs.getLong("contact_no") + "")) {
                    contactNo = rs.getLong("contact_no");
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
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return contactNo;

    }

    public String getDealerCode(String userCode) throws VahanException {
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        String dealerCode = "";
        String sql = " Select dealer_cd from " + TableList.TM_USER_PERMISSIONS + " where user_cd = ? ";
        try {
            tmgr = new TransactionManager("getDealerCode");
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, Integer.valueOf(userCode));
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dealerCode = rs.getString("dealer_cd");
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
        return dealerCode;
    }

    public String getDealerName(String empCode) throws VahanException {
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        String sql = " Select user_name  from " + TableList.TM_USER_INFO + " where user_cd = ? ";
        try {
            tmgr = new TransactionManager("getDealerName");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, empCode);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                return rs.getString("user_name");
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
        return null;
    }

    public Map getVehCatgMapForAllVehClasses(String dealerCd, String purCd, String stateCd, List activeTCList) throws VahanException {
        Map vehCatgMap = new HashMap();
        PreparedStatement psmt = null;
        RowSet rs = null;
        RowSet rsVehCatg = null;
        TransactionManager tmgr = null;
        String sql = "";
        TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
        if (tmConfigurationDobj.getTmTradeCertConfigDobj().isCmvrVchCatgApplicable()) {
            sql = "select veh_catg_cmv as vch_catg from " + TableList.VM_TRADE_VCH_CATG_MAPPING;
        } else {
            sql = "Select vch_catg from vm_feemast_catg where pur_cd = " + Integer.valueOf(purCd) + " and state_cd ='" + stateCd + "'";
        }
        try {
            tmgr = new TransactionManager("getVehCatgMap");
            psmt = tmgr.prepareStatement(sql);
            rs = tmgr.fetchDetachedRowSet_No_release();
            String whereClause = "'";
            while (rs.next()) {
                whereClause += rs.getString("vch_catg").trim() + "','";
            }

            if (whereClause.contains(",")) {
                whereClause = whereClause.substring(0, whereClause.lastIndexOf(","));
            } else {
                return vehCatgMap;
            }
            if (tmConfigurationDobj.getTmTradeCertConfigDobj().isCmvrVchCatgApplicable()) {
                sql = "select cmv_catg_descr as catg_desc ,veh_catg_cmv as catg  from " + TableList.VM_TRADE_VCH_CATG_MAPPING;
            } else {
                sql = " Select catg,catg_desc from vm_vch_catg where catg in (" + whereClause + ")";
            }
            psmt = tmgr.prepareStatement(sql);
            rsVehCatg = tmgr.fetchDetachedRowSet_No_release();
            while (rsVehCatg.next()) {
                if (activeTCList.isEmpty()) {//// NEW Application Case
                    sql = " Select vch_catg from va_trade_certificate where dealer_cd = '" + dealerCd + "' and vch_catg = '" + rsVehCatg.getString("catg") + "'";
                    String sqlVt = " Select vch_catg from vt_trade_certificate where dealer_cd = '" + dealerCd + "' and vch_catg = '" + rsVehCatg.getString("catg") + "'";

                    psmt = tmgr.prepareStatement(sql);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    psmt = tmgr.prepareStatement(sqlVt);
                    RowSet rsVt = tmgr.fetchDetachedRowSet();
                    if (!rs.next() && !rsVt.next()) {
                        vehCatgMap.put(rsVehCatg.getString("catg"), rsVehCatg.getString("catg_desc"));
                    }
                } else { //Edit application case
                    sql = " Select vch_catg from va_trade_certificate where dealer_cd = '" + dealerCd + "' and vch_catg = '" + rsVehCatg.getString("catg") + "'";

                    psmt = tmgr.prepareStatement(sql);
                    rs = tmgr.fetchDetachedRowSet();
                    if (!rs.next()) {
                        for (Object dobjObj : activeTCList) {
                            IssueTradeCertDobj dobj = (IssueTradeCertDobj) dobjObj;
                            if (dobj.getVehCatgFor().equals(rsVehCatg.getString("catg")) && vehCatgMap.get(rsVehCatg.getString("catg")) == null) {
                                vehCatgMap.put(rsVehCatg.getString("catg"), rsVehCatg.getString("catg_desc"));
                            }
                        }
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

    public Map getFuelTypeMapForDealerAndStateAndVehCategories(String dealerCd, String stateCd, String vehCatg, boolean toEditActiveTC) throws VahanException {
        Map fuelTypeMap = new HashMap();
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("getFuelTypeMapForDealer");
            String sql = " Select code,descr "
                    + " from vm_fuel "
                    + " where ";
            if (!toEditActiveTC) {  ///// NEW CASE
                sql += " NOT ";
            }
            sql += " ( code in ( Select fuel_cd from va_trade_certificate where dealer_cd = ? and state_cd = ? and vch_catg = ? and LENGTH(TRIM(fuel_cd::char)) >0"
                    + "                       UNION "
                    + "                       Select fuel_cd from vt_trade_certificate where dealer_cd = ? and state_cd = ? and vch_catg = ? and LENGTH(TRIM(fuel_cd::char)) >0"
                    + "                     )"
                    + "           ) order by descr";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, dealerCd);
            psmt.setString(2, stateCd);
            psmt.setString(3, vehCatg);
            psmt.setString(4, dealerCd);
            psmt.setString(5, stateCd);
            psmt.setString(6, vehCatg);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                fuelTypeMap.put(rs.getInt("code") + "", rs.getString("descr"));
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
        return fuelTypeMap;
    }

    public String updateApplicationAddMoreSections(ApplicationTradeCertDobj applicationTradeCertDobj) throws VahanException {

        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        String applNo = null;
        String exceptionSegment = ": some reason ";
        String result = ": Application No:";
        try {
            int srNo = Integer.valueOf(applicationTradeCertDobj.getSrNo());
            tmgr = new TransactionManager("ApplicationTradeCertImpl.updateApplicationAddMoreSections()");
            if (srNo <= 1) {
                applNo = getApplicationNo(applicationTradeCertDobj.getDealerFor(), applicationTradeCertDobj.getOffCd(), applicationTradeCertDobj.getStateCd(), tmgr);
            } else {
                applNo = applicationTradeCertDobj.getApplNo();
            }
            String sql = "INSERT INTO va_trade_certificate(state_cd, off_cd, appl_no, dealer_cd, sr_no, vch_catg,no_of_vch, op_dt) VALUES (?, ?, ?, ?, ?, ?,?, CURRENT_TIMESTAMP)";

            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, applicationTradeCertDobj.getStateCd());
            pstmt.setInt(2, applicationTradeCertDobj.getOffCd());
            pstmt.setString(3, applNo);
            pstmt.setString(4, applicationTradeCertDobj.getDealerFor());
            pstmt.setInt(5, srNo);
            pstmt.setString(6, applicationTradeCertDobj.getVehCatgFor());
            pstmt.setInt(7, Integer.valueOf(applicationTradeCertDobj.getNoOfAllowedVehicles()));
            int i = pstmt.executeUpdate();

            if (i > 0) {
                tmgr.commit();
                result = "SUCCESS" + result + applNo;
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            result = "FAILURE" + exceptionSegment;
            throw new VahanException(TableConstants.SomthingWentWrong);

        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return result;
    }

    public String insertIntoVaTradeCertificate(TransactionManager tmgr, Collection applicationTradeCertificateDobjs) throws VahanException {

        PreparedStatement pstmt = null;
        String exceptionSegment = ": some reason ";
        String result = ": Application No:";
        String sql = "";
        TmConfigurationDobj tmConfigDobj = null;

        try {

            tmConfigDobj = Util.getTmConfiguration();

            String applNo = "";
            ApplicationTradeCertDobj applicationTradeCertDobj = null;

            for (Object keyObj : applicationTradeCertificateDobjs) {

                applNo = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());

                applicationTradeCertDobj = (ApplicationTradeCertDobj) keyObj;

                if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {   /// for GUJRAT FUEL-TYPE work
                    sql = "INSERT INTO va_trade_certificate(state_cd, off_cd, appl_no, dealer_cd, sr_no, vch_catg,no_of_vch,fuel_cd, op_dt, rto_side_appl,stock_transfer_req";

                    if (!CommonUtils.isNullOrBlank(applicationTradeCertDobj.getStatus())) {
                        sql += ", status";
                    }

                    sql += ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, true,?";

                    if (!CommonUtils.isNullOrBlank(applicationTradeCertDobj.getStatus())) {
                        sql += ", ?";
                    }

                } else {

                    sql = "INSERT INTO va_trade_certificate(state_cd, off_cd, appl_no, dealer_cd, sr_no, vch_catg, no_of_vch, op_dt, rto_side_appl,stock_transfer_req";

                    if (!CommonUtils.isNullOrBlank(applicationTradeCertDobj.getStatus())) {
                        sql += ", status";
                    }

                    sql += ") VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, true,?";

                    if (!CommonUtils.isNullOrBlank(applicationTradeCertDobj.getStatus())) {
                        sql += ", ?";
                    }

                }
                sql += ")";

                pstmt = tmgr.prepareStatement(sql);
                break;
            }

            int i = 1;
            for (Object keyObj : applicationTradeCertificateDobjs) {
                int j = 1;
                applicationTradeCertDobj = (ApplicationTradeCertDobj) keyObj;

                applicationTradeCertDobj.setApplNo(applNo);

                if (pstmt != null) {
                    pstmt.setString(j++, applicationTradeCertDobj.getStateCd());
                    pstmt.setInt(j++, applicationTradeCertDobj.getOffCd());
                    pstmt.setString(j++, applNo);
                    pstmt.setString(j++, applicationTradeCertDobj.getDealerFor());
                    pstmt.setInt(j++, i++);
                    pstmt.setString(j++, applicationTradeCertDobj.getVehCatgFor());
                    pstmt.setInt(j++, Integer.valueOf(applicationTradeCertDobj.getNoOfAllowedVehicles()));
                    if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed() && !CommonUtils.isNullOrBlank(applicationTradeCertDobj.getFuelTypeFor().trim())) {   /// for GUJRAT FUEL-TYPE work
                        pstmt.setInt(j++, Integer.valueOf(applicationTradeCertDobj.getFuelTypeFor()));
                    }
                    pstmt.setBoolean(j++, applicationTradeCertDobj.isStockTransferReq());
                    if (!CommonUtils.isNullOrBlank(applicationTradeCertDobj.getStatus())) {
                        pstmt.setString(j++, applicationTradeCertDobj.getStatus());
                    }
                    pstmt.addBatch();
                }
            }

            if (pstmt != null) {
                int[] recordsInserted = pstmt.executeBatch();

                if (recordsInserted.length == applicationTradeCertificateDobjs.size()) {
                    result = "SUCCESS" + result + applNo;
                }
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            result = "FAILURE" + exceptionSegment;
            throw new VahanException(TableConstants.SomthingWentWrong);

        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return result;
    }

    private String checkForApplicationNoExistsForDealer(TransactionManager tmgr, String dealer) throws VahanException {
        PreparedStatement psmt = null;
        RowSet rs = null;

        String sql = "Select appl_no from va_trade_certificate where dealer_cd = " + dealer;

        try {
            psmt = tmgr.prepareStatement(sql);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                return rs.getString("appl_no");
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return null;
    }

    public String getApplicationNo(String dealer, int offCd, String stateCd, TransactionManager tmgr) throws VahanException {

        String applNo = "AUTOGENERATE";

        try {
            applNo = checkForApplicationNoExistsForDealer(tmgr, dealer);
            if (applNo == null) {
                applNo = ServerUtil.getUniqueApplNo(tmgr, stateCd);
            }
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

        return applNo;
    }

    public void getAllSrNoForSelectedDealer(String dealerFor, List applicationSectionsList) throws VahanException {

        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;

        String sql = "Select *  from va_trade_certificate where dealer_cd = ?";

        try {
            tmgr = new TransactionManager("getAllSrNoForSelectedDealer");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, dealerFor);
            rs = tmgr.fetchDetachedRowSet();
            ApplicationTradeCertDobj applicationTradeCertDobj = null;
            while (rs.next()) {
                applicationTradeCertDobj = new ApplicationTradeCertDobj();
                applicationTradeCertDobj.setStateCd(rs.getString("state_cd"));
                applicationTradeCertDobj.setOffCd(rs.getInt("off_cd"));
                applicationTradeCertDobj.setSrNo(String.valueOf(rs.getInt("sr_no")));
                applicationTradeCertDobj.setVehCatgName(getVehCatgDesc(rs.getString("vch_catg")));
                applicationTradeCertDobj.setVehCatgFor(rs.getString("vch_catg"));
                applicationTradeCertDobj.setNoOfAllowedVehicles(String.valueOf(rs.getInt("no_of_vch")));
                applicationTradeCertDobj.setApplNo(String.valueOf(rs.getString("appl_no")));
                applicationTradeCertDobj.setDealerName(getDealerName(rs.getString("dealer_cd"), rs.getString("state_cd"), rs.getInt("off_cd"), rs.getString("applicant_type")));
                applicationTradeCertDobj.setDealerFor(rs.getString("dealer_cd"));
                applicationTradeCertDobj.setApplicantCategory(rs.getString("applicant_type"));
                applicationSectionsList.add(applicationTradeCertDobj);
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public void getAllSrNoForSelectedApplication(String applNo, Map applicationSectionsMap) throws VahanException {

        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;

        String sql = "Select * from va_trade_certificate where appl_no = ?";

        try {
            tmgr = new TransactionManager("getAllSrNoForSelectedApplication");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            rs = tmgr.fetchDetachedRowSet();

            ApplicationTradeCertDobj applicationTradeCertDobj = null;
            while (rs.next()) {
                applicationTradeCertDobj = new ApplicationTradeCertDobj();
                applicationTradeCertDobj.setStateCd(rs.getString("state_cd"));
                applicationTradeCertDobj.setOffCd(rs.getInt("off_cd"));
                applicationTradeCertDobj.setSrNo(String.valueOf(rs.getInt("sr_no")));
                applicationTradeCertDobj.setVehCatgName(getVehCatgDesc(rs.getString("vch_catg")));
                applicationTradeCertDobj.setVehCatgFor(rs.getString("vch_catg"));
                applicationTradeCertDobj.setNoOfAllowedVehicles(String.valueOf(rs.getInt("no_of_vch")));
                applicationTradeCertDobj.setApplNo(String.valueOf(rs.getString("appl_no")));
                applicationTradeCertDobj.setDealerName(getDealerName(rs.getString("dealer_cd"), rs.getString("state_cd"), rs.getInt("off_cd"), rs.getString("applicant_type")));
                applicationTradeCertDobj.setDealerFor(rs.getString("dealer_cd"));
                applicationTradeCertDobj.setFuelTypeFor(rs.getString("fuel_cd"));
                applicationTradeCertDobj.setFuelTypeName(getFuelTypeDesc(rs.getInt("fuel_cd")));
                applicationTradeCertDobj.setApplicantCategory(rs.getString("applicant_type"));
                if (!CommonUtils.isNullOrBlank(rs.getString("status"))) {
                    applicationTradeCertDobj.setStatus(rs.getString("status"));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("trade_cert_appl_type"))) {
                    applicationTradeCertDobj.setNewRenewalTradeCert(rs.getString("trade_cert_appl_type"));
                }
                if (!CommonUtils.isNullOrBlank(applicationTradeCertDobj.getNewRenewalTradeCert())
                        && TableConstants.TRADE_CERT_DUPLICATE_CONSTANT_VAL.equals(applicationTradeCertDobj.getNewRenewalTradeCert())) {
                    applicationTradeCertDobj.setSelectedDuplicateCertificates(Utility.convertStringToList(rs.getString("selected_duplicate_certificate")));
                }
                applicationTradeCertDobj.setRtoSideAppl(rs.getBoolean("rto_side_appl"));

                applicationSectionsMap.put(applicationTradeCertDobj.getSrNo(), applicationTradeCertDobj);
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

    }

    public static String getFuelTypeDesc(int fuelCd) throws VahanException {
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

    public void getAllOnlineApplicationsPending(String stateCd, int offCd, List pendingOnlineApplicationForVerfications, String status) throws VahanException {
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;

        String sql = "Select vm.applicant_name,va_tc.sr_no,va_tc.vch_catg,va_tc.no_of_vch,va_tc.appl_no,va_tc.dealer_cd,* from " + TableList.VA_TRADE_CERTIFICATE + " va_tc"
                + " LEFT JOIN " + TableList.VM_APPLICANT_MAST_APPL + " vm ON vm.applicant_cd = va_tc.dealer_cd "
                + " where status = ?  and state_cd = ? and off_cd = ? order by op_dt";

        try {
            tmgr = new TransactionManager("ApplicationTradeCertImpl:getAllOnlineApplicationsPending");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, status);  //// P: online application Pending to be received, R for online Received , V for Verified, A for Approve
            psmt.setString(2, stateCd);
            psmt.setInt(3, offCd);
            rs = tmgr.fetchDetachedRowSet();

            VerifyApproveTradeCertDobj verifyApproveTradeCertDobj = null;

            while (rs.next()) {
                verifyApproveTradeCertDobj = new VerifyApproveTradeCertDobj();
                verifyApproveTradeCertDobj.setStateCd(rs.getString("state_cd"));
                verifyApproveTradeCertDobj.setOffCd(rs.getInt("off_cd"));
                verifyApproveTradeCertDobj.setSrNo(String.valueOf(rs.getInt("sr_no")));
                verifyApproveTradeCertDobj.setVehCatgName(getVehCatgDesc(rs.getString("vch_catg")));
                verifyApproveTradeCertDobj.setVehCatgFor(rs.getString("vch_catg"));
                verifyApproveTradeCertDobj.setNoOfAllowedVehicles(String.valueOf(rs.getInt("no_of_vch")));
                verifyApproveTradeCertDobj.setApplNo(rs.getString("appl_no"));
                verifyApproveTradeCertDobj.setDealerName(rs.getString("applicant_name"));
                verifyApproveTradeCertDobj.setDealerFor(rs.getString("dealer_cd"));
                verifyApproveTradeCertDobj.setPurCd(String.valueOf(TableConstants.VM_TRANSACTION_TRADE_CERT_ONLINE_APPLICATION));
                verifyApproveTradeCertDobj.setStatus(rs.getString("status"));

                pendingOnlineApplicationForVerfications.add(verifyApproveTradeCertDobj);
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (psmt != null) {
                    psmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public void getAllSrNoForOnlineApplication(String applNo, Map applicationSectionsMap, boolean addressToBeFull) throws VahanException {

        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        String sql = "";
        int totalVchSold = 0;
        TmConfigurationDobj tmConfigDobj = null;
        try {

            tmConfigDobj = Util.getTmConfiguration();

            if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {   //// using_online_schema_tc work
                sql = "Select * from " + TableList.VA_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " where appl_no = ?";
            } else {
                sql = "Select * from " + TableList.VA_TRADE_CERTIFICATE + " where appl_no = ?";
            }
            tmgr = new TransactionManager("getAllSrNoForSelectedApplication");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            rs = tmgr.fetchDetachedRowSet();

            VerifyApproveTradeCertDobj verifyApproveTradeCertDobj = null;
            while (rs.next()) {
                verifyApproveTradeCertDobj = new VerifyApproveTradeCertDobj();
                verifyApproveTradeCertDobj.setStateCd(rs.getString("state_cd"));
                verifyApproveTradeCertDobj.setOffCd(rs.getInt("off_cd"));
                verifyApproveTradeCertDobj.setSrNo(String.valueOf(rs.getInt("sr_no")));
                if (verifyApproveTradeCertDobj.getStateCd().equals("OR")) {
                    verifyApproveTradeCertDobj.setVehCatgName(getVehClassDesc(rs.getString("vch_catg")));
                    IssueTradeCertDobj tcDobj = getVtTradeCertificateRecord(tmgr, rs.getString("dealer_cd"), rs.getString("vch_catg"));
                    if (tcDobj != null) {
                        verifyApproveTradeCertDobj.setTradeCertNo(tcDobj.getTradeCertNo());
                        verifyApproveTradeCertDobj.setValidUpto(tcDobj.getValidUpto());
                        verifyApproveTradeCertDobj.setValidUptoAsString(tcDobj.getValidUptoAsString());
                        int noOfVchAllowedLastYear = Integer.valueOf(tcDobj.getNoOfAllowedVehicles());
                        totalVchSold = rs.getInt("extra_vch_sold_last_yr") + noOfVchAllowedLastYear;
                    }
                } else {
                    verifyApproveTradeCertDobj.setVehCatgName(getVehCatgDesc(rs.getString("vch_catg")));
                }
                verifyApproveTradeCertDobj.setVehCatgFor(rs.getString("vch_catg"));
                verifyApproveTradeCertDobj.setNoOfAllowedVehicles(String.valueOf(rs.getInt("no_of_vch")));
                verifyApproveTradeCertDobj.setApplNo(rs.getString("appl_no"));
                verifyApproveTradeCertDobj.setDealerName(getDealerName(rs.getString("dealer_cd"), rs.getString("state_cd"), rs.getInt("off_cd"), rs.getString("applicant_type")));
                verifyApproveTradeCertDobj.setDealerFor(rs.getString("dealer_cd"));
                verifyApproveTradeCertDobj.setPurCd(String.valueOf(TableConstants.VM_TRANSACTION_TRADE_CERT_ONLINE_APPLICATION));
                verifyApproveTradeCertDobj.setNewRenewalTradeCert(rs.getString("trade_cert_appl_type"));
                if (verifyApproveTradeCertDobj.getStateCd().equals("OR")) {
                    verifyApproveTradeCertDobj.setExtraVehiclesSoldLastYr(String.valueOf(totalVchSold));
                }
                verifyApproveTradeCertDobj.setApplicantType(rs.getString("applicant_type"));
                verifyApproveTradeCertDobj.setStatus(rs.getString("status"));
                verifyApproveTradeCertDobj.setNewRenewalTradeCert(rs.getString("trade_cert_appl_type"));

                if (verifyApproveTradeCertDobj.getNewRenewalTradeCert().equalsIgnoreCase("R")
                        || verifyApproveTradeCertDobj.getNewRenewalTradeCert().equalsIgnoreCase("D")) {
                    setTCNoValidUptoFromOnlineSchemaTradeCertficateAppl(tmgr, verifyApproveTradeCertDobj);
                }
                verifyApproveTradeCertDobj.setTempApplNo(fetchTemporaryApplNoFromOnlineSchemaVhaTradeCertAppl(rs.getString("dealer_cd"), rs.getString("appl_no")));

                ///////// Fetching applicant details from onlineschema.vm_applicant_mast_appl
                verifyApproveTradeCertDobj.setDealerMasterDobj(new DealerMasterDobj());
                verifyApproveTradeCertDobj.getDealerMasterDobj().setDealerCode(rs.getString("dealer_cd"));
                if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {
                    fetchApplicantDetailsFromMasterForOR(tmgr, verifyApproveTradeCertDobj, addressToBeFull);
                } else {
                    fetchApplicantDetailsFromMaster(tmgr, verifyApproveTradeCertDobj);
                }

                ///////// Fetching applicant details from vm_dealer_mast
                //verifyApproveTradeCertDobj.setDealerMasterDobj(new DealerMasterDobj());
                //verifyApproveTradeCertDobj.getDealerMasterDobj().setDealerCode(rs.getString("dealer_cd"));
                //fetchApplicantDetailsFromMaster(tmgr, verifyApproveTradeCertDobj.getDealerMasterDobj());
                if (tmConfigDobj.getTmTradeCertConfigDobj().isInspectionDtlsReq()) {   //// for UP
                    fetchInspectionDetailsFromVaTradeCert(tmgr, verifyApproveTradeCertDobj);
                }
                applicationSectionsMap.put(verifyApproveTradeCertDobj.getSrNo(), verifyApproveTradeCertDobj);
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (psmt != null) {
                    psmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

    }

    public static void fetchApplicantDetailsFromMaster(TransactionManager tmgr, VerifyApproveTradeCertDobj verifyApproveTradeCertDobj) throws VahanException {

        PreparedStatement psmt = null;
        RowSet rs = null;
        StringBuilder fullAddress = new StringBuilder();
        DealerMasterDobj dealerMasterDobj = verifyApproveTradeCertDobj.getDealerMasterDobj();

        String sql = "Select * from " + TableList.VM_APPLICANT_MAST_APPL + " where applicant_cd = ?";

        try {
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, dealerMasterDobj.getDealerCode());
            rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                dealerMasterDobj.setDealerName(rs.getString("applicant_name"));
                dealerMasterDobj.setDealerCode(rs.getString("applicant_cd"));
                dealerMasterDobj.setStateCode(rs.getString("applicant_state_cd"));
                dealerMasterDobj.setStateName(ServerUtil.getStateNameByStateCode(rs.getString("applicant_state_cd")));
                dealerMasterDobj.setOffCode(rs.getInt("applicant_off_cd"));
                dealerMasterDobj.setOfficeName(ServerUtil.getOfficeName(Integer.parseInt(rs.getString("applicant_off_cd")), rs.getString("applicant_state_cd")));
                dealerMasterDobj.setDealerRegnNo(rs.getString("applicant_cd"));
                dealerMasterDobj.setDealerAdd1(rs.getString("applicant_address1"));
                fullAddress.append("");
                if (!CommonUtils.isNullOrBlank(rs.getString("applicant_address1"))) {
                    fullAddress.append(",").append(rs.getString("applicant_address1"));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("applicant_address2"))) {
                    fullAddress.append(",").append(rs.getString("applicant_address2"));
                }
                if (!CommonUtils.isNullOrBlank(dealerMasterDobj.getStateName())) {
                    fullAddress.append(",").append(dealerMasterDobj.getStateName());
                }
                if (rs.getInt("applicant_pincode") != 0) {
                    fullAddress.append(",-").append(rs.getInt("applicant_pincode"));
                }
                if (fullAddress.toString().startsWith(",")) {
                    dealerMasterDobj.setDealerAdd1(fullAddress.substring(1));
                } else {
                    dealerMasterDobj.setDealerAdd1(fullAddress.toString());
                }
                dealerMasterDobj.setDealerPincode(rs.getInt("applicant_pincode"));
                dealerMasterDobj.setDealerStateCode(rs.getString("applicant_state_cd"));
                dealerMasterDobj.setTin_NO(rs.getString("applicant_tin_no"));
                dealerMasterDobj.setContactNo(rs.getString("applicant_mobile_no"));
                dealerMasterDobj.setEmailId(rs.getString("applicant_email"));

                if (rs.getString("applicant_maker_list").toString().startsWith(",")) {
                    dealerMasterDobj.setMaker(rs.getString("applicant_maker_list").substring(1));
                } else {
                    dealerMasterDobj.setMaker(rs.getString("applicant_maker_list"));
                }
                StringBuilder vchClassesCommaSeperatedList = new StringBuilder();
                if (!CommonUtils.isNullOrBlank(rs.getString("applicant_veh_class_list"))) {
                    String[] vchClassArr = rs.getString("applicant_veh_class_list").split(",");
                    for (String vchClass : vchClassArr) {
                        vchClassesCommaSeperatedList.append(getVehCatgDesc(vchClass)).append(",");
                    }
                    dealerMasterDobj.setVchClass(vchClassesCommaSeperatedList.substring(0, vchClassesCommaSeperatedList.lastIndexOf(",")));
                }
                ////////////////maker desc

                StringBuilder makerCommaSeperatedList = new StringBuilder();
                if (!CommonUtils.isNullOrBlank(rs.getString("applicant_maker_list"))) {
                    String[] makerArr = rs.getString("applicant_maker_list").split(",");
                    for (String makercode : makerArr) {
                        if (!makercode.equals("")) {
                            makerCommaSeperatedList.append(getMakerdescr(makercode)).append(",");
                        }
                    }
                    dealerMasterDobj.setMaker(makerCommaSeperatedList.substring(0, makerCommaSeperatedList.lastIndexOf(",")));
                }

                if (rs.getString("applicant_relation") != null && !rs.getString("applicant_relation").equals("")) {
                    dealerMasterDobj.setApplicantRelation(rs.getString("applicant_relation"));
                    dealerMasterDobj.setIndividual(true);
                }
                if (rs.getString("applicant_category") != null && !rs.getString("applicant_category").equals("")) {
                    verifyApproveTradeCertDobj.setApplicantType(rs.getString("applicant_category"));
                    verifyApproveTradeCertDobj.setApplicantTypeDescription(getApplicantTypeDescriptionAsPerCode(rs.getString("applicant_category")));
                }

            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public static void fetchApplicantDetailsFromMasterForOR(TransactionManager tmgr, VerifyApproveTradeCertDobj verifyApproveTradeCertDobj, boolean addressToBeInFull) throws VahanException {

        PreparedStatement psmt = null;
        RowSet rs = null;
        StringBuilder fullAddress = new StringBuilder();
        DealerMasterDobj dealerMasterDobj = verifyApproveTradeCertDobj.getDealerMasterDobj();
        boolean recordFound = false;
        String sql = "Select * from " + TableList.VM_APPLICANT_MAST_APPL_ONLINE_SCHEMA_TC + " where applicant_cd = ?";

        try {
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, dealerMasterDobj.getDealerCode());
            rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                recordFound = true;
                dealerMasterDobj.setDealerName(rs.getString("applicant_name"));
                dealerMasterDobj.setDealerCode(rs.getString("applicant_cd"));
                dealerMasterDobj.setStateCode(rs.getString("applicant_state_cd"));
                dealerMasterDobj.setStateName(ServerUtil.getStateNameByStateCode(rs.getString("applicant_state_cd")));
                dealerMasterDobj.setOffCode(rs.getInt("applicant_off_cd"));
                dealerMasterDobj.setOfficeName(ServerUtil.getOfficeName(Integer.parseInt(rs.getString("applicant_off_cd")), rs.getString("applicant_state_cd")));
                dealerMasterDobj.setDealerRegnNo(rs.getString("applicant_cd"));
                dealerMasterDobj.setDealerAdd1(rs.getString("applicant_address1"));
                if (addressToBeInFull) {
                    fullAddress.append("");
                    if (!CommonUtils.isNullOrBlank(rs.getString("applicant_address1"))) {
                        fullAddress.append(",").append(rs.getString("applicant_address1"));
                    }
                    if (!CommonUtils.isNullOrBlank(rs.getString("applicant_address2"))) {
                        fullAddress.append(",").append(rs.getString("applicant_address2"));
                    }
                    if (!CommonUtils.isNullOrBlank(dealerMasterDobj.getStateName())) {
                        fullAddress.append(",").append(dealerMasterDobj.getStateName());
                    }
                    if (rs.getInt("applicant_pincode") != 0) {
                        fullAddress.append("-").append(rs.getInt("applicant_pincode"));
                    }
                    if (fullAddress.toString().startsWith(",")) {
                        dealerMasterDobj.setDealerAdd1(fullAddress.substring(1));
                    } else {
                        dealerMasterDobj.setDealerAdd1(fullAddress.toString());
                    }
                } else {
                    if (!CommonUtils.isNullOrBlank(rs.getString("applicant_address2"))) {
                        dealerMasterDobj.setDealerAdd2(rs.getString("applicant_address2"));
                    }
                    if (!CommonUtils.isNullOrBlank(rs.getInt("applicant_district") + "")) {
                        dealerMasterDobj.setDealerDistrict(rs.getInt("applicant_district"));
                    }
                }
                dealerMasterDobj.setDealerPincode(rs.getInt("applicant_pincode"));
                dealerMasterDobj.setDealerStateCode(rs.getString("applicant_state_cd"));
                dealerMasterDobj.setTin_NO(rs.getString("applicant_tin_no"));
                dealerMasterDobj.setContactNo(rs.getString("applicant_mobile_no"));
                dealerMasterDobj.setEmailId(rs.getString("applicant_email"));

                if (dealerMasterDobj.getStateCode().equals("OR")) {   /////////// ONLY FOR ODISHA .. since form design is different from Delhi online application
                    verifyApproveTradeCertDobj.getVehClassAffiliatedByApplicantListForEx().clear();
                    String[] affiliatedVehClassesCodeArr = rs.getString("applicant_veh_class_list").split(",");
                    for (String vehClassesCode : affiliatedVehClassesCodeArr) {
                        verifyApproveTradeCertDobj.getVehClassAffiliatedByApplicantListForEx().add(getVehClassDesc(vehClassesCode).toUpperCase());
                    }
                    verifyApproveTradeCertDobj.getSelectedApplicantManufacturerListForEx().clear();
                    String[] applicantSelectedManufactureCodeArr = rs.getString("applicant_maker_list").split(",");
                    for (String makerCode : applicantSelectedManufactureCodeArr) {
                        // added by chhavindra
                        if (makerCode != null && !makerCode.equals("")) {
                            String makerDescr = getMakerdescr(makerCode);
                            verifyApproveTradeCertDobj.getSelectedApplicantManufacturerListForEx().add(makerDescr);
                            verifyApproveTradeCertDobj.getSelectedManufacturerMapFromSession().put(makerDescr, makerCode);
                        }
                    }
                } else {                                            /////////// FOR DELHI .. as per form design for Delhi online application
                    if (rs.getString("applicant_maker_list").toString().startsWith(",")) {
                        dealerMasterDobj.setMaker(rs.getString("applicant_maker_list").substring(1));
                    } else {
                        dealerMasterDobj.setMaker(rs.getString("applicant_maker_list"));
                    }
                    StringBuilder vchClassesCommaSeperatedList = new StringBuilder();
                    String[] vchClassArr = rs.getString("applicant_veh_class_list").split(",");
                    for (String vchClass : vchClassArr) {
                        vchClassesCommaSeperatedList.append(getVehCatgDesc(vchClass)).append(",");
                    }
                    dealerMasterDobj.setVchClass(vchClassesCommaSeperatedList.substring(0, vchClassesCommaSeperatedList.lastIndexOf(",")));
                    ////////////////maker desc

                    StringBuilder makerCommaSeperatedList = new StringBuilder();
                    String[] makerArr = rs.getString("applicant_maker_list").split(",");
                    for (String makercode : makerArr) {
                        if (!makercode.equals("")) {
                            makerCommaSeperatedList.append(getMakerdescr(makercode)).append(",");
                        }
                    }
                    dealerMasterDobj.setMaker(makerCommaSeperatedList.substring(0, makerCommaSeperatedList.lastIndexOf(",")));
                }
                if (rs.getString("applicant_relation") != null && !rs.getString("applicant_relation").equals("")) {
                    dealerMasterDobj.setApplicantRelation(rs.getString("applicant_relation"));
                    dealerMasterDobj.setIndividual(true);
                }
                if (rs.getString("applicant_category") != null && !rs.getString("applicant_category").equals("")) {
                    verifyApproveTradeCertDobj.setApplicantType(rs.getString("applicant_category"));
                }
                if (rs.getString("applicant_landline_no") != null && !rs.getString("applicant_landline_no").equals("")) {
                    verifyApproveTradeCertDobj.setLandLineNo(rs.getString("applicant_landline_no"));
                }
                if (rs.getString("applicant_branch_name") != null && !rs.getString("applicant_branch_name").equals("")) {
                    verifyApproveTradeCertDobj.setBranchName(rs.getString("applicant_branch_name"));
                }
            }

            if (!recordFound && verifyApproveTradeCertDobj.getApplicantType().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)) {
                sql = "Select * from " + TableList.VM_DEALER_MAST + " where dealer_cd = ?";
                psmt = tmgr.prepareStatement(sql);
                psmt.setString(1, dealerMasterDobj.getDealerCode());
                rs = tmgr.fetchDetachedRowSet();

                while (rs.next()) {
                    recordFound = true;
                    dealerMasterDobj.setDealerName(rs.getString("dealer_name"));
                    dealerMasterDobj.setDealerCode(rs.getString("dealer_cd"));
                    dealerMasterDobj.setStateCode(rs.getString("state_cd"));
                    dealerMasterDobj.setStateName(ServerUtil.getStateNameByStateCode(rs.getString("state_cd")));
                    dealerMasterDobj.setOffCode(rs.getInt("off_cd"));
                    dealerMasterDobj.setOfficeName(ServerUtil.getOfficeName(Integer.parseInt(rs.getString("off_cd")), rs.getString("state_cd")));
                    dealerMasterDobj.setDealerRegnNo(rs.getString("dealer_cd"));
                    dealerMasterDobj.setDealerAdd1(rs.getString("d_add1"));
                    if (addressToBeInFull) {
                        fullAddress.append("");
                        if (!CommonUtils.isNullOrBlank(rs.getString("d_add1"))) {
                            fullAddress.append(",").append(rs.getString("d_add1"));
                        }
                        if (!CommonUtils.isNullOrBlank(rs.getString("d_add2"))) {
                            fullAddress.append(",").append(rs.getString("d_add2"));
                        }
                        if (!CommonUtils.isNullOrBlank(dealerMasterDobj.getStateName())) {
                            fullAddress.append(",").append(dealerMasterDobj.getStateName());
                        }
                        if (rs.getInt("d_pincode") != 0) {
                            fullAddress.append(",-").append(rs.getInt("d_pincode"));
                        }
                        if (fullAddress.toString().startsWith(",")) {
                            dealerMasterDobj.setDealerAdd1(fullAddress.substring(1));
                        } else {
                            dealerMasterDobj.setDealerAdd1(fullAddress.toString());
                        }
                    } else {
                        if (!CommonUtils.isNullOrBlank(rs.getString("d_add2"))) {
                            dealerMasterDobj.setDealerAdd2(rs.getString("d_add2"));
                        }
                        if (!CommonUtils.isNullOrBlank(rs.getString("d_district"))) {
                            dealerMasterDobj.setDealerDistrict(Integer.valueOf(rs.getString("d_district")));
                        }
                    }
                    dealerMasterDobj.setDealerPincode(rs.getInt("d_pincode"));
                    dealerMasterDobj.setDealerStateCode(rs.getString("state_cd"));
                    dealerMasterDobj.setTin_NO(rs.getString("tin_no"));
                    dealerMasterDobj.setContactNo(rs.getString("contact_no"));
                    dealerMasterDobj.setEmailId(rs.getString("email_id"));
                    if (dealerMasterDobj.getStateCode().equals("OR")) {   /////////// ONLY FOR ODISHA .. since form design is different from Delhi online application
                        verifyApproveTradeCertDobj.getVehClassAffiliatedByApplicantListForEx().clear();
                        verifyApproveTradeCertDobj.getVehClassAffiliatedByApplicantList().clear();
                        String[] affiliatedVehClassesCodeArr = rs.getString("vch_class").split(",");
                        for (String vehClassesCode : affiliatedVehClassesCodeArr) {
                            verifyApproveTradeCertDobj.getVehClassAffiliatedByApplicantList().add(vehClassesCode);
                            verifyApproveTradeCertDobj.getVehClassAffiliatedByApplicantListForEx().add(getVehClassDesc(vehClassesCode));
                        }
                        verifyApproveTradeCertDobj.getSelectedApplicantManufacturerListForEx().clear();
                        verifyApproveTradeCertDobj.getSelectedApplicantManufacturerList().clear();
                        String[] applicantSelectedManufactureCodeArr = rs.getString("maker").split(",");
                        for (String makerCode : applicantSelectedManufactureCodeArr) {
                            // added by chhavindra
                            if (makerCode != null && !makerCode.equals("")) {
                                String makerDescr = getMakerdescr(makerCode);
                                verifyApproveTradeCertDobj.getSelectedApplicantManufacturerList().add(makerCode);
                                verifyApproveTradeCertDobj.getSelectedApplicantManufacturerListForEx().add(makerDescr);
                                verifyApproveTradeCertDobj.getSelectedManufacturerMapFromSession().put(makerDescr, makerCode);
                            }
                        }
                    } else {                                            /////////// FOR DELHI .. as per form design for Delhi online application
                        if (rs.getString("maker").toString().startsWith(",")) {
                            dealerMasterDobj.setMaker(rs.getString("maker").substring(1));
                        } else {
                            dealerMasterDobj.setMaker(rs.getString("maker"));
                        }
                        StringBuilder vchClassesCommaSeperatedList = new StringBuilder();
                        String[] vchClassArr = rs.getString("vch_class").split(",");
                        for (String vchClass : vchClassArr) {
                            vchClassesCommaSeperatedList.append(getVehCatgDesc(vchClass)).append(",");
                        }
                        dealerMasterDobj.setVchClass(vchClassesCommaSeperatedList.substring(0, vchClassesCommaSeperatedList.lastIndexOf(",")));
                        ////////////////maker desc

                        StringBuilder makerCommaSeperatedList = new StringBuilder();
                        String[] makerArr = rs.getString("maker").split(",");
                        for (String makercode : makerArr) {
                            if (!makercode.equals("")) {
                                makerCommaSeperatedList.append(getMakerdescr(makercode)).append(",");
                            }
                        }
                        dealerMasterDobj.setMaker(makerCommaSeperatedList.substring(0, makerCommaSeperatedList.lastIndexOf(",")));
                    }
                    if (rs.getString("applicant_relation") != null && !rs.getString("applicant_relation").equals("")) {
                        dealerMasterDobj.setApplicantRelation(rs.getString("applicant_relation"));
                        dealerMasterDobj.setIndividual(true);
                    }

                    verifyApproveTradeCertDobj.setApplicantType(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER);
                }

            }

            if (!recordFound && verifyApproveTradeCertDobj.getApplicantType().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER)) {
                sql = "Select * from " + TableList.VM_FINANCIER_TC + " where financer_cd = ?";
                psmt = tmgr.prepareStatement(sql);
                psmt.setString(1, dealerMasterDobj.getDealerCode());
                rs = tmgr.fetchDetachedRowSet();

                while (rs.next()) {
                    recordFound = true;
                    dealerMasterDobj.setDealerName(rs.getString("financer_name"));
                    dealerMasterDobj.setDealerCode(rs.getString("financer_cd"));
                    dealerMasterDobj.setStateCode(rs.getString("state_cd"));
                    dealerMasterDobj.setStateName(ServerUtil.getStateNameByStateCode(rs.getString("state_cd")));
                    dealerMasterDobj.setOffCode(rs.getInt("off_cd"));
                    dealerMasterDobj.setOfficeName(ServerUtil.getOfficeName(Integer.parseInt(rs.getString("off_cd")), rs.getString("state_cd")));
                    dealerMasterDobj.setDealerRegnNo(rs.getString("financer_cd"));
                    dealerMasterDobj.setDealerAdd1(rs.getString("branch_add1"));
                    if (addressToBeInFull) {
                        fullAddress.append("");
                        if (!CommonUtils.isNullOrBlank(rs.getString("branch_add1"))) {
                            fullAddress.append(",").append(rs.getString("branch_add1"));
                        }
                        if (!CommonUtils.isNullOrBlank(rs.getString("branch_add2"))) {
                            fullAddress.append(",").append(rs.getString("branch_add2"));
                        }
                        if (!CommonUtils.isNullOrBlank(dealerMasterDobj.getStateName())) {
                            fullAddress.append(",").append(dealerMasterDobj.getStateName());
                        }
                        if (rs.getInt("branch_pincode") != 0) {
                            fullAddress.append(",-").append(rs.getInt("branch_pincode"));
                        }
                        if (fullAddress.toString().startsWith(",")) {
                            dealerMasterDobj.setDealerAdd1(fullAddress.substring(1));
                        } else {
                            dealerMasterDobj.setDealerAdd1(fullAddress.toString());
                        }
                    } else {
                        if (!CommonUtils.isNullOrBlank(rs.getString("branch_add2"))) {
                            dealerMasterDobj.setDealerAdd2(rs.getString("branch_add2"));
                        }
                        if (!CommonUtils.isNullOrBlank(rs.getInt("branch_district") + "")) {
                            dealerMasterDobj.setDealerDistrict(rs.getInt("branch_district"));
                        }
                    }
                    dealerMasterDobj.setDealerPincode(rs.getInt("branch_pincode"));
                    dealerMasterDobj.setDealerStateCode(rs.getString("state_cd"));

                    dealerMasterDobj.setContactNo(rs.getString("contact_no"));
                    dealerMasterDobj.setEmailId(rs.getString("email_id"));
                    if (dealerMasterDobj.getStateCode().equals("OR")) {   /////////// ONLY FOR ODISHA .. since form design is different from Delhi online application
                        verifyApproveTradeCertDobj.getVehClassAffiliatedByApplicantListForEx().clear();
                        String[] affiliatedVehClassesCodeArr = rs.getString("vch_class").split(",");
                        for (String vehClassesCode : affiliatedVehClassesCodeArr) {
                            verifyApproveTradeCertDobj.getVehClassAffiliatedByApplicantListForEx().add(getVehClassDesc(vehClassesCode));
                        }

                    } else {                                            /////////// FOR DELHI .. as per form design for Delhi online application

                        StringBuilder vchClassesCommaSeperatedList = new StringBuilder();
                        String[] vchClassArr = rs.getString("vch_class").split(",");
                        for (String vchClass : vchClassArr) {
                            vchClassesCommaSeperatedList.append(getVehCatgDesc(vchClass)).append(",");
                        }
                        dealerMasterDobj.setVchClass(vchClassesCommaSeperatedList.substring(0, vchClassesCommaSeperatedList.lastIndexOf(",")));

                    }

                    if (rs.getString("landline_no") != null && !rs.getString("landline_no").equals("")) {
                        verifyApproveTradeCertDobj.setLandLineNo(rs.getString("landline_no"));
                    }
                    if (rs.getString("branch_name") != null && !rs.getString("branch_name").equals("")) {
                        verifyApproveTradeCertDobj.setBranchName(rs.getString("branch_name"));
                    }
                    verifyApproveTradeCertDobj.setApplicantType(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER);
                }
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public static void setTCNoValidUptoFromOnlineSchemaTradeCertficateAppl(TransactionManager tmgr, VerifyApproveTradeCertDobj verifyApproveTradeCertDobj) throws VahanException {
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        PreparedStatement psmt = null;
        RowSet rs = null;
        String sql;

        try {
            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();
            if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {    //// using_online_schema_tc work
                sql = "Select cert_no,valid_upto from " + TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " where appl_no = ?";
            } else {
                sql = "Select cert_no,valid_upto from " + TableList.VHA_TRADE_CERTIFICATE_APPL + " where appl_no = ?";
            }
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, verifyApproveTradeCertDobj.getApplNo());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                verifyApproveTradeCertDobj.setTradeCertNo(rs.getString("cert_no").toUpperCase());
                verifyApproveTradeCertDobj.setValidUptoAsString(format.format(rs.getDate("valid_upto").getTime()));
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public void fetchApplicantDetailsFromVMDealerMast(TransactionManager tmgr, DealerMasterDobj dealerMasterDobj) throws VahanException {

        PreparedStatement psmt = null;
        RowSet rs = null;
        StringBuilder fullAddress = new StringBuilder();

        String sql = "Select * from vm_dealer_mast where dealer_cd = ?";

        try {
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, dealerMasterDobj.getDealerCode());
            rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                dealerMasterDobj.setDealerName(rs.getString("dealer_name"));
                dealerMasterDobj.setDealerCode(rs.getString("dealer_cd"));
                dealerMasterDobj.setStateCode(rs.getString("state_cd"));
                dealerMasterDobj.setStateName(ServerUtil.getStateNameByStateCode(rs.getString("applicant_state_cd")));
                dealerMasterDobj.setOffCode(rs.getInt("off_cd"));
                dealerMasterDobj.setOfficeName(ServerUtil.getOfficeName(Integer.parseInt(rs.getString("applicant_off_cd")), rs.getString("applicant_state_cd")));
                dealerMasterDobj.setDealerRegnNo(rs.getString("dealer_regn_no"));
                dealerMasterDobj.setDealerAdd1(rs.getString("d_add1"));
                fullAddress.append("");
                if (rs.getString("d_add1") != null && !rs.getString("d_add1").equals("")) {
                    fullAddress.append(",").append(rs.getString("d_add1"));
                }
                if (rs.getString("d_add2") != null && !rs.getString("d_add2").equals("") && !rs.getString("d_add2").equals(rs.getString("d_add1"))) {
                    fullAddress.append(",").append(rs.getString("d_add2"));
                }
                if (rs.getInt("d_district") != 0) {
                    fullAddress.append(",").append(rs.getInt("d_district"));
                }
                if (rs.getInt("d_pincode") != 0) {
                    fullAddress.append(",").append(rs.getInt("d_pincode"));
                }
                if (rs.getString("d_state") != null && !rs.getString("d_state").equals("")) {
                    fullAddress.append(",").append(dealerMasterDobj.getStateName());
                }
                if (fullAddress.toString().startsWith(",")) {
                    dealerMasterDobj.setDealerAdd1(fullAddress.substring(1));
                } else {
                    dealerMasterDobj.setDealerAdd1(fullAddress.toString());
                }
                dealerMasterDobj.setDealerAdd2(rs.getString("d_add2"));
                dealerMasterDobj.setDealerDistrict(rs.getInt("d_district"));
                dealerMasterDobj.setDealerPincode(rs.getInt("d_pincode"));
                dealerMasterDobj.setDealerStateCode(rs.getString("d_state"));
                dealerMasterDobj.setDealerValidUpto(rs.getDate("valid_upto"));
                dealerMasterDobj.setEnteredBy(rs.getString("entered_by"));
                dealerMasterDobj.setEnteredOn(rs.getString("entered_on"));
                dealerMasterDobj.setTin_NO(rs.getString("tin_no"));
                dealerMasterDobj.setContactNo(rs.getString("contact_no"));
                dealerMasterDobj.setEmailId(rs.getString("email_id"));
                if (rs.getString("maker").toString().startsWith(",")) {
                    dealerMasterDobj.setMaker(rs.getString("maker").substring(1));
                } else {
                    dealerMasterDobj.setMaker(rs.getString("maker"));
                }
                dealerMasterDobj.setVchClass(rs.getString("vch_class"));
                if (rs.getString("applicant_relation") != null && !rs.getString("applicant_relation").equals("")) {
                    dealerMasterDobj.setApplicantRelation(rs.getString("applicant_relation"));
                    dealerMasterDobj.setIndividual(true);
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    //////////////////////////////
    public String submitOnlineApplication(List<VerifyApproveTradeCertDobj> verifyApproveTradeCertDobjList, String status, TransactionManager tmgr, String applicationType) throws VahanException {
        PreparedStatement pstmt = null;
        PreparedStatement psSave = null;
        PreparedStatement psSaveIntoVtFee = null;
        PreparedStatement psRecordsInsertedInVh = null;
        PreparedStatement psRecordsUpdatedInVt = null;
        PreparedStatement psRecordsInsertedInVha = null;
        PreparedStatement psRecordsDeletedFromVa = null;
        String exceptionSegment = null;
        String saveReturn = ": Trade Certificate No:";
        String result = ":";
        TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();

        String tradeCertificateNo = null;
        boolean requireFirstTcNo = false;
        try {
            if ("NEW".equalsIgnoreCase(applicationType)) {
                tradeCertificateNo = getUniqueTcNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode(), verifyApproveTradeCertDobjList.get(0).getApplicantType());
                requireFirstTcNo = true;
            }
        } catch (VahanException ve) {
            throw ve;
        }

        try {
            String sql = "UPDATE " + TableList.VA_TRADE_CERTIFICATE + " SET status = ? ,op_dt = CURRENT_TIMESTAMP WHERE appl_no = ?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, status);  //// M: Modified By Applicant, R : Received , V : Verified , A : Approved , RX : Discarded By Receiver , VX : Discarded By Verifier , AX : Discarded By Approver, P:Pending , I :Issue 
            pstmt.setString(2, verifyApproveTradeCertDobjList.get(0).getApplNo());
            int recordsUpdated = pstmt.executeUpdate();

            if (recordsUpdated > 0) {
                saveReturn = "SUCCESS" + result + verifyApproveTradeCertDobjList.get(0).getApplNo();

                if (status.equalsIgnoreCase("A")) {    ////////////// 

                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    Set<String> setOfTradeCertNoForTcPrint = new HashSet<>();
                    boolean isRenew = false;
                    String applNo = "";
                    boolean alreadySaved = false;
                    String sqlVtTcSave = "INSERT INTO vt_trade_certificate(state_cd,off_cd,appl_no,dealer_cd,sr_no,vch_catg,no_of_vch,cert_no,valid_from,valid_upto,issue_dt,applicant_type,no_of_vch_used) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
                    String sqlVhTcSave = "INSERT INTO vh_trade_certificate(state_cd,off_cd,appl_no,dealer_cd,sr_no,vch_catg,no_of_vch,cert_no,valid_from,valid_upto,issue_dt,no_of_vch_used,applicant_type,moved_on,moved_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?,CURRENT_TIMESTAMP,?)";
                    String sqlVtTcUpdate = "UPDATE vt_trade_certificate SET no_of_vch = ?,valid_from=?,valid_upto=?,issue_dt=?,no_of_vch_used=?,appl_no=?,sr_no=? WHERE cert_no=? and dealer_cd=? and vch_catg =? and appl_no=?";
                    String sqlVhaTcSave = "INSERT INTO " + TableList.VHA_TRADE_CERTIFICATE + "(state_cd,off_cd,appl_no,dealer_cd,sr_no,vch_catg,no_of_vch,op_dt,moved_on,moved_by,status,trade_cert_appl_type";
                    if (tmConfigurationDobj.getTmTradeCertConfigDobj().isInspectionDtlsReq()) { //// For UP
                        sqlVhaTcSave += ",inspec_by,inspec_on,inspec_remark";
                    }
                    sqlVhaTcSave += ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?,  ?, ?";
                    if (tmConfigurationDobj.getTmTradeCertConfigDobj().isInspectionDtlsReq()) { ///// FOR UP
                        sqlVhaTcSave += ", ?, ?, ?";
                    }
                    sqlVhaTcSave += ")";
                    String sqlVaTcDelete = "DELETE FROM " + TableList.VA_TRADE_CERTIFICATE + " WHERE appl_no=?";

                    try {
                        psSave = tmgr.prepareStatement(sqlVtTcSave);
                        psRecordsInsertedInVh = tmgr.prepareStatement(sqlVhTcSave);
                        psRecordsUpdatedInVt = tmgr.prepareStatement(sqlVtTcUpdate);
                        psRecordsInsertedInVha = tmgr.prepareStatement(sqlVhaTcSave);
                        psRecordsDeletedFromVa = tmgr.prepareStatement(sqlVaTcDelete);

                        IssueTradeCertDobj dobjPrev = null;
                        int recordAddedInBatch = 0;
                        int srNoForUpdateInVt = 1;

                        for (Object dobjListObj : verifyApproveTradeCertDobjList) {
                            VerifyApproveTradeCertDobj dobj = (VerifyApproveTradeCertDobj) dobjListObj;

                            applNo = dobj.getApplNo();
                            dobjPrev = getVtTradeCertificateRecord(tmgr, dobj.getDealerFor(), dobj.getVehCatgFor());

                            if (dobjPrev != null
                                    && dobjPrev.getIssueDt().getTime() == dobj.getIssueDt().getTime()
                                    && dobjPrev.getDealerFor().trim().equals(dobj.getDealerFor().trim())
                                    && dobjPrev.getApplNo().trim().equals(dobj.getApplNo().trim())) {

                                alreadySaved = true;
                                saveReturn = "SUCCESS" + saveReturn + dobjPrev.getTradeCertNo();

                            }

                            if ((dobj.getNewRenewalTradeCert().equals("N") || dobj.getNewRenewalTradeCert().equals("R"))
                                    && Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_ONLINE_APPLICATION) {

                                if (dobjPrev == null) { ///NEW 

                                    if (!requireFirstTcNo && tmConfigurationDobj.isTcNoForEachVehCatg()) {
                                        tradeCertificateNo = getUniqueTcNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode(), verifyApproveTradeCertDobjList.get(0).getApplicantType());
                                    }
                                    requireFirstTcNo = false;
                                    saveReturn = insertIntoVtTradeCertificate(psSave, tradeCertificateNo, dobj);
                                    if ("SUCCESS".equals(saveReturn)) {
                                        recordAddedInBatch++;
                                    }
                                } else {   //// RENEW 

                                    tradeCertificateNo = dobjPrev.getTradeCertNo();

                                    isRenew = true;

                                    psRecordsInsertedInVh.setString(1, dobjPrev.getStateCd());
                                    psRecordsInsertedInVh.setInt(2, dobjPrev.getOffCd());
                                    psRecordsInsertedInVh.setString(3, dobjPrev.getApplNo());
                                    psRecordsInsertedInVh.setString(4, dobjPrev.getDealerFor());
                                    psRecordsInsertedInVh.setInt(5, Integer.valueOf(dobjPrev.getSrNo()));
                                    psRecordsInsertedInVh.setString(6, dobjPrev.getVehCatgFor());
                                    psRecordsInsertedInVh.setInt(7, Integer.valueOf(dobjPrev.getNoOfAllowedVehicles()));
                                    psRecordsInsertedInVh.setString(8, dobjPrev.getTradeCertNo());
                                    psRecordsInsertedInVh.setDate(9, new java.sql.Date(dobjPrev.getValidDt().getTime()));
                                    psRecordsInsertedInVh.setDate(10, new java.sql.Date(dobjPrev.getValidUpto().getTime()));
                                    psRecordsInsertedInVh.setDate(11, new java.sql.Date(dobjPrev.getIssueDt().getTime()));
                                    psRecordsInsertedInVh.setString(12, dobjPrev.getApplicantCategory());
                                    psRecordsInsertedInVh.setString(13, String.valueOf(dobj.getEmpCd()));
                                    psRecordsInsertedInVh.addBatch();

                                    int noOfVehBalance = Integer.valueOf(dobjPrev.getNoOfAllowedVehicles()) - Integer.valueOf(dobjPrev.getNoOfVehiclesUsed());
                                    psRecordsUpdatedInVt.setInt(1, Integer.valueOf(dobj.getNoOfAllowedVehicles()));

                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(dobj.getValidDt());
                                    Date validFromRenew = cal.getTime();
                                    psRecordsUpdatedInVt.setDate(2, new java.sql.Date(validFromRenew.getTime()));

                                    cal.setTime(dobjPrev.getValidUpto());
                                    cal.add(Calendar.DATE, 1);
                                    cal.add(Calendar.MONTH, -1);
                                    Date tradeCertificateExpireDate = cal.getTime();
                                    Calendar currentCalDate = Calendar.getInstance();
                                    Date currentDate = currentCalDate.getTime();

                                    if ((currentDate.after(tradeCertificateExpireDate) && dobjPrev.getNoOfVehiclesUsed().equals(dobjPrev.getNoOfAllowedVehicles()))
                                            || (currentDate.after(tradeCertificateExpireDate) && !dobjPrev.getNoOfVehiclesUsed().equals(dobjPrev.getNoOfAllowedVehicles()))) {
                                        psRecordsUpdatedInVt.setDate(3, new java.sql.Date(dobj.getValidUpto().getTime()));
                                    } else {
                                        psRecordsUpdatedInVt.setDate(3, new java.sql.Date(dobjPrev.getValidUpto().getTime()));
                                    }

                                    psRecordsUpdatedInVt.setDate(4, new java.sql.Date(dobj.getIssueDt().getTime()));
                                    psRecordsUpdatedInVt.setInt(5, 0);
                                    psRecordsUpdatedInVt.setString(6, dobj.getApplNo());
                                    psRecordsUpdatedInVt.setInt(7, srNoForUpdateInVt++);
                                    psRecordsUpdatedInVt.setString(8, dobjPrev.getTradeCertNo());
                                    psRecordsUpdatedInVt.setString(9, dobjPrev.getDealerFor());
                                    psRecordsUpdatedInVt.setString(10, dobjPrev.getVehCatgFor());
                                    psRecordsUpdatedInVt.setString(11, dobjPrev.getApplNo());
                                    psRecordsUpdatedInVt.addBatch();

                                }
                            }

                            if ((dobj.getNewRenewalTradeCert().equals("D") && dobjPrev != null)
                                    && Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_ONLINE_APPLICATION) {  //// DUPLICATE CASE

                                tradeCertificateNo = dobjPrev.getTradeCertNo();
                            }
                            int paramIndex = 1;
                            psRecordsInsertedInVha.setString(paramIndex++, dobj.getStateCd());
                            psRecordsInsertedInVha.setInt(paramIndex++, dobj.getOffCd());
                            psRecordsInsertedInVha.setString(paramIndex++, dobj.getApplNo());
                            psRecordsInsertedInVha.setString(paramIndex++, dobj.getDealerFor());
                            psRecordsInsertedInVha.setInt(paramIndex++, Integer.valueOf(dobj.getSrNo()));
                            psRecordsInsertedInVha.setString(paramIndex++, dobj.getVehCatgFor());
                            psRecordsInsertedInVha.setInt(paramIndex++, Integer.valueOf(dobj.getNoOfAllowedVehicles()));
                            psRecordsInsertedInVha.setDate(paramIndex++, new java.sql.Date(getOpDtFromVa(dobj.getApplNo()).getTime()));
                            psRecordsInsertedInVha.setString(paramIndex++, String.valueOf(dobj.getEmpCd()));
                            psRecordsInsertedInVha.setString(paramIndex++, status);
                            psRecordsInsertedInVha.setString(paramIndex++, dobj.getNewRenewalTradeCert());
                            if (tmConfigurationDobj.getTmTradeCertConfigDobj().isInspectionDtlsReq()) { /// For UP
                                psRecordsInsertedInVha.setInt(paramIndex++, Integer.valueOf(dobj.getInspectionBy()));
                                psRecordsInsertedInVha.setDate(paramIndex++, new java.sql.Date(dobj.getInspectionOn().getTime()));
                                psRecordsInsertedInVha.setString(paramIndex++, dobj.getInspectionRemark());
                            }
                            psRecordsInsertedInVha.addBatch();

                            psRecordsDeletedFromVa.setString(1, dobj.getApplNo());

                            setOfTradeCertNoForTcPrint.add(tradeCertificateNo);
                        }

                        if (!alreadySaved) {

                            if ((verifyApproveTradeCertDobjList.get(0).getNewRenewalTradeCert().equals("N") || verifyApproveTradeCertDobjList.get(0).getNewRenewalTradeCert().equals("R"))
                                    && Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_ONLINE_APPLICATION) {

                                if (isRenew) { /// RENEW
                                    int[] recordsInsertedInVh = psRecordsInsertedInVh.executeBatch();
                                    int[] recordsUpdatedInVt = null;
                                    try {
                                        recordsUpdatedInVt = psRecordsUpdatedInVt.executeBatch();
                                    } catch (BatchUpdateException bue) {
                                        LOGGER.error(bue.toString() + " " + bue.getStackTrace()[0]);
                                        throw bue;
                                    }
                                    if (recordsInsertedInVh.length == verifyApproveTradeCertDobjList.size() && recordsUpdatedInVt.length == verifyApproveTradeCertDobjList.size()) {  //// No Of record to be inserted and updated is 1
                                        saveReturn = recordUpdatedInVtFee(saveReturn, tmConfigurationDobj, tmgr, applNo, TableConstants.VM_TRANSACTION_TRADE_CERT_ONLINE_APPLICATION, verifyApproveTradeCertDobjList.get(0).getNewRenewalTradeCert());
                                    } else {
                                        saveReturn = "FAILURE";
                                    }
                                } else { //// NEW

                                    if (recordAddedInBatch == verifyApproveTradeCertDobjList.size()) {   //// No Of record to be Added is 1
                                        int[] recordsInserted = psSave.executeBatch();
                                        saveReturn = "SUCCESS : Application No: " + applNo;
                                    } else {
                                        saveReturn = "FAILURE";
                                    }

                                }
                            } else if (verifyApproveTradeCertDobjList.get(0).getNewRenewalTradeCert().equals("D") && Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_ONLINE_APPLICATION) {   /// DUPLICATE
                                saveReturn = recordUpdatedInVtFee(saveReturn, tmConfigurationDobj, tmgr, applNo, TableConstants.VM_TRANSACTION_TRADE_CERT_ONLINE_APPLICATION, verifyApproveTradeCertDobjList.get(0).getNewRenewalTradeCert());
                            }

                            if (saveReturn.contains("SUCCESS")) {
                                psRecordsInsertedInVha.executeBatch();
                                psRecordsDeletedFromVa.executeUpdate();
                            }

                            int vaTcPrintCount = 0;
                            for (String tradeCertNo : setOfTradeCertNoForTcPrint) {
                                deletePreviousApplFromVaTCPrint(tmgr, tradeCertNo);
                                boolean isRecordInserted = insertIntoVaTCPrintForOnlineApplication(tmgr, applNo, tradeCertNo, verifyApproveTradeCertDobjList.get(0).getStateCd(), verifyApproveTradeCertDobjList.get(0).getOffCd());

                                if (isRecordInserted) {
                                    vaTcPrintCount++;
                                }
                            }
                            if (vaTcPrintCount != setOfTradeCertNoForTcPrint.size()) {
                                saveReturn = "FAILURE";
                            }

                        } else {
                            if (isRenew || alreadySaved || verifyApproveTradeCertDobjList.get(0).getNewRenewalTradeCert().equals("D")) {
                                saveReturn = "SUCCESS" + saveReturn + dobjPrev.getTradeCertNo();
                            } else {
                                if (tmConfigurationDobj.isTcNoForEachVehCatg()) {
                                    saveReturn = "SUCCESS" + saveReturn + setOfTradeCertNoForTcPrint.size();
                                } else {
                                    saveReturn = "SUCCESS" + saveReturn + tradeCertificateNo;
                                }
                            }
                        }
                    } catch (SQLException ex) {
                        LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                        throw new VahanException(TableConstants.SomthingWentWrong);
                    } catch (Exception ex) {
                        LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                        throw new VahanException(TableConstants.SomthingWentWrong);
                    }

                    if (!saveReturn.contains(tradeCertificateNo) && (isRenew || alreadySaved || verifyApproveTradeCertDobjList.get(0).getNewRenewalTradeCert().equals("D"))
                            && !tmConfigurationDobj.isTcNoForEachVehCatg()) {
                        saveReturn += ":" + tradeCertificateNo;
                    } else {
                        saveReturn = "SUCCESS" + saveReturn + setOfTradeCertNoForTcPrint.size();
                    }

                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                }
            }
            if (saveReturn.contains("SUCCESS")) {
                //////// to be run only in online application is approved by approver  (status = A) AND IF ONLY FOR NEW APPLICANT

                if (status.equalsIgnoreCase("A") && isNewApplicant(verifyApproveTradeCertDobjList.get(0).getDealerFor())
                        && !tmConfigurationDobj.getTmTradeCertConfigDobj().isDealerMasterNotToBeUpdated()) {  /// update_dealer_regn_no_as_tc_no work for Chhattisgarh

                    boolean applicantAddedIntoVahanSystem;
                    if (verifyApproveTradeCertDobjList.get(0).getApplicantType().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)) { ///// for Dealer
                        applicantAddedIntoVahanSystem = insertIntoVMDealerMastFromAppl(tmgr, verifyApproveTradeCertDobjList.get(0));
                    } else if (verifyApproveTradeCertDobjList.get(0).getApplicantType().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER)) { ///// for Financier
                        applicantAddedIntoVahanSystem = insertIntoVMFinancerMastFromAppl(tmgr, verifyApproveTradeCertDobjList.get(0));
                    } else if (verifyApproveTradeCertDobjList.get(0).getApplicantType().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_MANUFACTURER)) { ///// for Manufacturer
                        applicantAddedIntoVahanSystem = insertIntoVMManufacturerMastFromAppl(tmgr, verifyApproveTradeCertDobjList.get(0));
                    } else if (verifyApproveTradeCertDobjList.get(0).getApplicantType().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_RETRO_FITTER)) { ///// for RetroFitter
                        applicantAddedIntoVahanSystem = insertIntoVMRetroFitterMastFromAppl(tmgr, verifyApproveTradeCertDobjList.get(0));
                    } else {  ////////// By default [Dealer] 
                        applicantAddedIntoVahanSystem = insertIntoVMDealerMastFromAppl(tmgr, verifyApproveTradeCertDobjList.get(0));
                    }
                    sendWelcomeSMSToApplicant(verifyApproveTradeCertDobjList.get(0).getDealerMasterDobj().getContactNo());
                    if (applicantAddedIntoVahanSystem) {
                        //To diplay recent trade certificate no. on home page.
                        if (verifyApproveTradeCertDobjList.get(0).getTradeCertNo() != null && !verifyApproveTradeCertDobjList.get(0).getTradeCertNo().isEmpty()) {
                            String message = "Trade Certificate No. " + verifyApproveTradeCertDobjList.get(0).getTradeCertNo() + " generated against Application No: " + verifyApproveTradeCertDobjList.get(0).getApplNo();
                            ServerUtil.insertUsersTransactionMessage(message, 1, tmgr);
                        }
                        //To diplay recent trade certificate no. on home page.
                        if (isGenerationOfUserIdApplicable(verifyApproveTradeCertDobjList)
                                && "N".equalsIgnoreCase(verifyApproveTradeCertDobjList.get(0).getNewRenewalTradeCert())) {
                            String[] loginIdArr = generateLoginIdAndInsertIntoTmUserInfo(tmgr, verifyApproveTradeCertDobjList.get(0));
                            if (loginIdArr != null
                                    && loginIdArr.length > 0
                                    && insertIntoTmUserPermissions(tmgr, verifyApproveTradeCertDobjList.get(0))
                                    && insertIntoTmOffEmpAction(tmgr, verifyApproveTradeCertDobjList.get(0))) {
                                sendLoginCredentialsToApplicant(loginIdArr, verifyApproveTradeCertDobjList.get(0).getDealerMasterDobj().getContactNo());
                                tmgr.commit();
                                saveReturn = "SUCCESS";
                            } else {
                                throw new VahanException("Error occurred while generating login ID");
                            }
                        } else {
                            sendWelcomeSMSToApplicant(verifyApproveTradeCertDobjList.get(0).getDealerMasterDobj().getContactNo());
                            tmgr.commit();
                            saveReturn = "SUCCESS";
                        }
                    }
                } else {   ////////////// all other cases............ (status = M,P,R,V,MG)   AND IF ONLY FOR EXISTING APPLICANT
                    tmgr.commit();
                    saveReturn = "SUCCESS";
                }
            }
        } catch (VahanException ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            saveReturn = "FAILURE";
            throw ve;
        } catch (SQLException sqlex) {
            LOGGER.error(sqlex.toString() + " " + sqlex.getStackTrace()[0]);
            saveReturn = "FAILURE";
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (psSave != null) {
                    psSave.close();
                }
                if (psSaveIntoVtFee != null) {
                    psSaveIntoVtFee.close();
                }
                if (psRecordsUpdatedInVt != null) {
                    psRecordsUpdatedInVt.close();
                }
                if (psRecordsInsertedInVh != null) {
                    psRecordsInsertedInVh.close();
                }
                if (psRecordsInsertedInVha != null) {
                    psRecordsInsertedInVha.close();
                }
                if (psRecordsDeletedFromVa != null) {
                    psRecordsDeletedFromVa.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return saveReturn;
    }

    private boolean isGenerationOfUserIdApplicable(List<VerifyApproveTradeCertDobj> verifyApproveTradeCertDobjList) {
        return (isApplicable(verifyApproveTradeCertDobjList.get(0).getStateCd())
                && ("LMV".equalsIgnoreCase(verifyApproveTradeCertDobjList.get(0).getVehCatgFor())
                || "2WN".equalsIgnoreCase(verifyApproveTradeCertDobjList.get(0).getVehCatgFor())));
    }

    public String submitOdishaTCApplication(List<VerifyApproveTradeCertDobj> verifyApproveTradeCertDobjList, String status, TransactionManager tmgr) throws VahanException {
        PreparedStatement pstmt = null;
        PreparedStatement psSave = null;
        PreparedStatement psSaveIntoVtFee = null;
        PreparedStatement psRecordsInsertedInVh = null;
        PreparedStatement psRecordsUpdatedInVt = null;
        PreparedStatement psRecordsInsertedInVha = null;
        PreparedStatement psRecordsDeletedFromVa = null;
        String exceptionSegment = null;
        String saveReturn = ": Trade Certificate No:";
        String result = ":";

        String tradeCertificateNo = null;
        boolean requireFirstTcNo = false;
        try {
            tradeCertificateNo = getUniqueTcNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode(), verifyApproveTradeCertDobjList.get(0).getApplicantType());
            requireFirstTcNo = true;
        } catch (VahanException ve) {
            throw ve;
        }

        try {
            String sql = "UPDATE " + TableList.VA_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " SET status = ? ,op_dt = CURRENT_TIMESTAMP WHERE appl_no = ?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setString(2, verifyApproveTradeCertDobjList.get(0).getApplNo());
            int recordsUpdated = pstmt.executeUpdate();

            if (recordsUpdated > 0) {
                saveReturn = "SUCCESS" + result + verifyApproveTradeCertDobjList.get(0).getApplNo();
                for (int i = 0; i < verifyApproveTradeCertDobjList.size(); i++) {
                    if (verifyApproveTradeCertDobjList.get(i).getNewRenewalTradeCert().equalsIgnoreCase("NEW")) {
                        verifyApproveTradeCertDobjList.get(i).setNewRenewalTradeCert("N");
                    } else if (verifyApproveTradeCertDobjList.get(i).getNewRenewalTradeCert().equalsIgnoreCase("RENEW")) {
                        verifyApproveTradeCertDobjList.get(i).setNewRenewalTradeCert("R");
                    } else if (verifyApproveTradeCertDobjList.get(i).getNewRenewalTradeCert().equalsIgnoreCase("DUPLICATE")) {
                        verifyApproveTradeCertDobjList.get(i).setNewRenewalTradeCert("D");
                    }
                }
                if (status.equalsIgnoreCase("A")) {    ////////////// 

                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    Set<String> setOfTradeCertNoForTcPrint = new HashSet<>();
                    boolean isRenew = false;
                    String applNo = "";
                    boolean alreadySaved = false;

                    String sqlVtTcSave = "INSERT INTO " + TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + "(state_cd,off_cd,appl_no,dealer_cd,sr_no,vch_catg,no_of_vch,cert_no,valid_from,valid_upto,issue_dt,no_of_vch_used,applicant_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0,?)";
                    String sqlVhTcSave = "INSERT INTO " + TableList.VH_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + "(state_cd,off_cd,appl_no,dealer_cd,sr_no,vch_catg,no_of_vch,cert_no,valid_from,valid_upto,issue_dt,no_of_vch_used,applicant_type,moved_on,moved_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?,CURRENT_TIMESTAMP,?)";
                    String sqlVtTcUpdate = "UPDATE " + TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " SET no_of_vch = ?,valid_from=?,valid_upto=?,issue_dt=?,no_of_vch_used=?,appl_no=?,sr_no=? WHERE cert_no=? and dealer_cd=? and vch_catg =? and appl_no=?";
                    String sqlVhaTcSave = "INSERT INTO " + TableList.VHA_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + "(state_cd,off_cd,appl_no,dealer_cd,sr_no,vch_catg,no_of_vch,op_dt,moved_on,moved_by,status,applicant_type,trade_cert_appl_type,extra_vch_sold_last_yr) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP,?,?,?,?,?)";
                    String sqlVaTcDelete = "DELETE FROM " + TableList.VA_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " WHERE appl_no=?";

                    TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
                    try {
                        psSave = tmgr.prepareStatement(sqlVtTcSave);
                        psRecordsInsertedInVh = tmgr.prepareStatement(sqlVhTcSave);
                        psRecordsUpdatedInVt = tmgr.prepareStatement(sqlVtTcUpdate);
                        psRecordsInsertedInVha = tmgr.prepareStatement(sqlVhaTcSave);
                        psRecordsDeletedFromVa = tmgr.prepareStatement(sqlVaTcDelete);

                        IssueTradeCertDobj dobjPrev = null;
                        int recordAddedInBatch = 0;
                        int srNoForUpdateInVt = 1;

                        for (Object dobjListObj : verifyApproveTradeCertDobjList) {
                            VerifyApproveTradeCertDobj dobj = (VerifyApproveTradeCertDobj) dobjListObj;

                            applNo = dobj.getApplNo();
                            dobjPrev = getVtTradeCertificateRecord(tmgr, dobj.getDealerFor(), dobj.getVehCatgFor());

                            if (dobjPrev != null
                                    && dobjPrev.getIssueDt().getTime() == dobj.getIssueDt().getTime()
                                    && dobjPrev.getDealerFor().trim().equals(dobj.getDealerFor().trim())
                                    && dobjPrev.getApplNo().trim().equals(dobj.getApplNo().trim())) {

                                alreadySaved = true;
                                saveReturn = "SUCCESS" + saveReturn + dobjPrev.getTradeCertNo();

                            }

                            if (dobj.getNewRenewalTradeCert().equals("N") || dobj.getNewRenewalTradeCert().equals("R")) {

                                if (dobjPrev == null) { ///NEW 

                                    if (requireFirstTcNo) {
                                        String[] tempTCnoArr = tradeCertificateNo.split("/");
                                        tradeCertificateNo = tempTCnoArr[0] + "/" + tempTCnoArr[1] + "/" + tempTCnoArr[2] + "/" + tempTCnoArr[3] + "/" + dobj.getApplicantType() + "/" + tempTCnoArr[4];
                                        requireFirstTcNo = false;
                                    } else if (!requireFirstTcNo && tmConfigurationDobj.isTcNoForEachVehCatg()) {
                                        tradeCertificateNo = getUniqueTcNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode(), verifyApproveTradeCertDobjList.get(0).getApplicantType());
                                        String[] tempTCnoArr = tradeCertificateNo.split("/");
                                        tradeCertificateNo = tempTCnoArr[0] + "/" + tempTCnoArr[1] + "/" + tempTCnoArr[2] + "/" + tempTCnoArr[3] + "/" + dobj.getApplicantType() + "/" + tempTCnoArr[4];
                                    }

                                    saveReturn = insertIntoVtTradeCertificate(psSave, tradeCertificateNo, dobj);
                                    if ("SUCCESS".equals(saveReturn)) {
                                        recordAddedInBatch++;
                                    }
                                } else {   //// RENEW 

                                    tradeCertificateNo = dobjPrev.getTradeCertNo();

                                    isRenew = true;

                                    psRecordsInsertedInVh.setString(1, dobjPrev.getStateCd());
                                    psRecordsInsertedInVh.setInt(2, dobjPrev.getOffCd());
                                    psRecordsInsertedInVh.setString(3, dobjPrev.getApplNo());
                                    psRecordsInsertedInVh.setString(4, dobjPrev.getDealerFor());
                                    psRecordsInsertedInVh.setInt(5, Integer.valueOf(dobjPrev.getSrNo()));
                                    psRecordsInsertedInVh.setString(6, dobjPrev.getVehCatgFor());
                                    psRecordsInsertedInVh.setInt(7, Integer.valueOf(dobjPrev.getNoOfAllowedVehicles()));
                                    psRecordsInsertedInVh.setString(8, dobjPrev.getTradeCertNo());
                                    psRecordsInsertedInVh.setDate(9, new java.sql.Date(dobjPrev.getValidDt().getTime()));
                                    psRecordsInsertedInVh.setDate(10, new java.sql.Date(dobjPrev.getValidUpto().getTime()));
                                    psRecordsInsertedInVh.setDate(11, new java.sql.Date(dobjPrev.getIssueDt().getTime()));
                                    psRecordsInsertedInVh.setString(12, dobjPrev.getApplicantCategory());
                                    psRecordsInsertedInVh.setString(13, String.valueOf(dobj.getEmpCd()));
                                    psRecordsInsertedInVh.addBatch();

                                    int noOfVehBalance = 0;
                                    if (Integer.valueOf(dobjPrev.getNoOfVehiclesUsed()) < 0) {
                                        noOfVehBalance = Integer.valueOf(dobj.getNoOfAllowedVehicles()) - Integer.valueOf(dobjPrev.getNoOfVehiclesUsed());
                                    } else {
                                        noOfVehBalance = Integer.valueOf(dobj.getNoOfAllowedVehicles());
                                    }
                                    psRecordsUpdatedInVt.setInt(1, noOfVehBalance);

                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(dobj.getValidDt());
                                    Date validFromRenew = cal.getTime();
                                    psRecordsUpdatedInVt.setDate(2, new java.sql.Date(validFromRenew.getTime()));

                                    cal.setTime(dobjPrev.getValidUpto());
                                    cal.add(Calendar.DATE, 1);
                                    cal.add(Calendar.MONTH, -1);
                                    Date tradeCertificateExpireDate = cal.getTime();
                                    Calendar currentCalDate = Calendar.getInstance();
                                    Date currentDate = currentCalDate.getTime();

                                    if ((currentDate.after(tradeCertificateExpireDate) && dobjPrev.getNoOfVehiclesUsed().equals(dobjPrev.getNoOfAllowedVehicles()))
                                            || (currentDate.after(tradeCertificateExpireDate) && !dobjPrev.getNoOfVehiclesUsed().equals(dobjPrev.getNoOfAllowedVehicles()))) {
                                        psRecordsUpdatedInVt.setDate(3, new java.sql.Date(dobj.getValidUpto().getTime()));
                                    } else {
                                        psRecordsUpdatedInVt.setDate(3, new java.sql.Date(dobjPrev.getValidUpto().getTime()));
                                    }

                                    psRecordsUpdatedInVt.setDate(4, new java.sql.Date(dobj.getIssueDt().getTime()));
                                    psRecordsUpdatedInVt.setInt(5, 0);
                                    psRecordsUpdatedInVt.setString(6, dobj.getApplNo());
                                    psRecordsUpdatedInVt.setInt(7, srNoForUpdateInVt++);
                                    psRecordsUpdatedInVt.setString(8, dobjPrev.getTradeCertNo());
                                    psRecordsUpdatedInVt.setString(9, dobjPrev.getDealerFor());
                                    psRecordsUpdatedInVt.setString(10, dobjPrev.getVehCatgFor());
                                    psRecordsUpdatedInVt.setString(11, dobjPrev.getApplNo());
                                    psRecordsUpdatedInVt.addBatch();

                                }
                            }

                            if (dobj.getNewRenewalTradeCert().equals("D") && dobjPrev != null) {  //// DUPLICATE CASE

                                tradeCertificateNo = dobjPrev.getTradeCertNo();
                            }

                            psRecordsInsertedInVha.setString(1, dobj.getStateCd());
                            psRecordsInsertedInVha.setInt(2, dobj.getOffCd());
                            psRecordsInsertedInVha.setString(3, dobj.getApplNo());
                            psRecordsInsertedInVha.setString(4, dobj.getDealerFor());
                            psRecordsInsertedInVha.setInt(5, Integer.valueOf(dobj.getSrNo()));
                            psRecordsInsertedInVha.setString(6, dobj.getVehCatgFor());
                            psRecordsInsertedInVha.setInt(7, Integer.valueOf(dobj.getNoOfAllowedVehicles()));
                            psRecordsInsertedInVha.setDate(8, new java.sql.Date(getOpDtFromVa(dobj.getApplNo()).getTime()));
                            psRecordsInsertedInVha.setString(9, String.valueOf(dobj.getEmpCd()));
                            psRecordsInsertedInVha.setString(10, status);
                            psRecordsInsertedInVha.setString(11, dobj.getApplicantType());
                            psRecordsInsertedInVha.setString(12, dobj.getNewRenewalTradeCert());
                            psRecordsInsertedInVha.setInt(13, Integer.valueOf(dobj.getExtraVehiclesSoldLastYr()) - getNoOfVchFromVt(tradeCertificateNo));
                            psRecordsInsertedInVha.addBatch();

                            psRecordsDeletedFromVa.setString(1, dobj.getApplNo());

                            setOfTradeCertNoForTcPrint.add(tradeCertificateNo);
                        }

                        if (!alreadySaved) {

                            if (verifyApproveTradeCertDobjList.get(0).getNewRenewalTradeCert().equals("N") || verifyApproveTradeCertDobjList.get(0).getNewRenewalTradeCert().equals("R")) {

                                if (isRenew) { /// RENEW
                                    int[] recordsInsertedInVh = psRecordsInsertedInVh.executeBatch();
                                    int[] recordsUpdatedInVt = null;
                                    try {
                                        recordsUpdatedInVt = psRecordsUpdatedInVt.executeBatch();
                                    } catch (BatchUpdateException bue) {
                                        LOGGER.error(bue.toString() + " " + bue.getStackTrace()[0]);
                                        throw bue;
                                    }
                                    if (recordsInsertedInVh.length == verifyApproveTradeCertDobjList.size() && recordsUpdatedInVt.length == verifyApproveTradeCertDobjList.size()) {  //// No Of record to be inserted and updated is 1
                                        saveReturn = recordUpdatedInVtFee(saveReturn, tmConfigurationDobj, tmgr, applNo, Integer.valueOf(verifyApproveTradeCertDobjList.get(0).getPurCd()), verifyApproveTradeCertDobjList.get(0).getNewRenewalTradeCert());
                                    } else {
                                        saveReturn = "FAILURE";
                                    }
                                } else { //// NEW

                                    if (recordAddedInBatch == verifyApproveTradeCertDobjList.size()) {   //// No Of record to be Added is 1
                                        int[] recordsInserted = psSave.executeBatch();
                                        saveReturn = "SUCCESS : Application No: " + applNo;
                                    } else {
                                        saveReturn = "FAILURE";
                                    }

                                }
                            } else if (verifyApproveTradeCertDobjList.get(0).getNewRenewalTradeCert().equals("D")) {   /// DUPLICATE
                                saveReturn = recordUpdatedInVtFee(saveReturn, tmConfigurationDobj, tmgr, applNo, Integer.valueOf(verifyApproveTradeCertDobjList.get(0).getPurCd()), verifyApproveTradeCertDobjList.get(0).getNewRenewalTradeCert());
                            }

                            if (saveReturn.contains("SUCCESS")) {
                                psRecordsInsertedInVha.executeBatch();
                                psRecordsDeletedFromVa.executeUpdate();
                            }

                            int vaTcPrintCount = 0;
                            for (String tradeCertNo : setOfTradeCertNoForTcPrint) {
                                deletePreviousApplFromVaTCPrint(tmgr, tradeCertNo);
                                boolean isRecordInserted = insertIntoVaTCPrintForOnlineApplication(tmgr, applNo, tradeCertNo, verifyApproveTradeCertDobjList.get(0).getStateCd(), verifyApproveTradeCertDobjList.get(0).getOffCd());

                                if (isRecordInserted) {
                                    vaTcPrintCount++;
                                }
                            }
                            if (vaTcPrintCount != setOfTradeCertNoForTcPrint.size()) {
                                saveReturn = "FAILURE";
                            }

                        } else {
                            if (isRenew || alreadySaved || verifyApproveTradeCertDobjList.get(0).getNewRenewalTradeCert().equals("D")) {
                                saveReturn = "SUCCESS" + saveReturn + dobjPrev.getTradeCertNo();
                            } else {
                                if (tmConfigurationDobj.isTcNoForEachVehCatg()) {
                                    saveReturn = "SUCCESS" + saveReturn + setOfTradeCertNoForTcPrint.size();
                                } else {
                                    saveReturn = "SUCCESS" + saveReturn + tradeCertificateNo;
                                }
                            }
                        }
                    } catch (SQLException ex) {
                        LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                        throw new VahanException(TableConstants.SomthingWentWrong);
                    } catch (Exception ex) {
                        LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                        throw new VahanException(TableConstants.SomthingWentWrong);
                    }

                    if (!saveReturn.contains(tradeCertificateNo) && (isRenew || alreadySaved || verifyApproveTradeCertDobjList.get(0).getNewRenewalTradeCert().equals("D"))
                            && !tmConfigurationDobj.isTcNoForEachVehCatg()) {
                        saveReturn += ":" + tradeCertificateNo;
                    } else {
                        saveReturn = "SUCCESS" + saveReturn + setOfTradeCertNoForTcPrint.size();
                    }

                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                }
            }
            if (saveReturn.contains("SUCCESS")) {
                //////// to be run only in online application is approved by approver  (status = A) AND IF ONLY FOR NEW APPLICANT
                if (status.equalsIgnoreCase("A") && isNewApplicant(verifyApproveTradeCertDobjList.get(0).getDealerFor(), verifyApproveTradeCertDobjList.get(0).getApplicantType())) {
                    boolean recordInserted = false;
                    if (verifyApproveTradeCertDobjList.get(0).getApplicantType().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)) {
                        recordInserted = insertIntoVMDealerMastFromAppl(tmgr, verifyApproveTradeCertDobjList.get(0));
                    } else {
                        recordInserted = insertIntoVMFinancerMastFromAppl(tmgr, verifyApproveTradeCertDobjList.get(0));
                    }
                    sendWelcomeSMSToApplicant(verifyApproveTradeCertDobjList.get(0).getDealerMasterDobj().getContactNo());
                }
                saveReturn = "SUCCESS";
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            exceptionSegment = e.toString() + " : " + e.getStackTrace()[0];
            saveReturn = "FAILURE " + saveReturn + " Unable to update status of online application due to [message{" + exceptionSegment + "}]";
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (psSave != null) {
                    psSave.close();
                }
                if (psSaveIntoVtFee != null) {
                    psSaveIntoVtFee.close();
                }
                if (psRecordsUpdatedInVt != null) {
                    psRecordsUpdatedInVt.close();
                }
                if (psRecordsInsertedInVh != null) {
                    psRecordsInsertedInVh.close();
                }
                if (psRecordsInsertedInVha != null) {
                    psRecordsInsertedInVha.close();
                }
                if (psRecordsDeletedFromVa != null) {
                    psRecordsDeletedFromVa.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return saveReturn;
    }

    public static boolean sendWelcomeSMSToApplicant(String contactNumber) throws VahanException {
        boolean isSMSSent = false;
        try {
            String smsString = "[Apply For Trade Certificate]: Congratulations, Your online application for trade certificate has been approved. ";
            ServerUtil.sendSMS(contactNumber, smsString);
            isSMSSent = true;
        } catch (Exception ve) {
            LOGGER.error("Application approved but SMS is not sent for contact no " + contactNumber);
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

        return isSMSSent;
    }

    private String recordUpdatedInVtFee(String saveReturn, TmConfigurationDobj tmConfigurationDobj, TransactionManager tmgr, String applNo, int purCd, String tradeCertApplType) throws SQLException {

        PreparedStatement ps = null;
        try {
            if (!tmConfigurationDobj.isTcNoForEachVehCatg()) {
                String sqlVtFeeUpdate = "Update vt_fee set flag = ? where regn_no = ? and pur_cd = ? ";
                ps = tmgr.prepareStatement(sqlVtFeeUpdate);
                ps.setString(1, tradeCertApplType);
                ps.setString(2, applNo);
                ps.setInt(3, purCd);
                ps.executeUpdate();
            }
            saveReturn = "SUCCESS : Application No: " + applNo;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return saveReturn;
    }

    private boolean isNewApplicant(String dealerCd, String applicantType) throws VahanException {
        TransactionManager tmg = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String query = null;
        boolean dealerNotFound = true;
        if (applicantType.equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)) {
            query = "select dealer_cd from vahan4.vm_dealer_mast where dealer_cd = ?";
        } else {
            query = "select financer_cd from " + TableList.VM_FINANCIER_TC + " where financer_cd = ?";
        }
        try {
            tmg = new TransactionManager("isNewApplicant");
            ps = tmg.prepareStatement(query);
            ps.setString(1, dealerCd);
            rs = tmg.fetchDetachedRowSet();
            while (rs.next()) {
                dealerNotFound = false;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmg != null) {
                try {
                    tmg.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
        return dealerNotFound;
    }

    //////////////////////////////
    public String updateAuditTrailFieldForOnlineApplication(TransactionManager tmgr, VerifyApproveTradeCertDobj verifyApproveTradeCertDobj, String field, int formId) throws VahanException {

        PreparedStatement pstmt = null;
        String exceptionSegment = null;
        String result = ":";
        boolean fromSubmitOnlineApplication = false;
        String fieldContent = null;
        boolean revertToApplicant = false;
        try {
            if (tmgr == null) {
                tmgr = new TransactionManager("ApplicationTradeCertImpl.updateAuditTrailFieldForOnlineApplication()");
            } else {
                fromSubmitOnlineApplication = true;
            }

            switch (field) {
                case "receiver_deficiency_mail_content":
                    fieldContent = "[Mail Content] " + verifyApproveTradeCertDobj.getReceiverDeficiencyMailContent();
                    break;
                case "receiver_backward_remarks":
                    revertToApplicant = true;
                    fieldContent = "[Revert Back To Applicant Remarks] " + verifyApproveTradeCertDobj.getReceiverBackwardRemarks();
                    break;
                case "receiver_forward_remarks":
                    fieldContent = "[Send Forward Remarks] " + verifyApproveTradeCertDobj.getReceiverForwardRemarks();
                    break;
                case "verifier_deficiency_mail_content":
                    fieldContent = "[Mail Content] " + verifyApproveTradeCertDobj.getVerifierDeficiencyMailContent();
                    break;
                case "verifier_backward_remarks":
                    fieldContent = "[Revert Backward Remarks] " + verifyApproveTradeCertDobj.getVerifierBackwardRemarks();
                    break;
                case "verifier_forward_remarks":
                    fieldContent = "[Send Forward Remarks] " + verifyApproveTradeCertDobj.getVerifierForwardRemarks();
                    break;
                case "approver_deficiency_mail_content":
                    fieldContent = "[Mail Content] " + verifyApproveTradeCertDobj.getApproverDeficiencyMailContent();
                    break;
                case "approver_backward_remarks":
                    fieldContent = "[Revert Backward Remarks] " + verifyApproveTradeCertDobj.getApproverBackwardRemarks();
                    break;
                case "approver_forward_remarks":
                    fieldContent = "[Send Forward Remarks] " + verifyApproveTradeCertDobj.getApproverForwardRemarks();
                    break;
            }

            fieldContent = "[" + new java.text.SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new java.util.Date()) + "] " + fieldContent;

            verifyApproveTradeCertDobj.setIterationCounter(selectLatestIterationCounterFromAuditTrailForOnlineApplication(tmgr, verifyApproveTradeCertDobj));
            fillAuditTrailForOnlineApplication(tmgr, verifyApproveTradeCertDobj, formId);

            switch (field) {
                case "receiver_deficiency_mail_content": {
//                    String mail = (verifyApproveTradeCertDobj.getReceiverDeficiencyMailContent()
//                            + " #NEW MAIL:[" + fieldContent + "]");
//                    if (mail.length() <= 500 && verifyApproveTradeCertDobj.getReceiverDeficiencyMailContent()!=null) {
//                        verifyApproveTradeCertDobj.setReceiverDeficiencyMailContent(mail);
//                    } else {
//                        verifyApproveTradeCertDobj.setReceiverDeficiencyMailContent("#NEW MAIL:[" + fieldContent + "]");
//                    }
                    verifyApproveTradeCertDobj.setReceiverDeficiencyMailContent(fieldContent);
                    break;
                }
                case "receiver_backward_remarks": {
//                    String backwardRemarks = (verifyApproveTradeCertDobj.getReceiverBackwardRemarks()
//                            + " #NEW REMARK:[" + fieldContent + "]");
//                    if (backwardRemarks.length() <= 500) {
//                        verifyApproveTradeCertDobj.setReceiverBackwardRemarks(backwardRemarks);
//                    } else {
//                        verifyApproveTradeCertDobj.setReceiverBackwardRemarks("#NEW REMARK:[" + fieldContent + "]");
//                    }
                    verifyApproveTradeCertDobj.setReceiverBackwardRemarks(fieldContent);
                    break;
                }
                case "receiver_forward_remarks": {
//                    String forwardRemarks = (verifyApproveTradeCertDobj.getReceiverForwardRemarks()
//                            + " #NEW REMARK:[" + fieldContent + "]");
//                    if (forwardRemarks.length() <= 500) {
//                        verifyApproveTradeCertDobj.setReceiverForwardRemarks(forwardRemarks);
//                    } else {
//                        verifyApproveTradeCertDobj.setReceiverForwardRemarks("#NEW REMARK:[" + fieldContent + "]");
//                    }
                    verifyApproveTradeCertDobj.setReceiverForwardRemarks(fieldContent);
                    break;
                }
                case "verifier_deficiency_mail_content": {
//                    String mail = (verifyApproveTradeCertDobj.getVerifierDeficiencyMailContent()
//                            + " #NEW MAIL:[" + fieldContent + "]");
//                    if (mail.length() <= 500) {
//                        verifyApproveTradeCertDobj.setVerifierDeficiencyMailContent(mail);
//                    } else {
//                        verifyApproveTradeCertDobj.setVerifierDeficiencyMailContent("#NEW MAIL:[" + fieldContent + "]");
//                    }
                    verifyApproveTradeCertDobj.setVerifierDeficiencyMailContent(fieldContent);
                    break;
                }
                case "verifier_backward_remarks": {
//                    String backwardRemarks = (verifyApproveTradeCertDobj.getVerifierBackwardRemarks()
//                            + " #NEW REMARK:[" + fieldContent + "]");
//                    if (backwardRemarks.length() <= 500) {
//                        verifyApproveTradeCertDobj.setVerifierBackwardRemarks(backwardRemarks);
//                    } else {
//                        verifyApproveTradeCertDobj.setVerifierBackwardRemarks("#NEW REMARK:[" + fieldContent + "]");
//                    }
                    verifyApproveTradeCertDobj.setVerifierBackwardRemarks(fieldContent);
                    break;
                }
                case "verifier_forward_remarks": {
//                    forwardRemarks = (verifyApproveTradeCertDobj.getVerifierForwardRemarks()
//                            + " #NEW REMARK:[" + fieldContent + "]");
//                    if (forwardRemarks.length() <= 500) {
//                        verifyApproveTradeCertDobj.setVerifierForwardRemarks(forwardRemarks);
//                    } else {
//                        verifyApproveTradeCertDobj.setVerifierForwardRemarks("#NEW REMARK:[" + fieldContent + "]");
//                    }
                    verifyApproveTradeCertDobj.setVerifierForwardRemarks(fieldContent);
                    break;
                }
                case "approver_deficiency_mail_content": {
//                    String mail = (verifyApproveTradeCertDobj.getApproverDeficiencyMailContent()
//                            + " #NEW MAIL:[" + fieldContent + "]");
//                    if (mail.length() <= 500) {
//                        verifyApproveTradeCertDobj.setApproverDeficiencyMailContent(mail);
//                    } else {
//                        verifyApproveTradeCertDobj.setApproverDeficiencyMailContent("#NEW MAIL:[" + fieldContent + "]");
//                    }
                    verifyApproveTradeCertDobj.setApproverDeficiencyMailContent(fieldContent);
                    break;
                }
                case "approver_backward_remarks": {
//                    String backwardRemarks = (verifyApproveTradeCertDobj.getApproverBackwardRemarks()
//                            + " #NEW REMARK:[" + fieldContent + "]");
//                    if (backwardRemarks.length() <= 500) {
//                        verifyApproveTradeCertDobj.setApproverBackwardRemarks(backwardRemarks);
//                    } else {
//                        verifyApproveTradeCertDobj.setApproverBackwardRemarks("#NEW REMARK:[" + fieldContent + "]");
//                    }
                    verifyApproveTradeCertDobj.setApproverBackwardRemarks(fieldContent);
                    break;
                }
                case "approver_forward_remarks": {
//                    forwardRemarks = (verifyApproveTradeCertDobj.getApproverForwardRemarks()
//                            + " #NEW REMARK:[" + fieldContent + "]");
//                    if (forwardRemarks.length() <= 500) {
//                        verifyApproveTradeCertDobj.setApproverForwardRemarks(forwardRemarks);
//                    } else {
//                        verifyApproveTradeCertDobj.setApproverForwardRemarks("#NEW REMARK:[" + fieldContent + "]");
//                    }
                    verifyApproveTradeCertDobj.setApproverForwardRemarks(fieldContent);
                    break;
                }
            }

            int recordIntoVhaInserted = 0;
            if (!fromSubmitOnlineApplication) {
                if (formId == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE && revertToApplicant) {
                    recordIntoVhaInserted = insertIntoVhaTradeCertAuditTrail(tmgr, verifyApproveTradeCertDobj, "P");
                }
            }
            int recordsUpdated = updateIntoVaTradeCertificateAuditTrail(tmgr, verifyApproveTradeCertDobj, formId);
            if (recordsUpdated > 0) {
                if (!fromSubmitOnlineApplication) {
                    if (formId == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE && revertToApplicant) {
                        int recordIntoVaAuditTrailUpdated = updateRevertStatusVaTradeCertAuditTrail(tmgr, verifyApproveTradeCertDobj, "M");
                        int statusInVaTradeCertificateUpdated = updateVaTradeCertificateStatus(tmgr, verifyApproveTradeCertDobj, "P");
                        if (recordIntoVaAuditTrailUpdated > 0 && statusInVaTradeCertificateUpdated > 0 && recordIntoVhaInserted > 0) {
                            tmgr.commit();
                        }
                    } else {
                        tmgr.commit();
                    }
                }
                result = "SUCCESS : Updated [" + field + "] in audit trail of trade certificate module.";
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            exceptionSegment = e.toString() + " : " + e.getStackTrace()[0];
            result = "FAILURE " + exceptionSegment;
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null && !fromSubmitOnlineApplication) {
                    tmgr.release();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return result;
    }

    //////////////////////////////
    public String updateRevertStatusInAuditTrailOnRevertToApplicant(String applNo, String currentTimestamp, String status, VerifyApproveTradeCertDobj dobj) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        String result = ":";
        try {
            tmgr = new TransactionManager("updateRevertStatusInAuditTrailOnRevertToApplicant");

            int i = insertIntoVhaTradeCertAuditTrail(tmgr, dobj, "P");
            if (i > 0) {
                String sql = "UPDATE " + TableList.VA_TRADE_CERTIFICATE_AUDIT_TRAIL
                        + " SET current_status = ?, \n "
                        + " verifier_hard_copy_received_on = ?, verifier = ?, verifier_backward_remarks = ?\n"
                        + " WHERE state_cd = ? and appl_no = ? and sr_no = ?";
                pstmt = tmgr.prepareStatement(sql);
                int idx = 1;
                pstmt.setString(idx++, status);
                pstmt.setString(idx++, dobj.getVerifierHardCopyReceivedOn());
                pstmt.setString(idx++, dobj.getVerifier());
                pstmt.setString(idx++, dobj.getVerifierBackwardRemarks());
                pstmt.setString(idx++, Util.getUserStateCode());
                pstmt.setString(idx++, applNo);
                pstmt.setInt(idx++, 1);
                i = pstmt.executeUpdate();
                if (i > 0) {
                    sql = "UPDATE " + TableList.VA_TRADE_CERTIFICATE + " SET status = ? , op_dt = ?  WHERE appl_no = ?";
                    pstmt = tmgr.prepareStatement(sql);
                    pstmt.setString(1, "P");  /// P:Pending 
                    pstmt.setTimestamp(2, new java.sql.Timestamp(new java.util.Date().getTime()));
                    pstmt.setString(3, applNo);
                    i = pstmt.executeUpdate();
                    if (i > 0) {
                        tmgr.commit();
                        result = "SUCCESS";
                    } else {
                        throw new VahanException("Status not updated in trade certificate application table.");
                    }
                } else {
                    throw new VahanException("Status not updated in trade certificate audit table.");
                }
            } else {
                throw new VahanException("Previous audit record not moved in trade certificate audit history table.");
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            result = "FAILURE";
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return result;
    }

    public static int checkStatusOfAllDocuments(String applNo, String flag) throws VahanException {
        //   PreparedStatement ps;
        TransactionManager tmgr = null;
        //   RowSet rs;
        int noOfRecords = 0;
        //   String query = "";
        try {
            //  tmgr = new TransactionManager("checkStatusOfAllDocuments");

            if (flag.equals("R")) {
                // query = " select count from dblink('" + getDMSDblink() + "','Select count(doc_recieved) as count from vahandocs.vt_document where appl_no = ''" + applNo + "'' and doc_recieved = true') as  (count integer) ";
                /////// USING DMS SERVICE //////////////
                List vtDocumentModelList = DmsDocCheckUtils.getList(VerifyApproveTradeCertBean.dmsTradeCertUploadedDocsUrlWithinServer, applNo);
                if (!vtDocumentModelList.isEmpty()) {
                    int count = 0;
                    for (Object vtDocumentModelListObj : vtDocumentModelList) {
                        VTDocumentModel vtDocumentModelListItem = (VTDocumentModel) vtDocumentModelListObj;
                        if (vtDocumentModelListItem.isDoc_recieved()) {
                            count++;
                        }
                    }
                    noOfRecords = count;
                }
                ////////////////////////////////////////
            } else if (flag.equals("V")) {
                // query = " select count from dblink('" + getDMSDblink() + "','Select count(doc_verified) as count from vahandocs.vt_document where appl_no = ''" + applNo + "'' and doc_verified = true') as  (count integer) ";
                /////// USING DMS SERVICE //////////////
                List vtDocumentModelList = DmsDocCheckUtils.getList(VerifyApproveTradeCertBean.dmsTradeCertUploadedDocsUrlWithinServer, applNo);
                if (!vtDocumentModelList.isEmpty()) {
                    int count = 0;
                    for (Object vtDocumentModelListObj : vtDocumentModelList) {
                        VTDocumentModel vtDocumentModelListItem = (VTDocumentModel) vtDocumentModelListObj;
                        if (vtDocumentModelListItem.isDoc_verified()) {
                            count++;
                        }
                    }
                    noOfRecords = count;
                }
                ////////////////////////////////////////
            } else if (flag.equals("A")) {
                // query = " select count from dblink('" + getDMSDblink() + "','Select count(doc_approved) as count from vahandocs.vt_document where appl_no = ''" + applNo + "'' and doc_approved = true') as  (count integer) ";
                /////// USING DMS SERVICE //////////////
                List vtDocumentModelList = DmsDocCheckUtils.getList(VerifyApproveTradeCertBean.dmsTradeCertUploadedDocsUrlWithinServer, applNo);
                if (!vtDocumentModelList.isEmpty()) {
                    int count = 0;
                    for (Object vtDocumentModelListObj : vtDocumentModelList) {
                        VTDocumentModel vtDocumentModelListItem = (VTDocumentModel) vtDocumentModelListObj;
                        if (vtDocumentModelListItem.isDoc_approved()) {
                            count++;
                        }
                    }
                    noOfRecords = count;
                }
                ////////////////////////////////////////
            }
//            ps = tmgr.prepareStatement(query);
//            rs = tmgr.fetchDetachedRowSet();
//            if (rs.next()) {
//                noOfRecords = rs.getInt(1);
//            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
//            try {
//                if (tmgr != null) {
//                    tmgr.release();
//                }
//            } catch (Exception e) {
//                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//            }
        }
        return noOfRecords;
    }

    public static boolean documentsNotUploaded(String applNo) throws VahanException {
        try {
            List vtDocumentModelList = DmsDocCheckUtils.getList(VerifyApproveTradeCertBean.dmsTradeCertUploadedDocsUrlWithinServer, applNo);
            return vtDocumentModelList.isEmpty() ? true : false;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void fillAuditTrailForOnlineApplication(TransactionManager tmgr, VerifyApproveTradeCertDobj verifyApproveTradeCertDobj, int formId) throws VahanException {
        PreparedStatement pstmt = null;
        RowSet rs = null;
        try {

            String sql = "SELECT "
                    + " receiver_hard_copy_received_on, receiver, receiver_forward_remarks, receiver_backward_remarks, receiver_deficiency_mail_content,"
                    + " verifier_hard_copy_received_on, verifier, verifier_forward_remarks, verifier_backward_remarks, verifier_deficiency_mail_content,"
                    + " approver_hard_copy_received_on, approver, approver_forward_remarks, approver_backward_remarks, approver_deficiency_mail_content,"
                    + " current_status "
                    + " FROM " + TableList.VA_TRADE_CERTIFICATE_AUDIT_TRAIL
                    + " WHERE  state_cd = ? and appl_no = ? and sr_no = ? and iteration_counter = ?";

            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, verifyApproveTradeCertDobj.getStateCd());
            pstmt.setString(2, verifyApproveTradeCertDobj.getApplNo());
            pstmt.setInt(3, Integer.valueOf(verifyApproveTradeCertDobj.getSrNo()));
            pstmt.setInt(4, verifyApproveTradeCertDobj.getIterationCounter());
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {

                if (!CommonUtils.isNullOrBlank(verifyApproveTradeCertDobj.getCurrentTimeStampAsMarkHardCopyReceivedInStringFormat())
                        && formId == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE) {
                    verifyApproveTradeCertDobj.setReceiverHardCopyReceivedOn(verifyApproveTradeCertDobj.getCurrentTimeStampAsMarkHardCopyReceivedInStringFormat());
                } else {
                    verifyApproveTradeCertDobj.setReceiverHardCopyReceivedOn(rs.getString("receiver_hard_copy_received_on"));
                }

                if (CommonUtils.isNullOrBlank(verifyApproveTradeCertDobj.getReceiver())) {
                    verifyApproveTradeCertDobj.setReceiver(rs.getString("receiver"));
                }

                if (CommonUtils.isNullOrBlank(verifyApproveTradeCertDobj.getReceiverForwardRemarks())) {
                    verifyApproveTradeCertDobj.setReceiverForwardRemarks(rs.getString("receiver_forward_remarks"));
                }

                if (CommonUtils.isNullOrBlank(verifyApproveTradeCertDobj.getReceiverBackwardRemarks())) {
                    verifyApproveTradeCertDobj.setReceiverBackwardRemarks(rs.getString("receiver_backward_remarks"));
                }

                if (CommonUtils.isNullOrBlank(verifyApproveTradeCertDobj.getReceiverDeficiencyMailContent())) {
                    verifyApproveTradeCertDobj.setReceiverDeficiencyMailContent(rs.getString("receiver_deficiency_mail_content"));
                }

                if (!CommonUtils.isNullOrBlank(verifyApproveTradeCertDobj.getCurrentTimeStampAsMarkHardCopyReceivedInStringFormat())
                        && formId == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION) {
                    verifyApproveTradeCertDobj.setVerifierHardCopyReceivedOn(verifyApproveTradeCertDobj.getCurrentTimeStampAsMarkHardCopyReceivedInStringFormat());
                } else {
                    verifyApproveTradeCertDobj.setVerifierHardCopyReceivedOn(rs.getString("verifier_hard_copy_received_on"));
                }

                if (CommonUtils.isNullOrBlank(verifyApproveTradeCertDobj.getVerifier())) {
                    verifyApproveTradeCertDobj.setVerifier(rs.getString("verifier"));
                }

                if (CommonUtils.isNullOrBlank(verifyApproveTradeCertDobj.getVerifierForwardRemarks())) {
                    verifyApproveTradeCertDobj.setVerifierForwardRemarks(rs.getString("verifier_forward_remarks"));
                }

                if (CommonUtils.isNullOrBlank(verifyApproveTradeCertDobj.getVerifierBackwardRemarks())) {
                    verifyApproveTradeCertDobj.setVerifierBackwardRemarks(rs.getString("verifier_backward_remarks"));
                }

                if (CommonUtils.isNullOrBlank(verifyApproveTradeCertDobj.getVerifierDeficiencyMailContent())) {
                    verifyApproveTradeCertDobj.setVerifierDeficiencyMailContent(rs.getString("verifier_deficiency_mail_content"));
                }

                if (!CommonUtils.isNullOrBlank(verifyApproveTradeCertDobj.getCurrentTimeStampAsMarkHardCopyReceivedInStringFormat())
                        && formId == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE) {
                    verifyApproveTradeCertDobj.setApproverHardCopyReceivedOn(verifyApproveTradeCertDobj.getCurrentTimeStampAsMarkHardCopyReceivedInStringFormat());
                } else {
                    verifyApproveTradeCertDobj.setApproverHardCopyReceivedOn(rs.getString("approver_hard_copy_received_on"));
                }

                if (CommonUtils.isNullOrBlank(verifyApproveTradeCertDobj.getApprover())) {
                    verifyApproveTradeCertDobj.setApprover(rs.getString("approver"));
                }

                if (CommonUtils.isNullOrBlank(verifyApproveTradeCertDobj.getApproverForwardRemarks())) {
                    verifyApproveTradeCertDobj.setApproverForwardRemarks(rs.getString("approver_forward_remarks"));
                }

                if (CommonUtils.isNullOrBlank(verifyApproveTradeCertDobj.getApproverBackwardRemarks())) {
                    verifyApproveTradeCertDobj.setApproverBackwardRemarks(rs.getString("approver_backward_remarks"));
                }

                if (CommonUtils.isNullOrBlank(verifyApproveTradeCertDobj.getApproverDeficiencyMailContent())) {
                    verifyApproveTradeCertDobj.setApproverDeficiencyMailContent(rs.getString("approver_deficiency_mail_content"));
                }

                verifyApproveTradeCertDobj.setCurrentStatus(rs.getString("current_status"));
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

    }

    private int updateIntoVaTradeCertificateAuditTrail(TransactionManager tmgr, VerifyApproveTradeCertDobj verifyApproveTradeCertDobj, int formId) throws VahanException {

        PreparedStatement pstmt = null;
        int recordsUpdated = 0;
        try {

            String sql = "UPDATE " + TableList.VA_TRADE_CERTIFICATE_AUDIT_TRAIL
                    + "   SET ";

            if (formId == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE) {
                sql += "       receiver_hard_copy_received_on = ?, receiver = ?, receiver_forward_remarks = ?, receiver_backward_remarks = ?, receiver_deficiency_mail_content = ?,";
            } else if (formId == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION) {
                sql += "       verifier_hard_copy_received_on = ?, verifier = ?, verifier_forward_remarks = ?, verifier_backward_remarks = ?, verifier_deficiency_mail_content = ?,";
            }
            if (formId == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE) {
                sql += "       approver_hard_copy_received_on = ?, approver = ?, approver_forward_remarks = ?, approver_backward_remarks = ?, approver_deficiency_mail_content = ?,";
            }

            sql += "       current_status = ? "
                    + " WHERE  state_cd = ? and appl_no = ? and sr_no = ? and iteration_counter = ?";

            pstmt = tmgr.prepareStatement(sql);

            if (formId == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_RECEIVE) {
                pstmt.setString(1, verifyApproveTradeCertDobj.getReceiverHardCopyReceivedOn());
                pstmt.setString(2, verifyApproveTradeCertDobj.getReceiver());
                pstmt.setString(3, verifyApproveTradeCertDobj.getReceiverForwardRemarks());
                pstmt.setString(4, verifyApproveTradeCertDobj.getReceiverBackwardRemarks());
                pstmt.setString(5, verifyApproveTradeCertDobj.getReceiverDeficiencyMailContent());
            }

            if (formId == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_VERIFICATION) {
                pstmt.setString(1, verifyApproveTradeCertDobj.getVerifierHardCopyReceivedOn());
                pstmt.setString(2, verifyApproveTradeCertDobj.getVerifier());
                pstmt.setString(3, verifyApproveTradeCertDobj.getVerifierForwardRemarks());
                pstmt.setString(4, verifyApproveTradeCertDobj.getVerifierBackwardRemarks());
                pstmt.setString(5, verifyApproveTradeCertDobj.getVerifierDeficiencyMailContent());
            }

            if (formId == TableConstants.TM_ROLE_TRADE_CERT_ONLINE_APPL_APPROVE) {
                pstmt.setString(1, verifyApproveTradeCertDobj.getApproverHardCopyReceivedOn());
                pstmt.setString(2, verifyApproveTradeCertDobj.getApprover());
                pstmt.setString(3, verifyApproveTradeCertDobj.getApproverForwardRemarks());
                pstmt.setString(4, verifyApproveTradeCertDobj.getApproverBackwardRemarks());
                pstmt.setString(5, verifyApproveTradeCertDobj.getApproverDeficiencyMailContent());
            }

            pstmt.setString(6, verifyApproveTradeCertDobj.getStatus());
            pstmt.setString(7, verifyApproveTradeCertDobj.getStateCd());
            pstmt.setString(8, verifyApproveTradeCertDobj.getApplNo());
            pstmt.setInt(9, Integer.valueOf(verifyApproveTradeCertDobj.getSrNo()));
            pstmt.setInt(10, Integer.valueOf(verifyApproveTradeCertDobj.getIterationCounter()));

            recordsUpdated = pstmt.executeUpdate();

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return recordsUpdated;

    }

    public static String getDMSDblink() throws VahanException {

        PreparedStatement psmt = null;
        RowSet rs;
        TransactionManager tmgr = null;
        String DMSDblink = "";
        String sql = " Select conn_dblink from tm_dblink_list where conn_type = 'DMS'";

        try {
            tmgr = new TransactionManager("getDMSDblink");
            psmt = tmgr.prepareStatement(sql);
            rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                DMSDblink = rs.getString("conn_dblink");
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return DMSDblink;
    }

    public static String getVehClassDesc(String vehClassCd) throws VahanException {

        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;

        String sql = " Select vch_class_descr from onlineschema_tc.vm_vch_class where vch_class_cd = ? ";

        try {
            tmgr = new TransactionManager("getVehCatgDesc");
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, Integer.valueOf(vehClassCd));
            rs = tmgr.fetchDetachedRowSet();

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
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return vehClassCd;
    }

    public static String getVehCatgDesc(String vehCatgCd) throws VahanException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
        String sql = "";
        if (tmConfigurationDobj.getTmTradeCertConfigDobj().isCmvrVchCatgApplicable()) {
            sql = "select cmv_catg_descr as catg_desc from " + TableList.VM_TRADE_VCH_CATG_MAPPING + " where veh_catg_cmv = ?";
        } else {
            sql = "Select catg_desc from vm_vch_catg where catg = ?";
        }
        try {
            tmgr = new TransactionManager("getVehCatgDesc");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, vehCatgCd);
            rs = psmt.executeQuery();
            if (rs.next()) {
                return rs.getString("catg_desc");
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (psmt != null) {
                    psmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return vehCatgCd;
    }

    public static String getDealerName(String dealerCd, String stateCd, int offCd, String applicantType) throws VahanException {
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        String dealerName = "";
        String sql = null;
        if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_MANUFACTURER)) {
            sql = " Select maker_name  from " + TableList.VM_MAKER_TC + " where maker_cd = ? and state_cd = ?";
        } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER)) {
            sql = " Select financer_name  from " + TableList.VM_FINANCIER_TC + " where financer_cd = ? and state_cd = ?";
        } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_RETRO_FITTER)) {
            sql = " Select retrofitter_name  from " + TableList.VM_RETROFITTER_TC + " where retrofitter_cd = ? and state_cd = ?";
        } else {
            sql = " Select dealer_name  from " + TableList.VM_DEALER_MAST + " where dealer_cd = ? and state_cd = ? and off_cd = ? ";
        }

        try {
            tmgr = new TransactionManager("getDealerName");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, String.valueOf(dealerCd));
            psmt.setString(2, stateCd);
            if (CommonUtils.isNullOrBlank(applicantType)
                    || (!CommonUtils.isNullOrBlank(applicantType) && (applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)))) {
                psmt.setInt(3, offCd);
            }
            rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                dealerName = rs.getString(1);
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
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return dealerCd;
    }

    public String getApplicantNameByType(String dealerCd, String stateCd, int offCd, String applicantType) throws VahanException {
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        String dealerName = "";
        String fieldName = "";
        String tableName = "";
        String applicantCd = "";
        if (applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)) {
            tableName = TableList.VM_DEALER_MAST;
            fieldName = "dealer_name";
            applicantCd = "dealer_cd";
        } else if (applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER)) {
            tableName = TableList.VM_FINANCIER_TC;
            fieldName = "financer_name";
            applicantCd = "financer_cd";
        }

        String sql = " Select " + fieldName + " from " + tableName + " where " + applicantCd + " in ('" + dealerCd + "') and state_cd = ? and off_cd = ? ";

        try {
            tmgr = new TransactionManager("getDealerName");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, stateCd);
            psmt.setInt(2, offCd);
            rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                dealerName = rs.getString(fieldName);
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
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return dealerCd;
    }

    public String save(List applicationTradeCertificateDobjs) throws VahanException {

        TransactionManager tmgr = null;
        String saveReturn = "";
        try {
            tmgr = new TransactionManager("ApplicationTradeCert_Impl.save()");
            saveReturn = insertIntoVaTradeCertificate(tmgr, applicationTradeCertificateDobjs);
            if (saveReturn.contains("SUCCESS") && !applicationTradeCertificateDobjs.isEmpty()) {
                ApplicationTradeCertDobj dobj = (ApplicationTradeCertDobj) applicationTradeCertificateDobjs.get(0);
                Status_dobj status = new Status_dobj();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                String dt = sdf.format(new Date());
                status.setAppl_dt(dt);
                status.setAppl_no(saveReturn.substring(saveReturn.lastIndexOf(":") + 1));
                status.setPur_cd(Integer.parseInt(dobj.getPurCd()));
                status.setFlow_slno(1);
                status.setFile_movement_slno(1);
                int actionCdFromUtil = Util.getSelectedSeat().getAction_cd();
                if (Integer.parseInt(dobj.getPurCd()) == TableConstants.VM_TRANSACTION_TRADE_CERT_NEW) {
                    status.setAction_cd(TableConstants.TM_ROLE_TRADE_CERT_APPL_ENTRY);
                    Util.getSelectedSeat().setAction_cd(TableConstants.TM_ROLE_TRADE_CERT_APPL_ENTRY);
                } else if (Integer.parseInt(dobj.getPurCd()) == TableConstants.VM_TRANSACTION_TRADE_CERT_DUP) {
                    status.setAction_cd(TableConstants.TM_ROLE_TRADE_CERT_APPL_ENTRY_DUP);
                    Util.getSelectedSeat().setAction_cd(TableConstants.TM_ROLE_TRADE_CERT_APPL_ENTRY_DUP);
                }
                status.setEmp_cd(Long.parseLong(Util.getEmpCode()));
                status.setRegn_no("NEW");
                status.setOffice_remark("");
                status.setPublic_remark("");
                status.setStatus("N");
                status.setState_cd(dobj.getStateCd());
                status.setOff_cd(dobj.getOffCd());
                ServerUtil.fileFlowForNewApplication(tmgr, status);

                status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
                status.setStatus(TableConstants.STATUS_COMPLETE);
                status.setAppl_no(saveReturn.substring(saveReturn.lastIndexOf(":") + 1));
                status.setOffice_remark("");
                status.setPublic_remark("");

                ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, saveReturn.substring(saveReturn.lastIndexOf(":") + 1),
                        TableConstants.TM_ROLE_TRADE_CERT_FEE, Integer.parseInt(dobj.getPurCd()), null, tmgr);

                ServerUtil.fileFlow(tmgr, status);
                Util.getSelectedSeat().setAction_cd(actionCdFromUtil);
                tmgr.commit();
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                tmgr.release();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }

        }
        return saveReturn;
    }

    public String update(List applicationTradeCertificateDobjs) throws VahanException {
        TransactionManager tmgr = null;
        Map mapSectionsSrNoOfSelectedApplicationPrev = new HashMap();
        String updateReturn = "";
        String updateVaReturn = "";
        String historySaveReturn = "";
        try {
            tmgr = new TransactionManager("ApplicationTradeCert_Impl.update()");

            String applNo = ((ApplicationTradeCertDobj) applicationTradeCertificateDobjs.get(0)).getApplNo();
            getVaTradeCertificateRecords(mapSectionsSrNoOfSelectedApplicationPrev, applNo);
            if (!mapSectionsSrNoOfSelectedApplicationPrev.isEmpty()) {
                updateVaReturn = updateVaTradeCertificate(tmgr, applicationTradeCertificateDobjs);
                historySaveReturn = insertIntoVhaTradeCertificate(tmgr, mapSectionsSrNoOfSelectedApplicationPrev);

                if (updateVaReturn.contains("SUCCESS") && historySaveReturn.contains("SUCCESS")) {
                    tmgr.commit();
                    updateReturn += updateVaReturn;
                } else {
                    updateReturn = "FAILURE";
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return updateReturn;
    }

    private void getVaTradeCertificateRecords(Map mapSectionsSrNoOfSelectedApplicationPrev, String applNo) throws VahanException {
        getAllSrNoForSelectedApplication(applNo, mapSectionsSrNoOfSelectedApplicationPrev);
    }

    private String insertIntoVhaTradeCertificate(TransactionManager tmgr, Map mapSectionsSrNoOfSelectedApplicationPrev) throws VahanException {
        PreparedStatement pstmt = null;
        String exceptionSegment = ": some reason ";
        String result = ": Application No:";
        try {

            String sql = "INSERT INTO " + TableList.VHA_TRADE_CERTIFICATE + "(state_cd, off_cd, appl_no, dealer_cd, sr_no, vch_catg,no_of_vch, op_dt,moved_on,moved_by) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,?)";

            pstmt = tmgr.prepareStatement(sql);

            String applNo = ((ApplicationTradeCertDobj) mapSectionsSrNoOfSelectedApplicationPrev.get("1")).getApplNo();

            ApplicationTradeCertDobj applicationTradeCertDobj = null;

            for (Object keyObj : mapSectionsSrNoOfSelectedApplicationPrev.keySet()) {

                applicationTradeCertDobj = (ApplicationTradeCertDobj) mapSectionsSrNoOfSelectedApplicationPrev.get(keyObj);
                applicationTradeCertDobj.setApplNo(applNo);

                pstmt.setString(1, applicationTradeCertDobj.getStateCd());
                pstmt.setInt(2, applicationTradeCertDobj.getOffCd());
                pstmt.setString(3, applNo);
                pstmt.setString(4, applicationTradeCertDobj.getDealerFor());
                pstmt.setInt(5, Integer.valueOf(applicationTradeCertDobj.getSrNo()));
                pstmt.setString(6, applicationTradeCertDobj.getVehCatgFor());
                pstmt.setInt(7, Integer.valueOf(applicationTradeCertDobj.getNoOfAllowedVehicles()));
                pstmt.setString(8, Util.getEmpCode());

                pstmt.addBatch();
            }

            int[] recordsInserted = pstmt.executeBatch();

            if (recordsInserted.length == mapSectionsSrNoOfSelectedApplicationPrev.size()) {
                result = "SUCCESS" + result + applNo;
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            result = "FAILURE" + exceptionSegment;
            throw new VahanException(TableConstants.SomthingWentWrong);

        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return result;
    }

    private String updateVaTradeCertificate(TransactionManager tmgr, List applicationTradeCertificateDobjs) throws VahanException {
        PreparedStatement pstmt = null;
        String exceptionSegment = ": some reason ";
        String result = ": Application No:";
        try {

            String sql = "UPDATE va_trade_certificate SET no_of_vch = ?,op_dt = CURRENT_TIMESTAMP WHERE appl_no = ?";

            pstmt = tmgr.prepareStatement(sql);

            String applNo = ((ApplicationTradeCertDobj) applicationTradeCertificateDobjs.get(0)).getApplNo();

            ApplicationTradeCertDobj applicationTradeCertDobj = null;

            for (Object keyObj : applicationTradeCertificateDobjs) {

                applicationTradeCertDobj = (ApplicationTradeCertDobj) keyObj;
                applicationTradeCertDobj.setApplNo(applNo);

                pstmt.setInt(1, Integer.valueOf(applicationTradeCertDobj.getNoOfAllowedVehicles()));
                pstmt.setString(2, applNo);

                pstmt.addBatch();
            }

            int[] recordsInserted = pstmt.executeBatch();

            if (recordsInserted.length == applicationTradeCertificateDobjs.size()) {
                result = "SUCCESS" + result + applNo;
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            result = "FAILURE" + exceptionSegment;
            throw new VahanException(TableConstants.SomthingWentWrong);

        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return result;
    }

    public String fileMoveToIssueTradeCertificate(Status_dobj statusParam) throws VahanException {
        Status_dobj status = statusParam;
        TransactionManager tmgr = null;
        String exceptionSegment = ": some reason ";
        String result = ": Application No:";
        try {

            tmgr = new TransactionManager("ApplicationTradeCert_Impl.fileMoveToIssueTradeCertificate()");
            status = ServerUtil.webServiceForNextStage(status, tmgr);
            ServerUtil.fileFlow(tmgr, status);
            tmgr.commit();
            result = "SUCCESS " + result + status.getAppl_no();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            result = "FAILURE : " + exceptionSegment;
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
        return result;
    }

    public String grantTradeCertificate(List<VerifyApproveTradeCertDobj> verifyApproveTradeCertDobjList, Status_dobj statusDobj) throws VahanException {
        TransactionManager tmgr = null;
        String exceptionSegment = ": some reason ";
        String result = "FAILURE";
        try {

            tmgr = new TransactionManager("ApplicationTradeCert_Impl.grantTradeCertificate()");
            result = submitOdishaTCApplication(verifyApproveTradeCertDobjList, "A", tmgr);
            if (result.contains("SUCCESS")) {
                statusDobj = ServerUtil.webServiceForNextStage(statusDobj, tmgr);
                ServerUtil.fileFlow(tmgr, statusDobj);
                tmgr.commit();
                result = "SUCCESS " + result + statusDobj.getAppl_no();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            result = "FAILURE : " + exceptionSegment;
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
        return result;
    }

    public String fileMoveToSubmitOnlineApplication(Status_dobj statusParam, TransactionManager tmgr) throws VahanException {
        Status_dobj status = statusParam;

        String exceptionSegment = ": some reason ";
        String result = ": Application No:";
        try {
            status = ServerUtil.webServiceForNextStage(status, tmgr);
            ServerUtil.fileFlow(tmgr, status);
            //  tmgr.commit();
            result = "SUCCESS " + result + status.getAppl_no();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            result = "FAILURE : " + exceptionSegment;
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return result;
    }

    public String sameApplcationPendingForDealerAndVehicleCategoryAndFuelType(String dealerCd, List vehicleCategoriesList, List fuelTypes) throws VahanException {
        TransactionManager tmgr = null;
        String pendingApplNo = "";
        PreparedStatement pstmt = null;
        List tempFuelList = new ArrayList();
        String sql = "select fuel_cd,vch_catg,appl_no from va_trade_certificate where dealer_cd = ?";
        try {
            tmgr = new TransactionManager("ApplicationTradeCertImpl.sameApplcationPendingForDealerAndVehicleCategoryAndFuelType()");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, dealerCd);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                if (vehicleCategoriesList != null && !vehicleCategoriesList.isEmpty() && vehicleCategoriesList.get(0) instanceof ApplicationTradeCertDobj) {  //Renew
                    List tempList = new ArrayList();
                    for (Object vehCatgListItemObj : vehicleCategoriesList) {
                        ApplicationTradeCertDobj vehCatgListItem = (ApplicationTradeCertDobj) vehCatgListItemObj;
                        tempList.add(vehCatgListItem.getVehCatgFor());
                        tempFuelList.add(vehCatgListItem.getFuelTypeFor());
                    }
                    vehicleCategoriesList.clear();
                    vehicleCategoriesList.addAll(tempList);
                }
                if (fuelTypes.isEmpty()) {
                    for (Object vehCatgObj : vehicleCategoriesList) {
                        String vehCatg = (String) vehCatgObj;
                        String vehCatgFromDB = rs.getString("vch_catg");
                        if (vehCatgFromDB.equalsIgnoreCase(vehCatg)) {
                            pendingApplNo = rs.getString("appl_no");
                            break;
                        }
                    }
                } else {
                    for (Object vehCatgObj : vehicleCategoriesList) {
                        String vehCatg = (String) vehCatgObj;
                        String vehCatgFromDB = rs.getString("vch_catg");
                        if (!tempFuelList.isEmpty()) {
                            fuelTypes = tempFuelList;
                        }
                        for (Object fuelTypeObj : fuelTypes) {
                            String fuelType = (String) fuelTypeObj;
                            String fuelTypeFromDB = rs.getInt("fuel_cd") + "";
                            if (vehCatgFromDB.equalsIgnoreCase(vehCatg)
                                    && fuelType.equalsIgnoreCase(fuelTypeFromDB)) {
                                pendingApplNo = rs.getString("appl_no");
                                break;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return pendingApplNo;
    }

    public static void insertTCApplicationToHistory(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VHA_TRADE_CERTIFICATE
                + " SELECT state_cd, off_cd, appl_no, dealer_cd, sr_no, vch_catg, no_of_vch, \n"
                + "       op_dt, current_timestamp as moved_on, ? moved_by \n"
                + "  FROM " + TableList.VA_TRADE_CERTIFICATE + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    } // end of insertIntoToHistory

    public static void deleteApplcantMastApplDetails(TransactionManager tmgr, String applicantCd) throws SQLException {
        String sql = "DELETE FROM " + TableList.VM_APPLICANT_MAST_APPL + " WHERE applicant_cd=?";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, applicantCd);
        ps.executeUpdate();
    }

    public String fetchTemporaryApplNoFromOnlineSchemaVaTradeCertAppl(String dealerFor, String applNo) throws VahanException {
        TransactionManager tmgr = null;
        String tempApplNo = "";
        PreparedStatement pstmt = null;
        String sql = "select temp_appl_no from " + TableList.VHA_TRADE_CERTIFICATE_APPL + " where applicant_cd = ? and appl_no = ? ";
        try {
            tmgr = new TransactionManager("ApplicationTradeCertImpl.fetchTemporaryApplNoFromOnlineSchemaVaTradeCertAppl()");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, dealerFor);
            pstmt.setString(2, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                tempApplNo = rs.getString("temp_appl_no");
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return tempApplNo;
    }

    private Date getOpDtFromVa(String applNo) throws VahanException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        Date opDt = null;
        String sql = "";

        try {
            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();
            if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {    //// using_online_schema_tc work
                sql = "Select op_dt from " + TableList.VA_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " where appl_no='" + applNo + "'";
            } else {
                sql = "Select op_dt from " + TableList.VA_TRADE_CERTIFICATE + " where appl_no='" + applNo + "'";
            }
            tmgr = new TransactionManager("getOpDtFromVa");
            psmt = tmgr.prepareStatement(sql);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                opDt = rs.getDate("op_dt");
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return opDt;
    }

    public int getNoOfVchFromVt(String tradeCertNo) throws VahanException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        int noOfVch = 0;
        String sql = "";

        try {
            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();
            if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {   //// using_online_schema_tc work
                sql = "Select no_of_vch from " + TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " where cert_no='" + tradeCertNo + "'";
            } else {
                sql = "Select no_of_vch from " + TableList.VT_TRADE_CERTIFICATE + " where cert_no='" + tradeCertNo + "'";
            }
            tmgr = new TransactionManager("getOpDtFromVa");
            psmt = tmgr.prepareStatement(sql);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                noOfVch = rs.getInt("no_of_vch");
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return noOfVch;
    }

    public String insertIntoVtTradeCertificate(PreparedStatement psInsertIntoVtTradeCert, String tradeCertificateNo, VerifyApproveTradeCertDobj dobj) throws VahanException {

        String exceptionSegment = ": some reason ";
        String result = ": Trade Certificate No:";
        try {

            dobj.setTradeCertNo(tradeCertificateNo);

            psInsertIntoVtTradeCert.setString(1, dobj.getStateCd());
            psInsertIntoVtTradeCert.setInt(2, dobj.getOffCd());
            psInsertIntoVtTradeCert.setString(3, dobj.getApplNo());
            psInsertIntoVtTradeCert.setString(4, dobj.getDealerFor());
            psInsertIntoVtTradeCert.setInt(5, Integer.valueOf(dobj.getSrNo()));
            psInsertIntoVtTradeCert.setString(6, dobj.getVehCatgFor());
            psInsertIntoVtTradeCert.setInt(7, Integer.valueOf(dobj.getNoOfAllowedVehicles()));
            psInsertIntoVtTradeCert.setString(8, dobj.getTradeCertNo());
            psInsertIntoVtTradeCert.setDate(9, new java.sql.Date(dobj.getValidDt().getTime()));
            psInsertIntoVtTradeCert.setDate(10, new java.sql.Date(dobj.getValidUpto().getTime()));
            psInsertIntoVtTradeCert.setDate(11, new java.sql.Date(dobj.getIssueDt().getTime()));
            psInsertIntoVtTradeCert.setString(12, dobj.getApplicantType());

            psInsertIntoVtTradeCert.addBatch();
            result = "SUCCESS";
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            result = "FAILURE" + exceptionSegment;
            throw new VahanException(TableConstants.SomthingWentWrong);

        }
        return result;
    }

    private void deletePreviousApplFromVaTCPrint(TransactionManager tmgr, String tradeCertNo) throws VahanException, SQLException {
        PreparedStatement pstmt = null;

        String sqlVaTcPrint = "delete from va_tc_print where  cert_no = ?";
        String sqlVhTcPrint = "delete from vh_tc_print where  cert_no = ?";
        try {

            pstmt = tmgr.prepareStatement(sqlVaTcPrint);
            pstmt.setString(1, tradeCertNo);
            pstmt.executeUpdate();

            pstmt = tmgr.prepareStatement(sqlVhTcPrint);
            pstmt.setString(1, tradeCertNo);
            pstmt.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
        }
    }

    private boolean insertIntoVaTCPrintForOnlineApplication(TransactionManager tmgr, String applNo, String tradeCertNo, String stateCd, int offCd) throws VahanException, SQLException {

        PreparedStatement pstmt = null;

        String sqlVaTcPrint = "Insert into va_tc_print(state_cd,off_cd,appl_no,cert_no,op_dt) values(?,?,?,?,CURRENT_TIMESTAMP)";

        try {
            pstmt = tmgr.prepareStatement(sqlVaTcPrint);
            pstmt.setString(1, stateCd);
            pstmt.setInt(2, offCd);
            pstmt.setString(3, applNo);
            pstmt.setString(4, tradeCertNo);
            int i = pstmt.executeUpdate();
            if (i > 0) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
        }
        return false;
    }

    public int insertIntoVhaTradeCertAuditTrail(TransactionManager tmgr, VerifyApproveTradeCertDobj verifyApproveTradeCertDobj)
            throws VahanException {
        PreparedStatement ps = null;
        try {

            String vaTradeCertificateAuditTrailSql = null;

            vaTradeCertificateAuditTrailSql = " INSERT INTO " + TableList.VHA_TRADE_CERTIFICATE_AUDIT_TRAIL
                    + " (moved_on,moved_by,state_cd,appl_no,sr_no,iteration_counter,"
                    + " applicant , applicant_forward_remarks,"
                    + " receiver_hard_copy_received_on , receiver ,receiver_forward_remarks,receiver_backward_remarks,receiver_deficiency_mail_content,"
                    + " verifier_hard_copy_received_on , verifier ,verifier_forward_remarks,verifier_backward_remarks,verifier_deficiency_mail_content,"
                    + " approver_hard_copy_received_on , approver ,approver_forward_remarks,approver_backward_remarks,approver_deficiency_mail_content,"
                    + " current_status)\n"
                    + " SELECT  CURRENT_TIMESTAMP,?, state_cd,appl_no,sr_no,iteration_counter,"
                    + " applicant , applicant_forward_remarks,"
                    + " receiver_hard_copy_received_on , receiver ,receiver_forward_remarks,receiver_backward_remarks,receiver_deficiency_mail_content,"
                    + " verifier_hard_copy_received_on , verifier ,verifier_forward_remarks,verifier_backward_remarks,verifier_deficiency_mail_content,"
                    + " approver_hard_copy_received_on , approver ,approver_forward_remarks,approver_backward_remarks,approver_deficiency_mail_content,"
                    + " current_status "
                    + " FROM " + TableList.VA_TRADE_CERTIFICATE_AUDIT_TRAIL
                    + " WHERE state_cd = ? and appl_no = ? and sr_no = ? ";
            ps = tmgr.prepareStatement(vaTradeCertificateAuditTrailSql);
            ps.setString(1, verifyApproveTradeCertDobj.getEmpCd() + "");
            ps.setString(2, verifyApproveTradeCertDobj.getStateCd());
            ps.setString(3, verifyApproveTradeCertDobj.getApplNo());
            ps.setInt(4, 1);
            return ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public int insertIntoVhaTradeCertAuditTrail(TransactionManager tmgr, VerifyApproveTradeCertDobj verifyApproveTradeCertDobj, String status)
            throws VahanException {
        PreparedStatement ps = null;
        try {

            String vaTradeCertificateAuditTrailSql = null;

            vaTradeCertificateAuditTrailSql = " INSERT INTO " + TableList.VHA_TRADE_CERTIFICATE_AUDIT_TRAIL
                    + " (moved_on,moved_by,state_cd,appl_no,sr_no,iteration_counter,"
                    + " applicant , applicant_forward_remarks,"
                    + " receiver_hard_copy_received_on , receiver ,receiver_forward_remarks,receiver_backward_remarks,receiver_deficiency_mail_content,"
                    + " verifier_hard_copy_received_on , verifier ,verifier_forward_remarks,verifier_backward_remarks,verifier_deficiency_mail_content,"
                    + " approver_hard_copy_received_on , approver ,approver_forward_remarks,approver_backward_remarks,approver_deficiency_mail_content,"
                    + " current_status)\n"
                    + " SELECT  CURRENT_TIMESTAMP,?, state_cd,appl_no,sr_no,iteration_counter,"
                    + " applicant , applicant_forward_remarks,"
                    + " receiver_hard_copy_received_on , receiver ,receiver_forward_remarks,receiver_backward_remarks,receiver_deficiency_mail_content,"
                    + " verifier_hard_copy_received_on , verifier ,verifier_forward_remarks,verifier_backward_remarks,verifier_deficiency_mail_content,"
                    + " approver_hard_copy_received_on , approver ,approver_forward_remarks,approver_backward_remarks,approver_deficiency_mail_content,"
                    + " ? "
                    + " FROM " + TableList.VA_TRADE_CERTIFICATE_AUDIT_TRAIL
                    + " WHERE state_cd = ? and appl_no = ? and sr_no = ? ";
            ps = tmgr.prepareStatement(vaTradeCertificateAuditTrailSql);
            ps.setString(1, verifyApproveTradeCertDobj.getEmpCd() + "");
            ps.setString(2, status);
            ps.setString(3, verifyApproveTradeCertDobj.getStateCd());
            ps.setString(4, verifyApproveTradeCertDobj.getApplNo());
            ps.setInt(5, 1);
            return ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public int updateVaTradeCertAuditTrail(TransactionManager tmgr, VerifyApproveTradeCertDobj verifyApproveTradeCertDobj, String status)
            throws VahanException {
        PreparedStatement ps = null;
        try {

            String vaTradeCertificateAuditTrailSql = null;
            int latestIterationCounter = selectLatestIterationCounterFromAuditTrailForOnlineApplication(tmgr, verifyApproveTradeCertDobj) + 1;
            vaTradeCertificateAuditTrailSql = "UPDATE " + TableList.VA_TRADE_CERTIFICATE_AUDIT_TRAIL
                    + " SET iteration_counter = ?,"
                    + " applicant = ?, applicant_forward_remarks = ?,"
                    + " receiver_hard_copy_received_on = ?, receiver = ?,receiver_forward_remarks = ?,receiver_backward_remarks = ?,receiver_deficiency_mail_content = ?,"
                    + " verifier_hard_copy_received_on = ?, verifier = ?,verifier_forward_remarks = ?,verifier_backward_remarks = ?,verifier_deficiency_mail_content = ?,"
                    + " approver_hard_copy_received_on = ?, approver = ?,approver_forward_remarks = ?,approver_backward_remarks = ?,approver_deficiency_mail_content = ?,"
                    + " current_status = ? \n"
                    + " WHERE state_cd = ? and appl_no = ? and sr_no = ?";
            ps = tmgr.prepareStatement(vaTradeCertificateAuditTrailSql);
            ps.setInt(1, latestIterationCounter); ////Incrementing iteration counter
            ps.setString(2, null);
            ps.setString(3, null);
            ps.setString(4, null);
            ps.setString(5, null);
            ps.setString(6, null);
            ps.setString(7, null);
            ps.setString(8, null);
            ps.setString(9, null);
            ps.setString(10, null);
            ps.setString(11, null);
            ps.setString(12, null);
            ps.setString(13, null);
            ps.setString(14, null);
            ps.setString(15, null);
            ps.setString(16, null);
            ps.setString(17, null);
            ps.setString(18, null);
            ps.setString(19, status);
            ps.setString(20, verifyApproveTradeCertDobj.getStateCd());
            ps.setString(21, verifyApproveTradeCertDobj.getApplNo());
            ps.setInt(22, 1);
            return ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public int updateRevertStatusVaTradeCertAuditTrail(TransactionManager tmgr, VerifyApproveTradeCertDobj verifyApproveTradeCertDobj, String status)
            throws VahanException {
        PreparedStatement ps = null;
        try {

            String vaTradeCertificateAuditTrailSql = "UPDATE " + TableList.VA_TRADE_CERTIFICATE_AUDIT_TRAIL
                    + " SET current_status = ? \n"
                    + " WHERE state_cd = ? and appl_no = ? and sr_no = ?";
            ps = tmgr.prepareStatement(vaTradeCertificateAuditTrailSql);
            ps.setString(1, status);
            ps.setString(2, verifyApproveTradeCertDobj.getStateCd());
            ps.setString(3, verifyApproveTradeCertDobj.getApplNo());
            ps.setInt(4, 1);
            return ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    private int selectLatestIterationCounterFromAuditTrailForOnlineApplication(TransactionManager tmgr, VerifyApproveTradeCertDobj verifyApproveTradeCertDobj) throws VahanException {
        PreparedStatement pstmt = null;
        RowSet rs = null;
        int iCounter = 0;
        try {

            String sql = "SELECT iteration_counter FROM " + TableList.VA_TRADE_CERTIFICATE_AUDIT_TRAIL + " WHERE state_cd = ? and appl_no = ? and sr_no = ?";

            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, verifyApproveTradeCertDobj.getStateCd());
            pstmt.setString(2, verifyApproveTradeCertDobj.getApplNo());
            pstmt.setInt(3, Integer.valueOf(verifyApproveTradeCertDobj.getSrNo()));
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                iCounter = rs.getInt("iteration_counter");
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return iCounter;
    }

    public static String getMakerdescr(String makerCd) throws VahanException {

        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;

        String sql = " SELECT  * FROM vahan4.vm_maker  where show_in_rto = 't' and code =" + makerCd + " ";

        try {
            tmgr = new TransactionManager("getMakerdescr");
            psmt = tmgr.prepareStatement(sql);

            rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                return rs.getString("descr");
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Unable to get vehicle catagory description.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return makerCd;
    }

    private boolean insertIntoVMDealerMastFromAppl(TransactionManager tmgr, VerifyApproveTradeCertDobj dobj)
            throws VahanException {

        PreparedStatement ps = null;
        String vaTradeCertificateSql = null;
        String vaDealerOtherDeatilsSql = null;

        try {
            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();
            if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {   //// using_online_schema_tc work
                vaTradeCertificateSql = "INSERT INTO " + TableList.VM_DEALER_MAST + "(dealer_cd,state_cd,off_cd,dealer_name,dealer_regn_no,d_add1,d_add2,d_district,d_pincode,d_state,valid_upto,entered_by,entered_on,tin_no,contact_no,email_id,maker,vch_class,applicant_relation)\n"
                        + " SELECT  applicant_cd, applicant_state_cd, applicant_off_cd, applicant_name,?,applicant_address1,applicant_address2,applicant_district,applicant_pincode,applicant_state_cd,?,?,CURRENT_TIMESTAMP,applicant_tin_no,CAST(applicant_mobile_no AS NUMERIC), applicant_email,applicant_maker_list,applicant_veh_class_list,applicant_relation"
                        + " \n"
                        + " FROM " + TableList.VM_APPLICANT_MAST_APPL_ONLINE_SCHEMA_TC + " where " + TableList.VM_APPLICANT_MAST_APPL_ONLINE_SCHEMA_TC + ".applicant_cd=?";

                vaDealerOtherDeatilsSql = "INSERT INTO " + TableList.VM_DEALER_OTHER_DETAILS + "(dealer_cd,showroom_name,showroom_add1,showroom_add2,showroom_district,showroom_pincode,loi_auth_no,loi_auth_date)\n"
                        + " SELECT applicant_cd,applicant_showroom_name,applicant_showroom_address1,applicant_showroom_address2,applicant_showroom_district,applicant_showroom_pincode,applicant_loi_auth_no,applicant_loi_auth_date\n"
                        + " FROM " + TableList.VM_APPLICANT_MAST_APPL_ONLINE_SCHEMA_TC + " where " + TableList.VM_APPLICANT_MAST_APPL_ONLINE_SCHEMA_TC + ".applicant_cd=?";
            } else {
                vaTradeCertificateSql = "INSERT INTO " + TableList.VM_DEALER_MAST + "(dealer_cd,state_cd,off_cd,dealer_name,dealer_regn_no,d_add1,d_add2,d_district,d_pincode,d_state,valid_upto,entered_by,entered_on,tin_no,contact_no,email_id,maker,vch_class,applicant_relation)\n"
                        + " SELECT  applicant_cd, applicant_state_cd, applicant_off_cd, applicant_name,?,applicant_address1,applicant_address2,applicant_district,applicant_pincode,applicant_state_cd,?,?,CURRENT_TIMESTAMP,applicant_tin_no,CAST(applicant_mobile_no AS NUMERIC), applicant_email,applicant_maker_list,applicant_veh_class_list,applicant_relation"
                        + " \n"
                        + " FROM " + TableList.VM_APPLICANT_MAST_APPL + " where " + TableList.VM_APPLICANT_MAST_APPL + ".applicant_cd=?";
            }

            ps = tmgr.prepareStatement(vaTradeCertificateSql);
            /// IN PLACE OF 'applicant_regn_no' FOR dealer_regn_no
            int idx = 1;
            ps.setString(idx++, dobj.getTradeCertNo());
            ps.setDate(idx++, new java.sql.Date(dobj.getValidUpto().getTime()));
            ps.setString(idx++, Util.getEmpCode() + "");
            ps.setString(idx++, dobj.getDealerFor() + "");
            int i = ps.executeUpdate();

            int j = 0;
            if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {    //// using_online_schema_tc work
                ps = tmgr.prepareStatement(vaDealerOtherDeatilsSql);
                ps.setString(1, dobj.getDealerFor() + "");
                j = ps.executeUpdate();
            }

            if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc() && i > 0 && j > 0) {   //// using_online_schema_tc work
                return true;
            } else if (i > 0) {
                return true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return false;
    }

    private boolean insertIntoVMFinancerMastFromAppl(TransactionManager tmgr, VerifyApproveTradeCertDobj dobj)
            throws VahanException {
        try {
            PreparedStatement ps = null;
            String vaTradeCertificateSql = null;
            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();
            if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {   //// using_online_schema_tc work
                vaTradeCertificateSql = "INSERT INTO " + TableList.VM_FINANCIER_TC + "(financer_cd,state_cd,off_cd,financer_name,branch_name,branch_add1,branch_add2,branch_district,branch_pincode,contact_no,landline_no,email_id,vch_class,entered_on,entered_by)\n"
                        + " SELECT  applicant_cd, applicant_state_cd, applicant_off_cd, applicant_name,applicant_branch_name,applicant_address1,applicant_address2,applicant_district,applicant_pincode,CAST(applicant_mobile_no AS NUMERIC),CAST(applicant_landline_no AS NUMERIC),applicant_email,applicant_veh_class_list,CURRENT_TIMESTAMP,? "
                        + " \n"
                        + " FROM " + TableList.VM_APPLICANT_MAST_APPL_ONLINE_SCHEMA_TC + " where " + TableList.VM_APPLICANT_MAST_APPL_ONLINE_SCHEMA_TC + ".applicant_cd=?";
            } else {
                vaTradeCertificateSql = "INSERT INTO " + TableList.VM_FINANCIER_TC + "(financer_cd,state_cd,off_cd,financer_name,branch_name,branch_add1,branch_add2,branch_district,branch_pincode,contact_no,landline_no,email_id,vch_class,entered_on,entered_by)\n"
                        + " SELECT  applicant_cd, applicant_state_cd, applicant_off_cd, applicant_name,applicant_branch_name,applicant_address1,applicant_address2,applicant_district,applicant_pincode,CAST(applicant_mobile_no AS NUMERIC),CAST(applicant_landline_no AS NUMERIC),applicant_email,applicant_veh_class_list,CURRENT_TIMESTAMP,? "
                        + " \n"
                        + " FROM " + TableList.VM_APPLICANT_MAST_APPL + " where " + TableList.VM_APPLICANT_MAST_APPL + ".applicant_cd=?";
            }

            ps = tmgr.prepareStatement(vaTradeCertificateSql);
            int idx = 1;
            ps.setString(idx++, Util.getEmpCode() + "");
            ps.setString(idx++, dobj.getDealerFor() + "");

            int i = ps.executeUpdate();

            if (i > 0) {
                return true;
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return false;
    }

    private boolean insertIntoVMManufacturerMastFromAppl(TransactionManager tmgr, VerifyApproveTradeCertDobj dobj)
            throws VahanException {
        try {
            PreparedStatement ps = null;
            String vaTradeCertificateSql = null;

            vaTradeCertificateSql = "INSERT INTO " + TableList.VM_MAKER_TC + "(maker_cd,state_cd,maker_name,m_add1,m_add2,district,pincode,contact_no,email_id,vch_class,entered_on,entered_by)\n"
                    + " SELECT  applicant_cd, applicant_state_cd, applicant_name,applicant_address1,applicant_address2,applicant_district,applicant_pincode,CAST(applicant_mobile_no AS NUMERIC),applicant_email,applicant_veh_class_list,CURRENT_TIMESTAMP,? "
                    + " FROM " + TableList.VM_APPLICANT_MAST_APPL + " where " + TableList.VM_APPLICANT_MAST_APPL + ".applicant_cd=?";

            ps = tmgr.prepareStatement(vaTradeCertificateSql);
            ps.setString(1, Util.getEmpCode() + "");
            ps.setString(2, dobj.getDealerFor() + "");

            int i = ps.executeUpdate();

            if (i > 0) {
                return true;
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return false;
    }

    private boolean insertIntoVMRetroFitterMastFromAppl(TransactionManager tmgr, VerifyApproveTradeCertDobj dobj)
            throws VahanException {
        try {
            PreparedStatement ps = null;
            String vaTradeCertificateSql = null;

            vaTradeCertificateSql = "INSERT INTO " + TableList.VM_RETROFITTER_TC + "(retrofitter_cd,state_cd,off_cd,retrofitter_name,rf_add1,rf_add2,district,pincode,contact_no,email_id,vch_class,entered_on,entered_by)\n"
                    + " SELECT  applicant_cd, applicant_state_cd, applicant_off_cd, applicant_name,applicant_address1,applicant_address2,applicant_district,applicant_pincode,CAST(applicant_mobile_no AS NUMERIC),applicant_email,applicant_veh_class_list,CURRENT_TIMESTAMP,? "
                    + " FROM " + TableList.VM_APPLICANT_MAST_APPL + " where " + TableList.VM_APPLICANT_MAST_APPL + ".applicant_cd=?";

            ps = tmgr.prepareStatement(vaTradeCertificateSql);
            ps.setString(1, Util.getEmpCode() + "");
            ps.setString(2, dobj.getDealerFor() + "");
            int i = ps.executeUpdate();

            if (i > 0) {
                return true;
            } else {
                throw new VahanException("Error in Inserting Data ");
            }

        } catch (VahanException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }
    //////////////////////////////

    public int updateVaTradeCertificateStatus(TransactionManager tmgr, VerifyApproveTradeCertDobj dobj, String status) throws VahanException {
        PreparedStatement pstmt = null;
        String exceptionSegment = null;
        String result = ":";
        try {
            String sql = "UPDATE " + TableList.VA_TRADE_CERTIFICATE + " SET status = ? ,op_dt = CURRENT_TIMESTAMP WHERE appl_no = ?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, status);  /// P:Pending 
            pstmt.setString(2, dobj.getApplNo());
            return pstmt.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            exceptionSegment = e.toString() + " : " + e.getStackTrace()[0];
            result = "FAILURE " + result + " Unable to update status of online application due to [message{" + exceptionSegment + "}]";
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public static List getVehClassCategoryMappingsInList() throws VahanException {
        TransactionManager tmg = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String query = null;
        List vehClassCategoryList = new ArrayList();
        query = "select vm_vh_class.descr, vm_vch_catg.catg, vm_vch_catg.catg_desc from vahan4.vm_vhclass_catg_map "
                + " left outer join vm_vh_class vm_vh_class on vm_vh_class.vh_class = vm_vhclass_catg_map.vh_class "
                + " left outer join vm_vch_catg vm_vch_catg on vm_vch_catg.catg = vm_vhclass_catg_map.vch_catg"
                + " where length(descr)>0";

        try {
            tmg = new TransactionManager("getVehClassCategoryMappingsInList");
            ps = tmg.prepareStatement(query);
            rs = tmg.fetchDetachedRowSet();
            int srNo = 0;
            VehicleClassCategoryMappingDobj vehicleClassCategoryMappingDobj = null;
            while (rs.next()) {
                srNo = vehClassCategoryList.size() + 1;
                vehicleClassCategoryMappingDobj = new VehicleClassCategoryMappingDobj(srNo, rs.getString("descr"), rs.getString("catg_desc"), rs.getString("catg"));
                vehClassCategoryList.add(vehicleClassCategoryMappingDobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmg != null) {
                try {
                    tmg.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
        return vehClassCategoryList;
    }

    public static List fetchTCList(String dealerCd) throws VahanException {
        List tcListForSelectedDealerAndFuel = new ArrayList<>();
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        IssueTradeCertDobj activeTradeCertToEditDobj = null;
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String sql = " SELECT  * FROM " + TableList.VT_TRADE_CERTIFICATE + " where dealer_cd =? ";

        try {
            tmgr = new TransactionManager("checkTCExist");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, dealerCd);
            rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                activeTradeCertToEditDobj = new IssueTradeCertDobj();
                activeTradeCertToEditDobj.setTradeCertNo(rs.getString("cert_no"));
                activeTradeCertToEditDobj.setValidUpto(rs.getDate("valid_upto"));
                activeTradeCertToEditDobj.setVehCatgFor(rs.getString("vch_catg"));
                if (rs.getDate("valid_upto") != null) {
                    activeTradeCertToEditDobj.setValidUptoAsString(format.format(rs.getDate("valid_upto")));
                }
                activeTradeCertToEditDobj.setNoOfAllowedVehicles(rs.getString("no_of_vch"));

                tcListForSelectedDealerAndFuel.add(activeTradeCertToEditDobj);
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
        }
        return tcListForSelectedDealerAndFuel;
    }

    public static String fetchTemporaryApplNoFromOnlineSchemaVhaTradeCertAppl(String dealerFor, String applNo) throws VahanException {
        TransactionManager tmgr = null;
        String tempApplNo = "";
        PreparedStatement pstmt = null;
        String sql = "select temp_appl_no from " + TableList.VHA_TRADE_CERTIFICATE_APPL + " where applicant_cd = ? and appl_no = ? ";
        try {
            tmgr = new TransactionManager("fetchTemporaryApplNoFromOnlineSchemaVhaTradeCertAppl()");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, dealerFor);
            pstmt.setString(2, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                tempApplNo = rs.getString("temp_appl_no");
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return tempApplNo;
    }

    public static String fetchApplicantNameFromOnlineSchemaVmApplicantMastAppl(String dealerFor, String stateCd) throws VahanException {
        TransactionManager tmgr = null;
        String applicantName = "";
        PreparedStatement pstmt = null;
        String sql = "select applicant_name from " + TableList.VM_APPLICANT_MAST_APPL + " where applicant_cd = ? and applicant_state_cd = ?";
        try {
            tmgr = new TransactionManager("fetchApplicantNameFromOnlineSchemaVmApplicantMastAppl()");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, dealerFor);
            pstmt.setString(2, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                applicantName = rs.getString("applicant_name");
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return applicantName;
    }

    public static int getPurposeCodeFeeForTradeCertificate(VerifyApproveTradeCertDobj dobj) throws VahanException {
        int fee = 0;
        TransactionManager tmgr = null;
        String whereIam = "getPurposeCodeFeeForTradeCertificate";
        PreparedStatement ps = null;
        RowSet rsFee = null;
        try {
            tmgr = new TransactionManager(whereIam);
            String feeSQL = "select * from vm_feemast_catg where state_cd=? and pur_cd=? and vch_catg=?";
            ps = tmgr.prepareStatement(feeSQL);
            ps.setString(1, dobj.getStateCd());
            ps.setInt(2, Integer.valueOf(dobj.getPurCd()));
            ps.setString(3, dobj.getVehCatgFor());//vehClassAppliedFromApplicant

            rsFee = tmgr.fetchDetachedRowSet();
            if (rsFee.next()) {
                fee = rsFee.getInt("fees");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex);
                }
            }
        }
        return fee;
    }

    public void generatingFees(TradeCertificateDobj tradeDobj, List<TradeCertificateDobj.ListOfFeeDetail> dobjList, String condition) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement psmt = null;
        String sql = "select pur_cd,fees,catg_desc from vm_feemast_catg vfc \n"
                + "inner join vm_vch_catg vmVCH on vfc.vch_catg = vmVCH.catg \n"
                + "where state_cd=? and vch_catg=? and ";
        if (!condition.equals("duplicate")) {
            sql = sql + "pur_cd in (" + TableConstants.VM_TRANSACTION_TRADE_CERT_NEW + ",89,250)";
        } else {
            sql = sql + "pur_cd = " + TableConstants.VM_TRANSACTION_TRADE_CERT_DUP;
        }

        try {
            tmgr = new TransactionManager("generatingFees");
            psmt = tmgr.prepareStatement(sql);
            for (TradeCertificateDobj.ListOfFeeDetail dobj : dobjList) {

                psmt.setString(1, tradeDobj.getApplicant_state_cd());
                if (POSValidator.validate(dobj.getVehCatgCode(), "AN")) {
                    psmt.setString(2, dobj.getVehCatgCode());
                }
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    if (tradeDobj.getApplicant_state_cd().equals("CG")) {
                        if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_TRADE_CERT_NEW || rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_TRADE_CERT_DUP) {
                            dobj.setFeeAmount(200);
                        }
                    } else {
                        if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_TRADE_CERT_NEW) {
                            dobj.setFeeAmount(rs.getInt("fees") * dobj.getNo_of_tc_required());
                        } else if (rs.getInt("pur_cd") == 89) {
                            dobj.setTaxAmount(rs.getInt("fees") * dobj.getNo_of_tc_required());
                        } else if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_TRADE_CERT_DUP) {
                            dobj.setFeeAmount(rs.getInt("fees"));
                        } else {
                            dobj.setSurCharge(rs.getInt("fees") * dobj.getNo_of_tc_required());
                        }
                    }
                }

                tradeDobj.setTotalNoOfVehicles(tradeDobj.getTotalNoOfVehicles() + dobj.getNo_of_tc_required());
                tradeDobj.setTotalFeeAmount(tradeDobj.getTotalFeeAmount() + dobj.getFeeAmount());
                tradeDobj.setTotalTaxAmount(tradeDobj.getTotalTaxAmount() + dobj.getTaxAmount());
                tradeDobj.setTotalSurcharge(tradeDobj.getTotalSurcharge() + dobj.getSurCharge());
            }
            tradeDobj.setTotalAmount(tradeDobj.getTotalFeeAmount() + tradeDobj.getTotalTaxAmount() + tradeDobj.getTotalSurcharge());
        } catch (Exception e) {
            LOGGER.error("[ED applicant:" + tradeDobj.getApplicant_name() + "] " + e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error("[ED applicant:" + tradeDobj.getApplicant_name() + "] " + ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

    }

    public String getAlreadyAppliedVehicle(TradeCertificateDobj tradeDobj, String vchCatg) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement psmt = null;
        String sql = "select vCatg.catg_desc from " + TableList.VA_TRADE_CERTIFICATE + " vatc"
                + " inner join vm_vch_catg vCatg on vCatg.catg = vatc.vch_catg"
                + " where vatc.state_cd=? and vatc.off_cd::text=? and vatc.dealer_cd=?"
                + " and vatc.vch_catg in (" + vchCatg + ")";

        try {
            vchCatg = "";
            tmgr = new TransactionManager("generatingFees");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, tradeDobj.getApplicant_state_cd());
            psmt.setString(2, tradeDobj.getApplicant_off_cd());
            psmt.setString(3, tradeDobj.getApplicant_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                vchCatg = vchCatg + rs.getString("catg_desc") + ", ";
            }

        } catch (Exception e) {
            LOGGER.error("[ED applicant:" + tradeDobj.getApplicant_name() + "] " + e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error("[ED applicant:" + tradeDobj.getApplicant_name() + "] " + ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return vchCatg;
    }

    //DMS URL fetch by vahan4 dblink table
    public Map getDMSUrlMap() throws VahanException {
        Map dmsUrlMap = new HashMap();
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        String sql = " Select * from vahan4.tm_dblink_list  where conn_type in('dms_url_ser_tc','dms_url_ser_tcl','dms_url_tc');";
        try {
            tmgr = new TransactionManager("getDMSUrlMap");
            psmt = tmgr.prepareStatement(sql);
            rs = tmgr.fetchDetachedRowSet(); //conn_type, conn_dblink
            while (rs.next()) {
                dmsUrlMap.put(rs.getString("conn_type"), rs.getString("conn_dblink"));
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                tmgr.release();
                psmt.close();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return dmsUrlMap;
    }

    private boolean isNewApplicant(String dealerCd) throws VahanException {
        TransactionManager tmg = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String query = null;
        boolean dealerNotFound = true;
        query = "select dealer_cd from vahan4.vm_dealer_mast where dealer_cd = ?";

        try {
            tmg = new TransactionManager("getVehClassCategoryMappingsInList");
            ps = tmg.prepareStatement(query);
            ps.setString(1, dealerCd);
            rs = tmg.fetchDetachedRowSet();
            while (rs.next()) {
                dealerNotFound = false;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmg != null) {
                try {
                    tmg.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
        return dealerNotFound;
    }

    public static IssueTradeCertDobj getVtTradeCertificateRecord(TransactionManager tmgr, String dealerFor, String vehCatgFor) throws VahanException {

        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        IssueTradeCertDobj issueTradeCertDobj = null;
        PreparedStatement psmt = null;
        RowSet rs = null;

        String sql = null;

        try {
            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();
            if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {    //// using_online_schema_tc work
                sql = " Select * from " + TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " where dealer_cd = ? and vch_catg = ?";
            } else {
                sql = "Select * from " + TableList.VT_TRADE_CERTIFICATE + " where dealer_cd = ? and vch_catg = ?";
            }
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, dealerFor);
            psmt.setString(2, vehCatgFor);
            rs = tmgr.fetchDetachedRowSet_No_release();

            while (rs.next()) {
                issueTradeCertDobj = new IssueTradeCertDobj();
                issueTradeCertDobj.setSrNo(String.valueOf(rs.getInt("sr_no")));
                issueTradeCertDobj.setVehCatgName(getVehCatgDesc(rs.getString("vch_catg")));
                issueTradeCertDobj.setVehCatgFor(rs.getString("vch_catg"));
                issueTradeCertDobj.setFuelTypeFor(rs.getInt("fuel_cd") + "");
                issueTradeCertDobj.setFuelTypeName(getFuelTypeDesc(rs.getInt("fuel_cd")));
                issueTradeCertDobj.setNoOfAllowedVehicles(String.valueOf(rs.getInt("no_of_vch")));
                issueTradeCertDobj.setNoOfVehiclesUsed(String.valueOf(rs.getInt("no_of_vch_used")));
                issueTradeCertDobj.setApplNo(String.valueOf(rs.getString("appl_no")));
                issueTradeCertDobj.setDealerName(getDealerName(rs.getString("dealer_cd"), rs.getString("state_cd"), rs.getInt("off_cd"), rs.getString("applicant_type")));
                issueTradeCertDobj.setDealerFor(rs.getString("dealer_cd"));
                issueTradeCertDobj.setTradeCertNo(rs.getString("cert_no"));
                issueTradeCertDobj.setValidDt(rs.getDate("valid_from"));
                issueTradeCertDobj.setValidUpto(rs.getDate("valid_upto"));
                issueTradeCertDobj.setApplicantCategory(rs.getString("applicant_type"));
                if (rs.getDate("valid_upto") != null) {
                    issueTradeCertDobj.setValidUptoAsString(format.format(rs.getDate("valid_upto")));

                }
                issueTradeCertDobj.setIssueDt(rs.getDate("issue_dt"));
                issueTradeCertDobj.setStateCd(rs.getString("state_cd"));
                issueTradeCertDobj.setOffCd(rs.getInt("off_cd"));

            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return issueTradeCertDobj;
    }

    private String[] generateLoginIdAndInsertIntoTmUserInfo(TransactionManager tmgr, VerifyApproveTradeCertDobj dobj) throws VahanException {
        String[] loginIdArr = null;
        PreparedStatement ps = null;
        Date da = new Date();
        java.sql.Date d = new java.sql.Date(da.getTime());
        try {
            String loginId;
            if (isApplicable(dobj.getStateCd())) {
                loginId = generateDealerLoginIdAsPer_STCD_OFFCD_DLRCD_VEHCLS_TCNO_DLRNAME(dobj);// login id generation pattern type UP
            } else {
                loginId = generateDealerLoginId(dobj);
            }
            String loginPwd = generateDealerLoginPwd(dobj);
            String sql = "INSERT INTO " + TableList.TM_USER_INFO + "(state_cd, off_cd, user_cd, user_name, desig_cd, user_id, user_pwd, mobile_no, email_id, user_catg, status, created_by,created_dt,op_dt)VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getStateCd());
            ps.setInt(2, dobj.getOffCd());
            ps.setLong(3, Long.valueOf(dobj.getDealerMasterDobj().getDealerCode()));
            ps.setString(4, dobj.getDealerMasterDobj().getDealerName());
            ps.setString(5, "DS");
            ps.setString(6, loginId);
            ps.setString(7, ServerUtil.MD5(loginPwd));
            ps.setLong(8, Long.valueOf(dobj.getDealerMasterDobj().getContactNo()));
            ps.setString(9, dobj.getDealerMasterDobj().getEmailId());
            ps.setString(10, "D");
            ps.setString(11, "A");
            ps.setLong(12, Long.valueOf(Util.getEmpCode()));
            ps.setDate(13, d);

            int i = ps.executeUpdate();
            if (i > 0) {
                loginIdArr = new String[2];
                loginIdArr[0] = loginId;
                loginIdArr[1] = loginPwd;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return loginIdArr;
    }

    private String generateDealerLoginIdAsPer_STCD_OFFCD_DLRCD_VEHCLS_TCNO_DLRNAME(VerifyApproveTradeCertDobj dobj) {
        String stateCode = dobj.getDealerMasterDobj().getStateCode().toLowerCase();
        String officeCode;
        String dealer = "dlr";//for dealer
        String vehicleClass = "";
        if (dobj.getDealerMasterDobj().getOffCode() < 10) {
            officeCode = "0" + String.valueOf(dobj.getDealerMasterDobj().getOffCode());;
        } else {
            officeCode = String.valueOf(dobj.getDealerMasterDobj().getOffCode());
        }
        switch (dobj.getVehCatgFor()) {
            case "LMV":
                vehicleClass = "4w";
                break;
            case "2WN":
                vehicleClass = "2w";
                break;
        }
        String tradeCertNo[] = dobj.getTradeCertNo().trim().replace("TC", "/").split("/");
        int remainingChar = 20 - ((stateCode + officeCode + dealer + vehicleClass + tradeCertNo[1]).length());
        String tempDealerName[] = dobj.getDealerMasterDobj().getDealerName().replaceFirst("M/S", "").replaceAll("[^a-zA-Z]", " ").trim().split(" ");
        String dealerName = (tempDealerName[0].length() > --remainingChar) ? (tempDealerName[0].toLowerCase().substring(0, remainingChar)) : (tempDealerName[0].toLowerCase());
        return stateCode + officeCode + dealer + vehicleClass + tradeCertNo[1] + dealerName;
    }

    private String getSaltAlphabet() {
        String saltAlphabet;
        Random randomObj = new Random();
        int floorVal = 65;
        int CeilingVal = 91;
        int randomIndx = randomObj.nextInt(CeilingVal - floorVal) + floorVal;
        saltAlphabet = ((char) randomIndx) + "";
        int[] invalidCharArray = {91, 92, 93, 94, 95, 96};
        List invalidCharArrayList = Arrays.asList(invalidCharArray);
        if (invalidCharArrayList.contains(randomIndx + 6)) {
            saltAlphabet = saltAlphabet.toLowerCase();
        } else {
            saltAlphabet = (((char) (randomIndx + 6)) + "").toLowerCase();
        }
        return saltAlphabet;
    }

    private String generateDealerLoginId(VerifyApproveTradeCertDobj dobj) {
        String first2CharFromName = dobj.getDealerMasterDobj().getDealerName().substring(0, 2);
        String thirdCharFromName = dobj.getDealerMasterDobj().getDealerName().substring(2, 3);
        String first3DigitsFromDealerCode = dobj.getDealerMasterDobj().getDealerCode().substring(0, 3);
        String remainingDealerCode = dobj.getDealerMasterDobj().getDealerCode().substring(3);
        return first2CharFromName + getSaltAlphabet() + thirdCharFromName + first3DigitsFromDealerCode + "@" + remainingDealerCode;
    }

    private String generateDealerLoginPwd(VerifyApproveTradeCertDobj dobj) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String dealerCode = dobj.getDealerMasterDobj().getDealerCode();
        Random rand = new Random();
        int randomIndex = rand.nextInt(dealerCode.length());
        String randomNumber = "";
        for (int i = 0; i < dealerCode.length(); i++) {
            if (i == randomIndex) {
                randomNumber = Integer.valueOf(dealerCode.charAt(i) + "")
                        + "" + getSaltAlphabet()
                        + "" + (Integer.valueOf(dealerCode.charAt(i) + "") + 2)
                        + "" + getSaltAlphabet()
                        + "" + (Integer.valueOf(dealerCode.charAt(i) + "") + 4);
                break;
            }
        }
        String first3CharOfDealerName = dobj.getDealerMasterDobj().getDealerName().substring(0, 3).replaceAll(" ", "Z");
        String modified3Chars = "";
        for (int i = 0; i < 3; i++) {
            if (i == 1) {
                modified3Chars += String.valueOf(first3CharOfDealerName.charAt(i)).toUpperCase();
            } else {
                modified3Chars += String.valueOf(first3CharOfDealerName.charAt(i)).toLowerCase();
            }
        }
        return modified3Chars.substring(0, 2) + "@" + randomNumber.substring(0, 3) + "@" + modified3Chars.substring(2) + "@" + randomNumber.substring(3);
    }

    private boolean insertIntoTmUserPermissions(TransactionManager tmgr, VerifyApproveTradeCertDobj dobj) throws VahanException {
        PreparedStatement ps = null;
        try {
            int i = 1;
            String sql = "INSERT INTO " + TableList.TM_USER_PERMISSIONS + "(\n"
                    + "            state_cd,user_cd, lower_range_no, upper_range_no, class_type, vch_class, \n"
                    + "            pmt_type, pmt_catg, dealer_cd, maker, assigned_office,all_Office_Auth,op_dt)\n"
                    + "    VALUES (?,?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?, ?, current_timestamp)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, dobj.getStateCd());
            ps.setLong(i++, Long.valueOf(dobj.getDealerMasterDobj().getDealerCode()));
            ps.setInt(i++, 1);
            ps.setInt(i++, 9999);
            switch (dobj.getStateCd()) {
                case "UP":
                case "BR":
                    ps.setInt(i++, 2);// for Non-Transport
                    break;
                default:
                    ps.setInt(i++, 3);// for Non-transport & Transport both
            }
            ps.setString(i++, "ANY");
            ps.setString(i++, "ANY");
            ps.setString(i++, "ANY");
            ps.setString(i++, dobj.getDealerMasterDobj().getDealerCode());
            ps.setString(i++, dobj.getDealerMasterDobj().getMaker());
            ps.setString(i++, dobj.getOffCd() + "");
            ps.setBoolean(i++, false);

            int returnVal = ps.executeUpdate();
            if (returnVal > 0) {
                return true;
            } else {
                throw new VahanException("Error occurred while setting User Permissions.");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    private boolean insertIntoTmOffEmpAction(TransactionManager tmgr, VerifyApproveTradeCertDobj dobj) throws VahanException {
        PreparedStatement ps = null;
        Date da = new Date();
        java.sql.Date d = new java.sql.Date(da.getTime());
        int returnVal = 0;
        String sql = "insert into " + TableList.TM_OFF_EMP_ACTION + " values (?,?,?,?,?,?)";

        try {
            ps = tmgr.prepareStatement(sql);
            //int[] actionCodesArr = {21101, 21102};
            int[] actionCodesArr = {66665, 66666};
            for (int actionCd : actionCodesArr) {

                ps.setString(1, dobj.getStateCd());
                ps.setInt(2, dobj.getOffCd());
                ps.setInt(3, Integer.valueOf(dobj.getDealerMasterDobj().getDealerCode()));
                ps.setInt(4, actionCd);
                ps.setInt(5, Integer.valueOf(Util.getEmpCode()));
                ps.setDate(6, d);

                returnVal += ps.executeUpdate();
            }
            if (returnVal == 2) {
                return true;
            } else {
                throw new VahanException("Error occurred while assigning actions.");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public static void sendLoginCredentialsToApplicant(String[] loginIdArr, String contactNumber) throws VahanException {
        try {
            String smsString = "[Apply For Trade Certificate]: Congratulations, Your online application for new trade certificate has been approved. "
                    + "Please use user ID[" + loginIdArr[0] + "] "
                    + "and Password[" + loginIdArr[1] + "] to login to dealer point registration portal as 'Dealer Admin'"
                    + "and kindly assign roles to other dealer staff.";
            System.out.println("smsString : " + smsString);
            ServerUtil.sendSMS(contactNumber, smsString);
        } catch (Exception ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    private boolean isApplicable(String stateCD) {
        switch (stateCD) {
            case "UP":
            case "BR":
                return true;
            default:
                return false;
        }
    }

    public static String getUniqueTcNo(TransactionManager tmgr, String stateCd, int off_cd, String applicant_type) throws VahanException {
        String strSQL = "";
        String tcNo = "";
        Boolean ifTcExists = false;
        String sequence_no_string = "";
        String sequenceNoAfterDigitAppend = "";
        String offCd = String.valueOf(off_cd);
        Integer sequence_no_length = null;
        final String moduleCd = "TC";
        PreparedStatement psmt = null;
        RowSet rs = null;
        TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();

        try {
            if (stateCd == null || off_cd == 0 || CommonUtils.isNullOrBlank(applicant_type)) {
                LOGGER.error("getUniqueTcNo :: State Code OR Office Code OR Applicant Type is null or empty");
                throw new VahanException(TableConstants.SomthingWentWrong);
            }

            /* appending zeros before OFF_CD to make it a combination of two digits */
            offCd = (offCd.length() == 1) ? "0" + offCd : offCd;

            /* updating the sequence no for TC if the record already exists for provided state code and office code 
             ** and generating a new Unique TC No by fetching the updated record for new TC entry*/
            while (true) {
                strSQL = "SELECT * FROM " + TableList.VSM_TC_NO + " WHERE state_cd = ? AND off_cd = ?  AND applicant_type=?";
                psmt = tmgr.prepareStatement(strSQL);
                psmt.setString(1, stateCd);
                psmt.setInt(2, off_cd);
                psmt.setString(3, applicant_type);
                rs = tmgr.fetchDetachedRowSet_No_release();

                if (rs.next()) {
                    if (rs.getInt("sequence_no") >= 0 && rs.getInt("sequence_no") < 999) {
                        strSQL = "UPDATE " + TableList.VSM_TC_NO + " SET SEQUENCE_NO = SEQUENCE_NO + 1 WHERE state_cd = ? AND off_cd = ? AND applicant_type=?";
                        psmt = tmgr.prepareStatement(strSQL);
                        psmt.setString(1, stateCd);
                        psmt.setInt(2, off_cd);
                        psmt.setString(3, applicant_type);
                        psmt.executeUpdate();
                        rs.close();

                        strSQL = "SELECT MODULE_PREFIX, SEQUENCE_NO FROM " + TableList.VSM_TC_NO + " WHERE state_cd = ? AND off_cd = ? AND applicant_type=?";
                        psmt = tmgr.prepareStatement(strSQL);
                        psmt.setString(1, stateCd);
                        psmt.setInt(2, off_cd);
                        psmt.setString(3, applicant_type);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {

                            /* appending zeros before sequence_no to make it a combination of three digits */
                            sequence_no_string = String.valueOf(rs.getInt("sequence_no"));
                            sequence_no_length = sequence_no_string.length();
                            switch (sequence_no_length) {
                                case 1:
                                    sequenceNoAfterDigitAppend = "00" + sequence_no_string;
                                    break;
                                case 2:
                                    sequenceNoAfterDigitAppend = "0" + sequence_no_string;
                                    break;
                                default:
                                    sequenceNoAfterDigitAppend = sequence_no_string;
                                    break;
                            }
                            tcNo = stateCd + offCd + rs.getString("MODULE_PREFIX").trim() + sequenceNoAfterDigitAppend;
                        }
                        ifTcExists = existingTcChecker(tmgr, stateCd, off_cd, tcNo);
                        if (ifTcExists) {
                            continue;
                        } else {
                            break;
                        }

                    } else {
                        LOGGER.error("getUniqueTcNo :: Maximum Trade No. Reached");
                        throw new VahanException(TableConstants.SomthingWentWrong + "[Maximum Trade No. Reached]");
                    }
                } else {
                    if (tmConfigurationDobj.getTmTradeCertConfigDobj().isTcInitiationInRtoByCode()) {
                        /* inserting a new record if the record does not exist for provided state code and office code 
                         ** and after insertion, generating a new Unique TC No by calling the getUniqueTcNO method*/ /*comment when new rto to be initialized manually*/ //           

                        strSQL = "INSERT INTO " + TableList.VSM_TC_NO + "(\n"
                                + " state_cd, off_cd, tc_starting_date, module_prefix, \n"
                                + " sequence_no, applicant_type, entered_by) \n"
                                + " VALUES(?, ?,CURRENT_DATE, ?, ?, ?, ?)";

                        psmt = tmgr.prepareStatement(strSQL);
                        psmt.setString(1, stateCd);
                        psmt.setInt(2, off_cd);
                        psmt.setString(3, moduleCd);
                        psmt.setInt(4, 0);
                        psmt.setString(5, applicant_type);
                        psmt.setString(6, Util.getEmpCode());
                        int value = psmt.executeUpdate();
                        if (value == 0) {
                            tcNo = null;
                        } else {
                            tcNo = getUniqueTcNo(tmgr, stateCd, off_cd, applicant_type);
                        }
                        break;
                    } else {
                        throw new VahanException(TableConstants.TC_NO_GENERATION_ERROR_FOR_NEW_RTO);
                    }
                }
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + "  " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
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
        return tcNo;
    }

    public static Boolean existingTcChecker(TransactionManager tmgr, String stateCd, int off_cd, String tcNo) throws VahanException {

        String strSQL = "";
        PreparedStatement psmt = null;
        RowSet rs = null;
        Boolean ifTcExists = false;
        int i = 1;

        try {

            strSQL = "select * from ((select new_assigned_tc_no from " + TableList.VH_REASSIGN_TC_ONLINE_SCHEMA_TC
                    + " where state_cd = ? and off_cd = ? and new_assigned_tc_no = ?)"
                    + " vh_r_tc full outer join "
                    + "(select cert_no from " + TableList.VT_TRADE_CERTIFICATE
                    + " where state_cd = ? and off_cd = ? and cert_no = ?)"
                    + " vt_tc on vh_r_tc.new_assigned_tc_no = vt_tc.cert_no full outer join "
                    + "(select cert_no from " + TableList.VH_TRADE_CERTIFICATE
                    + " where state_cd = ? and off_cd = ? and cert_no = ?) vh_tc on vh_r_tc.new_assigned_tc_no = vh_tc.cert_no)";

            psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(i++, stateCd);
            psmt.setInt(i++, off_cd);
            psmt.setString(i++, tcNo);
            psmt.setString(i++, stateCd);
            psmt.setInt(i++, off_cd);
            psmt.setString(i++, tcNo);
            psmt.setString(i++, stateCd);
            psmt.setInt(i++, off_cd);
            psmt.setString(i++, tcNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                ifTcExists = true;
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "TC existence could not be checked" + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);

        } finally {
            try {
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
        return ifTcExists;
    }

    public static String getApplicantTypeDescriptionAsPerCode(String applicantTypeCd) throws VahanException {
        PreparedStatement psmt = null;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql = " Select descr from " + TableList.VM_TC_APPLICANT_TYPE + " where code = ?";

        try {
            tmgr = new TransactionManager("getApplicantTypeDescriptionAsPerCode");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applicantTypeCd);
            rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                return rs.getString("descr");
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return applicantTypeCd;
    }

    public static Integer getNumberOfDocumentsRequiredToUpload(String stateCd, String tradeCertApplType, String applicantType) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        RowSet rs = null;
        Integer noDoc = 0;
        applicantType = resetNewRenewalTradeCertWithCorrectValues(applicantType);
        String sql = "select no_docs from " + TableList.TM_TRADE_CERT_DOC_MAST + " where state_code = ? and trade_cert_appl_type = ? and applicant_type = ?";
        try {
            tmgr = new TransactionManager("getNumberOfDocumentsRequiredToUpload");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, stateCd);
            pstmt.setString(2, tradeCertApplType);
            pstmt.setString(3, applicantType);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                noDoc = rs.getInt("no_docs");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return noDoc;
    }

    private static String resetNewRenewalTradeCertWithCorrectValues(String applicationType) {
        switch (applicationType) {
            case "New_Trade_Certificate": {
                applicationType = "N";
                break;
            }
            case "Renew_Trade_Certificate": {
                applicationType = "R";
                break;
            }
            case "Duplicate_Trade_Certificate": {
                applicationType = "D";
                break;
            }
        }
        return applicationType;
    }

    public Map getMapOfInspectionOfficiers(String stateCd, int offCd) throws VahanException {
        Map mapOfInspectionOfficiers = new HashMap();
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        String sql = " select user_name,desig_Cd,user_id,user_cd from " + TableList.TM_USER_INFO + " where desig_Cd in ('RI-TECH','ARTO') and user_catg in ('L') and state_cd = ?  and off_cd = ?";
        try {
            tmgr = new TransactionManager("getMapOfInspectionOfficiers");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, stateCd);
            psmt.setInt(2, offCd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (!CommonUtils.isNullOrBlank(rs.getString("user_id"))) {
                    mapOfInspectionOfficiers.put(rs.getInt("user_cd"), rs.getString("user_name") + "[" + rs.getString("desig_Cd") + "] [ ID : " + rs.getString("user_id") + " ]");
                } else {
                    mapOfInspectionOfficiers.put(rs.getInt("user_cd"), rs.getString("user_name") + "[" + rs.getString("desig_Cd") + "]");
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
                if (psmt != null) {
                    psmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return mapOfInspectionOfficiers;
    }

    public String attachInspectionDetailsWithApplication(VerifyApproveTradeCertDobj verifyApproveTradeCertDobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        try {
            String sqlQuery = " UPDATE " + TableList.VA_TRADE_CERTIFICATE + " set inspec_by = ?, inspec_on = ?, inspec_remark = ? \n"
                    + " WHERE state_cd = ? and off_cd = ? and appl_no = ?";
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setInt(1, Integer.valueOf(verifyApproveTradeCertDobj.getInspectionBy()));
            ps.setDate(2, new java.sql.Date(verifyApproveTradeCertDobj.getInspectionOn().getTime()));
            ps.setString(3, verifyApproveTradeCertDobj.getInspectionRemark());
            ps.setString(4, verifyApproveTradeCertDobj.getStateCd());
            ps.setInt(5, verifyApproveTradeCertDobj.getOffCd());
            ps.setString(6, verifyApproveTradeCertDobj.getApplNo());
            int i = ps.executeUpdate();
            return (i > 0) ? "SUCCESS" : "FAILURE";
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public static void fetchInspectionDetailsFromVaTradeCert(TransactionManager tmgr, VerifyApproveTradeCertDobj verifyApproveTradeCertDobj) throws VahanException {
        PreparedStatement psmt = null;
        RowSet rs = null;

        String sql = "Select * from " + TableList.VA_TRADE_CERTIFICATE + " where appl_no = ?";

        try {
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, verifyApproveTradeCertDobj.getApplNo());
            rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                verifyApproveTradeCertDobj.setInspectionBy(rs.getInt("inspec_by") + "");
                verifyApproveTradeCertDobj.setInspectionOn(rs.getDate("inspec_on"));
                verifyApproveTradeCertDobj.setInspectionRemark(rs.getString("inspec_remark"));
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public static boolean isRequiredDocsUploaded(String tempApplNo) throws VahanException {
        try {
            List vtDocumentModelList = DmsDocCheckUtils.getList(VerifyApproveTradeCertBean.dmsTradeCertUploadedDocsUrlWithinServer, tempApplNo);
            if (!vtDocumentModelList.isEmpty()) {
                return true;
            } else {
                throw new VahanException("Trade Certificate DMS error: No documents uploaded");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in checking if required documents uploaded due to " + ex.getMessage());
        }
    }

    public String fileMove(Status_dobj status) throws VahanException {
        TransactionManager tmgr = null;
        String result = ": Application No:";
        try {
            tmgr = new TransactionManager("fileMove");
            status.setEntry_status(TableConstants.STATUS_APPROVED);
            ServerUtil.updateApprovedStatus(tmgr, status);
            status = ServerUtil.webServiceForNextStage(status, tmgr);
            ServerUtil.fileFlow(tmgr, status);
            tmgr.commit();
            result = "SUCCESS " + result + status.getAppl_no();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            result = "FAILURE : ";
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

    public static boolean isTradeCertificateProcessNotCompleted(String applNo, int purCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement psmt = null;
        RowSet rs = null;
        String sql = "Select * from " + TableList.VA_STATUS + " where state_cd = ? and off_cd = ? and appl_no = ? and pur_cd = ? and action_cd = ?";

        try {
            tmgr = new TransactionManager("isTradeCertificateProcessCompleted");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, Util.getUserStateCode());
            psmt.setInt(2, Util.getUserOffCode());
            psmt.setString(3, applNo);
            psmt.setInt(4, purCd);
            psmt.setInt(5, TableConstants.TM_ROLE_TRADE_CERT_PRINT);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                return ("N".equals(rs.getString("status"))) ? true : false;
            } else {
                throw new VahanException("Record not found in status history for trade certificate print action.");
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (psmt != null) {
                    psmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public static boolean isApplicationReverted(String applNo, String stateCd, int offCd) throws VahanException {
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        String sql = " select current_status from " + TableList.VA_TRADE_CERTIFICATE_AUDIT_TRAIL + " where appl_no = ? and state_cd = ? ";
        try {
            tmgr = new TransactionManager("isApplicationReverted");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            psmt.setString(2, stateCd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                return (rs.getString("current_status").equals("M")) ? true : false;
            } else {
                throw new VahanException("No record found in trade certificate audit trail table for the given application.");
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (psmt != null) {
                    psmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

    }
}
