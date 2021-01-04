/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.VehicleTrackingDetailsDobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author DELL
 */
public class VehicleTrackingDetailsImpl {

    private static final Logger LOGGER = Logger.getLogger(VehicleTrackingDetailsImpl.class);

    public VehicleTrackingDetailsDobj getVehicleTrackingDetailsByChasiOrRegn_no(String chasi_no, String regno) throws VahanException {
        VehicleTrackingDetailsDobj dobj = null;
        PreparedStatement ps = null;
        String sql = null;
        String parameter_regn_no_or_chassi_no = null;
        TransactionManager tmgr = null;
        try {
            if (chasi_no != null) {
                parameter_regn_no_or_chassi_no = chasi_no;
            } else {
                parameter_regn_no_or_chassi_no = regno;
            }

            sql = " SELECT * from getvltdinfo(?)";
            tmgr = new TransactionManager("getVehicleTrackingDetailsByChasiOrRegn_no");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, parameter_regn_no_or_chassi_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new VehicleTrackingDetailsDobj();
                dobj.setRegn_no(regno);
                dobj.setChasi_no(chasi_no);
                dobj.setDevice_sr_no(rs.getString("device_sr_no"));
                dobj.setImei_no(rs.getString("imei_no"));
                dobj.setIcc_id(rs.getString("icc_id"));
                dobj.setActivation_rcpt_no(rs.getString("activation_rcpt_no"));
                dobj.setDevice_activation_date(rs.getDate("device_activation_date"));
                dobj.setDevice_activated_upto(rs.getDate("device_activated_upto"));
                dobj.setDevice_activation_status(rs.getString("device_activation_status"));
                dobj.setVltd_manufacturer(rs.getString("vltd_manufacturer"));
                dobj.setFitted_date(rs.getDate("fitted_date"));
                dobj.setFitment_center(rs.getString("fitment_center"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0] + "-Appl_no-" + parameter_regn_no_or_chassi_no);
            throw new VahanException("Error: Unable to get VLTD Details!!!!!");
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

    public VehicleTrackingDetailsDobj getVehicleTrackingDetailsByAppl_no(String appl_no, String state_cd, int off_cd) throws VahanException {
        VehicleTrackingDetailsDobj dobj = null;
        PreparedStatement ps = null;
        String sql = null;
        TransactionManager tmgr = null;
        try {
            sql = "SELECT state_cd, off_cd, appl_no, regn_no,chasi_no, device_sr_no, imei_no, icc_id, activation_rcpt_no, device_activation_date, device_activated_upto, device_activation_status, vltd_manufacturer, fitted_date, fitment_center, op_dt\n"
                    + "	FROM " + TableList.VA_VLTD + " where appl_no=? and state_cd=? and off_cd=?";

            tmgr = new TransactionManager("getVehicleTrackingDetailsByAppl_no");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new VehicleTrackingDetailsDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setDevice_sr_no(rs.getString("device_sr_no"));
                dobj.setImei_no(rs.getString("imei_no"));
                dobj.setIcc_id(rs.getString("icc_id"));
                dobj.setActivation_rcpt_no(rs.getString("activation_rcpt_no"));
                dobj.setDevice_activation_date(rs.getDate("device_activation_date"));
                dobj.setDevice_activated_upto(rs.getDate("device_activated_upto"));
                dobj.setDevice_activation_status(rs.getString("device_activation_status"));
                dobj.setVltd_manufacturer(rs.getString("vltd_manufacturer"));
                dobj.setFitted_date(rs.getDate("fitted_date"));
                dobj.setFitment_center(rs.getString("fitment_center"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0] + "-Appl_no-" + appl_no);
            throw new VahanException("Error: Unable to get VLTD Details!!!!!");
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

    public void updateStatusVehicleTrackingDetails(VehicleTrackingDetailsDobj dobj, String state_cd, int off_cd, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        int i = 1;
        try {

            VehicleTrackingDetailsDobj vehicleTrackingDetailsDobj = getVehicleTrackingDetailsByAppl_no(dobj.getAppl_no(), state_cd, off_cd);
            if (vehicleTrackingDetailsDobj == null) {
                sql = "INSERT INTO " + TableList.VA_VLTD + "(state_cd, off_cd, appl_no, regn_no,chasi_no, device_sr_no, imei_no, icc_id, "
                        + " activation_rcpt_no, device_activation_date, device_activated_upto, device_activation_status, vltd_manufacturer,"
                        + " fitted_date, fitment_center, op_dt)"
                        + "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, current_timestamp);";

                ps = tmgr.prepareStatement(sql);
                ps.setString(i++, state_cd);
                ps.setInt(i++, off_cd);
                ps.setString(i++, dobj.getAppl_no());
                ps.setString(i++, dobj.getRegn_no());
                ps.setString(i++, dobj.getChasi_no());
                ps.setString(i++, dobj.getDevice_sr_no());
                ps.setString(i++, dobj.getImei_no());
                ps.setString(i++, dobj.getIcc_id());
                ps.setString(i++, dobj.getActivation_rcpt_no());
                ps.setDate(i++, new java.sql.Date(dobj.getDevice_activation_date().getTime()));
                ps.setDate(i++, new java.sql.Date(dobj.getDevice_activated_upto().getTime()));
                ps.setString(i++, dobj.getDevice_activation_status());
                ps.setString(i++, dobj.getVltd_manufacturer());
                ps.setDate(i++, new java.sql.Date(dobj.getFitted_date().getTime()));
                ps.setString(i++, dobj.getFitment_center());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0] + "-Appl_no-" + dobj.getAppl_no());
            throw new VahanException("Error: Unable to Insert Data !!!!!");
        }
    }

    public void insertVhaVehicleTrackFromVaVehicleTrack(VehicleTrackingDetailsDobj dobj, String state_cd, int off_cd, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "  INSERT INTO " + TableList.VHA_VLTD + "(\n"
                    + "	moved_on, moved_by, state_cd, off_cd, appl_no, regn_no,chasi_no, device_sr_no, imei_no, icc_id, activation_rcpt_no, device_activation_date, device_activated_upto, device_activation_status, vltd_manufacturer, fitted_date, fitment_center, op_dt)\n"
                    + "	SELECT current_timestamp, ?, state_cd, off_cd, appl_no, regn_no,chasi_no, device_sr_no, imei_no, icc_id, activation_rcpt_no, device_activation_date, device_activated_upto, device_activation_status, vltd_manufacturer, fitted_date, fitment_center, op_dt\n"
                    + " FROM " + TableList.VA_VLTD + " where appl_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, dobj.getAppl_no());
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);
            ps.executeUpdate();
            sql = "UPDATE " + TableList.VA_VLTD + "\n"
                    + "	SET  regn_no=?\n"
                    + "	WHERE  appl_no=? and state_cd=? and off_cd=?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_no());
            ps.setString(2, dobj.getAppl_no());
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0] + "-Appl_no-" + dobj.getAppl_no());
            throw new VahanException("Error:" + TableConstants.SomthingWentWrong);
        }
    }

    public void insertVtVehicleTrackFromVaVehicleTrack(VehicleTrackingDetailsDobj dobj, String state_cd, int off_cd, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "INSERT INTO " + TableList.VH_VLTD + "(state_cd, off_cd, regn_no, chasi_no, device_sr_no, imei_no, icc_id, activation_rcpt_no,"
                    + " device_activation_date, device_activated_upto, device_activation_status, vltd_manufacturer, fitted_date, fitment_center, op_dt, moved_on, moved_by)"
                    + "	(SELECT state_cd, off_cd, regn_no, chasi_no, device_sr_no, imei_no, icc_id, activation_rcpt_no, device_activation_date, device_activated_upto,"
                    + " device_activation_status, vltd_manufacturer, fitted_date, fitment_center, op_dt,current_timestamp,? as moved_by	FROM  " + TableList.VT_VLTD + " where regn_no=? and state_cd=? and off_cd=?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, dobj.getRegn_no());
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);
            ps.executeUpdate();

            ServerUtil.deleteFromTable(tmgr, dobj.getRegn_no(), "", TableList.VT_VLTD);

            sql = "INSERT INTO " + TableList.VT_VLTD + "(\n"
                    + "	state_cd, off_cd, regn_no, chasi_no,device_sr_no, imei_no, icc_id, activation_rcpt_no, device_activation_date, device_activated_upto, device_activation_status, vltd_manufacturer, fitted_date, fitment_center, op_dt)\n"
                    + "	SELECT  state_cd, off_cd,  regn_no,chasi_no, device_sr_no, imei_no, icc_id, activation_rcpt_no, device_activation_date, device_activated_upto, device_activation_status, vltd_manufacturer, fitted_date, fitment_center, op_dt\n"
                    + "    FROM " + TableList.VA_VLTD + " where appl_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getAppl_no());
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0] + "-Appl_no-" + dobj.getAppl_no());
            throw new VahanException("Error:" + TableConstants.SomthingWentWrong);
        }
    }

    public void approveStatusVehTrackDetails(VehicleTrackingDetailsDobj dobj, String state_cd, int off_cd, TransactionManager tmgr) throws VahanException {
        try {
            insertVhaVehicleTrackFromVaVehicleTrack(dobj, state_cd, off_cd, tmgr);
            insertVtVehicleTrackFromVaVehicleTrack(dobj, state_cd, off_cd, tmgr);
            ServerUtil.deleteFromTable(tmgr, "", dobj.getAppl_no(), TableList.VA_VLTD);
        } catch (VahanException | SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0] + "-Appl_no-" + dobj.getAppl_no());
            throw new VahanException("Approval Error:" + TableConstants.SomthingWentWrong);
        }
    }

    public VehicleTrackingDetailsDobj getVltdDetails(String chasi_no, String regno) throws VahanException {
        VehicleTrackingDetailsDobj dobj = null;
        PreparedStatement ps = null;
        String sql = null;

        TransactionManager tmgr = null;
        try {


            sql = " SELECT * from getvltdinfo(?)";
            tmgr = new TransactionManager("getVehicleTrackingDetailsByChasiOrRegn_no");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regno);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new VehicleTrackingDetailsDobj();
                dobj.setRegn_no(regno);
                dobj.setChasi_no(chasi_no);
                dobj.setDevice_sr_no(rs.getString("device_sr_no"));
                dobj.setImei_no(rs.getString("imei_no"));
                dobj.setIcc_id(rs.getString("icc_id"));
                dobj.setActivation_rcpt_no(rs.getString("activation_rcpt_no"));
                dobj.setDevice_activation_date(rs.getDate("device_activation_date"));
                dobj.setDevice_activated_upto(rs.getDate("device_activated_upto"));
                dobj.setDevice_activation_status(rs.getString("device_activation_status"));
                dobj.setVltd_manufacturer(rs.getString("vltd_manufacturer"));
                dobj.setFitted_date(rs.getDate("fitted_date"));
                dobj.setFitment_center(rs.getString("fitment_center"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0] + "-Appl_no-" + regno);
            throw new VahanException("Error: Unable to get VLTD Details!!!!!");
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

    public VehicleTrackingDetailsDobj getVLTDDetailsByRegnNo(String regno, String state_cd, int off_cd) throws VahanException {
        VehicleTrackingDetailsDobj dobj = null;
        PreparedStatement ps = null;
        String sql = null;
        TransactionManager tmgr = null;
        try {

            sql = " SELECT device_sr_no,imei_no,icc_id,activation_rcpt_no,device_activation_date,device_activated_upto,device_activated_upto,"
                    + " device_activation_status,vltd_manufacturer,fitted_date,fitment_center from " + TableList.VT_VLTD + " where regn_no=? and state_cd=? and off_cd=?";
            tmgr = new TransactionManager("getVLTDDetailsByRegnNo");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regno);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new VehicleTrackingDetailsDobj();
                dobj.setRegn_no(regno);
                dobj.setDevice_sr_no(rs.getString("device_sr_no"));
                dobj.setImei_no(rs.getString("imei_no"));
                dobj.setIcc_id(rs.getString("icc_id"));
                dobj.setActivation_rcpt_no(rs.getString("activation_rcpt_no"));
                dobj.setDevice_activation_date(rs.getDate("device_activation_date"));
                dobj.setDevice_activated_upto(rs.getDate("device_activated_upto"));
                dobj.setDevice_activation_status(rs.getString("device_activation_status"));
                dobj.setVltd_manufacturer(rs.getString("vltd_manufacturer"));
                dobj.setFitted_date(rs.getDate("fitted_date"));
                dobj.setFitment_center(rs.getString("fitment_center"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0] + "-Regn No-" + regno);
            throw new VahanException("Error: Unable to get VLTD Details!!!!!");
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
}
