/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.fileMonitoring;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.fileMonitoring.FilePendingReportDO;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author Deepak
 */
public class FilePendingReportImpl {

    static String whereami = "Pending File Report";
    private static final Logger LOGGER = Logger.getLogger(FilePendingReportImpl.class);

    public static List<FilePendingReportDO> getPendingFiles(FilePendingReportDO dobj) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<FilePendingReportDO> fileList = new ArrayList();
        try {
            HttpSession session = Util.getSession();
            tmgr = new TransactionManagerReadOnly("FilePendingReportImpl:getPendingFiles");
            Date fromdate = dobj.getFromdate();
            Date todate = dobj.getToDate();
            Calendar c = Calendar.getInstance();
            c.setTime(todate);
            java.sql.Date toDate = new java.sql.Date(c.getTimeInMillis());
            c.setTime(fromdate);
            java.sql.Date fromDate = new java.sql.Date(c.getTimeInMillis());
            String getUserDtls = "";
            int userCode = FilePendingReportImpl.getUserCdByUserId(Util.getUserId());
            Map dealerData = new HashMap();
            dealerData = OwnerImpl.getDealerDetail(userCode);
            String dealerCd = dealerData.get("dealer_cd").toString();
            session.setAttribute("DealerName", dealerData.get("dealer_name").toString());

            getUserDtls = " select distinct vrc.appl_no,to_char(vrc.rcpt_dt,'dd-MM-yyyy hh:mi:ss') as rcpt_dt,((EXTRACT(epoch from age( now(),vrc.rcpt_dt)) / 86400)::int) as pendingDays  \n"
                    + " ,case when vfd.status is null then 'Pending for Physical Submission' when vfd.status='R' then 'Reject due to '||vfd.status_desc end status"
                    + " from " + TableList.VPH_RCPT_CART + " vrc \n"
                    + " inner join va_details b on b.appl_no=vrc.appl_no and b.pur_cd=123 and b.entry_status <> 'A' \n"
                    + " inner join  " + TableList.TM_USER_PERMISSIONS + " tmc on tmc.user_cd = vrc.user_cd and tmc.dealer_cd=? \n"
                    + " left join  " + TableList.VT_FILE_DTLS + "  vfd on vfd.file_no=vrc.appl_no \n "
                    + " left join  " + TableList.VT_FMS_SUBMIT_FILE_DTLS + " fmsd on fmsd.file_no=vrc.appl_no \n"
                    + " where vrc.rcpt_dt between '" + fromDate + " 00:00:00' and '" + toDate + " 23:59:59'\n"
                    + " and vrc.state_cd=? and (vfd.status in ('R') or vfd.status is null) and fmsd.file_no is null"
                    + " order by rcpt_dt";
            pstmt = tmgr.prepareStatement(getUserDtls);
            pstmt.setString(1, dealerCd);
            pstmt.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            int i = 1;
            while (rs.next()) {
                FilePendingReportDO dobj1 = new FilePendingReportDO();
                dobj1.setSrNo(i++);
                dobj1.setFileNo(rs.getString("appl_no"));
                dobj1.setRegistrationDate(rs.getString("rcpt_dt"));
                dobj1.setAppl_no(rs.getString("appl_no"));
                dobj1.setStatus(rs.getString("status"));
                dobj1.setEnter(false);
                dobj1.setPendingDays(rs.getInt("pendingDays"));
                fileList.add(dobj1);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in geting pending files");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }

            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);

            }
        }
        return fileList;
    }

    public static int getUserCdByUserId(String userID) {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement pstmt = null;
        int userCode = 0;
        RowSet rs = null;
        try {

            tmgr = new TransactionManagerReadOnly("FilePendingReportImpl:getUserCdByUserId");
            String pageQuery = "select user_cd from " + TableList.TM_USER_INFO + " where user_id=?";
            pstmt = tmgr.prepareStatement(pageQuery);
            pstmt.setString(1, userID);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                userCode = rs.getInt("user_cd");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }

            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);

            }
        }
        return userCode;

    }

    public static boolean saveFileDetail(List<FilePendingReportDO> list, FilePendingReportDO dobj1) {
        boolean retFlag = false;
        PreparedStatement pstmt = null;
        TransactionManager tmgr = null;
        try {
            List<FilePendingReportDO> list1 = new ArrayList();
            tmgr = new TransactionManager("FilePendingReportImpl::saveFileDetail");
            int user_cd = getUserCdByUserId(Util.getUserId());
            Map dealerData = OwnerImpl.getDealerDetail(user_cd);
            String dealerCd = dealerData.get("dealer_cd").toString();
            for (int i = 0; i < list.size(); i++) {
                FilePendingReportDO dobj = new FilePendingReportDO();
                dobj = (FilePendingReportDO) list.get(i);
                if (dobj.isEnter()) {
                    String status = getCurrentFileStatus(dobj.getFileNo());

                    if (status == null) {
                        String sql = "INSERT INTO " + TableList.VT_FILE_DTLS + " (file_no,application_no,status,status_desc,pending_days,send_by,send_date,dealer_cd,off_cd,state_cd)VALUES (?,?,?,?,?,?,current_timestamp,?,?,?)";
                        pstmt = tmgr.prepareStatement(sql);
                        pstmt.setString(1, dobj.getFileNo());
                        pstmt.setString(2, dobj.getAppl_no());
                        pstmt.setString(3, "P");
                        pstmt.setString(4, dobj.getStatus());
                        pstmt.setInt(5, dobj.getPendingDays());
                        pstmt.setString(6, dealerCd);
                        pstmt.setString(7, dealerCd);
                        pstmt.setInt(8, Util.getUserOffCode());
                        pstmt.setString(9, Util.getUserStateCode());
                        pstmt.executeUpdate();
                        list1.add(dobj);
                    } else if (status.equalsIgnoreCase("R")) {
                        String sendHistQuery = "Insert into " + TableList.VH_FILE_DTLS + " select file.*,?,current_timestamp from " + TableList.VT_FILE_DTLS + " file where file_no=?";
                        pstmt = tmgr.prepareStatement(sendHistQuery);
                        pstmt.setString(1, dealerCd);
                        pstmt.setString(2, dobj.getFileNo());
                        pstmt.executeUpdate();

                        String strQuery = "update " + TableList.VT_FILE_DTLS + " set status='P',status_desc=null,send_date=current_timestamp where file_no=?";
                        pstmt = tmgr.prepareStatement(strQuery);
                        pstmt.setString(1, dobj.getFileNo());
                        pstmt.executeUpdate();
                        list1.add(dobj);
                    }
                }
            }
            tmgr.commit();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("sendFilesList", list1);
            retFlag = true;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);

        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }

            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);

            }
        }

        return retFlag;
    }

    public static String getCurrentFileStatus(String fileNo) {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement pstmt = null;
        String fileStatus = null;
        RowSet rs = null;
        try {
            tmgr = new TransactionManagerReadOnly("FilePendingReportImpl::getCurrentFileStatus");
            String pageQuery = "select status from " + TableList.VT_FILE_DTLS + " where file_no=?";
            pstmt = tmgr.prepareStatement(pageQuery);
            pstmt.setString(1, fileNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                fileStatus = rs.getString("status");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }

            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);

            }
        }
        return fileStatus;

    }

    public static void sendMessage(String error) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(error));
    }
}
