/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.db.user_mgmt.dobj.FitnessOfficerMobileDtlsDobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author DELL
 */
public class FitnessOfficerMobileDtlsImpl {

    private static final Logger LOGGER = Logger.getLogger(FitnessOfficerMobileDtlsImpl.class);

    public List getFitnessOfficerDetails(String state_cd, Integer off_cd) throws VahanException {
        String sql;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        List fitoffdtls = new ArrayList();
        try {
            tmgr = new TransactionManagerReadOnly("getFitnessOfficerDetails");
            sql = " select user_id from " + TableList.VA_REGISTRATION_REQUEST + " where user_id in(select user_id from " + TableList.TM_USER_INFO + " where state_cd=? and off_cd=?) and status = 'requested' ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                fitoffdtls.addAll(Arrays.asList(rs.getString("user_id")));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in getting User Details");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return fitoffdtls;
    }

    public static FitnessOfficerMobileDtlsDobj getfitoffData(String user_id, String state_cd, Integer off_cd) throws VahanException {

        PreparedStatement ps = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String sql;
        FitnessOfficerMobileDtlsDobj dobj = null;
        try {
            tmgr = new TransactionManager("getfitoffData");
            sql = "select user_cd from " + TableList.VM_FIT_OFFICER + " where user_cd in (select user_cd from " + TableList.TM_USER_INFO + " where user_id=? and state_cd=? and off_cd=?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, user_id);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                sql = "select a.user_id, a.name, a.device_id, b.mobile_no from mvahan4.va_registration_request a\n"
                        + "inner join  tm_user_info b on a.user_id=b.user_id where a.user_id=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, user_id);
                rs = tmgr.fetchDetachedRowSet();
                while (rs.next()) {
                    dobj = new FitnessOfficerMobileDtlsDobj();
                    dobj.setUser_id(rs.getString("user_id"));
                    dobj.setUserName(rs.getString("name"));
                    dobj.setDevice_id(rs.getString("device_id"));
                    dobj.setMobileNo(rs.getString("mobile_no"));
                }
            } else {
                JSFUtils.showMessagesInDialog("Alert", "This User ID is not a Fitness Officer. First Make This Fitness Officer then Try Again!!", FacesMessage.SEVERITY_INFO);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj;
    }

    public boolean updateFitoffDtls(FitnessOfficerMobileDtlsDobj fitoffDobj, String user_id, String state_cd, Integer off_cd) throws VahanException {
        boolean flag = false;
        PreparedStatement ps;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql;
        try {
            tmgr = new TransactionManager("updateFitoffDtls");
            sql = "select * from " + TableList.TM_DEVICE_ID + " where device_id=? and user_id=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, fitoffDobj.getDevice_id());
            ps.setString(2, fitoffDobj.getUser_id());
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                throw new VahanException("This Device ID is Already Verified with Selected UserID. Please Use Different Device-ID and Try Again!!");
            } else {
                sql = "INSERT INTO " + TableList.TM_DEVICE_ID + "(user_id, mobile_num, device_id, created_by_user_id, updated_by_user_id, created_on) VALUES (?, ?, ?, ?, ?, current_timestamp)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, fitoffDobj.getUser_id());
                ps.setString(2, fitoffDobj.getMobileNo());
                ps.setString(3, fitoffDobj.getDevice_id());
                ps.setString(4, user_id);
                ps.setString(5, "");
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                sql = "UPDATE " + TableList.VA_REGISTRATION_REQUEST + " set status=?, completed_on = current_timestamp where user_id=? and device_id=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, "handled");
                ps.setString(2, fitoffDobj.getUser_id());
                ps.setString(3, fitoffDobj.getDevice_id());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                flag = true;
                tmgr.commit();
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException(TableConstants.SomthingWentWrong);
                }
            }
        }
        return flag;
    }

    public String checkRecordStatus(FitnessOfficerMobileDtlsDobj fitoffDobj) throws VahanException {
        PreparedStatement ps;
        RowSet rs;
        TransactionManager tmgr = null;
        String status = null;
        String sql;
        try {
            tmgr = new TransactionManager("checkRecordStatus");
            sql = "select status from " + TableList.VA_REGISTRATION_REQUEST + " where user_id=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, fitoffDobj.getUser_id());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                status = rs.getString("status");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException(TableConstants.SomthingWentWrong);
                }
            }
        }
        return status;
    }

    public static String deleteHandleRecord(FitnessOfficerMobileDtlsDobj dobj, String state_cd, Integer off_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String msg = null;
        try {
            tmgr = new TransactionManager("deleteHandleRecord");
            String sql = " Delete from " + TableList.TM_DEVICE_ID + " where user_id=? and device_id=? and mobile_num=?";
            int j = 1;
            ps = tmgr.prepareStatement(sql);
            ps.setString(j++, dobj.getUser_id());
            ps.setString(j++, dobj.getDevice_id());
            ps.setString(j++, dobj.getMobileNo());
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            sql = "UPDATE " + TableList.VA_REGISTRATION_REQUEST + " set status=?, completed_on = current_timestamp where user_id=? and device_id=? ";
            int k = 1;
            ps = tmgr.prepareStatement(sql);
            ps.setString(k++, "deleted");
            ps.setString(k++, dobj.getUser_id());
            ps.setString(k++, dobj.getDevice_id());
            int i = ps.executeUpdate();
            if (i > 0) {
                msg = "Device Record Deleted Successfully";
                tmgr.commit();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Data Has not been Deleted");
        } finally {
            try {
                ps.close();
                tmgr.release();
            } catch (Exception e) {
                msg = "Device Record Not Deleted";
            }
        }
        return msg;
    }

    public boolean rejectFitoffDtls(FitnessOfficerMobileDtlsDobj fitoffDobj, String user_id, String state_cd, Integer off_cd) throws VahanException {
        boolean flag = false;
        PreparedStatement ps;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql;
        try {
            tmgr = new TransactionManager("updateFitoffDtls");
            sql = "select * from " + TableList.TM_DEVICE_ID + " where device_id=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, fitoffDobj.getDevice_id());
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                throw new VahanException("This Device ID is Already Verified. Please Use Different Device-ID and Try Again!!");
            } else {
                sql = "UPDATE " + TableList.VA_REGISTRATION_REQUEST + " set status=?, completed_on = current_timestamp where user_id=? and device_id=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, "rejected");
                ps.setString(2, fitoffDobj.getUser_id());
                ps.setString(3, fitoffDobj.getDevice_id());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                flag = true;
                tmgr.commit();
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException(TableConstants.SomthingWentWrong);
                }
            }
        }
        return flag;
    }

    //  For Fitness Office Location
    public boolean saveFitnessOfficeDtls(FitnessOfficerMobileDtlsDobj fitoffDobj, String state_cd, Integer off_cd) throws VahanException {
        boolean flag = false;
        PreparedStatement ps;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql;
        try {
            tmgr = new TransactionManager("updateFitoffDtls");
            sql = "select * from " + TableList.TM_OFFICE_DETAILS + " where longitude=? and latitude=? and off_cd=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, fitoffDobj.getLongitude());
            ps.setString(2, fitoffDobj.getLatitude());
            ps.setInt(3, off_cd);
            ps.setString(4, state_cd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                throw new VahanException("This Office Location Details are Already Entered. Please Use Different Office Location Details and Try Again!!");
            } else {
                sql = "INSERT INTO " + TableList.TM_OFFICE_DETAILS + "(state_cd, off_cd, longitude, latitude, location_name) VALUES (?, ?, ?, ?, ?)";
                ps = tmgr.prepareStatement(sql);

                ps.setString(1, state_cd);
                ps.setInt(2, off_cd);
                ps.setString(3, fitoffDobj.getLongitude());
                ps.setString(4, fitoffDobj.getLatitude());
                ps.setString(5, fitoffDobj.getLocationName());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                flag = true;
                tmgr.commit();
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException(TableConstants.SomthingWentWrong);
                }
            }
        }
        return flag;
    }

    public static List fillOfficeLocationDataTable(String state_cd, Integer off_cd) throws VahanException {
        List<FitnessOfficerMobileDtlsDobj> officeLocationList = new ArrayList<>();
        TransactionManager tmgr = null;
        FitnessOfficerMobileDtlsDobj dobj = null;
        try {
            tmgr = new TransactionManager("fillOfficeLocationDataTable");
            PreparedStatement ps;
            RowSet rs;
            String sql = "select longitude,latitude,location_name from " + TableList.TM_OFFICE_DETAILS + " where  state_cd=? and off_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new FitnessOfficerMobileDtlsDobj();
                dobj.setLongitude(rs.getString("longitude"));
                dobj.setLatitude(rs.getString("latitude"));
                dobj.setLocationName(rs.getString("location_name"));
                officeLocationList.add(dobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException(e.getMessage());
                }
            }
        }
        return officeLocationList;
    }

    public static List fillHandledDeviceDataTable(String state_cd, Integer off_cd) throws VahanException {
        List<FitnessOfficerMobileDtlsDobj> handleDeviceList = new ArrayList<>();
        TransactionManager tmgr = null;
        FitnessOfficerMobileDtlsDobj dobj = null;
        try {
            tmgr = new TransactionManager("fillhandleDeviceDataTable");
            PreparedStatement ps;
            RowSet rs;
            String sql = " select a.user_id, b.user_name, a.device_id, b.mobile_no"
                    + "  from "
                    + TableList.TM_DEVICE_ID + " a "
                    + " inner join "
                    + TableList.TM_USER_INFO + "  b on a.user_id=b.user_id "
                    + " where  b.state_cd=? and b.off_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new FitnessOfficerMobileDtlsDobj();
                dobj.setUser_id(rs.getString("user_id"));
                dobj.setUserName(rs.getString("user_name"));
                dobj.setDevice_id(rs.getString("device_id"));
                dobj.setMobileNo(rs.getString("mobile_no"));
                handleDeviceList.add(dobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException(e.getMessage());
                }
            }
        }
        return handleDeviceList;
    }

    public static String deleteLocationEntry(FitnessOfficerMobileDtlsDobj dobj, String state_cd, Integer off_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String msg = null;
        try {
            tmgr = new TransactionManager("deleteLocationEntry");
            String sql = "DELETE FROM " + TableList.TM_OFFICE_DETAILS + " WHERE off_cd=? and state_cd=? and longitude=? and latitude=? and location_name=? ";
            int k = 1;
            ps = tmgr.prepareStatement(sql);
            ps.setInt(k++, off_cd);
            ps.setString(k++, state_cd);
            ps.setString(k++, dobj.getLongitude());
            ps.setString(k++, dobj.getLatitude());
            ps.setString(k++, dobj.getLocationName());
            int i = ps.executeUpdate();
            if (i > 0) {
                msg = "Office Location Entry Successfully Deleted";
                tmgr.commit();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Data Has not been Deleted");
        } finally {
            try {
                ps.close();
                tmgr.release();
            } catch (Exception e) {
                msg = "Office Location Entry Not Deleted";
            }
        }
        return msg;
    }
}
