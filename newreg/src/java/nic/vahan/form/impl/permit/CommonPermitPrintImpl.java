/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitCheckDetailsDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import static nic.vahan.form.impl.fancy.AdvanceRegnFeeImpl.replaceTagValues;

/**
 *
 * @author Administrator
 */
public class CommonPermitPrintImpl {

    private static final Logger LOGGER = Logger.getLogger(CommonPermitPrintImpl.class);

    /**
     * Do not include any use global static variable. use only local variable in
     * static method.
     */
    private CommonPermitPrintImpl() {
    }

    /**
     * @param Tmgr = TransactionManager
     * @param permitType = Permit Type int value
     * @param purCd = Purpose Code int value
     */
    public static String getPermitDocId(TransactionManager tmgr, int permitType, int purCd, String stateCd) throws VahanException {
        String docId = null;
        if (purCd == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD && !(stateCd.equalsIgnoreCase("DL") && permitType == Integer.parseInt(TableConstants.NATIONAL_PERMIT))) {
            permitType = 0;
        }
        try {
            String sqlQuery;
            PreparedStatement ps;
            RowSet rs;
            sqlQuery = "select doc_id from " + TableList.VM_PERMIT_DOC_STATE_MAP + " where pmt_type =? AND pur_cd =? AND STATE_CD=?";
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setInt(1, permitType);
            ps.setInt(2, purCd);
            ps.setString(3, stateCd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                docId = rs.getString("doc_id");
            }
            return docId;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    /**
     * Doc id have a comma separate value like 1,2,3 function gave me
     * ('1','2','3') like this formate
     */
    public static String getPermitDocIdForQuery(String docId) throws VahanException {
        String docIdforQuery = null;
        try {
            if (!docId.isEmpty()) {
                String[] temp = docId.split(",");
                StringBuilder stringBuilder = new StringBuilder();
                for (int j = 0; j < temp.length; j++) {
                    stringBuilder.append("(").append("'").append(temp[j]).append("'").append("),");
                }
                docIdforQuery = stringBuilder.substring(0, stringBuilder.length() - 1);
            }
            return docIdforQuery;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public static void insertVaPermitPrint(TransactionManager tmgr, String[] beanData) throws VahanException {
        String sqlQuery;
        PreparedStatement ps;
        RowSet rs, rs1;
        try {
            sqlQuery = "select * from " + TableList.VM_PERMIT_DOCUMENTS + " where doc_id in (" + beanData[0] + ")";
            tmgr.prepareStatement(sqlQuery);
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                sqlQuery = "Select * from " + TableList.VA_PERMIT_PRINT + " where appl_no = ? AND doc_id = ?";
                ps = tmgr.prepareStatement(sqlQuery);
                ps.setString(1, beanData[1]);
                ps.setString(2, rs.getString("doc_id"));
                rs1 = tmgr.fetchDetachedRowSet_No_release();
                if (rs1.next()) {
                    throw new VahanException("Regn_no : " + beanData[2] + " pending in Permit print Stage. Please print your permit first");
                } else {
                    sqlQuery = "INSERT INTO " + TableList.VA_PERMIT_PRINT + "(\n"
                            + "            appl_no, regn_no, doc_id, op_dt)\n"
                            + "    VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
                    ps = tmgr.prepareStatement(sqlQuery);
                    ps.setString(1, beanData[1]);
                    ps.setString(2, beanData[2]);
                    ps.setString(3, rs.getString("doc_id"));
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public static int[] makeIntegerArray(String code) {
        int[] codeInIngeter = null;
        if (!(code.isEmpty() || CommonUtils.isNullOrBlank(code))) {
            String[] codeInString = code.split(",");
            codeInIngeter = new int[codeInString.length];
            for (int i = 0; i < codeInString.length; i++) {
                codeInIngeter[i] = Integer.valueOf(codeInString[i]);
            }
        }
        return codeInIngeter;
    }

    public static int getOffCdPermissionByOfficer(int off_cd, String state_cd) {
        int newOffCd = 0;
        String Query = "";
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManagerReadOnly("Get OffCd Permission By Officer");
            Query = "SELECT * FROM " + TableList.VM_OFF_ALLOTMENT + " where ? = ANY(STRING_TO_ARRAY(allotted_off_cd,',')::integer[]) AND STATE_CD = ? order by 2";
            ps = tmgr.prepareStatement(Query);
            ps.setInt(1, off_cd);
            ps.setString(2, state_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                newOffCd = rs.getInt("off_cd");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
            }
        }
        return newOffCd;
    }

    public static int[] getPermitIssuingOffCd(int off_cd, String state_cd) {
        int[] newOffCd = new int[50];
        int iCtr = 0;
        String Query = "";
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManagerReadOnly("getPermitIssuingOffCd");
            Query = "SELECT * FROM " + TableList.VM_OFF_ALLOTMENT + " where ? = ANY(STRING_TO_ARRAY(allotted_off_cd,',')::integer[]) AND STATE_CD = ? order by 2";
            ps = tmgr.prepareStatement(Query);
            ps.setInt(1, off_cd);
            ps.setString(2, state_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                newOffCd[iCtr] = rs.getInt("off_cd");
                iCtr++;
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
        return newOffCd;
    }

    public static Date getDateDD_MMM_YYYY(String dateInString) {
        String datePattern = "\\d{2}-\\d{2}-\\d{4}";
        Date date = null;
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            if (!CommonUtils.isNullOrBlank(dateInString)) {
                Boolean isDateddMMyyyy = dateInString.matches(datePattern);
                if (isDateddMMyyyy) {
                    date = formatter1.parse(dateInString);
                } else {
                    date = formatter2.parse(dateInString);
                }
            }
        } catch (ParseException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return date;
    }

    public static String getDateStringDD_MMM_YYYY(Date date) {
        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy ");
        String reportDate = df.format(date);
        return reportDate;
    }

    public static String getValueForSelectedList(ArrayList arrayList, String selectedLabel) {
        String value = null;
        for (int i = 0; i < arrayList.size(); i++) {
            SelectItem st = (SelectItem) arrayList.get(i);
            if (st != null && st.getLabel().trim().equalsIgnoreCase(selectedLabel.trim())) {
                value = String.valueOf(st.getValue());
                break;
            }
        }
        return value;
    }

    public static String checkVehicleAge(Date replaceDate, int purCode) {
        String msg = null;
        try {
            if (purCode == TableConstants.VM_PMT_FRESH_PUR_CD) {
                if (replaceDate.getTime() < (new Date()).getTime()) {
                    msg = "Vehicle age Expired";
                }
            } else if (purCode == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
                if (replaceDate.getTime() < (new Date()).getTime()) {
                    msg = "Vehicle age Expired";
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return msg;
    }

    /**
     * @param stateCd = State Code (String)
     * @param purCd = Purpose Code (Int)
     * @param pmtType = Permit Type (String)
     * @param vhClass = Vehicle Class (String)
     * @param seatCap = Seating Capacity (String)
     * @param unldWt = Unladen Weight (String)
     * @param ldWt = laden Weight (String)
     * @param applNo = Application No (String)
     */
    public static String getWhereClause(TransactionManager tmgr, String stateCd, int purCd,
            int pmtType, int vhClass, int seatCap, int unldWt, int ldWt, String applNo, int pmtCatg) throws VahanException {
        String whereClause = null;
        boolean isStateCdNeeded = false;
        boolean isPurCdNeeded = false;
        PreparedStatement ps;
        try {
            String Query = "SELECT * FROM permit.vm_permit_fee_state_configuration WHERE state_cd=? AND pur_cd=? AND pmt_type=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, stateCd);
            ps.setInt(2, purCd);
            ps.setInt(3, pmtType);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (!CommonUtils.isNullOrBlank(stateCd) && !"-1".equalsIgnoreCase(stateCd)) {
                    isStateCdNeeded = true;
                }
                if (purCd != 0) {
                    isPurCdNeeded = true;
                }
                if (isPurCdNeeded || isStateCdNeeded) {
                    whereClause = " WHERE ";
                }

                if (!CommonUtils.isNullOrBlank(whereClause)) {
                    if (isStateCdNeeded) {
                        whereClause += "STATE_CD = '" + stateCd + "'";
                    }

                    if (isPurCdNeeded) {
                        whereClause += " AND pur_cd = " + purCd;
                    }

                    if (rs.getBoolean("pmt_type_flag")) {
                        whereClause += " AND pmt_type = " + pmtType;
                    }

                    if (rs.getBoolean("pmt_catg_flag")) {
                        whereClause += " AND pmt_catg = " + pmtCatg;
                    }

                    if (rs.getBoolean("vh_class_flag")) {
                        whereClause += " AND l_vh_class <= " + vhClass + " AND u_vh_class >= " + vhClass;
                    }

                    if (rs.getBoolean("per_region_flag") && !CommonUtils.isNullOrBlank(applNo)) {
                        int record = getHowManyRegionContain(applNo, tmgr);
                        whereClause += " AND per_region = '" + record + "'";
                    }

                    if (rs.getBoolean("seat_cap_flag")) {
                        whereClause += " AND l_seat_cap <= " + seatCap + " AND u_seat_cap >= " + seatCap;
                    }

                    if (rs.getBoolean("unld_wt_flag")) {
                        whereClause += " AND l_unld_wt <= " + unldWt + " AND u_unld_wt >= " + unldWt;
                    }

                    if (rs.getBoolean("ld_wt_flag")) {
                        whereClause += " AND l_ld_wt <= " + ldWt + " AND u_ld_wt >= " + ldWt;
                    }
                }
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
//        finally {
//            if (whereClause == null) {
//                LOGGER.info("Applno: " + applNo + " {" + stateCd + "," + purCd + "," + pmtType + ")");
//            }
//        }
        return whereClause;
    }

    public static int getHowManyRegionContain(String applNo, TransactionManager tmgr) throws VahanException {
        int record = 0;
        String Query;
        PreparedStatement ps;
        RowSet rs;
        RowSet rset;
        try {
            Query = "SELECT region_covered FROM " + TableList.VA_PERMIT + " where appl_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (!CommonUtils.isNullOrBlank(rs.getString("region_covered"))) {
                    String[] region = rs.getString("region_covered").split(",");
                    for (int i = 0; i < region.length; i++) {
                        Query = "SELECT regions_covered FROM " + TableList.VM_REGION + " where region_cd = ? and state_cd = ? and off_cd = ?";
                        ps = tmgr.prepareStatement(Query);
                        ps.setInt(1, Integer.parseInt(region[i].trim()));
                        ps.setString(2, Util.getUserStateCode());
                        ps.setInt(3, Util.getUserOffCode());
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            record = rs.getInt("regions_covered");
                        }
                    }
                } else {
                    Query = "SELECT route_cd FROM " + TableList.va_permit_route + " where appl_no = ?";
                    ps = tmgr.prepareStatement(Query);
                    ps.setString(1, applNo);
                    rset = tmgr.fetchDetachedRowSet_No_release();
                    while (rset.next()) {
                        Query = "SELECT regions_covered FROM " + TableList.VM_ROUTE_MASTER + " where code = ?  and state_cd = ? and off_cd = ?";
                        ps = tmgr.prepareStatement(Query);
                        ps.setString(1, rset.getString("route_cd"));
                        ps.setString(2, Util.getUserStateCode());
                        ps.setInt(3, Util.getUserOffCode());
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            record = rs.getInt("regions_covered");
                        }
                    }
                }
            }
        } catch (SQLException | NumberFormatException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return record;
    }

    public static String getLableForSelectedList(ArrayList arrayList, String selectedValue) {
        String lable = null;
        for (int i = 0; i < arrayList.size(); i++) {
            SelectItem st = (SelectItem) arrayList.get(i);
            if (st.getValue().toString().equalsIgnoreCase(selectedValue)) {
                lable = st.getLabel();
                break;
            }
        }
        return lable;
    }

    public static String getRegnNoinVtPermit(String value, String stateCd) throws VahanException {
        String regnNo = "";
        String Query;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("getRegnNoinVtPermit");
            Query = "SELECT regn_no FROM " + TableList.VT_PERMIT + " where regn_no = ? ";
            if (!CommonUtils.isNullOrBlank(stateCd)) {
                Query += " and state_cd = ?";
            }
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, value);
            if (!CommonUtils.isNullOrBlank(stateCd)) {
                ps.setString(2, stateCd);
            }
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                regnNo = rs.getString("regn_no");
            } else {
                Query = "SELECT regn_no FROM " + TableList.VT_PERMIT + " where pmt_no = ?";
                if (!CommonUtils.isNullOrBlank(stateCd)) {
                    Query += " and state_cd = ?";
                }
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, value);
                if (!CommonUtils.isNullOrBlank(stateCd)) {
                    ps.setString(2, stateCd);
                }
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    regnNo = rs.getString("regn_no");
                }
            }
            if (CommonUtils.isNullOrBlank(regnNo)) {
                throw new VahanException("Details not found, Please enter correct registration/permit mumber.");
            }
        } catch (VahanException ve) {
            throw ve;
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
        return regnNo.toUpperCase();
    }

    public static String getRegnNoForSurrenderInVtPermit(String value, String stateCd) throws VahanException {
        String regnNo = "";
        String Query;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("getRegnNoForSurrenderInVtPermit");
            Query = "SELECT regn_no FROM " + TableList.VT_PERMIT + " where regn_no = ? ";
            if (!CommonUtils.isNullOrBlank(stateCd)) {
                Query += " and state_cd = ?";
            }
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, value);
            if (!CommonUtils.isNullOrBlank(stateCd)) {
                ps.setString(2, stateCd);
            }
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                regnNo = rs.getString("regn_no");
            } else {
                Query = "SELECT regn_no FROM " + TableList.VT_PERMIT + " where pmt_no = ?";
                if (!CommonUtils.isNullOrBlank(stateCd)) {
                    Query += " and state_cd = ?";
                }
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, value);
                if (!CommonUtils.isNullOrBlank(stateCd)) {
                    ps.setString(2, stateCd);
                }
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    regnNo = rs.getString("regn_no");
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
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return regnNo.toUpperCase();
    }

    public static String getRegnNoinVtTempPermit(String value, String stateCd) throws VahanException {
        String regnNo = "";
        String Query;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("getRegnNoinVtTempPermit");
            Query = "SELECT regn_no FROM " + TableList.VT_TEMP_PERMIT + " where regn_no = ? and state_cd = ? and pur_cd in (?,?,?)";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, value);
            ps.setString(2, stateCd);
            ps.setInt(3, TableConstants.VM_PMT_TEMP_PUR_CD);
            ps.setInt(4, TableConstants.VM_PMT_RENEW_TEMP_PUR_CD);
            ps.setInt(5, TableConstants.VM_PMT_SPECIAL_PUR_CD);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                regnNo = rs.getString("regn_no");
            } else {
                Query = "SELECT regn_no FROM " + TableList.VT_TEMP_PERMIT + " where pmt_no = ? and state_cd = ? and pur_cd in (?,?,?)";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, value);
                ps.setString(2, stateCd);
                ps.setInt(3, TableConstants.VM_PMT_TEMP_PUR_CD);
                ps.setInt(4, TableConstants.VM_PMT_RENEW_TEMP_PUR_CD);
                ps.setInt(5, TableConstants.VM_PMT_SPECIAL_PUR_CD);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    regnNo = rs.getString("regn_no");
                }
            }

            if (CommonUtils.isNullOrBlank(regnNo)) {
                throw new VahanException("Details not found, Please enter correct registration/permit mumber.");
            }
        } catch (VahanException ve) {
            throw ve;
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
        return regnNo.toUpperCase();
    }

    public static String getRegnNoinVtPermitTranaction(String value) {
        String regnNo = "";
        String Query;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("getRegnNoinVtPermitTranaction");
            Query = "SELECT regn_no FROM " + TableList.VT_PERMIT_TRANSACTION + " where regn_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, value);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                regnNo = rs.getString("regn_no");
            } else {
                Query = "SELECT regn_no FROM " + TableList.VT_PERMIT_TRANSACTION + " where pmt_no = ?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, value);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    regnNo = rs.getString("regn_no");
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
        return regnNo.toUpperCase();
    }

    public static String getPmtNoThroughVtPermit(String value) {
        String pmt_no = "";
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("getPmtNoThroughVtPermit");
            String Query = "SELECT pmt_no FROM " + TableList.VT_PERMIT + " where state_cd = ? and regn_no = ? ";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, value);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                pmt_no = rs.getString("pmt_no");
            } else {
                Query = "SELECT pmt_no FROM " + TableList.VT_PERMIT + " where state_cd = ? and pmt_no = ?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, Util.getUserStateCode());
                ps.setString(2, value);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    pmt_no = rs.getString("pmt_no");
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
        return pmt_no.toUpperCase();
    }

    public static String[] getPmtNoAndPmtTypeThroughVtPermit(String value) {
        String[] pmtDetails = new String[2];
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("getPmtNoAndPmtTypeThroughVtPermit");
            String Query = "SELECT pmt_no,pmt_type FROM " + TableList.VT_PERMIT + " where state_cd = ? and regn_no = ? ";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, value);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                pmtDetails[0] = rs.getString("pmt_no");
                pmtDetails[1] = rs.getString("pmt_type");
            } else {
                Query = "SELECT pmt_no,pmt_type FROM " + TableList.VT_PERMIT + " where state_cd = ? and pmt_no = ?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, Util.getUserStateCode());
                ps.setString(2, value);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    pmtDetails[0] = rs.getString("pmt_no");
                    pmtDetails[1] = rs.getString("pmt_type");
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
        return pmtDetails;
    }

    public static String getPmtNoFromVtPermitForAuth(String value, boolean multiPmtAllow) {
        String pmt_no = "";
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        PreparedStatement ps1;
        try {
            tmgr = new TransactionManagerReadOnly("getPmtNoFromVtPermitForAuth");
            if (multiPmtAllow) {
                String Query = "SELECT regn_no,pmt_no,pmt_type FROM " + TableList.VT_PERMIT + " where state_cd = ? and regn_no = ?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, Util.getUserStateCode());
                ps.setString(2, value);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    if (rs.getInt("pmt_type") == Integer.parseInt(TableConstants.NATIONAL_PERMIT)) {
                        pmt_no = rs.getString("pmt_no");
                        break;
                    } else if (rs.getInt("pmt_type") == Integer.parseInt(TableConstants.AITP)) {
                        pmt_no = rs.getString("pmt_no");
                        break;
                    } else if (rs.getInt("pmt_type") == Integer.parseInt(TableConstants.GOODS_PERMIT)) {
                        String SQuery = "SELECT pmt_no FROM " + TableList.VT_PERMIT + " where state_cd = ? and regn_no = ? and pmt_type=?";
                        ps1 = tmgr.prepareStatement(SQuery);
                        ps1.setString(1, Util.getUserStateCode());
                        ps1.setString(2, rs.getString("regn_no"));
                        ps1.setInt(3, Integer.parseInt(TableConstants.NATIONAL_PERMIT));
                        RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                        if (rs1.next()) {
                            pmt_no = rs1.getString("pmt_no");
                        }
                        break;
                    }
                }
            } else {
                String Query = "SELECT pmt_no FROM " + TableList.VT_PERMIT + " where state_cd = ? and regn_no = ? ";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, Util.getUserStateCode());
                ps.setString(2, value);
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    pmt_no = rs.getString("pmt_no");
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
        return pmt_no.toUpperCase();
    }

    public static int getPmtOffCdThroughVtPermit(String value) {
        int off_cd = 0;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManagerReadOnly("getPmtOffCdThroughVtPermit");
            String Query = "SELECT off_cd FROM " + TableList.VT_PERMIT + " where state_cd = ? and pmt_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, value);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                off_cd = rs.getInt("off_cd");
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
        return off_cd;
    }

    public static int getPurCdInVhaPermitTranaction(String appl_no, int pur_cd) {
        int trans_pur_cd = 0;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("getRegnNoinVtPermit");
            String Query = "SELECT trans_pur_cd FROM " + TableList.VHA_PERMIT_TRANSACTION + " where appl_no=? AND pur_cd=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            ps.setInt(2, pur_cd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                trans_pur_cd = rs.getInt("trans_pur_cd");
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
        return trans_pur_cd;
    }

    public static Map getVmPermitStateConfiguration(String stateCd) {
        Map<String, String> stateConfMap = new LinkedHashMap<String, String>();
        TransactionManagerReadOnly tmgr = null;
        RowSet rs = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManagerReadOnly("Private Service Permit Print");
            String query = "SELECT state_cd, temp_days_valid_upto, temp_weeks_valid_upto, spec_days_valid_upto,\n"
                    + "  spec_weeks_valid_upto, sc_renew_before_days, cc_renew_before_days,\n"
                    + "  ai_renew_before_days, psv_renew_before_days, gp_renew_before_days,\n"
                    + "  np_renew_before_days, sc_rule_heading, sc_formno_heading, cc_rule_heading,\n"
                    + "  cc_formno_heading, aitp_rule_heading, aitp_formno_heading, psvp_rule_heading,\n"
                    + "  psvp_formno_heading, gp_rule_heading, gp_formno_heading, np_rule_heading,\n"
                    + "  np_formno_heading, temp_rule_heading, temp_formno_heading, spec_rule_heading,\n"
                    + "  spec_formno_heading, auth_aitp_rule_heading, auth_aitp_formno_heading,\n"
                    + "  auth_np_rule_heading, auth_np_formno_heading, note_in_footer,\n"
                    + "  temp_days_valid_from, temp_weeks_valid_from, spec_days_valid_from,\n"
                    + "  spec_weeks_valid_from, sc_renew_after_days, cc_renew_after_days,\n"
                    + "  ai_renew_after_days, psv_renew_after_days, gp_renew_after_days,\n"
                    + "  np_renew_after_days, renewal_of_permit_valid_from_flag, temp_route_area,\n"
                    + "  genrate_ol_appl, permanent_permit_valid, allowed_surr_pur_cd,\n"
                    + "  temp_pmt_type, renew_temp_pmt, spl_pmt_route, allowed_sta_rto_relation,\n"
                    + "  sta_rto_condition, permit_main_header, permit_main_footer, temp_state_as_region,\n"
                    + "  spl_pmt_passenger, auth_valid_from_prv_date_after_expire, show_region_on_cs_permit,\n"
                    + "  tax_exempted, insaurance_exempted, insaurance_condition, pmt_type_header_on_print,\n"
                    + "  pmt_reservation_allow, misllenious_restriction_on_permit, permit_expire_exempted,\n"
                    + "  fitness_exempted, multi_appl_allow, multi_appl_condition, offer_validity_days,\n"
                    + "  update_owner_identification, show_route_details_cs, allow_special_vh_class,\n"
                    + "  allow_reservation_in_cs, cs_formno_heading, allow_renew_countersignature,\n"
                    + "  cash_mode_disabled, allow_pending_fitness, route_flag_condition,\n"
                    + "  multi_pmt_allow_on_veh, service_charge_per_transaction, show_period_on_aitp_auth,\n"
                    + "  render_payment_table_aitp, other_office_route_allow, exempt_fine_vh_blacklist,\n"
                    + "  surrender_pmt_office, cancel_permit_by_authority, show_state_to_in_routemaster,\n"
                    + "  show_other_state_route, auth_recursive_fee, pmt_validity_from,\n"
                    + "  homeauth_renew_enable_verify, show_time_table, veh_replace_condition,\n"
                    + "  allow_loi_after_expired, tax_depend_on_pmt, show_uploaded_document,\n"
                    + "  fee_exempted_condition, render_goods_passanger_tax, validity_from_regn_dt, render_modify_trips,\n"
                    + "  exempt_echallan,fine_exempted_condition\n"
                    + "  FROM " + TableList.VM_PERMIT_STATE_CONFIGURATION + " where state_cd = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, stateCd);
            rs = tmgr.fetchDetachedRowSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    stateConfMap.put(rsmd.getColumnName(i), rs.getString(i));
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
        return stateConfMap;
    }

    public static Map getTmTempPmtStateConfiguration(String stateCd) {
        Map<String, String> stateConfMap = new LinkedHashMap<String, String>();
        TransactionManagerReadOnly tmgr = null;
        RowSet rs = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManagerReadOnly("getTmTempPmtStateConfiguration");
            String query = "SELECT state_cd, temp_days_valid_upto, temp_weeks_valid_upto, temp_rule_heading,\n"
                    + "  temp_formno_heading, temp_days_valid_from, temp_weeks_valid_from,\n"
                    + "  temp_route_area, temp_pmt_type, temp_renew_pmt, temp_state_as_region,\n"
                    + "  temp_period_mode, temp_months_valid_upto, permanent_permit_valid,\n"
                    + "  tax_valid_on_verify, temp_tax_on_rlength\n"
                    + "  FROM  " + TableList.TM_TEMP_PMT_STATE_CONFIGURATION + " where state_cd = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, stateCd);
            rs = tmgr.fetchDetachedRowSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    stateConfMap.put(rsmd.getColumnName(i), rs.getString(i));
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
        return stateConfMap;
    }

    public static Map getTmSpecialPmtStateConfiguration(String stateCd) {
        Map<String, String> stateConfMap = new LinkedHashMap<String, String>();
        TransactionManagerReadOnly tmgr = null;
        RowSet rs = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManagerReadOnly("getTmSpecialPmtStateConfiguration");
            String query = "SELECT state_cd, spl_days_valid_upto, spl_weeks_valid_upto, spl_rule_heading,\n"
                    + "  spl_formno_heading, spl_days_valid_from, spl_weeks_valid_from,\n"
                    + "  spl_pmt_route, spl_pmt_passenger, spl_period_mode, challan_exampt,\n"
                    + "  route_allow_for_special_tax, permanent_permit_valid, spl_tax_on_rlength,\n"
                    + "  spl_permit_footer, spl_route_area\n"
                    + "  FROM " + TableList.TM_SPL_PMT_STATE_CONFIGURATION + " where state_cd = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, stateCd);
            rs = tmgr.fetchDetachedRowSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    stateConfMap.put(rsmd.getColumnName(i), rs.getString(i));
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
        return stateConfMap;
    }

    public static boolean takeRenewalOfPermit_After(String state_cd, String regn_no, String pmt_no) {
        boolean flag = false;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        String Query;
        RowSet rs, rs1;
        try {
            tmgr = new TransactionManagerReadOnly("Take Renewal Of Permit After");
            Query = "select DATE_PART('day',valid_upto-current_date)::int as dateDiffrence,\n"
                    + "case\n"
                    + "when pmt_type=101 then 'sc_renew_after_days'\n"
                    + "when pmt_type=102 then 'cc_renew_after_days'\n"
                    + "when pmt_type=103 then 'ai_renew_after_days'\n"
                    + "when pmt_type=104 then 'psv_renew_after_days'\n"
                    + "when pmt_type=105 then 'gp_renew_after_days'\n"
                    + "when pmt_type=106 then 'np_renew_after_days'\n"
                    + "end as cofigValue,state_cd\n"
                    + "from " + TableList.VT_PERMIT + " where state_cd = ? AND regn_no = ? AND pmt_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, state_cd);
            ps.setString(2, regn_no);
            ps.setString(3, pmt_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (!CommonUtils.isNullOrBlank(rs.getString("cofigValue"))) {
                    Query = "select " + rs.getString("cofigValue") + " from " + TableList.VM_PERMIT_STATE_CONFIGURATION + " where state_cd = ?";
                    ps = tmgr.prepareStatement(Query);
                    ps.setString(1, rs.getString("state_cd"));
                    rs1 = tmgr.fetchDetachedRowSet_No_release();
                    if (rs1.next()) {
                        if ((rs.getInt("dateDiffrence") > 0) && (rs.getInt("dateDiffrence") <= rs1.getInt(rs.getString("cofigValue")))) {
                            flag = true;
                        } else if (rs.getInt("dateDiffrence") <= 0) {
                            flag = true;
                        }
                    }
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
        return flag;
    }

    public static boolean getPreviousApplStatusInVHA(String appl_no) {
        boolean flag = false;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        try {
            tmgr = new TransactionManagerReadOnly("Previous Appl Status in VHA");
            String Query = "select * from " + TableList.VHA_STATUS + " where appl_no = ? and pur_cd = ?";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, appl_no);
            ps.setInt(i++, TableConstants.VM_PMT_APPLICATION_PUR_CD);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                flag = true;
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
        return flag;
    }

    public static int getActionCd(String state_cd, int pur_cd, int transPurCd, int flow_srno, TransactionManager tmgr, int tempSpl_purCd) {
        int actionCd = 0;
        PreparedStatement ps;
        VehicleParameters vehParameters = new VehicleParameters();
        try {
            if (transPurCd == 0) {
                vehParameters.setPUR_CD(pur_cd);
            } else {
                vehParameters.setPUR_CD(transPurCd);
            }
            if (tempSpl_purCd != 0 && tempSpl_purCd == TableConstants.VM_PMT_SPECIAL_PUR_CD && pur_cd == TableConstants.VM_PMT_CANCELATION_PUR_CD
                    && transPurCd == TableConstants.VM_PMT_CANCELATION_PUR_CD) {
                vehParameters.setPUR_CD(tempSpl_purCd);
            }
            String Query = "SELECT action_cd,condition_formula FROM " + TableList.TM_PURPOSE_ACTION_FLOW + " where state_cd=? and pur_cd=? and flow_srno=?";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, state_cd);
            ps.setInt(i++, pur_cd);
            ps.setInt(i++, flow_srno);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "TM_PURPOSE_ACTION_FLOW(State:" + state_cd + ",PurCd:" + pur_cd + ",TransPurCd:" + transPurCd + ")")) {
                    actionCd = rs.getInt("action_cd");
                } else {
                    actionCd = getActionCd(state_cd, pur_cd, transPurCd, flow_srno + 1, tmgr, 0);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return actionCd;
    }

    public static int getflowSerialNumber(String state_cd, int pur_cd, int action_cd, TransactionManager tmgr) {
        int flowSrNo = 0;
        PreparedStatement ps;
        try {
            String Query = "SELECT flow_srno FROM " + TableList.TM_PURPOSE_ACTION_FLOW + " where state_cd=? and pur_cd=? and action_cd=?";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, state_cd);
            ps.setInt(i++, pur_cd);
            ps.setInt(i++, action_cd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                flowSrNo = rs.getInt("flow_srno");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return flowSrNo;
    }

    public static int getPermitValidity(String appl_no, String tableName) {
        TransactionManagerReadOnly tmgr = null;
        int actionCd = 0;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManagerReadOnly(appl_no);
            String Query = "SELECT pur_cd, period_mode, period FROM " + tableName + " where appl_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                actionCd = rs.getInt("action_cd");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
            }
        }
        return actionCd;
    }

    public static String[] getPmtValidity(int pur_cd, int pmt_code, int pmt_catg, boolean hypth, int owner_cd) {
        PassengerPermitDetailDobj pmtDobj = null;
        Owner_dobj owner_dobj = null;
        String[] value = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getPmtValidity");
            String Query = "SELECT * FROM " + TableList.VM_PERMIT_VALIDITY_MAST + " where state_cd = ? AND pur_cd = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, pur_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                pmtDobj = new PassengerPermitDetailDobj();
                owner_dobj = new Owner_dobj();
                owner_dobj.setOwner_cd(owner_cd);
                if (pmt_code != 0) {
                    pmtDobj.setPmt_type_code(String.valueOf(pmt_code));
                }
                if (pmt_catg != 0) {
                    pmtDobj.setPmtCatg(String.valueOf(pmt_catg));
                }
                if (hypth) {
                    pmtDobj.setHypth(hypth);
                }

                VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(owner_dobj, pmtDobj, 0, 0, 0, 0, 0, 0, 0, 0);
                if (isCondition(FormulaUtils.replaceTagPermitValues(rs.getString("condition_formula"), parameters), "VM_PERMIT_VALIDITY_MAST(State:" + Util.getUserStateCode() + ",PurCd:" + pur_cd + ")")) {
                    value = new String[2];
                    value[0] = rs.getString("period_mode");
                    value[1] = rs.getString("period");
                }
            }
        } catch (SQLException | VahanException e) {
            value = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                value = null;
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return value;
    }

    public static String getPmtSubType(String applNo) {
        TransactionManagerReadOnly tmgr = null;
        String value = "";
        try {
            tmgr = new TransactionManagerReadOnly("getPmtSubType");
            String Query = "SELECT \n"
                    + "CASE \n"
                    + "  WHEN (route_cd is null or length(trim(route_cd)) = 0) and (region_covered is not null or length(trim(region_covered)) > 0) THEN 'Area Details' \n"
                    + "  WHEN (region_covered is null or length(trim(region_covered)) = 0) and (route_cd is not null or length(trim(route_cd)) > 0) THEN 'Route Details' \n"
                    + "  WHEN (route_cd is null or length(trim(route_cd)) = 0) and (region_covered is null or length(trim(region_covered)) = 0) THEN 'All State' \n"
                    + "  WHEN (route_cd is not null or length(trim(route_cd)) > 0) and (region_covered is not null or length(trim(region_covered)) > 0) THEN 'Area/Route Details' \n"
                    + "  ELSE 'All State'\n"
                    + "END as permit_sub_type\n"
                    + "from " + TableList.VA_PERMIT + " a\n"
                    + "left join " + TableList.va_permit_route + " b on a.appl_no = b.appl_no\n"
                    + "where a.appl_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                value = rs.getString("permit_sub_type");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                value = null;
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return value;
    }

    public static String checkPmtValidation(PermitCheckDetailsDobj validDobj) {
        String msg = null;
        if (validDobj != null) {
            if (validDobj.getTaxUpto().equalsIgnoreCase("LIFE TIME") && !CommonUtils.isNullOrBlank(validDobj.getTaxMode()) && ("OLS".contains(validDobj.getTaxMode()))) {
                validDobj.setTaxUpto("31-DEC-2999");
            } else if (validDobj.getTaxUpto().equalsIgnoreCase("LIFE TIME") && !CommonUtils.isNullOrBlank(validDobj.getTaxMode()) && (!"OLS".contains(validDobj.getTaxMode()))) {
                validDobj.setTaxUpto("");
            }
            if (!validDobj.isExemptedFitness() && (CommonUtils.isNullOrBlank(validDobj.getFitValidTo()) || !(DateUtils.compareDates(DateUtils.getCurrentLocalDate(), CommonPermitPrintImpl.getDateDD_MMM_YYYY(validDobj.getFitValidTo())) <= 1))) {
                msg = "Your vehicle fitness is expired.";
            } else if (!validDobj.isExemptedIns() && CommonUtils.isNullOrBlank(validDobj.getInsUpto())) {
                msg = "Please fill the insurance details.";
            } else if (!validDobj.isExemptedIns() && !(validDobj.isInsValid())) {
                msg = "Your vehicle insurance is expired.";
            } else if ((!validDobj.isChalPending() && !validDobj.isExemptedChallan()) || (validDobj.isChalPending() && validDobj.isChalPendingInService() && !validDobj.isExemptedChallan())) {
                msg = "Please pay challan first.";
            } else if (validDobj.isVehicleIsBlackListed()) {
                msg = "Vehicle is Black Listed";
            } else if (CommonUtils.isNullOrBlank(validDobj.getTaxUpto()) || !(DateUtils.compareDates(DateUtils.getCurrentLocalDate(), CommonPermitPrintImpl.getDateDD_MMM_YYYY(validDobj.getTaxUpto())) <= 1)) {
                msg = validDobj.isExemptedTax() && CommonUtils.isNullOrBlank(validDobj.getTaxUpto()) ? null : "Please pay MV Tax first.";
            }
        } else {
            msg = "Something went wrong, try again.";
        }
        return msg;
    }

    public static String checkPmtGracePeriod(PermitCheckDetailsDobj validDobj, int pmt_type_code) {
        String msg = null;
        PassengerPermitDetailImpl passImpl = new PassengerPermitDetailImpl();
        String taxUpto = validDobj.getTaxUpto();
        if ("KL,PY".contains(Util.getUserStateCode())) {            
            String[] gracePeriod = passImpl.getGracePeriodForTax(pmt_type_code,validDobj);            
            if (gracePeriod != null && !CommonUtils.isNullOrBlank(validDobj.getTaxUpto()) && gracePeriod[0].equals("D")) {
                Date validUptoTax = ServerUtil.dateRange(CommonPermitPrintImpl.getDateDD_MMM_YYYY(validDobj.getTaxUpto()), 0, 0, Integer.parseInt(gracePeriod[1]));
                validDobj.setTaxUpto(getDateStringDD_MMM_YYYY(validUptoTax));
            } else if (gracePeriod != null && !CommonUtils.isNullOrBlank(validDobj.getTaxUpto()) && gracePeriod[0].equals("M")) {
                Date validUptoTax = ServerUtil.dateRange(CommonPermitPrintImpl.getDateDD_MMM_YYYY(validDobj.getTaxUpto()), 0, Integer.parseInt(gracePeriod[1]), 0);
                validDobj.setTaxUpto(getDateStringDD_MMM_YYYY(validUptoTax));
            } else if (gracePeriod != null && !CommonUtils.isNullOrBlank(validDobj.getTaxUpto()) && gracePeriod[0].equals("Y")) {
                Date validUptoTax = ServerUtil.dateRange(CommonPermitPrintImpl.getDateDD_MMM_YYYY(validDobj.getTaxUpto()), Integer.parseInt(gracePeriod[1]), 0, 0);
                validDobj.setTaxUpto(getDateStringDD_MMM_YYYY(validUptoTax));
            }
        }
        if (CommonUtils.isNullOrBlank(validDobj.getTaxUpto()) || !(DateUtils.compareDates(DateUtils.getCurrentLocalDate(), CommonPermitPrintImpl.getDateDD_MMM_YYYY(validDobj.getTaxUpto())) <= 1)) {
            msg = validDobj.isExemptedTax() && CommonUtils.isNullOrBlank(validDobj.getTaxUpto()) ? null : "Please pay MV Tax first.";
        }
        validDobj.setTaxUpto(taxUpto);
        return msg;
    }

    public static String checkGoodsPassengerTaxValidation(PermitCheckDetailsDobj validDobj) {
        String msg = null;
        if (validDobj != null) {
            if ((!validDobj.isExemptedTax()) && (CommonUtils.isNullOrBlank(validDobj.getGptaxUpto()) || !(DateUtils.compareDates(DateUtils.getCurrentLocalDate(), CommonPermitPrintImpl.getDateDD_MMM_YYYY(validDobj.getGptaxUpto())) <= 1))) {
                msg = "Please pay Goods/Passenger Tax first.";
            }
        }
        return msg;
    }

    public static String getDocumentId(String applNo, String regnNo) {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String docId = null;
        try {
            tmgr = new TransactionManagerReadOnly("getDocumentId");
            String Query = "SELECT doc_id from " + TableList.VA_PERMIT_PRINT + " where appl_no = ? and regn_no = ?";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, applNo);
            ps.setString(i++, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                docId = rs.getString("doc_id");
            }
        } catch (Exception e) {
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                docId = null;
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return docId;
    }

    public static boolean isVehicleSurrInPermanentMode(String regnNo) {
        boolean flag = true;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManagerReadOnly("isVehicleSurrInPermanentMode");
            String Query = "SELECT * FROM " + TableList.VHA_PERMIT_TRANSACTION + " where regn_no =? AND trans_pur_cd in (?,?) order by MOVED_BY DESC limit 1";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regnNo);
            ps.setInt(2, TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD);
            ps.setInt(3, TableConstants.VM_PMT_CANCELATION_PUR_CD);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                flag = false;
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
        return flag;
    }

    public static String[] getPmtValidateMsg(String stateCd, int pur_cd, Owner_dobj ownerdobj, PassengerPermitDetailDobj pmtDobj) throws VahanException {
        String[] values = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        int pYear = 0;
        String regionCoveredTempPurpose = "";
        try {
            tmgr = new TransactionManagerReadOnly("getPermitValidateMsg");
            String Query = "SELECT pmt_smart_card, condition_formula, validate_msg FROM " + TableList.VM_PERMIT_VALIDATE_MSG + " where state_cd = ? and pur_cd = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, stateCd);
            ps.setInt(2, pur_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (pur_cd != TableConstants.VM_PMT_TEMP_PUR_CD
                    && ((ownerdobj != null && pur_cd == TableConstants.VM_PMT_TRANSFER_PUR_CD)
                    || (ownerdobj != null && ("UP").contains(Util.getUserStateCode()) && !CommonUtils.isNullOrBlank(pmtDobj.getRegnNo()) && !pmtDobj.getRegnNo().equals("NEW")))) {
                pYear = ServerUtil.maxDiffYear(new Date(), ownerdobj.getRegn_dt());
            }
            if (pur_cd != TableConstants.VM_PMT_TEMP_PUR_CD && pmtDobj != null && !CommonUtils.isNullOrBlank(pmtDobj.getRegion_covered())) {
                regionCoveredTempPurpose = pmtDobj.getRegion_covered();
                pmtDobj.setRegion_covered(String.valueOf(pmtDobj.getRegion_covered().split(",").length));
            }
            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(ownerdobj, pmtDobj, 0, 0, 0, pYear, 0, 0, 0, 0);
            if (pur_cd != TableConstants.VM_PMT_TEMP_PUR_CD && pmtDobj != null) {
                parameters.setREGN_NO(pmtDobj.getRegnNo());
            }
            while (rs.next()) {
                if (isCondition(FormulaUtils.replaceTagPermitValues(rs.getString("condition_formula"), parameters), "VM_PERMIT_VALIDATE_MSG(State:" + stateCd + ",PurCd:" + pur_cd + ")")) {
                    values = new String[2];
                    values[0] = rs.getString("pmt_smart_card");
                    values[1] = rs.getString("validate_msg");
                }
            }
            if (pur_cd != TableConstants.VM_PMT_TEMP_PUR_CD && pmtDobj != null) {
                pmtDobj.setRegion_covered(regionCoveredTempPurpose);
            }
        } catch (VahanException e) {
            throw e;
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
        return values;
    }

    public static String getPmtValidityMsg(String stateCd, int purCd, String applNo) {
        String msg = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String Query = null;
        int i = 1;
        RowSet rs, rs1;
        int pYear = 0, pMonth = 0, pDay = 0;
        try {
            tmgr = new TransactionManager("getPmtValidityMsg");
            Map<String, String> getmap = CommonPermitPrintImpl.getVmPermitStateConfiguration(stateCd);
            if (getmap.get("renewal_of_permit_valid_from_flag").equalsIgnoreCase("A")
                    && (purCd == TableConstants.VM_PMT_FRESH_PUR_CD || purCd == TableConstants.VM_PMT_RENEWAL_PUR_CD)) {
                msg = "Application No : " + applNo + ", Permit Validity : When you approve the permit";
            } else if ((getmap.get("renewal_of_permit_valid_from_flag").equalsIgnoreCase("F")
                    && (purCd == TableConstants.VM_PMT_FRESH_PUR_CD || purCd == TableConstants.VM_PMT_RENEWAL_PUR_CD))
                    || (getmap.get("renewal_of_permit_valid_from_flag").equalsIgnoreCase("P")
                    && purCd == TableConstants.VM_PMT_FRESH_PUR_CD)) {
                Query = "SELECT regn_no,rcpt_no,period_mode,period,off_cd from " + TableList.VA_PERMIT + " where state_cd = ? and appl_no = ?";
                ps = tmgr.prepareStatement(Query);
                i = 1;
                ps.setString(i++, stateCd);
                ps.setString(i++, applNo);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    Query = "SELECT * from " + TableList.VT_FEE + " where state_cd = ? and off_cd = ? and pur_cd = ? and regn_no = ? and rcpt_no = ? ";
                    ps = tmgr.prepareStatement(Query);
                    i = 1;
                    ps.setString(i++, stateCd);
                    ps.setInt(i++, rs.getInt("off_cd"));
                    ps.setInt(i++, purCd);
                    ps.setString(i++, rs.getString("regn_no"));
                    ps.setString(i++, rs.getString("rcpt_no"));
                    rs1 = tmgr.fetchDetachedRowSet_No_release();
                    if (rs1.next()) {
                        getDateStringDD_MMM_YYYY(rs1.getDate("rcpt_dt"));
                        if (rs.getString("period_mode").equalsIgnoreCase("Y")) {
                            pYear = rs.getInt("period");
                        }
                        if (rs.getString("period_mode").equalsIgnoreCase("M")) {
                            pMonth = rs.getInt("period");
                        }
                        msg = "Application No : " + applNo + ", Permit Validity : " + getDateStringDD_MMM_YYYY(rs1.getDate("rcpt_dt")) + " to " + getDateStringDD_MMM_YYYY(ServerUtil.dateRange(rs1.getDate("rcpt_dt"), pYear, pMonth, -1));
                    }
                }
            } else if (getmap.get("renewal_of_permit_valid_from_flag").equalsIgnoreCase("P")
                    && purCd == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
                Query = "SELECT a.valid_upto,b.period_mode,b.period from " + TableList.VT_PERMIT + " a\n"
                        + "inner join " + TableList.VA_PERMIT + " b on a.regn_no = b.regn_no and a.state_cd = b.state_cd\n"
                        + "where a.state_cd = ? and b.pur_cd = ? and b.appl_no = ?";
                ps = tmgr.prepareStatement(Query);
                i = 1;
                ps.setString(i++, stateCd);
                ps.setInt(i++, purCd);
                ps.setString(i++, applNo);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    Date validFrom = ServerUtil.dateRange(rs.getDate("valid_upto"), pYear, pMonth, 1);

                    if (rs.getString("period_mode").equalsIgnoreCase("Y")) {
                        pYear = rs.getInt("period");
                    }
                    if (rs.getString("period_mode").equalsIgnoreCase("M")) {
                        pMonth = rs.getInt("period");
                    }
                    Date validUpto = ServerUtil.dateRange(validFrom, pYear, pMonth, -1);
                    msg = "Application No : " + applNo + ", Permit Validity : " + getDateStringDD_MMM_YYYY(validFrom) + " to " + getDateStringDD_MMM_YYYY(validUpto);
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
        return msg;
    }

    public static String getPmtFeePaidAtTymOfRegn(String stateCd, String regnNo, String appl_no) {
        String msg = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getPmtFeePaidAtTymOfRegn");
            String Query = " SELECT 'This Vehicle already submitted permit fee with Receipt No. '|| string_agg(rcpt_no,',')  ||' at the time of new registration. Permit fee is - Rs ' || sum(fees)||'/- & fine is - Rs '|| sum(fine)||'/-' as msg  \n"
                    + " from ( \n"
                    + " SELECT  rcpt_no,sum(fees)fees, sum(fine)fine \n"
                    + " from " + TableList.VT_FEE + " a \n"
                    + " where (state_cd,off_cd,rcpt_no) in (SELECT state_cd,off_cd,rcpt_no from " + TableList.VT_FEE + " \n"
                    + " where (state_cd,off_cd,rcpt_no) in (SELECT state_cd,off_cd,rcpt_no from " + TableList.VP_APPL_RCPT_MAPPING + " \n"
                    + " where appl_no in (SELECT appl_no from " + TableList.VA_DETAILS + "  where regn_no=? and pur_cd in (?,?,?) and state_cd = ?)) and pur_cd in (?,?,?)) and pur_cd in (?,?,?,?,?,?,?) group by rcpt_no \n"
                    + " union \n"
                    + " SELECT rcpt_no,sum(fees)fees, sum(fine)fine  from " + TableList.VT_FEE + " a \n"
                    + " left outer join tm_purpose_mast b on a.pur_cd=b.pur_cd \n"
                    + " where (state_cd,off_cd,rcpt_no) in (SELECT state_cd,off_cd,rcpt_no from " + TableList.VT_FEE + " \n"
                    + " where (state_cd,off_cd,rcpt_no) in (SELECT state_cd,off_cd,rcpt_no from " + TableList.VP_APPL_RCPT_MAPPING + " \n"
                    + " where appl_no =?) and pur_cd in (?,?,?)) and a.pur_cd in (?,?,?) group by rcpt_no ,b.descr \n"
                    + ") a ";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, regnNo);
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO);
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO);
            ps.setInt(i++, TableConstants.VM_PMT_FRESH_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_APPLICATION_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_SURCHARG_FEE);
            ps.setInt(i++, TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD);
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_USER_CHARGES);
            ps.setInt(i++, TableConstants.PERMIT_PAPER_DOCUMENT_CHARGE);
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_ALL);
            ps.setString(i++, appl_no);
            ps.setInt(i++, TableConstants.VM_PMT_APPLICATION_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_SURCHARG_FEE);
            ps.setInt(i++, TableConstants.VM_PMT_APPLICATION_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_SURCHARG_FEE);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                msg = rs.getString("msg");
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
        return msg;
    }

    public static boolean checkPermitPrintPending(String applNo, String docId) throws VahanException {
        boolean flag = false;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("checkPermitPrintPending");
            String Query = "SELECT regn_no from " + TableList.VA_PERMIT_PRINT + " where appl_no = ? and doc_id = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, applNo);
            ps.setString(i++, docId);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
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
        return flag;
    }

    public static boolean getPermitAuthAllowed(String stateCd, int pmt_type, int pmt_catg) throws VahanException {
        boolean flag = false;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("checkPermitPrintPending");
            String Query = "SELECT * from " + TableList.VM_ALLOWED_AUTH + " where state_cd = ? and pmt_type IN (?,0) and pmt_catg IN (?,0)";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, stateCd);
            ps.setInt(i++, pmt_type);
            ps.setInt(i++, pmt_catg);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
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
        return flag;
    }

    public static void deleteFromTable(TransactionManager tmgr, String TableName, String appl_no) throws SQLException {
        String Query = "DELETE FROM " + TableName + " WHERE appl_no=?";
        PreparedStatement ps = tmgr.prepareStatement(Query);
        ps.setString(1, appl_no);
        ps.executeUpdate();
    }

    public static String getAllottedOffCd(int off_cd, String state_cd) {
        String Query = "";
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        String allottedOffCd = "";

        try {
            tmgr = new TransactionManagerReadOnly("getAllottedOffCd");
            Query = "SELECT * FROM " + TableList.VM_OFF_ALLOTMENT + " where off_cd=? AND STATE_CD = ? order by 2";
            ps = tmgr.prepareStatement(Query);
            ps.setInt(1, off_cd);
            ps.setString(2, state_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                allottedOffCd = rs.getString("allotted_off_cd");
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
        return allottedOffCd;
    }

    public static boolean isSTAOffice(int off_cd, String state_cd) {
        String Query = "";
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        boolean flag = false;

        try {
            tmgr = new TransactionManagerReadOnly("isSTAOffice");
            Query = "SELECT off_type_cd FROM " + TableList.TM_OFFICE + " where off_cd=? AND STATE_CD = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setInt(1, off_cd);
            ps.setString(2, state_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = rs.getInt("off_type_cd") == TableConstants.TM_OFFICE_CODE_FOR_STA ? true : false;
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
        return flag;
    }

    public static void removePrintFlow(TransactionManager tmgr, String appl_no) throws VahanException {
        PreparedStatement ps;
        try {
            String sql = "INSERT INTO " + TableList.VHA_STATUS + " "
                    + " SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno, \n"
                    + "  action_cd, seat_cd, cntr_id, 'C', office_remark, public_remark, \n"
                    + "  file_movement_type, ? , op_dt, current_timestamp, ? "
                    + "  from  " + TableList.VA_STATUS + " where appl_no=? and pur_cd=? and action_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(Util.getEmpCode()));//this should be as status_dobj.getEmp_cd() need to be updated in future...
            ps.setString(2, Util.getClientIpAdress());
            ps.setString(3, appl_no);
            ps.setInt(4, TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD);
            ps.setInt(5, TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_PRINT);
            ps.executeUpdate();

            sql = "Delete FROM " + TableList.VA_STATUS + " WHERE appl_no=? and pur_cd=? and action_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setInt(2, TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD);
            ps.setInt(3, TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_PRINT);
            ps.executeUpdate();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }
}
