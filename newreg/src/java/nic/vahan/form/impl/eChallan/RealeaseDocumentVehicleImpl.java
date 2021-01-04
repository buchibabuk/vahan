package nic.vahan.form.impl.eChallan;

import nic.vahan.form.dobj.eChallan.RealesedDocumentVehicleDobj;
import nic.vahan.form.dobj.eChallan.ReleaseDocumentDetailsDobj;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.server.ServerUtil;
import nic.vahan.db.TableList;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

public class RealeaseDocumentVehicleImpl {

    private static Logger LOGGER = Logger.getLogger(RealeaseDocumentVehicleImpl.class);

    public RealesedDocumentVehicleDobj realaseDocument(String applicationNo) throws SQLException, VahanException, Exception {
        String whereiam = "realaseDocument";
        TransactionManager tmgr = null;
        ResultSet rs = null;
        PreparedStatement psmt = null;
        RealesedDocumentVehicleDobj dobj = new RealesedDocumentVehicleDobj();
        try {

            String sql = " SELECT challan.regn_no,challan.is_doc_impound, challan.is_vch_impdound,vt_chall_acc.accused_catg,vm_accused.descr"
                    + " FROM   " + TableList.VH_CHALLAN + " challan"
                    + " INNER JOIN  " + TableList.VT_CHALLAN_ACCUSED + "  vt_chall_acc ON  vt_chall_acc.appl_no=challan.appl_no"
                    + "  join  " + TableList.VM_ACCUSED + " vm_accused on vm_accused.code  = vt_chall_acc.accused_catg"
                    + " WHERE challan.appl_no=?";

            tmgr = new TransactionManager(whereiam);
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applicationNo.toUpperCase());
            rs = psmt.executeQuery();
            while (rs.next()) {
                ReleaseDocumentDetailsDobj releasvehicledetlsdobj = new ReleaseDocumentDetailsDobj();
                releasvehicledetlsdobj.getMasteraccusedobj().setCode(rs.getString("accused_catg"));
                releasvehicledetlsdobj.getMasteraccusedobj().setDescr(rs.getString("descr"));
                releasvehicledetlsdobj.setDocumentImpound(rs.getString("is_doc_impound"));
                releasvehicledetlsdobj.setVehicleImpound(rs.getString("is_vch_impdound"));
                dobj.getRealesdocdetails().setVehicleNo(rs.getString("regn_no"));
                dobj.getRealesdocdtlist().add(releasvehicledetlsdobj);

            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj;
    }

    public boolean challanExistance(String applicationNo) throws SQLException, VahanException, Exception {
        String whereiam = "realaseDocument";
        TransactionManager tmgr = null;
        ResultSet rs = null;
        PreparedStatement psmt = null;
        boolean flag = false;
        RealesedDocumentVehicleDobj dobj = new RealesedDocumentVehicleDobj();
        try {
            String sql = "Select CHAL_NO from  " + TableList.VH_CHALLAN + " where appl_no=?";


            tmgr = new TransactionManager(whereiam);
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applicationNo);
            rs = psmt.executeQuery();
            if (rs.next()) {
                flag = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean chkChallanSettlement(String applicationNO) throws VahanException, Exception {
        PreparedStatement pstm = null;
        PreparedStatement pstmt1 = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        boolean flag = false;
        String sql = "select *  from " + TableList.VT_CHALLAN_AMT
                + " where appl_no=? and adv_amt>0 and  (adv_rcpt_no is not null OR adv_rcpt_no !='') ";
        String sqlCourt = "SELECT * FROM  " + TableList.VT_CHALLAN_SETTLEMANT + "  WHERE appl_no=? ";
        try {
            tmgr = new TransactionManager("DAODisposalOfChallan.chkRefToCourt()");
            pstmt1 = tmgr.prepareStatement(sql);
            pstmt1.setString(1, applicationNO);
            rs = pstmt1.executeQuery();
            if (rs.next()) {
                flag = true;
            } else {
                pstm = null;
                rs = null;
                pstm = tmgr.prepareStatement(sqlCourt);
                pstm.setString(1, applicationNO);

                rs = pstm.executeQuery();
                if (rs.next()) {
                    flag = true;
                }
            }


        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean chkPaymentReciept(String applicationNo) throws VahanException, Exception {
        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        boolean flag = false;
        String sql = "select *  from " + TableList.VT_CHALLAN_AMT
                + " where appl_no=? and adv_amt>0 and  (adv_rcpt_no is not null OR adv_rcpt_no !='') ";
        String sqlChallanFee = "select * from  " + TableList.VT_CHALLAN_AMT + "  where appl_no=? and settlement_amt > 0 AND (cmpd_rcpt_no is not null or cmpd_rcpt_no!= '')";
        String sqlChallanTax = "select * from  " + TableList.VT_CHALLAN_TAX + "  where appl_no=?  AND (cmpd_rcpt_no is not null or cmpd_rcpt_no!= '')";
        String sqlChallanReferToCourt = "select * from  " + TableList.VT_CHALLAN_REFER_TO_COURT + " where appl_no=? and court_paid_amount is not NULL AND (court_rcpt_no is not null or court_rcpt_no!= '')";

        try {

            tmgr = new TransactionManager("DAODisposalOfChallan.chkRefToCourt()");
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, applicationNo);
            rs = pstm.executeQuery();
            if (rs.next()) {
                flag = true;
            } else {
                pstm = null;
                rs = null;
                pstm = tmgr.prepareStatement(sqlChallanFee);
                pstm.setString(1, applicationNo);

                rs = pstm.executeQuery();
                if (rs.next()) {
                    flag = true;
                }

                pstm = null;
                rs = null;
                pstm = tmgr.prepareStatement(sqlChallanReferToCourt);
                pstm.setString(1, applicationNo);

                rs = pstm.executeQuery();
                if (rs.next()) {
                    flag = true;
                }
                pstm = null;
                rs = null;
                pstm = tmgr.prepareStatement(sqlChallanTax);
                pstm.setString(1, applicationNo);

                rs = pstm.executeQuery();
                if (rs.next()) {
                    flag = true;
                }
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean checkChallanDisposal(String applicationNO) throws SQLException, VahanException, Exception {
        String whereiam = "realaseDocument";
        TransactionManager tmgr = null;
        ResultSet rs = null;
        PreparedStatement psmt = null;
        boolean flag = false;
        RealesedDocumentVehicleDobj dobj = new RealesedDocumentVehicleDobj();
        try {
            String sql1 = "select *  from " + TableList.VT_CHALLAN_AMT
                    + " where appl_no=? and adv_amt>0 and  (adv_rcpt_no is not null OR adv_rcpt_no !='') ";
            String sql = "Select * from  " + TableList.VH_CHALLAN + "   where appl_no=?";
            tmgr = new TransactionManager(whereiam);
            psmt = tmgr.prepareStatement(sql1);
            psmt.setString(1, applicationNO);
            rs = psmt.executeQuery();
            if (rs.next()) {
                flag = true;
            } else {
                psmt = null;
                rs = null;
                psmt = tmgr.prepareStatement(sql);
                psmt.setString(1, applicationNO.toUpperCase());

                rs = psmt.executeQuery();
                if (rs.next()) {
                    flag = true;
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public RealesedDocumentVehicleDobj fetchDocumentVehicleImpound(String applicationNo, String roleCd) throws SQLException, VahanException, Exception {
        String whereiam = "realaseDocument";
        TransactionManager tmgr = null;
        ResultSet rs = null;
        PreparedStatement psmt = null;
        RealesedDocumentVehicleDobj dobj = new RealesedDocumentVehicleDobj();
        String sql1 = "";
        String sql2 = "";
        String msg = "";
        try {
            sql1 = "Select * from  " + TableList.VT_VEHICLE_IMPOUND + "  where  appl_no=?  and release_dt is null and release_by is null";
            sql2 = "Select * from  " + TableList.VT_VEHICLE_IMPOUND + " where  appl_no=? ";
            tmgr = new TransactionManager(whereiam);
            if (Integer.parseInt(roleCd) == TableConstants.VM_ROLE_ENFORCEMENT_RELEASE_DOC_VEHICLE_ENTRY) {
                psmt = tmgr.prepareStatement(sql1);
            } else {
                psmt = tmgr.prepareStatement(sql2);
            }

            psmt.setString(1, applicationNo.toUpperCase());
            rs = psmt.executeQuery();
            if (rs.next()) {
                dobj.getRealesdocdetails().getVehicleimpouddobj().setImpndDate(rs.getDate("impound_dt"));
                dobj.getRealesdocdetails().getVehicleimpouddobj().setSezNO(rs.getString("seizure_no"));
                dobj.getRealesdocdetails().getVehicleimpouddobj().setImpndPlace(rs.getString("impound_place"));
                dobj.getRealesdocdetails().getVehicleimpouddobj().setOfficerToContact(rs.getString("contact_off"));
                if (Integer.parseInt(roleCd) != TableConstants.VM_ROLE_ENFORCEMENT_RELEASE_DOC_VEHICLE_ENTRY) {
                    dobj.getRealesdocdetails().setRealeasedate(rs.getString("release_dt"));
                    dobj.getRealesdocdetails().setRealeseofficer(rs.getString("release_by"));
                    dobj.getRealesdocdetails().setRemarkany(rs.getString("remarks"));
                }

                msg = "";
            }

            psmt.close();
            rs.close();
            psmt = null;
            rs = null;
            sql1 = "Select docImpnd.appl_no, docImpnd.doc_cd, docImpnd.doc_no, docImpnd.iss_auth, docImpnd.validity, "
                    + "docImpnd.accused_catg, docImpnd.doc_impnd_dt, "
                    + " docImpnd.release_dt, docImpnd.release_by, docImpnd.remarks, docImpnd.state_cd, docImpnd.off_cd,"
                    + " documents.descr"
                    + "  from  " + TableList.VT_DOCS_IMPOUND + "  docImpnd "
                    + " INNER JOIN " + TableList.VM_DOCUMENTS + "  documents ON docImpnd.doc_cd=documents.code "
                    + " where appl_no=? and docImpnd.state_cd=? and release_dt is null and release_by is null";
            sql2 = "Select docImpnd.appl_no, docImpnd.doc_cd, docImpnd.doc_no, docImpnd.iss_auth, docImpnd.validity, "
                    + "docImpnd.accused_catg, docImpnd.doc_impnd_dt, "
                    + " docImpnd.release_dt, docImpnd.release_by, docImpnd.remarks, docImpnd.state_cd, docImpnd.off_cd,"
                    + " documents.descr"
                    + "  from   " + TableList.VT_DOCS_IMPOUND + "  docImpnd "
                    + " INNER JOIN " + TableList.VM_DOCUMENTS + " documents ON docImpnd.doc_cd=documents.code  "
                    + " where appl_no=? and docImpnd.state_cd=?";
            if (Integer.parseInt(roleCd) == TableConstants.VM_ROLE_ENFORCEMENT_RELEASE_DOC_VEHICLE_ENTRY) {
                psmt = tmgr.prepareStatement(sql1);
            } else {
                psmt = tmgr.prepareStatement(sql2);
            }

            psmt.setString(1, applicationNo.toUpperCase());
            psmt.setString(2, Util.getUserStateCode());
            rs = psmt.executeQuery();

            while (rs.next()) {
                ReleaseDocumentDetailsDobj releasvehicledetlsdobj = new ReleaseDocumentDetailsDobj();
                releasvehicledetlsdobj.getDocimpounddobj().setDocNo(rs.getString("doc_no"));
                releasvehicledetlsdobj.getDocimpounddobj().setDocument(rs.getString("doc_cd"));
                releasvehicledetlsdobj.getDocimpounddobj().setIssueAuth(rs.getString("iss_auth"));
                releasvehicledetlsdobj.getDocimpounddobj().setValidUpto(rs.getString("validity"));
                releasvehicledetlsdobj.getDocimpounddobj().setDocDesc(rs.getString("descr"));
                dobj.getRealesdocdtlist().add(releasvehicledetlsdobj);
                if (Integer.parseInt(roleCd) != TableConstants.VM_ROLE_ENFORCEMENT_RELEASE_DOC_VEHICLE_ENTRY) {

                    dobj.getRealesdocdetails().setRealeasedate(rs.getString("release_dt"));
                    dobj.getRealesdocdetails().setRealeseofficer(rs.getString("release_by"));
                    dobj.getRealesdocdetails().setRemarkany(rs.getString("remarks"));
                }
                msg = "";
            }
            dobj.setMessgae(msg);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
                psmt.close();
                rs.close();
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return dobj;
    }

    public boolean updateDocumentVehicleImpound(String applicationNo, String vehicleno, RealesedDocumentVehicleDobj dobj, String isDocumentImpound, String isVehicleImpound, Status_dobj statusDobj) throws SQLException, VahanException, Exception {
        String whereiam = "realaseDocument";
        TransactionManager tmgr = null;
        boolean flag = false;
        PreparedStatement psmt = null;

        String sql = "";
        try {
            tmgr = new TransactionManager(whereiam);
            if (isVehicleImpound.equals("Y")) {
                sql = "update  " + TableList.VT_VEHICLE_IMPOUND + "  set release_dt =? ,release_by = ?, remarks =? where  "
                        + " appl_no=?";
                psmt = tmgr.prepareStatement(sql);
                psmt.setTimestamp(1, getTimeStamp(dobj.getRealesdocdetails().getRealeasedate()));
                psmt.setString(2, dobj.getRealesdocdetails().getRealeseofficer());
                psmt.setString(3, dobj.getRealesdocdetails().getRemarkany());
                psmt.setString(4, applicationNo);
                psmt.executeUpdate();
                psmt.close();
            }
            if (isDocumentImpound.equals("Y")) {
                psmt = null;
                sql = "update  " + TableList.VT_DOCS_IMPOUND + "  set release_dt =?, release_by =?, remarks =? where"
                        + " appl_no=?";
                psmt = tmgr.prepareStatement(sql);
                psmt.setTimestamp(1, getTimeStamp(dobj.getRealesdocdetails().getRealeasedate()));
                psmt.setString(2, dobj.getRealesdocdetails().getRealeseofficer());
                psmt.setString(3, dobj.getRealesdocdetails().getRemarkany());
                psmt.setString(4, applicationNo);
                psmt.executeUpdate();
                psmt.close();
            }
            tmgr.commit();
            flag = true;

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public static Timestamp getTimeStamp(String Date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yy");
        Timestamp st = null;
        try {
            java.util.Date date = sdf.parse(Date);
            st = new Timestamp(date.getTime());
        } catch (ParseException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return st;
    }

    public void movetoapprove(int actionCode, Status_dobj statusDobj, String changedDataContents, String applicationNo, String vehicleno, RealesedDocumentVehicleDobj dobj, String isDocumentImpound, String isVehicleImpound) throws Exception {
        TransactionManager tmgr = null;
        PreparedStatement psmt = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("Dispose Challan : saveSettleData");
            if (changedDataContents != null) {

                if (isVehicleImpound.equals("Y")) {
                    sql = "update  " + TableList.VT_VEHICLE_IMPOUND + "  set release_dt =? ,release_by = ?, remarks =? where  "
                            + " appl_no=?";
                    psmt = tmgr.prepareStatement(sql);
                    psmt.setTimestamp(1, getTimeStamp(dobj.getRealesdocdetails().getRealeasedate()));
                    psmt.setString(2, dobj.getRealesdocdetails().getRealeseofficer());
                    psmt.setString(3, dobj.getRealesdocdetails().getRemarkany());
                    psmt.setString(4, applicationNo);

                    psmt.executeUpdate();
                    psmt.close();
                }
                if (isDocumentImpound.equals("Y")) {
                    psmt = null;
                    sql = "update  " + TableList.VT_DOCS_IMPOUND + "  set release_dt =?, release_by =?, remarks =? where"
                            + " appl_no=?";
                    psmt = tmgr.prepareStatement(sql);
                    psmt.setTimestamp(1, getTimeStamp(dobj.getRealesdocdetails().getRealeasedate()));
                    psmt.setString(2, dobj.getRealesdocdetails().getRealeseofficer());
                    psmt.setString(3, dobj.getRealesdocdetails().getRemarkany());
                    psmt.setString(4, applicationNo);
                    psmt.executeUpdate();
                    psmt.close();
                }

            }
            if (actionCode == TableConstants.VM_ROLE_ENFORCEMENT_RELEASE_DOC_VEHICLE_APPROVE) {
                statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, statusDobj);

            }
            statusDobj = ServerUtil.webServiceForNextStage(statusDobj, tmgr);
            ServerUtil.fileFlow(tmgr, statusDobj);
            tmgr.commit();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

    }

    public void reback(String roleCd, Status_dobj statusDobj, String changedDataContents, String applicationNo, String vehicleno, RealesedDocumentVehicleDobj dobj, String isDocumentImpound, String isVehicleImpound) {
        TransactionManager tmgr = null;
        PreparedStatement psmt = null;


        String sql = "";
        try {
            tmgr = new TransactionManager("movetoapprove");
            if (!changedDataContents.isEmpty()) {

                if (isVehicleImpound.equals("Y")) {
                    sql = "update  " + TableList.VT_VEHICLE_IMPOUND + "  set release_dt =? ,release_by = ?, remarks =? where  "
                            + " appl_no=?";
                    psmt = tmgr.prepareStatement(sql);
                    psmt.setTimestamp(1, getTimeStamp(dobj.getRealesdocdetails().getRealeasedate()));
                    psmt.setString(2, dobj.getRealesdocdetails().getRealeseofficer());
                    psmt.setString(3, dobj.getRealesdocdetails().getRemarkany());
                    psmt.setString(4, applicationNo);

                    psmt.executeUpdate();
                    psmt.close();
                }
                if (isDocumentImpound.equals("Y")) {
                    psmt = null;
                    sql = "update  " + TableList.VT_DOCS_IMPOUND + "  set release_dt =?, release_by =?, remarks =? where"
                            + " appl_no=?";
                    psmt = tmgr.prepareStatement(sql);
                    psmt.setTimestamp(1, getTimeStamp(dobj.getRealesdocdetails().getRealeasedate()));
                    psmt.setString(2, dobj.getRealesdocdetails().getRealeseofficer());
                    psmt.setString(3, dobj.getRealesdocdetails().getRemarkany());
                    psmt.setString(4, applicationNo);
                    psmt.executeUpdate();
                    psmt.close();
                }

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
}
