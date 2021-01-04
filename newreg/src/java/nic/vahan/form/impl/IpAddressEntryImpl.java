/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.net.InetAddress;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.IpAddressEntryDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author acer
 */
public class IpAddressEntryImpl {

    private static final Logger LOGGER = Logger.getLogger(IpAddressEntryImpl.class);

    public static List<IpAddressEntryDobj> getIpAddressList(String stateCd, int offCd) throws VahanException {
        List<IpAddressEntryDobj> listUser = new ArrayList();
        String sql = " select ip_address from " + TableList.TM_USER_IPADDRESS + " where state_cd=? and off_cd=?  ";
        TransactionManager tmgr = null;
        IpAddressEntryDobj dobj = null;
        try {
            tmgr = new TransactionManager("getIpAddressList");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new IpAddressEntryDobj();
                dobj.setIpAddress(rs.getString("ip_address"));
                listUser.add(dobj);
            }
        } catch (Exception e) {
            listUser = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some error occurred while fetching the record");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return listUser;
    }

    public static String insertDataintoDataTable(List<IpAddressEntryDobj> ipAddressList, String state_cd, int offCd, String emp_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String msg = null;
        String sql = "";
        String ipAddresses = "";
        try {
            tmgr = new TransactionManager("insertDataintoDataTable");
            int i = 1;
            for (IpAddressEntryDobj dobj : ipAddressList) {
                sql = "select ip_address from " + TableList.TM_USER_IPADDRESS + " where state_cd= ? and off_cd = ? and ip_address = ? ";
                ps = tmgr.prepareStatement(sql);
                i = 1;
                ps.setString(i++, state_cd);
                ps.setInt(i++, offCd);
                ps.setString(i++, dobj.getIpAddress());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                ipAddresses = ipAddresses + "'" + dobj.getIpAddress() + "',";
                if (!rs.next()) {
                    int j = 0;
                    sql = "INSERT INTO  " + TableList.TM_USER_IPADDRESS + "(state_cd, off_cd, ip_address, entered_by, "
                            + " entered_on) VALUES (?, ?, ?, ?, current_timestamp)";
                    ps = tmgr.prepareStatement(sql);
                    i = 1;
                    ps.setString(i++, state_cd);
                    ps.setInt(i++, offCd);
                    ps.setString(i++, dobj.getIpAddress());
                    ps.setString(i++, Util.getEmpCode());
                    j = ps.executeUpdate();
                }

            }
            ipAddresses = ipAddresses.substring(0, ipAddresses.length() - 1);

            String insertHisSql = "INSERT INTO " + TableList.THM_USER_IPADDRESS + " SELECT current_timestamp as moved_on,? as moved_by,state_cd, off_cd, ip_address, entered_by, entered_on "
                    + " FROM " + TableList.TM_USER_IPADDRESS + " where state_cd = ? and off_cd = ? and  ip_address not in (" + ipAddresses + ")";

            ps = tmgr.prepareStatement(insertHisSql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "delete from " + TableList.TM_USER_IPADDRESS + " where ip_address not in (" + ipAddresses + ") and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            i = 1;
            ps.setString(i++, state_cd);
            ps.setInt(i++, offCd);
            ps.executeUpdate();
            tmgr.commit();
            msg = "IP Address SuccessFully Updated";
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            msg = "Data Not Saved.";
            throw new VahanException(msg);
        } finally {
            try {
                ps.close();
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                msg = "Data Not Saved.";
            }
        }
        return msg;
    }

    public static String deleteAllIpRecord(List<IpAddressEntryDobj> ipAddressList, String state_cd, int offCd, String emp_cd) throws VahanException {
        String msg = null;
        PreparedStatement psInsertHistory = null;
        PreparedStatement psDelete = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        int count = 0;
//        String sql = "";
//        String ipAddresses = "";
        try {
            tmgr = new TransactionManager("delete ip Details");
            String insertHisSql = "INSERT INTO " + TableList.THM_USER_IPADDRESS + " SELECT current_timestamp as moved_on,? as moved_by,state_cd, off_cd, ip_address, entered_by, entered_on "
                    + " FROM " + TableList.TM_USER_IPADDRESS + " where state_cd = ? and off_cd = ? ";

            psInsertHistory = tmgr.prepareStatement(insertHisSql);
            psInsertHistory.setString(1, Util.getEmpCode());
            psInsertHistory.setString(2, Util.getUserStateCode());
            psInsertHistory.setInt(3, offCd);
            psInsertHistory.executeUpdate();

            String deleteSql = " delete from " + TableList.TM_USER_IPADDRESS + " where state_cd = ? and off_cd = ? ";
            psDelete = tmgr.prepareStatement(deleteSql);
            psDelete.setString(1, Util.getUserStateCode());
            psDelete.setInt(2, offCd);
            psDelete.executeUpdate();
            tmgr.commit();
            msg = "IP Address SuccessFully Deleted";
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            msg = "Data Not Saved.";
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                    msg = "Data Not Saved.";
                }
            }
        }
        return msg;
    }

    public String[] getIpDetails() throws VahanException {
        String[] ipArray = new String[2];
        InetAddress ip = null;
        String serverIP = null;
        String ipAddress = null;
        try {
            ip = InetAddress.getLocalHost();
            serverIP = ip.toString().split("/")[1];
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            ipAddress = request.getRemoteAddr();
            ipArray[0] = serverIP;
            ipArray[1] = ipAddress;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Getting IP Details");
        }
        return ipArray;
    }
}
