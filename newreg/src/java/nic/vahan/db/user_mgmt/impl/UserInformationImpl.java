/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.impl;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.db.user_mgmt.dobj.UserInformationDobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author acer
 */
public class UserInformationImpl {

    private static final Logger LOGGER = Logger.getLogger(UserInformationImpl.class);

    public List<UserInformationDobj> getOfficeAdmin() throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        RowSet rs = null;
        UserInformationDobj dobj = null;
        List<UserInformationDobj> list = new ArrayList<>();
        String getlistrecord = "Select user_name, i.off_cd, user_id, o.off_name, user_cd from " + TableList.TM_USER_INFO + " i "
                + " left join " + TableList.TM_OFFICE + " o on i.state_cd = o.state_cd and i.off_cd= o.off_cd where user_catg = 'A' and i.state_cd= ? order by off_cd";
        try {
            tmgr = new TransactionManagerReadOnly("getOfficeAdmin");
            ps = tmgr.prepareStatement(getlistrecord);
            ps.setString(1, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UserInformationDobj();
                dobj.setUser_name(rs.getString("user_name"));
                dobj.setOfficeName(rs.getString("off_name"));
                dobj.setUser_id(rs.getString("user_id"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setUser_cd(rs.getInt("user_cd"));
                list.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return list;
    }

    public List<UserInformationDobj> getUserUnderOffAdmin(int off_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        RowSet rs = null;
        UserInformationDobj dobj = null;
        List<UserInformationDobj> list = new ArrayList<>();
        String getlistrecord = " select user_name,user_cd,user_id,off_name,i.off_cd,c.descr, case when (i.status = 'A') then 'Online' else 'Offline' end as user_status from " + TableList.TM_USER_INFO + " i "
                + " left join " + TableList.TM_USER_CATG + " c on i.user_catg = c.code "
                + " left join " + TableList.TM_OFFICE + " o on i.state_cd= o.state_cd and i.off_cd = o.off_cd "
                + " where user_catg IN('Y','J','H','L','X','T') and i.state_cd=? and i.off_cd=? order by user_name";
        try {
            tmgr = new TransactionManagerReadOnly("getOfficeAdmin");
            ps = tmgr.prepareStatement(getlistrecord);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, off_cd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UserInformationDobj();
                dobj.setUser_name(rs.getString("user_name"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setUser_cd(rs.getInt("user_cd"));
                dobj.setOfficeName(rs.getString("off_name"));
                dobj.setUser_id(rs.getString("user_id"));
                dobj.setUserCatg_descr(rs.getString("descr"));
                dobj.setUser_status(rs.getString("user_status"));
                list.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return list;
    }

    public List<UserInformationDobj> getAssignAction(UserInformationDobj assigninfo) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        RowSet rs = null;
        UserInformationDobj dobj = null;
        List<UserInformationDobj> list = new ArrayList<>();
        String getlistrecord = "  select i.user_cd,i.off_cd,to_char(i.entered_on,'dd-Mon-yyyy')as entered_on,i.state_cd,c.action_cd,c.action_abbrv,c.action_descr,u.user_name"
                + " from " + TableList.TM_OFF_EMP_ACTION + " i  "
                + " left join " + TableList.TM_ACTION + " c on c.action_cd = i.action_cd "
                + " left join " + TableList.TM_USER_INFO + " u on u.user_cd = i.user_cd "
                + " where i.state_cd=? and i.off_cd=? and i.user_cd=? ";
        try {
            tmgr = new TransactionManagerReadOnly("getAssignAction");
            ps = tmgr.prepareStatement(getlistrecord);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, assigninfo.getOff_cd());
            ps.setInt(3, assigninfo.getUser_cd());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UserInformationDobj();
                dobj.setUser_name(rs.getString("user_name"));
                dobj.setAction_name(rs.getString("action_abbrv"));
                dobj.setAction_descr(rs.getString("action_descr"));
                dobj.setEntered_on(rs.getString("entered_on"));
                list.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return list;
    }

    public List<UserInformationDobj> getOfficeTiming() throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        RowSet rs = null;
        UserInformationDobj dobj = null;
        List<UserInformationDobj> list = new ArrayList<>();
        String getrecord = "  select i.state_cd,i.off_cd,open_time,close_time,o.off_name from " + TableList.TM_CONFIGURATION_LOGIN + " i "
                + " left join " + TableList.TM_OFFICE + " o on i.state_cd = o.state_cd and i.off_cd = o.off_cd "
                + " where i.state_cd=? order by off_name ";
        try {
            tmgr = new TransactionManagerReadOnly("getOfficeTiming");
            ps = tmgr.prepareStatement(getrecord);
            ps.setString(1, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UserInformationDobj();
                dobj.setOfficeName(rs.getString("off_name"));
                dobj.setOpen_timing(rs.getString("open_time"));
                dobj.setClose_timing(rs.getString("close_time"));
                list.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return list;
    }

    public List<UserInformationDobj> getOfficeIp() throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        RowSet rs = null;
        UserInformationDobj dobj = null;
        List<UserInformationDobj> list = new ArrayList<>();
        String getrecord = "  select count(i.off_cd) as total, i.state_cd,o.off_name from " + TableList.TM_USER_IPADDRESS + " i "
                + "  left join " + TableList.TM_OFFICE + " o on i.state_cd = o.state_cd and i.off_cd = o.off_cd  where i.state_cd=? group by 2,3 ";
        try {
            tmgr = new TransactionManagerReadOnly("getOfficeIp");
            ps = tmgr.prepareStatement(getrecord);
            ps.setString(1, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UserInformationDobj();
                dobj.setOfficeName(rs.getString("off_name"));
                dobj.setTotal_ip(rs.getInt("total"));
                list.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return list;
    }

    public List<UserInformationDobj> getBlockUserDetails() throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        RowSet rs = null;
        UserInformationDobj dobj = null;
        List<UserInformationDobj> list = new ArrayList<>();
        String getrecord = "  select i.state_cd,i.off_cd,i.user_cd,i.reason,i.blocked_by,o.off_name,u.user_name as user,v.user_name as blocked_by_name,c.descr from  " + TableList.TM_USER_STATUS + "  i "
                + " inner join " + TableList.TM_OFFICE + " o on i.state_cd = o.state_cd and i.off_cd = o.off_cd  "
                + " inner join " + TableList.TM_USER_INFO + " u on u.user_cd = i.user_cd::numeric "
                + " inner join " + TableList.TM_USER_INFO + " v on v.user_cd = i.blocked_by::numeric "
                + "inner join " + TableList.TM_USER_CATG + " c on c.code = u.user_catg"
                + " where i.state_cd=? order by off_name";
        try {
            tmgr = new TransactionManagerReadOnly("getBlockUserDetails");
            ps = tmgr.prepareStatement(getrecord);
            ps.setString(1, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UserInformationDobj();
                dobj.setOfficeName(rs.getString("off_name"));
                dobj.setUser_name(rs.getString("user"));
                dobj.setBlocked_by(rs.getString("blocked_by_name"));
                dobj.setBloked_user_catg(rs.getString("descr").toUpperCase());
                dobj.setReason(rs.getString("reason").toUpperCase());
                list.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return list;
    }

    public List<UserInformationDobj> getIpDetails() throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        RowSet rs = null;
        UserInformationDobj dobj = null;
        List<UserInformationDobj> list = new ArrayList<>();
        String getrecord = "  select i.state_cd,i.off_cd,ip_address,o.off_name from " + TableList.TM_USER_IPADDRESS + " i "
                + "  left join " + TableList.TM_OFFICE + " o on i.state_cd = o.state_cd and i.off_cd = o.off_cd  where i.state_cd=? ";
        try {
            tmgr = new TransactionManagerReadOnly("getIpDetails");
            ps = tmgr.prepareStatement(getrecord);
            ps.setString(1, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UserInformationDobj();
                dobj.setOfficeName(rs.getString("off_name"));
                dobj.setIp_address(rs.getString("ip_address"));
                list.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return list;
    }

    // For Office Admin 
    public UserInformationDobj getOfficeAdminRecord() throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        RowSet rs = null;
        UserInformationDobj dobj = null;
        List<UserInformationDobj> list = new ArrayList<>();
        String getlistrecord = "Select user_name, i.off_cd, user_id, o.off_name, user_cd from " + TableList.TM_USER_INFO + " i "
                + " left join " + TableList.TM_OFFICE + " o on i.state_cd = o.state_cd and i.off_cd= o.off_cd where user_catg = 'A' and i.state_cd= ?  and i.off_cd= ?";
        try {
            tmgr = new TransactionManagerReadOnly("getOfficeAdminRecord");
            ps = tmgr.prepareStatement(getlistrecord);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserOffCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new UserInformationDobj();
                dobj.setUser_name(rs.getString("user_name"));
                dobj.setOfficeName(rs.getString("off_name"));
                dobj.setUser_id(rs.getString("user_id"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setUser_cd(rs.getInt("user_cd"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return dobj;
    }

    public UserInformationDobj getOfficeTimings() throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        RowSet rs = null;
        UserInformationDobj dobj = null;
        List<UserInformationDobj> list = new ArrayList<>();
        String getrecord = "  select i.state_cd,i.off_cd,open_time,close_time,o.off_name from " + TableList.TM_CONFIGURATION_LOGIN + " i "
                + " left join " + TableList.TM_OFFICE + " o on i.state_cd = o.state_cd and i.off_cd = o.off_cd "
                + " where i.state_cd=? and i.off_cd=? ";
        try {
            tmgr = new TransactionManagerReadOnly("getOfficeTimings");
            ps = tmgr.prepareStatement(getrecord);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserOffCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new UserInformationDobj();
                dobj.setOfficeName(rs.getString("off_name"));
                dobj.setOpen_timing(rs.getString("open_time"));
                dobj.setClose_timing(rs.getString("close_time"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return dobj;
    }

    public List<UserInformationDobj> getOfficeIpDetails() throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        RowSet rs = null;
        UserInformationDobj dobj = null;
        List<UserInformationDobj> list = new ArrayList<>();
        String getrecord = "  select count(i.off_cd) as total, i.state_cd,o.off_name from " + TableList.TM_USER_IPADDRESS + " i "
                + "  left join " + TableList.TM_OFFICE + " o on i.state_cd = o.state_cd and i.off_cd = o.off_cd  where i.state_cd=? and i.off_cd=? group by 2,3  ";
        try {
            tmgr = new TransactionManagerReadOnly("getOfficeIpDetails");
            ps = tmgr.prepareStatement(getrecord);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserOffCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UserInformationDobj();
                dobj.setOfficeName(rs.getString("off_name"));
                dobj.setTotal_ip(rs.getInt("total"));
                list.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return list;
    }

    public List<UserInformationDobj> getOfficeBlockUserDetails() throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        RowSet rs = null;
        UserInformationDobj dobj = null;
        List<UserInformationDobj> list = new ArrayList<>();
        String getrecord = "  select i.state_cd,i.off_cd,i.user_cd,i.reason,i.blocked_by,o.off_name,u.user_name as user,v.user_name as blocked_by_name,c.descr from  " + TableList.TM_USER_STATUS + "  i "
                + " inner join " + TableList.TM_OFFICE + " o on i.state_cd = o.state_cd and i.off_cd = o.off_cd  "
                + " inner join " + TableList.TM_USER_INFO + " u on u.user_cd = i.user_cd::numeric "
                + " inner join " + TableList.TM_USER_INFO + " v on v.user_cd = i.blocked_by::numeric "
                + " inner join " + TableList.TM_USER_CATG + " c on c.code = u.user_catg "
                + " where i.state_cd=? and i.off_cd=? order by off_name";
        try {
            tmgr = new TransactionManagerReadOnly("getOfficeBlockUserDetails");
            ps = tmgr.prepareStatement(getrecord);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserOffCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UserInformationDobj();
                dobj.setOfficeName(rs.getString("off_name"));
                dobj.setUser_name(rs.getString("user"));
                dobj.setBlocked_by(rs.getString("blocked_by_name"));
                dobj.setBloked_user_catg(rs.getString("descr").toUpperCase());
                dobj.setReason(rs.getString("reason").toUpperCase());
                list.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return list;
    }

    public List<UserInformationDobj> getDealerAdminUnderOffAdmin(int off_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        RowSet rs = null;
        UserInformationDobj dobj = null;
        List<UserInformationDobj> list = new ArrayList<>();
        String getlistrecord = " select user_name,i.user_cd,user_id,off_name,i.off_cd,c.descr,vd.dealer_cd, case when (i.status = 'A') "
                + " then 'Online' else 'Offline' end as user_status,COALESCE(vd.dealer_name, ''::character varying) AS dlr_name from tm_user_info i "
                + " left join " + TableList.TM_USER_CATG + " c on i.user_catg = c.code  "
                + " left join " + TableList.TM_OFFICE + " o on i.state_cd= o.state_cd and i.off_cd = o.off_cd  "
                + " left join " + TableList.TM_USER_PERMISSIONS + " p on i.user_cd=p.user_cd and i.state_cd=p.state_cd "
                + " left join " + TableList.VM_DEALER_MAST + " vd ON vd.dealer_cd::text = p.dealer_cd::text "
                + " where user_catg IN('D') and i.state_cd=? and i.off_cd=? order by user_name ";
        try {
            tmgr = new TransactionManagerReadOnly("getDealerAdminUnderOffAdmin");
            ps = tmgr.prepareStatement(getlistrecord);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, off_cd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UserInformationDobj();
                dobj.setDealer_name(rs.getString("dlr_name"));
                dobj.setUser_name(rs.getString("user_name"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setUser_cd(rs.getInt("user_cd"));
                dobj.setOfficeName(rs.getString("off_name"));
                dobj.setUser_id(rs.getString("user_id"));
                dobj.setUserCatg_descr(rs.getString("descr"));
                dobj.setUser_status(rs.getString("user_status"));
                dobj.setDealer_cd(rs.getString("dealer_cd"));
                list.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return list;
    }

    public List<UserInformationDobj> getDealerStaffDetails(String dealer_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        RowSet rs = null;
        UserInformationDobj dobj = null;
        List<UserInformationDobj> list = new ArrayList<>();
        String getlistrecord = "  select user_name,i.off_cd,i.user_id,i.user_name,i.user_cd,i.state_cd,off_name,c.descr,case when (i.status = 'A')  then 'Online' else 'Offline' end as user_status,COALESCE(vd.dealer_name, ''::character varying) "
                + " AS dlr_name from " + TableList.TM_USER_PERMISSIONS + " p "
                + " left join " + TableList.TM_USER_INFO + " i on p.state_cd=i.state_cd and p.user_cd = i.user_cd "
                + " left join " + TableList.TM_OFFICE + " o on i.state_cd= o.state_cd and i.off_cd = o.off_cd "
                + " left join " + TableList.TM_USER_CATG + " c on i.user_catg = c.code "
                + " left join " + TableList.VM_DEALER_MAST + " vd ON vd.dealer_cd::text = p.dealer_cd::text "
                + "  where p.dealer_cd=?  and user_catg='B' and p.state_cd=? ";
        try {
            tmgr = new TransactionManagerReadOnly("getDealerStaffDetails");
            ps = tmgr.prepareStatement(getlistrecord);
            ps.setString(1, dealer_cd);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UserInformationDobj();
                dobj.setDealer_name(rs.getString("dlr_name"));
                dobj.setUser_name(rs.getString("user_name"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setUser_cd(rs.getInt("user_cd"));
                dobj.setOfficeName(rs.getString("off_name"));
                dobj.setUser_id(rs.getString("user_id"));
                dobj.setUserCatg_descr(rs.getString("descr"));
                dobj.setUser_status(rs.getString("user_status"));
                list.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return list;
    }
}
