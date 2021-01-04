/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.bean.fancy.RetenRegnNo_dobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.common.Appl_Details_Dobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.NewVehicleNo;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.fileFlow;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

public class RetentionImpl {

    private static final Logger LOGGER = Logger.getLogger(RetentionImpl.class);

    public RetenRegnNo_dobj getVaSurrenderRetentionDetails(String appl_no) throws VahanException {

        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        RetenRegnNo_dobj retenRegnNoDobj = null;
        try {

            tmgr = new TransactionManagerReadOnly("getVaSurrenderRetentionDetails");

            sql = "SELECT *  FROM " + TableList.VA_SURRENDER_RETENTION + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                retenRegnNoDobj = new RetenRegnNo_dobj();
                retenRegnNoDobj.setAppl_no(rs.getString("appl_no"));
                retenRegnNoDobj.setRegn_no(rs.getString("regn_no"));
                retenRegnNoDobj.setState_cd(rs.getString("state_cd"));
                retenRegnNoDobj.setOff_cd(rs.getInt("off_cd"));
                retenRegnNoDobj.setReason(rs.getString("reason"));
                retenRegnNoDobj.setAppl_date(rs.getDate("dt_of_app"));
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during fetching details of retention of registration no");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return retenRegnNoDobj;
    }

    public String insertIntoVtSurrenderRetention(TransactionManager tmgr, Owner_dobj ownerDobj, String reason, String applNo, Date applDate, String stateCd, int offCd, int actionCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        String newRegnNo = null;
        String rcptNo = null;
        int pos = 1;

        try {
            sql = "SELECT rcpt_no FROM " + TableList.VT_FEE + " WHERE regn_no=? and pur_cd = ? and state_cd =? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getRegn_no());
            ps.setInt(2, TableConstants.SWAPPING_REGN_PUR_CD);
            ps.setString(3, stateCd);
            ps.setInt(4, offCd);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                rcptNo = rs.getString("rcpt_no");
            }

            if (rcptNo == null || rcptNo.trim().equalsIgnoreCase("")) {
                throw new VahanException("Can't Approve as Fees is not Paid for Fancy No Surrender Retention.");
            }

            NewVehicleNo newVehicleNo = new NewVehicleNo();
            newRegnNo = newVehicleNo.generateAssignNewRegistrationNo(offCd,
                    actionCd, applNo, ownerDobj.getRegn_no(), 1, null, null, tmgr);
            if (newRegnNo == null || newRegnNo.trim().equalsIgnoreCase("")) {
                throw new VahanException("Can't Approved because of New Vehicle No Generation is Failed.");
            }

            sql = "INSERT INTO " + TableList.VT_SURRENDER_RETENTION
                    + "  ( state_cd, off_cd, regn_appl_no, old_regn_no, new_regn_no, owner_name, f_name, c_add1, c_add2, "
                    + "    c_add3, c_district, c_pincode, c_state, vh_class, rcpt_no, mobile_no, surr_dt, "
                    + "    file_no, reason, dt_of_app, approved_by, op_dt) "
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = tmgr.prepareStatement(sql);

            ps.setString(pos++, stateCd);
            ps.setInt(pos++, offCd);
            ps.setNull(pos++, java.sql.Types.VARCHAR);
            ps.setString(pos++, ownerDobj.getRegn_no());
            ps.setString(pos++, newRegnNo);
            ps.setString(pos++, ownerDobj.getOwner_name());
            ps.setString(pos++, ownerDobj.getF_name());
            ps.setString(pos++, ownerDobj.getC_add1());
            ps.setString(pos++, ownerDobj.getC_add2());
            ps.setString(pos++, ownerDobj.getC_add3());
            ps.setInt(pos++, ownerDobj.getC_district());
            ps.setInt(pos++, ownerDobj.getC_pincode());
            ps.setString(pos++, ownerDobj.getC_state());
            ps.setInt(pos++, ownerDobj.getVh_class());
            ps.setString(pos++, rcptNo);
            if (ownerDobj.getOwner_identity().getMobile_no() != null) {
                ps.setLong(pos++, ownerDobj.getOwner_identity().getMobile_no());
            } else {
                ps.setLong(pos++, 0);
            }
            ps.setDate(pos++, new java.sql.Date(new Date().getTime()));//surrender date is the approval date
            ps.setNull(pos++, java.sql.Types.VARCHAR);//file no is not known so currently putting it as NULL
            ps.setString(pos++, reason);
            ps.setDate(pos++, new java.sql.Date(applDate.getTime()));
            ps.setString(pos++, Util.getEmpCode());
            ps.setTimestamp(pos++, ServerUtil.getSystemDateInPostgres());
            ps.executeUpdate();

            SwappingRegnImpl.updateTablesForRetention(tmgr, ownerDobj.getRegn_no(), newRegnNo);

            //HSRP
            ServerUtil.verifyInsertNewRegHsrpDetail(applNo, newRegnNo, TableConstants.HSRP_NEW_BOTH_SIDE, stateCd, offCd, tmgr);

        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

        return newRegnNo;
    }

    public void insertIntoVaSurrenderRetention(TransactionManager tmgr, RetenRegnNo_dobj retenRegnNoDobj, String empCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "INSERT INTO " + TableList.VA_SURRENDER_RETENTION
                    + " (state_cd,off_cd,appl_no, regn_no, file_no, reason, dt_of_app, approved_by, op_dt)"
                    + " VALUES (?, ?,?, ?, ?, ?, ?, ?, current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, retenRegnNoDobj.getState_cd());
            ps.setInt(2, retenRegnNoDobj.getOff_cd());
            ps.setString(3, retenRegnNoDobj.getAppl_no());
            ps.setString(4, retenRegnNoDobj.getRegn_no());
            ps.setNull(5, java.sql.Types.VARCHAR);//file no is not known so currently putting it as NULL
            ps.setString(6, retenRegnNoDobj.getReason());
            ps.setDate(7, new java.sql.Date(retenRegnNoDobj.getAppl_date().getTime()));
            ps.setString(8, empCode);
            ps.executeUpdate();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void makeChangesRetention(RetenRegnNo_dobj retenRegnNoDobj, String changedata, String empCd) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangesRetention");
            insertUpdateRetention(tmgr, retenRegnNoDobj, empCd);
            ComparisonBeanImpl.updateChangedData(retenRegnNoDobj.getAppl_no(), changedata, tmgr);
            tmgr.commit();
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
        }
    }// end of makeChangesRetention

    public void insertUpdateRetention(TransactionManager tmgr, RetenRegnNo_dobj retenRegnNoDobj, String empCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "SELECT appl_no FROM " + TableList.VA_SURRENDER_RETENTION + " where appl_no = ?";
        try {
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, retenRegnNoDobj.getAppl_no());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it
                ToImpl impl = new ToImpl();
                impl.insertIntoVhaSurrenderRetention(tmgr, retenRegnNoDobj.getAppl_no());
                updateVaRetention(tmgr, retenRegnNoDobj, empCd);
            } else {
                insertIntoVaSurrenderRetention(tmgr, retenRegnNoDobj, empCd);
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    } // end of insertUpdateRetention

    public void updateVaRetention(TransactionManager tmgr, RetenRegnNo_dobj retenRegnNoDobj, String empCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;

        try {
            sql = "UPDATE " + TableList.VA_SURRENDER_RETENTION
                    + "   SET reason=?,approved_by=?,op_dt=current_timestamp "
                    + " WHERE appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(pos++, retenRegnNoDobj.getReason());
            ps.setString(pos++, empCd);
            ps.setString(pos++, retenRegnNoDobj.getAppl_no());

            ps.executeUpdate();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    } // end of updateVaRetention

    public void updateRetentionStatus(RetenRegnNo_dobj retenRegnNoDobj, RetenRegnNo_dobj retenRegnNoDobjPrev, Status_dobj status_dobj, String changedData, Appl_Details_Dobj applDetailsDobj) throws Exception {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        Date applDate = format.parse(status_dobj.getAppl_dt());
        String newRegnNo = null;

        try {

            tmgr = new TransactionManager("updateRetentionStatus");
            //=================WEB SERVICES FOR NEXTSTAGE START=========//
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            //=================WEB SERVICES FOR NEXTSTAGE END=========//

            if (applDetailsDobj.getCurrent_action_cd() == TableConstants.RETENTION_ENTRY
                    || applDetailsDobj.getCurrent_action_cd() == TableConstants.RETENTION_VERIFICATION
                    || applDetailsDobj.getCurrent_action_cd() == TableConstants.RETENTION_APPROVAL) {

                if ((changedData != null && !changedData.equals("")) || retenRegnNoDobjPrev == null) {
                    insertUpdateRetention(tmgr, retenRegnNoDobj, applDetailsDobj.getCurrentEmpCd());
                }
            }
            if (applDetailsDobj.getCurrent_action_cd() == TableConstants.RETENTION_ENTRY || applDetailsDobj.getCurrent_action_cd() == TableConstants.RETENTION_VERIFICATION) {
                if (retenRegnNoDobj.isAssignFancyNumber() && retenRegnNoDobj.getAssignFancyRegnNumber() != null) {
                    new Rereg_Impl().updateAdvanceRegNoDetails(retenRegnNoDobj.getAppl_no(), retenRegnNoDobj.getAssignFancyRegnNumber(), tmgr);
                }
            }
            if (applDetailsDobj.getCurrent_action_cd() == TableConstants.RETENTION_APPROVAL
                    && !status_dobj.getStatus().trim().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                newRegnNo = insertIntoVtSurrenderRetention(tmgr, applDetailsDobj.getOwnerDobj(),
                        retenRegnNoDobj.getReason(), status_dobj.getAppl_no(),
                        applDate, applDetailsDobj.getCurrent_state_cd(),
                        applDetailsDobj.getCurrent_off_cd(), TableConstants.RETENTION_APPROVAL);


                ServerUtil.deleteFromTable(tmgr, null, retenRegnNoDobj.getAppl_no(),
                        TableList.VA_SURRENDER_RETENTION);

                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                //for updating the status of application when it is approved end


                //SmartCard Or Print
                if (!CommonUtils.isNullOrBlank(newRegnNo)) {
                    ServerUtil.VerifyInsertSmartCardPrintDetail(retenRegnNoDobj.getAppl_no(), newRegnNo,
                            applDetailsDobj.getCurrent_state_cd(), applDetailsDobj.getCurrent_off_cd(), status_dobj.getPur_cd(), tmgr);
                }
            }

            insertIntoVhaChangedData(tmgr, retenRegnNoDobj.getAppl_no(), changedData); //for saving the data into table those are changed by the user

            fileFlow(tmgr, status_dobj); // for updating va_status and vha_status for new role,seat for the other employee

            tmgr.commit();//Commiting data here....

        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }//end of updateRetentionStatus()

    public RetenRegnNo_dobj getVtSurrenderRetentionDetails(String appl_no) throws VahanException {

        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        RetenRegnNo_dobj retenRegnNoDobj = null;
        try {

            tmgr = new TransactionManagerReadOnly("getVtSurrenderRetentionDetails");

            sql = "SELECT *  FROM " + TableList.VT_SURRENDER_RETENTION + " where regn_appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                retenRegnNoDobj = new RetenRegnNo_dobj();
                retenRegnNoDobj.setAppl_no(rs.getString("regn_appl_no"));
                retenRegnNoDobj.setRegn_no(rs.getString("old_regn_no"));
                retenRegnNoDobj.setState_cd(rs.getString("state_cd"));
                retenRegnNoDobj.setOff_cd(rs.getInt("off_cd"));
                retenRegnNoDobj.setReason(rs.getString("reason"));
                retenRegnNoDobj.setAppl_date(rs.getDate("surr_dt"));
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during fetching details of retention of the registeration no");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return retenRegnNoDobj;
    }
}