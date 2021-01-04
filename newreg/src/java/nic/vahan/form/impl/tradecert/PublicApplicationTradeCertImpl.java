/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.tradecert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.java.util.CommonUtils;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.validators.POSValidator;
import nic.vahan.db.MasterTableDO;
import static nic.vahan.db.MasterTableFiller.makeMasterTableDO;
import nic.vahan.db.MasterTables;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.tradecert.PublicApplicationTradeCertDobj;
import nic.vahan.form.dobj.tradecert.VehicleClassCategoryMappingDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.form.dobj.tradecert.IssueTradeCertDobj;

/**
 *
 * @author nicsi
 */
public class PublicApplicationTradeCertImpl {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(PublicApplicationTradeCertImpl.class);
    public static MasterTables masterTables = loadMasterTables();/////

    public static List<SelectItem> getOfficeAsPerSelectedStateAndDistrict(String selectedStateCd, String selectedDistrictCd) throws VahanException {
        List<SelectItem> officesList = null;
        TransactionManager tmgr = null;
        String Sql = "Select * from " + TableList.TM_OFFICE + " where state_cd = ?  and dist_cd = ? order by off_name ";
        PreparedStatement ps;
        RowSet rs;
        try {
            tmgr = new TransactionManager("getOfficeAsPerSelectedStateAndDistrict");
            ps = tmgr.prepareStatement(Sql);
            if (POSValidator.validate(selectedStateCd, "A")) {
                ps.setString(1, selectedStateCd);
            }
            if (POSValidator.validate(selectedDistrictCd, "N")) {
                ps.setInt(2, Integer.valueOf(selectedDistrictCd));
            }
            LOGGER.info("[MASTER QUERY] PublicApplicationTradeCertImpl:getOfficeAsPerSelectedStateAndDistrict:::" + ps.toString());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (selectedStateCd.equals("DL")) {
                    if (rs.getInt("off_cd") != 51) {   //// off_cd = 51 ('RAJPUR ROAD/BURARI')  NOT TO BE INCLUDED IN THE OFFICES LIST
                        officesList.add(new SelectItem(rs.getInt("off_cd"), rs.getString("off_name")));
                    }
                } else {
                    officesList.add(new SelectItem(rs.getInt("off_cd"), rs.getString("off_name")));
                }
            }
        } catch (SQLException | VahanException e) {
            LOGGER.error(e.getMessage());
            throw new VahanException("Error in getting offices list after selecting state and district.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }

            }
        }
        return officesList;
    }

    /**
     *
     * @param stateCode
     * @param tmgr Instance of TransactionManager
     * @param tableName
     * @param colName
     * @param descr
     * @return List of key, value pair used for selectOneMenu
     */
    private static List getMasterData(String stateCode, TransactionManager tmgr, String tableName, String colName, String descr) throws VahanException {
        List dataList = new ArrayList<>();
        String Sql = "Select * from " + tableName + " where state_cd = ? order by " + descr + "";
        PreparedStatement ps;
        RowSet rs;
        try {
            ps = tmgr.prepareStatement(Sql);
            if (POSValidator.validate(stateCode, "A")) {
                ps.setString(1, stateCode);
            }
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if (stateCode.equals("DL")) {
                    if (rs.getInt(colName) != 51) {   //// off_cd = 51 ('RAJPUR ROAD/BURARI')  NOT TO BE INCLUDED IN THE OFFICES LIST
                        dataList.add(new SelectItem(rs.getInt(colName), rs.getString(descr)));
                    }
                } else {
                    dataList.add(new SelectItem(rs.getInt(colName), rs.getString(descr)));
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        return dataList;
    }

    public static List getOfficeData(String stateCode) throws VahanException {
        TransactionManager tmgr = null;
        List officeList = new ArrayList<>();
        try {
            tmgr = new TransactionManager("getOfficeData");
            officeList = getMasterData(stateCode, tmgr, TableList.TM_OFFICE, "off_cd", "off_name");
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }

            }
        }
        return officeList;
    }

    public static List getDistrictData(String stateCode) throws VahanException {
        TransactionManager tmgr = null;
        List districtList = new ArrayList<>();
        try {
            tmgr = new TransactionManager("getDistrictData");
            districtList = getMasterData(stateCode, tmgr, TableList.TM_DISTRICT, "dist_cd", "descr");
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }

            }
        }
        return districtList;
    }

    public void fetchApplicantOtherDetailsFromMaster(PublicApplicationTradeCertDobj publicApplicationTradeCertDobj) throws VahanException {

        TransactionManager tmgr = null;
        PreparedStatement psmt = null;
        RowSet rs = null;
        boolean recordFound = false;
        String sql = "Select applicant_showroom_name,applicant_showroom_address1,applicant_showroom_address2,applicant_showroom_district,"
                + "applicant_showroom_pincode,applicant_loi_auth_no,applicant_loi_auth_date from " + TableList.VM_APPLICANT_MAST_APPL_ONLINE_SCHEMA_TC + " where applicant_cd = ?";

        try {
            tmgr = new TransactionManager("fetchOtherDetails");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, publicApplicationTradeCertDobj.getApplicantCode());
            rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                recordFound = true;
                publicApplicationTradeCertDobj.setShowRoomName(rs.getString("applicant_showroom_name"));
                publicApplicationTradeCertDobj.setShowRoomAddress1(rs.getString("applicant_showroom_address1"));
                publicApplicationTradeCertDobj.setShowRoomAddress2(rs.getString("applicant_showroom_address2"));
                publicApplicationTradeCertDobj.setShowRoomDistrict(rs.getString("applicant_showroom_district"));
                publicApplicationTradeCertDobj.setShowRoomPincode(rs.getInt("applicant_showroom_pincode") + "");
                publicApplicationTradeCertDobj.setLOIAuthorisationNo(rs.getString("applicant_loi_auth_no"));
                publicApplicationTradeCertDobj.setLOIAuthorisationDate(rs.getDate("applicant_loi_auth_date"));
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                if (publicApplicationTradeCertDobj.getApplicantCategory().equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)) {
                    publicApplicationTradeCertDobj.setLOIAuthorisationDateString(sdf.format(publicApplicationTradeCertDobj.getLOIAuthorisationDate()));
                }
            }
            if (!recordFound) {
                sql = "Select showroom_name,showroom_add1,showroom_add2,showroom_district,"
                        + "showroom_pincode,loi_auth_no,loi_auth_date from " + TableList.VM_DEALER_OTHER_DETAILS + " where dealer_cd = ?";
                tmgr = new TransactionManager("fetchOtherDetails");
                psmt = tmgr.prepareStatement(sql);
                psmt.setString(1, publicApplicationTradeCertDobj.getApplicantCode());
                rs = tmgr.fetchDetachedRowSet();
                while (rs.next()) {
                    recordFound = true;
                    publicApplicationTradeCertDobj.setShowRoomName(rs.getString("showroom_name"));
                    publicApplicationTradeCertDobj.setShowRoomAddress1(rs.getString("showroom_add1"));
                    publicApplicationTradeCertDobj.setShowRoomAddress2(rs.getString("showroom_add2"));
                    publicApplicationTradeCertDobj.setShowRoomDistrict(rs.getString("showroom_district"));
                    publicApplicationTradeCertDobj.setShowRoomPincode(rs.getInt("showroom_pincode") + "");
                    publicApplicationTradeCertDobj.setLOIAuthorisationNo(rs.getString("loi_auth_no"));
                    publicApplicationTradeCertDobj.setLOIAuthorisationDate(rs.getDate("loi_auth_date"));
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                    if (publicApplicationTradeCertDobj.getApplicantCategory().equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER) && publicApplicationTradeCertDobj.getLOIAuthorisationDate() != null) {
                        publicApplicationTradeCertDobj.setLOIAuthorisationDateString(sdf.format(publicApplicationTradeCertDobj.getLOIAuthorisationDate()));
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
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public String saveBasicDetails(PublicApplicationTradeCertDobj dobj, boolean noApplicantMasterRecordExist) throws VahanException {

        TransactionManager tmgr = null;
        String saveReturn = "FAILURE";
        try {

            tmgr = new TransactionManager("PublicApplicationTradeCertImpl.saveBasicDetails()");
            if (noApplicantMasterRecordExist) {
                if (dobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER) && (dobj.getApplicantCode() == null || dobj.getApplicantCode().equals(""))) {
                    dobj.setApplicantCode(generateApplicantCodeAfterSaveIntoVmApplicantMastAppl(tmgr, dobj));
                } else if (dobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER) && (dobj.getApplicantCode() == null || dobj.getApplicantCode().equals(""))) {
                    dobj.setApplicantCode(generateApplicantCodeAfterSaveFinancerIntoVmApplMastAppl(tmgr, dobj));
                }
            }
            if (!dobj.getApplicantCode().equals("")) {
                saveReturn = updateNoOfUsedVchIntoVtTradeCertificate(tmgr, dobj);
                saveReturn = insertIntoVaTradeCertificate(tmgr, dobj);

                if (saveReturn.contains("SUCCESS") && dobj != null) {
                    Status_dobj status = new Status_dobj();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                    String dt = sdf.format(new Date());
                    status.setAppl_dt(dt);
                    status.setAppl_no(saveReturn.substring(saveReturn.lastIndexOf(":") + 1));
                    status.setPur_cd(Integer.parseInt(dobj.getPurCd()));
                    status.setFlow_slno(1);
                    status.setFile_movement_slno(1);
                    status.setAction_cd(TableConstants.TM_ROLE_TRADE_CERT_DEALER_APPL_ENTRY);
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

                    if (dobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER)) {
                        VehicleParameters par = new VehicleParameters();
                        par.setAPPLICANT_TYPE(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER);
                        status.setVehicleParameters(par);
                        ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, saveReturn.substring(saveReturn.lastIndexOf(":") + 1),
                                TableConstants.TM_ROLE_TRADE_CERT_FEE, Integer.parseInt(dobj.getPurCd()), null, tmgr);
                    } else {/////////////  for dealer

                        if (dobj.getNewRenewalTradeCert().trim().equalsIgnoreCase("R") || dobj.getNewRenewalTradeCert().trim().equalsIgnoreCase("D")) {  //// For RENEW and DUPLICATE
                            VehicleParameters par = new VehicleParameters();
                            par.setAPPLICANT_TYPE(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER);
                            par.setAPPLICATION_TYPE(dobj.getNewRenewalTradeCert().trim());
                            status.setVehicleParameters(par);
                            ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, saveReturn.substring(saveReturn.lastIndexOf(":") + 1),
                                    TableConstants.TM_ROLE_TRADE_CERT_FEE, Integer.parseInt(dobj.getPurCd()), null, tmgr);
                        } else {  //// NEW 'N'  
                            ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, saveReturn.substring(saveReturn.lastIndexOf(":") + 1),
                                    TableConstants.TM_ROLE_TRADE_CERT_DEALER_APPL_INSPECT, Integer.parseInt(dobj.getPurCd()), null, tmgr);
                        }
                    }
                    ServerUtil.fileFlow(tmgr, status);
                    tmgr.commit();
                }
            }

        } catch (VahanException ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception bex) {
            LOGGER.error(bex.toString() + " " + bex.getStackTrace()[0]);
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
        return saveReturn;

    }

    private String generateApplicantCodeAfterSaveIntoVmApplicantMastAppl(TransactionManager tmgr, PublicApplicationTradeCertDobj dobj) throws VahanException {
        PreparedStatement ps;
        String sql;
        int i = 1;
        try {
            String applCode = "";
            while (true) {
                applCode = String.valueOf(ServerUtil.getUniqueInstrumentNo(tmgr, dobj.getApplicantState()));
                if (checkForDuplicateApplicantCode(applCode)) {
                    break;
                }
            }
            sql = "INSERT INTO " + TableList.VM_APPLICANT_MAST_APPL_ONLINE_SCHEMA_TC + "(\n"
                    + "            applicant_cd , applicant_state_cd , applicant_off_cd , applicant_name , applicant_relation , applicant_address1 , ";
            if (dobj.getApplicantAddress2() != null && dobj.getApplicantAddress2().trim().length() > 0) {
                sql += "applicant_address2 , ";
            }
            if (dobj.getApplicantDistrict() != null && dobj.getApplicantDistrict().trim().length() > 0 && !dobj.getApplicantDistrict().trim().equals("-1")) {
                sql += "applicant_district , ";
            }
            sql += "\n          applicant_category , applicant_pincode , applicant_mobile_no , applicant_email , applicant_maker_list , applicant_veh_class_list , \n"
                    + "            applicant_tin_no,applicant_regn_no";



            sql += ", applicant_showroom_name  ";
            sql += ", applicant_showroom_address1  ";



            if (dobj.getShowRoomAddress2() != null && dobj.getShowRoomAddress2().trim().length() > 0) {
                sql += ", applicant_showroom_address2  ";
            }
            if (dobj.getShowRoomDistrict() != null && dobj.getShowRoomDistrict().trim().length() > 0 && !dobj.getShowRoomDistrict().trim().equals("-1")) {
                sql += ", applicant_showroom_district  ";
            }
            sql += ", applicant_showroom_pincode  ";
            sql += ", applicant_loi_auth_no  ";
            sql += ", applicant_loi_auth_date  ";

            sql += ")\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?,";
            if (dobj.getApplicantAddress2() != null && dobj.getApplicantAddress2().trim().length() > 0) {
                sql += " ? , ";
            }
            if (dobj.getApplicantDistrict() != null && dobj.getApplicantDistrict().trim().length() > 0 && !dobj.getApplicantDistrict().trim().equals("-1")) {
                sql += " ? , ";
            }
            sql += "          \n?, ?, ?, ?, ?, ?, \n"
                    + "            ?,?,?,?";
            if (dobj.getShowRoomAddress2() != null && dobj.getShowRoomAddress2().trim().length() > 0) {
                sql += ", ?  ";
            }
            if (dobj.getShowRoomDistrict() != null && dobj.getShowRoomDistrict().trim().length() > 0 && !dobj.getShowRoomDistrict().trim().equals("-1")) {
                sql += ", ?  ";
            }
            sql += ",?,?,?)";
            ps = tmgr.prepareStatement(sql);
            if (POSValidator.validate(applCode, "AN")) {
                ps.setString(i++, applCode);
            }
            if (POSValidator.validate(dobj.getApplicantState(), "A")) {
                ps.setString(i++, dobj.getApplicantState());
            }
            if (POSValidator.validate(dobj.getApplicantOffice(), "N")) {
                ps.setInt(i++, Integer.valueOf(dobj.getApplicantOffice()));
            }
            if (POSValidator.validate(dobj.getApplicantName(), "USERNAME")) {
                ps.setString(i++, dobj.getApplicantName());
            }
            if (!CommonUtils.isNullOrBlank(dobj.getApplicantRelation()) && POSValidator.validate(dobj.getApplicantRelation(), "AS")) {
                ps.setString(i++, dobj.getApplicantRelation());
            } else {
                ps.setString(i++, null);
            }
            if (POSValidator.validate(dobj.getApplicantAddress1().trim(), "ADD")) {
                ps.setString(i++, dobj.getApplicantAddress1().trim());
            }
            if (dobj.getApplicantAddress2() != null && dobj.getApplicantAddress2().trim().length() > 0 && POSValidator.validate(dobj.getApplicantAddress2().trim(), "ADD")) {
                ps.setString(i++, dobj.getApplicantAddress2().trim());
            }
            if (dobj.getApplicantDistrict() != null && dobj.getApplicantDistrict().trim().length() > 0 && !dobj.getApplicantDistrict().trim().equals("-1")
                    && POSValidator.validate(dobj.getApplicantDistrict().trim(), "N")) {
                ps.setInt(i++, Integer.valueOf(dobj.getApplicantDistrict().trim()));
            }
            if (POSValidator.validate(dobj.getApplicantCategory(), "AN")) {
                ps.setString(i++, dobj.getApplicantCategory());
            }
            if (POSValidator.validate(dobj.getApplicantPincode(), "N")) {
                ps.setInt(i++, Integer.valueOf(dobj.getApplicantPincode()));
            }
            if (POSValidator.validate(dobj.getApplicantMobileNumber(), "N")) {
                ps.setString(i++, dobj.getApplicantMobileNumber());
            }
            if (!CommonUtils.isNullOrBlank(dobj.getApplicantMailId()) && POSValidator.validate(dobj.getApplicantMailId(), "EMAIL")) {
                ps.setString(i++, dobj.getApplicantMailId());
            } else {
                ps.setString(i++, dobj.getApplicantMailId());
            }
            if (POSValidator.validate(collectionToString(dobj.getSelectedApplicantManufacturerList(), ','), "DESP")) {
                ps.setString(i++, collectionToString(dobj.getSelectedApplicantManufacturerList(), ','));
            }
            if (POSValidator.validate(collectionToString(dobj.getVehClassAffiliatedByApplicantList(), ','), "DESP")) {
                ps.setString(i++, collectionToString(dobj.getVehClassAffiliatedByApplicantList(), ','));
            }
            if (POSValidator.validate(dobj.getApplicantTinNo(), "AN")) {
                ps.setString(i++, dobj.getApplicantTinNo());
            }
            //rizwan
            if (POSValidator.validate(dobj.getApplicantRegnNo(), "ANS")) {
                ps.setString(i++, dobj.getApplicantRegnNo());
            }
            if (POSValidator.validate(dobj.getShowRoomName(), "USERNAME")) {
                ps.setString(i++, dobj.getShowRoomName());
            }

            if (POSValidator.validate(dobj.getShowRoomAddress1().trim(), "ADD")) {
                ps.setString(i++, dobj.getShowRoomAddress1().trim());
            }
            if (dobj.getShowRoomAddress2() != null && dobj.getShowRoomAddress2().trim().length() > 0 && POSValidator.validate(dobj.getShowRoomAddress2().trim(), "ADD")) {
                ps.setString(i++, dobj.getShowRoomAddress2().trim());
            }
            if (dobj.getShowRoomDistrict() != null && dobj.getShowRoomDistrict().trim().length() > 0 && !dobj.getShowRoomDistrict().trim().equals("-1")
                    && POSValidator.validate(dobj.getShowRoomDistrict().trim(), "N")) {
                ps.setInt(i++, Integer.valueOf(dobj.getShowRoomDistrict().trim()));
            }
            if (POSValidator.validate(dobj.getShowRoomPincode(), "N")) {
                ps.setInt(i++, Integer.valueOf(dobj.getShowRoomPincode()));
            }
            if (POSValidator.validate(dobj.getLOIAuthorisationNo(), "AN")) {
                ps.setString(i++, dobj.getLOIAuthorisationNo().toUpperCase());
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            String LOIAuthorisationDate = sdf.format(dobj.getLOIAuthorisationDate());
            if (POSValidator.validate(LOIAuthorisationDate, "DATE")) {
                ps.setDate(i++, new java.sql.Date(dobj.getLOIAuthorisationDate().getTime()));
            }

            int noOfRecordInserted = ps.executeUpdate();
            if (noOfRecordInserted > 0) {
                dobj.setApplicantCode(applCode);
                return dobj.getApplicantCode();
            }
        } catch (SQLException | VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);//dobj.set
            throw new VahanException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }

        return "";

    }

    private String generateApplicantCodeAfterSaveFinancerIntoVmApplMastAppl(TransactionManager tmgr, PublicApplicationTradeCertDobj dobj) throws VahanException {
        PreparedStatement ps;
        String sql;
        int i = 1;
        try {
            String applCode = "";
            while (true) {
                applCode = String.valueOf(ServerUtil.getUniqueInstrumentNo(tmgr, dobj.getApplicantState()));
                if (checkForDuplicateApplicantCode(applCode)) {
                    break;
                }
            }
            sql = "INSERT INTO " + TableList.VM_APPLICANT_MAST_APPL_ONLINE_SCHEMA_TC + "(\n"
                    + "            applicant_cd , applicant_state_cd , applicant_off_cd , applicant_name , applicant_address1 , ";
            if (dobj.getApplicantAddress2() != null && dobj.getApplicantAddress2().trim().length() > 0) {
                sql += "applicant_address2 , ";
            }
            if (dobj.getApplicantDistrict() != null && dobj.getApplicantDistrict().trim().length() > 0 && !dobj.getApplicantDistrict().trim().equals("-1")) {
                sql += "applicant_district , ";
            }
            sql += "\n applicant_category , applicant_pincode , applicant_mobile_no , applicant_email , applicant_maker_list , applicant_veh_class_list , \n"
                    + "            applicant_regn_no , applicant_branch_name, applicant_landline_no ";


            sql += ")\n"
                    + "    VALUES (?, ?, ?, ?, ?,";
            if (dobj.getApplicantAddress2() != null && dobj.getApplicantAddress2().trim().length() > 0) {
                sql += " ? , ";
            }
            if (dobj.getApplicantDistrict() != null && dobj.getApplicantDistrict().trim().length() > 0 && !dobj.getApplicantDistrict().trim().equals("-1")) {
                sql += " ? , ";
            }
            sql += "\n?, ?, ?, ?, ?, ?, \n"
                    + " ?,?,?)";

            ps = tmgr.prepareStatement(sql);
            if (POSValidator.validate(applCode, "AN")) {
                ps.setString(i++, applCode);
            }
            if (POSValidator.validate(dobj.getApplicantState(), "A")) {
                ps.setString(i++, dobj.getApplicantState());
            }
            if (POSValidator.validate(dobj.getApplicantOffice(), "N")) {
                ps.setInt(i++, Integer.valueOf(dobj.getApplicantOffice()));
            }
            if (POSValidator.validate(dobj.getApplicantName(), "USERNAME")) {
                ps.setString(i++, dobj.getApplicantName());
            }
            if (POSValidator.validate(dobj.getApplicantAddress1().trim(), "ADD")) {
                ps.setString(i++, dobj.getApplicantAddress1().trim());
            }
            if (dobj.getApplicantAddress2() != null && dobj.getApplicantAddress2().trim().length() > 0 && POSValidator.validate(dobj.getApplicantAddress2().trim(), "ADD")) {
                ps.setString(i++, dobj.getApplicantAddress2().trim());
            }
            if (dobj.getApplicantDistrict() != null && dobj.getApplicantDistrict().trim().length() > 0 && !dobj.getApplicantDistrict().trim().equals("-1")
                    && POSValidator.validate(dobj.getApplicantDistrict().trim(), "N")) {
                ps.setInt(i++, Integer.valueOf(dobj.getApplicantDistrict().trim()));
            }
            if (POSValidator.validate(dobj.getApplicantCategory(), "AN")) {
                ps.setString(i++, dobj.getApplicantCategory());
            }
            if (POSValidator.validate(dobj.getApplicantPincode(), "N")) {
                ps.setInt(i++, Integer.valueOf(dobj.getApplicantPincode()));
            }
            if (POSValidator.validate(dobj.getApplicantMobileNumber(), "N")) {
                ps.setString(i++, dobj.getApplicantMobileNumber());
            }
            if (POSValidator.validate(dobj.getApplicantMailId(), "EMAIL")) {
                ps.setString(i++, dobj.getApplicantMailId());
            }

            ps.setString(i++, "");

            if (POSValidator.validate(collectionToString(dobj.getVehClassAffiliatedByApplicantList(), ','), "DESP")) {
                ps.setString(i++, collectionToString(dobj.getVehClassAffiliatedByApplicantList(), ','));
            }
            if (POSValidator.validate(dobj.getApplicantRegnNo(), "ANS")) {
                ps.setString(i++, dobj.getApplicantRegnNo());
            }
            if (POSValidator.validate(dobj.getApplicantBranchName(), "USERNAME")) {
                ps.setString(i++, dobj.getApplicantBranchName());
            }
            if (POSValidator.validate(dobj.getLandLineNumber(), "N")) {
                ps.setString(i++, dobj.getLandLineNumber());
            }
            int noOfRecordInserted = ps.executeUpdate();
            if (noOfRecordInserted > 0) {
                dobj.setApplicantCode(applCode);
                return dobj.getApplicantCode();
            }
        } catch (SQLException | VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);//dobj.set
            throw new VahanException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }

        return "";

    }

    public static String getDistrictNameFromCode(String stateCode, String districtCode) {
        String districtName = "";
        try {
            List districtList = (List) getDistrictData(stateCode);
            for (Object districtObj : districtList) {
                SelectItem districtItem = (SelectItem) districtObj;
                if (districtItem.getValue().toString().equals(districtCode)) {
                    districtName = districtItem.getLabel();
                    break;
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
        return districtName;
    }

    private boolean checkForDuplicateApplicantCode(String applicantCode) throws VahanException {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs;
        boolean status = false;
        String query = "";
        try {
            tmgr = new TransactionManager("checkForDuplicateApplicantCode");
            query = "select \n"
                    + "NOT EXISTS (select applicant_cd from " + TableList.VM_APPLICANT_MAST_APPL_ONLINE_SCHEMA_TC + " where applicant_cd = ?) AND \n"
                    + "NOT EXISTS (select dealer_cd    from " + TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " where dealer_cd    = ?) AND \n"
                    + "NOT EXISTS (select financer_cd  from " + TableList.VM_FINANCIER_TC + " where financer_cd    = ?) AND \n"
                    + "NOT EXISTS (select dealer_cd    from " + TableList.VM_DEALER_MAST + " where dealer_cd    = ?) AND \n"
                    + "NOT EXISTS (select dealer_cd    from " + TableList.VA_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " where dealer_cd    = ?) if_not_exist";
            ps = tmgr.prepareStatement(query);
            if (POSValidator.validate(applicantCode, "AN")) {
                ps.setString(1, applicantCode);
                ps.setString(2, applicantCode);
                ps.setString(3, applicantCode);
                ps.setString(4, applicantCode);
                ps.setString(5, applicantCode);
            }

            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                status = rs.getBoolean("if_not_exist");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("PublicApplicationTradeCertImpl:checkForDuplicateApplicantCode:: Exception caught while checking for duplicate applicant code. [message{" + e.getMessage() + "}]");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return status;
    }

    private static String collectionToString(Collection collection, char delimiter) {
        String elementStr = "";
        for (Object element : collection) {
            if (element instanceof String) {
                String elementString = (String) element;
                elementStr += elementString + delimiter;
            } else {

                List elementList = (List) element;
                for (Object elementListObj : elementList) {
                    String elementListString = (String) elementListObj;
                    elementStr += elementListString + delimiter;
                }
            }
        }

        if (elementStr.charAt(elementStr.length() - 1) == delimiter) {
            elementStr = elementStr.substring(0, elementStr.length() - 1);
        }
        return elementStr;
    }

    public String insertIntoVaTradeCertificate(TransactionManager tmgr, PublicApplicationTradeCertDobj applicationTradeCertDobj) throws VahanException {

        PreparedStatement pstmt = null;
        String exceptionSegment = ": some reason ";
        String result = ": Application No:";
        String sql = "";

        try {

            String applNo = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());

            sql = "INSERT INTO " + TableList.VA_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + "(state_cd, off_cd, appl_no, dealer_cd, sr_no, vch_catg, no_of_vch, op_dt, applicant_type,trade_cert_appl_type";
            if (!CommonUtils.isNullOrBlank(applicationTradeCertDobj.getStatus())) {
                sql += ", status";
            }
            if (!CommonUtils.isNullOrBlank(applicationTradeCertDobj.getExtraVehiclesSoldLastYr())) {
                sql += ", extra_vch_sold_last_yr";
            }
            sql += ") VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?";
            if (!CommonUtils.isNullOrBlank(applicationTradeCertDobj.getStatus())) {
                sql += ", ?";
            }
            if (!CommonUtils.isNullOrBlank(applicationTradeCertDobj.getExtraVehiclesSoldLastYr())) {
                sql += ", ?";
            }
            sql += ")";

            pstmt = tmgr.prepareStatement(sql);



            int j = 1;

            applicationTradeCertDobj.setApplNo(applNo);

            if (pstmt != null) {
                pstmt.setString(j++, applicationTradeCertDobj.getStateCd());
                pstmt.setInt(j++, applicationTradeCertDobj.getOffCd());
                pstmt.setString(j++, applNo);
                pstmt.setString(j++, applicationTradeCertDobj.getApplicantCode());
                pstmt.setInt(j++, 1);
                pstmt.setString(j++, applicationTradeCertDobj.getVehClassAppliedFromApplicant());

                pstmt.setInt(j++, Integer.valueOf(applicationTradeCertDobj.getNoOfTradeCertificateReqFromApplicant()));
                pstmt.setString(j++, applicationTradeCertDobj.getApplicantCategory());
                pstmt.setString(j++, applicationTradeCertDobj.getNewRenewalTradeCert());
                if (!CommonUtils.isNullOrBlank(applicationTradeCertDobj.getStatus())) {
                    pstmt.setString(j++, applicationTradeCertDobj.getStatus());
                }
                if (!CommonUtils.isNullOrBlank(applicationTradeCertDobj.getExtraVehiclesSoldLastYr())) {
                    int totalVehiclesSoldLastYr = Integer.valueOf(applicationTradeCertDobj.getExtraVehiclesSoldLastYr());
                    pstmt.setInt(j++, (totalVehiclesSoldLastYr - Integer.valueOf(applicationTradeCertDobj.getNoOfCertificateGotLastYr())));
                }
                pstmt.addBatch();
            }


            if (pstmt != null) {
                int recordInserted = pstmt.executeUpdate();
                if (recordInserted > 0) {
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

    public String updateNoOfUsedVchIntoVtTradeCertificate(TransactionManager tmgr, PublicApplicationTradeCertDobj applicationTradeCertDobj) throws VahanException {

        PreparedStatement pstmt = null;
        String exceptionSegment = ": some reason ";
        String result = ": Application No:";
        String sql = "";
        String tradeCertNo = "";
        IssueTradeCertDobj dobjPrev = null;
        PreparedStatement psRecordsInsertedInVh = null;
        try {

            dobjPrev = new ApplicationTradeCertImpl().getVtTradeCertificateRecord(tmgr, applicationTradeCertDobj.getDealerFor(), applicationTradeCertDobj.getVehCatgFor());
            String sqlVhTcSave = "INSERT INTO " + TableList.VH_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + "(state_cd,off_cd,appl_no,dealer_cd,sr_no,vch_catg,no_of_vch,cert_no,valid_from,valid_upto,issue_dt,no_of_vch_used,applicant_type,moved_on,moved_by)\n"
                    + " SELECT state_cd,off_cd,appl_no,dealer_cd,sr_no,vch_catg,no_of_vch,cert_no,valid_from,valid_upto,issue_dt,no_of_vch_used,applicant_type,CURRENT_TIMESTAMP,'" + applicationTradeCertDobj.getEmpCd() + "'\n"
                    + " FROM " + TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " where " + TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + ".cert_no=?";
            psRecordsInsertedInVh = tmgr.prepareStatement(sqlVhTcSave);
            psRecordsInsertedInVh.setString(1, tradeCertNo);
            int recordsInsertedInVh = psRecordsInsertedInVh.executeUpdate();

            tradeCertNo = applicationTradeCertDobj.getTradeCertNo().toUpperCase();
            sql = "Update " + TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " SET no_of_vch_used = ? where cert_no=?";

            pstmt = tmgr.prepareStatement(sql);

            if (pstmt != null) {
                int noOfAllowedVch = new ApplicationTradeCertImpl().getNoOfVchFromVt(tradeCertNo);
                int diff = Integer.valueOf(applicationTradeCertDobj.getExtraVehiclesSoldLastYr()) - noOfAllowedVch;
                int noOfVchUsed = 0;
                if (diff >= 0) {
                    noOfVchUsed = noOfAllowedVch;
                } else {
                    noOfVchUsed = diff;
                }
                pstmt.setInt(1, noOfVchUsed);
                pstmt.setString(2, tradeCertNo);
                pstmt.addBatch();
            }

            if (pstmt != null) {
                int recordInserted = pstmt.executeUpdate();
                if (recordInserted > 0) {
                    result = "SUCCESS" + result + applicationTradeCertDobj.getTradeCertNo();
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

    public boolean checkTradeCertNoExistInVtTradeCert(String tradeCertNo) throws VahanException {
        boolean tradeCertExist = false;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;

        String sql = "Select * from " + TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " where cert_no='" + tradeCertNo.toUpperCase() + "'";

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

    public static boolean validateUniqueTinNo(String tinNo, String stateCd, String offCd) throws VahanException {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs;
        boolean status = false;
        String query = "";
        try {
            tmgr = new TransactionManager("validateUniqueTinNo");
            query = "Select tin_no from vm_dealer_mast where tin_no = ? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(query);
            if (POSValidator.validate(tinNo, "AN")) {
                ps.setString(1, tinNo);
            }
            if (POSValidator.validate(stateCd, "A")) {
                ps.setString(2, stateCd);
            }
            if (POSValidator.validate(offCd, "N")) {
                ps.setInt(3, Integer.valueOf(offCd));
            }
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                status = true;
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
        return status;
    }

    public static Timestamp getAfterOneYearTimeStamp(Date currentDate) {
        return getDateToTimeStamp(getAfterOneYearDateDDMMYYYHHMMSS(currentDate));
    }

    public static String getAfterOneYearDateDDMMYYYHHMMSS(Date currentDate) {
        java.util.Calendar cal = java.util.Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(currentDate);
        cal.add(java.util.Calendar.YEAR, 1);
        cal.add(java.util.Calendar.DATE, -1);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(cal.getTime());
    }

    /**
     *
     * @param string date in format as dd-MM-yyyy hh:mm:ss a
     * @return TimeStamp
     */
    public static Timestamp getDateToTimeStamp(String strDt) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
        Timestamp timeStampDate = null;
        Date date = null;
        try {
            date = sdf1.parse(strDt);
        } catch (ParseException ex) {
        }

        if (date != null) {
            timeStampDate = new Timestamp(date.getTime());
        }
        return timeStampDate;
    }

    //Loads the Master Tables in the respective MasterTableDO
    public synchronized static MasterTables loadMasterTables() {

        // Create an empty MasterTables object
        MasterTables mtables = new MasterTables();

        // Where I Am
        String whereiam = "PublicApplicationTradeCertImpl.loadMasterTables()";

        // Load the tables and convert to the java objects
        try {
            LOGGER.info("........... Creating MasterTables object ...");
            mtables.VM_MAKER = makeMasterTableDO(TableList.VM_MAKER_TRADE, "code", "descr", "VM_MAKER");//
            mtables.VM_VCH_CATG = makeMasterTableDO(TableList.VM_VCH_CATG, "catg", "catg_desc", "VM_VCH_CATG");
            mtables.VM_VCH_CLASS = makeMasterTableDO(TableList.VM_VCH_CLASS, "vch_class_cd", "vch_class_descr", "VM_VCH_CLASS");
            mtables.TM_OFFICE = makeMasterTableDO(TableList.TM_OFFICE, "off_cd", "off_name", "TM_OFFICE");
            //......................................................................
            mtables.fillMasterTableVector();

            LOGGER.info("...DONE!... Creating MasterTables object ...");
        } catch (SQLException e) {
            LOGGER.error(e);
        } catch (Exception e) {
            LOGGER.error(e);
        }

        return mtables;
    }

    /**
     * Make DO for a given master table.
     *
     * @param tableName The master table name
     *
     * @return The MasterTableDO object
     *
     * @throws SQLException
     * @throws VahanException
     */
    public static MasterTableDO makeMasterTableDO(String tableName, String codeName, String descName, String tableLable)
            throws SQLException, Exception {

        String[][] metadata = null;
        String[][] data = null;
        Map dataMap = new LinkedHashMap();
        TransactionManager tmg = null;
        String query = "";
        String sqlRowCount = "";
        if (tableName.equals("vahan4.vm_maker")) {

            query = "SELECT  * FROM " + tableName + " where show_in_rto = 't' ORDER BY " + descName;
        } else {
            query = "SELECT  * FROM " + tableName + " ORDER BY " + descName;
        }
        //     LOGGER.info("[Master Query] " + query);
        String whereiam = "PublicApplicationTradeCertImpl.makeTableDO()";
        try {
            tmg = new TransactionManager("rowCount");
            if (tableName.equals("vahan4.vm_maker")) {
                sqlRowCount = "SELECT COUNT(*) FROM " + tableName + " where show_in_rto = 't'";
            } else {
                sqlRowCount = "SELECT COUNT(*) FROM " + tableName;
            }
            PreparedStatement ps = tmg.prepareStatement(sqlRowCount);
            javax.sql.RowSet rsC = tmg.fetchDetachedRowSet();
            int rowsCount = 0;
            if (rsC.next()) {
                rowsCount = rsC.getInt(1);
            }

            int nCols = 0;

            //................................................................
            // Get the whole table and store the data into this object.
            // NOTE:Though the master table can contain columns more than 2,
            // but we will store only the first two columns
            //................................................................
            tmg = new TransactionManager(whereiam);
            ps = tmg.prepareStatement(query);
            rsC = tmg.fetchDetachedRowSet();

            // Get the number of columns
            java.sql.ResultSetMetaData rsmd = rsC.getMetaData();
            nCols = rsmd.getColumnCount();

            // Get the metadata
            metadata = new String[4][nCols];
            for (int i = 1; i <= nCols; i++) {
                // Get the designated column's name
                metadata[0][i - 1] = rsmd.getColumnName(i);
                // Retrieves the designated column's database-specific type name
                metadata[1][i - 1] = rsmd.getColumnTypeName(i);
                // Retrieves the designated column's SQL type
                metadata[2][i - 1] = "" + rsmd.getColumnType(i);
                // Retrieves the designated column's normal maximum width in characters
                metadata[3][i - 1] = "" + rsmd.getColumnDisplaySize(i);
            }
            // Get the table data
            if (rowsCount > 0) {

                data = new String[rowsCount][nCols];
                int row = 0;
                while (rsC.next()) {
                    row++;
                    for (int col = 1; col <= nCols; col++) {
                        data[row - 1][col - 1] = rsC.getString(col);
                    }
                    dataMap.put(rsC.getString(codeName), rsC.getString(descName));
                }
            }

            if (nCols < 2) {
                throw new Exception("Master table \"" + tableName + "\" contains less than 2 columns!");
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        String[][] data_AO = null;
        data_AO = data;
        return new MasterTableDO(tableName, metadata, data, data_AO, dataMap, codeName, descName, tableLable);
    }

    public static int randLong(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public static PublicApplicationTradeCertDobj getTradeApplicationDataFromTradeCertificateNumber(String tcNumber) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        RowSet rs1 = null;
        PublicApplicationTradeCertDobj dobj = null;
        boolean recordFound = false;
        String sql = "";
        boolean tradeCertExist = false;
        try {
            tmgr = new TransactionManager("getTradeApplicationDataFromTradeCertificateNumber");

            sql = "select vt.appl_no,vt.dealer_cd,vt.vch_catg,vm.vch_class_descr,vt.no_of_vch,vt.sr_no,vt.cert_no,vt.valid_upto,vt.applicant_type "
                    + " from " + TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " vt INNER JOIN " + TableList.VM_VCH_CLASS_ONLINE_SCHEMA_TC
                    + " vm on vt.vch_catg::numeric = vm.vch_class_cd::numeric where vt.cert_no =?";
            ps = tmgr.prepareStatement(sql);
            if (POSValidator.validate(tcNumber, "ANWS")) {
                ps.setString(1, tcNumber);
            }
            rs = tmgr.fetchDetachedRowSet();

            {    ////// CASE : IF THE ENTRY OF NEW APPLICANT HAS BEEN MADE THROUGH APPLICATION INWARD FORM 'form_tc_dealer_application' 
                if (rs.next()) {
                    tradeCertExist = true;
                    sql = " select b.applicant_state_cd, b.applicant_off_cd, b.applicant_name,"
                            + " b.applicant_relation, b.applicant_address1, b.applicant_address2,b.applicant_district,b.applicant_pincode,"
                            + " b.applicant_mobile_no,b.applicant_showroom_name,b.applicant_showroom_address1,b.applicant_showroom_address2,b.applicant_showroom_district,"
                            + " b.applicant_showroom_pincode,b.applicant_loi_auth_no,b.applicant_loi_auth_date ,b.applicant_email, b.applicant_maker_list,"
                            + " b.applicant_veh_class_list, b.applicant_tin_no,b.applicant_category,b.applicant_branch_name,b.applicant_landline_no"
                            + " from " + TableList.VM_APPLICANT_MAST_APPL_ONLINE_SCHEMA_TC + " b where b.applicant_cd=? ";


                    ps = tmgr.prepareStatement(sql);

                    ps.setString(1, rs.getString("dealer_cd"));

                    LOGGER.info("[Master Query]: " + ps.toString());
                    rs1 = tmgr.fetchDetachedRowSet();
                    if (rs1.next()) {
                        recordFound = true;
                        dobj = new PublicApplicationTradeCertDobj();
                        dobj.setApplicantName(rs1.getString("applicant_name"));
                        if (!CommonUtils.isNullOrBlank(rs1.getString("applicant_relation"))) {
                            dobj.setIndividualOrCompany("Individual");
                        } else {
                            dobj.setIndividualOrCompany("Company");
                        }
                        dobj.setApplicantRelation(rs1.getString("applicant_relation"));
                        dobj.setApplicantAddress1(rs1.getString("applicant_address1"));
                        if (new String(rs1.getString("applicant_address2")) != null) {
                            dobj.setApplicantAddress2(rs1.getString("applicant_address2"));
                        }
                        dobj.setApplicantPincode(rs1.getString("applicant_pincode"));
                        dobj.setApplicantState(rs1.getString("applicant_state_cd"));
                        dobj.setApplicantMobileNumber(rs1.getString("applicant_mobile_no"));
                        dobj.setApplicantMailId(rs1.getString("applicant_email"));
                        dobj.setApplicantDistrict(rs1.getString("applicant_district"));
                        dobj.setShowRoomName(rs1.getString("applicant_showroom_name"));
                        dobj.setShowRoomAddress1(rs1.getString("applicant_showroom_address1"));
                        dobj.setShowRoomAddress2(rs1.getString("applicant_showroom_address2"));
                        dobj.setShowRoomDistrict(rs1.getString("applicant_showroom_district"));
                        dobj.setShowRoomPincode(rs1.getString("applicant_showroom_pincode"));
                        dobj.setLOIAuthorisationNo(rs1.getString("applicant_loi_auth_no"));
                        dobj.setLOIAuthorisationDate(rs1.getDate("applicant_loi_auth_date"));
                        dobj.setApplicantOffice(rs1.getString("applicant_off_cd"));
                        dobj.setOffCd(Integer.valueOf(rs1.getString("applicant_off_cd")));
                        dobj.setStateCd(rs1.getString("applicant_state_cd"));
                        dobj.setStateName(ServerUtil.getStateNameByStateCode(rs1.getString("applicant_state_cd")));
                        dobj.setApplNo(rs.getString("appl_no"));
                        dobj.setApplicantCode(rs.getString("dealer_cd"));
                        dobj.getVehClassAffiliatedByApplicantList().clear();
                        String[] affiliatedVehClassesCodeArr = rs1.getString("applicant_veh_class_list").split(",");
                        for (String vehClassesCode : affiliatedVehClassesCodeArr) {
                            dobj.getVehClassAffiliatedByApplicantList().add(new ApplicationTradeCertImpl().getVehClassDesc(vehClassesCode));
                            dobj.getSelectedVehClassMapFromSession().put(new ApplicationTradeCertImpl().getVehClassDesc(vehClassesCode), vehClassesCode);
                        }
                        dobj.getVehClassAffiliatedByApplicantListForEx().clear();
                        dobj.getVehClassAffiliatedByApplicantListForEx().addAll(dobj.getVehClassAffiliatedByApplicantList());
                        dobj.getSelectedApplicantManufacturerList().clear();
                        String[] applicantSelectedManufactureCodeArr = rs1.getString("applicant_maker_list").split(",");
                        for (String makerCode : applicantSelectedManufactureCodeArr) {
                            if (makerCode != null && !makerCode.equals("")) {
                                dobj.getSelectedApplicantManufacturerList().add(new ApplicationTradeCertImpl().getMakerdescr(makerCode));
                                dobj.getSelectedManufacturerMapFromSession().put(new ApplicationTradeCertImpl().getMakerdescr(makerCode), makerCode);
                            }
                        }
                        dobj.getSelectedApplicantManufacturerListForEx().clear();
                        dobj.getSelectedApplicantManufacturerListForEx().addAll(dobj.getSelectedApplicantManufacturerList());
                        dobj.setApplicantTinNo(rs1.getString("applicant_tin_no"));
                        dobj.setApplicantCategory(rs1.getString("applicant_category"));
                        dobj.setApplicantBranchName(rs1.getString("applicant_branch_name"));
                        dobj.setLandLineNumber(rs1.getString("applicant_landline_no"));
                        dobj.setNoOfTradeCertificateReqFromApplicant(rs.getString("no_of_vch") + "");
                        dobj.setNoOfCertificateGotLastYr(rs.getString("no_of_vch") + "");
                        dobj.setVehClassAppliedFromApplicant(rs.getString("vch_catg"));
                        dobj.setSrNo(rs.getInt("sr_no") + "");
                        dobj.setVehCatgName(rs.getString("vch_class_descr"));
                        dobj.setTradeCertNo(rs.getString("cert_no").toUpperCase());
                        dobj.setValidUpto(rs.getDate("valid_upto"));
                        dobj.setValidUptoDateString(DateUtil.formatDate("dd-MMM-yyyy", rs.getDate("valid_upto")));
                        //////////////// FOR CHECKING AND SETTING EXPIRED CONDITION (RENEW CASE) 
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(dobj.getValidUpto());
                        cal.add(Calendar.DATE, 1);
                        cal.add(Calendar.MONTH, -1);
                        //Trade Condition 
                        if (dobj.getStateCd().equals("KA")) {
                            cal.add(Calendar.MONTH, -1);
                        }
                        //trade condition end
                        Date tradeCertificateExpireDate = cal.getTime();

                        if (new Date().after(tradeCertificateExpireDate)) {
                            dobj.setExpired(true);
                        }
                    }
                }
            }
            {  //// ////// CASE : IF THE ENTRY OF NEW APPLICANT HAS BEEN MADE THROUGH BACKLOG FORM 'form_tc_dealer_backlog_application' 
                if (!recordFound) {
                    //a.trade_cert_appl_type,


                    if (tradeCertExist && rs.getString("applicant_type").equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER)) {
                        sql = "select b.state_cd, b.off_cd, b.dealer_name,"
                                + " b.applicant_relation, b.d_add1, b.d_add2, b.d_district , b.d_pincode,"
                                + " b.contact_no,c.showroom_name,c.showroom_add1,c.showroom_add2,c.showroom_district,"
                                + " c.showroom_pincode,c.loi_auth_no,c.loi_auth_date , b.email_id, b.maker,"
                                + " b.vch_class, b.tin_no from "
                                + TableList.VM_DEALER_MAST + " b LEFT JOIN " + TableList.VM_DEALER_OTHER_DETAILS + " c ON b.dealer_cd::text = c.dealer_cd::text "
                                + " where b.dealer_cd =?";

                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, rs.getString("dealer_cd"));

                        LOGGER.info("[Master Query]: " + ps.toString());
                        rs1 = tmgr.fetchDetachedRowSet();

                        if (rs1.next()) {
                            recordFound = true;
                            dobj = new PublicApplicationTradeCertDobj();
                            dobj.setApplicantName(rs1.getString("dealer_name"));
                            if (!CommonUtils.isNullOrBlank(rs1.getString("applicant_relation"))) {
                                dobj.setIndividualOrCompany("Individual");
                            } else {
                                dobj.setIndividualOrCompany("Company");
                            }
                            dobj.setApplicantRelation(rs1.getString("applicant_relation"));
                            dobj.setApplicantAddress1(rs1.getString("d_add1"));
                            if (new String(rs1.getString("d_add2")) != null) {
                                dobj.setApplicantAddress2(rs1.getString("d_add2"));
                            }
                            dobj.setApplicantPincode(rs1.getString("d_pincode"));
                            dobj.setApplicantState(rs1.getString("state_cd"));
                            dobj.setApplicantMobileNumber(rs1.getString("contact_no"));
                            dobj.setApplicantMailId(rs1.getString("email_id"));
                            dobj.setApplicantDistrict(rs1.getString("d_district"));
                            dobj.setShowRoomName(rs1.getString("showroom_name"));
                            dobj.setShowRoomAddress1(rs1.getString("showroom_add1"));
                            dobj.setShowRoomAddress2(rs1.getString("showroom_add2"));
                            dobj.setShowRoomDistrict(rs1.getString("showroom_district"));
                            dobj.setShowRoomPincode(rs1.getString("showroom_pincode"));
                            dobj.setLOIAuthorisationNo(rs1.getString("loi_auth_no"));
                            dobj.setLOIAuthorisationDate(rs1.getDate("loi_auth_date"));
                            dobj.setApplicantOffice(rs1.getString("off_cd"));
                            dobj.setOffCd(Integer.valueOf(rs1.getString("off_cd")));
                            dobj.setStateCd(rs1.getString("state_cd"));
                            dobj.setStateName(ServerUtil.getStateNameByStateCode(rs1.getString("state_cd")));
                            dobj.setApplNo(rs.getString("appl_no"));
                            dobj.setApplicantCode(rs.getString("dealer_cd"));
                            dobj.getVehClassAffiliatedByApplicantList().clear();
                            String[] affiliatedVehClassesCodeArr = rs1.getString("vch_class").split(",");
                            for (String vehClassesCode : affiliatedVehClassesCodeArr) {
                                dobj.getVehClassAffiliatedByApplicantList().add(new ApplicationTradeCertImpl().getVehClassDesc(vehClassesCode));
                                dobj.getSelectedVehClassMapFromSession().put(new ApplicationTradeCertImpl().getVehClassDesc(vehClassesCode), vehClassesCode);
                            }
                            dobj.getVehClassAffiliatedByApplicantListForEx().clear();
                            dobj.getVehClassAffiliatedByApplicantListForEx().addAll(dobj.getVehClassAffiliatedByApplicantList());
                            dobj.getSelectedApplicantManufacturerList().clear();
                            String[] applicantSelectedManufactureCodeArr = rs1.getString("maker").split(",");
                            for (String makerCode : applicantSelectedManufactureCodeArr) {
                                if (makerCode != null && !makerCode.equals("")) {
                                    dobj.getSelectedApplicantManufacturerList().add(new ApplicationTradeCertImpl().getMakerdescr(makerCode));
                                    dobj.getSelectedManufacturerMapFromSession().put(new ApplicationTradeCertImpl().getMakerdescr(makerCode), makerCode);
                                }
                            }
                            dobj.getSelectedApplicantManufacturerListForEx().clear();
                            dobj.getSelectedApplicantManufacturerListForEx().addAll(dobj.getSelectedApplicantManufacturerList());
                            dobj.setApplicantTinNo(rs1.getString("tin_no"));

                            dobj.setNoOfTradeCertificateReqFromApplicant(rs.getString("no_of_vch") + "");
                            dobj.setNoOfCertificateGotLastYr(rs.getString("no_of_vch") + "");
                            dobj.setVehClassAppliedFromApplicant(rs.getString("vch_catg"));
                            dobj.setSrNo(rs.getInt("sr_no") + "");
                            dobj.setVehCatgName(rs.getString("vch_class_descr"));
                            dobj.setTradeCertNo(rs.getString("cert_no").toUpperCase());
                            dobj.setValidUpto(rs.getDate("valid_upto"));
                            dobj.setValidUptoDateString(DateUtil.formatDate("dd-MMM-yyyy", rs.getDate("valid_upto")));
                            dobj.setApplicantCategory(rs.getString("applicant_type"));

                            //////////////// FOR CHECKING AND SETTING EXPIRED CONDITION (RENEW CASE) 
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(dobj.getValidUpto());
                            cal.add(Calendar.DATE, 1);
                            cal.add(Calendar.MONTH, -1);
                            //Trade Condition 
                            if (dobj.getStateCd().equals("KA")) {
                                cal.add(Calendar.MONTH, -1);
                            }
                            //trade condition end
                            Date tradeCertificateExpireDate = cal.getTime();

                            if (new Date().after(tradeCertificateExpireDate)) {
                                dobj.setExpired(true);
                            }
                        }
                    } else if (tradeCertExist) {
                        sql = " select b.state_cd, b.off_cd, b.financer_name,"
                                + " b.branch_name, b.branch_add1, b.branch_add2, b.branch_district , b.branch_pincode,"
                                + " b.contact_no,b.email_id,b.landline_no,"
                                + " b.vch_class from " + TableList.VM_FINANCIER_TC + " b  where b.financer_cd =? ";

                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, rs.getString("dealer_cd"));
                        LOGGER.info("[Master Query]: " + ps.toString());
                        rs1 = tmgr.fetchDetachedRowSet();

                        if (rs1.next()) {
                            recordFound = true;
                            dobj = new PublicApplicationTradeCertDobj();
                            dobj.setApplicantName(rs1.getString("financer_name"));
                            dobj.setApplicantBranchName(rs1.getString("branch_name"));
                            dobj.setApplicantAddress1(rs1.getString("branch_add1"));
                            if (new String(rs1.getString("branch_add2")) != null) {
                                dobj.setApplicantAddress2(rs1.getString("branch_add2"));
                            }
                            dobj.setApplicantPincode(rs1.getString("branch_pincode"));
                            dobj.setApplicantState(rs1.getString("state_cd"));
                            dobj.setApplicantMobileNumber(rs1.getString("contact_no"));
                            dobj.setLandLineNumber(rs1.getString("landline_no"));
                            dobj.setApplicantMailId(rs1.getString("email_id"));
                            dobj.setApplicantDistrict(rs1.getString("branch_district"));
                            dobj.setApplicantOffice(rs1.getString("off_cd"));
                            dobj.setOffCd(Integer.valueOf(rs1.getString("off_cd")));
                            dobj.setStateCd(rs1.getString("state_cd"));
                            dobj.setStateName(ServerUtil.getStateNameByStateCode(rs1.getString("state_cd")));
                            dobj.setApplNo(rs.getString("appl_no"));
                            dobj.setApplicantCode(rs.getString("dealer_cd"));
                            dobj.getVehClassAffiliatedByApplicantList().clear();
                            String[] affiliatedVehClassesCodeArr = rs1.getString("vch_class").split(",");
                            for (String vehClassesCode : affiliatedVehClassesCodeArr) {
                                dobj.getVehClassAffiliatedByApplicantList().add(new ApplicationTradeCertImpl().getVehClassDesc(vehClassesCode));
                                dobj.getSelectedVehClassMapFromSession().put(new ApplicationTradeCertImpl().getVehClassDesc(vehClassesCode), vehClassesCode);
                            }
                            dobj.getVehClassAffiliatedByApplicantListForEx().clear();
                            dobj.getVehClassAffiliatedByApplicantListForEx().addAll(dobj.getVehClassAffiliatedByApplicantList());

                            dobj.setNoOfTradeCertificateReqFromApplicant(rs.getString("no_of_vch") + "");
                            dobj.setNoOfCertificateGotLastYr(rs.getString("no_of_vch") + "");
                            dobj.setVehClassAppliedFromApplicant(rs.getString("vch_catg"));
                            dobj.setSrNo(rs.getInt("sr_no") + "");
                            dobj.setVehCatgName(rs.getString("vch_class_descr"));
                            dobj.setTradeCertNo(rs.getString("cert_no").toUpperCase());
                            dobj.setValidUpto(rs.getDate("valid_upto"));
                            dobj.setValidUptoDateString(DateUtil.formatDate("dd-MMM-yyyy", rs.getDate("valid_upto")));
                            dobj.setApplicantCategory(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER);

                            //////////////// FOR CHECKING AND SETTING EXPIRED CONDITION (RENEW CASE) 
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(dobj.getValidUpto());
                            cal.add(Calendar.DATE, 1);
                            cal.add(Calendar.MONTH, -1);
                            //Trade Condition 
                            if (dobj.getStateCd().equals("KA")) {
                                cal.add(Calendar.MONTH, -1);
                            }
                            //trade condition end
                            Date tradeCertificateExpireDate = cal.getTime();

                            if (new Date().after(tradeCertificateExpireDate)) {
                                dobj.setExpired(true);
                            }
                        }
                    }


                }
            }

        } catch (SQLException e) {
            LOGGER.error(e);
            throw new VahanException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e);
            throw new VahanException(e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return dobj;
    }

    public static int fetchDistrictCodeFromOfficeCd(String stateCd, int offCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int distCd = -1;
        try {
            tmgr = new TransactionManager("fetchDistrictCodeFromOfficeCd");
            String sql = "SELECT dist_cd FROM " + TableList.TM_DISTRICT_MAPPING_ONLINE_SCHEMA_TC + " WHERE state_cd = ? and off_cd = ?";

            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, stateCd);
            pstmt.setInt(2, offCd);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                distCd = rs.getInt("dist_cd");
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(ex.getMessage());
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
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return distCd;
    }

    public static List getVehClassCategoryMappingsInList() throws VahanException {
        TransactionManager tmg = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String query = null;
        List vehClassCategoryList = new ArrayList();
        query = "select vm_vch_class.vch_class_descr, vm_vch_catg.catg, vm_vch_catg.catg_desc from onlineschema_tc.vch_class_catg_mapping "
                + " left outer join onlineschema_tc.vm_vch_class vm_vch_class on vm_vch_class.vch_class_cd = vch_class_catg_mapping.vch_class_cd "
                + " left outer join vahan4.vm_vch_catg vm_vch_catg on vm_vch_catg.catg = vch_class_catg_mapping.vch_catg"
                + " where length(vch_class_descr)>0";

        try {
            tmg = new TransactionManager("getVehClassCategoryMappingsInList");
            ps = tmg.prepareStatement(query);
            rs = tmg.fetchDetachedRowSet();
            int srNo = 0;
            VehicleClassCategoryMappingDobj vehicleClassCategoryMappingDobj = null;
            while (rs.next()) {
                srNo = vehClassCategoryList.size() + 1;
                vehicleClassCategoryMappingDobj = new VehicleClassCategoryMappingDobj(srNo, rs.getString("vch_class_descr"), rs.getString("catg_desc"), rs.getString("catg"));
                vehClassCategoryList.add(vehicleClassCategoryMappingDobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new VahanException(e.getMessage());
        } finally {
            if (tmg != null) {
                try {
                    tmg.release();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
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

    public static boolean applcationPendingForApplicantAndVehicleClass(PublicApplicationTradeCertDobj dobj) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        String sql = "select appl_no from " + TableList.VA_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC + " where dealer_cd = ? and vch_catg = ? ";
        try {
            tmgr = new TransactionManager("ApplicationTradeCertImpl.sameApplcationPendingForApplicantAndVehicleClass()");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, dobj.getApplicantCode());
            pstmt.setString(2, dobj.getVehClassAppliedFromApplicant());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return true;
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
        return false;
    }
}
