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
import nic.vahan.form.dobj.UpdateTaxNoticeHeadsDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author afzal
 */
public class UpdateTaxNoticeHeadsImpl {

    private static final Logger LOGGER = Logger.getLogger(UpdateTaxNoticeHeadsImpl.class);

    public UpdateTaxNoticeHeadsDobj getHeadDetails(String state_cd, String headvalue) throws VahanException {
        UpdateTaxNoticeHeadsDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String sql;
        RowSet rs;
        try {
            tmgr = new TransactionManager("getHeadDetails");
            sql = "select " + headvalue + " from  " + TableList.VT_TAX_NOTICE_HEAD + " where state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new UpdateTaxNoticeHeadsDobj();
                dobj.setHead(rs.getString(1));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error is getting tax head value from VT_TAX_NOTICE_HEAD");
        } catch (Exception e) {
            LOGGER.error(e);
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return dobj;
    }

    public boolean updateHeadDetails(String state_cd, String headcolumn, String headvalue) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String sql;
        boolean status = false;
        try {
            tmgr = new TransactionManager("getHeadDetails");
            sql = "update " + TableList.VT_TAX_NOTICE_HEAD + " set " + headcolumn + "='" + headvalue + "' where state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.executeUpdate();
            tmgr.commit();
            status = true;

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error is getting tax head values from VT_TAX_NOTICE_HEAD");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return status;
    }
}
