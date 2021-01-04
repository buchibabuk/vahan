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
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.fileMonitoring.RejectedFileReportDO;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author Deepak
 */
public class RejectedFileReportImpl {

    static String whereami = "Rejected File Report";
    private static final Logger LOGGER = Logger.getLogger(RejectedFileReportImpl.class);

    public static List<RejectedFileReportDO> getRejectedFiles(RejectedFileReportDO dobj) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<RejectedFileReportDO> fileList = new ArrayList();
        Date fromdate = dobj.getFromdate();
        Date todate = dobj.getToDate();
        Calendar c = Calendar.getInstance();
        c.setTime(todate);
        java.sql.Date tDate = new java.sql.Date(c.getTimeInMillis());
        c.setTime(fromdate);
        java.sql.Date fDtate = new java.sql.Date(c.getTimeInMillis());
        try {
            tmgr = new TransactionManager("IncommingFilesImpl::getRejectedFiles");
            String getUserDtls = "";

            getUserDtls = "select a.file_no,a.application_no,case when a.status='R' then 'Rejected' end status,"
                    + "  a.status_desc,to_char(a.send_date,'dd-MM-yyyy HH:MI') as send_date,b.dealer_name  from " + TableList.VH_REJECT_FILE_DLTS + " a"
                    + "  left join vm_dealer_mast b on a.state_cd = b.state_cd and a.dealer_cd = b.dealer_cd"
                    + "  where a.off_cd=? and a.send_by=? and a.status='R' and a.send_date between  '" + fDtate + " 00:00:00' and '" + tDate + " 23:59:59'";
            pstmt = tmgr.prepareStatement(getUserDtls);
            pstmt.setInt(1, Util.getUserOffCode());
            pstmt.setString(2, dobj.getDealerName());
            rs = tmgr.fetchDetachedRowSet();
            int i = 1;
            while (rs.next()) {
                RejectedFileReportDO dobj1 = new RejectedFileReportDO();
                dobj1.setSrNo(i++);
                dobj1.setFileNo(rs.getString("file_no"));
                dobj1.setAppl_no(rs.getString("application_no"));
                dobj1.setStatus(rs.getString("status"));
                dobj1.setStatusDesc(rs.getString("status_desc"));
                dobj1.setRejectedDate(rs.getString("send_date"));
                dobj1.setDealerName(rs.getString("dealer_name"));
                fileList.add(dobj1);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in geting Reject File Report");
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

    public static void sendMessage(String error) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(error));
    }
}
