/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl;

import java.sql.PreparedStatement;
import java.util.Date;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.CdDobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author Kartikey Singh
 */
public class CdImplementation {

    private static final Logger LOGGER = Logger.getLogger(CdImplementation.class);

    public void insertUpdateVaCd(CdDobj dobj, TransactionManager tmgr) throws VahanException {
        String sql = "Select * from " + TableList.VA_CD_REGN_DTL + " where appl_no=? ";
        try {
            int i = 1;
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(i++, dobj.getApplNo());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                insertIntoVhaCd(dobj, tmgr);
                updateVaCd(dobj, tmgr);
            } else {
                insertIntoVaCd(dobj, tmgr);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in CD Vehicles Details");
        }
    }

    /**
     * @author Kartikey Singh
     */
    public void insertUpdateVaCd(CdDobj dobj, TransactionManager tmgr, String empCode) throws VahanException {
        String sql = "Select * from " + TableList.VA_CD_REGN_DTL + " where appl_no=? ";
        try {
            int i = 1;
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(i++, dobj.getApplNo());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                insertIntoVhaCd(dobj, tmgr, empCode);
                updateVaCd(dobj, tmgr);
            } else {
                insertIntoVaCd(dobj, tmgr);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in CD Vehicles Details");
        }
    }

    public CdDobj getVaCdDobj(String applNo) throws VahanException {
        CdDobj dobj = null;
        TransactionManager tmgr = null;
        String sql = "Select * from " + TableList.VA_CD_REGN_DTL + " where appl_no=? ";
        try {
            tmgr = new TransactionManager("getVaCdDobj");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new CdDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setRegNo(rs.getString("regn_no"));
                dobj.setCdRegnNo(rs.getString("cd_regn_no"));
                dobj.setSaleDate(rs.getDate("sale_dt"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in CD Vehicles Details");
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

    public void insertIntoVaCd(CdDobj dobj, TransactionManager tmgr) throws VahanException {
        String sql = "INSERT INTO " + TableList.VA_CD_REGN_DTL + "(state_cd,off_cd,appl_no, regn_no, cd_regn_no, sale_dt,op_dt)"
                + " VALUES (?,?,?, ?, ?, ?,current_timestamp)";
        try {
            int i = 1;
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(i++, dobj.getState_cd());
            ps.setInt(i++, dobj.getOff_cd());
            ps.setString(i++, dobj.getApplNo());
            ps.setString(i++, dobj.getRegNo());
            ps.setString(i++, dobj.getCdRegnNo() == null ? "" : dobj.getCdRegnNo());
            if (dobj.getSaleDate() == null) {
                ps.setDate(i++, new java.sql.Date(new Date().getTime()));
            } else {
                ps.setDate(i++, new java.sql.Date(dobj.getSaleDate().getTime()));
            }
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Insertion of CD details ");
        }

    }

    public void updateVaCd(CdDobj dobj, TransactionManager tmgr) throws VahanException {

        String sql = "UPDATE " + TableList.VA_CD_REGN_DTL
                + "   SET state_cd=?,off_cd=?, appl_no=?, regn_no=?, cd_regn_no=?, sale_dt=?,op_dt = current_timestamp"
                + " WHERE appl_no=?";

        try {
            int i = 1;
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(i++, dobj.getState_cd());
            ps.setInt(i++, dobj.getOff_cd());
            ps.setString(i++, dobj.getApplNo());
            ps.setString(i++, dobj.getRegNo());
            ps.setString(i++, dobj.getCdRegnNo());
            ps.setDate(i++, new java.sql.Date(dobj.getSaleDate().getTime()));
            ps.setString(i++, dobj.getApplNo());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Insertion of CD details ");
        }


    }

    public void insertIntoVhaCd(CdDobj dobj, TransactionManager tmgr) throws VahanException {
        String sql = "INSERT INTO " + TableList.VHA_CD_REGN_DTL
                + " Select current_timestamp as moved_on, ? as moved_by,state_cd,off_cd, appl_no, regn_no, cd_regn_no, sale_dt,op_dt"
                + "  from " + TableList.VA_CD_REGN_DTL + " where appl_no=? ";
        try {
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, dobj.getApplNo());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Insertion of VH CD details ");
        }

    }

    /**
     * @author Kartikey Singh
     */
    public void insertIntoVhaCd(CdDobj dobj, TransactionManager tmgr, String empCode) throws VahanException {
        String sql = "INSERT INTO " + TableList.VHA_CD_REGN_DTL
                + " Select current_timestamp as moved_on, ? as moved_by,state_cd,off_cd, appl_no, regn_no, cd_regn_no, sale_dt,op_dt"
                + "  from " + TableList.VA_CD_REGN_DTL + " where appl_no=? ";
        try {
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, dobj.getApplNo());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Insertion of VH CD details ");
        }

    }

    public void insertIntoVtCd(CdDobj dobj, TransactionManager tmgr) throws VahanException {

        try {
            PreparedStatement ps = null;
            String sql = "INSERT INTO " + TableList.VT_CD_REGN_DTL
                    + " Select state_cd,off_cd, ?, cd_regn_no, sale_dt "
                    + "  from " + TableList.VA_CD_REGN_DTL + " where appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getRegNo());
            ps.setString(2, dobj.getApplNo());
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Insertion of Vt CD details ");
        }

    }

    public void insertOrUpdateCdVehicleDtls(CdDobj dobj, TransactionManager tmgr) throws VahanException {
        try {
            PreparedStatement ps = null;
            RowSet rs = null;
            String sql = " Select *  from " + TableList.VA_CD_REGN_DTL + " where appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getApplNo());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                updateVaCd(dobj, tmgr);
            } else {
                insertIntoVaCd(dobj, tmgr);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Insertion/Updation of CD details ");
        }
    }
}
