/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author acer
 */
public class LinkTractorTrailerImpl {

    public void saveLinkingDetails(String tractorRegnNo, String trailerRegnNo, String stateCode, Integer offCode) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String query = "";
        try {
            tmgr = new TransactionManager("saveLinkingDetails");
            query = "INSERT INTO " + TableList.VT_SIDE_TRAILER + "("
                    + "            regn_no, link_regn_no, state_cd, off_cd, op_dt)"
                    + "    VALUES (?, ?, ?, ?, current_timestamp)";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, trailerRegnNo);
            ps.setString(2, tractorRegnNo);
            ps.setString(3, stateCode);
            ps.setInt(4, offCode);
            int count = ps.executeUpdate();
            if (count > 0 && stateCode.equals("OR")) {
                query = "INSERT INTO " + TableList.VT_TAX_CLEAR + "("
                        + "        regn_no, off_cd, state_cd, appl_no,  pur_cd, clear_fr, clear_to, "
                        + "        tcr_no, iss_dt, remark, user_cd, op_dt, rcpt_no, rcpt_dt)"
                        + "SELECT  ?, off_cd, state_cd, ?, pur_cd, tax_from, tax_upto,\n"
                        + "        regn_no, CURRENT_TIMESTAMP, rcpt_no, collected_by, CURRENT_TIMESTAMP, rcpt_no, rcpt_dt"
                        + "  FROM " + TableList.VT_TAX
                        + " Where  regn_no = ? and state_cd = ? order by rcpt_dt desc limit 1";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, trailerRegnNo);
                ps.setString(2, "COMBINED TRAILOR");
                ps.setString(3, tractorRegnNo);
                ps.setString(4, stateCode);
                ps.executeUpdate();
            }
            if (count > 0) {
                tmgr.commit();
            }
        } catch (SQLException e) {
            throw new VahanException(e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                throw new VahanException(e.getMessage());
            }
        }
    }
}
