/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.dobj.ComparisonDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import nic.vahan5.reg.server.ServerUtility;

/**
 *
 * @author Kartikey Singh
 */
public class ComparisonBeanImplementation implements Serializable {

    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ComparisonBeanImplementation.class);

    public static ArrayList<ComparisonBean> dataChangedByPreviousUser(String applNo) {

        ArrayList<ComparisonBean> prevChangedDataList = new ArrayList<>();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String query = "";
        query = " select d.emp_cd,ui.user_name,d.changed_data,to_char(d.op_dt,'DD-MON-YYYY HH24:MI:SS') as op_dt\n"
                + "  from vha_changed_data d left outer join tm_user_info ui\n"
                + "  on d.emp_cd = ui.user_cd\n"
                + "  where d.appl_no = ? \n"
                + "  order by d.op_dt::timestamp  desc";
        try {
            tmgr = new TransactionManager("dataChangedByPreviousUser");
            ps = tmgr.prepareStatement(query);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                ComparisonBean bean = new ComparisonBean();
                bean.setUser(rs.getInt("emp_cd"));
                bean.setUserName(rs.getString("user_name"));
                bean.setChanged_data(rs.getString("changed_data").replace("|", "&nbsp; <font color=\"red\">|</font> &nbsp;"));
                bean.setOp_dt(rs.getString("op_dt"));
                prevChangedDataList.add(bean);
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return prevChangedDataList;
    }

    public static ArrayList<ComparisonBean> dataChangedByPreviousUsers(String appl_no, int pur_cd) throws VahanException {

        ArrayList<ComparisonBean> prevChangedDataList = new ArrayList<>();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String query = "";
        query = "select d.emp_cd,ui.user_name,d.changed_data,to_char(d.op_dt,'DD-MON-YYYY HH24:MI:SS') as op_dt "
                + " FROM " + TableList.VHA_CHANGED_DATA + " d left outer join " + TableList.TM_USER_INFO + " ui "
                + " ON d.emp_cd=ui.user_cd "
                + " WHERE d.appl_no= ? order by d.op_dt::timestamp desc";
        try {
            tmgr = new TransactionManager("dataChangedByPreviousUser");
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                ComparisonBean bean = new ComparisonBean();
                bean.setUser(rs.getInt("emp_cd"));
                bean.setUserName(rs.getString("user_name"));
                bean.setChanged_data(rs.getString("changed_data").replace("|", "&nbsp; <font color=\"red\">|</font> &nbsp;"));
                bean.setOp_dt(rs.getString("op_dt"));
                prevChangedDataList.add(bean);
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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
        return prevChangedDataList;
    }

    public static void updateChangedData(String appl_no, String changedData, TransactionManager tmgr) throws VahanException {

        String sql = null;
        PreparedStatement ps = null;
        try {
            //for saving the data into table those are changed by the user
            if (!changedData.equals("")) {

                sql = "INSERT INTO VHA_CHANGED_DATA (APPL_NO,EMP_CD,CHANGED_DATA,OP_DT,STATE_CD,OFF_CD) "
                        + " VALUES(?,?,?,CURRENT_TIMESTAMP,?,?)";
                ps = tmgr.prepareStatement(sql);

                ps.setString(1, appl_no);
                ps.setLong(2, Long.parseLong(Util.getEmpCode()));
                ps.setString(3, changedData);
                ps.setString(4, Util.getUserStateCode());
                ps.setInt(5, Util.getUserSeatOffCode());
                ps.executeUpdate();
                if (Util.getTmConfiguration() != null && Util.getTmConfiguration().isAllowFacelessService()) {
                    if (Util.getUserCategory() != null && !Util.getUserCategory().equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)) {
                        if (appl_no != null && Util.getSelectedSeat() != null && (Util.getSelectedSeat().getOff_cd() != 0)) {
                            ServerUtil.updateRtoOpenDateInVtFaceLessService(tmgr, appl_no, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                        } else {
                            throw new Exception("Problem in getting office code/applNo");
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("saveChangedData : Error in Database Update");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Update Error in saveChangedData");
        }
    }

    /*
     * @author Kartikey Singh
     */
    public static void updateChangedData(String appl_no, String changedData, TransactionManager tmgr, String empCode,
            String stateCode ,int selectedOffCode, String userCategory, boolean isAllowFacelessService) throws VahanException {

        String sql = null;
        PreparedStatement ps = null;
        try {
            //for saving the data into table those are changed by the user
            if (!changedData.equals("")) {

                sql = "INSERT INTO VHA_CHANGED_DATA (APPL_NO,EMP_CD,CHANGED_DATA,OP_DT,STATE_CD,OFF_CD) "
                        + " VALUES(?,?,?,CURRENT_TIMESTAMP,?,?)";
                ps = tmgr.prepareStatement(sql);

                ps.setString(1, appl_no);
                ps.setLong(2, Long.parseLong(empCode));
                ps.setString(3, changedData);
                ps.setString(4, stateCode);
                ps.setInt(5, selectedOffCode);
                ps.executeUpdate();
                if (isAllowFacelessService) {
                    if (userCategory != null && !userCategory.equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)) {
                        if (appl_no != null && selectedOffCode != 0) {
                            ServerUtility.updateRtoOpenDateInVtFaceLessService(tmgr, appl_no, stateCode, selectedOffCode);
                        } else {
                            throw new Exception("Problem in getting office code/applNo");
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("saveChangedData : Error in Database Update");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Update Error in saveChangedData");
        }
    }

    public static String changedDataContents(List<ComparisonBean> compBeanList) throws Exception {
        String changedData = "";
        for (int i = 0; i < compBeanList.size(); i++) {
            changedData += "[" + compBeanList.get(i).getFields() + "  " + compBeanList.get(i).getOld_value() + " to " + compBeanList.get(i).getNew_value() + "]";
            changedData += "|";
        }

        return changedData;
    }

    public static String changedDataContentsByUser(List<ComparisonDobj> comparisonList) throws Exception {
        String changedData = "";
        for (int i = 0; i < comparisonList.size(); i++) {
            changedData += "[" + comparisonList.get(i).getFields() + "  " + comparisonList.get(i).getOld_value() + " to " + comparisonList.get(i).getNew_value() + "]";
            changedData += "|";
        }
        return changedData;
    }
    
}
