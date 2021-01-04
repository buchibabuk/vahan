/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.CommonUtils;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerLocal;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.form.impl.ToImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
//import tax.web.service.VahanTaxParameters;
import nic.vahan.form.dobj.tax.VahanTaxParameters;
import static nic.vahan.server.ServerUtil.isTransport;

public class FormulaUtils {

    private static final Logger LOGGER = Logger.getLogger(FormulaUtils.class);
    public static Map TagFieldsMap = null;
    public static Map TagFieldsMapVerify = null;
    public static Map TagDescrDisplay = null;

    static {
        getTagFields();
    }

    private static void getTagFields() {
        if (TagFieldsMap == null) {
            TagFieldsMap = new LinkedHashMap<String, String>();
            if (TagFieldsMapVerify == null) {
                TagFieldsMapVerify = new LinkedHashMap<String, String>();
            }
            if (TagDescrDisplay == null) {
                TagDescrDisplay = new LinkedHashMap<String, String>();
            }

            String sql = "SELECT regexp_replace(code, '[^0-9]', '', 'g')::numeric as sr_no, code, descr, value_field, field_type FROM vm_tax_slab_fields order by 1";
            TransactionManager tmgr = null;
            try {
                tmgr = new TransactionManager("getTagFields");
                tmgr.prepareStatement(sql);
                RowSet rs = tmgr.fetchDetachedRowSet();
                while (rs.next()) {
                    TagFieldsMap.put(rs.getString("code"), rs.getString("value_field"));
                    TagFieldsMapVerify.put(rs.getString("code"), rs.getString("field_type"));
                    TagDescrDisplay.put(rs.getString("code"), rs.getString("descr"));
                }
                TagFieldsMap.put("<pDay>", "PMT_DAYS");
                TagFieldsMap.put("<pMonth>", "PMT_MONTHS");
                TagFieldsMap.put("<pCMonth>", "PMT_CEL_MONTH");
                TagFieldsMap.put("<pYear>", "PMT_YEAR");
                TagFieldsMap.put("<state_cd>", "STATE_CD");
                TagFieldsMap.put("<regn_no>", "REGN_NO");
                TagFieldsMap.put("<per_route>", "ROUTE_COUNT");
                TagFieldsMap.put("<per_region>", "REGION_COUNT");
                TagFieldsMap.put("<noc_ret>", "NOC_RETENTION");
                TagFieldsMap.put("<tmp_purpose>", "TMP_PURPOSE");
                TagFieldsMap.put("<exem_amount>", "EXEM_AMOUNT");
                TagFieldsMap.put("<fine_to_be_taken>", "FINE_TO_BE_TAKEN");
                TagFieldsMap.put("<multi_region>", "MULTI_REGION");
                TagFieldsMap.put("<multi_doc>", "MULTI_DOC");
                TagFieldsMap.put("<vehicle_hypth>", "VEHICLE_HYPTH");
                TagFieldsMap.put("<FIT_UPTO>", "FIT_UPTO");
                TagFieldsMap.put("<own_catg>", "OWN_CATG");
                TagFieldsMap.put("<to_reason>", "TO_REASON");
                TagFieldsMap.put("<fit_status>", "FIT_STATUS");
                TagFieldsMap.put("<pAmount>", "PMT_AMOUNT");
                TagFieldsMap.put("<excem_flag>", "EXCEM_FLAG");
                TagFieldsMap.put("<pOffer_Letter>", "OFFER_LETTER");
                TagFieldsMap.put("<pdup_reason>", "DUP_PMT_REASON");
                TagFieldsMap.put("<online_permit>", "ONLINE_PERMIT");
                TagFieldsMap.put("<permit_expired>", "PERMIT_EXPIRED");
                TagFieldsMap.put("<APPLICANT_TYPE>", "APPLICANT_TYPE");
                TagFieldsMap.put("<APPLICATION_TYPE>", "APPLICATION_TYPE");
                TagFieldsMap.put("<insp_rqrd>", "INSP_RQRD");
                TagFieldsMap.put("<PREV_OFF_CD>", "PREV_OFF_CD");
                TagFieldsMap.put("<region_descr>", "REGION_DESCR");
                TagFieldsMap.put("<regn_upto>", "REGN_UPTO");
                TagFieldsMap.put("<OWNER_CATG>", "OWNER_CATG");
                TagFieldsMap.put("<daysWithinState>", "DAYSWITHINSTATE");
                TagFieldsMap.put("<blackListCode>", "BLACKLISTCODE");
                TagFieldsMap.put("<manufactur_date>", "MANUFACTUR_DATE");
                TagFieldsMap.put("<authYear>", "AUTHYEAR");
                TagFieldsMap.put("<action_cd>", "ACTION_CD");
                TagFieldsMap.put("<oldVchType>", "OLD_VCH_TYPE");
                TagFieldsMap.put("<per_state>", "STATE_COUNT");
                TagFieldsMap.put("<tripextended>", "TRIPEXTENDED");
                TagFieldsMap.put("<is_payment_done>", "ISPAYMENTDONE");
                TagFieldsMap.put("<regn_state>", "REGN_STATE");
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

        }
    }

    public static String replaceTagValues(String inputString, VehicleParameters dobj) {
        String retString = inputString;
        if (TagFieldsMap == null) {
            getTagFields();
        }
        Set entries = TagFieldsMap.entrySet();
        Iterator entryIter = entries.iterator();
        while (entryIter.hasNext()) {
            Map.Entry entry = (Map.Entry) entryIter.next();
            Object key = entry.getKey();  // Get the key from the entry.
            Object value = entry.getValue();  // Get the value.

            try {
                Method method = findMethod(dobj.getClass(), "get" + value.toString().substring(0, 1).toUpperCase() + value.toString().substring(1));
                Class ab = method.getReturnType();

                Object retObj = method.invoke(dobj, null);
                if (ab.isInstance(new String())) {
                    if (retObj == null) {
                        retString = retString.replace(key.toString(), "null");
                    } else {
                        retString = retString.replace(key.toString(), "'" + retObj.toString().toUpperCase() + "'");
                    }
                } else {
                    retString = retString.replace(key.toString(), retObj.toString().toUpperCase());
                }
            } catch (Exception e) {
                //LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        }
        return retString;
    }

    public static String replaceTagValues(String inputString, VahanTaxParameters dobj) {
        String retString = inputString;
        if (TagFieldsMap == null) {
            getTagFields();
        }
        Set entries = TagFieldsMap.entrySet();
        Iterator entryIter = entries.iterator();
        while (entryIter.hasNext()) {
            Map.Entry entry = (Map.Entry) entryIter.next();
            Object key = entry.getKey();  // Get the key from the entry.
            Object value = entry.getValue();  // Get the value.

            try {
                Method method = findMethod(dobj.getClass(), "get" + value.toString().substring(0, 1).toUpperCase() + value.toString().substring(1));
                Class ab = method.getReturnType();

                Object retObj = method.invoke(dobj, null);
                if (ab.isInstance(new String())) {
                    retString = retString.replace(key.toString(), "'" + retObj.toString().toUpperCase() + "'");

                } else {
                    retString = retString.replace(key.toString(), retObj.toString().toUpperCase());
                }
            } catch (Exception e) {
                //LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        }
        return retString;
    }

    public static String replaceTagValues(String inputString) {
        String retString = inputString;
        String replaceChar = "";
        if (TagFieldsMapVerify == null) {
            getTagFields();
        }
        Set entries = TagFieldsMapVerify.entrySet();
        Iterator entryIter = entries.iterator();
        while (entryIter.hasNext()) {
            Map.Entry entry = (Map.Entry) entryIter.next();
            Object key = entry.getKey();  // Get the key from the entry.
            Object value = entry.getValue();  // Get the value.
            if (value.toString().equalsIgnoreCase("N")) {
                replaceChar = "1";
            } else if ("<50>,<93>,<39>,<40>".contains(key.toString())) {
                replaceChar = "''";
            } else if (value.toString().equalsIgnoreCase("C")) {
                replaceChar = "'Y'";
            } else {
                replaceChar = "";
            }

            try {
                retString = retString.replace(key.toString(), replaceChar);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        }
        return retString;
    }

    public static String makeIfCondition(String type, String condString, String rateString) {
        String retCondString = condString;
        String retRateString = rateString;
        String retType = type;
        String retString = "";

        if (TagFieldsMap == null) {
            getTagFields();
        }
        if (type.equalsIgnoreCase("T")) {
            retType = "Amount";
        } else if (type.equalsIgnoreCase("R")) {
            retType = "Rebate Amount";
        } else if (type.equalsIgnoreCase("S")) {
            retType = "Surcharge Amount";
        } else if (type.equalsIgnoreCase("M")) {
            retType = "Minimum Amount";
        } else if (type.equalsIgnoreCase("X")) {
            retType = "Maximum Amount";
        }
        Set entries = TagFieldsMap.entrySet();
        Iterator entryIter = entries.iterator();
        while (entryIter.hasNext()) {
            Map.Entry entry = (Map.Entry) entryIter.next();
            Object key = entry.getKey();  // Get the key from the entry.
            Object value = entry.getValue();  // Get the value.
            try {
                retCondString = retCondString.replace(key.toString(), "{" + value.toString() + "}");
                retRateString = retRateString.replace(key.toString(), "{" + value.toString() + "}");
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        if (retCondString.length() > 0) {
            retString = "<font color=\"blue\">If</font> (" + retCondString + ")<br/> <font color=\"blue\">then</font> <br/>";
        }
        retString = retString + retType + " <font color=\"blue\">=</font> (" + retRateString + ")";

        return retString;
    }

    private static Method findMethod(Class<?> clazz, String methodName) throws NoSuchMethodException {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new NoSuchMethodException();
    }

    public static boolean isCondition(String inputString, String whereIam) {
        boolean status = false;
        TransactionManagerLocal tmgr = null;
        try {
            if (inputString != null && !inputString.isEmpty() && inputString.trim().length() > 0) {
                String sql = "select COALESCE((" + inputString + "), false)::boolean as ret";
                tmgr = new TransactionManagerLocal("isCondition-" + whereIam);
                tmgr.prepareStatement(sql);
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    status = rs.getBoolean("ret");
                }
            }
        } catch (SQLException e) {
            status = false;
            LOGGER.error(whereIam + "  " + e.toString() + " " + e.getStackTrace()[0]);
            //throw new VahanException("Error in Configuration of set for state");

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

    public static boolean isVerifyFormula(String inputString) {
        boolean status = false;
        TransactionManagerReadOnly tmgr = null;

        try {
            String sql = "select (" + inputString + ")::boolean as ret";
            tmgr = new TransactionManagerReadOnly("isVerifyFormula");
            tmgr.prepareStatement(sql);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                status = rs.getBoolean("ret");
            }
            status = true;

        } catch (Exception e) {
            status = false;
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

    public static double getFormulaValue(String inputString) throws Exception {
        double dblAmt = 0;
        TransactionManagerReadOnly tmgr = null;
        try {
            String sql = "select (" + inputString + ")::numeric as ret";
            tmgr = new TransactionManagerReadOnly("getFormulaValue");
            tmgr.prepareStatement(sql);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                dblAmt = rs.getDouble("ret");
            }
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return dblAmt;
    }

    public static String getSqlFormulaValue(String inputString, String whereIam) throws Exception {
        String value = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            String sql = "select (" + inputString + ") as ret";
            tmgr = new TransactionManagerReadOnly("getSqlFormulaValue" + whereIam);
            PreparedStatement ps = tmgr.prepareStatement(sql);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                value = rs.getString("ret");
            }
        } catch (SQLException e) {
            LOGGER.error(whereIam);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return value;
    }

    public static Map regn_no_records(String appl_no, String regnNo, TransactionManager tmgr) throws VahanException {

        try {
            VehicleParameters vehParameters = null;
            Map retrun_data = new HashMap<String, String>();
            OwnerImpl ownImpl = new OwnerImpl();
            Owner_dobj ownDobj = ownImpl.set_Owner_appl_db_to_dobj(null, appl_no, null, 1);

            if (ownDobj == null) {//for TO-Retention new No Generation
                TmConfigurationDobj tmConfig = Util.getTmConfiguration();
                if (tmConfig != null) {
                    ToImpl toImpl = new ToImpl();
                    if (tmConfig.isTo_retention() && (tmConfig.isTo_retention_for_all_regn() || toImpl.isFancyNo(regnNo))) {
                        ownDobj = ownImpl.set_Owner_appl_db_to_dobj_with_state_off_cd(regnNo, null, null, 5);//TO-Retention
                    }
                    if (tmConfig.isScrap_veh_no_retain()) {
                        ownDobj = ownImpl.set_Owner_appl_db_to_dobj(regnNo, null, null, 5);//Scrap Vehicle-Retention
                    }
                }
            }
            List<Owner_dobj> toDetails = fetchTODetails(appl_no);
            List<Owner_dobj> convDetails = fetchConvDetails(tmgr, appl_no);
            List<Owner_dobj> reassignOwnerDobj = fetchReAssignDetails(tmgr, appl_no);
            List<Owner_dobj> renewalDetails = fetchRenewalDetails(tmgr, appl_no);

            PassengerPermitDetailImpl passengerPermitDetailImpl = new PassengerPermitDetailImpl();
            PassengerPermitDetailDobj permitDobj = passengerPermitDetailImpl.set_vt_permit_regnNo_to_dobj(regnNo, "");
            if (permitDobj == null) {
                permitDobj = TaxServer_Impl.getPermitInfoForSavedTax(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), appl_no, tmgr);
                if (Util.getSelectedSeat() != null && ownDobj != null && ownDobj.getPmt_type() > 0 && permitDobj != null
                        && !(Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ROLE_NEW_REGISTRATION_FEE
                        || Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ROLE_DEALER_CART_PAYMENT)) {
                    permitDobj.setPmt_type_code(String.valueOf(ownDobj.getPmt_type()));
                    permitDobj.setPmtCatg(String.valueOf(ownDobj.getPmt_catg()));
                }

                if (permitDobj == null) {
                    permitDobj = passengerPermitDetailImpl.getPermitHistory(regnNo, Util.getUserStateCode());
                }
            }

            if (ownDobj != null || !convDetails.isEmpty() || !reassignOwnerDobj.isEmpty() || !renewalDetails.isEmpty() || !toDetails.isEmpty()) {
                if (ownDobj != null) {
                    vehParameters = fillVehicleParametersFromDobj(ownDobj, permitDobj);
                }

                if (!convDetails.isEmpty()) {
                    vehParameters = fillVehicleParametersFromDobj(convDetails.get(0), permitDobj);
                }
                if (!renewalDetails.isEmpty() && renewalDetails.get(0).getState_cd().equalsIgnoreCase("RJ") && isTransport(renewalDetails.get(0).getVh_class(), null)) {
                    vehParameters = fillVehicleParametersFromDobj(renewalDetails.get(0), permitDobj);
                }
                if (!reassignOwnerDobj.isEmpty()) {
                    if (reassignOwnerDobj.get(0).getPmt_type() != 0 && reassignOwnerDobj.get(0).getPmt_catg() != 0) {
                        permitDobj = new PassengerPermitDetailDobj();
                        permitDobj.setPmt_type_code(String.valueOf(reassignOwnerDobj.get(0).getPmt_type()));
                        permitDobj.setPmtCatg(String.valueOf(reassignOwnerDobj.get(0).getPmt_catg()));
                    }
                    vehParameters = fillVehicleParametersFromDobj(reassignOwnerDobj.get(0), permitDobj);
                }
                if (!toDetails.isEmpty()) {
                    vehParameters = fillVehicleParametersFromDobj(toDetails.get(0), permitDobj);
                }
                String sql = "select state_cd, series_id, criteria_formula, prefix_series from vm_regn_series where state_cd=? and off_cd=?";
                PreparedStatement pstmt = tmgr.prepareStatement(sql);
                pstmt.setString(1, vehParameters.getSTATE_CD());
                pstmt.setInt(2, Util.getSelectedSeat().getOff_cd());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                int recordCount = 0;
                int series_id = 0;
                String foundSeries = "";
                while (rs.next()) {
                    String dataFromTable = "vm_regn_series(State:" + rs.getString("state_cd") + ",Sr:" + rs.getInt("series_id") + ")";
                    if (isCondition(replaceTagValues(rs.getString("criteria_formula"), vehParameters), dataFromTable)) {
                        recordCount++;
                        series_id = rs.getInt("series_id");
                        retrun_data.put("series_id", series_id);
                        retrun_data.put("prefix_series", rs.getString("prefix_series"));
                        foundSeries = foundSeries + ", " + rs.getString("prefix_series");
                    }
                }

                //select * from vm_series  where state_cd='KL'
                if (recordCount == 0 && "KL,TN".contains(vehParameters.getSTATE_CD())) {
                    sql = "select * from vm_series  where state_cd=?";
                    pstmt = tmgr.prepareStatement(sql);
                    pstmt.setString(1, vehParameters.getSTATE_CD());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    String dataFromTable = "vm_regn_series(State:" + vehParameters.getSTATE_CD() + ")";
                    while (rs.next()) {
                        if (isCondition(replaceTagValues(rs.getString("criteria_formula"), vehParameters), dataFromTable)) {
                            series_id = rs.getInt("series_id");
                            sql = "select * from vm_regn_series_future  where state_cd=? and off_cd=? and series_id=? order by entered_on limit 1";
                            pstmt = tmgr.prepareStatement(sql);
                            pstmt.setString(1, vehParameters.getSTATE_CD());
                            pstmt.setInt(2, Util.getSelectedSeat().getOff_cd());
                            pstmt.setInt(3, series_id);
                            RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                            if (rs1.next()) {
                                recordCount++;
                                retrun_data.put("prefix_series", rs1.getString("prefix_series"));
                                foundSeries = foundSeries + ", " + rs1.getString("prefix_series");
                                retrun_data.put("series_id", series_id);
                            }

                        }
                    }

                }

                if (recordCount == 0) {
                    throw new VahanException("No criteria conditions matched for generating registration no.");
                } else if (recordCount > 1) {
                    throw new VahanException("More than one registration series (" + foundSeries + ")");
                } else {
                    return retrun_data;
                }
            } else {
                throw new VahanException("Problem in criteria conditions for generating registration no.");
            }

        } catch (VahanException vme) {
            throw vme;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        }
    }

    public static VehicleParameters fillVehicleParametersFromDobj(Owner_dobj ownerDobj) {

        VehicleParameters taxParameters = new VehicleParameters();
        try {

            if (ownerDobj == null) {
                return taxParameters;
            }
            if (Util.getSelectedSeat() != null) {
                taxParameters.setOFF_CD(Util.getSelectedSeat().getOff_cd());
            }
            if (Util.getUserLoginOffCode() != null && Util.getUserLoginOffCode() >= 0 && "RJ".equals(ownerDobj.getState_cd())) {
                switch (Util.getUserCategory()) {
                    case TableConstants.USER_CATG_DEALER:
                        if (Util.getSelectedSeat() != null && TableConstants.TM_ROLE_DEALER_APPROVAL == Util.getSelectedSeat().getAction_cd()) {
                            taxParameters.setLOGIN_OFF_CD(Util.getSelectedSeat().getOff_cd());
                        } else {
                            taxParameters.setLOGIN_OFF_CD(Util.getUserLoginOffCode());
                        }
                        break;
                    case TableConstants.USER_CATG_OFF_STAFF:
                        if (!CommonUtils.isNullOrBlank(ownerDobj.getAppl_no())) {
                            int loginOffcd = ServerUtil.getOfficeCdForDealerTempAppl(ownerDobj.getAppl_no(), ownerDobj.getState_cd(), "");
                            taxParameters.setLOGIN_OFF_CD(loginOffcd);
                            if (Util.getSelectedSeat().getAction_cd() == TableConstants.TM_NEW_RC_VERIFICATION && taxParameters.getOFF_CD() != null && taxParameters.getOFF_CD() == loginOffcd) {
                                taxParameters.setPREV_OFF_CD(loginOffcd);
                            }
                        }
                        break;
                }
            } else {
                taxParameters.setLOGIN_OFF_CD(Util.getUserLoginOffCode());
            }
            if (ownerDobj.getOff_cd() > 0) {
                taxParameters.setOFF_CD(ownerDobj.getOff_cd());
            }
            taxParameters.setAC_FITTED(ownerDobj.getAc_fitted());
            // taxParameters.setCALENDARMONTH(ownerDobj.getc);
            taxParameters.setCC(ownerDobj.getCubic_cap() == null ? 0.0f : ownerDobj.getCubic_cap());//float type
            // taxParameters.setDELAYDAYS(ownerDo);
            taxParameters.setFLOOR_AREA(ownerDobj.getFloor_area());
            taxParameters.setFUEL(ownerDobj.getFuel());
            taxParameters.setHP(ownerDobj.getHp() == null ? 0.0f : ownerDobj.getHp());
            taxParameters.setLD_WT(ownerDobj.getLd_wt());
            taxParameters.setOWNER_CD(ownerDobj.getOwner_cd());
            if (ownerDobj.getDob_temp() != null
                    && ownerDobj.getDob_temp().getTemp_regn_type() != null
                    && ownerDobj.getDob_temp().getTemp_regn_type().trim().length() > 0) {
                taxParameters.setREGN_TYPE(ownerDobj.getDob_temp().getTemp_regn_type());
            } else {
                taxParameters.setREGN_TYPE(ownerDobj.getRegn_type());
            }
            taxParameters.setSALE_AMT(ownerDobj.getSale_amt());
            taxParameters.setSEAT_CAP(ownerDobj.getSeat_cap());
            taxParameters.setSLEEPAR_CAP(ownerDobj.getSleeper_cap());
            taxParameters.setSTAND_CAP(ownerDobj.getStand_cap());
            taxParameters.setSTATE_CD(ownerDobj.getState_cd());
            taxParameters.setUNLD_WT(ownerDobj.getUnld_wt());
            taxParameters.setVH_CLASS(ownerDobj.getVh_class());
            if (Util.getUserStateCode() != null && "RJ".equals(Util.getUserStateCode())) {
                String reqdTaxMode = ownerDobj.getRqrd_tax_modes();
                if (reqdTaxMode != null) {
                    String taxMode = ServerUtil.getTaxModeFromReqdTaxMode(reqdTaxMode);
                    taxParameters.setTAX_MODE(taxMode);
                }
            } else {
                taxParameters.setTAX_MODE(ownerDobj.getTax_mode());
            }
            if (ownerDobj.getRegn_dt() != null) {
                taxParameters.setREGN_DATE(DateUtils.parseDate(ownerDobj.getRegn_dt()));
                taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getRegn_dt()));
            } else {
                taxParameters.setREGN_DATE(DateUtils.parseDate(new Date()));
                taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new Date()));
            }

            taxParameters.setVCH_CATG(ownerDobj.getVch_catg() != null ? ownerDobj.getVch_catg().trim() : null);
            taxParameters.setVH_CLASS(ownerDobj.getVh_class());
            taxParameters.setSEAT_CAP(ownerDobj.getSeat_cap());
            if (ownerDobj.getPurchase_dt() != null) {
                taxParameters.setVCH_AGE((int) Math.ceil(DateUtils.getDate1MinusDate2_Months(ownerDobj.getPurchase_dt(), new Date()) / 12.0));
            }
            taxParameters.setAUDIO_FITTED(ownerDobj.getAudio_fitted());
            taxParameters.setVIDEO_FITTED(ownerDobj.getVideo_fitted());

            if (ownerDobj.getVehType() <= 0) {
                taxParameters.setVCH_TYPE(ServerUtil.VehicleClassType(ownerDobj.getVh_class()));
            } else {
                taxParameters.setVCH_TYPE(ownerDobj.getVehType());
            }
            taxParameters.setREGN_NO(ownerDobj.getRegn_no());
            taxParameters.setPERMIT_TYPE(ownerDobj.getPmt_type());
            taxParameters.setPERMIT_SUB_CATG(ownerDobj.getPmt_catg());
            taxParameters.setOTHER_CRITERIA(ownerDobj.getOther_criteria());
            taxParameters.setFIT_UPTO(ownerDobj.getFit_upto() != null ? JSFUtils.getDateInDD_MMM_YYYY(DateUtils.parseDate(ownerDobj.getFit_upto())) : null);
            if (taxParameters.getFIT_UPTO() == null || taxParameters.getFIT_UPTO().isEmpty() || taxParameters.getFIT_UPTO().trim().length() <= 0) {
                taxParameters.setFIT_UPTO(JSFUtils.getDateInDD_MMM_YYYY(DateUtils.parseDate(new Date())));
            }
            taxParameters.setNORMS(ownerDobj.getNorms());
            taxParameters.setVEH_PURCHASE_AS(ownerDobj.getVch_purchase_as());
            if (ownerDobj.getPurchase_dt() != null) {
                taxParameters.setPURCHASE_DATE(DateUtil.parseDateYYYYMMDDToString(ownerDobj.getPurchase_dt()));
            }
            if (ownerDobj.getOwner_identity() != null) {
                taxParameters.setOWNER_CATG(ownerDobj.getOwner_identity().getOwnerCatg());
            }
            if (ownerDobj.getManu_yr() != null && ownerDobj.getManu_mon() != null) {
                taxParameters.setMANUFACTUR_DATE(ownerDobj.getManu_yr().toString() + ((ownerDobj.getManu_mon() < 10) ? "0" + ownerDobj.getManu_mon() : ownerDobj.getManu_mon().toString()));
            }
            if (ownerDobj.getDob_temp() != null) {
                taxParameters.setTMP_PURPOSE(ownerDobj.getDob_temp().getPurpose());
            }
        } catch (VahanException ve) {
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return taxParameters;
    }

    public static VehicleParameters fillVehicleParametersFromDobj(Owner_dobj ownerDobj, PassengerPermitDetailDobj permitDobj) throws VahanException {

        VehicleParameters taxParameters = new VehicleParameters();
        try {

            if (ownerDobj == null) {
                return taxParameters;
            }
            if (Util.getSelectedSeat() != null) {
                taxParameters.setOFF_CD(Util.getSelectedSeat().getOff_cd());
            }
            if (ownerDobj.getOff_cd() > 0) {
                taxParameters.setOFF_CD(ownerDobj.getOff_cd());
            }
            taxParameters.setAC_FITTED(ownerDobj.getAc_fitted());
            // taxParameters.setCALENDARMONTH(ownerDobj.getc);
            taxParameters.setCC(ownerDobj.getCubic_cap() == null ? 0.0f : ownerDobj.getCubic_cap());//float type
            // taxParameters.setDELAYDAYS(ownerDo);
            taxParameters.setFLOOR_AREA(ownerDobj.getFloor_area());
            taxParameters.setFUEL(ownerDobj.getFuel());
            taxParameters.setHP(ownerDobj.getHp() == null ? 0.0f : ownerDobj.getHp());
            taxParameters.setLD_WT(ownerDobj.getLd_wt());
            taxParameters.setOWNER_CD(ownerDobj.getOwner_cd());
            if (ownerDobj.getDob_temp() != null
                    && ownerDobj.getDob_temp().getTemp_regn_type() != null
                    && ownerDobj.getDob_temp().getTemp_regn_type().trim().length() > 0) {
                taxParameters.setREGN_TYPE(ownerDobj.getDob_temp().getTemp_regn_type());
            } else {
                taxParameters.setREGN_TYPE(ownerDobj.getRegn_type());
            }
            taxParameters.setSALE_AMT(ownerDobj.getSale_amt());
            taxParameters.setSEAT_CAP(ownerDobj.getSeat_cap());
            taxParameters.setSLEEPAR_CAP(ownerDobj.getSleeper_cap());
            taxParameters.setSTAND_CAP(ownerDobj.getStand_cap());
            taxParameters.setSTATE_CD(ownerDobj.getState_cd());
            taxParameters.setVEH_PURCHASE_AS(ownerDobj.getVch_purchase_as());
            taxParameters.setUNLD_WT(ownerDobj.getUnld_wt());
            taxParameters.setVH_CLASS(ownerDobj.getVh_class());
            taxParameters.setTAX_MODE(ownerDobj.getTax_mode());
            taxParameters.setREGN_NO(ownerDobj.getRegn_no());
            if (ownerDobj.getRegn_dt() != null) {
                taxParameters.setREGN_DATE(DateUtils.parseDate(ownerDobj.getRegn_dt()));
                taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getRegn_dt()));
            } else {
                taxParameters.setREGN_DATE(DateUtils.parseDate(new Date()));
                taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new Date()));
            }

            taxParameters.setVCH_CATG(ownerDobj.getVch_catg() != null ? ownerDobj.getVch_catg().trim() : null);
            taxParameters.setVH_CLASS(ownerDobj.getVh_class());
            taxParameters.setSEAT_CAP(ownerDobj.getSeat_cap());
            if (ownerDobj.getPurchase_dt() != null) {
                taxParameters.setVCH_AGE((int) Math.ceil(DateUtils.getDate1MinusDate2_Months(ownerDobj.getPurchase_dt(), new Date()) / 12.0));
            }
            taxParameters.setAUDIO_FITTED(ownerDobj.getAudio_fitted());
            taxParameters.setVIDEO_FITTED(ownerDobj.getVideo_fitted());
            taxParameters.setOTHER_CRITERIA(ownerDobj.getOther_criteria());
            taxParameters.setPERMIT_TYPE(ownerDobj.getPmt_type());
            taxParameters.setPERMIT_SUB_CATG(ownerDobj.getPmt_catg());
            if (permitDobj != null) {
                if (permitDobj.getPmt_type_code() != null && !permitDobj.getPmt_type_code().equals("")) {
                    taxParameters.setPERMIT_TYPE(Integer.parseInt(permitDobj.getPmt_type_code().trim()));
                }
                if (permitDobj.getPmtCatg() != null && !permitDobj.getPmtCatg().equals("")) {
                    taxParameters.setPERMIT_SUB_CATG(Integer.parseInt(permitDobj.getPmtCatg().trim()));
                }
                if (permitDobj.getServices_TYPE() != null && !permitDobj.getServices_TYPE().equals("")) {
                    taxParameters.setSERVICE_TYPE(Integer.parseInt(permitDobj.getServices_TYPE().trim()));
                }
                if (permitDobj.getDomain_CODE() != null && !permitDobj.getDomain_CODE().equals("")) {
                    taxParameters.setDOMAIN_CD(Integer.parseInt(permitDobj.getDomain_CODE().trim()));
                }
                if (permitDobj.getNumberOfTrips() != null && !permitDobj.getNumberOfTrips().equals("")) {
                    taxParameters.setNO_OF_ROUTES(Integer.parseInt(permitDobj.getNumberOfTrips().trim()));
                }
                if (permitDobj.getRout_length() != null && !permitDobj.getRout_length().equals("")) {
                    taxParameters.setROUTE_LENGTH(Double.parseDouble(permitDobj.getRout_length().trim()));
                }
                if (permitDobj.getRegion_covered() != null && !permitDobj.getRegion_covered().equals("") && JSFUtils.isNumeric(permitDobj.getRegion_covered().trim())) {
                    taxParameters.setREGION_COUNT(Integer.parseInt(permitDobj.getRegion_covered().trim()));
                }
                if (permitDobj.getMultiRegion() != null && !permitDobj.getMultiRegion().equals("") && permitDobj.getMultiRegion().equalsIgnoreCase("TRUE")) {
                    taxParameters.setMULTI_REGION(permitDobj.getMultiRegion());
                }
            }
            taxParameters.setVCH_IMPORTED(ownerDobj.getImported_vch());
            if (ownerDobj.getVehType() <= 0) {
                taxParameters.setVCH_TYPE(ServerUtil.VehicleClassType(ownerDobj.getVh_class()));
            } else {
                taxParameters.setVCH_TYPE(ownerDobj.getVehType());
            }
            taxParameters.setFIT_UPTO(ownerDobj.getFit_upto() != null ? JSFUtils.getDateInDD_MMM_YYYY(DateUtils.parseDate(ownerDobj.getFit_upto())) : null);
            if (taxParameters.getFIT_UPTO() == null || taxParameters.getFIT_UPTO().isEmpty() || taxParameters.getFIT_UPTO().trim().length() <= 0) {
                taxParameters.setFIT_UPTO(JSFUtils.getDateInDD_MMM_YYYY(DateUtils.parseDate(new Date())));
            }
            taxParameters.setNORMS(ownerDobj.getNorms());
            taxParameters.setGCW(ownerDobj.getGcw());
            if (Util.getUserLoginOffCode() != null) {
                taxParameters.setLOGIN_OFF_CD(Util.getUserLoginOffCode());
            }
            if (ownerDobj.getRegn_upto() != null) {
                taxParameters.setREGN_UPTO(DateUtils.parseDate(ownerDobj.getRegn_upto()));
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

        return taxParameters;
    }

    public static VahanTaxParameters fillTaxParametersFromDobj(Owner_dobj ownerDobj, PassengerPermitDetailDobj permitDobj) throws VahanException {

        VahanTaxParameters taxParameters = new VahanTaxParameters();
        try {

            if (ownerDobj == null) {
                return taxParameters;
            }
            if (Util.getSelectedSeat() != null) {
                taxParameters.setOFF_CD(Util.getSelectedSeat().getOff_cd());
            }
            taxParameters.setAC_FITTED(ownerDobj.getAc_fitted());
            taxParameters.setCC(ownerDobj.getCubic_cap() == null ? 0.0f : ownerDobj.getCubic_cap());//float type
            taxParameters.setFLOOR_AREA(ownerDobj.getFloor_area());
            taxParameters.setFUEL(ownerDobj.getFuel());
            taxParameters.setHP(ownerDobj.getHp() == null ? 0.0f : ownerDobj.getHp());
            taxParameters.setLD_WT(ownerDobj.getLd_wt());
            taxParameters.setOWNER_CD(ownerDobj.getOwner_cd());
            taxParameters.setREGN_TYPE(ownerDobj.getRegn_type());
            taxParameters.setSALE_AMT(ownerDobj.getSale_amt());
            taxParameters.setSEAT_CAP(ownerDobj.getSeat_cap());
            taxParameters.setSLEEPAR_CAP(ownerDobj.getSleeper_cap());
            taxParameters.setSTAND_CAP(ownerDobj.getStand_cap());
            taxParameters.setSTATE_CD(ownerDobj.getState_cd());
            taxParameters.setUNLD_WT(ownerDobj.getUnld_wt());
            taxParameters.setVH_CLASS(ownerDobj.getVh_class());
            taxParameters.setTAX_MODE(ownerDobj.getTax_mode());
            taxParameters.setNORMS(ownerDobj.getNorms());

            if (ownerDobj.getPurchase_dt() != null) {
                taxParameters.setPURCHASE_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
            } else {
                taxParameters.setPURCHASE_DATE(DateUtils.parseDate(new Date()));
            }

            if (ownerDobj.getRegn_dt() != null) {
                taxParameters.setREGN_DATE(DateUtils.parseDate(ownerDobj.getRegn_dt()));
                taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getRegn_dt()));
            } else {
                taxParameters.setREGN_DATE(DateUtils.parseDate(new Date()));
                taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new Date()));
            }

            taxParameters.setVCH_IMPORTED(ownerDobj.getImported_vch());
            taxParameters.setVCH_CATG(ownerDobj.getVch_catg());
            taxParameters.setOTHER_CRITERIA(ownerDobj.getOther_criteria());
            taxParameters.setVEH_PURCHASE_AS(ownerDobj.getVch_purchase_as());
            taxParameters.setAUDIO_FITTED(ownerDobj.getAudio_fitted());
            taxParameters.setVIDEO_FITTED(ownerDobj.getVideo_fitted());
            //taxParameters.setVCHAGE((int) Math.ceil(DateUtils.getDate1MinusDate2_Months(ownerDobj.getPurchase_dt(), new Date()) / 12.0));
            taxParameters.setPERMIT_TYPE(ownerDobj.getPmt_type());
            taxParameters.setPERMIT_SUB_CATG(ownerDobj.getPmt_catg());
            if (ownerDobj.getInsDobj() != null) {
                taxParameters.setIDV((int) ownerDobj.getInsDobj().getIdv());
            }
            if (permitDobj != null) {
                if (permitDobj.getPmt_type_code() != null && !permitDobj.getPmt_type_code().equals("")) {
                    taxParameters.setPERMIT_TYPE(Integer.parseInt(permitDobj.getPmt_type_code().trim()));
                }
                if (permitDobj.getPmtCatg() != null && !permitDobj.getPmtCatg().equals("")) {
                    taxParameters.setPERMIT_SUB_CATG(Integer.parseInt(permitDobj.getPmtCatg().trim()));
                }
                if (permitDobj.getServices_TYPE() != null && !permitDobj.getServices_TYPE().equals("")) {
                    taxParameters.setSERVICE_TYPE(Integer.parseInt(permitDobj.getServices_TYPE().trim()));
                }
                if (permitDobj.getDomain_CODE() != null && !permitDobj.getDomain_CODE().equals("")) {
                    taxParameters.setDOMAIN_CD(Integer.parseInt(permitDobj.getDomain_CODE().trim()));
                }
                if (permitDobj.getNumberOfTrips() != null && !permitDobj.getNumberOfTrips().equals("")) {
                    taxParameters.setNO_OF_ROUTES(Integer.parseInt(permitDobj.getNumberOfTrips().trim()));
                }
                if (permitDobj.getRout_length() != null && !permitDobj.getRout_length().equals("")) {
                    taxParameters.setROUTE_LENGTH(Double.parseDouble(permitDobj.getRout_length().trim()));
                }
            }

            if (ownerDobj.getVehType() <= 0) {
                taxParameters.setVCH_TYPE(ServerUtil.VehicleClassType(ownerDobj.getVh_class()));
            } else {
                taxParameters.setVCH_TYPE(ownerDobj.getVehType());
            }
            if (ownerDobj.getLinkedRegnNo() != null) {
                fillTaxParametersFromDobjForPrimeMover(taxParameters, ownerDobj.getLinkedRegnNo(),
                        ownerDobj.getState_cd(), ownerDobj.getOff_cd());
            }
            taxParameters.setGCW(ownerDobj.getGcw());
            taxParameters.setNO_OF_TYRES(ownerDobj.getNumberOfTyres());
            if (ownerDobj.getAxleDobj() != null) {
                taxParameters.setNO_OF_AXLE(ownerDobj.getAxleDobj().getNoOfAxle());
            }
            if (ownerDobj.getListTrailerDobj() != null && ownerDobj.getListTrailerDobj().size() > 0) {
                taxParameters.setNO_OF_ATTACHED_TRAILERS(ownerDobj.getListTrailerDobj().size());
            } else {
                taxParameters.setNO_OF_ATTACHED_TRAILERS(0);
            }
            if (Util.getUserLoginOffCode() != null) {
                taxParameters.setLOGIN_OFF_CD(Util.getUserLoginOffCode());
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return taxParameters;
    }

    private static void fillTaxParametersFromDobjForPrimeMover(VahanTaxParameters vahanTax, String pmRegnNo, String statCd, int offCd) {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("fillTaxParametersFromDobjForPrimeMover");
            String sql = "Select *,state.descr as current_state_name,dist.descr as current_district_name from " + TableList.VT_OWNER + " owner "
                    + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                    + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                    + " where owner.regn_no= ? and owner.state_cd=? and owner.off_cd=? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, pmRegnNo);
            ps.setString(2, statCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            Owner_dobj owDobj = new OwnerImpl().fillFrom_VT_OwnerDobj(rs);
            if (owDobj != null) {
                vahanTax.setPM_SALE_AMT(owDobj.getSale_amt());
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex);
                }
            }
        }

    }

    private static List<Owner_dobj> fetchConvDetails(TransactionManager tmgr, String appl_no) throws VahanException {
        List<Owner_dobj> ownerDetails = new ArrayList<>();
        String regn_no = null;
        int vh_class = 0;
        String vhCatg = null;
        String sqlConv = "select * from  " + TableList.VA_CONVERSION
                + " where appl_no=?";
        RowSet rsConv = null;
        try {
            PreparedStatement pstmtConv = tmgr.prepareStatement(sqlConv);
            pstmtConv.setString(1, appl_no);
            rsConv = tmgr.fetchDetachedRowSet_No_release();
            if (rsConv.next()) {
                regn_no = rsConv.getString("old_regn_no");
                vh_class = rsConv.getInt("new_vch_class");
                vhCatg = rsConv.getString("new_vch_catg");
            }
            if (regn_no != null) {
                ownerDetails = ServerUtil.getExistingOwnerDetails(regn_no);
                ownerDetails.get(0).setVh_class(vh_class);
                ownerDetails.get(0).setVehType(ServerUtil.VehicleClassType(vh_class));
                if (!CommonUtils.isNullOrBlank(vhCatg)) {
                    ownerDetails.get(0).setVch_catg(vhCatg);
                }

                pstmtConv = tmgr.prepareStatement("SELECT pmt_type, pmt_catg, num_generation \n"
                        + "  FROM " + TableList.VA_NUM_GEN_PERMITDETAILS + " where appl_no=?");
                pstmtConv.setString(1, appl_no);
                RowSet rsPmtType = tmgr.fetchDetachedRowSet_No_release();
                if (rsPmtType.next()) {
                    ownerDetails.get(0).setPmt_type(rsPmtType.getInt("pmt_type"));
                    ownerDetails.get(0).setPmt_catg(rsPmtType.getInt("pmt_catg"));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in fetching of Conversion Details");
        }
        return ownerDetails;
    }

    private static List<Owner_dobj> fetchReAssignDetails(TransactionManager tmgr, String appl_no) throws VahanException {
        List<Owner_dobj> ownerDetails = new ArrayList<>();
        String regn_no = null;
        int pmt_type = 0;
        int pmt_catg = 0;
        String sqlConv = "select * from  " + TableList.VA_RE_ASSIGN
                + " where appl_no=?";
        RowSet rsConv = null;
        try {
            PreparedStatement pstmtConv = tmgr.prepareStatement(sqlConv);
            pstmtConv.setString(1, appl_no);
            rsConv = tmgr.fetchDetachedRowSet_No_release();
            if (rsConv.next()) {
                regn_no = rsConv.getString("old_regn_no");
                if (rsConv.getInt("pmt_type") != -1) {
                    pmt_type = rsConv.getInt("pmt_type");
                }
                if (rsConv.getInt("pmt_catg") != -1) {
                    pmt_catg = rsConv.getInt("pmt_catg");
                }
            }
            if (regn_no != null) {
                ownerDetails = ServerUtil.getExistingOwnerDetails(regn_no);
                ownerDetails.get(0).setPmt_type(pmt_type);
                ownerDetails.get(0).setPmt_catg(pmt_catg);
            }

        } catch (SQLException e) {
            //LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in fetching of Re Assign Details");
        }
        return ownerDetails;
    }

    private static List<Owner_dobj> fetchRenewalDetails(TransactionManager tmgr, String appl_no) throws VahanException {
        List<Owner_dobj> ownerDetails = new ArrayList<>();
        String regn_no = null;
        String sqlConv = "select * from  " + TableList.VA_RENEWAL
                + " where appl_no=?";
        RowSet rsConv = null;
        try {
            PreparedStatement pstmtConv = tmgr.prepareStatement(sqlConv);
            pstmtConv.setString(1, appl_no);
            rsConv = tmgr.fetchDetachedRowSet_No_release();
            if (rsConv.next()) {
                regn_no = rsConv.getString("regn_no");
            }
            if (regn_no != null) {
                ownerDetails = ServerUtil.getExistingOwnerDetails(regn_no);
                pstmtConv = tmgr.prepareStatement("SELECT pmt_type, pmt_catg, num_generation \n"
                        + "  FROM " + TableList.VA_NUM_GEN_PERMITDETAILS + " where appl_no=?");
                pstmtConv.setString(1, appl_no);
                RowSet rsPmtType = tmgr.fetchDetachedRowSet_No_release();
                if (rsPmtType.next()) {
                    ownerDetails.get(0).setPmt_type(rsPmtType.getInt("pmt_type"));
                    ownerDetails.get(0).setPmt_catg(rsPmtType.getInt("pmt_catg"));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in fetching of Renewal Details");
        }
        return ownerDetails;
    }

    // makeRegistrationSeriesDesc for Registration series managment form - Formula description.
    public static String makeRegistrationSeriesDesc(String criteriaString) {
        String retCriteriaString = criteriaString;
        String retString = "";
        if (TagFieldsMap == null) {
            getTagFields();
        }
        Set entries = TagFieldsMap.entrySet();
        Iterator entryIter = entries.iterator();
        while (entryIter.hasNext()) {
            Map.Entry entry = (Map.Entry) entryIter.next();
            Object key = entry.getKey();  // Get the key from the entry.
            Object value = entry.getValue();  // Get the value.
            try {
                retCriteriaString = retCriteriaString.replace(key.toString(), "{" + value.toString() + "}");

            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        if (retCriteriaString.length() > 0) {
//            retString = "(" + retCriteriaString + ")";
            retString = retCriteriaString;
        }
        return retString;
    }

    public static VehicleParameters fillPermitParametersFromDobj(Owner_dobj ownerDobj, PassengerPermitDetailDobj pmtDobj, int pDay, int pMonth, int pCMonth, int pYear, int exem_amount, int fine_to_be_taken, int monthly_tax_amt, int rLength) throws VahanException {
        VehicleParameters pmtParameters = new VehicleParameters();
        try {
            pmtParameters.setOFF_CD(Util.getSelectedSeat().getOff_cd());
            if (ownerDobj != null) {
                if (ownerDobj.getVh_class() != 0) {
                    pmtParameters.setVH_CLASS(ownerDobj.getVh_class());
                }
                if (!CommonUtils.isNullOrBlank(ownerDobj.getVch_catg())) {
                    pmtParameters.setVCH_CATG(ownerDobj.getVch_catg());
                }
                if (!CommonUtils.isNullOrBlank(ownerDobj.getState_cd())) {
                    pmtParameters.setSTATE_CD(ownerDobj.getState_cd());
                }
                if (!CommonUtils.isNullOrBlank(ownerDobj.getRegn_no())) {
                    pmtParameters.setREGN_NO(ownerDobj.getRegn_no());
                }
                if (ownerDobj.getFuel() > 0) {
                    pmtParameters.setFUEL(ownerDobj.getFuel());
                }
                pmtParameters.setUNLD_WT(ownerDobj.getUnld_wt());
                pmtParameters.setLD_WT(ownerDobj.getLd_wt());
                if (!CommonUtils.isNullOrBlank(String.valueOf(ownerDobj.getGcw()))) {
                    pmtParameters.setGCW(ownerDobj.getGcw());
                }
                pmtParameters.setSEAT_CAP(ownerDobj.getSeat_cap());
                pmtParameters.setSLEEPAR_CAP(ownerDobj.getSleeper_cap());
                pmtParameters.setSTAND_CAP(ownerDobj.getStand_cap());
                if (ownerDobj.getOwner_identity() != null) {
                    pmtParameters.setOWN_CATG(ownerDobj.getOwner_identity().getOwnerCatg());
                } else {
                    pmtParameters.setOWN_CATG(0);
                }
                if (!CommonUtils.isNullOrBlank(ownerDobj.getTax_mode())) {
                    pmtParameters.setTAX_MODE(ownerDobj.getTax_mode());
                }
                if (ownerDobj.getOwner_cd() > 0) {
                    pmtParameters.setOWNER_CD(ownerDobj.getOwner_cd());
                }
                if (ownerDobj.getBlackListedVehicleDobj() != null && ownerDobj.getBlackListedVehicleDobj().getComplain_type() > 0) {
                    pmtParameters.setBLACKLISTCODE(ownerDobj.getBlackListedVehicleDobj().getComplain_type());
                } else {
                    pmtParameters.setBLACKLISTCODE(0);
                }
                if (ownerDobj.getOther_criteria() > 0) {
                    pmtParameters.setOTHER_CRITERIA(ownerDobj.getOther_criteria());
                }
            }
            if (pmtDobj != null) {
                if (!CommonUtils.isNullOrBlank(pmtDobj.getPmt_type_code())) {
                    pmtParameters.setPERMIT_TYPE(Integer.parseInt(pmtDobj.getPmt_type_code().trim()));
                }
                if (!CommonUtils.isNullOrBlank(pmtDobj.getPmtCatg())) {
                    pmtParameters.setPERMIT_SUB_CATG(Integer.parseInt(pmtDobj.getPmtCatg().trim()));
                }

                if (!CommonUtils.isNullOrBlank(pmtDobj.getRout_length())) {
                    pmtParameters.setROUTE_COUNT(Integer.parseInt(pmtDobj.getRout_length().trim()));
                }

                if (!CommonUtils.isNullOrBlank(pmtDobj.getRegion_covered())) {
                    pmtParameters.setREGION_COUNT(Integer.parseInt(pmtDobj.getRegion_covered().trim()));
                }
                if (!CommonUtils.isNullOrBlank(pmtDobj.getState_covered())) {
                    pmtParameters.setSTATE_COUNT(Integer.parseInt(pmtDobj.getState_covered().trim()));
                }

                if (pmtDobj.getMultiRegion().equalsIgnoreCase("TRUE")) {
                    pmtParameters.setMULTI_REGION(pmtDobj.getMultiRegion());
                }
                if (pmtDobj.isHypth()) {
                    pmtParameters.setVEHICLE_HYPTH(String.valueOf(pmtDobj.isHypth()));
                }
                if (!CommonUtils.isNullOrBlank(pmtDobj.getServices_TYPE())) {
                    pmtParameters.setSERVICE_TYPE(Integer.parseInt(pmtDobj.getServices_TYPE().trim()));
                }
                pmtParameters.setMULTI_DOC(pmtDobj.getMultiDoc());
                if (!CommonUtils.isNullOrBlank(pmtDobj.getTransferForexempted())) {
                    pmtParameters.setEXCEM_FLAG(pmtDobj.getTransferForexempted());
                }
                if (!CommonUtils.isNullOrBlank(pmtDobj.getDupPmtReason())) {
                    pmtParameters.setDUP_PMT_REASON(pmtDobj.getDupPmtReason());
                }
                if (!CommonUtils.isNullOrBlank(pmtDobj.getDomainCovered())) {
                    pmtParameters.setREGION_DESCR(pmtDobj.getDomainCovered());
                }
                if (!CommonUtils.isNullOrBlank(String.valueOf(pmtDobj.getTransPurCd()))) {
                    pmtParameters.setTRANSACTION_PUR_CD(pmtDobj.getTransPurCd());
                }
            }
            if (pDay != 0) {
                pmtParameters.setPMT_DAYS(pDay);
            }
            if (rLength != 0) {
                pmtParameters.setROUTE_LENGTH(Double.valueOf(rLength));
            }
            if (pMonth != 0) {
                pmtParameters.setPMT_MONTHS(pMonth);
            }
            if (pCMonth != 0) {
                pmtParameters.setPMT_CEL_MONTH(pCMonth);
            }
            if (pYear != 0) {
                pmtParameters.setPMT_YEAR(pYear);
            }
            if (exem_amount != 0) {
                pmtParameters.setEXEM_AMOUNT(exem_amount);
            } else {
                pmtParameters.setEXEM_AMOUNT(0);
            }
            if (fine_to_be_taken != 0) {
                pmtParameters.setFINE_TO_BE_TAKEN(fine_to_be_taken);
            } else {
                pmtParameters.setFINE_TO_BE_TAKEN(0);
            }
            if (monthly_tax_amt != 0) {
                pmtParameters.setPMT_AMOUNT(monthly_tax_amt);
            } else {
                pmtParameters.setPMT_AMOUNT(0);
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting the Permit Details. Please try after sometime or contact Administrator.");
        }

        return pmtParameters;
    }

    public static String replaceTagPermitValues(String inputString, VehicleParameters dobj) {
        String retString = inputString;
        if (TagFieldsMap == null) {
            getTagFields();
        }
        Set entries = TagFieldsMap.entrySet();
        Iterator entryIter = entries.iterator();
        while (entryIter.hasNext()) {
            Map.Entry entry = (Map.Entry) entryIter.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            try {
                Method method = findMethod(dobj.getClass(), "get" + value.toString().substring(0, 1).toUpperCase() + value.toString().substring(1));
                Class ab = method.getReturnType();
                Object retObj = method.invoke(dobj, null);
                if (ab.isInstance(new String())) {
                    retString = retString.replace(key.toString(), "'" + retObj.toString().toUpperCase() + "'");
                } else {
                    retString = retString.replace(key.toString(), retObj.toString().toUpperCase());
                }
            } catch (Exception e) {
            }
        }
        return retString;
    }

    private static List<Owner_dobj> fetchTODetails(String appl_no) throws VahanException {
        List<Owner_dobj> ownerDetails = new ArrayList<>();
        int ownerCd = 0;
        int saleAmount = 0;
        String regnNo = null;

        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("fetchTODetails");
            String sql = "select * from  " + TableList.VA_TO
                    + " where appl_no=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                regnNo = rs.getString("regn_no");
                ownerCd = rs.getInt("owner_cd");
                saleAmount = rs.getInt("sale_amt");
            }
            if (regnNo != null) {
                ownerDetails = ServerUtil.getExistingOwnerDetails(regnNo);
                ownerDetails.get(0).setOwner_cd(ownerCd);
                ownerDetails.get(0).setSale_amt(saleAmount);
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in fetching of To Details");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex);
                }
            }
        }

        return ownerDetails;
    }
}
