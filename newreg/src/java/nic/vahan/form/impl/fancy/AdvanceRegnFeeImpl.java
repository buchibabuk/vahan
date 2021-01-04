/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.fancy;

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
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;
import nic.vahan.form.impl.FeeDraftimpl;
import nic.vahan.form.impl.Receipt_Master_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import nic.vahan.db.DocumentType;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.OwnerImpl;
import org.apache.log4j.Logger;
import nic.vahan.server.CommonUtils;

/**
 *
 * @author Administrator
 */
public class AdvanceRegnFeeImpl {

    private static final Logger LOGGER = Logger.getLogger(AdvanceRegnFeeImpl.class);
    public static Map TagFieldsMap = null;
    public static Map TagFieldsMapVerify = null;
    public static Map TagDescrDisplay = null;

    public static AdvanceRegnNo_dobj get_fancy_appl_no(String appl_no, String stateCode, int offCode) throws VahanException {

        AdvanceRegnNo_dobj dobj = null;
        TransactionManager tmg = null;
        try {
            tmg = new TransactionManager("get_fancy_appl_no");
            String sql = "Select mobile_no,* from " + TableList.VA_OWNER + "  a \n"
                    + "left join " + TableList.VA_OWNER_IDENTIFICATION + " b on b.appl_no = a.appl_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd \n"
                    + "inner join " + TableList.VA_DETAILS + " c on c.appl_no = a.appl_no and c.state_cd = a.state_cd and c.off_cd = a.off_cd \n"
                    + "where a.appl_no=? and a.state_cd=? and a.off_cd=? and case when (c.pur_cd =1 and a.regn_no not in ('NEW')) then true  when a.regn_no in ('NEW') then true else false end";
            PreparedStatement ps = tmg.prepareStatement(sql);
            ps.setString(1, appl_no.toUpperCase());
            ps.setString(2, stateCode);
            ps.setInt(3, offCode);
            RowSet rsNew = tmg.fetchDetachedRowSet_No_release();

            if (rsNew.next()) {
                dobj = new AdvanceRegnNo_dobj();
                dobj.setOwner_name(rsNew.getString("owner_name"));
                dobj.setF_name(rsNew.getString("f_name"));
                dobj.setC_add1(rsNew.getString("c_add1"));
                dobj.setC_add2(rsNew.getString("c_add2"));
                dobj.setC_add3(rsNew.getString("c_add3"));
                dobj.setC_district(rsNew.getInt("c_district"));
                dobj.setC_pincode(rsNew.getInt("c_pincode"));
                dobj.setRegnType(rsNew.getString("regn_type"));
                dobj.setVehClass(rsNew.getString("vh_class"));
                dobj.setChasisNo(rsNew.getString("chasi_no"));
                dobj.setMobile_no(rsNew.getLong("mobile_no"));
                dobj.setOwnerCd(rsNew.getInt("owner_cd"));
                dobj.setPmtType(rsNew.getInt("pmt_type"));
            } else {
                sql = "SELECT owner_name,f_name,c_add1,c_add2,c_add3,c_district,c_pincode,regn_type,vh_class,chasi_no,mobile_no,cubic_cap,b.owner_cd,pmt_type from " + TableList.VA_RE_ASSIGN + " a "
                        + " inner join " + TableList.VT_OWNER + " b on a.old_regn_no = b.regn_no "
                        + " left join " + TableList.VT_OWNER_IDENTIFICATION + " c on c.regn_no = b.regn_no "
                        + " where a.appl_no = ? and a.state_cd = ? and a.off_cd = ?"
                        + " union "
                        + " SELECT owner_name,f_name,c_add1,c_add2,c_add3,c_district,c_pincode,regn_type,vh_class,chasi_no,mobile_no,cubic_cap,b.owner_cd,'0' as pmt_type from " + TableList.VA_CONVERSION + " a "
                        + " inner join " + TableList.VT_OWNER + " b on a.old_regn_no = b.regn_no "
                        + " left join " + TableList.VT_OWNER_IDENTIFICATION + " c on c.regn_no = b.regn_no "
                        + " where a.appl_no = ? and a.state_cd = ? and a.off_cd = ?"
                        + " union "
                        + " SELECT a.owner_name,a.f_name,a.c_add1,a.c_add2,a.c_add3,a.c_district,a.c_pincode,b.regn_type,b.vh_class,chasi_no,mobile_no,cubic_cap,b.owner_cd,'0' as pmt_type from " + TableList.VA_TO + "  a \n"
                        + "                         inner join " + TableList.VT_OWNER + "  b on a.regn_no = b.regn_no \n"
                        + "                         left join " + TableList.VT_OWNER_IDENTIFICATION + "  c on c.regn_no = b.regn_no \n"
                        + "                         where a.appl_no = ? and a.state_cd = ? and a.off_cd = ?"
                        + " union "
                        + " SELECT b.owner_name,b.f_name,b.c_add1,b.c_add2,b.c_add3,b.c_district,b.c_pincode,b.regn_type,b.vh_class,chasi_no,mobile_no,cubic_cap,b.owner_cd,'0' as pmt_type from " + TableList.VA_NOC + "  a \n"
                        + "                         inner join " + TableList.VT_OWNER + "  b on a.regn_no = b.regn_no \n"
                        + "                         left join " + TableList.VT_OWNER_IDENTIFICATION + "  c on c.regn_no = b.regn_no \n"
                        + "                         where a.appl_no = ? and a.state_cd = ? and a.off_cd = ? "
                        + " union "
                        + " SELECT owner_name,f_name,c_add1,c_add2,c_add3,c_district,c_pincode,regn_type,vh_class,chasi_no,mobile_no,cubic_cap,b.owner_cd,'0' as pmt_type from " + TableList.VA_SURRENDER_RETENTION + " a "
                        + " inner join " + TableList.VT_OWNER + " b on a.regn_no = b.regn_no "
                        + " left join " + TableList.VT_OWNER_IDENTIFICATION + " c on c.regn_no = b.regn_no "
                        + " where a.appl_no = ? and a.state_cd = ? and a.off_cd = ?"
                        + " union "
                        + " SELECT owner_name,f_name,c_add1,c_add2,c_add3,c_district,c_pincode,regn_type,vh_class,chasi_no,mobile_no,cubic_cap,b.owner_cd,'0' as pmt_type from " + TableList.VA_RENEWAL + " a "
                        + " inner join " + TableList.VT_OWNER + " b on a.regn_no = b.regn_no "
                        + " left join " + TableList.VT_OWNER_IDENTIFICATION + " c on c.regn_no = b.regn_no "
                        + " where a.appl_no = ? and a.state_cd = ? and a.off_cd = ? and a.state_cd='RJ'";
                ps = tmg.prepareStatement(sql);
                int i = 0;

                ps.setString(++i, appl_no.toUpperCase());
                ps.setString(++i, stateCode);
                ps.setInt(++i, offCode);
                ps.setString(++i, appl_no.toUpperCase());
                ps.setString(++i, stateCode);
                ps.setInt(++i, offCode);
                ps.setString(++i, appl_no.toUpperCase());
                ps.setString(++i, stateCode);
                ps.setInt(++i, offCode);
                ps.setString(++i, appl_no.toUpperCase());
                ps.setString(++i, stateCode);
                ps.setInt(++i, offCode);
                ps.setString(++i, appl_no.toUpperCase());
                ps.setString(++i, stateCode);
                ps.setInt(++i, offCode);
                ps.setString(++i, appl_no.toUpperCase());
                ps.setString(++i, stateCode);
                ps.setInt(++i, offCode);
                rsNew = tmg.fetchDetachedRowSet_No_release();
                if (rsNew.next()) {
                    dobj = new AdvanceRegnNo_dobj();
                    dobj.setOwner_name(rsNew.getString("owner_name"));
                    dobj.setF_name(rsNew.getString("f_name"));
                    dobj.setC_add1(rsNew.getString("c_add1"));
                    dobj.setC_add2(rsNew.getString("c_add2"));
                    dobj.setC_add3(rsNew.getString("c_add3"));
                    dobj.setC_district(rsNew.getInt("c_district"));
                    dobj.setC_pincode(rsNew.getInt("c_pincode"));
                    dobj.setRegnType(rsNew.getString("regn_type"));
                    dobj.setVehClass(rsNew.getString("vh_class"));
                    dobj.setChasisNo(rsNew.getString("chasi_no"));
                    dobj.setMobile_no(rsNew.getLong("mobile_no"));
                    dobj.setOwnerCd(rsNew.getInt("owner_cd"));
                    dobj.setPmtType(rsNew.getInt("pmt_type"));
                } else {
                    throw new VahanException("Either Invalid Application No or Registration No has been already generated for this application no.");
                }
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting the Application Details...");
        } finally {
            if (tmg != null) {
                try {
                    tmg.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException("Problem in getting the Application Details...");
                }
            }
        }
        return dobj;
    }

    public static AdvanceRegnNo_dobj get_old_regn_no(String regn_no, String stateCode, int offCode) throws VahanException {

        AdvanceRegnNo_dobj dobj = null;
        TransactionManager tmg = null;
        try {
            tmg = new TransactionManager("get_old_regn_no");
            String sql = "Select mobile_no,* from " + TableList.VT_OWNER + "  a \n"
                    + "left join " + TableList.VT_OWNER_IDENTIFICATION + " b on b.regn_no = a.regn_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd \n"
                    + "where a.regn_no=? and a.state_cd=? and a.off_cd=? and a.status in (?,?)";
            PreparedStatement ps = tmg.prepareStatement(sql);
            ps.setString(1, regn_no.toUpperCase());
            ps.setString(2, stateCode);
            ps.setInt(3, offCode);
            ps.setString(4, TableConstants.VT_RC_RELEASE_STATUS);
            ps.setString(5, TableConstants.VT_RC_ACTIVE_STATUS);
            RowSet rsNew = tmg.fetchDetachedRowSet_No_release();

            if (rsNew.next()) {
                dobj = new AdvanceRegnNo_dobj();
                dobj.setOwner_name(rsNew.getString("owner_name"));
                dobj.setF_name(rsNew.getString("f_name"));
                dobj.setC_add1(rsNew.getString("c_add1"));
                dobj.setC_add2(rsNew.getString("c_add2"));
                dobj.setC_add3(rsNew.getString("c_add3"));
                dobj.setC_district(rsNew.getInt("c_district"));
                dobj.setC_pincode(rsNew.getInt("c_pincode"));
                dobj.setRegnType(rsNew.getString("regn_type"));
                dobj.setVehClass(rsNew.getString("vh_class"));
                dobj.setChasisNo(rsNew.getString("chasi_no"));
                dobj.setMobile_no(rsNew.getLong("mobile_no"));
                dobj.setOwnerCd(rsNew.getInt("owner_cd"));
            } else {
                throw new VahanException("Either Invalid Registration Number Or Problem in getting the Registration Details.");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting the Registration Details...");
        } finally {
            if (tmg != null) {
                try {
                    tmg.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException("Problem in getting the Registration Details...");
                }
            }
        }
        return dobj;
    }

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
                    if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getFancyNumbers-1")) {
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

    public static List<AdvanceRegnNo_dobj> getAvailableNumbers(String stateCode, int offCd, String series, int runningNumber) {
        List<AdvanceRegnNo_dobj> mp = new ArrayList<>();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT regn_no from vahan4.get_regn_no_available_including_fancy(?,?,?,?,?)";
            tmgr = new TransactionManager("getAvailableNumbers");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setInt(2, offCd);
            ps.setString(3, series);
            ps.setInt(4, runningNumber);
            int alreadyGenerated = Util.getTmConfiguration().getRegn_gen_random_batch();
            int maxNumber = runningNumber + 1000 - alreadyGenerated - 1;
            if (maxNumber > 9999) {
                maxNumber = 9999;
            } else if (maxNumber < runningNumber) {
                maxNumber = runningNumber;
            }
            ps.setInt(5, maxNumber);
            RowSet rs = tmgr.fetchDetachedRowSet();
            int i = 1;
            while (rs.next()) {
                AdvanceRegnNo_dobj dobj = new AdvanceRegnNo_dobj();
                dobj.setSerialNo("" + i++);
                dobj.setRegn_no(rs.getString("regn_no"));
                mp.add(dobj);
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

    public static String sanitizeFancyNumber(String num) {
        String finalNumber = "";
        if (num.length() == 1 && !num.equalsIgnoreCase("0")) {
            finalNumber = "000" + num;
        } else if (num.length() == 2 && !num.equalsIgnoreCase("00")) {
            finalNumber = "00" + num;
        } else if (num.length() == 3 && !num.equalsIgnoreCase("000")) {
            finalNumber = "0" + num;
        } else if (num.length() == 4 && !num.equalsIgnoreCase("0000")) {
            finalNumber = num;
        }
        return finalNumber;
    }

    public static List<AdvanceRegnNo_dobj> getSeriesAndRunningNumber(String stateCode, int offCd) {
        List<AdvanceRegnNo_dobj> seriesList = new ArrayList<>();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            String sql = "select prefix_series,running_no from vm_regn_series  where state_cd=? and off_cd=?";
            tmgr = new TransactionManager("getAvailableNumbers");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setInt(2, offCd);

            RowSet rs = tmgr.fetchDetachedRowSet();
            int i = 1;
            while (rs.next()) {
                AdvanceRegnNo_dobj dobj = new AdvanceRegnNo_dobj();
                dobj.setSerialNo("" + i++);
                int runnNumber = rs.getInt("running_no");
                int runnNumber1 = runnNumber - Util.getTmConfiguration().getRegn_gen_random_batch();
                if (runnNumber1 < 1) {
                    runnNumber1 = 1;
                }
                String currentNumber = sanitizeFancyNumber(String.valueOf(runnNumber1)) + " Onwards";
                String seriesName = ((rs.getString("prefix_series")));
                String finalNumber = seriesName + currentNumber;
                dobj.setSeriesName(finalNumber);
                dobj.setConsolidatedseries(rs.getString("prefix_series"));
                dobj.setRunningNumber(rs.getInt("running_no"));
                seriesList.add(dobj);
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

        return seriesList;
    }

    public static boolean getCheckFancyConfiguration(String stateCode) {

        boolean flag = false;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT advance_regn_no FROM tm_configuration where state_cd=? ";
            tmgr = new TransactionManager("getFancyNumbers");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {

                flag = rs.getBoolean("advance_regn_no");

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

    public static boolean verifyFancyRegnNo(String regnNo, String stateCode, int offCd, String regnType, String vehClass, String applNo, int pmtType) throws VahanException, Exception {
        boolean flag = true;
        boolean retFlag = true;
        String check = checkRetaintedRegnNo(regnNo.toUpperCase());
        if (check.equalsIgnoreCase("")) {
            retFlag = false;
        }
        if (retFlag) {
            check = verifyInVtOwner(regnNo.toUpperCase(), stateCode, offCd);
        } else {
            check = verifyRegnNoWithoutSurrender(regnNo.toUpperCase(), stateCode, offCd);
        }
        if (!check.equalsIgnoreCase("")) {
            flag = false;
            throw new VahanException(check);
        } else {
            HashMap hm = checkBookingPermission(regnNo, stateCode, offCd, Integer.parseInt(regnType));
            if (hm.get("status").toString().equalsIgnoreCase("A")) {
                offCd = Integer.parseInt(hm.get("seriesOffCode").toString());
                if (retFlag) {
                    check = verifySeries(regnNo.toUpperCase(), stateCode, offCd, applNo, pmtType);
                    if (!check.equalsIgnoreCase("")) {
                        flag = false;
                        throw new VahanException(check);
                    }
                }
                check = checkFancyNumber(regnNo.toUpperCase(), stateCode, regnType, vehClass, pmtType);
                if (check.equalsIgnoreCase("")) {
                    //flag=false;
                    TmConfigurationDobj tmDobj = Util.getTmConfiguration();
                    if (tmDobj.getAdvanceNoJump() != 0) {
                        check = verifyNumber(regnNo.toUpperCase(), stateCode, offCd);
                        if (!check.equalsIgnoreCase("")) {
                            flag = false;
                            throw new VahanException(check);
                        }
                    }

                }
            } else {
                flag = false;
                throw new VahanException("You are not Allowed to Book this Number");
            }
        }
        return flag;
    }

    public static String verifyInVtOwner(String regnNo, String stateCode, int offCode) throws VahanException, Exception {
        int i = 1;
        String returnString = "";
        TransactionManager tmg = null;
        PreparedStatement ps = null;// tmg.prepareStatement(sql);
        try {
            String sqlString = "select regn_no ,recp_no,'Advance Booked' as st from " + TableList.VT_ADVANCE_REGN_NO + " where regn_no=? "
                    + " union select regn_no,'','Assigned' as st from " + TableList.VT_OWNER + " where regn_no=? "
                    + " union select regn_no,'','Blacklisted' as st from " + TableList.VT_BLOCK_VEH + " where regn_no=?"
                    + " union select regn_no,'','Online Booked' as st from " + TableList.VT_FANCY_REGISTER + " where regn_no=?"
                    + " union select regn_no,'','Online Booked' as st from " + TableList.VA_FANCY_REGISTER + " where regn_no=?"
                    + " union select regn_no,'','Online Blocked' as st from " + TableList.BLOCK_REGN_LIST + " where regn_no=?"
                    + " union select regn_no,'','Allotted' as st from " + TableList.VM_REGN_ALLOTED + " where regn_no=?"
                    + " union select regn_no,'','Booked' as st from " + TableList.VA_OWNER + " where regn_no=?"
                    + " union select old_regn_no as regn_no,'','Reassigned' as st from " + TableList.VH_RE_ASSIGN + " where old_regn_no=?"
                    + " union select old_regn_no as regn_no,'','Surrendered' as st from " + TableList.VT_SURRENDER_RETENTION + " where old_regn_no=?"
                    + " union select regn_no,'','Booked' as st from " + TableList.VA_OWNER_CHOICE_NO + "  where regn_no=?"
                    + " union select regn_no,'','NOC Issued' as st from " + TableList.VT_NOC + " where regn_no=? ";
            tmg = new TransactionManager("verifyInVtOwner");
            ps = tmg.prepareStatement(sqlString);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);

            RowSet rs = tmg.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                returnString = "This Registration Number Can not be allotted due to Already : " + rs.getString("st");
            }
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

    public static String verifySeries(String regnNo, String stateCode, int offCode, String applNo, int pmtType) throws VahanException, Exception {
        TransactionManager tmg = null;
        String returnString = "";
        PreparedStatement ps = null;// tmg.prepareStatement(sql);
        VehicleParameters vehDobj = null;
        OwnerDetailsDobj owDobj = null;
        Owner_dobj ownerDobj = null;
        try {
            int currOffCode = 0;
            int purCode = 0;
            ArrayList<Status_dobj> statusList = new ArrayList();
            if (Util.getSelectedSeat() != null) {
                currOffCode = Util.getSelectedSeat().getOff_cd();
            }
            if (applNo != null && !"".equalsIgnoreCase(applNo)) {
                owDobj = new OwnerImpl().getOwnerDetailsFromAppl(applNo, stateCode, currOffCode);
                ownerDobj = new OwnerImpl().getOwnerDobj(owDobj);
                vehDobj = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
                if (pmtType != 0) {
                    vehDobj.setPERMIT_TYPE(pmtType);
                }
                statusList = ServerUtil.applicationStatusByApplNo(applNo, stateCode);
            }
            for (Status_dobj sdobj : statusList) {
                if (sdobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION) {
                    purCode = sdobj.getPur_cd();
                }
            }
            String seriesPart = regnNo.substring(0, regnNo.length() - 4);
            String sqlString = "select prefix_series,criteria_formula from vm_regn_series where (prefix_series=? or next_prefix_series=?) and state_cd=? and off_cd=? \n"
                    + " union select prefix_series,'true' as criteria_formula from vm_advance_regn_series where  prefix_series=? and state_cd=? and off_cd=? "
                    + " union select prefix_series,criteria_formula from vhm_regn_series where prefix_series=? and state_cd=? and off_cd=?";
            tmg = new TransactionManager("verifySeries");
            ps = tmg.prepareStatement(sqlString);
            ps.setString(1, seriesPart);
            ps.setString(2, seriesPart);
            ps.setString(3, stateCode);
            ps.setInt(4, offCode);
            ps.setString(5, seriesPart);
            ps.setString(6, stateCode);
            ps.setInt(7, currOffCode);
            ps.setString(8, seriesPart);
            ps.setString(9, stateCode);
            ps.setInt(10, offCode);
            RowSet rs = tmg.fetchDetachedRowSet();
            if (applNo != null && !"".equalsIgnoreCase(applNo) && vehDobj != null && purCode != TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION) {
                boolean verSeries = false;
                while (rs.next()) {
                    if (FormulaUtils.isCondition(FormulaUtils.replaceTagValues(rs.getString("criteria_formula"), vehDobj), "Fancy Number Verify Series")) {
                        verSeries = true;
                    }
                }
                if (!verSeries) {
                    returnString = "This Series is Not Compatible with this application";
                }
            } else if (!rs.next()) {
                returnString = "This Series is Not Available";
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("This Series is Not Available");
        } finally {
            if (tmg != null) {
                tmg.release();
            }
        }
        return returnString;
    }

    public static String verifyNumber(String regnNo, String stateCode, int offCode) throws VahanException, Exception {
        String returnString = "";
        int numPart = 0;
        TransactionManager tmg = null;
        PreparedStatement ps = null;// tmg.prepareStatement(sql);
        try {
            String seriesPart = regnNo.substring(0, regnNo.length() - 4);
            TmConfigurationDobj dob = Util.getTmConfiguration();
            int advanceJump = dob.getAdvanceNoJump();
            numPart = Integer.parseInt(regnNo.substring(regnNo.length() - 4, regnNo.length()));
            String sqlString = "select * from vm_regn_series where  state_cd=? and off_cd=? and prefix_series=? or next_prefix_series=?";
            tmg = new TransactionManager("verifyNumber");
            ps = tmg.prepareStatement(sqlString);
            ps.setString(1, stateCode);
            ps.setInt(2, offCode);
            ps.setString(3, seriesPart);
            ps.setString(4, seriesPart);
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
            throw new VahanException("Last Four Digits of the Registration No is not Proper " + numPart);

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

    public static String checkFancyNumber(String regnNo, String stateCode, String regnType, String vehClass, int pmtType) throws VahanException, Exception {
        int numPart;
        String returnString = "";
        TransactionManager tmg = null;
        PreparedStatement ps = null;// tmg.prepareStatement(sql);
        VehicleParameters vehParameters = new VehicleParameters();
        try {
            vehParameters.setVCH_TYPE(Integer.parseInt(regnType));
            vehParameters.setVH_CLASS(Integer.parseInt(vehClass));
            vehParameters.setPERMIT_TYPE(pmtType);
            vehParameters.setOFF_CD(Util.getSelectedSeat().getOff_cd());
            String sqlString = "select fancy_number,condition_formula from vm_fancy_mast where state_cd=? and fancy_number=?";
            tmg = new TransactionManager("checkFancyNumber");
            ps = tmg.prepareStatement(sqlString);
            ps.setString(1, stateCode);
            ps.setString(2, regnNo.substring(regnNo.length() - 4, regnNo.length()));
            RowSet rs = tmg.fetchDetachedRowSet();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "checkFancyNumber-1")) {
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
                returnString = "Fancy Number is already booked against this Application Number.";
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

    public static Integer getAmount(String regnNo, String stateCode, int offCode, String regnType, String vehClass, int ownerCd, int pmtType) throws VahanException {
        int amount = 0;
        TransactionManager tmgr = null;
        boolean flg = false;
        boolean flg1 = false;
        PreparedStatement ps = null;
        VehicleParameters vehParameters = new VehicleParameters();
        try {
            vehParameters.setVCH_TYPE(Integer.parseInt(regnType));
            vehParameters.setVH_CLASS(Integer.parseInt(vehClass));
            vehParameters.setOWNER_CD(ownerCd);
            vehParameters.setPERMIT_TYPE(pmtType);
            vehParameters.setOFF_CD(Util.getSelectedSeat().getOff_cd());
            String sql = "Select booking_fee,condition_formula from vm_fancy_mast where fancy_number = ? and state_cd=? ";
            tmgr = new TransactionManager("getAmount");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo.substring((regnNo.length() - 4), regnNo.length()));
            ps.setString(2, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getAmount-11")) {
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
                    if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getAmount-12")) {
                        amount = rs.getInt("booking_fee");
                        flg1 = true;
                    }
                }
            }
            if (!flg1 && !flg || "WB".equalsIgnoreCase(stateCode)) {
                String seriesPart = regnNo.substring(0, regnNo.length() - 4);
                TmConfigurationDobj dob = Util.getTmConfiguration();
                int advanceJump = dob.getAdvanceNoJump();
                int numPart = Integer.parseInt(regnNo.substring(regnNo.length() - 4, regnNo.length()));
                int diffNo = 0;
                int minAmt = 0;
                int diffCount = 0;
                String sqlString = "select * from vm_regn_series where  state_cd=? and off_cd=? and (prefix_series=? or next_prefix_series=?)";
                ps = tmgr.prepareStatement(sqlString);
                ps.setString(1, stateCode);
                ps.setInt(2, offCode);
                ps.setString(3, seriesPart);
                ps.setString(4, seriesPart);
                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    String currSeries = rs.getString("prefix_series");
                    String upCommSeries = rs.getString("next_prefix_series");
                    int currNumber = rs.getInt("running_no");
                    int upperNumber = rs.getInt("upper_range_no");
                    if (seriesPart.equalsIgnoreCase(currSeries)) {
                        if (numPart <= upperNumber && numPart >= currNumber) {
                            diffNo = numPart - currNumber;
                        } else {
                        }
                    } else if (seriesPart.equalsIgnoreCase(upCommSeries)) {
                        if (numPart <= upperNumber) {
                            diffNo = upperNumber - currNumber + numPart;
                        } else {
                        }
                    }
                }
                sql = "SELECT state_cd, l_diff_regn_no, u_diff_regn_no, fixed_rate, diff_regn_unit,jump_rate, condition_formula,min_amt  FROM tm_fancy_jump_rate where state_cd=? and l_diff_regn_no<=? and u_diff_regn_no>=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCode);
                ps.setInt(2, diffNo);
                ps.setInt(3, diffNo);
                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getAmount-13")) {
                        if (rs.getInt("jump_rate") != 0) {
                            amount += rs.getInt("fixed_rate") + (Math.floor((diffNo - rs.getInt("l_diff_regn_no") + 1) / rs.getInt("diff_regn_unit"))) * rs.getInt("jump_rate");
                        } else {
                            amount += rs.getInt("fixed_rate");
                        }
                        minAmt = rs.getInt("min_amt");
                        diffCount = rs.getInt("diff_regn_unit");
                    }
                }
                if (minAmt != 0 && amount < minAmt) {
                    amount = minAmt;
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

    public static Integer getRetainAmount(String regnNo, String stateCode, int offCode, Owner_dobj owner_dobj) throws VahanException {
        int amount = 0;
        TransactionManager tmgr = null;
        boolean flg = false;
        boolean flg1 = false;
        PreparedStatement ps = null;
        VehicleParameters vehParameters = null;
        try {
            vehParameters = FormulaUtils.fillVehicleParametersFromDobj(owner_dobj);
            String sql = "Select booking_fee,condition_formula, retain_fee from vm_fancy_mast where fancy_number = ? and state_cd=? ";
            tmgr = new TransactionManager("getAmount");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo.substring((regnNo.length() - 4), regnNo.length()));
            ps.setString(2, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getAmount-11")) {
                    amount = rs.getInt("retain_fee");
                    flg = true;
                }
            }
            if (!flg) {
                sql = "Select booking_fee,condition_formula ,retain_fee from vm_fancy_mast  where fancy_number= 'NONE' and state_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCode);
                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getAmount-12")) {
                        amount = rs.getInt("retain_fee");
                        flg1 = true;
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
            vehParameters.setPUR_CD(TableConstants.VM_TRANSACTION_MAST_CHOICE_NO);
            String sql = "Select amount,condition_formula from vm_feemast_service where  state_cd=? and pur_cd in (?,?)";
            tmgr = new TransactionManager("getAmount");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setInt(2, 0);
            ps.setInt(3, TableConstants.VM_TRANSACTION_MAST_CHOICE_NO);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getServicesCharges-1")) {
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

    public static Integer getOutOfTurnFee(String regnNo, String stateCode, String regnType, String vehClass) throws VahanException {
        int amount = 0;
        TransactionManager tmgr = null;
        boolean flg = false;
        PreparedStatement ps = null;
        VehicleParameters vehParameters = new VehicleParameters();
        try {
            vehParameters.setVCH_TYPE(Integer.parseInt(regnType));
            vehParameters.setVH_CLASS(Integer.parseInt(vehClass));
            String sql = "Select  booking_fee,condition_formula from vm_fancy_mast where  fancy_number=? and state_cd = ?";
            tmgr = new TransactionManager("getAmount");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getOutOfTurnFee-1")) {
                    amount = rs.getInt("booking_fee");
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

    public static synchronized String saveFancyDetail(AdvanceRegnNo_dobj dobj, String state_cd, int off_cd, FeeDraftDobj feeDraftDobj, List<EpayDobj> listTransCharges, String payMode) throws VahanException, Exception {
        String rcpt = null;
        TransactionManager tmg = null;
        PreparedStatement ps;
        String sql;
        int flag1 = 0, flag2 = 0, flag3 = 0, flag4 = 0;
        boolean retFlag = true;
        try {
            String check = checkRetaintedRegnNo(dobj.getRegn_no().toUpperCase());
            if (check.equalsIgnoreCase("")) {
                retFlag = false;
            }
            tmg = new TransactionManager("saveFancyDetail");
            //
            sql = "select * from vt_advance_regn_no where regn_no=?";
            ps = tmg.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_no().toUpperCase());
            RowSet rs = tmg.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                throw new VahanException("Vehicle No " + dobj.getRegn_no().toUpperCase() + " already booked!!! Please try with different no.");
            }
            //Generate a receipt No
            rcpt = Receipt_Master_Impl.generateNewRcptNo(off_cd, tmg);
            String appNumber = ServerUtil.getUniqueApplNo(tmg, state_cd);
            Long inscd = 0L;
            if (feeDraftDobj != null) {
                feeDraftDobj.setAppl_no(appNumber);
                feeDraftDobj.setRcpt_no(rcpt);
                FeeDraftimpl feeDraft_impl = new FeeDraftimpl();
                inscd = feeDraft_impl.saveDraftDetails(feeDraftDobj, tmg);
            }

            sql = "Insert into vt_fee (regn_no,payment_mode,fees,fine,rcpt_no,rcpt_dt,pur_cd,collected_by,state_cd,off_cd) "
                    + " values(?,?,?,?,?,current_timestamp,?,?,?,?)";
            ps = tmg.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_no().toUpperCase());
            ps.setString(2, payMode);
            ps.setLong(3, dobj.getTotal_amt());
            ps.setInt(4, 0);
            ps.setString(5, rcpt);
            ps.setInt(6, TableConstants.VM_TRANSACTION_MAST_CHOICE_NO);
            ps.setString(7, Util.getEmpCode());
            ps.setString(8, state_cd);
            ps.setInt(9, off_cd);
            flag1 = ps.executeUpdate();


            ////////////////////// Save For User Charges /////////////////////
            if (dobj.getUserCharge() > 0L) {
                ps.setString(1, dobj.getRegn_no().toUpperCase());
                ps.setString(2, payMode);
                ps.setLong(3, dobj.getUserCharge());
                ps.setInt(4, 0);
                ps.setString(5, rcpt);
                ps.setInt(6, TableConstants.VM_TRANSACTION_MAST_USER_CHARGES);
                ps.setString(7, Util.getEmpCode());
                ps.setString(8, state_cd);
                ps.setInt(9, off_cd);
                flag2 = ps.executeUpdate();
            } else {
                flag2 = 1;
            }
            Long transaction_Charges = 0L;
            if (listTransCharges != null && !listTransCharges.isEmpty()) {
                for (EpayDobj ePayDobj : listTransCharges) {
                    transaction_Charges = transaction_Charges + ePayDobj.getAct_TaxFee();
                    ServerUtil.saveCommonChargesForAll(ePayDobj, dobj.getRegn_no().toUpperCase(), tmg, rcpt, TableConstants.TM_PAYMENT_MODE_CASH);
                }
            }
            //////////////////////// End ////////////////////////////////////////

            //insert it into va_fancy_register
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

            /////////////////Calculate  Excess Amount ////////////////
            Long excessAmount = 0L;
            Long cashAmount = dobj.getTotal_amt();
            if (dobj.getUserCharge() > 0L) {
                cashAmount += dobj.getUserCharge();
            }
            if (transaction_Charges > 0L) {
                cashAmount += transaction_Charges;
            }
            if (feeDraftDobj != null) {
                excessAmount = returnExcessAmount(feeDraftDobj);
                if (excessAmount < cashAmount) {
                    cashAmount = cashAmount - excessAmount;
                    excessAmount = 0L;
                } else if (excessAmount >= cashAmount) {

                    if ((cashAmount - excessAmount) < 0) {
                        excessAmount = -(cashAmount - excessAmount);
                    } else {
                        excessAmount = 0L;
                    }
                    cashAmount = 0L;
                }
            }
            sql = "INSERT INTO " + TableList.VP_APPL_RCPT_MAPPING + " "
                    + "( state_cd, off_cd, appl_no, rcpt_no, owner_name, chasi_no, vh_class,instrument_cd, excess_amt, cash_amt,remarks)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = tmg.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, appNumber);
            ps.setString(4, rcpt);
            ps.setString(5, dobj.getOwner_name().toUpperCase());
            if (dobj.getChasisNo() == null || dobj.getChasisNo().equalsIgnoreCase("")) {
                ps.setString(6, "NA");  ////////////For Chasi no i.e not available in case of advance Regn No   
            } else {
                ps.setString(6, dobj.getChasisNo());
            }
            ps.setInt(7, 0);           /////////For Vh_class i.e not available in case of advance Regn No
            ps.setLong(8, inscd);
            ps.setLong(9, excessAmount);
            ps.setLong(10, cashAmount);
            ps.setString(11, null);
            flag4 = ps.executeUpdate();
            sql = "DELETE FROM " + TableList.VM_AVAILABLE_NO_FANCY + " WHERE regn_no=?";
            ps = tmg.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_no().toUpperCase());
            ps.executeUpdate();
            ServerUtil.insertForQRDetails(appNumber, null, null, rcpt, false, DocumentType.RECEIPT_QR, state_cd, off_cd, tmg);
            if (retFlag) {
                check = verifyInVtOwner(dobj.getRegn_no().toUpperCase(), state_cd, off_cd);
            } else {
                check = verifyRegnNoWithoutSurrender(dobj.getRegn_no().toUpperCase(), state_cd, off_cd);
            }
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
                tmgr = new TransactionManager("getTagFields-AdvRegnFeeWithfee");
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

    public static HashMap checkBookingPermission(String regnNo, String stateCode, int offCode, int regnType) throws VahanException, Exception {
        TransactionManager tmg = null;
        String returnString = "";
        String allowedCode = "";
        int numberStart;
        int numberEnd;
        HashMap hm = new HashMap();

        PreparedStatement ps = null;// tmg.prepareStatement(sql);
        try {
            String seriesPart = regnNo.substring(0, regnNo.length() - 4);
            int numberPart = Integer.parseInt(regnNo.substring(regnNo.length() - 4, regnNo.length()));
            int seriOffCode = getOfficeCodeofSeries(seriesPart, stateCode);
            if (seriOffCode == offCode) {
                allowedCode = "S";
            } else {
                allowedCode = "O";
            }
            VehicleParameters vehPar = new VehicleParameters();
            vehPar.setVCH_TYPE(regnType);
            String sqlString = "select state_cd, off_cd, allowed_from, allowed_to, allowed_other_rto, condition_formula from tm_fancy_conf where state_cd=? and off_cd=? and allowed_other_rto=?";
            tmg = new TransactionManager("checkBookingPermission");
            ps = tmg.prepareStatement(sqlString);
            ps.setString(1, stateCode);
            ps.setInt(2, offCode);
            ps.setString(3, allowedCode);
            RowSet rs = tmg.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehPar), "checkBookingPermission-1")) {
                    numberStart = rs.getInt("allowed_from");
                    numberEnd = rs.getInt("allowed_to");
                    if (numberPart >= numberStart && numberPart <= numberEnd) {
                        hm.put("status", "A");
                        hm.put("seriesOffCode", seriOffCode);
                    } else {
                        hm.put("status", "N");
                    }
                } else {
                    hm.put("status", "A");
                    hm.put("seriesOffCode", offCode);
                }
            } else {
                hm.put("status", "A");
                hm.put("seriesOffCode", offCode);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            if (tmg != null) {
                tmg.release();
            }

        }
        return hm;
    }

    public static int getOfficeCodeofSeries(String seriesPart, String stateCode) throws VahanException, Exception {
        TransactionManager tmg = null;
        int retOffCode = 0;
        PreparedStatement ps = null;// tmg.prepareStatement(sql);
        try {
            String sqlString = "select off_cd from vm_regn_series where (prefix_series=? or next_prefix_series=?) and state_cd=?  \n"
                    + " union select off_cd from vm_advance_regn_series where  prefix_series=? and state_cd=? "
                    + " union select off_cd from vhm_regn_series where  prefix_series=? and state_cd=? ";
            tmg = new TransactionManager("verifySeries");
            ps = tmg.prepareStatement(sqlString);
            ps.setString(1, seriesPart);
            ps.setString(2, seriesPart);
            ps.setString(3, stateCode);
            ps.setString(4, seriesPart);
            ps.setString(5, stateCode);
            ps.setString(6, seriesPart);
            ps.setString(7, stateCode);


            RowSet rs = tmg.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                retOffCode = rs.getInt("off_cd");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            if (tmg != null) {
                tmg.release();
            }

        }

        return retOffCode;
    }

    public static boolean isFancyReceiptBeforeFeeTax(String applNo, Date fancyReceiptDate) {
        boolean result = false;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("isFancyReceiptBeforeFeeTax");
            String sql = "select b.* "
                    + "from vp_appl_rcpt_mapping a,vt_fee b "
                    + " where a.state_cd=b.state_cd and a.off_cd=b.off_cd and a.rcpt_no=b.rcpt_no "
                    + " and a.appl_no=? and b.pur_cd=1 and ? <= b.rcpt_dt";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setDate(2, new java.sql.Date(fancyReceiptDate.getTime()));
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                result = true;
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }

        }

        return result;
    }

    public static String verifyRegnNoWithoutSurrender(String regnNo, String stateCode, int offCode) throws VahanException, Exception {
        int i = 1;
        String returnString = "";
        TransactionManager tmg = null;
        PreparedStatement ps = null;// tmg.prepareStatement(sql);
        try {
            String sqlString = "select regn_no ,recp_no,'Advance Booked' as st from " + TableList.VT_ADVANCE_REGN_NO + " where regn_no=? "
                    + " union select regn_no,'','Assigned' as st from " + TableList.VT_OWNER + " where regn_no=? "
                    + " union select regn_no,'','Blacklisted' as st from " + TableList.VT_BLOCK_VEH + " where regn_no=?"
                    + " union select regn_no,'','Online Booked' as st from " + TableList.VT_FANCY_REGISTER + " where regn_no=?"
                    + " union select regn_no,'','Online Booked' as st from " + TableList.VA_FANCY_REGISTER + " where regn_no=?"
                    + " union select regn_no,'','Online Blocked' as st from " + TableList.BLOCK_REGN_LIST + " where regn_no=?"
                    + " union select regn_no,'','Allotted' as st from " + TableList.VM_REGN_ALLOTED + " where regn_no=?"
                    + " union select regn_no,'','Booked' as st from " + TableList.VA_OWNER + " where regn_no=?"
                    + " union select old_regn_no as regn_no,'','Reassigned' as st from " + TableList.VH_RE_ASSIGN + " where old_regn_no=?"
                    + " union select regn_no,'','NOC Issued' as st from " + TableList.VT_NOC + " where regn_no=? ";
            tmg = new TransactionManager("verifyInVtOwner");
            ps = tmg.prepareStatement(sqlString);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);

            RowSet rs = tmg.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                returnString = "This Registration Number Can not be allotted due to Already : " + rs.getString("st");
            }
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

    public static String checkRetaintedRegnNo(String regnNumber) throws VahanException, Exception {
        String returnString = "";
        TransactionManager tmg = null;
        PreparedStatement ps = null;// tmg.prepareStatement(sql);
        try {
            String retFeeMode = Util.getTmConfiguration().getRetentionFeeValidMode();
            int retDays = Util.getTmConfiguration().getRetentionFeeValidPeriod();
            if (retFeeMode.equalsIgnoreCase("Y")) {
                retDays = 365 * retDays;
            } else if (retFeeMode.equalsIgnoreCase("M")) {
                retDays = 30 * retDays;
            }
            String sqlString = "SELECT old_regn_no  FROM vt_surrender_retention where old_regn_no=? and surr_dt+interval '" + retDays + " days' <=current_date;";
            tmg = new TransactionManager("checkRetnationNumber");
            ps = tmg.prepareStatement(sqlString);
            ps.setString(1, regnNumber);
            RowSet rs = tmg.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                returnString = "";
            } else {
                returnString = "You are not eligible to book this Retention Number";
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Application Number is not Proper");
        } finally {
            if (tmg != null) {
                tmg.release();
            }
        }
        return returnString;
    }

    public static void checkOfficeOnReceipt(String rcptNo, String stateCd, int offCd) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("checkOfficeOnReceipt");
            String sql = "select state_cd,off_cd from " + TableList.VT_SURRENDER_RETENTION + " where state_cd=? and rcpt_no=? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setString(2, rcptNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (rs.getInt("off_cd") != offCd) {
                    throw new VahanException("Advance Retention receipt belongs to " + ServerUtil.getOfficeName(rs.getInt("off_cd"), stateCd) + ".");
                }
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
    }

    public void validateAdvanceRegistrationNumber(String stateCd, int offCd, String regnNo) throws VahanException, Exception {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("validateAdvanceRegistrationNumber");
            String check = validateAdvanceRegistrationNumber(tmgr, regnNo, stateCd, offCd);
            if (!check.equalsIgnoreCase("")) {
                throw new VahanException(check);
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(ex.getMessage());
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

    public static String validateAdvanceRegistrationNumber(TransactionManager tmgr, String regnNo, String stateCd, int offCd) throws VahanException, Exception {
        boolean retFlag = false;
        String check = checkRetaintedRegnNo(regnNo);
        if (check.equalsIgnoreCase("")) {
            retFlag = false;
        }

        String sql = "select * from vt_advance_regn_no where regn_no=?";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, regnNo);
        RowSet rs = tmgr.fetchDetachedRowSet();
        if (rs.next()) {
            throw new VahanException("Vehicle No " + regnNo + " already booked!!! Please try with different no.");
        }

        if (retFlag) {
            check = verifyInVtOwner(regnNo, stateCd, offCd);
        } else {
            check = verifyRegnNoWithoutSurrender(regnNo, stateCd, offCd);
        }
        return check;
    }

    public String getPaymentApplicationNumber(String regnApplNo, String regnNo) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        String payApplNo = null;
        String var = null;
        try {
            tmgr = new TransactionManagerReadOnly("getPaymentApplicationNumber");
            if (!CommonUtils.isNullOrBlank(regnApplNo)) {
                var = " regn_appl_no = ?";
            } else if (!CommonUtils.isNullOrBlank(regnNo)) {
                var = " regn_no = ?";
            }
            String sql = "select pay_appl_no from " + TableList.VP_ADVANCE_REGN_NO + "  where " + var;
            PreparedStatement ps = tmgr.prepareStatement(sql);
            if (!CommonUtils.isNullOrBlank(regnApplNo)) {
                ps.setString(1, regnApplNo);
            } else if (!CommonUtils.isNullOrBlank(regnNo)) {
                ps.setString(1, regnNo);
            }

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                payApplNo = rs.getString("pay_appl_no");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return payApplNo;
    }
}