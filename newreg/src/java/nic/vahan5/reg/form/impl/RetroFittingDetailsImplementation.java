/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import static nic.vahan.form.impl.RetroFittingDetailsImpl.insertIntoVhaCng;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Kartikey Singh
 */
public class RetroFittingDetailsImplementation {

    private static final Logger LOGGER = Logger.getLogger(RetroFittingDetailsImplementation.class);

    public static RetroFittingDetailsDobj setCngDetails_db_to_dobj(String appl_no) throws VahanException {
        if (appl_no != null) {
            appl_no = appl_no.toUpperCase();
        }

        RetroFittingDetailsDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("setCngDetails_db_to_dobj");
            sql = "SELECT kit_srno, kit_type, kit_manuf, kit_pucc_norms, workshop, \n"
                    + "       workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, approval_no, \n"
                    + "       approval_dt\n"
                    + "  FROM " + TableList.VA_RETROFITTING_DTLS + " where appl_no=?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new RetroFittingDetailsDobj();
                dobj.setKit_srno(rs.getString("kit_srno"));
                dobj.setKit_type(rs.getString("kit_type"));
                dobj.setKit_manuf(rs.getString("kit_manuf"));
                dobj.setWorkshop(rs.getString("workshop"));
                dobj.setWorkshop_lic_no(rs.getString("workshop_lic_no"));
                dobj.setCyl_srno(rs.getString("cyl_srno"));
                dobj.setKit_pucc_norms(rs.getString("kit_pucc_norms"));
                dobj.setApproval_no(rs.getString("approval_no"));
                dobj.setInstall_dt((java.util.Date) rs.getDate("fitment_dt"));
                dobj.setHydro_dt((java.util.Date) rs.getDate("hydro_test_dt"));
                dobj.setApproval_dt((java.util.Date) rs.getDate("approval_dt"));
                TmConfigurationDobj configDobj = Util.getTmConfiguration();
                if (configDobj.isCnginfo_from_cngmaker()) {
                    dobj.setDisable(true);
                } else {
                    dobj.setDisable(false);
                }
            }
        } catch (SQLException e) {
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
        return dobj;
    }

    public static RetroFittingDetailsDobj setCngDetails_db_to_dobj_VT(String regn_no, String state_cd, int off_cd) throws VahanException {
        if (regn_no != null) {
            regn_no = regn_no.toUpperCase();
        }

        RetroFittingDetailsDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("setCngDetails_db_to_dobj_VT");
            sql = "SELECT kit_srno, kit_type, kit_manuf, kit_pucc_norms, workshop, \n"
                    + "       workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, approval_no, \n"
                    + "       approval_dt\n"
                    + "  FROM " + TableList.VT_RETROFITTING_DTLS + " where regn_no=? and state_cd = ? and off_cd = ?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new RetroFittingDetailsDobj();
                dobj.setKit_srno(rs.getString("kit_srno"));
                dobj.setKit_type(rs.getString("kit_type"));
                dobj.setKit_manuf(rs.getString("kit_manuf"));
                dobj.setWorkshop(rs.getString("workshop"));
                dobj.setWorkshop_lic_no(rs.getString("workshop_lic_no"));
                dobj.setCyl_srno(rs.getString("cyl_srno"));
                dobj.setKit_pucc_norms(rs.getString("kit_pucc_norms"));
                dobj.setApproval_no(rs.getString("approval_no"));
                dobj.setInstall_dt((java.util.Date) rs.getDate("fitment_dt"));
                dobj.setHydro_dt((java.util.Date) rs.getDate("hydro_test_dt"));
                dobj.setApproval_dt((java.util.Date) rs.getDate("approval_dt"));
                TmConfigurationDobj configDobj = Util.getTmConfiguration();
                if (configDobj.isCnginfo_from_cngmaker()) {
                    dobj.setDisable(true);
                } else {
                    dobj.setDisable(false);
                }
            }
        } catch (SQLException e) {
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
        return dobj;
    }
    
    /**
     * @author Kartikey Singh
     */
    public static RetroFittingDetailsDobj setCngDetails_db_to_dobj_VT(String regn_no, String state_cd, int off_cd, boolean cnginfo_from_cngmaker) throws VahanException {
        if (regn_no != null) {
            regn_no = regn_no.toUpperCase();
        }

        RetroFittingDetailsDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("setCngDetails_db_to_dobj_VT");
            sql = "SELECT kit_srno, kit_type, kit_manuf, kit_pucc_norms, workshop, \n"
                    + "       workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, approval_no, \n"
                    + "       approval_dt\n"
                    + "  FROM " + TableList.VT_RETROFITTING_DTLS + " where regn_no=? and state_cd = ? and off_cd = ?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new RetroFittingDetailsDobj();
                dobj.setKit_srno(rs.getString("kit_srno"));
                dobj.setKit_type(rs.getString("kit_type"));
                dobj.setKit_manuf(rs.getString("kit_manuf"));
                dobj.setWorkshop(rs.getString("workshop"));
                dobj.setWorkshop_lic_no(rs.getString("workshop_lic_no"));
                dobj.setCyl_srno(rs.getString("cyl_srno"));
                dobj.setKit_pucc_norms(rs.getString("kit_pucc_norms"));
                dobj.setApproval_no(rs.getString("approval_no"));
                dobj.setInstall_dt((java.util.Date) rs.getDate("fitment_dt"));
                dobj.setHydro_dt((java.util.Date) rs.getDate("hydro_test_dt"));
                dobj.setApproval_dt((java.util.Date) rs.getDate("approval_dt"));                
                if (cnginfo_from_cngmaker) {
                    dobj.setDisable(true);
                } else {
                    dobj.setDisable(false);
                }
            }
        } catch (SQLException e) {
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
        return dobj;
    }

    public static void saveCngVehicleDetails_Impl(RetroFittingDetailsDobj dobj, String appl_no, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "SELECT * FROM " + TableList.VA_RETROFITTING_DTLS + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoVhaCng(tmgr, appl_no);
                updateVaCng(tmgr, dobj, appl_no);
            } else {
                insertVaCng(tmgr, dobj, appl_no);
            }

        } catch (SQLException e) {
            LOGGER.error("saveCngVehicleDetails_Impl" + e.getMessage());
            throw new VahanException("Error In Retrofitting Detail Update.");
        }
    }

    /*
     * Author: Kartikey Singh
     */
    public static void saveCngVehicleDetails_Impl(RetroFittingDetailsDobj dobj, String appl_no, TransactionManager tmgr,
            String empCode, String userStateCode, int offCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "SELECT * FROM " + TableList.VA_RETROFITTING_DTLS + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoVhaCng(tmgr, appl_no, empCode);
                updateVaCng(tmgr, dobj, appl_no, userStateCode, offCode);
            } else {
                insertVaCng(tmgr, dobj, appl_no, userStateCode, offCode);
            }

        } catch (SQLException e) {
            LOGGER.error("saveCngVehicleDetails_Impl" + e.getMessage());
            throw new VahanException("Error In Retrofitting Detail Update.");
        }
    }

    private static void insertVaCng(TransactionManager tmgr, RetroFittingDetailsDobj dobj, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        {
            sql = "INSERT INTO " + TableList.VA_RETROFITTING_DTLS + " (\n"
                    + "        state_cd,off_cd,appl_no, kit_srno, kit_type, kit_manuf, kit_pucc_norms, workshop, \n"
                    + "            workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, approval_no, \n"
                    + "            approval_dt,op_dt)\n"
                    + "    VALUES (?,?,?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?, \n"
                    + "            ?,current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, appl_no);
            ps.setString(i++, dobj.getKit_srno());
            ps.setString(i++, dobj.getKit_type());
            ps.setString(i++, dobj.getKit_manuf());
            ps.setString(i++, dobj.getKit_pucc_norms());
            ps.setString(i++, dobj.getWorkshop());
            ps.setString(i++, dobj.getWorkshop_lic_no());
            if (dobj.getInstall_dt() == null) {
                ps.setNull(i++, java.sql.Types.NULL);
            } else {
                ps.setDate(i++, new java.sql.Date(dobj.getInstall_dt().getTime()));
            }
            if (dobj.getHydro_dt() == null) {
                ps.setNull(i++, java.sql.Types.NULL);
            } else {
                ps.setDate(i++, new java.sql.Date(dobj.getHydro_dt().getTime()));
            }
            ps.setString(i++, dobj.getCyl_srno());
            ps.setString(i++, dobj.getApproval_no());
            ps.setDate(i++, new java.sql.Date(dobj.getApproval_dt().getTime()));
            ps.executeUpdate();

        }
    }

    /*
     * Author: Kartikey Singh
     */
    private static void insertVaCng(TransactionManager tmgr, RetroFittingDetailsDobj dobj, String appl_no,
            String userStateCode, int offCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        {
            sql = "INSERT INTO " + TableList.VA_RETROFITTING_DTLS + " (\n"
                    + "        state_cd,off_cd,appl_no, kit_srno, kit_type, kit_manuf, kit_pucc_norms, workshop, \n"
                    + "            workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, approval_no, \n"
                    + "            approval_dt,op_dt)\n"
                    + "    VALUES (?,?,?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?, \n"
                    + "            ?,current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, userStateCode);
            ps.setInt(i++, offCode);
            ps.setString(i++, appl_no);
            ps.setString(i++, dobj.getKit_srno());
            ps.setString(i++, dobj.getKit_type());
            ps.setString(i++, dobj.getKit_manuf());
            ps.setString(i++, dobj.getKit_pucc_norms());
            ps.setString(i++, dobj.getWorkshop());
            ps.setString(i++, dobj.getWorkshop_lic_no());
            if (dobj.getInstall_dt() == null) {
                ps.setNull(i++, java.sql.Types.NULL);
            } else {
                ps.setDate(i++, new java.sql.Date(dobj.getInstall_dt().getTime()));
            }
            if (dobj.getHydro_dt() == null) {
                ps.setNull(i++, java.sql.Types.NULL);
            } else {
                ps.setDate(i++, new java.sql.Date(dobj.getHydro_dt().getTime()));
            }
            ps.setString(i++, dobj.getCyl_srno());
            ps.setString(i++, dobj.getApproval_no());
            ps.setDate(i++, new java.sql.Date(dobj.getApproval_dt().getTime()));
            ps.executeUpdate();
        }
    }

    public static void insertIntoVhaCng(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VHA_RETROFITTING_DTLS
                + " SELECT  current_timestamp as moved_on, ? as moved_by, state_cd, off_cd,appl_no,kit_srno, kit_type, kit_manuf, kit_pucc_norms, workshop, \n"
                + "       workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, approval_no, \n"
                + "       approval_dt,op_dt\n"
                + "  FROM " + TableList.VA_RETROFITTING_DTLS + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);

        ps.executeUpdate();
    }

    /**
     * @author Kartikey Singh
     */
    public static void insertIntoVhaCng(TransactionManager tmgr, String appl_no, String empCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VHA_RETROFITTING_DTLS
                + " SELECT  current_timestamp as moved_on, ? as moved_by, state_cd, off_cd,appl_no,kit_srno, kit_type, kit_manuf, kit_pucc_norms, workshop, \n"
                + "       workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, approval_no, \n"
                + "       approval_dt,op_dt\n"
                + "  FROM " + TableList.VA_RETROFITTING_DTLS + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, empCode);
        ps.setString(2, appl_no);

        ps.executeUpdate();
    }

    private static void updateVaCng(TransactionManager tmgr, RetroFittingDetailsDobj dobj, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "UPDATE " + TableList.VA_RETROFITTING_DTLS + " \n"
                + "   SET state_cd = ?,off_cd = ?,appl_no=?, kit_srno=?, kit_type=?, kit_manuf=?, kit_pucc_norms=?, \n"
                + "       workshop=?, workshop_lic_no=?, fitment_dt=?, hydro_test_dt=?, \n"
                + "       cyl_srno=?, approval_no=?, approval_dt=?,op_dt = current_timestamp\n"
                + " WHERE appl_no=?";
        ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
        ps.setString(i++, appl_no);
        ps.setString(i++, dobj.getKit_srno());
        ps.setString(i++, dobj.getKit_type());
        ps.setString(i++, dobj.getKit_manuf());
        ps.setString(i++, dobj.getKit_pucc_norms());
        ps.setString(i++, dobj.getWorkshop());
        ps.setString(i++, dobj.getWorkshop_lic_no());
        if (dobj.getInstall_dt() == null) {
            ps.setNull(i++, java.sql.Types.NULL);
        } else {
            ps.setDate(i++, new java.sql.Date(dobj.getInstall_dt().getTime()));
        }
        if (dobj.getHydro_dt() == null) {
            ps.setNull(i++, java.sql.Types.NULL);
        } else {
            ps.setDate(i++, new java.sql.Date(dobj.getHydro_dt().getTime()));
        }
        ps.setString(i++, dobj.getCyl_srno());
        ps.setString(i++, dobj.getApproval_no());
        ps.setDate(i++, new java.sql.Date(dobj.getApproval_dt().getTime()));
        ps.setString(i++, appl_no);
        ps.executeUpdate();
    }

    /*
     * Author: Kartikey Singh
     */
    private static void updateVaCng(TransactionManager tmgr, RetroFittingDetailsDobj dobj, String appl_no,
            String userStateCode, int offCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "UPDATE " + TableList.VA_RETROFITTING_DTLS + " \n"
                + "   SET state_cd = ?,off_cd = ?,appl_no=?, kit_srno=?, kit_type=?, kit_manuf=?, kit_pucc_norms=?, \n"
                + "       workshop=?, workshop_lic_no=?, fitment_dt=?, hydro_test_dt=?, \n"
                + "       cyl_srno=?, approval_no=?, approval_dt=?,op_dt = current_timestamp\n"
                + " WHERE appl_no=?";
        ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, userStateCode);
        ps.setInt(i++, offCode);
        ps.setString(i++, appl_no);
        ps.setString(i++, dobj.getKit_srno());
        ps.setString(i++, dobj.getKit_type());
        ps.setString(i++, dobj.getKit_manuf());
        ps.setString(i++, dobj.getKit_pucc_norms());
        ps.setString(i++, dobj.getWorkshop());
        ps.setString(i++, dobj.getWorkshop_lic_no());
        if (dobj.getInstall_dt() == null) {
            ps.setNull(i++, java.sql.Types.NULL);
        } else {
            ps.setDate(i++, new java.sql.Date(dobj.getInstall_dt().getTime()));
        }
        if (dobj.getHydro_dt() == null) {
            ps.setNull(i++, java.sql.Types.NULL);
        } else {
            ps.setDate(i++, new java.sql.Date(dobj.getHydro_dt().getTime()));
        }
        ps.setString(i++, dobj.getCyl_srno());
        ps.setString(i++, dobj.getApproval_no());
        ps.setDate(i++, new java.sql.Date(dobj.getApproval_dt().getTime()));
        ps.setString(i++, appl_no);
        ps.executeUpdate();
    }

    public static void insertIntoVtFromVaRetroFit(TransactionManager tmgr, Status_dobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VT_RETROFITTING_DTLS
                + " ( state_cd , off_cd , regn_no, kit_srno, kit_type, kit_manuf, kit_pucc_norms,"
                + "   workshop, workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno,"
                + "   approval_no, approval_dt, op_dt) "
                + "SELECT ?,?,?, kit_srno, kit_type, kit_manuf, kit_pucc_norms, workshop,"
                + "       workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, approval_no,"
                + "       approval_dt,current_timestamp "
                + "  FROM " + TableList.VA_RETROFITTING_DTLS + " WHERE appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getUserStateCode());
        ps.setInt(2, Util.getSelectedSeat().getOff_cd());
        ps.setString(3, dobj.getRegn_no());
        ps.setString(4, dobj.getAppl_no());
        ps.executeUpdate();
    } // end of insertIntoVtFromVaRetroFit
    
    /**
     * @author Kartikey Singh
     */
    public static void insertIntoVtFromVaRetroFit(TransactionManager tmgr, Status_dobj dobj, String userStateCode, int selectedOffCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VT_RETROFITTING_DTLS
                + " ( state_cd , off_cd , regn_no, kit_srno, kit_type, kit_manuf, kit_pucc_norms,"
                + "   workshop, workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno,"
                + "   approval_no, approval_dt, op_dt) "
                + "SELECT ?,?,?, kit_srno, kit_type, kit_manuf, kit_pucc_norms, workshop,"
                + "       workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, approval_no,"
                + "       approval_dt,current_timestamp "
                + "  FROM " + TableList.VA_RETROFITTING_DTLS + " WHERE appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, userStateCode);
        ps.setInt(2, selectedOffCode);
        ps.setString(3, dobj.getRegn_no());
        ps.setString(4, dobj.getAppl_no());
        ps.executeUpdate();
    }

    public static void insertIntoRetroFitVH(TransactionManager tmgr, Status_dobj dobj, String stateCode, int offCode, String oldRegn) throws SQLException {
        PreparedStatement ps = null;
        int pos = 1;

        String sql = "INSERT INTO " + TableList.VH_RETROFITTING_DTLS
                + " SELECT state_cd, off_cd, regn_no, kit_srno, kit_type, kit_manuf, kit_pucc_norms, "
                + "       workshop, workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, "
                + "       approval_no, approval_dt, op_dt, ? as appl_no, current_timestamp as moved_on, ? as moved_by "
                + "  FROM " + TableList.VT_RETROFITTING_DTLS + " WHERE regn_no = ? and state_cd = ? and off_cd = ? ";

        ps = tmgr.prepareStatement(sql);

        ps.setString(pos++, dobj.getAppl_no());
        ps.setString(pos++, String.valueOf(dobj.getEmp_cd()));
        if (!CommonUtils.isNullOrBlank(oldRegn)) {
            ps.setString(pos++, oldRegn);
        } else {
            ps.setString(pos++, dobj.getRegn_no());
        }
        ps.setString(pos++, stateCode);
        ps.setInt(pos++, offCode);
        ps.executeUpdate();

    } // end of insertIntoRetroFitVH

    public static void deleteFromVtRetroFit(TransactionManager tmgr, String regnNo, String stateCode, int offCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "DELETE FROM " + TableList.VT_RETROFITTING_DTLS + " WHERE regn_no=? and state_cd = ? and off_cd = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regnNo);
        ps.setString(2, stateCode);
        ps.setInt(3, offCode);
        ps.executeUpdate();

    } // end of deleteFromVtRetroFit

    public static void deleteFromVaRetroFittingDetails(TransactionManager tmgr, String applNo) throws VahanException {
        PreparedStatement ps;
        String sql;
        try {
            sql = "Delete from " + TableList.VA_RETROFITTING_DTLS + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }
    }
}
