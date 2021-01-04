/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import javax.sql.RowSet;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.hsrp.HSRP_dobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC075
 */
public class HSRPDetailsImpl {

    private static final Logger LOGGER = Logger.getLogger(HSRPDetailsImpl.class);

    public static HSRP_dobj getHsrpDetails(String state_cd, int officeCode, String regNo) {
        ArrayList listhsrp = new ArrayList();
        HSRP_dobj dobj = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement st = null;
        RowSet rs = null;
        try {
            tmgr = new TransactionManagerReadOnly("HSRPDetailsImpl.getHsrpDetails");
            String sqlQuery = "";
            sqlQuery = "select a.hsrp_no_front , a.hsrp_no_back , to_char(a.hsrp_fix_dt,'DD-Mon-YYYY') as hsrp_fix_dt , b.hsrp "
                    + " from " + TableList.VT_HSRP + " a "
                    + " , " + TableList.VM_SMART_CARD_HSRP + " b "
                    + " where a.regn_no = ? and a.state_cd=b.state_cd and a.off_cd = b.off_cd ";
            st = tmgr.prepareStatement(sqlQuery);
            st.setString(1, regNo);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new HSRP_dobj();
                try {
                    if (rs.getString("hsrp_no_front") == null) {
                        dobj.setHsrp_no_front(" ");
                    } else {
                        dobj.setHsrp_no_front(rs.getString("hsrp_no_front"));
                    }
                    if (rs.getString("hsrp_no_back") == null) {
                        dobj.setHsrp_no_back(" ");
                    } else {
                        dobj.setHsrp_no_back(rs.getString("hsrp_no_back"));
                    }
                    if (rs.getString("hsrp_fix_dt") == null) {
                        dobj.setHsrp_fix_dt(" ");
                    } else {
                        dobj.setHsrp_fix_dt(rs.getString("hsrp_fix_dt"));
                    }
                    if (!rs.getBoolean("hsrp")) {
                        dobj.setHsrp(false);
                    } else {
                        dobj.setHsrp(rs.getBoolean("hsrp"));
                    }
                    listhsrp.add(dobj);
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return dobj;
    }

    public static boolean isHsrp(String state_cd, int officeCode) {
        TransactionManager tmgr = null;
        String whereiam = "HSRPDetailsImpl.isHsrp";
        boolean isHsrp = false;
        try {
            tmgr = new TransactionManager(whereiam);
            isHsrp = ServerUtil.verifyForHsrp(state_cd, officeCode, tmgr);

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return isHsrp;
    }
}
