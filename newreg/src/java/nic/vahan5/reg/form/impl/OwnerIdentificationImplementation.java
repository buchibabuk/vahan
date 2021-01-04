/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.TmConfigurationOwnerIdentificationDobj;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author Kartikey Singh
 */
public class OwnerIdentificationImplementation {
    private static final Logger LOGGER = Logger.getLogger(OwnerIdentificationImplementation.class);

    public OwnerIdentificationDobj set_Owner_identification_appl_db_dobj(String appl_no) {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        OwnerIdentificationDobj dobj = null;
        try {

            tmgr = new TransactionManager("TO_Impl");

            sql = "SELECT appl_no, regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no, \n"
                    + "       ration_card_no, voter_id, dl_no,owner_ctg,dept_cd \n"
                    + "  FROM " + TableList.VA_OWNER_IDENTIFICATION + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                dobj = new OwnerIdentificationDobj();

                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setMobile_no(rs.getLong("mobile_no"));
                dobj.setEmail_id(rs.getString("email_id"));
                dobj.setPan_no(rs.getString("pan_no"));
                dobj.setAadhar_no(rs.getString("aadhar_no"));
                dobj.setPassport_no(rs.getString("passport_no"));
                dobj.setRation_card_no(rs.getString("ration_card_no"));
                dobj.setVoter_id(rs.getString("voter_id"));
                dobj.setDl_no(rs.getString("dl_no"));
                dobj.setOwnerCatg(rs.getInt("owner_ctg"));
                dobj.setOwnerCdDept(rs.getInt("dept_cd"));
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return dobj;
    }

    public static void insertIntoOwnerIdentificationHistory(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VHA_OWNER_IDENTIFICATION
                + "            SELECT current_timestamp as moved_on, ? moved_by,state_cd,off_cd,appl_no, regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no,"
                + "            ration_card_no, voter_id, dl_no, verified_on, owner_ctg, op_dt,dept_cd"
                + "  FROM " + TableList.VA_OWNER_IDENTIFICATION
                + " WHERE appl_no = ? ";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();

    } // end of insertIntoOwnerIdentificationHistory

    public static void insertIntoOwnerIdentificationHistoryVH(TransactionManager tmgr, String regn_no, String stateCode, int offCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VH_OWNER_IDENTIFICATION
                + "            SELECT state_cd,off_cd,regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no,"
                + "            ration_card_no, voter_id, dl_no, verified_on, current_timestamp as moved_on, ? moved_by,owner_ctg,dept_cd "
                + "  FROM " + TableList.VT_OWNER_IDENTIFICATION
                + " WHERE regn_no = ? and state_cd = ?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, regn_no);
        ps.setString(3, stateCode);
        ps.executeUpdate();

    } // end of insertIntoOwnerIdentificationHistoryVH
    
    /*
     * @author Kartikey Singh
     */
    public static void insertIntoOwnerIdentificationHistoryVH(TransactionManager tmgr, String regn_no, String stateCode, int offCode, String empCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VH_OWNER_IDENTIFICATION
                + "            SELECT state_cd,off_cd,regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no,"
                + "            ration_card_no, voter_id, dl_no, verified_on, current_timestamp as moved_on, ? moved_by,owner_ctg,dept_cd "
                + "  FROM " + TableList.VT_OWNER_IDENTIFICATION
                + " WHERE regn_no = ? and state_cd = ?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, empCode);
        ps.setString(2, regn_no);
        ps.setString(3, stateCode);
        ps.executeUpdate();

    }

    public static void updateOwnerIdentification(TransactionManager tmgr, OwnerIdentificationDobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;

        sql = " UPDATE " + TableList.VA_OWNER_IDENTIFICATION
                + "   SET mobile_no=?, email_id=?, pan_no=?, aadhar_no=?, "
                + "       passport_no=?, ration_card_no=?, voter_id=?, dl_no=?, verified_on=current_timestamp ,owner_ctg=?,op_dt = current_timestamp,dept_cd=?"
                + " WHERE appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setLong(pos++, dobj.getMobile_no());
        ps.setString(pos++, dobj.getEmail_id());
        ps.setString(pos++, dobj.getPan_no());
        ps.setString(pos++, dobj.getAadhar_no());
        ps.setString(pos++, dobj.getPassport_no());
        ps.setString(pos++, dobj.getRation_card_no());
        ps.setString(pos++, dobj.getVoter_id());
        ps.setString(pos++, dobj.getDl_no());
        ps.setInt(pos++, dobj.getOwnerCatg());
        ps.setInt(pos++, dobj.getOwnerCdDept());
        ps.setString(pos++, dobj.getAppl_no());

        ps.executeUpdate();

    } // end of updateOwnerIdentification

    public static void updateOwnerIdentificationVT(TransactionManager tmgr, OwnerIdentificationDobj dobj) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        Owner_dobj owner_dobj = null;
        String aadhar_no = "";
        int pos = 1;
        OwnerImpl impl = new OwnerImpl();
        if (dobj.getRegn_no() != null) {
            owner_dobj = impl.set_Owner_appl_db_to_dobj(dobj.getRegn_no(), null, null, 0);
            owner_dobj.setVh_class(55);
            if (owner_dobj != null && (owner_dobj.getVh_class() != 0) && (owner_dobj.getVh_class() == TableConstants.ERICKSHAW_VCH_CLASS && Util.getUserStateCode().equalsIgnoreCase("DL"))) {
                aadhar_no = "aadhar_no=?,";
            }
        }
        
        sql = " UPDATE " + TableList.VT_OWNER_IDENTIFICATION
                + "   SET mobile_no=?, email_id=?, pan_no=?," + aadhar_no + " "
                + "       passport_no=?, ration_card_no=?, voter_id=?, dl_no=?, verified_on=current_timestamp,owner_ctg=?,dept_cd=? "
                + " WHERE regn_no=? and state_cd = ? and off_cd = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setLong(pos++, dobj.getMobile_no());
        ps.setString(pos++, dobj.getEmail_id());
        ps.setString(pos++, dobj.getPan_no());
        if (!aadhar_no.isEmpty()) {
            ps.setString(pos++, dobj.getAadhar_no());
        }
        ps.setString(pos++, dobj.getPassport_no());
        ps.setString(pos++, dobj.getRation_card_no());
        ps.setString(pos++, dobj.getVoter_id());
        ps.setString(pos++, dobj.getDl_no());
        ps.setInt(pos++, dobj.getOwnerCatg());
        ps.setInt(pos++, dobj.getOwnerCdDept());
        ps.setString(pos++, dobj.getRegn_no());
        ps.setString(pos++, dobj.getState_cd());
        ps.setInt(pos++, dobj.getOff_cd());
        ps.executeUpdate();
        
    } // end of updateOwnerIdentificationVT

    public static void insertIntoOwnerIdentificationVA(TransactionManager tmgr, OwnerIdentificationDobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;

        sql = "INSERT INTO  " + TableList.VA_OWNER_IDENTIFICATION + "(\n"
                + "         state_cd,off_cd,appl_no, regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no, \n"
                + "            ration_card_no, voter_id, dl_no, verified_on,owner_ctg,op_dt,dept_cd)\n"
                + "    VALUES (?,?,?, ?, ?, ?, ?, ?, ?, \n"
                + "            ?, ?, ?, current_timestamp,?,current_timestamp,?)";

        ps = tmgr.prepareStatement(sql);
        ps.setString(pos++, Util.getUserStateCode());
        ps.setInt(pos++, Util.getSelectedSeat().getOff_cd());
        ps.setString(pos++, dobj.getAppl_no());
        ps.setString(pos++, dobj.getRegn_no());
        ps.setLong(pos++, dobj.getMobile_no());
        ps.setString(pos++, dobj.getEmail_id());
        ps.setString(pos++, dobj.getPan_no());
        ps.setString(pos++, dobj.getAadhar_no());
        ps.setString(pos++, dobj.getPassport_no());
        ps.setString(pos++, dobj.getRation_card_no());
        ps.setString(pos++, dobj.getVoter_id());
        ps.setString(pos++, dobj.getDl_no());
        ps.setInt(pos++, dobj.getOwnerCatg());
        ps.setInt(pos++, dobj.getOwnerCdDept());
        ps.executeUpdate();

    } // end of insertIntoOwnerIdentificationVA

    public static void insertIntoOwnerIdentificationVT(TransactionManager tmgr, OwnerIdentificationDobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;

        sql = "INSERT INTO " + TableList.VT_OWNER_IDENTIFICATION + "(\n"
                + "            state_cd , off_cd ,regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no, \n"
                + "            ration_card_no, voter_id, dl_no, verified_on,owner_ctg,dept_cd)\n"
                + "    VALUES (?,?,?, ?, ?, ?, ?, ?, \n"
                + "            ?, ?, ?, current_timestamp,?,?)";

        ps = tmgr.prepareStatement(sql);
        ps.setString(pos++, dobj.getState_cd());
        ps.setInt(pos++, dobj.getOff_cd());
        ps.setString(pos++, dobj.getRegn_no());
        ps.setLong(pos++, dobj.getMobile_no());
        ps.setString(pos++, dobj.getEmail_id());
        ps.setString(pos++, dobj.getPan_no());
        ps.setString(pos++, dobj.getAadhar_no());
        ps.setString(pos++, dobj.getPassport_no());
        ps.setString(pos++, dobj.getRation_card_no());
        ps.setString(pos++, dobj.getVoter_id());
        ps.setString(pos++, dobj.getDl_no());
        ps.setInt(pos++, dobj.getOwnerCatg());
        ps.setInt(pos++, dobj.getOwnerCdDept());
        ps.executeUpdate();

    } // end of insertIntoOwnerIdentificationVT

    public static void insertUpdateOwnerIdentification(TransactionManager tmgr, OwnerIdentificationDobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "SELECT regn_no FROM " + TableList.VA_OWNER_IDENTIFICATION + " where appl_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getAppl_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) { //if any record is exist then update otherwise insert it
            insertIntoOwnerIdentificationHistory(tmgr, dobj.getAppl_no());
            updateOwnerIdentification(tmgr, dobj);
        } else {
            insertIntoOwnerIdentificationVA(tmgr, dobj);
        }
    } // end of insertUpdateOwnerIdentification

    public static void insertUpdateOwnerIdentificationVT(TransactionManager tmgr, OwnerIdentificationDobj dobj) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        
        sql = "SELECT regn_no FROM " + TableList.VT_OWNER_IDENTIFICATION + " where regn_no = ? and state_cd = ? and off_cd = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getRegn_no());
        ps.setString(2, dobj.getState_cd());
        ps.setInt(3, dobj.getOff_cd());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        
        if (rs.next()) { //if any record is exist then update otherwise insert it
            insertIntoOwnerIdentificationHistoryVH(tmgr, dobj.getRegn_no(), dobj.getState_cd(), dobj.getOff_cd());
            updateOwnerIdentificationVT(tmgr, dobj);
        } else {
            insertIntoOwnerIdentificationVT(tmgr, dobj);
        }
    } // end of insertUpdateOwnerIdentification

    public static void insertIntoVtFromVaOwnerIdentification(TransactionManager tmgr, String applNo) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT into  " + TableList.VT_OWNER_IDENTIFICATION
                + " select ?,?,regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no,"
                + "       ration_card_no, voter_id, dl_no, current_timestamp,owner_ctg,dept_cd "
                + "  FROM " + TableList.VA_OWNER_IDENTIFICATION
                + " where appl_no=?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getUserStateCode());
        ps.setInt(2, Util.getSelectedSeat().getOff_cd());
        ps.setString(3, applNo);
        ps.executeUpdate();

    } // end of insertIntoVtFromVaOwnerIdentification
    
    /*
     * @author Kartikey Singh
     */
    public static void insertIntoVtFromVaOwnerIdentification(TransactionManager tmgr, String applNo, String userStateCode,
            int selectedOffCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT into  " + TableList.VT_OWNER_IDENTIFICATION
                + " select ?,?,regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no,"
                + "       ration_card_no, voter_id, dl_no, current_timestamp,owner_ctg,dept_cd "
                + "  FROM " + TableList.VA_OWNER_IDENTIFICATION
                + " where appl_no=?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, userStateCode);
        ps.setInt(2, selectedOffCode);
        ps.setString(3, applNo);
        ps.executeUpdate();
    }

    public static void deleteFromVtOwnerIdentification(TransactionManager tmgr, String regnNo, String stateCode, int offCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "DELETE FROM " + TableList.VT_OWNER_IDENTIFICATION + " WHERE regn_no=? and state_cd = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regnNo);
        ps.setString(2, stateCode);
        ps.executeUpdate();

    } // end of deleteFromVTOwnerIdentification

    public static OwnerIdentificationDobj selectOwnerIdentificationDetail(String regnNo) {
        PreparedStatement ps = null;
        OwnerIdentificationDobj owner_identification = null;
        String sql = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("selectOwnerIdentificationDetail");
            sql = "SELECT * FROM " + TableList.VT_OWNER_IDENTIFICATION + " where regn_no = ? and state_cd = ? and off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) {
                owner_identification = new OwnerIdentificationDobj();
                owner_identification.setRegn_no(rs.getString("regn_no"));
                owner_identification.setMobile_no(rs.getLong("mobile_no"));
                owner_identification.setEmail_id(rs.getString("email_id"));
                owner_identification.setPan_no(rs.getString("pan_no"));
                owner_identification.setAadhar_no(rs.getString("aadhar_no"));
                owner_identification.setPassport_no(rs.getString("passport_no"));
                owner_identification.setRation_card_no(rs.getString("ration_card_no"));
                owner_identification.setVoter_id(rs.getString("voter_id"));
                owner_identification.setDl_no(rs.getString("dl_no"));
                owner_identification.setVerified_on(rs.getDate("verified_on"));
                owner_identification.setOwnerCatg(rs.getInt("owner_ctg"));
                owner_identification.setOwnerCdDept(rs.getInt("dept_cd"));
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
        return owner_identification;
    }

    public static boolean updateOwnerIdentification(OwnerIdentificationDobj dobj) {
        boolean flag = false;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("updateOwnerIdentification");
            insertIntoOwnerIdentificationHistoryVH(tmgr, dobj.getRegn_no(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
            updateOwnerIdentificationVT(tmgr, dobj);
            flag = true;
            tmgr.commit();
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

    public static List getOwnerCatgDepts(String stateCd, int ownerCd) {

        TransactionManager tmgr = null;
        List ownerCdDeptList = new ArrayList();
        try {
            tmgr = new TransactionManager("getOwnerCatgDepts");
            String sql = "SELECT * FROM vm_owcode_dept where state_cd = ? and ow_code = ? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, ownerCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                ownerCdDeptList.add(new SelectItem(rs.getInt("dept_cd"), rs.getString("descr")));
            }
            if (ownerCdDeptList.isEmpty()) {
                return null;
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
        return ownerCdDeptList;
    }

    public TmConfigurationOwnerIdentificationDobj getTmConfigurationOwnerIdentification(String stateCd) throws VahanException {
        TmConfigurationOwnerIdentificationDobj configurationOwnerIdentificationDobj = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            String sql = "SELECT * FROM " + TableList.TM_CONFIGURATION_OWNER_IDENTIFICATION + " where state_cd = ? ";
            tmgr = new TransactionManagerReadOnly("TmConfigurationOwnerIdentificationDobj");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                configurationOwnerIdentificationDobj = new TmConfigurationOwnerIdentificationDobj();
                configurationOwnerIdentificationDobj.setState_cd((rs.getString("state_cd")));
                configurationOwnerIdentificationDobj.setDl_required((rs.getString("dl_required")));
                configurationOwnerIdentificationDobj.setDl_validation_required(rs.getBoolean("dl_validation_required"));
                configurationOwnerIdentificationDobj.setPan_card_mandatory(rs.getString("pancard_mandatory"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " getTmConfigurationOwnerIdentification " + e.getStackTrace()[0]);
            throw new VahanException("Problem in fetching Configuration Details of Owner Identification, Please Contact to the System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return configurationOwnerIdentificationDobj;
    }
}
