/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.NidChangedDobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author ASHOK
 */
public class NidChangedImpl {

    private static final Logger LOGGER = Logger.getLogger(NidChangedImpl.class);

    public void UpdateFitNid(NidChangedDobj dobj) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        int i = 1;
        try {

            tmgr = new TransactionManager("UpdateFitNid");

            sql = " INSERT INTO " + TableList.VH_FITNESS_NID
                    + " (state_cd, off_cd, regn_no, old_fit_nid, new_fit_nid, reason, moved_on, moved_by)"
                    + "    VALUES (?, ?, ?, ?, ?, ?, current_timestamp,?)";

            ps = tmgr.prepareStatement(sql);

            ps.setString(i++, dobj.getState_cd());
            ps.setInt(i++, dobj.getOff_cd());
            ps.setString(i++, dobj.getRegn_no());
            if (dobj.getOld_fit_nid() == null) {
                ps.setNull(i++, java.sql.Types.DATE);
            } else {
                ps.setDate(i++, new java.sql.Date(dobj.getOld_fit_nid().getTime()));
            }
            ps.setDate(i++, new java.sql.Date(dobj.getNew_fit_nid().getTime()));
            ps.setString(i++, dobj.getReason());
            ps.setString(i++, dobj.getMoved_by());
            ps.executeUpdate();


            sql = " update " + TableList.VT_FITNESS + " set "
                    + " fit_nid=?,"
                    + " op_dt=current_timestamp "
                    + " WHERE regn_no=? and state_cd=? ";
            ps = tmgr.prepareStatement(sql);
            i = 1;
            ps.setDate(i++, new java.sql.Date(dobj.getNew_fit_nid().getTime()));
            ps.setString(i++, dobj.getRegn_no());
            ps.setString(i++, dobj.getState_cd());
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

            tmgr.commit();

        } catch (VahanException ve) {
            throw ve;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error In Datebase Process For Updating NID");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

    }
}
