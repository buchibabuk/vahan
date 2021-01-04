/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TempRcCancelDobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;

/**
 *
 * @author Administrator
 */
public class TempRcCancelImpl {

    private static final Logger LOGGER = Logger.getLogger(TempRcCancelImpl.class);

    public TempRcCancelDobj getOwnerDetails(String text) {
        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        TempRcCancelDobj dobj = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("getOwnerDetails");
            sql = "SELECT tempOwner.*,to_char(tempOwner.valid_from,'dd-Mon-yyyy') as validFrom,to_char(tempOwner.valid_upto,'dd-Mon-yyyy') as validUpto,\n"
                    + "vh_class.descr as vhclass,state.descr stateName,district.descr as p_dist,statep.descr as pstate  \n"
                    + "FROM vt_owner_temp tempOwner  \n"
                    + "left join VM_VH_CLASS vh_class on vh_class.vh_class=tempOwner.vh_class  \n"
                    + "left join TM_STATE state on state.state_code=tempOwner.state_cd  \n"
                    + "left join tm_district district on district.dist_cd=tempOwner.p_district and district.state_cd=tempOwner.p_state\n"
                    + "left join TM_STATE statep on statep.state_code=tempOwner.p_state  \n"
                    + "where tempOwner.temp_regn_no = ? and tempOwner.state_cd=? and tempOwner.off_cd=?";
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, text);
            pstm.setString(2, Util.getUserStateCode());
            pstm.setInt(3, Util.getSelectedSeat().getOff_cd());
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            if (rowSet.next()) {
                dobj = new TempRcCancelDobj();
                dobj.setAddress(rowSet.getString("p_add1") + "," + rowSet.getString("p_add2") + "," + rowSet.getString("p_add3") + "," + rowSet.getString("p_dist") + "," + rowSet.getString("p_pincode") + "," + rowSet.getString("pstate"));
                dobj.setChassisNo(rowSet.getString("chasi_no"));
                dobj.setOwnerName(rowSet.getString("owner_name"));
                dobj.setFatherName(rowSet.getString("f_name"));
                dobj.setPurchaseDt(ServerUtil.parseDateToString(rowSet.getDate("purchase_dt")));
                dobj.setValidFrom(rowSet.getString("validFrom"));
                dobj.setValidUpto(rowSet.getString("validUpto"));
                dobj.setEngineNo(rowSet.getString("eng_no"));
                dobj.setVhClass(rowSet.getString("vhclass"));
                dobj.setState(rowSet.getString("stateName"));
                dobj.setBodyType(rowSet.getString("body_type"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj;
    }

    public boolean cancelTempRc(String temp_regn_no, String reason, Status_dobj statusDobj) throws VahanException {
        boolean flag = false;
        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        RowSet rs = null;
        String sql = "";
        int i = 0;
        try {
            tmgr = new TransactionManager("cancel temporary RC");
            String applicationNo = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            sql = "INSERT INTO " + TableList.VA_TEMP_RC_CANCEL + "(\n"
                    + "          state_cd, off_cd, appl_no, temp_regn_no, reason, op_dt)\n"
                    + "    VALUES (?, ?, ?, ?, ?,current_timestamp)";
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(++i, Util.getUserStateCode());
            pstm.setInt(++i, Util.getSelectedSeat().getOff_cd());
            pstm.setString(++i, applicationNo);
            pstm.setString(++i, temp_regn_no);
            pstm.setString(++i, reason);
            pstm.executeUpdate();
            if (!applicationNo.equals("") && !(applicationNo == null)) {
                statusDobj.setAppl_no(applicationNo);
                ServerUtil.fileFlowForNewApplication(tmgr, statusDobj);
                statusDobj.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
                statusDobj.setStatus(TableConstants.STATUS_COMPLETE);
                statusDobj.setAppl_no(applicationNo);
                statusDobj.setOffice_remark("");
                statusDobj.setPublic_remark("");
                ServerUtil.webServiceForNextStage(statusDobj, TableConstants.FORWARD, null, applicationNo,
                        statusDobj.getAction_cd(), statusDobj.getPur_cd(), null, tmgr);
                ServerUtil.fileFlow(tmgr, statusDobj);
            }
            tmgr.commit();
            flag = true;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error : Temporary RC Cancellation");
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);

            }
        }
        return flag;
    }

    public TempRcCancelDobj getOwnerDetailsFoVerification(String regn_no) {
        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        TempRcCancelDobj dobj = null;
        String sql = "";
        try {
            dobj = getOwnerDetails(regn_no);
            tmgr = new TransactionManager("getOwnerDetails");
            sql = "SELECT * from  " + TableList.VA_TEMP_RC_CANCEL + " where temp_regn_no=? and state_cd=? and off_cd=?";
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, regn_no);
            pstm.setString(2, Util.getUserStateCode());
            pstm.setInt(3, Util.getSelectedSeat().getOff_cd());
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            if (rowSet.next()) {
                dobj.setCancelDate(rowSet.getDate("op_dt"));
                dobj.setReasonToCancel(rowSet.getString("reason"));
                dobj.setTempRegnNo(rowSet.getString("temp_regn_no"));
                dobj.setAppl_no(rowSet.getString("appl_no"));

            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj;

    }

    public void onlySaveNotVerifyApprove(String roleCd, Status_dobj statusDobj, String change, TempRcCancelDobj tempRcCancelDobj, TempRcCancelDobj prevTempRcCancelDobj, TransactionManager tmgr) throws Exception {
        PreparedStatement pstmt = null;
        String sql = "";
        try {
            if (!change.isEmpty()) {
                insertInVhaTempRcCancel(tmgr, tempRcCancelDobj);
                insertIntoVhaChangedData(tmgr, prevTempRcCancelDobj.getAppl_no(), change);
                sql = "UPDATE " + TableList.VA_TEMP_RC_CANCEL + "\n"
                        + "   SET  reason=? ,op_dt=current_timestamp\n"
                        + " WHERE temp_regn_no=? and state_cd=? and off_cd=?";
                pstmt = tmgr.prepareStatement(sql);
                pstmt.setString(1, tempRcCancelDobj.getReasonToCancel());
                pstmt.setString(2, tempRcCancelDobj.getTempRegnNo());
                pstmt.setString(3, Util.getUserStateCode());
                pstmt.setInt(4, Util.getSelectedSeat().getOff_cd());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

//    public void saveDataInApprovalCase(String roleCd, Status_dobj statusDobj, String change, TempRcCancelDobj tempRcCancelDobj, TempRcCancelDobj prevTempRcCancelDobj, TransactionManager tmgr) throws Exception {
//        try {
//          
//            MoveDataFromVtToVh(tempRcCancelDobj, tmgr);
//
//        } catch (SQLException e) {
//            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//        } catch (Exception e) {
//            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//        }
//
//    }
    public void reback(String roleCd, Status_dobj statusDobj, String change, TempRcCancelDobj tempRcCancelDobj, TempRcCancelDobj prevTempRcCancelDobj) {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("reback");
            if (!change.isEmpty()) {
                onlySaveNotVerifyApprove(roleCd, statusDobj, change, tempRcCancelDobj, prevTempRcCancelDobj, tmgr);
            }
            statusDobj = ServerUtil.webServiceForNextStage(statusDobj, tmgr);
            ServerUtil.fileFlow(tmgr, statusDobj);
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

    }

    public void movetoapprove(String roleCd, Status_dobj statusDobj, String change, TempRcCancelDobj tempRcCancelDobj, TempRcCancelDobj prevTempRcCancelDobj) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("movetoapprove");
            if (!change.isEmpty()) {
                onlySaveNotVerifyApprove(roleCd, statusDobj, change, tempRcCancelDobj, prevTempRcCancelDobj, tmgr);
            }
            if (Integer.parseInt(roleCd) == TableConstants.VM_ROLE_TEMP_RC_CANCEL_APPROVE) {
                MoveDataFromVtToVh(tempRcCancelDobj, tmgr);
                statusDobj.setEntry_status("A");
                ServerUtil.updateApprovedStatus(tmgr, statusDobj);
            }
            statusDobj = ServerUtil.webServiceForNextStage(statusDobj, tmgr);
            ServerUtil.fileFlow(tmgr, statusDobj);
            tmgr.commit();
        } catch (VahanException e) {
            throw e;
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

    }

    public void saveUpdatedDetails(String roleCd, Status_dobj statusDobj, String change, TempRcCancelDobj tempRcCancelDobj, TempRcCancelDobj prevTempRcCancelDobj) {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveUpdatedDetails");
            onlySaveNotVerifyApprove(roleCd, statusDobj, change, tempRcCancelDobj, prevTempRcCancelDobj, tmgr);
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
    }

    public void MoveDataFromVtToVh(TempRcCancelDobj dobj, TransactionManager tmgr) throws SQLException {
        PreparedStatement pstm = null;
        String sql = "";
        sql = "INSERT INTO " + TableList.VH_OWNER_TEMP + "(\n"
                + "            state_cd, off_cd, appl_no, temp_regn_no, valid_from, valid_upto, \n"
                + "            purchase_dt, owner_name, f_name, c_add1, c_add2, c_add3, c_district, \n"
                + "            c_pincode, c_state, p_add1, p_add2, p_add3, p_district, p_pincode, \n"
                + "            p_state, owner_cd, regn_type, vh_class, chasi_no, eng_no, maker, \n"
                + "            maker_model, body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, \n"
                + "            unld_wt, ld_wt, gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, \n"
                + "            cubic_cap, floor_area, ac_fitted, audio_fitted, video_fitted, \n"
                + "            vch_purchase_as, vch_catg, dealer_cd, sale_amt, laser_code, garage_add, \n"
                + "            length, width, height, regn_upto, fit_upto, annual_income, imported_vch, \n"
                + "            other_criteria, state_cd_to, off_cd_to, op_dt, moved_on, moved_by, \n"
                + "            reason, purpose, body_building)\n"
                + "SELECT state_cd, off_cd, appl_no, temp_regn_no, valid_from, valid_upto, \n"
                + "       purchase_dt, owner_name, f_name, c_add1, c_add2, c_add3, c_district, \n"
                + "       c_pincode, c_state, p_add1, p_add2, p_add3, p_district, p_pincode, \n"
                + "       p_state, owner_cd, regn_type, vh_class, chasi_no, eng_no, maker, \n"
                + "       maker_model, body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, \n"
                + "       unld_wt, ld_wt, gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, \n"
                + "       cubic_cap, floor_area, ac_fitted, audio_fitted, video_fitted, \n"
                + "       vch_purchase_as, vch_catg, dealer_cd, sale_amt, laser_code, garage_add, \n"
                + "       length, width, height, regn_upto, fit_upto, annual_income, imported_vch, \n"
                + "       other_criteria, state_cd_to, off_cd_to, op_dt,current_timestamp,?,?, purpose, body_building\n"
                + "  FROM " + TableList.VT_OWNER_TEMP + " where temp_regn_no=? and state_cd=? and off_cd=?";
        pstm = tmgr.prepareStatement(sql);
        pstm.setString(1, Util.getEmpCode());
        pstm.setString(2, dobj.getReasonToCancel());
        pstm.setString(3, dobj.getTempRegnNo());
        pstm.setString(4, Util.getUserStateCode());
        pstm.setInt(5, Util.getSelectedSeat().getOff_cd());
        pstm.executeUpdate();
        pstm = null;
        sql = " DELETE FROM " + TableList.VT_OWNER_TEMP
                + " WHERE temp_regn_no=? and state_cd=? and off_cd=?";
        pstm = tmgr.prepareStatement(sql);
        pstm.setString(1, dobj.getTempRegnNo());
        pstm.setString(2, Util.getUserStateCode());
        pstm.setInt(3, Util.getSelectedSeat().getOff_cd());
        pstm.executeUpdate();
        pstm = null;
        sql = "INSERT INTO " + TableList.VH_INSURANCE + "(\n"
                + "            regn_no, comp_cd, ins_type, ins_from, ins_upto, policy_no, moved_on, \n"
                + "            moved_by, state_cd, off_cd, idv)\n"
                + "SELECT  regn_no, comp_cd, ins_type, ins_from, ins_upto, \n"
                + "       policy_no,current_timestamp,?,state_cd, off_cd,idv\n"
                + "  FROM " + TableList.VT_INSURANCE + " where regn_no=? and state_cd=?";
        pstm = tmgr.prepareStatement(sql);
        pstm.setString(1, Util.getEmpCode());
        pstm.setString(2, dobj.getTempRegnNo());
        pstm.setString(3, Util.getUserStateCode());
        pstm.executeUpdate();
        pstm = null;
        sql = " DELETE FROM " + TableList.VT_INSURANCE
                + " WHERE regn_no=? and state_cd=?";
        pstm = tmgr.prepareStatement(sql);
        pstm.setString(1, dobj.getTempRegnNo());
        pstm.setString(2, Util.getUserStateCode());
        pstm.executeUpdate();
        pstm = null;
        sql = "INSERT INTO " + TableList.VH_HYPTH + "(\n"
                + "            state_cd, off_cd, regn_no, sr_no, hp_type, fncr_name, fncr_add1, \n"
                + "            fncr_add2, fncr_add3, fncr_district, fncr_pincode, fncr_state, \n"
                + "            from_dt, op_dt, appl_no, moved_on, moved_by)\n"
                + "   SELECT state_cd, off_cd, regn_no, sr_no, hp_type, fncr_name, fncr_add1, \n"
                + "       fncr_add2, fncr_add3, fncr_district, fncr_pincode, fncr_state, \n"
                + "       from_dt, op_dt,?,current_timestamp,?\n"
                + "  FROM " + TableList.VT_HYPTH + " where regn_no=? and state_cd=?";
        pstm = tmgr.prepareStatement(sql);
        pstm.setString(1, dobj.getAppl_no());
        pstm.setString(2, Util.getEmpCode());
        pstm.setString(3, dobj.getTempRegnNo());
        pstm.setString(4, Util.getUserStateCode());
        pstm.executeUpdate();
        pstm = null;
        sql = " DELETE FROM " + TableList.VT_HYPTH
                + " WHERE regn_no=? and state_cd=?";
        pstm = tmgr.prepareStatement(sql);
        pstm.setString(1, dobj.getTempRegnNo());
        pstm.setString(2, Util.getUserStateCode());
        pstm.executeUpdate();
        pstm = null;
        sql = "INSERT INTO " + TableList.VH_AXLE + "(\n"
                + "            state_cd, off_cd, regn_no, f_axle_descp, r_axle_descp, o_axle_descp, \n"
                + "            t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, t_axle_weight, \n"
                + "            appl_no, moved_on, moved_by)\n"
                + " \n"
                + "  SELECT state_cd, off_cd, regn_no, f_axle_descp, r_axle_descp, o_axle_descp, \n"
                + "       t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, t_axle_weight, \n"
                + "       ?,current_timestamp,?\n"
                + "  FROM " + TableList.VT_AXLE + " where regn_no=? and state_cd=? and off_cd=?";
        pstm = tmgr.prepareStatement(sql);
        pstm.setString(1, dobj.getAppl_no());
        pstm.setString(2, Util.getEmpCode());
        pstm.setString(3, dobj.getTempRegnNo());
        pstm.setString(4, Util.getUserStateCode());
        pstm.setInt(5, Util.getSelectedSeat().getOff_cd());
        pstm.executeUpdate();
        pstm = null;
        sql = " DELETE FROM " + TableList.VT_AXLE
                + " WHERE regn_no=? and state_cd=? and off_cd=?";
        pstm = tmgr.prepareStatement(sql);
        pstm.setString(1, dobj.getTempRegnNo());
        pstm.setString(2, Util.getUserStateCode());
        pstm.setInt(3, Util.getSelectedSeat().getOff_cd());
        pstm.executeUpdate();
        pstm = null;
        sql = "INSERT INTO " + TableList.VH_RETROFITTING_DTLS + "(\n"
                + "            state_cd, off_cd, regn_no, kit_srno, kit_type, kit_manuf, kit_pucc_norms, \n"
                + "            workshop, workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, \n"
                + "            approval_no, approval_dt, op_dt, appl_no, moved_on, moved_by)\n"
                + "SELECT state_cd, off_cd, regn_no, kit_srno, kit_type, kit_manuf, kit_pucc_norms, \n"
                + "       workshop, workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, \n"
                + "       approval_no, approval_dt, op_dt,?,current_timestamp,?\n"
                + "  FROM " + TableList.VT_RETROFITTING_DTLS + " WHERE regn_no=? and state_cd=? and off_cd=?";
        pstm = tmgr.prepareStatement(sql);
        pstm.setString(1, dobj.getAppl_no());
        pstm.setString(2, Util.getEmpCode());
        pstm.setString(3, dobj.getTempRegnNo());
        pstm.setString(4, Util.getUserStateCode());
        pstm.setInt(5, Util.getSelectedSeat().getOff_cd());
        pstm.executeUpdate();
        pstm = null;
        sql = " DELETE FROM " + TableList.VT_RETROFITTING_DTLS
                + " WHERE regn_no=? and state_cd=? and off_cd=?";
        pstm = tmgr.prepareStatement(sql);
        pstm.setString(1, dobj.getTempRegnNo());
        pstm.setString(2, Util.getUserStateCode());
        pstm.setInt(3, Util.getSelectedSeat().getOff_cd());
        pstm.executeUpdate();
        pstm = null;
        sql = "INSERT INTO " + TableList.VH_TRAILER + "(\n"
                + "            state_cd, off_cd, regn_no, chasi_no, body_type, ld_wt, unld_wt, \n"
                + "            f_axle_descp, r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight, \n"
                + "            r_axle_weight, o_axle_weight, t_axle_weight, appl_no, moved_on, \n"
                + "            moved_by)\n"
                + "SELECT state_cd, off_cd,regn_no, chasi_no, body_type, ld_wt, unld_wt, f_axle_descp, r_axle_descp, \n"
                + "       o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, \n"
                + "       t_axle_weight,?,current_timestamp,?\n"
                + "  FROM " + TableList.VT_TRAILER + " where regn_no=? and state_cd=? and off_cd=?";
        pstm = tmgr.prepareStatement(sql);
        pstm.setString(1, dobj.getAppl_no());
        pstm.setString(2, Util.getEmpCode());
        pstm.setString(3, dobj.getTempRegnNo());
        pstm.setString(4, Util.getUserStateCode());
        pstm.setInt(5, Util.getSelectedSeat().getOff_cd());
        pstm.executeUpdate();
        pstm = null;
        sql = " DELETE FROM " + TableList.VT_TRAILER
                + " WHERE regn_no=? and state_cd=? and off_cd=?";
        pstm = tmgr.prepareStatement(sql);
        pstm.setString(1, dobj.getTempRegnNo());
        pstm.setString(2, Util.getUserStateCode());
        pstm.setInt(3, Util.getSelectedSeat().getOff_cd());
        pstm.executeUpdate();
        pstm = null;
        sql = "INSERT INTO " + TableList.VH_OWNER_IDENTIFICATION + "(\n"
                + "            state_cd, off_cd, regn_no, mobile_no, email_id, pan_no, aadhar_no, \n"
                + "            passport_no, ration_card_no, voter_id, dl_no, verified_on, moved_on, \n"
                + "            moved_by, owner_ctg)\n"
                + "            SELECT state_cd, off_cd, regn_no, mobile_no, email_id, pan_no, aadhar_no, \n"
                + "       passport_no, ration_card_no, voter_id, dl_no, verified_on,current_timestamp,? ,owner_ctg\n"
                + "  FROM " + TableList.VT_OWNER_IDENTIFICATION + " where regn_no=? and state_cd=?";
        pstm = tmgr.prepareStatement(sql);
        pstm.setString(1, Util.getEmpCode());
        pstm.setString(2, dobj.getTempRegnNo());
        pstm.setString(3, Util.getUserStateCode());
        pstm.executeUpdate();
        pstm = null;
        sql = " DELETE FROM " + TableList.VT_OWNER_IDENTIFICATION
                + " WHERE regn_no=? and state_cd=?";
        pstm = tmgr.prepareStatement(sql);
        pstm.setString(1, dobj.getTempRegnNo());
        pstm.setString(2, Util.getUserStateCode());
        pstm.executeUpdate();
        pstm = null;
        sql = " DELETE FROM  " + TableList.VA_TEMP_RC_CANCEL + " WHERE temp_regn_no=? and state_cd=? and off_cd=?";
        pstm = tmgr.prepareStatement(sql);
        pstm.setString(1, dobj.getTempRegnNo());
        pstm.setString(2, Util.getUserStateCode());
        pstm.setInt(3, Util.getSelectedSeat().getOff_cd());
        pstm.executeUpdate();
    }

    public void insertInVhaTempRcCancel(TransactionManager tmgr, TempRcCancelDobj dobj) {
        PreparedStatement ps = null;
        String sql = "INSERT INTO " + TableList.VHA_TEMP_RC_CANCEL + "(\n"
                + "           moved_on, moved_by,state_cd,off_cd,  appl_no, temp_regn_no,  reason, op_dt ) \n"
                + "SELECT current_timestamp as moved_on, ? as moved_by, state_cd, off_cd, appl_no, temp_regn_no, reason, op_dt\n"
                + "  FROM " + TableList.VA_TEMP_RC_CANCEL + " where temp_regn_no=? and state_cd=? and off_cd=?";
        try {
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, dobj.getTempRegnNo());
            ps.setString(3, Util.getUserStateCode());
            ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public boolean chekIfAlreadyExist(String tempRegnNo) {
        boolean exist = true;
        PreparedStatement pstmt = null;
        TransactionManager tmgr = null;
        try {
            String sql = " SELECT * FROM " + TableList.VA_TEMP_RC_CANCEL + " WHERE temp_regn_no=?";
            tmgr = new TransactionManager("chekIfAlreadyExist");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, tempRegnNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                exist = false;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return exist;
    }

    public void insertInVhaTempRcCanApplNo(TransactionManager tmgr, String applNo) {
        PreparedStatement ps = null;
        String sql = "INSERT INTO " + TableList.VHA_TEMP_RC_CANCEL + "(\n"
                + "           moved_on, moved_by,state_cd,off_cd,  appl_no, temp_regn_no,  reason, op_dt ) \n"
                + "SELECT current_timestamp as moved_on, ? as moved_by, state_cd, off_cd, appl_no, temp_regn_no, reason, op_dt\n"
                + "  FROM " + TableList.VA_TEMP_RC_CANCEL + " where appl_no=? and state_cd=? and off_cd=?";
        try {
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, applNo);
            ps.setString(3, Util.getUserStateCode());
            ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }
}