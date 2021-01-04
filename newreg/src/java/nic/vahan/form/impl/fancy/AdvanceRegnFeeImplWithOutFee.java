/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.fancy;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class AdvanceRegnFeeImplWithOutFee {

    private static final Logger LOGGER = Logger.getLogger(AdvanceRegnFeeImplWithOutFee.class);
    public static Map TagFieldsMap = null;
    public static Map TagFieldsMapVerify = null;
    public static Map TagDescrDisplay = null;

    public static List<AdvanceRegnNo_dobj> getFancyNumbers(String stateCode, String regnType, String vehClass) {

        List<AdvanceRegnNo_dobj> mp = new ArrayList<>();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;

        VehicleParameters vehParameters = new VehicleParameters();
        try {
            vehParameters.setVCH_TYPE(Integer.parseInt(regnType));
            vehParameters.setVH_CLASS(Integer.parseInt(vehClass));
            vehParameters.setOFF_CD(Util.getSelectedSeat().getOff_cd());
            String sql = "select fancy_number,booking_fee,condition_formula from vm_fancy_mast where state_cd=? order by fancy_number,booking_fee ";
            tmgr = new TransactionManager("getFancyNumbers");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (regnType.equalsIgnoreCase("0")) {
                while (rs.next()) {
                    AdvanceRegnNo_dobj dobj = new AdvanceRegnNo_dobj();
                    dobj.setRegn_no(rs.getString("fancy_number"));
                    dobj.setTotal_amt(rs.getInt("booking_fee"));
                    mp.add(dobj);
                }
            } else {
                while (rs.next()) {
                    if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getFancyNumbers-2")) {
                        AdvanceRegnNo_dobj dobj = new AdvanceRegnNo_dobj();
                        dobj.setRegn_no(rs.getString("fancy_number"));
                        dobj.setTotal_amt(rs.getInt("booking_fee"));
                        mp.add(dobj);
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
        return mp;
    }

    public static int getCheckFancyConfiguration(String stateCode, VehicleParameters vehDobj) {

        boolean flag = false;
        int retFlag = 0;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String advWithoutFee = "false";
        try {
            String sql = "SELECT advance_regn_no,adv_without_fee FROM tm_configuration where state_cd=? ";
            tmgr = new TransactionManager("getFancyNumbers");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {

                flag = rs.getBoolean("advance_regn_no");
                advWithoutFee = rs.getString("adv_without_fee");
            }
            if (flag == false) {
                retFlag = 1;
            }
            if (advWithoutFee.equalsIgnoreCase("false")) {
                retFlag = 2;
            } else if (advWithoutFee.equalsIgnoreCase("true")) {
                retFlag = 1;
            } else if (isCondition(replaceTagValues(advWithoutFee, vehDobj), "getCheckFancyConfiguration-2")) {
                retFlag = 1;
            } else {
                retFlag = 3;
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

        return retFlag;
    }

    public static String verifyNumber(String regnNo, String stateCode, int offCode) throws VahanException, Exception {
        String returnString = "";
        int numPart;
        TransactionManager tmg = null;
        PreparedStatement ps = null;// tmg.prepareStatement(sql);
        try {
            String seriesPart = regnNo.substring(0, regnNo.length() - 4);
            numPart = Integer.parseInt(regnNo.substring(regnNo.length() - 4), regnNo.length());
            TmConfigurationDobj dob = Util.getTmConfiguration();
            int advanceJump = dob.getAdvanceNoJump();
            String sqlString = "select * from vm_regn_series where  state_cd=? and off_cd=?";
            tmg = new TransactionManager("verifyNumber");
            ps = tmg.prepareStatement(sqlString);
            ps.setString(1, stateCode);
            ps.setInt(2, offCode);
            RowSet rs = tmg.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                String currSeries = rs.getString("prefix_series");
                String upCommSeries = rs.getString("next_prefix_series");
                int currNumber = rs.getInt("running_no");
                int upperNumber = rs.getInt("upper_range_no");
                if (seriesPart.equalsIgnoreCase(currSeries)) {
                    if (numPart <= currNumber + advanceJump && numPart >= currNumber) {
                        //returnString = "Registration Number is Available";
                        returnString = "";
                    } else {
                        returnString = "Registration Number is not in Range";
                    }
                } else if (seriesPart.equalsIgnoreCase(upCommSeries)) {
                    if (numPart <= currNumber + advanceJump - upperNumber) {
                        returnString = "";
                    } else {
                        returnString = "Registration Number is not in Range";
                    }
                }
            }

        } catch (NumberFormatException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Registration Number is not Proper");

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            if (tmg != null) {
                tmg.release();
            }

        }
        return returnString;
    }

    public static String checkFancyNumber(String regnNo, String stateCode, String regnType, String vehClass) throws VahanException, Exception {
        int numPart;
        String returnString = "";
        TransactionManager tmg = null;
        PreparedStatement ps = null;// tmg.prepareStatement(sql);
        VehicleParameters vehParameters = new VehicleParameters();
        try {
            vehParameters.setVCH_TYPE(Integer.parseInt(regnType));
            vehParameters.setVH_CLASS(Integer.parseInt(vehClass));
            vehParameters.setOFF_CD(Util.getSelectedSeat().getOff_cd());
            String sqlString = "select fancy_number,condition_formula from vm_fancy_mast where state_cd=? and fancy_number=?";
            tmg = new TransactionManager("checkFancyNumber");
            ps = tmg.prepareStatement(sqlString);
            ps.setString(1, stateCode);
            ps.setString(2, regnNo.substring(regnNo.length() - 4, regnNo.length()));
            RowSet rs = tmg.fetchDetachedRowSet();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "checkFancyNumber-21")) {
                    returnString = "Registration No is Fancy";
                }
            }

        } catch (NumberFormatException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Registration Number is not Proper");

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            if (tmg != null) {
                tmg.release();
            }
        }

        return returnString;
    }

    public static String checkApplNo(String applicantionNo) throws VahanException, Exception {
        String returnString = "";
        TransactionManager tmg = null;
        PreparedStatement ps = null;// tmg.prepareStatement(sql);
        try {
            String sqlString = "select regn_appl_no from vt_advance_regn_no where regn_appl_no = ?";
            tmg = new TransactionManager("checkFancyNumber");
            ps = tmg.prepareStatement(sqlString);
            ps.setString(1, applicantionNo);
            RowSet rs = tmg.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                returnString = "Application Number is Already Booked Fancy Number";
            }

        } catch (NumberFormatException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Application Number is not Proper");

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            if (tmg != null) {
                tmg.release();
            }
        }

        return returnString;
    }

    public static String checkRegnNoAlreadyBook(String oldregnNo) throws VahanException, Exception {
        String returnString = "";
        String rcptNo = "";
        TransactionManager tmg = null;
        PreparedStatement ps = null;// tmg.prepareStatement(sql);
        try {
            String sqlString = "select recp_no from vt_advance_regn_no_order where old_regn_no = ?";
            tmg = new TransactionManager("checkRegnNoAlreadyBook");
            ps = tmg.prepareStatement(sqlString);
            ps.setString(1, oldregnNo);
            RowSet rs = tmg.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                rcptNo = rs.getString("recp_no");
                returnString = "This Old Registration Number is Already Booked Advance/Fancy Number with Application Number " + rcptNo + "";
            }

        } catch (NumberFormatException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Registration Number is not Proper");

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            if (tmg != null) {
                tmg.release();
            }
        }

        return returnString;
    }

    public static boolean isNumberBooked(String regnNo) {
        boolean booked = false;
        TransactionManager tmgr = null;
        try {
            String sql = "Select * from  va_fancy_register where regn_no=? and status is null ";
            tmgr = new TransactionManager("isNumberBooked");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo.toUpperCase());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                booked = true;

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

        return booked;
    }

    public static Integer getAmount(String regnNo, String stateCode, String regnType, String vehClass) throws VahanException {
        int amount = 0;
        TransactionManager tmgr = null;
        boolean flg = false;
        PreparedStatement ps = null;
        VehicleParameters vehParameters = new VehicleParameters();
        try {
            vehParameters.setVCH_TYPE(Integer.parseInt(regnType));
            vehParameters.setVH_CLASS(Integer.parseInt(vehClass));
            String sql = "Select booking_fee,condition_formula from vm_fancy_mast where fancy_number = ? and state_cd=? ";
            tmgr = new TransactionManager("getAmount");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo.substring((regnNo.length() - 4), regnNo.length()));
            ps.setString(2, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getAmount-21")) {


                    amount = rs.getInt("booking_fee");
                    flg = true;

                }
            }


            if (!flg) {
                sql = "Select booking_fee,condition_formula from vm_fancy_mast where fancy_number= 'NONE' and state_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCode);
                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getAmount-22")) {
                        amount = rs.getInt("booking_fee");
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException(e.getMessage());
                }
            }
        }

        return amount;

    }

    public static Integer getServicesCharges(String stateCode, String regnType, String vehClass) throws VahanException {
        int amount = 0;
        TransactionManager tmgr = null;
        boolean flg = false;
        PreparedStatement ps = null;
        VehicleParameters vehParameters = new VehicleParameters();
        try {
            vehParameters.setVCH_TYPE(Integer.parseInt(regnType));
            vehParameters.setVH_CLASS(Integer.parseInt(vehClass));
            String sql = "Select amount,condition_formula from vm_feemast_service where  state_cd=?";
            tmgr = new TransactionManager("getAmount");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getServicesCharges-2")) {
                    amount = rs.getInt("amount");
                    flg = true;
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException(e.getMessage());
                }
            }
        }

        return amount;

    }

    public static synchronized String saveFancyDetail(AdvanceRegnNo_dobj dobj, String state_cd, int off_cd, boolean blnOldRegistration) throws VahanException, Exception {
        String rcpt = null;
        TransactionManager tmg = null;
        PreparedStatement ps;
        String sql;
        int flag1 = 0, flag3 = 0, flag4 = 0;;
        try {

            tmg = new TransactionManager("saveFancyDetail");
            //Generate a receipt No
            rcpt = ServerUtil.getUniqueApplNo(tmg, state_cd);

            if (blnOldRegistration) {
                sql = "INSERT INTO vt_advance_regn_no_order(state_cd, off_cd, recp_no, regn_no, old_regn_no, reason, user_cd, op_dt)VALUES (?, ?, ?, ?, ?, ?, ?, current_timestamp)";
                ps = tmg.prepareStatement(sql);
                ps.setString(1, state_cd);
                ps.setInt(2, off_cd);
                ps.setString(3, rcpt);
                ps.setString(4, dobj.getRegn_no().toUpperCase());
                ps.setString(5, dobj.getOld_regn_no().toUpperCase());
                ps.setString(6, dobj.getOrderNumber().toUpperCase());
                ps.setLong(7, Long.parseLong(Util.getEmpCode()));
                flag1 = ps.executeUpdate();

            } else {
                sql = "INSERT INTO vt_advance_regn_no_order(state_cd, off_cd, recp_no, regn_no, reason, user_cd, op_dt)VALUES (?, ?, ?, ?, ?, ?, current_timestamp)";
                ps = tmg.prepareStatement(sql);
                ps.setString(1, state_cd);
                ps.setInt(2, off_cd);
                ps.setString(3, rcpt);
                ps.setString(4, dobj.getRegn_no().toUpperCase());
                ps.setString(5, dobj.getOrderNumber().toUpperCase());
                ps.setLong(6, Long.parseLong(Util.getEmpCode()));
                flag1 = ps.executeUpdate();

            }
            //insert it into vt_advance_regn_no
            sql = "INSERT INTO vt_advance_regn_no(state_cd, off_cd, recp_no, regn_appl_no, regn_no, owner_name,f_name, c_add1, c_add2, c_district, c_pincode, c_state,mobile_no,total_amt)VALUES (?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?,?, ?)";

            ps = tmg.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, rcpt);
            if (dobj.getRegn_appl_no() != null && !dobj.getRegn_appl_no().equalsIgnoreCase("")) {
                ps.setString(4, dobj.getRegn_appl_no().toUpperCase());
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }
            ps.setString(5, dobj.getRegn_no().toUpperCase());
            ps.setString(6, dobj.getOwner_name().toUpperCase());
            ps.setString(7, dobj.getF_name().toUpperCase());
            ps.setString(8, dobj.getC_add1().toUpperCase());
            ps.setString(9, dobj.getC_add2().toUpperCase());
            ps.setInt(10, dobj.getC_district());
            ps.setInt(11, dobj.getC_pincode());
            ps.setString(12, state_cd);
            ps.setLong(13, dobj.getMobile_no());
            ps.setLong(14, dobj.getTotal_amt());
            flag3 = ps.executeUpdate();

            String check = AdvanceRegnFeeImpl.verifyInVtOwner(dobj.getRegn_no().toUpperCase(), state_cd, off_cd);
            if (check.equalsIgnoreCase("")) {
                tmg.commit();
            } else {
                throw new VahanException(check);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } catch (Exception e) {
            throw new VahanException(e.getMessage());
        } finally {
            if (tmg != null) {
                try {
                    tmg.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException(e.getMessage());
                }
            }
        }

        return rcpt;
    }

    public static Long returnExcessAmount(FeeDraftDobj feeDraftDobj) {
        Long totalExcessAmount = 0L;
        for (PaymentCollectionDobj draftPayment : feeDraftDobj.getDraftPaymentList()) {
            totalExcessAmount += Integer.parseInt(draftPayment.getAmount());
        }
        return totalExcessAmount;
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
                    retString = retString.replace(key.toString(), "'" + retObj.toString().toUpperCase() + "'");

                } else {
                    retString = retString.replace(key.toString(), retObj.toString().toUpperCase());
                }
            } catch (Exception e) {
            }

        }
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

    private static void getTagFields() {
        if (TagFieldsMap == null) {
            TagFieldsMap = new LinkedHashMap<String, String>();
            if (TagFieldsMapVerify == null) {
                TagFieldsMapVerify = new LinkedHashMap<String, String>();
            }
            if (TagDescrDisplay == null) {
                TagDescrDisplay = new LinkedHashMap<String, String>();
            }

            String sql = "SELECT regexp_replace(code, '[^0-9]', '', 'g')::numeric as sr_no, code, descr, value_field, field_type FROM VM_TAX_SLAB_FIELDS order by 1";
            TransactionManager tmgr = null;
            try {
                tmgr = new TransactionManager("getTagFields-AdvRegnFeeWithoutfee");
                tmgr.prepareStatement(sql);
                RowSet rs = tmgr.fetchDetachedRowSet();
                while (rs.next()) {
                    TagFieldsMap.put(rs.getString("code"), rs.getString("value_field"));
                    TagFieldsMapVerify.put(rs.getString("code"), rs.getString("field_type"));
                    TagDescrDisplay.put(rs.getString("code"), rs.getString("descr"));
                }
            } catch (Exception e) {
            } finally {
                try {
                    if (tmgr != null) {
                        tmgr.release();
                    }
                } catch (Exception e) {
                }
            }

        }
    }
}
