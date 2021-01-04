/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.tradecert;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.tradecert.ApplicationAuditTrailTradeCertDobj;
import nic.vahan.server.CommonUtils;

import org.apache.log4j.Logger;

/**
 *
 * @author nic
 */
public class ApplicationAuditTrailTradeCertImpl {

    private static final Logger LOGGER = Logger.getLogger(ApplicationAuditTrailTradeCertImpl.class);

    public static List<ApplicationAuditTrailTradeCertDobj> getAuditTrailInfo(String applNo) throws VahanException {
        List<ApplicationAuditTrailTradeCertDobj> auditList = new ArrayList<ApplicationAuditTrailTradeCertDobj>();
        ApplicationAuditTrailTradeCertDobj dobj = new ApplicationAuditTrailTradeCertDobj();


        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        String sql = "SELECT state_cd, appl_no, sr_no, iteration_counter, applicant, applicant_forward_remarks, receiver_hard_copy_received_on, \n"
                + "       receiver, receiver_forward_remarks, receiver_backward_remarks, \n"
                + "       receiver_deficiency_mail_content, verifier_hard_copy_received_on, \n"
                + "       verifier, verifier_forward_remarks, verifier_backward_remarks, \n"
                + "       verifier_deficiency_mail_content, approver_hard_copy_received_on, \n"
                + "       approver, approver_forward_remarks, approver_backward_remarks, \n"
                + "       approver_deficiency_mail_content, current_status\n"
                + "  FROM " + TableList.VA_TRADE_CERTIFICATE_AUDIT_TRAIL + " where appl_no = ? "
                + "  union\n"
                + "  SELECT state_cd, appl_no, sr_no, iteration_counter, applicant, applicant_forward_remarks, receiver_hard_copy_received_on, \n"
                + "       receiver, receiver_forward_remarks, receiver_backward_remarks, \n"
                + "       receiver_deficiency_mail_content, verifier_hard_copy_received_on, \n"
                + "       verifier, verifier_forward_remarks, verifier_backward_remarks, \n"
                + "       verifier_deficiency_mail_content, approver_hard_copy_received_on, \n"
                + "       approver, approver_forward_remarks, approver_backward_remarks, \n"
                + "       approver_deficiency_mail_content, current_status\n"
                + "  FROM " + TableList.VHA_TRADE_CERTIFICATE_AUDIT_TRAIL + " where appl_no = ? order by iteration_counter";
        try {
            tmgr = new TransactionManager("getAuditTrailList");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo.trim().toUpperCase());
            psmt.setString(2, applNo.trim().toUpperCase());
            rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                dobj = new ApplicationAuditTrailTradeCertDobj();
                dobj.setApplicationNo(rs.getString("appl_no"));
                dobj.setIterationCounter(String.valueOf(rs.getInt("iteration_counter")));

                dobj.setApplicant(getApplicantNameByCd(rs.getString("applicant")));
                dobj.setApplicantForwardRemarks(rs.getString("applicant_forward_remarks"));

                dobj.setReceiver(getUserNameByCd(rs.getString("receiver")));
                dobj.setReceiverBackwardRemarks(rs.getString("receiver_backward_remarks"));
                dobj.setReceiverDeficiencyMailContent(rs.getString("receiver_deficiency_mail_content"));
                dobj.setReceiverForwardRemarks(rs.getString("receiver_forward_remarks"));
                dobj.setReceiverHardCopyReceivedOn(rs.getString("receiver_hard_copy_received_on"));

                dobj.setVerifier(getUserNameByCd(rs.getString("verifier")));
                dobj.setVerifierBackwardRemarks(rs.getString("verifier_backward_remarks"));
                dobj.setVerifierForwardRemarks(rs.getString("verifier_forward_remarks"));
                dobj.setVerifierDeficiencyMailContent(rs.getString("verifier_deficiency_mail_content"));
                dobj.setVerifierHardCopyReceivedOn(rs.getString("verifier_hard_copy_received_on"));

                dobj.setApprover(getUserNameByCd(rs.getString("approver")));
                dobj.setApproverBackwardRemarks(rs.getString("approver_backward_remarks"));
                dobj.setApproverForwardRemarks(rs.getString("approver_forward_remarks"));
                dobj.setApproverHardCopyReceivedOn(rs.getString("approver_hard_copy_received_on"));
                dobj.setApproverDeficiencyMailContent(rs.getString("approver_deficiency_mail_content"));


                dobj.setCurrentStatus(getStatusDescription(rs.getString("current_status")));
                auditList.add(dobj);

            }
//            if(!auditList.isEmpty()){
//                 Collections.sort(auditList, new IterationsListComparator());   ///// iterations in reverse order
//            }
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
            }
        }
        return auditList;
    }

    public static boolean checkApplicationNoExist(String applicationNo) {
        boolean isExist = false;


        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        String sqlapplication = "checkApplicationNoExist";
        try {

            tmgr = new TransactionManager("Check applicationNo ");
            String sql = "SELECT state_cd, appl_no, sr_no, iteration_counter, applicant, applicant_forward_remarks, receiver_hard_copy_received_on, \n"
                    + "       receiver, receiver_forward_remarks, receiver_backward_remarks, \n"
                    + "       receiver_deficiency_mail_content, verifier_hard_copy_received_on, \n"
                    + "       verifier, verifier_forward_remarks, verifier_backward_remarks, \n"
                    + "       verifier_deficiency_mail_content, approver_hard_copy_received_on, \n"
                    + "       approver, approver_forward_remarks, approver_backward_remarks, \n"
                    + "       approver_deficiency_mail_content, current_status\n"
                    + "  FROM " + TableList.VA_TRADE_CERTIFICATE_AUDIT_TRAIL + " where appl_no = ?"
                    + "  union\n"
                    + "  SELECT state_cd, appl_no, sr_no, iteration_counter, applicant, applicant_forward_remarks, receiver_hard_copy_received_on, \n"
                    + "       receiver, receiver_forward_remarks, receiver_backward_remarks, \n"
                    + "       receiver_deficiency_mail_content, verifier_hard_copy_received_on, \n"
                    + "       verifier, verifier_forward_remarks, verifier_backward_remarks, \n"
                    + "       verifier_deficiency_mail_content, approver_hard_copy_received_on, \n"
                    + "       approver, approver_forward_remarks, approver_backward_remarks, \n"
                    + "       approver_deficiency_mail_content, current_status\n"
                    + "  FROM " + TableList.VHA_TRADE_CERTIFICATE_AUDIT_TRAIL + " where appl_no = ?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applicationNo.trim().toUpperCase());
            psmt.setString(2, applicationNo.trim().toUpperCase());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isExist = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return isExist;
    }

    public static String getStatusDescription(String currentStatus) {
        if (currentStatus.equals("P")) {  ///// Pending
            currentStatus = "Online Application Pending (Pending On Receiving Stage)";
        } else if (currentStatus.equals("R")) { //// Received
            currentStatus = "Online Application Received (Pending On Verification Stage)";
        } else if (currentStatus.equals("V")) {  //// Verified
            currentStatus = "Online Application Verified (Pending On Approving Stage)";
        } else if (currentStatus.equals("A")) {  //// Approved
            currentStatus = "Online Application Approved (Trade Certficate To Be Printed And Collected)";
        } else if (currentStatus.equals("M")) {  ///// Modified By Applicant
            currentStatus = "Online Application To Be Modified By Applicant (Pending From Applicant Side)";
        } else if (currentStatus.equals("MG")) {  ///// Mail Generated
            currentStatus = "Mail Generated For Online Application";
        }
        return currentStatus;
    }

    public static String getUserNameByCd(String userCd) {
        if (CommonUtils.isNullOrBlank(userCd)) {
            return userCd;
        }
        String userName = "";
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;

        try {

            tmgr = new TransactionManager("getUserNameByCd");
            String sql = "SELECT user_name FROM tm_user_info where user_cd = ?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, Integer.valueOf(userCd));
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                userName = rs.getString("user_name");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return userName;
    }

    public static String getApplicantNameByCd(String userCd) {
        if (CommonUtils.isNullOrBlank(userCd)) {
            return userCd;
        }
        String userName = userCd;
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        String sql = "Select * from " + TableList.VM_APPLICANT_MAST_APPL + " where applicant_cd = ?";
        try {

            tmgr = new TransactionManager("getApplicantNameByCd");

            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, userCd);
            rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                userName = rs.getString("applicant_name");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return userName;
    }

    private static class IterationsListComparator implements Comparator<ApplicationAuditTrailTradeCertDobj> {

        @Override
        public int compare(ApplicationAuditTrailTradeCertDobj o1, ApplicationAuditTrailTradeCertDobj o2) {
            Integer iteration1 = Integer.valueOf(o1.getIterationCounter());
            Integer iteration2 = Integer.valueOf(o2.getIterationCounter());
            return iteration2.compareTo(iteration1);
        }
    }
}
