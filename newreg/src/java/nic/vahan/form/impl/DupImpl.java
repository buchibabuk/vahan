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
import nic.vahan.form.dobj.DupDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.permit.PermitDupPrintImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC095
 */
public class DupImpl {

    private static final Logger LOGGER = Logger.getLogger(DupImpl.class);

    public DupDobj set_dobj_from_db(String appl_no, int purCode) throws SQLException {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        DupDobj dobj = null;
        try {
            tmgr = new TransactionManager("DuplicateCertificate");

            ps = tmgr.prepareStatement("select * from " + TableList.VA_DUP + " where appl_no=? and pur_cd=?");
            ps.setString(1, appl_no);
            ps.setInt(2, purCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                dobj = new DupDobj();

                dobj.setAppl_no(appl_no);
                dobj.setPur_cd(rs.getInt("pur_cd"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setReason(rs.getString("reason"));
                dobj.setFir_no(rs.getString("fir_no"));
                dobj.setFir_dt(rs.getDate("fir_dt"));
                dobj.setPolice_station(rs.getString("police_station"));
                dobj.setOp_dt(rs.getDate("op_dt"));
            }


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

    public String getRegnNo(String appl_no, int pur_cd) {
        String regn_no = null;

        TransactionManager tmgr = null;
        PreparedStatement ps = null;

        try {
            tmgr = new TransactionManager("DuplicateCertificate");
            // con = Database.getConnection();

            ps = tmgr.prepareStatement("select * from " + " va_details " + " where appl_no=? and pur_cd=? ");
            ps.setString(1, appl_no);
            ps.setInt(2, pur_cd);
            // ResultSet rs = ps.executeQuery();
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                regn_no = rs.getString("regn_no");
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

        return regn_no;
    }

    public void update_DupCert_Status(int role, DupDobj dobj, DupDobj dup_dobj_prv, Status_dobj status_dobj, String changedData) throws SQLException, Exception {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = "";

        try {

            tmgr = new TransactionManager("update_DupCert_Status");
            //=================WEB SERVICES FOR NEXTSTAGE START=========//
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            //=================WEB SERVICES FOR NEXTSTAGE END=========//

            if (role == TableConstants.TM_ROLE_ENTRY
                    || role == TableConstants.TM_ROLE_VERIFICATION
                    || role == TableConstants.TM_ROLE_APPROVAL) {
                if ((changedData != null && !changedData.equals("")) || dup_dobj_prv == null) {

                    insertUpdateDUP(tmgr, dobj.getAppl_no(), dobj.getRegn_no(), dobj);  //when there is change by user or Entry Scrutiny first time

                }
            }

            if (role == TableConstants.TM_ROLE_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                //Insurance Approval
                InsImpl.approvalInsurance(tmgr, dobj.getRegn_no(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), Util.getEmpCode());
                //Insert Into VH_DUP From VA_DUP
                sql = "INSERT INTO " + TableList.VH_DUP
                        + " SELECT state_cd, off_cd, appl_no, pur_cd, regn_no, reason, fir_no, fir_dt,"
                        + " police_station, current_timestamp as moved_by, ? as moved_by FROM " + TableList.VA_DUP + " WHERE appl_no=? and pur_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, dobj.getAppl_no());
                ps.setInt(3, dobj.getPur_cd());
                ps.executeUpdate();

                //inserting data into vha_ca from va_ca
                sql = "INSERT INTO " + TableList.VHA_DUP
                        + " SELECT "
                        + " current_timestamp + interval '1 second' as moved_on,? as moved_by ,"
                        + " state_cd,off_cd, "
                        + " appl_no, "
                        + "  pur_cd,  regn_no,"
                        + "  reason,  fir_no,"
                        + "  fir_dt, police_station,op_dt"
                        + "  FROM " + TableList.VA_DUP
                        + " WHERE  appl_no=? and pur_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, dobj.getAppl_no());
                ps.setInt(3, dobj.getPur_cd());
                ps.executeUpdate();

                sql = "DELETE FROM " + TableList.VA_DUP + " where appl_no = ? and pur_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getAppl_no());
                ps.setInt(2, dobj.getPur_cd());
                ps.executeUpdate();

                if (TableConstants.VM_PMT_DUPLICATE_PUR_CD == dobj.getPur_cd()) {
                    sql = "SELECT * FROM " + TableList.VT_PERMIT + " WHERE REGN_NO = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, dobj.getRegn_no());
                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        String docId = "";
                        PermitDupPrintImpl pmtDupImpl = new PermitDupPrintImpl();
                        String list = pmtDupImpl.getMultiDocumentList(dobj.getRegn_no(), dobj.getAppl_no());
                        if (CommonUtils.isNullOrBlank(list)) {
                            docId = CommonPermitPrintImpl.getPermitDocIdForQuery(CommonPermitPrintImpl.getPermitDocId(tmgr, rs.getInt("pmt_type"), rs.getInt("pur_cd"), Util.getUserStateCode()));
                        } else {
                            docId = CommonPermitPrintImpl.getPermitDocIdForQuery(list);
                            pmtDupImpl.moveVaToVhDupDocList(dobj.getAppl_no(), tmgr);
                        }
                        String[] beanData = {docId, dobj.getAppl_no(), rs.getString("regn_no")};
                        CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
                    }
                    sql = "SELECT * FROM " + TableList.VA_INSURANCE + " WHERE APPL_NO = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, dobj.getAppl_no());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        PassengerPermitDetailImpl passImpl = new PassengerPermitDetailImpl();
                        passImpl.insertIntoVTinsAndDeleteVaIns(tmgr, dobj.getAppl_no(), dobj.getRegn_no());
                    }
                    if (role == TableConstants.TM_ROLE_APPROVAL && Util.getUserStateCode().equalsIgnoreCase("DL")) {
                        ServerUtil.updateOnlinePermitStatus(tmgr, status_dobj);
                    }
                } else if (TableConstants.VM_PMT_DUP_COUNTER_SIGNATURE_PUR_CD == dobj.getPur_cd()) {
                    sql = "SELECT * FROM " + TableList.VT_PERMIT_COUNTERSIGNATURE + " WHERE REGN_NO = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, dobj.getRegn_no());
                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        String docId = "";
                        PermitDupPrintImpl pmtDupImpl = new PermitDupPrintImpl();
                        String list = pmtDupImpl.getMultiDocumentList(dobj.getRegn_no(), dobj.getAppl_no());
                        docId = CommonPermitPrintImpl.getPermitDocIdForQuery(list);
                        pmtDupImpl.moveVaToVhDupDocList(dobj.getAppl_no(), tmgr);
                        String[] beanData = {docId, dobj.getAppl_no(), rs.getString("regn_no")};
                        CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
                    }
                } else {

                    //for updating the status of application when it is approved start
                    status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                    ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                    //for updating the status of application when it is approved end


                    if (dobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DUP_RC) {
                        //SmartCard Or Print.
                        boolean isSmartCard = ServerUtil.verifyForSmartCard(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tmgr);
                        if (isSmartCard) {
                            new SmartCardImpl().insertDeleteSmartcardDetails(tmgr, dobj.getRegn_no(), "DUP RC");
                            ServerUtil.deleteFromSmartCardTable(tmgr, dobj.getRegn_no(), TableList.RC_BE_TO_BO);
                        }
                        ServerUtil.VerifyInsertSmartCardPrintDetail(dobj.getAppl_no(), dobj.getRegn_no(),
                                Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), status_dobj.getPur_cd(), tmgr);
                    }
                    //For Print of Print of Duplicate Fitness Certificate
                    if (dobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DUP_FC) {
                        sql = "Insert into " + TableList.VA_FC_PRINT + " ( state_cd,off_cd,appl_no, regn_no, op_dt)  VALUES (?, ?, ?,?,current_timestamp) ";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, Util.getUserStateCode());
                        ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                        ps.setString(3, dobj.getAppl_no());
                        ps.setString(4, dobj.getRegn_no());

                        ps.executeUpdate();
                    }
                }
            }


            insertIntoVhaChangedData(tmgr, dobj.getAppl_no(), changedData);//for saving the data into table those are changed by the user

            ServerUtil.fileFlow(tmgr, status_dobj); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit(); //commiting data here
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }//end of update_DupCert_Status

    public static void insertUpdateDUP(TransactionManager tmgr, String appl_no, String regn_no, DupDobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "SELECT * FROM " + TableList.VA_DUP + " where appl_no = ? and pur_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, appl_no);
        ps.setInt(2, dobj.getPur_cd());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) { //if any record is exist then update otherwise insert it
            insertIntoDupHistory(tmgr, appl_no, dobj.getPur_cd());
            updateDup(tmgr, dobj);
        } else {
            insertIntoDup(tmgr, dobj);
        }
    }

    public static void insertIntoDup(TransactionManager tmgr, DupDobj dobj) throws SQLException {

        String sql = "INSERT INTO " + TableList.VA_DUP
                + " (state_cd,off_cd,appl_no,"
                + "  pur_cd, regn_no,"
                + "  reason,  fir_no,"
                + "  fir_dt, police_station,"
                + "  op_dt )"
                + "  VALUES (?, ?, ?, ?, ?, ?, ?,?,?, current_timestamp)";

        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getUserStateCode());
        ps.setInt(2, Util.getSelectedSeat().getOff_cd());
        ps.setString(3, dobj.getAppl_no());
        ps.setInt(4, dobj.getPur_cd());
        ps.setString(5, dobj.getRegn_no());
        ps.setString(6, dobj.getReason());
        ps.setString(7, dobj.getFir_no());
        ps.setDate(8, dobj.getFir_dt() == null ? null : new java.sql.Date(dobj.getFir_dt().getTime()));
        ps.setString(9, dobj.getPolice_station());
        ps.executeUpdate();

    }

    public static void updateDup(TransactionManager tmgr, DupDobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = " update " + TableList.VA_DUP + " set "
                + "  regn_no=?,"
                + "  reason=?,"
                + "  fir_no=?,"
                + "  fir_dt=?,"
                + "  police_station=?,"
                + "  op_dt =current_timestamp where appl_no=? and pur_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getRegn_no());
        ps.setString(2, dobj.getReason());
        ps.setString(3, dobj.getFir_no());
        ps.setDate(4, dobj.getFir_dt() == null ? null : new java.sql.Date(dobj.getFir_dt().getTime()));
        ps.setString(5, dobj.getPolice_station());
        ps.setString(6, dobj.getAppl_no());
        ps.setInt(7, dobj.getPur_cd());

        ps.executeUpdate();

    }

    public static void insertIntoDupHistory(TransactionManager tmgr, String appl_no, int purCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        //inserting data into vha_ca from va_ca
        sql = "INSERT INTO " + TableList.VHA_DUP
                + " SELECT "
                + "  current_timestamp as moved_on,? as moved_by ,state_cd,off_cd, appl_no, "
                + "  pur_cd,  regn_no,"
                + "  reason,  fir_no,"
                + "  fir_dt, police_station,op_dt"
                + "  FROM  " + TableList.VA_DUP
                + " WHERE  appl_no=? and pur_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.setInt(3, purCode);
        ps.executeUpdate();
    }

    public static void saveChangeDup(DupDobj dup_Dobj, String changedata) throws SQLException, VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveChangeDup");
            insertUpdateDUP(tmgr, dup_Dobj.getAppl_no(), dup_Dobj.getRegn_no(), dup_Dobj);
            ComparisonBeanImpl.updateChangedData(dup_Dobj.getAppl_no(), changedata, tmgr);
            tmgr.commit();
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
}
