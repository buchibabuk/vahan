/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.impl;

import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.Dealer;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.user_mgmt.dobj.UserMgmtDobj;
import nic.vahan.db.user_mgmt.dobj.UserAuthorityDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC102
 */
public class UserMgmtImpl {

    private static final Logger LOGGER = Logger.getLogger(UserMgmtImpl.class);

    //for filling datatable
    public static void fillDt(UserMgmtDobj dobj) {
        String state_cd = dobj.getState_cd();
        int off_cd = dobj.getOffice_cd();
        String user_catg = dobj.getUser_catg().trim();
        long emp_cd = dobj.getUser_cd();
        TreeMap<Long, String> dtMap = new TreeMap<>();
        dtMap = dobj.getDtMap();
        String dealerCode = "";
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String sql;
        String[] officeArray = null;
        try {
            tmgr = new TransactionManager("fillDt");
            sql = "select assigned_office,dealer_cd from " + TableList.TM_USER_PERMISSIONS + "  where user_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, emp_cd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                String office = rs.getString("assigned_office");
                dealerCode = rs.getString("dealer_cd");
                officeArray = office.split(",");
            }

            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_PORTAL_ADMIN)) {
                sql = "Select user_cd,user_id,user_name from " + TableList.TM_USER_INFO + " where state_cd=? and user_catg=? order by user_cd";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, state_cd);
                ps.setString(2, TableConstants.USER_CATG_STATE_ADMIN);
                rs = tmgr.fetchDetachedRowSet();
                while (rs.next()) {
                    dtMap.put(rs.getLong("user_cd"), rs.getString("user_name") + "(" + rs.getString("user_id") + ")");
                }
            }

            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)) {
                if (dobj.getOffice_cd() != 0) {
                    sql = "Select b.user_cd,b.user_id,b.user_name from tm_user_info b \n"
                            + "where b.state_cd = ? and b.user_cd IN"
                            + " (Select user_cd from tm_user_permissions a where a.assigned_office!='ANY' and ? = ANY(string_to_array(a.assigned_office,',')::numeric[])  order by user_cd)\n"
                            + "	and b.user_catg IN ('A','F','W','V')";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, state_cd);
                    ps.setInt(2, dobj.getOffice_cd());
                    rs = tmgr.fetchDetachedRowSet();
                } else {
                    sql = "Select user_cd,user_id,user_name from tm_user_info \n"
                            + " where state_cd = ? and user_catg IN ('R')";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, state_cd);
                    rs = tmgr.fetchDetachedRowSet();
                }
                while (rs.next()) {
                    dtMap.put(rs.getLong("user_cd"), rs.getString("user_name") + "(" + rs.getString("user_id") + ")");
                }
            }

            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)) {
                for (int i = 0; i < officeArray.length; i++) {
                    int offc = Integer.parseInt(officeArray[i]);
                    sql = " Select tui.user_cd, tui.user_id, tui.user_name from " + TableList.TM_USER_INFO + " \n"
                            + " tui where tui.user_cd IN (Select user_cd from " + TableList.TM_USER_PERMISSIONS + " \n"
                            + " where state_cd = ? and assigned_office!='ANY' and ? = ANY(string_to_array(assigned_office,',')::numeric[]) order by user_cd) "
                            + " and  tui.user_cd NOT IN( Select user_cd::numeric from " + TableList.TM_USER_STATUS + " where state_cd = ? and off_cd = ?::numeric ) and  tui.user_catg IN ('L','Y','X','D','Z') ";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, state_cd);
                    ps.setInt(2, offc);
                    ps.setString(3, state_cd);
                    ps.setInt(4, off_cd);
                    rs = tmgr.fetchDetachedRowSet();
                    while (rs.next()) {
                        dtMap.put(rs.getLong("user_cd"), rs.getString("user_name") + "(" + rs.getString("user_id") + ")");
                    }
                }
            }
            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN)) {
                for (int i = 0; i < officeArray.length; i++) {
                    int offc = Integer.parseInt(officeArray[i]);
                    sql = "Select user_cd, user_id, user_name from " + TableList.TM_USER_INFO + " \n"
                            + " where user_cd IN (Select user_cd from " + TableList.TM_USER_PERMISSIONS + " \n"
                            + " where state_cd= ? and assigned_office!='ANY' and ? = ANY(string_to_array(assigned_office,',')::numeric[])"
                            + " and dealer_cd = ? order by user_cd)\n"
                            + " and user_catg = ? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, state_cd);
                    ps.setInt(2, offc);
                    ps.setString(3, dealerCode);
                    ps.setString(4, TableConstants.USER_CATG_DEALER);
                    rs = tmgr.fetchDetachedRowSet();
                    while (rs.next()) {
                        dtMap.put(rs.getLong("user_cd"), rs.getString("user_name") + "(" + rs.getString("user_id") + ")");
                    }
                }
            }
            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_ADMIN)) {
                for (int i = 0; i < officeArray.length; i++) {
                    int offc = Integer.parseInt(officeArray[i]);
                    sql = "Select user_cd, user_id, user_name from " + TableList.TM_USER_INFO + " \n"
                            + "where user_cd IN (Select user_cd from " + TableList.TM_USER_PERMISSIONS + " "
                            + "where state_cd = ? and assigned_office!='ANY' and ? = ANY(string_to_array(assigned_office,',')::numeric[]) order by user_cd)\n"
                            + "	and user_catg IN ('E')";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, state_cd);
                    ps.setInt(2, offc);
                    rs = tmgr.fetchDetachedRowSet();
                    while (rs.next()) {
                        dtMap.put(rs.getLong("user_cd"), rs.getString("user_name") + "(" + rs.getString("user_id") + ")");
                    }
                }
            }
        } catch (SQLException e) {
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

    public static void getAssignedOffice(UserMgmtDobj dobj, TransactionManager tmgr, TreeMap<Integer, String> dtMap) {
        long emp_cd = dobj.getUser_cd();
        tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String sql;

        try {
            tmgr = new TransactionManager("get office");
            sql = "SELECT user_cd, assigned_office FROM " + TableList.TM_USER_PERMISSIONS + "  where user_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, emp_cd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                String office = rs.getString("assigned_office");
                String[] stringArray = office.split(",");
                for (int i = 0; i < stringArray.length; i++) {
                    String offcd = stringArray[i];
                    String sql1 = "Select a.user_cd,a.user_name,b.assigned_office \n"
                            + "from " + TableList.TM_USER_INFO + " a\n"
                            + "inner join " + TableList.TM_USER_PERMISSIONS + " b on a.user_cd = b.user_cd  where b.state_cd = ? and b.assigned_office = ? ";
                    ps = null;
                    rs = null;
                    ps = tmgr.prepareStatement(sql1);
                    ps.setString(1, dobj.getState_cd());
                    ps.setString(2, offcd);
                    rs = tmgr.fetchDetachedRowSet();
                    while (rs.next()) {
                        dtMap.put(rs.getInt("user_cd"), rs.getString("user_name"));
                    }
                }
            }

        } catch (SQLException e) {
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

    //for checking employee record existing or new
    public static String checkSaveOrUpdateEmpRecord(UserMgmtDobj dobj, UserAuthorityDobj userDobj) throws VahanException {
        String str = "";
        long emp_cd = 0;
        int team_id = userDobj.getTeam_id();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        Boolean flag = false;
        String sql;
        long userCode = 0l;
        try {
            String state_cd = dobj.getState_cd();
            if (dobj.getUser_cd() != 0) {
                emp_cd = dobj.getUser_cd();
            }

            tmgr = new TransactionManager("checkSaveOrUpdateEmpRecord");
            sql = "Select user_cd from " + TableList.TM_USER_INFO + " where state_cd=? and user_cd=? order by user_cd";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setLong(2, emp_cd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                flag = true;
                userCode = rs.getLong("user_cd");
            }

            if (flag) {
                str = UpdateEmpDate(dobj, tmgr);
                if (userDobj != null) {
                    UpdatePermissionData(dobj, userDobj, tmgr);
                    if (userDobj.isIsEnforcementOfficer()) {
                        insertIntoVmEnforcementOfficer(tmgr, userCode, team_id);
                    } else {
                        deleteFromVmEnforcementOfficer(tmgr, team_id);
                    }
                    if (userDobj.isIsFitOfficer()) {
                        insertIntoVmFitOfficer(tmgr, userCode);
                    } else {
                        deleteFromVmFitOfficer(tmgr, userCode);
                    }
                    if (userDobj.isIsSiqnUpload()) {
                        updateUploadedSignature(tmgr, userDobj, userCode);
                    }
                }
            } else {
                if (!checkUniqueUserID(dobj.getUser_id().toLowerCase())) {
                    str = saveEmpData(dobj, userDobj, tmgr);
                    if (userDobj != null) {
                        userCode = dobj.getUser_cd();
                        SavePermissionData(dobj, userDobj, tmgr);
                        if (userDobj.isIsFitOfficer()) {
                            insertIntoVmFitOfficer(tmgr, userCode);
                        } else {
                            deleteFromVmFitOfficer(tmgr, userCode);
                        }
                        if (userDobj.isIsEnforcementOfficer()) {
                            insertIntoVmEnforcementOfficer(tmgr, userCode, team_id);
                        } else {
                            deleteFromVmEnforcementOfficer(tmgr, team_id);
                        }
                        if (userDobj.isIsSiqnUpload()) {
                            uploadSignature(tmgr, userDobj, userCode);
                        }
                        if (null != dobj && TableConstants.CASHIER.equals(dobj.getDesig_cd()) && "OR".equals(state_cd)) {
                            insertCashierData(tmgr, state_cd, dobj.getOffice_cd(), userCode);
                        }
                    }
                } else {
                    str = "User ID Already Exist.";
                    throw new VahanException("User ID Already Exist.");
                }
            }
            tmgr.commit();
        } catch (VahanException vex) {
            throw new VahanException(vex.getMessage());
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in checkSaveOrUpdateEmpRecord");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in checkSaveOrUpdateEmpRecord");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException("Error in checkSaveOrUpdateEmpRecord" + e.getMessage());
            }
        }
        return str;
    }

    //For updating employee record
    private static String UpdateEmpDate(UserMgmtDobj dobj, TransactionManager tmgr) throws VahanException {
        if (!CommonUtils.isNullOrBlank(dobj.getState_cd()) && !CommonUtils.isNullOrBlank(dobj.getUser_name())
                && !CommonUtils.isNullOrBlank(dobj.getUser_id()) && !CommonUtils.isNullOrBlank(dobj.getDesig_cd())) {
            long userCode = 0l;
            userCode = dobj.getUser_cd();
            insertHistory(userCode, tmgr);
            try {
                Date da = new Date();
                java.sql.Date d = new java.sql.Date(da.getTime());
                PreparedStatement ps = null;
                String sql = "UPDATE " + TableList.TM_USER_INFO + " SET state_cd=?, off_cd=?, user_cd=?, user_name=?, desig_cd=?, phone_off=?, mobile_no=?, email_id=?, user_catg=?, status=?, created_by=?, created_dt=?, aadhaar = ?, op_dt = current_timestamp WHERE state_cd=? and user_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getState_cd());
                ps.setInt(2, dobj.getOffice_cd());
                ps.setLong(3, dobj.getUser_cd());
                ps.setString(4, dobj.getUser_name());
                ps.setString(5, dobj.getDesig_cd());
                ps.setString(6, dobj.getOff_phone());
                ps.setLong(7, dobj.getMobile_no());
                ps.setString(8, dobj.getEmail());
                ps.setString(9, dobj.getSelectedUserCatg());
                ps.setString(10, dobj.getStatus());
                ps.setLong(11, dobj.getCreated_by());
                ps.setDate(12, d);
                ps.setLong(13, dobj.getAadharNo());
                ps.setString(14, dobj.getState_cd());
                ps.setLong(15, dobj.getUser_cd());
                ps.setInt(16, dobj.getOffice_cd());

                ps.executeUpdate();
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException("Error in UpdateEmpDate");
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException("Error in UpdateEmpDate");
            }
            return "Employee updated Successfully ";
        } else {
            throw new VahanException("Fill all required fields!!!");
        }
    }

    //For inserting updated user history 
    public static String insertHistory(Long user_cd, TransactionManager tmgr) throws VahanException {
        try {
            Date da = new Date();
            PreparedStatement ps = null;
            String sql1 = "INSERT INTO " + TableList.THM_USER_INFO + "  \n"
                    + "SELECT state_cd, off_cd, user_cd, user_name, desig_cd, user_id,user_pwd, phone_off, mobile_no, email_id, user_catg, status, created_by, created_dt,aadhaar,current_timestamp as moved_on,? as moved_by,op_dt \n"
                    + "  FROM " + TableList.TM_USER_INFO + " where user_cd=?";

            ps = tmgr.prepareStatement(sql1);
            ps.setString(1, Util.getEmpCode());
            ps.setLong(2, user_cd);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new VahanException("Error in insertHisotry" + e.getMessage());
        }
        return "Records inserted ";
    }

    //For saving employee record
    public static String saveEmpData(UserMgmtDobj dobj, UserAuthorityDobj userDobj, TransactionManager tmgr) throws VahanException {
        String str = "";
        boolean uniqueDealerFlag = false;

        if (!CommonUtils.isNullOrBlank(dobj.getState_cd()) && !CommonUtils.isNullOrBlank(dobj.getUser_name())
                && !CommonUtils.isNullOrBlank(dobj.getUser_id()) && !CommonUtils.isNullOrBlank(dobj.getDesig_cd())) {
            String state_cd = dobj.getState_cd();

            PreparedStatement ps = null;
            String sql = null;
            String offCode;
            long userCode = 0l;
            try {
                if (dobj.getSelectedUserCatg().equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)) {
                    sql = "Select user_cd from " + TableList.TM_USER_INFO + " where state_cd=? and user_catg=? order by user_cd";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, state_cd);
                    ps.setString(2, TableConstants.USER_CATG_STATE_ADMIN);
                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        if (dobj.getSelectedUserCatg().equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)) {
                            str = "State Admin Already Exist.";
                            throw new VahanException(str);
                        }
                    }

                } else if (dobj.getSelectedUserCatg().equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)) {
                    for (Object temp : ServerUtil.makeList(dobj.getAssignedOffice())) {
                        offCode = temp.toString();
                        sql = "select a.user_cd, a.user_catg, b.assigned_office \n"
                                + "from " + TableList.TM_USER_INFO + " a \n"
                                + "inner join " + TableList.TM_USER_PERMISSIONS + " b on a.user_cd = b.user_cd\n"
                                + "where a.state_cd = ? and a.user_catg = ? and ? =  ANY (string_to_array(b.assigned_office,','))";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, state_cd);
                        ps.setString(2, TableConstants.USER_CATG_OFFICE_ADMIN);
                        ps.setString(3, offCode);
                        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            if (dobj.getSelectedUserCatg().equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)) {
                                str = "Office Admin Already Exist.";
                                throw new VahanException(str);
                            }
                        }
                    }
                } else if (dobj.getSelectedUserCatg().equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN)) {
                    uniqueDealerFlag = verifyUniqueDealerAdmin(userDobj.getDealerCode());
                    if (uniqueDealerFlag) {
                        str = "Dealer Admin Exist For Selected Dealer.";
                        throw new VahanException(str);
                    }
                }

                if (!checkUniqueUserID(dobj.getUser_id().toLowerCase()) && !checkStateOrOffAdminUnique(dobj.getState_cd(), dobj.getOfficeList(), dobj.getSelectedUserCatg()) && !uniqueDealerFlag) {
                    Date da = new Date();
                    java.sql.Date d = new java.sql.Date(da.getTime());
                    userCode = ServerUtil.getUniqueUserCd(tmgr, dobj.getState_cd());
                    dobj.setUser_cd(userCode);
                    sql = "INSERT INTO " + TableList.TM_USER_INFO + "(state_cd, off_cd, user_cd, user_name, desig_cd, user_id, user_pwd,phone_off, mobile_no, email_id, user_catg, status, created_by,created_dt, aadhaar,op_dt,newuser_change_password)VALUES (?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, current_timestamp, ?)";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, dobj.getState_cd());
                    ps.setInt(2, dobj.getOffice_cd());
                    ps.setLong(3, dobj.getUser_cd());
                    ps.setString(4, dobj.getUser_name());
                    ps.setString(5, dobj.getDesig_cd());
                    ps.setString(6, dobj.getUser_id().toLowerCase());
                    ps.setString(7, dobj.getUser_pwd());
                    ps.setString(8, dobj.getOff_phone());
                    ps.setLong(9, dobj.getMobile_no());
                    ps.setString(10, dobj.getEmail());
                    ps.setString(11, dobj.getSelectedUserCatg());
                    ps.setString(12, dobj.getStatus());
                    ps.setLong(13, dobj.getCreated_by());
                    ps.setDate(14, d);
                    ps.setLong(15, dobj.getAadharNo());
                    ps.setString(16, dobj.getNewuser_change_password());

                    ps.executeUpdate();
                    str = "Employee registered successfully ";
                } else {
                    str = "Employee could not be registered !!";
                    throw new VahanException(str);
                }

            } catch (VahanException e) {
                throw e;
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException("Error in saveEmpData");
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException("Error in saveEmpData");
            }
        } else {
            str = "Fill all required fields!!!";
            throw new VahanException(str);
        }
        return str;
    }

    //For Deleting Employee Records
    public static String deleteEmp(UserMgmtDobj dobj) {
        String state_code = dobj.getState_cd();
        long user_code = dobj.getUser_cd();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String str = "";

        String sql = "Delete from " + TableList.TM_USER_INFO + " where state_cd=? and user_cd=?";
        try {
            tmgr = new TransactionManager("deleteEmp");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_code);
            ps.setLong(2, user_code);
            ps.executeUpdate();

            sql = "Delete from " + TableList.TM_USER_PERMISSIONS + " where user_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, user_code);
            ps.executeUpdate();

            tmgr.commit();
            str = "Employee Deleted Successfully!!!";
        } catch (SQLException e) {
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
        return str;
    }

    public static UserMgmtDobj getEmpDetails(UserMgmtDobj dobj) {
        UserMgmtDobj robj = null;
        long emp_cd = dobj.getUser_cd();
        String state_cd = dobj.getState_cd();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;

        try {
            tmgr = new TransactionManager("getEmpDetails");
            String sql = "Select * from " + TableList.TM_USER_INFO + " where state_cd=? and user_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setLong(2, emp_cd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                robj = new UserMgmtDobj();
                robj.setUser_name(rs.getString("user_name"));//user_name
                robj.setDesig_cd(rs.getString("desig_cd"));//desig_cd
                robj.setUser_id(rs.getString("user_id"));//user_id
                robj.setUser_pwd(rs.getString("user_pwd"));//user_pwd
                robj.setOff_phone(rs.getString("phone_off"));//phone_off
                robj.setMobile_no(rs.getLong("mobile_no"));//mobile_no
                robj.setEmail(rs.getString("email_id"));//email_id
                robj.setSelectedUserCatg(rs.getString("user_catg"));//user_catg
                robj.setStatus(rs.getString("status"));//status
                if (rs.getLong("aadhaar") == 0) {
                    robj.setAadharNo(null);//aadhaar
                } else {
                    robj.setAadharNo(rs.getLong("aadhaar"));//aadhaar
                }

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
        return robj;
    }

    public static UserAuthorityDobj getUserPermissionDetails(long user_cd, UserAuthorityDobj userDobj) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("getUserPermissionDetails");
            String sql = "Select * from " + TableList.TM_USER_PERMISSIONS + " where user_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, user_cd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                userDobj = new UserAuthorityDobj();
                userDobj.setIsFitOfficer(false);
                userDobj.setLowerBound(rs.getInt("lower_range_no"));
                userDobj.setUpperBound(rs.getInt("upper_range_no"));
                userDobj.setVehType(rs.getInt("class_type"));
                if (!rs.getString("vch_class").equalsIgnoreCase("ANY")) {
                    for (String str : rs.getString("vch_class").split(",")) {
                        userDobj.getSelectedVehClass().add(str);
                    }
                }
                if (!rs.getString("pmt_type").equalsIgnoreCase("ANY")) {
                    for (int tmp : converterForCommasInt(rs.getString("pmt_type"))) {
                        userDobj.getPermitType().add(tmp);
                    }
                }
                if (!rs.getString("pmt_catg").equalsIgnoreCase("ANY")) {
                    for (String str : rs.getString("pmt_catg").split(",")) {
                        userDobj.getSelectedPermitCatg().add(str);
                    }
                }
                userDobj.setDealerCode(rs.getString("dealer_cd"));
                if (!rs.getString("maker").equalsIgnoreCase("ANY")) {
                    for (String str : rs.getString("maker").split(",")) {
                        userDobj.getSelectedMakerType().add(str);
                    }
                }

                if (!rs.getString("assigned_office").equalsIgnoreCase("ANY")) {
                    userDobj.setAssignedOffice(rs.getString("assigned_office"));
                }
                userDobj.setAllOfficeAuth(rs.getBoolean("all_office_auth"));


                sql = "Select user_cd from " + TableList.VM_FIT_OFFICER + " where user_cd = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setLong(1, user_cd);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    userDobj.setIsFitOfficer(true);
                }

                sql = "Select team_id from " + TableList.VM_ENFORCEMENT_OFFICER + " where user_cd = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setLong(1, user_cd);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    userDobj.setIsEnforcementOfficer(true);
                    userDobj.setTeam_id(rs.getInt("team_id"));
                }

                sql = "Select * from " + TableList.TM_USER_SIGN + " where user_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setLong(1, user_cd);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    userDobj.setSignatureFile(rs.getBytes("doc_sign"));
                }

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

        return userDobj;
    }

    //    Function for generating new employee code.
//    private static Integer generateEmpCode(String state_cd, int office_cd, TransactionManager tmgr) throws VahanException {
//        Integer employee_code = null;
//        String temp = "";
//        int egov_code = 0;
//        int count = 0;
//        if (state_cd != null && office_cd != 0) {
//            egov_code = getE_GovCode(state_cd, tmgr);
//            count = getEmpCount(tmgr);
//            temp = egov_code + "" + office_cd + "" + (count + 1) + "";
//            temp = temp.trim();
//            employee_code = Integer.parseInt(temp);
//            return employee_code;
//        } else {
//            return employee_code;
//        }
//    }
//
//    private static int getE_GovCode(String state_cd, TransactionManager tmgr) throws VahanException {
//        int eGovCode = 0;
//        try {
//            PreparedStatement ps = null;
//            RowSet rs = null;
//            String sql = "select * from " + TableList.TM_STATE + " where state_code=?";
//            ps = tmgr.prepareStatement(sql);
//            ps.setString(1, state_cd);
//            rs = tmgr.fetchDetachedRowSet();
//            while (rs.next()) {
//                eGovCode = rs.getInt(3);
//            }
//        } catch (SQLException e) {
//            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//            throw new VahanException("Error in getE_GovCode" + e.getMessage());
//        }
//        return eGovCode;
//    }
//
//    private static int getEmpCount(TransactionManager tmgr) throws VahanException {
//        int count = 0;
//        PreparedStatement ps = null;
//        RowSet rs = null;
//        try {
//            String sql = "select user_cd from " + TableList.TM_USER_INFO + "";
//            ps = tmgr.prepareStatement(sql);
//            rs = tmgr.fetchDetachedRowSet();
//            while (rs.next()) {
//                count++;
//            }
//        } catch (SQLException e) {
//            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//            throw new VahanException("Error in getEmpCount " + e.getMessage());
//        }
//        return count;
//    }
    //Inserting permission data of user
    private static void SavePermissionData(UserMgmtDobj dobj, UserAuthorityDobj userDobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps;
        String selectedVehClass = "";
        StringBuilder selectedVehClass_1 = new StringBuilder();
        String selectedPermitType = "";
        StringBuilder selectedPermitType_1 = new StringBuilder();
        String selectedPermitCatg = "";
        StringBuilder selectedPermitCatg_1 = new StringBuilder();
        String selectedMaker = "";
        StringBuilder selectedMaker_1 = new StringBuilder();
        String assignedOffice = userDobj.getAssignedOffice();

        if (userDobj.getVehType() == 0) {
            userDobj.setVehType(3);
        }

        if (dobj.getSelectedUserCatg().equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN)) {
            if (CommonUtils.isNullOrBlank(userDobj.getDealerCode())) {
                throw new VahanException("Invalid Dealer Entry");
            }
        } else {
            userDobj.setDealerCode("ANY");
        }

        for (int j = 0; j < userDobj.getSelectedVehClass().size(); j++) {
            selectedVehClass_1.append(userDobj.getSelectedVehClass().get(j)).append(",");
        }
        for (int j = 0; j < userDobj.getPermitType().size(); j++) {
            selectedPermitType_1.append(userDobj.getPermitType().get(j)).append(",");
        }
        for (int j = 0; j < userDobj.getSelectedPermitCatg().size(); j++) {
            selectedPermitCatg_1.append(userDobj.getSelectedPermitCatg().get(j)).append(",");
        }
        for (int j = 0; j < userDobj.getSelectedMakerType().size(); j++) {
            selectedMaker_1.append(userDobj.getSelectedMakerType().get(j)).append(",");
        }

        if (selectedVehClass_1.toString().endsWith(",")) {
            selectedVehClass = selectedVehClass_1.substring(0, selectedVehClass_1.length() - 1);
        }
        if (selectedPermitType_1.toString().endsWith(",")) {
            selectedPermitType = selectedPermitType_1.substring(0, selectedPermitType_1.length() - 1);
        }
        if (selectedPermitCatg_1.toString().endsWith(",")) {
            selectedPermitCatg = selectedPermitCatg_1.substring(0, selectedPermitCatg_1.length() - 1);
        }
        if (selectedMaker_1.toString().endsWith(",")) {
            selectedMaker = selectedMaker_1.substring(0, selectedMaker_1.length() - 1);
        }
        if (assignedOffice.endsWith(",")) {
            assignedOffice = assignedOffice.substring(0, assignedOffice.length() - 1);
        }

        if (selectedVehClass.isEmpty()) {
            selectedVehClass = "ANY";
        }
        if (selectedPermitType.isEmpty() || userDobj.getPermitType().size() == userDobj.getPermitTypeCount()) {
            selectedPermitType = "ANY";
        }
        if (selectedPermitCatg.isEmpty() || userDobj.getSelectedPermitCatg().size() == userDobj.getPermitTypeCatgCount()) {
            selectedPermitCatg = "ANY";
        }
        if (selectedMaker.isEmpty()) {
            selectedMaker = "ANY";
        }
        if (CommonUtils.isNullOrBlank(assignedOffice)) {
            assignedOffice = "ANY";
        }


        int i = 1;
        String sql = "";
        try {
            if (dobj.getSelectedUserCatg().equalsIgnoreCase(TableConstants.USER_CATG_DEALER)) {
                sql = "INSERT INTO " + TableList.TM_USER_PERMISSIONS + "(\n"
                        + "            state_cd,user_cd, lower_range_no, upper_range_no, class_type, vch_class, \n"
                        + "            pmt_type, pmt_catg, dealer_cd, maker, assigned_office,all_Office_Auth,op_dt)\n"
                        + " Select state_cd,?,lower_range_no, upper_range_no, class_type, vch_class,pmt_type, pmt_catg, dealer_cd, maker, ?,all_Office_Auth, current_timestamp "
                        + "from " + TableList.TM_USER_PERMISSIONS + " where user_cd = ?";

                ps = tmgr.prepareStatement(sql);
                ps.setLong(i++, dobj.getUser_cd());
                ps.setString(i++, assignedOffice);
                ps.setLong(i++, dobj.getCreated_by());
            } else {
                sql = "INSERT INTO " + TableList.TM_USER_PERMISSIONS + "(\n"
                        + "            state_cd,user_cd, lower_range_no, upper_range_no, class_type, vch_class, \n"
                        + "            pmt_type, pmt_catg, dealer_cd, maker, assigned_office,all_Office_Auth,op_dt)\n"
                        + "    VALUES (?,?, ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, ?, ?, current_timestamp)";

                ps = tmgr.prepareStatement(sql);
                ps.setString(i++, dobj.getState_cd());
                ps.setLong(i++, dobj.getUser_cd());
                ps.setInt(i++, userDobj.getLowerBound());
                ps.setInt(i++, userDobj.getUpperBound());
                ps.setInt(i++, userDobj.getVehType());
                ps.setString(i++, selectedVehClass);
                ps.setString(i++, selectedPermitType);
                ps.setString(i++, selectedPermitCatg);
                ps.setString(i++, userDobj.getDealerCode());
                ps.setString(i++, selectedMaker);
                ps.setString(i++, assignedOffice);
                ps.setBoolean(i++, userDobj.isAllOfficeAuth());

            }
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in SavePermissionData " + e.getMessage());
        }
    }

    public static String insertPermissionHistory(UserMgmtDobj dobj, UserAuthorityDobj userDobj, TransactionManager tmgr) throws VahanException {

        try {
            Date da = new Date();
            PreparedStatement ps = null;
            String sql1 = "";
            if (dobj.getSelectedUserCatg().equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN)) {
                sql1 = "INSERT INTO " + TableList.THM_USER_PERMISSIONS + "  \n"
                        + "Select current_timestamp as moved_on,? as moved_by,state_cd,user_cd, lower_range_no, upper_range_no, class_type, vch_class, \n"
                        + " pmt_type, pmt_catg, dealer_cd, maker, assigned_office ,all_Office_Auth,op_dt \n"
                        + "  FROM " + TableList.TM_USER_PERMISSIONS + " where dealer_cd=?";
                ps = tmgr.prepareStatement(sql1);
                ps.setString(1, Util.getEmpCode());
                if (!userDobj.getDealerCode().equalsIgnoreCase("ANY")) {
                    ps.setString(2, userDobj.getDealerCode());
                }
            } else {
                sql1 = "INSERT INTO " + TableList.THM_USER_PERMISSIONS + "  \n"
                        + "Select current_timestamp as moved_on,? as moved_by,state_cd,user_cd, lower_range_no, upper_range_no, class_type, vch_class, \n"
                        + " pmt_type, pmt_catg, dealer_cd, maker, assigned_office ,all_Office_Auth,op_dt \n"
                        + "  FROM " + TableList.TM_USER_PERMISSIONS + " where user_cd=?";

                ps = tmgr.prepareStatement(sql1);
                ps.setString(1, Util.getEmpCode());
                ps.setLong(2, dobj.getUser_cd());
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in insertPermissionHistory" + e.getMessage());
        }
        return "records inserted ";
    }

    //Updating permission data of user
    private static void UpdatePermissionData(UserMgmtDobj dobj, UserAuthorityDobj userDobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps;
        String selectedVehClass = "";

        String selectedPermitType = "";
        String selectedPermitCatg = "";
        String selectedMaker = "";
        String assignedOffice = userDobj.getAssignedOffice();

        if (CommonUtils.isNullOrBlank(userDobj.getDealerCode())
                && !dobj.getSelectedUserCatg().equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN)
                && !dobj.getSelectedUserCatg().equalsIgnoreCase(TableConstants.USER_CATG_DEALER)) {
            userDobj.setDealerCode("ANY");
        }
        for (int j = 0; j < userDobj.getSelectedVehClass().size(); j++) {
            selectedVehClass += userDobj.getSelectedVehClass().get(j) + ",";
        }
        for (int j = 0; j < userDobj.getPermitType().size(); j++) {
            selectedPermitType += userDobj.getPermitType().get(j) + ",";
        }
        for (int j = 0; j < userDobj.getSelectedPermitCatg().size(); j++) {
            selectedPermitCatg += userDobj.getSelectedPermitCatg().get(j) + ",";
        }
        for (int j = 0; j < userDobj.getSelectedMakerType().size(); j++) {
            selectedMaker += userDobj.getSelectedMakerType().get(j) + ",";
        }

        if (CommonUtils.isNullOrBlank(assignedOffice)) {
            userDobj.setDealerCode("ANY");
        }
        if (selectedVehClass.isEmpty()) {
            selectedVehClass = "ANY";
        }

        if (selectedPermitType.isEmpty() || userDobj.getPermitType().size() == userDobj.getPermitTypeCount()) {
            selectedPermitType = "ANY";
        }

        if (selectedPermitCatg.isEmpty() || userDobj.getSelectedPermitCatg().size() == userDobj.getPermitTypeCatgCount()) {
            selectedPermitCatg = "ANY";
        }

        if (selectedMaker.isEmpty()) {
            selectedMaker = "ANY";
        }

        if (selectedVehClass.endsWith(",")) {
            selectedVehClass = selectedVehClass.substring(0, selectedVehClass.length() - 1);
        }
        if (selectedPermitType.endsWith(",")) {
            selectedPermitType = selectedPermitType.substring(0, selectedPermitType.length() - 1);
        }
        if (selectedPermitCatg.endsWith(",")) {
            selectedPermitCatg = selectedPermitCatg.substring(0, selectedPermitCatg.length() - 1);
        }
        if (selectedMaker.endsWith(",")) {
            selectedMaker = selectedMaker.substring(0, selectedMaker.length() - 1);
        }
        if (assignedOffice.endsWith(",")) {
            assignedOffice = assignedOffice.substring(0, assignedOffice.length() - 1);
        }

        insertPermissionHistory(dobj, userDobj, tmgr);
        int i = 1;
        String sql = "";
        try {
            if (dobj.getSelectedUserCatg().equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN)) {
                sql = "UPDATE " + TableList.TM_USER_PERMISSIONS + "\n"
                        + "  SET lower_range_no = ?, upper_range_no = ?, class_type = ?, \n"
                        + "       vch_class = ?, pmt_type = ?, pmt_catg = ?, maker = ?,all_Office_Auth=?, op_dt = current_timestamp \n"
                        + " WHERE dealer_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setInt(i++, userDobj.getLowerBound());
                ps.setInt(i++, userDobj.getUpperBound());
                ps.setInt(i++, userDobj.getVehType());
                ps.setString(i++, selectedVehClass);
                ps.setString(i++, selectedPermitType);
                ps.setString(i++, selectedPermitCatg);
                ps.setString(i++, selectedMaker);
                ps.setBoolean(i++, userDobj.isAllOfficeAuth());
                if (!userDobj.getDealerCode().equalsIgnoreCase("ANY")) {
                    ps.setString(i++, userDobj.getDealerCode());
                }
            } else {
                sql = "UPDATE " + TableList.TM_USER_PERMISSIONS + "\n"
                        + "  SET lower_range_no = ?, upper_range_no = ?, class_type = ?, \n"
                        + "       vch_class = ?, pmt_type = ?, pmt_catg = ?, maker = ?, all_Office_Auth=?, dealer_cd = ?, op_dt = current_timestamp \n"
                        + " WHERE user_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setInt(i++, userDobj.getLowerBound());
                ps.setInt(i++, userDobj.getUpperBound());
                ps.setInt(i++, userDobj.getVehType());
                ps.setString(i++, selectedVehClass);
                ps.setString(i++, selectedPermitType);
                ps.setString(i++, selectedPermitCatg);
                ps.setString(i++, selectedMaker);
                ps.setBoolean(i++, userDobj.isAllOfficeAuth());
                ps.setString(i++, userDobj.getDealerCode());
                ps.setLong(i++, dobj.getUser_cd());
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in UpdatePermissionData " + e.getMessage());
        }
    }

    private static int[] converterForCommasInt(String string) {
        int[] temp;
        String[] temp1 = string.split(",");
        temp = new int[temp1.length];
        for (int i = 0; i < temp1.length; i++) {
            temp[i] = Integer.parseInt(temp1[i]);
        }
        return temp;
    }

    public static boolean checkUniqueUserID(String userId) {
        boolean uniqueUserIdFlag = false;
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs;
        String sql = "Select user_id from " + TableList.TM_USER_INFO + " where user_id=?";
        try {
            tmgr = new TransactionManager("converterForCommasInt");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, userId);//user_id
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                uniqueUserIdFlag = true;
            }
        } catch (SQLException e) {
            JSFUtils.showMessage(e.getMessage());
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return uniqueUserIdFlag;
    }

//    public static boolean checkStateOrOffAdminUnique(String state_cd, int off_cd, String user_catg) {
//        boolean flag = false;
//        PreparedStatement ps;
//        TransactionManager tmgr = null;
//        RowSet rs;
//        String sql = "";
//        try {
//            tmgr = new TransactionManager("checkStateOrOffAdminUnique");
//            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_PORTAL_ADMIN)) {
//                sql = "Select user_cd from " + TableList.TM_USER_INFO + " where state_cd=? and user_catg=? order by user_cd;";
//                ps = tmgr.prepareStatement(sql);
//                ps.setString(1, state_cd);
//               ps.setString(2, TableConstants.USER_CATG_STATE_ADMIN);
//
//                rs = tmgr.fetchDetachedRowSet();
//                if (rs.next()) {
//                    flag = true;
//                }
//            } else if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)) {
//                for (Object temp : off_cd) {
//                    offCode = temp.toString();
//                    sql = "select a.user_cd, a.user_catg, b.assigned_office \n"
//                            + "from " + TableList.TM_USER_INFO + " a \n"
//                            + "inner join " + TableList.TM_USER_PERMISSIONS + " b on a.user_cd = b.user_cd\n"
//                            + "where a.state_cd = ? and a.user_catg = ? and ? =  ANY (string_to_array(b.assigned_office,',')); ";
//                    ps = tmgr.prepareStatement(sql);
//                    ps.setString(1, state_cd);
//                    ps.setString(2, user_catg);
//                    ps.setString(3, offCode);
//                    rs = tmgr.fetchDetachedRowSet();
//                    if (rs.next()) {
//                        flag = true;
//                    }
//                }
//            }
//
//        } catch (SQLException e) {
//            JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.FATAL);
//            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//        } finally {
//            if (tmgr != null) {
//                try {
//                    tmgr.release();
//                } catch (Exception e) {
//                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//                }
//            }
//        }
//        return flag;
//    }
    public static boolean checkStateOrOffAdminUnique(String state_cd, List off_cd, String user_catg) {
        boolean checkStateOrOfficeAdmin = false;
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs;
        String offCode;
        String sql = "";
        try {
            tmgr = new TransactionManager("checkStateOrOffAdminUnique");
            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)) {
                sql = "Select user_cd from " + TableList.TM_USER_INFO + " where state_cd=? and user_catg=? order by user_cd";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, state_cd);
                ps.setString(2, user_catg);

                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    checkStateOrOfficeAdmin = true;
                }
            } else if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)) {
                for (Object temp : off_cd) {
                    offCode = temp.toString();
                    sql = "select a.user_cd, a.user_catg, b.assigned_office \n"
                            + "from " + TableList.TM_USER_INFO + " a \n"
                            + "inner join " + TableList.TM_USER_PERMISSIONS + " b on a.user_cd = b.user_cd\n"
                            + "where a.state_cd = ? and a.user_catg = ? and ? =  ANY (string_to_array(b.assigned_office,',')); ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, state_cd);
                    ps.setString(2, user_catg);
                    ps.setString(3, offCode);
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        checkStateOrOfficeAdmin = true;
                    }
                }
            } else if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_ADMIN)) {
                for (Object temp : off_cd) {
                    offCode = temp.toString();
                    sql = "select a.user_cd, a.user_catg, b.assigned_office \n"
                            + "from " + TableList.TM_USER_INFO + " a \n"
                            + "inner join " + TableList.TM_USER_PERMISSIONS + " b on a.user_cd = b.user_cd\n"
                            + "where a.state_cd = ? and a.user_catg = ? and ? =  ANY (string_to_array(b.assigned_office,',')); ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, state_cd);
                    ps.setString(2, user_catg);
                    ps.setString(3, offCode);
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        checkStateOrOfficeAdmin = true;
                    }
                }
            } else if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_HSRP)) {
                for (Object temp : off_cd) {
                    offCode = temp.toString();
                    sql = " select a.user_cd, a.user_catg, b.assigned_office "
                            + " from " + TableList.TM_USER_INFO + " a "
                            + " inner join " + TableList.TM_USER_PERMISSIONS + " b on a.user_cd = b.user_cd "
                            + " where a.state_cd = ? and a.user_catg = ? and b.assigned_office ~ 'ANY'  ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, state_cd);
                    ps.setString(2, user_catg);
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        checkStateOrOfficeAdmin = true;
                    }
                }
            } else if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_SMARTCARD)) {
                for (Object temp : off_cd) {
                    offCode = temp.toString();
                    sql = " select a.user_cd, a.user_catg, b.assigned_office "
                            + " from " + TableList.TM_USER_INFO + " a "
                            + " inner join " + TableList.TM_USER_PERMISSIONS + " b on a.user_cd = b.user_cd "
                            + " where a.state_cd = ? and a.user_catg = ? and b.assigned_office ~ 'ANY'  ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, state_cd);
                    ps.setString(2, user_catg);
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        checkStateOrOfficeAdmin = true;
                    }
                }
            }

        } catch (SQLException e) {
            JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.FATAL);
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return checkStateOrOfficeAdmin;
    }

    private static void insertIntoVmFitOfficer(TransactionManager tmgr, long userCode) throws VahanException {
        PreparedStatement ps;
        RowSet rs;
        String sql = "Select * from " + TableList.VM_FIT_OFFICER + " where user_cd = ?";
        try {
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, userCode);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (!rs.next()) {
                sql = "INSERT INTO " + TableList.VM_FIT_OFFICER + "(\n"
                        + "            state_cd, off_cd, user_cd)\n"
                        + "    VALUES (?, ?, ?)";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getUserSeatOffCode());
                ps.setLong(3, userCode);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in insertIntoVmFitOfficer" + e.getMessage());
        }

    }

    private static void insertIntoVmEnforcementOfficer(TransactionManager tmgr, long userCode, int team_id) throws VahanException {
        PreparedStatement ps;
        RowSet rs;
        String sql = "Select * from VM_ENFORCEMENT_OFFICER where user_cd = ?";
        try {
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, userCode);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (!rs.next()) {
                sql = "INSERT INTO VM_ENFORCEMENT_OFFICER(\n"
                        + "            state_cd, off_cd, user_cd, team_id)\n"
                        + "    VALUES (?, ?, ?,?)";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getUserSeatOffCode());
                ps.setLong(3, userCode);
                ps.setInt(4, team_id);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in insertIntoVmEnforcementOfficer" + e.getMessage());
        }

    }

    private static void deleteFromVmFitOfficer(TransactionManager tmgr, long userCode) throws VahanException {
        PreparedStatement ps;
        RowSet rs;
        String sql = "Select user_cd from " + TableList.VM_FIT_OFFICER + " where user_cd = ?";
        try {
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, userCode);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                sql = "Delete from " + TableList.VM_FIT_OFFICER + " where user_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setLong(1, userCode);

                ps.executeUpdate();
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in deleteFromVmFitOfficer" + e.getMessage());
        }
    }

    private static void deleteFromVmEnforcementOfficer(TransactionManager tmgr, long userCode) throws VahanException {
        PreparedStatement ps;
        RowSet rs;
        String sql = "Select user_cd from VM_ENFORCEMENT_OFFICER where user_cd = ?";
        try {
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, userCode);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                sql = "Delete from VM_ENFORCEMENT_OFFICER where user_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setLong(1, userCode);

                ps.executeUpdate();
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in deleteFromVM_ENFORCEMENT_OFFICER" + e.getMessage());
        }
    }

    public static boolean updatePassword(UserMgmtDobj dobj) throws VahanException {
        boolean flag = false;
        PreparedStatement ps;
        TransactionManager tmgr = null;
        String sql = "";
        long userCode = 0l;
        userCode = dobj.getUser_cd();
        RowSet rs;
        try {

            tmgr = new TransactionManager("updatePassword");
            insertHistory(userCode, tmgr);
            sql = "Update " + TableList.TM_USER_INFO + " SET user_pwd = ?, op_dt = current_timestamp where user_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getUser_pwd());
            ps.setLong(2, dobj.getUser_cd());
            int count = ps.executeUpdate();
            if (count > 0) {
                flag = true;
                tmgr.commit();
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return flag;
    }

    public static List<Dealer> getDealerList(String state_cd, List offCode) throws VahanException {
        List listDealer = new ArrayList();
        String sql = "select dealer_cd, dealer_name from " + TableList.VM_DEALER_MAST + " where state_cd=? and off_cd=? order by dealer_name";
        TransactionManager tmgr = null;
        Dealer dobj = null;
        try {
            tmgr = new TransactionManager("getDealerList");
            if (!offCode.isEmpty()) {
                for (Object offCd : offCode) {
                    PreparedStatement ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setInt(2, Integer.parseInt(String.valueOf(offCd)));
                    RowSet rs = tmgr.fetchDetachedRowSet();
                    while (rs.next()) {
                        dobj = new Dealer();
                        dobj.setDealer_cd(rs.getString("dealer_cd"));
                        dobj.setDealer_name(rs.getString("dealer_name") + "(" + rs.getString("dealer_cd") + ")");
                        listDealer.add(dobj);
                    }
                }
            }
        } catch (SQLException | NumberFormatException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                    throw new VahanException(ex.getMessage());
                }
            }
        }
        return listDealer;
    }
// to fetch assigned_office list acc to user

    public static List getAssignedOfficeList(Long userCode) throws VahanException {
        PreparedStatement ps;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql;
        List assignedOffice = new ArrayList();
        try {
            tmgr = new TransactionManager("getAssignedOfficeList");
            sql = "Select b.off_cd,\n"
                    + "	b.off_name\n"
                    + "from " + TableList.TM_USER_PERMISSIONS + " a \n"
                    + "	inner join " + TableList.TM_USER_INFO + " c on a.user_cd = c.user_cd\n"
                    + "	inner join " + TableList.TM_OFFICE + " b on c.state_cd = b.state_cd and  b.off_cd  = ANY(string_to_array(a.assigned_office,',')::numeric[])\n"
                    + "where a.user_cd = ? order by b.off_cd";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, userCode);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                assignedOffice.add(new SelectItem(rs.getInt("off_cd"), rs.getString("off_name")));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException(e.getMessage());
                }
            }
        }
        return assignedOffice;
    }

    public static boolean verifyUniqueDealerAdmin(String dealerCode) throws VahanException {
        TransactionManager tmgr = null;
        boolean uniqueDealerAdmin = false;
        try {
            tmgr = new TransactionManager("verifyUniqueDealerAdmin");
            PreparedStatement ps = null;
            RowSet rs = null;
            String query = "Select * from " + TableList.TM_USER_INFO + " where user_catg  = ?"
                    + " and user_cd IN (Select user_cd from " + TableList.TM_USER_PERMISSIONS + " where dealer_cd  = ?)";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, TableConstants.USER_CATG_DEALER_ADMIN);
            ps.setString(2, dealerCode);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                uniqueDealerAdmin = true;
            }
        } catch (SQLException e) {
            throw new VahanException(e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException(e.getMessage());
                }
            }
        }
        return uniqueDealerAdmin;
    }

    private static void uploadSignature(TransactionManager tmgr, UserAuthorityDobj userDobj, long userCode) throws VahanException {
        String sql = "INSERT INTO " + TableList.TM_USER_SIGN + "(\n"
                + "  user_cd, doc_id, doc_sign)\n"
                + "  VALUES (?, ?, ?)";

        PreparedStatement ps;
        try {
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, userCode);
            ps.setInt(2, 16);
            ps.setBytes(3, userDobj.getSignatureFile());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new VahanException(e.getMessage());
        }
    }

    private static void updateUploadedSignature(TransactionManager tmgr, UserAuthorityDobj userDobj, long userCode) throws VahanException {
        PreparedStatement ps;
        RowSet rs;
        String sql = "Select * from " + TableList.TM_USER_SIGN + " Where user_cd = ?";
        try {
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, userCode);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (!rs.next()) {
                uploadSignature(tmgr, userDobj, userCode);
            } else {
                sql = "INSERT INTO " + TableList.THM_USER_SIGN + "(\n"
                        + " user_cd, doc_id, doc_sign, moved_on, moved_by)\n"
                        + "    Select user_cd, doc_id, doc_sign,current_timestamp as moved_on, ? as moved_by from " + TableList.TM_USER_SIGN + " Where user_cd = ?";


                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setLong(2, userCode);
                ps.executeUpdate();

                sql = "Update " + TableList.TM_USER_SIGN + " Set doc_sign = ? Where user_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setBytes(1, userDobj.getSignatureFile());
                ps.setLong(2, userCode);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new VahanException(e.getMessage());
        }

    }

    public boolean validateOfficeType(String state_cd, String selectedUserCatg, List assignedOfficeList) throws VahanException {
        boolean status = false;
        PreparedStatement ps;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql = "";
        int offCode = 0;
        try {
            tmgr = new TransactionManager("validateOfficeType");
            for (Object offCd : assignedOfficeList) {
                offCode = Integer.parseInt(offCd.toString());
                sql = "Select ','||user_catg_map||',' as user_catg from " + TableList.TM_OFFICE_TYPE + " where code = (SELECT off_type_cd from " + TableList.TM_OFFICE + " where off_cd = ? and state_cd = ?)";
                ps = tmgr.prepareStatement(sql);
                ps.setInt(1, offCode);
                ps.setString(2, state_cd);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    if (rs.getString("user_catg").contains("," + selectedUserCatg + ",")) {
                        status = true;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Validating Office.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Validating Office.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException("Error in Validating Office.");
            }
        }
        return status;
    }

    public boolean isSelfDealer(UserAuthorityDobj dobj, String stateCode) throws VahanException {
        boolean status = false;
        PreparedStatement ps;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("verifyDealerSelfOrNonSelf");
            sql = "SELECT regn_mark_gen_by_dealer from vm_dealer_mast where state_cd = ? and dealer_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setString(2, dobj.getDealerCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                status = rs.getBoolean("regn_mark_gen_by_dealer");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Validating Dealer.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Validating Dealer.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException("Error in Validating Dealer.");
            }
        }
        return status;
    }

    public static void insertCashierData(TransactionManager tmgr, String state_cd, int off_cd, long userCode) throws VahanException {
        PreparedStatement ps;
        RowSet rs;
        String sql = "Select user_cd from " + TableList.TM_USER_OPEN_CASH + " Where user_cd = ?";
        try {
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, userCode);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (!rs.next()) {
                sql = "INSERT INTO " + TableList.TM_USER_OPEN_CASH + "("
                        + " state_cd, off_cd, user_cd, open_cash_dt, close_cash_dt, close_cash)"
                        + " VALUES (?, ? ,?,current_timestamp,current_timestamp,false);";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, state_cd);
                ps.setInt(2, off_cd);
                ps.setLong(3, userCode);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public static String deleteFromTmUserSign(long userCode) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        RowSet rs;
        String msg = null;
        try {
            tmgr = new TransactionManager("deleteFromTmUserSign");
            String sql = "Select user_cd from " + TableList.TM_USER_SIGN + " where user_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, userCode);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                sql = " INSERT INTO " + TableList.THM_USER_SIGN + "  \n"
                        + " SELECT user_cd, doc_id, doc_sign,current_timestamp as moved_on,? as moved_by \n"
                        + "  FROM " + TableList.TM_USER_SIGN + " where user_cd=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setLong(2, userCode);
                ps.executeUpdate();

                sql = "Delete from " + TableList.TM_USER_SIGN + " where user_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setLong(1, userCode);
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                msg = "User Signature Remove Sucessfully";
            } else {
                msg = "User Signature Not Found";
            }
            tmgr.commit();
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return msg;
    }
}
