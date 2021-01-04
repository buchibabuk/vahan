/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.eapplication;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.Status_dobj;
import org.apache.log4j.Logger;

public class EApplicationImpl {

    private static final Logger LOGGER = Logger.getLogger(EApplicationImpl.class);
    private static final int CHASI_LENGHT = 5;

    public boolean isValidRegistration(String regn_no, String chasis_no) {
        boolean var = false;
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;

        if ((regn_no != null) && (chasis_no != null) && (chasis_no.length() == CHASI_LENGHT)) {

            try {
                tmgr = new TransactionManager("isValidRegistration");
                sql = "select regn_no,chasi_no from " + TableList.VT_OWNER + " where regn_no=? and right(chasi_no,5)=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no.toUpperCase());
                ps.setString(2, chasis_no.toUpperCase());
                RowSet rs = tmgr.fetchDetachedRowSet();

                if (rs.next()) {//found
                    var = true;//..................Registration no is valid..................
                }

            } catch (Exception e) {
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

        }

        return var;
    }

    public List<Status_dobj> getEApplicationStatus(String regn_no, int pur_cd, String tableName) {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        List<Status_dobj> list = new ArrayList<>();

        if (regn_no != null && tableName != null) {
            regn_no = regn_no.toUpperCase();
            try {
                tmgr = new TransactionManager("getEApplicationStatus");
                sql = "select a.regn_no,a.appl_no,b.* "
                        + "from " + tableName + " a, va_status b "
                        + "where a.appl_no = b.appl_no and a.regn_no=? "
                        + "order by b.op_dt desc";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no);
                RowSet rs = tmgr.fetchDetachedRowSet();

                if (rs.next()) {
                    Status_dobj dobj = new Status_dobj();

                    dobj.setAppl_no(rs.getString("appl_no"));
                    dobj.setRegn_no(rs.getString("regn_no"));
                    dobj.setPur_cd(rs.getInt("pur_cd"));
                    dobj.setAction_cd(rs.getInt("action_cd"));
                    dobj.setState_cd(rs.getString("state_cd"));
                    dobj.setOff_cd(rs.getInt("off_cd"));


                    list.add(dobj);
                }


            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
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
        }
        return list;
    }

    public String getEApplicationTOReason(String regn_no) {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String reason = null;

        regn_no = regn_no.toUpperCase();
        try {
            tmgr = new TransactionManagerReadOnly("getEApplicationStatus");
            sql = "select reason from " + TableList.VA_TO + " where regn_no = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                reason = rs.getString("reason");
            }


        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
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
        return reason;
    }
}
