/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.sql.RowSet;
import nic.java.util.CommonUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.DocumentType;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.RcSurrenderReleaseCancellationDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertForQRDetails;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
public class RcSurrenderReleaseCancellationImpl {

    private static final Logger LOGGER = Logger.getLogger(RcSurrenderReleaseCancellationImpl.class);

    public RcSurrenderReleaseCancellationDobj getVehicleDetailsForRc(String vehicleNO, int purCode, int actionCode, String applNo) throws VahanException {
        PreparedStatement psVtOwner = null;
        PreparedStatement psOtherDetails = null;
        PreparedStatement psReleaseDetails = null;
        TransactionManagerReadOnly tmgr = null;
        RcSurrenderReleaseCancellationDobj dobj = null;
        String sqlVtOwner = "";
        String sqlOtherDEtails = "";
        String sqlReleaseDetails = "";
        String status = null;

        sqlVtOwner = "SELECT state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name, \n"
                + "       f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state, \n"
                + "       p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd, \n"
                + "       regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type, \n"
                + "       no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt, \n"
                + "       gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap, \n"
                + "       floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as, \n"
                + "       vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length, \n"
                + "       width, height, regn_upto, fit_upto, annual_income, imported_vch, \n"
                + "       other_criteria, status, op_dt\n"
                + "  FROM " + TableList.VT_OWNER + " where regn_no=? and state_cd = ? and off_cd  = ?";
        try {
            dobj = new RcSurrenderReleaseCancellationDobj();
            tmgr = new TransactionManagerReadOnly("Vehicle Details");

            psVtOwner = tmgr.prepareStatement(sqlVtOwner);
            psVtOwner.setString(1, vehicleNO);
            psVtOwner.setString(2, Util.getUserStateCode());
            psVtOwner.setInt(3, Util.getSelectedSeat().getOff_cd());

            RowSet rowSet = tmgr.fetchDetachedRowSet_No_release();

            if (rowSet.next()) {

                dobj.setAddress(rowSet.getString("c_add1") + "," + rowSet.getString("c_add2") + "," + rowSet.getString("c_add3") + "," + rowSet.getString("c_district") + "," + rowSet.getString("c_pincode") + "," + rowSet.getString("c_state"));
                dobj.setAddress1(rowSet.getString("c_add1") + "," + rowSet.getString("c_add2") + ",<br/>" + rowSet.getString("c_add3") + "," + rowSet.getString("c_district") + ",<br/>" + rowSet.getString("c_pincode") + "," + rowSet.getString("c_state"));
                dobj.setChassisNo(rowSet.getString("chasi_no"));
                dobj.setFitnessValidity(rowSet.getDate("fit_upto"));
                dobj.setOwnerName(rowSet.getString("owner_name"));
                dobj.setVehicleClass(rowSet.getString("vh_class"));
                dobj.setVehicleNo(rowSet.getString("regn_no"));
                status = rowSet.getString("status");
            }


            String sqlOtherDEtailsva = "SELECT appl_no, regn_no, surr_dt, file_ref_no, approved_by, reason, \n"
                    + "       rc, rc_sno, permit, permit_sno, fc, fc_sno, taxexem, op_dt\n"
                    + "  FROM " + TableList.VA_RC_SURRENDER + " where  regn_no=?";
            String sqlOtherDEtailsvt = "SELECT appl_no, regn_no, surr_dt, file_ref_no, approved_by, reason, \n"
                    + "       rc, rc_sno, permit, permit_sno, fc, fc_sno, taxexem, op_dt\n"
                    + "  FROM " + TableList.VT_RC_SURRENDER + " where  regn_no=?";
            String sqlVtCnclDetail = "SELECT *  FROM " + TableList.VT_RC_CANCEL + " where  regn_no=?";
            String sqlVtSuspDetail = "SELECT *  FROM " + TableList.VT_RC_SUSPEND + " where  regn_no=?";
            if (purCode == TableConstants.VM_MAST_RC_SURRENDER) {
                sqlOtherDEtails = sqlOtherDEtailsva;
            }
            if (purCode == TableConstants.VM_MAST_RC_RELEASE) {
                if (!CommonUtils.isNullOrBlank(status) && status.equalsIgnoreCase(TableConstants.VT_RC_SURRENDER_STATUS)) {
                    sqlOtherDEtails = sqlOtherDEtailsvt;
                } else if (!CommonUtils.isNullOrBlank(status) && status.equalsIgnoreCase(TableConstants.VT_RC_CANCEL_STATUS)) {
                    if (!"".equalsIgnoreCase(sqlVtCnclDetail)) {
                        psOtherDetails = tmgr.prepareStatement(sqlVtCnclDetail);
                        psOtherDetails.setString(1, vehicleNO);
                        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            dobj.setApprovedBy(rs.getString("approved_by"));
                            dobj.setSurrenderDate(rs.getDate("cancel_dt"));
                            dobj.setFileReferenceNo(rs.getString("file_ref_no"));
                            if (CommonUtils.isNullOrBlank(rs.getString("reason"))) {
                                dobj.setReason("NA");
                            } else {
                                dobj.setReason(rs.getString("reason"));
                            }
                            dobj.setRcCancelBy(rs.getInt("requested_by"));
                        }
                    }
                } else if (!CommonUtils.isNullOrBlank(status) && status.equalsIgnoreCase(TableConstants.VT_RC_SUSPEND_STATUS)) {
                    if (!"".equalsIgnoreCase(sqlVtSuspDetail)) {
                        psOtherDetails = tmgr.prepareStatement(sqlVtSuspDetail);
                        psOtherDetails.setString(1, vehicleNO);
                        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            dobj.setApprovedBy(rs.getString("approved_by"));
                            dobj.setSurrenderDate(rs.getDate("susp_dt"));
                            dobj.setSuspendedUptoDate(rs.getDate("suspended_upto"));
                            dobj.setFileReferenceNo(rs.getString("file_ref_no"));
                            if (CommonUtils.isNullOrBlank(rs.getString("reason"))) {
                                dobj.setReason("NA");
                            } else {
                                dobj.setReason(rs.getString("reason"));
                            }
                        }
                    }
                }
            }
            if (!"".equalsIgnoreCase(sqlOtherDEtails)) {
                psOtherDetails = tmgr.prepareStatement(sqlOtherDEtails);
                psOtherDetails.setString(1, vehicleNO);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    dobj.setApprovedBy(rs.getString("approved_by"));
                    if (CommonUtils.isNullOrBlank(rs.getString("reason"))) {
                        dobj.setReason("NA");
                    } else {
                        dobj.setReason(rs.getString("reason"));
                    }
                    dobj.setFileReferenceNo(rs.getString("file_ref_no"));
                    dobj.setSurrenderDate(rs.getDate("surr_dt"));
                    dobj.setRcStatus(rs.getString("rc"));
                    dobj.setRcNo(rs.getString("rc_sno"));
                    dobj.setPermitStatus(rs.getString("permit"));
                    dobj.setPermitNo(rs.getString("permit_sno"));
                    dobj.setFitnessCerStatus(rs.getString("fc"));
                    dobj.setFitnessCerNo(rs.getString("fc_sno"));
                    dobj.setTaxExampStatus(rs.getString("taxexem"));
                    dobj.setSurAndSus(TableConstants.RC_SURRENDER);
                }
            }
            sqlReleaseDetails = "SELECT appl_no, regn_no, release_dt, rel_file_ref_no, rel_approved_by, \n"
                    + "       rel_op_dt\n"
                    + "  FROM " + TableList.VA_RC_RELEASE + " where regn_no=?";
            psReleaseDetails = tmgr.prepareStatement(sqlReleaseDetails);
            psReleaseDetails.setString(1, vehicleNO);
            RowSet rowSet1 = tmgr.fetchDetachedRowSet_No_release();
            if (rowSet1.next()) {
                dobj.setReleaseApprovedBy(rowSet1.getString("rel_approved_by"));
                dobj.setReleaseDate(rowSet1.getDate("release_dt"));
                dobj.setReleaseFileReferenceNo(rowSet1.getString("rel_file_ref_no"));
            }
            sqlReleaseDetails = "SELECT appl_no, regn_no, cancel_dt, file_ref_no, approved_by, reason, \n"
                    + "       op_dt,requested_by\n"
                    + "  FROM " + TableList.VA_RC_CANCEL + " where regn_no=? and appl_no=?";
            psReleaseDetails = tmgr.prepareStatement(sqlReleaseDetails);
            psReleaseDetails.setString(1, vehicleNO);
            psReleaseDetails.setString(2, applNo);
            RowSet rowSetCancel = tmgr.fetchDetachedRowSet_No_release();
            if (rowSetCancel.next()) {
                dobj.setApprovedBy(rowSetCancel.getString("approved_by"));
                dobj.setSurrenderDate(rowSetCancel.getDate("cancel_dt"));
                dobj.setFileReferenceNo(rowSetCancel.getString("file_ref_no"));
                dobj.setReason(rowSetCancel.getString("reason"));
                dobj.setRcCancelBy(rowSetCancel.getInt("requested_by"));
            }
            String sqlSuspensionDetails = "SELECT appl_no, regn_no, susp_dt,suspended_upto, file_ref_no, approved_by, reason, \n"
                    + "       op_dt\n"
                    + "  FROM " + TableList.VA_RC_SUSPEND + " where regn_no=? and appl_no=?";
            psReleaseDetails = tmgr.prepareStatement(sqlSuspensionDetails);
            psReleaseDetails.setString(1, vehicleNO);
            psReleaseDetails.setString(2, applNo);
            RowSet rowSetSuspension = tmgr.fetchDetachedRowSet_No_release();
            if (rowSetSuspension.next()) {
                dobj.setApprovedBy(rowSetSuspension.getString("approved_by"));
                dobj.setSurrenderDate(rowSetSuspension.getDate("susp_dt"));
                dobj.setSuspendedUptoDate(rowSetSuspension.getDate("suspended_upto"));
                dobj.setFileReferenceNo(rowSetSuspension.getString("file_ref_no"));
                dobj.setReason(rowSetSuspension.getString("reason"));
                dobj.setSurAndSus(TableConstants.RC_SUSPENSION);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
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

    /**
     *
     * @param dobjSurr
     * @param statusDobj
     * @return
     * @throws Exception
     */
    public boolean saveForRcSurrenderReleaseCancellation(int actionCd, int purCd, Status_dobj statusDobj, String changedDataContents, RcSurrenderReleaseCancellationDobj dobjSurr, TransactionManager tmgr, String surOrSus) throws VahanException {
        boolean saveFlag = false;
        PreparedStatement ps = null;
        RcSurrenderReleaseCancellationDobj dobj = null;
        String sqlVaRcRelease = "";
        String sqlVaRcCancellation = "";
        int index = 1;
        java.util.Date currdate = new java.util.Date();
        String sqlVaRcSurrender = "";
        String sqlVaRcSuspend = "";
        sqlVaRcSurrender = "INSERT INTO " + TableList.VA_RC_SURRENDER + "(\n"
                + "state_cd,off_cd, appl_no, regn_no, surr_dt, file_ref_no, approved_by, reason, \n"
                + " rc, rc_sno, permit, permit_sno, fc, fc_sno, taxexem, op_dt)\n"
                + " VALUES (?, ?, ?, ?, ?, ?, \n"
                + " ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
        sqlVaRcSuspend = "INSERT INTO " + TableList.VA_RC_SUSPEND + "(\n"
                + "state_cd,off_cd, appl_no, regn_no, susp_dt,suspended_upto, file_ref_no, approved_by, reason, \n"
                + "  op_dt)\n"
                + " VALUES (?, ?, ?, ?, ?, ?, \n"
                + " ?, ?, ?,?)";
        sqlVaRcRelease = "INSERT INTO " + TableList.VA_RC_RELEASE + "(\n"
                + "           state_cd,off_cd, appl_no, regn_no, release_dt, rel_file_ref_no, rel_approved_by, \n"
                + "            rel_op_dt)\n"
                + "    VALUES (?, ?, ?, ?, ?, \n"
                + "            ?,?,?)";

        sqlVaRcCancellation = "INSERT INTO " + TableList.VA_RC_CANCEL + "(\n"
                + "           state_cd,off_cd, appl_no, regn_no, cancel_dt, file_ref_no, approved_by, reason, \n"
                + "            op_dt,requested_by)\n"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, \n"
                + "            current_timestamp,?)";
        try {
            if (statusDobj.getPur_cd() == TableConstants.VM_MAST_RC_SURRENDER && statusDobj.getAction_cd() == TableConstants.VM_ROLE_RC_SURRENDER_ENTRY) {
                index = 1;
                if (surOrSus.equalsIgnoreCase(TableConstants.RC_INWARD_SURRENDER)) {
                    ps = tmgr.prepareStatement(sqlVaRcSurrender);
                    ps.setString(index++, Util.getUserStateCode());
                    ps.setInt(index++, Util.getSelectedSeat().getOff_cd());
                    ps.setString(index++, statusDobj.getAppl_no());
                    ps.setString(index++, dobjSurr.getVehicleNo());
                    ps.setDate(index++, (Date) getDate(dobjSurr.getSurrenderDate().toString()));
                    ps.setString(index++, dobjSurr.getFileReferenceNo());
                    ps.setString(index++, dobjSurr.getApprovedBy());
                    ps.setString(index++, dobjSurr.getReason());
                    ps.setString(index++, dobjSurr.getRcStatus());
                    ps.setString(index++, dobjSurr.getRcNo());
                    ps.setString(index++, dobjSurr.getPermitStatus());
                    ps.setString(index++, dobjSurr.getPermitNo());
                    ps.setString(index++, dobjSurr.getFitnessCerStatus());
                    ps.setString(index++, dobjSurr.getFitnessCerNo());
                    ps.setString(index++, dobjSurr.getTaxExampStatus());
                    ps.setDate(index++, getDate(dobjSurr.getSurrenderDate().toString()));
                    ps.execute();
                } else if (surOrSus.equalsIgnoreCase(TableConstants.RC_INWARD_SUSPENSION)) {
                    ps = tmgr.prepareStatement(sqlVaRcSuspend);
                    ps.setString(index++, Util.getUserStateCode());
                    ps.setInt(index++, Util.getSelectedSeat().getOff_cd());
                    ps.setString(index++, statusDobj.getAppl_no());
                    ps.setString(index++, dobjSurr.getVehicleNo());
                    ps.setDate(index++, (Date) getDate(dobjSurr.getSurrenderDate().toString()));
                    ps.setDate(index++, (Date) getDate(dobjSurr.getSuspendedUptoDate().toString()));
                    ps.setString(index++, dobjSurr.getFileReferenceNo());
                    ps.setString(index++, dobjSurr.getApprovedBy());
                    ps.setString(index++, dobjSurr.getReason());
                    ps.setDate(index++, getDate(dobjSurr.getSurrenderDate().toString()));
                    ps.execute();
                }
            }
            if (statusDobj.getPur_cd() == TableConstants.VM_MAST_RC_RELEASE && statusDobj.getAction_cd() == TableConstants.VM_ROLE_RC_RELEASE_ENTRY) {
                index = 1;
                ps = tmgr.prepareStatement(sqlVaRcRelease);
                ps.setString(index++, Util.getUserStateCode());
                ps.setInt(index++, Util.getSelectedSeat().getOff_cd());
                ps.setString(index++, statusDobj.getAppl_no());
                ps.setString(index++, dobjSurr.getVehicleNo());
                ps.setDate(index++, (Date) getDate(dobjSurr.getReleaseDate().toString()));
                ps.setString(index++, dobjSurr.getReleaseFileReferenceNo());
                ps.setString(index++, dobjSurr.getReleaseApprovedBy());
                ps.setDate(index++, getDate(dobjSurr.getReleaseDate().toString()));
                ps.execute();
            }
            if (statusDobj.getPur_cd() == TableConstants.VM_MAST_RC_CANCELLATION && statusDobj.getAction_cd() == TableConstants.VM_ROLE_RC_CANCELLATION_ENTRY) {
                index = 1;
                ps = tmgr.prepareStatement(sqlVaRcCancellation);
                ps.setString(index++, Util.getUserStateCode());
                ps.setInt(index++, Util.getSelectedSeat().getOff_cd());
                ps.setString(index++, statusDobj.getAppl_no());
                ps.setString(index++, dobjSurr.getVehicleNo());
                ps.setDate(index++, (Date) getDate(dobjSurr.getCancellationDate().toString()));
                ps.setString(index++, dobjSurr.getCancellationFileReferenceNo());
                ps.setString(index++, dobjSurr.getCancellationApprovedBy());
                ps.setString(index++, dobjSurr.getCancellationReason());
                ps.setInt(index++, dobjSurr.getRcCancelBy());
                ps.execute();


            }

            saveFlag = true;

        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }



        return saveFlag;
    }

    public static Date getDate(String Date) throws VahanException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yy");
        Date st = null;
        try {
            java.util.Date date = sdf.parse(Date);
            st = new Date(date.getTime());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return st;
    }

    public void updateRcStatus(String actionCd, String purCd, Status_dobj statusDobj, String changedDataContents, RcSurrenderReleaseCancellationDobj dobjSurr, String vehicleStatus, String surOrSus) throws VahanException {
        OwnerImpl ownerImpl = new OwnerImpl();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        RcSurrenderReleaseCancellationDobj dobj = null;
        java.util.Date currdate = new java.util.Date();
        String vtRcSurrender = "INSERT INTO " + TableList.VT_RC_SURRENDER + "(\n"
                + "  state_cd, off_cd, appl_no, regn_no, surr_dt, file_ref_no, approved_by, \n"
                + "  reason, rc, rc_sno, permit, permit_sno, fc, fc_sno, taxexem, \n"
                + "  op_dt)\n"
                + "  VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                + "  ?, ?, ?, ?, ?, ?, ?, ?, \n"
                + "  ?)";
        String sqlVhRcSurrender = "INSERT INTO vh_rc_surrender(SELECT VTRELEASE.appl_no as rel_app_no,VTSURRENDER.state_cd, VTSURRENDER.off_cd, VTSURRENDER.appl_no, VTSURRENDER.regn_no, VTSURRENDER.surr_dt, VTSURRENDER.file_ref_no, VTSURRENDER.approved_by, \n"
                + "       VTSURRENDER.reason, VTSURRENDER.rc, VTSURRENDER.rc_sno, VTSURRENDER.permit, VTSURRENDER.permit_sno, VTSURRENDER.fc, VTSURRENDER.fc_sno, VTSURRENDER.taxexem, \n"
                + "       VTSURRENDER.op_dt,\n"
                + " VTRELEASE.release_dt, VTRELEASE.rel_file_ref_no, VTRELEASE.rel_approved_by, \n"
                + "       VTRELEASE.rel_op_dt,CURRENT_TIMESTAMP,?\n"
                + "  FROM " + TableList.VT_RC_SURRENDER + " VTSURRENDER\n"
                + "INNER JOIN " + TableList.VA_RC_RELEASE + " VTRELEASE ON VTSURRENDER.regn_no=VTRELEASE.regn_no\n"
                + "where VTSURRENDER.regn_no=?)";
        String sqlVhRcCancel = "INSERT INTO " + TableList.VH_RC_CANCEL + "(SELECT  current_timestamp ,? , appl_no ,regn_no , cancel_dt , file_ref_no , approved_by ,reason , op_dt "
                + "  FROM " + TableList.VT_RC_CANCEL
                + " where regn_no=?)";
        String sqlVtOwner = "UPDATE " + TableList.VT_OWNER + "\n"
                + "   SET  status=? , op_dt=current_timestamp \n"
                + " WHERE regn_no=? and state_cd = ? and off_cd = ?";
        String sqlVaRcReleaseDel = "DELETE FROM " + TableList.VA_RC_RELEASE + "\n"
                + " WHERE appl_no=?";
        String sqlVaRcSurrenderDel = "DELETE FROM " + TableList.VA_RC_SURRENDER + "\n"
                + " WHERE appl_no=?";
        String vtRcSurrenderDel = "DELETE FROM " + TableList.VT_RC_SURRENDER + "\n"
                + " WHERE regn_no=?";
        String vtRcCancelDel = "DELETE FROM " + TableList.VT_RC_CANCEL + "\n"
                + " WHERE regn_no=?";
        String sqlVtRcCancellation = "INSERT INTO " + TableList.VT_RC_CANCEL + "(SELECT appl_no, regn_no, "
                + "cancel_dt, file_ref_no, approved_by, reason, \n"
                + "       op_dt\n"
                + "  FROM " + TableList.VA_RC_CANCEL + " where appl_no=?)";
        String sqlVaRcCancellationDel = "DELETE FROM " + TableList.VA_RC_CANCEL + "\n"
                + " WHERE appl_no=?";
        String sqlVaRcSuspendDel = "DELETE FROM " + TableList.VA_RC_SUSPEND + "\n"
                + " WHERE appl_no=?";
        String sqlVtRcSuspend = "INSERT INTO " + TableList.VT_RC_SUSPEND + "(SELECT state_cd,off_cd, appl_no, regn_no, "
                + "susp_dt, file_ref_no, approved_by, reason, \n"
                + "       op_dt,suspended_upto\n"
                + "  FROM " + TableList.VA_RC_SUSPEND + " where appl_no=?)";
        String vtRcSuspendDel = "DELETE FROM " + TableList.VT_RC_SUSPEND + "\n"
                + "  WHERE regn_no=?";
        String sqlVhRcSuspend = "INSERT INTO " + TableList.VH_RC_SUSPEND + "(SELECT VTRELEASE.appl_no as rel_app_no,VTSUSPEND.state_cd, VTSUSPEND.off_cd, VTSUSPEND.appl_no, VTSUSPEND.regn_no, VTSUSPEND.susp_dt, VTSUSPEND.file_ref_no, VTSUSPEND.approved_by, \n"
                + " VTSUSPEND.reason, VTSUSPEND.op_dt,\n"
                + " VTRELEASE.release_dt, VTRELEASE.rel_file_ref_no, VTRELEASE.rel_approved_by, \n"
                + " VTRELEASE.rel_op_dt,CURRENT_TIMESTAMP,?,VTSUSPEND.suspended_upto\n"
                + " FROM " + TableList.VT_RC_SUSPEND + " VTSUSPEND\n"
                + "INNER JOIN " + TableList.VA_RC_RELEASE + " VTRELEASE ON VTSUSPEND.regn_no=VTRELEASE.regn_no\n"
                + "where VTSUSPEND.regn_no=?)";


        try {
            tmgr = new TransactionManager("SAVE SURRENDER DETAILS");
            if (!changedDataContents.isEmpty()) {
                updateRcReleaseSurrenderCancellantion(Integer.parseInt(actionCd), Integer.parseInt(purCd), statusDobj, changedDataContents, dobjSurr, tmgr, surOrSus);
            }
            if ((TableConstants.VM_ROLE_RC_SURRENDER_APPROVAL == Integer.parseInt(actionCd)
                    || TableConstants.VM_ROLE_RC_RELEASE_APPROVAL == Integer.parseInt(actionCd)
                    || TableConstants.VM_ROLE_RC_CANCELLATION_APPROVAL == Integer.parseInt(actionCd))
                    && !statusDobj.getStatus().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                if (changedDataContents.isEmpty()) {
                    insertVhaRcReleaseSurrenderCancellantion(statusDobj, tmgr, surOrSus);
                }
                if ((TableConstants.VM_MAST_RC_SURRENDER == Integer.parseInt(purCd))) {
                    if (surOrSus.equalsIgnoreCase(TableConstants.RC_INWARD_SURRENDER)) {
                        ps = tmgr.prepareStatement(vtRcSurrender);
                        ps.setString(1, Util.getUserStateCode());
                        ps.setInt(2, Util.getUserOffCode());
                        ps.setString(3, statusDobj.getAppl_no());
                        ps.setString(4, dobjSurr.getVehicleNo());
                        ps.setDate(5, (Date) getDate(dobjSurr.getSurrenderDate().toString()));
                        ps.setString(6, dobjSurr.getFileReferenceNo());
                        ps.setString(7, dobjSurr.getApprovedBy());
                        ps.setString(8, dobjSurr.getReason());
                        ps.setString(9, dobjSurr.getRcStatus());
                        ps.setString(10, dobjSurr.getRcNo());
                        ps.setString(11, dobjSurr.getPermitStatus());
                        ps.setString(12, dobjSurr.getPermitNo());
                        ps.setString(13, dobjSurr.getFitnessCerStatus());
                        ps.setString(14, dobjSurr.getFitnessCerNo());
                        ps.setString(15, dobjSurr.getTaxExampStatus());
                        ps.setDate(16, getDate(dobjSurr.getSurrenderDate().toString()));
                        ps.execute();
                        ps = null;
                        //for moving in history in vh_owner
                        ownerImpl.insertInVhOwnerFromVtOwner(statusDobj.getAppl_no(), statusDobj.getRegn_no(), tmgr);
                        //for moving in history in vh_owner
                        ps = tmgr.prepareStatement(sqlVtOwner);

                        ps.setString(1, TableConstants.VT_RC_SURRENDER_STATUS);
                        ps.setString(2, dobjSurr.getVehicleNo());
                        ps.setString(3, Util.getUserStateCode());
                        ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                        ps.executeUpdate();
                        ps = null;
                        ps = tmgr.prepareStatement(sqlVaRcSurrenderDel);
                        ps.setString(1, statusDobj.getAppl_no());
                        ps.execute();
                    } else if (surOrSus.equalsIgnoreCase(TableConstants.RC_INWARD_SUSPENSION)) {
                        ps = tmgr.prepareStatement(sqlVtRcSuspend);
                        ps.setString(1, statusDobj.getAppl_no());
                        ps.execute();
                        ps = null;
                        //for moving in history in vh_owner
                        ownerImpl.insertInVhOwnerFromVtOwner(statusDobj.getAppl_no(), statusDobj.getRegn_no(), tmgr);
                        //for moving in history in vh_owner
                        ps = tmgr.prepareStatement(sqlVtOwner);

                        ps.setString(1, TableConstants.VT_RC_SUSPEND_STATUS);
                        ps.setString(2, dobjSurr.getVehicleNo());
                        ps.setString(3, Util.getUserStateCode());
                        ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                        ps.executeUpdate();
                        ps = null;
                        ps = tmgr.prepareStatement(sqlVaRcSuspendDel);
                        ps.setString(1, statusDobj.getAppl_no());
                        ps.execute();
                    }

                    statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
                    ServerUtil.updateApprovedStatus(tmgr, statusDobj);
                } else if ((TableConstants.VM_MAST_RC_RELEASE == Integer.parseInt(purCd)) && (TableConstants.VM_ROLE_RC_RELEASE_APPROVAL == Integer.parseInt(actionCd))) {
                    if (vehicleStatus.equalsIgnoreCase(TableConstants.VT_RC_SURRENDER_STATUS)) {
                        ps = tmgr.prepareStatement(sqlVhRcSurrender);
                        ps.setString(1, Util.getEmpCode());
                        ps.setString(2, dobjSurr.getVehicleNo());

                        ps.executeUpdate();
                        ps = null;
                        ownerImpl.insertInVhOwnerFromVtOwner(statusDobj.getAppl_no(), statusDobj.getRegn_no(), tmgr);
                        ps = tmgr.prepareStatement(sqlVtOwner);
                        ps.setString(1, TableConstants.VT_RC_RELEASE_STATUS);
                        ps.setString(2, dobjSurr.getVehicleNo());
                        ps.setString(3, Util.getUserStateCode());
                        ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                        ps.executeUpdate();
                        statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
                        ServerUtil.updateApprovedStatus(tmgr, statusDobj);
                        ps = null;
                        ps = tmgr.prepareStatement(sqlVaRcReleaseDel);
                        ps.setString(1, statusDobj.getAppl_no());
                        ps.execute();
                        ps = null;
                        ps = tmgr.prepareStatement(vtRcSurrenderDel);
                        ps.setString(1, dobjSurr.getVehicleNo());
                        ps.execute();
                        statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
                        ServerUtil.updateApprovedStatus(tmgr, statusDobj);
                    } else if (vehicleStatus.equalsIgnoreCase(TableConstants.VT_RC_CANCEL_STATUS)) {
                        ps = tmgr.prepareStatement(sqlVhRcCancel);
                        ps.setString(1, Util.getEmpCode());
                        ps.setString(2, dobjSurr.getVehicleNo());

                        ps.executeUpdate();
                        ps = null;
                        ownerImpl.insertInVhOwnerFromVtOwner(statusDobj.getAppl_no(), statusDobj.getRegn_no(), tmgr);
                        ps = tmgr.prepareStatement(sqlVtOwner);
                        ps.setString(1, TableConstants.VT_RC_RELEASE_STATUS);
                        ps.setString(2, dobjSurr.getVehicleNo());
                        ps.setString(3, Util.getUserStateCode());
                        ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                        ps.executeUpdate();
                        statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
                        ServerUtil.updateApprovedStatus(tmgr, statusDobj);
                        ps = null;
                        ps = tmgr.prepareStatement(sqlVaRcReleaseDel);
                        ps.setString(1, statusDobj.getAppl_no());
                        ps.execute();
                        ps = null;
                        ps = tmgr.prepareStatement(vtRcCancelDel);
                        ps.setString(1, dobjSurr.getVehicleNo());
                        ps.execute();
                        statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
                        ServerUtil.updateApprovedStatus(tmgr, statusDobj);
                        insertForQRDetails(statusDobj.getAppl_no(), statusDobj.getRegn_no(), null, null, false, DocumentType.RC_QR, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tmgr);
                    } else if (vehicleStatus.equalsIgnoreCase(TableConstants.VT_RC_SUSPEND_STATUS)) {
                        ps = tmgr.prepareStatement(sqlVhRcSuspend);
                        ps.setString(1, Util.getEmpCode());
                        ps.setString(2, dobjSurr.getVehicleNo());

                        ps.executeUpdate();
                        ps = null;
                        ownerImpl.insertInVhOwnerFromVtOwner(statusDobj.getAppl_no(), statusDobj.getRegn_no(), tmgr);
                        ps = tmgr.prepareStatement(sqlVtOwner);
                        ps.setString(1, TableConstants.VT_RC_RELEASE_STATUS);
                        ps.setString(2, dobjSurr.getVehicleNo());
                        ps.setString(3, Util.getUserStateCode());
                        ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                        ps.executeUpdate();
                        statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
                        ServerUtil.updateApprovedStatus(tmgr, statusDobj);
                        ps = null;
                        ps = tmgr.prepareStatement(sqlVaRcReleaseDel);
                        ps.setString(1, statusDobj.getAppl_no());
                        ps.execute();
                        ps = null;
                        ps = tmgr.prepareStatement(vtRcSuspendDel);
                        ps.setString(1, dobjSurr.getVehicleNo());
                        ps.execute();
                        statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
                        ServerUtil.updateApprovedStatus(tmgr, statusDobj);
                    }
                } else if ((TableConstants.VM_MAST_RC_CANCELLATION == Integer.parseInt(purCd)) && (TableConstants.VM_ROLE_RC_CANCELLATION_APPROVAL == Integer.parseInt(actionCd))) {
                    ps = tmgr.prepareStatement(sqlVtRcCancellation);
                    ps.setString(1, statusDobj.getAppl_no());
                    ps.execute();
                    insertForQRDetails(statusDobj.getAppl_no(), statusDobj.getRegn_no(), null, null, false, DocumentType.RC_CANCEL_QR, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tmgr);
                    ps = null;

                    ps = tmgr.prepareStatement(sqlVaRcCancellationDel);
                    ps.setString(1, statusDobj.getAppl_no());
                    ps.execute();
                    ps = null;

                    ps = tmgr.prepareStatement(sqlVtOwner);
                    ps.setString(1, TableConstants.VT_RC_CANCEL_STATUS);
                    ps.setString(2, dobjSurr.getVehicleNo());
                    ps.setString(3, Util.getUserStateCode());
                    ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                    ps.execute();
                    ps = null;
                    statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
                    ServerUtil.updateApprovedStatus(tmgr, statusDobj);
                }
            }
            statusDobj = ServerUtil.webServiceForNextStage(statusDobj, tmgr);
            ServerUtil.fileFlow(tmgr, statusDobj);
            tmgr.commit();
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
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

    public void updateRcReleaseSurrenderCancellantion(int actionCd, int purCd, Status_dobj statusDobj, String changedDataContents, RcSurrenderReleaseCancellationDobj dobjSurr, TransactionManager tmgr, String surOrSus) throws SQLException, VahanException {
        PreparedStatement ps = null;
        int i = 0;
        try {
            insertVhaRcReleaseSurrenderCancellantion(statusDobj, tmgr, surOrSus);
            insertIntoVhaChangedData(tmgr, statusDobj.getAppl_no(), changedDataContents);
            if ((TableConstants.VM_MAST_RC_SURRENDER == statusDobj.getPur_cd()) && (TableConstants.VM_ROLE_RC_SURRENDER_VERIFICATION == statusDobj.getAction_cd() || TableConstants.VM_ROLE_RC_SURRENDER_APPROVAL == statusDobj.getAction_cd() || TableConstants.VM_ROLE_RC_SURRENDER_ENTRY == statusDobj.getAction_cd())) {
                if (surOrSus.equalsIgnoreCase(TableConstants.RC_INWARD_SURRENDER)) {
                    String sqlVaRcSurrender = "UPDATE " + TableList.VA_RC_SURRENDER + "\n"
                            + "   SET state_cd=?,off_cd=?,appl_no=?, regn_no=?, surr_dt=?, file_ref_no=?, approved_by=?, \n"
                            + "       reason=?, rc=?, rc_sno=?, permit=?, permit_sno=?, fc=?, fc_sno=?, \n"
                            + "       taxexem=?, op_dt=current_timestamp \n"
                            + " WHERE appl_no=?";
                    ps = tmgr.prepareStatement(sqlVaRcSurrender);
                    ps.setString(++i, Util.getUserStateCode());
                    ps.setInt(++i, Util.getSelectedSeat().getOff_cd());
                    ps.setString(++i, statusDobj.getAppl_no());
                    ps.setString(++i, dobjSurr.getVehicleNo());
                    ps.setDate(++i, (Date) getDate(dobjSurr.getSurrenderDate().toString()));
                    ps.setString(++i, dobjSurr.getFileReferenceNo());
                    ps.setString(++i, dobjSurr.getApprovedBy());
                    ps.setString(++i, dobjSurr.getReason());
                    ps.setString(++i, dobjSurr.getRcStatus());
                    ps.setString(++i, dobjSurr.getRcNo());
                    ps.setString(++i, dobjSurr.getPermitStatus());
                    ps.setString(++i, dobjSurr.getPermitNo());
                    ps.setString(++i, dobjSurr.getFitnessCerStatus());
                    ps.setString(++i, dobjSurr.getFitnessCerNo());
                    ps.setString(++i, dobjSurr.getTaxExampStatus());
                    ps.setString(++i, statusDobj.getAppl_no());
                    ps.executeUpdate();
                } else if (surOrSus.equalsIgnoreCase(TableConstants.RC_INWARD_SUSPENSION)) {
                    String sqlVaRcSurrender = "UPDATE " + TableList.VA_RC_SUSPEND + "\n"
                            + "   SET state_cd=?,off_cd=?,appl_no=?, regn_no=?, susp_dt=?, suspended_upto=?,file_ref_no=?, approved_by=?, \n"
                            + "    reason=? , op_dt=current_timestamp \n"
                            + " WHERE appl_no=?";
                    ps = tmgr.prepareStatement(sqlVaRcSurrender);
                    ps.setString(++i, Util.getUserStateCode());
                    ps.setInt(++i, Util.getSelectedSeat().getOff_cd());
                    ps.setString(++i, statusDobj.getAppl_no());
                    ps.setString(++i, dobjSurr.getVehicleNo());
                    ps.setDate(++i, (Date) getDate(dobjSurr.getSurrenderDate().toString()));
                    ps.setDate(++i, (Date) getDate(dobjSurr.getSuspendedUptoDate().toString()));
                    ps.setString(++i, dobjSurr.getFileReferenceNo());
                    ps.setString(++i, dobjSurr.getApprovedBy());
                    ps.setString(++i, dobjSurr.getReason());
                    ps.setString(++i, statusDobj.getAppl_no());
                    ps.executeUpdate();
                }
            } else if ((TableConstants.VM_MAST_RC_RELEASE == statusDobj.getPur_cd()) && (TableConstants.VM_ROLE_RC_RELEASE_VERIFICATION == statusDobj.getAction_cd() || TableConstants.VM_ROLE_RC_RELEASE_APPROVAL == statusDobj.getAction_cd() || TableConstants.VM_ROLE_RC_RELEASE_ENTRY == statusDobj.getAction_cd())) {
                String vaRcRelease = "UPDATE " + TableList.VA_RC_RELEASE + "\n"
                        + "   SET state_cd=?,off_cd=?, appl_no=?, regn_no=?, release_dt=?, rel_file_ref_no=?, rel_approved_by=?, \n"
                        + "       rel_op_dt=current_timestamp \n"
                        + " WHERE appl_no=?";
                ps = tmgr.prepareStatement(vaRcRelease);
                ps.setString(++i, Util.getUserStateCode());
                ps.setInt(++i, Util.getSelectedSeat().getOff_cd());
                ps.setString(++i, statusDobj.getAppl_no());
                ps.setString(++i, dobjSurr.getVehicleNo());
                ps.setDate(++i, (Date) getDate(dobjSurr.getReleaseDate().toString()));
                ps.setString(++i, dobjSurr.getReleaseFileReferenceNo());
                ps.setString(++i, dobjSurr.getReleaseApprovedBy());
                ps.setString(++i, statusDobj.getAppl_no());
                ps.executeUpdate();
            } else if ((TableConstants.VM_MAST_RC_CANCELLATION == statusDobj.getPur_cd()) && (TableConstants.VM_ROLE_RC_CANCELLATION_VERIFICATION == statusDobj.getAction_cd() || TableConstants.VM_ROLE_RC_CANCELLATION_APPROVAL == statusDobj.getAction_cd() || TableConstants.VM_ROLE_RC_CANCELLATION_ENTRY == statusDobj.getAction_cd()
                    || TableConstants.VM_ROLE_RC_CANCELLATION_INSPECTION == statusDobj.getAction_cd())) {
                String sqlvaRCCancel = "UPDATE " + TableList.VA_RC_CANCEL + "\n"
                        + "   SET state_cd=?,off_cd=?, appl_no=?, regn_no=?, cancel_dt=?, file_ref_no=?, approved_by=?, \n"
                        + "       reason=?, op_dt=current_timestamp,requested_by=?\n"
                        + " WHERE appl_no=?";
                ps = tmgr.prepareStatement(sqlvaRCCancel);
                ps.setString(++i, Util.getUserStateCode());
                ps.setInt(++i, Util.getSelectedSeat().getOff_cd());
                ps.setString(++i, statusDobj.getAppl_no());
                ps.setString(++i, dobjSurr.getVehicleNo());
                ps.setDate(++i, (Date) getDate(dobjSurr.getCancellationDate().toString()));
                ps.setString(++i, dobjSurr.getCancellationFileReferenceNo());
                ps.setString(++i, dobjSurr.getCancellationApprovedBy());
                ps.setString(++i, dobjSurr.getCancellationReason());
                ps.setInt(++i, dobjSurr.getRcCancelBy());
                ps.setString(++i, statusDobj.getAppl_no());
                ps.executeUpdate();
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    //VHA 
    public static void insertVhaRcReleaseSurrenderCancellantion(Status_dobj statusDobj, TransactionManager tmgr, String surOrSus) throws SQLException, VahanException {
        PreparedStatement ps = null;
        String sqlQry = "";
        try {
            if ((TableConstants.VM_MAST_RC_SURRENDER == statusDobj.getPur_cd())) {
                if (surOrSus.equalsIgnoreCase(TableConstants.RC_INWARD_SURRENDER)) {
                    sqlQry = "INSERT INTO " + TableList.VHA_RC_SURRENDER + "(SELECT current_timestamp + interval '1 second' as moved_on,? as moved_by ,state_cd,off_cd,appl_no, regn_no, "
                            + "surr_dt, file_ref_no, approved_by, reason, \n"
                            + "       rc, rc_sno, permit, permit_sno, fc, fc_sno, taxexem,"
                            + " op_dt\n"
                            + "  FROM " + TableList.VA_RC_SURRENDER + " where appl_no=?)";
                } else if (surOrSus.equalsIgnoreCase(TableConstants.RC_INWARD_SUSPENSION)) {
                    sqlQry = "INSERT INTO " + TableList.VHA_RC_SUSPEND + "(SELECT current_timestamp + interval '1 second' as moved_on,? as moved_by ,state_cd,off_cd,appl_no, regn_no, "
                            + "susp_dt, file_ref_no, approved_by, reason, \n"
                            + " op_dt,suspended_upto\n"
                            + "  FROM " + TableList.VA_RC_SUSPEND + " where appl_no=?)";
                }
            } else if ((TableConstants.VM_MAST_RC_RELEASE == statusDobj.getPur_cd())) {
                sqlQry = "INSERT INTO " + TableList.VHA_RC_RELEASE + ""
                        + "(SELECT current_timestamp + interval '1 second' as moved_on,? as moved_by ,state_cd,off_cd, appl_no, regn_no, release_dt, rel_file_ref_no, rel_approved_by,"
                        + " rel_op_dt\n"
                        + "FROM " + TableList.VA_RC_RELEASE + " where appl_no=?)";
            } else if ((TableConstants.VM_MAST_RC_CANCELLATION == statusDobj.getPur_cd())) {
                sqlQry = "INSERT INTO " + TableList.VHA_RC_CANCEL + "(SELECT current_timestamp + interval '1 second' as moved_on,? as moved_by ,state_cd ,off_cd, appl_no, regn_no, cancel_dt, file_ref_no, "
                        + "  approved_by, reason, \n"
                        + "  op_dt,requested_by \n"
                        + "  FROM " + TableList.VA_RC_CANCEL + " where appl_no=?\n"
                        + "   )";
            }
            ps = tmgr.prepareStatement(sqlQry);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, statusDobj.getAppl_no());
            ps.execute();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public static void insertVhaRcReleaseSurrenderCancellantionDispose(Status_dobj statusDobj, TransactionManager tmgr) throws SQLException, VahanException {
        PreparedStatement ps = null;
        String sqlQry = "";
        try {
            if ((TableConstants.VM_MAST_RC_SURRENDER == statusDobj.getPur_cd())) {
                String sqlSelect = "select * from " + TableList.VA_RC_SURRENDER + " where appl_no=?";
                ps = tmgr.prepareStatement(sqlSelect);
                ps.setString(1, statusDobj.getAppl_no());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    sqlQry = "INSERT INTO " + TableList.VHA_RC_SURRENDER + "(SELECT current_timestamp + interval '1 second' as moved_on,? as moved_by ,state_cd,off_cd,appl_no, regn_no, "
                            + "surr_dt, file_ref_no, approved_by, reason, \n"
                            + "       rc, rc_sno, permit, permit_sno, fc, fc_sno, taxexem,"
                            + " op_dt\n"
                            + "  FROM " + TableList.VA_RC_SURRENDER + " where appl_no=?)";
                } else {
                    sqlQry = "INSERT INTO " + TableList.VHA_RC_SUSPEND + "(SELECT current_timestamp + interval '1 second' as moved_on,? as moved_by ,state_cd,off_cd,appl_no, regn_no, "
                            + "susp_dt, file_ref_no, approved_by, reason, \n"
                            + " op_dt, suspended_upto\n"
                            + "  FROM " + TableList.VA_RC_SUSPEND + " where appl_no=?)";
                }

            } else if ((TableConstants.VM_MAST_RC_RELEASE == statusDobj.getPur_cd())) {
                sqlQry = "INSERT INTO " + TableList.VHA_RC_RELEASE + ""
                        + "(SELECT current_timestamp + interval '1 second' as moved_on,? as moved_by ,state_cd,off_cd, appl_no, regn_no, release_dt, rel_file_ref_no, rel_approved_by,"
                        + " rel_op_dt\n"
                        + "FROM " + TableList.VA_RC_RELEASE + " where appl_no=?)";
            } else if ((TableConstants.VM_MAST_RC_CANCELLATION == statusDobj.getPur_cd())) {
                sqlQry = "INSERT INTO " + TableList.VHA_RC_CANCEL + "(SELECT current_timestamp + interval '1 second' as moved_on,? as moved_by ,state_cd ,off_cd, appl_no, regn_no, cancel_dt, file_ref_no, "
                        + "  approved_by, reason, \n"
                        + "  op_dt \n"
                        + "  FROM " + TableList.VA_RC_CANCEL + " where appl_no=?\n"
                        + "   )";
            }
            ps = tmgr.prepareStatement(sqlQry);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, statusDobj.getAppl_no());
            ps.execute();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }
    //VHA

    public boolean saveNoStatusUpdate(int actionCode, int purCode, Status_dobj statusDobj, String changedDataContents, RcSurrenderReleaseCancellationDobj dobj, String surOrSus) throws VahanException {
        TransactionManager tmgr = null;
        boolean checkExistance = false;
        boolean isSaved = true;
        try {
            tmgr = new TransactionManager("only save no status update");
            checkExistance = checkVehicleRCDetailsAleadyExist(actionCode, purCode, statusDobj, dobj, tmgr, surOrSus);

            if (!checkExistance) {
                isSaved = saveForRcSurrenderReleaseCancellation(actionCode, purCode, statusDobj, changedDataContents, dobj, tmgr, surOrSus);
            }
            if (checkExistance) {
                if (!changedDataContents.isEmpty()) {
                    updateRcReleaseSurrenderCancellantion(actionCode, purCode, statusDobj, changedDataContents, dobj, tmgr, surOrSus);
                }
            }
            tmgr.commit();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);

        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);

            }
        }
        return isSaved;
    }

    public boolean rcDataSave(int actionCode, int purCode, Status_dobj status, String changedDataContents, RcSurrenderReleaseCancellationDobj dobj, String surOrSus) throws VahanException {
        TransactionManager tmgr = null;
        boolean checkExistance = false;
        boolean falg = false;
        try {
            tmgr = new TransactionManager("Save When Move And Approve");
            checkExistance = checkVehicleRCDetailsAleadyExist(actionCode, purCode, status, dobj, tmgr, surOrSus);
            if (checkExistance) {
                if (!changedDataContents.isEmpty()) {
                    updateRcReleaseSurrenderCancellantion(actionCode, purCode, status, changedDataContents, dobj, tmgr, surOrSus);
                }
            }
            if (!checkExistance) {
                saveForRcSurrenderReleaseCancellation(actionCode, purCode, status, changedDataContents, dobj, tmgr, surOrSus);
            }
            if (purCode == TableConstants.VM_MAST_RC_CANCELLATION || (purCode == TableConstants.VM_MAST_RC_SURRENDER && surOrSus == "rcSus")) {
                status.getVehicleParameters().setNOC_RETENTION(dobj.getRcCancelBy());
            }
            status = ServerUtil.webServiceForNextStage(status, tmgr);
            ServerUtil.fileFlow(tmgr, status);
            falg = true;
            tmgr.commit();
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);

        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);

            }
        }
        return falg;

    }

    private boolean checkVehicleRCDetailsAleadyExist(int actionCode, int purCode, Status_dobj status, RcSurrenderReleaseCancellationDobj dobj, TransactionManager tmgr, String surOrSus) throws SQLException, VahanException {
        boolean flag = false;
        PreparedStatement ps = null;
        try {
            if (TableConstants.VM_MAST_RC_SURRENDER == purCode) {
                if (surOrSus.equalsIgnoreCase(TableConstants.RC_INWARD_SURRENDER)) {
                    String vaRcSurrender = "select * from " + TableList.VA_RC_SURRENDER + " where appl_no=? and regn_no=?";
                    ps = tmgr.prepareStatement(vaRcSurrender);
                    ps.setString(1, status.getAppl_no());
                    ps.setString(2, status.getRegn_no());
                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        flag = true;
                    }
                } else if (surOrSus.equalsIgnoreCase(TableConstants.RC_INWARD_SUSPENSION)) {
                    String vaRcSurrender = "select * from " + TableList.VA_RC_SUSPEND + " where appl_no=? and regn_no=?";
                    ps = tmgr.prepareStatement(vaRcSurrender);
                    ps.setString(1, status.getAppl_no());
                    ps.setString(2, status.getRegn_no());
                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        flag = true;
                    }
                }
            }
            if (TableConstants.VM_MAST_RC_RELEASE == purCode) {
                String vaRcSurrender = "select * from " + TableList.VA_RC_RELEASE + " where appl_no=? and regn_no=?";
                ps = tmgr.prepareStatement(vaRcSurrender);
                ps.setString(1, status.getAppl_no());
                ps.setString(2, status.getRegn_no());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    flag = true;
                }
            }
            if (TableConstants.VM_MAST_RC_CANCELLATION == purCode) {

                String vaRcCancel = "select * from " + TableList.VA_RC_CANCEL + " where appl_no=? and regn_no=?";
                ps = tmgr.prepareStatement(vaRcCancel);
                ps.setString(1, status.getAppl_no());
                ps.setString(2, status.getRegn_no());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    flag = true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

        return flag;

    }

    public void reback(String actionCode, String purCode, Status_dobj status, String changedDataContents, RcSurrenderReleaseCancellationDobj dobj, String surOrSus) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("movetoapprove");
            if (!changedDataContents.isEmpty()) {
                updateRcReleaseSurrenderCancellantion(Integer.parseInt(actionCode), Integer.parseInt(purCode), status, changedDataContents, dobj, tmgr, surOrSus);
            }
            status = ServerUtil.webServiceForNextStage(status, tmgr);
            ServerUtil.fileFlow(tmgr, status);
            tmgr.commit();
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
    }
}
