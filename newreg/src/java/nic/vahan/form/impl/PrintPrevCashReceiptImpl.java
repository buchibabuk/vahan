/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.connection.TransactionManager;
import org.apache.log4j.Logger;

/**
 *
 * @author nic
 */
public class PrintPrevCashReceiptImpl {

    private static Logger LOGGER = Logger.getLogger(PrintPrevCashReceiptImpl.class);

    public boolean isReceiptExist(String rcpt_no, String userCode) throws VahanException {
        boolean isExist = false;
        TransactionManager tmgr = null;
        String sql;
        PreparedStatement ps = null;
        RowSet rs = null;
        Calendar cal = Calendar.getInstance();
        //Date date = cal.getTime();
        DateFormat writeFormat = new SimpleDateFormat("dd-MMM-yyyy");

        try {
            tmgr = new TransactionManager("PrintPrevCashReceiptImpl");
            sql = "select rcpt_no, rcpt_dt,current_date as current_date  from vt_fee where rcpt_no=? and state_cd=? and off_cd=? and collected_by=? "
                    + " union all select rcpt_no, rcpt_dt,current_date as current_date from vt_tax where rcpt_no=? and state_cd=? and off_cd=? and collected_by=?";// and to_char(rcpt_dt,'dd-Mon-yyyy')=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcpt_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            ps.setString(4, userCode);
            ps.setString(5, rcpt_no);
            ps.setString(6, Util.getUserStateCode());
            ps.setInt(7, Util.getSelectedSeat().getOff_cd());
            ps.setString(8, userCode);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                cal.setTime(rs.getDate("current_date"));
                cal.add(Calendar.DATE, -15);
                if (rs.getDate("rcpt_dt").before(cal.getTime()) && !userCode.equalsIgnoreCase(TableConstants.ONLINE_PAYMENT)) {
                    throw new VahanException("You can not print the Receipt No " + rcpt_no + " generated on 15 Days previous date!!!");
                }
                isExist = true;
            } else {
                throw new VahanException("Either Receipt No " + rcpt_no + " does not exist or you are not authorized to see this receipt!!!");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
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
}
