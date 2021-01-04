/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.admin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.DelDupRegnNoDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.ExArmyImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.ImportedVehicleImpl;
import nic.vahan.form.impl.NewImpl;
import nic.vahan.form.impl.RetroFittingDetailsImpl;
import nic.vahan.form.impl.TempRegImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC075
 */
public class DelDupRegnNoImpl {

    private static Logger LOGGER = Logger.getLogger(DelDupRegnNoImpl.class);

    public boolean deleteDuplicateVehicle(OwnerDetailsDobj ownerDetail) {
        boolean isDelete = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        Status_dobj dobj = new Status_dobj();
        try {
            tmgr = new TransactionManager("deleteDuplicateVehicle");
            String sql = "INSERT INTO " + TableList.VH_DEDUPLICATION + "(\n"
                    + "            state_cd,  off_cd, regn_no, moved_by, op_dt ) "
                    + "    VALUES (?, ?, ?, ?,  current_timestamp )";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDetail.getState_cd());
            ps.setInt(2, ownerDetail.getOff_cd());
            ps.setString(3, ownerDetail.getRegn_no());
            ps.setString(4, Util.getEmpCode());
            ps.executeUpdate();
            dobj.setAppl_no(ownerDetail.getRegn_no());
            dobj.setEmp_cd(Long.parseLong(Util.getEmpCode()));
            //deleting axle records
            AxleImpl.insertIntoAxleVH(tmgr, dobj, ownerDetail.getState_cd(), ownerDetail.getOff_cd(), ownerDetail.getRegn_no());
            AxleImpl.deleteFromVtAxle(tmgr, ownerDetail.getRegn_no(), ownerDetail.getState_cd(), ownerDetail.getOff_cd());
            //deleting Hypth Records
            if ("TN".equalsIgnoreCase(ownerDetail.getState_cd()) && !CommonUtils.isNullOrBlank(ownerDetail.getMoveHPADetails()) && "true".equalsIgnoreCase(ownerDetail.getMoveHPADetails())) {
                HpaImpl.insertIntoHypthVH(tmgr, dobj, ownerDetail.getState_cd(), ownerDetail.getOff_cd(), ownerDetail.getRegn_no());
                sql = "update vt_hypth set off_cd=? where state_cd=? and (regn_no,off_cd,op_dt) in (select regn_no,off_cd,op_dt from vt_hypth where state_cd=? and regn_no=? order by op_dt desc limit 1)";
                ps = tmgr.prepareStatement(sql);
                ps.setInt(1, Util.getUserSeatOffCode());
                ps.setString(2, ownerDetail.getState_cd());
                ps.setString(3, ownerDetail.getState_cd());
                ps.setString(4, ownerDetail.getRegn_no());
                ps.executeUpdate();

            } else {
                HpaImpl.insertIntoHypthVH(tmgr, dobj, ownerDetail.getState_cd(), ownerDetail.getOff_cd(), ownerDetail.getRegn_no());
                HpaImpl.deleteFromVtHypth(tmgr, ownerDetail.getRegn_no(), ownerDetail.getState_cd(), ownerDetail.getOff_cd());
            }

            //deleting import vehicle
            ImportedVehicleImpl.insertIntoImportedVehVH(tmgr, dobj, ownerDetail.getState_cd(), ownerDetail.getOff_cd(), ownerDetail.getRegn_no());
            ImportedVehicleImpl.deleteFromVtImportedVeh(tmgr, ownerDetail.getRegn_no(), ownerDetail.getState_cd(), ownerDetail.getOff_cd());
            //deleting insurance vehicle
            insertUpdateInsurance(tmgr, ownerDetail.getRegn_no(), ownerDetail.getState_cd(), Util.getEmpCode(), ownerDetail.getOff_cd());

            //deleting qwner ex army
            ExArmyImpl.insertIntoOwnerExArmyVH(tmgr, dobj, ownerDetail.getState_cd(), ownerDetail.getOff_cd(), ownerDetail.getRegn_no());
            ExArmyImpl.deleteFromVtOwnerExArmy(tmgr, ownerDetail.getRegn_no(), ownerDetail.getState_cd(), ownerDetail.getOff_cd());

            //deleting qwner identification
            insertUpdateIdentificationDtls(tmgr, ownerDetail.getRegn_no(), ownerDetail.getState_cd(), ownerDetail.getOff_cd());
            // deleting retrofitting details 
            RetroFittingDetailsImpl.insertIntoRetroFitVH(tmgr, dobj, ownerDetail.getState_cd(), ownerDetail.getOff_cd(), ownerDetail.getRegn_no());
            RetroFittingDetailsImpl.deleteFromVtRetroFit(tmgr, ownerDetail.getRegn_no(), ownerDetail.getState_cd(), ownerDetail.getOff_cd());

            // deleting temp regn details
            TempRegImpl.deleteFromVtTmpRegnDtls(tmgr, dobj, ownerDetail.getState_cd(), ownerDetail.getOff_cd(), ownerDetail.getRegn_no());

            //Delete Other state Vehicle
            OwnerAdminImpl ownerAdminImpl = new OwnerAdminImpl();
            Owner_dobj ownerDobj = new Owner_dobj();
            ownerDobj.setAppl_no(ownerDetail.getRegn_no());
            ownerDobj.setRegn_no(ownerDetail.getRegn_no());
            ownerDobj.setState_cd(ownerDetail.getState_cd());
            ownerDobj.setOff_cd(ownerDetail.getOff_cd());
            ownerAdminImpl.insertVhOtherState(ownerDobj, tmgr);
            sql = "Delete from " + TableList.VT_OTHER_STATE_VEH
                    + " where new_regn_no=? and state_cd= ? and off_cd= ?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDetail.getRegn_no());
            ps.setString(2, ownerDetail.getState_cd());
            ps.setInt(3, ownerDetail.getOff_cd());
            ps.executeUpdate();

            //Deleting from vt owner
            NewImpl.insertIntoVhOwner(tmgr, dobj, ownerDetail.getState_cd(), ownerDetail.getOff_cd(), ownerDetail.getRegn_no());
            sql = "delete from " + TableList.VDD_OWNER + " where state_cd=? and off_cd=? and regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDetail.getState_cd());
            ps.setInt(2, ownerDetail.getOff_cd());
            ps.setString(3, ownerDetail.getRegn_no());
            ps.executeUpdate();
            sql = "delete from " + TableList.VT_OWNER + " where state_cd=? and off_cd=? and regn_no=?";
            int i = 0;
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDetail.getState_cd());
            ps.setInt(2, ownerDetail.getOff_cd());
            ps.setString(3, ownerDetail.getRegn_no());
            i = ps.executeUpdate();
            if (i > 0) {
                isDelete = true;
            } else {
                isDelete = false;
            }
            tmgr.commit();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);

        } finally {

            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return isDelete;
    }

    /**
     * @return the LOGGER
     */
    public boolean reStoreDuplicateVehicle(OwnerDetailsDobj ownerDetail, String emp_code) {
        boolean isDelete = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("reStoreDuplicateVehicle");
            String sql = "INSERT INTO vh_deduplication_restore Select state_cd, off_cd, regn_no, op_dt as moved_on, moved_by,current_timestamp,? as restore_by from vh_deduplication "
                    + " where state_cd=? and off_cd=? and regn_no=? and op_dt=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, emp_code);
            ps.setString(2, ownerDetail.getState_cd());
            ps.setInt(3, ownerDetail.getOff_cd());
            ps.setString(4, ownerDetail.getRegn_no());
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(ownerDetail.getMoved_on()));
            ps.executeUpdate();
            sql = "delete from " + TableList.VH_DEDUPLICATION + " "
                    + " where state_cd=? and off_cd=? and regn_no=? and op_dt=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDetail.getState_cd());
            ps.setInt(2, ownerDetail.getOff_cd());
            ps.setString(3, ownerDetail.getRegn_no());
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(ownerDetail.getMoved_on()));
            int del_flag = ps.executeUpdate();
            //Insert axle records
            if (del_flag > 0) {
                sql = "INSERT INTO " + TableList.VT_AXLE
                        + " SELECT state_cd, off_cd, regn_no, f_axle_descp, r_axle_descp, o_axle_descp, "
                        + "       t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, t_axle_weight, "
                        + "       current_timestamp as op_dt, no_of_axles, r_overhang, f_axle_tyre, r_axle_tyre, o_axle_tyre, t_axle_tyre "
                        + "  FROM " + TableList.VH_AXLE + " WHERE regn_no = ? and state_cd = ? and off_cd = ? and moved_on=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, ownerDetail.getRegn_no());
                ps.setString(2, ownerDetail.getState_cd());
                ps.setInt(3, ownerDetail.getOff_cd());
                ps.setTimestamp(4, java.sql.Timestamp.valueOf(ownerDetail.getMoved_on()));
                ps.executeUpdate();

                //Insert Hypth Records
                sql = "INSERT INTO " + TableList.VT_HYPTH
                        + " SELECT state_cd, off_cd, regn_no, sr_no, hp_type, fncr_name, fncr_add1, "
                        + "       fncr_add2, fncr_add3, fncr_district, fncr_pincode, fncr_state,"
                        + "        from_dt, current_timestamp as op_dt "
                        + "  FROM " + TableList.VH_HYPTH + " WHERE regn_no = ? and state_cd = ? and off_cd=? and moved_on=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, ownerDetail.getRegn_no());
                ps.setString(2, ownerDetail.getState_cd());
                ps.setInt(3, ownerDetail.getOff_cd());
                ps.setTimestamp(4, java.sql.Timestamp.valueOf(ownerDetail.getMoved_on()));
                ps.executeUpdate();

                //Insert import vehicle
                sql = "INSERT INTO " + TableList.VT_IMPORT_VEH
                        + "    SELECT  state_cd, off_cd, regn_no, contry_code, dealer, place, foreign_regno,  "
                        + "       manu_year, current_timestamp as op_dt"
                        + "  FROM " + TableList.VH_IMPORT_VEH + " WHERE regn_no = ? and state_cd = ? and off_cd = ? and moved_on=? ";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, ownerDetail.getRegn_no());
                ps.setString(2, ownerDetail.getState_cd());
                ps.setInt(3, ownerDetail.getOff_cd());
                ps.setTimestamp(4, java.sql.Timestamp.valueOf(ownerDetail.getMoved_on()));
                ps.executeUpdate();

                //Insert insurance vehicle

                sql = "select * from  " + TableList.VT_INSURANCE + " where regn_no=? and state_cd = ? and off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, ownerDetail.getRegn_no());
                ps.setString(2, ownerDetail.getState_cd());
                ps.setInt(3, ownerDetail.getOff_cd());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (!rs.next()) {
                    sql = "INSERT into " + TableList.VT_INSURANCE + " "
                            + "Select state_cd, off_cd, regn_no, comp_cd, ins_type, ins_from, ins_upto,"
                            + " policy_no, idv, current_timestamp as op_dt "
                            + " FROM " + TableList.VH_INSURANCE + " where regn_no=? and state_cd = ? and off_cd=? and moved_on=? ";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, ownerDetail.getRegn_no());
                    ps.setString(2, ownerDetail.getState_cd());
                    ps.setInt(3, ownerDetail.getOff_cd());
                    ps.setTimestamp(4, java.sql.Timestamp.valueOf(ownerDetail.getMoved_on()));
                    ps.executeUpdate();
                }
                //Insert qwner ex army
                sql = "INSERT INTO " + TableList.VT_OWNER_EX_ARMY
                        + "    SELECT  state_cd, off_cd, regn_no, voucher_no, voucher_dt, place, current_timestamp as op_dt"
                        + "  FROM " + TableList.VH_OWNER_EX_ARMY + " WHERE regn_no = ? and state_cd = ? and off_cd = ? and moved_on= ?  ";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, ownerDetail.getRegn_no());
                ps.setString(2, ownerDetail.getState_cd());
                ps.setInt(3, ownerDetail.getOff_cd());
                ps.setTimestamp(4, java.sql.Timestamp.valueOf(ownerDetail.getMoved_on()));
                ps.executeUpdate();

                //Insert qwner identification
                sql = "select * from  " + TableList.VT_OWNER_IDENTIFICATION + " where regn_no=? and state_cd = ? and off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, ownerDetail.getRegn_no());
                ps.setString(2, ownerDetail.getState_cd());
                ps.setInt(3, ownerDetail.getOff_cd());
                RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                if (!rs1.next()) {
                    sql = "INSERT INTO " + TableList.VT_OWNER_IDENTIFICATION
                            + "            SELECT state_cd, off_cd, regn_no, mobile_no, email_id, pan_no, aadhar_no, "
                            + "            passport_no, ration_card_no, voter_id, dl_no,verified_on, owner_ctg,dept_cd "
                            + "  FROM " + TableList.VH_OWNER_IDENTIFICATION
                            + " WHERE regn_no = ? and state_cd = ? and off_cd = ? and moved_on=? ";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, ownerDetail.getRegn_no());
                    ps.setString(2, ownerDetail.getState_cd());
                    ps.setInt(3, ownerDetail.getOff_cd());
                    ps.setTimestamp(4, java.sql.Timestamp.valueOf(ownerDetail.getMoved_on()));
                    ps.executeUpdate();
                }

                // Insert retrofitting details 
                sql = "INSERT INTO " + TableList.VT_RETROFITTING_DTLS
                        + " SELECT state_cd, off_cd, regn_no, kit_srno, kit_type, kit_manuf, kit_pucc_norms, "
                        + "       workshop, workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, "
                        + "        approval_no, approval_dt, current_timestamp as op_dt "
                        + "  FROM " + TableList.VH_RETROFITTING_DTLS + " WHERE regn_no = ? and state_cd = ? and off_cd = ? and moved_on=?  ";


                ps = tmgr.prepareStatement(sql);
                ps.setString(1, ownerDetail.getRegn_no());
                ps.setString(2, ownerDetail.getState_cd());
                ps.setInt(3, ownerDetail.getOff_cd());
                ps.setTimestamp(4, java.sql.Timestamp.valueOf(ownerDetail.getMoved_on()));
                ps.executeUpdate();

                // Insert temp regn details
                sql = "INSERT INTO " + TableList.VT_TMP_REGN_DTL + " (SELECT state_cd, off_cd, regn_no, tmp_off_cd, regn_auth, tmp_state_cd, "
                        + "  tmp_regn_no, tmp_regn_dt, tmp_valid_upto, dealer_cd, current_timestamp as op_dt "
                        + "  FROM " + TableList.VH_TMP_REGN_DTL + " where regn_no=? and state_cd = ? and off_cd = ? and  moved_on=? )";


                ps = tmgr.prepareStatement(sql);
                ps.setString(1, ownerDetail.getRegn_no());
                ps.setString(2, ownerDetail.getState_cd());
                ps.setInt(3, ownerDetail.getOff_cd());
                ps.setTimestamp(4, java.sql.Timestamp.valueOf(ownerDetail.getMoved_on()));
                ps.executeUpdate();
                //Insert into VT_OTHER_STATE_VEH  
                sql = "insert into  " + TableList.VT_OTHER_STATE_VEH + " \n"
                        + "  SELECT state_cd, off_cd, new_regn_no, entry_dt, old_regn_no, old_rgst_auth, \n"
                        + "       old_off_cd, old_state_cd, ncrb_ref, confirm_ref, noc_dt, noc_no, \n"
                        + "       current_timestamp as op_dt\n"
                        + "  FROM  " + TableList.VH_OTHER_STATE_VEH + "  where new_regn_no=? and state_cd=? and off_cd=? and moved_on=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, ownerDetail.getRegn_no());
                ps.setString(2, ownerDetail.getState_cd());
                ps.setInt(3, ownerDetail.getOff_cd());
                ps.setTimestamp(4, java.sql.Timestamp.valueOf(ownerDetail.getMoved_on()));
                ps.executeUpdate();
                //insert deduplication.vdd_owner
                sql = "SELECT regn_no\n"
                        + "  FROM deduplication.vdd_owner where regn_no=? and state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, ownerDetail.getRegn_no());
                ps.setString(2, ownerDetail.getState_cd());
                ps.setInt(3, ownerDetail.getOff_cd());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (!rs.next()) {
                    sql = "INSERT INTO deduplication.vdd_owner(regn_no, state_cd, off_cd) VALUES (?, ?, ?)";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, ownerDetail.getRegn_no());
                    ps.setString(2, ownerDetail.getState_cd());
                    ps.setInt(3, ownerDetail.getOff_cd());
                    ps.executeUpdate();
                }
                //Insert into vt owner
                sql = "INSERT INTO " + TableList.VT_OWNER
                        + "    SELECT state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name,"
                        + "           f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state,"
                        + "           p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd, "
                        + "           regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type, "
                        + "           no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt,"
                        + "           gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap,"
                        + "           floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as,"
                        + "           vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length,"
                        + "           width, height, regn_upto, fit_upto, annual_income, imported_vch,"
                        + "           other_criteria, status, current_timestamp as op_dt "
                        + "  FROM " + TableList.VH_OWNER + " WHERE regn_no = ? and state_cd = ? and off_cd =? and moved_on=?";
                int i = 0;
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, ownerDetail.getRegn_no());
                ps.setString(2, ownerDetail.getState_cd());
                ps.setInt(3, ownerDetail.getOff_cd());
                ps.setTimestamp(4, java.sql.Timestamp.valueOf(ownerDetail.getMoved_on()));
                i = ps.executeUpdate();
                if (i > 0) {
                    isDelete = true;
                    tmgr.commit();

                } else {
                    isDelete = false;
                }

            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {

            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return isDelete;
    }

    public void insertUpdateInsurance(TransactionManager tmgr, String regn_no, String stateCd, String empCd, int offcd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "INSERT into " + TableList.VH_INSURANCE + " "
                    + "Select regn_no, comp_cd, ins_type, ins_from,"
                    + " ins_upto, policy_no, current_timestamp as moved_on, ? as moved_by, state_cd, off_cd, idv "
                    + " FROM " + TableList.VT_INSURANCE + " where regn_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCd);
            ps.setString(2, regn_no);
            ps.setString(3, stateCd);
            ps.setInt(4, offcd);
            ps.executeUpdate();

            sql = "Delete from " + TableList.VT_INSURANCE + " where regn_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offcd);
            ps.executeUpdate();

        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void insertUpdateIdentificationDtls(TransactionManager tmgr, String regn_no, String stateCode, int offCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VH_OWNER_IDENTIFICATION
                + "            SELECT state_cd,off_cd,regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no,"
                + "            ration_card_no, voter_id, dl_no, verified_on, current_timestamp as moved_on, ? moved_by,owner_ctg,dept_cd "
                + "  FROM " + TableList.VT_OWNER_IDENTIFICATION
                + " WHERE regn_no = ? and state_cd = ? and off_cd=?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, regn_no);
        ps.setString(3, stateCode);
        ps.setInt(4, offCode);
        ps.executeUpdate();
        sql = "DELETE FROM " + TableList.VT_OWNER_IDENTIFICATION + " WHERE regn_no=? and state_cd = ? and off_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regn_no);
        ps.setString(2, stateCode);
        ps.setInt(3, offCode);
        ps.executeUpdate();

    } // end of insertUpdateIdentificationHistoryVH

    // vdd owner
    public List<DelDupRegnNoDobj> getDupVddRegnList(String stateCd, int offCd) throws VahanException {
        List<DelDupRegnNoDobj> vDDownerList = new ArrayList<>();
        DelDupRegnNoDobj delDupRegnNoDobj = null;
        String sql = "select b.regn_no, count(1)as total, string_agg(b.state_cd || b.off_cd::varchar, ',')  as offices \n"
                + " from (select regn_no from deduplication.vdd_owner where state_cd =? and off_cd =?) a, deduplication.vdd_owner b\n"
                + " where a.regn_no = b.regn_no\n"
                + " group by 1 having count(1) > 1 order by 1;";
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("getDupVddRegnList");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                delDupRegnNoDobj = new DelDupRegnNoDobj();
                delDupRegnNoDobj.setRegnNo(rs.getString("regn_no"));
                delDupRegnNoDobj.setTotalOffices(rs.getInt("total"));
                delDupRegnNoDobj.setOffices(rs.getString("offices"));
                vDDownerList.add(delDupRegnNoDobj);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return vDDownerList;
    }
}
