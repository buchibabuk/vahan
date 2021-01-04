/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.eChallan;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.eChallan.AccusedDetailsDobj;
import nic.vahan.form.dobj.eChallan.ChallanConfigrationDobj;
import nic.vahan.form.dobj.eChallan.CompoundingInOfficeDobj;
import nic.vahan.form.dobj.eChallan.OffencesDobj;
import nic.vahan.form.dobj.eChallan.WitnessdetailDobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class ChallanUtil {

    private static Logger LOGGER = Logger.getLogger(ChallanUtil.class);

    public static OffencesDobj getOffenceDetails(String offenceCode, String accuseInOffDetail, CompoundingInOfficeDobj dobj) throws VahanException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmtgetOffenceDetails = null;
        OffencesDobj offdobj = null;
        String compFee = "";
        try {
            String sqlOffenceDetails = "select a.*,\n"
                    + "CASE WHEN a.challan_count=0 THEN a.penalty1 \n"
                    + "      WHEN challan_count=1 THEN a.penalty2 \n"
                    + "      WHEN challan_count>=2 THEN a.penalty3  \n"
                    + "            ELSE '0' end   as offence_penalty\n"
                    + "             from \n"
                    + "(SELECT vm_offence_penalty.offence_cd,vm_offence_penalty.vh_class,vm_offence_penalty.section_cd,vm_offence_penalty.penalty1,vm_offence_penalty.penalty2,vm_offence_penalty.penalty3,vm_offence_penalty.offence_applied_on,\n"
                    + "(select count (*) as challan_count  from  echallan.vt_challan where regn_no='UP32AB0004') as challan_count,\n"
                    + "offence.offence_desc as offenceDescr, offence.mva_clause,\n"
                    + "accused.descr\n"
                    + " FROM echallan.vm_offence_penalty vm_offence_penalty \n"
                    + "left Outer Join  echallan.vm_offences offence on offence.offence_cd=vm_offence_penalty.offence_cd and vm_offence_penalty.state_cd in (offence.state_cd,?)\n"
                    + "left Outer Join  echallan.vm_accused accused on ?=ANY(string_to_array(vm_offence_penalty.offence_applied_on,','))\n"
                    + "where accused.code=? and  vh_class=? and vm_offence_penalty.offence_cd=? and vm_offence_penalty.state_cd =?) a";

            tmgr = new TransactionManager("SaveChallanDAO: getoffencedetails");

            pstmtgetOffenceDetails = tmgr.prepareStatement(sqlOffenceDetails);
            pstmtgetOffenceDetails.setString(1, Util.getUserStateCode());
            pstmtgetOffenceDetails.setString(2, accuseInOffDetail);
            pstmtgetOffenceDetails.setString(3, accuseInOffDetail);
            pstmtgetOffenceDetails.setInt(4, Integer.parseInt(dobj.getVhClass()));
            pstmtgetOffenceDetails.setInt(5, Integer.parseInt(offenceCode));
            pstmtgetOffenceDetails.setString(6, Util.getUserStateCode());
            RowSet resulSet = tmgr.fetchDetachedRowSet();

            if (resulSet.next()) {
                if (offenceCode.equals("1")) {
                } else {
                    compFee = resulSet.getString("offence_penalty");
                }
                offdobj = new OffencesDobj(accuseInOffDetail, resulSet.getString("descr"), resulSet.getString("offence_cd"), resulSet.getString("offenceDescr"), compFee, resulSet.getString("mva_clause"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return offdobj;

    }

    public static List<AccusedDetailsDobj> getAccusedDetails(String applicationNo) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmtAccusedDetails;
        List accusedList = new ArrayList();
        AccusedDetailsDobj accdtl = null;
        try {
            String sqlAccusedDetails = "SELECT  ch_accused.accused_catg, ch_accused.accused_name, ch_accused.accused_add, ch_accused.dl_no ,accused.descr ,"
                    + " ch_accused.police_station_name,ch_accused.city,ch_accused.accused_flag,ch_accused.pin_code"
                    + " FROM " + TableList.VT_CHALLAN_ACCUSED + "  ch_accused"
                    + " INNER JOIN  " + TableList.VM_ACCUSED + "  accused ON accused.code=ch_accused.accused_catg"
                    + " WHERE appl_no=?";
            tmgr = new TransactionManager("getAccusedDetails");
            pstmtAccusedDetails = tmgr.prepareStatement(sqlAccusedDetails);
            pstmtAccusedDetails.setString(1, applicationNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                accdtl = new AccusedDetailsDobj(rs.getString("accused_catg"), rs.getString("descr"), rs.getString("accused_name"), rs.getString("accused_add"), rs.getString("dl_no"), rs.getString("police_station_name"), rs.getString("city"), rs.getString("accused_flag"), rs.getInt("pin_code"));

                accusedList.add(accdtl);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return accusedList;
    }

    public static List<WitnessdetailDobj> fetchWitnessDetails(String applNo) {
        WitnessdetailDobj dobj = null;
        List WitnessdetailDobjList = new ArrayList();
        PreparedStatement pstmt = null;
        TransactionManager tmgr = null;
        String sql = "";
        RowSet rowSet = null;
        sql = "SELECT appl_no, witness_name, witness_contact_no, witness_address, state_cd, \n"
                + " off_cd,police_station_name\n"
                + " FROM " + TableList.VT_WITNESS_DETAILS + " where appl_no=?";
        try {
            tmgr = new TransactionManager("fetchWitnessDetails");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, applNo);
            rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                dobj = new WitnessdetailDobj();
                dobj.setApplNo(rowSet.getString("appl_no"));
                dobj.setWitnessName(rowSet.getString("witness_name"));
                dobj.setWitnessContactNo(rowSet.getLong("witness_contact_no"));
                dobj.setWitnessAddress(rowSet.getString("witness_address"));
                dobj.setStateCd(rowSet.getString("state_cd"));
                dobj.setOffCd(rowSet.getInt("off_cd"));
                dobj.setPoliceStation(rowSet.getString("police_station_name"));
                WitnessdetailDobjList.add(dobj);
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

        return WitnessdetailDobjList;
    }

    public static String getSubstringFromRight(String value, int length) {
        // To get right characters from a string, change the begin index.
        if (value == null) {
            return null;
        }
        if (length < 0) {
            return "";
        }
        if (value.length() <= length) {
            return value;
        }
        return value.substring(value.length() - length);
    }

    public static Timestamp getTimeStamp(String Date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yy");
        Timestamp st = null;
        try {
            java.util.Date date = sdf.parse(Date);
            st = new Timestamp(date.getTime());
        } catch (ParseException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return st;
    }

    public ChallanConfigrationDobj getChallanConfigration(String userStateCode) throws VahanException, Exception {
        ChallanConfigrationDobj config_dob = null;
        PreparedStatement pstmt = null;
        TransactionManager tmgr = null;
        String sql = "";
        try {
            sql = "SELECT *  FROM " + TableList.TM_CHALLAN_CONFIGURATION + " WHERE State_cd = ? ";
            tmgr = new TransactionManager("getChallanConfigration");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, userStateCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                config_dob = new ChallanConfigrationDobj();
                config_dob.setState_cd(rs.getString("state_cd"));
                config_dob.setBook_no_enable(rs.getBoolean("book_no_enable"));
                config_dob.setIs_tax_enable(rs.getBoolean("is_tax_enable"));
                config_dob.setIsmagistrate_exist(rs.getBoolean("ismagistrate_exist"));
                config_dob.setIs_nccr_no(rs.getBoolean("is_nccr_no"));
                config_dob.setIs_challan_date(rs.getBoolean("is_challan_date"));
                config_dob.setIs_challan_time(rs.getBoolean("is_challan_time"));
                config_dob.setAccu_flag(rs.getBoolean("accu_flag"));
                config_dob.setAccu_pol_sta(rs.getBoolean("accu_pol_sta"));
                config_dob.setAccu_city(rs.getBoolean("accu_city"));
                config_dob.setAccu_pin_cd(rs.getBoolean("accu_pin_cd"));
                config_dob.setImpnd_pol_sta(rs.getBoolean("impnd_pol_sta"));
                config_dob.setImpnd_dist(rs.getBoolean("impnd_dist"));
                config_dob.setCommingFrom(rs.getBoolean("is_coming_from"));
                config_dob.setGoingTo(rs.getBoolean("is_going_to"));
            } else {
                throw new VahanException("State:" + userStateCode + "-Enforcement Module has not been Configure for this State, Please contact Administrator.");
            }
        } finally {
            tmgr.release();
        }
        return config_dob;
    }

    public String getSelectedMagistrate(String name, String value) {
        String magistrate_cd = "";
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        String sql = "";
        try {
            sql = "SELECT court_cd, state_cd, magistrate_name, magistrate_cd, court_name\n"
                    + "  FROM  " + TableList.VM_COURT + " where " + name + " = ? ";
            tmgr = new TransactionManager("getSelectedMagistrate");
            pstmt = tmgr.prepareStatement(sql);
            if ("court_cd".equalsIgnoreCase(name)) {
                pstmt.setInt(1, Integer.parseInt(value));
            }
            if ("magistrate_cd".equalsIgnoreCase(name)) {
                pstmt.setString(1, value);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if ("court_cd".equalsIgnoreCase(name)) {
                    magistrate_cd = rs.getString("magistrate_cd");
                }
                if ("magistrate_cd".equalsIgnoreCase(name)) {
                    magistrate_cd = rs.getString("court_cd");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return magistrate_cd;
    }
}
