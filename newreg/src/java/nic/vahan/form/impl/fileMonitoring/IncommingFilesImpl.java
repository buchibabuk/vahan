/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.fileMonitoring;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.fileMonitoring.IncommingFilesReportDO;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author Deepak
 */
public class IncommingFilesImpl {

    static String whereami = "Pending File Report";
    private static final Logger LOGGER = Logger.getLogger(IncommingFilesImpl.class);

    public static List<IncommingFilesReportDO> getPendingFiles(IncommingFilesReportDO dobj) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<IncommingFilesReportDO> fileList = new ArrayList();
        try {
            tmgr = new TransactionManager("IncommingFilesImpl::getPendingFiles");
            String getUserDtls = "";
            getUserDtls = "select file_no,application_no,case when status='P' then 'Pending for Physical Sumbission' end status,pending_days,to_char(send_date,'dd-MM-yyyy HH:MI') as send_date from " + TableList.VT_FILE_DTLS + " where off_cd=? and send_by=? and status='P'";
            pstmt = tmgr.prepareStatement(getUserDtls);
            pstmt.setInt(1, Util.getUserOffCode());
            pstmt.setString(2, dobj.getDealerName());
            rs = tmgr.fetchDetachedRowSet();
            int i = 1;
            while (rs.next()) {
                IncommingFilesReportDO dobj1 = new IncommingFilesReportDO();
                dobj1.setSrNo(i++);
                dobj1.setFileNo(rs.getString("file_no"));
                dobj1.setAppl_no(rs.getString("application_no"));
                dobj1.setStatus(rs.getString("status"));
                dobj1.setEnter(false);
                dobj1.setPendingDays(rs.getInt("pending_days"));
                dobj1.setReceiveDate(rs.getString("send_date"));
                fileList.add(dobj1);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in geting Pending File Report");
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

    public static HashMap getDealers(String stateCode, int rtoName) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        Map dealerList = new HashMap();
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("IncommingFilesImpl::getDealers");
            String pageQuery = "select dealer_name,dealer_cd from " + TableList.VM_DEALER_MAST + " where state_cd=? and (off_cd=? OR regn_mark_gen_by_dealer=false)";
            pstmt = tmgr.prepareStatement(pageQuery);
            pstmt.setString(1, stateCode);
            pstmt.setInt(2, rtoName);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dealerList.put(rs.getString("dealer_name") + "(" + rs.getString("dealer_cd") + ")", rs.getString("dealer_cd"));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in geting Dealers");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }

            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);

            }
        }
        return (HashMap) dealerList;

    }

    public static boolean saveFileDetail(List<IncommingFilesReportDO> list, String file) throws VahanException {
        boolean retFlag = false;
        PreparedStatement pstmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("IncommingFilesImpl::saveFileDetail");
            String sql = "insert into " + TableList.VT_FMS_SUBMIT_FILE_DTLS + " select file_no,application_no,'S','File Submitted Successfully',send_by,pending_days,"
                    + "  current_timestamp,dealer_cd,off_cd,state_cd from " + TableList.VT_FILE_DTLS + " where file_no=?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, file);
            pstmt.executeUpdate();
            sql = "INSERT INTO " + TableList.VT_FILE_SUBMIT_DOCS + " (file_no,document_id,docs_enter_date)VALUES (?, ?,current_timestamp)";
            pstmt = tmgr.prepareStatement(sql);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isEnter()) {
                    pstmt.setString(1, file.trim());
                    pstmt.setInt(2, list.get(i).getServiceID());
                    pstmt.executeUpdate();
                }
            }
            sql = "delete from " + TableList.VT_FILE_DTLS + " where file_no=?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, file);
            pstmt.executeUpdate();
            tmgr.commit();
            retFlag = true;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in save File Details");
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

    public static boolean saveRejectFilesDetail(List<IncommingFilesReportDO> list, String file, String reason) throws VahanException {
        boolean retFlag = false;
        PreparedStatement pstmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("IncommingFilesImpl::saveRejectFilesDetail");
            String sql = "insert into " + TableList.VH_REJECT_FILE_DLTS + " select file_no,application_no,'R','" + reason + "',send_by,pending_days,"
                    + "  current_timestamp,dealer_cd,off_cd,state_cd from " + TableList.VT_FILE_DTLS + " where file_no=?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, file);
            pstmt.executeUpdate();
            sql = "update " + TableList.VT_FILE_DTLS + " set  status='R',status_desc='" + reason + "' where file_no=?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, file);
            pstmt.executeUpdate();
            sql = "INSERT INTO " + TableList.VT_FMS_REJECT_DOCUMENTS_DLTS + "  (file_no,document_id,documnet_title,reject_date)VALUES (?,?,?,current_timestamp)";
            pstmt = tmgr.prepareStatement(sql);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isEnter()) {
                    pstmt.setString(1, file.trim());
                    pstmt.setInt(2, list.get(i).getServiceID());
                    pstmt.setString(3, list.get(i).getServiceName());
                    pstmt.executeUpdate();
                }
            }
            tmgr.commit();
            retFlag = true;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in save Reject File Details");
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

    public static List<IncommingFilesReportDO> getModuleDesc() throws VahanException {
        List<IncommingFilesReportDO> list = new ArrayList<IncommingFilesReportDO>();
        int i = 1;
        RowSet rs = null;
        PreparedStatement pstmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("IncommingFilesImpl::getModuleDesc");
            String sqlQuery = "select document_id,documnet_title from " + TableList.VM_DOCUMENTS_DTLS + " ";
            pstmt = tmgr.prepareStatement(sqlQuery);
            rs = tmgr.fetchDetachedRowSet();
            IncommingFilesReportDO bean = null;
            while (rs.next()) {
                bean = new IncommingFilesReportDO();
                bean.setSerialNo("" + i++);
                bean.setEnter(false);
                bean.setServiceName(rs.getString("documnet_title"));
                bean.setServiceID(rs.getInt("document_id"));
                list.add(bean);
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Get Documents");

        } finally {
            try {
                try {
                    if (tmgr != null) {
                        tmgr.release();
                    }

                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);

                }

            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);

            }
        }
        return list;
    }

    public static void sendMessage(String error) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(error));
    }
}
