/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.eChallan;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.eChallan.ChallanReportDobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class DuplicateAndCancellationChallanImpl {

    private static final Logger LOGGER = Logger.getLogger(DuplicateAndCancellationChallanImpl.class);

    public boolean cancelChallan(String applNo, String reason) throws Exception {
        boolean flag = false;
        String sql;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("cancelChallan");
            sql = "INSERT INTO " + TableList.VH_CHALLAN + "(\n"
                    + "            appl_no, chal_no, regn_no, chal_date, chal_time, chal_place, \n"
                    + "            is_doc_impound, is_vch_impdound, chal_officer, nc_c_ref_no, remarks, \n"
                    + "            op_dt, state_cd, off_cd) SELECT appl_no, chal_no, regn_no, chal_date, chal_time, chal_place, \n"
                    + "       is_doc_impound, is_vch_impdound, chal_officer, nc_c_ref_no, ?, \n"
                    + "       op_dt, state_cd, off_cd\n"
                    + "  FROM " + TableList.VT_CHALLAN + " where appl_no=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, reason);
            psmt.setString(2, applNo);
            psmt.setString(3, Util.getUserStateCode());
            psmt.executeUpdate();
            psmt = null;
            sql = null;
            sql = "DELETE FROM " + TableList.VT_CHALLAN
                    + " WHERE  appl_no=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            psmt = null;
            sql = null;
            sql = "DELETE FROM " + TableList.VA_CHALLAN
                    + " WHERE  appl_no=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            psmt = null;
            sql = null;
            sql = "DELETE FROM " + TableList.VT_CHALLAN_ACCUSED
                    + " WHERE  appl_no=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            psmt = null;
            sql = null;
            sql = "DELETE FROM " + TableList.VT_CHALLAN_ADDL_INFO
                    + " WHERE  appl_no=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            psmt = null;
            sql = null;
            sql = "DELETE FROM " + TableList.VT_CHALLAN_AMT
                    + " WHERE  appl_no=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            psmt = null;
            sql = null;
            sql = "DELETE FROM " + TableList.VT_CHALLAN_OWNER
                    + " WHERE  appl_no=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            psmt = null;
            sql = null;
            sql = "DELETE FROM " + TableList.VT_CHALLAN_SETTLEMANT
                    + " WHERE appl_no=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            psmt = null;
            sql = null;
            sql = "DELETE FROM " + TableList.VT_WITNESS_DETAILS
                    + " WHERE  appl_no=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            psmt = null;
            sql = null;
            sql = "DELETE FROM " + TableList.VT_VCH_OFFENCES
                    + " WHERE  appl_no=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            psmt = null;
            sql = null;
            sql = "DELETE FROM " + TableList.VT_VEHICLE_IMPOUND
                    + " WHERE  appl_no=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            psmt = null;
            sql = null;
            sql = "DELETE FROM " + TableList.VT_DOCS_IMPOUND
                    + " WHERE  appl_no=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            psmt = null;
            sql = null;
            sql = "DELETE FROM " + TableList.VA_STATUS
                    + " WHERE  appl_no=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            psmt = null;
            sql = null;
            sql = "DELETE FROM " + TableList.VA_DETAILS
                    + " WHERE  appl_no=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            tmgr.commit();
            flag = true;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }


        }

        return flag;
    }
    //function to print challan report

    public ChallanReportDobj printChallanReport(String appl_no) throws VahanException {
        TransactionManager tmgr = null;
        ChallanReportDobj dobj = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        try {
            dobj = new ChallanReportDobj();
            String sql = " select challan.*,info.user_name,off.off_name from "
                    + TableList.VH_CHALLAN + " challan "
                    + " left join " + TableList.TM_USER_INFO + " info on info.user_cd=challan.chal_officer::numeric and  info.state_cd=challan.state_cd \n"
                    + " left join " + TableList.TM_OFFICE + " off on off.off_cd=challan.off_cd and  off.state_cd=challan.state_cd \n"
                    + "where challan.appl_no=? and challan.state_cd=? and challan.off_cd = ?";

            tmgr = new TransactionManager("printChallanReport");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj.setVcr_no(rs.getString("chal_no"));
                dobj.setVehicle_no(rs.getString("regn_no"));
                dobj.setChal_date(rs.getDate("chal_date"));
                dobj.setChal_place(rs.getString("chal_place"));
                dobj.setChal_time(rs.getString("chal_time"));
                dobj.setRemark(rs.getString("remarks"));
                dobj.setChal_officer(rs.getString("user_name"));
                dobj.setRto_name(rs.getString("off_name"));

            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
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
    //function to get list of application number

    public List getApplNo() throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        List applNo = new ArrayList();
        SelectItem item = new SelectItem("");
        try {
            String sql = "select appl_no from " + TableList.VT_CHALLAN
                    + " where appl_no not in(select appl_no from " + TableList.VH_CHALLAN
                    + " where state_cd=? and off_cd=?) and state_cd=? and off_cd=?";
            tmgr = new TransactionManager("getApplNo");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, Util.getUserStateCode());
            pstmt.setInt(2, Util.getSelectedSeat().getOff_cd());
            pstmt.setString(3, Util.getUserStateCode());
            pstmt.setInt(4, Util.getSelectedSeat().getOff_cd());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                item = new SelectItem(rs.getString("appl_no"));
                applNo.add(item);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return applNo;
    }

    public List getApplNoToPrintDuplicateChalllan() throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        List applNo = new ArrayList();
        SelectItem item = new SelectItem("");
        try {
            String sql = "Select appl_no from " + TableList.VA_CHALLAN + " where state_cd=? and off_cd=?";
            tmgr = new TransactionManager("getApplNoToPrintDuplicateChalllan");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, Util.getUserStateCode());
            pstmt.setInt(2, Util.getSelectedSeat().getOff_cd());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                item = new SelectItem(rs.getString("appl_no"));
                applNo.add(item);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return applNo;
    }
}
