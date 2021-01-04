/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.AltDobj;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.InspectionDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.Trailer_dobj;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

public class AltImpl {

    private static final Logger LOGGER = Logger.getLogger(AltImpl.class);

    public AltDobj set_ALT_appl_db_to_dobj(String appl_no) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        AltDobj alt_dobj = null;
        VahanException vahanexecption = null;
        try {
            tmgr = new TransactionManager("ALT_Impl");
            ps = tmgr.prepareStatement("SELECT vh_class, chasi_no, eng_no,"
                    + " body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt,"
                    + " ld_wt, fuel, color, wheelbase, cubic_cap, floor_area, ac_fitted,"
                    + " audio_fitted, video_fitted, vch_catg, length, width, height,"
                    + " fit_upto"
                    + " FROM " + TableList.VA_ALT + " where appl_no=?");
            ps.setString(1, appl_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                alt_dobj = new AltDobj();
                alt_dobj.setVh_class(rs.getInt("vh_class"));
                alt_dobj.setChasi_no(rs.getString("chasi_no"));
                alt_dobj.setEng_no(rs.getString("eng_no"));
                alt_dobj.setBody_type(rs.getString("body_type"));
                alt_dobj.setNo_cyl(rs.getInt("no_cyl"));
                alt_dobj.setHp(rs.getFloat("hp"));
                alt_dobj.setSeat_cap(rs.getInt("seat_cap"));
                alt_dobj.setStand_cap(rs.getInt("stand_cap"));
                alt_dobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                alt_dobj.setUnld_wt(rs.getInt("unld_wt"));
                alt_dobj.setLd_wt(rs.getInt("ld_wt"));
                alt_dobj.setFuel(rs.getInt("fuel"));
                alt_dobj.setColor(rs.getString("color"));
                alt_dobj.setWheelbase(rs.getInt("wheelbase"));
                alt_dobj.setCubic_cap(rs.getFloat("cubic_cap"));
                alt_dobj.setFloor_area(rs.getFloat("floor_area"));
                alt_dobj.setAc_fitted(rs.getString("ac_fitted"));
                alt_dobj.setAudio_fitted(rs.getString("audio_fitted"));
                alt_dobj.setVideo_fitted(rs.getString("video_fitted"));
                alt_dobj.setVch_catg(rs.getString("vch_catg"));
                alt_dobj.setLength(rs.getInt("length"));
                alt_dobj.setWidth(rs.getInt("width"));
                alt_dobj.setHeight(rs.getInt("height"));
                alt_dobj.setFit_date_upto(rs.getDate("fit_upto"));
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanexecption = new VahanException("Database Error in fetching details for [ " + appl_no + "]");
            throw vahanexecption;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanexecption = new VahanException("Error in fetching details for [ " + appl_no + "]");
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
        return alt_dobj;

    }

    public String set_ALT_appl_db_to_dobj_vaOwnerOther(String appl_no) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String val = null;
        VahanException vahanexecption = null;
        try {
            tmgr = new TransactionManager("ALT_Impl_vaother");
            ps = tmgr.prepareStatement("select push_back_seat||','||ordinary_seat as val from va_owner_other "
                    + "where appl_no=?");
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                val = rs.getString("val");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanexecption = new VahanException("Database Error in fetching details for [ " + appl_no + "]");
            throw vahanexecption;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanexecption = new VahanException("Error in fetching details for [ " + appl_no + "]");
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
        return val;

    }
    ///////////////////////

//    Showing initial details from vt_owner
    public AltDobj set_ALT_appl_db_to_dobj_from_VT_OWNER(String regn_no, SessionVariables sessionVar) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        AltDobj alt_dobj = null;
        VahanException vahanexecption = null;
        try {
            tmgr = new TransactionManager("ALT_Impl");
            ps = tmgr.prepareStatement("SELECT vtown.vh_class, vtown.chasi_no, vtown.eng_no,vtowner.push_back_seat,vtowner.ordinary_seat,"
                    + "       vtown.body_type, vtown.no_cyl,vtown. hp, vtown.seat_cap, vtown.stand_cap, vtown.sleeper_cap, vtown.unld_wt,"
                    + "       vtown.ld_wt,vtown.fuel, vtown.color, vtown.wheelbase, vtown.cubic_cap,vtown.floor_area, vtown.ac_fitted,"
                    + "       vtown.audio_fitted, vtown.video_fitted, vtown.vch_catg, vtown.length,vtown.width, vtown.height,"
                    + "       vtown.fit_upto "
                    + "FROM " + TableList.VT_OWNER + "  vtown "
                    + " left join " + TableList.VT_OWNER_OTHER + "  vtowner on vtown.state_cd = vtowner.state_cd and  vtown.off_cd = vtowner.off_cd and vtown.regn_no = vtowner.regn_no "
                    + "where  vtown.regn_no=? and vtown.state_cd=? and  vtown.off_cd =? ");
            ps.setString(1, regn_no);
            ps.setString(2, sessionVar.getStateCodeSelected());
            ps.setInt(3, sessionVar.getOffCodeSelected());


            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                alt_dobj = new AltDobj();
                alt_dobj.setVh_class(rs.getInt("vh_class"));
                alt_dobj.setChasi_no(rs.getString("chasi_no"));
                alt_dobj.setEng_no(rs.getString("eng_no"));
                alt_dobj.setBody_type(rs.getString("body_type"));
                alt_dobj.setNo_cyl(rs.getInt("no_cyl"));
                alt_dobj.setHp(rs.getFloat("hp"));
                alt_dobj.setSeat_cap(rs.getInt("seat_cap"));
                alt_dobj.setStand_cap(rs.getInt("stand_cap"));
                alt_dobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                alt_dobj.setUnld_wt(rs.getInt("unld_wt"));
                alt_dobj.setLd_wt(rs.getInt("ld_wt"));
                alt_dobj.setFuel(rs.getInt("fuel"));
                alt_dobj.setColor(rs.getString("color"));
                alt_dobj.setWheelbase(rs.getInt("wheelbase"));
                alt_dobj.setCubic_cap(rs.getFloat("cubic_cap"));
                alt_dobj.setFloor_area(rs.getFloat("floor_area"));
                alt_dobj.setAc_fitted(rs.getString("ac_fitted"));
                alt_dobj.setAudio_fitted(rs.getString("audio_fitted"));
                alt_dobj.setVideo_fitted(rs.getString("video_fitted"));
                alt_dobj.setVch_catg(rs.getString("vch_catg"));
                alt_dobj.setLength(rs.getInt("length"));
                alt_dobj.setWidth(rs.getInt("width"));
                alt_dobj.setHeight(rs.getInt("height"));
                alt_dobj.setFit_date_upto(rs.getDate("fit_upto"));
                alt_dobj.setPush_bk_seat(rs.getInt("push_back_seat"));
                alt_dobj.setOrdinary_seat(rs.getInt("ordinary_seat"));

            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanexecption = new VahanException("Database Error in fetching details for [ " + regn_no + "]");
            throw vahanexecption;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanexecption = new VahanException("Error in fetching details for [ " + regn_no + "]");
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
        return alt_dobj;

    }

    public void makeChange_alteration(AltDobj alt_dobj, RetroFittingDetailsDobj cng_dobj,
            List<Trailer_dobj> listTrailerDetails, Owner_dobj ownerDobj,
            List<Trailer_dobj> listTrailerDetach, String changedata, String changedata1, boolean renderSpeedGovt, String state_cd, String emp_cd, InspectionDobj inspDobj, AxleDetailsDobj axleDobj) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChange_alteration");
            insertOrUpdateALT(tmgr, alt_dobj.getAppl_no(), alt_dobj.getRegn_no(), alt_dobj);
            if (state_cd.equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(alt_dobj.getVh_class()) + ",")) {
                insertOrUpdateVaOwnerOther(tmgr, alt_dobj.getAppl_no(), alt_dobj.getRegn_no(), alt_dobj, emp_cd, state_cd);
            }
            if (listTrailerDetails != null && !listTrailerDetails.isEmpty()) {
                insertOrUpdateTrailer(tmgr, alt_dobj.getAppl_no(), alt_dobj.getRegn_no(), listTrailerDetails);
            }
            if (listTrailerDetach != null && !listTrailerDetach.isEmpty()) {
                insertOrUpdateTrailerDetach(tmgr, alt_dobj.getAppl_no(), alt_dobj.getRegn_no(), listTrailerDetach);
            }
            if (renderSpeedGovt) {
                FitnessImpl.insertUpdateVaSpeedGovernor(ownerDobj.getSpeedGovernorDobj(), tmgr);
            }
            if (ownerDobj.getReflectiveTapeDobj() != null) {
                new FitnessImpl().insertOrUpdateVaReflectiveTape(tmgr, ownerDobj.getReflectiveTapeDobj());
            }

            if (!changedata1.isEmpty() || cng_dobj != null) {
                RetroFittingDetailsImpl.saveCngVehicleDetails_Impl(cng_dobj, alt_dobj.getAppl_no(), tmgr);
            }
            if (axleDobj != null) {
                AxleImpl.saveAxleDetails_Impl(axleDobj, alt_dobj.getAppl_no(), tmgr);
            }
            if (inspDobj != null && inspDobj.getInsp_dt() != null
                    && Util.getSelectedSeat().getAction_cd() == TableConstants.INSPECTION_FOR_ALT) {
                FitnessImpl fitnessImpl = new FitnessImpl();
                fitnessImpl.insertOrUpdateInspection(tmgr, inspDobj);
            }
            changedata = changedata + changedata1;
            ComparisonBeanImpl.updateChangedData(alt_dobj.getAppl_no(), changedata, tmgr);
            tmgr.commit();
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
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

    public void update_ALT_Status(AltDobj alt_dobj, RetroFittingDetailsDobj cng_dobj,
            List<Trailer_dobj> listTrailerDetails, List<Trailer_dobj> listTrailerDetach,
            Status_dobj status_dobj, String changedData, String changedCngData,
            Owner_dobj ownerDobj, boolean renderSpeedGovt, String state_cd, String emp_cd, AxleDetailsDobj axleDobj, InspectionDobj inspDobj, InspectionDobj inspDobjPrev) throws VahanException, Exception {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("update_ALT_Status");
            //=================WEB SERVICES FOR NEXTSTAGE START=========//
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            status_dobj.setRegn_no(alt_dobj.getRegn_no());
            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_ENTRY) {
                insertOrUpdateALT(tmgr, alt_dobj.getAppl_no(), alt_dobj.getRegn_no(), alt_dobj);

                if (state_cd.equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(alt_dobj.getVh_class()) + ",")) {
                    insertOrUpdateVaOwnerOther(tmgr, alt_dobj.getAppl_no(), alt_dobj.getRegn_no(), alt_dobj, emp_cd, state_cd);
                }
                if (listTrailerDetails != null && !listTrailerDetails.isEmpty()) {
                    insertOrUpdateTrailer(tmgr, alt_dobj.getAppl_no(), alt_dobj.getRegn_no(), listTrailerDetails);
                }
                if (listTrailerDetach != null && !listTrailerDetach.isEmpty()) {
                    insertOrUpdateTrailerDetach(tmgr, alt_dobj.getAppl_no(), alt_dobj.getRegn_no(), listTrailerDetach);
                }
                if (cng_dobj != null) {
                    RetroFittingDetailsImpl.saveCngVehicleDetails_Impl(cng_dobj, alt_dobj.getAppl_no(), tmgr);
                }
                if (axleDobj != null) {
                    AxleImpl.saveAxleDetails_Impl(axleDobj, alt_dobj.getAppl_no(), tmgr);
                }
            }
            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_ENTRY
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL
                    || Util.getSelectedSeat().getAction_cd() == TableConstants.INSPECTION_FOR_ALT) {

                if (renderSpeedGovt && (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_ENTRY
                        || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION)) {
                    FitnessImpl.insertUpdateVaSpeedGovernor(ownerDobj.getSpeedGovernorDobj(), tmgr);
                }
                if (ownerDobj.getReflectiveTapeDobj() != null && (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_ENTRY
                        || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION)) {
                    new FitnessImpl().insertOrUpdateVaReflectiveTape(tmgr, ownerDobj.getReflectiveTapeDobj());
                }

                if ((alt_dobj.isOldVahanRecords() || !changedData.isEmpty()) && (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION
                        || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL)) {
                    insertOrUpdateALT(tmgr, alt_dobj.getAppl_no(), alt_dobj.getRegn_no(), alt_dobj);
                    if (state_cd.equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(alt_dobj.getVh_class()) + ",")) {
                        insertOrUpdateVaOwnerOther(tmgr, alt_dobj.getAppl_no(), alt_dobj.getRegn_no(), alt_dobj, emp_cd, state_cd);
                    }
                    if (listTrailerDetails != null && !listTrailerDetails.isEmpty()) {
                        insertOrUpdateTrailer(tmgr, alt_dobj.getAppl_no(), alt_dobj.getRegn_no(), listTrailerDetails);
                    }

                }
                if ((status_dobj.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION
                        || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL) && cng_dobj != null) {
                    RetroFittingDetailsImpl.saveCngVehicleDetails_Impl(cng_dobj, alt_dobj.getAppl_no(), tmgr);
                }
                if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION && axleDobj != null) {
                    AxleImpl.saveAxleDetails_Impl(axleDobj, alt_dobj.getAppl_no(), tmgr);
                }
                if (inspDobj != null
                        && inspDobj.getInsp_dt() != null
                        && Util.getSelectedSeat().getAction_cd() == TableConstants.INSPECTION_FOR_ALT) {
                    FitnessImpl fitnessImpl = new FitnessImpl();
                    fitnessImpl.insertOrUpdateInspection(tmgr, inspDobj);
                }
            }
            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {


                if (renderSpeedGovt) {
                    FitnessImpl.moveFromVtSpeedGovToVhSpeedGovTo(ownerDobj.getSpeedGovernorDobj().getAppl_no(), ownerDobj.getSpeedGovernorDobj().getRegn_no(),
                            ownerDobj.getSpeedGovernorDobj().getState_cd(), ownerDobj.getSpeedGovernorDobj().getOff_cd(), tmgr);
                    FitnessImpl.moveFromVaSpeedGovToVtSpeedGov(ownerDobj.getSpeedGovernorDobj().getAppl_no(), tmgr);
                    ServerUtil.deleteFromTable(tmgr, null, ownerDobj.getSpeedGovernorDobj().getAppl_no(), TableList.VA_SPEED_GOVERNOR);
                }

                if (ownerDobj.getReflectiveTapeDobj() != null) {
                    new FitnessImpl().moveFromVtReflectiveTapeToVhReflectiveTape(ownerDobj.getReflectiveTapeDobj().getRegn_no(), ownerDobj.getState_cd(), tmgr);
                    new FitnessImpl().moveFromVaReflectiveTapeToVtReflectiveTape(ownerDobj.getReflectiveTapeDobj().getApplNo(), ownerDobj.getState_cd(), ownerDobj.getOff_cd(), tmgr);
                    new FitnessImpl().deleteVaReflectiveTape(ownerDobj.getReflectiveTapeDobj().getApplNo(), tmgr);
                }
                //for inspection
                if (inspDobj != null
                        && inspDobj.getInsp_dt() != null) {
                    FitnessImpl fitImpl = new FitnessImpl();
                    fitImpl.insertOrUpdateInspection(tmgr, inspDobj);
                    fitImpl.insertVtToVhInspection(tmgr, inspDobj, Util.getEmpCode());
                    fitImpl.insertVtInspection(tmgr, inspDobj);
                    ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_INSPECTION);
                }
                //Save record from va_retrofiiting_detail to vt_retrofitting_details
                if (cng_dobj != null) {
                    //Insert into vh_retrofitting_details from vt_retrofitting_details
                    RetroFittingDetailsImpl.insertIntoRetroFitVH(tmgr, status_dobj, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), null);
                    //Delete from vt_retrofitting _details
                    RetroFittingDetailsImpl.deleteFromVtRetroFit(tmgr, alt_dobj.getRegn_no().trim(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                    //Insert into vt_retrofitting_detials from va_retrofitting_details
                    sql = "INSERT INTO " + TableList.VT_RETROFITTING_DTLS + " (\n"
                            + "           state_cd , off_cd , regn_no, kit_srno, kit_type, kit_manuf, kit_pucc_norms, \n"
                            + "            workshop, workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, \n"
                            + "            approval_no, approval_dt, op_dt)\n"
                            + "SELECT ?,?,?, kit_srno, kit_type, kit_manuf, kit_pucc_norms, workshop, \n"
                            + "       workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, approval_no, \n"
                            + "       approval_dt,current_timestamp\n"
                            + "  FROM " + TableList.VA_RETROFITTING_DTLS + " where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                    ps.setString(3, alt_dobj.getRegn_no());
                    ps.setString(4, alt_dobj.getAppl_no());
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                    //Insert into vha_retrofitting_detail from va_retrofitting_detail
                    sql = "INSERT INTO " + TableList.VHA_RETROFITTING_DTLS
                            + " SELECT  current_timestamp + interval '1 second' as moved_on, ? as moved_by, state_cd, off_cd,appl_no,kit_srno, kit_type, kit_manuf, kit_pucc_norms, workshop, \n"
                            + "       workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, approval_no, \n"
                            + "       approval_dt,op_dt\n"
                            + "  FROM " + TableList.VA_RETROFITTING_DTLS + " where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, alt_dobj.getAppl_no());
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                    //Delete from va_retrofitting_detail
                    sql = "DELETE FROM " + TableList.VA_RETROFITTING_DTLS + " WHERE appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, alt_dobj.getAppl_no());
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                }

                if ((listTrailerDetails != null && !listTrailerDetails.isEmpty())
                        || (listTrailerDetach != null && !listTrailerDetach.isEmpty())) {
                    approvalOfTrailer(tmgr, alt_dobj.getAppl_no(), alt_dobj.getRegn_no(), listTrailerDetails, listTrailerDetach);
                }

                //Insurance Approval
                InsImpl.approvalInsurance(tmgr, alt_dobj.getRegn_no(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), Util.getEmpCode());

                //// move record from vt_owner to vh_alt - start
                String vh_alt = "INSERT INTO " + TableList.VH_ALT + " "
                        + " Select ? as state_cd, ? as off_cd, ? as appl_no, regn_no, vh_class, chasi_no, eng_no,"
                        + " body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt,"
                        + " ld_wt, gcw, fuel, color, wheelbase, cubic_cap, floor_area, ac_fitted,"
                        + " audio_fitted, video_fitted, vch_catg, length, width, height,"
                        + " fit_upto, op_dt,current_timestamp as moved_on,? as moved_by"
                        + " From " + TableList.VT_OWNER
                        + "  where regn_no=? and state_cd = ? and off_cd  = ?";
                ps = tmgr.prepareStatement(vh_alt);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                ps.setString(3, alt_dobj.getAppl_no());
                ps.setString(4, Util.getEmpCode());
                ps.setString(5, alt_dobj.getRegn_no());
                ps.setString(6, Util.getUserStateCode());
                ps.setInt(7, Util.getSelectedSeat().getOff_cd());

                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                //// move record from vt_owner to vh_alt - end
                //Approval of Axle Details
                if (axleDobj != null) {
                    //Insert into vh_axle from vt_axle
                    AxleImpl.insertIntoAxleVH(tmgr, status_dobj, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), alt_dobj.getRegn_no());
                    //Delete from vt_axle
                    AxleImpl.deleteFromVtAxle(tmgr, alt_dobj.getRegn_no().trim(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                    //Insert into vt_axle from va_axle
                    AxleImpl.insertIntoVtFromVaAxle(tmgr, status_dobj);
                    //Insert into vha_axle from va_axle
                    AxleImpl.insertIntoVhaAxle(tmgr, alt_dobj.getAppl_no());
                    //Delete from va_axle
                    AxleImpl.deleteFromVaAxle(tmgr, alt_dobj.getAppl_no());
                }
                //////////// //updation of vt_ownwer/////////////////////////////////////
                sql = " update  " + TableList.VT_OWNER
                        + " ownr set chasi_no= vaalt.chasi_no, eng_no= vaalt.eng_no, ld_wt= vaalt.ld_wt, unld_wt= vaalt.unld_wt,"
                        + " no_cyl= vaalt.no_cyl, hp= vaalt.hp, seat_cap= vaalt.seat_cap, stand_cap= vaalt.stand_cap, sleeper_cap= vaalt.sleeper_cap, "
                        + " cubic_cap= vaalt.cubic_cap, wheelbase= vaalt.wheelbase,floor_area= vaalt.floor_area, "
                        + " fuel= vaalt.fuel, body_type= vaalt.body_type, color= vaalt.color, "
                        + " ac_fitted=vaalt.ac_fitted,audio_fitted=vaalt.audio_fitted,video_fitted=vaalt.video_fitted,vch_catg=vaalt.vch_catg,"
                        + " length=vaalt.length,width=vaalt.width,height=vaalt.height"
                        + " from va_alt vaalt where ownr.regn_no=vaalt.regn_no and ownr.state_cd=vaalt.state_cd and ownr.off_cd=vaalt.off_cd and vaalt.appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, alt_dobj.getAppl_no());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                //Insert into vha_alt from va_alt
                sql = "INSERT INTO " + TableList.VHA_ALT + " "
                        + "(state_cd, off_cd, appl_no, regn_no, vh_class, chasi_no, eng_no,"
                        + "       body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt,"
                        + "       ld_wt, fuel, color, wheelbase, cubic_cap, floor_area, ac_fitted,"
                        + "       audio_fitted, video_fitted, vch_catg, length, width, height,"
                        + "       fit_upto, op_dt,"
                        + " moved_on,moved_by)"
                        + "SELECT state_cd, off_cd, appl_no, regn_no, vh_class, chasi_no, eng_no,"
                        + "       body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt,"
                        + "       ld_wt, fuel, color, wheelbase, cubic_cap, floor_area, ac_fitted,"
                        + "       audio_fitted, video_fitted, vch_catg, length, width, height,"
                        + "       fit_upto, op_dt,"
                        + " current_timestamp + interval '1 second' as moved_on,? as moved_by FROM " + TableList.VA_ALT + " where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, alt_dobj.getAppl_no());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                //Delete from va_alt
                sql = "DELETE FROM " + TableList.VA_ALT + " where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, alt_dobj.getAppl_no());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                //data insert for fastag schedula
                if (alt_dobj != null || cng_dobj != null) {
                    OwnerImpl.insertUpdateFastagSchedular(alt_dobj.getAppl_no(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), alt_dobj.getRegn_no(),
                            Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), alt_dobj.getRegn_no(), ownerDobj.getChasi_no(), tmgr);
                }
                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                //for updating the status of application when it is approved end

                //SmartCard Or Print
                ServerUtil.VerifyInsertSmartCardPrintDetail(alt_dobj.getAppl_no(), alt_dobj.getRegn_no(),
                        Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), status_dobj.getPur_cd(), tmgr);
                /////////////////// ALT PUSHBACK TARUN
                if (state_cd.equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(alt_dobj.getVh_class()) + ",")) {
                    ownerOtherApproval(tmgr, alt_dobj.getAppl_no(), alt_dobj.getRegn_no(), alt_dobj, emp_cd);
                }
            }
            changedData = changedData + changedCngData;
            insertIntoVhaChangedData(tmgr, alt_dobj.getAppl_no(), changedData);//for saving the data into table those are changed by the user
            ServerUtil.fileFlow(tmgr, status_dobj); //for updateing va_status and vha status for new role,seat for new emp
            tmgr.commit();//Commiting data here....
        } catch (VahanException vme) {
            throw vme;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in processing application [ " + alt_dobj.getAppl_no() + " ]");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in processing application [ " + alt_dobj.getAppl_no() + " ]");
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

    private void insertIntoALT(TransactionManager tmgr, AltDobj dobj) throws SQLException {
        PreparedStatement ps = null;

        String query = "INSERT INTO " + TableList.VA_ALT + " (\n"
                + " state_cd, off_cd, appl_no, regn_no, vh_class, chasi_no, eng_no, \n"
                + " body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, \n"
                + " ld_wt, fuel, color, wheelbase, cubic_cap, floor_area, ac_fitted, \n"
                + " audio_fitted, video_fitted, vch_catg, length, width, height, \n"
                + " fit_upto, op_dt)\n"
                + " VALUES (?, ?, ?, ?, ?, ?, ?,"
                + "         ?, ?, ?, ?, ?, ?, ?,"
                + "         ?, ?, ?, ?, ?, ?, ?,"
                + "         ?, ?, ?, ?, ?, ?,"
                + "         ?, ?)";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, Util.getUserStateCode());
        ps.setInt(2, Util.getSelectedSeat().getOff_cd());
        ps.setString(3, dobj.getAppl_no());
        ps.setString(4, dobj.getRegn_no());
        ps.setInt(5, dobj.getVh_class());
        ps.setString(6, dobj.getChasi_no());
        ps.setString(7, dobj.getEng_no());
        ps.setString(8, dobj.getBody_type());
        ps.setInt(9, dobj.getNo_cyl());
        ps.setFloat(10, dobj.getHp());
        ps.setInt(11, dobj.getSeat_cap());
        ps.setInt(12, dobj.getStand_cap());
        ps.setInt(13, dobj.getSleeper_cap());
        ps.setInt(14, dobj.getUnld_wt());
        ps.setInt(15, dobj.getLd_wt());
        ps.setInt(16, dobj.getFuel());
        ps.setString(17, dobj.getColor());
        ps.setInt(18, dobj.getWheelbase());
        ps.setFloat(19, dobj.getCubic_cap());
        ps.setFloat(20, dobj.getFloor_area());
        ps.setString(21, dobj.getAc_fitted());
        ps.setString(22, dobj.getAudio_fitted());
        ps.setString(23, dobj.getVideo_fitted());
        ps.setString(24, dobj.getVch_catg());
        ps.setInt(25, dobj.getLength());
        ps.setInt(26, dobj.getWidth());
        ps.setInt(27, dobj.getHeight());
        if (dobj.getFit_date_upto() != null) {
            ps.setDate(28, new java.sql.Date(dobj.getFit_date_upto().getTime()));
        } else {
            ps.setNull(28, java.sql.Types.NULL);
        }
        ps.setTimestamp(29, ServerUtil.getSystemDateInPostgres());

        ps.executeUpdate();

    }

    public void insertOrUpdateALT(TransactionManager tmgr, String appl_no, String regn_no, AltDobj dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "SELECT appl_no FROM " + TableList.VA_ALT + " where appl_no = ? and state_cd =?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoALTHistory(tmgr, appl_no);
                updateALT(tmgr, appl_no, regn_no, dobj);
            } else {
                insertIntoALT(tmgr, dobj);
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Inserting/Updating Alteration Data.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Inserting/Updating Alteration Data.");
        }
    }

    public static void insertIntoALTHistory(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "INSERT INTO " + TableList.VHA_ALT + " "
                + "(state_cd, off_cd, appl_no, regn_no, vh_class, chasi_no, eng_no,"
                + "       body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt,"
                + "       ld_wt, fuel, color, wheelbase, cubic_cap, floor_area, ac_fitted,"
                + "       audio_fitted, video_fitted, vch_catg, length, width, height,"
                + "       fit_upto, op_dt,"
                + " moved_on,moved_by)"
                + "SELECT state_cd, off_cd, appl_no, regn_no, vh_class, chasi_no, eng_no,"
                + "       body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt,"
                + "       ld_wt, fuel, color, wheelbase, cubic_cap, floor_area, ac_fitted,"
                + "       audio_fitted, video_fitted, vch_catg, length, width, height,"
                + "       fit_upto, op_dt,"
                + " current_timestamp as moved_on,? as moved_by FROM " + TableList.VA_ALT + " where appl_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();

    }

    private void updateALT(TransactionManager tmgr, String appl_no, String regn_no, AltDobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String query = "UPDATE " + TableList.VA_ALT + " "
                + "   SET chasi_no=?, eng_no=?, body_type=?, no_cyl=?, hp=?, seat_cap=?, stand_cap=?,"
                + "       sleeper_cap=?, unld_wt=?, ld_wt=?, fuel=?, color=?, wheelbase=?,"
                + "       cubic_cap=?, floor_area=?, ac_fitted=?, audio_fitted=?, video_fitted=?,"
                + "       length=?, width=?, height=?, fit_upto=?, op_dt = current_timestamp"
                + " WHERE appl_no=?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, dobj.getChasi_no());
        ps.setString(2, dobj.getEng_no());
        ps.setString(3, dobj.getBody_type());
        ps.setInt(4, dobj.getNo_cyl());
        ps.setFloat(5, dobj.getHp());
        ps.setInt(6, dobj.getSeat_cap());
        ps.setInt(7, dobj.getStand_cap());
        ps.setInt(8, dobj.getSleeper_cap());
        ps.setInt(9, dobj.getUnld_wt());
        ps.setInt(10, dobj.getLd_wt());
        ps.setInt(11, dobj.getFuel());
        ps.setString(12, dobj.getColor());
        ps.setInt(13, dobj.getWheelbase());
        ps.setFloat(14, dobj.getCubic_cap());
        ps.setFloat(15, dobj.getFloor_area());
        ps.setString(16, dobj.getAc_fitted());
        ps.setString(17, dobj.getAudio_fitted());
        ps.setString(18, dobj.getVideo_fitted());
        ps.setInt(19, dobj.getLength());
        ps.setInt(20, dobj.getWidth());
        ps.setInt(21, dobj.getHeight());
        if (dobj.getFit_date_upto() != null) {
            ps.setDate(22, new java.sql.Date(dobj.getFit_date_upto().getTime()));
        } else {
            ps.setNull(22, java.sql.Types.NULL);
        }
        ps.setString(23, dobj.getAppl_no());
        ps.executeUpdate();

    }

    public String[] present_technicalDetail(String regn_no) {
        String[] current_technical_detail = null;

        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("present_c_addressDetails");
            String sql = "SELECT ' Kit SrNo ['|| kit_srno ||'], Kit Type ['|| kit_type ||'], Kit Manufacturer ['|| kit_manuf ||'], Kit Pucc Norm ['|| kit_pucc_norms ||'], Workshop ['|| workshop||'], Workshop License No ['|| workshop_lic_no ||'], Fitment Dt ['|| fitment_dt ||'], Hydro Dt ['|| hydro_test_dt ||'], Cylinder SrNo ['|| cyl_srno ||'], Approval No ['|| approval_no ||'], Approval Dt ['|| approval_dt ||']' \n"
                    + "as current_technical_detail\n"
                    + "from " + TableList.VT_RETROFITTING_DTLS + " \n"
                    + "where regn_no=? and state_cd  =? and off_cd = ? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                current_technical_detail = new String[1];
                current_technical_detail[0] = rs.getString("current_technical_detail").replace(",", "&nbsp; <font color=\"red\">|</font> &nbsp;");

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

        return current_technical_detail;
    }

    public List<Trailer_dobj> setTrailerApplDBtoDObj(String applNo, String regnNo) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs;
        String query = "";
        List<Trailer_dobj> listTrailerDobj = new ArrayList<>();
        Trailer_dobj dobj = null;
        String parameterName = "";
        String parameterValue = "";
        String tableName = "";
        try {
            if (applNo != null) {
                parameterName = "appl_no";
                tableName = TableList.VA_TRAILER;
                parameterValue = applNo.toUpperCase();
                query = " SELECT chasi_no,body_type,ld_wt,unld_wt,f_axle_descp,r_axle_descp,o_axle_descp,t_axle_descp,f_axle_weight,r_axle_weight,o_axle_weight,"
                        + " t_axle_weight,sr_no,true::boolean as modifiable from " + TableList.VA_TRAILER + " where appl_no||chasi_no NOT IN (Select appl_no||chasi_no from " + TableList.VA_TRAILER_DETACH + ") and appl_no = ? \n"
                        + " UNION \n"
                        + " SELECT b.chasi_no,b.body_type,b.ld_wt,b.unld_wt,b.f_axle_descp,b.r_axle_descp,b.o_axle_descp,b.t_axle_descp,"
                        + " b.f_axle_weight,b.r_axle_weight,b.o_axle_weight,b.t_axle_weight,0 as sr_no,false::boolean as modifiable from " + TableList.VA_TRAILER + " a inner join " + TableList.VT_TRAILER + " b on a.regn_no = b.regn_no where a.appl_no = ?"
                        + " and  a.appl_no||b.chasi_no NOT IN (Select appl_no||chasi_no from va_trailer_detach)";
                tmgr = new TransactionManager("setTrailerApplDBtoDObj");
                ps = tmgr.prepareStatement(query);
                ps.setString(1, parameterValue);
                ps.setString(2, parameterValue);
            }
            if (regnNo != null) {
                parameterName = "regn_no";
                tableName = TableList.VT_TRAILER;
                parameterValue = regnNo.toUpperCase();
                query = "SELECT *,false::boolean as modifiable from " + TableList.VT_TRAILER + " where regn_no||chasi_no not in (select regn_no||chasi_no from " + TableList.VA_TRAILER_DETACH + ") and regn_no = ?";
                tmgr = new TransactionManager("setTrailerApplDBtoDObj");
                ps = tmgr.prepareStatement(query);
                ps.setString(1, parameterValue);
            }
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                dobj = new Trailer_dobj();
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setBody_type(rs.getString("body_type"));//body_type character varying(30),
                dobj.setLd_wt(rs.getInt("ld_wt"));//ld_wt numeric(6,0),                
                dobj.setUnld_wt(rs.getInt("unld_wt")); //unld_wt numeric(6,0),
                dobj.setF_axle_descp(rs.getString("f_axle_descp"));//f_axle_descp character varying(16),
                dobj.setR_axle_descp(rs.getString("r_axle_descp"));//r_axle_descp character varying(16),
                dobj.setO_axle_descp(rs.getString("o_axle_descp"));//o_axle_descp character varying(16),
                dobj.setT_axle_descp(rs.getString("t_axle_descp"));//t_axle_descp character varying(16),
                dobj.setF_axle_weight(rs.getInt("f_axle_weight"));//f_axle_weight numeric(6,0),
                dobj.setR_axle_weight(rs.getInt("r_axle_weight"));//r_axle_weight numeric(6,0),
                dobj.setO_axle_weight(rs.getInt("o_axle_weight"));//o_axle_weight numeric(6,0),
                dobj.setT_axle_weight(rs.getInt("t_axle_weight"));//t_axle_weight numeric(6,0), 
                if (applNo != null) {
                    dobj.setSrNo(rs.getInt("sr_no"));
                }
                dobj.setModifiable(rs.getBoolean("modifiable"));
                listTrailerDobj.add(dobj);
            }
            if (applNo != null) {
                fetchLinkedTrailerNotDetachedDtls(listTrailerDobj, applNo, null, tmgr);
            }
            if (regnNo != null) {
                fetchLinkedTrailerNotDetachedDtls(listTrailerDobj, null, regnNo, tmgr);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("setTrailerApplDBtoDObj" + e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("setTrailerApplDBtoDObj" + e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                throw new VahanException("setTrailerApplDBtoDObj" + e.getMessage());
            }
        }
        return listTrailerDobj;
    }

    private void insertOrUpdateTrailer(TransactionManager tmgr, String appl_no, String regn_no, List<Trailer_dobj> listTrailerDobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "SELECT appl_no FROM " + TableList.VA_TRAILER + "  where appl_no = ? \n"
                    + "UNION \n"
                    + "SELECT appl_no FROM " + TableList.VA_SIDE_TRAILER + "  where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoTrailerHistory(tmgr, appl_no);
                updateTrailer(tmgr, appl_no, regn_no, listTrailerDobj);
            } else {
                insertIntoTrailer(tmgr, appl_no, regn_no, listTrailerDobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Error in Inserting/Updating Alteration Data.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Error in Inserting/Updating Alteration Data.");
        }
    }

    public void insertIntoTrailerHistory(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "INSERT INTO " + TableList.VHA_TRAILER + " "
                + " SELECT current_timestamp as moved_on, ? as moved_by, state_cd, off_cd, appl_no, regn_no, sr_no, chasi_no, body_type, ld_wt, unld_wt, f_axle_descp, "
                + "       r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight, "
                + "       o_axle_weight, t_axle_weight, op_dt"
                + "  FROM " + TableList.VA_TRAILER + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();

        String query = "INSERT INTO " + TableList.VHA_SIDE_TRAILER + "(\n"
                + "   moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, link_regn_no,  op_dt) \n"
                + "   SELECT current_timestamp,?, state_cd, off_cd, appl_no, regn_no, link_regn_no, op_dt\n"
                + "  FROM " + TableList.VA_SIDE_TRAILER + " where appl_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    private void updateTrailer(TransactionManager tmgr, String appl_no, String regn_no, List<Trailer_dobj> listTrailerDobj) throws SQLException {
        PreparedStatement ps = null;
        if (listTrailerDobj != null && !listTrailerDobj.isEmpty()) {
            for (Trailer_dobj trailerDobj : listTrailerDobj) {
                if (!trailerDobj.isDisable()) {
                    String query = "UPDATE " + TableList.VA_TRAILER + "\n"
                            + "   SET  regn_no=?, chasi_no=?, body_type=?, ld_wt=?, unld_wt=?, \n"
                            + "       f_axle_descp=?, r_axle_descp=?, o_axle_descp=?, t_axle_descp=?, \n"
                            + "       f_axle_weight=?, r_axle_weight=?, o_axle_weight=?, t_axle_weight=?, op_dt = current_timestamp \n"
                            + " WHERE appl_no =? and sr_no = ?";
                    ps = tmgr.prepareStatement(query);
                    int i = 1;

                    ps.setString(i++, regn_no);
                    ps.setString(i++, trailerDobj.getChasi_no());
                    ps.setString(i++, trailerDobj.getBody_type());
                    ps.setInt(i++, trailerDobj.getLd_wt());
                    ps.setInt(i++, trailerDobj.getUnld_wt());
                    ps.setString(i++, trailerDobj.getF_axle_descp());
                    ps.setString(i++, trailerDobj.getR_axle_descp());
                    ps.setString(i++, trailerDobj.getO_axle_descp());
                    ps.setString(i++, trailerDobj.getT_axle_descp());
                    ps.setInt(i++, trailerDobj.getF_axle_weight());
                    ps.setInt(i++, trailerDobj.getR_axle_weight());
                    ps.setInt(i++, trailerDobj.getO_axle_weight());
                    ps.setInt(i++, trailerDobj.getT_axle_weight());
                    ps.setString(i++, appl_no);
                    ps.setInt(i++, trailerDobj.getSrNo());

                    ps.executeUpdate();
                } else {
                    String query = "UPDATE " + TableList.VA_SIDE_TRAILER + "\n"
                            + "   SET link_regn_no=?, op_dt=current_timestamp\n"
                            + " WHERE appl_no = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, trailerDobj.getLinkedRegnNo());
                    ps.setString(2, appl_no);
                    ps.executeUpdate();
                }
            }
        }
    }

    private void insertIntoTrailer(TransactionManager tmgr, String applNo, String regnNo, List<Trailer_dobj> listTrailerDobj) throws SQLException {
        PreparedStatement ps = null;
        int srNo = 1;
        if (listTrailerDobj != null && !listTrailerDobj.isEmpty()) {
            for (Trailer_dobj trailerDobj : listTrailerDobj) {
                if (!trailerDobj.isDisable()) {
                    String query = "INSERT INTO " + TableList.VA_TRAILER + "(state_cd , off_cd, appl_no, regn_no, sr_no, chasi_no, body_type, ld_wt, unld_wt, f_axle_descp,"
                            + " r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight,"
                            + " o_axle_weight, t_axle_weight, op_dt) "
                            + " VALUES (?, ?, ?, ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";
                    ps = tmgr.prepareStatement(query);
                    int i = 1;
                    ps.setString(i++, Util.getUserStateCode());
                    ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
                    ps.setString(i++, applNo);
                    ps.setString(i++, regnNo);
                    ps.setString(i++, trailerDobj.getChasi_no());
                    ps.setString(i++, trailerDobj.getBody_type());
                    ps.setInt(i++, trailerDobj.getLd_wt());
                    ps.setInt(i++, trailerDobj.getUnld_wt());
                    ps.setString(i++, trailerDobj.getF_axle_descp());
                    ps.setString(i++, trailerDobj.getR_axle_descp());
                    ps.setString(i++, trailerDobj.getO_axle_descp());
                    ps.setString(i++, trailerDobj.getT_axle_descp());
                    ps.setInt(i++, trailerDobj.getF_axle_weight());
                    ps.setInt(i++, trailerDobj.getR_axle_weight());
                    ps.setInt(i++, trailerDobj.getO_axle_weight());
                    ps.setInt(i++, trailerDobj.getT_axle_weight());
                    ps.executeUpdate();

                    srNo++;
                } else {
                    String query = "INSERT INTO " + TableList.VA_SIDE_TRAILER + "(\n"
                            + "            state_cd, off_cd, appl_no, regn_no, link_regn_no,  op_dt)\n"
                            + "    VALUES (?, ?, ?, ?, ?, current_timestamp)";
                    ps = tmgr.prepareStatement(query);
                    int i = 1;
                    ps.setString(i++, Util.getUserStateCode());
                    ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
                    ps.setString(i++, applNo);
                    ps.setString(i++, regnNo);
                    ps.setString(i++, trailerDobj.getLinkedRegnNo());

                    ps.executeUpdate();
                }
            }
        }
    }

    private void approvalOfTrailer(TransactionManager tmgr, String appl_no, String regn_no, List<Trailer_dobj> listTrailerDetails, List<Trailer_dobj> listTrailerDetach) throws SQLException {
        PreparedStatement ps;
        String query = "";
        //In Case of detachment of trailer data is present in vt_trailer so move to vh_trailer and delete from vt_trailer.
        if (listTrailerDetach != null && !listTrailerDetach.isEmpty()) {
            for (Trailer_dobj detachDobj : listTrailerDetach) {
                if (!detachDobj.isDisable()) {
                    query = "INSERT INTO " + TableList.VH_TRAILER + "(\n"
                            + "            state_cd, off_cd, regn_no, chasi_no, body_type, ld_wt, unld_wt, \n"
                            + "            f_axle_descp, r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight, \n"
                            + "            r_axle_weight, o_axle_weight, t_axle_weight, appl_no, moved_on, \n"
                            + "            moved_by)\n"
                            + "SELECT state_cd, off_cd , regn_no, chasi_no, body_type, ld_wt, unld_wt, f_axle_descp, r_axle_descp, \n"
                            + "       o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, \n"
                            + "       t_axle_weight, ? as appl_no,current_timestamp as moved_on, ?  as moved_by \n"
                            + "  FROM " + TableList.VT_TRAILER + " where regn_no = ? and chasi_no=?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, appl_no);
                    ps.setString(2, Util.getEmpCode());
                    ps.setString(3, regn_no);
                    ps.setString(4, detachDobj.getChasi_no());
                    ps.executeUpdate();

                    query = "Delete from " + TableList.VT_TRAILER + " where regn_no = ? and chasi_no = ? and state_cd = ? and off_cd = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, regn_no);
                    ps.setString(2, detachDobj.getChasi_no());
                    ps.setString(3, Util.getUserStateCode());
                    ps.setInt(4, Util.getUserSeatOffCode());
                    ps.executeUpdate();

                    query = "INSERT INTO " + TableList.VHA_TRAILER_DETACH + "(moved_on,moved_by,state_cd,off_cd,appl_no,regn_no,chasi_no,op_dt,detach_trailer_flag)\n"
                            + "SELECT current_timestamp + interval '1 second' as moved_on,? as moved_by,state_cd,off_cd,appl_no, regn_no, chasi_no,op_dt,detach_trailer_flag\n"
                            + "  FROM " + TableList.VA_TRAILER_DETACH + " WHERE appl_no = ? and chasi_no = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, appl_no);
                    ps.setString(3, detachDobj.getChasi_no());
                    ps.executeUpdate();

                    query = "Delete from " + TableList.VA_TRAILER_DETACH + " where appl_no = ? and chasi_no = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, appl_no);
                    ps.setString(2, detachDobj.getChasi_no());
                    ps.executeUpdate();
                } else {
                    query = "INSERT INTO " + TableList.VH_SIDE_TRAILER + "(\n"
                            + "            regn_no, link_regn_no, state_cd, off_cd, op_dt, moved_on, moved_by)\n"
                            + "    SELECT regn_no, link_regn_no, state_cd, off_cd, op_dt,current_timestamp,?\n"
                            + "  FROM " + TableList.VT_SIDE_TRAILER + " where link_regn_no = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, detachDobj.getLinkedRegnNo());
                    ps.executeUpdate();

                    query = "Delete from " + TableList.VT_SIDE_TRAILER + " where link_regn_no = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, detachDobj.getLinkedRegnNo());
                    ps.executeUpdate();

                    query = "INSERT INTO " + TableList.VHA_TRAILER_DETACH + "(moved_on,moved_by,state_cd,off_cd,appl_no,regn_no,chasi_no,op_dt,detach_trailer_flag)\n"
                            + "SELECT current_timestamp + interval '1 second' as moved_on,? as moved_by,state_cd,off_cd,appl_no, regn_no, chasi_no,op_dt,detach_trailer_flag\n"
                            + "  FROM " + TableList.VA_TRAILER_DETACH + " WHERE appl_no = ? and chasi_no = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, appl_no);
                    ps.setString(3, detachDobj.getChasi_no());
                    ps.executeUpdate();

                    query = "Delete from " + TableList.VA_TRAILER_DETACH + " where appl_no = ? and chasi_no = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, appl_no);
                    ps.setString(2, detachDobj.getChasi_no());
                    ps.executeUpdate();
                }
            }
        }
        //In case of trailer attachment there is no data in vt_trailer so directly insert in to vt_trailer.

        if (listTrailerDetails != null && !listTrailerDetails.isEmpty()) {
            for (Trailer_dobj attachDobj : listTrailerDetails) {
                if (!attachDobj.isDisable()) {
                    query = "INSERT INTO " + TableList.VT_TRAILER + "(\n"
                            + "            regn_no, chasi_no, body_type, ld_wt, unld_wt, f_axle_descp, r_axle_descp, \n"
                            + "            o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, \n"
                            + "            t_axle_weight, state_cd, off_cd, op_dt)\n"
                            + "    SELECT regn_no, chasi_no, body_type, ld_wt, unld_wt, f_axle_descp, \n"
                            + "       r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight, \n"
                            + "       o_axle_weight, t_axle_weight,? as state_cd,? as off_cd,current_timestamp\n"
                            + "  FROM " + TableList.VA_TRAILER + " where appl_no = ? and sr_no = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setInt(2, Util.getUserSeatOffCode());
                    ps.setString(3, appl_no);
                    ps.setInt(4, attachDobj.getSrNo());
                    ps.executeUpdate();

                    query = "INSERT INTO " + TableList.VHA_TRAILER + "(\n"
                            + "            moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, sr_no, \n"
                            + "            chasi_no, body_type, ld_wt, unld_wt, f_axle_descp, r_axle_descp, \n"
                            + "            o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, \n"
                            + "            t_axle_weight, op_dt)\n"
                            + "    SELECT current_timestamp + interval '1 second' as moved_on, ? as moved_by, state_cd, off_cd, appl_no, regn_no, sr_no, chasi_no, body_type, \n"
                            + "       ld_wt, unld_wt, f_axle_descp, r_axle_descp, o_axle_descp, t_axle_descp, \n"
                            + "       f_axle_weight, r_axle_weight, o_axle_weight, t_axle_weight, op_dt \n"
                            + "  FROM " + TableList.VA_TRAILER + " WHERE appl_no = ? and sr_no = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, appl_no);
                    ps.setInt(3, attachDobj.getSrNo());
                    ps.executeUpdate();

                    query = "Delete from " + TableList.VA_TRAILER + " where appl_no = ? and sr_no = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, appl_no);
                    ps.setInt(2, attachDobj.getSrNo());
                    ps.executeUpdate();

                } else {
                    query = "INSERT INTO " + TableList.VT_SIDE_TRAILER + "(\n"
                            + "   regn_no, link_regn_no, state_cd, off_cd, op_dt)\n"
                            + "    SELECT regn_no, link_regn_no, state_cd, off_cd, current_timestamp\n"
                            + "  FROM " + TableList.VA_SIDE_TRAILER + " where appl_no = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, appl_no);
                    ps.executeUpdate();

                    query = "INSERT INTO " + TableList.VHA_SIDE_TRAILER + "(\n"
                            + "   moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, link_regn_no,  op_dt) \n"
                            + "   SELECT current_timestamp,?, state_cd, off_cd, appl_no, regn_no, link_regn_no, op_dt\n"
                            + "  FROM " + TableList.VA_SIDE_TRAILER + " where appl_no = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, appl_no);
                    ps.executeUpdate();

                    query = "Delete from " + TableList.VA_SIDE_TRAILER + " where appl_no = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, appl_no);
                    ps.executeUpdate();

                }
            }
        }
    }

    private void insertOrUpdateTrailerDetach(TransactionManager tmgr, String appl_no, String regn_no, List<Trailer_dobj> listTrailerDetach) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "SELECT appl_no FROM " + TableList.VA_TRAILER_DETACH + "  where appl_no = ? \n";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoTrailerDetachHistory(tmgr, appl_no);
                updateTrailerDetach(tmgr, appl_no, regn_no, listTrailerDetach);
            } else {
                insertIntoTrailerDetach(tmgr, appl_no, regn_no, listTrailerDetach);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Error in Inserting/Updating Trailer Data.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Error in Inserting/Updating Trailer Data.");
        }
    }

    private void insertIntoTrailerDetach(TransactionManager tmgr, String applNo, String regnNo, List<Trailer_dobj> listTrailerDetach) throws SQLException {
        PreparedStatement ps = null;
        if (listTrailerDetach != null && !listTrailerDetach.isEmpty()) {
            for (Trailer_dobj trailerDobj : listTrailerDetach) {
                String query = "INSERT INTO " + TableList.VA_TRAILER_DETACH + "(\n"
                        + "            state_cd,off_cd,appl_no, regn_no, chasi_no,op_dt,detach_trailer_flag)\n"
                        + "    VALUES (?,?,?, ?, ?,current_timestamp,?)";
                ps = tmgr.prepareStatement(query);
                int i = 1;
                ps.setString(i++, Util.getUserStateCode());
                ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
                ps.setString(i++, applNo);
                ps.setString(i++, regnNo);
                ps.setString(i++, trailerDobj.getChasi_no());
                if (trailerDobj.isDisable()) {
                    ps.setString(i++, TableConstants.DETACH_LINKED_TRAILER);
                } else {
                    ps.setString(i++, TableConstants.DETACH_TRAILER);
                }
                ps.executeUpdate();
            }
        }
    }

    private void insertIntoTrailerDetachHistory(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "INSERT INTO " + TableList.VHA_TRAILER_DETACH + "(\n"
                + "       moved_on,  moved_by,state_cd,off_cd,appl_no, regn_no, chasi_no,op_dt,detach_trailer_flag)\n"
                + "    SELECT current_timestamp as moved_on, ? moved_by,state_cd,off_cd,appl_no, regn_no, chasi_no,op_dt,detach_trailer_flag \n"
                + "  FROM " + TableList.VA_TRAILER_DETACH + " WHERE appl_no= ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    private void updateTrailerDetach(TransactionManager tmgr, String appl_no, String regn_no, List<Trailer_dobj> listTrailerDetach) throws SQLException {
        PreparedStatement ps = null;
        if (listTrailerDetach != null && !listTrailerDetach.isEmpty()) {
            for (Trailer_dobj trailerDobj : listTrailerDetach) {
                String query = "UPDATE " + TableList.VA_TRAILER_DETACH + "\n"
                        + "   SET appl_no=?, regn_no=?, chasi_no=? ,op_dt = current_timestamp\n"
                        + " WHERE appl_no = ? and regn_no = ? and chasi_no = ? ";
                ps = tmgr.prepareStatement(query);
                int i = 1;
                ps.setString(i++, appl_no);
                ps.setString(i++, regn_no);
                ps.setString(i++, trailerDobj.getChasi_no());
                ps.setString(i++, appl_no);
                ps.setString(i++, regn_no);
                ps.setString(i++, trailerDobj.getChasi_no());
                ps.executeUpdate();
            }
        }
    }

    public List<Trailer_dobj> setTrailerDetachApplDBtoDObj(String applNo) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String query = "";
        List<Trailer_dobj> listTrailerDobj = new ArrayList<>();
        Trailer_dobj dobj = null;
        String parameterName = "";
        String parameterValue = "";
        String tableName = "";
        if (applNo != null) {
            parameterName = "appl_no";
            tableName = TableList.VA_TRAILER_DETACH;
            parameterValue = applNo.toUpperCase();
        }

        try {
            tmgr = new TransactionManager("setTrailerApplDBtoDObj");
            query = "Select * from " + tableName + " where " + parameterName + " = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, parameterValue);
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if (rs.getString("detach_trailer_flag").equalsIgnoreCase("N")) {
                    dobj = getTrailerDetail(tmgr, rs.getString("appl_no"), rs.getString("regn_no"), rs.getString("chasi_no"));
                    if (dobj != null) {
                        listTrailerDobj.add(dobj);
                    }
                } else if (rs.getString("detach_trailer_flag").equalsIgnoreCase("L")) {
                    fetchLinkedTrailerDtls(listTrailerDobj, null, rs.getString("regn_no"), tmgr);
                }

            }
        } catch (SQLException e) {
            throw new VahanException("setTrailerDetachApplDBtoDObj->" + e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                throw new VahanException("setTrailerDetachApplDBtoDObj->" + e.getMessage());
            }
        }
        return listTrailerDobj;
    }

    private Trailer_dobj getTrailerDetail(TransactionManager tmgr, String applNo, String regnNo, String chasiNo) throws SQLException {
        Trailer_dobj dobj = null;
        PreparedStatement ps;
        RowSet rs;
        String sql = "SELECT * from " + TableList.VT_TRAILER + " where regn_no||chasi_no in (select regn_no||chasi_no from " + TableList.VA_TRAILER_DETACH + ") and regn_no = ? and chasi_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regnNo);
        ps.setString(2, chasiNo);
        rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            dobj = new Trailer_dobj();
            dobj.setAppl_no(applNo);
            dobj.setRegn_no(regnNo);
            dobj.setChasi_no(rs.getString("chasi_no"));
            dobj.setBody_type(rs.getString("body_type"));//body_type character varying(30),
            dobj.setLd_wt(rs.getInt("ld_wt"));//ld_wt numeric(6,0),                
            dobj.setUnld_wt(rs.getInt("unld_wt")); //unld_wt numeric(6,0),
            dobj.setF_axle_descp(rs.getString("f_axle_descp"));//f_axle_descp character varying(16),
            dobj.setR_axle_descp(rs.getString("r_axle_descp"));//r_axle_descp character varying(16),
            dobj.setO_axle_descp(rs.getString("o_axle_descp"));//o_axle_descp character varying(16),
            dobj.setT_axle_descp(rs.getString("t_axle_descp"));//t_axle_descp character varying(16),
            dobj.setF_axle_weight(rs.getInt("f_axle_weight"));//f_axle_weight numeric(6,0),
            dobj.setR_axle_weight(rs.getInt("r_axle_weight"));//r_axle_weight numeric(6,0),
            dobj.setO_axle_weight(rs.getInt("o_axle_weight"));//o_axle_weight numeric(6,0),
            dobj.setT_axle_weight(rs.getInt("t_axle_weight"));//t_axle_weight numeric(6,0), 
            dobj.setState_cd(rs.getString("state_cd"));
            dobj.setOff_cd(rs.getInt("off_cd"));
        } else {
            sql = "SELECT * from " + TableList.VA_TRAILER + " where appl_no IN (Select appl_no from " + TableList.VA_TRAILER_DETACH + ") and appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new Trailer_dobj();
                dobj.setAppl_no(applNo);
                dobj.setRegn_no(regnNo);
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setBody_type(rs.getString("body_type"));//body_type character varying(30),
                dobj.setLd_wt(rs.getInt("ld_wt"));//ld_wt numeric(6,0),                
                dobj.setUnld_wt(rs.getInt("unld_wt")); //unld_wt numeric(6,0),
                dobj.setF_axle_descp(rs.getString("f_axle_descp"));//f_axle_descp character varying(16),
                dobj.setR_axle_descp(rs.getString("r_axle_descp"));//r_axle_descp character varying(16),
                dobj.setO_axle_descp(rs.getString("o_axle_descp"));//o_axle_descp character varying(16),
                dobj.setT_axle_descp(rs.getString("t_axle_descp"));//t_axle_descp character varying(16),
                dobj.setF_axle_weight(rs.getInt("f_axle_weight"));//f_axle_weight numeric(6,0),
                dobj.setR_axle_weight(rs.getInt("r_axle_weight"));//r_axle_weight numeric(6,0),
                dobj.setO_axle_weight(rs.getInt("o_axle_weight"));//o_axle_weight numeric(6,0),
                dobj.setT_axle_weight(rs.getInt("t_axle_weight"));//t_axle_weight numeric(6,0), 
            }
        }
        return dobj;
    }

    public List<Trailer_dobj> setTrailerDetailsVA(String appl_no) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        Trailer_dobj dobj = null;
        List<Trailer_dobj> trailerList = new ArrayList<>();
        try {
            tmgr = new TransactionManager("setTrailerDetailsVA");
            String query = "Select * from " + TableList.VA_TRAILER + " where appl_no = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                dobj = new Trailer_dobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setBody_type(rs.getString("body_type"));//body_type character varying(30),
                dobj.setLd_wt(rs.getInt("ld_wt"));//ld_wt numeric(6,0),                
                dobj.setUnld_wt(rs.getInt("unld_wt")); //unld_wt numeric(6,0),
                dobj.setF_axle_descp(rs.getString("f_axle_descp"));//f_axle_descp character varying(16),
                dobj.setR_axle_descp(rs.getString("r_axle_descp"));//r_axle_descp character varying(16),
                dobj.setO_axle_descp(rs.getString("o_axle_descp"));//o_axle_descp character varying(16),
                dobj.setT_axle_descp(rs.getString("t_axle_descp"));//t_axle_descp character varying(16),
                dobj.setF_axle_weight(rs.getInt("f_axle_weight"));//f_axle_weight numeric(6,0),
                dobj.setR_axle_weight(rs.getInt("r_axle_weight"));//r_axle_weight numeric(6,0),
                dobj.setO_axle_weight(rs.getInt("o_axle_weight"));//o_axle_weight numeric(6,0),
                dobj.setT_axle_weight(rs.getInt("t_axle_weight"));//t_axle_weight numeric(6,0), 
                dobj.setSrNo(rs.getInt("sr_no"));
                trailerList.add(dobj);
            }
            fetchLinkedTrailerDtls(trailerList, appl_no, null, tmgr);
        } catch (SQLException e) {
            throw new VahanException("setTrailerDetailsVA " + e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                throw new VahanException("setTrailerDetailsVA " + e.getMessage());
            }
        }
        return trailerList;
    }

    public void getLinkedTrailerDtls(Trailer_dobj dobj) throws VahanException {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs;
        String query = "Select a.chasi_no,a.body_type,a.ld_wt,a.unld_wt,a.vh_class,b.* from " + TableList.VT_OWNER + " a inner join " + TableList.VT_AXLE + " b "
                + " on a.regn_no = b.regn_no Where a.regn_no = ?";
        try {
            tmgr = new TransactionManager("getLinkedTrailerDtls");
            ps = tmgr.prepareStatement(query);
            ps.setString(1, dobj.getLinkedRegnNo());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (!TableConstants.TRAILER_VEH_CLASS.contains("," + rs.getString("vh_class") + ",")) {
                    throw new VahanException("Vehicle is not linkable.");
                }
                dobj.setLinkedRegnNo(rs.getString("regn_no"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setBody_type(rs.getString("body_type"));//body_type character varying(30),
                dobj.setLd_wt(rs.getInt("ld_wt"));//ld_wt numeric(6,0),                
                dobj.setUnld_wt(rs.getInt("unld_wt")); //unld_wt numeric(6,0),
                dobj.setF_axle_descp(rs.getString("f_axle_descp"));//f_axle_descp character varying(16),
                dobj.setR_axle_descp(rs.getString("r_axle_descp"));//r_axle_descp character varying(16),
                dobj.setO_axle_descp(rs.getString("o_axle_descp"));//o_axle_descp character varying(16),
                dobj.setT_axle_descp(rs.getString("t_axle_descp"));//t_axle_descp character varying(16),
                dobj.setF_axle_weight(rs.getInt("f_axle_weight"));//f_axle_weight numeric(6,0),
                dobj.setR_axle_weight(rs.getInt("r_axle_weight"));//r_axle_weight numeric(6,0),
                dobj.setO_axle_weight(rs.getInt("o_axle_weight"));//o_axle_weight numeric(6,0),
                dobj.setT_axle_weight(rs.getInt("t_axle_weight"));//t_axle_weight numeric(6,0), 
                dobj.setDisable(true);
                dobj.setModifiable(false);
            } else {
                throw new VahanException("Vehicle Details not found.");
            }

        } catch (SQLException e) {
            throw new VahanException("getLinkedTrailerDtls " + e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                throw new VahanException("getLinkedTrailerDtls " + e.getMessage());
            }
        }
    }

    private void fetchLinkedTrailerDtls(List<Trailer_dobj> trailerList, String applNo, String regnNo, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps;
        RowSet rs;
        String query = "";
        if (applNo != null) {
            query = "Select * from " + TableList.VA_SIDE_TRAILER + " where  appl_no = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, applNo);
        } else if (regnNo != null) {
            query = "Select * from " + TableList.VT_SIDE_TRAILER + " where regn_no = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regnNo);
        }
        rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            query = "Select a.chasi_no,a.body_type,a.ld_wt,a.unld_wt,b.* from " + TableList.VT_OWNER + " a inner join " + TableList.VT_AXLE + " b "
                    + " on a.regn_no = b.regn_no Where a.regn_no = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, rs.getString("link_regn_no"));
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                Trailer_dobj dobj = new Trailer_dobj();
                dobj.setLinkedRegnNo(rs.getString("regn_no"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setBody_type(rs.getString("body_type"));//body_type character varying(30),
                dobj.setLd_wt(rs.getInt("ld_wt"));//ld_wt numeric(6,0),                
                dobj.setUnld_wt(rs.getInt("unld_wt")); //unld_wt numeric(6,0),
                dobj.setF_axle_descp(rs.getString("f_axle_descp"));//f_axle_descp character varying(16),
                dobj.setR_axle_descp(rs.getString("r_axle_descp"));//r_axle_descp character varying(16),
                dobj.setO_axle_descp(rs.getString("o_axle_descp"));//o_axle_descp character varying(16),
                dobj.setT_axle_descp(rs.getString("t_axle_descp"));//t_axle_descp character varying(16),
                dobj.setF_axle_weight(rs.getInt("f_axle_weight"));//f_axle_weight numeric(6,0),
                dobj.setR_axle_weight(rs.getInt("r_axle_weight"));//r_axle_weight numeric(6,0),
                dobj.setO_axle_weight(rs.getInt("o_axle_weight"));//o_axle_weight numeric(6,0),
                dobj.setT_axle_weight(rs.getInt("t_axle_weight"));//t_axle_weight numeric(6,0), 
                dobj.setDisable(true);
                dobj.setModifiable(false);
                trailerList.add(dobj);
            }
        }

    }

    private void fetchLinkedTrailerNotDetachedDtls(List<Trailer_dobj> trailerList, String applNo, String regnNo, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps;
        RowSet rs;
        String query = "";
        if (applNo != null) {
            query = "Select * from " + TableList.VA_SIDE_TRAILER + " where appl_no NOT IN (Select appl_no from " + TableList.VA_TRAILER_DETACH + " where appl_no = ?) and appl_no = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, applNo);
            ps.setString(2, applNo);
        } else if (regnNo != null) {
            query = "Select * from " + TableList.VT_SIDE_TRAILER + " where regn_no NOT IN (Select regn_no from " + TableList.VA_TRAILER_DETACH + " where regn_no = ?) and regn_no = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regnNo);
            ps.setString(2, regnNo);
        }
        rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            query = "Select a.chasi_no,a.body_type,a.ld_wt,a.unld_wt,b.* from " + TableList.VT_OWNER + " a inner join " + TableList.VT_AXLE + " b "
                    + " on a.regn_no = b.regn_no Where a.regn_no = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, rs.getString("link_regn_no"));
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                Trailer_dobj dobj = new Trailer_dobj();
                dobj.setLinkedRegnNo(rs.getString("regn_no"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setBody_type(rs.getString("body_type"));//body_type character varying(30),
                dobj.setLd_wt(rs.getInt("ld_wt"));//ld_wt numeric(6,0),                
                dobj.setUnld_wt(rs.getInt("unld_wt")); //unld_wt numeric(6,0),
                dobj.setF_axle_descp(rs.getString("f_axle_descp"));//f_axle_descp character varying(16),
                dobj.setR_axle_descp(rs.getString("r_axle_descp"));//r_axle_descp character varying(16),
                dobj.setO_axle_descp(rs.getString("o_axle_descp"));//o_axle_descp character varying(16),
                dobj.setT_axle_descp(rs.getString("t_axle_descp"));//t_axle_descp character varying(16),
                dobj.setF_axle_weight(rs.getInt("f_axle_weight"));//f_axle_weight numeric(6,0),
                dobj.setR_axle_weight(rs.getInt("r_axle_weight"));//r_axle_weight numeric(6,0),
                dobj.setO_axle_weight(rs.getInt("o_axle_weight"));//o_axle_weight numeric(6,0),
                dobj.setT_axle_weight(rs.getInt("t_axle_weight"));//t_axle_weight numeric(6,0), 
                dobj.setDisable(true);
                dobj.setModifiable(false);
                trailerList.add(dobj);
            }
        }
    }

    public static void insertOrUpdateVaOwnerOther(TransactionManager tmgr, String appl_no, String regn_no, AltDobj dobj, String emp_cd, String state_cd) throws VahanException {
        VahanException vahanexecption = null;
        try {
            PreparedStatement ps = tmgr.prepareStatement("Select * from " + TableList.VA_OWNER_OTHER + " where appl_no=?");
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                insertVhaOwnerOther(tmgr, appl_no, regn_no, dobj, emp_cd);
                updateVaOwnerOther(tmgr, appl_no, regn_no, dobj);
            } else {
                insertVaOwnerOther(tmgr, appl_no, regn_no, dobj, state_cd);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanexecption = new VahanException("Something went Wrong");
            throw vahanexecption;
        }
    }

    public static void insertVhaOwnerOther(TransactionManager tmgr, String appl_no, String regn_no, AltDobj dobj, String emp_cd) throws Exception {
        PreparedStatement ps = null;
        String sql = "INSERT INTO  " + TableList.VHA_OWNER_OTHER
                + "  SELECT current_timestamp as moved_on, ? moved_by,state_cd,off_cd,appl_no, regn_no, "
                + "  push_back_seat,ordinary_seat,op_dt "
                + "  FROM  " + TableList.VA_OWNER_OTHER
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, emp_cd);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public static void updateVaOwnerOther(TransactionManager tmgr, String appl_no, String regn_no, AltDobj dobj) throws Exception {
        PreparedStatement ps = null;
        String sql = "UPDATE " + TableList.VA_OWNER_OTHER
                + "   SET push_back_seat=?, ordinary_seat=?,op_dt = current_timestamp"
                + " WHERE appl_no=?";
        ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setInt(i++, dobj.getPush_bk_seat());
        ps.setInt(i++, dobj.getOrdinary_seat());
        ps.setString(i++, dobj.getAppl_no());
        ps.executeUpdate();
    }

    public static void insertVaOwnerOther(TransactionManager tmgr, String appl_no, String regn_no, AltDobj dobj, String state_cd) throws Exception {
        PreparedStatement ps = null;
        String sql = "INSERT INTO " + TableList.VA_OWNER_OTHER
                + "(state_cd,off_cd,appl_no,regn_no,push_back_seat, ordinary_seat,op_dt)"
                + "  VALUES (?,?,?,?,?,?,current_timestamp)";
        ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, state_cd);
        ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
        ps.setString(i++, dobj.getAppl_no());
        ps.setString(i++, dobj.getRegn_no());
        ps.setInt(i++, dobj.getPush_bk_seat());
        ps.setInt(i++, dobj.getOrdinary_seat());
        ps.executeUpdate();
    }

    public void ownerOtherApproval(TransactionManager tmgr, String appl_no, String regn_no, AltDobj dobj, String emp_cd) throws SQLException, VahanException {
        PreparedStatement ps = null;
        String query = "SELECT * FROM " + TableList.VT_OWNER_OTHER + " WHERE regn_no=?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, dobj.getRegn_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            query = "INSERT INTO " + TableList.VH_OWNER_OTHER + "(\n"
                    + "          state_cd, off_cd, regn_no, push_back_seat,ordinary_seat,op_dt ,moved_on, moved_by)\n"
                    + "    SELECT state_cd, off_cd, regn_no, push_back_seat,ordinary_seat,op_dt,current_timestamp,?\n"
                    + "  FROM " + TableList.VT_OWNER_OTHER + " where regn_no = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, emp_cd);
            ps.setString(2, dobj.getRegn_no());
            ps.executeUpdate();
            query = "DELETE FROM " + TableList.VT_OWNER_OTHER + " WHERE regn_no=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, dobj.getRegn_no());
            ps.executeUpdate();
        }
        query = "INSERT into  " + TableList.VT_OWNER_OTHER
                + " Select state_cd, off_cd, regn_no, push_back_seat,ordinary_seat,"
                + " current_timestamp as op_dt"
                + "  FROM " + TableList.VA_OWNER_OTHER
                + " where appl_no=? and regn_no != 'NEW'";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, dobj.getAppl_no());
        ps.executeUpdate();
        query = "INSERT INTO  " + TableList.VHA_OWNER_OTHER
                + "  SELECT current_timestamp + interval '1 second' as moved_on, ? moved_by,state_cd,off_cd,appl_no, regn_no, "
                + "  push_back_seat,ordinary_seat,op_dt "
                + "  FROM  " + TableList.VA_OWNER_OTHER
                + " where appl_no=? and regn_no != 'NEW' ";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, emp_cd);
        ps.setString(2, dobj.getAppl_no());
        ps.executeUpdate();
        query = "DELETE FROM " + TableList.VA_OWNER_OTHER + " WHERE regn_no=?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, dobj.getRegn_no());
        ps.executeUpdate();
    }
}
