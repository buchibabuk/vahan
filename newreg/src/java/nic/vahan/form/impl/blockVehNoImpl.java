/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.BlockVehicleNumberDobj;
import nic.vahan.server.CommonUtils;

/**
 *
 * @author Administrator
 */
public class blockVehNoImpl {

    public static void getVehSeries(List listVehSeries) throws Exception {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select distinct on (prefix_series) prefix_series from ("
                + "select distinct prefix_series from " + TableList.VM_REGN_SERIES + " where state_cd=? and off_cd=? "
                + " union select distinct next_prefix_series as prefix_series from " + TableList.VM_REGN_SERIES + " where state_cd=? and off_cd=? "
                + " union select distinct prefix_series from " + TableList.VHM_REGN_SERIES + " where state_cd=? and off_cd=? "
                + " union select distinct prefix_series from " + TableList.VM_ADVANCE_REGN_SERIES + " where state_cd=? and off_cd=?) a order by 1";
        try {
            tmgr = new TransactionManager("getVehSeries");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserSeatOffCode());
            ps.setString(3, Util.getUserStateCode());
            ps.setInt(4, Util.getUserSeatOffCode());
            ps.setString(5, Util.getUserStateCode());
            ps.setInt(6, Util.getUserSeatOffCode());
            ps.setString(7, Util.getUserStateCode());
            ps.setInt(8, Util.getUserSeatOffCode());
            rs = ps.executeQuery();

            while (rs.next()) {
                if (!CommonUtils.isNullOrBlank(rs.getString("prefix_series"))) {
                    listVehSeries.add(rs.getString("prefix_series"));
                }
            }
        } catch (SQLException e) {
            throw new Exception(e);

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }

    public static boolean updateBlockVehNo(BlockVehicleNumberDobj dobj) throws Exception {
        boolean isSave = false;
        boolean vehFound = false;
        int no;
        int i = 0;
        String strRegnNo = "";
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        tmgr = new TransactionManager("updateBlockVehNo");
        String sql = "insert into " + TableList.VT_BLOCK_VEH + " values(?,?,?,?,current_timestamp)";
        try {
            for (no = dobj.getFromNumber(); no <= dobj.getToNumber(); no++) {
                String vehNo = no + "";
                if (vehNo.length() == 3) {
                    vehNo = "0" + vehNo;
                } else if (vehNo.length() == 2) {
                    vehNo = "00" + vehNo;
                } else if (vehNo.length() == 1) {
                    vehNo = "000" + vehNo;
                }
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getUserSeatOffCode());
                strRegnNo = dobj.getVehSeries() + vehNo;
                ps.setString(3, strRegnNo);
                ps.setLong(4, Long.parseLong(Util.getEmpCode()));
                vehFound = isVehicleNoBlocked(tmgr, strRegnNo);
                if (!vehFound) {
                    i = ps.executeUpdate();
                }
            }
            if (i > 0) {
                isSave = true;
                tmgr.commit();
            } else {
                isSave = false;
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }
        return isSave;
    }

    public static boolean isVehicleNoBlocked(TransactionManager tmgr, String regnNo) throws VahanException, Exception {
        boolean vehicleFound = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from " + TableList.VT_BLOCK_VEH + " where state_cd=? and off_cd=? and regn_no=?";

        try {
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserSeatOffCode());
            ps.setString(3, regnNo);
            rs = ps.executeQuery();
            if (rs.next()) {
                vehicleFound = true;
            } else {
                vehicleFound = false;
            }

        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return vehicleFound;
    }

    public static void listofBlockedVeh(List<BlockVehicleNumberDobj> blockedNo) throws VahanException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        BlockVehicleNumberDobj dobj = null;
//        String sql = "select regn_no from " + TableList.VT_BLOCK_VEH + " where state_cd =? and off_cd=? and"
//                + " right(regn_no,4) not in(select fancy_number from vm_fancy_mast where state_cd=?) order by regn_no";
//        
        String sql = "select regn_no from " + TableList.VT_BLOCK_VEH + " where state_cd =? and off_cd=? order by regn_no";
        try {
            tmgr = new TransactionManager(("listofBlockedVeh"));
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserSeatOffCode());
//            ps.setString(3, Util.getUserStateCode());
            rs = ps.executeQuery();
            while (rs.next()) {
                dobj = new BlockVehicleNumberDobj();
                dobj.setBlockedRegnNo(rs.getString("regn_no"));
                blockedNo.add(dobj);
            }

        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }

    public static boolean releaseVehicle(String regnNo) throws Exception {
        boolean isRelease = false;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = "";
        tmgr = new TransactionManager("releaseVehicle");
        try {
            sql = "insert into " + TableList.VH_BLOCK_VEH + " select state_cd,off_cd,regn_no,blocked_by,blocked_dt,?,current_timestamp"
                    + " from " + TableList.VT_BLOCK_VEH + " where state_cd =? and off_cd=? and regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(Util.getEmpCode()));
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getUserSeatOffCode());
            ps.setString(4, regnNo);
            int i = ps.executeUpdate();
            sql = "delete from " + TableList.VT_BLOCK_VEH + " where state_cd=? and off_cd=? and regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserSeatOffCode());
            ps.setString(3, regnNo);
            i = ps.executeUpdate();
            if (i > 0) {
                isRelease = true;
                tmgr.commit();
            } else {
                isRelease = false;
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }
        return isRelease;
    }
}
