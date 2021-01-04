/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.eChallan;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.sql.RowSet;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.eChallan.InsertCDDataInTableDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
public class InsertCDDataInTableImpl {

    private static final Logger LOGGER = Logger.getLogger(InsertCDDataInTableImpl.class);

    public boolean insertCDData(List<InsertCDDataInTableDobj> list) throws Exception {
        boolean flag = false;
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        RowSet rs = null;
        String sql = "";
        String vt_challan_owner = "";
        String vt_challan = "";
        String chassis = "";
        String owner_name = "";
        int vh_class = 0;
        int seat_cap = 0;
        int stand_cap = 0;
        int sleep_cap = 0;
        int ld_wt = 0;
        int fuel = 0;
        String color = "";


        try {
            tmgr = new TransactionManager("InsertCDDataInTableImpl.insertCDData()");
            sql = "select * from " + TableList.VT_OWNER + "where regn_no=? and state_cd?";
            for (InsertCDDataInTableDobj dobj : list) {
                String applicationNo = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
                pstmt = tmgr.prepareStatement(sql);
                pstmt.setString(1, dobj.getRegn_no());
                pstmt.setString(2, Util.getUserStateCode());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    owner_name = rs.getString("owner_name");
                    chassis = rs.getString("chasi_no");
                    vh_class = rs.getInt("vh_class");
                    seat_cap = rs.getInt("seat_cap");
                    sleep_cap = rs.getInt("sleeper_cap");
                    stand_cap = rs.getInt("stand_cap");
                    ld_wt = rs.getInt("ld_wt");
                    fuel = rs.getInt("fuel");
                    color = rs.getString("color");
                }
                //data insert into vt_challan_owner
                vt_challan_owner = "INSERT INTO " + TableList.VT_CHALLAN_OWNER + "(\n"
                        + "            appl_no, regn_no, chasi_no, vh_class, vch_off_cd, vch_state_cd, \n"
                        + "            owner_name, seat_cap, stand_cap, sleeper_cap, ld_wt, color, fuel, \n"
                        + "            state_cd, off_cd)\n"
                        + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, ?, ?, ?, \n"
                        + "            ?, ?)";
                pstmt = tmgr.prepareStatement(vt_challan_owner);
                pstmt.setString(1, applicationNo);
                pstmt.setString(2, dobj.getRegn_no());
                pstmt.setString(3, chassis);
                pstmt.setInt(4, vh_class);
                pstmt.setInt(5, Util.getSelectedSeat().getOff_cd());
                pstmt.setString(6, Util.getUserStateCode());
                pstmt.setString(7, owner_name);
                pstmt.setInt(8, seat_cap);
                pstmt.setInt(9, stand_cap);
                pstmt.setInt(10, sleep_cap);
                pstmt.setInt(11, ld_wt);
                pstmt.setString(12, color);
                pstmt.setInt(13, fuel);
                pstmt.setString(14, Util.getUserStateCode());
                pstmt.setInt(15, Integer.parseInt(dobj.getOffice()));
                pstmt.executeUpdate();
                //data insert into vt_challan
                vt_challan = "INSERT INTO " + TableList.VT_CHALLAN + "(\n"
                        + "            appl_no, chal_no, regn_no, chal_date, chal_time, chal_place, \n"
                        + "            is_doc_impound, is_vch_impdound, chal_officer, \n"
                        + "            op_dt, state_cd, off_cd)\n"
                        + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, ?, \n"
                        + "            ?)";
                pstmt = tmgr.prepareStatement(vt_challan);
                pstmt.setString(1, applicationNo);
                pstmt.setString(2, dobj.getChal_no());
                pstmt.setString(3, dobj.getRegn_no());
                pstmt.setString(4, dobj.getChal_date());
                pstmt.setString(5, dobj.getChal_time());
                pstmt.setString(6, dobj.getChal_place());
                pstmt.setString(7, "N");
                pstmt.setString(8, "N");
                pstmt.setString(9, Util.getEmpCode());
                pstmt.setString(10, dobj.getOp_date());
                pstmt.setString(11, Util.getUserStateCode());
                pstmt.setInt(12, Integer.parseInt(dobj.getOffice()));
                pstmt.executeUpdate();
                tmgr.commit();


            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {

            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean checkRegnNo(String regn_no) throws Exception {
        boolean flag = false;

        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        RowSet rs = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("InsertCDDataInTableImpl.checkRegnNo()");
            sql = "select * from " + TableList.VT_CHALLAN + "where regn_no=? and state_cd?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, regn_no);
            pstmt.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = true;
            }
            sql = "select * from " + TableList.VT_CHALLAN_OWNER + "where regn_no=? and state_cd?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, regn_no);
            pstmt.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {

            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flag;
    }
}
