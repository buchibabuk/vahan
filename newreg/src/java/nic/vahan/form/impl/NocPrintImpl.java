/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.reports.NOCReportDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author nic
 */
public class NocPrintImpl {

    private static final Logger LOGGER = Logger.getLogger(NocPrintImpl.class);

    public NOCReportDobj isApplExistForNOC(String enterNo, String radioBtnvalue, boolean print_no_dues_cert) throws VahanException {
        NOCReportDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String sql;
        RowSet rs;
        String whereClause = null;
        boolean printnoduescert = false;
        try {
            if (radioBtnvalue.equals("A")) {
                whereClause = "and a.appl_no=upper('" + enterNo + "')  ";
            } else if (radioBtnvalue.equals("R")) {
                whereClause = "and a.regn_no=upper('" + enterNo + "') ";
            }
            tmgr = new TransactionManager("NocPrintImpl.isApplExistForNOC method");
            if (print_no_dues_cert) {
                sql = "select a.appl_no from  " + TableList.VT_NOC + " a "
                        + " inner join " + TableList.VA_TO + " b on b.appl_no = a.appl_no and b.state_cd = a.state_to"
                        + " where a.state_cd=? and a.off_cd=?" + whereClause + " order by a.noc_dt desc limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (!rs.next()) {
                    printnoduescert = false;
                    sql = "select a.appl_no from  " + TableList.VT_NOC + " a "
                            + " where a.state_cd=? and a.off_cd=?" + whereClause + " order by a.noc_dt desc limit 1";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                    rs = tmgr.fetchDetachedRowSet();
                } else {
                    printnoduescert = true;
                }
                rs.beforeFirst();
                if (rs.next()) {
                    dobj = new NOCReportDobj();
                    dobj.setAppl_no(rs.getString("appl_no"));
                    dobj.setPrint_no_dues_cert(printnoduescert);
                }

            } else {
                sql = "select a.appl_no from  " + TableList.VT_NOC + " a "
                        + " where a.state_cd=? and a.off_cd=?" + whereClause + " order by a.noc_dt desc limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    dobj = new NOCReportDobj();
                    dobj.setAppl_no(rs.getString("appl_no"));
                    dobj.setPrint_no_dues_cert(false);
                } else {
                    throw new VahanException("Application / Registration does not exist or you are not authorized to print NOC Slip for this application.!");
                }
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
        return dobj;
    }
}
