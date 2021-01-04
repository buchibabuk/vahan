/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.admin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.ExArmyDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.ImportedVehicleDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.NocDobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.Trailer_dobj;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.ExArmyImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.ImportedVehicleImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.OtherStateVehImpl;
import nic.vahan.form.impl.OwnerIdentificationImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.RetroFittingDetailsImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

public class NocEndorsementImpl {

    private static final Logger LOGGER = Logger.getLogger(OwnerImpl.class);

    public NocDobj getVtNocData(String regn_no) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        NocDobj noc_dobj = null;
        String sql = "SELECT noc_dt,state_to,off_to,noc_no FROM " + TableList.VT_NOC + " WHERE regn_no=? and state_cd= ? and off_cd = ? order by noc_dt desc limit 1";
        try {
            tmgr = new TransactionManager("getVtNocDate");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                noc_dobj = new NocDobj();
                noc_dobj.setState_to(rs.getString("state_to"));
                noc_dobj.setOff_to(rs.getInt("off_to"));
                noc_dobj.setNoc_no(rs.getString("noc_no"));
                noc_dobj.setNoc_dt(rs.getDate("noc_dt"));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem in NOC Data");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return noc_dobj;
    }

    public void insertDeleteForNocEndorsement(String regnNo, String chasiNo, NocDobj nocDobj, String stateCd, int offCd, FitnessDobj fitnessDobj, ExArmyDobj exArmyDobj, AxleDetailsDobj axleDetailsdobj, InsDobj ins_dobj, ImportedVehicleDobj imp_dobj, RetroFittingDetailsDobj cng_dobj, Trailer_dobj tra_dobj, List<HpaDobj> hypth) throws VahanException {

        TransactionManager tmgr = null;
        String applNo = null;
        try {
            tmgr = new TransactionManager("insertDeleteForNocEndorsement");

            Status_dobj statusDobj = new Status_dobj();
            statusDobj.setRegn_no(regnNo);
            statusDobj.setState_cd(stateCd);
            statusDobj.setOff_cd(offCd);
            statusDobj.setAppl_no(regnNo);

            this.insertDeleteOwnerDetails(tmgr, statusDobj);
            this.insertDeleteOwnerIdentificationDetails(tmgr, statusDobj);
            this.insertDeleteOtherStateVehicleDetails(tmgr, statusDobj);
            this.insertNocEndorsementDetails(tmgr, chasiNo, nocDobj, statusDobj);
            if (!hypth.isEmpty()) {
                this.insertDeleteHypoDetails(tmgr, statusDobj);
            }

            if (ins_dobj != null) {
                this.insertDeleteInsuranceDetails(tmgr, statusDobj, stateCd, Util.getEmpCode());
            }
            if (axleDetailsdobj != null) {
                this.insertDeleteAxleDetails(tmgr, statusDobj);
            }
            if (exArmyDobj != null) {
                this.insertDeleteExArmyDetails(tmgr, statusDobj);
            }
            if (imp_dobj != null) {
                this.insertDeleteImportedDetails(tmgr, statusDobj);
            }
            if (cng_dobj != null) {
                this.insertDeleteRetroFittingDetails(tmgr, statusDobj);
            }
            if (fitnessDobj != null) {
                this.insertDeleteFitnessDetails(tmgr, statusDobj);
            }
            if (tra_dobj != null) {
                this.insertDeleteTrailerDetails(tmgr, statusDobj);
            }
            tmgr.commit();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem in Endorsement of NOC");
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

    void insertDeleteOwnerDetails(TransactionManager tmgr, Status_dobj statusDobj) throws SQLException, Exception {
        PreparedStatement ps = null;
        String insertSql = "INSERT INTO " + TableList.VH_OWNER
                + " (state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name,"
                + " f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state,"
                + " p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd,"
                + " regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type,"
                + " no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt,"
                + " gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap,"
                + " floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as,"
                + " vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length,"
                + " width, height, regn_upto, fit_upto, annual_income, imported_vch,"
                + " other_criteria, status, op_dt, appl_no, moved_on, moved_by,moved_status)"
                + "  SELECT state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name, "
                + "     f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state, "
                + "     p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd, "
                + "     regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type, "
                + "     no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt, "
                + "     gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap, "
                + "     floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as, "
                + "     vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length, "
                + "     width, height, regn_upto, fit_upto, annual_income, imported_vch, "
                + "  other_criteria, status,op_dt,? as appl_no,current_timestamp as moved_on, ? as moved_by,? "
                + "  FROM vt_owner where regn_no = ? and state_cd = ? and off_cd = ? ";

        ps = tmgr.prepareStatement(insertSql);
        int i = 1;
        ps.setString(i++, statusDobj.getAppl_no());
        ps.setString(i++, String.valueOf(Util.getEmpCode()));
        ps.setString(i++, TableConstants.VH_MOVED_STATUS_DELETE);
        ps.setString(i++, statusDobj.getRegn_no());
        ps.setString(i++, statusDobj.getState_cd());
        ps.setInt(i++, statusDobj.getOff_cd());
        ps.executeUpdate();


        String deleteSql = "DELETE FROM " + TableList.VT_OWNER + " WHERE regn_no = ? and state_cd = ? and off_cd = ? ";
        ps = tmgr.prepareStatement(deleteSql);
        int j = 1;
        ps.setString(j++, statusDobj.getRegn_no());
        ps.setString(j++, statusDobj.getState_cd());
        ps.setInt(j++, statusDobj.getOff_cd());
        ps.executeUpdate();
    }

    void insertDeleteOwnerIdentificationDetails(TransactionManager tmgr, Status_dobj statusDobj) throws SQLException, Exception {
        OwnerIdentificationImpl.insertIntoOwnerIdentificationHistoryVH(tmgr, statusDobj.getRegn_no(), statusDobj.getState_cd(), statusDobj.getOff_cd());
        OwnerIdentificationImpl.deleteFromVtOwnerIdentification(tmgr, statusDobj.getRegn_no(), statusDobj.getState_cd(), statusDobj.getOff_cd());
    }

    void insertDeleteOtherStateVehicleDetails(TransactionManager tmgr, Status_dobj statusDobj) throws SQLException, Exception {
        OtherStateVehImpl otherStateVeh = new OtherStateVehImpl();
        otherStateVeh.insertIntoOtherStateVehHistoryVH(tmgr, statusDobj.getRegn_no(), null, statusDobj.getState_cd(), statusDobj.getOff_cd());
        otherStateVeh.deleteFromVtOtherStateVeh(tmgr, statusDobj.getRegn_no(), statusDobj.getState_cd(), statusDobj.getOff_cd());
    }

    void insertNocEndorsementDetails(TransactionManager tmgr, String chasiNo, NocDobj nocDobj, Status_dobj statusDobj) throws SQLException, Exception {
        PreparedStatement ps = null;
        String insertSql = "INSERT INTO vt_noc_endorsement(state_cd, off_cd, regn_no, chasi_no, state_to, off_to, noc_no, noc_dt, moved_by, moved_on) \n"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";

        ps = tmgr.prepareStatement(insertSql);
        int i = 1;
        ps.setString(i++, statusDobj.getState_cd());
        ps.setInt(i++, statusDobj.getOff_cd());
        ps.setString(i++, statusDobj.getRegn_no());
        ps.setString(i++, chasiNo);
        ps.setString(i++, nocDobj.getState_to());
        ps.setInt(i++, nocDobj.getOff_to());
        ps.setString(i++, nocDobj.getNoc_no());
        ps.setDate(i++, new java.sql.Date(nocDobj.getNoc_dt().getTime()));
        ps.setString(i++, String.valueOf(Util.getEmpCode()));
        ps.executeUpdate();
    }

    void insertDeleteInsuranceDetails(TransactionManager tmgr, Status_dobj statusDobj, String stateCd, String empCd) throws SQLException, Exception {
        InsImpl.insert_ins_dtls_to_Vh_insurance(tmgr, statusDobj.getRegn_no(), stateCd, empCd);
    }

    void insertDeleteAxleDetails(TransactionManager tmgr, Status_dobj statusDobj) throws SQLException, Exception {
        AxleImpl.insertIntoAxleVH(tmgr, statusDobj, statusDobj.getState_cd(), statusDobj.getOff_cd(), null);
        AxleImpl.deleteFromVtAxle(tmgr, statusDobj.getRegn_no(), statusDobj.getState_cd(), statusDobj.getOff_cd());
    }

    void insertDeleteExArmyDetails(TransactionManager tmgr, Status_dobj statusDobj) throws SQLException, Exception {
        ExArmyImpl.insertIntoOwnerExArmyVH(tmgr, statusDobj, statusDobj.getState_cd(), statusDobj.getOff_cd(), null);
        ExArmyImpl.deleteFromVtOwnerExArmy(tmgr, statusDobj.getRegn_no(), statusDobj.getState_cd(), statusDobj.getOff_cd());
    }

    void insertDeleteHypoDetails(TransactionManager tmgr, Status_dobj statusDobj) throws SQLException, Exception {
        HpaImpl.insertIntoHypthVH(tmgr, statusDobj, statusDobj.getState_cd(), statusDobj.getOff_cd(), null);
        HpaImpl.deleteFromVtHypth(tmgr, statusDobj.getRegn_no(), statusDobj.getState_cd(), statusDobj.getOff_cd());
    }

    void insertDeleteImportedDetails(TransactionManager tmgr, Status_dobj statusDobj) throws SQLException, Exception {
        ImportedVehicleImpl.insertIntoImportedVehVH(tmgr, statusDobj, statusDobj.getState_cd(), statusDobj.getOff_cd(), null);
        ImportedVehicleImpl.deleteFromVtImportedVeh(tmgr, statusDobj.getRegn_no(), statusDobj.getState_cd(), statusDobj.getOff_cd());
    }

    void insertDeleteRetroFittingDetails(TransactionManager tmgr, Status_dobj statusDobj) throws SQLException, Exception {
        RetroFittingDetailsImpl.insertIntoRetroFitVH(tmgr, statusDobj, statusDobj.getState_cd(), statusDobj.getOff_cd(), null);
        RetroFittingDetailsImpl.deleteFromVtRetroFit(tmgr, statusDobj.getRegn_no(), statusDobj.getState_cd(), statusDobj.getOff_cd());
    }

    void insertDeleteFitnessDetails(TransactionManager tmgr, Status_dobj statusDobj) throws SQLException, Exception {
        PreparedStatement ps = null;
        String insertSql = "INSERT INTO " + TableList.VH_FITNESS
                + " SELECT state_cd, off_cd, ? as appl_no,regn_no, chasi_no, fit_chk_dt, fit_chk_tm, fit_result, fit_valid_to, fit_nid,  "
                + "        fit_off_cd1, fit_off_cd2, remark, fare_mtr_no, speedgov_no, speedgov_compname,  "
                + "        brake, steer, susp, engin, tyre, horn, lamp, embo, speed, paint,  "
                + "        wiper, dimen, body, fare, elec, finis, road, poll, transm, glas,  "
                + "        emis, rear, others, op_dt, current_timestamp,? as moved_by "
                + "  FROM " + TableList.VT_FITNESS
                + "  WHERE regn_no=? and state_cd=? and off_cd=?";
        ps = tmgr.prepareStatement(insertSql);
        int i = 1;
        ps.setString(i++, statusDobj.getAppl_no());
        ps.setString(i++, String.valueOf(Util.getEmpCode()));
        ps.setString(i++, statusDobj.getRegn_no());
        ps.setString(i++, statusDobj.getState_cd());
        ps.setInt(i++, statusDobj.getOff_cd());
        ps.executeUpdate();


        String deleteSql = "DELETE FROM " + TableList.VT_FITNESS
                + " WHERE regn_no=? and state_cd=? and off_cd=?";
        ps = tmgr.prepareStatement(deleteSql);
        int j = 1;
        ps.setString(j++, statusDobj.getRegn_no());
        ps.setString(j++, statusDobj.getState_cd());
        ps.setInt(j++, statusDobj.getOff_cd());
        ps.executeUpdate();
    }

    void insertDeleteTrailerDetails(TransactionManager tmgr, Status_dobj statusDobj) throws SQLException, Exception {
        PreparedStatement ps = null;
        String insertSql = "INSERT INTO " + TableList.VH_TRAILER
                + " SELECT state_cd, off_cd,regn_no, chasi_no, body_type, ld_wt, unld_wt, f_axle_descp, r_axle_descp, "
                + " o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, "
                + " t_axle_weight, ? as appl_no,current_timestamp as moved_on, ? as moved_by "
                + " FROM vt_trailer where regn_no=? and state_cd=? and off_cd=?";
        ps = tmgr.prepareStatement(insertSql);
        int i = 1;
        ps.setString(i++, statusDobj.getAppl_no());
        ps.setString(i++, String.valueOf(Util.getEmpCode()));
        ps.setString(i++, statusDobj.getRegn_no());
        ps.setString(i++, statusDobj.getState_cd());
        ps.setInt(i++, statusDobj.getOff_cd());
        ps.executeUpdate();


        String deleteSql = "DELETE FROM " + TableList.VT_TRAILER
                + " WHERE regn_no=? and state_cd=? and off_cd=? ";
        ps = tmgr.prepareStatement(deleteSql);
        int j = 1;
        ps.setString(j++, statusDobj.getRegn_no());
        ps.setString(j++, statusDobj.getState_cd());
        ps.setInt(j++, statusDobj.getOff_cd());
        ps.executeUpdate();
    }
}
