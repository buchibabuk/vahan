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
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.eChallan.ChallanHoldDobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class ChallanHoldImpl {

    private static Logger LOGGER = Logger.getLogger(ChallanHoldImpl.class);

    public ChallanHoldDobj getChallanDetails(String applNo) throws VahanException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        ChallanHoldDobj dobj = null;

        try {
            String sql = "SELECT  challan.chal_no,challan.chal_place, challan.regn_no, challan.chal_date,owner.owner_name,userr.user_name,\n"
                    + " challan.chal_time\n"
                    + "  FROM " + TableList.VT_CHALLAN + " challan\n"
                    + " LEFT OUTER JOIN  " + TableList.VIEW_VV_OWNER + " owner on challan.regn_no=owner.regn_no and challan.state_cd=owner.state_cd\n"
                    + "  LEFT OUTER JOIN " + TableList.TM_USER_INFO + " userr ON challan.chal_officer:: numeric=userr.user_cd and challan.state_cd=userr.state_cd\n"
                    + "  where challan.appl_no=? and challan.state_cd=?";

            tmgr = new TransactionManager("getChallanDetails");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, applNo);
            pstmt.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new ChallanHoldDobj();
                dobj.setChallanNo(rs.getString("chal_no"));
                dobj.setChallanDt(rs.getString("chal_date"));
                dobj.setChallanOfficer(rs.getString("user_name"));
                dobj.setChallanTime(rs.getString("chal_time"));
                dobj.setChallanPlace(rs.getString("chal_place"));
                dobj.setOwnerName(rs.getString("owner_name"));
                dobj.setVehicleNo(rs.getString("regn_no"));


            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj;

    }

    public boolean holdAndActiveChallan(ChallanHoldDobj dobj, String applNo) throws VahanException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        String status = "";
        boolean flag = false;

        try {
            tmgr = new TransactionManager("holdAndActiveChallan");
            String sql = "INSERT INTO " + TableList.VT_CHALLAN_HOLD + "(\n"
                    + "            appl_no, regn_no, hold_fee, hold_status, hold_reasons, hold_from, \n"
                    + "            hold_upto, state_cd, off_cd)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?)";


            if (dobj.getChallanHoldStatus().equals("ACTIVE")) {
                status = "A";
            }
            if (dobj.getChallanHoldStatus().equals("HOLD")) {
                status = "H";
            }
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, applNo);
            pstmt.setString(2, dobj.getVehicleNo());
            pstmt.setInt(3, dobj.getHoldFee());
            pstmt.setString(4, status);
            pstmt.setString(5, dobj.getHoldReason());
            pstmt.setTimestamp(6, ChallanUtil.getTimeStamp(dobj.getHoldFrom()));
            pstmt.setTimestamp(7, ChallanUtil.getTimeStamp(dobj.getHoldUpto()));
            pstmt.setString(8, Util.getUserStateCode());
            pstmt.setInt(9, Util.getUserOffCode());
            int val = pstmt.executeUpdate();
            if (val > 0) {
                tmgr.commit();
                flag = true;
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return flag;
    }
    //function to get list of application number

    public List getApplNo() throws SQLException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        List applNo = new ArrayList();
        SelectItem item = new SelectItem("");
        try {
            String sql = "select appl_no from " + TableList.VT_CHALLAN
                    + " where state_cd=? and off_cd=?";
            tmgr = new TransactionManager("getApplNo");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, Util.getUserStateCode());
            pstmt.setInt(2, Util.getUserOffCode());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                item = new SelectItem(rs.getString("appl_no"));
                applNo.add(item);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return applNo;
    }

    public boolean isChallanHold(String applNo) throws VahanException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        boolean flag = false;

        try {
            tmgr = new TransactionManager("holdAndActiveChallan");
            String sql1 = "select * from " + TableList.VT_CHALLAN_HOLD + " where appl_no=? and state_cd=?";
            pstmt = tmgr.prepareStatement(sql1);
            pstmt.setString(1, applNo);
            pstmt.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {


                flag = true;

            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return flag;
    }
}
