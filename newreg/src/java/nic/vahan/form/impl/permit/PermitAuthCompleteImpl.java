/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author R Gautam
 */
public class PermitAuthCompleteImpl {

    private static final Logger LOGGER = Logger.getLogger(PermitAuthCompleteImpl.class);

    public void insertInPermitAuthComplete(TransactionManager tmgr, String stateCd, int offCd, String applNo, String regnNo, String pmtNo) throws VahanException {
        PreparedStatement ps = null;
        String Query;
        try {
            getPrvPermitAuthComplete(tmgr, stateCd, regnNo);
            Query = "INSERT INTO permit.va_permit_auth_complete(\n"
                    + "            state_cd, off_cd, appl_no, regn_no, pmt_no, change_auth_dtls, op_dt)\n"
                    + "    VALUES (?, ?, ?, ?, ?, NOW() :: DATE, CURRENT_TIMESTAMP);";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setString(i++, applNo);
            ps.setString(i++, regnNo);
            ps.setString(i++, pmtNo);
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Problem in save date in Authrization Details");
        }
    }

    public void getPrvPermitAuthComplete(TransactionManager tmgr, String stateCd, String regnNo) throws VahanException {
        PreparedStatement ps = null;
        String Query;
        try {
            Query = "select * from permit.va_permit_auth_complete where state_cd = ?  and regn_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, stateCd);
            ps.setString(2, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                moveVaToVhPermitAuthComplete(tmgr, rs.getString("appl_no"), regnNo, stateCd);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Problem in get data in Authrization complete Details");
        }
    }

    public void moveVaToVhPermitAuthComplete(TransactionManager tmgr, String applNo, String regnNo, String state_cd) throws VahanException {
        PreparedStatement ps = null;
        try {
            String Query = "INSERT INTO permit.vh_permit_auth_complete(\n"
                    + "            state_cd, off_cd,appl_no, regn_no, pmt_no, change_auth_dtls, op_dt, moved_on, \n"
                    + "            moved_by)\n"
                    + "    SELECT state_cd, off_cd,appl_no, regn_no, pmt_no, change_auth_dtls, op_dt,CURRENT_TIMESTAMP,?\n"
                    + "      FROM permit.va_permit_auth_complete where state_cd=? and appl_no=? and regn_no=?  ";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, state_cd);
            ps.setString(3, applNo);
            ps.setString(4, regnNo);
            ps.executeUpdate();
            deleteAuthComplete(tmgr, applNo, regnNo, state_cd);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Problem in move data in Authrization Details");
        }
    }

    public void deleteAuthComplete(TransactionManager tmgr, String applNo, String regnNo, String state_cd) throws VahanException {
        PreparedStatement ps = null;
        try {
            String Query = "Delete from permit.va_permit_auth_complete where state_cd=? and appl_no=? and regn_no=?  ";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, state_cd);
            ps.setString(2, applNo);
            ps.setString(3, regnNo);
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Problem in Delete data in Authrization Complete Details");
        }
    }
}
