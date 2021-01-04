/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.FitnessCancellationDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author ASHOK
 */
public class FitnessCancellationImpl {

    private static final Logger LOGGER = Logger.getLogger(FitnessCancellationImpl.class);

    public FitnessCancellationDobj getFitnessCancellationDobj(String applNo) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        FitnessCancellationDobj dobj = null;
        String sql = null;

        try {
            tmgr = new TransactionManager("getFitnessCancellationDobj");

            sql = "SELECT * FROM " + TableList.VA_FIT_CANCEL
                    + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                dobj = new FitnessCancellationDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setReason(rs.getString("reason"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            VahanException vahanexecption = new VahanException("Error in fetching details for Fitness");
            throw vahanexecption;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return dobj;
    }

    public void insertIntoFitCancelHistory(TransactionManager tmgr, String appl_no, String empCode) throws Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VHA_FIT_CANCEL
                + " SELECT a.*,current_timestamp as moved_on,? as moved_by "
                + "  FROM  " + TableList.VA_FIT_CANCEL
                + " a WHERE  a.appl_no=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, empCode);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public void updateFitCancel(TransactionManager tmgr, FitnessCancellationDobj dobj) throws Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = " update " + TableList.VA_FIT_CANCEL
                + " set reason=?,"
                + " op_dt=current_timestamp"
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);

        ps.setString(1, dobj.getReason());
        ps.setString(2, dobj.getAppl_no());
        ps.executeUpdate();
    }

    public void insertIntoFitCancel(TransactionManager tmgr, FitnessCancellationDobj dobj) throws Exception {
        PreparedStatement ps = null;
        String sql = null;
        sql = "INSERT INTO " + TableList.VA_FIT_CANCEL
                + " (state_cd, off_cd, appl_no, regn_no, reason, op_dt)"
                + " VALUES (?, ?, ?, ?, ?, current_timestamp)";
        ps = tmgr.prepareStatement(sql);

        ps.setString(1, dobj.getState_cd());
        ps.setInt(2, dobj.getOff_cd());
        ps.setString(3, dobj.getAppl_no());
        ps.setString(4, dobj.getRegn_no());
        ps.setString(5, dobj.getReason());
        ps.executeUpdate();
    }

    public void makeChangeFitCancel(FitnessCancellationDobj dobj, String changedata) throws Exception {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangeFitCancel");
            insertUpdateFitCancel(tmgr, dobj);
            ComparisonBeanImpl.updateChangedData(dobj.getAppl_no(), changedata, tmgr);
            tmgr.commit();
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }

    public void insertUpdateFitCancel(TransactionManager tmgr, FitnessCancellationDobj dobj) throws Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "SELECT regn_no FROM " + TableList.VA_FIT_CANCEL + " where appl_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getAppl_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) { //if any record is exist then update otherwise insert it
            insertIntoFitCancelHistory(tmgr, dobj.getAppl_no(), Util.getEmpCode());
            updateFitCancel(tmgr, dobj);
        } else {
            insertIntoFitCancel(tmgr, dobj);
        }
    }

    public void fitnessCancellation(FitnessCancellationDobj fitCanDobj, FitnessCancellationDobj fitCanPrvDobj, Status_dobj status_dobj, String changedData, String empCd) throws Exception {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        int i = 1;
        try {

            tmgr = new TransactionManager("fitnessCancellation");

            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);

            if (status_dobj.getCurrent_role() == TableConstants.FITNESS_CANCEL_ENTRY
                    || status_dobj.getCurrent_role() == TableConstants.FITNESS_CANCEL_VERIFICATION
                    || status_dobj.getCurrent_role() == TableConstants.FITNESS_CANCEL_APPROVAL) {

                if ((changedData != null && !changedData.equals("")) || fitCanPrvDobj == null) {
                    insertUpdateFitCancel(tmgr, fitCanDobj);//when there is change by user or Entry Scrutiny                    
                }

            }

            if (status_dobj.getCurrent_role() == TableConstants.FITNESS_CANCEL_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {



                sql = "INSERT INTO " + TableList.VH_FITNESS
                        + " SELECT state_cd, off_cd, ? as appl_no,regn_no, chasi_no, fit_chk_dt, fit_chk_tm, fit_result, fit_valid_to, fit_nid,"
                        + "        fit_off_cd1, fit_off_cd2, remark, fare_mtr_no, speedgov_no, speedgov_compname,  "
                        + "        brake, steer, susp, engin, tyre, horn, lamp, embo, speed, paint,  "
                        + "        wiper, dimen, body, fare, elec, finis, road, poll, transm, glas,  "
                        + "        emis, rear, others, op_dt, current_timestamp, ? as moved_by "
                        + "  FROM " + TableList.VT_FITNESS
                        + "  WHERE regn_no=? and state_cd=?";
                ps = tmgr.prepareStatement(sql);

                ps.setString(i++, fitCanDobj.getAppl_no());
                ps.setString(i++, empCd);
                ps.setString(i++, fitCanDobj.getRegn_no());
                ps.setString(i++, fitCanDobj.getState_cd());
                ps.executeUpdate();


                sql = " UPDATE " + TableList.VT_FITNESS + " set "
                        + " fit_valid_to=current_timestamp,"
                        + " fit_nid=current_date,"
                        + " remark=?,"
                        + " op_dt=current_timestamp "
                        + " WHERE regn_no=? and state_cd=?";
                ps = tmgr.prepareStatement(sql);
                i = 1;
                ps.setString(i++, "FC:" + fitCanDobj.getReason());//FC stands for Fitness Cancellation
                ps.setString(i++, fitCanDobj.getRegn_no());
                ps.setString(i++, fitCanDobj.getState_cd());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                sql = "INSERT INTO " + TableList.VH_OWNER
                        + "           (state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name,"
                        + "            f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state,"
                        + "            p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd,"
                        + "            regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type,"
                        + "            no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt,"
                        + "            gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap,"
                        + "            floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as,"
                        + "            vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length,"
                        + "            width, height, regn_upto, fit_upto, annual_income, imported_vch,"
                        + "            other_criteria, status, op_dt, appl_no, moved_on, moved_by,moved_status)"
                        + "   SELECT state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name,"
                        + "       f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state,"
                        + "       p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd,"
                        + "       regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type,"
                        + "       no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt,"
                        + "       gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap,"
                        + "       floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as,"
                        + "       vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length,"
                        + "       width, height, regn_upto, fit_upto, annual_income, imported_vch,"
                        + "       other_criteria, status, op_dt, ?,current_timestamp,?,?"
                        + "  FROM " + TableList.VT_OWNER
                        + "  where regn_no=? and state_cd=? ";
                ps = tmgr.prepareStatement(sql);
                i = 1;
                ps.setString(i++, fitCanDobj.getAppl_no());
                ps.setString(i++, empCd);
                ps.setString(i++, TableConstants.VH_MOVED_STATUS_UPDATE);
                ps.setString(i++, fitCanDobj.getRegn_no());
                ps.setString(i++, fitCanDobj.getState_cd());
                //ps.setInt(i++, fitCanDobj.getOff_cd());
                ps.executeUpdate();


                sql = " UPDATE " + TableList.VT_OWNER + " set "
                        + " fit_upto=current_timestamp,"
                        + " op_dt=current_timestamp "
                        + " WHERE regn_no=? and state_cd=?";
                ps = tmgr.prepareStatement(sql);
                i = 1;
                ps.setString(i++, fitCanDobj.getRegn_no());
                ps.setString(i++, fitCanDobj.getState_cd());
                // ps.setInt(i++, fitCanDobj.getOff_cd());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                sql = "INSERT INTO " + TableList.VHA_FIT_CANCEL
                        + " SELECT a.*,current_timestamp + interval '1 second' as moved_on,? as moved_by "
                        + "  FROM  " + TableList.VA_FIT_CANCEL
                        + " a WHERE  a.appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, empCd);
                ps.setString(2, fitCanDobj.getAppl_no());
                ps.executeUpdate();

                sql = "DELETE FROM " + TableList.VA_FIT_CANCEL + " where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, fitCanDobj.getAppl_no());
                ps.executeUpdate();

                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                //for updating the status of application when it is approved end
            }

            ServerUtil.insertIntoVhaChangedData(tmgr, fitCanDobj.getAppl_no(), changedData);//for saving the data into table those are changed by the user

            ServerUtil.fileFlow(tmgr, status_dobj); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();

        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
    }

    public void fitnessRevocation(FitnessCancellationDobj fitCanDobj, FitnessCancellationDobj fitCanPrvDobj, Status_dobj status_dobj, String changedData, String empCd, FitnessDobj fitnessDobj) throws Exception {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        int i = 1;

        try {
            tmgr = new TransactionManager("fitnessRevocation");

            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);

            if (status_dobj.getCurrent_role() == TableConstants.FITNESS_REVOKE_ENTRY
                    || status_dobj.getCurrent_role() == TableConstants.FITNESS_REVOKE_VERIFICATION
                    || status_dobj.getCurrent_role() == TableConstants.FITNESS_REVOKE_APPROVAL) {

                if ((changedData != null && !changedData.equals("")) || fitCanPrvDobj == null) {
                    insertUpdateFitRevoke(tmgr, fitCanDobj);//when there is change by user or Entry Scrutiny                    
                }
            }

            if (status_dobj.getCurrent_role() == TableConstants.FITNESS_REVOKE_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                sql = "INSERT INTO " + TableList.VH_FITNESS
                        + " SELECT state_cd, off_cd, ? as appl_no,regn_no, chasi_no, fit_chk_dt, fit_chk_tm, fit_result, fit_valid_to, fit_nid,"
                        + "        fit_off_cd1, fit_off_cd2, remark, fare_mtr_no, speedgov_no, speedgov_compname,  "
                        + "        brake, steer, susp, engin, tyre, horn, lamp, embo, speed, paint,  "
                        + "        wiper, dimen, body, fare, elec, finis, road, poll, transm, glas,  "
                        + "        emis, rear, others, op_dt, current_timestamp, ? as moved_by "
                        + "  FROM " + TableList.VT_FITNESS
                        + "  WHERE regn_no=? and state_cd=?";
                ps = tmgr.prepareStatement(sql);

                ps.setString(i++, fitCanDobj.getAppl_no());
                ps.setString(i++, empCd);
                ps.setString(i++, fitCanDobj.getRegn_no());
                ps.setString(i++, fitCanDobj.getState_cd());
                ps.executeUpdate();

                sql = " UPDATE " + TableList.VT_FITNESS + " set "
                        + " fit_valid_to=?,"
                        + " fit_nid=?,"
                        + " remark=?,"
                        + " op_dt=current_timestamp "
                        + " WHERE regn_no=? and state_cd=?";
                ps = tmgr.prepareStatement(sql);
                i = 1;
                ps.setDate(i++, new java.sql.Date(fitnessDobj.getFit_valid_to().getTime()));
                if (fitnessDobj.getFit_nid() == null) {
                    ps.setDate(i++, new java.sql.Date(fitnessDobj.getFit_valid_to().getTime()));
                } else {
                    ps.setDate(i++, new java.sql.Date(fitnessDobj.getFit_nid().getTime()));
                }
                ps.setString(i++, "FR:" + fitCanDobj.getReason());//FR stands for Fitness Revocation
                ps.setString(i++, fitCanDobj.getRegn_no());
                ps.setString(i++, fitCanDobj.getState_cd());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                sql = "INSERT INTO " + TableList.VH_OWNER
                        + "           (state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name,"
                        + "            f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state,"
                        + "            p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd,"
                        + "            regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type,"
                        + "            no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt,"
                        + "            gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap,"
                        + "            floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as,"
                        + "            vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length,"
                        + "            width, height, regn_upto, fit_upto, annual_income, imported_vch,"
                        + "            other_criteria, status, op_dt, appl_no, moved_on, moved_by,moved_status)"
                        + "   SELECT state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name,"
                        + "       f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state,"
                        + "       p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd,"
                        + "       regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type,"
                        + "       no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt,"
                        + "       gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap,"
                        + "       floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as,"
                        + "       vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length,"
                        + "       width, height, regn_upto, fit_upto, annual_income, imported_vch,"
                        + "       other_criteria, status, op_dt, ?,current_timestamp,?,?"
                        + "  FROM " + TableList.VT_OWNER
                        + "  where regn_no=? and state_cd=?";
                ps = tmgr.prepareStatement(sql);
                i = 1;
                ps.setString(i++, fitCanDobj.getAppl_no());
                ps.setString(i++, empCd);
                ps.setString(i++, TableConstants.VH_MOVED_STATUS_UPDATE);
                ps.setString(i++, fitCanDobj.getRegn_no());
                ps.setString(i++, fitCanDobj.getState_cd());
                ps.executeUpdate();

                sql = " UPDATE " + TableList.VT_OWNER + " set "
                        + " fit_upto=?,"
                        + " op_dt=current_timestamp "
                        + " WHERE regn_no=? and state_cd=?";
                ps = tmgr.prepareStatement(sql);
                i = 1;
                ps.setDate(i++, new java.sql.Date(fitnessDobj.getFit_valid_to().getTime()));
                ps.setString(i++, fitCanDobj.getRegn_no());
                ps.setString(i++, fitCanDobj.getState_cd());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                sql = "INSERT INTO " + TableList.VHA_FIT_REVOKE
                        + " SELECT current_timestamp + interval '1 second' as moved_on,? as moved_by,a.* "
                        + "  FROM  " + TableList.VA_FIT_REVOKE
                        + " a WHERE  a.appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, empCd);
                ps.setString(2, fitCanDobj.getAppl_no());
                ps.executeUpdate();

                sql = "DELETE FROM " + TableList.VA_FIT_REVOKE + " where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, fitCanDobj.getAppl_no());
                ps.executeUpdate();

                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);

            }
            ServerUtil.insertIntoVhaChangedData(tmgr, fitCanDobj.getAppl_no(), changedData);//for saving the data into table those are changed by the user

            ServerUtil.fileFlow(tmgr, status_dobj); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();


        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
    }

    public FitnessCancellationDobj getFitnessRevokeDetails(String applNo) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        FitnessCancellationDobj dobj = null;
        String sql = null;

        try {
            tmgr = new TransactionManager("getFitnessRevokeDetails");

            sql = "SELECT * FROM " + TableList.VA_FIT_REVOKE
                    + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                dobj = new FitnessCancellationDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setReason(rs.getString("reason"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            VahanException vahanexecption = new VahanException("Error in fetching details for Fitness");
            throw vahanexecption;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj;
    }

    public void insertIntoFitRevoke(TransactionManager tmgr, FitnessCancellationDobj dobj) throws Exception {
        PreparedStatement ps = null;
        String sql = null;
        sql = "INSERT INTO " + TableList.VA_FIT_REVOKE
                + " (state_cd, off_cd, appl_no, regn_no, reason, op_dt)"
                + " VALUES (?, ?, ?, ?, ?, current_timestamp)";
        ps = tmgr.prepareStatement(sql);

        ps.setString(1, dobj.getState_cd());
        ps.setInt(2, dobj.getOff_cd());
        ps.setString(3, dobj.getAppl_no());
        ps.setString(4, dobj.getRegn_no());
        ps.setString(5, dobj.getReason());
        ps.executeUpdate();
    }

    public void updateFitRevoke(TransactionManager tmgr, FitnessCancellationDobj dobj) throws Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = " update " + TableList.VA_FIT_REVOKE
                + " set reason=?,"
                + " op_dt=current_timestamp"
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);

        ps.setString(1, dobj.getReason());
        ps.setString(2, dobj.getAppl_no());
        ps.executeUpdate();
    }

    public void insertIntoFitRevokeHistory(TransactionManager tmgr, String appl_no, String empCode) throws Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VHA_FIT_REVOKE
                + " SELECT current_timestamp as moved_on,? as moved_by,a.* "
                + "  FROM  " + TableList.VA_FIT_REVOKE
                + " a WHERE  a.appl_no=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, empCode);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public void makeChangeFitRevoke(FitnessCancellationDobj dobj, String changedata) throws Exception {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangeFitRevoke");
            insertUpdateFitRevoke(tmgr, dobj);
            ComparisonBeanImpl.updateChangedData(dobj.getAppl_no(), changedata, tmgr);
            tmgr.commit();
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }

    public void insertUpdateFitRevoke(TransactionManager tmgr, FitnessCancellationDobj dobj) throws Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "SELECT regn_no FROM " + TableList.VA_FIT_REVOKE + " where appl_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getAppl_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) { //if any record is exist then update otherwise insert it
            insertIntoFitRevokeHistory(tmgr, dobj.getAppl_no(), Util.getEmpCode());
            updateFitRevoke(tmgr, dobj);
        } else {
            insertIntoFitRevoke(tmgr, dobj);
        }
    }
}
