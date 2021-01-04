/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.ImportedVehicleDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC103
 */
public class ImportedVehicleImpl {

    private static final Logger LOGGER = Logger.getLogger(ImportedVehicleImpl.class);

    public static ImportedVehicleDobj setImpVehDetails_db_to_dobj(String appl_no, String regn_no, String state_cd, int off_cd) {
        ImportedVehicleDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        String param = "";
        boolean vtFlag = false;
        if (appl_no != null) {
            vtFlag = false;
            param = appl_no.toUpperCase();
            sql = "SELECT contry_code, dealer, place, foreign_regno, manu_year\n"
                    + "  FROM " + TableList.VA_IMPORT_VEH + " where appl_no=?";
        }
        if (regn_no != null) {
            vtFlag = true;
            param = regn_no.toUpperCase();
            sql = "SELECT contry_code, dealer, place, foreign_regno, manu_year\n"
                    + "  FROM " + TableList.VT_IMPORT_VEH + " where regn_no=? and state_cd = ? and off_cd = ? ";
        }

        try {
            tmgr = new TransactionManager("setImpVehDetails_db_to_dobj");

            ps = tmgr.prepareStatement(sql);
            if (vtFlag) {
                ps.setString(1, param);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
            } else {
                ps.setString(1, param);
            }

            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new ImportedVehicleDobj();
                dobj.setCm_country_imp(rs.getInt("contry_code"));
                dobj.setTf_dealer_imp(rs.getString("dealer"));
                dobj.setTf_place_imp(rs.getString("place"));
                dobj.setTf_foreign_imp(rs.getString("foreign_regno"));
                dobj.setTf_YOM_imp(rs.getInt("manu_year"));

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

    public static void saveImportedDetails_Impl(ImportedVehicleDobj dobj, String appl_no, TransactionManager tmgr) {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "SELECT * FROM " + TableList.VA_IMPORT_VEH + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoVhaImpVeh(tmgr, appl_no);
                updateImpVeh(tmgr, dobj, appl_no);
            } else {
                insertImpVeh(tmgr, dobj, appl_no);
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }


    }

    private static void insertImpVeh(TransactionManager tmgr, ImportedVehicleDobj dobj, String appl_no) {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "INSERT INTO " + TableList.VA_IMPORT_VEH + " (\n"
                    + "     state_cd,off_cd,appl_no, contry_code, dealer, place, foreign_regno, manu_year,op_dt)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?,?,?,current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, appl_no);
            ps.setInt(i++, dobj.getCm_country_imp());
            ps.setString(i++, dobj.getTf_dealer_imp());
            ps.setString(i++, dobj.getTf_place_imp());
            ps.setString(i++, dobj.getTf_foreign_imp());
            ps.setInt(i++, dobj.getTf_YOM_imp());
            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public static void insertIntoVhaImpVeh(TransactionManager tmgr, String appl_no) {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "INSERT INTO " + TableList.VHA_IMPORT_VEH + " \n"
                    + "SELECT current_timestamp as moved_on, ? as moved_by, state_cd, off_cd,appl_no, contry_code, dealer, place, foreign_regno, manu_year,op_dt \n"
                    + "  FROM va_import_veh where appl_no=?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    private static void updateImpVeh(TransactionManager tmgr, ImportedVehicleDobj dobj, String appl_no) {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "UPDATE " + TableList.VA_IMPORT_VEH + " \n"
                    + "   SET state_cd = ?,off_cd = ?,appl_no=?, contry_code=?, dealer=?, place=?, foreign_regno=?, \n"
                    + "       manu_year=?,op_dt = current_timestamp\n"
                    + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, appl_no);
            ps.setInt(i++, dobj.getCm_country_imp());
            ps.setString(i++, dobj.getTf_dealer_imp());
            ps.setString(i++, dobj.getTf_place_imp());
            ps.setString(i++, dobj.getTf_foreign_imp());
            ps.setInt(i++, dobj.getTf_YOM_imp());
            ps.setString(i++, appl_no);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public static void insertIntoVtFromVaImportedVeh(TransactionManager tmgr, Status_dobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VT_IMPORT_VEH
                + " ( state_cd,off_cd,regn_no, contry_code, dealer, place, foreign_regno, manu_year,op_dt) "
                + "  SELECT ?,?,?, contry_code, dealer, place, foreign_regno, manu_year ,current_timestamp"
                + "  FROM " + TableList.VA_IMPORT_VEH
                + "  WHERE appl_no=?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getUserStateCode());
        ps.setInt(2, Util.getSelectedSeat().getOff_cd());
        ps.setString(3, dobj.getRegn_no());
        ps.setString(4, dobj.getAppl_no());
        ps.executeUpdate();

    } // end of insertIntoVtFromVaImportedVeh

    public static void insertIntoImportedVehVH(TransactionManager tmgr, Status_dobj dobj, String stateCode, int offCode, String oldRegn) throws SQLException {
        PreparedStatement ps = null;
        int pos = 1;

        String sql = "INSERT INTO " + TableList.VH_IMPORT_VEH
                + "    SELECT  state_cd, off_cd, regn_no, contry_code, dealer, place, foreign_regno, "
                + "       manu_year, ? as appl_no, current_timestamp as moved_on, ? as moved_by "
                + "  FROM " + TableList.VT_IMPORT_VEH + " WHERE regn_no = ? and state_cd = ? and off_cd = ?";

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

    } // end of insertIntoImportedVehVH

    public static void deleteFromVtImportedVeh(TransactionManager tmgr, String regnNo, String stateCode, int offCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "DELETE FROM " + TableList.VT_IMPORT_VEH + " WHERE regn_no=? and state_cd = ? and off_cd = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regnNo);
        ps.setString(2, stateCode);
        ps.setInt(3, offCode);
        ps.executeUpdate();

    } // end of deleteFromVtImportedVeh

    public static void deleteFromVaImp(TransactionManager tmgr, String applNo) throws VahanException {
        PreparedStatement ps;
        String sql;
        try {
            sql = "Delete from " + TableList.VA_IMPORT_VEH + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }
    }

    public ImportedVehicleDobj getImportedVehicleDetails(String regnNo, String stateCd, int offCd) {
        ImportedVehicleDobj dobj = null;
        String sql = "SELECT regn_no, contry_code, dealer, place, foreign_regno, manu_year\n"
                + "  FROM " + TableList.VT_IMPORT_VEH + " where regn_no=? and state_cd = ? and off_cd = ?";
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("getImportedVehicleDetails");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new ImportedVehicleDobj();
                dobj.setCm_country_imp(rs.getInt("contry_code"));
                dobj.setTf_dealer_imp(rs.getString("dealer"));
                dobj.setTf_place_imp(rs.getString("place"));
                dobj.setTf_foreign_imp(rs.getString("foreign_regno"));
                dobj.setTf_YOM_imp(rs.getInt("manu_year"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return dobj;

    }
}
