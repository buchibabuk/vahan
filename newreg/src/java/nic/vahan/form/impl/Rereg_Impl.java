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
import nic.vahan.CommonUtils.Utility;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Rereg_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.common.Appl_Details_Dobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.NewVehicleNo;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;

public class Rereg_Impl {

    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Rereg_Impl.class);

    public Rereg_dobj set_dobj_from_db(String appl_no, int role_code) throws VahanException {


        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        Rereg_dobj dobj = null;

        try {
            tmgr = new TransactionManager("Rereg_Impl.set_dobj_from_db");
            ps = tmgr.prepareStatement("select * from " + " va_re_assign " + "where appl_no=?");
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                dobj = new Rereg_dobj();
                dobj.setAppl_no(appl_no);
                dobj.setOld_regn_no(rs.getString("old_regn_no"));
                dobj.setNew_regn_no(rs.getString("new_regn_no"));
                dobj.setReason(rs.getString("reason"));
                dobj.setOp_dt(rs.getDate("op_dt"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setPmt_type(rs.getInt("pmt_type"));
                dobj.setPmt_catg(rs.getInt("pmt_catg"));
                dobj.setOddEvenOpted(rs.getBoolean("oddevenopted"));
            }


        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(ex.getMessage());
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

//    public String getOldRegNo(String appl_no) throws VahanException {
//        String regn_no = null;
//        TransactionManager tmgr = null;
//        PreparedStatement ps = null;
//        RowSet rs = null;
//
//        try {
//
//            tmgr = new TransactionManager("getOldRegNo");
//            ps = tmgr.prepareStatement("select vt_fee.* from  " + TableList.VT_OWNER
//                    + " vt_owner ,vt_fee,vp_appl_rcpt_mapping "
//                    + " where vt_fee.regn_no=vt_owner.regn_no and vp_appl_rcpt_mapping.rcpt_no=vt_fee.rcpt_no"
//                    + " and vp_appl_rcpt_mapping.appl_no=? and vt_fee.pur_cd=?");
//            ps.setString(1, appl_no);
//            ps.setInt(2, TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO);
//            // ResultSet rs = ps.executeQuery();
//            rs = tmgr.fetchDetachedRowSet();
//
//            if (rs.next()) {
//                regn_no = rs.getString("regn_no");
//            } else {
//                throw new VahanMessageException("Vehicle Not Registered");
//            }
//
//        } catch (Exception e) {
//            throw new VahanException(e.getMessage());
//        } finally {
//            try {
//                if (tmgr != null) {
//                    tmgr.release();
//                }
//            } catch (Exception e) {
//                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//            }
//        }
//
//        return regn_no;
//
//    }
    public void update_ReReg_Status(int role, Rereg_dobj dobj, Status_dobj status_dobj, String changedData, Appl_Details_Dobj sessionVariable) throws VahanException, Exception {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;

        try {

            tmgr = new TransactionManager("update_ReReg_Status");
            //=================WEB SERVICES FOR NEXTSTAGE START=========//
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ToImpl to = new ToImpl();
            if (role == TableConstants.TM_ROLE_ENTRY
                    || role == TableConstants.TM_ROLE_VERIFICATION
                    || role == TableConstants.TM_ROLE_APPROVAL) {
                if (role == TableConstants.TM_ROLE_ENTRY || role == TableConstants.TM_ROLE_VERIFICATION && dobj.getNew_regn_no() != null) {
                    if (dobj.isAdvRegnNo()) {
                        updateAdvanceRegNoDetails(dobj.getAppl_no(), dobj.getNew_regn_no(), tmgr);
                    } else if (dobj.isRetRegnNo()) {
                        updateRetentionRegNoDetails(dobj.getAppl_no(), dobj.getNew_regn_no(), tmgr);
                    }
                    if (role == TableConstants.TM_ROLE_VERIFICATION && !CommonUtils.isNullOrBlank(dobj.getNew_regn_no())) {
                        to.insertVaStopDupRegnNoGen(dobj.getAppl_no(), dobj.getOld_regn_no(), dobj.getNew_regn_no(), TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO, tmgr);
                    }
                }
                if (role == TableConstants.TM_ROLE_ENTRY) {
                    insertUpdateReReg(tmgr, dobj.getAppl_no(), dobj);
                } else if (!changedData.isEmpty() && (role == TableConstants.TM_ROLE_VERIFICATION
                        || role == TableConstants.TM_ROLE_APPROVAL)) {
                    insertUpdateReReg(tmgr, dobj.getAppl_no(), dobj);
                }

            }


            if (role == TableConstants.TM_ROLE_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                ServerUtil.checkApprovalStatusOfAppls(tmgr, dobj.getAppl_no(), TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO, TableConstants.VM_TRANSACTION_MAST_FIT_CERT);
                ServerUtil.checkApprovalStatusOfAppls(tmgr, dobj.getAppl_no(), TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO, TableConstants.VM_TRANSACTION_MAST_TO);
                //Approve Insurance
                InsImpl.approvalInsurance(tmgr, dobj.getOld_regn_no(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), sessionVariable.getCurrentEmpCd());

                if (Utility.isNullOrBlank(dobj.getNew_regn_no())) {
                    NewVehicleNo newVehicleNo = new NewVehicleNo();
                    String regn_no = newVehicleNo.generateAssignNewRegistrationNo(sessionVariable.getCurrent_off_cd(), sessionVariable.getCurrent_action_cd(), dobj.getAppl_no(), null, 1, null,null, tmgr);
                    dobj.setNew_regn_no(regn_no);
                }

                updateFeeAndTaxForAdvanceRegnNo(tmgr, dobj);
                SwappingRegnImpl.updateTablesForRetention(tmgr, dobj.getOld_regn_no(), dobj.getNew_regn_no());
                to.insertIntoVhaVaStopDupRegnNoHistory(tmgr, dobj.getAppl_no(), sessionVariable);

                String sql = "INSERT INTO " + TableList.VH_RE_ASSIGN + "("
                        + "            state_cd, off_cd, appl_no, old_regn_no, new_regn_no, reason,"
                        + "            moved_on, moved_by)"
                        + "    VALUES (?, ?, ?, ?, ?, ?,"
                        + "            current_timestamp, ?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, sessionVariable.getCurrent_state_cd());
                ps.setInt(2, sessionVariable.getCurrent_off_cd());
                ps.setString(3, dobj.getAppl_no());
                ps.setString(4, dobj.getOld_regn_no());
                ps.setString(5, dobj.getNew_regn_no());
                ps.setString(6, dobj.getReason());
                ps.setString(7, sessionVariable.getCurrentEmpCd());
                ps.executeUpdate();

                //updation of vt_owner
                sql = "UPDATE " + TableList.VT_OWNER + " SET regn_no=?, op_dt=? "
                        + "WHERE regn_no=? and state_cd = ? and off_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getNew_regn_no());
                ps.setDate(2, new java.sql.Date(new java.util.Date().getTime()));
                ps.setString(3, dobj.getOld_regn_no());
                ps.setString(4, sessionVariable.getCurrent_state_cd());
                ps.setInt(5, sessionVariable.getCurrent_off_cd());
                ps.executeUpdate();
                if (!CommonUtils.isNullOrBlank(dobj.getNew_regn_no())) {
                    OwnerImpl.insertUpdateFastagSchedular(dobj.getAppl_no(), sessionVariable.getCurrent_state_cd(), sessionVariable.getCurrent_off_cd(), dobj.getOld_regn_no(),
                            sessionVariable.getCurrent_state_cd(), sessionVariable.getCurrent_off_cd(), dobj.getNew_regn_no(), sessionVariable.getChasi_no(), tmgr);
                }
                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                //for updating the status of application when it is approved end

                //For New Vehicle HSRP Details
                if (",WB,RJ,".contains("," + Util.getUserStateCode() + ",")) {
                    if (!ServerUtil.verifyForOldVehicleHsrp(sessionVariable.getCurrent_state_cd(), sessionVariable.getCurrent_off_cd(), tmgr)) {
                        ServerUtil.verifyInsertNewRegHsrpDetail(dobj.getAppl_no(), dobj.getNew_regn_no(), TableConstants.HSRP_NEW_BOTH_SIDE,
                                sessionVariable.getCurrent_state_cd(), sessionVariable.getCurrent_off_cd(), tmgr);
                    }
                }
                //SmartCard Or Print
                ServerUtil.VerifyInsertSmartCardPrintDetail(dobj.getAppl_no(), dobj.getNew_regn_no(),
                        sessionVariable.getCurrent_state_cd(), sessionVariable.getCurrent_off_cd(), status_dobj.getPur_cd(), tmgr);
            }

            insertIntoVhaChangedData(tmgr, dobj.getAppl_no(), changedData);//for saving the data into table those are changed by the user

            ServerUtil.fileFlow(tmgr, status_dobj); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();//Commiting data here....

        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException("Re Registration : Error in Database Update");
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Re Registration");
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }

    public static void insertUpdateReReg(TransactionManager tmgr, String appl_no, Rereg_dobj rereg_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "SELECT appl_no FROM va_re_assign where appl_no = ? and state_cd =?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoReRegHistory(tmgr, appl_no);
                updateReReg(tmgr, appl_no, rereg_dobj);
            } else {
                insertIntoReReg(tmgr, appl_no, rereg_dobj);
            }

        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
        }


    }

    public static void insertIntoReReg(TransactionManager tmgr, String appl_no, Rereg_dobj rereg_dobj) throws VahanException, SQLException {

        String sql = "INSERT INTO va_re_assign(appl_no ,  old_regn_no , new_regn_no ,  reason ,  op_dt ,  state_cd ,  off_cd, pmt_type, pmt_catg ,oddevenopted)"
                + "    VALUES (?, ?, ?, ?, current_timestamp,?, ?, ? , ?,?)";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, appl_no);
        ps.setString(2, rereg_dobj.getOld_regn_no());
        ps.setString(3, rereg_dobj.getNew_regn_no());
        ps.setString(4, rereg_dobj.getReason());
        ps.setString(5, Util.getUserStateCode());
        ps.setInt(6, Util.getSelectedSeat().getOff_cd());
        ps.setInt(7, rereg_dobj.getPmt_type());
        ps.setInt(8, rereg_dobj.getPmt_catg());
        ps.setBoolean(9, rereg_dobj.isOddEvenOpted());

        ps.executeUpdate();

    }

    public static void updateReReg(TransactionManager tmgr, String appl_no, Rereg_dobj rereg_dobj) throws VahanException, SQLException {

        String sql = "update va_re_assign "
                + " set  old_regn_no=? , new_regn_no=? ,  reason=? ,  op_dt=current_timestamp ,  state_cd=? ,  off_cd=?, pmt_type = ?, pmt_catg = ?,oddevenopted = ? "
                + "  where appl_no=?";
        PreparedStatement ps = tmgr.prepareStatement(sql);

        ps.setString(1, rereg_dobj.getOld_regn_no());
        ps.setString(2, rereg_dobj.getNew_regn_no());
        ps.setString(3, rereg_dobj.getReason());
        ps.setString(4, Util.getUserStateCode());
        ps.setInt(5, Util.getSelectedSeat().getOff_cd());
        ps.setInt(6, rereg_dobj.getPmt_type());
        ps.setInt(7, rereg_dobj.getPmt_catg());
        ps.setBoolean(8, rereg_dobj.isOddEvenOpted());
        ps.setString(9, appl_no);

        ps.executeUpdate();

    }

    public static void insertIntoReRegHistory(TransactionManager tmgr, String appl_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        try {
            //inserting data into vha_ca from va_ca
            sql = "INSERT INTO vha_re_assign "
                    + " SELECT appl_no, old_regn_no, new_regn_no, reason,"
                    + "        op_dt,current_timestamp as moved_on,? as moved_by,state_cd, off_cd, pmt_type, pmt_catg,oddevenopted "
                    + "  FROM  va_re_assign"
                    + " WHERE  appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();

        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
        }
    }

    public static void saveChangeReReg(Rereg_dobj rereg_dobj, String changedata) throws VahanException, Exception {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveChangeReReg");
            insertUpdateReReg(tmgr, rereg_dobj.getAppl_no(), rereg_dobj);
            ComparisonBeanImpl.updateChangedData(rereg_dobj.getAppl_no(), changedata, tmgr);
            tmgr.commit();
        } catch (SQLException ve) {
            throw new VahanException(ve.getMessage());
        } catch (VahanException ve) {
            throw new VahanException(ve.getMessage());
        } catch (Exception ve) {
            throw new VahanException(ve.getMessage());
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }

    public static void updateAdvanceRegNoDetails(String applNo, String regnNo, TransactionManager tmgr) throws Exception {
        PreparedStatement ps;
        RowSet rs;
        String sql = null;
        sql = "SELECT regn_appl_no from " + TableList.VT_ADVANCE_REGN_NO + " where  regn_no=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regnNo);
        rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            String RegnApplNo = rs.getString("regn_appl_no");
            if (CommonUtils.isNullOrBlank(RegnApplNo)) {
                sql = "Update " + TableList.VT_ADVANCE_REGN_NO
                        + " set regn_appl_no=? where regn_no=? and (regn_appl_no is null or length(regn_appl_no) = 0)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
                ps.setString(2, regnNo);
                int count = ps.executeUpdate();
                if (count == 0) {
                    throw new VahanException("This Fancy Registration Number already used by another application.");
                }
            }
        }
    }

    public void updateFeeAndTaxForAdvanceRegnNo(TransactionManager tmgr, Rereg_dobj dobj) throws SQLException {
        PreparedStatement ps = null;
        // for updating va_details for newreg generation
        String sql = "update " + TableList.VA_DETAILS + " set regn_no=? where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getNew_regn_no());
        ps.setString(2, dobj.getAppl_no());
        ps.executeUpdate();


        sql = "update " + TableList.VA_INSURANCE + " set regn_no=?,op_dt = current_timestamp where appl_no=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getNew_regn_no());
        ps.setString(2, dobj.getAppl_no());
        ps.executeUpdate();

        sql = "UPDATE " + TableList.VT_TAX + " set regn_no= ? WHERE regn_no = ? and state_cd = ? and off_cd = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getNew_regn_no());
        ps.setString(2, dobj.getOld_regn_no());
        ps.setString(3, Util.getUserStateCode());
        ps.setInt(4, Util.getSelectedSeat().getOff_cd());
        ps.executeUpdate();
        
        sql = "UPDATE " + TableList.VT_REFUND_EXCESS + " set regn_no= ? WHERE regn_no = ? and state_cd = ? and off_cd = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getNew_regn_no());
        ps.setString(2, dobj.getOld_regn_no());
        ps.setString(3, Util.getUserStateCode());
        ps.setInt(4, Util.getSelectedSeat().getOff_cd());
        ps.executeUpdate();

        sql = "UPDATE  " + TableList.VT_TAX_BASED_ON
                + " set regn_no= ? WHERE regn_no=? and state_cd = ? and off_cd = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getNew_regn_no());
        ps.setString(2, dobj.getOld_regn_no());
        ps.setString(3, Util.getUserStateCode());
        ps.setInt(4, Util.getSelectedSeat().getOff_cd());
        ps.executeUpdate();

        sql = "UPDATE " + TableList.VT_FEE + " set regn_no= ? WHERE regn_no=? and state_cd = ? and off_cd = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getNew_regn_no());
        ps.setString(2, dobj.getOld_regn_no());
        ps.setString(3, Util.getUserStateCode());
        ps.setInt(4, Util.getSelectedSeat().getOff_cd());
        ps.executeUpdate();

        sql = "update " + TableList.VA_FC_PRINT + " set regn_no=? where regn_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getNew_regn_no());
        ps.setString(2, dobj.getOld_regn_no());
        ps.executeUpdate();
    }

    private void updateRetentionRegNoDetails(String applNo, String regnNo, TransactionManager tmgr) throws SQLException, VahanException {
        PreparedStatement ps;
        RowSet rs;
        String sql = null;
        sql = "SELECT regn_appl_no from " + TableList.VT_SURRENDER_RETENTION + " where  old_regn_no=? and (regn_appl_no is null or length(regn_appl_no) = 0)";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regnNo);
        rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            String RegnApplNo = rs.getString("regn_appl_no");
            if (CommonUtils.isNullOrBlank(RegnApplNo)) {
                sql = "Update " + TableList.VT_SURRENDER_RETENTION
                        + " set regn_appl_no=? where old_regn_no=? and (regn_appl_no is null or length(regn_appl_no) = 0)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
                ps.setString(2, regnNo);
                int count = ps.executeUpdate();
                if (count == 0) {
                    throw new VahanException("This Fancy Registration Number already used by another application.");
                }
            }
        }
    }

    public String oddEvenOpted(String applNo, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps;
        RowSet rs;
        String oldRegnNo = null;
        String sql = "SELECT old_regn_no from va_re_assign where oddevenopted = ? and  appl_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setBoolean(1, true);
        ps.setString(2, applNo);
        rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            oldRegnNo = rs.getString("old_regn_no");
        }
        return oldRegnNo;
    }
}
