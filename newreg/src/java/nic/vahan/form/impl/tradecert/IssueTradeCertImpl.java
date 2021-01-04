/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.tradecert;

import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.common.jsf.utils.validators.POSValidator;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.user_mgmt.dobj.DealerMasterDobj;
import nic.vahan.form.dobj.tradecert.IssueTradeCertDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.tradecert.VerifyApproveTradeCertDobj;
import nic.vahan.form.impl.Util;
import static nic.vahan.form.impl.tradecert.ApplicationTradeCertImpl.getDealerName;
import static nic.vahan.form.impl.tradecert.ApplicationTradeCertImpl.getFuelTypeDesc;
import static nic.vahan.form.impl.tradecert.ApplicationTradeCertImpl.getUniqueTcNo;
import static nic.vahan.form.impl.tradecert.ApplicationTradeCertImpl.getVehCatgDesc;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC081
 */
public class IssueTradeCertImpl {

    private static final Logger LOGGER = Logger.getLogger(IssueTradeCertImpl.class);

    public static void getAllSrNoFromVtTradeCertificateForSelectedApplicant(String selectedApplicantCd, Map applicationSectionsMap, boolean addressToBeFull) throws VahanException {
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        String sql = "";



        try {
            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();
            if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {  //// using_online_schema_tc work
                sql = " Select * from " + TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " where dealer_cd = ?";
            } else {
                sql = "Select * from " + TableList.VT_TRADE_CERTIFICATE + " where dealer_cd = ?";
            }
            tmgr = new TransactionManager("getAllSrNoFromVtTradeCertificateForSelectedApplicant");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, selectedApplicantCd);
            rs = tmgr.fetchDetachedRowSet();
            VerifyApproveTradeCertDobj verifyApproveTradeCertDobj = null;
            int i = 1;
            while (rs.next()) {
                verifyApproveTradeCertDobj = new VerifyApproveTradeCertDobj();
                verifyApproveTradeCertDobj.setStateCd(rs.getString("state_cd"));
                verifyApproveTradeCertDobj.setOffCd(rs.getInt("off_cd"));
                verifyApproveTradeCertDobj.setSrNo(String.valueOf(rs.getInt("sr_no")));
                verifyApproveTradeCertDobj.setVehCatgName(getVehClassDesc(rs.getString("vch_catg")));
                verifyApproveTradeCertDobj.setVehCatgFor(rs.getString("vch_catg"));
                verifyApproveTradeCertDobj.setTradeCertNo(rs.getString("cert_no"));
                verifyApproveTradeCertDobj.setOriginalTradeCertNo(rs.getString("cert_no"));
                verifyApproveTradeCertDobj.setValidFromDate(rs.getDate("valid_from"));
                verifyApproveTradeCertDobj.setValidUptoDate(rs.getDate("valid_upto"));
                verifyApproveTradeCertDobj.setValidUpto(rs.getDate("valid_upto"));
                verifyApproveTradeCertDobj.setValidUptoAsString(format.format(rs.getDate("valid_upto")));
                verifyApproveTradeCertDobj.setNoOfAllowedVehicles(String.valueOf(rs.getInt("no_of_vch")));
                verifyApproveTradeCertDobj.setNoOfVehiclesUsed(String.valueOf(rs.getInt("no_of_vch_used")));
                verifyApproveTradeCertDobj.setApplNo(String.valueOf(rs.getString("appl_no")));
                verifyApproveTradeCertDobj.setDealerName(getDealerName(rs.getString("dealer_cd"), rs.getString("state_cd"), rs.getInt("off_cd"), rs.getString("applicant_type")));
                verifyApproveTradeCertDobj.setDealerFor(rs.getString("dealer_cd"));
                verifyApproveTradeCertDobj.setApplicantType(rs.getString("applicant_type"));
                verifyApproveTradeCertDobj.setIssueDate(rs.getDate("issue_dt"));
                verifyApproveTradeCertDobj.setTabIndex(i + "");
                ///////// Fetching applicant details from onlineschema.vm_applicant_mast_appl
                verifyApproveTradeCertDobj.setDealerMasterDobj(new DealerMasterDobj());
                verifyApproveTradeCertDobj.getDealerMasterDobj().setDealerCode(rs.getString("dealer_cd"));
                if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {    //// using_online_schema_tc work
                    ApplicationTradeCertImpl.fetchApplicantDetailsFromMasterForOR(tmgr, verifyApproveTradeCertDobj, addressToBeFull);
                } else {
                    ApplicationTradeCertImpl.fetchApplicantDetailsFromMaster(tmgr, verifyApproveTradeCertDobj);
                }
                i++;
                applicationSectionsMap.put(verifyApproveTradeCertDobj.getTabIndex(), verifyApproveTradeCertDobj);
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
                if (rs.next()) {
                    sql = " Select appl_no from vt_trade_certificate where appl_no = '" + rsApplNo.getString("appl_no") + "'";

                    psmt = tmgr.prepareStatement(sql);

                    rs = psmt.executeQuery();
                    if (!rs.next()) {
                        applicationMap.put(rsApplNo.getString("appl_no"), rsApplNo.getString("appl_no"));
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
                if (psmt != null) {
                    psmt.close();
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
        String sql = " Select dealer_cd,dealer_name  from " + TableList.VM_DEALER_MAST + " where state_cd = ? and off_cd = ?";
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
                if (psmt != null) {
                    psmt.close();
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
            }
            if (filtered) {
                sql = " Select vch_catg from va_trade_certificate where dealer_cd = " + dealerCd + " and vh_class = " + Integer.valueOf(vehClass);

                psmt = tmgr.prepareStatement(sql);

                rs = psmt.executeQuery();
                while (rs.next()) {
                    vehCatgMap.remove(rs.getString("vch_catg"));
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
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return vehClassMap;
    }

    public IssueTradeCertDobj getVtTradeCertificateRecord(TransactionManager tmgr, String dealerFor, String vehCatgFor, String fuelCd) throws VahanException {

        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        IssueTradeCertDobj issueTradeCertDobj = null;
        PreparedStatement psmt = null;
        RowSet rs = null;

        String sql;
        if (!CommonUtils.isNullOrBlank(fuelCd) && !fuelCd.trim().equals("0")) {
            sql = "Select * from vt_trade_certificate where dealer_cd = ? and vch_catg = ? and fuel_cd = ?";
        } else {
            sql = "Select * from vt_trade_certificate where  dealer_cd=? and vch_catg=?";
        }

        try {
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, dealerFor);
            psmt.setString(2, vehCatgFor);
            if (!CommonUtils.isNullOrBlank(fuelCd) && !fuelCd.trim().equals("0")) {
                psmt.setInt(3, Integer.valueOf(fuelCd));
            }
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
                issueTradeCertDobj.setDealerName(getDealerName(rs.getString("dealer_cd"), rs.getString("state_cd"), rs.getInt("off_cd")));
                issueTradeCertDobj.setDealerFor(rs.getString("dealer_cd"));
                issueTradeCertDobj.setTradeCertNo(rs.getString("cert_no"));
                issueTradeCertDobj.setValidDt(rs.getDate("valid_from"));
                issueTradeCertDobj.setValidUpto(rs.getDate("valid_upto"));
                if (rs.getDate("valid_upto") != null) {
                    issueTradeCertDobj.setValidUptoAsString(format.format(rs.getDate("valid_upto")));
                }
                issueTradeCertDobj.setIssueDt(rs.getDate("issue_dt"));
                issueTradeCertDobj.setStateCd(rs.getString("state_cd"));
                issueTradeCertDobj.setOffCd(rs.getInt("off_cd"));
                issueTradeCertDobj.setApplicantCategory(rs.getString("applicant_type"));
                issueTradeCertDobj.setNoOfVehiclePrint(rs.getInt("no_vch_print") + "");

                if (!CommonUtils.isNullOrBlank(rs.getString("selected_duplicate_certificate"))) {
                    issueTradeCertDobj.setSelectedDuplicateCertificates(Utility.convertStringToList(rs.getString("selected_duplicate_certificate")));
                    issueTradeCertDobj.setNewRenewalTradeCert(TableConstants.TRADE_CERT_DUPLICATE_CONSTANT_VAL);
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
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return issueTradeCertDobj;
    }

    public String insertIntoVtTradeCertificate(PreparedStatement psInsertIntoVtTradeCert, String tradeCertificateNo, IssueTradeCertDobj dobj) throws VahanException {

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
            psInsertIntoVtTradeCert.setInt(12, Integer.valueOf(dobj.getFuelTypeFor()));
            psInsertIntoVtTradeCert.setString(13, dobj.getApplicantCategory());
            psInsertIntoVtTradeCert.addBatch();
            result = "SUCCESS";
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            result = "FAILURE" + exceptionSegment;
            throw new VahanException(TableConstants.SomthingWentWrong);

        }
        return result;
    }

    public void getAllSrNoForSelectedApplication(String applNo, Map applicationSections) throws VahanException {

        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;

        String sql = "Select * from va_trade_certificate where appl_no = ?";

        try {
            tmgr = new TransactionManager("getAllSrNoForSelectedApplication");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            rs = tmgr.fetchDetachedRowSet();

            IssueTradeCertDobj issueTradeCertDobj = null;
            while (rs.next()) {
                issueTradeCertDobj = new IssueTradeCertDobj();
                issueTradeCertDobj.setSrNo(String.valueOf(rs.getInt("sr_no")));
                issueTradeCertDobj.setVehCatgName(ApplicationTradeCertImpl.getVehCatgDesc(rs.getString("vch_catg")));
                issueTradeCertDobj.setVehCatgFor(rs.getString("vch_catg"));
                issueTradeCertDobj.setFuelTypeFor(rs.getInt("fuel_cd") + "");
                issueTradeCertDobj.setFuelTypeName(getFuelTypeDesc(rs.getInt("fuel_cd")));
                issueTradeCertDobj.setNoOfAllowedVehicles(String.valueOf(rs.getInt("no_of_vch")));
                issueTradeCertDobj.setApplNo(String.valueOf(rs.getString("appl_no")));
                issueTradeCertDobj.setDealerName(getDealerName(rs.getString("dealer_cd"), rs.getString("state_cd"), rs.getInt("off_cd"), rs.getString("applicant_type")));
                issueTradeCertDobj.setApplicantCategory(rs.getString("applicant_type"));
                issueTradeCertDobj.setNewRenewalTradeCert(rs.getString("trade_cert_appl_type"));
                if (!CommonUtils.isNullOrBlank(issueTradeCertDobj.getNewRenewalTradeCert())
                        && TableConstants.TRADE_CERT_DUPLICATE_CONSTANT_VAL.equals(issueTradeCertDobj.getNewRenewalTradeCert())) {
                    issueTradeCertDobj.setSelectedDuplicateCertificates(Utility.convertStringToList(rs.getString("selected_duplicate_certificate")));
                }
                issueTradeCertDobj.setDealerFor(rs.getString("dealer_cd"));
                if (!CommonUtils.isNullOrBlank(rs.getString("status"))) {
                    issueTradeCertDobj.setStatus(rs.getString("status"));
                }
                fillTCNoAndNoOfVchUsed(issueTradeCertDobj);
                issueTradeCertDobj.setRtoSideAppl(rs.getBoolean("rto_side_appl"));

                issueTradeCertDobj.setStockTransferReq(rs.getBoolean("stock_transfer_req"));

                applicationSections.put(issueTradeCertDobj.getSrNo(), issueTradeCertDobj);
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

    public void getAllSrNoForSelectedApplicationFromVt(String applNo, List<IssueTradeCertDobj> list) throws VahanException {

        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;

        String sql = "Select sr_no,vch_catg,no_of_vch,appl_no,dealer_cd,state_cd,off_cd,fuel_cd from vt_trade_certificate where appl_no =?";

        try {
            tmgr = new TransactionManager("getAllSrNoForSelectedApplication");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            rs = psmt.executeQuery();

            IssueTradeCertDobj issueTradeCertDobj = null;
            while (rs.next()) {
                issueTradeCertDobj = new IssueTradeCertDobj();
                issueTradeCertDobj.setSrNo(String.valueOf(rs.getInt("sr_no")));
                issueTradeCertDobj.setVehCatgName(getVehCatgDesc(rs.getString("vch_catg")));
                issueTradeCertDobj.setVehCatgFor(rs.getString("vch_catg"));
                issueTradeCertDobj.setFuelTypeFor(rs.getInt("vch_catg") + "");
                issueTradeCertDobj.setFuelTypeName(getFuelTypeDesc(rs.getInt("vch_catg")));
                issueTradeCertDobj.setNoOfAllowedVehicles(String.valueOf(rs.getInt("no_of_vch")));
                issueTradeCertDobj.setApplNo(String.valueOf(rs.getString("appl_no")));
                issueTradeCertDobj.setDealerName(getDealerName(rs.getString("dealer_cd"), rs.getString("state_cd"), rs.getInt("off_cd")));
                issueTradeCertDobj.setDealerFor(rs.getString("dealer_cd"));
                fillTCNoAndNoOfVchUsed(issueTradeCertDobj);

                list.add(issueTradeCertDobj);
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

    public String getTradeCertNo(String vehCatgFor, String dealerFor, String fuelCd) throws VahanException {
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        String certNo = "";
        String sql = "";

        try {
            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();
            if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {   //// using_online_schema_tc work
                sql = "Select cert_no from " + TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " where  dealer_cd=? and vch_catg=?";
            } else {
                if (!CommonUtils.isNullOrBlank(fuelCd) && !fuelCd.trim().equals("0")) {
                    sql = "Select cert_no from vt_trade_certificate where  dealer_cd=? and vch_catg=? and fuel_cd=?";
                } else {
                    sql = "Select cert_no from vt_trade_certificate where  dealer_cd=? and vch_catg=?";
                }
            }
            tmgr = new TransactionManager("getTradeCertNo");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, dealerFor);
            psmt.setString(2, vehCatgFor);
            if (!CommonUtils.isNullOrBlank(fuelCd) && !fuelCd.trim().equals("0")) {
                psmt.setInt(3, Integer.valueOf(fuelCd));
            }
            rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                certNo = rs.getString("cert_no");
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
        return certNo;
    }

    public void fillTCNoAndNoOfVchUsed(IssueTradeCertDobj issueTradeCertDobj) throws VahanException {
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        String sql = "";

        try {
            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();
            if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {   //// using_online_schema_tc work
                sql = "Select no_of_vch_used,cert_no from " + TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " where  dealer_cd=? and vch_catg=?";
            } else {
                if (!CommonUtils.isNullOrBlank(issueTradeCertDobj.getFuelTypeFor()) && !issueTradeCertDobj.getFuelTypeFor().trim().equals("0")) {
                    sql = "Select no_of_vch_used,cert_no from vt_trade_certificate where  dealer_cd=? and vch_catg=? and fuel_cd=?";
                } else {
                    sql = "Select no_of_vch_used,cert_no from vt_trade_certificate where  dealer_cd=? and vch_catg=?";
                }
            }
            tmgr = new TransactionManager("fillTCNoAndNoOfVchUsed");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, issueTradeCertDobj.getDealerFor());
            psmt.setString(2, issueTradeCertDobj.getVehCatgFor());
            if (!CommonUtils.isNullOrBlank(issueTradeCertDobj.getFuelTypeFor()) && !issueTradeCertDobj.getFuelTypeFor().trim().equals("0")) {
                psmt.setInt(3, Integer.valueOf(issueTradeCertDobj.getFuelTypeFor()));
            }
            rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                issueTradeCertDobj.setTradeCertNo(rs.getString("cert_no"));
                issueTradeCertDobj.setNoOfVehiclesUsed(String.valueOf(rs.getInt("no_of_vch_used")));
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

    public static String getVehClassDesc(String vehClassCd) throws VahanException {

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
                if (psmt != null) {
                    psmt.close();
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
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return dealerCd;
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
                if (psmt != null) {
                    psmt.close();
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
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return feeDetailsArr;
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
                if (psmt != null) {
                    psmt.close();
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
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return bankMap;
    }

    public Date getApplicationSubmissionDate(String applNo) throws VahanException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        Date applicationSubmissionDate = null;
        String sql = " Select op_dt  from va_trade_certificate where appl_no ='" + applNo + "'";

        try {
            tmgr = new TransactionManager("getApplicationSubmissionDate");
            psmt = tmgr.prepareStatement(sql);

            rs = psmt.executeQuery();

            if (rs.next()) {
                applicationSubmissionDate = new Date(rs.getTimestamp("op_dt").getTime());
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
        return applicationSubmissionDate;
    }

    public void getAllExpiredSrNoForSelectedDealerFromVtTradeCert(String selectedDealer, String certNo, String vchCatgList, String fuelCDList, Map mapSectionsSrNoOfSelectedDealer) throws VahanException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String sql = null;

        try {
            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();
            if (certNo != null) {   /// DUPLICATE
                sql = "Select * from vt_trade_certificate where dealer_cd = ? and cert_no = ? ";
            } else {   //// Renew
                sql = "Select * from vt_trade_certificate where dealer_cd = ? ";
                if (vchCatgList != null) {
                    sql += " and vch_catg in " + vchCatgList;
                    if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {  // GUJRAT FUEL-TYPE WORK
                        sql += " and fuel_cd in " + fuelCDList;
                    }
                }
            }
            tmgr = new TransactionManager("getAllExpiredSrNoForSelectedDealerFromVtTradeCert");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, selectedDealer);
            if (certNo != null) {   /// DUPLICATE
                psmt.setString(2, certNo);
            }
//            else{   ///// Renew
//                if(vchCatgList!=null){
//                    psmt.setString(2, vchCatgList);
//                    if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("GJ")) {  // GUJRAT FUEL-TYPE WORK
//                       psmt.setString(3,fuelCDList);
//                    }
//                }
//            }

            rs = tmgr.fetchDetachedRowSet();

            IssueTradeCertDobj issueTradeCertDobj = null;
            while (rs.next()) {
                issueTradeCertDobj = new IssueTradeCertDobj();

                issueTradeCertDobj.setStateCd(rs.getString("state_cd"));
                issueTradeCertDobj.setOffCd(rs.getInt("off_cd"));
                issueTradeCertDobj.setApplNo(rs.getString("appl_no"));
                issueTradeCertDobj.setDealerFor(rs.getString("dealer_cd"));
                issueTradeCertDobj.setSrNo(String.valueOf(rs.getInt("sr_no")));
                issueTradeCertDobj.setVehCatgName(getVehCatgDesc(rs.getString("vch_catg")));
                issueTradeCertDobj.setVehCatgFor(rs.getString("vch_catg"));
                issueTradeCertDobj.setFuelTypeFor(rs.getInt("fuel_cd") + "");
                issueTradeCertDobj.setFuelTypeName(getFuelTypeDesc(rs.getInt("fuel_cd")));
                issueTradeCertDobj.setNoOfAllowedVehicles(String.valueOf(rs.getInt("no_of_vch")));
                issueTradeCertDobj.setDealerName(getDealerName(rs.getString("dealer_cd"), rs.getString("state_cd"), rs.getInt("off_cd")));
                issueTradeCertDobj.setNoOfVehiclesUsed(String.valueOf(rs.getInt("no_of_vch_used")));
                issueTradeCertDobj.setTradeCertNo(rs.getString("cert_no"));
                issueTradeCertDobj.setValidDt(rs.getDate("valid_from"));
                issueTradeCertDobj.setValidUpto(rs.getDate("valid_upto"));
                if (rs.getDate("valid_upto") != null) {
                    issueTradeCertDobj.setValidUptoAsString(DateUtil.formatDate(rs.getDate("valid_upto"), "dd-MMM-yyyy"));
                    String dateAsString = rs.getDate("valid_upto").toString();
                    issueTradeCertDobj.setValidUptoAsString(dateAsString.split("-")[2]
                            + "-" + issueTradeCertDobj.getValidUptoAsString().split("-")[1]
                            + "-" + issueTradeCertDobj.getValidUptoAsString().split("-")[2]);
                }
                issueTradeCertDobj.setIssueDt(rs.getDate("issue_dt"));

                mapSectionsSrNoOfSelectedDealer.put(issueTradeCertDobj.getApplNo() + ":" + issueTradeCertDobj.getSrNo(), issueTradeCertDobj);
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

    public void getTradeCertDetailsForTradeCertNoFromVtTradeCert(String tradeCertNo, Map mapSectionsSrNoOfSelectedDealer) throws VahanException {
        String vtTradeCertificateTableName = "";
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");

        try {
            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();
            if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {   //// using_online_schema_tc work
                vtTradeCertificateTableName = TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC;
            } else {
                vtTradeCertificateTableName = TableList.VT_TRADE_CERTIFICATE;
            }

            String sql = "Select * from " + vtTradeCertificateTableName + " where cert_no= ?";

            tmgr = new TransactionManager("getTradeCertDetailsForTradeCertNoFromVtTradeCert");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, tradeCertNo);
            rs = tmgr.fetchDetachedRowSet();

            IssueTradeCertDobj issueTradeCertDobj = null;
            int srNo = 1;
            while (rs.next()) {
                issueTradeCertDobj = new IssueTradeCertDobj();

                issueTradeCertDobj.setStateCd(rs.getString("state_cd"));
                issueTradeCertDobj.setOffCd(rs.getInt("off_cd"));
                issueTradeCertDobj.setApplNo(rs.getString("appl_no"));
                issueTradeCertDobj.setDealerFor(rs.getString("dealer_cd"));
                issueTradeCertDobj.setSrNo((srNo++) + "");

                if (tmConfigDobj.getTmTradeCertConfigDobj().isVehClassToBeRendered()) {    /// render_veh_class work
                    issueTradeCertDobj.setVehCatgName(getVehClassDesc(rs.getString("vch_catg")));
                } else {
                    issueTradeCertDobj.setVehCatgName(ApplicationTradeCertImpl.getVehCatgDesc(rs.getString("vch_catg")));
                }

                issueTradeCertDobj.setVehCatgFor(rs.getString("vch_catg"));
                issueTradeCertDobj.setFuelTypeFor(rs.getInt("fuel_cd") + "");
                issueTradeCertDobj.setFuelTypeName(ApplicationTradeCertImpl.getFuelTypeDesc(rs.getInt("fuel_cd")));
                issueTradeCertDobj.setNoOfAllowedVehicles(String.valueOf(rs.getInt("no_of_vch")));
                issueTradeCertDobj.setDealerName(getDealerName(rs.getString("dealer_cd"), rs.getString("state_cd"), rs.getInt("off_cd")));
                issueTradeCertDobj.setNoOfVehiclesUsed(String.valueOf(rs.getInt("no_of_vch_used")));
                issueTradeCertDobj.setNoOfVehiclePrint(rs.getInt("no_vch_print") + "");
                issueTradeCertDobj.setTradeCertNo(rs.getString("cert_no"));
                issueTradeCertDobj.setValidDt(rs.getDate("valid_from"));
                issueTradeCertDobj.setValidUpto(rs.getDate("valid_upto"));
                if (rs.getDate("valid_upto") != null) {
                    issueTradeCertDobj.setValidUptoAsString(format.format(rs.getDate("valid_upto")));
                }
                issueTradeCertDobj.setIssueDt(rs.getDate("issue_dt"));

                if (rs.getInt("no_of_vch") == 1) {
                    issueTradeCertDobj.setFlagNotToPrintNoOfVehicleRange(true);
                }

                mapSectionsSrNoOfSelectedDealer.put(issueTradeCertDobj.getApplNo() + ":" + issueTradeCertDobj.getSrNo(), issueTradeCertDobj);
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

    public void getValidityDetailsOnTradeCertNoForDuplicateCase(IssueTradeCertDobj issueTradeCertDobj) throws VahanException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;

        String sql = "Select valid_from,valid_upto,issue_dt from vt_trade_certificate where cert_no='" + issueTradeCertDobj.getTradeCertNo() + "'";

        try {
            tmgr = new TransactionManager("getValidityDetailsOnTradeCertNoForDuplicateCase");
            psmt = tmgr.prepareStatement(sql);

            rs = psmt.executeQuery();

            while (rs.next()) {
                issueTradeCertDobj.setValidDt(rs.getDate("valid_from"));
                issueTradeCertDobj.setValidUpto(rs.getDate("valid_upto"));
                issueTradeCertDobj.setIssueDt(rs.getDate("issue_dt"));
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

    public Date getValidUptoOnTradeCertNo(String tradeCertNo) throws VahanException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String vtTradeCertificateTable = "";

        try {
            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();
            if (tmConfigDobj.getTmTradeCertConfigDobj().isUsingOnlineSchemaTc()) {   /// using_online_schema_tc  work
                vtTradeCertificateTable = TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC;
            } else {
                vtTradeCertificateTable = TableList.VT_TRADE_CERTIFICATE;
            }

            String sql = "Select valid_upto from " + vtTradeCertificateTable + " where cert_no= ?";

            tmgr = new TransactionManager("getValidUptoOnTradeCertNo");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, tradeCertNo);
            rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                return rs.getDate("valid_upto");
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
        return null;
    }

    public String saveAndMoveFile(List<IssueTradeCertDobj> issueTradeCertificateDobjs, Status_dobj statusDobjParam, String changedData, String applicationType) throws VahanException {
        String dealerCd = null;
        String stateCd = null;
        Set<String> setOfTradeCertNoForTcPrint = new HashSet<>();
        Status_dobj statusDobj = statusDobjParam;
        PreparedStatement psSave = null;
        PreparedStatement psRecordsInsertedInVh = null;
        PreparedStatement psRecordsUpdatedInVt = null;
        PreparedStatement psRecordsInsertedInVha = null;
        PreparedStatement psRecordsDeletedFromVa = null;
        PreparedStatement psRecordsUpdatedInVm;
        TransactionManager tmgr = null;
        boolean isRenew = false;
        String saveReturn = ": Trade Certificate No:";
        String applNo = "";
        boolean alreadySaved = false;
        String sqlVtTcSave = "INSERT INTO " + TableList.VT_TRADE_CERTIFICATE + "(state_cd,off_cd,appl_no,dealer_cd,sr_no,vch_catg,no_of_vch,cert_no,valid_from,valid_upto,issue_dt,fuel_cd,applicant_type,no_of_vch_used) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
        String sqlVhTcSave = "INSERT INTO " + TableList.VH_TRADE_CERTIFICATE + "(state_cd,off_cd,appl_no,dealer_cd,sr_no,vch_catg,no_of_vch,cert_no,valid_from,valid_upto,issue_dt,fuel_cd,no_of_vch_used,no_vch_print,applicant_type,moved_on,moved_by,selected_duplicate_certificate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?, ?,CURRENT_TIMESTAMP,?,?)";
        String sqlVtTcUpdate = "UPDATE " + TableList.VT_TRADE_CERTIFICATE + " SET no_of_vch = ?,valid_from=?,valid_upto=?,issue_dt=?,no_of_vch_used=?,appl_no=?,sr_no=?,no_vch_print=?,selected_duplicate_certificate=? WHERE cert_no=? and dealer_cd=? and vch_catg =? and appl_no=? and sr_no=?";
        String sqlVhaTcSave = "INSERT INTO " + TableList.VHA_TRADE_CERTIFICATE + "(state_cd,off_cd,appl_no,dealer_cd,sr_no,vch_catg,no_of_vch,fuel_cd,stock_transfer_req,op_dt,status,applicant_type,moved_on,moved_by,selected_duplicate_certificate,trade_cert_appl_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?,?,?)";
        String sqlVaTcDelete = "DELETE FROM " + TableList.VA_TRADE_CERTIFICATE + " WHERE appl_no=?";
        String sqlVmTcSave = "UPDATE " + TableList.VM_DEALER_MAST + " SET valid_upto =? WHERE dealer_cd=?";
        TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
        boolean editActiveTC = false;

        String tradeCertificateNo = null;
        boolean requireFirstTcNo = false;


        try {
            try {
                tmgr = new TransactionManager("saveAndMoveFile");
                if ("NEW".equalsIgnoreCase(applicationType)) {
                    tradeCertificateNo = getUniqueTcNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode(), TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER);
                    requireFirstTcNo = true;
                }
            } catch (VahanException ve) {
                throw ve;
            }

            statusDobj = ServerUtil.webServiceForNextStage(statusDobj, tmgr);
            if (statusDobj.getStatus().equals(TableConstants.STATUS_COMPLETE)) {
                psSave = tmgr.prepareStatement(sqlVtTcSave);
                psRecordsInsertedInVh = tmgr.prepareStatement(sqlVhTcSave);
                psRecordsUpdatedInVt = tmgr.prepareStatement(sqlVtTcUpdate);
                psRecordsInsertedInVha = tmgr.prepareStatement(sqlVhaTcSave);
                psRecordsDeletedFromVa = tmgr.prepareStatement(sqlVaTcDelete);
                psRecordsUpdatedInVm = tmgr.prepareStatement(sqlVmTcSave);

                IssueTradeCertDobj dobjPrev = null;
                int recordAddedInBatch = 0;
                int srNoForUpdateInVt = 1;

                for (IssueTradeCertDobj dobjCurr : issueTradeCertificateDobjs) {

                    applNo = dobjCurr.getApplNo();
                    stateCd = dobjCurr.getStateCd();
                    dealerCd = dobjCurr.getDealerFor();
                    dobjPrev = getVtTradeCertificateRecord(tmgr, dobjCurr.getDealerFor(), dobjCurr.getVehCatgFor(), dobjCurr.getFuelTypeFor());

                    if (dobjPrev != null
                            && dobjPrev.getIssueDt().getTime() == dobjCurr.getIssueDt().getTime()
                            && dobjPrev.getDealerFor().trim().equals(dobjCurr.getDealerFor().trim())
                            && dobjPrev.getApplNo().trim().equals(dobjCurr.getApplNo().trim())) {

                        alreadySaved = true;
                        saveReturn = "SUCCESS" + saveReturn + dobjPrev.getTradeCertNo();
                        break;
                    }

                    if ((!CommonUtils.isNullOrBlank(dobjCurr.getStatus())) && dobjCurr.getStatus().equalsIgnoreCase("E")) { //// E: edit existing active TC (offline)
                        editActiveTC = true;
                    }

                    if (Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_NEW) {

                        if (dobjPrev == null || editActiveTC) { /// NEW or EDIT

                            if (dobjPrev != null && editActiveTC) {  /// EDIT
                                tradeCertificateNo = dobjPrev.getTradeCertNo();

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
                                psRecordsInsertedInVh.setInt(12, Integer.valueOf(dobjPrev.getFuelTypeFor()));
                                psRecordsInsertedInVh.setInt(13, Integer.valueOf(dobjPrev.getNoOfAllowedVehicles()));
                                psRecordsInsertedInVh.setString(14, dobjPrev.getApplicantCategory());
                                psRecordsInsertedInVh.setString(15, String.valueOf(dobjCurr.getEmpCd()));
                                if (!CommonUtils.isNullOrBlank(dobjPrev.getNewRenewalTradeCert())
                                        && TableConstants.TRADE_CERT_DUPLICATE_CONSTANT_VAL.equals(dobjPrev.getNewRenewalTradeCert())) {
                                    psRecordsInsertedInVh.setString(16, Utility.convertListToString(dobjPrev.getSelectedDuplicateCertificates()));
                                } else {
                                    psRecordsInsertedInVh.setNull(16, java.sql.Types.VARCHAR);
                                }
                                psRecordsInsertedInVh.addBatch();

                                psRecordsUpdatedInVt.setInt(1, Integer.valueOf(dobjPrev.getNoOfAllowedVehicles()) + Integer.valueOf(dobjCurr.getNoOfAllowedVehicles())); /// EDIT no_of_allowed_vehicles
                                psRecordsUpdatedInVt.setDate(2, new java.sql.Date(dobjPrev.getValidDt().getTime()));
                                psRecordsUpdatedInVt.setDate(3, new java.sql.Date(dobjPrev.getValidUpto().getTime()));
                                psRecordsUpdatedInVt.setDate(4, new java.sql.Date(dobjPrev.getIssueDt().getTime()));
                                psRecordsUpdatedInVt.setInt(5, 0);
                                psRecordsUpdatedInVt.setString(6, dobjCurr.getApplNo());
                                psRecordsUpdatedInVt.setInt(7, srNoForUpdateInVt++);
                                psRecordsUpdatedInVt.setInt(8, Integer.valueOf(dobjPrev.getNoOfAllowedVehicles()));
                                if (!CommonUtils.isNullOrBlank(dobjPrev.getNewRenewalTradeCert())
                                        && TableConstants.TRADE_CERT_DUPLICATE_CONSTANT_VAL.equals(dobjPrev.getNewRenewalTradeCert())) {
                                    psRecordsUpdatedInVt.setString(9, Utility.convertListToString(dobjPrev.getSelectedDuplicateCertificates()));
                                } else {
                                    psRecordsUpdatedInVt.setNull(9, java.sql.Types.VARCHAR);
                                }
                                psRecordsUpdatedInVt.setString(10, dobjPrev.getTradeCertNo());
                                psRecordsUpdatedInVt.setString(11, dobjPrev.getDealerFor());
                                psRecordsUpdatedInVt.setString(12, dobjPrev.getVehCatgFor());
                                psRecordsUpdatedInVt.setString(13, dobjPrev.getApplNo());
                                psRecordsUpdatedInVt.setInt(14, Integer.parseInt(dobjPrev.getSrNo()));
                                psRecordsUpdatedInVt.addBatch();

                            } else { ///NEW 
                                if (!requireFirstTcNo && tmConfigurationDobj.isTcNoForEachVehCatg()) {
                                    tradeCertificateNo = getUniqueTcNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode(), TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER);
                                }
                                requireFirstTcNo = false;

                                saveReturn = insertIntoVtTradeCertificate(psSave, tradeCertificateNo, dobjCurr);
                                if ("SUCCESS".equals(saveReturn)) {
                                    recordAddedInBatch++;
                                }
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
                            psRecordsInsertedInVh.setInt(12, Integer.valueOf(dobjPrev.getFuelTypeFor()));
                            if ((dobjPrev.getNoOfVehiclePrint() == null)
                                    || (dobjPrev.getNoOfVehiclePrint() != null && dobjPrev.getNoOfVehiclePrint().trim().equalsIgnoreCase("0"))) {
                                psRecordsInsertedInVh.setInt(13, 0);
                            } else {
                                psRecordsInsertedInVh.setInt(13, Integer.valueOf(dobjPrev.getNoOfVehiclePrint()));
                            }
                            psRecordsInsertedInVh.setString(14, dobjPrev.getApplicantCategory());
                            psRecordsInsertedInVh.setString(15, String.valueOf(dobjCurr.getEmpCd()));
                            if (dobjPrev.getSelectedDuplicateCertificates() != null
                                    && dobjPrev.getSelectedDuplicateCertificates().size() > 0) {
                                psRecordsInsertedInVh.setString(16, Utility.convertListToString(dobjPrev.getSelectedDuplicateCertificates()));
                            } else {
                                psRecordsInsertedInVh.setNull(16, java.sql.Types.VARCHAR);
                            }
                            psRecordsInsertedInVh.addBatch();

                            if (tmConfigurationDobj.getTmTradeCertConfigDobj().isNoOfVehicleToBeUpdatedWithoutUsingBalance()) { /// For Gujarat
                                psRecordsUpdatedInVt.setInt(1, Integer.valueOf(dobjCurr.getNoOfAllowedVehicles()));
                            } else {
                                if (Integer.valueOf(dobjPrev.getNoOfAllowedVehicles()) >= Integer.valueOf(dobjPrev.getNoOfVehiclesUsed())) {
                                    int noOfVehBalance = Integer.valueOf(dobjPrev.getNoOfAllowedVehicles()) - Integer.valueOf(dobjPrev.getNoOfVehiclesUsed());
                                    // (E: Endorsement case) added previous no of tc too, for existing dealer 
                                    if (!CommonUtils.isNullOrBlank(dobjCurr.getNewRenewalTradeCert())
                                            && TableConstants.TRADE_CERT_ENDORSEMENT_CONSTANT_VAL.equals(dobjCurr.getNewRenewalTradeCert())) {
                                        psRecordsUpdatedInVt.setInt(1, Integer.valueOf(dobjPrev.getNoOfAllowedVehicles())
                                                + Integer.valueOf(dobjCurr.getNoOfAllowedVehicles()));
                                    } else {
                                        psRecordsUpdatedInVt.setInt(1, Integer.valueOf(dobjCurr.getNoOfAllowedVehicles()));
                                    }
                                } else {
                                    int noOfVehBalance = Integer.valueOf(dobjPrev.getNoOfVehiclesUsed()) - Integer.valueOf(dobjPrev.getNoOfAllowedVehicles());
                                    psRecordsUpdatedInVt.setInt(1, Integer.valueOf(dobjCurr.getNoOfAllowedVehicles()));
                                }
                            }

                            if (!CommonUtils.isNullOrBlank(dobjCurr.getNewRenewalTradeCert())
                                    && TableConstants.TRADE_CERT_ENDORSEMENT_CONSTANT_VAL.equals(dobjCurr.getNewRenewalTradeCert())) {
                                psRecordsUpdatedInVt.setDate(2, new java.sql.Date(dobjPrev.getValidDt().getTime()));
                                psRecordsUpdatedInVt.setDate(3, new java.sql.Date(dobjPrev.getValidUpto().getTime()));
                                psRecordsUpdatedInVt.setDate(4, new java.sql.Date(dobjCurr.getIssueDt().getTime()));
                            } else {
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(new Date());
                                Date currentDate = cal.getTime();
                                Date validFromRenew;
                                Date prevDobjValidUptoDate = new Date(dobjPrev.getValidUpto().getTime());

                                if (currentDate.after(prevDobjValidUptoDate)) {
                                    validFromRenew = cal.getTime();
                                } else {
                                    cal.setTime(prevDobjValidUptoDate);
                                    cal.add(Calendar.DATE, 1);
                                    validFromRenew = cal.getTime();
                                }

                                psRecordsUpdatedInVt.setDate(2, new java.sql.Date(validFromRenew.getTime()));
                                cal.setTime(validFromRenew);
                                cal.add(Calendar.DATE, 365);
                                Date tradeCertificateRenewDate = cal.getTime();
                                dobjCurr.setValidUpto(tradeCertificateRenewDate);

                                psRecordsUpdatedInVt.setDate(3, new java.sql.Date(dobjCurr.getValidUpto().getTime()));
                                psRecordsUpdatedInVt.setDate(4, new java.sql.Date(dobjCurr.getIssueDt().getTime()));
                            }

                            psRecordsUpdatedInVt.setInt(5, 0);
                            psRecordsUpdatedInVt.setString(6, dobjCurr.getApplNo());
                            psRecordsUpdatedInVt.setInt(7, srNoForUpdateInVt++);
                            if ((dobjPrev.getNoOfVehiclePrint() == null)
                                    || (dobjPrev.getNoOfVehiclePrint() != null && dobjPrev.getNoOfVehiclePrint().trim().equalsIgnoreCase("0"))) {
                                psRecordsUpdatedInVt.setInt(8, 0);
                            } else {
                                psRecordsUpdatedInVt.setInt(8, Integer.valueOf(dobjPrev.getNoOfVehiclePrint()));
                            }
                            if (!CommonUtils.isNullOrBlank(dobjCurr.getNewRenewalTradeCert())
                                    && TableConstants.TRADE_CERT_DUPLICATE_CONSTANT_VAL.equals(dobjCurr.getNewRenewalTradeCert())) {
                                psRecordsUpdatedInVt.setString(9, Utility.convertListToString(dobjCurr.getSelectedDuplicateCertificates()));
                            } else {
                                psRecordsUpdatedInVt.setNull(9, java.sql.Types.VARCHAR);
                            }
                            psRecordsUpdatedInVt.setString(10, dobjPrev.getTradeCertNo());
                            psRecordsUpdatedInVt.setString(11, dobjPrev.getDealerFor());
                            psRecordsUpdatedInVt.setString(12, dobjPrev.getVehCatgFor());
                            psRecordsUpdatedInVt.setString(13, dobjPrev.getApplNo());
                            psRecordsUpdatedInVt.setInt(14, Integer.parseInt(dobjPrev.getSrNo()));
                            psRecordsUpdatedInVt.addBatch();


                            //update valid Upto in vm_dealer_mast in case of renew
                            psRecordsUpdatedInVm.setDate(1, new java.sql.Date(dobjCurr.getValidUpto().getTime()));
                            psRecordsUpdatedInVm.setString(2, dobjPrev.getDealerFor());
                            psRecordsUpdatedInVm.addBatch();
                        }
                    }

                    if (Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_DUP && dobjPrev != null) {
                        tradeCertificateNo = dobjPrev.getTradeCertNo();
                        // update "selected_duplicate_certificate" column for duplicate in vahan4.vt_trade_certificate
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
                        psRecordsInsertedInVh.setInt(12, Integer.valueOf(dobjPrev.getFuelTypeFor()));
                        if ((!CommonUtils.isNullOrBlank(dobjPrev.getNoOfVehiclePrint())
                                && "0".equals(dobjPrev.getNoOfVehiclePrint()))) {
                            psRecordsInsertedInVh.setInt(13, 0);
                        } else {
                            psRecordsInsertedInVh.setInt(13, Integer.valueOf(dobjPrev.getNoOfVehiclePrint()));
                        }
                        psRecordsInsertedInVh.setString(14, dobjPrev.getApplicantCategory());
                        psRecordsInsertedInVh.setString(15, String.valueOf(dobjCurr.getEmpCd()));
                        if (!CommonUtils.isNullOrBlank(dobjPrev.getNewRenewalTradeCert())
                                && TableConstants.TRADE_CERT_DUPLICATE_CONSTANT_VAL.equals(dobjPrev.getNewRenewalTradeCert())) {
                            psRecordsInsertedInVh.setString(16, Utility.convertListToString(dobjPrev.getSelectedDuplicateCertificates()));
                        } else {
                            psRecordsInsertedInVh.setNull(16, java.sql.Types.VARCHAR);
                        }
                        psRecordsInsertedInVh.addBatch();
                        if (tmConfigurationDobj.getTmTradeCertConfigDobj().isNoOfVehicleToBeUpdatedWithoutUsingBalance()) { /// For Gujarat
                            psRecordsUpdatedInVt.setInt(1, Integer.valueOf(dobjPrev.getNoOfAllowedVehicles()));
                        } else {
                            if (Integer.valueOf(dobjPrev.getNoOfAllowedVehicles()) >= Integer.valueOf(dobjPrev.getNoOfVehiclesUsed())) {
                                int noOfVehBalance = Integer.valueOf(dobjPrev.getNoOfAllowedVehicles()) - Integer.valueOf(dobjPrev.getNoOfVehiclesUsed());
                                psRecordsUpdatedInVt.setInt(1, Integer.valueOf(dobjPrev.getNoOfAllowedVehicles()));
                            } else {
                                int noOfVehBalance = Integer.valueOf(dobjPrev.getNoOfVehiclesUsed()) - Integer.valueOf(dobjPrev.getNoOfAllowedVehicles());
                                psRecordsUpdatedInVt.setInt(1, Integer.valueOf(dobjCurr.getNoOfAllowedVehicles()));
                            }
                        }
                        psRecordsUpdatedInVt.setDate(2, new java.sql.Date(dobjPrev.getValidDt().getTime()));
                        psRecordsUpdatedInVt.setDate(3, new java.sql.Date(dobjPrev.getValidUpto().getTime()));
                        psRecordsUpdatedInVt.setDate(4, new java.sql.Date(dobjPrev.getIssueDt().getTime()));
                        psRecordsUpdatedInVt.setInt(5, 0);
                        psRecordsUpdatedInVt.setString(6, dobjCurr.getApplNo());
                        psRecordsUpdatedInVt.setInt(7, srNoForUpdateInVt++);
                        psRecordsUpdatedInVt.setInt(8, Integer.valueOf(dobjPrev.getNoOfVehiclePrint()));
                        if (!CommonUtils.isNullOrBlank(dobjCurr.getNewRenewalTradeCert())
                                && TableConstants.TRADE_CERT_DUPLICATE_CONSTANT_VAL.equals(dobjCurr.getNewRenewalTradeCert())) {
                            psRecordsUpdatedInVt.setString(9, Utility.convertListToString(dobjCurr.getSelectedDuplicateCertificates()));
                        } else {
                            psRecordsUpdatedInVt.setNull(9, java.sql.Types.VARCHAR);
                        }
                        psRecordsUpdatedInVt.setString(10, dobjPrev.getTradeCertNo());
                        psRecordsUpdatedInVt.setString(11, dobjPrev.getDealerFor());
                        psRecordsUpdatedInVt.setString(12, dobjPrev.getVehCatgFor());
                        psRecordsUpdatedInVt.setString(13, dobjPrev.getApplNo());
                        psRecordsUpdatedInVt.setInt(14, Integer.parseInt(dobjPrev.getSrNo()));
                        psRecordsUpdatedInVt.addBatch();
                        //update "selected_duplicate_certificate" column for duplicate in vahan4.vt_trade_certificate
                    }

                    saveAndMoveFileBlock1(psRecordsInsertedInVha, psRecordsDeletedFromVa, dobjCurr);
                    setOfTradeCertNoForTcPrint.add(tradeCertificateNo);
                }

                if (!alreadySaved) {
                    if (Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_NEW) {
                        if (isRenew || editActiveTC) { /// RENEW or EDIT
                            int[] recordsInsertedInVh = psRecordsInsertedInVh.executeBatch();
                            int[] recordsUpdatedInVt = psRecordsUpdatedInVt.executeBatch();
                            if (isRenew) {
                                int[] recordsUpdatedInVm = {};
                                if (!tmConfigurationDobj.getTmTradeCertConfigDobj().isDealerMasterNotToBeUpdated()) {
                                    insertIntoVHMFromVMDealerMast(tmgr, dealerCd);
                                    recordsUpdatedInVm = psRecordsUpdatedInVm.executeBatch();
                                }
                                if (recordsInsertedInVh.length == issueTradeCertificateDobjs.size() && recordsUpdatedInVt.length == issueTradeCertificateDobjs.size() && (!tmConfigurationDobj.getTmTradeCertConfigDobj().isDealerMasterNotToBeUpdated() && recordsUpdatedInVm.length > 0)) {
                                    saveReturn = recordUpdatedInVtFee(saveReturn, tmConfigurationDobj, tmgr, applNo, Integer.valueOf(issueTradeCertificateDobjs.get(0).getPurCd()), issueTradeCertificateDobjs.get(0).getNewRenewalTradeCert());
                                } else if (recordsInsertedInVh.length == issueTradeCertificateDobjs.size() && recordsUpdatedInVt.length == issueTradeCertificateDobjs.size()) {
                                    saveReturn = recordUpdatedInVtFee(saveReturn, tmConfigurationDobj, tmgr, applNo, Integer.valueOf(issueTradeCertificateDobjs.get(0).getPurCd()), issueTradeCertificateDobjs.get(0).getNewRenewalTradeCert());
                                } else {
                                    saveReturn = "FAILURE";
                                }
                            } else {
                                if (recordsInsertedInVh.length == issueTradeCertificateDobjs.size() && recordsUpdatedInVt.length == issueTradeCertificateDobjs.size()) {
                                    saveReturn = recordUpdatedInVtFee(saveReturn, tmConfigurationDobj, tmgr, applNo, Integer.valueOf(issueTradeCertificateDobjs.get(0).getPurCd()), issueTradeCertificateDobjs.get(0).getNewRenewalTradeCert());
                                } else {
                                    saveReturn = "FAILURE";
                                }
                            }
                        } else { //// NEW

                            if (recordAddedInBatch == issueTradeCertificateDobjs.size()) {
                                int[] recordsInserted = psSave.executeBatch();
                                saveReturn = "SUCCESS : Application No: " + applNo;
                            } else {
                                saveReturn = "FAILURE";
                            }
                        }
                    } else {   /// DUPLICATE
                        int[] recordsInsertedInVh = psRecordsInsertedInVh.executeBatch();
                        int[] recordsUpdatedInVt = psRecordsUpdatedInVt.executeBatch();
                        if (recordsInsertedInVh.length > 0 && recordsUpdatedInVt.length > 0) {
                            saveReturn = recordUpdatedInVtFee(saveReturn, tmConfigurationDobj, tmgr, applNo, Integer.valueOf(issueTradeCertificateDobjs.get(0).getPurCd()), issueTradeCertificateDobjs.get(0).getNewRenewalTradeCert());
                        } else {
                            saveReturn = "FAILURE";
                        }
                    }

                    saveAndMoveFileBlock2(saveReturn, psRecordsInsertedInVha, psRecordsDeletedFromVa);

                    int vaTcPrintCount = 0;
                    for (String tradeCertNo : setOfTradeCertNoForTcPrint) {
                        deletePreviousApplFromVaTCPrint(tmgr, tradeCertNo);
                        boolean isRecordInserted = insertIntoVaTCPrint(tmgr, applNo, tradeCertNo);
                        if (isRecordInserted) {
                            vaTcPrintCount++;
                        }
                    }
                    if (vaTcPrintCount != setOfTradeCertNoForTcPrint.size()) {
                        saveReturn = "FAILURE";
                    }

                    if (saveReturn.contains("SUCCESS")) {
//                        if (tmConfigurationDobj.getTmTradeCertConfigDobj().isTcNoToBeUpdatedWithDealerRegnNo()) {  /// update_dealer_regn_no_as_tc_no work for Chhattisgarh
//                            updateDealerRegnNoAsTCNo(tmgr, tradeCertificateNo, dealerCd);
//                        }
                        insertIntoVhaChangedData(tmgr, issueTradeCertificateDobjs.get(0).getApplNo(), changedData);
                        ServerUtil.fileFlow(tmgr, statusDobj);

                        tmgr.commit();
                    }
                } else {
                    if (isRenew || alreadySaved || Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_DUP) {
                        saveReturn = "SUCCESS" + saveReturn + dobjPrev.getTradeCertNo();
                    } else {
                        if (tmConfigurationDobj.isTcNoForEachVehCatg()) {
                            saveReturn = "SUCCESS" + saveReturn + setOfTradeCertNoForTcPrint.size();
                        } else {
                            saveReturn = "SUCCESS" + saveReturn + tradeCertificateNo;
                        }
                    }
                }

                if (!saveReturn.contains(tradeCertificateNo) && (isRenew || alreadySaved || Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_TRADE_CERT_DUP)
                        && !tmConfigurationDobj.isTcNoForEachVehCatg()) {
                    saveReturn += ":" + tradeCertificateNo;
                } else {
                    saveReturn = "SUCCESS" + saveReturn + setOfTradeCertNoForTcPrint.size();
                }

            } else if (statusDobj.getStatus().equals(TableConstants.STATUS_REVERT)) {
                ServerUtil.fileFlow(tmgr, statusDobj);
                saveReturn = "SUCCESS";
                tmgr.commit();
            }
        } catch (VahanException ve) {
            saveReturn = "FAILURE";
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            throw ve;
        } catch (BatchUpdateException bue) {
            saveReturn = "FAILURE";
            LOGGER.error(bue.toString() + " " + bue.getStackTrace()[0]);
            LOGGER.error(bue.toString() + " " + bue.getNextException());
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (SQLException ex) {
            saveReturn = "FAILURE";
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception ex) {
            saveReturn = "FAILURE";
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (psSave != null) {
                    psSave.close();
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

    private boolean insertIntoVHMFromVMDealerMast(TransactionManager tmgr, String dealerCd)
            throws VahanException {
        try {
            PreparedStatement ps = null;
            String sql = "INSERT INTO " + TableList.VHM_DEALER_MAST
                    + " (dealer_cd,state_cd,off_cd,dealer_name,dealer_regn_no,d_add1,d_add2,d_district,d_pincode,d_state,"
                    + " valid_upto,entered_by,entered_on,tin_no,moved_on,moved_by,regn_mark_gen_by_dealer)"
                    + " SELECT dealer_cd,state_cd,off_cd,dealer_name,dealer_regn_no,d_add1,d_add2,d_district,d_pincode,d_state,"
                    + " valid_upto,entered_by,entered_on,tin_no,CURRENT_TIMESTAMP,?,regn_mark_gen_by_dealer"
                    + " FROM " + TableList.VM_DEALER_MAST
                    + " WHERE dealer_cd = ?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, dealerCd);
            int i = ps.executeUpdate();

            if (i > 0) {
                return true;
            } else {
                throw new VahanException("Exception in moving dealer master data into history table. ");
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
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

    private boolean insertIntoVaTCPrint(TransactionManager tmgr, String applNo, String tradeCertNo) throws VahanException, SQLException {

        PreparedStatement pstmt = null;

        String sqlVaTcPrint = "Insert into va_tc_print(state_cd,off_cd,appl_no,cert_no,op_dt) values(?,?,?,?,CURRENT_TIMESTAMP)";

        try {
            pstmt = tmgr.prepareStatement(sqlVaTcPrint);
            pstmt.setString(1, Util.getUserStateCode());
            pstmt.setInt(2, Util.getSelectedSeat().getOff_cd());
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

    private void saveAndMoveFileBlock1(PreparedStatement psRecordsInsertedInVha, PreparedStatement psRecordsDeletedFromVa, IssueTradeCertDobj dobjCurr) throws SQLException, VahanException {

        psRecordsInsertedInVha.setString(1, dobjCurr.getStateCd());
        psRecordsInsertedInVha.setInt(2, dobjCurr.getOffCd());
        psRecordsInsertedInVha.setString(3, dobjCurr.getApplNo());
        psRecordsInsertedInVha.setString(4, dobjCurr.getDealerFor());
        psRecordsInsertedInVha.setInt(5, Integer.valueOf(dobjCurr.getSrNo()));
        psRecordsInsertedInVha.setString(6, dobjCurr.getVehCatgFor());
        psRecordsInsertedInVha.setInt(7, Integer.valueOf(dobjCurr.getNoOfAllowedVehicles()));
        psRecordsInsertedInVha.setInt(8, Integer.valueOf(dobjCurr.getFuelTypeFor()));
        psRecordsInsertedInVha.setBoolean(9, dobjCurr.isStockTransferReq());
        psRecordsInsertedInVha.setDate(10, new java.sql.Date(getOpDtFromVa(dobjCurr.getApplNo()).getTime()));
        psRecordsInsertedInVha.setString(11, dobjCurr.getStatus());
        psRecordsInsertedInVha.setString(12, dobjCurr.getApplicantCategory());
        psRecordsInsertedInVha.setString(13, String.valueOf(dobjCurr.getEmpCd()));
        if (dobjCurr.getSelectedDuplicateCertificates() != null
                && dobjCurr.getSelectedDuplicateCertificates().size() > 0) {
            psRecordsInsertedInVha.setString(14, Utility.convertListToString(dobjCurr.getSelectedDuplicateCertificates()));
        } else {
            psRecordsInsertedInVha.setNull(14, java.sql.Types.VARCHAR);
        }
        psRecordsInsertedInVha.setString(15, dobjCurr.getNewRenewalTradeCert());
        psRecordsInsertedInVha.addBatch();

        psRecordsDeletedFromVa.setString(1, dobjCurr.getApplNo());

    }

    private void saveAndMoveFileBlock2(String saveReturn, PreparedStatement psRecordsInsertedInVha, PreparedStatement psRecordsDeletedFromVa) throws SQLException {
        if (saveReturn.contains("SUCCESS")) {
            psRecordsInsertedInVha.executeBatch();
            psRecordsDeletedFromVa.executeUpdate();
        }

    }

    private Date getOpDtFromVa(String applNo) throws VahanException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        Date opDt = null;

        String sql = "Select op_dt from va_trade_certificate where appl_no='" + applNo + "'";

        try {
            tmgr = new TransactionManager("getOpDtFromVa");
            psmt = tmgr.prepareStatement(sql);
            rs = psmt.executeQuery();
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

    public boolean checkTradeCertExpiredForTradeCertNoFromVtTradeCert(String tradeCertNo) throws VahanException {
        boolean tradeCertExpired = false;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;

        String sql = "Select * from vt_trade_certificate where cert_no='" + tradeCertNo + "'";

        try {
            tmgr = new TransactionManager("checkTradeCertExpiredForTradeCertNoFromVtTradeCert");
            psmt = tmgr.prepareStatement(sql);
            rs = psmt.executeQuery();
            Calendar cal = Calendar.getInstance();

            while (rs.next()) {

                cal.setTime(rs.getDate("valid_upto"));
                cal.add(Calendar.DATE, 1);
                cal.add(Calendar.MONTH, -1);
                Date tradeCertificateExpireDate = cal.getTime();

                if (new Date().after(tradeCertificateExpireDate)
                        || rs.getInt("no_of_vch") == rs.getInt("no_of_vch_used")) {

                    tradeCertExpired = true;
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
        return tradeCertExpired;
    }

    public static int insertIntoVHTradeCertificateFromVTTradeCertificate(TransactionManager tmgr, String applNo)
            throws VahanException {
        SessionVariables sessionVariables = new SessionVariables();
        try {
            PreparedStatement ps = null;
            String vaTradeCertificateSql = null;

            vaTradeCertificateSql = "INSERT INTO " + TableList.VH_TRADE_CERTIFICATE + "(state_cd,off_cd,appl_no,dealer_cd,sr_no,vch_catg,no_of_vch,cert_no,valid_from,valid_upto,issue_dt,fuel_cd,no_of_vch_used,no_vch_print,applicant_type,moved_on,moved_by)\n"
                    + " SELECT  state_cd,off_cd,appl_no,dealer_cd,sr_no,vch_catg,no_of_vch,cert_no,valid_from,valid_upto,issue_dt,fuel_cd,no_of_vch_used,no_vch_print,applicant_type,CURRENT_TIMESTAMP,?\n"
                    + " FROM " + TableList.VT_TRADE_CERTIFICATE + " where " + TableList.VT_TRADE_CERTIFICATE + ".appl_no=?";


            ps = tmgr.prepareStatement(vaTradeCertificateSql);
            if (POSValidator.validate(sessionVariables.getEmpCodeLoggedIn(), "AN")) {
                ps.setString(1, sessionVariables.getEmpCodeLoggedIn());
            }
            if (POSValidator.validate(applNo, "AN")) {
                ps.setString(2, applNo);
            }

            return ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }
    }

    public boolean checkTradeCertNoExistInVtTradeCert(String tradeCertNo) throws VahanException {
        boolean tradeCertExist = false;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;

        String sql = "Select * from vt_trade_certificate where cert_no='" + tradeCertNo + "'";

        try {
            tmgr = new TransactionManager("checkTradeCertNoExistInVtTradeCert");
            psmt = tmgr.prepareStatement(sql);
            rs = psmt.executeQuery();
            while (rs.next()) {
                tradeCertExist = true;
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
        return tradeCertExist;
    }

    public Date fetchDealerValidityDateFromMaster(String dealerCd) throws VahanException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        Date dealerValidUpto = null;

        String sql = "Select valid_upto from vm_dealer_mast where dealer_cd=?";

        try {
            tmgr = new TransactionManager("fetchDealerValidityDateFromMaster");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, dealerCd);
            rs = psmt.executeQuery();
            while (rs.next()) {
                dealerValidUpto = rs.getDate("valid_upto");
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
        return dealerValidUpto;
    }

    public int updateDealerRegnNoAsTCNo(TransactionManager tmgr, String tradeCertNo, String dealerCd) throws VahanException {
        PreparedStatement psmt = null;
        int recordUpdated = 0;
        String sql = "update vm_dealer_mast set dealer_regn_no = ? where dealer_cd = ?";

        try {
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, tradeCertNo);
            psmt.setString(2, dealerCd);
            recordUpdated = psmt.executeUpdate();

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
        return recordUpdated;
    }
}
