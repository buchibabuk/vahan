/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Regn_series_dobj;
import nic.vahan.form.impl.fancy.AdvanceRegnFeeImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author DELL
 */
public class OwnerChoiceNoImpl {

    private static final Logger LOGGER = Logger.getLogger(OwnerChoiceNoImpl.class);

    public static Integer getAmountForRegistrationNo(Owner_dobj ownerDobj, String regnNo, String statecd) throws VahanException {
        int amount = 0;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        VehicleParameters vehParameters = null;
        try {
            tmgr = new TransactionManagerReadOnly("getAmountForRegistrationNo");
            vehParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
            String sql = "Select booking_fee,condition_formula from " + TableList.VM_FANCY_MAST + " where fancy_number = ? and state_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo.substring((regnNo.length() - 4), regnNo.length()));
            ps.setString(2, statecd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getAmountForRegistrationNo-1")) {
                    throw new VahanException("Registration number is fancy.");
                }
            }
            sql = "Select booking_fee,condition_formula from " + TableList.VM_FANCY_MAST + " where fancy_number= 'NONE' and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, statecd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getAmountForRegistrationNo-2")) {
                    amount = rs.getInt("booking_fee");
                }
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem is getting amount of choice registration No.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        }
        return amount;
    }

    public boolean saveChoiceRegistrationNo(String stateCd, int offCd, String choiceRegnNo, String applNo, int choiceFees, String empCd) throws VahanException {
        String sql = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean flag = false;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("saveChoiceRegistrationNo");
            sql = "select appl_no from " + TableList.VA_OWNER_CHOICE_NO + " where regn_no = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, choiceRegnNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (!rs.next()) {
                sql = "INSERT INTO " + TableList.VA_OWNER_CHOICE_NO + "(regn_no, appl_no, choice_fees, op_dt) VALUES (?, ?, ?, current_timestamp)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, choiceRegnNo);
                ps.setString(2, applNo);
                if (choiceFees > 0) {
                    ps.setInt(3, choiceFees);
                } else {
                    throw new VahanException("Choice registration fee can't be zero.");
                }
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                String isAlreadyAllotted = AdvanceRegnFeeImpl.verifyInVtOwner(choiceRegnNo, stateCd, offCd);
                if (CommonUtils.isNullOrBlank(isAlreadyAllotted)) {
                    tmgr.commit();
                    flag = true;
                } else {
                    throw new VahanException(isAlreadyAllotted);
                }
            } else {
                throw new VahanException(choiceRegnNo + " Registration Already Booked. Please select another one.");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in saving choice number.");
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

    public static Map<String, String> getOwnerChoiceRegnNoDetails(String appl_no) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        Map<String, String> choiceDetails = null;
        try {
            tmgr = new TransactionManagerReadOnly("getOwnerChoiceRegnNoDetails");
            sql = "select regn_no, choice_fees from " + TableList.VA_OWNER_CHOICE_NO + " where appl_no = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                choiceDetails = new HashMap<>();
                choiceDetails.put("regn_no", rs.getString("regn_no"));
                choiceDetails.put("choice_fees", rs.getString("choice_fees"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Unable to get Owner Choice No.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return choiceDetails;
    }

    public void insertIntoVtAdvanceRegn(Owner_dobj dobj, TransactionManager tmgr, String rcptNo, long amount, long mobileNo) throws VahanException {
        PreparedStatement ps = null;
        try {
            if (dobj != null) {
                String sql = "INSERT INTO " + TableList.VT_ADVANCE_REGN_NO + "(state_cd, off_cd, recp_no, regn_appl_no, regn_no, owner_name,f_name, c_add1, c_add2, c_district, c_pincode, c_state,mobile_no,total_amt)VALUES (?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?,?, ?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getState_cd());
                ps.setInt(2, dobj.getOff_cd());
                ps.setString(3, rcptNo);
                ps.setString(4, dobj.getAppl_no());
                ps.setString(5, dobj.getRegn_no().toUpperCase());
                ps.setString(6, dobj.getOwner_name().toUpperCase());
                ps.setString(7, dobj.getF_name().toUpperCase());
                ps.setString(8, dobj.getC_add1().toUpperCase());
                ps.setString(9, dobj.getC_add2().toUpperCase());
                ps.setInt(10, dobj.getC_district());
                ps.setInt(11, dobj.getC_pincode());
                ps.setString(12, dobj.getState_cd());
                ps.setLong(13, mobileNo);
                ps.setLong(14, amount);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in saving advance registration number.");
        }
    }

    public static void saveAndDeleteDataFromRegnAllotedTable(TransactionManager tmgr, String stateCd, int offCd, String applNo, String newRegnNo) throws VahanException {
        try {
            String sql = "";
            PreparedStatement ps = null;
            sql = "Update " + TableList.VM_REGN_AVAILABLE + " set regn_no=?  where status='A' and state_cd=? "
                    + " and off_cd=? and regn_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, newRegnNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.setString(4, newRegnNo);
            ps.executeUpdate();

            sql = "Delete from " + TableList.VM_REGN_AVAILABLE + " where status='A' and state_cd=? "
                    + " and off_cd=? and regn_no=?  ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, newRegnNo);
            ps.executeUpdate();

            sql = "insert into " + TableList.VM_REGN_ALLOTED + " (state_cd, off_cd, regn_no, appl_no) values (?, ?, ?, ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, newRegnNo);
            ps.setString(4, applNo);
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem occured during updation available registration no.");
        }

    }

    public List<Regn_series_dobj> getAllAvilableRegistrationNos(String stateCd, int offCd, String seriesPart, Owner_dobj ownerDobj) throws VahanException {
        String sql = "";
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        List<Regn_series_dobj> regnSeriesList = new ArrayList<>();
        Regn_series_dobj dobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("getOwnerChoiceAvailableNos");
            Regn_series_dobj regnDobj = this.getSeriesDetails(stateCd, offCd, seriesPart, tmgr, ownerDobj);
            if (regnDobj != null) {
                sql = "select * from get_regn_series_status(?, ? , ?, ?, ?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, offCd);
                ps.setString(3, seriesPart);
                ps.setInt(4, regnDobj.getLower_range_no());
                ps.setInt(5, regnDobj.getUpper_range_no());
                rs = tmgr.fetchDetachedRowSet();
                while (rs.next()) {
                    switch (rs.getString("status")) {
                        case "A":
                            dobj = new Regn_series_dobj();
                            dobj.setPrefix_series(seriesPart);
                            dobj.setSufixRegnNo(rs.getString("regn_no"));
                            dobj.setRegnColorCode("choice-number-available");
                            regnSeriesList.add(dobj);
                            break;
                        case "U":
                            dobj = new Regn_series_dobj();
                            dobj.setPrefix_series(seriesPart);
                            dobj.setSufixRegnNo(rs.getString("regn_no"));
                            dobj.setRegnColorCode("choice-number-used-registration");
                            regnSeriesList.add(dobj);
                            break;
                    }
                }
            } else {
                throw new VahanException("Registration series not available for office " + ServerUtil.getOfficeName(offCd, stateCd) + " , Please contact to Office Administrator");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem occurred during getting available Registration Series.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return regnSeriesList;
    }

    public Regn_series_dobj getSeriesDetails(String stateCd, int offCd, String seriesPart, TransactionManagerReadOnly tmgr, Owner_dobj dobj) throws VahanException {
        String sql = "";
        PreparedStatement ps = null;
        RowSet rs = null;
        Regn_series_dobj choiceDobj = null;
        try {
            VehicleParameters vehicleParameters = FormulaUtils.fillVehicleParametersFromDobj(dobj);
            sql = "select * from " + TableList.VM_REGN_SERIES + " where  state_cd=? and off_cd=? and prefix_series=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, seriesPart);
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("criteria_formula"), vehicleParameters), "getSeriesDetails-1")) {
                    choiceDobj = new Regn_series_dobj();
                    choiceDobj.setLower_range_no(rs.getInt("lower_range_no"));
                    choiceDobj.setUpper_range_no(rs.getInt("upper_range_no"));
                    choiceDobj.setRunning_no(rs.getInt("running_no"));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem occurred during getting Series details.");
        }
        return choiceDobj;
    }

    public boolean clearSelectedChoiceNumber(String stateCd, int offCd, String empCd, String applNo) throws VahanException {
        TransactionManager tmgr = null;
        boolean flag = false;
        try {
            tmgr = new TransactionManager("clearSelectedChoiceNumber");
            this.insertIntoHistoryChoiceNumber(tmgr, empCd, TableConstants.FANCY_REJECTED, applNo, stateCd);
            tmgr.commit();
            flag = true;
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem occurred durinig clear the choice number.");
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

    public void insertIntoHistoryChoiceNumber(TransactionManager tmgr, String empCd, String reason, String applNo, String stateCd) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        try {
            sql = "INSERT INTO " + TableList.VHA_OWNER_CHOICE_NO + "(moved_on, moved_by, reason, regn_no, appl_no, choice_fees, op_dt) "
                    + " SELECT current_timestamp,?, ?, regn_no, appl_no, choice_fees, op_dt FROM " + TableList.VA_OWNER_CHOICE_NO + " where appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCd);
            ps.setString(2, reason);
            ps.setString(3, applNo);
            ps.executeUpdate();
            ServerUtil.deleteFromTable(tmgr, null, applNo, TableList.VA_OWNER_CHOICE_NO);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error occurred while moving history.");
        }
    }

    public static String checkPaymentMadeForChoiceAppl(String dealerCd, String stateCd) throws Exception {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("checkPaymentMadeForChoiceAppl");
            sql = "select c.appl_no,v.rcpt_no from " + TableList.VA_OWNER_CHOICE_NO + " c"
                    + " inner join " + TableList.VA_OWNER + " o on o.appl_no=c.appl_no "
                    + " left join " + TableList.VPH_RCPT_CART + " v on v.appl_no=c.appl_no and o.state_cd=v.state_cd and o.off_cd=v.off_cd "
                    + " where o.state_cd = ? and v.rcpt_no is null and o.dealer_cd= ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setString(2, dealerCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (CommonUtils.isNullOrBlank(rs.getString("rcpt_no"))) {
                    return rs.getString("appl_no");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Unable to get Owner Choice No.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return null;
    }
}
