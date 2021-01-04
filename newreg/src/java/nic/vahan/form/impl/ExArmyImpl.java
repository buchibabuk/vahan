/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.ExArmyDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC103
 */
public class ExArmyImpl {

    private static final Logger LOGGER = Logger.getLogger(ExArmyImpl.class);

    public static ExArmyDobj setExArmyDetails_db_to_dobj(String appl_no, String regn_no, String state_cd, int off_cd) {

        String condition = null;
        String sql = null;
        ExArmyDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean vtFlag = false;

        if (appl_no != null) {
            vtFlag = false;
            condition = appl_no.toUpperCase();
            sql = "SELECT voucher_no,voucher_dt,place FROM " + TableList.VA_OWNER_EX_ARMY + " where appl_no = ?";
        }

        if (regn_no != null) {
            vtFlag = true;
            condition = regn_no.toUpperCase();
            sql = "SELECT voucher_no,voucher_dt,place FROM " + TableList.VT_OWNER_EX_ARMY + " where regn_no = ?"
                    + " and state_cd = ? and off_cd = ? ";
        }


        try {
            tmgr = new TransactionManager("setExArmyDetails_db_to_dobj");
            ps = tmgr.prepareStatement(sql);
            if (vtFlag) {
                ps.setString(1, condition);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
            } else {
                ps.setString(1, condition);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new ExArmyDobj();
                dobj.setTf_Voucher_no(rs.getString("voucher_no"));
                dobj.setTf_VoucherDate((java.util.Date) rs.getDate("voucher_dt"));
                dobj.setTf_POP(rs.getString("place"));
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

    public static void saveExArmyVehicleDetails_Impl(ExArmyDobj dobj, String appl_no, TransactionManager tmgr) {
        PreparedStatement ps = null;
        String sql = null;
        try {
            //inserting data into vha_insurance from va_insurance
            sql = "SELECT appl_no FROM " + TableList.VA_OWNER_EX_ARMY + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoVhaExArmy(tmgr, appl_no);
                updateExArmy(tmgr, dobj, appl_no);
            } else {
                insertExArmy(tmgr, dobj, appl_no);
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    private static void insertExArmy(TransactionManager tmgr, ExArmyDobj dobj, String appl_no) {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "INSERT INTO " + TableList.VA_OWNER_EX_ARMY + " (\n"
                    + "     state_cd,off_cd,appl_no, voucher_no, voucher_dt, place,op_dt)\n"
                    + "    VALUES (?,?,?, ?, ?, ?,current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, appl_no);
            ps.setString(i++, dobj.getTf_Voucher_no());
            ps.setDate(i++, new java.sql.Date(dobj.getTf_VoucherDate().getTime()));
            ps.setString(i++, dobj.getTf_POP());
            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public static void insertIntoVhaExArmy(TransactionManager tmgr, String appl_no) {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "INSERT INTO " + TableList.VHA_OWNER_EX_ARMY + " \n"
                    + "SELECT current_timestamp as moved_on, ? as moved_by, state_cd, off_cd,appl_no, voucher_no, voucher_dt, place,op_dt \n"
                    + "FROM va_owner_ex_army where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    private static void updateExArmy(TransactionManager tmgr, ExArmyDobj dobj, String appl_no) {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "UPDATE " + TableList.VA_OWNER_EX_ARMY + " \n"
                    + "   SET state_cd = ?,off_cd = ?,appl_no=?, voucher_no=?, voucher_dt=?, place=?,op_dt = current_timestamp\n"
                    + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, appl_no);
            ps.setString(i++, dobj.getTf_Voucher_no());
            ps.setDate(i++, new java.sql.Date(dobj.getTf_VoucherDate().getTime()));
            ps.setString(i++, dobj.getTf_POP());
            ps.setString(i++, appl_no);
            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public static void insertIntoVtFromVaOwnerExArmy(TransactionManager tmgr, Status_dobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VT_OWNER_EX_ARMY
                + " ( state_cd , off_cd , regn_no, voucher_no, voucher_dt, place , op_dt ) "
                + "  SELECT ?,?,?,voucher_no, voucher_dt, place , current_timestamp "
                + "  FROM " + TableList.VA_OWNER_EX_ARMY + " WHERE appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getUserStateCode());
        ps.setInt(2, Util.getSelectedSeat().getOff_cd());
        ps.setString(3, dobj.getRegn_no());
        ps.setString(4, dobj.getAppl_no());
        ps.executeUpdate();

    } // end of insertIntoVtFromVaOwnerExArmy

    public static void insertIntoOwnerExArmyVH(TransactionManager tmgr, Status_dobj dobj, String stateCode, int offCode, String oldRegn) throws SQLException {
        PreparedStatement ps = null;
        int pos = 1;

        String sql = "INSERT INTO " + TableList.VH_OWNER_EX_ARMY
                + "    SELECT  state_cd, off_cd, regn_no, voucher_no, "
                + "    voucher_dt, place, ? as appl_no, current_timestamp as moved_on, ? as moved_by "
                + "  FROM " + TableList.VT_OWNER_EX_ARMY + " WHERE regn_no = ? and state_cd = ? and off_cd = ? ";

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

    } // end of insertIntoOwnerExArmyVH

    public static void deleteFromVtOwnerExArmy(TransactionManager tmgr, String regnNo, String stateCode, int offCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "DELETE FROM " + TableList.VT_OWNER_EX_ARMY + " WHERE regn_no=? and state_cd = ? and off_cd = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regnNo);
        ps.setString(2, stateCode);
        ps.setInt(3, offCode);
        ps.executeUpdate();

    } // end of deleteFromVtOwnerExArmy

    public static void deleteFromVaExArmy(TransactionManager tmgr, String applNo) throws VahanException {
        PreparedStatement ps;
        String sql;
        try {
            sql = "Delete from " + TableList.VA_OWNER_EX_ARMY + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }
    }
}
