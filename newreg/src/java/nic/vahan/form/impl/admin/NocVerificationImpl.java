/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.admin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.NocDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

public class NocVerificationImpl {

    private static final Logger LOGGER = Logger.getLogger(NocVerificationImpl.class);

    public void insertIntoNOCVerification(NocDobj noc_dobj) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("insertIntoNOCVerification");

            sql = "SELECT * FROM " + TableList.VT_NOC_VERIFICATION + " WHERE regn_no = ? AND noc_no = ?";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, noc_dobj.getRegn_no());
            ps.setString(i++, noc_dobj.getNoc_no());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                throw new VahanException("Vehicle No " + noc_dobj.getRegn_no() + " and NOC No " + noc_dobj.getNoc_no() + " is already available in NOC Verification Records.");
            }

            sql = "INSERT INTO " + TableList.VT_NOC_VERIFICATION + "(state_cd, off_cd, from_state_cd, from_off_cd, regn_no, chasi_no, noc_no, noc_dt, ncrb_ref,entered_by, entered_on)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?,current_timestamp)";

            ps = tmgr.prepareStatement(sql);
            i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, noc_dobj.getState_from());
            ps.setInt(i++, noc_dobj.getOff_from());
            ps.setString(i++, noc_dobj.getRegn_no());
            ps.setString(i++, noc_dobj.getChasiNo());
            ps.setString(i++, noc_dobj.getNoc_no());
            java.sql.Date sqlDate = new java.sql.Date(noc_dobj.getNoc_dt().getTime());
            ps.setDate(i++, sqlDate);
            ps.setString(i++, noc_dobj.getNcrb_ref());
            ps.setLong(i++, Long.parseLong(Util.getEmpCode()));
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            tmgr.commit();
        } catch (SQLException se) {
            LOGGER.error(se);
            throw new VahanException("Problem In Verification of Data");
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

    public boolean checkNocVerified(String regnNo) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        boolean nocVerified = false;
        try {
            tmgr = new TransactionManager("checkNocVerified");

            sql = "SELECT * FROM " + TableList.VT_NOC_VERIFICATION + " WHERE regn_no = ?";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                nocVerified = true;
            }
        } catch (SQLException se) {
            LOGGER.error(se.toString() + " " + se.getStackTrace()[0]);
            throw new VahanException("Problem In Verification of Data");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return nocVerified;
    }
}
