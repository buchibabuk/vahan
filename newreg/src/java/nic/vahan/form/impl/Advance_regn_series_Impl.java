/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Advance_regn_dobj;
import org.apache.log4j.Logger;

/**
 *
 * @author NICSI
 */
public class Advance_regn_series_Impl {

    private static final Logger LOGGER = Logger.getLogger(Advance_regn_series_Impl.class);

    public Map<String, Integer> getRegnSeries() throws VahanException {
        Map<String, Integer> map = new LinkedHashMap<String, Integer>();

        String getListQuery = "select a.descr,a.series_id, a.criteria_formula, b.prefix_series"
                + "  FROM  VM_SERIES a "
                + "  LEFT JOIN VM_REGN_SERIES b on a.series_id=b.series_id and a.state_cd=b.state_cd and b.off_cd=?"
                + "  WHERE a.state_cd=?"
                + "  order by series_id";
        VahanException e = null;
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        try {
            tmgr = new TransactionManager("Advance_regn_series_Impl:getRegnSeries");
            pstmt = tmgr.prepareStatement(getListQuery);
            pstmt.setInt(1, Util.getUserOffCode());
            pstmt.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                map.put(rs.getInt("series_id") + "-" + rs.getString("descr"), rs.getInt("series_id"));
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = new VahanException("Problem in fetched data");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = new VahanException("Problem in fetched data");
        } finally {
            try {

                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                e = new VahanException("Problem in fetched data");
            }
        }
        if (e != null) {
            throw e;
        }

        return map;
    }

    public List<Advance_regn_dobj> fetchAllAdvanceRegnSeries() throws VahanException {

        List<Advance_regn_dobj> list = new ArrayList<Advance_regn_dobj>();
        String getListQuery = "select series_id,prefix_series from " + TableList.VM_ADVANCE_REGN_SERIES + " where state_cd =? and off_cd=? order by series_id";
        VahanException e = null;
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("Regn_series_Impl:addRegnSeries");
            pstmt = tmgr.prepareStatement(getListQuery);
            pstmt.setString(1, Util.getUserStateCode());
            pstmt.setInt(2, Util.getUserOffCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                Advance_regn_dobj dobj = new Advance_regn_dobj();
                dobj.setSeries_no(rs.getInt("series_id"));
                dobj.setPrefix_series(rs.getString("prefix_series"));
                list.add(dobj);
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = new VahanException("Problem in fetched data");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = new VahanException("Problem in fetched data");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                e = new VahanException("Problem in fetched data");
            }
        }
        if (e != null) {
            throw e;
        }

        return list;
    }

    public String saveOrCheckAdvanceSeries(String prifix_series_No, String series_id, String state_Cd, String emp_code, int off_cd) throws VahanException {

        TransactionManager tmgr = null;
        String flag_chk = "";
        try {
            if (!prifix_series_No.substring(0, 2).equals(state_Cd)) {
                flag_chk = "Prefix value does not belong to your State !";
                throw new VahanException(flag_chk);
            }

            tmgr = new TransactionManager("Advance_regn_series_Impl:saveOrCheckAdvanceSeries");
            String qry = "select * from " + TableList.VM_ADVANCE_REGN_SERIES + " where state_cd  =? and off_cd=? and prefix_series=? ";
            PreparedStatement pstmt = tmgr.prepareStatement(qry);
            pstmt.setString(1, state_Cd);
            pstmt.setInt(2, off_cd);
            pstmt.setString(3, prifix_series_No);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                flag_chk = "Prefix Series Already Exist.";
                throw new VahanException(flag_chk);
            }
            qry = "INSERT INTO " + TableList.VM_ADVANCE_REGN_SERIES + "(series_id, prefix_series, entered_by, entered_on, state_cd, off_cd) VALUES (?, ?, ?, current_timestamp, ?, ?)";

            pstmt = tmgr.prepareStatement(qry);
            pstmt.setInt(1, Integer.parseInt(series_id));
            pstmt.setString(2, prifix_series_No);
            pstmt.setString(3, emp_code);
            pstmt.setString(4, state_Cd);
            pstmt.setInt(5, off_cd);
            int a = pstmt.executeUpdate();
            if (a > 0) {
                flag_chk = "Prefix Series saved Successfully !";
                tmgr.commit();
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in saveOrCheckAdvanceSeries" + ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in saveOrCheckAdvanceSeries" + ex.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                throw new VahanException("Error in saveOrCheckAdvanceSeries" + ex.getMessage());
            }

        }

        return flag_chk;
    }
}
